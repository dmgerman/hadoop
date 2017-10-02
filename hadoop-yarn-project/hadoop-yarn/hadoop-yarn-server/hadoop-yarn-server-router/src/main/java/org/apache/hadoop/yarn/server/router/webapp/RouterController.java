begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.router.webapp
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
name|router
operator|.
name|webapp
package|;
end_package

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
comment|/**  * Controller for the Router Web UI.  */
end_comment

begin_class
DECL|class|RouterController
specifier|public
class|class
name|RouterController
extends|extends
name|Controller
block|{
annotation|@
name|Inject
DECL|method|RouterController (RequestContext ctx)
name|RouterController
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
annotation|@
name|Override
DECL|method|index ()
specifier|public
name|void
name|index
parameter_list|()
block|{
name|setTitle
argument_list|(
literal|"Router"
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
DECL|method|federation ()
specifier|public
name|void
name|federation
parameter_list|()
block|{
name|setTitle
argument_list|(
literal|"Federation"
argument_list|)
expr_stmt|;
name|render
argument_list|(
name|FederationPage
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|method|apps ()
specifier|public
name|void
name|apps
parameter_list|()
block|{
name|setTitle
argument_list|(
literal|"Applications"
argument_list|)
expr_stmt|;
name|render
argument_list|(
name|AppsPage
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
name|setTitle
argument_list|(
literal|"Nodes"
argument_list|)
expr_stmt|;
name|render
argument_list|(
name|NodesPage
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

