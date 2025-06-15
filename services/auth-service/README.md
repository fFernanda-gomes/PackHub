# 🔐 Auth Service - PackHub

Este microserviço é responsável pela **autenticação de usuários** na aplicação PackHub.  
Inclui endpoints para **cadastro, login e validação de token JWT**.

---

## 📁 Estrutura de Pastas

```
src/main/java/com.packhub.auth/
├── config/          → Configurações de segurança, JWT, etc.
├── domain/
│   ├── entities/    → Entidades JPA como User
│   ├── repositories/→ Interfaces JPA
│   └── service/     → Lógica de negócio
├── dto/             → Objetos de requisição e resposta
└── web/             → Controllers REST
```

---

## 🧪 Endpoints principais

| Método | Rota            | Descrição                   |
|--------|------------------|------------------------------|
| POST   | /auth/login      | Login de usuário com JWT     |
| POST   | /auth/register   | Cadastro de novo usuário     |
| GET    | /auth/me         | Dados do usuário autenticado |

> ⚠️ Todos os endpoints protegidos requerem o header:  
> `Authorization: Bearer <token>`

---

## 🔐 Segurança

A autenticação é baseada em **JWT (JSON Web Token)**.  
O token é retornado após o login e deve ser enviado no cabeçalho `Authorization` para acessar endpoints protegidos.

---

## ⚙️ Tecnologias

- Java 17
- Spring Boot 3.5
- Spring Security
- JWT
- JPA / Hibernate
- PostgreSQL
- Maven

---

## ✅ Testes

- Escritos com **JUnit 5** e **Mockito**
- Mock de dependências como repositórios
- Cobertura de testes com **JaCoCo** > 90%

---

## 🚀 Como rodar este serviço isoladamente

```bash
# Rodar localmente com Maven
./mvnw spring-boot:run
```

A aplicação irá iniciar em:  
➡️ http://localhost:8080

---

## 📝 Observações

Este serviço faz parte do projeto maior **PackHub**.  
Utilize-o junto com o `product-service` via Docker Compose para uma experiência completa.
