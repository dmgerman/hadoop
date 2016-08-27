begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.api.records
package|package
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
name|yarn
operator|.
name|api
operator|.
name|ApplicationMasterProtocol
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

begin_comment
comment|/**  * {@code UpdateContainerRequest} represents the request made by an  * application to the {@code ResourceManager} to update an attribute of a  * {@code Container} such as its Resource allocation or (@code ExecutionType}  *<p>  * It includes:  *<ul>  *<li>version for the container.</li>  *<li>{@link ContainerId} for the container.</li>  *<li>  *     {@link Resource} capability of the container after the update request  *     is completed.  *</li>  *<li>  *     {@link ExecutionType} of the container after the update request is  *     completed.  *</li>  *</ul>  *  * Update rules:  *<ul>  *<li>  *     Currently only ONE aspect of the container can be updated per request  *     (user can either update Capability OR ExecutionType in one request..  *     not both).  *</li>  *<li>  *     There must be only 1 update request per container in an allocate call.  *</li>  *<li>  *     If a new update request is sent for a container (in a subsequent allocate  *     call) before the first one is satisfied by the Scheduler, it will  *     overwrite the previous request.  *</li>  *</ul>  * @see ApplicationMasterProtocol#allocate(org.apache.hadoop.yarn.api.protocolrecords.AllocateRequest)  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|UpdateContainerRequest
specifier|public
specifier|abstract
class|class
name|UpdateContainerRequest
block|{
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|method|newInstance (int version, ContainerId containerId, ContainerUpdateType updateType, Resource targetCapability, ExecutionType targetExecutionType)
specifier|public
specifier|static
name|UpdateContainerRequest
name|newInstance
parameter_list|(
name|int
name|version
parameter_list|,
name|ContainerId
name|containerId
parameter_list|,
name|ContainerUpdateType
name|updateType
parameter_list|,
name|Resource
name|targetCapability
parameter_list|,
name|ExecutionType
name|targetExecutionType
parameter_list|)
block|{
name|UpdateContainerRequest
name|request
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|UpdateContainerRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|request
operator|.
name|setContainerVersion
argument_list|(
name|version
argument_list|)
expr_stmt|;
name|request
operator|.
name|setContainerId
argument_list|(
name|containerId
argument_list|)
expr_stmt|;
name|request
operator|.
name|setContainerUpdateType
argument_list|(
name|updateType
argument_list|)
expr_stmt|;
name|request
operator|.
name|setExecutionType
argument_list|(
name|targetExecutionType
argument_list|)
expr_stmt|;
name|request
operator|.
name|setCapability
argument_list|(
name|targetCapability
argument_list|)
expr_stmt|;
return|return
name|request
return|;
block|}
comment|/**    * Get the<code>ContainerId</code> of the container.    * @return<code>ContainerId</code> of the container    */
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|method|getContainerVersion ()
specifier|public
specifier|abstract
name|int
name|getContainerVersion
parameter_list|()
function_decl|;
comment|/**    * Set the current version of the container.    * @param containerVersion of the container    */
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|method|setContainerVersion (int containerVersion)
specifier|public
specifier|abstract
name|void
name|setContainerVersion
parameter_list|(
name|int
name|containerVersion
parameter_list|)
function_decl|;
comment|/**    * Get the<code>ContainerUpdateType</code> of the container.    * @return<code>ContainerUpdateType</code> of the container.    */
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|method|getContainerUpdateType ()
specifier|public
specifier|abstract
name|ContainerUpdateType
name|getContainerUpdateType
parameter_list|()
function_decl|;
comment|/**    * Set the<code>ContainerUpdateType</code> of the container.    * @param updateType of the Container    */
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|method|setContainerUpdateType (ContainerUpdateType updateType)
specifier|public
specifier|abstract
name|void
name|setContainerUpdateType
parameter_list|(
name|ContainerUpdateType
name|updateType
parameter_list|)
function_decl|;
comment|/**    * Get the<code>ContainerId</code> of the container.    * @return<code>ContainerId</code> of the container    */
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|method|getContainerId ()
specifier|public
specifier|abstract
name|ContainerId
name|getContainerId
parameter_list|()
function_decl|;
comment|/**    * Set the<code>ContainerId</code> of the container.    * @param containerId<code>ContainerId</code> of the container    */
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|method|setContainerId (ContainerId containerId)
specifier|public
specifier|abstract
name|void
name|setContainerId
parameter_list|(
name|ContainerId
name|containerId
parameter_list|)
function_decl|;
comment|/**    * Get the<code>Resource</code> capability of the container.    * @return<code>Resource</code> capability of the container    */
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|method|getCapability ()
specifier|public
specifier|abstract
name|Resource
name|getCapability
parameter_list|()
function_decl|;
comment|/**    * Set the<code>Resource</code> capability of the container.    * @param capability<code>Resource</code> capability of the container    */
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|method|setCapability (Resource capability)
specifier|public
specifier|abstract
name|void
name|setCapability
parameter_list|(
name|Resource
name|capability
parameter_list|)
function_decl|;
comment|/**    * Get the target<code>ExecutionType</code> of the container.    * @return<code>ExecutionType</code> of the container    */
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|method|getExecutionType ()
specifier|public
specifier|abstract
name|ExecutionType
name|getExecutionType
parameter_list|()
function_decl|;
comment|/**    * Set the target<code>ExecutionType</code> of the container.    * @param executionType<code>ExecutionType</code> of the container    */
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|method|setExecutionType (ExecutionType executionType)
specifier|public
specifier|abstract
name|void
name|setExecutionType
parameter_list|(
name|ExecutionType
name|executionType
parameter_list|)
function_decl|;
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
specifier|final
name|int
name|prime
init|=
literal|2153
decl_stmt|;
name|int
name|result
init|=
literal|2459
decl_stmt|;
name|ContainerId
name|cId
init|=
name|getContainerId
argument_list|()
decl_stmt|;
name|ExecutionType
name|execType
init|=
name|getExecutionType
argument_list|()
decl_stmt|;
name|Resource
name|capability
init|=
name|getCapability
argument_list|()
decl_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|capability
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|capability
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|cId
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|cId
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|getContainerVersion
argument_list|()
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|execType
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|execType
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|equals (Object obj)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|obj
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
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|UpdateContainerRequest
name|other
init|=
operator|(
name|UpdateContainerRequest
operator|)
name|obj
decl_stmt|;
name|Resource
name|capability
init|=
name|getCapability
argument_list|()
decl_stmt|;
if|if
condition|(
name|capability
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|getCapability
argument_list|()
operator|!=
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
elseif|else
if|if
condition|(
operator|!
name|capability
operator|.
name|equals
argument_list|(
name|other
operator|.
name|getCapability
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|ContainerId
name|cId
init|=
name|getContainerId
argument_list|()
decl_stmt|;
if|if
condition|(
name|cId
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|getContainerId
argument_list|()
operator|!=
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
elseif|else
if|if
condition|(
operator|!
name|cId
operator|.
name|equals
argument_list|(
name|other
operator|.
name|getContainerId
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|getContainerVersion
argument_list|()
operator|!=
name|other
operator|.
name|getContainerVersion
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|ExecutionType
name|execType
init|=
name|getExecutionType
argument_list|()
decl_stmt|;
if|if
condition|(
name|execType
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|getExecutionType
argument_list|()
operator|!=
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
elseif|else
if|if
condition|(
operator|!
name|execType
operator|.
name|equals
argument_list|(
name|other
operator|.
name|getExecutionType
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

