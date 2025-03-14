@echo off
echo Parando contêineres...
docker-compose down

echo Construindo imagens...
docker-compose build

echo Subindo contêineres...
docker-compose up -d

echo Aplicação redeployada com sucesso!
pause