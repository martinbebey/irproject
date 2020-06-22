import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class KLoop {
	private static double log2 = Math.log(2);
	private static int nLabelsToPrint = 8;
	private static String dataSetFilename = "dataset.dat";
	
	@SuppressWarnings("unchecked")
	public static KLoopCluster[] runAlgorithm(String dataDirFilename, int k, int reps, long seed, boolean makeNewDataset) {
		
		HashMap<String, String> stemMap;
		ArrayList<Doc> docList;
		
		if(makeNewDataset) {
			ArrayList<Doc> countsList = getCountsList(dataDirFilename);
			HashMap<String, Integer> totalCounts = getTotalCounts(countsList);
			
			stemMap = getStemMap(totalCounts);
			
			countsList = stemCountsList(countsList);
			totalCounts = getTotalCounts(countsList);
			
			docList = getDocsList(countsList, totalCounts);
			
			totalCounts = null; // clear out memory
			countsList = null;
			
			System.out.print("Saving data file " + dataDirFilename + dataSetFilename + "...");
			writeProcessedDataSet(dataDirFilename + dataSetFilename, docList, stemMap);
		} else {
		    try {
		    	System.out.print("Reading data file " + dataDirFilename + dataSetFilename + "...");
				ObjectInputStream ois = new ObjectInputStream(new FileInputStream(dataDirFilename + dataSetFilename));
				docList = (ArrayList<Doc>) ois.readObject();
				stemMap = (HashMap<String, String>) ois.readObject();
				ois.close();
			} catch(FileNotFoundException e) {
				System.out.println("ERROR: could not find data file " + dataDirFilename + dataSetFilename + ".");
				return null;
			} catch (IOException e) {
				System.out.println("ERROR: IOException while reading file " + dataDirFilename + dataSetFilename + ".");
				e.printStackTrace();
				return null;
			} catch (ClassNotFoundException e) {
				System.out.println("ERROR: ClassNotFoundException while reading file " + dataDirFilename + dataSetFilename + ".");
				e.printStackTrace();
				return null;
			}
		}
		
		System.out.print("Done.\n");
		
		docList = scrambleDocList(docList, seed);
		k = k < docList.size() ? k : docList.size(); // limit k if there aren't enough docs
		
		KLoopCluster[] clusters = new KLoopCluster[k];
		for(int i = 0; i < clusters.length; i++) {
			clusters[i] = new KLoopCluster();
			Doc doc = docList.get(i);
			clusters[i].addDoc(doc);
		}
		
		int iteration = 0;
		//printClusters(clusters, stemMap, iteration++);
		
		for(int i = k; i < docList.size(); i++) {
			addToMostSimilarCluster(clusters, docList.get(i));
			iteration++;
			//printClusters(clusters, stemMap, iteration++);
		}
		
		for(int rep = 1; rep < reps; rep++) {
			for(Doc doc : docList) {
				removeFromClusters(clusters, doc);
				addToMostSimilarCluster(clusters, doc);
				iteration++;
				//printClusters(clusters, stemMap, iteration++);
			}
		}
		printClusters(clusters, stemMap, iteration++);
		
		double wAvgSim = 0.0;
		int totalSize = 0;
		for(KLoopCluster c : clusters) {
			wAvgSim += c.selfSimilarity()*c.size();
			totalSize += c.size();
		}
		wAvgSim /= totalSize;
		
		System.out.println("\nWeighted average self-similarity: " + wAvgSim);
		
		return clusters;
	}
	
	static ArrayList<Doc> stemCountsList(ArrayList<Doc> countsList) {
		ArrayList<Doc> newCountsList = new ArrayList<>();
		while(!countsList.isEmpty()) {
			Doc doc = countsList.remove(0);
			HashMap<String, Integer> newCounts = new HashMap<>();
			for(String word : doc.counts().keySet()) {
				String stem = Stemmer.stem(word);
				newCounts.put(stem, newCounts.getOrDefault(stem, 0) + doc.counts().get(word));
			}
			newCountsList.add(new Doc(doc.filename(), newCounts));
		}
		return newCountsList;
	}

	static ArrayList<Doc> getCountsList(String dataDirFilename) {
		ArrayList<Doc> countsList = new ArrayList<>();
		ArrayList<String> filenames = new ArrayList<>();
		
		File dataDir = new File(dataDirFilename);
		for(String filename : dataDir.list()) {
			if(!filename.equals(dataSetFilename)) {
				System.out.println("Loading file " + dataDirFilename + filename + "...");
				countsList.add(new Doc(filename, Preprocessor.genDocCounts(dataDirFilename + filename, false)));
				filenames.add(filename);
			}
		}
		
		return countsList;
		
		
	}
	
	static HashMap<String, Integer> getTotalCounts(ArrayList<Doc> countsList) {
		HashMap<String, Integer> totalCounts = new HashMap<>();
		for(Doc doc : countsList) {
			for(String word : doc.counts().keySet()) {
				totalCounts.put(word, totalCounts.getOrDefault(word, 0) + doc.counts().get(word));
			}
		}
		return totalCounts;
	}
	
	static ArrayList<Doc> getDocsList(ArrayList<Doc> countsList, HashMap<String, Integer> totalCounts) {
		HashMap<String, Double> IDFTable = calcGlobalIDFTable(totalCounts);
		
		ArrayList<Doc> docList = new ArrayList<>();
		
		for(Doc count : countsList) {
			docList.add(count.toDocVector(IDFTable));
		}
		
		return docList;
	}
	
	static HashMap<String, String> getStemMap(HashMap<String, Integer> totalCounts) {
		HashMap<String, ArrayList<String>> stemWords = new HashMap<>();
		
		for(String word : totalCounts.keySet()) {
			String stem = Stemmer.stem(word);
			if(stemWords.containsKey(stem)) {
				if(!stemWords.get(stem).contains(word)) {
					stemWords.get(stem).add(word);
				}
			} else {
				ArrayList<String> newList = new ArrayList<>();
				newList.add(word);
				stemWords.put(stem, newList);
			}
		}
		
		HashMap<String, String> stemMap = new HashMap<>();
		for(String stem : stemWords.keySet()) {
			String mostCommonVariant = stemWords.get(stem).get(0);
			int mostCommonVariantCount = totalCounts.get(stemWords.get(stem).get(0));
			for(String variant : stemWords.get(stem)) {
				int variantCount = totalCounts.get(variant);
				if(variantCount > mostCommonVariantCount) {
					mostCommonVariant = variant;
					mostCommonVariantCount = variantCount;
				}
			}
			stemMap.put(stem, mostCommonVariant);
		}
		
		return stemMap;
	}
	
	static KLoopCluster addToMostSimilarCluster(KLoopCluster[] clusters, Doc doc) {
		KLoopCluster mostSimilar = clusters[0];
		double highestWeightedSimilarity = mostSimilar.cosineSimilarity(doc)/Math.log(1 + Math.log(1 + clusters[0].docs().size()));
		
		for(int i = 1; i < clusters.length; i++) {
			double weightedSimilarity = clusters[i].cosineSimilarity(doc)/Math.log(1 + Math.log(1 + clusters[i].docs().size()));
			if(weightedSimilarity > highestWeightedSimilarity) {
				mostSimilar = clusters[i];
				highestWeightedSimilarity = weightedSimilarity;
			}
		}
		
		mostSimilar.addDoc(doc);
		
		return mostSimilar;
	}
	
	static boolean removeFromClusters(KLoopCluster[] clusters, Doc doc) {
		for(KLoopCluster c : clusters) {
			if(c.removeDoc(doc))
				return true;
		}
		return false;
	}
	
	static HashMap<String, Double> calcGlobalIDFTable(HashMap<String, Integer> totalCounts) {
		HashMap<String, Double> IDFs = new HashMap<>();
		for(String word : totalCounts.keySet()) {
			IDFs.put(word, 1.0 + Math.log(((double) totalCounts.size())/totalCounts.get(word))/log2);
		}
		
		return IDFs;
	}
	
	private static ArrayList<Doc> scrambleDocList(ArrayList<Doc> list, long seed) {
		ArrayList<Doc> newList = new ArrayList<>();
		Random rng = new Random(seed);
		
		while(!list.isEmpty()) {
			newList.add(list.remove(rng.nextInt(list.size())));
		}
		
		return newList;
	}
	
	static void printClusters(KLoopCluster[] clusters, HashMap<String, String> stemMap, int iteration) {
		System.out.println("\nIteration " + iteration + ":\n");
		for(KLoopCluster c : clusters) {
			System.out.print("Top " + nLabelsToPrint + " rated words: [");
			String[] labels = c.calcLabels(nLabelsToPrint, stemMap);
			if(labels.length == 0) {
				System.out.print("]\n");
			} else {
				for(int i = 0; i < labels.length - 1; i++) {
					System.out.print(labels[i] + ", ");
				}
				System.out.print(labels[labels.length - 1] + "]\n");
			}
			System.out.println(c);
			System.out.println("Size: " + c.docs().size());
			System.out.println("Self-similarity: " + c.selfSimilarity() + "\n");
		}
	}
	
	static void writeProcessedDataSet(String filename, ArrayList<Doc> docList, HashMap<String, String> stemMap) {
		try {
			File file = new File(filename);
			file.createNewFile();
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
			oos.writeObject(docList);
			oos.writeObject(stemMap);
			oos.close();
		} catch (IOException e) {
			System.out.println("ERROR: IOException when writing to file " + filename + ". Dataset file has not been saved.");
			e.printStackTrace();
			System.exit(0);
		}
	}
}
