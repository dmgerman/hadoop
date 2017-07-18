begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.http
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|http
package|;
end_package

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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
import|;
end_import

begin_comment
comment|/**  * Test webapp loading  */
end_comment

begin_class
DECL|class|TestHttpServerWebapps
specifier|public
class|class
name|TestHttpServerWebapps
extends|extends
name|HttpServerFunctionalTest
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
name|TestHttpServerWebapps
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Test that the test server is loadable on the classpath    * @throws Throwable if something went wrong    */
annotation|@
name|Test
DECL|method|testValidServerResource ()
specifier|public
name|void
name|testValidServerResource
parameter_list|()
throws|throws
name|Throwable
block|{
name|HttpServer2
name|server
init|=
literal|null
decl_stmt|;
try|try
block|{
name|server
operator|=
name|createServer
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|stop
argument_list|(
name|server
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Test that an invalid webapp triggers an exception    * @throws Throwable if something went wrong    */
annotation|@
name|Test
DECL|method|testMissingServerResource ()
specifier|public
name|void
name|testMissingServerResource
parameter_list|()
throws|throws
name|Throwable
block|{
try|try
block|{
name|HttpServer2
name|server
init|=
name|createServer
argument_list|(
literal|"NoSuchWebapp"
argument_list|)
decl_stmt|;
comment|//should not have got here.
comment|//close the server
name|String
name|serverDescription
init|=
name|server
operator|.
name|toString
argument_list|()
decl_stmt|;
name|stop
argument_list|(
name|server
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected an exception, got "
operator|+
name|serverDescription
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|expected
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Expected exception "
operator|+
name|expected
argument_list|,
name|expected
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

