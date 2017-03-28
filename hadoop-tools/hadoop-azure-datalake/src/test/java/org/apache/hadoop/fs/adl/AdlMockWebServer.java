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
name|java
operator|.
name|net
operator|.
name|URL
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
import|import
name|com
operator|.
name|squareup
operator|.
name|okhttp
operator|.
name|mockwebserver
operator|.
name|MockWebServer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_comment
comment|/**  * Mock server to simulate Adls backend calls. This infrastructure is expandable  * to override expected server response based on the derived test functionality.  * Common functionality to generate token information before request is send to  * adls backend is also managed within AdlMockWebServer implementation using  * {@link org.apache.hadoop.fs.adl.common.CustomMockTokenProvider}.  */
end_comment

begin_class
DECL|class|AdlMockWebServer
specifier|public
class|class
name|AdlMockWebServer
block|{
comment|// Create a MockWebServer. These are lean enough that you can create a new
comment|// instance for every unit test.
DECL|field|server
specifier|private
name|MockWebServer
name|server
init|=
literal|null
decl_stmt|;
DECL|field|fs
specifier|private
name|TestableAdlFileSystem
name|fs
init|=
literal|null
decl_stmt|;
DECL|field|port
specifier|private
name|int
name|port
init|=
literal|0
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
DECL|method|getMockServer ()
specifier|public
name|MockWebServer
name|getMockServer
parameter_list|()
block|{
return|return
name|server
return|;
block|}
DECL|method|getMockAdlFileSystem ()
specifier|public
name|TestableAdlFileSystem
name|getMockAdlFileSystem
parameter_list|()
block|{
return|return
name|fs
return|;
block|}
DECL|method|getPort ()
specifier|public
name|int
name|getPort
parameter_list|()
block|{
return|return
name|port
return|;
block|}
DECL|method|getConf ()
specifier|public
name|Configuration
name|getConf
parameter_list|()
block|{
return|return
name|conf
return|;
block|}
DECL|method|setConf (Configuration conf)
specifier|public
name|void
name|setConf
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
block|}
annotation|@
name|Before
DECL|method|preTestSetup ()
specifier|public
name|void
name|preTestSetup
parameter_list|()
throws|throws
name|IOException
throws|,
name|URISyntaxException
block|{
name|server
operator|=
operator|new
name|MockWebServer
argument_list|()
expr_stmt|;
comment|// Start the server.
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// Ask the server for its URL. You'll need this to make HTTP requests.
name|URL
name|baseUrl
init|=
name|server
operator|.
name|getUrl
argument_list|(
literal|""
argument_list|)
decl_stmt|;
name|port
operator|=
name|baseUrl
operator|.
name|getPort
argument_list|()
expr_stmt|;
comment|// Exercise your application code, which should make those HTTP requests.
comment|// Responses are returned in the same order that they are enqueued.
name|fs
operator|=
operator|new
name|TestableAdlFileSystem
argument_list|()
expr_stmt|;
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
literal|"adl://localhost:"
operator|+
name|port
argument_list|)
decl_stmt|;
name|fs
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
name|After
DECL|method|postTestSetup ()
specifier|public
name|void
name|postTestSetup
parameter_list|()
throws|throws
name|IOException
block|{
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
name|server
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

