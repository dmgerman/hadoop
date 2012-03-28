begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one   * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|namenode
operator|.
name|NameNode
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
name|mockito
operator|.
name|Mockito
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
import|;
end_import

begin_comment
comment|/**  * Utility class for accessing package-private DataNode information during tests.  *  */
end_comment

begin_class
DECL|class|DataNodeTestUtils
specifier|public
class|class
name|DataNodeTestUtils
block|{
specifier|public
specifier|static
name|DatanodeRegistration
DECL|method|getDNRegistrationByMachineName (DataNode dn, String mName)
name|getDNRegistrationByMachineName
parameter_list|(
name|DataNode
name|dn
parameter_list|,
name|String
name|mName
parameter_list|)
block|{
return|return
name|dn
operator|.
name|getDNRegistrationByMachineName
argument_list|(
name|mName
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|DatanodeRegistration
DECL|method|getDNRegistrationForBP (DataNode dn, String bpid)
name|getDNRegistrationForBP
parameter_list|(
name|DataNode
name|dn
parameter_list|,
name|String
name|bpid
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|dn
operator|.
name|getDNRegistrationForBP
argument_list|(
name|bpid
argument_list|)
return|;
block|}
DECL|method|setHeartbeatsDisabledForTests (DataNode dn, boolean heartbeatsDisabledForTests)
specifier|public
specifier|static
name|void
name|setHeartbeatsDisabledForTests
parameter_list|(
name|DataNode
name|dn
parameter_list|,
name|boolean
name|heartbeatsDisabledForTests
parameter_list|)
block|{
name|dn
operator|.
name|setHeartbeatsDisabledForTests
argument_list|(
name|heartbeatsDisabledForTests
argument_list|)
expr_stmt|;
block|}
DECL|method|triggerDeletionReport (DataNode dn)
specifier|public
specifier|static
name|void
name|triggerDeletionReport
parameter_list|(
name|DataNode
name|dn
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|BPOfferService
name|bpos
range|:
name|dn
operator|.
name|getAllBpOs
argument_list|()
control|)
block|{
name|bpos
operator|.
name|triggerDeletionReportForTests
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|triggerHeartbeat (DataNode dn)
specifier|public
specifier|static
name|void
name|triggerHeartbeat
parameter_list|(
name|DataNode
name|dn
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|BPOfferService
name|bpos
range|:
name|dn
operator|.
name|getAllBpOs
argument_list|()
control|)
block|{
name|bpos
operator|.
name|triggerHeartbeatForTests
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|triggerBlockReport (DataNode dn)
specifier|public
specifier|static
name|void
name|triggerBlockReport
parameter_list|(
name|DataNode
name|dn
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|BPOfferService
name|bpos
range|:
name|dn
operator|.
name|getAllBpOs
argument_list|()
control|)
block|{
name|bpos
operator|.
name|triggerBlockReportForTests
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Insert a Mockito spy object between the given DataNode and    * the given NameNode. This can be used to delay or wait for    * RPC calls on the datanode->NN path.    */
DECL|method|spyOnBposToNN ( DataNode dn, NameNode nn)
specifier|public
specifier|static
name|DatanodeProtocolClientSideTranslatorPB
name|spyOnBposToNN
parameter_list|(
name|DataNode
name|dn
parameter_list|,
name|NameNode
name|nn
parameter_list|)
block|{
name|String
name|bpid
init|=
name|nn
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getBlockPoolId
argument_list|()
decl_stmt|;
name|BPOfferService
name|bpos
init|=
literal|null
decl_stmt|;
for|for
control|(
name|BPOfferService
name|thisBpos
range|:
name|dn
operator|.
name|getAllBpOs
argument_list|()
control|)
block|{
if|if
condition|(
name|thisBpos
operator|.
name|getBlockPoolId
argument_list|()
operator|.
name|equals
argument_list|(
name|bpid
argument_list|)
condition|)
block|{
name|bpos
operator|=
name|thisBpos
expr_stmt|;
break|break;
block|}
block|}
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|bpos
operator|!=
literal|null
argument_list|,
literal|"No such bpid: %s"
argument_list|,
name|bpid
argument_list|)
expr_stmt|;
name|BPServiceActor
name|bpsa
init|=
literal|null
decl_stmt|;
for|for
control|(
name|BPServiceActor
name|thisBpsa
range|:
name|bpos
operator|.
name|getBPServiceActors
argument_list|()
control|)
block|{
if|if
condition|(
name|thisBpsa
operator|.
name|getNNSocketAddress
argument_list|()
operator|.
name|equals
argument_list|(
name|nn
operator|.
name|getServiceRpcAddress
argument_list|()
argument_list|)
condition|)
block|{
name|bpsa
operator|=
name|thisBpsa
expr_stmt|;
break|break;
block|}
block|}
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|bpsa
operator|!=
literal|null
argument_list|,
literal|"No service actor to NN at %s"
argument_list|,
name|nn
operator|.
name|getServiceRpcAddress
argument_list|()
argument_list|)
expr_stmt|;
name|DatanodeProtocolClientSideTranslatorPB
name|origNN
init|=
name|bpsa
operator|.
name|getNameNodeProxy
argument_list|()
decl_stmt|;
name|DatanodeProtocolClientSideTranslatorPB
name|spy
init|=
name|Mockito
operator|.
name|spy
argument_list|(
name|origNN
argument_list|)
decl_stmt|;
name|bpsa
operator|.
name|setNameNode
argument_list|(
name|spy
argument_list|)
expr_stmt|;
return|return
name|spy
return|;
block|}
comment|/**    * This method is used for testing.     * Examples are adding and deleting blocks directly.    * The most common usage will be when the data node's storage is simulated.    *     * @return the fsdataset that stores the blocks    */
DECL|method|getFSDataset (DataNode dn)
specifier|public
specifier|static
name|FsDatasetSpi
argument_list|<
name|?
argument_list|>
name|getFSDataset
parameter_list|(
name|DataNode
name|dn
parameter_list|)
block|{
return|return
name|dn
operator|.
name|getFSDataset
argument_list|()
return|;
block|}
DECL|method|getFsDatasetImpl (DataNode dn)
specifier|public
specifier|static
name|FSDataset
name|getFsDatasetImpl
parameter_list|(
name|DataNode
name|dn
parameter_list|)
block|{
return|return
operator|(
name|FSDataset
operator|)
name|dn
operator|.
name|getFSDataset
argument_list|()
return|;
block|}
DECL|method|getFile (DataNode dn, String bpid, long bid)
specifier|public
specifier|static
name|File
name|getFile
parameter_list|(
name|DataNode
name|dn
parameter_list|,
name|String
name|bpid
parameter_list|,
name|long
name|bid
parameter_list|)
block|{
return|return
name|getFsDatasetImpl
argument_list|(
name|dn
argument_list|)
operator|.
name|getFile
argument_list|(
name|bpid
argument_list|,
name|bid
argument_list|)
return|;
block|}
DECL|method|getBlockFile (DataNode dn, String bpid, Block b )
specifier|public
specifier|static
name|File
name|getBlockFile
parameter_list|(
name|DataNode
name|dn
parameter_list|,
name|String
name|bpid
parameter_list|,
name|Block
name|b
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getFsDatasetImpl
argument_list|(
name|dn
argument_list|)
operator|.
name|getBlockFile
argument_list|(
name|bpid
argument_list|,
name|b
argument_list|)
return|;
block|}
DECL|method|unlinkBlock (DataNode dn, ExtendedBlock block, int numLinks )
specifier|public
specifier|static
name|boolean
name|unlinkBlock
parameter_list|(
name|DataNode
name|dn
parameter_list|,
name|ExtendedBlock
name|block
parameter_list|,
name|int
name|numLinks
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getFsDatasetImpl
argument_list|(
name|dn
argument_list|)
operator|.
name|getReplicaInfo
argument_list|(
name|block
argument_list|)
operator|.
name|unlinkBlock
argument_list|(
name|numLinks
argument_list|)
return|;
block|}
DECL|method|getPendingAsyncDeletions (DataNode dn)
specifier|public
specifier|static
name|long
name|getPendingAsyncDeletions
parameter_list|(
name|DataNode
name|dn
parameter_list|)
block|{
return|return
name|getFsDatasetImpl
argument_list|(
name|dn
argument_list|)
operator|.
name|asyncDiskService
operator|.
name|countPendingDeletions
argument_list|()
return|;
block|}
comment|/**    * Fetch a copy of ReplicaInfo from a datanode by block id    * @param dn datanode to retrieve a replicainfo object from    * @param bpid Block pool Id    * @param blkId id of the replica's block    * @return copy of ReplicaInfo object @link{FSDataset#fetchReplicaInfo}    */
DECL|method|fetchReplicaInfo (final DataNode dn, final String bpid, final long blkId)
specifier|public
specifier|static
name|ReplicaInfo
name|fetchReplicaInfo
parameter_list|(
specifier|final
name|DataNode
name|dn
parameter_list|,
specifier|final
name|String
name|bpid
parameter_list|,
specifier|final
name|long
name|blkId
parameter_list|)
block|{
return|return
name|getFsDatasetImpl
argument_list|(
name|dn
argument_list|)
operator|.
name|fetchReplicaInfo
argument_list|(
name|bpid
argument_list|,
name|blkId
argument_list|)
return|;
block|}
block|}
end_class

end_unit

