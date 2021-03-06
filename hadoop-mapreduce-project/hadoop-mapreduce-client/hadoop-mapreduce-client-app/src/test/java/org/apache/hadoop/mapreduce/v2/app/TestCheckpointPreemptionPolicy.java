begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.app
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
operator|.
name|app
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
name|PreemptionContract
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
name|PreemptionMessage
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
name|Priority
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

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|*
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

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
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|TaskAttemptListenerImpl
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
name|mapreduce
operator|.
name|v2
operator|.
name|api
operator|.
name|records
operator|.
name|JobId
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
name|mapreduce
operator|.
name|v2
operator|.
name|api
operator|.
name|records
operator|.
name|TaskAttemptId
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
name|mapreduce
operator|.
name|v2
operator|.
name|api
operator|.
name|records
operator|.
name|TaskId
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
name|mapreduce
operator|.
name|v2
operator|.
name|api
operator|.
name|records
operator|.
name|TaskType
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
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|MRAppMaster
operator|.
name|RunningAppContext
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
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|rm
operator|.
name|RMContainerAllocator
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
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|rm
operator|.
name|preemption
operator|.
name|AMPreemptionPolicy
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
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|rm
operator|.
name|preemption
operator|.
name|CheckpointAMPreemptionPolicy
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
name|mapreduce
operator|.
name|v2
operator|.
name|util
operator|.
name|MRBuilderUtils
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
name|PreemptionContainer
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
name|PreemptionResourceRequest
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
name|StrictPreemptionContract
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
name|Container
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
name|ContainerId
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
name|ResourceRequest
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
name|event
operator|.
name|Event
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
name|event
operator|.
name|EventHandler
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
name|factories
operator|.
name|RecordFactory
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
name|factory
operator|.
name|providers
operator|.
name|RecordFactoryProvider
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
name|Allocation
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_class
DECL|class|TestCheckpointPreemptionPolicy
specifier|public
class|class
name|TestCheckpointPreemptionPolicy
block|{
DECL|field|pel
name|TaskAttemptListenerImpl
name|pel
init|=
literal|null
decl_stmt|;
DECL|field|r
name|RMContainerAllocator
name|r
decl_stmt|;
DECL|field|jid
name|JobId
name|jid
decl_stmt|;
DECL|field|mActxt
name|RunningAppContext
name|mActxt
decl_stmt|;
DECL|field|preemptedContainers
name|Set
argument_list|<
name|ContainerId
argument_list|>
name|preemptedContainers
init|=
operator|new
name|HashSet
argument_list|<
name|ContainerId
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|assignedContainers
name|Map
argument_list|<
name|ContainerId
argument_list|,
name|TaskAttemptId
argument_list|>
name|assignedContainers
init|=
operator|new
name|HashMap
argument_list|<
name|ContainerId
argument_list|,
name|TaskAttemptId
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|recordFactory
specifier|private
specifier|final
name|RecordFactory
name|recordFactory
init|=
name|RecordFactoryProvider
operator|.
name|getRecordFactory
argument_list|(
literal|null
argument_list|)
decl_stmt|;
DECL|field|contToResourceMap
name|HashMap
argument_list|<
name|ContainerId
argument_list|,
name|Resource
argument_list|>
name|contToResourceMap
init|=
operator|new
name|HashMap
argument_list|<
name|ContainerId
argument_list|,
name|Resource
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|minAlloc
specifier|private
name|int
name|minAlloc
init|=
literal|1024
decl_stmt|;
annotation|@
name|Before
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
comment|// mocked generics
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|ApplicationId
name|appId
init|=
name|ApplicationId
operator|.
name|newInstance
argument_list|(
literal|200
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|ApplicationAttemptId
name|appAttemptId
init|=
name|ApplicationAttemptId
operator|.
name|newInstance
argument_list|(
name|appId
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|jid
operator|=
name|MRBuilderUtils
operator|.
name|newJobId
argument_list|(
name|appId
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|mActxt
operator|=
name|mock
argument_list|(
name|RunningAppContext
operator|.
name|class
argument_list|)
expr_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|EventHandler
argument_list|<
name|Event
argument_list|>
name|ea
init|=
name|mock
argument_list|(
name|EventHandler
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|mActxt
operator|.
name|getEventHandler
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|ea
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
literal|40
condition|;
operator|++
name|i
control|)
block|{
name|ContainerId
name|cId
init|=
name|ContainerId
operator|.
name|newContainerId
argument_list|(
name|appAttemptId
argument_list|,
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
literal|0
operator|==
name|i
operator|%
literal|7
condition|)
block|{
name|preemptedContainers
operator|.
name|add
argument_list|(
name|cId
argument_list|)
expr_stmt|;
block|}
name|TaskId
name|tId
init|=
literal|0
operator|==
name|i
operator|%
literal|2
condition|?
name|MRBuilderUtils
operator|.
name|newTaskId
argument_list|(
name|jid
argument_list|,
name|i
operator|/
literal|2
argument_list|,
name|TaskType
operator|.
name|MAP
argument_list|)
else|:
name|MRBuilderUtils
operator|.
name|newTaskId
argument_list|(
name|jid
argument_list|,
name|i
operator|/
literal|2
operator|+
literal|1
argument_list|,
name|TaskType
operator|.
name|REDUCE
argument_list|)
decl_stmt|;
name|assignedContainers
operator|.
name|put
argument_list|(
name|cId
argument_list|,
name|MRBuilderUtils
operator|.
name|newTaskAttemptId
argument_list|(
name|tId
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|contToResourceMap
operator|.
name|put
argument_list|(
name|cId
argument_list|,
name|Resource
operator|.
name|newInstance
argument_list|(
literal|2
operator|*
name|minAlloc
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|ContainerId
argument_list|,
name|TaskAttemptId
argument_list|>
name|ent
range|:
name|assignedContainers
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"cont:"
operator|+
name|ent
operator|.
name|getKey
argument_list|()
operator|.
name|getContainerId
argument_list|()
operator|+
literal|" type:"
operator|+
name|ent
operator|.
name|getValue
argument_list|()
operator|.
name|getTaskId
argument_list|()
operator|.
name|getTaskType
argument_list|()
operator|+
literal|" res:"
operator|+
name|contToResourceMap
operator|.
name|get
argument_list|(
name|ent
operator|.
name|getKey
argument_list|()
argument_list|)
operator|.
name|getMemorySize
argument_list|()
operator|+
literal|"MB"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testStrictPreemptionContract ()
specifier|public
name|void
name|testStrictPreemptionContract
parameter_list|()
block|{
specifier|final
name|Map
argument_list|<
name|ContainerId
argument_list|,
name|TaskAttemptId
argument_list|>
name|containers
init|=
name|assignedContainers
decl_stmt|;
name|AMPreemptionPolicy
operator|.
name|Context
name|mPctxt
init|=
operator|new
name|AMPreemptionPolicy
operator|.
name|Context
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|TaskAttemptId
name|getTaskAttempt
parameter_list|(
name|ContainerId
name|cId
parameter_list|)
block|{
return|return
name|containers
operator|.
name|get
argument_list|(
name|cId
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|Container
argument_list|>
name|getContainers
parameter_list|(
name|TaskType
name|t
parameter_list|)
block|{
name|List
argument_list|<
name|Container
argument_list|>
name|p
init|=
operator|new
name|ArrayList
argument_list|<
name|Container
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|ContainerId
argument_list|,
name|TaskAttemptId
argument_list|>
name|ent
range|:
name|assignedContainers
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|ent
operator|.
name|getValue
argument_list|()
operator|.
name|getTaskId
argument_list|()
operator|.
name|getTaskType
argument_list|()
operator|.
name|equals
argument_list|(
name|t
argument_list|)
condition|)
block|{
name|p
operator|.
name|add
argument_list|(
name|Container
operator|.
name|newInstance
argument_list|(
name|ent
operator|.
name|getKey
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|contToResourceMap
operator|.
name|get
argument_list|(
name|ent
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|,
name|Priority
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|)
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|p
return|;
block|}
block|}
decl_stmt|;
name|PreemptionMessage
name|pM
init|=
name|generatePreemptionMessage
argument_list|(
name|preemptedContainers
argument_list|,
name|contToResourceMap
argument_list|,
name|Resource
operator|.
name|newInstance
argument_list|(
literal|1024
argument_list|,
literal|1
argument_list|)
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|CheckpointAMPreemptionPolicy
name|policy
init|=
operator|new
name|CheckpointAMPreemptionPolicy
argument_list|()
decl_stmt|;
name|policy
operator|.
name|init
argument_list|(
name|mActxt
argument_list|)
expr_stmt|;
name|policy
operator|.
name|preempt
argument_list|(
name|mPctxt
argument_list|,
name|pM
argument_list|)
expr_stmt|;
for|for
control|(
name|ContainerId
name|c
range|:
name|preemptedContainers
control|)
block|{
name|TaskAttemptId
name|t
init|=
name|assignedContainers
operator|.
name|get
argument_list|(
name|c
argument_list|)
decl_stmt|;
if|if
condition|(
name|TaskType
operator|.
name|MAP
operator|.
name|equals
argument_list|(
name|t
operator|.
name|getTaskId
argument_list|()
operator|.
name|getTaskType
argument_list|()
argument_list|)
condition|)
block|{
assert|assert
name|policy
operator|.
name|isPreempted
argument_list|(
name|t
argument_list|)
operator|==
literal|false
assert|;
block|}
else|else
block|{
assert|assert
name|policy
operator|.
name|isPreempted
argument_list|(
name|t
argument_list|)
assert|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testPreemptionContract ()
specifier|public
name|void
name|testPreemptionContract
parameter_list|()
block|{
specifier|final
name|Map
argument_list|<
name|ContainerId
argument_list|,
name|TaskAttemptId
argument_list|>
name|containers
init|=
name|assignedContainers
decl_stmt|;
name|AMPreemptionPolicy
operator|.
name|Context
name|mPctxt
init|=
operator|new
name|AMPreemptionPolicy
operator|.
name|Context
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|TaskAttemptId
name|getTaskAttempt
parameter_list|(
name|ContainerId
name|cId
parameter_list|)
block|{
return|return
name|containers
operator|.
name|get
argument_list|(
name|cId
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|Container
argument_list|>
name|getContainers
parameter_list|(
name|TaskType
name|t
parameter_list|)
block|{
name|List
argument_list|<
name|Container
argument_list|>
name|p
init|=
operator|new
name|ArrayList
argument_list|<
name|Container
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|ContainerId
argument_list|,
name|TaskAttemptId
argument_list|>
name|ent
range|:
name|assignedContainers
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|ent
operator|.
name|getValue
argument_list|()
operator|.
name|getTaskId
argument_list|()
operator|.
name|getTaskType
argument_list|()
operator|.
name|equals
argument_list|(
name|t
argument_list|)
condition|)
block|{
name|p
operator|.
name|add
argument_list|(
name|Container
operator|.
name|newInstance
argument_list|(
name|ent
operator|.
name|getKey
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|contToResourceMap
operator|.
name|get
argument_list|(
name|ent
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|,
name|Priority
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|)
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|p
return|;
block|}
block|}
decl_stmt|;
name|PreemptionMessage
name|pM
init|=
name|generatePreemptionMessage
argument_list|(
name|preemptedContainers
argument_list|,
name|contToResourceMap
argument_list|,
name|Resource
operator|.
name|newInstance
argument_list|(
name|minAlloc
argument_list|,
literal|1
argument_list|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|CheckpointAMPreemptionPolicy
name|policy
init|=
operator|new
name|CheckpointAMPreemptionPolicy
argument_list|()
decl_stmt|;
name|policy
operator|.
name|init
argument_list|(
name|mActxt
argument_list|)
expr_stmt|;
name|int
name|supposedMemPreemption
init|=
operator|(
name|int
operator|)
name|pM
operator|.
name|getContract
argument_list|()
operator|.
name|getResourceRequest
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getResourceRequest
argument_list|()
operator|.
name|getCapability
argument_list|()
operator|.
name|getMemorySize
argument_list|()
operator|*
name|pM
operator|.
name|getContract
argument_list|()
operator|.
name|getResourceRequest
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getResourceRequest
argument_list|()
operator|.
name|getNumContainers
argument_list|()
decl_stmt|;
comment|// first round of preemption
name|policy
operator|.
name|preempt
argument_list|(
name|mPctxt
argument_list|,
name|pM
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|TaskAttemptId
argument_list|>
name|preempting
init|=
name|validatePreemption
argument_list|(
name|pM
argument_list|,
name|policy
argument_list|,
name|supposedMemPreemption
argument_list|)
decl_stmt|;
comment|// redundant message
name|policy
operator|.
name|preempt
argument_list|(
name|mPctxt
argument_list|,
name|pM
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|TaskAttemptId
argument_list|>
name|preempting2
init|=
name|validatePreemption
argument_list|(
name|pM
argument_list|,
name|policy
argument_list|,
name|supposedMemPreemption
argument_list|)
decl_stmt|;
comment|// check that nothing got added
assert|assert
name|preempting2
operator|.
name|equals
argument_list|(
name|preempting
argument_list|)
assert|;
comment|// simulate 2 task completions/successful preemption
name|policy
operator|.
name|handleCompletedContainer
argument_list|(
name|preempting
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|policy
operator|.
name|handleCompletedContainer
argument_list|(
name|preempting
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
comment|// remove from assignedContainers
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|ContainerId
argument_list|,
name|TaskAttemptId
argument_list|>
argument_list|>
name|it
init|=
name|assignedContainers
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|ContainerId
argument_list|,
name|TaskAttemptId
argument_list|>
name|ent
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|ent
operator|.
name|getValue
argument_list|()
operator|.
name|equals
argument_list|(
name|preempting
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|||
name|ent
operator|.
name|getValue
argument_list|()
operator|.
name|equals
argument_list|(
name|preempting
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
condition|)
name|it
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
comment|// one more message asking for preemption
name|policy
operator|.
name|preempt
argument_list|(
name|mPctxt
argument_list|,
name|pM
argument_list|)
expr_stmt|;
comment|// triggers preemption of 2 more containers (i.e., the preemption set changes)
name|List
argument_list|<
name|TaskAttemptId
argument_list|>
name|preempting3
init|=
name|validatePreemption
argument_list|(
name|pM
argument_list|,
name|policy
argument_list|,
name|supposedMemPreemption
argument_list|)
decl_stmt|;
assert|assert
name|preempting3
operator|.
name|equals
argument_list|(
name|preempting2
argument_list|)
operator|==
literal|false
assert|;
block|}
DECL|method|validatePreemption (PreemptionMessage pM, CheckpointAMPreemptionPolicy policy, int supposedMemPreemption)
specifier|private
name|List
argument_list|<
name|TaskAttemptId
argument_list|>
name|validatePreemption
parameter_list|(
name|PreemptionMessage
name|pM
parameter_list|,
name|CheckpointAMPreemptionPolicy
name|policy
parameter_list|,
name|int
name|supposedMemPreemption
parameter_list|)
block|{
name|Resource
name|effectivelyPreempted
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
name|List
argument_list|<
name|TaskAttemptId
argument_list|>
name|preempting
init|=
operator|new
name|ArrayList
argument_list|<
name|TaskAttemptId
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|ContainerId
argument_list|,
name|TaskAttemptId
argument_list|>
name|ent
range|:
name|assignedContainers
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|policy
operator|.
name|isPreempted
argument_list|(
name|ent
operator|.
name|getValue
argument_list|()
argument_list|)
condition|)
block|{
name|Resources
operator|.
name|addTo
argument_list|(
name|effectivelyPreempted
argument_list|,
name|contToResourceMap
operator|.
name|get
argument_list|(
name|ent
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// preempt only reducers
if|if
condition|(
name|policy
operator|.
name|isPreempted
argument_list|(
name|ent
operator|.
name|getValue
argument_list|()
argument_list|)
condition|)
block|{
name|assertEquals
argument_list|(
name|TaskType
operator|.
name|REDUCE
argument_list|,
name|ent
operator|.
name|getValue
argument_list|()
operator|.
name|getTaskId
argument_list|()
operator|.
name|getTaskType
argument_list|()
argument_list|)
expr_stmt|;
name|preempting
operator|.
name|add
argument_list|(
name|ent
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// preempt enough
assert|assert
operator|(
name|effectivelyPreempted
operator|.
name|getMemorySize
argument_list|()
operator|>=
name|supposedMemPreemption
operator|)
operator|:
literal|" preempted: "
operator|+
name|effectivelyPreempted
operator|.
name|getMemorySize
argument_list|()
assert|;
comment|// preempt not too much enough
assert|assert
name|effectivelyPreempted
operator|.
name|getMemorySize
argument_list|()
operator|<=
name|supposedMemPreemption
operator|+
name|minAlloc
assert|;
return|return
name|preempting
return|;
block|}
DECL|method|generatePreemptionMessage ( Set<ContainerId> containerToPreempt, HashMap<ContainerId, Resource> resPerCont, Resource minimumAllocation, boolean strict)
specifier|private
name|PreemptionMessage
name|generatePreemptionMessage
parameter_list|(
name|Set
argument_list|<
name|ContainerId
argument_list|>
name|containerToPreempt
parameter_list|,
name|HashMap
argument_list|<
name|ContainerId
argument_list|,
name|Resource
argument_list|>
name|resPerCont
parameter_list|,
name|Resource
name|minimumAllocation
parameter_list|,
name|boolean
name|strict
parameter_list|)
block|{
name|Set
argument_list|<
name|ContainerId
argument_list|>
name|currentContPreemption
init|=
name|Collections
operator|.
name|unmodifiableSet
argument_list|(
operator|new
name|HashSet
argument_list|<
name|ContainerId
argument_list|>
argument_list|(
name|containerToPreempt
argument_list|)
argument_list|)
decl_stmt|;
name|containerToPreempt
operator|.
name|clear
argument_list|()
expr_stmt|;
name|Resource
name|tot
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
for|for
control|(
name|ContainerId
name|c
range|:
name|currentContPreemption
control|)
block|{
name|Resources
operator|.
name|addTo
argument_list|(
name|tot
argument_list|,
name|resPerCont
operator|.
name|get
argument_list|(
name|c
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|int
name|numCont
init|=
operator|(
name|int
operator|)
name|Math
operator|.
name|ceil
argument_list|(
name|tot
operator|.
name|getMemorySize
argument_list|()
operator|/
operator|(
name|double
operator|)
name|minimumAllocation
operator|.
name|getMemorySize
argument_list|()
argument_list|)
decl_stmt|;
name|ResourceRequest
name|rr
init|=
name|ResourceRequest
operator|.
name|newInstance
argument_list|(
name|Priority
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|)
argument_list|,
name|ResourceRequest
operator|.
name|ANY
argument_list|,
name|minimumAllocation
argument_list|,
name|numCont
argument_list|)
decl_stmt|;
if|if
condition|(
name|strict
condition|)
block|{
return|return
name|generatePreemptionMessage
argument_list|(
operator|new
name|Allocation
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
name|currentContPreemption
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|)
return|;
block|}
return|return
name|generatePreemptionMessage
argument_list|(
operator|new
name|Allocation
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|currentContPreemption
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|rr
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
DECL|method|generatePreemptionMessage (Allocation allocation)
specifier|private
name|PreemptionMessage
name|generatePreemptionMessage
parameter_list|(
name|Allocation
name|allocation
parameter_list|)
block|{
name|PreemptionMessage
name|pMsg
init|=
literal|null
decl_stmt|;
comment|// assemble strict preemption request
if|if
condition|(
name|allocation
operator|.
name|getStrictContainerPreemptions
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|pMsg
operator|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|PreemptionMessage
operator|.
name|class
argument_list|)
expr_stmt|;
name|StrictPreemptionContract
name|pStrict
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|StrictPreemptionContract
operator|.
name|class
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|PreemptionContainer
argument_list|>
name|pCont
init|=
operator|new
name|HashSet
argument_list|<
name|PreemptionContainer
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|ContainerId
name|cId
range|:
name|allocation
operator|.
name|getStrictContainerPreemptions
argument_list|()
control|)
block|{
name|PreemptionContainer
name|pc
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|PreemptionContainer
operator|.
name|class
argument_list|)
decl_stmt|;
name|pc
operator|.
name|setId
argument_list|(
name|cId
argument_list|)
expr_stmt|;
name|pCont
operator|.
name|add
argument_list|(
name|pc
argument_list|)
expr_stmt|;
block|}
name|pStrict
operator|.
name|setContainers
argument_list|(
name|pCont
argument_list|)
expr_stmt|;
name|pMsg
operator|.
name|setStrictContract
argument_list|(
name|pStrict
argument_list|)
expr_stmt|;
block|}
comment|// assemble negotiable preemption request
if|if
condition|(
name|allocation
operator|.
name|getResourcePreemptions
argument_list|()
operator|!=
literal|null
operator|&&
name|allocation
operator|.
name|getResourcePreemptions
argument_list|()
operator|.
name|size
argument_list|()
operator|>
literal|0
operator|&&
name|allocation
operator|.
name|getContainerPreemptions
argument_list|()
operator|!=
literal|null
operator|&&
name|allocation
operator|.
name|getContainerPreemptions
argument_list|()
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|pMsg
operator|==
literal|null
condition|)
block|{
name|pMsg
operator|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|PreemptionMessage
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
name|PreemptionContract
name|contract
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|PreemptionContract
operator|.
name|class
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|PreemptionContainer
argument_list|>
name|pCont
init|=
operator|new
name|HashSet
argument_list|<
name|PreemptionContainer
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|ContainerId
name|cId
range|:
name|allocation
operator|.
name|getContainerPreemptions
argument_list|()
control|)
block|{
name|PreemptionContainer
name|pc
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|PreemptionContainer
operator|.
name|class
argument_list|)
decl_stmt|;
name|pc
operator|.
name|setId
argument_list|(
name|cId
argument_list|)
expr_stmt|;
name|pCont
operator|.
name|add
argument_list|(
name|pc
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|PreemptionResourceRequest
argument_list|>
name|pRes
init|=
operator|new
name|ArrayList
argument_list|<
name|PreemptionResourceRequest
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|ResourceRequest
name|crr
range|:
name|allocation
operator|.
name|getResourcePreemptions
argument_list|()
control|)
block|{
name|PreemptionResourceRequest
name|prr
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|PreemptionResourceRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|prr
operator|.
name|setResourceRequest
argument_list|(
name|crr
argument_list|)
expr_stmt|;
name|pRes
operator|.
name|add
argument_list|(
name|prr
argument_list|)
expr_stmt|;
block|}
name|contract
operator|.
name|setContainers
argument_list|(
name|pCont
argument_list|)
expr_stmt|;
name|contract
operator|.
name|setResourceRequest
argument_list|(
name|pRes
argument_list|)
expr_stmt|;
name|pMsg
operator|.
name|setContract
argument_list|(
name|contract
argument_list|)
expr_stmt|;
block|}
return|return
name|pMsg
return|;
block|}
block|}
end_class

end_unit

