begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.blockmanagement
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|blockmanagement
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
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  * An implementation of @see BlockPlacementStatus for  * @see BlockPlacementPolicyWithUpgradeDomain  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|BlockPlacementStatusWithUpgradeDomain
specifier|public
class|class
name|BlockPlacementStatusWithUpgradeDomain
implements|implements
name|BlockPlacementStatus
block|{
DECL|field|parentBlockPlacementStatus
specifier|private
specifier|final
name|BlockPlacementStatus
name|parentBlockPlacementStatus
decl_stmt|;
DECL|field|upgradeDomains
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|upgradeDomains
decl_stmt|;
DECL|field|numberOfReplicas
specifier|private
specifier|final
name|int
name|numberOfReplicas
decl_stmt|;
DECL|field|upgradeDomainFactor
specifier|private
specifier|final
name|int
name|upgradeDomainFactor
decl_stmt|;
comment|/**    * @param parentBlockPlacementStatus the parent class' status    * @param upgradeDomains the set of upgrade domains of the replicas    * @param numberOfReplicas the number of replicas of the block    * @param upgradeDomainFactor the configured upgrade domain factor    */
DECL|method|BlockPlacementStatusWithUpgradeDomain ( BlockPlacementStatus parentBlockPlacementStatus, Set<String> upgradeDomains, int numberOfReplicas, int upgradeDomainFactor)
specifier|public
name|BlockPlacementStatusWithUpgradeDomain
parameter_list|(
name|BlockPlacementStatus
name|parentBlockPlacementStatus
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|upgradeDomains
parameter_list|,
name|int
name|numberOfReplicas
parameter_list|,
name|int
name|upgradeDomainFactor
parameter_list|)
block|{
name|this
operator|.
name|parentBlockPlacementStatus
operator|=
name|parentBlockPlacementStatus
expr_stmt|;
name|this
operator|.
name|upgradeDomains
operator|=
name|upgradeDomains
expr_stmt|;
name|this
operator|.
name|numberOfReplicas
operator|=
name|numberOfReplicas
expr_stmt|;
name|this
operator|.
name|upgradeDomainFactor
operator|=
name|upgradeDomainFactor
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|isPlacementPolicySatisfied ()
specifier|public
name|boolean
name|isPlacementPolicySatisfied
parameter_list|()
block|{
return|return
name|parentBlockPlacementStatus
operator|.
name|isPlacementPolicySatisfied
argument_list|()
operator|&&
name|isUpgradeDomainPolicySatisfied
argument_list|()
return|;
block|}
DECL|method|isUpgradeDomainPolicySatisfied ()
specifier|private
name|boolean
name|isUpgradeDomainPolicySatisfied
parameter_list|()
block|{
if|if
condition|(
name|numberOfReplicas
operator|<=
name|upgradeDomainFactor
condition|)
block|{
return|return
operator|(
name|numberOfReplicas
operator|<=
name|upgradeDomains
operator|.
name|size
argument_list|()
operator|)
return|;
block|}
else|else
block|{
return|return
name|upgradeDomains
operator|.
name|size
argument_list|()
operator|>=
name|upgradeDomainFactor
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|getErrorDescription ()
specifier|public
name|String
name|getErrorDescription
parameter_list|()
block|{
if|if
condition|(
name|isPlacementPolicySatisfied
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|StringBuilder
name|errorDescription
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|parentBlockPlacementStatus
operator|.
name|isPlacementPolicySatisfied
argument_list|()
condition|)
block|{
name|errorDescription
operator|.
name|append
argument_list|(
name|parentBlockPlacementStatus
operator|.
name|getErrorDescription
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|isUpgradeDomainPolicySatisfied
argument_list|()
condition|)
block|{
if|if
condition|(
name|errorDescription
operator|.
name|length
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|errorDescription
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
block|}
name|errorDescription
operator|.
name|append
argument_list|(
literal|"The block has "
operator|+
name|numberOfReplicas
operator|+
literal|" replicas. But it only has "
operator|+
name|upgradeDomains
operator|.
name|size
argument_list|()
operator|+
literal|" upgrade domains "
operator|+
name|upgradeDomains
operator|+
literal|"."
argument_list|)
expr_stmt|;
block|}
return|return
name|errorDescription
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getAdditionalReplicasRequired ()
specifier|public
name|int
name|getAdditionalReplicasRequired
parameter_list|()
block|{
if|if
condition|(
name|isPlacementPolicySatisfied
argument_list|()
condition|)
block|{
return|return
literal|0
return|;
block|}
else|else
block|{
comment|// It is possible for a block to have the correct number of upgrade
comment|// domains, but only a single rack, or be on multiple racks, but only in
comment|// one upgrade domain.
name|int
name|parent
init|=
name|parentBlockPlacementStatus
operator|.
name|getAdditionalReplicasRequired
argument_list|()
decl_stmt|;
name|int
name|child
decl_stmt|;
if|if
condition|(
name|numberOfReplicas
operator|<=
name|upgradeDomainFactor
condition|)
block|{
name|child
operator|=
name|numberOfReplicas
operator|-
name|upgradeDomains
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|child
operator|=
name|upgradeDomainFactor
operator|-
name|upgradeDomains
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
return|return
name|Math
operator|.
name|max
argument_list|(
name|parent
argument_list|,
name|child
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

