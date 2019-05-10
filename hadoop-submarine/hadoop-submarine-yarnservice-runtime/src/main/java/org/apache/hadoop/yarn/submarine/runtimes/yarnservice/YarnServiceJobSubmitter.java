begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License. See accompanying LICENSE file.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.submarine.runtimes.yarnservice
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
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
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
name|client
operator|.
name|api
operator|.
name|AppAdminClient
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
name|exceptions
operator|.
name|YarnException
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
name|service
operator|.
name|utils
operator|.
name|ServiceApiUtil
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
name|ParametersHolder
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
name|common
operator|.
name|JobSubmitter
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
name|PyTorchLaunchCommandFactory
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
name|pytorch
operator|.
name|PyTorchServiceSpec
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
name|TensorFlowServiceSpec
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
name|service
operator|.
name|exceptions
operator|.
name|LauncherExitCodes
operator|.
name|EXIT_SUCCESS
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
name|client
operator|.
name|cli
operator|.
name|param
operator|.
name|ParametersHolder
operator|.
name|SUPPORTED_FRAMEWORKS_MESSAGE
import|;
end_import

begin_comment
comment|/**  * Submit a job to cluster.  */
end_comment

begin_class
DECL|class|YarnServiceJobSubmitter
specifier|public
class|class
name|YarnServiceJobSubmitter
implements|implements
name|JobSubmitter
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
name|YarnServiceJobSubmitter
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|clientContext
specifier|private
name|ClientContext
name|clientContext
decl_stmt|;
DECL|field|serviceWrapper
specifier|private
name|ServiceWrapper
name|serviceWrapper
decl_stmt|;
DECL|method|YarnServiceJobSubmitter (ClientContext clientContext)
name|YarnServiceJobSubmitter
parameter_list|(
name|ClientContext
name|clientContext
parameter_list|)
block|{
name|this
operator|.
name|clientContext
operator|=
name|clientContext
expr_stmt|;
block|}
comment|/**    * {@inheritDoc}    */
annotation|@
name|Override
DECL|method|submitJob (ParametersHolder paramsHolder)
specifier|public
name|ApplicationId
name|submitJob
parameter_list|(
name|ParametersHolder
name|paramsHolder
parameter_list|)
throws|throws
name|IOException
throws|,
name|YarnException
block|{
name|Framework
name|framework
init|=
name|paramsHolder
operator|.
name|getFramework
argument_list|()
decl_stmt|;
name|RunJobParameters
name|parameters
init|=
operator|(
name|RunJobParameters
operator|)
name|paramsHolder
operator|.
name|getParameters
argument_list|()
decl_stmt|;
if|if
condition|(
name|framework
operator|==
name|Framework
operator|.
name|TENSORFLOW
condition|)
block|{
return|return
name|submitTensorFlowJob
argument_list|(
operator|(
name|TensorFlowRunJobParameters
operator|)
name|parameters
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|framework
operator|==
name|Framework
operator|.
name|PYTORCH
condition|)
block|{
return|return
name|submitPyTorchJob
argument_list|(
operator|(
name|PyTorchRunJobParameters
operator|)
name|parameters
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|SUPPORTED_FRAMEWORKS_MESSAGE
argument_list|)
throw|;
block|}
block|}
DECL|method|submitTensorFlowJob ( TensorFlowRunJobParameters parameters)
specifier|private
name|ApplicationId
name|submitTensorFlowJob
parameter_list|(
name|TensorFlowRunJobParameters
name|parameters
parameter_list|)
throws|throws
name|IOException
throws|,
name|YarnException
block|{
name|FileSystemOperations
name|fsOperations
init|=
operator|new
name|FileSystemOperations
argument_list|(
name|clientContext
argument_list|)
decl_stmt|;
name|HadoopEnvironmentSetup
name|hadoopEnvSetup
init|=
operator|new
name|HadoopEnvironmentSetup
argument_list|(
name|clientContext
argument_list|,
name|fsOperations
argument_list|)
decl_stmt|;
name|Service
name|serviceSpec
init|=
name|createTensorFlowServiceSpec
argument_list|(
name|parameters
argument_list|,
name|fsOperations
argument_list|,
name|hadoopEnvSetup
argument_list|)
decl_stmt|;
return|return
name|submitJobInternal
argument_list|(
name|serviceSpec
argument_list|)
return|;
block|}
DECL|method|submitPyTorchJob (PyTorchRunJobParameters parameters)
specifier|private
name|ApplicationId
name|submitPyTorchJob
parameter_list|(
name|PyTorchRunJobParameters
name|parameters
parameter_list|)
throws|throws
name|IOException
throws|,
name|YarnException
block|{
name|FileSystemOperations
name|fsOperations
init|=
operator|new
name|FileSystemOperations
argument_list|(
name|clientContext
argument_list|)
decl_stmt|;
name|HadoopEnvironmentSetup
name|hadoopEnvSetup
init|=
operator|new
name|HadoopEnvironmentSetup
argument_list|(
name|clientContext
argument_list|,
name|fsOperations
argument_list|)
decl_stmt|;
name|Service
name|serviceSpec
init|=
name|createPyTorchServiceSpec
argument_list|(
name|parameters
argument_list|,
name|fsOperations
argument_list|,
name|hadoopEnvSetup
argument_list|)
decl_stmt|;
return|return
name|submitJobInternal
argument_list|(
name|serviceSpec
argument_list|)
return|;
block|}
DECL|method|submitJobInternal (Service serviceSpec)
specifier|private
name|ApplicationId
name|submitJobInternal
parameter_list|(
name|Service
name|serviceSpec
parameter_list|)
throws|throws
name|IOException
throws|,
name|YarnException
block|{
name|String
name|serviceSpecFile
init|=
name|ServiceSpecFileGenerator
operator|.
name|generateJson
argument_list|(
name|serviceSpec
argument_list|)
decl_stmt|;
name|AppAdminClient
name|appAdminClient
init|=
name|YarnServiceUtils
operator|.
name|createServiceClient
argument_list|(
name|clientContext
operator|.
name|getYarnConfig
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|code
init|=
name|appAdminClient
operator|.
name|actionLaunch
argument_list|(
name|serviceSpecFile
argument_list|,
name|serviceSpec
operator|.
name|getName
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|code
operator|!=
name|EXIT_SUCCESS
condition|)
block|{
throw|throw
operator|new
name|YarnException
argument_list|(
literal|"Fail to launch application with exit code:"
operator|+
name|code
argument_list|)
throw|;
block|}
name|String
name|appStatus
init|=
name|appAdminClient
operator|.
name|getStatusString
argument_list|(
name|serviceSpec
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|Service
name|app
init|=
name|ServiceApiUtil
operator|.
name|jsonSerDeser
operator|.
name|fromJson
argument_list|(
name|appStatus
argument_list|)
decl_stmt|;
comment|// Retry multiple times if applicationId is null
name|int
name|maxRetryTimes
init|=
literal|30
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|app
operator|.
name|getId
argument_list|()
operator|==
literal|null
operator|&&
name|count
operator|<
name|maxRetryTimes
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Waiting for application Id. AppStatusString=\n {}"
argument_list|,
name|appStatus
argument_list|)
expr_stmt|;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|appStatus
operator|=
name|appAdminClient
operator|.
name|getStatusString
argument_list|(
name|serviceSpec
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|app
operator|=
name|ServiceApiUtil
operator|.
name|jsonSerDeser
operator|.
name|fromJson
argument_list|(
name|appStatus
argument_list|)
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
comment|// Retry timeout
if|if
condition|(
name|app
operator|.
name|getId
argument_list|()
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|YarnException
argument_list|(
literal|"Can't get application id for Service "
operator|+
name|serviceSpec
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
name|ApplicationId
name|appid
init|=
name|ApplicationId
operator|.
name|fromString
argument_list|(
name|app
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
name|appAdminClient
operator|.
name|stop
argument_list|()
expr_stmt|;
return|return
name|appid
return|;
block|}
DECL|method|createTensorFlowServiceSpec ( TensorFlowRunJobParameters parameters, FileSystemOperations fsOperations, HadoopEnvironmentSetup hadoopEnvSetup)
specifier|private
name|Service
name|createTensorFlowServiceSpec
parameter_list|(
name|TensorFlowRunJobParameters
name|parameters
parameter_list|,
name|FileSystemOperations
name|fsOperations
parameter_list|,
name|HadoopEnvironmentSetup
name|hadoopEnvSetup
parameter_list|)
throws|throws
name|IOException
block|{
name|TensorFlowLaunchCommandFactory
name|launchCommandFactory
init|=
operator|new
name|TensorFlowLaunchCommandFactory
argument_list|(
name|hadoopEnvSetup
argument_list|,
name|parameters
argument_list|,
name|clientContext
operator|.
name|getYarnConfig
argument_list|()
argument_list|)
decl_stmt|;
name|Localizer
name|localizer
init|=
operator|new
name|Localizer
argument_list|(
name|fsOperations
argument_list|,
name|clientContext
operator|.
name|getRemoteDirectoryManager
argument_list|()
argument_list|,
name|parameters
argument_list|)
decl_stmt|;
name|TensorFlowServiceSpec
name|tensorFlowServiceSpec
init|=
operator|new
name|TensorFlowServiceSpec
argument_list|(
name|parameters
argument_list|,
name|this
operator|.
name|clientContext
argument_list|,
name|fsOperations
argument_list|,
name|launchCommandFactory
argument_list|,
name|localizer
argument_list|)
decl_stmt|;
name|serviceWrapper
operator|=
name|tensorFlowServiceSpec
operator|.
name|create
argument_list|()
expr_stmt|;
return|return
name|serviceWrapper
operator|.
name|getService
argument_list|()
return|;
block|}
DECL|method|createPyTorchServiceSpec (PyTorchRunJobParameters parameters, FileSystemOperations fsOperations, HadoopEnvironmentSetup hadoopEnvSetup)
specifier|private
name|Service
name|createPyTorchServiceSpec
parameter_list|(
name|PyTorchRunJobParameters
name|parameters
parameter_list|,
name|FileSystemOperations
name|fsOperations
parameter_list|,
name|HadoopEnvironmentSetup
name|hadoopEnvSetup
parameter_list|)
throws|throws
name|IOException
block|{
name|PyTorchLaunchCommandFactory
name|launchCommandFactory
init|=
operator|new
name|PyTorchLaunchCommandFactory
argument_list|(
name|hadoopEnvSetup
argument_list|,
name|parameters
argument_list|,
name|clientContext
operator|.
name|getYarnConfig
argument_list|()
argument_list|)
decl_stmt|;
name|Localizer
name|localizer
init|=
operator|new
name|Localizer
argument_list|(
name|fsOperations
argument_list|,
name|clientContext
operator|.
name|getRemoteDirectoryManager
argument_list|()
argument_list|,
name|parameters
argument_list|)
decl_stmt|;
name|PyTorchServiceSpec
name|pyTorchServiceSpec
init|=
operator|new
name|PyTorchServiceSpec
argument_list|(
name|parameters
argument_list|,
name|this
operator|.
name|clientContext
argument_list|,
name|fsOperations
argument_list|,
name|launchCommandFactory
argument_list|,
name|localizer
argument_list|)
decl_stmt|;
name|serviceWrapper
operator|=
name|pyTorchServiceSpec
operator|.
name|create
argument_list|()
expr_stmt|;
return|return
name|serviceWrapper
operator|.
name|getService
argument_list|()
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getServiceWrapper ()
specifier|public
name|ServiceWrapper
name|getServiceWrapper
parameter_list|()
block|{
return|return
name|serviceWrapper
return|;
block|}
block|}
end_class

end_unit

