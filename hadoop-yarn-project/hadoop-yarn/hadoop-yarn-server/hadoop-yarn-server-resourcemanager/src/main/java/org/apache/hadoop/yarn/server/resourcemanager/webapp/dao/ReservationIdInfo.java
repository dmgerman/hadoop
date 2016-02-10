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
name|ReservationId
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

begin_comment
comment|/**  * Simple class that represent a reservation ID.  */
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
DECL|class|ReservationIdInfo
specifier|public
class|class
name|ReservationIdInfo
block|{
annotation|@
name|XmlElement
argument_list|(
name|name
operator|=
literal|"cluster-timestamp"
argument_list|)
DECL|field|clusterTimestamp
specifier|private
name|long
name|clusterTimestamp
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
name|long
name|reservationId
decl_stmt|;
DECL|method|ReservationIdInfo ()
specifier|public
name|ReservationIdInfo
parameter_list|()
block|{
name|this
operator|.
name|clusterTimestamp
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|reservationId
operator|=
literal|0
expr_stmt|;
block|}
DECL|method|ReservationIdInfo (ReservationId reservationId)
specifier|public
name|ReservationIdInfo
parameter_list|(
name|ReservationId
name|reservationId
parameter_list|)
block|{
name|this
operator|.
name|clusterTimestamp
operator|=
name|reservationId
operator|.
name|getClusterTimestamp
argument_list|()
expr_stmt|;
name|this
operator|.
name|reservationId
operator|=
name|reservationId
operator|.
name|getId
argument_list|()
expr_stmt|;
block|}
DECL|method|getClusterTimestamp ()
specifier|public
name|long
name|getClusterTimestamp
parameter_list|()
block|{
return|return
name|this
operator|.
name|clusterTimestamp
return|;
block|}
DECL|method|setClusterTimestamp (long newClusterTimestamp)
specifier|public
name|void
name|setClusterTimestamp
parameter_list|(
name|long
name|newClusterTimestamp
parameter_list|)
block|{
name|this
operator|.
name|clusterTimestamp
operator|=
name|newClusterTimestamp
expr_stmt|;
block|}
DECL|method|getReservationId ()
specifier|public
name|long
name|getReservationId
parameter_list|()
block|{
return|return
name|this
operator|.
name|reservationId
return|;
block|}
DECL|method|setReservationId (long newReservationId)
specifier|public
name|void
name|setReservationId
parameter_list|(
name|long
name|newReservationId
parameter_list|)
block|{
name|this
operator|.
name|reservationId
operator|=
name|newReservationId
expr_stmt|;
block|}
block|}
end_class

end_unit

