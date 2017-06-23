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
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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

begin_comment
comment|/**  * A {@link ResourceCalculator} which uses the concept of    *<em>dominant resource</em> to compare multi-dimensional resources.  *  * Essentially the idea is that the in a multi-resource environment,   * the resource allocation should be determined by the dominant share   * of an entity (user or queue), which is the maximum share that the   * entity has been allocated of any resource.   *   * In a nutshell, it seeks to maximize the minimum dominant share across   * all entities.   *   * For example, if user A runs CPU-heavy tasks and user B runs  * memory-heavy tasks, it attempts to equalize CPU share of user A   * with Memory-share of user B.   *   * In the single resource case, it reduces to max-min fairness for that resource.  *   * See the Dominant Resource Fairness paper for more details:  * www.cs.berkeley.edu/~matei/papers/2011/nsdi_drf.pdf  */
end_comment

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|DominantResourceCalculator
specifier|public
class|class
name|DominantResourceCalculator
extends|extends
name|ResourceCalculator
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|DominantResourceCalculator
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Override
DECL|method|compare (Resource clusterResource, Resource lhs, Resource rhs, boolean singleType)
specifier|public
name|int
name|compare
parameter_list|(
name|Resource
name|clusterResource
parameter_list|,
name|Resource
name|lhs
parameter_list|,
name|Resource
name|rhs
parameter_list|,
name|boolean
name|singleType
parameter_list|)
block|{
if|if
condition|(
name|lhs
operator|.
name|equals
argument_list|(
name|rhs
argument_list|)
condition|)
block|{
return|return
literal|0
return|;
block|}
if|if
condition|(
name|isInvalidDivisor
argument_list|(
name|clusterResource
argument_list|)
condition|)
block|{
if|if
condition|(
operator|(
name|lhs
operator|.
name|getMemorySize
argument_list|()
operator|<
name|rhs
operator|.
name|getMemorySize
argument_list|()
operator|&&
name|lhs
operator|.
name|getVirtualCores
argument_list|()
operator|>
name|rhs
operator|.
name|getVirtualCores
argument_list|()
operator|)
operator|||
operator|(
name|lhs
operator|.
name|getMemorySize
argument_list|()
operator|>
name|rhs
operator|.
name|getMemorySize
argument_list|()
operator|&&
name|lhs
operator|.
name|getVirtualCores
argument_list|()
operator|<
name|rhs
operator|.
name|getVirtualCores
argument_list|()
operator|)
condition|)
block|{
return|return
literal|0
return|;
block|}
elseif|else
if|if
condition|(
name|lhs
operator|.
name|getMemorySize
argument_list|()
operator|>
name|rhs
operator|.
name|getMemorySize
argument_list|()
operator|||
name|lhs
operator|.
name|getVirtualCores
argument_list|()
operator|>
name|rhs
operator|.
name|getVirtualCores
argument_list|()
condition|)
block|{
return|return
literal|1
return|;
block|}
elseif|else
if|if
condition|(
name|lhs
operator|.
name|getMemorySize
argument_list|()
operator|<
name|rhs
operator|.
name|getMemorySize
argument_list|()
operator|||
name|lhs
operator|.
name|getVirtualCores
argument_list|()
operator|<
name|rhs
operator|.
name|getVirtualCores
argument_list|()
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
block|}
name|float
name|l
init|=
name|getResourceAsValue
argument_list|(
name|clusterResource
argument_list|,
name|lhs
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|float
name|r
init|=
name|getResourceAsValue
argument_list|(
name|clusterResource
argument_list|,
name|rhs
argument_list|,
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
name|l
operator|<
name|r
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
elseif|else
if|if
condition|(
name|l
operator|>
name|r
condition|)
block|{
return|return
literal|1
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|singleType
condition|)
block|{
name|l
operator|=
name|getResourceAsValue
argument_list|(
name|clusterResource
argument_list|,
name|lhs
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|r
operator|=
name|getResourceAsValue
argument_list|(
name|clusterResource
argument_list|,
name|rhs
argument_list|,
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|l
operator|<
name|r
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
elseif|else
if|if
condition|(
name|l
operator|>
name|r
condition|)
block|{
return|return
literal|1
return|;
block|}
block|}
return|return
literal|0
return|;
block|}
comment|/**    * Use 'dominant' for now since we only have 2 resources - gives us a slight    * performance boost.    *     * Once we add more resources, we'll need a more complicated (and slightly    * less performant algorithm).    */
DECL|method|getResourceAsValue ( Resource clusterResource, Resource resource, boolean dominant)
specifier|protected
name|float
name|getResourceAsValue
parameter_list|(
name|Resource
name|clusterResource
parameter_list|,
name|Resource
name|resource
parameter_list|,
name|boolean
name|dominant
parameter_list|)
block|{
comment|// Just use 'dominant' resource
return|return
operator|(
name|dominant
operator|)
condition|?
name|Math
operator|.
name|max
argument_list|(
operator|(
name|float
operator|)
name|resource
operator|.
name|getMemorySize
argument_list|()
operator|/
name|clusterResource
operator|.
name|getMemorySize
argument_list|()
argument_list|,
operator|(
name|float
operator|)
name|resource
operator|.
name|getVirtualCores
argument_list|()
operator|/
name|clusterResource
operator|.
name|getVirtualCores
argument_list|()
argument_list|)
else|:
name|Math
operator|.
name|min
argument_list|(
operator|(
name|float
operator|)
name|resource
operator|.
name|getMemorySize
argument_list|()
operator|/
name|clusterResource
operator|.
name|getMemorySize
argument_list|()
argument_list|,
operator|(
name|float
operator|)
name|resource
operator|.
name|getVirtualCores
argument_list|()
operator|/
name|clusterResource
operator|.
name|getVirtualCores
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|computeAvailableContainers (Resource available, Resource required)
specifier|public
name|long
name|computeAvailableContainers
parameter_list|(
name|Resource
name|available
parameter_list|,
name|Resource
name|required
parameter_list|)
block|{
return|return
name|Math
operator|.
name|min
argument_list|(
name|available
operator|.
name|getMemorySize
argument_list|()
operator|/
name|required
operator|.
name|getMemorySize
argument_list|()
argument_list|,
name|available
operator|.
name|getVirtualCores
argument_list|()
operator|/
name|required
operator|.
name|getVirtualCores
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|divide (Resource clusterResource, Resource numerator, Resource denominator)
specifier|public
name|float
name|divide
parameter_list|(
name|Resource
name|clusterResource
parameter_list|,
name|Resource
name|numerator
parameter_list|,
name|Resource
name|denominator
parameter_list|)
block|{
return|return
name|getResourceAsValue
argument_list|(
name|clusterResource
argument_list|,
name|numerator
argument_list|,
literal|true
argument_list|)
operator|/
name|getResourceAsValue
argument_list|(
name|clusterResource
argument_list|,
name|denominator
argument_list|,
literal|true
argument_list|)
return|;
block|}
annotation|@
name|Override
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
name|getMemorySize
argument_list|()
operator|==
literal|0.0f
operator|||
name|r
operator|.
name|getVirtualCores
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
name|Math
operator|.
name|max
argument_list|(
operator|(
name|float
operator|)
name|a
operator|.
name|getMemorySize
argument_list|()
operator|/
name|b
operator|.
name|getMemorySize
argument_list|()
argument_list|,
operator|(
name|float
operator|)
name|a
operator|.
name|getVirtualCores
argument_list|()
operator|/
name|b
operator|.
name|getVirtualCores
argument_list|()
argument_list|)
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
name|getMemorySize
argument_list|()
argument_list|,
name|denominator
argument_list|)
argument_list|,
name|divideAndCeil
argument_list|(
name|numerator
operator|.
name|getVirtualCores
argument_list|()
argument_list|,
name|denominator
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|divideAndCeil (Resource numerator, float denominator)
specifier|public
name|Resource
name|divideAndCeil
parameter_list|(
name|Resource
name|numerator
parameter_list|,
name|float
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
name|getMemorySize
argument_list|()
argument_list|,
name|denominator
argument_list|)
argument_list|,
name|divideAndCeil
argument_list|(
name|numerator
operator|.
name|getVirtualCores
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
if|if
condition|(
name|stepFactor
operator|.
name|getMemorySize
argument_list|()
operator|==
literal|0
operator|||
name|stepFactor
operator|.
name|getVirtualCores
argument_list|()
operator|==
literal|0
condition|)
block|{
name|Resource
name|step
init|=
name|Resources
operator|.
name|clone
argument_list|(
name|stepFactor
argument_list|)
decl_stmt|;
if|if
condition|(
name|stepFactor
operator|.
name|getMemorySize
argument_list|()
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Memory cannot be allocated in increments of zero. Assuming "
operator|+
name|minimumResource
operator|.
name|getMemorySize
argument_list|()
operator|+
literal|"MB increment size. "
operator|+
literal|"Please ensure the scheduler configuration is correct."
argument_list|)
expr_stmt|;
name|step
operator|.
name|setMemorySize
argument_list|(
name|minimumResource
operator|.
name|getMemorySize
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|stepFactor
operator|.
name|getVirtualCores
argument_list|()
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"VCore cannot be allocated in increments of zero. Assuming "
operator|+
name|minimumResource
operator|.
name|getVirtualCores
argument_list|()
operator|+
literal|"VCores increment size. "
operator|+
literal|"Please ensure the scheduler configuration is correct."
argument_list|)
expr_stmt|;
name|step
operator|.
name|setVirtualCores
argument_list|(
name|minimumResource
operator|.
name|getVirtualCores
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|stepFactor
operator|=
name|step
expr_stmt|;
block|}
name|long
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
name|getMemorySize
argument_list|()
argument_list|,
name|minimumResource
operator|.
name|getMemorySize
argument_list|()
argument_list|)
argument_list|,
name|stepFactor
operator|.
name|getMemorySize
argument_list|()
argument_list|)
argument_list|,
name|maximumResource
operator|.
name|getMemorySize
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|normalizedCores
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
name|getVirtualCores
argument_list|()
argument_list|,
name|minimumResource
operator|.
name|getVirtualCores
argument_list|()
argument_list|)
argument_list|,
name|stepFactor
operator|.
name|getVirtualCores
argument_list|()
argument_list|)
argument_list|,
name|maximumResource
operator|.
name|getVirtualCores
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|Resources
operator|.
name|createResource
argument_list|(
name|normalizedMemory
argument_list|,
name|normalizedCores
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
name|getMemorySize
argument_list|()
argument_list|,
name|stepFactor
operator|.
name|getMemorySize
argument_list|()
argument_list|)
argument_list|,
name|roundUp
argument_list|(
name|r
operator|.
name|getVirtualCores
argument_list|()
argument_list|,
name|stepFactor
operator|.
name|getVirtualCores
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
name|getMemorySize
argument_list|()
argument_list|,
name|stepFactor
operator|.
name|getMemorySize
argument_list|()
argument_list|)
argument_list|,
name|roundDown
argument_list|(
name|r
operator|.
name|getVirtualCores
argument_list|()
argument_list|,
name|stepFactor
operator|.
name|getVirtualCores
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
operator|(
name|int
operator|)
name|Math
operator|.
name|ceil
argument_list|(
name|r
operator|.
name|getMemorySize
argument_list|()
operator|*
name|by
argument_list|)
argument_list|,
name|stepFactor
operator|.
name|getMemorySize
argument_list|()
argument_list|)
argument_list|,
name|roundUp
argument_list|(
operator|(
name|int
operator|)
name|Math
operator|.
name|ceil
argument_list|(
name|r
operator|.
name|getVirtualCores
argument_list|()
operator|*
name|by
argument_list|)
argument_list|,
name|stepFactor
operator|.
name|getVirtualCores
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
name|getMemorySize
argument_list|()
operator|*
name|by
argument_list|)
argument_list|,
name|stepFactor
operator|.
name|getMemorySize
argument_list|()
argument_list|)
argument_list|,
name|roundDown
argument_list|(
call|(
name|int
call|)
argument_list|(
name|r
operator|.
name|getVirtualCores
argument_list|()
operator|*
name|by
argument_list|)
argument_list|,
name|stepFactor
operator|.
name|getVirtualCores
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
name|getMemorySize
argument_list|()
operator|<=
name|bigger
operator|.
name|getMemorySize
argument_list|()
operator|&&
name|smaller
operator|.
name|getVirtualCores
argument_list|()
operator|<=
name|bigger
operator|.
name|getVirtualCores
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|isAnyMajorResourceZero (Resource resource)
specifier|public
name|boolean
name|isAnyMajorResourceZero
parameter_list|(
name|Resource
name|resource
parameter_list|)
block|{
return|return
name|resource
operator|.
name|getMemorySize
argument_list|()
operator|==
literal|0f
operator|||
name|resource
operator|.
name|getVirtualCores
argument_list|()
operator|==
literal|0
return|;
block|}
block|}
end_class

end_unit

