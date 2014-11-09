import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;


class Show {
	String nameContains;
	int secsIn;
	int secsSkip;

	public Show(String nameContains, int secsIn, int secsSkip) {
		this.nameContains = nameContains.toLowerCase();
		this.secsIn = secsIn;
		this.secsSkip = secsSkip;
	}
}

public class gui extends JFrame implements MouseListener {

	static JFrame frame;
	static Container c;
	static DefaultListModel lm;
	static JTable table;
	static JButton addBtn, startBtn;
	static JTextField showName, showSecsIn, showSecsSkip, delayField;
	
	//static Show[] shows = new Show[] { new Show("American Dad!", 0, 34),
		//new Show("Bob's Burgers", 0, 21) };
	static ArrayList<Show> shows = new ArrayList();
	
	public static void main(String[] args) {
		shows.add(new Show("American Dad!", 0, 34));
		shows.add(new Show("Bob's Burgers", 0, 21));
		
		JFrame frame = new gui();
		frame.setVisible(true);
	}
	
	public gui() {
		setTitle("Netflix Skip");
		setBounds(300, 300, 510, 400);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBackground(Color.WHITE);
		setUIFont(new javax.swing.plaf.FontUIResource("Open Sans",Font.PLAIN,13));
		addMouseListener(this);
		
		JPanel panel = new JPanel();
		panel.setBackground(Color.white);
		//panel.setBorder(BorderFactory.createTitledBorder("Shows"));

	    String cols[] = { "Show", "secsIn", "secSkip" };
	    String showData[][] = new String[shows.size()][3];
	    for (int i=0;i<shows.size();i++) {
	    	showData[i][0] = shows.get(i).nameContains;
	    	showData[i][1] = Integer.toString(shows.get(i).secsIn);
	    	showData[i][2] = Integer.toString(shows.get(i).secsSkip);
	    };
		table = new JTable(showData, cols);
		table.setModel(new DefaultTableModel(showData, cols));
		table.setRowHeight(25);
		table.setBackground(Color.WHITE);
		table.setRowSelectionAllowed(false);
		table.getColumnModel().getColumn(0).setMaxWidth(300);
		table.getColumnModel().getColumn(1).setMaxWidth(100);
		table.getColumnModel().getColumn(2).setMaxWidth(100);
		JScrollPane scrollPane = new JScrollPane(table);
		panel.add(scrollPane);
		
		JPanel panel2 = new JPanel();
		panel2.setBackground(Color.white);
		panel2.setPreferredSize(new Dimension(500,200));
		panel2.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		panel2.setBorder(BorderFactory.createTitledBorder("Add New Show"));

		JLabel showNameLabel = new JLabel("<html><p style=\"padding:5 8;\">Show Name:</p></html>");
		showNameLabel.setOpaque(true);
		showNameLabel.setBackground(new Color(28,28,28));
		showNameLabel.setForeground(Color.WHITE);
		//showNameLabel.setBorder(BorderFactory.createLineBorder(new Color(28,28,28)));
		panel2.add(showNameLabel);
		
		showName = new JTextField("",29);
		showName.setBorder(BorderFactory.createCompoundBorder(
		        BorderFactory.createLineBorder(new Color(28,28,28)), 
		        BorderFactory.createEmptyBorder(4, 4, 4, 4)));
		panel2.add(showName);
		
		JLabel secsInLabel = new JLabel("<html><p style=\"padding:5 8;\">Secs before intro:</p></html>");
		secsInLabel.setOpaque(true);
		secsInLabel.setBackground(new Color(28,28,28));
		secsInLabel.setForeground(Color.WHITE);
		panel2.add(secsInLabel);
		
		showSecsIn = new JTextField("",4);
		showSecsIn.setBorder(BorderFactory.createCompoundBorder(
		        BorderFactory.createLineBorder(new Color(28,28,28)), 
		        BorderFactory.createEmptyBorder(4, 4, 4, 4)));
		panel2.add(showSecsIn);
		
		JLabel secsSkipLabel = new JLabel("<html><p style=\"padding:5 8;\">Length (secs) of intro:</p></html>"); 
		secsSkipLabel.setOpaque(true);
		secsSkipLabel.setBackground(new Color(28,28,28));
		secsSkipLabel.setForeground(Color.WHITE);
		panel2.add(secsSkipLabel);
		
		showSecsSkip = new JTextField("",4);
		showSecsSkip.setBorder(BorderFactory.createCompoundBorder(
		        BorderFactory.createLineBorder(new Color(28,28,28)), 
		        BorderFactory.createEmptyBorder(4, 4, 4, 4)));
		panel2.add(showSecsSkip);
		
		addBtn = new JButton("<html><p style=\"padding:4px 10px;\"><b>ADD</b></p></html>");
		addBtn.setBackground(new Color(185,9,11));
		addBtn.setForeground(Color.white);
		addBtn.setBorder(BorderFactory.createLineBorder(new Color(185,9,11)));
		addBtn.addMouseListener(this);
		panel2.add(addBtn);
				
		JPanel panel3 = new JPanel();
		panel3.setBackground(Color.white);
		panel3.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Starting the Program"),
				BorderFactory.createEmptyBorder(4, 4, 4, 4)));
		
		JLabel delayLabel = new JLabel("<html><p style=\"padding:8 12;\">Start delay (secs):</p></html>");
		delayLabel.setOpaque(true);
		delayLabel.setBackground(new Color(28,28,28));
		delayLabel.setForeground(Color.WHITE);
		panel3.add(delayLabel);
		
		delayField = new JTextField("",10);
		delayField.setBorder(BorderFactory.createCompoundBorder(
		        BorderFactory.createLineBorder(new Color(28,28,28)), 
		        BorderFactory.createEmptyBorder(8, 8, 8, 8)));
		panel3.add(delayField);
		
		startBtn = new JButton("<html><p style=\"padding:7px 16px;\"><b>START</b></p></html>");
		startBtn.setBackground(new Color(185,9,11));
		startBtn.setForeground(Color.white);
		startBtn.setBorder(BorderFactory.createLineBorder(new Color(185,9,11)));
		startBtn.addMouseListener(this);
		panel3.add(startBtn);
		
		Container c = getContentPane();
		c.setLayout(new BoxLayout(c,BoxLayout.Y_AXIS));
		//c.setLayout(new GridLayout(3,1));
		c.setBackground(Color.WHITE);
	    c.add(panel);
	    c.add(panel2);
	    c.add(panel3);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getSource() == addBtn) {
			shows.add(new Show(showName.getText(), Integer.parseInt(showSecsIn.getText()), Integer.parseInt(showSecsSkip.getText())));
			Vector row = new Vector(3);
			row.add(showName.getText());
			row.add(Integer.parseInt(showSecsIn.getText()));
			row.add(Integer.parseInt(showSecsSkip.getText()));
		    ((DefaultTableModel)(table.getModel())).insertRow(shows.size()-1,row);
			showName.setText("");
			showSecsIn.setText("");
			showSecsSkip.setText("");
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {	}

	@Override
	public void mouseExited(MouseEvent arg0) {	}

	@Override
	public void mousePressed(MouseEvent arg0) {	}

	@Override
	public void mouseReleased(MouseEvent arg0) {	}
	
	
	public static void setUIFont (javax.swing.plaf.FontUIResource f){
	    java.util.Enumeration keys = UIManager.getDefaults().keys();
	    while (keys.hasMoreElements()) {
	      Object key = keys.nextElement();
	      Object value = UIManager.get (key);
	      if (value != null && value instanceof javax.swing.plaf.FontUIResource)
	        UIManager.put (key, f);
	      }
	    } 
	
}
