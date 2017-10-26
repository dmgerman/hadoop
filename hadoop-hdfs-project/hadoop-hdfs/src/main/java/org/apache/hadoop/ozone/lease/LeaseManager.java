begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.lease
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|lease
package|;
end_package

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
name|Executors
import|;
end_import

begin_comment
comment|/**  * LeaseManager is someone who can provide you leases based on your  * requirement. If you want to return the lease back before it expires,  * you can give it back to Lease Manager. He is the one responsible for  * the lifecycle of leases. The resource for which lease is created  * should have proper {@code equals} method implementation, resource  * equality is checked while the lease is created.  *  * @param<T> Type of leases that this lease manager can create  */
end_comment

begin_class
DECL|class|LeaseManager
specifier|public
class|class
name|LeaseManager
parameter_list|<
name|T
parameter_list|>
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
name|LeaseManager
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|defaultTimeout
specifier|private
specifier|final
name|long
name|defaultTimeout
decl_stmt|;
DECL|field|activeLeases
specifier|private
name|Map
argument_list|<
name|T
argument_list|,
name|Lease
argument_list|<
name|T
argument_list|>
argument_list|>
name|activeLeases
decl_stmt|;
DECL|field|leaseMonitor
specifier|private
name|LeaseMonitor
name|leaseMonitor
decl_stmt|;
DECL|field|leaseMonitorThread
specifier|private
name|Thread
name|leaseMonitorThread
decl_stmt|;
DECL|field|isRunning
specifier|private
name|boolean
name|isRunning
decl_stmt|;
comment|/**    * Creates an instance of lease manager.    *    * @param defaultTimeout    *        Default timeout in milliseconds to be used for lease creation.    */
DECL|method|LeaseManager (long defaultTimeout)
specifier|public
name|LeaseManager
parameter_list|(
name|long
name|defaultTimeout
parameter_list|)
block|{
name|this
operator|.
name|defaultTimeout
operator|=
name|defaultTimeout
expr_stmt|;
block|}
comment|/**    * Starts the lease manager service.    */
DECL|method|start ()
specifier|public
name|void
name|start
parameter_list|()
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Starting LeaseManager service"
argument_list|)
expr_stmt|;
name|activeLeases
operator|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|leaseMonitor
operator|=
operator|new
name|LeaseMonitor
argument_list|()
expr_stmt|;
name|leaseMonitorThread
operator|=
operator|new
name|Thread
argument_list|(
name|leaseMonitor
argument_list|)
expr_stmt|;
name|leaseMonitorThread
operator|.
name|setName
argument_list|(
literal|"LeaseManager#LeaseMonitor"
argument_list|)
expr_stmt|;
name|leaseMonitorThread
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|leaseMonitorThread
operator|.
name|setUncaughtExceptionHandler
argument_list|(
parameter_list|(
name|thread
parameter_list|,
name|throwable
parameter_list|)
lambda|->
block|{
comment|// Let us just restart this thread after logging an error.
comment|// if this thread is not running we cannot handle Lease expiry.
name|LOG
operator|.
name|error
argument_list|(
literal|"LeaseMonitor thread encountered an error. Thread: {}"
argument_list|,
name|thread
operator|.
name|toString
argument_list|()
argument_list|,
name|throwable
argument_list|)
expr_stmt|;
name|leaseMonitorThread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Starting LeaseManager#LeaseMonitor Thread"
argument_list|)
expr_stmt|;
name|leaseMonitorThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|isRunning
operator|=
literal|true
expr_stmt|;
block|}
comment|/**    * Returns a lease for the specified resource with default timeout.    *    * @param resource    *        Resource for which lease has to be created    * @throws LeaseAlreadyExistException    *         If there is already a lease on the resource    */
DECL|method|acquire (T resource)
specifier|public
specifier|synchronized
name|Lease
argument_list|<
name|T
argument_list|>
name|acquire
parameter_list|(
name|T
name|resource
parameter_list|)
throws|throws
name|LeaseAlreadyExistException
block|{
return|return
name|acquire
argument_list|(
name|resource
argument_list|,
name|defaultTimeout
argument_list|)
return|;
block|}
comment|/**    * Returns a lease for the specified resource with the timeout provided.    *    * @param resource    *        Resource for which lease has to be created    * @param timeout    *        The timeout in milliseconds which has to be set on the lease    * @throws LeaseAlreadyExistException    *         If there is already a lease on the resource    */
DECL|method|acquire (T resource, long timeout)
specifier|public
specifier|synchronized
name|Lease
argument_list|<
name|T
argument_list|>
name|acquire
parameter_list|(
name|T
name|resource
parameter_list|,
name|long
name|timeout
parameter_list|)
throws|throws
name|LeaseAlreadyExistException
block|{
name|checkStatus
argument_list|()
expr_stmt|;
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
literal|"Acquiring lease on {} for {} milliseconds"
argument_list|,
name|resource
argument_list|,
name|timeout
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|activeLeases
operator|.
name|containsKey
argument_list|(
name|resource
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|LeaseAlreadyExistException
argument_list|(
literal|"Resource: "
operator|+
name|resource
argument_list|)
throw|;
block|}
name|Lease
argument_list|<
name|T
argument_list|>
name|lease
init|=
operator|new
name|Lease
argument_list|<>
argument_list|(
name|resource
argument_list|,
name|timeout
argument_list|)
decl_stmt|;
name|activeLeases
operator|.
name|put
argument_list|(
name|resource
argument_list|,
name|lease
argument_list|)
expr_stmt|;
name|leaseMonitorThread
operator|.
name|interrupt
argument_list|()
expr_stmt|;
return|return
name|lease
return|;
block|}
comment|/**    * Returns a lease associated with the specified resource.    *    * @param resource    *        Resource for which the lease has to be returned    * @throws LeaseNotFoundException    *         If there is no active lease on the resource    */
DECL|method|get (T resource)
specifier|public
name|Lease
argument_list|<
name|T
argument_list|>
name|get
parameter_list|(
name|T
name|resource
parameter_list|)
throws|throws
name|LeaseNotFoundException
block|{
name|checkStatus
argument_list|()
expr_stmt|;
name|Lease
argument_list|<
name|T
argument_list|>
name|lease
init|=
name|activeLeases
operator|.
name|get
argument_list|(
name|resource
argument_list|)
decl_stmt|;
if|if
condition|(
name|lease
operator|!=
literal|null
condition|)
block|{
return|return
name|lease
return|;
block|}
throw|throw
operator|new
name|LeaseNotFoundException
argument_list|(
literal|"Resource: "
operator|+
name|resource
argument_list|)
throw|;
block|}
comment|/**    * Releases the lease associated with the specified resource.    *    * @param resource    *        The for which the lease has to be released    * @throws LeaseNotFoundException    *         If there is no active lease on the resource    */
DECL|method|release (T resource)
specifier|public
specifier|synchronized
name|void
name|release
parameter_list|(
name|T
name|resource
parameter_list|)
throws|throws
name|LeaseNotFoundException
block|{
name|checkStatus
argument_list|()
expr_stmt|;
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
literal|"Releasing lease on {}"
argument_list|,
name|resource
argument_list|)
expr_stmt|;
block|}
name|Lease
argument_list|<
name|T
argument_list|>
name|lease
init|=
name|activeLeases
operator|.
name|remove
argument_list|(
name|resource
argument_list|)
decl_stmt|;
if|if
condition|(
name|lease
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|LeaseNotFoundException
argument_list|(
literal|"Resource: "
operator|+
name|resource
argument_list|)
throw|;
block|}
name|lease
operator|.
name|invalidate
argument_list|()
expr_stmt|;
block|}
comment|/**    * Shuts down the LeaseManager and releases the resources. All the active    * {@link Lease} will be released (callbacks on leases will not be    * executed).    */
DECL|method|shutdown ()
specifier|public
name|void
name|shutdown
parameter_list|()
block|{
name|checkStatus
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Shutting down LeaseManager service"
argument_list|)
expr_stmt|;
name|leaseMonitor
operator|.
name|disable
argument_list|()
expr_stmt|;
name|leaseMonitorThread
operator|.
name|interrupt
argument_list|()
expr_stmt|;
for|for
control|(
name|T
name|resource
range|:
name|activeLeases
operator|.
name|keySet
argument_list|()
control|)
block|{
try|try
block|{
name|release
argument_list|(
name|resource
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|LeaseNotFoundException
name|ex
parameter_list|)
block|{
comment|//Ignore the exception, someone might have released the lease
block|}
block|}
name|isRunning
operator|=
literal|false
expr_stmt|;
block|}
comment|/**    * Throws {@link LeaseManagerNotRunningException} if the service is not    * running.    */
DECL|method|checkStatus ()
specifier|private
name|void
name|checkStatus
parameter_list|()
block|{
if|if
condition|(
operator|!
name|isRunning
condition|)
block|{
throw|throw
operator|new
name|LeaseManagerNotRunningException
argument_list|(
literal|"LeaseManager not running."
argument_list|)
throw|;
block|}
block|}
comment|/**    * Monitors the leases and expires them based on the timeout, also    * responsible for executing the callbacks of expired leases.    */
DECL|class|LeaseMonitor
specifier|private
specifier|final
class|class
name|LeaseMonitor
implements|implements
name|Runnable
block|{
DECL|field|monitor
specifier|private
name|boolean
name|monitor
init|=
literal|true
decl_stmt|;
DECL|field|executorService
specifier|private
name|ExecutorService
name|executorService
decl_stmt|;
DECL|method|LeaseMonitor ()
specifier|private
name|LeaseMonitor
parameter_list|()
block|{
name|this
operator|.
name|monitor
operator|=
literal|true
expr_stmt|;
name|this
operator|.
name|executorService
operator|=
name|Executors
operator|.
name|newCachedThreadPool
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
while|while
condition|(
name|monitor
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"LeaseMonitor: checking for lease expiry"
argument_list|)
expr_stmt|;
name|long
name|sleepTime
init|=
name|Long
operator|.
name|MAX_VALUE
decl_stmt|;
for|for
control|(
name|T
name|resource
range|:
name|activeLeases
operator|.
name|keySet
argument_list|()
control|)
block|{
try|try
block|{
name|Lease
argument_list|<
name|T
argument_list|>
name|lease
init|=
name|get
argument_list|(
name|resource
argument_list|)
decl_stmt|;
name|long
name|remainingTime
init|=
name|lease
operator|.
name|getRemainingTime
argument_list|()
decl_stmt|;
if|if
condition|(
name|remainingTime
operator|<=
literal|0
condition|)
block|{
comment|//Lease has timed out
name|List
argument_list|<
name|Callable
argument_list|<
name|Void
argument_list|>
argument_list|>
name|leaseCallbacks
init|=
name|lease
operator|.
name|getCallbacks
argument_list|()
decl_stmt|;
name|release
argument_list|(
name|resource
argument_list|)
expr_stmt|;
name|executorService
operator|.
name|execute
argument_list|(
operator|new
name|LeaseCallbackExecutor
argument_list|(
name|resource
argument_list|,
name|leaseCallbacks
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sleepTime
operator|=
name|remainingTime
operator|>
name|sleepTime
condition|?
name|sleepTime
else|:
name|remainingTime
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|LeaseNotFoundException
decl||
name|LeaseExpiredException
name|ex
parameter_list|)
block|{
comment|//Ignore the exception, someone might have released the lease
block|}
block|}
try|try
block|{
if|if
condition|(
operator|!
name|Thread
operator|.
name|interrupted
argument_list|()
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|sleepTime
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ignored
parameter_list|)
block|{
comment|// This means a new lease is added to activeLeases.
block|}
block|}
block|}
comment|/**      * Disables lease monitor, next interrupt call on the thread      * will stop lease monitor.      */
DECL|method|disable ()
specifier|public
name|void
name|disable
parameter_list|()
block|{
name|monitor
operator|=
literal|false
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

