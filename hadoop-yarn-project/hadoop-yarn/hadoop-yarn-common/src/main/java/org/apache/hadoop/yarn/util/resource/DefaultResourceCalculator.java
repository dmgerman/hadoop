begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.util.resource
package|package
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
name|InterfaceStability
operator|.
name|Unstable
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

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|DefaultResourceCalculator
specifier|public
class|class
name|DefaultResourceCalculator
extends|extends
name|ResourceCalculator
block|{
annotation|@
name|Override
DECL|method|compare (Resource unused, Resource lhs, Resource rhs)
specifier|public
name|int
name|compare
parameter_list|(
name|Resource
name|unused
parameter_list|,
name|Resource
name|lhs
parameter_list|,
name|Resource
name|rhs
parameter_list|)
block|{
comment|// Only consider memory
return|return
name|lhs
operator|.
name|getMemory
argument_list|()
operator|-
name|rhs
operator|.
name|getMemory
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|computeAvailableContainers (Resource available, Resource required)
specifier|public
name|int
name|computeAvailableContainers
parameter_list|(
name|Resource
name|available
parameter_list|,
name|Resource
name|required
parameter_list|)
block|{
comment|// Only consider memory
return|return
name|available
operator|.
name|getMemory
argument_list|()
operator|/
name|required
operator|.
name|getMemory
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|divide (Resource unused, Resource numerator, Resource denominator)
specifier|public
name|float
name|divide
parameter_list|(
name|Resource
name|unused
parameter_list|,
name|Resource
name|numerator
parameter_list|,
name|Resource
name|denominator
parameter_list|)
block|{
return|return
name|ratio
argument_list|(
name|numerator
argument_list|,
name|denominator
argument_list|)
return|;
block|}
DECL|method|isInvalidDivisor (Resource r)
specifier|public
name|boolean
name|isInvalidDivisor
parameter_list|(
name|Resource
name|r
parameter_list|)
block|{
if|if
condition|(
name|r
operator|.
name|getMemory
argument_list|()
operator|==
literal|0.0f
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|ratio (Resource a, Resource b)
specifier|public
name|float
name|ratio
parameter_list|(
name|Resource
name|a
parameter_list|,
name|Resource
name|b
parameter_list|)
block|{
return|return
operator|(
name|float
operator|)
name|a
operator|.
name|getMemory
argument_list|()
operator|/
name|b
operator|.
name|getMemory
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|divideAndCeil (Resource numerator, int denominator)
specifier|public
name|Resource
name|divideAndCeil
parameter_list|(
name|Resource
name|numerator
parameter_list|,
name|int
name|denominator
parameter_list|)
block|{
return|return
name|Resources
operator|.
name|createResource
argument_list|(
name|divideAndCeil
argument_list|(
name|numerator
operator|.
name|getMemory
argument_list|()
argument_list|,
name|denominator
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|normalize (Resource r, Resource minimumResource, Resource maximumResource, Resource stepFactor)
specifier|public
name|Resource
name|normalize
parameter_list|(
name|Resource
name|r
parameter_list|,
name|Resource
name|minimumResource
parameter_list|,
name|Resource
name|maximumResource
parameter_list|,
name|Resource
name|stepFactor
parameter_list|)
block|{
name|int
name|normalizedMemory
init|=
name|Math
operator|.
name|min
argument_list|(
name|roundUp
argument_list|(
name|Math
operator|.
name|max
argument_list|(
name|r
operator|.
name|getMemory
argument_list|()
argument_list|,
name|minimumResource
operator|.
name|getMemory
argument_list|()
argument_list|)
argument_list|,
name|stepFactor
operator|.
name|getMemory
argument_list|()
argument_list|)
argument_list|,
name|maximumResource
operator|.
name|getMemory
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|Resources
operator|.
name|createResource
argument_list|(
name|normalizedMemory
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|normalize (Resource r, Resource minimumResource, Resource maximumResource)
specifier|public
name|Resource
name|normalize
parameter_list|(
name|Resource
name|r
parameter_list|,
name|Resource
name|minimumResource
parameter_list|,
name|Resource
name|maximumResource
parameter_list|)
block|{
return|return
name|normalize
argument_list|(
name|r
argument_list|,
name|minimumResource
argument_list|,
name|maximumResource
argument_list|,
name|minimumResource
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|roundUp (Resource r, Resource stepFactor)
specifier|public
name|Resource
name|roundUp
parameter_list|(
name|Resource
name|r
parameter_list|,
name|Resource
name|stepFactor
parameter_list|)
block|{
return|return
name|Resources
operator|.
name|createResource
argument_list|(
name|roundUp
argument_list|(
name|r
operator|.
name|getMemory
argument_list|()
argument_list|,
name|stepFactor
operator|.
name|getMemory
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|roundDown (Resource r, Resource stepFactor)
specifier|public
name|Resource
name|roundDown
parameter_list|(
name|Resource
name|r
parameter_list|,
name|Resource
name|stepFactor
parameter_list|)
block|{
return|return
name|Resources
operator|.
name|createResource
argument_list|(
name|roundDown
argument_list|(
name|r
operator|.
name|getMemory
argument_list|()
argument_list|,
name|stepFactor
operator|.
name|getMemory
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|multiplyAndNormalizeUp (Resource r, double by, Resource stepFactor)
specifier|public
name|Resource
name|multiplyAndNormalizeUp
parameter_list|(
name|Resource
name|r
parameter_list|,
name|double
name|by
parameter_list|,
name|Resource
name|stepFactor
parameter_list|)
block|{
return|return
name|Resources
operator|.
name|createResource
argument_list|(
name|roundUp
argument_list|(
call|(
name|int
call|)
argument_list|(
name|r
operator|.
name|getMemory
argument_list|()
operator|*
name|by
operator|+
literal|0.5
argument_list|)
argument_list|,
name|stepFactor
operator|.
name|getMemory
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|multiplyAndNormalizeDown (Resource r, double by, Resource stepFactor)
specifier|public
name|Resource
name|multiplyAndNormalizeDown
parameter_list|(
name|Resource
name|r
parameter_list|,
name|double
name|by
parameter_list|,
name|Resource
name|stepFactor
parameter_list|)
block|{
return|return
name|Resources
operator|.
name|createResource
argument_list|(
name|roundDown
argument_list|(
call|(
name|int
call|)
argument_list|(
name|r
operator|.
name|getMemory
argument_list|()
operator|*
name|by
argument_list|)
argument_list|,
name|stepFactor
operator|.
name|getMemory
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|fitsIn (Resource cluster, Resource smaller, Resource bigger)
specifier|public
name|boolean
name|fitsIn
parameter_list|(
name|Resource
name|cluster
parameter_list|,
name|Resource
name|smaller
parameter_list|,
name|Resource
name|bigger
parameter_list|)
block|{
return|return
name|smaller
operator|.
name|getMemory
argument_list|()
operator|<=
name|bigger
operator|.
name|getMemory
argument_list|()
return|;
block|}
block|}
end_class

end_unit

