begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.common.helpers
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|helpers
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|client
operator|.
name|BlockID
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
name|hdds
operator|.
name|protocol
operator|.
name|datanode
operator|.
name|proto
operator|.
name|ContainerProtos
operator|.
name|ContainerCommandRequestProto
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
name|hdds
operator|.
name|protocol
operator|.
name|datanode
operator|.
name|proto
operator|.
name|ContainerProtos
operator|.
name|Type
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
name|ozone
operator|.
name|audit
operator|.
name|DNAction
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
import|;
end_import

begin_comment
comment|/**  * Utilities for converting protobuf classes to Java classes.  */
end_comment

begin_class
DECL|class|ContainerCommandRequestPBHelper
specifier|public
specifier|final
class|class
name|ContainerCommandRequestPBHelper
block|{
DECL|field|LOG
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ContainerCommandRequestPBHelper
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|ContainerCommandRequestPBHelper ()
specifier|private
name|ContainerCommandRequestPBHelper
parameter_list|()
block|{   }
DECL|method|getAuditParams ( ContainerCommandRequestProto msg)
specifier|public
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getAuditParams
parameter_list|(
name|ContainerCommandRequestProto
name|msg
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|auditParams
init|=
operator|new
name|TreeMap
argument_list|<>
argument_list|()
decl_stmt|;
name|Type
name|cmdType
init|=
name|msg
operator|.
name|getCmdType
argument_list|()
decl_stmt|;
name|String
name|containerID
init|=
name|String
operator|.
name|valueOf
argument_list|(
name|msg
operator|.
name|getContainerID
argument_list|()
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|cmdType
condition|)
block|{
case|case
name|CreateContainer
case|:
name|auditParams
operator|.
name|put
argument_list|(
literal|"containerID"
argument_list|,
name|containerID
argument_list|)
expr_stmt|;
name|auditParams
operator|.
name|put
argument_list|(
literal|"containerType"
argument_list|,
name|msg
operator|.
name|getCreateContainer
argument_list|()
operator|.
name|getContainerType
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|auditParams
return|;
case|case
name|ReadContainer
case|:
name|auditParams
operator|.
name|put
argument_list|(
literal|"containerID"
argument_list|,
name|containerID
argument_list|)
expr_stmt|;
return|return
name|auditParams
return|;
case|case
name|UpdateContainer
case|:
name|auditParams
operator|.
name|put
argument_list|(
literal|"containerID"
argument_list|,
name|containerID
argument_list|)
expr_stmt|;
name|auditParams
operator|.
name|put
argument_list|(
literal|"forceUpdate"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|msg
operator|.
name|getUpdateContainer
argument_list|()
operator|.
name|getForceUpdate
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|auditParams
return|;
case|case
name|DeleteContainer
case|:
name|auditParams
operator|.
name|put
argument_list|(
literal|"containerID"
argument_list|,
name|containerID
argument_list|)
expr_stmt|;
name|auditParams
operator|.
name|put
argument_list|(
literal|"forceDelete"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|msg
operator|.
name|getDeleteContainer
argument_list|()
operator|.
name|getForceDelete
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|auditParams
return|;
case|case
name|ListContainer
case|:
name|auditParams
operator|.
name|put
argument_list|(
literal|"startContainerID"
argument_list|,
name|containerID
argument_list|)
expr_stmt|;
name|auditParams
operator|.
name|put
argument_list|(
literal|"count"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|msg
operator|.
name|getListContainer
argument_list|()
operator|.
name|getCount
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|auditParams
return|;
case|case
name|PutBlock
case|:
try|try
block|{
name|auditParams
operator|.
name|put
argument_list|(
literal|"blockData"
argument_list|,
name|BlockData
operator|.
name|getFromProtoBuf
argument_list|(
name|msg
operator|.
name|getPutBlock
argument_list|()
operator|.
name|getBlockData
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"Encountered error parsing BlockData from protobuf: "
operator|+
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
return|return
name|auditParams
return|;
case|case
name|GetBlock
case|:
name|auditParams
operator|.
name|put
argument_list|(
literal|"blockData"
argument_list|,
name|BlockID
operator|.
name|getFromProtobuf
argument_list|(
name|msg
operator|.
name|getGetBlock
argument_list|()
operator|.
name|getBlockID
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|auditParams
return|;
case|case
name|DeleteBlock
case|:
name|auditParams
operator|.
name|put
argument_list|(
literal|"blockData"
argument_list|,
name|BlockID
operator|.
name|getFromProtobuf
argument_list|(
name|msg
operator|.
name|getDeleteBlock
argument_list|()
operator|.
name|getBlockID
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|auditParams
return|;
case|case
name|ListBlock
case|:
name|auditParams
operator|.
name|put
argument_list|(
literal|"startLocalID"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|msg
operator|.
name|getListBlock
argument_list|()
operator|.
name|getStartLocalID
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|auditParams
operator|.
name|put
argument_list|(
literal|"count"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|msg
operator|.
name|getListBlock
argument_list|()
operator|.
name|getCount
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|auditParams
return|;
case|case
name|ReadChunk
case|:
name|auditParams
operator|.
name|put
argument_list|(
literal|"blockData"
argument_list|,
name|BlockID
operator|.
name|getFromProtobuf
argument_list|(
name|msg
operator|.
name|getReadChunk
argument_list|()
operator|.
name|getBlockID
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|auditParams
return|;
case|case
name|DeleteChunk
case|:
name|auditParams
operator|.
name|put
argument_list|(
literal|"blockData"
argument_list|,
name|BlockID
operator|.
name|getFromProtobuf
argument_list|(
name|msg
operator|.
name|getDeleteChunk
argument_list|()
operator|.
name|getBlockID
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|auditParams
return|;
case|case
name|WriteChunk
case|:
name|auditParams
operator|.
name|put
argument_list|(
literal|"blockData"
argument_list|,
name|BlockID
operator|.
name|getFromProtobuf
argument_list|(
name|msg
operator|.
name|getWriteChunk
argument_list|()
operator|.
name|getBlockID
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|auditParams
return|;
case|case
name|ListChunk
case|:
name|auditParams
operator|.
name|put
argument_list|(
literal|"blockData"
argument_list|,
name|BlockID
operator|.
name|getFromProtobuf
argument_list|(
name|msg
operator|.
name|getListChunk
argument_list|()
operator|.
name|getBlockID
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|auditParams
operator|.
name|put
argument_list|(
literal|"prevChunkName"
argument_list|,
name|msg
operator|.
name|getListChunk
argument_list|()
operator|.
name|getPrevChunkName
argument_list|()
argument_list|)
expr_stmt|;
name|auditParams
operator|.
name|put
argument_list|(
literal|"count"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|msg
operator|.
name|getListChunk
argument_list|()
operator|.
name|getCount
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|auditParams
return|;
case|case
name|CompactChunk
case|:
return|return
literal|null
return|;
comment|//CompactChunk operation
case|case
name|PutSmallFile
case|:
try|try
block|{
name|auditParams
operator|.
name|put
argument_list|(
literal|"blockData"
argument_list|,
name|BlockData
operator|.
name|getFromProtoBuf
argument_list|(
name|msg
operator|.
name|getPutSmallFile
argument_list|()
operator|.
name|getBlock
argument_list|()
operator|.
name|getBlockData
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"Encountered error parsing BlockData from protobuf: "
operator|+
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|auditParams
return|;
case|case
name|GetSmallFile
case|:
name|auditParams
operator|.
name|put
argument_list|(
literal|"blockData"
argument_list|,
name|BlockID
operator|.
name|getFromProtobuf
argument_list|(
name|msg
operator|.
name|getGetSmallFile
argument_list|()
operator|.
name|getBlock
argument_list|()
operator|.
name|getBlockID
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|auditParams
return|;
case|case
name|CloseContainer
case|:
name|auditParams
operator|.
name|put
argument_list|(
literal|"containerID"
argument_list|,
name|containerID
argument_list|)
expr_stmt|;
return|return
name|auditParams
return|;
case|case
name|GetCommittedBlockLength
case|:
name|auditParams
operator|.
name|put
argument_list|(
literal|"blockData"
argument_list|,
name|BlockID
operator|.
name|getFromProtobuf
argument_list|(
name|msg
operator|.
name|getGetCommittedBlockLength
argument_list|()
operator|.
name|getBlockID
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|auditParams
return|;
default|default :
name|LOG
operator|.
name|debug
argument_list|(
literal|"Invalid command type - "
operator|+
name|cmdType
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
DECL|method|getAuditAction (Type cmdType)
specifier|public
specifier|static
name|DNAction
name|getAuditAction
parameter_list|(
name|Type
name|cmdType
parameter_list|)
block|{
switch|switch
condition|(
name|cmdType
condition|)
block|{
case|case
name|CreateContainer
case|:
return|return
name|DNAction
operator|.
name|CREATE_CONTAINER
return|;
case|case
name|ReadContainer
case|:
return|return
name|DNAction
operator|.
name|READ_CONTAINER
return|;
case|case
name|UpdateContainer
case|:
return|return
name|DNAction
operator|.
name|UPDATE_CONTAINER
return|;
case|case
name|DeleteContainer
case|:
return|return
name|DNAction
operator|.
name|DELETE_CONTAINER
return|;
case|case
name|ListContainer
case|:
return|return
name|DNAction
operator|.
name|LIST_CONTAINER
return|;
case|case
name|PutBlock
case|:
return|return
name|DNAction
operator|.
name|PUT_BLOCK
return|;
case|case
name|GetBlock
case|:
return|return
name|DNAction
operator|.
name|GET_BLOCK
return|;
case|case
name|DeleteBlock
case|:
return|return
name|DNAction
operator|.
name|DELETE_BLOCK
return|;
case|case
name|ListBlock
case|:
return|return
name|DNAction
operator|.
name|LIST_BLOCK
return|;
case|case
name|ReadChunk
case|:
return|return
name|DNAction
operator|.
name|READ_CHUNK
return|;
case|case
name|DeleteChunk
case|:
return|return
name|DNAction
operator|.
name|DELETE_CHUNK
return|;
case|case
name|WriteChunk
case|:
return|return
name|DNAction
operator|.
name|WRITE_CHUNK
return|;
case|case
name|ListChunk
case|:
return|return
name|DNAction
operator|.
name|LIST_CHUNK
return|;
case|case
name|CompactChunk
case|:
return|return
name|DNAction
operator|.
name|COMPACT_CHUNK
return|;
case|case
name|PutSmallFile
case|:
return|return
name|DNAction
operator|.
name|PUT_SMALL_FILE
return|;
case|case
name|GetSmallFile
case|:
return|return
name|DNAction
operator|.
name|GET_SMALL_FILE
return|;
case|case
name|CloseContainer
case|:
return|return
name|DNAction
operator|.
name|CLOSE_CONTAINER
return|;
case|case
name|GetCommittedBlockLength
case|:
return|return
name|DNAction
operator|.
name|GET_COMMITTED_BLOCK_LENGTH
return|;
default|default :
name|LOG
operator|.
name|debug
argument_list|(
literal|"Invalid command type - "
operator|+
name|cmdType
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit

