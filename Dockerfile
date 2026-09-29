# Etape 1 : Construction de l'application (Build Stage)
FROM eclipse-temurin:11-jdk AS builder
WORKDIR /app

# Copier les fichiers de configuration Maven
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Donner les droits d'exécution au wrapper Maven
RUN chmod +x ./mvnw

# Télécharger les dépendances (cache la couche si le pom.xml ne change pas)
RUN ./mvnw dependency:go-offline -B

# Copier le code source
COPY src ./src

# Compiler et générer le .jar (sans exécuter les tests)
RUN ./mvnw clean package -DskipTests

# Etape 2 : Création de l'image finale d'exécution (Run Stage)
FROM eclipse-temurin:11-jre
WORKDIR /app

# Copier uniquement le .jar généré depuis l'étape de build
COPY --from=builder /app/target/odcdevops-0.0.1-SNAPSHOT.jar app.jar

# Exposer le port par défaut de Spring Boot
EXPOSE 8080

# Commande pour démarrer l'application
ENTRYPOINT ["java", "-jar", "app.jar"]
