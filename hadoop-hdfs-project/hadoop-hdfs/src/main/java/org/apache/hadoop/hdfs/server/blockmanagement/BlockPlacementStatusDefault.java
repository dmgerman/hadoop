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

begin_class
DECL|class|BlockPlacementStatusDefault
specifier|public
class|class
name|BlockPlacementStatusDefault
implements|implements
name|BlockPlacementStatus
block|{
DECL|field|requiredRacks
specifier|private
name|int
name|requiredRacks
init|=
literal|0
decl_stmt|;
DECL|field|currentRacks
specifier|private
name|int
name|currentRacks
init|=
literal|0
decl_stmt|;
DECL|field|totalRacks
specifier|private
specifier|final
name|int
name|totalRacks
decl_stmt|;
DECL|method|BlockPlacementStatusDefault (int currentRacks, int requiredRacks, int totalRacks)
specifier|public
name|BlockPlacementStatusDefault
parameter_list|(
name|int
name|currentRacks
parameter_list|,
name|int
name|requiredRacks
parameter_list|,
name|int
name|totalRacks
parameter_list|)
block|{
name|this
operator|.
name|requiredRacks
operator|=
name|requiredRacks
expr_stmt|;
name|this
operator|.
name|currentRacks
operator|=
name|currentRacks
expr_stmt|;
name|this
operator|.
name|totalRacks
operator|=
name|totalRacks
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
name|requiredRacks
operator|<=
name|currentRacks
operator|||
name|currentRacks
operator|>=
name|totalRacks
return|;
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
return|return
literal|"Block should be additionally replicated on "
operator|+
operator|(
name|requiredRacks
operator|-
name|currentRacks
operator|)
operator|+
literal|" more rack(s). Total number of racks in the cluster: "
operator|+
name|totalRacks
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
return|return
name|requiredRacks
operator|-
name|currentRacks
return|;
block|}
block|}
block|}
end_class

end_unit

