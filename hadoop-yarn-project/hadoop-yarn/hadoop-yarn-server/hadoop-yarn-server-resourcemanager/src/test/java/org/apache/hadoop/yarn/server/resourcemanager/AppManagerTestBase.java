begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager
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
package|;
end_package

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
operator|.
name|toSet
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|times
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|verify
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|ApplicationSubmissionContext
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
name|exceptions
operator|.
name|YarnException
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
name|placement
operator|.
name|PlacementManager
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
name|recovery
operator|.
name|RMStateStore
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
name|rmapp
operator|.
name|RMApp
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
name|YarnScheduler
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
name|security
operator|.
name|ClientToAMTokenSecretManagerInRM
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
name|security
operator|.
name|ApplicationACLsManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|ArgumentCaptor
import|;
end_import

begin_comment
comment|/**  * Base class for AppManager related test.  *  */
end_comment

begin_class
DECL|class|AppManagerTestBase
specifier|public
class|class
name|AppManagerTestBase
block|{
comment|// Extend and make the functions we want to test public
DECL|class|TestRMAppManager
specifier|protected
class|class
name|TestRMAppManager
extends|extends
name|RMAppManager
block|{
DECL|field|stateStore
specifier|private
specifier|final
name|RMStateStore
name|stateStore
decl_stmt|;
DECL|method|TestRMAppManager (RMContext context, Configuration conf)
specifier|public
name|TestRMAppManager
parameter_list|(
name|RMContext
name|context
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
operator|new
name|ApplicationACLsManager
argument_list|(
name|conf
argument_list|)
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|this
operator|.
name|stateStore
operator|=
name|context
operator|.
name|getStateStore
argument_list|()
expr_stmt|;
block|}
DECL|method|TestRMAppManager (RMContext context, ClientToAMTokenSecretManagerInRM clientToAMSecretManager, YarnScheduler scheduler, ApplicationMasterService masterService, ApplicationACLsManager applicationACLsManager, Configuration conf)
specifier|public
name|TestRMAppManager
parameter_list|(
name|RMContext
name|context
parameter_list|,
name|ClientToAMTokenSecretManagerInRM
name|clientToAMSecretManager
parameter_list|,
name|YarnScheduler
name|scheduler
parameter_list|,
name|ApplicationMasterService
name|masterService
parameter_list|,
name|ApplicationACLsManager
name|applicationACLsManager
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|,
name|scheduler
argument_list|,
name|masterService
argument_list|,
name|applicationACLsManager
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|this
operator|.
name|stateStore
operator|=
name|context
operator|.
name|getStateStore
argument_list|()
expr_stmt|;
block|}
DECL|method|checkAppNumCompletedLimit ()
specifier|public
name|void
name|checkAppNumCompletedLimit
parameter_list|()
block|{
name|super
operator|.
name|checkAppNumCompletedLimit
argument_list|()
expr_stmt|;
block|}
DECL|method|finishApplication (ApplicationId appId)
specifier|public
name|void
name|finishApplication
parameter_list|(
name|ApplicationId
name|appId
parameter_list|)
block|{
name|super
operator|.
name|finishApplication
argument_list|(
name|appId
argument_list|)
expr_stmt|;
block|}
DECL|method|getCompletedAppsListSize ()
specifier|public
name|int
name|getCompletedAppsListSize
parameter_list|()
block|{
return|return
name|super
operator|.
name|getCompletedAppsListSize
argument_list|()
return|;
block|}
DECL|method|getNumberOfCompletedAppsInStateStore ()
specifier|public
name|int
name|getNumberOfCompletedAppsInStateStore
parameter_list|()
block|{
return|return
name|this
operator|.
name|completedAppsInStateStore
return|;
block|}
DECL|method|getCompletedApps ()
specifier|public
name|List
argument_list|<
name|ApplicationId
argument_list|>
name|getCompletedApps
parameter_list|()
block|{
return|return
name|completedApps
return|;
block|}
DECL|method|getFirstNCompletedApps (int n)
specifier|public
name|Set
argument_list|<
name|ApplicationId
argument_list|>
name|getFirstNCompletedApps
parameter_list|(
name|int
name|n
parameter_list|)
block|{
return|return
name|getCompletedApps
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|limit
argument_list|(
name|n
argument_list|)
operator|.
name|collect
argument_list|(
name|toSet
argument_list|()
argument_list|)
return|;
block|}
DECL|method|getCompletedAppsWithEvenIdsInRange (int n)
specifier|public
name|Set
argument_list|<
name|ApplicationId
argument_list|>
name|getCompletedAppsWithEvenIdsInRange
parameter_list|(
name|int
name|n
parameter_list|)
block|{
return|return
name|getCompletedApps
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|limit
argument_list|(
name|n
argument_list|)
operator|.
name|filter
argument_list|(
name|app
lambda|->
name|app
operator|.
name|getId
argument_list|()
operator|%
literal|2
operator|==
literal|0
argument_list|)
operator|.
name|collect
argument_list|(
name|toSet
argument_list|()
argument_list|)
return|;
block|}
DECL|method|getRemovedAppsFromStateStore (int numRemoves)
specifier|public
name|Set
argument_list|<
name|ApplicationId
argument_list|>
name|getRemovedAppsFromStateStore
parameter_list|(
name|int
name|numRemoves
parameter_list|)
block|{
name|ArgumentCaptor
argument_list|<
name|RMApp
argument_list|>
name|argumentCaptor
init|=
name|ArgumentCaptor
operator|.
name|forClass
argument_list|(
name|RMApp
operator|.
name|class
argument_list|)
decl_stmt|;
name|verify
argument_list|(
name|stateStore
argument_list|,
name|times
argument_list|(
name|numRemoves
argument_list|)
argument_list|)
operator|.
name|removeApplication
argument_list|(
name|argumentCaptor
operator|.
name|capture
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|argumentCaptor
operator|.
name|getAllValues
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|RMApp
operator|::
name|getApplicationId
argument_list|)
operator|.
name|collect
argument_list|(
name|toSet
argument_list|()
argument_list|)
return|;
block|}
DECL|method|submitApplication ( ApplicationSubmissionContext submissionContext, String user)
specifier|public
name|void
name|submitApplication
parameter_list|(
name|ApplicationSubmissionContext
name|submissionContext
parameter_list|,
name|String
name|user
parameter_list|)
throws|throws
name|YarnException
block|{
name|super
operator|.
name|submitApplication
argument_list|(
name|submissionContext
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|,
name|user
argument_list|)
expr_stmt|;
block|}
DECL|method|getUserNameForPlacement (final String user, final ApplicationSubmissionContext context, final PlacementManager placementManager)
specifier|public
name|String
name|getUserNameForPlacement
parameter_list|(
specifier|final
name|String
name|user
parameter_list|,
specifier|final
name|ApplicationSubmissionContext
name|context
parameter_list|,
specifier|final
name|PlacementManager
name|placementManager
parameter_list|)
throws|throws
name|YarnException
block|{
return|return
name|super
operator|.
name|getUserNameForPlacement
argument_list|(
name|user
argument_list|,
name|context
argument_list|,
name|placementManager
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

