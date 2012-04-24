begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|factory
operator|.
name|providers
operator|.
name|RpcFactoryProvider
import|;
end_import

begin_comment
comment|/**  * This uses Hadoop RPC. Uses a tunnel ProtoSpecificRpcEngine over   * Hadoop connection.  * This does not give cross-language wire compatibility, since the Hadoop   * RPC wire format is non-standard, but it does permit use of Protocol Buffers  *  protocol versioning features for inter-Java RPCs.  */
end_comment

begin_class
DECL|class|HadoopYarnProtoRPC
specifier|public
class|class
name|HadoopYarnProtoRPC
extends|extends
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
name|HadoopYarnProtoRPC
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Override
DECL|method|getProxy (Class protocol, InetSocketAddress addr, Configuration conf)
specifier|public
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
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Creating a HadoopYarnProtoRpc proxy for protocol "
operator|+
name|protocol
argument_list|)
expr_stmt|;
return|return
name|RpcFactoryProvider
operator|.
name|getClientFactory
argument_list|(
name|conf
argument_list|)
operator|.
name|getClient
argument_list|(
name|protocol
argument_list|,
literal|1
argument_list|,
name|addr
argument_list|,
name|conf
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|stopProxy (Object proxy, Configuration conf)
specifier|public
name|void
name|stopProxy
parameter_list|(
name|Object
name|proxy
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|RpcFactoryProvider
operator|.
name|getClientFactory
argument_list|(
name|conf
argument_list|)
operator|.
name|stopClient
argument_list|(
name|proxy
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getServer (Class protocol, Object instance, InetSocketAddress addr, Configuration conf, SecretManager<? extends TokenIdentifier> secretManager, int numHandlers, String portRangeConfig)
specifier|public
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
parameter_list|,
name|String
name|portRangeConfig
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Creating a HadoopYarnProtoRpc server for protocol "
operator|+
name|protocol
operator|+
literal|" with "
operator|+
name|numHandlers
operator|+
literal|" handlers"
argument_list|)
expr_stmt|;
return|return
name|RpcFactoryProvider
operator|.
name|getServerFactory
argument_list|(
name|conf
argument_list|)
operator|.
name|getServer
argument_list|(
name|protocol
argument_list|,
name|instance
argument_list|,
name|addr
argument_list|,
name|conf
argument_list|,
name|secretManager
argument_list|,
name|numHandlers
argument_list|,
name|portRangeConfig
argument_list|)
return|;
block|}
block|}
end_class

end_unit

