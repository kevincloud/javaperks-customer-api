FROM ubuntu:18.04
  
LABEL Kevin Cochran "kcochran@hashicorp.com"

RUN apt-get update
RUN apt-get install -y openjdk-11-jre openjdk-11-jdk
RUN mkdir /app
ADD target/javaperks-customer-api-0.2.8.jar /app/javaperks-customer-api.jar
ADD config.yml /app/config.yml
ADD bootstrap.sh /app/bootstrap.sh
RUN chmod +x /app/bootstrap.sh
RUN . /app/bootstrap.sh

WORKDIR /app

ENTRYPOINT [ "java", "-jar", "/app/javaperks-customer-api.jar", "server", "/app/config.yml" ]
