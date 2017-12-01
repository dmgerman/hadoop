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
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_comment
comment|/**  * Docker Volume Command, run "docker volume --help" for more details.  */
end_comment

begin_class
DECL|class|DockerVolumeCommand
specifier|public
class|class
name|DockerVolumeCommand
extends|extends
name|DockerCommand
block|{
DECL|field|VOLUME_COMMAND
specifier|public
specifier|static
specifier|final
name|String
name|VOLUME_COMMAND
init|=
literal|"volume"
decl_stmt|;
DECL|field|VOLUME_CREATE_SUB_COMMAND
specifier|public
specifier|static
specifier|final
name|String
name|VOLUME_CREATE_SUB_COMMAND
init|=
literal|"create"
decl_stmt|;
DECL|field|VOLUME_LS_SUB_COMMAND
specifier|public
specifier|static
specifier|final
name|String
name|VOLUME_LS_SUB_COMMAND
init|=
literal|"ls"
decl_stmt|;
comment|// Regex pattern for volume name
DECL|field|VOLUME_NAME_PATTERN
specifier|public
specifier|static
specifier|final
name|Pattern
name|VOLUME_NAME_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"[a-zA-Z0-9][a-zA-Z0-9_.-]*"
argument_list|)
decl_stmt|;
DECL|field|volumeName
specifier|private
name|String
name|volumeName
decl_stmt|;
DECL|field|driverName
specifier|private
name|String
name|driverName
decl_stmt|;
DECL|field|subCommand
specifier|private
name|String
name|subCommand
decl_stmt|;
DECL|method|DockerVolumeCommand (String subCommand)
specifier|public
name|DockerVolumeCommand
parameter_list|(
name|String
name|subCommand
parameter_list|)
block|{
name|super
argument_list|(
name|VOLUME_COMMAND
argument_list|)
expr_stmt|;
name|this
operator|.
name|subCommand
operator|=
name|subCommand
expr_stmt|;
name|super
operator|.
name|addCommandArguments
argument_list|(
literal|"sub-command"
argument_list|,
name|subCommand
argument_list|)
expr_stmt|;
block|}
DECL|method|setVolumeName (String volumeName)
specifier|public
name|DockerVolumeCommand
name|setVolumeName
parameter_list|(
name|String
name|volumeName
parameter_list|)
block|{
name|super
operator|.
name|addCommandArguments
argument_list|(
literal|"volume"
argument_list|,
name|volumeName
argument_list|)
expr_stmt|;
name|this
operator|.
name|volumeName
operator|=
name|volumeName
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setDriverName (String driverName)
specifier|public
name|DockerVolumeCommand
name|setDriverName
parameter_list|(
name|String
name|driverName
parameter_list|)
block|{
name|super
operator|.
name|addCommandArguments
argument_list|(
literal|"driver"
argument_list|,
name|driverName
argument_list|)
expr_stmt|;
name|this
operator|.
name|driverName
operator|=
name|driverName
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|getVolumeName ()
specifier|public
name|String
name|getVolumeName
parameter_list|()
block|{
return|return
name|volumeName
return|;
block|}
DECL|method|getDriverName ()
specifier|public
name|String
name|getDriverName
parameter_list|()
block|{
return|return
name|driverName
return|;
block|}
DECL|method|getSubCommand ()
specifier|public
name|String
name|getSubCommand
parameter_list|()
block|{
return|return
name|subCommand
return|;
block|}
DECL|method|setFormat (String format)
specifier|public
name|DockerVolumeCommand
name|setFormat
parameter_list|(
name|String
name|format
parameter_list|)
block|{
name|super
operator|.
name|addCommandArguments
argument_list|(
literal|"format"
argument_list|,
name|format
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
block|}
end_class

end_unit

