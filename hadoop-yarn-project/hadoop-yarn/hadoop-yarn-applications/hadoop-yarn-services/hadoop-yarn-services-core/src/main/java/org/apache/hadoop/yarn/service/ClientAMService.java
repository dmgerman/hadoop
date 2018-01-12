begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.service
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|service
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
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|CommonConfigurationKeysPublic
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
name|security
operator|.
name|UserGroupInformation
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
operator|.
name|ExitUtil
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
name|api
operator|.
name|ApplicationConstants
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
name|ipc
operator|.
name|YarnRPC
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
name|proto
operator|.
name|ClientAMProtocol
operator|.
name|ComponentCountProto
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
name|proto
operator|.
name|ClientAMProtocol
operator|.
name|FlexComponentsRequestProto
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
name|proto
operator|.
name|ClientAMProtocol
operator|.
name|FlexComponentsResponseProto
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
name|proto
operator|.
name|ClientAMProtocol
operator|.
name|GetStatusRequestProto
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
name|proto
operator|.
name|ClientAMProtocol
operator|.
name|GetStatusResponseProto
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
name|proto
operator|.
name|ClientAMProtocol
operator|.
name|StopRequestProto
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
name|proto
operator|.
name|ClientAMProtocol
operator|.
name|StopResponseProto
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
name|service
operator|.
name|component
operator|.
name|ComponentEvent
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
name|service
operator|.
name|utils
operator|.
name|ServiceApiUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

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
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|service
operator|.
name|component
operator|.
name|ComponentEventType
operator|.
name|FLEX
import|;
end_import

begin_class
DECL|class|ClientAMService
specifier|public
class|class
name|ClientAMService
extends|extends
name|AbstractService
implements|implements
name|ClientAMProtocol
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ClientAMService
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|context
specifier|private
name|ServiceContext
name|context
decl_stmt|;
DECL|field|server
specifier|private
name|Server
name|server
decl_stmt|;
DECL|field|bindAddress
specifier|private
name|InetSocketAddress
name|bindAddress
decl_stmt|;
DECL|method|ClientAMService (ServiceContext context)
specifier|public
name|ClientAMService
parameter_list|(
name|ServiceContext
name|context
parameter_list|)
block|{
name|super
argument_list|(
literal|"Client AM Service"
argument_list|)
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
block|}
DECL|method|serviceStart ()
annotation|@
name|Override
specifier|protected
name|void
name|serviceStart
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
name|getConfig
argument_list|()
decl_stmt|;
name|YarnRPC
name|rpc
init|=
name|YarnRPC
operator|.
name|create
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|InetSocketAddress
name|address
init|=
operator|new
name|InetSocketAddress
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|server
operator|=
name|rpc
operator|.
name|getServer
argument_list|(
name|ClientAMProtocol
operator|.
name|class
argument_list|,
name|this
argument_list|,
name|address
argument_list|,
name|conf
argument_list|,
name|context
operator|.
name|secretManager
argument_list|,
literal|1
argument_list|)
expr_stmt|;
comment|// Enable service authorization?
if|if
condition|(
name|conf
operator|.
name|getBoolean
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|HADOOP_SECURITY_AUTHORIZATION
argument_list|,
literal|false
argument_list|)
condition|)
block|{
name|this
operator|.
name|server
operator|.
name|refreshServiceAcl
argument_list|(
name|getConfig
argument_list|()
argument_list|,
operator|new
name|ClientAMPolicyProvider
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
name|String
name|nodeHostString
init|=
name|System
operator|.
name|getenv
argument_list|(
name|ApplicationConstants
operator|.
name|Environment
operator|.
name|NM_HOST
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
name|bindAddress
operator|=
name|NetUtils
operator|.
name|createSocketAddrForHost
argument_list|(
name|nodeHostString
argument_list|,
name|server
operator|.
name|getListenerAddress
argument_list|()
operator|.
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Instantiated ClientAMService at "
operator|+
name|bindAddress
argument_list|)
expr_stmt|;
name|super
operator|.
name|serviceStart
argument_list|()
expr_stmt|;
block|}
DECL|method|serviceStop ()
annotation|@
name|Override
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
name|super
operator|.
name|serviceStop
argument_list|()
expr_stmt|;
block|}
DECL|method|flexComponents ( FlexComponentsRequestProto request)
annotation|@
name|Override
specifier|public
name|FlexComponentsResponseProto
name|flexComponents
parameter_list|(
name|FlexComponentsRequestProto
name|request
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|request
operator|.
name|getComponentsList
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
for|for
control|(
name|ComponentCountProto
name|component
range|:
name|request
operator|.
name|getComponentsList
argument_list|()
control|)
block|{
name|ComponentEvent
name|event
init|=
operator|new
name|ComponentEvent
argument_list|(
name|component
operator|.
name|getName
argument_list|()
argument_list|,
name|FLEX
argument_list|)
operator|.
name|setDesired
argument_list|(
name|component
operator|.
name|getNumberOfContainers
argument_list|()
argument_list|)
decl_stmt|;
name|context
operator|.
name|scheduler
operator|.
name|getDispatcher
argument_list|()
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
name|event
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Flexing component {} to {}"
argument_list|,
name|component
operator|.
name|getName
argument_list|()
argument_list|,
name|component
operator|.
name|getNumberOfContainers
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|FlexComponentsResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getStatus (GetStatusRequestProto request)
specifier|public
name|GetStatusResponseProto
name|getStatus
parameter_list|(
name|GetStatusRequestProto
name|request
parameter_list|)
throws|throws
name|IOException
throws|,
name|YarnException
block|{
name|String
name|stat
init|=
name|ServiceApiUtil
operator|.
name|jsonSerDeser
operator|.
name|toJson
argument_list|(
name|context
operator|.
name|service
argument_list|)
decl_stmt|;
return|return
name|GetStatusResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setStatus
argument_list|(
name|stat
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|stop (StopRequestProto requestProto)
specifier|public
name|StopResponseProto
name|stop
parameter_list|(
name|StopRequestProto
name|requestProto
parameter_list|)
throws|throws
name|IOException
throws|,
name|YarnException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Stop the service by {}"
argument_list|,
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
argument_list|)
expr_stmt|;
name|context
operator|.
name|scheduler
operator|.
name|getDiagnostics
argument_list|()
operator|.
name|append
argument_list|(
literal|"Stopped by user "
operator|+
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
argument_list|)
expr_stmt|;
comment|// Stop the service in 2 seconds delay to make sure this rpc call is completed.
comment|// shutdown hook will be executed which will stop AM gracefully.
name|Thread
name|thread
init|=
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
name|ExitUtil
operator|.
name|terminate
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Interrupted while stopping"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|thread
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
name|StopResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|getBindAddress ()
specifier|public
name|InetSocketAddress
name|getBindAddress
parameter_list|()
block|{
return|return
name|bindAddress
return|;
block|}
block|}
end_class

end_unit

