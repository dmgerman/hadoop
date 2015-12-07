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

begin_comment
comment|/**  * Simple class to allow users to send information required to create an  * ReservationSubmissionContext which can then be used to submit a reservation.  */
end_comment

begin_class
annotation|@
name|XmlRootElement
argument_list|(
name|name
operator|=
literal|"reservation-submission-context"
argument_list|)
annotation|@
name|XmlAccessorType
argument_list|(
name|XmlAccessType
operator|.
name|FIELD
argument_list|)
DECL|class|ReservationSubmissionRequestInfo
specifier|public
class|class
name|ReservationSubmissionRequestInfo
block|{
annotation|@
name|XmlElement
argument_list|(
name|name
operator|=
literal|"queue"
argument_list|)
DECL|field|queue
specifier|private
name|String
name|queue
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
DECL|method|ReservationSubmissionRequestInfo ()
specifier|public
name|ReservationSubmissionRequestInfo
parameter_list|()
block|{   }
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
DECL|method|setQueue (String queue)
specifier|public
name|void
name|setQueue
parameter_list|(
name|String
name|queue
parameter_list|)
block|{
name|this
operator|.
name|queue
operator|=
name|queue
expr_stmt|;
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
DECL|method|setReservationDefinition ( ReservationDefinitionInfo reservationDefinition)
specifier|public
name|void
name|setReservationDefinition
parameter_list|(
name|ReservationDefinitionInfo
name|reservationDefinition
parameter_list|)
block|{
name|this
operator|.
name|reservationDefinition
operator|=
name|reservationDefinition
expr_stmt|;
block|}
block|}
end_class

end_unit

