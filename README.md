# 🧩 PackHub

**PackHub** é uma API construída com arquitetura de microsserviços utilizando Java + Spring Boot.  
O sistema é dividido em dois serviços principais:

- `auth-service`: Responsável pela autenticação dos usuários
- `product-service`: Responsável pelo cadastro e gerenciamento de produtos

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

- Java 17+
- Maven 3.8+
- Docker + Docker Compose
- IntelliJ IDEA (recomendado)

---

## 🚀 Como rodar localmente

1. Clone o repositório:

```bash
git clone https://github.com/seu-user/packhub.git
cd packhub
```

2. Rode os serviços com Docker Compose:

```bash
docker-compose up --build
```

3. Acesse:

- `http://localhost:8080` → `auth-service`
- `http://localhost:8081` → `product-service`

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

