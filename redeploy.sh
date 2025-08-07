#!/bin/bash

set -e  # Остановить при ошибке

SERVICES=(
  account-service
  audit-service
  cash-service
  dictionaries-service
  exchange-service
  front-ui
  notification-service
  transfer-service
)

OLD_TAG=0.1.0
TAG=0.1.0
HELM_RELEASE=financeapp
NAME_SPACE=test
HELM_CHART_PATH=./financeapp  # путь к чарту

echo "Uninstalling Helm release $HELM_RELEASE..."
helm uninstall "$HELM_RELEASE" --namespace "$NAME_SPACE" || true

for SERVICE in "${SERVICES[@]}"; do
  IMAGE="${SERVICE}:${OLD_TAG}"
  echo "Removing image $IMAGE..."
  minikube ssh docker image rm "$IMAGE" || true
done

for SERVICE in "${SERVICES[@]}"; do
  IMAGE="${SERVICE}:${TAG}"
  echo "Building $IMAGE..."
  docker build -t "$IMAGE" "./$SERVICE"
done

for SERVICE in "${SERVICES[@]}"; do
  IMAGE="${SERVICE}:${TAG}"
  echo "Loading $IMAGE into Minikube..."
  minikube image load "$IMAGE"
done

echo "Reinstalling Helm release $HELM_RELEASE..."
helm upgrade --install "$HELM_RELEASE" "$HELM_CHART_PATH" -f ./financeapp/values.yaml --namespace "$NAME_SPACE"
