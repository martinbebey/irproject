import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Scanner;

public class CSVParser {
	public static void main(String[] args) {
		try {
			Scanner sc = new Scanner(new File("src/test/kloop/scpscrape.csv"), "UTF-8");
			sc.useDelimiter("\"\\d+-\\d+\",\"http://www.scp-wiki.net/.*\",\"");
			sc.next();
			while(sc.hasNext()) {
				String text = sc.next();
				int i = text.lastIndexOf("\",\"http://www.scp-wiki.net/");
				String name = text.substring(i+"\",\"http://www.scp-wiki.net/".length(),i+"\",\"http://www.scp-wiki.net/".length()+7);
				System.out.println(name);
				//FileWriter fw = new FileWriter("src/data/" + name + ".txt");
				
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("src/test/kloop/data/" + name + ".txt"), "UTF-8"));
				int endIndex = text.lastIndexOf("<div class=\"\"footer-wikiwalk-nav\"\">");
				if(endIndex > 0)
					text = text.substring(0, endIndex);
				System.out.println(text.matches(".*<div class=\"\"page-rate-widget-box\"\\\">.*?<\\/div>.*"));
				text = text.replaceAll("<div class=\"\"page-rate-widget-box\"\">.*?<\\/div>", "");
				text = text.replaceAll("</[Pp]>", "\n");
				text = text.replaceAll("<.*?>", "");
				text = text.replaceAll("\"\"", "\"");
				text = text.replaceAll("&nbsp;", "");
				
				bw.write(text.substring(1, text.length() - 2));
				bw.flush();
				bw.close();
			}
			
			sc.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
