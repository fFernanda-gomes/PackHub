# 🧩 PackHub

*PackHub* é uma API modular com arquitetura de *microsserviços* desenvolvida em *Java + Spring Boot*.  
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
│   └── product-service/      # Serviço de gerenciamento de produtos
├── shared/                   # Bibliotecas e utilitários compartilhados
├── tests/                    # Testes end-to-end e de integração
└── scripts/                  # Scripts úteis para o time
```

---

## 🧪 Pré-requisitos

- Java 17+ e Maven 3.8+ (Apenas para desenvolvimento fora do Docker)
- Docker + Docker Compose
- IntelliJ IDEA (recomendado)

---

## 🚀 Como rodar localmente

1. Clone o repositório:

```bash
git clone https://github.com/fFernanda-gomes/packhub.git
cd packhub
```

2. Crie um arquivo .env baseado no .env.example:

```bash
cp infra/env/.env-example infra/env/.env
```
> Edite o `.env` com suas configurações personalizadas, se necessário.
> 

3. Rode os serviços com Docker Compose:

```bash
docker-compose --env-file infra/env/.env up --build
```

4. Acesse os serviços:

- `http://localhost:8080` → `auth-service`
- `http://localhost:8081` → `product-service`

---

## 🔐 Autenticação (JWT)

1. Faça uma requisição `POST` para criar seu usuário:
```
POST http://localhost:8080/users
```

Corpo da requisição:

```json
{
  "userCode": 123456,
  "password": "suaSenha"
}
```

2. Faça uma requisição `POST` para autenticar:

```
POST http://localhost:8080/users/auth
```

Corpo da requisição:

```json
{
  "userCode": 123456,
  "password": "suaSenha"
}
```
> 🔸 *Você pode escolher os valores de `userCode` e `password` livremente.*

3. O serviço responderá com um token JWT:

```json
{
  "id": 1,
  "userCode": 123456,
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```
> **Copie o token para utilizá-lo nos endpoints privados.**

4. Para acessar endpoints protegidos em outros serviços, envie o token no header:

```
Authorization: Bearer SEU_TOKEN
```

---

## 📦 Tecnologias utilizadas

- Java 17
- Spring Boot 3.5
- Spring Security + JWT
- Spring Data JPA
- PostgreSQL (produção)
- H2 Database (testes)
- Maven
- Docker

---

## 👥 Equipe

| Nome     | Função |
|----------|--------|
| Pessoa 1 | "" |
| Pessoa 2 | "" |
| Pessoa 3 | "" |
| Pessoa 4 | "" |
| Pessoa 5 | "" |
| Pessoa 6 | "" |

