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
name|YarnWebParams
operator|.
name|QUEUE_NAME
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
name|YarnApplicationState
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
name|scheduler
operator|.
name|capacity
operator|.
name|CapacityScheduler
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
name|util
operator|.
name|StringHelper
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
name|Controller
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

begin_comment
comment|// Do NOT rename/refactor this to RMView as it will wreak havoc
end_comment

begin_comment
comment|// on Mac OS HFS as its case-insensitive!
end_comment

begin_class
DECL|class|RmController
specifier|public
class|class
name|RmController
extends|extends
name|Controller
block|{
annotation|@
name|Inject
DECL|method|RmController (RequestContext ctx)
name|RmController
parameter_list|(
name|RequestContext
name|ctx
parameter_list|)
block|{
name|super
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
block|}
DECL|method|index ()
annotation|@
name|Override
specifier|public
name|void
name|index
parameter_list|()
block|{
name|setTitle
argument_list|(
literal|"Applications"
argument_list|)
expr_stmt|;
block|}
DECL|method|about ()
specifier|public
name|void
name|about
parameter_list|()
block|{
name|setTitle
argument_list|(
literal|"About the Cluster"
argument_list|)
expr_stmt|;
name|render
argument_list|(
name|AboutPage
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|method|app ()
specifier|public
name|void
name|app
parameter_list|()
block|{
name|render
argument_list|(
name|AppPage
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|method|nodes ()
specifier|public
name|void
name|nodes
parameter_list|()
block|{
name|render
argument_list|(
name|NodesPage
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|method|scheduler ()
specifier|public
name|void
name|scheduler
parameter_list|()
block|{
comment|// limit applications to those in states relevant to scheduling
name|set
argument_list|(
name|YarnWebParams
operator|.
name|APP_STATE
argument_list|,
name|StringHelper
operator|.
name|cjoin
argument_list|(
name|YarnApplicationState
operator|.
name|NEW
operator|.
name|toString
argument_list|()
argument_list|,
name|YarnApplicationState
operator|.
name|NEW_SAVING
operator|.
name|toString
argument_list|()
argument_list|,
name|YarnApplicationState
operator|.
name|SUBMITTED
operator|.
name|toString
argument_list|()
argument_list|,
name|YarnApplicationState
operator|.
name|ACCEPTED
operator|.
name|toString
argument_list|()
argument_list|,
name|YarnApplicationState
operator|.
name|RUNNING
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|ResourceManager
name|rm
init|=
name|getInstance
argument_list|(
name|ResourceManager
operator|.
name|class
argument_list|)
decl_stmt|;
name|ResourceScheduler
name|rs
init|=
name|rm
operator|.
name|getResourceScheduler
argument_list|()
decl_stmt|;
if|if
condition|(
name|rs
operator|==
literal|null
operator|||
name|rs
operator|instanceof
name|CapacityScheduler
condition|)
block|{
name|setTitle
argument_list|(
literal|"Capacity Scheduler"
argument_list|)
expr_stmt|;
name|render
argument_list|(
name|CapacitySchedulerPage
operator|.
name|class
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|rs
operator|instanceof
name|FairScheduler
condition|)
block|{
name|setTitle
argument_list|(
literal|"Fair Scheduler"
argument_list|)
expr_stmt|;
name|render
argument_list|(
name|FairSchedulerPage
operator|.
name|class
argument_list|)
expr_stmt|;
return|return;
block|}
name|setTitle
argument_list|(
literal|"Default Scheduler"
argument_list|)
expr_stmt|;
name|render
argument_list|(
name|DefaultSchedulerPage
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|method|queue ()
specifier|public
name|void
name|queue
parameter_list|()
block|{
name|setTitle
argument_list|(
name|join
argument_list|(
literal|"Queue "
argument_list|,
name|get
argument_list|(
name|QUEUE_NAME
argument_list|,
literal|"unknown"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|submit ()
specifier|public
name|void
name|submit
parameter_list|()
block|{
name|setTitle
argument_list|(
literal|"Application Submission Not Allowed"
argument_list|)
expr_stmt|;
block|}
DECL|method|nodelabels ()
specifier|public
name|void
name|nodelabels
parameter_list|()
block|{
name|setTitle
argument_list|(
literal|"Node Labels"
argument_list|)
expr_stmt|;
name|render
argument_list|(
name|NodeLabelsPage
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

