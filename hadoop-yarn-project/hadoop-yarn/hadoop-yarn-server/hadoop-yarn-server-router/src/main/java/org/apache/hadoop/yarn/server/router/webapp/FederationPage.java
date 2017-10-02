begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
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

begin_comment
comment|/**  * Renders a block for the applications with metrics information.  */
end_comment

begin_class
DECL|class|FederationPage
class|class
name|FederationPage
extends|extends
name|RouterView
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
name|setTitle
argument_list|(
literal|"Federation"
argument_list|)
expr_stmt|;
name|set
argument_list|(
name|DATATABLES_ID
argument_list|,
literal|"rms"
argument_list|)
expr_stmt|;
name|set
argument_list|(
name|initID
argument_list|(
name|DATATABLES
argument_list|,
literal|"rms"
argument_list|)
argument_list|,
name|rmsTableInit
argument_list|()
argument_list|)
expr_stmt|;
name|setTableStyles
argument_list|(
name|html
argument_list|,
literal|"rms"
argument_list|,
literal|".healthStatus {width:10em}"
argument_list|,
literal|".healthReport {width:10em}"
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
name|FederationBlock
operator|.
name|class
return|;
block|}
DECL|method|rmsTableInit ()
specifier|private
name|String
name|rmsTableInit
parameter_list|()
block|{
name|StringBuilder
name|b
init|=
name|tableInit
argument_list|()
operator|.
name|append
argument_list|(
literal|", aoColumnDefs: ["
argument_list|)
decl_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"{'bSearchable': false, 'aTargets': [ 7 ]}"
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|", {'sType': 'title-numeric', 'bSearchable': false, "
operator|+
literal|"'aTargets': [ 8, 9 ] }"
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|", {'sType': 'title-numeric', 'aTargets': [ 5 ]}"
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"]}"
argument_list|)
expr_stmt|;
return|return
name|b
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

