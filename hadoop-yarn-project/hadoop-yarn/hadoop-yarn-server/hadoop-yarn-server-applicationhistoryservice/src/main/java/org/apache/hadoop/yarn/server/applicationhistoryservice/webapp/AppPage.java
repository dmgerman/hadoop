begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.applicationhistoryservice.webapp
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
name|applicationhistoryservice
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
name|AppBlock
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
name|WebPageUtils
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
name|YarnWebParams
import|;
end_import

begin_class
DECL|class|AppPage
specifier|public
class|class
name|AppPage
extends|extends
name|AHSView
block|{
annotation|@
name|Override
DECL|method|preHead (Page.HTML<__> html)
specifier|protected
name|void
name|preHead
parameter_list|(
name|Page
operator|.
name|HTML
argument_list|<
name|__
argument_list|>
name|html
parameter_list|)
block|{
name|commonPreHead
argument_list|(
name|html
argument_list|)
expr_stmt|;
name|String
name|appId
init|=
name|$
argument_list|(
name|YarnWebParams
operator|.
name|APPLICATION_ID
argument_list|)
decl_stmt|;
name|set
argument_list|(
name|TITLE
argument_list|,
name|appId
operator|.
name|isEmpty
argument_list|()
condition|?
literal|"Bad request: missing application ID"
else|:
name|join
argument_list|(
literal|"Application "
argument_list|,
name|$
argument_list|(
name|YarnWebParams
operator|.
name|APPLICATION_ID
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|set
argument_list|(
name|DATATABLES_ID
argument_list|,
literal|"attempts ResourceRequests"
argument_list|)
expr_stmt|;
name|set
argument_list|(
name|initID
argument_list|(
name|DATATABLES
argument_list|,
literal|"attempts"
argument_list|)
argument_list|,
name|WebPageUtils
operator|.
name|attemptsTableInit
argument_list|()
argument_list|)
expr_stmt|;
name|setTableStyles
argument_list|(
name|html
argument_list|,
literal|"attempts"
argument_list|,
literal|".queue {width:6em}"
argument_list|,
literal|".ui {width:8em}"
argument_list|)
expr_stmt|;
name|setTableStyles
argument_list|(
name|html
argument_list|,
literal|"ResourceRequests"
argument_list|)
expr_stmt|;
name|set
argument_list|(
name|YarnWebParams
operator|.
name|WEB_UI_TYPE
argument_list|,
name|YarnWebParams
operator|.
name|APP_HISTORY_WEB_UI
argument_list|)
expr_stmt|;
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
name|AppBlock
operator|.
name|class
return|;
block|}
DECL|method|getAttemptsTableColumnDefs ()
specifier|protected
name|String
name|getAttemptsTableColumnDefs
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
return|return
name|sb
operator|.
name|append
argument_list|(
literal|"[\n"
argument_list|)
operator|.
name|append
argument_list|(
literal|"{'sType':'natural', 'aTargets': [0]"
argument_list|)
operator|.
name|append
argument_list|(
literal|", 'mRender': parseHadoopID }"
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n, {'sType':'numeric', 'aTargets': [1]"
argument_list|)
operator|.
name|append
argument_list|(
literal|", 'mRender': renderHadoopDate }]"
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

