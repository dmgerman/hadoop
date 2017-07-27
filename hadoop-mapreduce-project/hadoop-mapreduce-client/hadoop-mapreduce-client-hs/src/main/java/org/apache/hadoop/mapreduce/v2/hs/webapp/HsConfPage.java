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
name|JOB_ID
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
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|webapp
operator|.
name|ConfBlock
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
comment|/**  * Render a page with the configuration for a give job in it.  */
end_comment

begin_class
DECL|class|HsConfPage
specifier|public
class|class
name|HsConfPage
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
name|String
name|jobID
init|=
name|$
argument_list|(
name|JOB_ID
argument_list|)
decl_stmt|;
name|set
argument_list|(
name|TITLE
argument_list|,
name|jobID
operator|.
name|isEmpty
argument_list|()
condition|?
literal|"Bad request: missing job ID"
else|:
name|join
argument_list|(
literal|"Configuration for MapReduce Job "
argument_list|,
name|$
argument_list|(
name|JOB_ID
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|commonPreHead
argument_list|(
name|html
argument_list|)
expr_stmt|;
name|set
argument_list|(
name|DATATABLES_ID
argument_list|,
literal|"conf"
argument_list|)
expr_stmt|;
name|set
argument_list|(
name|initID
argument_list|(
name|DATATABLES
argument_list|,
literal|"conf"
argument_list|)
argument_list|,
name|confTableInit
argument_list|()
argument_list|)
expr_stmt|;
name|set
argument_list|(
name|postInitID
argument_list|(
name|DATATABLES
argument_list|,
literal|"conf"
argument_list|)
argument_list|,
name|confPostTableInit
argument_list|()
argument_list|)
expr_stmt|;
name|setTableStyles
argument_list|(
name|html
argument_list|,
literal|"conf"
argument_list|)
expr_stmt|;
comment|//Override the default nav config
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
block|}
comment|/**    * The body of this block is the configuration block.    * @return HsConfBlock.class    */
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
name|ConfBlock
operator|.
name|class
return|;
block|}
comment|/**    * @return the end of the JS map that is the jquery datatable config for the    * conf table.    */
DECL|method|confTableInit ()
specifier|private
name|String
name|confTableInit
parameter_list|()
block|{
return|return
name|tableInit
argument_list|()
operator|.
name|append
argument_list|(
literal|"}"
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * @return the java script code to allow the jquery conf datatable to filter    * by column.    */
DECL|method|confPostTableInit ()
specifier|private
name|String
name|confPostTableInit
parameter_list|()
block|{
return|return
literal|"var confInitVals = new Array();\n"
operator|+
literal|"$('tfoot input').keyup( function () \n{"
operator|+
literal|"  confDataTable.fnFilter( this.value, $('tfoot input').index(this) );\n"
operator|+
literal|"} );\n"
operator|+
literal|"$('tfoot input').each( function (i) {\n"
operator|+
literal|"  confInitVals[i] = this.value;\n"
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
literal|"    this.value = confInitVals[$('tfoot input').index(this)];\n"
operator|+
literal|"  }\n"
operator|+
literal|"} );\n"
return|;
block|}
block|}
end_class

end_unit

