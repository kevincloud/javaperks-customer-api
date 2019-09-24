package com.javaperks.api;

public class VaultManager implements ISecretsManager
{
    private String vaultAddress;
    private String vaultToken;

    public VaultManager(String vaultAddress, String vaultToken) {
        this.vaultAddress = vaultAddress;
        this.vaultToken = vaultToken;
    }

    public String getSecretsUrl() {
        return this.vaultAddress;
    }

    public String getSecretsToken() {
        return this.vaultToken;
    }

    public String getVaultAddress() {
        return this.vaultAddress;
    }

    public void setVaultAddress(String address) {
        this.vaultAddress = address;
    }

    public String getVaultToken() {
        return this.vaultToken;
    }

    public void setVaultToken(String token) {
        this.vaultToken = token;
    }
}