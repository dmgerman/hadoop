begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.streaming
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|streaming
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
name|File
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

begin_comment
comment|/**  * This class tests if hadoopStreaming returns Exception   * on failure when submitted an invalid/failed job  * The test case provides an invalid input file for map/reduce job as  * a unit test case  */
end_comment

begin_class
DECL|class|TestStreamingFailure
specifier|public
class|class
name|TestStreamingFailure
extends|extends
name|TestStreaming
block|{
DECL|field|INVALID_INPUT_FILE
specifier|protected
name|File
name|INVALID_INPUT_FILE
decl_stmt|;
DECL|method|TestStreamingFailure ()
specifier|public
name|TestStreamingFailure
parameter_list|()
throws|throws
name|IOException
block|{
name|INVALID_INPUT_FILE
operator|=
operator|new
name|File
argument_list|(
literal|"invalid_input.txt"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setInputOutput ()
specifier|protected
name|void
name|setInputOutput
parameter_list|()
block|{
name|inputFile
operator|=
name|INVALID_INPUT_FILE
operator|.
name|getAbsolutePath
argument_list|()
expr_stmt|;
name|outDir
operator|=
name|OUTPUT_DIR
operator|.
name|getAbsolutePath
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|Test
DECL|method|testCommandLine ()
specifier|public
name|void
name|testCommandLine
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|returnStatus
init|=
name|runStreamJob
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Streaming Job Failure code expected"
argument_list|,
literal|5
argument_list|,
name|returnStatus
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

