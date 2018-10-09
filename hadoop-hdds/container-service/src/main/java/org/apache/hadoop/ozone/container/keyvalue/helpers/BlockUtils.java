begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.keyvalue.helpers
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
name|keyvalue
operator|.
name|helpers
package|;
end_package

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
name|hdds
operator|.
name|protocol
operator|.
name|datanode
operator|.
name|proto
operator|.
name|ContainerProtos
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
name|ContainerCommandResponseProto
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
name|GetBlockResponseProto
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
name|GetCommittedBlockLengthResponseProto
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
name|PutBlockResponseProto
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
name|scm
operator|.
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|StorageContainerException
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
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|BlockData
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
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|ContainerUtils
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
name|container
operator|.
name|keyvalue
operator|.
name|KeyValueContainerData
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
name|container
operator|.
name|common
operator|.
name|utils
operator|.
name|ContainerCache
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
name|utils
operator|.
name|MetadataStore
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
import|import static
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
name|Result
operator|.
name|NO_SUCH_BLOCK
import|;
end_import

begin_import
import|import static
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
name|Result
operator|.
name|UNABLE_TO_READ_METADATA_DB
import|;
end_import

begin_comment
comment|/**  * Utils functions to help block functions.  */
end_comment

begin_class
DECL|class|BlockUtils
specifier|public
specifier|final
class|class
name|BlockUtils
block|{
comment|/** Never constructed. **/
DECL|method|BlockUtils ()
specifier|private
name|BlockUtils
parameter_list|()
block|{    }
comment|/**    * Get a DB handler for a given container.    * If the handler doesn't exist in cache yet, first create one and    * add into cache. This function is called with containerManager    * ReadLock held.    *    * @param containerData containerData.    * @param conf configuration.    * @return MetadataStore handle.    * @throws StorageContainerException    */
DECL|method|getDB (KeyValueContainerData containerData, Configuration conf)
specifier|public
specifier|static
name|MetadataStore
name|getDB
parameter_list|(
name|KeyValueContainerData
name|containerData
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|StorageContainerException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|containerData
argument_list|)
expr_stmt|;
name|ContainerCache
name|cache
init|=
name|ContainerCache
operator|.
name|getInstance
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|cache
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|containerData
operator|.
name|getDbFile
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
return|return
name|cache
operator|.
name|getDB
argument_list|(
name|containerData
operator|.
name|getContainerID
argument_list|()
argument_list|,
name|containerData
operator|.
name|getContainerDBType
argument_list|()
argument_list|,
name|containerData
operator|.
name|getDbFile
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|String
name|message
init|=
name|String
operator|.
name|format
argument_list|(
literal|"Error opening DB. Container:%s "
operator|+
literal|"ContainerPath:%s"
argument_list|,
name|containerData
operator|.
name|getContainerID
argument_list|()
argument_list|,
name|containerData
operator|.
name|getDbFile
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
throw|throw
operator|new
name|StorageContainerException
argument_list|(
name|message
argument_list|,
name|UNABLE_TO_READ_METADATA_DB
argument_list|)
throw|;
block|}
block|}
comment|/**    * Remove a DB handler from cache.    *    * @param container - Container data.    * @param conf - Configuration.    */
DECL|method|removeDB (KeyValueContainerData container, Configuration conf)
specifier|public
specifier|static
name|void
name|removeDB
parameter_list|(
name|KeyValueContainerData
name|container
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|container
argument_list|)
expr_stmt|;
name|ContainerCache
name|cache
init|=
name|ContainerCache
operator|.
name|getInstance
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|cache
argument_list|)
expr_stmt|;
name|cache
operator|.
name|removeDB
argument_list|(
name|container
operator|.
name|getContainerID
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Shutdown all DB Handles.    *    * @param cache - Cache for DB Handles.    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|shutdownCache (ContainerCache cache)
specifier|public
specifier|static
name|void
name|shutdownCache
parameter_list|(
name|ContainerCache
name|cache
parameter_list|)
block|{
name|cache
operator|.
name|shutdownCache
argument_list|()
expr_stmt|;
block|}
comment|/**    * Parses the {@link BlockData} from a bytes array.    *    * @param bytes Block data in bytes.    * @return Block data.    * @throws IOException if the bytes array is malformed or invalid.    */
DECL|method|getBlockData (byte[] bytes)
specifier|public
specifier|static
name|BlockData
name|getBlockData
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|ContainerProtos
operator|.
name|BlockData
name|blockData
init|=
name|ContainerProtos
operator|.
name|BlockData
operator|.
name|parseFrom
argument_list|(
name|bytes
argument_list|)
decl_stmt|;
name|BlockData
name|data
init|=
name|BlockData
operator|.
name|getFromProtoBuf
argument_list|(
name|blockData
argument_list|)
decl_stmt|;
return|return
name|data
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|StorageContainerException
argument_list|(
literal|"Failed to parse block data from "
operator|+
literal|"the bytes array."
argument_list|,
name|NO_SUCH_BLOCK
argument_list|)
throw|;
block|}
block|}
comment|/**    * Returns putBlock response success.    * @param msg - Request.    * @return Response.    */
DECL|method|putBlockResponseSuccess ( ContainerCommandRequestProto msg, long blockLength)
specifier|public
specifier|static
name|ContainerCommandResponseProto
name|putBlockResponseSuccess
parameter_list|(
name|ContainerCommandRequestProto
name|msg
parameter_list|,
name|long
name|blockLength
parameter_list|)
block|{
name|ContainerProtos
operator|.
name|BlockData
name|blockData
init|=
name|msg
operator|.
name|getPutBlock
argument_list|()
operator|.
name|getBlockData
argument_list|()
decl_stmt|;
name|GetCommittedBlockLengthResponseProto
operator|.
name|Builder
name|committedBlockLengthResponseBuilder
init|=
name|getCommittedBlockLengthResponseBuilder
argument_list|(
name|blockLength
argument_list|,
name|blockData
operator|.
name|getBlockID
argument_list|()
argument_list|)
decl_stmt|;
name|committedBlockLengthResponseBuilder
operator|.
name|setBlockCommitSequenceId
argument_list|(
name|blockData
operator|.
name|getBlockCommitSequenceId
argument_list|()
argument_list|)
expr_stmt|;
name|PutBlockResponseProto
operator|.
name|Builder
name|putKeyResponse
init|=
name|PutBlockResponseProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|putKeyResponse
operator|.
name|setCommittedBlockLength
argument_list|(
name|committedBlockLengthResponseBuilder
argument_list|)
expr_stmt|;
name|ContainerProtos
operator|.
name|ContainerCommandResponseProto
operator|.
name|Builder
name|builder
init|=
name|ContainerUtils
operator|.
name|getSuccessResponseBuilder
argument_list|(
name|msg
argument_list|)
decl_stmt|;
name|builder
operator|.
name|setPutBlock
argument_list|(
name|putKeyResponse
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
comment|/**    * Returns successful blockResponse.    * @param msg - Request.    * @return Response.    */
DECL|method|getBlockResponseSuccess ( ContainerCommandRequestProto msg)
specifier|public
specifier|static
name|ContainerCommandResponseProto
name|getBlockResponseSuccess
parameter_list|(
name|ContainerCommandRequestProto
name|msg
parameter_list|)
block|{
return|return
name|ContainerUtils
operator|.
name|getSuccessResponse
argument_list|(
name|msg
argument_list|)
return|;
block|}
DECL|method|getBlockDataResponse ( ContainerCommandRequestProto msg, BlockData data)
specifier|public
specifier|static
name|ContainerCommandResponseProto
name|getBlockDataResponse
parameter_list|(
name|ContainerCommandRequestProto
name|msg
parameter_list|,
name|BlockData
name|data
parameter_list|)
block|{
name|GetBlockResponseProto
operator|.
name|Builder
name|getBlock
init|=
name|ContainerProtos
operator|.
name|GetBlockResponseProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|getBlock
operator|.
name|setBlockData
argument_list|(
name|data
operator|.
name|getProtoBufMessage
argument_list|()
argument_list|)
expr_stmt|;
name|ContainerProtos
operator|.
name|ContainerCommandResponseProto
operator|.
name|Builder
name|builder
init|=
name|ContainerUtils
operator|.
name|getSuccessResponseBuilder
argument_list|(
name|msg
argument_list|)
decl_stmt|;
name|builder
operator|.
name|setGetBlock
argument_list|(
name|getBlock
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
comment|/**    * Returns successful getCommittedBlockLength Response.    * @param msg - Request.    * @return Response.    */
DECL|method|getBlockLengthResponse ( ContainerCommandRequestProto msg, long blockLength)
specifier|public
specifier|static
name|ContainerCommandResponseProto
name|getBlockLengthResponse
parameter_list|(
name|ContainerCommandRequestProto
name|msg
parameter_list|,
name|long
name|blockLength
parameter_list|)
block|{
name|GetCommittedBlockLengthResponseProto
operator|.
name|Builder
name|committedBlockLengthResponseBuilder
init|=
name|getCommittedBlockLengthResponseBuilder
argument_list|(
name|blockLength
argument_list|,
name|msg
operator|.
name|getGetCommittedBlockLength
argument_list|()
operator|.
name|getBlockID
argument_list|()
argument_list|)
decl_stmt|;
name|ContainerProtos
operator|.
name|ContainerCommandResponseProto
operator|.
name|Builder
name|builder
init|=
name|ContainerUtils
operator|.
name|getSuccessResponseBuilder
argument_list|(
name|msg
argument_list|)
decl_stmt|;
name|builder
operator|.
name|setGetCommittedBlockLength
argument_list|(
name|committedBlockLengthResponseBuilder
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
specifier|private
specifier|static
name|GetCommittedBlockLengthResponseProto
operator|.
name|Builder
DECL|method|getCommittedBlockLengthResponseBuilder (long blockLength, ContainerProtos.DatanodeBlockID blockID)
name|getCommittedBlockLengthResponseBuilder
parameter_list|(
name|long
name|blockLength
parameter_list|,
name|ContainerProtos
operator|.
name|DatanodeBlockID
name|blockID
parameter_list|)
block|{
name|ContainerProtos
operator|.
name|GetCommittedBlockLengthResponseProto
operator|.
name|Builder
name|getCommittedBlockLengthResponseBuilder
init|=
name|ContainerProtos
operator|.
name|GetCommittedBlockLengthResponseProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|getCommittedBlockLengthResponseBuilder
operator|.
name|setBlockLength
argument_list|(
name|blockLength
argument_list|)
expr_stmt|;
name|getCommittedBlockLengthResponseBuilder
operator|.
name|setBlockID
argument_list|(
name|blockID
argument_list|)
expr_stmt|;
return|return
name|getCommittedBlockLengthResponseBuilder
return|;
block|}
block|}
end_class

end_unit

