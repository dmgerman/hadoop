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
name|FSDataInputStream
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
name|mapreduce
operator|.
name|MRJobConfig
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
name|yarn
operator|.
name|conf
operator|.
name|YarnConfiguration
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

begin_comment
comment|/**  * Simple MapReduce to test ability of the MRAppMaster to request and use  * OPPORTUNISTIC containers.  * This test runs a simple external merge sort using MapReduce.  * The Hadoop framework's merge on the reduce side will merge the partitions  * created to generate the final output which is sorted on the key.  */
end_comment

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
name|value
operator|=
block|{
literal|"unchecked"
block|,
literal|"deprecation"
block|}
argument_list|)
DECL|class|TestMROpportunisticMaps
specifier|public
class|class
name|TestMROpportunisticMaps
block|{
comment|// Where MR job's input will reside.
DECL|field|INPUT_DIR
specifier|private
specifier|static
specifier|final
name|Path
name|INPUT_DIR
init|=
operator|new
name|Path
argument_list|(
literal|"/test/input"
argument_list|)
decl_stmt|;
comment|// Where output goes.
DECL|field|OUTPUT
specifier|private
specifier|static
specifier|final
name|Path
name|OUTPUT
init|=
operator|new
name|Path
argument_list|(
literal|"/test/output"
argument_list|)
decl_stmt|;
comment|/**    * Test will run with 4 Maps, All OPPORTUNISTIC.    * @throws Exception    */
annotation|@
name|Test
DECL|method|testAllOpportunisticMaps ()
specifier|public
name|void
name|testAllOpportunisticMaps
parameter_list|()
throws|throws
name|Exception
block|{
name|doTest
argument_list|(
literal|4
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|4
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test will run with 4 Maps, 2 OPPORTUNISTIC and 2 GUARANTEED.    * @throws Exception    */
annotation|@
name|Test
DECL|method|testHalfOpportunisticMaps ()
specifier|public
name|void
name|testHalfOpportunisticMaps
parameter_list|()
throws|throws
name|Exception
block|{
name|doTest
argument_list|(
literal|4
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|)
expr_stmt|;
block|}
DECL|method|doTest (int numMappers, int numReducers, int numNodes, int percent)
specifier|public
name|void
name|doTest
parameter_list|(
name|int
name|numMappers
parameter_list|,
name|int
name|numReducers
parameter_list|,
name|int
name|numNodes
parameter_list|,
name|int
name|percent
parameter_list|)
throws|throws
name|Exception
block|{
name|doTest
argument_list|(
name|numMappers
argument_list|,
name|numReducers
argument_list|,
name|numNodes
argument_list|,
literal|1000
argument_list|,
name|percent
argument_list|)
expr_stmt|;
block|}
DECL|method|doTest (int numMappers, int numReducers, int numNodes, int numLines, int percent)
specifier|public
name|void
name|doTest
parameter_list|(
name|int
name|numMappers
parameter_list|,
name|int
name|numReducers
parameter_list|,
name|int
name|numNodes
parameter_list|,
name|int
name|numLines
parameter_list|,
name|int
name|percent
parameter_list|)
throws|throws
name|Exception
block|{
name|MiniDFSCluster
name|dfsCluster
init|=
literal|null
decl_stmt|;
name|MiniMRClientCluster
name|mrCluster
init|=
literal|null
decl_stmt|;
name|FileSystem
name|fileSystem
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
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|AMRM_PROXY_ENABLED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|OPPORTUNISTIC_CONTAINER_ALLOCATION_ENABLED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|DIST_SCHEDULING_ENABLED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|YarnConfiguration
operator|.
name|NM_OPPORTUNISTIC_CONTAINERS_MAX_QUEUE_LENGTH
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|dfsCluster
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
name|numNodes
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|fileSystem
operator|=
name|dfsCluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
name|mrCluster
operator|=
name|MiniMRClientClusterFactory
operator|.
name|create
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
argument_list|,
name|numNodes
argument_list|,
name|conf
argument_list|)
expr_stmt|;
comment|// Generate input.
name|createInput
argument_list|(
name|fileSystem
argument_list|,
name|numMappers
argument_list|,
name|numLines
argument_list|)
expr_stmt|;
comment|// Run the test.
name|runMergeTest
argument_list|(
operator|new
name|JobConf
argument_list|(
name|conf
argument_list|)
argument_list|,
name|fileSystem
argument_list|,
name|numMappers
argument_list|,
name|numReducers
argument_list|,
name|numLines
argument_list|,
name|percent
argument_list|)
expr_stmt|;
block|}
finally|finally
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
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|createInput (FileSystem fs, int numMappers, int numLines)
specifier|private
name|void
name|createInput
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|int
name|numMappers
parameter_list|,
name|int
name|numLines
parameter_list|)
throws|throws
name|Exception
block|{
name|fs
operator|.
name|delete
argument_list|(
name|INPUT_DIR
argument_list|,
literal|true
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
name|INPUT_DIR
argument_list|,
literal|"input_"
operator|+
name|i
operator|+
literal|".txt"
argument_list|)
argument_list|)
decl_stmt|;
name|Writer
name|writer
init|=
operator|new
name|OutputStreamWriter
argument_list|(
name|os
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|numLines
condition|;
name|j
operator|++
control|)
block|{
comment|// Create sorted key, value pairs.
name|int
name|k
init|=
name|j
operator|+
literal|1
decl_stmt|;
name|String
name|formattedNumber
init|=
name|String
operator|.
name|format
argument_list|(
literal|"%09d"
argument_list|,
name|k
argument_list|)
decl_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|formattedNumber
operator|+
literal|" "
operator|+
name|formattedNumber
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|runMergeTest (JobConf job, FileSystem fileSystem, int numMappers, int numReducers, int numLines, int percent)
specifier|private
name|void
name|runMergeTest
parameter_list|(
name|JobConf
name|job
parameter_list|,
name|FileSystem
name|fileSystem
parameter_list|,
name|int
name|numMappers
parameter_list|,
name|int
name|numReducers
parameter_list|,
name|int
name|numLines
parameter_list|,
name|int
name|percent
parameter_list|)
throws|throws
name|Exception
block|{
name|fileSystem
operator|.
name|delete
argument_list|(
name|OUTPUT
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|job
operator|.
name|setJobName
argument_list|(
literal|"Test"
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
name|RunningJob
name|submittedJob
init|=
literal|null
decl_stmt|;
name|FileInputFormat
operator|.
name|setInputPaths
argument_list|(
name|job
argument_list|,
name|INPUT_DIR
argument_list|)
expr_stmt|;
name|FileOutputFormat
operator|.
name|setOutputPath
argument_list|(
name|job
argument_list|,
name|OUTPUT
argument_list|)
expr_stmt|;
name|job
operator|.
name|set
argument_list|(
literal|"mapreduce.output.textoutputformat.separator"
argument_list|,
literal|" "
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
name|Text
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
name|setMapperClass
argument_list|(
name|MyMapper
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setPartitionerClass
argument_list|(
name|MyPartitioner
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setOutputFormat
argument_list|(
name|TextOutputFormat
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
comment|// All OPPORTUNISTIC
name|job
operator|.
name|setInt
argument_list|(
name|MRJobConfig
operator|.
name|MR_NUM_OPPORTUNISTIC_MAPS_PERCENT
argument_list|,
name|percent
argument_list|)
expr_stmt|;
name|job
operator|.
name|setInt
argument_list|(
literal|"mapreduce.map.maxattempts"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|job
operator|.
name|setInt
argument_list|(
literal|"mapreduce.reduce.maxattempts"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|job
operator|.
name|setInt
argument_list|(
literal|"mapred.test.num_lines"
argument_list|,
name|numLines
argument_list|)
expr_stmt|;
name|job
operator|.
name|setBoolean
argument_list|(
name|MRJobConfig
operator|.
name|MR_ENCRYPTED_INTERMEDIATE_DATA
argument_list|,
literal|true
argument_list|)
expr_stmt|;
try|try
block|{
name|submittedJob
operator|=
name|client
operator|.
name|submitJob
argument_list|(
name|job
argument_list|)
expr_stmt|;
try|try
block|{
if|if
condition|(
operator|!
name|client
operator|.
name|monitorAndPrintJob
argument_list|(
name|job
argument_list|,
name|submittedJob
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Job failed!"
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Job failed with: "
operator|+
name|ioe
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|verifyOutput
argument_list|(
name|fileSystem
argument_list|,
name|numMappers
argument_list|,
name|numLines
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|verifyOutput (FileSystem fileSystem, int numMappers, int numLines)
specifier|private
name|void
name|verifyOutput
parameter_list|(
name|FileSystem
name|fileSystem
parameter_list|,
name|int
name|numMappers
parameter_list|,
name|int
name|numLines
parameter_list|)
throws|throws
name|Exception
block|{
name|FSDataInputStream
name|dis
init|=
literal|null
decl_stmt|;
name|long
name|numValidRecords
init|=
literal|0
decl_stmt|;
name|long
name|numInvalidRecords
init|=
literal|0
decl_stmt|;
name|String
name|prevKeyValue
init|=
literal|"000000000"
decl_stmt|;
name|Path
index|[]
name|fileList
init|=
name|FileUtil
operator|.
name|stat2Paths
argument_list|(
name|fileSystem
operator|.
name|listStatus
argument_list|(
name|OUTPUT
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
name|Path
name|outFile
range|:
name|fileList
control|)
block|{
try|try
block|{
name|dis
operator|=
name|fileSystem
operator|.
name|open
argument_list|(
name|outFile
argument_list|)
expr_stmt|;
name|String
name|record
decl_stmt|;
while|while
condition|(
operator|(
name|record
operator|=
name|dis
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
comment|// Split the line into key and value.
name|int
name|blankPos
init|=
name|record
operator|.
name|indexOf
argument_list|(
literal|" "
argument_list|)
decl_stmt|;
name|String
name|keyString
init|=
name|record
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|blankPos
argument_list|)
decl_stmt|;
name|String
name|valueString
init|=
name|record
operator|.
name|substring
argument_list|(
name|blankPos
operator|+
literal|1
argument_list|)
decl_stmt|;
comment|// Check for sorted output and correctness of record.
if|if
condition|(
name|keyString
operator|.
name|compareTo
argument_list|(
name|prevKeyValue
argument_list|)
operator|>=
literal|0
operator|&&
name|keyString
operator|.
name|equals
argument_list|(
name|valueString
argument_list|)
condition|)
block|{
name|prevKeyValue
operator|=
name|keyString
expr_stmt|;
name|numValidRecords
operator|++
expr_stmt|;
block|}
else|else
block|{
name|numInvalidRecords
operator|++
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|dis
operator|!=
literal|null
condition|)
block|{
name|dis
operator|.
name|close
argument_list|()
expr_stmt|;
name|dis
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
comment|// Make sure we got all input records in the output in sorted order.
name|assertEquals
argument_list|(
call|(
name|long
call|)
argument_list|(
name|numMappers
operator|*
name|numLines
argument_list|)
argument_list|,
name|numValidRecords
argument_list|)
expr_stmt|;
comment|// Make sure there is no extraneous invalid record.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|numInvalidRecords
argument_list|)
expr_stmt|;
block|}
comment|/**    * A mapper implementation that assumes that key text contains valid integers    * in displayable form.    */
DECL|class|MyMapper
specifier|public
specifier|static
class|class
name|MyMapper
extends|extends
name|MapReduceBase
implements|implements
name|Mapper
argument_list|<
name|LongWritable
argument_list|,
name|Text
argument_list|,
name|Text
argument_list|,
name|Text
argument_list|>
block|{
DECL|field|keyText
specifier|private
name|Text
name|keyText
decl_stmt|;
DECL|field|valueText
specifier|private
name|Text
name|valueText
decl_stmt|;
DECL|method|MyMapper ()
specifier|public
name|MyMapper
parameter_list|()
block|{
name|keyText
operator|=
operator|new
name|Text
argument_list|()
expr_stmt|;
name|valueText
operator|=
operator|new
name|Text
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|map (LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter)
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
name|String
name|record
init|=
name|value
operator|.
name|toString
argument_list|()
decl_stmt|;
name|int
name|blankPos
init|=
name|record
operator|.
name|indexOf
argument_list|(
literal|" "
argument_list|)
decl_stmt|;
name|keyText
operator|.
name|set
argument_list|(
name|record
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|blankPos
argument_list|)
argument_list|)
expr_stmt|;
name|valueText
operator|.
name|set
argument_list|(
name|record
operator|.
name|substring
argument_list|(
name|blankPos
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|output
operator|.
name|collect
argument_list|(
name|keyText
argument_list|,
name|valueText
argument_list|)
expr_stmt|;
block|}
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{     }
block|}
comment|/**    * Partitioner implementation to make sure that output is in total sorted    * order.  We basically route key ranges to different reducers such that    * key values monotonically increase with the partition number.  For example,    * in a test with 4 reducers, the keys are numbers from 1 to 1000 in the    * form "000000001" to "000001000" in each input file. The keys "000000001"    * to "000000250" are routed to partition 0, "000000251" to "000000500" are    * routed to partition 1.    */
DECL|class|MyPartitioner
specifier|static
class|class
name|MyPartitioner
implements|implements
name|Partitioner
argument_list|<
name|Text
argument_list|,
name|Text
argument_list|>
block|{
DECL|field|job
specifier|private
name|JobConf
name|job
decl_stmt|;
DECL|method|MyPartitioner ()
specifier|public
name|MyPartitioner
parameter_list|()
block|{     }
DECL|method|configure (JobConf jobConf)
specifier|public
name|void
name|configure
parameter_list|(
name|JobConf
name|jobConf
parameter_list|)
block|{
name|this
operator|.
name|job
operator|=
name|jobConf
expr_stmt|;
block|}
DECL|method|getPartition (Text key, Text value, int numPartitions)
specifier|public
name|int
name|getPartition
parameter_list|(
name|Text
name|key
parameter_list|,
name|Text
name|value
parameter_list|,
name|int
name|numPartitions
parameter_list|)
block|{
name|int
name|keyValue
init|=
literal|0
decl_stmt|;
try|try
block|{
name|keyValue
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|key
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|nfe
parameter_list|)
block|{
name|keyValue
operator|=
literal|0
expr_stmt|;
block|}
name|int
name|partitionNumber
init|=
operator|(
name|numPartitions
operator|*
operator|(
name|Math
operator|.
name|max
argument_list|(
literal|0
argument_list|,
name|keyValue
operator|-
literal|1
argument_list|)
operator|)
operator|)
operator|/
name|job
operator|.
name|getInt
argument_list|(
literal|"mapred.test.num_lines"
argument_list|,
literal|10000
argument_list|)
decl_stmt|;
return|return
name|partitionNumber
return|;
block|}
block|}
block|}
end_class

end_unit

