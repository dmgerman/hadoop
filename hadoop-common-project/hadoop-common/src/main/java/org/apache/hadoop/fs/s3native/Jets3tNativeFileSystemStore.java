begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3native
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|s3native
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
name|s3native
operator|.
name|NativeS3FileSystem
operator|.
name|PATH_DELIMITER
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedInputStream
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
name|net
operator|.
name|URI
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
name|Collections
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
name|s3
operator|.
name|S3Credentials
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
name|s3
operator|.
name|S3Exception
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jets3t
operator|.
name|service
operator|.
name|S3Service
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jets3t
operator|.
name|service
operator|.
name|S3ServiceException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jets3t
operator|.
name|service
operator|.
name|ServiceException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jets3t
operator|.
name|service
operator|.
name|StorageObjectsChunk
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jets3t
operator|.
name|service
operator|.
name|impl
operator|.
name|rest
operator|.
name|httpclient
operator|.
name|RestS3Service
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jets3t
operator|.
name|service
operator|.
name|model
operator|.
name|MultipartPart
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jets3t
operator|.
name|service
operator|.
name|model
operator|.
name|MultipartUpload
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jets3t
operator|.
name|service
operator|.
name|model
operator|.
name|S3Bucket
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jets3t
operator|.
name|service
operator|.
name|model
operator|.
name|S3Object
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jets3t
operator|.
name|service
operator|.
name|model
operator|.
name|StorageObject
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jets3t
operator|.
name|service
operator|.
name|security
operator|.
name|AWSCredentials
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jets3t
operator|.
name|service
operator|.
name|utils
operator|.
name|MultipartUtils
import|;
end_import

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|Jets3tNativeFileSystemStore
class|class
name|Jets3tNativeFileSystemStore
implements|implements
name|NativeFileSystemStore
block|{
DECL|field|s3Service
specifier|private
name|S3Service
name|s3Service
decl_stmt|;
DECL|field|bucket
specifier|private
name|S3Bucket
name|bucket
decl_stmt|;
DECL|field|multipartBlockSize
specifier|private
name|long
name|multipartBlockSize
decl_stmt|;
DECL|field|multipartEnabled
specifier|private
name|boolean
name|multipartEnabled
decl_stmt|;
DECL|field|multipartCopyBlockSize
specifier|private
name|long
name|multipartCopyBlockSize
decl_stmt|;
DECL|field|MAX_PART_SIZE
specifier|static
specifier|final
name|long
name|MAX_PART_SIZE
init|=
operator|(
name|long
operator|)
literal|5
operator|*
literal|1024
operator|*
literal|1024
operator|*
literal|1024
decl_stmt|;
DECL|field|serverSideEncryptionAlgorithm
specifier|private
name|String
name|serverSideEncryptionAlgorithm
decl_stmt|;
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
name|Jets3tNativeFileSystemStore
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Override
DECL|method|initialize (URI uri, Configuration conf)
specifier|public
name|void
name|initialize
parameter_list|(
name|URI
name|uri
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|S3Credentials
name|s3Credentials
init|=
operator|new
name|S3Credentials
argument_list|()
decl_stmt|;
name|s3Credentials
operator|.
name|initialize
argument_list|(
name|uri
argument_list|,
name|conf
argument_list|)
expr_stmt|;
try|try
block|{
name|AWSCredentials
name|awsCredentials
init|=
operator|new
name|AWSCredentials
argument_list|(
name|s3Credentials
operator|.
name|getAccessKey
argument_list|()
argument_list|,
name|s3Credentials
operator|.
name|getSecretAccessKey
argument_list|()
argument_list|)
decl_stmt|;
name|this
operator|.
name|s3Service
operator|=
operator|new
name|RestS3Service
argument_list|(
name|awsCredentials
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|S3ServiceException
name|e
parameter_list|)
block|{
name|handleS3ServiceException
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
name|multipartEnabled
operator|=
name|conf
operator|.
name|getBoolean
argument_list|(
literal|"fs.s3n.multipart.uploads.enabled"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|multipartBlockSize
operator|=
name|Math
operator|.
name|min
argument_list|(
name|conf
operator|.
name|getLong
argument_list|(
literal|"fs.s3n.multipart.uploads.block.size"
argument_list|,
literal|64
operator|*
literal|1024
operator|*
literal|1024
argument_list|)
argument_list|,
name|MAX_PART_SIZE
argument_list|)
expr_stmt|;
name|multipartCopyBlockSize
operator|=
name|Math
operator|.
name|min
argument_list|(
name|conf
operator|.
name|getLong
argument_list|(
literal|"fs.s3n.multipart.copy.block.size"
argument_list|,
name|MAX_PART_SIZE
argument_list|)
argument_list|,
name|MAX_PART_SIZE
argument_list|)
expr_stmt|;
name|serverSideEncryptionAlgorithm
operator|=
name|conf
operator|.
name|get
argument_list|(
literal|"fs.s3n.server-side-encryption-algorithm"
argument_list|)
expr_stmt|;
name|bucket
operator|=
operator|new
name|S3Bucket
argument_list|(
name|uri
operator|.
name|getHost
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|storeFile (String key, File file, byte[] md5Hash)
specifier|public
name|void
name|storeFile
parameter_list|(
name|String
name|key
parameter_list|,
name|File
name|file
parameter_list|,
name|byte
index|[]
name|md5Hash
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|multipartEnabled
operator|&&
name|file
operator|.
name|length
argument_list|()
operator|>=
name|multipartBlockSize
condition|)
block|{
name|storeLargeFile
argument_list|(
name|key
argument_list|,
name|file
argument_list|,
name|md5Hash
argument_list|)
expr_stmt|;
return|return;
block|}
name|BufferedInputStream
name|in
init|=
literal|null
decl_stmt|;
try|try
block|{
name|in
operator|=
operator|new
name|BufferedInputStream
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|file
argument_list|)
argument_list|)
expr_stmt|;
name|S3Object
name|object
init|=
operator|new
name|S3Object
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|object
operator|.
name|setDataInputStream
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|object
operator|.
name|setContentType
argument_list|(
literal|"binary/octet-stream"
argument_list|)
expr_stmt|;
name|object
operator|.
name|setContentLength
argument_list|(
name|file
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|object
operator|.
name|setServerSideEncryptionAlgorithm
argument_list|(
name|serverSideEncryptionAlgorithm
argument_list|)
expr_stmt|;
if|if
condition|(
name|md5Hash
operator|!=
literal|null
condition|)
block|{
name|object
operator|.
name|setMd5Hash
argument_list|(
name|md5Hash
argument_list|)
expr_stmt|;
block|}
name|s3Service
operator|.
name|putObject
argument_list|(
name|bucket
argument_list|,
name|object
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|S3ServiceException
name|e
parameter_list|)
block|{
name|handleS3ServiceException
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|in
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
block|}
block|}
block|}
DECL|method|storeLargeFile (String key, File file, byte[] md5Hash)
specifier|public
name|void
name|storeLargeFile
parameter_list|(
name|String
name|key
parameter_list|,
name|File
name|file
parameter_list|,
name|byte
index|[]
name|md5Hash
parameter_list|)
throws|throws
name|IOException
block|{
name|S3Object
name|object
init|=
operator|new
name|S3Object
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|object
operator|.
name|setDataInputFile
argument_list|(
name|file
argument_list|)
expr_stmt|;
name|object
operator|.
name|setContentType
argument_list|(
literal|"binary/octet-stream"
argument_list|)
expr_stmt|;
name|object
operator|.
name|setContentLength
argument_list|(
name|file
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|object
operator|.
name|setServerSideEncryptionAlgorithm
argument_list|(
name|serverSideEncryptionAlgorithm
argument_list|)
expr_stmt|;
if|if
condition|(
name|md5Hash
operator|!=
literal|null
condition|)
block|{
name|object
operator|.
name|setMd5Hash
argument_list|(
name|md5Hash
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|StorageObject
argument_list|>
name|objectsToUploadAsMultipart
init|=
operator|new
name|ArrayList
argument_list|<
name|StorageObject
argument_list|>
argument_list|()
decl_stmt|;
name|objectsToUploadAsMultipart
operator|.
name|add
argument_list|(
name|object
argument_list|)
expr_stmt|;
name|MultipartUtils
name|mpUtils
init|=
operator|new
name|MultipartUtils
argument_list|(
name|multipartBlockSize
argument_list|)
decl_stmt|;
try|try
block|{
name|mpUtils
operator|.
name|uploadObjects
argument_list|(
name|bucket
operator|.
name|getName
argument_list|()
argument_list|,
name|s3Service
argument_list|,
name|objectsToUploadAsMultipart
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ServiceException
name|e
parameter_list|)
block|{
name|handleServiceException
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|S3Exception
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|storeEmptyFile (String key)
specifier|public
name|void
name|storeEmptyFile
parameter_list|(
name|String
name|key
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|S3Object
name|object
init|=
operator|new
name|S3Object
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|object
operator|.
name|setDataInputStream
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
operator|new
name|byte
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|object
operator|.
name|setContentType
argument_list|(
literal|"binary/octet-stream"
argument_list|)
expr_stmt|;
name|object
operator|.
name|setContentLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|object
operator|.
name|setServerSideEncryptionAlgorithm
argument_list|(
name|serverSideEncryptionAlgorithm
argument_list|)
expr_stmt|;
name|s3Service
operator|.
name|putObject
argument_list|(
name|bucket
argument_list|,
name|object
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|S3ServiceException
name|e
parameter_list|)
block|{
name|handleS3ServiceException
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|retrieveMetadata (String key)
specifier|public
name|FileMetadata
name|retrieveMetadata
parameter_list|(
name|String
name|key
parameter_list|)
throws|throws
name|IOException
block|{
name|StorageObject
name|object
init|=
literal|null
decl_stmt|;
try|try
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
literal|"Getting metadata for key: "
operator|+
name|key
operator|+
literal|" from bucket:"
operator|+
name|bucket
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|object
operator|=
name|s3Service
operator|.
name|getObjectDetails
argument_list|(
name|bucket
operator|.
name|getName
argument_list|()
argument_list|,
name|key
argument_list|)
expr_stmt|;
return|return
operator|new
name|FileMetadata
argument_list|(
name|key
argument_list|,
name|object
operator|.
name|getContentLength
argument_list|()
argument_list|,
name|object
operator|.
name|getLastModifiedDate
argument_list|()
operator|.
name|getTime
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ServiceException
name|e
parameter_list|)
block|{
comment|// Following is brittle. Is there a better way?
if|if
condition|(
literal|"NoSuchKey"
operator|.
name|equals
argument_list|(
name|e
operator|.
name|getErrorCode
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
comment|//return null if key not found
block|}
name|handleServiceException
argument_list|(
name|e
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
comment|//never returned - keep compiler happy
block|}
finally|finally
block|{
if|if
condition|(
name|object
operator|!=
literal|null
condition|)
block|{
name|object
operator|.
name|closeDataInputStream
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**    * @param key    * The key is the object name that is being retrieved from the S3 bucket    * @return    * This method returns null if the key is not found    * @throws IOException    */
annotation|@
name|Override
DECL|method|retrieve (String key)
specifier|public
name|InputStream
name|retrieve
parameter_list|(
name|String
name|key
parameter_list|)
throws|throws
name|IOException
block|{
try|try
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
literal|"Getting key: "
operator|+
name|key
operator|+
literal|" from bucket:"
operator|+
name|bucket
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|S3Object
name|object
init|=
name|s3Service
operator|.
name|getObject
argument_list|(
name|bucket
operator|.
name|getName
argument_list|()
argument_list|,
name|key
argument_list|)
decl_stmt|;
return|return
name|object
operator|.
name|getDataInputStream
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|ServiceException
name|e
parameter_list|)
block|{
name|handleServiceException
argument_list|(
name|key
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
comment|//return null if key not found
block|}
block|}
comment|/**    *    * @param key    * The key is the object name that is being retrieved from the S3 bucket    * @return    * This method returns null if the key is not found    * @throws IOException    */
annotation|@
name|Override
DECL|method|retrieve (String key, long byteRangeStart)
specifier|public
name|InputStream
name|retrieve
parameter_list|(
name|String
name|key
parameter_list|,
name|long
name|byteRangeStart
parameter_list|)
throws|throws
name|IOException
block|{
try|try
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
literal|"Getting key: "
operator|+
name|key
operator|+
literal|" from bucket:"
operator|+
name|bucket
operator|.
name|getName
argument_list|()
operator|+
literal|" with byteRangeStart: "
operator|+
name|byteRangeStart
argument_list|)
expr_stmt|;
block|}
name|S3Object
name|object
init|=
name|s3Service
operator|.
name|getObject
argument_list|(
name|bucket
argument_list|,
name|key
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|byteRangeStart
argument_list|,
literal|null
argument_list|)
decl_stmt|;
return|return
name|object
operator|.
name|getDataInputStream
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|ServiceException
name|e
parameter_list|)
block|{
name|handleServiceException
argument_list|(
name|key
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
comment|//return null if key not found
block|}
block|}
annotation|@
name|Override
DECL|method|list (String prefix, int maxListingLength)
specifier|public
name|PartialListing
name|list
parameter_list|(
name|String
name|prefix
parameter_list|,
name|int
name|maxListingLength
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|list
argument_list|(
name|prefix
argument_list|,
name|maxListingLength
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|list (String prefix, int maxListingLength, String priorLastKey, boolean recurse)
specifier|public
name|PartialListing
name|list
parameter_list|(
name|String
name|prefix
parameter_list|,
name|int
name|maxListingLength
parameter_list|,
name|String
name|priorLastKey
parameter_list|,
name|boolean
name|recurse
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|list
argument_list|(
name|prefix
argument_list|,
name|recurse
condition|?
literal|null
else|:
name|PATH_DELIMITER
argument_list|,
name|maxListingLength
argument_list|,
name|priorLastKey
argument_list|)
return|;
block|}
comment|/**    *    * @return    * This method returns null if the list could not be populated    * due to S3 giving ServiceException    * @throws IOException    */
DECL|method|list (String prefix, String delimiter, int maxListingLength, String priorLastKey)
specifier|private
name|PartialListing
name|list
parameter_list|(
name|String
name|prefix
parameter_list|,
name|String
name|delimiter
parameter_list|,
name|int
name|maxListingLength
parameter_list|,
name|String
name|priorLastKey
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
if|if
condition|(
name|prefix
operator|.
name|length
argument_list|()
operator|>
literal|0
operator|&&
operator|!
name|prefix
operator|.
name|endsWith
argument_list|(
name|PATH_DELIMITER
argument_list|)
condition|)
block|{
name|prefix
operator|+=
name|PATH_DELIMITER
expr_stmt|;
block|}
name|StorageObjectsChunk
name|chunk
init|=
name|s3Service
operator|.
name|listObjectsChunked
argument_list|(
name|bucket
operator|.
name|getName
argument_list|()
argument_list|,
name|prefix
argument_list|,
name|delimiter
argument_list|,
name|maxListingLength
argument_list|,
name|priorLastKey
argument_list|)
decl_stmt|;
name|FileMetadata
index|[]
name|fileMetadata
init|=
operator|new
name|FileMetadata
index|[
name|chunk
operator|.
name|getObjects
argument_list|()
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|fileMetadata
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|StorageObject
name|object
init|=
name|chunk
operator|.
name|getObjects
argument_list|()
index|[
name|i
index|]
decl_stmt|;
name|fileMetadata
index|[
name|i
index|]
operator|=
operator|new
name|FileMetadata
argument_list|(
name|object
operator|.
name|getKey
argument_list|()
argument_list|,
name|object
operator|.
name|getContentLength
argument_list|()
argument_list|,
name|object
operator|.
name|getLastModifiedDate
argument_list|()
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|PartialListing
argument_list|(
name|chunk
operator|.
name|getPriorLastKey
argument_list|()
argument_list|,
name|fileMetadata
argument_list|,
name|chunk
operator|.
name|getCommonPrefixes
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|S3ServiceException
name|e
parameter_list|)
block|{
name|handleS3ServiceException
argument_list|(
name|e
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
comment|//never returned - keep compiler happy
block|}
catch|catch
parameter_list|(
name|ServiceException
name|e
parameter_list|)
block|{
name|handleServiceException
argument_list|(
name|e
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
comment|//return null if list could not be populated
block|}
block|}
annotation|@
name|Override
DECL|method|delete (String key)
specifier|public
name|void
name|delete
parameter_list|(
name|String
name|key
parameter_list|)
throws|throws
name|IOException
block|{
try|try
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
literal|"Deleting key:"
operator|+
name|key
operator|+
literal|"from bucket"
operator|+
name|bucket
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|s3Service
operator|.
name|deleteObject
argument_list|(
name|bucket
argument_list|,
name|key
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ServiceException
name|e
parameter_list|)
block|{
name|handleServiceException
argument_list|(
name|key
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|rename (String srcKey, String dstKey)
specifier|public
name|void
name|rename
parameter_list|(
name|String
name|srcKey
parameter_list|,
name|String
name|dstKey
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|s3Service
operator|.
name|renameObject
argument_list|(
name|bucket
operator|.
name|getName
argument_list|()
argument_list|,
name|srcKey
argument_list|,
operator|new
name|S3Object
argument_list|(
name|dstKey
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ServiceException
name|e
parameter_list|)
block|{
name|handleServiceException
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|copy (String srcKey, String dstKey)
specifier|public
name|void
name|copy
parameter_list|(
name|String
name|srcKey
parameter_list|,
name|String
name|dstKey
parameter_list|)
throws|throws
name|IOException
block|{
try|try
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
literal|"Copying srcKey: "
operator|+
name|srcKey
operator|+
literal|"to dstKey: "
operator|+
name|dstKey
operator|+
literal|"in bucket: "
operator|+
name|bucket
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|multipartEnabled
condition|)
block|{
name|S3Object
name|object
init|=
name|s3Service
operator|.
name|getObjectDetails
argument_list|(
name|bucket
argument_list|,
name|srcKey
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|multipartCopyBlockSize
operator|>
literal|0
operator|&&
name|object
operator|.
name|getContentLength
argument_list|()
operator|>
name|multipartCopyBlockSize
condition|)
block|{
name|copyLargeFile
argument_list|(
name|object
argument_list|,
name|dstKey
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
name|S3Object
name|dstObject
init|=
operator|new
name|S3Object
argument_list|(
name|dstKey
argument_list|)
decl_stmt|;
name|dstObject
operator|.
name|setServerSideEncryptionAlgorithm
argument_list|(
name|serverSideEncryptionAlgorithm
argument_list|)
expr_stmt|;
name|s3Service
operator|.
name|copyObject
argument_list|(
name|bucket
operator|.
name|getName
argument_list|()
argument_list|,
name|srcKey
argument_list|,
name|bucket
operator|.
name|getName
argument_list|()
argument_list|,
name|dstObject
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ServiceException
name|e
parameter_list|)
block|{
name|handleServiceException
argument_list|(
name|srcKey
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|copyLargeFile (S3Object srcObject, String dstKey)
specifier|public
name|void
name|copyLargeFile
parameter_list|(
name|S3Object
name|srcObject
parameter_list|,
name|String
name|dstKey
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|long
name|partCount
init|=
name|srcObject
operator|.
name|getContentLength
argument_list|()
operator|/
name|multipartCopyBlockSize
operator|+
operator|(
name|srcObject
operator|.
name|getContentLength
argument_list|()
operator|%
name|multipartCopyBlockSize
operator|>
literal|0
condition|?
literal|1
else|:
literal|0
operator|)
decl_stmt|;
name|MultipartUpload
name|multipartUpload
init|=
name|s3Service
operator|.
name|multipartStartUpload
argument_list|(
name|bucket
operator|.
name|getName
argument_list|()
argument_list|,
name|dstKey
argument_list|,
name|srcObject
operator|.
name|getMetadataMap
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|MultipartPart
argument_list|>
name|listedParts
init|=
operator|new
name|ArrayList
argument_list|<
name|MultipartPart
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|partCount
condition|;
name|i
operator|++
control|)
block|{
name|long
name|byteRangeStart
init|=
name|i
operator|*
name|multipartCopyBlockSize
decl_stmt|;
name|long
name|byteLength
decl_stmt|;
if|if
condition|(
name|i
operator|<
name|partCount
operator|-
literal|1
condition|)
block|{
name|byteLength
operator|=
name|multipartCopyBlockSize
expr_stmt|;
block|}
else|else
block|{
name|byteLength
operator|=
name|srcObject
operator|.
name|getContentLength
argument_list|()
operator|%
name|multipartCopyBlockSize
expr_stmt|;
if|if
condition|(
name|byteLength
operator|==
literal|0
condition|)
block|{
name|byteLength
operator|=
name|multipartCopyBlockSize
expr_stmt|;
block|}
block|}
name|MultipartPart
name|copiedPart
init|=
name|s3Service
operator|.
name|multipartUploadPartCopy
argument_list|(
name|multipartUpload
argument_list|,
name|i
operator|+
literal|1
argument_list|,
name|bucket
operator|.
name|getName
argument_list|()
argument_list|,
name|srcObject
operator|.
name|getKey
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|byteRangeStart
argument_list|,
name|byteRangeStart
operator|+
name|byteLength
operator|-
literal|1
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|listedParts
operator|.
name|add
argument_list|(
name|copiedPart
argument_list|)
expr_stmt|;
block|}
name|Collections
operator|.
name|reverse
argument_list|(
name|listedParts
argument_list|)
expr_stmt|;
name|s3Service
operator|.
name|multipartCompleteUpload
argument_list|(
name|multipartUpload
argument_list|,
name|listedParts
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ServiceException
name|e
parameter_list|)
block|{
name|handleServiceException
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|purge (String prefix)
specifier|public
name|void
name|purge
parameter_list|(
name|String
name|prefix
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|S3Object
index|[]
name|objects
init|=
name|s3Service
operator|.
name|listObjects
argument_list|(
name|bucket
operator|.
name|getName
argument_list|()
argument_list|,
name|prefix
argument_list|,
literal|null
argument_list|)
decl_stmt|;
for|for
control|(
name|S3Object
name|object
range|:
name|objects
control|)
block|{
name|s3Service
operator|.
name|deleteObject
argument_list|(
name|bucket
argument_list|,
name|object
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|S3ServiceException
name|e
parameter_list|)
block|{
name|handleS3ServiceException
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|dump ()
specifier|public
name|void
name|dump
parameter_list|()
throws|throws
name|IOException
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"S3 Native Filesystem, "
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|bucket
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
try|try
block|{
name|S3Object
index|[]
name|objects
init|=
name|s3Service
operator|.
name|listObjects
argument_list|(
name|bucket
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|S3Object
name|object
range|:
name|objects
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|object
operator|.
name|getKey
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|S3ServiceException
name|e
parameter_list|)
block|{
name|handleS3ServiceException
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|sb
argument_list|)
expr_stmt|;
block|}
DECL|method|handleServiceException (String key, ServiceException e)
specifier|private
name|void
name|handleServiceException
parameter_list|(
name|String
name|key
parameter_list|,
name|ServiceException
name|e
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
literal|"NoSuchKey"
operator|.
name|equals
argument_list|(
name|e
operator|.
name|getErrorCode
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
literal|"Key '"
operator|+
name|key
operator|+
literal|"' does not exist in S3"
argument_list|)
throw|;
block|}
else|else
block|{
name|handleServiceException
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|handleS3ServiceException (S3ServiceException e)
specifier|private
name|void
name|handleS3ServiceException
parameter_list|(
name|S3ServiceException
name|e
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|IOException
condition|)
block|{
throw|throw
operator|(
name|IOException
operator|)
name|e
operator|.
name|getCause
argument_list|()
throw|;
block|}
else|else
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
literal|"S3 Error code: "
operator|+
name|e
operator|.
name|getS3ErrorCode
argument_list|()
operator|+
literal|"; S3 Error message: "
operator|+
name|e
operator|.
name|getS3ErrorMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
throw|throw
operator|new
name|S3Exception
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|handleServiceException (ServiceException e)
specifier|private
name|void
name|handleServiceException
parameter_list|(
name|ServiceException
name|e
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|IOException
condition|)
block|{
throw|throw
operator|(
name|IOException
operator|)
name|e
operator|.
name|getCause
argument_list|()
throw|;
block|}
else|else
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
literal|"Got ServiceException with Error code: "
operator|+
name|e
operator|.
name|getErrorCode
argument_list|()
operator|+
literal|";and Error message: "
operator|+
name|e
operator|.
name|getErrorMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

