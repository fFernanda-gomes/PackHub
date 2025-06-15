# 📦 Product Service - PackHub

Este microserviço é responsável pelo **gerenciamento de produtos** na aplicação PackHub.  
Inclui endpoints para **cadastro, edição, listagem e exclusão de produtos**, com associação ao usuário autenticado. Suporta **CRUD de produtos com imagem**, usando Cloudinary.

---

## 📁 Estrutura de Pastas

```
src/main/java/com.packhub.product/
├── config/          → Configurações de CORS, serialização, etc.
├── domain/
│   ├── entities/    → Entidades JPA como Product
│   ├── repositories/→ Interfaces JPA
│   └── service/     → Lógica de negócio
├── dto/             → Objetos de requisição e resposta
└── web/             → Controllers REST
```

---

## 🧪 Endpoints principais

| Método | Rota                                | Descrição                          |
|--------|-------------------------------------|--------------------------------------|
| POST   | /products                           | Cria novo produto (com imagem)       |
| GET    | /products                           | Lista todos os produtos              |
| GET    | /products/{id}                      | Busca produto por ID                 |
| GET    | /products/user/{userCode}           | Lista produtos por userCode          |
| PUT    | /products/{id}                      | Atualiza produto                     |
| DELETE | /products/{id}                      | Exclui produto                       |

> Todos os endpoints exigem token JWT:
> `Authorization: Bearer SEU_TOKEN`

---

## 🧾 Validações

- Todos os produtos devem conter:
  - Nome (**não pode ser nulo ou vazio**)
  - Preço (**não pode ser nulo** e deve ser positivo)
  - URL da imagem (**obrigatória**)
- Cada produto está associado a um usuário autenticado
- Datas:
  - `createdAt` é preenchido automaticamente ao salvar o produto
  - `updatedAt` é atualizado automaticamente a cada modificação

---

# ☁️ Configuração do Cloudinary

Para fazer o upload de imagens, o serviço utiliza o Cloudinary. Crie uma conta em https://cloudinary.com/.

Depois, adicione as seguintes variáveis no `application.properties` ou `application.yml`:

```
cloudinary.cloud-name=SEU_CLOUD_NAME
cloudinary.api-key=SEU_API_KEY
cloudinary.api-secret=SEU_API_SECRET
```

Se estiver usando `.env` com Docker, adicione:

```
CLOUDINARY_CLOUD_NAME=SEU_CLOUD_NAME
CLOUDINARY_API_KEY=SEU_API_KEY
CLOUDINARY_API_SECRET=SEU_API_SECRET
```

---

## 🔐 Requisições protegidas

Este serviço exige autenticação via token JWT.

Antes de realizar requisições aos endpoints protegidos, você deve:

1. Autenticar-se no `auth-service`
2. Obter o token JWT
3. Enviar o token em cada requisição no header:

```
Authorization: Bearer SEU_TOKEN
```

---

## ➕ Exemplo completo: criação de produto

**Rota:** `POST /products`  
**Autenticação:** Bearer Token (JWT)  
**Content-Type:** multipart/form-data

### Parâmetros:

- `image`: Arquivo de imagem (PNG, JPEG etc)
- `data`: Objeto JSON com os campos:
  ```json
  {
    "name": "Camiseta",
    "price": 49.90
  }
  ```

### Exemplo de Requisição `curl`:

```bash
curl --request POST http://localhost:8081/products \
  --header "Authorization: Bearer SEU_TOKEN" \
  --header "Content-Type: multipart/form-data" \
  --form "image=@/caminho/imagem.jpg" \
  --form 'data={ "name": "Produto X", "price": 99.90 }'
```
---

## ⚙️ Tecnologias

- Java 17
- Spring Boot 3.5
- Spring Security (integração com auth)
- JPA / Hibernate
- PostgreSQL 
- Maven

---

## ✅ Testes

- JUnit 5 + Mockito com mocks
- Cenários de erro e sucesso testados
- Cobertura JaCoCo > 90%

---

## 🚀 Rodar isoladamente

```bash
cd services/product-service
./mvnw spring-boot:run
```

- Porta padrão: http://localhost:8081

---

## 📝 Observações

Este serviço faz parte do projeto maior **PackHub**.  
Consulte o [`auth-service`](../auth-service/README.md) para aprender como obter o token JWT necessário para usar este serviço.  
Utilize-o junto com o `auth-service` via Docker Compose para uma experiência completa.
