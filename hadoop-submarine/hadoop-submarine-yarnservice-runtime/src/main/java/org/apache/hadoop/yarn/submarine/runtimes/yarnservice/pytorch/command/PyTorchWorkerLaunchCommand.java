begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *     http://www.apache.org/licenses/LICENSE-2.0  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.submarine.runtimes.yarnservice.pytorch.command
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
name|pytorch
operator|.
name|command
package|;
end_package

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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang3
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
name|runjob
operator|.
name|PyTorchRunJobParameters
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
name|common
operator|.
name|api
operator|.
name|Role
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
name|common
operator|.
name|conf
operator|.
name|SubmarineLogs
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
operator|.
name|AbstractLaunchCommand
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
name|command
operator|.
name|LaunchScriptBuilder
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

begin_comment
comment|/**  * Launch command implementation for PyTorch components.  */
end_comment

begin_class
DECL|class|PyTorchWorkerLaunchCommand
specifier|public
class|class
name|PyTorchWorkerLaunchCommand
extends|extends
name|AbstractLaunchCommand
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
name|PyTorchWorkerLaunchCommand
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|yarnConfig
specifier|private
specifier|final
name|Configuration
name|yarnConfig
decl_stmt|;
DECL|field|distributed
specifier|private
specifier|final
name|boolean
name|distributed
decl_stmt|;
DECL|field|numberOfWorkers
specifier|private
specifier|final
name|int
name|numberOfWorkers
decl_stmt|;
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|role
specifier|private
specifier|final
name|Role
name|role
decl_stmt|;
DECL|field|launchCommand
specifier|private
specifier|final
name|String
name|launchCommand
decl_stmt|;
DECL|method|PyTorchWorkerLaunchCommand (HadoopEnvironmentSetup hadoopEnvSetup, Role role, Component component, PyTorchRunJobParameters parameters, Configuration yarnConfig)
specifier|public
name|PyTorchWorkerLaunchCommand
parameter_list|(
name|HadoopEnvironmentSetup
name|hadoopEnvSetup
parameter_list|,
name|Role
name|role
parameter_list|,
name|Component
name|component
parameter_list|,
name|PyTorchRunJobParameters
name|parameters
parameter_list|,
name|Configuration
name|yarnConfig
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|hadoopEnvSetup
argument_list|,
name|component
argument_list|,
name|parameters
argument_list|,
name|role
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|role
operator|=
name|role
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|parameters
operator|.
name|getName
argument_list|()
expr_stmt|;
name|this
operator|.
name|distributed
operator|=
name|parameters
operator|.
name|isDistributed
argument_list|()
expr_stmt|;
name|this
operator|.
name|numberOfWorkers
operator|=
name|parameters
operator|.
name|getNumWorkers
argument_list|()
expr_stmt|;
name|this
operator|.
name|yarnConfig
operator|=
name|yarnConfig
expr_stmt|;
name|logReceivedParameters
argument_list|()
expr_stmt|;
name|this
operator|.
name|launchCommand
operator|=
name|parameters
operator|.
name|getWorkerLaunchCmd
argument_list|()
expr_stmt|;
if|if
condition|(
name|StringUtils
operator|.
name|isEmpty
argument_list|(
name|this
operator|.
name|launchCommand
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"LaunchCommand must not be null "
operator|+
literal|"or empty!"
argument_list|)
throw|;
block|}
block|}
DECL|method|logReceivedParameters ()
specifier|private
name|void
name|logReceivedParameters
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|numberOfWorkers
operator|<=
literal|0
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Received number of workers: {}"
argument_list|,
name|this
operator|.
name|numberOfWorkers
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|generateLaunchScript ()
specifier|public
name|String
name|generateLaunchScript
parameter_list|()
throws|throws
name|IOException
block|{
name|LaunchScriptBuilder
name|builder
init|=
name|getBuilder
argument_list|()
decl_stmt|;
return|return
name|builder
operator|.
name|withLaunchCommand
argument_list|(
name|createLaunchCommand
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|createLaunchCommand ()
specifier|public
name|String
name|createLaunchCommand
parameter_list|()
block|{
if|if
condition|(
name|SubmarineLogs
operator|.
name|isVerbose
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"PyTorch Worker command =["
operator|+
name|launchCommand
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
return|return
name|launchCommand
operator|+
literal|'\n'
return|;
block|}
block|}
end_class

end_unit

