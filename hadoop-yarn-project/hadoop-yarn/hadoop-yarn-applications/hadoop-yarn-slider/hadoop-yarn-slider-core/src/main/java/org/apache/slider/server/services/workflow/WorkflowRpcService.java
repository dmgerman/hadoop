begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.server.services.workflow
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|services
operator|.
name|workflow
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
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
name|service
operator|.
name|AbstractService
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

begin_comment
comment|/**  * A YARN service that maps the start/stop lifecycle of an RPC server  * to the YARN service lifecycle.   */
end_comment

begin_class
DECL|class|WorkflowRpcService
specifier|public
class|class
name|WorkflowRpcService
extends|extends
name|AbstractService
block|{
comment|/** RPC server*/
DECL|field|server
specifier|private
specifier|final
name|Server
name|server
decl_stmt|;
comment|/**    * Construct an instance    * @param name service name    * @param server service to stop    */
DECL|method|WorkflowRpcService (String name, Server server)
specifier|public
name|WorkflowRpcService
parameter_list|(
name|String
name|name
parameter_list|,
name|Server
name|server
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|server
operator|!=
literal|null
argument_list|,
literal|"Null server"
argument_list|)
expr_stmt|;
name|this
operator|.
name|server
operator|=
name|server
expr_stmt|;
block|}
comment|/**    * Get the server    * @return the server    */
DECL|method|getServer ()
specifier|public
name|Server
name|getServer
parameter_list|()
block|{
return|return
name|server
return|;
block|}
comment|/**    * Get the socket address of this server    * @return the address this server is listening on    */
DECL|method|getConnectAddress ()
specifier|public
name|InetSocketAddress
name|getConnectAddress
parameter_list|()
block|{
return|return
name|NetUtils
operator|.
name|getConnectAddress
argument_list|(
name|server
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|serviceStart ()
specifier|protected
name|void
name|serviceStart
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|serviceStart
argument_list|()
expr_stmt|;
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceStop ()
specifier|protected
name|void
name|serviceStop
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|server
operator|!=
literal|null
condition|)
block|{
name|server
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

