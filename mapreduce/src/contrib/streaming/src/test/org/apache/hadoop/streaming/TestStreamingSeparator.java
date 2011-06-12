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

begin_comment
comment|/**  * This class tests hadoopStreaming with customized separator in MapReduce local mode.  */
end_comment

begin_class
DECL|class|TestStreamingSeparator
specifier|public
class|class
name|TestStreamingSeparator
block|{
comment|// "map" command: grep -E (red|green|blue)
comment|// reduce command: uniq
DECL|field|INPUT_FILE
specifier|protected
name|File
name|INPUT_FILE
init|=
operator|new
name|File
argument_list|(
literal|"TestStreamingSeparator.input.txt"
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
literal|"TestStreamingSeparator.out"
argument_list|)
decl_stmt|;
DECL|field|input
specifier|protected
name|String
name|input
init|=
literal|"roses1are.red\nviolets1are.blue\nbunnies1are.pink\n"
decl_stmt|;
comment|// mapreduce.input.keyvaluelinerecordreader.key.value.separator reads 1 as separator
comment|// stream.map.input.field.separator uses 2 as separator
comment|// map behaves like "/usr/bin/tr 2 3"; (translate 2 to 3)
DECL|field|map
specifier|protected
name|String
name|map
init|=
name|UtilTest
operator|.
name|makeJavaCommand
argument_list|(
name|TrApp
operator|.
name|class
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"2"
block|,
literal|"3"
block|}
argument_list|)
decl_stmt|;
comment|// stream.map.output.field.separator recognize 3 as separator
comment|// stream.reduce.input.field.separator recognize 3 as separator
comment|// reduce behaves like "/usr/bin/tr 3 4"; (translate 3 to 4)
DECL|field|reduce
specifier|protected
name|String
name|reduce
init|=
name|UtilTest
operator|.
name|makeJavaCommand
argument_list|(
name|TrAppReduce
operator|.
name|class
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"3"
block|,
literal|"4"
block|}
argument_list|)
decl_stmt|;
comment|// stream.reduce.output.field.separator recognize 4 as separator
comment|// mapreduce.output.textoutputformat.separator outputs 5 as separator
DECL|field|outputExpect
specifier|protected
name|String
name|outputExpect
init|=
literal|"bunnies5are.pink\nroses5are.red\nviolets5are.blue\n"
decl_stmt|;
DECL|field|job
specifier|private
name|StreamJob
name|job
decl_stmt|;
DECL|method|TestStreamingSeparator ()
specifier|public
name|TestStreamingSeparator
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
name|reduce
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
block|,
literal|"-inputformat"
block|,
literal|"KeyValueTextInputFormat"
block|,
literal|"-jobconf"
block|,
literal|"mapreduce.input.keyvaluelinerecordreader.key.value.separator=1"
block|,
literal|"-jobconf"
block|,
literal|"stream.map.input.field.separator=2"
block|,
literal|"-jobconf"
block|,
literal|"stream.map.output.field.separator=3"
block|,
literal|"-jobconf"
block|,
literal|"stream.reduce.input.field.separator=3"
block|,
literal|"-jobconf"
block|,
literal|"stream.reduce.output.field.separator=4"
block|,
literal|"-jobconf"
block|,
literal|"mapreduce.output.textoutputformat.separator=5"
block|,     }
return|;
block|}
annotation|@
name|Test
DECL|method|testCommandLine ()
specifier|public
name|void
name|testCommandLine
parameter_list|()
throws|throws
name|Exception
block|{
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
name|boolean
name|mayExit
init|=
literal|false
decl_stmt|;
comment|// During tests, the default Configuration will use a local mapred
comment|// So don't specify -config or -cluster
name|job
operator|=
operator|new
name|StreamJob
argument_list|(
name|genArgs
argument_list|()
argument_list|,
name|mayExit
argument_list|)
expr_stmt|;
name|job
operator|.
name|go
argument_list|()
expr_stmt|;
name|File
name|outFile
init|=
operator|new
name|File
argument_list|(
name|OUTPUT_DIR
argument_list|,
literal|"part-00000"
argument_list|)
operator|.
name|getAbsoluteFile
argument_list|()
decl_stmt|;
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
name|outFile
operator|.
name|delete
argument_list|()
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"outEx1="
operator|+
name|outputExpect
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"  out1="
operator|+
name|output
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|outputExpect
argument_list|,
name|output
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
DECL|method|main (String[]args)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
operator|new
name|TestStreamingSeparator
argument_list|()
operator|.
name|testCommandLine
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

