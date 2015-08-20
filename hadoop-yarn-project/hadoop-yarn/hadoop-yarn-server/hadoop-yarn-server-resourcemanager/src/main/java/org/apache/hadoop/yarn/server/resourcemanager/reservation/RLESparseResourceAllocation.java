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
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|SortedMap
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
name|Resource
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
decl_stmt|;
DECL|field|cumulativeCapacity
specifier|private
name|TreeMap
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
DECL|field|minAlloc
specifier|private
specifier|final
name|Resource
name|minAlloc
decl_stmt|;
DECL|method|RLESparseResourceAllocation (ResourceCalculator resourceCalculator, Resource minAlloc)
specifier|public
name|RLESparseResourceAllocation
parameter_list|(
name|ResourceCalculator
name|resourceCalculator
parameter_list|,
name|Resource
name|minAlloc
parameter_list|)
block|{
name|this
operator|.
name|resourceCalculator
operator|=
name|resourceCalculator
expr_stmt|;
name|this
operator|.
name|minAlloc
operator|=
name|minAlloc
expr_stmt|;
block|}
DECL|method|isSameAsPrevious (Long key, Resource capacity)
specifier|private
name|boolean
name|isSameAsPrevious
parameter_list|(
name|Long
name|key
parameter_list|,
name|Resource
name|capacity
parameter_list|)
block|{
name|Entry
argument_list|<
name|Long
argument_list|,
name|Resource
argument_list|>
name|previous
init|=
name|cumulativeCapacity
operator|.
name|lowerEntry
argument_list|(
name|key
argument_list|)
decl_stmt|;
return|return
operator|(
name|previous
operator|!=
literal|null
operator|&&
name|previous
operator|.
name|getValue
argument_list|()
operator|.
name|equals
argument_list|(
name|capacity
argument_list|)
operator|)
return|;
block|}
DECL|method|isSameAsNext (Long key, Resource capacity)
specifier|private
name|boolean
name|isSameAsNext
parameter_list|(
name|Long
name|key
parameter_list|,
name|Resource
name|capacity
parameter_list|)
block|{
name|Entry
argument_list|<
name|Long
argument_list|,
name|Resource
argument_list|>
name|next
init|=
name|cumulativeCapacity
operator|.
name|higherEntry
argument_list|(
name|key
argument_list|)
decl_stmt|;
return|return
operator|(
name|next
operator|!=
literal|null
operator|&&
name|next
operator|.
name|getValue
argument_list|()
operator|.
name|equals
argument_list|(
name|capacity
argument_list|)
operator|)
return|;
block|}
comment|/**    * Add a resource for the specified interval    *    * @param reservationInterval the interval for which the resource is to be    *          added    * @param totCap the resource to be added    * @return true if addition is successful, false otherwise    */
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
name|long
name|startKey
init|=
name|reservationInterval
operator|.
name|getStartTime
argument_list|()
decl_stmt|;
name|long
name|endKey
init|=
name|reservationInterval
operator|.
name|getEndTime
argument_list|()
decl_stmt|;
name|NavigableMap
argument_list|<
name|Long
argument_list|,
name|Resource
argument_list|>
name|ticks
init|=
name|cumulativeCapacity
operator|.
name|headMap
argument_list|(
name|endKey
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|ticks
operator|!=
literal|null
operator|&&
operator|!
name|ticks
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|Resource
name|updatedCapacity
init|=
name|Resource
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|Entry
argument_list|<
name|Long
argument_list|,
name|Resource
argument_list|>
name|lowEntry
init|=
name|ticks
operator|.
name|floorEntry
argument_list|(
name|startKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|lowEntry
operator|==
literal|null
condition|)
block|{
comment|// This is the earliest starting interval
name|cumulativeCapacity
operator|.
name|put
argument_list|(
name|startKey
argument_list|,
name|totCap
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|updatedCapacity
operator|=
name|Resources
operator|.
name|add
argument_list|(
name|lowEntry
operator|.
name|getValue
argument_list|()
argument_list|,
name|totCap
argument_list|)
expr_stmt|;
comment|// Add a new tick only if the updated value is different
comment|// from the previous tick
if|if
condition|(
operator|(
name|startKey
operator|==
name|lowEntry
operator|.
name|getKey
argument_list|()
operator|)
operator|&&
operator|(
name|isSameAsPrevious
argument_list|(
name|lowEntry
operator|.
name|getKey
argument_list|()
argument_list|,
name|updatedCapacity
argument_list|)
operator|)
condition|)
block|{
name|cumulativeCapacity
operator|.
name|remove
argument_list|(
name|lowEntry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|cumulativeCapacity
operator|.
name|put
argument_list|(
name|startKey
argument_list|,
name|updatedCapacity
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Increase all the capacities of overlapping intervals
name|Set
argument_list|<
name|Entry
argument_list|<
name|Long
argument_list|,
name|Resource
argument_list|>
argument_list|>
name|overlapSet
init|=
name|ticks
operator|.
name|tailMap
argument_list|(
name|startKey
argument_list|,
literal|false
argument_list|)
operator|.
name|entrySet
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
name|entry
range|:
name|overlapSet
control|)
block|{
name|updatedCapacity
operator|=
name|Resources
operator|.
name|add
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|,
name|totCap
argument_list|)
expr_stmt|;
name|entry
operator|.
name|setValue
argument_list|(
name|updatedCapacity
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// This is the first interval to be added
name|cumulativeCapacity
operator|.
name|put
argument_list|(
name|startKey
argument_list|,
name|totCap
argument_list|)
expr_stmt|;
block|}
name|Resource
name|nextTick
init|=
name|cumulativeCapacity
operator|.
name|get
argument_list|(
name|endKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|nextTick
operator|!=
literal|null
condition|)
block|{
comment|// If there is overlap, remove the duplicate entry
if|if
condition|(
name|isSameAsPrevious
argument_list|(
name|endKey
argument_list|,
name|nextTick
argument_list|)
condition|)
block|{
name|cumulativeCapacity
operator|.
name|remove
argument_list|(
name|endKey
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// Decrease capacity as this is end of the interval
name|cumulativeCapacity
operator|.
name|put
argument_list|(
name|endKey
argument_list|,
name|Resources
operator|.
name|subtract
argument_list|(
name|cumulativeCapacity
operator|.
name|floorEntry
argument_list|(
name|endKey
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|,
name|totCap
argument_list|)
argument_list|)
expr_stmt|;
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
comment|/**    * Removes a resource for the specified interval    *    * @param reservationInterval the interval for which the resource is to be    *          removed    * @param totCap the resource to be removed    * @return true if removal is successful, false otherwise    */
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
name|long
name|startKey
init|=
name|reservationInterval
operator|.
name|getStartTime
argument_list|()
decl_stmt|;
name|long
name|endKey
init|=
name|reservationInterval
operator|.
name|getEndTime
argument_list|()
decl_stmt|;
comment|// update the start key
name|NavigableMap
argument_list|<
name|Long
argument_list|,
name|Resource
argument_list|>
name|ticks
init|=
name|cumulativeCapacity
operator|.
name|headMap
argument_list|(
name|endKey
argument_list|,
literal|false
argument_list|)
decl_stmt|;
comment|// Decrease all the capacities of overlapping intervals
name|SortedMap
argument_list|<
name|Long
argument_list|,
name|Resource
argument_list|>
name|overlapSet
init|=
name|ticks
operator|.
name|tailMap
argument_list|(
name|startKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|overlapSet
operator|!=
literal|null
operator|&&
operator|!
name|overlapSet
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|Resource
name|updatedCapacity
init|=
name|Resource
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|long
name|currentKey
init|=
operator|-
literal|1
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|Entry
argument_list|<
name|Long
argument_list|,
name|Resource
argument_list|>
argument_list|>
name|overlapEntries
init|=
name|overlapSet
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|overlapEntries
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Entry
argument_list|<
name|Long
argument_list|,
name|Resource
argument_list|>
name|entry
init|=
name|overlapEntries
operator|.
name|next
argument_list|()
decl_stmt|;
name|currentKey
operator|=
name|entry
operator|.
name|getKey
argument_list|()
expr_stmt|;
name|updatedCapacity
operator|=
name|Resources
operator|.
name|subtract
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|,
name|totCap
argument_list|)
expr_stmt|;
comment|// update each entry between start and end key
name|cumulativeCapacity
operator|.
name|put
argument_list|(
name|currentKey
argument_list|,
name|updatedCapacity
argument_list|)
expr_stmt|;
block|}
comment|// Remove the first overlap entry if it is same as previous after
comment|// updation
name|Long
name|firstKey
init|=
name|overlapSet
operator|.
name|firstKey
argument_list|()
decl_stmt|;
if|if
condition|(
name|isSameAsPrevious
argument_list|(
name|firstKey
argument_list|,
name|overlapSet
operator|.
name|get
argument_list|(
name|firstKey
argument_list|)
argument_list|)
condition|)
block|{
name|cumulativeCapacity
operator|.
name|remove
argument_list|(
name|firstKey
argument_list|)
expr_stmt|;
block|}
comment|// Remove the next entry if it is same as end entry after updation
if|if
condition|(
operator|(
name|currentKey
operator|!=
operator|-
literal|1
operator|)
operator|&&
operator|(
name|isSameAsNext
argument_list|(
name|currentKey
argument_list|,
name|updatedCapacity
argument_list|)
operator|)
condition|)
block|{
name|cumulativeCapacity
operator|.
name|remove
argument_list|(
name|cumulativeCapacity
operator|.
name|higherKey
argument_list|(
name|currentKey
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
comment|/**    * Returns the capacity, i.e. total resources allocated at the specified point    * of time    *    * @param tick the time (UTC in ms) at which the capacity is requested    * @return the resources allocated at the specified time    */
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
comment|/**    * Get the timestamp of the earliest resource allocation    *    * @return the timestamp of the first resource allocation    */
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
comment|/**    * Get the timestamp of the latest resource allocation    *    * @return the timestamp of the last resource allocation    */
DECL|method|getLatestEndTime ()
specifier|public
name|long
name|getLatestEndTime
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
name|lastKey
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
comment|/**    * Returns true if there are no non-zero entries    *    * @return true if there are no allocations or false otherwise    */
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
comment|// Deletion leaves a single zero entry so check for that
if|if
condition|(
name|cumulativeCapacity
operator|.
name|size
argument_list|()
operator|==
literal|1
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
comment|/**    * Returns the JSON string representation of the current resources allocated    * over time    *    * @return the JSON string representation of the current resources allocated    *         over time    */
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
comment|/**    * Returns the representation of the current resources allocated over time as    * an interval map.    *    * @return the representation of the current resources allocated over time as    *         an interval map.    */
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
block|}
end_class

end_unit

