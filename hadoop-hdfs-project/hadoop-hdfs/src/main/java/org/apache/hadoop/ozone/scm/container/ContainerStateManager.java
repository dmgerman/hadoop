begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.scm.container
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
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
name|annotations
operator|.
name|VisibleForTesting
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
name|commons
operator|.
name|lang3
operator|.
name|builder
operator|.
name|EqualsBuilder
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
name|lang3
operator|.
name|builder
operator|.
name|HashCodeBuilder
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
name|ozone
operator|.
name|OzoneConfigKeys
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
name|OzoneConsts
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
name|protocol
operator|.
name|proto
operator|.
name|OzoneProtos
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
name|scm
operator|.
name|pipelines
operator|.
name|PipelineSelector
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
name|scm
operator|.
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|ContainerInfo
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
name|scm
operator|.
name|container
operator|.
name|common
operator|.
name|helpers
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
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|Queue
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
name|PriorityQueue
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
name|Arrays
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
name|ConcurrentLinkedQueue
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
name|hadoop
operator|.
name|ozone
operator|.
name|protocol
operator|.
name|proto
operator|.
name|OzoneProtos
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
name|ozone
operator|.
name|protocol
operator|.
name|proto
operator|.
name|OzoneProtos
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
name|protocol
operator|.
name|proto
operator|.
name|OzoneProtos
operator|.
name|Owner
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
name|protocol
operator|.
name|proto
operator|.
name|OzoneProtos
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
name|ozone
operator|.
name|protocol
operator|.
name|proto
operator|.
name|OzoneProtos
operator|.
name|ReplicationFactor
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
name|ozone
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
comment|/**  * A container state manager keeps track of container states and returns  * containers that match various queries.  *<p>  * This state machine is driven by a combination of server and client actions.  *<p>  * This is how a create container happens: 1. When a container is created, the  * Server(or SCM) marks that Container as ALLOCATED state. In this state, SCM  * has chosen a pipeline for container to live on. However, the container is not  * created yet. This container along with the pipeline is returned to the  * client.  *<p>  * 2. The client when it sees the Container state as ALLOCATED understands that  * container needs to be created on the specified pipeline. The client lets the  * SCM know that saw this flag and is initiating the on the data nodes.  *<p>  * This is done by calling into notifyObjectCreation(ContainerName,  * BEGIN_CREATE) flag. When SCM gets this call, SCM puts the container state  * into CREATING. All this state means is that SCM told Client to create a  * container and client saw that request.  *<p>  * 3. Then client makes calls to datanodes directly, asking the datanodes to  * create the container. This is done with the help of pipeline that supports  * this container.  *<p>  * 4. Once the creation of the container is complete, the client will make  * another call to the SCM, this time specifing the containerName and the  * COMPLETE_CREATE as the Event.  *<p>  * 5. With COMPLETE_CREATE event, the container moves to an Open State. This is  * the state when clients can write to a container.  *<p>  * 6. If the client does not respond with the COMPLETE_CREATE event with a  * certain time, the state machine times out and triggers a delete operation of  * the container.  *<p>  * Please see the function initializeStateMachine below to see how this looks in  * code.  *<p>  * Reusing existing container :  *<p>  * The create container call is not made all the time, the system tries to use  * open containers as much as possible. So in those cases, it looks thru the  * list of open containers and will return containers that match the specific  * signature.  *<p>  * Please note : Logically there are 3 separate state machines in the case of  * containers.  *<p>  * The Create State Machine -- Commented extensively above.  *<p>  * Open/Close State Machine - Once the container is in the Open State,  * eventually it will be closed, once sufficient data has been written to it.  *<p>  * TimeOut Delete Container State Machine - if the container creating times out,  * then Container State manager decides to delete the container.  */
end_comment

begin_class
DECL|class|ContainerStateManager
specifier|public
class|class
name|ContainerStateManager
implements|implements
name|Closeable
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
name|OzoneProtos
operator|.
name|LifeCycleState
argument_list|,
DECL|field|stateMachine
name|OzoneProtos
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
DECL|field|cacheSize
specifier|private
specifier|final
name|long
name|cacheSize
decl_stmt|;
DECL|field|blockSize
specifier|private
specifier|final
name|long
name|blockSize
decl_stmt|;
comment|// A map that maintains the ContainerKey to Containers of that type ordered
comment|// by last access time.
DECL|field|lock
specifier|private
specifier|final
name|ReadWriteLock
name|lock
decl_stmt|;
DECL|field|containerCloseQueue
specifier|private
specifier|final
name|Queue
argument_list|<
name|ContainerInfo
argument_list|>
name|containerCloseQueue
decl_stmt|;
DECL|field|containers
specifier|private
name|Map
argument_list|<
name|ContainerKey
argument_list|,
name|PriorityQueue
argument_list|<
name|ContainerInfo
argument_list|>
argument_list|>
name|containers
decl_stmt|;
comment|/**    * Constructs a Container State Manager that tracks all containers owned by    * SCM for the purpose of allocation of blocks.    *<p>    * TODO : Add Container Tags so we know which containers are owned by SCM.    */
DECL|method|ContainerStateManager (Configuration configuration, Mapping containerMapping, final long cacheSize)
specifier|public
name|ContainerStateManager
parameter_list|(
name|Configuration
name|configuration
parameter_list|,
name|Mapping
name|containerMapping
parameter_list|,
specifier|final
name|long
name|cacheSize
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|cacheSize
operator|=
name|cacheSize
expr_stmt|;
comment|// Initialize the container state machine.
name|Set
argument_list|<
name|OzoneProtos
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
name|ALLOCATED
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
name|OzoneConsts
operator|.
name|GB
operator|*
name|configuration
operator|.
name|getInt
argument_list|(
name|ScmConfigKeys
operator|.
name|OZONE_SCM_CONTAINER_SIZE_GB
argument_list|,
name|ScmConfigKeys
operator|.
name|OZONE_SCM_CONTAINER_SIZE_DEFAULT
argument_list|)
expr_stmt|;
name|this
operator|.
name|blockSize
operator|=
name|OzoneConsts
operator|.
name|MB
operator|*
name|configuration
operator|.
name|getLong
argument_list|(
name|OzoneConfigKeys
operator|.
name|OZONE_SCM_BLOCK_SIZE_IN_MB
argument_list|,
name|OzoneConfigKeys
operator|.
name|OZONE_SCM_BLOCK_SIZE_DEFAULT
argument_list|)
expr_stmt|;
name|lock
operator|=
operator|new
name|ReentrantReadWriteLock
argument_list|()
expr_stmt|;
name|containers
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|initializeContainerMaps
argument_list|()
expr_stmt|;
name|loadExistingContainers
argument_list|(
name|containerMapping
argument_list|)
expr_stmt|;
name|containerCloseQueue
operator|=
operator|new
name|ConcurrentLinkedQueue
argument_list|<>
argument_list|()
expr_stmt|;
block|}
comment|/**    * Creates containers maps of following types.    *<p>    * OZONE  of type {Ratis, StandAlone, Chained} for each of these {ALLOCATED,    * CREATING, OPEN, CLOSED, DELETING, DELETED}  container states    *<p>    * CBLOCK of type {Ratis, StandAlone, Chained} for each of these {ALLOCATED,    * CREATING, OPEN, CLOSED, DELETING, DELETED}  container states    *<p>    * Commented out for now: HDFS of type {Ratis, StandAlone, Chained} for each    * of these {ALLOCATED, CREATING, OPEN, CLOSED, DELETING, DELETED}  container    * states    */
DECL|method|initializeContainerMaps ()
specifier|private
name|void
name|initializeContainerMaps
parameter_list|()
block|{
comment|// Called only from Ctor path, hence no lock is held.
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|containers
argument_list|)
expr_stmt|;
for|for
control|(
name|OzoneProtos
operator|.
name|Owner
name|owner
range|:
name|OzoneProtos
operator|.
name|Owner
operator|.
name|values
argument_list|()
control|)
block|{
for|for
control|(
name|ReplicationType
name|type
range|:
name|ReplicationType
operator|.
name|values
argument_list|()
control|)
block|{
for|for
control|(
name|ReplicationFactor
name|factor
range|:
name|ReplicationFactor
operator|.
name|values
argument_list|()
control|)
block|{
for|for
control|(
name|LifeCycleState
name|state
range|:
name|LifeCycleState
operator|.
name|values
argument_list|()
control|)
block|{
name|ContainerKey
name|key
init|=
operator|new
name|ContainerKey
argument_list|(
name|owner
argument_list|,
name|type
argument_list|,
name|factor
argument_list|,
name|state
argument_list|)
decl_stmt|;
name|PriorityQueue
argument_list|<
name|ContainerInfo
argument_list|>
name|queue
init|=
operator|new
name|PriorityQueue
argument_list|<>
argument_list|()
decl_stmt|;
name|containers
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|queue
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
comment|/**    * Load containers from the container store into the containerMaps.    *    * @param containerMapping -- Mapping object containing container store.    */
DECL|method|loadExistingContainers (Mapping containerMapping)
specifier|private
name|void
name|loadExistingContainers
parameter_list|(
name|Mapping
name|containerMapping
parameter_list|)
block|{
try|try
block|{
name|List
argument_list|<
name|ContainerInfo
argument_list|>
name|containerList
init|=
name|containerMapping
operator|.
name|listContainer
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
for|for
control|(
name|ContainerInfo
name|container
range|:
name|containerList
control|)
block|{
name|ContainerKey
name|key
init|=
operator|new
name|ContainerKey
argument_list|(
name|container
operator|.
name|getOwner
argument_list|()
argument_list|,
name|container
operator|.
name|getPipeline
argument_list|()
operator|.
name|getType
argument_list|()
argument_list|,
name|container
operator|.
name|getPipeline
argument_list|()
operator|.
name|getFactor
argument_list|()
argument_list|,
name|container
operator|.
name|getState
argument_list|()
argument_list|)
decl_stmt|;
name|containers
operator|.
name|get
argument_list|(
name|key
argument_list|)
operator|.
name|add
argument_list|(
name|container
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
if|if
condition|(
operator|!
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|equals
argument_list|(
literal|"No container exists in current db"
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Could not list the containers"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Return the info of all the containers kept by the in-memory mapping.    *    * @return the list of all container info.    */
DECL|method|getAllContainers ()
name|List
argument_list|<
name|ContainerInfo
argument_list|>
name|getAllContainers
parameter_list|()
block|{
name|List
argument_list|<
name|ContainerInfo
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|containers
operator|.
name|forEach
argument_list|(
parameter_list|(
name|key
parameter_list|,
name|value
parameter_list|)
lambda|->
name|list
operator|.
name|addAll
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|list
return|;
block|}
comment|// 1. Client -> SCM: Begin_create
comment|// 2. Client -> Datanode: create
comment|// 3. Client -> SCM: complete    {SCM:Creating ->OK}
comment|// 3. Client -> SCM: complete    {SCM:DELETING -> INVALID}
comment|// 4. Client->Datanode: write data.
comment|// Client-driven Create State Machine
comment|// States:<ALLOCATED>------------->CREATING----------------->[OPEN]
comment|// Events:            (BEGIN_CREATE)    |    (COMPLETE_CREATE)
comment|//                                      |
comment|//                                      |(TIMEOUT)
comment|//                                      V
comment|//                                  DELETING----------------->[DELETED]
comment|//                                           (CLEANUP)
comment|// SCM Open/Close State Machine
comment|// States: OPEN------------------>PENDING_CLOSE---------->[CLOSE]
comment|// Events:        (FULL_CONTAINER)               (CLOSE)
comment|// Delete State Machine
comment|// States: OPEN------------------>DELETING------------------>[DELETED]
comment|// Events:         (DELETE)                  (CLEANUP)
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
name|BEGIN_CREATE
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
name|COMPLETE_CREATE
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
name|OPEN
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
comment|// Creating timeout -> Deleting
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
name|DELETING
argument_list|,
name|LifeCycleEvent
operator|.
name|TIMEOUT
argument_list|)
expr_stmt|;
block|}
comment|/**    * allocates a new container based on the type, replication etc.    *    * @param selector -- Pipeline selector class.    * @param type -- Replication type.    * @param replicationFactor - Replication replicationFactor.    * @param containerName - Container Name.    * @return Container Info.    * @throws IOException    */
DECL|method|allocateContainer (PipelineSelector selector, OzoneProtos .ReplicationType type, OzoneProtos.ReplicationFactor replicationFactor, final String containerName, OzoneProtos.Owner owner)
specifier|public
name|ContainerInfo
name|allocateContainer
parameter_list|(
name|PipelineSelector
name|selector
parameter_list|,
name|OzoneProtos
operator|.
name|ReplicationType
name|type
parameter_list|,
name|OzoneProtos
operator|.
name|ReplicationFactor
name|replicationFactor
parameter_list|,
specifier|final
name|String
name|containerName
parameter_list|,
name|OzoneProtos
operator|.
name|Owner
name|owner
parameter_list|)
throws|throws
name|IOException
block|{
name|Pipeline
name|pipeline
init|=
name|selector
operator|.
name|getReplicationPipeline
argument_list|(
name|type
argument_list|,
name|replicationFactor
argument_list|,
name|containerName
argument_list|)
decl_stmt|;
name|ContainerInfo
name|containerInfo
init|=
operator|new
name|ContainerInfo
operator|.
name|Builder
argument_list|()
operator|.
name|setContainerName
argument_list|(
name|containerName
argument_list|)
operator|.
name|setState
argument_list|(
name|OzoneProtos
operator|.
name|LifeCycleState
operator|.
name|ALLOCATED
argument_list|)
operator|.
name|setPipeline
argument_list|(
name|pipeline
argument_list|)
comment|// This is bytes allocated for blocks inside container, not the
comment|// container size
operator|.
name|setAllocatedBytes
argument_list|(
literal|0
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
name|build
argument_list|()
decl_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|containerInfo
argument_list|)
expr_stmt|;
name|lock
operator|.
name|writeLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|ContainerKey
name|key
init|=
operator|new
name|ContainerKey
argument_list|(
name|owner
argument_list|,
name|type
argument_list|,
name|replicationFactor
argument_list|,
name|containerInfo
operator|.
name|getState
argument_list|()
argument_list|)
decl_stmt|;
name|PriorityQueue
argument_list|<
name|ContainerInfo
argument_list|>
name|queue
init|=
name|containers
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|queue
argument_list|)
expr_stmt|;
name|queue
operator|.
name|add
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
block|}
finally|finally
block|{
name|lock
operator|.
name|writeLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
return|return
name|containerInfo
return|;
block|}
comment|/**    * Update the Container State to the next state.    *    * @param info - ContainerInfo    * @param event - LifeCycle Event    * @return Updated ContainerInfo.    * @throws SCMException    */
DECL|method|updateContainerState (ContainerInfo info, OzoneProtos.LifeCycleEvent event)
specifier|public
name|ContainerInfo
name|updateContainerState
parameter_list|(
name|ContainerInfo
name|info
parameter_list|,
name|OzoneProtos
operator|.
name|LifeCycleEvent
name|event
parameter_list|)
throws|throws
name|SCMException
block|{
name|LifeCycleState
name|newState
decl_stmt|;
try|try
block|{
name|newState
operator|=
name|this
operator|.
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
expr_stmt|;
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
literal|"reason: invalid state transition from state: %s upon event: %s."
argument_list|,
name|info
operator|.
name|getPipeline
argument_list|()
operator|.
name|getContainerName
argument_list|()
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
comment|// This is a post condition after executing getNextState.
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|newState
argument_list|)
expr_stmt|;
name|Pipeline
name|pipeline
init|=
name|info
operator|.
name|getPipeline
argument_list|()
decl_stmt|;
name|ContainerKey
name|oldKey
init|=
operator|new
name|ContainerKey
argument_list|(
name|info
operator|.
name|getOwner
argument_list|()
argument_list|,
name|pipeline
operator|.
name|getType
argument_list|()
argument_list|,
name|pipeline
operator|.
name|getFactor
argument_list|()
argument_list|,
name|info
operator|.
name|getState
argument_list|()
argument_list|)
decl_stmt|;
name|ContainerKey
name|newKey
init|=
operator|new
name|ContainerKey
argument_list|(
name|info
operator|.
name|getOwner
argument_list|()
argument_list|,
name|pipeline
operator|.
name|getType
argument_list|()
argument_list|,
name|pipeline
operator|.
name|getFactor
argument_list|()
argument_list|,
name|newState
argument_list|)
decl_stmt|;
name|lock
operator|.
name|writeLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|PriorityQueue
argument_list|<
name|ContainerInfo
argument_list|>
name|currentQueue
init|=
name|containers
operator|.
name|get
argument_list|(
name|oldKey
argument_list|)
decl_stmt|;
comment|// This should never happen, since we have initialized the map and
comment|// queues to all possible states. No harm in asserting that info.
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|currentQueue
argument_list|)
expr_stmt|;
comment|// TODO : Should we read this container info from the database if this
comment|// is missing in the queue?. Right now we just add it into the queue.
comment|// We also need a background thread that will remove unused containers
comment|// from memory after 24 hours.  This is really a low priority work item
comment|// since typical clusters will have less than 10's of millions of open
comment|// containers at a given time, which we can easily keep in memory.
if|if
condition|(
name|currentQueue
operator|.
name|contains
argument_list|(
name|info
argument_list|)
condition|)
block|{
name|currentQueue
operator|.
name|remove
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
name|PriorityQueue
argument_list|<
name|ContainerInfo
argument_list|>
name|nextQueue
init|=
name|containers
operator|.
name|get
argument_list|(
name|newKey
argument_list|)
decl_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|nextQueue
argument_list|)
expr_stmt|;
name|ContainerInfo
name|containerInfo
init|=
operator|new
name|ContainerInfo
operator|.
name|Builder
argument_list|()
operator|.
name|setContainerName
argument_list|(
name|info
operator|.
name|getContainerName
argument_list|()
argument_list|)
operator|.
name|setState
argument_list|(
name|newState
argument_list|)
operator|.
name|setPipeline
argument_list|(
name|info
operator|.
name|getPipeline
argument_list|()
argument_list|)
operator|.
name|setAllocatedBytes
argument_list|(
name|info
operator|.
name|getAllocatedBytes
argument_list|()
argument_list|)
operator|.
name|setUsedBytes
argument_list|(
name|info
operator|.
name|getUsedBytes
argument_list|()
argument_list|)
operator|.
name|setNumberOfKeys
argument_list|(
name|info
operator|.
name|getNumberOfKeys
argument_list|()
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
name|info
operator|.
name|getOwner
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|containerInfo
argument_list|)
expr_stmt|;
name|nextQueue
operator|.
name|add
argument_list|(
name|containerInfo
argument_list|)
expr_stmt|;
return|return
name|containerInfo
return|;
block|}
finally|finally
block|{
name|lock
operator|.
name|writeLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Return a container matching the attributes specified.    *    * @param size - Space needed in the Container.    * @param owner - Owner of the container {OZONE, CBLOCK}    * @param type - Replication Type {StandAlone, Ratis}    * @param factor - Replication Factor {ONE, THREE}    * @param state - State of the Container-- {Open, Allocated etc.}    * @return ContainerInfo    */
DECL|method|getMatchingContainer (final long size, Owner owner, ReplicationType type, ReplicationFactor factor, LifeCycleState state)
specifier|public
name|ContainerInfo
name|getMatchingContainer
parameter_list|(
specifier|final
name|long
name|size
parameter_list|,
name|Owner
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
name|ContainerKey
name|key
init|=
operator|new
name|ContainerKey
argument_list|(
name|owner
argument_list|,
name|type
argument_list|,
name|factor
argument_list|,
name|state
argument_list|)
decl_stmt|;
name|lock
operator|.
name|writeLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|PriorityQueue
argument_list|<
name|ContainerInfo
argument_list|>
name|queue
init|=
name|containers
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|queue
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|// We don't have any Containers of this type.
return|return
literal|null
return|;
block|}
name|Iterator
argument_list|<
name|ContainerInfo
argument_list|>
name|iter
init|=
name|queue
operator|.
name|iterator
argument_list|()
decl_stmt|;
comment|// Two assumptions here.
comment|// 1. The Iteration on the heap is in ordered by the last used time.
comment|// 2. We remove and add the node back to push the node to the end of
comment|// the queue.
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|ContainerInfo
name|info
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|info
operator|.
name|getAllocatedBytes
argument_list|()
operator|+
name|size
operator|<=
name|this
operator|.
name|containerSize
condition|)
block|{
name|queue
operator|.
name|remove
argument_list|(
name|info
argument_list|)
expr_stmt|;
name|info
operator|.
name|allocate
argument_list|(
name|size
argument_list|)
expr_stmt|;
name|info
operator|.
name|updateLastUsedTime
argument_list|()
expr_stmt|;
name|queue
operator|.
name|add
argument_list|(
name|info
argument_list|)
expr_stmt|;
return|return
name|info
return|;
block|}
block|}
block|}
finally|finally
block|{
name|lock
operator|.
name|writeLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getMatchingContainers (Owner owner, ReplicationType type, ReplicationFactor factor, LifeCycleState state)
specifier|public
name|List
argument_list|<
name|ContainerInfo
argument_list|>
name|getMatchingContainers
parameter_list|(
name|Owner
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
name|ContainerKey
name|key
init|=
operator|new
name|ContainerKey
argument_list|(
name|owner
argument_list|,
name|type
argument_list|,
name|factor
argument_list|,
name|state
argument_list|)
decl_stmt|;
name|lock
operator|.
name|readLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
operator|(
name|ContainerInfo
index|[]
operator|)
name|containers
operator|.
name|get
argument_list|(
name|key
argument_list|)
operator|.
name|toArray
argument_list|(
operator|new
name|ContainerInfo
index|[
literal|0
index|]
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Could not get matching containers"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|lock
operator|.
name|readLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
comment|//TODO: update container metadata db with actual allocated bytes values.
block|}
comment|/**    * Class that acts as the container Key.    */
DECL|class|ContainerKey
specifier|private
specifier|static
class|class
name|ContainerKey
block|{
DECL|field|state
specifier|private
specifier|final
name|LifeCycleState
name|state
decl_stmt|;
DECL|field|type
specifier|private
specifier|final
name|ReplicationType
name|type
decl_stmt|;
DECL|field|owner
specifier|private
specifier|final
name|OzoneProtos
operator|.
name|Owner
name|owner
decl_stmt|;
DECL|field|replicationFactor
specifier|private
specifier|final
name|ReplicationFactor
name|replicationFactor
decl_stmt|;
comment|/**      * Constructs a Container Key.      *      * @param owner - Container Owners      * @param type - Replication Type.      * @param factor - Replication Factors      * @param state - LifeCycle State      */
DECL|method|ContainerKey (Owner owner, ReplicationType type, ReplicationFactor factor, LifeCycleState state)
name|ContainerKey
parameter_list|(
name|Owner
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
name|this
operator|.
name|state
operator|=
name|state
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|owner
operator|=
name|owner
expr_stmt|;
name|this
operator|.
name|replicationFactor
operator|=
name|factor
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|equals (Object o)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|ContainerKey
name|that
init|=
operator|(
name|ContainerKey
operator|)
name|o
decl_stmt|;
return|return
operator|new
name|EqualsBuilder
argument_list|()
operator|.
name|append
argument_list|(
name|state
argument_list|,
name|that
operator|.
name|state
argument_list|)
operator|.
name|append
argument_list|(
name|type
argument_list|,
name|that
operator|.
name|type
argument_list|)
operator|.
name|append
argument_list|(
name|owner
argument_list|,
name|that
operator|.
name|owner
argument_list|)
operator|.
name|append
argument_list|(
name|replicationFactor
argument_list|,
name|that
operator|.
name|replicationFactor
argument_list|)
operator|.
name|isEquals
argument_list|()
return|;
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
operator|new
name|HashCodeBuilder
argument_list|(
literal|137
argument_list|,
literal|757
argument_list|)
operator|.
name|append
argument_list|(
name|state
argument_list|)
operator|.
name|append
argument_list|(
name|type
argument_list|)
operator|.
name|append
argument_list|(
name|owner
argument_list|)
operator|.
name|append
argument_list|(
name|replicationFactor
argument_list|)
operator|.
name|toHashCode
argument_list|()
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
literal|"ContainerKey{"
operator|+
literal|"state="
operator|+
name|state
operator|+
literal|", type="
operator|+
name|type
operator|+
literal|", owner="
operator|+
name|owner
operator|+
literal|", replicationFactor="
operator|+
name|replicationFactor
operator|+
literal|'}'
return|;
block|}
block|}
block|}
end_class

end_unit

