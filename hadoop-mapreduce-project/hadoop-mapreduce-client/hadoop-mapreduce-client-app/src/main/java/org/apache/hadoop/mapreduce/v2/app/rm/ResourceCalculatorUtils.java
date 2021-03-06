begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.app.rm
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|rm
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
name|proto
operator|.
name|YarnServiceProtos
operator|.
name|SchedulerResourceTypes
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|EnumSet
import|;
end_import

begin_class
DECL|class|ResourceCalculatorUtils
specifier|public
class|class
name|ResourceCalculatorUtils
block|{
DECL|method|divideAndCeil (long a, long b)
specifier|public
specifier|static
name|int
name|divideAndCeil
parameter_list|(
name|long
name|a
parameter_list|,
name|long
name|b
parameter_list|)
block|{
if|if
condition|(
name|b
operator|==
literal|0
condition|)
block|{
return|return
literal|0
return|;
block|}
return|return
call|(
name|int
call|)
argument_list|(
operator|(
name|a
operator|+
operator|(
name|b
operator|-
literal|1
operator|)
operator|)
operator|/
name|b
argument_list|)
return|;
block|}
DECL|method|computeAvailableContainers (Resource available, Resource required, EnumSet<SchedulerResourceTypes> resourceTypes)
specifier|public
specifier|static
name|int
name|computeAvailableContainers
parameter_list|(
name|Resource
name|available
parameter_list|,
name|Resource
name|required
parameter_list|,
name|EnumSet
argument_list|<
name|SchedulerResourceTypes
argument_list|>
name|resourceTypes
parameter_list|)
block|{
if|if
condition|(
name|resourceTypes
operator|.
name|contains
argument_list|(
name|SchedulerResourceTypes
operator|.
name|CPU
argument_list|)
condition|)
block|{
return|return
name|Math
operator|.
name|min
argument_list|(
name|calculateRatioOrMaxValue
argument_list|(
name|available
operator|.
name|getMemorySize
argument_list|()
argument_list|,
name|required
operator|.
name|getMemorySize
argument_list|()
argument_list|)
argument_list|,
name|calculateRatioOrMaxValue
argument_list|(
name|available
operator|.
name|getVirtualCores
argument_list|()
argument_list|,
name|required
operator|.
name|getVirtualCores
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
return|return
name|calculateRatioOrMaxValue
argument_list|(
name|available
operator|.
name|getMemorySize
argument_list|()
argument_list|,
name|required
operator|.
name|getMemorySize
argument_list|()
argument_list|)
return|;
block|}
DECL|method|divideAndCeilContainers (Resource required, Resource factor, EnumSet<SchedulerResourceTypes> resourceTypes)
specifier|public
specifier|static
name|int
name|divideAndCeilContainers
parameter_list|(
name|Resource
name|required
parameter_list|,
name|Resource
name|factor
parameter_list|,
name|EnumSet
argument_list|<
name|SchedulerResourceTypes
argument_list|>
name|resourceTypes
parameter_list|)
block|{
if|if
condition|(
name|resourceTypes
operator|.
name|contains
argument_list|(
name|SchedulerResourceTypes
operator|.
name|CPU
argument_list|)
condition|)
block|{
return|return
name|Math
operator|.
name|max
argument_list|(
name|divideAndCeil
argument_list|(
name|required
operator|.
name|getMemorySize
argument_list|()
argument_list|,
name|factor
operator|.
name|getMemorySize
argument_list|()
argument_list|)
argument_list|,
name|divideAndCeil
argument_list|(
name|required
operator|.
name|getVirtualCores
argument_list|()
argument_list|,
name|factor
operator|.
name|getVirtualCores
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
return|return
name|divideAndCeil
argument_list|(
name|required
operator|.
name|getMemorySize
argument_list|()
argument_list|,
name|factor
operator|.
name|getMemorySize
argument_list|()
argument_list|)
return|;
block|}
DECL|method|calculateRatioOrMaxValue (long numerator, long denominator)
specifier|private
specifier|static
name|int
name|calculateRatioOrMaxValue
parameter_list|(
name|long
name|numerator
parameter_list|,
name|long
name|denominator
parameter_list|)
block|{
if|if
condition|(
name|denominator
operator|==
literal|0
condition|)
block|{
return|return
name|Integer
operator|.
name|MAX_VALUE
return|;
block|}
return|return
call|(
name|int
call|)
argument_list|(
name|numerator
operator|/
name|denominator
argument_list|)
return|;
block|}
block|}
end_class

end_unit

