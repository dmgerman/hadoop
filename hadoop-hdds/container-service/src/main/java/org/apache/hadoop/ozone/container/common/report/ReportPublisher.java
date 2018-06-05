begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.common.report
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
name|report
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|protobuf
operator|.
name|GeneratedMessage
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
name|Configurable
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|statemachine
operator|.
name|DatanodeStateMachine
operator|.
name|DatanodeStates
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ScheduledExecutorService
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_comment
comment|/**  * Abstract class responsible for scheduling the reports based on the  * configured interval. All the ReportPublishers should extend this class.  */
end_comment

begin_class
DECL|class|ReportPublisher
specifier|public
specifier|abstract
class|class
name|ReportPublisher
parameter_list|<
name|T
extends|extends
name|GeneratedMessage
parameter_list|>
implements|implements
name|Configurable
implements|,
name|Runnable
block|{
DECL|field|config
specifier|private
name|Configuration
name|config
decl_stmt|;
DECL|field|context
specifier|private
name|StateContext
name|context
decl_stmt|;
DECL|field|executor
specifier|private
name|ScheduledExecutorService
name|executor
decl_stmt|;
comment|/**    * Initializes ReportPublisher with stateContext and executorService.    *    * @param stateContext Datanode state context    * @param executorService ScheduledExecutorService to schedule reports    */
DECL|method|init (StateContext stateContext, ScheduledExecutorService executorService)
specifier|public
name|void
name|init
parameter_list|(
name|StateContext
name|stateContext
parameter_list|,
name|ScheduledExecutorService
name|executorService
parameter_list|)
block|{
name|this
operator|.
name|context
operator|=
name|stateContext
expr_stmt|;
name|this
operator|.
name|executor
operator|=
name|executorService
expr_stmt|;
name|this
operator|.
name|executor
operator|.
name|schedule
argument_list|(
name|this
argument_list|,
name|getReportFrequency
argument_list|()
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setConf (Configuration conf)
specifier|public
name|void
name|setConf
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|config
operator|=
name|conf
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getConf ()
specifier|public
name|Configuration
name|getConf
parameter_list|()
block|{
return|return
name|config
return|;
block|}
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
name|publishReport
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|executor
operator|.
name|isShutdown
argument_list|()
operator|||
operator|!
operator|(
name|context
operator|.
name|getState
argument_list|()
operator|==
name|DatanodeStates
operator|.
name|SHUTDOWN
operator|)
condition|)
block|{
name|executor
operator|.
name|schedule
argument_list|(
name|this
argument_list|,
name|getReportFrequency
argument_list|()
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Generates and publishes the report to datanode state context.    */
DECL|method|publishReport ()
specifier|private
name|void
name|publishReport
parameter_list|()
block|{
name|context
operator|.
name|addReport
argument_list|(
name|getReport
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns the frequency in which this particular report has to be scheduled.    *    * @return report interval in milliseconds    */
DECL|method|getReportFrequency ()
specifier|protected
specifier|abstract
name|long
name|getReportFrequency
parameter_list|()
function_decl|;
comment|/**    * Generate and returns the report which has to be sent as part of heartbeat.    *    * @return datanode report    */
DECL|method|getReport ()
specifier|protected
specifier|abstract
name|T
name|getReport
parameter_list|()
function_decl|;
block|}
end_class

end_unit

