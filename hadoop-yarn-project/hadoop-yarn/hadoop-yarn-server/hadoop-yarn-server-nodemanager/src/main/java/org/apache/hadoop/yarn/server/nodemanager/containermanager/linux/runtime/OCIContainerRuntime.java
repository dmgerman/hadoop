begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  *  Licensed to the Apache Software Foundation (ASF) under one  *  or more contributor license agreements.  See the NOTICE file  *  distributed with this work for additional information  *  regarding copyright ownership.  The ASF licenses this file  *  to you under the Apache License, Version 2.0 (the  *  "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
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
name|security
operator|.
name|authorize
operator|.
name|AccessControlList
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
name|Shell
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
name|CsiAdaptorProtocol
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
name|impl
operator|.
name|pb
operator|.
name|client
operator|.
name|CsiAdaptorProtocolPBClientImpl
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
name|linux
operator|.
name|privileged
operator|.
name|PrivilegedOperationExecutor
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
name|linux
operator|.
name|resources
operator|.
name|CGroupsHandler
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
name|linux
operator|.
name|resources
operator|.
name|ResourceHandlerModule
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
name|ContainerRuntime
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
name|csi
operator|.
name|CsiConfigUtils
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
name|net
operator|.
name|InetSocketAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Paths
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
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
name|DockerLinuxContainerRuntime
operator|.
name|isDockerContainerRequested
import|;
end_import

begin_comment
comment|/**  *<p>This class is a {@link ContainerRuntime} implementation that uses the  * native {@code container-executor} binary via a  * {@link PrivilegedOperationExecutor} instance to launch processes inside  * OCI-compliant containers.</p>  *  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|OCIContainerRuntime
specifier|public
specifier|abstract
class|class
name|OCIContainerRuntime
implements|implements
name|LinuxContainerRuntime
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
name|OCIContainerRuntime
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|HOSTNAME_PATTERN
specifier|private
specifier|static
specifier|final
name|Pattern
name|HOSTNAME_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^[a-zA-Z0-9][a-zA-Z0-9_.-]+$"
argument_list|)
decl_stmt|;
DECL|field|USER_MOUNT_PATTERN
specifier|static
specifier|final
name|Pattern
name|USER_MOUNT_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"(?<=^|,)([^:\\x00]+):([^:\\x00]+)"
operator|+
literal|"(:(r[ow]|(r[ow][+])?(r?shared|r?slave|r?private)))?(?:,|$)"
argument_list|)
decl_stmt|;
DECL|field|TMPFS_MOUNT_PATTERN
specifier|static
specifier|final
name|Pattern
name|TMPFS_MOUNT_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^/[^:\\x00]+$"
argument_list|)
decl_stmt|;
DECL|field|PORTS_MAPPING_PATTERN
specifier|static
specifier|final
name|String
name|PORTS_MAPPING_PATTERN
init|=
literal|"^:[0-9]+|^[0-9]+:[0-9]+|^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]"
operator|+
literal|"|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])"
operator|+
literal|":[0-9]+:[0-9]+$"
decl_stmt|;
DECL|field|HOST_NAME_LENGTH
specifier|private
specifier|static
specifier|final
name|int
name|HOST_NAME_LENGTH
init|=
literal|64
decl_stmt|;
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|field|RUNTIME_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|RUNTIME_PREFIX
init|=
literal|"YARN_CONTAINER_RUNTIME_%s_%s"
decl_stmt|;
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|field|CONTAINER_PID_NAMESPACE_SUFFIX
specifier|public
specifier|static
specifier|final
name|String
name|CONTAINER_PID_NAMESPACE_SUFFIX
init|=
literal|"CONTAINER_PID_NAMESPACE"
decl_stmt|;
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|field|RUN_PRIVILEGED_CONTAINER_SUFFIX
specifier|public
specifier|static
specifier|final
name|String
name|RUN_PRIVILEGED_CONTAINER_SUFFIX
init|=
literal|"RUN_PRIVILEGED_CONTAINER"
decl_stmt|;
DECL|field|csiClients
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|CsiAdaptorProtocol
argument_list|>
name|csiClients
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|getAllowedNetworks ()
specifier|abstract
name|Set
argument_list|<
name|String
argument_list|>
name|getAllowedNetworks
parameter_list|()
function_decl|;
DECL|method|getAllowedRuntimes ()
specifier|abstract
name|Set
argument_list|<
name|String
argument_list|>
name|getAllowedRuntimes
parameter_list|()
function_decl|;
DECL|method|getHostPidNamespaceEnabled ()
specifier|abstract
name|boolean
name|getHostPidNamespaceEnabled
parameter_list|()
function_decl|;
DECL|method|getPrivilegedContainersEnabledOnCluster ()
specifier|abstract
name|boolean
name|getPrivilegedContainersEnabledOnCluster
parameter_list|()
function_decl|;
DECL|method|getPrivilegedContainersAcl ()
specifier|abstract
name|AccessControlList
name|getPrivilegedContainersAcl
parameter_list|()
function_decl|;
DECL|method|getEnvOciContainerPidNamespace ()
specifier|abstract
name|String
name|getEnvOciContainerPidNamespace
parameter_list|()
function_decl|;
DECL|method|getEnvOciContainerRunPrivilegedContainer ()
specifier|abstract
name|String
name|getEnvOciContainerRunPrivilegedContainer
parameter_list|()
function_decl|;
DECL|method|OCIContainerRuntime (PrivilegedOperationExecutor privilegedOperationExecutor)
specifier|public
name|OCIContainerRuntime
parameter_list|(
name|PrivilegedOperationExecutor
name|privilegedOperationExecutor
parameter_list|)
block|{
name|this
argument_list|(
name|privilegedOperationExecutor
argument_list|,
name|ResourceHandlerModule
operator|.
name|getCGroupsHandler
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|OCIContainerRuntime (PrivilegedOperationExecutor privilegedOperationExecutor, CGroupsHandler cGroupsHandler)
specifier|public
name|OCIContainerRuntime
parameter_list|(
name|PrivilegedOperationExecutor
name|privilegedOperationExecutor
parameter_list|,
name|CGroupsHandler
name|cGroupsHandler
parameter_list|)
block|{   }
DECL|method|initialize (Configuration conf, Context nmContext)
specifier|public
name|void
name|initialize
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|Context
name|nmContext
parameter_list|)
throws|throws
name|ContainerExecutionException
block|{    }
DECL|method|isOCICompliantContainerRequested ( Configuration daemonConf, Map<String, String> env)
specifier|public
specifier|static
name|boolean
name|isOCICompliantContainerRequested
parameter_list|(
name|Configuration
name|daemonConf
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|env
parameter_list|)
block|{
return|return
name|isDockerContainerRequested
argument_list|(
name|daemonConf
argument_list|,
name|env
argument_list|)
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|mountReadOnlyPath (String mount, Map<Path, List<String>> localizedResources)
specifier|protected
name|String
name|mountReadOnlyPath
parameter_list|(
name|String
name|mount
parameter_list|,
name|Map
argument_list|<
name|Path
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|localizedResources
parameter_list|)
throws|throws
name|ContainerExecutionException
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Path
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|resource
range|:
name|localizedResources
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|resource
operator|.
name|getValue
argument_list|()
operator|.
name|contains
argument_list|(
name|mount
argument_list|)
condition|)
block|{
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
name|path
init|=
name|Paths
operator|.
name|get
argument_list|(
name|resource
operator|.
name|getKey
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|path
operator|.
name|isAbsolute
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ContainerExecutionException
argument_list|(
literal|"Mount must be absolute: "
operator|+
name|mount
argument_list|)
throw|;
block|}
if|if
condition|(
name|Files
operator|.
name|isSymbolicLink
argument_list|(
name|path
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|ContainerExecutionException
argument_list|(
literal|"Mount cannot be a symlink: "
operator|+
name|mount
argument_list|)
throw|;
block|}
return|return
name|path
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
throw|throw
operator|new
name|ContainerExecutionException
argument_list|(
literal|"Mount must be a localized "
operator|+
literal|"resource: "
operator|+
name|mount
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|prepareContainer (ContainerRuntimeContext ctx)
specifier|public
name|void
name|prepareContainer
parameter_list|(
name|ContainerRuntimeContext
name|ctx
parameter_list|)
throws|throws
name|ContainerExecutionException
block|{   }
DECL|method|getUserIdInfo (String userName)
specifier|protected
name|String
name|getUserIdInfo
parameter_list|(
name|String
name|userName
parameter_list|)
throws|throws
name|ContainerExecutionException
block|{
name|String
name|id
decl_stmt|;
name|Shell
operator|.
name|ShellCommandExecutor
name|shexec
init|=
operator|new
name|Shell
operator|.
name|ShellCommandExecutor
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"id"
block|,
literal|"-u"
block|,
name|userName
block|}
argument_list|)
decl_stmt|;
try|try
block|{
name|shexec
operator|.
name|execute
argument_list|()
expr_stmt|;
name|id
operator|=
name|shexec
operator|.
name|getOutput
argument_list|()
operator|.
name|replaceAll
argument_list|(
literal|"[^0-9]"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ContainerExecutionException
argument_list|(
name|e
argument_list|)
throw|;
block|}
return|return
name|id
return|;
block|}
DECL|method|getGroupIdInfo (String userName)
specifier|protected
name|String
index|[]
name|getGroupIdInfo
parameter_list|(
name|String
name|userName
parameter_list|)
throws|throws
name|ContainerExecutionException
block|{
name|String
index|[]
name|id
decl_stmt|;
name|Shell
operator|.
name|ShellCommandExecutor
name|shexec
init|=
operator|new
name|Shell
operator|.
name|ShellCommandExecutor
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"id"
block|,
literal|"-G"
block|,
name|userName
block|}
argument_list|)
decl_stmt|;
try|try
block|{
name|shexec
operator|.
name|execute
argument_list|()
expr_stmt|;
name|id
operator|=
name|shexec
operator|.
name|getOutput
argument_list|()
operator|.
name|replace
argument_list|(
literal|"\n"
argument_list|,
literal|""
argument_list|)
operator|.
name|split
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ContainerExecutionException
argument_list|(
name|e
argument_list|)
throw|;
block|}
return|return
name|id
return|;
block|}
DECL|method|validateContainerNetworkType (String network)
specifier|protected
name|void
name|validateContainerNetworkType
parameter_list|(
name|String
name|network
parameter_list|)
throws|throws
name|ContainerExecutionException
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|allowedNetworks
init|=
name|getAllowedNetworks
argument_list|()
decl_stmt|;
if|if
condition|(
name|allowedNetworks
operator|.
name|contains
argument_list|(
name|network
argument_list|)
condition|)
block|{
return|return;
block|}
name|String
name|msg
init|=
literal|"Disallowed network:  '"
operator|+
name|network
operator|+
literal|"' specified. Allowed networks: are "
operator|+
name|allowedNetworks
operator|.
name|toString
argument_list|()
decl_stmt|;
throw|throw
operator|new
name|ContainerExecutionException
argument_list|(
name|msg
argument_list|)
throw|;
block|}
DECL|method|validateContainerRuntimeType (String runtime)
specifier|protected
name|void
name|validateContainerRuntimeType
parameter_list|(
name|String
name|runtime
parameter_list|)
throws|throws
name|ContainerExecutionException
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|allowedRuntimes
init|=
name|getAllowedRuntimes
argument_list|()
decl_stmt|;
if|if
condition|(
name|runtime
operator|==
literal|null
operator|||
name|runtime
operator|.
name|isEmpty
argument_list|()
operator|||
name|allowedRuntimes
operator|.
name|contains
argument_list|(
name|runtime
argument_list|)
condition|)
block|{
return|return;
block|}
name|String
name|msg
init|=
literal|"Disallowed runtime:  '"
operator|+
name|runtime
operator|+
literal|"' specified. Allowed runtimes: are "
operator|+
name|allowedRuntimes
operator|.
name|toString
argument_list|()
decl_stmt|;
throw|throw
operator|new
name|ContainerExecutionException
argument_list|(
name|msg
argument_list|)
throw|;
block|}
comment|/**    * Return whether the YARN container is allowed to run using the host's PID    * namespace for the OCI-compliant container. For this to be allowed, the    * submitting user must request the feature and the feature must be enabled    * on the cluster.    *    * @param container the target YARN container    * @return whether host pid namespace is requested and allowed    * @throws ContainerExecutionException if host pid namespace is requested    * but is not allowed    */
DECL|method|allowHostPidNamespace (Container container)
specifier|protected
name|boolean
name|allowHostPidNamespace
parameter_list|(
name|Container
name|container
parameter_list|)
throws|throws
name|ContainerExecutionException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|environment
init|=
name|container
operator|.
name|getLaunchContext
argument_list|()
operator|.
name|getEnvironment
argument_list|()
decl_stmt|;
name|String
name|envOciContainerPidNamespace
init|=
name|getEnvOciContainerPidNamespace
argument_list|()
decl_stmt|;
name|String
name|pidNamespace
init|=
name|environment
operator|.
name|get
argument_list|(
name|envOciContainerPidNamespace
argument_list|)
decl_stmt|;
if|if
condition|(
name|pidNamespace
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
name|pidNamespace
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"host"
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"NOT requesting PID namespace. Value of "
operator|+
name|envOciContainerPidNamespace
operator|+
literal|"is invalid: "
operator|+
name|pidNamespace
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
name|boolean
name|hostPidNamespaceEnabled
init|=
name|getHostPidNamespaceEnabled
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|hostPidNamespaceEnabled
condition|)
block|{
name|String
name|message
init|=
literal|"Host pid namespace being requested but this is not "
operator|+
literal|"enabled on this cluster"
decl_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
name|message
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|ContainerExecutionException
argument_list|(
name|message
argument_list|)
throw|;
block|}
return|return
literal|true
return|;
block|}
DECL|method|validateHostname (String hostname)
specifier|protected
specifier|static
name|void
name|validateHostname
parameter_list|(
name|String
name|hostname
parameter_list|)
throws|throws
name|ContainerExecutionException
block|{
if|if
condition|(
name|hostname
operator|!=
literal|null
operator|&&
operator|!
name|hostname
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|HOSTNAME_PATTERN
operator|.
name|matcher
argument_list|(
name|hostname
argument_list|)
operator|.
name|matches
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ContainerExecutionException
argument_list|(
literal|"Hostname '"
operator|+
name|hostname
operator|+
literal|"' doesn't match OCI-compliant hostname pattern"
argument_list|)
throw|;
block|}
if|if
condition|(
name|hostname
operator|.
name|length
argument_list|()
operator|>
name|HOST_NAME_LENGTH
condition|)
block|{
throw|throw
operator|new
name|ContainerExecutionException
argument_list|(
literal|"Hostname can not be greater than "
operator|+
name|HOST_NAME_LENGTH
operator|+
literal|" characters: "
operator|+
name|hostname
argument_list|)
throw|;
block|}
block|}
block|}
comment|/**    * Return whether the YARN container is allowed to run in a privileged    * OCI-compliant container. For a privileged container to be allowed all of    * the following three conditions must be satisfied:    *    *<ol>    *<li>Submitting user must request for a privileged container</li>    *<li>Privileged containers must be enabled on the cluster</li>    *<li>Submitting user must be white-listed to run a privileged    *   container</li>    *</ol>    *    * @param container the target YARN container    * @return whether privileged container execution is allowed    * @throws ContainerExecutionException if privileged container execution    * is requested but is not allowed    */
DECL|method|allowPrivilegedContainerExecution (Container container)
specifier|protected
name|boolean
name|allowPrivilegedContainerExecution
parameter_list|(
name|Container
name|container
parameter_list|)
throws|throws
name|ContainerExecutionException
block|{
if|if
condition|(
operator|!
name|isContainerRequestedAsPrivileged
argument_list|(
name|container
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Privileged container requested for : "
operator|+
name|container
operator|.
name|getContainerId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|//Ok, so we have been asked to run a privileged container. Security
comment|// checks need to be run. Each violation is an error.
comment|//check if privileged containers are enabled.
name|boolean
name|privilegedContainersEnabledOnCluster
init|=
name|getPrivilegedContainersEnabledOnCluster
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|privilegedContainersEnabledOnCluster
condition|)
block|{
name|String
name|message
init|=
literal|"Privileged container being requested but privileged "
operator|+
literal|"containers are not enabled on this cluster"
decl_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
name|message
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|ContainerExecutionException
argument_list|(
name|message
argument_list|)
throw|;
block|}
comment|//check if submitting user is in the whitelist.
name|String
name|submittingUser
init|=
name|container
operator|.
name|getUser
argument_list|()
decl_stmt|;
name|UserGroupInformation
name|submitterUgi
init|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|submittingUser
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|getPrivilegedContainersAcl
argument_list|()
operator|.
name|isUserAllowed
argument_list|(
name|submitterUgi
argument_list|)
condition|)
block|{
name|String
name|message
init|=
literal|"Cannot launch privileged container. Submitting user ("
operator|+
name|submittingUser
operator|+
literal|") fails ACL check."
decl_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
name|message
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|ContainerExecutionException
argument_list|(
name|message
argument_list|)
throw|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"All checks pass. Launching privileged container for : "
operator|+
name|container
operator|.
name|getContainerId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|/**    * This function only returns whether a privileged container was requested,    * not whether the container was or will be launched as privileged.    * @param container    * @return true if container is requested as privileged    */
DECL|method|isContainerRequestedAsPrivileged ( Container container)
specifier|protected
name|boolean
name|isContainerRequestedAsPrivileged
parameter_list|(
name|Container
name|container
parameter_list|)
block|{
name|String
name|envOciContainerRunPrivilegedContainer
init|=
name|getEnvOciContainerRunPrivilegedContainer
argument_list|()
decl_stmt|;
name|String
name|runPrivilegedContainerEnvVar
init|=
name|container
operator|.
name|getLaunchContext
argument_list|()
operator|.
name|getEnvironment
argument_list|()
operator|.
name|get
argument_list|(
name|envOciContainerRunPrivilegedContainer
argument_list|)
decl_stmt|;
return|return
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|runPrivilegedContainerEnvVar
argument_list|)
return|;
block|}
DECL|method|getCsiClients ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|CsiAdaptorProtocol
argument_list|>
name|getCsiClients
parameter_list|()
block|{
return|return
name|csiClients
return|;
block|}
comment|/**    * Initiate CSI clients to talk to the CSI adaptors on this node and    * cache the clients for easier fetch.    * @param config configuration    * @throws ContainerExecutionException    */
DECL|method|initiateCsiClients (Configuration config)
specifier|protected
name|void
name|initiateCsiClients
parameter_list|(
name|Configuration
name|config
parameter_list|)
throws|throws
name|ContainerExecutionException
block|{
name|String
index|[]
name|driverNames
init|=
name|CsiConfigUtils
operator|.
name|getCsiDriverNames
argument_list|(
name|config
argument_list|)
decl_stmt|;
if|if
condition|(
name|driverNames
operator|!=
literal|null
operator|&&
name|driverNames
operator|.
name|length
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|String
name|driverName
range|:
name|driverNames
control|)
block|{
try|try
block|{
comment|// find out the adaptors service address
name|InetSocketAddress
name|adaptorServiceAddress
init|=
name|CsiConfigUtils
operator|.
name|getCsiAdaptorAddressForDriver
argument_list|(
name|driverName
argument_list|,
name|config
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Initializing a csi-adaptor-client for csi-adaptor {},"
operator|+
literal|" csi-driver {}"
argument_list|,
name|adaptorServiceAddress
operator|.
name|toString
argument_list|()
argument_list|,
name|driverName
argument_list|)
expr_stmt|;
name|CsiAdaptorProtocolPBClientImpl
name|client
init|=
operator|new
name|CsiAdaptorProtocolPBClientImpl
argument_list|(
literal|1L
argument_list|,
name|adaptorServiceAddress
argument_list|,
name|config
argument_list|)
decl_stmt|;
name|csiClients
operator|.
name|put
argument_list|(
name|driverName
argument_list|,
name|client
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
decl||
name|YarnException
name|e1
parameter_list|)
block|{
throw|throw
operator|new
name|ContainerExecutionException
argument_list|(
name|e1
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
block|}
DECL|method|formatOciEnvKey (String runtimeTypeUpper, String envKeySuffix)
specifier|public
specifier|static
name|String
name|formatOciEnvKey
parameter_list|(
name|String
name|runtimeTypeUpper
parameter_list|,
name|String
name|envKeySuffix
parameter_list|)
block|{
return|return
name|String
operator|.
name|format
argument_list|(
name|RUNTIME_PREFIX
argument_list|,
name|runtimeTypeUpper
argument_list|,
name|envKeySuffix
argument_list|)
return|;
block|}
block|}
end_class

end_unit

