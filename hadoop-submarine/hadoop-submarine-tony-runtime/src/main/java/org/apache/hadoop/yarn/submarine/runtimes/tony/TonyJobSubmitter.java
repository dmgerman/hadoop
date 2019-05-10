begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License. See accompanying LICENSE file.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.submarine.runtimes.tony
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
name|tony
package|;
end_package

begin_import
import|import
name|com
operator|.
name|linkedin
operator|.
name|tony
operator|.
name|Constants
import|;
end_import

begin_import
import|import
name|com
operator|.
name|linkedin
operator|.
name|tony
operator|.
name|TonyClient
import|;
end_import

begin_import
import|import
name|com
operator|.
name|linkedin
operator|.
name|tony
operator|.
name|client
operator|.
name|CallbackHandler
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
name|logging
operator|.
name|Log
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
name|logging
operator|.
name|LogFactory
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
name|runtimes
operator|.
name|common
operator|.
name|JobSubmitter
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
name|OutputStream
import|;
end_import

begin_comment
comment|/**  * Implementation of JobSumitter with TonY runtime.  */
end_comment

begin_class
DECL|class|TonyJobSubmitter
specifier|public
class|class
name|TonyJobSubmitter
implements|implements
name|JobSubmitter
implements|,
name|CallbackHandler
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TonyJobSubmitter
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|applicationId
specifier|private
name|ApplicationId
name|applicationId
decl_stmt|;
DECL|field|tonyClient
specifier|private
name|TonyClient
name|tonyClient
decl_stmt|;
DECL|method|TonyJobSubmitter ()
specifier|public
name|TonyJobSubmitter
parameter_list|()
block|{ }
DECL|method|setTonyClient (TonyClient client)
specifier|public
name|void
name|setTonyClient
parameter_list|(
name|TonyClient
name|client
parameter_list|)
block|{
name|this
operator|.
name|tonyClient
operator|=
name|client
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|submitJob (ParametersHolder parameters)
specifier|public
name|ApplicationId
name|submitJob
parameter_list|(
name|ParametersHolder
name|parameters
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|parameters
operator|.
name|getFramework
argument_list|()
operator|==
name|Framework
operator|.
name|PYTORCH
condition|)
block|{
comment|// we need to throw an exception, as ParametersHolder's parameters field
comment|// could not be casted to TensorFlowRunJobParameters.
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Support \"â-framework\" option for PyTorch in Tony is coming. "
operator|+
literal|"Please check the documentation about how to submit a "
operator|+
literal|"PyTorch job with TonY runtime."
argument_list|)
throw|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Starting Tony runtime.."
argument_list|)
expr_stmt|;
name|File
name|tonyFinalConfPath
init|=
name|File
operator|.
name|createTempFile
argument_list|(
literal|"temp"
argument_list|,
name|Constants
operator|.
name|TONY_FINAL_XML
argument_list|)
decl_stmt|;
comment|// Write user's overridden conf to an xml to be localized.
name|Configuration
name|tonyConf
init|=
name|TonyUtils
operator|.
name|tonyConfFromClientContext
argument_list|(
operator|(
name|TensorFlowRunJobParameters
operator|)
name|parameters
operator|.
name|getParameters
argument_list|()
argument_list|)
decl_stmt|;
try|try
init|(
name|OutputStream
name|os
init|=
operator|new
name|FileOutputStream
argument_list|(
name|tonyFinalConfPath
argument_list|)
init|)
block|{
name|tonyConf
operator|.
name|writeXml
argument_list|(
name|os
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Failed to create "
operator|+
name|tonyFinalConfPath
operator|+
literal|" conf file. Exiting."
argument_list|,
name|e
argument_list|)
throw|;
block|}
try|try
block|{
name|tonyClient
operator|.
name|init
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"--conf_file"
block|,
name|tonyFinalConfPath
operator|.
name|getAbsolutePath
argument_list|()
block|}
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to init TonyClient: "
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|Thread
name|clientThread
init|=
operator|new
name|Thread
argument_list|(
name|tonyClient
operator|::
name|start
argument_list|)
decl_stmt|;
name|java
operator|.
name|lang
operator|.
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|addShutdownHook
argument_list|(
operator|new
name|Thread
argument_list|(
parameter_list|()
lambda|->
block|{
try|try
block|{
name|tonyClient
operator|.
name|forceKillApplication
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnException
decl||
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to kill application during shutdown."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|clientThread
operator|.
name|start
argument_list|()
expr_stmt|;
while|while
condition|(
name|clientThread
operator|.
name|isAlive
argument_list|()
condition|)
block|{
if|if
condition|(
name|applicationId
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"TonyClient returned applicationId: "
operator|+
name|applicationId
argument_list|)
expr_stmt|;
return|return
name|applicationId
return|;
block|}
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|onApplicationIdReceived (ApplicationId appId)
specifier|public
name|void
name|onApplicationIdReceived
parameter_list|(
name|ApplicationId
name|appId
parameter_list|)
block|{
name|applicationId
operator|=
name|appId
expr_stmt|;
block|}
block|}
end_class

end_unit

