begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.common
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
name|scheduler
operator|.
name|common
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|ContainerId
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
name|rmcontainer
operator|.
name|RMContainer
import|;
end_import

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|AssignmentInformation
specifier|public
class|class
name|AssignmentInformation
block|{
DECL|enum|Operation
specifier|public
enum|enum
name|Operation
block|{
DECL|enumConstant|ALLOCATION
DECL|enumConstant|RESERVATION
name|ALLOCATION
block|,
name|RESERVATION
block|;
DECL|field|SIZE
specifier|private
specifier|static
name|int
name|SIZE
init|=
name|Operation
operator|.
name|values
argument_list|()
operator|.
name|length
decl_stmt|;
DECL|method|size ()
specifier|static
name|int
name|size
parameter_list|()
block|{
return|return
name|SIZE
return|;
block|}
block|}
DECL|class|AssignmentDetails
specifier|public
specifier|static
class|class
name|AssignmentDetails
block|{
DECL|field|rmContainer
specifier|public
name|RMContainer
name|rmContainer
decl_stmt|;
DECL|field|containerId
specifier|public
name|ContainerId
name|containerId
decl_stmt|;
DECL|field|queue
specifier|public
name|String
name|queue
decl_stmt|;
DECL|method|AssignmentDetails (RMContainer rmContainer, String queue)
specifier|public
name|AssignmentDetails
parameter_list|(
name|RMContainer
name|rmContainer
parameter_list|,
name|String
name|queue
parameter_list|)
block|{
name|this
operator|.
name|containerId
operator|=
name|rmContainer
operator|.
name|getContainerId
argument_list|()
expr_stmt|;
name|this
operator|.
name|rmContainer
operator|=
name|rmContainer
expr_stmt|;
name|this
operator|.
name|queue
operator|=
name|queue
expr_stmt|;
block|}
block|}
DECL|field|operationCounts
specifier|private
specifier|final
name|int
index|[]
name|operationCounts
decl_stmt|;
DECL|field|operationResources
specifier|private
specifier|final
name|Resource
index|[]
name|operationResources
decl_stmt|;
DECL|field|operationDetails
specifier|private
specifier|final
name|List
argument_list|<
name|AssignmentDetails
argument_list|>
index|[]
name|operationDetails
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|AssignmentInformation ()
specifier|public
name|AssignmentInformation
parameter_list|()
block|{
name|int
name|numOps
init|=
name|Operation
operator|.
name|size
argument_list|()
decl_stmt|;
name|this
operator|.
name|operationCounts
operator|=
operator|new
name|int
index|[
name|numOps
index|]
expr_stmt|;
name|this
operator|.
name|operationResources
operator|=
operator|new
name|Resource
index|[
name|numOps
index|]
expr_stmt|;
name|this
operator|.
name|operationDetails
operator|=
operator|new
name|List
index|[
name|numOps
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numOps
condition|;
name|i
operator|++
control|)
block|{
name|operationCounts
index|[
name|i
index|]
operator|=
literal|0
expr_stmt|;
name|operationResources
index|[
name|i
index|]
operator|=
name|Resource
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|operationDetails
index|[
name|i
index|]
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|getNumAllocations ()
specifier|public
name|int
name|getNumAllocations
parameter_list|()
block|{
return|return
name|operationCounts
index|[
name|Operation
operator|.
name|ALLOCATION
operator|.
name|ordinal
argument_list|()
index|]
return|;
block|}
DECL|method|incrAllocations ()
specifier|public
name|void
name|incrAllocations
parameter_list|()
block|{
name|increment
argument_list|(
name|Operation
operator|.
name|ALLOCATION
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|incrAllocations (int by)
specifier|public
name|void
name|incrAllocations
parameter_list|(
name|int
name|by
parameter_list|)
block|{
name|increment
argument_list|(
name|Operation
operator|.
name|ALLOCATION
argument_list|,
name|by
argument_list|)
expr_stmt|;
block|}
DECL|method|getNumReservations ()
specifier|public
name|int
name|getNumReservations
parameter_list|()
block|{
return|return
name|operationCounts
index|[
name|Operation
operator|.
name|RESERVATION
operator|.
name|ordinal
argument_list|()
index|]
return|;
block|}
DECL|method|incrReservations ()
specifier|public
name|void
name|incrReservations
parameter_list|()
block|{
name|increment
argument_list|(
name|Operation
operator|.
name|RESERVATION
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|incrReservations (int by)
specifier|public
name|void
name|incrReservations
parameter_list|(
name|int
name|by
parameter_list|)
block|{
name|increment
argument_list|(
name|Operation
operator|.
name|RESERVATION
argument_list|,
name|by
argument_list|)
expr_stmt|;
block|}
DECL|method|increment (Operation op, int by)
specifier|private
name|void
name|increment
parameter_list|(
name|Operation
name|op
parameter_list|,
name|int
name|by
parameter_list|)
block|{
name|operationCounts
index|[
name|op
operator|.
name|ordinal
argument_list|()
index|]
operator|+=
name|by
expr_stmt|;
block|}
DECL|method|getAllocated ()
specifier|public
name|Resource
name|getAllocated
parameter_list|()
block|{
return|return
name|operationResources
index|[
name|Operation
operator|.
name|ALLOCATION
operator|.
name|ordinal
argument_list|()
index|]
return|;
block|}
DECL|method|getReserved ()
specifier|public
name|Resource
name|getReserved
parameter_list|()
block|{
return|return
name|operationResources
index|[
name|Operation
operator|.
name|RESERVATION
operator|.
name|ordinal
argument_list|()
index|]
return|;
block|}
DECL|method|addAssignmentDetails (Operation op, RMContainer rmContainer, String queue)
specifier|private
name|void
name|addAssignmentDetails
parameter_list|(
name|Operation
name|op
parameter_list|,
name|RMContainer
name|rmContainer
parameter_list|,
name|String
name|queue
parameter_list|)
block|{
name|getDetails
argument_list|(
name|op
argument_list|)
operator|.
name|add
argument_list|(
operator|new
name|AssignmentDetails
argument_list|(
name|rmContainer
argument_list|,
name|queue
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|addAllocationDetails (RMContainer rmContainer, String queue)
specifier|public
name|void
name|addAllocationDetails
parameter_list|(
name|RMContainer
name|rmContainer
parameter_list|,
name|String
name|queue
parameter_list|)
block|{
name|addAssignmentDetails
argument_list|(
name|Operation
operator|.
name|ALLOCATION
argument_list|,
name|rmContainer
argument_list|,
name|queue
argument_list|)
expr_stmt|;
block|}
DECL|method|addReservationDetails (RMContainer rmContainer, String queue)
specifier|public
name|void
name|addReservationDetails
parameter_list|(
name|RMContainer
name|rmContainer
parameter_list|,
name|String
name|queue
parameter_list|)
block|{
name|addAssignmentDetails
argument_list|(
name|Operation
operator|.
name|RESERVATION
argument_list|,
name|rmContainer
argument_list|,
name|queue
argument_list|)
expr_stmt|;
block|}
DECL|method|getDetails (Operation op)
specifier|private
name|List
argument_list|<
name|AssignmentDetails
argument_list|>
name|getDetails
parameter_list|(
name|Operation
name|op
parameter_list|)
block|{
return|return
name|operationDetails
index|[
name|op
operator|.
name|ordinal
argument_list|()
index|]
return|;
block|}
DECL|method|getAllocationDetails ()
specifier|public
name|List
argument_list|<
name|AssignmentDetails
argument_list|>
name|getAllocationDetails
parameter_list|()
block|{
return|return
name|getDetails
argument_list|(
name|Operation
operator|.
name|ALLOCATION
argument_list|)
return|;
block|}
DECL|method|getReservationDetails ()
specifier|public
name|List
argument_list|<
name|AssignmentDetails
argument_list|>
name|getReservationDetails
parameter_list|()
block|{
return|return
name|getDetails
argument_list|(
name|Operation
operator|.
name|RESERVATION
argument_list|)
return|;
block|}
DECL|method|getFirstRMContainerFromOperation (Operation op)
specifier|private
name|RMContainer
name|getFirstRMContainerFromOperation
parameter_list|(
name|Operation
name|op
parameter_list|)
block|{
name|List
argument_list|<
name|AssignmentDetails
argument_list|>
name|assignDetails
init|=
name|getDetails
argument_list|(
name|op
argument_list|)
decl_stmt|;
if|if
condition|(
name|assignDetails
operator|!=
literal|null
operator|&&
operator|!
name|assignDetails
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|assignDetails
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|rmContainer
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|getFirstAllocatedOrReservedRMContainer ()
specifier|public
name|RMContainer
name|getFirstAllocatedOrReservedRMContainer
parameter_list|()
block|{
name|RMContainer
name|rmContainer
decl_stmt|;
name|rmContainer
operator|=
name|getFirstRMContainerFromOperation
argument_list|(
name|Operation
operator|.
name|ALLOCATION
argument_list|)
expr_stmt|;
if|if
condition|(
literal|null
operator|!=
name|rmContainer
condition|)
block|{
return|return
name|rmContainer
return|;
block|}
return|return
name|getFirstRMContainerFromOperation
argument_list|(
name|Operation
operator|.
name|RESERVATION
argument_list|)
return|;
block|}
DECL|method|getFirstAllocatedOrReservedContainerId ()
specifier|public
name|ContainerId
name|getFirstAllocatedOrReservedContainerId
parameter_list|()
block|{
name|RMContainer
name|rmContainer
init|=
name|getFirstAllocatedOrReservedRMContainer
argument_list|()
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|rmContainer
condition|)
block|{
return|return
name|rmContainer
operator|.
name|getContainerId
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

