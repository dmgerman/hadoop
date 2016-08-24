begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.aliyun.oss
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|aliyun
operator|.
name|oss
package|;
end_package

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
name|aliyun
operator|.
name|oss
operator|.
name|Constants
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedOutputStream
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
name|FileInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
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
name|com
operator|.
name|aliyun
operator|.
name|oss
operator|.
name|ClientException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|aliyun
operator|.
name|oss
operator|.
name|OSSException
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
name|logging
operator|.
name|Log
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
name|logging
operator|.
name|LogFactory
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
name|fs
operator|.
name|FileSystem
operator|.
name|Statistics
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
name|LocalDirAllocator
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
name|com
operator|.
name|aliyun
operator|.
name|oss
operator|.
name|OSSClient
import|;
end_import

begin_import
import|import
name|com
operator|.
name|aliyun
operator|.
name|oss
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
name|aliyun
operator|.
name|oss
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
name|aliyun
operator|.
name|oss
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
name|aliyun
operator|.
name|oss
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
name|aliyun
operator|.
name|oss
operator|.
name|model
operator|.
name|InitiateMultipartUploadResult
import|;
end_import

begin_import
import|import
name|com
operator|.
name|aliyun
operator|.
name|oss
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
name|aliyun
operator|.
name|oss
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
name|aliyun
operator|.
name|oss
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
name|aliyun
operator|.
name|oss
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
name|aliyun
operator|.
name|oss
operator|.
name|model
operator|.
name|UploadPartResult
import|;
end_import

begin_comment
comment|/**  * The output stream for OSS blob system.  * Data will be buffered on local disk, then uploaded to OSS in  * {@link #close()} method.  */
end_comment

begin_class
DECL|class|AliyunOSSOutputStream
specifier|public
class|class
name|AliyunOSSOutputStream
extends|extends
name|OutputStream
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|AliyunOSSOutputStream
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|bucketName
specifier|private
name|String
name|bucketName
decl_stmt|;
DECL|field|key
specifier|private
name|String
name|key
decl_stmt|;
DECL|field|statistics
specifier|private
name|Statistics
name|statistics
decl_stmt|;
DECL|field|progress
specifier|private
name|Progressable
name|progress
decl_stmt|;
DECL|field|serverSideEncryptionAlgorithm
specifier|private
name|String
name|serverSideEncryptionAlgorithm
decl_stmt|;
DECL|field|partSize
specifier|private
name|long
name|partSize
decl_stmt|;
DECL|field|partSizeThreshold
specifier|private
name|long
name|partSizeThreshold
decl_stmt|;
DECL|field|dirAlloc
specifier|private
name|LocalDirAllocator
name|dirAlloc
decl_stmt|;
DECL|field|closed
specifier|private
name|boolean
name|closed
decl_stmt|;
DECL|field|tmpFile
specifier|private
name|File
name|tmpFile
decl_stmt|;
DECL|field|backupStream
specifier|private
name|BufferedOutputStream
name|backupStream
decl_stmt|;
DECL|field|ossClient
specifier|private
name|OSSClient
name|ossClient
decl_stmt|;
DECL|method|AliyunOSSOutputStream (Configuration conf, OSSClient client, String bucketName, String key, Progressable progress, Statistics statistics, String serverSideEncryptionAlgorithm)
specifier|public
name|AliyunOSSOutputStream
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|OSSClient
name|client
parameter_list|,
name|String
name|bucketName
parameter_list|,
name|String
name|key
parameter_list|,
name|Progressable
name|progress
parameter_list|,
name|Statistics
name|statistics
parameter_list|,
name|String
name|serverSideEncryptionAlgorithm
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|bucketName
operator|=
name|bucketName
expr_stmt|;
name|this
operator|.
name|key
operator|=
name|key
expr_stmt|;
comment|// The caller cann't get any progress information
name|this
operator|.
name|progress
operator|=
name|progress
expr_stmt|;
name|ossClient
operator|=
name|client
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
name|partSize
operator|=
name|conf
operator|.
name|getLong
argument_list|(
name|MULTIPART_UPLOAD_SIZE_KEY
argument_list|,
name|MULTIPART_UPLOAD_SIZE_DEFAULT
argument_list|)
expr_stmt|;
if|if
condition|(
name|partSize
operator|<
name|MIN_MULTIPART_UPLOAD_PART_SIZE
condition|)
block|{
name|partSize
operator|=
name|MIN_MULTIPART_UPLOAD_PART_SIZE
expr_stmt|;
block|}
name|partSizeThreshold
operator|=
name|conf
operator|.
name|getLong
argument_list|(
name|MIN_MULTIPART_UPLOAD_THRESHOLD_KEY
argument_list|,
name|MIN_MULTIPART_UPLOAD_THRESHOLD_DEFAULT
argument_list|)
expr_stmt|;
if|if
condition|(
name|conf
operator|.
name|get
argument_list|(
name|BUFFER_DIR_KEY
argument_list|)
operator|==
literal|null
condition|)
block|{
name|conf
operator|.
name|set
argument_list|(
name|BUFFER_DIR_KEY
argument_list|,
name|conf
operator|.
name|get
argument_list|(
literal|"hadoop.tmp.dir"
argument_list|)
operator|+
literal|"/oss"
argument_list|)
expr_stmt|;
block|}
name|dirAlloc
operator|=
operator|new
name|LocalDirAllocator
argument_list|(
name|BUFFER_DIR_KEY
argument_list|)
expr_stmt|;
name|tmpFile
operator|=
name|dirAlloc
operator|.
name|createTmpFileForWrite
argument_list|(
literal|"output-"
argument_list|,
name|LocalDirAllocator
operator|.
name|SIZE_UNKNOWN
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|backupStream
operator|=
operator|new
name|BufferedOutputStream
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
name|tmpFile
argument_list|)
argument_list|)
expr_stmt|;
name|closed
operator|=
literal|false
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
name|closed
condition|)
block|{
return|return;
block|}
name|closed
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|backupStream
operator|!=
literal|null
condition|)
block|{
name|backupStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|long
name|dataLen
init|=
name|tmpFile
operator|.
name|length
argument_list|()
decl_stmt|;
try|try
block|{
if|if
condition|(
name|dataLen
operator|<=
name|partSizeThreshold
condition|)
block|{
name|uploadObject
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|multipartUploadObject
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|tmpFile
operator|.
name|delete
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Can not delete file: "
operator|+
name|tmpFile
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Upload temporary file as an OSS object, using single upload.    *    * @throws IOException    */
DECL|method|uploadObject ()
specifier|private
name|void
name|uploadObject
parameter_list|()
throws|throws
name|IOException
block|{
name|File
name|object
init|=
name|tmpFile
operator|.
name|getAbsoluteFile
argument_list|()
decl_stmt|;
name|FileInputStream
name|fis
init|=
operator|new
name|FileInputStream
argument_list|(
name|object
argument_list|)
decl_stmt|;
name|ObjectMetadata
name|meta
init|=
operator|new
name|ObjectMetadata
argument_list|()
decl_stmt|;
name|meta
operator|.
name|setContentLength
argument_list|(
name|object
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|serverSideEncryptionAlgorithm
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|meta
operator|.
name|setServerSideEncryption
argument_list|(
name|serverSideEncryptionAlgorithm
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|PutObjectResult
name|result
init|=
name|ossClient
operator|.
name|putObject
argument_list|(
name|bucketName
argument_list|,
name|key
argument_list|,
name|fis
argument_list|,
name|meta
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
name|result
operator|.
name|getETag
argument_list|()
argument_list|)
expr_stmt|;
name|statistics
operator|.
name|incrementWriteOps
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|fis
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Upload temporary file as an OSS object, using multipart upload.    *    * @throws IOException    */
DECL|method|multipartUploadObject ()
specifier|private
name|void
name|multipartUploadObject
parameter_list|()
throws|throws
name|IOException
block|{
name|File
name|object
init|=
name|tmpFile
operator|.
name|getAbsoluteFile
argument_list|()
decl_stmt|;
name|long
name|dataLen
init|=
name|object
operator|.
name|length
argument_list|()
decl_stmt|;
name|long
name|realPartSize
init|=
name|AliyunOSSUtils
operator|.
name|calculatePartSize
argument_list|(
name|dataLen
argument_list|,
name|partSize
argument_list|)
decl_stmt|;
name|int
name|partNum
init|=
call|(
name|int
call|)
argument_list|(
name|dataLen
operator|/
name|realPartSize
argument_list|)
decl_stmt|;
if|if
condition|(
name|dataLen
operator|%
name|realPartSize
operator|!=
literal|0
condition|)
block|{
name|partNum
operator|+=
literal|1
expr_stmt|;
block|}
name|InitiateMultipartUploadRequest
name|initiateMultipartUploadRequest
init|=
operator|new
name|InitiateMultipartUploadRequest
argument_list|(
name|bucketName
argument_list|,
name|key
argument_list|)
decl_stmt|;
name|ObjectMetadata
name|meta
init|=
operator|new
name|ObjectMetadata
argument_list|()
decl_stmt|;
comment|//    meta.setContentLength(dataLen);
if|if
condition|(
operator|!
name|serverSideEncryptionAlgorithm
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|meta
operator|.
name|setServerSideEncryption
argument_list|(
name|serverSideEncryptionAlgorithm
argument_list|)
expr_stmt|;
block|}
name|initiateMultipartUploadRequest
operator|.
name|setObjectMetadata
argument_list|(
name|meta
argument_list|)
expr_stmt|;
name|InitiateMultipartUploadResult
name|initiateMultipartUploadResult
init|=
name|ossClient
operator|.
name|initiateMultipartUpload
argument_list|(
name|initiateMultipartUploadRequest
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|PartETag
argument_list|>
name|partETags
init|=
operator|new
name|ArrayList
argument_list|<
name|PartETag
argument_list|>
argument_list|()
decl_stmt|;
name|String
name|uploadId
init|=
name|initiateMultipartUploadResult
operator|.
name|getUploadId
argument_list|()
decl_stmt|;
try|try
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|partNum
condition|;
name|i
operator|++
control|)
block|{
comment|// TODO: Optimize this, avoid opening the object multiple times
name|FileInputStream
name|fis
init|=
operator|new
name|FileInputStream
argument_list|(
name|object
argument_list|)
decl_stmt|;
try|try
block|{
name|long
name|skipBytes
init|=
name|realPartSize
operator|*
name|i
decl_stmt|;
name|AliyunOSSUtils
operator|.
name|skipFully
argument_list|(
name|fis
argument_list|,
name|skipBytes
argument_list|)
expr_stmt|;
name|long
name|size
init|=
operator|(
name|realPartSize
operator|<
name|dataLen
operator|-
name|skipBytes
operator|)
condition|?
name|realPartSize
else|:
name|dataLen
operator|-
name|skipBytes
decl_stmt|;
name|UploadPartRequest
name|uploadPartRequest
init|=
operator|new
name|UploadPartRequest
argument_list|()
decl_stmt|;
name|uploadPartRequest
operator|.
name|setBucketName
argument_list|(
name|bucketName
argument_list|)
expr_stmt|;
name|uploadPartRequest
operator|.
name|setKey
argument_list|(
name|key
argument_list|)
expr_stmt|;
name|uploadPartRequest
operator|.
name|setUploadId
argument_list|(
name|uploadId
argument_list|)
expr_stmt|;
name|uploadPartRequest
operator|.
name|setInputStream
argument_list|(
name|fis
argument_list|)
expr_stmt|;
name|uploadPartRequest
operator|.
name|setPartSize
argument_list|(
name|size
argument_list|)
expr_stmt|;
name|uploadPartRequest
operator|.
name|setPartNumber
argument_list|(
name|i
operator|+
literal|1
argument_list|)
expr_stmt|;
name|UploadPartResult
name|uploadPartResult
init|=
name|ossClient
operator|.
name|uploadPart
argument_list|(
name|uploadPartRequest
argument_list|)
decl_stmt|;
name|statistics
operator|.
name|incrementWriteOps
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|partETags
operator|.
name|add
argument_list|(
name|uploadPartResult
operator|.
name|getPartETag
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|fis
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
name|CompleteMultipartUploadRequest
name|completeMultipartUploadRequest
init|=
operator|new
name|CompleteMultipartUploadRequest
argument_list|(
name|bucketName
argument_list|,
name|key
argument_list|,
name|uploadId
argument_list|,
name|partETags
argument_list|)
decl_stmt|;
name|CompleteMultipartUploadResult
name|completeMultipartUploadResult
init|=
name|ossClient
operator|.
name|completeMultipartUpload
argument_list|(
name|completeMultipartUploadRequest
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
name|completeMultipartUploadResult
operator|.
name|getETag
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|OSSException
decl||
name|ClientException
name|e
parameter_list|)
block|{
name|AbortMultipartUploadRequest
name|abortMultipartUploadRequest
init|=
operator|new
name|AbortMultipartUploadRequest
argument_list|(
name|bucketName
argument_list|,
name|key
argument_list|,
name|uploadId
argument_list|)
decl_stmt|;
name|ossClient
operator|.
name|abortMultipartUpload
argument_list|(
name|abortMultipartUploadRequest
argument_list|)
expr_stmt|;
block|}
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
name|backupStream
operator|.
name|flush
argument_list|()
expr_stmt|;
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
name|backupStream
operator|.
name|write
argument_list|(
name|b
argument_list|)
expr_stmt|;
name|statistics
operator|.
name|incrementBytesWritten
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

