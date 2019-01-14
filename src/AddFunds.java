import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import java.awt.Font;
import javax.swing.SwingConstants;

/**
 * @author Szymon Sakowicz
 */
@SuppressWarnings("serial")
public class AddFunds extends JFrame {

	private JPanel contentPane;
	private JTextField addFundField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					AddFunds frame = new AddFunds();
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
	public AddFunds() {
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

		addFundField = new JTextField();
		addFundField.setText("0");
		addFundField.setToolTipText("");
		addFundField.setHorizontalAlignment(SwingConstants.RIGHT);
		addFundField.setFont(new Font("Tahoma", Font.PLAIN, 40));
		addFundField.setBounds(220, 207, 222, 72);
		contentPane.add(addFundField);
		addFundField.setColumns(10);

		JLabel lblWprowadzKwot = new JLabel("WprowadŸ kwot\u0119 (PLN):");
		lblWprowadzKwot.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblWprowadzKwot.setBounds(45, 221, 298, 41);
		contentPane.add(lblWprowadzKwot);

		JButton btnDoladuj = new JButton("Do³aduj");
		btnDoladuj.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String money = addFundField.getText();
				if (money.matches("\\d*\\.?\\,?\\d+")) { // mo¿na wpisaæ tylko liczby, kropkê i przecinek
					BigDecimal funds = new BigDecimal(addFundField.getText().replace(',', '.')); // je¿eli ktoœ wpisze przecinek to zamieni siê na kropkê (jak w bigdecimal)
					if (funds.compareTo(BigDecimal.ZERO) <= 0)
						JOptionPane.showMessageDialog(null, "Wpisz kwotê wiêksz¹ od 0.");
					else {
						int charge = db.addFunds(funds, sesja);
						if (charge == 1) {
							JOptionPane.showMessageDialog(null, "Do³adowano konto");
							new UserMenu().setVisible(true);
							contentPane.setVisible(false);
							db.closeConnection();
							dispose();

						} else
							JOptionPane.showMessageDialog(null, "B³¹d");
					}
				} else
					JOptionPane.showMessageDialog(null, "Niedozwolone znaki. WprowadŸ wartoœæ liczbow¹.");

			}
		});
		btnDoladuj.setBounds(452, 207, 128, 72);
		contentPane.add(btnDoladuj);

		JLabel lblDodajrodkiDo = new JLabel("Dodaj \u015Brodki do swojego konta");
		lblDodajrodkiDo.setHorizontalAlignment(SwingConstants.CENTER);
		lblDodajrodkiDo.setFont(new Font("Tahoma", Font.PLAIN, 43));
		lblDodajrodkiDo.setBounds(10, 60, 674, 115);
		contentPane.add(lblDodajrodkiDo);

		BigDecimal balance = db.balance();
		JLabel balanceLabel = new JLabel("Saldo: " + balance + " PLN");
		balanceLabel.setBounds(510, 45, 164, 23);
		contentPane.add(balanceLabel);
		contentPane.getRootPane().setDefaultButton(btnDoladuj);

	}
}
