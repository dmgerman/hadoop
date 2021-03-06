begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|fair
operator|.
name|FairScheduler
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
name|FairSchedulerInfo
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
name|FairSchedulerLeafQueueInfo
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
name|FairSchedulerQueueInfo
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
name|ResponseInfo
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
name|LI
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

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|servlet
operator|.
name|RequestScoped
import|;
end_import

begin_class
DECL|class|FairSchedulerPage
specifier|public
class|class
name|FairSchedulerPage
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
DECL|field|Q_MAX_WIDTH
specifier|static
specifier|final
name|float
name|Q_MAX_WIDTH
init|=
literal|0.8f
decl_stmt|;
DECL|field|Q_STATS_POS
specifier|static
specifier|final
name|float
name|Q_STATS_POS
init|=
name|Q_MAX_WIDTH
operator|+
literal|0.05f
decl_stmt|;
DECL|field|Q_END
specifier|static
specifier|final
name|String
name|Q_END
init|=
literal|"left:101%"
decl_stmt|;
DECL|field|Q_GIVEN
specifier|static
specifier|final
name|String
name|Q_GIVEN
init|=
literal|"left:0%;background:none;border:1px solid #000000"
decl_stmt|;
DECL|field|Q_INSTANTANEOUS_FS
specifier|static
specifier|final
name|String
name|Q_INSTANTANEOUS_FS
init|=
literal|"left:0%;background:none;border:1px dashed #000000"
decl_stmt|;
DECL|field|Q_OVER
specifier|static
specifier|final
name|String
name|Q_OVER
init|=
literal|"background:#FFA333"
decl_stmt|;
DECL|field|Q_UNDER
specifier|static
specifier|final
name|String
name|Q_UNDER
init|=
literal|"background:#5BD75B"
decl_stmt|;
DECL|field|STEADY_FAIR_SHARE
specifier|static
specifier|final
name|String
name|STEADY_FAIR_SHARE
init|=
literal|"Steady Fair Share"
decl_stmt|;
DECL|field|INSTANTANEOUS_FAIR_SHARE
specifier|static
specifier|final
name|String
name|INSTANTANEOUS_FAIR_SHARE
init|=
literal|"Instantaneous Fair Share"
decl_stmt|;
annotation|@
name|RequestScoped
DECL|class|FSQInfo
specifier|static
class|class
name|FSQInfo
block|{
DECL|field|qinfo
name|FairSchedulerQueueInfo
name|qinfo
decl_stmt|;
block|}
DECL|class|LeafQueueBlock
specifier|static
class|class
name|LeafQueueBlock
extends|extends
name|HtmlBlock
block|{
DECL|field|qinfo
specifier|final
name|FairSchedulerLeafQueueInfo
name|qinfo
decl_stmt|;
DECL|method|LeafQueueBlock (ViewContext ctx, FSQInfo info)
annotation|@
name|Inject
name|LeafQueueBlock
parameter_list|(
name|ViewContext
name|ctx
parameter_list|,
name|FSQInfo
name|info
parameter_list|)
block|{
name|super
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
name|qinfo
operator|=
operator|(
name|FairSchedulerLeafQueueInfo
operator|)
name|info
operator|.
name|qinfo
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
name|ResponseInfo
name|ri
init|=
name|info
argument_list|(
literal|"\'"
operator|+
name|qinfo
operator|.
name|getQueueName
argument_list|()
operator|+
literal|"\' Queue Status"
argument_list|)
operator|.
name|__
argument_list|(
literal|"Used Resources:"
argument_list|,
name|qinfo
operator|.
name|getUsedResources
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|__
argument_list|(
literal|"Demand Resources:"
argument_list|,
name|qinfo
operator|.
name|getDemandResources
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|__
argument_list|(
literal|"AM Used Resources:"
argument_list|,
name|qinfo
operator|.
name|getAMUsedResources
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|__
argument_list|(
literal|"AM Max Resources:"
argument_list|,
name|qinfo
operator|.
name|getAMMaxResources
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|__
argument_list|(
literal|"Num Active Applications:"
argument_list|,
name|qinfo
operator|.
name|getNumActiveApplications
argument_list|()
argument_list|)
operator|.
name|__
argument_list|(
literal|"Num Pending Applications:"
argument_list|,
name|qinfo
operator|.
name|getNumPendingApplications
argument_list|()
argument_list|)
operator|.
name|__
argument_list|(
literal|"Min Resources:"
argument_list|,
name|qinfo
operator|.
name|getMinResources
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|__
argument_list|(
literal|"Max Resources:"
argument_list|,
name|qinfo
operator|.
name|getMaxResources
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|__
argument_list|(
literal|"Max Container Allocation:"
argument_list|,
name|qinfo
operator|.
name|getMaxContainerAllocation
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|__
argument_list|(
literal|"Reserved Resources:"
argument_list|,
name|qinfo
operator|.
name|getReservedResources
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|maxApps
init|=
name|qinfo
operator|.
name|getMaxApplications
argument_list|()
decl_stmt|;
if|if
condition|(
name|maxApps
operator|<
name|Integer
operator|.
name|MAX_VALUE
condition|)
block|{
name|ri
operator|.
name|__
argument_list|(
literal|"Max Running Applications:"
argument_list|,
name|qinfo
operator|.
name|getMaxApplications
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|ri
operator|.
name|__
argument_list|(
name|STEADY_FAIR_SHARE
operator|+
literal|":"
argument_list|,
name|qinfo
operator|.
name|getSteadyFairShare
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|ri
operator|.
name|__
argument_list|(
name|INSTANTANEOUS_FAIR_SHARE
operator|+
literal|":"
argument_list|,
name|qinfo
operator|.
name|getFairShare
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|ri
operator|.
name|__
argument_list|(
literal|"Preemptable:"
argument_list|,
name|qinfo
operator|.
name|isPreemptable
argument_list|()
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
comment|// clear the info contents so this queue's info doesn't accumulate into another queue's info
name|ri
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|ParentQueueBlock
specifier|static
class|class
name|ParentQueueBlock
extends|extends
name|HtmlBlock
block|{
DECL|field|qinfo
specifier|final
name|FairSchedulerQueueInfo
name|qinfo
decl_stmt|;
DECL|method|ParentQueueBlock (ViewContext ctx, FSQInfo info)
annotation|@
name|Inject
name|ParentQueueBlock
parameter_list|(
name|ViewContext
name|ctx
parameter_list|,
name|FSQInfo
name|info
parameter_list|)
block|{
name|super
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
name|qinfo
operator|=
operator|(
name|FairSchedulerQueueInfo
operator|)
name|info
operator|.
name|qinfo
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
name|ResponseInfo
name|ri
init|=
name|info
argument_list|(
literal|"\'"
operator|+
name|qinfo
operator|.
name|getQueueName
argument_list|()
operator|+
literal|"\' Queue Status"
argument_list|)
operator|.
name|__
argument_list|(
literal|"Used Resources:"
argument_list|,
name|qinfo
operator|.
name|getUsedResources
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|__
argument_list|(
literal|"Min Resources:"
argument_list|,
name|qinfo
operator|.
name|getMinResources
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|__
argument_list|(
literal|"Max Resources:"
argument_list|,
name|qinfo
operator|.
name|getMaxResources
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|__
argument_list|(
literal|"Max Container Allocation:"
argument_list|,
name|qinfo
operator|.
name|getMaxContainerAllocation
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|__
argument_list|(
literal|"Reserved Resources:"
argument_list|,
name|qinfo
operator|.
name|getReservedResources
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|maxApps
init|=
name|qinfo
operator|.
name|getMaxApplications
argument_list|()
decl_stmt|;
if|if
condition|(
name|maxApps
operator|<
name|Integer
operator|.
name|MAX_VALUE
condition|)
block|{
name|ri
operator|.
name|__
argument_list|(
literal|"Max Running Applications:"
argument_list|,
name|qinfo
operator|.
name|getMaxApplications
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|ri
operator|.
name|__
argument_list|(
name|STEADY_FAIR_SHARE
operator|+
literal|":"
argument_list|,
name|qinfo
operator|.
name|getSteadyFairShare
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|ri
operator|.
name|__
argument_list|(
name|INSTANTANEOUS_FAIR_SHARE
operator|+
literal|":"
argument_list|,
name|qinfo
operator|.
name|getFairShare
argument_list|()
operator|.
name|toString
argument_list|()
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
comment|// clear the info contents so this queue's info doesn't accumulate into another queue's info
name|ri
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|QueueBlock
specifier|static
class|class
name|QueueBlock
extends|extends
name|HtmlBlock
block|{
DECL|field|fsqinfo
specifier|final
name|FSQInfo
name|fsqinfo
decl_stmt|;
DECL|method|QueueBlock (FSQInfo info)
annotation|@
name|Inject
name|QueueBlock
parameter_list|(
name|FSQInfo
name|info
parameter_list|)
block|{
name|fsqinfo
operator|=
name|info
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
name|Collection
argument_list|<
name|FairSchedulerQueueInfo
argument_list|>
name|subQueues
init|=
name|fsqinfo
operator|.
name|qinfo
operator|.
name|getChildQueues
argument_list|()
decl_stmt|;
name|UL
argument_list|<
name|Hamlet
argument_list|>
name|ul
init|=
name|html
operator|.
name|ul
argument_list|(
literal|"#pq"
argument_list|)
decl_stmt|;
for|for
control|(
name|FairSchedulerQueueInfo
name|info
range|:
name|subQueues
control|)
block|{
name|float
name|capacity
init|=
name|info
operator|.
name|getMaxResourcesFraction
argument_list|()
decl_stmt|;
name|float
name|steadyFairShare
init|=
name|info
operator|.
name|getSteadyFairShareMemoryFraction
argument_list|()
decl_stmt|;
name|float
name|instantaneousFairShare
init|=
name|info
operator|.
name|getFairShareMemoryFraction
argument_list|()
decl_stmt|;
name|float
name|used
init|=
name|info
operator|.
name|getUsedMemoryFraction
argument_list|()
decl_stmt|;
name|LI
argument_list|<
name|UL
argument_list|<
name|Hamlet
argument_list|>
argument_list|>
name|li
init|=
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
name|capacity
operator|*
name|Q_MAX_WIDTH
argument_list|)
argument_list|)
operator|.
name|$title
argument_list|(
name|join
argument_list|(
name|join
argument_list|(
name|STEADY_FAIR_SHARE
operator|+
literal|":"
argument_list|,
name|percent
argument_list|(
name|steadyFairShare
argument_list|)
argument_list|)
argument_list|,
name|join
argument_list|(
literal|" "
operator|+
name|INSTANTANEOUS_FAIR_SHARE
operator|+
literal|":"
argument_list|,
name|percent
argument_list|(
name|instantaneousFairShare
argument_list|)
argument_list|)
argument_list|)
argument_list|)
operator|.
name|span
argument_list|()
operator|.
name|$style
argument_list|(
name|join
argument_list|(
name|Q_GIVEN
argument_list|,
literal|";font-size:1px;"
argument_list|,
name|width
argument_list|(
name|steadyFairShare
operator|/
name|capacity
argument_list|)
argument_list|)
argument_list|)
operator|.
name|__
argument_list|(
literal|'.'
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
name|Q_INSTANTANEOUS_FS
argument_list|,
literal|";font-size:1px;"
argument_list|,
name|width
argument_list|(
name|instantaneousFairShare
operator|/
name|capacity
argument_list|)
argument_list|)
argument_list|)
operator|.
name|__
argument_list|(
literal|'.'
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
name|used
operator|/
name|capacity
argument_list|)
argument_list|,
literal|";font-size:1px;left:0%;"
argument_list|,
name|used
operator|>
name|instantaneousFairShare
condition|?
name|Q_OVER
else|:
name|Q_UNDER
argument_list|)
argument_list|)
operator|.
name|__
argument_list|(
literal|'.'
argument_list|)
operator|.
name|__
argument_list|()
operator|.
name|span
argument_list|(
literal|".q"
argument_list|,
name|info
operator|.
name|getQueueName
argument_list|()
argument_list|)
operator|.
name|__
argument_list|()
operator|.
name|span
argument_list|()
operator|.
name|$class
argument_list|(
literal|"qstats"
argument_list|)
operator|.
name|$style
argument_list|(
name|left
argument_list|(
name|Q_STATS_POS
argument_list|)
argument_list|)
operator|.
name|__
argument_list|(
name|join
argument_list|(
name|percent
argument_list|(
name|used
argument_list|)
argument_list|,
literal|" used"
argument_list|)
argument_list|)
operator|.
name|__
argument_list|()
decl_stmt|;
name|fsqinfo
operator|.
name|qinfo
operator|=
name|info
expr_stmt|;
if|if
condition|(
name|info
operator|instanceof
name|FairSchedulerLeafQueueInfo
condition|)
block|{
name|li
operator|.
name|ul
argument_list|(
literal|"#lq"
argument_list|)
operator|.
name|li
argument_list|()
operator|.
name|__
argument_list|(
name|LeafQueueBlock
operator|.
name|class
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
name|li
operator|.
name|ul
argument_list|(
literal|"#lq"
argument_list|)
operator|.
name|li
argument_list|()
operator|.
name|__
argument_list|(
name|ParentQueueBlock
operator|.
name|class
argument_list|)
operator|.
name|__
argument_list|()
operator|.
name|__
argument_list|()
expr_stmt|;
name|li
operator|.
name|__
argument_list|(
name|QueueBlock
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
name|li
operator|.
name|__
argument_list|()
expr_stmt|;
block|}
name|ul
operator|.
name|__
argument_list|()
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
DECL|field|fs
specifier|final
name|FairScheduler
name|fs
decl_stmt|;
DECL|field|fsqinfo
specifier|final
name|FSQInfo
name|fsqinfo
decl_stmt|;
DECL|method|QueuesBlock (ResourceManager rm, FSQInfo info)
annotation|@
name|Inject
name|QueuesBlock
parameter_list|(
name|ResourceManager
name|rm
parameter_list|,
name|FSQInfo
name|info
parameter_list|)
block|{
name|fs
operator|=
operator|(
name|FairScheduler
operator|)
name|rm
operator|.
name|getResourceScheduler
argument_list|()
expr_stmt|;
name|fsqinfo
operator|=
name|info
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
literal|"Application Queues"
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
name|Q_MAX_WIDTH
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
name|FairSchedulerInfo
name|sinfo
init|=
operator|new
name|FairSchedulerInfo
argument_list|(
name|fs
argument_list|)
decl_stmt|;
name|fsqinfo
operator|.
name|qinfo
operator|=
name|sinfo
operator|.
name|getRootQueueInfo
argument_list|()
expr_stmt|;
name|float
name|used
init|=
name|fsqinfo
operator|.
name|qinfo
operator|.
name|getUsedMemoryFraction
argument_list|()
decl_stmt|;
name|ul
operator|.
name|li
argument_list|()
operator|.
name|$style
argument_list|(
literal|"margin-bottom: 1em"
argument_list|)
operator|.
name|span
argument_list|()
operator|.
name|$style
argument_list|(
literal|"font-weight: bold"
argument_list|)
operator|.
name|__
argument_list|(
literal|"Legend:"
argument_list|)
operator|.
name|__
argument_list|()
operator|.
name|span
argument_list|()
operator|.
name|$class
argument_list|(
literal|"qlegend ui-corner-all"
argument_list|)
operator|.
name|$style
argument_list|(
name|Q_GIVEN
argument_list|)
operator|.
name|$title
argument_list|(
literal|"The steady fair shares consider all queues, "
operator|+
literal|"both active (with running applications) and inactive."
argument_list|)
operator|.
name|__
argument_list|(
name|STEADY_FAIR_SHARE
argument_list|)
operator|.
name|__
argument_list|()
operator|.
name|span
argument_list|()
operator|.
name|$class
argument_list|(
literal|"qlegend ui-corner-all"
argument_list|)
operator|.
name|$style
argument_list|(
name|Q_INSTANTANEOUS_FS
argument_list|)
operator|.
name|$title
argument_list|(
literal|"The instantaneous fair shares consider only active "
operator|+
literal|"queues (with running applications)."
argument_list|)
operator|.
name|__
argument_list|(
name|INSTANTANEOUS_FAIR_SHARE
argument_list|)
operator|.
name|__
argument_list|()
operator|.
name|span
argument_list|()
operator|.
name|$class
argument_list|(
literal|"qlegend ui-corner-all"
argument_list|)
operator|.
name|$style
argument_list|(
name|Q_UNDER
argument_list|)
operator|.
name|__
argument_list|(
literal|"Used"
argument_list|)
operator|.
name|__
argument_list|()
operator|.
name|span
argument_list|()
operator|.
name|$class
argument_list|(
literal|"qlegend ui-corner-all"
argument_list|)
operator|.
name|$style
argument_list|(
name|Q_OVER
argument_list|)
operator|.
name|__
argument_list|(
literal|"Used (over fair share)"
argument_list|)
operator|.
name|__
argument_list|()
operator|.
name|span
argument_list|()
operator|.
name|$class
argument_list|(
literal|"qlegend ui-corner-all ui-state-default"
argument_list|)
operator|.
name|__
argument_list|(
literal|"Max Capacity"
argument_list|)
operator|.
name|__
argument_list|()
operator|.
name|__
argument_list|()
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
name|Q_MAX_WIDTH
argument_list|)
argument_list|)
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
name|used
argument_list|)
argument_list|,
literal|";left:0%;"
argument_list|,
name|used
operator|>
literal|1
condition|?
name|Q_OVER
else|:
name|Q_UNDER
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
literal|"root"
argument_list|)
operator|.
name|__
argument_list|()
operator|.
name|span
argument_list|()
operator|.
name|$class
argument_list|(
literal|"qstats"
argument_list|)
operator|.
name|$style
argument_list|(
name|left
argument_list|(
name|Q_STATS_POS
argument_list|)
argument_list|)
operator|.
name|__
argument_list|(
name|join
argument_list|(
name|percent
argument_list|(
name|used
argument_list|)
argument_list|,
literal|" used"
argument_list|)
argument_list|)
operator|.
name|__
argument_list|()
operator|.
name|__
argument_list|(
name|QueueBlock
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
name|FairSchedulerAppsBlock
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
literal|".qstats { font-weight: normal; font-size: 80%; position: absolute }"
argument_list|,
literal|".qlegend { font-weight: normal; padding: 0 1em; margin: 1em }"
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
literal|"    var callback = { call:reopenQueryNodes }"
argument_list|,
literal|"    data.inst.open_node('#pq', callback);"
argument_list|,
literal|"   })."
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
literal|"    var queues = $('.q', data.rslt.obj);"
argument_list|,
literal|"    var q = '^' + queues.first().text();"
argument_list|,
literal|"    q += queues.length == 1 ? '$' : '\\\\.';"
argument_list|,
literal|"    $('#apps').dataTable().fnFilter(q, 4, true);"
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
operator|.
name|__
argument_list|(
name|SchedulerPageUtil
operator|.
name|QueueBlockUtil
operator|.
name|class
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
name|QueuesBlock
operator|.
name|class
return|;
block|}
annotation|@
name|Override
DECL|method|initAppsTable ()
specifier|protected
name|String
name|initAppsTable
parameter_list|()
block|{
return|return
name|WebPageUtils
operator|.
name|appsTableInit
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
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

