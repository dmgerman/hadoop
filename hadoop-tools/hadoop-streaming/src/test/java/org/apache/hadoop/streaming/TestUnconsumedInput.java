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
name|DataOutputStream
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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang
operator|.
name|StringUtils
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
name|FileUtil
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|HdfsConfiguration
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
name|hdfs
operator|.
name|MiniDFSCluster
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
DECL|class|TestUnconsumedInput
specifier|public
class|class
name|TestUnconsumedInput
block|{
DECL|field|EXPECTED_OUTPUT_SIZE
specifier|protected
specifier|final
name|int
name|EXPECTED_OUTPUT_SIZE
init|=
literal|10000
decl_stmt|;
DECL|field|INPUT_FILE
specifier|protected
name|File
name|INPUT_FILE
init|=
operator|new
name|File
argument_list|(
literal|"stream_uncinput_input.txt"
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
literal|"stream_uncinput_out"
argument_list|)
decl_stmt|;
comment|// map parses input lines and generates count entries for each word.
DECL|field|input
specifier|protected
name|String
name|input
init|=
literal|"roses.are.red\nviolets.are.blue\nbunnies.are.pink\n"
decl_stmt|;
DECL|field|map
specifier|protected
name|String
name|map
init|=
name|UtilTest
operator|.
name|makeJavaCommand
argument_list|(
name|OutputOnlyApp
operator|.
name|class
argument_list|,
operator|new
name|String
index|[]
block|{
name|Integer
operator|.
name|toString
argument_list|(
name|EXPECTED_OUTPUT_SIZE
argument_list|)
block|}
argument_list|)
decl_stmt|;
DECL|field|job
specifier|private
name|StreamJob
name|job
decl_stmt|;
DECL|method|TestUnconsumedInput ()
specifier|public
name|TestUnconsumedInput
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
DECL|method|createInput ()
specifier|protected
name|void
name|createInput
parameter_list|()
throws|throws
name|IOException
block|{
name|DataOutputStream
name|out
init|=
operator|new
name|DataOutputStream
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
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10000
condition|;
operator|++
name|i
control|)
block|{
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
block|}
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|genArgs ()
specifier|protected
name|String
index|[]
name|genArgs
parameter_list|()
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
name|map
block|,
literal|"-reducer"
block|,
literal|"org.apache.hadoop.mapred.lib.IdentityReducer"
block|,
literal|"-numReduceTasks"
block|,
literal|"0"
block|,
literal|"-jobconf"
block|,
literal|"mapreduce.task.files.preserve.failedtasks=true"
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
block|}
return|;
block|}
annotation|@
name|Test
DECL|method|testUnconsumedInput ()
specifier|public
name|void
name|testUnconsumedInput
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|outFileName
init|=
literal|"part-00000"
decl_stmt|;
name|File
name|outFile
init|=
literal|null
decl_stmt|;
try|try
block|{
try|try
block|{
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|OUTPUT_DIR
operator|.
name|getAbsoluteFile
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{       }
name|createInput
argument_list|()
expr_stmt|;
comment|// setup config to ignore unconsumed input
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"stream.minRecWrittenToEnableSkip_"
argument_list|,
literal|"0"
argument_list|)
expr_stmt|;
name|job
operator|=
operator|new
name|StreamJob
argument_list|()
expr_stmt|;
name|job
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|int
name|exitCode
init|=
name|job
operator|.
name|run
argument_list|(
name|genArgs
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Job failed"
argument_list|,
literal|0
argument_list|,
name|exitCode
argument_list|)
expr_stmt|;
name|outFile
operator|=
operator|new
name|File
argument_list|(
name|OUTPUT_DIR
argument_list|,
name|outFileName
argument_list|)
operator|.
name|getAbsoluteFile
argument_list|()
expr_stmt|;
name|String
name|output
init|=
name|StreamUtil
operator|.
name|slurp
argument_list|(
name|outFile
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Output was truncated"
argument_list|,
name|EXPECTED_OUTPUT_SIZE
argument_list|,
name|StringUtils
operator|.
name|countMatches
argument_list|(
name|output
argument_list|,
literal|"\t"
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|INPUT_FILE
operator|.
name|delete
argument_list|()
expr_stmt|;
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|OUTPUT_DIR
operator|.
name|getAbsoluteFile
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

