begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.factories.impl.pb
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|factories
operator|.
name|impl
operator|.
name|pb
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

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetSocketAddress
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Matchers
operator|.
name|anyString
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
name|atLeastOnce
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
name|verify
import|;
end_import

begin_comment
comment|/**  * Test class for RpcClientFactoryPBImpl.  */
end_comment

begin_class
DECL|class|TestRpcClientFactoryPBImpl
specifier|public
class|class
name|TestRpcClientFactoryPBImpl
block|{
annotation|@
name|Test
DECL|method|testToUseCustomClassloader ()
specifier|public
name|void
name|testToUseCustomClassloader
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|configuration
init|=
name|mock
argument_list|(
name|Configuration
operator|.
name|class
argument_list|)
decl_stmt|;
name|RpcClientFactoryPBImpl
name|rpcClientFactoryPB
init|=
name|RpcClientFactoryPBImpl
operator|.
name|get
argument_list|()
decl_stmt|;
try|try
block|{
name|rpcClientFactoryPB
operator|.
name|getClient
argument_list|(
name|Class
operator|.
name|forName
argument_list|(
literal|"org.apache.hadoop.yarn.api.ApplicationClientProtocol"
argument_list|)
argument_list|,
operator|-
literal|1
argument_list|,
operator|new
name|InetSocketAddress
argument_list|(
literal|0
argument_list|)
argument_list|,
name|configuration
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// Do nothing
block|}
name|verify
argument_list|(
name|configuration
argument_list|,
name|atLeastOnce
argument_list|()
argument_list|)
operator|.
name|getClassByName
argument_list|(
name|anyString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

