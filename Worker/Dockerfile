#
# Build Stage
#

FROM maven:3.6.3-jdk-11-openj9 as build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package

#
# Package stage
#
FROM tomcat:9.0-alpine
COPY --from=build /home/app/target/Worker.war /usr/local/tomcat/webapps/Worker.war
EXPOSE 8080

CMD ["/usr/local/tomcat/bin/catalina.sh", "run"]