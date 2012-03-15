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
name|List
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
comment|/**    * Service state: initially {@link STATE#NOTINITED}.    */
DECL|field|state
specifier|private
name|STATE
name|state
init|=
name|STATE
operator|.
name|NOTINITED
decl_stmt|;
comment|/**    * Service name.    */
DECL|field|name
specifier|private
specifier|final
name|String
name|name
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
name|Configuration
name|config
decl_stmt|;
comment|/**    * List of state change listeners; it is final to ensure    * that it will never be null.    */
DECL|field|listeners
specifier|private
name|List
argument_list|<
name|ServiceStateChangeListener
argument_list|>
name|listeners
init|=
operator|new
name|ArrayList
argument_list|<
name|ServiceStateChangeListener
argument_list|>
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
block|}
annotation|@
name|Override
DECL|method|getServiceState ()
specifier|public
specifier|synchronized
name|STATE
name|getServiceState
parameter_list|()
block|{
return|return
name|state
return|;
block|}
comment|/**    * {@inheritDoc}    * @throws IllegalStateException if the current service state does not permit    * this action    */
annotation|@
name|Override
DECL|method|init (Configuration conf)
specifier|public
specifier|synchronized
name|void
name|init
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|ensureCurrentState
argument_list|(
name|STATE
operator|.
name|NOTINITED
argument_list|)
expr_stmt|;
name|this
operator|.
name|config
operator|=
name|conf
expr_stmt|;
name|changeState
argument_list|(
name|STATE
operator|.
name|INITED
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Service:"
operator|+
name|getName
argument_list|()
operator|+
literal|" is inited."
argument_list|)
expr_stmt|;
block|}
comment|/**    * {@inheritDoc}    * @throws IllegalStateException if the current service state does not permit    * this action    */
annotation|@
name|Override
DECL|method|start ()
specifier|public
specifier|synchronized
name|void
name|start
parameter_list|()
block|{
name|startTime
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
name|ensureCurrentState
argument_list|(
name|STATE
operator|.
name|INITED
argument_list|)
expr_stmt|;
name|changeState
argument_list|(
name|STATE
operator|.
name|STARTED
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Service:"
operator|+
name|getName
argument_list|()
operator|+
literal|" is started."
argument_list|)
expr_stmt|;
block|}
comment|/**    * {@inheritDoc}    * @throws IllegalStateException if the current service state does not permit    * this action    */
annotation|@
name|Override
DECL|method|stop ()
specifier|public
specifier|synchronized
name|void
name|stop
parameter_list|()
block|{
if|if
condition|(
name|state
operator|==
name|STATE
operator|.
name|STOPPED
operator|||
name|state
operator|==
name|STATE
operator|.
name|INITED
operator|||
name|state
operator|==
name|STATE
operator|.
name|NOTINITED
condition|)
block|{
comment|// already stopped, or else it was never
comment|// started (eg another service failing canceled startup)
return|return;
block|}
name|ensureCurrentState
argument_list|(
name|STATE
operator|.
name|STARTED
argument_list|)
expr_stmt|;
name|changeState
argument_list|(
name|STATE
operator|.
name|STOPPED
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Service:"
operator|+
name|getName
argument_list|()
operator|+
literal|" is stopped."
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|register (ServiceStateChangeListener l)
specifier|public
specifier|synchronized
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
specifier|synchronized
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
comment|/**    * Verify that that a service is in a given state.    * @param currentState the desired state    * @throws IllegalStateException if the service state is different from    * the desired state    */
DECL|method|ensureCurrentState (STATE currentState)
specifier|private
name|void
name|ensureCurrentState
parameter_list|(
name|STATE
name|currentState
parameter_list|)
block|{
name|ServiceOperations
operator|.
name|ensureCurrentState
argument_list|(
name|state
argument_list|,
name|currentState
argument_list|)
expr_stmt|;
block|}
comment|/**    * Change to a new state and notify all listeners.    * This is a private method that is only invoked from synchronized methods,    * which avoid having to clone the listener list. It does imply that    * the state change listener methods should be short lived, as they    * will delay the state transition.    * @param newState new service state    */
DECL|method|changeState (STATE newState)
specifier|private
name|void
name|changeState
parameter_list|(
name|STATE
name|newState
parameter_list|)
block|{
name|state
operator|=
name|newState
expr_stmt|;
comment|//notify listeners
for|for
control|(
name|ServiceStateChangeListener
name|l
range|:
name|listeners
control|)
block|{
name|l
operator|.
name|stateChanged
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

