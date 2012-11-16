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
name|view
operator|.
name|TwoColumnLayout
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
name|sjoin
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
name|*
import|;
end_import

begin_comment
comment|// Do NOT rename/refactor this to RMView as it will wreak havoc
end_comment

begin_comment
comment|// on Mac OS HFS
end_comment

begin_class
DECL|class|RmView
specifier|public
class|class
name|RmView
extends|extends
name|TwoColumnLayout
block|{
DECL|field|MAX_DISPLAY_ROWS
specifier|static
specifier|final
name|int
name|MAX_DISPLAY_ROWS
init|=
literal|100
decl_stmt|;
comment|// direct table rendering
DECL|field|MAX_FAST_ROWS
specifier|static
specifier|final
name|int
name|MAX_FAST_ROWS
init|=
literal|1000
decl_stmt|;
comment|// inline js array
annotation|@
name|Override
DECL|method|preHead (Page.HTML<_> html)
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
name|set
argument_list|(
name|DATATABLES_ID
argument_list|,
literal|"apps"
argument_list|)
expr_stmt|;
name|set
argument_list|(
name|initID
argument_list|(
name|DATATABLES
argument_list|,
literal|"apps"
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
literal|"apps"
argument_list|,
literal|".queue {width:6em}"
argument_list|,
literal|".ui {width:8em}"
argument_list|)
expr_stmt|;
comment|// Set the correct title.
name|String
name|reqState
init|=
name|$
argument_list|(
name|APP_STATE
argument_list|)
decl_stmt|;
name|reqState
operator|=
operator|(
name|reqState
operator|==
literal|null
operator|||
name|reqState
operator|.
name|isEmpty
argument_list|()
condition|?
literal|"All"
else|:
name|reqState
operator|)
expr_stmt|;
name|setTitle
argument_list|(
name|sjoin
argument_list|(
name|reqState
argument_list|,
literal|"Applications"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|commonPreHead (Page.HTML<_> html)
specifier|protected
name|void
name|commonPreHead
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
name|set
argument_list|(
name|ACCORDION_ID
argument_list|,
literal|"nav"
argument_list|)
expr_stmt|;
name|set
argument_list|(
name|initID
argument_list|(
name|ACCORDION
argument_list|,
literal|"nav"
argument_list|)
argument_list|,
literal|"{autoHeight:false, active:0}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|nav ()
specifier|protected
name|Class
argument_list|<
name|?
extends|extends
name|SubView
argument_list|>
name|nav
parameter_list|()
block|{
return|return
name|NavBlock
operator|.
name|class
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
name|AppsBlockWithMetrics
operator|.
name|class
return|;
block|}
DECL|method|appsTableInit ()
specifier|private
name|String
name|appsTableInit
parameter_list|()
block|{
name|AppsList
name|list
init|=
name|getInstance
argument_list|(
name|AppsList
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// id, user, name, queue, starttime, finishtime, state, status, progress, ui
name|StringBuilder
name|init
init|=
name|tableInit
argument_list|()
operator|.
name|append
argument_list|(
literal|", 'aaData': appsTableData"
argument_list|)
operator|.
name|append
argument_list|(
literal|", bDeferRender: true"
argument_list|)
operator|.
name|append
argument_list|(
literal|", bProcessing: true"
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n, aoColumnDefs: [\n"
argument_list|)
operator|.
name|append
argument_list|(
literal|"{'sType':'numeric', 'aTargets': [0]"
argument_list|)
operator|.
name|append
argument_list|(
literal|", 'mRender': parseHadoopID }"
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n, {'sType':'numeric', 'aTargets': [4, 5]"
argument_list|)
operator|.
name|append
argument_list|(
literal|", 'mRender': renderHadoopDate }"
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n, {'sType':'numeric', bSearchable:false, 'aTargets': [8]"
argument_list|)
operator|.
name|append
argument_list|(
literal|", 'mRender': parseHadoopProgress }]"
argument_list|)
comment|// Sort by id upon page load
operator|.
name|append
argument_list|(
literal|", aaSorting: [[0, 'desc']]"
argument_list|)
decl_stmt|;
name|String
name|rows
init|=
name|$
argument_list|(
literal|"rowlimit"
argument_list|)
decl_stmt|;
name|int
name|rowLimit
init|=
name|rows
operator|.
name|isEmpty
argument_list|()
condition|?
name|MAX_DISPLAY_ROWS
else|:
name|Integer
operator|.
name|parseInt
argument_list|(
name|rows
argument_list|)
decl_stmt|;
if|if
condition|(
name|list
operator|.
name|apps
operator|.
name|size
argument_list|()
operator|<
name|rowLimit
condition|)
block|{
name|list
operator|.
name|rendering
operator|=
name|Render
operator|.
name|HTML
expr_stmt|;
return|return
name|init
operator|.
name|append
argument_list|(
literal|'}'
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
if|if
condition|(
name|list
operator|.
name|apps
operator|.
name|size
argument_list|()
operator|>
name|MAX_FAST_ROWS
condition|)
block|{
name|tableInitProgress
argument_list|(
name|init
argument_list|,
name|list
operator|.
name|apps
operator|.
name|size
argument_list|()
operator|*
literal|6
argument_list|)
expr_stmt|;
block|}
name|list
operator|.
name|rendering
operator|=
name|Render
operator|.
name|JS_ARRAY
expr_stmt|;
return|return
name|init
operator|.
name|append
argument_list|(
literal|", aaData:appsData}"
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

