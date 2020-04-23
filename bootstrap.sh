#!/bin/bash

export KUBE_TOKEN=$(cat /var/run/secrets/kubernetes.io/serviceaccount/token)
export VAULT_TOKEN=$(curl --request POST \
  --data "{\"jwt\": \"$KUBE_TOKEN\", \"role\": \"cust-api\"}" \
  $VAULT_ADDR/v1/auth/kubernetes/login | jq -r .auth.client_token) && echo $VAULT_TOKEN

echo "vaultAddress: $VAULT_ADDR" >>/app/config.yml
echo "vaultToken: $VAULT_TOKEN" >>/app/config.yml
