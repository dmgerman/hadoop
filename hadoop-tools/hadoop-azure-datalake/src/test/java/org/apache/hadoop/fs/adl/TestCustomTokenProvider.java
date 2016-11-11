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
name|com
operator|.
name|squareup
operator|.
name|okhttp
operator|.
name|mockwebserver
operator|.
name|MockResponse
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
name|FileStatus
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
name|permission
operator|.
name|FsPermission
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
name|junit
operator|.
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
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
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|ADL_BLOCK_SIZE
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

begin_comment
comment|/**  * Test access token provider behaviour with custom token provider and for token  * provider cache is enabled.  */
end_comment

begin_class
annotation|@
name|RunWith
argument_list|(
name|Parameterized
operator|.
name|class
argument_list|)
DECL|class|TestCustomTokenProvider
specifier|public
class|class
name|TestCustomTokenProvider
extends|extends
name|AdlMockWebServer
block|{
DECL|field|TEN_MINUTES_IN_MILIS
specifier|private
specifier|static
specifier|final
name|long
name|TEN_MINUTES_IN_MILIS
init|=
literal|600000
decl_stmt|;
DECL|field|backendCallCount
specifier|private
name|int
name|backendCallCount
decl_stmt|;
DECL|field|expectedCallbackToAccessToken
specifier|private
name|int
name|expectedCallbackToAccessToken
decl_stmt|;
DECL|field|fileSystems
specifier|private
name|TestableAdlFileSystem
index|[]
name|fileSystems
decl_stmt|;
DECL|field|typeOfTokenProviderClass
specifier|private
name|Class
name|typeOfTokenProviderClass
decl_stmt|;
DECL|field|expiryFromNow
specifier|private
name|long
name|expiryFromNow
decl_stmt|;
DECL|field|fsObjectCount
specifier|private
name|int
name|fsObjectCount
decl_stmt|;
DECL|method|TestCustomTokenProvider (Class typeOfTokenProviderClass, long expiryFromNow, int fsObjectCount, int backendCallCount, int expectedCallbackToAccessToken)
specifier|public
name|TestCustomTokenProvider
parameter_list|(
name|Class
name|typeOfTokenProviderClass
parameter_list|,
name|long
name|expiryFromNow
parameter_list|,
name|int
name|fsObjectCount
parameter_list|,
name|int
name|backendCallCount
parameter_list|,
name|int
name|expectedCallbackToAccessToken
parameter_list|)
throws|throws
name|IllegalAccessException
throws|,
name|InstantiationException
throws|,
name|URISyntaxException
throws|,
name|IOException
block|{
name|this
operator|.
name|typeOfTokenProviderClass
operator|=
name|typeOfTokenProviderClass
expr_stmt|;
name|this
operator|.
name|expiryFromNow
operator|=
name|expiryFromNow
expr_stmt|;
name|this
operator|.
name|fsObjectCount
operator|=
name|fsObjectCount
expr_stmt|;
name|this
operator|.
name|backendCallCount
operator|=
name|backendCallCount
expr_stmt|;
name|this
operator|.
name|expectedCallbackToAccessToken
operator|=
name|expectedCallbackToAccessToken
expr_stmt|;
block|}
annotation|@
name|Parameterized
operator|.
name|Parameters
argument_list|(
name|name
operator|=
literal|"{index}"
argument_list|)
DECL|method|testDataForTokenProvider ()
specifier|public
specifier|static
name|Collection
name|testDataForTokenProvider
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Object
index|[]
index|[]
block|{
comment|// Data set in order
comment|// INPUT - CustomTokenProvider class to load
comment|// INPUT - expiry time in milis. Subtract from current time
comment|// INPUT - No. of FileSystem object
comment|// INPUT - No. of backend calls per FileSystem object
comment|// EXPECTED - Number of callbacks to get token after test finished.
block|{
name|CustomMockTokenProvider
operator|.
name|class
block|,
literal|0
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|}
block|,
block|{
name|CustomMockTokenProvider
operator|.
name|class
block|,
name|TEN_MINUTES_IN_MILIS
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|}
block|,
block|{
name|CustomMockTokenProvider
operator|.
name|class
block|,
name|TEN_MINUTES_IN_MILIS
block|,
literal|2
block|,
literal|1
block|,
literal|2
block|}
block|,
block|{
name|CustomMockTokenProvider
operator|.
name|class
block|,
name|TEN_MINUTES_IN_MILIS
block|,
literal|10
block|,
literal|10
block|,
literal|10
block|}
block|}
argument_list|)
return|;
block|}
comment|/**    * Explicitly invoked init so that base class mock server is setup before    * test data initialization is done.    *    * @throws IOException    * @throws URISyntaxException    */
DECL|method|init ()
specifier|public
name|void
name|init
parameter_list|()
throws|throws
name|IOException
throws|,
name|URISyntaxException
block|{
name|Configuration
name|configuration
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|configuration
operator|.
name|set
argument_list|(
name|AZURE_AD_TOKEN_PROVIDER_CLASS_KEY
argument_list|,
name|typeOfTokenProviderClass
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|fileSystems
operator|=
operator|new
name|TestableAdlFileSystem
index|[
name|fsObjectCount
index|]
expr_stmt|;
name|URI
name|uri
init|=
operator|new
name|URI
argument_list|(
literal|"adl://localhost:"
operator|+
name|getPort
argument_list|()
argument_list|)
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
name|fsObjectCount
condition|;
operator|++
name|i
control|)
block|{
name|fileSystems
index|[
name|i
index|]
operator|=
operator|new
name|TestableAdlFileSystem
argument_list|()
expr_stmt|;
name|fileSystems
index|[
name|i
index|]
operator|.
name|initialize
argument_list|(
name|uri
argument_list|,
name|configuration
argument_list|)
expr_stmt|;
operator|(
operator|(
name|CustomMockTokenProvider
operator|)
name|fileSystems
index|[
name|i
index|]
operator|.
name|getAzureTokenProvider
argument_list|()
operator|)
operator|.
name|setExpiryTimeInMillisAfter
argument_list|(
name|expiryFromNow
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testCustomTokenManagement ()
specifier|public
name|void
name|testCustomTokenManagement
parameter_list|()
throws|throws
name|IOException
throws|,
name|URISyntaxException
block|{
name|int
name|accessTokenCallbackDuringExec
init|=
literal|0
decl_stmt|;
name|init
argument_list|()
expr_stmt|;
for|for
control|(
name|TestableAdlFileSystem
name|tfs
range|:
name|fileSystems
control|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|backendCallCount
condition|;
operator|++
name|i
control|)
block|{
name|getMockServer
argument_list|()
operator|.
name|enqueue
argument_list|(
operator|new
name|MockResponse
argument_list|()
operator|.
name|setResponseCode
argument_list|(
literal|200
argument_list|)
operator|.
name|setBody
argument_list|(
name|TestADLResponseData
operator|.
name|getGetFileStatusJSONResponse
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|FileStatus
name|fileStatus
init|=
name|tfs
operator|.
name|getFileStatus
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/test1/test2"
argument_list|)
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|fileStatus
operator|.
name|isFile
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"adl://"
operator|+
name|getMockServer
argument_list|()
operator|.
name|getHostName
argument_list|()
operator|+
literal|":"
operator|+
name|getMockServer
argument_list|()
operator|.
name|getPort
argument_list|()
operator|+
literal|"/test1/test2"
argument_list|,
name|fileStatus
operator|.
name|getPath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|4194304
argument_list|,
name|fileStatus
operator|.
name|getLen
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|ADL_BLOCK_SIZE
argument_list|,
name|fileStatus
operator|.
name|getBlockSize
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|fileStatus
operator|.
name|getReplication
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
operator|new
name|FsPermission
argument_list|(
literal|"777"
argument_list|)
argument_list|,
name|fileStatus
operator|.
name|getPermission
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"NotSupportYet"
argument_list|,
name|fileStatus
operator|.
name|getOwner
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"NotSupportYet"
argument_list|,
name|fileStatus
operator|.
name|getGroup
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|accessTokenCallbackDuringExec
operator|+=
operator|(
operator|(
name|CustomMockTokenProvider
operator|)
name|tfs
operator|.
name|getAzureTokenProvider
argument_list|()
operator|)
operator|.
name|getAccessTokenRequestCount
argument_list|()
expr_stmt|;
block|}
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expectedCallbackToAccessToken
argument_list|,
name|accessTokenCallbackDuringExec
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

