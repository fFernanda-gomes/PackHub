# 📦 Product Service - PackHub

Este microserviço é responsável pelo **gerenciamento de produtos** na aplicação PackHub.  
Inclui endpoints para **cadastro, edição, listagem e exclusão de produtos**, com associação ao usuário autenticado.

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

## ➕ Criar Produto

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
curl --request POST   --url http://localhost:8081/products   --header 'Authorization: Bearer SEU_TOKEN_JWT'   --header 'Content-Type: multipart/form-data'   --form image=@/caminho/da/imagem.png   --form 'data={ "name": "Camiseta", "price": 49.9 }'
```

---

## 📁 Estrutura de Pastas

```
src/main/java/com.packhub.product/
├── config/          → Configurações de CORS, serialização, etc.
├── domain/
│   ├── entities/    → Entidades JPA como Product
│   ├── exception/   → Classes de erro customizadas
│   ├── listeners/   → Listeners de eventos de entidades
│   ├── repositories/→ Interfaces JPA
│   └── service/     → Lógica de negócio
├── dto/             → Objetos de requisição e resposta
└── web/             → Controllers REST
```

---

## ⚙️ Tecnologias

- Java 17
- Spring Boot 3.5
- Spring Security (integração com auth)
- JPA / Hibernate
- PostgreSQL (produção)
- H2 Database (testes)
- Maven

---

## 🚀 Como rodar este serviço isoladamente

```bash
# Rodar localmente com Maven
./mvnw spring-boot:run
```

A aplicação irá iniciar em:  
➡️ http://localhost:8081

---

## 🧪 Endpoints principais

| Método | Rota         | Descrição                        |
|--------|--------------|-----------------------------------|
| POST   | /products     | Criação de produto                |
| GET    | /products     | Listagem de produtos              |
| PUT    | /products/{id}| Atualização de um produto         |
| DELETE | /products/{id}| Exclusão de um produto            |

> ⚠️ Todos os endpoints requerem autenticação com JWT:  
> `Authorization: Bearer <token>`

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

## 📝 Observações

Este serviço faz parte do projeto maior **PackHub**.  
Utilize-o junto com o `auth-service` via Docker Compose para uma experiência completa.
