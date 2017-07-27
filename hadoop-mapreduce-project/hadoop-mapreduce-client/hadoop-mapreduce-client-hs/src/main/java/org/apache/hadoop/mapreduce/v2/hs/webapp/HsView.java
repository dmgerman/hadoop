begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|webapp
operator|.
name|AMParams
operator|.
name|TASK_ID
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
name|ACCORDION
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
name|ACCORDION_ID
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
name|postInitID
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

begin_comment
comment|/**  * A view that should be used as the base class for all history server pages.  */
end_comment

begin_class
DECL|class|HsView
specifier|public
class|class
name|HsView
extends|extends
name|TwoColumnLayout
block|{
comment|/*    * (non-Javadoc)    * @see org.apache.hadoop.yarn.webapp.view.TwoColumnLayout#preHead(org.apache.hadoop.yarn.webapp.hamlet.Hamlet.HTML)    */
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
name|set
argument_list|(
name|DATATABLES_ID
argument_list|,
literal|"jobs"
argument_list|)
expr_stmt|;
name|set
argument_list|(
name|initID
argument_list|(
name|DATATABLES
argument_list|,
literal|"jobs"
argument_list|)
argument_list|,
name|jobsTableInit
argument_list|()
argument_list|)
expr_stmt|;
name|set
argument_list|(
name|postInitID
argument_list|(
name|DATATABLES
argument_list|,
literal|"jobs"
argument_list|)
argument_list|,
name|jobsPostTableInit
argument_list|()
argument_list|)
expr_stmt|;
name|setTableStyles
argument_list|(
name|html
argument_list|,
literal|"jobs"
argument_list|)
expr_stmt|;
block|}
comment|/**    * The prehead that should be common to all subclasses.    * @param html used to render.    */
DECL|method|commonPreHead (Page.HTML<__> html)
specifier|protected
name|void
name|commonPreHead
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
comment|/**    * Determine which navigation column is active.    */
DECL|method|setActiveNavColumnForTask ()
specifier|protected
name|void
name|setActiveNavColumnForTask
parameter_list|()
block|{
name|String
name|tid
init|=
name|$
argument_list|(
name|TASK_ID
argument_list|)
decl_stmt|;
name|String
name|activeNav
init|=
literal|"2"
decl_stmt|;
if|if
condition|(
operator|(
name|tid
operator|==
literal|null
operator|||
name|tid
operator|.
name|isEmpty
argument_list|()
operator|)
condition|)
block|{
name|activeNav
operator|=
literal|"1"
expr_stmt|;
block|}
name|set
argument_list|(
name|initID
argument_list|(
name|ACCORDION
argument_list|,
literal|"nav"
argument_list|)
argument_list|,
literal|"{autoHeight:false, active:"
operator|+
name|activeNav
operator|+
literal|"}"
argument_list|)
expr_stmt|;
block|}
comment|/*    * (non-Javadoc)    * @see org.apache.hadoop.yarn.webapp.view.TwoColumnLayout#nav()    */
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
name|HsNavBlock
operator|.
name|class
return|;
block|}
comment|/*    * (non-Javadoc)    * @see org.apache.hadoop.yarn.webapp.view.TwoColumnLayout#content()    */
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
name|HsJobsBlock
operator|.
name|class
return|;
block|}
comment|//TODO We need a way to move all of the javascript/CSS that is for a subview
comment|// into that subview.
comment|/**    * @return The end of a javascript map that is the jquery datatable     * configuration for the jobs table.  the Jobs table is assumed to be    * rendered by the class returned from {@link #content()}     */
DECL|method|jobsTableInit ()
specifier|private
name|String
name|jobsTableInit
parameter_list|()
block|{
return|return
name|tableInit
argument_list|()
operator|.
name|append
argument_list|(
literal|", 'aaData': jobsTableData"
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
comment|// Sort by id upon page load
name|append
argument_list|(
literal|", aaSorting: [[3, 'desc']]"
argument_list|)
operator|.
name|append
argument_list|(
literal|", aoColumnDefs:["
argument_list|)
operator|.
comment|// Maps Total, Maps Completed, Reduces Total and Reduces Completed
name|append
argument_list|(
literal|"{'sType':'numeric', 'bSearchable': false"
operator|+
literal|", 'aTargets': [ 8, 9, 10, 11 ] }"
argument_list|)
operator|.
name|append
argument_list|(
literal|"]}"
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * @return javascript to add into the jquery block after the table has    *  been initialized. This code adds in per field filtering.    */
DECL|method|jobsPostTableInit ()
specifier|private
name|String
name|jobsPostTableInit
parameter_list|()
block|{
return|return
literal|"var asInitVals = new Array();\n"
operator|+
literal|"$('tfoot input').keyup( function () \n{"
operator|+
literal|"  jobsDataTable.fnFilter( this.value, $('tfoot input').index(this) );\n"
operator|+
literal|"} );\n"
operator|+
literal|"$('tfoot input').each( function (i) {\n"
operator|+
literal|"  asInitVals[i] = this.value;\n"
operator|+
literal|"} );\n"
operator|+
literal|"$('tfoot input').focus( function () {\n"
operator|+
literal|"  if ( this.className == 'search_init' )\n"
operator|+
literal|"  {\n"
operator|+
literal|"    this.className = '';\n"
operator|+
literal|"    this.value = '';\n"
operator|+
literal|"  }\n"
operator|+
literal|"} );\n"
operator|+
literal|"$('tfoot input').blur( function (i) {\n"
operator|+
literal|"  if ( this.value == '' )\n"
operator|+
literal|"  {\n"
operator|+
literal|"    this.className = 'search_init';\n"
operator|+
literal|"    this.value = asInitVals[$('tfoot input').index(this)];\n"
operator|+
literal|"  }\n"
operator|+
literal|"} );\n"
return|;
block|}
block|}
end_class

end_unit

