begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*******************************************************************************  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  *******************************************************************************/
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
name|classification
operator|.
name|InterfaceAudience
operator|.
name|LimitedPrivate
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
name|ReservationId
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
name|RLESparseResourceAllocation
operator|.
name|RLEOperator
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
name|server
operator|.
name|resourcemanager
operator|.
name|reservation
operator|.
name|exceptions
operator|.
name|PlanningQuotaException
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

begin_comment
comment|/**  * This policy enforces a time-extended notion of Capacity. In particular it  * guarantees that the allocation received in input when combined with all  * previous allocation for the user does not violate an instantaneous max limit  * on the resources received, and that for every window of time of length  * validWindow, the integral of the allocations for a user (sum of the currently  * submitted allocation and all prior allocations for the user) does not exceed  * validWindow * maxAvg.  *  * This allows flexibility, in the sense that an allocation can instantaneously  * use large portions of the available capacity, but prevents abuses by bounding  * the average use over time.  *  * By controlling maxInst, maxAvg, validWindow the administrator configuring  * this policy can obtain a behavior ranging from instantaneously enforced  * capacity (akin to existing queues), or fully flexible allocations (likely  * reserved to super-users, or trusted systems).  */
end_comment

begin_class
annotation|@
name|LimitedPrivate
argument_list|(
literal|"yarn"
argument_list|)
annotation|@
name|Unstable
DECL|class|CapacityOverTimePolicy
specifier|public
class|class
name|CapacityOverTimePolicy
extends|extends
name|NoOverCommitPolicy
block|{
DECL|field|conf
specifier|private
name|ReservationSchedulerConfiguration
name|conf
decl_stmt|;
DECL|field|validWindow
specifier|private
name|long
name|validWindow
decl_stmt|;
DECL|field|maxInst
specifier|private
name|float
name|maxInst
decl_stmt|;
DECL|field|maxAvg
specifier|private
name|float
name|maxAvg
decl_stmt|;
annotation|@
name|Override
DECL|method|init (String reservationQueuePath, ReservationSchedulerConfiguration conf)
specifier|public
name|void
name|init
parameter_list|(
name|String
name|reservationQueuePath
parameter_list|,
name|ReservationSchedulerConfiguration
name|conf
parameter_list|)
block|{
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
name|validWindow
operator|=
name|this
operator|.
name|conf
operator|.
name|getReservationWindow
argument_list|(
name|reservationQueuePath
argument_list|)
expr_stmt|;
name|maxInst
operator|=
name|this
operator|.
name|conf
operator|.
name|getInstantaneousMaxCapacity
argument_list|(
name|reservationQueuePath
argument_list|)
operator|/
literal|100
expr_stmt|;
name|maxAvg
operator|=
name|this
operator|.
name|conf
operator|.
name|getAverageCapacity
argument_list|(
name|reservationQueuePath
argument_list|)
operator|/
literal|100
expr_stmt|;
block|}
comment|/**    * The validation algorithm walks over the RLE encoded allocation and    * checks that for all transition points (when the start or end of the    * checking window encounters a value in the RLE). At this point it    * checkes whether the integral computed exceeds the quota limit. Note that    * this might not find the exact time of a violation, but if a violation    * exists it will find it. The advantage is a much lower number of checks    * as compared to time-slot by time-slot checks.    *    * @param plan the plan to validate against    * @param reservation the reservation allocation to test.    * @throws PlanningException if the validation fails.    */
annotation|@
name|Override
DECL|method|validate (Plan plan, ReservationAllocation reservation)
specifier|public
name|void
name|validate
parameter_list|(
name|Plan
name|plan
parameter_list|,
name|ReservationAllocation
name|reservation
parameter_list|)
throws|throws
name|PlanningException
block|{
comment|// rely on NoOverCommitPolicy to check for: 1) user-match, 2) physical
comment|// cluster limits, and 3) maxInst (via override of available)
try|try
block|{
name|super
operator|.
name|validate
argument_list|(
name|plan
argument_list|,
name|reservation
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PlanningException
name|p
parameter_list|)
block|{
comment|//wrap it in proper quota exception
throw|throw
operator|new
name|PlanningQuotaException
argument_list|(
name|p
argument_list|)
throw|;
block|}
name|long
name|checkStart
init|=
name|reservation
operator|.
name|getStartTime
argument_list|()
operator|-
name|validWindow
decl_stmt|;
name|long
name|checkEnd
init|=
name|reservation
operator|.
name|getEndTime
argument_list|()
operator|+
name|validWindow
decl_stmt|;
comment|//---- check for integral violations of capacity --------
comment|// Gather a view of what to check (curr allocation of user, minus old
comment|// version of this reservation, plus new version)
name|RLESparseResourceAllocation
name|consumptionForUserOverTime
init|=
name|plan
operator|.
name|getConsumptionForUserOverTime
argument_list|(
name|reservation
operator|.
name|getUser
argument_list|()
argument_list|,
name|checkStart
argument_list|,
name|checkEnd
argument_list|)
decl_stmt|;
name|ReservationAllocation
name|old
init|=
name|plan
operator|.
name|getReservationById
argument_list|(
name|reservation
operator|.
name|getReservationId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|old
operator|!=
literal|null
condition|)
block|{
name|consumptionForUserOverTime
operator|=
name|RLESparseResourceAllocation
operator|.
name|merge
argument_list|(
name|plan
operator|.
name|getResourceCalculator
argument_list|()
argument_list|,
name|plan
operator|.
name|getTotalCapacity
argument_list|()
argument_list|,
name|consumptionForUserOverTime
argument_list|,
name|old
operator|.
name|getResourcesOverTime
argument_list|(
name|checkStart
argument_list|,
name|checkEnd
argument_list|)
argument_list|,
name|RLEOperator
operator|.
name|add
argument_list|,
name|checkStart
argument_list|,
name|checkEnd
argument_list|)
expr_stmt|;
block|}
name|RLESparseResourceAllocation
name|resRLE
init|=
name|reservation
operator|.
name|getResourcesOverTime
argument_list|(
name|checkStart
argument_list|,
name|checkEnd
argument_list|)
decl_stmt|;
name|RLESparseResourceAllocation
name|toCheck
init|=
name|RLESparseResourceAllocation
operator|.
name|merge
argument_list|(
name|plan
operator|.
name|getResourceCalculator
argument_list|()
argument_list|,
name|plan
operator|.
name|getTotalCapacity
argument_list|()
argument_list|,
name|consumptionForUserOverTime
argument_list|,
name|resRLE
argument_list|,
name|RLEOperator
operator|.
name|add
argument_list|,
name|Long
operator|.
name|MIN_VALUE
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
name|NavigableMap
argument_list|<
name|Long
argument_list|,
name|Resource
argument_list|>
name|integralUp
init|=
operator|new
name|TreeMap
argument_list|<>
argument_list|()
decl_stmt|;
name|NavigableMap
argument_list|<
name|Long
argument_list|,
name|Resource
argument_list|>
name|integralDown
init|=
operator|new
name|TreeMap
argument_list|<>
argument_list|()
decl_stmt|;
name|long
name|prevTime
init|=
name|toCheck
operator|.
name|getEarliestStartTime
argument_list|()
decl_stmt|;
name|IntegralResource
name|prevResource
init|=
operator|new
name|IntegralResource
argument_list|(
literal|0L
argument_list|,
literal|0L
argument_list|)
decl_stmt|;
name|IntegralResource
name|runningTot
init|=
operator|new
name|IntegralResource
argument_list|(
literal|0L
argument_list|,
literal|0L
argument_list|)
decl_stmt|;
comment|// add intermediate points
name|Map
argument_list|<
name|Long
argument_list|,
name|Resource
argument_list|>
name|temp
init|=
operator|new
name|TreeMap
argument_list|<>
argument_list|()
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
name|pointToCheck
range|:
name|toCheck
operator|.
name|getCumulative
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Long
name|timeToCheck
init|=
name|pointToCheck
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|Resource
name|resourceToCheck
init|=
name|pointToCheck
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|Long
name|nextPoint
init|=
name|toCheck
operator|.
name|getCumulative
argument_list|()
operator|.
name|higherKey
argument_list|(
name|timeToCheck
argument_list|)
decl_stmt|;
if|if
condition|(
name|nextPoint
operator|==
literal|null
operator|||
name|toCheck
operator|.
name|getCumulative
argument_list|()
operator|.
name|get
argument_list|(
name|nextPoint
argument_list|)
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
operator|(
name|nextPoint
operator|-
name|timeToCheck
operator|)
operator|/
name|validWindow
condition|;
name|i
operator|++
control|)
block|{
name|temp
operator|.
name|put
argument_list|(
name|timeToCheck
operator|+
operator|(
name|i
operator|*
name|validWindow
operator|)
argument_list|,
name|resourceToCheck
argument_list|)
expr_stmt|;
block|}
block|}
name|temp
operator|.
name|putAll
argument_list|(
name|toCheck
operator|.
name|getCumulative
argument_list|()
argument_list|)
expr_stmt|;
comment|// compute point-wise integral for the up-fronts and down-fronts
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
name|currPoint
range|:
name|temp
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Long
name|currTime
init|=
name|currPoint
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|Resource
name|currResource
init|=
name|currPoint
operator|.
name|getValue
argument_list|()
decl_stmt|;
comment|//add to running total current contribution
name|prevResource
operator|.
name|multiplyBy
argument_list|(
name|currTime
operator|-
name|prevTime
argument_list|)
expr_stmt|;
name|runningTot
operator|.
name|add
argument_list|(
name|prevResource
argument_list|)
expr_stmt|;
name|integralUp
operator|.
name|put
argument_list|(
name|currTime
argument_list|,
name|normalizeToResource
argument_list|(
name|runningTot
argument_list|,
name|validWindow
argument_list|)
argument_list|)
expr_stmt|;
name|integralDown
operator|.
name|put
argument_list|(
name|currTime
operator|+
name|validWindow
argument_list|,
name|normalizeToResource
argument_list|(
name|runningTot
argument_list|,
name|validWindow
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|currResource
operator|!=
literal|null
condition|)
block|{
name|prevResource
operator|.
name|memory
operator|=
name|currResource
operator|.
name|getMemorySize
argument_list|()
expr_stmt|;
name|prevResource
operator|.
name|vcores
operator|=
name|currResource
operator|.
name|getVirtualCores
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|prevResource
operator|.
name|memory
operator|=
literal|0L
expr_stmt|;
name|prevResource
operator|.
name|vcores
operator|=
literal|0L
expr_stmt|;
block|}
name|prevTime
operator|=
name|currTime
expr_stmt|;
block|}
comment|// compute final integral as delta of up minus down transitions
name|RLESparseResourceAllocation
name|intUp
init|=
operator|new
name|RLESparseResourceAllocation
argument_list|(
name|integralUp
argument_list|,
name|plan
operator|.
name|getResourceCalculator
argument_list|()
argument_list|)
decl_stmt|;
name|RLESparseResourceAllocation
name|intDown
init|=
operator|new
name|RLESparseResourceAllocation
argument_list|(
name|integralDown
argument_list|,
name|plan
operator|.
name|getResourceCalculator
argument_list|()
argument_list|)
decl_stmt|;
name|RLESparseResourceAllocation
name|integral
init|=
name|RLESparseResourceAllocation
operator|.
name|merge
argument_list|(
name|plan
operator|.
name|getResourceCalculator
argument_list|()
argument_list|,
name|plan
operator|.
name|getTotalCapacity
argument_list|()
argument_list|,
name|intUp
argument_list|,
name|intDown
argument_list|,
name|RLEOperator
operator|.
name|subtract
argument_list|,
name|Long
operator|.
name|MIN_VALUE
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
comment|// define over-time integral limit
comment|// note: this is aligned with the normalization done above
name|NavigableMap
argument_list|<
name|Long
argument_list|,
name|Resource
argument_list|>
name|tlimit
init|=
operator|new
name|TreeMap
argument_list|<>
argument_list|()
decl_stmt|;
name|Resource
name|maxAvgRes
init|=
name|Resources
operator|.
name|multiply
argument_list|(
name|plan
operator|.
name|getTotalCapacity
argument_list|()
argument_list|,
name|maxAvg
argument_list|)
decl_stmt|;
name|tlimit
operator|.
name|put
argument_list|(
name|toCheck
operator|.
name|getEarliestStartTime
argument_list|()
operator|-
name|validWindow
argument_list|,
name|maxAvgRes
argument_list|)
expr_stmt|;
name|RLESparseResourceAllocation
name|targetLimit
init|=
operator|new
name|RLESparseResourceAllocation
argument_list|(
name|tlimit
argument_list|,
name|plan
operator|.
name|getResourceCalculator
argument_list|()
argument_list|)
decl_stmt|;
comment|// compare using merge() limit with integral
try|try
block|{
name|RLESparseResourceAllocation
operator|.
name|merge
argument_list|(
name|plan
operator|.
name|getResourceCalculator
argument_list|()
argument_list|,
name|plan
operator|.
name|getTotalCapacity
argument_list|()
argument_list|,
name|targetLimit
argument_list|,
name|integral
argument_list|,
name|RLEOperator
operator|.
name|subtractTestNonNegative
argument_list|,
name|checkStart
argument_list|,
name|checkEnd
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PlanningException
name|p
parameter_list|)
block|{
throw|throw
operator|new
name|PlanningQuotaException
argument_list|(
literal|"Integral (avg over time) quota capacity "
operator|+
name|maxAvg
operator|+
literal|" over a window of "
operator|+
name|validWindow
operator|/
literal|1000
operator|+
literal|" seconds, "
operator|+
literal|" would be exceeded by accepting reservation: "
operator|+
name|reservation
operator|.
name|getReservationId
argument_list|()
argument_list|,
name|p
argument_list|)
throw|;
block|}
block|}
DECL|method|normalizeToResource (IntegralResource runningTot, long window)
specifier|private
name|Resource
name|normalizeToResource
parameter_list|(
name|IntegralResource
name|runningTot
parameter_list|,
name|long
name|window
parameter_list|)
block|{
comment|// normalize to fit in windows. Rounding should not impact more than
comment|// sub 1 core average allocations. This will all be removed once
comment|// Resource moves to long.
name|int
name|memory
init|=
operator|(
name|int
operator|)
name|Math
operator|.
name|round
argument_list|(
operator|(
name|double
operator|)
name|runningTot
operator|.
name|memory
operator|/
name|window
argument_list|)
decl_stmt|;
name|int
name|vcores
init|=
operator|(
name|int
operator|)
name|Math
operator|.
name|round
argument_list|(
operator|(
name|double
operator|)
name|runningTot
operator|.
name|vcores
operator|/
name|window
argument_list|)
decl_stmt|;
return|return
name|Resource
operator|.
name|newInstance
argument_list|(
name|memory
argument_list|,
name|vcores
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|availableResources ( RLESparseResourceAllocation available, Plan plan, String user, ReservationId oldId, long start, long end)
specifier|public
name|RLESparseResourceAllocation
name|availableResources
parameter_list|(
name|RLESparseResourceAllocation
name|available
parameter_list|,
name|Plan
name|plan
parameter_list|,
name|String
name|user
parameter_list|,
name|ReservationId
name|oldId
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
comment|// this only propagates the instantaneous maxInst properties, while
comment|// the time-varying one depends on the current allocation as well
comment|// and are not easily captured here
name|Resource
name|planTotalCapacity
init|=
name|plan
operator|.
name|getTotalCapacity
argument_list|()
decl_stmt|;
name|Resource
name|maxInsRes
init|=
name|Resources
operator|.
name|multiply
argument_list|(
name|planTotalCapacity
argument_list|,
name|maxInst
argument_list|)
decl_stmt|;
name|NavigableMap
argument_list|<
name|Long
argument_list|,
name|Resource
argument_list|>
name|instQuota
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
name|instQuota
operator|.
name|put
argument_list|(
name|start
argument_list|,
name|maxInsRes
argument_list|)
expr_stmt|;
name|RLESparseResourceAllocation
name|instRLEQuota
init|=
operator|new
name|RLESparseResourceAllocation
argument_list|(
name|instQuota
argument_list|,
name|plan
operator|.
name|getResourceCalculator
argument_list|()
argument_list|)
decl_stmt|;
name|RLESparseResourceAllocation
name|used
init|=
name|plan
operator|.
name|getConsumptionForUserOverTime
argument_list|(
name|user
argument_list|,
name|start
argument_list|,
name|end
argument_list|)
decl_stmt|;
comment|// add back in old reservation used resources if any
name|ReservationAllocation
name|old
init|=
name|plan
operator|.
name|getReservationById
argument_list|(
name|oldId
argument_list|)
decl_stmt|;
if|if
condition|(
name|old
operator|!=
literal|null
condition|)
block|{
name|used
operator|=
name|RLESparseResourceAllocation
operator|.
name|merge
argument_list|(
name|plan
operator|.
name|getResourceCalculator
argument_list|()
argument_list|,
name|Resources
operator|.
name|clone
argument_list|(
name|plan
operator|.
name|getTotalCapacity
argument_list|()
argument_list|)
argument_list|,
name|used
argument_list|,
name|old
operator|.
name|getResourcesOverTime
argument_list|(
name|start
argument_list|,
name|end
argument_list|)
argument_list|,
name|RLEOperator
operator|.
name|subtract
argument_list|,
name|start
argument_list|,
name|end
argument_list|)
expr_stmt|;
block|}
name|instRLEQuota
operator|=
name|RLESparseResourceAllocation
operator|.
name|merge
argument_list|(
name|plan
operator|.
name|getResourceCalculator
argument_list|()
argument_list|,
name|planTotalCapacity
argument_list|,
name|instRLEQuota
argument_list|,
name|used
argument_list|,
name|RLEOperator
operator|.
name|subtract
argument_list|,
name|start
argument_list|,
name|end
argument_list|)
expr_stmt|;
name|instRLEQuota
operator|=
name|RLESparseResourceAllocation
operator|.
name|merge
argument_list|(
name|plan
operator|.
name|getResourceCalculator
argument_list|()
argument_list|,
name|planTotalCapacity
argument_list|,
name|available
argument_list|,
name|instRLEQuota
argument_list|,
name|RLEOperator
operator|.
name|min
argument_list|,
name|start
argument_list|,
name|end
argument_list|)
expr_stmt|;
return|return
name|instRLEQuota
return|;
block|}
annotation|@
name|Override
DECL|method|getValidWindow ()
specifier|public
name|long
name|getValidWindow
parameter_list|()
block|{
return|return
name|validWindow
return|;
block|}
comment|/**    * This class provides support for Resource-like book-keeping, based on    * long(s), as using Resource to store the "integral" of the allocation over    * time leads to integer overflows for large allocations/clusters. (Evolving    * Resource to use long is too disruptive at this point.)    *    * The comparison/multiplication behaviors of IntegralResource are consistent    * with the DefaultResourceCalculator.    */
DECL|class|IntegralResource
specifier|private
specifier|static
class|class
name|IntegralResource
block|{
DECL|field|memory
name|long
name|memory
decl_stmt|;
DECL|field|vcores
name|long
name|vcores
decl_stmt|;
DECL|method|IntegralResource (Resource resource)
specifier|public
name|IntegralResource
parameter_list|(
name|Resource
name|resource
parameter_list|)
block|{
name|this
operator|.
name|memory
operator|=
name|resource
operator|.
name|getMemorySize
argument_list|()
expr_stmt|;
name|this
operator|.
name|vcores
operator|=
name|resource
operator|.
name|getVirtualCores
argument_list|()
expr_stmt|;
block|}
DECL|method|IntegralResource (long mem, long vcores)
specifier|public
name|IntegralResource
parameter_list|(
name|long
name|mem
parameter_list|,
name|long
name|vcores
parameter_list|)
block|{
name|this
operator|.
name|memory
operator|=
name|mem
expr_stmt|;
name|this
operator|.
name|vcores
operator|=
name|vcores
expr_stmt|;
block|}
DECL|method|add (Resource r)
specifier|public
name|void
name|add
parameter_list|(
name|Resource
name|r
parameter_list|)
block|{
name|memory
operator|+=
name|r
operator|.
name|getMemorySize
argument_list|()
expr_stmt|;
name|vcores
operator|+=
name|r
operator|.
name|getVirtualCores
argument_list|()
expr_stmt|;
block|}
DECL|method|add (IntegralResource r)
specifier|public
name|void
name|add
parameter_list|(
name|IntegralResource
name|r
parameter_list|)
block|{
name|memory
operator|+=
name|r
operator|.
name|memory
expr_stmt|;
name|vcores
operator|+=
name|r
operator|.
name|vcores
expr_stmt|;
block|}
DECL|method|subtract (Resource r)
specifier|public
name|void
name|subtract
parameter_list|(
name|Resource
name|r
parameter_list|)
block|{
name|memory
operator|-=
name|r
operator|.
name|getMemorySize
argument_list|()
expr_stmt|;
name|vcores
operator|-=
name|r
operator|.
name|getVirtualCores
argument_list|()
expr_stmt|;
block|}
DECL|method|negate ()
specifier|public
name|IntegralResource
name|negate
parameter_list|()
block|{
return|return
operator|new
name|IntegralResource
argument_list|(
operator|-
name|memory
argument_list|,
operator|-
name|vcores
argument_list|)
return|;
block|}
DECL|method|multiplyBy (long window)
specifier|public
name|void
name|multiplyBy
parameter_list|(
name|long
name|window
parameter_list|)
block|{
name|memory
operator|=
name|memory
operator|*
name|window
expr_stmt|;
name|vcores
operator|=
name|vcores
operator|*
name|window
expr_stmt|;
block|}
DECL|method|compareTo (IntegralResource other)
specifier|public
name|long
name|compareTo
parameter_list|(
name|IntegralResource
name|other
parameter_list|)
block|{
name|long
name|diff
init|=
name|memory
operator|-
name|other
operator|.
name|memory
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
name|vcores
operator|-
name|other
operator|.
name|vcores
expr_stmt|;
block|}
return|return
name|diff
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
return|return
literal|"<memory:"
operator|+
name|memory
operator|+
literal|", vCores:"
operator|+
name|vcores
operator|+
literal|">"
return|;
block|}
block|}
block|}
end_class

end_unit

