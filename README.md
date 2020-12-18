![build](https://github.com/equidis/spring-boot-availability-service/workflows/build/badge.svg)
[![codecov](https://codecov.io/gh/equidis/spring-boot-availability-service/branch/master/graph/badge.svg?token=OB1F66EA4A)](https://app.codecov.io/gh/equidis/spring-boot-availability-service)
![release](https://img.shields.io/github/v/tag/equidis/spring-boot-availability-service)
![license](https://img.shields.io/github/license/equidis/spring-boot-availability-service)

# Availability service

Sample [Spring Boot](https://spring.io/projects/spring-boot) microservice that mimics
[Micronaut GRPC availability service](https://github.com/equidis/micronaut-grpc-availability-service) to compare performance.

## Usage

### Running application

###### Using Gradle

`./gradlew bootRun`

###### Using Java archive

`./gradlew build`
`java -jar build/libs/sb-availability-{APP_VERSION}.jar`

###### Using Docker

`./gradlew jibDockerBuild`
