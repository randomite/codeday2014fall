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

public class Main {
	static Dimension sz;
	static Robot r;
	static Rectangle screenRect;
	static BufferedImage capture;
	static String out, text;
	static Path currentRelativePath;
	static String s;
	static void skip() throws Exception{
		s = Paths.get("").toAbsolutePath().toString() + "/";
		System.out.println(s);
		out = s + "out.png";
		text = s + "text.png";
		r.mouseMove(100, 100);
		Thread.sleep(100);
		r.mouseMove(200, 200);
		Thread.sleep(500);
		capture = r.createScreenCapture(screenRect);
		ImageIO.write(capture, "png", new File(out));
		double xp1 = 1661.0/1920;
		double yp1 = 902.0/1080;
		double xp2 = 1728.0/1920;
		double yp2 = 924.0/1080;
		double x1 = sz.getWidth()*xp1;
		double y1 = sz.getHeight()*yp1;
		double x2 = sz.getWidth()*xp2;
		double y2 = sz.getHeight()*yp2;
		System.out.println(x1+","+y1 + " | " + x2+","+y2);
		Runtime.getRuntime().exec("convert "+out+" +repage -crop "+(x2-x1)+"x"+(y2-y1)+"+"+x1+"+"+y1+" "+text).waitFor();
		Runtime.getRuntime().exec("tesseract -psm 7 "+text+" out").waitFor();
		char[] a = new String(Files.readAllBytes(Paths.get(s,"out.txt"))).toCharArray();
		String f = "";
		int mins = 0;
		int secs = 0;
		for(char c : a){
			if(c>='0' && c<='9')
				f+=c;
			if(f.length()==2 && mins==0){
				mins=Integer.parseInt(f);
				f="";
			}else if(f.length()==2){
				secs=Integer.parseInt(String.valueOf(f.charAt(0)>6?'6':f.charAt(0))+f.charAt(1));
				break;
			}
		}
		System.out.println(mins+":"+secs);
		xp1 = 192.0/1920;
		yp1 = 913.0/1080;
		xp2 = 1624.0/1920;
		x1 = xp1*sz.getWidth();
		y1 = yp1*sz.getHeight();
		x2 = xp2*sz.getWidth();
		int ts = 22;
		double x = (x2-x1)*((double)ts/(mins*60+secs))+x1;
		System.out.println(y1);
		r.mouseMove((int)x, (int)y1);
		r.mousePress(InputEvent.BUTTON1_MASK);
		Thread.sleep(10);
		r.mouseRelease(InputEvent.BUTTON1_MASK);
		Thread.sleep(1000);
		r.mousePress(InputEvent.BUTTON1_MASK);
		Thread.sleep(10);
		r.mouseRelease(InputEvent.BUTTON1_MASK);
		Thread.sleep(10);
		r.mouseMove((int)x1, (int)(y1+(sz.getHeight()*0.05)));
		Thread.sleep(10);
		r.mousePress(InputEvent.BUTTON1_MASK);
		Thread.sleep(10);
		r.mouseRelease(InputEvent.BUTTON1_MASK);
		next();
	}
	static void next() throws Exception{
		double xp1 = 1143.0/1920;
		double yp1 = 1013.0/1080;
		double xp2 = 1303.0/1920;
		double yp2 = 1031.0/1080;
		double x1 = sz.getWidth()*xp1;
		double y1 = sz.getHeight()*yp1;
		double x2 = sz.getWidth()*xp2;
		double y2 = sz.getHeight()*yp2;
		while(true){
			Thread.sleep(1000);
			capture = r.createScreenCapture(screenRect);
			ImageIO.write(capture, "png", new File(out));
			Runtime.getRuntime().exec("convert "+out+" +repage -crop "+(x2-x1)+"x"+(y2-y1)+"+"+x1+"+"+y1+" "+text).waitFor();
			Runtime.getRuntime().exec("tesseract -psm 7 "+text+" out").waitFor();
			String back = new String(Files.readAllBytes(Paths.get(s,"out.txt")));
			if(back.toLowerCase().contains("back to browse")){
				r.mouseMove((int)x2, (int)(y2-0.1*sz.getHeight()));
				Thread.sleep(100);
				r.mousePress(InputEvent.BUTTON1_MASK);
				Thread.sleep(10);
				r.mouseRelease(InputEvent.BUTTON1_MASK);
				Thread.sleep(10000);
				break;
			}
		}
		skip();
	}
	public static void main(String[] args) throws Exception {
		Thread.sleep(5000);
		sz = Toolkit.getDefaultToolkit().getScreenSize();
		r = new Robot();
		screenRect = new Rectangle(sz);
		skip();
	}
}
