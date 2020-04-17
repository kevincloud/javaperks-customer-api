#!/bin/bash

apt-get update
apt-get install -y openjdk-11-jre openjdk-11-jdk jq curl unzip

export KUBE_TOKEN=$(cat /var/run/secrets/kubernetes.io/serviceaccount/token)
export VAULT_TOKEN=$(curl --request POST \
  --data "{\"jwt\": \"$KUBE_TOKEN\", \"role\": \"cust-api\"}" \
  $VAULT_ADDR/v1/auth/kubernetes/login | jq -r .auth.client_token) && echo $VAULT_TOKEN

java -jar /app/javaperks-customer-api.jar server /app/config.yml
