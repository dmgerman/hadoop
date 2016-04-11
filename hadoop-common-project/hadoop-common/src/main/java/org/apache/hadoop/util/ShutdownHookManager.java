begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
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
name|commons
operator|.
name|logging
operator|.
name|Log
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
name|logging
operator|.
name|LogFactory
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
name|Comparator
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
name|Future
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
name|TimeoutException
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

begin_comment
comment|/**  * The<code>ShutdownHookManager</code> enables running shutdownHook  * in a deterministic order, higher priority first.  *<p/>  * The JVM runs ShutdownHooks in a non-deterministic order or in parallel.  * This class registers a single JVM shutdownHook and run all the  * shutdownHooks registered to it (to this class) in order based on their  * priority.  */
end_comment

begin_class
DECL|class|ShutdownHookManager
specifier|public
class|class
name|ShutdownHookManager
block|{
DECL|field|MGR
specifier|private
specifier|static
specifier|final
name|ShutdownHookManager
name|MGR
init|=
operator|new
name|ShutdownHookManager
argument_list|()
decl_stmt|;
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|ShutdownHookManager
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|TIMEOUT_DEFAULT
specifier|private
specifier|static
specifier|final
name|long
name|TIMEOUT_DEFAULT
init|=
literal|10
decl_stmt|;
DECL|field|TIME_UNIT_DEFAULT
specifier|private
specifier|static
specifier|final
name|TimeUnit
name|TIME_UNIT_DEFAULT
init|=
name|TimeUnit
operator|.
name|SECONDS
decl_stmt|;
DECL|field|EXECUTOR
specifier|private
specifier|static
specifier|final
name|ExecutorService
name|EXECUTOR
init|=
name|HadoopExecutors
operator|.
name|newSingleThreadExecutor
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
name|build
argument_list|()
argument_list|)
decl_stmt|;
static|static
block|{
try|try
block|{
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|addShutdownHook
argument_list|(
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|MGR
operator|.
name|shutdownInProgress
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
for|for
control|(
name|HookEntry
name|entry
range|:
name|MGR
operator|.
name|getShutdownHooksInOrder
argument_list|()
control|)
block|{
name|Future
argument_list|<
name|?
argument_list|>
name|future
init|=
name|EXECUTOR
operator|.
name|submit
argument_list|(
name|entry
operator|.
name|getHook
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|future
operator|.
name|get
argument_list|(
name|entry
operator|.
name|getTimeout
argument_list|()
argument_list|,
name|entry
operator|.
name|getTimeUnit
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TimeoutException
name|ex
parameter_list|)
block|{
name|future
operator|.
name|cancel
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"ShutdownHook '"
operator|+
name|entry
operator|.
name|getHook
argument_list|()
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"' timeout, "
operator|+
name|ex
operator|.
name|toString
argument_list|()
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"ShutdownHook '"
operator|+
name|entry
operator|.
name|getHook
argument_list|()
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"' failed, "
operator|+
name|ex
operator|.
name|toString
argument_list|()
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
try|try
block|{
name|EXECUTOR
operator|.
name|shutdown
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|EXECUTOR
operator|.
name|awaitTermination
argument_list|(
name|TIMEOUT_DEFAULT
argument_list|,
name|TIME_UNIT_DEFAULT
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"ShutdownHookManger shutdown forcefully."
argument_list|)
expr_stmt|;
name|EXECUTOR
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"ShutdownHookManger complete shutdown."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"ShutdownHookManger interrupted while waiting for "
operator|+
literal|"termination."
argument_list|,
name|ex
argument_list|)
expr_stmt|;
name|EXECUTOR
operator|.
name|shutdownNow
argument_list|()
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
block|}
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|ex
parameter_list|)
block|{
comment|// JVM is being shut down. Ignore
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to add the ShutdownHook"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Return<code>ShutdownHookManager</code> singleton.    *    * @return<code>ShutdownHookManager</code> singleton.    */
DECL|method|get ()
specifier|public
specifier|static
name|ShutdownHookManager
name|get
parameter_list|()
block|{
return|return
name|MGR
return|;
block|}
comment|/**    * Private structure to store ShutdownHook, its priority and timeout    * settings.    */
DECL|class|HookEntry
specifier|static
class|class
name|HookEntry
block|{
DECL|field|hook
specifier|private
specifier|final
name|Runnable
name|hook
decl_stmt|;
DECL|field|priority
specifier|private
specifier|final
name|int
name|priority
decl_stmt|;
DECL|field|timeout
specifier|private
specifier|final
name|long
name|timeout
decl_stmt|;
DECL|field|unit
specifier|private
specifier|final
name|TimeUnit
name|unit
decl_stmt|;
DECL|method|HookEntry (Runnable hook, int priority)
name|HookEntry
parameter_list|(
name|Runnable
name|hook
parameter_list|,
name|int
name|priority
parameter_list|)
block|{
name|this
argument_list|(
name|hook
argument_list|,
name|priority
argument_list|,
name|TIMEOUT_DEFAULT
argument_list|,
name|TIME_UNIT_DEFAULT
argument_list|)
expr_stmt|;
block|}
DECL|method|HookEntry (Runnable hook, int priority, long timeout, TimeUnit unit)
name|HookEntry
parameter_list|(
name|Runnable
name|hook
parameter_list|,
name|int
name|priority
parameter_list|,
name|long
name|timeout
parameter_list|,
name|TimeUnit
name|unit
parameter_list|)
block|{
name|this
operator|.
name|hook
operator|=
name|hook
expr_stmt|;
name|this
operator|.
name|priority
operator|=
name|priority
expr_stmt|;
name|this
operator|.
name|timeout
operator|=
name|timeout
expr_stmt|;
name|this
operator|.
name|unit
operator|=
name|unit
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|hook
operator|.
name|hashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|equals (Object obj)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
name|boolean
name|eq
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|obj
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|obj
operator|instanceof
name|HookEntry
condition|)
block|{
name|eq
operator|=
operator|(
name|hook
operator|==
operator|(
operator|(
name|HookEntry
operator|)
name|obj
operator|)
operator|.
name|hook
operator|)
expr_stmt|;
block|}
block|}
return|return
name|eq
return|;
block|}
DECL|method|getHook ()
name|Runnable
name|getHook
parameter_list|()
block|{
return|return
name|hook
return|;
block|}
DECL|method|getPriority ()
name|int
name|getPriority
parameter_list|()
block|{
return|return
name|priority
return|;
block|}
DECL|method|getTimeout ()
name|long
name|getTimeout
parameter_list|()
block|{
return|return
name|timeout
return|;
block|}
DECL|method|getTimeUnit ()
name|TimeUnit
name|getTimeUnit
parameter_list|()
block|{
return|return
name|unit
return|;
block|}
block|}
DECL|field|hooks
specifier|private
specifier|final
name|Set
argument_list|<
name|HookEntry
argument_list|>
name|hooks
init|=
name|Collections
operator|.
name|synchronizedSet
argument_list|(
operator|new
name|HashSet
argument_list|<
name|HookEntry
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|shutdownInProgress
specifier|private
name|AtomicBoolean
name|shutdownInProgress
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
comment|//private to constructor to ensure singularity
DECL|method|ShutdownHookManager ()
specifier|private
name|ShutdownHookManager
parameter_list|()
block|{   }
comment|/**    * Returns the list of shutdownHooks in order of execution,    * Highest priority first.    *    * @return the list of shutdownHooks in order of execution.    */
DECL|method|getShutdownHooksInOrder ()
name|List
argument_list|<
name|HookEntry
argument_list|>
name|getShutdownHooksInOrder
parameter_list|()
block|{
name|List
argument_list|<
name|HookEntry
argument_list|>
name|list
decl_stmt|;
synchronized|synchronized
init|(
name|MGR
operator|.
name|hooks
init|)
block|{
name|list
operator|=
operator|new
name|ArrayList
argument_list|<
name|HookEntry
argument_list|>
argument_list|(
name|MGR
operator|.
name|hooks
argument_list|)
expr_stmt|;
block|}
name|Collections
operator|.
name|sort
argument_list|(
name|list
argument_list|,
operator|new
name|Comparator
argument_list|<
name|HookEntry
argument_list|>
argument_list|()
block|{
comment|//reversing comparison so highest priority hooks are first
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|HookEntry
name|o1
parameter_list|,
name|HookEntry
name|o2
parameter_list|)
block|{
return|return
name|o2
operator|.
name|priority
operator|-
name|o1
operator|.
name|priority
return|;
block|}
block|}
argument_list|)
expr_stmt|;
return|return
name|list
return|;
block|}
comment|/**    * Adds a shutdownHook with a priority, the higher the priority    * the earlier will run. ShutdownHooks with same priority run    * in a non-deterministic order.    *    * @param shutdownHook shutdownHook<code>Runnable</code>    * @param priority priority of the shutdownHook.    */
DECL|method|addShutdownHook (Runnable shutdownHook, int priority)
specifier|public
name|void
name|addShutdownHook
parameter_list|(
name|Runnable
name|shutdownHook
parameter_list|,
name|int
name|priority
parameter_list|)
block|{
if|if
condition|(
name|shutdownHook
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"shutdownHook cannot be NULL"
argument_list|)
throw|;
block|}
if|if
condition|(
name|shutdownInProgress
operator|.
name|get
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Shutdown in progress, cannot add a "
operator|+
literal|"shutdownHook"
argument_list|)
throw|;
block|}
name|hooks
operator|.
name|add
argument_list|(
operator|new
name|HookEntry
argument_list|(
name|shutdownHook
argument_list|,
name|priority
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    *    * Adds a shutdownHook with a priority and timeout the higher the priority    * the earlier will run. ShutdownHooks with same priority run    * in a non-deterministic order. The shutdown hook will be terminated if it    * has not been finished in the specified period of time.    *    * @param shutdownHook shutdownHook<code>Runnable</code>    * @param priority priority of the shutdownHook    * @param timeout timeout of the shutdownHook    * @param unit unit of the timeout<code>TimeUnit</code>    */
DECL|method|addShutdownHook (Runnable shutdownHook, int priority, long timeout, TimeUnit unit)
specifier|public
name|void
name|addShutdownHook
parameter_list|(
name|Runnable
name|shutdownHook
parameter_list|,
name|int
name|priority
parameter_list|,
name|long
name|timeout
parameter_list|,
name|TimeUnit
name|unit
parameter_list|)
block|{
if|if
condition|(
name|shutdownHook
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"shutdownHook cannot be NULL"
argument_list|)
throw|;
block|}
if|if
condition|(
name|shutdownInProgress
operator|.
name|get
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Shutdown in progress, cannot add a "
operator|+
literal|"shutdownHook"
argument_list|)
throw|;
block|}
name|hooks
operator|.
name|add
argument_list|(
operator|new
name|HookEntry
argument_list|(
name|shutdownHook
argument_list|,
name|priority
argument_list|,
name|timeout
argument_list|,
name|unit
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Removes a shutdownHook.    *    * @param shutdownHook shutdownHook to remove.    * @return TRUE if the shutdownHook was registered and removed,    * FALSE otherwise.    */
DECL|method|removeShutdownHook (Runnable shutdownHook)
specifier|public
name|boolean
name|removeShutdownHook
parameter_list|(
name|Runnable
name|shutdownHook
parameter_list|)
block|{
if|if
condition|(
name|shutdownInProgress
operator|.
name|get
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Shutdown in progress, cannot remove a "
operator|+
literal|"shutdownHook"
argument_list|)
throw|;
block|}
return|return
name|hooks
operator|.
name|remove
argument_list|(
operator|new
name|HookEntry
argument_list|(
name|shutdownHook
argument_list|,
literal|0
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Indicates if a shutdownHook is registered or not.    *    * @param shutdownHook shutdownHook to check if registered.    * @return TRUE/FALSE depending if the shutdownHook is is registered.    */
DECL|method|hasShutdownHook (Runnable shutdownHook)
specifier|public
name|boolean
name|hasShutdownHook
parameter_list|(
name|Runnable
name|shutdownHook
parameter_list|)
block|{
return|return
name|hooks
operator|.
name|contains
argument_list|(
operator|new
name|HookEntry
argument_list|(
name|shutdownHook
argument_list|,
literal|0
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Indicates if shutdown is in progress or not.    *     * @return TRUE if the shutdown is in progress, otherwise FALSE.    */
DECL|method|isShutdownInProgress ()
specifier|public
name|boolean
name|isShutdownInProgress
parameter_list|()
block|{
return|return
name|shutdownInProgress
operator|.
name|get
argument_list|()
return|;
block|}
comment|/**    * clear all registered shutdownHooks.    */
DECL|method|clearShutdownHooks ()
specifier|public
name|void
name|clearShutdownHooks
parameter_list|()
block|{
name|hooks
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

