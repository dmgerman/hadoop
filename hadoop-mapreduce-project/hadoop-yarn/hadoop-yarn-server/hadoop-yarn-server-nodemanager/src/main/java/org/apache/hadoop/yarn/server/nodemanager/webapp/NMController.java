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
name|conf
operator|.
name|Configuration
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
name|ApplicationId
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
name|ContainerId
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
name|conf
operator|.
name|YarnConfiguration
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
name|nodemanager
operator|.
name|Context
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
name|nodemanager
operator|.
name|containermanager
operator|.
name|application
operator|.
name|Application
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
name|ConverterUtils
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
DECL|field|nmContext
specifier|private
name|Context
name|nmContext
decl_stmt|;
DECL|field|nmConf
specifier|private
name|Configuration
name|nmConf
decl_stmt|;
annotation|@
name|Inject
DECL|method|NMController (Configuration nmConf, RequestContext requestContext, Context nmContext)
specifier|public
name|NMController
parameter_list|(
name|Configuration
name|nmConf
parameter_list|,
name|RequestContext
name|requestContext
parameter_list|,
name|Context
name|nmContext
parameter_list|)
block|{
name|super
argument_list|(
name|requestContext
argument_list|)
expr_stmt|;
name|this
operator|.
name|nmContext
operator|=
name|nmContext
expr_stmt|;
name|this
operator|.
name|nmConf
operator|=
name|nmConf
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
DECL|method|logs ()
specifier|public
name|void
name|logs
parameter_list|()
block|{
name|String
name|containerIdStr
init|=
name|$
argument_list|(
name|CONTAINER_ID
argument_list|)
decl_stmt|;
name|ContainerId
name|containerId
init|=
literal|null
decl_stmt|;
try|try
block|{
name|containerId
operator|=
name|ConverterUtils
operator|.
name|toContainerId
argument_list|(
name|containerIdStr
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|render
argument_list|(
name|ContainerLogsPage
operator|.
name|class
argument_list|)
expr_stmt|;
return|return;
block|}
name|ApplicationId
name|appId
init|=
name|containerId
operator|.
name|getApplicationAttemptId
argument_list|()
operator|.
name|getApplicationId
argument_list|()
decl_stmt|;
name|Application
name|app
init|=
name|nmContext
operator|.
name|getApplications
argument_list|()
operator|.
name|get
argument_list|(
name|appId
argument_list|)
decl_stmt|;
if|if
condition|(
name|app
operator|==
literal|null
operator|&&
name|nmConf
operator|.
name|getBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|LOG_AGGREGATION_ENABLED
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_LOG_AGGREGATION_ENABLED
argument_list|)
condition|)
block|{
name|String
name|logServerUrl
init|=
name|nmConf
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|YARN_LOG_SERVER_URL
argument_list|)
decl_stmt|;
name|String
name|redirectUrl
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|logServerUrl
operator|==
literal|null
operator|||
name|logServerUrl
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|redirectUrl
operator|=
literal|"false"
expr_stmt|;
block|}
else|else
block|{
name|redirectUrl
operator|=
name|url
argument_list|(
name|logServerUrl
argument_list|,
name|nmContext
operator|.
name|getNodeId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|containerIdStr
argument_list|,
name|containerIdStr
argument_list|,
name|$
argument_list|(
name|APP_OWNER
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|set
argument_list|(
name|ContainerLogsPage
operator|.
name|REDIRECT_URL
argument_list|,
name|redirectUrl
argument_list|)
expr_stmt|;
block|}
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

