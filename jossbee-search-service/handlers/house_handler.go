// handlers/house_handler.go
package handlers

import (
	"net/http"

	"github.com/labstack/echo/v4"
	"github.com/labstack/gommon/log"
	"jossbee-search-service/elasticsearch"
	"jossbee-search-service/service"
)

type HouseHandler struct {
	houseService *service.HouseService
}

func NewHouseHandler(esClient *elasticsearch.Client) *HouseHandler {
	houseService := service.NewHouseService(esClient)
	return &HouseHandler{
		houseService: houseService,
	}
}

func (h *HouseHandler) GetHouses(c echo.Context) error {
	numberOfBedRooms := c.QueryParam("numberOfBedRooms")
	city := c.QueryParam("city")
	priceFrom := c.QueryParam("priceFrom")
	priceTo := c.QueryParam("priceTo")

	houses, err := h.houseService.GetHouses(c.Request().Context(), numberOfBedRooms, city, priceFrom, priceTo)
	if err != nil {
		log.Errorf("Failed to retrieve houses, error is: %s", err.Error())
		return c.String(http.StatusInternalServerError, "Failed to retrieve houses")
	}

	return c.JSON(http.StatusOK, houses)
}
