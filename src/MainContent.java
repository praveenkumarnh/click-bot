import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

public class MainContent extends JPanel {
	
	private DefaultListModel<Command> listModel;
	private JList<Command> listDisplay;
	private Robot robot;
	
	private static final long serialVersionUID = 1L;

	public MainContent() throws AWTException {
		robot = new Robot();
		
		listModel = new DefaultListModel<>();
		this.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
		this.setLayout(new GridBagLayout());
		
		JScrollPane list = createCommandList();
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(8, 8, 8, 8);
		this.add(list, c);
		
		JPanel buttonsPanel = createButtonsPanel();
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 0;
		c.insets = new Insets(7, 8, 8, 8);  // top = 7 to align with list's border
		c.anchor = GridBagConstraints.PAGE_START;
		this.add(buttonsPanel, c);
	}
	
	/**
	 * Creates the box that lists commands.
	 * @return the list box
	 */
	private JScrollPane createCommandList() {
		listDisplay = new JList<>(listModel);
		listDisplay.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		listDisplay.setVisibleRowCount(16);
		listDisplay.setFixedCellWidth(200);
		listDisplay.setFont(new Font("Consolas", Font.PLAIN, 12));
		JScrollPane scrollPane = new JScrollPane(listDisplay,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		return scrollPane;
	}
	
	/**
	 * Create the panel containing user controls
	 * @return the panel
	 */
	private JPanel createButtonsPanel() {
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new GridBagLayout());
		
		// create components
		JButton addCommandButton = createAddCommandButton();
		JTextField addCommandTextField = createAddCommandTextField();
		JButton runButton = createRunButton();
		
		// format components in layout
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		buttonsPanel.add(addCommandButton, c);
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.insets = new Insets(16, 0, 0, 0);
		buttonsPanel.add(addCommandTextField, c);
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 5;
		c.insets = new Insets(150, 0, 0, 0); // figure out a good number
		buttonsPanel.add(runButton, c);
		
		return buttonsPanel;
	}
	
	/**
	 * Create the Add Command button.
	 * @return the Add Command button
	 */
	private JButton createAddCommandButton() {
		JButton button = new JButton("Add Command");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO: create dialog that lets user select action
			}
		});
		button.setPreferredSize(new Dimension(110, 23));
		return button;
	}
	
	/**
	 * Create the text field for users to type commands.
	 * @return the text field
	 */
	private JTextField createAddCommandTextField() {
		JTextField textField = new JTextField();
		textField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String input = e.getActionCommand();
				String[] toks = input.split(" ");
				Command c = null;
				try {
					if (toks[0].equals("click")) {
						c = Click.fromString(input);
						textField.setText("");
					} else if (toks[0].equals("move")) {
						c = Move.fromString(input);
						textField.setText("");
					} else if (toks[0].equals("wait")) {
						c = Wait.fromString(input);
						textField.setText("");
					} else if (toks[0].equals("type")) {
						c = Keytype.fromString(input);
						textField.setText("");
					} else {
						// invalid input
						return;
					}
				} catch (IllegalArgumentException ex) {
					// invalid input
					return;
				}
				listModel.addElement(c);
			}
		});
		textField.addFocusListener(new FocusListener() {
			private boolean showingHint = true;
			@Override
			public void focusGained(FocusEvent arg0) {
				if (showingHint) {
					textField.setText("");
					textField.setForeground(Color.BLACK);
					showingHint = false;
				}
			}
			@Override
			public void focusLost(FocusEvent arg0) {
				if (!showingHint && textField.getText().isEmpty()) {
					textField.setForeground(new Color(117, 117, 117));
					textField.setText("type command");
					showingHint = true;
				}
			}
		});
		textField.setPreferredSize(new Dimension(108, 23));
		textField.setAlignmentY(CENTER_ALIGNMENT);
		textField.setFont(new Font("Consolas", Font.PLAIN, 11));
		textField.setForeground(new Color(117, 117, 117));
		textField.setText("type command");
		return textField;
	}
	
	/**
	 * Create the Run button.
	 * @return the Run button
	 */
	private JButton createRunButton() {
		JButton button = new JButton("RUN");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (int i = 0; i < listModel.size(); ++i) {
//					listDisplay.setSelectedIndex(i);  TODO: I don't know why this doesn't work
					listModel.get(i).execute(robot);
				}
			}
		});
		button.setPreferredSize(new Dimension(110, 23));
		return button;
	}
}
