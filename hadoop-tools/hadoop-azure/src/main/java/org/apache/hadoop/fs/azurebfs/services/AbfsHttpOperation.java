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
name|io
operator|.
name|InputStream
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
name|java
operator|.
name|util
operator|.
name|UUID
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|net
operator|.
name|ssl
operator|.
name|HttpsURLConnection
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|net
operator|.
name|ssl
operator|.
name|SSLSocketFactory
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
name|utils
operator|.
name|SSLSocketFactoryEx
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jackson
operator|.
name|JsonFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jackson
operator|.
name|JsonParser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jackson
operator|.
name|JsonToken
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jackson
operator|.
name|map
operator|.
name|ObjectMapper
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
name|constants
operator|.
name|HttpHeaderConfigurations
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
name|services
operator|.
name|ListResultSchema
import|;
end_import

begin_comment
comment|/**  * Represents an HTTP operation.  */
end_comment

begin_class
DECL|class|AbfsHttpOperation
specifier|public
class|class
name|AbfsHttpOperation
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
name|AbfsHttpOperation
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|CONNECT_TIMEOUT
specifier|private
specifier|static
specifier|final
name|int
name|CONNECT_TIMEOUT
init|=
literal|30
operator|*
literal|1000
decl_stmt|;
DECL|field|READ_TIMEOUT
specifier|private
specifier|static
specifier|final
name|int
name|READ_TIMEOUT
init|=
literal|30
operator|*
literal|1000
decl_stmt|;
DECL|field|CLEAN_UP_BUFFER_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|CLEAN_UP_BUFFER_SIZE
init|=
literal|64
operator|*
literal|1024
decl_stmt|;
DECL|field|ONE_THOUSAND
specifier|private
specifier|static
specifier|final
name|int
name|ONE_THOUSAND
init|=
literal|1000
decl_stmt|;
DECL|field|ONE_MILLION
specifier|private
specifier|static
specifier|final
name|int
name|ONE_MILLION
init|=
name|ONE_THOUSAND
operator|*
name|ONE_THOUSAND
decl_stmt|;
DECL|field|method
specifier|private
specifier|final
name|String
name|method
decl_stmt|;
DECL|field|url
specifier|private
specifier|final
name|URL
name|url
decl_stmt|;
DECL|field|connection
specifier|private
name|HttpURLConnection
name|connection
decl_stmt|;
DECL|field|statusCode
specifier|private
name|int
name|statusCode
decl_stmt|;
DECL|field|statusDescription
specifier|private
name|String
name|statusDescription
decl_stmt|;
DECL|field|storageErrorCode
specifier|private
name|String
name|storageErrorCode
init|=
literal|""
decl_stmt|;
DECL|field|storageErrorMessage
specifier|private
name|String
name|storageErrorMessage
init|=
literal|""
decl_stmt|;
DECL|field|clientRequestId
specifier|private
name|String
name|clientRequestId
init|=
literal|""
decl_stmt|;
DECL|field|requestId
specifier|private
name|String
name|requestId
init|=
literal|""
decl_stmt|;
DECL|field|listResultSchema
specifier|private
name|ListResultSchema
name|listResultSchema
init|=
literal|null
decl_stmt|;
comment|// metrics
DECL|field|bytesSent
specifier|private
name|int
name|bytesSent
decl_stmt|;
DECL|field|bytesReceived
specifier|private
name|long
name|bytesReceived
decl_stmt|;
comment|// optional trace enabled metrics
DECL|field|isTraceEnabled
specifier|private
specifier|final
name|boolean
name|isTraceEnabled
decl_stmt|;
DECL|field|connectionTimeMs
specifier|private
name|long
name|connectionTimeMs
decl_stmt|;
DECL|field|sendRequestTimeMs
specifier|private
name|long
name|sendRequestTimeMs
decl_stmt|;
DECL|field|recvResponseTimeMs
specifier|private
name|long
name|recvResponseTimeMs
decl_stmt|;
DECL|method|getConnection ()
specifier|protected
name|HttpURLConnection
name|getConnection
parameter_list|()
block|{
return|return
name|connection
return|;
block|}
DECL|method|getMethod ()
specifier|public
name|String
name|getMethod
parameter_list|()
block|{
return|return
name|method
return|;
block|}
DECL|method|getUrl ()
specifier|public
name|URL
name|getUrl
parameter_list|()
block|{
return|return
name|url
return|;
block|}
DECL|method|getStatusCode ()
specifier|public
name|int
name|getStatusCode
parameter_list|()
block|{
return|return
name|statusCode
return|;
block|}
DECL|method|getStatusDescription ()
specifier|public
name|String
name|getStatusDescription
parameter_list|()
block|{
return|return
name|statusDescription
return|;
block|}
DECL|method|getStorageErrorCode ()
specifier|public
name|String
name|getStorageErrorCode
parameter_list|()
block|{
return|return
name|storageErrorCode
return|;
block|}
DECL|method|getStorageErrorMessage ()
specifier|public
name|String
name|getStorageErrorMessage
parameter_list|()
block|{
return|return
name|storageErrorMessage
return|;
block|}
DECL|method|getClientRequestId ()
specifier|public
name|String
name|getClientRequestId
parameter_list|()
block|{
return|return
name|clientRequestId
return|;
block|}
DECL|method|getRequestId ()
specifier|public
name|String
name|getRequestId
parameter_list|()
block|{
return|return
name|requestId
return|;
block|}
DECL|method|getBytesSent ()
specifier|public
name|int
name|getBytesSent
parameter_list|()
block|{
return|return
name|bytesSent
return|;
block|}
DECL|method|getBytesReceived ()
specifier|public
name|long
name|getBytesReceived
parameter_list|()
block|{
return|return
name|bytesReceived
return|;
block|}
DECL|method|getListResultSchema ()
specifier|public
name|ListResultSchema
name|getListResultSchema
parameter_list|()
block|{
return|return
name|listResultSchema
return|;
block|}
DECL|method|getResponseHeader (String httpHeader)
specifier|public
name|String
name|getResponseHeader
parameter_list|(
name|String
name|httpHeader
parameter_list|)
block|{
return|return
name|connection
operator|.
name|getHeaderField
argument_list|(
name|httpHeader
argument_list|)
return|;
block|}
comment|// Returns a trace message for the request
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
specifier|final
name|String
name|urlStr
init|=
name|url
operator|.
name|toString
argument_list|()
decl_stmt|;
specifier|final
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|statusCode
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|","
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|storageErrorCode
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|",cid="
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|clientRequestId
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|",rid="
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|requestId
argument_list|)
expr_stmt|;
if|if
condition|(
name|isTraceEnabled
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|",connMs="
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|connectionTimeMs
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|",sendMs="
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|sendRequestTimeMs
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|",recvMs="
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|recvResponseTimeMs
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|",sent="
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|bytesSent
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|",recv="
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|bytesReceived
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|","
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|method
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|","
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|urlStr
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Initializes a new HTTP request and opens the connection.    *    * @param url The full URL including query string parameters.    * @param method The HTTP method (PUT, PATCH, POST, GET, HEAD, or DELETE).    * @param requestHeaders The HTTP request headers.READ_TIMEOUT    *    * @throws IOException if an error occurs.    */
DECL|method|AbfsHttpOperation (final URL url, final String method, final List<AbfsHttpHeader> requestHeaders)
specifier|public
name|AbfsHttpOperation
parameter_list|(
specifier|final
name|URL
name|url
parameter_list|,
specifier|final
name|String
name|method
parameter_list|,
specifier|final
name|List
argument_list|<
name|AbfsHttpHeader
argument_list|>
name|requestHeaders
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|isTraceEnabled
operator|=
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
expr_stmt|;
name|this
operator|.
name|url
operator|=
name|url
expr_stmt|;
name|this
operator|.
name|method
operator|=
name|method
expr_stmt|;
name|this
operator|.
name|clientRequestId
operator|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
name|this
operator|.
name|connection
operator|=
name|openConnection
argument_list|()
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|connection
operator|instanceof
name|HttpsURLConnection
condition|)
block|{
name|HttpsURLConnection
name|secureConn
init|=
operator|(
name|HttpsURLConnection
operator|)
name|this
operator|.
name|connection
decl_stmt|;
name|SSLSocketFactory
name|sslSocketFactory
init|=
name|SSLSocketFactoryEx
operator|.
name|getDefaultFactory
argument_list|()
decl_stmt|;
if|if
condition|(
name|sslSocketFactory
operator|!=
literal|null
condition|)
block|{
name|secureConn
operator|.
name|setSSLSocketFactory
argument_list|(
name|sslSocketFactory
argument_list|)
expr_stmt|;
block|}
block|}
name|this
operator|.
name|connection
operator|.
name|setConnectTimeout
argument_list|(
name|CONNECT_TIMEOUT
argument_list|)
expr_stmt|;
name|this
operator|.
name|connection
operator|.
name|setReadTimeout
argument_list|(
name|READ_TIMEOUT
argument_list|)
expr_stmt|;
name|this
operator|.
name|connection
operator|.
name|setRequestMethod
argument_list|(
name|method
argument_list|)
expr_stmt|;
for|for
control|(
name|AbfsHttpHeader
name|header
range|:
name|requestHeaders
control|)
block|{
name|this
operator|.
name|connection
operator|.
name|setRequestProperty
argument_list|(
name|header
operator|.
name|getName
argument_list|()
argument_list|,
name|header
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|connection
operator|.
name|setRequestProperty
argument_list|(
name|HttpHeaderConfigurations
operator|.
name|X_MS_CLIENT_REQUEST_ID
argument_list|,
name|clientRequestId
argument_list|)
expr_stmt|;
block|}
comment|/**    * Sends the HTTP request.  Note that HttpUrlConnection requires that an    * empty buffer be sent in order to set the "Content-Length: 0" header, which    * is required by our endpoint.    *    * @param buffer the request entity body.    * @param offset an offset into the buffer where the data beings.    * @param length the length of the data in the buffer.    *    * @throws IOException if an error occurs.    */
DECL|method|sendRequest (byte[] buffer, int offset, int length)
specifier|public
name|void
name|sendRequest
parameter_list|(
name|byte
index|[]
name|buffer
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|connection
operator|.
name|setDoOutput
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|this
operator|.
name|connection
operator|.
name|setFixedLengthStreamingMode
argument_list|(
name|length
argument_list|)
expr_stmt|;
if|if
condition|(
name|buffer
operator|==
literal|null
condition|)
block|{
comment|// An empty buffer is sent to set the "Content-Length: 0" header, which
comment|// is required by our endpoint.
name|buffer
operator|=
operator|new
name|byte
index|[]
block|{}
expr_stmt|;
name|offset
operator|=
literal|0
expr_stmt|;
name|length
operator|=
literal|0
expr_stmt|;
block|}
comment|// send the request body
name|long
name|startTime
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|isTraceEnabled
condition|)
block|{
name|startTime
operator|=
name|System
operator|.
name|nanoTime
argument_list|()
expr_stmt|;
block|}
try|try
init|(
name|OutputStream
name|outputStream
init|=
name|this
operator|.
name|connection
operator|.
name|getOutputStream
argument_list|()
init|)
block|{
comment|// update bytes sent before they are sent so we may observe
comment|// attempted sends as well as successful sends via the
comment|// accompanying statusCode
name|this
operator|.
name|bytesSent
operator|=
name|length
expr_stmt|;
name|outputStream
operator|.
name|write
argument_list|(
name|buffer
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|this
operator|.
name|isTraceEnabled
condition|)
block|{
name|this
operator|.
name|sendRequestTimeMs
operator|=
name|elapsedTimeMs
argument_list|(
name|startTime
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Gets and processes the HTTP response.    *    * @param buffer a buffer to hold the response entity body    * @param offset an offset in the buffer where the data will being.    * @param length the number of bytes to be written to the buffer.    *    * @throws IOException if an error occurs.    */
DECL|method|processResponse (final byte[] buffer, final int offset, final int length)
specifier|public
name|void
name|processResponse
parameter_list|(
specifier|final
name|byte
index|[]
name|buffer
parameter_list|,
specifier|final
name|int
name|offset
parameter_list|,
specifier|final
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
comment|// get the response
name|long
name|startTime
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|isTraceEnabled
condition|)
block|{
name|startTime
operator|=
name|System
operator|.
name|nanoTime
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|statusCode
operator|=
name|this
operator|.
name|connection
operator|.
name|getResponseCode
argument_list|()
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|isTraceEnabled
condition|)
block|{
name|this
operator|.
name|recvResponseTimeMs
operator|=
name|elapsedTimeMs
argument_list|(
name|startTime
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|statusDescription
operator|=
name|this
operator|.
name|connection
operator|.
name|getResponseMessage
argument_list|()
expr_stmt|;
name|this
operator|.
name|requestId
operator|=
name|this
operator|.
name|connection
operator|.
name|getHeaderField
argument_list|(
name|HttpHeaderConfigurations
operator|.
name|X_MS_REQUEST_ID
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|requestId
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|requestId
operator|=
name|AbfsHttpConstants
operator|.
name|EMPTY_STRING
expr_stmt|;
block|}
if|if
condition|(
name|AbfsHttpConstants
operator|.
name|HTTP_METHOD_HEAD
operator|.
name|equals
argument_list|(
name|this
operator|.
name|method
argument_list|)
condition|)
block|{
comment|// If it is HEAD, and it is ERROR
return|return;
block|}
if|if
condition|(
name|this
operator|.
name|isTraceEnabled
condition|)
block|{
name|startTime
operator|=
name|System
operator|.
name|nanoTime
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|statusCode
operator|>=
name|HttpURLConnection
operator|.
name|HTTP_BAD_REQUEST
condition|)
block|{
name|processStorageErrorResponse
argument_list|()
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|isTraceEnabled
condition|)
block|{
name|this
operator|.
name|recvResponseTimeMs
operator|+=
name|elapsedTimeMs
argument_list|(
name|startTime
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|bytesReceived
operator|=
name|this
operator|.
name|connection
operator|.
name|getHeaderFieldLong
argument_list|(
name|HttpHeaderConfigurations
operator|.
name|CONTENT_LENGTH
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// consume the input stream to release resources
name|int
name|totalBytesRead
init|=
literal|0
decl_stmt|;
try|try
init|(
name|InputStream
name|stream
init|=
name|this
operator|.
name|connection
operator|.
name|getInputStream
argument_list|()
init|)
block|{
if|if
condition|(
name|isNullInputStream
argument_list|(
name|stream
argument_list|)
condition|)
block|{
return|return;
block|}
name|boolean
name|endOfStream
init|=
literal|false
decl_stmt|;
comment|// this is a list operation and need to retrieve the data
comment|// need a better solution
if|if
condition|(
name|AbfsHttpConstants
operator|.
name|HTTP_METHOD_GET
operator|.
name|equals
argument_list|(
name|this
operator|.
name|method
argument_list|)
operator|&&
name|buffer
operator|==
literal|null
condition|)
block|{
name|parseListFilesResponse
argument_list|(
name|stream
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|buffer
operator|!=
literal|null
condition|)
block|{
while|while
condition|(
name|totalBytesRead
operator|<
name|length
condition|)
block|{
name|int
name|bytesRead
init|=
name|stream
operator|.
name|read
argument_list|(
name|buffer
argument_list|,
name|offset
operator|+
name|totalBytesRead
argument_list|,
name|length
operator|-
name|totalBytesRead
argument_list|)
decl_stmt|;
if|if
condition|(
name|bytesRead
operator|==
operator|-
literal|1
condition|)
block|{
name|endOfStream
operator|=
literal|true
expr_stmt|;
break|break;
block|}
name|totalBytesRead
operator|+=
name|bytesRead
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|endOfStream
operator|&&
name|stream
operator|.
name|read
argument_list|()
operator|!=
operator|-
literal|1
condition|)
block|{
comment|// read and discard
name|int
name|bytesRead
init|=
literal|0
decl_stmt|;
name|byte
index|[]
name|b
init|=
operator|new
name|byte
index|[
name|CLEAN_UP_BUFFER_SIZE
index|]
decl_stmt|;
while|while
condition|(
operator|(
name|bytesRead
operator|=
name|stream
operator|.
name|read
argument_list|(
name|b
argument_list|)
operator|)
operator|>=
literal|0
condition|)
block|{
name|totalBytesRead
operator|+=
name|bytesRead
expr_stmt|;
block|}
block|}
block|}
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
literal|"UnexpectedError: "
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
name|this
operator|.
name|isTraceEnabled
condition|)
block|{
name|this
operator|.
name|recvResponseTimeMs
operator|+=
name|elapsedTimeMs
argument_list|(
name|startTime
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|bytesReceived
operator|=
name|totalBytesRead
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Open the HTTP connection.    *    * @throws IOException if an error occurs.    */
DECL|method|openConnection ()
specifier|private
name|HttpURLConnection
name|openConnection
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|isTraceEnabled
condition|)
block|{
return|return
operator|(
name|HttpURLConnection
operator|)
name|url
operator|.
name|openConnection
argument_list|()
return|;
block|}
name|long
name|start
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
try|try
block|{
return|return
operator|(
name|HttpURLConnection
operator|)
name|url
operator|.
name|openConnection
argument_list|()
return|;
block|}
finally|finally
block|{
name|connectionTimeMs
operator|=
name|elapsedTimeMs
argument_list|(
name|start
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * When the request fails, this function is used to parse the responseAbfsHttpClient.LOG.debug("ExpectedError: ", ex);    * and extract the storageErrorCode and storageErrorMessage.  Any errors    * encountered while attempting to process the error response are logged,    * but otherwise ignored.    *    * For storage errors, the response body *usually* has the following format:    *    * {    *   "error":    *   {    *     "code": "string",    *     "message": "string"    *   }    * }    *    */
DECL|method|processStorageErrorResponse ()
specifier|private
name|void
name|processStorageErrorResponse
parameter_list|()
block|{
try|try
init|(
name|InputStream
name|stream
init|=
name|connection
operator|.
name|getErrorStream
argument_list|()
init|)
block|{
if|if
condition|(
name|stream
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|JsonFactory
name|jf
init|=
operator|new
name|JsonFactory
argument_list|()
decl_stmt|;
try|try
init|(
name|JsonParser
name|jp
init|=
name|jf
operator|.
name|createJsonParser
argument_list|(
name|stream
argument_list|)
init|)
block|{
name|String
name|fieldName
decl_stmt|,
name|fieldValue
decl_stmt|;
name|jp
operator|.
name|nextToken
argument_list|()
expr_stmt|;
comment|// START_OBJECT - {
name|jp
operator|.
name|nextToken
argument_list|()
expr_stmt|;
comment|// FIELD_NAME - "error":
name|jp
operator|.
name|nextToken
argument_list|()
expr_stmt|;
comment|// START_OBJECT - {
name|jp
operator|.
name|nextToken
argument_list|()
expr_stmt|;
while|while
condition|(
name|jp
operator|.
name|hasCurrentToken
argument_list|()
condition|)
block|{
if|if
condition|(
name|jp
operator|.
name|getCurrentToken
argument_list|()
operator|==
name|JsonToken
operator|.
name|FIELD_NAME
condition|)
block|{
name|fieldName
operator|=
name|jp
operator|.
name|getCurrentName
argument_list|()
expr_stmt|;
name|jp
operator|.
name|nextToken
argument_list|()
expr_stmt|;
name|fieldValue
operator|=
name|jp
operator|.
name|getText
argument_list|()
expr_stmt|;
switch|switch
condition|(
name|fieldName
condition|)
block|{
case|case
literal|"code"
case|:
name|storageErrorCode
operator|=
name|fieldValue
expr_stmt|;
break|break;
case|case
literal|"message"
case|:
name|storageErrorMessage
operator|=
name|fieldValue
expr_stmt|;
break|break;
default|default:
break|break;
block|}
block|}
name|jp
operator|.
name|nextToken
argument_list|()
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
comment|// Ignore errors that occur while attempting to parse the storage
comment|// error, since the response may have been handled by the HTTP driver
comment|// or for other reasons have an unexpected
name|LOG
operator|.
name|debug
argument_list|(
literal|"ExpectedError: "
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Returns the elapsed time in milliseconds.    */
DECL|method|elapsedTimeMs (final long startTime)
specifier|private
name|long
name|elapsedTimeMs
parameter_list|(
specifier|final
name|long
name|startTime
parameter_list|)
block|{
return|return
operator|(
name|System
operator|.
name|nanoTime
argument_list|()
operator|-
name|startTime
operator|)
operator|/
name|ONE_MILLION
return|;
block|}
comment|/**    * Parse the list file response    *    * @param stream InputStream contains the list results.    * @throws IOException    */
DECL|method|parseListFilesResponse (final InputStream stream)
specifier|private
name|void
name|parseListFilesResponse
parameter_list|(
specifier|final
name|InputStream
name|stream
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|stream
operator|==
literal|null
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|listResultSchema
operator|!=
literal|null
condition|)
block|{
comment|// already parse the response
return|return;
block|}
try|try
block|{
specifier|final
name|ObjectMapper
name|objectMapper
init|=
operator|new
name|ObjectMapper
argument_list|()
decl_stmt|;
name|this
operator|.
name|listResultSchema
operator|=
name|objectMapper
operator|.
name|readValue
argument_list|(
name|stream
argument_list|,
name|ListResultSchema
operator|.
name|class
argument_list|)
expr_stmt|;
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
literal|"Unable to deserialize list results"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
throw|throw
name|ex
throw|;
block|}
block|}
comment|/**    * Check null stream, this is to pass findbugs's redundant check for NULL    * @param stream InputStream    */
DECL|method|isNullInputStream (InputStream stream)
specifier|private
name|boolean
name|isNullInputStream
parameter_list|(
name|InputStream
name|stream
parameter_list|)
block|{
return|return
name|stream
operator|==
literal|null
condition|?
literal|true
else|:
literal|false
return|;
block|}
block|}
end_class

end_unit

