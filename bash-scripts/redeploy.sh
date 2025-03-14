#!/bin/bash
echo "Parando e removendo contêineres, redes, imagens e volumes..."
docker-compose down

echo "Construindo ou reconstruindo imagens..."
docker-compose build

echo "Subindo os contêineres em modo destacado..."
docker-compose up -d

echo "Aplicação redeployada com sucesso!"