begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.blockmanagement
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|blockmanagement
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|Assert
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|conf
operator|.
name|Configuration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|BlockLocation
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|FileStatus
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|FileSystem
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSTestUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|MiniDFSCluster
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|protocol
operator|.
name|ExtendedBlock
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|protocol
operator|.
name|LocatedBlock
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|blockmanagement
operator|.
name|BlockPlacementPolicyRaid
operator|.
name|CachedFullPathNames
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|blockmanagement
operator|.
name|BlockPlacementPolicyRaid
operator|.
name|CachedLocatedBlocks
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|blockmanagement
operator|.
name|BlockPlacementPolicyRaid
operator|.
name|FileType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|namenode
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|raid
operator|.
name|RaidNode
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_class
DECL|class|TestBlockPlacementPolicyRaid
specifier|public
class|class
name|TestBlockPlacementPolicyRaid
block|{
annotation|@
name|Test
DECL|method|testFoo ()
specifier|public
name|void
name|testFoo
parameter_list|()
block|{   }
comment|//  private Configuration conf = null;
comment|//  private MiniDFSCluster cluster = null;
comment|//  private FSNamesystem namesystem = null;
comment|//  private BlockPlacementPolicyRaid policy = null;
comment|//  private FileSystem fs = null;
comment|//  String[] rack1 = {"/rack1"};
comment|//  String[] rack2 = {"/rack2"};
comment|//  String[] host1 = {"host1.rack1.com"};
comment|//  String[] host2 = {"host2.rack2.com"};
comment|//  String xorPrefix = null;
comment|//  String raidTempPrefix = null;
comment|//  String raidrsTempPrefix = null;
comment|//  String raidrsHarTempPrefix = null;
comment|//
comment|//  final static Log LOG =
comment|//      LogFactory.getLog(TestBlockPlacementPolicyRaid.class);
comment|//
comment|//  protected void setupCluster() throws IOException {
comment|//    conf = new Configuration();
comment|//    conf.setLong(DFSConfigKeys.DFS_BLOCKREPORT_INTERVAL_MSEC_KEY, 1000L);
comment|//    conf.set("dfs.replication.pending.timeout.sec", "2");
comment|//    conf.setLong(DFSConfigKeys.DFS_BLOCK_SIZE_KEY, 1L);
comment|//    conf.set("dfs.block.replicator.classname",
comment|//             "org.apache.hadoop.hdfs.server.namenode.BlockPlacementPolicyRaid");
comment|//    conf.set(RaidNode.STRIPE_LENGTH_KEY, "2");
comment|//    conf.set(RaidNode.RS_PARITY_LENGTH_KEY, "3");
comment|//    conf.setInt(DFSConfigKeys.DFS_BYTES_PER_CHECKSUM_KEY, 1);
comment|//    // start the cluster with one datanode first
comment|//    cluster = new MiniDFSCluster.Builder(conf).numDataNodes(1).
comment|//        format(true).racks(rack1).hosts(host1).build();
comment|//    cluster.waitActive();
comment|//    namesystem = cluster.getNameNode().getNamesystem();
comment|//    Assert.assertTrue("BlockPlacementPolicy type is not correct.",
comment|//      namesystem.blockManager.replicator instanceof BlockPlacementPolicyRaid);
comment|//    policy = (BlockPlacementPolicyRaid) namesystem.blockManager.replicator;
comment|//    fs = cluster.getFileSystem();
comment|//    xorPrefix = RaidNode.xorDestinationPath(conf).toUri().getPath();
comment|//    raidTempPrefix = RaidNode.xorTempPrefix(conf);
comment|//    raidrsTempPrefix = RaidNode.rsTempPrefix(conf);
comment|//    raidrsHarTempPrefix = RaidNode.rsHarTempPrefix(conf);
comment|//  }
comment|//
comment|//  /**
comment|//   * Test that the parity files will be placed at the good locations when we
comment|//   * create them.
comment|//   */
comment|//  @Test
comment|//  public void testChooseTargetForRaidFile() throws IOException {
comment|//    setupCluster();
comment|//    try {
comment|//      String src = "/dir/file";
comment|//      String parity = raidrsTempPrefix + src;
comment|//      DFSTestUtil.createFile(fs, new Path(src), 4, (short)1, 0L);
comment|//      DFSTestUtil.waitReplication(fs, new Path(src), (short)1);
comment|//      refreshPolicy();
comment|//      setBlockPlacementPolicy(namesystem, policy);
comment|//      // start 3 more datanodes
comment|//      String[] racks = {"/rack2", "/rack2", "/rack2",
comment|//                        "/rack2", "/rack2", "/rack2"};
comment|//      String[] hosts =
comment|//        {"host2.rack2.com", "host3.rack2.com", "host4.rack2.com",
comment|//         "host5.rack2.com", "host6.rack2.com", "host7.rack2.com"};
comment|//      cluster.startDataNodes(conf, 6, true, null, racks, hosts, null);
comment|//      int numBlocks = 6;
comment|//      DFSTestUtil.createFile(fs, new Path(parity), numBlocks, (short)2, 0L);
comment|//      DFSTestUtil.waitReplication(fs, new Path(parity), (short)2);
comment|//      FileStatus srcStat = fs.getFileStatus(new Path(src));
comment|//      BlockLocation[] srcLoc =
comment|//        fs.getFileBlockLocations(srcStat, 0, srcStat.getLen());
comment|//      FileStatus parityStat = fs.getFileStatus(new Path(parity));
comment|//      BlockLocation[] parityLoc =
comment|//          fs.getFileBlockLocations(parityStat, 0, parityStat.getLen());
comment|//      int parityLen = RaidNode.rsParityLength(conf);
comment|//      for (int i = 0; i< numBlocks / parityLen; i++) {
comment|//        Set<String> locations = new HashSet<String>();
comment|//        for (int j = 0; j< srcLoc.length; j++) {
comment|//          String [] names = srcLoc[j].getNames();
comment|//          for (int k = 0; k< names.length; k++) {
comment|//            LOG.info("Source block location: " + names[k]);
comment|//            locations.add(names[k]);
comment|//          }
comment|//        }
comment|//        for (int j = 0 ; j< parityLen; j++) {
comment|//          String[] names = parityLoc[j + i * parityLen].getNames();
comment|//          for (int k = 0; k< names.length; k++) {
comment|//            LOG.info("Parity block location: " + names[k]);
comment|//            Assert.assertTrue(locations.add(names[k]));
comment|//          }
comment|//        }
comment|//      }
comment|//    } finally {
comment|//      if (cluster != null) {
comment|//        cluster.shutdown();
comment|//      }
comment|//    }
comment|//  }
comment|//
comment|//  /**
comment|//   * Test that the har parity files will be placed at the good locations when we
comment|//   * create them.
comment|//   */
comment|//  @Test
comment|//  public void testChooseTargetForHarRaidFile() throws IOException {
comment|//    setupCluster();
comment|//    try {
comment|//      String[] racks = {"/rack2", "/rack2", "/rack2",
comment|//                        "/rack2", "/rack2", "/rack2"};
comment|//      String[] hosts =
comment|//        {"host2.rack2.com", "host3.rack2.com", "host4.rack2.com",
comment|//         "host5.rack2.com", "host6.rack2.com", "host7.rack2.com"};
comment|//      cluster.startDataNodes(conf, 6, true, null, racks, hosts, null);
comment|//      String harParity = raidrsHarTempPrefix + "/dir/file";
comment|//      int numBlocks = 11;
comment|//      DFSTestUtil.createFile(fs, new Path(harParity), numBlocks, (short)1, 0L);
comment|//      DFSTestUtil.waitReplication(fs, new Path(harParity), (short)1);
comment|//      FileStatus stat = fs.getFileStatus(new Path(harParity));
comment|//      BlockLocation[] loc = fs.getFileBlockLocations(stat, 0, stat.getLen());
comment|//      int rsParityLength = RaidNode.rsParityLength(conf);
comment|//      for (int i = 0; i< numBlocks - rsParityLength; i++) {
comment|//        Set<String> locations = new HashSet<String>();
comment|//        for (int j = 0; j< rsParityLength; j++) {
comment|//          for (int k = 0; k< loc[i + j].getNames().length; k++) {
comment|//            // verify that every adjacent 4 blocks are on differnt nodes
comment|//            String name = loc[i + j].getNames()[k];
comment|//            LOG.info("Har Raid block location: " + name);
comment|//            Assert.assertTrue(locations.add(name));
comment|//          }
comment|//        }
comment|//      }
comment|//    } finally {
comment|//      if (cluster != null) {
comment|//        cluster.shutdown();
comment|//      }
comment|//    }
comment|//  }
comment|//
comment|//  /**
comment|//   * Test BlockPlacementPolicyRaid.CachedLocatedBlocks
comment|//   * Verify that the results obtained from cache is the same as
comment|//   * the results obtained directly
comment|//   */
comment|//  @Test
comment|//  public void testCachedBlocks() throws IOException {
comment|//    setupCluster();
comment|//    try {
comment|//      String file1 = "/dir/file1";
comment|//      String file2 = "/dir/file2";
comment|//      DFSTestUtil.createFile(fs, new Path(file1), 3, (short)1, 0L);
comment|//      DFSTestUtil.createFile(fs, new Path(file2), 4, (short)1, 0L);
comment|//      // test blocks cache
comment|//      CachedLocatedBlocks cachedBlocks = new CachedLocatedBlocks(namesystem);
comment|//      verifyCachedBlocksResult(cachedBlocks, namesystem, file1);
comment|//      verifyCachedBlocksResult(cachedBlocks, namesystem, file1);
comment|//      verifyCachedBlocksResult(cachedBlocks, namesystem, file2);
comment|//      verifyCachedBlocksResult(cachedBlocks, namesystem, file2);
comment|//      try {
comment|//        Thread.sleep(1200L);
comment|//      } catch (InterruptedException e) {
comment|//      }
comment|//      verifyCachedBlocksResult(cachedBlocks, namesystem, file2);
comment|//      verifyCachedBlocksResult(cachedBlocks, namesystem, file1);
comment|//    } finally {
comment|//      if (cluster != null) {
comment|//        cluster.shutdown();
comment|//      }
comment|//    }
comment|//  }
comment|//
comment|//  /**
comment|//   * Test BlockPlacementPolicyRaid.CachedFullPathNames
comment|//   * Verify that the results obtained from cache is the same as
comment|//   * the results obtained directly
comment|//   */
comment|//  @Test
comment|//  public void testCachedPathNames() throws IOException {
comment|//    setupCluster();
comment|//    try {
comment|//      String file1 = "/dir/file1";
comment|//      String file2 = "/dir/file2";
comment|//      DFSTestUtil.createFile(fs, new Path(file1), 3, (short)1, 0L);
comment|//      DFSTestUtil.createFile(fs, new Path(file2), 4, (short)1, 0L);
comment|//      // test full path cache
comment|//      CachedFullPathNames cachedFullPathNames =
comment|//          new CachedFullPathNames(namesystem);
comment|//      FSInodeInfo inode1 = null;
comment|//      FSInodeInfo inode2 = null;
comment|//      NameNodeRaidTestUtil.readLock(namesystem.dir);
comment|//      try {
comment|//        inode1 = NameNodeRaidTestUtil.getNode(namesystem.dir, file1, true);
comment|//        inode2 = NameNodeRaidTestUtil.getNode(namesystem.dir, file2, true);
comment|//      } finally {
comment|//        NameNodeRaidTestUtil.readUnLock(namesystem.dir);
comment|//      }
comment|//      verifyCachedFullPathNameResult(cachedFullPathNames, inode1);
comment|//      verifyCachedFullPathNameResult(cachedFullPathNames, inode1);
comment|//      verifyCachedFullPathNameResult(cachedFullPathNames, inode2);
comment|//      verifyCachedFullPathNameResult(cachedFullPathNames, inode2);
comment|//      try {
comment|//        Thread.sleep(1200L);
comment|//      } catch (InterruptedException e) {
comment|//      }
comment|//      verifyCachedFullPathNameResult(cachedFullPathNames, inode2);
comment|//      verifyCachedFullPathNameResult(cachedFullPathNames, inode1);
comment|//    } finally {
comment|//      if (cluster != null) {
comment|//        cluster.shutdown();
comment|//      }
comment|//    }
comment|//  }
comment|//  /**
comment|//   * Test the result of getCompanionBlocks() on the unraided files
comment|//   */
comment|//  @Test
comment|//  public void testGetCompanionBLocks() throws IOException {
comment|//    setupCluster();
comment|//    try {
comment|//      String file1 = "/dir/file1";
comment|//      String file2 = "/raid/dir/file2";
comment|//      String file3 = "/raidrs/dir/file3";
comment|//      // Set the policy to default policy to place the block in the default way
comment|//      setBlockPlacementPolicy(namesystem, new BlockPlacementPolicyDefault(
comment|//          conf, namesystem, namesystem.clusterMap));
comment|//      DFSTestUtil.createFile(fs, new Path(file1), 3, (short)1, 0L);
comment|//      DFSTestUtil.createFile(fs, new Path(file2), 4, (short)1, 0L);
comment|//      DFSTestUtil.createFile(fs, new Path(file3), 8, (short)1, 0L);
comment|//      Collection<LocatedBlock> companionBlocks;
comment|//
comment|//      companionBlocks = getCompanionBlocks(
comment|//          namesystem, policy, getBlocks(namesystem, file1).get(0).getBlock());
comment|//      Assert.assertTrue(companionBlocks == null || companionBlocks.size() == 0);
comment|//
comment|//      companionBlocks = getCompanionBlocks(
comment|//          namesystem, policy, getBlocks(namesystem, file1).get(2).getBlock());
comment|//      Assert.assertTrue(companionBlocks == null || companionBlocks.size() == 0);
comment|//
comment|//      companionBlocks = getCompanionBlocks(
comment|//          namesystem, policy, getBlocks(namesystem, file2).get(0).getBlock());
comment|//      Assert.assertEquals(1, companionBlocks.size());
comment|//
comment|//      companionBlocks = getCompanionBlocks(
comment|//          namesystem, policy, getBlocks(namesystem, file2).get(3).getBlock());
comment|//      Assert.assertEquals(1, companionBlocks.size());
comment|//
comment|//      int rsParityLength = RaidNode.rsParityLength(conf);
comment|//      companionBlocks = getCompanionBlocks(
comment|//          namesystem, policy, getBlocks(namesystem, file3).get(0).getBlock());
comment|//      Assert.assertEquals(rsParityLength, companionBlocks.size());
comment|//
comment|//      companionBlocks = getCompanionBlocks(
comment|//          namesystem, policy, getBlocks(namesystem, file3).get(4).getBlock());
comment|//      Assert.assertEquals(rsParityLength, companionBlocks.size());
comment|//
comment|//      companionBlocks = getCompanionBlocks(
comment|//          namesystem, policy, getBlocks(namesystem, file3).get(6).getBlock());
comment|//      Assert.assertEquals(2, companionBlocks.size());
comment|//    } finally {
comment|//      if (cluster != null) {
comment|//        cluster.shutdown();
comment|//      }
comment|//    }
comment|//  }
comment|//
comment|//  static void setBlockPlacementPolicy(
comment|//      FSNamesystem namesystem, BlockPlacementPolicy policy) {
comment|//    namesystem.writeLock();
comment|//    try {
comment|//      namesystem.blockManager.replicator = policy;
comment|//    } finally {
comment|//      namesystem.writeUnlock();
comment|//    }
comment|//  }
comment|//
comment|//  /**
comment|//   * Test BlockPlacementPolicyRaid actually deletes the correct replica.
comment|//   * Start 2 datanodes and create 1 source file and its parity file.
comment|//   * 1) Start host1, create the parity file with replication 1
comment|//   * 2) Start host2, create the source file with replication 2
comment|//   * 3) Set repliation of source file to 1
comment|//   * Verify that the policy should delete the block with more companion blocks.
comment|//   */
comment|//  @Test
comment|//  public void testDeleteReplica() throws IOException {
comment|//    setupCluster();
comment|//    try {
comment|//      // Set the policy to default policy to place the block in the default way
comment|//      setBlockPlacementPolicy(namesystem, new BlockPlacementPolicyDefault(
comment|//          conf, namesystem, namesystem.clusterMap));
comment|//      DatanodeDescriptor datanode1 =
comment|//        NameNodeRaidTestUtil.getDatanodeMap(namesystem).values().iterator().next();
comment|//      String source = "/dir/file";
comment|//      String parity = xorPrefix + source;
comment|//
comment|//      final Path parityPath = new Path(parity);
comment|//      DFSTestUtil.createFile(fs, parityPath, 3, (short)1, 0L);
comment|//      DFSTestUtil.waitReplication(fs, parityPath, (short)1);
comment|//
comment|//      // start one more datanode
comment|//      cluster.startDataNodes(conf, 1, true, null, rack2, host2, null);
comment|//      DatanodeDescriptor datanode2 = null;
comment|//      for (DatanodeDescriptor d : NameNodeRaidTestUtil.getDatanodeMap(namesystem).values()) {
comment|//        if (!d.getName().equals(datanode1.getName())) {
comment|//          datanode2 = d;
comment|//        }
comment|//      }
comment|//      Assert.assertTrue(datanode2 != null);
comment|//      cluster.waitActive();
comment|//      final Path sourcePath = new Path(source);
comment|//      DFSTestUtil.createFile(fs, sourcePath, 5, (short)2, 0L);
comment|//      DFSTestUtil.waitReplication(fs, sourcePath, (short)2);
comment|//
comment|//      refreshPolicy();
comment|//      Assert.assertEquals(parity,
comment|//                          policy.getParityFile(source));
comment|//      Assert.assertEquals(source,
comment|//                          policy.getSourceFile(parity, xorPrefix));
comment|//
comment|//      List<LocatedBlock> sourceBlocks = getBlocks(namesystem, source);
comment|//      List<LocatedBlock> parityBlocks = getBlocks(namesystem, parity);
comment|//      Assert.assertEquals(5, sourceBlocks.size());
comment|//      Assert.assertEquals(3, parityBlocks.size());
comment|//
comment|//      // verify the result of getCompanionBlocks()
comment|//      Collection<LocatedBlock> companionBlocks;
comment|//      companionBlocks = getCompanionBlocks(
comment|//          namesystem, policy, sourceBlocks.get(0).getBlock());
comment|//      verifyCompanionBlocks(companionBlocks, sourceBlocks, parityBlocks,
comment|//                            new int[]{0, 1}, new int[]{0});
comment|//
comment|//      companionBlocks = getCompanionBlocks(
comment|//          namesystem, policy, sourceBlocks.get(1).getBlock());
comment|//      verifyCompanionBlocks(companionBlocks, sourceBlocks, parityBlocks,
comment|//                            new int[]{0, 1}, new int[]{0});
comment|//
comment|//      companionBlocks = getCompanionBlocks(
comment|//          namesystem, policy, sourceBlocks.get(2).getBlock());
comment|//      verifyCompanionBlocks(companionBlocks, sourceBlocks, parityBlocks,
comment|//                            new int[]{2, 3}, new int[]{1});
comment|//
comment|//      companionBlocks = getCompanionBlocks(
comment|//          namesystem, policy, sourceBlocks.get(3).getBlock());
comment|//      verifyCompanionBlocks(companionBlocks, sourceBlocks, parityBlocks,
comment|//                            new int[]{2, 3}, new int[]{1});
comment|//
comment|//      companionBlocks = getCompanionBlocks(
comment|//          namesystem, policy, sourceBlocks.get(4).getBlock());
comment|//      verifyCompanionBlocks(companionBlocks, sourceBlocks, parityBlocks,
comment|//                            new int[]{4}, new int[]{2});
comment|//
comment|//      companionBlocks = getCompanionBlocks(
comment|//          namesystem, policy, parityBlocks.get(0).getBlock());
comment|//      verifyCompanionBlocks(companionBlocks, sourceBlocks, parityBlocks,
comment|//                            new int[]{0, 1}, new int[]{0});
comment|//
comment|//      companionBlocks = getCompanionBlocks(
comment|//          namesystem, policy, parityBlocks.get(1).getBlock());
comment|//      verifyCompanionBlocks(companionBlocks, sourceBlocks, parityBlocks,
comment|//                            new int[]{2, 3}, new int[]{1});
comment|//
comment|//      companionBlocks = getCompanionBlocks(
comment|//          namesystem, policy, parityBlocks.get(2).getBlock());
comment|//      verifyCompanionBlocks(companionBlocks, sourceBlocks, parityBlocks,
comment|//                            new int[]{4}, new int[]{2});
comment|//
comment|//      // Set the policy back to raid policy. We have to create a new object
comment|//      // here to clear the block location cache
comment|//      refreshPolicy();
comment|//      setBlockPlacementPolicy(namesystem, policy);
comment|//      // verify policy deletes the correct blocks. companion blocks should be
comment|//      // evenly distributed.
comment|//      fs.setReplication(sourcePath, (short)1);
comment|//      DFSTestUtil.waitReplication(fs, sourcePath, (short)1);
comment|//      Map<String, Integer> counters = new HashMap<String, Integer>();
comment|//      refreshPolicy();
comment|//      for (int i = 0; i< parityBlocks.size(); i++) {
comment|//        companionBlocks = getCompanionBlocks(
comment|//            namesystem, policy, parityBlocks.get(i).getBlock());
comment|//
comment|//        counters = BlockPlacementPolicyRaid.countCompanionBlocks(
comment|//            companionBlocks, false);
comment|//        Assert.assertTrue(counters.get(datanode1.getName())>= 1&&
comment|//                          counters.get(datanode1.getName())<= 2);
comment|//        Assert.assertTrue(counters.get(datanode1.getName()) +
comment|//                          counters.get(datanode2.getName()) ==
comment|//                          companionBlocks.size());
comment|//
comment|//        counters = BlockPlacementPolicyRaid.countCompanionBlocks(
comment|//            companionBlocks, true);
comment|//        Assert.assertTrue(counters.get(datanode1.getParent().getName())>= 1&&
comment|//                          counters.get(datanode1.getParent().getName())<= 2);
comment|//        Assert.assertTrue(counters.get(datanode1.getParent().getName()) +
comment|//                          counters.get(datanode2.getParent().getName()) ==
comment|//                          companionBlocks.size());
comment|//      }
comment|//    } finally {
comment|//      if (cluster != null) {
comment|//        cluster.shutdown();
comment|//      }
comment|//    }
comment|//  }
comment|//
comment|//  // create a new BlockPlacementPolicyRaid to clear the cache
comment|//  private void refreshPolicy() {
comment|//      policy = new BlockPlacementPolicyRaid();
comment|//      policy.initialize(conf, namesystem, namesystem.clusterMap);
comment|//  }
comment|//
comment|//  private void verifyCompanionBlocks(Collection<LocatedBlock> companionBlocks,
comment|//      List<LocatedBlock> sourceBlocks, List<LocatedBlock> parityBlocks,
comment|//      int[] sourceBlockIndexes, int[] parityBlockIndexes) {
comment|//    Set<ExtendedBlock> blockSet = new HashSet<ExtendedBlock>();
comment|//    for (LocatedBlock b : companionBlocks) {
comment|//      blockSet.add(b.getBlock());
comment|//    }
comment|//    Assert.assertEquals(sourceBlockIndexes.length + parityBlockIndexes.length,
comment|//                        blockSet.size());
comment|//    for (int index : sourceBlockIndexes) {
comment|//      Assert.assertTrue(blockSet.contains(sourceBlocks.get(index).getBlock()));
comment|//    }
comment|//    for (int index : parityBlockIndexes) {
comment|//      Assert.assertTrue(blockSet.contains(parityBlocks.get(index).getBlock()));
comment|//    }
comment|//  }
comment|//
comment|//  private void verifyCachedFullPathNameResult(
comment|//      CachedFullPathNames cachedFullPathNames, FSInodeInfo inode)
comment|//  throws IOException {
comment|//    String res1 = inode.getFullPathName();
comment|//    String res2 = cachedFullPathNames.get(inode);
comment|//    LOG.info("Actual path name: " + res1);
comment|//    LOG.info("Cached path name: " + res2);
comment|//    Assert.assertEquals(cachedFullPathNames.get(inode),
comment|//                        inode.getFullPathName());
comment|//  }
comment|//
comment|//  private void verifyCachedBlocksResult(CachedLocatedBlocks cachedBlocks,
comment|//      FSNamesystem namesystem, String file) throws IOException{
comment|//    long len = NameNodeRaidUtil.getFileInfo(namesystem, file, true).getLen();
comment|//    List<LocatedBlock> res1 = NameNodeRaidUtil.getBlockLocations(namesystem,
comment|//        file, 0L, len, false, false).getLocatedBlocks();
comment|//    List<LocatedBlock> res2 = cachedBlocks.get(file);
comment|//    for (int i = 0; i< res1.size(); i++) {
comment|//      LOG.info("Actual block: " + res1.get(i).getBlock());
comment|//      LOG.info("Cached block: " + res2.get(i).getBlock());
comment|//      Assert.assertEquals(res1.get(i).getBlock(), res2.get(i).getBlock());
comment|//    }
comment|//  }
comment|//
comment|//  private Collection<LocatedBlock> getCompanionBlocks(
comment|//      FSNamesystem namesystem, BlockPlacementPolicyRaid policy,
comment|//      ExtendedBlock block) throws IOException {
comment|//    INodeFile inode = namesystem.blockManager.blocksMap.getINode(block
comment|//        .getLocalBlock());
comment|//    FileType type = policy.getFileType(inode.getFullPathName());
comment|//    return policy.getCompanionBlocks(inode.getFullPathName(), type,
comment|//        block.getLocalBlock());
comment|//  }
comment|//
comment|//  private List<LocatedBlock> getBlocks(FSNamesystem namesystem, String file)
comment|//      throws IOException {
comment|//    long len = NameNodeRaidUtil.getFileInfo(namesystem, file, true).getLen();
comment|//    return NameNodeRaidUtil.getBlockLocations(namesystem,
comment|//               file, 0, len, false, false).getLocatedBlocks();
comment|//  }
block|}
end_class

end_unit

