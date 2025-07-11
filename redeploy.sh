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

OLD_TAG=0.2.7.4
TAG=0.2.7.4
HELM_RELEASE=fin
HELM_CHART_PATH=./financeapp  # путь к чарту

echo "Uninstalling Helm release $HELM_RELEASE..."
helm uninstall "$HELM_RELEASE" || true
#
#for SERVICE in "${SERVICES[@]}"; do
#  IMAGE="${SERVICE}:${OLD_TAG}"
#  echo "Removing image $IMAGE..."
#  minikube ssh docker image rm "$IMAGE" || true
#done

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

#echo "Reinstalling Helm release $HELM_RELEASE..."
#helm install "$HELM_RELEASE" "$HELM_CHART_PATH"
