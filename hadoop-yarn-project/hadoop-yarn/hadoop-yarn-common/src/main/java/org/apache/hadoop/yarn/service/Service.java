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

begin_comment
comment|/**  * Service LifeCycle.  */
end_comment

begin_interface
DECL|interface|Service
specifier|public
interface|interface
name|Service
extends|extends
name|Closeable
block|{
comment|/**    * Service states    */
DECL|enum|STATE
specifier|public
enum|enum
name|STATE
block|{
comment|/** Constructed but not initialized */
DECL|enumConstant|NOTINITED
name|NOTINITED
argument_list|(
literal|0
argument_list|,
literal|"NOTINITED"
argument_list|)
block|,
comment|/** Initialized but not started or stopped */
DECL|enumConstant|INITED
name|INITED
argument_list|(
literal|1
argument_list|,
literal|"INITED"
argument_list|)
block|,
comment|/** started and not stopped */
DECL|enumConstant|STARTED
name|STARTED
argument_list|(
literal|2
argument_list|,
literal|"STARTED"
argument_list|)
block|,
comment|/** stopped. No further state transitions are permitted */
DECL|enumConstant|STOPPED
name|STOPPED
argument_list|(
literal|3
argument_list|,
literal|"STOPPED"
argument_list|)
block|;
comment|/**      * An integer value for use in array lookup and JMX interfaces.      * Although {@link Enum#ordinal()} could do this, explicitly      * identify the numbers gives more stability guarantees over time.      */
DECL|field|value
specifier|private
specifier|final
name|int
name|value
decl_stmt|;
comment|/**      * A name of the state that can be used in messages      */
DECL|field|statename
specifier|private
specifier|final
name|String
name|statename
decl_stmt|;
DECL|method|STATE (int value, String name)
specifier|private
name|STATE
parameter_list|(
name|int
name|value
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
name|this
operator|.
name|statename
operator|=
name|name
expr_stmt|;
block|}
comment|/**      * Get the integer value of a state      * @return the numeric value of the state      */
DECL|method|getValue ()
specifier|public
name|int
name|getValue
parameter_list|()
block|{
return|return
name|value
return|;
block|}
comment|/**      * Get the name of a state      * @return the state's name      */
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|statename
return|;
block|}
block|}
comment|/**    * Initialize the service.    *    * The transition MUST be from {@link STATE#NOTINITED} to {@link STATE#INITED}    * unless the operation failed and an exception was raised, in which case    * {@link #stop()} MUST be invoked and the service enter the state    * {@link STATE#STOPPED}.    * @param config the configuration of the service    * @throws RuntimeException on any failure during the operation     */
DECL|method|init (Configuration config)
name|void
name|init
parameter_list|(
name|Configuration
name|config
parameter_list|)
function_decl|;
comment|/**    * Start the service.    *    * The transition MUST be from {@link STATE#INITED} to {@link STATE#STARTED}    * unless the operation failed and an exception was raised, in which case    * {@link #stop()} MUST be invoked and the service enter the state    * {@link STATE#STOPPED}.    * @throws RuntimeException on any failure during the operation    */
DECL|method|start ()
name|void
name|start
parameter_list|()
function_decl|;
comment|/**    * Stop the service. This MUST be a no-op if the service is already    * in the {@link STATE#STOPPED} state. It SHOULD be a best-effort attempt    * to stop all parts of the service.    *    * The implementation must be designed to complete regardless of the service    * state, including the initialized/uninitialized state of all its internal    * fields.    * @throws RuntimeException on any failure during the stop operation    */
DECL|method|stop ()
name|void
name|stop
parameter_list|()
function_decl|;
comment|/**    * A version of stop() that is designed to be usable in Java7 closure    * clauses.    * Implementation classes MUST relay this directly to {@link #stop()}    * @throws IOException never    * @throws RuntimeException on any failure during the stop operation    */
DECL|method|close ()
name|void
name|close
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Register a listener to the service state change events.    * If the supplied listener is already listening to this service,    * this method is a no-op.    * @param listener a new listener    */
DECL|method|registerServiceListener (ServiceStateChangeListener listener)
name|void
name|registerServiceListener
parameter_list|(
name|ServiceStateChangeListener
name|listener
parameter_list|)
function_decl|;
comment|/**    * Unregister a previously registered listener of the service state    * change events. No-op if the listener is already unregistered.    * @param listener the listener to unregister.    */
DECL|method|unregisterServiceListener (ServiceStateChangeListener listener)
name|void
name|unregisterServiceListener
parameter_list|(
name|ServiceStateChangeListener
name|listener
parameter_list|)
function_decl|;
comment|/**    * Get the name of this service.    * @return the service name    */
DECL|method|getName ()
name|String
name|getName
parameter_list|()
function_decl|;
comment|/**    * Get the configuration of this service.    * This is normally not a clone and may be manipulated, though there are no    * guarantees as to what the consequences of such actions may be    * @return the current configuration, unless a specific implentation chooses    * otherwise.    */
DECL|method|getConfig ()
name|Configuration
name|getConfig
parameter_list|()
function_decl|;
comment|/**    * Get the current service state    * @return the state of the service    */
DECL|method|getServiceState ()
name|STATE
name|getServiceState
parameter_list|()
function_decl|;
comment|/**    * Get the service start time    * @return the start time of the service. This will be zero if the service    * has not yet been started.    */
DECL|method|getStartTime ()
name|long
name|getStartTime
parameter_list|()
function_decl|;
comment|/**    * Query to see if the service is in a specific state.    * In a multi-threaded system, the state may not hold for very long.    * @param state the expected state    * @return true if, at the time of invocation, the service was in that state.    */
DECL|method|isInState (STATE state)
name|boolean
name|isInState
parameter_list|(
name|STATE
name|state
parameter_list|)
function_decl|;
comment|/**    * Get the first exception raised during the service failure. If null,    * no exception was logged    * @return the failure logged during a transition to the stopped state    */
DECL|method|getFailureCause ()
name|Throwable
name|getFailureCause
parameter_list|()
function_decl|;
comment|/**    * Get the state in which the failure in {@link #getFailureCause()} occurred.    * @return the state or null if there was no failure    */
DECL|method|getFailureState ()
name|STATE
name|getFailureState
parameter_list|()
function_decl|;
comment|/**    * Block waiting for the service to stop; uses the termination notification    * object to do so.    *    * This method will only return after all the service stop actions    * have been executed (to success or failure), or the timeout elapsed    * This method can be called before the service is inited or started; this is    * to eliminate any race condition with the service stopping before    * this event occurs.    * @param timeout timeout in milliseconds. A value of zero means "forever"    * @return true iff the service stopped in the time period    */
DECL|method|waitForServiceToStop (long timeout)
name|boolean
name|waitForServiceToStop
parameter_list|(
name|long
name|timeout
parameter_list|)
function_decl|;
comment|/**    * Get a snapshot of the lifecycle history; it is a static list    * @return a possibly empty but never null list of lifecycle events.    */
DECL|method|getLifecycleHistory ()
specifier|public
name|List
argument_list|<
name|LifecycleEvent
argument_list|>
name|getLifecycleHistory
parameter_list|()
function_decl|;
comment|/**    * Get the blockers on a service -remote dependencies    * that are stopping the service from being<i>live</i>.    * @return a (snapshotted) map of blocker name-&gt;description values    */
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
function_decl|;
block|}
end_interface

end_unit

