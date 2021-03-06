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
import|import static
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|text
operator|.
name|StringEscapeUtils
operator|.
name|escapeHtml4
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|text
operator|.
name|StringEscapeUtils
operator|.
name|escapeEcmaScript
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
name|server
operator|.
name|resourcemanager
operator|.
name|webapp
operator|.
name|RMWSConsts
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
name|router
operator|.
name|Router
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
comment|/**  * Applications block for the Router Web UI.  */
end_comment

begin_class
DECL|class|AppsBlock
specifier|public
class|class
name|AppsBlock
extends|extends
name|HtmlBlock
block|{
DECL|field|router
specifier|private
specifier|final
name|Router
name|router
decl_stmt|;
annotation|@
name|Inject
DECL|method|AppsBlock (Router router, ViewContext ctx)
name|AppsBlock
parameter_list|(
name|Router
name|router
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
name|router
operator|=
name|router
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|render (Block html)
specifier|protected
name|void
name|render
parameter_list|(
name|Block
name|html
parameter_list|)
block|{
comment|// Get the applications from the Resource Managers
name|Configuration
name|conf
init|=
name|this
operator|.
name|router
operator|.
name|getConfig
argument_list|()
decl_stmt|;
name|String
name|webAppAddress
init|=
name|WebAppUtils
operator|.
name|getRouterWebAppURLWithScheme
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|AppsInfo
name|apps
init|=
name|RouterWebServiceUtil
operator|.
name|genericForward
argument_list|(
name|webAppAddress
argument_list|,
literal|null
argument_list|,
name|AppsInfo
operator|.
name|class
argument_list|,
name|HTTPMethods
operator|.
name|GET
argument_list|,
name|RMWSConsts
operator|.
name|RM_WEB_SERVICE_PATH
operator|+
name|RMWSConsts
operator|.
name|APPS
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
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
name|__
argument_list|()
operator|.
name|__
argument_list|()
operator|.
name|tbody
argument_list|()
decl_stmt|;
comment|// Render the applications
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
name|AppInfo
name|app
range|:
name|apps
operator|.
name|getApps
argument_list|()
control|)
block|{
try|try
block|{
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
operator|*
literal|100.0F
argument_list|)
decl_stmt|;
name|String
name|trackingURL
init|=
name|app
operator|.
name|getTrackingUrl
argument_list|()
operator|==
literal|null
condition|?
literal|"#"
else|:
name|app
operator|.
name|getTrackingUrl
argument_list|()
decl_stmt|;
comment|// AppID numerical value parsed by parseHadoopID in yarn.dt.plugins.js
name|appsTableData
operator|.
name|append
argument_list|(
literal|"[\""
argument_list|)
operator|.
name|append
argument_list|(
literal|"<a href='"
argument_list|)
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
name|escape
argument_list|(
name|app
operator|.
name|getUser
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
name|escape
argument_list|(
name|app
operator|.
name|getName
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
name|escape
argument_list|(
name|app
operator|.
name|getApplicationType
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
name|escape
argument_list|(
name|app
operator|.
name|getQueue
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
name|app
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
name|app
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
name|app
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
comment|// History link
operator|.
name|append
argument_list|(
literal|"\",\"<a href='"
argument_list|)
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
literal|"History"
argument_list|)
operator|.
name|append
argument_list|(
literal|"</a>"
argument_list|)
expr_stmt|;
name|appsTableData
operator|.
name|append
argument_list|(
literal|"\"],\n"
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
name|info
argument_list|(
literal|"Cannot add application {}: {}"
argument_list|,
name|app
operator|.
name|getAppId
argument_list|()
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
DECL|method|escape (String str)
specifier|private
specifier|static
name|String
name|escape
parameter_list|(
name|String
name|str
parameter_list|)
block|{
return|return
name|escapeEcmaScript
argument_list|(
name|escapeHtml4
argument_list|(
name|str
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

