begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.common.statemachine.commandhandler
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|statemachine
operator|.
name|commandhandler
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|SCMCommandProto
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|statemachine
operator|.
name|SCMConnectionManager
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|statemachine
operator|.
name|StateContext
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
name|ozone
operator|.
name|container
operator|.
name|ozoneimpl
operator|.
name|ContainerController
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
name|ozone
operator|.
name|container
operator|.
name|ozoneimpl
operator|.
name|OzoneContainer
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
name|ozone
operator|.
name|protocol
operator|.
name|commands
operator|.
name|DeleteContainerCommand
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
name|ozone
operator|.
name|protocol
operator|.
name|commands
operator|.
name|SCMCommand
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
name|Time
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

begin_comment
comment|/**  * Handler to process the DeleteContainerCommand from SCM.  */
end_comment

begin_class
DECL|class|DeleteContainerCommandHandler
specifier|public
class|class
name|DeleteContainerCommandHandler
implements|implements
name|CommandHandler
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
name|DeleteContainerCommandHandler
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|invocationCount
specifier|private
name|int
name|invocationCount
decl_stmt|;
DECL|field|totalTime
specifier|private
name|long
name|totalTime
decl_stmt|;
annotation|@
name|Override
DECL|method|handle (final SCMCommand command, final OzoneContainer ozoneContainer, final StateContext context, final SCMConnectionManager connectionManager)
specifier|public
name|void
name|handle
parameter_list|(
specifier|final
name|SCMCommand
name|command
parameter_list|,
specifier|final
name|OzoneContainer
name|ozoneContainer
parameter_list|,
specifier|final
name|StateContext
name|context
parameter_list|,
specifier|final
name|SCMConnectionManager
name|connectionManager
parameter_list|)
block|{
specifier|final
name|long
name|startTime
init|=
name|Time
operator|.
name|monotonicNow
argument_list|()
decl_stmt|;
name|invocationCount
operator|++
expr_stmt|;
try|try
block|{
specifier|final
name|DeleteContainerCommand
name|deleteContainerCommand
init|=
operator|(
name|DeleteContainerCommand
operator|)
name|command
decl_stmt|;
specifier|final
name|ContainerController
name|controller
init|=
name|ozoneContainer
operator|.
name|getController
argument_list|()
decl_stmt|;
name|controller
operator|.
name|deleteContainer
argument_list|(
name|deleteContainerCommand
operator|.
name|getContainerID
argument_list|()
argument_list|,
name|deleteContainerCommand
operator|.
name|isForce
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Exception occurred while deleting the container."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|totalTime
operator|+=
name|Time
operator|.
name|monotonicNow
argument_list|()
operator|-
name|startTime
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getCommandType ()
specifier|public
name|SCMCommandProto
operator|.
name|Type
name|getCommandType
parameter_list|()
block|{
return|return
name|SCMCommandProto
operator|.
name|Type
operator|.
name|deleteContainerCommand
return|;
block|}
annotation|@
name|Override
DECL|method|getInvocationCount ()
specifier|public
name|int
name|getInvocationCount
parameter_list|()
block|{
return|return
name|this
operator|.
name|invocationCount
return|;
block|}
annotation|@
name|Override
DECL|method|getAverageRunTime ()
specifier|public
name|long
name|getAverageRunTime
parameter_list|()
block|{
return|return
name|invocationCount
operator|==
literal|0
condition|?
literal|0
else|:
name|totalTime
operator|/
name|invocationCount
return|;
block|}
block|}
end_class

end_unit

