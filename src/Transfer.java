import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JComboBox;

/**
 * @author Szymon Sakowicz
 */
@SuppressWarnings("serial")
public class Transfer extends JFrame {

	private JPanel contentPane;
	private JTextField usernameField;
	private JTextField valueField;
	private JTextField captionField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Transfer frame = new Transfer();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Transfer() {
		Database db = new Database();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 700, 500);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		JButton logoutButton = new JButton("Wyloguj");
		logoutButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new MainMenu();
				contentPane.setVisible(false);
				dispose();
				db.setSessionID(0);
				db.closeConnection();
			}
		});
		logoutButton.setBounds(585, 11, 89, 23);
		contentPane.add(logoutButton);
		int sesja = db.getSessionID();
		String name = db.nameById(sesja);
		JLabel Hello = new JLabel("Zalogowano jako: " + name);
		Hello.setHorizontalAlignment(SwingConstants.RIGHT);
		Hello.setBounds(220, 15, 261, 19);
		contentPane.add(Hello);

		JButton btnPowrt = new JButton("Powr\u00F3t");
		btnPowrt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new UserMenu().setVisible(true);
				contentPane.setVisible(false);
				db.closeConnection();
				dispose();
			}
		});
		btnPowrt.setBounds(486, 11, 89, 23);
		contentPane.add(btnPowrt);

		JLabel lblWylijPienidze = new JLabel("Wy\u015Blij pieni\u0105dze");
		lblWylijPienidze.setHorizontalAlignment(SwingConstants.CENTER);
		lblWylijPienidze.setFont(new Font("Tahoma", Font.PLAIN, 40));
		lblWylijPienidze.setBounds(82, 55, 506, 74);
		contentPane.add(lblWylijPienidze);

		JLabel lblNazwaUytkownika = new JLabel("Nazwa u\u017Cytkownika:");
		lblNazwaUytkownika.setBounds(82, 157, 145, 31);
		contentPane.add(lblNazwaUytkownika);

		usernameField = new JTextField();
		usernameField.setBounds(220, 159, 214, 26);
		contentPane.add(usernameField);
		usernameField.setColumns(10);

		JLabel lblKwota = new JLabel("Kwota:");
		lblKwota.setBounds(82, 199, 145, 31);
		contentPane.add(lblKwota);

		valueField = new JTextField();
		valueField.setColumns(10);
		valueField.setBounds(220, 201, 214, 26);
		contentPane.add(valueField);

		BigDecimal balance = db.balance();
		JLabel balanceLabel = new JLabel("Saldo: " + balance + " PLN");
		balanceLabel.setBounds(510, 45, 164, 23);
		contentPane.add(balanceLabel);

		JComboBox<String> comboBox = new JComboBox<String>();
		comboBox.setBounds(220, 245, 214, 26);
		contentPane.add(comboBox);
		comboBox.addItem("¯ywnoœæ");
		comboBox.addItem("Paliwo");
		comboBox.addItem("Rachunki");
		comboBox.addItem("Ubezpieczenia");
		comboBox.addItem("Osobiste");
		comboBox.addItem("Oszczednoœci");
		comboBox.addItem("Inne");

		JButton sendMoney = new JButton("Wy\u015Blij");
		sendMoney.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String money = valueField.getText();
				if (money.matches("\\d*\\.?\\,?\\d+")) {
					if ((valueField.getText().trim().length() == 0) || (usernameField.getText().trim().length() == 0)
							|| (captionField.getText().trim().length() == 0))
						JOptionPane.showMessageDialog(null, "Wypelnij wszystkie pola.");
					else {
						int id = db.idByUserame(usernameField.getText());
						if (id == 0)
							JOptionPane.showMessageDialog(null, "Nie ma takiego u¿ytkownika");
						else {
							BigDecimal value = new BigDecimal(valueField.getText().replace(',', '.'));
							if (value.compareTo(balance) > 0)
								JOptionPane.showMessageDialog(null, "Za ma³o œrodków na koncie. Doladuj konto.");
							else if (value.compareTo(BigDecimal.ZERO) <= 0)
								JOptionPane.showMessageDialog(null, "Wpisz kwotê wiêksz¹ od 0.");
							else {
								String category = (String) comboBox.getSelectedItem();
								String caption = captionField.getText();
								int sent = db.sendMoney(id, value, category, caption);
								if (sent == 1) {
									JOptionPane.showMessageDialog(null, "Wys³ano pomyœlnie");
									new UserMenu().setVisible(true);
									contentPane.setVisible(false);
									db.closeConnection();
									dispose();
								} else if (sent == 2)
									JOptionPane.showMessageDialog(null, "Nie mo¿esz wys³ac pieniedzy samemu sobie.");
								else
									JOptionPane.showMessageDialog(null, "B³¹d");
							}
						}
					}
				} else
					JOptionPane.showMessageDialog(null, "Niedozwolone znaki. WprowadŸ wartoœæ liczbow¹.");

			}
		});
		sendMoney.setBounds(306, 323, 128, 23);
		contentPane.add(sendMoney);

		JLabel lblKategoria = new JLabel("Kategoria");
		lblKategoria.setBounds(82, 241, 145, 31);
		contentPane.add(lblKategoria);

		JLabel lblTytuem = new JLabel("Tytu\u0142em");
		lblTytuem.setBounds(82, 282, 145, 31);
		contentPane.add(lblTytuem);

		captionField = new JTextField();
		captionField.setColumns(10);
		captionField.setBounds(220, 284, 214, 26);
		contentPane.add(captionField);
		contentPane.getRootPane().setDefaultButton(sendMoney);
	}
}
