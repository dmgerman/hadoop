begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.keyvalue.impl
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
name|impl
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|primitives
operator|.
name|Longs
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
name|hdfs
operator|.
name|DFSUtil
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
name|keyvalue
operator|.
name|helpers
operator|.
name|BlockUtils
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
name|interfaces
operator|.
name|Container
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
name|interfaces
operator|.
name|BlockManager
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
name|BatchOperation
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
name|MetadataKeyFilters
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
operator|.
name|ReferenceCountedDB
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
name|ArrayList
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
name|UNKNOWN_BCSID
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
name|BCSID_MISMATCH
import|;
end_import

begin_comment
comment|/**  * This class is for performing block related operations on the KeyValue  * Container.  */
end_comment

begin_class
DECL|class|BlockManagerImpl
specifier|public
class|class
name|BlockManagerImpl
implements|implements
name|BlockManager
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
name|BlockManagerImpl
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|blockCommitSequenceIdKey
specifier|private
specifier|static
name|byte
index|[]
name|blockCommitSequenceIdKey
init|=
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
name|OzoneConsts
operator|.
name|BLOCK_COMMIT_SEQUENCE_ID_PREFIX
argument_list|)
decl_stmt|;
DECL|field|config
specifier|private
name|Configuration
name|config
decl_stmt|;
comment|/**    * Constructs a Block Manager.    *    * @param conf - Ozone configuration    */
DECL|method|BlockManagerImpl (Configuration conf)
specifier|public
name|BlockManagerImpl
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|conf
argument_list|,
literal|"Config cannot be null"
argument_list|)
expr_stmt|;
name|this
operator|.
name|config
operator|=
name|conf
expr_stmt|;
block|}
comment|/**    * Puts or overwrites a block.    *    * @param container - Container for which block need to be added.    * @param data     - BlockData.    * @return length of the block.    * @throws IOException    */
DECL|method|putBlock (Container container, BlockData data)
specifier|public
name|long
name|putBlock
parameter_list|(
name|Container
name|container
parameter_list|,
name|BlockData
name|data
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|data
argument_list|,
literal|"BlockData cannot be null for put "
operator|+
literal|"operation."
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkState
argument_list|(
name|data
operator|.
name|getContainerID
argument_list|()
operator|>=
literal|0
argument_list|,
literal|"Container Id "
operator|+
literal|"cannot be negative"
argument_list|)
expr_stmt|;
comment|// We are not locking the key manager since LevelDb serializes all actions
comment|// against a single DB. We rely on DB level locking to avoid conflicts.
try|try
init|(
name|ReferenceCountedDB
name|db
init|=
name|BlockUtils
operator|.
name|getDB
argument_list|(
operator|(
name|KeyValueContainerData
operator|)
name|container
operator|.
name|getContainerData
argument_list|()
argument_list|,
name|config
argument_list|)
init|)
block|{
comment|// This is a post condition that acts as a hint to the user.
comment|// Should never fail.
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|db
argument_list|,
literal|"DB cannot be null here"
argument_list|)
expr_stmt|;
name|long
name|bcsId
init|=
name|data
operator|.
name|getBlockCommitSequenceId
argument_list|()
decl_stmt|;
name|long
name|containerBCSId
init|=
operator|(
operator|(
name|KeyValueContainerData
operator|)
name|container
operator|.
name|getContainerData
argument_list|()
operator|)
operator|.
name|getBlockCommitSequenceId
argument_list|()
decl_stmt|;
comment|// default blockCommitSequenceId for any block is 0. It the putBlock
comment|// request is not coming via Ratis(for test scenarios), it will be 0.
comment|// In such cases, we should overwrite the block as well
if|if
condition|(
name|bcsId
operator|!=
literal|0
condition|)
block|{
if|if
condition|(
name|bcsId
operator|<=
name|containerBCSId
condition|)
block|{
comment|// Since the blockCommitSequenceId stored in the db is greater than
comment|// equal to blockCommitSequenceId to be updated, it means the putBlock
comment|// transaction is reapplied in the ContainerStateMachine on restart.
comment|// It also implies that the given block must already exist in the db.
comment|// just log and return
name|LOG
operator|.
name|warn
argument_list|(
literal|"blockCommitSequenceId "
operator|+
name|containerBCSId
operator|+
literal|" in the Container Db is greater than"
operator|+
literal|" the supplied value "
operator|+
name|bcsId
operator|+
literal|" .Ignoring it"
argument_list|)
expr_stmt|;
return|return
name|data
operator|.
name|getSize
argument_list|()
return|;
block|}
block|}
comment|// update the blockData as well as BlockCommitSequenceId here
name|BatchOperation
name|batch
init|=
operator|new
name|BatchOperation
argument_list|()
decl_stmt|;
name|batch
operator|.
name|put
argument_list|(
name|Longs
operator|.
name|toByteArray
argument_list|(
name|data
operator|.
name|getLocalID
argument_list|()
argument_list|)
argument_list|,
name|data
operator|.
name|getProtoBufMessage
argument_list|()
operator|.
name|toByteArray
argument_list|()
argument_list|)
expr_stmt|;
name|batch
operator|.
name|put
argument_list|(
name|blockCommitSequenceIdKey
argument_list|,
name|Longs
operator|.
name|toByteArray
argument_list|(
name|bcsId
argument_list|)
argument_list|)
expr_stmt|;
name|db
operator|.
name|getStore
argument_list|()
operator|.
name|writeBatch
argument_list|(
name|batch
argument_list|)
expr_stmt|;
name|container
operator|.
name|updateBlockCommitSequenceId
argument_list|(
name|bcsId
argument_list|)
expr_stmt|;
comment|// Increment keycount here
name|container
operator|.
name|getContainerData
argument_list|()
operator|.
name|incrKeyCount
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Block "
operator|+
name|data
operator|.
name|getBlockID
argument_list|()
operator|+
literal|" successfully committed with bcsId "
operator|+
name|bcsId
operator|+
literal|" chunk size "
operator|+
name|data
operator|.
name|getChunks
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|data
operator|.
name|getSize
argument_list|()
return|;
block|}
block|}
comment|/**    * Gets an existing block.    *    * @param container - Container from which block need to be fetched.    * @param blockID - BlockID of the block.    * @return Key Data.    * @throws IOException    */
annotation|@
name|Override
DECL|method|getBlock (Container container, BlockID blockID)
specifier|public
name|BlockData
name|getBlock
parameter_list|(
name|Container
name|container
parameter_list|,
name|BlockID
name|blockID
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|bcsId
init|=
name|blockID
operator|.
name|getBlockCommitSequenceId
argument_list|()
decl_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|blockID
argument_list|,
literal|"BlockID cannot be null in GetBlock request"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|container
argument_list|,
literal|"Container cannot be null"
argument_list|)
expr_stmt|;
name|KeyValueContainerData
name|containerData
init|=
operator|(
name|KeyValueContainerData
operator|)
name|container
operator|.
name|getContainerData
argument_list|()
decl_stmt|;
try|try
init|(
name|ReferenceCountedDB
name|db
init|=
name|BlockUtils
operator|.
name|getDB
argument_list|(
name|containerData
argument_list|,
name|config
argument_list|)
init|)
block|{
comment|// This is a post condition that acts as a hint to the user.
comment|// Should never fail.
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|db
argument_list|,
literal|"DB cannot be null here"
argument_list|)
expr_stmt|;
name|long
name|containerBCSId
init|=
name|containerData
operator|.
name|getBlockCommitSequenceId
argument_list|()
decl_stmt|;
if|if
condition|(
name|containerBCSId
operator|<
name|bcsId
condition|)
block|{
throw|throw
operator|new
name|StorageContainerException
argument_list|(
literal|"Unable to find the block with bcsID "
operator|+
name|bcsId
operator|+
literal|" .Container "
operator|+
name|container
operator|.
name|getContainerData
argument_list|()
operator|.
name|getContainerID
argument_list|()
operator|+
literal|" bcsId is "
operator|+
name|containerBCSId
operator|+
literal|"."
argument_list|,
name|UNKNOWN_BCSID
argument_list|)
throw|;
block|}
name|byte
index|[]
name|kData
init|=
name|db
operator|.
name|getStore
argument_list|()
operator|.
name|get
argument_list|(
name|Longs
operator|.
name|toByteArray
argument_list|(
name|blockID
operator|.
name|getLocalID
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|kData
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|StorageContainerException
argument_list|(
literal|"Unable to find the block."
operator|+
name|blockID
argument_list|,
name|NO_SUCH_BLOCK
argument_list|)
throw|;
block|}
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
name|kData
argument_list|)
decl_stmt|;
name|long
name|id
init|=
name|blockData
operator|.
name|getBlockID
argument_list|()
operator|.
name|getBlockCommitSequenceId
argument_list|()
decl_stmt|;
if|if
condition|(
name|id
operator|<
name|bcsId
condition|)
block|{
throw|throw
operator|new
name|StorageContainerException
argument_list|(
literal|"bcsId "
operator|+
name|bcsId
operator|+
literal|" mismatches with existing block Id "
operator|+
name|id
operator|+
literal|" for block "
operator|+
name|blockID
operator|+
literal|"."
argument_list|,
name|BCSID_MISMATCH
argument_list|)
throw|;
block|}
return|return
name|BlockData
operator|.
name|getFromProtoBuf
argument_list|(
name|blockData
argument_list|)
return|;
block|}
block|}
comment|/**    * Returns the length of the committed block.    *    * @param container - Container from which block need to be fetched.    * @param blockID - BlockID of the block.    * @return length of the block.    * @throws IOException in case, the block key does not exist in db.    */
annotation|@
name|Override
DECL|method|getCommittedBlockLength (Container container, BlockID blockID)
specifier|public
name|long
name|getCommittedBlockLength
parameter_list|(
name|Container
name|container
parameter_list|,
name|BlockID
name|blockID
parameter_list|)
throws|throws
name|IOException
block|{
name|KeyValueContainerData
name|containerData
init|=
operator|(
name|KeyValueContainerData
operator|)
name|container
operator|.
name|getContainerData
argument_list|()
decl_stmt|;
try|try
init|(
name|ReferenceCountedDB
name|db
init|=
name|BlockUtils
operator|.
name|getDB
argument_list|(
name|containerData
argument_list|,
name|config
argument_list|)
init|)
block|{
comment|// This is a post condition that acts as a hint to the user.
comment|// Should never fail.
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|db
argument_list|,
literal|"DB cannot be null here"
argument_list|)
expr_stmt|;
name|byte
index|[]
name|kData
init|=
name|db
operator|.
name|getStore
argument_list|()
operator|.
name|get
argument_list|(
name|Longs
operator|.
name|toByteArray
argument_list|(
name|blockID
operator|.
name|getLocalID
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|kData
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|StorageContainerException
argument_list|(
literal|"Unable to find the block."
argument_list|,
name|NO_SUCH_BLOCK
argument_list|)
throw|;
block|}
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
name|kData
argument_list|)
decl_stmt|;
return|return
name|blockData
operator|.
name|getSize
argument_list|()
return|;
block|}
block|}
comment|/**    * Deletes an existing block.    *    * @param container - Container from which block need to be deleted.    * @param blockID - ID of the block.    * @throws StorageContainerException    */
DECL|method|deleteBlock (Container container, BlockID blockID)
specifier|public
name|void
name|deleteBlock
parameter_list|(
name|Container
name|container
parameter_list|,
name|BlockID
name|blockID
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|blockID
argument_list|,
literal|"block ID cannot be null."
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkState
argument_list|(
name|blockID
operator|.
name|getContainerID
argument_list|()
operator|>=
literal|0
argument_list|,
literal|"Container ID cannot be negative."
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkState
argument_list|(
name|blockID
operator|.
name|getLocalID
argument_list|()
operator|>=
literal|0
argument_list|,
literal|"Local ID cannot be negative."
argument_list|)
expr_stmt|;
name|KeyValueContainerData
name|cData
init|=
operator|(
name|KeyValueContainerData
operator|)
name|container
operator|.
name|getContainerData
argument_list|()
decl_stmt|;
try|try
init|(
name|ReferenceCountedDB
name|db
init|=
name|BlockUtils
operator|.
name|getDB
argument_list|(
name|cData
argument_list|,
name|config
argument_list|)
init|)
block|{
comment|// This is a post condition that acts as a hint to the user.
comment|// Should never fail.
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|db
argument_list|,
literal|"DB cannot be null here"
argument_list|)
expr_stmt|;
comment|// Note : There is a race condition here, since get and delete
comment|// are not atomic. Leaving it here since the impact is refusing
comment|// to delete a Block which might have just gotten inserted after
comment|// the get check.
name|byte
index|[]
name|kKey
init|=
name|Longs
operator|.
name|toByteArray
argument_list|(
name|blockID
operator|.
name|getLocalID
argument_list|()
argument_list|)
decl_stmt|;
name|byte
index|[]
name|kData
init|=
name|db
operator|.
name|getStore
argument_list|()
operator|.
name|get
argument_list|(
name|kKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|kData
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|StorageContainerException
argument_list|(
literal|"Unable to find the block."
argument_list|,
name|NO_SUCH_BLOCK
argument_list|)
throw|;
block|}
name|db
operator|.
name|getStore
argument_list|()
operator|.
name|delete
argument_list|(
name|kKey
argument_list|)
expr_stmt|;
comment|// Decrement blockcount here
name|container
operator|.
name|getContainerData
argument_list|()
operator|.
name|decrKeyCount
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * List blocks in a container.    *    * @param container - Container from which blocks need to be listed.    * @param startLocalID  - Key to start from, 0 to begin.    * @param count    - Number of blocks to return.    * @return List of Blocks that match the criteria.    */
annotation|@
name|Override
DECL|method|listBlock (Container container, long startLocalID, int count)
specifier|public
name|List
argument_list|<
name|BlockData
argument_list|>
name|listBlock
parameter_list|(
name|Container
name|container
parameter_list|,
name|long
name|startLocalID
parameter_list|,
name|int
name|count
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|container
argument_list|,
literal|"container cannot be null"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkState
argument_list|(
name|startLocalID
operator|>=
literal|0
argument_list|,
literal|"startLocal ID cannot be "
operator|+
literal|"negative"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|count
operator|>
literal|0
argument_list|,
literal|"Count must be a positive number."
argument_list|)
expr_stmt|;
name|container
operator|.
name|readLock
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|BlockData
argument_list|>
name|result
init|=
literal|null
decl_stmt|;
name|KeyValueContainerData
name|cData
init|=
operator|(
name|KeyValueContainerData
operator|)
name|container
operator|.
name|getContainerData
argument_list|()
decl_stmt|;
try|try
init|(
name|ReferenceCountedDB
name|db
init|=
name|BlockUtils
operator|.
name|getDB
argument_list|(
name|cData
argument_list|,
name|config
argument_list|)
init|)
block|{
name|result
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|byte
index|[]
name|startKeyInBytes
init|=
name|Longs
operator|.
name|toByteArray
argument_list|(
name|startLocalID
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|byte
index|[]
argument_list|,
name|byte
index|[]
argument_list|>
argument_list|>
name|range
init|=
name|db
operator|.
name|getStore
argument_list|()
operator|.
name|getSequentialRangeKVs
argument_list|(
name|startKeyInBytes
argument_list|,
name|count
argument_list|,
name|MetadataKeyFilters
operator|.
name|getNormalKeyFilter
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|byte
index|[]
argument_list|,
name|byte
index|[]
argument_list|>
name|entry
range|:
name|range
control|)
block|{
name|BlockData
name|value
init|=
name|BlockUtils
operator|.
name|getBlockData
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
decl_stmt|;
name|BlockData
name|data
init|=
operator|new
name|BlockData
argument_list|(
name|value
operator|.
name|getBlockID
argument_list|()
argument_list|)
decl_stmt|;
name|result
operator|.
name|add
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
block|}
comment|/**    * Shutdown KeyValueContainerManager.    */
DECL|method|shutdown ()
specifier|public
name|void
name|shutdown
parameter_list|()
block|{
name|BlockUtils
operator|.
name|shutdownCache
argument_list|(
name|ContainerCache
operator|.
name|getInstance
argument_list|(
name|config
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

