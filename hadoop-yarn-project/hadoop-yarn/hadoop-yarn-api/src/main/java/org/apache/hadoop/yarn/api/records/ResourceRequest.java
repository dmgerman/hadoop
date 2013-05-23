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
operator|.
name|Stable
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
name|AMRMProtocol
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
comment|/**  *<p><code>ResourceRequest</code> represents the request made by an  * application to the<code>ResourceManager</code> to obtain various   *<code>Container</code> allocations.</p>  *   *<p>It includes:  *<ul>  *<li>{@link Priority} of the request.</li>  *<li>  *       The<em>name</em> of the machine or rack on which the allocation is   *       desired. A special value of<em>*</em> signifies that   *<em>any</em> host/rack is acceptable to the application.  *</li>  *<li>{@link Resource} required for each request.</li>  *<li>  *       Number of containers of such specifications which are required   *       by the application.  *</li>  *</ul>  *</p>  *   * @see Resource  * @see AMRMProtocol#allocate(org.apache.hadoop.yarn.api.protocolrecords.AllocateRequest)  */
end_comment

begin_class
annotation|@
name|Public
annotation|@
name|Stable
DECL|class|ResourceRequest
specifier|public
specifier|abstract
class|class
name|ResourceRequest
implements|implements
name|Comparable
argument_list|<
name|ResourceRequest
argument_list|>
block|{
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|newInstance (Priority priority, String hostName, Resource capability, int numContainers)
specifier|public
specifier|static
name|ResourceRequest
name|newInstance
parameter_list|(
name|Priority
name|priority
parameter_list|,
name|String
name|hostName
parameter_list|,
name|Resource
name|capability
parameter_list|,
name|int
name|numContainers
parameter_list|)
block|{
name|ResourceRequest
name|request
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|ResourceRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|request
operator|.
name|setPriority
argument_list|(
name|priority
argument_list|)
expr_stmt|;
name|request
operator|.
name|setHostName
argument_list|(
name|hostName
argument_list|)
expr_stmt|;
name|request
operator|.
name|setCapability
argument_list|(
name|capability
argument_list|)
expr_stmt|;
name|request
operator|.
name|setNumContainers
argument_list|(
name|numContainers
argument_list|)
expr_stmt|;
return|return
name|request
return|;
block|}
comment|/**    * The constant string representing no locality.    * It should be used by all references that want to pass an arbitrary host    * name in.    */
DECL|field|ANY
specifier|public
specifier|static
specifier|final
name|String
name|ANY
init|=
literal|"*"
decl_stmt|;
comment|/**    * Check whether the given<em>host/rack</em> string represents an arbitrary    * host name.    *    * @param hostName<em>host/rack</em> on which the allocation is desired    * @return whether the given<em>host/rack</em> string represents an arbitrary    * host name    */
DECL|method|isAnyLocation (String hostName)
specifier|public
specifier|static
name|boolean
name|isAnyLocation
parameter_list|(
name|String
name|hostName
parameter_list|)
block|{
return|return
name|ANY
operator|.
name|equals
argument_list|(
name|hostName
argument_list|)
return|;
block|}
comment|/**    * Get the<code>Priority</code> of the request.    * @return<code>Priority</code> of the request    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getPriority ()
specifier|public
specifier|abstract
name|Priority
name|getPriority
parameter_list|()
function_decl|;
comment|/**    * Set the<code>Priority</code> of the request    * @param priority<code>Priority</code> of the request    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|setPriority (Priority priority)
specifier|public
specifier|abstract
name|void
name|setPriority
parameter_list|(
name|Priority
name|priority
parameter_list|)
function_decl|;
comment|/**    * Get the<em>host/rack</em> on which the allocation is desired.    *     * A special value of<em>*</em> signifies that<em>any</em> host/rack is     * acceptable.    *     * @return<em>host/rack</em> on which the allocation is desired    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getHostName ()
specifier|public
specifier|abstract
name|String
name|getHostName
parameter_list|()
function_decl|;
comment|/**    * Set<em>host/rack</em> on which the allocation is desired.    *     * A special value of<em>*</em> signifies that<em>any</em> host/rack is     * acceptable.    *     * @param hostName<em>host/rack</em> on which the allocation is desired    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|setHostName (String hostName)
specifier|public
specifier|abstract
name|void
name|setHostName
parameter_list|(
name|String
name|hostName
parameter_list|)
function_decl|;
comment|/**    * Get the<code>Resource</code> capability of the request.    * @return<code>Resource</code> capability of the request    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getCapability ()
specifier|public
specifier|abstract
name|Resource
name|getCapability
parameter_list|()
function_decl|;
comment|/**    * Set the<code>Resource</code> capability of the request    * @param capability<code>Resource</code> capability of the request    */
annotation|@
name|Public
annotation|@
name|Stable
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
comment|/**    * Get the number of containers required with the given specifications.    * @return number of containers required with the given specifications    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getNumContainers ()
specifier|public
specifier|abstract
name|int
name|getNumContainers
parameter_list|()
function_decl|;
comment|/**    * Set the number of containers required with the given specifications    * @param numContainers number of containers required with the given     *                      specifications    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|setNumContainers (int numContainers)
specifier|public
specifier|abstract
name|void
name|setNumContainers
parameter_list|(
name|int
name|numContainers
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
name|Resource
name|capability
init|=
name|getCapability
argument_list|()
decl_stmt|;
name|String
name|hostName
init|=
name|getHostName
argument_list|()
decl_stmt|;
name|Priority
name|priority
init|=
name|getPriority
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
name|hostName
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|hostName
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
name|getNumContainers
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
name|priority
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|priority
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
return|return
literal|true
return|;
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
return|return
literal|false
return|;
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
return|return
literal|false
return|;
name|ResourceRequest
name|other
init|=
operator|(
name|ResourceRequest
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
return|return
literal|false
return|;
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
return|return
literal|false
return|;
name|String
name|hostName
init|=
name|getHostName
argument_list|()
decl_stmt|;
if|if
condition|(
name|hostName
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|getHostName
argument_list|()
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|hostName
operator|.
name|equals
argument_list|(
name|other
operator|.
name|getHostName
argument_list|()
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|getNumContainers
argument_list|()
operator|!=
name|other
operator|.
name|getNumContainers
argument_list|()
condition|)
return|return
literal|false
return|;
name|Priority
name|priority
init|=
name|getPriority
argument_list|()
decl_stmt|;
if|if
condition|(
name|priority
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|getPriority
argument_list|()
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|priority
operator|.
name|equals
argument_list|(
name|other
operator|.
name|getPriority
argument_list|()
argument_list|)
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|compareTo (ResourceRequest other)
specifier|public
name|int
name|compareTo
parameter_list|(
name|ResourceRequest
name|other
parameter_list|)
block|{
name|int
name|priorityComparison
init|=
name|this
operator|.
name|getPriority
argument_list|()
operator|.
name|compareTo
argument_list|(
name|other
operator|.
name|getPriority
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|priorityComparison
operator|==
literal|0
condition|)
block|{
name|int
name|hostNameComparison
init|=
name|this
operator|.
name|getHostName
argument_list|()
operator|.
name|compareTo
argument_list|(
name|other
operator|.
name|getHostName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|hostNameComparison
operator|==
literal|0
condition|)
block|{
name|int
name|capabilityComparison
init|=
name|this
operator|.
name|getCapability
argument_list|()
operator|.
name|compareTo
argument_list|(
name|other
operator|.
name|getCapability
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|capabilityComparison
operator|==
literal|0
condition|)
block|{
name|int
name|numContainersComparison
init|=
name|this
operator|.
name|getNumContainers
argument_list|()
operator|-
name|other
operator|.
name|getNumContainers
argument_list|()
decl_stmt|;
if|if
condition|(
name|numContainersComparison
operator|==
literal|0
condition|)
block|{
return|return
literal|0
return|;
block|}
else|else
block|{
return|return
name|numContainersComparison
return|;
block|}
block|}
else|else
block|{
return|return
name|capabilityComparison
return|;
block|}
block|}
else|else
block|{
return|return
name|hostNameComparison
return|;
block|}
block|}
else|else
block|{
return|return
name|priorityComparison
return|;
block|}
block|}
block|}
end_class

end_unit

