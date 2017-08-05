begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.federation.store.protocol.impl.pb
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
name|protocol
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
name|AddMountTableEntryRequestProto
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
name|AddMountTableEntryRequestProtoOrBuilder
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
name|MountTableRecordProto
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
name|AddMountTableEntryRequest
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
name|MountTable
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
name|impl
operator|.
name|pb
operator|.
name|MountTablePBImpl
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
name|impl
operator|.
name|pb
operator|.
name|PBRecord
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
comment|/**  * Protobuf implementation of the state store API object  * AddMountTableEntryRequest.  */
end_comment

begin_class
DECL|class|AddMountTableEntryRequestPBImpl
specifier|public
class|class
name|AddMountTableEntryRequestPBImpl
extends|extends
name|AddMountTableEntryRequest
implements|implements
name|PBRecord
block|{
specifier|private
name|FederationProtocolPBTranslator
argument_list|<
name|AddMountTableEntryRequestProto
argument_list|,
name|AddMountTableEntryRequestProto
operator|.
name|Builder
argument_list|,
DECL|field|translator
name|AddMountTableEntryRequestProtoOrBuilder
argument_list|>
name|translator
init|=
operator|new
name|FederationProtocolPBTranslator
argument_list|<
name|AddMountTableEntryRequestProto
argument_list|,
name|AddMountTableEntryRequestProto
operator|.
name|Builder
argument_list|,
name|AddMountTableEntryRequestProtoOrBuilder
argument_list|>
argument_list|(
name|AddMountTableEntryRequestProto
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|AddMountTableEntryRequestPBImpl ()
specifier|public
name|AddMountTableEntryRequestPBImpl
parameter_list|()
block|{   }
DECL|method|AddMountTableEntryRequestPBImpl (AddMountTableEntryRequestProto proto)
specifier|public
name|AddMountTableEntryRequestPBImpl
parameter_list|(
name|AddMountTableEntryRequestProto
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
DECL|method|getProto ()
specifier|public
name|AddMountTableEntryRequestProto
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
DECL|method|getEntry ()
specifier|public
name|MountTable
name|getEntry
parameter_list|()
block|{
name|MountTableRecordProto
name|entryProto
init|=
name|this
operator|.
name|translator
operator|.
name|getProtoOrBuilder
argument_list|()
operator|.
name|getEntry
argument_list|()
decl_stmt|;
return|return
operator|new
name|MountTablePBImpl
argument_list|(
name|entryProto
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setEntry (MountTable mount)
specifier|public
name|void
name|setEntry
parameter_list|(
name|MountTable
name|mount
parameter_list|)
block|{
if|if
condition|(
name|mount
operator|instanceof
name|MountTablePBImpl
condition|)
block|{
name|MountTablePBImpl
name|mountPB
init|=
operator|(
name|MountTablePBImpl
operator|)
name|mount
decl_stmt|;
name|MountTableRecordProto
name|mountProto
init|=
name|mountPB
operator|.
name|getProto
argument_list|()
decl_stmt|;
name|translator
operator|.
name|getBuilder
argument_list|()
operator|.
name|setEntry
argument_list|(
name|mountProto
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

