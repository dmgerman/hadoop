begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.federation.store.records.impl.pb
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
name|federation
operator|.
name|store
operator|.
name|records
operator|.
name|impl
operator|.
name|pb
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|federation
operator|.
name|protocol
operator|.
name|proto
operator|.
name|HdfsServerFederationProtos
operator|.
name|NamenodeMembershipStatsRecordProto
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
name|federation
operator|.
name|protocol
operator|.
name|proto
operator|.
name|HdfsServerFederationProtos
operator|.
name|NamenodeMembershipStatsRecordProto
operator|.
name|Builder
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
name|federation
operator|.
name|protocol
operator|.
name|proto
operator|.
name|HdfsServerFederationProtos
operator|.
name|NamenodeMembershipStatsRecordProtoOrBuilder
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
name|federation
operator|.
name|store
operator|.
name|protocol
operator|.
name|impl
operator|.
name|pb
operator|.
name|FederationProtocolPBTranslator
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
name|federation
operator|.
name|store
operator|.
name|records
operator|.
name|MembershipStats
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|protobuf
operator|.
name|Message
import|;
end_import

begin_comment
comment|/**  * Protobuf implementation of the MembershipStats record.  */
end_comment

begin_class
DECL|class|MembershipStatsPBImpl
specifier|public
class|class
name|MembershipStatsPBImpl
extends|extends
name|MembershipStats
implements|implements
name|PBRecord
block|{
specifier|private
name|FederationProtocolPBTranslator
argument_list|<
name|NamenodeMembershipStatsRecordProto
argument_list|,
DECL|field|translator
name|Builder
argument_list|,
name|NamenodeMembershipStatsRecordProtoOrBuilder
argument_list|>
name|translator
init|=
operator|new
name|FederationProtocolPBTranslator
argument_list|<
name|NamenodeMembershipStatsRecordProto
argument_list|,
name|Builder
argument_list|,
name|NamenodeMembershipStatsRecordProtoOrBuilder
argument_list|>
argument_list|(
name|NamenodeMembershipStatsRecordProto
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|MembershipStatsPBImpl ()
specifier|public
name|MembershipStatsPBImpl
parameter_list|()
block|{   }
annotation|@
name|Override
DECL|method|getProto ()
specifier|public
name|NamenodeMembershipStatsRecordProto
name|getProto
parameter_list|()
block|{
return|return
name|this
operator|.
name|translator
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setProto (Message proto)
specifier|public
name|void
name|setProto
parameter_list|(
name|Message
name|proto
parameter_list|)
block|{
name|this
operator|.
name|translator
operator|.
name|setProto
argument_list|(
name|proto
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|readInstance (String base64String)
specifier|public
name|void
name|readInstance
parameter_list|(
name|String
name|base64String
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|translator
operator|.
name|readInstance
argument_list|(
name|base64String
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setTotalSpace (long space)
specifier|public
name|void
name|setTotalSpace
parameter_list|(
name|long
name|space
parameter_list|)
block|{
name|this
operator|.
name|translator
operator|.
name|getBuilder
argument_list|()
operator|.
name|setTotalSpace
argument_list|(
name|space
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getTotalSpace ()
specifier|public
name|long
name|getTotalSpace
parameter_list|()
block|{
return|return
name|this
operator|.
name|translator
operator|.
name|getProtoOrBuilder
argument_list|()
operator|.
name|getTotalSpace
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setAvailableSpace (long space)
specifier|public
name|void
name|setAvailableSpace
parameter_list|(
name|long
name|space
parameter_list|)
block|{
name|this
operator|.
name|translator
operator|.
name|getBuilder
argument_list|()
operator|.
name|setAvailableSpace
argument_list|(
name|space
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getAvailableSpace ()
specifier|public
name|long
name|getAvailableSpace
parameter_list|()
block|{
return|return
name|this
operator|.
name|translator
operator|.
name|getProtoOrBuilder
argument_list|()
operator|.
name|getAvailableSpace
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setProvidedSpace (long capacity)
specifier|public
name|void
name|setProvidedSpace
parameter_list|(
name|long
name|capacity
parameter_list|)
block|{
name|this
operator|.
name|translator
operator|.
name|getBuilder
argument_list|()
operator|.
name|setProvidedSpace
argument_list|(
name|capacity
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getProvidedSpace ()
specifier|public
name|long
name|getProvidedSpace
parameter_list|()
block|{
return|return
name|this
operator|.
name|translator
operator|.
name|getProtoOrBuilder
argument_list|()
operator|.
name|getProvidedSpace
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setNumOfFiles (long files)
specifier|public
name|void
name|setNumOfFiles
parameter_list|(
name|long
name|files
parameter_list|)
block|{
name|this
operator|.
name|translator
operator|.
name|getBuilder
argument_list|()
operator|.
name|setNumOfFiles
argument_list|(
name|files
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getNumOfFiles ()
specifier|public
name|long
name|getNumOfFiles
parameter_list|()
block|{
return|return
name|this
operator|.
name|translator
operator|.
name|getProtoOrBuilder
argument_list|()
operator|.
name|getNumOfFiles
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setNumOfBlocks (long blocks)
specifier|public
name|void
name|setNumOfBlocks
parameter_list|(
name|long
name|blocks
parameter_list|)
block|{
name|this
operator|.
name|translator
operator|.
name|getBuilder
argument_list|()
operator|.
name|setNumOfBlocks
argument_list|(
name|blocks
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getNumOfBlocks ()
specifier|public
name|long
name|getNumOfBlocks
parameter_list|()
block|{
return|return
name|this
operator|.
name|translator
operator|.
name|getProtoOrBuilder
argument_list|()
operator|.
name|getNumOfBlocks
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setNumOfBlocksMissing (long blocks)
specifier|public
name|void
name|setNumOfBlocksMissing
parameter_list|(
name|long
name|blocks
parameter_list|)
block|{
name|this
operator|.
name|translator
operator|.
name|getBuilder
argument_list|()
operator|.
name|setNumOfBlocksMissing
argument_list|(
name|blocks
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getNumOfBlocksMissing ()
specifier|public
name|long
name|getNumOfBlocksMissing
parameter_list|()
block|{
return|return
name|this
operator|.
name|translator
operator|.
name|getProtoOrBuilder
argument_list|()
operator|.
name|getNumOfBlocksMissing
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setNumOfBlocksPendingReplication (long blocks)
specifier|public
name|void
name|setNumOfBlocksPendingReplication
parameter_list|(
name|long
name|blocks
parameter_list|)
block|{
name|this
operator|.
name|translator
operator|.
name|getBuilder
argument_list|()
operator|.
name|setNumOfBlocksPendingReplication
argument_list|(
name|blocks
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getNumOfBlocksPendingReplication ()
specifier|public
name|long
name|getNumOfBlocksPendingReplication
parameter_list|()
block|{
return|return
name|this
operator|.
name|translator
operator|.
name|getProtoOrBuilder
argument_list|()
operator|.
name|getNumOfBlocksPendingReplication
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setNumOfBlocksUnderReplicated (long blocks)
specifier|public
name|void
name|setNumOfBlocksUnderReplicated
parameter_list|(
name|long
name|blocks
parameter_list|)
block|{
name|this
operator|.
name|translator
operator|.
name|getBuilder
argument_list|()
operator|.
name|setNumOfBlocksUnderReplicated
argument_list|(
name|blocks
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getNumOfBlocksUnderReplicated ()
specifier|public
name|long
name|getNumOfBlocksUnderReplicated
parameter_list|()
block|{
return|return
name|this
operator|.
name|translator
operator|.
name|getProtoOrBuilder
argument_list|()
operator|.
name|getNumOfBlocksUnderReplicated
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setNumOfBlocksPendingDeletion (long blocks)
specifier|public
name|void
name|setNumOfBlocksPendingDeletion
parameter_list|(
name|long
name|blocks
parameter_list|)
block|{
name|this
operator|.
name|translator
operator|.
name|getBuilder
argument_list|()
operator|.
name|setNumOfBlocksPendingDeletion
argument_list|(
name|blocks
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getNumOfBlocksPendingDeletion ()
specifier|public
name|long
name|getNumOfBlocksPendingDeletion
parameter_list|()
block|{
return|return
name|this
operator|.
name|translator
operator|.
name|getProtoOrBuilder
argument_list|()
operator|.
name|getNumOfBlocksPendingDeletion
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setNumOfActiveDatanodes (int nodes)
specifier|public
name|void
name|setNumOfActiveDatanodes
parameter_list|(
name|int
name|nodes
parameter_list|)
block|{
name|this
operator|.
name|translator
operator|.
name|getBuilder
argument_list|()
operator|.
name|setNumOfActiveDatanodes
argument_list|(
name|nodes
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getNumOfActiveDatanodes ()
specifier|public
name|int
name|getNumOfActiveDatanodes
parameter_list|()
block|{
return|return
name|this
operator|.
name|translator
operator|.
name|getProtoOrBuilder
argument_list|()
operator|.
name|getNumOfActiveDatanodes
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setNumOfDeadDatanodes (int nodes)
specifier|public
name|void
name|setNumOfDeadDatanodes
parameter_list|(
name|int
name|nodes
parameter_list|)
block|{
name|this
operator|.
name|translator
operator|.
name|getBuilder
argument_list|()
operator|.
name|setNumOfDeadDatanodes
argument_list|(
name|nodes
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getNumOfDeadDatanodes ()
specifier|public
name|int
name|getNumOfDeadDatanodes
parameter_list|()
block|{
return|return
name|this
operator|.
name|translator
operator|.
name|getProtoOrBuilder
argument_list|()
operator|.
name|getNumOfDeadDatanodes
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setNumOfStaleDatanodes (int nodes)
specifier|public
name|void
name|setNumOfStaleDatanodes
parameter_list|(
name|int
name|nodes
parameter_list|)
block|{
name|this
operator|.
name|translator
operator|.
name|getBuilder
argument_list|()
operator|.
name|setNumOfStaleDatanodes
argument_list|(
name|nodes
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getNumOfStaleDatanodes ()
specifier|public
name|int
name|getNumOfStaleDatanodes
parameter_list|()
block|{
return|return
name|this
operator|.
name|translator
operator|.
name|getProtoOrBuilder
argument_list|()
operator|.
name|getNumOfStaleDatanodes
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setNumOfDecommissioningDatanodes (int nodes)
specifier|public
name|void
name|setNumOfDecommissioningDatanodes
parameter_list|(
name|int
name|nodes
parameter_list|)
block|{
name|this
operator|.
name|translator
operator|.
name|getBuilder
argument_list|()
operator|.
name|setNumOfDecommissioningDatanodes
argument_list|(
name|nodes
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getNumOfDecommissioningDatanodes ()
specifier|public
name|int
name|getNumOfDecommissioningDatanodes
parameter_list|()
block|{
return|return
name|this
operator|.
name|translator
operator|.
name|getProtoOrBuilder
argument_list|()
operator|.
name|getNumOfDecommissioningDatanodes
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setNumOfDecomActiveDatanodes (int nodes)
specifier|public
name|void
name|setNumOfDecomActiveDatanodes
parameter_list|(
name|int
name|nodes
parameter_list|)
block|{
name|this
operator|.
name|translator
operator|.
name|getBuilder
argument_list|()
operator|.
name|setNumOfDecomActiveDatanodes
argument_list|(
name|nodes
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getNumOfDecomActiveDatanodes ()
specifier|public
name|int
name|getNumOfDecomActiveDatanodes
parameter_list|()
block|{
return|return
name|this
operator|.
name|translator
operator|.
name|getProtoOrBuilder
argument_list|()
operator|.
name|getNumOfDecomActiveDatanodes
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setNumOfDecomDeadDatanodes (int nodes)
specifier|public
name|void
name|setNumOfDecomDeadDatanodes
parameter_list|(
name|int
name|nodes
parameter_list|)
block|{
name|this
operator|.
name|translator
operator|.
name|getBuilder
argument_list|()
operator|.
name|setNumOfDecomDeadDatanodes
argument_list|(
name|nodes
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getNumOfDecomDeadDatanodes ()
specifier|public
name|int
name|getNumOfDecomDeadDatanodes
parameter_list|()
block|{
return|return
name|this
operator|.
name|translator
operator|.
name|getProtoOrBuilder
argument_list|()
operator|.
name|getNumOfDecomDeadDatanodes
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setNumOfInMaintenanceLiveDataNodes (int nodes)
specifier|public
name|void
name|setNumOfInMaintenanceLiveDataNodes
parameter_list|(
name|int
name|nodes
parameter_list|)
block|{
name|this
operator|.
name|translator
operator|.
name|getBuilder
argument_list|()
operator|.
name|setNumOfInMaintenanceLiveDataNodes
argument_list|(
name|nodes
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getNumOfInMaintenanceLiveDataNodes ()
specifier|public
name|int
name|getNumOfInMaintenanceLiveDataNodes
parameter_list|()
block|{
return|return
name|this
operator|.
name|translator
operator|.
name|getProtoOrBuilder
argument_list|()
operator|.
name|getNumOfInMaintenanceLiveDataNodes
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setNumOfInMaintenanceDeadDataNodes (int nodes)
specifier|public
name|void
name|setNumOfInMaintenanceDeadDataNodes
parameter_list|(
name|int
name|nodes
parameter_list|)
block|{
name|this
operator|.
name|translator
operator|.
name|getBuilder
argument_list|()
operator|.
name|setNumOfInMaintenanceDeadDataNodes
argument_list|(
name|nodes
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getNumOfInMaintenanceDeadDataNodes ()
specifier|public
name|int
name|getNumOfInMaintenanceDeadDataNodes
parameter_list|()
block|{
return|return
name|this
operator|.
name|translator
operator|.
name|getProtoOrBuilder
argument_list|()
operator|.
name|getNumOfInMaintenanceDeadDataNodes
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setNumOfEnteringMaintenanceDataNodes (int nodes)
specifier|public
name|void
name|setNumOfEnteringMaintenanceDataNodes
parameter_list|(
name|int
name|nodes
parameter_list|)
block|{
name|this
operator|.
name|translator
operator|.
name|getBuilder
argument_list|()
operator|.
name|setNumOfEnteringMaintenanceDataNodes
argument_list|(
name|nodes
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getNumOfEnteringMaintenanceDataNodes ()
specifier|public
name|int
name|getNumOfEnteringMaintenanceDataNodes
parameter_list|()
block|{
return|return
name|this
operator|.
name|translator
operator|.
name|getProtoOrBuilder
argument_list|()
operator|.
name|getNumOfEnteringMaintenanceDataNodes
argument_list|()
return|;
block|}
block|}
end_class

end_unit

