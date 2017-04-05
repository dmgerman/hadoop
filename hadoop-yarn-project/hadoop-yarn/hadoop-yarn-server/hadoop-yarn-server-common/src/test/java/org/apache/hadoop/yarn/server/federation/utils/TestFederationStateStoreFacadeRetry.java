begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.federation.utils
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
name|utils
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|cache
operator|.
name|integration
operator|.
name|CacheLoaderException
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
name|io
operator|.
name|retry
operator|.
name|RetryPolicy
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
name|io
operator|.
name|retry
operator|.
name|RetryPolicy
operator|.
name|RetryAction
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
name|federation
operator|.
name|store
operator|.
name|exception
operator|.
name|FederationStateStoreErrorCode
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
name|exception
operator|.
name|FederationStateStoreException
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
name|exception
operator|.
name|FederationStateStoreInvalidInputException
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
name|exception
operator|.
name|FederationStateStoreRetriableException
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

begin_comment
comment|/**  * Test class to validate FederationStateStoreFacade retry policy.  */
end_comment

begin_class
DECL|class|TestFederationStateStoreFacadeRetry
specifier|public
class|class
name|TestFederationStateStoreFacadeRetry
block|{
DECL|field|maxRetries
specifier|private
name|int
name|maxRetries
init|=
literal|4
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
comment|/*    * Test to validate that FederationStateStoreRetriableException is a retriable    * exception.    */
annotation|@
name|Test
DECL|method|testFacadeRetriableException ()
specifier|public
name|void
name|testFacadeRetriableException
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|YarnConfiguration
operator|.
name|CLIENT_FAILOVER_RETRIES
argument_list|,
name|maxRetries
argument_list|)
expr_stmt|;
name|RetryPolicy
name|policy
init|=
name|FederationStateStoreFacade
operator|.
name|createRetryPolicy
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|RetryAction
name|action
init|=
name|policy
operator|.
name|shouldRetry
argument_list|(
operator|new
name|FederationStateStoreRetriableException
argument_list|(
literal|""
argument_list|)
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|)
decl_stmt|;
comment|// We compare only the action, since delay and the reason are random values
comment|// during this test
name|Assert
operator|.
name|assertEquals
argument_list|(
name|RetryAction
operator|.
name|RETRY
operator|.
name|action
argument_list|,
name|action
operator|.
name|action
argument_list|)
expr_stmt|;
comment|// After maxRetries we stop to retry
name|action
operator|=
name|policy
operator|.
name|shouldRetry
argument_list|(
operator|new
name|FederationStateStoreRetriableException
argument_list|(
literal|""
argument_list|)
argument_list|,
name|maxRetries
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|RetryAction
operator|.
name|FAIL
operator|.
name|action
argument_list|,
name|action
operator|.
name|action
argument_list|)
expr_stmt|;
block|}
comment|/*    * Test to validate that YarnException is not a retriable exception.    */
annotation|@
name|Test
DECL|method|testFacadeYarnException ()
specifier|public
name|void
name|testFacadeYarnException
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|YarnConfiguration
operator|.
name|CLIENT_FAILOVER_RETRIES
argument_list|,
name|maxRetries
argument_list|)
expr_stmt|;
name|RetryPolicy
name|policy
init|=
name|FederationStateStoreFacade
operator|.
name|createRetryPolicy
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|RetryAction
name|action
init|=
name|policy
operator|.
name|shouldRetry
argument_list|(
operator|new
name|YarnException
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|RetryAction
operator|.
name|FAIL
operator|.
name|action
argument_list|,
name|action
operator|.
name|action
argument_list|)
expr_stmt|;
block|}
comment|/*    * Test to validate that FederationStateStoreException is not a retriable    * exception.    */
annotation|@
name|Test
DECL|method|testFacadeStateStoreException ()
specifier|public
name|void
name|testFacadeStateStoreException
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|YarnConfiguration
operator|.
name|CLIENT_FAILOVER_RETRIES
argument_list|,
name|maxRetries
argument_list|)
expr_stmt|;
name|RetryPolicy
name|policy
init|=
name|FederationStateStoreFacade
operator|.
name|createRetryPolicy
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|RetryAction
name|action
init|=
name|policy
operator|.
name|shouldRetry
argument_list|(
operator|new
name|FederationStateStoreException
argument_list|(
name|FederationStateStoreErrorCode
operator|.
name|APPLICATIONS_INSERT_FAIL
argument_list|)
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|RetryAction
operator|.
name|FAIL
operator|.
name|action
argument_list|,
name|action
operator|.
name|action
argument_list|)
expr_stmt|;
block|}
comment|/*    * Test to validate that FederationStateStoreInvalidInputException is not a    * retriable exception.    */
annotation|@
name|Test
DECL|method|testFacadeInvalidInputException ()
specifier|public
name|void
name|testFacadeInvalidInputException
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|YarnConfiguration
operator|.
name|CLIENT_FAILOVER_RETRIES
argument_list|,
name|maxRetries
argument_list|)
expr_stmt|;
name|RetryPolicy
name|policy
init|=
name|FederationStateStoreFacade
operator|.
name|createRetryPolicy
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|RetryAction
name|action
init|=
name|policy
operator|.
name|shouldRetry
argument_list|(
operator|new
name|FederationStateStoreInvalidInputException
argument_list|(
literal|""
argument_list|)
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|RetryAction
operator|.
name|FAIL
operator|.
name|action
argument_list|,
name|action
operator|.
name|action
argument_list|)
expr_stmt|;
block|}
comment|/*    * Test to validate that CacheLoaderException is a retriable exception.    */
annotation|@
name|Test
DECL|method|testFacadeCacheRetriableException ()
specifier|public
name|void
name|testFacadeCacheRetriableException
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|YarnConfiguration
operator|.
name|CLIENT_FAILOVER_RETRIES
argument_list|,
name|maxRetries
argument_list|)
expr_stmt|;
name|RetryPolicy
name|policy
init|=
name|FederationStateStoreFacade
operator|.
name|createRetryPolicy
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|RetryAction
name|action
init|=
name|policy
operator|.
name|shouldRetry
argument_list|(
operator|new
name|CacheLoaderException
argument_list|(
literal|""
argument_list|)
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|)
decl_stmt|;
comment|// We compare only the action, since delay and the reason are random values
comment|// during this test
name|Assert
operator|.
name|assertEquals
argument_list|(
name|RetryAction
operator|.
name|RETRY
operator|.
name|action
argument_list|,
name|action
operator|.
name|action
argument_list|)
expr_stmt|;
comment|// After maxRetries we stop to retry
name|action
operator|=
name|policy
operator|.
name|shouldRetry
argument_list|(
operator|new
name|CacheLoaderException
argument_list|(
literal|""
argument_list|)
argument_list|,
name|maxRetries
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|RetryAction
operator|.
name|FAIL
operator|.
name|action
argument_list|,
name|action
operator|.
name|action
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

