# Documentação de Acesso às Rotas da API - track-flow

Esta documentação descreve, em detalhes, os endpoints disponíveis para interação com a API de rastreamento de pacotes. A API está acessível a partir da base URL:  
**http://localhost:8080/api**

---

## 1. Criação de Pacote

- **Método:** POST  
- **Endpoint:** `/packages`  
- **Descrição:** Cria um novo pacote no sistema, gerando automaticamente um evento inicial (por exemplo, "Pacote chegou ao centro de distribuição").  
- **Request Body (JSON):**  
  ```json
  {
      "description": "Pacote de Exemplo",
      "sender": "Empresa X",
      "recipient": "Cliente Y",
      "estimatedDeliveryDate": "2025-03-20"
  }
  ```  
- **Exemplo de Resposta:**  
  - **Status:** 201 Created  
  - **Headers:**  
    - `Location`: URL do novo recurso criado  
    - `ETag`: valor gerado para cache  
  - **Body:**  
  ```json
  {
      "id": "uuid-gerado",
      "description": "Pacote de Exemplo",
      "sender": "Empresa X",
      "recipient": "Cliente Y",
      "status": "CREATED",
      "createdAt": "2023-10-05T14:30:45Z",
      "updatedAt": "2023-10-05T14:30:45Z",
      "deliveredAt": null,
      "events": []
  }
  ```

---

## 2. Listagem de Pacotes

- **Método:** GET  
- **Endpoint:** `/packages`  
- **Descrição:** Lista todos os pacotes cadastrados.  
- **Query Parameters (opcionais):**  
  - `sender`: Filtra pacotes pelo remetente.  
  - `recipient`: Filtra pacotes pelo destinatário.  
- **Exemplos de Chamada:**  
  - Listar todos:  
    ```
    GET http://localhost:8080/api/packages
    ```  
  - Listar com filtros:  
    ```
    GET http://localhost:8080/api/packages?sender=Empresa%20X&recipient=Cliente%20Y
    ```  
- **Exemplo de Resposta (JSON):**  
  ```json
  [
      {
          "id": "uuid-1",
          "description": "Pacote 1",
          "sender": "Empresa X",
          "recipient": "Cliente Y",
          "status": "CREATED",
          "createdAt": "2023-10-05T14:30:45Z",
          "updatedAt": "2023-10-05T14:30:45Z"
      },
      {
          "id": "uuid-2",
          "description": "Pacote 2",
          "sender": "Empresa X",
          "recipient": "Cliente Y",
          "status": "IN_TRANSIT",
          "createdAt": "2023-10-06T10:15:00Z",
          "updatedAt": "2023-10-06T10:15:00Z"
      }
  ]
  ```

---

## 3. Filtragem de Pacotes

- **Método:** GET  
- **Endpoint:** `/packages/filter`  
- **Descrição:** Retorna uma lista de pacotes aplicando os filtros pelos parâmetros informados.  
- **Query Parameters (opcionais):**  
  - `sender`  
  - `recipient`  
- **Exemplo de Chamada:**  
  ```
  GET http://localhost:8080/api/packages/filter?sender=Empresa%20X&recipient=Cliente%20Y
  ```  
- **Exemplo de Resposta:**  
  Lista de pacotes filtrados conforme os critérios.

---

## 4. Consulta de Eventos do Pacote

- **Método:** GET  
- **Endpoint:** `/packages/{id}/events`  
- **Descrição:** Retorna os eventos associados a um pacote específico.  
- **Path Parameter:**  
  - `id`: UUID do pacote.  
- **Exemplo de Chamada:**  
  ```
  GET http://localhost:8080/api/packages/abcd-efgh-1234/events
  ```  
- **Exemplo de Resposta (JSON):**  
  ```json
  [
      {
          "id": "event_uuid",
          "packageId": "abcd-efgh-1234",
          "localization": "Centro de Distribuição São Paulo",
          "description": "Pacote chegou ao centro de distribuição",
          "timestamp": "2023-10-05T15:00:00Z"
      }
  ]
  ```

---

## 5. Consulta de Pacote por ID com Suporte a Cache (ETag)

- **Método:** GET  
- **Endpoint:** `/packages/{id}`  
- **Descrição:** Recupera os detalhes de um pacote específico. Suporta validação de cache via ETag.
- **Path Parameter:**  
  - `id`: UUID do pacote.  
- **Query Parameter (opcional):**  
  - `showEvents`: Boolean, indica se os eventos associados ao pacote também devem ser retornados (default true).  
- **Header (opcional):**  
  - `If-None-Match`: Se fornecido e igual ao ETag gerado, o servidor responde com status 304 (Not Modified).  
- **Exemplo de Chamada:**  
  ```
  GET http://localhost:8080/api/packages/abcd-efgh-1234?showEvents=true
  ```  
  Para utilizar cache:  
  ```
  GET http://localhost:8080/api/packages/abcd-efgh-1234?showEvents=true
  Header: If-None-Match: eTagGerado
  ```  
- **Exemplo de Resposta:**  
  - **Caso Modificado (Status 200):**
    ```json
    {
        "id": "abcd-efgh-1234",
        "description": "Pacote de Exemplo",
        "sender": "Empresa X",
        "recipient": "Cliente Y",
        "status": "CREATED",
        "createdAt": "2023-10-05T14:30:45Z",
        "updatedAt": "2023-10-05T14:30:45Z",
        "deliveredAt": null,
        "events": [ ... ]
    }
    ```  
  - **Caso Não Modificado (Status 304):**  
    Sem body, apenas headers `ETag` e `Cache-Control`.

---

## 6. Atualização de Status de um Pacote

- **Método:** PUT  
- **Endpoint:** `/packages/{id}/status`  
- **Descrição:** Atualiza o status de um pacote.  
- **Path Parameter:**  
  - `id`: UUID do pacote.  
- **Request Body (JSON):**  
  ```json
  {
      "status": "IN_TRANSIT"
  }
  ```  
- **Exemplo de Chamada:**  
  ```
  PUT http://localhost:8080/api/packages/abcd-efgh-1234/status
  ```  
- **Exemplo de Resposta:**  
  ```json
  {
      "id": "abcd-efgh-1234",
      "description": "Pacote de Exemplo",
      "sender": "Empresa X",
      "recipient": "Cliente Y",
      "status": "IN_TRANSIT",
      "createdAt": "2023-10-05T14:30:45Z",
      "updatedAt": "2023-10-05T15:45:00Z",
      "deliveredAt": null,
      "events": [ ... ]
  }
  ```  
- **Observações:**  
  Se ocorrer um erro na atualização (por exemplo, transição de status inválida), a API retorna status 400.

---

## 7. Cancelamento de um Pacote

- **Método:** PUT  
- **Endpoint:** `/packages/{id}/cancel`  
- **Descrição:** Cancela o pacote correspondente ao `id` informado.  
- **Path Parameter:**  
  - `id`: UUID do pacote.  
- **Exemplo de Chamada:**  
  ```
  PUT http://localhost:8080/api/packages/abcd-efgh-1234/cancel
  ```  
- **Exemplo de Resposta:**  
  ```json
  {
      "id": "abcd-efgh-1234",
      "status": "CANCELED",
      "timestamp": "2023-10-05T16:00:00Z"
  }
  ```  
- **Observações:**  
  Em caso de falha na operação, a API retornará status 400 com uma descrição do erro.

---

## Informações Adicionais

- **Cache com ETag:**  
  O endpoint GET `/packages/{id}` utiliza ETag para controle de cache. Se o cabeçalho `If-None-Match` for enviado e corresponder ao ETag do recurso, o servidor retornará status 304 (Not Modified).

- **Logs e Monitoramento:**  
  Cada rota registra logs informativos com detalhes relevantes, usando níveis adequados (INFO, DEBUG, WARN e ERROR). Os logs auxiliam no diagnóstico e não são redundantes.

---

Esta documentação de acesso às rotas oferece uma visão clara e completa dos recursos disponíveis na API do track-flow, facilitando a integração e os testes realizados pelos usuários da API.