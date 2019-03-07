begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt
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
name|rmapp
operator|.
name|attempt
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
name|concurrent
operator|.
name|ConcurrentHashMap
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
name|AtomicBoolean
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
name|AtomicInteger
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
name|AtomicLong
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
name|commons
operator|.
name|lang3
operator|.
name|time
operator|.
name|DateUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|ApplicationAttemptId
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
name|ApplicationResourceUsageReport
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
name|api
operator|.
name|records
operator|.
name|ResourceInformation
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
name|RMContext
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
name|rmapp
operator|.
name|RMApp
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
name|rmcontainer
operator|.
name|RMContainer
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
name|NodeType
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

begin_class
DECL|class|RMAppAttemptMetrics
specifier|public
class|class
name|RMAppAttemptMetrics
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|RMAppAttemptMetrics
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|attemptId
specifier|private
name|ApplicationAttemptId
name|attemptId
init|=
literal|null
decl_stmt|;
comment|// preemption info
DECL|field|resourcePreempted
specifier|private
name|Resource
name|resourcePreempted
init|=
name|Resource
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
decl_stmt|;
comment|// application headroom
DECL|field|applicationHeadroom
specifier|private
specifier|volatile
name|Resource
name|applicationHeadroom
init|=
name|Resource
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
decl_stmt|;
DECL|field|numNonAMContainersPreempted
specifier|private
name|AtomicInteger
name|numNonAMContainersPreempted
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|field|isPreempted
specifier|private
name|AtomicBoolean
name|isPreempted
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
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
DECL|field|resourceUsageMap
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|AtomicLong
argument_list|>
name|resourceUsageMap
init|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|preemptedResourceMap
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|AtomicLong
argument_list|>
name|preemptedResourceMap
init|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|rmContext
specifier|private
name|RMContext
name|rmContext
decl_stmt|;
DECL|field|localityStatistics
specifier|private
name|int
index|[]
index|[]
name|localityStatistics
init|=
operator|new
name|int
index|[
name|NodeType
operator|.
name|values
argument_list|()
operator|.
name|length
index|]
index|[
name|NodeType
operator|.
name|values
argument_list|()
operator|.
name|length
index|]
decl_stmt|;
DECL|field|totalAllocatedContainers
specifier|private
specifier|volatile
name|int
name|totalAllocatedContainers
decl_stmt|;
DECL|method|RMAppAttemptMetrics (ApplicationAttemptId attemptId, RMContext rmContext)
specifier|public
name|RMAppAttemptMetrics
parameter_list|(
name|ApplicationAttemptId
name|attemptId
parameter_list|,
name|RMContext
name|rmContext
parameter_list|)
block|{
name|this
operator|.
name|attemptId
operator|=
name|attemptId
expr_stmt|;
name|ReentrantReadWriteLock
name|lock
init|=
operator|new
name|ReentrantReadWriteLock
argument_list|()
decl_stmt|;
name|this
operator|.
name|readLock
operator|=
name|lock
operator|.
name|readLock
argument_list|()
expr_stmt|;
name|this
operator|.
name|writeLock
operator|=
name|lock
operator|.
name|writeLock
argument_list|()
expr_stmt|;
name|this
operator|.
name|rmContext
operator|=
name|rmContext
expr_stmt|;
block|}
DECL|method|updatePreemptionInfo (Resource resource, RMContainer container)
specifier|public
name|void
name|updatePreemptionInfo
parameter_list|(
name|Resource
name|resource
parameter_list|,
name|RMContainer
name|container
parameter_list|)
block|{
name|writeLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|resourcePreempted
operator|=
name|Resources
operator|.
name|addTo
argument_list|(
name|resourcePreempted
argument_list|,
name|resource
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
if|if
condition|(
operator|!
name|container
operator|.
name|isAMContainer
argument_list|()
condition|)
block|{
comment|// container got preempted is not a master container
name|LOG
operator|.
name|info
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Non-AM container preempted, current appAttemptId=%s, "
operator|+
literal|"containerId=%s, resource=%s"
argument_list|,
name|attemptId
argument_list|,
name|container
operator|.
name|getContainerId
argument_list|()
argument_list|,
name|resource
argument_list|)
argument_list|)
expr_stmt|;
name|numNonAMContainersPreempted
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// container got preempted is a master container
name|LOG
operator|.
name|info
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"AM container preempted, "
operator|+
literal|"current appAttemptId=%s, containerId=%s, resource=%s"
argument_list|,
name|attemptId
argument_list|,
name|container
operator|.
name|getContainerId
argument_list|()
argument_list|,
name|resource
argument_list|)
argument_list|)
expr_stmt|;
name|isPreempted
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getResourcePreempted ()
specifier|public
name|Resource
name|getResourcePreempted
parameter_list|()
block|{
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
return|return
name|Resource
operator|.
name|newInstance
argument_list|(
name|resourcePreempted
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
DECL|method|getPreemptedMemory ()
specifier|public
name|long
name|getPreemptedMemory
parameter_list|()
block|{
return|return
name|preemptedResourceMap
operator|.
name|get
argument_list|(
name|ResourceInformation
operator|.
name|MEMORY_MB
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|getPreemptedVcore ()
specifier|public
name|long
name|getPreemptedVcore
parameter_list|()
block|{
return|return
name|preemptedResourceMap
operator|.
name|get
argument_list|(
name|ResourceInformation
operator|.
name|VCORES
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|getPreemptedResourceSecondsMap ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|getPreemptedResourceSecondsMap
parameter_list|()
block|{
return|return
name|convertAtomicLongMaptoLongMap
argument_list|(
name|preemptedResourceMap
argument_list|)
return|;
block|}
DECL|method|getNumNonAMContainersPreempted ()
specifier|public
name|int
name|getNumNonAMContainersPreempted
parameter_list|()
block|{
return|return
name|numNonAMContainersPreempted
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|setIsPreempted ()
specifier|public
name|void
name|setIsPreempted
parameter_list|()
block|{
name|this
operator|.
name|isPreempted
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|getIsPreempted ()
specifier|public
name|boolean
name|getIsPreempted
parameter_list|()
block|{
return|return
name|this
operator|.
name|isPreempted
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|getAggregateAppResourceUsage ()
specifier|public
name|AggregateAppResourceUsage
name|getAggregateAppResourceUsage
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|resourcesUsed
init|=
name|convertAtomicLongMaptoLongMap
argument_list|(
name|resourceUsageMap
argument_list|)
decl_stmt|;
comment|// Only add in the running containers if this is the active attempt.
name|RMApp
name|rmApp
init|=
name|rmContext
operator|.
name|getRMApps
argument_list|()
operator|.
name|get
argument_list|(
name|attemptId
operator|.
name|getApplicationId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|rmApp
operator|!=
literal|null
condition|)
block|{
name|RMAppAttempt
name|currentAttempt
init|=
name|rmApp
operator|.
name|getCurrentAppAttempt
argument_list|()
decl_stmt|;
if|if
condition|(
name|currentAttempt
operator|!=
literal|null
operator|&&
name|currentAttempt
operator|.
name|getAppAttemptId
argument_list|()
operator|.
name|equals
argument_list|(
name|attemptId
argument_list|)
condition|)
block|{
name|ApplicationResourceUsageReport
name|appResUsageReport
init|=
name|rmContext
operator|.
name|getScheduler
argument_list|()
operator|.
name|getAppResourceUsageReport
argument_list|(
name|attemptId
argument_list|)
decl_stmt|;
if|if
condition|(
name|appResUsageReport
operator|!=
literal|null
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|tmp
init|=
name|appResUsageReport
operator|.
name|getResourceSecondsMap
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|entry
range|:
name|tmp
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Long
name|value
init|=
name|resourcesUsed
operator|.
name|get
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
name|value
operator|+=
name|entry
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|value
operator|=
name|entry
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
name|resourcesUsed
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
return|return
operator|new
name|AggregateAppResourceUsage
argument_list|(
name|resourcesUsed
argument_list|)
return|;
block|}
DECL|method|updateAggregateAppResourceUsage (Resource allocated, long deltaUsedMillis)
specifier|public
name|void
name|updateAggregateAppResourceUsage
parameter_list|(
name|Resource
name|allocated
parameter_list|,
name|long
name|deltaUsedMillis
parameter_list|)
block|{
name|updateUsageMap
argument_list|(
name|allocated
argument_list|,
name|deltaUsedMillis
argument_list|,
name|resourceUsageMap
argument_list|)
expr_stmt|;
block|}
DECL|method|updateAggregatePreemptedAppResourceUsage (Resource allocated, long deltaUsedMillis)
specifier|public
name|void
name|updateAggregatePreemptedAppResourceUsage
parameter_list|(
name|Resource
name|allocated
parameter_list|,
name|long
name|deltaUsedMillis
parameter_list|)
block|{
name|updateUsageMap
argument_list|(
name|allocated
argument_list|,
name|deltaUsedMillis
argument_list|,
name|preemptedResourceMap
argument_list|)
expr_stmt|;
block|}
DECL|method|updateAggregateAppResourceUsage ( Map<String, Long> resourceSecondsMap)
specifier|public
name|void
name|updateAggregateAppResourceUsage
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|resourceSecondsMap
parameter_list|)
block|{
name|updateUsageMap
argument_list|(
name|resourceSecondsMap
argument_list|,
name|resourceUsageMap
argument_list|)
expr_stmt|;
block|}
DECL|method|updateAggregatePreemptedAppResourceUsage ( Map<String, Long> preemptedResourceSecondsMap)
specifier|public
name|void
name|updateAggregatePreemptedAppResourceUsage
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|preemptedResourceSecondsMap
parameter_list|)
block|{
name|updateUsageMap
argument_list|(
name|preemptedResourceSecondsMap
argument_list|,
name|preemptedResourceMap
argument_list|)
expr_stmt|;
block|}
DECL|method|updateUsageMap (Resource allocated, long deltaUsedMillis, Map<String, AtomicLong> targetMap)
specifier|private
name|void
name|updateUsageMap
parameter_list|(
name|Resource
name|allocated
parameter_list|,
name|long
name|deltaUsedMillis
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|AtomicLong
argument_list|>
name|targetMap
parameter_list|)
block|{
for|for
control|(
name|ResourceInformation
name|entry
range|:
name|allocated
operator|.
name|getResources
argument_list|()
control|)
block|{
name|AtomicLong
name|resourceUsed
decl_stmt|;
if|if
condition|(
operator|!
name|targetMap
operator|.
name|containsKey
argument_list|(
name|entry
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|resourceUsed
operator|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|targetMap
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getName
argument_list|()
argument_list|,
name|resourceUsed
argument_list|)
expr_stmt|;
block|}
name|resourceUsed
operator|=
name|targetMap
operator|.
name|get
argument_list|(
name|entry
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|resourceUsed
operator|.
name|addAndGet
argument_list|(
operator|(
name|entry
operator|.
name|getValue
argument_list|()
operator|*
name|deltaUsedMillis
operator|)
operator|/
name|DateUtils
operator|.
name|MILLIS_PER_SECOND
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|updateUsageMap (Map<String, Long> sourceMap, Map<String, AtomicLong> targetMap)
specifier|private
name|void
name|updateUsageMap
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|sourceMap
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|AtomicLong
argument_list|>
name|targetMap
parameter_list|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|entry
range|:
name|sourceMap
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|AtomicLong
name|resourceUsed
decl_stmt|;
if|if
condition|(
operator|!
name|targetMap
operator|.
name|containsKey
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
block|{
name|resourceUsed
operator|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|targetMap
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|resourceUsed
argument_list|)
expr_stmt|;
block|}
name|resourceUsed
operator|=
name|targetMap
operator|.
name|get
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|resourceUsed
operator|.
name|set
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|convertAtomicLongMaptoLongMap ( Map<String, AtomicLong> source)
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|convertAtomicLongMaptoLongMap
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|AtomicLong
argument_list|>
name|source
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|ret
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|AtomicLong
argument_list|>
name|entry
range|:
name|source
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|ret
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
DECL|method|incNumAllocatedContainers (NodeType containerType, NodeType requestType)
specifier|public
name|void
name|incNumAllocatedContainers
parameter_list|(
name|NodeType
name|containerType
parameter_list|,
name|NodeType
name|requestType
parameter_list|)
block|{
name|localityStatistics
index|[
name|containerType
operator|.
name|getIndex
argument_list|()
index|]
index|[
name|requestType
operator|.
name|getIndex
argument_list|()
index|]
operator|++
expr_stmt|;
name|totalAllocatedContainers
operator|++
expr_stmt|;
block|}
DECL|method|getLocalityStatistics ()
specifier|public
name|int
index|[]
index|[]
name|getLocalityStatistics
parameter_list|()
block|{
return|return
name|this
operator|.
name|localityStatistics
return|;
block|}
DECL|method|getTotalAllocatedContainers ()
specifier|public
name|int
name|getTotalAllocatedContainers
parameter_list|()
block|{
return|return
name|this
operator|.
name|totalAllocatedContainers
return|;
block|}
DECL|method|getApplicationAttemptHeadroom ()
specifier|public
name|Resource
name|getApplicationAttemptHeadroom
parameter_list|()
block|{
return|return
name|Resource
operator|.
name|newInstance
argument_list|(
name|applicationHeadroom
argument_list|)
return|;
block|}
DECL|method|setApplicationAttemptHeadRoom (Resource headRoom)
specifier|public
name|void
name|setApplicationAttemptHeadRoom
parameter_list|(
name|Resource
name|headRoom
parameter_list|)
block|{
name|this
operator|.
name|applicationHeadroom
operator|=
name|headRoom
expr_stmt|;
block|}
block|}
end_class

end_unit

