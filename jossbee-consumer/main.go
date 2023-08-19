package main

import (
	"context"
	"fmt"
	"log"
	"os"
	"os/signal"
	"syscall"
	"time"

	"github.com/confluentinc/confluent-kafka-go/v2/kafka"
	"github.com/olivere/elastic/v7"
)

func main() {

	kafkaBroker := "pkc-41p56.asia-south1.gcp.confluent.cloud:9092"
	consumerGroup := "house_topic_consumer_group"
	topic := "house_topic"

	saslUsername := "EYJ4BVM3A6YUTRS6"
	saslPassword := "plEHr04UtmxdTD1ZgcwOr2SAr52cZAGzaeqUX/dPaWN+Qz/V9bP5DsGl4Y9IvzI4"

	elasticURL := "https://zK2WsfJEu4:PLSutiAbRWd2vmnGEzhr5yfV@jossbee-58293848.us-east-1.bonsaisearch.net"
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

	elasticClient, err := elastic.NewClient(
		elastic.SetURL(elasticURL),
		elastic.SetSniff(false), // Disable sniffing in a single-node setup
	)
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
		elasticClient.Stop()
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

			indexDoc := map[string]interface{}{
				"message": string(e.Value),
			}
			_, err := elasticClient.Index().
				Index(indexName).
				BodyJson(indexDoc).
				Do(context.Background())

			if err != nil {
				fmt.Printf("Failed to index document: %v\n", err)
			}

		case kafka.Error:
			fmt.Printf("Kafka error: %v\n", e)
		}
		time.Sleep(100 * time.Millisecond) // Add a small delay between polls
	}
}
