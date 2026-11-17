# syntax=docker/dockerfile:1

# ---------------------------------------------------------------- build
FROM maven:3.10-eclipse-temurin-21 AS build
WORKDIR /build

# Las dependencias se cachean en su propia capa: cambiar el codigo no las re-descarga.
COPY pom.xml ./
RUN mvn -B -q dependency:go-offline

COPY src ./src
RUN mvn -B -q clean package -DskipTests

# ---------------------------------------------------------------- runtime
FROM eclipse-temurin:21-jre

# curl lo usa el healthcheck del compose.
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app
RUN groupadd --system --gid 10001 awra \
    && useradd --system --uid 10001 --gid awra awra

COPY --from=build --chown=awra:awra /build/target/*.jar /app/app.jar

USER awra
EXPOSE 8080

# /api/publico/sitio consulta la base: el contenedor solo esta sano si la API
# puede hablar con Oracle.
HEALTHCHECK --interval=15s --timeout=5s --start-period=90s --retries=10 \
    CMD curl -fsS http://localhost:8080/api/publico/sitio > /dev/null || exit 1

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
