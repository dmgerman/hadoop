begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|mockito
operator|.
name|Mockito
operator|.
name|mock
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
name|when
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|NodeManager
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
name|NodeManager
operator|.
name|NMContext
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
name|recovery
operator|.
name|NMNullStateStoreService
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
name|security
operator|.
name|NMContainerTokenSecretManager
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
name|security
operator|.
name|NMTokenSecretManagerInNM
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
name|webapp
operator|.
name|ApplicationPage
operator|.
name|ApplicationBlock
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
name|test
operator|.
name|WebAppTests
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
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
name|Binder
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
name|Injector
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
name|Module
import|;
end_import

begin_class
annotation|@
name|RunWith
argument_list|(
name|Parameterized
operator|.
name|class
argument_list|)
DECL|class|TestNMAppsPage
specifier|public
class|class
name|TestNMAppsPage
block|{
DECL|field|applicationid
name|String
name|applicationid
decl_stmt|;
DECL|method|TestNMAppsPage (String appid)
specifier|public
name|TestNMAppsPage
parameter_list|(
name|String
name|appid
parameter_list|)
block|{
name|this
operator|.
name|applicationid
operator|=
name|appid
expr_stmt|;
block|}
annotation|@
name|Parameterized
operator|.
name|Parameters
DECL|method|getAppIds ()
specifier|public
specifier|static
name|Collection
argument_list|<
name|Object
index|[]
argument_list|>
name|getAppIds
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Object
index|[]
index|[]
block|{
block|{
literal|"appid"
block|}
block|,
block|{
literal|"application_123123213_0001"
block|}
block|,
block|{
literal|""
block|}
block|}
argument_list|)
return|;
block|}
annotation|@
name|Test
DECL|method|testNMAppsPage ()
specifier|public
name|void
name|testNMAppsPage
parameter_list|()
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
specifier|final
name|NMContext
name|nmcontext
init|=
operator|new
name|NMContext
argument_list|(
operator|new
name|NMContainerTokenSecretManager
argument_list|(
name|conf
argument_list|)
argument_list|,
operator|new
name|NMTokenSecretManagerInNM
argument_list|()
argument_list|,
literal|null
argument_list|,
operator|new
name|ApplicationACLsManager
argument_list|(
name|conf
argument_list|)
argument_list|,
operator|new
name|NMNullStateStoreService
argument_list|()
argument_list|)
decl_stmt|;
name|Injector
name|injector
init|=
name|WebAppTests
operator|.
name|createMockInjector
argument_list|(
name|NMContext
operator|.
name|class
argument_list|,
name|nmcontext
argument_list|,
operator|new
name|Module
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|configure
parameter_list|(
name|Binder
name|binder
parameter_list|)
block|{
name|NodeManager
name|nm
init|=
name|TestNMAppsPage
operator|.
name|mocknm
argument_list|(
name|nmcontext
argument_list|)
decl_stmt|;
name|binder
operator|.
name|bind
argument_list|(
name|NodeManager
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
name|nm
argument_list|)
expr_stmt|;
name|binder
operator|.
name|bind
argument_list|(
name|Context
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
name|nmcontext
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
decl_stmt|;
name|ApplicationBlock
name|instance
init|=
name|injector
operator|.
name|getInstance
argument_list|(
name|ApplicationBlock
operator|.
name|class
argument_list|)
decl_stmt|;
name|instance
operator|.
name|set
argument_list|(
name|YarnWebParams
operator|.
name|APPLICATION_ID
argument_list|,
name|applicationid
argument_list|)
expr_stmt|;
name|instance
operator|.
name|render
argument_list|()
expr_stmt|;
block|}
DECL|method|mocknm (NMContext nmcontext)
specifier|protected
specifier|static
name|NodeManager
name|mocknm
parameter_list|(
name|NMContext
name|nmcontext
parameter_list|)
block|{
name|NodeManager
name|rm
init|=
name|mock
argument_list|(
name|NodeManager
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|rm
operator|.
name|getNMContext
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|nmcontext
argument_list|)
expr_stmt|;
return|return
name|rm
return|;
block|}
block|}
end_class

end_unit

