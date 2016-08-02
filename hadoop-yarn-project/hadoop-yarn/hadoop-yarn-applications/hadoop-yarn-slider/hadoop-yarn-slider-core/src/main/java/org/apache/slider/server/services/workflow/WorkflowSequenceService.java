begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.server.services.workflow
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|services
operator|.
name|workflow
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
name|service
operator|.
name|AbstractService
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
name|service
operator|.
name|Service
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
name|service
operator|.
name|ServiceStateChangeListener
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
name|service
operator|.
name|ServiceStateException
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
name|List
import|;
end_import

begin_comment
comment|/**  * This resembles the YARN CompositeService, except that it  * starts one service after another  *   * Workflow  *<ol>  *<li>When the<code>WorkflowSequenceService</code> instance is  *   initialized, it only initializes itself.</li>  *     *<li>When the<code>WorkflowSequenceService</code> instance is  *   started, it initializes then starts the first of its children.  *   If there are no children, it immediately stops.</li>  *     *<li>When the active child stops, it did not fail, and the parent has not  *   stopped -then the next service is initialized and started. If there is no  *   remaining child the parent service stops.</li>  *     *<li>If the active child did fail, the parent service notes the exception  *   and stops -effectively propagating up the failure.  *</li>  *</ol>  *   * New service instances MAY be added to a running instance -but no guarantees  * can be made as to whether or not they will be run.  */
end_comment

begin_class
DECL|class|WorkflowSequenceService
specifier|public
class|class
name|WorkflowSequenceService
extends|extends
name|AbstractService
implements|implements
name|ServiceParent
implements|,
name|ServiceStateChangeListener
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
name|WorkflowSequenceService
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * list of services    */
DECL|field|serviceList
specifier|private
specifier|final
name|List
argument_list|<
name|Service
argument_list|>
name|serviceList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|/**    * The currently active service.    * Volatile -may change& so should be read into a     * local variable before working with    */
DECL|field|activeService
specifier|private
specifier|volatile
name|Service
name|activeService
decl_stmt|;
comment|/**   the previous service -the last one that finished.    null if one did not finish yet    */
DECL|field|previousService
specifier|private
specifier|volatile
name|Service
name|previousService
decl_stmt|;
DECL|field|stopIfNoChildServicesAtStartup
specifier|private
name|boolean
name|stopIfNoChildServicesAtStartup
init|=
literal|true
decl_stmt|;
comment|/**    * Construct an instance    * @param name service name    */
DECL|method|WorkflowSequenceService (String name)
specifier|public
name|WorkflowSequenceService
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
comment|/**    * Construct an instance with the default name    */
DECL|method|WorkflowSequenceService ()
specifier|public
name|WorkflowSequenceService
parameter_list|()
block|{
name|this
argument_list|(
literal|"WorkflowSequenceService"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a service sequence with the given list of services    * @param name service name    * @param children initial sequence    */
DECL|method|WorkflowSequenceService (String name, Service... children)
specifier|public
name|WorkflowSequenceService
parameter_list|(
name|String
name|name
parameter_list|,
name|Service
modifier|...
name|children
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
for|for
control|(
name|Service
name|service
range|:
name|children
control|)
block|{
name|addService
argument_list|(
name|service
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Create a service sequence with the given list of services    * @param name service name    * @param children initial sequence    */
DECL|method|WorkflowSequenceService (String name, List<Service> children)
specifier|public
name|WorkflowSequenceService
parameter_list|(
name|String
name|name
parameter_list|,
name|List
argument_list|<
name|Service
argument_list|>
name|children
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
for|for
control|(
name|Service
name|service
range|:
name|children
control|)
block|{
name|addService
argument_list|(
name|service
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Get the current service -which may be null    * @return service running    */
DECL|method|getActiveService ()
specifier|public
name|Service
name|getActiveService
parameter_list|()
block|{
return|return
name|activeService
return|;
block|}
comment|/**    * Get the previously active service    * @return the service last run, or null if there is none.    */
DECL|method|getPreviousService ()
specifier|public
name|Service
name|getPreviousService
parameter_list|()
block|{
return|return
name|previousService
return|;
block|}
DECL|method|setStopIfNoChildServicesAtStartup (boolean stopIfNoChildServicesAtStartup)
specifier|protected
name|void
name|setStopIfNoChildServicesAtStartup
parameter_list|(
name|boolean
name|stopIfNoChildServicesAtStartup
parameter_list|)
block|{
name|this
operator|.
name|stopIfNoChildServicesAtStartup
operator|=
name|stopIfNoChildServicesAtStartup
expr_stmt|;
block|}
comment|/**    * When started    * @throws Exception    */
annotation|@
name|Override
DECL|method|serviceStart ()
specifier|protected
name|void
name|serviceStart
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
operator|!
name|startNextService
argument_list|()
operator|&&
name|stopIfNoChildServicesAtStartup
condition|)
block|{
comment|//nothing to start -so stop
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|serviceStop ()
specifier|protected
name|void
name|serviceStop
parameter_list|()
throws|throws
name|Exception
block|{
comment|//stop current service.
comment|//this triggers a callback that is caught and ignored
name|Service
name|current
init|=
name|activeService
decl_stmt|;
name|previousService
operator|=
name|current
expr_stmt|;
name|activeService
operator|=
literal|null
expr_stmt|;
if|if
condition|(
name|current
operator|!=
literal|null
condition|)
block|{
name|current
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Start the next service in the list.    * Return false if there are no more services to run, or this    * service has stopped    * @return true if a service was started    * @throws RuntimeException from any init or start failure    * @throws ServiceStateException if this call is made before    * the service is started    */
DECL|method|startNextService ()
specifier|public
specifier|synchronized
name|boolean
name|startNextService
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
comment|//downgrade to a failed
name|LOG
operator|.
name|debug
argument_list|(
literal|"Not starting next service -{} is stopped"
argument_list|,
name|this
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
name|isInState
argument_list|(
name|STATE
operator|.
name|STARTED
argument_list|)
condition|)
block|{
comment|//reject attempts to start a service too early
throw|throw
operator|new
name|ServiceStateException
argument_list|(
literal|"Cannot start a child service when not started"
argument_list|)
throw|;
block|}
if|if
condition|(
name|serviceList
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|//nothing left to run
return|return
literal|false
return|;
block|}
if|if
condition|(
name|activeService
operator|!=
literal|null
operator|&&
name|activeService
operator|.
name|getFailureCause
argument_list|()
operator|!=
literal|null
condition|)
block|{
comment|//did the last service fail? Is this caused by some premature callback?
name|LOG
operator|.
name|debug
argument_list|(
literal|"Not starting next service due to a failure of {}"
argument_list|,
name|activeService
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
comment|//bear in mind that init& start can fail, which
comment|//can trigger re-entrant calls into the state change listener.
comment|//by setting the current service to null
comment|//the start-next-service logic is skipped.
comment|//now, what does that mean w.r.t exit states?
name|activeService
operator|=
literal|null
expr_stmt|;
name|Service
name|head
init|=
name|serviceList
operator|.
name|remove
argument_list|(
literal|0
argument_list|)
decl_stmt|;
try|try
block|{
name|head
operator|.
name|init
argument_list|(
name|getConfig
argument_list|()
argument_list|)
expr_stmt|;
name|head
operator|.
name|registerServiceListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|head
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
name|noteFailure
argument_list|(
name|e
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
comment|//at this point the service must have explicitly started& not failed,
comment|//else an exception would have been raised
name|activeService
operator|=
name|head
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|/**    * State change event relays service stop events to    * {@link #onServiceCompleted(Service)}. Subclasses can    * extend that with extra logic    * @param service the service that has changed.    */
annotation|@
name|Override
DECL|method|stateChanged (Service service)
specifier|public
name|void
name|stateChanged
parameter_list|(
name|Service
name|service
parameter_list|)
block|{
comment|// only react to the state change when it is the current service
comment|// and it has entered the STOPPED state
if|if
condition|(
name|service
operator|==
name|activeService
operator|&&
name|service
operator|.
name|isInState
argument_list|(
name|STATE
operator|.
name|STOPPED
argument_list|)
condition|)
block|{
name|onServiceCompleted
argument_list|(
name|service
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * handler for service completion: base class starts the next service    * @param service service that has completed    */
DECL|method|onServiceCompleted (Service service)
specifier|protected
specifier|synchronized
name|void
name|onServiceCompleted
parameter_list|(
name|Service
name|service
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Running service stopped: {}"
argument_list|,
name|service
argument_list|)
expr_stmt|;
name|previousService
operator|=
name|activeService
expr_stmt|;
comment|//start the next service if we are not stopped ourselves
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
comment|//did the service fail? if so: propagate
name|Throwable
name|failureCause
init|=
name|service
operator|.
name|getFailureCause
argument_list|()
decl_stmt|;
if|if
condition|(
name|failureCause
operator|!=
literal|null
condition|)
block|{
name|Exception
name|e
init|=
operator|(
name|failureCause
operator|instanceof
name|Exception
operator|)
condition|?
operator|(
name|Exception
operator|)
name|failureCause
else|:
operator|new
name|Exception
argument_list|(
name|failureCause
argument_list|)
decl_stmt|;
name|noteFailure
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|stop
argument_list|()
expr_stmt|;
block|}
comment|//start the next service
name|boolean
name|started
decl_stmt|;
try|try
block|{
name|started
operator|=
name|startNextService
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|//something went wrong here
name|noteFailure
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|started
operator|=
literal|false
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|started
condition|)
block|{
comment|//no start because list is empty
comment|//stop and expect the notification to go upstream
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
comment|//not started, so just note that the current service
comment|//has gone away
name|activeService
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|/**    * Add the passed {@link Service} to the list of services managed by this    * {@link WorkflowSequenceService}    * @param service the {@link Service} to be added    */
annotation|@
name|Override
DECL|method|addService (Service service)
specifier|public
specifier|synchronized
name|void
name|addService
parameter_list|(
name|Service
name|service
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|service
operator|!=
literal|null
argument_list|,
literal|"null service argument"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Adding service {} "
argument_list|,
name|service
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|serviceList
init|)
block|{
name|serviceList
operator|.
name|add
argument_list|(
name|service
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Get an unmodifiable list of services    * @return a list of child services at the time of invocation -    * added services will not be picked up.    */
annotation|@
name|Override
comment|//Parent
DECL|method|getServices ()
specifier|public
specifier|synchronized
name|List
argument_list|<
name|Service
argument_list|>
name|getServices
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|serviceList
argument_list|)
return|;
block|}
annotation|@
name|Override
comment|// Object
DECL|method|toString ()
specifier|public
specifier|synchronized
name|String
name|toString
parameter_list|()
block|{
return|return
name|super
operator|.
name|toString
argument_list|()
operator|+
literal|"; current service "
operator|+
name|activeService
operator|+
literal|"; queued service count="
operator|+
name|serviceList
operator|.
name|size
argument_list|()
return|;
block|}
block|}
end_class

end_unit

