begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm.container
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
name|container
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
name|conf
operator|.
name|StorageUnit
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
name|pipeline
operator|.
name|Pipeline
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
name|states
operator|.
name|ContainerState
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
name|states
operator|.
name|ContainerStateMap
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
name|hdds
operator|.
name|scm
operator|.
name|pipeline
operator|.
name|PipelineManager
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
name|protocol
operator|.
name|proto
operator|.
name|HddsProtos
operator|.
name|ReplicationFactor
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
name|ReplicationType
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
name|util
operator|.
name|Time
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|NavigableSet
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
name|atomic
operator|.
name|AtomicLong
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
name|FAILED_TO_CHANGE_CONTAINER_STATE
import|;
end_import

begin_comment
comment|/**  * A container state manager keeps track of container states and returns  * containers that match various queries.  *<p>  * This state machine is driven by a combination of server and client actions.  *<p>  * This is how a create container happens: 1. When a container is created, the  * Server(or SCM) marks that Container as ALLOCATED state. In this state, SCM  * has chosen a pipeline for container to live on. However, the container is not  * created yet. This container along with the pipeline is returned to the  * client.  *<p>  * 2. The client when it sees the Container state as ALLOCATED understands that  * container needs to be created on the specified pipeline. The client lets the  * SCM know that saw this flag and is initiating the on the data nodes.  *<p>  * This is done by calling into notifyObjectCreation(ContainerName,  * BEGIN_CREATE) flag. When SCM gets this call, SCM puts the container state  * into CREATING. All this state means is that SCM told Client to create a  * container and client saw that request.  *<p>  * 3. Then client makes calls to datanodes directly, asking the datanodes to  * create the container. This is done with the help of pipeline that supports  * this container.  *<p>  * 4. Once the creation of the container is complete, the client will make  * another call to the SCM, this time specifying the containerName and the  * COMPLETE_CREATE as the Event.  *<p>  * 5. With COMPLETE_CREATE event, the container moves to an Open State. This is  * the state when clients can write to a container.  *<p>  * 6. If the client does not respond with the COMPLETE_CREATE event with a  * certain time, the state machine times out and triggers a delete operation of  * the container.  *<p>  * Please see the function initializeStateMachine below to see how this looks in  * code.  *<p>  * Reusing existing container :  *<p>  * The create container call is not made all the time, the system tries to use  * open containers as much as possible. So in those cases, it looks thru the  * list of open containers and will return containers that match the specific  * signature.  *<p>  * Please note : Logically there are 3 separate state machines in the case of  * containers.  *<p>  * The Create State Machine -- Commented extensively above.  *<p>  * Open/Close State Machine - Once the container is in the Open State,  * eventually it will be closed, once sufficient data has been written to it.  *<p>  * TimeOut Delete Container State Machine - if the container creating times out,  * then Container State manager decides to delete the container.  */
end_comment

begin_class
DECL|class|ContainerStateManager
specifier|public
class|class
name|ContainerStateManager
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
name|ContainerStateManager
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|StateMachine
argument_list|<
name|HddsProtos
operator|.
name|LifeCycleState
argument_list|,
DECL|field|stateMachine
name|HddsProtos
operator|.
name|LifeCycleEvent
argument_list|>
name|stateMachine
decl_stmt|;
DECL|field|containerSize
specifier|private
specifier|final
name|long
name|containerSize
decl_stmt|;
DECL|field|lastUsedMap
specifier|private
specifier|final
name|ConcurrentHashMap
argument_list|<
name|ContainerState
argument_list|,
name|ContainerID
argument_list|>
name|lastUsedMap
decl_stmt|;
DECL|field|containers
specifier|private
specifier|final
name|ContainerStateMap
name|containers
decl_stmt|;
DECL|field|containerCount
specifier|private
specifier|final
name|AtomicLong
name|containerCount
decl_stmt|;
comment|/**    * Constructs a Container State Manager that tracks all containers owned by    * SCM for the purpose of allocation of blocks.    *<p>    * TODO : Add Container Tags so we know which containers are owned by SCM.    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|ContainerStateManager (final Configuration configuration)
specifier|public
name|ContainerStateManager
parameter_list|(
specifier|final
name|Configuration
name|configuration
parameter_list|)
block|{
comment|// Initialize the container state machine.
specifier|final
name|Set
argument_list|<
name|HddsProtos
operator|.
name|LifeCycleState
argument_list|>
name|finalStates
init|=
operator|new
name|HashSet
argument_list|()
decl_stmt|;
comment|// These are the steady states of a container.
name|finalStates
operator|.
name|add
argument_list|(
name|LifeCycleState
operator|.
name|OPEN
argument_list|)
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
name|finalStates
operator|.
name|add
argument_list|(
name|LifeCycleState
operator|.
name|DELETED
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
name|OPEN
argument_list|,
name|finalStates
argument_list|)
expr_stmt|;
name|initializeStateMachine
argument_list|()
expr_stmt|;
name|this
operator|.
name|containerSize
operator|=
operator|(
name|long
operator|)
name|configuration
operator|.
name|getStorageSize
argument_list|(
name|ScmConfigKeys
operator|.
name|OZONE_SCM_CONTAINER_SIZE
argument_list|,
name|ScmConfigKeys
operator|.
name|OZONE_SCM_CONTAINER_SIZE_DEFAULT
argument_list|,
name|StorageUnit
operator|.
name|BYTES
argument_list|)
expr_stmt|;
name|this
operator|.
name|lastUsedMap
operator|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|this
operator|.
name|containerCount
operator|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|this
operator|.
name|containers
operator|=
operator|new
name|ContainerStateMap
argument_list|()
expr_stmt|;
block|}
comment|/*    *    * Event and State Transition Mapping:    *    * State: OPEN         ----------------> CLOSING    * Event:                    FINALIZE    *    * State: CLOSING      ----------------> QUASI_CLOSED    * Event:                  QUASI_CLOSE    *    * State: CLOSING      ----------------> CLOSED    * Event:                     CLOSE    *    * State: QUASI_CLOSED ----------------> CLOSED    * Event:                  FORCE_CLOSE    *    * State: CLOSED       ----------------> DELETING    * Event:                    DELETE    *    * State: DELETING     ----------------> DELETED    * Event:                    CLEANUP    *    *    * Container State Flow:    *    * [OPEN]--------------->[CLOSING]--------------->[QUASI_CLOSED]    *          (FINALIZE)      |      (QUASI_CLOSE)        |    *                          |                           |    *                          |                           |    *                  (CLOSE) |             (FORCE_CLOSE) |    *                          |                           |    *                          |                           |    *                          +--------->[CLOSED]<--------+    *                                        |    *                                (DELETE)|    *                                        |    *                                        |    *                                   [DELETING]    *                                        |    *                              (CLEANUP) |    *                                        |    *                                        V    *                                    [DELETED]    *    */
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
name|QUASI_CLOSED
argument_list|,
name|LifeCycleEvent
operator|.
name|QUASI_CLOSE
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
name|QUASI_CLOSED
argument_list|,
name|LifeCycleState
operator|.
name|CLOSED
argument_list|,
name|LifeCycleEvent
operator|.
name|FORCE_CLOSE
argument_list|)
expr_stmt|;
name|stateMachine
operator|.
name|addTransition
argument_list|(
name|LifeCycleState
operator|.
name|CLOSED
argument_list|,
name|LifeCycleState
operator|.
name|DELETING
argument_list|,
name|LifeCycleEvent
operator|.
name|DELETE
argument_list|)
expr_stmt|;
name|stateMachine
operator|.
name|addTransition
argument_list|(
name|LifeCycleState
operator|.
name|DELETING
argument_list|,
name|LifeCycleState
operator|.
name|DELETED
argument_list|,
name|LifeCycleEvent
operator|.
name|CLEANUP
argument_list|)
expr_stmt|;
block|}
DECL|method|loadContainer (final ContainerInfo containerInfo)
name|void
name|loadContainer
parameter_list|(
specifier|final
name|ContainerInfo
name|containerInfo
parameter_list|)
throws|throws
name|SCMException
block|{
name|containers
operator|.
name|addContainer
argument_list|(
name|containerInfo
argument_list|)
expr_stmt|;
name|containerCount
operator|.
name|set
argument_list|(
name|Long
operator|.
name|max
argument_list|(
name|containerInfo
operator|.
name|getContainerID
argument_list|()
argument_list|,
name|containerCount
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Allocates a new container based on the type, replication etc.    *    * @param pipelineManager -- Pipeline Manager class.    * @param type -- Replication type.    * @param replicationFactor - Replication replicationFactor.    * @return ContainerWithPipeline    * @throws IOException  on Failure.    */
DECL|method|allocateContainer (final PipelineManager pipelineManager, final HddsProtos.ReplicationType type, final HddsProtos.ReplicationFactor replicationFactor, final String owner)
name|ContainerInfo
name|allocateContainer
parameter_list|(
specifier|final
name|PipelineManager
name|pipelineManager
parameter_list|,
specifier|final
name|HddsProtos
operator|.
name|ReplicationType
name|type
parameter_list|,
specifier|final
name|HddsProtos
operator|.
name|ReplicationFactor
name|replicationFactor
parameter_list|,
specifier|final
name|String
name|owner
parameter_list|)
throws|throws
name|IOException
block|{
name|Pipeline
name|pipeline
decl_stmt|;
try|try
block|{
name|pipeline
operator|=
name|pipelineManager
operator|.
name|createPipeline
argument_list|(
name|type
argument_list|,
name|replicationFactor
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|Pipeline
argument_list|>
name|pipelines
init|=
name|pipelineManager
operator|.
name|getPipelines
argument_list|(
name|type
argument_list|,
name|replicationFactor
argument_list|,
name|Pipeline
operator|.
name|PipelineState
operator|.
name|OPEN
argument_list|)
decl_stmt|;
if|if
condition|(
name|pipelines
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Could not allocate container. Cannot get any"
operator|+
literal|" matching pipeline for Type:"
operator|+
name|type
operator|+
literal|", Factor:"
operator|+
name|replicationFactor
operator|+
literal|", State:PipelineState.OPEN"
argument_list|)
throw|;
block|}
name|pipeline
operator|=
name|pipelines
operator|.
name|get
argument_list|(
operator|(
name|int
operator|)
name|containerCount
operator|.
name|get
argument_list|()
operator|%
name|pipelines
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|pipeline
argument_list|,
literal|"Pipeline type=%s/"
operator|+
literal|"replication=%s couldn't be found for the new container. "
operator|+
literal|"Do you have enough nodes?"
argument_list|,
name|type
argument_list|,
name|replicationFactor
argument_list|)
expr_stmt|;
specifier|final
name|long
name|containerID
init|=
name|containerCount
operator|.
name|incrementAndGet
argument_list|()
decl_stmt|;
specifier|final
name|ContainerInfo
name|containerInfo
init|=
operator|new
name|ContainerInfo
operator|.
name|Builder
argument_list|()
operator|.
name|setState
argument_list|(
name|LifeCycleState
operator|.
name|OPEN
argument_list|)
operator|.
name|setPipelineID
argument_list|(
name|pipeline
operator|.
name|getId
argument_list|()
argument_list|)
operator|.
name|setUsedBytes
argument_list|(
literal|0
argument_list|)
operator|.
name|setNumberOfKeys
argument_list|(
literal|0
argument_list|)
operator|.
name|setStateEnterTime
argument_list|(
name|Time
operator|.
name|monotonicNow
argument_list|()
argument_list|)
operator|.
name|setOwner
argument_list|(
name|owner
argument_list|)
operator|.
name|setContainerID
argument_list|(
name|containerID
argument_list|)
operator|.
name|setDeleteTransactionId
argument_list|(
literal|0
argument_list|)
operator|.
name|setReplicationFactor
argument_list|(
name|replicationFactor
argument_list|)
operator|.
name|setReplicationType
argument_list|(
name|pipeline
operator|.
name|getType
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|pipelineManager
operator|.
name|addContainerToPipeline
argument_list|(
name|pipeline
operator|.
name|getId
argument_list|()
argument_list|,
name|ContainerID
operator|.
name|valueof
argument_list|(
name|containerID
argument_list|)
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|containerInfo
argument_list|)
expr_stmt|;
name|containers
operator|.
name|addContainer
argument_list|(
name|containerInfo
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|trace
argument_list|(
literal|"New container allocated: {}"
argument_list|,
name|containerInfo
argument_list|)
expr_stmt|;
return|return
name|containerInfo
return|;
block|}
comment|/**    * Update the Container State to the next state.    *    * @param containerID - ContainerID    * @param event - LifeCycle Event    * @return Updated ContainerInfo.    * @throws SCMException  on Failure.    */
DECL|method|updateContainerState (final ContainerID containerID, final HddsProtos.LifeCycleEvent event)
name|ContainerInfo
name|updateContainerState
parameter_list|(
specifier|final
name|ContainerID
name|containerID
parameter_list|,
specifier|final
name|HddsProtos
operator|.
name|LifeCycleEvent
name|event
parameter_list|)
throws|throws
name|SCMException
throws|,
name|ContainerNotFoundException
block|{
specifier|final
name|ContainerInfo
name|info
init|=
name|containers
operator|.
name|getContainerInfo
argument_list|(
name|containerID
argument_list|)
decl_stmt|;
try|try
block|{
specifier|final
name|LifeCycleState
name|newState
init|=
name|stateMachine
operator|.
name|getNextState
argument_list|(
name|info
operator|.
name|getState
argument_list|()
argument_list|,
name|event
argument_list|)
decl_stmt|;
name|containers
operator|.
name|updateState
argument_list|(
name|containerID
argument_list|,
name|info
operator|.
name|getState
argument_list|()
argument_list|,
name|newState
argument_list|)
expr_stmt|;
return|return
name|containers
operator|.
name|getContainerInfo
argument_list|(
name|containerID
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
literal|"Failed to update container state %s, "
operator|+
literal|"reason: invalid state transition from state: %s upon "
operator|+
literal|"event: %s."
argument_list|,
name|containerID
argument_list|,
name|info
operator|.
name|getState
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
name|FAILED_TO_CHANGE_CONTAINER_STATE
argument_list|)
throw|;
block|}
block|}
comment|/**    * Update the container State.    * @param info - Container Info    * @return  ContainerInfo    * @throws SCMException - on Error.    */
DECL|method|updateContainerInfo (final ContainerInfo info)
name|ContainerInfo
name|updateContainerInfo
parameter_list|(
specifier|final
name|ContainerInfo
name|info
parameter_list|)
throws|throws
name|ContainerNotFoundException
block|{
name|containers
operator|.
name|updateContainerInfo
argument_list|(
name|info
argument_list|)
expr_stmt|;
return|return
name|containers
operator|.
name|getContainerInfo
argument_list|(
name|info
operator|.
name|containerID
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Update deleteTransactionId for a container.    *    * @param deleteTransactionMap maps containerId to its new    *                             deleteTransactionID    */
DECL|method|updateDeleteTransactionId ( final Map<Long, Long> deleteTransactionMap)
name|void
name|updateDeleteTransactionId
parameter_list|(
specifier|final
name|Map
argument_list|<
name|Long
argument_list|,
name|Long
argument_list|>
name|deleteTransactionMap
parameter_list|)
block|{
name|deleteTransactionMap
operator|.
name|forEach
argument_list|(
parameter_list|(
name|k
parameter_list|,
name|v
parameter_list|)
lambda|->
block|{
try|try
block|{
name|containers
operator|.
name|getContainerInfo
argument_list|(
name|ContainerID
operator|.
name|valueof
argument_list|(
name|k
argument_list|)
argument_list|)
operator|.
name|updateDeleteTransactionId
argument_list|(
name|v
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ContainerNotFoundException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Exception while updating delete transaction id."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Return a container matching the attributes specified.    *    * @param size - Space needed in the Container.    * @param owner - Owner of the container - A specific nameservice.    * @param type - Replication Type {StandAlone, Ratis}    * @param factor - Replication Factor {ONE, THREE}    * @param state - State of the Container-- {Open, Allocated etc.}    * @return ContainerInfo, null if there is no match found.    */
DECL|method|getMatchingContainer (final long size, String owner, ReplicationType type, ReplicationFactor factor, LifeCycleState state)
name|ContainerInfo
name|getMatchingContainer
parameter_list|(
specifier|final
name|long
name|size
parameter_list|,
name|String
name|owner
parameter_list|,
name|ReplicationType
name|type
parameter_list|,
name|ReplicationFactor
name|factor
parameter_list|,
name|LifeCycleState
name|state
parameter_list|)
block|{
comment|// Find containers that match the query spec, if no match return null.
specifier|final
name|NavigableSet
argument_list|<
name|ContainerID
argument_list|>
name|matchingSet
init|=
name|containers
operator|.
name|getMatchingContainerIDs
argument_list|(
name|state
argument_list|,
name|owner
argument_list|,
name|factor
argument_list|,
name|type
argument_list|)
decl_stmt|;
if|if
condition|(
name|matchingSet
operator|==
literal|null
operator|||
name|matchingSet
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// Get the last used container and find container above the last used
comment|// container ID.
specifier|final
name|ContainerState
name|key
init|=
operator|new
name|ContainerState
argument_list|(
name|owner
argument_list|,
name|type
argument_list|,
name|factor
argument_list|)
decl_stmt|;
specifier|final
name|ContainerID
name|lastID
init|=
name|lastUsedMap
operator|.
name|getOrDefault
argument_list|(
name|key
argument_list|,
name|matchingSet
operator|.
name|first
argument_list|()
argument_list|)
decl_stmt|;
comment|// There is a small issue here. The first time, we will skip the first
comment|// container. But in most cases it will not matter.
name|NavigableSet
argument_list|<
name|ContainerID
argument_list|>
name|resultSet
init|=
name|matchingSet
operator|.
name|tailSet
argument_list|(
name|lastID
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|resultSet
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|resultSet
operator|=
name|matchingSet
expr_stmt|;
block|}
name|ContainerInfo
name|selectedContainer
init|=
name|findContainerWithSpace
argument_list|(
name|size
argument_list|,
name|resultSet
argument_list|,
name|owner
argument_list|)
decl_stmt|;
if|if
condition|(
name|selectedContainer
operator|==
literal|null
condition|)
block|{
comment|// If we did not find any space in the tailSet, we need to look for
comment|// space in the headset, we need to pass true to deal with the
comment|// situation that we have a lone container that has space. That is we
comment|// ignored the last used container under the assumption we can find
comment|// other containers with space, but if have a single container that is
comment|// not true. Hence we need to include the last used container as the
comment|// last element in the sorted set.
name|resultSet
operator|=
name|matchingSet
operator|.
name|headSet
argument_list|(
name|lastID
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|selectedContainer
operator|=
name|findContainerWithSpace
argument_list|(
name|size
argument_list|,
name|resultSet
argument_list|,
name|owner
argument_list|)
expr_stmt|;
block|}
return|return
name|selectedContainer
return|;
block|}
DECL|method|findContainerWithSpace (final long size, final NavigableSet<ContainerID> searchSet, final String owner)
specifier|private
name|ContainerInfo
name|findContainerWithSpace
parameter_list|(
specifier|final
name|long
name|size
parameter_list|,
specifier|final
name|NavigableSet
argument_list|<
name|ContainerID
argument_list|>
name|searchSet
parameter_list|,
specifier|final
name|String
name|owner
parameter_list|)
block|{
try|try
block|{
comment|// Get the container with space to meet our request.
for|for
control|(
name|ContainerID
name|id
range|:
name|searchSet
control|)
block|{
specifier|final
name|ContainerInfo
name|containerInfo
init|=
name|containers
operator|.
name|getContainerInfo
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|containerInfo
operator|.
name|getUsedBytes
argument_list|()
operator|+
name|size
operator|<=
name|this
operator|.
name|containerSize
condition|)
block|{
name|containerInfo
operator|.
name|updateLastUsedTime
argument_list|()
expr_stmt|;
specifier|final
name|ContainerState
name|key
init|=
operator|new
name|ContainerState
argument_list|(
name|owner
argument_list|,
name|containerInfo
operator|.
name|getReplicationType
argument_list|()
argument_list|,
name|containerInfo
operator|.
name|getReplicationFactor
argument_list|()
argument_list|)
decl_stmt|;
name|lastUsedMap
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|containerInfo
operator|.
name|containerID
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|containerInfo
return|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|ContainerNotFoundException
name|e
parameter_list|)
block|{
comment|// This should not happen!
name|LOG
operator|.
name|warn
argument_list|(
literal|"Exception while finding container with space"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|getAllContainerIDs ()
name|Set
argument_list|<
name|ContainerID
argument_list|>
name|getAllContainerIDs
parameter_list|()
block|{
return|return
name|containers
operator|.
name|getAllContainerIDs
argument_list|()
return|;
block|}
comment|/**    * Returns Containers by State.    *    * @param state - State - Open, Closed etc.    * @return List of containers by state.    */
DECL|method|getContainerIDsByState (final LifeCycleState state)
name|Set
argument_list|<
name|ContainerID
argument_list|>
name|getContainerIDsByState
parameter_list|(
specifier|final
name|LifeCycleState
name|state
parameter_list|)
block|{
return|return
name|containers
operator|.
name|getContainerIDsByState
argument_list|(
name|state
argument_list|)
return|;
block|}
comment|/**    * Returns a set of ContainerIDs that match the Container.    *    * @param owner  Owner of the Containers.    * @param type - Replication Type of the containers    * @param factor - Replication factor of the containers.    * @param state - Current State, like Open, Close etc.    * @return Set of containers that match the specific query parameters.    */
DECL|method|getMatchingContainerIDs (final String owner, final ReplicationType type, final ReplicationFactor factor, final LifeCycleState state)
name|NavigableSet
argument_list|<
name|ContainerID
argument_list|>
name|getMatchingContainerIDs
parameter_list|(
specifier|final
name|String
name|owner
parameter_list|,
specifier|final
name|ReplicationType
name|type
parameter_list|,
specifier|final
name|ReplicationFactor
name|factor
parameter_list|,
specifier|final
name|LifeCycleState
name|state
parameter_list|)
block|{
return|return
name|containers
operator|.
name|getMatchingContainerIDs
argument_list|(
name|state
argument_list|,
name|owner
argument_list|,
name|factor
argument_list|,
name|type
argument_list|)
return|;
block|}
comment|/**    * Returns the containerInfo for the given container id.    * @param containerID id of the container    * @return ContainerInfo containerInfo    * @throws IOException    */
DECL|method|getContainer (final ContainerID containerID)
name|ContainerInfo
name|getContainer
parameter_list|(
specifier|final
name|ContainerID
name|containerID
parameter_list|)
throws|throws
name|ContainerNotFoundException
block|{
return|return
name|containers
operator|.
name|getContainerInfo
argument_list|(
name|containerID
argument_list|)
return|;
block|}
DECL|method|close ()
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{   }
comment|/**    * Returns the latest list of DataNodes where replica for given containerId    * exist. Throws an SCMException if no entry is found for given containerId.    *    * @param containerID    * @return Set<DatanodeDetails>    */
DECL|method|getContainerReplicas ( final ContainerID containerID)
name|Set
argument_list|<
name|ContainerReplica
argument_list|>
name|getContainerReplicas
parameter_list|(
specifier|final
name|ContainerID
name|containerID
parameter_list|)
throws|throws
name|ContainerNotFoundException
block|{
return|return
name|containers
operator|.
name|getContainerReplicas
argument_list|(
name|containerID
argument_list|)
return|;
block|}
comment|/**    * Add a container Replica for given DataNode.    *    * @param containerID    * @param replica    */
DECL|method|updateContainerReplica (final ContainerID containerID, final ContainerReplica replica)
name|void
name|updateContainerReplica
parameter_list|(
specifier|final
name|ContainerID
name|containerID
parameter_list|,
specifier|final
name|ContainerReplica
name|replica
parameter_list|)
throws|throws
name|ContainerNotFoundException
block|{
name|containers
operator|.
name|updateContainerReplica
argument_list|(
name|containerID
argument_list|,
name|replica
argument_list|)
expr_stmt|;
block|}
comment|/**    * Remove a container Replica for given DataNode.    *    * @param containerID    * @param replica    * @return True of dataNode is removed successfully else false.    */
DECL|method|removeContainerReplica (final ContainerID containerID, final ContainerReplica replica)
name|void
name|removeContainerReplica
parameter_list|(
specifier|final
name|ContainerID
name|containerID
parameter_list|,
specifier|final
name|ContainerReplica
name|replica
parameter_list|)
throws|throws
name|ContainerNotFoundException
throws|,
name|ContainerReplicaNotFoundException
block|{
name|containers
operator|.
name|removeContainerReplica
argument_list|(
name|containerID
argument_list|,
name|replica
argument_list|)
expr_stmt|;
block|}
DECL|method|removeContainer (final ContainerID containerID)
name|void
name|removeContainer
parameter_list|(
specifier|final
name|ContainerID
name|containerID
parameter_list|)
throws|throws
name|ContainerNotFoundException
block|{
name|containers
operator|.
name|removeContainer
argument_list|(
name|containerID
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

