begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *     http://www.apache.org/licenses/LICENSE-2.0  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.submarine.runtimes.yarnservice.tensorflow.command
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
name|tensorflow
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
name|TensorFlowRunJobParameters
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
name|IOException
import|;
end_import

begin_comment
comment|/**  * Launch command implementation for Tensorboard's PS component.  */
end_comment

begin_class
DECL|class|TensorFlowPsLaunchCommand
specifier|public
class|class
name|TensorFlowPsLaunchCommand
extends|extends
name|TensorFlowLaunchCommand
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
name|TensorFlowPsLaunchCommand
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|launchCommand
specifier|private
specifier|final
name|String
name|launchCommand
decl_stmt|;
DECL|method|TensorFlowPsLaunchCommand (HadoopEnvironmentSetup hadoopEnvSetup, Role role, Component component, TensorFlowRunJobParameters parameters, Configuration yarnConfig)
specifier|public
name|TensorFlowPsLaunchCommand
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
name|TensorFlowRunJobParameters
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
name|role
argument_list|,
name|component
argument_list|,
name|parameters
argument_list|,
name|yarnConfig
argument_list|)
expr_stmt|;
name|this
operator|.
name|launchCommand
operator|=
name|parameters
operator|.
name|getPSLaunchCmd
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
literal|"PS command =["
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

