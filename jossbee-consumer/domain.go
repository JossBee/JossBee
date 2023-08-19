package main

type House struct {
	ID                string   `json:"id"`
	IsActive          bool     `json:"isActive"`
	CreatedAt         string   `json:"createdAt"`
	CreatedBy         string   `json:"createdBy"`
	UpdatedAt         string   `json:"updatedAt"`
	UpdatedBy         string   `json:"updatedBy"`
	Title             string   `json:"title"`
	Description       string   `json:"description"`
	NumberOfBedRooms  int      `json:"numberOfBedRooms"`
	NumberOfBathrooms int      `json:"numberOfBathrooms"`
	GuestsCapacity    int      `json:"guestsCapacity"`
	PricePerNight     float64  `json:"pricePerNight"`
	Discount          float64  `json:"discount"`
	Host              Host     `json:"host"`
	Address           Address  `json:"address"`
	Category          Category `json:"category"`
	Amenities         []string `json:"amenities"`
	Photos            []string `json:"photos"`
}

type Host struct {
	HostUuid              string `json:"hostUuid"`
	Name                  string `json:"name"`
	HostProfilePictureUrl string `json:"hostProfilePictureUrl"`
}

type Address struct {
	Street    string  `json:"street"`
	Latitude  float64 `json:"latitude"`
	Longitude float64 `json:"longitude"`
	City      string  `json:"city"`
	State     string  `json:"state"`
	Country   string  `json:"country"`
	ZipCode   string  `json:"zipCode"`
}

type Category struct {
	Id       string `json:"id"`
	ImageUrl string `json:"imageUrl"`
	Name     string `json:"name"`
}
