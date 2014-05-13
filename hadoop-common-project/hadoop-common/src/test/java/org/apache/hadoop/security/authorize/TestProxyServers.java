begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.security.authorize
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
operator|.
name|authorize
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
name|assertFalse
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
name|assertTrue
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
name|junit
operator|.
name|Test
import|;
end_import

begin_class
DECL|class|TestProxyServers
specifier|public
class|class
name|TestProxyServers
block|{
annotation|@
name|Test
DECL|method|testProxyServer ()
specifier|public
name|void
name|testProxyServer
parameter_list|()
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|ProxyServers
operator|.
name|isProxyServer
argument_list|(
literal|"1.1.1.1"
argument_list|)
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|ProxyServers
operator|.
name|CONF_HADOOP_PROXYSERVERS
argument_list|,
literal|"2.2.2.2, 3.3.3.3"
argument_list|)
expr_stmt|;
name|ProxyUsers
operator|.
name|refreshSuperUserGroupsConfiguration
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|ProxyServers
operator|.
name|isProxyServer
argument_list|(
literal|"1.1.1.1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ProxyServers
operator|.
name|isProxyServer
argument_list|(
literal|"2.2.2.2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ProxyServers
operator|.
name|isProxyServer
argument_list|(
literal|"3.3.3.3"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

