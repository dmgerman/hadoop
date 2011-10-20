begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.app.local
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
name|local
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
name|mapreduce
operator|.
name|JobCounter
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
name|TaskType
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
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|job
operator|.
name|event
operator|.
name|JobCounterUpdateEvent
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
name|job
operator|.
name|event
operator|.
name|TaskAttemptContainerAssignedEvent
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
name|rm
operator|.
name|ContainerAllocator
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
name|rm
operator|.
name|ContainerAllocatorEvent
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
name|rm
operator|.
name|RMCommunicator
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
name|util
operator|.
name|BuilderUtils
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
name|Records
import|;
end_import

begin_comment
comment|/**  * Allocates containers locally. Doesn't allocate a real container;  * instead sends an allocated event for all requests.  */
end_comment

begin_class
DECL|class|LocalContainerAllocator
specifier|public
class|class
name|LocalContainerAllocator
extends|extends
name|RMCommunicator
implements|implements
name|ContainerAllocator
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
name|LocalContainerAllocator
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|eventHandler
specifier|private
specifier|final
name|EventHandler
name|eventHandler
decl_stmt|;
DECL|field|appID
specifier|private
specifier|final
name|ApplicationId
name|appID
decl_stmt|;
DECL|field|containerCount
specifier|private
name|AtomicInteger
name|containerCount
init|=
operator|new
name|AtomicInteger
argument_list|()
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
DECL|method|LocalContainerAllocator (ClientService clientService, AppContext context)
specifier|public
name|LocalContainerAllocator
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
name|this
operator|.
name|eventHandler
operator|=
name|context
operator|.
name|getEventHandler
argument_list|()
expr_stmt|;
name|this
operator|.
name|appID
operator|=
name|context
operator|.
name|getApplicationID
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|heartbeat ()
specifier|protected
specifier|synchronized
name|void
name|heartbeat
parameter_list|()
throws|throws
name|Exception
block|{
name|AllocateRequest
name|allocateRequest
init|=
name|BuilderUtils
operator|.
name|newAllocateRequest
argument_list|(
name|this
operator|.
name|applicationAttemptId
argument_list|,
name|this
operator|.
name|lastResponseID
argument_list|,
name|super
operator|.
name|getApplicationProgress
argument_list|()
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|ResourceRequest
argument_list|>
argument_list|()
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|ContainerId
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
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
if|if
condition|(
name|response
operator|.
name|getReboot
argument_list|()
condition|)
block|{
comment|// TODO
name|LOG
operator|.
name|info
argument_list|(
literal|"Event from RM: shutting down Application Master"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|handle (ContainerAllocatorEvent event)
specifier|public
name|void
name|handle
parameter_list|(
name|ContainerAllocatorEvent
name|event
parameter_list|)
block|{
if|if
condition|(
name|event
operator|.
name|getType
argument_list|()
operator|==
name|ContainerAllocator
operator|.
name|EventType
operator|.
name|CONTAINER_REQ
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Processing the event "
operator|+
name|event
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|ContainerId
name|cID
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|ContainerId
operator|.
name|class
argument_list|)
decl_stmt|;
name|cID
operator|.
name|setApplicationAttemptId
argument_list|(
name|applicationAttemptId
argument_list|)
expr_stmt|;
comment|// use negative ids to denote that these are local. Need a better way ??
name|cID
operator|.
name|setId
argument_list|(
operator|(
operator|-
literal|1
operator|)
operator|*
name|containerCount
operator|.
name|getAndIncrement
argument_list|()
argument_list|)
expr_stmt|;
name|Container
name|container
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|Container
operator|.
name|class
argument_list|)
decl_stmt|;
name|container
operator|.
name|setId
argument_list|(
name|cID
argument_list|)
expr_stmt|;
name|NodeId
name|nodeId
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|NodeId
operator|.
name|class
argument_list|)
decl_stmt|;
name|nodeId
operator|.
name|setHost
argument_list|(
literal|"localhost"
argument_list|)
expr_stmt|;
name|nodeId
operator|.
name|setPort
argument_list|(
literal|1234
argument_list|)
expr_stmt|;
name|container
operator|.
name|setNodeId
argument_list|(
name|nodeId
argument_list|)
expr_stmt|;
name|container
operator|.
name|setContainerToken
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|container
operator|.
name|setNodeHttpAddress
argument_list|(
literal|"localhost:9999"
argument_list|)
expr_stmt|;
comment|// send the container-assigned event to task attempt
if|if
condition|(
name|event
operator|.
name|getAttemptID
argument_list|()
operator|.
name|getTaskId
argument_list|()
operator|.
name|getTaskType
argument_list|()
operator|==
name|TaskType
operator|.
name|MAP
condition|)
block|{
name|JobCounterUpdateEvent
name|jce
init|=
operator|new
name|JobCounterUpdateEvent
argument_list|(
name|event
operator|.
name|getAttemptID
argument_list|()
operator|.
name|getTaskId
argument_list|()
operator|.
name|getJobId
argument_list|()
argument_list|)
decl_stmt|;
comment|// TODO Setting OTHER_LOCAL_MAP for now.
name|jce
operator|.
name|addCounterUpdate
argument_list|(
name|JobCounter
operator|.
name|OTHER_LOCAL_MAPS
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|eventHandler
operator|.
name|handle
argument_list|(
name|jce
argument_list|)
expr_stmt|;
block|}
name|eventHandler
operator|.
name|handle
argument_list|(
operator|new
name|TaskAttemptContainerAssignedEvent
argument_list|(
name|event
operator|.
name|getAttemptID
argument_list|()
argument_list|,
name|container
argument_list|,
name|applicationACLs
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

