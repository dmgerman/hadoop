begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager
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
package|;
end_package

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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
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
name|classification
operator|.
name|InterfaceAudience
operator|.
name|Private
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
name|net
operator|.
name|NetworkTopology
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
name|StartContainerRequest
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
name|StopContainerRequest
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
name|SubmitApplicationRequest
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
name|ApplicationSubmissionContext
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
name|ContainerLaunchContext
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
name|YarnException
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
name|Task
operator|.
name|State
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
name|scheduler
operator|.
name|NodeType
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
name|util
operator|.
name|resource
operator|.
name|Resources
import|;
end_import

begin_class
annotation|@
name|Private
DECL|class|Application
specifier|public
class|class
name|Application
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
name|Application
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|taskCounter
specifier|private
name|AtomicInteger
name|taskCounter
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|field|numAttempts
specifier|private
name|AtomicInteger
name|numAttempts
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|field|user
specifier|final
specifier|private
name|String
name|user
decl_stmt|;
DECL|field|queue
specifier|final
specifier|private
name|String
name|queue
decl_stmt|;
DECL|field|applicationId
specifier|final
specifier|private
name|ApplicationId
name|applicationId
decl_stmt|;
DECL|field|applicationAttemptId
specifier|final
specifier|private
name|ApplicationAttemptId
name|applicationAttemptId
decl_stmt|;
DECL|field|resourceManager
specifier|final
specifier|private
name|ResourceManager
name|resourceManager
decl_stmt|;
DECL|field|recordFactory
specifier|private
specifier|final
specifier|static
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
DECL|field|requestSpec
specifier|final
specifier|private
name|Map
argument_list|<
name|Priority
argument_list|,
name|Resource
argument_list|>
name|requestSpec
init|=
operator|new
name|TreeMap
argument_list|<
name|Priority
argument_list|,
name|Resource
argument_list|>
argument_list|(
operator|new
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
name|resource
operator|.
name|Priority
operator|.
name|Comparator
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|requests
specifier|final
specifier|private
name|Map
argument_list|<
name|Priority
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|ResourceRequest
argument_list|>
argument_list|>
name|requests
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
name|ResourceRequest
argument_list|>
argument_list|>
argument_list|(
operator|new
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
name|resource
operator|.
name|Priority
operator|.
name|Comparator
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|tasks
specifier|final
name|Map
argument_list|<
name|Priority
argument_list|,
name|Set
argument_list|<
name|Task
argument_list|>
argument_list|>
name|tasks
init|=
operator|new
name|TreeMap
argument_list|<
name|Priority
argument_list|,
name|Set
argument_list|<
name|Task
argument_list|>
argument_list|>
argument_list|(
operator|new
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
name|resource
operator|.
name|Priority
operator|.
name|Comparator
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|ask
specifier|final
specifier|private
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
argument_list|(
operator|new
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
operator|.
name|ResourceRequestComparator
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|nodes
specifier|final
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|NodeManager
argument_list|>
name|nodes
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|NodeManager
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|used
name|Resource
name|used
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|Resource
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|Application (String user, ResourceManager resourceManager)
specifier|public
name|Application
parameter_list|(
name|String
name|user
parameter_list|,
name|ResourceManager
name|resourceManager
parameter_list|)
block|{
name|this
argument_list|(
name|user
argument_list|,
literal|"default"
argument_list|,
name|resourceManager
argument_list|)
expr_stmt|;
block|}
DECL|method|Application (String user, String queue, ResourceManager resourceManager)
specifier|public
name|Application
parameter_list|(
name|String
name|user
parameter_list|,
name|String
name|queue
parameter_list|,
name|ResourceManager
name|resourceManager
parameter_list|)
block|{
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
name|this
operator|.
name|queue
operator|=
name|queue
expr_stmt|;
name|this
operator|.
name|resourceManager
operator|=
name|resourceManager
expr_stmt|;
name|this
operator|.
name|applicationId
operator|=
name|this
operator|.
name|resourceManager
operator|.
name|getClientRMService
argument_list|()
operator|.
name|getNewApplicationId
argument_list|()
expr_stmt|;
name|this
operator|.
name|applicationAttemptId
operator|=
name|ApplicationAttemptId
operator|.
name|newInstance
argument_list|(
name|this
operator|.
name|applicationId
argument_list|,
name|this
operator|.
name|numAttempts
operator|.
name|getAndIncrement
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|getUser ()
specifier|public
name|String
name|getUser
parameter_list|()
block|{
return|return
name|user
return|;
block|}
DECL|method|getQueue ()
specifier|public
name|String
name|getQueue
parameter_list|()
block|{
return|return
name|queue
return|;
block|}
DECL|method|getApplicationId ()
specifier|public
name|ApplicationId
name|getApplicationId
parameter_list|()
block|{
return|return
name|applicationId
return|;
block|}
DECL|method|resolve (String hostName)
specifier|public
specifier|static
name|String
name|resolve
parameter_list|(
name|String
name|hostName
parameter_list|)
block|{
return|return
name|NetworkTopology
operator|.
name|DEFAULT_RACK
return|;
block|}
DECL|method|getNextTaskId ()
specifier|public
name|int
name|getNextTaskId
parameter_list|()
block|{
return|return
name|taskCounter
operator|.
name|incrementAndGet
argument_list|()
return|;
block|}
DECL|method|getUsedResources ()
specifier|public
name|Resource
name|getUsedResources
parameter_list|()
block|{
return|return
name|used
return|;
block|}
DECL|method|submit ()
specifier|public
specifier|synchronized
name|void
name|submit
parameter_list|()
throws|throws
name|IOException
throws|,
name|YarnException
block|{
name|ApplicationSubmissionContext
name|context
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|ApplicationSubmissionContext
operator|.
name|class
argument_list|)
decl_stmt|;
name|context
operator|.
name|setApplicationId
argument_list|(
name|this
operator|.
name|applicationId
argument_list|)
expr_stmt|;
name|context
operator|.
name|setQueue
argument_list|(
name|this
operator|.
name|queue
argument_list|)
expr_stmt|;
name|SubmitApplicationRequest
name|request
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|SubmitApplicationRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|request
operator|.
name|setApplicationSubmissionContext
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|resourceManager
operator|.
name|getClientRMService
argument_list|()
operator|.
name|submitApplication
argument_list|(
name|request
argument_list|)
expr_stmt|;
block|}
DECL|method|addResourceRequestSpec ( Priority priority, Resource capability)
specifier|public
specifier|synchronized
name|void
name|addResourceRequestSpec
parameter_list|(
name|Priority
name|priority
parameter_list|,
name|Resource
name|capability
parameter_list|)
block|{
name|Resource
name|currentSpec
init|=
name|requestSpec
operator|.
name|put
argument_list|(
name|priority
argument_list|,
name|capability
argument_list|)
decl_stmt|;
if|if
condition|(
name|currentSpec
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Resource spec already exists for "
operator|+
literal|"priority "
operator|+
name|priority
operator|.
name|getPriority
argument_list|()
operator|+
literal|" - "
operator|+
name|currentSpec
operator|.
name|getMemory
argument_list|()
argument_list|)
throw|;
block|}
block|}
DECL|method|addNodeManager (String host, int containerManagerPort, NodeManager nodeManager)
specifier|public
specifier|synchronized
name|void
name|addNodeManager
parameter_list|(
name|String
name|host
parameter_list|,
name|int
name|containerManagerPort
parameter_list|,
name|NodeManager
name|nodeManager
parameter_list|)
block|{
name|nodes
operator|.
name|put
argument_list|(
name|host
operator|+
literal|":"
operator|+
name|containerManagerPort
argument_list|,
name|nodeManager
argument_list|)
expr_stmt|;
block|}
DECL|method|getNodeManager (String host)
specifier|private
specifier|synchronized
name|NodeManager
name|getNodeManager
parameter_list|(
name|String
name|host
parameter_list|)
block|{
return|return
name|nodes
operator|.
name|get
argument_list|(
name|host
argument_list|)
return|;
block|}
DECL|method|addTask (Task task)
specifier|public
specifier|synchronized
name|void
name|addTask
parameter_list|(
name|Task
name|task
parameter_list|)
block|{
name|Priority
name|priority
init|=
name|task
operator|.
name|getPriority
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|ResourceRequest
argument_list|>
name|requests
init|=
name|this
operator|.
name|requests
operator|.
name|get
argument_list|(
name|priority
argument_list|)
decl_stmt|;
if|if
condition|(
name|requests
operator|==
literal|null
condition|)
block|{
name|requests
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|ResourceRequest
argument_list|>
argument_list|()
expr_stmt|;
name|this
operator|.
name|requests
operator|.
name|put
argument_list|(
name|priority
argument_list|,
name|requests
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Added priority="
operator|+
name|priority
operator|+
literal|" application="
operator|+
name|applicationId
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|Resource
name|capability
init|=
name|requestSpec
operator|.
name|get
argument_list|(
name|priority
argument_list|)
decl_stmt|;
comment|// Note down the task
name|Set
argument_list|<
name|Task
argument_list|>
name|tasks
init|=
name|this
operator|.
name|tasks
operator|.
name|get
argument_list|(
name|priority
argument_list|)
decl_stmt|;
if|if
condition|(
name|tasks
operator|==
literal|null
condition|)
block|{
name|tasks
operator|=
operator|new
name|HashSet
argument_list|<
name|Task
argument_list|>
argument_list|()
expr_stmt|;
name|this
operator|.
name|tasks
operator|.
name|put
argument_list|(
name|priority
argument_list|,
name|tasks
argument_list|)
expr_stmt|;
block|}
name|tasks
operator|.
name|add
argument_list|(
name|task
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Added task "
operator|+
name|task
operator|.
name|getTaskId
argument_list|()
operator|+
literal|" to application "
operator|+
name|applicationId
operator|+
literal|" at priority "
operator|+
name|priority
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"addTask: application="
operator|+
name|applicationId
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
comment|// Create resource requests
for|for
control|(
name|String
name|host
range|:
name|task
operator|.
name|getHosts
argument_list|()
control|)
block|{
comment|// Data-local
name|addResourceRequest
argument_list|(
name|priority
argument_list|,
name|requests
argument_list|,
name|host
argument_list|,
name|capability
argument_list|)
expr_stmt|;
block|}
comment|// Rack-local
for|for
control|(
name|String
name|rack
range|:
name|task
operator|.
name|getRacks
argument_list|()
control|)
block|{
name|addResourceRequest
argument_list|(
name|priority
argument_list|,
name|requests
argument_list|,
name|rack
argument_list|,
name|capability
argument_list|)
expr_stmt|;
block|}
comment|// Off-switch
name|addResourceRequest
argument_list|(
name|priority
argument_list|,
name|requests
argument_list|,
name|ResourceRequest
operator|.
name|ANY
argument_list|,
name|capability
argument_list|)
expr_stmt|;
block|}
DECL|method|finishTask (Task task)
specifier|public
specifier|synchronized
name|void
name|finishTask
parameter_list|(
name|Task
name|task
parameter_list|)
throws|throws
name|IOException
throws|,
name|YarnException
block|{
name|Set
argument_list|<
name|Task
argument_list|>
name|tasks
init|=
name|this
operator|.
name|tasks
operator|.
name|get
argument_list|(
name|task
operator|.
name|getPriority
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|tasks
operator|.
name|remove
argument_list|(
name|task
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Finishing unknown task "
operator|+
name|task
operator|.
name|getTaskId
argument_list|()
operator|+
literal|" from application "
operator|+
name|applicationId
argument_list|)
throw|;
block|}
name|NodeManager
name|nodeManager
init|=
name|task
operator|.
name|getNodeManager
argument_list|()
decl_stmt|;
name|ContainerId
name|containerId
init|=
name|task
operator|.
name|getContainerId
argument_list|()
decl_stmt|;
name|task
operator|.
name|stop
argument_list|()
expr_stmt|;
name|StopContainerRequest
name|stopRequest
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|StopContainerRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|stopRequest
operator|.
name|setContainerId
argument_list|(
name|containerId
argument_list|)
expr_stmt|;
name|nodeManager
operator|.
name|stopContainer
argument_list|(
name|stopRequest
argument_list|)
expr_stmt|;
name|Resources
operator|.
name|subtractFrom
argument_list|(
name|used
argument_list|,
name|requestSpec
operator|.
name|get
argument_list|(
name|task
operator|.
name|getPriority
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Finished task "
operator|+
name|task
operator|.
name|getTaskId
argument_list|()
operator|+
literal|" of application "
operator|+
name|applicationId
operator|+
literal|" on node "
operator|+
name|nodeManager
operator|.
name|getHostName
argument_list|()
operator|+
literal|", currently using "
operator|+
name|used
operator|+
literal|" resources"
argument_list|)
expr_stmt|;
block|}
DECL|method|addResourceRequest ( Priority priority, Map<String, ResourceRequest> requests, String resourceName, Resource capability)
specifier|private
specifier|synchronized
name|void
name|addResourceRequest
parameter_list|(
name|Priority
name|priority
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|ResourceRequest
argument_list|>
name|requests
parameter_list|,
name|String
name|resourceName
parameter_list|,
name|Resource
name|capability
parameter_list|)
block|{
name|ResourceRequest
name|request
init|=
name|requests
operator|.
name|get
argument_list|(
name|resourceName
argument_list|)
decl_stmt|;
if|if
condition|(
name|request
operator|==
literal|null
condition|)
block|{
name|request
operator|=
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
name|utils
operator|.
name|BuilderUtils
operator|.
name|newResourceRequest
argument_list|(
name|priority
argument_list|,
name|resourceName
argument_list|,
name|capability
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|requests
operator|.
name|put
argument_list|(
name|resourceName
argument_list|,
name|request
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|request
operator|.
name|setNumContainers
argument_list|(
name|request
operator|.
name|getNumContainers
argument_list|()
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
comment|// Note this down for next interaction with ResourceManager
name|ask
operator|.
name|remove
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|ask
operator|.
name|add
argument_list|(
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
name|utils
operator|.
name|BuilderUtils
operator|.
name|newResourceRequest
argument_list|(
name|request
argument_list|)
argument_list|)
expr_stmt|;
comment|// clone to ensure the RM doesn't manipulate the same obj
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"addResourceRequest: applicationId="
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
literal|" capability="
operator|+
name|capability
operator|+
literal|" numContainers="
operator|+
name|request
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
block|}
DECL|method|getResources ()
specifier|public
specifier|synchronized
name|List
argument_list|<
name|Container
argument_list|>
name|getResources
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"getResources begin:"
operator|+
literal|" application="
operator|+
name|applicationId
operator|+
literal|" #ask="
operator|+
name|ask
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|ResourceRequest
name|request
range|:
name|ask
control|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"getResources:"
operator|+
literal|" application="
operator|+
name|applicationId
operator|+
literal|" ask-request="
operator|+
name|request
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Get resources from the ResourceManager
name|resourceManager
operator|.
name|getResourceScheduler
argument_list|()
operator|.
name|allocate
argument_list|(
name|applicationAttemptId
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|ResourceRequest
argument_list|>
argument_list|(
name|ask
argument_list|)
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|ContainerId
argument_list|>
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"-======="
operator|+
name|applicationAttemptId
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"----------"
operator|+
name|resourceManager
operator|.
name|getRMContext
argument_list|()
operator|.
name|getRMApps
argument_list|()
operator|.
name|get
argument_list|(
name|applicationId
argument_list|)
operator|.
name|getRMAppAttempt
argument_list|(
name|applicationAttemptId
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Container
argument_list|>
name|containers
init|=
literal|null
decl_stmt|;
comment|// TODO: Fix
comment|//       resourceManager.getRMContext().getRMApps()
comment|//        .get(applicationId).getRMAppAttempt(applicationAttemptId)
comment|//        .pullNewlyAllocatedContainers();
comment|// Clear state for next interaction with ResourceManager
name|ask
operator|.
name|clear
argument_list|()
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
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
literal|" recieved="
operator|+
name|containers
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|containers
return|;
block|}
DECL|method|assign (List<Container> containers)
specifier|public
specifier|synchronized
name|void
name|assign
parameter_list|(
name|List
argument_list|<
name|Container
argument_list|>
name|containers
parameter_list|)
throws|throws
name|IOException
throws|,
name|YarnException
block|{
name|int
name|numContainers
init|=
name|containers
operator|.
name|size
argument_list|()
decl_stmt|;
comment|// Schedule in priority order
for|for
control|(
name|Priority
name|priority
range|:
name|requests
operator|.
name|keySet
argument_list|()
control|)
block|{
name|assign
argument_list|(
name|priority
argument_list|,
name|NodeType
operator|.
name|NODE_LOCAL
argument_list|,
name|containers
argument_list|)
expr_stmt|;
name|assign
argument_list|(
name|priority
argument_list|,
name|NodeType
operator|.
name|RACK_LOCAL
argument_list|,
name|containers
argument_list|)
expr_stmt|;
name|assign
argument_list|(
name|priority
argument_list|,
name|NodeType
operator|.
name|OFF_SWITCH
argument_list|,
name|containers
argument_list|)
expr_stmt|;
if|if
condition|(
name|containers
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
break|break;
block|}
block|}
name|int
name|assignedContainers
init|=
name|numContainers
operator|-
name|containers
operator|.
name|size
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Application "
operator|+
name|applicationId
operator|+
literal|" assigned "
operator|+
name|assignedContainers
operator|+
literal|"/"
operator|+
name|numContainers
argument_list|)
expr_stmt|;
block|}
DECL|method|schedule ()
specifier|public
specifier|synchronized
name|void
name|schedule
parameter_list|()
throws|throws
name|IOException
throws|,
name|YarnException
block|{
name|assign
argument_list|(
name|getResources
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|assign (Priority priority, NodeType type, List<Container> containers)
specifier|private
specifier|synchronized
name|void
name|assign
parameter_list|(
name|Priority
name|priority
parameter_list|,
name|NodeType
name|type
parameter_list|,
name|List
argument_list|<
name|Container
argument_list|>
name|containers
parameter_list|)
throws|throws
name|IOException
throws|,
name|YarnException
block|{
for|for
control|(
name|Iterator
argument_list|<
name|Container
argument_list|>
name|i
init|=
name|containers
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Container
name|container
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|host
init|=
name|container
operator|.
name|getNodeId
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|Resources
operator|.
name|equals
argument_list|(
name|requestSpec
operator|.
name|get
argument_list|(
name|priority
argument_list|)
argument_list|,
name|container
operator|.
name|getResource
argument_list|()
argument_list|)
condition|)
block|{
comment|// See which task can use this container
for|for
control|(
name|Iterator
argument_list|<
name|Task
argument_list|>
name|t
init|=
name|tasks
operator|.
name|get
argument_list|(
name|priority
argument_list|)
operator|.
name|iterator
argument_list|()
init|;
name|t
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Task
name|task
init|=
name|t
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|task
operator|.
name|getState
argument_list|()
operator|==
name|State
operator|.
name|PENDING
operator|&&
name|task
operator|.
name|canSchedule
argument_list|(
name|type
argument_list|,
name|host
argument_list|)
condition|)
block|{
name|NodeManager
name|nodeManager
init|=
name|getNodeManager
argument_list|(
name|host
argument_list|)
decl_stmt|;
name|task
operator|.
name|start
argument_list|(
name|nodeManager
argument_list|,
name|container
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|i
operator|.
name|remove
argument_list|()
expr_stmt|;
comment|// Track application resource usage
name|Resources
operator|.
name|addTo
argument_list|(
name|used
argument_list|,
name|container
operator|.
name|getResource
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Assigned container ("
operator|+
name|container
operator|+
literal|") of type "
operator|+
name|type
operator|+
literal|" to task "
operator|+
name|task
operator|.
name|getTaskId
argument_list|()
operator|+
literal|" at priority "
operator|+
name|priority
operator|+
literal|" on node "
operator|+
name|nodeManager
operator|.
name|getHostName
argument_list|()
operator|+
literal|", currently using "
operator|+
name|used
operator|+
literal|" resources"
argument_list|)
expr_stmt|;
comment|// Update resource requests
name|updateResourceRequests
argument_list|(
name|requests
operator|.
name|get
argument_list|(
name|priority
argument_list|)
argument_list|,
name|type
argument_list|,
name|task
argument_list|)
expr_stmt|;
comment|// Launch the container
name|StartContainerRequest
name|startRequest
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|StartContainerRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|startRequest
operator|.
name|setContainerLaunchContext
argument_list|(
name|createCLC
argument_list|()
argument_list|)
expr_stmt|;
name|startRequest
operator|.
name|setContainerToken
argument_list|(
name|container
operator|.
name|getContainerToken
argument_list|()
argument_list|)
expr_stmt|;
name|nodeManager
operator|.
name|startContainer
argument_list|(
name|startRequest
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
block|}
DECL|method|updateResourceRequests (Map<String, ResourceRequest> requests, NodeType type, Task task)
specifier|private
name|void
name|updateResourceRequests
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|ResourceRequest
argument_list|>
name|requests
parameter_list|,
name|NodeType
name|type
parameter_list|,
name|Task
name|task
parameter_list|)
block|{
if|if
condition|(
name|type
operator|==
name|NodeType
operator|.
name|NODE_LOCAL
condition|)
block|{
for|for
control|(
name|String
name|host
range|:
name|task
operator|.
name|getHosts
argument_list|()
control|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"updateResourceRequests:"
operator|+
literal|" application="
operator|+
name|applicationId
operator|+
literal|" type="
operator|+
name|type
operator|+
literal|" host="
operator|+
name|host
operator|+
literal|" request="
operator|+
operator|(
operator|(
name|requests
operator|==
literal|null
operator|)
condition|?
literal|"null"
else|:
name|requests
operator|.
name|get
argument_list|(
name|host
argument_list|)
operator|)
argument_list|)
expr_stmt|;
block|}
name|updateResourceRequest
argument_list|(
name|requests
operator|.
name|get
argument_list|(
name|host
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|type
operator|==
name|NodeType
operator|.
name|NODE_LOCAL
operator|||
name|type
operator|==
name|NodeType
operator|.
name|RACK_LOCAL
condition|)
block|{
for|for
control|(
name|String
name|rack
range|:
name|task
operator|.
name|getRacks
argument_list|()
control|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"updateResourceRequests:"
operator|+
literal|" application="
operator|+
name|applicationId
operator|+
literal|" type="
operator|+
name|type
operator|+
literal|" rack="
operator|+
name|rack
operator|+
literal|" request="
operator|+
operator|(
operator|(
name|requests
operator|==
literal|null
operator|)
condition|?
literal|"null"
else|:
name|requests
operator|.
name|get
argument_list|(
name|rack
argument_list|)
operator|)
argument_list|)
expr_stmt|;
block|}
name|updateResourceRequest
argument_list|(
name|requests
operator|.
name|get
argument_list|(
name|rack
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|updateResourceRequest
argument_list|(
name|requests
operator|.
name|get
argument_list|(
name|ResourceRequest
operator|.
name|ANY
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"updateResourceRequests:"
operator|+
literal|" application="
operator|+
name|applicationId
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
block|}
DECL|method|updateResourceRequest (ResourceRequest request)
specifier|private
name|void
name|updateResourceRequest
parameter_list|(
name|ResourceRequest
name|request
parameter_list|)
block|{
name|request
operator|.
name|setNumContainers
argument_list|(
name|request
operator|.
name|getNumContainers
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
comment|// Note this for next interaction with ResourceManager
name|ask
operator|.
name|remove
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|ask
operator|.
name|add
argument_list|(
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
name|utils
operator|.
name|BuilderUtils
operator|.
name|newResourceRequest
argument_list|(
name|request
argument_list|)
argument_list|)
expr_stmt|;
comment|// clone to ensure the RM doesn't manipulate the same obj
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"updateResourceRequest:"
operator|+
literal|" application="
operator|+
name|applicationId
operator|+
literal|" request="
operator|+
name|request
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|createCLC ()
specifier|private
name|ContainerLaunchContext
name|createCLC
parameter_list|()
block|{
name|ContainerLaunchContext
name|clc
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|ContainerLaunchContext
operator|.
name|class
argument_list|)
decl_stmt|;
return|return
name|clc
return|;
block|}
block|}
end_class

end_unit

