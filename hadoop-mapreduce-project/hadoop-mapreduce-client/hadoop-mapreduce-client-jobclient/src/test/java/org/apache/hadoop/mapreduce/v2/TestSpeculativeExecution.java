begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
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
name|logging
operator|.
name|Log
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
name|logging
operator|.
name|LogFactory
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
name|FSDataOutputStream
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
name|fs
operator|.
name|permission
operator|.
name|FsPermission
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
name|IntWritable
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
name|Counters
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
name|Job
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
name|JobCounter
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
name|JobStatus
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
name|mapreduce
operator|.
name|Mapper
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
name|Reducer
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
name|TaskAttemptID
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
name|TaskType
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
name|v2
operator|.
name|api
operator|.
name|records
operator|.
name|TaskAttemptId
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
name|v2
operator|.
name|app
operator|.
name|speculate
operator|.
name|LegacyTaskRuntimeEstimator
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
name|v2
operator|.
name|app
operator|.
name|speculate
operator|.
name|TaskRuntimeEstimator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
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
DECL|class|TestSpeculativeExecution
specifier|public
class|class
name|TestSpeculativeExecution
block|{
comment|/*    * This class is used to control when speculative execution happens.    */
DECL|class|TestSpecEstimator
specifier|public
specifier|static
class|class
name|TestSpecEstimator
extends|extends
name|LegacyTaskRuntimeEstimator
block|{
DECL|field|SPECULATE_THIS
specifier|private
specifier|static
specifier|final
name|long
name|SPECULATE_THIS
init|=
literal|999999L
decl_stmt|;
DECL|method|TestSpecEstimator ()
specifier|public
name|TestSpecEstimator
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
comment|/*      * This will only be called if speculative execution is turned on.      *       * If either mapper or reducer speculation is turned on, this will be      * called.      *       * This will cause speculation to engage for the first mapper or first      * reducer (that is, attempt ID "*_m_000000_0" or "*_r_000000_0")      *       * If this attempt is killed, the retry will have attempt id 1, so it      * will not engage speculation again.      */
annotation|@
name|Override
DECL|method|estimatedRuntime (TaskAttemptId id)
specifier|public
name|long
name|estimatedRuntime
parameter_list|(
name|TaskAttemptId
name|id
parameter_list|)
block|{
if|if
condition|(
operator|(
name|id
operator|.
name|getTaskId
argument_list|()
operator|.
name|getId
argument_list|()
operator|==
literal|0
operator|)
operator|&&
operator|(
name|id
operator|.
name|getId
argument_list|()
operator|==
literal|0
operator|)
condition|)
block|{
return|return
name|SPECULATE_THIS
return|;
block|}
return|return
name|super
operator|.
name|estimatedRuntime
argument_list|(
name|id
argument_list|)
return|;
block|}
block|}
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TestSpeculativeExecution
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|mrCluster
specifier|protected
specifier|static
name|MiniMRYarnCluster
name|mrCluster
decl_stmt|;
DECL|field|initialConf
specifier|private
specifier|static
name|Configuration
name|initialConf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
DECL|field|localFs
specifier|private
specifier|static
name|FileSystem
name|localFs
decl_stmt|;
static|static
block|{
try|try
block|{
name|localFs
operator|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|initialConf
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|io
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"problem getting local fs"
argument_list|,
name|io
argument_list|)
throw|;
block|}
block|}
DECL|field|TEST_ROOT_DIR
specifier|private
specifier|static
name|Path
name|TEST_ROOT_DIR
init|=
operator|new
name|Path
argument_list|(
literal|"target"
argument_list|,
name|TestSpeculativeExecution
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"-tmpDir"
argument_list|)
operator|.
name|makeQualified
argument_list|(
name|localFs
operator|.
name|getUri
argument_list|()
argument_list|,
name|localFs
operator|.
name|getWorkingDirectory
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|APP_JAR
specifier|static
name|Path
name|APP_JAR
init|=
operator|new
name|Path
argument_list|(
name|TEST_ROOT_DIR
argument_list|,
literal|"MRAppJar.jar"
argument_list|)
decl_stmt|;
DECL|field|TEST_OUT_DIR
specifier|private
specifier|static
name|Path
name|TEST_OUT_DIR
init|=
operator|new
name|Path
argument_list|(
name|TEST_ROOT_DIR
argument_list|,
literal|"test.out.dir"
argument_list|)
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|setup ()
specifier|public
specifier|static
name|void
name|setup
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
operator|(
operator|new
name|File
argument_list|(
name|MiniMRYarnCluster
operator|.
name|APPJAR
argument_list|)
operator|)
operator|.
name|exists
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"MRAppJar "
operator|+
name|MiniMRYarnCluster
operator|.
name|APPJAR
operator|+
literal|" not found. Not running test."
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|mrCluster
operator|==
literal|null
condition|)
block|{
name|mrCluster
operator|=
operator|new
name|MiniMRYarnCluster
argument_list|(
name|TestSpeculativeExecution
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|mrCluster
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|mrCluster
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
comment|// workaround the absent public distcache.
name|localFs
operator|.
name|copyFromLocalFile
argument_list|(
operator|new
name|Path
argument_list|(
name|MiniMRYarnCluster
operator|.
name|APPJAR
argument_list|)
argument_list|,
name|APP_JAR
argument_list|)
expr_stmt|;
name|localFs
operator|.
name|setPermission
argument_list|(
name|APP_JAR
argument_list|,
operator|new
name|FsPermission
argument_list|(
literal|"700"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|tearDown ()
specifier|public
specifier|static
name|void
name|tearDown
parameter_list|()
block|{
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
name|mrCluster
operator|=
literal|null
expr_stmt|;
block|}
block|}
DECL|class|SpeculativeMapper
specifier|public
specifier|static
class|class
name|SpeculativeMapper
extends|extends
name|Mapper
argument_list|<
name|Object
argument_list|,
name|Text
argument_list|,
name|Text
argument_list|,
name|IntWritable
argument_list|>
block|{
DECL|method|map (Object key, Text value, Context context)
specifier|public
name|void
name|map
parameter_list|(
name|Object
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
comment|// Make one mapper slower for speculative execution
name|TaskAttemptID
name|taid
init|=
name|context
operator|.
name|getTaskAttemptID
argument_list|()
decl_stmt|;
name|long
name|sleepTime
init|=
literal|100
decl_stmt|;
name|Configuration
name|conf
init|=
name|context
operator|.
name|getConfiguration
argument_list|()
decl_stmt|;
name|boolean
name|test_speculate_map
init|=
name|conf
operator|.
name|getBoolean
argument_list|(
name|MRJobConfig
operator|.
name|MAP_SPECULATIVE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
comment|// IF TESTING MAPPER SPECULATIVE EXECUTION:
comment|//   Make the "*_m_000000_0" attempt take much longer than the others.
comment|//   When speculative execution is enabled, this should cause the attempt
comment|//   to be killed and restarted. At that point, the attempt ID will be
comment|//   "*_m_000000_1", so sleepTime will still remain 100ms.
if|if
condition|(
operator|(
name|taid
operator|.
name|getTaskType
argument_list|()
operator|==
name|TaskType
operator|.
name|MAP
operator|)
operator|&&
name|test_speculate_map
operator|&&
operator|(
name|taid
operator|.
name|getTaskID
argument_list|()
operator|.
name|getId
argument_list|()
operator|==
literal|0
operator|)
operator|&&
operator|(
name|taid
operator|.
name|getId
argument_list|()
operator|==
literal|0
operator|)
condition|)
block|{
name|sleepTime
operator|=
literal|10000
expr_stmt|;
block|}
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|sleepTime
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
comment|// Ignore
block|}
name|context
operator|.
name|write
argument_list|(
name|value
argument_list|,
operator|new
name|IntWritable
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|SpeculativeReducer
specifier|public
specifier|static
class|class
name|SpeculativeReducer
extends|extends
name|Reducer
argument_list|<
name|Text
argument_list|,
name|IntWritable
argument_list|,
name|Text
argument_list|,
name|IntWritable
argument_list|>
block|{
DECL|method|reduce (Text key, Iterable<IntWritable> values, Context context)
specifier|public
name|void
name|reduce
parameter_list|(
name|Text
name|key
parameter_list|,
name|Iterable
argument_list|<
name|IntWritable
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
comment|// Make one reducer slower for speculative execution
name|TaskAttemptID
name|taid
init|=
name|context
operator|.
name|getTaskAttemptID
argument_list|()
decl_stmt|;
name|long
name|sleepTime
init|=
literal|100
decl_stmt|;
name|Configuration
name|conf
init|=
name|context
operator|.
name|getConfiguration
argument_list|()
decl_stmt|;
name|boolean
name|test_speculate_reduce
init|=
name|conf
operator|.
name|getBoolean
argument_list|(
name|MRJobConfig
operator|.
name|REDUCE_SPECULATIVE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
comment|// IF TESTING REDUCE SPECULATIVE EXECUTION:
comment|//   Make the "*_r_000000_0" attempt take much longer than the others.
comment|//   When speculative execution is enabled, this should cause the attempt
comment|//   to be killed and restarted. At that point, the attempt ID will be
comment|//   "*_r_000000_1", so sleepTime will still remain 100ms.
if|if
condition|(
operator|(
name|taid
operator|.
name|getTaskType
argument_list|()
operator|==
name|TaskType
operator|.
name|REDUCE
operator|)
operator|&&
name|test_speculate_reduce
operator|&&
operator|(
name|taid
operator|.
name|getTaskID
argument_list|()
operator|.
name|getId
argument_list|()
operator|==
literal|0
operator|)
operator|&&
operator|(
name|taid
operator|.
name|getId
argument_list|()
operator|==
literal|0
operator|)
condition|)
block|{
name|sleepTime
operator|=
literal|10000
expr_stmt|;
block|}
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|sleepTime
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
comment|// Ignore
block|}
name|context
operator|.
name|write
argument_list|(
name|key
argument_list|,
operator|new
name|IntWritable
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testSpeculativeExecution ()
specifier|public
name|void
name|testSpeculativeExecution
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
operator|!
operator|(
operator|new
name|File
argument_list|(
name|MiniMRYarnCluster
operator|.
name|APPJAR
argument_list|)
operator|)
operator|.
name|exists
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"MRAppJar "
operator|+
name|MiniMRYarnCluster
operator|.
name|APPJAR
operator|+
literal|" not found. Not running test."
argument_list|)
expr_stmt|;
return|return;
block|}
comment|/*------------------------------------------------------------------      * Test that Map/Red does not speculate if MAP_SPECULATIVE and       * REDUCE_SPECULATIVE are both false.      * -----------------------------------------------------------------      */
name|Job
name|job
init|=
name|runSpecTest
argument_list|(
literal|false
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|boolean
name|succeeded
init|=
name|job
operator|.
name|waitForCompletion
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|succeeded
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|JobStatus
operator|.
name|State
operator|.
name|SUCCEEDED
argument_list|,
name|job
operator|.
name|getJobState
argument_list|()
argument_list|)
expr_stmt|;
name|Counters
name|counters
init|=
name|job
operator|.
name|getCounters
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|counters
operator|.
name|findCounter
argument_list|(
name|JobCounter
operator|.
name|TOTAL_LAUNCHED_MAPS
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|counters
operator|.
name|findCounter
argument_list|(
name|JobCounter
operator|.
name|TOTAL_LAUNCHED_REDUCES
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|counters
operator|.
name|findCounter
argument_list|(
name|JobCounter
operator|.
name|NUM_FAILED_MAPS
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
comment|/*----------------------------------------------------------------------      * Test that Mapper speculates if MAP_SPECULATIVE is true and      * REDUCE_SPECULATIVE is false.      * ---------------------------------------------------------------------      */
name|job
operator|=
name|runSpecTest
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|succeeded
operator|=
name|job
operator|.
name|waitForCompletion
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|succeeded
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|JobStatus
operator|.
name|State
operator|.
name|SUCCEEDED
argument_list|,
name|job
operator|.
name|getJobState
argument_list|()
argument_list|)
expr_stmt|;
name|counters
operator|=
name|job
operator|.
name|getCounters
argument_list|()
expr_stmt|;
comment|// The long-running map will be killed and a new one started.
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|counters
operator|.
name|findCounter
argument_list|(
name|JobCounter
operator|.
name|TOTAL_LAUNCHED_MAPS
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|counters
operator|.
name|findCounter
argument_list|(
name|JobCounter
operator|.
name|TOTAL_LAUNCHED_REDUCES
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|counters
operator|.
name|findCounter
argument_list|(
name|JobCounter
operator|.
name|NUM_FAILED_MAPS
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|counters
operator|.
name|findCounter
argument_list|(
name|JobCounter
operator|.
name|NUM_KILLED_MAPS
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
comment|/*----------------------------------------------------------------------      * Test that Reducer speculates if REDUCE_SPECULATIVE is true and      * MAP_SPECULATIVE is false.      * ---------------------------------------------------------------------      */
name|job
operator|=
name|runSpecTest
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|succeeded
operator|=
name|job
operator|.
name|waitForCompletion
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|succeeded
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|JobStatus
operator|.
name|State
operator|.
name|SUCCEEDED
argument_list|,
name|job
operator|.
name|getJobState
argument_list|()
argument_list|)
expr_stmt|;
name|counters
operator|=
name|job
operator|.
name|getCounters
argument_list|()
expr_stmt|;
comment|// The long-running map will be killed and a new one started.
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|counters
operator|.
name|findCounter
argument_list|(
name|JobCounter
operator|.
name|TOTAL_LAUNCHED_MAPS
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|counters
operator|.
name|findCounter
argument_list|(
name|JobCounter
operator|.
name|TOTAL_LAUNCHED_REDUCES
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|createTempFile (String filename, String contents)
specifier|private
name|Path
name|createTempFile
parameter_list|(
name|String
name|filename
parameter_list|,
name|String
name|contents
parameter_list|)
throws|throws
name|IOException
block|{
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
name|TEST_ROOT_DIR
argument_list|,
name|filename
argument_list|)
decl_stmt|;
name|FSDataOutputStream
name|os
init|=
name|localFs
operator|.
name|create
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|os
operator|.
name|writeBytes
argument_list|(
name|contents
argument_list|)
expr_stmt|;
name|os
operator|.
name|close
argument_list|()
expr_stmt|;
name|localFs
operator|.
name|setPermission
argument_list|(
name|path
argument_list|,
operator|new
name|FsPermission
argument_list|(
literal|"700"
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|path
return|;
block|}
DECL|method|runSpecTest (boolean mapspec, boolean redspec)
specifier|private
name|Job
name|runSpecTest
parameter_list|(
name|boolean
name|mapspec
parameter_list|,
name|boolean
name|redspec
parameter_list|)
throws|throws
name|IOException
throws|,
name|ClassNotFoundException
throws|,
name|InterruptedException
block|{
name|Path
name|first
init|=
name|createTempFile
argument_list|(
literal|"specexec_map_input1"
argument_list|,
literal|"a\nz"
argument_list|)
decl_stmt|;
name|Path
name|secnd
init|=
name|createTempFile
argument_list|(
literal|"specexec_map_input2"
argument_list|,
literal|"a\nz"
argument_list|)
decl_stmt|;
name|Configuration
name|conf
init|=
name|mrCluster
operator|.
name|getConfig
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|MRJobConfig
operator|.
name|MAP_SPECULATIVE
argument_list|,
name|mapspec
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|MRJobConfig
operator|.
name|REDUCE_SPECULATIVE
argument_list|,
name|redspec
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setClass
argument_list|(
name|MRJobConfig
operator|.
name|MR_AM_TASK_ESTIMATOR
argument_list|,
name|TestSpecEstimator
operator|.
name|class
argument_list|,
name|TaskRuntimeEstimator
operator|.
name|class
argument_list|)
expr_stmt|;
name|Job
name|job
init|=
name|Job
operator|.
name|getInstance
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|job
operator|.
name|setJarByClass
argument_list|(
name|TestSpeculativeExecution
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setMapperClass
argument_list|(
name|SpeculativeMapper
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setReducerClass
argument_list|(
name|SpeculativeReducer
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
name|IntWritable
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setNumReduceTasks
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|FileInputFormat
operator|.
name|setInputPaths
argument_list|(
name|job
argument_list|,
name|first
argument_list|)
expr_stmt|;
name|FileInputFormat
operator|.
name|addInputPath
argument_list|(
name|job
argument_list|,
name|secnd
argument_list|)
expr_stmt|;
name|FileOutputFormat
operator|.
name|setOutputPath
argument_list|(
name|job
argument_list|,
name|TEST_OUT_DIR
argument_list|)
expr_stmt|;
comment|// Delete output directory if it exists.
try|try
block|{
name|localFs
operator|.
name|delete
argument_list|(
name|TEST_OUT_DIR
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
comment|// Creates the Job Configuration
name|job
operator|.
name|addFileToClassPath
argument_list|(
name|APP_JAR
argument_list|)
expr_stmt|;
comment|// The AppMaster jar itself.
name|job
operator|.
name|setMaxMapAttempts
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|job
operator|.
name|submit
argument_list|()
expr_stmt|;
return|return
name|job
return|;
block|}
block|}
end_class

end_unit

