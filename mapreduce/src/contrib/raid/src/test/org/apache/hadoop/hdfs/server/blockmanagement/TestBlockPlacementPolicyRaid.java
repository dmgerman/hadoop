begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|///**
end_comment

begin_comment
comment|// * Licensed to the Apache Software Foundation (ASF) under one
end_comment

begin_comment
comment|// * or more contributor license agreements.  See the NOTICE file
end_comment

begin_comment
comment|// * distributed with this work for additional information
end_comment

begin_comment
comment|// * regarding copyright ownership.  The ASF licenses this file
end_comment

begin_comment
comment|// * to you under the Apache License, Version 2.0 (the
end_comment

begin_comment
comment|// * "License"); you may not use this file except in compliance
end_comment

begin_comment
comment|// * with the License.  You may obtain a copy of the License at
end_comment

begin_comment
comment|// *
end_comment

begin_comment
comment|// *     http://www.apache.org/licenses/LICENSE-2.0
end_comment

begin_comment
comment|// *
end_comment

begin_comment
comment|// * Unless required by applicable law or agreed to in writing, software
end_comment

begin_comment
comment|// * distributed under the License is distributed on an "AS IS" BASIS,
end_comment

begin_comment
comment|// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
end_comment

begin_comment
comment|// * See the License for the specific language governing permissions and
end_comment

begin_comment
comment|// * limitations under the License.
end_comment

begin_comment
comment|// */
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|//package org.apache.hadoop.hdfs.server.blockmanagement;
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|//import java.io.IOException;
end_comment

begin_comment
comment|//import java.util.Collection;
end_comment

begin_comment
comment|//import java.util.HashMap;
end_comment

begin_comment
comment|//import java.util.HashSet;
end_comment

begin_comment
comment|//import java.util.List;
end_comment

begin_comment
comment|//import java.util.Map;
end_comment

begin_comment
comment|//import java.util.Set;
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|//import junit.framework.Assert;
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|//import org.apache.commons.logging.Log;
end_comment

begin_comment
comment|//import org.apache.commons.logging.LogFactory;
end_comment

begin_comment
comment|//import org.apache.hadoop.conf.Configuration;
end_comment

begin_comment
comment|//import org.apache.hadoop.fs.BlockLocation;
end_comment

begin_comment
comment|//import org.apache.hadoop.fs.FileStatus;
end_comment

begin_comment
comment|//import org.apache.hadoop.fs.FileSystem;
end_comment

begin_comment
comment|//import org.apache.hadoop.fs.Path;
end_comment

begin_comment
comment|//import org.apache.hadoop.hdfs.DFSConfigKeys;
end_comment

begin_comment
comment|//import org.apache.hadoop.hdfs.DFSTestUtil;
end_comment

begin_comment
comment|//import org.apache.hadoop.hdfs.MiniDFSCluster;
end_comment

begin_comment
comment|//import org.apache.hadoop.hdfs.protocol.ExtendedBlock;
end_comment

begin_comment
comment|//import org.apache.hadoop.hdfs.protocol.LocatedBlock;
end_comment

begin_comment
comment|//import org.apache.hadoop.hdfs.server.blockmanagement.BlockPlacementPolicyRaid.CachedFullPathNames;
end_comment

begin_comment
comment|//import org.apache.hadoop.hdfs.server.blockmanagement.BlockPlacementPolicyRaid.CachedLocatedBlocks;
end_comment

begin_comment
comment|//import org.apache.hadoop.hdfs.server.blockmanagement.BlockPlacementPolicyRaid.FileType;
end_comment

begin_comment
comment|//import org.apache.hadoop.hdfs.server.namenode.*;
end_comment

begin_comment
comment|//import org.apache.hadoop.raid.RaidNode;
end_comment

begin_comment
comment|//import org.junit.Test;
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|//public class TestBlockPlacementPolicyRaid {
end_comment

begin_comment
comment|//  private Configuration conf = null;
end_comment

begin_comment
comment|//  private MiniDFSCluster cluster = null;
end_comment

begin_comment
comment|//  private FSNamesystem namesystem = null;
end_comment

begin_comment
comment|//  private BlockPlacementPolicyRaid policy = null;
end_comment

begin_comment
comment|//  private FileSystem fs = null;
end_comment

begin_comment
comment|//  String[] rack1 = {"/rack1"};
end_comment

begin_comment
comment|//  String[] rack2 = {"/rack2"};
end_comment

begin_comment
comment|//  String[] host1 = {"host1.rack1.com"};
end_comment

begin_comment
comment|//  String[] host2 = {"host2.rack2.com"};
end_comment

begin_comment
comment|//  String xorPrefix = null;
end_comment

begin_comment
comment|//  String raidTempPrefix = null;
end_comment

begin_comment
comment|//  String raidrsTempPrefix = null;
end_comment

begin_comment
comment|//  String raidrsHarTempPrefix = null;
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|//  final static Log LOG =
end_comment

begin_comment
comment|//      LogFactory.getLog(TestBlockPlacementPolicyRaid.class);
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|//  protected void setupCluster() throws IOException {
end_comment

begin_comment
comment|//    conf = new Configuration();
end_comment

begin_comment
comment|//    conf.setLong(DFSConfigKeys.DFS_BLOCKREPORT_INTERVAL_MSEC_KEY, 1000L);
end_comment

begin_comment
comment|//    conf.set("dfs.replication.pending.timeout.sec", "2");
end_comment

begin_comment
comment|//    conf.setLong(DFSConfigKeys.DFS_BLOCK_SIZE_KEY, 1L);
end_comment

begin_comment
comment|//    conf.set("dfs.block.replicator.classname",
end_comment

begin_comment
comment|//             "org.apache.hadoop.hdfs.server.namenode.BlockPlacementPolicyRaid");
end_comment

begin_comment
comment|//    conf.set(RaidNode.STRIPE_LENGTH_KEY, "2");
end_comment

begin_comment
comment|//    conf.set(RaidNode.RS_PARITY_LENGTH_KEY, "3");
end_comment

begin_comment
comment|//    conf.setInt(DFSConfigKeys.DFS_BYTES_PER_CHECKSUM_KEY, 1);
end_comment

begin_comment
comment|//    // start the cluster with one datanode first
end_comment

begin_comment
comment|//    cluster = new MiniDFSCluster.Builder(conf).numDataNodes(1).
end_comment

begin_comment
comment|//        format(true).racks(rack1).hosts(host1).build();
end_comment

begin_comment
comment|//    cluster.waitActive();
end_comment

begin_comment
comment|//    namesystem = cluster.getNameNode().getNamesystem();
end_comment

begin_comment
comment|//    Assert.assertTrue("BlockPlacementPolicy type is not correct.",
end_comment

begin_comment
comment|//      namesystem.blockManager.replicator instanceof BlockPlacementPolicyRaid);
end_comment

begin_comment
comment|//    policy = (BlockPlacementPolicyRaid) namesystem.blockManager.replicator;
end_comment

begin_comment
comment|//    fs = cluster.getFileSystem();
end_comment

begin_comment
comment|//    xorPrefix = RaidNode.xorDestinationPath(conf).toUri().getPath();
end_comment

begin_comment
comment|//    raidTempPrefix = RaidNode.xorTempPrefix(conf);
end_comment

begin_comment
comment|//    raidrsTempPrefix = RaidNode.rsTempPrefix(conf);
end_comment

begin_comment
comment|//    raidrsHarTempPrefix = RaidNode.rsHarTempPrefix(conf);
end_comment

begin_comment
comment|//  }
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|//  /**
end_comment

begin_comment
comment|//   * Test that the parity files will be placed at the good locations when we
end_comment

begin_comment
comment|//   * create them.
end_comment

begin_comment
comment|//   */
end_comment

begin_comment
comment|//  @Test
end_comment

begin_comment
comment|//  public void testChooseTargetForRaidFile() throws IOException {
end_comment

begin_comment
comment|//    setupCluster();
end_comment

begin_comment
comment|//    try {
end_comment

begin_comment
comment|//      String src = "/dir/file";
end_comment

begin_comment
comment|//      String parity = raidrsTempPrefix + src;
end_comment

begin_comment
comment|//      DFSTestUtil.createFile(fs, new Path(src), 4, (short)1, 0L);
end_comment

begin_comment
comment|//      DFSTestUtil.waitReplication(fs, new Path(src), (short)1);
end_comment

begin_comment
comment|//      refreshPolicy();
end_comment

begin_comment
comment|//      setBlockPlacementPolicy(namesystem, policy);
end_comment

begin_comment
comment|//      // start 3 more datanodes
end_comment

begin_comment
comment|//      String[] racks = {"/rack2", "/rack2", "/rack2",
end_comment

begin_comment
comment|//                        "/rack2", "/rack2", "/rack2"};
end_comment

begin_comment
comment|//      String[] hosts =
end_comment

begin_comment
comment|//        {"host2.rack2.com", "host3.rack2.com", "host4.rack2.com",
end_comment

begin_comment
comment|//         "host5.rack2.com", "host6.rack2.com", "host7.rack2.com"};
end_comment

begin_comment
comment|//      cluster.startDataNodes(conf, 6, true, null, racks, hosts, null);
end_comment

begin_comment
comment|//      int numBlocks = 6;
end_comment

begin_comment
comment|//      DFSTestUtil.createFile(fs, new Path(parity), numBlocks, (short)2, 0L);
end_comment

begin_comment
comment|//      DFSTestUtil.waitReplication(fs, new Path(parity), (short)2);
end_comment

begin_comment
comment|//      FileStatus srcStat = fs.getFileStatus(new Path(src));
end_comment

begin_comment
comment|//      BlockLocation[] srcLoc =
end_comment

begin_comment
comment|//        fs.getFileBlockLocations(srcStat, 0, srcStat.getLen());
end_comment

begin_comment
comment|//      FileStatus parityStat = fs.getFileStatus(new Path(parity));
end_comment

begin_comment
comment|//      BlockLocation[] parityLoc =
end_comment

begin_comment
comment|//          fs.getFileBlockLocations(parityStat, 0, parityStat.getLen());
end_comment

begin_comment
comment|//      int parityLen = RaidNode.rsParityLength(conf);
end_comment

begin_comment
comment|//      for (int i = 0; i< numBlocks / parityLen; i++) {
end_comment

begin_comment
comment|//        Set<String> locations = new HashSet<String>();
end_comment

begin_comment
comment|//        for (int j = 0; j< srcLoc.length; j++) {
end_comment

begin_comment
comment|//          String [] names = srcLoc[j].getNames();
end_comment

begin_comment
comment|//          for (int k = 0; k< names.length; k++) {
end_comment

begin_comment
comment|//            LOG.info("Source block location: " + names[k]);
end_comment

begin_comment
comment|//            locations.add(names[k]);
end_comment

begin_comment
comment|//          }
end_comment

begin_comment
comment|//        }
end_comment

begin_comment
comment|//        for (int j = 0 ; j< parityLen; j++) {
end_comment

begin_comment
comment|//          String[] names = parityLoc[j + i * parityLen].getNames();
end_comment

begin_comment
comment|//          for (int k = 0; k< names.length; k++) {
end_comment

begin_comment
comment|//            LOG.info("Parity block location: " + names[k]);
end_comment

begin_comment
comment|//            Assert.assertTrue(locations.add(names[k]));
end_comment

begin_comment
comment|//          }
end_comment

begin_comment
comment|//        }
end_comment

begin_comment
comment|//      }
end_comment

begin_comment
comment|//    } finally {
end_comment

begin_comment
comment|//      if (cluster != null) {
end_comment

begin_comment
comment|//        cluster.shutdown();
end_comment

begin_comment
comment|//      }
end_comment

begin_comment
comment|//    }
end_comment

begin_comment
comment|//  }
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|//  /**
end_comment

begin_comment
comment|//   * Test that the har parity files will be placed at the good locations when we
end_comment

begin_comment
comment|//   * create them.
end_comment

begin_comment
comment|//   */
end_comment

begin_comment
comment|//  @Test
end_comment

begin_comment
comment|//  public void testChooseTargetForHarRaidFile() throws IOException {
end_comment

begin_comment
comment|//    setupCluster();
end_comment

begin_comment
comment|//    try {
end_comment

begin_comment
comment|//      String[] racks = {"/rack2", "/rack2", "/rack2",
end_comment

begin_comment
comment|//                        "/rack2", "/rack2", "/rack2"};
end_comment

begin_comment
comment|//      String[] hosts =
end_comment

begin_comment
comment|//        {"host2.rack2.com", "host3.rack2.com", "host4.rack2.com",
end_comment

begin_comment
comment|//         "host5.rack2.com", "host6.rack2.com", "host7.rack2.com"};
end_comment

begin_comment
comment|//      cluster.startDataNodes(conf, 6, true, null, racks, hosts, null);
end_comment

begin_comment
comment|//      String harParity = raidrsHarTempPrefix + "/dir/file";
end_comment

begin_comment
comment|//      int numBlocks = 11;
end_comment

begin_comment
comment|//      DFSTestUtil.createFile(fs, new Path(harParity), numBlocks, (short)1, 0L);
end_comment

begin_comment
comment|//      DFSTestUtil.waitReplication(fs, new Path(harParity), (short)1);
end_comment

begin_comment
comment|//      FileStatus stat = fs.getFileStatus(new Path(harParity));
end_comment

begin_comment
comment|//      BlockLocation[] loc = fs.getFileBlockLocations(stat, 0, stat.getLen());
end_comment

begin_comment
comment|//      int rsParityLength = RaidNode.rsParityLength(conf);
end_comment

begin_comment
comment|//      for (int i = 0; i< numBlocks - rsParityLength; i++) {
end_comment

begin_comment
comment|//        Set<String> locations = new HashSet<String>();
end_comment

begin_comment
comment|//        for (int j = 0; j< rsParityLength; j++) {
end_comment

begin_comment
comment|//          for (int k = 0; k< loc[i + j].getNames().length; k++) {
end_comment

begin_comment
comment|//            // verify that every adjacent 4 blocks are on differnt nodes
end_comment

begin_comment
comment|//            String name = loc[i + j].getNames()[k];
end_comment

begin_comment
comment|//            LOG.info("Har Raid block location: " + name);
end_comment

begin_comment
comment|//            Assert.assertTrue(locations.add(name));
end_comment

begin_comment
comment|//          }
end_comment

begin_comment
comment|//        }
end_comment

begin_comment
comment|//      }
end_comment

begin_comment
comment|//    } finally {
end_comment

begin_comment
comment|//      if (cluster != null) {
end_comment

begin_comment
comment|//        cluster.shutdown();
end_comment

begin_comment
comment|//      }
end_comment

begin_comment
comment|//    }
end_comment

begin_comment
comment|//  }
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|//  /**
end_comment

begin_comment
comment|//   * Test BlockPlacementPolicyRaid.CachedLocatedBlocks
end_comment

begin_comment
comment|//   * Verify that the results obtained from cache is the same as
end_comment

begin_comment
comment|//   * the results obtained directly
end_comment

begin_comment
comment|//   */
end_comment

begin_comment
comment|//  @Test
end_comment

begin_comment
comment|//  public void testCachedBlocks() throws IOException {
end_comment

begin_comment
comment|//    setupCluster();
end_comment

begin_comment
comment|//    try {
end_comment

begin_comment
comment|//      String file1 = "/dir/file1";
end_comment

begin_comment
comment|//      String file2 = "/dir/file2";
end_comment

begin_comment
comment|//      DFSTestUtil.createFile(fs, new Path(file1), 3, (short)1, 0L);
end_comment

begin_comment
comment|//      DFSTestUtil.createFile(fs, new Path(file2), 4, (short)1, 0L);
end_comment

begin_comment
comment|//      // test blocks cache
end_comment

begin_comment
comment|//      CachedLocatedBlocks cachedBlocks = new CachedLocatedBlocks(namesystem);
end_comment

begin_comment
comment|//      verifyCachedBlocksResult(cachedBlocks, namesystem, file1);
end_comment

begin_comment
comment|//      verifyCachedBlocksResult(cachedBlocks, namesystem, file1);
end_comment

begin_comment
comment|//      verifyCachedBlocksResult(cachedBlocks, namesystem, file2);
end_comment

begin_comment
comment|//      verifyCachedBlocksResult(cachedBlocks, namesystem, file2);
end_comment

begin_comment
comment|//      try {
end_comment

begin_comment
comment|//        Thread.sleep(1200L);
end_comment

begin_comment
comment|//      } catch (InterruptedException e) {
end_comment

begin_comment
comment|//      }
end_comment

begin_comment
comment|//      verifyCachedBlocksResult(cachedBlocks, namesystem, file2);
end_comment

begin_comment
comment|//      verifyCachedBlocksResult(cachedBlocks, namesystem, file1);
end_comment

begin_comment
comment|//    } finally {
end_comment

begin_comment
comment|//      if (cluster != null) {
end_comment

begin_comment
comment|//        cluster.shutdown();
end_comment

begin_comment
comment|//      }
end_comment

begin_comment
comment|//    }
end_comment

begin_comment
comment|//  }
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|//  /**
end_comment

begin_comment
comment|//   * Test BlockPlacementPolicyRaid.CachedFullPathNames
end_comment

begin_comment
comment|//   * Verify that the results obtained from cache is the same as
end_comment

begin_comment
comment|//   * the results obtained directly
end_comment

begin_comment
comment|//   */
end_comment

begin_comment
comment|//  @Test
end_comment

begin_comment
comment|//  public void testCachedPathNames() throws IOException {
end_comment

begin_comment
comment|//    setupCluster();
end_comment

begin_comment
comment|//    try {
end_comment

begin_comment
comment|//      String file1 = "/dir/file1";
end_comment

begin_comment
comment|//      String file2 = "/dir/file2";
end_comment

begin_comment
comment|//      DFSTestUtil.createFile(fs, new Path(file1), 3, (short)1, 0L);
end_comment

begin_comment
comment|//      DFSTestUtil.createFile(fs, new Path(file2), 4, (short)1, 0L);
end_comment

begin_comment
comment|//      // test full path cache
end_comment

begin_comment
comment|//      CachedFullPathNames cachedFullPathNames =
end_comment

begin_comment
comment|//          new CachedFullPathNames(namesystem);
end_comment

begin_comment
comment|//      FSInodeInfo inode1 = null;
end_comment

begin_comment
comment|//      FSInodeInfo inode2 = null;
end_comment

begin_comment
comment|//      NameNodeRaidTestUtil.readLock(namesystem.dir);
end_comment

begin_comment
comment|//      try {
end_comment

begin_comment
comment|//        inode1 = NameNodeRaidTestUtil.getNode(namesystem.dir, file1, true);
end_comment

begin_comment
comment|//        inode2 = NameNodeRaidTestUtil.getNode(namesystem.dir, file2, true);
end_comment

begin_comment
comment|//      } finally {
end_comment

begin_comment
comment|//        NameNodeRaidTestUtil.readUnLock(namesystem.dir);
end_comment

begin_comment
comment|//      }
end_comment

begin_comment
comment|//      verifyCachedFullPathNameResult(cachedFullPathNames, inode1);
end_comment

begin_comment
comment|//      verifyCachedFullPathNameResult(cachedFullPathNames, inode1);
end_comment

begin_comment
comment|//      verifyCachedFullPathNameResult(cachedFullPathNames, inode2);
end_comment

begin_comment
comment|//      verifyCachedFullPathNameResult(cachedFullPathNames, inode2);
end_comment

begin_comment
comment|//      try {
end_comment

begin_comment
comment|//        Thread.sleep(1200L);
end_comment

begin_comment
comment|//      } catch (InterruptedException e) {
end_comment

begin_comment
comment|//      }
end_comment

begin_comment
comment|//      verifyCachedFullPathNameResult(cachedFullPathNames, inode2);
end_comment

begin_comment
comment|//      verifyCachedFullPathNameResult(cachedFullPathNames, inode1);
end_comment

begin_comment
comment|//    } finally {
end_comment

begin_comment
comment|//      if (cluster != null) {
end_comment

begin_comment
comment|//        cluster.shutdown();
end_comment

begin_comment
comment|//      }
end_comment

begin_comment
comment|//    }
end_comment

begin_comment
comment|//  }
end_comment

begin_comment
comment|//  /**
end_comment

begin_comment
comment|//   * Test the result of getCompanionBlocks() on the unraided files
end_comment

begin_comment
comment|//   */
end_comment

begin_comment
comment|//  @Test
end_comment

begin_comment
comment|//  public void testGetCompanionBLocks() throws IOException {
end_comment

begin_comment
comment|//    setupCluster();
end_comment

begin_comment
comment|//    try {
end_comment

begin_comment
comment|//      String file1 = "/dir/file1";
end_comment

begin_comment
comment|//      String file2 = "/raid/dir/file2";
end_comment

begin_comment
comment|//      String file3 = "/raidrs/dir/file3";
end_comment

begin_comment
comment|//      // Set the policy to default policy to place the block in the default way
end_comment

begin_comment
comment|//      setBlockPlacementPolicy(namesystem, new BlockPlacementPolicyDefault(
end_comment

begin_comment
comment|//          conf, namesystem, namesystem.clusterMap));
end_comment

begin_comment
comment|//      DFSTestUtil.createFile(fs, new Path(file1), 3, (short)1, 0L);
end_comment

begin_comment
comment|//      DFSTestUtil.createFile(fs, new Path(file2), 4, (short)1, 0L);
end_comment

begin_comment
comment|//      DFSTestUtil.createFile(fs, new Path(file3), 8, (short)1, 0L);
end_comment

begin_comment
comment|//      Collection<LocatedBlock> companionBlocks;
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|//      companionBlocks = getCompanionBlocks(
end_comment

begin_comment
comment|//          namesystem, policy, getBlocks(namesystem, file1).get(0).getBlock());
end_comment

begin_comment
comment|//      Assert.assertTrue(companionBlocks == null || companionBlocks.size() == 0);
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|//      companionBlocks = getCompanionBlocks(
end_comment

begin_comment
comment|//          namesystem, policy, getBlocks(namesystem, file1).get(2).getBlock());
end_comment

begin_comment
comment|//      Assert.assertTrue(companionBlocks == null || companionBlocks.size() == 0);
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|//      companionBlocks = getCompanionBlocks(
end_comment

begin_comment
comment|//          namesystem, policy, getBlocks(namesystem, file2).get(0).getBlock());
end_comment

begin_comment
comment|//      Assert.assertEquals(1, companionBlocks.size());
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|//      companionBlocks = getCompanionBlocks(
end_comment

begin_comment
comment|//          namesystem, policy, getBlocks(namesystem, file2).get(3).getBlock());
end_comment

begin_comment
comment|//      Assert.assertEquals(1, companionBlocks.size());
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|//      int rsParityLength = RaidNode.rsParityLength(conf);
end_comment

begin_comment
comment|//      companionBlocks = getCompanionBlocks(
end_comment

begin_comment
comment|//          namesystem, policy, getBlocks(namesystem, file3).get(0).getBlock());
end_comment

begin_comment
comment|//      Assert.assertEquals(rsParityLength, companionBlocks.size());
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|//      companionBlocks = getCompanionBlocks(
end_comment

begin_comment
comment|//          namesystem, policy, getBlocks(namesystem, file3).get(4).getBlock());
end_comment

begin_comment
comment|//      Assert.assertEquals(rsParityLength, companionBlocks.size());
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|//      companionBlocks = getCompanionBlocks(
end_comment

begin_comment
comment|//          namesystem, policy, getBlocks(namesystem, file3).get(6).getBlock());
end_comment

begin_comment
comment|//      Assert.assertEquals(2, companionBlocks.size());
end_comment

begin_comment
comment|//    } finally {
end_comment

begin_comment
comment|//      if (cluster != null) {
end_comment

begin_comment
comment|//        cluster.shutdown();
end_comment

begin_comment
comment|//      }
end_comment

begin_comment
comment|//    }
end_comment

begin_comment
comment|//  }
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|//  static void setBlockPlacementPolicy(
end_comment

begin_comment
comment|//      FSNamesystem namesystem, BlockPlacementPolicy policy) {
end_comment

begin_comment
comment|//    namesystem.writeLock();
end_comment

begin_comment
comment|//    try {
end_comment

begin_comment
comment|//      namesystem.blockManager.replicator = policy;
end_comment

begin_comment
comment|//    } finally {
end_comment

begin_comment
comment|//      namesystem.writeUnlock();
end_comment

begin_comment
comment|//    }
end_comment

begin_comment
comment|//  }
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|//  /**
end_comment

begin_comment
comment|//   * Test BlockPlacementPolicyRaid actually deletes the correct replica.
end_comment

begin_comment
comment|//   * Start 2 datanodes and create 1 source file and its parity file.
end_comment

begin_comment
comment|//   * 1) Start host1, create the parity file with replication 1
end_comment

begin_comment
comment|//   * 2) Start host2, create the source file with replication 2
end_comment

begin_comment
comment|//   * 3) Set repliation of source file to 1
end_comment

begin_comment
comment|//   * Verify that the policy should delete the block with more companion blocks.
end_comment

begin_comment
comment|//   */
end_comment

begin_comment
comment|//  @Test
end_comment

begin_comment
comment|//  public void testDeleteReplica() throws IOException {
end_comment

begin_comment
comment|//    setupCluster();
end_comment

begin_comment
comment|//    try {
end_comment

begin_comment
comment|//      // Set the policy to default policy to place the block in the default way
end_comment

begin_comment
comment|//      setBlockPlacementPolicy(namesystem, new BlockPlacementPolicyDefault(
end_comment

begin_comment
comment|//          conf, namesystem, namesystem.clusterMap));
end_comment

begin_comment
comment|//      DatanodeDescriptor datanode1 =
end_comment

begin_comment
comment|//        NameNodeRaidTestUtil.getDatanodeMap(namesystem).values().iterator().next();
end_comment

begin_comment
comment|//      String source = "/dir/file";
end_comment

begin_comment
comment|//      String parity = xorPrefix + source;
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|//      final Path parityPath = new Path(parity);
end_comment

begin_comment
comment|//      DFSTestUtil.createFile(fs, parityPath, 3, (short)1, 0L);
end_comment

begin_comment
comment|//      DFSTestUtil.waitReplication(fs, parityPath, (short)1);
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|//      // start one more datanode
end_comment

begin_comment
comment|//      cluster.startDataNodes(conf, 1, true, null, rack2, host2, null);
end_comment

begin_comment
comment|//      DatanodeDescriptor datanode2 = null;
end_comment

begin_comment
comment|//      for (DatanodeDescriptor d : NameNodeRaidTestUtil.getDatanodeMap(namesystem).values()) {
end_comment

begin_comment
comment|//        if (!d.getName().equals(datanode1.getName())) {
end_comment

begin_comment
comment|//          datanode2 = d;
end_comment

begin_comment
comment|//        }
end_comment

begin_comment
comment|//      }
end_comment

begin_comment
comment|//      Assert.assertTrue(datanode2 != null);
end_comment

begin_comment
comment|//      cluster.waitActive();
end_comment

begin_comment
comment|//      final Path sourcePath = new Path(source);
end_comment

begin_comment
comment|//      DFSTestUtil.createFile(fs, sourcePath, 5, (short)2, 0L);
end_comment

begin_comment
comment|//      DFSTestUtil.waitReplication(fs, sourcePath, (short)2);
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|//      refreshPolicy();
end_comment

begin_comment
comment|//      Assert.assertEquals(parity,
end_comment

begin_comment
comment|//                          policy.getParityFile(source));
end_comment

begin_comment
comment|//      Assert.assertEquals(source,
end_comment

begin_comment
comment|//                          policy.getSourceFile(parity, xorPrefix));
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|//      List<LocatedBlock> sourceBlocks = getBlocks(namesystem, source);
end_comment

begin_comment
comment|//      List<LocatedBlock> parityBlocks = getBlocks(namesystem, parity);
end_comment

begin_comment
comment|//      Assert.assertEquals(5, sourceBlocks.size());
end_comment

begin_comment
comment|//      Assert.assertEquals(3, parityBlocks.size());
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|//      // verify the result of getCompanionBlocks()
end_comment

begin_comment
comment|//      Collection<LocatedBlock> companionBlocks;
end_comment

begin_comment
comment|//      companionBlocks = getCompanionBlocks(
end_comment

begin_comment
comment|//          namesystem, policy, sourceBlocks.get(0).getBlock());
end_comment

begin_comment
comment|//      verifyCompanionBlocks(companionBlocks, sourceBlocks, parityBlocks,
end_comment

begin_comment
comment|//                            new int[]{0, 1}, new int[]{0});
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|//      companionBlocks = getCompanionBlocks(
end_comment

begin_comment
comment|//          namesystem, policy, sourceBlocks.get(1).getBlock());
end_comment

begin_comment
comment|//      verifyCompanionBlocks(companionBlocks, sourceBlocks, parityBlocks,
end_comment

begin_comment
comment|//                            new int[]{0, 1}, new int[]{0});
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|//      companionBlocks = getCompanionBlocks(
end_comment

begin_comment
comment|//          namesystem, policy, sourceBlocks.get(2).getBlock());
end_comment

begin_comment
comment|//      verifyCompanionBlocks(companionBlocks, sourceBlocks, parityBlocks,
end_comment

begin_comment
comment|//                            new int[]{2, 3}, new int[]{1});
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|//      companionBlocks = getCompanionBlocks(
end_comment

begin_comment
comment|//          namesystem, policy, sourceBlocks.get(3).getBlock());
end_comment

begin_comment
comment|//      verifyCompanionBlocks(companionBlocks, sourceBlocks, parityBlocks,
end_comment

begin_comment
comment|//                            new int[]{2, 3}, new int[]{1});
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|//      companionBlocks = getCompanionBlocks(
end_comment

begin_comment
comment|//          namesystem, policy, sourceBlocks.get(4).getBlock());
end_comment

begin_comment
comment|//      verifyCompanionBlocks(companionBlocks, sourceBlocks, parityBlocks,
end_comment

begin_comment
comment|//                            new int[]{4}, new int[]{2});
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|//      companionBlocks = getCompanionBlocks(
end_comment

begin_comment
comment|//          namesystem, policy, parityBlocks.get(0).getBlock());
end_comment

begin_comment
comment|//      verifyCompanionBlocks(companionBlocks, sourceBlocks, parityBlocks,
end_comment

begin_comment
comment|//                            new int[]{0, 1}, new int[]{0});
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|//      companionBlocks = getCompanionBlocks(
end_comment

begin_comment
comment|//          namesystem, policy, parityBlocks.get(1).getBlock());
end_comment

begin_comment
comment|//      verifyCompanionBlocks(companionBlocks, sourceBlocks, parityBlocks,
end_comment

begin_comment
comment|//                            new int[]{2, 3}, new int[]{1});
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|//      companionBlocks = getCompanionBlocks(
end_comment

begin_comment
comment|//          namesystem, policy, parityBlocks.get(2).getBlock());
end_comment

begin_comment
comment|//      verifyCompanionBlocks(companionBlocks, sourceBlocks, parityBlocks,
end_comment

begin_comment
comment|//                            new int[]{4}, new int[]{2});
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|//      // Set the policy back to raid policy. We have to create a new object
end_comment

begin_comment
comment|//      // here to clear the block location cache
end_comment

begin_comment
comment|//      refreshPolicy();
end_comment

begin_comment
comment|//      setBlockPlacementPolicy(namesystem, policy);
end_comment

begin_comment
comment|//      // verify policy deletes the correct blocks. companion blocks should be
end_comment

begin_comment
comment|//      // evenly distributed.
end_comment

begin_comment
comment|//      fs.setReplication(sourcePath, (short)1);
end_comment

begin_comment
comment|//      DFSTestUtil.waitReplication(fs, sourcePath, (short)1);
end_comment

begin_comment
comment|//      Map<String, Integer> counters = new HashMap<String, Integer>();
end_comment

begin_comment
comment|//      refreshPolicy();
end_comment

begin_comment
comment|//      for (int i = 0; i< parityBlocks.size(); i++) {
end_comment

begin_comment
comment|//        companionBlocks = getCompanionBlocks(
end_comment

begin_comment
comment|//            namesystem, policy, parityBlocks.get(i).getBlock());
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|//        counters = BlockPlacementPolicyRaid.countCompanionBlocks(
end_comment

begin_comment
comment|//            companionBlocks, false);
end_comment

begin_comment
comment|//        Assert.assertTrue(counters.get(datanode1.getName())>= 1&&
end_comment

begin_comment
comment|//                          counters.get(datanode1.getName())<= 2);
end_comment

begin_comment
comment|//        Assert.assertTrue(counters.get(datanode1.getName()) +
end_comment

begin_comment
comment|//                          counters.get(datanode2.getName()) ==
end_comment

begin_comment
comment|//                          companionBlocks.size());
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|//        counters = BlockPlacementPolicyRaid.countCompanionBlocks(
end_comment

begin_comment
comment|//            companionBlocks, true);
end_comment

begin_comment
comment|//        Assert.assertTrue(counters.get(datanode1.getParent().getName())>= 1&&
end_comment

begin_comment
comment|//                          counters.get(datanode1.getParent().getName())<= 2);
end_comment

begin_comment
comment|//        Assert.assertTrue(counters.get(datanode1.getParent().getName()) +
end_comment

begin_comment
comment|//                          counters.get(datanode2.getParent().getName()) ==
end_comment

begin_comment
comment|//                          companionBlocks.size());
end_comment

begin_comment
comment|//      }
end_comment

begin_comment
comment|//    } finally {
end_comment

begin_comment
comment|//      if (cluster != null) {
end_comment

begin_comment
comment|//        cluster.shutdown();
end_comment

begin_comment
comment|//      }
end_comment

begin_comment
comment|//    }
end_comment

begin_comment
comment|//  }
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|//  // create a new BlockPlacementPolicyRaid to clear the cache
end_comment

begin_comment
comment|//  private void refreshPolicy() {
end_comment

begin_comment
comment|//      policy = new BlockPlacementPolicyRaid();
end_comment

begin_comment
comment|//      policy.initialize(conf, namesystem, namesystem.clusterMap);
end_comment

begin_comment
comment|//  }
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|//  private void verifyCompanionBlocks(Collection<LocatedBlock> companionBlocks,
end_comment

begin_comment
comment|//      List<LocatedBlock> sourceBlocks, List<LocatedBlock> parityBlocks,
end_comment

begin_comment
comment|//      int[] sourceBlockIndexes, int[] parityBlockIndexes) {
end_comment

begin_comment
comment|//    Set<ExtendedBlock> blockSet = new HashSet<ExtendedBlock>();
end_comment

begin_comment
comment|//    for (LocatedBlock b : companionBlocks) {
end_comment

begin_comment
comment|//      blockSet.add(b.getBlock());
end_comment

begin_comment
comment|//    }
end_comment

begin_comment
comment|//    Assert.assertEquals(sourceBlockIndexes.length + parityBlockIndexes.length,
end_comment

begin_comment
comment|//                        blockSet.size());
end_comment

begin_comment
comment|//    for (int index : sourceBlockIndexes) {
end_comment

begin_comment
comment|//      Assert.assertTrue(blockSet.contains(sourceBlocks.get(index).getBlock()));
end_comment

begin_comment
comment|//    }
end_comment

begin_comment
comment|//    for (int index : parityBlockIndexes) {
end_comment

begin_comment
comment|//      Assert.assertTrue(blockSet.contains(parityBlocks.get(index).getBlock()));
end_comment

begin_comment
comment|//    }
end_comment

begin_comment
comment|//  }
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|//  private void verifyCachedFullPathNameResult(
end_comment

begin_comment
comment|//      CachedFullPathNames cachedFullPathNames, FSInodeInfo inode)
end_comment

begin_comment
comment|//  throws IOException {
end_comment

begin_comment
comment|//    String res1 = inode.getFullPathName();
end_comment

begin_comment
comment|//    String res2 = cachedFullPathNames.get(inode);
end_comment

begin_comment
comment|//    LOG.info("Actual path name: " + res1);
end_comment

begin_comment
comment|//    LOG.info("Cached path name: " + res2);
end_comment

begin_comment
comment|//    Assert.assertEquals(cachedFullPathNames.get(inode),
end_comment

begin_comment
comment|//                        inode.getFullPathName());
end_comment

begin_comment
comment|//  }
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|//  private void verifyCachedBlocksResult(CachedLocatedBlocks cachedBlocks,
end_comment

begin_comment
comment|//      FSNamesystem namesystem, String file) throws IOException{
end_comment

begin_comment
comment|//    long len = NameNodeRaidUtil.getFileInfo(namesystem, file, true).getLen();
end_comment

begin_comment
comment|//    List<LocatedBlock> res1 = NameNodeRaidUtil.getBlockLocations(namesystem,
end_comment

begin_comment
comment|//        file, 0L, len, false, false).getLocatedBlocks();
end_comment

begin_comment
comment|//    List<LocatedBlock> res2 = cachedBlocks.get(file);
end_comment

begin_comment
comment|//    for (int i = 0; i< res1.size(); i++) {
end_comment

begin_comment
comment|//      LOG.info("Actual block: " + res1.get(i).getBlock());
end_comment

begin_comment
comment|//      LOG.info("Cached block: " + res2.get(i).getBlock());
end_comment

begin_comment
comment|//      Assert.assertEquals(res1.get(i).getBlock(), res2.get(i).getBlock());
end_comment

begin_comment
comment|//    }
end_comment

begin_comment
comment|//  }
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|//  private Collection<LocatedBlock> getCompanionBlocks(
end_comment

begin_comment
comment|//      FSNamesystem namesystem, BlockPlacementPolicyRaid policy,
end_comment

begin_comment
comment|//      ExtendedBlock block) throws IOException {
end_comment

begin_comment
comment|//    INodeFile inode = namesystem.blockManager.blocksMap.getINode(block
end_comment

begin_comment
comment|//        .getLocalBlock());
end_comment

begin_comment
comment|//    FileType type = policy.getFileType(inode.getFullPathName());
end_comment

begin_comment
comment|//    return policy.getCompanionBlocks(inode.getFullPathName(), type,
end_comment

begin_comment
comment|//        block.getLocalBlock());
end_comment

begin_comment
comment|//  }
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|//  private List<LocatedBlock> getBlocks(FSNamesystem namesystem, String file)
end_comment

begin_comment
comment|//      throws IOException {
end_comment

begin_comment
comment|//    long len = NameNodeRaidUtil.getFileInfo(namesystem, file, true).getLen();
end_comment

begin_comment
comment|//    return NameNodeRaidUtil.getBlockLocations(namesystem,
end_comment

begin_comment
comment|//               file, 0, len, false, false).getLocatedBlocks();
end_comment

begin_comment
comment|//  }
end_comment

begin_comment
comment|//}
end_comment

end_unit

