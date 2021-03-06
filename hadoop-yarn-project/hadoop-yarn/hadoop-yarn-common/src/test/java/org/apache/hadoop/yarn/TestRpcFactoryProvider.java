begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
package|;
end_package

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
name|exceptions
operator|.
name|YarnRuntimeException
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
name|factories
operator|.
name|RpcClientFactory
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
name|factories
operator|.
name|RpcServerFactory
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
name|factories
operator|.
name|impl
operator|.
name|pb
operator|.
name|RpcClientFactoryPBImpl
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
name|factories
operator|.
name|impl
operator|.
name|pb
operator|.
name|RpcServerFactoryPBImpl
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
name|factory
operator|.
name|providers
operator|.
name|RpcFactoryProvider
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
DECL|class|TestRpcFactoryProvider
specifier|public
class|class
name|TestRpcFactoryProvider
block|{
annotation|@
name|Test
DECL|method|testFactoryProvider ()
specifier|public
name|void
name|testFactoryProvider
parameter_list|()
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|RpcClientFactory
name|clientFactory
init|=
literal|null
decl_stmt|;
name|RpcServerFactory
name|serverFactory
init|=
literal|null
decl_stmt|;
name|clientFactory
operator|=
name|RpcFactoryProvider
operator|.
name|getClientFactory
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|serverFactory
operator|=
name|RpcFactoryProvider
operator|.
name|getServerFactory
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|RpcClientFactoryPBImpl
operator|.
name|class
argument_list|,
name|clientFactory
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|RpcServerFactoryPBImpl
operator|.
name|class
argument_list|,
name|serverFactory
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|IPC_CLIENT_FACTORY_CLASS
argument_list|,
literal|"unknown"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|IPC_SERVER_FACTORY_CLASS
argument_list|,
literal|"unknown"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|IPC_RECORD_FACTORY_CLASS
argument_list|,
literal|"unknown"
argument_list|)
expr_stmt|;
try|try
block|{
name|clientFactory
operator|=
name|RpcFactoryProvider
operator|.
name|getClientFactory
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Expected an exception - unknown serializer"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnRuntimeException
name|e
parameter_list|)
block|{     }
try|try
block|{
name|serverFactory
operator|=
name|RpcFactoryProvider
operator|.
name|getServerFactory
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Expected an exception - unknown serializer"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnRuntimeException
name|e
parameter_list|)
block|{     }
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|IPC_CLIENT_FACTORY_CLASS
argument_list|,
literal|"NonExistantClass"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|IPC_SERVER_FACTORY_CLASS
argument_list|,
name|RpcServerFactoryPBImpl
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|clientFactory
operator|=
name|RpcFactoryProvider
operator|.
name|getClientFactory
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Expected an exception - unknown class"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnRuntimeException
name|e
parameter_list|)
block|{     }
try|try
block|{
name|serverFactory
operator|=
name|RpcFactoryProvider
operator|.
name|getServerFactory
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnRuntimeException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"Error while loading factory using reflection: ["
operator|+
name|RpcServerFactoryPBImpl
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

