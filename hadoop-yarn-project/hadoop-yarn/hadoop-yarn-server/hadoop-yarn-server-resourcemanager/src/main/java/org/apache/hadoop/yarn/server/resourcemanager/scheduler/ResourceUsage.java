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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|ReentrantReadWriteLock
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|ReentrantReadWriteLock
operator|.
name|ReadLock
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|ReentrantReadWriteLock
operator|.
name|WriteLock
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
name|server
operator|.
name|resourcemanager
operator|.
name|nodelabels
operator|.
name|RMNodeLabelsManager
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
block|{
DECL|field|readLock
specifier|private
name|ReadLock
name|readLock
decl_stmt|;
DECL|field|writeLock
specifier|private
name|WriteLock
name|writeLock
decl_stmt|;
DECL|field|usages
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|UsageByLabel
argument_list|>
name|usages
decl_stmt|;
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
name|ReentrantReadWriteLock
name|lock
init|=
operator|new
name|ReentrantReadWriteLock
argument_list|()
decl_stmt|;
name|readLock
operator|=
name|lock
operator|.
name|readLock
argument_list|()
expr_stmt|;
name|writeLock
operator|=
name|lock
operator|.
name|writeLock
argument_list|()
expr_stmt|;
name|usages
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|UsageByLabel
argument_list|>
argument_list|()
expr_stmt|;
name|usages
operator|.
name|put
argument_list|(
name|NL
argument_list|,
operator|new
name|UsageByLabel
argument_list|(
name|NL
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Usage enum here to make implement cleaner
DECL|enum|ResourceType
specifier|private
enum|enum
name|ResourceType
block|{
comment|//CACHED_USED and CACHED_PENDING may be read by anyone, but must only
comment|//be written by ordering policies
DECL|enumConstant|USED
DECL|enumConstant|PENDING
DECL|enumConstant|AMUSED
DECL|enumConstant|RESERVED
DECL|enumConstant|CACHED_USED
name|USED
argument_list|(
literal|0
argument_list|)
block|,
name|PENDING
argument_list|(
literal|1
argument_list|)
block|,
name|AMUSED
argument_list|(
literal|2
argument_list|)
block|,
name|RESERVED
argument_list|(
literal|3
argument_list|)
block|,
name|CACHED_USED
argument_list|(
literal|4
argument_list|)
block|,
DECL|enumConstant|CACHED_PENDING
name|CACHED_PENDING
argument_list|(
literal|5
argument_list|)
block|;
DECL|field|idx
specifier|private
name|int
name|idx
decl_stmt|;
DECL|method|ResourceType (int value)
specifier|private
name|ResourceType
parameter_list|(
name|int
name|value
parameter_list|)
block|{
name|this
operator|.
name|idx
operator|=
name|value
expr_stmt|;
block|}
block|}
DECL|class|UsageByLabel
specifier|private
specifier|static
class|class
name|UsageByLabel
block|{
comment|// usage by label, contains all UsageType
DECL|field|resArr
specifier|private
name|Resource
index|[]
name|resArr
decl_stmt|;
DECL|method|UsageByLabel (String label)
specifier|public
name|UsageByLabel
parameter_list|(
name|String
name|label
parameter_list|)
block|{
name|resArr
operator|=
operator|new
name|Resource
index|[
name|ResourceType
operator|.
name|values
argument_list|()
operator|.
name|length
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
name|resArr
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|resArr
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
block|}
empty_stmt|;
block|}
DECL|method|getUsed ()
specifier|public
name|Resource
name|getUsed
parameter_list|()
block|{
return|return
name|resArr
index|[
name|ResourceType
operator|.
name|USED
operator|.
name|idx
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"{used="
operator|+
name|resArr
index|[
literal|0
index|]
operator|+
literal|"%, "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"pending="
operator|+
name|resArr
index|[
literal|1
index|]
operator|+
literal|"%, "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"am_used="
operator|+
name|resArr
index|[
literal|2
index|]
operator|+
literal|"%, "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"reserved="
operator|+
name|resArr
index|[
literal|3
index|]
operator|+
literal|"%}"
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
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
DECL|method|copyAllUsed (ResourceUsage other)
specifier|public
name|void
name|copyAllUsed
parameter_list|(
name|ResourceUsage
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
DECL|method|normalize (Resource res)
specifier|private
specifier|static
name|Resource
name|normalize
parameter_list|(
name|Resource
name|res
parameter_list|)
block|{
if|if
condition|(
name|res
operator|==
literal|null
condition|)
block|{
return|return
name|Resources
operator|.
name|none
argument_list|()
return|;
block|}
return|return
name|res
return|;
block|}
DECL|method|_get (String label, ResourceType type)
specifier|private
name|Resource
name|_get
parameter_list|(
name|String
name|label
parameter_list|,
name|ResourceType
name|type
parameter_list|)
block|{
if|if
condition|(
name|label
operator|==
literal|null
condition|)
block|{
name|label
operator|=
name|RMNodeLabelsManager
operator|.
name|NO_LABEL
expr_stmt|;
block|}
try|try
block|{
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
name|UsageByLabel
name|usage
init|=
name|usages
operator|.
name|get
argument_list|(
name|label
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|usage
condition|)
block|{
return|return
name|Resources
operator|.
name|none
argument_list|()
return|;
block|}
return|return
name|normalize
argument_list|(
name|usage
operator|.
name|resArr
index|[
name|type
operator|.
name|idx
index|]
argument_list|)
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
DECL|method|_getAll (ResourceType type)
specifier|private
name|Resource
name|_getAll
parameter_list|(
name|ResourceType
name|type
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
name|allOfType
init|=
name|Resources
operator|.
name|createResource
argument_list|(
literal|0
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|UsageByLabel
argument_list|>
name|usageEntry
range|:
name|usages
operator|.
name|entrySet
argument_list|()
control|)
block|{
comment|//all usages types are initialized
name|Resources
operator|.
name|addTo
argument_list|(
name|allOfType
argument_list|,
name|usageEntry
operator|.
name|getValue
argument_list|()
operator|.
name|resArr
index|[
name|type
operator|.
name|idx
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|allOfType
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
DECL|method|getAndAddIfMissing (String label)
specifier|private
name|UsageByLabel
name|getAndAddIfMissing
parameter_list|(
name|String
name|label
parameter_list|)
block|{
if|if
condition|(
name|label
operator|==
literal|null
condition|)
block|{
name|label
operator|=
name|RMNodeLabelsManager
operator|.
name|NO_LABEL
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|usages
operator|.
name|containsKey
argument_list|(
name|label
argument_list|)
condition|)
block|{
name|UsageByLabel
name|u
init|=
operator|new
name|UsageByLabel
argument_list|(
name|label
argument_list|)
decl_stmt|;
name|usages
operator|.
name|put
argument_list|(
name|label
argument_list|,
name|u
argument_list|)
expr_stmt|;
return|return
name|u
return|;
block|}
return|return
name|usages
operator|.
name|get
argument_list|(
name|label
argument_list|)
return|;
block|}
DECL|method|_set (String label, ResourceType type, Resource res)
specifier|private
name|void
name|_set
parameter_list|(
name|String
name|label
parameter_list|,
name|ResourceType
name|type
parameter_list|,
name|Resource
name|res
parameter_list|)
block|{
try|try
block|{
name|writeLock
operator|.
name|lock
argument_list|()
expr_stmt|;
name|UsageByLabel
name|usage
init|=
name|getAndAddIfMissing
argument_list|(
name|label
argument_list|)
decl_stmt|;
name|usage
operator|.
name|resArr
index|[
name|type
operator|.
name|idx
index|]
operator|=
name|res
expr_stmt|;
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
DECL|method|_inc (String label, ResourceType type, Resource res)
specifier|private
name|void
name|_inc
parameter_list|(
name|String
name|label
parameter_list|,
name|ResourceType
name|type
parameter_list|,
name|Resource
name|res
parameter_list|)
block|{
try|try
block|{
name|writeLock
operator|.
name|lock
argument_list|()
expr_stmt|;
name|UsageByLabel
name|usage
init|=
name|getAndAddIfMissing
argument_list|(
name|label
argument_list|)
decl_stmt|;
name|Resources
operator|.
name|addTo
argument_list|(
name|usage
operator|.
name|resArr
index|[
name|type
operator|.
name|idx
index|]
argument_list|,
name|res
argument_list|)
expr_stmt|;
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
DECL|method|_dec (String label, ResourceType type, Resource res)
specifier|private
name|void
name|_dec
parameter_list|(
name|String
name|label
parameter_list|,
name|ResourceType
name|type
parameter_list|,
name|Resource
name|res
parameter_list|)
block|{
try|try
block|{
name|writeLock
operator|.
name|lock
argument_list|()
expr_stmt|;
name|UsageByLabel
name|usage
init|=
name|getAndAddIfMissing
argument_list|(
name|label
argument_list|)
decl_stmt|;
name|Resources
operator|.
name|subtractFrom
argument_list|(
name|usage
operator|.
name|resArr
index|[
name|type
operator|.
name|idx
index|]
argument_list|,
name|res
argument_list|)
expr_stmt|;
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
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
try|try
block|{
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
return|return
name|usages
operator|.
name|toString
argument_list|()
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

