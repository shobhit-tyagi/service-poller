FROM openjdk:11-jre-slim
RUN mkdir /home/app
COPY target/*.jar /home/app/app.jar
ENTRYPOINT ["java","-jar","/home/app/app.jar"]