import java.math.BigDecimal;
import java.sql.*;
import java.util.Calendar;

/**
 * @author Szymon Sakowicz
 */
public class Database {
	private static int id = 0;
	Connection c = null;
	Statement stmt = null;
	Calendar cal = Calendar.getInstance();

	Database() {
		if (c == null) {
			// Po³¹czenie z baz¹
			try {
				Class.forName("org.sqlite.JDBC");
				c = DriverManager.getConnection("jdbc:sqlite:database.db");
			} catch (Exception e) {
				System.err.println(e.getClass().getName() + ": " + e.getMessage());
				System.exit(0);
			}
		}
	}

	public void closeConnection() {
		// Zamkniêcie po³¹czenia z baz¹
		try {
			c.close();
		} catch (SQLException e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
	}

	// Stworzenie tabel
	public void createTableUsers() {

		try {
			stmt = c.createStatement();
			String sql = "CREATE TABLE IF NOT EXISTS Users" + "(ID integer PRIMARY KEY AUTOINCREMENT,"
					+ " Name		VARCHAR(20)    	NOT NULL, " + " Surname		VARCHAR(20)    	NOT NULL, "
					+ " Username	VARCHAR(20)     NOT NULL, " + " Password	VARCHAR(50)     NOT NULL)";
			stmt.executeUpdate(sql);
			stmt.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
	}

	public void createTableTransactions() {

		try {
			stmt = c.createStatement();
			String sql = "CREATE TABLE IF NOT EXISTS Transactions" + "(ID INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ " FromWho		INTEGER    		NOT NULL, " + " ToWho		INTEGER    		NOT NULL, "
					+ " Value		DECIMAL    		NOT NULL, " + " Category	VARCHAR(20)     NOT NULL, "
					+ " Caption		VARCHAR(100), " + " Time		DATETIME	NOT NULL)";
			stmt.executeUpdate(sql);
			stmt.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage() + "test");
			System.exit(0);
		}
	}

	// rejestracja
	public int registration(String name, String surname, String username, String password) {
		String checkUsername = "SELECT * FROM Users WHERE Username=?";

		try {
			PreparedStatement checkStatement = c.prepareStatement(checkUsername);
			checkStatement.setString(1, username);
			ResultSet set = checkStatement.executeQuery();
			if (set.next())
				return 2; // zwraca 2 jeœli nazwa u¿ytkownika zajêta
			else {
				String registrationInsert = "INSERT INTO Users (Name,Surname,Username,Password) " + "VALUES (?,?,?,?);";
				PreparedStatement statement = c.prepareStatement(registrationInsert);
				statement.setString(1, name);
				statement.setString(2, surname);
				statement.setString(3, username);
				statement.setString(4, password);
				statement.executeUpdate();
				return 1;
			}
		} catch (SQLException e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			return 0;
		}

	}

	public int login(String username, String password) {
		String login = "SELECT * FROM Users WHERE Username=? and Password=?";
		PreparedStatement statement;
		try {
			statement = c.prepareStatement(login);
			statement.setString(1, username);
			statement.setString(2, password);
			ResultSet set = statement.executeQuery();
			setSessionID(set.getInt(1));
			if (set.next())
				return 1;
			else
				return 0;
		} catch (SQLException e) {
			return 0;
		}
	}

	// Imie i nazwisko po ID u¿ytkownika
	public String nameById(int i) {

		PreparedStatement statement;
		String name = null;
		String surname = null;
		ResultSet set;
		try {
			String nameById = "SELECT * FROM Users WHERE ID=?";
			statement = c.prepareStatement(nameById);
			statement.setInt(1, i);
			set = statement.executeQuery();
			name = set.getString(2);
			surname = set.getString(3);

		} catch (SQLException e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
		return name + " " + surname;
	}

	// Nazwa u¿ytkownika po ID u¿ytkownika
	public String usernameById(int i) {

		PreparedStatement statement;
		String username = null;
		ResultSet set;
		try {
			String nameById = "SELECT * FROM Users WHERE ID=?";
			statement = c.prepareStatement(nameById);
			statement.setInt(1, i);
			set = statement.executeQuery();
			username = set.getString(4);

		} catch (SQLException e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
		return username;
	}

	// id u¿ytkownika po nazwie u¿ytkownika
	public int idByUserame(String username) {

		PreparedStatement statement;
		ResultSet set;
		int id = 0;
		try {
			String idByName = "SELECT * FROM Users WHERE username=?";
			statement = c.prepareStatement(idByName);
			statement.setString(1, username);
			set = statement.executeQuery();
			id = set.getInt(1);

		} catch (SQLException e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			return 0;
		}
		return id;
	}

	// Do³adowanie konta
	public int addFunds(BigDecimal funds, int id) {

		try {
			String registrationInsert = "INSERT INTO Transactions (FromWho, ToWho, Value, Category, Time) "
					+ "VALUES (0,?,?,'Do³adowanie',?);";
			PreparedStatement statement = c.prepareStatement(registrationInsert);
			statement.setInt(1, id);
			statement.setBigDecimal(2, funds);
			statement.setDate(3, new java.sql.Date(cal.getTimeInMillis()));
			statement.executeUpdate();
			return 1;

		} catch (SQLException e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			return 0;
		}

	}

	// Przelewy
	public int sendMoney(int toWho, BigDecimal value, String category, String caption) {
		if (toWho == getSessionID())
			return 2; // zwraca 2 jeœli ktoœ chce wys³aæ przelew do siebie

		try {
			String registrationInsert = "INSERT INTO Transactions (FromWho, ToWho, Value, Category, Caption, Time) "
					+ "VALUES (?,?,?,?,?,?);";
			PreparedStatement statement = c.prepareStatement(registrationInsert);
			statement.setInt(1, getSessionID());
			statement.setInt(2, toWho);
			statement.setBigDecimal(3, value);
			statement.setString(4, category);
			statement.setString(5, caption);
			statement.setDate(6, new java.sql.Date(cal.getTimeInMillis()));
			statement.executeUpdate();
			return 1;

		} catch (SQLException e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			return 0;
		}

	}

	// zwraca saldo
	public BigDecimal balance() {
		try {
			String registrationInsert = "SELECT SUM(Value) FROM Transactions WHERE ToWho=?";
			PreparedStatement statement = c.prepareStatement(registrationInsert);
			ResultSet set;
			statement.setInt(1, getSessionID());
			set = statement.executeQuery();
			BigDecimal balance = set.getBigDecimal(1);
			if (balance == null)
				balance = new BigDecimal(0);
			registrationInsert = "SELECT SUM(Value) FROM Transactions WHERE FromWho=?";
			statement = c.prepareStatement(registrationInsert);
			statement.setInt(1, getSessionID());
			set = statement.executeQuery();
			BigDecimal sent = set.getBigDecimal(1);
			if (sent == null)
				sent = new BigDecimal(0);
			return balance.subtract(sent).setScale(2);

		} catch (SQLException e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			return new BigDecimal(0);
		}

	}

	// zwraca iloœæ transakcji u¿ytkownika
	public int howMuchTransations() {
		PreparedStatement statement;
		ResultSet set;
		try {
			String sumTransactions = "SELECT count(*) From Transactions WHERE (ToWho = ? OR FromWho = ?)";
			statement = c.prepareStatement(sumTransactions);
			statement.setInt(1, id);
			statement.setInt(2, id);
			set = statement.executeQuery();
			int col = set.getInt(1);
			return col;
		} catch (SQLException e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			return 10;
		}

	}

	// Wype³nia tabelê z ostatnimi transakcjami

	public String[][] fillTable() {
		int col = this.howMuchTransations();
		String[][] baza = new String[col][7];
		PreparedStatement statement;
		ResultSet set;
		int id = this.getSessionID();
		try {
			String idByName = "SELECT * FROM Transactions WHERE (ToWho = ? OR FromWho = ?)";
			statement = c.prepareStatement(idByName);
			statement.setInt(1, id);
			statement.setInt(2, id);
			set = statement.executeQuery();
			int i = this.howMuchTransations() - 1;
			while (set.next()) {

				int fromWho = set.getInt(2);
				int toWho = set.getInt(3);
				if (fromWho == id || toWho == id) {
					if (fromWho == id) {
						baza[i][1] = "Wychodzacy";
						baza[i][2] = String.valueOf(this.usernameById(toWho));
					} else if (fromWho == 0) {
						baza[i][1] = "Doladowanie";
						baza[i][2] = "-";
					} else if (toWho == id) {
						baza[i][1] = "Przychodzacy";
						baza[i][2] = String.valueOf(this.usernameById(fromWho));
					}

					BigDecimal value = set.getBigDecimal(4).setScale(2); // ustawia
																			// 2
																			// miejsca
																			// po
																			// przecinku
					if (fromWho == id)
						baza[i][3] = "-" + String.valueOf(value);
					else
						baza[i][3] = String.valueOf(value);
					baza[i][4] = set.getString(5);
					baza[i][5] = set.getString(6);
					baza[i][0] = String.valueOf(set.getDate(7)) + " " + String.valueOf(set.getTime(7));
					i--;
				}
			}
			return baza;

		} catch (SQLException e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			return null;
		}

	}

	// Do raportu pdf: ile kategorii transakcji wychodz¹cych u¿ytkownika
	public int howMuchOutcomesByCategory() {
		PreparedStatement statement;
		ResultSet set;
		try {
			String sumTransactions = "SELECT Category, SUM (Value) FROM Transactions WHERE FromWho = ? GROUP BY Category ORDER BY SUM(Value) DESC";
			statement = c.prepareStatement(sumTransactions);
			statement.setInt(1, id);
			set = statement.executeQuery();
			int col = 0;
			while (set.next())
				col++;
			return col;
		} catch (SQLException e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			return 1;
		}
	}

	// Do raportu pdf: ile kategorii transakcji przychodz¹cych do u¿ytkownika
	public int howMuchIncomesByCategory() {
		PreparedStatement statement;
		ResultSet set;
		try {
			String sumTransactions = "SELECT Category, SUM (Value) FROM Transactions WHERE ToWho = ? GROUP BY Category ORDER BY SUM(Value) DESC";
			statement = c.prepareStatement(sumTransactions);
			statement.setInt(1, id);
			set = statement.executeQuery();
			int col = 0;
			while (set.next())
				col++;
			return col;
		} catch (SQLException e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			return 1;
		}
	}

	// Do raportu pdf: zwraca kategorie i zsumowan¹ wartoœæ przelewów
	// przychodz¹chych w danej kategorii
	public String[][] incomesByCategory() {
		int col = this.howMuchIncomesByCategory();
		String[][] baza = new String[col][2];
		PreparedStatement statement;
		ResultSet set;
		int id = this.getSessionID();
		try {
			String idByName = "SELECT Category, SUM (Value) FROM Transactions WHERE ToWho = ? GROUP BY Category ORDER BY Value DESC";
			statement = c.prepareStatement(idByName);
			statement.setInt(1, id);
			set = statement.executeQuery();
			int i = 0;
			while (set.next()) {
				baza[i][0] = set.getString("Category");
				BigDecimal value = set.getBigDecimal(2).setScale(2);
				baza[i][1] = String.valueOf(value);
				i++;
			}
			return baza;

		} catch (SQLException e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			return null;
		}

	}

	// Do raportu pdf: zwraca kategorie i zsumowan¹ wartoœæ przelewów
	// wychodz¹cych w danej kategorii
	public String[][] outcomesByCategory() {
		int col = this.howMuchOutcomesByCategory();
		String[][] baza = new String[col][2];
		PreparedStatement statement;
		ResultSet set;
		int id = this.getSessionID();
		try {
			String idByName = "SELECT Category, SUM (Value) FROM Transactions WHERE FromWho = ? GROUP BY Category ORDER BY Value DESC";
			statement = c.prepareStatement(idByName);
			statement.setInt(1, id);
			set = statement.executeQuery();
			int i = 0;
			while (set.next()) {
				baza[i][0] = set.getString("Category");
				BigDecimal value = set.getBigDecimal(2).setScale(2);
				baza[i][1] = String.valueOf(value);
				i++;
			}
			return baza;

		} catch (SQLException e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			return null;
		}

	}

	// Do raportu pdf:wartoœæ transakcji przychodz¹cych
	public BigDecimal howMuchIncomes() {
		PreparedStatement statement;
		ResultSet set;
		try {
			String sumTransactions = "SELECT SUM (Value) FROM Transactions WHERE ToWho = ?  ORDER BY SUM(Value) DESC";
			statement = c.prepareStatement(sumTransactions);
			statement.setInt(1, id);
			set = statement.executeQuery();
			BigDecimal sum = set.getBigDecimal(1);
			if (sum == null)
				sum = new BigDecimal(0).setScale(2);
			return sum;
		} catch (SQLException e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			return BigDecimal.TEN;
		}
	}

	// Do raportu pdf:wartoœæ transakcji wychodz¹cych
	public BigDecimal howMuchOutcomes() {
		PreparedStatement statement;
		ResultSet set;
		try {
			String sumTransactions = "SELECT SUM (Value) FROM Transactions WHERE FromWho = ?  ORDER BY SUM(Value) DESC";
			statement = c.prepareStatement(sumTransactions);
			statement.setInt(1, id);
			set = statement.executeQuery();
			BigDecimal sum = set.getBigDecimal(1);
			if (sum == null)
				sum = new BigDecimal(0).setScale(2);
			return sum;
		} catch (SQLException e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			return BigDecimal.TEN;
		}
	}

	// Do raportu pdf:ile transakcji przychodz¹cych
	public int incomes() {
		PreparedStatement statement;
		ResultSet set;
		try {
			String sumTransactions = "SELECT COUNT(*) FROM Transactions WHERE ToWho = ?";
			statement = c.prepareStatement(sumTransactions);
			statement.setInt(1, id);
			set = statement.executeQuery();
			int sum = set.getInt(1);
			return sum;
		} catch (SQLException e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			return 0;
		}
	}

	// Do raportu pdf:ile transakcji wychodz¹cych
	public int outcomes() {
		PreparedStatement statement;
		ResultSet set;
		try {
			String sumTransactions = "SELECT COUNT(*) FROM Transactions WHERE FromWho = ?";
			statement = c.prepareStatement(sumTransactions);
			statement.setInt(1, id);
			set = statement.executeQuery();
			int sum = set.getInt(1);
			return sum;
		} catch (SQLException e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			return 0;
		}
	}

	// logowanie, ustawia id aktualnie zalogowanego uzytkownika
	public void setSessionID(int x) {
		id = x;
	}

	// sprawdza kto jest aktualnie zalogowany
	public int getSessionID() {
		return id;
	}
}