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
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
operator|.
name|StringUtils
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
name|ResourceManager
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
name|scheduler
operator|.
name|fifo
operator|.
name|FifoScheduler
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
name|webapp
operator|.
name|dao
operator|.
name|FifoSchedulerInfo
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
name|AppsBlock
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
name|hamlet2
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
name|hamlet2
operator|.
name|Hamlet
operator|.
name|DIV
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
name|hamlet2
operator|.
name|Hamlet
operator|.
name|UL
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
name|InfoBlock
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
DECL|class|DefaultSchedulerPage
class|class
name|DefaultSchedulerPage
extends|extends
name|RmView
block|{
DECL|field|_Q
specifier|static
specifier|final
name|String
name|_Q
init|=
literal|".ui-state-default.ui-corner-all"
decl_stmt|;
DECL|field|WIDTH_F
specifier|static
specifier|final
name|float
name|WIDTH_F
init|=
literal|0.8f
decl_stmt|;
DECL|field|Q_END
specifier|static
specifier|final
name|String
name|Q_END
init|=
literal|"left:101%"
decl_stmt|;
DECL|field|OVER
specifier|static
specifier|final
name|String
name|OVER
init|=
literal|"font-size:1px;background:#FFA333"
decl_stmt|;
DECL|field|UNDER
specifier|static
specifier|final
name|String
name|UNDER
init|=
literal|"font-size:1px;background:#5BD75B"
decl_stmt|;
DECL|field|EPSILON
specifier|static
specifier|final
name|float
name|EPSILON
init|=
literal|1e-8f
decl_stmt|;
DECL|class|QueueInfoBlock
specifier|static
class|class
name|QueueInfoBlock
extends|extends
name|HtmlBlock
block|{
DECL|field|sinfo
specifier|final
name|FifoSchedulerInfo
name|sinfo
decl_stmt|;
annotation|@
name|Inject
DECL|method|QueueInfoBlock (ViewContext ctx, ResourceManager rm)
name|QueueInfoBlock
parameter_list|(
name|ViewContext
name|ctx
parameter_list|,
name|ResourceManager
name|rm
parameter_list|)
block|{
name|super
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
name|sinfo
operator|=
operator|new
name|FifoSchedulerInfo
argument_list|(
name|rm
argument_list|)
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
name|info
argument_list|(
literal|"\'"
operator|+
name|sinfo
operator|.
name|getQueueName
argument_list|()
operator|+
literal|"\' Queue Status"
argument_list|)
operator|.
name|__
argument_list|(
literal|"Queue State:"
argument_list|,
name|sinfo
operator|.
name|getState
argument_list|()
argument_list|)
operator|.
name|__
argument_list|(
literal|"Minimum Queue Memory Capacity:"
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|sinfo
operator|.
name|getMinQueueMemoryCapacity
argument_list|()
argument_list|)
argument_list|)
operator|.
name|__
argument_list|(
literal|"Maximum Queue Memory Capacity:"
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|sinfo
operator|.
name|getMaxQueueMemoryCapacity
argument_list|()
argument_list|)
argument_list|)
operator|.
name|__
argument_list|(
literal|"Number of Nodes:"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|sinfo
operator|.
name|getNumNodes
argument_list|()
argument_list|)
argument_list|)
operator|.
name|__
argument_list|(
literal|"Used Node Capacity:"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|sinfo
operator|.
name|getUsedNodeCapacity
argument_list|()
argument_list|)
argument_list|)
operator|.
name|__
argument_list|(
literal|"Available Node Capacity:"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|sinfo
operator|.
name|getAvailNodeCapacity
argument_list|()
argument_list|)
argument_list|)
operator|.
name|__
argument_list|(
literal|"Total Node Capacity:"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|sinfo
operator|.
name|getTotalNodeCapacity
argument_list|()
argument_list|)
argument_list|)
operator|.
name|__
argument_list|(
literal|"Number of Node Containers:"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|sinfo
operator|.
name|getNumContainers
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|html
operator|.
name|__
argument_list|(
name|InfoBlock
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|QueuesBlock
specifier|static
class|class
name|QueuesBlock
extends|extends
name|HtmlBlock
block|{
DECL|field|sinfo
specifier|final
name|FifoSchedulerInfo
name|sinfo
decl_stmt|;
DECL|field|fs
specifier|final
name|FifoScheduler
name|fs
decl_stmt|;
DECL|method|QueuesBlock (ResourceManager rm)
annotation|@
name|Inject
name|QueuesBlock
parameter_list|(
name|ResourceManager
name|rm
parameter_list|)
block|{
name|sinfo
operator|=
operator|new
name|FifoSchedulerInfo
argument_list|(
name|rm
argument_list|)
expr_stmt|;
name|fs
operator|=
operator|(
name|FifoScheduler
operator|)
name|rm
operator|.
name|getResourceScheduler
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|render (Block html)
specifier|public
name|void
name|render
parameter_list|(
name|Block
name|html
parameter_list|)
block|{
name|html
operator|.
name|__
argument_list|(
name|MetricsOverviewTable
operator|.
name|class
argument_list|)
expr_stmt|;
name|UL
argument_list|<
name|DIV
argument_list|<
name|DIV
argument_list|<
name|Hamlet
argument_list|>
argument_list|>
argument_list|>
name|ul
init|=
name|html
operator|.
name|div
argument_list|(
literal|"#cs-wrapper.ui-widget"
argument_list|)
operator|.
name|div
argument_list|(
literal|".ui-widget-header.ui-corner-top"
argument_list|)
operator|.
name|__
argument_list|(
literal|"FifoScheduler Queue"
argument_list|)
operator|.
name|__
argument_list|()
operator|.
name|div
argument_list|(
literal|"#cs.ui-widget-content.ui-corner-bottom"
argument_list|)
operator|.
name|ul
argument_list|()
decl_stmt|;
if|if
condition|(
name|fs
operator|==
literal|null
condition|)
block|{
name|ul
operator|.
name|li
argument_list|()
operator|.
name|a
argument_list|(
name|_Q
argument_list|)
operator|.
name|$style
argument_list|(
name|width
argument_list|(
name|WIDTH_F
argument_list|)
argument_list|)
operator|.
name|span
argument_list|()
operator|.
name|$style
argument_list|(
name|Q_END
argument_list|)
operator|.
name|__
argument_list|(
literal|"100% "
argument_list|)
operator|.
name|__
argument_list|()
operator|.
name|span
argument_list|(
literal|".q"
argument_list|,
literal|"default"
argument_list|)
operator|.
name|__
argument_list|()
operator|.
name|__
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|float
name|used
init|=
name|sinfo
operator|.
name|getUsedCapacity
argument_list|()
decl_stmt|;
name|float
name|set
init|=
name|sinfo
operator|.
name|getCapacity
argument_list|()
decl_stmt|;
name|float
name|delta
init|=
name|Math
operator|.
name|abs
argument_list|(
name|set
operator|-
name|used
argument_list|)
operator|+
literal|0.001f
decl_stmt|;
name|ul
operator|.
name|li
argument_list|()
operator|.
name|a
argument_list|(
name|_Q
argument_list|)
operator|.
name|$style
argument_list|(
name|width
argument_list|(
name|WIDTH_F
argument_list|)
argument_list|)
operator|.
name|$title
argument_list|(
name|join
argument_list|(
literal|"used:"
argument_list|,
name|percent
argument_list|(
name|used
argument_list|)
argument_list|)
argument_list|)
operator|.
name|span
argument_list|()
operator|.
name|$style
argument_list|(
name|Q_END
argument_list|)
operator|.
name|__
argument_list|(
literal|"100%"
argument_list|)
operator|.
name|__
argument_list|()
operator|.
name|span
argument_list|()
operator|.
name|$style
argument_list|(
name|join
argument_list|(
name|width
argument_list|(
name|delta
argument_list|)
argument_list|,
literal|';'
argument_list|,
name|used
operator|>
name|set
condition|?
name|OVER
else|:
name|UNDER
argument_list|,
literal|';'
argument_list|,
name|used
operator|>
name|set
condition|?
name|left
argument_list|(
name|set
argument_list|)
else|:
name|left
argument_list|(
name|used
argument_list|)
argument_list|)
argument_list|)
operator|.
name|__
argument_list|(
literal|"."
argument_list|)
operator|.
name|__
argument_list|()
operator|.
name|span
argument_list|(
literal|".q"
argument_list|,
name|sinfo
operator|.
name|getQueueName
argument_list|()
argument_list|)
operator|.
name|__
argument_list|()
operator|.
name|__
argument_list|(
name|QueueInfoBlock
operator|.
name|class
argument_list|)
operator|.
name|__
argument_list|()
expr_stmt|;
block|}
name|ul
operator|.
name|__
argument_list|()
operator|.
name|__
argument_list|()
operator|.
name|script
argument_list|()
operator|.
name|$type
argument_list|(
literal|"text/javascript"
argument_list|)
operator|.
name|__
argument_list|(
literal|"$('#cs').hide();"
argument_list|)
operator|.
name|__
argument_list|()
operator|.
name|__
argument_list|()
operator|.
name|__
argument_list|(
name|AppsBlock
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
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
argument_list|()
operator|.
name|$type
argument_list|(
literal|"text/css"
argument_list|)
operator|.
name|__
argument_list|(
literal|"#cs { padding: 0.5em 0 1em 0; margin-bottom: 1em; position: relative }"
argument_list|,
literal|"#cs ul { list-style: none }"
argument_list|,
literal|"#cs a { font-weight: normal; margin: 2px; position: relative }"
argument_list|,
literal|"#cs a span { font-weight: normal; font-size: 80% }"
argument_list|,
literal|"#cs-wrapper .ui-widget-header { padding: 0.2em 0.5em }"
argument_list|,
literal|"table.info tr th {width: 50%}"
argument_list|)
operator|.
name|__
argument_list|()
operator|.
comment|// to center info table
name|script
argument_list|(
literal|"/static/jt/jquery.jstree.js"
argument_list|)
operator|.
name|script
argument_list|()
operator|.
name|$type
argument_list|(
literal|"text/javascript"
argument_list|)
operator|.
name|__
argument_list|(
literal|"$(function() {"
argument_list|,
literal|"  $('#cs a span').addClass('ui-corner-all').css('position', 'absolute');"
argument_list|,
literal|"  $('#cs').bind('loaded.jstree', function (e, data) {"
argument_list|,
literal|"    data.inst.open_all(); })."
argument_list|,
literal|"    jstree({"
argument_list|,
literal|"    core: { animation: 188, html_titles: true },"
argument_list|,
literal|"    plugins: ['themeroller', 'html_data', 'ui'],"
argument_list|,
literal|"    themeroller: { item_open: 'ui-icon-minus',"
argument_list|,
literal|"      item_clsd: 'ui-icon-plus', item_leaf: 'ui-icon-gear'"
argument_list|,
literal|"    }"
argument_list|,
literal|"  });"
argument_list|,
literal|"  $('#cs').bind('select_node.jstree', function(e, data) {"
argument_list|,
literal|"    var q = $('.q', data.rslt.obj).first().text();"
argument_list|,
literal|"    if (q == 'root') q = '';"
argument_list|,
literal|"    $('#apps').dataTable().fnFilter(q, 4);"
argument_list|,
literal|"  });"
argument_list|,
literal|"  $('#cs').show();"
argument_list|,
literal|"});"
argument_list|)
operator|.
name|__
argument_list|()
expr_stmt|;
block|}
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
name|QueuesBlock
operator|.
name|class
return|;
block|}
DECL|method|percent (float f)
specifier|static
name|String
name|percent
parameter_list|(
name|float
name|f
parameter_list|)
block|{
return|return
name|StringUtils
operator|.
name|formatPercent
argument_list|(
name|f
argument_list|,
literal|1
argument_list|)
return|;
block|}
DECL|method|width (float f)
specifier|static
name|String
name|width
parameter_list|(
name|float
name|f
parameter_list|)
block|{
return|return
name|StringUtils
operator|.
name|format
argument_list|(
literal|"width:%.1f%%"
argument_list|,
name|f
operator|*
literal|100
argument_list|)
return|;
block|}
DECL|method|left (float f)
specifier|static
name|String
name|left
parameter_list|(
name|float
name|f
parameter_list|)
block|{
return|return
name|StringUtils
operator|.
name|format
argument_list|(
literal|"left:%.1f%%"
argument_list|,
name|f
operator|*
literal|100
argument_list|)
return|;
block|}
block|}
end_class

end_unit

