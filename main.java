import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;
//Runtime.getRuntime().exec("convert -negate "+text+" -edge 50 "+text).waitFor();

public class main {
	
	static Dimension sz;
	static Robot r;
	static Rectangle screenRect;
	static BufferedImage capture;
	static String out, text;
	static Path currentRelativePath;
	static String s;
	static double width;
	static double height;
	
	static void skip() throws Exception {	
		s = Paths.get("").toAbsolutePath().toString() + "/";
		print(s);
		out = s + "out.png";
		text = s + "text.png";
		moveTo(100,100);
		moveTo(200,200);
		capture = r.createScreenCapture(screenRect);
		ImageIO.write(capture, "png", new File(out));
		double xp1 = 1661.0/1920;
		double yp1 = 902.0/1080;
		double xp2 = 1728.0/1920;
		double yp2 = 924.0/1080;
		double x1 = width*xp1;
		double y1 = height*yp1;
		double x2 = width*xp2;
		double y2 = height*yp2;
		print(x1+","+y1 + " | " + x2+","+y2);
		exec("convert "+out+" +repage -crop "+(x2-x1)+"x"+(y2-y1)+"+"+x1+"+"+y1+" "+text);
		exec("tesseract -psm 7 "+text+" out");
		char[] a = new String(Files.readAllBytes(Paths.get(s,"out.txt"))).toCharArray();
		String f = "";
		int mins = 0;
		int secs = 0;
		for (char c : a) {
			if (c>='0' && c<='9') {
				f += c;
			}
			if (f.length()==2 && mins==0) {
				mins = Integer.parseInt(f);
				f = "";
			}
			else if (f.length()==2) {
				secs = Integer.parseInt(String.valueOf(f.charAt(0)>6?'6':f.charAt(0))+f.charAt(1));
				break;
			}
		}
		
		print(mins+":"+secs);
		xp1 = 192.0/1920;
		yp1 = 913.0/1080;
		xp2 = 1624.0/1920;
		x1 = xp1*width;
		y1 = yp1*height;
		x2 = xp2*width;
		int ts = 22;
		double x = (x2-x1)*((double)ts/(mins*60+secs))+x1;
		print(y1);
		moveTo(x, y1);
		leftClick();
		leftClick();
		moveTo(x1, y1+height*0.05);
		leftClick();
		next();
	}
	
	static void next() throws Exception{
		double xp1 = 1143.0/1920;
		double yp1 = 1013.0/1080;
		double xp2 = 1303.0/1920;
		double yp2 = 1031.0/1080;
		double x1 = width*xp1;
		double y1 = height*yp1;
		double x2 = width*xp2;
		double y2 = height*yp2;
		while (true) {
			Thread.sleep(1000);
			capture = r.createScreenCapture(screenRect);
			ImageIO.write(capture, "png", new File(out));
			exec("convert "+out+" +repage -crop " + (x2-x1) + "x" + (y2-y1) + "+" + x1 + "+" +y1+ " " + text);
			exec("tesseract -psm 7 "+text+" out");
			String back = new String(Files.readAllBytes(Paths.get(s,"out.txt")));
			if (back.toLowerCase().contains("back to browse")) {
				moveTo(x2, y2-0.1*height);
				leftClick();
				break;
			}
		}
		skip();
	}
	
	public static void main(String[] args) throws Exception {
		Thread.sleep(5000);
		sz = Toolkit.getDefaultToolkit().getScreenSize();
		width = sz.getWidth();
		height = sz.getHeight();
		r = new Robot();
		screenRect = new Rectangle(sz);
		skip();
	}
	
	static void print(Object message) {
		System.out.println(message);
	}

	static Process exec(String cmd) throws Exception {
		return Runtime.getRuntime().exec(cmd);
	}
	
	static void leftClick() throws Exception {
		r.mousePress(InputEvent.BUTTON1_MASK);
		Thread.sleep(10);
		r.mouseRelease(InputEvent.BUTTON1_MASK);
		Thread.sleep(10000);
	}
	
	static void moveTo(double x, double y) throws Exception {
		r.mouseMove((int)x, (int)y);
		Thread.sleep(100);
	}
	
}
