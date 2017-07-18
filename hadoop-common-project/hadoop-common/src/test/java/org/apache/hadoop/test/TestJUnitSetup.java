begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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

begin_class
DECL|class|TestJUnitSetup
specifier|public
class|class
name|TestJUnitSetup
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestJUnitSetup
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Test
DECL|method|testJavaAssert ()
specifier|public
name|void
name|testJavaAssert
parameter_list|()
block|{
try|try
block|{
assert|assert
literal|false
operator|:
literal|"Good! Java assert is on."
assert|;
block|}
catch|catch
parameter_list|(
name|AssertionError
name|ae
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"The AssertionError is expected."
argument_list|,
name|ae
argument_list|)
expr_stmt|;
return|return;
block|}
name|Assert
operator|.
name|fail
argument_list|(
literal|"Java assert does not work."
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

