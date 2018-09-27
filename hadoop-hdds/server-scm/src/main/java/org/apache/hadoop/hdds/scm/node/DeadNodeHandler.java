begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm.node
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
name|node
package|;
end_package

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
name|DatanodeDetails
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
name|container
operator|.
name|ContainerStateManager
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
name|replication
operator|.
name|ReplicationRequest
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
name|events
operator|.
name|SCMEvents
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
name|node
operator|.
name|states
operator|.
name|Node2ContainerMap
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
name|server
operator|.
name|events
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
name|hdds
operator|.
name|server
operator|.
name|events
operator|.
name|EventPublisher
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

begin_comment
comment|/**  * Handles Dead Node event.  */
end_comment

begin_class
DECL|class|DeadNodeHandler
specifier|public
class|class
name|DeadNodeHandler
implements|implements
name|EventHandler
argument_list|<
name|DatanodeDetails
argument_list|>
block|{
DECL|field|node2ContainerMap
specifier|private
specifier|final
name|Node2ContainerMap
name|node2ContainerMap
decl_stmt|;
DECL|field|containerStateManager
specifier|private
specifier|final
name|ContainerStateManager
name|containerStateManager
decl_stmt|;
DECL|field|nodeManager
specifier|private
specifier|final
name|NodeManager
name|nodeManager
decl_stmt|;
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
name|DeadNodeHandler
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|DeadNodeHandler ( Node2ContainerMap node2ContainerMap, ContainerStateManager containerStateManager, NodeManager nodeManager)
specifier|public
name|DeadNodeHandler
parameter_list|(
name|Node2ContainerMap
name|node2ContainerMap
parameter_list|,
name|ContainerStateManager
name|containerStateManager
parameter_list|,
name|NodeManager
name|nodeManager
parameter_list|)
block|{
name|this
operator|.
name|node2ContainerMap
operator|=
name|node2ContainerMap
expr_stmt|;
name|this
operator|.
name|containerStateManager
operator|=
name|containerStateManager
expr_stmt|;
name|this
operator|.
name|nodeManager
operator|=
name|nodeManager
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onMessage (DatanodeDetails datanodeDetails, EventPublisher publisher)
specifier|public
name|void
name|onMessage
parameter_list|(
name|DatanodeDetails
name|datanodeDetails
parameter_list|,
name|EventPublisher
name|publisher
parameter_list|)
block|{
name|nodeManager
operator|.
name|processDeadNode
argument_list|(
name|datanodeDetails
operator|.
name|getUuid
argument_list|()
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|ContainerID
argument_list|>
name|containers
init|=
name|node2ContainerMap
operator|.
name|getContainers
argument_list|(
name|datanodeDetails
operator|.
name|getUuid
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|containers
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"There's no containers in dead datanode {}, no replica will be"
operator|+
literal|" removed from the in-memory state."
argument_list|,
name|datanodeDetails
operator|.
name|getUuid
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Datanode {}  is dead. Removing replications from the in-memory state."
argument_list|,
name|datanodeDetails
operator|.
name|getUuid
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|ContainerID
name|container
range|:
name|containers
control|)
block|{
try|try
block|{
name|containerStateManager
operator|.
name|removeContainerReplica
argument_list|(
name|container
argument_list|,
name|datanodeDetails
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|containerStateManager
operator|.
name|isOpen
argument_list|(
name|container
argument_list|)
condition|)
block|{
name|ReplicationRequest
name|replicationRequest
init|=
name|containerStateManager
operator|.
name|checkReplicationState
argument_list|(
name|container
argument_list|)
decl_stmt|;
if|if
condition|(
name|replicationRequest
operator|!=
literal|null
condition|)
block|{
name|publisher
operator|.
name|fireEvent
argument_list|(
name|SCMEvents
operator|.
name|REPLICATE_CONTAINER
argument_list|,
name|replicationRequest
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|SCMException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Can't remove container from containerStateMap {}"
argument_list|,
name|container
operator|.
name|getId
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

