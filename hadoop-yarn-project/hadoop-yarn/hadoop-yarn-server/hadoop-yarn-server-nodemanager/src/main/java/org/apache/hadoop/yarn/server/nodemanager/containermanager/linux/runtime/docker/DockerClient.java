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
name|util
operator|.
name|StringUtils
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
name|api
operator|.
name|records
operator|.
name|ApplicationId
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
name|api
operator|.
name|records
operator|.
name|ContainerId
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
name|Context
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
name|container
operator|.
name|Container
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
name|localizer
operator|.
name|ResourceLocalizationService
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
name|ContainerExecutionException
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
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
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
name|io
operator|.
name|OutputStreamWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Writer
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
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|DockerClient
specifier|public
specifier|final
class|class
name|DockerClient
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
name|DockerClient
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|TMP_FILE_PREFIX
specifier|private
specifier|static
specifier|final
name|String
name|TMP_FILE_PREFIX
init|=
literal|"docker."
decl_stmt|;
DECL|field|TMP_FILE_SUFFIX
specifier|private
specifier|static
specifier|final
name|String
name|TMP_FILE_SUFFIX
init|=
literal|".cmd"
decl_stmt|;
DECL|field|tmpDirPath
specifier|private
specifier|final
name|String
name|tmpDirPath
decl_stmt|;
DECL|method|DockerClient (Configuration conf)
specifier|public
name|DockerClient
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|ContainerExecutionException
block|{
name|String
name|tmpDirBase
init|=
name|conf
operator|.
name|get
argument_list|(
literal|"hadoop.tmp.dir"
argument_list|)
decl_stmt|;
if|if
condition|(
name|tmpDirBase
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ContainerExecutionException
argument_list|(
literal|"hadoop.tmp.dir not set!"
argument_list|)
throw|;
block|}
name|tmpDirPath
operator|=
name|tmpDirBase
operator|+
literal|"/nm-docker-cmds"
expr_stmt|;
name|File
name|tmpDir
init|=
operator|new
name|File
argument_list|(
name|tmpDirPath
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|tmpDir
operator|.
name|exists
argument_list|()
operator|||
name|tmpDir
operator|.
name|mkdirs
argument_list|()
operator|)
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unable to create directory: "
operator|+
name|tmpDirPath
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|ContainerExecutionException
argument_list|(
literal|"Unable to create directory: "
operator|+
name|tmpDirPath
argument_list|)
throw|;
block|}
block|}
DECL|method|writeCommandToTempFile (DockerCommand cmd, String filePrefix)
specifier|public
name|String
name|writeCommandToTempFile
parameter_list|(
name|DockerCommand
name|cmd
parameter_list|,
name|String
name|filePrefix
parameter_list|)
throws|throws
name|ContainerExecutionException
block|{
name|File
name|dockerCommandFile
init|=
literal|null
decl_stmt|;
try|try
block|{
name|dockerCommandFile
operator|=
name|File
operator|.
name|createTempFile
argument_list|(
name|TMP_FILE_PREFIX
operator|+
name|filePrefix
argument_list|,
name|TMP_FILE_SUFFIX
argument_list|,
operator|new
name|File
argument_list|(
name|tmpDirPath
argument_list|)
argument_list|)
expr_stmt|;
name|Writer
name|writer
init|=
operator|new
name|OutputStreamWriter
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
name|dockerCommandFile
argument_list|)
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
name|PrintWriter
name|printWriter
init|=
operator|new
name|PrintWriter
argument_list|(
name|writer
argument_list|)
decl_stmt|;
name|printWriter
operator|.
name|println
argument_list|(
literal|"[docker-command-execution]"
argument_list|)
expr_stmt|;
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
name|cmd
operator|.
name|getDockerCommandWithArguments
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|contains
argument_list|(
literal|"="
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|ContainerExecutionException
argument_list|(
literal|"'=' found in entry for docker command file, key = "
operator|+
name|entry
operator|.
name|getKey
argument_list|()
operator|+
literal|"; value = "
operator|+
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|contains
argument_list|(
literal|"\n"
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|ContainerExecutionException
argument_list|(
literal|"'\\n' found in entry for docker command file, key = "
operator|+
name|entry
operator|.
name|getKey
argument_list|()
operator|+
literal|"; value = "
operator|+
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
throw|;
block|}
name|printWriter
operator|.
name|println
argument_list|(
literal|"  "
operator|+
name|entry
operator|.
name|getKey
argument_list|()
operator|+
literal|"="
operator|+
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
name|printWriter
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|dockerCommandFile
operator|.
name|getAbsolutePath
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unable to write docker command to temporary file!"
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|ContainerExecutionException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|writeCommandToTempFile (DockerCommand cmd, Container container, Context nmContext)
specifier|public
name|String
name|writeCommandToTempFile
parameter_list|(
name|DockerCommand
name|cmd
parameter_list|,
name|Container
name|container
parameter_list|,
name|Context
name|nmContext
parameter_list|)
throws|throws
name|ContainerExecutionException
block|{
name|ContainerId
name|containerId
init|=
name|container
operator|.
name|getContainerId
argument_list|()
decl_stmt|;
name|String
name|filePrefix
init|=
name|containerId
operator|.
name|toString
argument_list|()
decl_stmt|;
name|ApplicationId
name|appId
init|=
name|containerId
operator|.
name|getApplicationAttemptId
argument_list|()
operator|.
name|getApplicationId
argument_list|()
decl_stmt|;
name|File
name|dockerCommandFile
decl_stmt|;
name|String
name|cmdDir
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|nmContext
operator|==
literal|null
operator|||
name|nmContext
operator|.
name|getLocalDirsHandler
argument_list|()
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ContainerExecutionException
argument_list|(
literal|"Unable to write temporary docker command"
argument_list|)
throw|;
block|}
try|try
block|{
name|cmdDir
operator|=
name|nmContext
operator|.
name|getLocalDirsHandler
argument_list|()
operator|.
name|getLocalPathForWrite
argument_list|(
name|ResourceLocalizationService
operator|.
name|NM_PRIVATE_DIR
operator|+
name|Path
operator|.
name|SEPARATOR
operator|+
name|appId
operator|+
name|Path
operator|.
name|SEPARATOR
operator|+
name|filePrefix
operator|+
name|Path
operator|.
name|SEPARATOR
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
name|dockerCommandFile
operator|=
name|File
operator|.
name|createTempFile
argument_list|(
name|TMP_FILE_PREFIX
operator|+
name|filePrefix
argument_list|,
name|TMP_FILE_SUFFIX
argument_list|,
operator|new
name|File
argument_list|(
name|cmdDir
argument_list|)
argument_list|)
expr_stmt|;
name|Writer
name|writer
init|=
operator|new
name|OutputStreamWriter
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
name|dockerCommandFile
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
name|PrintWriter
name|printWriter
init|=
operator|new
name|PrintWriter
argument_list|(
name|writer
argument_list|)
decl_stmt|;
name|printWriter
operator|.
name|println
argument_list|(
literal|"[docker-command-execution]"
argument_list|)
expr_stmt|;
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
name|cmd
operator|.
name|getDockerCommandWithArguments
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|contains
argument_list|(
literal|"="
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|ContainerExecutionException
argument_list|(
literal|"'=' found in entry for docker command file, key = "
operator|+
name|entry
operator|.
name|getKey
argument_list|()
operator|+
literal|"; value = "
operator|+
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|contains
argument_list|(
literal|"\n"
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|ContainerExecutionException
argument_list|(
literal|"'\\n' found in entry for docker command file, key = "
operator|+
name|entry
operator|.
name|getKey
argument_list|()
operator|+
literal|"; value = "
operator|+
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
throw|;
block|}
name|printWriter
operator|.
name|println
argument_list|(
literal|"  "
operator|+
name|entry
operator|.
name|getKey
argument_list|()
operator|+
literal|"="
operator|+
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
name|printWriter
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|dockerCommandFile
operator|.
name|toString
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unable to write docker command to "
operator|+
name|cmdDir
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|ContainerExecutionException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

