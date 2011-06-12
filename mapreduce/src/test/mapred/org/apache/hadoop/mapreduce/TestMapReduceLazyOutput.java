begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
package|;
end_package

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
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStreamWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Writer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|MiniDFSCluster
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
name|MiniMRCluster
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
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|lib
operator|.
name|input
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
name|mapreduce
operator|.
name|lib
operator|.
name|input
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
name|mapreduce
operator|.
name|lib
operator|.
name|output
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
name|mapreduce
operator|.
name|lib
operator|.
name|output
operator|.
name|LazyOutputFormat
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
name|mapreduce
operator|.
name|lib
operator|.
name|output
operator|.
name|TextOutputFormat
import|;
end_import

begin_comment
comment|/**  * A JUnit test to test the Map-Reduce framework's feature to create part  * files only if there is an explicit output.collect. This helps in preventing  * 0 byte files  */
end_comment

begin_class
DECL|class|TestMapReduceLazyOutput
specifier|public
class|class
name|TestMapReduceLazyOutput
extends|extends
name|TestCase
block|{
DECL|field|NUM_HADOOP_SLAVES
specifier|private
specifier|static
specifier|final
name|int
name|NUM_HADOOP_SLAVES
init|=
literal|3
decl_stmt|;
DECL|field|NUM_MAPS_PER_NODE
specifier|private
specifier|static
specifier|final
name|int
name|NUM_MAPS_PER_NODE
init|=
literal|2
decl_stmt|;
DECL|field|INPUT
specifier|private
specifier|static
specifier|final
name|Path
name|INPUT
init|=
operator|new
name|Path
argument_list|(
literal|"/testlazy/input"
argument_list|)
decl_stmt|;
DECL|field|input
specifier|private
specifier|static
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|input
init|=
name|Arrays
operator|.
name|asList
argument_list|(
literal|"All"
argument_list|,
literal|"Roads"
argument_list|,
literal|"Lead"
argument_list|,
literal|"To"
argument_list|,
literal|"Hadoop"
argument_list|)
decl_stmt|;
DECL|class|TestMapper
specifier|public
specifier|static
class|class
name|TestMapper
extends|extends
name|Mapper
argument_list|<
name|LongWritable
argument_list|,
name|Text
argument_list|,
name|LongWritable
argument_list|,
name|Text
argument_list|>
block|{
DECL|method|map (LongWritable key, Text value, Context context )
specifier|public
name|void
name|map
parameter_list|(
name|LongWritable
name|key
parameter_list|,
name|Text
name|value
parameter_list|,
name|Context
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|String
name|id
init|=
name|context
operator|.
name|getTaskAttemptID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
comment|// Mapper 0 does not output anything
if|if
condition|(
operator|!
name|id
operator|.
name|endsWith
argument_list|(
literal|"0_0"
argument_list|)
condition|)
block|{
name|context
operator|.
name|write
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|class|TestReducer
specifier|public
specifier|static
class|class
name|TestReducer
extends|extends
name|Reducer
argument_list|<
name|LongWritable
argument_list|,
name|Text
argument_list|,
name|LongWritable
argument_list|,
name|Text
argument_list|>
block|{
DECL|method|reduce (LongWritable key, Iterable<Text> values, Context context)
specifier|public
name|void
name|reduce
parameter_list|(
name|LongWritable
name|key
parameter_list|,
name|Iterable
argument_list|<
name|Text
argument_list|>
name|values
parameter_list|,
name|Context
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|String
name|id
init|=
name|context
operator|.
name|getTaskAttemptID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
comment|// Reducer 0 does not output anything
if|if
condition|(
operator|!
name|id
operator|.
name|endsWith
argument_list|(
literal|"0_0"
argument_list|)
condition|)
block|{
for|for
control|(
name|Text
name|val
range|:
name|values
control|)
block|{
name|context
operator|.
name|write
argument_list|(
name|key
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|runTestLazyOutput (Configuration conf, Path output, int numReducers, boolean createLazily)
specifier|private
specifier|static
name|void
name|runTestLazyOutput
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|Path
name|output
parameter_list|,
name|int
name|numReducers
parameter_list|,
name|boolean
name|createLazily
parameter_list|)
throws|throws
name|Exception
block|{
name|Job
name|job
init|=
name|Job
operator|.
name|getInstance
argument_list|(
name|conf
argument_list|,
literal|"Test-Lazy-Output"
argument_list|)
decl_stmt|;
name|FileInputFormat
operator|.
name|setInputPaths
argument_list|(
name|job
argument_list|,
name|INPUT
argument_list|)
expr_stmt|;
name|FileOutputFormat
operator|.
name|setOutputPath
argument_list|(
name|job
argument_list|,
name|output
argument_list|)
expr_stmt|;
name|job
operator|.
name|setJarByClass
argument_list|(
name|TestMapReduceLazyOutput
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setInputFormatClass
argument_list|(
name|TextInputFormat
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setOutputKeyClass
argument_list|(
name|LongWritable
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setOutputValueClass
argument_list|(
name|Text
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setNumReduceTasks
argument_list|(
name|numReducers
argument_list|)
expr_stmt|;
name|job
operator|.
name|setMapperClass
argument_list|(
name|TestMapper
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setReducerClass
argument_list|(
name|TestReducer
operator|.
name|class
argument_list|)
expr_stmt|;
if|if
condition|(
name|createLazily
condition|)
block|{
name|LazyOutputFormat
operator|.
name|setOutputFormatClass
argument_list|(
name|job
argument_list|,
name|TextOutputFormat
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|job
operator|.
name|setOutputFormatClass
argument_list|(
name|TextOutputFormat
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|job
operator|.
name|waitForCompletion
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|createInput (FileSystem fs, int numMappers)
specifier|public
name|void
name|createInput
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|int
name|numMappers
parameter_list|)
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numMappers
condition|;
name|i
operator|++
control|)
block|{
name|OutputStream
name|os
init|=
name|fs
operator|.
name|create
argument_list|(
operator|new
name|Path
argument_list|(
name|INPUT
argument_list|,
literal|"text"
operator|+
name|i
operator|+
literal|".txt"
argument_list|)
argument_list|)
decl_stmt|;
name|Writer
name|wr
init|=
operator|new
name|OutputStreamWriter
argument_list|(
name|os
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|inp
range|:
name|input
control|)
block|{
name|wr
operator|.
name|write
argument_list|(
name|inp
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
block|}
name|wr
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|testLazyOutput ()
specifier|public
name|void
name|testLazyOutput
parameter_list|()
throws|throws
name|Exception
block|{
name|MiniDFSCluster
name|dfs
init|=
literal|null
decl_stmt|;
name|MiniMRCluster
name|mr
init|=
literal|null
decl_stmt|;
name|FileSystem
name|fileSys
init|=
literal|null
decl_stmt|;
try|try
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
comment|// Start the mini-MR and mini-DFS clusters
name|dfs
operator|=
operator|new
name|MiniDFSCluster
argument_list|(
name|conf
argument_list|,
name|NUM_HADOOP_SLAVES
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|fileSys
operator|=
name|dfs
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
name|mr
operator|=
operator|new
name|MiniMRCluster
argument_list|(
name|NUM_HADOOP_SLAVES
argument_list|,
name|fileSys
operator|.
name|getUri
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|int
name|numReducers
init|=
literal|2
decl_stmt|;
name|int
name|numMappers
init|=
name|NUM_HADOOP_SLAVES
operator|*
name|NUM_MAPS_PER_NODE
decl_stmt|;
name|createInput
argument_list|(
name|fileSys
argument_list|,
name|numMappers
argument_list|)
expr_stmt|;
name|Path
name|output1
init|=
operator|new
name|Path
argument_list|(
literal|"/testlazy/output1"
argument_list|)
decl_stmt|;
comment|// Test 1.
name|runTestLazyOutput
argument_list|(
name|mr
operator|.
name|createJobConf
argument_list|()
argument_list|,
name|output1
argument_list|,
name|numReducers
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|Path
index|[]
name|fileList
init|=
name|FileUtil
operator|.
name|stat2Paths
argument_list|(
name|fileSys
operator|.
name|listStatus
argument_list|(
name|output1
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
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|fileList
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Test1 File list["
operator|+
name|i
operator|+
literal|"]"
operator|+
literal|": "
operator|+
name|fileList
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|fileList
operator|.
name|length
operator|==
operator|(
name|numReducers
operator|-
literal|1
operator|)
argument_list|)
expr_stmt|;
comment|// Test 2. 0 Reducers, maps directly write to the output files
name|Path
name|output2
init|=
operator|new
name|Path
argument_list|(
literal|"/testlazy/output2"
argument_list|)
decl_stmt|;
name|runTestLazyOutput
argument_list|(
name|mr
operator|.
name|createJobConf
argument_list|()
argument_list|,
name|output2
argument_list|,
literal|0
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fileList
operator|=
name|FileUtil
operator|.
name|stat2Paths
argument_list|(
name|fileSys
operator|.
name|listStatus
argument_list|(
name|output2
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
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|fileList
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Test2 File list["
operator|+
name|i
operator|+
literal|"]"
operator|+
literal|": "
operator|+
name|fileList
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|fileList
operator|.
name|length
operator|==
name|numMappers
operator|-
literal|1
argument_list|)
expr_stmt|;
comment|// Test 3. 0 Reducers, but flag is turned off
name|Path
name|output3
init|=
operator|new
name|Path
argument_list|(
literal|"/testlazy/output3"
argument_list|)
decl_stmt|;
name|runTestLazyOutput
argument_list|(
name|mr
operator|.
name|createJobConf
argument_list|()
argument_list|,
name|output3
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|fileList
operator|=
name|FileUtil
operator|.
name|stat2Paths
argument_list|(
name|fileSys
operator|.
name|listStatus
argument_list|(
name|output3
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
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|fileList
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Test3 File list["
operator|+
name|i
operator|+
literal|"]"
operator|+
literal|": "
operator|+
name|fileList
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|fileList
operator|.
name|length
operator|==
name|numMappers
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|dfs
operator|!=
literal|null
condition|)
block|{
name|dfs
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|mr
operator|!=
literal|null
condition|)
block|{
name|mr
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

