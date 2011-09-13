begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.app.rm
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|rm
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
name|Map
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
name|TreeMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|MRJobConfig
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
name|mapreduce
operator|.
name|v2
operator|.
name|api
operator|.
name|records
operator|.
name|TaskAttemptId
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
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|AppContext
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
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|client
operator|.
name|ClientService
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
name|protocolrecords
operator|.
name|AllocateRequest
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
name|protocolrecords
operator|.
name|AllocateResponse
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
name|AMResponse
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
name|api
operator|.
name|records
operator|.
name|ResourceRequest
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
name|YarnRemoteException
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
name|factories
operator|.
name|RecordFactory
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
name|factory
operator|.
name|providers
operator|.
name|RecordFactoryProvider
import|;
end_import

begin_comment
comment|/**  * Keeps the data structures to send container requests to RM.  */
end_comment

begin_class
DECL|class|RMContainerRequestor
specifier|public
specifier|abstract
class|class
name|RMContainerRequestor
extends|extends
name|RMCommunicator
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
name|RMContainerRequestor
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|ANY
specifier|static
specifier|final
name|String
name|ANY
init|=
literal|"*"
decl_stmt|;
DECL|field|lastResponseID
specifier|private
name|int
name|lastResponseID
decl_stmt|;
DECL|field|availableResources
specifier|private
name|Resource
name|availableResources
decl_stmt|;
DECL|field|recordFactory
specifier|private
specifier|final
name|RecordFactory
name|recordFactory
init|=
name|RecordFactoryProvider
operator|.
name|getRecordFactory
argument_list|(
literal|null
argument_list|)
decl_stmt|;
comment|//Key -> Priority
comment|//Value -> Map
comment|//Key->ResourceName (e.g., hostname, rackname, *)
comment|//Value->Map
comment|//Key->Resource Capability
comment|//Value->ResourceReqeust
specifier|private
specifier|final
name|Map
argument_list|<
name|Priority
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|Resource
argument_list|,
name|ResourceRequest
argument_list|>
argument_list|>
argument_list|>
DECL|field|remoteRequestsTable
name|remoteRequestsTable
init|=
operator|new
name|TreeMap
argument_list|<
name|Priority
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|Resource
argument_list|,
name|ResourceRequest
argument_list|>
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|ask
specifier|private
specifier|final
name|Set
argument_list|<
name|ResourceRequest
argument_list|>
name|ask
init|=
operator|new
name|TreeSet
argument_list|<
name|ResourceRequest
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|release
specifier|private
specifier|final
name|Set
argument_list|<
name|ContainerId
argument_list|>
name|release
init|=
operator|new
name|TreeSet
argument_list|<
name|ContainerId
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|nodeBlacklistingEnabled
specifier|private
name|boolean
name|nodeBlacklistingEnabled
decl_stmt|;
DECL|field|maxTaskFailuresPerNode
specifier|private
name|int
name|maxTaskFailuresPerNode
decl_stmt|;
DECL|field|nodeFailures
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|nodeFailures
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|blacklistedNodes
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|blacklistedNodes
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|RMContainerRequestor (ClientService clientService, AppContext context)
specifier|public
name|RMContainerRequestor
parameter_list|(
name|ClientService
name|clientService
parameter_list|,
name|AppContext
name|context
parameter_list|)
block|{
name|super
argument_list|(
name|clientService
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
DECL|class|ContainerRequest
specifier|static
class|class
name|ContainerRequest
block|{
DECL|field|attemptID
specifier|final
name|TaskAttemptId
name|attemptID
decl_stmt|;
DECL|field|capability
specifier|final
name|Resource
name|capability
decl_stmt|;
DECL|field|hosts
specifier|final
name|String
index|[]
name|hosts
decl_stmt|;
DECL|field|racks
specifier|final
name|String
index|[]
name|racks
decl_stmt|;
comment|//final boolean earlierAttemptFailed;
DECL|field|priority
specifier|final
name|Priority
name|priority
decl_stmt|;
DECL|method|ContainerRequest (ContainerRequestEvent event, Priority priority)
specifier|public
name|ContainerRequest
parameter_list|(
name|ContainerRequestEvent
name|event
parameter_list|,
name|Priority
name|priority
parameter_list|)
block|{
name|this
operator|.
name|attemptID
operator|=
name|event
operator|.
name|getAttemptID
argument_list|()
expr_stmt|;
name|this
operator|.
name|capability
operator|=
name|event
operator|.
name|getCapability
argument_list|()
expr_stmt|;
name|this
operator|.
name|hosts
operator|=
name|event
operator|.
name|getHosts
argument_list|()
expr_stmt|;
name|this
operator|.
name|racks
operator|=
name|event
operator|.
name|getRacks
argument_list|()
expr_stmt|;
comment|//this.earlierAttemptFailed = event.getEarlierAttemptFailed();
name|this
operator|.
name|priority
operator|=
name|priority
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|init (Configuration conf)
specifier|public
name|void
name|init
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|super
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|nodeBlacklistingEnabled
operator|=
name|conf
operator|.
name|getBoolean
argument_list|(
name|MRJobConfig
operator|.
name|MR_AM_JOB_NODE_BLACKLISTING_ENABLE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"nodeBlacklistingEnabled:"
operator|+
name|nodeBlacklistingEnabled
argument_list|)
expr_stmt|;
name|maxTaskFailuresPerNode
operator|=
name|conf
operator|.
name|getInt
argument_list|(
name|MRJobConfig
operator|.
name|MAX_TASK_FAILURES_PER_TRACKER
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"maxTaskFailuresPerNode is "
operator|+
name|maxTaskFailuresPerNode
argument_list|)
expr_stmt|;
block|}
DECL|method|heartbeat ()
specifier|protected
specifier|abstract
name|void
name|heartbeat
parameter_list|()
throws|throws
name|Exception
function_decl|;
DECL|method|makeRemoteRequest ()
specifier|protected
name|AMResponse
name|makeRemoteRequest
parameter_list|()
throws|throws
name|YarnRemoteException
block|{
name|AllocateRequest
name|allocateRequest
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|AllocateRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|allocateRequest
operator|.
name|setApplicationAttemptId
argument_list|(
name|applicationAttemptId
argument_list|)
expr_stmt|;
name|allocateRequest
operator|.
name|setResponseId
argument_list|(
name|lastResponseID
argument_list|)
expr_stmt|;
name|allocateRequest
operator|.
name|addAllAsks
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|ResourceRequest
argument_list|>
argument_list|(
name|ask
argument_list|)
argument_list|)
expr_stmt|;
name|allocateRequest
operator|.
name|addAllReleases
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|ContainerId
argument_list|>
argument_list|(
name|release
argument_list|)
argument_list|)
expr_stmt|;
name|AllocateResponse
name|allocateResponse
init|=
name|scheduler
operator|.
name|allocate
argument_list|(
name|allocateRequest
argument_list|)
decl_stmt|;
name|AMResponse
name|response
init|=
name|allocateResponse
operator|.
name|getAMResponse
argument_list|()
decl_stmt|;
name|lastResponseID
operator|=
name|response
operator|.
name|getResponseId
argument_list|()
expr_stmt|;
name|availableResources
operator|=
name|response
operator|.
name|getAvailableResources
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"getResources() for "
operator|+
name|applicationId
operator|+
literal|":"
operator|+
literal|" ask="
operator|+
name|ask
operator|.
name|size
argument_list|()
operator|+
literal|" release= "
operator|+
name|release
operator|.
name|size
argument_list|()
operator|+
literal|" newContainers="
operator|+
name|response
operator|.
name|getAllocatedContainers
argument_list|()
operator|.
name|size
argument_list|()
operator|+
literal|" finishedContainers="
operator|+
name|response
operator|.
name|getCompletedContainersStatuses
argument_list|()
operator|.
name|size
argument_list|()
operator|+
literal|" resourcelimit="
operator|+
name|availableResources
argument_list|)
expr_stmt|;
name|ask
operator|.
name|clear
argument_list|()
expr_stmt|;
name|release
operator|.
name|clear
argument_list|()
expr_stmt|;
return|return
name|response
return|;
block|}
DECL|method|containerFailedOnHost (String hostName)
specifier|protected
name|void
name|containerFailedOnHost
parameter_list|(
name|String
name|hostName
parameter_list|)
block|{
if|if
condition|(
operator|!
name|nodeBlacklistingEnabled
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|blacklistedNodes
operator|.
name|contains
argument_list|(
name|hostName
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Host "
operator|+
name|hostName
operator|+
literal|" is already blacklisted."
argument_list|)
expr_stmt|;
return|return;
comment|//already blacklisted
block|}
name|Integer
name|failures
init|=
name|nodeFailures
operator|.
name|remove
argument_list|(
name|hostName
argument_list|)
decl_stmt|;
name|failures
operator|=
name|failures
operator|==
literal|null
condition|?
literal|0
else|:
name|failures
expr_stmt|;
name|failures
operator|++
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|failures
operator|+
literal|" failures on node "
operator|+
name|hostName
argument_list|)
expr_stmt|;
if|if
condition|(
name|failures
operator|>=
name|maxTaskFailuresPerNode
condition|)
block|{
name|blacklistedNodes
operator|.
name|add
argument_list|(
name|hostName
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Blacklisted host "
operator|+
name|hostName
argument_list|)
expr_stmt|;
comment|//remove all the requests corresponding to this hostname
for|for
control|(
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|Resource
argument_list|,
name|ResourceRequest
argument_list|>
argument_list|>
name|remoteRequests
range|:
name|remoteRequestsTable
operator|.
name|values
argument_list|()
control|)
block|{
comment|//remove from host
name|Map
argument_list|<
name|Resource
argument_list|,
name|ResourceRequest
argument_list|>
name|reqMap
init|=
name|remoteRequests
operator|.
name|remove
argument_list|(
name|hostName
argument_list|)
decl_stmt|;
if|if
condition|(
name|reqMap
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|ResourceRequest
name|req
range|:
name|reqMap
operator|.
name|values
argument_list|()
control|)
block|{
name|ask
operator|.
name|remove
argument_list|(
name|req
argument_list|)
expr_stmt|;
block|}
block|}
comment|//TODO: remove from rack
block|}
block|}
else|else
block|{
name|nodeFailures
operator|.
name|put
argument_list|(
name|hostName
argument_list|,
name|failures
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getAvailableResources ()
specifier|protected
name|Resource
name|getAvailableResources
parameter_list|()
block|{
return|return
name|availableResources
return|;
block|}
DECL|method|addContainerReq (ContainerRequest req)
specifier|protected
name|void
name|addContainerReq
parameter_list|(
name|ContainerRequest
name|req
parameter_list|)
block|{
comment|// Create resource requests
for|for
control|(
name|String
name|host
range|:
name|req
operator|.
name|hosts
control|)
block|{
comment|// Data-local
name|addResourceRequest
argument_list|(
name|req
operator|.
name|priority
argument_list|,
name|host
argument_list|,
name|req
operator|.
name|capability
argument_list|)
expr_stmt|;
block|}
comment|// Nothing Rack-local for now
for|for
control|(
name|String
name|rack
range|:
name|req
operator|.
name|racks
control|)
block|{
name|addResourceRequest
argument_list|(
name|req
operator|.
name|priority
argument_list|,
name|rack
argument_list|,
name|req
operator|.
name|capability
argument_list|)
expr_stmt|;
block|}
comment|// Off-switch
name|addResourceRequest
argument_list|(
name|req
operator|.
name|priority
argument_list|,
name|ANY
argument_list|,
name|req
operator|.
name|capability
argument_list|)
expr_stmt|;
block|}
DECL|method|decContainerReq (ContainerRequest req)
specifier|protected
name|void
name|decContainerReq
parameter_list|(
name|ContainerRequest
name|req
parameter_list|)
block|{
comment|// Update resource requests
for|for
control|(
name|String
name|hostName
range|:
name|req
operator|.
name|hosts
control|)
block|{
name|decResourceRequest
argument_list|(
name|req
operator|.
name|priority
argument_list|,
name|hostName
argument_list|,
name|req
operator|.
name|capability
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|rack
range|:
name|req
operator|.
name|racks
control|)
block|{
name|decResourceRequest
argument_list|(
name|req
operator|.
name|priority
argument_list|,
name|rack
argument_list|,
name|req
operator|.
name|capability
argument_list|)
expr_stmt|;
block|}
name|decResourceRequest
argument_list|(
name|req
operator|.
name|priority
argument_list|,
name|ANY
argument_list|,
name|req
operator|.
name|capability
argument_list|)
expr_stmt|;
block|}
DECL|method|addResourceRequest (Priority priority, String resourceName, Resource capability)
specifier|private
name|void
name|addResourceRequest
parameter_list|(
name|Priority
name|priority
parameter_list|,
name|String
name|resourceName
parameter_list|,
name|Resource
name|capability
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|Resource
argument_list|,
name|ResourceRequest
argument_list|>
argument_list|>
name|remoteRequests
init|=
name|this
operator|.
name|remoteRequestsTable
operator|.
name|get
argument_list|(
name|priority
argument_list|)
decl_stmt|;
if|if
condition|(
name|remoteRequests
operator|==
literal|null
condition|)
block|{
name|remoteRequests
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|Resource
argument_list|,
name|ResourceRequest
argument_list|>
argument_list|>
argument_list|()
expr_stmt|;
name|this
operator|.
name|remoteRequestsTable
operator|.
name|put
argument_list|(
name|priority
argument_list|,
name|remoteRequests
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Added priority="
operator|+
name|priority
argument_list|)
expr_stmt|;
block|}
name|Map
argument_list|<
name|Resource
argument_list|,
name|ResourceRequest
argument_list|>
name|reqMap
init|=
name|remoteRequests
operator|.
name|get
argument_list|(
name|resourceName
argument_list|)
decl_stmt|;
if|if
condition|(
name|reqMap
operator|==
literal|null
condition|)
block|{
name|reqMap
operator|=
operator|new
name|HashMap
argument_list|<
name|Resource
argument_list|,
name|ResourceRequest
argument_list|>
argument_list|()
expr_stmt|;
name|remoteRequests
operator|.
name|put
argument_list|(
name|resourceName
argument_list|,
name|reqMap
argument_list|)
expr_stmt|;
block|}
name|ResourceRequest
name|remoteRequest
init|=
name|reqMap
operator|.
name|get
argument_list|(
name|capability
argument_list|)
decl_stmt|;
if|if
condition|(
name|remoteRequest
operator|==
literal|null
condition|)
block|{
name|remoteRequest
operator|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|ResourceRequest
operator|.
name|class
argument_list|)
expr_stmt|;
name|remoteRequest
operator|.
name|setPriority
argument_list|(
name|priority
argument_list|)
expr_stmt|;
name|remoteRequest
operator|.
name|setHostName
argument_list|(
name|resourceName
argument_list|)
expr_stmt|;
name|remoteRequest
operator|.
name|setCapability
argument_list|(
name|capability
argument_list|)
expr_stmt|;
name|remoteRequest
operator|.
name|setNumContainers
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|reqMap
operator|.
name|put
argument_list|(
name|capability
argument_list|,
name|remoteRequest
argument_list|)
expr_stmt|;
block|}
name|remoteRequest
operator|.
name|setNumContainers
argument_list|(
name|remoteRequest
operator|.
name|getNumContainers
argument_list|()
operator|+
literal|1
argument_list|)
expr_stmt|;
comment|// Note this down for next interaction with ResourceManager
name|ask
operator|.
name|add
argument_list|(
name|remoteRequest
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"addResourceRequest:"
operator|+
literal|" applicationId="
operator|+
name|applicationId
operator|.
name|getId
argument_list|()
operator|+
literal|" priority="
operator|+
name|priority
operator|.
name|getPriority
argument_list|()
operator|+
literal|" resourceName="
operator|+
name|resourceName
operator|+
literal|" numContainers="
operator|+
name|remoteRequest
operator|.
name|getNumContainers
argument_list|()
operator|+
literal|" #asks="
operator|+
name|ask
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|decResourceRequest (Priority priority, String resourceName, Resource capability)
specifier|private
name|void
name|decResourceRequest
parameter_list|(
name|Priority
name|priority
parameter_list|,
name|String
name|resourceName
parameter_list|,
name|Resource
name|capability
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|Resource
argument_list|,
name|ResourceRequest
argument_list|>
argument_list|>
name|remoteRequests
init|=
name|this
operator|.
name|remoteRequestsTable
operator|.
name|get
argument_list|(
name|priority
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|Resource
argument_list|,
name|ResourceRequest
argument_list|>
name|reqMap
init|=
name|remoteRequests
operator|.
name|get
argument_list|(
name|resourceName
argument_list|)
decl_stmt|;
name|ResourceRequest
name|remoteRequest
init|=
name|reqMap
operator|.
name|get
argument_list|(
name|capability
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"BEFORE decResourceRequest:"
operator|+
literal|" applicationId="
operator|+
name|applicationId
operator|.
name|getId
argument_list|()
operator|+
literal|" priority="
operator|+
name|priority
operator|.
name|getPriority
argument_list|()
operator|+
literal|" resourceName="
operator|+
name|resourceName
operator|+
literal|" numContainers="
operator|+
name|remoteRequest
operator|.
name|getNumContainers
argument_list|()
operator|+
literal|" #asks="
operator|+
name|ask
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|remoteRequest
operator|.
name|setNumContainers
argument_list|(
name|remoteRequest
operator|.
name|getNumContainers
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|remoteRequest
operator|.
name|getNumContainers
argument_list|()
operator|==
literal|0
condition|)
block|{
name|reqMap
operator|.
name|remove
argument_list|(
name|capability
argument_list|)
expr_stmt|;
if|if
condition|(
name|reqMap
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|remoteRequests
operator|.
name|remove
argument_list|(
name|resourceName
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|remoteRequests
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|remoteRequestsTable
operator|.
name|remove
argument_list|(
name|priority
argument_list|)
expr_stmt|;
block|}
comment|//remove from ask if it may have
name|ask
operator|.
name|remove
argument_list|(
name|remoteRequest
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ask
operator|.
name|add
argument_list|(
name|remoteRequest
argument_list|)
expr_stmt|;
comment|//this will override the request if ask doesn't
comment|//already have it.
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"AFTER decResourceRequest:"
operator|+
literal|" applicationId="
operator|+
name|applicationId
operator|.
name|getId
argument_list|()
operator|+
literal|" priority="
operator|+
name|priority
operator|.
name|getPriority
argument_list|()
operator|+
literal|" resourceName="
operator|+
name|resourceName
operator|+
literal|" numContainers="
operator|+
name|remoteRequest
operator|.
name|getNumContainers
argument_list|()
operator|+
literal|" #asks="
operator|+
name|ask
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|release (ContainerId containerId)
specifier|protected
name|void
name|release
parameter_list|(
name|ContainerId
name|containerId
parameter_list|)
block|{
name|release
operator|.
name|add
argument_list|(
name|containerId
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

