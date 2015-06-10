begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p/>  * http://www.apache.org/licenses/LICENSE-2.0  *<p/>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.io.retry
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|retry
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
name|ipc
operator|.
name|RemoteException
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
name|ipc
operator|.
name|RetriableException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
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
name|rules
operator|.
name|Timeout
import|;
end_import

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
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|core
operator|.
name|Is
operator|.
name|is
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertThat
import|;
end_import

begin_comment
comment|/**  * Test the behavior of the default retry policy.  */
end_comment

begin_class
DECL|class|TestDefaultRetryPolicy
specifier|public
class|class
name|TestDefaultRetryPolicy
block|{
annotation|@
name|Rule
DECL|field|timeout
specifier|public
name|Timeout
name|timeout
init|=
operator|new
name|Timeout
argument_list|(
literal|300000
argument_list|)
decl_stmt|;
comment|/**    * Verify that the default retry policy correctly retries    * RetriableException when defaultRetryPolicyEnabled is enabled.    *    * @throws IOException    */
annotation|@
name|Test
DECL|method|testWithRetriable ()
specifier|public
name|void
name|testWithRetriable
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|RetryPolicy
name|policy
init|=
name|RetryUtils
operator|.
name|getDefaultRetryPolicy
argument_list|(
name|conf
argument_list|,
literal|"Test.No.Such.Key"
argument_list|,
literal|true
argument_list|,
comment|// defaultRetryPolicyEnabled = true
literal|"Test.No.Such.Key"
argument_list|,
literal|"10000,6"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|RetryPolicy
operator|.
name|RetryAction
name|action
init|=
name|policy
operator|.
name|shouldRetry
argument_list|(
operator|new
name|RetriableException
argument_list|(
literal|"Dummy exception"
argument_list|)
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|action
operator|.
name|action
argument_list|,
name|is
argument_list|(
name|RetryPolicy
operator|.
name|RetryAction
operator|.
name|RetryDecision
operator|.
name|RETRY
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Verify that the default retry policy correctly retries    * a RetriableException wrapped in a RemoteException when    * defaultRetryPolicyEnabled is enabled.    *    * @throws IOException    */
annotation|@
name|Test
DECL|method|testWithWrappedRetriable ()
specifier|public
name|void
name|testWithWrappedRetriable
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|RetryPolicy
name|policy
init|=
name|RetryUtils
operator|.
name|getDefaultRetryPolicy
argument_list|(
name|conf
argument_list|,
literal|"Test.No.Such.Key"
argument_list|,
literal|true
argument_list|,
comment|// defaultRetryPolicyEnabled = true
literal|"Test.No.Such.Key"
argument_list|,
literal|"10000,6"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|RetryPolicy
operator|.
name|RetryAction
name|action
init|=
name|policy
operator|.
name|shouldRetry
argument_list|(
operator|new
name|RemoteException
argument_list|(
name|RetriableException
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
literal|"Dummy exception"
argument_list|)
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|action
operator|.
name|action
argument_list|,
name|is
argument_list|(
name|RetryPolicy
operator|.
name|RetryAction
operator|.
name|RetryDecision
operator|.
name|RETRY
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Verify that the default retry policy does *not* retry    * RetriableException when defaultRetryPolicyEnabled is disabled.    *    * @throws IOException    */
annotation|@
name|Test
DECL|method|testWithRetriableAndRetryDisabled ()
specifier|public
name|void
name|testWithRetriableAndRetryDisabled
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|RetryPolicy
name|policy
init|=
name|RetryUtils
operator|.
name|getDefaultRetryPolicy
argument_list|(
name|conf
argument_list|,
literal|"Test.No.Such.Key"
argument_list|,
literal|false
argument_list|,
comment|// defaultRetryPolicyEnabled = false
literal|"Test.No.Such.Key"
argument_list|,
literal|"10000,6"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|RetryPolicy
operator|.
name|RetryAction
name|action
init|=
name|policy
operator|.
name|shouldRetry
argument_list|(
operator|new
name|RetriableException
argument_list|(
literal|"Dummy exception"
argument_list|)
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|action
operator|.
name|action
argument_list|,
name|is
argument_list|(
name|RetryPolicy
operator|.
name|RetryAction
operator|.
name|RetryDecision
operator|.
name|FAIL
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

