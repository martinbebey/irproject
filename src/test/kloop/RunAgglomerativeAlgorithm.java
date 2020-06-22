import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.apporiented.algorithm.clustering.*;

public class RunAgglomerativeAlgorithm {
	private static String dataSetFilename = "dataset.dat";
	
	public static void main(String[] args) {
		LinkageStrategy ls = new WeightedLinkageStrategy();
		ClusteringAlgorithm ca = new DefaultClusteringAlgorithm();
		
		getDataAndPerformFlatClustering("src/test/kloop/data/", false, ca, ls, 0.08);
	}
	
	@SuppressWarnings("unchecked")
	public static void getDataAndPerformFlatClustering(String dataDirFilename, boolean makeNewDataset, ClusteringAlgorithm clusteringAlgorithm, LinkageStrategy linkageStrategy, double threshold) {
		HashMap<String, String> stemMap;
		ArrayList<Doc> docList;
		
		if(makeNewDataset) {
			ArrayList<Doc> countsList = KLoop.getCountsList(dataDirFilename);
			HashMap<String, Integer> totalCounts = KLoop.getTotalCounts(countsList);
			
			stemMap = KLoop.getStemMap(totalCounts);
			
			countsList = KLoop.stemCountsList(countsList);
			totalCounts = KLoop.getTotalCounts(countsList);
			
			docList = KLoop.getDocsList(countsList, totalCounts);
			
			totalCounts = null; // clear out memory
			countsList = null;
			
			System.out.print("Saving data file " + dataDirFilename + dataSetFilename + "...");
			KLoop.writeProcessedDataSet(dataDirFilename + dataSetFilename, docList, stemMap);
		} else {
		    try {
		    	System.out.print("Reading data file " + dataDirFilename + dataSetFilename + "...");
				ObjectInputStream ois = new ObjectInputStream(new FileInputStream(dataDirFilename + dataSetFilename));
				docList = (ArrayList<Doc>) ois.readObject();
				stemMap = (HashMap<String, String>) ois.readObject();
				ois.close();
			} catch(FileNotFoundException e) {
				System.out.println("ERROR: could not find data file " + dataDirFilename + dataSetFilename + ".");
				return;
			} catch (IOException e) {
				System.out.println("ERROR: IOException while reading file " + dataDirFilename + dataSetFilename + ".");
				e.printStackTrace();
				return;
			} catch (ClassNotFoundException e) {
				System.out.println("ERROR: ClassNotFoundException while reading file " + dataDirFilename + dataSetFilename + ".");
				e.printStackTrace();
				return;
			}
		}
		
		double[][] distances = new double[docList.size()][docList.size()];
		String[] clusterNames = new String[docList.size()];
		for(int i = 0; i < distances.length; i++) {
			clusterNames[i] = docList.get(i).filename();
			distances[i][i] = 1.0;
			for(int j = i + 1; j < distances[i].length; j++) {
				distances[i][j] = cosineSimilarity(docList.get(i), docList.get(j));
				distances[j][i] = distances[i][j];
				//System.out.println(i + "," + j + ", " + distances[i][j]);
			}
		}
		
		
		List<com.apporiented.algorithm.clustering.Cluster> agglClusters = clusteringAlgorithm.performFlatClustering(distances, clusterNames, linkageStrategy, threshold);
		
		System.out.println(agglClusters);
		// convert to other type of cluster for printing the output, yes, this is a gross way to do this, but it'll do for now
		
		for(Cluster c : agglClusters) {
			printCluster(c, "");
		}
	}
	
	private static double cosineSimilarity(Doc a, Doc b) {
		// make sure the smaller doc is a and the larger doc is b
		if(a.vec().size() > b.vec().size()) {
			Doc c = a;
			a = b;
			b = c;
			c = null;
		}
		
		double prod = 0.0;
		for(String word : a.vec().keySet()) {
			if(b.vec().containsKey(word)) {
				prod += a.vec().get(word)*b.vec().get(word);
			}
		}
		
		return prod/(a.length()*b.length());
	}
	
	private static Doc getDoc(ArrayList<Doc> docList, String filename) {
		for(Doc d : docList) {
			if(d.filename().equals(filename))
				return d;
		}
		return null;
	}
	
	private static void printCluster(Cluster c, String padding) {
		System.out.println(padding + "Cluster with " + c.getChildren().size() + " children and " + c.getLeafNames().size() + " leaf nodes.");
		System.out.print(padding + "Leaf nodes:");
		for(String name : c.getLeafNames()) {
			System.out.print(name + ", ");
		}
		System.out.println("\n" + padding + "Child nodes:");
		for(Cluster child : c.getChildren()) {
			printCluster(child, padding + ">");
		}
	}
}
