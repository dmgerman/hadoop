begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity
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
name|capacity
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
name|XmlRootElement
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
name|scheduler
operator|.
name|ResourceUsage
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
name|webapp
operator|.
name|dao
operator|.
name|ResourceInfo
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
name|webapp
operator|.
name|dao
operator|.
name|ResourcesInfo
import|;
end_import

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
DECL|class|UserInfo
specifier|public
class|class
name|UserInfo
block|{
DECL|field|username
specifier|protected
name|String
name|username
decl_stmt|;
DECL|field|resourcesUsed
specifier|protected
name|ResourceInfo
name|resourcesUsed
decl_stmt|;
DECL|field|numPendingApplications
specifier|protected
name|int
name|numPendingApplications
decl_stmt|;
DECL|field|numActiveApplications
specifier|protected
name|int
name|numActiveApplications
decl_stmt|;
DECL|field|AMResourceUsed
specifier|protected
name|ResourceInfo
name|AMResourceUsed
decl_stmt|;
DECL|field|userResourceLimit
specifier|protected
name|ResourceInfo
name|userResourceLimit
decl_stmt|;
DECL|field|resources
specifier|protected
name|ResourcesInfo
name|resources
decl_stmt|;
DECL|field|userWeight
specifier|private
name|float
name|userWeight
decl_stmt|;
DECL|field|isActive
specifier|private
name|boolean
name|isActive
decl_stmt|;
DECL|method|UserInfo ()
name|UserInfo
parameter_list|()
block|{}
DECL|method|UserInfo (String username, Resource resUsed, int activeApps, int pendingApps, Resource amResUsed, Resource resourceLimit, ResourceUsage resourceUsage, float weight, boolean isActive)
name|UserInfo
parameter_list|(
name|String
name|username
parameter_list|,
name|Resource
name|resUsed
parameter_list|,
name|int
name|activeApps
parameter_list|,
name|int
name|pendingApps
parameter_list|,
name|Resource
name|amResUsed
parameter_list|,
name|Resource
name|resourceLimit
parameter_list|,
name|ResourceUsage
name|resourceUsage
parameter_list|,
name|float
name|weight
parameter_list|,
name|boolean
name|isActive
parameter_list|)
block|{
name|this
operator|.
name|username
operator|=
name|username
expr_stmt|;
name|this
operator|.
name|resourcesUsed
operator|=
operator|new
name|ResourceInfo
argument_list|(
name|resUsed
argument_list|)
expr_stmt|;
name|this
operator|.
name|numActiveApplications
operator|=
name|activeApps
expr_stmt|;
name|this
operator|.
name|numPendingApplications
operator|=
name|pendingApps
expr_stmt|;
name|this
operator|.
name|AMResourceUsed
operator|=
operator|new
name|ResourceInfo
argument_list|(
name|amResUsed
argument_list|)
expr_stmt|;
name|this
operator|.
name|userResourceLimit
operator|=
operator|new
name|ResourceInfo
argument_list|(
name|resourceLimit
argument_list|)
expr_stmt|;
name|this
operator|.
name|resources
operator|=
operator|new
name|ResourcesInfo
argument_list|(
name|resourceUsage
argument_list|)
expr_stmt|;
name|this
operator|.
name|userWeight
operator|=
name|weight
expr_stmt|;
name|this
operator|.
name|isActive
operator|=
name|isActive
expr_stmt|;
block|}
DECL|method|getUsername ()
specifier|public
name|String
name|getUsername
parameter_list|()
block|{
return|return
name|username
return|;
block|}
DECL|method|getResourcesUsed ()
specifier|public
name|ResourceInfo
name|getResourcesUsed
parameter_list|()
block|{
return|return
name|resourcesUsed
return|;
block|}
DECL|method|getNumPendingApplications ()
specifier|public
name|int
name|getNumPendingApplications
parameter_list|()
block|{
return|return
name|numPendingApplications
return|;
block|}
DECL|method|getNumActiveApplications ()
specifier|public
name|int
name|getNumActiveApplications
parameter_list|()
block|{
return|return
name|numActiveApplications
return|;
block|}
DECL|method|getAMResourcesUsed ()
specifier|public
name|ResourceInfo
name|getAMResourcesUsed
parameter_list|()
block|{
return|return
name|AMResourceUsed
return|;
block|}
DECL|method|getUserResourceLimit ()
specifier|public
name|ResourceInfo
name|getUserResourceLimit
parameter_list|()
block|{
return|return
name|userResourceLimit
return|;
block|}
DECL|method|getResourceUsageInfo ()
specifier|public
name|ResourcesInfo
name|getResourceUsageInfo
parameter_list|()
block|{
return|return
name|resources
return|;
block|}
DECL|method|getUserWeight ()
specifier|public
name|float
name|getUserWeight
parameter_list|()
block|{
return|return
name|userWeight
return|;
block|}
DECL|method|getIsActive ()
specifier|public
name|boolean
name|getIsActive
parameter_list|()
block|{
return|return
name|isActive
return|;
block|}
block|}
end_class

end_unit

