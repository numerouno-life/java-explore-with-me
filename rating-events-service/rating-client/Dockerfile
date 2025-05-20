FROM amazoncorretto:21
ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:9095
COPY target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]