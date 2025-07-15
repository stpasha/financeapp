#!/bin/bash

# Какие теги оставить
KEEP_TAGS=("0.2.7.7" "0.2.8.2.3")

# Получаем список всех образов с именем и тегом
docker images --format '{{.Repository}}:{{.Tag}} {{.ID}}' | while read line; do
    IMAGE_TAG=$(echo "$line" | awk '{print $1}')
    IMAGE_ID=$(echo "$line" | awk '{print $2}')
    REPO=$(echo "$IMAGE_TAG" | cut -d: -f1)
    TAG=$(echo "$IMAGE_TAG" | cut -d: -f2)

    # Пропускаем те образы, которые должны остаться
    if [[ " ${KEEP_TAGS[*]} " =~ " ${TAG} " ]]; then
        continue
    fi

    # Удаляем остальные версии с тегами
    if [[ "$TAG" != "<none>" ]]; then
        echo "Удаляю образ: $REPO:$TAG ($IMAGE_ID)"
        docker rmi "$REPO:$TAG"
    fi
done

# Удаляем dangling-образы (<none>:<none>)
echo "Удаляю dangling-образы..."
docker images --filter "dangling=true" -q | xargs -r docker rmi