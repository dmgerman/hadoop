begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
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
name|Map
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

begin_comment
comment|/**  * MultipartUploader is an interface for copying files multipart and across  * multiple nodes. Users should:  *<ol>  *<li>Initialize an upload.</li>  *<li>Upload parts in any order.</li>  *<li>Complete the upload in order to have it materialize in the destination  *   FS.</li>  *</ol>  */
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
DECL|class|MultipartUploader
specifier|public
specifier|abstract
class|class
name|MultipartUploader
implements|implements
name|Closeable
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MultipartUploader
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Perform any cleanup.    * The upload is not required to support any operations after this.    * @throws IOException problems on close.    */
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{   }
comment|/**    * Initialize a multipart upload.    * @param filePath Target path for upload.    * @return unique identifier associating part uploads.    * @throws IOException IO failure    */
DECL|method|initialize (Path filePath)
specifier|public
specifier|abstract
name|UploadHandle
name|initialize
parameter_list|(
name|Path
name|filePath
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Put part as part of a multipart upload.    * It is possible to have parts uploaded in any order (or in parallel).    * @param filePath Target path for upload (same as {@link #initialize(Path)}).    * @param inputStream Data for this part. Implementations MUST close this    * stream after reading in the data.    * @param partNumber Index of the part relative to others.    * @param uploadId Identifier from {@link #initialize(Path)}.    * @param lengthInBytes Target length to read from the stream.    * @return unique PartHandle identifier for the uploaded part.    * @throws IOException IO failure    */
DECL|method|putPart (Path filePath, InputStream inputStream, int partNumber, UploadHandle uploadId, long lengthInBytes)
specifier|public
specifier|abstract
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
function_decl|;
comment|/**    * Complete a multipart upload.    * @param filePath Target path for upload (same as {@link #initialize(Path)}.    * @param handles non-empty map of part number to part handle.    *          from {@link #putPart(Path, InputStream, int, UploadHandle, long)}.    * @param multipartUploadId Identifier from {@link #initialize(Path)}.    * @return unique PathHandle identifier for the uploaded file.    * @throws IOException IO failure    */
DECL|method|complete (Path filePath, Map<Integer, PartHandle> handles, UploadHandle multipartUploadId)
specifier|public
specifier|abstract
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
name|handles
parameter_list|,
name|UploadHandle
name|multipartUploadId
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Aborts a multipart upload.    * @param filePath Target path for upload (same as {@link #initialize(Path)}.    * @param multipartUploadId Identifier from {@link #initialize(Path)}.    * @throws IOException IO failure    */
DECL|method|abort (Path filePath, UploadHandle multipartUploadId)
specifier|public
specifier|abstract
name|void
name|abort
parameter_list|(
name|Path
name|filePath
parameter_list|,
name|UploadHandle
name|multipartUploadId
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Utility method to validate uploadIDs.    * @param uploadId Upload ID    * @throws IllegalArgumentException invalid ID    */
DECL|method|checkUploadId (byte[] uploadId)
specifier|protected
name|void
name|checkUploadId
parameter_list|(
name|byte
index|[]
name|uploadId
parameter_list|)
throws|throws
name|IllegalArgumentException
block|{
name|checkArgument
argument_list|(
name|uploadId
operator|!=
literal|null
argument_list|,
literal|"null uploadId"
argument_list|)
expr_stmt|;
name|checkArgument
argument_list|(
name|uploadId
operator|.
name|length
operator|>
literal|0
argument_list|,
literal|"Empty UploadId is not valid"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Utility method to validate partHandles.    * @param partHandles handles    * @throws IllegalArgumentException if the parts are invalid    */
DECL|method|checkPartHandles (Map<Integer, PartHandle> partHandles)
specifier|protected
name|void
name|checkPartHandles
parameter_list|(
name|Map
argument_list|<
name|Integer
argument_list|,
name|PartHandle
argument_list|>
name|partHandles
parameter_list|)
block|{
name|checkArgument
argument_list|(
operator|!
name|partHandles
operator|.
name|isEmpty
argument_list|()
argument_list|,
literal|"Empty upload"
argument_list|)
expr_stmt|;
name|partHandles
operator|.
name|keySet
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|forEach
argument_list|(
name|key
lambda|->
name|checkArgument
argument_list|(
name|key
operator|>
literal|0
argument_list|,
literal|"Invalid part handle index %s"
argument_list|,
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Check all the arguments to the    * {@link #putPart(Path, InputStream, int, UploadHandle, long)} operation.    * @param filePath Target path for upload (same as {@link #initialize(Path)}).    * @param inputStream Data for this part. Implementations MUST close this    * stream after reading in the data.    * @param partNumber Index of the part relative to others.    * @param uploadId Identifier from {@link #initialize(Path)}.    * @param lengthInBytes Target length to read from the stream.    * @throws IllegalArgumentException invalid argument    */
DECL|method|checkPutArguments (Path filePath, InputStream inputStream, int partNumber, UploadHandle uploadId, long lengthInBytes)
specifier|protected
name|void
name|checkPutArguments
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
name|IllegalArgumentException
block|{
name|checkArgument
argument_list|(
name|filePath
operator|!=
literal|null
argument_list|,
literal|"null filePath"
argument_list|)
expr_stmt|;
name|checkArgument
argument_list|(
name|inputStream
operator|!=
literal|null
argument_list|,
literal|"null inputStream"
argument_list|)
expr_stmt|;
name|checkArgument
argument_list|(
name|partNumber
operator|>
literal|0
argument_list|,
literal|"Invalid part number: %d"
argument_list|,
name|partNumber
argument_list|)
expr_stmt|;
name|checkArgument
argument_list|(
name|uploadId
operator|!=
literal|null
argument_list|,
literal|"null uploadId"
argument_list|)
expr_stmt|;
name|checkArgument
argument_list|(
name|lengthInBytes
operator|>=
literal|0
argument_list|,
literal|"Invalid part length: %d"
argument_list|,
name|lengthInBytes
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

