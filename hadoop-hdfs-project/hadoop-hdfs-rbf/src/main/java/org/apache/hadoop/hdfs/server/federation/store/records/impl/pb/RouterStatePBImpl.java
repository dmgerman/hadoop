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
name|RouterRecordProto
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
name|RouterRecordProto
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
name|RouterRecordProtoOrBuilder
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
name|StateStoreVersionRecordProto
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
name|router
operator|.
name|RouterServiceState
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
name|driver
operator|.
name|StateStoreSerializer
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
name|RouterState
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
name|StateStoreVersion
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
comment|/**  * Protobuf implementation of the RouterState record.  */
end_comment

begin_class
DECL|class|RouterStatePBImpl
specifier|public
class|class
name|RouterStatePBImpl
extends|extends
name|RouterState
implements|implements
name|PBRecord
block|{
specifier|private
name|FederationProtocolPBTranslator
argument_list|<
name|RouterRecordProto
argument_list|,
name|Builder
argument_list|,
DECL|field|translator
name|RouterRecordProtoOrBuilder
argument_list|>
name|translator
init|=
operator|new
name|FederationProtocolPBTranslator
argument_list|<
name|RouterRecordProto
argument_list|,
name|Builder
argument_list|,
name|RouterRecordProtoOrBuilder
argument_list|>
argument_list|(
name|RouterRecordProto
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|RouterStatePBImpl ()
specifier|public
name|RouterStatePBImpl
parameter_list|()
block|{   }
DECL|method|RouterStatePBImpl (RouterRecordProto proto)
specifier|public
name|RouterStatePBImpl
parameter_list|(
name|RouterRecordProto
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
name|RouterRecordProto
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
DECL|method|setAddress (String address)
specifier|public
name|void
name|setAddress
parameter_list|(
name|String
name|address
parameter_list|)
block|{
name|RouterRecordProto
operator|.
name|Builder
name|builder
init|=
name|this
operator|.
name|translator
operator|.
name|getBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|address
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearAddress
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|setAddress
argument_list|(
name|address
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getAddress ()
specifier|public
name|String
name|getAddress
parameter_list|()
block|{
name|RouterRecordProtoOrBuilder
name|proto
init|=
name|this
operator|.
name|translator
operator|.
name|getProtoOrBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|proto
operator|.
name|hasAddress
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|proto
operator|.
name|getAddress
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setStateStoreVersion (StateStoreVersion version)
specifier|public
name|void
name|setStateStoreVersion
parameter_list|(
name|StateStoreVersion
name|version
parameter_list|)
block|{
name|RouterRecordProto
operator|.
name|Builder
name|builder
init|=
name|this
operator|.
name|translator
operator|.
name|getBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|version
operator|instanceof
name|StateStoreVersionPBImpl
condition|)
block|{
name|StateStoreVersionPBImpl
name|versionPB
init|=
operator|(
name|StateStoreVersionPBImpl
operator|)
name|version
decl_stmt|;
name|StateStoreVersionRecordProto
name|versionProto
init|=
operator|(
name|StateStoreVersionRecordProto
operator|)
name|versionPB
operator|.
name|getProto
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setStateStoreVersion
argument_list|(
name|versionProto
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|clearStateStoreVersion
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getStateStoreVersion ()
specifier|public
name|StateStoreVersion
name|getStateStoreVersion
parameter_list|()
throws|throws
name|IOException
block|{
name|RouterRecordProtoOrBuilder
name|proto
init|=
name|this
operator|.
name|translator
operator|.
name|getProtoOrBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|proto
operator|.
name|hasStateStoreVersion
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|StateStoreVersionRecordProto
name|versionProto
init|=
name|proto
operator|.
name|getStateStoreVersion
argument_list|()
decl_stmt|;
name|StateStoreVersion
name|version
init|=
name|StateStoreSerializer
operator|.
name|newRecord
argument_list|(
name|StateStoreVersion
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|version
operator|instanceof
name|StateStoreVersionPBImpl
condition|)
block|{
name|StateStoreVersionPBImpl
name|versionPB
init|=
operator|(
name|StateStoreVersionPBImpl
operator|)
name|version
decl_stmt|;
name|versionPB
operator|.
name|setProto
argument_list|(
name|versionProto
argument_list|)
expr_stmt|;
return|return
name|versionPB
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Cannot get State Store version"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getStatus ()
specifier|public
name|RouterServiceState
name|getStatus
parameter_list|()
block|{
name|RouterRecordProtoOrBuilder
name|proto
init|=
name|this
operator|.
name|translator
operator|.
name|getProtoOrBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|proto
operator|.
name|hasStatus
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|RouterServiceState
operator|.
name|valueOf
argument_list|(
name|proto
operator|.
name|getStatus
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setStatus (RouterServiceState newStatus)
specifier|public
name|void
name|setStatus
parameter_list|(
name|RouterServiceState
name|newStatus
parameter_list|)
block|{
name|RouterRecordProto
operator|.
name|Builder
name|builder
init|=
name|this
operator|.
name|translator
operator|.
name|getBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|newStatus
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearStatus
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|setStatus
argument_list|(
name|newStatus
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getVersion ()
specifier|public
name|String
name|getVersion
parameter_list|()
block|{
name|RouterRecordProtoOrBuilder
name|proto
init|=
name|this
operator|.
name|translator
operator|.
name|getProtoOrBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|proto
operator|.
name|hasVersion
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|proto
operator|.
name|getVersion
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setVersion (String version)
specifier|public
name|void
name|setVersion
parameter_list|(
name|String
name|version
parameter_list|)
block|{
name|RouterRecordProto
operator|.
name|Builder
name|builder
init|=
name|this
operator|.
name|translator
operator|.
name|getBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|version
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearVersion
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|setVersion
argument_list|(
name|version
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getCompileInfo ()
specifier|public
name|String
name|getCompileInfo
parameter_list|()
block|{
name|RouterRecordProtoOrBuilder
name|proto
init|=
name|this
operator|.
name|translator
operator|.
name|getProtoOrBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|proto
operator|.
name|hasCompileInfo
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|proto
operator|.
name|getCompileInfo
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setCompileInfo (String info)
specifier|public
name|void
name|setCompileInfo
parameter_list|(
name|String
name|info
parameter_list|)
block|{
name|RouterRecordProto
operator|.
name|Builder
name|builder
init|=
name|this
operator|.
name|translator
operator|.
name|getBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|info
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearCompileInfo
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|setCompileInfo
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|setDateStarted (long dateStarted)
specifier|public
name|void
name|setDateStarted
parameter_list|(
name|long
name|dateStarted
parameter_list|)
block|{
name|this
operator|.
name|translator
operator|.
name|getBuilder
argument_list|()
operator|.
name|setDateStarted
argument_list|(
name|dateStarted
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getDateStarted ()
specifier|public
name|long
name|getDateStarted
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
name|getDateStarted
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setDateModified (long time)
specifier|public
name|void
name|setDateModified
parameter_list|(
name|long
name|time
parameter_list|)
block|{
name|this
operator|.
name|translator
operator|.
name|getBuilder
argument_list|()
operator|.
name|setDateModified
argument_list|(
name|time
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getDateModified ()
specifier|public
name|long
name|getDateModified
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
name|getDateModified
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setDateCreated (long time)
specifier|public
name|void
name|setDateCreated
parameter_list|(
name|long
name|time
parameter_list|)
block|{
name|this
operator|.
name|translator
operator|.
name|getBuilder
argument_list|()
operator|.
name|setDateCreated
argument_list|(
name|time
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getDateCreated ()
specifier|public
name|long
name|getDateCreated
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
name|getDateCreated
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setAdminAddress (String adminAddress)
specifier|public
name|void
name|setAdminAddress
parameter_list|(
name|String
name|adminAddress
parameter_list|)
block|{
name|this
operator|.
name|translator
operator|.
name|getBuilder
argument_list|()
operator|.
name|setAdminAddress
argument_list|(
name|adminAddress
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getAdminAddress ()
specifier|public
name|String
name|getAdminAddress
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
name|getAdminAddress
argument_list|()
return|;
block|}
block|}
end_class

end_unit

