begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.webapp
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
name|nodemanager
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

begin_class
DECL|class|NMController
specifier|public
class|class
name|NMController
extends|extends
name|Controller
implements|implements
name|YarnWebParams
block|{
annotation|@
name|Inject
DECL|method|NMController (RequestContext requestContext)
specifier|public
name|NMController
parameter_list|(
name|RequestContext
name|requestContext
parameter_list|)
block|{
name|super
argument_list|(
name|requestContext
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
comment|// TODO: What use of this with info() in?
DECL|method|index ()
specifier|public
name|void
name|index
parameter_list|()
block|{
name|setTitle
argument_list|(
name|join
argument_list|(
literal|"NodeManager - "
argument_list|,
name|$
argument_list|(
name|NM_NODENAME
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|info ()
specifier|public
name|void
name|info
parameter_list|()
block|{
name|render
argument_list|(
name|NodePage
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|method|node ()
specifier|public
name|void
name|node
parameter_list|()
block|{
name|render
argument_list|(
name|NodePage
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|method|allApplications ()
specifier|public
name|void
name|allApplications
parameter_list|()
block|{
name|render
argument_list|(
name|AllApplicationsPage
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|method|allContainers ()
specifier|public
name|void
name|allContainers
parameter_list|()
block|{
name|render
argument_list|(
name|AllContainersPage
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|method|application ()
specifier|public
name|void
name|application
parameter_list|()
block|{
name|render
argument_list|(
name|ApplicationPage
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|method|container ()
specifier|public
name|void
name|container
parameter_list|()
block|{
name|render
argument_list|(
name|ContainerPage
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|method|errorsAndWarnings ()
specifier|public
name|void
name|errorsAndWarnings
parameter_list|()
block|{
name|render
argument_list|(
name|NMErrorsAndWarningsPage
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|method|logs ()
specifier|public
name|void
name|logs
parameter_list|()
block|{
name|render
argument_list|(
name|ContainerLogsPage
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

