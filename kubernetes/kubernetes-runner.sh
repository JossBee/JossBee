#!/bin/bash

kubectl apply -f statefulsets/authdb-statefulset.yaml
kubectl apply -f deployments/auth-server-deployment.yaml
kubectl apply -f services/authdb-nodeport-service.yaml
kubectl apply -f services/auth-server-nodeport-service.yaml

echo "Started ..........................................."
