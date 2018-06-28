begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.adl
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|adl
package|;
end_package

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
name|com
operator|.
name|microsoft
operator|.
name|azure
operator|.
name|datalake
operator|.
name|store
operator|.
name|oauth2
operator|.
name|DeviceCodeTokenProvider
import|;
end_import

begin_import
import|import
name|com
operator|.
name|microsoft
operator|.
name|azure
operator|.
name|datalake
operator|.
name|store
operator|.
name|oauth2
operator|.
name|MsiTokenProvider
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
name|builder
operator|.
name|EqualsBuilder
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
name|adl
operator|.
name|common
operator|.
name|CustomMockTokenProvider
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
name|adl
operator|.
name|oauth2
operator|.
name|AzureADTokenProvider
import|;
end_import

begin_import
import|import
name|com
operator|.
name|microsoft
operator|.
name|azure
operator|.
name|datalake
operator|.
name|store
operator|.
name|oauth2
operator|.
name|AccessTokenProvider
import|;
end_import

begin_import
import|import
name|com
operator|.
name|microsoft
operator|.
name|azure
operator|.
name|datalake
operator|.
name|store
operator|.
name|oauth2
operator|.
name|ClientCredsTokenProvider
import|;
end_import

begin_import
import|import
name|com
operator|.
name|microsoft
operator|.
name|azure
operator|.
name|datalake
operator|.
name|store
operator|.
name|oauth2
operator|.
name|RefreshTokenBasedTokenProvider
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
name|adl
operator|.
name|AdlConfKeys
operator|.
name|AZURE_AD_CLIENT_ID_KEY
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
name|adl
operator|.
name|AdlConfKeys
operator|.
name|AZURE_AD_CLIENT_SECRET_KEY
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
name|adl
operator|.
name|AdlConfKeys
operator|.
name|AZURE_AD_REFRESH_TOKEN_KEY
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
name|adl
operator|.
name|AdlConfKeys
operator|.
name|AZURE_AD_REFRESH_URL_KEY
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
name|adl
operator|.
name|AdlConfKeys
operator|.
name|AZURE_AD_TOKEN_PROVIDER_CLASS_KEY
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
name|adl
operator|.
name|AdlConfKeys
operator|.
name|AZURE_AD_TOKEN_PROVIDER_TYPE_KEY
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
name|adl
operator|.
name|AdlConfKeys
operator|.
name|DEVICE_CODE_CLIENT_APP_ID
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
name|adl
operator|.
name|TokenProviderType
operator|.
name|*
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
operator|.
name|ProviderUtils
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
name|test
operator|.
name|GenericTestUtils
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
name|Rule
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
name|junit
operator|.
name|rules
operator|.
name|TemporaryFolder
import|;
end_import

begin_comment
comment|/**  * Test appropriate token provider is loaded as per configuration.  */
end_comment

begin_class
DECL|class|TestAzureADTokenProvider
specifier|public
class|class
name|TestAzureADTokenProvider
block|{
DECL|field|CLIENT_ID
specifier|private
specifier|static
specifier|final
name|String
name|CLIENT_ID
init|=
literal|"MY_CLIENT_ID"
decl_stmt|;
DECL|field|REFRESH_TOKEN
specifier|private
specifier|static
specifier|final
name|String
name|REFRESH_TOKEN
init|=
literal|"MY_REFRESH_TOKEN"
decl_stmt|;
DECL|field|CLIENT_SECRET
specifier|private
specifier|static
specifier|final
name|String
name|CLIENT_SECRET
init|=
literal|"MY_CLIENT_SECRET"
decl_stmt|;
DECL|field|REFRESH_URL
specifier|private
specifier|static
specifier|final
name|String
name|REFRESH_URL
init|=
literal|"http://localhost:8080/refresh"
decl_stmt|;
annotation|@
name|Rule
DECL|field|tempDir
specifier|public
specifier|final
name|TemporaryFolder
name|tempDir
init|=
operator|new
name|TemporaryFolder
argument_list|()
decl_stmt|;
annotation|@
name|Test
DECL|method|testRefreshTokenProvider ()
specifier|public
name|void
name|testRefreshTokenProvider
parameter_list|()
throws|throws
name|URISyntaxException
throws|,
name|IOException
block|{
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
name|AZURE_AD_CLIENT_ID_KEY
argument_list|,
literal|"MY_CLIENTID"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|AZURE_AD_REFRESH_TOKEN_KEY
argument_list|,
literal|"XYZ"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setEnum
argument_list|(
name|AZURE_AD_TOKEN_PROVIDER_TYPE_KEY
argument_list|,
name|RefreshToken
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|AZURE_AD_REFRESH_URL_KEY
argument_list|,
literal|"http://localhost:8080/refresh"
argument_list|)
expr_stmt|;
name|URI
name|uri
init|=
operator|new
name|URI
argument_list|(
literal|"adl://localhost:8080"
argument_list|)
decl_stmt|;
name|AdlFileSystem
name|fileSystem
init|=
operator|new
name|AdlFileSystem
argument_list|()
decl_stmt|;
name|fileSystem
operator|.
name|initialize
argument_list|(
name|uri
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|AccessTokenProvider
name|tokenProvider
init|=
name|fileSystem
operator|.
name|getTokenProvider
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|tokenProvider
operator|instanceof
name|RefreshTokenBasedTokenProvider
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testClientCredTokenProvider ()
specifier|public
name|void
name|testClientCredTokenProvider
parameter_list|()
throws|throws
name|IOException
throws|,
name|URISyntaxException
block|{
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
name|AZURE_AD_CLIENT_ID_KEY
argument_list|,
literal|"MY_CLIENTID"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|AZURE_AD_CLIENT_SECRET_KEY
argument_list|,
literal|"XYZ"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setEnum
argument_list|(
name|AZURE_AD_TOKEN_PROVIDER_TYPE_KEY
argument_list|,
name|ClientCredential
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|AZURE_AD_REFRESH_URL_KEY
argument_list|,
literal|"http://localhost:8080/refresh"
argument_list|)
expr_stmt|;
name|URI
name|uri
init|=
operator|new
name|URI
argument_list|(
literal|"adl://localhost:8080"
argument_list|)
decl_stmt|;
name|AdlFileSystem
name|fileSystem
init|=
operator|new
name|AdlFileSystem
argument_list|()
decl_stmt|;
name|fileSystem
operator|.
name|initialize
argument_list|(
name|uri
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|AccessTokenProvider
name|tokenProvider
init|=
name|fileSystem
operator|.
name|getTokenProvider
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|tokenProvider
operator|instanceof
name|ClientCredsTokenProvider
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMSITokenProvider ()
specifier|public
name|void
name|testMSITokenProvider
parameter_list|()
throws|throws
name|IOException
throws|,
name|URISyntaxException
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setEnum
argument_list|(
name|AZURE_AD_TOKEN_PROVIDER_TYPE_KEY
argument_list|,
name|MSI
argument_list|)
expr_stmt|;
name|URI
name|uri
init|=
operator|new
name|URI
argument_list|(
literal|"adl://localhost:8080"
argument_list|)
decl_stmt|;
name|AdlFileSystem
name|fileSystem
init|=
operator|new
name|AdlFileSystem
argument_list|()
decl_stmt|;
name|fileSystem
operator|.
name|initialize
argument_list|(
name|uri
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|AccessTokenProvider
name|tokenProvider
init|=
name|fileSystem
operator|.
name|getTokenProvider
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|tokenProvider
operator|instanceof
name|MsiTokenProvider
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDeviceCodeTokenProvider ()
specifier|public
name|void
name|testDeviceCodeTokenProvider
parameter_list|()
throws|throws
name|IOException
throws|,
name|URISyntaxException
block|{
name|boolean
name|runTest
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|runTest
condition|)
block|{
comment|// Device code auth method causes an interactive prompt, so run this only
comment|// when running the test interactively at a local terminal. Disabling
comment|// test by default, to not break any automation.
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setEnum
argument_list|(
name|AZURE_AD_TOKEN_PROVIDER_TYPE_KEY
argument_list|,
name|DeviceCode
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DEVICE_CODE_CLIENT_APP_ID
argument_list|,
literal|"CLIENT_APP_ID_GUID"
argument_list|)
expr_stmt|;
name|URI
name|uri
init|=
operator|new
name|URI
argument_list|(
literal|"adl://localhost:8080"
argument_list|)
decl_stmt|;
name|AdlFileSystem
name|fileSystem
init|=
operator|new
name|AdlFileSystem
argument_list|()
decl_stmt|;
name|fileSystem
operator|.
name|initialize
argument_list|(
name|uri
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|AccessTokenProvider
name|tokenProvider
init|=
name|fileSystem
operator|.
name|getTokenProvider
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|tokenProvider
operator|instanceof
name|DeviceCodeTokenProvider
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testCustomCredTokenProvider ()
specifier|public
name|void
name|testCustomCredTokenProvider
parameter_list|()
throws|throws
name|URISyntaxException
throws|,
name|IOException
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setEnum
argument_list|(
name|AZURE_AD_TOKEN_PROVIDER_TYPE_KEY
argument_list|,
name|TokenProviderType
operator|.
name|Custom
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setClass
argument_list|(
name|AZURE_AD_TOKEN_PROVIDER_CLASS_KEY
argument_list|,
name|CustomMockTokenProvider
operator|.
name|class
argument_list|,
name|AzureADTokenProvider
operator|.
name|class
argument_list|)
expr_stmt|;
name|URI
name|uri
init|=
operator|new
name|URI
argument_list|(
literal|"adl://localhost:8080"
argument_list|)
decl_stmt|;
name|AdlFileSystem
name|fileSystem
init|=
operator|new
name|AdlFileSystem
argument_list|()
decl_stmt|;
name|fileSystem
operator|.
name|initialize
argument_list|(
name|uri
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|AccessTokenProvider
name|tokenProvider
init|=
name|fileSystem
operator|.
name|getTokenProvider
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|tokenProvider
operator|instanceof
name|SdkTokenProviderAdapter
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testInvalidProviderConfigurationForType ()
specifier|public
name|void
name|testInvalidProviderConfigurationForType
parameter_list|()
throws|throws
name|URISyntaxException
throws|,
name|IOException
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setEnum
argument_list|(
name|AZURE_AD_TOKEN_PROVIDER_TYPE_KEY
argument_list|,
name|TokenProviderType
operator|.
name|Custom
argument_list|)
expr_stmt|;
name|URI
name|uri
init|=
operator|new
name|URI
argument_list|(
literal|"adl://localhost:8080"
argument_list|)
decl_stmt|;
name|AdlFileSystem
name|fileSystem
init|=
operator|new
name|AdlFileSystem
argument_list|()
decl_stmt|;
try|try
block|{
name|fileSystem
operator|.
name|initialize
argument_list|(
name|uri
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Initialization should have failed due no token provider "
operator|+
literal|"configuration"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
name|AZURE_AD_TOKEN_PROVIDER_CLASS_KEY
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|conf
operator|.
name|setClass
argument_list|(
name|AZURE_AD_TOKEN_PROVIDER_CLASS_KEY
argument_list|,
name|CustomMockTokenProvider
operator|.
name|class
argument_list|,
name|AzureADTokenProvider
operator|.
name|class
argument_list|)
expr_stmt|;
name|fileSystem
operator|.
name|initialize
argument_list|(
name|uri
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testInvalidProviderConfigurationForClassPath ()
specifier|public
name|void
name|testInvalidProviderConfigurationForClassPath
parameter_list|()
throws|throws
name|URISyntaxException
throws|,
name|IOException
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|URI
name|uri
init|=
operator|new
name|URI
argument_list|(
literal|"adl://localhost:8080"
argument_list|)
decl_stmt|;
name|AdlFileSystem
name|fileSystem
init|=
operator|new
name|AdlFileSystem
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setEnum
argument_list|(
name|AZURE_AD_TOKEN_PROVIDER_TYPE_KEY
argument_list|,
name|TokenProviderType
operator|.
name|Custom
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|AZURE_AD_TOKEN_PROVIDER_CLASS_KEY
argument_list|,
literal|"wrong.classpath.CustomMockTokenProvider"
argument_list|)
expr_stmt|;
try|try
block|{
name|fileSystem
operator|.
name|initialize
argument_list|(
name|uri
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Initialization should have failed due invalid provider "
operator|+
literal|"configuration"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"wrong.classpath.CustomMockTokenProvider"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|createTempCredProvider (Configuration conf)
specifier|private
name|CredentialProvider
name|createTempCredProvider
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|URISyntaxException
throws|,
name|IOException
block|{
specifier|final
name|File
name|file
init|=
name|tempDir
operator|.
name|newFile
argument_list|(
literal|"test.jks"
argument_list|)
decl_stmt|;
specifier|final
name|URI
name|jks
init|=
name|ProviderUtils
operator|.
name|nestURIForLocalJavaKeyStoreProvider
argument_list|(
name|file
operator|.
name|toURI
argument_list|()
argument_list|)
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|CredentialProviderFactory
operator|.
name|CREDENTIAL_PROVIDER_PATH
argument_list|,
name|jks
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
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
return|;
block|}
annotation|@
name|Test
DECL|method|testRefreshTokenWithCredentialProvider ()
specifier|public
name|void
name|testRefreshTokenWithCredentialProvider
parameter_list|()
throws|throws
name|IOException
throws|,
name|URISyntaxException
block|{
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
name|AZURE_AD_CLIENT_ID_KEY
argument_list|,
literal|"DUMMY"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|AZURE_AD_REFRESH_TOKEN_KEY
argument_list|,
literal|"DUMMY"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setEnum
argument_list|(
name|AZURE_AD_TOKEN_PROVIDER_TYPE_KEY
argument_list|,
name|RefreshToken
argument_list|)
expr_stmt|;
name|CredentialProvider
name|provider
init|=
name|createTempCredProvider
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|provider
operator|.
name|createCredentialEntry
argument_list|(
name|AZURE_AD_CLIENT_ID_KEY
argument_list|,
name|CLIENT_ID
operator|.
name|toCharArray
argument_list|()
argument_list|)
expr_stmt|;
name|provider
operator|.
name|createCredentialEntry
argument_list|(
name|AZURE_AD_REFRESH_TOKEN_KEY
argument_list|,
name|REFRESH_TOKEN
operator|.
name|toCharArray
argument_list|()
argument_list|)
expr_stmt|;
name|provider
operator|.
name|flush
argument_list|()
expr_stmt|;
name|URI
name|uri
init|=
operator|new
name|URI
argument_list|(
literal|"adl://localhost:8080"
argument_list|)
decl_stmt|;
name|AdlFileSystem
name|fileSystem
init|=
operator|new
name|AdlFileSystem
argument_list|()
decl_stmt|;
name|fileSystem
operator|.
name|initialize
argument_list|(
name|uri
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|RefreshTokenBasedTokenProvider
name|expected
init|=
operator|new
name|RefreshTokenBasedTokenProvider
argument_list|(
name|CLIENT_ID
argument_list|,
name|REFRESH_TOKEN
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|EqualsBuilder
operator|.
name|reflectionEquals
argument_list|(
name|expected
argument_list|,
name|fileSystem
operator|.
name|getTokenProvider
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRefreshTokenWithCredentialProviderFallback ()
specifier|public
name|void
name|testRefreshTokenWithCredentialProviderFallback
parameter_list|()
throws|throws
name|IOException
throws|,
name|URISyntaxException
block|{
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
name|AZURE_AD_CLIENT_ID_KEY
argument_list|,
name|CLIENT_ID
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|AZURE_AD_REFRESH_TOKEN_KEY
argument_list|,
name|REFRESH_TOKEN
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setEnum
argument_list|(
name|AZURE_AD_TOKEN_PROVIDER_TYPE_KEY
argument_list|,
name|RefreshToken
argument_list|)
expr_stmt|;
name|createTempCredProvider
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|URI
name|uri
init|=
operator|new
name|URI
argument_list|(
literal|"adl://localhost:8080"
argument_list|)
decl_stmt|;
name|AdlFileSystem
name|fileSystem
init|=
operator|new
name|AdlFileSystem
argument_list|()
decl_stmt|;
name|fileSystem
operator|.
name|initialize
argument_list|(
name|uri
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|RefreshTokenBasedTokenProvider
name|expected
init|=
operator|new
name|RefreshTokenBasedTokenProvider
argument_list|(
name|CLIENT_ID
argument_list|,
name|REFRESH_TOKEN
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|EqualsBuilder
operator|.
name|reflectionEquals
argument_list|(
name|expected
argument_list|,
name|fileSystem
operator|.
name|getTokenProvider
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testClientCredWithCredentialProvider ()
specifier|public
name|void
name|testClientCredWithCredentialProvider
parameter_list|()
throws|throws
name|IOException
throws|,
name|URISyntaxException
block|{
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
name|AZURE_AD_CLIENT_ID_KEY
argument_list|,
literal|"DUMMY"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|AZURE_AD_CLIENT_SECRET_KEY
argument_list|,
literal|"DUMMY"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|AZURE_AD_REFRESH_URL_KEY
argument_list|,
literal|"DUMMY"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setEnum
argument_list|(
name|AZURE_AD_TOKEN_PROVIDER_TYPE_KEY
argument_list|,
name|ClientCredential
argument_list|)
expr_stmt|;
name|CredentialProvider
name|provider
init|=
name|createTempCredProvider
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|provider
operator|.
name|createCredentialEntry
argument_list|(
name|AZURE_AD_CLIENT_ID_KEY
argument_list|,
name|CLIENT_ID
operator|.
name|toCharArray
argument_list|()
argument_list|)
expr_stmt|;
name|provider
operator|.
name|createCredentialEntry
argument_list|(
name|AZURE_AD_CLIENT_SECRET_KEY
argument_list|,
name|CLIENT_SECRET
operator|.
name|toCharArray
argument_list|()
argument_list|)
expr_stmt|;
name|provider
operator|.
name|createCredentialEntry
argument_list|(
name|AZURE_AD_REFRESH_URL_KEY
argument_list|,
name|REFRESH_URL
operator|.
name|toCharArray
argument_list|()
argument_list|)
expr_stmt|;
name|provider
operator|.
name|flush
argument_list|()
expr_stmt|;
name|URI
name|uri
init|=
operator|new
name|URI
argument_list|(
literal|"adl://localhost:8080"
argument_list|)
decl_stmt|;
name|AdlFileSystem
name|fileSystem
init|=
operator|new
name|AdlFileSystem
argument_list|()
decl_stmt|;
name|fileSystem
operator|.
name|initialize
argument_list|(
name|uri
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|ClientCredsTokenProvider
name|expected
init|=
operator|new
name|ClientCredsTokenProvider
argument_list|(
name|REFRESH_URL
argument_list|,
name|CLIENT_ID
argument_list|,
name|CLIENT_SECRET
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|EqualsBuilder
operator|.
name|reflectionEquals
argument_list|(
name|expected
argument_list|,
name|fileSystem
operator|.
name|getTokenProvider
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testClientCredWithCredentialProviderFallback ()
specifier|public
name|void
name|testClientCredWithCredentialProviderFallback
parameter_list|()
throws|throws
name|IOException
throws|,
name|URISyntaxException
block|{
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
name|AZURE_AD_CLIENT_ID_KEY
argument_list|,
name|CLIENT_ID
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|AZURE_AD_CLIENT_SECRET_KEY
argument_list|,
name|CLIENT_SECRET
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|AZURE_AD_REFRESH_URL_KEY
argument_list|,
name|REFRESH_URL
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setEnum
argument_list|(
name|AZURE_AD_TOKEN_PROVIDER_TYPE_KEY
argument_list|,
name|ClientCredential
argument_list|)
expr_stmt|;
name|createTempCredProvider
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|URI
name|uri
init|=
operator|new
name|URI
argument_list|(
literal|"adl://localhost:8080"
argument_list|)
decl_stmt|;
name|AdlFileSystem
name|fileSystem
init|=
operator|new
name|AdlFileSystem
argument_list|()
decl_stmt|;
name|fileSystem
operator|.
name|initialize
argument_list|(
name|uri
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|ClientCredsTokenProvider
name|expected
init|=
operator|new
name|ClientCredsTokenProvider
argument_list|(
name|REFRESH_URL
argument_list|,
name|CLIENT_ID
argument_list|,
name|CLIENT_SECRET
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|EqualsBuilder
operator|.
name|reflectionEquals
argument_list|(
name|expected
argument_list|,
name|fileSystem
operator|.
name|getTokenProvider
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCredentialProviderPathExclusions ()
specifier|public
name|void
name|testCredentialProviderPathExclusions
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|providerPath
init|=
literal|"user:///,jceks://adl/user/hrt_qa/sqoopdbpasswd.jceks,"
operator|+
literal|"jceks://hdfs@nn1.example.com/my/path/test.jceks"
decl_stmt|;
name|Configuration
name|config
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|config
operator|.
name|set
argument_list|(
name|CredentialProviderFactory
operator|.
name|CREDENTIAL_PROVIDER_PATH
argument_list|,
name|providerPath
argument_list|)
expr_stmt|;
name|String
name|newPath
init|=
literal|"user:///,jceks://hdfs@nn1.example.com/my/path/test.jceks"
decl_stmt|;
name|excludeAndTestExpectations
argument_list|(
name|config
argument_list|,
name|newPath
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testExcludeAllProviderTypesFromConfig ()
specifier|public
name|void
name|testExcludeAllProviderTypesFromConfig
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|providerPath
init|=
literal|"jceks://adl/tmp/test.jceks,"
operator|+
literal|"jceks://adl@/my/path/test.jceks"
decl_stmt|;
name|Configuration
name|config
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|config
operator|.
name|set
argument_list|(
name|CredentialProviderFactory
operator|.
name|CREDENTIAL_PROVIDER_PATH
argument_list|,
name|providerPath
argument_list|)
expr_stmt|;
name|String
name|newPath
init|=
literal|null
decl_stmt|;
name|excludeAndTestExpectations
argument_list|(
name|config
argument_list|,
name|newPath
argument_list|)
expr_stmt|;
block|}
DECL|method|excludeAndTestExpectations (Configuration config, String newPath)
name|void
name|excludeAndTestExpectations
parameter_list|(
name|Configuration
name|config
parameter_list|,
name|String
name|newPath
parameter_list|)
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
name|ProviderUtils
operator|.
name|excludeIncompatibleCredentialProviders
argument_list|(
name|config
argument_list|,
name|AdlFileSystem
operator|.
name|class
argument_list|)
decl_stmt|;
name|String
name|effectivePath
init|=
name|conf
operator|.
name|get
argument_list|(
name|CredentialProviderFactory
operator|.
name|CREDENTIAL_PROVIDER_PATH
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|newPath
argument_list|,
name|effectivePath
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

