begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.common.impl
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
name|impl
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
comment|//Number of pending deletion blocks in container.
DECL|field|numPendingDeletionBlocks
specifier|private
name|int
name|numPendingDeletionBlocks
decl_stmt|;
DECL|field|dbFile
specifier|private
name|File
name|dbFile
init|=
literal|null
decl_stmt|;
comment|/**    * Constructs KeyValueContainerData object.    * @param type - containerType    * @param id - ContainerId    */
DECL|method|KeyValueContainerData (ContainerProtos.ContainerType type, long id)
specifier|public
name|KeyValueContainerData
parameter_list|(
name|ContainerProtos
operator|.
name|ContainerType
name|type
parameter_list|,
name|long
name|id
parameter_list|)
block|{
name|super
argument_list|(
name|type
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|this
operator|.
name|numPendingDeletionBlocks
operator|=
literal|0
expr_stmt|;
block|}
comment|/**    * Constructs KeyValueContainerData object.    * @param type - containerType    * @param id - ContainerId    * @param layOutVersion    */
DECL|method|KeyValueContainerData (ContainerProtos.ContainerType type, long id, int layOutVersion)
specifier|public
name|KeyValueContainerData
parameter_list|(
name|ContainerProtos
operator|.
name|ContainerType
name|type
parameter_list|,
name|long
name|id
parameter_list|,
name|int
name|layOutVersion
parameter_list|)
block|{
name|super
argument_list|(
name|type
argument_list|,
name|id
argument_list|,
name|layOutVersion
argument_list|)
expr_stmt|;
name|this
operator|.
name|numPendingDeletionBlocks
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
comment|/**    * Returns container metadata path.    *    * @return - path    */
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
comment|/**    * Get chunks path.    * @return - Physical path where container file and checksum is stored.    */
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
comment|/**    * Returns the number of pending deletion blocks in container.    * @return numPendingDeletionBlocks    */
DECL|method|getNumPendingDeletionBlocks ()
specifier|public
name|int
name|getNumPendingDeletionBlocks
parameter_list|()
block|{
return|return
name|numPendingDeletionBlocks
return|;
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
operator|+=
name|numBlocks
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
operator|-=
name|numBlocks
expr_stmt|;
block|}
comment|/**    * Constructs a KeyValueContainerData object from ProtoBuf classes.    *    * @param protoData - ProtoBuf Message    * @throws IOException    */
DECL|method|getFromProtoBuf ( ContainerProtos.CreateContainerData protoData)
specifier|public
specifier|static
name|KeyValueContainerData
name|getFromProtoBuf
parameter_list|(
name|ContainerProtos
operator|.
name|CreateContainerData
name|protoData
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|containerID
decl_stmt|;
name|ContainerProtos
operator|.
name|ContainerType
name|containerType
decl_stmt|;
name|containerID
operator|=
name|protoData
operator|.
name|getContainerId
argument_list|()
expr_stmt|;
name|containerType
operator|=
name|protoData
operator|.
name|getContainerType
argument_list|()
expr_stmt|;
name|KeyValueContainerData
name|keyValueContainerData
init|=
operator|new
name|KeyValueContainerData
argument_list|(
name|containerType
argument_list|,
name|containerID
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
name|keyValueContainerData
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
return|return
name|keyValueContainerData
return|;
block|}
block|}
end_class

end_unit

