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
name|view
operator|.
name|JQueryUI
operator|.
name|_PROGRESSBAR
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
name|_PROGRESSBAR_VALUE
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
name|Render
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
class|class
name|AppsBlock
extends|extends
name|HtmlBlock
block|{
DECL|field|list
specifier|final
name|AppsList
name|list
decl_stmt|;
DECL|method|AppsBlock (AppsList list, ViewContext ctx)
annotation|@
name|Inject
name|AppsBlock
parameter_list|(
name|AppsList
name|list
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
name|list
operator|=
name|list
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
literal|".queue"
argument_list|,
literal|"Queue"
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
literal|".note"
argument_list|,
literal|"Note"
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
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|RMApp
name|app
range|:
name|list
operator|.
name|apps
operator|.
name|values
argument_list|()
control|)
block|{
name|String
name|appId
init|=
name|app
operator|.
name|getApplicationId
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|String
name|trackingUrl
init|=
name|app
operator|.
name|getTrackingUrl
argument_list|()
decl_stmt|;
name|String
name|ui
init|=
name|trackingUrl
operator|==
literal|null
operator|||
name|trackingUrl
operator|.
name|isEmpty
argument_list|()
condition|?
literal|"UNASSIGNED"
else|:
operator|(
name|app
operator|.
name|getFinishTime
argument_list|()
operator|==
literal|0
condition|?
literal|"ApplicationMaster URL"
else|:
literal|"JobHistory URL"
operator|)
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
operator|*
literal|100
argument_list|)
decl_stmt|;
name|tbody
operator|.
name|tr
argument_list|()
operator|.
name|td
argument_list|()
operator|.
name|br
argument_list|()
operator|.
name|$title
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|app
operator|.
name|getApplicationId
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
operator|.
name|_
argument_list|()
operator|.
comment|// for sorting
name|a
argument_list|(
name|url
argument_list|(
literal|"app"
argument_list|,
name|appId
argument_list|)
argument_list|,
name|appId
argument_list|)
operator|.
name|_
argument_list|()
operator|.
name|td
argument_list|(
name|app
operator|.
name|getUser
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|td
argument_list|(
name|app
operator|.
name|getName
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|td
argument_list|(
name|app
operator|.
name|getQueue
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|td
argument_list|(
name|app
operator|.
name|getState
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|td
argument_list|()
operator|.
name|br
argument_list|()
operator|.
name|$title
argument_list|(
name|percent
argument_list|)
operator|.
name|_
argument_list|()
operator|.
comment|// for sorting
name|div
argument_list|(
name|_PROGRESSBAR
argument_list|)
operator|.
name|$title
argument_list|(
name|join
argument_list|(
name|percent
argument_list|,
literal|'%'
argument_list|)
argument_list|)
operator|.
comment|// tooltip
name|div
argument_list|(
name|_PROGRESSBAR_VALUE
argument_list|)
operator|.
name|$style
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
name|_
argument_list|()
operator|.
name|_
argument_list|()
operator|.
name|_
argument_list|()
operator|.
name|td
argument_list|()
operator|.
name|a
argument_list|(
name|trackingUrl
operator|==
literal|null
condition|?
literal|"#"
else|:
name|join
argument_list|(
literal|"http://"
argument_list|,
name|trackingUrl
argument_list|)
argument_list|,
name|ui
argument_list|)
operator|.
name|_
argument_list|()
operator|.
name|td
argument_list|(
name|app
operator|.
name|getDiagnostics
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|_
argument_list|()
expr_stmt|;
if|if
condition|(
name|list
operator|.
name|rendering
operator|!=
name|Render
operator|.
name|HTML
operator|&&
operator|++
name|i
operator|>=
literal|20
condition|)
break|break;
block|}
name|tbody
operator|.
name|_
argument_list|()
operator|.
name|_
argument_list|()
expr_stmt|;
if|if
condition|(
name|list
operator|.
name|rendering
operator|==
name|Render
operator|.
name|JS_ARRAY
condition|)
block|{
name|echo
argument_list|(
literal|"<script type='text/javascript'>\n"
argument_list|,
literal|"var appsData="
argument_list|)
expr_stmt|;
name|list
operator|.
name|toDataTableArrays
argument_list|(
name|writer
argument_list|()
argument_list|)
expr_stmt|;
name|echo
argument_list|(
literal|"\n</script>\n"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

