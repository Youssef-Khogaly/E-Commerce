FROM eclipse-temurin:25

COPY ./target/E-commerce-app.jar /usr/app/
WORKDIR /usr/app

EXPOSE 8080
EXPOSE 3306

ENTRYPOINT ["java" , "-jar" , "E-commerce-app.jar"]