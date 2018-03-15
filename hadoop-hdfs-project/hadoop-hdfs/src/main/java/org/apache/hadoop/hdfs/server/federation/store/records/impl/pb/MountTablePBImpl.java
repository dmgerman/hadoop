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
name|java
operator|.
name|util
operator|.
name|LinkedList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|permission
operator|.
name|FsPermission
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
name|federation
operator|.
name|protocol
operator|.
name|proto
operator|.
name|HdfsServerFederationProtos
operator|.
name|MountTableRecordProto
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
name|MountTableRecordProto
operator|.
name|DestOrder
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
name|MountTableRecordProtoOrBuilder
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
name|RemoteLocationProto
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
name|HdfsConstants
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
name|proto
operator|.
name|HdfsProtos
operator|.
name|QuotaUsageProto
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
name|resolver
operator|.
name|RemoteLocation
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
name|resolver
operator|.
name|order
operator|.
name|DestinationOrder
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
name|RouterAdminServer
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
name|RouterPermissionChecker
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
name|RouterQuotaUsage
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
name|MountTable
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
comment|/**  * Protobuf implementation of the MountTable record.  */
end_comment

begin_class
DECL|class|MountTablePBImpl
specifier|public
class|class
name|MountTablePBImpl
extends|extends
name|MountTable
implements|implements
name|PBRecord
block|{
specifier|private
name|FederationProtocolPBTranslator
argument_list|<
name|MountTableRecordProto
argument_list|,
name|Builder
argument_list|,
DECL|field|translator
name|MountTableRecordProtoOrBuilder
argument_list|>
name|translator
init|=
operator|new
name|FederationProtocolPBTranslator
argument_list|<
name|MountTableRecordProto
argument_list|,
name|Builder
argument_list|,
name|MountTableRecordProtoOrBuilder
argument_list|>
argument_list|(
name|MountTableRecordProto
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|MountTablePBImpl ()
specifier|public
name|MountTablePBImpl
parameter_list|()
block|{   }
DECL|method|MountTablePBImpl (MountTableRecordProto proto)
specifier|public
name|MountTablePBImpl
parameter_list|(
name|MountTableRecordProto
name|proto
parameter_list|)
block|{
name|this
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
name|MountTableRecordProto
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
DECL|method|getSourcePath ()
specifier|public
name|String
name|getSourcePath
parameter_list|()
block|{
name|MountTableRecordProtoOrBuilder
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
name|hasSrcPath
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
name|getSrcPath
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setSourcePath (String path)
specifier|public
name|void
name|setSourcePath
parameter_list|(
name|String
name|path
parameter_list|)
block|{
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
name|path
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearSrcPath
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|setSrcPath
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getDestinations ()
specifier|public
name|List
argument_list|<
name|RemoteLocation
argument_list|>
name|getDestinations
parameter_list|()
block|{
name|MountTableRecordProtoOrBuilder
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
name|proto
operator|.
name|getDestinationsCount
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|List
argument_list|<
name|RemoteLocation
argument_list|>
name|ret
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RemoteLocationProto
argument_list|>
name|destList
init|=
name|proto
operator|.
name|getDestinationsList
argument_list|()
decl_stmt|;
for|for
control|(
name|RemoteLocationProto
name|dest
range|:
name|destList
control|)
block|{
name|String
name|nsId
init|=
name|dest
operator|.
name|getNameserviceId
argument_list|()
decl_stmt|;
name|String
name|path
init|=
name|dest
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|RemoteLocation
name|loc
init|=
operator|new
name|RemoteLocation
argument_list|(
name|nsId
argument_list|,
name|path
argument_list|)
decl_stmt|;
name|ret
operator|.
name|add
argument_list|(
name|loc
argument_list|)
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
annotation|@
name|Override
DECL|method|setDestinations (final List<RemoteLocation> dests)
specifier|public
name|void
name|setDestinations
parameter_list|(
specifier|final
name|List
argument_list|<
name|RemoteLocation
argument_list|>
name|dests
parameter_list|)
block|{
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
name|builder
operator|.
name|clearDestinations
argument_list|()
expr_stmt|;
for|for
control|(
name|RemoteLocation
name|dest
range|:
name|dests
control|)
block|{
name|RemoteLocationProto
operator|.
name|Builder
name|itemBuilder
init|=
name|RemoteLocationProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|String
name|nsId
init|=
name|dest
operator|.
name|getNameserviceId
argument_list|()
decl_stmt|;
name|String
name|path
init|=
name|dest
operator|.
name|getDest
argument_list|()
decl_stmt|;
name|itemBuilder
operator|.
name|setNameserviceId
argument_list|(
name|nsId
argument_list|)
expr_stmt|;
name|itemBuilder
operator|.
name|setPath
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|RemoteLocationProto
name|item
init|=
name|itemBuilder
operator|.
name|build
argument_list|()
decl_stmt|;
name|builder
operator|.
name|addDestinations
argument_list|(
name|item
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|addDestination (String nsId, String path)
specifier|public
name|boolean
name|addDestination
parameter_list|(
name|String
name|nsId
parameter_list|,
name|String
name|path
parameter_list|)
block|{
comment|// Check if the location is already there
name|List
argument_list|<
name|RemoteLocation
argument_list|>
name|dests
init|=
name|getDestinations
argument_list|()
decl_stmt|;
for|for
control|(
name|RemoteLocation
name|dest
range|:
name|dests
control|)
block|{
if|if
condition|(
name|dest
operator|.
name|getNameserviceId
argument_list|()
operator|.
name|equals
argument_list|(
name|nsId
argument_list|)
operator|&&
name|dest
operator|.
name|getDest
argument_list|()
operator|.
name|equals
argument_list|(
name|path
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
comment|// Add it to the existing list
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
name|RemoteLocationProto
operator|.
name|Builder
name|itemBuilder
init|=
name|RemoteLocationProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|itemBuilder
operator|.
name|setNameserviceId
argument_list|(
name|nsId
argument_list|)
expr_stmt|;
name|itemBuilder
operator|.
name|setPath
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|RemoteLocationProto
name|item
init|=
name|itemBuilder
operator|.
name|build
argument_list|()
decl_stmt|;
name|builder
operator|.
name|addDestinations
argument_list|(
name|item
argument_list|)
expr_stmt|;
return|return
literal|true
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
DECL|method|isReadOnly ()
specifier|public
name|boolean
name|isReadOnly
parameter_list|()
block|{
name|MountTableRecordProtoOrBuilder
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
name|hasReadOnly
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|proto
operator|.
name|getReadOnly
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setReadOnly (boolean ro)
specifier|public
name|void
name|setReadOnly
parameter_list|(
name|boolean
name|ro
parameter_list|)
block|{
name|this
operator|.
name|translator
operator|.
name|getBuilder
argument_list|()
operator|.
name|setReadOnly
argument_list|(
name|ro
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getDestOrder ()
specifier|public
name|DestinationOrder
name|getDestOrder
parameter_list|()
block|{
name|MountTableRecordProtoOrBuilder
name|proto
init|=
name|this
operator|.
name|translator
operator|.
name|getProtoOrBuilder
argument_list|()
decl_stmt|;
return|return
name|convert
argument_list|(
name|proto
operator|.
name|getDestOrder
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setDestOrder (DestinationOrder order)
specifier|public
name|void
name|setDestOrder
parameter_list|(
name|DestinationOrder
name|order
parameter_list|)
block|{
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
name|order
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearDestOrder
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|setDestOrder
argument_list|(
name|convert
argument_list|(
name|order
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getOwnerName ()
specifier|public
name|String
name|getOwnerName
parameter_list|()
block|{
name|MountTableRecordProtoOrBuilder
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
name|hasOwnerName
argument_list|()
condition|)
block|{
return|return
name|RouterAdminServer
operator|.
name|getSuperUser
argument_list|()
return|;
block|}
return|return
name|proto
operator|.
name|getOwnerName
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setOwnerName (String owner)
specifier|public
name|void
name|setOwnerName
parameter_list|(
name|String
name|owner
parameter_list|)
block|{
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
name|owner
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearOwnerName
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|setOwnerName
argument_list|(
name|owner
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getGroupName ()
specifier|public
name|String
name|getGroupName
parameter_list|()
block|{
name|MountTableRecordProtoOrBuilder
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
name|hasGroupName
argument_list|()
condition|)
block|{
return|return
name|RouterAdminServer
operator|.
name|getSuperGroup
argument_list|()
return|;
block|}
return|return
name|proto
operator|.
name|getGroupName
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setGroupName (String group)
specifier|public
name|void
name|setGroupName
parameter_list|(
name|String
name|group
parameter_list|)
block|{
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
name|group
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearGroupName
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|setGroupName
argument_list|(
name|group
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getMode ()
specifier|public
name|FsPermission
name|getMode
parameter_list|()
block|{
name|MountTableRecordProtoOrBuilder
name|proto
init|=
name|this
operator|.
name|translator
operator|.
name|getProtoOrBuilder
argument_list|()
decl_stmt|;
name|short
name|mode
init|=
name|RouterPermissionChecker
operator|.
name|MOUNT_TABLE_PERMISSION_DEFAULT
decl_stmt|;
if|if
condition|(
name|proto
operator|.
name|hasMode
argument_list|()
condition|)
block|{
name|mode
operator|=
operator|(
name|short
operator|)
name|proto
operator|.
name|getMode
argument_list|()
expr_stmt|;
block|}
return|return
operator|new
name|FsPermission
argument_list|(
name|mode
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setMode (FsPermission mode)
specifier|public
name|void
name|setMode
parameter_list|(
name|FsPermission
name|mode
parameter_list|)
block|{
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
name|mode
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearMode
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|setMode
argument_list|(
name|mode
operator|.
name|toShort
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getQuota ()
specifier|public
name|RouterQuotaUsage
name|getQuota
parameter_list|()
block|{
name|MountTableRecordProtoOrBuilder
name|proto
init|=
name|this
operator|.
name|translator
operator|.
name|getProtoOrBuilder
argument_list|()
decl_stmt|;
name|long
name|nsQuota
init|=
name|HdfsConstants
operator|.
name|QUOTA_DONT_SET
decl_stmt|;
name|long
name|nsCount
init|=
name|RouterQuotaUsage
operator|.
name|QUOTA_USAGE_COUNT_DEFAULT
decl_stmt|;
name|long
name|ssQuota
init|=
name|HdfsConstants
operator|.
name|QUOTA_DONT_SET
decl_stmt|;
name|long
name|ssCount
init|=
name|RouterQuotaUsage
operator|.
name|QUOTA_USAGE_COUNT_DEFAULT
decl_stmt|;
if|if
condition|(
name|proto
operator|.
name|hasQuota
argument_list|()
condition|)
block|{
name|QuotaUsageProto
name|quotaProto
init|=
name|proto
operator|.
name|getQuota
argument_list|()
decl_stmt|;
name|nsQuota
operator|=
name|quotaProto
operator|.
name|getQuota
argument_list|()
expr_stmt|;
name|nsCount
operator|=
name|quotaProto
operator|.
name|getFileAndDirectoryCount
argument_list|()
expr_stmt|;
name|ssQuota
operator|=
name|quotaProto
operator|.
name|getSpaceQuota
argument_list|()
expr_stmt|;
name|ssCount
operator|=
name|quotaProto
operator|.
name|getSpaceConsumed
argument_list|()
expr_stmt|;
block|}
name|RouterQuotaUsage
operator|.
name|Builder
name|builder
init|=
operator|new
name|RouterQuotaUsage
operator|.
name|Builder
argument_list|()
operator|.
name|quota
argument_list|(
name|nsQuota
argument_list|)
operator|.
name|fileAndDirectoryCount
argument_list|(
name|nsCount
argument_list|)
operator|.
name|spaceQuota
argument_list|(
name|ssQuota
argument_list|)
operator|.
name|spaceConsumed
argument_list|(
name|ssCount
argument_list|)
decl_stmt|;
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setQuota (RouterQuotaUsage quota)
specifier|public
name|void
name|setQuota
parameter_list|(
name|RouterQuotaUsage
name|quota
parameter_list|)
block|{
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
name|quota
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearQuota
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|QuotaUsageProto
name|quotaUsage
init|=
name|QuotaUsageProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setFileAndDirectoryCount
argument_list|(
name|quota
operator|.
name|getFileAndDirectoryCount
argument_list|()
argument_list|)
operator|.
name|setQuota
argument_list|(
name|quota
operator|.
name|getQuota
argument_list|()
argument_list|)
operator|.
name|setSpaceConsumed
argument_list|(
name|quota
operator|.
name|getSpaceConsumed
argument_list|()
argument_list|)
operator|.
name|setSpaceQuota
argument_list|(
name|quota
operator|.
name|getSpaceQuota
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setQuota
argument_list|(
name|quotaUsage
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|convert (DestOrder order)
specifier|private
name|DestinationOrder
name|convert
parameter_list|(
name|DestOrder
name|order
parameter_list|)
block|{
switch|switch
condition|(
name|order
condition|)
block|{
case|case
name|LOCAL
case|:
return|return
name|DestinationOrder
operator|.
name|LOCAL
return|;
case|case
name|RANDOM
case|:
return|return
name|DestinationOrder
operator|.
name|RANDOM
return|;
case|case
name|HASH_ALL
case|:
return|return
name|DestinationOrder
operator|.
name|HASH_ALL
return|;
default|default:
return|return
name|DestinationOrder
operator|.
name|HASH
return|;
block|}
block|}
DECL|method|convert (DestinationOrder order)
specifier|private
name|DestOrder
name|convert
parameter_list|(
name|DestinationOrder
name|order
parameter_list|)
block|{
switch|switch
condition|(
name|order
condition|)
block|{
case|case
name|LOCAL
case|:
return|return
name|DestOrder
operator|.
name|LOCAL
return|;
case|case
name|RANDOM
case|:
return|return
name|DestOrder
operator|.
name|RANDOM
return|;
case|case
name|HASH_ALL
case|:
return|return
name|DestOrder
operator|.
name|HASH_ALL
return|;
default|default:
return|return
name|DestOrder
operator|.
name|HASH
return|;
block|}
block|}
block|}
end_class

end_unit

