begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**   * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.client.api
package|package
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
name|Map
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
name|InterfaceAudience
operator|.
name|Private
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
operator|.
name|Public
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
name|service
operator|.
name|AbstractService
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
name|ContainerStatus
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
name|NodeId
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
name|impl
operator|.
name|NMClientImpl
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

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|class|NMClient
specifier|public
specifier|abstract
class|class
name|NMClient
extends|extends
name|AbstractService
block|{
comment|/**    * Create a new instance of NMClient.    */
annotation|@
name|Public
DECL|method|createNMClient ()
specifier|public
specifier|static
name|NMClient
name|createNMClient
parameter_list|()
block|{
name|NMClient
name|client
init|=
operator|new
name|NMClientImpl
argument_list|()
decl_stmt|;
return|return
name|client
return|;
block|}
comment|/**    * Create a new instance of NMClient.    */
annotation|@
name|Public
DECL|method|createNMClient (String name)
specifier|public
specifier|static
name|NMClient
name|createNMClient
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|NMClient
name|client
init|=
operator|new
name|NMClientImpl
argument_list|(
name|name
argument_list|)
decl_stmt|;
return|return
name|client
return|;
block|}
DECL|enum|UpgradeOp
specifier|protected
enum|enum
name|UpgradeOp
block|{
DECL|enumConstant|REINIT
DECL|enumConstant|RESTART
DECL|enumConstant|COMMIT
DECL|enumConstant|ROLLBACK
name|REINIT
block|,
name|RESTART
block|,
name|COMMIT
block|,
name|ROLLBACK
block|}
DECL|field|nmTokenCache
specifier|private
name|NMTokenCache
name|nmTokenCache
init|=
name|NMTokenCache
operator|.
name|getSingleton
argument_list|()
decl_stmt|;
annotation|@
name|Private
DECL|method|NMClient (String name)
specifier|protected
name|NMClient
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
comment|/**    *<p>Start an allocated container.</p>    *    *<p>The<code>ApplicationMaster</code> or other applications that use the    * client must provide the details of the allocated container, including the    * Id, the assigned node's Id and the token via {@link Container}. In    * addition, the AM needs to provide the {@link ContainerLaunchContext} as    * well.</p>    *    * @param container the allocated container    * @param containerLaunchContext the context information needed by the    *<code>NodeManager</code> to launch the    *                               container    * @return a map between the auxiliary service names and their outputs    * @throws YarnException YarnException.    * @throws IOException IOException.    */
DECL|method|startContainer (Container container, ContainerLaunchContext containerLaunchContext)
specifier|public
specifier|abstract
name|Map
argument_list|<
name|String
argument_list|,
name|ByteBuffer
argument_list|>
name|startContainer
parameter_list|(
name|Container
name|container
parameter_list|,
name|ContainerLaunchContext
name|containerLaunchContext
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
comment|/**    *<p>Increase the resource of a container.</p>    *    *<p>The<code>ApplicationMaster</code> or other applications that use the    * client must provide the details of the container, including the Id and    * the target resource encapsulated in the updated container token via    * {@link Container}.    *</p>    *    * @param container the container with updated token.    *    * @throws YarnException YarnException.    * @throws IOException IOException.    */
annotation|@
name|Deprecated
DECL|method|increaseContainerResource (Container container)
specifier|public
specifier|abstract
name|void
name|increaseContainerResource
parameter_list|(
name|Container
name|container
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
comment|/**    *<p>Update the resources of a container.</p>    *    *<p>The<code>ApplicationMaster</code> or other applications that use the    * client must provide the details of the container, including the Id and    * the target resource encapsulated in the updated container token via    * {@link Container}.    *</p>    *    * @param container the container with updated token.    *    * @throws YarnException YarnException.    * @throws IOException IOException.    */
DECL|method|updateContainerResource (Container container)
specifier|public
specifier|abstract
name|void
name|updateContainerResource
parameter_list|(
name|Container
name|container
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
comment|/**    *<p>Stop an started container.</p>    *    * @param containerId the Id of the started container    * @param nodeId the Id of the<code>NodeManager</code>    *    * @throws YarnException YarnException.    * @throws IOException IOException.    */
DECL|method|stopContainer (ContainerId containerId, NodeId nodeId)
specifier|public
specifier|abstract
name|void
name|stopContainer
parameter_list|(
name|ContainerId
name|containerId
parameter_list|,
name|NodeId
name|nodeId
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
comment|/**    *<p>Query the status of a container.</p>    *    * @param containerId the Id of the started container    * @param nodeId the Id of the<code>NodeManager</code>    *     * @return the status of a container.    *    * @throws YarnException YarnException.    * @throws IOException IOException.    */
DECL|method|getContainerStatus (ContainerId containerId, NodeId nodeId)
specifier|public
specifier|abstract
name|ContainerStatus
name|getContainerStatus
parameter_list|(
name|ContainerId
name|containerId
parameter_list|,
name|NodeId
name|nodeId
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
comment|/**    *<p>Re-Initialize the Container.</p>    *    * @param containerId the Id of the container to Re-Initialize.    * @param containerLaunchContex the updated ContainerLaunchContext.    * @param autoCommit commit re-initialization automatically ?    *    * @throws YarnException YarnException.    * @throws IOException IOException.    */
DECL|method|reInitializeContainer (ContainerId containerId, ContainerLaunchContext containerLaunchContex, boolean autoCommit)
specifier|public
specifier|abstract
name|void
name|reInitializeContainer
parameter_list|(
name|ContainerId
name|containerId
parameter_list|,
name|ContainerLaunchContext
name|containerLaunchContex
parameter_list|,
name|boolean
name|autoCommit
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
comment|/**    *<p>Restart the specified container.</p>    *    * @param containerId the Id of the container to restart.    *    * @throws YarnException YarnException.    * @throws IOException IOException.    */
DECL|method|restartContainer (ContainerId containerId)
specifier|public
specifier|abstract
name|void
name|restartContainer
parameter_list|(
name|ContainerId
name|containerId
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
comment|/**    *<p>Rollback last reInitialization of the specified container.</p>    *    * @param containerId the Id of the container to restart.    *    * @throws YarnException YarnException.    * @throws IOException IOException.    */
DECL|method|rollbackLastReInitialization (ContainerId containerId)
specifier|public
specifier|abstract
name|void
name|rollbackLastReInitialization
parameter_list|(
name|ContainerId
name|containerId
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
comment|/**    *<p>Commit last reInitialization of the specified container.</p>    *    * @param containerId the Id of the container to commit reInitialize.    *    * @throws YarnException YarnException.    * @throws IOException IOException.    */
DECL|method|commitLastReInitialization (ContainerId containerId)
specifier|public
specifier|abstract
name|void
name|commitLastReInitialization
parameter_list|(
name|ContainerId
name|containerId
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
comment|/**    *<p>Set whether the containers that are started by this client, and are    * still running should be stopped when the client stops. By default, the    * feature should be enabled.</p> However, containers will be stopped only      * when service is stopped. i.e. after {@link NMClient#stop()}.     *    * @param enabled whether the feature is enabled or not    */
DECL|method|cleanupRunningContainersOnStop (boolean enabled)
specifier|public
specifier|abstract
name|void
name|cleanupRunningContainersOnStop
parameter_list|(
name|boolean
name|enabled
parameter_list|)
function_decl|;
comment|/**    * Set the NM Token cache of the<code>NMClient</code>. This cache must be    * shared with the {@link AMRMClient} that requested the containers managed    * by this<code>NMClient</code>    *<p>    * If a NM token cache is not set, the {@link NMTokenCache#getSingleton()}    * singleton instance will be used.    *    * @param nmTokenCache the NM token cache to use.    */
DECL|method|setNMTokenCache (NMTokenCache nmTokenCache)
specifier|public
name|void
name|setNMTokenCache
parameter_list|(
name|NMTokenCache
name|nmTokenCache
parameter_list|)
block|{
name|this
operator|.
name|nmTokenCache
operator|=
name|nmTokenCache
expr_stmt|;
block|}
comment|/**    * Get the NM token cache of the<code>NMClient</code>. This cache must be    * shared with the {@link AMRMClient} that requested the containers managed    * by this<code>NMClient</code>    *<p>    * If a NM token cache is not set, the {@link NMTokenCache#getSingleton()}    * singleton instance will be used.    *    * @return the NM token cache    */
DECL|method|getNMTokenCache ()
specifier|public
name|NMTokenCache
name|getNMTokenCache
parameter_list|()
block|{
return|return
name|nmTokenCache
return|;
block|}
comment|/**    * Get the NodeId of the node on which container is running. It returns    * null if the container if container is not found or if it is not running.    *    * @param containerId Container Id of the container.    * @return NodeId of the container on which it is running.    */
DECL|method|getNodeIdOfStartedContainer (ContainerId containerId)
specifier|public
name|NodeId
name|getNodeIdOfStartedContainer
parameter_list|(
name|ContainerId
name|containerId
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

