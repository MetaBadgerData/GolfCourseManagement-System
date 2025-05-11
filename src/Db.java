
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class Db {

	/*
	 * load SQL driver (JDBC: Java Db Connector/ODBC)
	 * - add to build path
	 * 
	 * set up our database (script)
	 * 
	 * connect to the database
	 * 
	 * insert/modify/delete data (Java)
	 * 
	 * query data (Java)
	 * 
	 * disconnect from the database
	 * 
	 */

	 /* SQLite connection to a local database */
//	private String url = "jdbc:sqlite:/Users/asauppe/Documents/teaching/cs364/Company.db";

	/* MySQL connection to a local database */
	private String url = "jdbc:mysql://138.49.184.47:3306/dyer7427_GolfCourseManagementSystem?user=white5664&password=";


	//private String url = "jdbc:mysql://138.49.184.47:3306/ssfoley_Company_JDBC?user=ssfoley-student&password="; // password added in constructor

	private Connection connection;
	
	public Db() {
		String password = "tgu$K+wyeCG7P-fq"; //TODO: set this to your password
		String encodedPassword; 
		try {
			encodedPassword = URLEncoder.encode(password, StandardCharsets.UTF_8.toString());
			url = url + encodedPassword;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	public void connect() {
		try {
			connection = DriverManager.getConnection(url);
		} catch (SQLException e) {
			System.out.println("Cannot connect!");
			System.out.println(e);
		}
	}
	public Connection getConnection() {
		return connection;
	}
	
	public void disconnect() {
		try {
			connection.close();
		} catch (SQLException e) {
			System.out.println("Cannot disconnect!");
		}
	}
	
	public ResultSet runQuery(String query) throws SQLException {
		PreparedStatement stmt = connection.prepareStatement(query);
		ResultSet results = stmt.executeQuery();
		return results;
	}
	
	public ResultSet MenuLookup(String item_name) throws SQLException {
		String query = "SELECT * FROM Menu WHERE item_name = ?";
		PreparedStatement stmt = connection.prepareStatement(query);
		stmt.setString(1, item_name);
		ResultSet results = stmt.executeQuery();
		return results;
	}
	
	public void insertMenu(Menu e) throws SQLException {
		String sql = "INSERT INTO Menu (menu_item_id, item_name, category, price) VALUES (?, ?, ?, ?)";
		PreparedStatement stmt = connection.prepareStatement(sql);
		stmt.setInt(1, e.getMenu_item_id());
		stmt.setString(2, e.getItem_name());
		stmt.setString(3, e.getCategory());
		stmt.setDouble(4, e.getPrice());
		int numRowsAffected = stmt.executeUpdate();
		System.out.println("Number of rows affected: " + numRowsAffected);
	}

	public void insertReservation(Reservations e) throws SQLException {
		String sql = "INSERT INTO Reservations (GolferID, CourseID, Date, Time, TotalCost) VALUES (?, ?, ?, ?, ?)";
		PreparedStatement stmt = connection.prepareStatement(sql);
		stmt.setInt(1, e.getGolferId());
		stmt.setInt(2, e.getCourseId());
		stmt.setString(3, e.getDate());
		stmt.setString(4, e.getTime());
		stmt.setDouble(4, e.getTotalCost());

		int numRowsAffected = stmt.executeUpdate();
		System.out.println("Number of rows affected: " + numRowsAffected);
	}
	
	public void updateItemPrice(Menu e, double price) throws SQLException {
		String sql = "UPDATE Menu SET Price = ? WHERE menu_item_id = ?";
		PreparedStatement stmt = connection.prepareStatement(sql);
		stmt.setDouble(1, price);
		stmt.setInt(2, e.getMenu_item_id());
		stmt.executeUpdate();
		e.setPrice(price);
	}
	
	public boolean deleteMenu(Menu e) throws SQLException {
		String sql = "DELETE FROM Menu WHERE menu_item_id = ?";
		PreparedStatement stmt = connection.prepareStatement(sql);
		stmt.setInt(1, e.getMenu_item_id());
		int numRowsAffected = stmt.executeUpdate();
		return numRowsAffected > 0;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
