package com.apporiented.algorithm.clustering;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ClusterTest {

    private Cluster cluster;
    
    @Before
    public void setup() {
        ClusteringAlgorithm alg = new DefaultClusteringAlgorithm();
        cluster = alg.performClustering(SampleClusterData.DISTANCES, SampleClusterData.NAMES,
                new AverageLinkageStrategy());
    }
    
    @Test
    public void testCountLeafs() throws Exception {
        int leafs = cluster.countLeafs();
        assertEquals(6, leafs);
        assertEquals("clstr#5", cluster.getName());
    }
    
    @Test
    public void testGetTotalDistance() throws Exception {
        int dist = (int) cluster.getTotalDistance();
        assertEquals(10, dist);
        assertEquals("clstr#5", cluster.getName());
    }
}
