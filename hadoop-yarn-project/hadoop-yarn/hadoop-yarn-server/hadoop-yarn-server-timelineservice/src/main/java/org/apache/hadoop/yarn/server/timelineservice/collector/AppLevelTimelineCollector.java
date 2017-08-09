begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timelineservice.collector
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
name|collector
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
name|util
operator|.
name|concurrent
operator|.
name|ThreadFactoryBuilder
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
name|classification
operator|.
name|InterfaceStability
operator|.
name|Unstable
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
name|timelineservice
operator|.
name|TimelineEntities
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
name|conf
operator|.
name|YarnConfiguration
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
name|concurrent
operator|.
name|ScheduledThreadPoolExecutor
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
name|TimeUnit
import|;
end_import

begin_comment
comment|/**  * Service that handles writes to the timeline service and writes them to the  * backing storage for a given YARN application.  *  * App-related lifecycle management is handled by this service.  */
end_comment

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|AppLevelTimelineCollector
specifier|public
class|class
name|AppLevelTimelineCollector
extends|extends
name|TimelineCollector
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
name|TimelineCollector
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|AGGREGATION_EXECUTOR_NUM_THREADS
specifier|private
specifier|final
specifier|static
name|int
name|AGGREGATION_EXECUTOR_NUM_THREADS
init|=
literal|1
decl_stmt|;
DECL|field|AGGREGATION_EXECUTOR_EXEC_INTERVAL_SECS
specifier|private
specifier|final
specifier|static
name|int
name|AGGREGATION_EXECUTOR_EXEC_INTERVAL_SECS
init|=
literal|15
decl_stmt|;
DECL|field|entityTypesSkipAggregation
specifier|private
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|entityTypesSkipAggregation
init|=
name|initializeSkipSet
argument_list|()
decl_stmt|;
DECL|field|appId
specifier|private
specifier|final
name|ApplicationId
name|appId
decl_stmt|;
DECL|field|context
specifier|private
specifier|final
name|TimelineCollectorContext
name|context
decl_stmt|;
DECL|field|appAggregationExecutor
specifier|private
name|ScheduledThreadPoolExecutor
name|appAggregationExecutor
decl_stmt|;
DECL|field|appAggregator
specifier|private
name|AppLevelAggregator
name|appAggregator
decl_stmt|;
DECL|field|currentUser
specifier|private
name|UserGroupInformation
name|currentUser
decl_stmt|;
DECL|method|AppLevelTimelineCollector (ApplicationId appId)
specifier|public
name|AppLevelTimelineCollector
parameter_list|(
name|ApplicationId
name|appId
parameter_list|)
block|{
name|super
argument_list|(
name|AppLevelTimelineCollector
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|" - "
operator|+
name|appId
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|appId
argument_list|,
literal|"AppId shouldn't be null"
argument_list|)
expr_stmt|;
name|this
operator|.
name|appId
operator|=
name|appId
expr_stmt|;
name|context
operator|=
operator|new
name|TimelineCollectorContext
argument_list|()
expr_stmt|;
block|}
DECL|method|initializeSkipSet ()
specifier|private
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|initializeSkipSet
parameter_list|()
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|result
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|result
operator|.
name|add
argument_list|(
name|TimelineEntityType
operator|.
name|YARN_APPLICATION
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|TimelineEntityType
operator|.
name|YARN_FLOW_RUN
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|TimelineEntityType
operator|.
name|YARN_FLOW_ACTIVITY
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|result
return|;
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
name|context
operator|.
name|setClusterId
argument_list|(
name|conf
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|RM_CLUSTER_ID
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_CLUSTER_ID
argument_list|)
argument_list|)
expr_stmt|;
comment|// Set the default values, which will be updated with an RPC call to get the
comment|// context info from NM.
comment|// Current user usually is not the app user, but keep this field non-null
name|currentUser
operator|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
expr_stmt|;
name|context
operator|.
name|setUserId
argument_list|(
name|currentUser
operator|.
name|getShortUserName
argument_list|()
argument_list|)
expr_stmt|;
name|context
operator|.
name|setAppId
argument_list|(
name|appId
operator|.
name|toString
argument_list|()
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
comment|// Launch the aggregation thread
name|appAggregationExecutor
operator|=
operator|new
name|ScheduledThreadPoolExecutor
argument_list|(
name|AppLevelTimelineCollector
operator|.
name|AGGREGATION_EXECUTOR_NUM_THREADS
argument_list|,
operator|new
name|ThreadFactoryBuilder
argument_list|()
operator|.
name|setNameFormat
argument_list|(
literal|"TimelineCollector Aggregation thread #%d"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|appAggregator
operator|=
operator|new
name|AppLevelAggregator
argument_list|()
expr_stmt|;
name|appAggregationExecutor
operator|.
name|scheduleAtFixedRate
argument_list|(
name|appAggregator
argument_list|,
name|AppLevelTimelineCollector
operator|.
name|AGGREGATION_EXECUTOR_EXEC_INTERVAL_SECS
argument_list|,
name|AppLevelTimelineCollector
operator|.
name|AGGREGATION_EXECUTOR_EXEC_INTERVAL_SECS
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
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
name|appAggregationExecutor
operator|.
name|shutdown
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|appAggregationExecutor
operator|.
name|awaitTermination
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"App-level aggregator shutdown timed out, shutdown now. "
argument_list|)
expr_stmt|;
name|appAggregationExecutor
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
block|}
comment|// Perform one round of aggregation after the aggregation executor is done.
name|appAggregator
operator|.
name|aggregate
argument_list|()
expr_stmt|;
name|super
operator|.
name|serviceStop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getTimelineEntityContext ()
specifier|public
name|TimelineCollectorContext
name|getTimelineEntityContext
parameter_list|()
block|{
return|return
name|context
return|;
block|}
annotation|@
name|Override
DECL|method|getEntityTypesSkipAggregation ()
specifier|protected
name|Set
argument_list|<
name|String
argument_list|>
name|getEntityTypesSkipAggregation
parameter_list|()
block|{
return|return
name|entityTypesSkipAggregation
return|;
block|}
DECL|class|AppLevelAggregator
specifier|private
class|class
name|AppLevelAggregator
implements|implements
name|Runnable
block|{
DECL|method|aggregate ()
specifier|private
name|void
name|aggregate
parameter_list|()
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
literal|"App-level real-time aggregating"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|isReadyToAggregate
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"App-level collector is not ready, skip aggregation. "
argument_list|)
expr_stmt|;
return|return;
block|}
try|try
block|{
name|TimelineCollectorContext
name|currContext
init|=
name|getTimelineEntityContext
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|AggregationStatusTable
argument_list|>
name|aggregationGroups
init|=
name|getAggregationGroups
argument_list|()
decl_stmt|;
if|if
condition|(
name|aggregationGroups
operator|==
literal|null
operator|||
name|aggregationGroups
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"App-level collector is empty, skip aggregation. "
argument_list|)
expr_stmt|;
return|return;
block|}
name|TimelineEntity
name|resultEntity
init|=
name|TimelineCollector
operator|.
name|aggregateWithoutGroupId
argument_list|(
name|aggregationGroups
argument_list|,
name|currContext
operator|.
name|getAppId
argument_list|()
argument_list|,
name|TimelineEntityType
operator|.
name|YARN_APPLICATION
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|TimelineEntities
name|entities
init|=
operator|new
name|TimelineEntities
argument_list|()
decl_stmt|;
name|entities
operator|.
name|addEntity
argument_list|(
name|resultEntity
argument_list|)
expr_stmt|;
name|putEntitiesAsync
argument_list|(
name|entities
argument_list|,
name|currentUser
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
literal|"Error aggregating timeline metrics"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
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
literal|"App-level real-time aggregation complete"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
name|aggregate
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

