FROM ubuntu:18.04
  
LABEL Kevin Cochran "kcochran@hashicorp.com"

RUN apt-get update && apt-get upgrade -y
RUN apt-get install -y openjdk-11-jre openjdk-11-jdk
RUN mkdir /app
ADD target/javaperks-customer-api-*.jar /app/javaperks-customer-api.jar

WORKDIR /app

ENTRYPOINT [ "java", "-jar", "javaperks-customer-api.jar", "server config.yml" ]
