import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * @author Szymon Sakowicz
 */
public class MainMenu {

	private JFrame frame;
	private JTextField loginField;
	private JTextField usernameField;
	private JPasswordField passwordField;
	private JPasswordField passwordConfirmField;
	private JTextField nameField;
	private JTextField surnameField;
	private JPanel labelUsernameR;
	private JPasswordField loginPWField;
	private JButton registerButton;
	private JButton loginButton;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainMenu window = new MainMenu();
					window.frame.setVisible(true);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainMenu() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 * 
	 */

	private void initialize() {
		// Stworzenie obiektu baza danych (po³¹czenie siê z ni¹)
		Database db = new Database();
		db.createTableUsers();
		db.createTableTransactions();

		frame = new JFrame();
		frame.setVisible(true);
		frame.setBounds(100, 100, 700, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JLabel labelTitle = new JLabel("Aplikacja Bankowa");
		labelTitle.setHorizontalAlignment(SwingConstants.CENTER);
		labelTitle.setFont(new Font("Tahoma", Font.PLAIN, 30));
		labelTitle.setBounds(103, 32, 386, 63);
		frame.getContentPane().add(labelTitle);

		JButton createAccountButton = new JButton("Za\u0142\u00F3\u017C konto");
		createAccountButton.addActionListener(new ActionListener() {
			
			//Wyœwietlanie pola rejestracji.
			public void actionPerformed(ActionEvent e) {
				if (labelUsernameR.isVisible() == false) {
					labelUsernameR.setVisible(true);
					frame.getRootPane().setDefaultButton(registerButton);
				} else {
					labelUsernameR.setVisible(false);
					frame.getRootPane().setDefaultButton(loginButton);
				}
			}
		});
		createAccountButton.setBounds(264, 197, 110, 36);
		frame.getContentPane().add(createAccountButton);

		loginField = new JTextField();
		loginField.setBounds(262, 136, 256, 20);
		frame.getContentPane().add(loginField);
		loginField.setColumns(10);

		JLabel labelUsername = new JLabel("Nazwa u\u017Cytkownika:");
		labelUsername.setBounds(133, 138, 140, 17);
		frame.getContentPane().add(labelUsername);

		JLabel labelPassword = new JLabel("Has\u0142o:");
		labelPassword.setBounds(133, 170, 140, 14);
		frame.getContentPane().add(labelPassword);

		loginButton = new JButton("Zaloguj si\u0119");
		loginButton.addKeyListener(new KeyAdapter() {
			//Zalogowanie po nacisniêciu Enter
			@Override
			public void keyPressed(KeyEvent e) {
			}
		});
		loginButton.addActionListener(new ActionListener() {
			
			@SuppressWarnings("deprecation") //Ignorowanie b³edu eclipse
			//Logowanie
			public void actionPerformed(ActionEvent e) {
				if ((loginField.getText().trim().length() == 0) || (loginPWField.getText().trim().length() == 0))
					JOptionPane.showMessageDialog(null, "Wypelnij wszystkie pola.");
				else {
					String username = loginField.getText();
					String password = loginPWField.getText();
					int loggedSuccesful = db.login(username, password);
					if (loggedSuccesful == 1) {
						JOptionPane.showMessageDialog(null, "Zalogowano!");
						new UserMenu().setVisible(true);
						frame.setVisible(false);
						db.closeConnection();
					} else
						JOptionPane.showMessageDialog(null, "Z³a nazwa u¿ytkownika lub has³o");
				}

			}
		});
		loginButton.setBounds(408, 198, 110, 35);
		frame.getContentPane().add(loginButton);

		labelUsernameR = new JPanel();
		labelUsernameR.setVisible(false);
		labelUsernameR.setBounds(123, 232, 423, 218);
		frame.getContentPane().add(labelUsernameR);
		labelUsernameR.setLayout(null);

		JLabel lblNazwaUytkownika = new JLabel("Nazwa u\u017Cytkownika:");
		lblNazwaUytkownika.setBounds(10, 81, 118, 14);
		labelUsernameR.add(lblNazwaUytkownika);

		JLabel labelPasswordR = new JLabel("Has\u0142o:");
		labelPasswordR.setBounds(10, 113, 118, 14);
		labelUsernameR.add(labelPasswordR);

		JLabel labelPasswordCheck = new JLabel("Potwierdz has\u0142o:");
		labelPasswordCheck.setBounds(10, 145, 118, 14);
		labelUsernameR.add(labelPasswordCheck);

		usernameField = new JTextField();
		usernameField.setBounds(138, 78, 259, 20);
		labelUsernameR.add(usernameField);
		usernameField.setColumns(10);

		passwordField = new JPasswordField();
		passwordField.setBounds(138, 110, 259, 20);
		labelUsernameR.add(passwordField);

		passwordConfirmField = new JPasswordField();
		passwordConfirmField.setBounds(138, 142, 259, 20);
		labelUsernameR.add(passwordConfirmField);

		JLabel labelSurname = new JLabel("Nazwisko:");
		labelSurname.setBounds(10, 49, 118, 14);
		labelUsernameR.add(labelSurname);

		JLabel labelName = new JLabel("Imi\u0119:");
		labelName.setBounds(10, 17, 118, 14);
		labelUsernameR.add(labelName);

		nameField = new JTextField();
		nameField.setBounds(138, 14, 259, 20);
		labelUsernameR.add(nameField);
		nameField.setColumns(10);

		surnameField = new JTextField();
		surnameField.setBounds(138, 46, 259, 20);
		labelUsernameR.add(surnameField);
		surnameField.setColumns(10);

		loginPWField = new JPasswordField();
		loginPWField.setBounds(262, 167, 257, 20);
		frame.getContentPane().add(loginPWField);

		registerButton = new JButton("Za\u0142\u00F3\u017C konto");
		//Rejestracja
		registerButton.addActionListener(new ActionListener() {
			@SuppressWarnings("deprecation")
			public void actionPerformed(ActionEvent e) {
				if ((nameField.getText().trim().length() == 0) || (usernameField.getText().trim().length() == 0)
						|| (surnameField.getText().trim().length() == 0)
						|| (passwordField.getText().trim().length() == 0)
						|| (surnameField.getText().trim().length() == 0))
					JOptionPane.showMessageDialog(null, "Wypelnij wszystkie pola.");
				else {
					if (!passwordField.getText().equals(passwordConfirmField.getText()))
						JOptionPane.showMessageDialog(null, "Wprowadzone has³a ró¿nia siê.");
					else {
						String name = nameField.getText();
						String surname = surnameField.getText();
						String username = usernameField.getText();
						String password = passwordField.getText();
						int regSucessful = db.registration(name, surname, username, password);
						if (regSucessful == 1) {
							JOptionPane.showMessageDialog(null, "Konto stworzone.");
							labelUsernameR.setVisible(false);
							frame.getRootPane().setDefaultButton(loginButton);
						} else if (regSucessful == 2)
							JOptionPane.showMessageDialog(null, "Nazwa u¿ytkownika zajêta. Wybierz inna.");
						else
							JOptionPane.showMessageDialog(null, "B³¹d");
					}
				}
			}
		});
		registerButton.setBounds(249, 173, 148, 34);
		labelUsernameR.add(registerButton);

		JButton buttonQuit = new JButton("WyjdŸ");
		buttonQuit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				db.closeConnection();
				System.exit(0);

			}
		});
		buttonQuit.setBounds(585, 11, 89, 23);
		frame.getContentPane().add(buttonQuit);
		frame.getRootPane().setDefaultButton(loginButton);
	}
}
