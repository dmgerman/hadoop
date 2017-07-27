begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.hs.webapp
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
operator|.
name|hs
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
name|*
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
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|webapp
operator|.
name|CountersBlock
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
comment|/**  * Render the counters page  */
end_comment

begin_class
DECL|class|HsCountersPage
specifier|public
class|class
name|HsCountersPage
extends|extends
name|HsView
block|{
comment|/*    * (non-Javadoc)    * @see org.apache.hadoop.mapreduce.v2.hs.webapp.HsView#preHead(org.apache.hadoop.yarn.webapp.hamlet.Hamlet.HTML)    */
DECL|method|preHead (Page.HTML<__> html)
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
name|setActiveNavColumnForTask
argument_list|()
expr_stmt|;
name|set
argument_list|(
name|DATATABLES_SELECTOR
argument_list|,
literal|"#counters .dt-counters"
argument_list|)
expr_stmt|;
name|set
argument_list|(
name|initSelector
argument_list|(
name|DATATABLES
argument_list|)
argument_list|,
literal|"{bJQueryUI:true, sDom:'t', iDisplayLength:-1}"
argument_list|)
expr_stmt|;
block|}
comment|/*    * (non-Javadoc)    * @see org.apache.hadoop.yarn.webapp.view.TwoColumnLayout#postHead(org.apache.hadoop.yarn.webapp.hamlet.Hamlet.HTML)    */
DECL|method|postHead (Page.HTML<__> html)
annotation|@
name|Override
specifier|protected
name|void
name|postHead
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
name|html
operator|.
name|style
argument_list|(
literal|"#counters, .dt-counters { table-layout: fixed }"
argument_list|,
literal|"#counters th { overflow: hidden; vertical-align: middle }"
argument_list|,
literal|"#counters .dataTables_wrapper { min-height: 1em }"
argument_list|,
literal|"#counters .group { width: 15em }"
argument_list|,
literal|"#counters .name { width: 30em }"
argument_list|)
expr_stmt|;
block|}
comment|/**    * The content of this page is the CountersBlock now.    * @return CountersBlock.class    */
DECL|method|content ()
annotation|@
name|Override
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
name|CountersBlock
operator|.
name|class
return|;
block|}
block|}
end_class

end_unit

