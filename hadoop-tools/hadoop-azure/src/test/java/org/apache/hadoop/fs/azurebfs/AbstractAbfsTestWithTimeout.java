begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.azurebfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|azurebfs
package|;
end_package

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
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
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
name|rules
operator|.
name|TestName
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
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|azurebfs
operator|.
name|constants
operator|.
name|TestConfigurationKeys
operator|.
name|TEST_TIMEOUT
import|;
end_import

begin_comment
comment|/**  * Base class for any ABFS test with timeouts& named threads.  * This class does not attempt to bind to Azure.  */
end_comment

begin_class
DECL|class|AbstractAbfsTestWithTimeout
specifier|public
class|class
name|AbstractAbfsTestWithTimeout
extends|extends
name|Assert
block|{
comment|/**    * The name of the current method.    */
annotation|@
name|Rule
DECL|field|methodName
specifier|public
name|TestName
name|methodName
init|=
operator|new
name|TestName
argument_list|()
decl_stmt|;
comment|/**    * Set the timeout for every test.    * This is driven by the value returned by {@link #getTestTimeoutMillis()}.    */
annotation|@
name|Rule
DECL|field|testTimeout
specifier|public
name|Timeout
name|testTimeout
init|=
operator|new
name|Timeout
argument_list|(
name|getTestTimeoutMillis
argument_list|()
argument_list|)
decl_stmt|;
comment|/**    * Name the junit thread for the class. This will overridden    * before the individual test methods are run.    */
annotation|@
name|BeforeClass
DECL|method|nameTestThread ()
specifier|public
specifier|static
name|void
name|nameTestThread
parameter_list|()
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|setName
argument_list|(
literal|"JUnit"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Name the thread to the current test method.    */
annotation|@
name|Before
DECL|method|nameThread ()
specifier|public
name|void
name|nameThread
parameter_list|()
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|setName
argument_list|(
literal|"JUnit-"
operator|+
name|methodName
operator|.
name|getMethodName
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Override point: the test timeout in milliseconds.    * @return a timeout in milliseconds    */
DECL|method|getTestTimeoutMillis ()
specifier|protected
name|int
name|getTestTimeoutMillis
parameter_list|()
block|{
return|return
name|TEST_TIMEOUT
return|;
block|}
block|}
end_class

end_unit

