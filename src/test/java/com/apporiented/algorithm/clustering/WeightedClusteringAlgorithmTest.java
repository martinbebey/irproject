package com.apporiented.algorithm.clustering;

import org.junit.Assert;
import org.junit.Test;

public class WeightedClusteringAlgorithmTest {
	public static final double[][] DISTANCES = new double[][] { // A--B-C--D
	{ 0, 2, 3, 5 }, { 2, 0, 1, 3 }, { 3, 1, 0, 2 }, { 5, 3, 2, 0 } };
	public static final String[] NAMES = new String[] { "A", "B", "C", "D" };
	public static final double[] WEIGHTSA = new double[] { 1, 100, 1, 1 }; // weight on B, D should be alone
	public static final double[] WEIGHTSD = new double[] { 1, 1, 100, 1 };// weight on C, A should be alone

	@Test
	public void testClusteringAvgLink() {
		ClusteringAlgorithm alg = new DefaultClusteringAlgorithm();
		Cluster ca = alg.performWeightedClustering(DISTANCES, NAMES, WEIGHTSA, new WeightedLinkageStrategy());
		Assert.assertEquals("D", ca.getChildren().get(0).getName());
		Cluster cd = alg.performWeightedClustering(DISTANCES, NAMES, WEIGHTSD, new WeightedLinkageStrategy());
		Assert.assertEquals("A", cd.getChildren().get(0).getName());
	}
}
