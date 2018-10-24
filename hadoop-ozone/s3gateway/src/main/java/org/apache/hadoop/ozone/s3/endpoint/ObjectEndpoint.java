begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.s3.endpoint
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|s3
operator|.
name|endpoint
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|DELETE
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|DefaultValue
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|GET
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|HEAD
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|HeaderParam
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|PUT
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|PathParam
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|QueryParam
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|Context
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|HttpHeaders
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|Response
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|Response
operator|.
name|ResponseBuilder
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|Response
operator|.
name|Status
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|StreamingOutput
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
name|time
operator|.
name|Instant
import|;
end_import

begin_import
import|import
name|java
operator|.
name|time
operator|.
name|ZoneId
import|;
end_import

begin_import
import|import
name|java
operator|.
name|time
operator|.
name|ZonedDateTime
import|;
end_import

begin_import
import|import
name|java
operator|.
name|time
operator|.
name|format
operator|.
name|DateTimeFormatter
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
name|ReplicationFactor
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
name|ReplicationType
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
name|client
operator|.
name|OzoneBucket
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
name|client
operator|.
name|OzoneKeyDetails
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
name|client
operator|.
name|io
operator|.
name|OzoneInputStream
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
name|client
operator|.
name|io
operator|.
name|OzoneOutputStream
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
name|s3
operator|.
name|SignedChunksInputStream
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
name|s3
operator|.
name|exception
operator|.
name|OS3Exception
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
name|s3
operator|.
name|exception
operator|.
name|S3ErrorTable
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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|IOUtils
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
name|web
operator|.
name|utils
operator|.
name|OzoneUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|HttpStatus
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

begin_comment
comment|/**  * Key level rest endpoints.  */
end_comment

begin_class
annotation|@
name|Path
argument_list|(
literal|"/{bucket}/{path:.+}"
argument_list|)
DECL|class|ObjectEndpoint
specifier|public
class|class
name|ObjectEndpoint
extends|extends
name|EndpointBase
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
name|ObjectEndpoint
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Context
DECL|field|headers
specifier|private
name|HttpHeaders
name|headers
decl_stmt|;
DECL|field|customizableGetHeaders
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|customizableGetHeaders
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|ObjectEndpoint ()
specifier|public
name|ObjectEndpoint
parameter_list|()
block|{
name|customizableGetHeaders
operator|.
name|add
argument_list|(
literal|"Content-Type"
argument_list|)
expr_stmt|;
name|customizableGetHeaders
operator|.
name|add
argument_list|(
literal|"Content-Language"
argument_list|)
expr_stmt|;
name|customizableGetHeaders
operator|.
name|add
argument_list|(
literal|"Expires"
argument_list|)
expr_stmt|;
name|customizableGetHeaders
operator|.
name|add
argument_list|(
literal|"Cache-Control"
argument_list|)
expr_stmt|;
name|customizableGetHeaders
operator|.
name|add
argument_list|(
literal|"Content-Disposition"
argument_list|)
expr_stmt|;
name|customizableGetHeaders
operator|.
name|add
argument_list|(
literal|"Content-Encoding"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Rest endpoint to upload object to a bucket.    *<p>    * See: https://docs.aws.amazon.com/AmazonS3/latest/API/RESTObjectPUT.html for    * more details.    */
annotation|@
name|PUT
DECL|method|put ( @athParamR) String bucketName, @PathParam(R) String keyPath, @DefaultValue(R) @QueryParam(R) ReplicationType replicationType, @DefaultValue(R) @QueryParam(R) ReplicationFactor replicationFactor, @HeaderParam(R) long length, InputStream body)
specifier|public
name|Response
name|put
parameter_list|(
annotation|@
name|PathParam
argument_list|(
literal|"bucket"
argument_list|)
name|String
name|bucketName
parameter_list|,
annotation|@
name|PathParam
argument_list|(
literal|"path"
argument_list|)
name|String
name|keyPath
parameter_list|,
annotation|@
name|DefaultValue
argument_list|(
literal|"STAND_ALONE"
argument_list|)
annotation|@
name|QueryParam
argument_list|(
literal|"replicationType"
argument_list|)
name|ReplicationType
name|replicationType
parameter_list|,
annotation|@
name|DefaultValue
argument_list|(
literal|"ONE"
argument_list|)
annotation|@
name|QueryParam
argument_list|(
literal|"replicationFactor"
argument_list|)
name|ReplicationFactor
name|replicationFactor
parameter_list|,
annotation|@
name|HeaderParam
argument_list|(
literal|"Content-Length"
argument_list|)
name|long
name|length
parameter_list|,
name|InputStream
name|body
parameter_list|)
throws|throws
name|IOException
throws|,
name|OS3Exception
block|{
name|OzoneOutputStream
name|output
init|=
literal|null
decl_stmt|;
try|try
block|{
name|String
name|copyHeader
init|=
name|headers
operator|.
name|getHeaderString
argument_list|(
literal|"x-amz-copy-source"
argument_list|)
decl_stmt|;
if|if
condition|(
name|copyHeader
operator|!=
literal|null
condition|)
block|{
comment|//Copy object, as copy source available.
name|CopyObjectResponse
name|copyObjectResponse
init|=
name|copyObject
argument_list|(
name|copyHeader
argument_list|,
name|bucketName
argument_list|,
name|keyPath
argument_list|,
name|replicationType
argument_list|,
name|replicationFactor
argument_list|)
decl_stmt|;
return|return
name|Response
operator|.
name|status
argument_list|(
name|Status
operator|.
name|OK
argument_list|)
operator|.
name|entity
argument_list|(
name|copyObjectResponse
argument_list|)
operator|.
name|header
argument_list|(
literal|"Connection"
argument_list|,
literal|"close"
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
comment|// Normal put object
name|OzoneBucket
name|bucket
init|=
name|getBucket
argument_list|(
name|bucketName
argument_list|)
decl_stmt|;
name|output
operator|=
name|bucket
operator|.
name|createKey
argument_list|(
name|keyPath
argument_list|,
name|length
argument_list|,
name|replicationType
argument_list|,
name|replicationFactor
argument_list|)
expr_stmt|;
if|if
condition|(
literal|"STREAMING-AWS4-HMAC-SHA256-PAYLOAD"
operator|.
name|equals
argument_list|(
name|headers
operator|.
name|getHeaderString
argument_list|(
literal|"x-amz-content-sha256"
argument_list|)
argument_list|)
condition|)
block|{
name|body
operator|=
operator|new
name|SignedChunksInputStream
argument_list|(
name|body
argument_list|)
expr_stmt|;
block|}
name|IOUtils
operator|.
name|copy
argument_list|(
name|body
argument_list|,
name|output
argument_list|)
expr_stmt|;
return|return
name|Response
operator|.
name|ok
argument_list|()
operator|.
name|status
argument_list|(
name|HttpStatus
operator|.
name|SC_OK
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Exception occurred in PutObject"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
throw|throw
name|ex
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|output
operator|!=
literal|null
condition|)
block|{
name|output
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Rest endpoint to download object from a bucket.    *<p>    * See: https://docs.aws.amazon.com/AmazonS3/latest/API/RESTObjectGET.html for    * more details.    */
annotation|@
name|GET
DECL|method|get ( @athParamR) String bucketName, @PathParam(R) String keyPath, InputStream body)
specifier|public
name|Response
name|get
parameter_list|(
annotation|@
name|PathParam
argument_list|(
literal|"bucket"
argument_list|)
name|String
name|bucketName
parameter_list|,
annotation|@
name|PathParam
argument_list|(
literal|"path"
argument_list|)
name|String
name|keyPath
parameter_list|,
name|InputStream
name|body
parameter_list|)
throws|throws
name|IOException
throws|,
name|OS3Exception
block|{
try|try
block|{
name|OzoneBucket
name|bucket
init|=
name|getBucket
argument_list|(
name|bucketName
argument_list|)
decl_stmt|;
name|OzoneInputStream
name|key
init|=
name|bucket
operator|.
name|readKey
argument_list|(
name|keyPath
argument_list|)
decl_stmt|;
name|StreamingOutput
name|output
init|=
name|dest
lambda|->
name|IOUtils
operator|.
name|copy
argument_list|(
name|key
argument_list|,
name|dest
argument_list|)
decl_stmt|;
name|ResponseBuilder
name|responseBuilder
init|=
name|Response
operator|.
name|ok
argument_list|(
name|output
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|responseHeader
range|:
name|customizableGetHeaders
control|)
block|{
name|String
name|headerValue
init|=
name|headers
operator|.
name|getHeaderString
argument_list|(
name|responseHeader
argument_list|)
decl_stmt|;
if|if
condition|(
name|headerValue
operator|!=
literal|null
condition|)
block|{
name|responseBuilder
operator|.
name|header
argument_list|(
name|responseHeader
argument_list|,
name|headerValue
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|responseBuilder
operator|.
name|build
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
if|if
condition|(
name|ex
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"NOT_FOUND"
argument_list|)
condition|)
block|{
name|OS3Exception
name|os3Exception
init|=
name|S3ErrorTable
operator|.
name|newError
argument_list|(
name|S3ErrorTable
operator|.
name|NO_SUCH_KEY
argument_list|,
name|keyPath
argument_list|)
decl_stmt|;
throw|throw
name|os3Exception
throw|;
block|}
else|else
block|{
throw|throw
name|ex
throw|;
block|}
block|}
block|}
comment|/**    * Rest endpoint to check existence of an object in a bucket.    *<p>    * See: https://docs.aws.amazon.com/AmazonS3/latest/API/RESTObjectHEAD.html    * for more details.    */
annotation|@
name|HEAD
DECL|method|head ( @athParamR) String bucketName, @PathParam(R) String keyPath)
specifier|public
name|Response
name|head
parameter_list|(
annotation|@
name|PathParam
argument_list|(
literal|"bucket"
argument_list|)
name|String
name|bucketName
parameter_list|,
annotation|@
name|PathParam
argument_list|(
literal|"path"
argument_list|)
name|String
name|keyPath
parameter_list|)
throws|throws
name|Exception
block|{
name|OzoneKeyDetails
name|key
decl_stmt|;
try|try
block|{
name|key
operator|=
name|getBucket
argument_list|(
name|bucketName
argument_list|)
operator|.
name|getKey
argument_list|(
name|keyPath
argument_list|)
expr_stmt|;
comment|// TODO: return the specified range bytes of this object.
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Exception occurred in HeadObject"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
if|if
condition|(
name|ex
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"KEY_NOT_FOUND"
argument_list|)
condition|)
block|{
comment|// Just return 404 with no content
return|return
name|Response
operator|.
name|status
argument_list|(
name|Status
operator|.
name|NOT_FOUND
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
else|else
block|{
throw|throw
name|ex
throw|;
block|}
block|}
name|ZonedDateTime
name|lastModificationTime
init|=
name|Instant
operator|.
name|ofEpochMilli
argument_list|(
name|key
operator|.
name|getModificationTime
argument_list|()
argument_list|)
operator|.
name|atZone
argument_list|(
name|ZoneId
operator|.
name|of
argument_list|(
literal|"GMT"
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|Response
operator|.
name|ok
argument_list|()
operator|.
name|status
argument_list|(
name|HttpStatus
operator|.
name|SC_OK
argument_list|)
operator|.
name|header
argument_list|(
literal|"Last-Modified"
argument_list|,
name|DateTimeFormatter
operator|.
name|RFC_1123_DATE_TIME
operator|.
name|format
argument_list|(
name|lastModificationTime
argument_list|)
argument_list|)
operator|.
name|header
argument_list|(
literal|"ETag"
argument_list|,
literal|""
operator|+
name|key
operator|.
name|getModificationTime
argument_list|()
argument_list|)
operator|.
name|header
argument_list|(
literal|"Content-Length"
argument_list|,
name|key
operator|.
name|getDataSize
argument_list|()
argument_list|)
operator|.
name|header
argument_list|(
literal|"Content-Type"
argument_list|,
literal|"binary/octet-stream"
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
comment|/**    * Delete a specific object from a bucket.    *<p>    * See: https://docs.aws.amazon.com/AmazonS3/latest/API/RESTObjectDELETE.html    * for more details.    */
annotation|@
name|DELETE
DECL|method|delete ( @athParamR) String bucketName, @PathParam(R) String keyPath)
specifier|public
name|Response
name|delete
parameter_list|(
annotation|@
name|PathParam
argument_list|(
literal|"bucket"
argument_list|)
name|String
name|bucketName
parameter_list|,
annotation|@
name|PathParam
argument_list|(
literal|"path"
argument_list|)
name|String
name|keyPath
parameter_list|)
throws|throws
name|IOException
throws|,
name|OS3Exception
block|{
try|try
block|{
name|OzoneBucket
name|bucket
init|=
name|getBucket
argument_list|(
name|bucketName
argument_list|)
decl_stmt|;
name|bucket
operator|.
name|getKey
argument_list|(
name|keyPath
argument_list|)
expr_stmt|;
name|bucket
operator|.
name|deleteKey
argument_list|(
name|keyPath
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
if|if
condition|(
name|ex
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"BUCKET_NOT_FOUND"
argument_list|)
condition|)
block|{
throw|throw
name|S3ErrorTable
operator|.
name|newError
argument_list|(
name|S3ErrorTable
operator|.
name|NO_SUCH_BUCKET
argument_list|,
name|bucketName
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
operator|!
name|ex
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"NOT_FOUND"
argument_list|)
condition|)
block|{
throw|throw
name|ex
throw|;
block|}
comment|//NOT_FOUND is not a problem, AWS doesn't throw exception for missing
comment|// keys. Just return 204.
block|}
return|return
name|Response
operator|.
name|status
argument_list|(
name|Status
operator|.
name|NO_CONTENT
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|setHeaders (HttpHeaders headers)
specifier|public
name|void
name|setHeaders
parameter_list|(
name|HttpHeaders
name|headers
parameter_list|)
block|{
name|this
operator|.
name|headers
operator|=
name|headers
expr_stmt|;
block|}
DECL|method|copyObject (String copyHeader, String destBucket, String destkey, ReplicationType replicationType, ReplicationFactor replicationFactor)
specifier|private
name|CopyObjectResponse
name|copyObject
parameter_list|(
name|String
name|copyHeader
parameter_list|,
name|String
name|destBucket
parameter_list|,
name|String
name|destkey
parameter_list|,
name|ReplicationType
name|replicationType
parameter_list|,
name|ReplicationFactor
name|replicationFactor
parameter_list|)
throws|throws
name|OS3Exception
throws|,
name|IOException
block|{
if|if
condition|(
name|copyHeader
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|copyHeader
operator|=
name|copyHeader
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|int
name|pos
init|=
name|copyHeader
operator|.
name|indexOf
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
if|if
condition|(
name|pos
operator|==
operator|-
literal|1
condition|)
block|{
name|OS3Exception
name|ex
init|=
name|S3ErrorTable
operator|.
name|newError
argument_list|(
name|S3ErrorTable
operator|.
name|INVALID_ARGUMENT
argument_list|,
name|copyHeader
argument_list|)
decl_stmt|;
name|ex
operator|.
name|setErrorMessage
argument_list|(
literal|"Copy Source must mention the source bucket and "
operator|+
literal|"key: sourcebucket/sourcekey"
argument_list|)
expr_stmt|;
throw|throw
name|ex
throw|;
block|}
name|String
name|sourceBucket
init|=
name|copyHeader
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|pos
argument_list|)
decl_stmt|;
name|String
name|sourceKey
init|=
name|copyHeader
operator|.
name|substring
argument_list|(
name|pos
operator|+
literal|1
argument_list|)
decl_stmt|;
name|OzoneInputStream
name|sourceInputStream
init|=
literal|null
decl_stmt|;
name|OzoneOutputStream
name|destOutputStream
init|=
literal|null
decl_stmt|;
name|boolean
name|closed
init|=
literal|false
decl_stmt|;
try|try
block|{
comment|// Checking whether we trying to copying to it self.
if|if
condition|(
name|sourceBucket
operator|.
name|equals
argument_list|(
name|destBucket
argument_list|)
condition|)
block|{
if|if
condition|(
name|sourceKey
operator|.
name|equals
argument_list|(
name|destkey
argument_list|)
condition|)
block|{
name|OS3Exception
name|ex
init|=
name|S3ErrorTable
operator|.
name|newError
argument_list|(
name|S3ErrorTable
operator|.
name|INVALID_REQUEST
argument_list|,
name|copyHeader
argument_list|)
decl_stmt|;
name|ex
operator|.
name|setErrorMessage
argument_list|(
literal|"This copy request is illegal because it is "
operator|+
literal|"trying to copy an object to it self itself without changing "
operator|+
literal|"the object's metadata, storage class, website redirect "
operator|+
literal|"location or encryption attributes."
argument_list|)
expr_stmt|;
throw|throw
name|ex
throw|;
block|}
block|}
name|OzoneBucket
name|sourceOzoneBucket
init|=
name|getBucket
argument_list|(
name|sourceBucket
argument_list|)
decl_stmt|;
name|OzoneBucket
name|destOzoneBucket
init|=
name|getBucket
argument_list|(
name|destBucket
argument_list|)
decl_stmt|;
name|OzoneKeyDetails
name|sourceKeyDetails
init|=
name|sourceOzoneBucket
operator|.
name|getKey
argument_list|(
name|sourceKey
argument_list|)
decl_stmt|;
name|long
name|sourceKeyLen
init|=
name|sourceKeyDetails
operator|.
name|getDataSize
argument_list|()
decl_stmt|;
name|sourceInputStream
operator|=
name|sourceOzoneBucket
operator|.
name|readKey
argument_list|(
name|sourceKey
argument_list|)
expr_stmt|;
name|destOutputStream
operator|=
name|destOzoneBucket
operator|.
name|createKey
argument_list|(
name|destkey
argument_list|,
name|sourceKeyLen
argument_list|,
name|replicationType
argument_list|,
name|replicationFactor
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|copy
argument_list|(
name|sourceInputStream
argument_list|,
name|destOutputStream
argument_list|)
expr_stmt|;
comment|// Closing here, as if we don't call close this key will not commit in
comment|// OM, and getKey fails.
name|sourceInputStream
operator|.
name|close
argument_list|()
expr_stmt|;
name|destOutputStream
operator|.
name|close
argument_list|()
expr_stmt|;
name|closed
operator|=
literal|true
expr_stmt|;
name|OzoneKeyDetails
name|destKeyDetails
init|=
name|destOzoneBucket
operator|.
name|getKey
argument_list|(
name|destkey
argument_list|)
decl_stmt|;
name|CopyObjectResponse
name|copyObjectResponse
init|=
operator|new
name|CopyObjectResponse
argument_list|()
decl_stmt|;
name|copyObjectResponse
operator|.
name|setETag
argument_list|(
name|OzoneUtils
operator|.
name|getRequestID
argument_list|()
argument_list|)
expr_stmt|;
name|copyObjectResponse
operator|.
name|setLastModified
argument_list|(
name|Instant
operator|.
name|ofEpochMilli
argument_list|(
name|destKeyDetails
operator|.
name|getModificationTime
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|copyObjectResponse
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
if|if
condition|(
name|ex
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"KEY_NOT_FOUND"
argument_list|)
condition|)
block|{
throw|throw
name|S3ErrorTable
operator|.
name|newError
argument_list|(
name|S3ErrorTable
operator|.
name|NO_SUCH_KEY
argument_list|,
name|sourceKey
argument_list|)
throw|;
block|}
name|LOG
operator|.
name|error
argument_list|(
literal|"Exception occurred in PutObject"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
throw|throw
name|ex
throw|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|closed
condition|)
block|{
if|if
condition|(
name|sourceInputStream
operator|!=
literal|null
condition|)
block|{
name|sourceInputStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|destOutputStream
operator|!=
literal|null
condition|)
block|{
name|destOutputStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

