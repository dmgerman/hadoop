begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
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
name|YarnWebParams
operator|.
name|APP_STATE
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
name|HashSet
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
name|ConcurrentMap
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
name|server
operator|.
name|resourcemanager
operator|.
name|RMContext
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
name|scheduler
operator|.
name|fair
operator|.
name|FairScheduler
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
name|FairSchedulerInfo
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
name|hamlet
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
name|hamlet
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
name|hamlet
operator|.
name|Hamlet
operator|.
name|TBODY
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
name|util
operator|.
name|WebAppUtils
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
name|view
operator|.
name|HtmlBlock
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

begin_comment
comment|/**  * Shows application information specific to the fair  * scheduler as part of the fair scheduler page.  */
end_comment

begin_class
DECL|class|FairSchedulerAppsBlock
specifier|public
class|class
name|FairSchedulerAppsBlock
extends|extends
name|HtmlBlock
block|{
DECL|field|apps
specifier|final
name|ConcurrentMap
argument_list|<
name|ApplicationId
argument_list|,
name|RMApp
argument_list|>
name|apps
decl_stmt|;
DECL|field|fsinfo
specifier|final
name|FairSchedulerInfo
name|fsinfo
decl_stmt|;
DECL|field|conf
specifier|final
name|Configuration
name|conf
decl_stmt|;
DECL|method|FairSchedulerAppsBlock (RMContext rmContext, ResourceManager rm, ViewContext ctx, Configuration conf)
annotation|@
name|Inject
specifier|public
name|FairSchedulerAppsBlock
parameter_list|(
name|RMContext
name|rmContext
parameter_list|,
name|ResourceManager
name|rm
parameter_list|,
name|ViewContext
name|ctx
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|super
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
name|FairScheduler
name|scheduler
init|=
operator|(
name|FairScheduler
operator|)
name|rm
operator|.
name|getResourceScheduler
argument_list|()
decl_stmt|;
name|fsinfo
operator|=
operator|new
name|FairSchedulerInfo
argument_list|(
name|scheduler
argument_list|)
expr_stmt|;
name|apps
operator|=
name|rmContext
operator|.
name|getRMApps
argument_list|()
expr_stmt|;
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
block|}
DECL|method|render (Block html)
annotation|@
name|Override
specifier|public
name|void
name|render
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
literal|".fairshare"
argument_list|,
literal|"Fair Share"
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
name|_
argument_list|()
operator|.
name|_
argument_list|()
operator|.
name|tbody
argument_list|()
decl_stmt|;
name|Collection
argument_list|<
name|YarnApplicationState
argument_list|>
name|reqAppStates
init|=
literal|null
decl_stmt|;
name|String
name|reqStateString
init|=
name|$
argument_list|(
name|APP_STATE
argument_list|)
decl_stmt|;
if|if
condition|(
name|reqStateString
operator|!=
literal|null
operator|&&
operator|!
name|reqStateString
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|String
index|[]
name|appStateStrings
init|=
name|reqStateString
operator|.
name|split
argument_list|(
literal|","
argument_list|)
decl_stmt|;
name|reqAppStates
operator|=
operator|new
name|HashSet
argument_list|<
name|YarnApplicationState
argument_list|>
argument_list|(
name|appStateStrings
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|stateString
range|:
name|appStateStrings
control|)
block|{
name|reqAppStates
operator|.
name|add
argument_list|(
name|YarnApplicationState
operator|.
name|valueOf
argument_list|(
name|stateString
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
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
name|RMApp
name|app
range|:
name|apps
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|reqAppStates
operator|!=
literal|null
operator|&&
operator|!
name|reqAppStates
operator|.
name|contains
argument_list|(
name|app
operator|.
name|createApplicationState
argument_list|()
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|AppInfo
name|appInfo
init|=
operator|new
name|AppInfo
argument_list|(
name|app
argument_list|,
literal|true
argument_list|,
name|WebAppUtils
operator|.
name|getHttpSchemePrefix
argument_list|(
name|conf
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|percent
init|=
name|String
operator|.
name|format
argument_list|(
literal|"%.1f"
argument_list|,
name|appInfo
operator|.
name|getProgress
argument_list|()
argument_list|)
decl_stmt|;
name|ApplicationAttemptId
name|attemptId
init|=
name|app
operator|.
name|getCurrentAppAttempt
argument_list|()
operator|.
name|getAppAttemptId
argument_list|()
decl_stmt|;
name|int
name|fairShare
init|=
name|fsinfo
operator|.
name|getAppFairShare
argument_list|(
name|attemptId
argument_list|)
decl_stmt|;
comment|//AppID numerical value parsed by parseHadoopID in yarn.dt.plugins.js
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
name|appInfo
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
name|appInfo
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
name|appInfo
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
name|appInfo
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
name|appInfo
operator|.
name|getApplicationType
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
name|appInfo
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
name|fairShare
argument_list|)
operator|.
name|append
argument_list|(
literal|"\",\""
argument_list|)
operator|.
name|append
argument_list|(
name|appInfo
operator|.
name|getStartTime
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
name|appInfo
operator|.
name|getFinishTime
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
name|appInfo
operator|.
name|getState
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
name|appInfo
operator|.
name|getFinalStatus
argument_list|()
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
literal|"\",\"<a href='"
argument_list|)
expr_stmt|;
name|String
name|trackingURL
init|=
operator|!
name|appInfo
operator|.
name|isTrackingUrlReady
argument_list|()
condition|?
literal|"#"
else|:
name|appInfo
operator|.
name|getTrackingUrlPretty
argument_list|()
decl_stmt|;
name|appsTableData
operator|.
name|append
argument_list|(
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
name|appInfo
operator|.
name|getTrackingUI
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"</a>\"],\n"
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
name|_
argument_list|(
literal|"var appsTableData="
operator|+
name|appsTableData
argument_list|)
operator|.
name|_
argument_list|()
expr_stmt|;
name|tbody
operator|.
name|_
argument_list|()
operator|.
name|_
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

