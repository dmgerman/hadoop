begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred.lib
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|lib
package|;
end_package

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
name|io
operator|.
name|LongWritable
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
name|io
operator|.
name|Text
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
name|mapred
operator|.
name|FileInputFormat
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
name|mapred
operator|.
name|FileOutputFormat
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
name|mapred
operator|.
name|HadoopTestCase
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
name|mapred
operator|.
name|JobClient
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
name|mapred
operator|.
name|JobConf
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
name|mapred
operator|.
name|JobContext
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
name|mapred
operator|.
name|RunningJob
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
name|mapred
operator|.
name|TextInputFormat
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
name|mapred
operator|.
name|TextOutputFormat
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
name|mapred
operator|.
name|Utils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
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
name|fail
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
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
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStreamReader
import|;
end_import

begin_class
DECL|class|TestKeyFieldBasedComparator
specifier|public
class|class
name|TestKeyFieldBasedComparator
extends|extends
name|HadoopTestCase
block|{
DECL|field|TEST_DIR
specifier|private
specifier|static
specifier|final
name|File
name|TEST_DIR
init|=
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.data"
argument_list|,
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.io.tmpdir"
argument_list|)
argument_list|)
argument_list|,
literal|"TestKeyFieldBasedComparator-lib"
argument_list|)
decl_stmt|;
DECL|field|conf
name|JobConf
name|conf
decl_stmt|;
DECL|field|localConf
name|JobConf
name|localConf
decl_stmt|;
DECL|field|line1
name|String
name|line1
init|=
literal|"123 -123 005120 123.9 0.01 0.18 010 10.0 4444.1 011 011 234"
decl_stmt|;
DECL|field|line2
name|String
name|line2
init|=
literal|"134 -12 005100 123.10 -1.01 0.19 02 10.1 4444"
decl_stmt|;
DECL|method|TestKeyFieldBasedComparator ()
specifier|public
name|TestKeyFieldBasedComparator
parameter_list|()
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|HadoopTestCase
operator|.
name|LOCAL_MR
argument_list|,
name|HadoopTestCase
operator|.
name|LOCAL_FS
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|conf
operator|=
name|createJobConf
argument_list|()
expr_stmt|;
name|localConf
operator|=
name|createJobConf
argument_list|()
expr_stmt|;
name|localConf
operator|.
name|set
argument_list|(
name|JobContext
operator|.
name|MAP_OUTPUT_KEY_FIELD_SEPARATOR
argument_list|,
literal|" "
argument_list|)
expr_stmt|;
block|}
DECL|method|configure (String keySpec, int expect)
specifier|public
name|void
name|configure
parameter_list|(
name|String
name|keySpec
parameter_list|,
name|int
name|expect
parameter_list|)
throws|throws
name|Exception
block|{
name|Path
name|testdir
init|=
operator|new
name|Path
argument_list|(
name|TEST_DIR
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
decl_stmt|;
name|Path
name|inDir
init|=
operator|new
name|Path
argument_list|(
name|testdir
argument_list|,
literal|"in"
argument_list|)
decl_stmt|;
name|Path
name|outDir
init|=
operator|new
name|Path
argument_list|(
name|testdir
argument_list|,
literal|"out"
argument_list|)
decl_stmt|;
name|FileSystem
name|fs
init|=
name|getFileSystem
argument_list|()
decl_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|testdir
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInputFormat
argument_list|(
name|TextInputFormat
operator|.
name|class
argument_list|)
expr_stmt|;
name|FileInputFormat
operator|.
name|setInputPaths
argument_list|(
name|conf
argument_list|,
name|inDir
argument_list|)
expr_stmt|;
name|FileOutputFormat
operator|.
name|setOutputPath
argument_list|(
name|conf
argument_list|,
name|outDir
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setOutputKeyClass
argument_list|(
name|Text
operator|.
name|class
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setOutputValueClass
argument_list|(
name|LongWritable
operator|.
name|class
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setNumMapTasks
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setNumReduceTasks
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setOutputFormat
argument_list|(
name|TextOutputFormat
operator|.
name|class
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setOutputKeyComparatorClass
argument_list|(
name|KeyFieldBasedComparator
operator|.
name|class
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setKeyFieldComparatorOptions
argument_list|(
name|keySpec
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setKeyFieldPartitionerOptions
argument_list|(
literal|"-k1.1,1.1"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|JobContext
operator|.
name|MAP_OUTPUT_KEY_FIELD_SEPARATOR
argument_list|,
literal|" "
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setMapperClass
argument_list|(
name|InverseMapper
operator|.
name|class
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setReducerClass
argument_list|(
name|IdentityReducer
operator|.
name|class
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|fs
operator|.
name|mkdirs
argument_list|(
name|testdir
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Mkdirs failed to create "
operator|+
name|testdir
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|fs
operator|.
name|mkdirs
argument_list|(
name|inDir
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Mkdirs failed to create "
operator|+
name|inDir
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
comment|// set up input data in 2 files
name|Path
name|inFile
init|=
operator|new
name|Path
argument_list|(
name|inDir
argument_list|,
literal|"part0"
argument_list|)
decl_stmt|;
name|FileOutputStream
name|fos
init|=
operator|new
name|FileOutputStream
argument_list|(
name|inFile
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|fos
operator|.
name|write
argument_list|(
operator|(
name|line1
operator|+
literal|"\n"
operator|)
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|fos
operator|.
name|write
argument_list|(
operator|(
name|line2
operator|+
literal|"\n"
operator|)
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|fos
operator|.
name|close
argument_list|()
expr_stmt|;
name|JobClient
name|jc
init|=
operator|new
name|JobClient
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|RunningJob
name|r_job
init|=
name|jc
operator|.
name|submitJob
argument_list|(
name|conf
argument_list|)
decl_stmt|;
while|while
condition|(
operator|!
name|r_job
operator|.
name|isComplete
argument_list|()
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|r_job
operator|.
name|isSuccessful
argument_list|()
condition|)
block|{
name|fail
argument_list|(
literal|"Oops! The job broke due to an unexpected error"
argument_list|)
expr_stmt|;
block|}
name|Path
index|[]
name|outputFiles
init|=
name|FileUtil
operator|.
name|stat2Paths
argument_list|(
name|getFileSystem
argument_list|()
operator|.
name|listStatus
argument_list|(
name|outDir
argument_list|,
operator|new
name|Utils
operator|.
name|OutputFileUtils
operator|.
name|OutputFilesFilter
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|outputFiles
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|InputStream
name|is
init|=
name|getFileSystem
argument_list|()
operator|.
name|open
argument_list|(
name|outputFiles
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|BufferedReader
name|reader
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|is
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|line
init|=
name|reader
operator|.
name|readLine
argument_list|()
decl_stmt|;
comment|//make sure we get what we expect as the first line, and also
comment|//that we have two lines
if|if
condition|(
name|expect
operator|==
literal|1
condition|)
block|{
name|assertTrue
argument_list|(
name|line
operator|.
name|startsWith
argument_list|(
name|line1
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|expect
operator|==
literal|2
condition|)
block|{
name|assertTrue
argument_list|(
name|line
operator|.
name|startsWith
argument_list|(
name|line2
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|line
operator|=
name|reader
operator|.
name|readLine
argument_list|()
expr_stmt|;
if|if
condition|(
name|expect
operator|==
literal|1
condition|)
block|{
name|assertTrue
argument_list|(
name|line
operator|.
name|startsWith
argument_list|(
name|line2
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|expect
operator|==
literal|2
condition|)
block|{
name|assertTrue
argument_list|(
name|line
operator|.
name|startsWith
argument_list|(
name|line1
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|After
DECL|method|cleanup ()
specifier|public
name|void
name|cleanup
parameter_list|()
block|{
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|TEST_DIR
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBasicUnixComparator ()
specifier|public
name|void
name|testBasicUnixComparator
parameter_list|()
throws|throws
name|Exception
block|{
name|configure
argument_list|(
literal|"-k1,1n"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|configure
argument_list|(
literal|"-k2,2n"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|configure
argument_list|(
literal|"-k2.2,2n"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|configure
argument_list|(
literal|"-k3.4,3n"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|configure
argument_list|(
literal|"-k3.2,3.3n -k4,4n"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|configure
argument_list|(
literal|"-k3.2,3.3n -k4,4nr"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|configure
argument_list|(
literal|"-k2.4,2.4n"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|configure
argument_list|(
literal|"-k7,7"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|configure
argument_list|(
literal|"-k7,7n"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|configure
argument_list|(
literal|"-k8,8n"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|configure
argument_list|(
literal|"-k9,9"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|configure
argument_list|(
literal|"-k11,11"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|configure
argument_list|(
literal|"-k10,10"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|localTestWithoutMRJob
argument_list|(
literal|"-k9,9"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|field|line1_bytes
name|byte
index|[]
name|line1_bytes
init|=
name|line1
operator|.
name|getBytes
argument_list|()
decl_stmt|;
DECL|field|line2_bytes
name|byte
index|[]
name|line2_bytes
init|=
name|line2
operator|.
name|getBytes
argument_list|()
decl_stmt|;
DECL|method|localTestWithoutMRJob (String keySpec, int expect)
specifier|public
name|void
name|localTestWithoutMRJob
parameter_list|(
name|String
name|keySpec
parameter_list|,
name|int
name|expect
parameter_list|)
throws|throws
name|Exception
block|{
name|KeyFieldBasedComparator
argument_list|<
name|Void
argument_list|,
name|Void
argument_list|>
name|keyFieldCmp
init|=
operator|new
name|KeyFieldBasedComparator
argument_list|<
name|Void
argument_list|,
name|Void
argument_list|>
argument_list|()
decl_stmt|;
name|localConf
operator|.
name|setKeyFieldComparatorOptions
argument_list|(
name|keySpec
argument_list|)
expr_stmt|;
name|keyFieldCmp
operator|.
name|configure
argument_list|(
name|localConf
argument_list|)
expr_stmt|;
name|int
name|result
init|=
name|keyFieldCmp
operator|.
name|compare
argument_list|(
name|line1_bytes
argument_list|,
literal|0
argument_list|,
name|line1_bytes
operator|.
name|length
argument_list|,
name|line2_bytes
argument_list|,
literal|0
argument_list|,
name|line2_bytes
operator|.
name|length
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|expect
operator|>=
literal|0
operator|&&
name|result
operator|<
literal|0
operator|)
operator|||
operator|(
name|expect
operator|<
literal|0
operator|&&
name|result
operator|>=
literal|0
operator|)
condition|)
name|fail
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

