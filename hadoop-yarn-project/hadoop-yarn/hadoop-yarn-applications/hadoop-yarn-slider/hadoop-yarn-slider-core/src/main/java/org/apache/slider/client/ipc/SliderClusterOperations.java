begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.client.ipc
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|client
operator|.
name|ipc
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|exceptions
operator|.
name|YarnException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|api
operator|.
name|ClusterNode
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|api
operator|.
name|SliderClusterProtocol
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|api
operator|.
name|StateValues
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|api
operator|.
name|proto
operator|.
name|Messages
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|api
operator|.
name|resource
operator|.
name|Application
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|api
operator|.
name|types
operator|.
name|ContainerInformation
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|api
operator|.
name|types
operator|.
name|NodeInformation
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|api
operator|.
name|types
operator|.
name|NodeInformationList
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|api
operator|.
name|types
operator|.
name|PingInformation
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|common
operator|.
name|tools
operator|.
name|Duration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|core
operator|.
name|exceptions
operator|.
name|NoSuchNodeException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|core
operator|.
name|exceptions
operator|.
name|SliderException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|core
operator|.
name|exceptions
operator|.
name|WaitTimeoutException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|core
operator|.
name|persist
operator|.
name|JsonSerDeser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jackson
operator|.
name|JsonParseException
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
name|ArrayList
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
name|Collection
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
name|Map
operator|.
name|Entry
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|api
operator|.
name|types
operator|.
name|RestTypeMarshalling
operator|.
name|unmarshall
import|;
end_import

begin_comment
comment|/**  * Cluster operations at a slightly higher level than the RPC code  */
end_comment

begin_class
DECL|class|SliderClusterOperations
specifier|public
class|class
name|SliderClusterOperations
block|{
specifier|protected
specifier|static
specifier|final
name|Logger
DECL|field|log
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SliderClusterOperations
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|appMaster
specifier|private
specifier|final
name|SliderClusterProtocol
name|appMaster
decl_stmt|;
DECL|field|jsonSerDeser
specifier|private
specifier|static
specifier|final
name|JsonSerDeser
argument_list|<
name|Application
argument_list|>
name|jsonSerDeser
init|=
operator|new
name|JsonSerDeser
argument_list|<
name|Application
argument_list|>
argument_list|(
name|Application
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|EMPTY
specifier|private
specifier|static
specifier|final
name|Messages
operator|.
name|EmptyPayloadProto
name|EMPTY
decl_stmt|;
static|static
block|{
name|EMPTY
operator|=
name|Messages
operator|.
name|EmptyPayloadProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
DECL|method|SliderClusterOperations (SliderClusterProtocol appMaster)
specifier|public
name|SliderClusterOperations
parameter_list|(
name|SliderClusterProtocol
name|appMaster
parameter_list|)
block|{
name|this
operator|.
name|appMaster
operator|=
name|appMaster
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
specifier|final
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"SliderClusterOperations{"
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"IPC binding="
argument_list|)
operator|.
name|append
argument_list|(
name|appMaster
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|'}'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Get a node from the AM    * @param uuid uuid of node    * @return deserialized node    * @throws IOException IO problems    * @throws NoSuchNodeException if the node isn't found    */
DECL|method|getNode (String uuid)
specifier|public
name|ClusterNode
name|getNode
parameter_list|(
name|String
name|uuid
parameter_list|)
throws|throws
name|IOException
throws|,
name|NoSuchNodeException
throws|,
name|YarnException
block|{
name|Messages
operator|.
name|GetNodeRequestProto
name|req
init|=
name|Messages
operator|.
name|GetNodeRequestProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setUuid
argument_list|(
name|uuid
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|Messages
operator|.
name|GetNodeResponseProto
name|node
init|=
name|appMaster
operator|.
name|getNode
argument_list|(
name|req
argument_list|)
decl_stmt|;
return|return
name|ClusterNode
operator|.
name|fromProtobuf
argument_list|(
name|node
operator|.
name|getClusterNode
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Unmarshall a list of nodes from a protobud response    * @param nodes node list    * @return possibly empty list of cluster nodes    * @throws IOException    */
DECL|method|convertNodeWireToClusterNodes (List<Messages.RoleInstanceState> nodes)
specifier|public
name|List
argument_list|<
name|ClusterNode
argument_list|>
name|convertNodeWireToClusterNodes
parameter_list|(
name|List
argument_list|<
name|Messages
operator|.
name|RoleInstanceState
argument_list|>
name|nodes
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|ClusterNode
argument_list|>
name|nodeList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|nodes
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Messages
operator|.
name|RoleInstanceState
name|node
range|:
name|nodes
control|)
block|{
name|nodeList
operator|.
name|add
argument_list|(
name|ClusterNode
operator|.
name|fromProtobuf
argument_list|(
name|node
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|nodeList
return|;
block|}
comment|/**    * Echo text (debug action)    * @param text text    * @return the text, echoed back    * @throws YarnException    * @throws IOException    */
DECL|method|echo (String text)
specifier|public
name|String
name|echo
parameter_list|(
name|String
name|text
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
name|Messages
operator|.
name|EchoRequestProto
operator|.
name|Builder
name|builder
init|=
name|Messages
operator|.
name|EchoRequestProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setText
argument_list|(
name|text
argument_list|)
expr_stmt|;
name|Messages
operator|.
name|EchoRequestProto
name|req
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
name|Messages
operator|.
name|EchoResponseProto
name|response
init|=
name|appMaster
operator|.
name|echo
argument_list|(
name|req
argument_list|)
decl_stmt|;
return|return
name|response
operator|.
name|getText
argument_list|()
return|;
block|}
comment|/**    * Connect to a live cluster and get its current state    * @return its description    */
DECL|method|getApplication ()
specifier|public
name|Application
name|getApplication
parameter_list|()
throws|throws
name|YarnException
throws|,
name|IOException
block|{
name|Messages
operator|.
name|GetJSONClusterStatusRequestProto
name|req
init|=
name|Messages
operator|.
name|GetJSONClusterStatusRequestProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
name|Messages
operator|.
name|GetJSONClusterStatusResponseProto
name|resp
init|=
name|appMaster
operator|.
name|getJSONClusterStatus
argument_list|(
name|req
argument_list|)
decl_stmt|;
name|String
name|statusJson
init|=
name|resp
operator|.
name|getClusterSpec
argument_list|()
decl_stmt|;
try|try
block|{
return|return
name|jsonSerDeser
operator|.
name|fromJson
argument_list|(
name|statusJson
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|JsonParseException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Error when parsing app json file"
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
comment|/**    * Kill a container    * @param id container ID    * @return a success flag    * @throws YarnException    * @throws IOException    */
DECL|method|killContainer (String id)
specifier|public
name|boolean
name|killContainer
parameter_list|(
name|String
name|id
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
name|Messages
operator|.
name|KillContainerRequestProto
operator|.
name|Builder
name|builder
init|=
name|Messages
operator|.
name|KillContainerRequestProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setId
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|Messages
operator|.
name|KillContainerRequestProto
name|req
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
name|Messages
operator|.
name|KillContainerResponseProto
name|response
init|=
name|appMaster
operator|.
name|killContainer
argument_list|(
name|req
argument_list|)
decl_stmt|;
return|return
name|response
operator|.
name|getSuccess
argument_list|()
return|;
block|}
comment|/**    * List all node UUIDs in a role    * @param role role name or "" for all    * @return an array of UUID strings    * @throws IOException    * @throws YarnException    */
DECL|method|listNodeUUIDsByRole (String role)
specifier|public
name|String
index|[]
name|listNodeUUIDsByRole
parameter_list|(
name|String
name|role
parameter_list|)
throws|throws
name|IOException
throws|,
name|YarnException
block|{
name|Collection
argument_list|<
name|String
argument_list|>
name|uuidList
init|=
name|innerListNodeUUIDSByRole
argument_list|(
name|role
argument_list|)
decl_stmt|;
name|String
index|[]
name|uuids
init|=
operator|new
name|String
index|[
name|uuidList
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
return|return
name|uuidList
operator|.
name|toArray
argument_list|(
name|uuids
argument_list|)
return|;
block|}
DECL|method|innerListNodeUUIDSByRole (String role)
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|innerListNodeUUIDSByRole
parameter_list|(
name|String
name|role
parameter_list|)
throws|throws
name|IOException
throws|,
name|YarnException
block|{
name|Messages
operator|.
name|ListNodeUUIDsByRoleRequestProto
name|req
init|=
name|Messages
operator|.
name|ListNodeUUIDsByRoleRequestProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setRole
argument_list|(
name|role
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|Messages
operator|.
name|ListNodeUUIDsByRoleResponseProto
name|resp
init|=
name|appMaster
operator|.
name|listNodeUUIDsByRole
argument_list|(
name|req
argument_list|)
decl_stmt|;
return|return
name|resp
operator|.
name|getUuidList
argument_list|()
return|;
block|}
comment|/**    * List all nodes in a role. This is a double round trip: once to list    * the nodes in a role, another to get their details    * @param role    * @return an array of ContainerNode instances    * @throws IOException    * @throws YarnException    */
DECL|method|listClusterNodesInRole (String role)
specifier|public
name|List
argument_list|<
name|ClusterNode
argument_list|>
name|listClusterNodesInRole
parameter_list|(
name|String
name|role
parameter_list|)
throws|throws
name|IOException
throws|,
name|YarnException
block|{
name|Collection
argument_list|<
name|String
argument_list|>
name|uuidList
init|=
name|innerListNodeUUIDSByRole
argument_list|(
name|role
argument_list|)
decl_stmt|;
name|Messages
operator|.
name|GetClusterNodesRequestProto
name|req
init|=
name|Messages
operator|.
name|GetClusterNodesRequestProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|addAllUuid
argument_list|(
name|uuidList
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|Messages
operator|.
name|GetClusterNodesResponseProto
name|resp
init|=
name|appMaster
operator|.
name|getClusterNodes
argument_list|(
name|req
argument_list|)
decl_stmt|;
return|return
name|convertNodeWireToClusterNodes
argument_list|(
name|resp
operator|.
name|getClusterNodeList
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Get the details on a list of uuids    * @param uuids instance IDs    * @return a possibly empty list of node details    * @throws IOException    * @throws YarnException    */
annotation|@
name|VisibleForTesting
DECL|method|listClusterNodes (String[] uuids)
specifier|public
name|List
argument_list|<
name|ClusterNode
argument_list|>
name|listClusterNodes
parameter_list|(
name|String
index|[]
name|uuids
parameter_list|)
throws|throws
name|IOException
throws|,
name|YarnException
block|{
name|Messages
operator|.
name|GetClusterNodesRequestProto
name|req
init|=
name|Messages
operator|.
name|GetClusterNodesRequestProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|addAllUuid
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|uuids
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|Messages
operator|.
name|GetClusterNodesResponseProto
name|resp
init|=
name|appMaster
operator|.
name|getClusterNodes
argument_list|(
name|req
argument_list|)
decl_stmt|;
return|return
name|convertNodeWireToClusterNodes
argument_list|(
name|resp
operator|.
name|getClusterNodeList
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Wait for an instance of a named role to be live (or past it in the lifecycle)    * @param role role to look for    * @param timeout time to wait    * @return the state. If still in CREATED, the cluster didn't come up    * in the time period. If LIVE, all is well. If>LIVE, it has shut for a reason    * @throws IOException IO    * @throws SliderException Slider    * @throws WaitTimeoutException if the wait timed out    */
annotation|@
name|VisibleForTesting
DECL|method|waitForRoleInstanceLive (String role, long timeout)
specifier|public
name|int
name|waitForRoleInstanceLive
parameter_list|(
name|String
name|role
parameter_list|,
name|long
name|timeout
parameter_list|)
throws|throws
name|WaitTimeoutException
throws|,
name|IOException
throws|,
name|YarnException
block|{
name|Duration
name|duration
init|=
operator|new
name|Duration
argument_list|(
name|timeout
argument_list|)
decl_stmt|;
name|duration
operator|.
name|start
argument_list|()
expr_stmt|;
name|boolean
name|live
init|=
literal|false
decl_stmt|;
name|int
name|state
init|=
name|StateValues
operator|.
name|STATE_CREATED
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Waiting {} millis for a live node in role {}"
argument_list|,
name|timeout
argument_list|,
name|role
argument_list|)
expr_stmt|;
try|try
block|{
while|while
condition|(
operator|!
name|live
condition|)
block|{
comment|// see if there is a node in that role yet
name|List
argument_list|<
name|String
argument_list|>
name|uuids
init|=
name|innerListNodeUUIDSByRole
argument_list|(
name|role
argument_list|)
decl_stmt|;
name|String
index|[]
name|containers
init|=
name|uuids
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|uuids
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
name|int
name|roleCount
init|=
name|containers
operator|.
name|length
decl_stmt|;
name|ClusterNode
name|roleInstance
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|roleCount
operator|!=
literal|0
condition|)
block|{
comment|// if there is, get the node
name|roleInstance
operator|=
name|getNode
argument_list|(
name|containers
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
if|if
condition|(
name|roleInstance
operator|!=
literal|null
condition|)
block|{
name|state
operator|=
name|roleInstance
operator|.
name|state
expr_stmt|;
name|live
operator|=
name|state
operator|>=
name|StateValues
operator|.
name|STATE_LIVE
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|live
condition|)
block|{
if|if
condition|(
name|duration
operator|.
name|getLimitExceeded
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|WaitTimeoutException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Timeout after %d millis"
operator|+
literal|" waiting for a live instance of type %s; "
operator|+
literal|"instances found %d %s"
argument_list|,
name|timeout
argument_list|,
name|role
argument_list|,
name|roleCount
argument_list|,
operator|(
name|roleInstance
operator|!=
literal|null
condition|?
operator|(
literal|" instance -\n"
operator|+
name|roleInstance
operator|.
name|toString
argument_list|()
operator|)
else|:
literal|""
operator|)
argument_list|)
argument_list|)
throw|;
block|}
else|else
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ignored
parameter_list|)
block|{
comment|// ignored
block|}
block|}
block|}
block|}
block|}
finally|finally
block|{
name|duration
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|state
return|;
block|}
DECL|method|flex (Map<String, Long> componentCounts)
specifier|public
name|void
name|flex
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|componentCounts
parameter_list|)
throws|throws
name|IOException
block|{
name|Messages
operator|.
name|FlexComponentsRequestProto
operator|.
name|Builder
name|builder
init|=
name|Messages
operator|.
name|FlexComponentsRequestProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|componentCount
range|:
name|componentCounts
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Messages
operator|.
name|ComponentCountProto
name|componentProto
init|=
name|Messages
operator|.
name|ComponentCountProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setName
argument_list|(
name|componentCount
operator|.
name|getKey
argument_list|()
argument_list|)
operator|.
name|setNumberOfContainers
argument_list|(
name|componentCount
operator|.
name|getValue
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|builder
operator|.
name|addComponents
argument_list|(
name|componentProto
argument_list|)
expr_stmt|;
block|}
name|appMaster
operator|.
name|flexComponents
argument_list|(
name|builder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Commit (possibly delayed) AM suicide    *    * @param signal exit code    * @param text text text to log    * @param delay delay in millis    * @throws YarnException    * @throws IOException    */
DECL|method|amSuicide (String text, int signal, int delay)
specifier|public
name|void
name|amSuicide
parameter_list|(
name|String
name|text
parameter_list|,
name|int
name|signal
parameter_list|,
name|int
name|delay
parameter_list|)
throws|throws
name|IOException
block|{
name|Messages
operator|.
name|AMSuicideRequestProto
operator|.
name|Builder
name|builder
init|=
name|Messages
operator|.
name|AMSuicideRequestProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|text
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setText
argument_list|(
name|text
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|setSignal
argument_list|(
name|signal
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setDelay
argument_list|(
name|delay
argument_list|)
expr_stmt|;
name|Messages
operator|.
name|AMSuicideRequestProto
name|req
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
name|appMaster
operator|.
name|amSuicide
argument_list|(
name|req
argument_list|)
expr_stmt|;
block|}
DECL|method|getContainers ()
specifier|public
name|List
argument_list|<
name|ContainerInformation
argument_list|>
name|getContainers
parameter_list|()
throws|throws
name|IOException
block|{
name|Messages
operator|.
name|GetLiveContainersResponseProto
name|response
init|=
name|appMaster
operator|.
name|getLiveContainers
argument_list|(
name|Messages
operator|.
name|GetLiveContainersRequestProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|unmarshall
argument_list|(
name|response
argument_list|)
return|;
block|}
DECL|method|getLiveNodes ()
specifier|public
name|NodeInformationList
name|getLiveNodes
parameter_list|()
throws|throws
name|IOException
block|{
name|Messages
operator|.
name|GetLiveNodesResponseProto
name|response
init|=
name|appMaster
operator|.
name|getLiveNodes
argument_list|(
name|Messages
operator|.
name|GetLiveNodesRequestProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|records
init|=
name|response
operator|.
name|getNodesCount
argument_list|()
decl_stmt|;
name|NodeInformationList
name|nil
init|=
operator|new
name|NodeInformationList
argument_list|(
name|records
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|records
condition|;
name|i
operator|++
control|)
block|{
name|nil
operator|.
name|add
argument_list|(
name|unmarshall
argument_list|(
name|response
operator|.
name|getNodes
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|nil
return|;
block|}
DECL|method|getLiveNode (String hostname)
specifier|public
name|NodeInformation
name|getLiveNode
parameter_list|(
name|String
name|hostname
parameter_list|)
throws|throws
name|IOException
block|{
name|Messages
operator|.
name|GetLiveNodeRequestProto
operator|.
name|Builder
name|builder
init|=
name|Messages
operator|.
name|GetLiveNodeRequestProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setName
argument_list|(
name|hostname
argument_list|)
expr_stmt|;
return|return
name|unmarshall
argument_list|(
name|appMaster
operator|.
name|getLiveNode
argument_list|(
name|builder
operator|.
name|build
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
DECL|method|ping (String text)
specifier|public
name|PingInformation
name|ping
parameter_list|(
name|String
name|text
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
DECL|method|stop (String text)
specifier|public
name|void
name|stop
parameter_list|(
name|String
name|text
parameter_list|)
throws|throws
name|IOException
block|{
name|amSuicide
argument_list|(
name|text
argument_list|,
literal|3
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

