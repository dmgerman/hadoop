begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.webapp
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
name|webapp
package|;
end_package

begin_import
import|import static
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
name|StringHelper
operator|.
name|join
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|webapp
operator|.
name|view
operator|.
name|JQueryUI
operator|.
name|C_PROGRESSBAR
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|webapp
operator|.
name|view
operator|.
name|JQueryUI
operator|.
name|C_PROGRESSBAR_VALUE
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
name|List
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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang
operator|.
name|StringEscapeUtils
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
name|util
operator|.
name|StringUtils
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
name|GetApplicationsRequest
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
name|ApplicationReport
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
name|YarnApplicationState
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
name|ResourceManager
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
name|RMApp
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
name|attempt
operator|.
name|RMAppAttempt
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
name|AppsBlock
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
name|webapp
operator|.
name|View
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
name|webapp
operator|.
name|hamlet2
operator|.
name|Hamlet
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
name|webapp
operator|.
name|hamlet2
operator|.
name|Hamlet
operator|.
name|TABLE
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
name|webapp
operator|.
name|hamlet2
operator|.
name|Hamlet
operator|.
name|TBODY
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Inject
import|;
end_import

begin_class
DECL|class|RMAppsBlock
specifier|public
class|class
name|RMAppsBlock
extends|extends
name|AppsBlock
block|{
DECL|field|rm
specifier|private
name|ResourceManager
name|rm
decl_stmt|;
annotation|@
name|Inject
DECL|method|RMAppsBlock (ResourceManager rm, View.ViewContext ctx)
name|RMAppsBlock
parameter_list|(
name|ResourceManager
name|rm
parameter_list|,
name|View
operator|.
name|ViewContext
name|ctx
parameter_list|)
block|{
name|super
argument_list|(
literal|null
argument_list|,
name|ctx
argument_list|)
expr_stmt|;
name|this
operator|.
name|rm
operator|=
name|rm
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|renderData (Block html)
specifier|protected
name|void
name|renderData
parameter_list|(
name|Block
name|html
parameter_list|)
block|{
name|TBODY
argument_list|<
name|TABLE
argument_list|<
name|Hamlet
argument_list|>
argument_list|>
name|tbody
init|=
name|html
operator|.
name|table
argument_list|(
literal|"#apps"
argument_list|)
operator|.
name|thead
argument_list|()
operator|.
name|tr
argument_list|()
operator|.
name|th
argument_list|(
literal|".id"
argument_list|,
literal|"ID"
argument_list|)
operator|.
name|th
argument_list|(
literal|".user"
argument_list|,
literal|"User"
argument_list|)
operator|.
name|th
argument_list|(
literal|".name"
argument_list|,
literal|"Name"
argument_list|)
operator|.
name|th
argument_list|(
literal|".type"
argument_list|,
literal|"Application Type"
argument_list|)
operator|.
name|th
argument_list|(
literal|".queue"
argument_list|,
literal|"Queue"
argument_list|)
operator|.
name|th
argument_list|(
literal|".priority"
argument_list|,
literal|"Application Priority"
argument_list|)
operator|.
name|th
argument_list|(
literal|".starttime"
argument_list|,
literal|"StartTime"
argument_list|)
operator|.
name|th
argument_list|(
literal|".finishtime"
argument_list|,
literal|"FinishTime"
argument_list|)
operator|.
name|th
argument_list|(
literal|".state"
argument_list|,
literal|"State"
argument_list|)
operator|.
name|th
argument_list|(
literal|".finalstatus"
argument_list|,
literal|"FinalStatus"
argument_list|)
operator|.
name|th
argument_list|(
literal|".runningcontainer"
argument_list|,
literal|"Running Containers"
argument_list|)
operator|.
name|th
argument_list|(
literal|".allocatedCpu"
argument_list|,
literal|"Allocated CPU VCores"
argument_list|)
operator|.
name|th
argument_list|(
literal|".allocatedMemory"
argument_list|,
literal|"Allocated Memory MB"
argument_list|)
operator|.
name|th
argument_list|(
literal|".reservedCpu"
argument_list|,
literal|"Reserved CPU VCores"
argument_list|)
operator|.
name|th
argument_list|(
literal|".reservedMemory"
argument_list|,
literal|"Reserved Memory MB"
argument_list|)
operator|.
name|th
argument_list|(
literal|".queuePercentage"
argument_list|,
literal|"% of Queue"
argument_list|)
operator|.
name|th
argument_list|(
literal|".clusterPercentage"
argument_list|,
literal|"% of Cluster"
argument_list|)
operator|.
name|th
argument_list|(
literal|".progress"
argument_list|,
literal|"Progress"
argument_list|)
operator|.
name|th
argument_list|(
literal|".ui"
argument_list|,
literal|"Tracking UI"
argument_list|)
operator|.
name|th
argument_list|(
literal|".blacklisted"
argument_list|,
literal|"Blacklisted Nodes"
argument_list|)
operator|.
name|__
argument_list|()
operator|.
name|__
argument_list|()
operator|.
name|tbody
argument_list|()
decl_stmt|;
name|StringBuilder
name|appsTableData
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"[\n"
argument_list|)
decl_stmt|;
for|for
control|(
name|ApplicationReport
name|appReport
range|:
name|appReports
control|)
block|{
comment|// TODO: remove the following condition. It is still here because
comment|// the history side implementation of ApplicationBaseProtocol
comment|// hasn't filtering capability (YARN-1819).
if|if
condition|(
operator|!
name|reqAppStates
operator|.
name|isEmpty
argument_list|()
operator|&&
operator|!
name|reqAppStates
operator|.
name|contains
argument_list|(
name|appReport
operator|.
name|getYarnApplicationState
argument_list|()
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|AppInfo
name|app
init|=
operator|new
name|AppInfo
argument_list|(
name|appReport
argument_list|)
decl_stmt|;
name|ApplicationAttemptId
name|appAttemptId
init|=
name|ApplicationAttemptId
operator|.
name|fromString
argument_list|(
name|app
operator|.
name|getCurrentAppAttemptId
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|queuePercent
init|=
literal|"N/A"
decl_stmt|;
name|String
name|clusterPercent
init|=
literal|"N/A"
decl_stmt|;
if|if
condition|(
name|appReport
operator|.
name|getApplicationResourceUsageReport
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|queuePercent
operator|=
name|String
operator|.
name|format
argument_list|(
literal|"%.1f"
argument_list|,
name|appReport
operator|.
name|getApplicationResourceUsageReport
argument_list|()
operator|.
name|getQueueUsagePercentage
argument_list|()
argument_list|)
expr_stmt|;
name|clusterPercent
operator|=
name|String
operator|.
name|format
argument_list|(
literal|"%.1f"
argument_list|,
name|appReport
operator|.
name|getApplicationResourceUsageReport
argument_list|()
operator|.
name|getClusterUsagePercentage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|String
name|blacklistedNodesCount
init|=
literal|"N/A"
decl_stmt|;
name|RMApp
name|rmApp
init|=
name|rm
operator|.
name|getRMContext
argument_list|()
operator|.
name|getRMApps
argument_list|()
operator|.
name|get
argument_list|(
name|appAttemptId
operator|.
name|getApplicationId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|rmApp
operator|!=
literal|null
condition|)
block|{
name|RMAppAttempt
name|appAttempt
init|=
name|rmApp
operator|.
name|getRMAppAttempt
argument_list|(
name|appAttemptId
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|nodes
init|=
literal|null
operator|==
name|appAttempt
condition|?
literal|null
else|:
name|appAttempt
operator|.
name|getBlacklistedNodes
argument_list|()
decl_stmt|;
if|if
condition|(
name|nodes
operator|!=
literal|null
condition|)
block|{
name|blacklistedNodesCount
operator|=
name|String
operator|.
name|valueOf
argument_list|(
name|nodes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|String
name|percent
init|=
name|StringUtils
operator|.
name|format
argument_list|(
literal|"%.1f"
argument_list|,
name|app
operator|.
name|getProgress
argument_list|()
argument_list|)
decl_stmt|;
name|appsTableData
operator|.
name|append
argument_list|(
literal|"[\"<a href='"
argument_list|)
operator|.
name|append
argument_list|(
name|url
argument_list|(
literal|"app"
argument_list|,
name|app
operator|.
name|getAppId
argument_list|()
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"'>"
argument_list|)
operator|.
name|append
argument_list|(
name|app
operator|.
name|getAppId
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"</a>\",\""
argument_list|)
operator|.
name|append
argument_list|(
name|StringEscapeUtils
operator|.
name|escapeJavaScript
argument_list|(
name|StringEscapeUtils
operator|.
name|escapeHtml
argument_list|(
name|app
operator|.
name|getUser
argument_list|()
argument_list|)
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"\",\""
argument_list|)
operator|.
name|append
argument_list|(
name|StringEscapeUtils
operator|.
name|escapeJavaScript
argument_list|(
name|StringEscapeUtils
operator|.
name|escapeHtml
argument_list|(
name|app
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"\",\""
argument_list|)
operator|.
name|append
argument_list|(
name|StringEscapeUtils
operator|.
name|escapeJavaScript
argument_list|(
name|StringEscapeUtils
operator|.
name|escapeHtml
argument_list|(
name|app
operator|.
name|getType
argument_list|()
argument_list|)
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"\",\""
argument_list|)
operator|.
name|append
argument_list|(
name|StringEscapeUtils
operator|.
name|escapeJavaScript
argument_list|(
name|StringEscapeUtils
operator|.
name|escapeHtml
argument_list|(
name|app
operator|.
name|getQueue
argument_list|()
argument_list|)
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"\",\""
argument_list|)
operator|.
name|append
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|app
operator|.
name|getPriority
argument_list|()
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"\",\""
argument_list|)
operator|.
name|append
argument_list|(
name|app
operator|.
name|getStartedTime
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"\",\""
argument_list|)
operator|.
name|append
argument_list|(
name|app
operator|.
name|getFinishedTime
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"\",\""
argument_list|)
operator|.
name|append
argument_list|(
name|app
operator|.
name|getAppState
argument_list|()
operator|==
literal|null
condition|?
name|UNAVAILABLE
else|:
name|app
operator|.
name|getAppState
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"\",\""
argument_list|)
operator|.
name|append
argument_list|(
name|app
operator|.
name|getFinalAppStatus
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"\",\""
argument_list|)
operator|.
name|append
argument_list|(
name|app
operator|.
name|getRunningContainers
argument_list|()
operator|==
operator|-
literal|1
condition|?
literal|"N/A"
else|:
name|String
operator|.
name|valueOf
argument_list|(
name|app
operator|.
name|getRunningContainers
argument_list|()
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"\",\""
argument_list|)
operator|.
name|append
argument_list|(
name|app
operator|.
name|getAllocatedCpuVcores
argument_list|()
operator|==
operator|-
literal|1
condition|?
literal|"N/A"
else|:
name|String
operator|.
name|valueOf
argument_list|(
name|app
operator|.
name|getAllocatedCpuVcores
argument_list|()
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"\",\""
argument_list|)
operator|.
name|append
argument_list|(
name|app
operator|.
name|getAllocatedMemoryMB
argument_list|()
operator|==
operator|-
literal|1
condition|?
literal|"N/A"
else|:
name|String
operator|.
name|valueOf
argument_list|(
name|app
operator|.
name|getAllocatedMemoryMB
argument_list|()
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"\",\""
argument_list|)
operator|.
name|append
argument_list|(
name|app
operator|.
name|getReservedCpuVcores
argument_list|()
operator|==
operator|-
literal|1
condition|?
literal|"N/A"
else|:
name|String
operator|.
name|valueOf
argument_list|(
name|app
operator|.
name|getReservedCpuVcores
argument_list|()
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"\",\""
argument_list|)
operator|.
name|append
argument_list|(
name|app
operator|.
name|getReservedMemoryMB
argument_list|()
operator|==
operator|-
literal|1
condition|?
literal|"N/A"
else|:
name|String
operator|.
name|valueOf
argument_list|(
name|app
operator|.
name|getReservedMemoryMB
argument_list|()
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"\",\""
argument_list|)
operator|.
name|append
argument_list|(
name|queuePercent
argument_list|)
operator|.
name|append
argument_list|(
literal|"\",\""
argument_list|)
operator|.
name|append
argument_list|(
name|clusterPercent
argument_list|)
operator|.
name|append
argument_list|(
literal|"\",\""
argument_list|)
comment|// Progress bar
operator|.
name|append
argument_list|(
literal|"<br title='"
argument_list|)
operator|.
name|append
argument_list|(
name|percent
argument_list|)
operator|.
name|append
argument_list|(
literal|"'><div class='"
argument_list|)
operator|.
name|append
argument_list|(
name|C_PROGRESSBAR
argument_list|)
operator|.
name|append
argument_list|(
literal|"' title='"
argument_list|)
operator|.
name|append
argument_list|(
name|join
argument_list|(
name|percent
argument_list|,
literal|'%'
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"'> "
argument_list|)
operator|.
name|append
argument_list|(
literal|"<div class='"
argument_list|)
operator|.
name|append
argument_list|(
name|C_PROGRESSBAR_VALUE
argument_list|)
operator|.
name|append
argument_list|(
literal|"' style='"
argument_list|)
operator|.
name|append
argument_list|(
name|join
argument_list|(
literal|"width:"
argument_list|,
name|percent
argument_list|,
literal|'%'
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"'></div></div>"
argument_list|)
operator|.
name|append
argument_list|(
literal|"\",\"<a "
argument_list|)
expr_stmt|;
name|String
name|trackingURL
init|=
name|app
operator|.
name|getTrackingUrl
argument_list|()
operator|==
literal|null
operator|||
name|app
operator|.
name|getTrackingUrl
argument_list|()
operator|.
name|equals
argument_list|(
name|UNAVAILABLE
argument_list|)
operator|||
name|app
operator|.
name|getAppState
argument_list|()
operator|==
name|YarnApplicationState
operator|.
name|NEW
condition|?
literal|null
else|:
name|app
operator|.
name|getTrackingUrl
argument_list|()
decl_stmt|;
name|String
name|trackingUI
init|=
name|app
operator|.
name|getTrackingUrl
argument_list|()
operator|==
literal|null
operator|||
name|app
operator|.
name|getTrackingUrl
argument_list|()
operator|.
name|equals
argument_list|(
name|UNAVAILABLE
argument_list|)
operator|||
name|app
operator|.
name|getAppState
argument_list|()
operator|==
name|YarnApplicationState
operator|.
name|NEW
condition|?
literal|"Unassigned"
else|:
name|app
operator|.
name|getAppState
argument_list|()
operator|==
name|YarnApplicationState
operator|.
name|FINISHED
operator|||
name|app
operator|.
name|getAppState
argument_list|()
operator|==
name|YarnApplicationState
operator|.
name|FAILED
operator|||
name|app
operator|.
name|getAppState
argument_list|()
operator|==
name|YarnApplicationState
operator|.
name|KILLED
condition|?
literal|"History"
else|:
literal|"ApplicationMaster"
decl_stmt|;
name|appsTableData
operator|.
name|append
argument_list|(
name|trackingURL
operator|==
literal|null
condition|?
literal|"#"
else|:
literal|"href='"
operator|+
name|trackingURL
argument_list|)
operator|.
name|append
argument_list|(
literal|"'>"
argument_list|)
operator|.
name|append
argument_list|(
name|trackingUI
argument_list|)
operator|.
name|append
argument_list|(
literal|"</a>\","
argument_list|)
operator|.
name|append
argument_list|(
literal|"\""
argument_list|)
operator|.
name|append
argument_list|(
name|blacklistedNodesCount
argument_list|)
operator|.
name|append
argument_list|(
literal|"\"],\n"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|appsTableData
operator|.
name|charAt
argument_list|(
name|appsTableData
operator|.
name|length
argument_list|()
operator|-
literal|2
argument_list|)
operator|==
literal|','
condition|)
block|{
name|appsTableData
operator|.
name|delete
argument_list|(
name|appsTableData
operator|.
name|length
argument_list|()
operator|-
literal|2
argument_list|,
name|appsTableData
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
name|appsTableData
operator|.
name|append
argument_list|(
literal|"]"
argument_list|)
expr_stmt|;
name|html
operator|.
name|script
argument_list|()
operator|.
name|$type
argument_list|(
literal|"text/javascript"
argument_list|)
operator|.
name|__
argument_list|(
literal|"var appsTableData="
operator|+
name|appsTableData
argument_list|)
operator|.
name|__
argument_list|()
expr_stmt|;
name|tbody
operator|.
name|__
argument_list|()
operator|.
name|__
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getApplicationReport ( final GetApplicationsRequest request)
specifier|protected
name|List
argument_list|<
name|ApplicationReport
argument_list|>
name|getApplicationReport
parameter_list|(
specifier|final
name|GetApplicationsRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
return|return
name|rm
operator|.
name|getClientRMService
argument_list|()
operator|.
name|getApplications
argument_list|(
name|request
argument_list|)
operator|.
name|getApplicationList
argument_list|()
return|;
block|}
block|}
end_class

end_unit

