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
name|ThreadFactoryBuilder
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
name|hdfs
operator|.
name|protocol
operator|.
name|DatanodeID
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
name|OzoneConfiguration
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
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|ContainerReportsProto
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
name|exceptions
operator|.
name|SCMException
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
name|CommandQueue
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
name|apache
operator|.
name|hadoop
operator|.
name|util
operator|.
name|concurrent
operator|.
name|HadoopExecutors
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
name|io
operator|.
name|Closeable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|LinkedList
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
name|PriorityQueue
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
name|scm
operator|.
name|ScmConfigKeys
operator|.
name|OZONE_SCM_CONTAINER_REPORTS_WAIT_TIMEOUT_DEFAULT
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
name|scm
operator|.
name|ScmConfigKeys
operator|.
name|OZONE_SCM_CONTAINER_REPORTS_WAIT_TIMEOUT_SECONDS
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
name|scm
operator|.
name|ScmConfigKeys
operator|.
name|OZONE_SCM_CONTAINER_REPORT_PROCESSING_INTERVAL_DEFAULT
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
name|scm
operator|.
name|ScmConfigKeys
operator|.
name|OZONE_SCM_CONTAINER_REPORT_PROCESSING_INTERVAL_SECONDS
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
name|scm
operator|.
name|ScmConfigKeys
operator|.
name|OZONE_SCM_MAX_CONTAINER_REPORT_THREADS
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
name|scm
operator|.
name|ScmConfigKeys
operator|.
name|OZONE_SCM_MAX_CONTAINER_REPORT_THREADS_DEFAULT
import|;
end_import

begin_comment
comment|/**  * This class takes a set of container reports that belong to a pool and then  * computes the replication levels for each container.  */
end_comment

begin_class
DECL|class|ContainerReplicationManager
specifier|public
class|class
name|ContainerReplicationManager
implements|implements
name|Closeable
block|{
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
name|ContainerReplicationManager
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|poolManager
specifier|private
specifier|final
name|NodePoolManager
name|poolManager
decl_stmt|;
DECL|field|commandQueue
specifier|private
specifier|final
name|CommandQueue
name|commandQueue
decl_stmt|;
DECL|field|poolNames
specifier|private
specifier|final
name|HashSet
argument_list|<
name|String
argument_list|>
name|poolNames
decl_stmt|;
DECL|field|poolQueue
specifier|private
specifier|final
name|PriorityQueue
argument_list|<
name|PeriodicPool
argument_list|>
name|poolQueue
decl_stmt|;
DECL|field|nodeManager
specifier|private
specifier|final
name|NodeManager
name|nodeManager
decl_stmt|;
DECL|field|containerProcessingLag
specifier|private
specifier|final
name|int
name|containerProcessingLag
decl_stmt|;
DECL|field|runnable
specifier|private
specifier|final
name|AtomicBoolean
name|runnable
decl_stmt|;
DECL|field|executorService
specifier|private
specifier|final
name|ExecutorService
name|executorService
decl_stmt|;
DECL|field|maxPoolWait
specifier|private
specifier|final
name|int
name|maxPoolWait
decl_stmt|;
DECL|field|poolProcessCount
specifier|private
name|long
name|poolProcessCount
decl_stmt|;
DECL|field|inProgressPoolList
specifier|private
specifier|final
name|List
argument_list|<
name|InProgressPool
argument_list|>
name|inProgressPoolList
decl_stmt|;
DECL|field|threadFaultCount
specifier|private
specifier|final
name|AtomicInteger
name|threadFaultCount
decl_stmt|;
comment|/**    * Returns the number of times we have processed pools.    * @return long    */
DECL|method|getPoolProcessCount ()
specifier|public
name|long
name|getPoolProcessCount
parameter_list|()
block|{
return|return
name|poolProcessCount
return|;
block|}
comment|/**    * Constructs a class that computes Replication Levels.    *    * @param conf - OzoneConfiguration    * @param nodeManager - Node Manager    * @param poolManager - Pool Manager    * @param commandQueue - Datanodes Command Queue.    */
DECL|method|ContainerReplicationManager (OzoneConfiguration conf, NodeManager nodeManager, NodePoolManager poolManager, CommandQueue commandQueue)
specifier|public
name|ContainerReplicationManager
parameter_list|(
name|OzoneConfiguration
name|conf
parameter_list|,
name|NodeManager
name|nodeManager
parameter_list|,
name|NodePoolManager
name|poolManager
parameter_list|,
name|CommandQueue
name|commandQueue
parameter_list|)
block|{
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
name|commandQueue
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|nodeManager
argument_list|)
expr_stmt|;
name|this
operator|.
name|containerProcessingLag
operator|=
name|conf
operator|.
name|getInt
argument_list|(
name|OZONE_SCM_CONTAINER_REPORT_PROCESSING_INTERVAL_SECONDS
argument_list|,
name|OZONE_SCM_CONTAINER_REPORT_PROCESSING_INTERVAL_DEFAULT
argument_list|)
operator|*
literal|1000
expr_stmt|;
name|int
name|maxContainerReportThreads
init|=
name|conf
operator|.
name|getInt
argument_list|(
name|OZONE_SCM_MAX_CONTAINER_REPORT_THREADS
argument_list|,
name|OZONE_SCM_MAX_CONTAINER_REPORT_THREADS_DEFAULT
argument_list|)
decl_stmt|;
name|this
operator|.
name|maxPoolWait
operator|=
name|conf
operator|.
name|getInt
argument_list|(
name|OZONE_SCM_CONTAINER_REPORTS_WAIT_TIMEOUT_SECONDS
argument_list|,
name|OZONE_SCM_CONTAINER_REPORTS_WAIT_TIMEOUT_DEFAULT
argument_list|)
operator|*
literal|1000
expr_stmt|;
name|this
operator|.
name|poolManager
operator|=
name|poolManager
expr_stmt|;
name|this
operator|.
name|commandQueue
operator|=
name|commandQueue
expr_stmt|;
name|this
operator|.
name|nodeManager
operator|=
name|nodeManager
expr_stmt|;
name|this
operator|.
name|poolNames
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
expr_stmt|;
name|this
operator|.
name|poolQueue
operator|=
operator|new
name|PriorityQueue
argument_list|<>
argument_list|()
expr_stmt|;
name|runnable
operator|=
operator|new
name|AtomicBoolean
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|this
operator|.
name|threadFaultCount
operator|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|executorService
operator|=
name|HadoopExecutors
operator|.
name|newCachedThreadPool
argument_list|(
operator|new
name|ThreadFactoryBuilder
argument_list|()
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
operator|.
name|setNameFormat
argument_list|(
literal|"Container Reports Processing Thread - %d"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|,
name|maxContainerReportThreads
argument_list|)
expr_stmt|;
name|inProgressPoolList
operator|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
expr_stmt|;
name|initPoolProcessThread
argument_list|()
expr_stmt|;
block|}
comment|/**    * Returns the number of pools that are under process right now.    * @return  int - Number of pools that are in process.    */
DECL|method|getInProgressPoolCount ()
specifier|public
name|int
name|getInProgressPoolCount
parameter_list|()
block|{
return|return
name|inProgressPoolList
operator|.
name|size
argument_list|()
return|;
block|}
comment|/**    * Exits the background thread.    */
DECL|method|setExit ()
specifier|public
name|void
name|setExit
parameter_list|()
block|{
name|this
operator|.
name|runnable
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * Adds or removes pools from names that we need to process.    *    * There are two different cases that we need to process.    * The case where some pools are being added and some times we have to    * handle cases where pools are removed.    */
DECL|method|refreshPools ()
specifier|private
name|void
name|refreshPools
parameter_list|()
block|{
name|List
argument_list|<
name|String
argument_list|>
name|pools
init|=
name|this
operator|.
name|poolManager
operator|.
name|getNodePools
argument_list|()
decl_stmt|;
if|if
condition|(
name|pools
operator|!=
literal|null
condition|)
block|{
name|HashSet
argument_list|<
name|String
argument_list|>
name|removedPools
init|=
name|computePoolDifference
argument_list|(
name|this
operator|.
name|poolNames
argument_list|,
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|pools
argument_list|)
argument_list|)
decl_stmt|;
name|HashSet
argument_list|<
name|String
argument_list|>
name|addedPools
init|=
name|computePoolDifference
argument_list|(
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|pools
argument_list|)
argument_list|,
name|this
operator|.
name|poolNames
argument_list|)
decl_stmt|;
comment|// TODO: Support remove pool API in pool manager so that this code
comment|// path can be tested. This never happens in the current code base.
for|for
control|(
name|String
name|poolName
range|:
name|removedPools
control|)
block|{
for|for
control|(
name|PeriodicPool
name|periodicPool
range|:
name|poolQueue
control|)
block|{
if|if
condition|(
name|periodicPool
operator|.
name|getPoolName
argument_list|()
operator|.
name|compareTo
argument_list|(
name|poolName
argument_list|)
operator|==
literal|0
condition|)
block|{
name|poolQueue
operator|.
name|remove
argument_list|(
name|periodicPool
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// Remove the pool names that we have in the list.
name|this
operator|.
name|poolNames
operator|.
name|removeAll
argument_list|(
name|removedPools
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|poolName
range|:
name|addedPools
control|)
block|{
name|poolQueue
operator|.
name|add
argument_list|(
operator|new
name|PeriodicPool
argument_list|(
name|poolName
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Add to the pool names we are tracking.
name|poolNames
operator|.
name|addAll
argument_list|(
name|addedPools
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Handle the case where pools are added.    *    * @param newPools - New Pools list    * @param oldPool - oldPool List.    */
DECL|method|computePoolDifference (HashSet<String> newPools, Set<String> oldPool)
specifier|private
name|HashSet
argument_list|<
name|String
argument_list|>
name|computePoolDifference
parameter_list|(
name|HashSet
argument_list|<
name|String
argument_list|>
name|newPools
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|oldPool
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|newPools
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|oldPool
argument_list|)
expr_stmt|;
name|HashSet
argument_list|<
name|String
argument_list|>
name|newSet
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|newPools
argument_list|)
decl_stmt|;
name|newSet
operator|.
name|removeAll
argument_list|(
name|oldPool
argument_list|)
expr_stmt|;
return|return
name|newSet
return|;
block|}
DECL|method|initPoolProcessThread ()
specifier|private
name|void
name|initPoolProcessThread
parameter_list|()
block|{
comment|/*      * Task that runs to check if we need to start a pool processing job.      * if so we create a pool reconciliation job and find out of all the      * expected containers are on the nodes.      */
name|Runnable
name|processPools
init|=
parameter_list|()
lambda|->
block|{
while|while
condition|(
name|runnable
operator|.
name|get
argument_list|()
condition|)
block|{
comment|// Make sure that we don't have any new pools.
name|refreshPools
argument_list|()
expr_stmt|;
name|PeriodicPool
name|pool
init|=
name|poolQueue
operator|.
name|poll
argument_list|()
decl_stmt|;
if|if
condition|(
name|pool
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|pool
operator|.
name|getLastProcessedTime
argument_list|()
operator|+
name|this
operator|.
name|containerProcessingLag
operator|<
name|Time
operator|.
name|monotonicNow
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Adding pool {} to container processing queue"
argument_list|,
name|pool
operator|.
name|getPoolName
argument_list|()
argument_list|)
expr_stmt|;
name|InProgressPool
name|inProgressPool
init|=
operator|new
name|InProgressPool
argument_list|(
name|maxPoolWait
argument_list|,
name|pool
argument_list|,
name|this
operator|.
name|nodeManager
argument_list|,
name|this
operator|.
name|poolManager
argument_list|,
name|this
operator|.
name|commandQueue
argument_list|,
name|this
operator|.
name|executorService
argument_list|)
decl_stmt|;
name|inProgressPool
operator|.
name|startReconciliation
argument_list|()
expr_stmt|;
name|inProgressPoolList
operator|.
name|add
argument_list|(
name|inProgressPool
argument_list|)
expr_stmt|;
name|poolProcessCount
operator|++
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Not within the time window for processing: {}"
argument_list|,
name|pool
operator|.
name|getPoolName
argument_list|()
argument_list|)
expr_stmt|;
comment|// Put back this pool since we are not planning to process it.
name|poolQueue
operator|.
name|add
argument_list|(
name|pool
argument_list|)
expr_stmt|;
comment|// we might over sleep here, not a big deal.
name|sleepUninterruptibly
argument_list|(
name|this
operator|.
name|containerProcessingLag
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
block|}
block|}
name|sleepUninterruptibly
argument_list|(
name|this
operator|.
name|maxPoolWait
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
comment|// We will have only one thread for pool processing.
name|Thread
name|poolProcessThread
init|=
operator|new
name|Thread
argument_list|(
name|processPools
argument_list|)
decl_stmt|;
name|poolProcessThread
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|poolProcessThread
operator|.
name|setName
argument_list|(
literal|"Pool replica thread"
argument_list|)
expr_stmt|;
name|poolProcessThread
operator|.
name|setUncaughtExceptionHandler
argument_list|(
parameter_list|(
name|Thread
name|t
parameter_list|,
name|Throwable
name|e
parameter_list|)
lambda|->
block|{
comment|// Let us just restart this thread after logging a critical error.
comment|// if this thread is not running we cannot handle commands from SCM.
name|LOG
operator|.
name|error
argument_list|(
literal|"Critical Error : Pool replica thread encountered an "
operator|+
literal|"error. Thread: {} Error Count : {}"
argument_list|,
name|t
operator|.
name|toString
argument_list|()
argument_list|,
name|e
argument_list|,
name|threadFaultCount
operator|.
name|incrementAndGet
argument_list|()
argument_list|)
expr_stmt|;
name|poolProcessThread
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// TODO : Add a config to restrict how many times we will restart this
comment|// thread in a single session.
block|}
argument_list|)
expr_stmt|;
name|poolProcessThread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
comment|/**    * Adds a container report to appropriate inProgress Pool.    * @param containerReport  -- Container report for a specific container from    * a datanode.    */
DECL|method|handleContainerReport (ContainerReportsProto containerReport)
specifier|public
name|void
name|handleContainerReport
parameter_list|(
name|ContainerReportsProto
name|containerReport
parameter_list|)
block|{
name|String
name|poolName
init|=
literal|null
decl_stmt|;
name|DatanodeID
name|datanodeID
init|=
name|DatanodeID
operator|.
name|getFromProtoBuf
argument_list|(
name|containerReport
operator|.
name|getDatanodeID
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|poolName
operator|=
name|poolManager
operator|.
name|getNodePool
argument_list|(
name|datanodeID
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SCMException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Skipping processing container report from datanode {}, "
operator|+
literal|"cause: failed to get the corresponding node pool"
argument_list|,
name|datanodeID
operator|.
name|toString
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return;
block|}
for|for
control|(
name|InProgressPool
name|ppool
range|:
name|inProgressPoolList
control|)
block|{
if|if
condition|(
name|ppool
operator|.
name|getPoolName
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
name|poolName
argument_list|)
condition|)
block|{
name|ppool
operator|.
name|handleContainerReport
argument_list|(
name|containerReport
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
comment|// TODO: Decide if we can do anything else with this report.
name|LOG
operator|.
name|debug
argument_list|(
literal|"Discarding the container report for pool {}. That pool is not "
operator|+
literal|"currently in the pool reconciliation process. Container Name: {}"
argument_list|,
name|poolName
argument_list|,
name|containerReport
operator|.
name|getDatanodeID
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get in process pool list, used for testing.    * @return List of InProgressPool    */
annotation|@
name|VisibleForTesting
DECL|method|getInProcessPoolList ()
specifier|public
name|List
argument_list|<
name|InProgressPool
argument_list|>
name|getInProcessPoolList
parameter_list|()
block|{
return|return
name|inProgressPoolList
return|;
block|}
comment|/**    * Shutdown the Container Replication Manager.    * @throws IOException if an I/O error occurs    */
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|setExit
argument_list|()
expr_stmt|;
name|HadoopExecutors
operator|.
name|shutdown
argument_list|(
name|executorService
argument_list|,
name|LOG
argument_list|,
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

