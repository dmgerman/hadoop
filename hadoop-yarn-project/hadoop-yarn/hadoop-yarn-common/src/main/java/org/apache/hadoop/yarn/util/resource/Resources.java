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

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|LimitedPrivate
argument_list|(
block|{
literal|"YARN"
block|,
literal|"MapReduce"
block|}
argument_list|)
annotation|@
name|Unstable
DECL|class|Resources
specifier|public
class|class
name|Resources
block|{
comment|// Java doesn't have const :(
DECL|field|NONE
specifier|private
specifier|static
specifier|final
name|Resource
name|NONE
init|=
operator|new
name|Resource
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|getMemory
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setMemory
parameter_list|(
name|int
name|memory
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"NONE cannot be modified!"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getVirtualCores
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setVirtualCores
parameter_list|(
name|int
name|cores
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"NONE cannot be modified!"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|compareTo
parameter_list|(
name|Resource
name|o
parameter_list|)
block|{
name|int
name|diff
init|=
literal|0
operator|-
name|o
operator|.
name|getMemory
argument_list|()
decl_stmt|;
if|if
condition|(
name|diff
operator|==
literal|0
condition|)
block|{
name|diff
operator|=
literal|0
operator|-
name|o
operator|.
name|getVirtualCores
argument_list|()
expr_stmt|;
block|}
return|return
name|diff
return|;
block|}
block|}
decl_stmt|;
DECL|field|UNBOUNDED
specifier|private
specifier|static
specifier|final
name|Resource
name|UNBOUNDED
init|=
operator|new
name|Resource
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|getMemory
parameter_list|()
block|{
return|return
name|Integer
operator|.
name|MAX_VALUE
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setMemory
parameter_list|(
name|int
name|memory
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"NONE cannot be modified!"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getVirtualCores
parameter_list|()
block|{
return|return
name|Integer
operator|.
name|MAX_VALUE
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setVirtualCores
parameter_list|(
name|int
name|cores
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"NONE cannot be modified!"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|compareTo
parameter_list|(
name|Resource
name|o
parameter_list|)
block|{
name|int
name|diff
init|=
literal|0
operator|-
name|o
operator|.
name|getMemory
argument_list|()
decl_stmt|;
if|if
condition|(
name|diff
operator|==
literal|0
condition|)
block|{
name|diff
operator|=
literal|0
operator|-
name|o
operator|.
name|getVirtualCores
argument_list|()
expr_stmt|;
block|}
return|return
name|diff
return|;
block|}
block|}
decl_stmt|;
DECL|method|createResource (int memory)
specifier|public
specifier|static
name|Resource
name|createResource
parameter_list|(
name|int
name|memory
parameter_list|)
block|{
return|return
name|createResource
argument_list|(
name|memory
argument_list|,
operator|(
name|memory
operator|>
literal|0
operator|)
condition|?
literal|1
else|:
literal|0
argument_list|)
return|;
block|}
DECL|method|createResource (int memory, int cores)
specifier|public
specifier|static
name|Resource
name|createResource
parameter_list|(
name|int
name|memory
parameter_list|,
name|int
name|cores
parameter_list|)
block|{
name|Resource
name|resource
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|Resource
operator|.
name|class
argument_list|)
decl_stmt|;
name|resource
operator|.
name|setMemory
argument_list|(
name|memory
argument_list|)
expr_stmt|;
name|resource
operator|.
name|setVirtualCores
argument_list|(
name|cores
argument_list|)
expr_stmt|;
return|return
name|resource
return|;
block|}
DECL|method|none ()
specifier|public
specifier|static
name|Resource
name|none
parameter_list|()
block|{
return|return
name|NONE
return|;
block|}
DECL|method|unbounded ()
specifier|public
specifier|static
name|Resource
name|unbounded
parameter_list|()
block|{
return|return
name|UNBOUNDED
return|;
block|}
DECL|method|clone (Resource res)
specifier|public
specifier|static
name|Resource
name|clone
parameter_list|(
name|Resource
name|res
parameter_list|)
block|{
return|return
name|createResource
argument_list|(
name|res
operator|.
name|getMemory
argument_list|()
argument_list|,
name|res
operator|.
name|getVirtualCores
argument_list|()
argument_list|)
return|;
block|}
DECL|method|addTo (Resource lhs, Resource rhs)
specifier|public
specifier|static
name|Resource
name|addTo
parameter_list|(
name|Resource
name|lhs
parameter_list|,
name|Resource
name|rhs
parameter_list|)
block|{
name|lhs
operator|.
name|setMemory
argument_list|(
name|lhs
operator|.
name|getMemory
argument_list|()
operator|+
name|rhs
operator|.
name|getMemory
argument_list|()
argument_list|)
expr_stmt|;
name|lhs
operator|.
name|setVirtualCores
argument_list|(
name|lhs
operator|.
name|getVirtualCores
argument_list|()
operator|+
name|rhs
operator|.
name|getVirtualCores
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|lhs
return|;
block|}
DECL|method|add (Resource lhs, Resource rhs)
specifier|public
specifier|static
name|Resource
name|add
parameter_list|(
name|Resource
name|lhs
parameter_list|,
name|Resource
name|rhs
parameter_list|)
block|{
return|return
name|addTo
argument_list|(
name|clone
argument_list|(
name|lhs
argument_list|)
argument_list|,
name|rhs
argument_list|)
return|;
block|}
DECL|method|subtractFrom (Resource lhs, Resource rhs)
specifier|public
specifier|static
name|Resource
name|subtractFrom
parameter_list|(
name|Resource
name|lhs
parameter_list|,
name|Resource
name|rhs
parameter_list|)
block|{
name|lhs
operator|.
name|setMemory
argument_list|(
name|lhs
operator|.
name|getMemory
argument_list|()
operator|-
name|rhs
operator|.
name|getMemory
argument_list|()
argument_list|)
expr_stmt|;
name|lhs
operator|.
name|setVirtualCores
argument_list|(
name|lhs
operator|.
name|getVirtualCores
argument_list|()
operator|-
name|rhs
operator|.
name|getVirtualCores
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|lhs
return|;
block|}
DECL|method|subtract (Resource lhs, Resource rhs)
specifier|public
specifier|static
name|Resource
name|subtract
parameter_list|(
name|Resource
name|lhs
parameter_list|,
name|Resource
name|rhs
parameter_list|)
block|{
return|return
name|subtractFrom
argument_list|(
name|clone
argument_list|(
name|lhs
argument_list|)
argument_list|,
name|rhs
argument_list|)
return|;
block|}
DECL|method|negate (Resource resource)
specifier|public
specifier|static
name|Resource
name|negate
parameter_list|(
name|Resource
name|resource
parameter_list|)
block|{
return|return
name|subtract
argument_list|(
name|NONE
argument_list|,
name|resource
argument_list|)
return|;
block|}
DECL|method|multiplyTo (Resource lhs, double by)
specifier|public
specifier|static
name|Resource
name|multiplyTo
parameter_list|(
name|Resource
name|lhs
parameter_list|,
name|double
name|by
parameter_list|)
block|{
name|lhs
operator|.
name|setMemory
argument_list|(
call|(
name|int
call|)
argument_list|(
name|lhs
operator|.
name|getMemory
argument_list|()
operator|*
name|by
argument_list|)
argument_list|)
expr_stmt|;
name|lhs
operator|.
name|setVirtualCores
argument_list|(
call|(
name|int
call|)
argument_list|(
name|lhs
operator|.
name|getVirtualCores
argument_list|()
operator|*
name|by
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|lhs
return|;
block|}
DECL|method|multiply (Resource lhs, double by)
specifier|public
specifier|static
name|Resource
name|multiply
parameter_list|(
name|Resource
name|lhs
parameter_list|,
name|double
name|by
parameter_list|)
block|{
return|return
name|multiplyTo
argument_list|(
name|clone
argument_list|(
name|lhs
argument_list|)
argument_list|,
name|by
argument_list|)
return|;
block|}
DECL|method|multiplyAndNormalizeUp ( ResourceCalculator calculator,Resource lhs, double by, Resource factor)
specifier|public
specifier|static
name|Resource
name|multiplyAndNormalizeUp
parameter_list|(
name|ResourceCalculator
name|calculator
parameter_list|,
name|Resource
name|lhs
parameter_list|,
name|double
name|by
parameter_list|,
name|Resource
name|factor
parameter_list|)
block|{
return|return
name|calculator
operator|.
name|multiplyAndNormalizeUp
argument_list|(
name|lhs
argument_list|,
name|by
argument_list|,
name|factor
argument_list|)
return|;
block|}
DECL|method|multiplyAndNormalizeDown ( ResourceCalculator calculator,Resource lhs, double by, Resource factor)
specifier|public
specifier|static
name|Resource
name|multiplyAndNormalizeDown
parameter_list|(
name|ResourceCalculator
name|calculator
parameter_list|,
name|Resource
name|lhs
parameter_list|,
name|double
name|by
parameter_list|,
name|Resource
name|factor
parameter_list|)
block|{
return|return
name|calculator
operator|.
name|multiplyAndNormalizeDown
argument_list|(
name|lhs
argument_list|,
name|by
argument_list|,
name|factor
argument_list|)
return|;
block|}
DECL|method|multiplyAndRoundDown (Resource lhs, double by)
specifier|public
specifier|static
name|Resource
name|multiplyAndRoundDown
parameter_list|(
name|Resource
name|lhs
parameter_list|,
name|double
name|by
parameter_list|)
block|{
name|Resource
name|out
init|=
name|clone
argument_list|(
name|lhs
argument_list|)
decl_stmt|;
name|out
operator|.
name|setMemory
argument_list|(
call|(
name|int
call|)
argument_list|(
name|lhs
operator|.
name|getMemory
argument_list|()
operator|*
name|by
argument_list|)
argument_list|)
expr_stmt|;
name|out
operator|.
name|setVirtualCores
argument_list|(
call|(
name|int
call|)
argument_list|(
name|lhs
operator|.
name|getVirtualCores
argument_list|()
operator|*
name|by
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|out
return|;
block|}
DECL|method|normalize ( ResourceCalculator calculator, Resource lhs, Resource min, Resource max, Resource increment)
specifier|public
specifier|static
name|Resource
name|normalize
parameter_list|(
name|ResourceCalculator
name|calculator
parameter_list|,
name|Resource
name|lhs
parameter_list|,
name|Resource
name|min
parameter_list|,
name|Resource
name|max
parameter_list|,
name|Resource
name|increment
parameter_list|)
block|{
return|return
name|calculator
operator|.
name|normalize
argument_list|(
name|lhs
argument_list|,
name|min
argument_list|,
name|max
argument_list|,
name|increment
argument_list|)
return|;
block|}
DECL|method|roundUp ( ResourceCalculator calculator, Resource lhs, Resource factor)
specifier|public
specifier|static
name|Resource
name|roundUp
parameter_list|(
name|ResourceCalculator
name|calculator
parameter_list|,
name|Resource
name|lhs
parameter_list|,
name|Resource
name|factor
parameter_list|)
block|{
return|return
name|calculator
operator|.
name|roundUp
argument_list|(
name|lhs
argument_list|,
name|factor
argument_list|)
return|;
block|}
DECL|method|roundDown ( ResourceCalculator calculator, Resource lhs, Resource factor)
specifier|public
specifier|static
name|Resource
name|roundDown
parameter_list|(
name|ResourceCalculator
name|calculator
parameter_list|,
name|Resource
name|lhs
parameter_list|,
name|Resource
name|factor
parameter_list|)
block|{
return|return
name|calculator
operator|.
name|roundDown
argument_list|(
name|lhs
argument_list|,
name|factor
argument_list|)
return|;
block|}
DECL|method|isInvalidDivisor ( ResourceCalculator resourceCalculator, Resource divisor)
specifier|public
specifier|static
name|boolean
name|isInvalidDivisor
parameter_list|(
name|ResourceCalculator
name|resourceCalculator
parameter_list|,
name|Resource
name|divisor
parameter_list|)
block|{
return|return
name|resourceCalculator
operator|.
name|isInvalidDivisor
argument_list|(
name|divisor
argument_list|)
return|;
block|}
DECL|method|ratio ( ResourceCalculator resourceCalculator, Resource lhs, Resource rhs)
specifier|public
specifier|static
name|float
name|ratio
parameter_list|(
name|ResourceCalculator
name|resourceCalculator
parameter_list|,
name|Resource
name|lhs
parameter_list|,
name|Resource
name|rhs
parameter_list|)
block|{
return|return
name|resourceCalculator
operator|.
name|ratio
argument_list|(
name|lhs
argument_list|,
name|rhs
argument_list|)
return|;
block|}
DECL|method|divide ( ResourceCalculator resourceCalculator, Resource clusterResource, Resource lhs, Resource rhs)
specifier|public
specifier|static
name|float
name|divide
parameter_list|(
name|ResourceCalculator
name|resourceCalculator
parameter_list|,
name|Resource
name|clusterResource
parameter_list|,
name|Resource
name|lhs
parameter_list|,
name|Resource
name|rhs
parameter_list|)
block|{
return|return
name|resourceCalculator
operator|.
name|divide
argument_list|(
name|clusterResource
argument_list|,
name|lhs
argument_list|,
name|rhs
argument_list|)
return|;
block|}
DECL|method|divideAndCeil ( ResourceCalculator resourceCalculator, Resource lhs, int rhs)
specifier|public
specifier|static
name|Resource
name|divideAndCeil
parameter_list|(
name|ResourceCalculator
name|resourceCalculator
parameter_list|,
name|Resource
name|lhs
parameter_list|,
name|int
name|rhs
parameter_list|)
block|{
return|return
name|resourceCalculator
operator|.
name|divideAndCeil
argument_list|(
name|lhs
argument_list|,
name|rhs
argument_list|)
return|;
block|}
DECL|method|equals (Resource lhs, Resource rhs)
specifier|public
specifier|static
name|boolean
name|equals
parameter_list|(
name|Resource
name|lhs
parameter_list|,
name|Resource
name|rhs
parameter_list|)
block|{
return|return
name|lhs
operator|.
name|equals
argument_list|(
name|rhs
argument_list|)
return|;
block|}
DECL|method|lessThan ( ResourceCalculator resourceCalculator, Resource clusterResource, Resource lhs, Resource rhs)
specifier|public
specifier|static
name|boolean
name|lessThan
parameter_list|(
name|ResourceCalculator
name|resourceCalculator
parameter_list|,
name|Resource
name|clusterResource
parameter_list|,
name|Resource
name|lhs
parameter_list|,
name|Resource
name|rhs
parameter_list|)
block|{
return|return
operator|(
name|resourceCalculator
operator|.
name|compare
argument_list|(
name|clusterResource
argument_list|,
name|lhs
argument_list|,
name|rhs
argument_list|)
operator|<
literal|0
operator|)
return|;
block|}
DECL|method|lessThanOrEqual ( ResourceCalculator resourceCalculator, Resource clusterResource, Resource lhs, Resource rhs)
specifier|public
specifier|static
name|boolean
name|lessThanOrEqual
parameter_list|(
name|ResourceCalculator
name|resourceCalculator
parameter_list|,
name|Resource
name|clusterResource
parameter_list|,
name|Resource
name|lhs
parameter_list|,
name|Resource
name|rhs
parameter_list|)
block|{
return|return
operator|(
name|resourceCalculator
operator|.
name|compare
argument_list|(
name|clusterResource
argument_list|,
name|lhs
argument_list|,
name|rhs
argument_list|)
operator|<=
literal|0
operator|)
return|;
block|}
DECL|method|greaterThan ( ResourceCalculator resourceCalculator, Resource clusterResource, Resource lhs, Resource rhs)
specifier|public
specifier|static
name|boolean
name|greaterThan
parameter_list|(
name|ResourceCalculator
name|resourceCalculator
parameter_list|,
name|Resource
name|clusterResource
parameter_list|,
name|Resource
name|lhs
parameter_list|,
name|Resource
name|rhs
parameter_list|)
block|{
return|return
name|resourceCalculator
operator|.
name|compare
argument_list|(
name|clusterResource
argument_list|,
name|lhs
argument_list|,
name|rhs
argument_list|)
operator|>
literal|0
return|;
block|}
DECL|method|greaterThanOrEqual ( ResourceCalculator resourceCalculator, Resource clusterResource, Resource lhs, Resource rhs)
specifier|public
specifier|static
name|boolean
name|greaterThanOrEqual
parameter_list|(
name|ResourceCalculator
name|resourceCalculator
parameter_list|,
name|Resource
name|clusterResource
parameter_list|,
name|Resource
name|lhs
parameter_list|,
name|Resource
name|rhs
parameter_list|)
block|{
return|return
name|resourceCalculator
operator|.
name|compare
argument_list|(
name|clusterResource
argument_list|,
name|lhs
argument_list|,
name|rhs
argument_list|)
operator|>=
literal|0
return|;
block|}
DECL|method|min ( ResourceCalculator resourceCalculator, Resource clusterResource, Resource lhs, Resource rhs)
specifier|public
specifier|static
name|Resource
name|min
parameter_list|(
name|ResourceCalculator
name|resourceCalculator
parameter_list|,
name|Resource
name|clusterResource
parameter_list|,
name|Resource
name|lhs
parameter_list|,
name|Resource
name|rhs
parameter_list|)
block|{
return|return
name|resourceCalculator
operator|.
name|compare
argument_list|(
name|clusterResource
argument_list|,
name|lhs
argument_list|,
name|rhs
argument_list|)
operator|<=
literal|0
condition|?
name|lhs
else|:
name|rhs
return|;
block|}
DECL|method|max ( ResourceCalculator resourceCalculator, Resource clusterResource, Resource lhs, Resource rhs)
specifier|public
specifier|static
name|Resource
name|max
parameter_list|(
name|ResourceCalculator
name|resourceCalculator
parameter_list|,
name|Resource
name|clusterResource
parameter_list|,
name|Resource
name|lhs
parameter_list|,
name|Resource
name|rhs
parameter_list|)
block|{
return|return
name|resourceCalculator
operator|.
name|compare
argument_list|(
name|clusterResource
argument_list|,
name|lhs
argument_list|,
name|rhs
argument_list|)
operator|>=
literal|0
condition|?
name|lhs
else|:
name|rhs
return|;
block|}
DECL|method|fitsIn (Resource smaller, Resource bigger)
specifier|public
specifier|static
name|boolean
name|fitsIn
parameter_list|(
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
DECL|method|componentwiseMin (Resource lhs, Resource rhs)
specifier|public
specifier|static
name|Resource
name|componentwiseMin
parameter_list|(
name|Resource
name|lhs
parameter_list|,
name|Resource
name|rhs
parameter_list|)
block|{
return|return
name|createResource
argument_list|(
name|Math
operator|.
name|min
argument_list|(
name|lhs
operator|.
name|getMemory
argument_list|()
argument_list|,
name|rhs
operator|.
name|getMemory
argument_list|()
argument_list|)
argument_list|,
name|Math
operator|.
name|min
argument_list|(
name|lhs
operator|.
name|getVirtualCores
argument_list|()
argument_list|,
name|rhs
operator|.
name|getVirtualCores
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
DECL|method|componentwiseMax (Resource lhs, Resource rhs)
specifier|public
specifier|static
name|Resource
name|componentwiseMax
parameter_list|(
name|Resource
name|lhs
parameter_list|,
name|Resource
name|rhs
parameter_list|)
block|{
return|return
name|createResource
argument_list|(
name|Math
operator|.
name|max
argument_list|(
name|lhs
operator|.
name|getMemory
argument_list|()
argument_list|,
name|rhs
operator|.
name|getMemory
argument_list|()
argument_list|)
argument_list|,
name|Math
operator|.
name|max
argument_list|(
name|lhs
operator|.
name|getVirtualCores
argument_list|()
argument_list|,
name|rhs
operator|.
name|getVirtualCores
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

