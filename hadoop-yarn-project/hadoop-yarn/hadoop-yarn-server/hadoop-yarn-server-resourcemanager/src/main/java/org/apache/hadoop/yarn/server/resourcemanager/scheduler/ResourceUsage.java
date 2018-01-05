begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler
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
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
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
name|nodelabels
operator|.
name|CommonNodeLabelsManager
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
name|util
operator|.
name|resource
operator|.
name|Resources
import|;
end_import

begin_comment
comment|/**  * Resource Usage by Labels for following fields by label - AM resource (to  * enforce max-am-resource-by-label after YARN-2637) - Used resource (includes  * AM resource usage) - Reserved resource - Pending resource - Headroom  *   * This class can be used to track resource usage in queue/user/app.  *   * And it is thread-safe  */
end_comment

begin_class
DECL|class|ResourceUsage
specifier|public
class|class
name|ResourceUsage
extends|extends
name|AbstractResourceUsage
block|{
comment|// short for no-label :)
DECL|field|NL
specifier|private
specifier|static
specifier|final
name|String
name|NL
init|=
name|CommonNodeLabelsManager
operator|.
name|NO_LABEL
decl_stmt|;
DECL|method|ResourceUsage ()
specifier|public
name|ResourceUsage
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
comment|/*    * Used    */
DECL|method|getUsed ()
specifier|public
name|Resource
name|getUsed
parameter_list|()
block|{
return|return
name|getUsed
argument_list|(
name|NL
argument_list|)
return|;
block|}
DECL|method|getUsed (String label)
specifier|public
name|Resource
name|getUsed
parameter_list|(
name|String
name|label
parameter_list|)
block|{
return|return
name|_get
argument_list|(
name|label
argument_list|,
name|ResourceType
operator|.
name|USED
argument_list|)
return|;
block|}
DECL|method|incUsed (String label, Resource res)
specifier|public
name|void
name|incUsed
parameter_list|(
name|String
name|label
parameter_list|,
name|Resource
name|res
parameter_list|)
block|{
name|_inc
argument_list|(
name|label
argument_list|,
name|ResourceType
operator|.
name|USED
argument_list|,
name|res
argument_list|)
expr_stmt|;
block|}
DECL|method|incUsed (Resource res)
specifier|public
name|void
name|incUsed
parameter_list|(
name|Resource
name|res
parameter_list|)
block|{
name|incUsed
argument_list|(
name|NL
argument_list|,
name|res
argument_list|)
expr_stmt|;
block|}
DECL|method|decUsed (Resource res)
specifier|public
name|void
name|decUsed
parameter_list|(
name|Resource
name|res
parameter_list|)
block|{
name|decUsed
argument_list|(
name|NL
argument_list|,
name|res
argument_list|)
expr_stmt|;
block|}
DECL|method|decUsed (String label, Resource res)
specifier|public
name|void
name|decUsed
parameter_list|(
name|String
name|label
parameter_list|,
name|Resource
name|res
parameter_list|)
block|{
name|_dec
argument_list|(
name|label
argument_list|,
name|ResourceType
operator|.
name|USED
argument_list|,
name|res
argument_list|)
expr_stmt|;
block|}
DECL|method|setUsed (Resource res)
specifier|public
name|void
name|setUsed
parameter_list|(
name|Resource
name|res
parameter_list|)
block|{
name|setUsed
argument_list|(
name|NL
argument_list|,
name|res
argument_list|)
expr_stmt|;
block|}
DECL|method|copyAllUsed (AbstractResourceUsage other)
specifier|public
name|void
name|copyAllUsed
parameter_list|(
name|AbstractResourceUsage
name|other
parameter_list|)
block|{
try|try
block|{
name|writeLock
operator|.
name|lock
argument_list|()
expr_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|UsageByLabel
argument_list|>
name|entry
range|:
name|other
operator|.
name|usages
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|setUsed
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|Resources
operator|.
name|clone
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|getUsed
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|writeLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|setUsed (String label, Resource res)
specifier|public
name|void
name|setUsed
parameter_list|(
name|String
name|label
parameter_list|,
name|Resource
name|res
parameter_list|)
block|{
name|_set
argument_list|(
name|label
argument_list|,
name|ResourceType
operator|.
name|USED
argument_list|,
name|res
argument_list|)
expr_stmt|;
block|}
comment|/*    * Pending    */
DECL|method|getPending ()
specifier|public
name|Resource
name|getPending
parameter_list|()
block|{
return|return
name|getPending
argument_list|(
name|NL
argument_list|)
return|;
block|}
DECL|method|getPending (String label)
specifier|public
name|Resource
name|getPending
parameter_list|(
name|String
name|label
parameter_list|)
block|{
return|return
name|_get
argument_list|(
name|label
argument_list|,
name|ResourceType
operator|.
name|PENDING
argument_list|)
return|;
block|}
DECL|method|incPending (String label, Resource res)
specifier|public
name|void
name|incPending
parameter_list|(
name|String
name|label
parameter_list|,
name|Resource
name|res
parameter_list|)
block|{
name|_inc
argument_list|(
name|label
argument_list|,
name|ResourceType
operator|.
name|PENDING
argument_list|,
name|res
argument_list|)
expr_stmt|;
block|}
DECL|method|incPending (Resource res)
specifier|public
name|void
name|incPending
parameter_list|(
name|Resource
name|res
parameter_list|)
block|{
name|incPending
argument_list|(
name|NL
argument_list|,
name|res
argument_list|)
expr_stmt|;
block|}
DECL|method|decPending (Resource res)
specifier|public
name|void
name|decPending
parameter_list|(
name|Resource
name|res
parameter_list|)
block|{
name|decPending
argument_list|(
name|NL
argument_list|,
name|res
argument_list|)
expr_stmt|;
block|}
DECL|method|decPending (String label, Resource res)
specifier|public
name|void
name|decPending
parameter_list|(
name|String
name|label
parameter_list|,
name|Resource
name|res
parameter_list|)
block|{
name|_dec
argument_list|(
name|label
argument_list|,
name|ResourceType
operator|.
name|PENDING
argument_list|,
name|res
argument_list|)
expr_stmt|;
block|}
DECL|method|setPending (Resource res)
specifier|public
name|void
name|setPending
parameter_list|(
name|Resource
name|res
parameter_list|)
block|{
name|setPending
argument_list|(
name|NL
argument_list|,
name|res
argument_list|)
expr_stmt|;
block|}
DECL|method|setPending (String label, Resource res)
specifier|public
name|void
name|setPending
parameter_list|(
name|String
name|label
parameter_list|,
name|Resource
name|res
parameter_list|)
block|{
name|_set
argument_list|(
name|label
argument_list|,
name|ResourceType
operator|.
name|PENDING
argument_list|,
name|res
argument_list|)
expr_stmt|;
block|}
comment|/*    * Reserved    */
DECL|method|getReserved ()
specifier|public
name|Resource
name|getReserved
parameter_list|()
block|{
return|return
name|getReserved
argument_list|(
name|NL
argument_list|)
return|;
block|}
DECL|method|getReserved (String label)
specifier|public
name|Resource
name|getReserved
parameter_list|(
name|String
name|label
parameter_list|)
block|{
return|return
name|_get
argument_list|(
name|label
argument_list|,
name|ResourceType
operator|.
name|RESERVED
argument_list|)
return|;
block|}
DECL|method|incReserved (String label, Resource res)
specifier|public
name|void
name|incReserved
parameter_list|(
name|String
name|label
parameter_list|,
name|Resource
name|res
parameter_list|)
block|{
name|_inc
argument_list|(
name|label
argument_list|,
name|ResourceType
operator|.
name|RESERVED
argument_list|,
name|res
argument_list|)
expr_stmt|;
block|}
DECL|method|incReserved (Resource res)
specifier|public
name|void
name|incReserved
parameter_list|(
name|Resource
name|res
parameter_list|)
block|{
name|incReserved
argument_list|(
name|NL
argument_list|,
name|res
argument_list|)
expr_stmt|;
block|}
DECL|method|decReserved (Resource res)
specifier|public
name|void
name|decReserved
parameter_list|(
name|Resource
name|res
parameter_list|)
block|{
name|decReserved
argument_list|(
name|NL
argument_list|,
name|res
argument_list|)
expr_stmt|;
block|}
DECL|method|decReserved (String label, Resource res)
specifier|public
name|void
name|decReserved
parameter_list|(
name|String
name|label
parameter_list|,
name|Resource
name|res
parameter_list|)
block|{
name|_dec
argument_list|(
name|label
argument_list|,
name|ResourceType
operator|.
name|RESERVED
argument_list|,
name|res
argument_list|)
expr_stmt|;
block|}
DECL|method|setReserved (Resource res)
specifier|public
name|void
name|setReserved
parameter_list|(
name|Resource
name|res
parameter_list|)
block|{
name|setReserved
argument_list|(
name|NL
argument_list|,
name|res
argument_list|)
expr_stmt|;
block|}
DECL|method|setReserved (String label, Resource res)
specifier|public
name|void
name|setReserved
parameter_list|(
name|String
name|label
parameter_list|,
name|Resource
name|res
parameter_list|)
block|{
name|_set
argument_list|(
name|label
argument_list|,
name|ResourceType
operator|.
name|RESERVED
argument_list|,
name|res
argument_list|)
expr_stmt|;
block|}
comment|/*    * AM-Used    */
DECL|method|getAMUsed ()
specifier|public
name|Resource
name|getAMUsed
parameter_list|()
block|{
return|return
name|getAMUsed
argument_list|(
name|NL
argument_list|)
return|;
block|}
DECL|method|getAMUsed (String label)
specifier|public
name|Resource
name|getAMUsed
parameter_list|(
name|String
name|label
parameter_list|)
block|{
return|return
name|_get
argument_list|(
name|label
argument_list|,
name|ResourceType
operator|.
name|AMUSED
argument_list|)
return|;
block|}
DECL|method|incAMUsed (String label, Resource res)
specifier|public
name|void
name|incAMUsed
parameter_list|(
name|String
name|label
parameter_list|,
name|Resource
name|res
parameter_list|)
block|{
name|_inc
argument_list|(
name|label
argument_list|,
name|ResourceType
operator|.
name|AMUSED
argument_list|,
name|res
argument_list|)
expr_stmt|;
block|}
DECL|method|incAMUsed (Resource res)
specifier|public
name|void
name|incAMUsed
parameter_list|(
name|Resource
name|res
parameter_list|)
block|{
name|incAMUsed
argument_list|(
name|NL
argument_list|,
name|res
argument_list|)
expr_stmt|;
block|}
DECL|method|decAMUsed (Resource res)
specifier|public
name|void
name|decAMUsed
parameter_list|(
name|Resource
name|res
parameter_list|)
block|{
name|decAMUsed
argument_list|(
name|NL
argument_list|,
name|res
argument_list|)
expr_stmt|;
block|}
DECL|method|decAMUsed (String label, Resource res)
specifier|public
name|void
name|decAMUsed
parameter_list|(
name|String
name|label
parameter_list|,
name|Resource
name|res
parameter_list|)
block|{
name|_dec
argument_list|(
name|label
argument_list|,
name|ResourceType
operator|.
name|AMUSED
argument_list|,
name|res
argument_list|)
expr_stmt|;
block|}
DECL|method|setAMUsed (Resource res)
specifier|public
name|void
name|setAMUsed
parameter_list|(
name|Resource
name|res
parameter_list|)
block|{
name|setAMUsed
argument_list|(
name|NL
argument_list|,
name|res
argument_list|)
expr_stmt|;
block|}
DECL|method|setAMUsed (String label, Resource res)
specifier|public
name|void
name|setAMUsed
parameter_list|(
name|String
name|label
parameter_list|,
name|Resource
name|res
parameter_list|)
block|{
name|_set
argument_list|(
name|label
argument_list|,
name|ResourceType
operator|.
name|AMUSED
argument_list|,
name|res
argument_list|)
expr_stmt|;
block|}
DECL|method|getAllPending ()
specifier|public
name|Resource
name|getAllPending
parameter_list|()
block|{
return|return
name|_getAll
argument_list|(
name|ResourceType
operator|.
name|PENDING
argument_list|)
return|;
block|}
DECL|method|getAllUsed ()
specifier|public
name|Resource
name|getAllUsed
parameter_list|()
block|{
return|return
name|_getAll
argument_list|(
name|ResourceType
operator|.
name|USED
argument_list|)
return|;
block|}
comment|// Cache Used
DECL|method|getCachedUsed ()
specifier|public
name|Resource
name|getCachedUsed
parameter_list|()
block|{
return|return
name|_get
argument_list|(
name|NL
argument_list|,
name|ResourceType
operator|.
name|CACHED_USED
argument_list|)
return|;
block|}
DECL|method|getCachedUsed (String label)
specifier|public
name|Resource
name|getCachedUsed
parameter_list|(
name|String
name|label
parameter_list|)
block|{
return|return
name|_get
argument_list|(
name|label
argument_list|,
name|ResourceType
operator|.
name|CACHED_USED
argument_list|)
return|;
block|}
DECL|method|getCachedPending ()
specifier|public
name|Resource
name|getCachedPending
parameter_list|()
block|{
return|return
name|_get
argument_list|(
name|NL
argument_list|,
name|ResourceType
operator|.
name|CACHED_PENDING
argument_list|)
return|;
block|}
DECL|method|getCachedPending (String label)
specifier|public
name|Resource
name|getCachedPending
parameter_list|(
name|String
name|label
parameter_list|)
block|{
return|return
name|_get
argument_list|(
name|label
argument_list|,
name|ResourceType
operator|.
name|CACHED_PENDING
argument_list|)
return|;
block|}
DECL|method|setCachedUsed (String label, Resource res)
specifier|public
name|void
name|setCachedUsed
parameter_list|(
name|String
name|label
parameter_list|,
name|Resource
name|res
parameter_list|)
block|{
name|_set
argument_list|(
name|label
argument_list|,
name|ResourceType
operator|.
name|CACHED_USED
argument_list|,
name|res
argument_list|)
expr_stmt|;
block|}
DECL|method|setCachedUsed (Resource res)
specifier|public
name|void
name|setCachedUsed
parameter_list|(
name|Resource
name|res
parameter_list|)
block|{
name|_set
argument_list|(
name|NL
argument_list|,
name|ResourceType
operator|.
name|CACHED_USED
argument_list|,
name|res
argument_list|)
expr_stmt|;
block|}
DECL|method|setCachedPending (String label, Resource res)
specifier|public
name|void
name|setCachedPending
parameter_list|(
name|String
name|label
parameter_list|,
name|Resource
name|res
parameter_list|)
block|{
name|_set
argument_list|(
name|label
argument_list|,
name|ResourceType
operator|.
name|CACHED_PENDING
argument_list|,
name|res
argument_list|)
expr_stmt|;
block|}
DECL|method|setCachedPending (Resource res)
specifier|public
name|void
name|setCachedPending
parameter_list|(
name|Resource
name|res
parameter_list|)
block|{
name|_set
argument_list|(
name|NL
argument_list|,
name|ResourceType
operator|.
name|CACHED_PENDING
argument_list|,
name|res
argument_list|)
expr_stmt|;
block|}
comment|/*    * AM-Resource Limit    */
DECL|method|getAMLimit ()
specifier|public
name|Resource
name|getAMLimit
parameter_list|()
block|{
return|return
name|getAMLimit
argument_list|(
name|NL
argument_list|)
return|;
block|}
DECL|method|getAMLimit (String label)
specifier|public
name|Resource
name|getAMLimit
parameter_list|(
name|String
name|label
parameter_list|)
block|{
return|return
name|_get
argument_list|(
name|label
argument_list|,
name|ResourceType
operator|.
name|AMLIMIT
argument_list|)
return|;
block|}
DECL|method|incAMLimit (String label, Resource res)
specifier|public
name|void
name|incAMLimit
parameter_list|(
name|String
name|label
parameter_list|,
name|Resource
name|res
parameter_list|)
block|{
name|_inc
argument_list|(
name|label
argument_list|,
name|ResourceType
operator|.
name|AMLIMIT
argument_list|,
name|res
argument_list|)
expr_stmt|;
block|}
DECL|method|incAMLimit (Resource res)
specifier|public
name|void
name|incAMLimit
parameter_list|(
name|Resource
name|res
parameter_list|)
block|{
name|incAMLimit
argument_list|(
name|NL
argument_list|,
name|res
argument_list|)
expr_stmt|;
block|}
DECL|method|decAMLimit (Resource res)
specifier|public
name|void
name|decAMLimit
parameter_list|(
name|Resource
name|res
parameter_list|)
block|{
name|decAMLimit
argument_list|(
name|NL
argument_list|,
name|res
argument_list|)
expr_stmt|;
block|}
DECL|method|decAMLimit (String label, Resource res)
specifier|public
name|void
name|decAMLimit
parameter_list|(
name|String
name|label
parameter_list|,
name|Resource
name|res
parameter_list|)
block|{
name|_dec
argument_list|(
name|label
argument_list|,
name|ResourceType
operator|.
name|AMLIMIT
argument_list|,
name|res
argument_list|)
expr_stmt|;
block|}
DECL|method|setAMLimit (Resource res)
specifier|public
name|void
name|setAMLimit
parameter_list|(
name|Resource
name|res
parameter_list|)
block|{
name|setAMLimit
argument_list|(
name|NL
argument_list|,
name|res
argument_list|)
expr_stmt|;
block|}
DECL|method|setAMLimit (String label, Resource res)
specifier|public
name|void
name|setAMLimit
parameter_list|(
name|String
name|label
parameter_list|,
name|Resource
name|res
parameter_list|)
block|{
name|_set
argument_list|(
name|label
argument_list|,
name|ResourceType
operator|.
name|AMLIMIT
argument_list|,
name|res
argument_list|)
expr_stmt|;
block|}
DECL|method|getUserAMLimit ()
specifier|public
name|Resource
name|getUserAMLimit
parameter_list|()
block|{
return|return
name|getAMLimit
argument_list|(
name|NL
argument_list|)
return|;
block|}
DECL|method|getUserAMLimit (String label)
specifier|public
name|Resource
name|getUserAMLimit
parameter_list|(
name|String
name|label
parameter_list|)
block|{
return|return
name|_get
argument_list|(
name|label
argument_list|,
name|ResourceType
operator|.
name|USERAMLIMIT
argument_list|)
return|;
block|}
DECL|method|setUserAMLimit (Resource res)
specifier|public
name|void
name|setUserAMLimit
parameter_list|(
name|Resource
name|res
parameter_list|)
block|{
name|setAMLimit
argument_list|(
name|NL
argument_list|,
name|res
argument_list|)
expr_stmt|;
block|}
DECL|method|setUserAMLimit (String label, Resource res)
specifier|public
name|void
name|setUserAMLimit
parameter_list|(
name|String
name|label
parameter_list|,
name|Resource
name|res
parameter_list|)
block|{
name|_set
argument_list|(
name|label
argument_list|,
name|ResourceType
operator|.
name|USERAMLIMIT
argument_list|,
name|res
argument_list|)
expr_stmt|;
block|}
DECL|method|getCachedDemand (String label)
specifier|public
name|Resource
name|getCachedDemand
parameter_list|(
name|String
name|label
parameter_list|)
block|{
try|try
block|{
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
name|Resource
name|demand
init|=
name|Resources
operator|.
name|createResource
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Resources
operator|.
name|addTo
argument_list|(
name|demand
argument_list|,
name|getCachedUsed
argument_list|(
name|label
argument_list|)
argument_list|)
expr_stmt|;
name|Resources
operator|.
name|addTo
argument_list|(
name|demand
argument_list|,
name|getCachedPending
argument_list|(
name|label
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|demand
return|;
block|}
finally|finally
block|{
name|readLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

