begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.webproxy.amfilter
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
operator|.
name|amfilter
package|;
end_package

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
name|http
operator|.
name|FilterContainer
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
name|FilterInitializer
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
name|HttpConfig
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
name|ApplicationConstants
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

begin_class
DECL|class|AmFilterInitializer
specifier|public
class|class
name|AmFilterInitializer
extends|extends
name|FilterInitializer
block|{
DECL|field|FILTER_NAME
specifier|private
specifier|static
specifier|final
name|String
name|FILTER_NAME
init|=
literal|"AM_PROXY_FILTER"
decl_stmt|;
DECL|field|FILTER_CLASS
specifier|private
specifier|static
specifier|final
name|String
name|FILTER_CLASS
init|=
name|AmIpFilter
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|initFilter (FilterContainer container, Configuration conf)
specifier|public
name|void
name|initFilter
parameter_list|(
name|FilterContainer
name|container
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|String
name|proxy
init|=
name|YarnConfiguration
operator|.
name|getProxyHostAndPort
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|String
index|[]
name|parts
init|=
name|proxy
operator|.
name|split
argument_list|(
literal|":"
argument_list|)
decl_stmt|;
name|params
operator|.
name|put
argument_list|(
name|AmIpFilter
operator|.
name|PROXY_HOST
argument_list|,
name|parts
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
name|AmIpFilter
operator|.
name|PROXY_URI_BASE
argument_list|,
name|HttpConfig
operator|.
name|getSchemePrefix
argument_list|()
operator|+
name|proxy
operator|+
name|System
operator|.
name|getenv
argument_list|(
name|ApplicationConstants
operator|.
name|APPLICATION_WEB_PROXY_BASE_ENV
argument_list|)
argument_list|)
expr_stmt|;
name|container
operator|.
name|addFilter
argument_list|(
name|FILTER_NAME
argument_list|,
name|FILTER_CLASS
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

