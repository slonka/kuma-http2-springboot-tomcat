FROM openjdk:17.0.2-jdk

ADD build/libs/demo-0.0.1-SNAPSHOT.war app.war

ENTRYPOINT ["java"]

CMD ["-jar","app.war"]
