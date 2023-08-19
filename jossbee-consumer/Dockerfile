FROM golang:1.19 AS build-stage

WORKDIR /app

COPY . .
RUN go mod vendor
RUN go build -o main .

# Deploy the application binary into a lean image
FROM alpine AS build-release-stage

RUN apk update && \
        apk add --no-cache tzdata
RUN apk --no-cache add ca-certificates

WORKDIR /app

COPY --from=build-stage . .
CMD ["/app/main"]