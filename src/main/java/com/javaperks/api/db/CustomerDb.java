package com.javaperks.api.db;

import java.util.ArrayList;
import java.util.List;
import java.lang.Integer;

import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.javaperks.api.ISecretsManager;
import com.bettercloud.vault.*;
import com.bettercloud.vault.api.database.DatabaseCredential;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomerDb implements ICustomerDb
{
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomerDb.class);

    private Vault vault;
    private String vaultaddr;
    private String vaulttoken;
    private String connstr;
    private String dbuser;
    private String dbpass;
    private String dbserver;
    private String database;
    private VaultConfig vaultConfig;

    public CustomerDb(ISecretsManager config) throws Exception
    {
        this.vaultaddr = config.getSecretsUrl();
        this.vaulttoken = config.getSecretsToken();

        try
        {
            this.vaultConfig = new VaultConfig()
                .address(this.vaultaddr)
                .token(this.vaulttoken)
                .build();
        } catch ( VaultException ex) {
            throw new Exception("Could not initialize Vault session.");
        }

        this.vault = InitVault();
        this.dbserver = this.vault.logical().read("secret/dbhost").getData().get("address");
        this.database = this.vault.logical().read("secret/dbhost").getData().get("database");

        this.connstr = "jdbc:mysql://" + this.dbserver + "/" + this.database + "?useSSL=false";
    }

    private Vault InitVault() throws VaultException {
        Vault vault = new Vault(this.vaultConfig);

        return vault;
    }

    public List<Customer> getCustomers() {
        LOGGER.info("Get all customers");
        List<Customer> customers = new ArrayList<Customer>();

        try (Connection cn = DriverManager.getConnection(this.connstr, this.dbuser, this.dbpass))
        {
            String sql = "select * from customer_main";
            Statement s = cn.createStatement();
            ResultSet rs = s.executeQuery(sql);

            while(rs.next()) {
                customers.add(new Customer(rs.getInt("custid"), rs.getString("custno"), rs.getString("firstname"), rs.getString("lastname"), rs.getString("email"), rs.getString("dob"), rs.getString("ssn"), rs.getDate("datecreated")));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        for (Customer c : customers) {
            try (Connection cn = DriverManager.getConnection(this.connstr, this.dbuser, this.dbpass))
            {
                String sql = "select * from customer_addresses where custid = " + Integer.toString(c.getCustId());
                Statement s = cn.createStatement();
                ResultSet rs = s.executeQuery(sql);
                List<Address> list = new ArrayList<Address>();
    
                while(rs.next()) {
                    Address a = new Address(
                        rs.getInt("addrid"),
                        rs.getInt("custid"),
                        rs.getString("contact"),
                        rs.getString("address1"),
                        rs.getString("address2"),
                        rs.getString("city"),
                        rs.getString("state"),
                        rs.getString("zip"),
                        rs.getString("phone"),
                        rs.getString("addrtype")
                    );
                    
                    list.add(a);
                }

                c.setAddresses(list);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        return customers;
    }

    public Address getAddressById(int addrid) {
        Address address = null;

        try (Connection cn = DriverManager.getConnection(this.connstr, this.dbuser, this.dbpass))
        {
            String sql = "select * from customer_addresses where addrid = " + Integer.toString(addrid);
            Statement s = cn.createStatement();
            ResultSet rs = s.executeQuery(sql);

            while(rs.next()) {
                address = new Address(
                    rs.getInt("addrid"),
                    rs.getInt("custid"),
                    rs.getString("contact"),
                    rs.getString("address1"),
                    rs.getString("address2"),
                    rs.getString("city"),
                    rs.getString("state"),
                    rs.getString("zip"),
                    rs.getString("phone"),
                    rs.getString("addrtype")
                );
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return address;
    }

    public Status updatePersonalInfo(Customer customer) {
        try (Connection cn = DriverManager.getConnection(this.connstr, this.dbuser, this.dbpass))
        {
            String sql = "update customer_main set " +
                "firstname = '" + customer.getFirstName().replace("'", "''") + "', " +
                "lastname = '" + customer.getLastName().replace("'", "''") + "', " +
                "dob = '" + customer.getDOB().replace("'", "''") + "', " +
                "ssn = '" + customer.getSSN().replace("'", "''") + "' " +
            "where custid = " + Integer.toString(customer.getCustId());
            Statement s = cn.createStatement();
            s.executeUpdate(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        Status status = new Status(true, "Success!");
        return status;
    }

    public Status updateEmailAddress(Customer customer) {
        try (Connection cn = DriverManager.getConnection(this.connstr, this.dbuser, this.dbpass))
        {
            String sql = "update customer_main set " +
                "email = '" + customer.getEmail().replace("'", "''") + "' " +
            "where custid = " + Integer.toString(customer.getCustId());
            Statement s = cn.createStatement();
            s.executeUpdate(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        Status status = new Status(true, "Success!");
        return status;
    }

    public Status updateAddress(Address address) {
        try (Connection cn = DriverManager.getConnection(this.connstr, this.dbuser, this.dbpass))
        {
            String sql = "update customer_addresses set " +
                "contact = '" + address.getContact().replace("'", "''") + "', " +
                "address1 = '" + address.getAddress1().replace("'", "''") + "', " +
                "address2 = '" + address.getAddress2().replace("'", "''") + "', " +
                "city = '" + address.getCity().replace("'", "''") + "', " +
                "state = '" + address.getState().replace("'", "''") + "', " +
                "zip = '" + address.getZip().replace("'", "''") + "', " +
                "phone = '" + address.getPhone().replace("'", "''") + "' " +
            "where custid = " + Integer.toString(address.getCustId()) + " " +
                "and addrtype = '" + address.getAddrType() + "'";
            Statement s = cn.createStatement();
            s.executeUpdate(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        Status status = new Status(true, "Success!");
        return status;
    }

    public Customer getCustomerById(String id) {
        LOGGER.info("Get a customer by custid");
        Vault v;

        try {
            v = InitVault();
        } catch (VaultException ex) {
            LOGGER.error("Error: " + ex.getMessage());
            return null;
        }

        try {
            DatabaseCredential creds = v.database("custdbcreds").creds("cust-api-role").getCredential();
            this.dbuser = creds.getUsername();
            this.dbpass = creds.getPassword();
        } catch (VaultException ex) {
            LOGGER.error("Error: " + ex.getMessage());
        }

        LOGGER.info("Username: " + this.dbuser);
        LOGGER.info("Password: " + this.dbpass);

        Customer customer = null;

        try (Connection cn = DriverManager.getConnection(this.connstr, this.dbuser, this.dbpass))
        {
            String sql = "select * from customer_main where custno = '" + id + "'";
            Statement s = cn.createStatement();
            ResultSet rs = s.executeQuery(sql);

            while(rs.next()) {
                customer = new Customer(rs.getInt("custid"), rs.getString("custno"), rs.getString("firstname"), rs.getString("lastname"), rs.getString("email"), rs.getString("dob"), rs.getString("ssn"), rs.getDate("datecreated"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        try (Connection cn = DriverManager.getConnection(this.connstr, this.dbuser, this.dbpass))
        {
            String sql = "select * from customer_addresses where custid = " + Integer.toString(customer.getCustId());
            Statement s = cn.createStatement();
            ResultSet rs = s.executeQuery(sql);
            List<Address> list = new ArrayList<Address>();

            while(rs.next()) {
                Address a = new Address(
                    rs.getInt("addrid"),
                    rs.getInt("custid"),
                    rs.getString("contact"),
                    rs.getString("address1"),
                    rs.getString("address2"),
                    rs.getString("city"),
                    rs.getString("state"),
                    rs.getString("zip"),
                    rs.getString("phone"),
                    rs.getString("addrtype")
                );
                
                list.add(a);
            }

            customer.setAddresses(list);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return customer;
    }
}
