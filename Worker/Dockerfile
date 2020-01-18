
FROM tomcat:9.0-alpine

LABEL maintainer="Richard"

COPY tomcat_settings/context.xml /usr/local/tomcat/webapps/manager/META-INF
COPY tomcat_settings/tomcat-users.xml /usr/local/tomcat/conf

COPY target/Worker.war /usr/local/tomcat/webapps/Worker.war

EXPOSE 8080

CMD ["/usr/local/tomcat/bin/catalina.sh", "run"]