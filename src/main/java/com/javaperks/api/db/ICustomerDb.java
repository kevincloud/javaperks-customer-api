package com.javaperks.api.db;

public interface ICustomerDb
{
    public Customer getCustomerById(String id);
    public Address getAddressById(int addrid);
    public Status updatePersonalInfo(Customer customer);
    public Status updateEmailAddress(Customer customer);
    public Status updateAddress(Address address);
}
