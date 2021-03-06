begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.webapp.dao
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
name|webapp
operator|.
name|dao
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
name|ReservationAllocationState
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
name|ResourceAllocationRequest
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
name|ReservationInterval
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlAccessType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlAccessorType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlElement
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlRootElement
import|;
end_import

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

begin_comment
comment|/**  * Simple class that represent a reservation.  */
end_comment

begin_class
annotation|@
name|XmlRootElement
annotation|@
name|XmlAccessorType
argument_list|(
name|XmlAccessType
operator|.
name|FIELD
argument_list|)
DECL|class|ReservationInfo
specifier|public
class|class
name|ReservationInfo
block|{
annotation|@
name|XmlElement
argument_list|(
name|name
operator|=
literal|"acceptance-time"
argument_list|)
DECL|field|acceptanceTime
specifier|private
name|long
name|acceptanceTime
decl_stmt|;
DECL|field|user
specifier|private
name|String
name|user
decl_stmt|;
annotation|@
name|XmlElement
argument_list|(
name|name
operator|=
literal|"resource-allocations"
argument_list|)
DECL|field|resourceAllocations
specifier|private
name|List
argument_list|<
name|ResourceAllocationInfo
argument_list|>
name|resourceAllocations
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
annotation|@
name|XmlElement
argument_list|(
name|name
operator|=
literal|"reservation-id"
argument_list|)
DECL|field|reservationId
specifier|private
name|String
name|reservationId
decl_stmt|;
annotation|@
name|XmlElement
argument_list|(
name|name
operator|=
literal|"reservation-definition"
argument_list|)
DECL|field|reservationDefinition
specifier|private
name|ReservationDefinitionInfo
name|reservationDefinition
decl_stmt|;
DECL|method|ReservationInfo ()
specifier|public
name|ReservationInfo
parameter_list|()
block|{
name|acceptanceTime
operator|=
literal|0
expr_stmt|;
name|user
operator|=
literal|""
expr_stmt|;
name|reservationDefinition
operator|=
operator|new
name|ReservationDefinitionInfo
argument_list|()
expr_stmt|;
block|}
DECL|method|ReservationInfo (ReservationAllocationState allocation, boolean includeResourceAllocations)
specifier|public
name|ReservationInfo
parameter_list|(
name|ReservationAllocationState
name|allocation
parameter_list|,
name|boolean
name|includeResourceAllocations
parameter_list|)
throws|throws
name|Exception
block|{
name|acceptanceTime
operator|=
name|allocation
operator|.
name|getAcceptanceTime
argument_list|()
expr_stmt|;
name|user
operator|=
name|allocation
operator|.
name|getUser
argument_list|()
expr_stmt|;
if|if
condition|(
name|includeResourceAllocations
condition|)
block|{
name|List
argument_list|<
name|ResourceAllocationRequest
argument_list|>
name|requests
init|=
name|allocation
operator|.
name|getResourceAllocationRequests
argument_list|()
decl_stmt|;
for|for
control|(
name|ResourceAllocationRequest
name|request
range|:
name|requests
control|)
block|{
name|resourceAllocations
operator|.
name|add
argument_list|(
operator|new
name|ResourceAllocationInfo
argument_list|(
operator|new
name|ReservationInterval
argument_list|(
name|request
operator|.
name|getStartTime
argument_list|()
argument_list|,
name|request
operator|.
name|getEndTime
argument_list|()
argument_list|)
argument_list|,
name|request
operator|.
name|getCapability
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|reservationId
operator|=
name|allocation
operator|.
name|getReservationId
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
name|reservationDefinition
operator|=
operator|new
name|ReservationDefinitionInfo
argument_list|(
name|allocation
operator|.
name|getReservationDefinition
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|getUser ()
specifier|public
name|String
name|getUser
parameter_list|()
block|{
return|return
name|user
return|;
block|}
DECL|method|setUser (String newUser)
specifier|public
name|void
name|setUser
parameter_list|(
name|String
name|newUser
parameter_list|)
block|{
name|user
operator|=
name|newUser
expr_stmt|;
block|}
DECL|method|getAcceptanceTime ()
specifier|public
name|long
name|getAcceptanceTime
parameter_list|()
block|{
return|return
name|acceptanceTime
return|;
block|}
DECL|method|getResourceAllocations ()
specifier|public
name|List
argument_list|<
name|ResourceAllocationInfo
argument_list|>
name|getResourceAllocations
parameter_list|()
block|{
return|return
name|resourceAllocations
return|;
block|}
DECL|method|getReservationId ()
specifier|public
name|String
name|getReservationId
parameter_list|()
block|{
return|return
name|reservationId
return|;
block|}
DECL|method|getReservationDefinition ()
specifier|public
name|ReservationDefinitionInfo
name|getReservationDefinition
parameter_list|()
block|{
return|return
name|reservationDefinition
return|;
block|}
block|}
end_class

end_unit

