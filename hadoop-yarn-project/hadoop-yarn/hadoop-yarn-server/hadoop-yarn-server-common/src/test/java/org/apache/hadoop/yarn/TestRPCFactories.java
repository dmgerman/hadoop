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
name|InetSocketAddress
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
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
name|ipc
operator|.
name|Server
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
name|exceptions
operator|.
name|YarnException
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
name|server
operator|.
name|api
operator|.
name|ResourceTracker
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
name|api
operator|.
name|protocolrecords
operator|.
name|NodeHeartbeatRequest
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
name|api
operator|.
name|protocolrecords
operator|.
name|NodeHeartbeatResponse
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
name|api
operator|.
name|protocolrecords
operator|.
name|RegisterNodeManagerRequest
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
name|api
operator|.
name|protocolrecords
operator|.
name|RegisterNodeManagerResponse
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
DECL|class|TestRPCFactories
specifier|public
class|class
name|TestRPCFactories
block|{
annotation|@
name|Test
DECL|method|test ()
specifier|public
name|void
name|test
parameter_list|()
block|{
name|testPbServerFactory
argument_list|()
expr_stmt|;
name|testPbClientFactory
argument_list|()
expr_stmt|;
block|}
DECL|method|testPbServerFactory ()
specifier|private
name|void
name|testPbServerFactory
parameter_list|()
block|{
name|InetSocketAddress
name|addr
init|=
operator|new
name|InetSocketAddress
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|ResourceTracker
name|instance
init|=
operator|new
name|ResourceTrackerTestImpl
argument_list|()
decl_stmt|;
name|Server
name|server
init|=
literal|null
decl_stmt|;
try|try
block|{
name|server
operator|=
name|RpcServerFactoryPBImpl
operator|.
name|get
argument_list|()
operator|.
name|getServer
argument_list|(
name|ResourceTracker
operator|.
name|class
argument_list|,
name|instance
argument_list|,
name|addr
argument_list|,
name|conf
argument_list|,
literal|null
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnRuntimeException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Failed to create server"
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|server
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|testPbClientFactory ()
specifier|private
name|void
name|testPbClientFactory
parameter_list|()
block|{
name|InetSocketAddress
name|addr
init|=
operator|new
name|InetSocketAddress
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|addr
operator|.
name|getHostName
argument_list|()
operator|+
name|addr
operator|.
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|ResourceTracker
name|instance
init|=
operator|new
name|ResourceTrackerTestImpl
argument_list|()
decl_stmt|;
name|Server
name|server
init|=
literal|null
decl_stmt|;
try|try
block|{
name|server
operator|=
name|RpcServerFactoryPBImpl
operator|.
name|get
argument_list|()
operator|.
name|getServer
argument_list|(
name|ResourceTracker
operator|.
name|class
argument_list|,
name|instance
argument_list|,
name|addr
argument_list|,
name|conf
argument_list|,
literal|null
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|server
operator|.
name|getListenerAddress
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|NetUtils
operator|.
name|getConnectAddress
argument_list|(
name|server
argument_list|)
argument_list|)
expr_stmt|;
name|ResourceTracker
name|client
init|=
literal|null
decl_stmt|;
try|try
block|{
name|client
operator|=
operator|(
name|ResourceTracker
operator|)
name|RpcClientFactoryPBImpl
operator|.
name|get
argument_list|()
operator|.
name|getClient
argument_list|(
name|ResourceTracker
operator|.
name|class
argument_list|,
literal|1
argument_list|,
name|NetUtils
operator|.
name|getConnectAddress
argument_list|(
name|server
argument_list|)
argument_list|,
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
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Failed to create client"
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|YarnRuntimeException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Failed to create server"
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|server
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|ResourceTrackerTestImpl
specifier|public
class|class
name|ResourceTrackerTestImpl
implements|implements
name|ResourceTracker
block|{
annotation|@
name|Override
DECL|method|registerNodeManager ( RegisterNodeManagerRequest request)
specifier|public
name|RegisterNodeManagerResponse
name|registerNodeManager
parameter_list|(
name|RegisterNodeManagerRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|nodeHeartbeat (NodeHeartbeatRequest request)
specifier|public
name|NodeHeartbeatResponse
name|nodeHeartbeat
parameter_list|(
name|NodeHeartbeatRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit

