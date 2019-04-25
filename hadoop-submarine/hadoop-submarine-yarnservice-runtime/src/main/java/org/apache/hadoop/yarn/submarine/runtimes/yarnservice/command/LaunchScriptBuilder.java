begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *     http://www.apache.org/licenses/LICENSE-2.0  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.submarine.runtimes.yarnservice.command
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|submarine
operator|.
name|runtimes
operator|.
name|yarnservice
operator|.
name|command
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
name|yarn
operator|.
name|service
operator|.
name|api
operator|.
name|records
operator|.
name|Component
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
name|submarine
operator|.
name|client
operator|.
name|cli
operator|.
name|param
operator|.
name|RunJobParameters
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
name|submarine
operator|.
name|runtimes
operator|.
name|yarnservice
operator|.
name|HadoopEnvironmentSetup
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
import|import static
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
operator|.
name|UTF_8
import|;
end_import

begin_comment
comment|/**  * This class is a builder to conveniently create launch scripts.  * All dependencies are provided with the constructor except  * the launch command.  */
end_comment

begin_class
DECL|class|LaunchScriptBuilder
specifier|public
class|class
name|LaunchScriptBuilder
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
name|LaunchScriptBuilder
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|file
specifier|private
specifier|final
name|File
name|file
decl_stmt|;
DECL|field|hadoopEnvSetup
specifier|private
specifier|final
name|HadoopEnvironmentSetup
name|hadoopEnvSetup
decl_stmt|;
DECL|field|parameters
specifier|private
specifier|final
name|RunJobParameters
name|parameters
decl_stmt|;
DECL|field|component
specifier|private
specifier|final
name|Component
name|component
decl_stmt|;
DECL|field|writer
specifier|private
specifier|final
name|OutputStreamWriter
name|writer
decl_stmt|;
DECL|field|scriptBuffer
specifier|private
specifier|final
name|StringBuilder
name|scriptBuffer
decl_stmt|;
DECL|field|launchCommand
specifier|private
name|String
name|launchCommand
decl_stmt|;
DECL|method|LaunchScriptBuilder (String namePrefix, HadoopEnvironmentSetup hadoopEnvSetup, RunJobParameters parameters, Component component)
name|LaunchScriptBuilder
parameter_list|(
name|String
name|namePrefix
parameter_list|,
name|HadoopEnvironmentSetup
name|hadoopEnvSetup
parameter_list|,
name|RunJobParameters
name|parameters
parameter_list|,
name|Component
name|component
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|file
operator|=
name|File
operator|.
name|createTempFile
argument_list|(
name|namePrefix
operator|+
literal|"-launch-script"
argument_list|,
literal|".sh"
argument_list|)
expr_stmt|;
name|this
operator|.
name|hadoopEnvSetup
operator|=
name|hadoopEnvSetup
expr_stmt|;
name|this
operator|.
name|parameters
operator|=
name|parameters
expr_stmt|;
name|this
operator|.
name|component
operator|=
name|component
expr_stmt|;
name|this
operator|.
name|writer
operator|=
operator|new
name|OutputStreamWriter
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
name|file
argument_list|)
argument_list|,
name|UTF_8
argument_list|)
expr_stmt|;
name|this
operator|.
name|scriptBuffer
operator|=
operator|new
name|StringBuilder
argument_list|()
expr_stmt|;
block|}
DECL|method|append (String s)
specifier|public
name|void
name|append
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|scriptBuffer
operator|.
name|append
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
DECL|method|withLaunchCommand (String command)
specifier|public
name|LaunchScriptBuilder
name|withLaunchCommand
parameter_list|(
name|String
name|command
parameter_list|)
block|{
name|this
operator|.
name|launchCommand
operator|=
name|command
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|build ()
specifier|public
name|String
name|build
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|launchCommand
operator|!=
literal|null
condition|)
block|{
name|append
argument_list|(
name|launchCommand
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"LaunchScript object was null!"
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"LaunchScript's Builder object: {}"
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
block|}
try|try
init|(
name|PrintWriter
name|pw
init|=
operator|new
name|PrintWriter
argument_list|(
name|writer
argument_list|)
init|)
block|{
name|writeBashHeader
argument_list|(
name|pw
argument_list|)
expr_stmt|;
name|hadoopEnvSetup
operator|.
name|addHdfsClassPath
argument_list|(
name|parameters
argument_list|,
name|pw
argument_list|,
name|component
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Appending command to launch script: {}"
argument_list|,
name|scriptBuffer
argument_list|)
expr_stmt|;
block|}
name|pw
operator|.
name|append
argument_list|(
name|scriptBuffer
argument_list|)
expr_stmt|;
block|}
return|return
name|file
operator|.
name|getAbsolutePath
argument_list|()
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
return|return
literal|"LaunchScriptBuilder{"
operator|+
literal|"file="
operator|+
name|file
operator|+
literal|", hadoopEnvSetup="
operator|+
name|hadoopEnvSetup
operator|+
literal|", parameters="
operator|+
name|parameters
operator|+
literal|", component="
operator|+
name|component
operator|+
literal|", writer="
operator|+
name|writer
operator|+
literal|", scriptBuffer="
operator|+
name|scriptBuffer
operator|+
literal|", launchCommand='"
operator|+
name|launchCommand
operator|+
literal|'\''
operator|+
literal|'}'
return|;
block|}
DECL|method|writeBashHeader (PrintWriter pw)
specifier|private
name|void
name|writeBashHeader
parameter_list|(
name|PrintWriter
name|pw
parameter_list|)
block|{
name|pw
operator|.
name|append
argument_list|(
literal|"#!/bin/bash\n"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

