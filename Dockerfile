FROM eclipse-temurin:17-jdk-alpine
COPY target/hamsoo-0.0.1-SNAPSHOT.jar /usr/app/
COPY target/classes/fonts /usr/share/fonts
WORKDIR /usr/app/
EXPOSE 8989

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar hamsoo-0.0.1-SNAPSHOT.jar"]
