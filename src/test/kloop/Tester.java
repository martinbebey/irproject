import java.util.HashMap;
import java.util.HashSet;

import com.apporiented.algorithm.clustering.ClusteringAlgorithm;
import com.apporiented.algorithm.clustering.DefaultClusteringAlgorithm;
import com.apporiented.algorithm.clustering.LinkageStrategy;
import com.apporiented.algorithm.clustering.WeightedLinkageStrategy;

public class Tester {
	public static void main(String[] args) {
		KLoop.runAlgorithm("src/data/", 64, 4, 0, false);
		
	}
	
	public static void printHashSet(HashSet<String> hs) {
		System.out.println("HashSet contains: {");
		for(String s : hs) {
			System.out.println("\"" + s + "\", ");
		}
		System.out.println("}");
	}
	public static void printHashMap(HashMap<String, Integer> hm) {
		System.out.println("HashSet contains: {");
		for(String s : hm.keySet()) {
			System.out.println("\"" + s + "\", " + hm.get(s));
		}
		System.out.println("}");
	}
	public static void printHashMapDouble(HashMap<String, Double> hm) {
		System.out.println("HashSet contains: {");
		for(String s : hm.keySet()) {
			System.out.println("\"" + s + "\", " + hm.get(s));
		}
		System.out.println("}");
	}
}
