begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  Licensed to the Apache Software Foundation (ASF) under one  *  or more contributor license agreements.  See the NOTICE file  *  distributed with this work for additional information  *  regarding copyright ownership.  The ASF licenses this file  *  to you under the Apache License, Version 2.0 (the  *  "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.runtime.docker
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
name|containermanager
operator|.
name|linux
operator|.
name|runtime
operator|.
name|docker
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * Encapsulates the docker exec command and its command  * line arguments.  */
end_comment

begin_class
DECL|class|DockerExecCommand
specifier|public
class|class
name|DockerExecCommand
extends|extends
name|DockerCommand
block|{
DECL|field|EXEC_COMMAND
specifier|private
specifier|static
specifier|final
name|String
name|EXEC_COMMAND
init|=
literal|"exec"
decl_stmt|;
DECL|field|userEnv
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|userEnv
decl_stmt|;
DECL|method|DockerExecCommand (String containerId)
specifier|public
name|DockerExecCommand
parameter_list|(
name|String
name|containerId
parameter_list|)
block|{
name|super
argument_list|(
name|EXEC_COMMAND
argument_list|)
expr_stmt|;
name|super
operator|.
name|addCommandArguments
argument_list|(
literal|"name"
argument_list|,
name|containerId
argument_list|)
expr_stmt|;
name|this
operator|.
name|userEnv
operator|=
operator|new
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
expr_stmt|;
block|}
DECL|method|setInteractive ()
specifier|public
name|DockerExecCommand
name|setInteractive
parameter_list|()
block|{
name|super
operator|.
name|addCommandArguments
argument_list|(
literal|"interactive"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setTTY ()
specifier|public
name|DockerExecCommand
name|setTTY
parameter_list|()
block|{
name|super
operator|.
name|addCommandArguments
argument_list|(
literal|"tty"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setOverrideCommandWithArgs ( List<String> overrideCommandWithArgs)
specifier|public
name|DockerExecCommand
name|setOverrideCommandWithArgs
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|overrideCommandWithArgs
parameter_list|)
block|{
for|for
control|(
name|String
name|override
range|:
name|overrideCommandWithArgs
control|)
block|{
name|super
operator|.
name|addCommandArguments
argument_list|(
literal|"launch-command"
argument_list|,
name|override
argument_list|)
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|getDockerCommandWithArguments ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|getDockerCommandWithArguments
parameter_list|()
block|{
return|return
name|super
operator|.
name|getDockerCommandWithArguments
argument_list|()
return|;
block|}
block|}
end_class

end_unit

