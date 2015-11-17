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
name|concurrent
operator|.
name|TimeoutException
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
name|Block
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
name|util
operator|.
name|Time
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
comment|/**  * Test if live nodes count per node is correct   * so NN makes right decision for under/over-replicated blocks  */
end_comment

begin_class
DECL|class|TestNodeCount
specifier|public
class|class
name|TestNodeCount
block|{
DECL|field|REPLICATION_FACTOR
specifier|final
name|short
name|REPLICATION_FACTOR
init|=
operator|(
name|short
operator|)
literal|2
decl_stmt|;
DECL|field|TIMEOUT
specifier|final
name|long
name|TIMEOUT
init|=
literal|20000L
decl_stmt|;
DECL|field|timeout
name|long
name|timeout
init|=
literal|0
decl_stmt|;
DECL|field|failtime
name|long
name|failtime
init|=
literal|0
decl_stmt|;
DECL|field|lastBlock
name|Block
name|lastBlock
init|=
literal|null
decl_stmt|;
DECL|field|lastNum
name|NumberReplicas
name|lastNum
init|=
literal|null
decl_stmt|;
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testNodeCount ()
specifier|public
name|void
name|testNodeCount
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
comment|// avoid invalidation by startup delay in order to make test non-transient
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_STARTUP_DELAY_BLOCK_DELETION_SEC_KEY
argument_list|,
literal|60
argument_list|)
expr_stmt|;
comment|// reduce intervals to make test execution time shorter
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_REPLICATION_INTERVAL_KEY
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
name|DFS_HEARTBEAT_INTERVAL_KEY
argument_list|,
literal|1
argument_list|)
expr_stmt|;
comment|// start a mini dfs cluster of 2 nodes
specifier|final
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
name|REPLICATION_FACTOR
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
specifier|final
name|FSNamesystem
name|namesystem
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
decl_stmt|;
specifier|final
name|BlockManager
name|bm
init|=
name|namesystem
operator|.
name|getBlockManager
argument_list|()
decl_stmt|;
specifier|final
name|HeartbeatManager
name|hm
init|=
name|bm
operator|.
name|getDatanodeManager
argument_list|()
operator|.
name|getHeartbeatManager
argument_list|()
decl_stmt|;
specifier|final
name|FileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
comment|// populate the cluster with a one block file
specifier|final
name|Path
name|FILE_PATH
init|=
operator|new
name|Path
argument_list|(
literal|"/testfile"
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|FILE_PATH
argument_list|,
literal|1L
argument_list|,
name|REPLICATION_FACTOR
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
name|FILE_PATH
argument_list|,
name|REPLICATION_FACTOR
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
name|FILE_PATH
argument_list|)
decl_stmt|;
comment|// keep a copy of all datanode descriptor
specifier|final
name|DatanodeDescriptor
index|[]
name|datanodes
init|=
name|hm
operator|.
name|getDatanodes
argument_list|()
decl_stmt|;
comment|// start two new nodes
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
comment|// bring down first datanode
name|DatanodeDescriptor
name|datanode
init|=
name|datanodes
index|[
literal|0
index|]
decl_stmt|;
name|DataNodeProperties
name|dnprop
init|=
name|cluster
operator|.
name|stopDataNode
argument_list|(
name|datanode
operator|.
name|getXferAddr
argument_list|()
argument_list|)
decl_stmt|;
comment|// make sure that NN detects that the datanode is down
name|BlockManagerTestUtil
operator|.
name|noticeDeadDatanode
argument_list|(
name|cluster
operator|.
name|getNameNode
argument_list|()
argument_list|,
name|datanode
operator|.
name|getXferAddr
argument_list|()
argument_list|)
expr_stmt|;
comment|// the block will be replicated
name|DFSTestUtil
operator|.
name|waitReplication
argument_list|(
name|fs
argument_list|,
name|FILE_PATH
argument_list|,
name|REPLICATION_FACTOR
argument_list|)
expr_stmt|;
comment|// restart the first datanode
name|cluster
operator|.
name|restartDataNode
argument_list|(
name|dnprop
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
comment|// check if excessive replica is detected
name|initializeTimeout
argument_list|(
name|TIMEOUT
argument_list|)
expr_stmt|;
while|while
condition|(
name|countNodes
argument_list|(
name|block
operator|.
name|getLocalBlock
argument_list|()
argument_list|,
name|namesystem
argument_list|)
operator|.
name|excessReplicas
argument_list|()
operator|==
literal|0
condition|)
block|{
name|checkTimeout
argument_list|(
literal|"excess replicas not detected"
argument_list|)
expr_stmt|;
block|}
comment|// find out a non-excess node
name|DatanodeDescriptor
name|nonExcessDN
init|=
literal|null
decl_stmt|;
for|for
control|(
name|DatanodeStorageInfo
name|storage
range|:
name|bm
operator|.
name|blocksMap
operator|.
name|getStorages
argument_list|(
name|block
operator|.
name|getLocalBlock
argument_list|()
argument_list|)
control|)
block|{
specifier|final
name|DatanodeDescriptor
name|dn
init|=
name|storage
operator|.
name|getDatanodeDescriptor
argument_list|()
decl_stmt|;
name|Collection
argument_list|<
name|BlockInfo
argument_list|>
name|blocks
init|=
name|bm
operator|.
name|excessReplicateMap
operator|.
name|get
argument_list|(
name|dn
operator|.
name|getDatanodeUuid
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|blocks
operator|==
literal|null
operator|||
operator|!
name|blocks
operator|.
name|contains
argument_list|(
name|block
operator|.
name|getLocalBlock
argument_list|()
argument_list|)
condition|)
block|{
name|nonExcessDN
operator|=
name|dn
expr_stmt|;
break|break;
block|}
block|}
name|assertTrue
argument_list|(
name|nonExcessDN
operator|!=
literal|null
argument_list|)
expr_stmt|;
comment|// bring down non excessive datanode
name|dnprop
operator|=
name|cluster
operator|.
name|stopDataNode
argument_list|(
name|nonExcessDN
operator|.
name|getXferAddr
argument_list|()
argument_list|)
expr_stmt|;
comment|// make sure that NN detects that the datanode is down
name|BlockManagerTestUtil
operator|.
name|noticeDeadDatanode
argument_list|(
name|cluster
operator|.
name|getNameNode
argument_list|()
argument_list|,
name|nonExcessDN
operator|.
name|getXferAddr
argument_list|()
argument_list|)
expr_stmt|;
comment|// The block should be replicated
name|initializeTimeout
argument_list|(
name|TIMEOUT
argument_list|)
expr_stmt|;
while|while
condition|(
name|countNodes
argument_list|(
name|block
operator|.
name|getLocalBlock
argument_list|()
argument_list|,
name|namesystem
argument_list|)
operator|.
name|liveReplicas
argument_list|()
operator|!=
name|REPLICATION_FACTOR
condition|)
block|{
name|checkTimeout
argument_list|(
literal|"live replica count not correct"
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
block|}
comment|// restart the first datanode
name|cluster
operator|.
name|restartDataNode
argument_list|(
name|dnprop
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
comment|// check if excessive replica is detected
name|initializeTimeout
argument_list|(
name|TIMEOUT
argument_list|)
expr_stmt|;
while|while
condition|(
name|countNodes
argument_list|(
name|block
operator|.
name|getLocalBlock
argument_list|()
argument_list|,
name|namesystem
argument_list|)
operator|.
name|excessReplicas
argument_list|()
operator|!=
literal|2
condition|)
block|{
name|checkTimeout
argument_list|(
literal|"excess replica count not equal to 2"
argument_list|)
expr_stmt|;
block|}
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
DECL|method|initializeTimeout (long timeout)
name|void
name|initializeTimeout
parameter_list|(
name|long
name|timeout
parameter_list|)
block|{
name|this
operator|.
name|timeout
operator|=
name|timeout
expr_stmt|;
name|this
operator|.
name|failtime
operator|=
name|Time
operator|.
name|monotonicNow
argument_list|()
operator|+
operator|(
operator|(
name|timeout
operator|<=
literal|0
operator|)
condition|?
name|Long
operator|.
name|MAX_VALUE
else|:
name|timeout
operator|)
expr_stmt|;
block|}
DECL|method|checkTimeout (String testLabel)
name|void
name|checkTimeout
parameter_list|(
name|String
name|testLabel
parameter_list|)
throws|throws
name|TimeoutException
block|{
name|checkTimeout
argument_list|(
name|testLabel
argument_list|,
literal|10
argument_list|)
expr_stmt|;
block|}
comment|/* check for timeout, then wait for cycleTime msec */
DECL|method|checkTimeout (String testLabel, long cycleTime)
name|void
name|checkTimeout
parameter_list|(
name|String
name|testLabel
parameter_list|,
name|long
name|cycleTime
parameter_list|)
throws|throws
name|TimeoutException
block|{
if|if
condition|(
name|Time
operator|.
name|monotonicNow
argument_list|()
operator|>
name|failtime
condition|)
block|{
throw|throw
operator|new
name|TimeoutException
argument_list|(
literal|"Timeout: "
operator|+
name|testLabel
operator|+
literal|" for block "
operator|+
name|lastBlock
operator|+
literal|" after "
operator|+
name|timeout
operator|+
literal|" msec.  Last counts: live = "
operator|+
name|lastNum
operator|.
name|liveReplicas
argument_list|()
operator|+
literal|", excess = "
operator|+
name|lastNum
operator|.
name|excessReplicas
argument_list|()
operator|+
literal|", corrupt = "
operator|+
name|lastNum
operator|.
name|corruptReplicas
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
name|cycleTime
operator|>
literal|0
condition|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|cycleTime
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
comment|//ignore
block|}
block|}
block|}
comment|/* threadsafe read of the replication counts for this block */
DECL|method|countNodes (Block block, FSNamesystem namesystem)
name|NumberReplicas
name|countNodes
parameter_list|(
name|Block
name|block
parameter_list|,
name|FSNamesystem
name|namesystem
parameter_list|)
block|{
name|BlockManager
name|blockManager
init|=
name|namesystem
operator|.
name|getBlockManager
argument_list|()
decl_stmt|;
name|namesystem
operator|.
name|readLock
argument_list|()
expr_stmt|;
try|try
block|{
name|lastBlock
operator|=
name|block
expr_stmt|;
name|lastNum
operator|=
name|blockManager
operator|.
name|countNodes
argument_list|(
name|blockManager
operator|.
name|getStoredBlock
argument_list|(
name|block
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|lastNum
return|;
block|}
finally|finally
block|{
name|namesystem
operator|.
name|readUnlock
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

