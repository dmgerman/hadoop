begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.webapp
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
name|security
operator|.
name|PrivilegedExceptionAction
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
name|EnumSet
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
name|ApplicationBaseProtocol
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

begin_class
DECL|class|AppsBlock
specifier|public
class|class
name|AppsBlock
extends|extends
name|HtmlBlock
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
name|AppsBlock
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|appBaseProt
specifier|protected
name|ApplicationBaseProtocol
name|appBaseProt
decl_stmt|;
annotation|@
name|Inject
DECL|method|AppsBlock (ApplicationBaseProtocol appBaseProt, ViewContext ctx)
name|AppsBlock
parameter_list|(
name|ApplicationBaseProtocol
name|appBaseProt
parameter_list|,
name|ViewContext
name|ctx
parameter_list|)
block|{
name|super
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
name|this
operator|.
name|appBaseProt
operator|=
name|appBaseProt
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|render (Block html)
specifier|public
name|void
name|render
parameter_list|(
name|Block
name|html
parameter_list|)
block|{
name|setTitle
argument_list|(
literal|"Applications"
argument_list|)
expr_stmt|;
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
name|EnumSet
argument_list|<
name|YarnApplicationState
argument_list|>
name|reqAppStates
init|=
name|EnumSet
operator|.
name|noneOf
argument_list|(
name|YarnApplicationState
operator|.
name|class
argument_list|)
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
operator|.
name|trim
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|UserGroupInformation
name|callerUGI
init|=
name|getCallerUGI
argument_list|()
decl_stmt|;
name|Collection
argument_list|<
name|ApplicationReport
argument_list|>
name|appReports
init|=
literal|null
decl_stmt|;
try|try
block|{
specifier|final
name|GetApplicationsRequest
name|request
init|=
name|GetApplicationsRequest
operator|.
name|newInstance
argument_list|(
name|reqAppStates
argument_list|)
decl_stmt|;
if|if
condition|(
name|callerUGI
operator|==
literal|null
condition|)
block|{
name|appReports
operator|=
name|appBaseProt
operator|.
name|getApplications
argument_list|(
name|request
argument_list|)
operator|.
name|getApplicationList
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|appReports
operator|=
name|callerUGI
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|Collection
argument_list|<
name|ApplicationReport
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Collection
argument_list|<
name|ApplicationReport
argument_list|>
name|run
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|appBaseProt
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
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|String
name|message
init|=
literal|"Failed to read the applications."
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|message
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|html
operator|.
name|p
argument_list|()
operator|.
name|_
argument_list|(
name|message
argument_list|)
operator|.
name|_
argument_list|()
expr_stmt|;
return|return;
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
name|String
name|percent
init|=
name|String
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
comment|// AppID numerical value parsed by parseHadoopID in yarn.dt.plugins.js
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
operator|==
name|UNAVAILABLE
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
operator|==
name|UNAVAILABLE
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

