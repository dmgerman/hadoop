begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.ipc
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|ipc
package|;
end_package

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
name|org
operator|.
name|apache
operator|.
name|avro
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
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|security
operator|.
name|token
operator|.
name|SecretManager
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
name|security
operator|.
name|token
operator|.
name|TokenIdentifier
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
name|conf
operator|.
name|YarnConfiguration
import|;
end_import

begin_comment
comment|/**  * Abstraction to get the RPC implementation for Yarn.  */
end_comment

begin_class
DECL|class|YarnRPC
specifier|public
specifier|abstract
class|class
name|YarnRPC
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|YarnRPC
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|getProxy (Class protocol, InetSocketAddress addr, Configuration conf)
specifier|public
specifier|abstract
name|Object
name|getProxy
parameter_list|(
name|Class
name|protocol
parameter_list|,
name|InetSocketAddress
name|addr
parameter_list|,
name|Configuration
name|conf
parameter_list|)
function_decl|;
DECL|method|getServer (Class protocol, Object instance, InetSocketAddress addr, Configuration conf, SecretManager<? extends TokenIdentifier> secretManager, int numHandlers)
specifier|public
specifier|abstract
name|Server
name|getServer
parameter_list|(
name|Class
name|protocol
parameter_list|,
name|Object
name|instance
parameter_list|,
name|InetSocketAddress
name|addr
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|SecretManager
argument_list|<
name|?
extends|extends
name|TokenIdentifier
argument_list|>
name|secretManager
parameter_list|,
name|int
name|numHandlers
parameter_list|)
function_decl|;
DECL|method|create (Configuration conf)
specifier|public
specifier|static
name|YarnRPC
name|create
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Creating YarnRPC for "
operator|+
name|conf
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|IPC_RPC_IMPL
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|clazzName
init|=
name|conf
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|IPC_RPC_IMPL
argument_list|)
decl_stmt|;
if|if
condition|(
name|clazzName
operator|==
literal|null
condition|)
block|{
name|clazzName
operator|=
name|YarnConfiguration
operator|.
name|DEFAULT_IPC_RPC_IMPL
expr_stmt|;
block|}
try|try
block|{
return|return
operator|(
name|YarnRPC
operator|)
name|Class
operator|.
name|forName
argument_list|(
name|clazzName
argument_list|)
operator|.
name|newInstance
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|YarnException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

