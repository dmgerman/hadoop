begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.metrics
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
name|metrics
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
name|timeline
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
name|timeline
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
name|server
operator|.
name|metrics
operator|.
name|AppAttemptMetricsConstants
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
name|ApplicationMetricsConstants
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
name|resourcemanager
operator|.
name|rmapp
operator|.
name|RMAppMetrics
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

begin_class
DECL|class|TimelineServiceV1Publisher
specifier|public
class|class
name|TimelineServiceV1Publisher
extends|extends
name|AbstractTimelineServicePublisher
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
name|TimelineServiceV1Publisher
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|TimelineServiceV1Publisher ()
specifier|public
name|TimelineServiceV1Publisher
parameter_list|()
block|{
name|super
argument_list|(
literal|"TimelineserviceV1Publisher"
argument_list|)
expr_stmt|;
block|}
DECL|field|client
specifier|private
name|TimelineClient
name|client
decl_stmt|;
annotation|@
name|Override
DECL|method|serviceInit (Configuration conf)
specifier|public
name|void
name|serviceInit
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|Exception
block|{
name|client
operator|=
name|TimelineClient
operator|.
name|createTimelineClient
argument_list|()
expr_stmt|;
name|addIfService
argument_list|(
name|client
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
DECL|method|publishApplicationCreatedEvent (ApplicationCreatedEvent event)
name|void
name|publishApplicationCreatedEvent
parameter_list|(
name|ApplicationCreatedEvent
name|event
parameter_list|)
block|{
name|TimelineEntity
name|entity
init|=
name|createApplicationEntity
argument_list|(
name|event
operator|.
name|getApplicationId
argument_list|()
argument_list|)
decl_stmt|;
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
name|ApplicationMetricsConstants
operator|.
name|NAME_ENTITY_INFO
argument_list|,
name|event
operator|.
name|getApplicationName
argument_list|()
argument_list|)
expr_stmt|;
name|entityInfo
operator|.
name|put
argument_list|(
name|ApplicationMetricsConstants
operator|.
name|TYPE_ENTITY_INFO
argument_list|,
name|event
operator|.
name|getApplicationType
argument_list|()
argument_list|)
expr_stmt|;
name|entityInfo
operator|.
name|put
argument_list|(
name|ApplicationMetricsConstants
operator|.
name|USER_ENTITY_INFO
argument_list|,
name|event
operator|.
name|getUser
argument_list|()
argument_list|)
expr_stmt|;
name|entityInfo
operator|.
name|put
argument_list|(
name|ApplicationMetricsConstants
operator|.
name|QUEUE_ENTITY_INFO
argument_list|,
name|event
operator|.
name|getQueue
argument_list|()
argument_list|)
expr_stmt|;
name|entityInfo
operator|.
name|put
argument_list|(
name|ApplicationMetricsConstants
operator|.
name|SUBMITTED_TIME_ENTITY_INFO
argument_list|,
name|event
operator|.
name|getSubmittedTime
argument_list|()
argument_list|)
expr_stmt|;
name|entityInfo
operator|.
name|put
argument_list|(
name|ApplicationMetricsConstants
operator|.
name|APP_TAGS_INFO
argument_list|,
name|event
operator|.
name|getAppTags
argument_list|()
argument_list|)
expr_stmt|;
name|entityInfo
operator|.
name|put
argument_list|(
name|ApplicationMetricsConstants
operator|.
name|UNMANAGED_APPLICATION_ENTITY_INFO
argument_list|,
name|event
operator|.
name|isUnmanagedApp
argument_list|()
argument_list|)
expr_stmt|;
name|entityInfo
operator|.
name|put
argument_list|(
name|ApplicationMetricsConstants
operator|.
name|APPLICATION_PRIORITY_INFO
argument_list|,
name|event
operator|.
name|getApplicationPriority
argument_list|()
operator|.
name|getPriority
argument_list|()
argument_list|)
expr_stmt|;
name|entityInfo
operator|.
name|put
argument_list|(
name|ApplicationMetricsConstants
operator|.
name|APP_NODE_LABEL_EXPRESSION
argument_list|,
name|event
operator|.
name|getAppNodeLabelsExpression
argument_list|()
argument_list|)
expr_stmt|;
name|entityInfo
operator|.
name|put
argument_list|(
name|ApplicationMetricsConstants
operator|.
name|AM_NODE_LABEL_EXPRESSION
argument_list|,
name|event
operator|.
name|getAmNodeLabelsExpression
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|event
operator|.
name|getCallerContext
argument_list|()
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|event
operator|.
name|getCallerContext
argument_list|()
operator|.
name|getContext
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|entityInfo
operator|.
name|put
argument_list|(
name|ApplicationMetricsConstants
operator|.
name|YARN_APP_CALLER_CONTEXT
argument_list|,
name|event
operator|.
name|getCallerContext
argument_list|()
operator|.
name|getContext
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|event
operator|.
name|getCallerContext
argument_list|()
operator|.
name|getSignature
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|entityInfo
operator|.
name|put
argument_list|(
name|ApplicationMetricsConstants
operator|.
name|YARN_APP_CALLER_SIGNATURE
argument_list|,
name|event
operator|.
name|getCallerContext
argument_list|()
operator|.
name|getSignature
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|entity
operator|.
name|setOtherInfo
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
name|setEventType
argument_list|(
name|ApplicationMetricsConstants
operator|.
name|CREATED_EVENT_TYPE
argument_list|)
expr_stmt|;
name|tEvent
operator|.
name|setTimestamp
argument_list|(
name|event
operator|.
name|getTimestamp
argument_list|()
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
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|publishApplicationFinishedEvent (ApplicationFinishedEvent event)
name|void
name|publishApplicationFinishedEvent
parameter_list|(
name|ApplicationFinishedEvent
name|event
parameter_list|)
block|{
name|TimelineEntity
name|entity
init|=
name|createApplicationEntity
argument_list|(
name|event
operator|.
name|getApplicationId
argument_list|()
argument_list|)
decl_stmt|;
name|TimelineEvent
name|tEvent
init|=
operator|new
name|TimelineEvent
argument_list|()
decl_stmt|;
name|tEvent
operator|.
name|setEventType
argument_list|(
name|ApplicationMetricsConstants
operator|.
name|FINISHED_EVENT_TYPE
argument_list|)
expr_stmt|;
name|tEvent
operator|.
name|setTimestamp
argument_list|(
name|event
operator|.
name|getTimestamp
argument_list|()
argument_list|)
expr_stmt|;
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
name|ApplicationMetricsConstants
operator|.
name|DIAGNOSTICS_INFO_EVENT_INFO
argument_list|,
name|event
operator|.
name|getDiagnosticsInfo
argument_list|()
argument_list|)
expr_stmt|;
name|eventInfo
operator|.
name|put
argument_list|(
name|ApplicationMetricsConstants
operator|.
name|FINAL_STATUS_EVENT_INFO
argument_list|,
name|event
operator|.
name|getFinalApplicationStatus
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|eventInfo
operator|.
name|put
argument_list|(
name|ApplicationMetricsConstants
operator|.
name|STATE_EVENT_INFO
argument_list|,
name|event
operator|.
name|getYarnApplicationState
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|event
operator|.
name|getLatestApplicationAttemptId
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|eventInfo
operator|.
name|put
argument_list|(
name|ApplicationMetricsConstants
operator|.
name|LATEST_APP_ATTEMPT_EVENT_INFO
argument_list|,
name|event
operator|.
name|getLatestApplicationAttemptId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|RMAppMetrics
name|appMetrics
init|=
name|event
operator|.
name|getAppMetrics
argument_list|()
decl_stmt|;
name|entity
operator|.
name|addOtherInfo
argument_list|(
name|ApplicationMetricsConstants
operator|.
name|APP_CPU_METRICS
argument_list|,
name|appMetrics
operator|.
name|getVcoreSeconds
argument_list|()
argument_list|)
expr_stmt|;
name|entity
operator|.
name|addOtherInfo
argument_list|(
name|ApplicationMetricsConstants
operator|.
name|APP_MEM_METRICS
argument_list|,
name|appMetrics
operator|.
name|getMemorySeconds
argument_list|()
argument_list|)
expr_stmt|;
name|tEvent
operator|.
name|setEventInfo
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
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|publishApplicationUpdatedEvent (ApplicationUpdatedEvent event)
name|void
name|publishApplicationUpdatedEvent
parameter_list|(
name|ApplicationUpdatedEvent
name|event
parameter_list|)
block|{
name|TimelineEntity
name|entity
init|=
name|createApplicationEntity
argument_list|(
name|event
operator|.
name|getApplicationId
argument_list|()
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
name|ApplicationMetricsConstants
operator|.
name|QUEUE_ENTITY_INFO
argument_list|,
name|event
operator|.
name|getQueue
argument_list|()
argument_list|)
expr_stmt|;
name|eventInfo
operator|.
name|put
argument_list|(
name|ApplicationMetricsConstants
operator|.
name|APPLICATION_PRIORITY_INFO
argument_list|,
name|event
operator|.
name|getApplicationPriority
argument_list|()
operator|.
name|getPriority
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
name|setEventType
argument_list|(
name|ApplicationMetricsConstants
operator|.
name|UPDATED_EVENT_TYPE
argument_list|)
expr_stmt|;
name|tEvent
operator|.
name|setTimestamp
argument_list|(
name|event
operator|.
name|getTimestamp
argument_list|()
argument_list|)
expr_stmt|;
name|tEvent
operator|.
name|setEventInfo
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
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|publishApplicationStateUpdatedEvent ( ApplicaitonStateUpdatedEvent event)
name|void
name|publishApplicationStateUpdatedEvent
parameter_list|(
name|ApplicaitonStateUpdatedEvent
name|event
parameter_list|)
block|{
name|TimelineEntity
name|entity
init|=
name|createApplicationEntity
argument_list|(
name|event
operator|.
name|getApplicationId
argument_list|()
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
name|ApplicationMetricsConstants
operator|.
name|STATE_EVENT_INFO
argument_list|,
name|event
operator|.
name|getAppState
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
name|setEventType
argument_list|(
name|ApplicationMetricsConstants
operator|.
name|STATE_UPDATED_EVENT_TYPE
argument_list|)
expr_stmt|;
name|tEvent
operator|.
name|setTimestamp
argument_list|(
name|event
operator|.
name|getTimestamp
argument_list|()
argument_list|)
expr_stmt|;
name|tEvent
operator|.
name|setEventInfo
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
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|publishApplicationACLsUpdatedEvent (ApplicationACLsUpdatedEvent event)
name|void
name|publishApplicationACLsUpdatedEvent
parameter_list|(
name|ApplicationACLsUpdatedEvent
name|event
parameter_list|)
block|{
name|TimelineEntity
name|entity
init|=
name|createApplicationEntity
argument_list|(
name|event
operator|.
name|getApplicationId
argument_list|()
argument_list|)
decl_stmt|;
name|TimelineEvent
name|tEvent
init|=
operator|new
name|TimelineEvent
argument_list|()
decl_stmt|;
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
name|ApplicationMetricsConstants
operator|.
name|APP_VIEW_ACLS_ENTITY_INFO
argument_list|,
name|event
operator|.
name|getViewAppACLs
argument_list|()
argument_list|)
expr_stmt|;
name|entity
operator|.
name|setOtherInfo
argument_list|(
name|entityInfo
argument_list|)
expr_stmt|;
name|tEvent
operator|.
name|setEventType
argument_list|(
name|ApplicationMetricsConstants
operator|.
name|ACLS_UPDATED_EVENT_TYPE
argument_list|)
expr_stmt|;
name|tEvent
operator|.
name|setTimestamp
argument_list|(
name|event
operator|.
name|getTimestamp
argument_list|()
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
argument_list|)
expr_stmt|;
block|}
DECL|method|createApplicationEntity ( ApplicationId applicationId)
specifier|private
specifier|static
name|TimelineEntity
name|createApplicationEntity
parameter_list|(
name|ApplicationId
name|applicationId
parameter_list|)
block|{
name|TimelineEntity
name|entity
init|=
operator|new
name|TimelineEntity
argument_list|()
decl_stmt|;
name|entity
operator|.
name|setEntityType
argument_list|(
name|ApplicationMetricsConstants
operator|.
name|ENTITY_TYPE
argument_list|)
expr_stmt|;
name|entity
operator|.
name|setEntityId
argument_list|(
name|applicationId
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|entity
return|;
block|}
annotation|@
name|Override
DECL|method|publishAppAttemptRegisteredEvent (AppAttemptRegisteredEvent event)
name|void
name|publishAppAttemptRegisteredEvent
parameter_list|(
name|AppAttemptRegisteredEvent
name|event
parameter_list|)
block|{
name|TimelineEntity
name|entity
init|=
name|createAppAttemptEntity
argument_list|(
name|event
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|)
decl_stmt|;
name|TimelineEvent
name|tEvent
init|=
operator|new
name|TimelineEvent
argument_list|()
decl_stmt|;
name|tEvent
operator|.
name|setEventType
argument_list|(
name|AppAttemptMetricsConstants
operator|.
name|REGISTERED_EVENT_TYPE
argument_list|)
expr_stmt|;
name|tEvent
operator|.
name|setTimestamp
argument_list|(
name|event
operator|.
name|getTimestamp
argument_list|()
argument_list|)
expr_stmt|;
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
name|AppAttemptMetricsConstants
operator|.
name|TRACKING_URL_EVENT_INFO
argument_list|,
name|event
operator|.
name|getTrackingUrl
argument_list|()
argument_list|)
expr_stmt|;
name|eventInfo
operator|.
name|put
argument_list|(
name|AppAttemptMetricsConstants
operator|.
name|ORIGINAL_TRACKING_URL_EVENT_INFO
argument_list|,
name|event
operator|.
name|getOriginalTrackingURL
argument_list|()
argument_list|)
expr_stmt|;
name|eventInfo
operator|.
name|put
argument_list|(
name|AppAttemptMetricsConstants
operator|.
name|HOST_EVENT_INFO
argument_list|,
name|event
operator|.
name|getHost
argument_list|()
argument_list|)
expr_stmt|;
name|eventInfo
operator|.
name|put
argument_list|(
name|AppAttemptMetricsConstants
operator|.
name|RPC_PORT_EVENT_INFO
argument_list|,
name|event
operator|.
name|getRpcPort
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|event
operator|.
name|getMasterContainerId
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|eventInfo
operator|.
name|put
argument_list|(
name|AppAttemptMetricsConstants
operator|.
name|MASTER_CONTAINER_EVENT_INFO
argument_list|,
name|event
operator|.
name|getMasterContainerId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|tEvent
operator|.
name|setEventInfo
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
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|publishAppAttemptFinishedEvent (AppAttemptFinishedEvent event)
name|void
name|publishAppAttemptFinishedEvent
parameter_list|(
name|AppAttemptFinishedEvent
name|event
parameter_list|)
block|{
name|TimelineEntity
name|entity
init|=
name|createAppAttemptEntity
argument_list|(
name|event
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|)
decl_stmt|;
name|TimelineEvent
name|tEvent
init|=
operator|new
name|TimelineEvent
argument_list|()
decl_stmt|;
name|tEvent
operator|.
name|setEventType
argument_list|(
name|AppAttemptMetricsConstants
operator|.
name|FINISHED_EVENT_TYPE
argument_list|)
expr_stmt|;
name|tEvent
operator|.
name|setTimestamp
argument_list|(
name|event
operator|.
name|getTimestamp
argument_list|()
argument_list|)
expr_stmt|;
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
name|AppAttemptMetricsConstants
operator|.
name|TRACKING_URL_EVENT_INFO
argument_list|,
name|event
operator|.
name|getTrackingUrl
argument_list|()
argument_list|)
expr_stmt|;
name|eventInfo
operator|.
name|put
argument_list|(
name|AppAttemptMetricsConstants
operator|.
name|ORIGINAL_TRACKING_URL_EVENT_INFO
argument_list|,
name|event
operator|.
name|getOriginalTrackingURL
argument_list|()
argument_list|)
expr_stmt|;
name|eventInfo
operator|.
name|put
argument_list|(
name|AppAttemptMetricsConstants
operator|.
name|DIAGNOSTICS_INFO_EVENT_INFO
argument_list|,
name|event
operator|.
name|getDiagnosticsInfo
argument_list|()
argument_list|)
expr_stmt|;
name|eventInfo
operator|.
name|put
argument_list|(
name|AppAttemptMetricsConstants
operator|.
name|FINAL_STATUS_EVENT_INFO
argument_list|,
name|event
operator|.
name|getFinalApplicationStatus
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|eventInfo
operator|.
name|put
argument_list|(
name|AppAttemptMetricsConstants
operator|.
name|STATE_EVENT_INFO
argument_list|,
name|event
operator|.
name|getYarnApplicationAttemptState
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|event
operator|.
name|getMasterContainerId
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|eventInfo
operator|.
name|put
argument_list|(
name|AppAttemptMetricsConstants
operator|.
name|MASTER_CONTAINER_EVENT_INFO
argument_list|,
name|event
operator|.
name|getMasterContainerId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|tEvent
operator|.
name|setEventInfo
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
argument_list|)
expr_stmt|;
block|}
DECL|method|createAppAttemptEntity ( ApplicationAttemptId appAttemptId)
specifier|private
specifier|static
name|TimelineEntity
name|createAppAttemptEntity
parameter_list|(
name|ApplicationAttemptId
name|appAttemptId
parameter_list|)
block|{
name|TimelineEntity
name|entity
init|=
operator|new
name|TimelineEntity
argument_list|()
decl_stmt|;
name|entity
operator|.
name|setEntityType
argument_list|(
name|AppAttemptMetricsConstants
operator|.
name|ENTITY_TYPE
argument_list|)
expr_stmt|;
name|entity
operator|.
name|setEntityId
argument_list|(
name|appAttemptId
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|entity
operator|.
name|addPrimaryFilter
argument_list|(
name|AppAttemptMetricsConstants
operator|.
name|PARENT_PRIMARY_FILTER
argument_list|,
name|appAttemptId
operator|.
name|getApplicationId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|entity
return|;
block|}
annotation|@
name|Override
DECL|method|publishContainerCreatedEvent (ContainerCreatedEvent event)
name|void
name|publishContainerCreatedEvent
parameter_list|(
name|ContainerCreatedEvent
name|event
parameter_list|)
block|{
name|TimelineEntity
name|entity
init|=
name|createContainerEntity
argument_list|(
name|event
operator|.
name|getContainerId
argument_list|()
argument_list|)
decl_stmt|;
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
name|event
operator|.
name|getAllocatedResource
argument_list|()
operator|.
name|getMemorySize
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
name|event
operator|.
name|getAllocatedResource
argument_list|()
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
name|event
operator|.
name|getAllocatedNode
argument_list|()
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
name|event
operator|.
name|getAllocatedNode
argument_list|()
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
name|event
operator|.
name|getAllocatedPriority
argument_list|()
operator|.
name|getPriority
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
name|event
operator|.
name|getNodeHttpAddress
argument_list|()
argument_list|)
expr_stmt|;
name|entity
operator|.
name|setOtherInfo
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
name|setEventType
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
name|event
operator|.
name|getTimestamp
argument_list|()
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
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|publishContainerFinishedEvent (ContainerFinishedEvent event)
name|void
name|publishContainerFinishedEvent
parameter_list|(
name|ContainerFinishedEvent
name|event
parameter_list|)
block|{
name|TimelineEntity
name|entity
init|=
name|createContainerEntity
argument_list|(
name|event
operator|.
name|getContainerId
argument_list|()
argument_list|)
decl_stmt|;
name|TimelineEvent
name|tEvent
init|=
operator|new
name|TimelineEvent
argument_list|()
decl_stmt|;
name|tEvent
operator|.
name|setEventType
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
name|event
operator|.
name|getTimestamp
argument_list|()
argument_list|)
expr_stmt|;
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
name|event
operator|.
name|getDiagnosticsInfo
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
name|event
operator|.
name|getContainerExitStatus
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
name|event
operator|.
name|getContainerState
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
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
name|ALLOCATED_HOST_ENTITY_INFO
argument_list|,
name|event
operator|.
name|getAllocatedNode
argument_list|()
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
name|event
operator|.
name|getAllocatedNode
argument_list|()
operator|.
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
name|entity
operator|.
name|setOtherInfo
argument_list|(
name|entityInfo
argument_list|)
expr_stmt|;
name|tEvent
operator|.
name|setEventInfo
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
argument_list|)
expr_stmt|;
block|}
DECL|method|createContainerEntity (ContainerId containerId)
specifier|private
specifier|static
name|TimelineEntity
name|createContainerEntity
parameter_list|(
name|ContainerId
name|containerId
parameter_list|)
block|{
name|TimelineEntity
name|entity
init|=
operator|new
name|TimelineEntity
argument_list|()
decl_stmt|;
name|entity
operator|.
name|setEntityType
argument_list|(
name|ContainerMetricsConstants
operator|.
name|ENTITY_TYPE
argument_list|)
expr_stmt|;
name|entity
operator|.
name|setEntityId
argument_list|(
name|containerId
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|entity
operator|.
name|addPrimaryFilter
argument_list|(
name|ContainerMetricsConstants
operator|.
name|PARENT_PRIMARIY_FILTER
argument_list|,
name|containerId
operator|.
name|getApplicationAttemptId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|entity
return|;
block|}
DECL|method|putEntity (TimelineEntity entity)
specifier|private
name|void
name|putEntity
parameter_list|(
name|TimelineEntity
name|entity
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
operator|.
name|getEntityId
argument_list|()
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
name|client
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
literal|"Error when publishing entity ["
operator|+
name|entity
operator|.
name|getEntityType
argument_list|()
operator|+
literal|","
operator|+
name|entity
operator|.
name|getEntityId
argument_list|()
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

