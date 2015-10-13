begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.web.oauth2
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|web
operator|.
name|oauth2
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
name|net
operator|.
name|ServerSocketUtil
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
name|util
operator|.
name|Timer
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
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockserver
operator|.
name|client
operator|.
name|server
operator|.
name|MockServerClient
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockserver
operator|.
name|integration
operator|.
name|ClientAndServer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockserver
operator|.
name|model
operator|.
name|Header
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockserver
operator|.
name|model
operator|.
name|HttpRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockserver
operator|.
name|model
operator|.
name|HttpResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockserver
operator|.
name|model
operator|.
name|Parameter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockserver
operator|.
name|model
operator|.
name|ParameterBody
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
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
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
name|hdfs
operator|.
name|client
operator|.
name|HdfsClientConfigKeys
operator|.
name|ACCESS_TOKEN_PROVIDER_KEY
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
name|hdfs
operator|.
name|client
operator|.
name|HdfsClientConfigKeys
operator|.
name|OAUTH_CLIENT_ID_KEY
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
name|hdfs
operator|.
name|client
operator|.
name|HdfsClientConfigKeys
operator|.
name|OAUTH_REFRESH_URL_KEY
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
name|hdfs
operator|.
name|web
operator|.
name|oauth2
operator|.
name|OAuth2Constants
operator|.
name|ACCESS_TOKEN
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
name|hdfs
operator|.
name|web
operator|.
name|oauth2
operator|.
name|OAuth2Constants
operator|.
name|CLIENT_CREDENTIALS
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
name|hdfs
operator|.
name|web
operator|.
name|oauth2
operator|.
name|OAuth2Constants
operator|.
name|CLIENT_ID
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
name|hdfs
operator|.
name|web
operator|.
name|oauth2
operator|.
name|OAuth2Constants
operator|.
name|CLIENT_SECRET
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
name|hdfs
operator|.
name|web
operator|.
name|oauth2
operator|.
name|OAuth2Constants
operator|.
name|EXPIRES_IN
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
name|hdfs
operator|.
name|web
operator|.
name|oauth2
operator|.
name|OAuth2Constants
operator|.
name|GRANT_TYPE
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
name|hdfs
operator|.
name|web
operator|.
name|oauth2
operator|.
name|OAuth2Constants
operator|.
name|TOKEN_TYPE
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|when
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockserver
operator|.
name|integration
operator|.
name|ClientAndServer
operator|.
name|startClientAndServer
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockserver
operator|.
name|matchers
operator|.
name|Times
operator|.
name|exactly
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockserver
operator|.
name|model
operator|.
name|HttpRequest
operator|.
name|request
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockserver
operator|.
name|model
operator|.
name|HttpResponse
operator|.
name|response
import|;
end_import

begin_class
DECL|class|TestClientCredentialTimeBasedTokenRefresher
specifier|public
class|class
name|TestClientCredentialTimeBasedTokenRefresher
block|{
DECL|field|CONTENT_TYPE_APPLICATION_JSON
specifier|public
specifier|final
specifier|static
name|Header
name|CONTENT_TYPE_APPLICATION_JSON
init|=
operator|new
name|Header
argument_list|(
literal|"Content-Type"
argument_list|,
literal|"application/json"
argument_list|)
decl_stmt|;
DECL|field|CLIENT_ID_FOR_TESTING
specifier|public
specifier|final
specifier|static
name|String
name|CLIENT_ID_FOR_TESTING
init|=
literal|"joebob"
decl_stmt|;
DECL|method|buildConf (String credential, String tokenExpires, String clientId, String refreshURL)
specifier|public
name|Configuration
name|buildConf
parameter_list|(
name|String
name|credential
parameter_list|,
name|String
name|tokenExpires
parameter_list|,
name|String
name|clientId
parameter_list|,
name|String
name|refreshURL
parameter_list|)
block|{
comment|// Configurations are simple enough that it's not worth mocking them out.
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|CredentialBasedAccessTokenProvider
operator|.
name|OAUTH_CREDENTIAL_KEY
argument_list|,
name|credential
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|ACCESS_TOKEN_PROVIDER_KEY
argument_list|,
name|ConfCredentialBasedAccessTokenProvider
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|OAUTH_CLIENT_ID_KEY
argument_list|,
name|clientId
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|OAUTH_REFRESH_URL_KEY
argument_list|,
name|refreshURL
argument_list|)
expr_stmt|;
return|return
name|conf
return|;
block|}
annotation|@
name|Test
DECL|method|refreshUrlIsCorrect ()
specifier|public
name|void
name|refreshUrlIsCorrect
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|int
name|PORT
init|=
name|ServerSocketUtil
operator|.
name|getPort
argument_list|(
literal|0
argument_list|,
literal|20
argument_list|)
decl_stmt|;
specifier|final
name|String
name|REFRESH_ADDRESS
init|=
literal|"http://localhost:"
operator|+
name|PORT
operator|+
literal|"/refresh"
decl_stmt|;
name|long
name|tokenExpires
init|=
literal|0
decl_stmt|;
name|Configuration
name|conf
init|=
name|buildConf
argument_list|(
literal|"myreallycoolcredential"
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|tokenExpires
argument_list|)
argument_list|,
name|CLIENT_ID_FOR_TESTING
argument_list|,
name|REFRESH_ADDRESS
argument_list|)
decl_stmt|;
name|Timer
name|mockTimer
init|=
name|mock
argument_list|(
name|Timer
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|mockTimer
operator|.
name|now
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|tokenExpires
operator|+
literal|1000l
argument_list|)
expr_stmt|;
name|AccessTokenProvider
name|credProvider
init|=
operator|new
name|ConfCredentialBasedAccessTokenProvider
argument_list|(
name|mockTimer
argument_list|)
decl_stmt|;
name|credProvider
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
comment|// Build mock server to receive refresh request
name|ClientAndServer
name|mockServer
init|=
name|startClientAndServer
argument_list|(
name|PORT
argument_list|)
decl_stmt|;
name|HttpRequest
name|expectedRequest
init|=
name|request
argument_list|()
operator|.
name|withMethod
argument_list|(
literal|"POST"
argument_list|)
operator|.
name|withPath
argument_list|(
literal|"/refresh"
argument_list|)
operator|.
name|withBody
argument_list|(
comment|// Note, OkHttp does not sort the param values, so we need to do
comment|// it ourselves via the ordering provided to ParameterBody...
name|ParameterBody
operator|.
name|params
argument_list|(
name|Parameter
operator|.
name|param
argument_list|(
name|CLIENT_SECRET
argument_list|,
literal|"myreallycoolcredential"
argument_list|)
argument_list|,
name|Parameter
operator|.
name|param
argument_list|(
name|GRANT_TYPE
argument_list|,
name|CLIENT_CREDENTIALS
argument_list|)
argument_list|,
name|Parameter
operator|.
name|param
argument_list|(
name|CLIENT_ID
argument_list|,
name|CLIENT_ID_FOR_TESTING
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|MockServerClient
name|mockServerClient
init|=
operator|new
name|MockServerClient
argument_list|(
literal|"localhost"
argument_list|,
name|PORT
argument_list|)
decl_stmt|;
comment|// https://tools.ietf.org/html/rfc6749#section-5.1
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
init|=
operator|new
name|TreeMap
argument_list|<>
argument_list|()
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
name|EXPIRES_IN
argument_list|,
literal|"0987654321"
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|TOKEN_TYPE
argument_list|,
literal|"bearer"
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|ACCESS_TOKEN
argument_list|,
literal|"new access token"
argument_list|)
expr_stmt|;
name|ObjectMapper
name|mapper
init|=
operator|new
name|ObjectMapper
argument_list|()
decl_stmt|;
name|HttpResponse
name|resp
init|=
name|response
argument_list|()
operator|.
name|withStatusCode
argument_list|(
name|HttpStatus
operator|.
name|SC_OK
argument_list|)
operator|.
name|withHeaders
argument_list|(
name|CONTENT_TYPE_APPLICATION_JSON
argument_list|)
operator|.
name|withBody
argument_list|(
name|mapper
operator|.
name|writeValueAsString
argument_list|(
name|map
argument_list|)
argument_list|)
decl_stmt|;
name|mockServerClient
operator|.
name|when
argument_list|(
name|expectedRequest
argument_list|,
name|exactly
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|respond
argument_list|(
name|resp
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"new access token"
argument_list|,
name|credProvider
operator|.
name|getAccessToken
argument_list|()
argument_list|)
expr_stmt|;
name|mockServerClient
operator|.
name|verify
argument_list|(
name|expectedRequest
argument_list|)
expr_stmt|;
name|mockServerClient
operator|.
name|clear
argument_list|(
name|expectedRequest
argument_list|)
expr_stmt|;
name|mockServer
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

