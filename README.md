# track-flow

Este projeto é uma aplicação Spring Boot para rastreamento de pacotes. A seguir, apresento instruções para executar a aplicação localmente.

## Pré-requisitos

- **Java 21**: Certifique-se de ter o JDK 21 instalado.
- **Maven**: Para construir o projeto.
- **Docker e Docker Compose**: Para rodar o MySQL (ou outro serviço dependente) em container.
- **MySQL**: Caso prefira executar o banco de dados localmente ao invés de usar o container.

## Dependências do Projeto

O projeto utiliza as seguintes dependências:
- Spring Boot Starters: Web, Data JPA, Actuator e Validation.
- MySQL Connector/J para conectar ao banco MySQL.
- HikariCP para pooling de conexões.
- Lombok (opcional para facilitar a escrita de código).
- Logstash Logback Encoder para formatação de logs.
- Spring Boot Actuator para monitoramento.

## Execução Local com Docker

Este projeto está preparado para rodar com Docker. Siga os passos abaixo:

1. **Construa o projeto com Maven:**

   Execute o comando abaixo na raiz do projeto:
   ```
   mvn clean package
   ```

2. **Construa a imagem Docker da aplicação:**

   ```
   docker build -t track-flow-app .
   ```

3. **Execute a aplicação juntamente com o MySQL usando Docker Compose:**

   Certifique-se de que o arquivo `docker-compose.yml` está corretamente configurado para levantar o serviço MySQL e a aplicação.
   ```
   docker-compose up --build -d
   ```

4. **Verifique os containers em execução:**
   ```
   docker ps
   ```

5. **Acesse o container da aplicação (caso seja necessário diagnosticar ou executar comandos):**
   ```
   docker exec -it <container_id> /bin/bash
   ```

6. **Acesse o MySQL via container:**

   Entre no container MySQL e abra um shell:
   ```
   docker exec -it <mysql_container_id> /bin/bash
   mysql -u admin -p   # Digite a senha: admin
   USE trackflowdb;
   SELECT * FROM pacotes;
   ```

7. **Para acompanhar os logs da aplicação:**
   ```
   docker logs -f <container_id_or_name>
   ```

### Utilizando Scripts para Facilitar a Execução

Para agilidade na reinicialização completa da aplicação, criei dois scripts:

- **Linux/MacOS:**  
  Use o script `redeploy.sh` localizado na pasta `bash-scripts`:
  ```
  cd bash-scripts
  ./redeploy.sh
  ```

- **Windows:**  
  Use o script `redeploy.bat` localizado na pasta `bash-scripts`:
  ```
  cd bash-scripts
  redeploy.bat
  ```

## Execução Local sem Docker

Caso prefira executar a aplicação localmente com um MySQL instalado na sua máquina:

1. **Configure o Banco de Dados:**
   - Instale e configure o MySQL.
   - Crie um banco de dados chamado `trackflowdb`.
   - Atualize as configurações de conexão no arquivo `src/main/resources/application.properties`, se necessário.

2. **Construa o projeto com Maven:**
   ```
   mvn clean package
   ```

3. **Rode a aplicação:**
   ```
   mvn spring-boot:run
   ```

4. **Acesse a aplicação em:**
   ```
   http://localhost:8080/api
   ```

## Comandos Úteis

- **Para reconstruir e subir os containers rapidamente:**
  ```
  docker-compose down && mvn clean package && docker build -t track-flow-app . && docker-compose up -d
  ```

- **Para visualizar logs da aplicação:**
  ```
  docker logs -f <container_id_or_name>
  ```

## Contrato da API

Para detalhes completos sobre o contrato da API – incluindo especificações, endpoints, request/response e regras de negócio – consulte o arquivo [CONTRACT.md](CONTRACT.md). Este arquivo contém a documentação técnica que garante que os consumidores da API e os desenvolvedores estejam alinhados quanto aos requisitos e comportamentos esperados.

---

Seguindo estas instruções e utilizando os scripts fornecidos, você conseguirá executar a aplicação localmente com facilidade e monitorar seu funcionamento.