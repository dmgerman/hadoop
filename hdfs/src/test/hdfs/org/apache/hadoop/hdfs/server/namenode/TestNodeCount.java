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
name|Iterator
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
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|blockmanagement
operator|.
name|NumberReplicas
import|;
end_import

begin_comment
comment|/**  * Test if live nodes count per node is correct   * so NN makes right decision for under/over-replicated blocks  *   * Two of the "while" loops below use "busy wait"  * because they are detecting transient states.  */
end_comment

begin_class
DECL|class|TestNodeCount
specifier|public
class|class
name|TestNodeCount
extends|extends
name|TestCase
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
DECL|method|testNodeCount ()
specifier|public
name|void
name|testNodeCount
parameter_list|()
throws|throws
name|Exception
block|{
comment|// start a mini dfs cluster of 2 nodes
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
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
name|DatanodeDescriptor
index|[]
name|datanodes
init|=
name|namesystem
operator|.
name|heartbeats
operator|.
name|toArray
argument_list|(
operator|new
name|DatanodeDescriptor
index|[
name|REPLICATION_FACTOR
index|]
argument_list|)
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
name|getName
argument_list|()
argument_list|)
decl_stmt|;
comment|// make sure that NN detects that the datanode is down
try|try
block|{
name|namesystem
operator|.
name|writeLock
argument_list|()
expr_stmt|;
synchronized|synchronized
init|(
name|namesystem
operator|.
name|heartbeats
init|)
block|{
name|datanode
operator|.
name|setLastUpdate
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// mark it dead
name|namesystem
operator|.
name|heartbeatCheck
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|namesystem
operator|.
name|writeUnlock
argument_list|()
expr_stmt|;
block|}
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
comment|// check if excessive replica is detected (transient)
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
name|Iterator
argument_list|<
name|DatanodeDescriptor
argument_list|>
name|iter
init|=
name|namesystem
operator|.
name|getBlockManager
argument_list|()
operator|.
name|blocksMap
operator|.
name|nodeIterator
argument_list|(
name|block
operator|.
name|getLocalBlock
argument_list|()
argument_list|)
decl_stmt|;
name|DatanodeDescriptor
name|nonExcessDN
init|=
literal|null
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|DatanodeDescriptor
name|dn
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|Collection
argument_list|<
name|Block
argument_list|>
name|blocks
init|=
name|namesystem
operator|.
name|getBlockManager
argument_list|()
operator|.
name|excessReplicateMap
operator|.
name|get
argument_list|(
name|dn
operator|.
name|getStorageID
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
name|getName
argument_list|()
argument_list|)
expr_stmt|;
comment|// make sure that NN detects that the datanode is down
try|try
block|{
name|namesystem
operator|.
name|writeLock
argument_list|()
expr_stmt|;
synchronized|synchronized
init|(
name|namesystem
operator|.
name|heartbeats
init|)
block|{
name|nonExcessDN
operator|.
name|setLastUpdate
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// mark it dead
name|namesystem
operator|.
name|heartbeatCheck
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|namesystem
operator|.
name|writeUnlock
argument_list|()
expr_stmt|;
block|}
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
comment|// check if excessive replica is detected (transient)
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
name|System
operator|.
name|currentTimeMillis
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
comment|/* busy wait on transient conditions */
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
literal|0
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
name|System
operator|.
name|currentTimeMillis
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
name|namesystem
operator|.
name|getBlockManager
argument_list|()
operator|.
name|countNodes
argument_list|(
name|block
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

