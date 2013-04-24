begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.application
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
name|application
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|EnumSet
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
name|concurrent
operator|.
name|locks
operator|.
name|ReentrantReadWriteLock
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
operator|.
name|ReadLock
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
operator|.
name|WriteLock
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
name|security
operator|.
name|Credentials
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
name|security
operator|.
name|UserGroupInformation
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
name|ApplicationAccessType
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
name|ApplicationId
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
name|logaggregation
operator|.
name|ContainerLogsRetentionPolicy
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
name|Context
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
name|AuxServicesEvent
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
name|AuxServicesEventType
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
name|Container
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
name|ContainerInitEvent
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
name|ContainerKillEvent
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
name|ResourceLocalizationService
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
name|ApplicationLocalizationEvent
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
name|LocalizationEventType
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
name|logaggregation
operator|.
name|LogAggregationService
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
name|loghandler
operator|.
name|event
operator|.
name|LogHandlerAppFinishedEvent
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
name|loghandler
operator|.
name|event
operator|.
name|LogHandlerAppStartedEvent
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
name|security
operator|.
name|ApplicationACLsManager
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
name|MultipleArcTransition
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
comment|/**  * The state machine for the representation of an Application  * within the NodeManager.  */
end_comment

begin_class
DECL|class|ApplicationImpl
specifier|public
class|class
name|ApplicationImpl
implements|implements
name|Application
block|{
DECL|field|dispatcher
specifier|final
name|Dispatcher
name|dispatcher
decl_stmt|;
DECL|field|user
specifier|final
name|String
name|user
decl_stmt|;
DECL|field|appId
specifier|final
name|ApplicationId
name|appId
decl_stmt|;
DECL|field|credentials
specifier|final
name|Credentials
name|credentials
decl_stmt|;
DECL|field|applicationACLs
name|Map
argument_list|<
name|ApplicationAccessType
argument_list|,
name|String
argument_list|>
name|applicationACLs
decl_stmt|;
DECL|field|aclsManager
specifier|final
name|ApplicationACLsManager
name|aclsManager
decl_stmt|;
DECL|field|readLock
specifier|private
specifier|final
name|ReadLock
name|readLock
decl_stmt|;
DECL|field|writeLock
specifier|private
specifier|final
name|WriteLock
name|writeLock
decl_stmt|;
DECL|field|context
specifier|private
specifier|final
name|Context
name|context
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
name|Application
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|containers
name|Map
argument_list|<
name|ContainerId
argument_list|,
name|Container
argument_list|>
name|containers
init|=
operator|new
name|HashMap
argument_list|<
name|ContainerId
argument_list|,
name|Container
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|ApplicationImpl (Dispatcher dispatcher, ApplicationACLsManager aclsManager, String user, ApplicationId appId, Credentials credentials, Context context)
specifier|public
name|ApplicationImpl
parameter_list|(
name|Dispatcher
name|dispatcher
parameter_list|,
name|ApplicationACLsManager
name|aclsManager
parameter_list|,
name|String
name|user
parameter_list|,
name|ApplicationId
name|appId
parameter_list|,
name|Credentials
name|credentials
parameter_list|,
name|Context
name|context
parameter_list|)
block|{
name|this
operator|.
name|dispatcher
operator|=
name|dispatcher
expr_stmt|;
name|this
operator|.
name|user
operator|=
name|user
operator|.
name|toString
argument_list|()
expr_stmt|;
name|this
operator|.
name|appId
operator|=
name|appId
expr_stmt|;
name|this
operator|.
name|credentials
operator|=
name|credentials
expr_stmt|;
name|this
operator|.
name|aclsManager
operator|=
name|aclsManager
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
name|ReentrantReadWriteLock
name|lock
init|=
operator|new
name|ReentrantReadWriteLock
argument_list|()
decl_stmt|;
name|readLock
operator|=
name|lock
operator|.
name|readLock
argument_list|()
expr_stmt|;
name|writeLock
operator|=
name|lock
operator|.
name|writeLock
argument_list|()
expr_stmt|;
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
annotation|@
name|Override
DECL|method|getUser ()
specifier|public
name|String
name|getUser
parameter_list|()
block|{
return|return
name|user
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getAppId ()
specifier|public
name|ApplicationId
name|getAppId
parameter_list|()
block|{
return|return
name|appId
return|;
block|}
annotation|@
name|Override
DECL|method|getApplicationState ()
specifier|public
name|ApplicationState
name|getApplicationState
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
name|this
operator|.
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
annotation|@
name|Override
DECL|method|getContainers ()
specifier|public
name|Map
argument_list|<
name|ContainerId
argument_list|,
name|Container
argument_list|>
name|getContainers
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
name|this
operator|.
name|containers
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
DECL|field|CONTAINER_DONE_TRANSITION
specifier|private
specifier|static
specifier|final
name|ContainerDoneTransition
name|CONTAINER_DONE_TRANSITION
init|=
operator|new
name|ContainerDoneTransition
argument_list|()
decl_stmt|;
specifier|private
specifier|static
name|StateMachineFactory
argument_list|<
name|ApplicationImpl
argument_list|,
name|ApplicationState
argument_list|,
DECL|field|stateMachineFactory
name|ApplicationEventType
argument_list|,
name|ApplicationEvent
argument_list|>
name|stateMachineFactory
init|=
operator|new
name|StateMachineFactory
argument_list|<
name|ApplicationImpl
argument_list|,
name|ApplicationState
argument_list|,
name|ApplicationEventType
argument_list|,
name|ApplicationEvent
argument_list|>
argument_list|(
name|ApplicationState
operator|.
name|NEW
argument_list|)
comment|// Transitions from NEW state
operator|.
name|addTransition
argument_list|(
name|ApplicationState
operator|.
name|NEW
argument_list|,
name|ApplicationState
operator|.
name|INITING
argument_list|,
name|ApplicationEventType
operator|.
name|INIT_APPLICATION
argument_list|,
operator|new
name|AppInitTransition
argument_list|()
argument_list|)
operator|.
name|addTransition
argument_list|(
name|ApplicationState
operator|.
name|NEW
argument_list|,
name|ApplicationState
operator|.
name|NEW
argument_list|,
name|ApplicationEventType
operator|.
name|INIT_CONTAINER
argument_list|,
operator|new
name|InitContainerTransition
argument_list|()
argument_list|)
comment|// Transitions from INITING state
operator|.
name|addTransition
argument_list|(
name|ApplicationState
operator|.
name|INITING
argument_list|,
name|ApplicationState
operator|.
name|INITING
argument_list|,
name|ApplicationEventType
operator|.
name|INIT_CONTAINER
argument_list|,
operator|new
name|InitContainerTransition
argument_list|()
argument_list|)
operator|.
name|addTransition
argument_list|(
name|ApplicationState
operator|.
name|INITING
argument_list|,
name|EnumSet
operator|.
name|of
argument_list|(
name|ApplicationState
operator|.
name|FINISHING_CONTAINERS_WAIT
argument_list|,
name|ApplicationState
operator|.
name|APPLICATION_RESOURCES_CLEANINGUP
argument_list|)
argument_list|,
name|ApplicationEventType
operator|.
name|FINISH_APPLICATION
argument_list|,
operator|new
name|AppFinishTriggeredTransition
argument_list|()
argument_list|)
operator|.
name|addTransition
argument_list|(
name|ApplicationState
operator|.
name|INITING
argument_list|,
name|ApplicationState
operator|.
name|INITING
argument_list|,
name|ApplicationEventType
operator|.
name|APPLICATION_CONTAINER_FINISHED
argument_list|,
name|CONTAINER_DONE_TRANSITION
argument_list|)
operator|.
name|addTransition
argument_list|(
name|ApplicationState
operator|.
name|INITING
argument_list|,
name|ApplicationState
operator|.
name|INITING
argument_list|,
name|ApplicationEventType
operator|.
name|APPLICATION_LOG_HANDLING_INITED
argument_list|,
operator|new
name|AppLogInitDoneTransition
argument_list|()
argument_list|)
operator|.
name|addTransition
argument_list|(
name|ApplicationState
operator|.
name|INITING
argument_list|,
name|ApplicationState
operator|.
name|INITING
argument_list|,
name|ApplicationEventType
operator|.
name|APPLICATION_LOG_HANDLING_FAILED
argument_list|,
operator|new
name|AppLogInitFailTransition
argument_list|()
argument_list|)
operator|.
name|addTransition
argument_list|(
name|ApplicationState
operator|.
name|INITING
argument_list|,
name|ApplicationState
operator|.
name|RUNNING
argument_list|,
name|ApplicationEventType
operator|.
name|APPLICATION_INITED
argument_list|,
operator|new
name|AppInitDoneTransition
argument_list|()
argument_list|)
comment|// Transitions from RUNNING state
operator|.
name|addTransition
argument_list|(
name|ApplicationState
operator|.
name|RUNNING
argument_list|,
name|ApplicationState
operator|.
name|RUNNING
argument_list|,
name|ApplicationEventType
operator|.
name|INIT_CONTAINER
argument_list|,
operator|new
name|InitContainerTransition
argument_list|()
argument_list|)
operator|.
name|addTransition
argument_list|(
name|ApplicationState
operator|.
name|RUNNING
argument_list|,
name|ApplicationState
operator|.
name|RUNNING
argument_list|,
name|ApplicationEventType
operator|.
name|APPLICATION_CONTAINER_FINISHED
argument_list|,
name|CONTAINER_DONE_TRANSITION
argument_list|)
operator|.
name|addTransition
argument_list|(
name|ApplicationState
operator|.
name|RUNNING
argument_list|,
name|EnumSet
operator|.
name|of
argument_list|(
name|ApplicationState
operator|.
name|FINISHING_CONTAINERS_WAIT
argument_list|,
name|ApplicationState
operator|.
name|APPLICATION_RESOURCES_CLEANINGUP
argument_list|)
argument_list|,
name|ApplicationEventType
operator|.
name|FINISH_APPLICATION
argument_list|,
operator|new
name|AppFinishTriggeredTransition
argument_list|()
argument_list|)
comment|// Transitions from FINISHING_CONTAINERS_WAIT state.
operator|.
name|addTransition
argument_list|(
name|ApplicationState
operator|.
name|FINISHING_CONTAINERS_WAIT
argument_list|,
name|EnumSet
operator|.
name|of
argument_list|(
name|ApplicationState
operator|.
name|FINISHING_CONTAINERS_WAIT
argument_list|,
name|ApplicationState
operator|.
name|APPLICATION_RESOURCES_CLEANINGUP
argument_list|)
argument_list|,
name|ApplicationEventType
operator|.
name|APPLICATION_CONTAINER_FINISHED
argument_list|,
operator|new
name|AppFinishTransition
argument_list|()
argument_list|)
comment|// Transitions from APPLICATION_RESOURCES_CLEANINGUP state
operator|.
name|addTransition
argument_list|(
name|ApplicationState
operator|.
name|APPLICATION_RESOURCES_CLEANINGUP
argument_list|,
name|ApplicationState
operator|.
name|APPLICATION_RESOURCES_CLEANINGUP
argument_list|,
name|ApplicationEventType
operator|.
name|APPLICATION_CONTAINER_FINISHED
argument_list|)
operator|.
name|addTransition
argument_list|(
name|ApplicationState
operator|.
name|APPLICATION_RESOURCES_CLEANINGUP
argument_list|,
name|ApplicationState
operator|.
name|FINISHED
argument_list|,
name|ApplicationEventType
operator|.
name|APPLICATION_RESOURCES_CLEANEDUP
argument_list|,
operator|new
name|AppCompletelyDoneTransition
argument_list|()
argument_list|)
comment|// Transitions from FINISHED state
operator|.
name|addTransition
argument_list|(
name|ApplicationState
operator|.
name|FINISHED
argument_list|,
name|ApplicationState
operator|.
name|FINISHED
argument_list|,
name|ApplicationEventType
operator|.
name|APPLICATION_LOG_HANDLING_FINISHED
argument_list|,
operator|new
name|AppLogsAggregatedTransition
argument_list|()
argument_list|)
comment|// create the topology tables
operator|.
name|installTopology
argument_list|()
decl_stmt|;
DECL|field|stateMachine
specifier|private
specifier|final
name|StateMachine
argument_list|<
name|ApplicationState
argument_list|,
name|ApplicationEventType
argument_list|,
name|ApplicationEvent
argument_list|>
name|stateMachine
decl_stmt|;
comment|/**    * Notify services of new application.    *     * In particular, this initializes the {@link LogAggregationService}    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|class|AppInitTransition
specifier|static
class|class
name|AppInitTransition
implements|implements
name|SingleArcTransition
argument_list|<
name|ApplicationImpl
argument_list|,
name|ApplicationEvent
argument_list|>
block|{
annotation|@
name|Override
DECL|method|transition (ApplicationImpl app, ApplicationEvent event)
specifier|public
name|void
name|transition
parameter_list|(
name|ApplicationImpl
name|app
parameter_list|,
name|ApplicationEvent
name|event
parameter_list|)
block|{
name|ApplicationInitEvent
name|initEvent
init|=
operator|(
name|ApplicationInitEvent
operator|)
name|event
decl_stmt|;
name|app
operator|.
name|applicationACLs
operator|=
name|initEvent
operator|.
name|getApplicationACLs
argument_list|()
expr_stmt|;
name|app
operator|.
name|aclsManager
operator|.
name|addApplication
argument_list|(
name|app
operator|.
name|getAppId
argument_list|()
argument_list|,
name|app
operator|.
name|applicationACLs
argument_list|)
expr_stmt|;
comment|// Inform the logAggregator
name|app
operator|.
name|dispatcher
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|LogHandlerAppStartedEvent
argument_list|(
name|app
operator|.
name|appId
argument_list|,
name|app
operator|.
name|user
argument_list|,
name|app
operator|.
name|credentials
argument_list|,
name|ContainerLogsRetentionPolicy
operator|.
name|ALL_CONTAINERS
argument_list|,
name|app
operator|.
name|applicationACLs
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Handles the APPLICATION_LOG_HANDLING_INITED event that occurs after    * {@link LogAggregationService} has created the directories for the app    * and started the aggregation thread for the app.    *     * In particular, this requests that the {@link ResourceLocalizationService}    * localize the application-scoped resources.    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|class|AppLogInitDoneTransition
specifier|static
class|class
name|AppLogInitDoneTransition
implements|implements
name|SingleArcTransition
argument_list|<
name|ApplicationImpl
argument_list|,
name|ApplicationEvent
argument_list|>
block|{
annotation|@
name|Override
DECL|method|transition (ApplicationImpl app, ApplicationEvent event)
specifier|public
name|void
name|transition
parameter_list|(
name|ApplicationImpl
name|app
parameter_list|,
name|ApplicationEvent
name|event
parameter_list|)
block|{
name|app
operator|.
name|dispatcher
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|ApplicationLocalizationEvent
argument_list|(
name|LocalizationEventType
operator|.
name|INIT_APPLICATION_RESOURCES
argument_list|,
name|app
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Handles the APPLICATION_LOG_HANDLING_FAILED event that occurs after    * {@link LogAggregationService} has failed to initialize the log     * aggregation service    *     * In particular, this requests that the {@link ResourceLocalizationService}    * localize the application-scoped resources.    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|class|AppLogInitFailTransition
specifier|static
class|class
name|AppLogInitFailTransition
implements|implements
name|SingleArcTransition
argument_list|<
name|ApplicationImpl
argument_list|,
name|ApplicationEvent
argument_list|>
block|{
annotation|@
name|Override
DECL|method|transition (ApplicationImpl app, ApplicationEvent event)
specifier|public
name|void
name|transition
parameter_list|(
name|ApplicationImpl
name|app
parameter_list|,
name|ApplicationEvent
name|event
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Log Aggregation service failed to initialize, there will "
operator|+
literal|"be no logs for this application"
argument_list|)
expr_stmt|;
name|app
operator|.
name|dispatcher
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|ApplicationLocalizationEvent
argument_list|(
name|LocalizationEventType
operator|.
name|INIT_APPLICATION_RESOURCES
argument_list|,
name|app
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Handles INIT_CONTAINER events which request that we launch a new    * container. When we're still in the INITTING state, we simply    * queue these up. When we're in the RUNNING state, we pass along    * an ContainerInitEvent to the appropriate ContainerImpl.    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|class|InitContainerTransition
specifier|static
class|class
name|InitContainerTransition
implements|implements
name|SingleArcTransition
argument_list|<
name|ApplicationImpl
argument_list|,
name|ApplicationEvent
argument_list|>
block|{
annotation|@
name|Override
DECL|method|transition (ApplicationImpl app, ApplicationEvent event)
specifier|public
name|void
name|transition
parameter_list|(
name|ApplicationImpl
name|app
parameter_list|,
name|ApplicationEvent
name|event
parameter_list|)
block|{
name|ApplicationContainerInitEvent
name|initEvent
init|=
operator|(
name|ApplicationContainerInitEvent
operator|)
name|event
decl_stmt|;
name|Container
name|container
init|=
name|initEvent
operator|.
name|getContainer
argument_list|()
decl_stmt|;
name|app
operator|.
name|containers
operator|.
name|put
argument_list|(
name|container
operator|.
name|getContainer
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|,
name|container
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Adding "
operator|+
name|container
operator|.
name|getContainer
argument_list|()
operator|.
name|getId
argument_list|()
operator|+
literal|" to application "
operator|+
name|app
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|app
operator|.
name|getApplicationState
argument_list|()
condition|)
block|{
case|case
name|RUNNING
case|:
name|app
operator|.
name|dispatcher
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|ContainerInitEvent
argument_list|(
name|container
operator|.
name|getContainer
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|INITING
case|:
case|case
name|NEW
case|:
comment|// these get queued up and sent out in AppInitDoneTransition
break|break;
default|default:
assert|assert
literal|false
operator|:
literal|"Invalid state for InitContainerTransition: "
operator|+
name|app
operator|.
name|getApplicationState
argument_list|()
assert|;
block|}
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|class|AppInitDoneTransition
specifier|static
class|class
name|AppInitDoneTransition
implements|implements
name|SingleArcTransition
argument_list|<
name|ApplicationImpl
argument_list|,
name|ApplicationEvent
argument_list|>
block|{
annotation|@
name|Override
DECL|method|transition (ApplicationImpl app, ApplicationEvent event)
specifier|public
name|void
name|transition
parameter_list|(
name|ApplicationImpl
name|app
parameter_list|,
name|ApplicationEvent
name|event
parameter_list|)
block|{
comment|// Start all the containers waiting for ApplicationInit
for|for
control|(
name|Container
name|container
range|:
name|app
operator|.
name|containers
operator|.
name|values
argument_list|()
control|)
block|{
name|app
operator|.
name|dispatcher
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|ContainerInitEvent
argument_list|(
name|container
operator|.
name|getContainer
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|class|ContainerDoneTransition
specifier|static
specifier|final
class|class
name|ContainerDoneTransition
implements|implements
name|SingleArcTransition
argument_list|<
name|ApplicationImpl
argument_list|,
name|ApplicationEvent
argument_list|>
block|{
annotation|@
name|Override
DECL|method|transition (ApplicationImpl app, ApplicationEvent event)
specifier|public
name|void
name|transition
parameter_list|(
name|ApplicationImpl
name|app
parameter_list|,
name|ApplicationEvent
name|event
parameter_list|)
block|{
name|ApplicationContainerFinishedEvent
name|containerEvent
init|=
operator|(
name|ApplicationContainerFinishedEvent
operator|)
name|event
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|app
operator|.
name|containers
operator|.
name|remove
argument_list|(
name|containerEvent
operator|.
name|getContainerID
argument_list|()
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Removing unknown "
operator|+
name|containerEvent
operator|.
name|getContainerID
argument_list|()
operator|+
literal|" from application "
operator|+
name|app
operator|.
name|toString
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
literal|"Removing "
operator|+
name|containerEvent
operator|.
name|getContainerID
argument_list|()
operator|+
literal|" from application "
operator|+
name|app
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|handleAppFinishWithContainersCleanedup ()
name|void
name|handleAppFinishWithContainersCleanedup
parameter_list|()
block|{
comment|// Delete Application level resources
name|this
operator|.
name|dispatcher
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|ApplicationLocalizationEvent
argument_list|(
name|LocalizationEventType
operator|.
name|DESTROY_APPLICATION_RESOURCES
argument_list|,
name|this
argument_list|)
argument_list|)
expr_stmt|;
comment|// tell any auxiliary services that the app is done
name|this
operator|.
name|dispatcher
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|AuxServicesEvent
argument_list|(
name|AuxServicesEventType
operator|.
name|APPLICATION_STOP
argument_list|,
name|appId
argument_list|)
argument_list|)
expr_stmt|;
comment|// TODO: Trigger the LogsManager
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|class|AppFinishTriggeredTransition
specifier|static
class|class
name|AppFinishTriggeredTransition
implements|implements
name|MultipleArcTransition
argument_list|<
name|ApplicationImpl
argument_list|,
name|ApplicationEvent
argument_list|,
name|ApplicationState
argument_list|>
block|{
annotation|@
name|Override
DECL|method|transition (ApplicationImpl app, ApplicationEvent event)
specifier|public
name|ApplicationState
name|transition
parameter_list|(
name|ApplicationImpl
name|app
parameter_list|,
name|ApplicationEvent
name|event
parameter_list|)
block|{
if|if
condition|(
name|app
operator|.
name|containers
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// No container to cleanup. Cleanup app level resources.
name|app
operator|.
name|handleAppFinishWithContainersCleanedup
argument_list|()
expr_stmt|;
return|return
name|ApplicationState
operator|.
name|APPLICATION_RESOURCES_CLEANINGUP
return|;
block|}
comment|// Send event to ContainersLauncher to finish all the containers of this
comment|// application.
for|for
control|(
name|ContainerId
name|containerID
range|:
name|app
operator|.
name|containers
operator|.
name|keySet
argument_list|()
control|)
block|{
name|app
operator|.
name|dispatcher
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|ContainerKillEvent
argument_list|(
name|containerID
argument_list|,
literal|"Container killed on application-finish event from RM."
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|ApplicationState
operator|.
name|FINISHING_CONTAINERS_WAIT
return|;
block|}
block|}
DECL|class|AppFinishTransition
specifier|static
class|class
name|AppFinishTransition
implements|implements
name|MultipleArcTransition
argument_list|<
name|ApplicationImpl
argument_list|,
name|ApplicationEvent
argument_list|,
name|ApplicationState
argument_list|>
block|{
annotation|@
name|Override
DECL|method|transition (ApplicationImpl app, ApplicationEvent event)
specifier|public
name|ApplicationState
name|transition
parameter_list|(
name|ApplicationImpl
name|app
parameter_list|,
name|ApplicationEvent
name|event
parameter_list|)
block|{
name|ApplicationContainerFinishedEvent
name|containerFinishEvent
init|=
operator|(
name|ApplicationContainerFinishedEvent
operator|)
name|event
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Removing "
operator|+
name|containerFinishEvent
operator|.
name|getContainerID
argument_list|()
operator|+
literal|" from application "
operator|+
name|app
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|app
operator|.
name|containers
operator|.
name|remove
argument_list|(
name|containerFinishEvent
operator|.
name|getContainerID
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|app
operator|.
name|containers
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// All containers are cleanedup.
name|app
operator|.
name|handleAppFinishWithContainersCleanedup
argument_list|()
expr_stmt|;
return|return
name|ApplicationState
operator|.
name|APPLICATION_RESOURCES_CLEANINGUP
return|;
block|}
return|return
name|ApplicationState
operator|.
name|FINISHING_CONTAINERS_WAIT
return|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|class|AppCompletelyDoneTransition
specifier|static
class|class
name|AppCompletelyDoneTransition
implements|implements
name|SingleArcTransition
argument_list|<
name|ApplicationImpl
argument_list|,
name|ApplicationEvent
argument_list|>
block|{
annotation|@
name|Override
DECL|method|transition (ApplicationImpl app, ApplicationEvent event)
specifier|public
name|void
name|transition
parameter_list|(
name|ApplicationImpl
name|app
parameter_list|,
name|ApplicationEvent
name|event
parameter_list|)
block|{
comment|// Inform the ContainerTokenSecretManager
if|if
condition|(
name|UserGroupInformation
operator|.
name|isSecurityEnabled
argument_list|()
condition|)
block|{
name|app
operator|.
name|context
operator|.
name|getContainerTokenSecretManager
argument_list|()
operator|.
name|appFinished
argument_list|(
name|app
operator|.
name|appId
argument_list|)
expr_stmt|;
block|}
comment|// Inform the logService
name|app
operator|.
name|dispatcher
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|LogHandlerAppFinishedEvent
argument_list|(
name|app
operator|.
name|appId
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|AppLogsAggregatedTransition
specifier|static
class|class
name|AppLogsAggregatedTransition
implements|implements
name|SingleArcTransition
argument_list|<
name|ApplicationImpl
argument_list|,
name|ApplicationEvent
argument_list|>
block|{
annotation|@
name|Override
DECL|method|transition (ApplicationImpl app, ApplicationEvent event)
specifier|public
name|void
name|transition
parameter_list|(
name|ApplicationImpl
name|app
parameter_list|,
name|ApplicationEvent
name|event
parameter_list|)
block|{
name|ApplicationId
name|appId
init|=
name|event
operator|.
name|getApplicationID
argument_list|()
decl_stmt|;
name|app
operator|.
name|context
operator|.
name|getApplications
argument_list|()
operator|.
name|remove
argument_list|(
name|appId
argument_list|)
expr_stmt|;
name|app
operator|.
name|aclsManager
operator|.
name|removeApplication
argument_list|(
name|appId
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|handle (ApplicationEvent event)
specifier|public
name|void
name|handle
parameter_list|(
name|ApplicationEvent
name|event
parameter_list|)
block|{
name|this
operator|.
name|writeLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|ApplicationId
name|applicationID
init|=
name|event
operator|.
name|getApplicationID
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Processing "
operator|+
name|applicationID
operator|+
literal|" of type "
operator|+
name|event
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|ApplicationState
name|oldState
init|=
name|stateMachine
operator|.
name|getCurrentState
argument_list|()
decl_stmt|;
name|ApplicationState
name|newState
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|// queue event requesting init of the same app
name|newState
operator|=
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
literal|"Application "
operator|+
name|applicationID
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
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|appId
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

