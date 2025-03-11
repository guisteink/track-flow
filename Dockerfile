# Usar uma imagem base com o JDK 21
FROM eclipse-temurin:21-jdk-jammy

# Diretório de trabalho dentro do contêiner
WORKDIR /app

# Copiar o arquivo JAR gerado pelo Maven para o contêiner
COPY target/track-flow-0.0.1-SNAPSHOT.jar app.jar

# Expor a porta em que a aplicação Spring Boot roda
EXPOSE 8080

# Comando para executar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]