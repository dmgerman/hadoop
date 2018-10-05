begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.azurebfs.oauth2
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
name|oauth2
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
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Hashtable
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
name|services
operator|.
name|ExponentialRetryPolicy
import|;
end_import

begin_comment
comment|/**  * This class provides convenience methods to obtain AAD tokens.  * While convenient, it is not necessary to use these methods to  * obtain the tokens. Customers can use any other method  * (e.g., using the adal4j client) to obtain tokens.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|AzureADAuthenticator
specifier|public
specifier|final
class|class
name|AzureADAuthenticator
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
name|AzureADAuthenticator
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|RESOURCE_NAME
specifier|private
specifier|static
specifier|final
name|String
name|RESOURCE_NAME
init|=
literal|"https://storage.azure.com/"
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
DECL|method|AzureADAuthenticator ()
specifier|private
name|AzureADAuthenticator
parameter_list|()
block|{
comment|// no operation
block|}
comment|/**    * gets Azure Active Directory token using the user ID and password of    * a service principal (that is, Web App in Azure Active Directory).    *    * Azure Active Directory allows users to set up a web app as a    * service principal. Users can optionally obtain service principal keys    * from AAD. This method gets a token using a service principal's client ID    * and keys. In addition, it needs the token endpoint associated with the    * user's directory.    *    *    * @param authEndpoint the OAuth 2.0 token endpoint associated    *                     with the user's directory (obtain from    *                     Active Directory configuration)    * @param clientId     the client ID (GUID) of the client web app    *                     btained from Azure Active Directory configuration    * @param clientSecret the secret key of the client web app    * @return {@link AzureADToken} obtained using the creds    * @throws IOException throws IOException if there is a failure in connecting to Azure AD    */
DECL|method|getTokenUsingClientCreds (String authEndpoint, String clientId, String clientSecret)
specifier|public
specifier|static
name|AzureADToken
name|getTokenUsingClientCreds
parameter_list|(
name|String
name|authEndpoint
parameter_list|,
name|String
name|clientId
parameter_list|,
name|String
name|clientSecret
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|authEndpoint
argument_list|,
literal|"authEndpoint"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|clientId
argument_list|,
literal|"clientId"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|clientSecret
argument_list|,
literal|"clientSecret"
argument_list|)
expr_stmt|;
name|QueryParams
name|qp
init|=
operator|new
name|QueryParams
argument_list|()
decl_stmt|;
name|qp
operator|.
name|add
argument_list|(
literal|"resource"
argument_list|,
name|RESOURCE_NAME
argument_list|)
expr_stmt|;
name|qp
operator|.
name|add
argument_list|(
literal|"grant_type"
argument_list|,
literal|"client_credentials"
argument_list|)
expr_stmt|;
name|qp
operator|.
name|add
argument_list|(
literal|"client_id"
argument_list|,
name|clientId
argument_list|)
expr_stmt|;
name|qp
operator|.
name|add
argument_list|(
literal|"client_secret"
argument_list|,
name|clientSecret
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"AADToken: starting to fetch token using client creds for client ID "
operator|+
name|clientId
argument_list|)
expr_stmt|;
return|return
name|getTokenCall
argument_list|(
name|authEndpoint
argument_list|,
name|qp
operator|.
name|serialize
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/**    * Gets AAD token from the local virtual machine's VM extension. This only works on    * an Azure VM with MSI extension    * enabled.    *    * @param tenantGuid  (optional) The guid of the AAD tenant. Can be {@code null}.    * @param clientId    (optional) The clientId guid of the MSI service    *                    principal to use. Can be {@code null}.    * @param bypassCache {@code boolean} specifying whether a cached token is acceptable or a fresh token    *                    request should me made to AAD    * @return {@link AzureADToken} obtained using the creds    * @throws IOException throws IOException if there is a failure in obtaining the token    */
DECL|method|getTokenFromMsi (String tenantGuid, String clientId, boolean bypassCache)
specifier|public
specifier|static
name|AzureADToken
name|getTokenFromMsi
parameter_list|(
name|String
name|tenantGuid
parameter_list|,
name|String
name|clientId
parameter_list|,
name|boolean
name|bypassCache
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|tenantGuid
argument_list|,
literal|"tenantGuid"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|clientId
argument_list|,
literal|"clientId"
argument_list|)
expr_stmt|;
name|String
name|authEndpoint
init|=
literal|"http://169.254.169.254/metadata/identity/oauth2/token"
decl_stmt|;
name|QueryParams
name|qp
init|=
operator|new
name|QueryParams
argument_list|()
decl_stmt|;
name|qp
operator|.
name|add
argument_list|(
literal|"api-version"
argument_list|,
literal|"2018-02-01"
argument_list|)
expr_stmt|;
name|qp
operator|.
name|add
argument_list|(
literal|"resource"
argument_list|,
name|RESOURCE_NAME
argument_list|)
expr_stmt|;
if|if
condition|(
name|tenantGuid
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|String
name|authority
init|=
literal|"https://login.microsoftonline.com/"
operator|+
name|tenantGuid
decl_stmt|;
name|qp
operator|.
name|add
argument_list|(
literal|"authority"
argument_list|,
name|authority
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|clientId
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|qp
operator|.
name|add
argument_list|(
literal|"client_id"
argument_list|,
name|clientId
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|bypassCache
condition|)
block|{
name|qp
operator|.
name|add
argument_list|(
literal|"bypass_cache"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
block|}
name|Hashtable
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|headers
init|=
operator|new
name|Hashtable
argument_list|<>
argument_list|()
decl_stmt|;
name|headers
operator|.
name|put
argument_list|(
literal|"Metadata"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"AADToken: starting to fetch token using MSI"
argument_list|)
expr_stmt|;
return|return
name|getTokenCall
argument_list|(
name|authEndpoint
argument_list|,
name|qp
operator|.
name|serialize
argument_list|()
argument_list|,
name|headers
argument_list|,
literal|"GET"
argument_list|)
return|;
block|}
comment|/**    * Gets Azure Active Directory token using refresh token.    *    * @param clientId the client ID (GUID) of the client web app obtained from Azure Active Directory configuration    * @param refreshToken the refresh token    * @return {@link AzureADToken} obtained using the refresh token    * @throws IOException throws IOException if there is a failure in connecting to Azure AD    */
DECL|method|getTokenUsingRefreshToken (String clientId, String refreshToken)
specifier|public
specifier|static
name|AzureADToken
name|getTokenUsingRefreshToken
parameter_list|(
name|String
name|clientId
parameter_list|,
name|String
name|refreshToken
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|authEndpoint
init|=
literal|"https://login.microsoftonline.com/Common/oauth2/token"
decl_stmt|;
name|QueryParams
name|qp
init|=
operator|new
name|QueryParams
argument_list|()
decl_stmt|;
name|qp
operator|.
name|add
argument_list|(
literal|"grant_type"
argument_list|,
literal|"refresh_token"
argument_list|)
expr_stmt|;
name|qp
operator|.
name|add
argument_list|(
literal|"refresh_token"
argument_list|,
name|refreshToken
argument_list|)
expr_stmt|;
if|if
condition|(
name|clientId
operator|!=
literal|null
condition|)
block|{
name|qp
operator|.
name|add
argument_list|(
literal|"client_id"
argument_list|,
name|clientId
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"AADToken: starting to fetch token using refresh token for client ID "
operator|+
name|clientId
argument_list|)
expr_stmt|;
return|return
name|getTokenCall
argument_list|(
name|authEndpoint
argument_list|,
name|qp
operator|.
name|serialize
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/**    * This exception class contains the http error code,    * requestId and error message, it is thrown when AzureADAuthenticator    * failed to get the Azure Active Directory token.    */
DECL|class|HttpException
specifier|public
specifier|static
class|class
name|HttpException
extends|extends
name|IOException
block|{
DECL|field|httpErrorCode
specifier|private
name|int
name|httpErrorCode
decl_stmt|;
DECL|field|requestId
specifier|private
name|String
name|requestId
decl_stmt|;
comment|/**      * Gets Http error status code.      * @return  http error code.      */
DECL|method|getHttpErrorCode ()
specifier|public
name|int
name|getHttpErrorCode
parameter_list|()
block|{
return|return
name|this
operator|.
name|httpErrorCode
return|;
block|}
comment|/**      * Gets http request id .      * @return  http request id.      */
DECL|method|getRequestId ()
specifier|public
name|String
name|getRequestId
parameter_list|()
block|{
return|return
name|this
operator|.
name|requestId
return|;
block|}
DECL|method|HttpException (int httpErrorCode, String requestId, String message)
name|HttpException
parameter_list|(
name|int
name|httpErrorCode
parameter_list|,
name|String
name|requestId
parameter_list|,
name|String
name|message
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|this
operator|.
name|httpErrorCode
operator|=
name|httpErrorCode
expr_stmt|;
name|this
operator|.
name|requestId
operator|=
name|requestId
expr_stmt|;
block|}
block|}
DECL|method|getTokenCall (String authEndpoint, String body, Hashtable<String, String> headers, String httpMethod)
specifier|private
specifier|static
name|AzureADToken
name|getTokenCall
parameter_list|(
name|String
name|authEndpoint
parameter_list|,
name|String
name|body
parameter_list|,
name|Hashtable
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|headers
parameter_list|,
name|String
name|httpMethod
parameter_list|)
throws|throws
name|IOException
block|{
name|AzureADToken
name|token
init|=
literal|null
decl_stmt|;
name|ExponentialRetryPolicy
name|retryPolicy
init|=
operator|new
name|ExponentialRetryPolicy
argument_list|(
literal|3
argument_list|,
literal|0
argument_list|,
literal|1000
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|int
name|httperror
init|=
literal|0
decl_stmt|;
name|IOException
name|ex
init|=
literal|null
decl_stmt|;
name|boolean
name|succeeded
init|=
literal|false
decl_stmt|;
name|int
name|retryCount
init|=
literal|0
decl_stmt|;
do|do
block|{
name|httperror
operator|=
literal|0
expr_stmt|;
name|ex
operator|=
literal|null
expr_stmt|;
try|try
block|{
name|token
operator|=
name|getTokenSingleCall
argument_list|(
name|authEndpoint
argument_list|,
name|body
argument_list|,
name|headers
argument_list|,
name|httpMethod
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|HttpException
name|e
parameter_list|)
block|{
name|httperror
operator|=
name|e
operator|.
name|httpErrorCode
expr_stmt|;
name|ex
operator|=
name|e
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|ex
operator|=
name|e
expr_stmt|;
block|}
name|succeeded
operator|=
operator|(
operator|(
name|httperror
operator|==
literal|0
operator|)
operator|&&
operator|(
name|ex
operator|==
literal|null
operator|)
operator|)
expr_stmt|;
name|retryCount
operator|++
expr_stmt|;
block|}
do|while
condition|(
operator|!
name|succeeded
operator|&&
name|retryPolicy
operator|.
name|shouldRetry
argument_list|(
name|retryCount
argument_list|,
name|httperror
argument_list|)
condition|)
do|;
if|if
condition|(
operator|!
name|succeeded
condition|)
block|{
throw|throw
name|ex
throw|;
block|}
return|return
name|token
return|;
block|}
DECL|method|getTokenSingleCall ( String authEndpoint, String payload, Hashtable<String, String> headers, String httpMethod)
specifier|private
specifier|static
name|AzureADToken
name|getTokenSingleCall
parameter_list|(
name|String
name|authEndpoint
parameter_list|,
name|String
name|payload
parameter_list|,
name|Hashtable
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|headers
parameter_list|,
name|String
name|httpMethod
parameter_list|)
throws|throws
name|IOException
block|{
name|AzureADToken
name|token
init|=
literal|null
decl_stmt|;
name|HttpURLConnection
name|conn
init|=
literal|null
decl_stmt|;
name|String
name|urlString
init|=
name|authEndpoint
decl_stmt|;
name|httpMethod
operator|=
operator|(
name|httpMethod
operator|==
literal|null
operator|)
condition|?
literal|"POST"
else|:
name|httpMethod
expr_stmt|;
if|if
condition|(
name|httpMethod
operator|.
name|equals
argument_list|(
literal|"GET"
argument_list|)
condition|)
block|{
name|urlString
operator|=
name|urlString
operator|+
literal|"?"
operator|+
name|payload
expr_stmt|;
block|}
try|try
block|{
name|URL
name|url
init|=
operator|new
name|URL
argument_list|(
name|urlString
argument_list|)
decl_stmt|;
name|conn
operator|=
operator|(
name|HttpURLConnection
operator|)
name|url
operator|.
name|openConnection
argument_list|()
expr_stmt|;
name|conn
operator|.
name|setRequestMethod
argument_list|(
name|httpMethod
argument_list|)
expr_stmt|;
name|conn
operator|.
name|setReadTimeout
argument_list|(
name|READ_TIMEOUT
argument_list|)
expr_stmt|;
name|conn
operator|.
name|setConnectTimeout
argument_list|(
name|CONNECT_TIMEOUT
argument_list|)
expr_stmt|;
if|if
condition|(
name|headers
operator|!=
literal|null
operator|&&
name|headers
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|headers
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|conn
operator|.
name|setRequestProperty
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|conn
operator|.
name|setRequestProperty
argument_list|(
literal|"Connection"
argument_list|,
literal|"close"
argument_list|)
expr_stmt|;
if|if
condition|(
name|httpMethod
operator|.
name|equals
argument_list|(
literal|"POST"
argument_list|)
condition|)
block|{
name|conn
operator|.
name|setDoOutput
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|conn
operator|.
name|getOutputStream
argument_list|()
operator|.
name|write
argument_list|(
name|payload
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|int
name|httpResponseCode
init|=
name|conn
operator|.
name|getResponseCode
argument_list|()
decl_stmt|;
name|String
name|requestId
init|=
name|conn
operator|.
name|getHeaderField
argument_list|(
literal|"x-ms-request-id"
argument_list|)
decl_stmt|;
name|String
name|responseContentType
init|=
name|conn
operator|.
name|getHeaderField
argument_list|(
literal|"Content-Type"
argument_list|)
decl_stmt|;
name|long
name|responseContentLength
init|=
name|conn
operator|.
name|getHeaderFieldLong
argument_list|(
literal|"Content-Length"
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|requestId
operator|=
name|requestId
operator|==
literal|null
condition|?
literal|""
else|:
name|requestId
expr_stmt|;
if|if
condition|(
name|httpResponseCode
operator|==
name|HttpURLConnection
operator|.
name|HTTP_OK
operator|&&
name|responseContentType
operator|.
name|startsWith
argument_list|(
literal|"application/json"
argument_list|)
operator|&&
name|responseContentLength
operator|>
literal|0
condition|)
block|{
name|InputStream
name|httpResponseStream
init|=
name|conn
operator|.
name|getInputStream
argument_list|()
decl_stmt|;
name|token
operator|=
name|parseTokenFromStream
argument_list|(
name|httpResponseStream
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|String
name|responseBody
init|=
name|consumeInputStream
argument_list|(
name|conn
operator|.
name|getErrorStream
argument_list|()
argument_list|,
literal|1024
argument_list|)
decl_stmt|;
name|String
name|proxies
init|=
literal|"none"
decl_stmt|;
name|String
name|httpProxy
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"http.proxy"
argument_list|)
decl_stmt|;
name|String
name|httpsProxy
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"https.proxy"
argument_list|)
decl_stmt|;
if|if
condition|(
name|httpProxy
operator|!=
literal|null
operator|||
name|httpsProxy
operator|!=
literal|null
condition|)
block|{
name|proxies
operator|=
literal|"http:"
operator|+
name|httpProxy
operator|+
literal|"; https:"
operator|+
name|httpsProxy
expr_stmt|;
block|}
name|String
name|logMessage
init|=
literal|"AADToken: HTTP connection failed for getting token from AzureAD. Http response: "
operator|+
name|httpResponseCode
operator|+
literal|" "
operator|+
name|conn
operator|.
name|getResponseMessage
argument_list|()
operator|+
literal|"\nContent-Type: "
operator|+
name|responseContentType
operator|+
literal|" Content-Length: "
operator|+
name|responseContentLength
operator|+
literal|" Request ID: "
operator|+
name|requestId
operator|.
name|toString
argument_list|()
operator|+
literal|" Proxies: "
operator|+
name|proxies
operator|+
literal|"\nFirst 1K of Body: "
operator|+
name|responseBody
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
name|logMessage
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|HttpException
argument_list|(
name|httpResponseCode
argument_list|,
name|requestId
argument_list|,
name|logMessage
argument_list|)
throw|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|conn
operator|!=
literal|null
condition|)
block|{
name|conn
operator|.
name|disconnect
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|token
return|;
block|}
DECL|method|parseTokenFromStream (InputStream httpResponseStream)
specifier|private
specifier|static
name|AzureADToken
name|parseTokenFromStream
parameter_list|(
name|InputStream
name|httpResponseStream
parameter_list|)
throws|throws
name|IOException
block|{
name|AzureADToken
name|token
init|=
operator|new
name|AzureADToken
argument_list|()
decl_stmt|;
try|try
block|{
name|int
name|expiryPeriod
init|=
literal|0
decl_stmt|;
name|JsonFactory
name|jf
init|=
operator|new
name|JsonFactory
argument_list|()
decl_stmt|;
name|JsonParser
name|jp
init|=
name|jf
operator|.
name|createJsonParser
argument_list|(
name|httpResponseStream
argument_list|)
decl_stmt|;
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
comment|// field value
name|fieldValue
operator|=
name|jp
operator|.
name|getText
argument_list|()
expr_stmt|;
if|if
condition|(
name|fieldName
operator|.
name|equals
argument_list|(
literal|"access_token"
argument_list|)
condition|)
block|{
name|token
operator|.
name|setAccessToken
argument_list|(
name|fieldValue
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fieldName
operator|.
name|equals
argument_list|(
literal|"expires_in"
argument_list|)
condition|)
block|{
name|expiryPeriod
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|fieldValue
argument_list|)
expr_stmt|;
block|}
block|}
name|jp
operator|.
name|nextToken
argument_list|()
expr_stmt|;
block|}
name|jp
operator|.
name|close
argument_list|()
expr_stmt|;
name|long
name|expiry
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|expiry
operator|=
name|expiry
operator|+
name|expiryPeriod
operator|*
literal|1000L
expr_stmt|;
comment|// convert expiryPeriod to milliseconds and add
name|token
operator|.
name|setExpiry
argument_list|(
operator|new
name|Date
argument_list|(
name|expiry
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"AADToken: fetched token with expiry "
operator|+
name|token
operator|.
name|getExpiry
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"AADToken: got exception when parsing json token "
operator|+
name|ex
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
name|ex
throw|;
block|}
finally|finally
block|{
name|httpResponseStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|token
return|;
block|}
DECL|method|consumeInputStream (InputStream inStream, int length)
specifier|private
specifier|static
name|String
name|consumeInputStream
parameter_list|(
name|InputStream
name|inStream
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|b
init|=
operator|new
name|byte
index|[
name|length
index|]
decl_stmt|;
name|int
name|totalBytesRead
init|=
literal|0
decl_stmt|;
name|int
name|bytesRead
init|=
literal|0
decl_stmt|;
do|do
block|{
name|bytesRead
operator|=
name|inStream
operator|.
name|read
argument_list|(
name|b
argument_list|,
name|totalBytesRead
argument_list|,
name|length
operator|-
name|totalBytesRead
argument_list|)
expr_stmt|;
if|if
condition|(
name|bytesRead
operator|>
literal|0
condition|)
block|{
name|totalBytesRead
operator|+=
name|bytesRead
expr_stmt|;
block|}
block|}
do|while
condition|(
name|bytesRead
operator|>=
literal|0
operator|&&
name|totalBytesRead
operator|<
name|length
condition|)
do|;
return|return
operator|new
name|String
argument_list|(
name|b
argument_list|,
literal|0
argument_list|,
name|totalBytesRead
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
return|;
block|}
block|}
end_class

end_unit

