package com.javaperks.api;

import io.dropwizard.Application;
import io.dropwizard.jersey.jackson.JsonProcessingExceptionMapper;
// import io.dropwizard.Configuration;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
// import io.dropwizard.client.JerseyClientBuilder;
// import javax.ws.rs.client.Client;

import com.javaperks.api.api.CustomerInterface;
import com.javaperks.api.api.HealthCheckInterface;
import com.javaperks.api.api.PaymentInterface;
import com.javaperks.api.db.*;
import com.javaperks.api.api.InvoiceInterface;
import com.javaperks.api.health.AppHealthCheck;

// import javax.ws.rs.client.Client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomerApiApplication extends Application<CustomerApiConfiguration> {
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomerApiApplication.class);

    public static void main(final String[] args) throws Exception {
        new CustomerApiApplication().run(args);
    }

    @Override
    public String getName() {
        return "CustomerApi";
    }

    @Override
    public void initialize(Bootstrap<CustomerApiConfiguration> bootstrap) {
    }

    @Override
    public void run(CustomerApiConfiguration c, Environment e) throws Exception {
        LOGGER.info("Registering API Resources");

        String vaultAddress = c.getVaultAddress(); //System.getenv("VAULT_ADDR");
        String vaultToken = c.getVaultToken(); //System.getenv("VAULT_TOKEN");

        VaultManager vaultconfig = new VaultManager(vaultAddress, vaultToken);
        ICustomerDb cdb = new CustomerDb(vaultconfig);
        e.healthChecks().register("ApiHealthCheck", new AppHealthCheck());

        final CustomerInterface ci = new CustomerInterface(cdb);
        final PaymentInterface pi = new PaymentInterface(e.getValidator(), vaultAddress, vaultToken);
        final InvoiceInterface ii = new InvoiceInterface(e.getValidator(), vaultAddress, vaultToken);
        final HealthCheckInterface hci = new HealthCheckInterface(e.healthChecks());

        e.jersey().register(new JsonProcessingExceptionMapper(true));
        e.jersey().register(ci);
        e.jersey().register(pi);
        e.jersey().register(ii);
        e.jersey().register(hci);
    }

}
