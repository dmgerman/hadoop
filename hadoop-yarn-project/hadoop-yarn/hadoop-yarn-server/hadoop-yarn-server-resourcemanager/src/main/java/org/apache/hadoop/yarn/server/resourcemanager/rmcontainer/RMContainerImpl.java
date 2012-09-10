begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.rmcontainer
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
name|resourcemanager
operator|.
name|rmcontainer
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ApplicationAttemptId
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
name|api
operator|.
name|records
operator|.
name|NodeId
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
name|Priority
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
name|Resource
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
name|resourcemanager
operator|.
name|rmapp
operator|.
name|attempt
operator|.
name|event
operator|.
name|RMAppAttemptContainerAcquiredEvent
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
name|resourcemanager
operator|.
name|rmapp
operator|.
name|attempt
operator|.
name|event
operator|.
name|RMAppAttemptContainerAllocatedEvent
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
name|resourcemanager
operator|.
name|rmapp
operator|.
name|attempt
operator|.
name|event
operator|.
name|RMAppAttemptContainerFinishedEvent
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
name|resourcemanager
operator|.
name|rmnode
operator|.
name|RMNodeCleanContainerEvent
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

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|,
literal|"rawtypes"
block|}
argument_list|)
DECL|class|RMContainerImpl
specifier|public
class|class
name|RMContainerImpl
implements|implements
name|RMContainer
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
name|RMContainerImpl
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|StateMachineFactory
argument_list|<
name|RMContainerImpl
argument_list|,
name|RMContainerState
argument_list|,
name|RMContainerEventType
argument_list|,
name|RMContainerEvent
argument_list|>
DECL|field|stateMachineFactory
name|stateMachineFactory
init|=
operator|new
name|StateMachineFactory
argument_list|<
name|RMContainerImpl
argument_list|,
name|RMContainerState
argument_list|,
name|RMContainerEventType
argument_list|,
name|RMContainerEvent
argument_list|>
argument_list|(
name|RMContainerState
operator|.
name|NEW
argument_list|)
comment|// Transitions from NEW state
operator|.
name|addTransition
argument_list|(
name|RMContainerState
operator|.
name|NEW
argument_list|,
name|RMContainerState
operator|.
name|ALLOCATED
argument_list|,
name|RMContainerEventType
operator|.
name|START
argument_list|,
operator|new
name|ContainerStartedTransition
argument_list|()
argument_list|)
operator|.
name|addTransition
argument_list|(
name|RMContainerState
operator|.
name|NEW
argument_list|,
name|RMContainerState
operator|.
name|KILLED
argument_list|,
name|RMContainerEventType
operator|.
name|KILL
argument_list|)
operator|.
name|addTransition
argument_list|(
name|RMContainerState
operator|.
name|NEW
argument_list|,
name|RMContainerState
operator|.
name|RESERVED
argument_list|,
name|RMContainerEventType
operator|.
name|RESERVED
argument_list|,
operator|new
name|ContainerReservedTransition
argument_list|()
argument_list|)
comment|// Transitions from RESERVED state
operator|.
name|addTransition
argument_list|(
name|RMContainerState
operator|.
name|RESERVED
argument_list|,
name|RMContainerState
operator|.
name|RESERVED
argument_list|,
name|RMContainerEventType
operator|.
name|RESERVED
argument_list|,
operator|new
name|ContainerReservedTransition
argument_list|()
argument_list|)
operator|.
name|addTransition
argument_list|(
name|RMContainerState
operator|.
name|RESERVED
argument_list|,
name|RMContainerState
operator|.
name|ALLOCATED
argument_list|,
name|RMContainerEventType
operator|.
name|START
argument_list|,
operator|new
name|ContainerStartedTransition
argument_list|()
argument_list|)
operator|.
name|addTransition
argument_list|(
name|RMContainerState
operator|.
name|RESERVED
argument_list|,
name|RMContainerState
operator|.
name|KILLED
argument_list|,
name|RMContainerEventType
operator|.
name|KILL
argument_list|)
comment|// nothing to do
operator|.
name|addTransition
argument_list|(
name|RMContainerState
operator|.
name|RESERVED
argument_list|,
name|RMContainerState
operator|.
name|RELEASED
argument_list|,
name|RMContainerEventType
operator|.
name|RELEASED
argument_list|)
comment|// nothing to do
comment|// Transitions from ALLOCATED state
operator|.
name|addTransition
argument_list|(
name|RMContainerState
operator|.
name|ALLOCATED
argument_list|,
name|RMContainerState
operator|.
name|ACQUIRED
argument_list|,
name|RMContainerEventType
operator|.
name|ACQUIRED
argument_list|,
operator|new
name|AcquiredTransition
argument_list|()
argument_list|)
operator|.
name|addTransition
argument_list|(
name|RMContainerState
operator|.
name|ALLOCATED
argument_list|,
name|RMContainerState
operator|.
name|EXPIRED
argument_list|,
name|RMContainerEventType
operator|.
name|EXPIRE
argument_list|,
operator|new
name|FinishedTransition
argument_list|()
argument_list|)
operator|.
name|addTransition
argument_list|(
name|RMContainerState
operator|.
name|ALLOCATED
argument_list|,
name|RMContainerState
operator|.
name|KILLED
argument_list|,
name|RMContainerEventType
operator|.
name|KILL
argument_list|,
operator|new
name|FinishedTransition
argument_list|()
argument_list|)
comment|// Transitions from ACQUIRED state
operator|.
name|addTransition
argument_list|(
name|RMContainerState
operator|.
name|ACQUIRED
argument_list|,
name|RMContainerState
operator|.
name|RUNNING
argument_list|,
name|RMContainerEventType
operator|.
name|LAUNCHED
argument_list|,
operator|new
name|LaunchedTransition
argument_list|()
argument_list|)
operator|.
name|addTransition
argument_list|(
name|RMContainerState
operator|.
name|ACQUIRED
argument_list|,
name|RMContainerState
operator|.
name|COMPLETED
argument_list|,
name|RMContainerEventType
operator|.
name|FINISHED
argument_list|,
operator|new
name|ContainerFinishedAtAcquiredState
argument_list|()
argument_list|)
operator|.
name|addTransition
argument_list|(
name|RMContainerState
operator|.
name|ACQUIRED
argument_list|,
name|RMContainerState
operator|.
name|RELEASED
argument_list|,
name|RMContainerEventType
operator|.
name|RELEASED
argument_list|,
operator|new
name|KillTransition
argument_list|()
argument_list|)
operator|.
name|addTransition
argument_list|(
name|RMContainerState
operator|.
name|ACQUIRED
argument_list|,
name|RMContainerState
operator|.
name|EXPIRED
argument_list|,
name|RMContainerEventType
operator|.
name|EXPIRE
argument_list|,
operator|new
name|KillTransition
argument_list|()
argument_list|)
operator|.
name|addTransition
argument_list|(
name|RMContainerState
operator|.
name|ACQUIRED
argument_list|,
name|RMContainerState
operator|.
name|KILLED
argument_list|,
name|RMContainerEventType
operator|.
name|KILL
argument_list|,
operator|new
name|KillTransition
argument_list|()
argument_list|)
comment|// Transitions from RUNNING state
operator|.
name|addTransition
argument_list|(
name|RMContainerState
operator|.
name|RUNNING
argument_list|,
name|RMContainerState
operator|.
name|COMPLETED
argument_list|,
name|RMContainerEventType
operator|.
name|FINISHED
argument_list|,
operator|new
name|FinishedTransition
argument_list|()
argument_list|)
operator|.
name|addTransition
argument_list|(
name|RMContainerState
operator|.
name|RUNNING
argument_list|,
name|RMContainerState
operator|.
name|KILLED
argument_list|,
name|RMContainerEventType
operator|.
name|KILL
argument_list|,
operator|new
name|KillTransition
argument_list|()
argument_list|)
operator|.
name|addTransition
argument_list|(
name|RMContainerState
operator|.
name|RUNNING
argument_list|,
name|RMContainerState
operator|.
name|RELEASED
argument_list|,
name|RMContainerEventType
operator|.
name|RELEASED
argument_list|,
operator|new
name|KillTransition
argument_list|()
argument_list|)
comment|// Transitions from COMPLETED state
operator|.
name|addTransition
argument_list|(
name|RMContainerState
operator|.
name|COMPLETED
argument_list|,
name|RMContainerState
operator|.
name|COMPLETED
argument_list|,
name|EnumSet
operator|.
name|of
argument_list|(
name|RMContainerEventType
operator|.
name|RELEASED
argument_list|,
name|RMContainerEventType
operator|.
name|KILL
argument_list|)
argument_list|)
comment|// Transitions from EXPIRED state
operator|.
name|addTransition
argument_list|(
name|RMContainerState
operator|.
name|EXPIRED
argument_list|,
name|RMContainerState
operator|.
name|EXPIRED
argument_list|,
name|EnumSet
operator|.
name|of
argument_list|(
name|RMContainerEventType
operator|.
name|RELEASED
argument_list|,
name|RMContainerEventType
operator|.
name|KILL
argument_list|)
argument_list|)
comment|// Transitions from RELEASED state
operator|.
name|addTransition
argument_list|(
name|RMContainerState
operator|.
name|RELEASED
argument_list|,
name|RMContainerState
operator|.
name|RELEASED
argument_list|,
name|EnumSet
operator|.
name|of
argument_list|(
name|RMContainerEventType
operator|.
name|RELEASED
argument_list|,
name|RMContainerEventType
operator|.
name|KILL
argument_list|,
name|RMContainerEventType
operator|.
name|FINISHED
argument_list|)
argument_list|)
comment|// Transitions from KILLED state
operator|.
name|addTransition
argument_list|(
name|RMContainerState
operator|.
name|KILLED
argument_list|,
name|RMContainerState
operator|.
name|KILLED
argument_list|,
name|EnumSet
operator|.
name|of
argument_list|(
name|RMContainerEventType
operator|.
name|RELEASED
argument_list|,
name|RMContainerEventType
operator|.
name|KILL
argument_list|,
name|RMContainerEventType
operator|.
name|FINISHED
argument_list|)
argument_list|)
comment|// create the topology tables
operator|.
name|installTopology
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|StateMachine
argument_list|<
name|RMContainerState
argument_list|,
name|RMContainerEventType
argument_list|,
DECL|field|stateMachine
name|RMContainerEvent
argument_list|>
name|stateMachine
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
DECL|field|containerId
specifier|private
specifier|final
name|ContainerId
name|containerId
decl_stmt|;
DECL|field|appAttemptId
specifier|private
specifier|final
name|ApplicationAttemptId
name|appAttemptId
decl_stmt|;
DECL|field|nodeId
specifier|private
specifier|final
name|NodeId
name|nodeId
decl_stmt|;
DECL|field|container
specifier|private
specifier|final
name|Container
name|container
decl_stmt|;
DECL|field|eventHandler
specifier|private
specifier|final
name|EventHandler
name|eventHandler
decl_stmt|;
DECL|field|containerAllocationExpirer
specifier|private
specifier|final
name|ContainerAllocationExpirer
name|containerAllocationExpirer
decl_stmt|;
DECL|field|reservedResource
specifier|private
name|Resource
name|reservedResource
decl_stmt|;
DECL|field|reservedNode
specifier|private
name|NodeId
name|reservedNode
decl_stmt|;
DECL|field|reservedPriority
specifier|private
name|Priority
name|reservedPriority
decl_stmt|;
DECL|method|RMContainerImpl (Container container, ApplicationAttemptId appAttemptId, NodeId nodeId, EventHandler handler, ContainerAllocationExpirer containerAllocationExpirer)
specifier|public
name|RMContainerImpl
parameter_list|(
name|Container
name|container
parameter_list|,
name|ApplicationAttemptId
name|appAttemptId
parameter_list|,
name|NodeId
name|nodeId
parameter_list|,
name|EventHandler
name|handler
parameter_list|,
name|ContainerAllocationExpirer
name|containerAllocationExpirer
parameter_list|)
block|{
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
name|this
operator|.
name|containerId
operator|=
name|container
operator|.
name|getId
argument_list|()
expr_stmt|;
name|this
operator|.
name|nodeId
operator|=
name|nodeId
expr_stmt|;
name|this
operator|.
name|container
operator|=
name|container
expr_stmt|;
name|this
operator|.
name|appAttemptId
operator|=
name|appAttemptId
expr_stmt|;
name|this
operator|.
name|eventHandler
operator|=
name|handler
expr_stmt|;
name|this
operator|.
name|containerAllocationExpirer
operator|=
name|containerAllocationExpirer
expr_stmt|;
name|ReentrantReadWriteLock
name|lock
init|=
operator|new
name|ReentrantReadWriteLock
argument_list|()
decl_stmt|;
name|this
operator|.
name|readLock
operator|=
name|lock
operator|.
name|readLock
argument_list|()
expr_stmt|;
name|this
operator|.
name|writeLock
operator|=
name|lock
operator|.
name|writeLock
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getContainerId ()
specifier|public
name|ContainerId
name|getContainerId
parameter_list|()
block|{
return|return
name|this
operator|.
name|containerId
return|;
block|}
annotation|@
name|Override
DECL|method|getApplicationAttemptId ()
specifier|public
name|ApplicationAttemptId
name|getApplicationAttemptId
parameter_list|()
block|{
return|return
name|this
operator|.
name|appAttemptId
return|;
block|}
annotation|@
name|Override
DECL|method|getContainer ()
specifier|public
name|Container
name|getContainer
parameter_list|()
block|{
return|return
name|this
operator|.
name|container
return|;
block|}
annotation|@
name|Override
DECL|method|getState ()
specifier|public
name|RMContainerState
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
DECL|method|getReservedResource ()
specifier|public
name|Resource
name|getReservedResource
parameter_list|()
block|{
return|return
name|reservedResource
return|;
block|}
annotation|@
name|Override
DECL|method|getReservedNode ()
specifier|public
name|NodeId
name|getReservedNode
parameter_list|()
block|{
return|return
name|reservedNode
return|;
block|}
annotation|@
name|Override
DECL|method|getReservedPriority ()
specifier|public
name|Priority
name|getReservedPriority
parameter_list|()
block|{
return|return
name|reservedPriority
return|;
block|}
annotation|@
name|Override
DECL|method|handle (RMContainerEvent event)
specifier|public
name|void
name|handle
parameter_list|(
name|RMContainerEvent
name|event
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Processing "
operator|+
name|event
operator|.
name|getContainerId
argument_list|()
operator|+
literal|" of type "
operator|+
name|event
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|writeLock
operator|.
name|lock
argument_list|()
expr_stmt|;
name|RMContainerState
name|oldState
init|=
name|getState
argument_list|()
decl_stmt|;
try|try
block|{
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
name|error
argument_list|(
literal|"Can't handle this event at current state"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|error
argument_list|(
literal|"Invalid event "
operator|+
name|event
operator|.
name|getType
argument_list|()
operator|+
literal|" on container "
operator|+
name|this
operator|.
name|containerId
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|oldState
operator|!=
name|getState
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|event
operator|.
name|getContainerId
argument_list|()
operator|+
literal|" Container Transitioned from "
operator|+
name|oldState
operator|+
literal|" to "
operator|+
name|getState
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|writeLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|BaseTransition
specifier|private
specifier|static
class|class
name|BaseTransition
implements|implements
name|SingleArcTransition
argument_list|<
name|RMContainerImpl
argument_list|,
name|RMContainerEvent
argument_list|>
block|{
annotation|@
name|Override
DECL|method|transition (RMContainerImpl cont, RMContainerEvent event)
specifier|public
name|void
name|transition
parameter_list|(
name|RMContainerImpl
name|cont
parameter_list|,
name|RMContainerEvent
name|event
parameter_list|)
block|{      }
block|}
DECL|class|ContainerReservedTransition
specifier|private
specifier|static
specifier|final
class|class
name|ContainerReservedTransition
extends|extends
name|BaseTransition
block|{
annotation|@
name|Override
DECL|method|transition (RMContainerImpl container, RMContainerEvent event)
specifier|public
name|void
name|transition
parameter_list|(
name|RMContainerImpl
name|container
parameter_list|,
name|RMContainerEvent
name|event
parameter_list|)
block|{
name|RMContainerReservedEvent
name|e
init|=
operator|(
name|RMContainerReservedEvent
operator|)
name|event
decl_stmt|;
name|container
operator|.
name|reservedResource
operator|=
name|e
operator|.
name|getReservedResource
argument_list|()
expr_stmt|;
name|container
operator|.
name|reservedNode
operator|=
name|e
operator|.
name|getReservedNode
argument_list|()
expr_stmt|;
name|container
operator|.
name|reservedPriority
operator|=
name|e
operator|.
name|getReservedPriority
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|ContainerStartedTransition
specifier|private
specifier|static
specifier|final
class|class
name|ContainerStartedTransition
extends|extends
name|BaseTransition
block|{
annotation|@
name|Override
DECL|method|transition (RMContainerImpl container, RMContainerEvent event)
specifier|public
name|void
name|transition
parameter_list|(
name|RMContainerImpl
name|container
parameter_list|,
name|RMContainerEvent
name|event
parameter_list|)
block|{
name|container
operator|.
name|eventHandler
operator|.
name|handle
argument_list|(
operator|new
name|RMAppAttemptContainerAllocatedEvent
argument_list|(
name|container
operator|.
name|appAttemptId
argument_list|,
name|container
operator|.
name|container
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|AcquiredTransition
specifier|private
specifier|static
specifier|final
class|class
name|AcquiredTransition
extends|extends
name|BaseTransition
block|{
annotation|@
name|Override
DECL|method|transition (RMContainerImpl container, RMContainerEvent event)
specifier|public
name|void
name|transition
parameter_list|(
name|RMContainerImpl
name|container
parameter_list|,
name|RMContainerEvent
name|event
parameter_list|)
block|{
comment|// Register with containerAllocationExpirer.
name|container
operator|.
name|containerAllocationExpirer
operator|.
name|register
argument_list|(
name|container
operator|.
name|getContainerId
argument_list|()
argument_list|)
expr_stmt|;
comment|// Tell the appAttempt
name|container
operator|.
name|eventHandler
operator|.
name|handle
argument_list|(
operator|new
name|RMAppAttemptContainerAcquiredEvent
argument_list|(
name|container
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|,
name|container
operator|.
name|getContainer
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|LaunchedTransition
specifier|private
specifier|static
specifier|final
class|class
name|LaunchedTransition
extends|extends
name|BaseTransition
block|{
annotation|@
name|Override
DECL|method|transition (RMContainerImpl container, RMContainerEvent event)
specifier|public
name|void
name|transition
parameter_list|(
name|RMContainerImpl
name|container
parameter_list|,
name|RMContainerEvent
name|event
parameter_list|)
block|{
comment|// Unregister from containerAllocationExpirer.
name|container
operator|.
name|containerAllocationExpirer
operator|.
name|unregister
argument_list|(
name|container
operator|.
name|getContainerId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|FinishedTransition
specifier|private
specifier|static
class|class
name|FinishedTransition
extends|extends
name|BaseTransition
block|{
annotation|@
name|Override
DECL|method|transition (RMContainerImpl container, RMContainerEvent event)
specifier|public
name|void
name|transition
parameter_list|(
name|RMContainerImpl
name|container
parameter_list|,
name|RMContainerEvent
name|event
parameter_list|)
block|{
name|RMContainerFinishedEvent
name|finishedEvent
init|=
operator|(
name|RMContainerFinishedEvent
operator|)
name|event
decl_stmt|;
comment|// Update container-status for diagnostics. Today we completely
comment|// replace it on finish. We may just need to update diagnostics.
name|container
operator|.
name|container
operator|.
name|setContainerStatus
argument_list|(
name|finishedEvent
operator|.
name|getRemoteContainerStatus
argument_list|()
argument_list|)
expr_stmt|;
comment|// Inform AppAttempt
name|container
operator|.
name|eventHandler
operator|.
name|handle
argument_list|(
operator|new
name|RMAppAttemptContainerFinishedEvent
argument_list|(
name|container
operator|.
name|appAttemptId
argument_list|,
name|container
operator|.
name|container
operator|.
name|getContainerStatus
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|ContainerFinishedAtAcquiredState
specifier|private
specifier|static
specifier|final
class|class
name|ContainerFinishedAtAcquiredState
extends|extends
name|FinishedTransition
block|{
annotation|@
name|Override
DECL|method|transition (RMContainerImpl container, RMContainerEvent event)
specifier|public
name|void
name|transition
parameter_list|(
name|RMContainerImpl
name|container
parameter_list|,
name|RMContainerEvent
name|event
parameter_list|)
block|{
comment|// Unregister from containerAllocationExpirer.
name|container
operator|.
name|containerAllocationExpirer
operator|.
name|unregister
argument_list|(
name|container
operator|.
name|getContainerId
argument_list|()
argument_list|)
expr_stmt|;
comment|// Inform AppAttempt
name|super
operator|.
name|transition
argument_list|(
name|container
argument_list|,
name|event
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|KillTransition
specifier|private
specifier|static
specifier|final
class|class
name|KillTransition
extends|extends
name|FinishedTransition
block|{
annotation|@
name|Override
DECL|method|transition (RMContainerImpl container, RMContainerEvent event)
specifier|public
name|void
name|transition
parameter_list|(
name|RMContainerImpl
name|container
parameter_list|,
name|RMContainerEvent
name|event
parameter_list|)
block|{
comment|// Unregister from containerAllocationExpirer.
name|container
operator|.
name|containerAllocationExpirer
operator|.
name|unregister
argument_list|(
name|container
operator|.
name|getContainerId
argument_list|()
argument_list|)
expr_stmt|;
comment|// Inform node
name|container
operator|.
name|eventHandler
operator|.
name|handle
argument_list|(
operator|new
name|RMNodeCleanContainerEvent
argument_list|(
name|container
operator|.
name|nodeId
argument_list|,
name|container
operator|.
name|containerId
argument_list|)
argument_list|)
expr_stmt|;
comment|// Inform appAttempt
name|super
operator|.
name|transition
argument_list|(
name|container
argument_list|,
name|event
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

