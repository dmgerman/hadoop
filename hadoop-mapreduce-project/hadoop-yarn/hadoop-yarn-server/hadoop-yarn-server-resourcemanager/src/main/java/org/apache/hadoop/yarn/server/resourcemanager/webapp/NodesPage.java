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
name|api
operator|.
name|records
operator|.
name|NodeHealthStatus
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
name|rmnode
operator|.
name|RMNode
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
name|Times
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
DECL|class|NodesPage
class|class
name|NodesPage
extends|extends
name|RmView
block|{
DECL|class|NodesBlock
specifier|static
class|class
name|NodesBlock
extends|extends
name|HtmlBlock
block|{
DECL|field|rmContext
specifier|final
name|RMContext
name|rmContext
decl_stmt|;
annotation|@
name|Inject
DECL|method|NodesBlock (RMContext context, ViewContext ctx)
name|NodesBlock
parameter_list|(
name|RMContext
name|context
parameter_list|,
name|ViewContext
name|ctx
parameter_list|)
block|{
name|super
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
name|this
operator|.
name|rmContext
operator|=
name|context
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
name|Hamlet
argument_list|>
argument_list|>
name|tbody
init|=
name|html
operator|.
name|table
argument_list|(
literal|"#nodes"
argument_list|)
operator|.
name|thead
argument_list|()
operator|.
name|tr
argument_list|()
operator|.
name|th
argument_list|(
literal|".rack"
argument_list|,
literal|"Rack"
argument_list|)
operator|.
name|th
argument_list|(
literal|".nodeaddress"
argument_list|,
literal|"Node Address"
argument_list|)
operator|.
name|th
argument_list|(
literal|".nodehttpaddress"
argument_list|,
literal|"Node HTTP Address"
argument_list|)
operator|.
name|th
argument_list|(
literal|".healthStatus"
argument_list|,
literal|"Health-status"
argument_list|)
operator|.
name|th
argument_list|(
literal|".lastHealthUpdate"
argument_list|,
literal|"Last health-update"
argument_list|)
operator|.
name|th
argument_list|(
literal|".healthReport"
argument_list|,
literal|"Health-report"
argument_list|)
operator|.
name|th
argument_list|(
literal|".containers"
argument_list|,
literal|"Containers"
argument_list|)
operator|.
comment|//          th(".mem", "Mem Used (MB)").
comment|//          th(".mem", "Mem Avail (MB)").
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
name|RMNode
name|ni
range|:
name|this
operator|.
name|rmContext
operator|.
name|getRMNodes
argument_list|()
operator|.
name|values
argument_list|()
control|)
block|{
name|NodeHealthStatus
name|health
init|=
name|ni
operator|.
name|getNodeHealthStatus
argument_list|()
decl_stmt|;
name|tbody
operator|.
name|tr
argument_list|()
operator|.
name|td
argument_list|(
name|ni
operator|.
name|getRackName
argument_list|()
argument_list|)
operator|.
name|td
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|ni
operator|.
name|getNodeID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
operator|.
name|td
argument_list|()
operator|.
name|a
argument_list|(
literal|"http://"
operator|+
name|ni
operator|.
name|getHttpAddress
argument_list|()
argument_list|,
name|ni
operator|.
name|getHttpAddress
argument_list|()
argument_list|)
operator|.
name|_
argument_list|()
operator|.
name|td
argument_list|(
name|health
operator|.
name|getIsNodeHealthy
argument_list|()
condition|?
literal|"Healthy"
else|:
literal|"Unhealthy"
argument_list|)
operator|.
name|td
argument_list|(
name|Times
operator|.
name|format
argument_list|(
name|health
operator|.
name|getLastHealthReportTime
argument_list|()
argument_list|)
argument_list|)
operator|.
name|td
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|health
operator|.
name|getHealthReport
argument_list|()
argument_list|)
argument_list|)
operator|.
comment|// TODO: acm: refactor2 FIXME
comment|//td(String.valueOf(ni.getNumContainers())).
comment|// TODO: FIXME Vinodkv
comment|//            td(String.valueOf(ni.getUsedResource().getMemory())).
comment|//            td(String.valueOf(ni.getAvailableResource().getMemory())).
name|td
argument_list|(
literal|"n/a"
argument_list|)
operator|.
name|_
argument_list|()
expr_stmt|;
block|}
name|tbody
operator|.
name|_
argument_list|()
operator|.
name|_
argument_list|()
expr_stmt|;
block|}
block|}
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
literal|"Nodes of the cluster"
argument_list|)
expr_stmt|;
name|set
argument_list|(
name|DATATABLES_ID
argument_list|,
literal|"nodes"
argument_list|)
expr_stmt|;
name|set
argument_list|(
name|initID
argument_list|(
name|DATATABLES
argument_list|,
literal|"nodes"
argument_list|)
argument_list|,
name|nodesTableInit
argument_list|()
argument_list|)
expr_stmt|;
name|setTableStyles
argument_list|(
name|html
argument_list|,
literal|"nodes"
argument_list|,
literal|".healthStatus {width:10em}"
argument_list|,
literal|".healthReport {width:10em}"
argument_list|)
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
name|NodesBlock
operator|.
name|class
return|;
block|}
DECL|method|nodesTableInit ()
specifier|private
name|String
name|nodesTableInit
parameter_list|()
block|{
return|return
name|tableInit
argument_list|()
operator|.
comment|// rack, nodeid, host, healthStatus, health update ts, health report,
comment|// containers, memused, memavail
name|append
argument_list|(
literal|", aoColumns:[null, null, null, null, null, null, "
argument_list|)
operator|.
name|append
argument_list|(
literal|"{sType:'title-numeric', bSearchable:false}]}"
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

