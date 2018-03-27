begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.scm.container.replication
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|scm
operator|.
name|container
operator|.
name|replication
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
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
name|hdsl
operator|.
name|protocol
operator|.
name|DatanodeDetails
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
name|ozone
operator|.
name|protocol
operator|.
name|commands
operator|.
name|SendContainerCommand
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
name|hdsl
operator|.
name|protocol
operator|.
name|proto
operator|.
name|HdslProtos
operator|.
name|NodeState
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
name|hdsl
operator|.
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|ContainerInfo
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
name|hdsl
operator|.
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|ContainerReportsRequestProto
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
name|ozone
operator|.
name|scm
operator|.
name|node
operator|.
name|NodeManager
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
name|ozone
operator|.
name|scm
operator|.
name|node
operator|.
name|NodePoolManager
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
name|util
operator|.
name|Time
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
name|UUID
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
name|ExecutorService
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
name|TimeUnit
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
name|function
operator|.
name|Predicate
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Uninterruptibles
operator|.
name|sleepUninterruptibly
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdsl
operator|.
name|protocol
operator|.
name|proto
operator|.
name|HdslProtos
operator|.
name|NodeState
operator|.
name|HEALTHY
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdsl
operator|.
name|protocol
operator|.
name|proto
operator|.
name|HdslProtos
operator|.
name|NodeState
operator|.
name|STALE
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdsl
operator|.
name|protocol
operator|.
name|proto
operator|.
name|HdslProtos
operator|.
name|NodeState
operator|.
name|INVALID
import|;
end_import

begin_comment
comment|/**  * These are pools that are actively checking for replication status of the  * containers.  */
end_comment

begin_class
DECL|class|InProgressPool
specifier|public
specifier|final
class|class
name|InProgressPool
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|InProgressPool
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|pool
specifier|private
specifier|final
name|PeriodicPool
name|pool
decl_stmt|;
DECL|field|nodeManager
specifier|private
specifier|final
name|NodeManager
name|nodeManager
decl_stmt|;
DECL|field|poolManager
specifier|private
specifier|final
name|NodePoolManager
name|poolManager
decl_stmt|;
DECL|field|executorService
specifier|private
specifier|final
name|ExecutorService
name|executorService
decl_stmt|;
DECL|field|containerCountMap
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|containerCountMap
decl_stmt|;
DECL|field|processedNodeSet
specifier|private
specifier|final
name|Map
argument_list|<
name|UUID
argument_list|,
name|Boolean
argument_list|>
name|processedNodeSet
decl_stmt|;
DECL|field|startTime
specifier|private
specifier|final
name|long
name|startTime
decl_stmt|;
DECL|field|status
specifier|private
name|ProgressStatus
name|status
decl_stmt|;
DECL|field|nodeCount
specifier|private
name|AtomicInteger
name|nodeCount
decl_stmt|;
DECL|field|nodeProcessed
specifier|private
name|AtomicInteger
name|nodeProcessed
decl_stmt|;
DECL|field|containerProcessedCount
specifier|private
name|AtomicInteger
name|containerProcessedCount
decl_stmt|;
DECL|field|maxWaitTime
specifier|private
name|long
name|maxWaitTime
decl_stmt|;
comment|/**    * Constructs an pool that is being processed.    *  @param maxWaitTime - Maximum wait time in milliseconds.    * @param pool - Pool that we are working against    * @param nodeManager - Nodemanager    * @param poolManager - pool manager    * @param executorService - Shared Executor service.    */
DECL|method|InProgressPool (long maxWaitTime, PeriodicPool pool, NodeManager nodeManager, NodePoolManager poolManager, ExecutorService executorService)
name|InProgressPool
parameter_list|(
name|long
name|maxWaitTime
parameter_list|,
name|PeriodicPool
name|pool
parameter_list|,
name|NodeManager
name|nodeManager
parameter_list|,
name|NodePoolManager
name|poolManager
parameter_list|,
name|ExecutorService
name|executorService
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|pool
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|nodeManager
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|poolManager
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|executorService
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|maxWaitTime
operator|>
literal|0
argument_list|)
expr_stmt|;
name|this
operator|.
name|pool
operator|=
name|pool
expr_stmt|;
name|this
operator|.
name|nodeManager
operator|=
name|nodeManager
expr_stmt|;
name|this
operator|.
name|poolManager
operator|=
name|poolManager
expr_stmt|;
name|this
operator|.
name|executorService
operator|=
name|executorService
expr_stmt|;
name|this
operator|.
name|containerCountMap
operator|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|this
operator|.
name|processedNodeSet
operator|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|this
operator|.
name|maxWaitTime
operator|=
name|maxWaitTime
expr_stmt|;
name|startTime
operator|=
name|Time
operator|.
name|monotonicNow
argument_list|()
expr_stmt|;
block|}
comment|/**    * Returns periodic pool.    *    * @return PeriodicPool    */
DECL|method|getPool ()
specifier|public
name|PeriodicPool
name|getPool
parameter_list|()
block|{
return|return
name|pool
return|;
block|}
comment|/**    * We are done if we have got reports from all nodes or we have    * done waiting for the specified time.    *    * @return true if we are done, false otherwise.    */
DECL|method|isDone ()
specifier|public
name|boolean
name|isDone
parameter_list|()
block|{
return|return
operator|(
name|nodeCount
operator|.
name|get
argument_list|()
operator|==
name|nodeProcessed
operator|.
name|get
argument_list|()
operator|)
operator|||
operator|(
name|this
operator|.
name|startTime
operator|+
name|this
operator|.
name|maxWaitTime
operator|)
operator|>
name|Time
operator|.
name|monotonicNow
argument_list|()
return|;
block|}
comment|/**    * Gets the number of containers processed.    *    * @return int    */
DECL|method|getContainerProcessedCount ()
specifier|public
name|int
name|getContainerProcessedCount
parameter_list|()
block|{
return|return
name|containerProcessedCount
operator|.
name|get
argument_list|()
return|;
block|}
comment|/**    * Returns the start time in milliseconds.    *    * @return - Start Time.    */
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
comment|/**    * Get the number of nodes in this pool.    *    * @return - node count    */
DECL|method|getNodeCount ()
specifier|public
name|int
name|getNodeCount
parameter_list|()
block|{
return|return
name|nodeCount
operator|.
name|get
argument_list|()
return|;
block|}
comment|/**    * Get the number of nodes that we have already processed container reports    * from.    *    * @return - Processed count.    */
DECL|method|getNodeProcessed ()
specifier|public
name|int
name|getNodeProcessed
parameter_list|()
block|{
return|return
name|nodeProcessed
operator|.
name|get
argument_list|()
return|;
block|}
comment|/**    * Returns the current status.    *    * @return Status    */
DECL|method|getStatus ()
specifier|public
name|ProgressStatus
name|getStatus
parameter_list|()
block|{
return|return
name|status
return|;
block|}
comment|/**    * Starts the reconciliation process for all the nodes in the pool.    */
DECL|method|startReconciliation ()
specifier|public
name|void
name|startReconciliation
parameter_list|()
block|{
name|List
argument_list|<
name|DatanodeDetails
argument_list|>
name|datanodeDetailsList
init|=
name|this
operator|.
name|poolManager
operator|.
name|getNodes
argument_list|(
name|pool
operator|.
name|getPoolName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|datanodeDetailsList
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Datanode list for {} is Empty. Pool with no nodes ? "
argument_list|,
name|pool
operator|.
name|getPoolName
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|status
operator|=
name|ProgressStatus
operator|.
name|Error
expr_stmt|;
return|return;
block|}
name|nodeProcessed
operator|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|containerProcessedCount
operator|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|nodeCount
operator|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|/*        Ask each datanode to send us commands.      */
name|SendContainerCommand
name|cmd
init|=
name|SendContainerCommand
operator|.
name|newBuilder
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
for|for
control|(
name|DatanodeDetails
name|dd
range|:
name|datanodeDetailsList
control|)
block|{
name|NodeState
name|currentState
init|=
name|getNodestate
argument_list|(
name|dd
argument_list|)
decl_stmt|;
if|if
condition|(
name|currentState
operator|==
name|HEALTHY
operator|||
name|currentState
operator|==
name|STALE
condition|)
block|{
name|nodeCount
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
comment|// Queue commands to all datanodes in this pool to send us container
comment|// report. Since we ignore dead nodes, it is possible that we would have
comment|// over replicated the container if the node comes back.
name|nodeManager
operator|.
name|addDatanodeCommand
argument_list|(
name|dd
operator|.
name|getUuid
argument_list|()
argument_list|,
name|cmd
argument_list|)
expr_stmt|;
block|}
block|}
name|this
operator|.
name|status
operator|=
name|ProgressStatus
operator|.
name|InProgress
expr_stmt|;
name|this
operator|.
name|getPool
argument_list|()
operator|.
name|setLastProcessedTime
argument_list|(
name|Time
operator|.
name|monotonicNow
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Gets the node state.    *    * @param datanode - datanode information.    * @return NodeState.    */
DECL|method|getNodestate (DatanodeDetails datanode)
specifier|private
name|NodeState
name|getNodestate
parameter_list|(
name|DatanodeDetails
name|datanode
parameter_list|)
block|{
name|NodeState
name|currentState
init|=
name|INVALID
decl_stmt|;
name|int
name|maxTry
init|=
literal|100
decl_stmt|;
comment|// We need to loop to make sure that we will retry if we get
comment|// node state unknown. This can lead to infinite loop if we send
comment|// in unknown node ID. So max try count is used to prevent it.
name|int
name|currentTry
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|currentState
operator|==
name|INVALID
operator|&&
name|currentTry
operator|<
name|maxTry
condition|)
block|{
comment|// Retry to make sure that we deal with the case of node state not
comment|// known.
name|currentState
operator|=
name|nodeManager
operator|.
name|getNodeState
argument_list|(
name|datanode
argument_list|)
expr_stmt|;
name|currentTry
operator|++
expr_stmt|;
if|if
condition|(
name|currentState
operator|==
name|INVALID
condition|)
block|{
comment|// Sleep to make sure that this is not a tight loop.
name|sleepUninterruptibly
argument_list|(
literal|100
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|currentState
operator|==
name|INVALID
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Not able to determine the state of Node: {}, Exceeded max "
operator|+
literal|"try and node manager returns INVALID state. This indicates we "
operator|+
literal|"are dealing with a node that we don't know about."
argument_list|,
name|datanode
argument_list|)
expr_stmt|;
block|}
return|return
name|currentState
return|;
block|}
comment|/**    * Queues a container Report for handling. This is done in a worker thread    * since decoding a container report might be compute intensive . We don't    * want to block since we have asked for bunch of container reports    * from a set of datanodes.    *    * @param containerReport - ContainerReport    */
DECL|method|handleContainerReport ( ContainerReportsRequestProto containerReport)
specifier|public
name|void
name|handleContainerReport
parameter_list|(
name|ContainerReportsRequestProto
name|containerReport
parameter_list|)
block|{
if|if
condition|(
name|status
operator|==
name|ProgressStatus
operator|.
name|InProgress
condition|)
block|{
name|executorService
operator|.
name|submit
argument_list|(
name|processContainerReport
argument_list|(
name|containerReport
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Cannot handle container report when the pool is in {} status."
argument_list|,
name|status
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|processContainerReport ( ContainerReportsRequestProto reports)
specifier|private
name|Runnable
name|processContainerReport
parameter_list|(
name|ContainerReportsRequestProto
name|reports
parameter_list|)
block|{
return|return
parameter_list|()
lambda|->
block|{
name|DatanodeDetails
name|datanodeDetails
init|=
name|DatanodeDetails
operator|.
name|getFromProtoBuf
argument_list|(
name|reports
operator|.
name|getDatanodeDetails
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|processedNodeSet
operator|.
name|computeIfAbsent
argument_list|(
name|datanodeDetails
operator|.
name|getUuid
argument_list|()
argument_list|,
parameter_list|(
lambda|k
argument_list|)
operator|->
literal|true
condition|)
block|)
block|{
name|nodeProcessed
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Total Nodes processed : {} Node Name: {} "
argument_list|,
name|nodeProcessed
argument_list|,
name|datanodeDetails
operator|.
name|getUuid
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|ContainerInfo
name|info
range|:
name|reports
operator|.
name|getReportsList
argument_list|()
control|)
block|{
name|containerProcessedCount
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Total Containers processed: {} Container Name: {}"
argument_list|,
name|containerProcessedCount
operator|.
name|get
argument_list|()
argument_list|,
name|info
operator|.
name|getContainerName
argument_list|()
argument_list|)
expr_stmt|;
comment|// Update the container map with count + 1 if the key exists or
comment|// update the map with 1. Since this is a concurrentMap the
comment|// computation and update is atomic.
name|containerCountMap
operator|.
name|merge
argument_list|(
name|info
operator|.
name|getContainerName
argument_list|()
argument_list|,
literal|1
argument_list|,
name|Integer
operator|::
name|sum
argument_list|)
expr_stmt|;
block|}
block|}
block|}
empty_stmt|;
block|}
end_class

begin_comment
comment|/**    * Filter the containers based on specific rules.    *    * @param predicate -- Predicate to filter by    * @return A list of map entries.    */
end_comment

begin_function
DECL|method|filterContainer ( Predicate<Map.Entry<String, Integer>> predicate)
specifier|public
name|List
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|>
name|filterContainer
parameter_list|(
name|Predicate
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|>
name|predicate
parameter_list|)
block|{
return|return
name|containerCountMap
operator|.
name|entrySet
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|predicate
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
return|;
block|}
end_function

begin_comment
comment|/**    * Used only for testing, calling this will abort container report    * processing. This is very dangerous call and should not be made by any users    */
end_comment

begin_function
annotation|@
name|VisibleForTesting
DECL|method|setDoneProcessing ()
specifier|public
name|void
name|setDoneProcessing
parameter_list|()
block|{
name|nodeProcessed
operator|.
name|set
argument_list|(
name|nodeCount
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/**    * Returns the pool name.    *    * @return Name of the pool.    */
end_comment

begin_function
DECL|method|getPoolName ()
name|String
name|getPoolName
parameter_list|()
block|{
return|return
name|pool
operator|.
name|getPoolName
argument_list|()
return|;
block|}
end_function

begin_function
DECL|method|finalizeReconciliation ()
specifier|public
name|void
name|finalizeReconciliation
parameter_list|()
block|{
name|status
operator|=
name|ProgressStatus
operator|.
name|Done
expr_stmt|;
comment|//TODO: Add finalizing logic. This is where actual reconciliation happens.
block|}
end_function

begin_comment
comment|/**    * Current status of the computing replication status.    */
end_comment

begin_enum
DECL|enum|ProgressStatus
specifier|public
enum|enum
name|ProgressStatus
block|{
DECL|enumConstant|InProgress
DECL|enumConstant|Done
DECL|enumConstant|Error
name|InProgress
block|,
name|Done
block|,
name|Error
block|}
end_enum

unit|}
end_unit

