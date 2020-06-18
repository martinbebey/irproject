package com.apporiented.algorithm.clustering;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DefaultClusteringAlgorithmTest {

	private double[][] distances;
	private String[] names;

	@Before
	public void setup() {
		distances = SampleClusterData.DISTANCES;
		names = SampleClusterData.NAMES;
	}

	@Test
	public void testClusteringAvgLink() {
		ClusteringAlgorithm alg = new DefaultClusteringAlgorithm();
		Cluster c = alg.performClustering(distances, names, new AverageLinkageStrategy());
		Assert.assertNotNull(c);
	}

	@Test
	public void testDuplicateNames() {
		ClusteringAlgorithm alg = new DefaultClusteringAlgorithm();
		try {
			Cluster c = alg.performClustering(distances, SampleClusterData.NAMES_WITH_DUPLICATE,
					new AverageLinkageStrategy());
			Assert.fail("Exception expected");
		} catch (IllegalArgumentException e) {
			Assert.assertEquals("Duplicate names", e.getMessage());
		}
	}

}
