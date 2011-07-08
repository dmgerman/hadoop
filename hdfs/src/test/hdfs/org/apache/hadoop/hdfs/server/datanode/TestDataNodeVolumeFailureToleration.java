begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode
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
name|datanode
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

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
name|FileUtil
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
name|server
operator|.
name|namenode
operator|.
name|FSNamesystem
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
name|HdfsConfiguration
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
name|commons
operator|.
name|logging
operator|.
name|impl
operator|.
name|Log4JLogger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Level
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|*
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assume
operator|.
name|assumeTrue
import|;
end_import

begin_comment
comment|/**  * Test the ability of a DN to tolerate volume failures.  */
end_comment

begin_class
DECL|class|TestDataNodeVolumeFailureToleration
specifier|public
class|class
name|TestDataNodeVolumeFailureToleration
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TestDataNodeVolumeFailureToleration
operator|.
name|class
argument_list|)
decl_stmt|;
block|{
operator|(
operator|(
name|Log4JLogger
operator|)
name|TestDataNodeVolumeFailureToleration
operator|.
name|LOG
operator|)
operator|.
name|getLogger
argument_list|()
operator|.
name|setLevel
argument_list|(
name|Level
operator|.
name|ALL
argument_list|)
expr_stmt|;
block|}
DECL|field|fs
specifier|private
name|FileSystem
name|fs
decl_stmt|;
DECL|field|cluster
specifier|private
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|dataDir
specifier|private
name|String
name|dataDir
decl_stmt|;
comment|// Sleep at least 3 seconds (a 1s heartbeat plus padding) to allow
comment|// for heartbeats to propagate from the datanodes to the namenode.
DECL|field|WAIT_FOR_HEARTBEATS
specifier|final
name|int
name|WAIT_FOR_HEARTBEATS
init|=
literal|3000
decl_stmt|;
comment|// Wait at least (2 * re-check + 10 * heartbeat) seconds for
comment|// a datanode to be considered dead by the namenode.
DECL|field|WAIT_FOR_DEATH
specifier|final
name|int
name|WAIT_FOR_DEATH
init|=
literal|15000
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|=
operator|new
name|HdfsConfiguration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BLOCK_SIZE_KEY
argument_list|,
literal|512L
argument_list|)
expr_stmt|;
comment|/*      * Lower the DN heartbeat, DF rate, and recheck interval to one second      * so state about failures and datanode death propagates faster.      */
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_HEARTBEAT_INTERVAL_KEY
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DF_INTERVAL_KEY
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_HEARTBEAT_RECHECK_INTERVAL_KEY
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
comment|// Allow a single volume failure (there are two volumes)
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_FAILED_VOLUMES_TOLERATED_KEY
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|cluster
operator|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|numDataNodes
argument_list|(
literal|1
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|fs
operator|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
name|dataDir
operator|=
name|cluster
operator|.
name|getDataDirectory
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|3
condition|;
name|i
operator|++
control|)
block|{
operator|new
name|File
argument_list|(
name|dataDir
argument_list|,
literal|"data"
operator|+
operator|(
literal|2
operator|*
name|i
operator|+
literal|1
operator|)
argument_list|)
operator|.
name|setExecutable
argument_list|(
literal|true
argument_list|)
expr_stmt|;
operator|new
name|File
argument_list|(
name|dataDir
argument_list|,
literal|"data"
operator|+
operator|(
literal|2
operator|*
name|i
operator|+
literal|2
operator|)
argument_list|)
operator|.
name|setExecutable
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
comment|/**    * Test the DFS_DATANODE_FAILED_VOLUMES_TOLERATED_KEY configuration    * option, ie the DN tolerates a failed-to-use scenario during    * its start-up.    */
annotation|@
name|Test
DECL|method|testValidVolumesAtStartup ()
specifier|public
name|void
name|testValidVolumesAtStartup
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeTrue
argument_list|(
operator|!
name|System
operator|.
name|getProperty
argument_list|(
literal|"os.name"
argument_list|)
operator|.
name|startsWith
argument_list|(
literal|"Windows"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Make sure no DNs are running.
name|cluster
operator|.
name|shutdownDataNodes
argument_list|()
expr_stmt|;
comment|// Bring up a datanode with two default data dirs, but with one bad one.
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_FAILED_VOLUMES_TOLERATED_KEY
argument_list|,
literal|1
argument_list|)
expr_stmt|;
comment|// We use subdirectories 0 and 1 in order to have only a single
comment|// data dir's parent inject a failure.
name|File
name|tld
init|=
operator|new
name|File
argument_list|(
name|MiniDFSCluster
operator|.
name|getBaseDirectory
argument_list|()
argument_list|,
literal|"badData"
argument_list|)
decl_stmt|;
name|File
name|dataDir1
init|=
operator|new
name|File
argument_list|(
name|tld
argument_list|,
literal|"data1"
argument_list|)
decl_stmt|;
name|File
name|dataDir1Actual
init|=
operator|new
name|File
argument_list|(
name|dataDir1
argument_list|,
literal|"1"
argument_list|)
decl_stmt|;
name|dataDir1Actual
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
comment|// Force an IOE to occur on one of the dfs.data.dir.
name|File
name|dataDir2
init|=
operator|new
name|File
argument_list|(
name|tld
argument_list|,
literal|"data2"
argument_list|)
decl_stmt|;
name|prepareDirToFail
argument_list|(
name|dataDir2
argument_list|)
expr_stmt|;
name|File
name|dataDir2Actual
init|=
operator|new
name|File
argument_list|(
name|dataDir2
argument_list|,
literal|"2"
argument_list|)
decl_stmt|;
comment|// Start one DN, with manually managed DN dir
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_DATA_DIR_KEY
argument_list|,
name|dataDir1Actual
operator|.
name|getPath
argument_list|()
operator|+
literal|","
operator|+
name|dataDir2Actual
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|startDataNodes
argument_list|(
name|conf
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
try|try
block|{
name|assertTrue
argument_list|(
literal|"The DN should have started up fine."
argument_list|,
name|cluster
operator|.
name|isDataNodeUp
argument_list|()
argument_list|)
expr_stmt|;
name|DataNode
name|dn
init|=
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|String
name|si
init|=
name|dn
operator|.
name|getFSDataset
argument_list|()
operator|.
name|getStorageInfo
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"The DN should have started with this directory"
argument_list|,
name|si
operator|.
name|contains
argument_list|(
name|dataDir1Actual
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"The DN shouldn't have a bad directory."
argument_list|,
name|si
operator|.
name|contains
argument_list|(
name|dataDir2Actual
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|cluster
operator|.
name|shutdownDataNodes
argument_list|()
expr_stmt|;
name|FileUtil
operator|.
name|chmod
argument_list|(
name|dataDir2
operator|.
name|toString
argument_list|()
argument_list|,
literal|"755"
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Test the DFS_DATANODE_FAILED_VOLUMES_TOLERATED_KEY configuration    * option, ie the DN shuts itself down when the number of failures    * experienced drops below the tolerated amount.    */
annotation|@
name|Test
DECL|method|testConfigureMinValidVolumes ()
specifier|public
name|void
name|testConfigureMinValidVolumes
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeTrue
argument_list|(
operator|!
name|System
operator|.
name|getProperty
argument_list|(
literal|"os.name"
argument_list|)
operator|.
name|startsWith
argument_list|(
literal|"Windows"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Bring up two additional datanodes that need both of their volumes
comment|// functioning in order to stay up.
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_FAILED_VOLUMES_TOLERATED_KEY
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|startDataNodes
argument_list|(
name|conf
argument_list|,
literal|2
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|FSNamesystem
name|ns
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
decl_stmt|;
name|long
name|origCapacity
init|=
name|DFSTestUtil
operator|.
name|getLiveDatanodeCapacity
argument_list|(
name|ns
argument_list|)
decl_stmt|;
name|long
name|dnCapacity
init|=
name|DFSTestUtil
operator|.
name|getDatanodeCapacity
argument_list|(
name|ns
argument_list|,
literal|0
argument_list|)
decl_stmt|;
comment|// Fail a volume on the 2nd DN
name|File
name|dn2Vol1
init|=
operator|new
name|File
argument_list|(
name|dataDir
argument_list|,
literal|"data"
operator|+
operator|(
literal|2
operator|*
literal|1
operator|+
literal|1
operator|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Couldn't chmod local vol"
argument_list|,
name|dn2Vol1
operator|.
name|setExecutable
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
comment|// Should only get two replicas (the first DN and the 3rd)
name|Path
name|file1
init|=
operator|new
name|Path
argument_list|(
literal|"/test1"
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|file1
argument_list|,
literal|1024
argument_list|,
operator|(
name|short
operator|)
literal|3
argument_list|,
literal|1L
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|waitReplication
argument_list|(
name|fs
argument_list|,
name|file1
argument_list|,
operator|(
name|short
operator|)
literal|2
argument_list|)
expr_stmt|;
comment|// Check that this single failure caused a DN to die.
name|DFSTestUtil
operator|.
name|waitForDatanodeStatus
argument_list|(
name|ns
argument_list|,
literal|2
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
name|origCapacity
operator|-
operator|(
literal|1
operator|*
name|dnCapacity
operator|)
argument_list|,
name|WAIT_FOR_HEARTBEATS
argument_list|)
expr_stmt|;
comment|// If we restore the volume we should still only be able to get
comment|// two replicas since the DN is still considered dead.
name|assertTrue
argument_list|(
literal|"Couldn't chmod local vol"
argument_list|,
name|dn2Vol1
operator|.
name|setExecutable
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|Path
name|file2
init|=
operator|new
name|Path
argument_list|(
literal|"/test2"
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|file2
argument_list|,
literal|1024
argument_list|,
operator|(
name|short
operator|)
literal|3
argument_list|,
literal|1L
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|waitReplication
argument_list|(
name|fs
argument_list|,
name|file2
argument_list|,
operator|(
name|short
operator|)
literal|2
argument_list|)
expr_stmt|;
block|}
comment|/**     * Restart the datanodes with a new volume tolerated value.    * @param volTolerated number of dfs data dir failures to tolerate    * @param manageDfsDirs whether the mini cluster should manage data dirs    * @throws IOException    */
DECL|method|restartDatanodes (int volTolerated, boolean manageDfsDirs)
specifier|private
name|void
name|restartDatanodes
parameter_list|(
name|int
name|volTolerated
parameter_list|,
name|boolean
name|manageDfsDirs
parameter_list|)
throws|throws
name|IOException
block|{
comment|//Make sure no datanode is running
name|cluster
operator|.
name|shutdownDataNodes
argument_list|()
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_FAILED_VOLUMES_TOLERATED_KEY
argument_list|,
name|volTolerated
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|startDataNodes
argument_list|(
name|conf
argument_list|,
literal|1
argument_list|,
name|manageDfsDirs
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
block|}
comment|/**    * Test for different combination of volume configs and volumes tolerated     * values.    */
annotation|@
name|Test
DECL|method|testVolumeAndTolerableConfiguration ()
specifier|public
name|void
name|testVolumeAndTolerableConfiguration
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Check if Block Pool Service exit for an invalid conf value.
name|testVolumeConfig
argument_list|(
operator|-
literal|1
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// Ditto if the value is too big.
name|testVolumeConfig
argument_list|(
literal|100
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// Test for one failed volume
name|testVolumeConfig
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// Test for one failed volume with 1 tolerable volume
name|testVolumeConfig
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// Test all good volumes
name|testVolumeConfig
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// Test all failed volumes
name|testVolumeConfig
argument_list|(
literal|0
argument_list|,
literal|2
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests for a given volumes to be tolerated and volumes failed.    */
DECL|method|testVolumeConfig (int volumesTolerated, int volumesFailed, boolean expectedBPServiceState, boolean manageDfsDirs)
specifier|private
name|void
name|testVolumeConfig
parameter_list|(
name|int
name|volumesTolerated
parameter_list|,
name|int
name|volumesFailed
parameter_list|,
name|boolean
name|expectedBPServiceState
parameter_list|,
name|boolean
name|manageDfsDirs
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|assumeTrue
argument_list|(
operator|!
name|System
operator|.
name|getProperty
argument_list|(
literal|"os.name"
argument_list|)
operator|.
name|startsWith
argument_list|(
literal|"Windows"
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|int
name|dnIndex
init|=
literal|0
decl_stmt|;
comment|// Fail the current directory since invalid storage directory perms
comment|// get fixed up automatically on datanode startup.
name|File
index|[]
name|dirs
init|=
block|{
operator|new
name|File
argument_list|(
name|MiniDFSCluster
operator|.
name|getStorageDir
argument_list|(
name|dnIndex
argument_list|,
literal|0
argument_list|)
argument_list|,
literal|"current"
argument_list|)
block|,
operator|new
name|File
argument_list|(
name|MiniDFSCluster
operator|.
name|getStorageDir
argument_list|(
name|dnIndex
argument_list|,
literal|1
argument_list|)
argument_list|,
literal|"current"
argument_list|)
block|}
decl_stmt|;
try|try
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|volumesFailed
condition|;
name|i
operator|++
control|)
block|{
name|prepareDirToFail
argument_list|(
name|dirs
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|restartDatanodes
argument_list|(
name|volumesTolerated
argument_list|,
name|manageDfsDirs
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedBPServiceState
argument_list|,
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|isBPServiceAlive
argument_list|(
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getBlockPoolId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
for|for
control|(
name|File
name|dir
range|:
name|dirs
control|)
block|{
name|FileUtil
operator|.
name|chmod
argument_list|(
name|dir
operator|.
name|toString
argument_list|()
argument_list|,
literal|"755"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**     * Prepare directories for a failure, set dir permission to 000    * @param dir    * @throws IOException    * @throws InterruptedException    */
DECL|method|prepareDirToFail (File dir)
specifier|private
name|void
name|prepareDirToFail
parameter_list|(
name|File
name|dir
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|dir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Couldn't chmod local vol"
argument_list|,
literal|0
argument_list|,
name|FileUtil
operator|.
name|chmod
argument_list|(
name|dir
operator|.
name|toString
argument_list|()
argument_list|,
literal|"000"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

