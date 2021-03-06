begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *     http://www.apache.org/licenses/LICENSE-2.0  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.submarine.runtimes.yarnservice.tensorflow
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
name|Service
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
name|client
operator|.
name|cli
operator|.
name|runjob
operator|.
name|Framework
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
name|ClientContext
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
name|AbstractServiceSpec
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
name|FileSystemOperations
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
name|ServiceWrapper
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
name|TensorFlowLaunchCommandFactory
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
name|tensorflow
operator|.
name|component
operator|.
name|TensorBoardComponent
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
name|tensorflow
operator|.
name|component
operator|.
name|TensorFlowPsComponent
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
name|utils
operator|.
name|Localizer
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

begin_import
import|import static
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
name|component
operator|.
name|TensorBoardComponent
operator|.
name|TENSORBOARD_QUICKLINK_LABEL
import|;
end_import

begin_comment
comment|/**  * This class contains all the logic to create an instance  * of a {@link Service} object for TensorFlow.  * Worker,PS and Tensorboard components are added to the Service  * based on the value of the received {@link RunJobParameters}.  */
end_comment

begin_class
DECL|class|TensorFlowServiceSpec
specifier|public
class|class
name|TensorFlowServiceSpec
extends|extends
name|AbstractServiceSpec
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
name|TensorFlowServiceSpec
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|tensorFlowParameters
specifier|private
specifier|final
name|TensorFlowRunJobParameters
name|tensorFlowParameters
decl_stmt|;
DECL|method|TensorFlowServiceSpec (TensorFlowRunJobParameters parameters, ClientContext clientContext, FileSystemOperations fsOperations, TensorFlowLaunchCommandFactory launchCommandFactory, Localizer localizer)
specifier|public
name|TensorFlowServiceSpec
parameter_list|(
name|TensorFlowRunJobParameters
name|parameters
parameter_list|,
name|ClientContext
name|clientContext
parameter_list|,
name|FileSystemOperations
name|fsOperations
parameter_list|,
name|TensorFlowLaunchCommandFactory
name|launchCommandFactory
parameter_list|,
name|Localizer
name|localizer
parameter_list|)
block|{
name|super
argument_list|(
name|parameters
argument_list|,
name|clientContext
argument_list|,
name|fsOperations
argument_list|,
name|launchCommandFactory
argument_list|,
name|localizer
argument_list|)
expr_stmt|;
name|this
operator|.
name|tensorFlowParameters
operator|=
name|parameters
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|create ()
specifier|public
name|ServiceWrapper
name|create
parameter_list|()
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Creating TensorFlow service spec"
argument_list|)
expr_stmt|;
name|ServiceWrapper
name|serviceWrapper
init|=
name|createServiceSpecWrapper
argument_list|()
decl_stmt|;
if|if
condition|(
name|tensorFlowParameters
operator|.
name|getNumWorkers
argument_list|()
operator|>
literal|0
condition|)
block|{
name|addWorkerComponents
argument_list|(
name|serviceWrapper
argument_list|,
name|Framework
operator|.
name|TENSORFLOW
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|tensorFlowParameters
operator|.
name|getNumPS
argument_list|()
operator|>
literal|0
condition|)
block|{
name|addPsComponent
argument_list|(
name|serviceWrapper
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|tensorFlowParameters
operator|.
name|isTensorboardEnabled
argument_list|()
condition|)
block|{
name|createTensorBoardComponent
argument_list|(
name|serviceWrapper
argument_list|)
expr_stmt|;
block|}
comment|// After all components added, handle quicklinks
name|handleQuicklinks
argument_list|(
name|serviceWrapper
operator|.
name|getService
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|serviceWrapper
return|;
block|}
DECL|method|createTensorBoardComponent (ServiceWrapper serviceWrapper)
specifier|private
name|void
name|createTensorBoardComponent
parameter_list|(
name|ServiceWrapper
name|serviceWrapper
parameter_list|)
throws|throws
name|IOException
block|{
name|TensorBoardComponent
name|tbComponent
init|=
operator|new
name|TensorBoardComponent
argument_list|(
name|fsOperations
argument_list|,
name|remoteDirectoryManager
argument_list|,
name|parameters
argument_list|,
operator|(
name|TensorFlowLaunchCommandFactory
operator|)
name|launchCommandFactory
argument_list|,
name|yarnConfig
argument_list|)
decl_stmt|;
name|serviceWrapper
operator|.
name|addComponent
argument_list|(
name|tbComponent
argument_list|)
expr_stmt|;
name|addQuicklink
argument_list|(
name|serviceWrapper
operator|.
name|getService
argument_list|()
argument_list|,
name|TENSORBOARD_QUICKLINK_LABEL
argument_list|,
name|tbComponent
operator|.
name|getTensorboardLink
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|addPsComponent (ServiceWrapper serviceWrapper)
specifier|private
name|void
name|addPsComponent
parameter_list|(
name|ServiceWrapper
name|serviceWrapper
parameter_list|)
throws|throws
name|IOException
block|{
name|serviceWrapper
operator|.
name|addComponent
argument_list|(
operator|new
name|TensorFlowPsComponent
argument_list|(
name|fsOperations
argument_list|,
name|remoteDirectoryManager
argument_list|,
operator|(
name|TensorFlowLaunchCommandFactory
operator|)
name|launchCommandFactory
argument_list|,
name|parameters
argument_list|,
name|yarnConfig
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

