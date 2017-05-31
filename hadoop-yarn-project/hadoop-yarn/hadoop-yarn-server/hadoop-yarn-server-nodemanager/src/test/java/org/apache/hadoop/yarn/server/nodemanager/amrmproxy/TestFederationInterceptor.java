begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.amrmproxy
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
name|amrmproxy
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|protocolrecords
operator|.
name|RegisterApplicationMasterRequest
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
name|protocolrecords
operator|.
name|RegisterApplicationMasterResponse
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
name|ApplicationAttemptId
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
name|exceptions
operator|.
name|InvalidApplicationMasterRequestException
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
name|util
operator|.
name|Records
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
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
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * Extends the TestAMRMProxyService and overrides methods in order to use the  * AMRMProxyService's pipeline test cases for testing the FederationInterceptor  * class. The tests for AMRMProxyService has been written cleverly so that it  * can be reused to validate different request intercepter chains.  */
end_comment

begin_class
DECL|class|TestFederationInterceptor
specifier|public
class|class
name|TestFederationInterceptor
extends|extends
name|BaseAMRMProxyTest
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestFederationInterceptor
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|HOME_SC_ID
specifier|public
specifier|static
specifier|final
name|String
name|HOME_SC_ID
init|=
literal|"SC-home"
decl_stmt|;
DECL|field|interceptor
specifier|private
name|TestableFederationInterceptor
name|interceptor
decl_stmt|;
DECL|field|testAppId
specifier|private
name|int
name|testAppId
decl_stmt|;
DECL|field|attemptId
specifier|private
name|ApplicationAttemptId
name|attemptId
decl_stmt|;
annotation|@
name|Override
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|interceptor
operator|=
operator|new
name|TestableFederationInterceptor
argument_list|()
expr_stmt|;
name|testAppId
operator|=
literal|1
expr_stmt|;
name|attemptId
operator|=
name|getApplicationAttemptId
argument_list|(
name|testAppId
argument_list|)
expr_stmt|;
name|interceptor
operator|.
name|init
argument_list|(
operator|new
name|AMRMProxyApplicationContextImpl
argument_list|(
literal|null
argument_list|,
name|getConf
argument_list|()
argument_list|,
name|attemptId
argument_list|,
literal|"test-user"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
block|{
name|interceptor
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createConfiguration ()
specifier|protected
name|YarnConfiguration
name|createConfiguration
parameter_list|()
block|{
name|YarnConfiguration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|AMRM_PROXY_ENABLED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|FEDERATION_ENABLED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|String
name|mockPassThroughInterceptorClass
init|=
name|PassThroughRequestInterceptor
operator|.
name|class
operator|.
name|getName
argument_list|()
decl_stmt|;
comment|// Create a request intercepter pipeline for testing. The last one in the
comment|// chain is the federation intercepter that calls the mock resource manager.
comment|// The others in the chain will simply forward it to the next one in the
comment|// chain
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|AMRM_PROXY_INTERCEPTOR_CLASS_PIPELINE
argument_list|,
name|mockPassThroughInterceptorClass
operator|+
literal|","
operator|+
name|mockPassThroughInterceptorClass
operator|+
literal|","
operator|+
name|TestableFederationInterceptor
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_CLUSTER_ID
argument_list|,
name|HOME_SC_ID
argument_list|)
expr_stmt|;
return|return
name|conf
return|;
block|}
annotation|@
name|Test
DECL|method|testRequestInterceptorChainCreation ()
specifier|public
name|void
name|testRequestInterceptorChainCreation
parameter_list|()
throws|throws
name|Exception
block|{
name|RequestInterceptor
name|root
init|=
name|super
operator|.
name|getAMRMProxyService
argument_list|()
operator|.
name|createRequestInterceptorChain
argument_list|()
decl_stmt|;
name|int
name|index
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|root
operator|!=
literal|null
condition|)
block|{
switch|switch
condition|(
name|index
condition|)
block|{
case|case
literal|0
case|:
case|case
literal|1
case|:
name|Assert
operator|.
name|assertEquals
argument_list|(
name|PassThroughRequestInterceptor
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|root
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
literal|2
case|:
name|Assert
operator|.
name|assertEquals
argument_list|(
name|TestableFederationInterceptor
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|root
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
break|break;
default|default:
name|Assert
operator|.
name|fail
argument_list|()
expr_stmt|;
block|}
name|root
operator|=
name|root
operator|.
name|getNextInterceptor
argument_list|()
expr_stmt|;
name|index
operator|++
expr_stmt|;
block|}
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"The number of interceptors in chain does not match"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
literal|3
argument_list|)
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|index
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Between AM and AMRMProxy, FederationInterceptor modifies the RM behavior,    * so that when AM registers more than once, it returns the same register    * success response instead of throwing    * {@link InvalidApplicationMasterRequestException}    *    * We did this because FederationInterceptor can receive concurrent register    * requests from AM because of timeout between AM and AMRMProxy. This can    * possible since the timeout between FederationInterceptor and RM longer    * because of performFailover + timeout.    */
annotation|@
name|Test
DECL|method|testTwoIdenticalRegisterRequest ()
specifier|public
name|void
name|testTwoIdenticalRegisterRequest
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Register the application twice
name|RegisterApplicationMasterRequest
name|registerReq
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|RegisterApplicationMasterRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|registerReq
operator|.
name|setHost
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|testAppId
argument_list|)
argument_list|)
expr_stmt|;
name|registerReq
operator|.
name|setRpcPort
argument_list|(
name|testAppId
argument_list|)
expr_stmt|;
name|registerReq
operator|.
name|setTrackingUrl
argument_list|(
literal|""
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|RegisterApplicationMasterResponse
name|registerResponse
init|=
name|interceptor
operator|.
name|registerApplicationMaster
argument_list|(
name|registerReq
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|registerResponse
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testTwoDifferentRegisterRequest ()
specifier|public
name|void
name|testTwoDifferentRegisterRequest
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Register the application first time
name|RegisterApplicationMasterRequest
name|registerReq
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|RegisterApplicationMasterRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|registerReq
operator|.
name|setHost
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|testAppId
argument_list|)
argument_list|)
expr_stmt|;
name|registerReq
operator|.
name|setRpcPort
argument_list|(
name|testAppId
argument_list|)
expr_stmt|;
name|registerReq
operator|.
name|setTrackingUrl
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|RegisterApplicationMasterResponse
name|registerResponse
init|=
name|interceptor
operator|.
name|registerApplicationMaster
argument_list|(
name|registerReq
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|registerResponse
argument_list|)
expr_stmt|;
comment|// Register the application second time with a different request obj
name|registerReq
operator|=
name|Records
operator|.
name|newRecord
argument_list|(
name|RegisterApplicationMasterRequest
operator|.
name|class
argument_list|)
expr_stmt|;
name|registerReq
operator|.
name|setHost
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|testAppId
argument_list|)
argument_list|)
expr_stmt|;
name|registerReq
operator|.
name|setRpcPort
argument_list|(
name|testAppId
argument_list|)
expr_stmt|;
name|registerReq
operator|.
name|setTrackingUrl
argument_list|(
literal|"different"
argument_list|)
expr_stmt|;
try|try
block|{
name|registerResponse
operator|=
name|interceptor
operator|.
name|registerApplicationMaster
argument_list|(
name|registerReq
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Should throw if a different request obj is used"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnException
name|e
parameter_list|)
block|{     }
block|}
block|}
end_class

end_unit

