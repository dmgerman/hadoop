begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  *  Licensed to the Apache Software Foundation (ASF) under one  *  or more contributor license agreements.  See the NOTICE file  *  distributed with this work for additional information  *  regarding copyright ownership.  The ASF licenses this file  *  to you under the Apache License, Version 2.0 (the  *  "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  *  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.web.handlers
package|package
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
name|handlers
package|;
end_package

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
name|OzoneConsts
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
name|OzoneRestUtils
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
name|rest
operator|.
name|OzoneException
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
name|rest
operator|.
name|headers
operator|.
name|Header
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
name|exceptions
operator|.
name|ErrorTable
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
name|interfaces
operator|.
name|Bucket
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
name|interfaces
operator|.
name|StorageHandler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|MDC
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
name|Request
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
name|UriInfo
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
import|import static
name|java
operator|.
name|net
operator|.
name|HttpURLConnection
operator|.
name|HTTP_CREATED
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|net
operator|.
name|HttpURLConnection
operator|.
name|HTTP_OK
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
name|OzoneConsts
operator|.
name|OZONE_FUNCTION
import|;
end_import

begin_comment
comment|/**  * Bucket Class handles all ozone Bucket related actions.  */
end_comment

begin_class
DECL|class|BucketHandler
specifier|public
class|class
name|BucketHandler
implements|implements
name|Bucket
block|{
comment|/**    * createBucket call handles the POST request for Creating a Bucket.    *    * @param volume - Volume name    * @param bucket - Bucket Name    * @param req - Http request    * @param info - Uri Info    * @param headers - Http headers    *    * @return Response    *    * @throws OzoneException    */
annotation|@
name|Override
DECL|method|createBucket (String volume, String bucket, Request req, UriInfo info, HttpHeaders headers)
specifier|public
name|Response
name|createBucket
parameter_list|(
name|String
name|volume
parameter_list|,
name|String
name|bucket
parameter_list|,
name|Request
name|req
parameter_list|,
name|UriInfo
name|info
parameter_list|,
name|HttpHeaders
name|headers
parameter_list|)
throws|throws
name|OzoneException
block|{
name|MDC
operator|.
name|put
argument_list|(
name|OZONE_FUNCTION
argument_list|,
literal|"createBucket"
argument_list|)
expr_stmt|;
return|return
operator|new
name|BucketProcessTemplate
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Response
name|doProcess
parameter_list|(
name|BucketArgs
name|args
parameter_list|)
throws|throws
name|OzoneException
throws|,
name|IOException
block|{
name|StorageHandler
name|fs
init|=
name|StorageHandlerBuilder
operator|.
name|getStorageHandler
argument_list|()
decl_stmt|;
name|args
operator|.
name|setVersioning
argument_list|(
name|getVersioning
argument_list|(
name|args
argument_list|)
argument_list|)
expr_stmt|;
name|args
operator|.
name|setStorageType
argument_list|(
name|getStorageType
argument_list|(
name|args
argument_list|)
argument_list|)
expr_stmt|;
name|fs
operator|.
name|createBucket
argument_list|(
name|args
argument_list|)
expr_stmt|;
return|return
name|OzoneRestUtils
operator|.
name|getResponse
argument_list|(
name|args
argument_list|,
name|HTTP_CREATED
argument_list|,
literal|""
argument_list|)
return|;
block|}
block|}
operator|.
name|handleCall
argument_list|(
name|volume
argument_list|,
name|bucket
argument_list|,
name|req
argument_list|,
name|info
argument_list|,
name|headers
argument_list|)
return|;
block|}
comment|/**    * updateBucket call handles the PUT request for updating a Bucket.    *    * There are only three possible actions currently with updateBucket.    * They are add/remove on ACLS, Bucket Versioning and  StorageType.    *  if you make a call with any other action, update just returns 200 OK.    *    * @param volume - Storage volume name    * @param bucket - Bucket name    * @param req - Http request    * @param info - Uri Info    * @param headers - Http headers    *    * @return Response    *    * @throws OzoneException    */
annotation|@
name|Override
DECL|method|updateBucket (String volume, String bucket, Request req, UriInfo info, HttpHeaders headers)
specifier|public
name|Response
name|updateBucket
parameter_list|(
name|String
name|volume
parameter_list|,
name|String
name|bucket
parameter_list|,
name|Request
name|req
parameter_list|,
name|UriInfo
name|info
parameter_list|,
name|HttpHeaders
name|headers
parameter_list|)
throws|throws
name|OzoneException
block|{
name|MDC
operator|.
name|put
argument_list|(
name|OZONE_FUNCTION
argument_list|,
literal|"updateBucket"
argument_list|)
expr_stmt|;
return|return
operator|new
name|BucketProcessTemplate
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Response
name|doProcess
parameter_list|(
name|BucketArgs
name|args
parameter_list|)
throws|throws
name|OzoneException
throws|,
name|IOException
block|{
name|StorageHandler
name|fs
init|=
name|StorageHandlerBuilder
operator|.
name|getStorageHandler
argument_list|()
decl_stmt|;
name|args
operator|.
name|setVersioning
argument_list|(
name|getVersioning
argument_list|(
name|args
argument_list|)
argument_list|)
expr_stmt|;
name|args
operator|.
name|setStorageType
argument_list|(
name|getStorageType
argument_list|(
name|args
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|args
operator|.
name|getVersioning
argument_list|()
operator|!=
name|OzoneConsts
operator|.
name|Versioning
operator|.
name|NOT_DEFINED
condition|)
block|{
name|fs
operator|.
name|setBucketVersioning
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|args
operator|.
name|getStorageType
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|fs
operator|.
name|setBucketStorageClass
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
return|return
name|OzoneRestUtils
operator|.
name|getResponse
argument_list|(
name|args
argument_list|,
name|HTTP_OK
argument_list|,
literal|""
argument_list|)
return|;
block|}
block|}
operator|.
name|handleCall
argument_list|(
name|volume
argument_list|,
name|bucket
argument_list|,
name|req
argument_list|,
name|info
argument_list|,
name|headers
argument_list|)
return|;
block|}
comment|/**    * Deletes an empty bucket.    *    * @param volume Volume name    * @param bucket Bucket Name    * @param req - Http request    * @param info - Uri Info    * @param headers - Http headers    *    * @return Response    *    * @throws OzoneException    */
annotation|@
name|Override
DECL|method|deleteBucket (String volume, String bucket, Request req, UriInfo info, HttpHeaders headers)
specifier|public
name|Response
name|deleteBucket
parameter_list|(
name|String
name|volume
parameter_list|,
name|String
name|bucket
parameter_list|,
name|Request
name|req
parameter_list|,
name|UriInfo
name|info
parameter_list|,
name|HttpHeaders
name|headers
parameter_list|)
throws|throws
name|OzoneException
block|{
name|MDC
operator|.
name|put
argument_list|(
name|OZONE_FUNCTION
argument_list|,
literal|"deleteBucket"
argument_list|)
expr_stmt|;
return|return
operator|new
name|BucketProcessTemplate
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Response
name|doProcess
parameter_list|(
name|BucketArgs
name|args
parameter_list|)
throws|throws
name|OzoneException
throws|,
name|IOException
block|{
name|StorageHandler
name|fs
init|=
name|StorageHandlerBuilder
operator|.
name|getStorageHandler
argument_list|()
decl_stmt|;
name|fs
operator|.
name|deleteBucket
argument_list|(
name|args
argument_list|)
expr_stmt|;
return|return
name|OzoneRestUtils
operator|.
name|getResponse
argument_list|(
name|args
argument_list|,
name|HTTP_OK
argument_list|,
literal|""
argument_list|)
return|;
block|}
block|}
operator|.
name|handleCall
argument_list|(
name|volume
argument_list|,
name|bucket
argument_list|,
name|req
argument_list|,
name|info
argument_list|,
name|headers
argument_list|)
return|;
block|}
comment|/**    * List Buckets allows the user to list the bucket.    *    * @param volume - Storage Volume Name    * @param bucket - Bucket Name    * @param info - Uri Info    * @param prefix - Prefix for the keys to be fetched    * @param maxKeys - MaxNumber of Keys to Return    * @param startPage - Continuation Token    * @param req - Http request    * @param headers - Http headers    *    * @return - Json Body    *    * @throws OzoneException    */
annotation|@
name|Override
DECL|method|listBucket (String volume, String bucket, final String info, final String prefix, final int maxKeys, final String startPage, Request req, UriInfo uriInfo, HttpHeaders headers)
specifier|public
name|Response
name|listBucket
parameter_list|(
name|String
name|volume
parameter_list|,
name|String
name|bucket
parameter_list|,
specifier|final
name|String
name|info
parameter_list|,
specifier|final
name|String
name|prefix
parameter_list|,
specifier|final
name|int
name|maxKeys
parameter_list|,
specifier|final
name|String
name|startPage
parameter_list|,
name|Request
name|req
parameter_list|,
name|UriInfo
name|uriInfo
parameter_list|,
name|HttpHeaders
name|headers
parameter_list|)
throws|throws
name|OzoneException
block|{
name|MDC
operator|.
name|put
argument_list|(
name|OZONE_FUNCTION
argument_list|,
literal|"listBucket"
argument_list|)
expr_stmt|;
return|return
operator|new
name|BucketProcessTemplate
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Response
name|doProcess
parameter_list|(
name|BucketArgs
name|args
parameter_list|)
throws|throws
name|OzoneException
throws|,
name|IOException
block|{
switch|switch
condition|(
name|info
condition|)
block|{
case|case
name|Header
operator|.
name|OZONE_INFO_QUERY_KEY
case|:
name|ListArgs
name|listArgs
init|=
operator|new
name|ListArgs
argument_list|(
name|args
argument_list|,
name|prefix
argument_list|,
name|maxKeys
argument_list|,
name|startPage
argument_list|)
decl_stmt|;
return|return
name|getBucketKeysList
argument_list|(
name|listArgs
argument_list|)
return|;
case|case
name|Header
operator|.
name|OZONE_INFO_QUERY_BUCKET
case|:
return|return
name|getBucketInfoResponse
argument_list|(
name|args
argument_list|)
return|;
default|default:
name|OzoneException
name|ozException
init|=
name|ErrorTable
operator|.
name|newError
argument_list|(
name|ErrorTable
operator|.
name|INVALID_QUERY_PARAM
argument_list|,
name|args
argument_list|)
decl_stmt|;
name|ozException
operator|.
name|setMessage
argument_list|(
literal|"Unrecognized query param : "
operator|+
name|info
argument_list|)
expr_stmt|;
throw|throw
name|ozException
throw|;
block|}
block|}
block|}
operator|.
name|handleCall
argument_list|(
name|volume
argument_list|,
name|bucket
argument_list|,
name|req
argument_list|,
name|uriInfo
argument_list|,
name|headers
argument_list|)
return|;
block|}
block|}
end_class

end_unit

