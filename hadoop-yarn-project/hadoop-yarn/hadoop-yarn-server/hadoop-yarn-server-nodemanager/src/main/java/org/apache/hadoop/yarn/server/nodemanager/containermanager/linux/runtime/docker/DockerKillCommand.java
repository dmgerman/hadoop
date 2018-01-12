begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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

begin_comment
comment|/**  * Encapsulates the docker kill command and its command line arguments.  */
end_comment

begin_class
DECL|class|DockerKillCommand
specifier|public
class|class
name|DockerKillCommand
extends|extends
name|DockerCommand
block|{
DECL|field|KILL_COMMAND
specifier|private
specifier|static
specifier|final
name|String
name|KILL_COMMAND
init|=
literal|"kill"
decl_stmt|;
DECL|method|DockerKillCommand (String containerName)
specifier|public
name|DockerKillCommand
parameter_list|(
name|String
name|containerName
parameter_list|)
block|{
name|super
argument_list|(
name|KILL_COMMAND
argument_list|)
expr_stmt|;
name|super
operator|.
name|addCommandArguments
argument_list|(
literal|"name"
argument_list|,
name|containerName
argument_list|)
expr_stmt|;
block|}
comment|/**    * Set the signal for the {@link DockerKillCommand}.    *    * @param signal  the signal to send to the container.    * @return the {@link DockerKillCommand} with the signal set.    */
DECL|method|setSignal (String signal)
specifier|public
name|DockerKillCommand
name|setSignal
parameter_list|(
name|String
name|signal
parameter_list|)
block|{
name|super
operator|.
name|addCommandArguments
argument_list|(
literal|"signal"
argument_list|,
name|signal
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
block|}
end_class

end_unit

