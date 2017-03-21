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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|ContainerData
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
name|helpers
operator|.
name|ContainerReport
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
name|EndpointStateMachine
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
name|ozone
operator|.
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|ContainerReportsProto
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
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|Type
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * Container Report handler.  */
end_comment

begin_class
DECL|class|ContainerReportHandler
specifier|public
class|class
name|ContainerReportHandler
implements|implements
name|CommandHandler
block|{
DECL|field|LOG
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ContainerReportHandler
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
comment|/**    * Constructs a ContainerReport handler.    */
DECL|method|ContainerReportHandler ()
specifier|public
name|ContainerReportHandler
parameter_list|()
block|{   }
comment|/**    * Handles a given SCM command.    *    * @param command - SCM Command    * @param container - Ozone Container.    * @param context - Current Context.    * @param connectionManager - The SCMs that we are talking to.    */
annotation|@
name|Override
DECL|method|handle (SCMCommand command, OzoneContainer container, StateContext context, SCMConnectionManager connectionManager)
specifier|public
name|void
name|handle
parameter_list|(
name|SCMCommand
name|command
parameter_list|,
name|OzoneContainer
name|container
parameter_list|,
name|StateContext
name|context
parameter_list|,
name|SCMConnectionManager
name|connectionManager
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Processing Container Report."
argument_list|)
expr_stmt|;
name|invocationCount
operator|++
expr_stmt|;
name|long
name|startTime
init|=
name|Time
operator|.
name|monotonicNow
argument_list|()
decl_stmt|;
try|try
block|{
name|ContainerReportsProto
operator|.
name|Builder
name|contianerReportsBuilder
init|=
name|ContainerReportsProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|ContainerData
argument_list|>
name|closedContainerList
init|=
name|container
operator|.
name|getContainerReports
argument_list|()
decl_stmt|;
for|for
control|(
name|ContainerData
name|cd
range|:
name|closedContainerList
control|)
block|{
name|ContainerReport
name|report
init|=
operator|new
name|ContainerReport
argument_list|(
name|cd
operator|.
name|getContainerName
argument_list|()
argument_list|,
name|cd
operator|.
name|getHash
argument_list|()
argument_list|)
decl_stmt|;
name|contianerReportsBuilder
operator|.
name|addReports
argument_list|(
name|report
operator|.
name|getProtoBufMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|contianerReportsBuilder
operator|.
name|setType
argument_list|(
name|ContainerReportsProto
operator|.
name|reportType
operator|.
name|fullReport
argument_list|)
expr_stmt|;
comment|// TODO : We send this report to all SCMs.Check if it is enough only to
comment|// send to the leader once we have RAFT enabled SCMs.
for|for
control|(
name|EndpointStateMachine
name|endPoint
range|:
name|connectionManager
operator|.
name|getValues
argument_list|()
control|)
block|{
name|endPoint
operator|.
name|getEndPoint
argument_list|()
operator|.
name|sendContainerReport
argument_list|(
name|contianerReportsBuilder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unable to process the Container Report command."
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|long
name|endTime
init|=
name|Time
operator|.
name|monotonicNow
argument_list|()
decl_stmt|;
name|totalTime
operator|+=
name|endTime
operator|-
name|startTime
expr_stmt|;
block|}
block|}
comment|/**    * Returns the command type that this command handler handles.    *    * @return Type    */
annotation|@
name|Override
DECL|method|getCommandType ()
specifier|public
name|Type
name|getCommandType
parameter_list|()
block|{
return|return
name|Type
operator|.
name|sendContainerReport
return|;
block|}
comment|/**    * Returns number of times this handler has been invoked.    *    * @return int    */
annotation|@
name|Override
DECL|method|getInvocationCount ()
specifier|public
name|int
name|getInvocationCount
parameter_list|()
block|{
return|return
name|invocationCount
return|;
block|}
comment|/**    * Returns the average time this function takes to run.    *    * @return long    */
annotation|@
name|Override
DECL|method|getAverageRunTime ()
specifier|public
name|long
name|getAverageRunTime
parameter_list|()
block|{
if|if
condition|(
name|invocationCount
operator|>
literal|0
condition|)
block|{
return|return
name|totalTime
operator|/
name|invocationCount
return|;
block|}
return|return
literal|0
return|;
block|}
block|}
end_class

end_unit

