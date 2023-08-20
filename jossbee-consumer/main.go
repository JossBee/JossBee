package main

import (
	"context"
	"encoding/json"
	"fmt"
	"log"
	"os"
	"os/signal"
	"strings"
	"syscall"
	"time"

	"github.com/confluentinc/confluent-kafka-go/v2/kafka"
	elasticsearch "github.com/elastic/go-elasticsearch/v8"
	"github.com/elastic/go-elasticsearch/v8/esapi"
)

func main() {
	kafkaBroker := "pkc-41p56.asia-south1.gcp.confluent.cloud:9092"
	consumerGroup := "house_topic_consumer_group"
	topic := "house_topic"

	saslUsername := "EYJ4BVM3A6YUTRS6"
	saslPassword := "plEHr04UtmxdTD1ZgcwOr2SAr52cZAGzaeqUX/dPaWN+Qz/V9bP5DsGl4Y9IvzI4"

	elasticURL := "10.18.116.174:9200"
	indexName := "jossbee"

	consumer, err := kafka.NewConsumer(&kafka.ConfigMap{
		"bootstrap.servers": kafkaBroker,
		"group.id":          consumerGroup,
		"auto.offset.reset": "earliest",
		"sasl.mechanisms":   "PLAIN",
		"sasl.username":     saslUsername,
		"sasl.password":     saslPassword,
		"security.protocol": "SASL_SSL",
	})
	if err != nil {
		log.Fatalf("Failed to create Kafka consumer: %v", err)
	}
	defer consumer.Close()

	log.Println("Kafka consumer connection successful")

	consumer.SubscribeTopics([]string{topic}, nil)

	cfg := elasticsearch.Config{
		Addresses: []string{elasticURL},
	}
	elasticClient, err := elasticsearch.NewClient(cfg)
	if err != nil {
		log.Fatalf("Failed to create Elasticsearch client: %v", err)
	}

	// Handle graceful shutdown
	sigChan := make(chan os.Signal, 1)
	signal.Notify(sigChan, syscall.SIGINT, syscall.SIGTERM)
	go func() {
		sig := <-sigChan
		fmt.Printf("Received signal %v, shutting down...\n", sig)
		consumer.Close()
		os.Exit(0)
	}()

	for {
		ev := consumer.Poll(100)
		if ev == nil {
			continue
		}

		switch e := ev.(type) {
		case *kafka.Message:
			fmt.Printf("Received message: %s\n", string(e.Value))

			var house House
			if err := json.Unmarshal(e.Value, &house); err != nil {
				fmt.Printf("Failed to unmarshal message: %v\n", err)
				continue
			}

			// Indexing the house into Elasticsearch
			esReq := esapi.IndexRequest{
				Index:   indexName,
				Body:    strings.NewReader(fmt.Sprintf(`{"doc":%s}`, string(e.Value))),
				Refresh: "true",
			}
			_, err := esReq.Do(context.Background(), elasticClient)
			if err != nil {
				fmt.Printf("Failed to index document: %v\n", err)
			}

		case kafka.Error:
			fmt.Printf("Kafka error: %v\n", e)
		}
		time.Sleep(100 * time.Millisecond)
	}
}
