#!/bin/bash

set -e  # Остановить при ошибке

SERVICES=(
  audit-service
  cash-service
  dictionaries-service
  exchange-service
  front-ui
  notification-service
  transfer-service
)

OLD_TAG=0.2.6.6
TAG=0.2.6.2
HELM_RELEASE=myapp
HELM_CHART_PATH=./financeapp  # путь к чарту

echo "Uninstalling Helm release $HELM_RELEASE..."
helm uninstall "$HELM_RELEASE" || true

for SERVICE in "${SERVICES[@]}"; do
  IMAGE="${SERVICE}:${OLD_TAG}"
  echo "Removing image $IMAGE..."
  minikube ssh docker image rm "$IMAGE" || true
done

for SERVICE in "${SERVICES[@]}"; do
  IMAGE="${SERVICE}:${OLD_TAG}"
  echo "Removing image $IMAGE..."
  minikube docker image rm "$IMAGE" || true
done

