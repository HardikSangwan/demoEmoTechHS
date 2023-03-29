FROM eclipse-temurin:19.0.2_7-jdk
COPY build/libs/demoEmoTechHS-0.0.1-SNAPSHOT.jar demoEmoTechHS-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "/demoEmoTechHS-0.0.1-SNAPSHOT.jar"]