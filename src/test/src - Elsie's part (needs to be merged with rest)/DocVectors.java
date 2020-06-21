import java.util.HashMap;

public class DocVectors {
	
	public static HashMap<String, Double> multiplyVectors(HashMap<String, Double> a, HashMap<String, Double> b) {
		HashMap<String, Double> c = new HashMap<>();
		
		// make sure a is the smaller vector, for faster computation
		if(a.size() > b.size()) {
			HashMap<String, Double> temp = b;
			b = a;
			a = temp;
			temp = null;
		}
		
		for(String word : a.keySet()) {
			if(b.containsKey(word)) {
				c.put(word, a.get(word)*b.get(word));
			}
		}
		
		return c;
	}
}
