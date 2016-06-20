begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.webapp.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|webapp
operator|.
name|util
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertArrayEquals
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
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|net
operator|.
name|UnknownHostException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|Path
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
name|http
operator|.
name|HttpServer2
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
name|http
operator|.
name|HttpServer2
operator|.
name|Builder
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
name|NetUtils
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
name|alias
operator|.
name|CredentialProvider
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
name|alias
operator|.
name|CredentialProviderFactory
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
name|alias
operator|.
name|JavaKeyStoreProvider
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
name|yarn
operator|.
name|conf
operator|.
name|YarnConfiguration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
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
name|mockito
operator|.
name|Mockito
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletRequest
import|;
end_import

begin_class
DECL|class|TestWebAppUtils
specifier|public
class|class
name|TestWebAppUtils
block|{
DECL|field|RM1_NODE_ID
specifier|private
specifier|static
specifier|final
name|String
name|RM1_NODE_ID
init|=
literal|"rm1"
decl_stmt|;
DECL|field|RM2_NODE_ID
specifier|private
specifier|static
specifier|final
name|String
name|RM2_NODE_ID
init|=
literal|"rm2"
decl_stmt|;
comment|// Because WebAppUtils#getResolvedAddress tries to resolve the hostname, we add a static mapping for dummy hostnames
comment|// to make this test run anywhere without having to give some resolvable hostnames
DECL|field|dummyHostNames
specifier|private
specifier|static
name|String
name|dummyHostNames
index|[]
init|=
block|{
literal|"host1"
block|,
literal|"host2"
block|,
literal|"host3"
block|}
decl_stmt|;
DECL|field|anyIpAddress
specifier|private
specifier|static
specifier|final
name|String
name|anyIpAddress
init|=
literal|"1.2.3.4"
decl_stmt|;
DECL|field|savedStaticResolution
specifier|private
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|savedStaticResolution
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|initializeDummyHostnameResolution ()
specifier|public
specifier|static
name|void
name|initializeDummyHostnameResolution
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|previousIpAddress
decl_stmt|;
for|for
control|(
name|String
name|hostName
range|:
name|dummyHostNames
control|)
block|{
if|if
condition|(
literal|null
operator|!=
operator|(
name|previousIpAddress
operator|=
name|NetUtils
operator|.
name|getStaticResolution
argument_list|(
name|hostName
argument_list|)
operator|)
condition|)
block|{
name|savedStaticResolution
operator|.
name|put
argument_list|(
name|hostName
argument_list|,
name|previousIpAddress
argument_list|)
expr_stmt|;
block|}
name|NetUtils
operator|.
name|addStaticResolution
argument_list|(
name|hostName
argument_list|,
name|anyIpAddress
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|AfterClass
DECL|method|restoreDummyHostnameResolution ()
specifier|public
specifier|static
name|void
name|restoreDummyHostnameResolution
parameter_list|()
throws|throws
name|Exception
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
name|hostnameToIpEntry
range|:
name|savedStaticResolution
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|NetUtils
operator|.
name|addStaticResolution
argument_list|(
name|hostnameToIpEntry
operator|.
name|getKey
argument_list|()
argument_list|,
name|hostnameToIpEntry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|TestRMWebAppURLRemoteAndLocal ()
specifier|public
name|void
name|TestRMWebAppURLRemoteAndLocal
parameter_list|()
throws|throws
name|UnknownHostException
block|{
name|Configuration
name|configuration
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
specifier|final
name|String
name|rmAddress
init|=
literal|"host1:8088"
decl_stmt|;
name|configuration
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_WEBAPP_ADDRESS
argument_list|,
name|rmAddress
argument_list|)
expr_stmt|;
specifier|final
name|String
name|rm1Address
init|=
literal|"host2:8088"
decl_stmt|;
specifier|final
name|String
name|rm2Address
init|=
literal|"host3:8088"
decl_stmt|;
name|configuration
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_WEBAPP_ADDRESS
operator|+
literal|"."
operator|+
name|RM1_NODE_ID
argument_list|,
name|rm1Address
argument_list|)
expr_stmt|;
name|configuration
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_WEBAPP_ADDRESS
operator|+
literal|"."
operator|+
name|RM2_NODE_ID
argument_list|,
name|rm2Address
argument_list|)
expr_stmt|;
name|configuration
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|RM_HA_ENABLED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|configuration
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_HA_IDS
argument_list|,
name|RM1_NODE_ID
operator|+
literal|","
operator|+
name|RM2_NODE_ID
argument_list|)
expr_stmt|;
name|String
name|rmRemoteUrl
init|=
name|WebAppUtils
operator|.
name|getResolvedRemoteRMWebAppURLWithoutScheme
argument_list|(
name|configuration
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"ResolvedRemoteRMWebAppUrl should resolve to the first HA RM address"
argument_list|,
name|rm1Address
argument_list|,
name|rmRemoteUrl
argument_list|)
expr_stmt|;
name|String
name|rmLocalUrl
init|=
name|WebAppUtils
operator|.
name|getResolvedRMWebAppURLWithoutScheme
argument_list|(
name|configuration
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"ResolvedRMWebAppUrl should resolve to the default RM webapp address"
argument_list|,
name|rmAddress
argument_list|,
name|rmLocalUrl
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetPassword ()
specifier|public
name|void
name|testGetPassword
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
name|provisionCredentialsForSSL
argument_list|()
decl_stmt|;
comment|// use WebAppUtils as would be used by loadSslConfiguration
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"keypass"
argument_list|,
name|WebAppUtils
operator|.
name|getPassword
argument_list|(
name|conf
argument_list|,
name|WebAppUtils
operator|.
name|WEB_APP_KEY_PASSWORD_KEY
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"storepass"
argument_list|,
name|WebAppUtils
operator|.
name|getPassword
argument_list|(
name|conf
argument_list|,
name|WebAppUtils
operator|.
name|WEB_APP_KEYSTORE_PASSWORD_KEY
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"trustpass"
argument_list|,
name|WebAppUtils
operator|.
name|getPassword
argument_list|(
name|conf
argument_list|,
name|WebAppUtils
operator|.
name|WEB_APP_TRUSTSTORE_PASSWORD_KEY
argument_list|)
argument_list|)
expr_stmt|;
comment|// let's make sure that a password that doesn't exist returns null
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|WebAppUtils
operator|.
name|getPassword
argument_list|(
name|conf
argument_list|,
literal|"invalid-alias"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testLoadSslConfiguration ()
specifier|public
name|void
name|testLoadSslConfiguration
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
name|provisionCredentialsForSSL
argument_list|()
decl_stmt|;
name|TestBuilder
name|builder
init|=
operator|(
name|TestBuilder
operator|)
operator|new
name|TestBuilder
argument_list|()
decl_stmt|;
name|builder
operator|=
operator|(
name|TestBuilder
operator|)
name|WebAppUtils
operator|.
name|loadSslConfiguration
argument_list|(
name|builder
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|String
name|keypass
init|=
literal|"keypass"
decl_stmt|;
name|String
name|storepass
init|=
literal|"storepass"
decl_stmt|;
name|String
name|trustpass
init|=
literal|"trustpass"
decl_stmt|;
comment|// make sure we get the right passwords in the builder
name|assertEquals
argument_list|(
name|keypass
argument_list|,
operator|(
operator|(
name|TestBuilder
operator|)
name|builder
operator|)
operator|.
name|keypass
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|storepass
argument_list|,
operator|(
operator|(
name|TestBuilder
operator|)
name|builder
operator|)
operator|.
name|keystorePassword
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|trustpass
argument_list|,
operator|(
operator|(
name|TestBuilder
operator|)
name|builder
operator|)
operator|.
name|truststorePassword
argument_list|)
expr_stmt|;
block|}
DECL|method|provisionCredentialsForSSL ()
specifier|protected
name|Configuration
name|provisionCredentialsForSSL
parameter_list|()
throws|throws
name|IOException
throws|,
name|Exception
block|{
name|File
name|testDir
init|=
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.data"
argument_list|,
literal|"target/test-dir"
argument_list|)
argument_list|)
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
specifier|final
name|Path
name|jksPath
init|=
operator|new
name|Path
argument_list|(
name|testDir
operator|.
name|toString
argument_list|()
argument_list|,
literal|"test.jks"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|ourUrl
init|=
name|JavaKeyStoreProvider
operator|.
name|SCHEME_NAME
operator|+
literal|"://file"
operator|+
name|jksPath
operator|.
name|toUri
argument_list|()
decl_stmt|;
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|testDir
argument_list|,
literal|"test.jks"
argument_list|)
decl_stmt|;
name|file
operator|.
name|delete
argument_list|()
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|CredentialProviderFactory
operator|.
name|CREDENTIAL_PROVIDER_PATH
argument_list|,
name|ourUrl
argument_list|)
expr_stmt|;
name|CredentialProvider
name|provider
init|=
name|CredentialProviderFactory
operator|.
name|getProviders
argument_list|(
name|conf
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|char
index|[]
name|keypass
init|=
block|{
literal|'k'
block|,
literal|'e'
block|,
literal|'y'
block|,
literal|'p'
block|,
literal|'a'
block|,
literal|'s'
block|,
literal|'s'
block|}
decl_stmt|;
name|char
index|[]
name|storepass
init|=
block|{
literal|'s'
block|,
literal|'t'
block|,
literal|'o'
block|,
literal|'r'
block|,
literal|'e'
block|,
literal|'p'
block|,
literal|'a'
block|,
literal|'s'
block|,
literal|'s'
block|}
decl_stmt|;
name|char
index|[]
name|trustpass
init|=
block|{
literal|'t'
block|,
literal|'r'
block|,
literal|'u'
block|,
literal|'s'
block|,
literal|'t'
block|,
literal|'p'
block|,
literal|'a'
block|,
literal|'s'
block|,
literal|'s'
block|}
decl_stmt|;
comment|// ensure that we get nulls when the key isn't there
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|provider
operator|.
name|getCredentialEntry
argument_list|(
name|WebAppUtils
operator|.
name|WEB_APP_KEY_PASSWORD_KEY
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|provider
operator|.
name|getCredentialEntry
argument_list|(
name|WebAppUtils
operator|.
name|WEB_APP_KEYSTORE_PASSWORD_KEY
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|provider
operator|.
name|getCredentialEntry
argument_list|(
name|WebAppUtils
operator|.
name|WEB_APP_TRUSTSTORE_PASSWORD_KEY
argument_list|)
argument_list|)
expr_stmt|;
comment|// create new aliases
try|try
block|{
name|provider
operator|.
name|createCredentialEntry
argument_list|(
name|WebAppUtils
operator|.
name|WEB_APP_KEY_PASSWORD_KEY
argument_list|,
name|keypass
argument_list|)
expr_stmt|;
name|provider
operator|.
name|createCredentialEntry
argument_list|(
name|WebAppUtils
operator|.
name|WEB_APP_KEYSTORE_PASSWORD_KEY
argument_list|,
name|storepass
argument_list|)
expr_stmt|;
name|provider
operator|.
name|createCredentialEntry
argument_list|(
name|WebAppUtils
operator|.
name|WEB_APP_TRUSTSTORE_PASSWORD_KEY
argument_list|,
name|trustpass
argument_list|)
expr_stmt|;
comment|// write out so that it can be found in checks
name|provider
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
throw|throw
name|e
throw|;
block|}
comment|// make sure we get back the right key directly from api
name|assertArrayEquals
argument_list|(
name|keypass
argument_list|,
name|provider
operator|.
name|getCredentialEntry
argument_list|(
name|WebAppUtils
operator|.
name|WEB_APP_KEY_PASSWORD_KEY
argument_list|)
operator|.
name|getCredential
argument_list|()
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|storepass
argument_list|,
name|provider
operator|.
name|getCredentialEntry
argument_list|(
name|WebAppUtils
operator|.
name|WEB_APP_KEYSTORE_PASSWORD_KEY
argument_list|)
operator|.
name|getCredential
argument_list|()
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|trustpass
argument_list|,
name|provider
operator|.
name|getCredentialEntry
argument_list|(
name|WebAppUtils
operator|.
name|WEB_APP_TRUSTSTORE_PASSWORD_KEY
argument_list|)
operator|.
name|getCredential
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|conf
return|;
block|}
annotation|@
name|Test
DECL|method|testAppendQueryParams ()
specifier|public
name|void
name|testAppendQueryParams
parameter_list|()
throws|throws
name|Exception
block|{
name|HttpServletRequest
name|request
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|HttpServletRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|String
name|targetUri
init|=
literal|"/test/path"
decl_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|request
operator|.
name|getCharacterEncoding
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|paramResultMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|paramResultMap
operator|.
name|put
argument_list|(
literal|"param1=x"
argument_list|,
name|targetUri
operator|+
literal|"?"
operator|+
literal|"param1=x"
argument_list|)
expr_stmt|;
name|paramResultMap
operator|.
name|put
argument_list|(
literal|"param1=x&param2=y"
argument_list|,
name|targetUri
operator|+
literal|"?"
operator|+
literal|"param1=x&param2=y"
argument_list|)
expr_stmt|;
name|paramResultMap
operator|.
name|put
argument_list|(
literal|"param1=x&param2=y&param3=x+y"
argument_list|,
name|targetUri
operator|+
literal|"?"
operator|+
literal|"param1=x&param2=y&param3=x+y"
argument_list|)
expr_stmt|;
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
name|paramResultMap
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Mockito
operator|.
name|when
argument_list|(
name|request
operator|.
name|getQueryString
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|uri
init|=
name|WebAppUtils
operator|.
name|appendQueryParams
argument_list|(
name|request
argument_list|,
name|targetUri
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|,
name|uri
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testGetHtmlEscapedURIWithQueryString ()
specifier|public
name|void
name|testGetHtmlEscapedURIWithQueryString
parameter_list|()
throws|throws
name|Exception
block|{
name|HttpServletRequest
name|request
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|HttpServletRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|String
name|targetUri
init|=
literal|"/test/path"
decl_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|request
operator|.
name|getCharacterEncoding
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|request
operator|.
name|getRequestURI
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|targetUri
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|paramResultMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|paramResultMap
operator|.
name|put
argument_list|(
literal|"param1=x"
argument_list|,
name|targetUri
operator|+
literal|"?"
operator|+
literal|"param1=x"
argument_list|)
expr_stmt|;
name|paramResultMap
operator|.
name|put
argument_list|(
literal|"param1=x&param2=y"
argument_list|,
name|targetUri
operator|+
literal|"?"
operator|+
literal|"param1=x&amp;param2=y"
argument_list|)
expr_stmt|;
name|paramResultMap
operator|.
name|put
argument_list|(
literal|"param1=x&param2=y&param3=x+y"
argument_list|,
name|targetUri
operator|+
literal|"?"
operator|+
literal|"param1=x&amp;param2=y&amp;param3=x+y"
argument_list|)
expr_stmt|;
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
name|paramResultMap
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Mockito
operator|.
name|when
argument_list|(
name|request
operator|.
name|getQueryString
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|uri
init|=
name|WebAppUtils
operator|.
name|getHtmlEscapedURIWithQueryString
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|,
name|uri
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|TestBuilder
specifier|public
class|class
name|TestBuilder
extends|extends
name|HttpServer2
operator|.
name|Builder
block|{
DECL|field|keypass
specifier|public
name|String
name|keypass
decl_stmt|;
DECL|field|keystorePassword
specifier|public
name|String
name|keystorePassword
decl_stmt|;
DECL|field|truststorePassword
specifier|public
name|String
name|truststorePassword
decl_stmt|;
annotation|@
name|Override
DECL|method|trustStore (String location, String password, String type)
specifier|public
name|Builder
name|trustStore
parameter_list|(
name|String
name|location
parameter_list|,
name|String
name|password
parameter_list|,
name|String
name|type
parameter_list|)
block|{
name|truststorePassword
operator|=
name|password
expr_stmt|;
return|return
name|super
operator|.
name|trustStore
argument_list|(
name|location
argument_list|,
name|password
argument_list|,
name|type
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|keyStore (String location, String password, String type)
specifier|public
name|Builder
name|keyStore
parameter_list|(
name|String
name|location
parameter_list|,
name|String
name|password
parameter_list|,
name|String
name|type
parameter_list|)
block|{
name|keystorePassword
operator|=
name|password
expr_stmt|;
return|return
name|super
operator|.
name|keyStore
argument_list|(
name|location
argument_list|,
name|password
argument_list|,
name|type
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|keyPassword (String password)
specifier|public
name|Builder
name|keyPassword
parameter_list|(
name|String
name|password
parameter_list|)
block|{
name|keypass
operator|=
name|password
expr_stmt|;
return|return
name|super
operator|.
name|keyPassword
argument_list|(
name|password
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

