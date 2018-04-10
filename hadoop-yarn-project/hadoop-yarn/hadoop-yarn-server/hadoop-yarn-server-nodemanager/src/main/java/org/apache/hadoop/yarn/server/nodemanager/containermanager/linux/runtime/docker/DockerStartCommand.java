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
comment|/**  * Encapsulates the docker start command and its command line arguments.  */
end_comment

begin_class
DECL|class|DockerStartCommand
specifier|public
class|class
name|DockerStartCommand
extends|extends
name|DockerCommand
block|{
DECL|field|START_COMMAND
specifier|private
specifier|static
specifier|final
name|String
name|START_COMMAND
init|=
literal|"start"
decl_stmt|;
DECL|method|DockerStartCommand (String containerName)
specifier|public
name|DockerStartCommand
parameter_list|(
name|String
name|containerName
parameter_list|)
block|{
name|super
argument_list|(
name|START_COMMAND
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
block|}
end_class

end_unit

