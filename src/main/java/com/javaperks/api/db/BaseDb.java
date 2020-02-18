package com.javaperks.api.db;

import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

// import javassist.bytecode.stackmap.BasicBlock.Catch;

// import java.sql.Connection;

public class BaseDb
{
    private String connstr;
    private String dbuser;
    private String dbpass;
    private String dbserver;
    private String database;
    // private Connection cn;

    public BaseDb(String server, String database, String username, String password) {
        this.dbserver = server;
        this.database = database;
        this.dbuser = username;
        this.dbpass = password;

        connstr = "jdbc:mysql://" + this.dbserver + "/" + this.database + "?useSSL=false";
    }

    public ResultSet getResults(String sql) {
        try (Connection cn = DriverManager.getConnection(this.connstr, this.dbuser, this.dbpass))
        {
            Statement s = cn.createStatement();
            ResultSet rs = s.executeQuery(sql);

            return rs;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
