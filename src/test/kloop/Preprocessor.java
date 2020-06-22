import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Preprocessor {
	public static String stopWordsFile = "src/test/kloop/stopwords.txt";
	public static HashSet<String> stopWordsSet;
	
	public static HashSet<String> genStopWordsSet() {
		HashSet<String> stopWords = new HashSet<>();
		
		try {
			Scanner sc = new Scanner(new BufferedReader(new FileReader(new File(stopWordsFile))));
			
			while(sc.hasNext()) {
				stopWords.add(sc.next().replaceAll("[^a-z]", ""));
			}
			
			sc.close();
		} catch (FileNotFoundException e) {
			System.out.println("ERROR: Could not find stopwords file: " + stopWordsFile + ". Leaving stopwords set empty.");
			e.printStackTrace();
		}
		
		stopWordsSet = stopWords;
		
		return stopWords;
	}
	
	public static HashMap<String, Integer> genDocCounts(String filename, boolean stemWords) {
		if(stopWordsSet == null) {
			genStopWordsSet();
		}
		
		HashMap<String, Integer> counts = new HashMap<>();
		try {
			Scanner sc = new Scanner(new File(filename), "UTF-8");
			sc.useDelimiter("[\\s/]");
			
			while(sc.hasNext()) {
				String word = sc.next();
				word = word.toLowerCase(); // make lowercase
				word = word.replaceAll("[,.\\?!@\\(\\)\\[\\]\\{\\}\"\'/]", ""); // remove punctuation
				
				// throw out stop words, empty words, and non alphabetic words
				if(!stopWordsSet.contains(word) && !word.isEmpty() && Pattern.matches("[a-z]*", word) && word.length() > 1) {
					counts.put(word, counts.getOrDefault(word, 0) + 1);
				}
			}
			
			sc.close();
		} catch (FileNotFoundException e) {
			System.out.println("ERROR: Could not find file: " + filename + ". Leaving document table empty.");
			e.printStackTrace();
		}
		
		if(stemWords) {
			HashMap<String, Integer> newTable = new HashMap<>();
			for(String word : counts.keySet()) {
				String stem = Stemmer.stem(word);
				if(!word.equals(stem)) {
					if(newTable.containsKey(stem)) {
						newTable.replace(stem, newTable.get(stem) + counts.get(word));
					} else {
						newTable.put(stem, counts.get(word));
					}
				}
			}
			counts = newTable;
		}
		
		return counts;
	}
	public static HashMap<String, Integer> genDocCounts(String filename) {
		return genDocCounts(filename, true);
	}
}
