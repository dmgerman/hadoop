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
name|FileOutputStream
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
name|zip
operator|.
name|GZIPOutputStream
import|;
end_import

begin_comment
comment|/**  * This class tests gzip input streaming in MapReduce local mode.  */
end_comment

begin_class
DECL|class|TestGzipInput
specifier|public
class|class
name|TestGzipInput
extends|extends
name|TestStreaming
block|{
DECL|method|TestGzipInput ()
specifier|public
name|TestGzipInput
parameter_list|()
throws|throws
name|IOException
block|{
name|INPUT_FILE
operator|=
operator|new
name|File
argument_list|(
name|TEST_DIR
argument_list|,
literal|"input.txt.gz"
argument_list|)
expr_stmt|;
block|}
DECL|method|createInput ()
specifier|protected
name|void
name|createInput
parameter_list|()
throws|throws
name|IOException
block|{
name|GZIPOutputStream
name|out
init|=
operator|new
name|GZIPOutputStream
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
name|INPUT_FILE
operator|.
name|getAbsoluteFile
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|out
operator|.
name|write
argument_list|(
name|input
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

