begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.test
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|test
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
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

begin_comment
comment|/**  * A base class for JUnit5+ tests that sets a default timeout for all tests  * that subclass this test.  *  * Threads are named to the method being executed, for ease of diagnostics  * in logs and thread dumps.  *  * Unlike {@link HadoopTestBase} this class does not extend JUnit Assert  * so is easier to use with AssertJ.  */
end_comment

begin_class
DECL|class|AbstractHadoopTestBase
specifier|public
specifier|abstract
class|class
name|AbstractHadoopTestBase
block|{
comment|/**    * System property name to set the test timeout: {@value}.    */
DECL|field|PROPERTY_TEST_DEFAULT_TIMEOUT
specifier|public
specifier|static
specifier|final
name|String
name|PROPERTY_TEST_DEFAULT_TIMEOUT
init|=
literal|"test.default.timeout"
decl_stmt|;
comment|/**    * The default timeout (in milliseconds) if the system property    * {@link #PROPERTY_TEST_DEFAULT_TIMEOUT}    * is not set: {@value}.    */
DECL|field|TEST_DEFAULT_TIMEOUT_VALUE
specifier|public
specifier|static
specifier|final
name|int
name|TEST_DEFAULT_TIMEOUT_VALUE
init|=
literal|100000
decl_stmt|;
comment|/**    * The JUnit rule that sets the default timeout for tests.    */
annotation|@
name|Rule
DECL|field|defaultTimeout
specifier|public
name|Timeout
name|defaultTimeout
init|=
name|retrieveTestTimeout
argument_list|()
decl_stmt|;
comment|/**    * Retrieve the test timeout from the system property    * {@link #PROPERTY_TEST_DEFAULT_TIMEOUT}, falling back to    * the value in {@link #TEST_DEFAULT_TIMEOUT_VALUE} if the    * property is not defined.    * @return the recommended timeout for tests    */
DECL|method|retrieveTestTimeout ()
specifier|public
specifier|static
name|Timeout
name|retrieveTestTimeout
parameter_list|()
block|{
name|String
name|propval
init|=
name|System
operator|.
name|getProperty
argument_list|(
name|PROPERTY_TEST_DEFAULT_TIMEOUT
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|TEST_DEFAULT_TIMEOUT_VALUE
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|millis
decl_stmt|;
try|try
block|{
name|millis
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|propval
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
comment|//fall back to the default value, as the property cannot be parsed
name|millis
operator|=
name|TEST_DEFAULT_TIMEOUT_VALUE
expr_stmt|;
block|}
return|return
operator|new
name|Timeout
argument_list|(
name|millis
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
return|;
block|}
comment|/**    * The method name.    */
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
comment|/**    * Get the method name; defaults to the value of {@link #methodName}.    * Subclasses may wish to override it, which will tune the thread naming.    * @return the name of the method.    */
DECL|method|getMethodName ()
specifier|protected
name|String
name|getMethodName
parameter_list|()
block|{
return|return
name|methodName
operator|.
name|getMethodName
argument_list|()
return|;
block|}
comment|/**    * Static initializer names this thread "JUnit".    */
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
comment|/**    * Before each method, the thread is renamed to match the method name.    */
annotation|@
name|Before
DECL|method|nameThreadToMethod ()
specifier|public
name|void
name|nameThreadToMethod
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
name|getMethodName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

