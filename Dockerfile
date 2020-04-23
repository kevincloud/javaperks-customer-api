FROM ubuntu:18.04
  
LABEL Kevin Cochran "kcochran@hashicorp.com"

RUN apt-get update
RUN apt-get install -y openjdk-11-jre openjdk-11-jdk curl jq
RUN mkdir /app
ADD target/javaperks-customer-api-0.2.8.jar /app/javaperks-customer-api.jar
ADD config.yml /app/config.yml
ADD bootstrap.sh /app/bootstrap.sh
RUN chmod +x /app/bootstrap.sh

WORKDIR /app

ENTRYPOINT [ "/app/bootstrap.sh" ]
