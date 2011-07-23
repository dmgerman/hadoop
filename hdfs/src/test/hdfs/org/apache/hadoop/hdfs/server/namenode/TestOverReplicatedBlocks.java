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
name|TestDatanodeBlockScanner
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
name|DatanodeID
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
name|datanode
operator|.
name|DataNodeTestUtils
import|;
end_import

begin_class
DECL|class|TestOverReplicatedBlocks
specifier|public
class|class
name|TestOverReplicatedBlocks
extends|extends
name|TestCase
block|{
comment|/** Test processOverReplicatedBlock can handle corrupt replicas fine.    * It make sure that it won't treat corrupt replicas as valid ones     * thus prevents NN deleting valid replicas but keeping    * corrupt ones.    */
DECL|method|testProcesOverReplicateBlock ()
specifier|public
name|void
name|testProcesOverReplicateBlock
parameter_list|()
throws|throws
name|IOException
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
comment|// corrupt the block on datanode 0
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
name|assertTrue
argument_list|(
name|TestDatanodeBlockScanner
operator|.
name|corruptReplica
argument_list|(
name|block
argument_list|,
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
comment|// remove block scanner log to trigger block scanning
name|File
name|scanLog
init|=
operator|new
name|File
argument_list|(
name|MiniDFSCluster
operator|.
name|getFinalizedDir
argument_list|(
name|MiniDFSCluster
operator|.
name|getStorageDir
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
argument_list|,
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getBlockPoolId
argument_list|()
argument_list|)
operator|.
name|getParent
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|"/../dncp_block_verification.log.prev"
argument_list|)
decl_stmt|;
comment|//wait for one minute for deletion to succeed;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
operator|!
name|scanLog
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
block|{}
block|}
comment|// restart the datanode so the corrupt replica will be detected
name|cluster
operator|.
name|restartDataNode
argument_list|(
name|dnProps
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
name|String
name|blockPoolId
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getBlockPoolId
argument_list|()
decl_stmt|;
specifier|final
name|DatanodeID
name|corruptDataNode
init|=
name|DataNodeTestUtils
operator|.
name|getDNRegistrationForBP
argument_list|(
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|,
name|blockPoolId
argument_list|)
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
comment|// set live datanode's remaining space to be 0
comment|// so they will be chosen to be deleted when over-replication occurs
name|String
name|corruptMachineName
init|=
name|corruptDataNode
operator|.
name|getName
argument_list|()
decl_stmt|;
for|for
control|(
name|DatanodeDescriptor
name|datanode
range|:
name|namesystem
operator|.
name|heartbeats
control|)
block|{
if|if
condition|(
operator|!
name|corruptMachineName
operator|.
name|equals
argument_list|(
name|datanode
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|datanode
operator|.
name|updateHeartbeat
argument_list|(
literal|100L
argument_list|,
literal|100L
argument_list|,
literal|0L
argument_list|,
literal|100L
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
comment|// decrease the replication factor to 1;
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
comment|// corrupt one won't be chosen to be excess one
comment|// without 4910 the number of live replicas would be 0: block gets lost
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|namesystem
operator|.
name|getBlockManager
argument_list|()
operator|.
name|countNodes
argument_list|(
name|block
operator|.
name|getLocalBlock
argument_list|()
argument_list|)
operator|.
name|liveReplicas
argument_list|()
argument_list|)
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
block|}
end_class

end_unit

