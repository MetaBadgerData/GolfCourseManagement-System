import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Golf {
		public static void main(String[] args) {

		Db db = new Db();
		/* STEP THREE: See if the connection worked. */
		db.connect();

		try {
			
			//Menu e = new Menu(16, "Cheese Curds", "Snacks", 10.99);
			//Menu d = new Menu(17, "Jalapeno Popper", "Snacks", 12.99);
			//Reservations e = new Reservations(15, 3, "4-10-25", "8:30", 120.00);
			String csvFile = "reservations.csv";  // Path to your CSV file
            List<String[]> csvData = readCSV(csvFile);
            for (int i = 1; i < csvData.size(); i++) { // Skip header row
				// Assuming CSV data has columns: id, num_guests, date, time, amount
				// Adjust the indices based on your CSV structure
				// For example, if your CSV has a header row, start from index 1
				// and parse the data accordingly.
				
				// Example parsing logic (adjust based on your CSV structure)
				String[] row = csvData.get(i);
                // Assuming CSV data has columns: id, num_guests, date, time, amount
                int id = Integer.parseInt(row[0]);
                int numGuests = Integer.parseInt(row[1]);
                String date = row[2];
                String time = row[3];
                double amount = Double.parseDouble(row[4]);
                
                // Create a Reservations object and insert into the database
                Reservations reservation = new Reservations(id, numGuests, date, time, amount);
                db.insertReservation(reservation);
			}
			/* STEP FOUR: Run a select and get the results */
			
			// String query = "SELECT * FROM MenudeleteMenu";
			// ResultSet results = db.runQuery(query);
			 
			// ArrayList<MenudeleteMenu> lst = new ArrayList<>();
			
			// while(results.next()) {
			// 	String ssn = results.getString("SSN");
			// 	double salary = results.getDouble("Salary");
			// 	String firstName = results.getString("FirstName");
			// 	String middleName = results.getString("MiddleName");
			// 	String lastName = results.getString("LastName");
				
			// 	MenudeleteMenu e = new MenudeleteMenu(ssn, salary, firstName, middleName, lastName);
				
			// 	lst.add(e);
			// }
			
			// for(MenudeleteMenu e : lst) {
			// 	System.out.println(e);
			// }
			  
			/* END STEP FOUR */

			/* STEP FIVE: Encapsulation and prepareStatements */
			///* 
			// ResultSet results = db.MenudeleteMenuLookup("148");
			// if(results.next()) {
			// 	String ssn = results.getString("SSN");
			// 	double salary = results.getDouble("Salary");
			// 	String firstName = results.getString("FirstName");
			// 	String middleName = results.getString("MiddleName");
			// 	String lastName = results.getString("LastName");
				
			// 	MenudeleteMenu e = new MenudeleteMenu(ssn, salary, firstName, middleName, lastName);
				
			// 	System.out.println(e.toString());
			// }
			 //*/
			/* END STEP FIVE */


			/* STEP SIX: Database modification */
			 
			// MenudeleteMenu e = new MenudeleteMenu("222-22-2222", 60000.00, "Edsger", "W.", "Dijkstra");
			// db.insertMenudeleteMenu(e);
			
			// System.out.println();
			
			// db.updateMenudeleteMenuSalary(e, 65000.00);
			 
			// boolean result = db.deleteMenu(e);
			// System.out.println(result);
			//  Menu e = new Menu(16, "Cheese Curds", "Snacks", 10.99);
			//  db.insertMenudeleteMenu(e);
			//  Menu d = new Menu(17, "Jalapeno Popper", "Snacks", 12.99);
			// db.insertMenudeleteMenu(d);
			//db.updateMenudeleteMenuSalary(d, 1000);


			// ResultSet results = db.MenudeleteMenuLookup("123-45-6789");
			// MenudeleteMenu f = null;
			// if(results.next()) {
			// 		String ssn = results.getString("SSN");
			// 		double salary = results.getDouble("Salary");
			// 		String firstName = results.getString("FirstName");
			// 		String middleName = results.getString("MiddleName");
			// 		String lastName = results.getString("LastName");
					
			// 		f = new MenudeleteMenu(ssn, salary, firstName, middleName, lastName);
					
			// 		System.out.println(e.toString());
			// 	}
			// db.updateMenudeleteMenuSalary(f, 123000);
			// db.deleteMenu(e);
			// db.deleteMenu(d);
			// db.insertMenu(e);
			// db.insertMenu(d);


			System.out.println("Menu Items successfully inserted!");
			/* END STEP SIX */
		} catch(SQLException e) {
			System.out.println("Something went wrong!");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Something went wrong with SQL!");
			e.printStackTrace();
		}
		
		System.out.println("Successfully connected!");
		
		db.disconnect();
		
	}
	public static List<String[]> readCSV(String filePath) throws IOException {
        List<String[]> data = new ArrayList<>();
        
        // Use Files.readAllLines to read the CSV file line by line
        List<String> lines = Files.readAllLines(Paths.get(filePath));
        
        // Iterate through the lines and split them by commas to get individual fields
        for (String line : lines) {
            String[] fields = line.split(",");
            data.add(fields);
        }
        return data;
    }

}