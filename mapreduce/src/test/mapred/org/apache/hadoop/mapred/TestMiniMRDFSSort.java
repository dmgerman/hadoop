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
name|DataOutputStream
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
name|junit
operator|.
name|extensions
operator|.
name|TestSetup
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|Test
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
name|junit
operator|.
name|framework
operator|.
name|TestSuite
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
name|BytesWritable
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
name|lib
operator|.
name|IdentityMapper
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
name|IdentityReducer
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
name|NullOutputFormat
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
name|FileInputFormatCounter
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
name|apache
operator|.
name|hadoop
operator|.
name|examples
operator|.
name|RandomWriter
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
name|examples
operator|.
name|Sort
import|;
end_import

begin_comment
comment|/**  * A JUnit test to test the Map-Reduce framework's sort   * with a Mini Map-Reduce Cluster with a Mini HDFS Clusters.  */
end_comment

begin_class
DECL|class|TestMiniMRDFSSort
specifier|public
class|class
name|TestMiniMRDFSSort
extends|extends
name|TestCase
block|{
comment|// Input/Output paths for sort
DECL|field|SORT_INPUT_PATH
specifier|private
specifier|static
specifier|final
name|Path
name|SORT_INPUT_PATH
init|=
operator|new
name|Path
argument_list|(
literal|"/sort/input"
argument_list|)
decl_stmt|;
DECL|field|SORT_OUTPUT_PATH
specifier|private
specifier|static
specifier|final
name|Path
name|SORT_OUTPUT_PATH
init|=
operator|new
name|Path
argument_list|(
literal|"/sort/output"
argument_list|)
decl_stmt|;
comment|// Knobs to control randomwriter; and hence sort
DECL|field|NUM_HADOOP_SLAVES
specifier|private
specifier|static
specifier|final
name|int
name|NUM_HADOOP_SLAVES
init|=
literal|3
decl_stmt|;
comment|// make it big enough to cause a spill in the map
DECL|field|RW_BYTES_PER_MAP
specifier|private
specifier|static
specifier|final
name|int
name|RW_BYTES_PER_MAP
init|=
literal|3
operator|*
literal|1024
operator|*
literal|1024
decl_stmt|;
DECL|field|RW_MAPS_PER_HOST
specifier|private
specifier|static
specifier|final
name|int
name|RW_MAPS_PER_HOST
init|=
literal|2
decl_stmt|;
DECL|field|mrCluster
specifier|private
specifier|static
name|MiniMRCluster
name|mrCluster
init|=
literal|null
decl_stmt|;
DECL|field|dfsCluster
specifier|private
specifier|static
name|MiniDFSCluster
name|dfsCluster
init|=
literal|null
decl_stmt|;
DECL|field|dfs
specifier|private
specifier|static
name|FileSystem
name|dfs
init|=
literal|null
decl_stmt|;
DECL|method|suite ()
specifier|public
specifier|static
name|Test
name|suite
parameter_list|()
block|{
name|TestSetup
name|setup
init|=
operator|new
name|TestSetup
argument_list|(
operator|new
name|TestSuite
argument_list|(
name|TestMiniMRDFSSort
operator|.
name|class
argument_list|)
argument_list|)
block|{
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|dfsCluster
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
name|dfs
operator|=
name|dfsCluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
name|mrCluster
operator|=
operator|new
name|MiniMRCluster
argument_list|(
name|NUM_HADOOP_SLAVES
argument_list|,
name|dfs
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
block|}
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|dfsCluster
operator|!=
literal|null
condition|)
block|{
name|dfsCluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|mrCluster
operator|!=
literal|null
condition|)
block|{
name|mrCluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
return|return
name|setup
return|;
block|}
DECL|method|runRandomWriter (JobConf job, Path sortInput)
specifier|public
specifier|static
name|void
name|runRandomWriter
parameter_list|(
name|JobConf
name|job
parameter_list|,
name|Path
name|sortInput
parameter_list|)
throws|throws
name|Exception
block|{
comment|// Scale down the default settings for RandomWriter for the test-case
comment|// Generates NUM_HADOOP_SLAVES * RW_MAPS_PER_HOST * RW_BYTES_PER_MAP
name|job
operator|.
name|setInt
argument_list|(
name|RandomWriter
operator|.
name|BYTES_PER_MAP
argument_list|,
name|RW_BYTES_PER_MAP
argument_list|)
expr_stmt|;
name|job
operator|.
name|setInt
argument_list|(
name|RandomWriter
operator|.
name|MAPS_PER_HOST
argument_list|,
name|RW_MAPS_PER_HOST
argument_list|)
expr_stmt|;
name|String
index|[]
name|rwArgs
init|=
block|{
name|sortInput
operator|.
name|toString
argument_list|()
block|}
decl_stmt|;
comment|// Run RandomWriter
name|assertEquals
argument_list|(
name|ToolRunner
operator|.
name|run
argument_list|(
name|job
argument_list|,
operator|new
name|RandomWriter
argument_list|()
argument_list|,
name|rwArgs
argument_list|)
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|runSort (JobConf job, Path sortInput, Path sortOutput)
specifier|private
specifier|static
name|void
name|runSort
parameter_list|(
name|JobConf
name|job
parameter_list|,
name|Path
name|sortInput
parameter_list|,
name|Path
name|sortOutput
parameter_list|)
throws|throws
name|Exception
block|{
name|job
operator|.
name|setInt
argument_list|(
name|JobContext
operator|.
name|JVM_NUMTASKS_TORUN
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|job
operator|.
name|setInt
argument_list|(
name|JobContext
operator|.
name|IO_SORT_MB
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|job
operator|.
name|setNumMapTasks
argument_list|(
literal|12
argument_list|)
expr_stmt|;
comment|// Setup command-line arguments to 'sort'
name|String
index|[]
name|sortArgs
init|=
block|{
name|sortInput
operator|.
name|toString
argument_list|()
block|,
name|sortOutput
operator|.
name|toString
argument_list|()
block|}
decl_stmt|;
comment|// Run Sort
name|Sort
name|sort
init|=
operator|new
name|Sort
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|ToolRunner
operator|.
name|run
argument_list|(
name|job
argument_list|,
name|sort
argument_list|,
name|sortArgs
argument_list|)
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|Counters
name|counters
init|=
name|sort
operator|.
name|getResult
argument_list|()
operator|.
name|getCounters
argument_list|()
decl_stmt|;
name|long
name|mapInput
init|=
name|counters
operator|.
name|findCounter
argument_list|(
name|FileInputFormatCounter
operator|.
name|BYTES_READ
argument_list|)
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|long
name|hdfsRead
init|=
name|counters
operator|.
name|findCounter
argument_list|(
name|Task
operator|.
name|FILESYSTEM_COUNTER_GROUP
argument_list|,
literal|"HDFS_BYTES_READ"
argument_list|)
operator|.
name|getValue
argument_list|()
decl_stmt|;
comment|// the hdfs read should be between 100% and 110% of the map input bytes
name|assertTrue
argument_list|(
literal|"map input = "
operator|+
name|mapInput
operator|+
literal|", hdfs read = "
operator|+
name|hdfsRead
argument_list|,
operator|(
name|hdfsRead
operator|<
operator|(
name|mapInput
operator|*
literal|1.1
operator|)
operator|)
operator|&&
operator|(
name|hdfsRead
operator|>=
name|mapInput
operator|)
argument_list|)
expr_stmt|;
block|}
DECL|method|runSortValidator (JobConf job, Path sortInput, Path sortOutput)
specifier|private
specifier|static
name|void
name|runSortValidator
parameter_list|(
name|JobConf
name|job
parameter_list|,
name|Path
name|sortInput
parameter_list|,
name|Path
name|sortOutput
parameter_list|)
throws|throws
name|Exception
block|{
name|String
index|[]
name|svArgs
init|=
block|{
literal|"-sortInput"
block|,
name|sortInput
operator|.
name|toString
argument_list|()
block|,
literal|"-sortOutput"
block|,
name|sortOutput
operator|.
name|toString
argument_list|()
block|}
decl_stmt|;
comment|// Run Sort-Validator
name|assertEquals
argument_list|(
name|ToolRunner
operator|.
name|run
argument_list|(
name|job
argument_list|,
operator|new
name|SortValidator
argument_list|()
argument_list|,
name|svArgs
argument_list|)
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|class|ReuseDetector
specifier|private
specifier|static
class|class
name|ReuseDetector
extends|extends
name|MapReduceBase
implements|implements
name|Mapper
argument_list|<
name|BytesWritable
argument_list|,
name|BytesWritable
argument_list|,
name|Text
argument_list|,
name|Text
argument_list|>
block|{
DECL|field|instances
specifier|static
name|int
name|instances
init|=
literal|0
decl_stmt|;
DECL|field|reporter
name|Reporter
name|reporter
init|=
literal|null
decl_stmt|;
annotation|@
name|Override
DECL|method|map (BytesWritable key, BytesWritable value, OutputCollector<Text, Text> output, Reporter reporter)
specifier|public
name|void
name|map
parameter_list|(
name|BytesWritable
name|key
parameter_list|,
name|BytesWritable
name|value
parameter_list|,
name|OutputCollector
argument_list|<
name|Text
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
name|this
operator|.
name|reporter
operator|=
name|reporter
expr_stmt|;
block|}
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|reporter
operator|.
name|incrCounter
argument_list|(
literal|"jvm"
argument_list|,
literal|"use"
argument_list|,
operator|++
name|instances
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|runJvmReuseTest (JobConf job, boolean reuse)
specifier|private
specifier|static
name|void
name|runJvmReuseTest
parameter_list|(
name|JobConf
name|job
parameter_list|,
name|boolean
name|reuse
parameter_list|)
throws|throws
name|IOException
block|{
comment|// setup a map-only job that reads the input and only sets the counters
comment|// based on how many times the jvm was reused.
name|job
operator|.
name|setInt
argument_list|(
name|JobContext
operator|.
name|JVM_NUMTASKS_TORUN
argument_list|,
name|reuse
condition|?
operator|-
literal|1
else|:
literal|1
argument_list|)
expr_stmt|;
name|FileInputFormat
operator|.
name|setInputPaths
argument_list|(
name|job
argument_list|,
name|SORT_INPUT_PATH
argument_list|)
expr_stmt|;
name|job
operator|.
name|setInputFormat
argument_list|(
name|SequenceFileInputFormat
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setOutputFormat
argument_list|(
name|NullOutputFormat
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setMapperClass
argument_list|(
name|ReuseDetector
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setOutputKeyClass
argument_list|(
name|Text
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
name|setNumMapTasks
argument_list|(
literal|24
argument_list|)
expr_stmt|;
name|job
operator|.
name|setNumReduceTasks
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|RunningJob
name|result
init|=
name|JobClient
operator|.
name|runJob
argument_list|(
name|job
argument_list|)
decl_stmt|;
name|long
name|uses
init|=
name|result
operator|.
name|getCounters
argument_list|()
operator|.
name|findCounter
argument_list|(
literal|"jvm"
argument_list|,
literal|"use"
argument_list|)
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|int
name|maps
init|=
name|job
operator|.
name|getNumMapTasks
argument_list|()
decl_stmt|;
if|if
condition|(
name|reuse
condition|)
block|{
name|assertTrue
argument_list|(
literal|"maps = "
operator|+
name|maps
operator|+
literal|", uses = "
operator|+
name|uses
argument_list|,
name|maps
operator|<
name|uses
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertEquals
argument_list|(
literal|"uses should be number of maps"
argument_list|,
name|job
operator|.
name|getNumMapTasks
argument_list|()
argument_list|,
name|uses
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testMapReduceSort ()
specifier|public
name|void
name|testMapReduceSort
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Run randomwriter to generate input for 'sort'
name|runRandomWriter
argument_list|(
name|mrCluster
operator|.
name|createJobConf
argument_list|()
argument_list|,
name|SORT_INPUT_PATH
argument_list|)
expr_stmt|;
comment|// Run sort
name|runSort
argument_list|(
name|mrCluster
operator|.
name|createJobConf
argument_list|()
argument_list|,
name|SORT_INPUT_PATH
argument_list|,
name|SORT_OUTPUT_PATH
argument_list|)
expr_stmt|;
comment|// Run sort-validator to check if sort worked correctly
name|runSortValidator
argument_list|(
name|mrCluster
operator|.
name|createJobConf
argument_list|()
argument_list|,
name|SORT_INPUT_PATH
argument_list|,
name|SORT_OUTPUT_PATH
argument_list|)
expr_stmt|;
block|}
DECL|method|testJvmReuse ()
specifier|public
name|void
name|testJvmReuse
parameter_list|()
throws|throws
name|Exception
block|{
name|runJvmReuseTest
argument_list|(
name|mrCluster
operator|.
name|createJobConf
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|testNoJvmReuse ()
specifier|public
name|void
name|testNoJvmReuse
parameter_list|()
throws|throws
name|Exception
block|{
name|runJvmReuseTest
argument_list|(
name|mrCluster
operator|.
name|createJobConf
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|class|BadPartitioner
specifier|private
specifier|static
class|class
name|BadPartitioner
implements|implements
name|Partitioner
argument_list|<
name|LongWritable
argument_list|,
name|Text
argument_list|>
block|{
DECL|field|low
name|boolean
name|low
decl_stmt|;
DECL|method|configure (JobConf conf)
specifier|public
name|void
name|configure
parameter_list|(
name|JobConf
name|conf
parameter_list|)
block|{
name|low
operator|=
name|conf
operator|.
name|getBoolean
argument_list|(
literal|"test.testmapred.badpartition"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|getPartition (LongWritable k, Text v, int numPartitions)
specifier|public
name|int
name|getPartition
parameter_list|(
name|LongWritable
name|k
parameter_list|,
name|Text
name|v
parameter_list|,
name|int
name|numPartitions
parameter_list|)
block|{
return|return
name|low
condition|?
operator|-
literal|1
else|:
name|numPartitions
return|;
block|}
block|}
DECL|method|testPartitioner ()
specifier|public
name|void
name|testPartitioner
parameter_list|()
throws|throws
name|Exception
block|{
name|JobConf
name|conf
init|=
name|mrCluster
operator|.
name|createJobConf
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setPartitionerClass
argument_list|(
name|BadPartitioner
operator|.
name|class
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setNumReduceTasks
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|FileSystem
name|fs
init|=
name|FileSystem
operator|.
name|get
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|Path
name|testdir
init|=
operator|new
name|Path
argument_list|(
literal|"blah"
argument_list|)
operator|.
name|makeQualified
argument_list|(
name|fs
operator|.
name|getUri
argument_list|()
argument_list|,
name|fs
operator|.
name|getWorkingDirectory
argument_list|()
argument_list|)
decl_stmt|;
name|Path
name|inFile
init|=
operator|new
name|Path
argument_list|(
name|testdir
argument_list|,
literal|"blah"
argument_list|)
decl_stmt|;
name|DataOutputStream
name|f
init|=
name|fs
operator|.
name|create
argument_list|(
name|inFile
argument_list|)
decl_stmt|;
name|f
operator|.
name|writeBytes
argument_list|(
literal|"blah blah blah\n"
argument_list|)
expr_stmt|;
name|f
operator|.
name|close
argument_list|()
expr_stmt|;
name|FileInputFormat
operator|.
name|setInputPaths
argument_list|(
name|conf
argument_list|,
name|inFile
argument_list|)
expr_stmt|;
name|FileOutputFormat
operator|.
name|setOutputPath
argument_list|(
name|conf
argument_list|,
operator|new
name|Path
argument_list|(
name|testdir
argument_list|,
literal|"out"
argument_list|)
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setMapperClass
argument_list|(
name|IdentityMapper
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
name|conf
operator|.
name|setOutputKeyClass
argument_list|(
name|LongWritable
operator|.
name|class
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setOutputValueClass
argument_list|(
name|Text
operator|.
name|class
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setMaxMapAttempts
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|// partition too low
name|conf
operator|.
name|setBoolean
argument_list|(
literal|"test.testmapred.badpartition"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|boolean
name|pass
init|=
literal|true
decl_stmt|;
try|try
block|{
name|JobClient
operator|.
name|runJob
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|pass
operator|=
literal|false
expr_stmt|;
block|}
name|assertFalse
argument_list|(
literal|"should fail for partition< 0"
argument_list|,
name|pass
argument_list|)
expr_stmt|;
comment|// partition too high
name|conf
operator|.
name|setBoolean
argument_list|(
literal|"test.testmapred.badpartition"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|pass
operator|=
literal|true
expr_stmt|;
try|try
block|{
name|JobClient
operator|.
name|runJob
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|pass
operator|=
literal|false
expr_stmt|;
block|}
name|assertFalse
argument_list|(
literal|"should fail for partition>= numPartitions"
argument_list|,
name|pass
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

