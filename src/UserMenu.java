import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import java.awt.Component;
import java.awt.Font;

/**
 * @author Szymon Sakowicz
 */
@SuppressWarnings("serial")
public class UserMenu extends JFrame {

	private JPanel contentPane;
	private static JTable table;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UserMenu frame = new UserMenu();
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
	public UserMenu() {
		Database db = new Database();

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 700, 500);
		contentPane = new JPanel();
		contentPane.setVisible(true);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JButton btnDoadowanieKonta = new JButton("Do\u0142adowanie konta");
		btnDoadowanieKonta.addActionListener(new ActionListener() {
			
			
			public void actionPerformed(ActionEvent e) {
				new AddFunds().setVisible(true); //Otwarcie nowego okna
				contentPane.setVisible(false);
				db.closeConnection(); // Zamkniêcie po³¹czenia z baz¹ (mo¿e byæ tylko jedno na raz)
				dispose(); // Zakmniecie aktualnego okna
			}
		});
		btnDoadowanieKonta.setBounds(40, 91, 152, 129);
		contentPane.add(btnDoadowanieKonta);

		JButton btnWykonajPrzelew = new JButton("Wykonaj przelew");
		btnWykonajPrzelew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Transfer().setVisible(true);
				contentPane.setVisible(false);
				db.closeConnection();
				dispose();
			}
		});
		btnWykonajPrzelew.setBounds(270, 91, 152, 129);
		contentPane.add(btnWykonajPrzelew);

		JButton logoutButton = new JButton("Wyloguj");
		logoutButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new MainMenu();
				contentPane.setVisible(false);
				dispose();
				db.setSessionID(0); // Wylogowywanie
				db.closeConnection();
			}
		});
		logoutButton.setBounds(585, 11, 89, 23);
		contentPane.add(logoutButton);
		int sesja = db.getSessionID();
		String name = db.nameById(sesja);
		JLabel Hello = new JLabel("Zalogowano jako: " + name);
		Hello.setHorizontalAlignment(SwingConstants.RIGHT);
		Hello.setBounds(304, 15, 274, 19);
		contentPane.add(Hello);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(40, 270, 620, 164);
		contentPane.add(scrollPane);

		String data[][] = db.fillTable();
		String column[] = { "Data", "Rodzaj", "Od/do kogo", "PLN", "Kategoria", "Tytu³em" };

		DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
		rightRenderer.setHorizontalAlignment(JLabel.RIGHT);

		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);

		table = new JTable(data, column);
		table.setEnabled(false);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
		table.setAlignmentX(Component.CENTER_ALIGNMENT);
		table.getColumnModel().getColumn(0).setMinWidth(115);
		table.getColumnModel().getColumn(0).setMaxWidth(130);
		table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
		table.getColumnModel().getColumn(1).setMinWidth(50);
		table.getColumnModel().getColumn(1).setMaxWidth(80);
		table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
		table.getColumnModel().getColumn(2).setMinWidth(30);
		table.getColumnModel().getColumn(2).setPreferredWidth(40);
		table.getColumnModel().getColumn(2).setMaxWidth(130);
		table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
		table.getColumnModel().getColumn(3).setMinWidth(30);
		table.getColumnModel().getColumn(3).setPreferredWidth(40);
		table.getColumnModel().getColumn(3).setMaxWidth(70);
		table.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);
		table.getColumnModel().getColumn(4).setMinWidth(60);
		table.getColumnModel().getColumn(4).setPreferredWidth(80);
		table.getColumnModel().getColumn(4).setMaxWidth(130);
		table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
		table.getColumnModel().getColumn(5).setMinWidth(60);
		table.getColumnModel().getColumn(5).setMaxWidth(200);

		scrollPane.setViewportView(table);

		BigDecimal balance = db.balance();
		JLabel balanceLabel = new JLabel("Saldo: " + balance + " PLN");
		balanceLabel.setBounds(510, 45, 164, 23);
		contentPane.add(balanceLabel);

		JButton btnNewButton = new JButton("Generuj raport");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				db.closeConnection();
				new GeneratePDF();
				dispose();
				new UserMenu().setVisible(true);

			}
		});
		btnNewButton.setBounds(508, 91, 152, 129);
		contentPane.add(btnNewButton);

		JLabel lblPanelUytkownika = new JLabel("PANEL U\u017BYTKOWNIKA");
		lblPanelUytkownika.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lblPanelUytkownika.setBounds(40, 20, 333, 57);
		contentPane.add(lblPanelUytkownika);

		JLabel lblOstatnieTransakcje = new JLabel("Ostatnie transakcje");
		lblOstatnieTransakcje.setHorizontalAlignment(SwingConstants.CENTER);
		lblOstatnieTransakcje.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lblOstatnieTransakcje.setBounds(40, 236, 620, 23);
		contentPane.add(lblOstatnieTransakcje);
	}

}
