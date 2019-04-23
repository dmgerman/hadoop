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
name|TonyConfigurationKeys
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
name|util
operator|.
name|Utils
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
name|ResourceInformation
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
name|ResourceNotFoundException
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
name|Arrays
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

begin_comment
comment|/**  * Utilities for Tony Runtime.  */
end_comment

begin_class
DECL|class|TonyUtils
specifier|public
specifier|final
class|class
name|TonyUtils
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
name|TonyUtils
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|tonyConfFromClientContext ( RunJobParameters parameters)
specifier|public
specifier|static
name|Configuration
name|tonyConfFromClientContext
parameter_list|(
name|RunJobParameters
name|parameters
parameter_list|)
block|{
name|Configuration
name|tonyConf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|tonyConf
operator|.
name|setInt
argument_list|(
name|TonyConfigurationKeys
operator|.
name|getInstancesKey
argument_list|(
name|Constants
operator|.
name|WORKER_JOB_NAME
argument_list|)
argument_list|,
name|parameters
operator|.
name|getNumWorkers
argument_list|()
argument_list|)
expr_stmt|;
name|tonyConf
operator|.
name|setInt
argument_list|(
name|TonyConfigurationKeys
operator|.
name|getInstancesKey
argument_list|(
name|Constants
operator|.
name|PS_JOB_NAME
argument_list|)
argument_list|,
name|parameters
operator|.
name|getNumPS
argument_list|()
argument_list|)
expr_stmt|;
comment|// Resources for PS& Worker
if|if
condition|(
name|parameters
operator|.
name|getPsResource
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|tonyConf
operator|.
name|setInt
argument_list|(
name|TonyConfigurationKeys
operator|.
name|getResourceKey
argument_list|(
name|Constants
operator|.
name|PS_JOB_NAME
argument_list|,
name|Constants
operator|.
name|VCORES
argument_list|)
argument_list|,
name|parameters
operator|.
name|getPsResource
argument_list|()
operator|.
name|getVirtualCores
argument_list|()
argument_list|)
expr_stmt|;
name|tonyConf
operator|.
name|setLong
argument_list|(
name|TonyConfigurationKeys
operator|.
name|getResourceKey
argument_list|(
name|Constants
operator|.
name|PS_JOB_NAME
argument_list|,
name|Constants
operator|.
name|MEMORY
argument_list|)
argument_list|,
name|parameters
operator|.
name|getPsResource
argument_list|()
operator|.
name|getMemorySize
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|parameters
operator|.
name|getWorkerResource
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|tonyConf
operator|.
name|setInt
argument_list|(
name|TonyConfigurationKeys
operator|.
name|getResourceKey
argument_list|(
name|Constants
operator|.
name|WORKER_JOB_NAME
argument_list|,
name|Constants
operator|.
name|VCORES
argument_list|)
argument_list|,
name|parameters
operator|.
name|getWorkerResource
argument_list|()
operator|.
name|getVirtualCores
argument_list|()
argument_list|)
expr_stmt|;
name|tonyConf
operator|.
name|setLong
argument_list|(
name|TonyConfigurationKeys
operator|.
name|getResourceKey
argument_list|(
name|Constants
operator|.
name|WORKER_JOB_NAME
argument_list|,
name|Constants
operator|.
name|MEMORY
argument_list|)
argument_list|,
name|parameters
operator|.
name|getWorkerResource
argument_list|()
operator|.
name|getMemorySize
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|tonyConf
operator|.
name|setLong
argument_list|(
name|TonyConfigurationKeys
operator|.
name|getResourceKey
argument_list|(
name|Constants
operator|.
name|WORKER_JOB_NAME
argument_list|,
name|Constants
operator|.
name|GPUS
argument_list|)
argument_list|,
name|parameters
operator|.
name|getWorkerResource
argument_list|()
operator|.
name|getResourceValue
argument_list|(
name|ResourceInformation
operator|.
name|GPU_URI
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ResourceNotFoundException
name|rnfe
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"GPU resources not enabled."
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|parameters
operator|.
name|getQueue
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|tonyConf
operator|.
name|set
argument_list|(
name|TonyConfigurationKeys
operator|.
name|YARN_QUEUE_NAME
argument_list|,
name|parameters
operator|.
name|getQueue
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Set up Docker for PS& Worker
if|if
condition|(
name|parameters
operator|.
name|getDockerImageName
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|tonyConf
operator|.
name|set
argument_list|(
name|TonyConfigurationKeys
operator|.
name|getContainerDockerKey
argument_list|()
argument_list|,
name|parameters
operator|.
name|getDockerImageName
argument_list|()
argument_list|)
expr_stmt|;
name|tonyConf
operator|.
name|setBoolean
argument_list|(
name|TonyConfigurationKeys
operator|.
name|DOCKER_ENABLED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|parameters
operator|.
name|getWorkerDockerImage
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|tonyConf
operator|.
name|set
argument_list|(
name|TonyConfigurationKeys
operator|.
name|getDockerImageKey
argument_list|(
name|Constants
operator|.
name|WORKER_JOB_NAME
argument_list|)
argument_list|,
name|parameters
operator|.
name|getWorkerDockerImage
argument_list|()
argument_list|)
expr_stmt|;
name|tonyConf
operator|.
name|setBoolean
argument_list|(
name|TonyConfigurationKeys
operator|.
name|DOCKER_ENABLED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|parameters
operator|.
name|getPsDockerImage
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|tonyConf
operator|.
name|set
argument_list|(
name|TonyConfigurationKeys
operator|.
name|getDockerImageKey
argument_list|(
name|Constants
operator|.
name|PS_JOB_NAME
argument_list|)
argument_list|,
name|parameters
operator|.
name|getPsDockerImage
argument_list|()
argument_list|)
expr_stmt|;
name|tonyConf
operator|.
name|setBoolean
argument_list|(
name|TonyConfigurationKeys
operator|.
name|DOCKER_ENABLED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|// Set up container environment
name|List
argument_list|<
name|String
argument_list|>
name|envs
init|=
name|parameters
operator|.
name|getEnvars
argument_list|()
decl_stmt|;
name|tonyConf
operator|.
name|setStrings
argument_list|(
name|TonyConfigurationKeys
operator|.
name|CONTAINER_LAUNCH_ENV
argument_list|,
name|envs
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|tonyConf
operator|.
name|setStrings
argument_list|(
name|TonyConfigurationKeys
operator|.
name|EXECUTION_ENV
argument_list|,
name|envs
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|env
lambda|->
name|env
operator|.
name|replaceAll
argument_list|(
literal|"DOCKER_"
argument_list|,
literal|""
argument_list|)
argument_list|)
operator|.
name|toArray
argument_list|(
name|String
index|[]
operator|::
operator|new
argument_list|)
argument_list|)
expr_stmt|;
name|tonyConf
operator|.
name|setStrings
argument_list|(
name|TonyConfigurationKeys
operator|.
name|CONTAINER_LAUNCH_ENV
argument_list|,
name|envs
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|env
lambda|->
name|env
operator|.
name|replaceAll
argument_list|(
literal|"DOCKER_"
argument_list|,
literal|""
argument_list|)
argument_list|)
operator|.
name|toArray
argument_list|(
name|String
index|[]
operator|::
operator|new
argument_list|)
argument_list|)
expr_stmt|;
comment|// Set up running command
if|if
condition|(
name|parameters
operator|.
name|getWorkerLaunchCmd
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|tonyConf
operator|.
name|set
argument_list|(
name|TonyConfigurationKeys
operator|.
name|getExecuteCommandKey
argument_list|(
name|Constants
operator|.
name|WORKER_JOB_NAME
argument_list|)
argument_list|,
name|parameters
operator|.
name|getWorkerLaunchCmd
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|parameters
operator|.
name|getPSLaunchCmd
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|tonyConf
operator|.
name|set
argument_list|(
name|TonyConfigurationKeys
operator|.
name|getExecuteCommandKey
argument_list|(
name|Constants
operator|.
name|PS_JOB_NAME
argument_list|)
argument_list|,
name|parameters
operator|.
name|getPSLaunchCmd
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|tonyConf
operator|.
name|setBoolean
argument_list|(
name|TonyConfigurationKeys
operator|.
name|SECURITY_ENABLED
argument_list|,
operator|!
name|parameters
operator|.
name|isSecurityDisabled
argument_list|()
argument_list|)
expr_stmt|;
comment|// Set up container resources
if|if
condition|(
name|parameters
operator|.
name|getLocalizations
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|tonyConf
operator|.
name|setStrings
argument_list|(
name|TonyConfigurationKeys
operator|.
name|getContainerResourcesKey
argument_list|()
argument_list|,
name|parameters
operator|.
name|getLocalizations
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|lo
lambda|->
name|lo
operator|.
name|getRemoteUri
argument_list|()
operator|+
name|Constants
operator|.
name|RESOURCE_DIVIDER
operator|+
name|lo
operator|.
name|getLocalPath
argument_list|()
argument_list|)
operator|.
name|toArray
argument_list|(
name|String
index|[]
operator|::
operator|new
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|parameters
operator|.
name|getConfPairs
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|String
index|[]
name|confArray
init|=
name|parameters
operator|.
name|getConfPairs
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
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
name|String
argument_list|>
name|cliConf
range|:
name|Utils
operator|.
name|parseKeyValue
argument_list|(
name|confArray
argument_list|)
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
index|[]
name|existingValue
init|=
name|tonyConf
operator|.
name|getStrings
argument_list|(
name|cliConf
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|existingValue
operator|!=
literal|null
operator|&&
name|TonyConfigurationKeys
operator|.
name|MULTI_VALUE_CONF
operator|.
name|contains
argument_list|(
name|cliConf
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
block|{
name|ArrayList
argument_list|<
name|String
argument_list|>
name|newValues
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|existingValue
argument_list|)
argument_list|)
decl_stmt|;
name|newValues
operator|.
name|add
argument_list|(
name|cliConf
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|tonyConf
operator|.
name|setStrings
argument_list|(
name|cliConf
operator|.
name|getKey
argument_list|()
argument_list|,
name|newValues
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|tonyConf
operator|.
name|set
argument_list|(
name|cliConf
operator|.
name|getKey
argument_list|()
argument_list|,
name|cliConf
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Resources: "
operator|+
name|tonyConf
operator|.
name|get
argument_list|(
name|TonyConfigurationKeys
operator|.
name|getContainerResourcesKey
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|tonyConf
return|;
block|}
DECL|method|TonyUtils ()
specifier|private
name|TonyUtils
parameter_list|()
block|{   }
block|}
end_class

end_unit

