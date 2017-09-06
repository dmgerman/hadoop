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
name|Resources
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * This data structure stores a periodic RLESparseResourceAllocation.  * Default period is 1 day (86400000ms).  */
end_comment

begin_class
DECL|class|PeriodicRLESparseResourceAllocation
specifier|public
class|class
name|PeriodicRLESparseResourceAllocation
extends|extends
name|RLESparseResourceAllocation
block|{
comment|// Log
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|PeriodicRLESparseResourceAllocation
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|timePeriod
specifier|private
name|long
name|timePeriod
decl_stmt|;
comment|/**    * Constructor.    *    * @param rleVector {@link RLESparseResourceAllocation} with the run-length               encoded data.    * @param timePeriod Time period in milliseconds.    */
DECL|method|PeriodicRLESparseResourceAllocation ( RLESparseResourceAllocation rleVector, Long timePeriod)
specifier|public
name|PeriodicRLESparseResourceAllocation
parameter_list|(
name|RLESparseResourceAllocation
name|rleVector
parameter_list|,
name|Long
name|timePeriod
parameter_list|)
block|{
name|super
argument_list|(
name|rleVector
operator|.
name|getCumulative
argument_list|()
argument_list|,
name|rleVector
operator|.
name|getResourceCalculator
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|timePeriod
operator|=
name|timePeriod
expr_stmt|;
block|}
comment|/**    * Constructor. Default time period set to 1 day.    *    * @param rleVector {@link RLESparseResourceAllocation} with the run-length               encoded data.    */
DECL|method|PeriodicRLESparseResourceAllocation ( RLESparseResourceAllocation rleVector)
specifier|public
name|PeriodicRLESparseResourceAllocation
parameter_list|(
name|RLESparseResourceAllocation
name|rleVector
parameter_list|)
block|{
name|this
argument_list|(
name|rleVector
argument_list|,
literal|86400000L
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get capacity at time based on periodic repetition.    *    * @param tick UTC time for which the allocated {@link Resource} is queried.    * @return {@link Resource} allocated at specified time    */
DECL|method|getCapacityAtTime (long tick)
specifier|public
name|Resource
name|getCapacityAtTime
parameter_list|(
name|long
name|tick
parameter_list|)
block|{
name|long
name|convertedTime
init|=
operator|(
name|tick
operator|%
name|timePeriod
operator|)
decl_stmt|;
return|return
name|super
operator|.
name|getCapacityAtTime
argument_list|(
name|convertedTime
argument_list|)
return|;
block|}
comment|/**    * Add resource for the specified interval. This function will be used by    * {@link InMemoryPlan} while placing reservations between 0 and timePeriod.    * The interval may include 0, but the end time must be strictly less than    * timePeriod.    *    * @param interval {@link ReservationInterval} to which the specified    *          resource is to be added.    * @param resource {@link Resource} to be added to the interval specified.    * @return true if addition is successful, false otherwise    */
DECL|method|addInterval (ReservationInterval interval, Resource resource)
specifier|public
name|boolean
name|addInterval
parameter_list|(
name|ReservationInterval
name|interval
parameter_list|,
name|Resource
name|resource
parameter_list|)
block|{
name|long
name|startTime
init|=
name|interval
operator|.
name|getStartTime
argument_list|()
decl_stmt|;
name|long
name|endTime
init|=
name|interval
operator|.
name|getEndTime
argument_list|()
decl_stmt|;
if|if
condition|(
name|startTime
operator|>=
literal|0
operator|&&
name|endTime
operator|>
name|startTime
operator|&&
name|endTime
operator|<=
name|timePeriod
condition|)
block|{
return|return
name|super
operator|.
name|addInterval
argument_list|(
name|interval
argument_list|,
name|resource
argument_list|)
return|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Cannot set capacity beyond end time: "
operator|+
name|timePeriod
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
comment|/**    * Removes a resource for the specified interval.    *    * @param interval the {@link ReservationInterval} for which the resource is    *          to be removed.    * @param resource the {@link Resource} to be removed.    * @return true if removal is successful, false otherwise    */
DECL|method|removeInterval ( ReservationInterval interval, Resource resource)
specifier|public
name|boolean
name|removeInterval
parameter_list|(
name|ReservationInterval
name|interval
parameter_list|,
name|Resource
name|resource
parameter_list|)
block|{
name|long
name|startTime
init|=
name|interval
operator|.
name|getStartTime
argument_list|()
decl_stmt|;
name|long
name|endTime
init|=
name|interval
operator|.
name|getEndTime
argument_list|()
decl_stmt|;
comment|// If the resource to be subtracted is less than the minimum resource in
comment|// the range, abort removal to avoid negative capacity.
if|if
condition|(
operator|!
name|Resources
operator|.
name|fitsIn
argument_list|(
name|resource
argument_list|,
name|super
operator|.
name|getMinimumCapacityInInterval
argument_list|(
name|interval
argument_list|)
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Request to remove more resources than what is available"
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
if|if
condition|(
name|startTime
operator|>=
literal|0
operator|&&
name|endTime
operator|>
name|startTime
operator|&&
name|endTime
operator|<=
name|timePeriod
condition|)
block|{
return|return
name|super
operator|.
name|removeInterval
argument_list|(
name|interval
argument_list|,
name|resource
argument_list|)
return|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Interval extends beyond the end time "
operator|+
name|timePeriod
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
comment|/**    * Get maximum capacity at periodic offsets from the specified time.    *    * @param tick UTC time base from which offsets are specified for finding    *          the maximum capacity.    * @param period periodic offset at which capacities are evaluted.    * @return the maximum {@link Resource} across the specified time instants.    * @return true if removal is successful, false otherwise    */
DECL|method|getMaximumPeriodicCapacity (long tick, long period)
specifier|public
name|Resource
name|getMaximumPeriodicCapacity
parameter_list|(
name|long
name|tick
parameter_list|,
name|long
name|period
parameter_list|)
block|{
name|Resource
name|maxResource
decl_stmt|;
if|if
condition|(
name|period
operator|<
name|timePeriod
condition|)
block|{
name|maxResource
operator|=
name|super
operator|.
name|getMaximumPeriodicCapacity
argument_list|(
name|tick
operator|%
name|timePeriod
argument_list|,
name|period
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// if period is greater than the length of PeriodicRLESparseAllocation,
comment|// only a single value exists in this interval.
name|maxResource
operator|=
name|super
operator|.
name|getCapacityAtTime
argument_list|(
name|tick
operator|%
name|timePeriod
argument_list|)
expr_stmt|;
block|}
return|return
name|maxResource
return|;
block|}
comment|/**    * Get time period of PeriodicRLESparseResourceAllocation.    *    * @return timePeriod time period represented in ms.    */
DECL|method|getTimePeriod ()
specifier|public
name|long
name|getTimePeriod
parameter_list|()
block|{
return|return
name|this
operator|.
name|timePeriod
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
name|StringBuilder
name|ret
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|ret
operator|.
name|append
argument_list|(
literal|"Period: "
argument_list|)
operator|.
name|append
argument_list|(
name|timePeriod
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
operator|.
name|append
argument_list|(
name|super
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|super
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|ret
operator|.
name|append
argument_list|(
literal|" no allocations\n"
argument_list|)
expr_stmt|;
block|}
return|return
name|ret
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

