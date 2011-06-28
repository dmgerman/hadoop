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
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|test
operator|.
name|MetricsAsserts
operator|.
name|assertCounter
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|test
operator|.
name|MetricsAsserts
operator|.
name|getMetrics
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
name|assertEquals
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
name|assertTrue
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
name|util
operator|.
name|ArrayList
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
name|blockmanagement
operator|.
name|DatanodeDescriptor
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

begin_comment
comment|/**  * Test reporting of DN volume failure counts and metrics.  */
end_comment

begin_class
DECL|class|TestDataNodeVolumeFailureReporting
specifier|public
class|class
name|TestDataNodeVolumeFailureReporting
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
name|TestDataNodeVolumeFailureReporting
operator|.
name|class
argument_list|)
decl_stmt|;
block|{
operator|(
operator|(
name|Log4JLogger
operator|)
name|TestDataNodeVolumeFailureReporting
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
comment|/**    * Test that individual volume failures do not cause DNs to fail, that    * all volumes failed on a single datanode do cause it to fail, and    * that the capacities and liveliness is adjusted correctly in the NN.    */
annotation|@
name|Test
DECL|method|testSuccessiveVolumeFailures ()
specifier|public
name|void
name|testSuccessiveVolumeFailures
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
comment|// Bring up two more datanodes
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
comment|/*      * Calculate the total capacity of all the datanodes. Sleep for      * three seconds to be sure the datanodes have had a chance to      * heartbeat their capacities.      */
name|Thread
operator|.
name|sleep
argument_list|(
name|WAIT_FOR_HEARTBEATS
argument_list|)
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
name|File
name|dn1Vol1
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
literal|0
operator|+
literal|1
operator|)
argument_list|)
decl_stmt|;
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
name|File
name|dn3Vol1
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
literal|2
operator|+
literal|1
operator|)
argument_list|)
decl_stmt|;
name|File
name|dn3Vol2
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
literal|2
operator|+
literal|2
operator|)
argument_list|)
decl_stmt|;
comment|/*      * Make the 1st volume directories on the first two datanodes      * non-accessible.  We don't make all three 1st volume directories      * readonly since that would cause the entire pipeline to      * fail. The client does not retry failed nodes even though      * perhaps they could succeed because just a single volume failed.      */
name|assertTrue
argument_list|(
literal|"Couldn't chmod local vol"
argument_list|,
name|dn1Vol1
operator|.
name|setExecutable
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
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
comment|/*      * Create file1 and wait for 3 replicas (ie all DNs can still      * store a block).  Then assert that all DNs are up, despite the      * volume failures.      */
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
literal|3
argument_list|)
expr_stmt|;
name|ArrayList
argument_list|<
name|DataNode
argument_list|>
name|dns
init|=
name|cluster
operator|.
name|getDataNodes
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"DN1 should be up"
argument_list|,
name|dns
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|isDatanodeUp
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"DN2 should be up"
argument_list|,
name|dns
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|isDatanodeUp
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"DN3 should be up"
argument_list|,
name|dns
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|isDatanodeUp
argument_list|()
argument_list|)
expr_stmt|;
comment|/*      * The metrics should confirm the volume failures.      */
name|assertCounter
argument_list|(
literal|"VolumeFailures"
argument_list|,
literal|1L
argument_list|,
name|getMetrics
argument_list|(
name|dns
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getMetrics
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertCounter
argument_list|(
literal|"VolumeFailures"
argument_list|,
literal|1L
argument_list|,
name|getMetrics
argument_list|(
name|dns
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getMetrics
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertCounter
argument_list|(
literal|"VolumeFailures"
argument_list|,
literal|0L
argument_list|,
name|getMetrics
argument_list|(
name|dns
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|getMetrics
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// Ensure we wait a sufficient amount of time
assert|assert
operator|(
name|WAIT_FOR_HEARTBEATS
operator|*
literal|10
operator|)
operator|>
name|WAIT_FOR_DEATH
assert|;
comment|// Eventually the NN should report two volume failures
name|DFSTestUtil
operator|.
name|waitForDatanodeStatus
argument_list|(
name|ns
argument_list|,
literal|3
argument_list|,
literal|0
argument_list|,
literal|2
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
comment|/*      * Now fail a volume on the third datanode. We should be able to get      * three replicas since we've already identified the other failures.      */
name|assertTrue
argument_list|(
literal|"Couldn't chmod local vol"
argument_list|,
name|dn3Vol1
operator|.
name|setExecutable
argument_list|(
literal|false
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
literal|3
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"DN3 should still be up"
argument_list|,
name|dns
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|isDatanodeUp
argument_list|()
argument_list|)
expr_stmt|;
name|assertCounter
argument_list|(
literal|"VolumeFailures"
argument_list|,
literal|1L
argument_list|,
name|getMetrics
argument_list|(
name|dns
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|getMetrics
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|ArrayList
argument_list|<
name|DatanodeDescriptor
argument_list|>
name|live
init|=
operator|new
name|ArrayList
argument_list|<
name|DatanodeDescriptor
argument_list|>
argument_list|()
decl_stmt|;
name|ArrayList
argument_list|<
name|DatanodeDescriptor
argument_list|>
name|dead
init|=
operator|new
name|ArrayList
argument_list|<
name|DatanodeDescriptor
argument_list|>
argument_list|()
decl_stmt|;
name|ns
operator|.
name|DFSNodesStatus
argument_list|(
name|live
argument_list|,
name|dead
argument_list|)
expr_stmt|;
name|live
operator|.
name|clear
argument_list|()
expr_stmt|;
name|dead
operator|.
name|clear
argument_list|()
expr_stmt|;
name|ns
operator|.
name|DFSNodesStatus
argument_list|(
name|live
argument_list|,
name|dead
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"DN3 should have 1 failed volume"
argument_list|,
literal|1
argument_list|,
name|live
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|getVolumeFailures
argument_list|()
argument_list|)
expr_stmt|;
comment|/*      * Once the datanodes have a chance to heartbeat their new capacity the      * total capacity should be down by three volumes (assuming the host      * did not grow or shrink the data volume while the test was running).      */
name|dnCapacity
operator|=
name|DFSTestUtil
operator|.
name|getDatanodeCapacity
argument_list|(
name|ns
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|waitForDatanodeStatus
argument_list|(
name|ns
argument_list|,
literal|3
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|,
name|origCapacity
operator|-
operator|(
literal|3
operator|*
name|dnCapacity
operator|)
argument_list|,
name|WAIT_FOR_HEARTBEATS
argument_list|)
expr_stmt|;
comment|/*      * Now fail the 2nd volume on the 3rd datanode. All its volumes      * are now failed and so it should report two volume failures      * and that it's no longer up. Only wait for two replicas since      * we'll never get a third.      */
name|assertTrue
argument_list|(
literal|"Couldn't chmod local vol"
argument_list|,
name|dn3Vol2
operator|.
name|setExecutable
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|Path
name|file3
init|=
operator|new
name|Path
argument_list|(
literal|"/test3"
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|file3
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
name|file3
argument_list|,
operator|(
name|short
operator|)
literal|2
argument_list|)
expr_stmt|;
comment|// The DN should consider itself dead
name|DFSTestUtil
operator|.
name|waitForDatanodeDeath
argument_list|(
name|dns
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
comment|// And report two failed volumes
name|assertCounter
argument_list|(
literal|"VolumeFailures"
argument_list|,
literal|2L
argument_list|,
name|getMetrics
argument_list|(
name|dns
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|getMetrics
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// The NN considers the DN dead
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
literal|2
argument_list|,
name|origCapacity
operator|-
operator|(
literal|4
operator|*
name|dnCapacity
operator|)
argument_list|,
name|WAIT_FOR_HEARTBEATS
argument_list|)
expr_stmt|;
comment|/*      * The datanode never tries to restore the failed volume, even if      * it's subsequently repaired, but it should see this volume on      * restart, so file creation should be able to succeed after      * restoring the data directories and restarting the datanodes.      */
name|assertTrue
argument_list|(
literal|"Couldn't chmod local vol"
argument_list|,
name|dn1Vol1
operator|.
name|setExecutable
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
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
name|assertTrue
argument_list|(
literal|"Couldn't chmod local vol"
argument_list|,
name|dn3Vol1
operator|.
name|setExecutable
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Couldn't chmod local vol"
argument_list|,
name|dn3Vol2
operator|.
name|setExecutable
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|restartDataNodes
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|Path
name|file4
init|=
operator|new
name|Path
argument_list|(
literal|"/test4"
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|file4
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
name|file4
argument_list|,
operator|(
name|short
operator|)
literal|3
argument_list|)
expr_stmt|;
comment|/*      * Eventually the capacity should be restored to its original value,      * and that the volume failure count should be reported as zero by      * both the metrics and the NN.      */
name|DFSTestUtil
operator|.
name|waitForDatanodeStatus
argument_list|(
name|ns
argument_list|,
literal|3
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
name|origCapacity
argument_list|,
name|WAIT_FOR_HEARTBEATS
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test that the NN re-learns of volume failures after restart.    */
annotation|@
name|Test
DECL|method|testVolFailureStatsPreservedOnNNRestart ()
specifier|public
name|void
name|testVolFailureStatsPreservedOnNNRestart
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
comment|// Bring up two more datanodes that can tolerate 1 failure
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
comment|// Fail the first volume on both datanodes (we have to keep the
comment|// third healthy so one node in the pipeline will not fail).
name|File
name|dn1Vol1
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
literal|0
operator|+
literal|1
operator|)
argument_list|)
decl_stmt|;
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
name|dn1Vol1
operator|.
name|setExecutable
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
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
literal|2
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
comment|// The NN reports two volumes failures
name|DFSTestUtil
operator|.
name|waitForDatanodeStatus
argument_list|(
name|ns
argument_list|,
literal|3
argument_list|,
literal|0
argument_list|,
literal|2
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
comment|// After restarting the NN it still see the two failures
name|cluster
operator|.
name|restartNameNode
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|DFSTestUtil
operator|.
name|waitForDatanodeStatus
argument_list|(
name|ns
argument_list|,
literal|3
argument_list|,
literal|0
argument_list|,
literal|2
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
block|}
block|}
end_class

end_unit

