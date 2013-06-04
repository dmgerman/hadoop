begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.webproxy
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|webproxy
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
name|*
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ApplicationId
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
name|server
operator|.
name|utils
operator|.
name|BuilderUtils
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
name|util
operator|.
name|TrackingUriPlugin
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
import|;
end_import

begin_class
DECL|class|TestProxyUriUtils
specifier|public
class|class
name|TestProxyUriUtils
block|{
annotation|@
name|Test
DECL|method|testGetPathApplicationId ()
specifier|public
name|void
name|testGetPathApplicationId
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"/proxy/application_100_0001"
argument_list|,
name|ProxyUriUtils
operator|.
name|getPath
argument_list|(
name|BuilderUtils
operator|.
name|newApplicationId
argument_list|(
literal|100l
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/proxy/application_6384623_0005"
argument_list|,
name|ProxyUriUtils
operator|.
name|getPath
argument_list|(
name|BuilderUtils
operator|.
name|newApplicationId
argument_list|(
literal|6384623l
argument_list|,
literal|5
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalArgumentException
operator|.
name|class
argument_list|)
DECL|method|testGetPathApplicationIdBad ()
specifier|public
name|void
name|testGetPathApplicationIdBad
parameter_list|()
block|{
name|ProxyUriUtils
operator|.
name|getPath
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetPathApplicationIdString ()
specifier|public
name|void
name|testGetPathApplicationIdString
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"/proxy/application_6384623_0005"
argument_list|,
name|ProxyUriUtils
operator|.
name|getPath
argument_list|(
name|BuilderUtils
operator|.
name|newApplicationId
argument_list|(
literal|6384623l
argument_list|,
literal|5
argument_list|)
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/proxy/application_6384623_0005/static/app"
argument_list|,
name|ProxyUriUtils
operator|.
name|getPath
argument_list|(
name|BuilderUtils
operator|.
name|newApplicationId
argument_list|(
literal|6384623l
argument_list|,
literal|5
argument_list|)
argument_list|,
literal|"/static/app"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/proxy/application_6384623_0005/"
argument_list|,
name|ProxyUriUtils
operator|.
name|getPath
argument_list|(
name|BuilderUtils
operator|.
name|newApplicationId
argument_list|(
literal|6384623l
argument_list|,
literal|5
argument_list|)
argument_list|,
literal|"/"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/proxy/application_6384623_0005/some/path"
argument_list|,
name|ProxyUriUtils
operator|.
name|getPath
argument_list|(
name|BuilderUtils
operator|.
name|newApplicationId
argument_list|(
literal|6384623l
argument_list|,
literal|5
argument_list|)
argument_list|,
literal|"some/path"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetPathAndQuery ()
specifier|public
name|void
name|testGetPathAndQuery
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"/proxy/application_6384623_0005/static/app?foo=bar"
argument_list|,
name|ProxyUriUtils
operator|.
name|getPathAndQuery
argument_list|(
name|BuilderUtils
operator|.
name|newApplicationId
argument_list|(
literal|6384623l
argument_list|,
literal|5
argument_list|)
argument_list|,
literal|"/static/app"
argument_list|,
literal|"?foo=bar"
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/proxy/application_6384623_0005/static/app?foo=bar&bad=good&proxyapproved=true"
argument_list|,
name|ProxyUriUtils
operator|.
name|getPathAndQuery
argument_list|(
name|BuilderUtils
operator|.
name|newApplicationId
argument_list|(
literal|6384623l
argument_list|,
literal|5
argument_list|)
argument_list|,
literal|"/static/app"
argument_list|,
literal|"foo=bar&bad=good"
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetProxyUri ()
specifier|public
name|void
name|testGetProxyUri
parameter_list|()
throws|throws
name|Exception
block|{
name|URI
name|originalUri
init|=
operator|new
name|URI
argument_list|(
literal|"http://host.com/static/foo?bar=bar"
argument_list|)
decl_stmt|;
name|URI
name|proxyUri
init|=
operator|new
name|URI
argument_list|(
literal|"http://proxy.net:8080/"
argument_list|)
decl_stmt|;
name|ApplicationId
name|id
init|=
name|BuilderUtils
operator|.
name|newApplicationId
argument_list|(
literal|6384623l
argument_list|,
literal|5
argument_list|)
decl_stmt|;
name|URI
name|expected
init|=
operator|new
name|URI
argument_list|(
literal|"http://proxy.net:8080/proxy/application_6384623_0005/static/foo?bar=bar"
argument_list|)
decl_stmt|;
name|URI
name|result
init|=
name|ProxyUriUtils
operator|.
name|getProxyUri
argument_list|(
name|originalUri
argument_list|,
name|proxyUri
argument_list|,
name|id
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetProxyUriNull ()
specifier|public
name|void
name|testGetProxyUriNull
parameter_list|()
throws|throws
name|Exception
block|{
name|URI
name|originalUri
init|=
literal|null
decl_stmt|;
name|URI
name|proxyUri
init|=
operator|new
name|URI
argument_list|(
literal|"http://proxy.net:8080/"
argument_list|)
decl_stmt|;
name|ApplicationId
name|id
init|=
name|BuilderUtils
operator|.
name|newApplicationId
argument_list|(
literal|6384623l
argument_list|,
literal|5
argument_list|)
decl_stmt|;
name|URI
name|expected
init|=
operator|new
name|URI
argument_list|(
literal|"http://proxy.net:8080/proxy/application_6384623_0005/"
argument_list|)
decl_stmt|;
name|URI
name|result
init|=
name|ProxyUriUtils
operator|.
name|getProxyUri
argument_list|(
name|originalUri
argument_list|,
name|proxyUri
argument_list|,
name|id
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetProxyUriFromPluginsReturnsNullIfNoPlugins ()
specifier|public
name|void
name|testGetProxyUriFromPluginsReturnsNullIfNoPlugins
parameter_list|()
throws|throws
name|URISyntaxException
block|{
name|ApplicationId
name|id
init|=
name|BuilderUtils
operator|.
name|newApplicationId
argument_list|(
literal|6384623l
argument_list|,
literal|5
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|TrackingUriPlugin
argument_list|>
name|list
init|=
name|Lists
operator|.
name|newArrayListWithExpectedSize
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|ProxyUriUtils
operator|.
name|getUriFromTrackingPlugins
argument_list|(
name|id
argument_list|,
name|list
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetProxyUriFromPluginsReturnsValidUriWhenAble ()
specifier|public
name|void
name|testGetProxyUriFromPluginsReturnsValidUriWhenAble
parameter_list|()
throws|throws
name|URISyntaxException
block|{
name|ApplicationId
name|id
init|=
name|BuilderUtils
operator|.
name|newApplicationId
argument_list|(
literal|6384623l
argument_list|,
literal|5
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|TrackingUriPlugin
argument_list|>
name|list
init|=
name|Lists
operator|.
name|newArrayListWithExpectedSize
argument_list|(
literal|2
argument_list|)
decl_stmt|;
comment|// Insert a plugin that returns null.
name|list
operator|.
name|add
argument_list|(
operator|new
name|TrackingUriPlugin
argument_list|()
block|{
specifier|public
name|URI
name|getTrackingUri
parameter_list|(
name|ApplicationId
name|id
parameter_list|)
throws|throws
name|URISyntaxException
block|{
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
comment|// Insert a plugin that returns a valid URI.
name|list
operator|.
name|add
argument_list|(
operator|new
name|TrackingUriPlugin
argument_list|()
block|{
specifier|public
name|URI
name|getTrackingUri
parameter_list|(
name|ApplicationId
name|id
parameter_list|)
throws|throws
name|URISyntaxException
block|{
return|return
operator|new
name|URI
argument_list|(
literal|"http://history.server.net/"
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|URI
name|result
init|=
name|ProxyUriUtils
operator|.
name|getUriFromTrackingPlugins
argument_list|(
name|id
argument_list|,
name|list
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

