# 🧩 PackHub

**PackHub** é uma API modular com arquitetura de **microsserviços** desenvolvida em **Java + Spring Boot**.  
O sistema permite que usuários autenticados cadastrem e gerenciem produtos com nome, imagem e preço.  
É ideal para projetos de marketplace, catálogos ou sistemas administrativos com autenticação e gerenciamento de dados.

O projeto é dividido em dois serviços principais:

- auth-service: Responsável pela autenticação dos usuários (login, JWT, registro)
- product-service: Responsável pelo cadastro e gerenciamento de produtos (CRUD com imagem, nome e preço)

---

## 🏗️ Estrutura do Monorepo

```
packhub/
├── docker-compose.yml        # Orquestração dos serviços
├── infra/                    # Configurações de infraestrutura (DB, Nginx, etc.)
├── services/                 # Microserviços independentes
│   ├── auth-service/         # Serviço de autenticação (login, JWT, etc.)
└── └── product-service/      # Serviço de gerenciamento de produtos
```

---

## 📦 Tecnologias utilizadas

- Java 17
- Spring Boot 3.5
- Spring Security + JWT
- Spring Data JPA
- PostgreSQL
- Maven
- Docker

---

## 🚀 Como rodar localmente

### ✅ Pré-requisitos

- Java 17+ e Maven 3.8+ (para rodar fora do Docker)
- Docker + Docker Compose
- Conta no [Cloudinary](https://cloudinary.com/)

### 🔧 Configuração

1. Clone o repositório:

```bash
git clone https://github.com/fFernanda-gomes/packhub.git
cd packhub
```

2. Crie um arquivo `.env`:

```bash
cp infra/env/.env-example infra/env/.env
```

3. Edite o `.env` com suas credenciais do PostgreSQL, JWT e Cloudinary.
> ℹ️ **Importante:** se for rodar com Docker, use os nomes dos serviços (`postgres-auth`, `postgres-product`) como `HOST`, em vez de `localhost`.

4. Rode com Docker:

```bash
docker-compose --env-file infra/env/.env up --build
```

---

## ✅ Testando a API

### Criar Usuário e Autenticar

1. Faça o registro de um novo usuário via `/auth/register`
2. Autentique via `/auth/login` para obter o token JWT

### Criar Produto

- Use o token JWT como `Bearer Token`
- Envie a imagem e os dados em `multipart/form-data`:

```bash
curl --request POST http://localhost:8081/products \
  --header "Authorization: Bearer {seu_token}" \
  --header "Content-Type: multipart/form-data" \
  --form "image=@/caminho/para/imagem.jpg" \
  --form 'data={ "name": "Produto X", "price": 99.90 }'
```

### ℹ️ Observação

Para ver todos os **endpoints disponíveis** e **exemplos de uso detalhados**, consulte os READMEs individuais dos serviços:

- 🔐 [`auth-service`](./services/auth-service/README.md)
- 📦 [`product-service`](./services/product-service/README.md)

---

## 🌐 Serviços Disponíveis

| Serviço          | Porta | Função                            |
|------------------|-------|------------------------------------|
| `auth-service`   | 8080  | Cadastro, login, JWT              |
| `product-service`| 8081  | CRUD de produtos com imagens      |

---

## 🔍 Acesse os serviços

- Swagger Auth: http://localhost:8080/swagger-ui.html
- Swagger Product: http://localhost:8081/swagger-ui.html

---

## 👥 Equipe

| Nome            | Função                                                              | GitHub |
|-----------------|---------------------------------------------------------------------|--------|
| Fernanda Gomes  | Infra, configuração, segurança, JWT, testes, produto e documentação | [@fFernanda-gomes](https://github.com/fFernanda-gomes) |
| Kauã Rodrigues  | Infra, API de produtos/autenticação e testes                        | [@Kaua1805](https://github.com/Kaua1805) |
| Arthur Felix    | Configuração, API de autenticação e testes                          | [@ArthurFelixMuniz](https://github.com/ArthurFelixMuniz) |
| Willian Duarte  | Configuração, JWT, API de autenticação e testes                     | [@Willian917](https://github.com/Willian917) |
| Luiz Henrique   | API de produtos, configuração e testes                              | [@HenriqueSantos39](https://github.com/HenriqueSantos39) |