begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair
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
name|resourcemanager
operator|.
name|scheduler
operator|.
name|fair
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
name|util
operator|.
name|ReflectionUtils
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
name|StringUtils
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
name|Resource
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
name|resourcemanager
operator|.
name|scheduler
operator|.
name|fair
operator|.
name|policies
operator|.
name|DominantResourceFairnessPolicy
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
name|resourcemanager
operator|.
name|scheduler
operator|.
name|fair
operator|.
name|policies
operator|.
name|FairSharePolicy
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
name|resourcemanager
operator|.
name|scheduler
operator|.
name|fair
operator|.
name|policies
operator|.
name|FifoPolicy
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
name|resource
operator|.
name|ResourceCalculator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentHashMap
import|;
end_import

begin_class
annotation|@
name|Public
annotation|@
name|Evolving
DECL|class|SchedulingPolicy
specifier|public
specifier|abstract
class|class
name|SchedulingPolicy
block|{
DECL|field|instances
specifier|private
specifier|static
specifier|final
name|ConcurrentHashMap
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|SchedulingPolicy
argument_list|>
argument_list|,
name|SchedulingPolicy
argument_list|>
name|instances
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|SchedulingPolicy
argument_list|>
argument_list|,
name|SchedulingPolicy
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|DEFAULT_POLICY
specifier|public
specifier|static
specifier|final
name|SchedulingPolicy
name|DEFAULT_POLICY
init|=
name|getInstance
argument_list|(
name|FairSharePolicy
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Returns a {@link SchedulingPolicy} instance corresponding to the passed clazz    */
DECL|method|getInstance (Class<? extends SchedulingPolicy> clazz)
specifier|public
specifier|static
name|SchedulingPolicy
name|getInstance
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|SchedulingPolicy
argument_list|>
name|clazz
parameter_list|)
block|{
name|SchedulingPolicy
name|policy
init|=
name|ReflectionUtils
operator|.
name|newInstance
argument_list|(
name|clazz
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|SchedulingPolicy
name|policyRet
init|=
name|instances
operator|.
name|putIfAbsent
argument_list|(
name|clazz
argument_list|,
name|policy
argument_list|)
decl_stmt|;
if|if
condition|(
name|policyRet
operator|!=
literal|null
condition|)
block|{
return|return
name|policyRet
return|;
block|}
return|return
name|policy
return|;
block|}
comment|/**    * Returns {@link SchedulingPolicy} instance corresponding to the    * {@link SchedulingPolicy} passed as a string. The policy can be "fair" for    * FairSharePolicy, "fifo" for FifoPolicy, or "drf" for    * DominantResourceFairnessPolicy. For a custom    * {@link SchedulingPolicy}s in the RM classpath, the policy should be    * canonical class name of the {@link SchedulingPolicy}.    *     * @param policy canonical class name or "drf" or "fair" or "fifo"    * @throws AllocationConfigurationException    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|parse (String policy)
specifier|public
specifier|static
name|SchedulingPolicy
name|parse
parameter_list|(
name|String
name|policy
parameter_list|)
throws|throws
name|AllocationConfigurationException
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
name|Class
name|clazz
decl_stmt|;
name|String
name|text
init|=
name|StringUtils
operator|.
name|toLowerCase
argument_list|(
name|policy
argument_list|)
decl_stmt|;
if|if
condition|(
name|text
operator|.
name|equalsIgnoreCase
argument_list|(
name|FairSharePolicy
operator|.
name|NAME
argument_list|)
condition|)
block|{
name|clazz
operator|=
name|FairSharePolicy
operator|.
name|class
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|text
operator|.
name|equalsIgnoreCase
argument_list|(
name|FifoPolicy
operator|.
name|NAME
argument_list|)
condition|)
block|{
name|clazz
operator|=
name|FifoPolicy
operator|.
name|class
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|text
operator|.
name|equalsIgnoreCase
argument_list|(
name|DominantResourceFairnessPolicy
operator|.
name|NAME
argument_list|)
condition|)
block|{
name|clazz
operator|=
name|DominantResourceFairnessPolicy
operator|.
name|class
expr_stmt|;
block|}
else|else
block|{
try|try
block|{
name|clazz
operator|=
name|Class
operator|.
name|forName
argument_list|(
name|policy
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|cnfe
parameter_list|)
block|{
throw|throw
operator|new
name|AllocationConfigurationException
argument_list|(
name|policy
operator|+
literal|" SchedulingPolicy class not found!"
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
operator|!
name|SchedulingPolicy
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|clazz
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|AllocationConfigurationException
argument_list|(
name|policy
operator|+
literal|" does not extend SchedulingPolicy"
argument_list|)
throw|;
block|}
return|return
name|getInstance
argument_list|(
name|clazz
argument_list|)
return|;
block|}
DECL|method|initialize (Resource clusterCapacity)
specifier|public
name|void
name|initialize
parameter_list|(
name|Resource
name|clusterCapacity
parameter_list|)
block|{}
comment|/**    * The {@link ResourceCalculator} returned by this method should be used    * for any calculations involving resources.    *    * @return ResourceCalculator instance to use    */
DECL|method|getResourceCalculator ()
specifier|public
specifier|abstract
name|ResourceCalculator
name|getResourceCalculator
parameter_list|()
function_decl|;
comment|/**    * @return returns the name of {@link SchedulingPolicy}    */
DECL|method|getName ()
specifier|public
specifier|abstract
name|String
name|getName
parameter_list|()
function_decl|;
comment|/**    * The comparator returned by this method is to be used for sorting the    * {@link Schedulable}s in that queue.    *     * @return the comparator to sort by    */
DECL|method|getComparator ()
specifier|public
specifier|abstract
name|Comparator
argument_list|<
name|Schedulable
argument_list|>
name|getComparator
parameter_list|()
function_decl|;
comment|/**    * Computes and updates the shares of {@link Schedulable}s as per    * the {@link SchedulingPolicy}, to be used later for scheduling decisions.    * The shares computed are instantaneous and only consider queues with    * running applications.    *     * @param schedulables {@link Schedulable}s whose shares are to be updated    * @param totalResources Total {@link Resource}s in the cluster    */
DECL|method|computeShares ( Collection<? extends Schedulable> schedulables, Resource totalResources)
specifier|public
specifier|abstract
name|void
name|computeShares
parameter_list|(
name|Collection
argument_list|<
name|?
extends|extends
name|Schedulable
argument_list|>
name|schedulables
parameter_list|,
name|Resource
name|totalResources
parameter_list|)
function_decl|;
comment|/**    * Computes and updates the steady shares of {@link FSQueue}s as per the    * {@link SchedulingPolicy}. The steady share does not differentiate    * between queues with and without running applications under them. The    * steady share is not used for scheduling, it is displayed on the Web UI    * for better visibility.    *    * @param queues {@link FSQueue}s whose shares are to be updated    * @param totalResources Total {@link Resource}s in the cluster    */
DECL|method|computeSteadyShares ( Collection<? extends FSQueue> queues, Resource totalResources)
specifier|public
specifier|abstract
name|void
name|computeSteadyShares
parameter_list|(
name|Collection
argument_list|<
name|?
extends|extends
name|FSQueue
argument_list|>
name|queues
parameter_list|,
name|Resource
name|totalResources
parameter_list|)
function_decl|;
comment|/**    * Check if the resource usage is over the fair share under this policy    *    * @param usage {@link Resource} the resource usage    * @param fairShare {@link Resource} the fair share    * @return true if check passes (is over) or false otherwise    */
DECL|method|checkIfUsageOverFairShare ( Resource usage, Resource fairShare)
specifier|public
specifier|abstract
name|boolean
name|checkIfUsageOverFairShare
parameter_list|(
name|Resource
name|usage
parameter_list|,
name|Resource
name|fairShare
parameter_list|)
function_decl|;
comment|/**    * Get headroom by calculating the min of<code>clusterAvailable</code> and    * (<code>queueFairShare</code> -<code>queueUsage</code>) resources that are    * applicable to this policy. For eg if only memory then leave other    * resources such as CPU to same as clusterAvailable.    *    * @param queueFairShare fairshare in the queue    * @param queueUsage resources used in the queue    * @param maxAvailable available resource in cluster for this queue    * @return calculated headroom    */
DECL|method|getHeadroom (Resource queueFairShare, Resource queueUsage, Resource maxAvailable)
specifier|public
specifier|abstract
name|Resource
name|getHeadroom
parameter_list|(
name|Resource
name|queueFairShare
parameter_list|,
name|Resource
name|queueUsage
parameter_list|,
name|Resource
name|maxAvailable
parameter_list|)
function_decl|;
comment|/**    * Check whether the policy of a child queue is allowed.    *    * @param childPolicy the policy of child queue    * @return true if the child policy is allowed; false otherwise    */
DECL|method|isChildPolicyAllowed (SchedulingPolicy childPolicy)
specifier|public
name|boolean
name|isChildPolicyAllowed
parameter_list|(
name|SchedulingPolicy
name|childPolicy
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

