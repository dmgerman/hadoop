begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.recovery
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
name|recovery
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
name|proto
operator|.
name|YarnServerResourceManagerRecoveryProtos
operator|.
name|ReservationAllocationStateProto
import|;
end_import

begin_comment
comment|/**  * Event representing maintaining ReservationSystem state.  */
end_comment

begin_class
DECL|class|RMStateStoreStoreReservationEvent
specifier|public
class|class
name|RMStateStoreStoreReservationEvent
extends|extends
name|RMStateStoreEvent
block|{
DECL|field|reservationAllocation
specifier|private
name|ReservationAllocationStateProto
name|reservationAllocation
decl_stmt|;
DECL|field|planName
specifier|private
name|String
name|planName
decl_stmt|;
DECL|field|reservationIdName
specifier|private
name|String
name|reservationIdName
decl_stmt|;
DECL|method|RMStateStoreStoreReservationEvent (RMStateStoreEventType type)
specifier|public
name|RMStateStoreStoreReservationEvent
parameter_list|(
name|RMStateStoreEventType
name|type
parameter_list|)
block|{
name|super
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
DECL|method|RMStateStoreStoreReservationEvent ( ReservationAllocationStateProto reservationAllocationState, RMStateStoreEventType type, String planName, String reservationIdName)
specifier|public
name|RMStateStoreStoreReservationEvent
parameter_list|(
name|ReservationAllocationStateProto
name|reservationAllocationState
parameter_list|,
name|RMStateStoreEventType
name|type
parameter_list|,
name|String
name|planName
parameter_list|,
name|String
name|reservationIdName
parameter_list|)
block|{
name|this
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|this
operator|.
name|reservationAllocation
operator|=
name|reservationAllocationState
expr_stmt|;
name|this
operator|.
name|planName
operator|=
name|planName
expr_stmt|;
name|this
operator|.
name|reservationIdName
operator|=
name|reservationIdName
expr_stmt|;
block|}
DECL|method|getReservationAllocation ()
specifier|public
name|ReservationAllocationStateProto
name|getReservationAllocation
parameter_list|()
block|{
return|return
name|reservationAllocation
return|;
block|}
DECL|method|getPlanName ()
specifier|public
name|String
name|getPlanName
parameter_list|()
block|{
return|return
name|planName
return|;
block|}
DECL|method|getReservationIdName ()
specifier|public
name|String
name|getReservationIdName
parameter_list|()
block|{
return|return
name|reservationIdName
return|;
block|}
block|}
end_class

end_unit

