import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import java.util.Locale;

public class main {

	static Dimension sz;
	static Robot r;
	static Rectangle screenRect;
	static BufferedImage capture;
	static String out, text, ch;
	static Path currentRelativePath;
	static String path;
	static double width;
	static double height;

	static char getCh(double x1, double h, double xo, char m) throws Exception {
		String c11 = "convert +repage -crop %dx%d+%d+0 -threshold 90%% -edge 50 -threshold 90%% -negate text.png ch.png";
		String c1 = "convert +repage -crop %dx%d+%d+0 -threshold 90%% -edge 50 -threshold 90%% -negate -bordercolor black -border 1x1 -fill white -floodfill +0+0 black -shave 1x1 text.png ch.png";
		OsCheck.OSType ostype = OsCheck.getOperatingSystemType();
		String c2;
		if (ostype == ostype.MacOS)
			c2 = "tesseract -psm 10 " + ch + " " + path + "out -c " + path
					+ "config";
		else {
			c2 = "tesseract -psm 10 " + ch + " " + path
					+ "out -c tessedit_char_whitelist=0123456789";
		}
		exec(String.format(c1, (int) x1, (int) h, (int) xo)).waitFor();
		exec(c2).waitFor();
		char c = 0;
		String s = new String(Files.readAllBytes(Paths.get(path, "out.txt")));
		if (s.length() != 0)
			c = s.charAt(0);
		if (!(c >= '0' && c <= m)) {
			print("C11");
			exec(String.format(c11, (int) x1, (int) h, (int) xo)).waitFor();
			exec(c2).waitFor();
			c = new String(Files.readAllBytes(Paths.get(s, "out.txt")))
					.charAt(0);
		}
		return c > m ? m : c;
	}

	static void skip() throws Exception {
		moveTo(100, 100);
		Thread.sleep(1000);
		moveTo(200, 200);
		Thread.sleep(1000);
		capture = r.createScreenCapture(screenRect);
		ImageIO.write(capture, "png", new File(out));
		double x1 = width * 1662.0 / 1920;
		double y1 = height * 903.0 / 1080;
		double x2 = width * 1728.0 / 1920;
		double y2 = height * 923.0 / 1080;
		print(x1 + "," + y1 + " | " + x2 + "," + y2);
		exec(
				"convert " + out + " +repage -crop " + (x2 - x1) + "x"
						+ (y2 - y1) + "+" + x1 + "+" + y1 + " " + text)
				.waitFor();
		double w = (x2 - x1);
		double h = (y2 - y1);
		x1 = 14.0 / w * w;
		x2 = 10.0 / w * w;
		double xo = 0;
		String f = "";
		f += getCh(x1, h, xo, '9');
		xo += x1;
		f += getCh(x1, h, xo, '9');
		int mins = Integer.parseInt(f);
		print(mins);
		f = "";
		xo += x1 + x2;
		f += getCh(x1, h, xo, '5');
		xo += x1;
		f += getCh(x1, h, xo, '9');
		int secs = Integer.parseInt(f);
		print(mins + ":" + secs);
		x1 = 192.0 / 1920 * width;
		y1 = 913.0 / 1080 * height;
		x2 = 1624.0 / 1920 * width;
		int ts = 34;
		double x = (x2 - x1) * ((double) ts / (mins * 60 + secs)) + x1;
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
					new InputStreamReader(exec(
							"convert -crop " + w1 + "x" + h1 + "+" + x1 + "+"
									+ y1 + " -resize 1x1 " + out + " txt:")
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
		double xp1 = 1143.0 / 1920;
		double yp1 = 1013.0 / 1080;
		double xp2 = 1303.0 / 1920;
		double yp2 = 1031.0 / 1080;
		double x1 = width * xp1;
		double y1 = height * yp1;
		double x2 = width * xp2;
		double y2 = height * yp2;
		while (true) {
			Thread.sleep(1000);
			capture = r.createScreenCapture(screenRect);
			ImageIO.write(capture, "png", new File(out));
			exec(
					"convert " + out + " +repage -crop " + (x2 - x1) + "x"
							+ (y2 - y1) + "+" + x1 + "+" + y1 + " " + text)
					.waitFor();
			exec("tesseract -psm 7 " + text + " out").waitFor();
			String back = new String(Files.readAllBytes(Paths.get(path,
					"out.txt")));
			if (back.toLowerCase().contains("back")) {
				moveTo(x2, y2 - 0.1 * height);
				leftClick();
				break;
			}
		}
		Thread.sleep(1000);
		loading();
	}

	public static void main(String[] args) throws Exception {
		Thread.sleep(5000);
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
