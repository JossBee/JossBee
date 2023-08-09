#!/bin/bash

#Start stateful set
kubectl apply -f statefulsets/authdb-statefulset.yaml

#Start ConfigMaps
kubect apply -f configMaps/auth-server-env-configmap.yaml

#Start deployment
kubectl apply -f deployments/auth-server-deployment.yaml

#Start the services
kubectl apply -f services/authdb-nodeport-service.yaml
kubectl apply -f services/auth-server-nodeport-service.yaml

echo "Started ..........................................."
