begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
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
name|Iterator
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
name|io
operator|.
name|Writable
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
name|WritableComparable
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
name|lib
operator|.
name|LazyOutputFormat
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

begin_comment
comment|/**  * A JUnit test to test the Map-Reduce framework's feature to create part  * files only if there is an explicit output.collect. This helps in preventing  * 0 byte files  */
end_comment

begin_class
DECL|class|TestLazyOutput
specifier|public
class|class
name|TestLazyOutput
block|{
DECL|field|NUM_HADOOP_WORKERS
specifier|private
specifier|static
specifier|final
name|int
name|NUM_HADOOP_WORKERS
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
DECL|field|INPUTPATH
specifier|private
specifier|static
specifier|final
name|Path
name|INPUTPATH
init|=
operator|new
name|Path
argument_list|(
literal|"/testlazy/input"
argument_list|)
decl_stmt|;
DECL|field|INPUTLIST
specifier|private
specifier|static
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|INPUTLIST
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
specifier|static
class|class
name|TestMapper
extends|extends
name|MapReduceBase
implements|implements
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
DECL|field|id
specifier|private
name|String
name|id
decl_stmt|;
DECL|method|configure (JobConf job)
specifier|public
name|void
name|configure
parameter_list|(
name|JobConf
name|job
parameter_list|)
block|{
name|id
operator|=
name|job
operator|.
name|get
argument_list|(
name|JobContext
operator|.
name|TASK_ATTEMPT_ID
argument_list|)
expr_stmt|;
block|}
DECL|method|map (LongWritable key, Text val, OutputCollector<LongWritable, Text> output, Reporter reporter)
specifier|public
name|void
name|map
parameter_list|(
name|LongWritable
name|key
parameter_list|,
name|Text
name|val
parameter_list|,
name|OutputCollector
argument_list|<
name|LongWritable
argument_list|,
name|Text
argument_list|>
name|output
parameter_list|,
name|Reporter
name|reporter
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Everybody other than id 0 outputs
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
name|output
operator|.
name|collect
argument_list|(
name|key
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|class|TestReducer
specifier|static
class|class
name|TestReducer
extends|extends
name|MapReduceBase
implements|implements
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
DECL|field|id
specifier|private
name|String
name|id
decl_stmt|;
DECL|method|configure (JobConf job)
specifier|public
name|void
name|configure
parameter_list|(
name|JobConf
name|job
parameter_list|)
block|{
name|id
operator|=
name|job
operator|.
name|get
argument_list|(
name|JobContext
operator|.
name|TASK_ATTEMPT_ID
argument_list|)
expr_stmt|;
block|}
comment|/** Writes all keys and values directly to output. */
DECL|method|reduce (LongWritable key, Iterator<Text> values, OutputCollector<LongWritable, Text> output, Reporter reporter)
specifier|public
name|void
name|reduce
parameter_list|(
name|LongWritable
name|key
parameter_list|,
name|Iterator
argument_list|<
name|Text
argument_list|>
name|values
parameter_list|,
name|OutputCollector
argument_list|<
name|LongWritable
argument_list|,
name|Text
argument_list|>
name|output
parameter_list|,
name|Reporter
name|reporter
parameter_list|)
throws|throws
name|IOException
block|{
while|while
condition|(
name|values
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Text
name|v
init|=
name|values
operator|.
name|next
argument_list|()
decl_stmt|;
comment|//Reducer 0 skips collect
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
name|output
operator|.
name|collect
argument_list|(
name|key
argument_list|,
name|v
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|runTestLazyOutput (JobConf job, Path output, int numReducers, boolean createLazily)
specifier|private
specifier|static
name|void
name|runTestLazyOutput
parameter_list|(
name|JobConf
name|job
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
name|job
operator|.
name|setJobName
argument_list|(
literal|"test-lazy-output"
argument_list|)
expr_stmt|;
name|FileInputFormat
operator|.
name|setInputPaths
argument_list|(
name|job
argument_list|,
name|INPUTPATH
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
name|setInputFormat
argument_list|(
name|TextInputFormat
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setMapOutputKeyClass
argument_list|(
name|LongWritable
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setMapOutputValueClass
argument_list|(
name|Text
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
name|JobClient
name|client
init|=
operator|new
name|JobClient
argument_list|(
name|job
argument_list|)
decl_stmt|;
name|job
operator|.
name|setNumReduceTasks
argument_list|(
name|numReducers
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
name|setOutputFormat
argument_list|(
name|TextOutputFormat
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
name|JobClient
operator|.
name|runJob
argument_list|(
name|job
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
name|INPUTPATH
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
name|INPUTLIST
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
annotation|@
name|Test
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
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|numDataNodes
argument_list|(
name|NUM_HADOOP_WORKERS
argument_list|)
operator|.
name|build
argument_list|()
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
name|NUM_HADOOP_WORKERS
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
name|NUM_HADOOP_WORKERS
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

