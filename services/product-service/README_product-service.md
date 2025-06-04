# 📦 Product Service - PackHub

Este microserviço é responsável pelo **gerenciamento de produtos** na aplicação PackHub.  
Inclui endpoints para **cadastro, edição, listagem e exclusão de produtos**, com associação ao usuário autenticado.

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
