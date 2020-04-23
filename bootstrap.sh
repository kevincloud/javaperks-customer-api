#!/bin/bash

export KUBE_TOKEN=$(cat /var/run/secrets/kubernetes.io/serviceaccount/token)
export VAULT_TOKEN=$(curl --request POST \
  --data "{\"jwt\": \"$KUBE_TOKEN\", \"role\": \"cust-api\"}" \
  $VAULT_ADDR/v1/auth/kubernetes/login | jq -r .auth.client_token) && echo $VAULT_TOKEN

mv /app/config.yml /app/xconfig.yml
echo "vaultAddress: $VAULT_ADDR" >>/app/xconfig.yml
echo "vaultToken: $VAULT_TOKEN" >>/app/xconfig.yml
cat /app/xconfig.yml | sed '/^$/d' >/app/config.yml
rm /app/xconfig.yml
