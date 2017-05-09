begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.providers
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|providers
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|api
operator|.
name|ResourceKeys
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|api
operator|.
name|resource
operator|.
name|Component
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|state
operator|.
name|RoleInstance
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Queue
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
name|ConcurrentLinkedQueue
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
name|atomic
operator|.
name|AtomicLong
import|;
end_import

begin_comment
comment|/**  * Provider role and key for use in app requests.  *   * This class uses the role name as the key for hashes and in equality tests,  * and ignores the other values.  */
end_comment

begin_class
DECL|class|ProviderRole
specifier|public
specifier|final
class|class
name|ProviderRole
block|{
DECL|field|name
specifier|public
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|group
specifier|public
specifier|final
name|String
name|group
decl_stmt|;
DECL|field|id
specifier|public
specifier|final
name|int
name|id
decl_stmt|;
DECL|field|placementPolicy
specifier|public
name|int
name|placementPolicy
decl_stmt|;
DECL|field|nodeFailureThreshold
specifier|public
name|int
name|nodeFailureThreshold
decl_stmt|;
DECL|field|placementTimeoutSeconds
specifier|public
specifier|final
name|long
name|placementTimeoutSeconds
decl_stmt|;
DECL|field|labelExpression
specifier|public
specifier|final
name|String
name|labelExpression
decl_stmt|;
DECL|field|component
specifier|public
specifier|final
name|Component
name|component
decl_stmt|;
DECL|field|componentIdCounter
specifier|public
name|AtomicLong
name|componentIdCounter
init|=
literal|null
decl_stmt|;
DECL|field|failedInstances
specifier|public
name|Queue
argument_list|<
name|RoleInstance
argument_list|>
name|failedInstances
init|=
operator|new
name|ConcurrentLinkedQueue
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|ProviderRole (String name, int id)
specifier|public
name|ProviderRole
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|id
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
name|id
argument_list|,
name|PlacementPolicy
operator|.
name|DEFAULT
argument_list|,
name|ResourceKeys
operator|.
name|DEFAULT_NODE_FAILURE_THRESHOLD
argument_list|,
name|ResourceKeys
operator|.
name|DEFAULT_PLACEMENT_ESCALATE_DELAY_SECONDS
argument_list|,
name|ResourceKeys
operator|.
name|DEF_YARN_LABEL_EXPRESSION
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a provider role    * @param name role/component name    * @param id ID. This becomes the YARN priority    * @param policy placement policy    * @param nodeFailureThreshold threshold for node failures (within a reset interval)    * after which a node failure is considered an app failure    * @param placementTimeoutSeconds for lax placement, timeout in seconds before    * @param labelExpression label expression for requests; may be null    */
DECL|method|ProviderRole (String name, int id, int policy, int nodeFailureThreshold, long placementTimeoutSeconds, String labelExpression)
specifier|public
name|ProviderRole
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|id
parameter_list|,
name|int
name|policy
parameter_list|,
name|int
name|nodeFailureThreshold
parameter_list|,
name|long
name|placementTimeoutSeconds
parameter_list|,
name|String
name|labelExpression
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
name|name
argument_list|,
name|id
argument_list|,
name|policy
argument_list|,
name|nodeFailureThreshold
argument_list|,
name|placementTimeoutSeconds
argument_list|,
name|labelExpression
argument_list|,
operator|new
name|Component
argument_list|()
operator|.
name|name
argument_list|(
name|name
argument_list|)
operator|.
name|numberOfContainers
argument_list|(
literal|0L
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a provider role with a role group    * @param name role/component name    * @param group role/component group    * @param id ID. This becomes the YARN priority    * @param policy placement policy    * @param nodeFailureThreshold threshold for node failures (within a reset interval)    * after which a node failure is considered an app failure    * @param placementTimeoutSeconds for lax placement, timeout in seconds before    * @param labelExpression label expression for requests; may be null    */
DECL|method|ProviderRole (String name, String group, int id, int policy, int nodeFailureThreshold, long placementTimeoutSeconds, String labelExpression, Component component)
specifier|public
name|ProviderRole
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|group
parameter_list|,
name|int
name|id
parameter_list|,
name|int
name|policy
parameter_list|,
name|int
name|nodeFailureThreshold
parameter_list|,
name|long
name|placementTimeoutSeconds
parameter_list|,
name|String
name|labelExpression
parameter_list|,
name|Component
name|component
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
if|if
condition|(
name|group
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|group
operator|=
name|name
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|group
operator|=
name|group
expr_stmt|;
block|}
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|placementPolicy
operator|=
name|policy
expr_stmt|;
name|this
operator|.
name|nodeFailureThreshold
operator|=
name|nodeFailureThreshold
expr_stmt|;
name|this
operator|.
name|placementTimeoutSeconds
operator|=
name|placementTimeoutSeconds
expr_stmt|;
name|this
operator|.
name|labelExpression
operator|=
name|labelExpression
expr_stmt|;
name|this
operator|.
name|component
operator|=
name|component
expr_stmt|;
if|if
condition|(
name|component
operator|.
name|getUniqueComponentSupport
argument_list|()
condition|)
block|{
name|componentIdCounter
operator|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|equals (Object o)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|ProviderRole
name|that
init|=
operator|(
name|ProviderRole
operator|)
name|o
decl_stmt|;
return|return
name|name
operator|.
name|equals
argument_list|(
name|that
operator|.
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|name
operator|.
name|hashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
specifier|final
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"ProviderRole{"
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"name='"
argument_list|)
operator|.
name|append
argument_list|(
name|name
argument_list|)
operator|.
name|append
argument_list|(
literal|'\''
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|", group="
argument_list|)
operator|.
name|append
argument_list|(
name|group
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|", id="
argument_list|)
operator|.
name|append
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|", placementPolicy="
argument_list|)
operator|.
name|append
argument_list|(
name|placementPolicy
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|", nodeFailureThreshold="
argument_list|)
operator|.
name|append
argument_list|(
name|nodeFailureThreshold
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|", placementTimeoutSeconds="
argument_list|)
operator|.
name|append
argument_list|(
name|placementTimeoutSeconds
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|", labelExpression='"
argument_list|)
operator|.
name|append
argument_list|(
name|labelExpression
argument_list|)
operator|.
name|append
argument_list|(
literal|'\''
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|'}'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

