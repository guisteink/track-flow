#!/bin/bash

# Parar e remover contêineres, redes, imagens e volumes
echo "Executando docker-compose down..."
docker-compose down

# Construir ou reconstruir imagens
echo "Executando docker-compose build..."
docker-compose build

# Subir os contêineres
echo "Executando docker-compose up..."
docker-compose up -d

echo "Concluído!"