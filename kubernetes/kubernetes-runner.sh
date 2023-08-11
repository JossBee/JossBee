#!/bin/bash

# Apply the Namespace
kubectl apply -f namespace/jossbee-localstack-namespace.yaml

# Apply the PersistentVolumeClaim
kubectl apply -f persistentVolume/jossbee-localstack-persistant-volume.yaml

#Start stateful set
kubectl apply -f statefulsets/authdb-statefulset.yaml
kubectl apply -f statefulsets/jossbee-localstack-statefulset.yaml

#Start ConfigMaps
kubectl apply -f configMaps/auth-server-env-configmap.yaml

#Start deployment
kubectl apply -f deployments/auth-server-deployment.yaml

#Start the services
kubectl apply -f services/authdb-nodeport-service.yaml
kubectl apply -f services/auth-server-nodeport-service.yaml
kubectl apply -f services/jossbee-localstack-service.yaml

echo "Started ..........................................."
