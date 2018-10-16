begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.security
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
name|resourcemanager
operator|.
name|security
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
name|server
operator|.
name|resourcemanager
operator|.
name|RMContext
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
name|webproxy
operator|.
name|ProxyCA
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
name|spy
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
name|times
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
name|verify
import|;
end_import

begin_class
DECL|class|TestProxyCAManager
specifier|public
class|class
name|TestProxyCAManager
block|{
annotation|@
name|Test
DECL|method|testBasics ()
specifier|public
name|void
name|testBasics
parameter_list|()
throws|throws
name|Exception
block|{
name|ProxyCA
name|proxyCA
init|=
name|spy
argument_list|(
operator|new
name|ProxyCA
argument_list|()
argument_list|)
decl_stmt|;
name|RMContext
name|rmContext
init|=
name|mock
argument_list|(
name|RMContext
operator|.
name|class
argument_list|)
decl_stmt|;
name|ProxyCAManager
name|proxyCAManager
init|=
operator|new
name|ProxyCAManager
argument_list|(
name|proxyCA
argument_list|,
name|rmContext
argument_list|)
decl_stmt|;
name|proxyCAManager
operator|.
name|init
argument_list|(
operator|new
name|YarnConfiguration
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|proxyCA
argument_list|,
name|proxyCAManager
operator|.
name|getProxyCA
argument_list|()
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|proxyCA
argument_list|,
name|times
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|init
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertNull
argument_list|(
name|proxyCA
operator|.
name|getCaCert
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNull
argument_list|(
name|proxyCA
operator|.
name|getCaKeyPair
argument_list|()
argument_list|)
expr_stmt|;
name|proxyCAManager
operator|.
name|start
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|proxyCA
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|init
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|proxyCA
operator|.
name|getCaCert
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|proxyCA
operator|.
name|getCaKeyPair
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

