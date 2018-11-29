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
name|DataInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutputStream
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
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
name|Charsets
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
name|commons
operator|.
name|lang3
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
name|commons
operator|.
name|lang3
operator|.
name|tuple
operator|.
name|Pair
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
name|BBPartHandle
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
name|BBUploadHandle
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
name|fs
operator|.
name|MultipartUploader
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
name|MultipartUploaderFactory
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
name|PartHandle
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
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|PathHandle
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
name|UploadHandle
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
name|Constants
operator|.
name|FS_S3A
import|;
end_import

begin_comment
comment|/**  * MultipartUploader for S3AFileSystem. This uses the S3 multipart  * upload mechanism.  */
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
DECL|class|S3AMultipartUploader
specifier|public
class|class
name|S3AMultipartUploader
extends|extends
name|MultipartUploader
block|{
DECL|field|s3a
specifier|private
specifier|final
name|S3AFileSystem
name|s3a
decl_stmt|;
comment|/** Header for Parts: {@value}. */
DECL|field|HEADER
specifier|public
specifier|static
specifier|final
name|String
name|HEADER
init|=
literal|"S3A-part01"
decl_stmt|;
DECL|method|S3AMultipartUploader (FileSystem fs, Configuration conf)
specifier|public
name|S3AMultipartUploader
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|fs
operator|instanceof
name|S3AFileSystem
argument_list|,
literal|"Wrong filesystem: expected S3A but got %s"
argument_list|,
name|fs
argument_list|)
expr_stmt|;
name|s3a
operator|=
operator|(
name|S3AFileSystem
operator|)
name|fs
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|initialize (Path filePath)
specifier|public
name|UploadHandle
name|initialize
parameter_list|(
name|Path
name|filePath
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|WriteOperationHelper
name|writeHelper
init|=
name|s3a
operator|.
name|getWriteOperationHelper
argument_list|()
decl_stmt|;
name|String
name|key
init|=
name|s3a
operator|.
name|pathToKey
argument_list|(
name|filePath
argument_list|)
decl_stmt|;
name|String
name|uploadId
init|=
name|writeHelper
operator|.
name|initiateMultiPartUpload
argument_list|(
name|key
argument_list|)
decl_stmt|;
return|return
name|BBUploadHandle
operator|.
name|from
argument_list|(
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|uploadId
operator|.
name|getBytes
argument_list|(
name|Charsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|putPart (Path filePath, InputStream inputStream, int partNumber, UploadHandle uploadId, long lengthInBytes)
specifier|public
name|PartHandle
name|putPart
parameter_list|(
name|Path
name|filePath
parameter_list|,
name|InputStream
name|inputStream
parameter_list|,
name|int
name|partNumber
parameter_list|,
name|UploadHandle
name|uploadId
parameter_list|,
name|long
name|lengthInBytes
parameter_list|)
throws|throws
name|IOException
block|{
name|checkPutArguments
argument_list|(
name|filePath
argument_list|,
name|inputStream
argument_list|,
name|partNumber
argument_list|,
name|uploadId
argument_list|,
name|lengthInBytes
argument_list|)
expr_stmt|;
name|byte
index|[]
name|uploadIdBytes
init|=
name|uploadId
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
name|checkUploadId
argument_list|(
name|uploadIdBytes
argument_list|)
expr_stmt|;
name|String
name|key
init|=
name|s3a
operator|.
name|pathToKey
argument_list|(
name|filePath
argument_list|)
decl_stmt|;
specifier|final
name|WriteOperationHelper
name|writeHelper
init|=
name|s3a
operator|.
name|getWriteOperationHelper
argument_list|()
decl_stmt|;
name|String
name|uploadIdString
init|=
operator|new
name|String
argument_list|(
name|uploadIdBytes
argument_list|,
literal|0
argument_list|,
name|uploadIdBytes
operator|.
name|length
argument_list|,
name|Charsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
name|UploadPartRequest
name|request
init|=
name|writeHelper
operator|.
name|newUploadPartRequest
argument_list|(
name|key
argument_list|,
name|uploadIdString
argument_list|,
name|partNumber
argument_list|,
operator|(
name|int
operator|)
name|lengthInBytes
argument_list|,
name|inputStream
argument_list|,
literal|null
argument_list|,
literal|0L
argument_list|)
decl_stmt|;
name|UploadPartResult
name|result
init|=
name|writeHelper
operator|.
name|uploadPart
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|String
name|eTag
init|=
name|result
operator|.
name|getETag
argument_list|()
decl_stmt|;
return|return
name|BBPartHandle
operator|.
name|from
argument_list|(
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|buildPartHandlePayload
argument_list|(
name|eTag
argument_list|,
name|lengthInBytes
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|complete (Path filePath, Map<Integer, PartHandle> handleMap, UploadHandle uploadId)
specifier|public
name|PathHandle
name|complete
parameter_list|(
name|Path
name|filePath
parameter_list|,
name|Map
argument_list|<
name|Integer
argument_list|,
name|PartHandle
argument_list|>
name|handleMap
parameter_list|,
name|UploadHandle
name|uploadId
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|uploadIdBytes
init|=
name|uploadId
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
name|checkUploadId
argument_list|(
name|uploadIdBytes
argument_list|)
expr_stmt|;
name|checkPartHandles
argument_list|(
name|handleMap
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|Integer
argument_list|,
name|PartHandle
argument_list|>
argument_list|>
name|handles
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|handleMap
operator|.
name|entrySet
argument_list|()
argument_list|)
decl_stmt|;
name|handles
operator|.
name|sort
argument_list|(
name|Comparator
operator|.
name|comparingInt
argument_list|(
name|Map
operator|.
name|Entry
operator|::
name|getKey
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|WriteOperationHelper
name|writeHelper
init|=
name|s3a
operator|.
name|getWriteOperationHelper
argument_list|()
decl_stmt|;
name|String
name|key
init|=
name|s3a
operator|.
name|pathToKey
argument_list|(
name|filePath
argument_list|)
decl_stmt|;
name|String
name|uploadIdStr
init|=
operator|new
name|String
argument_list|(
name|uploadIdBytes
argument_list|,
literal|0
argument_list|,
name|uploadIdBytes
operator|.
name|length
argument_list|,
name|Charsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
name|ArrayList
argument_list|<
name|PartETag
argument_list|>
name|eTags
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|eTags
operator|.
name|ensureCapacity
argument_list|(
name|handles
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|long
name|totalLength
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Integer
argument_list|,
name|PartHandle
argument_list|>
name|handle
range|:
name|handles
control|)
block|{
name|byte
index|[]
name|payload
init|=
name|handle
operator|.
name|getValue
argument_list|()
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
name|Pair
argument_list|<
name|Long
argument_list|,
name|String
argument_list|>
name|result
init|=
name|parsePartHandlePayload
argument_list|(
name|payload
argument_list|)
decl_stmt|;
name|totalLength
operator|+=
name|result
operator|.
name|getLeft
argument_list|()
expr_stmt|;
name|eTags
operator|.
name|add
argument_list|(
operator|new
name|PartETag
argument_list|(
name|handle
operator|.
name|getKey
argument_list|()
argument_list|,
name|result
operator|.
name|getRight
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|AtomicInteger
name|errorCount
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|CompleteMultipartUploadResult
name|result
init|=
name|writeHelper
operator|.
name|completeMPUwithRetries
argument_list|(
name|key
argument_list|,
name|uploadIdStr
argument_list|,
name|eTags
argument_list|,
name|totalLength
argument_list|,
name|errorCount
argument_list|)
decl_stmt|;
name|byte
index|[]
name|eTag
init|=
name|result
operator|.
name|getETag
argument_list|()
operator|.
name|getBytes
argument_list|(
name|Charsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
return|return
call|(
name|PathHandle
call|)
argument_list|()
operator|->
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|eTag
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|abort (Path filePath, UploadHandle uploadId)
specifier|public
name|void
name|abort
parameter_list|(
name|Path
name|filePath
parameter_list|,
name|UploadHandle
name|uploadId
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|byte
index|[]
name|uploadIdBytes
init|=
name|uploadId
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
name|checkUploadId
argument_list|(
name|uploadIdBytes
argument_list|)
expr_stmt|;
specifier|final
name|WriteOperationHelper
name|writeHelper
init|=
name|s3a
operator|.
name|getWriteOperationHelper
argument_list|()
decl_stmt|;
name|String
name|key
init|=
name|s3a
operator|.
name|pathToKey
argument_list|(
name|filePath
argument_list|)
decl_stmt|;
name|String
name|uploadIdString
init|=
operator|new
name|String
argument_list|(
name|uploadIdBytes
argument_list|,
literal|0
argument_list|,
name|uploadIdBytes
operator|.
name|length
argument_list|,
name|Charsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
name|writeHelper
operator|.
name|abortMultipartCommit
argument_list|(
name|key
argument_list|,
name|uploadIdString
argument_list|)
expr_stmt|;
block|}
comment|/**    * Factory for creating MultipartUploader objects for s3a:// FileSystems.    */
DECL|class|Factory
specifier|public
specifier|static
class|class
name|Factory
extends|extends
name|MultipartUploaderFactory
block|{
annotation|@
name|Override
DECL|method|createMultipartUploader (FileSystem fs, Configuration conf)
specifier|protected
name|MultipartUploader
name|createMultipartUploader
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
if|if
condition|(
name|FS_S3A
operator|.
name|equals
argument_list|(
name|fs
operator|.
name|getScheme
argument_list|()
argument_list|)
condition|)
block|{
return|return
operator|new
name|S3AMultipartUploader
argument_list|(
name|fs
argument_list|,
name|conf
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
comment|/**    * Build the payload for marshalling.    * @param eTag upload etag    * @param len length    * @return a byte array to marshall.    * @throws IOException error writing the payload    */
annotation|@
name|VisibleForTesting
DECL|method|buildPartHandlePayload (String eTag, long len)
specifier|static
name|byte
index|[]
name|buildPartHandlePayload
parameter_list|(
name|String
name|eTag
parameter_list|,
name|long
name|len
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|StringUtils
operator|.
name|isNotEmpty
argument_list|(
name|eTag
argument_list|)
argument_list|,
literal|"Empty etag"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|len
operator|>=
literal|0
argument_list|,
literal|"Invalid length"
argument_list|)
expr_stmt|;
name|ByteArrayOutputStream
name|bytes
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
try|try
init|(
name|DataOutputStream
name|output
init|=
operator|new
name|DataOutputStream
argument_list|(
name|bytes
argument_list|)
init|)
block|{
name|output
operator|.
name|writeUTF
argument_list|(
name|HEADER
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeLong
argument_list|(
name|len
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeUTF
argument_list|(
name|eTag
argument_list|)
expr_stmt|;
block|}
return|return
name|bytes
operator|.
name|toByteArray
argument_list|()
return|;
block|}
comment|/**    * Parse the payload marshalled as a part handle.    * @param data handle data    * @return the length and etag    * @throws IOException error reading the payload    */
annotation|@
name|VisibleForTesting
DECL|method|parsePartHandlePayload (byte[] data)
specifier|static
name|Pair
argument_list|<
name|Long
argument_list|,
name|String
argument_list|>
name|parsePartHandlePayload
parameter_list|(
name|byte
index|[]
name|data
parameter_list|)
throws|throws
name|IOException
block|{
try|try
init|(
name|DataInputStream
name|input
init|=
operator|new
name|DataInputStream
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|data
argument_list|)
argument_list|)
init|)
block|{
specifier|final
name|String
name|header
init|=
name|input
operator|.
name|readUTF
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|HEADER
operator|.
name|equals
argument_list|(
name|header
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Wrong header string: \""
operator|+
name|header
operator|+
literal|"\""
argument_list|)
throw|;
block|}
specifier|final
name|long
name|len
init|=
name|input
operator|.
name|readLong
argument_list|()
decl_stmt|;
specifier|final
name|String
name|etag
init|=
name|input
operator|.
name|readUTF
argument_list|()
decl_stmt|;
if|if
condition|(
name|len
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Negative length"
argument_list|)
throw|;
block|}
return|return
name|Pair
operator|.
name|of
argument_list|(
name|len
argument_list|,
name|etag
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

