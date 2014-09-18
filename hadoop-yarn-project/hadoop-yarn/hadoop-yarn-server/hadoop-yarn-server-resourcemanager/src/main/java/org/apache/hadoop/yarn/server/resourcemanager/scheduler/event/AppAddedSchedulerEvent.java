begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.event
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
name|event
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
name|ApplicationId
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

begin_class
DECL|class|AppAddedSchedulerEvent
specifier|public
class|class
name|AppAddedSchedulerEvent
extends|extends
name|SchedulerEvent
block|{
DECL|field|applicationId
specifier|private
specifier|final
name|ApplicationId
name|applicationId
decl_stmt|;
DECL|field|queue
specifier|private
specifier|final
name|String
name|queue
decl_stmt|;
DECL|field|user
specifier|private
specifier|final
name|String
name|user
decl_stmt|;
DECL|field|reservationID
specifier|private
specifier|final
name|ReservationId
name|reservationID
decl_stmt|;
DECL|field|isAppRecovering
specifier|private
specifier|final
name|boolean
name|isAppRecovering
decl_stmt|;
DECL|method|AppAddedSchedulerEvent ( ApplicationId applicationId, String queue, String user)
specifier|public
name|AppAddedSchedulerEvent
parameter_list|(
name|ApplicationId
name|applicationId
parameter_list|,
name|String
name|queue
parameter_list|,
name|String
name|user
parameter_list|)
block|{
name|this
argument_list|(
name|applicationId
argument_list|,
name|queue
argument_list|,
name|user
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|AppAddedSchedulerEvent (ApplicationId applicationId, String queue, String user, ReservationId reservationID)
specifier|public
name|AppAddedSchedulerEvent
parameter_list|(
name|ApplicationId
name|applicationId
parameter_list|,
name|String
name|queue
parameter_list|,
name|String
name|user
parameter_list|,
name|ReservationId
name|reservationID
parameter_list|)
block|{
name|this
argument_list|(
name|applicationId
argument_list|,
name|queue
argument_list|,
name|user
argument_list|,
literal|false
argument_list|,
name|reservationID
argument_list|)
expr_stmt|;
block|}
DECL|method|AppAddedSchedulerEvent (ApplicationId applicationId, String queue, String user, boolean isAppRecovering, ReservationId reservationID)
specifier|public
name|AppAddedSchedulerEvent
parameter_list|(
name|ApplicationId
name|applicationId
parameter_list|,
name|String
name|queue
parameter_list|,
name|String
name|user
parameter_list|,
name|boolean
name|isAppRecovering
parameter_list|,
name|ReservationId
name|reservationID
parameter_list|)
block|{
name|super
argument_list|(
name|SchedulerEventType
operator|.
name|APP_ADDED
argument_list|)
expr_stmt|;
name|this
operator|.
name|applicationId
operator|=
name|applicationId
expr_stmt|;
name|this
operator|.
name|queue
operator|=
name|queue
expr_stmt|;
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
name|this
operator|.
name|reservationID
operator|=
name|reservationID
expr_stmt|;
name|this
operator|.
name|isAppRecovering
operator|=
name|isAppRecovering
expr_stmt|;
block|}
DECL|method|getApplicationId ()
specifier|public
name|ApplicationId
name|getApplicationId
parameter_list|()
block|{
return|return
name|applicationId
return|;
block|}
DECL|method|getQueue ()
specifier|public
name|String
name|getQueue
parameter_list|()
block|{
return|return
name|queue
return|;
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
DECL|method|getIsAppRecovering ()
specifier|public
name|boolean
name|getIsAppRecovering
parameter_list|()
block|{
return|return
name|isAppRecovering
return|;
block|}
DECL|method|getReservationID ()
specifier|public
name|ReservationId
name|getReservationID
parameter_list|()
block|{
return|return
name|reservationID
return|;
block|}
block|}
end_class

end_unit

