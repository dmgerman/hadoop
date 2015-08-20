begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|s3a
package|;
end_package

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|AmazonClientException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|AmazonServiceException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|event
operator|.
name|ProgressEvent
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|event
operator|.
name|ProgressListener
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|s3
operator|.
name|AmazonS3Client
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|s3
operator|.
name|model
operator|.
name|AbortMultipartUploadRequest
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|s3
operator|.
name|model
operator|.
name|CannedAccessControlList
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|s3
operator|.
name|model
operator|.
name|CompleteMultipartUploadRequest
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|s3
operator|.
name|model
operator|.
name|InitiateMultipartUploadRequest
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|s3
operator|.
name|model
operator|.
name|ObjectMetadata
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|s3
operator|.
name|model
operator|.
name|PartETag
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|s3
operator|.
name|model
operator|.
name|PutObjectRequest
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|s3
operator|.
name|model
operator|.
name|PutObjectResult
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|s3
operator|.
name|model
operator|.
name|UploadPartRequest
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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang
operator|.
name|StringUtils
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
name|classification
operator|.
name|InterfaceStability
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
name|FileSystem
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
name|util
operator|.
name|Progressable
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
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
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
name|ThreadPoolExecutor
import|;
end_import

begin_comment
comment|/**  * Upload files/parts asap directly from a memory buffer (instead of buffering  * to a file).  *<p>  * Uploads are managed low-level rather than through the AWS TransferManager.  * This allows for uploading each part of a multi-part upload as soon as  * the bytes are in memory, rather than waiting until the file is closed.  *<p>  * Unstable: statistics and error handling might evolve  */
end_comment

begin_class
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|S3AFastOutputStream
specifier|public
class|class
name|S3AFastOutputStream
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
name|S3AFileSystem
operator|.
name|LOG
decl_stmt|;
DECL|field|key
specifier|private
specifier|final
name|String
name|key
decl_stmt|;
DECL|field|bucket
specifier|private
specifier|final
name|String
name|bucket
decl_stmt|;
DECL|field|client
specifier|private
specifier|final
name|AmazonS3Client
name|client
decl_stmt|;
DECL|field|partSize
specifier|private
specifier|final
name|int
name|partSize
decl_stmt|;
DECL|field|multiPartThreshold
specifier|private
specifier|final
name|int
name|multiPartThreshold
decl_stmt|;
DECL|field|fs
specifier|private
specifier|final
name|S3AFileSystem
name|fs
decl_stmt|;
DECL|field|cannedACL
specifier|private
specifier|final
name|CannedAccessControlList
name|cannedACL
decl_stmt|;
DECL|field|statistics
specifier|private
specifier|final
name|FileSystem
operator|.
name|Statistics
name|statistics
decl_stmt|;
DECL|field|serverSideEncryptionAlgorithm
specifier|private
specifier|final
name|String
name|serverSideEncryptionAlgorithm
decl_stmt|;
DECL|field|progressListener
specifier|private
specifier|final
name|ProgressListener
name|progressListener
decl_stmt|;
DECL|field|executorService
specifier|private
specifier|final
name|ListeningExecutorService
name|executorService
decl_stmt|;
DECL|field|multiPartUpload
specifier|private
name|MultiPartUpload
name|multiPartUpload
decl_stmt|;
DECL|field|closed
specifier|private
name|boolean
name|closed
decl_stmt|;
DECL|field|buffer
specifier|private
name|ByteArrayOutputStream
name|buffer
decl_stmt|;
DECL|field|bufferLimit
specifier|private
name|int
name|bufferLimit
decl_stmt|;
comment|/**    * Creates a fast OutputStream that uploads to S3 from memory.    * For MultiPartUploads, as soon as sufficient bytes have been written to    * the stream a part is uploaded immediately (by using the low-level    * multi-part upload API on the AmazonS3Client).    *    * @param client AmazonS3Client used for S3 calls    * @param fs S3AFilesystem    * @param bucket S3 bucket name    * @param key S3 key name    * @param progress report progress in order to prevent timeouts    * @param statistics track FileSystem.Statistics on the performed operations    * @param cannedACL used CannedAccessControlList    * @param serverSideEncryptionAlgorithm algorithm for server side encryption    * @param partSize size of a single part in a multi-part upload (except    * last part)    * @param multiPartThreshold files at least this size use multi-part upload    * @throws IOException    */
DECL|method|S3AFastOutputStream (AmazonS3Client client, S3AFileSystem fs, String bucket, String key, Progressable progress, FileSystem.Statistics statistics, CannedAccessControlList cannedACL, String serverSideEncryptionAlgorithm, long partSize, long multiPartThreshold, ThreadPoolExecutor threadPoolExecutor)
specifier|public
name|S3AFastOutputStream
parameter_list|(
name|AmazonS3Client
name|client
parameter_list|,
name|S3AFileSystem
name|fs
parameter_list|,
name|String
name|bucket
parameter_list|,
name|String
name|key
parameter_list|,
name|Progressable
name|progress
parameter_list|,
name|FileSystem
operator|.
name|Statistics
name|statistics
parameter_list|,
name|CannedAccessControlList
name|cannedACL
parameter_list|,
name|String
name|serverSideEncryptionAlgorithm
parameter_list|,
name|long
name|partSize
parameter_list|,
name|long
name|multiPartThreshold
parameter_list|,
name|ThreadPoolExecutor
name|threadPoolExecutor
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|bucket
operator|=
name|bucket
expr_stmt|;
name|this
operator|.
name|key
operator|=
name|key
expr_stmt|;
name|this
operator|.
name|client
operator|=
name|client
expr_stmt|;
name|this
operator|.
name|fs
operator|=
name|fs
expr_stmt|;
name|this
operator|.
name|cannedACL
operator|=
name|cannedACL
expr_stmt|;
name|this
operator|.
name|statistics
operator|=
name|statistics
expr_stmt|;
name|this
operator|.
name|serverSideEncryptionAlgorithm
operator|=
name|serverSideEncryptionAlgorithm
expr_stmt|;
comment|//Ensure limit as ByteArrayOutputStream size cannot exceed Integer.MAX_VALUE
if|if
condition|(
name|partSize
operator|>
name|Integer
operator|.
name|MAX_VALUE
condition|)
block|{
name|this
operator|.
name|partSize
operator|=
name|Integer
operator|.
name|MAX_VALUE
expr_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"s3a: MULTIPART_SIZE capped to ~2.14GB (maximum allowed size "
operator|+
literal|"when using 'FAST_UPLOAD = true')"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|partSize
operator|=
operator|(
name|int
operator|)
name|partSize
expr_stmt|;
block|}
if|if
condition|(
name|multiPartThreshold
operator|>
name|Integer
operator|.
name|MAX_VALUE
condition|)
block|{
name|this
operator|.
name|multiPartThreshold
operator|=
name|Integer
operator|.
name|MAX_VALUE
expr_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"s3a: MIN_MULTIPART_THRESHOLD capped to ~2.14GB (maximum "
operator|+
literal|"allowed size when using 'FAST_UPLOAD = true')"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|multiPartThreshold
operator|=
operator|(
name|int
operator|)
name|multiPartThreshold
expr_stmt|;
block|}
name|this
operator|.
name|bufferLimit
operator|=
name|this
operator|.
name|multiPartThreshold
expr_stmt|;
name|this
operator|.
name|closed
operator|=
literal|false
expr_stmt|;
name|int
name|initialBufferSize
init|=
name|this
operator|.
name|fs
operator|.
name|getConf
argument_list|()
operator|.
name|getInt
argument_list|(
name|Constants
operator|.
name|FAST_BUFFER_SIZE
argument_list|,
name|Constants
operator|.
name|DEFAULT_FAST_BUFFER_SIZE
argument_list|)
decl_stmt|;
if|if
condition|(
name|initialBufferSize
operator|<
literal|0
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"s3a: FAST_BUFFER_SIZE should be a positive number. Using "
operator|+
literal|"default value"
argument_list|)
expr_stmt|;
name|initialBufferSize
operator|=
name|Constants
operator|.
name|DEFAULT_FAST_BUFFER_SIZE
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|initialBufferSize
operator|>
name|this
operator|.
name|bufferLimit
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"s3a: automatically adjusting FAST_BUFFER_SIZE to not "
operator|+
literal|"exceed MIN_MULTIPART_THRESHOLD"
argument_list|)
expr_stmt|;
name|initialBufferSize
operator|=
name|this
operator|.
name|bufferLimit
expr_stmt|;
block|}
name|this
operator|.
name|buffer
operator|=
operator|new
name|ByteArrayOutputStream
argument_list|(
name|initialBufferSize
argument_list|)
expr_stmt|;
name|this
operator|.
name|executorService
operator|=
name|MoreExecutors
operator|.
name|listeningDecorator
argument_list|(
name|threadPoolExecutor
argument_list|)
expr_stmt|;
name|this
operator|.
name|multiPartUpload
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|progressListener
operator|=
operator|new
name|ProgressableListener
argument_list|(
name|progress
argument_list|)
expr_stmt|;
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
literal|"Initialized S3AFastOutputStream for bucket '{}' key '{}'"
argument_list|,
name|bucket
argument_list|,
name|key
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Writes a byte to the memory buffer. If this causes the buffer to reach    * its limit, the actual upload is submitted to the threadpool.    * @param b the int of which the lowest byte is written    * @throws IOException    */
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
name|buffer
operator|.
name|write
argument_list|(
name|b
argument_list|)
expr_stmt|;
if|if
condition|(
name|buffer
operator|.
name|size
argument_list|()
operator|==
name|bufferLimit
condition|)
block|{
name|uploadBuffer
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Writes a range of bytes from to the memory buffer. If this causes the    * buffer to reach its limit, the actual upload is submitted to the    * threadpool and the remainder of the array is written to memory    * (recursively).    * @param b byte array containing    * @param off offset in array where to start    * @param len number of bytes to be written    * @throws IOException    */
annotation|@
name|Override
DECL|method|write (byte b[], int off, int len)
specifier|public
specifier|synchronized
name|void
name|write
parameter_list|(
name|byte
name|b
index|[]
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
elseif|else
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
elseif|else
if|if
condition|(
name|len
operator|==
literal|0
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|buffer
operator|.
name|size
argument_list|()
operator|+
name|len
operator|<
name|bufferLimit
condition|)
block|{
name|buffer
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
block|}
else|else
block|{
name|int
name|firstPart
init|=
name|bufferLimit
operator|-
name|buffer
operator|.
name|size
argument_list|()
decl_stmt|;
name|buffer
operator|.
name|write
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|firstPart
argument_list|)
expr_stmt|;
name|uploadBuffer
argument_list|()
expr_stmt|;
name|this
operator|.
name|write
argument_list|(
name|b
argument_list|,
name|off
operator|+
name|firstPart
argument_list|,
name|len
operator|-
name|firstPart
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|uploadBuffer ()
specifier|private
specifier|synchronized
name|void
name|uploadBuffer
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|multiPartUpload
operator|==
literal|null
condition|)
block|{
name|multiPartUpload
operator|=
name|initiateMultiPartUpload
argument_list|()
expr_stmt|;
comment|/* Upload the existing buffer if it exceeds partSize. This possibly        requires multiple parts! */
specifier|final
name|byte
index|[]
name|allBytes
init|=
name|buffer
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
name|buffer
operator|=
literal|null
expr_stmt|;
comment|//earlier gc?
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
literal|"Total length of initial buffer: {}"
argument_list|,
name|allBytes
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
name|int
name|processedPos
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|(
name|multiPartThreshold
operator|-
name|processedPos
operator|)
operator|>=
name|partSize
condition|)
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
literal|"Initial buffer: processing from byte {} to byte {}"
argument_list|,
name|processedPos
argument_list|,
operator|(
name|processedPos
operator|+
name|partSize
operator|-
literal|1
operator|)
argument_list|)
expr_stmt|;
block|}
name|multiPartUpload
operator|.
name|uploadPartAsync
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|allBytes
argument_list|,
name|processedPos
argument_list|,
name|partSize
argument_list|)
argument_list|,
name|partSize
argument_list|)
expr_stmt|;
name|processedPos
operator|+=
name|partSize
expr_stmt|;
block|}
comment|//resize and reset stream
name|bufferLimit
operator|=
name|partSize
expr_stmt|;
name|buffer
operator|=
operator|new
name|ByteArrayOutputStream
argument_list|(
name|bufferLimit
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|write
argument_list|(
name|allBytes
argument_list|,
name|processedPos
argument_list|,
name|multiPartThreshold
operator|-
name|processedPos
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|//upload next part
name|multiPartUpload
operator|.
name|uploadPartAsync
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|buffer
operator|.
name|toByteArray
argument_list|()
argument_list|)
argument_list|,
name|partSize
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
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
name|closed
condition|)
block|{
return|return;
block|}
name|closed
operator|=
literal|true
expr_stmt|;
try|try
block|{
if|if
condition|(
name|multiPartUpload
operator|==
literal|null
condition|)
block|{
name|putObject
argument_list|()
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|buffer
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
comment|//send last part
name|multiPartUpload
operator|.
name|uploadPartAsync
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|buffer
operator|.
name|toByteArray
argument_list|()
argument_list|)
argument_list|,
name|buffer
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|final
name|List
argument_list|<
name|PartETag
argument_list|>
name|partETags
init|=
name|multiPartUpload
operator|.
name|waitForAllPartUploads
argument_list|()
decl_stmt|;
name|multiPartUpload
operator|.
name|complete
argument_list|(
name|partETags
argument_list|)
expr_stmt|;
block|}
name|statistics
operator|.
name|incrementWriteOps
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|// This will delete unnecessary fake parent directories
name|fs
operator|.
name|finishedWrite
argument_list|(
name|key
argument_list|)
expr_stmt|;
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
literal|"Upload complete for bucket '{}' key '{}'"
argument_list|,
name|bucket
argument_list|,
name|key
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|buffer
operator|=
literal|null
expr_stmt|;
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|createDefaultMetadata ()
specifier|private
name|ObjectMetadata
name|createDefaultMetadata
parameter_list|()
block|{
name|ObjectMetadata
name|om
init|=
operator|new
name|ObjectMetadata
argument_list|()
decl_stmt|;
if|if
condition|(
name|StringUtils
operator|.
name|isNotBlank
argument_list|(
name|serverSideEncryptionAlgorithm
argument_list|)
condition|)
block|{
name|om
operator|.
name|setSSEAlgorithm
argument_list|(
name|serverSideEncryptionAlgorithm
argument_list|)
expr_stmt|;
block|}
return|return
name|om
return|;
block|}
DECL|method|initiateMultiPartUpload ()
specifier|private
name|MultiPartUpload
name|initiateMultiPartUpload
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|ObjectMetadata
name|om
init|=
name|createDefaultMetadata
argument_list|()
decl_stmt|;
specifier|final
name|InitiateMultipartUploadRequest
name|initiateMPURequest
init|=
operator|new
name|InitiateMultipartUploadRequest
argument_list|(
name|bucket
argument_list|,
name|key
argument_list|,
name|om
argument_list|)
decl_stmt|;
name|initiateMPURequest
operator|.
name|setCannedACL
argument_list|(
name|cannedACL
argument_list|)
expr_stmt|;
try|try
block|{
return|return
operator|new
name|MultiPartUpload
argument_list|(
name|client
operator|.
name|initiateMultipartUpload
argument_list|(
name|initiateMPURequest
argument_list|)
operator|.
name|getUploadId
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|AmazonServiceException
name|ase
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unable to initiate MultiPartUpload (server side)"
operator|+
literal|": "
operator|+
name|ase
argument_list|,
name|ase
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|AmazonClientException
name|ace
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unable to initiate MultiPartUpload (client side)"
operator|+
literal|": "
operator|+
name|ace
argument_list|,
name|ace
argument_list|)
throw|;
block|}
block|}
DECL|method|putObject ()
specifier|private
name|void
name|putObject
parameter_list|()
throws|throws
name|IOException
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
literal|"Executing regular upload for bucket '{}' key '{}'"
argument_list|,
name|bucket
argument_list|,
name|key
argument_list|)
expr_stmt|;
block|}
specifier|final
name|ObjectMetadata
name|om
init|=
name|createDefaultMetadata
argument_list|()
decl_stmt|;
name|om
operator|.
name|setContentLength
argument_list|(
name|buffer
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|PutObjectRequest
name|putObjectRequest
init|=
operator|new
name|PutObjectRequest
argument_list|(
name|bucket
argument_list|,
name|key
argument_list|,
operator|new
name|ByteArrayInputStream
argument_list|(
name|buffer
operator|.
name|toByteArray
argument_list|()
argument_list|)
argument_list|,
name|om
argument_list|)
decl_stmt|;
name|putObjectRequest
operator|.
name|setCannedAcl
argument_list|(
name|cannedACL
argument_list|)
expr_stmt|;
name|putObjectRequest
operator|.
name|setGeneralProgressListener
argument_list|(
name|progressListener
argument_list|)
expr_stmt|;
name|ListenableFuture
argument_list|<
name|PutObjectResult
argument_list|>
name|putObjectResult
init|=
name|executorService
operator|.
name|submit
argument_list|(
operator|new
name|Callable
argument_list|<
name|PutObjectResult
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|PutObjectResult
name|call
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|client
operator|.
name|putObject
argument_list|(
name|putObjectRequest
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
comment|//wait for completion
try|try
block|{
name|putObjectResult
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Interrupted object upload:"
operator|+
name|ie
argument_list|,
name|ie
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|ee
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Regular upload failed"
argument_list|,
name|ee
operator|.
name|getCause
argument_list|()
argument_list|)
throw|;
block|}
block|}
DECL|class|MultiPartUpload
specifier|private
class|class
name|MultiPartUpload
block|{
DECL|field|uploadId
specifier|private
specifier|final
name|String
name|uploadId
decl_stmt|;
DECL|field|partETagsFutures
specifier|private
specifier|final
name|List
argument_list|<
name|ListenableFuture
argument_list|<
name|PartETag
argument_list|>
argument_list|>
name|partETagsFutures
decl_stmt|;
DECL|method|MultiPartUpload (String uploadId)
specifier|public
name|MultiPartUpload
parameter_list|(
name|String
name|uploadId
parameter_list|)
block|{
name|this
operator|.
name|uploadId
operator|=
name|uploadId
expr_stmt|;
name|this
operator|.
name|partETagsFutures
operator|=
operator|new
name|ArrayList
argument_list|<
name|ListenableFuture
argument_list|<
name|PartETag
argument_list|>
argument_list|>
argument_list|()
expr_stmt|;
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
literal|"Initiated multi-part upload for bucket '{}' key '{}' with "
operator|+
literal|"id '{}'"
argument_list|,
name|bucket
argument_list|,
name|key
argument_list|,
name|uploadId
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|uploadPartAsync (ByteArrayInputStream inputStream, int partSize)
specifier|public
name|void
name|uploadPartAsync
parameter_list|(
name|ByteArrayInputStream
name|inputStream
parameter_list|,
name|int
name|partSize
parameter_list|)
block|{
specifier|final
name|int
name|currentPartNumber
init|=
name|partETagsFutures
operator|.
name|size
argument_list|()
operator|+
literal|1
decl_stmt|;
specifier|final
name|UploadPartRequest
name|request
init|=
operator|new
name|UploadPartRequest
argument_list|()
operator|.
name|withBucketName
argument_list|(
name|bucket
argument_list|)
operator|.
name|withKey
argument_list|(
name|key
argument_list|)
operator|.
name|withUploadId
argument_list|(
name|uploadId
argument_list|)
operator|.
name|withInputStream
argument_list|(
name|inputStream
argument_list|)
operator|.
name|withPartNumber
argument_list|(
name|currentPartNumber
argument_list|)
operator|.
name|withPartSize
argument_list|(
name|partSize
argument_list|)
decl_stmt|;
name|request
operator|.
name|setGeneralProgressListener
argument_list|(
name|progressListener
argument_list|)
expr_stmt|;
name|ListenableFuture
argument_list|<
name|PartETag
argument_list|>
name|partETagFuture
init|=
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
literal|"Uploading part {} for id '{}'"
argument_list|,
name|currentPartNumber
argument_list|,
name|uploadId
argument_list|)
expr_stmt|;
block|}
return|return
name|client
operator|.
name|uploadPart
argument_list|(
name|request
argument_list|)
operator|.
name|getPartETag
argument_list|()
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|partETagsFutures
operator|.
name|add
argument_list|(
name|partETagFuture
argument_list|)
expr_stmt|;
block|}
DECL|method|waitForAllPartUploads ()
specifier|public
name|List
argument_list|<
name|PartETag
argument_list|>
name|waitForAllPartUploads
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
return|return
name|Futures
operator|.
name|allAsList
argument_list|(
name|partETagsFutures
argument_list|)
operator|.
name|get
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Interrupted partUpload:"
operator|+
name|ie
argument_list|,
name|ie
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|ee
parameter_list|)
block|{
comment|//there is no way of recovering so abort
comment|//cancel all partUploads
for|for
control|(
name|ListenableFuture
argument_list|<
name|PartETag
argument_list|>
name|future
range|:
name|partETagsFutures
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
comment|//abort multipartupload
name|this
operator|.
name|abort
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Part upload failed in multi-part upload with "
operator|+
literal|"id '"
operator|+
name|uploadId
operator|+
literal|"':"
operator|+
name|ee
argument_list|,
name|ee
argument_list|)
throw|;
block|}
comment|//should not happen?
return|return
literal|null
return|;
block|}
DECL|method|complete (List<PartETag> partETags)
specifier|public
name|void
name|complete
parameter_list|(
name|List
argument_list|<
name|PartETag
argument_list|>
name|partETags
parameter_list|)
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
literal|"Completing multi-part upload for key '{}', id '{}'"
argument_list|,
name|key
argument_list|,
name|uploadId
argument_list|)
expr_stmt|;
block|}
specifier|final
name|CompleteMultipartUploadRequest
name|completeRequest
init|=
operator|new
name|CompleteMultipartUploadRequest
argument_list|(
name|bucket
argument_list|,
name|key
argument_list|,
name|uploadId
argument_list|,
name|partETags
argument_list|)
decl_stmt|;
name|client
operator|.
name|completeMultipartUpload
argument_list|(
name|completeRequest
argument_list|)
expr_stmt|;
block|}
DECL|method|abort ()
specifier|public
name|void
name|abort
parameter_list|()
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Aborting multi-part upload with id '{}'"
argument_list|,
name|uploadId
argument_list|)
expr_stmt|;
try|try
block|{
name|client
operator|.
name|abortMultipartUpload
argument_list|(
operator|new
name|AbortMultipartUploadRequest
argument_list|(
name|bucket
argument_list|,
name|key
argument_list|,
name|uploadId
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e2
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unable to abort multipart upload, you may need to purge  "
operator|+
literal|"uploaded parts: "
operator|+
name|e2
argument_list|,
name|e2
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|class|ProgressableListener
specifier|private
specifier|static
class|class
name|ProgressableListener
implements|implements
name|ProgressListener
block|{
DECL|field|progress
specifier|private
specifier|final
name|Progressable
name|progress
decl_stmt|;
DECL|method|ProgressableListener (Progressable progress)
specifier|public
name|ProgressableListener
parameter_list|(
name|Progressable
name|progress
parameter_list|)
block|{
name|this
operator|.
name|progress
operator|=
name|progress
expr_stmt|;
block|}
DECL|method|progressChanged (ProgressEvent progressEvent)
specifier|public
name|void
name|progressChanged
parameter_list|(
name|ProgressEvent
name|progressEvent
parameter_list|)
block|{
if|if
condition|(
name|progress
operator|!=
literal|null
condition|)
block|{
name|progress
operator|.
name|progress
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

