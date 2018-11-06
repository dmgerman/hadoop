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
name|POST
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
name|Produces
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
name|MediaType
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
name|Status
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
name|util
operator|.
name|Iterator
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
name|OzoneKey
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
name|commontypes
operator|.
name|KeyMetadata
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
name|endpoint
operator|.
name|MultiDeleteRequest
operator|.
name|DeleteObject
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
name|endpoint
operator|.
name|MultiDeleteResponse
operator|.
name|DeletedObject
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
name|endpoint
operator|.
name|MultiDeleteResponse
operator|.
name|Error
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
name|edu
operator|.
name|umd
operator|.
name|cs
operator|.
name|findbugs
operator|.
name|annotations
operator|.
name|SuppressFBWarnings
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
name|hadoop
operator|.
name|ozone
operator|.
name|s3
operator|.
name|util
operator|.
name|S3StorageType
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
name|util
operator|.
name|S3utils
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

begin_import
import|import static
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
name|util
operator|.
name|S3Consts
operator|.
name|ENCODING_TYPE
import|;
end_import

begin_comment
comment|/**  * Bucket level rest endpoints.  */
end_comment

begin_class
annotation|@
name|Path
argument_list|(
literal|"/{bucket}"
argument_list|)
DECL|class|BucketEndpoint
specifier|public
class|class
name|BucketEndpoint
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
name|BucketEndpoint
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Rest endpoint to list objects in a specific bucket.    *<p>    * See: https://docs.aws.amazon.com/AmazonS3/latest/API/v2-RESTBucketGET.html    * for more details.    */
annotation|@
name|GET
annotation|@
name|SuppressFBWarnings
DECL|method|list ( @athParamR) String bucketName, @QueryParam(R) String delimiter, @QueryParam(R) String encodingType, @QueryParam(R) String marker, @DefaultValue(R) @QueryParam(R) int maxKeys, @QueryParam(R) String prefix, @QueryParam(R) String browser, @QueryParam(R) String continueToken, @QueryParam(R) String startAfter, @Context HttpHeaders hh)
specifier|public
name|Response
name|list
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
name|QueryParam
argument_list|(
literal|"delimiter"
argument_list|)
name|String
name|delimiter
parameter_list|,
annotation|@
name|QueryParam
argument_list|(
literal|"encoding-type"
argument_list|)
name|String
name|encodingType
parameter_list|,
annotation|@
name|QueryParam
argument_list|(
literal|"marker"
argument_list|)
name|String
name|marker
parameter_list|,
annotation|@
name|DefaultValue
argument_list|(
literal|"1000"
argument_list|)
annotation|@
name|QueryParam
argument_list|(
literal|"max-keys"
argument_list|)
name|int
name|maxKeys
parameter_list|,
annotation|@
name|QueryParam
argument_list|(
literal|"prefix"
argument_list|)
name|String
name|prefix
parameter_list|,
annotation|@
name|QueryParam
argument_list|(
literal|"browser"
argument_list|)
name|String
name|browser
parameter_list|,
annotation|@
name|QueryParam
argument_list|(
literal|"continuation-token"
argument_list|)
name|String
name|continueToken
parameter_list|,
annotation|@
name|QueryParam
argument_list|(
literal|"start-after"
argument_list|)
name|String
name|startAfter
parameter_list|,
annotation|@
name|Context
name|HttpHeaders
name|hh
parameter_list|)
throws|throws
name|OS3Exception
throws|,
name|IOException
block|{
if|if
condition|(
name|browser
operator|!=
literal|null
condition|)
block|{
name|InputStream
name|browserPage
init|=
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"/browser.html"
argument_list|)
decl_stmt|;
return|return
name|Response
operator|.
name|ok
argument_list|(
name|browserPage
argument_list|,
name|MediaType
operator|.
name|TEXT_HTML_TYPE
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
if|if
condition|(
name|prefix
operator|==
literal|null
condition|)
block|{
name|prefix
operator|=
literal|""
expr_stmt|;
block|}
name|OzoneBucket
name|bucket
init|=
name|getBucket
argument_list|(
name|bucketName
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|?
extends|extends
name|OzoneKey
argument_list|>
name|ozoneKeyIterator
decl_stmt|;
name|String
name|decodedToken
init|=
name|S3utils
operator|.
name|decodeContinueToken
argument_list|(
name|continueToken
argument_list|)
decl_stmt|;
if|if
condition|(
name|startAfter
operator|!=
literal|null
operator|&&
name|continueToken
operator|!=
literal|null
condition|)
block|{
comment|// If continuation token and start after both are provided, then we
comment|// ignore start After
name|ozoneKeyIterator
operator|=
name|bucket
operator|.
name|listKeys
argument_list|(
name|prefix
argument_list|,
name|decodedToken
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|startAfter
operator|!=
literal|null
operator|&&
name|continueToken
operator|==
literal|null
condition|)
block|{
name|ozoneKeyIterator
operator|=
name|bucket
operator|.
name|listKeys
argument_list|(
name|prefix
argument_list|,
name|startAfter
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|startAfter
operator|==
literal|null
operator|&&
name|continueToken
operator|!=
literal|null
condition|)
block|{
name|ozoneKeyIterator
operator|=
name|bucket
operator|.
name|listKeys
argument_list|(
name|prefix
argument_list|,
name|decodedToken
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ozoneKeyIterator
operator|=
name|bucket
operator|.
name|listKeys
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
block|}
name|ListObjectResponse
name|response
init|=
operator|new
name|ListObjectResponse
argument_list|()
decl_stmt|;
name|response
operator|.
name|setDelimiter
argument_list|(
name|delimiter
argument_list|)
expr_stmt|;
name|response
operator|.
name|setName
argument_list|(
name|bucketName
argument_list|)
expr_stmt|;
name|response
operator|.
name|setPrefix
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
name|response
operator|.
name|setMarker
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|response
operator|.
name|setMaxKeys
argument_list|(
name|maxKeys
argument_list|)
expr_stmt|;
name|response
operator|.
name|setEncodingType
argument_list|(
name|ENCODING_TYPE
argument_list|)
expr_stmt|;
name|response
operator|.
name|setTruncated
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|response
operator|.
name|setContinueToken
argument_list|(
name|continueToken
argument_list|)
expr_stmt|;
name|String
name|prevDir
init|=
literal|null
decl_stmt|;
name|String
name|lastKey
init|=
literal|null
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|ozoneKeyIterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|OzoneKey
name|next
init|=
name|ozoneKeyIterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|relativeKeyName
init|=
name|next
operator|.
name|getName
argument_list|()
operator|.
name|substring
argument_list|(
name|prefix
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|depth
init|=
name|StringUtils
operator|.
name|countMatches
argument_list|(
name|relativeKeyName
argument_list|,
name|delimiter
argument_list|)
decl_stmt|;
if|if
condition|(
name|delimiter
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|depth
operator|>
literal|0
condition|)
block|{
comment|// means key has multiple delimiters in its value.
comment|// ex: dir/dir1/dir2, where delimiter is "/" and prefix is dir/
name|String
name|dirName
init|=
name|relativeKeyName
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|relativeKeyName
operator|.
name|indexOf
argument_list|(
name|delimiter
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|dirName
operator|.
name|equals
argument_list|(
name|prevDir
argument_list|)
condition|)
block|{
name|response
operator|.
name|addPrefix
argument_list|(
name|prefix
operator|+
name|dirName
operator|+
name|delimiter
argument_list|)
expr_stmt|;
name|prevDir
operator|=
name|dirName
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|relativeKeyName
operator|.
name|endsWith
argument_list|(
name|delimiter
argument_list|)
condition|)
block|{
comment|// means or key is same as prefix with delimiter at end and ends with
comment|// delimiter. ex: dir/, where prefix is dir and delimiter is /
name|response
operator|.
name|addPrefix
argument_list|(
name|relativeKeyName
argument_list|)
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
else|else
block|{
comment|// means our key is matched with prefix if prefix is given and it
comment|// does not have any common prefix.
name|addKey
argument_list|(
name|response
argument_list|,
name|next
argument_list|)
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
block|}
else|else
block|{
name|addKey
argument_list|(
name|response
argument_list|,
name|next
argument_list|)
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|count
operator|==
name|maxKeys
condition|)
block|{
name|lastKey
operator|=
name|next
operator|.
name|getName
argument_list|()
expr_stmt|;
break|break;
block|}
block|}
name|response
operator|.
name|setKeyCount
argument_list|(
name|count
argument_list|)
expr_stmt|;
if|if
condition|(
name|count
operator|<
name|maxKeys
condition|)
block|{
name|response
operator|.
name|setTruncated
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|ozoneKeyIterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|response
operator|.
name|setTruncated
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|response
operator|.
name|setNextToken
argument_list|(
name|S3utils
operator|.
name|generateContinueToken
argument_list|(
name|lastKey
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|response
operator|.
name|setTruncated
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
name|response
operator|.
name|setKeyCount
argument_list|(
name|response
operator|.
name|getCommonPrefixes
argument_list|()
operator|.
name|size
argument_list|()
operator|+
name|response
operator|.
name|getContents
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|Response
operator|.
name|ok
argument_list|(
name|response
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|PUT
DECL|method|put (@athParamR) String bucketName, @Context HttpHeaders httpHeaders)
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
name|Context
name|HttpHeaders
name|httpHeaders
parameter_list|)
throws|throws
name|IOException
throws|,
name|OS3Exception
block|{
name|String
name|userName
init|=
name|getAuthenticationHeaderParser
argument_list|()
operator|.
name|getAccessKeyID
argument_list|()
decl_stmt|;
name|String
name|location
init|=
name|createS3Bucket
argument_list|(
name|userName
argument_list|,
name|bucketName
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Location is {}"
argument_list|,
name|location
argument_list|)
expr_stmt|;
return|return
name|Response
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
literal|"Location"
argument_list|,
name|location
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
comment|/**    * Rest endpoint to check the existence of a bucket.    *<p>    * See: https://docs.aws.amazon.com/AmazonS3/latest/API/RESTBucketHEAD.html    * for more details.    */
annotation|@
name|HEAD
DECL|method|head (@athParamR) String bucketName)
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
parameter_list|)
throws|throws
name|OS3Exception
throws|,
name|IOException
block|{
try|try
block|{
name|getBucket
argument_list|(
name|bucketName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|OS3Exception
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Exception occurred in headBucket"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
comment|//TODO: use a subclass fo OS3Exception and catch it here.
if|if
condition|(
name|ex
operator|.
name|getCode
argument_list|()
operator|.
name|contains
argument_list|(
literal|"NoSuchBucket"
argument_list|)
condition|)
block|{
return|return
name|Response
operator|.
name|status
argument_list|(
name|Status
operator|.
name|BAD_REQUEST
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
return|return
name|Response
operator|.
name|ok
argument_list|()
operator|.
name|build
argument_list|()
return|;
block|}
comment|/**    * Rest endpoint to delete specific bucket.    *<p>    * See: https://docs.aws.amazon.com/AmazonS3/latest/API/RESTBucketDELETE.html    * for more details.    */
annotation|@
name|DELETE
DECL|method|delete (@athParamR) String bucketName)
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
parameter_list|)
throws|throws
name|IOException
throws|,
name|OS3Exception
block|{
try|try
block|{
name|deleteS3Bucket
argument_list|(
name|bucketName
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
literal|"BUCKET_NOT_EMPTY"
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
name|BUCKET_NOT_EMPTY
argument_list|,
name|bucketName
argument_list|)
decl_stmt|;
throw|throw
name|os3Exception
throw|;
block|}
elseif|else
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
name|OS3Exception
name|os3Exception
init|=
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
return|return
name|Response
operator|.
name|status
argument_list|(
name|HttpStatus
operator|.
name|SC_NO_CONTENT
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
comment|/**    * Implement multi delete.    *<p>    * see: https://docs.aws.amazon    * .com/AmazonS3/latest/API/multiobjectdeleteapi.html    */
annotation|@
name|POST
annotation|@
name|Produces
argument_list|(
name|MediaType
operator|.
name|APPLICATION_XML
argument_list|)
DECL|method|multiDelete (@athParamR) String bucketName, @QueryParam(R) String delete, MultiDeleteRequest request)
specifier|public
name|MultiDeleteResponse
name|multiDelete
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
name|QueryParam
argument_list|(
literal|"delete"
argument_list|)
name|String
name|delete
parameter_list|,
name|MultiDeleteRequest
name|request
parameter_list|)
throws|throws
name|OS3Exception
throws|,
name|IOException
block|{
name|OzoneBucket
name|bucket
init|=
name|getBucket
argument_list|(
name|bucketName
argument_list|)
decl_stmt|;
name|MultiDeleteResponse
name|result
init|=
operator|new
name|MultiDeleteResponse
argument_list|()
decl_stmt|;
if|if
condition|(
name|request
operator|.
name|getObjects
argument_list|()
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|DeleteObject
name|keyToDelete
range|:
name|request
operator|.
name|getObjects
argument_list|()
control|)
block|{
try|try
block|{
name|bucket
operator|.
name|deleteKey
argument_list|(
name|keyToDelete
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|request
operator|.
name|isQuiet
argument_list|()
condition|)
block|{
name|result
operator|.
name|addDeleted
argument_list|(
operator|new
name|DeletedObject
argument_list|(
name|keyToDelete
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
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
literal|"KEY_NOT_FOUND"
argument_list|)
condition|)
block|{
name|result
operator|.
name|addError
argument_list|(
operator|new
name|Error
argument_list|(
name|keyToDelete
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"InternalError"
argument_list|,
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|request
operator|.
name|isQuiet
argument_list|()
condition|)
block|{
name|result
operator|.
name|addDeleted
argument_list|(
operator|new
name|DeletedObject
argument_list|(
name|keyToDelete
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|result
operator|.
name|addError
argument_list|(
operator|new
name|Error
argument_list|(
name|keyToDelete
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"InternalError"
argument_list|,
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|result
return|;
block|}
DECL|method|addKey (ListObjectResponse response, OzoneKey next)
specifier|private
name|void
name|addKey
parameter_list|(
name|ListObjectResponse
name|response
parameter_list|,
name|OzoneKey
name|next
parameter_list|)
block|{
name|KeyMetadata
name|keyMetadata
init|=
operator|new
name|KeyMetadata
argument_list|()
decl_stmt|;
name|keyMetadata
operator|.
name|setKey
argument_list|(
name|next
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|keyMetadata
operator|.
name|setSize
argument_list|(
name|next
operator|.
name|getDataSize
argument_list|()
argument_list|)
expr_stmt|;
name|keyMetadata
operator|.
name|setETag
argument_list|(
literal|""
operator|+
name|next
operator|.
name|getModificationTime
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|next
operator|.
name|getReplicationType
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
name|ReplicationType
operator|.
name|STAND_ALONE
operator|.
name|toString
argument_list|()
argument_list|)
condition|)
block|{
name|keyMetadata
operator|.
name|setStorageClass
argument_list|(
name|S3StorageType
operator|.
name|REDUCED_REDUNDANCY
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|keyMetadata
operator|.
name|setStorageClass
argument_list|(
name|S3StorageType
operator|.
name|STANDARD
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|keyMetadata
operator|.
name|setLastModified
argument_list|(
name|Instant
operator|.
name|ofEpochMilli
argument_list|(
name|next
operator|.
name|getModificationTime
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|response
operator|.
name|addKey
argument_list|(
name|keyMetadata
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

