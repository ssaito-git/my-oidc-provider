FROM gradle:8.5-jdk17 AS build
COPY --chown=gradle:gradle .. /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle :my-oauth2-oidc-provider-ktor-sample:buildFatJar --no-daemon

FROM eclipse-temurin:17-jre
EXPOSE 8080:8080
RUN mkdir /app
COPY --from=build \
  /home/gradle/src/my-oauth2-oidc-provider-ktor-sample/build/libs/my-oidc-provider-ktor-sample-all.jar \
  /app/my-oidc-provider-ktor-sample-all.jar
ENTRYPOINT ["java", "-jar", "/app/my-oidc-provider-ktor-sample-all.jar"]
