begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.shell
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|shell
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
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
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
comment|/**  * Test class to verify Tail shell command.  */
end_comment

begin_class
DECL|class|TestTail
specifier|public
class|class
name|TestTail
block|{
comment|// check follow delay with -s parameter.
annotation|@
name|Test
DECL|method|testSleepParameter ()
specifier|public
name|void
name|testSleepParameter
parameter_list|()
throws|throws
name|IOException
block|{
name|Tail
name|tail
init|=
operator|new
name|Tail
argument_list|()
decl_stmt|;
name|LinkedList
argument_list|<
name|String
argument_list|>
name|options
init|=
operator|new
name|LinkedList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|options
operator|.
name|add
argument_list|(
literal|"-f"
argument_list|)
expr_stmt|;
name|options
operator|.
name|add
argument_list|(
literal|"-s"
argument_list|)
expr_stmt|;
name|options
operator|.
name|add
argument_list|(
literal|"10000"
argument_list|)
expr_stmt|;
name|options
operator|.
name|add
argument_list|(
literal|"/path"
argument_list|)
expr_stmt|;
name|tail
operator|.
name|processOptions
argument_list|(
name|options
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|10000
argument_list|,
name|tail
operator|.
name|getFollowDelay
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// check follow delay without -s parameter.
annotation|@
name|Test
DECL|method|testFollowParameter ()
specifier|public
name|void
name|testFollowParameter
parameter_list|()
throws|throws
name|IOException
block|{
name|Tail
name|tail
init|=
operator|new
name|Tail
argument_list|()
decl_stmt|;
name|LinkedList
argument_list|<
name|String
argument_list|>
name|options
init|=
operator|new
name|LinkedList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|options
operator|.
name|add
argument_list|(
literal|"-f"
argument_list|)
expr_stmt|;
name|options
operator|.
name|add
argument_list|(
literal|"/path"
argument_list|)
expr_stmt|;
name|tail
operator|.
name|processOptions
argument_list|(
name|options
argument_list|)
expr_stmt|;
comment|// Follow delay should be the default 5000 ms.
name|assertEquals
argument_list|(
literal|5000
argument_list|,
name|tail
operator|.
name|getFollowDelay
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

