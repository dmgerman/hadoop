begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode.checker
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
name|checker
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
name|base
operator|.
name|Optional
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
name|ListeningExecutorService
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
name|MoreExecutors
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
name|classification
operator|.
name|InterfaceStability
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
name|WeakHashMap
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
name|Callable
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
name|ScheduledExecutorService
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
name|ScheduledThreadPoolExecutor
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

begin_comment
comment|/**  * An implementation of {@link AsyncChecker} that skips checking recently  * checked objects. It will enforce at least {@link minMsBetweenChecks}  * milliseconds between two successive checks of any one object.  *  * It is assumed that the total number of Checkable objects in the system  * is small, (not more than a few dozen) since the checker uses O(Checkables)  * storage and also potentially O(Checkables) threads.  *  * {@link minMsBetweenChecks} should be configured reasonably  * by the caller to avoid spinning up too many threads frequently.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|ThrottledAsyncChecker
specifier|public
class|class
name|ThrottledAsyncChecker
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
implements|implements
name|AsyncChecker
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
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
name|ThrottledAsyncChecker
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|timer
specifier|private
specifier|final
name|Timer
name|timer
decl_stmt|;
comment|/**    * The ExecutorService used to schedule asynchronous checks.    */
DECL|field|executorService
specifier|private
specifier|final
name|ListeningExecutorService
name|executorService
decl_stmt|;
DECL|field|scheduledExecutorService
specifier|private
specifier|final
name|ScheduledExecutorService
name|scheduledExecutorService
decl_stmt|;
comment|/**    * The minimum gap in milliseconds between two successive checks    * of the same object. This is the throttle.    */
DECL|field|minMsBetweenChecks
specifier|private
specifier|final
name|long
name|minMsBetweenChecks
decl_stmt|;
DECL|field|diskCheckTimeout
specifier|private
specifier|final
name|long
name|diskCheckTimeout
decl_stmt|;
comment|/**    * Map of checks that are currently in progress. Protected by the object    * lock.    */
DECL|field|checksInProgress
specifier|private
specifier|final
name|Map
argument_list|<
name|Checkable
argument_list|,
name|ListenableFuture
argument_list|<
name|V
argument_list|>
argument_list|>
name|checksInProgress
decl_stmt|;
comment|/**    * Maps Checkable objects to a future that can be used to retrieve    * the results of the operation.    * Protected by the object lock.    */
DECL|field|completedChecks
specifier|private
specifier|final
name|Map
argument_list|<
name|Checkable
argument_list|,
name|LastCheckResult
argument_list|<
name|V
argument_list|>
argument_list|>
name|completedChecks
decl_stmt|;
DECL|method|ThrottledAsyncChecker (final Timer timer, final long minMsBetweenChecks, final long diskCheckTimeout, final ExecutorService executorService)
name|ThrottledAsyncChecker
parameter_list|(
specifier|final
name|Timer
name|timer
parameter_list|,
specifier|final
name|long
name|minMsBetweenChecks
parameter_list|,
specifier|final
name|long
name|diskCheckTimeout
parameter_list|,
specifier|final
name|ExecutorService
name|executorService
parameter_list|)
block|{
name|this
operator|.
name|timer
operator|=
name|timer
expr_stmt|;
name|this
operator|.
name|minMsBetweenChecks
operator|=
name|minMsBetweenChecks
expr_stmt|;
name|this
operator|.
name|diskCheckTimeout
operator|=
name|diskCheckTimeout
expr_stmt|;
name|this
operator|.
name|executorService
operator|=
name|MoreExecutors
operator|.
name|listeningDecorator
argument_list|(
name|executorService
argument_list|)
expr_stmt|;
name|this
operator|.
name|checksInProgress
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|this
operator|.
name|completedChecks
operator|=
operator|new
name|WeakHashMap
argument_list|<>
argument_list|()
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|diskCheckTimeout
operator|>
literal|0
condition|)
block|{
name|ScheduledThreadPoolExecutor
name|scheduledThreadPoolExecutor
init|=
operator|new
name|ScheduledThreadPoolExecutor
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|this
operator|.
name|scheduledExecutorService
operator|=
name|MoreExecutors
operator|.
name|getExitingScheduledExecutorService
argument_list|(
name|scheduledThreadPoolExecutor
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|scheduledExecutorService
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|/**    * See {@link AsyncChecker#schedule}    *    * If the object has been checked recently then the check will    * be skipped. Multiple concurrent checks for the same object    * will receive the same Future.    */
annotation|@
name|Override
DECL|method|schedule (Checkable<K, V> target, K context)
specifier|public
name|Optional
argument_list|<
name|ListenableFuture
argument_list|<
name|V
argument_list|>
argument_list|>
name|schedule
parameter_list|(
name|Checkable
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|target
parameter_list|,
name|K
name|context
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Scheduling a check for {}"
argument_list|,
name|target
argument_list|)
expr_stmt|;
if|if
condition|(
name|checksInProgress
operator|.
name|containsKey
argument_list|(
name|target
argument_list|)
condition|)
block|{
return|return
name|Optional
operator|.
name|absent
argument_list|()
return|;
block|}
if|if
condition|(
name|completedChecks
operator|.
name|containsKey
argument_list|(
name|target
argument_list|)
condition|)
block|{
specifier|final
name|LastCheckResult
argument_list|<
name|V
argument_list|>
name|result
init|=
name|completedChecks
operator|.
name|get
argument_list|(
name|target
argument_list|)
decl_stmt|;
specifier|final
name|long
name|msSinceLastCheck
init|=
name|timer
operator|.
name|monotonicNow
argument_list|()
operator|-
name|result
operator|.
name|completedAt
decl_stmt|;
if|if
condition|(
name|msSinceLastCheck
operator|<
name|minMsBetweenChecks
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Skipped checking {}. Time since last check {}ms "
operator|+
literal|"is less than the min gap {}ms."
argument_list|,
name|target
argument_list|,
name|msSinceLastCheck
argument_list|,
name|minMsBetweenChecks
argument_list|)
expr_stmt|;
return|return
name|Optional
operator|.
name|absent
argument_list|()
return|;
block|}
block|}
specifier|final
name|ListenableFuture
argument_list|<
name|V
argument_list|>
name|lfWithoutTimeout
init|=
name|executorService
operator|.
name|submit
argument_list|(
operator|new
name|Callable
argument_list|<
name|V
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|V
name|call
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|target
operator|.
name|check
argument_list|(
name|context
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
specifier|final
name|ListenableFuture
argument_list|<
name|V
argument_list|>
name|lf
decl_stmt|;
if|if
condition|(
name|diskCheckTimeout
operator|>
literal|0
condition|)
block|{
name|lf
operator|=
name|TimeoutFuture
operator|.
name|create
argument_list|(
name|lfWithoutTimeout
argument_list|,
name|diskCheckTimeout
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|,
name|scheduledExecutorService
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|lf
operator|=
name|lfWithoutTimeout
expr_stmt|;
block|}
name|checksInProgress
operator|.
name|put
argument_list|(
name|target
argument_list|,
name|lf
argument_list|)
expr_stmt|;
name|addResultCachingCallback
argument_list|(
name|target
argument_list|,
name|lf
argument_list|)
expr_stmt|;
return|return
name|Optional
operator|.
name|of
argument_list|(
name|lf
argument_list|)
return|;
block|}
comment|/**    * Register a callback to cache the result of a check.    * @param target    * @param lf    */
DECL|method|addResultCachingCallback ( Checkable<K, V> target, ListenableFuture<V> lf)
specifier|private
name|void
name|addResultCachingCallback
parameter_list|(
name|Checkable
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|target
parameter_list|,
name|ListenableFuture
argument_list|<
name|V
argument_list|>
name|lf
parameter_list|)
block|{
name|Futures
operator|.
name|addCallback
argument_list|(
name|lf
argument_list|,
operator|new
name|FutureCallback
argument_list|<
name|V
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onSuccess
parameter_list|(
annotation|@
name|Nullable
name|V
name|result
parameter_list|)
block|{
synchronized|synchronized
init|(
name|ThrottledAsyncChecker
operator|.
name|this
init|)
block|{
name|checksInProgress
operator|.
name|remove
argument_list|(
name|target
argument_list|)
expr_stmt|;
name|completedChecks
operator|.
name|put
argument_list|(
name|target
argument_list|,
operator|new
name|LastCheckResult
argument_list|<>
argument_list|(
name|result
argument_list|,
name|timer
operator|.
name|monotonicNow
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
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
synchronized|synchronized
init|(
name|ThrottledAsyncChecker
operator|.
name|this
init|)
block|{
name|checksInProgress
operator|.
name|remove
argument_list|(
name|target
argument_list|)
expr_stmt|;
name|completedChecks
operator|.
name|put
argument_list|(
name|target
argument_list|,
operator|new
name|LastCheckResult
argument_list|<>
argument_list|(
name|t
argument_list|,
name|timer
operator|.
name|monotonicNow
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * {@inheritDoc}.    *    * The results of in-progress checks are not useful during shutdown,    * so we optimize for faster shutdown by interrupt all actively    * executing checks.    */
annotation|@
name|Override
DECL|method|shutdownAndWait (long timeout, TimeUnit timeUnit)
specifier|public
name|void
name|shutdownAndWait
parameter_list|(
name|long
name|timeout
parameter_list|,
name|TimeUnit
name|timeUnit
parameter_list|)
throws|throws
name|InterruptedException
block|{
if|if
condition|(
name|scheduledExecutorService
operator|!=
literal|null
condition|)
block|{
name|scheduledExecutorService
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
name|scheduledExecutorService
operator|.
name|awaitTermination
argument_list|(
name|timeout
argument_list|,
name|timeUnit
argument_list|)
expr_stmt|;
block|}
name|executorService
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
name|executorService
operator|.
name|awaitTermination
argument_list|(
name|timeout
argument_list|,
name|timeUnit
argument_list|)
expr_stmt|;
block|}
comment|/**    * Status of running a check. It can either be a result or an    * exception, depending on whether the check completed or threw.    */
DECL|class|LastCheckResult
specifier|private
specifier|static
specifier|final
class|class
name|LastCheckResult
parameter_list|<
name|V
parameter_list|>
block|{
comment|/**      * Timestamp at which the check completed.      */
DECL|field|completedAt
specifier|private
specifier|final
name|long
name|completedAt
decl_stmt|;
comment|/**      * Result of running the check if it completed. null if it threw.      */
annotation|@
name|Nullable
DECL|field|result
specifier|private
specifier|final
name|V
name|result
decl_stmt|;
comment|/**      * Exception thrown by the check. null if it returned a result.      */
DECL|field|exception
specifier|private
specifier|final
name|Throwable
name|exception
decl_stmt|;
comment|// null on success.
comment|/**      * Initialize with a result.      * @param result      */
DECL|method|LastCheckResult (V result, long completedAt)
specifier|private
name|LastCheckResult
parameter_list|(
name|V
name|result
parameter_list|,
name|long
name|completedAt
parameter_list|)
block|{
name|this
operator|.
name|result
operator|=
name|result
expr_stmt|;
name|this
operator|.
name|exception
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|completedAt
operator|=
name|completedAt
expr_stmt|;
block|}
comment|/**      * Initialize with an exception.      * @param completedAt      * @param t      */
DECL|method|LastCheckResult (Throwable t, long completedAt)
specifier|private
name|LastCheckResult
parameter_list|(
name|Throwable
name|t
parameter_list|,
name|long
name|completedAt
parameter_list|)
block|{
name|this
operator|.
name|result
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|exception
operator|=
name|t
expr_stmt|;
name|this
operator|.
name|completedAt
operator|=
name|completedAt
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

