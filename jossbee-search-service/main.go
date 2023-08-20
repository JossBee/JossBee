// main.go
package main

import (
	"github.com/labstack/echo/v4"
	"github.com/labstack/gommon/log"
	"jossbee-search-service/elasticsearch"
	"jossbee-search-service/handlers"
)

func main() {
	e := echo.New()

	esClient, err := elasticsearch.NewClient()
	if err != nil {
		log.Errorf("Failed to initialize the elastic-search client. Error is : %s", err.Error())
		panic("Failed to initialize elastic search")
	}

	houseHandler := handlers.NewHouseHandler(esClient)

	e.GET("/houses", houseHandler.GetHouses)

	e.Logger.Fatal(e.Start(":8080"))
}
