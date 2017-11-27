begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|FileNotFoundException
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
name|atomic
operator|.
name|AtomicInteger
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
name|CompleteMultipartUploadResult
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
name|MultipartUpload
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
name|amazonaws
operator|.
name|services
operator|.
name|s3
operator|.
name|model
operator|.
name|UploadPartResult
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
name|transfer
operator|.
name|model
operator|.
name|UploadResult
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|classification
operator|.
name|InterfaceAudience
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
name|Path
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkArgument
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkNotNull
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
name|fs
operator|.
name|s3a
operator|.
name|Invoker
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Helper for low-level operations against an S3 Bucket for writing data  * and creating and committing pending writes.  *<p>  * It hides direct access to the S3 API  * and is a location where the object upload process can be evolved/enhanced.  *<p>  * Features  *<ul>  *<li>Methods to create and submit requests to S3, so avoiding  *   all direct interaction with the AWS APIs.</li>  *<li>Some extra preflight checks of arguments, so failing fast on  *   errors.</li>  *<li>Callbacks to let the FS know of events in the output stream  *   upload process.</li>  *<li>Failure handling, including converting exceptions to IOEs.</li>  *<li>Integration with instrumentation and S3Guard.</li>  *</ul>  *  * This API is for internal use only.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|WriteOperationHelper
specifier|public
class|class
name|WriteOperationHelper
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
name|WriteOperationHelper
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|owner
specifier|private
specifier|final
name|S3AFileSystem
name|owner
decl_stmt|;
DECL|field|invoker
specifier|private
specifier|final
name|Invoker
name|invoker
decl_stmt|;
comment|/**    * Constructor.    * @param owner owner FS creating the helper    *    */
DECL|method|WriteOperationHelper (S3AFileSystem owner)
specifier|protected
name|WriteOperationHelper
parameter_list|(
name|S3AFileSystem
name|owner
parameter_list|)
block|{
name|this
operator|.
name|owner
operator|=
name|owner
expr_stmt|;
name|this
operator|.
name|invoker
operator|=
operator|new
name|Invoker
argument_list|(
operator|new
name|S3ARetryPolicy
argument_list|(
name|owner
operator|.
name|getConf
argument_list|()
argument_list|)
argument_list|,
name|this
operator|::
name|operationRetried
argument_list|)
expr_stmt|;
block|}
comment|/**    * Callback from {@link Invoker} when an operation is retried.    * @param text text of the operation    * @param ex exception    * @param retries number of retries    * @param idempotent is the method idempotent    */
DECL|method|operationRetried (String text, Exception ex, int retries, boolean idempotent)
name|void
name|operationRetried
parameter_list|(
name|String
name|text
parameter_list|,
name|Exception
name|ex
parameter_list|,
name|int
name|retries
parameter_list|,
name|boolean
name|idempotent
parameter_list|)
block|{
name|owner
operator|.
name|operationRetried
argument_list|(
name|text
argument_list|,
name|ex
argument_list|,
name|retries
argument_list|,
name|idempotent
argument_list|)
expr_stmt|;
block|}
comment|/**    * Execute a function with retry processing.    * @param action action to execute (used in error messages)    * @param path path of work (used in error messages)    * @param idempotent does the operation have semantics    * which mean that it can be retried even if was already executed?    * @param operation operation to execute    * @param<T> type of return value    * @return the result of the call    * @throws IOException any IOE raised, or translated exception    */
DECL|method|retry (String action, String path, boolean idempotent, Invoker.Operation<T> operation)
specifier|public
parameter_list|<
name|T
parameter_list|>
name|T
name|retry
parameter_list|(
name|String
name|action
parameter_list|,
name|String
name|path
parameter_list|,
name|boolean
name|idempotent
parameter_list|,
name|Invoker
operator|.
name|Operation
argument_list|<
name|T
argument_list|>
name|operation
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|invoker
operator|.
name|retry
argument_list|(
name|action
argument_list|,
name|path
argument_list|,
name|idempotent
argument_list|,
name|operation
argument_list|)
return|;
block|}
comment|/**    * Create a {@link PutObjectRequest} request against the specific key.    * @param destKey destination key    * @param inputStream source data.    * @param length size, if known. Use -1 for not known    * @return the request    */
DECL|method|createPutObjectRequest (String destKey, InputStream inputStream, long length)
specifier|public
name|PutObjectRequest
name|createPutObjectRequest
parameter_list|(
name|String
name|destKey
parameter_list|,
name|InputStream
name|inputStream
parameter_list|,
name|long
name|length
parameter_list|)
block|{
return|return
name|owner
operator|.
name|newPutObjectRequest
argument_list|(
name|destKey
argument_list|,
name|newObjectMetadata
argument_list|(
name|length
argument_list|)
argument_list|,
name|inputStream
argument_list|)
return|;
block|}
comment|/**    * Create a {@link PutObjectRequest} request to upload a file.    * @param dest key to PUT to.    * @param sourceFile source file    * @return the request    */
DECL|method|createPutObjectRequest (String dest, File sourceFile)
specifier|public
name|PutObjectRequest
name|createPutObjectRequest
parameter_list|(
name|String
name|dest
parameter_list|,
name|File
name|sourceFile
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
name|sourceFile
operator|.
name|length
argument_list|()
operator|<
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
literal|"File length is too big for a single PUT upload"
argument_list|)
expr_stmt|;
return|return
name|owner
operator|.
name|newPutObjectRequest
argument_list|(
name|dest
argument_list|,
name|newObjectMetadata
argument_list|(
operator|(
name|int
operator|)
name|sourceFile
operator|.
name|length
argument_list|()
argument_list|)
argument_list|,
name|sourceFile
argument_list|)
return|;
block|}
comment|/**    * Callback on a successful write.    * @param length length of the write    */
DECL|method|writeSuccessful (long length)
specifier|public
name|void
name|writeSuccessful
parameter_list|(
name|long
name|length
parameter_list|)
block|{   }
comment|/**    * Callback on a write failure.    * @param ex Any exception raised which triggered the failure.    */
DECL|method|writeFailed (Exception ex)
specifier|public
name|void
name|writeFailed
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Write to {} failed"
argument_list|,
name|this
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a new object metadata instance.    * Any standard metadata headers are added here, for example:    * encryption.    * @param length size, if known. Use -1 for not known    * @return a new metadata instance    */
DECL|method|newObjectMetadata (long length)
specifier|public
name|ObjectMetadata
name|newObjectMetadata
parameter_list|(
name|long
name|length
parameter_list|)
block|{
return|return
name|owner
operator|.
name|newObjectMetadata
argument_list|(
name|length
argument_list|)
return|;
block|}
comment|/**    * Start the multipart upload process.    * Retry policy: retrying, translated.    * @param destKey destination of upload    * @return the upload result containing the ID    * @throws IOException IO problem    */
annotation|@
name|Retries
operator|.
name|RetryTranslated
DECL|method|initiateMultiPartUpload (String destKey)
specifier|public
name|String
name|initiateMultiPartUpload
parameter_list|(
name|String
name|destKey
parameter_list|)
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Initiating Multipart upload to {}"
argument_list|,
name|destKey
argument_list|)
expr_stmt|;
specifier|final
name|InitiateMultipartUploadRequest
name|initiateMPURequest
init|=
operator|new
name|InitiateMultipartUploadRequest
argument_list|(
name|owner
operator|.
name|getBucket
argument_list|()
argument_list|,
name|destKey
argument_list|,
name|newObjectMetadata
argument_list|(
operator|-
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|initiateMPURequest
operator|.
name|setCannedACL
argument_list|(
name|owner
operator|.
name|getCannedACL
argument_list|()
argument_list|)
expr_stmt|;
name|owner
operator|.
name|setOptionalMultipartUploadRequestParameters
argument_list|(
name|initiateMPURequest
argument_list|)
expr_stmt|;
return|return
name|retry
argument_list|(
literal|"initiate MultiPartUpload"
argument_list|,
name|destKey
argument_list|,
literal|true
argument_list|,
parameter_list|()
lambda|->
name|owner
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
comment|/**    * Finalize a multipart PUT operation.    * This completes the upload, and, if that works, calls    * {@link S3AFileSystem#finishedWrite(String, long)} to update the filesystem.    * Retry policy: retrying, translated.    * @param destKey destination of the commit    * @param uploadId multipart operation Id    * @param partETags list of partial uploads    * @param length length of the upload    * @param retrying retrying callback    * @return the result of the operation.    * @throws IOException on problems.    */
annotation|@
name|Retries
operator|.
name|RetryTranslated
DECL|method|finalizeMultipartUpload ( String destKey, String uploadId, List<PartETag> partETags, long length, Retried retrying)
specifier|private
name|CompleteMultipartUploadResult
name|finalizeMultipartUpload
parameter_list|(
name|String
name|destKey
parameter_list|,
name|String
name|uploadId
parameter_list|,
name|List
argument_list|<
name|PartETag
argument_list|>
name|partETags
parameter_list|,
name|long
name|length
parameter_list|,
name|Retried
name|retrying
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|invoker
operator|.
name|retry
argument_list|(
literal|"Completing multipart commit"
argument_list|,
name|destKey
argument_list|,
literal|true
argument_list|,
name|retrying
argument_list|,
parameter_list|()
lambda|->
block|{
comment|// a copy of the list is required, so that the AWS SDK doesn't
comment|// attempt to sort an unmodifiable list.
name|CompleteMultipartUploadResult
name|result
init|=
name|owner
operator|.
name|getAmazonS3Client
argument_list|()
operator|.
name|completeMultipartUpload
argument_list|(
operator|new
name|CompleteMultipartUploadRequest
argument_list|(
name|owner
operator|.
name|getBucket
argument_list|()
argument_list|,
name|destKey
argument_list|,
name|uploadId
argument_list|,
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|partETags
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|owner
operator|.
name|finishedWrite
argument_list|(
name|destKey
argument_list|,
name|length
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
argument_list|)
return|;
block|}
comment|/**    * This completes a multipart upload to the destination key via    * {@code finalizeMultipartUpload()}.    * Retry policy: retrying, translated.    * Retries increment the {@code errorCount} counter.    * @param destKey destination    * @param uploadId multipart operation Id    * @param partETags list of partial uploads    * @param length length of the upload    * @param errorCount a counter incremented by 1 on every error; for    * use in statistics    * @return the result of the operation.    * @throws IOException if problems arose which could not be retried, or    * the retry count was exceeded    */
annotation|@
name|Retries
operator|.
name|RetryTranslated
DECL|method|completeMPUwithRetries ( String destKey, String uploadId, List<PartETag> partETags, long length, AtomicInteger errorCount)
specifier|public
name|CompleteMultipartUploadResult
name|completeMPUwithRetries
parameter_list|(
name|String
name|destKey
parameter_list|,
name|String
name|uploadId
parameter_list|,
name|List
argument_list|<
name|PartETag
argument_list|>
name|partETags
parameter_list|,
name|long
name|length
parameter_list|,
name|AtomicInteger
name|errorCount
parameter_list|)
throws|throws
name|IOException
block|{
name|checkNotNull
argument_list|(
name|uploadId
argument_list|)
expr_stmt|;
name|checkNotNull
argument_list|(
name|partETags
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Completing multipart upload {} with {} parts"
argument_list|,
name|uploadId
argument_list|,
name|partETags
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|finalizeMultipartUpload
argument_list|(
name|destKey
argument_list|,
name|uploadId
argument_list|,
name|partETags
argument_list|,
name|length
argument_list|,
parameter_list|(
name|text
parameter_list|,
name|e
parameter_list|,
name|r
parameter_list|,
name|i
parameter_list|)
lambda|->
name|errorCount
operator|.
name|incrementAndGet
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Abort a multipart upload operation.    * @param destKey destination key of the upload    * @param uploadId multipart operation Id    * @param retrying callback invoked on every retry    * @throws IOException failure to abort    * @throws FileNotFoundException if the abort ID is unknown    */
annotation|@
name|Retries
operator|.
name|RetryTranslated
DECL|method|abortMultipartUpload (String destKey, String uploadId, Retried retrying)
specifier|public
name|void
name|abortMultipartUpload
parameter_list|(
name|String
name|destKey
parameter_list|,
name|String
name|uploadId
parameter_list|,
name|Retried
name|retrying
parameter_list|)
throws|throws
name|IOException
block|{
name|invoker
operator|.
name|retry
argument_list|(
literal|"Aborting multipart upload"
argument_list|,
name|destKey
argument_list|,
literal|true
argument_list|,
name|retrying
argument_list|,
parameter_list|()
lambda|->
name|owner
operator|.
name|abortMultipartUpload
argument_list|(
name|destKey
argument_list|,
name|uploadId
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Abort a multipart commit operation.    * @param upload upload to abort.    * @throws IOException on problems.    */
annotation|@
name|Retries
operator|.
name|RetryTranslated
DECL|method|abortMultipartUpload (MultipartUpload upload)
specifier|public
name|void
name|abortMultipartUpload
parameter_list|(
name|MultipartUpload
name|upload
parameter_list|)
throws|throws
name|IOException
block|{
name|invoker
operator|.
name|retry
argument_list|(
literal|"Aborting multipart commit"
argument_list|,
name|upload
operator|.
name|getKey
argument_list|()
argument_list|,
literal|true
argument_list|,
parameter_list|()
lambda|->
name|owner
operator|.
name|abortMultipartUpload
argument_list|(
name|upload
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Abort multipart uploads under a path: limited to the first    * few hundred.    * @param prefix prefix for uploads to abort    * @return a count of aborts    * @throws IOException trouble; FileNotFoundExceptions are swallowed.    */
annotation|@
name|Retries
operator|.
name|RetryTranslated
DECL|method|abortMultipartUploadsUnderPath (String prefix)
specifier|public
name|int
name|abortMultipartUploadsUnderPath
parameter_list|(
name|String
name|prefix
parameter_list|)
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Aborting multipart uploads under {}"
argument_list|,
name|prefix
argument_list|)
expr_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
name|List
argument_list|<
name|MultipartUpload
argument_list|>
name|multipartUploads
init|=
name|owner
operator|.
name|listMultipartUploads
argument_list|(
name|prefix
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Number of outstanding uploads: {}"
argument_list|,
name|multipartUploads
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|MultipartUpload
name|upload
range|:
name|multipartUploads
control|)
block|{
try|try
block|{
name|abortMultipartUpload
argument_list|(
name|upload
argument_list|)
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Already aborted: {}"
argument_list|,
name|upload
operator|.
name|getKey
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|count
return|;
block|}
comment|/**    * Abort a multipart commit operation.    * @param destKey destination key of ongoing operation    * @param uploadId multipart operation Id    * @throws IOException on problems.    * @throws FileNotFoundException if the abort ID is unknown    */
annotation|@
name|Retries
operator|.
name|RetryTranslated
DECL|method|abortMultipartCommit (String destKey, String uploadId)
specifier|public
name|void
name|abortMultipartCommit
parameter_list|(
name|String
name|destKey
parameter_list|,
name|String
name|uploadId
parameter_list|)
throws|throws
name|IOException
block|{
name|abortMultipartUpload
argument_list|(
name|destKey
argument_list|,
name|uploadId
argument_list|,
name|invoker
operator|.
name|getRetryCallback
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create and initialize a part request of a multipart upload.    * Exactly one of: {@code uploadStream} or {@code sourceFile}    * must be specified.    * A subset of the file may be posted, by providing the starting point    * in {@code offset} and a length of block in {@code size} equal to    * or less than the remaining bytes.    * @param destKey destination key of ongoing operation    * @param uploadId ID of ongoing upload    * @param partNumber current part number of the upload    * @param size amount of data    * @param uploadStream source of data to upload    * @param sourceFile optional source file.    * @param offset offset in file to start reading.    * @return the request.    */
DECL|method|newUploadPartRequest ( String destKey, String uploadId, int partNumber, int size, InputStream uploadStream, File sourceFile, Long offset)
specifier|public
name|UploadPartRequest
name|newUploadPartRequest
parameter_list|(
name|String
name|destKey
parameter_list|,
name|String
name|uploadId
parameter_list|,
name|int
name|partNumber
parameter_list|,
name|int
name|size
parameter_list|,
name|InputStream
name|uploadStream
parameter_list|,
name|File
name|sourceFile
parameter_list|,
name|Long
name|offset
parameter_list|)
block|{
name|checkNotNull
argument_list|(
name|uploadId
argument_list|)
expr_stmt|;
comment|// exactly one source must be set; xor verifies this
name|checkArgument
argument_list|(
operator|(
name|uploadStream
operator|!=
literal|null
operator|)
operator|^
operator|(
name|sourceFile
operator|!=
literal|null
operator|)
argument_list|,
literal|"Data source"
argument_list|)
expr_stmt|;
name|checkArgument
argument_list|(
name|size
operator|>=
literal|0
argument_list|,
literal|"Invalid partition size %s"
argument_list|,
name|size
argument_list|)
expr_stmt|;
name|checkArgument
argument_list|(
name|partNumber
operator|>
literal|0
operator|&&
name|partNumber
operator|<=
literal|10000
argument_list|,
literal|"partNumber must be between 1 and 10000 inclusive, but is %s"
argument_list|,
name|partNumber
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Creating part upload request for {} #{} size {}"
argument_list|,
name|uploadId
argument_list|,
name|partNumber
argument_list|,
name|size
argument_list|)
expr_stmt|;
name|UploadPartRequest
name|request
init|=
operator|new
name|UploadPartRequest
argument_list|()
operator|.
name|withBucketName
argument_list|(
name|owner
operator|.
name|getBucket
argument_list|()
argument_list|)
operator|.
name|withKey
argument_list|(
name|destKey
argument_list|)
operator|.
name|withUploadId
argument_list|(
name|uploadId
argument_list|)
operator|.
name|withPartNumber
argument_list|(
name|partNumber
argument_list|)
operator|.
name|withPartSize
argument_list|(
name|size
argument_list|)
decl_stmt|;
if|if
condition|(
name|uploadStream
operator|!=
literal|null
condition|)
block|{
comment|// there's an upload stream. Bind to it.
name|request
operator|.
name|setInputStream
argument_list|(
name|uploadStream
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|checkArgument
argument_list|(
name|sourceFile
operator|.
name|exists
argument_list|()
argument_list|,
literal|"Source file does not exist: %s"
argument_list|,
name|sourceFile
argument_list|)
expr_stmt|;
name|checkArgument
argument_list|(
name|offset
operator|>=
literal|0
argument_list|,
literal|"Invalid offset %s"
argument_list|,
name|offset
argument_list|)
expr_stmt|;
name|long
name|length
init|=
name|sourceFile
operator|.
name|length
argument_list|()
decl_stmt|;
name|checkArgument
argument_list|(
name|offset
operator|==
literal|0
operator|||
name|offset
operator|<
name|length
argument_list|,
literal|"Offset %s beyond length of file %s"
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|request
operator|.
name|setFile
argument_list|(
name|sourceFile
argument_list|)
expr_stmt|;
name|request
operator|.
name|setFileOffset
argument_list|(
name|offset
argument_list|)
expr_stmt|;
block|}
return|return
name|request
return|;
block|}
comment|/**    * The toString method is intended to be used in logging/toString calls.    * @return a string description.    */
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
specifier|final
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"WriteOperationHelper {bucket="
argument_list|)
operator|.
name|append
argument_list|(
name|owner
operator|.
name|getBucket
argument_list|()
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|'}'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * PUT an object directly (i.e. not via the transfer manager).    * Byte length is calculated from the file length, or, if there is no    * file, from the content length of the header.    * @param putObjectRequest the request    * @return the upload initiated    * @throws IOException on problems    */
annotation|@
name|Retries
operator|.
name|RetryTranslated
DECL|method|putObject (PutObjectRequest putObjectRequest)
specifier|public
name|PutObjectResult
name|putObject
parameter_list|(
name|PutObjectRequest
name|putObjectRequest
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|retry
argument_list|(
literal|"put"
argument_list|,
name|putObjectRequest
operator|.
name|getKey
argument_list|()
argument_list|,
literal|true
argument_list|,
parameter_list|()
lambda|->
name|owner
operator|.
name|putObjectDirect
argument_list|(
name|putObjectRequest
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * PUT an object via the transfer manager.    * @param putObjectRequest the request    * @return the result of the operation    * @throws IOException on problems    */
annotation|@
name|Retries
operator|.
name|OnceTranslated
DECL|method|uploadObject (PutObjectRequest putObjectRequest)
specifier|public
name|UploadResult
name|uploadObject
parameter_list|(
name|PutObjectRequest
name|putObjectRequest
parameter_list|)
throws|throws
name|IOException
block|{
comment|// no retry; rely on xfer manager logic
return|return
name|retry
argument_list|(
literal|"put"
argument_list|,
name|putObjectRequest
operator|.
name|getKey
argument_list|()
argument_list|,
literal|true
argument_list|,
parameter_list|()
lambda|->
name|owner
operator|.
name|executePut
argument_list|(
name|putObjectRequest
argument_list|,
literal|null
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Revert a commit by deleting the file.    * Relies on retry code in filesystem    * @throws IOException on problems    * @param destKey destination key    */
annotation|@
name|Retries
operator|.
name|RetryTranslated
DECL|method|revertCommit (String destKey)
specifier|public
name|void
name|revertCommit
parameter_list|(
name|String
name|destKey
parameter_list|)
throws|throws
name|IOException
block|{
name|once
argument_list|(
literal|"revert commit"
argument_list|,
name|destKey
argument_list|,
parameter_list|()
lambda|->
block|{
name|Path
name|destPath
init|=
name|owner
operator|.
name|keyToQualifiedPath
argument_list|(
name|destKey
argument_list|)
decl_stmt|;
name|owner
operator|.
name|deleteObjectAtPath
argument_list|(
name|destPath
argument_list|,
name|destKey
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|owner
operator|.
name|maybeCreateFakeParentDirectory
argument_list|(
name|destPath
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Upload part of a multi-partition file.    * @param request request    * @return the result of the operation.    * @throws IOException on problems    */
annotation|@
name|Retries
operator|.
name|RetryTranslated
DECL|method|uploadPart (UploadPartRequest request)
specifier|public
name|UploadPartResult
name|uploadPart
parameter_list|(
name|UploadPartRequest
name|request
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|retry
argument_list|(
literal|"upload part"
argument_list|,
name|request
operator|.
name|getKey
argument_list|()
argument_list|,
literal|true
argument_list|,
parameter_list|()
lambda|->
name|owner
operator|.
name|uploadPart
argument_list|(
name|request
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

