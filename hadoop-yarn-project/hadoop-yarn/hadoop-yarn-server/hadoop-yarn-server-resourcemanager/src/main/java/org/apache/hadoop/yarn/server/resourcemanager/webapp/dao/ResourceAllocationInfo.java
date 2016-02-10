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
name|XmlRootElement
import|;
end_import

begin_comment
comment|/**  * Simple class that represent a resource allocation.  */
end_comment

begin_class
annotation|@
name|XmlRootElement
argument_list|(
name|name
operator|=
literal|"resource-allocation"
argument_list|)
annotation|@
name|XmlAccessorType
argument_list|(
name|XmlAccessType
operator|.
name|FIELD
argument_list|)
DECL|class|ResourceAllocationInfo
specifier|public
class|class
name|ResourceAllocationInfo
block|{
DECL|field|resource
specifier|private
name|ResourceInfo
name|resource
decl_stmt|;
DECL|field|startTime
specifier|private
name|long
name|startTime
decl_stmt|;
DECL|field|endTime
specifier|private
name|long
name|endTime
decl_stmt|;
DECL|method|ResourceAllocationInfo ()
specifier|public
name|ResourceAllocationInfo
parameter_list|()
block|{
name|resource
operator|=
operator|new
name|ResourceInfo
argument_list|()
expr_stmt|;
name|startTime
operator|=
operator|-
literal|1
expr_stmt|;
name|endTime
operator|=
operator|-
literal|1
expr_stmt|;
block|}
DECL|method|ResourceAllocationInfo (ReservationInterval interval, Resource res)
specifier|public
name|ResourceAllocationInfo
parameter_list|(
name|ReservationInterval
name|interval
parameter_list|,
name|Resource
name|res
parameter_list|)
block|{
name|this
operator|.
name|resource
operator|=
operator|new
name|ResourceInfo
argument_list|(
name|res
argument_list|)
expr_stmt|;
name|this
operator|.
name|startTime
operator|=
name|interval
operator|.
name|getStartTime
argument_list|()
expr_stmt|;
name|this
operator|.
name|endTime
operator|=
name|interval
operator|.
name|getEndTime
argument_list|()
expr_stmt|;
block|}
DECL|method|getStartTime ()
specifier|public
name|long
name|getStartTime
parameter_list|()
block|{
return|return
name|startTime
return|;
block|}
DECL|method|setStartTime (long newStartTime)
specifier|public
name|void
name|setStartTime
parameter_list|(
name|long
name|newStartTime
parameter_list|)
block|{
name|this
operator|.
name|startTime
operator|=
name|newStartTime
expr_stmt|;
block|}
DECL|method|getEndTime ()
specifier|public
name|long
name|getEndTime
parameter_list|()
block|{
return|return
name|endTime
return|;
block|}
DECL|method|setEndTime (long newEndTime)
specifier|public
name|void
name|setEndTime
parameter_list|(
name|long
name|newEndTime
parameter_list|)
block|{
name|this
operator|.
name|endTime
operator|=
name|newEndTime
expr_stmt|;
block|}
DECL|method|getResource ()
specifier|public
name|ResourceInfo
name|getResource
parameter_list|()
block|{
return|return
name|resource
return|;
block|}
DECL|method|setResource (ResourceInfo newResource)
specifier|public
name|void
name|setResource
parameter_list|(
name|ResourceInfo
name|newResource
parameter_list|)
block|{
name|this
operator|.
name|resource
operator|=
name|newResource
expr_stmt|;
block|}
block|}
end_class

end_unit

