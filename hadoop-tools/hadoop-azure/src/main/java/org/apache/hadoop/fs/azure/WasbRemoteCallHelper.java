begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.azure
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|azure
package|;
end_package

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
name|hadoop
operator|.
name|fs
operator|.
name|azure
operator|.
name|security
operator|.
name|Constants
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
name|io
operator|.
name|retry
operator|.
name|RetryPolicy
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
name|Header
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
name|HttpResponse
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
name|apache
operator|.
name|http
operator|.
name|NameValuePair
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
name|StatusLine
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
name|client
operator|.
name|HttpClient
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
name|client
operator|.
name|methods
operator|.
name|HttpGet
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
name|client
operator|.
name|methods
operator|.
name|HttpPost
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
name|client
operator|.
name|methods
operator|.
name|HttpPut
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
name|client
operator|.
name|methods
operator|.
name|HttpUriRequest
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
name|client
operator|.
name|utils
operator|.
name|URIBuilder
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
name|impl
operator|.
name|client
operator|.
name|HttpClientBuilder
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
name|java
operator|.
name|io
operator|.
name|BufferedReader
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
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InterruptedIOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
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
name|Random
import|;
end_import

begin_comment
comment|/**  * Helper class the has constants and helper methods  * used in WASB when integrating with a remote http cred  * service. Currently, remote service will be used to generate  * SAS keys.  */
end_comment

begin_class
DECL|class|WasbRemoteCallHelper
specifier|public
class|class
name|WasbRemoteCallHelper
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
name|WasbRemoteCallHelper
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Return code when the remote call is successful. {@value}    */
DECL|field|REMOTE_CALL_SUCCESS_CODE
specifier|public
specifier|static
specifier|final
name|int
name|REMOTE_CALL_SUCCESS_CODE
init|=
literal|0
decl_stmt|;
comment|/**    * Application Json content type.    */
DECL|field|APPLICATION_JSON
specifier|private
specifier|static
specifier|final
name|String
name|APPLICATION_JSON
init|=
literal|"application/json"
decl_stmt|;
comment|/**    * Max content length of the response.    */
DECL|field|MAX_CONTENT_LENGTH
specifier|private
specifier|static
specifier|final
name|int
name|MAX_CONTENT_LENGTH
init|=
literal|1024
decl_stmt|;
comment|/**    * Client instance to be used for making the remote call.    */
DECL|field|client
specifier|private
name|HttpClient
name|client
init|=
literal|null
decl_stmt|;
DECL|field|random
specifier|private
name|Random
name|random
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
DECL|field|retryPolicy
specifier|private
name|RetryPolicy
name|retryPolicy
init|=
literal|null
decl_stmt|;
DECL|method|WasbRemoteCallHelper (RetryPolicy retryPolicy)
specifier|public
name|WasbRemoteCallHelper
parameter_list|(
name|RetryPolicy
name|retryPolicy
parameter_list|)
block|{
name|this
operator|.
name|client
operator|=
name|HttpClientBuilder
operator|.
name|create
argument_list|()
operator|.
name|build
argument_list|()
expr_stmt|;
name|this
operator|.
name|retryPolicy
operator|=
name|retryPolicy
expr_stmt|;
block|}
DECL|method|updateHttpClient (HttpClient client)
annotation|@
name|VisibleForTesting
specifier|public
name|void
name|updateHttpClient
parameter_list|(
name|HttpClient
name|client
parameter_list|)
block|{
name|this
operator|.
name|client
operator|=
name|client
expr_stmt|;
block|}
comment|/**    * Helper method to make remote HTTP Get request.    *    * @param urls        - Service urls to be used, if one fails try another.    * @param path        - URL endpoint for the resource.    * @param queryParams - list of query parameters    * @param httpMethod  - http Method to be used.    * @return Http Response body returned as a string. The caller    * is expected to semantically understand the response.    * @throws IOException when there an error in executing the remote http request.    */
DECL|method|makeRemoteRequest (String[] urls, String path, List<NameValuePair> queryParams, String httpMethod)
specifier|public
name|String
name|makeRemoteRequest
parameter_list|(
name|String
index|[]
name|urls
parameter_list|,
name|String
name|path
parameter_list|,
name|List
argument_list|<
name|NameValuePair
argument_list|>
name|queryParams
parameter_list|,
name|String
name|httpMethod
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|retryableRequest
argument_list|(
name|urls
argument_list|,
name|path
argument_list|,
name|queryParams
argument_list|,
name|httpMethod
argument_list|)
return|;
block|}
DECL|method|retryableRequest (String[] urls, String path, List<NameValuePair> queryParams, String httpMethod)
specifier|protected
name|String
name|retryableRequest
parameter_list|(
name|String
index|[]
name|urls
parameter_list|,
name|String
name|path
parameter_list|,
name|List
argument_list|<
name|NameValuePair
argument_list|>
name|queryParams
parameter_list|,
name|String
name|httpMethod
parameter_list|)
throws|throws
name|IOException
block|{
name|HttpResponse
name|response
init|=
literal|null
decl_stmt|;
name|HttpUriRequest
name|httpRequest
init|=
literal|null
decl_stmt|;
comment|/**      * Get the index of local url if any. If list of urls contains strings like      * "https://localhost:" or "http://localhost", consider it as local url and      * give it affinity more than other urls in the list.      */
name|int
name|indexOfLocalUrl
init|=
operator|-
literal|1
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
name|urls
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|urls
index|[
name|i
index|]
operator|.
name|toLowerCase
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"https://localhost:"
argument_list|)
operator|||
name|urls
index|[
name|i
index|]
operator|.
name|toLowerCase
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"http://localhost:"
argument_list|)
condition|)
block|{
name|indexOfLocalUrl
operator|=
name|i
expr_stmt|;
block|}
block|}
name|boolean
name|requiresNewAuth
init|=
literal|false
decl_stmt|;
for|for
control|(
name|int
name|retry
init|=
literal|0
init|,
name|index
init|=
operator|(
name|indexOfLocalUrl
operator|!=
operator|-
literal|1
operator|)
condition|?
name|indexOfLocalUrl
else|:
name|random
operator|.
name|nextInt
argument_list|(
name|urls
operator|.
name|length
argument_list|)
init|;
condition|;
name|retry
operator|++
operator|,
name|index
operator|++
control|)
block|{
if|if
condition|(
name|index
operator|>=
name|urls
operator|.
name|length
condition|)
block|{
name|index
operator|=
name|index
operator|%
name|urls
operator|.
name|length
expr_stmt|;
block|}
comment|/**        * If the first request fails to localhost, then randomly pick the next url        * from the remaining urls in the list, so that load can be balanced.        */
if|if
condition|(
name|indexOfLocalUrl
operator|!=
operator|-
literal|1
operator|&&
name|retry
operator|==
literal|1
condition|)
block|{
name|index
operator|=
operator|(
name|index
operator|+
name|random
operator|.
name|nextInt
argument_list|(
name|urls
operator|.
name|length
argument_list|)
operator|)
operator|%
name|urls
operator|.
name|length
expr_stmt|;
if|if
condition|(
name|index
operator|==
name|indexOfLocalUrl
condition|)
block|{
name|index
operator|=
operator|(
name|index
operator|+
literal|1
operator|)
operator|%
name|urls
operator|.
name|length
expr_stmt|;
block|}
block|}
try|try
block|{
name|httpRequest
operator|=
name|getHttpRequest
argument_list|(
name|urls
argument_list|,
name|path
argument_list|,
name|queryParams
argument_list|,
name|index
argument_list|,
name|httpMethod
argument_list|,
name|requiresNewAuth
argument_list|)
expr_stmt|;
name|httpRequest
operator|.
name|setHeader
argument_list|(
literal|"Accept"
argument_list|,
name|APPLICATION_JSON
argument_list|)
expr_stmt|;
name|response
operator|=
name|client
operator|.
name|execute
argument_list|(
name|httpRequest
argument_list|)
expr_stmt|;
name|StatusLine
name|statusLine
init|=
name|response
operator|.
name|getStatusLine
argument_list|()
decl_stmt|;
if|if
condition|(
name|statusLine
operator|==
literal|null
operator|||
name|statusLine
operator|.
name|getStatusCode
argument_list|()
operator|!=
name|HttpStatus
operator|.
name|SC_OK
condition|)
block|{
name|requiresNewAuth
operator|=
operator|(
name|statusLine
operator|==
literal|null
operator|)
operator|||
operator|(
name|statusLine
operator|.
name|getStatusCode
argument_list|()
operator|==
name|HttpStatus
operator|.
name|SC_UNAUTHORIZED
operator|)
expr_stmt|;
throw|throw
operator|new
name|WasbRemoteCallException
argument_list|(
name|httpRequest
operator|.
name|getURI
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|":"
operator|+
operator|(
operator|(
name|statusLine
operator|!=
literal|null
operator|)
condition|?
name|statusLine
operator|.
name|toString
argument_list|()
else|:
literal|"NULL"
operator|)
argument_list|)
throw|;
block|}
else|else
block|{
name|requiresNewAuth
operator|=
literal|false
expr_stmt|;
block|}
name|Header
name|contentTypeHeader
init|=
name|response
operator|.
name|getFirstHeader
argument_list|(
literal|"Content-Type"
argument_list|)
decl_stmt|;
if|if
condition|(
name|contentTypeHeader
operator|==
literal|null
operator|||
operator|!
name|APPLICATION_JSON
operator|.
name|equals
argument_list|(
name|contentTypeHeader
operator|.
name|getValue
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|WasbRemoteCallException
argument_list|(
name|httpRequest
operator|.
name|getURI
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|":"
operator|+
literal|"Content-Type mismatch: expected: "
operator|+
name|APPLICATION_JSON
operator|+
literal|", got "
operator|+
operator|(
operator|(
name|contentTypeHeader
operator|!=
literal|null
operator|)
condition|?
name|contentTypeHeader
operator|.
name|getValue
argument_list|()
else|:
literal|"NULL"
operator|)
argument_list|)
throw|;
block|}
name|Header
name|contentLengthHeader
init|=
name|response
operator|.
name|getFirstHeader
argument_list|(
literal|"Content-Length"
argument_list|)
decl_stmt|;
if|if
condition|(
name|contentLengthHeader
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|WasbRemoteCallException
argument_list|(
name|httpRequest
operator|.
name|getURI
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|":"
operator|+
literal|"Content-Length header missing"
argument_list|)
throw|;
block|}
try|try
block|{
if|if
condition|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|contentLengthHeader
operator|.
name|getValue
argument_list|()
argument_list|)
operator|>
name|MAX_CONTENT_LENGTH
condition|)
block|{
throw|throw
operator|new
name|WasbRemoteCallException
argument_list|(
name|httpRequest
operator|.
name|getURI
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|":"
operator|+
literal|"Content-Length:"
operator|+
name|contentLengthHeader
operator|.
name|getValue
argument_list|()
operator|+
literal|"exceeded max:"
operator|+
name|MAX_CONTENT_LENGTH
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|nfe
parameter_list|)
block|{
throw|throw
operator|new
name|WasbRemoteCallException
argument_list|(
name|httpRequest
operator|.
name|getURI
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|":"
operator|+
literal|"Invalid Content-Length value :"
operator|+
name|contentLengthHeader
operator|.
name|getValue
argument_list|()
argument_list|)
throw|;
block|}
name|BufferedReader
name|rd
init|=
literal|null
decl_stmt|;
name|StringBuilder
name|responseBody
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
try|try
block|{
name|rd
operator|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|response
operator|.
name|getEntity
argument_list|()
operator|.
name|getContent
argument_list|()
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|responseLine
init|=
literal|""
decl_stmt|;
while|while
condition|(
operator|(
name|responseLine
operator|=
name|rd
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|responseBody
operator|.
name|append
argument_list|(
name|responseLine
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|rd
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|responseBody
operator|.
name|toString
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|uriSyntaxEx
parameter_list|)
block|{
throw|throw
operator|new
name|WasbRemoteCallException
argument_list|(
literal|"Encountered URISyntaxException "
operator|+
literal|"while building the HttpGetRequest to remote service"
argument_list|,
name|uriSyntaxEx
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
try|try
block|{
name|shouldRetry
argument_list|(
name|e
argument_list|,
name|retry
argument_list|,
operator|(
name|httpRequest
operator|!=
literal|null
operator|)
condition|?
name|httpRequest
operator|.
name|getURI
argument_list|()
operator|.
name|toString
argument_list|()
else|:
name|urls
index|[
name|index
index|]
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioex
parameter_list|)
block|{
name|String
name|message
init|=
literal|"Encountered error while making remote call to "
operator|+
name|String
operator|.
name|join
argument_list|(
literal|","
argument_list|,
name|urls
argument_list|)
operator|+
literal|" retried "
operator|+
name|retry
operator|+
literal|" time(s)."
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|message
argument_list|,
name|ioex
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|WasbRemoteCallException
argument_list|(
name|message
argument_list|,
name|ioex
argument_list|)
throw|;
block|}
block|}
block|}
block|}
DECL|method|getHttpRequest (String[] urls, String path, List<NameValuePair> queryParams, int urlIndex, String httpMethod, boolean requiresNewAuth)
specifier|protected
name|HttpUriRequest
name|getHttpRequest
parameter_list|(
name|String
index|[]
name|urls
parameter_list|,
name|String
name|path
parameter_list|,
name|List
argument_list|<
name|NameValuePair
argument_list|>
name|queryParams
parameter_list|,
name|int
name|urlIndex
parameter_list|,
name|String
name|httpMethod
parameter_list|,
name|boolean
name|requiresNewAuth
parameter_list|)
throws|throws
name|URISyntaxException
throws|,
name|IOException
block|{
name|URIBuilder
name|uriBuilder
init|=
literal|null
decl_stmt|;
name|uriBuilder
operator|=
operator|new
name|URIBuilder
argument_list|(
name|urls
index|[
name|urlIndex
index|]
argument_list|)
operator|.
name|setPath
argument_list|(
name|path
argument_list|)
operator|.
name|setParameters
argument_list|(
name|queryParams
argument_list|)
expr_stmt|;
if|if
condition|(
name|uriBuilder
operator|.
name|getHost
argument_list|()
operator|.
name|equals
argument_list|(
literal|"localhost"
argument_list|)
condition|)
block|{
name|uriBuilder
operator|.
name|setHost
argument_list|(
name|InetAddress
operator|.
name|getLocalHost
argument_list|()
operator|.
name|getCanonicalHostName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|HttpUriRequest
name|httpUriRequest
init|=
literal|null
decl_stmt|;
switch|switch
condition|(
name|httpMethod
condition|)
block|{
case|case
name|HttpPut
operator|.
name|METHOD_NAME
case|:
name|httpUriRequest
operator|=
operator|new
name|HttpPut
argument_list|(
name|uriBuilder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|HttpPost
operator|.
name|METHOD_NAME
case|:
name|httpUriRequest
operator|=
operator|new
name|HttpPost
argument_list|(
name|uriBuilder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
break|break;
default|default:
name|httpUriRequest
operator|=
operator|new
name|HttpGet
argument_list|(
name|uriBuilder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
break|break;
block|}
return|return
name|httpUriRequest
return|;
block|}
DECL|method|shouldRetry (final IOException ioe, final int retry, final String url)
specifier|private
name|void
name|shouldRetry
parameter_list|(
specifier|final
name|IOException
name|ioe
parameter_list|,
specifier|final
name|int
name|retry
parameter_list|,
specifier|final
name|String
name|url
parameter_list|)
throws|throws
name|IOException
block|{
name|CharSequence
name|authenticationExceptionMessage
init|=
name|Constants
operator|.
name|AUTHENTICATION_FAILED_ERROR_MESSAGE
decl_stmt|;
if|if
condition|(
name|ioe
operator|instanceof
name|WasbRemoteCallException
operator|&&
name|ioe
operator|.
name|getMessage
argument_list|()
operator|.
name|equals
argument_list|(
name|authenticationExceptionMessage
argument_list|)
condition|)
block|{
throw|throw
name|ioe
throw|;
block|}
try|try
block|{
specifier|final
name|RetryPolicy
operator|.
name|RetryAction
name|a
init|=
operator|(
name|retryPolicy
operator|!=
literal|null
operator|)
condition|?
name|retryPolicy
operator|.
name|shouldRetry
argument_list|(
name|ioe
argument_list|,
name|retry
argument_list|,
literal|0
argument_list|,
literal|true
argument_list|)
else|:
name|RetryPolicy
operator|.
name|RetryAction
operator|.
name|FAIL
decl_stmt|;
name|boolean
name|isRetry
init|=
name|a
operator|.
name|action
operator|==
name|RetryPolicy
operator|.
name|RetryAction
operator|.
name|RetryDecision
operator|.
name|RETRY
decl_stmt|;
name|boolean
name|isFailoverAndRetry
init|=
name|a
operator|.
name|action
operator|==
name|RetryPolicy
operator|.
name|RetryAction
operator|.
name|RetryDecision
operator|.
name|FAILOVER_AND_RETRY
decl_stmt|;
if|if
condition|(
name|isRetry
operator|||
name|isFailoverAndRetry
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Retrying connect to Remote service:{}. Already tried {}"
operator|+
literal|" time(s); retry policy is {}, "
operator|+
literal|"delay {}ms."
argument_list|,
name|url
argument_list|,
name|retry
argument_list|,
name|retryPolicy
argument_list|,
name|a
operator|.
name|delayMillis
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|a
operator|.
name|delayMillis
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedIOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
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
return|return;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Original exception is "
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|WasbRemoteCallException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"Not retrying anymore, already retried the urls {} time(s)"
argument_list|,
name|retry
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|WasbRemoteCallException
argument_list|(
name|url
operator|+
literal|":"
operator|+
literal|"Encountered IOException while making remote call"
argument_list|,
name|ioe
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

