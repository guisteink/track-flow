# track-flow

##########################################
docker ps
docker exec -it <container_id> /bin/bash
mysql -u admin -p
USE trackflowdb;
SELECT * FROM pacotes;
##########################################
docker ps
docker logs -f <container_id_or_name>
###########################################
docker-compose up --build -d
###########################################
docker build -t track-flow-app .
###########################################
docker-compose down && mvn clean package && docker build -t track-flow-app . && docker-compose up