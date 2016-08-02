begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.server.appmaster.web.rest.agent
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|web
operator|.
name|rest
operator|.
name|agent
package|;
end_package

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jackson
operator|.
name|annotate
operator|.
name|JsonIgnoreProperties
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jackson
operator|.
name|annotate
operator|.
name|JsonProperty
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jackson
operator|.
name|map
operator|.
name|annotate
operator|.
name|JsonSerialize
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
comment|/**  *  * Controller to Agent response data model.  *  */
end_comment

begin_class
annotation|@
name|JsonIgnoreProperties
argument_list|(
name|ignoreUnknown
operator|=
literal|true
argument_list|)
annotation|@
name|JsonSerialize
argument_list|(
name|include
operator|=
name|JsonSerialize
operator|.
name|Inclusion
operator|.
name|NON_NULL
argument_list|)
DECL|class|HeartBeatResponse
specifier|public
class|class
name|HeartBeatResponse
block|{
DECL|field|responseId
specifier|private
name|long
name|responseId
decl_stmt|;
DECL|field|executionCommands
name|List
argument_list|<
name|ExecutionCommand
argument_list|>
name|executionCommands
init|=
operator|new
name|ArrayList
argument_list|<
name|ExecutionCommand
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|statusCommands
name|List
argument_list|<
name|StatusCommand
argument_list|>
name|statusCommands
init|=
operator|new
name|ArrayList
argument_list|<
name|StatusCommand
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|registrationCommand
name|RegistrationCommand
name|registrationCommand
decl_stmt|;
DECL|field|yarnDockerMode
name|boolean
name|yarnDockerMode
init|=
literal|false
decl_stmt|;
DECL|field|restartAgent
name|boolean
name|restartAgent
init|=
literal|false
decl_stmt|;
DECL|field|restartEnabled
name|boolean
name|restartEnabled
init|=
literal|true
decl_stmt|;
DECL|field|hasMappedComponents
name|boolean
name|hasMappedComponents
init|=
literal|false
decl_stmt|;
DECL|field|terminateAgent
name|boolean
name|terminateAgent
init|=
literal|false
decl_stmt|;
annotation|@
name|JsonProperty
argument_list|(
literal|"responseId"
argument_list|)
DECL|method|getResponseId ()
specifier|public
name|long
name|getResponseId
parameter_list|()
block|{
return|return
name|responseId
return|;
block|}
annotation|@
name|JsonProperty
argument_list|(
literal|"responseId"
argument_list|)
DECL|method|setResponseId (long responseId)
specifier|public
name|void
name|setResponseId
parameter_list|(
name|long
name|responseId
parameter_list|)
block|{
name|this
operator|.
name|responseId
operator|=
name|responseId
expr_stmt|;
block|}
annotation|@
name|JsonProperty
argument_list|(
literal|"executionCommands"
argument_list|)
DECL|method|getExecutionCommands ()
specifier|public
name|List
argument_list|<
name|ExecutionCommand
argument_list|>
name|getExecutionCommands
parameter_list|()
block|{
return|return
name|executionCommands
return|;
block|}
annotation|@
name|JsonProperty
argument_list|(
literal|"executionCommands"
argument_list|)
DECL|method|setExecutionCommands (List<ExecutionCommand> executionCommands)
specifier|public
name|void
name|setExecutionCommands
parameter_list|(
name|List
argument_list|<
name|ExecutionCommand
argument_list|>
name|executionCommands
parameter_list|)
block|{
name|this
operator|.
name|executionCommands
operator|=
name|executionCommands
expr_stmt|;
block|}
annotation|@
name|JsonProperty
argument_list|(
literal|"statusCommands"
argument_list|)
DECL|method|getStatusCommands ()
specifier|public
name|List
argument_list|<
name|StatusCommand
argument_list|>
name|getStatusCommands
parameter_list|()
block|{
return|return
name|statusCommands
return|;
block|}
annotation|@
name|JsonProperty
argument_list|(
literal|"statusCommands"
argument_list|)
DECL|method|setStatusCommands (List<StatusCommand> statusCommands)
specifier|public
name|void
name|setStatusCommands
parameter_list|(
name|List
argument_list|<
name|StatusCommand
argument_list|>
name|statusCommands
parameter_list|)
block|{
name|this
operator|.
name|statusCommands
operator|=
name|statusCommands
expr_stmt|;
block|}
annotation|@
name|JsonProperty
argument_list|(
literal|"registrationCommand"
argument_list|)
DECL|method|getRegistrationCommand ()
specifier|public
name|RegistrationCommand
name|getRegistrationCommand
parameter_list|()
block|{
return|return
name|registrationCommand
return|;
block|}
annotation|@
name|JsonProperty
argument_list|(
literal|"registrationCommand"
argument_list|)
DECL|method|setRegistrationCommand (RegistrationCommand registrationCommand)
specifier|public
name|void
name|setRegistrationCommand
parameter_list|(
name|RegistrationCommand
name|registrationCommand
parameter_list|)
block|{
name|this
operator|.
name|registrationCommand
operator|=
name|registrationCommand
expr_stmt|;
block|}
annotation|@
name|JsonProperty
argument_list|(
literal|"restartAgent"
argument_list|)
DECL|method|isRestartAgent ()
specifier|public
name|boolean
name|isRestartAgent
parameter_list|()
block|{
return|return
name|restartAgent
return|;
block|}
annotation|@
name|JsonProperty
argument_list|(
literal|"restartAgent"
argument_list|)
DECL|method|setRestartAgent (boolean restartAgent)
specifier|public
name|void
name|setRestartAgent
parameter_list|(
name|boolean
name|restartAgent
parameter_list|)
block|{
name|this
operator|.
name|restartAgent
operator|=
name|restartAgent
expr_stmt|;
block|}
annotation|@
name|JsonProperty
argument_list|(
literal|"restartEnabled"
argument_list|)
DECL|method|getRstartEnabled ()
specifier|public
name|boolean
name|getRstartEnabled
parameter_list|()
block|{
return|return
name|restartEnabled
return|;
block|}
annotation|@
name|JsonProperty
argument_list|(
literal|"restartEnabled"
argument_list|)
DECL|method|setRestartEnabled (boolean restartEnabled)
specifier|public
name|void
name|setRestartEnabled
parameter_list|(
name|boolean
name|restartEnabled
parameter_list|)
block|{
name|this
operator|.
name|restartEnabled
operator|=
name|restartEnabled
expr_stmt|;
block|}
annotation|@
name|JsonProperty
argument_list|(
literal|"hasMappedComponents"
argument_list|)
DECL|method|hasMappedComponents ()
specifier|public
name|boolean
name|hasMappedComponents
parameter_list|()
block|{
return|return
name|hasMappedComponents
return|;
block|}
annotation|@
name|JsonProperty
argument_list|(
literal|"hasMappedComponents"
argument_list|)
DECL|method|setHasMappedComponents (boolean hasMappedComponents)
specifier|public
name|void
name|setHasMappedComponents
parameter_list|(
name|boolean
name|hasMappedComponents
parameter_list|)
block|{
name|this
operator|.
name|hasMappedComponents
operator|=
name|hasMappedComponents
expr_stmt|;
block|}
annotation|@
name|JsonProperty
argument_list|(
literal|"terminateAgent"
argument_list|)
DECL|method|isTerminateAgent ()
specifier|public
name|boolean
name|isTerminateAgent
parameter_list|()
block|{
return|return
name|terminateAgent
return|;
block|}
annotation|@
name|JsonProperty
argument_list|(
literal|"terminateAgent"
argument_list|)
DECL|method|setTerminateAgent (boolean terminateAgent)
specifier|public
name|void
name|setTerminateAgent
parameter_list|(
name|boolean
name|terminateAgent
parameter_list|)
block|{
name|this
operator|.
name|terminateAgent
operator|=
name|terminateAgent
expr_stmt|;
block|}
DECL|method|addExecutionCommand (ExecutionCommand execCmd)
specifier|public
name|void
name|addExecutionCommand
parameter_list|(
name|ExecutionCommand
name|execCmd
parameter_list|)
block|{
name|executionCommands
operator|.
name|add
argument_list|(
name|execCmd
argument_list|)
expr_stmt|;
block|}
DECL|method|addStatusCommand (StatusCommand statCmd)
specifier|public
name|void
name|addStatusCommand
parameter_list|(
name|StatusCommand
name|statCmd
parameter_list|)
block|{
name|statusCommands
operator|.
name|add
argument_list|(
name|statCmd
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"HeartBeatResponse{"
operator|+
literal|"responseId="
operator|+
name|responseId
operator|+
literal|", executionCommands="
operator|+
name|executionCommands
operator|+
literal|", statusCommands="
operator|+
name|statusCommands
operator|+
literal|", registrationCommand="
operator|+
name|registrationCommand
operator|+
literal|", restartAgent="
operator|+
name|restartAgent
operator|+
literal|", terminateAgent="
operator|+
name|terminateAgent
operator|+
literal|'}'
return|;
block|}
block|}
end_class

end_unit

