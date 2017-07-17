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
name|URI
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
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|databind
operator|.
name|ObjectReader
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
name|hadoop
operator|.
name|io
operator|.
name|retry
operator|.
name|RetryUtils
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
name|security
operator|.
name|UserGroupInformation
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
name|utils
operator|.
name|URIBuilder
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
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|core
operator|.
name|JsonParseException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|databind
operator|.
name|JsonMappingException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|databind
operator|.
name|ObjectMapper
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
name|azure
operator|.
name|WasbRemoteCallHelper
operator|.
name|REMOTE_CALL_SUCCESS_CODE
import|;
end_import

begin_comment
comment|/**  * Class implementing a RemoteSASKeyGenerator. This class  * uses the url passed in via the Configuration to make a  * rest call to generate the required SAS Key.  */
end_comment

begin_class
DECL|class|RemoteSASKeyGeneratorImpl
specifier|public
class|class
name|RemoteSASKeyGeneratorImpl
extends|extends
name|SASKeyGeneratorImpl
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
name|AzureNativeFileSystemStore
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|RESPONSE_READER
specifier|private
specifier|static
specifier|final
name|ObjectReader
name|RESPONSE_READER
init|=
operator|new
name|ObjectMapper
argument_list|()
operator|.
name|readerFor
argument_list|(
name|RemoteSASKeyGenerationResponse
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Configuration parameter name expected in the Configuration    * object to provide the url of the remote service {@value}    */
DECL|field|KEY_CRED_SERVICE_URLS
specifier|public
specifier|static
specifier|final
name|String
name|KEY_CRED_SERVICE_URLS
init|=
literal|"fs.azure.cred.service.urls"
decl_stmt|;
comment|/**    * Configuration key to enable http retry policy for SAS Key generation. {@value}    */
specifier|public
specifier|static
specifier|final
name|String
DECL|field|SAS_KEY_GENERATOR_HTTP_CLIENT_RETRY_POLICY_ENABLED_KEY
name|SAS_KEY_GENERATOR_HTTP_CLIENT_RETRY_POLICY_ENABLED_KEY
init|=
literal|"fs.azure.saskeygenerator.http.retry.policy.enabled"
decl_stmt|;
comment|/**    * Configuration key for SAS Key Generation http retry policy spec. {@value}    */
specifier|public
specifier|static
specifier|final
name|String
DECL|field|SAS_KEY_GENERATOR_HTTP_CLIENT_RETRY_POLICY_SPEC_KEY
name|SAS_KEY_GENERATOR_HTTP_CLIENT_RETRY_POLICY_SPEC_KEY
init|=
literal|"fs.azure.saskeygenerator.http.retry.policy.spec"
decl_stmt|;
comment|/**    * Container SAS Key generation OP name. {@value}    */
DECL|field|CONTAINER_SAS_OP
specifier|private
specifier|static
specifier|final
name|String
name|CONTAINER_SAS_OP
init|=
literal|"GET_CONTAINER_SAS"
decl_stmt|;
comment|/**    * Relative Blob SAS Key generation OP name. {@value}    */
DECL|field|BLOB_SAS_OP
specifier|private
specifier|static
specifier|final
name|String
name|BLOB_SAS_OP
init|=
literal|"GET_RELATIVE_BLOB_SAS"
decl_stmt|;
comment|/**    * Query parameter specifying the expiry period to be used for sas key    * {@value}    */
DECL|field|SAS_EXPIRY_QUERY_PARAM_NAME
specifier|private
specifier|static
specifier|final
name|String
name|SAS_EXPIRY_QUERY_PARAM_NAME
init|=
literal|"sas_expiry"
decl_stmt|;
comment|/**    * Query parameter name for the storage account. {@value}    */
DECL|field|STORAGE_ACCOUNT_QUERY_PARAM_NAME
specifier|private
specifier|static
specifier|final
name|String
name|STORAGE_ACCOUNT_QUERY_PARAM_NAME
init|=
literal|"storage_account"
decl_stmt|;
comment|/**    * Query parameter name for the storage account container. {@value}    */
DECL|field|CONTAINER_QUERY_PARAM_NAME
specifier|private
specifier|static
specifier|final
name|String
name|CONTAINER_QUERY_PARAM_NAME
init|=
literal|"container"
decl_stmt|;
comment|/**    * Query parameter name for the relative path inside the storage    * account container. {@value}    */
DECL|field|RELATIVE_PATH_QUERY_PARAM_NAME
specifier|private
specifier|static
specifier|final
name|String
name|RELATIVE_PATH_QUERY_PARAM_NAME
init|=
literal|"relative_path"
decl_stmt|;
comment|/**    * SAS Key Generation Remote http client retry policy spec. {@value}    */
specifier|private
specifier|static
specifier|final
name|String
DECL|field|SAS_KEY_GENERATOR_HTTP_CLIENT_RETRY_POLICY_SPEC_DEFAULT
name|SAS_KEY_GENERATOR_HTTP_CLIENT_RETRY_POLICY_SPEC_DEFAULT
init|=
literal|"10,3,100,2"
decl_stmt|;
DECL|field|remoteCallHelper
specifier|private
name|WasbRemoteCallHelper
name|remoteCallHelper
init|=
literal|null
decl_stmt|;
DECL|field|isKerberosSupportEnabled
specifier|private
name|boolean
name|isKerberosSupportEnabled
decl_stmt|;
DECL|field|isSpnegoTokenCacheEnabled
specifier|private
name|boolean
name|isSpnegoTokenCacheEnabled
decl_stmt|;
DECL|field|retryPolicy
specifier|private
name|RetryPolicy
name|retryPolicy
decl_stmt|;
DECL|field|commaSeparatedUrls
specifier|private
name|String
index|[]
name|commaSeparatedUrls
decl_stmt|;
DECL|method|RemoteSASKeyGeneratorImpl (Configuration conf)
specifier|public
name|RemoteSASKeyGeneratorImpl
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|super
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
DECL|method|initialize (Configuration conf)
specifier|public
name|void
name|initialize
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Initializing RemoteSASKeyGeneratorImpl instance"
argument_list|)
expr_stmt|;
name|this
operator|.
name|retryPolicy
operator|=
name|RetryUtils
operator|.
name|getMultipleLinearRandomRetry
argument_list|(
name|conf
argument_list|,
name|SAS_KEY_GENERATOR_HTTP_CLIENT_RETRY_POLICY_ENABLED_KEY
argument_list|,
literal|true
argument_list|,
name|SAS_KEY_GENERATOR_HTTP_CLIENT_RETRY_POLICY_SPEC_KEY
argument_list|,
name|SAS_KEY_GENERATOR_HTTP_CLIENT_RETRY_POLICY_SPEC_DEFAULT
argument_list|)
expr_stmt|;
name|this
operator|.
name|isKerberosSupportEnabled
operator|=
name|conf
operator|.
name|getBoolean
argument_list|(
name|Constants
operator|.
name|AZURE_KERBEROS_SUPPORT_PROPERTY_NAME
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|this
operator|.
name|isSpnegoTokenCacheEnabled
operator|=
name|conf
operator|.
name|getBoolean
argument_list|(
name|Constants
operator|.
name|AZURE_ENABLE_SPNEGO_TOKEN_CACHE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|this
operator|.
name|commaSeparatedUrls
operator|=
name|conf
operator|.
name|getTrimmedStrings
argument_list|(
name|KEY_CRED_SERVICE_URLS
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|commaSeparatedUrls
operator|==
literal|null
operator|||
name|this
operator|.
name|commaSeparatedUrls
operator|.
name|length
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|KEY_CRED_SERVICE_URLS
operator|+
literal|" config not set"
operator|+
literal|" in configuration."
argument_list|)
throw|;
block|}
if|if
condition|(
name|isKerberosSupportEnabled
operator|&&
name|UserGroupInformation
operator|.
name|isSecurityEnabled
argument_list|()
condition|)
block|{
name|this
operator|.
name|remoteCallHelper
operator|=
operator|new
name|SecureWasbRemoteCallHelper
argument_list|(
name|retryPolicy
argument_list|,
literal|false
argument_list|,
name|isSpnegoTokenCacheEnabled
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|remoteCallHelper
operator|=
operator|new
name|WasbRemoteCallHelper
argument_list|(
name|retryPolicy
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"Initialization of RemoteSASKeyGenerator instance successful"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getContainerSASUri (String storageAccount, String container)
specifier|public
name|URI
name|getContainerSASUri
parameter_list|(
name|String
name|storageAccount
parameter_list|,
name|String
name|container
parameter_list|)
throws|throws
name|SASKeyGenerationException
block|{
name|RemoteSASKeyGenerationResponse
name|sasKeyResponse
init|=
literal|null
decl_stmt|;
try|try
block|{
name|URIBuilder
name|uriBuilder
init|=
operator|new
name|URIBuilder
argument_list|()
decl_stmt|;
name|uriBuilder
operator|.
name|setPath
argument_list|(
literal|"/"
operator|+
name|CONTAINER_SAS_OP
argument_list|)
expr_stmt|;
name|uriBuilder
operator|.
name|addParameter
argument_list|(
name|STORAGE_ACCOUNT_QUERY_PARAM_NAME
argument_list|,
name|storageAccount
argument_list|)
expr_stmt|;
name|uriBuilder
operator|.
name|addParameter
argument_list|(
name|CONTAINER_QUERY_PARAM_NAME
argument_list|,
name|container
argument_list|)
expr_stmt|;
name|uriBuilder
operator|.
name|addParameter
argument_list|(
name|SAS_EXPIRY_QUERY_PARAM_NAME
argument_list|,
literal|""
operator|+
name|getSasKeyExpiryPeriod
argument_list|()
argument_list|)
expr_stmt|;
name|sasKeyResponse
operator|=
name|makeRemoteRequest
argument_list|(
name|commaSeparatedUrls
argument_list|,
name|uriBuilder
operator|.
name|getPath
argument_list|()
argument_list|,
name|uriBuilder
operator|.
name|getQueryParams
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|sasKeyResponse
operator|.
name|getResponseCode
argument_list|()
operator|==
name|REMOTE_CALL_SUCCESS_CODE
condition|)
block|{
return|return
operator|new
name|URI
argument_list|(
name|sasKeyResponse
operator|.
name|getSasKey
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|SASKeyGenerationException
argument_list|(
literal|"Remote Service encountered error in SAS Key generation : "
operator|+
name|sasKeyResponse
operator|.
name|getResponseMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|uriSyntaxEx
parameter_list|)
block|{
throw|throw
operator|new
name|SASKeyGenerationException
argument_list|(
literal|"Encountered URISyntaxException"
operator|+
literal|" while building the HttpGetRequest to remote service for "
argument_list|,
name|uriSyntaxEx
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getRelativeBlobSASUri (String storageAccount, String container, String relativePath)
specifier|public
name|URI
name|getRelativeBlobSASUri
parameter_list|(
name|String
name|storageAccount
parameter_list|,
name|String
name|container
parameter_list|,
name|String
name|relativePath
parameter_list|)
throws|throws
name|SASKeyGenerationException
block|{
try|try
block|{
name|URIBuilder
name|uriBuilder
init|=
operator|new
name|URIBuilder
argument_list|()
decl_stmt|;
name|uriBuilder
operator|.
name|setPath
argument_list|(
literal|"/"
operator|+
name|BLOB_SAS_OP
argument_list|)
expr_stmt|;
name|uriBuilder
operator|.
name|addParameter
argument_list|(
name|STORAGE_ACCOUNT_QUERY_PARAM_NAME
argument_list|,
name|storageAccount
argument_list|)
expr_stmt|;
name|uriBuilder
operator|.
name|addParameter
argument_list|(
name|CONTAINER_QUERY_PARAM_NAME
argument_list|,
name|container
argument_list|)
expr_stmt|;
name|uriBuilder
operator|.
name|addParameter
argument_list|(
name|RELATIVE_PATH_QUERY_PARAM_NAME
argument_list|,
name|relativePath
argument_list|)
expr_stmt|;
name|uriBuilder
operator|.
name|addParameter
argument_list|(
name|SAS_EXPIRY_QUERY_PARAM_NAME
argument_list|,
literal|""
operator|+
name|getSasKeyExpiryPeriod
argument_list|()
argument_list|)
expr_stmt|;
name|RemoteSASKeyGenerationResponse
name|sasKeyResponse
init|=
name|makeRemoteRequest
argument_list|(
name|commaSeparatedUrls
argument_list|,
name|uriBuilder
operator|.
name|getPath
argument_list|()
argument_list|,
name|uriBuilder
operator|.
name|getQueryParams
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|sasKeyResponse
operator|.
name|getResponseCode
argument_list|()
operator|==
name|REMOTE_CALL_SUCCESS_CODE
condition|)
block|{
return|return
operator|new
name|URI
argument_list|(
name|sasKeyResponse
operator|.
name|getSasKey
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|SASKeyGenerationException
argument_list|(
literal|"Remote Service encountered error in SAS Key generation : "
operator|+
name|sasKeyResponse
operator|.
name|getResponseMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|uriSyntaxEx
parameter_list|)
block|{
throw|throw
operator|new
name|SASKeyGenerationException
argument_list|(
literal|"Encountered URISyntaxException"
operator|+
literal|" while building the HttpGetRequest to "
operator|+
literal|" remote service"
argument_list|,
name|uriSyntaxEx
argument_list|)
throw|;
block|}
block|}
comment|/**    * Helper method to make a remote request.    *    * @param urls        - Urls to use for the remote request    * @param path        - hadoop.auth token for the remote request    * @param queryParams - queryParams to be used.    * @return RemoteSASKeyGenerationResponse    */
DECL|method|makeRemoteRequest (String[] urls, String path, List<NameValuePair> queryParams)
specifier|private
name|RemoteSASKeyGenerationResponse
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
parameter_list|)
throws|throws
name|SASKeyGenerationException
block|{
try|try
block|{
name|String
name|responseBody
init|=
name|remoteCallHelper
operator|.
name|makeRemoteRequest
argument_list|(
name|urls
argument_list|,
name|path
argument_list|,
name|queryParams
argument_list|,
name|HttpGet
operator|.
name|METHOD_NAME
argument_list|)
decl_stmt|;
return|return
name|RESPONSE_READER
operator|.
name|readValue
argument_list|(
name|responseBody
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|WasbRemoteCallException
name|remoteCallEx
parameter_list|)
block|{
throw|throw
operator|new
name|SASKeyGenerationException
argument_list|(
literal|"Encountered RemoteCallException"
operator|+
literal|" while retrieving SAS key from remote service"
argument_list|,
name|remoteCallEx
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|JsonParseException
name|jsonParserEx
parameter_list|)
block|{
throw|throw
operator|new
name|SASKeyGenerationException
argument_list|(
literal|"Encountered JsonParseException "
operator|+
literal|"while parsing the response from remote"
operator|+
literal|" service into RemoteSASKeyGenerationResponse object"
argument_list|,
name|jsonParserEx
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|JsonMappingException
name|jsonMappingEx
parameter_list|)
block|{
throw|throw
operator|new
name|SASKeyGenerationException
argument_list|(
literal|"Encountered JsonMappingException"
operator|+
literal|" while mapping the response from remote service into "
operator|+
literal|"RemoteSASKeyGenerationResponse object"
argument_list|,
name|jsonMappingEx
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
name|SASKeyGenerationException
argument_list|(
literal|"Encountered IOException while "
operator|+
literal|"accessing remote service to retrieve SAS Key"
argument_list|,
name|ioEx
argument_list|)
throw|;
block|}
block|}
block|}
end_class

begin_comment
comment|/**  * POJO representing the response expected from a Remote  * SAS Key generation service.  * The remote SAS Key generation service is expected to  * return SAS key in json format:  * {  *   "responseCode" : 0 or non-zero<int>,  *   "responseMessage" : relavant message on failure<String>,  *   "sasKey" : Requested SAS Key<String>  * }  */
end_comment

begin_class
DECL|class|RemoteSASKeyGenerationResponse
class|class
name|RemoteSASKeyGenerationResponse
block|{
comment|/**    * Response code for the call.    */
DECL|field|responseCode
specifier|private
name|int
name|responseCode
decl_stmt|;
comment|/**    * An intelligent message corresponding to    * result. Specifically in case of failure    * the reason for failure.    */
DECL|field|responseMessage
specifier|private
name|String
name|responseMessage
decl_stmt|;
comment|/**    * SAS Key corresponding to the request.    */
DECL|field|sasKey
specifier|private
name|String
name|sasKey
decl_stmt|;
DECL|method|getResponseCode ()
specifier|public
name|int
name|getResponseCode
parameter_list|()
block|{
return|return
name|responseCode
return|;
block|}
DECL|method|setResponseCode (int responseCode)
specifier|public
name|void
name|setResponseCode
parameter_list|(
name|int
name|responseCode
parameter_list|)
block|{
name|this
operator|.
name|responseCode
operator|=
name|responseCode
expr_stmt|;
block|}
DECL|method|getResponseMessage ()
specifier|public
name|String
name|getResponseMessage
parameter_list|()
block|{
return|return
name|responseMessage
return|;
block|}
DECL|method|setResponseMessage (String responseMessage)
specifier|public
name|void
name|setResponseMessage
parameter_list|(
name|String
name|responseMessage
parameter_list|)
block|{
name|this
operator|.
name|responseMessage
operator|=
name|responseMessage
expr_stmt|;
block|}
DECL|method|getSasKey ()
specifier|public
name|String
name|getSasKey
parameter_list|()
block|{
return|return
name|sasKey
return|;
block|}
DECL|method|setSasKey (String sasKey)
specifier|public
name|void
name|setSasKey
parameter_list|(
name|String
name|sasKey
parameter_list|)
block|{
name|this
operator|.
name|sasKey
operator|=
name|sasKey
expr_stmt|;
block|}
block|}
end_class

end_unit

