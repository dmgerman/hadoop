begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.service
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|service
package|;
end_package

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
name|ArrayList
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
name|atomic
operator|.
name|AtomicBoolean
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
name|annotations
operator|.
name|VisibleForTesting
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
name|conf
operator|.
name|Configuration
import|;
end_import

begin_comment
comment|/**  * This is the base implementation class for YARN services.  */
end_comment

begin_class
DECL|class|AbstractService
specifier|public
specifier|abstract
class|class
name|AbstractService
implements|implements
name|Service
block|{
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
name|AbstractService
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Service name.    */
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
comment|/** service state */
DECL|field|stateModel
specifier|private
specifier|final
name|ServiceStateModel
name|stateModel
decl_stmt|;
comment|/**    * Service start time. Will be zero until the service is started.    */
DECL|field|startTime
specifier|private
name|long
name|startTime
decl_stmt|;
comment|/**    * The configuration. Will be null until the service is initialized.    */
DECL|field|config
specifier|private
specifier|volatile
name|Configuration
name|config
decl_stmt|;
comment|/**    * List of state change listeners; it is final to ensure    * that it will never be null.    */
DECL|field|listeners
specifier|private
specifier|final
name|ServiceOperations
operator|.
name|ServiceListeners
name|listeners
init|=
operator|new
name|ServiceOperations
operator|.
name|ServiceListeners
argument_list|()
decl_stmt|;
comment|/**    * Static listeners to all events across all services    */
DECL|field|globalListeners
specifier|private
specifier|static
name|ServiceOperations
operator|.
name|ServiceListeners
name|globalListeners
init|=
operator|new
name|ServiceOperations
operator|.
name|ServiceListeners
argument_list|()
decl_stmt|;
comment|/**    * The cause of any failure -will be null.    * if a service did not stop due to a failure.    */
DECL|field|failureCause
specifier|private
name|Exception
name|failureCause
decl_stmt|;
comment|/**    * the state in which the service was when it failed.    * Only valid when the service is stopped due to a failure    */
DECL|field|failureState
specifier|private
name|STATE
name|failureState
init|=
literal|null
decl_stmt|;
comment|/**    * object used to co-ordinate {@link #waitForServiceToStop(long)}    * across threads.    */
DECL|field|terminationNotification
specifier|private
specifier|final
name|AtomicBoolean
name|terminationNotification
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
comment|/**    * History of lifecycle transitions    */
DECL|field|lifecycleHistory
specifier|private
specifier|final
name|List
argument_list|<
name|LifecycleEvent
argument_list|>
name|lifecycleHistory
init|=
operator|new
name|ArrayList
argument_list|<
name|LifecycleEvent
argument_list|>
argument_list|(
literal|5
argument_list|)
decl_stmt|;
comment|/**    * Map of blocking dependencies    */
DECL|field|blockerMap
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|blockerMap
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|stateChangeLock
specifier|private
specifier|final
name|Object
name|stateChangeLock
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
comment|/**    * Construct the service.    * @param name service name    */
DECL|method|AbstractService (String name)
specifier|public
name|AbstractService
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|stateModel
operator|=
operator|new
name|ServiceStateModel
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getServiceState ()
specifier|public
specifier|final
name|STATE
name|getServiceState
parameter_list|()
block|{
return|return
name|stateModel
operator|.
name|getState
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getFailureCause ()
specifier|public
specifier|final
specifier|synchronized
name|Throwable
name|getFailureCause
parameter_list|()
block|{
return|return
name|failureCause
return|;
block|}
annotation|@
name|Override
DECL|method|getFailureState ()
specifier|public
specifier|synchronized
name|STATE
name|getFailureState
parameter_list|()
block|{
return|return
name|failureState
return|;
block|}
comment|/**    * Set the configuration for this service.    * This method is called during {@link #init(Configuration)}    * and should only be needed if for some reason a service implementation    * needs to override that initial setting -for example replacing    * it with a new subclass of {@link Configuration}    * @param conf new configuration.    */
DECL|method|setConfig (Configuration conf)
specifier|protected
name|void
name|setConfig
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|this
operator|.
name|config
operator|=
name|conf
expr_stmt|;
block|}
comment|/**    * {@inheritDoc}    * This invokes {@link #serviceInit}    * @param conf the configuration of the service. This must not be null    * @throws ServiceStateException if the configuration was null,    * the state change not permitted, or something else went wrong    */
annotation|@
name|Override
DECL|method|init (Configuration conf)
specifier|public
name|void
name|init
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
if|if
condition|(
name|conf
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ServiceStateException
argument_list|(
literal|"Cannot initialize service "
operator|+
name|getName
argument_list|()
operator|+
literal|": null configuration"
argument_list|)
throw|;
block|}
if|if
condition|(
name|isInState
argument_list|(
name|STATE
operator|.
name|INITED
argument_list|)
condition|)
block|{
return|return;
block|}
synchronized|synchronized
init|(
name|stateChangeLock
init|)
block|{
if|if
condition|(
name|enterState
argument_list|(
name|STATE
operator|.
name|INITED
argument_list|)
operator|!=
name|STATE
operator|.
name|INITED
condition|)
block|{
name|setConfig
argument_list|(
name|conf
argument_list|)
expr_stmt|;
try|try
block|{
name|serviceInit
argument_list|(
name|config
argument_list|)
expr_stmt|;
if|if
condition|(
name|isInState
argument_list|(
name|STATE
operator|.
name|INITED
argument_list|)
condition|)
block|{
comment|//if the service ended up here during init,
comment|//notify the listeners
name|notifyListeners
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|noteFailure
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|ServiceOperations
operator|.
name|stopQuietly
argument_list|(
name|LOG
argument_list|,
name|this
argument_list|)
expr_stmt|;
throw|throw
name|ServiceStateException
operator|.
name|convert
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
block|}
comment|/**    * {@inheritDoc}    * @throws ServiceStateException if the current service state does not permit    * this action    */
annotation|@
name|Override
DECL|method|start ()
specifier|public
name|void
name|start
parameter_list|()
block|{
if|if
condition|(
name|isInState
argument_list|(
name|STATE
operator|.
name|STARTED
argument_list|)
condition|)
block|{
return|return;
block|}
comment|//enter the started state
synchronized|synchronized
init|(
name|stateChangeLock
init|)
block|{
if|if
condition|(
name|stateModel
operator|.
name|enterState
argument_list|(
name|STATE
operator|.
name|STARTED
argument_list|)
operator|!=
name|STATE
operator|.
name|STARTED
condition|)
block|{
try|try
block|{
name|startTime
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
name|serviceStart
argument_list|()
expr_stmt|;
if|if
condition|(
name|isInState
argument_list|(
name|STATE
operator|.
name|STARTED
argument_list|)
condition|)
block|{
comment|//if the service started (and isn't now in a later state), notify
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
literal|"Service "
operator|+
name|getName
argument_list|()
operator|+
literal|" is started"
argument_list|)
expr_stmt|;
block|}
name|notifyListeners
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|noteFailure
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|ServiceOperations
operator|.
name|stopQuietly
argument_list|(
name|LOG
argument_list|,
name|this
argument_list|)
expr_stmt|;
throw|throw
name|ServiceStateException
operator|.
name|convert
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
block|}
comment|/**    * {@inheritDoc}    */
annotation|@
name|Override
DECL|method|stop ()
specifier|public
name|void
name|stop
parameter_list|()
block|{
if|if
condition|(
name|isInState
argument_list|(
name|STATE
operator|.
name|STOPPED
argument_list|)
condition|)
block|{
return|return;
block|}
synchronized|synchronized
init|(
name|stateChangeLock
init|)
block|{
if|if
condition|(
name|enterState
argument_list|(
name|STATE
operator|.
name|STOPPED
argument_list|)
operator|!=
name|STATE
operator|.
name|STOPPED
condition|)
block|{
try|try
block|{
name|serviceStop
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|//stop-time exceptions are logged if they are the first one,
name|noteFailure
argument_list|(
name|e
argument_list|)
expr_stmt|;
throw|throw
name|ServiceStateException
operator|.
name|convert
argument_list|(
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
comment|//report that the service has terminated
name|terminationNotification
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|terminationNotification
init|)
block|{
name|terminationNotification
operator|.
name|notifyAll
argument_list|()
expr_stmt|;
block|}
comment|//notify anything listening for events
name|notifyListeners
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
comment|//already stopped: note it
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
literal|"Ignoring re-entrant call to stop()"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/**    * Relay to {@link #stop()}    * @throws IOException    */
annotation|@
name|Override
DECL|method|close ()
specifier|public
specifier|final
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|stop
argument_list|()
expr_stmt|;
block|}
comment|/**    * Failure handling: record the exception    * that triggered it -if there was not one already.    * Services are free to call this themselves.    * @param exception the exception    */
DECL|method|noteFailure (Exception exception)
specifier|protected
specifier|final
name|void
name|noteFailure
parameter_list|(
name|Exception
name|exception
parameter_list|)
block|{
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
literal|"noteFailure "
operator|+
name|exception
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|exception
operator|==
literal|null
condition|)
block|{
comment|//make sure failure logic doesn't itself cause problems
return|return;
block|}
comment|//record the failure details, and log it
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
name|failureCause
operator|==
literal|null
condition|)
block|{
name|failureCause
operator|=
name|exception
expr_stmt|;
name|failureState
operator|=
name|getServiceState
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Service "
operator|+
name|getName
argument_list|()
operator|+
literal|" failed in state "
operator|+
name|failureState
operator|+
literal|"; cause: "
operator|+
name|exception
argument_list|,
name|exception
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|waitForServiceToStop (long timeout)
specifier|public
specifier|final
name|boolean
name|waitForServiceToStop
parameter_list|(
name|long
name|timeout
parameter_list|)
block|{
name|boolean
name|completed
init|=
name|terminationNotification
operator|.
name|get
argument_list|()
decl_stmt|;
while|while
condition|(
operator|!
name|completed
condition|)
block|{
try|try
block|{
synchronized|synchronized
init|(
name|terminationNotification
init|)
block|{
name|terminationNotification
operator|.
name|wait
argument_list|(
name|timeout
argument_list|)
expr_stmt|;
block|}
comment|// here there has been a timeout, the object has terminated,
comment|// or there has been a spurious wakeup (which we ignore)
name|completed
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
comment|// interrupted; have another look at the flag
name|completed
operator|=
name|terminationNotification
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|terminationNotification
operator|.
name|get
argument_list|()
return|;
block|}
comment|/* ===================================================================== */
comment|/* Override Points */
comment|/* ===================================================================== */
comment|/**    * All initialization code needed by a service.    *    * This method will only ever be called once during the lifecycle of    * a specific service instance.    *    * Implementations do not need to be synchronized as the logic    * in {@link #init(Configuration)} prevents re-entrancy.    *    * The base implementation checks to see if the subclass has created    * a new configuration instance, and if so, updates the base class value    * @param conf configuration    * @throws Exception on a failure -these will be caught,    * possibly wrapped, and wil; trigger a service stop    */
DECL|method|serviceInit (Configuration conf)
specifier|protected
name|void
name|serviceInit
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|conf
operator|!=
name|config
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Config has been overridden during init"
argument_list|)
expr_stmt|;
name|setConfig
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Actions called during the INITED to STARTED transition.    *    * This method will only ever be called once during the lifecycle of    * a specific service instance.    *    * Implementations do not need to be synchronized as the logic    * in {@link #start()} prevents re-entrancy.    *    * @throws Exception if needed -these will be caught,    * wrapped, and trigger a service stop    */
DECL|method|serviceStart ()
specifier|protected
name|void
name|serviceStart
parameter_list|()
throws|throws
name|Exception
block|{    }
comment|/**    * Actions called during the transition to the STOPPED state.    *    * This method will only ever be called once during the lifecycle of    * a specific service instance.    *    * Implementations do not need to be synchronized as the logic    * in {@link #stop()} prevents re-entrancy.    *    * Implementations MUST write this to be robust against failures, including    * checks for null references -and for the first failure to not stop other    * attempts to shut down parts of the service.    *    * @throws Exception if needed -these will be caught and logged.    */
DECL|method|serviceStop ()
specifier|protected
name|void
name|serviceStop
parameter_list|()
throws|throws
name|Exception
block|{    }
annotation|@
name|Override
DECL|method|register (ServiceStateChangeListener l)
specifier|public
name|void
name|register
parameter_list|(
name|ServiceStateChangeListener
name|l
parameter_list|)
block|{
name|listeners
operator|.
name|add
argument_list|(
name|l
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|unregister (ServiceStateChangeListener l)
specifier|public
name|void
name|unregister
parameter_list|(
name|ServiceStateChangeListener
name|l
parameter_list|)
block|{
name|listeners
operator|.
name|remove
argument_list|(
name|l
argument_list|)
expr_stmt|;
block|}
comment|/**    * Register a global listener, which receives notifications    * from the state change events of all services in the JVM    * @param l listener    */
DECL|method|registerGlobalListener (ServiceStateChangeListener l)
specifier|public
specifier|static
name|void
name|registerGlobalListener
parameter_list|(
name|ServiceStateChangeListener
name|l
parameter_list|)
block|{
name|globalListeners
operator|.
name|add
argument_list|(
name|l
argument_list|)
expr_stmt|;
block|}
comment|/**    * unregister a global listener.    * @param l listener to unregister    * @return true if the listener was found (and then deleted)    */
DECL|method|unregisterGlobalListener (ServiceStateChangeListener l)
specifier|public
specifier|static
name|boolean
name|unregisterGlobalListener
parameter_list|(
name|ServiceStateChangeListener
name|l
parameter_list|)
block|{
return|return
name|globalListeners
operator|.
name|remove
argument_list|(
name|l
argument_list|)
return|;
block|}
comment|/**    * Package-scoped method for testing -resets the global listener list    */
annotation|@
name|VisibleForTesting
DECL|method|resetGlobalListeners ()
specifier|static
name|void
name|resetGlobalListeners
parameter_list|()
block|{
name|globalListeners
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
annotation|@
name|Override
DECL|method|getConfig ()
specifier|public
specifier|synchronized
name|Configuration
name|getConfig
parameter_list|()
block|{
return|return
name|config
return|;
block|}
annotation|@
name|Override
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
comment|/**    * Notify local and global listeners of state changes.    * Exceptions raised by listeners are NOT passed up.    */
DECL|method|notifyListeners ()
specifier|private
name|void
name|notifyListeners
parameter_list|()
block|{
try|try
block|{
name|listeners
operator|.
name|notifyListeners
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|globalListeners
operator|.
name|notifyListeners
argument_list|(
name|this
argument_list|)
expr_stmt|;
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
literal|"Exception while notifying listeners of "
operator|+
name|this
operator|+
literal|": "
operator|+
name|e
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Add a state change event to the lifecycle history    */
DECL|method|recordLifecycleEvent ()
specifier|private
name|void
name|recordLifecycleEvent
parameter_list|()
block|{
name|LifecycleEvent
name|event
init|=
operator|new
name|LifecycleEvent
argument_list|()
decl_stmt|;
name|event
operator|.
name|time
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
name|event
operator|.
name|state
operator|=
name|getServiceState
argument_list|()
expr_stmt|;
name|lifecycleHistory
operator|.
name|add
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getLifecycleHistory ()
specifier|public
specifier|synchronized
name|List
argument_list|<
name|LifecycleEvent
argument_list|>
name|getLifecycleHistory
parameter_list|()
block|{
return|return
operator|new
name|ArrayList
argument_list|<
name|LifecycleEvent
argument_list|>
argument_list|(
name|lifecycleHistory
argument_list|)
return|;
block|}
comment|/**    * Enter a state; record this via {@link #recordLifecycleEvent}    * and log at the info level.    * @param newState the proposed new state    * @return the original state    * it wasn't already in that state, and the state model permits state re-entrancy.    */
DECL|method|enterState (STATE newState)
specifier|private
name|STATE
name|enterState
parameter_list|(
name|STATE
name|newState
parameter_list|)
block|{
assert|assert
name|stateModel
operator|!=
literal|null
operator|:
literal|"null state in "
operator|+
name|name
operator|+
literal|" "
operator|+
name|this
operator|.
name|getClass
argument_list|()
assert|;
name|STATE
name|oldState
init|=
name|stateModel
operator|.
name|enterState
argument_list|(
name|newState
argument_list|)
decl_stmt|;
if|if
condition|(
name|oldState
operator|!=
name|newState
condition|)
block|{
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
literal|"Service: "
operator|+
name|getName
argument_list|()
operator|+
literal|" entered state "
operator|+
name|getServiceState
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|recordLifecycleEvent
argument_list|()
expr_stmt|;
block|}
return|return
name|oldState
return|;
block|}
annotation|@
name|Override
DECL|method|isInState (Service.STATE expected)
specifier|public
specifier|final
name|boolean
name|isInState
parameter_list|(
name|Service
operator|.
name|STATE
name|expected
parameter_list|)
block|{
return|return
name|stateModel
operator|.
name|isInState
argument_list|(
name|expected
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"Service "
operator|+
name|name
operator|+
literal|" in state "
operator|+
name|stateModel
return|;
block|}
comment|/**    * Put a blocker to the blocker map -replacing any    * with the same name.    * @param name blocker name    * @param details any specifics on the block. This must be non-null.    */
DECL|method|putBlocker (String name, String details)
specifier|protected
name|void
name|putBlocker
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|details
parameter_list|)
block|{
synchronized|synchronized
init|(
name|blockerMap
init|)
block|{
name|blockerMap
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|details
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Remove a blocker from the blocker map -    * this is a no-op if the blocker is not present    * @param name the name of the blocker    */
DECL|method|removeBlocker (String name)
specifier|public
name|void
name|removeBlocker
parameter_list|(
name|String
name|name
parameter_list|)
block|{
synchronized|synchronized
init|(
name|blockerMap
init|)
block|{
name|blockerMap
operator|.
name|remove
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getBlockers ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getBlockers
parameter_list|()
block|{
synchronized|synchronized
init|(
name|blockerMap
init|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|(
name|blockerMap
argument_list|)
decl_stmt|;
return|return
name|map
return|;
block|}
block|}
block|}
end_class

end_unit

