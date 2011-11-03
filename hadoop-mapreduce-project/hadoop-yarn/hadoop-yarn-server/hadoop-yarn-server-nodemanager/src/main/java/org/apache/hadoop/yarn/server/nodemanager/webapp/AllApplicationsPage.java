begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.webapp
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
name|nodemanager
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
name|webapp
operator|.
name|view
operator|.
name|JQueryUI
operator|.
name|DATATABLES
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
name|DATATABLES_ID
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
name|initID
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
name|tableInit
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
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
name|nodemanager
operator|.
name|Context
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
name|nodemanager
operator|.
name|containermanager
operator|.
name|application
operator|.
name|Application
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
name|ConverterUtils
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
name|YarnWebParams
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
name|SubView
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
name|BODY
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
DECL|class|AllApplicationsPage
specifier|public
class|class
name|AllApplicationsPage
extends|extends
name|NMView
block|{
DECL|method|preHead (Page.HTML<_> html)
annotation|@
name|Override
specifier|protected
name|void
name|preHead
parameter_list|(
name|Page
operator|.
name|HTML
argument_list|<
name|_
argument_list|>
name|html
parameter_list|)
block|{
name|commonPreHead
argument_list|(
name|html
argument_list|)
expr_stmt|;
name|setTitle
argument_list|(
literal|"Applications running on this node"
argument_list|)
expr_stmt|;
name|set
argument_list|(
name|DATATABLES_ID
argument_list|,
literal|"applications"
argument_list|)
expr_stmt|;
name|set
argument_list|(
name|initID
argument_list|(
name|DATATABLES
argument_list|,
literal|"applications"
argument_list|)
argument_list|,
name|appsTableInit
argument_list|()
argument_list|)
expr_stmt|;
name|setTableStyles
argument_list|(
name|html
argument_list|,
literal|"applications"
argument_list|)
expr_stmt|;
block|}
DECL|method|appsTableInit ()
specifier|private
name|String
name|appsTableInit
parameter_list|()
block|{
return|return
name|tableInit
argument_list|()
operator|.
comment|// applicationid, applicationstate
name|append
argument_list|(
literal|", aoColumns:[null, null]} "
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|content ()
specifier|protected
name|Class
argument_list|<
name|?
extends|extends
name|SubView
argument_list|>
name|content
parameter_list|()
block|{
return|return
name|AllApplicationsBlock
operator|.
name|class
return|;
block|}
DECL|class|AllApplicationsBlock
specifier|public
specifier|static
class|class
name|AllApplicationsBlock
extends|extends
name|HtmlBlock
implements|implements
name|YarnWebParams
block|{
DECL|field|nmContext
specifier|private
specifier|final
name|Context
name|nmContext
decl_stmt|;
annotation|@
name|Inject
DECL|method|AllApplicationsBlock (Context nmContext)
specifier|public
name|AllApplicationsBlock
parameter_list|(
name|Context
name|nmContext
parameter_list|)
block|{
name|this
operator|.
name|nmContext
operator|=
name|nmContext
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
name|TBODY
argument_list|<
name|TABLE
argument_list|<
name|BODY
argument_list|<
name|Hamlet
argument_list|>
argument_list|>
argument_list|>
name|tableBody
init|=
name|html
operator|.
name|body
argument_list|()
operator|.
name|table
argument_list|(
literal|"#applications"
argument_list|)
operator|.
name|thead
argument_list|()
operator|.
name|tr
argument_list|()
operator|.
name|td
argument_list|()
operator|.
name|_
argument_list|(
literal|"ApplicationId"
argument_list|)
operator|.
name|_
argument_list|()
operator|.
name|td
argument_list|()
operator|.
name|_
argument_list|(
literal|"ApplicationState"
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
name|tbody
argument_list|()
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|ApplicationId
argument_list|,
name|Application
argument_list|>
name|entry
range|:
name|this
operator|.
name|nmContext
operator|.
name|getApplications
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|ApplicationId
name|appId
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|Application
name|app
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|String
name|appIdStr
init|=
name|ConverterUtils
operator|.
name|toString
argument_list|(
name|appId
argument_list|)
decl_stmt|;
name|tableBody
operator|.
name|tr
argument_list|()
operator|.
name|td
argument_list|()
operator|.
name|a
argument_list|(
name|url
argument_list|(
literal|"application"
argument_list|,
name|appIdStr
argument_list|)
argument_list|,
name|appIdStr
argument_list|)
operator|.
name|_
argument_list|()
operator|.
name|td
argument_list|()
operator|.
name|_
argument_list|(
name|app
operator|.
name|getApplicationState
argument_list|()
argument_list|)
operator|.
name|_
argument_list|()
operator|.
name|_
argument_list|()
expr_stmt|;
block|}
name|tableBody
operator|.
name|_
argument_list|()
operator|.
name|_
argument_list|()
operator|.
name|_
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

