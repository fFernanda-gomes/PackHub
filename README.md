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
- Conta no [Cloudinary](https://cloudinary.com/) com as credenciais de acesso
  - `CLOUDINARY_CLOUD_NAME`
  - `CLOUDINARY_API_KEY`
  - `CLOUDINARY_API_SECRET`  
    Essas variáveis devem ser configuradas no `.env` localizado em `infra/env/.env`.

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

## ✅ Testando a API

### Criar Usuário e Autenticar

1. Faça o registro de um novo usuário via `/auth/register`
2. Autentique via `/auth/login` para obter o token JWT

### Criar Produto

- Use o token JWT como `Bearer Token`
- Envie a imagem e os dados em `multipart/form-data`:

```bash
curl --request POST http://localhost:8081/products   --header "Authorization: Bearer {seu_token}"   --header "Content-Type: multipart/form-data"   --form "image=@/caminho/para/imagem.jpg"   --form 'data={ "name": "Produto X", "price": 99.90 }'
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

