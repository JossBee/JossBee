FROM golang:1.16

WORKDIR /app

COPY *.go .
COPY go.mod .
COPY go.sum .

RUN go get github.com/confluentinc/confluent-kafka-go/v2/kafka
RUN go build -o jossbee-consumer

CMD ["./jossbee-consumer"]