begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.client.io
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|client
operator|.
name|io
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
name|hdfs
operator|.
name|ozone
operator|.
name|protocol
operator|.
name|proto
operator|.
name|ContainerProtos
operator|.
name|Result
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
name|ksm
operator|.
name|helpers
operator|.
name|KsmKeyLocationInfoGroup
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
name|protocol
operator|.
name|proto
operator|.
name|OzoneProtos
operator|.
name|ReplicationType
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
name|protocol
operator|.
name|proto
operator|.
name|OzoneProtos
operator|.
name|ReplicationFactor
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
name|ksm
operator|.
name|helpers
operator|.
name|KsmKeyArgs
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
name|ksm
operator|.
name|helpers
operator|.
name|KsmKeyInfo
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
name|ksm
operator|.
name|helpers
operator|.
name|KsmKeyLocationInfo
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
name|ksm
operator|.
name|helpers
operator|.
name|OpenKeySession
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
name|ksm
operator|.
name|protocolPB
operator|.
name|KeySpaceManagerProtocolClientSideTranslatorPB
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
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerLocationProtocolProtos
operator|.
name|ObjectStageChangeRequestProto
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
name|scm
operator|.
name|XceiverClientManager
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
name|scm
operator|.
name|XceiverClientSpi
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
name|scm
operator|.
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|Pipeline
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
name|scm
operator|.
name|protocolPB
operator|.
name|StorageContainerLocationProtocolClientSideTranslatorPB
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
name|scm
operator|.
name|storage
operator|.
name|ChunkOutputStream
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
name|scm
operator|.
name|storage
operator|.
name|ContainerProtocolCalls
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
name|io
operator|.
name|OutputStream
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

begin_comment
comment|/**  * Maintaining a list of ChunkInputStream. Write based on offset.  *  * Note that this may write to multiple containers in one write call. In case  * that first container succeeded but later ones failed, the succeeded writes  * are not rolled back.  *  * TODO : currently not support multi-thread access.  */
end_comment

begin_class
DECL|class|ChunkGroupOutputStream
specifier|public
class|class
name|ChunkGroupOutputStream
extends|extends
name|OutputStream
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ChunkGroupOutputStream
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// array list's get(index) is O(1)
DECL|field|streamEntries
specifier|private
specifier|final
name|ArrayList
argument_list|<
name|ChunkOutputStreamEntry
argument_list|>
name|streamEntries
decl_stmt|;
DECL|field|currentStreamIndex
specifier|private
name|int
name|currentStreamIndex
decl_stmt|;
DECL|field|byteOffset
specifier|private
name|long
name|byteOffset
decl_stmt|;
DECL|field|ksmClient
specifier|private
specifier|final
name|KeySpaceManagerProtocolClientSideTranslatorPB
name|ksmClient
decl_stmt|;
specifier|private
specifier|final
DECL|field|scmClient
name|StorageContainerLocationProtocolClientSideTranslatorPB
name|scmClient
decl_stmt|;
DECL|field|keyArgs
specifier|private
specifier|final
name|KsmKeyArgs
name|keyArgs
decl_stmt|;
DECL|field|openID
specifier|private
specifier|final
name|int
name|openID
decl_stmt|;
DECL|field|xceiverClientManager
specifier|private
specifier|final
name|XceiverClientManager
name|xceiverClientManager
decl_stmt|;
DECL|field|chunkSize
specifier|private
specifier|final
name|int
name|chunkSize
decl_stmt|;
DECL|field|requestID
specifier|private
specifier|final
name|String
name|requestID
decl_stmt|;
comment|/**    * A constructor for testing purpose only.    */
annotation|@
name|VisibleForTesting
DECL|method|ChunkGroupOutputStream ()
specifier|public
name|ChunkGroupOutputStream
parameter_list|()
block|{
name|streamEntries
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|ksmClient
operator|=
literal|null
expr_stmt|;
name|scmClient
operator|=
literal|null
expr_stmt|;
name|keyArgs
operator|=
literal|null
expr_stmt|;
name|openID
operator|=
operator|-
literal|1
expr_stmt|;
name|xceiverClientManager
operator|=
literal|null
expr_stmt|;
name|chunkSize
operator|=
literal|0
expr_stmt|;
name|requestID
operator|=
literal|null
expr_stmt|;
block|}
comment|/**    * For testing purpose only. Not building output stream from blocks, but    * taking from externally.    *    * @param outputStream    * @param length    */
annotation|@
name|VisibleForTesting
DECL|method|addStream (OutputStream outputStream, long length)
specifier|public
specifier|synchronized
name|void
name|addStream
parameter_list|(
name|OutputStream
name|outputStream
parameter_list|,
name|long
name|length
parameter_list|)
block|{
name|streamEntries
operator|.
name|add
argument_list|(
operator|new
name|ChunkOutputStreamEntry
argument_list|(
name|outputStream
argument_list|,
name|length
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getStreamEntries ()
specifier|public
name|List
argument_list|<
name|ChunkOutputStreamEntry
argument_list|>
name|getStreamEntries
parameter_list|()
block|{
return|return
name|streamEntries
return|;
block|}
DECL|method|ChunkGroupOutputStream ( OpenKeySession handler, XceiverClientManager xceiverClientManager, StorageContainerLocationProtocolClientSideTranslatorPB scmClient, KeySpaceManagerProtocolClientSideTranslatorPB ksmClient, int chunkSize, String requestId, ReplicationFactor factor, ReplicationType type)
specifier|public
name|ChunkGroupOutputStream
parameter_list|(
name|OpenKeySession
name|handler
parameter_list|,
name|XceiverClientManager
name|xceiverClientManager
parameter_list|,
name|StorageContainerLocationProtocolClientSideTranslatorPB
name|scmClient
parameter_list|,
name|KeySpaceManagerProtocolClientSideTranslatorPB
name|ksmClient
parameter_list|,
name|int
name|chunkSize
parameter_list|,
name|String
name|requestId
parameter_list|,
name|ReplicationFactor
name|factor
parameter_list|,
name|ReplicationType
name|type
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|streamEntries
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|this
operator|.
name|currentStreamIndex
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|byteOffset
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|ksmClient
operator|=
name|ksmClient
expr_stmt|;
name|this
operator|.
name|scmClient
operator|=
name|scmClient
expr_stmt|;
name|KsmKeyInfo
name|info
init|=
name|handler
operator|.
name|getKeyInfo
argument_list|()
decl_stmt|;
name|this
operator|.
name|keyArgs
operator|=
operator|new
name|KsmKeyArgs
operator|.
name|Builder
argument_list|()
operator|.
name|setVolumeName
argument_list|(
name|info
operator|.
name|getVolumeName
argument_list|()
argument_list|)
operator|.
name|setBucketName
argument_list|(
name|info
operator|.
name|getBucketName
argument_list|()
argument_list|)
operator|.
name|setKeyName
argument_list|(
name|info
operator|.
name|getKeyName
argument_list|()
argument_list|)
operator|.
name|setType
argument_list|(
name|type
argument_list|)
operator|.
name|setFactor
argument_list|(
name|factor
argument_list|)
operator|.
name|setDataSize
argument_list|(
name|info
operator|.
name|getDataSize
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|this
operator|.
name|openID
operator|=
name|handler
operator|.
name|getId
argument_list|()
expr_stmt|;
name|this
operator|.
name|xceiverClientManager
operator|=
name|xceiverClientManager
expr_stmt|;
name|this
operator|.
name|chunkSize
operator|=
name|chunkSize
expr_stmt|;
name|this
operator|.
name|requestID
operator|=
name|requestId
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Expecting open key with one block, but got"
operator|+
name|info
operator|.
name|getKeyLocationVersions
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * When a key is opened, it is possible that there are some blocks already    * allocated to it for this open session. In this case, to make use of these    * blocks, we need to add these blocks to stream entries. But, a key's version    * also includes blocks from previous versions, we need to avoid adding these    * old blocks to stream entries, because these old blocks should not be picked    * for write. To do this, the following method checks that, only those    * blocks created in this particular open version are added to stream entries.    *    * @param version the set of blocks that are pre-allocated.    * @param openVersion the version corresponding to the pre-allocation.    * @throws IOException    */
DECL|method|addPreallocateBlocks (KsmKeyLocationInfoGroup version, long openVersion)
specifier|public
name|void
name|addPreallocateBlocks
parameter_list|(
name|KsmKeyLocationInfoGroup
name|version
parameter_list|,
name|long
name|openVersion
parameter_list|)
throws|throws
name|IOException
block|{
comment|// server may return any number of blocks, (0 to any)
comment|// only the blocks allocated in this open session (block createVersion
comment|// equals to open session version)
for|for
control|(
name|KsmKeyLocationInfo
name|subKeyInfo
range|:
name|version
operator|.
name|getLocationList
argument_list|()
control|)
block|{
if|if
condition|(
name|subKeyInfo
operator|.
name|getCreateVersion
argument_list|()
operator|==
name|openVersion
condition|)
block|{
name|checkKeyLocationInfo
argument_list|(
name|subKeyInfo
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|checkKeyLocationInfo (KsmKeyLocationInfo subKeyInfo)
specifier|private
name|void
name|checkKeyLocationInfo
parameter_list|(
name|KsmKeyLocationInfo
name|subKeyInfo
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|containerKey
init|=
name|subKeyInfo
operator|.
name|getBlockID
argument_list|()
decl_stmt|;
name|String
name|containerName
init|=
name|subKeyInfo
operator|.
name|getContainerName
argument_list|()
decl_stmt|;
name|Pipeline
name|pipeline
init|=
name|scmClient
operator|.
name|getContainer
argument_list|(
name|containerName
argument_list|)
decl_stmt|;
name|XceiverClientSpi
name|xceiverClient
init|=
name|xceiverClientManager
operator|.
name|acquireClient
argument_list|(
name|pipeline
argument_list|)
decl_stmt|;
comment|// create container if needed
if|if
condition|(
name|subKeyInfo
operator|.
name|getShouldCreateContainer
argument_list|()
condition|)
block|{
try|try
block|{
name|ContainerProtocolCalls
operator|.
name|createContainer
argument_list|(
name|xceiverClient
argument_list|,
name|requestID
argument_list|)
expr_stmt|;
name|scmClient
operator|.
name|notifyObjectStageChange
argument_list|(
name|ObjectStageChangeRequestProto
operator|.
name|Type
operator|.
name|container
argument_list|,
name|containerName
argument_list|,
name|ObjectStageChangeRequestProto
operator|.
name|Op
operator|.
name|create
argument_list|,
name|ObjectStageChangeRequestProto
operator|.
name|Stage
operator|.
name|complete
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|StorageContainerException
name|ex
parameter_list|)
block|{
if|if
condition|(
name|ex
operator|.
name|getResult
argument_list|()
operator|.
name|equals
argument_list|(
name|Result
operator|.
name|CONTAINER_EXISTS
argument_list|)
condition|)
block|{
comment|//container already exist, this should never happen
name|LOG
operator|.
name|debug
argument_list|(
literal|"Container {} already exists."
argument_list|,
name|containerName
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Container creation failed for {}."
argument_list|,
name|containerName
argument_list|,
name|ex
argument_list|)
expr_stmt|;
throw|throw
name|ex
throw|;
block|}
block|}
block|}
name|streamEntries
operator|.
name|add
argument_list|(
operator|new
name|ChunkOutputStreamEntry
argument_list|(
name|containerKey
argument_list|,
name|keyArgs
operator|.
name|getKeyName
argument_list|()
argument_list|,
name|xceiverClientManager
argument_list|,
name|xceiverClient
argument_list|,
name|requestID
argument_list|,
name|chunkSize
argument_list|,
name|subKeyInfo
operator|.
name|getLength
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getByteOffset ()
specifier|public
name|long
name|getByteOffset
parameter_list|()
block|{
return|return
name|byteOffset
return|;
block|}
annotation|@
name|Override
DECL|method|write (int b)
specifier|public
specifier|synchronized
name|void
name|write
parameter_list|(
name|int
name|b
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|streamEntries
operator|.
name|size
argument_list|()
operator|<=
name|currentStreamIndex
condition|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|ksmClient
argument_list|)
expr_stmt|;
comment|// allocate a new block, if a exception happens, log an error and
comment|// throw exception to the caller directly, and the write fails.
try|try
block|{
name|allocateNewBlock
argument_list|(
name|currentStreamIndex
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Allocate block fail when writing."
argument_list|)
expr_stmt|;
throw|throw
name|ioe
throw|;
block|}
block|}
name|ChunkOutputStreamEntry
name|entry
init|=
name|streamEntries
operator|.
name|get
argument_list|(
name|currentStreamIndex
argument_list|)
decl_stmt|;
name|entry
operator|.
name|write
argument_list|(
name|b
argument_list|)
expr_stmt|;
if|if
condition|(
name|entry
operator|.
name|getRemaining
argument_list|()
operator|<=
literal|0
condition|)
block|{
name|currentStreamIndex
operator|+=
literal|1
expr_stmt|;
block|}
name|byteOffset
operator|+=
literal|1
expr_stmt|;
block|}
comment|/**    * Try to write the bytes sequence b[off:off+len) to streams.    *    * NOTE: Throws exception if the data could not fit into the remaining space.    * In which case nothing will be written.    * TODO:May need to revisit this behaviour.    *    * @param b byte data    * @param off starting offset    * @param len length to write    * @throws IOException    */
annotation|@
name|Override
DECL|method|write (byte[] b, int off, int len)
specifier|public
specifier|synchronized
name|void
name|write
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|b
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NullPointerException
argument_list|()
throw|;
block|}
if|if
condition|(
operator|(
name|off
operator|<
literal|0
operator|)
operator|||
operator|(
name|off
operator|>
name|b
operator|.
name|length
operator|)
operator|||
operator|(
name|len
operator|<
literal|0
operator|)
operator|||
operator|(
operator|(
name|off
operator|+
name|len
operator|)
operator|>
name|b
operator|.
name|length
operator|)
operator|||
operator|(
operator|(
name|off
operator|+
name|len
operator|)
operator|<
literal|0
operator|)
condition|)
block|{
throw|throw
operator|new
name|IndexOutOfBoundsException
argument_list|()
throw|;
block|}
if|if
condition|(
name|len
operator|==
literal|0
condition|)
block|{
return|return;
block|}
name|int
name|succeededAllocates
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|len
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|streamEntries
operator|.
name|size
argument_list|()
operator|<=
name|currentStreamIndex
condition|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|ksmClient
argument_list|)
expr_stmt|;
comment|// allocate a new block, if a exception happens, log an error and
comment|// throw exception to the caller directly, and the write fails.
try|try
block|{
name|allocateNewBlock
argument_list|(
name|currentStreamIndex
argument_list|)
expr_stmt|;
name|succeededAllocates
operator|+=
literal|1
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Try to allocate more blocks for write failed, already "
operator|+
literal|"allocated "
operator|+
name|succeededAllocates
operator|+
literal|" blocks for this write."
argument_list|)
expr_stmt|;
throw|throw
name|ioe
throw|;
block|}
block|}
comment|// in theory, this condition should never violate due the check above
comment|// still do a sanity check.
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|currentStreamIndex
operator|<
name|streamEntries
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|ChunkOutputStreamEntry
name|current
init|=
name|streamEntries
operator|.
name|get
argument_list|(
name|currentStreamIndex
argument_list|)
decl_stmt|;
name|int
name|writeLen
init|=
name|Math
operator|.
name|min
argument_list|(
name|len
argument_list|,
operator|(
name|int
operator|)
name|current
operator|.
name|getRemaining
argument_list|()
argument_list|)
decl_stmt|;
name|current
operator|.
name|write
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|writeLen
argument_list|)
expr_stmt|;
if|if
condition|(
name|current
operator|.
name|getRemaining
argument_list|()
operator|<=
literal|0
condition|)
block|{
name|currentStreamIndex
operator|+=
literal|1
expr_stmt|;
block|}
name|len
operator|-=
name|writeLen
expr_stmt|;
name|off
operator|+=
name|writeLen
expr_stmt|;
name|byteOffset
operator|+=
name|writeLen
expr_stmt|;
block|}
block|}
comment|/**    * Contact KSM to get a new block. Set the new block with the index (e.g.    * first block has index = 0, second has index = 1 etc.)    *    * The returned block is made to new ChunkOutputStreamEntry to write.    *    * @param index the index of the block.    * @throws IOException    */
DECL|method|allocateNewBlock (int index)
specifier|private
name|void
name|allocateNewBlock
parameter_list|(
name|int
name|index
parameter_list|)
throws|throws
name|IOException
block|{
name|KsmKeyLocationInfo
name|subKeyInfo
init|=
name|ksmClient
operator|.
name|allocateBlock
argument_list|(
name|keyArgs
argument_list|,
name|openID
argument_list|)
decl_stmt|;
name|checkKeyLocationInfo
argument_list|(
name|subKeyInfo
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|flush ()
specifier|public
specifier|synchronized
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<=
name|currentStreamIndex
condition|;
name|i
operator|++
control|)
block|{
name|streamEntries
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Commit the key to KSM, this will add the blocks as the new key blocks.    *    * @throws IOException    */
annotation|@
name|Override
DECL|method|close ()
specifier|public
specifier|synchronized
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|ChunkOutputStreamEntry
name|entry
range|:
name|streamEntries
control|)
block|{
if|if
condition|(
name|entry
operator|!=
literal|null
condition|)
block|{
name|entry
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|keyArgs
operator|!=
literal|null
condition|)
block|{
comment|// in test, this could be null
name|keyArgs
operator|.
name|setDataSize
argument_list|(
name|byteOffset
argument_list|)
expr_stmt|;
name|ksmClient
operator|.
name|commitKey
argument_list|(
name|keyArgs
argument_list|,
name|openID
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Closing ChunkGroupOutputStream, but key args is null"
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Builder class of ChunkGroupOutputStream.    */
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
block|{
DECL|field|openHandler
specifier|private
name|OpenKeySession
name|openHandler
decl_stmt|;
DECL|field|xceiverManager
specifier|private
name|XceiverClientManager
name|xceiverManager
decl_stmt|;
DECL|field|scmClient
specifier|private
name|StorageContainerLocationProtocolClientSideTranslatorPB
name|scmClient
decl_stmt|;
DECL|field|ksmClient
specifier|private
name|KeySpaceManagerProtocolClientSideTranslatorPB
name|ksmClient
decl_stmt|;
DECL|field|chunkSize
specifier|private
name|int
name|chunkSize
decl_stmt|;
DECL|field|requestID
specifier|private
name|String
name|requestID
decl_stmt|;
DECL|field|type
specifier|private
name|ReplicationType
name|type
decl_stmt|;
DECL|field|factor
specifier|private
name|ReplicationFactor
name|factor
decl_stmt|;
DECL|method|setHandler (OpenKeySession handler)
specifier|public
name|Builder
name|setHandler
parameter_list|(
name|OpenKeySession
name|handler
parameter_list|)
block|{
name|this
operator|.
name|openHandler
operator|=
name|handler
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setXceiverClientManager (XceiverClientManager manager)
specifier|public
name|Builder
name|setXceiverClientManager
parameter_list|(
name|XceiverClientManager
name|manager
parameter_list|)
block|{
name|this
operator|.
name|xceiverManager
operator|=
name|manager
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setScmClient ( StorageContainerLocationProtocolClientSideTranslatorPB client)
specifier|public
name|Builder
name|setScmClient
parameter_list|(
name|StorageContainerLocationProtocolClientSideTranslatorPB
name|client
parameter_list|)
block|{
name|this
operator|.
name|scmClient
operator|=
name|client
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setKsmClient ( KeySpaceManagerProtocolClientSideTranslatorPB client)
specifier|public
name|Builder
name|setKsmClient
parameter_list|(
name|KeySpaceManagerProtocolClientSideTranslatorPB
name|client
parameter_list|)
block|{
name|this
operator|.
name|ksmClient
operator|=
name|client
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setChunkSize (int size)
specifier|public
name|Builder
name|setChunkSize
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|this
operator|.
name|chunkSize
operator|=
name|size
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setRequestID (String id)
specifier|public
name|Builder
name|setRequestID
parameter_list|(
name|String
name|id
parameter_list|)
block|{
name|this
operator|.
name|requestID
operator|=
name|id
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setType (ReplicationType replicationType)
specifier|public
name|Builder
name|setType
parameter_list|(
name|ReplicationType
name|replicationType
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|replicationType
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setFactor (ReplicationFactor replicationFactor)
specifier|public
name|Builder
name|setFactor
parameter_list|(
name|ReplicationFactor
name|replicationFactor
parameter_list|)
block|{
name|this
operator|.
name|factor
operator|=
name|replicationFactor
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|build ()
specifier|public
name|ChunkGroupOutputStream
name|build
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|ChunkGroupOutputStream
argument_list|(
name|openHandler
argument_list|,
name|xceiverManager
argument_list|,
name|scmClient
argument_list|,
name|ksmClient
argument_list|,
name|chunkSize
argument_list|,
name|requestID
argument_list|,
name|factor
argument_list|,
name|type
argument_list|)
return|;
block|}
block|}
DECL|class|ChunkOutputStreamEntry
specifier|private
specifier|static
class|class
name|ChunkOutputStreamEntry
extends|extends
name|OutputStream
block|{
DECL|field|outputStream
specifier|private
name|OutputStream
name|outputStream
decl_stmt|;
DECL|field|containerKey
specifier|private
specifier|final
name|String
name|containerKey
decl_stmt|;
DECL|field|key
specifier|private
specifier|final
name|String
name|key
decl_stmt|;
DECL|field|xceiverClientManager
specifier|private
specifier|final
name|XceiverClientManager
name|xceiverClientManager
decl_stmt|;
DECL|field|xceiverClient
specifier|private
specifier|final
name|XceiverClientSpi
name|xceiverClient
decl_stmt|;
DECL|field|requestId
specifier|private
specifier|final
name|String
name|requestId
decl_stmt|;
DECL|field|chunkSize
specifier|private
specifier|final
name|int
name|chunkSize
decl_stmt|;
comment|// total number of bytes that should be written to this stream
DECL|field|length
specifier|private
specifier|final
name|long
name|length
decl_stmt|;
comment|// the current position of this stream 0<= currentPosition< length
DECL|field|currentPosition
specifier|private
name|long
name|currentPosition
decl_stmt|;
DECL|method|ChunkOutputStreamEntry (String containerKey, String key, XceiverClientManager xceiverClientManager, XceiverClientSpi xceiverClient, String requestId, int chunkSize, long length)
name|ChunkOutputStreamEntry
parameter_list|(
name|String
name|containerKey
parameter_list|,
name|String
name|key
parameter_list|,
name|XceiverClientManager
name|xceiverClientManager
parameter_list|,
name|XceiverClientSpi
name|xceiverClient
parameter_list|,
name|String
name|requestId
parameter_list|,
name|int
name|chunkSize
parameter_list|,
name|long
name|length
parameter_list|)
block|{
name|this
operator|.
name|outputStream
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|containerKey
operator|=
name|containerKey
expr_stmt|;
name|this
operator|.
name|key
operator|=
name|key
expr_stmt|;
name|this
operator|.
name|xceiverClientManager
operator|=
name|xceiverClientManager
expr_stmt|;
name|this
operator|.
name|xceiverClient
operator|=
name|xceiverClient
expr_stmt|;
name|this
operator|.
name|requestId
operator|=
name|requestId
expr_stmt|;
name|this
operator|.
name|chunkSize
operator|=
name|chunkSize
expr_stmt|;
name|this
operator|.
name|length
operator|=
name|length
expr_stmt|;
name|this
operator|.
name|currentPosition
operator|=
literal|0
expr_stmt|;
block|}
comment|/**      * For testing purpose, taking a some random created stream instance.      * @param  outputStream a existing writable output stream      * @param  length the length of data to write to the stream      */
DECL|method|ChunkOutputStreamEntry (OutputStream outputStream, long length)
name|ChunkOutputStreamEntry
parameter_list|(
name|OutputStream
name|outputStream
parameter_list|,
name|long
name|length
parameter_list|)
block|{
name|this
operator|.
name|outputStream
operator|=
name|outputStream
expr_stmt|;
name|this
operator|.
name|containerKey
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|key
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|xceiverClientManager
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|xceiverClient
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|requestId
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|chunkSize
operator|=
operator|-
literal|1
expr_stmt|;
name|this
operator|.
name|length
operator|=
name|length
expr_stmt|;
name|this
operator|.
name|currentPosition
operator|=
literal|0
expr_stmt|;
block|}
DECL|method|getLength ()
name|long
name|getLength
parameter_list|()
block|{
return|return
name|length
return|;
block|}
DECL|method|getRemaining ()
name|long
name|getRemaining
parameter_list|()
block|{
return|return
name|length
operator|-
name|currentPosition
return|;
block|}
DECL|method|checkStream ()
specifier|private
specifier|synchronized
name|void
name|checkStream
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|outputStream
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|outputStream
operator|=
operator|new
name|ChunkOutputStream
argument_list|(
name|containerKey
argument_list|,
name|key
argument_list|,
name|xceiverClientManager
argument_list|,
name|xceiverClient
argument_list|,
name|requestId
argument_list|,
name|chunkSize
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|write (int b)
specifier|public
name|void
name|write
parameter_list|(
name|int
name|b
parameter_list|)
throws|throws
name|IOException
block|{
name|checkStream
argument_list|()
expr_stmt|;
name|outputStream
operator|.
name|write
argument_list|(
name|b
argument_list|)
expr_stmt|;
name|this
operator|.
name|currentPosition
operator|+=
literal|1
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|write (byte[] b, int off, int len)
specifier|public
name|void
name|write
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
name|checkStream
argument_list|()
expr_stmt|;
name|outputStream
operator|.
name|write
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|this
operator|.
name|currentPosition
operator|+=
name|len
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|flush ()
specifier|public
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|this
operator|.
name|outputStream
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|outputStream
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|this
operator|.
name|outputStream
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|outputStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

