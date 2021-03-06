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
name|mockito
operator|.
name|ArgumentMatchers
operator|.
name|any
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|ArgumentMatchers
operator|.
name|anyString
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|times
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|timeout
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
name|MiniDFSNNTopology
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
name|client
operator|.
name|BlockReportOptions
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
name|protocolPB
operator|.
name|DatanodeProtocolClientSideTranslatorPB
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
name|fsdataset
operator|.
name|FsDatasetSpi
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
name|protocol
operator|.
name|DatanodeRegistration
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
name|protocol
operator|.
name|DatanodeStorage
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
name|protocol
operator|.
name|ReceivedDeletedBlockInfo
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
name|protocol
operator|.
name|ReceivedDeletedBlockInfo
operator|.
name|BlockStatus
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
name|protocol
operator|.
name|StorageBlockReport
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
name|protocol
operator|.
name|StorageReceivedDeletedBlocks
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
import|import
name|org
operator|.
name|mockito
operator|.
name|Mockito
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetSocketAddress
import|;
end_import

begin_comment
comment|/**  * Test manually requesting that the DataNode send a block report.  */
end_comment

begin_class
DECL|class|TestTriggerBlockReport
specifier|public
specifier|final
class|class
name|TestTriggerBlockReport
block|{
DECL|method|testTriggerBlockReport (boolean incremental, boolean withSpecificNN)
specifier|private
name|void
name|testTriggerBlockReport
parameter_list|(
name|boolean
name|incremental
parameter_list|,
name|boolean
name|withSpecificNN
parameter_list|)
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
comment|// Set a really long value for dfs.blockreport.intervalMsec and
comment|// dfs.heartbeat.interval, so that incremental block reports and heartbeats
comment|// won't be sent during this test unless they're triggered
comment|// manually.
name|conf
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BLOCKREPORT_INTERVAL_MSEC_KEY
argument_list|,
literal|10800000L
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_HEARTBEAT_INTERVAL_KEY
argument_list|,
literal|1080L
argument_list|)
expr_stmt|;
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
name|nnTopology
argument_list|(
name|MiniDFSNNTopology
operator|.
name|simpleHATopology
argument_list|()
argument_list|)
operator|.
name|numDataNodes
argument_list|(
literal|1
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|transitionToActive
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|FileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|DatanodeProtocolClientSideTranslatorPB
name|spyOnNn0
init|=
name|InternalDataNodeTestUtils
operator|.
name|spyOnBposToNN
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
argument_list|,
name|cluster
operator|.
name|getNameNode
argument_list|(
literal|0
argument_list|)
argument_list|)
decl_stmt|;
name|DatanodeProtocolClientSideTranslatorPB
name|spyOnNn1
init|=
name|InternalDataNodeTestUtils
operator|.
name|spyOnBposToNN
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
argument_list|,
name|cluster
operator|.
name|getNameNode
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
operator|new
name|Path
argument_list|(
literal|"/abc"
argument_list|)
argument_list|,
literal|16
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
literal|1L
argument_list|)
expr_stmt|;
comment|// We should get 1 incremental block report on both NNs.
name|Mockito
operator|.
name|verify
argument_list|(
name|spyOnNn0
argument_list|,
name|timeout
argument_list|(
literal|60000
argument_list|)
operator|.
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|blockReceivedAndDeleted
argument_list|(
name|any
argument_list|(
name|DatanodeRegistration
operator|.
name|class
argument_list|)
argument_list|,
name|anyString
argument_list|()
argument_list|,
name|any
argument_list|(
name|StorageReceivedDeletedBlocks
index|[]
operator|.
expr|class
argument_list|)
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|spyOnNn1
argument_list|,
name|timeout
argument_list|(
literal|60000
argument_list|)
operator|.
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|blockReceivedAndDeleted
argument_list|(
name|any
argument_list|(
name|DatanodeRegistration
operator|.
name|class
argument_list|)
argument_list|,
name|anyString
argument_list|()
argument_list|,
name|any
argument_list|(
name|StorageReceivedDeletedBlocks
index|[]
operator|.
expr|class
argument_list|)
argument_list|)
expr_stmt|;
comment|// We should not receive any more incremental or incremental block reports,
comment|// since the interval we configured is so long.
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
name|Thread
operator|.
name|sleep
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|spyOnNn0
argument_list|,
name|times
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|blockReport
argument_list|(
name|any
argument_list|(
name|DatanodeRegistration
operator|.
name|class
argument_list|)
argument_list|,
name|anyString
argument_list|()
argument_list|,
name|any
argument_list|(
name|StorageBlockReport
index|[]
operator|.
expr|class
argument_list|)
argument_list|,
name|any
argument_list|()
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|spyOnNn0
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|blockReceivedAndDeleted
argument_list|(
name|any
argument_list|(
name|DatanodeRegistration
operator|.
name|class
argument_list|)
argument_list|,
name|anyString
argument_list|()
argument_list|,
name|any
argument_list|(
name|StorageReceivedDeletedBlocks
index|[]
operator|.
expr|class
argument_list|)
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|spyOnNn1
argument_list|,
name|times
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|blockReport
argument_list|(
name|any
argument_list|(
name|DatanodeRegistration
operator|.
name|class
argument_list|)
argument_list|,
name|anyString
argument_list|()
argument_list|,
name|any
argument_list|(
name|StorageBlockReport
index|[]
operator|.
expr|class
argument_list|)
argument_list|,
name|any
argument_list|()
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|spyOnNn1
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|blockReceivedAndDeleted
argument_list|(
name|any
argument_list|(
name|DatanodeRegistration
operator|.
name|class
argument_list|)
argument_list|,
name|anyString
argument_list|()
argument_list|,
name|any
argument_list|(
name|StorageReceivedDeletedBlocks
index|[]
operator|.
expr|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Create a fake block deletion notification on the DataNode.
comment|// This will be sent with the next incremental block report.
name|ReceivedDeletedBlockInfo
name|rdbi
init|=
operator|new
name|ReceivedDeletedBlockInfo
argument_list|(
operator|new
name|Block
argument_list|(
literal|5678
argument_list|,
literal|512
argument_list|,
literal|1000
argument_list|)
argument_list|,
name|BlockStatus
operator|.
name|DELETED_BLOCK
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|DataNode
name|datanode
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
for|for
control|(
name|BPServiceActor
name|actor
range|:
name|datanode
operator|.
name|getAllBpOs
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getBPServiceActors
argument_list|()
control|)
block|{
specifier|final
name|FsDatasetSpi
argument_list|<
name|?
argument_list|>
name|dataset
init|=
name|datanode
operator|.
name|getFSDataset
argument_list|()
decl_stmt|;
specifier|final
name|DatanodeStorage
name|storage
decl_stmt|;
try|try
init|(
name|FsDatasetSpi
operator|.
name|FsVolumeReferences
name|volumes
init|=
name|dataset
operator|.
name|getFsVolumeReferences
argument_list|()
init|)
block|{
name|storage
operator|=
name|dataset
operator|.
name|getStorage
argument_list|(
name|volumes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getStorageID
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|actor
operator|.
name|getIbrManager
argument_list|()
operator|.
name|addRDBI
argument_list|(
name|rdbi
argument_list|,
name|storage
argument_list|)
expr_stmt|;
block|}
comment|// Manually trigger a block report.
comment|// Only trigger block report to NN1 when testing triggering block report on specific namenode.
name|InetSocketAddress
name|nnAddr
init|=
name|withSpecificNN
condition|?
name|cluster
operator|.
name|getNameNode
argument_list|(
literal|1
argument_list|)
operator|.
name|getServiceRpcAddress
argument_list|()
else|:
literal|null
decl_stmt|;
name|datanode
operator|.
name|triggerBlockReport
argument_list|(
operator|new
name|BlockReportOptions
operator|.
name|Factory
argument_list|()
operator|.
name|setNamenodeAddr
argument_list|(
name|nnAddr
argument_list|)
operator|.
name|setIncremental
argument_list|(
name|incremental
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
comment|// triggerBlockReport returns before the block report is
comment|// actually sent.  Wait for it to be sent here.
if|if
condition|(
name|incremental
condition|)
block|{
name|Mockito
operator|.
name|verify
argument_list|(
name|spyOnNn1
argument_list|,
name|timeout
argument_list|(
literal|60000
argument_list|)
operator|.
name|times
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|blockReceivedAndDeleted
argument_list|(
name|any
argument_list|(
name|DatanodeRegistration
operator|.
name|class
argument_list|)
argument_list|,
name|anyString
argument_list|()
argument_list|,
name|any
argument_list|(
name|StorageReceivedDeletedBlocks
index|[]
operator|.
expr|class
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|nn0IncrBlockReport
init|=
name|withSpecificNN
condition|?
literal|1
else|:
literal|2
decl_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|spyOnNn0
argument_list|,
name|timeout
argument_list|(
literal|60000
argument_list|)
operator|.
name|times
argument_list|(
name|nn0IncrBlockReport
argument_list|)
argument_list|)
operator|.
name|blockReceivedAndDeleted
argument_list|(
name|any
argument_list|(
name|DatanodeRegistration
operator|.
name|class
argument_list|)
argument_list|,
name|anyString
argument_list|()
argument_list|,
name|any
argument_list|(
name|StorageReceivedDeletedBlocks
index|[]
operator|.
expr|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Mockito
operator|.
name|verify
argument_list|(
name|spyOnNn1
argument_list|,
name|timeout
argument_list|(
literal|60000
argument_list|)
operator|.
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|blockReport
argument_list|(
name|any
argument_list|(
name|DatanodeRegistration
operator|.
name|class
argument_list|)
argument_list|,
name|anyString
argument_list|()
argument_list|,
name|any
argument_list|(
name|StorageBlockReport
index|[]
operator|.
expr|class
argument_list|)
argument_list|,
name|any
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|nn0BlockReport
init|=
name|withSpecificNN
condition|?
literal|0
else|:
literal|1
decl_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|spyOnNn0
argument_list|,
name|timeout
argument_list|(
literal|60000
argument_list|)
operator|.
name|times
argument_list|(
name|nn0BlockReport
argument_list|)
argument_list|)
operator|.
name|blockReport
argument_list|(
name|any
argument_list|(
name|DatanodeRegistration
operator|.
name|class
argument_list|)
argument_list|,
name|anyString
argument_list|()
argument_list|,
name|any
argument_list|(
name|StorageBlockReport
index|[]
operator|.
expr|class
argument_list|)
argument_list|,
name|any
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testTriggerFullBlockReport ()
specifier|public
name|void
name|testTriggerFullBlockReport
parameter_list|()
throws|throws
name|Exception
block|{
name|testTriggerBlockReport
argument_list|(
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|testTriggerBlockReport
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testTriggerIncrementalBlockReport ()
specifier|public
name|void
name|testTriggerIncrementalBlockReport
parameter_list|()
throws|throws
name|Exception
block|{
name|testTriggerBlockReport
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|testTriggerBlockReport
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

