# Documentação de Decisões de Design e Estratégias de Evolução

## 1. Modelagem do Banco de Dados

Na hora de modelar a entidade *Package*, busquei um equilíbrio entre desempenho e a integridade dos dados.

- **Estrutura e Tipos de Dados:**  
  Optei por tipos específicos para cada coluna – por exemplo, escolhi utilizar `VARCHAR` com tamanhos controlados para `description`, `sender` e `recipient`. Essa decisão ajuda a economizar espaço no banco e agiliza as buscas.

- **Índices:**  
  Criei índices nas colunas mais importantes nas consultas e joins, como `sender`, `recipient`, `status` e `createdAt`. Dessa forma, mesmo com grandes volumes de registro, as pesquisas se mantêm rápidas e eficientes.

- **Normalização vs. Desnormalização:**  
  Inicialmente, segui os princípios da normalização para preservar a integridade dos dados e evitar redundâncias. Entretanto, se a demanda por leitura aumentar consideravelmente, pretendo avaliar a criação de views materializadas ou tabelas resumo para reduzir a carga de joins pesados.

- **Particionamento:**  
  Recomendei particionar a tabela com base no `createdAt`. Essa estratégia não só melhora a performance das consultas, mas também facilita o expurgo de dados antigos, tornando a manutenção mais simples e eficiente.

## 2. Estratégias de Escalabilidade e Otimização

Para garantir que a aplicação se mantenha rápida mesmo sob alta carga, adotei as seguintes estratégias:

- **Pool de Conexões com HikariCP:**  
  Configurei o HikariCP ajustando parâmetros como `maximum-pool-size`, `minimum-idle`, `idle-timeout` e `max-lifetime` para suportar situações de alta carga. Além disso, utilizei o Spring Boot Actuator para monitorar o pool de conexões – acompanhando métricas como conexões ativas e disponíveis –, o que me ajuda a identificar e resolver possíveis gargalos.

- **Caching:**  
  Implementei cache (utilizando Caffeine ou o cache padrão do Spring) para acelerar operações de leitura, reduzindo a latência e a sobrecarga no banco de dados.

- **Expurgo de Dados:**  
  Com o particionamento, fica mais fácil descartar dados antigos. Planejo usar o MySQL Event Scheduler para agendar a remoção de partições que não são mais necessárias, mantendo a base de dados enxuta e performática.

## 3. Processamento Assíncrono e Gestão de Threads

Para garantir que processos demorados não travem a aplicação, adotei o processamento assíncrono:

- **Processamento Assíncrono com Spring Async:**  
  Ativei o processamento assíncrono com `@EnableAsync` e configurei um pool de threads customizado (usando `ThreadPoolTaskExecutor`). Essa abordagem me permite executar operações pesadas, como notificações via webhook, em segundo plano, sem bloquear a thread principal.

- **Resiliência e Monitoramento:**  
  Implementei um tratamento cuidadoso de exceções dentro dos métodos assíncronos (como no serviço `WebhookNotifier`), garantindo que falhas em tarefas de background não interrompam o fluxo geral da aplicação. Adotei logging detalhado para facilitar a identificação e resolução de problemas futuros.

## 4. Ideias para Evolução e Melhoria

Embora a implementação atual atenda bem aos requisitos, sempre há espaço para evolução. Algumas ideias que tenho para o futuro incluem:

- **Desnormalização e Views Materializadas:**  
  Caso as leituras se intensifiquem, posso considerar criar views materializadas ou tabelas resumo, reduzindo a necessidade de joins pesados e melhorando a performance.

- **Ferramentas de Monitoramento:**  
  Avaliar a integração com ferramentas de APM (Application Performance Monitoring) para identificar gargalos em tempo real e otimizar ainda mais o desempenho do pool de conexões e do executor de threads.

- **Arquitetura de Microserviços:**  
  Em um cenário de crescimento contínuo, migrar para uma arquitetura de microserviços pode ser uma estratégia interessante para isolar componentes críticos e escalá-los de forma independente.

- **Testes Automatizados:**  
  Ampliar a cobertura de testes unitários e de integração garante que a aplicação continue estável e performática mesmo em condições extremas e durante processos assíncronos.

- **Segurança e Resiliência:**  
  Implementar circuit breakers, utilizando ferramentas como Resilience4j, pode ajudar a prevenir que falhas em serviços externos impactem a aplicação, melhorando sua robustez geral.

---

Esta documentação reflete as decisões que tomei até o momento e serve como um guia para a evolução contínua da aplicação, garantindo que ela se mantenha estável, performática e escalável à medida que a demanda cresce.