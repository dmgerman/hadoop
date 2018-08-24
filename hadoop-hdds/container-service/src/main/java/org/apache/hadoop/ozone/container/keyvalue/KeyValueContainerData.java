begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.keyvalue
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
name|annotations
operator|.
name|VisibleForTesting
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
name|collect
operator|.
name|Lists
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|conf
operator|.
name|StorageSize
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
name|scm
operator|.
name|ScmConfigKeys
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
name|OzoneConsts
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
name|impl
operator|.
name|ContainerData
import|;
end_import

begin_import
import|import
name|org
operator|.
name|yaml
operator|.
name|snakeyaml
operator|.
name|nodes
operator|.
name|Tag
import|;
end_import

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
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|Math
operator|.
name|max
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
name|ozone
operator|.
name|OzoneConsts
operator|.
name|CHUNKS_PATH
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
name|ozone
operator|.
name|OzoneConsts
operator|.
name|CONTAINER_DB_TYPE
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
name|ozone
operator|.
name|OzoneConsts
operator|.
name|METADATA_PATH
import|;
end_import

begin_comment
comment|/**  * This class represents the KeyValueContainer metadata, which is the  * in-memory representation of container metadata and is represented on disk  * by the .container file.  */
end_comment

begin_class
DECL|class|KeyValueContainerData
specifier|public
class|class
name|KeyValueContainerData
extends|extends
name|ContainerData
block|{
comment|// Yaml Tag used for KeyValueContainerData.
DECL|field|KEYVALUE_YAML_TAG
specifier|public
specifier|static
specifier|final
name|Tag
name|KEYVALUE_YAML_TAG
init|=
operator|new
name|Tag
argument_list|(
literal|"KeyValueContainerData"
argument_list|)
decl_stmt|;
comment|// Fields need to be stored in .container file.
DECL|field|KV_YAML_FIELDS
specifier|private
specifier|static
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|KV_YAML_FIELDS
decl_stmt|;
comment|// Path to Container metadata Level DB/RocksDB Store and .container file.
DECL|field|metadataPath
specifier|private
name|String
name|metadataPath
decl_stmt|;
comment|// Path to Physical file system where chunks are stored.
DECL|field|chunksPath
specifier|private
name|String
name|chunksPath
decl_stmt|;
comment|//Type of DB used to store key to chunks mapping
DECL|field|containerDBType
specifier|private
name|String
name|containerDBType
decl_stmt|;
DECL|field|dbFile
specifier|private
name|File
name|dbFile
init|=
literal|null
decl_stmt|;
comment|/**    * Number of pending deletion blocks in KeyValueContainer.    */
DECL|field|numPendingDeletionBlocks
specifier|private
specifier|final
name|AtomicInteger
name|numPendingDeletionBlocks
decl_stmt|;
DECL|field|deleteTransactionId
specifier|private
name|long
name|deleteTransactionId
decl_stmt|;
static|static
block|{
comment|// Initialize YAML fields
name|KV_YAML_FIELDS
operator|=
name|Lists
operator|.
name|newArrayList
argument_list|()
expr_stmt|;
name|KV_YAML_FIELDS
operator|.
name|addAll
argument_list|(
name|YAML_FIELDS
argument_list|)
expr_stmt|;
name|KV_YAML_FIELDS
operator|.
name|add
argument_list|(
name|METADATA_PATH
argument_list|)
expr_stmt|;
name|KV_YAML_FIELDS
operator|.
name|add
argument_list|(
name|CHUNKS_PATH
argument_list|)
expr_stmt|;
name|KV_YAML_FIELDS
operator|.
name|add
argument_list|(
name|CONTAINER_DB_TYPE
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructs KeyValueContainerData object.    * @param id - ContainerId    * @param size - maximum size in GB of the container    */
DECL|method|KeyValueContainerData (long id, int size)
specifier|public
name|KeyValueContainerData
parameter_list|(
name|long
name|id
parameter_list|,
name|int
name|size
parameter_list|)
block|{
name|super
argument_list|(
name|ContainerProtos
operator|.
name|ContainerType
operator|.
name|KeyValueContainer
argument_list|,
name|id
argument_list|,
name|size
argument_list|)
expr_stmt|;
name|this
operator|.
name|numPendingDeletionBlocks
operator|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|this
operator|.
name|deleteTransactionId
operator|=
literal|0
expr_stmt|;
block|}
comment|/**    * Constructs KeyValueContainerData object.    * @param id - ContainerId    * @param layOutVersion    * @param size - maximum size in GB of the container    */
DECL|method|KeyValueContainerData (long id, int layOutVersion, int size)
specifier|public
name|KeyValueContainerData
parameter_list|(
name|long
name|id
parameter_list|,
name|int
name|layOutVersion
parameter_list|,
name|int
name|size
parameter_list|)
block|{
name|super
argument_list|(
name|ContainerProtos
operator|.
name|ContainerType
operator|.
name|KeyValueContainer
argument_list|,
name|id
argument_list|,
name|layOutVersion
argument_list|,
name|size
argument_list|)
expr_stmt|;
name|this
operator|.
name|numPendingDeletionBlocks
operator|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|this
operator|.
name|deleteTransactionId
operator|=
literal|0
expr_stmt|;
block|}
comment|/**    * Sets Container dbFile. This should be called only during creation of    * KeyValue container.    * @param containerDbFile    */
DECL|method|setDbFile (File containerDbFile)
specifier|public
name|void
name|setDbFile
parameter_list|(
name|File
name|containerDbFile
parameter_list|)
block|{
name|dbFile
operator|=
name|containerDbFile
expr_stmt|;
block|}
comment|/**    * Returns container DB file.    * @return dbFile    */
DECL|method|getDbFile ()
specifier|public
name|File
name|getDbFile
parameter_list|()
block|{
return|return
name|dbFile
return|;
block|}
comment|/**    * Returns container metadata path.    * @return - Physical path where container file and checksum is stored.    */
DECL|method|getMetadataPath ()
specifier|public
name|String
name|getMetadataPath
parameter_list|()
block|{
return|return
name|metadataPath
return|;
block|}
comment|/**    * Sets container metadata path.    *    * @param path - String.    */
DECL|method|setMetadataPath (String path)
specifier|public
name|void
name|setMetadataPath
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|this
operator|.
name|metadataPath
operator|=
name|path
expr_stmt|;
block|}
comment|/**    * Returns the path to base dir of the container.    * @return Path to base dir    */
DECL|method|getContainerPath ()
specifier|public
name|String
name|getContainerPath
parameter_list|()
block|{
if|if
condition|(
name|metadataPath
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|new
name|File
argument_list|(
name|metadataPath
argument_list|)
operator|.
name|getParent
argument_list|()
return|;
block|}
comment|/**    * Get chunks path.    * @return - Path where chunks are stored    */
DECL|method|getChunksPath ()
specifier|public
name|String
name|getChunksPath
parameter_list|()
block|{
return|return
name|chunksPath
return|;
block|}
comment|/**    * Set chunks Path.    * @param chunkPath - File path.    */
DECL|method|setChunksPath (String chunkPath)
specifier|public
name|void
name|setChunksPath
parameter_list|(
name|String
name|chunkPath
parameter_list|)
block|{
name|this
operator|.
name|chunksPath
operator|=
name|chunkPath
expr_stmt|;
block|}
comment|/**    * Returns the DBType used for the container.    * @return containerDBType    */
DECL|method|getContainerDBType ()
specifier|public
name|String
name|getContainerDBType
parameter_list|()
block|{
return|return
name|containerDBType
return|;
block|}
comment|/**    * Sets the DBType used for the container.    * @param containerDBType    */
DECL|method|setContainerDBType (String containerDBType)
specifier|public
name|void
name|setContainerDBType
parameter_list|(
name|String
name|containerDBType
parameter_list|)
block|{
name|this
operator|.
name|containerDBType
operator|=
name|containerDBType
expr_stmt|;
block|}
comment|/**    * Increase the count of pending deletion blocks.    *    * @param numBlocks increment number    */
DECL|method|incrPendingDeletionBlocks (int numBlocks)
specifier|public
name|void
name|incrPendingDeletionBlocks
parameter_list|(
name|int
name|numBlocks
parameter_list|)
block|{
name|this
operator|.
name|numPendingDeletionBlocks
operator|.
name|addAndGet
argument_list|(
name|numBlocks
argument_list|)
expr_stmt|;
block|}
comment|/**    * Decrease the count of pending deletion blocks.    *    * @param numBlocks decrement number    */
DECL|method|decrPendingDeletionBlocks (int numBlocks)
specifier|public
name|void
name|decrPendingDeletionBlocks
parameter_list|(
name|int
name|numBlocks
parameter_list|)
block|{
name|this
operator|.
name|numPendingDeletionBlocks
operator|.
name|addAndGet
argument_list|(
operator|-
literal|1
operator|*
name|numBlocks
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get the number of pending deletion blocks.    */
DECL|method|getNumPendingDeletionBlocks ()
specifier|public
name|int
name|getNumPendingDeletionBlocks
parameter_list|()
block|{
return|return
name|this
operator|.
name|numPendingDeletionBlocks
operator|.
name|get
argument_list|()
return|;
block|}
comment|/**    * Sets deleteTransactionId to latest delete transactionId for the container.    *    * @param transactionId latest transactionId of the container.    */
DECL|method|updateDeleteTransactionId (long transactionId)
specifier|public
name|void
name|updateDeleteTransactionId
parameter_list|(
name|long
name|transactionId
parameter_list|)
block|{
name|deleteTransactionId
operator|=
name|max
argument_list|(
name|transactionId
argument_list|,
name|deleteTransactionId
argument_list|)
expr_stmt|;
block|}
comment|/**    * Return the latest deleteTransactionId of the container.    */
DECL|method|getDeleteTransactionId ()
specifier|public
name|long
name|getDeleteTransactionId
parameter_list|()
block|{
return|return
name|deleteTransactionId
return|;
block|}
comment|/**    * Returns a ProtoBuf Message from ContainerData.    *    * @return Protocol Buffer Message    */
DECL|method|getProtoBufMessage ()
specifier|public
name|ContainerProtos
operator|.
name|ContainerData
name|getProtoBufMessage
parameter_list|()
block|{
name|ContainerProtos
operator|.
name|ContainerData
operator|.
name|Builder
name|builder
init|=
name|ContainerProtos
operator|.
name|ContainerData
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setContainerID
argument_list|(
name|this
operator|.
name|getContainerID
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setContainerPath
argument_list|(
name|this
operator|.
name|getMetadataPath
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setState
argument_list|(
name|this
operator|.
name|getState
argument_list|()
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
name|getMetadata
argument_list|()
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
if|if
condition|(
name|this
operator|.
name|getBytesUsed
argument_list|()
operator|>=
literal|0
condition|)
block|{
name|builder
operator|.
name|setBytesUsed
argument_list|(
name|this
operator|.
name|getBytesUsed
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|getContainerType
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setContainerType
argument_list|(
name|ContainerProtos
operator|.
name|ContainerType
operator|.
name|KeyValueContainer
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
DECL|method|getYamlFields ()
specifier|public
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|getYamlFields
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|KV_YAML_FIELDS
argument_list|)
return|;
block|}
comment|/**    * Constructs a KeyValueContainerData object from ProtoBuf classes.    *    * @param protoData - ProtoBuf Message    * @throws IOException    */
annotation|@
name|VisibleForTesting
DECL|method|getFromProtoBuf ( ContainerProtos.ContainerData protoData)
specifier|public
specifier|static
name|KeyValueContainerData
name|getFromProtoBuf
parameter_list|(
name|ContainerProtos
operator|.
name|ContainerData
name|protoData
parameter_list|)
throws|throws
name|IOException
block|{
comment|// TODO: Add containerMaxSize to ContainerProtos.ContainerData
name|StorageSize
name|storageSize
init|=
name|StorageSize
operator|.
name|parse
argument_list|(
name|ScmConfigKeys
operator|.
name|OZONE_SCM_CONTAINER_SIZE_DEFAULT
argument_list|)
decl_stmt|;
name|KeyValueContainerData
name|data
init|=
operator|new
name|KeyValueContainerData
argument_list|(
name|protoData
operator|.
name|getContainerID
argument_list|()
argument_list|,
operator|(
name|int
operator|)
name|storageSize
operator|.
name|getUnit
argument_list|()
operator|.
name|toBytes
argument_list|(
name|storageSize
operator|.
name|getValue
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
name|protoData
operator|.
name|getMetadataCount
argument_list|()
condition|;
name|x
operator|++
control|)
block|{
name|data
operator|.
name|addMetadata
argument_list|(
name|protoData
operator|.
name|getMetadata
argument_list|(
name|x
argument_list|)
operator|.
name|getKey
argument_list|()
argument_list|,
name|protoData
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
if|if
condition|(
name|protoData
operator|.
name|hasContainerPath
argument_list|()
condition|)
block|{
name|String
name|metadataPath
init|=
name|protoData
operator|.
name|getContainerPath
argument_list|()
operator|+
name|File
operator|.
name|separator
operator|+
name|OzoneConsts
operator|.
name|CONTAINER_META_PATH
decl_stmt|;
name|data
operator|.
name|setMetadataPath
argument_list|(
name|metadataPath
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|protoData
operator|.
name|hasState
argument_list|()
condition|)
block|{
name|data
operator|.
name|setState
argument_list|(
name|protoData
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|protoData
operator|.
name|hasBytesUsed
argument_list|()
condition|)
block|{
name|data
operator|.
name|setBytesUsed
argument_list|(
name|protoData
operator|.
name|getBytesUsed
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|data
return|;
block|}
block|}
end_class

end_unit

