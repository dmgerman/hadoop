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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|FSExceptionMessages
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
name|Seekable
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
name|proto
operator|.
name|HddsProtos
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
name|ContainerWithPipeline
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
name|pipeline
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
name|ozone
operator|.
name|om
operator|.
name|helpers
operator|.
name|OmKeyInfo
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
name|om
operator|.
name|helpers
operator|.
name|OmKeyLocationInfo
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
name|hdds
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
name|hdds
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
name|hdds
operator|.
name|scm
operator|.
name|storage
operator|.
name|BlockInputStream
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
name|storage
operator|.
name|ContainerProtocolCalls
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
name|security
operator|.
name|UserGroupInformation
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|util
operator|.
name|Preconditions
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
name|EOFException
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
name|InputStream
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
name|Arrays
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
comment|/**  * Maintaining a list of BlockInputStream. Read based on offset.  */
end_comment

begin_class
DECL|class|KeyInputStream
specifier|public
class|class
name|KeyInputStream
extends|extends
name|InputStream
implements|implements
name|Seekable
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
name|KeyInputStream
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|EOF
specifier|private
specifier|static
specifier|final
name|int
name|EOF
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|streamEntries
specifier|private
specifier|final
name|ArrayList
argument_list|<
name|ChunkInputStreamEntry
argument_list|>
name|streamEntries
decl_stmt|;
comment|// streamOffset[i] stores the offset at which blockInputStream i stores
comment|// data in the key
DECL|field|streamOffset
specifier|private
name|long
index|[]
name|streamOffset
init|=
literal|null
decl_stmt|;
DECL|field|currentStreamIndex
specifier|private
name|int
name|currentStreamIndex
decl_stmt|;
DECL|field|length
specifier|private
name|long
name|length
init|=
literal|0
decl_stmt|;
DECL|field|closed
specifier|private
name|boolean
name|closed
init|=
literal|false
decl_stmt|;
DECL|field|key
specifier|private
name|String
name|key
decl_stmt|;
DECL|method|KeyInputStream ()
specifier|public
name|KeyInputStream
parameter_list|()
block|{
name|streamEntries
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|currentStreamIndex
operator|=
literal|0
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getCurrentStreamIndex ()
specifier|public
specifier|synchronized
name|int
name|getCurrentStreamIndex
parameter_list|()
block|{
return|return
name|currentStreamIndex
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getRemainingOfIndex (int index)
specifier|public
name|long
name|getRemainingOfIndex
parameter_list|(
name|int
name|index
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|streamEntries
operator|.
name|get
argument_list|(
name|index
argument_list|)
operator|.
name|getRemaining
argument_list|()
return|;
block|}
comment|/**    * Append another stream to the end of the list.    *    * @param stream       the stream instance.    * @param streamLength the max number of bytes that should be written to this    *                     stream.    */
DECL|method|addStream (BlockInputStream stream, long streamLength)
specifier|public
specifier|synchronized
name|void
name|addStream
parameter_list|(
name|BlockInputStream
name|stream
parameter_list|,
name|long
name|streamLength
parameter_list|)
block|{
name|streamEntries
operator|.
name|add
argument_list|(
operator|new
name|ChunkInputStreamEntry
argument_list|(
name|stream
argument_list|,
name|streamLength
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|read ()
specifier|public
specifier|synchronized
name|int
name|read
parameter_list|()
throws|throws
name|IOException
block|{
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
literal|1
index|]
decl_stmt|;
if|if
condition|(
name|read
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
operator|==
name|EOF
condition|)
block|{
return|return
name|EOF
return|;
block|}
return|return
name|Byte
operator|.
name|toUnsignedInt
argument_list|(
name|buf
index|[
literal|0
index|]
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|read (byte[] b, int off, int len)
specifier|public
specifier|synchronized
name|int
name|read
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
name|checkNotClosed
argument_list|()
expr_stmt|;
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
name|off
operator|<
literal|0
operator|||
name|len
argument_list|<
literal|0
operator|||
name|len
argument_list|>
name|b
operator|.
name|length
operator|-
name|off
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
return|return
literal|0
return|;
block|}
name|int
name|totalReadLen
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
comment|// if we are at the last block and have read the entire block, return
if|if
condition|(
name|streamEntries
operator|.
name|size
argument_list|()
operator|==
literal|0
operator|||
operator|(
name|streamEntries
operator|.
name|size
argument_list|()
operator|-
literal|1
operator|<=
name|currentStreamIndex
operator|&&
name|streamEntries
operator|.
name|get
argument_list|(
name|currentStreamIndex
argument_list|)
operator|.
name|getRemaining
argument_list|()
operator|==
literal|0
operator|)
condition|)
block|{
return|return
name|totalReadLen
operator|==
literal|0
condition|?
name|EOF
else|:
name|totalReadLen
return|;
block|}
name|ChunkInputStreamEntry
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
name|numBytesToRead
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
name|int
name|numBytesRead
init|=
name|current
operator|.
name|read
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|numBytesToRead
argument_list|)
decl_stmt|;
if|if
condition|(
name|numBytesRead
operator|!=
name|numBytesToRead
condition|)
block|{
comment|// This implies that there is either data loss or corruption in the
comment|// chunk entries. Even EOF in the current stream would be covered in
comment|// this case.
throw|throw
operator|new
name|IOException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Inconsistent read for blockID=%s length=%d numBytesRead=%d"
argument_list|,
name|current
operator|.
name|blockInputStream
operator|.
name|getBlockID
argument_list|()
argument_list|,
name|current
operator|.
name|length
argument_list|,
name|numBytesRead
argument_list|)
argument_list|)
throw|;
block|}
name|totalReadLen
operator|+=
name|numBytesRead
expr_stmt|;
name|off
operator|+=
name|numBytesRead
expr_stmt|;
name|len
operator|-=
name|numBytesRead
expr_stmt|;
if|if
condition|(
name|current
operator|.
name|getRemaining
argument_list|()
operator|<=
literal|0
operator|&&
operator|(
operator|(
name|currentStreamIndex
operator|+
literal|1
operator|)
operator|<
name|streamEntries
operator|.
name|size
argument_list|()
operator|)
condition|)
block|{
name|currentStreamIndex
operator|+=
literal|1
expr_stmt|;
block|}
block|}
return|return
name|totalReadLen
return|;
block|}
annotation|@
name|Override
DECL|method|seek (long pos)
specifier|public
name|void
name|seek
parameter_list|(
name|long
name|pos
parameter_list|)
throws|throws
name|IOException
block|{
name|checkNotClosed
argument_list|()
expr_stmt|;
if|if
condition|(
name|pos
operator|<
literal|0
operator|||
name|pos
operator|>=
name|length
condition|)
block|{
if|if
condition|(
name|pos
operator|==
literal|0
condition|)
block|{
comment|// It is possible for length and pos to be zero in which case
comment|// seek should return instead of throwing exception
return|return;
block|}
throw|throw
operator|new
name|EOFException
argument_list|(
literal|"EOF encountered at pos: "
operator|+
name|pos
operator|+
literal|" for key: "
operator|+
name|key
argument_list|)
throw|;
block|}
name|Preconditions
operator|.
name|assertTrue
argument_list|(
name|currentStreamIndex
operator|>=
literal|0
argument_list|)
expr_stmt|;
if|if
condition|(
name|currentStreamIndex
operator|>=
name|streamEntries
operator|.
name|size
argument_list|()
condition|)
block|{
name|currentStreamIndex
operator|=
name|Arrays
operator|.
name|binarySearch
argument_list|(
name|streamOffset
argument_list|,
name|pos
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|pos
operator|<
name|streamOffset
index|[
name|currentStreamIndex
index|]
condition|)
block|{
name|currentStreamIndex
operator|=
name|Arrays
operator|.
name|binarySearch
argument_list|(
name|streamOffset
argument_list|,
literal|0
argument_list|,
name|currentStreamIndex
argument_list|,
name|pos
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|pos
operator|>=
name|streamOffset
index|[
name|currentStreamIndex
index|]
operator|+
name|streamEntries
operator|.
name|get
argument_list|(
name|currentStreamIndex
argument_list|)
operator|.
name|length
condition|)
block|{
name|currentStreamIndex
operator|=
name|Arrays
operator|.
name|binarySearch
argument_list|(
name|streamOffset
argument_list|,
name|currentStreamIndex
operator|+
literal|1
argument_list|,
name|streamEntries
operator|.
name|size
argument_list|()
argument_list|,
name|pos
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|currentStreamIndex
operator|<
literal|0
condition|)
block|{
comment|// Binary search returns -insertionPoint - 1  if element is not present
comment|// in the array. insertionPoint is the point at which element would be
comment|// inserted in the sorted array. We need to adjust the currentStreamIndex
comment|// accordingly so that currentStreamIndex = insertionPoint - 1
name|currentStreamIndex
operator|=
operator|-
name|currentStreamIndex
operator|-
literal|2
expr_stmt|;
block|}
comment|// seek to the proper offset in the BlockInputStream
name|streamEntries
operator|.
name|get
argument_list|(
name|currentStreamIndex
argument_list|)
operator|.
name|seek
argument_list|(
name|pos
operator|-
name|streamOffset
index|[
name|currentStreamIndex
index|]
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getPos ()
specifier|public
name|long
name|getPos
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|length
operator|==
literal|0
condition|?
literal|0
else|:
name|streamOffset
index|[
name|currentStreamIndex
index|]
operator|+
name|streamEntries
operator|.
name|get
argument_list|(
name|currentStreamIndex
argument_list|)
operator|.
name|getPos
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|seekToNewSource (long targetPos)
specifier|public
name|boolean
name|seekToNewSource
parameter_list|(
name|long
name|targetPos
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|available ()
specifier|public
name|int
name|available
parameter_list|()
throws|throws
name|IOException
block|{
name|checkNotClosed
argument_list|()
expr_stmt|;
name|long
name|remaining
init|=
name|length
operator|-
name|getPos
argument_list|()
decl_stmt|;
return|return
name|remaining
operator|<=
name|Integer
operator|.
name|MAX_VALUE
condition|?
operator|(
name|int
operator|)
name|remaining
else|:
name|Integer
operator|.
name|MAX_VALUE
return|;
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
name|closed
operator|=
literal|true
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|streamEntries
operator|.
name|size
argument_list|()
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
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Encapsulates BlockInputStream.    */
DECL|class|ChunkInputStreamEntry
specifier|public
specifier|static
class|class
name|ChunkInputStreamEntry
extends|extends
name|InputStream
implements|implements
name|Seekable
block|{
DECL|field|blockInputStream
specifier|private
specifier|final
name|BlockInputStream
name|blockInputStream
decl_stmt|;
DECL|field|length
specifier|private
specifier|final
name|long
name|length
decl_stmt|;
DECL|method|ChunkInputStreamEntry (BlockInputStream blockInputStream, long length)
specifier|public
name|ChunkInputStreamEntry
parameter_list|(
name|BlockInputStream
name|blockInputStream
parameter_list|,
name|long
name|length
parameter_list|)
block|{
name|this
operator|.
name|blockInputStream
operator|=
name|blockInputStream
expr_stmt|;
name|this
operator|.
name|length
operator|=
name|length
expr_stmt|;
block|}
DECL|method|getRemaining ()
specifier|synchronized
name|long
name|getRemaining
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|length
operator|-
name|getPos
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|read (byte[] b, int off, int len)
specifier|public
specifier|synchronized
name|int
name|read
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
name|int
name|readLen
init|=
name|blockInputStream
operator|.
name|read
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
decl_stmt|;
return|return
name|readLen
return|;
block|}
annotation|@
name|Override
DECL|method|read ()
specifier|public
specifier|synchronized
name|int
name|read
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|data
init|=
name|blockInputStream
operator|.
name|read
argument_list|()
decl_stmt|;
return|return
name|data
return|;
block|}
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
name|blockInputStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|seek (long pos)
specifier|public
name|void
name|seek
parameter_list|(
name|long
name|pos
parameter_list|)
throws|throws
name|IOException
block|{
name|blockInputStream
operator|.
name|seek
argument_list|(
name|pos
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getPos ()
specifier|public
name|long
name|getPos
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|blockInputStream
operator|.
name|getPos
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|seekToNewSource (long targetPos)
specifier|public
name|boolean
name|seekToNewSource
parameter_list|(
name|long
name|targetPos
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|false
return|;
block|}
block|}
DECL|method|getFromOmKeyInfo ( OmKeyInfo keyInfo, XceiverClientManager xceiverClientManager, StorageContainerLocationProtocolClientSideTranslatorPB storageContainerLocationClient, String requestId, boolean verifyChecksum)
specifier|public
specifier|static
name|LengthInputStream
name|getFromOmKeyInfo
parameter_list|(
name|OmKeyInfo
name|keyInfo
parameter_list|,
name|XceiverClientManager
name|xceiverClientManager
parameter_list|,
name|StorageContainerLocationProtocolClientSideTranslatorPB
name|storageContainerLocationClient
parameter_list|,
name|String
name|requestId
parameter_list|,
name|boolean
name|verifyChecksum
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|length
init|=
literal|0
decl_stmt|;
name|long
name|containerKey
decl_stmt|;
name|KeyInputStream
name|groupInputStream
init|=
operator|new
name|KeyInputStream
argument_list|()
decl_stmt|;
name|groupInputStream
operator|.
name|key
operator|=
name|keyInfo
operator|.
name|getKeyName
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|OmKeyLocationInfo
argument_list|>
name|keyLocationInfos
init|=
name|keyInfo
operator|.
name|getLatestVersionLocations
argument_list|()
operator|.
name|getBlocksLatestVersionOnly
argument_list|()
decl_stmt|;
name|groupInputStream
operator|.
name|streamOffset
operator|=
operator|new
name|long
index|[
name|keyLocationInfos
operator|.
name|size
argument_list|()
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|keyLocationInfos
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|OmKeyLocationInfo
name|omKeyLocationInfo
init|=
name|keyLocationInfos
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|BlockID
name|blockID
init|=
name|omKeyLocationInfo
operator|.
name|getBlockID
argument_list|()
decl_stmt|;
name|long
name|containerID
init|=
name|blockID
operator|.
name|getContainerID
argument_list|()
decl_stmt|;
name|ContainerWithPipeline
name|containerWithPipeline
init|=
name|storageContainerLocationClient
operator|.
name|getContainerWithPipeline
argument_list|(
name|containerID
argument_list|)
decl_stmt|;
name|Pipeline
name|pipeline
init|=
name|containerWithPipeline
operator|.
name|getPipeline
argument_list|()
decl_stmt|;
comment|// irrespective of the container state, we will always read via Standalone
comment|// protocol.
if|if
condition|(
name|pipeline
operator|.
name|getType
argument_list|()
operator|!=
name|HddsProtos
operator|.
name|ReplicationType
operator|.
name|STAND_ALONE
condition|)
block|{
name|pipeline
operator|=
name|Pipeline
operator|.
name|newBuilder
argument_list|(
name|pipeline
argument_list|)
operator|.
name|setType
argument_list|(
name|HddsProtos
operator|.
name|ReplicationType
operator|.
name|STAND_ALONE
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
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
name|boolean
name|success
init|=
literal|false
decl_stmt|;
name|containerKey
operator|=
name|omKeyLocationInfo
operator|.
name|getLocalID
argument_list|()
expr_stmt|;
try|try
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"get key accessing {} {}"
argument_list|,
name|containerID
argument_list|,
name|containerKey
argument_list|)
expr_stmt|;
name|groupInputStream
operator|.
name|streamOffset
index|[
name|i
index|]
operator|=
name|length
expr_stmt|;
name|ContainerProtos
operator|.
name|DatanodeBlockID
name|datanodeBlockID
init|=
name|blockID
operator|.
name|getDatanodeBlockIDProtobuf
argument_list|()
decl_stmt|;
if|if
condition|(
name|omKeyLocationInfo
operator|.
name|getToken
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|addToken
argument_list|(
name|omKeyLocationInfo
operator|.
name|getToken
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|ContainerProtos
operator|.
name|GetBlockResponseProto
name|response
init|=
name|ContainerProtocolCalls
operator|.
name|getBlock
argument_list|(
name|xceiverClient
argument_list|,
name|datanodeBlockID
argument_list|,
name|requestId
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|ContainerProtos
operator|.
name|ChunkInfo
argument_list|>
name|chunks
init|=
name|response
operator|.
name|getBlockData
argument_list|()
operator|.
name|getChunksList
argument_list|()
decl_stmt|;
for|for
control|(
name|ContainerProtos
operator|.
name|ChunkInfo
name|chunk
range|:
name|chunks
control|)
block|{
name|length
operator|+=
name|chunk
operator|.
name|getLen
argument_list|()
expr_stmt|;
block|}
name|success
operator|=
literal|true
expr_stmt|;
name|BlockInputStream
name|inputStream
init|=
operator|new
name|BlockInputStream
argument_list|(
name|omKeyLocationInfo
operator|.
name|getBlockID
argument_list|()
argument_list|,
name|xceiverClientManager
argument_list|,
name|xceiverClient
argument_list|,
name|chunks
argument_list|,
name|requestId
argument_list|,
name|verifyChecksum
argument_list|)
decl_stmt|;
name|groupInputStream
operator|.
name|addStream
argument_list|(
name|inputStream
argument_list|,
name|omKeyLocationInfo
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|xceiverClientManager
operator|.
name|releaseClient
argument_list|(
name|xceiverClient
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|groupInputStream
operator|.
name|length
operator|=
name|length
expr_stmt|;
return|return
operator|new
name|LengthInputStream
argument_list|(
name|groupInputStream
argument_list|,
name|length
argument_list|)
return|;
block|}
comment|/**    * Verify that the input stream is open. Non blocking; this gives    * the last state of the volatile {@link #closed} field.    * @throws IOException if the connection is closed.    */
DECL|method|checkNotClosed ()
specifier|private
name|void
name|checkNotClosed
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|closed
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|": "
operator|+
name|FSExceptionMessages
operator|.
name|STREAM_IS_CLOSED
operator|+
literal|" Key: "
operator|+
name|key
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

