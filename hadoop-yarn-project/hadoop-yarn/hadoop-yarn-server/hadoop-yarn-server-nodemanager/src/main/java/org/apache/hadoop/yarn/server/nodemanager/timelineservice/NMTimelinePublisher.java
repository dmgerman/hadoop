begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.timelineservice
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
name|timelineservice
package|;
end_package

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
name|service
operator|.
name|CompositeService
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
name|api
operator|.
name|records
operator|.
name|ContainerStatus
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
name|api
operator|.
name|records
operator|.
name|timelineservice
operator|.
name|ContainerEntity
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
name|timelineservice
operator|.
name|TimelineEntity
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
name|timelineservice
operator|.
name|TimelineEntity
operator|.
name|Identifier
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
name|timelineservice
operator|.
name|TimelineEntityType
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
name|timelineservice
operator|.
name|TimelineEvent
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
name|timelineservice
operator|.
name|TimelineMetric
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
name|client
operator|.
name|api
operator|.
name|TimelineClient
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
name|AsyncDispatcher
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
name|metrics
operator|.
name|ContainerMetricsConstants
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
name|application
operator|.
name|ApplicationContainerFinishedEvent
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
name|application
operator|.
name|ApplicationEvent
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
name|application
operator|.
name|ApplicationEventType
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
name|ContainerEvent
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
name|ContainerEventType
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
name|LocalizationEvent
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
name|monitor
operator|.
name|ContainersMonitorImpl
operator|.
name|ContainerMetric
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
name|ResourceCalculatorProcessTree
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
name|timeline
operator|.
name|TimelineUtils
import|;
end_import

begin_comment
comment|/**  * Metrics publisher service that publishes data to the timeline service v.2. It  * is used only if the timeline service v.2 is enabled and the system publishing  * of events and metrics is enabled.  */
end_comment

begin_class
DECL|class|NMTimelinePublisher
specifier|public
class|class
name|NMTimelinePublisher
extends|extends
name|CompositeService
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
name|NMTimelinePublisher
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|dispatcher
specifier|private
name|Dispatcher
name|dispatcher
decl_stmt|;
DECL|field|context
specifier|private
name|Context
name|context
decl_stmt|;
DECL|field|nodeId
specifier|private
name|NodeId
name|nodeId
decl_stmt|;
DECL|field|httpAddress
specifier|private
name|String
name|httpAddress
decl_stmt|;
DECL|method|NMTimelinePublisher (Context context)
specifier|public
name|NMTimelinePublisher
parameter_list|(
name|Context
name|context
parameter_list|)
block|{
name|super
argument_list|(
name|NMTimelinePublisher
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceInit (Configuration conf)
specifier|protected
name|void
name|serviceInit
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|Exception
block|{
name|dispatcher
operator|=
operator|new
name|AsyncDispatcher
argument_list|()
expr_stmt|;
name|dispatcher
operator|.
name|register
argument_list|(
name|NMTimelineEventType
operator|.
name|class
argument_list|,
operator|new
name|ForwardingEventHandler
argument_list|()
argument_list|)
expr_stmt|;
name|dispatcher
operator|.
name|register
argument_list|(
name|ContainerEventType
operator|.
name|class
argument_list|,
operator|new
name|ContainerEventHandler
argument_list|()
argument_list|)
expr_stmt|;
name|dispatcher
operator|.
name|register
argument_list|(
name|ApplicationEventType
operator|.
name|class
argument_list|,
operator|new
name|ApplicationEventHandler
argument_list|()
argument_list|)
expr_stmt|;
name|dispatcher
operator|.
name|register
argument_list|(
name|LocalizationEventType
operator|.
name|class
argument_list|,
operator|new
name|LocalizationEventDispatcher
argument_list|()
argument_list|)
expr_stmt|;
name|addIfService
argument_list|(
name|dispatcher
argument_list|)
expr_stmt|;
name|super
operator|.
name|serviceInit
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceStart ()
specifier|protected
name|void
name|serviceStart
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|serviceStart
argument_list|()
expr_stmt|;
comment|// context will be updated after containerManagerImpl is started
comment|// hence NMMetricsPublisher is added subservice of containerManagerImpl
name|this
operator|.
name|nodeId
operator|=
name|context
operator|.
name|getNodeId
argument_list|()
expr_stmt|;
name|this
operator|.
name|httpAddress
operator|=
name|nodeId
operator|.
name|getHost
argument_list|()
operator|+
literal|":"
operator|+
name|context
operator|.
name|getHttpPort
argument_list|()
expr_stmt|;
block|}
DECL|method|handleNMTimelineEvent (NMTimelineEvent event)
specifier|protected
name|void
name|handleNMTimelineEvent
parameter_list|(
name|NMTimelineEvent
name|event
parameter_list|)
block|{
switch|switch
condition|(
name|event
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|TIMELINE_ENTITY_PUBLISH
case|:
name|putEntity
argument_list|(
operator|(
operator|(
name|TimelinePublishEvent
operator|)
name|event
operator|)
operator|.
name|getTimelineEntityToPublish
argument_list|()
argument_list|,
operator|(
operator|(
name|TimelinePublishEvent
operator|)
name|event
operator|)
operator|.
name|getApplicationId
argument_list|()
argument_list|)
expr_stmt|;
break|break;
default|default:
name|LOG
operator|.
name|error
argument_list|(
literal|"Unknown NMTimelineEvent type: "
operator|+
name|event
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|reportContainerResourceUsage (Container container, Long pmemUsage, Float cpuUsagePercentPerCore)
specifier|public
name|void
name|reportContainerResourceUsage
parameter_list|(
name|Container
name|container
parameter_list|,
name|Long
name|pmemUsage
parameter_list|,
name|Float
name|cpuUsagePercentPerCore
parameter_list|)
block|{
if|if
condition|(
name|pmemUsage
operator|!=
name|ResourceCalculatorProcessTree
operator|.
name|UNAVAILABLE
operator|||
name|cpuUsagePercentPerCore
operator|!=
name|ResourceCalculatorProcessTree
operator|.
name|UNAVAILABLE
condition|)
block|{
name|ContainerEntity
name|entity
init|=
name|createContainerEntity
argument_list|(
name|container
operator|.
name|getContainerId
argument_list|()
argument_list|)
decl_stmt|;
name|long
name|currentTimeMillis
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
if|if
condition|(
name|pmemUsage
operator|!=
name|ResourceCalculatorProcessTree
operator|.
name|UNAVAILABLE
condition|)
block|{
name|TimelineMetric
name|memoryMetric
init|=
operator|new
name|TimelineMetric
argument_list|()
decl_stmt|;
name|memoryMetric
operator|.
name|setId
argument_list|(
name|ContainerMetric
operator|.
name|MEMORY
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|memoryMetric
operator|.
name|addValue
argument_list|(
name|currentTimeMillis
argument_list|,
name|pmemUsage
argument_list|)
expr_stmt|;
name|entity
operator|.
name|addMetric
argument_list|(
name|memoryMetric
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|cpuUsagePercentPerCore
operator|!=
name|ResourceCalculatorProcessTree
operator|.
name|UNAVAILABLE
condition|)
block|{
name|TimelineMetric
name|cpuMetric
init|=
operator|new
name|TimelineMetric
argument_list|()
decl_stmt|;
name|cpuMetric
operator|.
name|setId
argument_list|(
name|ContainerMetric
operator|.
name|CPU
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|cpuMetric
operator|.
name|addValue
argument_list|(
name|currentTimeMillis
argument_list|,
name|Math
operator|.
name|round
argument_list|(
name|cpuUsagePercentPerCore
argument_list|)
argument_list|)
expr_stmt|;
name|entity
operator|.
name|addMetric
argument_list|(
name|cpuMetric
argument_list|)
expr_stmt|;
block|}
name|dispatcher
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|TimelinePublishEvent
argument_list|(
name|entity
argument_list|,
name|container
operator|.
name|getContainerId
argument_list|()
operator|.
name|getApplicationAttemptId
argument_list|()
operator|.
name|getApplicationId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|publishContainerCreatedEvent (ContainerEntity entity, ContainerId containerId, Resource resource, Priority priority, long timestamp)
specifier|private
name|void
name|publishContainerCreatedEvent
parameter_list|(
name|ContainerEntity
name|entity
parameter_list|,
name|ContainerId
name|containerId
parameter_list|,
name|Resource
name|resource
parameter_list|,
name|Priority
name|priority
parameter_list|,
name|long
name|timestamp
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|entityInfo
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|entityInfo
operator|.
name|put
argument_list|(
name|ContainerMetricsConstants
operator|.
name|ALLOCATED_MEMORY_ENTITY_INFO
argument_list|,
name|resource
operator|.
name|getMemory
argument_list|()
argument_list|)
expr_stmt|;
name|entityInfo
operator|.
name|put
argument_list|(
name|ContainerMetricsConstants
operator|.
name|ALLOCATED_VCORE_ENTITY_INFO
argument_list|,
name|resource
operator|.
name|getVirtualCores
argument_list|()
argument_list|)
expr_stmt|;
name|entityInfo
operator|.
name|put
argument_list|(
name|ContainerMetricsConstants
operator|.
name|ALLOCATED_HOST_ENTITY_INFO
argument_list|,
name|nodeId
operator|.
name|getHost
argument_list|()
argument_list|)
expr_stmt|;
name|entityInfo
operator|.
name|put
argument_list|(
name|ContainerMetricsConstants
operator|.
name|ALLOCATED_PORT_ENTITY_INFO
argument_list|,
name|nodeId
operator|.
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
name|entityInfo
operator|.
name|put
argument_list|(
name|ContainerMetricsConstants
operator|.
name|ALLOCATED_PRIORITY_ENTITY_INFO
argument_list|,
name|priority
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|entityInfo
operator|.
name|put
argument_list|(
name|ContainerMetricsConstants
operator|.
name|ALLOCATED_HOST_HTTP_ADDRESS_ENTITY_INFO
argument_list|,
name|httpAddress
argument_list|)
expr_stmt|;
name|entity
operator|.
name|setInfo
argument_list|(
name|entityInfo
argument_list|)
expr_stmt|;
name|TimelineEvent
name|tEvent
init|=
operator|new
name|TimelineEvent
argument_list|()
decl_stmt|;
name|tEvent
operator|.
name|setId
argument_list|(
name|ContainerMetricsConstants
operator|.
name|CREATED_EVENT_TYPE
argument_list|)
expr_stmt|;
name|tEvent
operator|.
name|setTimestamp
argument_list|(
name|timestamp
argument_list|)
expr_stmt|;
name|entity
operator|.
name|addEvent
argument_list|(
name|tEvent
argument_list|)
expr_stmt|;
name|entity
operator|.
name|setCreatedTime
argument_list|(
name|timestamp
argument_list|)
expr_stmt|;
name|putEntity
argument_list|(
name|entity
argument_list|,
name|containerId
operator|.
name|getApplicationAttemptId
argument_list|()
operator|.
name|getApplicationId
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|publishContainerFinishedEvent (ContainerStatus containerStatus, long timeStamp)
specifier|private
name|void
name|publishContainerFinishedEvent
parameter_list|(
name|ContainerStatus
name|containerStatus
parameter_list|,
name|long
name|timeStamp
parameter_list|)
block|{
name|ContainerId
name|containerId
init|=
name|containerStatus
operator|.
name|getContainerId
argument_list|()
decl_stmt|;
name|TimelineEntity
name|entity
init|=
name|createContainerEntity
argument_list|(
name|containerId
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|eventInfo
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|eventInfo
operator|.
name|put
argument_list|(
name|ContainerMetricsConstants
operator|.
name|DIAGNOSTICS_INFO_EVENT_INFO
argument_list|,
name|containerStatus
operator|.
name|getDiagnostics
argument_list|()
argument_list|)
expr_stmt|;
name|eventInfo
operator|.
name|put
argument_list|(
name|ContainerMetricsConstants
operator|.
name|EXIT_STATUS_EVENT_INFO
argument_list|,
name|containerStatus
operator|.
name|getExitStatus
argument_list|()
argument_list|)
expr_stmt|;
name|eventInfo
operator|.
name|put
argument_list|(
name|ContainerMetricsConstants
operator|.
name|STATE_EVENT_INFO
argument_list|,
name|containerStatus
operator|.
name|getState
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|TimelineEvent
name|tEvent
init|=
operator|new
name|TimelineEvent
argument_list|()
decl_stmt|;
name|tEvent
operator|.
name|setId
argument_list|(
name|ContainerMetricsConstants
operator|.
name|FINISHED_EVENT_TYPE
argument_list|)
expr_stmt|;
name|tEvent
operator|.
name|setTimestamp
argument_list|(
name|timeStamp
argument_list|)
expr_stmt|;
name|tEvent
operator|.
name|setInfo
argument_list|(
name|eventInfo
argument_list|)
expr_stmt|;
name|entity
operator|.
name|addEvent
argument_list|(
name|tEvent
argument_list|)
expr_stmt|;
name|putEntity
argument_list|(
name|entity
argument_list|,
name|containerId
operator|.
name|getApplicationAttemptId
argument_list|()
operator|.
name|getApplicationId
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|createContainerEntity ( ContainerId containerId)
specifier|private
specifier|static
name|ContainerEntity
name|createContainerEntity
parameter_list|(
name|ContainerId
name|containerId
parameter_list|)
block|{
name|ContainerEntity
name|entity
init|=
operator|new
name|ContainerEntity
argument_list|()
decl_stmt|;
name|entity
operator|.
name|setId
argument_list|(
name|containerId
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Identifier
name|parentIdentifier
init|=
operator|new
name|Identifier
argument_list|()
decl_stmt|;
name|parentIdentifier
operator|.
name|setType
argument_list|(
name|TimelineEntityType
operator|.
name|YARN_APPLICATION_ATTEMPT
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|parentIdentifier
operator|.
name|setId
argument_list|(
name|containerId
operator|.
name|getApplicationAttemptId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|entity
operator|.
name|setParent
argument_list|(
name|parentIdentifier
argument_list|)
expr_stmt|;
return|return
name|entity
return|;
block|}
DECL|method|putEntity (TimelineEntity entity, ApplicationId appId)
specifier|private
name|void
name|putEntity
parameter_list|(
name|TimelineEntity
name|entity
parameter_list|,
name|ApplicationId
name|appId
parameter_list|)
block|{
try|try
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
literal|"Publishing the entity "
operator|+
name|entity
operator|+
literal|", JSON-style content: "
operator|+
name|TimelineUtils
operator|.
name|dumpTimelineRecordtoJSON
argument_list|(
name|entity
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|TimelineClient
name|timelineClient
init|=
name|context
operator|.
name|getApplications
argument_list|()
operator|.
name|get
argument_list|(
name|appId
argument_list|)
operator|.
name|getTimelineClient
argument_list|()
decl_stmt|;
name|timelineClient
operator|.
name|putEntities
argument_list|(
name|entity
argument_list|)
expr_stmt|;
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
literal|"Error when publishing entity "
operator|+
name|entity
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|publishApplicationEvent (ApplicationEvent event)
specifier|public
name|void
name|publishApplicationEvent
parameter_list|(
name|ApplicationEvent
name|event
parameter_list|)
block|{
comment|// publish only when the desired event is received
switch|switch
condition|(
name|event
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|INIT_APPLICATION
case|:
case|case
name|FINISH_APPLICATION
case|:
case|case
name|APPLICATION_CONTAINER_FINISHED
case|:
case|case
name|APPLICATION_LOG_HANDLING_FAILED
case|:
name|dispatcher
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
name|event
argument_list|)
expr_stmt|;
break|break;
default|default:
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
name|event
operator|.
name|getType
argument_list|()
operator|+
literal|" is not a desired ApplicationEvent which"
operator|+
literal|" needs to be published by NMTimelinePublisher"
argument_list|)
expr_stmt|;
block|}
break|break;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|publishContainerEvent (ContainerEvent event)
specifier|public
name|void
name|publishContainerEvent
parameter_list|(
name|ContainerEvent
name|event
parameter_list|)
block|{
comment|// publish only when the desired event is received
switch|switch
condition|(
name|event
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|INIT_CONTAINER
case|:
name|dispatcher
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
name|event
argument_list|)
expr_stmt|;
break|break;
default|default:
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
name|event
operator|.
name|getType
argument_list|()
operator|+
literal|" is not a desired ContainerEvent which needs to be published by"
operator|+
literal|" NMTimelinePublisher"
argument_list|)
expr_stmt|;
block|}
break|break;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|publishLocalizationEvent (LocalizationEvent event)
specifier|public
name|void
name|publishLocalizationEvent
parameter_list|(
name|LocalizationEvent
name|event
parameter_list|)
block|{
comment|// publish only when the desired event is received
switch|switch
condition|(
name|event
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|CONTAINER_RESOURCES_LOCALIZED
case|:
case|case
name|INIT_CONTAINER_RESOURCES
case|:
name|dispatcher
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
name|event
argument_list|)
expr_stmt|;
break|break;
default|default:
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
name|event
operator|.
name|getType
argument_list|()
operator|+
literal|" is not a desired LocalizationEvent which needs to be published"
operator|+
literal|" by NMTimelinePublisher"
argument_list|)
expr_stmt|;
block|}
break|break;
block|}
block|}
DECL|class|ApplicationEventHandler
specifier|private
class|class
name|ApplicationEventHandler
implements|implements
name|EventHandler
argument_list|<
name|ApplicationEvent
argument_list|>
block|{
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
switch|switch
condition|(
name|event
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|APPLICATION_CONTAINER_FINISHED
case|:
comment|// this is actually used to publish the container Event
name|ApplicationContainerFinishedEvent
name|evnt
init|=
operator|(
name|ApplicationContainerFinishedEvent
operator|)
name|event
decl_stmt|;
name|publishContainerFinishedEvent
argument_list|(
name|evnt
operator|.
name|getContainerStatus
argument_list|()
argument_list|,
name|event
operator|.
name|getTimestamp
argument_list|()
argument_list|)
expr_stmt|;
break|break;
default|default:
name|LOG
operator|.
name|error
argument_list|(
literal|"Seems like event type is captured only in "
operator|+
literal|"publishApplicationEvent method and not handled here"
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
DECL|class|ContainerEventHandler
specifier|private
class|class
name|ContainerEventHandler
implements|implements
name|EventHandler
argument_list|<
name|ContainerEvent
argument_list|>
block|{
annotation|@
name|Override
DECL|method|handle (ContainerEvent event)
specifier|public
name|void
name|handle
parameter_list|(
name|ContainerEvent
name|event
parameter_list|)
block|{
name|ContainerId
name|containerId
init|=
name|event
operator|.
name|getContainerID
argument_list|()
decl_stmt|;
name|Container
name|container
init|=
name|context
operator|.
name|getContainers
argument_list|()
operator|.
name|get
argument_list|(
name|containerId
argument_list|)
decl_stmt|;
name|long
name|timestamp
init|=
name|event
operator|.
name|getTimestamp
argument_list|()
decl_stmt|;
name|ContainerEntity
name|entity
init|=
name|createContainerEntity
argument_list|(
name|containerId
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|event
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|INIT_CONTAINER
case|:
name|publishContainerCreatedEvent
argument_list|(
name|entity
argument_list|,
name|containerId
argument_list|,
name|container
operator|.
name|getResource
argument_list|()
argument_list|,
name|container
operator|.
name|getPriority
argument_list|()
argument_list|,
name|timestamp
argument_list|)
expr_stmt|;
break|break;
default|default:
name|LOG
operator|.
name|error
argument_list|(
literal|"Seems like event type is captured only in "
operator|+
literal|"publishContainerEvent method and not handled here"
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
DECL|class|LocalizationEventDispatcher
specifier|private
specifier|static
specifier|final
class|class
name|LocalizationEventDispatcher
implements|implements
name|EventHandler
argument_list|<
name|LocalizationEvent
argument_list|>
block|{
annotation|@
name|Override
DECL|method|handle (LocalizationEvent event)
specifier|public
name|void
name|handle
parameter_list|(
name|LocalizationEvent
name|event
parameter_list|)
block|{
switch|switch
condition|(
name|event
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|INIT_CONTAINER_RESOURCES
case|:
case|case
name|CONTAINER_RESOURCES_LOCALIZED
case|:
comment|// TODO after priority based flush jira is finished
break|break;
default|default:
name|LOG
operator|.
name|error
argument_list|(
literal|"Seems like event type is captured only in "
operator|+
literal|"publishLocalizationEvent method and not handled here"
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
comment|/**    * EventHandler implementation which forward events to NMMetricsPublisher.    * Making use of it, NMMetricsPublisher can avoid to have a public handle    * method.    */
DECL|class|ForwardingEventHandler
specifier|private
specifier|final
class|class
name|ForwardingEventHandler
implements|implements
name|EventHandler
argument_list|<
name|NMTimelineEvent
argument_list|>
block|{
annotation|@
name|Override
DECL|method|handle (NMTimelineEvent event)
specifier|public
name|void
name|handle
parameter_list|(
name|NMTimelineEvent
name|event
parameter_list|)
block|{
name|handleNMTimelineEvent
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|TimelinePublishEvent
specifier|private
specifier|static
class|class
name|TimelinePublishEvent
extends|extends
name|NMTimelineEvent
block|{
DECL|field|appId
specifier|private
name|ApplicationId
name|appId
decl_stmt|;
DECL|field|entityToPublish
specifier|private
name|TimelineEntity
name|entityToPublish
decl_stmt|;
DECL|method|TimelinePublishEvent (TimelineEntity entity, ApplicationId appId)
specifier|public
name|TimelinePublishEvent
parameter_list|(
name|TimelineEntity
name|entity
parameter_list|,
name|ApplicationId
name|appId
parameter_list|)
block|{
name|super
argument_list|(
name|NMTimelineEventType
operator|.
name|TIMELINE_ENTITY_PUBLISH
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|appId
operator|=
name|appId
expr_stmt|;
name|this
operator|.
name|entityToPublish
operator|=
name|entity
expr_stmt|;
block|}
DECL|method|getApplicationId ()
specifier|public
name|ApplicationId
name|getApplicationId
parameter_list|()
block|{
return|return
name|appId
return|;
block|}
DECL|method|getTimelineEntityToPublish ()
specifier|public
name|TimelineEntity
name|getTimelineEntityToPublish
parameter_list|()
block|{
return|return
name|entityToPublish
return|;
block|}
block|}
block|}
end_class

end_unit

