# ─── Stage 1: Build ──────────────────────────────────────────────────────────
# Usa a imagem oficial do Maven com JDK 17 para compilar o projeto
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copia apenas o pom.xml primeiro para aproveitar o cache de dependências do Docker
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copia o código-fonte e faz o build sem rodar testes (mais rápido)
COPY src ./src
RUN mvn package -DskipTests -B

# ─── Stage 2: Runtime ────────────────────────────────────────────────────────
# Usa uma imagem menor (JRE apenas) para rodar a aplicação
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Cria um usuário não-root para segurança
RUN addgroup -S nutricao && adduser -S nutricao -G nutricao

# Copia apenas o JAR gerado pelo estágio de build
COPY --from=build /app/target/*.jar app.jar

# Muda o dono do arquivo para o usuário não-root
RUN chown nutricao:nutricao app.jar

USER nutricao

# Expõe a porta que o Spring Boot usa
EXPOSE 8080

# Variável de ambiente para ativar o profile Docker
ENV SPRING_PROFILES_ACTIVE=docker

# Inicia a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
