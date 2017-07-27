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
name|TASK_TYPE
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
name|DATATABLES_SELECTOR
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
name|initSelector
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
name|mapreduce
operator|.
name|v2
operator|.
name|api
operator|.
name|records
operator|.
name|TaskType
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
name|util
operator|.
name|MRApps
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
comment|/**  * A page showing the tasks for a given application.  */
end_comment

begin_class
DECL|class|HsTasksPage
specifier|public
class|class
name|HsTasksPage
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
name|set
argument_list|(
name|DATATABLES_ID
argument_list|,
literal|"tasks"
argument_list|)
expr_stmt|;
name|set
argument_list|(
name|DATATABLES_SELECTOR
argument_list|,
literal|".dt-tasks"
argument_list|)
expr_stmt|;
name|set
argument_list|(
name|initSelector
argument_list|(
name|DATATABLES
argument_list|)
argument_list|,
name|tasksTableInit
argument_list|()
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
literal|"{autoHeight:false, active:1}"
argument_list|)
expr_stmt|;
name|set
argument_list|(
name|initID
argument_list|(
name|DATATABLES
argument_list|,
literal|"tasks"
argument_list|)
argument_list|,
name|tasksTableInit
argument_list|()
argument_list|)
expr_stmt|;
name|set
argument_list|(
name|postInitID
argument_list|(
name|DATATABLES
argument_list|,
literal|"tasks"
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
literal|"tasks"
argument_list|)
expr_stmt|;
block|}
comment|/**    * The content of this page is the TasksBlock    * @return HsTasksBlock.class    */
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
name|HsTasksBlock
operator|.
name|class
return|;
block|}
comment|/**    * @return the end of the JS map that is the jquery datatable configuration    * for the tasks table.    */
DECL|method|tasksTableInit ()
specifier|private
name|String
name|tasksTableInit
parameter_list|()
block|{
name|TaskType
name|type
init|=
literal|null
decl_stmt|;
name|String
name|symbol
init|=
name|$
argument_list|(
name|TASK_TYPE
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|symbol
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|type
operator|=
name|MRApps
operator|.
name|taskType
argument_list|(
name|symbol
argument_list|)
expr_stmt|;
block|}
name|StringBuilder
name|b
init|=
name|tableInit
argument_list|()
operator|.
name|append
argument_list|(
literal|", 'aaData': tasksTableData"
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
literal|"{'sType':'natural', 'aTargets': [ 0 ]"
argument_list|)
operator|.
name|append
argument_list|(
literal|", 'mRender': parseHadoopID }"
argument_list|)
operator|.
name|append
argument_list|(
literal|", {'sType':'numeric', 'aTargets': [ 4"
argument_list|)
operator|.
name|append
argument_list|(
name|type
operator|==
name|TaskType
operator|.
name|REDUCE
condition|?
literal|", 9, 10, 11, 12"
else|:
literal|", 7"
argument_list|)
operator|.
name|append
argument_list|(
literal|" ], 'mRender': renderHadoopElapsedTime }"
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n, {'sType':'numeric', 'aTargets': [ 2, 3, 5"
argument_list|)
operator|.
name|append
argument_list|(
name|type
operator|==
name|TaskType
operator|.
name|REDUCE
condition|?
literal|", 6, 7, 8"
else|:
literal|", 6"
argument_list|)
operator|.
name|append
argument_list|(
literal|" ], 'mRender': renderHadoopDate }]"
argument_list|)
comment|// Sort by id upon page load
operator|.
name|append
argument_list|(
literal|"\n, aaSorting: [[0, 'asc']]"
argument_list|)
operator|.
name|append
argument_list|(
literal|"}"
argument_list|)
decl_stmt|;
return|return
name|b
operator|.
name|toString
argument_list|()
return|;
block|}
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
literal|"  $('.dt-tasks').dataTable().fnFilter("
operator|+
literal|" this.value, $('tfoot input').index(this) );\n"
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

