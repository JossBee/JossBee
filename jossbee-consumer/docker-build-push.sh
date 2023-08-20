#!/bin/bash

DOCKERHUB_USERNAME=124225

IMAGE_NAME=jossbee-consumer
IMAGE_TAG=latest

# Build the Docker image
docker build -t $IMAGE_NAME:$IMAGE_TAG .

# Authenticate with Docker Hub
docker login -u $DOCKERHUB_USERNAME

# Tag the image for Docker Hub
docker tag $IMAGE_NAME:$IMAGE_TAG $DOCKERHUB_USERNAME/$IMAGE_NAME:$IMAGE_TAG

# Push the image to Docker Hub
docker push $DOCKERHUB_USERNAME/$IMAGE_NAME:$IMAGE_TAG

docker logout

docker compose -f docker-compose.yaml up -d