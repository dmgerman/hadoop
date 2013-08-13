begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|nodemanager
operator|.
name|containermanager
operator|.
name|localizer
package|;
end_package

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
name|Queue
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
name|Semaphore
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
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|Lock
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
name|locks
operator|.
name|ReadWriteLock
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
name|locks
operator|.
name|ReentrantReadWriteLock
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
name|fs
operator|.
name|Path
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ContainerId
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
name|yarn
operator|.
name|event
operator|.
name|Dispatcher
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
name|yarn
operator|.
name|event
operator|.
name|EventHandler
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
name|yarn
operator|.
name|server
operator|.
name|nodemanager
operator|.
name|containermanager
operator|.
name|container
operator|.
name|ContainerResourceFailedEvent
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
name|yarn
operator|.
name|server
operator|.
name|nodemanager
operator|.
name|containermanager
operator|.
name|container
operator|.
name|ContainerResourceLocalizedEvent
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
name|yarn
operator|.
name|server
operator|.
name|nodemanager
operator|.
name|containermanager
operator|.
name|localizer
operator|.
name|event
operator|.
name|LocalizerResourceRequestEvent
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
name|yarn
operator|.
name|server
operator|.
name|nodemanager
operator|.
name|containermanager
operator|.
name|localizer
operator|.
name|event
operator|.
name|ResourceEvent
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
name|yarn
operator|.
name|server
operator|.
name|nodemanager
operator|.
name|containermanager
operator|.
name|localizer
operator|.
name|event
operator|.
name|ResourceEventType
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
name|yarn
operator|.
name|server
operator|.
name|nodemanager
operator|.
name|containermanager
operator|.
name|localizer
operator|.
name|event
operator|.
name|ResourceFailedLocalizationEvent
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
name|yarn
operator|.
name|server
operator|.
name|nodemanager
operator|.
name|containermanager
operator|.
name|localizer
operator|.
name|event
operator|.
name|ResourceLocalizedEvent
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
name|yarn
operator|.
name|server
operator|.
name|nodemanager
operator|.
name|containermanager
operator|.
name|localizer
operator|.
name|event
operator|.
name|ResourceReleaseEvent
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
name|yarn
operator|.
name|server
operator|.
name|nodemanager
operator|.
name|containermanager
operator|.
name|localizer
operator|.
name|event
operator|.
name|ResourceRequestEvent
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
name|yarn
operator|.
name|state
operator|.
name|InvalidStateTransitonException
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
name|yarn
operator|.
name|state
operator|.
name|SingleArcTransition
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
name|yarn
operator|.
name|state
operator|.
name|StateMachine
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
name|yarn
operator|.
name|state
operator|.
name|StateMachineFactory
import|;
end_import

begin_comment
comment|/**  * Datum representing a localized resource. Holds the statemachine of a  * resource. State of the resource is one of {@link ResourceState}.  *   */
end_comment

begin_class
DECL|class|LocalizedResource
specifier|public
class|class
name|LocalizedResource
implements|implements
name|EventHandler
argument_list|<
name|ResourceEvent
argument_list|>
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
name|LocalizedResource
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|localPath
name|Path
name|localPath
decl_stmt|;
DECL|field|size
name|long
name|size
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|rsrc
specifier|final
name|LocalResourceRequest
name|rsrc
decl_stmt|;
DECL|field|dispatcher
specifier|final
name|Dispatcher
name|dispatcher
decl_stmt|;
specifier|final
name|StateMachine
argument_list|<
name|ResourceState
argument_list|,
name|ResourceEventType
argument_list|,
name|ResourceEvent
argument_list|>
DECL|field|stateMachine
name|stateMachine
decl_stmt|;
DECL|field|sem
specifier|final
name|Semaphore
name|sem
init|=
operator|new
name|Semaphore
argument_list|(
literal|1
argument_list|)
decl_stmt|;
DECL|field|ref
specifier|final
name|Queue
argument_list|<
name|ContainerId
argument_list|>
name|ref
decl_stmt|;
comment|// Queue of containers using this localized
comment|// resource
DECL|field|readLock
specifier|private
specifier|final
name|Lock
name|readLock
decl_stmt|;
DECL|field|writeLock
specifier|private
specifier|final
name|Lock
name|writeLock
decl_stmt|;
DECL|field|timestamp
specifier|final
name|AtomicLong
name|timestamp
init|=
operator|new
name|AtomicLong
argument_list|(
name|currentTime
argument_list|()
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|StateMachineFactory
argument_list|<
name|LocalizedResource
argument_list|,
name|ResourceState
argument_list|,
DECL|field|stateMachineFactory
name|ResourceEventType
argument_list|,
name|ResourceEvent
argument_list|>
name|stateMachineFactory
init|=
operator|new
name|StateMachineFactory
argument_list|<
name|LocalizedResource
argument_list|,
name|ResourceState
argument_list|,
name|ResourceEventType
argument_list|,
name|ResourceEvent
argument_list|>
argument_list|(
name|ResourceState
operator|.
name|INIT
argument_list|)
comment|// From INIT (ref == 0, awaiting req)
operator|.
name|addTransition
argument_list|(
name|ResourceState
operator|.
name|INIT
argument_list|,
name|ResourceState
operator|.
name|DOWNLOADING
argument_list|,
name|ResourceEventType
operator|.
name|REQUEST
argument_list|,
operator|new
name|FetchResourceTransition
argument_list|()
argument_list|)
comment|// From DOWNLOADING (ref> 0, may be localizing)
operator|.
name|addTransition
argument_list|(
name|ResourceState
operator|.
name|DOWNLOADING
argument_list|,
name|ResourceState
operator|.
name|DOWNLOADING
argument_list|,
name|ResourceEventType
operator|.
name|REQUEST
argument_list|,
operator|new
name|FetchResourceTransition
argument_list|()
argument_list|)
comment|// TODO: Duplicate addition!!
operator|.
name|addTransition
argument_list|(
name|ResourceState
operator|.
name|DOWNLOADING
argument_list|,
name|ResourceState
operator|.
name|LOCALIZED
argument_list|,
name|ResourceEventType
operator|.
name|LOCALIZED
argument_list|,
operator|new
name|FetchSuccessTransition
argument_list|()
argument_list|)
operator|.
name|addTransition
argument_list|(
name|ResourceState
operator|.
name|DOWNLOADING
argument_list|,
name|ResourceState
operator|.
name|DOWNLOADING
argument_list|,
name|ResourceEventType
operator|.
name|RELEASE
argument_list|,
operator|new
name|ReleaseTransition
argument_list|()
argument_list|)
operator|.
name|addTransition
argument_list|(
name|ResourceState
operator|.
name|DOWNLOADING
argument_list|,
name|ResourceState
operator|.
name|FAILED
argument_list|,
name|ResourceEventType
operator|.
name|LOCALIZATION_FAILED
argument_list|,
operator|new
name|FetchFailedTransition
argument_list|()
argument_list|)
comment|// From LOCALIZED (ref>= 0, on disk)
operator|.
name|addTransition
argument_list|(
name|ResourceState
operator|.
name|LOCALIZED
argument_list|,
name|ResourceState
operator|.
name|LOCALIZED
argument_list|,
name|ResourceEventType
operator|.
name|REQUEST
argument_list|,
operator|new
name|LocalizedResourceTransition
argument_list|()
argument_list|)
operator|.
name|addTransition
argument_list|(
name|ResourceState
operator|.
name|LOCALIZED
argument_list|,
name|ResourceState
operator|.
name|LOCALIZED
argument_list|,
name|ResourceEventType
operator|.
name|RELEASE
argument_list|,
operator|new
name|ReleaseTransition
argument_list|()
argument_list|)
operator|.
name|installTopology
argument_list|()
decl_stmt|;
DECL|method|LocalizedResource (LocalResourceRequest rsrc, Dispatcher dispatcher)
specifier|public
name|LocalizedResource
parameter_list|(
name|LocalResourceRequest
name|rsrc
parameter_list|,
name|Dispatcher
name|dispatcher
parameter_list|)
block|{
name|this
operator|.
name|rsrc
operator|=
name|rsrc
expr_stmt|;
name|this
operator|.
name|dispatcher
operator|=
name|dispatcher
expr_stmt|;
name|this
operator|.
name|ref
operator|=
operator|new
name|LinkedList
argument_list|<
name|ContainerId
argument_list|>
argument_list|()
expr_stmt|;
name|ReadWriteLock
name|readWriteLock
init|=
operator|new
name|ReentrantReadWriteLock
argument_list|()
decl_stmt|;
name|this
operator|.
name|readLock
operator|=
name|readWriteLock
operator|.
name|readLock
argument_list|()
expr_stmt|;
name|this
operator|.
name|writeLock
operator|=
name|readWriteLock
operator|.
name|writeLock
argument_list|()
expr_stmt|;
name|this
operator|.
name|stateMachine
operator|=
name|stateMachineFactory
operator|.
name|make
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"{ "
argument_list|)
operator|.
name|append
argument_list|(
name|rsrc
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|","
argument_list|)
operator|.
name|append
argument_list|(
name|getState
argument_list|()
operator|==
name|ResourceState
operator|.
name|LOCALIZED
condition|?
name|getLocalPath
argument_list|()
operator|+
literal|","
operator|+
name|getSize
argument_list|()
else|:
literal|"pending"
argument_list|)
operator|.
name|append
argument_list|(
literal|",["
argument_list|)
expr_stmt|;
try|try
block|{
name|this
operator|.
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
for|for
control|(
name|ContainerId
name|c
range|:
name|ref
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"("
argument_list|)
operator|.
name|append
argument_list|(
name|c
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|"],"
argument_list|)
operator|.
name|append
argument_list|(
name|getTimestamp
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|","
argument_list|)
operator|.
name|append
argument_list|(
name|getState
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"}"
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
finally|finally
block|{
name|this
operator|.
name|readLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|release (ContainerId container)
specifier|private
name|void
name|release
parameter_list|(
name|ContainerId
name|container
parameter_list|)
block|{
if|if
condition|(
name|ref
operator|.
name|remove
argument_list|(
name|container
argument_list|)
condition|)
block|{
comment|// updating the timestamp only in case of success.
name|timestamp
operator|.
name|set
argument_list|(
name|currentTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Container "
operator|+
name|container
operator|+
literal|" doesn't exist in the container list of the Resource "
operator|+
name|this
operator|+
literal|" to which it sent RELEASE event"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|currentTime ()
specifier|private
name|long
name|currentTime
parameter_list|()
block|{
return|return
name|System
operator|.
name|nanoTime
argument_list|()
return|;
block|}
DECL|method|getState ()
specifier|public
name|ResourceState
name|getState
parameter_list|()
block|{
name|this
operator|.
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
return|return
name|stateMachine
operator|.
name|getCurrentState
argument_list|()
return|;
block|}
finally|finally
block|{
name|this
operator|.
name|readLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|getRequest ()
specifier|public
name|LocalResourceRequest
name|getRequest
parameter_list|()
block|{
return|return
name|rsrc
return|;
block|}
DECL|method|getLocalPath ()
specifier|public
name|Path
name|getLocalPath
parameter_list|()
block|{
return|return
name|localPath
return|;
block|}
DECL|method|getTimestamp ()
specifier|public
name|long
name|getTimestamp
parameter_list|()
block|{
return|return
name|timestamp
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|getSize ()
specifier|public
name|long
name|getSize
parameter_list|()
block|{
return|return
name|size
return|;
block|}
DECL|method|getRefCount ()
specifier|public
name|int
name|getRefCount
parameter_list|()
block|{
return|return
name|ref
operator|.
name|size
argument_list|()
return|;
block|}
DECL|method|tryAcquire ()
specifier|public
name|boolean
name|tryAcquire
parameter_list|()
block|{
return|return
name|sem
operator|.
name|tryAcquire
argument_list|()
return|;
block|}
DECL|method|unlock ()
specifier|public
name|void
name|unlock
parameter_list|()
block|{
name|sem
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|handle (ResourceEvent event)
specifier|public
name|void
name|handle
parameter_list|(
name|ResourceEvent
name|event
parameter_list|)
block|{
try|try
block|{
name|this
operator|.
name|writeLock
operator|.
name|lock
argument_list|()
expr_stmt|;
name|Path
name|resourcePath
init|=
name|event
operator|.
name|getLocalResourceRequest
argument_list|()
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Processing "
operator|+
name|resourcePath
operator|+
literal|" of type "
operator|+
name|event
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|ResourceState
name|oldState
init|=
name|this
operator|.
name|stateMachine
operator|.
name|getCurrentState
argument_list|()
decl_stmt|;
name|ResourceState
name|newState
init|=
literal|null
decl_stmt|;
try|try
block|{
name|newState
operator|=
name|this
operator|.
name|stateMachine
operator|.
name|doTransition
argument_list|(
name|event
operator|.
name|getType
argument_list|()
argument_list|,
name|event
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidStateTransitonException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Can't handle this event at current state"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|oldState
operator|!=
name|newState
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Resource "
operator|+
name|resourcePath
operator|+
operator|(
name|localPath
operator|!=
literal|null
condition|?
literal|"(->"
operator|+
name|localPath
operator|+
literal|")"
else|:
literal|""
operator|)
operator|+
literal|" transitioned from "
operator|+
name|oldState
operator|+
literal|" to "
operator|+
name|newState
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|this
operator|.
name|writeLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|ResourceTransition
specifier|static
specifier|abstract
class|class
name|ResourceTransition
implements|implements
name|SingleArcTransition
argument_list|<
name|LocalizedResource
argument_list|,
name|ResourceEvent
argument_list|>
block|{
comment|// typedef
block|}
comment|/**    * Transition from INIT to DOWNLOADING.    * Sends a {@link LocalizerResourceRequestEvent} to the    * {@link ResourceLocalizationService}.    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
comment|// dispatcher not typed
DECL|class|FetchResourceTransition
specifier|private
specifier|static
class|class
name|FetchResourceTransition
extends|extends
name|ResourceTransition
block|{
annotation|@
name|Override
DECL|method|transition (LocalizedResource rsrc, ResourceEvent event)
specifier|public
name|void
name|transition
parameter_list|(
name|LocalizedResource
name|rsrc
parameter_list|,
name|ResourceEvent
name|event
parameter_list|)
block|{
name|ResourceRequestEvent
name|req
init|=
operator|(
name|ResourceRequestEvent
operator|)
name|event
decl_stmt|;
name|LocalizerContext
name|ctxt
init|=
name|req
operator|.
name|getContext
argument_list|()
decl_stmt|;
name|ContainerId
name|container
init|=
name|ctxt
operator|.
name|getContainerId
argument_list|()
decl_stmt|;
name|rsrc
operator|.
name|ref
operator|.
name|add
argument_list|(
name|container
argument_list|)
expr_stmt|;
name|rsrc
operator|.
name|dispatcher
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|LocalizerResourceRequestEvent
argument_list|(
name|rsrc
argument_list|,
name|req
operator|.
name|getVisibility
argument_list|()
argument_list|,
name|ctxt
argument_list|,
name|req
operator|.
name|getLocalResourceRequest
argument_list|()
operator|.
name|getPattern
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Resource localized, notify waiting containers.    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
comment|// dispatcher not typed
DECL|class|FetchSuccessTransition
specifier|private
specifier|static
class|class
name|FetchSuccessTransition
extends|extends
name|ResourceTransition
block|{
annotation|@
name|Override
DECL|method|transition (LocalizedResource rsrc, ResourceEvent event)
specifier|public
name|void
name|transition
parameter_list|(
name|LocalizedResource
name|rsrc
parameter_list|,
name|ResourceEvent
name|event
parameter_list|)
block|{
name|ResourceLocalizedEvent
name|locEvent
init|=
operator|(
name|ResourceLocalizedEvent
operator|)
name|event
decl_stmt|;
name|rsrc
operator|.
name|localPath
operator|=
name|locEvent
operator|.
name|getLocation
argument_list|()
expr_stmt|;
name|rsrc
operator|.
name|size
operator|=
name|locEvent
operator|.
name|getSize
argument_list|()
expr_stmt|;
for|for
control|(
name|ContainerId
name|container
range|:
name|rsrc
operator|.
name|ref
control|)
block|{
name|rsrc
operator|.
name|dispatcher
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|ContainerResourceLocalizedEvent
argument_list|(
name|container
argument_list|,
name|rsrc
operator|.
name|rsrc
argument_list|,
name|rsrc
operator|.
name|localPath
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Resource localization failed, notify waiting containers.    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|class|FetchFailedTransition
specifier|private
specifier|static
class|class
name|FetchFailedTransition
extends|extends
name|ResourceTransition
block|{
annotation|@
name|Override
DECL|method|transition (LocalizedResource rsrc, ResourceEvent event)
specifier|public
name|void
name|transition
parameter_list|(
name|LocalizedResource
name|rsrc
parameter_list|,
name|ResourceEvent
name|event
parameter_list|)
block|{
name|ResourceFailedLocalizationEvent
name|failedEvent
init|=
operator|(
name|ResourceFailedLocalizationEvent
operator|)
name|event
decl_stmt|;
name|Queue
argument_list|<
name|ContainerId
argument_list|>
name|containers
init|=
name|rsrc
operator|.
name|ref
decl_stmt|;
for|for
control|(
name|ContainerId
name|container
range|:
name|containers
control|)
block|{
name|rsrc
operator|.
name|dispatcher
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|ContainerResourceFailedEvent
argument_list|(
name|container
argument_list|,
name|failedEvent
operator|.
name|getLocalResourceRequest
argument_list|()
argument_list|,
name|failedEvent
operator|.
name|getDiagnosticMessage
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Resource already localized, notify immediately.    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
comment|// dispatcher not typed
DECL|class|LocalizedResourceTransition
specifier|private
specifier|static
class|class
name|LocalizedResourceTransition
extends|extends
name|ResourceTransition
block|{
annotation|@
name|Override
DECL|method|transition (LocalizedResource rsrc, ResourceEvent event)
specifier|public
name|void
name|transition
parameter_list|(
name|LocalizedResource
name|rsrc
parameter_list|,
name|ResourceEvent
name|event
parameter_list|)
block|{
comment|// notify waiting containers
name|ResourceRequestEvent
name|reqEvent
init|=
operator|(
name|ResourceRequestEvent
operator|)
name|event
decl_stmt|;
name|ContainerId
name|container
init|=
name|reqEvent
operator|.
name|getContext
argument_list|()
operator|.
name|getContainerId
argument_list|()
decl_stmt|;
name|rsrc
operator|.
name|ref
operator|.
name|add
argument_list|(
name|container
argument_list|)
expr_stmt|;
name|rsrc
operator|.
name|dispatcher
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|ContainerResourceLocalizedEvent
argument_list|(
name|container
argument_list|,
name|rsrc
operator|.
name|rsrc
argument_list|,
name|rsrc
operator|.
name|localPath
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Decrement resource count, update timestamp.    */
DECL|class|ReleaseTransition
specifier|private
specifier|static
class|class
name|ReleaseTransition
extends|extends
name|ResourceTransition
block|{
annotation|@
name|Override
DECL|method|transition (LocalizedResource rsrc, ResourceEvent event)
specifier|public
name|void
name|transition
parameter_list|(
name|LocalizedResource
name|rsrc
parameter_list|,
name|ResourceEvent
name|event
parameter_list|)
block|{
comment|// Note: assumes that localizing container must succeed or fail
name|ResourceReleaseEvent
name|relEvent
init|=
operator|(
name|ResourceReleaseEvent
operator|)
name|event
decl_stmt|;
name|rsrc
operator|.
name|release
argument_list|(
name|relEvent
operator|.
name|getContainer
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

