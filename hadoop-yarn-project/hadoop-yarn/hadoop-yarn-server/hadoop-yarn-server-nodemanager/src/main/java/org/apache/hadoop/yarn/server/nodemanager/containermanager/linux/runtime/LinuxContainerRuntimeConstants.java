begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * *  *  Licensed to the Apache Software Foundation (ASF) under one  *  or more contributor license agreements.  See the NOTICE file  *  distributed with this work for additional information  *  regarding copyright ownership.  The ASF licenses this file  *  to you under the Apache License, Version 2.0 (the  *  "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  * /  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.runtime
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
name|fs
operator|.
name|Path
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
name|server
operator|.
name|nodemanager
operator|.
name|ContainerExecutor
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
name|server
operator|.
name|nodemanager
operator|.
name|containermanager
operator|.
name|runtime
operator|.
name|ContainerRuntimeContext
operator|.
name|Attribute
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

begin_class
DECL|class|LinuxContainerRuntimeConstants
specifier|public
specifier|final
class|class
name|LinuxContainerRuntimeConstants
block|{
DECL|method|LinuxContainerRuntimeConstants ()
specifier|private
name|LinuxContainerRuntimeConstants
parameter_list|()
block|{   }
DECL|field|LOCALIZED_RESOURCES
specifier|public
specifier|static
specifier|final
name|Attribute
argument_list|<
name|Map
argument_list|>
name|LOCALIZED_RESOURCES
init|=
name|Attribute
operator|.
name|attribute
argument_list|(
name|Map
operator|.
name|class
argument_list|,
literal|"localized_resources"
argument_list|)
decl_stmt|;
DECL|field|CONTAINER_LAUNCH_PREFIX_COMMANDS
specifier|public
specifier|static
specifier|final
name|Attribute
argument_list|<
name|List
argument_list|>
name|CONTAINER_LAUNCH_PREFIX_COMMANDS
init|=
name|Attribute
operator|.
name|attribute
argument_list|(
name|List
operator|.
name|class
argument_list|,
literal|"container_launch_prefix_commands"
argument_list|)
decl_stmt|;
DECL|field|RUN_AS_USER
specifier|public
specifier|static
specifier|final
name|Attribute
argument_list|<
name|String
argument_list|>
name|RUN_AS_USER
init|=
name|Attribute
operator|.
name|attribute
argument_list|(
name|String
operator|.
name|class
argument_list|,
literal|"run_as_user"
argument_list|)
decl_stmt|;
DECL|field|USER
specifier|public
specifier|static
specifier|final
name|Attribute
argument_list|<
name|String
argument_list|>
name|USER
init|=
name|Attribute
operator|.
name|attribute
argument_list|(
name|String
operator|.
name|class
argument_list|,
literal|"user"
argument_list|)
decl_stmt|;
DECL|field|APPID
specifier|public
specifier|static
specifier|final
name|Attribute
argument_list|<
name|String
argument_list|>
name|APPID
init|=
name|Attribute
operator|.
name|attribute
argument_list|(
name|String
operator|.
name|class
argument_list|,
literal|"appid"
argument_list|)
decl_stmt|;
DECL|field|CONTAINER_ID_STR
specifier|public
specifier|static
specifier|final
name|Attribute
argument_list|<
name|String
argument_list|>
name|CONTAINER_ID_STR
init|=
name|Attribute
operator|.
name|attribute
argument_list|(
name|String
operator|.
name|class
argument_list|,
literal|"container_id_str"
argument_list|)
decl_stmt|;
DECL|field|CONTAINER_WORK_DIR
specifier|public
specifier|static
specifier|final
name|Attribute
argument_list|<
name|Path
argument_list|>
name|CONTAINER_WORK_DIR
init|=
name|Attribute
operator|.
name|attribute
argument_list|(
name|Path
operator|.
name|class
argument_list|,
literal|"container_work_dir"
argument_list|)
decl_stmt|;
DECL|field|NM_PRIVATE_CONTAINER_SCRIPT_PATH
specifier|public
specifier|static
specifier|final
name|Attribute
argument_list|<
name|Path
argument_list|>
name|NM_PRIVATE_CONTAINER_SCRIPT_PATH
init|=
name|Attribute
operator|.
name|attribute
argument_list|(
name|Path
operator|.
name|class
argument_list|,
literal|"nm_private_container_script_path"
argument_list|)
decl_stmt|;
DECL|field|NM_PRIVATE_TOKENS_PATH
specifier|public
specifier|static
specifier|final
name|Attribute
argument_list|<
name|Path
argument_list|>
name|NM_PRIVATE_TOKENS_PATH
init|=
name|Attribute
operator|.
name|attribute
argument_list|(
name|Path
operator|.
name|class
argument_list|,
literal|"nm_private_tokens_path"
argument_list|)
decl_stmt|;
DECL|field|PID_FILE_PATH
specifier|public
specifier|static
specifier|final
name|Attribute
argument_list|<
name|Path
argument_list|>
name|PID_FILE_PATH
init|=
name|Attribute
operator|.
name|attribute
argument_list|(
name|Path
operator|.
name|class
argument_list|,
literal|"pid_file_path"
argument_list|)
decl_stmt|;
DECL|field|LOCAL_DIRS
specifier|public
specifier|static
specifier|final
name|Attribute
argument_list|<
name|List
argument_list|>
name|LOCAL_DIRS
init|=
name|Attribute
operator|.
name|attribute
argument_list|(
name|List
operator|.
name|class
argument_list|,
literal|"local_dirs"
argument_list|)
decl_stmt|;
DECL|field|LOG_DIRS
specifier|public
specifier|static
specifier|final
name|Attribute
argument_list|<
name|List
argument_list|>
name|LOG_DIRS
init|=
name|Attribute
operator|.
name|attribute
argument_list|(
name|List
operator|.
name|class
argument_list|,
literal|"log_dirs"
argument_list|)
decl_stmt|;
DECL|field|FILECACHE_DIRS
specifier|public
specifier|static
specifier|final
name|Attribute
argument_list|<
name|List
argument_list|>
name|FILECACHE_DIRS
init|=
name|Attribute
operator|.
name|attribute
argument_list|(
name|List
operator|.
name|class
argument_list|,
literal|"filecache_dirs"
argument_list|)
decl_stmt|;
DECL|field|USER_LOCAL_DIRS
specifier|public
specifier|static
specifier|final
name|Attribute
argument_list|<
name|List
argument_list|>
name|USER_LOCAL_DIRS
init|=
name|Attribute
operator|.
name|attribute
argument_list|(
name|List
operator|.
name|class
argument_list|,
literal|"user_local_dirs"
argument_list|)
decl_stmt|;
DECL|field|CONTAINER_LOCAL_DIRS
specifier|public
specifier|static
specifier|final
name|Attribute
argument_list|<
name|List
argument_list|>
name|CONTAINER_LOCAL_DIRS
init|=
name|Attribute
operator|.
name|attribute
argument_list|(
name|List
operator|.
name|class
argument_list|,
literal|"container_local_dirs"
argument_list|)
decl_stmt|;
DECL|field|CONTAINER_LOG_DIRS
specifier|public
specifier|static
specifier|final
name|Attribute
argument_list|<
name|List
argument_list|>
name|CONTAINER_LOG_DIRS
init|=
name|Attribute
operator|.
name|attribute
argument_list|(
name|List
operator|.
name|class
argument_list|,
literal|"container_log_dirs"
argument_list|)
decl_stmt|;
DECL|field|RESOURCES_OPTIONS
specifier|public
specifier|static
specifier|final
name|Attribute
argument_list|<
name|String
argument_list|>
name|RESOURCES_OPTIONS
init|=
name|Attribute
operator|.
name|attribute
argument_list|(
name|String
operator|.
name|class
argument_list|,
literal|"resources_options"
argument_list|)
decl_stmt|;
DECL|field|TC_COMMAND_FILE
specifier|public
specifier|static
specifier|final
name|Attribute
argument_list|<
name|String
argument_list|>
name|TC_COMMAND_FILE
init|=
name|Attribute
operator|.
name|attribute
argument_list|(
name|String
operator|.
name|class
argument_list|,
literal|"tc_command_file"
argument_list|)
decl_stmt|;
DECL|field|CONTAINER_RUN_CMDS
specifier|public
specifier|static
specifier|final
name|Attribute
argument_list|<
name|List
argument_list|>
name|CONTAINER_RUN_CMDS
init|=
name|Attribute
operator|.
name|attribute
argument_list|(
name|List
operator|.
name|class
argument_list|,
literal|"container_run_cmds"
argument_list|)
decl_stmt|;
DECL|field|CGROUP_RELATIVE_PATH
specifier|public
specifier|static
specifier|final
name|Attribute
argument_list|<
name|String
argument_list|>
name|CGROUP_RELATIVE_PATH
init|=
name|Attribute
operator|.
name|attribute
argument_list|(
name|String
operator|.
name|class
argument_list|,
literal|"cgroup_relative_path"
argument_list|)
decl_stmt|;
DECL|field|PID
specifier|public
specifier|static
specifier|final
name|Attribute
argument_list|<
name|String
argument_list|>
name|PID
init|=
name|Attribute
operator|.
name|attribute
argument_list|(
name|String
operator|.
name|class
argument_list|,
literal|"pid"
argument_list|)
decl_stmt|;
DECL|field|SIGNAL
specifier|public
specifier|static
specifier|final
name|Attribute
argument_list|<
name|ContainerExecutor
operator|.
name|Signal
argument_list|>
name|SIGNAL
init|=
name|Attribute
operator|.
name|attribute
argument_list|(
name|ContainerExecutor
operator|.
name|Signal
operator|.
name|class
argument_list|,
literal|"signal"
argument_list|)
decl_stmt|;
block|}
end_class

end_unit

