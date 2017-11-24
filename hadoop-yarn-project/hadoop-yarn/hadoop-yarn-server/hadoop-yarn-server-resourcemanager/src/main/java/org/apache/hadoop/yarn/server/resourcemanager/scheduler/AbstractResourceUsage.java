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
name|Set
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
name|atomic
operator|.
name|AtomicReferenceArray
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
comment|/**  * This class can be used to track resource usage in queue/user/app.  *  * And it is thread-safe  */
end_comment

begin_class
DECL|class|AbstractResourceUsage
specifier|public
class|class
name|AbstractResourceUsage
block|{
DECL|field|readLock
specifier|protected
name|ReadLock
name|readLock
decl_stmt|;
DECL|field|writeLock
specifier|protected
name|WriteLock
name|writeLock
decl_stmt|;
DECL|field|usages
specifier|protected
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|UsageByLabel
argument_list|>
name|usages
decl_stmt|;
DECL|field|noLabelUsages
specifier|private
specifier|final
name|UsageByLabel
name|noLabelUsages
decl_stmt|;
comment|// short for no-label :)
DECL|method|AbstractResourceUsage ()
specifier|public
name|AbstractResourceUsage
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
argument_list|<>
argument_list|()
expr_stmt|;
comment|// For default label, avoid map for faster access.
name|noLabelUsages
operator|=
operator|new
name|UsageByLabel
argument_list|()
expr_stmt|;
name|usages
operator|.
name|put
argument_list|(
name|CommonNodeLabelsManager
operator|.
name|NO_LABEL
argument_list|,
name|noLabelUsages
argument_list|)
expr_stmt|;
block|}
comment|// Usage enum here to make implement cleaner
DECL|enum|ResourceType
specifier|public
enum|enum
name|ResourceType
block|{
comment|// CACHED_USED and CACHED_PENDING may be read by anyone, but must only
comment|// be written by ordering policies
DECL|enumConstant|USED
DECL|enumConstant|PENDING
DECL|enumConstant|AMUSED
DECL|enumConstant|RESERVED
DECL|enumConstant|CACHED_USED
DECL|enumConstant|CACHED_PENDING
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
name|CACHED_PENDING
argument_list|(
DECL|enumConstant|AMLIMIT
DECL|enumConstant|MIN_RESOURCE
DECL|enumConstant|MAX_RESOURCE
DECL|enumConstant|EFF_MIN_RESOURCE
literal|5
argument_list|)
block|,
name|AMLIMIT
argument_list|(
literal|6
argument_list|)
block|,
name|MIN_RESOURCE
argument_list|(
literal|7
argument_list|)
block|,
name|MAX_RESOURCE
argument_list|(
literal|8
argument_list|)
block|,
name|EFF_MIN_RESOURCE
argument_list|(
DECL|enumConstant|EFF_MAX_RESOURCE
literal|9
argument_list|)
block|,
name|EFF_MAX_RESOURCE
argument_list|(
literal|10
argument_list|)
block|;
DECL|field|idx
specifier|private
name|int
name|idx
decl_stmt|;
DECL|method|ResourceType (int value)
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
specifier|public
specifier|static
class|class
name|UsageByLabel
block|{
comment|// usage by label, contains all UsageType
DECL|field|resArr
specifier|private
specifier|final
name|AtomicReferenceArray
argument_list|<
name|Resource
argument_list|>
name|resArr
decl_stmt|;
DECL|method|UsageByLabel ()
specifier|public
name|UsageByLabel
parameter_list|()
block|{
name|resArr
operator|=
operator|new
name|AtomicReferenceArray
argument_list|<>
argument_list|(
name|ResourceType
operator|.
name|values
argument_list|()
operator|.
name|length
argument_list|)
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
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|resArr
operator|.
name|set
argument_list|(
name|i
argument_list|,
name|Resource
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getUsed ()
specifier|public
name|Resource
name|getUsed
parameter_list|()
block|{
return|return
name|resArr
operator|.
name|get
argument_list|(
name|ResourceType
operator|.
name|USED
operator|.
name|idx
argument_list|)
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
operator|.
name|get
argument_list|(
name|ResourceType
operator|.
name|USED
operator|.
name|idx
argument_list|)
operator|+
literal|", "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"pending="
operator|+
name|resArr
operator|.
name|get
argument_list|(
name|ResourceType
operator|.
name|PENDING
operator|.
name|idx
argument_list|)
operator|+
literal|", "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"am_used="
operator|+
name|resArr
operator|.
name|get
argument_list|(
name|ResourceType
operator|.
name|AMUSED
operator|.
name|idx
argument_list|)
operator|+
literal|", "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"reserved="
operator|+
name|resArr
operator|.
name|get
argument_list|(
name|ResourceType
operator|.
name|RESERVED
operator|.
name|idx
argument_list|)
operator|+
literal|", "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"min_eff="
operator|+
name|resArr
operator|.
name|get
argument_list|(
name|ResourceType
operator|.
name|EFF_MIN_RESOURCE
operator|.
name|idx
argument_list|)
operator|+
literal|", "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"max_eff="
operator|+
name|resArr
operator|.
name|get
argument_list|(
name|ResourceType
operator|.
name|EFF_MAX_RESOURCE
operator|.
name|idx
argument_list|)
operator|+
literal|"}"
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
specifier|protected
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
operator|||
name|label
operator|.
name|equals
argument_list|(
name|RMNodeLabelsManager
operator|.
name|NO_LABEL
argument_list|)
condition|)
block|{
return|return
name|normalize
argument_list|(
name|noLabelUsages
operator|.
name|resArr
operator|.
name|get
argument_list|(
name|type
operator|.
name|idx
argument_list|)
argument_list|)
return|;
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
operator|.
name|get
argument_list|(
name|type
operator|.
name|idx
argument_list|)
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
specifier|protected
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
operator|.
name|get
argument_list|(
name|type
operator|.
name|idx
argument_list|)
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
operator|||
name|label
operator|.
name|equals
argument_list|(
name|RMNodeLabelsManager
operator|.
name|NO_LABEL
argument_list|)
condition|)
block|{
return|return
name|noLabelUsages
return|;
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
argument_list|()
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
specifier|protected
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
operator|.
name|set
argument_list|(
name|type
operator|.
name|idx
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
DECL|method|_inc (String label, ResourceType type, Resource res)
specifier|protected
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
name|usage
operator|.
name|resArr
operator|.
name|set
argument_list|(
name|type
operator|.
name|idx
argument_list|,
name|Resources
operator|.
name|add
argument_list|(
name|usage
operator|.
name|resArr
operator|.
name|get
argument_list|(
name|type
operator|.
name|idx
argument_list|)
argument_list|,
name|res
argument_list|)
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
specifier|protected
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
name|usage
operator|.
name|resArr
operator|.
name|set
argument_list|(
name|type
operator|.
name|idx
argument_list|,
name|Resources
operator|.
name|subtract
argument_list|(
name|usage
operator|.
name|resArr
operator|.
name|get
argument_list|(
name|type
operator|.
name|idx
argument_list|)
argument_list|,
name|res
argument_list|)
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
DECL|method|getNodePartitionsSet ()
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getNodePartitionsSet
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
name|keySet
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

