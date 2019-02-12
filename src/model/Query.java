/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import static model.DBConnection.conn;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 *
 * @author mian
 */
public class Query {
    
    private static String query;
    private static Statement stmt;
    private static ResultSet result;
    
    public static void makeQuery(String q)
    {
        query = q;
        
        try
        {
            //create statement obj
            stmt = conn.createStatement();
            
            //Determine query execution
            if(query.toLowerCase().startsWith("select"))
                result = stmt.executeQuery(query);
            
            if(query.toLowerCase().startsWith("delete") || query.toLowerCase().startsWith("insert")|| query.toLowerCase().startsWith("update"))
              
                stmt.executeUpdate(query);
        }
        catch(Exception ex)
        {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    public static ResultSet getResult()
        {
            return result;
        }
}
