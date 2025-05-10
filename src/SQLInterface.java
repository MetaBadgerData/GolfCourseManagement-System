import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.TableView.TableRow;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class SQLInterface extends JFrame {
    private JTable resultTable;
    private JLabel statusLabel;
    private JTextField searchField;
    private JButton addReservationButton;
    private JButton removeButton;
    private JButton updateButton;
    private JButton totalReservationsButton;
    private JButton habitsButton;
    private JButton lowSpendingButton;
    private JButton searchButton;
    private JButton menuButton;
    private JButton courseRevenueButton;
    private JButton providesButton;
    private JButton golfersButton;
    private JButton buysButton;
    private JButton staffButton;
    private JButton reservationsButton;
    private JButton worstBeverageButton;
    private JButton inactiveGolferButton;
    private JButton mostPopFoodButton;
    private JButton avgReservationCountButton;
    private JButton reservationLeaderButton;
    private JButton home;
    private TableRowSorter<DefaultTableModel> sorter;

    private JPanel queryButtonsPanel;
    private JPanel topPanel;

    private Db db;

    private static final String INACTIVE_GOLFERS_QUERY = "SELECT Golfers.GolferID, Golfers.FirstName, Golfers.LastName, Golfers.MembershipStatus "
            +
            "FROM Golfers " +
            "WHERE Golfers.golferID NOT IN (SELECT GolferID FROM Reservations)";

    private static final String COURSE_REVENUE_QUERY = "SELECT GolfCourses.courseId, GolfCourses.numOfHoles, SUM(Reservations.totalCost) AS totalRevenue "
            +
            "FROM GolfCourses " +
            "JOIN Reservations ON GolfCourses.courseId = Reservations.courseId " +
            "GROUP BY GolfCourses.courseId, GolfCourses.numOfHoles " +
            "ORDER BY totalRevenue DESC";

    private static final String WORST_BEVERAGE_QUERY = "SELECT Menu.MenuItemID, Menu.item_name, COUNT(Buys.MenuItemID) AS purchaseCount "
            +
            "FROM Menu " +
            "JOIN Buys ON Menu.MenuItemID = Buys.MenuItemID " +
            "WHERE Menu.category = 'Drinks' " +
            "GROUP BY Menu.MenuItemID, Menu.item_name " +
            "ORDER BY purchaseCount ASC " +
            "LIMIT 1";
    private static final String TOTAL_RESERVATIONS_QUERY = " SELECT CourseID, COUNT(*) AS totalReservations " +
            "FROM Reservations " +
            "GROUP BY CourseID " +
            "ORDER BY totalReservations DESC " + 
            "LIMIT 5";
    
    private static final String TOTAL_SPENDING_RESERVATION_QUERY = "SELECT  g.GolferID, g.FirstName, g.LastName, COUNT(r.ReservationID) AS NumReservations, SUM(r.TotalCost) AS TotalSpent " + 
            "FROM Golfers g " +
            "LEFT JOIN Reservations r ON g.GolferID = r.GolferID " +
            "GROUP BY g.GolferID, g.FirstName, g.LastName " +
            "ORDER BY TotalSpent DESC;";

    private static final String FREE_LOADER_QUERY = "SELECT g.GolferID, g.FirstName, g.LastName "
            +
            "FROM Golfers g " +
            "WHERE g.GolferID IN (SELECT DISTINCT r.GolferID FROM Reservations r WHERE r.GolferID IS NOT NULL) " +
            "AND g.GolferID NOT IN (SELECT DISTINCT b.GolferID FROM Buys b)";

    private static final String MOST_POPULAR_FOOD_QUERY = "SELECT Menu.item_name, COUNT(*) AS PurchaseCount " +
            "FROM Buys JOIN Menu ON Buys.MenuItemID = Menu.MenuItemID " +
            "WHERE Menu.MenuItemID " +
            "GROUP BY Menu.item_name " +
            "ORDER BY PurchaseCount DESC " +
            "LIMIT 1";
    
    private static final String AVG_RESERVATION_COUNT_QUERY = "SELECT AVG(ReservationCount) AS AvgReservationsPerGolfer " +
            "FROM ( Select g.GolferID, COUNT(r.ReservationID) AS ReservationCount " +
            "FROM Golfers g " +
            "LEFT JOIN Reservations r ON g.GolferID = r.GolferID " +
            "GROUP BY g.GolferID " + 
            ") AS GolferReservationCounts;";

    private static final String RESERVATION_LEADER_QUERY = "SELECT g.GolferID, g.FirstName, g.LastName, COUNT(r.ReservationID) AS ReservationCount " +
            "FROM Golfers g " +
            "LEFT JOIN Reservations r ON g.GolferID = r.GolferID " + 
            "GROUP BY g.GolferID, g.FirstName, g.LastName " +
            "HAVING COUNT(r.ReservationID) > ( " + 
            "SELECT AVG(ReservationCount) AS AvgReservationsPerGolfer " +
            "FROM ( Select g.GolferID, COUNT(r.ReservationID) AS ReservationCount " +
            "FROM Golfers g " +
            "LEFT JOIN Reservations r ON g.GolferID = r.GolferID " +
            "GROUP BY g.GolferID " +
            ") AS GolferReservationCounts " +
            ");";


    public SQLInterface() {
        setTitle("MySQL SQL Interface");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        initUI();
        initDatabase();
    }

    private void initUI() {
        resultTable = new JTable();
        statusLabel = new JLabel("Status: Ready"); 
        totalReservationsButton = new JButton("Total Reservations");
        habitsButton = new JButton("Habits");
        lowSpendingButton = new JButton("Low_spending_poopy_golfers");
        golfersButton = new JButton("Golfers");
        buysButton = new JButton("Buys");
        reservationsButton = new JButton("Reservations");
        staffButton = new JButton("Staff");
        providesButton = new JButton("Provides");
        courseRevenueButton = new JButton("Show Course Revenue");
        mostPopFoodButton = new JButton("Most Popular Food");
        worstBeverageButton = new JButton("Find Worst Beverage");
        inactiveGolferButton = new JButton("Show Inactive Golfers");
        avgReservationCountButton = new JButton("Avg Reservation Count");
        reservationLeaderButton = new JButton("Reservation Leader");
        menuButton = new JButton("Menu");
        home = new JButton("Home");

        searchField = new JTextField(10);
        searchButton = new JButton("Search");
        addReservationButton = new JButton("Add Reservation");
        removeButton = new JButton("Remove");
        updateButton = new JButton("Update");
            
        searchButton.addActionListener(e -> {
            String searchText = searchField.getText();
            if (sorter != null && searchText != null && !searchText.trim().isEmpty()) {
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText));
                statusLabel.setText("Status: Filter applied");
            } else if (sorter != null) {
                sorter.setRowFilter(null);
                statusLabel.setText("Status: Filter cleared");
            }
        });

        addReservationButton.addActionListener(e -> {
            // Implement add functionality
            //statusLabel.setText("Status: Add button clicked");
            new ReservationForm(this, db, () -> executeQuery("SELECT * FROM Reservations")).setVisible(true);
        });
        removeButton.addActionListener(e -> {
            // Implement remove functionality
            statusLabel.setText("Status: Remove button clicked");
        });
        updateButton.addActionListener(e -> {
            // Implement update functionality
            statusLabel.setText("Status: Update button clicked");
        });

        // tableButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        // String[] mainTable = {"Golfers", "GolfCourses", "Reservations", "Menu", "Buys"};
        // for (String tableName : mainTable) {
        //     JButton tableButton = new JButton("Show " + tableName);
        //     tableButton.addActionListener(e -> {
        //         queryArea.setText("SELECT * FROM " + tableName);
        //         executeQuery(new ActionEvent(e.getSource(), e.getID(), "Execute"));
        //     });
        //     tableButtonsPanel.add(tableButton);
        // }

        JScrollPane tableScroll = new JScrollPane(resultTable);


        golfersButton.addActionListener(e -> {
            queryButtonsPanel.setVisible(false);
            executeQuery("SELECT * FROM Golfers");
        });

        staffButton.addActionListener(e -> {
            queryButtonsPanel.setVisible(false);
            executeQuery("SELECT * FROM Staff");
        });

        buysButton.addActionListener(e -> {
            queryButtonsPanel.setVisible(false);
            executeQuery("SELECT * FROM Buys");
        });

        providesButton.addActionListener(e -> {
            queryButtonsPanel.setVisible(false);
            executeQuery("SELECT * FROM Provides");
        });

        reservationsButton.addActionListener(e -> {
            queryButtonsPanel.setVisible(false);
            executeQuery("SELECT * FROM Reservations");
        });

        menuButton.addActionListener(e -> {
            queryButtonsPanel.setVisible(false);
            executeQuery("SELECT * FROM Menu");
        });

        avgReservationCountButton.addActionListener(e -> { 
            executeQuery(AVG_RESERVATION_COUNT_QUERY);
        });

        // {
        //     queryButtonsPanel.setVisible(false);
        //     statusLabel.setText("Status: Menu buttons displayed");
        // });


        lowSpendingButton.addActionListener(e -> {
            executeQuery(FREE_LOADER_QUERY);
        });

        habitsButton.addActionListener(e -> {
            executeQuery(TOTAL_SPENDING_RESERVATION_QUERY);
        });

        totalReservationsButton.addActionListener(e -> {
            executeQuery(TOTAL_RESERVATIONS_QUERY);
        });

        inactiveGolferButton.addActionListener(e -> {
            executeQuery(INACTIVE_GOLFERS_QUERY);
        });

        courseRevenueButton.addActionListener(e -> {
            executeQuery(COURSE_REVENUE_QUERY);
        });

        worstBeverageButton.addActionListener(e -> {
            executeQuery(WORST_BEVERAGE_QUERY);
        });

        mostPopFoodButton.addActionListener(e -> {
            executeQuery(MOST_POPULAR_FOOD_QUERY);
        });

        reservationLeaderButton.addActionListener(e -> {
            executeQuery(RESERVATION_LEADER_QUERY);
        });

        home.addActionListener(e -> {
            queryButtonsPanel.setVisible(true);
            statusLabel.setText("Status: Query buttons displayed");
        });

        queryButtonsPanel = new JPanel(new GridLayout(0, 3, 10, 10));
        queryButtonsPanel.add(inactiveGolferButton);
        queryButtonsPanel.add(courseRevenueButton);
        queryButtonsPanel.add(avgReservationCountButton);
        queryButtonsPanel.add(reservationLeaderButton);
        queryButtonsPanel.add(worstBeverageButton);
        queryButtonsPanel.add(mostPopFoodButton);
        queryButtonsPanel.add(totalReservationsButton);
        queryButtonsPanel.add(lowSpendingButton); 
        queryButtonsPanel.add(habitsButton);
        queryButtonsPanel.setVisible(false);

        //this is the panel for all of the overall buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(menuButton);
        buttonPanel.add(golfersButton);
        buttonPanel.add(staffButton);
        buttonPanel.add(reservationsButton);
        buttonPanel.add(buysButton);
        buttonPanel.add(providesButton);
        buttonPanel.setVisible(true);


        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.Y_AXIS)); 
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search"));
        searchPanel.add(Box.createVerticalStrut(10));
        searchPanel.add(new JLabel("Filter Rows:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(addReservationButton);
        searchPanel.add(removeButton);
        searchPanel.add(updateButton);

        topPanel = new JPanel(new BorderLayout());
        JPanel topButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topButtonPanel.add(home);

        topPanel.add(buttonPanel, BorderLayout.NORTH);
        topPanel.add(topButtonPanel, BorderLayout.SOUTH);
        
        add(topPanel, BorderLayout.NORTH);
        add(queryButtonsPanel, BorderLayout.WEST);
        add(tableScroll, BorderLayout.CENTER);
        add(searchPanel, BorderLayout.EAST);
        add(statusLabel, BorderLayout.SOUTH);
    }

    private void initDatabase() {
        db = new Db();
        db.connect();
        try {
            if (db.getConnection() == null || db.getConnection().isClosed()) {
                showError("Failed to connect to database");
                System.exit(1);
            }
        } catch (SQLException e) {
            showError("Database connection error: " + e.getMessage());
            System.exit(1);
        }
    }

    private void executeQuery(String sql) {
        try {
            ResultSet rs = db.runQuery(sql);
            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();
                
            String[] columnNames = new String[columnCount];
            for (int i = 0; i < columnCount; i++) {
                columnNames[i] = meta.getColumnName(i + 1);
            }

            DefaultTableModel model = new DefaultTableModel(columnNames, 0);
            while (rs.next()) {
                Object[] row = new Object[columnCount];
                for (int i = 0; i < columnCount; i++) {
                    row[i] = rs.getObject(i + 1);
                }
                model.addRow(row);
            }

            resultTable.setModel(model);
            sorter = new TableRowSorter<>(model);
            resultTable.setRowSorter(sorter);
            statusLabel.setText("Status: Query executed successfully.");
        } catch (SQLException e) {
            showError("SQL Error: " + e.getMessage());
        }
    }

    private void showError(String message) {
        statusLabel.setText("Status: " + message);
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SQLInterface gui = new SQLInterface();
            gui.setVisible(true);
        });
    }
}

class ReservationForm extends JDialog {
    private JTextField golferIdField, courseIdField, dateField, timeField, totalCostField;
    private JButton submitButton;
    private Db db;
    private Runnable onSuccess;

    public ReservationForm(JFrame parent, Db db, Runnable onSuccess) {
        super(parent, "Add Reservation", true);
        this.db = db;
        this.onSuccess = onSuccess;
        setLayout(new GridLayout(6, 2, 5, 5));
        setSize(300, 250);
        setLocationRelativeTo(parent);

        golferIdField = new JTextField();
        courseIdField = new JTextField();
        dateField = new JTextField(); // Format: YYYY-MM-DD
        timeField = new JTextField(); // Format: HH:MM:SS
        totalCostField = new JTextField();

        submitButton = new JButton("Submit");

        add(new JLabel("Golfer ID:"));
        add(golferIdField);
        add(new JLabel("Course ID:"));
        add(courseIdField);
        add(new JLabel("Date (YYYY-MM-DD):"));
        add(dateField);
        add(new JLabel("Time (HH:MM:SS):"));
        add(timeField);
        add(new JLabel("Total Cost:"));
        add(totalCostField);
        add(new JLabel());
        add(submitButton);

        submitButton.addActionListener(e -> addReservation());
    }

    private void addReservation() {
        try {
            int golferId = Integer.parseInt(golferIdField.getText().trim());
            int courseId = Integer.parseInt(courseIdField.getText().trim());
            String date = dateField.getText().trim();
            String time = timeField.getText().trim();
            double totalCost = Double.parseDouble(totalCostField.getText().trim());

            String sql = "INSERT INTO Reservations (GolferID, CourseID, Date, Time, TotalCost) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = db.getConnection().prepareStatement(sql);
            stmt.setInt(1, golferId);
            stmt.setInt(2, courseId);
            stmt.setString(3, date);
            stmt.setString(4, time);
            stmt.setDouble(5, totalCost);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Reservation added successfully!");
                onSuccess.run(); // callback to refresh table
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add reservation.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}