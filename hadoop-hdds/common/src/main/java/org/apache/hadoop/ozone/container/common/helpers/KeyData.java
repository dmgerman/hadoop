begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
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
name|client
operator|.
name|BlockID
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
name|Collections
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
comment|/**  * Helper class to convert Protobuf to Java classes.  */
end_comment

begin_class
DECL|class|KeyData
specifier|public
class|class
name|KeyData
block|{
DECL|field|blockID
specifier|private
specifier|final
name|BlockID
name|blockID
decl_stmt|;
DECL|field|metadata
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|metadata
decl_stmt|;
comment|/**    * Please note : when we are working with keys, we don't care what they point    * to. So we We don't read chunkinfo nor validate them. It is responsibility    * of higher layer like ozone. We just read and write data from network.    */
DECL|field|chunks
specifier|private
name|List
argument_list|<
name|ContainerProtos
operator|.
name|ChunkInfo
argument_list|>
name|chunks
decl_stmt|;
comment|/**    * Constructs a KeyData Object.    *    * @param blockID    */
DECL|method|KeyData (BlockID blockID)
specifier|public
name|KeyData
parameter_list|(
name|BlockID
name|blockID
parameter_list|)
block|{
name|this
operator|.
name|blockID
operator|=
name|blockID
expr_stmt|;
name|this
operator|.
name|metadata
operator|=
operator|new
name|TreeMap
argument_list|<>
argument_list|()
expr_stmt|;
block|}
comment|/**    * Returns a keyData object from the protobuf data.    *    * @param data - Protobuf data.    * @return - KeyData    * @throws IOException    */
DECL|method|getFromProtoBuf (ContainerProtos.KeyData data)
specifier|public
specifier|static
name|KeyData
name|getFromProtoBuf
parameter_list|(
name|ContainerProtos
operator|.
name|KeyData
name|data
parameter_list|)
throws|throws
name|IOException
block|{
name|KeyData
name|keyData
init|=
operator|new
name|KeyData
argument_list|(
name|BlockID
operator|.
name|getFromProtobuf
argument_list|(
name|data
operator|.
name|getBlockID
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
name|data
operator|.
name|getMetadataCount
argument_list|()
condition|;
name|x
operator|++
control|)
block|{
name|keyData
operator|.
name|addMetadata
argument_list|(
name|data
operator|.
name|getMetadata
argument_list|(
name|x
argument_list|)
operator|.
name|getKey
argument_list|()
argument_list|,
name|data
operator|.
name|getMetadata
argument_list|(
name|x
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|keyData
operator|.
name|setChunks
argument_list|(
name|data
operator|.
name|getChunksList
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|keyData
return|;
block|}
comment|/**    * Returns a Protobuf message from KeyData.    * @return Proto Buf Message.    */
DECL|method|getProtoBufMessage ()
specifier|public
name|ContainerProtos
operator|.
name|KeyData
name|getProtoBufMessage
parameter_list|()
block|{
name|ContainerProtos
operator|.
name|KeyData
operator|.
name|Builder
name|builder
init|=
name|ContainerProtos
operator|.
name|KeyData
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setBlockID
argument_list|(
name|this
operator|.
name|blockID
operator|.
name|getDatanodeBlockIDProtobuf
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addAllChunks
argument_list|(
name|this
operator|.
name|chunks
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|metadata
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|ContainerProtos
operator|.
name|KeyValue
operator|.
name|Builder
name|keyValBuilder
init|=
name|ContainerProtos
operator|.
name|KeyValue
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|addMetadata
argument_list|(
name|keyValBuilder
operator|.
name|setKey
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
operator|.
name|setValue
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
comment|/**    * Adds metadata.    *    * @param key   - Key    * @param value - Value    * @throws IOException    */
DECL|method|addMetadata (String key, String value)
specifier|public
specifier|synchronized
name|void
name|addMetadata
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|value
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|this
operator|.
name|metadata
operator|.
name|containsKey
argument_list|(
name|key
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"This key already exists. Key "
operator|+
name|key
argument_list|)
throw|;
block|}
name|metadata
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
DECL|method|getMetadata ()
specifier|public
specifier|synchronized
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getMetadata
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
name|this
operator|.
name|metadata
argument_list|)
return|;
block|}
comment|/**    * Returns value of a key.    */
DECL|method|getValue (String key)
specifier|public
specifier|synchronized
name|String
name|getValue
parameter_list|(
name|String
name|key
parameter_list|)
block|{
return|return
name|metadata
operator|.
name|get
argument_list|(
name|key
argument_list|)
return|;
block|}
comment|/**    * Deletes a metadata entry from the map.    *    * @param key - Key    */
DECL|method|deleteKey (String key)
specifier|public
specifier|synchronized
name|void
name|deleteKey
parameter_list|(
name|String
name|key
parameter_list|)
block|{
name|metadata
operator|.
name|remove
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns chunks list.    *    * @return list of chunkinfo.    */
DECL|method|getChunks ()
specifier|public
name|List
argument_list|<
name|ContainerProtos
operator|.
name|ChunkInfo
argument_list|>
name|getChunks
parameter_list|()
block|{
return|return
name|chunks
return|;
block|}
comment|/**    * Returns container ID.    * @return long.    */
DECL|method|getContainerID ()
specifier|public
name|long
name|getContainerID
parameter_list|()
block|{
return|return
name|blockID
operator|.
name|getContainerID
argument_list|()
return|;
block|}
comment|/**    * Returns LocalID.    * @return long.    */
DECL|method|getLocalID ()
specifier|public
name|long
name|getLocalID
parameter_list|()
block|{
return|return
name|blockID
operator|.
name|getLocalID
argument_list|()
return|;
block|}
comment|/**    * Return Block ID.    * @return BlockID.    */
DECL|method|getBlockID ()
specifier|public
name|BlockID
name|getBlockID
parameter_list|()
block|{
return|return
name|blockID
return|;
block|}
comment|/**    * Sets Chunk list.    *    * @param chunks - List of chunks.    */
DECL|method|setChunks (List<ContainerProtos.ChunkInfo> chunks)
specifier|public
name|void
name|setChunks
parameter_list|(
name|List
argument_list|<
name|ContainerProtos
operator|.
name|ChunkInfo
argument_list|>
name|chunks
parameter_list|)
block|{
name|this
operator|.
name|chunks
operator|=
name|chunks
expr_stmt|;
block|}
comment|/**    * Get the total size of chunks allocated for the key.    * @return total size of the key.    */
DECL|method|getSize ()
specifier|public
name|long
name|getSize
parameter_list|()
block|{
return|return
name|chunks
operator|.
name|parallelStream
argument_list|()
operator|.
name|mapToLong
argument_list|(
name|e
lambda|->
name|e
operator|.
name|getLen
argument_list|()
argument_list|)
operator|.
name|sum
argument_list|()
return|;
block|}
block|}
end_class

end_unit

