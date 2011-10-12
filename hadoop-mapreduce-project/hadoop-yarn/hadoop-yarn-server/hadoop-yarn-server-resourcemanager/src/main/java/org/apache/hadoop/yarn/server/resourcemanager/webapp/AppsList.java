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
name|commons
operator|.
name|lang
operator|.
name|StringEscapeUtils
operator|.
name|escapeHtml
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
name|lang
operator|.
name|StringEscapeUtils
operator|.
name|escapeJavaScript
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
name|Jsons
operator|.
name|_SEP
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
name|Jsons
operator|.
name|appendLink
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
name|Jsons
operator|.
name|appendProgressBar
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
name|Jsons
operator|.
name|appendSortable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
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
name|Controller
operator|.
name|RequestContext
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
name|ToJSON
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

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|servlet
operator|.
name|RequestScoped
import|;
end_import

begin_comment
comment|// So we only need to do asm.getApplications once in a request
end_comment

begin_class
annotation|@
name|RequestScoped
DECL|class|AppsList
class|class
name|AppsList
implements|implements
name|ToJSON
block|{
DECL|field|rc
specifier|final
name|RequestContext
name|rc
decl_stmt|;
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
DECL|field|rendering
name|Render
name|rendering
decl_stmt|;
DECL|method|AppsList (RequestContext ctx, RMContext rmContext)
annotation|@
name|Inject
name|AppsList
parameter_list|(
name|RequestContext
name|ctx
parameter_list|,
name|RMContext
name|rmContext
parameter_list|)
block|{
name|rc
operator|=
name|ctx
expr_stmt|;
name|apps
operator|=
name|rmContext
operator|.
name|getRMApps
argument_list|()
expr_stmt|;
block|}
DECL|method|toDataTableArrays (PrintWriter out)
name|void
name|toDataTableArrays
parameter_list|(
name|PrintWriter
name|out
parameter_list|)
block|{
name|out
operator|.
name|append
argument_list|(
literal|'['
argument_list|)
expr_stmt|;
name|boolean
name|first
init|=
literal|true
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
name|first
condition|)
block|{
name|first
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|append
argument_list|(
literal|",\n"
argument_list|)
expr_stmt|;
block|}
name|String
name|appID
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
name|boolean
name|trackingUrlIsNotReady
init|=
name|trackingUrl
operator|==
literal|null
operator|||
name|trackingUrl
operator|.
name|isEmpty
argument_list|()
operator|||
literal|"N/A"
operator|.
name|equalsIgnoreCase
argument_list|(
name|trackingUrl
argument_list|)
decl_stmt|;
name|String
name|ui
init|=
name|trackingUrlIsNotReady
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
literal|"ApplicationMaster"
else|:
literal|"History"
operator|)
decl_stmt|;
name|out
operator|.
name|append
argument_list|(
literal|"[\""
argument_list|)
expr_stmt|;
name|appendSortable
argument_list|(
name|out
argument_list|,
name|app
operator|.
name|getApplicationId
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|appendLink
argument_list|(
name|out
argument_list|,
name|appID
argument_list|,
name|rc
operator|.
name|prefix
argument_list|()
argument_list|,
literal|"app"
argument_list|,
name|appID
argument_list|)
operator|.
name|append
argument_list|(
name|_SEP
argument_list|)
operator|.
name|append
argument_list|(
name|escapeHtml
argument_list|(
name|app
operator|.
name|getUser
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
name|_SEP
argument_list|)
operator|.
name|append
argument_list|(
name|escapeHtml
argument_list|(
name|app
operator|.
name|getName
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
name|_SEP
argument_list|)
operator|.
name|append
argument_list|(
name|escapeHtml
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
name|_SEP
argument_list|)
operator|.
name|append
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
name|append
argument_list|(
name|_SEP
argument_list|)
expr_stmt|;
name|appendProgressBar
argument_list|(
name|out
argument_list|,
name|app
operator|.
name|getProgress
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
name|_SEP
argument_list|)
expr_stmt|;
name|appendLink
argument_list|(
name|out
argument_list|,
name|ui
argument_list|,
name|rc
operator|.
name|prefix
argument_list|()
argument_list|,
name|trackingUrlIsNotReady
condition|?
literal|"#"
else|:
literal|"http://"
argument_list|,
name|trackingUrl
argument_list|)
operator|.
name|append
argument_list|(
name|_SEP
argument_list|)
operator|.
name|append
argument_list|(
name|escapeJavaScript
argument_list|(
name|escapeHtml
argument_list|(
name|app
operator|.
name|getDiagnostics
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"\"]"
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|append
argument_list|(
literal|']'
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toJSON (PrintWriter out)
specifier|public
name|void
name|toJSON
parameter_list|(
name|PrintWriter
name|out
parameter_list|)
block|{
name|out
operator|.
name|print
argument_list|(
literal|"{\"aaData\":"
argument_list|)
expr_stmt|;
name|toDataTableArrays
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
literal|"}\n"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

