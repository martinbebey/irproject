import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class Cluster {
	private ArrayList<Doc> docs;
	private HashMap<String, Double> clusterSum;
	
	public Cluster() {
		docs = new ArrayList<>();
		clusterSum = new HashMap<>();
	}
	
	public ArrayList<Doc> docs() {
		return docs;
	}
	
	public boolean containsDoc(Doc doc) {
		return docs.contains(doc);
	}
	
	public void addDoc(Doc doc) {
		docs.add(doc);
		for(String word : doc.vec().keySet()) {
			clusterSum.put(word, clusterSum.getOrDefault(word, 0.0) + doc.vec().get(word)/doc.length());
		}
	}
	
	public boolean removeDoc(Doc doc) {
		if(!docs.contains(doc))
			return false;
		
		for(String word : doc.vec().keySet()) {
			clusterSum.put(word, clusterSum.getOrDefault(word, 0.0) - doc.vec().get(word)/doc.length());
			if(clusterSum.get(word) == 0.0)
				clusterSum.remove(word);
		}
		return docs.remove(doc);
	}
	
	public double cosineSimilarity(Doc doc) {
		if(doc.length() == 0.0)
			return 0.0;
		if(docs.size() == 0)
			return 1.0;
		
		double dotProd = 0.0;
		
		for(String word : doc.vec().keySet()) {
			if(clusterSum.containsKey(word)) {
				double val = doc.vec().get(word);
				dotProd += val*clusterSum.get(word);
			}	
		}
		
		return dotProd/(doc.length()*docs.size());
	}
	
	public double selfSimilarity() {
		if(docs.size() == 0)
			return 0.0;
		if(docs.size() == 1)
			return 1.0;
		
		double sum = 0.0;
		
		for(Doc doc : docs) {
			sum += cosineSimilarity(doc);
		}
		
		return sum/docs.size();
	}
	
	public int size() {
		return clusterSum.size();
	}
	
	public String toString() {
		if(docs.size() == 0)
			return "[]";
		
		String s = "[";
		
		for(Doc doc : docs) {
			s += doc.filename() + ", "; 
		}
		
		return s.substring(0, s.length() - 2) + "]";
	}
	
	public String[] calcLabels(int n, HashMap<String, String> stemMap) {
		if(docs.size() == 0)
			return new String[0];
		if(clusterSum.size() < n)
			n = clusterSum.size();
		
		String[] labels = new String[n];
		
		ArrayList<String> words = new ArrayList<>(clusterSum.keySet());
		HashMap<String, Integer> wordOccurances = new HashMap<>();
		
		for(String word : clusterSum.keySet()) {
			for(Doc doc : docs) {
				if(doc.vec().containsKey(word)) {
					wordOccurances.put(word, wordOccurances.getOrDefault(word, 0) + 1);
				}
			}
		}
		
		Collections.sort(words, new Comparator<String>() {
			@Override
			public int compare(String a, String b) {
				double aVal = clusterSum.get(a)*Math.log(1 + Math.log(wordOccurances.getOrDefault(a, 1)));
				double bVal = clusterSum.get(b)*Math.log(1 + Math.log(wordOccurances.getOrDefault(b, 1)));
				if(aVal == bVal)
					return 0;
				if(aVal>bVal)
					return 1;
				return -1;
			}
		});
		
		for(int i = 0; i < labels.length; i++) {
			String word = words.get(words.size() - 1 - i);
			labels[i] = stemMap.getOrDefault(word, word) + " (" + wordOccurances.getOrDefault(word, 0) + ")";
		}
		
		return labels;
	}
}
