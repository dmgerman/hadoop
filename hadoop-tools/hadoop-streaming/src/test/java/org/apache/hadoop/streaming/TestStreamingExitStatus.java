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
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
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
name|fs
operator|.
name|FileSystem
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

begin_comment
comment|/**  * This class tests if hadoopStreaming fails a job when the mapper or  * reducers have non-zero exit status and the  * stream.non.zero.exit.status.is.failure jobconf is set.  */
end_comment

begin_class
DECL|class|TestStreamingExitStatus
specifier|public
class|class
name|TestStreamingExitStatus
block|{
DECL|field|TEST_DIR
specifier|protected
name|File
name|TEST_DIR
init|=
operator|new
name|File
argument_list|(
literal|"target/TestStreamingExitStatus"
argument_list|)
operator|.
name|getAbsoluteFile
argument_list|()
decl_stmt|;
DECL|field|INPUT_FILE
specifier|protected
name|File
name|INPUT_FILE
init|=
operator|new
name|File
argument_list|(
name|TEST_DIR
argument_list|,
literal|"input.txt"
argument_list|)
decl_stmt|;
DECL|field|OUTPUT_DIR
specifier|protected
name|File
name|OUTPUT_DIR
init|=
operator|new
name|File
argument_list|(
name|TEST_DIR
argument_list|,
literal|"out"
argument_list|)
decl_stmt|;
DECL|field|failingTask
specifier|protected
name|String
name|failingTask
init|=
name|UtilTest
operator|.
name|makeJavaCommand
argument_list|(
name|FailApp
operator|.
name|class
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"true"
block|}
argument_list|)
decl_stmt|;
DECL|field|echoTask
specifier|protected
name|String
name|echoTask
init|=
name|UtilTest
operator|.
name|makeJavaCommand
argument_list|(
name|FailApp
operator|.
name|class
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"false"
block|}
argument_list|)
decl_stmt|;
DECL|method|TestStreamingExitStatus ()
specifier|public
name|TestStreamingExitStatus
parameter_list|()
throws|throws
name|IOException
block|{
name|UtilTest
name|utilTest
init|=
operator|new
name|UtilTest
argument_list|(
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|utilTest
operator|.
name|checkUserDir
argument_list|()
expr_stmt|;
name|utilTest
operator|.
name|redirectIfAntJunit
argument_list|()
expr_stmt|;
block|}
DECL|method|genArgs (boolean exitStatusIsFailure, boolean failMap)
specifier|protected
name|String
index|[]
name|genArgs
parameter_list|(
name|boolean
name|exitStatusIsFailure
parameter_list|,
name|boolean
name|failMap
parameter_list|)
block|{
return|return
operator|new
name|String
index|[]
block|{
literal|"-input"
block|,
name|INPUT_FILE
operator|.
name|getAbsolutePath
argument_list|()
block|,
literal|"-output"
block|,
name|OUTPUT_DIR
operator|.
name|getAbsolutePath
argument_list|()
block|,
literal|"-mapper"
block|,
operator|(
name|failMap
condition|?
name|failingTask
else|:
name|echoTask
operator|)
block|,
literal|"-reducer"
block|,
operator|(
name|failMap
condition|?
name|echoTask
else|:
name|failingTask
operator|)
block|,
literal|"-jobconf"
block|,
literal|"mapreduce.task.files.preserve.failedtasks=true"
block|,
literal|"-jobconf"
block|,
literal|"stream.non.zero.exit.is.failure="
operator|+
name|exitStatusIsFailure
block|,
literal|"-jobconf"
block|,
literal|"stream.tmpdir="
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.data"
argument_list|,
literal|"/tmp"
argument_list|)
block|,
literal|"-jobconf"
block|,
literal|"mapreduce.task.io.sort.mb=10"
block|}
return|;
block|}
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|IOException
block|{
name|UtilTest
operator|.
name|recursiveDelete
argument_list|(
name|TEST_DIR
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|TEST_DIR
operator|.
name|mkdirs
argument_list|()
argument_list|)
expr_stmt|;
name|FileOutputStream
name|out
init|=
operator|new
name|FileOutputStream
argument_list|(
name|INPUT_FILE
operator|.
name|getAbsoluteFile
argument_list|()
argument_list|)
decl_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|"hello\n"
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|runStreamJob (boolean exitStatusIsFailure, boolean failMap)
specifier|public
name|void
name|runStreamJob
parameter_list|(
name|boolean
name|exitStatusIsFailure
parameter_list|,
name|boolean
name|failMap
parameter_list|)
throws|throws
name|Exception
block|{
name|boolean
name|mayExit
init|=
literal|false
decl_stmt|;
name|int
name|returnStatus
init|=
literal|0
decl_stmt|;
name|StreamJob
name|job
init|=
operator|new
name|StreamJob
argument_list|(
name|genArgs
argument_list|(
name|exitStatusIsFailure
argument_list|,
name|failMap
argument_list|)
argument_list|,
name|mayExit
argument_list|)
decl_stmt|;
name|returnStatus
operator|=
name|job
operator|.
name|go
argument_list|()
expr_stmt|;
if|if
condition|(
name|exitStatusIsFailure
condition|)
block|{
name|assertEquals
argument_list|(
literal|"Streaming Job failure code expected"
argument_list|,
comment|/*job not successful:*/
literal|1
argument_list|,
name|returnStatus
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertEquals
argument_list|(
literal|"Streaming Job expected to succeed"
argument_list|,
literal|0
argument_list|,
name|returnStatus
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testMapFailOk ()
specifier|public
name|void
name|testMapFailOk
parameter_list|()
throws|throws
name|Exception
block|{
name|runStreamJob
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMapFailNotOk ()
specifier|public
name|void
name|testMapFailNotOk
parameter_list|()
throws|throws
name|Exception
block|{
name|runStreamJob
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testReduceFailOk ()
specifier|public
name|void
name|testReduceFailOk
parameter_list|()
throws|throws
name|Exception
block|{
name|runStreamJob
argument_list|(
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testReduceFailNotOk ()
specifier|public
name|void
name|testReduceFailNotOk
parameter_list|()
throws|throws
name|Exception
block|{
name|runStreamJob
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

