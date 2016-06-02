begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode.erasurecode
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|datanode
operator|.
name|erasurecode
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
name|classification
operator|.
name|InterfaceAudience
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
name|conf
operator|.
name|Configuration
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
name|DFSConfigKeys
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
name|server
operator|.
name|datanode
operator|.
name|DataNode
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
name|server
operator|.
name|protocol
operator|.
name|BlockECReconstructionCommand
operator|.
name|BlockECReconstructionInfo
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
name|Daemon
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
name|java
operator|.
name|util
operator|.
name|Collection
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
name|LinkedBlockingQueue
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
name|SynchronousQueue
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
name|ThreadPoolExecutor
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

begin_comment
comment|/**  * ErasureCodingWorker handles the erasure coding reconstruction work commands.  * These commands would be issued from Namenode as part of Datanode's heart beat  * response. BPOfferService delegates the work to this class for handling EC  * commands.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|ErasureCodingWorker
specifier|public
specifier|final
class|class
name|ErasureCodingWorker
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|DataNode
operator|.
name|LOG
decl_stmt|;
DECL|field|datanode
specifier|private
specifier|final
name|DataNode
name|datanode
decl_stmt|;
DECL|field|conf
specifier|private
specifier|final
name|Configuration
name|conf
decl_stmt|;
DECL|field|stripedReconstructionPool
specifier|private
name|ThreadPoolExecutor
name|stripedReconstructionPool
decl_stmt|;
DECL|field|stripedReadPool
specifier|private
name|ThreadPoolExecutor
name|stripedReadPool
decl_stmt|;
DECL|method|ErasureCodingWorker (Configuration conf, DataNode datanode)
specifier|public
name|ErasureCodingWorker
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|DataNode
name|datanode
parameter_list|)
block|{
name|this
operator|.
name|datanode
operator|=
name|datanode
expr_stmt|;
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
name|initializeStripedReadThreadPool
argument_list|(
name|conf
operator|.
name|getInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DN_EC_RECONSTRUCTION_STRIPED_READ_THREADS_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_DN_EC_RECONSTRUCTION_STRIPED_READ_THREADS_DEFAULT
argument_list|)
argument_list|)
expr_stmt|;
name|initializeStripedBlkReconstructionThreadPool
argument_list|(
name|conf
operator|.
name|getInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DN_EC_RECONSTRUCTION_STRIPED_BLK_THREADS_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_DN_EC_RECONSTRUCTION_STRIPED_BLK_THREADS_DEFAULT
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|initializeStripedReadThreadPool (int num)
specifier|private
name|void
name|initializeStripedReadThreadPool
parameter_list|(
name|int
name|num
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Using striped reads; pool threads={}"
argument_list|,
name|num
argument_list|)
expr_stmt|;
name|stripedReadPool
operator|=
operator|new
name|ThreadPoolExecutor
argument_list|(
literal|1
argument_list|,
name|num
argument_list|,
literal|60
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|,
operator|new
name|SynchronousQueue
argument_list|<
name|Runnable
argument_list|>
argument_list|()
argument_list|,
operator|new
name|Daemon
operator|.
name|DaemonFactory
argument_list|()
block|{
specifier|private
specifier|final
name|AtomicInteger
name|threadIndex
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|public
name|Thread
name|newThread
parameter_list|(
name|Runnable
name|r
parameter_list|)
block|{
name|Thread
name|t
init|=
name|super
operator|.
name|newThread
argument_list|(
name|r
argument_list|)
decl_stmt|;
name|t
operator|.
name|setName
argument_list|(
literal|"stripedRead-"
operator|+
name|threadIndex
operator|.
name|getAndIncrement
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|t
return|;
block|}
block|}
argument_list|,
operator|new
name|ThreadPoolExecutor
operator|.
name|CallerRunsPolicy
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|rejectedExecution
parameter_list|(
name|Runnable
name|runnable
parameter_list|,
name|ThreadPoolExecutor
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Execution for striped reading rejected, "
operator|+
literal|"Executing in current thread"
argument_list|)
expr_stmt|;
comment|// will run in the current thread
name|super
operator|.
name|rejectedExecution
argument_list|(
name|runnable
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|stripedReadPool
operator|.
name|allowCoreThreadTimeOut
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|initializeStripedBlkReconstructionThreadPool (int num)
specifier|private
name|void
name|initializeStripedBlkReconstructionThreadPool
parameter_list|(
name|int
name|num
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Using striped block reconstruction; pool threads={}"
argument_list|,
name|num
argument_list|)
expr_stmt|;
name|stripedReconstructionPool
operator|=
operator|new
name|ThreadPoolExecutor
argument_list|(
literal|2
argument_list|,
name|num
argument_list|,
literal|60
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|,
operator|new
name|LinkedBlockingQueue
argument_list|<
name|Runnable
argument_list|>
argument_list|()
argument_list|,
operator|new
name|Daemon
operator|.
name|DaemonFactory
argument_list|()
block|{
specifier|private
specifier|final
name|AtomicInteger
name|threadIdx
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|public
name|Thread
name|newThread
parameter_list|(
name|Runnable
name|r
parameter_list|)
block|{
name|Thread
name|t
init|=
name|super
operator|.
name|newThread
argument_list|(
name|r
argument_list|)
decl_stmt|;
name|t
operator|.
name|setName
argument_list|(
literal|"stripedBlockReconstruction-"
operator|+
name|threadIdx
operator|.
name|getAndIncrement
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|t
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|stripedReconstructionPool
operator|.
name|allowCoreThreadTimeOut
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * Handles the Erasure Coding reconstruction work commands.    *    * @param ecTasks BlockECReconstructionInfo    *    */
DECL|method|processErasureCodingTasks ( Collection<BlockECReconstructionInfo> ecTasks)
specifier|public
name|void
name|processErasureCodingTasks
parameter_list|(
name|Collection
argument_list|<
name|BlockECReconstructionInfo
argument_list|>
name|ecTasks
parameter_list|)
block|{
for|for
control|(
name|BlockECReconstructionInfo
name|reconInfo
range|:
name|ecTasks
control|)
block|{
try|try
block|{
name|StripedReconstructionInfo
name|stripedReconInfo
init|=
operator|new
name|StripedReconstructionInfo
argument_list|(
name|reconInfo
operator|.
name|getExtendedBlock
argument_list|()
argument_list|,
name|reconInfo
operator|.
name|getErasureCodingPolicy
argument_list|()
argument_list|,
name|reconInfo
operator|.
name|getLiveBlockIndices
argument_list|()
argument_list|,
name|reconInfo
operator|.
name|getSourceDnInfos
argument_list|()
argument_list|,
name|reconInfo
operator|.
name|getTargetDnInfos
argument_list|()
argument_list|,
name|reconInfo
operator|.
name|getTargetStorageTypes
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|StripedBlockReconstructor
name|task
init|=
operator|new
name|StripedBlockReconstructor
argument_list|(
name|this
argument_list|,
name|stripedReconInfo
argument_list|)
decl_stmt|;
if|if
condition|(
name|task
operator|.
name|hasValidTargets
argument_list|()
condition|)
block|{
name|stripedReconstructionPool
operator|.
name|submit
argument_list|(
name|task
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"No missing internal block. Skip reconstruction for task:{}"
argument_list|,
name|reconInfo
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to reconstruct striped block {}"
argument_list|,
name|reconInfo
operator|.
name|getExtendedBlock
argument_list|()
operator|.
name|getLocalBlock
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|getDatanode ()
name|DataNode
name|getDatanode
parameter_list|()
block|{
return|return
name|datanode
return|;
block|}
DECL|method|getConf ()
name|Configuration
name|getConf
parameter_list|()
block|{
return|return
name|conf
return|;
block|}
DECL|method|getStripedReadPool ()
name|ThreadPoolExecutor
name|getStripedReadPool
parameter_list|()
block|{
return|return
name|stripedReadPool
return|;
block|}
block|}
end_class

end_unit

