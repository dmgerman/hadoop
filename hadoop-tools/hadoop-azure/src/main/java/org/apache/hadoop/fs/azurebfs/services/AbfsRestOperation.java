begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.azurebfs.services
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|azurebfs
operator|.
name|services
package|;
end_package

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
name|net
operator|.
name|HttpURLConnection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
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
name|fs
operator|.
name|azurebfs
operator|.
name|constants
operator|.
name|AbfsHttpConstants
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
name|azurebfs
operator|.
name|contracts
operator|.
name|exceptions
operator|.
name|AbfsRestOperationException
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
name|azurebfs
operator|.
name|contracts
operator|.
name|exceptions
operator|.
name|AzureBlobFileSystemException
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
name|azurebfs
operator|.
name|contracts
operator|.
name|exceptions
operator|.
name|InvalidAbfsRestOperationException
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
name|azurebfs
operator|.
name|constants
operator|.
name|HttpHeaderConfigurations
import|;
end_import

begin_comment
comment|/**  * The AbfsRestOperation for Rest AbfsClient.  */
end_comment

begin_class
DECL|class|AbfsRestOperation
specifier|public
class|class
name|AbfsRestOperation
block|{
comment|// Blob FS client, which has the credentials, retry policy, and logs.
DECL|field|client
specifier|private
specifier|final
name|AbfsClient
name|client
decl_stmt|;
comment|// the HTTP method (PUT, PATCH, POST, GET, HEAD, or DELETE)
DECL|field|method
specifier|private
specifier|final
name|String
name|method
decl_stmt|;
comment|// full URL including query parameters
DECL|field|url
specifier|private
specifier|final
name|URL
name|url
decl_stmt|;
comment|// all the custom HTTP request headers provided by the caller
DECL|field|requestHeaders
specifier|private
specifier|final
name|List
argument_list|<
name|AbfsHttpHeader
argument_list|>
name|requestHeaders
decl_stmt|;
comment|// This is a simple operation class, where all the upload methods have a
comment|// request body and all the download methods have a response body.
DECL|field|hasRequestBody
specifier|private
specifier|final
name|boolean
name|hasRequestBody
decl_stmt|;
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
name|AbfsClient
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// For uploads, this is the request entity body.  For downloads,
comment|// this will hold the response entity body.
DECL|field|buffer
specifier|private
name|byte
index|[]
name|buffer
decl_stmt|;
DECL|field|bufferOffset
specifier|private
name|int
name|bufferOffset
decl_stmt|;
DECL|field|bufferLength
specifier|private
name|int
name|bufferLength
decl_stmt|;
DECL|field|result
specifier|private
name|AbfsHttpOperation
name|result
decl_stmt|;
DECL|method|getResult ()
specifier|public
name|AbfsHttpOperation
name|getResult
parameter_list|()
block|{
return|return
name|result
return|;
block|}
comment|/**    * Initializes a new REST operation.    *    * @param client The Blob FS client.    * @param method The HTTP method (PUT, PATCH, POST, GET, HEAD, or DELETE).    * @param url The full URL including query string parameters.    * @param requestHeaders The HTTP request headers.    */
DECL|method|AbfsRestOperation (final AbfsClient client, final String method, final URL url, final List<AbfsHttpHeader> requestHeaders)
name|AbfsRestOperation
parameter_list|(
specifier|final
name|AbfsClient
name|client
parameter_list|,
specifier|final
name|String
name|method
parameter_list|,
specifier|final
name|URL
name|url
parameter_list|,
specifier|final
name|List
argument_list|<
name|AbfsHttpHeader
argument_list|>
name|requestHeaders
parameter_list|)
block|{
name|this
operator|.
name|client
operator|=
name|client
expr_stmt|;
name|this
operator|.
name|method
operator|=
name|method
expr_stmt|;
name|this
operator|.
name|url
operator|=
name|url
expr_stmt|;
name|this
operator|.
name|requestHeaders
operator|=
name|requestHeaders
expr_stmt|;
name|this
operator|.
name|hasRequestBody
operator|=
operator|(
name|AbfsHttpConstants
operator|.
name|HTTP_METHOD_PUT
operator|.
name|equals
argument_list|(
name|method
argument_list|)
operator|||
name|AbfsHttpConstants
operator|.
name|HTTP_METHOD_PATCH
operator|.
name|equals
argument_list|(
name|method
argument_list|)
operator|)
expr_stmt|;
block|}
comment|/**    * Initializes a new REST operation.    *    * @param client The Blob FS client.    * @param method The HTTP method (PUT, PATCH, POST, GET, HEAD, or DELETE).    * @param url The full URL including query string parameters.    * @param requestHeaders The HTTP request headers.    * @param buffer For uploads, this is the request entity body.  For downloads,    *               this will hold the response entity body.    * @param bufferOffset An offset into the buffer where the data beings.    * @param bufferLength The length of the data in the buffer.    */
DECL|method|AbfsRestOperation (AbfsClient client, String method, URL url, List<AbfsHttpHeader> requestHeaders, byte[] buffer, int bufferOffset, int bufferLength)
name|AbfsRestOperation
parameter_list|(
name|AbfsClient
name|client
parameter_list|,
name|String
name|method
parameter_list|,
name|URL
name|url
parameter_list|,
name|List
argument_list|<
name|AbfsHttpHeader
argument_list|>
name|requestHeaders
parameter_list|,
name|byte
index|[]
name|buffer
parameter_list|,
name|int
name|bufferOffset
parameter_list|,
name|int
name|bufferLength
parameter_list|)
block|{
name|this
argument_list|(
name|client
argument_list|,
name|method
argument_list|,
name|url
argument_list|,
name|requestHeaders
argument_list|)
expr_stmt|;
name|this
operator|.
name|buffer
operator|=
name|buffer
expr_stmt|;
name|this
operator|.
name|bufferOffset
operator|=
name|bufferOffset
expr_stmt|;
name|this
operator|.
name|bufferLength
operator|=
name|bufferLength
expr_stmt|;
block|}
comment|/**    * Executes the REST operation with retry, by issuing one or more    * HTTP operations.    */
DECL|method|execute ()
name|void
name|execute
parameter_list|()
throws|throws
name|AzureBlobFileSystemException
block|{
name|int
name|retryCount
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|!
name|executeHttpOperation
argument_list|(
name|retryCount
operator|++
argument_list|)
condition|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|client
operator|.
name|getRetryPolicy
argument_list|()
operator|.
name|getRetryInterval
argument_list|(
name|retryCount
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ex
parameter_list|)
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|result
operator|.
name|getStatusCode
argument_list|()
operator|>=
name|HttpURLConnection
operator|.
name|HTTP_BAD_REQUEST
condition|)
block|{
throw|throw
operator|new
name|AbfsRestOperationException
argument_list|(
name|result
operator|.
name|getStatusCode
argument_list|()
argument_list|,
name|result
operator|.
name|getStorageErrorCode
argument_list|()
argument_list|,
name|result
operator|.
name|getStorageErrorMessage
argument_list|()
argument_list|,
literal|null
argument_list|,
name|result
argument_list|)
throw|;
block|}
block|}
comment|/**    * Executes a single HTTP operation to complete the REST operation.  If it    * fails, there may be a retry.  The retryCount is incremented with each    * attempt.    */
DECL|method|executeHttpOperation (final int retryCount)
specifier|private
name|boolean
name|executeHttpOperation
parameter_list|(
specifier|final
name|int
name|retryCount
parameter_list|)
throws|throws
name|AzureBlobFileSystemException
block|{
name|AbfsHttpOperation
name|httpOperation
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|// initialize the HTTP request and open the connection
name|httpOperation
operator|=
operator|new
name|AbfsHttpOperation
argument_list|(
name|url
argument_list|,
name|method
argument_list|,
name|requestHeaders
argument_list|)
expr_stmt|;
comment|// sign the HTTP request
if|if
condition|(
name|client
operator|.
name|getAccessToken
argument_list|()
operator|==
literal|null
condition|)
block|{
comment|// sign the HTTP request
name|client
operator|.
name|getSharedKeyCredentials
argument_list|()
operator|.
name|signRequest
argument_list|(
name|httpOperation
operator|.
name|getConnection
argument_list|()
argument_list|,
name|hasRequestBody
condition|?
name|bufferLength
else|:
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|httpOperation
operator|.
name|getConnection
argument_list|()
operator|.
name|setRequestProperty
argument_list|(
name|HttpHeaderConfigurations
operator|.
name|AUTHORIZATION
argument_list|,
name|client
operator|.
name|getAccessToken
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|hasRequestBody
condition|)
block|{
comment|// HttpUrlConnection requires
name|httpOperation
operator|.
name|sendRequest
argument_list|(
name|buffer
argument_list|,
name|bufferOffset
argument_list|,
name|bufferLength
argument_list|)
expr_stmt|;
block|}
name|httpOperation
operator|.
name|processResponse
argument_list|(
name|buffer
argument_list|,
name|bufferOffset
argument_list|,
name|bufferLength
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
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
if|if
condition|(
name|httpOperation
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"HttpRequestFailure: "
operator|+
name|httpOperation
operator|.
name|toString
argument_list|()
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"HttpRequestFailure: "
operator|+
name|method
operator|+
literal|","
operator|+
name|url
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|client
operator|.
name|getRetryPolicy
argument_list|()
operator|.
name|shouldRetry
argument_list|(
name|retryCount
argument_list|,
operator|-
literal|1
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|InvalidAbfsRestOperationException
argument_list|(
name|ex
argument_list|)
throw|;
block|}
return|return
literal|false
return|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"HttpRequest: "
operator|+
name|httpOperation
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|client
operator|.
name|getRetryPolicy
argument_list|()
operator|.
name|shouldRetry
argument_list|(
name|retryCount
argument_list|,
name|httpOperation
operator|.
name|getStatusCode
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|result
operator|=
name|httpOperation
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

