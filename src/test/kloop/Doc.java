import java.io.IOException;
import java.io.ObjectStreamException;
import java.util.HashMap;

public class Doc implements java.io.Serializable {
	private HashMap<String, Integer> counts;
	private HashMap<String, Double> vec;
	private String filename;
	private double length = 0.0;
	
	public Doc(String filename, HashMap<String, Integer> counts) {
		this.filename = filename;
		this.counts = counts;
	}
	
	public HashMap<String, Double> vec() {
		return vec;
	}
	
	public HashMap<String, Integer> counts() {
		return counts;
	}
	
	public Doc toDocVector(HashMap<String, Double> IDFs) {
		vec = new HashMap<>();
		
		int nWords = 1;
		for(int n : counts.values()) {
			nWords += n;
		}
		
		length = 0.0;
		for(String word : counts.keySet()) {
			if(IDFs.containsKey(word)) {
				double val = IDFs.get(word)*((double) counts.get(word))/nWords;
				vec.put(word, val);
				length += val*val;
			}
		}
		length = Math.sqrt(length);
		counts = null;
		
		return this;
	}
	
	
	public double length() {
		return length;
	}
	
	public String filename() {
		return filename;
	}
	
	public boolean equals(Doc doc) {
		return this.filename.equals(doc.filename());
	}
	
	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.writeObject(vec);
		out.writeObject(filename);
		out.writeDouble(length);
	}
	@SuppressWarnings("unchecked")
	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		this.vec = (HashMap<String, Double>) in.readObject();
		this.filename = (String) in.readObject();
		this.length = in.readDouble();
	}
	private void readObjectNoData() throws ObjectStreamException {
		System.out.println("ERROR: Invalid class when reading object. Exiting program.");
		System.exit(0);
	}
}
