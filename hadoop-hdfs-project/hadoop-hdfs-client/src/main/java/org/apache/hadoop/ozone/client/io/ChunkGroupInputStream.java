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
name|hdfs
operator|.
name|ozone
operator|.
name|protocol
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
name|ChunkInputStream
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
comment|/**  * Maintaining a list of ChunkInputStream. Read based on offset.  */
end_comment

begin_class
DECL|class|ChunkGroupInputStream
specifier|public
class|class
name|ChunkGroupInputStream
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
name|ChunkGroupInputStream
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
comment|// streamOffset[i] stores the offset at which chunkInputStream i stores
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
DECL|method|ChunkGroupInputStream ()
specifier|public
name|ChunkGroupInputStream
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
DECL|method|addStream (ChunkInputStream stream, long streamLength)
specifier|public
specifier|synchronized
name|void
name|addStream
parameter_list|(
name|ChunkInputStream
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
name|readLen
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
name|actualLen
init|=
name|current
operator|.
name|read
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|readLen
argument_list|)
decl_stmt|;
comment|// this means the underlying stream has nothing at all, return
if|if
condition|(
name|actualLen
operator|==
name|EOF
condition|)
block|{
return|return
name|totalReadLen
operator|>
literal|0
condition|?
name|totalReadLen
else|:
name|EOF
return|;
block|}
name|totalReadLen
operator|+=
name|actualLen
expr_stmt|;
comment|// this means there is no more data to read beyond this point, return
if|if
condition|(
name|actualLen
operator|!=
name|readLen
condition|)
block|{
return|return
name|totalReadLen
return|;
block|}
name|off
operator|+=
name|readLen
expr_stmt|;
name|len
operator|-=
name|readLen
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
comment|// seek to the proper offset in the ChunkInputStream
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
comment|/**    * Encapsulates ChunkInputStream.    */
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
DECL|field|chunkInputStream
specifier|private
specifier|final
name|ChunkInputStream
name|chunkInputStream
decl_stmt|;
DECL|field|length
specifier|private
specifier|final
name|long
name|length
decl_stmt|;
DECL|field|currentPosition
specifier|private
name|long
name|currentPosition
decl_stmt|;
DECL|method|ChunkInputStreamEntry (ChunkInputStream chunkInputStream, long length)
specifier|public
name|ChunkInputStreamEntry
parameter_list|(
name|ChunkInputStream
name|chunkInputStream
parameter_list|,
name|long
name|length
parameter_list|)
block|{
name|this
operator|.
name|chunkInputStream
operator|=
name|chunkInputStream
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
DECL|method|getRemaining ()
specifier|synchronized
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
name|chunkInputStream
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
name|currentPosition
operator|+=
name|readLen
expr_stmt|;
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
name|chunkInputStream
operator|.
name|read
argument_list|()
decl_stmt|;
name|currentPosition
operator|+=
literal|1
expr_stmt|;
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
name|chunkInputStream
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
name|chunkInputStream
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
name|chunkInputStream
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
DECL|method|getFromKsmKeyInfo (KsmKeyInfo keyInfo, XceiverClientManager xceiverClientManager, StorageContainerLocationProtocolClientSideTranslatorPB storageContainerLocationClient, String requestId)
specifier|public
specifier|static
name|LengthInputStream
name|getFromKsmKeyInfo
parameter_list|(
name|KsmKeyInfo
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
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|length
init|=
literal|0
decl_stmt|;
name|String
name|containerKey
decl_stmt|;
name|ChunkGroupInputStream
name|groupInputStream
init|=
operator|new
name|ChunkGroupInputStream
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
name|KsmKeyLocationInfo
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
name|KsmKeyLocationInfo
name|ksmKeyLocationInfo
init|=
name|keyLocationInfos
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|String
name|containerName
init|=
name|ksmKeyLocationInfo
operator|.
name|getContainerName
argument_list|()
decl_stmt|;
name|Pipeline
name|pipeline
init|=
name|storageContainerLocationClient
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
name|boolean
name|success
init|=
literal|false
decl_stmt|;
name|containerKey
operator|=
name|ksmKeyLocationInfo
operator|.
name|getBlockID
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
name|xceiverClient
operator|.
name|getPipeline
argument_list|()
operator|.
name|getContainerName
argument_list|()
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
name|KeyData
name|containerKeyData
init|=
name|OzoneContainerTranslation
operator|.
name|containerKeyDataForRead
argument_list|(
name|xceiverClient
operator|.
name|getPipeline
argument_list|()
operator|.
name|getContainerName
argument_list|()
argument_list|,
name|containerKey
argument_list|)
decl_stmt|;
name|ContainerProtos
operator|.
name|GetKeyResponseProto
name|response
init|=
name|ContainerProtocolCalls
operator|.
name|getKey
argument_list|(
name|xceiverClient
argument_list|,
name|containerKeyData
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
name|getKeyData
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
name|ChunkInputStream
name|inputStream
init|=
operator|new
name|ChunkInputStream
argument_list|(
name|containerKey
argument_list|,
name|xceiverClientManager
argument_list|,
name|xceiverClient
argument_list|,
name|chunks
argument_list|,
name|requestId
argument_list|)
decl_stmt|;
name|groupInputStream
operator|.
name|addStream
argument_list|(
name|inputStream
argument_list|,
name|ksmKeyLocationInfo
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

