begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.app.rm.preemption
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
operator|.
name|rm
operator|.
name|preemption
package|;
end_package

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
name|Comparator
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
name|mapreduce
operator|.
name|JobCounter
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
name|checkpoint
operator|.
name|TaskCheckpointID
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
name|AppContext
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
name|job
operator|.
name|event
operator|.
name|JobCounterUpdateEvent
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
name|event
operator|.
name|EventHandler
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

begin_comment
comment|/**  * This policy works in combination with an implementation of task  * checkpointing. It computes the tasks to be preempted in response to the RM  * request for preemption. For strict requests, it maps containers to  * corresponding tasks; for fungible requests, it attempts to pick the best  * containers to preempt (reducers in reverse allocation order). The  * TaskAttemptListener will interrogate this policy when handling a task  * heartbeat to check whether the task should be preempted or not. When handling  * fungible requests, the policy discount the RM ask by the amount of currently  * in-flight preemptions (i.e., tasks that are checkpointing).  *  * This class it is also used to maintain the list of checkpoints for existing  * tasks. Centralizing this functionality here, allows us to have visibility on  * preemption and checkpoints in a single location, thus coordinating preemption  * and checkpoint management decisions in a single policy.  */
end_comment

begin_class
DECL|class|CheckpointAMPreemptionPolicy
specifier|public
class|class
name|CheckpointAMPreemptionPolicy
implements|implements
name|AMPreemptionPolicy
block|{
comment|// task attempts flagged for preemption
DECL|field|toBePreempted
specifier|private
specifier|final
name|Set
argument_list|<
name|TaskAttemptId
argument_list|>
name|toBePreempted
decl_stmt|;
DECL|field|countedPreemptions
specifier|private
specifier|final
name|Set
argument_list|<
name|TaskAttemptId
argument_list|>
name|countedPreemptions
decl_stmt|;
DECL|field|checkpoints
specifier|private
specifier|final
name|Map
argument_list|<
name|TaskId
argument_list|,
name|TaskCheckpointID
argument_list|>
name|checkpoints
decl_stmt|;
DECL|field|pendingFlexiblePreemptions
specifier|private
specifier|final
name|Map
argument_list|<
name|TaskAttemptId
argument_list|,
name|Resource
argument_list|>
name|pendingFlexiblePreemptions
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
DECL|field|eventHandler
specifier|private
name|EventHandler
name|eventHandler
decl_stmt|;
DECL|field|LOG
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|CheckpointAMPreemptionPolicy
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|CheckpointAMPreemptionPolicy ()
specifier|public
name|CheckpointAMPreemptionPolicy
parameter_list|()
block|{
name|this
argument_list|(
name|Collections
operator|.
name|synchronizedSet
argument_list|(
operator|new
name|HashSet
argument_list|<
name|TaskAttemptId
argument_list|>
argument_list|()
argument_list|)
argument_list|,
name|Collections
operator|.
name|synchronizedSet
argument_list|(
operator|new
name|HashSet
argument_list|<
name|TaskAttemptId
argument_list|>
argument_list|()
argument_list|)
argument_list|,
name|Collections
operator|.
name|synchronizedMap
argument_list|(
operator|new
name|HashMap
argument_list|<
name|TaskId
argument_list|,
name|TaskCheckpointID
argument_list|>
argument_list|()
argument_list|)
argument_list|,
name|Collections
operator|.
name|synchronizedMap
argument_list|(
operator|new
name|HashMap
argument_list|<
name|TaskAttemptId
argument_list|,
name|Resource
argument_list|>
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|CheckpointAMPreemptionPolicy (Set<TaskAttemptId> toBePreempted, Set<TaskAttemptId> countedPreemptions, Map<TaskId,TaskCheckpointID> checkpoints, Map<TaskAttemptId,Resource> pendingFlexiblePreemptions)
name|CheckpointAMPreemptionPolicy
parameter_list|(
name|Set
argument_list|<
name|TaskAttemptId
argument_list|>
name|toBePreempted
parameter_list|,
name|Set
argument_list|<
name|TaskAttemptId
argument_list|>
name|countedPreemptions
parameter_list|,
name|Map
argument_list|<
name|TaskId
argument_list|,
name|TaskCheckpointID
argument_list|>
name|checkpoints
parameter_list|,
name|Map
argument_list|<
name|TaskAttemptId
argument_list|,
name|Resource
argument_list|>
name|pendingFlexiblePreemptions
parameter_list|)
block|{
name|this
operator|.
name|toBePreempted
operator|=
name|toBePreempted
expr_stmt|;
name|this
operator|.
name|countedPreemptions
operator|=
name|countedPreemptions
expr_stmt|;
name|this
operator|.
name|checkpoints
operator|=
name|checkpoints
expr_stmt|;
name|this
operator|.
name|pendingFlexiblePreemptions
operator|=
name|pendingFlexiblePreemptions
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|init (AppContext context)
specifier|public
name|void
name|init
parameter_list|(
name|AppContext
name|context
parameter_list|)
block|{
name|this
operator|.
name|eventHandler
operator|=
name|context
operator|.
name|getEventHandler
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|preempt (Context ctxt, PreemptionMessage preemptionRequests)
specifier|public
name|void
name|preempt
parameter_list|(
name|Context
name|ctxt
parameter_list|,
name|PreemptionMessage
name|preemptionRequests
parameter_list|)
block|{
if|if
condition|(
name|preemptionRequests
operator|!=
literal|null
condition|)
block|{
comment|// handling non-negotiable preemption
name|StrictPreemptionContract
name|cStrict
init|=
name|preemptionRequests
operator|.
name|getStrictContract
argument_list|()
decl_stmt|;
if|if
condition|(
name|cStrict
operator|!=
literal|null
operator|&&
name|cStrict
operator|.
name|getContainers
argument_list|()
operator|!=
literal|null
operator|&&
name|cStrict
operator|.
name|getContainers
argument_list|()
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"strict preemption :"
operator|+
name|preemptionRequests
operator|.
name|getStrictContract
argument_list|()
operator|.
name|getContainers
argument_list|()
operator|.
name|size
argument_list|()
operator|+
literal|" containers to kill"
argument_list|)
expr_stmt|;
comment|// handle strict preemptions. These containers are non-negotiable
for|for
control|(
name|PreemptionContainer
name|c
range|:
name|preemptionRequests
operator|.
name|getStrictContract
argument_list|()
operator|.
name|getContainers
argument_list|()
control|)
block|{
name|ContainerId
name|reqCont
init|=
name|c
operator|.
name|getId
argument_list|()
decl_stmt|;
name|TaskAttemptId
name|reqTask
init|=
name|ctxt
operator|.
name|getTaskAttempt
argument_list|(
name|reqCont
argument_list|)
decl_stmt|;
if|if
condition|(
name|reqTask
operator|!=
literal|null
condition|)
block|{
comment|// ignore requests for preempting containers running maps
if|if
condition|(
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
operator|.
name|REDUCE
operator|.
name|equals
argument_list|(
name|reqTask
operator|.
name|getTaskId
argument_list|()
operator|.
name|getTaskType
argument_list|()
argument_list|)
condition|)
block|{
name|toBePreempted
operator|.
name|add
argument_list|(
name|reqTask
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"preempting "
operator|+
name|reqCont
operator|+
literal|" running task:"
operator|+
name|reqTask
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"NOT preempting "
operator|+
name|reqCont
operator|+
literal|" running task:"
operator|+
name|reqTask
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|// handling negotiable preemption
name|PreemptionContract
name|cNegot
init|=
name|preemptionRequests
operator|.
name|getContract
argument_list|()
decl_stmt|;
if|if
condition|(
name|cNegot
operator|!=
literal|null
operator|&&
name|cNegot
operator|.
name|getResourceRequest
argument_list|()
operator|!=
literal|null
operator|&&
name|cNegot
operator|.
name|getResourceRequest
argument_list|()
operator|.
name|size
argument_list|()
operator|>
literal|0
operator|&&
name|cNegot
operator|.
name|getContainers
argument_list|()
operator|!=
literal|null
operator|&&
name|cNegot
operator|.
name|getContainers
argument_list|()
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"negotiable preemption :"
operator|+
name|preemptionRequests
operator|.
name|getContract
argument_list|()
operator|.
name|getResourceRequest
argument_list|()
operator|.
name|size
argument_list|()
operator|+
literal|" resourceReq, "
operator|+
name|preemptionRequests
operator|.
name|getContract
argument_list|()
operator|.
name|getContainers
argument_list|()
operator|.
name|size
argument_list|()
operator|+
literal|" containers"
argument_list|)
expr_stmt|;
comment|// handle fungible preemption. Here we only look at the total amount of
comment|// resources to be preempted and pick enough of our containers to
comment|// satisfy that. We only support checkpointing for reducers for now.
name|List
argument_list|<
name|PreemptionResourceRequest
argument_list|>
name|reqResources
init|=
name|preemptionRequests
operator|.
name|getContract
argument_list|()
operator|.
name|getResourceRequest
argument_list|()
decl_stmt|;
comment|// compute the total amount of pending preemptions (to be discounted
comment|// from current request)
name|int
name|pendingPreemptionRam
init|=
literal|0
decl_stmt|;
name|int
name|pendingPreemptionCores
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Resource
name|r
range|:
name|pendingFlexiblePreemptions
operator|.
name|values
argument_list|()
control|)
block|{
name|pendingPreemptionRam
operator|+=
name|r
operator|.
name|getMemorySize
argument_list|()
expr_stmt|;
name|pendingPreemptionCores
operator|+=
name|r
operator|.
name|getVirtualCores
argument_list|()
expr_stmt|;
block|}
comment|// discount preemption request based on currently pending preemption
for|for
control|(
name|PreemptionResourceRequest
name|rr
range|:
name|reqResources
control|)
block|{
name|ResourceRequest
name|reqRsrc
init|=
name|rr
operator|.
name|getResourceRequest
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|ResourceRequest
operator|.
name|ANY
operator|.
name|equals
argument_list|(
name|reqRsrc
operator|.
name|getResourceName
argument_list|()
argument_list|)
condition|)
block|{
comment|// For now, only respond to aggregate requests and ignore locality
continue|continue;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"ResourceRequest:"
operator|+
name|reqRsrc
argument_list|)
expr_stmt|;
name|int
name|reqCont
init|=
name|reqRsrc
operator|.
name|getNumContainers
argument_list|()
decl_stmt|;
name|long
name|reqMem
init|=
name|reqRsrc
operator|.
name|getCapability
argument_list|()
operator|.
name|getMemorySize
argument_list|()
decl_stmt|;
name|long
name|totalMemoryToRelease
init|=
name|reqCont
operator|*
name|reqMem
decl_stmt|;
name|int
name|reqCores
init|=
name|reqRsrc
operator|.
name|getCapability
argument_list|()
operator|.
name|getVirtualCores
argument_list|()
decl_stmt|;
name|int
name|totalCoresToRelease
init|=
name|reqCont
operator|*
name|reqCores
decl_stmt|;
comment|// remove
if|if
condition|(
name|pendingPreemptionRam
operator|>
literal|0
condition|)
block|{
comment|// if goes negative we simply exit
name|totalMemoryToRelease
operator|-=
name|pendingPreemptionRam
expr_stmt|;
comment|// decrement pending resources if zero or negatve we will
comment|// ignore it while processing next PreemptionResourceRequest
name|pendingPreemptionRam
operator|-=
name|totalMemoryToRelease
expr_stmt|;
block|}
if|if
condition|(
name|pendingPreemptionCores
operator|>
literal|0
condition|)
block|{
name|totalCoresToRelease
operator|-=
name|pendingPreemptionCores
expr_stmt|;
name|pendingPreemptionCores
operator|-=
name|totalCoresToRelease
expr_stmt|;
block|}
comment|// reverse order of allocation (for now)
name|List
argument_list|<
name|Container
argument_list|>
name|listOfCont
init|=
name|ctxt
operator|.
name|getContainers
argument_list|(
name|TaskType
operator|.
name|REDUCE
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|listOfCont
argument_list|,
operator|new
name|Comparator
argument_list|<
name|Container
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
specifier|final
name|Container
name|o1
parameter_list|,
specifier|final
name|Container
name|o2
parameter_list|)
block|{
return|return
name|o2
operator|.
name|getId
argument_list|()
operator|.
name|compareTo
argument_list|(
name|o1
operator|.
name|getId
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
comment|// preempt reducers first
for|for
control|(
name|Container
name|cont
range|:
name|listOfCont
control|)
block|{
if|if
condition|(
name|totalMemoryToRelease
operator|<=
literal|0
operator|&&
name|totalCoresToRelease
operator|<=
literal|0
condition|)
block|{
break|break;
block|}
name|TaskAttemptId
name|reduceId
init|=
name|ctxt
operator|.
name|getTaskAttempt
argument_list|(
name|cont
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|cMem
init|=
operator|(
name|int
operator|)
name|cont
operator|.
name|getResource
argument_list|()
operator|.
name|getMemorySize
argument_list|()
decl_stmt|;
name|int
name|cCores
init|=
name|cont
operator|.
name|getResource
argument_list|()
operator|.
name|getVirtualCores
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|toBePreempted
operator|.
name|contains
argument_list|(
name|reduceId
argument_list|)
condition|)
block|{
name|totalMemoryToRelease
operator|-=
name|cMem
expr_stmt|;
name|totalCoresToRelease
operator|-=
name|cCores
expr_stmt|;
name|toBePreempted
operator|.
name|add
argument_list|(
name|reduceId
argument_list|)
expr_stmt|;
name|pendingFlexiblePreemptions
operator|.
name|put
argument_list|(
name|reduceId
argument_list|,
name|cont
operator|.
name|getResource
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"ResourceRequest:"
operator|+
name|reqRsrc
operator|+
literal|" satisfied preempting "
operator|+
name|reduceId
argument_list|)
expr_stmt|;
block|}
comment|// if map was preemptable we would do add them to toBePreempted here
block|}
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|handleFailedContainer (TaskAttemptId attemptID)
specifier|public
name|void
name|handleFailedContainer
parameter_list|(
name|TaskAttemptId
name|attemptID
parameter_list|)
block|{
name|toBePreempted
operator|.
name|remove
argument_list|(
name|attemptID
argument_list|)
expr_stmt|;
name|checkpoints
operator|.
name|remove
argument_list|(
name|attemptID
operator|.
name|getTaskId
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|handleCompletedContainer (TaskAttemptId attemptID)
specifier|public
name|void
name|handleCompletedContainer
parameter_list|(
name|TaskAttemptId
name|attemptID
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|" task completed:"
operator|+
name|attemptID
argument_list|)
expr_stmt|;
name|toBePreempted
operator|.
name|remove
argument_list|(
name|attemptID
argument_list|)
expr_stmt|;
name|pendingFlexiblePreemptions
operator|.
name|remove
argument_list|(
name|attemptID
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|isPreempted (TaskAttemptId yarnAttemptID)
specifier|public
name|boolean
name|isPreempted
parameter_list|(
name|TaskAttemptId
name|yarnAttemptID
parameter_list|)
block|{
if|if
condition|(
name|toBePreempted
operator|.
name|contains
argument_list|(
name|yarnAttemptID
argument_list|)
condition|)
block|{
name|updatePreemptionCounters
argument_list|(
name|yarnAttemptID
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|reportSuccessfulPreemption (TaskAttemptId taskAttemptID)
specifier|public
name|void
name|reportSuccessfulPreemption
parameter_list|(
name|TaskAttemptId
name|taskAttemptID
parameter_list|)
block|{
comment|// ignore
block|}
annotation|@
name|Override
DECL|method|getCheckpointID (TaskId taskId)
specifier|public
name|TaskCheckpointID
name|getCheckpointID
parameter_list|(
name|TaskId
name|taskId
parameter_list|)
block|{
return|return
name|checkpoints
operator|.
name|get
argument_list|(
name|taskId
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setCheckpointID (TaskId taskId, TaskCheckpointID cid)
specifier|public
name|void
name|setCheckpointID
parameter_list|(
name|TaskId
name|taskId
parameter_list|,
name|TaskCheckpointID
name|cid
parameter_list|)
block|{
name|checkpoints
operator|.
name|put
argument_list|(
name|taskId
argument_list|,
name|cid
argument_list|)
expr_stmt|;
if|if
condition|(
name|cid
operator|!=
literal|null
condition|)
block|{
name|updateCheckpointCounters
argument_list|(
name|taskId
argument_list|,
name|cid
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|}
argument_list|)
DECL|method|updateCheckpointCounters (TaskId taskId, TaskCheckpointID cid)
specifier|private
name|void
name|updateCheckpointCounters
parameter_list|(
name|TaskId
name|taskId
parameter_list|,
name|TaskCheckpointID
name|cid
parameter_list|)
block|{
name|JobCounterUpdateEvent
name|jce
init|=
operator|new
name|JobCounterUpdateEvent
argument_list|(
name|taskId
operator|.
name|getJobId
argument_list|()
argument_list|)
decl_stmt|;
name|jce
operator|.
name|addCounterUpdate
argument_list|(
name|JobCounter
operator|.
name|CHECKPOINTS
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|eventHandler
operator|.
name|handle
argument_list|(
name|jce
argument_list|)
expr_stmt|;
name|jce
operator|=
operator|new
name|JobCounterUpdateEvent
argument_list|(
name|taskId
operator|.
name|getJobId
argument_list|()
argument_list|)
expr_stmt|;
name|jce
operator|.
name|addCounterUpdate
argument_list|(
name|JobCounter
operator|.
name|CHECKPOINT_BYTES
argument_list|,
name|cid
operator|.
name|getCheckpointBytes
argument_list|()
argument_list|)
expr_stmt|;
name|eventHandler
operator|.
name|handle
argument_list|(
name|jce
argument_list|)
expr_stmt|;
name|jce
operator|=
operator|new
name|JobCounterUpdateEvent
argument_list|(
name|taskId
operator|.
name|getJobId
argument_list|()
argument_list|)
expr_stmt|;
name|jce
operator|.
name|addCounterUpdate
argument_list|(
name|JobCounter
operator|.
name|CHECKPOINT_TIME
argument_list|,
name|cid
operator|.
name|getCheckpointTime
argument_list|()
argument_list|)
expr_stmt|;
name|eventHandler
operator|.
name|handle
argument_list|(
name|jce
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|}
argument_list|)
DECL|method|updatePreemptionCounters (TaskAttemptId yarnAttemptID)
specifier|private
name|void
name|updatePreemptionCounters
parameter_list|(
name|TaskAttemptId
name|yarnAttemptID
parameter_list|)
block|{
if|if
condition|(
operator|!
name|countedPreemptions
operator|.
name|contains
argument_list|(
name|yarnAttemptID
argument_list|)
condition|)
block|{
name|countedPreemptions
operator|.
name|add
argument_list|(
name|yarnAttemptID
argument_list|)
expr_stmt|;
name|JobCounterUpdateEvent
name|jce
init|=
operator|new
name|JobCounterUpdateEvent
argument_list|(
name|yarnAttemptID
operator|.
name|getTaskId
argument_list|()
operator|.
name|getJobId
argument_list|()
argument_list|)
decl_stmt|;
name|jce
operator|.
name|addCounterUpdate
argument_list|(
name|JobCounter
operator|.
name|TASKS_REQ_PREEMPT
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|eventHandler
operator|.
name|handle
argument_list|(
name|jce
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

