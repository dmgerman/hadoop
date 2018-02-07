begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * *  *  Licensed to the Apache Software Foundation (ASF) under one  *  or more contributor license agreements.  See the NOTICE file  *  distributed with this work for additional information  *  regarding copyright ownership.  The ASF licenses this file  *  to you under the Apache License, Version 2.0 (the  *  "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  * /  */
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|classification
operator|.
name|InterfaceAudience
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
name|classification
operator|.
name|InterfaceStability
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
name|StringUtils
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
name|Collections
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
import|;
end_import

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
comment|/** Represents a docker sub-command  * e.g 'run', 'load', 'inspect' etc.,  */
DECL|class|DockerCommand
specifier|public
specifier|abstract
class|class
name|DockerCommand
block|{
DECL|field|command
specifier|private
specifier|final
name|String
name|command
decl_stmt|;
DECL|field|commandArguments
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|commandArguments
decl_stmt|;
DECL|method|DockerCommand (String command)
specifier|protected
name|DockerCommand
parameter_list|(
name|String
name|command
parameter_list|)
block|{
name|String
name|dockerCommandKey
init|=
literal|"docker-command"
decl_stmt|;
name|this
operator|.
name|command
operator|=
name|command
expr_stmt|;
name|this
operator|.
name|commandArguments
operator|=
operator|new
name|TreeMap
argument_list|<>
argument_list|()
expr_stmt|;
name|commandArguments
operator|.
name|put
argument_list|(
name|dockerCommandKey
argument_list|,
operator|new
name|ArrayList
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
name|commandArguments
operator|.
name|get
argument_list|(
name|dockerCommandKey
argument_list|)
operator|.
name|add
argument_list|(
name|command
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns the docker sub-command string being used    * e.g 'run'.    */
DECL|method|getCommandOption ()
specifier|public
specifier|final
name|String
name|getCommandOption
parameter_list|()
block|{
return|return
name|this
operator|.
name|command
return|;
block|}
comment|/**    * Add command commandWithArguments - this method is only meant for use by    * sub-classes.    *    * @param key   name of the key to be added    * @param value value of the key    */
DECL|method|addCommandArguments (String key, String value)
specifier|protected
specifier|final
name|void
name|addCommandArguments
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|list
init|=
name|commandArguments
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|list
operator|!=
literal|null
condition|)
block|{
name|list
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
return|return;
block|}
name|list
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|this
operator|.
name|commandArguments
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|list
argument_list|)
expr_stmt|;
block|}
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
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
name|commandArguments
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuffer
name|ret
init|=
operator|new
name|StringBuffer
argument_list|(
name|this
operator|.
name|command
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|entry
range|:
name|commandArguments
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|ret
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
operator|.
name|append
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|ret
operator|.
name|append
argument_list|(
literal|"="
argument_list|)
operator|.
name|append
argument_list|(
name|StringUtils
operator|.
name|join
argument_list|(
literal|","
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|ret
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Add the client configuration directory to the docker command.    *    * The client configuration option proceeds any of the docker subcommands    * (such as run, load, pull, etc). Ordering will be handled by    * container-executor. Docker expects the value to be a directory containing    * the file config.json. This file is typically generated via docker login.    *    * @param clientConfigDir - directory containing the docker client config.    */
DECL|method|setClientConfigDir (String clientConfigDir)
specifier|public
name|void
name|setClientConfigDir
parameter_list|(
name|String
name|clientConfigDir
parameter_list|)
block|{
if|if
condition|(
name|clientConfigDir
operator|!=
literal|null
condition|)
block|{
name|addCommandArguments
argument_list|(
literal|"docker-config"
argument_list|,
name|clientConfigDir
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

