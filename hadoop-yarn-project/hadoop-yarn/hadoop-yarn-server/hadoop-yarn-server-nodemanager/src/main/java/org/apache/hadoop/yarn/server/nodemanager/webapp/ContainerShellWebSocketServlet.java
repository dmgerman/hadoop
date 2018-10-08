begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.webapp
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
name|nodemanager
operator|.
name|webapp
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|annotation
operator|.
name|WebServlet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|websocket
operator|.
name|servlet
operator|.
name|WebSocketServlet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|websocket
operator|.
name|servlet
operator|.
name|WebSocketServletFactory
import|;
end_import

begin_comment
comment|/**  * Container shell web socket interface.  */
end_comment

begin_class
annotation|@
name|WebServlet
argument_list|(
name|urlPatterns
operator|=
literal|"/container/container/*"
argument_list|)
DECL|class|ContainerShellWebSocketServlet
specifier|public
class|class
name|ContainerShellWebSocketServlet
extends|extends
name|WebSocketServlet
block|{
annotation|@
name|Override
DECL|method|configure (WebSocketServletFactory factory)
specifier|public
name|void
name|configure
parameter_list|(
name|WebSocketServletFactory
name|factory
parameter_list|)
block|{
name|factory
operator|.
name|register
argument_list|(
name|ContainerShellWebSocket
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

