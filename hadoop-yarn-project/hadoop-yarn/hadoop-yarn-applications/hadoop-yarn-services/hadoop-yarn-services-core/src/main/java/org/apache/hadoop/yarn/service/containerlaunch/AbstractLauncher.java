begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.service.containerlaunch
package|package
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
name|containerlaunch
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
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
name|security
operator|.
name|UserGroupInformation
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
name|ContainerLaunchContext
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
name|ContainerRetryContext
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
name|ContainerRetryPolicy
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
name|LocalResource
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
name|ServiceContext
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
name|conf
operator|.
name|YarnServiceConstants
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
name|ServiceUtils
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
name|util
operator|.
name|Records
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
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
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
name|HashMap
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
import|;
end_import

begin_comment
comment|/**  * Launcher of applications: base class  */
end_comment

begin_class
DECL|class|AbstractLauncher
specifier|public
class|class
name|AbstractLauncher
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|AbstractLauncher
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|CLASSPATH
specifier|public
specifier|static
specifier|final
name|String
name|CLASSPATH
init|=
literal|"CLASSPATH"
decl_stmt|;
DECL|field|ENV_DOCKER_CONTAINER_MOUNTS
specifier|public
specifier|static
specifier|final
name|String
name|ENV_DOCKER_CONTAINER_MOUNTS
init|=
literal|"YARN_CONTAINER_RUNTIME_DOCKER_MOUNTS"
decl_stmt|;
comment|/**    * Env vars; set up at final launch stage    */
DECL|field|envVars
specifier|protected
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|envVars
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|containerLaunchContext
specifier|protected
specifier|final
name|ContainerLaunchContext
name|containerLaunchContext
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|ContainerLaunchContext
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|commands
specifier|protected
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|commands
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
literal|20
argument_list|)
decl_stmt|;
DECL|field|localResources
specifier|protected
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|LocalResource
argument_list|>
name|localResources
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|mountPaths
specifier|protected
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|mountPaths
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|serviceData
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|ByteBuffer
argument_list|>
name|serviceData
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|yarnDockerMode
specifier|protected
name|boolean
name|yarnDockerMode
init|=
literal|false
decl_stmt|;
DECL|field|dockerImage
specifier|protected
name|String
name|dockerImage
decl_stmt|;
DECL|field|dockerNetwork
specifier|protected
name|String
name|dockerNetwork
decl_stmt|;
DECL|field|dockerHostname
specifier|protected
name|String
name|dockerHostname
decl_stmt|;
DECL|field|runPrivilegedContainer
specifier|protected
name|boolean
name|runPrivilegedContainer
init|=
literal|false
decl_stmt|;
DECL|field|context
specifier|private
name|ServiceContext
name|context
decl_stmt|;
DECL|method|AbstractLauncher (ServiceContext context)
specifier|public
name|AbstractLauncher
parameter_list|(
name|ServiceContext
name|context
parameter_list|)
block|{
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
block|}
DECL|method|setYarnDockerMode (boolean yarnDockerMode)
specifier|public
name|void
name|setYarnDockerMode
parameter_list|(
name|boolean
name|yarnDockerMode
parameter_list|)
block|{
name|this
operator|.
name|yarnDockerMode
operator|=
name|yarnDockerMode
expr_stmt|;
block|}
comment|/**    * Get the env vars to work on    * @return env vars    */
DECL|method|getEnv ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getEnv
parameter_list|()
block|{
return|return
name|envVars
return|;
block|}
comment|/**    * Get the launch commands.    * @return the live list of commands     */
DECL|method|getCommands ()
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getCommands
parameter_list|()
block|{
return|return
name|commands
return|;
block|}
DECL|method|addLocalResource (String subPath, LocalResource resource)
specifier|public
name|void
name|addLocalResource
parameter_list|(
name|String
name|subPath
parameter_list|,
name|LocalResource
name|resource
parameter_list|)
block|{
name|localResources
operator|.
name|put
argument_list|(
name|subPath
argument_list|,
name|resource
argument_list|)
expr_stmt|;
block|}
DECL|method|addLocalResource (String subPath, LocalResource resource, String mountPath)
specifier|public
name|void
name|addLocalResource
parameter_list|(
name|String
name|subPath
parameter_list|,
name|LocalResource
name|resource
parameter_list|,
name|String
name|mountPath
parameter_list|)
block|{
name|localResources
operator|.
name|put
argument_list|(
name|subPath
argument_list|,
name|resource
argument_list|)
expr_stmt|;
name|mountPaths
operator|.
name|put
argument_list|(
name|subPath
argument_list|,
name|mountPath
argument_list|)
expr_stmt|;
block|}
DECL|method|addCommand (String cmd)
specifier|public
name|void
name|addCommand
parameter_list|(
name|String
name|cmd
parameter_list|)
block|{
name|commands
operator|.
name|add
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
block|}
comment|/**    * Complete the launch context (copy in env vars, etc).    * @return the container to launch    */
DECL|method|completeContainerLaunch ()
specifier|public
name|ContainerLaunchContext
name|completeContainerLaunch
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|cmdStr
init|=
name|ServiceUtils
operator|.
name|join
argument_list|(
name|commands
argument_list|,
literal|" "
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"Completed setting up container command {}"
argument_list|,
name|cmdStr
argument_list|)
expr_stmt|;
name|containerLaunchContext
operator|.
name|setCommands
argument_list|(
name|commands
argument_list|)
expr_stmt|;
comment|//env variables
if|if
condition|(
name|log
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Environment variables"
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
name|String
argument_list|>
name|envPair
range|:
name|envVars
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"    \"{}\"=\"{}\""
argument_list|,
name|envPair
operator|.
name|getKey
argument_list|()
argument_list|,
name|envPair
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|containerLaunchContext
operator|.
name|setEnvironment
argument_list|(
name|envVars
argument_list|)
expr_stmt|;
comment|//service data
if|if
condition|(
name|log
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Service Data size"
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
name|ByteBuffer
argument_list|>
name|entry
range|:
name|serviceData
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"\"{}\"=> {} bytes of data"
argument_list|,
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|array
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
block|}
name|containerLaunchContext
operator|.
name|setServiceData
argument_list|(
name|serviceData
argument_list|)
expr_stmt|;
comment|// resources
name|dumpLocalResources
argument_list|()
expr_stmt|;
name|containerLaunchContext
operator|.
name|setLocalResources
argument_list|(
name|localResources
argument_list|)
expr_stmt|;
comment|//tokens
if|if
condition|(
name|context
operator|.
name|tokens
operator|!=
literal|null
condition|)
block|{
name|containerLaunchContext
operator|.
name|setTokens
argument_list|(
name|context
operator|.
name|tokens
operator|.
name|duplicate
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|yarnDockerMode
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|env
init|=
name|containerLaunchContext
operator|.
name|getEnvironment
argument_list|()
decl_stmt|;
name|env
operator|.
name|put
argument_list|(
literal|"YARN_CONTAINER_RUNTIME_TYPE"
argument_list|,
literal|"docker"
argument_list|)
expr_stmt|;
name|env
operator|.
name|put
argument_list|(
literal|"YARN_CONTAINER_RUNTIME_DOCKER_IMAGE"
argument_list|,
name|dockerImage
argument_list|)
expr_stmt|;
if|if
condition|(
name|ServiceUtils
operator|.
name|isSet
argument_list|(
name|dockerNetwork
argument_list|)
condition|)
block|{
name|env
operator|.
name|put
argument_list|(
literal|"YARN_CONTAINER_RUNTIME_DOCKER_CONTAINER_NETWORK"
argument_list|,
name|dockerNetwork
argument_list|)
expr_stmt|;
block|}
name|env
operator|.
name|put
argument_list|(
literal|"YARN_CONTAINER_RUNTIME_DOCKER_CONTAINER_HOSTNAME"
argument_list|,
name|dockerHostname
argument_list|)
expr_stmt|;
if|if
condition|(
name|runPrivilegedContainer
condition|)
block|{
name|env
operator|.
name|put
argument_list|(
literal|"YARN_CONTAINER_RUNTIME_DOCKER_RUN_PRIVILEGED_CONTAINER"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|mountPaths
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|env
operator|.
name|get
argument_list|(
name|ENV_DOCKER_CONTAINER_MOUNTS
argument_list|)
operator|!=
literal|null
condition|)
block|{
comment|// user specified mounts in the spec
name|sb
operator|.
name|append
argument_list|(
name|env
operator|.
name|get
argument_list|(
name|ENV_DOCKER_CONTAINER_MOUNTS
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|mount
range|:
name|mountPaths
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|sb
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|","
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|mount
operator|.
name|getKey
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|":"
argument_list|)
operator|.
name|append
argument_list|(
name|mount
operator|.
name|getValue
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|":ro"
argument_list|)
expr_stmt|;
block|}
name|env
operator|.
name|put
argument_list|(
name|ENV_DOCKER_CONTAINER_MOUNTS
argument_list|,
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|log
operator|.
name|info
argument_list|(
literal|"yarn docker env var has been set {}"
argument_list|,
name|containerLaunchContext
operator|.
name|getEnvironment
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|containerLaunchContext
return|;
block|}
DECL|method|setRetryContext (int maxRetries, int retryInterval, long failuresValidityInterval)
specifier|public
name|void
name|setRetryContext
parameter_list|(
name|int
name|maxRetries
parameter_list|,
name|int
name|retryInterval
parameter_list|,
name|long
name|failuresValidityInterval
parameter_list|)
block|{
name|ContainerRetryContext
name|retryContext
init|=
name|ContainerRetryContext
operator|.
name|newInstance
argument_list|(
name|ContainerRetryPolicy
operator|.
name|RETRY_ON_ALL_ERRORS
argument_list|,
literal|null
argument_list|,
name|maxRetries
argument_list|,
name|retryInterval
argument_list|,
name|failuresValidityInterval
argument_list|)
decl_stmt|;
name|containerLaunchContext
operator|.
name|setContainerRetryContext
argument_list|(
name|retryContext
argument_list|)
expr_stmt|;
block|}
comment|/**    * Dump local resources at debug level    */
DECL|method|dumpLocalResources ()
specifier|private
name|void
name|dumpLocalResources
parameter_list|()
block|{
if|if
condition|(
name|log
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"{} resources: "
argument_list|,
name|localResources
operator|.
name|size
argument_list|()
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
name|LocalResource
argument_list|>
name|entry
range|:
name|localResources
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|key
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|LocalResource
name|val
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"{} = {}"
argument_list|,
name|key
argument_list|,
name|ServiceUtils
operator|.
name|stringify
argument_list|(
name|val
operator|.
name|getResource
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * This is critical for an insecure cluster -it passes    * down the username to YARN, and so gives the code running    * in containers the rights it needs to work with    * data.    * @throws IOException problems working with current user    */
DECL|method|propagateUsernameInInsecureCluster ()
specifier|protected
name|void
name|propagateUsernameInInsecureCluster
parameter_list|()
throws|throws
name|IOException
block|{
comment|//insecure cluster: propagate user name via env variable
name|String
name|userName
init|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|getUserName
argument_list|()
decl_stmt|;
name|envVars
operator|.
name|put
argument_list|(
name|YarnServiceConstants
operator|.
name|HADOOP_USER_NAME
argument_list|,
name|userName
argument_list|)
expr_stmt|;
block|}
comment|/**    * Utility method to set up the classpath    * @param classpath classpath to use    */
DECL|method|setClasspath (ClasspathConstructor classpath)
specifier|public
name|void
name|setClasspath
parameter_list|(
name|ClasspathConstructor
name|classpath
parameter_list|)
block|{
name|setEnv
argument_list|(
name|CLASSPATH
argument_list|,
name|classpath
operator|.
name|buildClasspath
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Set an environment variable in the launch context    * @param var variable name    * @param value value (must be non null)    */
DECL|method|setEnv (String var, String value)
specifier|public
name|void
name|setEnv
parameter_list|(
name|String
name|var
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|var
operator|!=
literal|null
argument_list|,
literal|"null variable name"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|value
operator|!=
literal|null
argument_list|,
literal|"null value"
argument_list|)
expr_stmt|;
name|envVars
operator|.
name|put
argument_list|(
name|var
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
DECL|method|putEnv (Map<String, String> map)
specifier|public
name|void
name|putEnv
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
parameter_list|)
block|{
name|envVars
operator|.
name|putAll
argument_list|(
name|map
argument_list|)
expr_stmt|;
block|}
DECL|method|setDockerImage (String dockerImage)
specifier|public
name|void
name|setDockerImage
parameter_list|(
name|String
name|dockerImage
parameter_list|)
block|{
name|this
operator|.
name|dockerImage
operator|=
name|dockerImage
expr_stmt|;
block|}
DECL|method|setDockerNetwork (String dockerNetwork)
specifier|public
name|void
name|setDockerNetwork
parameter_list|(
name|String
name|dockerNetwork
parameter_list|)
block|{
name|this
operator|.
name|dockerNetwork
operator|=
name|dockerNetwork
expr_stmt|;
block|}
DECL|method|setDockerHostname (String dockerHostname)
specifier|public
name|void
name|setDockerHostname
parameter_list|(
name|String
name|dockerHostname
parameter_list|)
block|{
name|this
operator|.
name|dockerHostname
operator|=
name|dockerHostname
expr_stmt|;
block|}
DECL|method|setRunPrivilegedContainer (boolean runPrivilegedContainer)
specifier|public
name|void
name|setRunPrivilegedContainer
parameter_list|(
name|boolean
name|runPrivilegedContainer
parameter_list|)
block|{
name|this
operator|.
name|runPrivilegedContainer
operator|=
name|runPrivilegedContainer
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getDockerImage ()
specifier|public
name|String
name|getDockerImage
parameter_list|()
block|{
return|return
name|dockerImage
return|;
block|}
block|}
end_class

end_unit

