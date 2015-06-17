begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode
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
name|namenode
package|;
end_package

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
name|MiniDFSCluster
operator|.
name|DataNodeProperties
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
name|server
operator|.
name|blockmanagement
operator|.
name|BlockManager
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
name|NumberReplicas
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
name|datanode
operator|.
name|DataNodeTestUtils
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
DECL|class|TestProcessCorruptBlocks
specifier|public
class|class
name|TestProcessCorruptBlocks
block|{
comment|/**    * The corrupt block has to be removed when the number of valid replicas    * matches replication factor for the file. In this the above condition is    * tested by reducing the replication factor     * The test strategy :     *   Bring up Cluster with 3 DataNodes    *   Create a file of replication factor 3     *   Corrupt one replica of a block of the file     *   Verify that there are still 2 good replicas and 1 corrupt replica    *    (corrupt replica should not be removed since number of good    *     replicas (2) is less than replication factor (3))    *   Set the replication factor to 2     *   Verify that the corrupt replica is removed.     *     (corrupt replica  should not be removed since number of good    *      replicas (2) is equal to replication factor (2))    */
annotation|@
name|Test
DECL|method|testWhenDecreasingReplication ()
specifier|public
name|void
name|testWhenDecreasingReplication
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BLOCKREPORT_INTERVAL_MSEC_KEY
argument_list|,
literal|1000L
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_REPLICATION_PENDING_TIMEOUT_SEC_KEY
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|MiniDFSCluster
name|cluster
init|=
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
literal|3
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|FileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
specifier|final
name|FSNamesystem
name|namesystem
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
decl_stmt|;
try|try
block|{
specifier|final
name|Path
name|fileName
init|=
operator|new
name|Path
argument_list|(
literal|"/foo1"
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|fileName
argument_list|,
literal|2
argument_list|,
operator|(
name|short
operator|)
literal|3
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|waitReplication
argument_list|(
name|fs
argument_list|,
name|fileName
argument_list|,
operator|(
name|short
operator|)
literal|3
argument_list|)
expr_stmt|;
name|ExtendedBlock
name|block
init|=
name|DFSTestUtil
operator|.
name|getFirstBlock
argument_list|(
name|fs
argument_list|,
name|fileName
argument_list|)
decl_stmt|;
name|corruptBlock
argument_list|(
name|cluster
argument_list|,
name|fs
argument_list|,
name|fileName
argument_list|,
literal|0
argument_list|,
name|block
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|waitReplication
argument_list|(
name|fs
argument_list|,
name|fileName
argument_list|,
operator|(
name|short
operator|)
literal|2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|countReplicas
argument_list|(
name|namesystem
argument_list|,
name|block
argument_list|)
operator|.
name|liveReplicas
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|countReplicas
argument_list|(
name|namesystem
argument_list|,
name|block
argument_list|)
operator|.
name|corruptReplicas
argument_list|()
argument_list|)
expr_stmt|;
name|namesystem
operator|.
name|setReplication
argument_list|(
name|fileName
operator|.
name|toString
argument_list|()
argument_list|,
operator|(
name|short
operator|)
literal|2
argument_list|)
expr_stmt|;
comment|// wait for 3 seconds so that all block reports are processed.
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|3000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ignored
parameter_list|)
block|{       }
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|countReplicas
argument_list|(
name|namesystem
argument_list|,
name|block
argument_list|)
operator|.
name|liveReplicas
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|countReplicas
argument_list|(
name|namesystem
argument_list|,
name|block
argument_list|)
operator|.
name|corruptReplicas
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * The corrupt block has to be removed when the number of valid replicas    * matches replication factor for the file. In this test, the above     * condition is achieved by increasing the number of good replicas by     * replicating on a new Datanode.     * The test strategy :     *   Bring up Cluster with 3 DataNodes    *   Create a file  of replication factor 3    *   Corrupt one replica of a block of the file     *   Verify that there are still 2 good replicas and 1 corrupt replica     *     (corrupt replica should not be removed since number of good replicas    *      (2) is less  than replication factor (3))     *   Start a new data node     *   Verify that the a new replica is created and corrupt replica is    *   removed.    *     */
annotation|@
name|Test
DECL|method|testByAddingAnExtraDataNode ()
specifier|public
name|void
name|testByAddingAnExtraDataNode
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BLOCKREPORT_INTERVAL_MSEC_KEY
argument_list|,
literal|1000L
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_REPLICATION_PENDING_TIMEOUT_SEC_KEY
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|MiniDFSCluster
name|cluster
init|=
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
literal|4
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|FileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
specifier|final
name|FSNamesystem
name|namesystem
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
decl_stmt|;
name|DataNodeProperties
name|dnPropsFourth
init|=
name|cluster
operator|.
name|stopDataNode
argument_list|(
literal|3
argument_list|)
decl_stmt|;
try|try
block|{
specifier|final
name|Path
name|fileName
init|=
operator|new
name|Path
argument_list|(
literal|"/foo1"
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|fileName
argument_list|,
literal|2
argument_list|,
operator|(
name|short
operator|)
literal|3
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|waitReplication
argument_list|(
name|fs
argument_list|,
name|fileName
argument_list|,
operator|(
name|short
operator|)
literal|3
argument_list|)
expr_stmt|;
name|ExtendedBlock
name|block
init|=
name|DFSTestUtil
operator|.
name|getFirstBlock
argument_list|(
name|fs
argument_list|,
name|fileName
argument_list|)
decl_stmt|;
name|corruptBlock
argument_list|(
name|cluster
argument_list|,
name|fs
argument_list|,
name|fileName
argument_list|,
literal|0
argument_list|,
name|block
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|waitReplication
argument_list|(
name|fs
argument_list|,
name|fileName
argument_list|,
operator|(
name|short
operator|)
literal|2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|countReplicas
argument_list|(
name|namesystem
argument_list|,
name|block
argument_list|)
operator|.
name|liveReplicas
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|countReplicas
argument_list|(
name|namesystem
argument_list|,
name|block
argument_list|)
operator|.
name|corruptReplicas
argument_list|()
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|restartDataNode
argument_list|(
name|dnPropsFourth
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|waitReplication
argument_list|(
name|fs
argument_list|,
name|fileName
argument_list|,
operator|(
name|short
operator|)
literal|3
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|countReplicas
argument_list|(
name|namesystem
argument_list|,
name|block
argument_list|)
operator|.
name|liveReplicas
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|countReplicas
argument_list|(
name|namesystem
argument_list|,
name|block
argument_list|)
operator|.
name|corruptReplicas
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * The corrupt block has to be removed when the number of valid replicas    * matches replication factor for the file. The above condition should hold    * true as long as there is one good replica. This test verifies that.    *     * The test strategy :     *   Bring up Cluster with 2 DataNodes    *   Create a file of replication factor 2     *   Corrupt one replica of a block of the file     *   Verify that there is  one good replicas and 1 corrupt replica     *     (corrupt replica should not be removed since number of good     *     replicas (1) is less than replication factor (2)).    *   Set the replication factor to 1     *   Verify that the corrupt replica is removed.     *     (corrupt replica should  be removed since number of good    *      replicas (1) is equal to replication factor (1))    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|20000
argument_list|)
DECL|method|testWithReplicationFactorAsOne ()
specifier|public
name|void
name|testWithReplicationFactorAsOne
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BLOCKREPORT_INTERVAL_MSEC_KEY
argument_list|,
literal|1000L
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_REPLICATION_PENDING_TIMEOUT_SEC_KEY
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|MiniDFSCluster
name|cluster
init|=
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
literal|2
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|FileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
specifier|final
name|FSNamesystem
name|namesystem
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
decl_stmt|;
try|try
block|{
specifier|final
name|Path
name|fileName
init|=
operator|new
name|Path
argument_list|(
literal|"/foo1"
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|fileName
argument_list|,
literal|2
argument_list|,
operator|(
name|short
operator|)
literal|2
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|waitReplication
argument_list|(
name|fs
argument_list|,
name|fileName
argument_list|,
operator|(
name|short
operator|)
literal|2
argument_list|)
expr_stmt|;
name|ExtendedBlock
name|block
init|=
name|DFSTestUtil
operator|.
name|getFirstBlock
argument_list|(
name|fs
argument_list|,
name|fileName
argument_list|)
decl_stmt|;
name|corruptBlock
argument_list|(
name|cluster
argument_list|,
name|fs
argument_list|,
name|fileName
argument_list|,
literal|0
argument_list|,
name|block
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|waitReplication
argument_list|(
name|fs
argument_list|,
name|fileName
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|countReplicas
argument_list|(
name|namesystem
argument_list|,
name|block
argument_list|)
operator|.
name|liveReplicas
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|countReplicas
argument_list|(
name|namesystem
argument_list|,
name|block
argument_list|)
operator|.
name|corruptReplicas
argument_list|()
argument_list|)
expr_stmt|;
name|namesystem
operator|.
name|setReplication
argument_list|(
name|fileName
operator|.
name|toString
argument_list|()
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|)
expr_stmt|;
comment|// wait for 3 seconds so that all block reports are processed.
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ignored
parameter_list|)
block|{         }
if|if
condition|(
name|countReplicas
argument_list|(
name|namesystem
argument_list|,
name|block
argument_list|)
operator|.
name|corruptReplicas
argument_list|()
operator|==
literal|0
condition|)
block|{
break|break;
block|}
block|}
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|countReplicas
argument_list|(
name|namesystem
argument_list|,
name|block
argument_list|)
operator|.
name|liveReplicas
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|countReplicas
argument_list|(
name|namesystem
argument_list|,
name|block
argument_list|)
operator|.
name|corruptReplicas
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * None of the blocks can be removed if all blocks are corrupt.    *     * The test strategy :     *    Bring up Cluster with 3 DataNodes    *    Create a file of replication factor 3     *    Corrupt all three replicas     *    Verify that all replicas are corrupt and 3 replicas are present.    *    Set the replication factor to 1     *    Verify that all replicas are corrupt and 3 replicas are present.    */
annotation|@
name|Test
DECL|method|testWithAllCorruptReplicas ()
specifier|public
name|void
name|testWithAllCorruptReplicas
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BLOCKREPORT_INTERVAL_MSEC_KEY
argument_list|,
literal|1000L
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_REPLICATION_PENDING_TIMEOUT_SEC_KEY
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|MiniDFSCluster
name|cluster
init|=
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
literal|3
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|FileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
specifier|final
name|FSNamesystem
name|namesystem
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
decl_stmt|;
try|try
block|{
specifier|final
name|Path
name|fileName
init|=
operator|new
name|Path
argument_list|(
literal|"/foo1"
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|fileName
argument_list|,
literal|2
argument_list|,
operator|(
name|short
operator|)
literal|3
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|waitReplication
argument_list|(
name|fs
argument_list|,
name|fileName
argument_list|,
operator|(
name|short
operator|)
literal|3
argument_list|)
expr_stmt|;
name|ExtendedBlock
name|block
init|=
name|DFSTestUtil
operator|.
name|getFirstBlock
argument_list|(
name|fs
argument_list|,
name|fileName
argument_list|)
decl_stmt|;
name|corruptBlock
argument_list|(
name|cluster
argument_list|,
name|fs
argument_list|,
name|fileName
argument_list|,
literal|0
argument_list|,
name|block
argument_list|)
expr_stmt|;
name|corruptBlock
argument_list|(
name|cluster
argument_list|,
name|fs
argument_list|,
name|fileName
argument_list|,
literal|1
argument_list|,
name|block
argument_list|)
expr_stmt|;
name|corruptBlock
argument_list|(
name|cluster
argument_list|,
name|fs
argument_list|,
name|fileName
argument_list|,
literal|2
argument_list|,
name|block
argument_list|)
expr_stmt|;
comment|// wait for 3 seconds so that all block reports are processed.
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|3000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ignored
parameter_list|)
block|{       }
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|countReplicas
argument_list|(
name|namesystem
argument_list|,
name|block
argument_list|)
operator|.
name|liveReplicas
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|countReplicas
argument_list|(
name|namesystem
argument_list|,
name|block
argument_list|)
operator|.
name|corruptReplicas
argument_list|()
argument_list|)
expr_stmt|;
name|namesystem
operator|.
name|setReplication
argument_list|(
name|fileName
operator|.
name|toString
argument_list|()
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|)
expr_stmt|;
comment|// wait for 3 seconds so that all block reports are processed.
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|3000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ignored
parameter_list|)
block|{       }
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|countReplicas
argument_list|(
name|namesystem
argument_list|,
name|block
argument_list|)
operator|.
name|liveReplicas
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|countReplicas
argument_list|(
name|namesystem
argument_list|,
name|block
argument_list|)
operator|.
name|corruptReplicas
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|countReplicas (final FSNamesystem namesystem, ExtendedBlock block)
specifier|private
specifier|static
name|NumberReplicas
name|countReplicas
parameter_list|(
specifier|final
name|FSNamesystem
name|namesystem
parameter_list|,
name|ExtendedBlock
name|block
parameter_list|)
block|{
specifier|final
name|BlockManager
name|blockManager
init|=
name|namesystem
operator|.
name|getBlockManager
argument_list|()
decl_stmt|;
return|return
name|blockManager
operator|.
name|countNodes
argument_list|(
name|blockManager
operator|.
name|getStoredBlock
argument_list|(
name|block
operator|.
name|getLocalBlock
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
DECL|method|corruptBlock (MiniDFSCluster cluster, FileSystem fs, final Path fileName, int dnIndex, ExtendedBlock block)
specifier|private
name|void
name|corruptBlock
parameter_list|(
name|MiniDFSCluster
name|cluster
parameter_list|,
name|FileSystem
name|fs
parameter_list|,
specifier|final
name|Path
name|fileName
parameter_list|,
name|int
name|dnIndex
parameter_list|,
name|ExtendedBlock
name|block
parameter_list|)
throws|throws
name|IOException
block|{
comment|// corrupt the block on datanode dnIndex
comment|// the indexes change once the nodes are restarted.
comment|// But the datadirectory will not change
name|assertTrue
argument_list|(
name|cluster
operator|.
name|corruptReplica
argument_list|(
name|dnIndex
argument_list|,
name|block
argument_list|)
argument_list|)
expr_stmt|;
comment|// Run directory scanner to update the DN's volume map
name|DataNodeTestUtils
operator|.
name|runDirectoryScanner
argument_list|(
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|DataNodeProperties
name|dnProps
init|=
name|cluster
operator|.
name|stopDataNode
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|// Each datanode has multiple data dirs, check each
for|for
control|(
name|int
name|dirIndex
init|=
literal|0
init|;
name|dirIndex
operator|<
literal|2
condition|;
name|dirIndex
operator|++
control|)
block|{
specifier|final
name|String
name|bpid
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getBlockPoolId
argument_list|()
decl_stmt|;
name|File
name|storageDir
init|=
name|cluster
operator|.
name|getStorageDir
argument_list|(
name|dnIndex
argument_list|,
name|dirIndex
argument_list|)
decl_stmt|;
name|File
name|dataDir
init|=
name|MiniDFSCluster
operator|.
name|getFinalizedDir
argument_list|(
name|storageDir
argument_list|,
name|bpid
argument_list|)
decl_stmt|;
name|File
name|scanLogFile
init|=
operator|new
name|File
argument_list|(
name|dataDir
argument_list|,
literal|"dncp_block_verification.log.curr"
argument_list|)
decl_stmt|;
if|if
condition|(
name|scanLogFile
operator|.
name|exists
argument_list|()
condition|)
block|{
comment|// wait for one minute for deletion to succeed;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
operator|!
name|scanLogFile
operator|.
name|delete
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
literal|"Could not delete log file in one minute"
argument_list|,
name|i
operator|<
literal|60
argument_list|)
expr_stmt|;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ignored
parameter_list|)
block|{           }
block|}
block|}
block|}
comment|// restart the detained so the corrupt replica will be detected
name|cluster
operator|.
name|restartDataNode
argument_list|(
name|dnProps
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

