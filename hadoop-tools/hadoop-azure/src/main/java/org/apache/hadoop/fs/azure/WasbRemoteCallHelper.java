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
name|ClientProtocolException
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
name|impl
operator|.
name|client
operator|.
name|HttpClientBuilder
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
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
import|;
end_import

begin_comment
comment|/**  * Helper class the has constants and helper methods  * used in WASB when integrating with a remote http cred  * service. Currently, remote service will be used to generate  * SAS keys.  */
end_comment

begin_class
DECL|class|WasbRemoteCallHelper
class|class
name|WasbRemoteCallHelper
block|{
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
comment|/**    * Client instance to be used for making the remote call.    */
DECL|field|client
specifier|private
name|HttpClient
name|client
init|=
literal|null
decl_stmt|;
annotation|@
name|VisibleForTesting
DECL|method|updateHttpClient (HttpClient client)
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
DECL|method|WasbRemoteCallHelper ()
specifier|public
name|WasbRemoteCallHelper
parameter_list|()
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
block|}
comment|/**    * Helper method to make remote HTTP Get request.    * @param getRequest - HttpGet request object constructed by caller.    * @return Http Response body returned as a string. The caller    *  is expected to semantically understand the response.    * @throws WasbRemoteCallException    * @throws IOException    */
DECL|method|makeRemoteGetRequest (HttpGet getRequest)
specifier|public
name|String
name|makeRemoteGetRequest
parameter_list|(
name|HttpGet
name|getRequest
parameter_list|)
throws|throws
name|WasbRemoteCallException
throws|,
name|IOException
block|{
try|try
block|{
specifier|final
name|String
name|APPLICATION_JSON
init|=
literal|"application/json"
decl_stmt|;
specifier|final
name|int
name|MAX_CONTENT_LENGTH
init|=
literal|1024
decl_stmt|;
name|getRequest
operator|.
name|setHeader
argument_list|(
literal|"Accept"
argument_list|,
name|APPLICATION_JSON
argument_list|)
expr_stmt|;
name|HttpResponse
name|response
init|=
name|client
operator|.
name|execute
argument_list|(
name|getRequest
argument_list|)
decl_stmt|;
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
throw|throw
operator|new
name|WasbRemoteCallException
argument_list|(
name|getRequest
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
name|contentTypeHeader
operator|.
name|getValue
argument_list|()
operator|!=
name|APPLICATION_JSON
condition|)
block|{
throw|throw
operator|new
name|WasbRemoteCallException
argument_list|(
name|getRequest
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
name|getRequest
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
name|getRequest
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
name|getRequest
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
decl_stmt|;
name|StringBuilder
name|responseBody
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
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
name|rd
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|responseBody
operator|.
name|toString
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|ClientProtocolException
name|clientProtocolEx
parameter_list|)
block|{
throw|throw
operator|new
name|WasbRemoteCallException
argument_list|(
name|getRequest
operator|.
name|getURI
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|":"
operator|+
literal|"Encountered ClientProtocolException while making remote call"
argument_list|,
name|clientProtocolEx
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioEx
parameter_list|)
block|{
throw|throw
operator|new
name|WasbRemoteCallException
argument_list|(
name|getRequest
operator|.
name|getURI
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|":"
operator|+
literal|"Encountered IOException while making remote call"
argument_list|,
name|ioEx
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

