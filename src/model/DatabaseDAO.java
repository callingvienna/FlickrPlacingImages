package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseDAO {
	public Connection con = null;
	public Statement st = null;
	public ResultSet rs = null;
	/**
	 * Access to the Database
	 */
	public ResultSet accessDatabaseSelect( String statement) {
		    
	        
        		String url = "jdbc:mysql://localhost:8889/pae_datastore";
	        String user = "XXXX";
	        String password = "XXXX";

	        try {
	        		//DriverManager.registerDriver(new com.mysql.jdbc.Driver ());
	        		Class.forName("com.mysql.jdbc.Driver").newInstance();
	        		con = DriverManager.getConnection(url, user, password);
	            st = con.createStatement();
	            rs = st.executeQuery(statement);

	        } catch (SQLException ex) {
	        		System.out.println(ex);

	        } catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
	           /* try {
	                
	                if (st != null) {
	                    st.close();
	                }
	                if (con != null) {
	                    con.close();
	                }
	             
	            } catch (SQLException ex) {
	            	System.out.println(ex);
	            }*/
	        }
	        return rs;
	}
	
	
	/**
	 * Access to the Database
	 */
	public void accessDatabaseManipulation( String statement) {
		 Connection con = null;
	        Statement st = null;
	        
	        String url = "jdbc:mysql://localhost:8889/pae_datastore";
	        String user = "XXXX";
	        String password = "XXXX";

	        try {
	            con = DriverManager.getConnection(url, user, password);
	            st = con.createStatement();
	            st.executeUpdate(statement);


	        } catch (SQLException ex) {
	        		System.out.println(ex);
	        } finally {
		           try {
		                
		                if (st != null) {
		                    st.close();
		                }
		                if (con != null) {
		                    con.close();
		                }
		             
		            } catch (SQLException ex) {
		            	System.out.println(ex);
		            }
		     }
	}
}
