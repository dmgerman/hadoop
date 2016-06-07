begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.reservation
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
name|reservation
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|NavigableMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
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
name|locks
operator|.
name|Lock
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
name|locks
operator|.
name|ReentrantReadWriteLock
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
name|reservation
operator|.
name|exceptions
operator|.
name|PlanningException
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
name|Resources
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gson
operator|.
name|stream
operator|.
name|JsonWriter
import|;
end_import

begin_comment
comment|/**  * This is a run length encoded sparse data structure that maintains resource  * allocations over time.  */
end_comment

begin_class
DECL|class|RLESparseResourceAllocation
specifier|public
class|class
name|RLESparseResourceAllocation
block|{
DECL|field|THRESHOLD
specifier|private
specifier|static
specifier|final
name|int
name|THRESHOLD
init|=
literal|100
decl_stmt|;
DECL|field|ZERO_RESOURCE
specifier|private
specifier|static
specifier|final
name|Resource
name|ZERO_RESOURCE
init|=
name|Resources
operator|.
name|none
argument_list|()
decl_stmt|;
DECL|field|cumulativeCapacity
specifier|private
name|NavigableMap
argument_list|<
name|Long
argument_list|,
name|Resource
argument_list|>
name|cumulativeCapacity
init|=
operator|new
name|TreeMap
argument_list|<
name|Long
argument_list|,
name|Resource
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|readWriteLock
specifier|private
specifier|final
name|ReentrantReadWriteLock
name|readWriteLock
init|=
operator|new
name|ReentrantReadWriteLock
argument_list|()
decl_stmt|;
DECL|field|readLock
specifier|private
specifier|final
name|Lock
name|readLock
init|=
name|readWriteLock
operator|.
name|readLock
argument_list|()
decl_stmt|;
DECL|field|writeLock
specifier|private
specifier|final
name|Lock
name|writeLock
init|=
name|readWriteLock
operator|.
name|writeLock
argument_list|()
decl_stmt|;
DECL|field|resourceCalculator
specifier|private
specifier|final
name|ResourceCalculator
name|resourceCalculator
decl_stmt|;
DECL|method|RLESparseResourceAllocation (ResourceCalculator resourceCalculator)
specifier|public
name|RLESparseResourceAllocation
parameter_list|(
name|ResourceCalculator
name|resourceCalculator
parameter_list|)
block|{
name|this
operator|.
name|resourceCalculator
operator|=
name|resourceCalculator
expr_stmt|;
block|}
DECL|method|RLESparseResourceAllocation (NavigableMap<Long, Resource> out, ResourceCalculator resourceCalculator)
specifier|public
name|RLESparseResourceAllocation
parameter_list|(
name|NavigableMap
argument_list|<
name|Long
argument_list|,
name|Resource
argument_list|>
name|out
parameter_list|,
name|ResourceCalculator
name|resourceCalculator
parameter_list|)
block|{
comment|// miss check for repeated entries
name|this
operator|.
name|cumulativeCapacity
operator|=
name|out
expr_stmt|;
name|this
operator|.
name|resourceCalculator
operator|=
name|resourceCalculator
expr_stmt|;
block|}
comment|/**    * Add a resource for the specified interval.    *    * @param reservationInterval the interval for which the resource is to be    *          added    * @param totCap the resource to be added    * @return true if addition is successful, false otherwise    */
DECL|method|addInterval (ReservationInterval reservationInterval, Resource totCap)
specifier|public
name|boolean
name|addInterval
parameter_list|(
name|ReservationInterval
name|reservationInterval
parameter_list|,
name|Resource
name|totCap
parameter_list|)
block|{
if|if
condition|(
name|totCap
operator|.
name|equals
argument_list|(
name|ZERO_RESOURCE
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
name|writeLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|NavigableMap
argument_list|<
name|Long
argument_list|,
name|Resource
argument_list|>
name|addInt
init|=
operator|new
name|TreeMap
argument_list|<
name|Long
argument_list|,
name|Resource
argument_list|>
argument_list|()
decl_stmt|;
name|addInt
operator|.
name|put
argument_list|(
name|reservationInterval
operator|.
name|getStartTime
argument_list|()
argument_list|,
name|totCap
argument_list|)
expr_stmt|;
name|addInt
operator|.
name|put
argument_list|(
name|reservationInterval
operator|.
name|getEndTime
argument_list|()
argument_list|,
name|ZERO_RESOURCE
argument_list|)
expr_stmt|;
try|try
block|{
name|cumulativeCapacity
operator|=
name|merge
argument_list|(
name|resourceCalculator
argument_list|,
name|totCap
argument_list|,
name|cumulativeCapacity
argument_list|,
name|addInt
argument_list|,
name|Long
operator|.
name|MIN_VALUE
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|,
name|RLEOperator
operator|.
name|add
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PlanningException
name|e
parameter_list|)
block|{
comment|// never happens for add
block|}
return|return
literal|true
return|;
block|}
finally|finally
block|{
name|writeLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Removes a resource for the specified interval.    *    * @param reservationInterval the interval for which the resource is to be    *          removed    * @param totCap the resource to be removed    * @return true if removal is successful, false otherwise    */
DECL|method|removeInterval (ReservationInterval reservationInterval, Resource totCap)
specifier|public
name|boolean
name|removeInterval
parameter_list|(
name|ReservationInterval
name|reservationInterval
parameter_list|,
name|Resource
name|totCap
parameter_list|)
block|{
if|if
condition|(
name|totCap
operator|.
name|equals
argument_list|(
name|ZERO_RESOURCE
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
name|writeLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|NavigableMap
argument_list|<
name|Long
argument_list|,
name|Resource
argument_list|>
name|removeInt
init|=
operator|new
name|TreeMap
argument_list|<
name|Long
argument_list|,
name|Resource
argument_list|>
argument_list|()
decl_stmt|;
name|removeInt
operator|.
name|put
argument_list|(
name|reservationInterval
operator|.
name|getStartTime
argument_list|()
argument_list|,
name|totCap
argument_list|)
expr_stmt|;
name|removeInt
operator|.
name|put
argument_list|(
name|reservationInterval
operator|.
name|getEndTime
argument_list|()
argument_list|,
name|ZERO_RESOURCE
argument_list|)
expr_stmt|;
try|try
block|{
name|cumulativeCapacity
operator|=
name|merge
argument_list|(
name|resourceCalculator
argument_list|,
name|totCap
argument_list|,
name|cumulativeCapacity
argument_list|,
name|removeInt
argument_list|,
name|Long
operator|.
name|MIN_VALUE
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|,
name|RLEOperator
operator|.
name|subtract
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PlanningException
name|e
parameter_list|)
block|{
comment|// never happens for subtract
block|}
return|return
literal|true
return|;
block|}
finally|finally
block|{
name|writeLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Returns the capacity, i.e. total resources allocated at the specified point    * of time.    *    * @return the resources allocated at the specified time    */
DECL|method|getCapacityAtTime (long tick)
specifier|public
name|Resource
name|getCapacityAtTime
parameter_list|(
name|long
name|tick
parameter_list|)
block|{
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|Entry
argument_list|<
name|Long
argument_list|,
name|Resource
argument_list|>
name|closestStep
init|=
name|cumulativeCapacity
operator|.
name|floorEntry
argument_list|(
name|tick
argument_list|)
decl_stmt|;
if|if
condition|(
name|closestStep
operator|!=
literal|null
condition|)
block|{
return|return
name|Resources
operator|.
name|clone
argument_list|(
name|closestStep
operator|.
name|getValue
argument_list|()
argument_list|)
return|;
block|}
return|return
name|Resources
operator|.
name|clone
argument_list|(
name|ZERO_RESOURCE
argument_list|)
return|;
block|}
finally|finally
block|{
name|readLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Get the timestamp of the earliest resource allocation.    *    * @return the timestamp of the first resource allocation    */
DECL|method|getEarliestStartTime ()
specifier|public
name|long
name|getEarliestStartTime
parameter_list|()
block|{
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
if|if
condition|(
name|cumulativeCapacity
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
else|else
block|{
return|return
name|cumulativeCapacity
operator|.
name|firstKey
argument_list|()
return|;
block|}
block|}
finally|finally
block|{
name|readLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Get the timestamp of the latest non-null resource allocation.    *    * @return the timestamp of the last resource allocation    */
DECL|method|getLatestNonNullTime ()
specifier|public
name|long
name|getLatestNonNullTime
parameter_list|()
block|{
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
if|if
condition|(
name|cumulativeCapacity
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
else|else
block|{
comment|// the last entry might contain null (to terminate
comment|// the sequence)... return previous one.
name|Entry
argument_list|<
name|Long
argument_list|,
name|Resource
argument_list|>
name|last
init|=
name|cumulativeCapacity
operator|.
name|lastEntry
argument_list|()
decl_stmt|;
if|if
condition|(
name|last
operator|.
name|getValue
argument_list|()
operator|==
literal|null
condition|)
block|{
return|return
name|cumulativeCapacity
operator|.
name|floorKey
argument_list|(
name|last
operator|.
name|getKey
argument_list|()
operator|-
literal|1
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|last
operator|.
name|getKey
argument_list|()
return|;
block|}
block|}
block|}
finally|finally
block|{
name|readLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Returns true if there are no non-zero entries.    *    * @return true if there are no allocations or false otherwise    */
DECL|method|isEmpty ()
specifier|public
name|boolean
name|isEmpty
parameter_list|()
block|{
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
if|if
condition|(
name|cumulativeCapacity
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
comment|// Deletion leaves a single zero entry with a null at the end so check for
comment|// that
if|if
condition|(
name|cumulativeCapacity
operator|.
name|size
argument_list|()
operator|==
literal|2
condition|)
block|{
return|return
name|cumulativeCapacity
operator|.
name|firstEntry
argument_list|()
operator|.
name|getValue
argument_list|()
operator|.
name|equals
argument_list|(
name|ZERO_RESOURCE
argument_list|)
operator|&&
name|cumulativeCapacity
operator|.
name|lastEntry
argument_list|()
operator|.
name|getValue
argument_list|()
operator|==
literal|null
return|;
block|}
return|return
literal|false
return|;
block|}
finally|finally
block|{
name|readLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|ret
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
if|if
condition|(
name|cumulativeCapacity
operator|.
name|size
argument_list|()
operator|>
name|THRESHOLD
condition|)
block|{
name|ret
operator|.
name|append
argument_list|(
literal|"Number of steps: "
argument_list|)
operator|.
name|append
argument_list|(
name|cumulativeCapacity
operator|.
name|size
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|" earliest entry: "
argument_list|)
operator|.
name|append
argument_list|(
name|cumulativeCapacity
operator|.
name|firstKey
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|" latest entry: "
argument_list|)
operator|.
name|append
argument_list|(
name|cumulativeCapacity
operator|.
name|lastKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Long
argument_list|,
name|Resource
argument_list|>
name|r
range|:
name|cumulativeCapacity
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|ret
operator|.
name|append
argument_list|(
name|r
operator|.
name|getKey
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|": "
argument_list|)
operator|.
name|append
argument_list|(
name|r
operator|.
name|getValue
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n "
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|ret
operator|.
name|toString
argument_list|()
return|;
block|}
finally|finally
block|{
name|readLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Returns the JSON string representation of the current resources allocated    * over time.    *    * @return the JSON string representation of the current resources allocated    *         over time    */
DECL|method|toMemJSONString ()
specifier|public
name|String
name|toMemJSONString
parameter_list|()
block|{
name|StringWriter
name|json
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|JsonWriter
name|jsonWriter
init|=
operator|new
name|JsonWriter
argument_list|(
name|json
argument_list|)
decl_stmt|;
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|jsonWriter
operator|.
name|beginObject
argument_list|()
expr_stmt|;
comment|// jsonWriter.name("timestamp").value("resource");
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Long
argument_list|,
name|Resource
argument_list|>
name|r
range|:
name|cumulativeCapacity
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|jsonWriter
operator|.
name|name
argument_list|(
name|r
operator|.
name|getKey
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|value
argument_list|(
name|r
operator|.
name|getValue
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|jsonWriter
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|jsonWriter
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|json
operator|.
name|toString
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// This should not happen
return|return
literal|""
return|;
block|}
finally|finally
block|{
name|readLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Returns the representation of the current resources allocated over time as    * an interval map (in the defined non-null range).    *    * @return the representation of the current resources allocated over time as    *         an interval map.    */
DECL|method|toIntervalMap ()
specifier|public
name|Map
argument_list|<
name|ReservationInterval
argument_list|,
name|Resource
argument_list|>
name|toIntervalMap
parameter_list|()
block|{
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|Map
argument_list|<
name|ReservationInterval
argument_list|,
name|Resource
argument_list|>
name|allocations
init|=
operator|new
name|TreeMap
argument_list|<
name|ReservationInterval
argument_list|,
name|Resource
argument_list|>
argument_list|()
decl_stmt|;
comment|// Empty
if|if
condition|(
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|allocations
return|;
block|}
name|Map
operator|.
name|Entry
argument_list|<
name|Long
argument_list|,
name|Resource
argument_list|>
name|lastEntry
init|=
literal|null
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Long
argument_list|,
name|Resource
argument_list|>
name|entry
range|:
name|cumulativeCapacity
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|lastEntry
operator|!=
literal|null
operator|&&
name|entry
operator|.
name|getValue
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|ReservationInterval
name|interval
init|=
operator|new
name|ReservationInterval
argument_list|(
name|lastEntry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
name|Resource
name|resource
init|=
name|lastEntry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|allocations
operator|.
name|put
argument_list|(
name|interval
argument_list|,
name|resource
argument_list|)
expr_stmt|;
block|}
name|lastEntry
operator|=
name|entry
expr_stmt|;
block|}
return|return
name|allocations
return|;
block|}
finally|finally
block|{
name|readLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|getCumulative ()
specifier|public
name|NavigableMap
argument_list|<
name|Long
argument_list|,
name|Resource
argument_list|>
name|getCumulative
parameter_list|()
block|{
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
return|return
name|cumulativeCapacity
return|;
block|}
finally|finally
block|{
name|readLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Merges the range start to end of two {@code RLESparseResourceAllocation}    * using a given {@code RLEOperator}.    *    * @param resCalc the resource calculator    * @param clusterResource the total cluster resources (for DRF)    * @param a the left operand    * @param b the right operand    * @param operator the operator to be applied during merge    * @param start the start-time of the range to be considered    * @param end the end-time of the range to be considered    * @return the a merged RLESparseResourceAllocation, produced by applying    *         "operator" to "a" and "b"    * @throws PlanningException in case the operator is subtractTestPositive and    *           the result would contain a negative value    */
DECL|method|merge (ResourceCalculator resCalc, Resource clusterResource, RLESparseResourceAllocation a, RLESparseResourceAllocation b, RLEOperator operator, long start, long end)
specifier|public
specifier|static
name|RLESparseResourceAllocation
name|merge
parameter_list|(
name|ResourceCalculator
name|resCalc
parameter_list|,
name|Resource
name|clusterResource
parameter_list|,
name|RLESparseResourceAllocation
name|a
parameter_list|,
name|RLESparseResourceAllocation
name|b
parameter_list|,
name|RLEOperator
name|operator
parameter_list|,
name|long
name|start
parameter_list|,
name|long
name|end
parameter_list|)
throws|throws
name|PlanningException
block|{
name|NavigableMap
argument_list|<
name|Long
argument_list|,
name|Resource
argument_list|>
name|cumA
init|=
name|a
operator|.
name|getRangeOverlapping
argument_list|(
name|start
argument_list|,
name|end
argument_list|)
operator|.
name|getCumulative
argument_list|()
decl_stmt|;
name|NavigableMap
argument_list|<
name|Long
argument_list|,
name|Resource
argument_list|>
name|cumB
init|=
name|b
operator|.
name|getRangeOverlapping
argument_list|(
name|start
argument_list|,
name|end
argument_list|)
operator|.
name|getCumulative
argument_list|()
decl_stmt|;
name|NavigableMap
argument_list|<
name|Long
argument_list|,
name|Resource
argument_list|>
name|out
init|=
name|merge
argument_list|(
name|resCalc
argument_list|,
name|clusterResource
argument_list|,
name|cumA
argument_list|,
name|cumB
argument_list|,
name|start
argument_list|,
name|end
argument_list|,
name|operator
argument_list|)
decl_stmt|;
return|return
operator|new
name|RLESparseResourceAllocation
argument_list|(
name|out
argument_list|,
name|resCalc
argument_list|)
return|;
block|}
DECL|method|merge (ResourceCalculator resCalc, Resource clusterResource, NavigableMap<Long, Resource> a, NavigableMap<Long, Resource> b, long start, long end, RLEOperator operator)
specifier|private
specifier|static
name|NavigableMap
argument_list|<
name|Long
argument_list|,
name|Resource
argument_list|>
name|merge
parameter_list|(
name|ResourceCalculator
name|resCalc
parameter_list|,
name|Resource
name|clusterResource
parameter_list|,
name|NavigableMap
argument_list|<
name|Long
argument_list|,
name|Resource
argument_list|>
name|a
parameter_list|,
name|NavigableMap
argument_list|<
name|Long
argument_list|,
name|Resource
argument_list|>
name|b
parameter_list|,
name|long
name|start
parameter_list|,
name|long
name|end
parameter_list|,
name|RLEOperator
name|operator
parameter_list|)
throws|throws
name|PlanningException
block|{
comment|// handle special cases of empty input
if|if
condition|(
name|a
operator|==
literal|null
operator|||
name|a
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
name|operator
operator|==
name|RLEOperator
operator|.
name|subtract
operator|||
name|operator
operator|==
name|RLEOperator
operator|.
name|subtractTestNonNegative
condition|)
block|{
return|return
name|negate
argument_list|(
name|operator
argument_list|,
name|b
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|b
return|;
block|}
block|}
if|if
condition|(
name|b
operator|==
literal|null
operator|||
name|b
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|a
return|;
block|}
comment|// define iterators and support variables
name|Iterator
argument_list|<
name|Entry
argument_list|<
name|Long
argument_list|,
name|Resource
argument_list|>
argument_list|>
name|aIt
init|=
name|a
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|Entry
argument_list|<
name|Long
argument_list|,
name|Resource
argument_list|>
argument_list|>
name|bIt
init|=
name|b
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|Entry
argument_list|<
name|Long
argument_list|,
name|Resource
argument_list|>
name|curA
init|=
name|aIt
operator|.
name|next
argument_list|()
decl_stmt|;
name|Entry
argument_list|<
name|Long
argument_list|,
name|Resource
argument_list|>
name|curB
init|=
name|bIt
operator|.
name|next
argument_list|()
decl_stmt|;
name|Entry
argument_list|<
name|Long
argument_list|,
name|Resource
argument_list|>
name|lastA
init|=
literal|null
decl_stmt|;
name|Entry
argument_list|<
name|Long
argument_list|,
name|Resource
argument_list|>
name|lastB
init|=
literal|null
decl_stmt|;
name|boolean
name|aIsDone
init|=
literal|false
decl_stmt|;
name|boolean
name|bIsDone
init|=
literal|false
decl_stmt|;
name|TreeMap
argument_list|<
name|Long
argument_list|,
name|Resource
argument_list|>
name|out
init|=
operator|new
name|TreeMap
argument_list|<
name|Long
argument_list|,
name|Resource
argument_list|>
argument_list|()
decl_stmt|;
while|while
condition|(
operator|!
operator|(
name|curA
operator|.
name|equals
argument_list|(
name|lastA
argument_list|)
operator|&&
name|curB
operator|.
name|equals
argument_list|(
name|lastB
argument_list|)
operator|)
condition|)
block|{
name|Resource
name|outRes
decl_stmt|;
name|long
name|time
init|=
operator|-
literal|1
decl_stmt|;
comment|// curA is smaller than curB
if|if
condition|(
name|bIsDone
operator|||
operator|(
name|curA
operator|.
name|getKey
argument_list|()
operator|<
name|curB
operator|.
name|getKey
argument_list|()
operator|&&
operator|!
name|aIsDone
operator|)
condition|)
block|{
name|outRes
operator|=
name|combineValue
argument_list|(
name|operator
argument_list|,
name|resCalc
argument_list|,
name|clusterResource
argument_list|,
name|curA
argument_list|,
name|lastB
argument_list|)
expr_stmt|;
name|time
operator|=
operator|(
name|curA
operator|.
name|getKey
argument_list|()
operator|<
name|start
operator|)
condition|?
name|start
else|:
name|curA
operator|.
name|getKey
argument_list|()
expr_stmt|;
name|lastA
operator|=
name|curA
expr_stmt|;
if|if
condition|(
name|aIt
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|curA
operator|=
name|aIt
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|aIsDone
operator|=
literal|true
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// curB is smaller than curA
if|if
condition|(
name|aIsDone
operator|||
operator|(
name|curA
operator|.
name|getKey
argument_list|()
operator|>
name|curB
operator|.
name|getKey
argument_list|()
operator|&&
operator|!
name|bIsDone
operator|)
condition|)
block|{
name|outRes
operator|=
name|combineValue
argument_list|(
name|operator
argument_list|,
name|resCalc
argument_list|,
name|clusterResource
argument_list|,
name|lastA
argument_list|,
name|curB
argument_list|)
expr_stmt|;
name|time
operator|=
operator|(
name|curB
operator|.
name|getKey
argument_list|()
operator|<
name|start
operator|)
condition|?
name|start
else|:
name|curB
operator|.
name|getKey
argument_list|()
expr_stmt|;
name|lastB
operator|=
name|curB
expr_stmt|;
if|if
condition|(
name|bIt
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|curB
operator|=
name|bIt
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|bIsDone
operator|=
literal|true
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// curA is equal to curB
name|outRes
operator|=
name|combineValue
argument_list|(
name|operator
argument_list|,
name|resCalc
argument_list|,
name|clusterResource
argument_list|,
name|curA
argument_list|,
name|curB
argument_list|)
expr_stmt|;
name|time
operator|=
operator|(
name|curA
operator|.
name|getKey
argument_list|()
operator|<
name|start
operator|)
condition|?
name|start
else|:
name|curA
operator|.
name|getKey
argument_list|()
expr_stmt|;
name|lastA
operator|=
name|curA
expr_stmt|;
if|if
condition|(
name|aIt
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|curA
operator|=
name|aIt
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|aIsDone
operator|=
literal|true
expr_stmt|;
block|}
name|lastB
operator|=
name|curB
expr_stmt|;
if|if
condition|(
name|bIt
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|curB
operator|=
name|bIt
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|bIsDone
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
comment|// add to out if not redundant
name|addIfNeeded
argument_list|(
name|out
argument_list|,
name|time
argument_list|,
name|outRes
argument_list|)
expr_stmt|;
block|}
name|addIfNeeded
argument_list|(
name|out
argument_list|,
name|end
argument_list|,
literal|null
argument_list|)
expr_stmt|;
return|return
name|out
return|;
block|}
DECL|method|negate (RLEOperator operator, NavigableMap<Long, Resource> a)
specifier|private
specifier|static
name|NavigableMap
argument_list|<
name|Long
argument_list|,
name|Resource
argument_list|>
name|negate
parameter_list|(
name|RLEOperator
name|operator
parameter_list|,
name|NavigableMap
argument_list|<
name|Long
argument_list|,
name|Resource
argument_list|>
name|a
parameter_list|)
throws|throws
name|PlanningException
block|{
name|TreeMap
argument_list|<
name|Long
argument_list|,
name|Resource
argument_list|>
name|out
init|=
operator|new
name|TreeMap
argument_list|<
name|Long
argument_list|,
name|Resource
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|Long
argument_list|,
name|Resource
argument_list|>
name|e
range|:
name|a
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Resource
name|val
init|=
name|Resources
operator|.
name|negate
argument_list|(
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
decl_stmt|;
comment|// test for negative value and throws
if|if
condition|(
name|operator
operator|==
name|RLEOperator
operator|.
name|subtractTestNonNegative
operator|&&
operator|(
name|Resources
operator|.
name|fitsIn
argument_list|(
name|val
argument_list|,
name|ZERO_RESOURCE
argument_list|)
operator|&&
operator|!
name|Resources
operator|.
name|equals
argument_list|(
name|val
argument_list|,
name|ZERO_RESOURCE
argument_list|)
operator|)
condition|)
block|{
throw|throw
operator|new
name|PlanningException
argument_list|(
literal|"RLESparseResourceAllocation: merge failed as the "
operator|+
literal|"resulting RLESparseResourceAllocation would be negative"
argument_list|)
throw|;
block|}
name|out
operator|.
name|put
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
return|return
name|out
return|;
block|}
DECL|method|addIfNeeded (TreeMap<Long, Resource> out, long time, Resource outRes)
specifier|private
specifier|static
name|void
name|addIfNeeded
parameter_list|(
name|TreeMap
argument_list|<
name|Long
argument_list|,
name|Resource
argument_list|>
name|out
parameter_list|,
name|long
name|time
parameter_list|,
name|Resource
name|outRes
parameter_list|)
block|{
if|if
condition|(
name|out
operator|.
name|isEmpty
argument_list|()
operator|||
operator|(
name|out
operator|.
name|lastEntry
argument_list|()
operator|!=
literal|null
operator|&&
name|outRes
operator|==
literal|null
operator|)
operator|||
operator|!
name|Resources
operator|.
name|equals
argument_list|(
name|out
operator|.
name|lastEntry
argument_list|()
operator|.
name|getValue
argument_list|()
argument_list|,
name|outRes
argument_list|)
condition|)
block|{
name|out
operator|.
name|put
argument_list|(
name|time
argument_list|,
name|outRes
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|combineValue (RLEOperator op, ResourceCalculator resCalc, Resource clusterResource, Entry<Long, Resource> eA, Entry<Long, Resource> eB)
specifier|private
specifier|static
name|Resource
name|combineValue
parameter_list|(
name|RLEOperator
name|op
parameter_list|,
name|ResourceCalculator
name|resCalc
parameter_list|,
name|Resource
name|clusterResource
parameter_list|,
name|Entry
argument_list|<
name|Long
argument_list|,
name|Resource
argument_list|>
name|eA
parameter_list|,
name|Entry
argument_list|<
name|Long
argument_list|,
name|Resource
argument_list|>
name|eB
parameter_list|)
throws|throws
name|PlanningException
block|{
comment|// deal with nulls
if|if
condition|(
name|eA
operator|==
literal|null
operator|||
name|eA
operator|.
name|getValue
argument_list|()
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|eB
operator|==
literal|null
operator|||
name|eB
operator|.
name|getValue
argument_list|()
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|op
operator|==
name|RLEOperator
operator|.
name|subtract
condition|)
block|{
return|return
name|Resources
operator|.
name|negate
argument_list|(
name|eB
operator|.
name|getValue
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|eB
operator|.
name|getValue
argument_list|()
return|;
block|}
block|}
if|if
condition|(
name|eB
operator|==
literal|null
operator|||
name|eB
operator|.
name|getValue
argument_list|()
operator|==
literal|null
condition|)
block|{
return|return
name|eA
operator|.
name|getValue
argument_list|()
return|;
block|}
name|Resource
name|a
init|=
name|eA
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|Resource
name|b
init|=
name|eB
operator|.
name|getValue
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|op
condition|)
block|{
case|case
name|add
case|:
return|return
name|Resources
operator|.
name|add
argument_list|(
name|a
argument_list|,
name|b
argument_list|)
return|;
case|case
name|subtract
case|:
return|return
name|Resources
operator|.
name|subtract
argument_list|(
name|a
argument_list|,
name|b
argument_list|)
return|;
case|case
name|subtractTestNonNegative
case|:
if|if
condition|(
operator|!
name|Resources
operator|.
name|fitsIn
argument_list|(
name|b
argument_list|,
name|a
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|PlanningException
argument_list|(
literal|"RLESparseResourceAllocation: merge failed as the "
operator|+
literal|"resulting RLESparseResourceAllocation would be negative"
argument_list|)
throw|;
block|}
else|else
block|{
return|return
name|Resources
operator|.
name|subtract
argument_list|(
name|a
argument_list|,
name|b
argument_list|)
return|;
block|}
case|case
name|min
case|:
return|return
name|Resources
operator|.
name|min
argument_list|(
name|resCalc
argument_list|,
name|clusterResource
argument_list|,
name|a
argument_list|,
name|b
argument_list|)
return|;
case|case
name|max
case|:
return|return
name|Resources
operator|.
name|max
argument_list|(
name|resCalc
argument_list|,
name|clusterResource
argument_list|,
name|a
argument_list|,
name|b
argument_list|)
return|;
default|default:
return|return
literal|null
return|;
block|}
block|}
DECL|method|getRangeOverlapping (long start, long end)
specifier|public
name|RLESparseResourceAllocation
name|getRangeOverlapping
parameter_list|(
name|long
name|start
parameter_list|,
name|long
name|end
parameter_list|)
block|{
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|NavigableMap
argument_list|<
name|Long
argument_list|,
name|Resource
argument_list|>
name|a
init|=
name|this
operator|.
name|getCumulative
argument_list|()
decl_stmt|;
if|if
condition|(
name|a
operator|!=
literal|null
operator|&&
operator|!
name|a
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// include the portion of previous entry that overlaps start
if|if
condition|(
name|start
operator|>
name|a
operator|.
name|firstKey
argument_list|()
condition|)
block|{
name|long
name|previous
init|=
name|a
operator|.
name|floorKey
argument_list|(
name|start
argument_list|)
decl_stmt|;
name|a
operator|=
name|a
operator|.
name|tailMap
argument_list|(
name|previous
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|end
operator|<
name|a
operator|.
name|lastKey
argument_list|()
condition|)
block|{
name|a
operator|=
name|a
operator|.
name|headMap
argument_list|(
name|end
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
name|RLESparseResourceAllocation
name|ret
init|=
operator|new
name|RLESparseResourceAllocation
argument_list|(
name|a
argument_list|,
name|resourceCalculator
argument_list|)
decl_stmt|;
return|return
name|ret
return|;
block|}
finally|finally
block|{
name|readLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * The set of operators that can be applied to two    * {@code RLESparseResourceAllocation} during a merge operation.    */
DECL|enum|RLEOperator
specifier|public
enum|enum
name|RLEOperator
block|{
DECL|enumConstant|add
DECL|enumConstant|subtract
DECL|enumConstant|min
DECL|enumConstant|max
DECL|enumConstant|subtractTestNonNegative
name|add
block|,
name|subtract
block|,
name|min
block|,
name|max
block|,
name|subtractTestNonNegative
block|}
block|}
end_class

end_unit

