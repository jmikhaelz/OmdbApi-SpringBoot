# OMDB API Spring Boot

Este proyecto es una implementación en Java con Spring Boot que consume la API de OMDB para obtener información sobre series y películas.

## 📜 Características

- Interfaz interactiva en la línea de comandos (`MenuInteractivo.java`)
- Cliente para consumo de la API OMDB (`OmdbApiClient.java`)
- Modelo de datos para series, temporadas y episodios
- Configuración mediante propiedades (`application.properties`)

## 🚀 Requisitos

- **Java 17+** (`openjdk 17.0.15`)
- **Maven** (`pom.xml`)
- **Spring Boot**

## ⚙️ Instalación y Ejecución

```bash
git clone https://github.com/TU-USUARIO/omdbapi-springboot.git
cd omdbapi-springboot
./mvnw spring-boot:run
```

## 📂 Estructura del Proyecto

```sh
.
├── src
│   ├── main/java/mx/aluracursos/omdbapi_springboot
│   │   ├── cli/           # Componentes de la interfaz interactiva
│   │   ├── client/        # Cliente para la API de OMDB
│   │   ├── config/        # Configuración del proyecto
│   │   ├── models/        # Clases de datos (Serie, Temporada, Episodio)
│   │   ├── service/       # Lógica de negocio y procesamiento de datos
│   └── test/java/mx/aluracursos/omdbapi_springboot
│       └── OmdbapiSpringbootApplicationTests.java # Pruebas automatizadas
├── pom.xml                # Dependencias y configuración de Maven
└── README.md              # Este documento 📖

```

## 🏗️ Tecnologías utilizadas
Especifica las herramientas y frameworks que usaste, por ejemplo:

Java 17

Spring Boot

Maven

API de OMDB