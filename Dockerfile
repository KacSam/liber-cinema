FROM maven:3-eclipse-temurin-21 AS build

WORKDIR /app

# Kopiowanie pliku pom.xml
COPY pom.xml .

# Pobranie zależności Maven (wykorzystuje cache kontenera)
RUN mvn dependency:go-offline -B

# Kopiowanie kodu źródłowego
COPY src ./src

# Budowanie aplikacji
RUN mvn package -DskipTests

# Obraz końcowy
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Kopiowanie zbudowanego jara z poprzedniego etapu
COPY --from=build /app/target/*.jar app.jar

# Odsłonięcie portu 8080
EXPOSE 8080

# Uruchomienie aplikacji
ENTRYPOINT ["java", "-jar", "app.jar"]
