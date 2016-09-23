begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.federation.policies
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
name|federation
operator|.
name|policies
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
name|server
operator|.
name|federation
operator|.
name|policies
operator|.
name|amrmproxy
operator|.
name|FederationAMRMProxyPolicy
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
name|federation
operator|.
name|policies
operator|.
name|exceptions
operator|.
name|FederationPolicyInitializationException
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
name|federation
operator|.
name|policies
operator|.
name|router
operator|.
name|FederationRouterPolicy
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
name|federation
operator|.
name|resolver
operator|.
name|SubClusterResolver
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
name|federation
operator|.
name|store
operator|.
name|records
operator|.
name|SubClusterPolicyConfiguration
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
name|federation
operator|.
name|utils
operator|.
name|FederationPoliciesTestUtil
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
name|federation
operator|.
name|utils
operator|.
name|FederationStateStoreFacade
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
import|;
end_import

begin_comment
comment|/**  * Test class for {@link FederationPolicyInitializationContextValidator}.  */
end_comment

begin_class
DECL|class|TestFederationPolicyInitializationContextValidator
specifier|public
class|class
name|TestFederationPolicyInitializationContextValidator
block|{
DECL|field|goodConfig
specifier|private
name|SubClusterPolicyConfiguration
name|goodConfig
decl_stmt|;
DECL|field|goodSR
specifier|private
name|SubClusterResolver
name|goodSR
decl_stmt|;
DECL|field|goodFacade
specifier|private
name|FederationStateStoreFacade
name|goodFacade
decl_stmt|;
DECL|field|context
specifier|private
name|FederationPolicyInitializationContext
name|context
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|goodFacade
operator|=
name|FederationPoliciesTestUtil
operator|.
name|initFacade
argument_list|()
expr_stmt|;
name|goodConfig
operator|=
operator|new
name|MockPolicyManager
argument_list|()
operator|.
name|serializeConf
argument_list|()
expr_stmt|;
name|goodSR
operator|=
name|FederationPoliciesTestUtil
operator|.
name|initResolver
argument_list|()
expr_stmt|;
name|context
operator|=
operator|new
name|FederationPolicyInitializationContext
argument_list|(
name|goodConfig
argument_list|,
name|goodSR
argument_list|,
name|goodFacade
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|correcInit ()
specifier|public
name|void
name|correcInit
parameter_list|()
throws|throws
name|Exception
block|{
name|FederationPolicyInitializationContextValidator
operator|.
name|validate
argument_list|(
name|context
argument_list|,
name|MockPolicyManager
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|FederationPolicyInitializationException
operator|.
name|class
argument_list|)
DECL|method|nullContext ()
specifier|public
name|void
name|nullContext
parameter_list|()
throws|throws
name|Exception
block|{
name|FederationPolicyInitializationContextValidator
operator|.
name|validate
argument_list|(
literal|null
argument_list|,
name|MockPolicyManager
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|FederationPolicyInitializationException
operator|.
name|class
argument_list|)
DECL|method|nullType ()
specifier|public
name|void
name|nullType
parameter_list|()
throws|throws
name|Exception
block|{
name|FederationPolicyInitializationContextValidator
operator|.
name|validate
argument_list|(
name|context
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|FederationPolicyInitializationException
operator|.
name|class
argument_list|)
DECL|method|wrongType ()
specifier|public
name|void
name|wrongType
parameter_list|()
throws|throws
name|Exception
block|{
name|FederationPolicyInitializationContextValidator
operator|.
name|validate
argument_list|(
name|context
argument_list|,
literal|"WrongType"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|FederationPolicyInitializationException
operator|.
name|class
argument_list|)
DECL|method|nullConf ()
specifier|public
name|void
name|nullConf
parameter_list|()
throws|throws
name|Exception
block|{
name|context
operator|.
name|setSubClusterPolicyConfiguration
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|FederationPolicyInitializationContextValidator
operator|.
name|validate
argument_list|(
name|context
argument_list|,
name|MockPolicyManager
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|FederationPolicyInitializationException
operator|.
name|class
argument_list|)
DECL|method|nullResolver ()
specifier|public
name|void
name|nullResolver
parameter_list|()
throws|throws
name|Exception
block|{
name|context
operator|.
name|setFederationSubclusterResolver
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|FederationPolicyInitializationContextValidator
operator|.
name|validate
argument_list|(
name|context
argument_list|,
name|MockPolicyManager
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|FederationPolicyInitializationException
operator|.
name|class
argument_list|)
DECL|method|nullFacade ()
specifier|public
name|void
name|nullFacade
parameter_list|()
throws|throws
name|Exception
block|{
name|context
operator|.
name|setFederationStateStoreFacade
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|FederationPolicyInitializationContextValidator
operator|.
name|validate
argument_list|(
name|context
argument_list|,
name|MockPolicyManager
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|class|MockPolicyManager
specifier|private
class|class
name|MockPolicyManager
implements|implements
name|FederationPolicyManager
block|{
annotation|@
name|Override
DECL|method|getAMRMPolicy ( FederationPolicyInitializationContext federationPolicyInitializationContext, FederationAMRMProxyPolicy oldInstance)
specifier|public
name|FederationAMRMProxyPolicy
name|getAMRMPolicy
parameter_list|(
name|FederationPolicyInitializationContext
name|federationPolicyInitializationContext
parameter_list|,
name|FederationAMRMProxyPolicy
name|oldInstance
parameter_list|)
throws|throws
name|FederationPolicyInitializationException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getRouterPolicy ( FederationPolicyInitializationContext federationPolicyInitializationContext, FederationRouterPolicy oldInstance)
specifier|public
name|FederationRouterPolicy
name|getRouterPolicy
parameter_list|(
name|FederationPolicyInitializationContext
name|federationPolicyInitializationContext
parameter_list|,
name|FederationRouterPolicy
name|oldInstance
parameter_list|)
throws|throws
name|FederationPolicyInitializationException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|serializeConf ()
specifier|public
name|SubClusterPolicyConfiguration
name|serializeConf
parameter_list|()
throws|throws
name|FederationPolicyInitializationException
block|{
name|ByteBuffer
name|buf
init|=
name|ByteBuffer
operator|.
name|allocate
argument_list|(
literal|0
argument_list|)
decl_stmt|;
return|return
name|SubClusterPolicyConfiguration
operator|.
name|newInstance
argument_list|(
literal|"queue1"
argument_list|,
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getCanonicalName
argument_list|()
argument_list|,
name|buf
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getQueue ()
specifier|public
name|String
name|getQueue
parameter_list|()
block|{
return|return
literal|"default"
return|;
block|}
annotation|@
name|Override
DECL|method|setQueue (String queue)
specifier|public
name|void
name|setQueue
parameter_list|(
name|String
name|queue
parameter_list|)
block|{      }
block|}
block|}
end_class

end_unit

