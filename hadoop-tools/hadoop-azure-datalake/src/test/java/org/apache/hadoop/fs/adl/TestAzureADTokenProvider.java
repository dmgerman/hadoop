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
name|TokenProviderType
operator|.
name|*
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

begin_comment
comment|/**  * Test appropriate token provider is loaded as per configuration.  */
end_comment

begin_class
DECL|class|TestAzureADTokenProvider
specifier|public
class|class
name|TestAzureADTokenProvider
block|{
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
literal|"dfs.adls.oauth2.access.token.provider"
argument_list|)
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
block|}
end_class

end_unit

