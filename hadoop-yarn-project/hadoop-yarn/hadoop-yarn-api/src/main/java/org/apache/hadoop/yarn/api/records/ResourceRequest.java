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
name|java
operator|.
name|io
operator|.
name|Serializable
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
operator|.
name|Evolving
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
comment|/**  * {@code ResourceRequest} represents the request made  * by an application to the {@code ResourceManager}  * to obtain various {@code Container} allocations.  *<p>  * It includes:  *<ul>  *<li>{@link Priority} of the request.</li>  *<li>  *     The<em>name</em> of the machine or rack on which the allocation is  *     desired. A special value of<em>*</em> signifies that  *<em>any</em> host/rack is acceptable to the application.  *</li>  *<li>{@link Resource} required for each request.</li>  *<li>  *     Number of containers, of above specifications, which are required  *     by the application.  *</li>  *<li>  *     A boolean<em>relaxLocality</em> flag, defaulting to {@code true},  *     which tells the {@code ResourceManager} if the application wants  *     locality to be loose (i.e. allows fall-through to rack or<em>any</em>)  *     or strict (i.e. specify hard constraint on resource allocation).  *</li>  *</ul>  *   * @see Resource  * @see ApplicationMasterProtocol#allocate(org.apache.hadoop.yarn.api.protocolrecords.AllocateRequest)  */
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
return|return
name|newInstance
argument_list|(
name|priority
argument_list|,
name|hostName
argument_list|,
name|capability
argument_list|,
name|numContainers
argument_list|,
literal|true
argument_list|)
return|;
block|}
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|newInstance (Priority priority, String hostName, Resource capability, int numContainers, boolean relaxLocality)
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
parameter_list|,
name|boolean
name|relaxLocality
parameter_list|)
block|{
return|return
name|newInstance
argument_list|(
name|priority
argument_list|,
name|hostName
argument_list|,
name|capability
argument_list|,
name|numContainers
argument_list|,
name|relaxLocality
argument_list|,
literal|null
argument_list|)
return|;
block|}
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|newInstance (Priority priority, String hostName, Resource capability, int numContainers, boolean relaxLocality, String labelExpression)
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
parameter_list|,
name|boolean
name|relaxLocality
parameter_list|,
name|String
name|labelExpression
parameter_list|)
block|{
return|return
name|newInstance
argument_list|(
name|priority
argument_list|,
name|hostName
argument_list|,
name|capability
argument_list|,
name|numContainers
argument_list|,
name|relaxLocality
argument_list|,
name|labelExpression
argument_list|,
name|ExecutionTypeRequest
operator|.
name|newInstance
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Public
annotation|@
name|Evolving
DECL|method|newInstance (Priority priority, String hostName, Resource capability, int numContainers, boolean relaxLocality, String labelExpression, ExecutionTypeRequest executionTypeRequest)
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
parameter_list|,
name|boolean
name|relaxLocality
parameter_list|,
name|String
name|labelExpression
parameter_list|,
name|ExecutionTypeRequest
name|executionTypeRequest
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
name|setResourceName
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
name|request
operator|.
name|setRelaxLocality
argument_list|(
name|relaxLocality
argument_list|)
expr_stmt|;
name|request
operator|.
name|setNodeLabelExpression
argument_list|(
name|labelExpression
argument_list|)
expr_stmt|;
name|request
operator|.
name|setExecutionTypeRequest
argument_list|(
name|executionTypeRequest
argument_list|)
expr_stmt|;
return|return
name|request
return|;
block|}
annotation|@
name|Public
annotation|@
name|Stable
DECL|class|ResourceRequestComparator
specifier|public
specifier|static
class|class
name|ResourceRequestComparator
implements|implements
name|java
operator|.
name|util
operator|.
name|Comparator
argument_list|<
name|ResourceRequest
argument_list|>
implements|,
name|Serializable
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
annotation|@
name|Override
DECL|method|compare (ResourceRequest r1, ResourceRequest r2)
specifier|public
name|int
name|compare
parameter_list|(
name|ResourceRequest
name|r1
parameter_list|,
name|ResourceRequest
name|r2
parameter_list|)
block|{
comment|// Compare priority, host and capability
name|int
name|ret
init|=
name|r1
operator|.
name|getPriority
argument_list|()
operator|.
name|compareTo
argument_list|(
name|r2
operator|.
name|getPriority
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|ret
operator|==
literal|0
condition|)
block|{
name|String
name|h1
init|=
name|r1
operator|.
name|getResourceName
argument_list|()
decl_stmt|;
name|String
name|h2
init|=
name|r2
operator|.
name|getResourceName
argument_list|()
decl_stmt|;
name|ret
operator|=
name|h1
operator|.
name|compareTo
argument_list|(
name|h2
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|ret
operator|==
literal|0
condition|)
block|{
name|ret
operator|=
name|r1
operator|.
name|getCapability
argument_list|()
operator|.
name|compareTo
argument_list|(
name|r2
operator|.
name|getCapability
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
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
annotation|@
name|Public
annotation|@
name|Stable
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
comment|/**    * Get the resource (e.g.<em>host/rack</em>) on which the allocation     * is desired.    *     * A special value of<em>*</em> signifies that<em>any</em> resource     * (host/rack) is acceptable.    *     * @return resource (e.g.<em>host/rack</em>) on which the allocation     *                  is desired    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getResourceName ()
specifier|public
specifier|abstract
name|String
name|getResourceName
parameter_list|()
function_decl|;
comment|/**    * Set the resource name (e.g.<em>host/rack</em>) on which the allocation     * is desired.    *     * A special value of<em>*</em> signifies that<em>any</em> resource name    * (e.g. host/rack) is acceptable.     *     * @param resourceName (e.g.<em>host/rack</em>) on which the     *                     allocation is desired    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|setResourceName (String resourceName)
specifier|public
specifier|abstract
name|void
name|setResourceName
parameter_list|(
name|String
name|resourceName
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
comment|/**    * Get whether locality relaxation is enabled with this    *<code>ResourceRequest</code>. Defaults to true.    *     * @return whether locality relaxation is enabled with this    *<code>ResourceRequest</code>.    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getRelaxLocality ()
specifier|public
specifier|abstract
name|boolean
name|getRelaxLocality
parameter_list|()
function_decl|;
comment|/**    * Set the<code>ExecutionTypeRequest</code> of the requested container.    *    * @param execSpec    *          ExecutionTypeRequest of the requested container    */
annotation|@
name|Public
annotation|@
name|Evolving
DECL|method|setExecutionTypeRequest (ExecutionTypeRequest execSpec)
specifier|public
name|void
name|setExecutionTypeRequest
parameter_list|(
name|ExecutionTypeRequest
name|execSpec
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
comment|/**    * Get whether locality relaxation is enabled with this    *<code>ResourceRequest</code>. Defaults to true.    *    * @return whether locality relaxation is enabled with this    *<code>ResourceRequest</code>.    */
annotation|@
name|Public
annotation|@
name|Evolving
DECL|method|getExecutionTypeRequest ()
specifier|public
name|ExecutionTypeRequest
name|getExecutionTypeRequest
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
comment|/**    *<p>For a request at a network hierarchy level, set whether locality can be relaxed    * to that level and beyond.<p>    *     *<p>If the flag is off on a rack-level<code>ResourceRequest</code>,    * containers at that request's priority will not be assigned to nodes on that    * request's rack unless requests specifically for those nodes have also been    * submitted.<p>    *     *<p>If the flag is off on an {@link ResourceRequest#ANY}-level    *<code>ResourceRequest</code>, containers at that request's priority will    * only be assigned on racks for which specific requests have also been    * submitted.<p>    *     *<p>For example, to request a container strictly on a specific node, the    * corresponding rack-level and any-level requests should have locality    * relaxation set to false.  Similarly, to request a container strictly on a    * specific rack, the corresponding any-level request should have locality    * relaxation set to false.<p>    *     * @param relaxLocality whether locality relaxation is enabled with this    *<code>ResourceRequest</code>.    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|setRelaxLocality (boolean relaxLocality)
specifier|public
specifier|abstract
name|void
name|setRelaxLocality
parameter_list|(
name|boolean
name|relaxLocality
parameter_list|)
function_decl|;
comment|/**    * Get node-label-expression for this Resource Request. If this is set, all    * containers allocated to satisfy this resource-request will be only on those    * nodes that satisfy this node-label-expression.    *      * Please note that node label expression now can only take effect when the    * resource request has resourceName = ANY    *     * @return node-label-expression    */
annotation|@
name|Public
annotation|@
name|Evolving
DECL|method|getNodeLabelExpression ()
specifier|public
specifier|abstract
name|String
name|getNodeLabelExpression
parameter_list|()
function_decl|;
comment|/**    * Set node label expression of this resource request. Now only support    * specifying a single node label. In the future we will support more complex    * node label expression specification like {@code AND(&&), OR(||)}, etc.    *     * Any please note that node label expression now can only take effect when    * the resource request has resourceName = ANY    *     * @param nodelabelExpression    *          node-label-expression of this ResourceRequest    */
annotation|@
name|Public
annotation|@
name|Evolving
DECL|method|setNodeLabelExpression (String nodelabelExpression)
specifier|public
specifier|abstract
name|void
name|setNodeLabelExpression
parameter_list|(
name|String
name|nodelabelExpression
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
name|getResourceName
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
name|getResourceName
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
name|getResourceName
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
name|getResourceName
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
name|ExecutionTypeRequest
name|execTypeRequest
init|=
name|getExecutionTypeRequest
argument_list|()
decl_stmt|;
if|if
condition|(
name|execTypeRequest
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|getExecutionTypeRequest
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
name|execTypeRequest
operator|.
name|equals
argument_list|(
name|other
operator|.
name|getExecutionTypeRequest
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
name|getNodeLabelExpression
argument_list|()
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|getNodeLabelExpression
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
else|else
block|{
comment|// do normalize on label expression before compare
name|String
name|label1
init|=
name|getNodeLabelExpression
argument_list|()
operator|.
name|replaceAll
argument_list|(
literal|"[\\t ]"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|String
name|label2
init|=
name|other
operator|.
name|getNodeLabelExpression
argument_list|()
operator|==
literal|null
condition|?
literal|null
else|:
name|other
operator|.
name|getNodeLabelExpression
argument_list|()
operator|.
name|replaceAll
argument_list|(
literal|"[\\t ]"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|label1
operator|.
name|equals
argument_list|(
name|label2
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
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
name|getResourceName
argument_list|()
operator|.
name|compareTo
argument_list|(
name|other
operator|.
name|getResourceName
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
return|return
name|this
operator|.
name|getNumContainers
argument_list|()
operator|-
name|other
operator|.
name|getNumContainers
argument_list|()
return|;
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

