begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.common.volume
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|volume
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
name|collect
operator|.
name|Sets
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
name|FutureCallback
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
name|Futures
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
name|ListenableFuture
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
name|datanode
operator|.
name|checker
operator|.
name|VolumeCheckResult
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
name|DiskChecker
operator|.
name|DiskErrorException
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
name|Timer
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
name|hdfs
operator|.
name|server
operator|.
name|datanode
operator|.
name|DataNode
operator|.
name|MAX_VOLUME_FAILURE_TOLERATED_LIMIT
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
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nullable
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
name|Collections
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
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Optional
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
name|CountDownLatch
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
name|ExecutionException
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
name|Executors
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
name|AtomicLong
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
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_DISK_CHECK_MIN_GAP_KEY
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
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_DISK_CHECK_TIMEOUT_DEFAULT
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
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_DISK_CHECK_TIMEOUT_KEY
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
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_FAILED_VOLUMES_TOLERATED_DEFAULT
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
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_FAILED_VOLUMES_TOLERATED_KEY
import|;
end_import

begin_comment
comment|/**  * A class that encapsulates running disk checks against each HDDS volume and  * allows retrieving a list of failed volumes.  */
end_comment

begin_class
DECL|class|HddsVolumeChecker
specifier|public
class|class
name|HddsVolumeChecker
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
name|HddsVolumeChecker
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|delegateChecker
specifier|private
name|AsyncChecker
argument_list|<
name|Boolean
argument_list|,
name|VolumeCheckResult
argument_list|>
name|delegateChecker
decl_stmt|;
DECL|field|numVolumeChecks
specifier|private
specifier|final
name|AtomicLong
name|numVolumeChecks
init|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|field|numAllVolumeChecks
specifier|private
specifier|final
name|AtomicLong
name|numAllVolumeChecks
init|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|field|numSkippedChecks
specifier|private
specifier|final
name|AtomicLong
name|numSkippedChecks
init|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|/**    * Max allowed time for a disk check in milliseconds. If the check    * doesn't complete within this time we declare the disk as dead.    */
DECL|field|maxAllowedTimeForCheckMs
specifier|private
specifier|final
name|long
name|maxAllowedTimeForCheckMs
decl_stmt|;
comment|/**    * Minimum time between two successive disk checks of a volume.    */
DECL|field|minDiskCheckGapMs
specifier|private
specifier|final
name|long
name|minDiskCheckGapMs
decl_stmt|;
comment|/**    * Timestamp of the last check of all volumes.    */
DECL|field|lastAllVolumesCheck
specifier|private
name|long
name|lastAllVolumesCheck
decl_stmt|;
DECL|field|timer
specifier|private
specifier|final
name|Timer
name|timer
decl_stmt|;
DECL|field|checkVolumeResultHandlerExecutorService
specifier|private
specifier|final
name|ExecutorService
name|checkVolumeResultHandlerExecutorService
decl_stmt|;
comment|/**    * @param conf Configuration object.    * @param timer {@link Timer} object used for throttling checks.    */
DECL|method|HddsVolumeChecker (Configuration conf, Timer timer)
specifier|public
name|HddsVolumeChecker
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|Timer
name|timer
parameter_list|)
throws|throws
name|DiskErrorException
block|{
name|maxAllowedTimeForCheckMs
operator|=
name|conf
operator|.
name|getTimeDuration
argument_list|(
name|DFS_DATANODE_DISK_CHECK_TIMEOUT_KEY
argument_list|,
name|DFS_DATANODE_DISK_CHECK_TIMEOUT_DEFAULT
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
if|if
condition|(
name|maxAllowedTimeForCheckMs
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|DiskErrorException
argument_list|(
literal|"Invalid value configured for "
operator|+
name|DFS_DATANODE_DISK_CHECK_TIMEOUT_KEY
operator|+
literal|" - "
operator|+
name|maxAllowedTimeForCheckMs
operator|+
literal|" (should be> 0)"
argument_list|)
throw|;
block|}
name|this
operator|.
name|timer
operator|=
name|timer
expr_stmt|;
comment|/**      * Maximum number of volume failures that can be tolerated without      * declaring a fatal error.      */
name|int
name|maxVolumeFailuresTolerated
init|=
name|conf
operator|.
name|getInt
argument_list|(
name|DFS_DATANODE_FAILED_VOLUMES_TOLERATED_KEY
argument_list|,
name|DFS_DATANODE_FAILED_VOLUMES_TOLERATED_DEFAULT
argument_list|)
decl_stmt|;
name|minDiskCheckGapMs
operator|=
name|conf
operator|.
name|getTimeDuration
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_DISK_CHECK_MIN_GAP_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_DISK_CHECK_MIN_GAP_DEFAULT
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
if|if
condition|(
name|minDiskCheckGapMs
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|DiskErrorException
argument_list|(
literal|"Invalid value configured for "
operator|+
name|DFS_DATANODE_DISK_CHECK_MIN_GAP_KEY
operator|+
literal|" - "
operator|+
name|minDiskCheckGapMs
operator|+
literal|" (should be>= 0)"
argument_list|)
throw|;
block|}
name|long
name|diskCheckTimeout
init|=
name|conf
operator|.
name|getTimeDuration
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_DISK_CHECK_TIMEOUT_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_DISK_CHECK_TIMEOUT_DEFAULT
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
if|if
condition|(
name|diskCheckTimeout
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|DiskErrorException
argument_list|(
literal|"Invalid value configured for "
operator|+
name|DFS_DATANODE_DISK_CHECK_TIMEOUT_KEY
operator|+
literal|" - "
operator|+
name|diskCheckTimeout
operator|+
literal|" (should be>= 0)"
argument_list|)
throw|;
block|}
name|lastAllVolumesCheck
operator|=
name|timer
operator|.
name|monotonicNow
argument_list|()
operator|-
name|minDiskCheckGapMs
expr_stmt|;
if|if
condition|(
name|maxVolumeFailuresTolerated
operator|<
name|MAX_VOLUME_FAILURE_TOLERATED_LIMIT
condition|)
block|{
throw|throw
operator|new
name|DiskErrorException
argument_list|(
literal|"Invalid value configured for "
operator|+
name|DFS_DATANODE_FAILED_VOLUMES_TOLERATED_KEY
operator|+
literal|" - "
operator|+
name|maxVolumeFailuresTolerated
operator|+
literal|" "
operator|+
name|DataNode
operator|.
name|MAX_VOLUME_FAILURES_TOLERATED_MSG
argument_list|)
throw|;
block|}
name|delegateChecker
operator|=
operator|new
name|ThrottledAsyncChecker
argument_list|<>
argument_list|(
name|timer
argument_list|,
name|minDiskCheckGapMs
argument_list|,
name|diskCheckTimeout
argument_list|,
name|Executors
operator|.
name|newCachedThreadPool
argument_list|(
operator|new
name|ThreadFactoryBuilder
argument_list|()
operator|.
name|setNameFormat
argument_list|(
literal|"DataNode DiskChecker thread %d"
argument_list|)
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|checkVolumeResultHandlerExecutorService
operator|=
name|Executors
operator|.
name|newCachedThreadPool
argument_list|(
operator|new
name|ThreadFactoryBuilder
argument_list|()
operator|.
name|setNameFormat
argument_list|(
literal|"VolumeCheck ResultHandler thread %d"
argument_list|)
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Run checks against all HDDS volumes.    *    * This check may be performed at service startup and subsequently at    * regular intervals to detect and handle failed volumes.    *    * @param volumes - Set of volumes to be checked. This set must be immutable    *                for the duration of the check else the results will be    *                unexpected.    *    * @return set of failed volumes.    */
DECL|method|checkAllVolumes (Collection<HddsVolume> volumes)
specifier|public
name|Set
argument_list|<
name|HddsVolume
argument_list|>
name|checkAllVolumes
parameter_list|(
name|Collection
argument_list|<
name|HddsVolume
argument_list|>
name|volumes
parameter_list|)
throws|throws
name|InterruptedException
block|{
specifier|final
name|long
name|gap
init|=
name|timer
operator|.
name|monotonicNow
argument_list|()
operator|-
name|lastAllVolumesCheck
decl_stmt|;
if|if
condition|(
name|gap
operator|<
name|minDiskCheckGapMs
condition|)
block|{
name|numSkippedChecks
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"Skipped checking all volumes, time since last check {} is less "
operator|+
literal|"than the minimum gap between checks ({} ms)."
argument_list|,
name|gap
argument_list|,
name|minDiskCheckGapMs
argument_list|)
expr_stmt|;
block|}
return|return
name|Collections
operator|.
name|emptySet
argument_list|()
return|;
block|}
name|lastAllVolumesCheck
operator|=
name|timer
operator|.
name|monotonicNow
argument_list|()
expr_stmt|;
specifier|final
name|Set
argument_list|<
name|HddsVolume
argument_list|>
name|healthyVolumes
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|HddsVolume
argument_list|>
name|failedVolumes
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|HddsVolume
argument_list|>
name|allVolumes
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|AtomicLong
name|numVolumes
init|=
operator|new
name|AtomicLong
argument_list|(
name|volumes
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|CountDownLatch
name|latch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
for|for
control|(
name|HddsVolume
name|v
range|:
name|volumes
control|)
block|{
name|Optional
argument_list|<
name|ListenableFuture
argument_list|<
name|VolumeCheckResult
argument_list|>
argument_list|>
name|olf
init|=
name|delegateChecker
operator|.
name|schedule
argument_list|(
name|v
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Scheduled health check for volume {}"
argument_list|,
name|v
argument_list|)
expr_stmt|;
if|if
condition|(
name|olf
operator|.
name|isPresent
argument_list|()
condition|)
block|{
name|allVolumes
operator|.
name|add
argument_list|(
name|v
argument_list|)
expr_stmt|;
name|Futures
operator|.
name|addCallback
argument_list|(
name|olf
operator|.
name|get
argument_list|()
argument_list|,
operator|new
name|ResultHandler
argument_list|(
name|v
argument_list|,
name|healthyVolumes
argument_list|,
name|failedVolumes
argument_list|,
name|numVolumes
argument_list|,
parameter_list|(
name|ignored1
parameter_list|,
name|ignored2
parameter_list|)
lambda|->
name|latch
operator|.
name|countDown
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|numVolumes
operator|.
name|decrementAndGet
argument_list|()
operator|==
literal|0
condition|)
block|{
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|// Wait until our timeout elapses, after which we give up on
comment|// the remaining volumes.
if|if
condition|(
operator|!
name|latch
operator|.
name|await
argument_list|(
name|maxAllowedTimeForCheckMs
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"checkAllVolumes timed out after {} ms"
operator|+
name|maxAllowedTimeForCheckMs
argument_list|)
expr_stmt|;
block|}
name|numAllVolumeChecks
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
comment|// All volumes that have not been detected as healthy should be
comment|// considered failed. This is a superset of 'failedVolumes'.
comment|//
comment|// Make a copy under the mutex as Sets.difference() returns a view
comment|// of a potentially changing set.
return|return
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|Sets
operator|.
name|difference
argument_list|(
name|allVolumes
argument_list|,
name|healthyVolumes
argument_list|)
argument_list|)
return|;
block|}
block|}
comment|/**    * A callback interface that is supplied the result of running an    * async disk check on multiple volumes.    */
DECL|interface|Callback
specifier|public
interface|interface
name|Callback
block|{
comment|/**      * @param healthyVolumes set of volumes that passed disk checks.      * @param failedVolumes set of volumes that failed disk checks.      */
DECL|method|call (Set<HddsVolume> healthyVolumes, Set<HddsVolume> failedVolumes)
name|void
name|call
parameter_list|(
name|Set
argument_list|<
name|HddsVolume
argument_list|>
name|healthyVolumes
parameter_list|,
name|Set
argument_list|<
name|HddsVolume
argument_list|>
name|failedVolumes
parameter_list|)
function_decl|;
block|}
comment|/**    * Check a single volume asynchronously, returning a {@link ListenableFuture}    * that can be used to retrieve the final result.    *    * If the volume cannot be referenced then it is already closed and    * cannot be checked. No error is propagated to the callback.    *    * @param volume the volume that is to be checked.    * @param callback callback to be invoked when the volume check completes.    * @return true if the check was scheduled and the callback will be invoked.    *         false otherwise.    */
DECL|method|checkVolume (final HddsVolume volume, Callback callback)
specifier|public
name|boolean
name|checkVolume
parameter_list|(
specifier|final
name|HddsVolume
name|volume
parameter_list|,
name|Callback
name|callback
parameter_list|)
block|{
if|if
condition|(
name|volume
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Cannot schedule check on null volume"
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
name|Optional
argument_list|<
name|ListenableFuture
argument_list|<
name|VolumeCheckResult
argument_list|>
argument_list|>
name|olf
init|=
name|delegateChecker
operator|.
name|schedule
argument_list|(
name|volume
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|olf
operator|.
name|isPresent
argument_list|()
condition|)
block|{
name|numVolumeChecks
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|Futures
operator|.
name|addCallback
argument_list|(
name|olf
operator|.
name|get
argument_list|()
argument_list|,
operator|new
name|ResultHandler
argument_list|(
name|volume
argument_list|,
operator|new
name|HashSet
argument_list|<>
argument_list|()
argument_list|,
operator|new
name|HashSet
argument_list|<>
argument_list|()
argument_list|,
operator|new
name|AtomicLong
argument_list|(
literal|1
argument_list|)
argument_list|,
name|callback
argument_list|)
argument_list|,
name|checkVolumeResultHandlerExecutorService
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
comment|/**    * A callback to process the results of checking a volume.    */
DECL|class|ResultHandler
specifier|private
class|class
name|ResultHandler
implements|implements
name|FutureCallback
argument_list|<
name|VolumeCheckResult
argument_list|>
block|{
DECL|field|volume
specifier|private
specifier|final
name|HddsVolume
name|volume
decl_stmt|;
DECL|field|failedVolumes
specifier|private
specifier|final
name|Set
argument_list|<
name|HddsVolume
argument_list|>
name|failedVolumes
decl_stmt|;
DECL|field|healthyVolumes
specifier|private
specifier|final
name|Set
argument_list|<
name|HddsVolume
argument_list|>
name|healthyVolumes
decl_stmt|;
DECL|field|volumeCounter
specifier|private
specifier|final
name|AtomicLong
name|volumeCounter
decl_stmt|;
annotation|@
name|Nullable
DECL|field|callback
specifier|private
specifier|final
name|Callback
name|callback
decl_stmt|;
comment|/**      *      * @param healthyVolumes set of healthy volumes. If the disk check is      *                       successful, add the volume here.      * @param failedVolumes set of failed volumes. If the disk check fails,      *                      add the volume here.      * @param volumeCounter volumeCounter used to trigger callback invocation.      * @param callback invoked when the volumeCounter reaches 0.      */
DECL|method|ResultHandler (HddsVolume volume, Set<HddsVolume> healthyVolumes, Set<HddsVolume> failedVolumes, AtomicLong volumeCounter, @Nullable Callback callback)
name|ResultHandler
parameter_list|(
name|HddsVolume
name|volume
parameter_list|,
name|Set
argument_list|<
name|HddsVolume
argument_list|>
name|healthyVolumes
parameter_list|,
name|Set
argument_list|<
name|HddsVolume
argument_list|>
name|failedVolumes
parameter_list|,
name|AtomicLong
name|volumeCounter
parameter_list|,
annotation|@
name|Nullable
name|Callback
name|callback
parameter_list|)
block|{
name|this
operator|.
name|volume
operator|=
name|volume
expr_stmt|;
name|this
operator|.
name|healthyVolumes
operator|=
name|healthyVolumes
expr_stmt|;
name|this
operator|.
name|failedVolumes
operator|=
name|failedVolumes
expr_stmt|;
name|this
operator|.
name|volumeCounter
operator|=
name|volumeCounter
expr_stmt|;
name|this
operator|.
name|callback
operator|=
name|callback
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onSuccess (@onnull VolumeCheckResult result)
specifier|public
name|void
name|onSuccess
parameter_list|(
annotation|@
name|Nonnull
name|VolumeCheckResult
name|result
parameter_list|)
block|{
switch|switch
condition|(
name|result
condition|)
block|{
case|case
name|HEALTHY
case|:
case|case
name|DEGRADED
case|:
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Volume {} is {}."
argument_list|,
name|volume
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
name|markHealthy
argument_list|()
expr_stmt|;
break|break;
case|case
name|FAILED
case|:
name|LOG
operator|.
name|warn
argument_list|(
literal|"Volume {} detected as being unhealthy"
argument_list|,
name|volume
argument_list|)
expr_stmt|;
name|markFailed
argument_list|()
expr_stmt|;
break|break;
default|default:
name|LOG
operator|.
name|error
argument_list|(
literal|"Unexpected health check result {} for volume {}"
argument_list|,
name|result
argument_list|,
name|volume
argument_list|)
expr_stmt|;
name|markHealthy
argument_list|()
expr_stmt|;
break|break;
block|}
name|cleanup
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onFailure (@onnull Throwable t)
specifier|public
name|void
name|onFailure
parameter_list|(
annotation|@
name|Nonnull
name|Throwable
name|t
parameter_list|)
block|{
name|Throwable
name|exception
init|=
operator|(
name|t
operator|instanceof
name|ExecutionException
operator|)
condition|?
name|t
operator|.
name|getCause
argument_list|()
else|:
name|t
decl_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"Exception running disk checks against volume "
operator|+
name|volume
argument_list|,
name|exception
argument_list|)
expr_stmt|;
name|markFailed
argument_list|()
expr_stmt|;
name|cleanup
argument_list|()
expr_stmt|;
block|}
DECL|method|markHealthy ()
specifier|private
name|void
name|markHealthy
parameter_list|()
block|{
synchronized|synchronized
init|(
name|HddsVolumeChecker
operator|.
name|this
init|)
block|{
name|healthyVolumes
operator|.
name|add
argument_list|(
name|volume
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|markFailed ()
specifier|private
name|void
name|markFailed
parameter_list|()
block|{
synchronized|synchronized
init|(
name|HddsVolumeChecker
operator|.
name|this
init|)
block|{
name|failedVolumes
operator|.
name|add
argument_list|(
name|volume
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|cleanup ()
specifier|private
name|void
name|cleanup
parameter_list|()
block|{
name|invokeCallback
argument_list|()
expr_stmt|;
block|}
DECL|method|invokeCallback ()
specifier|private
name|void
name|invokeCallback
parameter_list|()
block|{
try|try
block|{
specifier|final
name|long
name|remaining
init|=
name|volumeCounter
operator|.
name|decrementAndGet
argument_list|()
decl_stmt|;
if|if
condition|(
name|callback
operator|!=
literal|null
operator|&&
name|remaining
operator|==
literal|0
condition|)
block|{
name|callback
operator|.
name|call
argument_list|(
name|healthyVolumes
argument_list|,
name|failedVolumes
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// Propagating this exception is unlikely to be helpful.
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unexpected exception"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Shutdown the checker and its associated ExecutorService.    *    * See {@link ExecutorService#awaitTermination} for the interpretation    * of the parameters.    */
DECL|method|shutdownAndWait (int gracePeriod, TimeUnit timeUnit)
name|void
name|shutdownAndWait
parameter_list|(
name|int
name|gracePeriod
parameter_list|,
name|TimeUnit
name|timeUnit
parameter_list|)
block|{
try|try
block|{
name|delegateChecker
operator|.
name|shutdownAndWait
argument_list|(
name|gracePeriod
argument_list|,
name|timeUnit
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"{} interrupted during shutdown."
argument_list|,
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * This method is for testing only.    *    * @param testDelegate    */
annotation|@
name|VisibleForTesting
DECL|method|setDelegateChecker ( AsyncChecker<Boolean, VolumeCheckResult> testDelegate)
name|void
name|setDelegateChecker
parameter_list|(
name|AsyncChecker
argument_list|<
name|Boolean
argument_list|,
name|VolumeCheckResult
argument_list|>
name|testDelegate
parameter_list|)
block|{
name|delegateChecker
operator|=
name|testDelegate
expr_stmt|;
block|}
comment|/**    * Return the number of {@link #checkVolume} invocations.    */
DECL|method|getNumVolumeChecks ()
specifier|public
name|long
name|getNumVolumeChecks
parameter_list|()
block|{
return|return
name|numVolumeChecks
operator|.
name|get
argument_list|()
return|;
block|}
comment|/**    * Return the number of {@link #checkAllVolumes} invocations.    */
DECL|method|getNumAllVolumeChecks ()
specifier|public
name|long
name|getNumAllVolumeChecks
parameter_list|()
block|{
return|return
name|numAllVolumeChecks
operator|.
name|get
argument_list|()
return|;
block|}
comment|/**    * Return the number of checks skipped because the minimum gap since the    * last check had not elapsed.    */
DECL|method|getNumSkippedChecks ()
specifier|public
name|long
name|getNumSkippedChecks
parameter_list|()
block|{
return|return
name|numSkippedChecks
operator|.
name|get
argument_list|()
return|;
block|}
block|}
end_class

end_unit

