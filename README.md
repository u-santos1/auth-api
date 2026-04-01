# 🔐 AuthAPI — Projeto de Segurança Isolado

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=spring-security&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=JSON%20web%20tokens&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2CA5E0?style=for-the-badge&logo=docker&logoColor=white)
![Flyway](https://img.shields.io/badge/Flyway-CC0200?style=for-the-badge&logo=flyway&logoColor=white)

## 📌 O Que É

Uma API REST de autenticação e autorização completa, construída **100% do zero**.

O objetivo deste projeto não é ser apenas mais um sistema de login, mas sim um **laboratório isolado** para dominar profundamente os conceitos de segurança web, Spring Security, JWT (JSON Web Tokens) e controle de acesso (RBAC - Role-Based Access Control). Ao desenvolver esta API separadamente de outros sistemas comerciais, o foco se mantém estritamente na engenharia de segurança, Clean Code, tratamento de exceções e infraestrutura escalável.

## 🚀 Funcionalidades

- **Registro de Usuários:** Criação de contas com validação rigorosa de dados (higienização de strings e verificação de duplicidade).
- **Criptografia de Senhas:** Nenhuma senha é salva em texto limpo. Utilização de `BCryptPasswordEncoder` para hashing seguro.
- **Controle de Perfis (Roles):** Separação clara entre perfis de acesso (`ADMIN` e `USER`), garantindo que a escalada de privilégios seja controlada pela regra de negócio.
- **Autenticação JWT:** Geração e validação de tokens JWT para rotas protegidas, garantindo uma API Stateless (sem estado).
- **Tratamento de Exceções Profissional:** Respostas HTTP semânticas e amigáveis (ex: Erro `409 Conflict` para e-mails já cadastrados, ao invés de Erros `500` genéricos).
- **Database Migrations:** Versionamento de banco de dados garantindo que a estrutura (tabelas e constraints) evolua de forma segura e rastreável.

## 🛠️ Tecnologias Utilizadas

- **Linguagem:** Java 17
- **Framework:** Spring Boot 3
- **Segurança:** Spring Security & JWT
- **Banco de Dados:** PostgreSQL
- **Infraestrutura:** Docker (Containerização do Banco de Dados)
- **Migrations:** Flyway
- **Utilitários:** Lombok