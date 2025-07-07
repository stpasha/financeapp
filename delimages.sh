#!/bin/bash

# Список image IDs, которые нужно оставить
keep_ids=(
  d04f23a5cd17
  b290b128c11f
  3f94abec2675
  bf626c26908a
  1062f1f567e6
)

# Список всех image IDs из вывода
all_ids=(
  d04f23a5cd17
  b290b128c11f
  3f94abec2675
  bf626c26908a
  1062f1f567e6
  8e5a44b58328
  4e38101991da
  14b08e7ad0d1
  45ea3385622f
  b15314ecc9fb
  0bd25b822b78
  13679ff1ce90
  021387785afd
  8e308ccebf7b
  92c53f14e6b7
  9c4bf0a7d9c6
  43ef491be8fc
  df4ab4d0976f
  bea557c2d278
  eb0f7875a8e2
  05178bdc8ef9
)

# Удаляем всё, что не в списке keep_ids
for id in "${all_ids[@]}"; do
  if ! [[ " ${keep_ids[*]} " =~ " ${id} " ]]; then
    echo "Удаляю $id"
    docker rmi -f "$id"
  fi
done