begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timelineservice.documentstore
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
name|timelineservice
operator|.
name|documentstore
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
name|security
operator|.
name|UserGroupInformation
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
name|AbstractService
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
name|*
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
name|timelineservice
operator|.
name|collector
operator|.
name|TimelineCollectorContext
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
name|timelineservice
operator|.
name|documentstore
operator|.
name|lib
operator|.
name|DocumentStoreVendor
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
name|timelineservice
operator|.
name|storage
operator|.
name|TimelineAggregationTrack
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
name|timelineservice
operator|.
name|storage
operator|.
name|TimelineWriter
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
name|timelineservice
operator|.
name|documentstore
operator|.
name|collection
operator|.
name|CollectionType
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
name|timelineservice
operator|.
name|documentstore
operator|.
name|collection
operator|.
name|document
operator|.
name|entity
operator|.
name|TimelineEntityDocument
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
name|timelineservice
operator|.
name|documentstore
operator|.
name|collection
operator|.
name|document
operator|.
name|flowactivity
operator|.
name|FlowActivityDocument
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
name|timelineservice
operator|.
name|documentstore
operator|.
name|collection
operator|.
name|document
operator|.
name|flowrun
operator|.
name|FlowRunDocument
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
name|timelineservice
operator|.
name|documentstore
operator|.
name|writer
operator|.
name|TimelineCollectionWriter
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
name|Set
import|;
end_import

begin_comment
comment|/**  * This is a generic document store timeline writer for storing the timeline  * entity information. Based on the {@link DocumentStoreVendor} that is  * configured, the documents are written to that backend.  */
end_comment

begin_class
DECL|class|DocumentStoreTimelineWriterImpl
specifier|public
class|class
name|DocumentStoreTimelineWriterImpl
extends|extends
name|AbstractService
implements|implements
name|TimelineWriter
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
name|DocumentStoreTimelineWriterImpl
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|DOC_ID_DELIMITER
specifier|private
specifier|static
specifier|final
name|String
name|DOC_ID_DELIMITER
init|=
literal|"!"
decl_stmt|;
DECL|field|storeType
specifier|private
name|DocumentStoreVendor
name|storeType
decl_stmt|;
DECL|field|appCollWriter
specifier|private
name|TimelineCollectionWriter
argument_list|<
name|TimelineEntityDocument
argument_list|>
name|appCollWriter
decl_stmt|;
specifier|private
name|TimelineCollectionWriter
argument_list|<
name|TimelineEntityDocument
argument_list|>
DECL|field|entityCollWriter
name|entityCollWriter
decl_stmt|;
DECL|field|flowActivityCollWriter
specifier|private
name|TimelineCollectionWriter
argument_list|<
name|FlowActivityDocument
argument_list|>
name|flowActivityCollWriter
decl_stmt|;
DECL|field|flowRunCollWriter
specifier|private
name|TimelineCollectionWriter
argument_list|<
name|FlowRunDocument
argument_list|>
name|flowRunCollWriter
decl_stmt|;
DECL|method|DocumentStoreTimelineWriterImpl ()
specifier|public
name|DocumentStoreTimelineWriterImpl
parameter_list|()
block|{
name|super
argument_list|(
name|DocumentStoreTimelineWriterImpl
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
name|storeType
operator|=
name|DocumentStoreUtils
operator|.
name|getStoreVendor
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Initializing Document Store Writer for : "
operator|+
name|storeType
argument_list|)
expr_stmt|;
name|super
operator|.
name|serviceInit
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|this
operator|.
name|appCollWriter
operator|=
operator|new
name|TimelineCollectionWriter
argument_list|<>
argument_list|(
name|CollectionType
operator|.
name|APPLICATION
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|this
operator|.
name|entityCollWriter
operator|=
operator|new
name|TimelineCollectionWriter
argument_list|<>
argument_list|(
name|CollectionType
operator|.
name|ENTITY
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|this
operator|.
name|flowActivityCollWriter
operator|=
operator|new
name|TimelineCollectionWriter
argument_list|<>
argument_list|(
name|CollectionType
operator|.
name|FLOW_ACTIVITY
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|this
operator|.
name|flowRunCollWriter
operator|=
operator|new
name|TimelineCollectionWriter
argument_list|<>
argument_list|(
name|CollectionType
operator|.
name|FLOW_RUN
argument_list|,
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
block|}
annotation|@
name|Override
DECL|method|serviceStop ()
specifier|protected
name|void
name|serviceStop
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|serviceStop
argument_list|()
expr_stmt|;
name|appCollWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|entityCollWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|flowActivityCollWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|flowRunCollWriter
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|write (TimelineCollectorContext context, TimelineEntities data, UserGroupInformation callerUgi)
specifier|public
name|TimelineWriteResponse
name|write
parameter_list|(
name|TimelineCollectorContext
name|context
parameter_list|,
name|TimelineEntities
name|data
parameter_list|,
name|UserGroupInformation
name|callerUgi
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Writing Timeline Entity for appID : {}"
argument_list|,
name|context
operator|.
name|getAppId
argument_list|()
argument_list|)
expr_stmt|;
name|TimelineWriteResponse
name|putStatus
init|=
operator|new
name|TimelineWriteResponse
argument_list|()
decl_stmt|;
name|String
name|subApplicationUser
init|=
name|callerUgi
operator|.
name|getShortUserName
argument_list|()
decl_stmt|;
comment|//Avoiding NPE for document id
if|if
condition|(
name|DocumentStoreUtils
operator|.
name|isNullOrEmpty
argument_list|(
name|context
operator|.
name|getFlowName
argument_list|()
argument_list|,
name|context
operator|.
name|getAppId
argument_list|()
argument_list|,
name|context
operator|.
name|getClusterId
argument_list|()
argument_list|,
name|context
operator|.
name|getUserId
argument_list|()
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Found NULL for one of: flowName={} appId={} "
operator|+
literal|"userId={} clusterId={} . Not proceeding on writing to store : "
operator|+
name|storeType
argument_list|)
expr_stmt|;
return|return
name|putStatus
return|;
block|}
for|for
control|(
name|TimelineEntity
name|timelineEntity
range|:
name|data
operator|.
name|getEntities
argument_list|()
control|)
block|{
comment|// a set can have at most 1 null
if|if
condition|(
name|timelineEntity
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
name|TimelineEntityDocument
name|entityDocument
decl_stmt|;
comment|//If the entity is application, it will be stored in Application
comment|// Collection
if|if
condition|(
name|ApplicationEntity
operator|.
name|isApplicationEntity
argument_list|(
name|timelineEntity
argument_list|)
condition|)
block|{
name|entityDocument
operator|=
name|createTimelineEntityDoc
argument_list|(
name|context
argument_list|,
name|subApplicationUser
argument_list|,
name|timelineEntity
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// if it's an application entity, store metrics for aggregation
name|FlowRunDocument
name|flowRunDoc
init|=
name|createFlowRunDoc
argument_list|(
name|context
argument_list|,
name|timelineEntity
operator|.
name|getMetrics
argument_list|()
argument_list|)
decl_stmt|;
comment|// fetch flow activity if App is created or finished
name|FlowActivityDocument
name|flowActivityDoc
init|=
name|getFlowActivityDoc
argument_list|(
name|context
argument_list|,
name|timelineEntity
argument_list|,
name|flowRunDoc
argument_list|,
name|entityDocument
argument_list|)
decl_stmt|;
name|writeApplicationDoc
argument_list|(
name|entityDocument
argument_list|)
expr_stmt|;
name|writeFlowRunDoc
argument_list|(
name|flowRunDoc
argument_list|)
expr_stmt|;
if|if
condition|(
name|flowActivityDoc
operator|!=
literal|null
condition|)
block|{
name|storeFlowActivityDoc
argument_list|(
name|flowActivityDoc
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|entityDocument
operator|=
name|createTimelineEntityDoc
argument_list|(
name|context
argument_list|,
name|subApplicationUser
argument_list|,
name|timelineEntity
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|appendSubAppUserIfExists
argument_list|(
name|context
argument_list|,
name|subApplicationUser
argument_list|)
expr_stmt|;
comment|// The entity will be stored in Entity Collection
name|entityDocument
operator|.
name|setCreatedTime
argument_list|(
name|fetchEntityCreationTime
argument_list|(
name|timelineEntity
argument_list|)
argument_list|)
expr_stmt|;
name|writeEntityDoc
argument_list|(
name|entityDocument
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|putStatus
return|;
block|}
annotation|@
name|Override
DECL|method|write (TimelineCollectorContext context, TimelineDomain domain)
specifier|public
name|TimelineWriteResponse
name|write
parameter_list|(
name|TimelineCollectorContext
name|context
parameter_list|,
name|TimelineDomain
name|domain
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
DECL|method|appendSubAppUserIfExists (TimelineCollectorContext context, String subApplicationUser)
specifier|private
name|void
name|appendSubAppUserIfExists
parameter_list|(
name|TimelineCollectorContext
name|context
parameter_list|,
name|String
name|subApplicationUser
parameter_list|)
block|{
name|String
name|userId
init|=
name|context
operator|.
name|getUserId
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|userId
operator|.
name|equals
argument_list|(
name|subApplicationUser
argument_list|)
operator|&&
operator|!
name|userId
operator|.
name|contains
argument_list|(
name|subApplicationUser
argument_list|)
condition|)
block|{
name|userId
operator|=
name|userId
operator|.
name|concat
argument_list|(
name|DOC_ID_DELIMITER
argument_list|)
operator|.
name|concat
argument_list|(
name|subApplicationUser
argument_list|)
expr_stmt|;
name|context
operator|.
name|setUserId
argument_list|(
name|userId
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|createTimelineEntityDoc ( TimelineCollectorContext context, String subApplicationUser, TimelineEntity timelineEntity, boolean isAppEntity)
specifier|private
name|TimelineEntityDocument
name|createTimelineEntityDoc
parameter_list|(
name|TimelineCollectorContext
name|context
parameter_list|,
name|String
name|subApplicationUser
parameter_list|,
name|TimelineEntity
name|timelineEntity
parameter_list|,
name|boolean
name|isAppEntity
parameter_list|)
block|{
name|TimelineEntityDocument
name|entityDocument
init|=
operator|new
name|TimelineEntityDocument
argument_list|(
name|timelineEntity
argument_list|)
decl_stmt|;
name|entityDocument
operator|.
name|setContext
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|entityDocument
operator|.
name|setFlowVersion
argument_list|(
name|context
operator|.
name|getFlowVersion
argument_list|()
argument_list|)
expr_stmt|;
name|entityDocument
operator|.
name|setSubApplicationUser
argument_list|(
name|subApplicationUser
argument_list|)
expr_stmt|;
if|if
condition|(
name|isAppEntity
condition|)
block|{
name|entityDocument
operator|.
name|setId
argument_list|(
name|DocumentStoreUtils
operator|.
name|constructTimelineEntityDocId
argument_list|(
name|context
argument_list|,
name|timelineEntity
operator|.
name|getType
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|entityDocument
operator|.
name|setId
argument_list|(
name|DocumentStoreUtils
operator|.
name|constructTimelineEntityDocId
argument_list|(
name|context
argument_list|,
name|timelineEntity
operator|.
name|getType
argument_list|()
argument_list|,
name|timelineEntity
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|entityDocument
return|;
block|}
DECL|method|createFlowRunDoc (TimelineCollectorContext context, Set<TimelineMetric> metrics)
specifier|private
name|FlowRunDocument
name|createFlowRunDoc
parameter_list|(
name|TimelineCollectorContext
name|context
parameter_list|,
name|Set
argument_list|<
name|TimelineMetric
argument_list|>
name|metrics
parameter_list|)
block|{
name|FlowRunDocument
name|flowRunDoc
init|=
operator|new
name|FlowRunDocument
argument_list|(
name|context
argument_list|,
name|metrics
argument_list|)
decl_stmt|;
name|flowRunDoc
operator|.
name|setFlowVersion
argument_list|(
name|context
operator|.
name|getFlowVersion
argument_list|()
argument_list|)
expr_stmt|;
name|flowRunDoc
operator|.
name|setId
argument_list|(
name|DocumentStoreUtils
operator|.
name|constructFlowRunDocId
argument_list|(
name|context
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|flowRunDoc
return|;
block|}
DECL|method|fetchEntityCreationTime (TimelineEntity timelineEntity)
specifier|private
name|long
name|fetchEntityCreationTime
parameter_list|(
name|TimelineEntity
name|timelineEntity
parameter_list|)
block|{
name|TimelineEvent
name|event
decl_stmt|;
switch|switch
condition|(
name|TimelineEntityType
operator|.
name|valueOf
argument_list|(
name|timelineEntity
operator|.
name|getType
argument_list|()
argument_list|)
condition|)
block|{
case|case
name|YARN_CONTAINER
case|:
name|event
operator|=
name|DocumentStoreUtils
operator|.
name|fetchEvent
argument_list|(
name|timelineEntity
argument_list|,
name|ContainerMetricsConstants
operator|.
name|CREATED_EVENT_TYPE
argument_list|)
expr_stmt|;
if|if
condition|(
name|event
operator|!=
literal|null
condition|)
block|{
return|return
name|event
operator|.
name|getTimestamp
argument_list|()
return|;
block|}
break|break;
case|case
name|YARN_APPLICATION_ATTEMPT
case|:
name|event
operator|=
name|DocumentStoreUtils
operator|.
name|fetchEvent
argument_list|(
name|timelineEntity
argument_list|,
name|AppAttemptMetricsConstants
operator|.
name|REGISTERED_EVENT_TYPE
argument_list|)
expr_stmt|;
if|if
condition|(
name|event
operator|!=
literal|null
condition|)
block|{
return|return
name|event
operator|.
name|getTimestamp
argument_list|()
return|;
block|}
break|break;
default|default:
comment|//NO Op
block|}
if|if
condition|(
name|timelineEntity
operator|.
name|getCreatedTime
argument_list|()
operator|==
literal|null
condition|)
block|{
return|return
literal|0
return|;
block|}
return|return
name|timelineEntity
operator|.
name|getCreatedTime
argument_list|()
return|;
block|}
DECL|method|getFlowActivityDoc ( TimelineCollectorContext context, TimelineEntity timelineEntity, FlowRunDocument flowRunDoc, TimelineEntityDocument entityDocument)
specifier|private
name|FlowActivityDocument
name|getFlowActivityDoc
parameter_list|(
name|TimelineCollectorContext
name|context
parameter_list|,
name|TimelineEntity
name|timelineEntity
parameter_list|,
name|FlowRunDocument
name|flowRunDoc
parameter_list|,
name|TimelineEntityDocument
name|entityDocument
parameter_list|)
block|{
name|FlowActivityDocument
name|flowActivityDoc
init|=
literal|null
decl_stmt|;
comment|// check if the application is created
name|TimelineEvent
name|event
init|=
name|DocumentStoreUtils
operator|.
name|fetchEvent
argument_list|(
name|timelineEntity
argument_list|,
name|ApplicationMetricsConstants
operator|.
name|CREATED_EVENT_TYPE
argument_list|)
decl_stmt|;
if|if
condition|(
name|event
operator|!=
literal|null
condition|)
block|{
name|entityDocument
operator|.
name|setCreatedTime
argument_list|(
name|event
operator|.
name|getTimestamp
argument_list|()
argument_list|)
expr_stmt|;
name|flowRunDoc
operator|.
name|setMinStartTime
argument_list|(
name|event
operator|.
name|getTimestamp
argument_list|()
argument_list|)
expr_stmt|;
name|flowActivityDoc
operator|=
name|createFlowActivityDoc
argument_list|(
name|context
argument_list|,
name|context
operator|.
name|getFlowName
argument_list|()
argument_list|,
name|context
operator|.
name|getFlowVersion
argument_list|()
argument_list|,
name|context
operator|.
name|getFlowRunId
argument_list|()
argument_list|,
name|event
argument_list|)
expr_stmt|;
block|}
comment|// if application has finished, store it's finish time
name|event
operator|=
name|DocumentStoreUtils
operator|.
name|fetchEvent
argument_list|(
name|timelineEntity
argument_list|,
name|ApplicationMetricsConstants
operator|.
name|FINISHED_EVENT_TYPE
argument_list|)
expr_stmt|;
if|if
condition|(
name|event
operator|!=
literal|null
condition|)
block|{
name|flowRunDoc
operator|.
name|setMaxEndTime
argument_list|(
name|event
operator|.
name|getTimestamp
argument_list|()
argument_list|)
expr_stmt|;
comment|// this check is to handle in case both create and finish event exist
comment|// under the single list of events for an TimelineEntity
if|if
condition|(
name|flowActivityDoc
operator|==
literal|null
condition|)
block|{
name|flowActivityDoc
operator|=
name|createFlowActivityDoc
argument_list|(
name|context
argument_list|,
name|context
operator|.
name|getFlowName
argument_list|()
argument_list|,
name|context
operator|.
name|getFlowVersion
argument_list|()
argument_list|,
name|context
operator|.
name|getFlowRunId
argument_list|()
argument_list|,
name|event
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|flowActivityDoc
return|;
block|}
DECL|method|createFlowActivityDoc ( TimelineCollectorContext context, String flowName, String flowVersion, long flowRunId, TimelineEvent event)
specifier|private
name|FlowActivityDocument
name|createFlowActivityDoc
parameter_list|(
name|TimelineCollectorContext
name|context
parameter_list|,
name|String
name|flowName
parameter_list|,
name|String
name|flowVersion
parameter_list|,
name|long
name|flowRunId
parameter_list|,
name|TimelineEvent
name|event
parameter_list|)
block|{
name|FlowActivityDocument
name|flowActivityDoc
init|=
operator|new
name|FlowActivityDocument
argument_list|(
name|flowName
argument_list|,
name|flowVersion
argument_list|,
name|flowRunId
argument_list|)
decl_stmt|;
name|flowActivityDoc
operator|.
name|setDayTimestamp
argument_list|(
name|DocumentStoreUtils
operator|.
name|getTopOfTheDayTimestamp
argument_list|(
name|event
operator|.
name|getTimestamp
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|flowActivityDoc
operator|.
name|setFlowName
argument_list|(
name|flowName
argument_list|)
expr_stmt|;
name|flowActivityDoc
operator|.
name|setUser
argument_list|(
name|context
operator|.
name|getUserId
argument_list|()
argument_list|)
expr_stmt|;
name|flowActivityDoc
operator|.
name|setId
argument_list|(
name|DocumentStoreUtils
operator|.
name|constructFlowActivityDocId
argument_list|(
name|context
argument_list|,
name|event
operator|.
name|getTimestamp
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|flowActivityDoc
return|;
block|}
DECL|method|writeFlowRunDoc (FlowRunDocument flowRunDoc)
specifier|private
name|void
name|writeFlowRunDoc
parameter_list|(
name|FlowRunDocument
name|flowRunDoc
parameter_list|)
block|{
name|flowRunCollWriter
operator|.
name|writeDocument
argument_list|(
name|flowRunDoc
argument_list|)
expr_stmt|;
block|}
DECL|method|storeFlowActivityDoc (FlowActivityDocument flowActivityDoc)
specifier|private
name|void
name|storeFlowActivityDoc
parameter_list|(
name|FlowActivityDocument
name|flowActivityDoc
parameter_list|)
block|{
name|flowActivityCollWriter
operator|.
name|writeDocument
argument_list|(
name|flowActivityDoc
argument_list|)
expr_stmt|;
block|}
DECL|method|writeEntityDoc (TimelineEntityDocument entityDocument)
specifier|private
name|void
name|writeEntityDoc
parameter_list|(
name|TimelineEntityDocument
name|entityDocument
parameter_list|)
block|{
name|entityCollWriter
operator|.
name|writeDocument
argument_list|(
name|entityDocument
argument_list|)
expr_stmt|;
block|}
DECL|method|writeApplicationDoc (TimelineEntityDocument entityDocument)
specifier|private
name|void
name|writeApplicationDoc
parameter_list|(
name|TimelineEntityDocument
name|entityDocument
parameter_list|)
block|{
name|appCollWriter
operator|.
name|writeDocument
argument_list|(
name|entityDocument
argument_list|)
expr_stmt|;
block|}
DECL|method|aggregate (TimelineEntity data, TimelineAggregationTrack track)
specifier|public
name|TimelineWriteResponse
name|aggregate
parameter_list|(
name|TimelineEntity
name|data
parameter_list|,
name|TimelineAggregationTrack
name|track
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|flush ()
specifier|public
name|void
name|flush
parameter_list|()
block|{   }
block|}
end_class

end_unit

