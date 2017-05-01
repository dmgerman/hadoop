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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|conf
operator|.
name|Configuration
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
comment|/**  * This Agent employs a simple greedy placement strategy, placing the various  * stages of a {@link ReservationDefinition} from the deadline moving backward  * towards the arrival. This allows jobs with earlier deadline to be scheduled  * greedily as well. Combined with an opportunistic anticipation of work if the  * cluster is not fully utilized also seems to provide good latency for  * best-effort jobs (i.e., jobs running without a reservation).  *  * This agent does not account for locality and only consider container  * granularity for validation purposes (i.e., you can't exceed max-container  * size).  */
end_comment

begin_class
DECL|class|GreedyReservationAgent
specifier|public
class|class
name|GreedyReservationAgent
implements|implements
name|ReservationAgent
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
name|GreedyReservationAgent
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// Greedy planner
DECL|field|planner
specifier|private
name|ReservationAgent
name|planner
decl_stmt|;
DECL|field|allocateLeft
specifier|private
name|boolean
name|allocateLeft
decl_stmt|;
DECL|method|GreedyReservationAgent ()
specifier|public
name|GreedyReservationAgent
parameter_list|()
block|{   }
annotation|@
name|Override
DECL|method|init (Configuration conf)
specifier|public
name|void
name|init
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|allocateLeft
operator|=
name|conf
operator|.
name|getBoolean
argument_list|(
name|FAVOR_EARLY_ALLOCATION
argument_list|,
name|DEFAULT_GREEDY_FAVOR_EARLY_ALLOCATION
argument_list|)
expr_stmt|;
if|if
condition|(
name|allocateLeft
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Initializing the GreedyReservationAgent to favor \"early\""
operator|+
literal|" (left) allocations (controlled by parameter: "
operator|+
name|FAVOR_EARLY_ALLOCATION
operator|+
literal|")"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Initializing the GreedyReservationAgent to favor \"late\""
operator|+
literal|" (right) allocations (controlled by parameter: "
operator|+
name|FAVOR_EARLY_ALLOCATION
operator|+
literal|")"
argument_list|)
expr_stmt|;
block|}
name|planner
operator|=
operator|new
name|IterativePlanner
argument_list|(
operator|new
name|StageExecutionIntervalUnconstrained
argument_list|()
argument_list|,
operator|new
name|StageAllocatorGreedyRLE
argument_list|(
name|allocateLeft
argument_list|)
argument_list|,
name|allocateLeft
argument_list|)
expr_stmt|;
block|}
DECL|method|isAllocateLeft ()
specifier|public
name|boolean
name|isAllocateLeft
parameter_list|()
block|{
return|return
name|allocateLeft
return|;
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

