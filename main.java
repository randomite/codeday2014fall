import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
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

public class main {
	static int time = 3;
	static Dimension sz;
	static Robot r;
	static Rectangle screenRect;
	static BufferedImage capture;
	static String out, text, ch;
	static Path currentRelativePath;
	static String path;
	static double width;
	static double height;
	static String cfs = "convert +repage -crop %dx%d+%d+%d";
	static String numsOnly = "tessedit_char_whitelist=0123456789";
	static OsCheck.OSType ostype = OsCheck.getOperatingSystemType();
	static String tess(String cmd, String config) throws Exception {
		if (config.length() != 0) {
			cmd += " -c ";
			if (ostype == ostype.MacOS)
				cmd += path + "config";
			else {
				cmd += config;
			}
		}
		exec(cmd).waitFor();
		return new String(Files.readAllBytes(Paths.get(path, "out.txt")));
	}

	static String tess(String cmd) throws Exception {
		return tess(cmd, "");
	}

	static char getCh(double x1, double h, double xo, char m) throws Exception {
		String c11 = cfs
				+ " -threshold 90%% -edge 50 -threshold 90%% -negate text.png ch.png";
		String c1 = cfs
				+ " -threshold 90%% -edge 50 -threshold 90%% -negate -bordercolor black -border 1x1 -fill white -floodfill +0+0 black -shave 1x1 text.png ch.png";

		exec(String.format(c1, (int) x1, (int) h, (int) xo, 0)).waitFor();
		String c2 = "tesseract -psm 10 " + ch + " " + path + "out";
		String s = tess(c2, numsOnly);
		char c = 0;
		if (s.length() != 0)
			c = s.charAt(0);
		if (!(c >= '0' && c <= m)) {
			print("C11");
			exec(String.format(c11, (int) x1, (int) h, (int) xo, 0)).waitFor();
			s = tess(c2, numsOnly);
			if (s.length() == 0)
				return ' ';
			else
				return s.charAt(0);
		}
		return c > m ? m : c;
	}

	static void skip() throws Exception {
		moveTo(100, 100);
		Thread.sleep(250);
		moveTo(200, 200);
		Thread.sleep(250);
		capture = r.createScreenCapture(screenRect);
		ImageIO.write(capture, "png", new File(out));
		double x1, y1, w1, h1;
		x1 = width * 337.0 / 1920;
		y1 = height * 954.0 / 1080;
		w1 = width * 1084.0 / 1920;
		h1 = height * 49.0 / 1080;

		exec(
				String.format(cfs, (int) w1, (int) h1, (int) x1, (int) y1)
						+ " " + out + " " + text).waitFor();
		int ts = 0;
		String name = tess("tesseract -psm 7 " + text + " out").toLowerCase();
		print(name);
		for (Show show : gui.shows) {
			if (name.contains(show.nameContains)) {
				Thread.sleep(show.secsIn);
				ts = show.secsSkip;
				System.out.println(show.secsSkip);
			}
		}

		x1 = width * 1662.0 / 1920;
		y1 = height * 903.0 / 1080;
		w1 = width * 66.0 / 1920;
		h1 = height * 20.0 / 1080;
		print(x1 + "," + y1 + " | " + w1 + "," + h1);
		exec(
				String.format(cfs, (int) w1, (int) h1, (int) x1, (int) y1)
						+ " " + out + " " + text).waitFor();
		x1 = 14.0 * width / 1920;
		w1 = 10.0 * width / 1920;
		double xo = 0;
		String f = "";
		f += getCh(x1, h1, xo, '9');
		xo += x1;
		f += getCh(x1, h1, xo, '9');
		int mins = Integer.parseInt(f.trim());
		print(mins);
		f = "";
		xo += x1 + w1;
		f += getCh(x1, h1, xo, '5');
		xo += x1;
		f += getCh(x1, h1, xo, '9');
		int secs = Integer.parseInt(f.trim());
		print(mins + ":" + secs);
		x1 = 192.0 / 1920 * width;
		y1 = 913.0 / 1080 * height;
		h1 = 1432.0 / 1920 * width;
		double x = h1 * ((double) ts / (mins * 60 + secs)) + x1;
		print(y1);
		moveTo(x, y1);
		leftClick();
		leftClick();
		// moveTo(x1, y1+height*0.05);
		// leftClick();
		Thread.sleep(5000);
		next();
	}

	static void loading() throws Exception {
		double x1 = 345.0 / 1920 * width;
		double y1 = 945.0 / 1080 * height;
		double w1 = 1059.0 / 1920 * width;
		double h1 = 20.0 / 1080 * height;
		while (true) {
			capture = r.createScreenCapture(screenRect);
			ImageIO.write(capture, "png", new File(out));
			BufferedReader br = new BufferedReader(
					new InputStreamReader(
							exec(
									String.format(cfs, (int) w1, (int) h1,
											(int) x1, (int) y1)
											+ " -resize 1x1 " + out + " txt:")
									.getInputStream()));
			String line;
			String s = "";
			while ((line = br.readLine()) != null) {
				s += line;
			}
			print(s);
			if (s.contains("38, 38, 38") || s.contains("28,28,28")) {
				skip();
				return;
			}
		}
	}

	static void next() throws Exception {
		double x1 = width * 1143.0 / 1920;
		double y1 = height * 1013.0 / 1080;
		double w1 = width * 160.0 / 1920;
		double h1 = height * 28.0 / 1080;
		while (true) {
			Thread.sleep(1000);
			capture = r.createScreenCapture(screenRect);
			ImageIO.write(capture, "png", new File(out));
			exec(
					String.format(cfs, (int) w1, (int) h1, (int) x1, (int) y1)
							+ " " + out + " " + text).waitFor();

			String back = tess("tesseract -psm 7 " + text + " out");
			back = back.toLowerCase();
			print(back);
			if (back.contains("back") || back.contains("beck")
					|| back.contains("browse")) {
				moveTo(x1 + w1, y1 + h1 - 0.1 * height);
				leftClick();
				break;
			}
		}
		Thread.sleep(1000);
		loading();
	}
	static String convertStreamToString(java.io.InputStream is) {
	    java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
	    return s.hasNext() ? s.next() : "";
	}
	
	public static void main(String[] args) throws Exception {		
		JFrame frame = new gui();
		frame.setVisible(true);
		String result;
		do{
			if(ostype.equals(ostype.MacOS)){
		        final String script="tell application \"System Events\"\n" +
		                "\tname of application processes whose frontmost is true\n" +
		                "end";
		        PrintWriter out = new PrintWriter("test");
		        out.write(script);
		        out.close();
				result=convertStreamToString(exec("osascript test").getInputStream());
			}else{
				result=convertStreamToString(exec("xdotool getwindowfocus getwindowname").getInputStream());
			}
			System.out.println(result);
		}while(!(result.toLowerCase().contains("chrome") && (result.toLowerCase().contains("netflix") || !ostype.equals(ostype.Linux))));
		Thread.sleep(time*1000);
		sz = Toolkit.getDefaultToolkit().getScreenSize();
		width = sz.getWidth();
		height = sz.getHeight();
		r = new Robot();
		screenRect = new Rectangle(sz);
		path = Paths.get("").toAbsolutePath().toString() + "/";
		print(path);
		out = path + "out.png";
		text = path + "text.png";
		ch = path + "ch.png";
		skip();
	}
	
	static void print(Object message) {
		System.out.println(message);
	}

	static Process exec(String cmd) throws Exception {
		print(cmd);
		return Runtime.getRuntime().exec(cmd);
	}

	static void leftClick() throws Exception {
		r.mousePress(InputEvent.BUTTON1_MASK);
		Thread.sleep(100);
		r.mouseRelease(InputEvent.BUTTON1_MASK);
		Thread.sleep(100);
	}

	static void moveTo(double x, double y) throws Exception {
		r.mouseMove((int) x, (int) y);
		Thread.sleep(100);
	}

}

final class OsCheck {
	public enum OSType {
		Windows, MacOS, Linux, Other
	};

	protected static OSType detectedOS;

	public static OSType getOperatingSystemType() {
		if (detectedOS == null) {
			String OS = System.getProperty("os.name", "generic").toLowerCase(
					Locale.ENGLISH);
			if ((OS.indexOf("mac") >= 0) || (OS.indexOf("darwin") >= 0)) {
				detectedOS = OSType.MacOS;
			} else if (OS.indexOf("win") >= 0) {
				detectedOS = OSType.Windows;
			} else if (OS.indexOf("nux") >= 0) {
				detectedOS = OSType.Linux;
			} else {
				detectedOS = OSType.Other;
			}
		}
		return detectedOS;
	}
}

class gui extends JFrame implements MouseListener {

	static JFrame frame;
	static Container c;
	static JTable table;
	static JButton addBtn, startBtn, delBtn;
	static JTextField showName, showSecsIn, showSecsSkip, delayField;
	
	public static ArrayList<Show> shows = new ArrayList<Show>();
		
	public gui() throws Exception {
		read();
		setTitle("Netflix Skip");
		setBounds(300, 300, 510, 450);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBackground(Color.WHITE);
		setUIFont(new javax.swing.plaf.FontUIResource("Open Sans",Font.PLAIN,13));
		addMouseListener(this);
		
		JPanel panel = new JPanel();
		panel.setBackground(Color.white);

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
		//table.setRowSelectionAllowed(false);
		table.getColumnModel().getColumn(0).setMaxWidth(300);
		table.getColumnModel().getColumn(1).setMaxWidth(100);
		table.getColumnModel().getColumn(2).setMaxWidth(100);
		table.setPreferredSize(new Dimension(500,200));
		
		JScrollPane scrollPane = new JScrollPane(table);
		panel.add(scrollPane);
		
		JPanel delpanel = new JPanel();
		delpanel.setBackground(Color.white);
		delpanel.setPreferredSize(new Dimension(500,80));
		delpanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		delpanel.setBorder(BorderFactory.createTitledBorder("Delete Selected Show"));
		delBtn = new JButton("<html><p style=\"padding:4px 10px;\"><b>DELETE</b></p></html>");
		delBtn.setBackground(new Color(185,9,11));
		delBtn.setForeground(Color.white);
		delBtn.setBorder(BorderFactory.createLineBorder(new Color(185,9,11)));
		delBtn.addMouseListener(this);
		delpanel.add(delBtn);
		
		
		JPanel panel2 = new JPanel();
		panel2.setBackground(Color.white);
		panel2.setPreferredSize(new Dimension(500,200));
		panel2.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		panel2.setBorder(BorderFactory.createTitledBorder("Add New Show"));

		JLabel showNameLabel = new JLabel("<html><p style=\"padding:5 8;\">Show Name:</p></html>");
		showNameLabel.setOpaque(true);
		showNameLabel.setBackground(new Color(28,28,28));
		showNameLabel.setForeground(Color.WHITE);
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
		c.setBackground(Color.WHITE);
	    c.add(panel);
	    c.add(delpanel);
	    c.add(panel2);
	    c.add(panel3);
	}

	int selRow;
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getSource() == addBtn) {
			shows.add(new Show(showName.getText(), Integer.parseInt(showSecsIn.getText()), Integer.parseInt(showSecsSkip.getText())));
			Vector row = new Vector(3);
			row.add(showName.getText().toLowerCase());
			row.add(Integer.parseInt(showSecsIn.getText()));
			row.add(Integer.parseInt(showSecsSkip.getText()));
		    ((DefaultTableModel)(table.getModel())).insertRow(shows.size()-1,row);
			showName.setText("");
			showSecsIn.setText("");
			showSecsSkip.setText("");
			try {
				write();
			}
			catch(Exception ex) {}
		}else if(e.getSource() == startBtn){
			main.time = Integer.parseInt(delayField.getText());
		}
		else if (e.getSource() == delBtn) {
			selRow = table.getSelectedRow();
			((DefaultTableModel)(table.getModel())).removeRow(selRow);
			shows.remove(selRow);
			try {
				write();
			} catch (Exception ex) {}
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
	
	
	public static void write() throws Exception {
		String line = "";
		PrintWriter writer = new PrintWriter("shows", "UTF-8");
		for(Show show : shows) {
			line = show.nameContains+","+show.secsIn+","+show.secsSkip;
			writer.println(line);
		}
		writer.close();
	}
	
	public static void read() throws Exception {
		String showFileData = new String(Files.readAllBytes(Paths.get("shows")));		
		String[] showDataLines = showFileData.split("\n");
		for (String line : showDataLines) {
			line=line.trim();
			shows.add(new Show(line.split(",")[0], Integer.parseInt(line.split(",")[1]), Integer.parseInt(line.split(",")[2])));
		}
	}
	
	
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
