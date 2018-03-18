begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.router.webapp
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
name|router
operator|.
name|webapp
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
name|Set
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletRequest
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletResponse
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|Response
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
name|authorize
operator|.
name|AuthorizationException
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
name|server
operator|.
name|resourcemanager
operator|.
name|webapp
operator|.
name|dao
operator|.
name|ActivitiesInfo
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
name|webapp
operator|.
name|dao
operator|.
name|AppActivitiesInfo
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
name|webapp
operator|.
name|dao
operator|.
name|AppAttemptsInfo
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
name|webapp
operator|.
name|dao
operator|.
name|AppInfo
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
name|webapp
operator|.
name|dao
operator|.
name|AppPriority
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
name|webapp
operator|.
name|dao
operator|.
name|AppQueue
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
name|webapp
operator|.
name|dao
operator|.
name|AppState
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
name|webapp
operator|.
name|dao
operator|.
name|AppTimeoutInfo
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
name|webapp
operator|.
name|dao
operator|.
name|AppTimeoutsInfo
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
name|webapp
operator|.
name|dao
operator|.
name|ApplicationStatisticsInfo
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
name|webapp
operator|.
name|dao
operator|.
name|ApplicationSubmissionContextInfo
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
name|webapp
operator|.
name|dao
operator|.
name|AppsInfo
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
name|webapp
operator|.
name|dao
operator|.
name|ClusterInfo
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
name|webapp
operator|.
name|dao
operator|.
name|ClusterMetricsInfo
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
name|webapp
operator|.
name|dao
operator|.
name|DelegationToken
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
name|webapp
operator|.
name|dao
operator|.
name|LabelsToNodesInfo
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
name|webapp
operator|.
name|dao
operator|.
name|NodeInfo
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
name|webapp
operator|.
name|dao
operator|.
name|NodeLabelsInfo
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
name|webapp
operator|.
name|dao
operator|.
name|NodeToLabelsEntryList
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
name|webapp
operator|.
name|dao
operator|.
name|NodeToLabelsInfo
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
name|webapp
operator|.
name|dao
operator|.
name|NodesInfo
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
name|webapp
operator|.
name|dao
operator|.
name|ReservationDeleteRequestInfo
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
name|webapp
operator|.
name|dao
operator|.
name|ReservationSubmissionRequestInfo
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
name|webapp
operator|.
name|dao
operator|.
name|ReservationUpdateRequestInfo
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
name|webapp
operator|.
name|dao
operator|.
name|SchedulerTypeInfo
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
name|webapp
operator|.
name|dao
operator|.
name|AppAttemptInfo
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
name|webapp
operator|.
name|dao
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
name|yarn
operator|.
name|server
operator|.
name|webapp
operator|.
name|dao
operator|.
name|ContainersInfo
import|;
end_import

begin_comment
comment|/**  * Mock intercepter that does not do anything other than forwarding it to the  * next intercepter in the chain.  */
end_comment

begin_class
DECL|class|PassThroughRESTRequestInterceptor
specifier|public
class|class
name|PassThroughRESTRequestInterceptor
extends|extends
name|AbstractRESTRequestInterceptor
block|{
annotation|@
name|Override
DECL|method|getAppAttempts (HttpServletRequest hsr, String appId)
specifier|public
name|AppAttemptsInfo
name|getAppAttempts
parameter_list|(
name|HttpServletRequest
name|hsr
parameter_list|,
name|String
name|appId
parameter_list|)
block|{
return|return
name|getNextInterceptor
argument_list|()
operator|.
name|getAppAttempts
argument_list|(
name|hsr
argument_list|,
name|appId
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|checkUserAccessToQueue (String queue, String username, String queueAclType, HttpServletRequest hsr)
specifier|public
name|Response
name|checkUserAccessToQueue
parameter_list|(
name|String
name|queue
parameter_list|,
name|String
name|username
parameter_list|,
name|String
name|queueAclType
parameter_list|,
name|HttpServletRequest
name|hsr
parameter_list|)
throws|throws
name|AuthorizationException
block|{
return|return
name|getNextInterceptor
argument_list|()
operator|.
name|checkUserAccessToQueue
argument_list|(
name|queue
argument_list|,
name|username
argument_list|,
name|queueAclType
argument_list|,
name|hsr
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getAppAttempt (HttpServletRequest req, HttpServletResponse res, String appId, String appAttemptId)
specifier|public
name|AppAttemptInfo
name|getAppAttempt
parameter_list|(
name|HttpServletRequest
name|req
parameter_list|,
name|HttpServletResponse
name|res
parameter_list|,
name|String
name|appId
parameter_list|,
name|String
name|appAttemptId
parameter_list|)
block|{
return|return
name|getNextInterceptor
argument_list|()
operator|.
name|getAppAttempt
argument_list|(
name|req
argument_list|,
name|res
argument_list|,
name|appId
argument_list|,
name|appAttemptId
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getContainers (HttpServletRequest req, HttpServletResponse res, String appId, String appAttemptId)
specifier|public
name|ContainersInfo
name|getContainers
parameter_list|(
name|HttpServletRequest
name|req
parameter_list|,
name|HttpServletResponse
name|res
parameter_list|,
name|String
name|appId
parameter_list|,
name|String
name|appAttemptId
parameter_list|)
block|{
return|return
name|getNextInterceptor
argument_list|()
operator|.
name|getContainers
argument_list|(
name|req
argument_list|,
name|res
argument_list|,
name|appId
argument_list|,
name|appAttemptId
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getContainer (HttpServletRequest req, HttpServletResponse res, String appId, String appAttemptId, String containerId)
specifier|public
name|ContainerInfo
name|getContainer
parameter_list|(
name|HttpServletRequest
name|req
parameter_list|,
name|HttpServletResponse
name|res
parameter_list|,
name|String
name|appId
parameter_list|,
name|String
name|appAttemptId
parameter_list|,
name|String
name|containerId
parameter_list|)
block|{
return|return
name|getNextInterceptor
argument_list|()
operator|.
name|getContainer
argument_list|(
name|req
argument_list|,
name|res
argument_list|,
name|appId
argument_list|,
name|appAttemptId
argument_list|,
name|containerId
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|get ()
specifier|public
name|ClusterInfo
name|get
parameter_list|()
block|{
return|return
name|getNextInterceptor
argument_list|()
operator|.
name|get
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getClusterInfo ()
specifier|public
name|ClusterInfo
name|getClusterInfo
parameter_list|()
block|{
return|return
name|getNextInterceptor
argument_list|()
operator|.
name|getClusterInfo
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getClusterMetricsInfo ()
specifier|public
name|ClusterMetricsInfo
name|getClusterMetricsInfo
parameter_list|()
block|{
return|return
name|getNextInterceptor
argument_list|()
operator|.
name|getClusterMetricsInfo
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getSchedulerInfo ()
specifier|public
name|SchedulerTypeInfo
name|getSchedulerInfo
parameter_list|()
block|{
return|return
name|getNextInterceptor
argument_list|()
operator|.
name|getSchedulerInfo
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|dumpSchedulerLogs (String time, HttpServletRequest hsr)
specifier|public
name|String
name|dumpSchedulerLogs
parameter_list|(
name|String
name|time
parameter_list|,
name|HttpServletRequest
name|hsr
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getNextInterceptor
argument_list|()
operator|.
name|dumpSchedulerLogs
argument_list|(
name|time
argument_list|,
name|hsr
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getNodes (String states)
specifier|public
name|NodesInfo
name|getNodes
parameter_list|(
name|String
name|states
parameter_list|)
block|{
return|return
name|getNextInterceptor
argument_list|()
operator|.
name|getNodes
argument_list|(
name|states
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getNode (String nodeId)
specifier|public
name|NodeInfo
name|getNode
parameter_list|(
name|String
name|nodeId
parameter_list|)
block|{
return|return
name|getNextInterceptor
argument_list|()
operator|.
name|getNode
argument_list|(
name|nodeId
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getApps (HttpServletRequest hsr, String stateQuery, Set<String> statesQuery, String finalStatusQuery, String userQuery, String queueQuery, String count, String startedBegin, String startedEnd, String finishBegin, String finishEnd, Set<String> applicationTypes, Set<String> applicationTags, Set<String> unselectedFields)
specifier|public
name|AppsInfo
name|getApps
parameter_list|(
name|HttpServletRequest
name|hsr
parameter_list|,
name|String
name|stateQuery
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|statesQuery
parameter_list|,
name|String
name|finalStatusQuery
parameter_list|,
name|String
name|userQuery
parameter_list|,
name|String
name|queueQuery
parameter_list|,
name|String
name|count
parameter_list|,
name|String
name|startedBegin
parameter_list|,
name|String
name|startedEnd
parameter_list|,
name|String
name|finishBegin
parameter_list|,
name|String
name|finishEnd
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|applicationTypes
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|applicationTags
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|unselectedFields
parameter_list|)
block|{
return|return
name|getNextInterceptor
argument_list|()
operator|.
name|getApps
argument_list|(
name|hsr
argument_list|,
name|stateQuery
argument_list|,
name|statesQuery
argument_list|,
name|finalStatusQuery
argument_list|,
name|userQuery
argument_list|,
name|queueQuery
argument_list|,
name|count
argument_list|,
name|startedBegin
argument_list|,
name|startedEnd
argument_list|,
name|finishBegin
argument_list|,
name|finishEnd
argument_list|,
name|applicationTypes
argument_list|,
name|applicationTags
argument_list|,
name|unselectedFields
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getActivities (HttpServletRequest hsr, String nodeId)
specifier|public
name|ActivitiesInfo
name|getActivities
parameter_list|(
name|HttpServletRequest
name|hsr
parameter_list|,
name|String
name|nodeId
parameter_list|)
block|{
return|return
name|getNextInterceptor
argument_list|()
operator|.
name|getActivities
argument_list|(
name|hsr
argument_list|,
name|nodeId
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getAppActivities (HttpServletRequest hsr, String appId, String time)
specifier|public
name|AppActivitiesInfo
name|getAppActivities
parameter_list|(
name|HttpServletRequest
name|hsr
parameter_list|,
name|String
name|appId
parameter_list|,
name|String
name|time
parameter_list|)
block|{
return|return
name|getNextInterceptor
argument_list|()
operator|.
name|getAppActivities
argument_list|(
name|hsr
argument_list|,
name|appId
argument_list|,
name|time
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getAppStatistics (HttpServletRequest hsr, Set<String> stateQueries, Set<String> typeQueries)
specifier|public
name|ApplicationStatisticsInfo
name|getAppStatistics
parameter_list|(
name|HttpServletRequest
name|hsr
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|stateQueries
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|typeQueries
parameter_list|)
block|{
return|return
name|getNextInterceptor
argument_list|()
operator|.
name|getAppStatistics
argument_list|(
name|hsr
argument_list|,
name|stateQueries
argument_list|,
name|typeQueries
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getApp (HttpServletRequest hsr, String appId, Set<String> unselectedFields)
specifier|public
name|AppInfo
name|getApp
parameter_list|(
name|HttpServletRequest
name|hsr
parameter_list|,
name|String
name|appId
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|unselectedFields
parameter_list|)
block|{
return|return
name|getNextInterceptor
argument_list|()
operator|.
name|getApp
argument_list|(
name|hsr
argument_list|,
name|appId
argument_list|,
name|unselectedFields
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getAppState (HttpServletRequest hsr, String appId)
specifier|public
name|AppState
name|getAppState
parameter_list|(
name|HttpServletRequest
name|hsr
parameter_list|,
name|String
name|appId
parameter_list|)
throws|throws
name|AuthorizationException
block|{
return|return
name|getNextInterceptor
argument_list|()
operator|.
name|getAppState
argument_list|(
name|hsr
argument_list|,
name|appId
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|updateAppState (AppState targetState, HttpServletRequest hsr, String appId)
specifier|public
name|Response
name|updateAppState
parameter_list|(
name|AppState
name|targetState
parameter_list|,
name|HttpServletRequest
name|hsr
parameter_list|,
name|String
name|appId
parameter_list|)
throws|throws
name|AuthorizationException
throws|,
name|YarnException
throws|,
name|InterruptedException
throws|,
name|IOException
block|{
return|return
name|getNextInterceptor
argument_list|()
operator|.
name|updateAppState
argument_list|(
name|targetState
argument_list|,
name|hsr
argument_list|,
name|appId
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getNodeToLabels (HttpServletRequest hsr)
specifier|public
name|NodeToLabelsInfo
name|getNodeToLabels
parameter_list|(
name|HttpServletRequest
name|hsr
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getNextInterceptor
argument_list|()
operator|.
name|getNodeToLabels
argument_list|(
name|hsr
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getLabelsToNodes (Set<String> labels)
specifier|public
name|LabelsToNodesInfo
name|getLabelsToNodes
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|labels
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getNextInterceptor
argument_list|()
operator|.
name|getLabelsToNodes
argument_list|(
name|labels
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|replaceLabelsOnNodes (NodeToLabelsEntryList newNodeToLabels, HttpServletRequest hsr)
specifier|public
name|Response
name|replaceLabelsOnNodes
parameter_list|(
name|NodeToLabelsEntryList
name|newNodeToLabels
parameter_list|,
name|HttpServletRequest
name|hsr
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|getNextInterceptor
argument_list|()
operator|.
name|replaceLabelsOnNodes
argument_list|(
name|newNodeToLabels
argument_list|,
name|hsr
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|replaceLabelsOnNode (Set<String> newNodeLabelsName, HttpServletRequest hsr, String nodeId)
specifier|public
name|Response
name|replaceLabelsOnNode
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|newNodeLabelsName
parameter_list|,
name|HttpServletRequest
name|hsr
parameter_list|,
name|String
name|nodeId
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|getNextInterceptor
argument_list|()
operator|.
name|replaceLabelsOnNode
argument_list|(
name|newNodeLabelsName
argument_list|,
name|hsr
argument_list|,
name|nodeId
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getClusterNodeLabels (HttpServletRequest hsr)
specifier|public
name|NodeLabelsInfo
name|getClusterNodeLabels
parameter_list|(
name|HttpServletRequest
name|hsr
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getNextInterceptor
argument_list|()
operator|.
name|getClusterNodeLabels
argument_list|(
name|hsr
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|addToClusterNodeLabels (NodeLabelsInfo newNodeLabels, HttpServletRequest hsr)
specifier|public
name|Response
name|addToClusterNodeLabels
parameter_list|(
name|NodeLabelsInfo
name|newNodeLabels
parameter_list|,
name|HttpServletRequest
name|hsr
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|getNextInterceptor
argument_list|()
operator|.
name|addToClusterNodeLabels
argument_list|(
name|newNodeLabels
argument_list|,
name|hsr
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|removeFromCluserNodeLabels (Set<String> oldNodeLabels, HttpServletRequest hsr)
specifier|public
name|Response
name|removeFromCluserNodeLabels
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|oldNodeLabels
parameter_list|,
name|HttpServletRequest
name|hsr
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|getNextInterceptor
argument_list|()
operator|.
name|removeFromCluserNodeLabels
argument_list|(
name|oldNodeLabels
argument_list|,
name|hsr
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getLabelsOnNode (HttpServletRequest hsr, String nodeId)
specifier|public
name|NodeLabelsInfo
name|getLabelsOnNode
parameter_list|(
name|HttpServletRequest
name|hsr
parameter_list|,
name|String
name|nodeId
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getNextInterceptor
argument_list|()
operator|.
name|getLabelsOnNode
argument_list|(
name|hsr
argument_list|,
name|nodeId
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getAppPriority (HttpServletRequest hsr, String appId)
specifier|public
name|AppPriority
name|getAppPriority
parameter_list|(
name|HttpServletRequest
name|hsr
parameter_list|,
name|String
name|appId
parameter_list|)
throws|throws
name|AuthorizationException
block|{
return|return
name|getNextInterceptor
argument_list|()
operator|.
name|getAppPriority
argument_list|(
name|hsr
argument_list|,
name|appId
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|updateApplicationPriority (AppPriority targetPriority, HttpServletRequest hsr, String appId)
specifier|public
name|Response
name|updateApplicationPriority
parameter_list|(
name|AppPriority
name|targetPriority
parameter_list|,
name|HttpServletRequest
name|hsr
parameter_list|,
name|String
name|appId
parameter_list|)
throws|throws
name|AuthorizationException
throws|,
name|YarnException
throws|,
name|InterruptedException
throws|,
name|IOException
block|{
return|return
name|getNextInterceptor
argument_list|()
operator|.
name|updateApplicationPriority
argument_list|(
name|targetPriority
argument_list|,
name|hsr
argument_list|,
name|appId
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getAppQueue (HttpServletRequest hsr, String appId)
specifier|public
name|AppQueue
name|getAppQueue
parameter_list|(
name|HttpServletRequest
name|hsr
parameter_list|,
name|String
name|appId
parameter_list|)
throws|throws
name|AuthorizationException
block|{
return|return
name|getNextInterceptor
argument_list|()
operator|.
name|getAppQueue
argument_list|(
name|hsr
argument_list|,
name|appId
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|updateAppQueue (AppQueue targetQueue, HttpServletRequest hsr, String appId)
specifier|public
name|Response
name|updateAppQueue
parameter_list|(
name|AppQueue
name|targetQueue
parameter_list|,
name|HttpServletRequest
name|hsr
parameter_list|,
name|String
name|appId
parameter_list|)
throws|throws
name|AuthorizationException
throws|,
name|YarnException
throws|,
name|InterruptedException
throws|,
name|IOException
block|{
return|return
name|getNextInterceptor
argument_list|()
operator|.
name|updateAppQueue
argument_list|(
name|targetQueue
argument_list|,
name|hsr
argument_list|,
name|appId
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|createNewApplication (HttpServletRequest hsr)
specifier|public
name|Response
name|createNewApplication
parameter_list|(
name|HttpServletRequest
name|hsr
parameter_list|)
throws|throws
name|AuthorizationException
throws|,
name|IOException
throws|,
name|InterruptedException
block|{
return|return
name|getNextInterceptor
argument_list|()
operator|.
name|createNewApplication
argument_list|(
name|hsr
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|submitApplication (ApplicationSubmissionContextInfo newApp, HttpServletRequest hsr)
specifier|public
name|Response
name|submitApplication
parameter_list|(
name|ApplicationSubmissionContextInfo
name|newApp
parameter_list|,
name|HttpServletRequest
name|hsr
parameter_list|)
throws|throws
name|AuthorizationException
throws|,
name|IOException
throws|,
name|InterruptedException
block|{
return|return
name|getNextInterceptor
argument_list|()
operator|.
name|submitApplication
argument_list|(
name|newApp
argument_list|,
name|hsr
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|postDelegationToken (DelegationToken tokenData, HttpServletRequest hsr)
specifier|public
name|Response
name|postDelegationToken
parameter_list|(
name|DelegationToken
name|tokenData
parameter_list|,
name|HttpServletRequest
name|hsr
parameter_list|)
throws|throws
name|AuthorizationException
throws|,
name|IOException
throws|,
name|InterruptedException
throws|,
name|Exception
block|{
return|return
name|getNextInterceptor
argument_list|()
operator|.
name|postDelegationToken
argument_list|(
name|tokenData
argument_list|,
name|hsr
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|postDelegationTokenExpiration (HttpServletRequest hsr)
specifier|public
name|Response
name|postDelegationTokenExpiration
parameter_list|(
name|HttpServletRequest
name|hsr
parameter_list|)
throws|throws
name|AuthorizationException
throws|,
name|IOException
throws|,
name|Exception
block|{
return|return
name|getNextInterceptor
argument_list|()
operator|.
name|postDelegationTokenExpiration
argument_list|(
name|hsr
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|cancelDelegationToken (HttpServletRequest hsr)
specifier|public
name|Response
name|cancelDelegationToken
parameter_list|(
name|HttpServletRequest
name|hsr
parameter_list|)
throws|throws
name|AuthorizationException
throws|,
name|IOException
throws|,
name|InterruptedException
throws|,
name|Exception
block|{
return|return
name|getNextInterceptor
argument_list|()
operator|.
name|cancelDelegationToken
argument_list|(
name|hsr
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|createNewReservation (HttpServletRequest hsr)
specifier|public
name|Response
name|createNewReservation
parameter_list|(
name|HttpServletRequest
name|hsr
parameter_list|)
throws|throws
name|AuthorizationException
throws|,
name|IOException
throws|,
name|InterruptedException
block|{
return|return
name|getNextInterceptor
argument_list|()
operator|.
name|createNewReservation
argument_list|(
name|hsr
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|submitReservation (ReservationSubmissionRequestInfo resContext, HttpServletRequest hsr)
specifier|public
name|Response
name|submitReservation
parameter_list|(
name|ReservationSubmissionRequestInfo
name|resContext
parameter_list|,
name|HttpServletRequest
name|hsr
parameter_list|)
throws|throws
name|AuthorizationException
throws|,
name|IOException
throws|,
name|InterruptedException
block|{
return|return
name|getNextInterceptor
argument_list|()
operator|.
name|submitReservation
argument_list|(
name|resContext
argument_list|,
name|hsr
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|updateReservation (ReservationUpdateRequestInfo resContext, HttpServletRequest hsr)
specifier|public
name|Response
name|updateReservation
parameter_list|(
name|ReservationUpdateRequestInfo
name|resContext
parameter_list|,
name|HttpServletRequest
name|hsr
parameter_list|)
throws|throws
name|AuthorizationException
throws|,
name|IOException
throws|,
name|InterruptedException
block|{
return|return
name|getNextInterceptor
argument_list|()
operator|.
name|updateReservation
argument_list|(
name|resContext
argument_list|,
name|hsr
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|deleteReservation (ReservationDeleteRequestInfo resContext, HttpServletRequest hsr)
specifier|public
name|Response
name|deleteReservation
parameter_list|(
name|ReservationDeleteRequestInfo
name|resContext
parameter_list|,
name|HttpServletRequest
name|hsr
parameter_list|)
throws|throws
name|AuthorizationException
throws|,
name|IOException
throws|,
name|InterruptedException
block|{
return|return
name|getNextInterceptor
argument_list|()
operator|.
name|deleteReservation
argument_list|(
name|resContext
argument_list|,
name|hsr
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|listReservation (String queue, String reservationId, long startTime, long endTime, boolean includeResourceAllocations, HttpServletRequest hsr)
specifier|public
name|Response
name|listReservation
parameter_list|(
name|String
name|queue
parameter_list|,
name|String
name|reservationId
parameter_list|,
name|long
name|startTime
parameter_list|,
name|long
name|endTime
parameter_list|,
name|boolean
name|includeResourceAllocations
parameter_list|,
name|HttpServletRequest
name|hsr
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|getNextInterceptor
argument_list|()
operator|.
name|listReservation
argument_list|(
name|queue
argument_list|,
name|reservationId
argument_list|,
name|startTime
argument_list|,
name|endTime
argument_list|,
name|includeResourceAllocations
argument_list|,
name|hsr
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getAppTimeout (HttpServletRequest hsr, String appId, String type)
specifier|public
name|AppTimeoutInfo
name|getAppTimeout
parameter_list|(
name|HttpServletRequest
name|hsr
parameter_list|,
name|String
name|appId
parameter_list|,
name|String
name|type
parameter_list|)
throws|throws
name|AuthorizationException
block|{
return|return
name|getNextInterceptor
argument_list|()
operator|.
name|getAppTimeout
argument_list|(
name|hsr
argument_list|,
name|appId
argument_list|,
name|type
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getAppTimeouts (HttpServletRequest hsr, String appId)
specifier|public
name|AppTimeoutsInfo
name|getAppTimeouts
parameter_list|(
name|HttpServletRequest
name|hsr
parameter_list|,
name|String
name|appId
parameter_list|)
throws|throws
name|AuthorizationException
block|{
return|return
name|getNextInterceptor
argument_list|()
operator|.
name|getAppTimeouts
argument_list|(
name|hsr
argument_list|,
name|appId
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|updateApplicationTimeout (AppTimeoutInfo appTimeout, HttpServletRequest hsr, String appId)
specifier|public
name|Response
name|updateApplicationTimeout
parameter_list|(
name|AppTimeoutInfo
name|appTimeout
parameter_list|,
name|HttpServletRequest
name|hsr
parameter_list|,
name|String
name|appId
parameter_list|)
throws|throws
name|AuthorizationException
throws|,
name|YarnException
throws|,
name|InterruptedException
throws|,
name|IOException
block|{
return|return
name|getNextInterceptor
argument_list|()
operator|.
name|updateApplicationTimeout
argument_list|(
name|appTimeout
argument_list|,
name|hsr
argument_list|,
name|appId
argument_list|)
return|;
block|}
block|}
end_class

end_unit

