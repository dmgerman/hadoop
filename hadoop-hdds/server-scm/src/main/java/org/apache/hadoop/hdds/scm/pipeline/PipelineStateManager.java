begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm.pipeline
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|scm
operator|.
name|pipeline
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|HddsProtos
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|HddsProtos
operator|.
name|LifeCycleEvent
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|HddsProtos
operator|.
name|LifeCycleState
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
name|hdds
operator|.
name|scm
operator|.
name|ScmConfigKeys
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
name|hdds
operator|.
name|scm
operator|.
name|container
operator|.
name|ContainerID
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
name|hdds
operator|.
name|scm
operator|.
name|exceptions
operator|.
name|SCMException
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
name|ozone
operator|.
name|common
operator|.
name|statemachine
operator|.
name|InvalidStateTransitionException
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
name|ozone
operator|.
name|common
operator|.
name|statemachine
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
name|ozone
operator|.
name|lease
operator|.
name|LeaseManager
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
name|TimeUnit
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|scm
operator|.
name|exceptions
operator|.
name|SCMException
operator|.
name|ResultCodes
operator|.
name|FAILED_TO_CHANGE_PIPELINE_STATE
import|;
end_import

begin_comment
comment|/**  * Manages the state of pipelines in SCM. All write operations like pipeline  * creation, removal and updates should come via SCMPipelineManager.  * PipelineStateMap class holds the data structures related to pipeline and its  * state. All the read and write operations in PipelineStateMap are protected  * by a read write lock.  */
end_comment

begin_class
DECL|class|PipelineStateManager
class|class
name|PipelineStateManager
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|scm
operator|.
name|pipelines
operator|.
name|PipelineStateManager
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|pipelineStateMap
specifier|private
specifier|final
name|PipelineStateMap
name|pipelineStateMap
decl_stmt|;
DECL|field|stateMachine
specifier|private
specifier|final
name|StateMachine
argument_list|<
name|LifeCycleState
argument_list|,
name|LifeCycleEvent
argument_list|>
name|stateMachine
decl_stmt|;
DECL|field|pipelineLeaseManager
specifier|private
specifier|final
name|LeaseManager
argument_list|<
name|Pipeline
argument_list|>
name|pipelineLeaseManager
decl_stmt|;
DECL|method|PipelineStateManager (Configuration conf)
name|PipelineStateManager
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|this
operator|.
name|pipelineStateMap
operator|=
operator|new
name|PipelineStateMap
argument_list|()
expr_stmt|;
name|Set
argument_list|<
name|LifeCycleState
argument_list|>
name|finalStates
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|long
name|pipelineCreationLeaseTimeout
init|=
name|conf
operator|.
name|getTimeDuration
argument_list|(
name|ScmConfigKeys
operator|.
name|OZONE_SCM_PIPELINE_CREATION_LEASE_TIMEOUT
argument_list|,
name|ScmConfigKeys
operator|.
name|OZONE_SCM_PIPELINE_CREATION_LEASE_TIMEOUT_DEFAULT
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
comment|// TODO: Use LeaseManager for creation of pipelines.
comment|// Add pipeline initialization logic.
name|this
operator|.
name|pipelineLeaseManager
operator|=
operator|new
name|LeaseManager
argument_list|<>
argument_list|(
literal|"PipelineCreation"
argument_list|,
name|pipelineCreationLeaseTimeout
argument_list|)
expr_stmt|;
name|this
operator|.
name|pipelineLeaseManager
operator|.
name|start
argument_list|()
expr_stmt|;
name|finalStates
operator|.
name|add
argument_list|(
name|LifeCycleState
operator|.
name|CLOSED
argument_list|)
expr_stmt|;
name|this
operator|.
name|stateMachine
operator|=
operator|new
name|StateMachine
argument_list|<>
argument_list|(
name|LifeCycleState
operator|.
name|ALLOCATED
argument_list|,
name|finalStates
argument_list|)
expr_stmt|;
name|initializeStateMachine
argument_list|()
expr_stmt|;
block|}
comment|/*    * Event and State Transition Mapping.    *    * State: ALLOCATED ---------------> CREATING    * Event:                CREATE    *    * State: CREATING  ---------------> OPEN    * Event:               CREATED    *    * State: OPEN      ---------------> CLOSING    * Event:               FINALIZE    *    * State: CLOSING   ---------------> CLOSED    * Event:                CLOSE    *    * State: CREATING  ---------------> CLOSED    * Event:               TIMEOUT    *    *    * Container State Flow:    *    * [ALLOCATED]---->[CREATING]------>[OPEN]-------->[CLOSING]    *            (CREATE)     | (CREATED)     (FINALIZE)   |    *                         |                            |    *                         |                            |    *                         |(TIMEOUT)                   |(CLOSE)    *                         |                            |    *                         +--------> [CLOSED]<--------+    */
comment|/**    * Add javadoc.    */
DECL|method|initializeStateMachine ()
specifier|private
name|void
name|initializeStateMachine
parameter_list|()
block|{
name|stateMachine
operator|.
name|addTransition
argument_list|(
name|LifeCycleState
operator|.
name|ALLOCATED
argument_list|,
name|LifeCycleState
operator|.
name|CREATING
argument_list|,
name|LifeCycleEvent
operator|.
name|CREATE
argument_list|)
expr_stmt|;
name|stateMachine
operator|.
name|addTransition
argument_list|(
name|LifeCycleState
operator|.
name|CREATING
argument_list|,
name|LifeCycleState
operator|.
name|OPEN
argument_list|,
name|LifeCycleEvent
operator|.
name|CREATED
argument_list|)
expr_stmt|;
name|stateMachine
operator|.
name|addTransition
argument_list|(
name|LifeCycleState
operator|.
name|OPEN
argument_list|,
name|LifeCycleState
operator|.
name|CLOSING
argument_list|,
name|LifeCycleEvent
operator|.
name|FINALIZE
argument_list|)
expr_stmt|;
name|stateMachine
operator|.
name|addTransition
argument_list|(
name|LifeCycleState
operator|.
name|CLOSING
argument_list|,
name|LifeCycleState
operator|.
name|CLOSED
argument_list|,
name|LifeCycleEvent
operator|.
name|CLOSE
argument_list|)
expr_stmt|;
name|stateMachine
operator|.
name|addTransition
argument_list|(
name|LifeCycleState
operator|.
name|CREATING
argument_list|,
name|LifeCycleState
operator|.
name|CLOSED
argument_list|,
name|LifeCycleEvent
operator|.
name|TIMEOUT
argument_list|)
expr_stmt|;
block|}
DECL|method|updatePipelineState (PipelineID pipelineID, LifeCycleEvent event)
name|Pipeline
name|updatePipelineState
parameter_list|(
name|PipelineID
name|pipelineID
parameter_list|,
name|LifeCycleEvent
name|event
parameter_list|)
throws|throws
name|IOException
block|{
name|Pipeline
name|pipeline
init|=
literal|null
decl_stmt|;
try|try
block|{
name|pipeline
operator|=
name|pipelineStateMap
operator|.
name|getPipeline
argument_list|(
name|pipelineID
argument_list|)
expr_stmt|;
name|LifeCycleState
name|newState
init|=
name|stateMachine
operator|.
name|getNextState
argument_list|(
name|pipeline
operator|.
name|getLifeCycleState
argument_list|()
argument_list|,
name|event
argument_list|)
decl_stmt|;
return|return
name|pipelineStateMap
operator|.
name|updatePipelineState
argument_list|(
name|pipeline
operator|.
name|getID
argument_list|()
argument_list|,
name|newState
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|InvalidStateTransitionException
name|ex
parameter_list|)
block|{
name|String
name|error
init|=
name|String
operator|.
name|format
argument_list|(
literal|"Failed to update pipeline state %s, "
operator|+
literal|"reason: invalid state transition from state: %s upon "
operator|+
literal|"event: %s."
argument_list|,
name|pipeline
operator|.
name|getID
argument_list|()
argument_list|,
name|pipeline
operator|.
name|getLifeCycleState
argument_list|()
argument_list|,
name|event
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|error
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|SCMException
argument_list|(
name|error
argument_list|,
name|FAILED_TO_CHANGE_PIPELINE_STATE
argument_list|)
throw|;
block|}
block|}
DECL|method|addPipeline (Pipeline pipeline)
name|void
name|addPipeline
parameter_list|(
name|Pipeline
name|pipeline
parameter_list|)
throws|throws
name|IOException
block|{
name|pipelineStateMap
operator|.
name|addPipeline
argument_list|(
name|pipeline
argument_list|)
expr_stmt|;
block|}
DECL|method|addContainerToPipeline (PipelineID pipelineId, ContainerID containerID)
name|void
name|addContainerToPipeline
parameter_list|(
name|PipelineID
name|pipelineId
parameter_list|,
name|ContainerID
name|containerID
parameter_list|)
throws|throws
name|IOException
block|{
name|pipelineStateMap
operator|.
name|addContainerToPipeline
argument_list|(
name|pipelineId
argument_list|,
name|containerID
argument_list|)
expr_stmt|;
block|}
DECL|method|getPipeline (PipelineID pipelineID)
name|Pipeline
name|getPipeline
parameter_list|(
name|PipelineID
name|pipelineID
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|pipelineStateMap
operator|.
name|getPipeline
argument_list|(
name|pipelineID
argument_list|)
return|;
block|}
DECL|method|getPipelines (HddsProtos.ReplicationType type)
name|List
argument_list|<
name|Pipeline
argument_list|>
name|getPipelines
parameter_list|(
name|HddsProtos
operator|.
name|ReplicationType
name|type
parameter_list|)
block|{
return|return
name|pipelineStateMap
operator|.
name|getPipelines
argument_list|(
name|type
argument_list|)
return|;
block|}
DECL|method|getContainers (PipelineID pipelineID)
name|Set
argument_list|<
name|ContainerID
argument_list|>
name|getContainers
parameter_list|(
name|PipelineID
name|pipelineID
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|pipelineStateMap
operator|.
name|getContainers
argument_list|(
name|pipelineID
argument_list|)
return|;
block|}
DECL|method|removePipeline (PipelineID pipelineID)
name|void
name|removePipeline
parameter_list|(
name|PipelineID
name|pipelineID
parameter_list|)
throws|throws
name|IOException
block|{
name|pipelineStateMap
operator|.
name|removePipeline
argument_list|(
name|pipelineID
argument_list|)
expr_stmt|;
block|}
DECL|method|removeContainerFromPipeline (PipelineID pipelineID, ContainerID containerID)
name|void
name|removeContainerFromPipeline
parameter_list|(
name|PipelineID
name|pipelineID
parameter_list|,
name|ContainerID
name|containerID
parameter_list|)
throws|throws
name|IOException
block|{
name|pipelineStateMap
operator|.
name|removeContainerFromPipeline
argument_list|(
name|pipelineID
argument_list|,
name|containerID
argument_list|)
expr_stmt|;
block|}
DECL|method|close ()
name|void
name|close
parameter_list|()
block|{
name|pipelineLeaseManager
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

