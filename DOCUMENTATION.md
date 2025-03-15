# Documentação de Decisões de Design e Estratégias de Evolução

## 1. Modelagem do Banco de Dados

Na modelagem da entidade *Package* e suas associações (ex.: eventos), foram consideradas as seguintes decisões:

- **Estrutura e Tipos de Dados:**  
  Foram definidos tipos específicos para cada coluna, como `VARCHAR` de tamanhos controlados para campos como `description`, `sender` e `recipient`, garantindo economia de espaço e desempenho nas buscas.  
  As anotações JPA, como `@PrePersist` e `@PreUpdate`, garantem que as datas de criação e atualização sejam definidas automaticamente.

- **Índices e Relacionamentos:**  
  Foram criados índices nas colunas utilizadas nas consultas frequentes (`sender`, `recipient`, `status` e `createdAt`), otimizando as buscas.  
  O relacionamento entre *Package* e *Event* utiliza `@OneToMany` com _lazy fetching_ para evitar carregamento desnecessário dos eventos.

## 2. Estratégias de Escalabilidade e Otimização

Para garantir a performance sob alta carga, aplicamos diversas otimizações:

- **Pool de Conexões com HikariCP:**  
  O HikariCP está configurado com propriedades como `maximum-pool-size`, `minimum-idle` e `max-lifetime` para otimizar a gestão das conexões com o banco MySQL.  
  A utilização do Spring Boot Actuator e a exposição de métricas permitem monitorar a saúde do pool.

- **Caching com Caffeine:**  
  Implementamos cache para acelerar operações de leitura (ex.: método `getPackageById`), reduzindo a latência de acesso ao banco.

- **Otimizações de Consultas e Particionamento:**  
  A criação de índices e a sugestão de particionamento da tabela (baseado em `createdAt`) auxiliam na manutenção do desempenho à medida que o volume de dados cresce.

## 3. Abordagem para Gestão de Threads e Chamadas Assíncronas

- **Processamento Assíncrono com Spring Async:**  
  O uso da anotação `@EnableAsync` junto com a configuração de um `ThreadPoolTaskExecutor` personalizado garante que tarefas demoradas, como notificações via webhook, sejam executadas em background sem impactar a performance da thread principal.  
  A escolha de delegar a execução assíncrona à assinatura `@Async("taskExecutor")` reflete a preocupação com escalabilidade e resiliência da aplicação.

- **Gestão de Threads Customizada:**  
  A configuração do `ThreadPoolTaskExecutor` (core pool size, max pool size, queue capacity) foi feita para balancear o throughput e a utilização dos recursos de forma eficiente, atendendo a cenários de alta demanda.

## 4. Estratégias e Melhorias para Evolução Futura

Embora a implementação atual atenda aos requisitos, há diversas oportunidades para evoluir a aplicação:

- **Normalização, Desnormalização e Views Materializadas:**  
  Se a leitura intensificar, considerar a criação de views materializadas ou tabelas resumo pode reduzir a sobrecarga de joins e melhorar a performance.

- **Ferramentas de Monitoramento e Circuit Breakers:**  
  Integrar ferramentas de APM (Application Performance Monitoring) e implementar circuit breakers (ex.: com Resilience4j) permitirão identificar gargalos e aumentar a resiliência, evitando que falhas em serviços externos comprometam a aplicação.
    - Datadog? =)
- **Evolução da Arquitetura:**  
  Em cenários de crescimento contínuo, migrar para uma arquitetura de microserviços pode favorecer o isolamento, escalabilidade e manutenção dos componentes críticos.
    -  Deploy em ambiente AWS Elastic Computing Service

- **Ampliar a Cobertura de Testes:**  
  Incrementar testes unitários e de integração garantirá a estabilidade e a confiabilidade durante futuras evoluções e refatorações.

- **Notificações e Processamento Event-Driven:**  
  Embora o webhook atual funcione de maneira assíncrona, há oportunidade de evoluir para uma arquitetura baseada em eventos (ex.: uso de message brokers como RabbitMQ ou Kafka) para uma escalabilidade ainda maior da comunicação entre serviços.
    - Kafka, RabbitMQ, SQS, SNS, etc

---

Esta documentação reflete as decisões que tomei até o momento e serve como um guia para a evolução contínua da aplicação, garantindo que ela se mantenha estável, performática e escalável à medida que a demanda cresce.