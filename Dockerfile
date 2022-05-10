FROM openjdk:11.0.6-jre


ADD target/cryptocurrency-service.jar app.jar
RUN sh -c 'touch /app.jar'
VOLUME /tmp
EXPOSE 9000 5701/udp
ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar" ]
