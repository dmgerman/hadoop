begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.cosn
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|cosn
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|Set
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
name|Callable
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
name|ExecutionException
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
name|ExecutorService
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
name|security
operator|.
name|DigestOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|MessageDigest
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|NoSuchAlgorithmException
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Futures
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
name|util
operator|.
name|concurrent
operator|.
name|ListenableFuture
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
name|util
operator|.
name|concurrent
operator|.
name|ListeningExecutorService
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
name|util
operator|.
name|concurrent
operator|.
name|MoreExecutors
import|;
end_import

begin_import
import|import
name|com
operator|.
name|qcloud
operator|.
name|cos
operator|.
name|model
operator|.
name|PartETag
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

begin_comment
comment|/**  * The output stream for the COS blob store.  * Implement streaming upload to COS based on the multipart upload function.  * ( the maximum size of each part is 5GB)  * Support up to 40TB single file by multipart upload (each part is 5GB).  * Improve the upload performance of writing large files by using byte buffers  * and a fixed thread pool.  */
end_comment

begin_class
DECL|class|CosNOutputStream
specifier|public
class|class
name|CosNOutputStream
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
name|CosNOutputStream
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|conf
specifier|private
specifier|final
name|Configuration
name|conf
decl_stmt|;
DECL|field|store
specifier|private
specifier|final
name|NativeFileSystemStore
name|store
decl_stmt|;
DECL|field|digest
specifier|private
name|MessageDigest
name|digest
decl_stmt|;
DECL|field|blockSize
specifier|private
name|long
name|blockSize
decl_stmt|;
DECL|field|key
specifier|private
name|String
name|key
decl_stmt|;
DECL|field|currentBlockId
specifier|private
name|int
name|currentBlockId
init|=
literal|0
decl_stmt|;
DECL|field|blockCacheBuffers
specifier|private
name|Set
argument_list|<
name|ByteBufferWrapper
argument_list|>
name|blockCacheBuffers
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|currentBlockBuffer
specifier|private
name|ByteBufferWrapper
name|currentBlockBuffer
decl_stmt|;
DECL|field|currentBlockOutputStream
specifier|private
name|OutputStream
name|currentBlockOutputStream
decl_stmt|;
DECL|field|uploadId
specifier|private
name|String
name|uploadId
init|=
literal|null
decl_stmt|;
DECL|field|executorService
specifier|private
name|ListeningExecutorService
name|executorService
decl_stmt|;
DECL|field|etagList
specifier|private
name|List
argument_list|<
name|ListenableFuture
argument_list|<
name|PartETag
argument_list|>
argument_list|>
name|etagList
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|blockWritten
specifier|private
name|int
name|blockWritten
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
DECL|method|CosNOutputStream (Configuration conf, NativeFileSystemStore store, String key, long blockSize, ExecutorService executorService)
specifier|public
name|CosNOutputStream
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|NativeFileSystemStore
name|store
parameter_list|,
name|String
name|key
parameter_list|,
name|long
name|blockSize
parameter_list|,
name|ExecutorService
name|executorService
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
name|this
operator|.
name|store
operator|=
name|store
expr_stmt|;
name|this
operator|.
name|key
operator|=
name|key
expr_stmt|;
name|this
operator|.
name|blockSize
operator|=
name|blockSize
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|blockSize
operator|<
name|Constants
operator|.
name|MIN_PART_SIZE
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"The minimum size of a single block is limited to %d."
argument_list|,
name|Constants
operator|.
name|MIN_PART_SIZE
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|blockSize
operator|=
name|Constants
operator|.
name|MIN_PART_SIZE
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|blockSize
operator|>
name|Constants
operator|.
name|MAX_PART_SIZE
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"The maximum size of a single block is limited to %d."
argument_list|,
name|Constants
operator|.
name|MAX_PART_SIZE
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|blockSize
operator|=
name|Constants
operator|.
name|MAX_PART_SIZE
expr_stmt|;
block|}
comment|// Use a blocking thread pool with fair scheduling
name|this
operator|.
name|executorService
operator|=
name|MoreExecutors
operator|.
name|listeningDecorator
argument_list|(
name|executorService
argument_list|)
expr_stmt|;
try|try
block|{
name|this
operator|.
name|currentBlockBuffer
operator|=
name|BufferPool
operator|.
name|getInstance
argument_list|()
operator|.
name|getBuffer
argument_list|(
operator|(
name|int
operator|)
name|this
operator|.
name|blockSize
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Getting a buffer size: "
operator|+
name|String
operator|.
name|valueOf
argument_list|(
name|this
operator|.
name|blockSize
argument_list|)
operator|+
literal|" from buffer pool occurs an exception: "
argument_list|,
name|e
argument_list|)
throw|;
block|}
try|try
block|{
name|this
operator|.
name|digest
operator|=
name|MessageDigest
operator|.
name|getInstance
argument_list|(
literal|"MD5"
argument_list|)
expr_stmt|;
name|this
operator|.
name|currentBlockOutputStream
operator|=
operator|new
name|DigestOutputStream
argument_list|(
operator|new
name|ByteBufferOutputStream
argument_list|(
name|this
operator|.
name|currentBlockBuffer
operator|.
name|getByteBuffer
argument_list|()
argument_list|)
argument_list|,
name|this
operator|.
name|digest
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchAlgorithmException
name|e
parameter_list|)
block|{
name|this
operator|.
name|digest
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|currentBlockOutputStream
operator|=
operator|new
name|ByteBufferOutputStream
argument_list|(
name|this
operator|.
name|currentBlockBuffer
operator|.
name|getByteBuffer
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
name|this
operator|.
name|currentBlockOutputStream
operator|.
name|flush
argument_list|()
expr_stmt|;
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
if|if
condition|(
name|this
operator|.
name|closed
condition|)
block|{
return|return;
block|}
name|this
operator|.
name|currentBlockOutputStream
operator|.
name|flush
argument_list|()
expr_stmt|;
name|this
operator|.
name|currentBlockOutputStream
operator|.
name|close
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"The output stream has been close, and "
operator|+
literal|"begin to upload the last block: [{}]."
argument_list|,
name|this
operator|.
name|currentBlockId
argument_list|)
expr_stmt|;
name|this
operator|.
name|blockCacheBuffers
operator|.
name|add
argument_list|(
name|this
operator|.
name|currentBlockBuffer
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|blockCacheBuffers
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
name|byte
index|[]
name|md5Hash
init|=
name|this
operator|.
name|digest
operator|==
literal|null
condition|?
literal|null
else|:
name|this
operator|.
name|digest
operator|.
name|digest
argument_list|()
decl_stmt|;
name|store
operator|.
name|storeFile
argument_list|(
name|this
operator|.
name|key
argument_list|,
operator|new
name|ByteBufferInputStream
argument_list|(
name|this
operator|.
name|currentBlockBuffer
operator|.
name|getByteBuffer
argument_list|()
argument_list|)
argument_list|,
name|md5Hash
argument_list|,
name|this
operator|.
name|currentBlockBuffer
operator|.
name|getByteBuffer
argument_list|()
operator|.
name|remaining
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|PartETag
name|partETag
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|blockWritten
operator|>
literal|0
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Upload the last part..., blockId: [{}], written bytes: [{}]"
argument_list|,
name|this
operator|.
name|currentBlockId
argument_list|,
name|this
operator|.
name|blockWritten
argument_list|)
expr_stmt|;
name|partETag
operator|=
name|store
operator|.
name|uploadPart
argument_list|(
operator|new
name|ByteBufferInputStream
argument_list|(
name|currentBlockBuffer
operator|.
name|getByteBuffer
argument_list|()
argument_list|)
argument_list|,
name|key
argument_list|,
name|uploadId
argument_list|,
name|currentBlockId
operator|+
literal|1
argument_list|,
name|currentBlockBuffer
operator|.
name|getByteBuffer
argument_list|()
operator|.
name|remaining
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|final
name|List
argument_list|<
name|PartETag
argument_list|>
name|futurePartETagList
init|=
name|this
operator|.
name|waitForFinishPartUploads
argument_list|()
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|futurePartETagList
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Failed to multipart upload to cos, abort it."
argument_list|)
throw|;
block|}
name|List
argument_list|<
name|PartETag
argument_list|>
name|tmpPartEtagList
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|(
name|futurePartETagList
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|partETag
condition|)
block|{
name|tmpPartEtagList
operator|.
name|add
argument_list|(
name|partETag
argument_list|)
expr_stmt|;
block|}
name|store
operator|.
name|completeMultipartUpload
argument_list|(
name|this
operator|.
name|key
argument_list|,
name|this
operator|.
name|uploadId
argument_list|,
name|tmpPartEtagList
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|BufferPool
operator|.
name|getInstance
argument_list|()
operator|.
name|returnBuffer
argument_list|(
name|this
operator|.
name|currentBlockBuffer
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"An exception occurred "
operator|+
literal|"while returning the buffer to the buffer pool."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"The outputStream for key: [{}] has been uploaded."
argument_list|,
name|key
argument_list|)
expr_stmt|;
name|this
operator|.
name|blockWritten
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|closed
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|waitForFinishPartUploads ()
specifier|private
name|List
argument_list|<
name|PartETag
argument_list|>
name|waitForFinishPartUploads
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Wait for all parts to finish their uploading."
argument_list|)
expr_stmt|;
return|return
name|Futures
operator|.
name|allAsList
argument_list|(
name|this
operator|.
name|etagList
argument_list|)
operator|.
name|get
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Interrupt the part upload."
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Cancelling futures."
argument_list|)
expr_stmt|;
for|for
control|(
name|ListenableFuture
argument_list|<
name|PartETag
argument_list|>
name|future
range|:
name|this
operator|.
name|etagList
control|)
block|{
name|future
operator|.
name|cancel
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
operator|(
name|store
operator|)
operator|.
name|abortMultipartUpload
argument_list|(
name|this
operator|.
name|key
argument_list|,
name|this
operator|.
name|uploadId
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|error
argument_list|(
literal|"Multipart upload with id: [{}] to COS key: [{}]"
argument_list|,
name|this
operator|.
name|uploadId
argument_list|,
name|this
operator|.
name|key
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Multipart upload with id: "
operator|+
name|this
operator|.
name|uploadId
operator|+
literal|" to "
operator|+
name|this
operator|.
name|key
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|uploadPart ()
specifier|private
name|void
name|uploadPart
parameter_list|()
throws|throws
name|IOException
block|{
name|this
operator|.
name|currentBlockOutputStream
operator|.
name|flush
argument_list|()
expr_stmt|;
name|this
operator|.
name|currentBlockOutputStream
operator|.
name|close
argument_list|()
expr_stmt|;
name|this
operator|.
name|blockCacheBuffers
operator|.
name|add
argument_list|(
name|this
operator|.
name|currentBlockBuffer
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|currentBlockId
operator|==
literal|0
condition|)
block|{
name|uploadId
operator|=
operator|(
name|store
operator|)
operator|.
name|getUploadId
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
name|ListenableFuture
argument_list|<
name|PartETag
argument_list|>
name|partETagListenableFuture
init|=
name|this
operator|.
name|executorService
operator|.
name|submit
argument_list|(
operator|new
name|Callable
argument_list|<
name|PartETag
argument_list|>
argument_list|()
block|{
specifier|private
specifier|final
name|ByteBufferWrapper
name|buf
init|=
name|currentBlockBuffer
decl_stmt|;
specifier|private
specifier|final
name|String
name|localKey
init|=
name|key
decl_stmt|;
specifier|private
specifier|final
name|String
name|localUploadId
init|=
name|uploadId
decl_stmt|;
specifier|private
specifier|final
name|int
name|blockId
init|=
name|currentBlockId
decl_stmt|;
annotation|@
name|Override
specifier|public
name|PartETag
name|call
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"{} is uploading a part."
argument_list|,
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|PartETag
name|partETag
init|=
operator|(
name|store
operator|)
operator|.
name|uploadPart
argument_list|(
operator|new
name|ByteBufferInputStream
argument_list|(
name|this
operator|.
name|buf
operator|.
name|getByteBuffer
argument_list|()
argument_list|)
argument_list|,
name|this
operator|.
name|localKey
argument_list|,
name|this
operator|.
name|localUploadId
argument_list|,
name|this
operator|.
name|blockId
operator|+
literal|1
argument_list|,
name|this
operator|.
name|buf
operator|.
name|getByteBuffer
argument_list|()
operator|.
name|remaining
argument_list|()
argument_list|)
decl_stmt|;
name|BufferPool
operator|.
name|getInstance
argument_list|()
operator|.
name|returnBuffer
argument_list|(
name|this
operator|.
name|buf
argument_list|)
expr_stmt|;
return|return
name|partETag
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|this
operator|.
name|etagList
operator|.
name|add
argument_list|(
name|partETagListenableFuture
argument_list|)
expr_stmt|;
try|try
block|{
name|this
operator|.
name|currentBlockBuffer
operator|=
name|BufferPool
operator|.
name|getInstance
argument_list|()
operator|.
name|getBuffer
argument_list|(
operator|(
name|int
operator|)
name|this
operator|.
name|blockSize
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|String
name|errMsg
init|=
name|String
operator|.
name|format
argument_list|(
literal|"Getting a buffer [size:%d] from "
operator|+
literal|"the buffer pool failed."
argument_list|,
name|this
operator|.
name|blockSize
argument_list|)
decl_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
name|errMsg
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|this
operator|.
name|currentBlockId
operator|++
expr_stmt|;
if|if
condition|(
literal|null
operator|!=
name|this
operator|.
name|digest
condition|)
block|{
name|this
operator|.
name|digest
operator|.
name|reset
argument_list|()
expr_stmt|;
name|this
operator|.
name|currentBlockOutputStream
operator|=
operator|new
name|DigestOutputStream
argument_list|(
operator|new
name|ByteBufferOutputStream
argument_list|(
name|this
operator|.
name|currentBlockBuffer
operator|.
name|getByteBuffer
argument_list|()
argument_list|)
argument_list|,
name|this
operator|.
name|digest
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|currentBlockOutputStream
operator|=
operator|new
name|ByteBufferOutputStream
argument_list|(
name|this
operator|.
name|currentBlockBuffer
operator|.
name|getByteBuffer
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
if|if
condition|(
name|this
operator|.
name|closed
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"block stream has been closed."
argument_list|)
throw|;
block|}
while|while
condition|(
name|len
operator|>
literal|0
condition|)
block|{
name|long
name|writeBytes
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|blockWritten
operator|+
name|len
operator|>
name|this
operator|.
name|blockSize
condition|)
block|{
name|writeBytes
operator|=
name|this
operator|.
name|blockSize
operator|-
name|this
operator|.
name|blockWritten
expr_stmt|;
block|}
else|else
block|{
name|writeBytes
operator|=
name|len
expr_stmt|;
block|}
name|this
operator|.
name|currentBlockOutputStream
operator|.
name|write
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
operator|(
name|int
operator|)
name|writeBytes
argument_list|)
expr_stmt|;
name|this
operator|.
name|blockWritten
operator|+=
name|writeBytes
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|blockWritten
operator|>=
name|this
operator|.
name|blockSize
condition|)
block|{
name|this
operator|.
name|uploadPart
argument_list|()
expr_stmt|;
name|this
operator|.
name|blockWritten
operator|=
literal|0
expr_stmt|;
block|}
name|len
operator|-=
name|writeBytes
expr_stmt|;
name|off
operator|+=
name|writeBytes
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|write (byte[] b)
specifier|public
name|void
name|write
parameter_list|(
name|byte
index|[]
name|b
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|write
argument_list|(
name|b
argument_list|,
literal|0
argument_list|,
name|b
operator|.
name|length
argument_list|)
expr_stmt|;
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
if|if
condition|(
name|this
operator|.
name|closed
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"block stream has been closed."
argument_list|)
throw|;
block|}
name|byte
index|[]
name|singleBytes
init|=
operator|new
name|byte
index|[
literal|1
index|]
decl_stmt|;
name|singleBytes
index|[
literal|0
index|]
operator|=
operator|(
name|byte
operator|)
name|b
expr_stmt|;
name|this
operator|.
name|currentBlockOutputStream
operator|.
name|write
argument_list|(
name|singleBytes
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|this
operator|.
name|blockWritten
operator|+=
literal|1
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|blockWritten
operator|>=
name|this
operator|.
name|blockSize
condition|)
block|{
name|this
operator|.
name|uploadPart
argument_list|()
expr_stmt|;
name|this
operator|.
name|blockWritten
operator|=
literal|0
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

