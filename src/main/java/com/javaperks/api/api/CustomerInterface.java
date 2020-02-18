package com.javaperks.api.api;

// import javax.validation.ConstraintViolation;
// import javax.validation.Validator;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
// import javax.ws.rs.POST;
// import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.javaperks.api.db.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Produces(MediaType.APPLICATION_JSON)
@Path("/customers")
public class CustomerInterface
{
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomerInterface.class);

    private ICustomerDb cdb;

    public CustomerInterface(ICustomerDb database) throws Exception {
        this.cdb = database;
    }

    // @GET
    // public Response getCustomers()
    // {
    //     return Response.ok(this.cdb.getCustomers()).build();
    // }

    @GET
    @Path("/{id}")
    public Response getCustomerById(@PathParam("id") String id) {
        LOGGER.info("getCustomerById: id = " + id);
        return Response.ok(this.cdb.getCustomerById(id)).build();
    }

    @GET
    @Path("/address/{id}")
    public Response getAddressById(@PathParam("id") int id) {
        LOGGER.info("getAddressById: id = " + id);
        return Response.ok(this.cdb.getAddressById(id)).build();
    }

    @PUT
    @Path("/info/{id}")
    public Response updatePersonalInfo(Customer customer) {
        LOGGER.info("updatePersonalInfo: customer = " + customer.toString());
        return Response.ok(this.cdb.updatePersonalInfo(customer)).build();
    }

    @PUT
    @Path("/email/{id}")
    public Response updateEmailAddress(Customer customer) {
        LOGGER.info("updateEmailAddress: customer = " + customer.toString());
        return Response.ok(this.cdb.updateEmailAddress(customer)).build();
    }

    @PUT
    @Path("/address/{id}")
    public Response updateAddress(Address address) {
        LOGGER.info("updateAddress: address = " + address.toString());
        return Response.ok(this.cdb.updateAddress(address)).build();
    }
}