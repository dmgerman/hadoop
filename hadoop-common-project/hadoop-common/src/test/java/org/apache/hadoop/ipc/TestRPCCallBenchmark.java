begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ipc
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ipc
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
name|*
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
name|util
operator|.
name|ToolRunner
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

begin_class
DECL|class|TestRPCCallBenchmark
specifier|public
class|class
name|TestRPCCallBenchmark
block|{
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|20000
argument_list|)
DECL|method|testBenchmarkWithWritable ()
specifier|public
name|void
name|testBenchmarkWithWritable
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|rc
init|=
name|ToolRunner
operator|.
name|run
argument_list|(
operator|new
name|RPCCallBenchmark
argument_list|()
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"--clientThreads"
block|,
literal|"30"
block|,
literal|"--serverThreads"
block|,
literal|"30"
block|,
literal|"--time"
block|,
literal|"5"
block|,
literal|"--serverReaderThreads"
block|,
literal|"4"
block|,
literal|"--messageSize"
block|,
literal|"1024"
block|,
literal|"--engine"
block|,
literal|"writable"
block|}
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|rc
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|20000
argument_list|)
DECL|method|testBenchmarkWithProto ()
specifier|public
name|void
name|testBenchmarkWithProto
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|rc
init|=
name|ToolRunner
operator|.
name|run
argument_list|(
operator|new
name|RPCCallBenchmark
argument_list|()
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"--clientThreads"
block|,
literal|"30"
block|,
literal|"--serverThreads"
block|,
literal|"30"
block|,
literal|"--time"
block|,
literal|"5"
block|,
literal|"--serverReaderThreads"
block|,
literal|"4"
block|,
literal|"--messageSize"
block|,
literal|"1024"
block|,
literal|"--engine"
block|,
literal|"protobuf"
block|}
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|rc
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

