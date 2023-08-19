FROM golang:1.19 AS builder

WORKDIR /app

COPY . .

RUN go mod download

RUN CGO_ENABLED=0 GOOS=linux go build -o myapp .

# Use a minimal base image to run the application
FROM alpine:latest

WORKDIR /app

COPY --from=builder /app/myapp .

CMD ["./myapp"]
