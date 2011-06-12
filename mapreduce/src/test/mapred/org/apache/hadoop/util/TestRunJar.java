begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|fs
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_comment
comment|/**  * A test to rest the RunJar class.  */
end_comment

begin_class
DECL|class|TestRunJar
specifier|public
class|class
name|TestRunJar
extends|extends
name|TestCase
block|{
DECL|field|TEST_ROOT_DIR
specifier|private
specifier|static
name|String
name|TEST_ROOT_DIR
init|=
operator|new
name|Path
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.data"
argument_list|,
literal|"/tmp"
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
DECL|method|testRunjar ()
specifier|public
name|void
name|testRunjar
parameter_list|()
throws|throws
name|Throwable
block|{
name|File
name|outFile
init|=
operator|new
name|File
argument_list|(
name|TEST_ROOT_DIR
argument_list|,
literal|"out"
argument_list|)
decl_stmt|;
comment|// delete if output file already exists.
if|if
condition|(
name|outFile
operator|.
name|exists
argument_list|()
condition|)
block|{
name|outFile
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
name|String
index|[]
name|args
init|=
operator|new
name|String
index|[
literal|3
index|]
decl_stmt|;
name|args
index|[
literal|0
index|]
operator|=
literal|"build/test/mapred/testjar/testjob.jar"
expr_stmt|;
name|args
index|[
literal|1
index|]
operator|=
literal|"testjar.Hello"
expr_stmt|;
name|args
index|[
literal|2
index|]
operator|=
name|outFile
operator|.
name|toString
argument_list|()
expr_stmt|;
name|RunJar
operator|.
name|main
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"RunJar failed"
argument_list|,
name|outFile
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

