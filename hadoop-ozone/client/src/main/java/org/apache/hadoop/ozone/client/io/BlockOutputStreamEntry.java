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
name|nio
operator|.
name|ByteBuffer
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
name|hdds
operator|.
name|scm
operator|.
name|storage
operator|.
name|BlockOutputStream
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
name|security
operator|.
name|token
operator|.
name|OzoneBlockTokenIdentifier
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
name|common
operator|.
name|Checksum
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
name|hadoop
operator|.
name|security
operator|.
name|token
operator|.
name|Token
import|;
end_import

begin_comment
comment|/**  * Helper class used inside {@link BlockOutputStream}.  * */
end_comment

begin_class
DECL|class|BlockOutputStreamEntry
specifier|public
specifier|final
class|class
name|BlockOutputStreamEntry
extends|extends
name|OutputStream
block|{
DECL|field|outputStream
specifier|private
name|OutputStream
name|outputStream
decl_stmt|;
DECL|field|blockID
specifier|private
name|BlockID
name|blockID
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
DECL|field|pipeline
specifier|private
specifier|final
name|Pipeline
name|pipeline
decl_stmt|;
DECL|field|checksum
specifier|private
specifier|final
name|Checksum
name|checksum
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
DECL|field|token
specifier|private
name|Token
argument_list|<
name|OzoneBlockTokenIdentifier
argument_list|>
name|token
decl_stmt|;
DECL|field|streamBufferFlushSize
specifier|private
specifier|final
name|long
name|streamBufferFlushSize
decl_stmt|;
DECL|field|streamBufferMaxSize
specifier|private
specifier|final
name|long
name|streamBufferMaxSize
decl_stmt|;
DECL|field|watchTimeout
specifier|private
specifier|final
name|long
name|watchTimeout
decl_stmt|;
DECL|field|bufferList
specifier|private
name|List
argument_list|<
name|ByteBuffer
argument_list|>
name|bufferList
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"parameternumber"
argument_list|)
DECL|method|BlockOutputStreamEntry (BlockID blockID, String key, XceiverClientManager xceiverClientManager, Pipeline pipeline, String requestId, int chunkSize, long length, long streamBufferFlushSize, long streamBufferMaxSize, long watchTimeout, List<ByteBuffer> bufferList, Checksum checksum, Token<OzoneBlockTokenIdentifier> token)
specifier|private
name|BlockOutputStreamEntry
parameter_list|(
name|BlockID
name|blockID
parameter_list|,
name|String
name|key
parameter_list|,
name|XceiverClientManager
name|xceiverClientManager
parameter_list|,
name|Pipeline
name|pipeline
parameter_list|,
name|String
name|requestId
parameter_list|,
name|int
name|chunkSize
parameter_list|,
name|long
name|length
parameter_list|,
name|long
name|streamBufferFlushSize
parameter_list|,
name|long
name|streamBufferMaxSize
parameter_list|,
name|long
name|watchTimeout
parameter_list|,
name|List
argument_list|<
name|ByteBuffer
argument_list|>
name|bufferList
parameter_list|,
name|Checksum
name|checksum
parameter_list|,
name|Token
argument_list|<
name|OzoneBlockTokenIdentifier
argument_list|>
name|token
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
name|blockID
operator|=
name|blockID
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
name|pipeline
operator|=
name|pipeline
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
name|token
operator|=
name|token
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
name|this
operator|.
name|streamBufferFlushSize
operator|=
name|streamBufferFlushSize
expr_stmt|;
name|this
operator|.
name|streamBufferMaxSize
operator|=
name|streamBufferMaxSize
expr_stmt|;
name|this
operator|.
name|watchTimeout
operator|=
name|watchTimeout
expr_stmt|;
name|this
operator|.
name|bufferList
operator|=
name|bufferList
expr_stmt|;
name|this
operator|.
name|checksum
operator|=
name|checksum
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
DECL|method|getToken ()
name|Token
argument_list|<
name|OzoneBlockTokenIdentifier
argument_list|>
name|getToken
parameter_list|()
block|{
return|return
name|token
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
comment|/**    * BlockOutputStream is initialized in this function. This makes sure that    * xceiverClient initialization is not done during preallocation and only    * done when data is written.    * @throws IOException if xceiverClient initialization fails    */
DECL|method|checkStream ()
specifier|private
name|void
name|checkStream
parameter_list|()
throws|throws
name|IOException
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
if|if
condition|(
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
name|getToken
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|outputStream
operator|=
operator|new
name|BlockOutputStream
argument_list|(
name|blockID
argument_list|,
name|key
argument_list|,
name|xceiverClientManager
argument_list|,
name|pipeline
argument_list|,
name|requestId
argument_list|,
name|chunkSize
argument_list|,
name|streamBufferFlushSize
argument_list|,
name|streamBufferMaxSize
argument_list|,
name|watchTimeout
argument_list|,
name|bufferList
argument_list|,
name|checksum
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
comment|// after closing the chunkOutPutStream, blockId would have been
comment|// reconstructed with updated bcsId
name|this
operator|.
name|blockID
operator|=
operator|(
operator|(
name|BlockOutputStream
operator|)
name|outputStream
operator|)
operator|.
name|getBlockID
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|getTotalSuccessfulFlushedData ()
name|long
name|getTotalSuccessfulFlushedData
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|outputStream
operator|!=
literal|null
condition|)
block|{
name|BlockOutputStream
name|out
init|=
operator|(
name|BlockOutputStream
operator|)
name|this
operator|.
name|outputStream
decl_stmt|;
name|blockID
operator|=
name|out
operator|.
name|getBlockID
argument_list|()
expr_stmt|;
return|return
name|out
operator|.
name|getTotalSuccessfulFlushedData
argument_list|()
return|;
block|}
else|else
block|{
comment|// For a pre allocated block for which no write has been initiated,
comment|// the OutputStream will be null here.
comment|// In such cases, the default blockCommitSequenceId will be 0
return|return
literal|0
return|;
block|}
block|}
DECL|method|getWrittenDataLength ()
name|long
name|getWrittenDataLength
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|outputStream
operator|!=
literal|null
condition|)
block|{
name|BlockOutputStream
name|out
init|=
operator|(
name|BlockOutputStream
operator|)
name|this
operator|.
name|outputStream
decl_stmt|;
return|return
name|out
operator|.
name|getWrittenDataLength
argument_list|()
return|;
block|}
else|else
block|{
comment|// For a pre allocated block for which no write has been initiated,
comment|// the OutputStream will be null here.
comment|// In such cases, the default blockCommitSequenceId will be 0
return|return
literal|0
return|;
block|}
block|}
DECL|method|cleanup (boolean invalidateClient)
name|void
name|cleanup
parameter_list|(
name|boolean
name|invalidateClient
parameter_list|)
throws|throws
name|IOException
block|{
name|checkStream
argument_list|()
expr_stmt|;
name|BlockOutputStream
name|out
init|=
operator|(
name|BlockOutputStream
operator|)
name|this
operator|.
name|outputStream
decl_stmt|;
name|out
operator|.
name|cleanup
argument_list|(
name|invalidateClient
argument_list|)
expr_stmt|;
block|}
DECL|method|writeOnRetry (long len)
name|void
name|writeOnRetry
parameter_list|(
name|long
name|len
parameter_list|)
throws|throws
name|IOException
block|{
name|checkStream
argument_list|()
expr_stmt|;
name|BlockOutputStream
name|out
init|=
operator|(
name|BlockOutputStream
operator|)
name|this
operator|.
name|outputStream
decl_stmt|;
name|out
operator|.
name|writeOnRetry
argument_list|(
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
comment|/**    * Builder class for ChunkGroupOutputStreamEntry.    * */
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
block|{
DECL|field|blockID
specifier|private
name|BlockID
name|blockID
decl_stmt|;
DECL|field|key
specifier|private
name|String
name|key
decl_stmt|;
DECL|field|xceiverClientManager
specifier|private
name|XceiverClientManager
name|xceiverClientManager
decl_stmt|;
DECL|field|pipeline
specifier|private
name|Pipeline
name|pipeline
decl_stmt|;
DECL|field|requestId
specifier|private
name|String
name|requestId
decl_stmt|;
DECL|field|chunkSize
specifier|private
name|int
name|chunkSize
decl_stmt|;
DECL|field|length
specifier|private
name|long
name|length
decl_stmt|;
DECL|field|streamBufferFlushSize
specifier|private
name|long
name|streamBufferFlushSize
decl_stmt|;
DECL|field|streamBufferMaxSize
specifier|private
name|long
name|streamBufferMaxSize
decl_stmt|;
DECL|field|watchTimeout
specifier|private
name|long
name|watchTimeout
decl_stmt|;
DECL|field|bufferList
specifier|private
name|List
argument_list|<
name|ByteBuffer
argument_list|>
name|bufferList
decl_stmt|;
DECL|field|token
specifier|private
name|Token
argument_list|<
name|OzoneBlockTokenIdentifier
argument_list|>
name|token
decl_stmt|;
DECL|field|checksum
specifier|private
name|Checksum
name|checksum
decl_stmt|;
DECL|method|setChecksum (Checksum cs)
specifier|public
name|Builder
name|setChecksum
parameter_list|(
name|Checksum
name|cs
parameter_list|)
block|{
name|this
operator|.
name|checksum
operator|=
name|cs
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setBlockID (BlockID bID)
specifier|public
name|Builder
name|setBlockID
parameter_list|(
name|BlockID
name|bID
parameter_list|)
block|{
name|this
operator|.
name|blockID
operator|=
name|bID
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setKey (String keys)
specifier|public
name|Builder
name|setKey
parameter_list|(
name|String
name|keys
parameter_list|)
block|{
name|this
operator|.
name|key
operator|=
name|keys
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setXceiverClientManager (XceiverClientManager xClientManager)
specifier|public
name|Builder
name|setXceiverClientManager
parameter_list|(
name|XceiverClientManager
name|xClientManager
parameter_list|)
block|{
name|this
operator|.
name|xceiverClientManager
operator|=
name|xClientManager
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setPipeline (Pipeline ppln)
specifier|public
name|Builder
name|setPipeline
parameter_list|(
name|Pipeline
name|ppln
parameter_list|)
block|{
name|this
operator|.
name|pipeline
operator|=
name|ppln
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setRequestId (String request)
specifier|public
name|Builder
name|setRequestId
parameter_list|(
name|String
name|request
parameter_list|)
block|{
name|this
operator|.
name|requestId
operator|=
name|request
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setChunkSize (int cSize)
specifier|public
name|Builder
name|setChunkSize
parameter_list|(
name|int
name|cSize
parameter_list|)
block|{
name|this
operator|.
name|chunkSize
operator|=
name|cSize
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setLength (long len)
specifier|public
name|Builder
name|setLength
parameter_list|(
name|long
name|len
parameter_list|)
block|{
name|this
operator|.
name|length
operator|=
name|len
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setStreamBufferFlushSize (long bufferFlushSize)
specifier|public
name|Builder
name|setStreamBufferFlushSize
parameter_list|(
name|long
name|bufferFlushSize
parameter_list|)
block|{
name|this
operator|.
name|streamBufferFlushSize
operator|=
name|bufferFlushSize
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setStreamBufferMaxSize (long bufferMaxSize)
specifier|public
name|Builder
name|setStreamBufferMaxSize
parameter_list|(
name|long
name|bufferMaxSize
parameter_list|)
block|{
name|this
operator|.
name|streamBufferMaxSize
operator|=
name|bufferMaxSize
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setWatchTimeout (long timeout)
specifier|public
name|Builder
name|setWatchTimeout
parameter_list|(
name|long
name|timeout
parameter_list|)
block|{
name|this
operator|.
name|watchTimeout
operator|=
name|timeout
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setBufferList (List<ByteBuffer> bffrLst)
specifier|public
name|Builder
name|setBufferList
parameter_list|(
name|List
argument_list|<
name|ByteBuffer
argument_list|>
name|bffrLst
parameter_list|)
block|{
name|this
operator|.
name|bufferList
operator|=
name|bffrLst
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setToken (Token<OzoneBlockTokenIdentifier> bToken)
specifier|public
name|Builder
name|setToken
parameter_list|(
name|Token
argument_list|<
name|OzoneBlockTokenIdentifier
argument_list|>
name|bToken
parameter_list|)
block|{
name|this
operator|.
name|token
operator|=
name|bToken
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|build ()
specifier|public
name|BlockOutputStreamEntry
name|build
parameter_list|()
block|{
return|return
operator|new
name|BlockOutputStreamEntry
argument_list|(
name|blockID
argument_list|,
name|key
argument_list|,
name|xceiverClientManager
argument_list|,
name|pipeline
argument_list|,
name|requestId
argument_list|,
name|chunkSize
argument_list|,
name|length
argument_list|,
name|streamBufferFlushSize
argument_list|,
name|streamBufferMaxSize
argument_list|,
name|watchTimeout
argument_list|,
name|bufferList
argument_list|,
name|checksum
argument_list|,
name|token
argument_list|)
return|;
block|}
block|}
DECL|method|getOutputStream ()
specifier|public
name|OutputStream
name|getOutputStream
parameter_list|()
block|{
return|return
name|outputStream
return|;
block|}
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
DECL|method|getKey ()
specifier|public
name|String
name|getKey
parameter_list|()
block|{
return|return
name|key
return|;
block|}
DECL|method|getXceiverClientManager ()
specifier|public
name|XceiverClientManager
name|getXceiverClientManager
parameter_list|()
block|{
return|return
name|xceiverClientManager
return|;
block|}
DECL|method|getPipeline ()
specifier|public
name|Pipeline
name|getPipeline
parameter_list|()
block|{
return|return
name|pipeline
return|;
block|}
DECL|method|getChecksum ()
specifier|public
name|Checksum
name|getChecksum
parameter_list|()
block|{
return|return
name|checksum
return|;
block|}
DECL|method|getRequestId ()
specifier|public
name|String
name|getRequestId
parameter_list|()
block|{
return|return
name|requestId
return|;
block|}
DECL|method|getChunkSize ()
specifier|public
name|int
name|getChunkSize
parameter_list|()
block|{
return|return
name|chunkSize
return|;
block|}
DECL|method|getCurrentPosition ()
specifier|public
name|long
name|getCurrentPosition
parameter_list|()
block|{
return|return
name|currentPosition
return|;
block|}
DECL|method|getStreamBufferFlushSize ()
specifier|public
name|long
name|getStreamBufferFlushSize
parameter_list|()
block|{
return|return
name|streamBufferFlushSize
return|;
block|}
DECL|method|getStreamBufferMaxSize ()
specifier|public
name|long
name|getStreamBufferMaxSize
parameter_list|()
block|{
return|return
name|streamBufferMaxSize
return|;
block|}
DECL|method|getWatchTimeout ()
specifier|public
name|long
name|getWatchTimeout
parameter_list|()
block|{
return|return
name|watchTimeout
return|;
block|}
DECL|method|getBufferList ()
specifier|public
name|List
argument_list|<
name|ByteBuffer
argument_list|>
name|getBufferList
parameter_list|()
block|{
return|return
name|bufferList
return|;
block|}
DECL|method|setCurrentPosition (long curPosition)
specifier|public
name|void
name|setCurrentPosition
parameter_list|(
name|long
name|curPosition
parameter_list|)
block|{
name|this
operator|.
name|currentPosition
operator|=
name|curPosition
expr_stmt|;
block|}
block|}
end_class

end_unit

