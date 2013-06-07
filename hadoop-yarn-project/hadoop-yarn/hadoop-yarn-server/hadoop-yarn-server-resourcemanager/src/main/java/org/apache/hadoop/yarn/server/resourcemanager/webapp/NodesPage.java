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
name|server
operator|.
name|resourcemanager
operator|.
name|webapp
operator|.
name|RMWebApp
operator|.
name|NODE_STATE
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
name|tableInit
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|http
operator|.
name|HttpConfig
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
name|api
operator|.
name|records
operator|.
name|NodeState
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
name|server
operator|.
name|resourcemanager
operator|.
name|scheduler
operator|.
name|ResourceScheduler
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
name|NodeInfo
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
name|hamlet
operator|.
name|Hamlet
operator|.
name|TR
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
DECL|field|rm
specifier|final
name|ResourceManager
name|rm
decl_stmt|;
DECL|field|BYTES_IN_MB
specifier|private
specifier|static
specifier|final
name|long
name|BYTES_IN_MB
init|=
literal|1024
operator|*
literal|1024
decl_stmt|;
annotation|@
name|Inject
DECL|method|NodesBlock (RMContext context, ResourceManager rm, ViewContext ctx)
name|NodesBlock
parameter_list|(
name|RMContext
name|context
parameter_list|,
name|ResourceManager
name|rm
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
name|this
operator|.
name|rm
operator|=
name|rm
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
name|html
operator|.
name|_
argument_list|(
name|MetricsOverviewTable
operator|.
name|class
argument_list|)
expr_stmt|;
name|ResourceScheduler
name|sched
init|=
name|rm
operator|.
name|getResourceScheduler
argument_list|()
decl_stmt|;
name|String
name|type
init|=
name|$
argument_list|(
name|NODE_STATE
argument_list|)
decl_stmt|;
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
literal|".state"
argument_list|,
literal|"Node State"
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
name|th
argument_list|(
literal|".mem"
argument_list|,
literal|"Mem Used"
argument_list|)
operator|.
name|th
argument_list|(
literal|".mem"
argument_list|,
literal|"Mem Avail"
argument_list|)
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
name|NodeState
name|stateFilter
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|type
operator|!=
literal|null
operator|&&
operator|!
name|type
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|stateFilter
operator|=
name|NodeState
operator|.
name|valueOf
argument_list|(
name|type
operator|.
name|toUpperCase
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Collection
argument_list|<
name|RMNode
argument_list|>
name|rmNodes
init|=
name|this
operator|.
name|rmContext
operator|.
name|getRMNodes
argument_list|()
operator|.
name|values
argument_list|()
decl_stmt|;
name|boolean
name|isInactive
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|stateFilter
operator|!=
literal|null
condition|)
block|{
switch|switch
condition|(
name|stateFilter
condition|)
block|{
case|case
name|DECOMMISSIONED
case|:
case|case
name|LOST
case|:
case|case
name|REBOOTED
case|:
name|rmNodes
operator|=
name|this
operator|.
name|rmContext
operator|.
name|getInactiveRMNodes
argument_list|()
operator|.
name|values
argument_list|()
expr_stmt|;
name|isInactive
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
for|for
control|(
name|RMNode
name|ni
range|:
name|rmNodes
control|)
block|{
if|if
condition|(
name|stateFilter
operator|!=
literal|null
condition|)
block|{
name|NodeState
name|state
init|=
name|ni
operator|.
name|getState
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|stateFilter
operator|.
name|equals
argument_list|(
name|state
argument_list|)
condition|)
block|{
continue|continue;
block|}
block|}
else|else
block|{
comment|// No filter. User is asking for all nodes. Make sure you skip the
comment|// unhealthy nodes.
if|if
condition|(
name|ni
operator|.
name|getState
argument_list|()
operator|==
name|NodeState
operator|.
name|UNHEALTHY
condition|)
block|{
continue|continue;
block|}
block|}
name|NodeInfo
name|info
init|=
operator|new
name|NodeInfo
argument_list|(
name|ni
argument_list|,
name|sched
argument_list|)
decl_stmt|;
name|int
name|usedMemory
init|=
operator|(
name|int
operator|)
name|info
operator|.
name|getUsedMemory
argument_list|()
decl_stmt|;
name|int
name|availableMemory
init|=
operator|(
name|int
operator|)
name|info
operator|.
name|getAvailableMemory
argument_list|()
decl_stmt|;
name|TR
argument_list|<
name|TBODY
argument_list|<
name|TABLE
argument_list|<
name|Hamlet
argument_list|>
argument_list|>
argument_list|>
name|row
init|=
name|tbody
operator|.
name|tr
argument_list|()
operator|.
name|td
argument_list|(
name|info
operator|.
name|getRack
argument_list|()
argument_list|)
operator|.
name|td
argument_list|(
name|info
operator|.
name|getState
argument_list|()
argument_list|)
operator|.
name|td
argument_list|(
name|info
operator|.
name|getNodeId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|isInactive
condition|)
block|{
name|row
operator|.
name|td
argument_list|()
operator|.
name|_
argument_list|(
literal|"N/A"
argument_list|)
operator|.
name|_
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|String
name|httpAddress
init|=
name|info
operator|.
name|getNodeHTTPAddress
argument_list|()
decl_stmt|;
name|row
operator|.
name|td
argument_list|()
operator|.
name|a
argument_list|(
name|HttpConfig
operator|.
name|getSchemePrefix
argument_list|()
operator|+
name|httpAddress
argument_list|,
name|httpAddress
argument_list|)
operator|.
name|_
argument_list|()
expr_stmt|;
block|}
name|row
operator|.
name|td
argument_list|()
operator|.
name|br
argument_list|()
operator|.
name|$title
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|info
operator|.
name|getLastHealthUpdate
argument_list|()
argument_list|)
argument_list|)
operator|.
name|_
argument_list|()
operator|.
name|_
argument_list|(
name|Times
operator|.
name|format
argument_list|(
name|info
operator|.
name|getLastHealthUpdate
argument_list|()
argument_list|)
argument_list|)
operator|.
name|_
argument_list|()
operator|.
name|td
argument_list|(
name|info
operator|.
name|getHealthReport
argument_list|()
argument_list|)
operator|.
name|td
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|info
operator|.
name|getNumContainers
argument_list|()
argument_list|)
argument_list|)
operator|.
name|td
argument_list|()
operator|.
name|br
argument_list|()
operator|.
name|$title
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|usedMemory
argument_list|)
argument_list|)
operator|.
name|_
argument_list|()
operator|.
name|_
argument_list|(
name|StringUtils
operator|.
name|byteDesc
argument_list|(
name|usedMemory
operator|*
name|BYTES_IN_MB
argument_list|)
argument_list|)
operator|.
name|_
argument_list|()
operator|.
name|td
argument_list|()
operator|.
name|br
argument_list|()
operator|.
name|$title
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|usedMemory
argument_list|)
argument_list|)
operator|.
name|_
argument_list|()
operator|.
name|_
argument_list|(
name|StringUtils
operator|.
name|byteDesc
argument_list|(
name|availableMemory
operator|*
name|BYTES_IN_MB
argument_list|)
argument_list|)
operator|.
name|_
argument_list|()
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
name|String
name|type
init|=
name|$
argument_list|(
name|NODE_STATE
argument_list|)
decl_stmt|;
name|String
name|title
init|=
literal|"Nodes of the cluster"
decl_stmt|;
if|if
condition|(
name|type
operator|!=
literal|null
operator|&&
operator|!
name|type
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|title
operator|=
name|title
operator|+
literal|" ("
operator|+
name|type
operator|+
literal|")"
expr_stmt|;
block|}
name|setTitle
argument_list|(
name|title
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

