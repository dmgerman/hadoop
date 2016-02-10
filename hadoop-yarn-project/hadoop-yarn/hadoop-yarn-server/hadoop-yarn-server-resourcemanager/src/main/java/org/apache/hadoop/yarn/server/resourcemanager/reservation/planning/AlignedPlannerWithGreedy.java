begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.reservation.planning
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
operator|.
name|planning
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|ReservationDefinition
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
name|server
operator|.
name|resourcemanager
operator|.
name|reservation
operator|.
name|Plan
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
comment|/**  * A planning algorithm that first runs LowCostAligned, and if it fails runs  * Greedy.  */
end_comment

begin_class
DECL|class|AlignedPlannerWithGreedy
specifier|public
class|class
name|AlignedPlannerWithGreedy
implements|implements
name|ReservationAgent
block|{
comment|// Default smoothness factor
DECL|field|DEFAULT_SMOOTHNESS_FACTOR
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_SMOOTHNESS_FACTOR
init|=
literal|10
decl_stmt|;
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
name|AlignedPlannerWithGreedy
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// Smoothness factor
DECL|field|planner
specifier|private
specifier|final
name|ReservationAgent
name|planner
decl_stmt|;
comment|// Constructor
DECL|method|AlignedPlannerWithGreedy ()
specifier|public
name|AlignedPlannerWithGreedy
parameter_list|()
block|{
name|this
argument_list|(
name|DEFAULT_SMOOTHNESS_FACTOR
argument_list|)
expr_stmt|;
block|}
comment|// Constructor
DECL|method|AlignedPlannerWithGreedy (int smoothnessFactor)
specifier|public
name|AlignedPlannerWithGreedy
parameter_list|(
name|int
name|smoothnessFactor
parameter_list|)
block|{
comment|// List of algorithms
name|List
argument_list|<
name|ReservationAgent
argument_list|>
name|listAlg
init|=
operator|new
name|LinkedList
argument_list|<
name|ReservationAgent
argument_list|>
argument_list|()
decl_stmt|;
comment|// LowCostAligned planning algorithm
name|ReservationAgent
name|algAligned
init|=
operator|new
name|IterativePlanner
argument_list|(
operator|new
name|StageEarliestStartByDemand
argument_list|()
argument_list|,
operator|new
name|StageAllocatorLowCostAligned
argument_list|(
name|smoothnessFactor
argument_list|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|listAlg
operator|.
name|add
argument_list|(
name|algAligned
argument_list|)
expr_stmt|;
comment|// Greedy planning algorithm
name|ReservationAgent
name|algGreedy
init|=
operator|new
name|IterativePlanner
argument_list|(
operator|new
name|StageEarliestStartByJobArrival
argument_list|()
argument_list|,
operator|new
name|StageAllocatorGreedy
argument_list|()
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|listAlg
operator|.
name|add
argument_list|(
name|algGreedy
argument_list|)
expr_stmt|;
comment|// Set planner:
comment|// 1. Attempt to execute algAligned
comment|// 2. If failed, fall back to algGreedy
name|planner
operator|=
operator|new
name|TryManyReservationAgents
argument_list|(
name|listAlg
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createReservation (ReservationId reservationId, String user, Plan plan, ReservationDefinition contract)
specifier|public
name|boolean
name|createReservation
parameter_list|(
name|ReservationId
name|reservationId
parameter_list|,
name|String
name|user
parameter_list|,
name|Plan
name|plan
parameter_list|,
name|ReservationDefinition
name|contract
parameter_list|)
throws|throws
name|PlanningException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"placing the following ReservationRequest: "
operator|+
name|contract
argument_list|)
expr_stmt|;
try|try
block|{
name|boolean
name|res
init|=
name|planner
operator|.
name|createReservation
argument_list|(
name|reservationId
argument_list|,
name|user
argument_list|,
name|plan
argument_list|,
name|contract
argument_list|)
decl_stmt|;
if|if
condition|(
name|res
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"OUTCOME: SUCCESS, Reservation ID: "
operator|+
name|reservationId
operator|.
name|toString
argument_list|()
operator|+
literal|", Contract: "
operator|+
name|contract
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"OUTCOME: FAILURE, Reservation ID: "
operator|+
name|reservationId
operator|.
name|toString
argument_list|()
operator|+
literal|", Contract: "
operator|+
name|contract
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|res
return|;
block|}
catch|catch
parameter_list|(
name|PlanningException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"OUTCOME: FAILURE, Reservation ID: "
operator|+
name|reservationId
operator|.
name|toString
argument_list|()
operator|+
literal|", Contract: "
operator|+
name|contract
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|updateReservation (ReservationId reservationId, String user, Plan plan, ReservationDefinition contract)
specifier|public
name|boolean
name|updateReservation
parameter_list|(
name|ReservationId
name|reservationId
parameter_list|,
name|String
name|user
parameter_list|,
name|Plan
name|plan
parameter_list|,
name|ReservationDefinition
name|contract
parameter_list|)
throws|throws
name|PlanningException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"updating the following ReservationRequest: "
operator|+
name|contract
argument_list|)
expr_stmt|;
return|return
name|planner
operator|.
name|updateReservation
argument_list|(
name|reservationId
argument_list|,
name|user
argument_list|,
name|plan
argument_list|,
name|contract
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|deleteReservation (ReservationId reservationId, String user, Plan plan)
specifier|public
name|boolean
name|deleteReservation
parameter_list|(
name|ReservationId
name|reservationId
parameter_list|,
name|String
name|user
parameter_list|,
name|Plan
name|plan
parameter_list|)
throws|throws
name|PlanningException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"removing the following ReservationId: "
operator|+
name|reservationId
argument_list|)
expr_stmt|;
return|return
name|planner
operator|.
name|deleteReservation
argument_list|(
name|reservationId
argument_list|,
name|user
argument_list|,
name|plan
argument_list|)
return|;
block|}
block|}
end_class

end_unit

