begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|junit
operator|.
name|Before
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
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|webapp
operator|.
name|util
operator|.
name|WebAppUtils
import|;
end_import

begin_comment
comment|/**  * Test class for {@Link AmFilterInitializer}.  */
end_comment

begin_class
DECL|class|TestAmFilterInitializer
specifier|public
class|class
name|TestAmFilterInitializer
block|{
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|NetUtils
operator|.
name|addStaticResolution
argument_list|(
literal|"host1"
argument_list|,
literal|"172.0.0.1"
argument_list|)
expr_stmt|;
name|NetUtils
operator|.
name|addStaticResolution
argument_list|(
literal|"host2"
argument_list|,
literal|"172.0.0.1"
argument_list|)
expr_stmt|;
name|NetUtils
operator|.
name|addStaticResolution
argument_list|(
literal|"host3"
argument_list|,
literal|"172.0.0.1"
argument_list|)
expr_stmt|;
name|NetUtils
operator|.
name|addStaticResolution
argument_list|(
literal|"host4"
argument_list|,
literal|"172.0.0.1"
argument_list|)
expr_stmt|;
name|NetUtils
operator|.
name|addStaticResolution
argument_list|(
literal|"host5"
argument_list|,
literal|"172.0.0.1"
argument_list|)
expr_stmt|;
name|NetUtils
operator|.
name|addStaticResolution
argument_list|(
literal|"host6"
argument_list|,
literal|"172.0.0.1"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testInitFilter ()
specifier|public
name|void
name|testInitFilter
parameter_list|()
block|{
comment|// Check PROXY_ADDRESS
name|MockFilterContainer
name|con
init|=
operator|new
name|MockFilterContainer
argument_list|()
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|PROXY_ADDRESS
argument_list|,
literal|"host1:1000"
argument_list|)
expr_stmt|;
name|AmFilterInitializer
name|afi
init|=
operator|new
name|MockAmFilterInitializer
argument_list|()
decl_stmt|;
name|assertNull
argument_list|(
name|con
operator|.
name|givenParameters
argument_list|)
expr_stmt|;
name|afi
operator|.
name|initFilter
argument_list|(
name|con
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|con
operator|.
name|givenParameters
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"host1"
argument_list|,
name|con
operator|.
name|givenParameters
operator|.
name|get
argument_list|(
name|AmIpFilter
operator|.
name|PROXY_HOSTS
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"http://host1:1000/foo"
argument_list|,
name|con
operator|.
name|givenParameters
operator|.
name|get
argument_list|(
name|AmIpFilter
operator|.
name|PROXY_URI_BASES
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|con
operator|.
name|givenParameters
operator|.
name|get
argument_list|(
name|AmFilterInitializer
operator|.
name|RM_HA_URLS
argument_list|)
argument_list|)
expr_stmt|;
comment|// Check a single RM_WEBAPP_ADDRESS
name|con
operator|=
operator|new
name|MockFilterContainer
argument_list|()
expr_stmt|;
name|conf
operator|=
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_WEBAPP_ADDRESS
argument_list|,
literal|"host2:2000"
argument_list|)
expr_stmt|;
name|afi
operator|=
operator|new
name|MockAmFilterInitializer
argument_list|()
expr_stmt|;
name|assertNull
argument_list|(
name|con
operator|.
name|givenParameters
argument_list|)
expr_stmt|;
name|afi
operator|.
name|initFilter
argument_list|(
name|con
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|con
operator|.
name|givenParameters
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"host2"
argument_list|,
name|con
operator|.
name|givenParameters
operator|.
name|get
argument_list|(
name|AmIpFilter
operator|.
name|PROXY_HOSTS
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"http://host2:2000/foo"
argument_list|,
name|con
operator|.
name|givenParameters
operator|.
name|get
argument_list|(
name|AmIpFilter
operator|.
name|PROXY_URI_BASES
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|con
operator|.
name|givenParameters
operator|.
name|get
argument_list|(
name|AmFilterInitializer
operator|.
name|RM_HA_URLS
argument_list|)
argument_list|)
expr_stmt|;
comment|// Check multiple RM_WEBAPP_ADDRESSes (RM HA)
name|con
operator|=
operator|new
name|MockFilterContainer
argument_list|()
expr_stmt|;
name|conf
operator|=
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|conf
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
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_HA_IDS
argument_list|,
literal|"rm1,rm2,rm3"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_WEBAPP_ADDRESS
operator|+
literal|".rm1"
argument_list|,
literal|"host2:2000"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_WEBAPP_ADDRESS
operator|+
literal|".rm2"
argument_list|,
literal|"host3:3000"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_WEBAPP_ADDRESS
operator|+
literal|".rm3"
argument_list|,
literal|"host4:4000"
argument_list|)
expr_stmt|;
name|afi
operator|=
operator|new
name|MockAmFilterInitializer
argument_list|()
expr_stmt|;
name|assertNull
argument_list|(
name|con
operator|.
name|givenParameters
argument_list|)
expr_stmt|;
name|afi
operator|.
name|initFilter
argument_list|(
name|con
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|con
operator|.
name|givenParameters
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|String
index|[]
name|proxyHosts
init|=
name|con
operator|.
name|givenParameters
operator|.
name|get
argument_list|(
name|AmIpFilter
operator|.
name|PROXY_HOSTS
argument_list|)
operator|.
name|split
argument_list|(
name|AmIpFilter
operator|.
name|PROXY_HOSTS_DELIMITER
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|proxyHosts
operator|.
name|length
argument_list|)
expr_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|proxyHosts
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"host2"
argument_list|,
name|proxyHosts
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"host3"
argument_list|,
name|proxyHosts
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"host4"
argument_list|,
name|proxyHosts
index|[
literal|2
index|]
argument_list|)
expr_stmt|;
name|String
index|[]
name|proxyBases
init|=
name|con
operator|.
name|givenParameters
operator|.
name|get
argument_list|(
name|AmIpFilter
operator|.
name|PROXY_URI_BASES
argument_list|)
operator|.
name|split
argument_list|(
name|AmIpFilter
operator|.
name|PROXY_URI_BASES_DELIMITER
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|proxyBases
operator|.
name|length
argument_list|)
expr_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|proxyBases
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"http://host2:2000/foo"
argument_list|,
name|proxyBases
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"http://host3:3000/foo"
argument_list|,
name|proxyBases
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"http://host4:4000/foo"
argument_list|,
name|proxyBases
index|[
literal|2
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"host2:2000,host3:3000,host4:4000"
argument_list|,
name|con
operator|.
name|givenParameters
operator|.
name|get
argument_list|(
name|AmFilterInitializer
operator|.
name|RM_HA_URLS
argument_list|)
argument_list|)
expr_stmt|;
comment|// Check multiple RM_WEBAPP_ADDRESSes (RM HA) with HTTPS
name|con
operator|=
operator|new
name|MockFilterContainer
argument_list|()
expr_stmt|;
name|conf
operator|=
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|YARN_HTTP_POLICY_KEY
argument_list|,
name|HttpConfig
operator|.
name|Policy
operator|.
name|HTTPS_ONLY
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|conf
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
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_HA_IDS
argument_list|,
literal|"rm1,rm2"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_WEBAPP_HTTPS_ADDRESS
operator|+
literal|".rm1"
argument_list|,
literal|"host5:5000"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_WEBAPP_HTTPS_ADDRESS
operator|+
literal|".rm2"
argument_list|,
literal|"host6:6000"
argument_list|)
expr_stmt|;
name|afi
operator|=
operator|new
name|MockAmFilterInitializer
argument_list|()
expr_stmt|;
name|assertNull
argument_list|(
name|con
operator|.
name|givenParameters
argument_list|)
expr_stmt|;
name|afi
operator|.
name|initFilter
argument_list|(
name|con
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|con
operator|.
name|givenParameters
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|proxyHosts
operator|=
name|con
operator|.
name|givenParameters
operator|.
name|get
argument_list|(
name|AmIpFilter
operator|.
name|PROXY_HOSTS
argument_list|)
operator|.
name|split
argument_list|(
name|AmIpFilter
operator|.
name|PROXY_HOSTS_DELIMITER
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|proxyHosts
operator|.
name|length
argument_list|)
expr_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|proxyHosts
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"host5"
argument_list|,
name|proxyHosts
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"host6"
argument_list|,
name|proxyHosts
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|proxyBases
operator|=
name|con
operator|.
name|givenParameters
operator|.
name|get
argument_list|(
name|AmIpFilter
operator|.
name|PROXY_URI_BASES
argument_list|)
operator|.
name|split
argument_list|(
name|AmIpFilter
operator|.
name|PROXY_URI_BASES_DELIMITER
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|proxyBases
operator|.
name|length
argument_list|)
expr_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|proxyBases
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"https://host5:5000/foo"
argument_list|,
name|proxyBases
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"https://host6:6000/foo"
argument_list|,
name|proxyBases
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"host5:5000,host6:6000"
argument_list|,
name|con
operator|.
name|givenParameters
operator|.
name|get
argument_list|(
name|AmFilterInitializer
operator|.
name|RM_HA_URLS
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetProxyHostsAndPortsForAmFilter ()
specifier|public
name|void
name|testGetProxyHostsAndPortsForAmFilter
parameter_list|()
block|{
comment|// Check no configs given
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|proxyHosts
init|=
name|WebAppUtils
operator|.
name|getProxyHostsAndPortsForAmFilter
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|proxyHosts
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|WebAppUtils
operator|.
name|getResolvedRMWebAppURLWithoutScheme
argument_list|(
name|conf
argument_list|)
argument_list|,
name|proxyHosts
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
comment|// Check conf in which only RM hostname is set
name|conf
operator|=
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_WEBAPP_ADDRESS
argument_list|,
literal|"${yarn.resourcemanager.hostname}:8088"
argument_list|)
expr_stmt|;
comment|// default in yarn-default.xml
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_HOSTNAME
argument_list|,
literal|"host1"
argument_list|)
expr_stmt|;
name|proxyHosts
operator|=
name|WebAppUtils
operator|.
name|getProxyHostsAndPortsForAmFilter
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|proxyHosts
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"host1:8088"
argument_list|,
name|proxyHosts
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
comment|// Check PROXY_ADDRESS has priority
name|conf
operator|=
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|PROXY_ADDRESS
argument_list|,
literal|"host1:1000"
argument_list|)
expr_stmt|;
name|conf
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
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_HA_IDS
argument_list|,
literal|"rm1,rm2,rm3"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_WEBAPP_ADDRESS
operator|+
literal|".rm1"
argument_list|,
literal|"host2:2000"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_WEBAPP_ADDRESS
operator|+
literal|".rm2"
argument_list|,
literal|"host3:3000"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_WEBAPP_ADDRESS
operator|+
literal|".rm3"
argument_list|,
literal|"host4:4000"
argument_list|)
expr_stmt|;
name|proxyHosts
operator|=
name|WebAppUtils
operator|.
name|getProxyHostsAndPortsForAmFilter
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|proxyHosts
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"host1:1000"
argument_list|,
name|proxyHosts
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
comment|// Check getting a single RM_WEBAPP_ADDRESS
name|conf
operator|=
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_WEBAPP_ADDRESS
argument_list|,
literal|"host2:2000"
argument_list|)
expr_stmt|;
name|proxyHosts
operator|=
name|WebAppUtils
operator|.
name|getProxyHostsAndPortsForAmFilter
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|proxyHosts
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|proxyHosts
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"host2:2000"
argument_list|,
name|proxyHosts
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
comment|// Check getting multiple RM_WEBAPP_ADDRESSes (RM HA)
name|conf
operator|=
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|conf
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
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_HA_IDS
argument_list|,
literal|"rm1,rm2,rm3"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_WEBAPP_ADDRESS
operator|+
literal|".rm1"
argument_list|,
literal|"host2:2000"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_WEBAPP_ADDRESS
operator|+
literal|".rm2"
argument_list|,
literal|"host3:3000"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_WEBAPP_ADDRESS
operator|+
literal|".rm3"
argument_list|,
literal|"host4:4000"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_WEBAPP_ADDRESS
operator|+
literal|".rm4"
argument_list|,
literal|"dummy"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_WEBAPP_HTTPS_ADDRESS
operator|+
literal|".rm1"
argument_list|,
literal|"host5:5000"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_WEBAPP_HTTPS_ADDRESS
operator|+
literal|".rm2"
argument_list|,
literal|"host6:6000"
argument_list|)
expr_stmt|;
name|proxyHosts
operator|=
name|WebAppUtils
operator|.
name|getProxyHostsAndPortsForAmFilter
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|proxyHosts
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|proxyHosts
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"host2:2000"
argument_list|,
name|proxyHosts
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"host3:3000"
argument_list|,
name|proxyHosts
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"host4:4000"
argument_list|,
name|proxyHosts
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
comment|// Check getting multiple RM_WEBAPP_ADDRESSes (RM HA) with HTTPS
name|conf
operator|=
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|YARN_HTTP_POLICY_KEY
argument_list|,
name|HttpConfig
operator|.
name|Policy
operator|.
name|HTTPS_ONLY
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|conf
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
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_HA_IDS
argument_list|,
literal|"rm1,rm2,rm3,dummy"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_WEBAPP_ADDRESS
operator|+
literal|".rm1"
argument_list|,
literal|"host2:2000"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_WEBAPP_ADDRESS
operator|+
literal|".rm2"
argument_list|,
literal|"host3:3000"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_WEBAPP_ADDRESS
operator|+
literal|".rm3"
argument_list|,
literal|"host4:4000"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_WEBAPP_HTTPS_ADDRESS
operator|+
literal|".rm1"
argument_list|,
literal|"host5:5000"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_WEBAPP_HTTPS_ADDRESS
operator|+
literal|".rm2"
argument_list|,
literal|"host6:6000"
argument_list|)
expr_stmt|;
name|proxyHosts
operator|=
name|WebAppUtils
operator|.
name|getProxyHostsAndPortsForAmFilter
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|proxyHosts
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|proxyHosts
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"host5:5000"
argument_list|,
name|proxyHosts
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"host6:6000"
argument_list|,
name|proxyHosts
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
comment|// Check config without explicit RM_WEBAPP_ADDRESS settings (RM HA)
name|conf
operator|=
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|conf
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
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_HA_IDS
argument_list|,
literal|"rm1,rm2,rm3"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_HOSTNAME
operator|+
literal|".rm1"
argument_list|,
literal|"host2"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_HOSTNAME
operator|+
literal|".rm2"
argument_list|,
literal|"host3"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_HOSTNAME
operator|+
literal|".rm3"
argument_list|,
literal|"host4"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_HOSTNAME
operator|+
literal|".rm4"
argument_list|,
literal|"dummy"
argument_list|)
expr_stmt|;
name|proxyHosts
operator|=
name|WebAppUtils
operator|.
name|getProxyHostsAndPortsForAmFilter
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|proxyHosts
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|proxyHosts
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"host2:"
operator|+
name|YarnConfiguration
operator|.
name|DEFAULT_RM_WEBAPP_PORT
argument_list|,
name|proxyHosts
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"host3:"
operator|+
name|YarnConfiguration
operator|.
name|DEFAULT_RM_WEBAPP_PORT
argument_list|,
name|proxyHosts
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"host4:"
operator|+
name|YarnConfiguration
operator|.
name|DEFAULT_RM_WEBAPP_PORT
argument_list|,
name|proxyHosts
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
comment|// Check config without explicit RM_WEBAPP_HTTPS_ADDRESS settings (RM HA)
name|conf
operator|=
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|YARN_HTTP_POLICY_KEY
argument_list|,
name|HttpConfig
operator|.
name|Policy
operator|.
name|HTTPS_ONLY
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|conf
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
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_HA_IDS
argument_list|,
literal|"rm1,rm2,rm3"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_HOSTNAME
operator|+
literal|".rm1"
argument_list|,
literal|"host2"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_HOSTNAME
operator|+
literal|".rm2"
argument_list|,
literal|"host3"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_HOSTNAME
operator|+
literal|".rm3"
argument_list|,
literal|"host4"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_HOSTNAME
operator|+
literal|".rm4"
argument_list|,
literal|"dummy"
argument_list|)
expr_stmt|;
name|proxyHosts
operator|=
name|WebAppUtils
operator|.
name|getProxyHostsAndPortsForAmFilter
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|proxyHosts
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|proxyHosts
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"host2:"
operator|+
name|YarnConfiguration
operator|.
name|DEFAULT_RM_WEBAPP_HTTPS_PORT
argument_list|,
name|proxyHosts
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"host3:"
operator|+
name|YarnConfiguration
operator|.
name|DEFAULT_RM_WEBAPP_HTTPS_PORT
argument_list|,
name|proxyHosts
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"host4:"
operator|+
name|YarnConfiguration
operator|.
name|DEFAULT_RM_WEBAPP_HTTPS_PORT
argument_list|,
name|proxyHosts
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|class|MockAmFilterInitializer
class|class
name|MockAmFilterInitializer
extends|extends
name|AmFilterInitializer
block|{
annotation|@
name|Override
DECL|method|getApplicationWebProxyBase ()
specifier|protected
name|String
name|getApplicationWebProxyBase
parameter_list|()
block|{
return|return
literal|"/foo"
return|;
block|}
block|}
DECL|class|MockFilterContainer
class|class
name|MockFilterContainer
implements|implements
name|FilterContainer
block|{
DECL|field|givenParameters
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|givenParameters
decl_stmt|;
annotation|@
name|Override
DECL|method|addFilter (String name, String classname, Map<String, String> parameters)
specifier|public
name|void
name|addFilter
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|classname
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|parameters
parameter_list|)
block|{
name|givenParameters
operator|=
name|parameters
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|addGlobalFilter (String name, String classname, Map<String, String> parameters)
specifier|public
name|void
name|addGlobalFilter
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|classname
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|parameters
parameter_list|)
block|{     }
block|}
block|}
end_class

end_unit

