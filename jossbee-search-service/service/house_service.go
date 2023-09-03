// service/house_service.go
package service

import (
	"context"
	"encoding/json"
	"fmt"
	"jossbee-search-service/elasticsearch"
	"jossbee-search-service/model"
	"strings"

	"github.com/elastic/go-elasticsearch/v8/esapi"
)

type HouseService struct {
	esClient *elasticsearch.Client
}

func NewHouseService(esClient *elasticsearch.Client) *HouseService {
	return &HouseService{
		esClient: esClient,
	}
}

func (s *HouseService) GetHouses(ctx context.Context, numberOfBedRooms, city, priceFrom, priceTo string) ([]model.House, error) {
	query := map[string]interface{}{
		"query": map[string]interface{}{
			"bool": map[string]interface{}{
				"must": []map[string]interface{}{},
			},
		},
	}

	if numberOfBedRooms != "" {
		query["query"].(map[string]interface{})["bool"].(map[string]interface{})["must"] = append(query["query"].(map[string]interface{})["bool"].(map[string]interface{})["must"].([]map[string]interface{}),
			map[string]interface{}{
				"term": map[string]interface{}{
					"numberOfBedRooms": numberOfBedRooms,
				},
			})
	}

	if city != "" {
		query["query"].(map[string]interface{})["bool"].(map[string]interface{})["must"] = append(query["query"].(map[string]interface{})["bool"].(map[string]interface{})["must"].([]map[string]interface{}),
			map[string]interface{}{
				"term": map[string]interface{}{
					"address.city": city,
				},
			})
	}

	if priceFrom != "" || priceTo != "" {
		rangeQuery := map[string]interface{}{}
		if priceFrom != "" {
			rangeQuery["gte"] = priceFrom
		}
		if priceTo != "" {
			rangeQuery["lte"] = priceTo
		}
		query["query"].(map[string]interface{})["bool"].(map[string]interface{})["must"] = append(query["query"].(map[string]interface{})["bool"].(map[string]interface{})["must"].([]map[string]interface{}),
			map[string]interface{}{
				"range": map[string]interface{}{
					"pricePerNight": rangeQuery,
				},
			})
	}

	var buf strings.Builder
	if err := json.NewEncoder(&buf).Encode(query); err != nil {
		return nil, err
	}

	req := esapi.SearchRequest{
		Index: []string{"houses"},
		Body:  strings.NewReader(buf.String()),
	}
	res, err := req.Do(ctx, s.esClient)
	if err != nil {
		return nil, err
	}
	defer res.Body.Close()

	if res.IsError() {
		return nil, fmt.Errorf("elasticsearch error: %s", res.Status())
	}

	var response map[string]interface{}
	if err := json.NewDecoder(res.Body).Decode(&response); err != nil {
		return nil, err
	}

	// Parse the response to extract and return houses
	var houses []model.House
	hits, found := response["hits"].(map[string]interface{})["hits"].([]interface{})
	if !found {
		return houses, nil
	}

	for _, hit := range hits {
		source, found := hit.(map[string]interface{})["_source"].(map[string]interface{})
		if !found {
			continue
		}

		var house model.House
		if err := mapToStruct(source, &house); err != nil {
			return nil, err
		}
		houses = append(houses, house)
	}

	return houses, nil
}

func mapToStruct(source map[string]interface{}, dest interface{}) error {
	data, err := json.Marshal(source)
	if err != nil {
		return err
	}
	if err := json.Unmarshal(data, &dest); err != nil {
		return err
	}
	return nil
}
