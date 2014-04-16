begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
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
name|DataInput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|conf
operator|.
name|Configured
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
name|NullWritable
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
name|mapreduce
operator|.
name|InputFormat
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
name|InputSplit
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
name|Partitioner
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
name|RecordReader
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
name|TaskAttemptContext
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
name|util
operator|.
name|Tool
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

begin_comment
comment|/**  * Dummy class for testing MR framefork. Sleeps for a defined period   * of time in mapper and reducer. Generates fake input for map / reduce   * jobs. Note that generated number of input pairs is in the order   * of<code>numMappers * mapSleepTime / 100</code>, so the job uses  * some disk space.  */
end_comment

begin_class
DECL|class|SleepJob
specifier|public
class|class
name|SleepJob
extends|extends
name|Configured
implements|implements
name|Tool
block|{
DECL|field|MAP_SLEEP_COUNT
specifier|public
specifier|static
name|String
name|MAP_SLEEP_COUNT
init|=
literal|"mapreduce.sleepjob.map.sleep.count"
decl_stmt|;
DECL|field|REDUCE_SLEEP_COUNT
specifier|public
specifier|static
name|String
name|REDUCE_SLEEP_COUNT
init|=
literal|"mapreduce.sleepjob.reduce.sleep.count"
decl_stmt|;
DECL|field|MAP_SLEEP_TIME
specifier|public
specifier|static
name|String
name|MAP_SLEEP_TIME
init|=
literal|"mapreduce.sleepjob.map.sleep.time"
decl_stmt|;
DECL|field|REDUCE_SLEEP_TIME
specifier|public
specifier|static
name|String
name|REDUCE_SLEEP_TIME
init|=
literal|"mapreduce.sleepjob.reduce.sleep.time"
decl_stmt|;
DECL|class|SleepJobPartitioner
specifier|public
specifier|static
class|class
name|SleepJobPartitioner
extends|extends
name|Partitioner
argument_list|<
name|IntWritable
argument_list|,
name|NullWritable
argument_list|>
block|{
DECL|method|getPartition (IntWritable k, NullWritable v, int numPartitions)
specifier|public
name|int
name|getPartition
parameter_list|(
name|IntWritable
name|k
parameter_list|,
name|NullWritable
name|v
parameter_list|,
name|int
name|numPartitions
parameter_list|)
block|{
return|return
name|k
operator|.
name|get
argument_list|()
operator|%
name|numPartitions
return|;
block|}
block|}
DECL|class|EmptySplit
specifier|public
specifier|static
class|class
name|EmptySplit
extends|extends
name|InputSplit
implements|implements
name|Writable
block|{
DECL|method|write (DataOutput out)
specifier|public
name|void
name|write
parameter_list|(
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{ }
DECL|method|readFields (DataInput in)
specifier|public
name|void
name|readFields
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{ }
DECL|method|getLength ()
specifier|public
name|long
name|getLength
parameter_list|()
block|{
return|return
literal|0L
return|;
block|}
DECL|method|getLocations ()
specifier|public
name|String
index|[]
name|getLocations
parameter_list|()
block|{
return|return
operator|new
name|String
index|[
literal|0
index|]
return|;
block|}
block|}
DECL|class|SleepInputFormat
specifier|public
specifier|static
class|class
name|SleepInputFormat
extends|extends
name|InputFormat
argument_list|<
name|IntWritable
argument_list|,
name|IntWritable
argument_list|>
block|{
DECL|method|getSplits (JobContext jobContext)
specifier|public
name|List
argument_list|<
name|InputSplit
argument_list|>
name|getSplits
parameter_list|(
name|JobContext
name|jobContext
parameter_list|)
block|{
name|List
argument_list|<
name|InputSplit
argument_list|>
name|ret
init|=
operator|new
name|ArrayList
argument_list|<
name|InputSplit
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|numSplits
init|=
name|jobContext
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getInt
argument_list|(
name|MRJobConfig
operator|.
name|NUM_MAPS
argument_list|,
literal|1
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
name|numSplits
condition|;
operator|++
name|i
control|)
block|{
name|ret
operator|.
name|add
argument_list|(
operator|new
name|EmptySplit
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
DECL|method|createRecordReader ( InputSplit ignored, TaskAttemptContext taskContext)
specifier|public
name|RecordReader
argument_list|<
name|IntWritable
argument_list|,
name|IntWritable
argument_list|>
name|createRecordReader
parameter_list|(
name|InputSplit
name|ignored
parameter_list|,
name|TaskAttemptContext
name|taskContext
parameter_list|)
throws|throws
name|IOException
block|{
name|Configuration
name|conf
init|=
name|taskContext
operator|.
name|getConfiguration
argument_list|()
decl_stmt|;
specifier|final
name|int
name|count
init|=
name|conf
operator|.
name|getInt
argument_list|(
name|MAP_SLEEP_COUNT
argument_list|,
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|count
operator|<
literal|0
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Invalid map count: "
operator|+
name|count
argument_list|)
throw|;
specifier|final
name|int
name|redcount
init|=
name|conf
operator|.
name|getInt
argument_list|(
name|REDUCE_SLEEP_COUNT
argument_list|,
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|redcount
operator|<
literal|0
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Invalid reduce count: "
operator|+
name|redcount
argument_list|)
throw|;
specifier|final
name|int
name|emitPerMapTask
init|=
operator|(
name|redcount
operator|*
name|taskContext
operator|.
name|getNumReduceTasks
argument_list|()
operator|)
decl_stmt|;
return|return
operator|new
name|RecordReader
argument_list|<
name|IntWritable
argument_list|,
name|IntWritable
argument_list|>
argument_list|()
block|{
specifier|private
name|int
name|records
init|=
literal|0
decl_stmt|;
specifier|private
name|int
name|emitCount
init|=
literal|0
decl_stmt|;
specifier|private
name|IntWritable
name|key
init|=
literal|null
decl_stmt|;
specifier|private
name|IntWritable
name|value
init|=
literal|null
decl_stmt|;
specifier|public
name|void
name|initialize
parameter_list|(
name|InputSplit
name|split
parameter_list|,
name|TaskAttemptContext
name|context
parameter_list|)
block|{         }
specifier|public
name|boolean
name|nextKeyValue
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|count
operator|==
literal|0
condition|)
block|{
return|return
literal|false
return|;
block|}
name|key
operator|=
operator|new
name|IntWritable
argument_list|()
expr_stmt|;
name|key
operator|.
name|set
argument_list|(
name|emitCount
argument_list|)
expr_stmt|;
name|int
name|emit
init|=
name|emitPerMapTask
operator|/
name|count
decl_stmt|;
if|if
condition|(
operator|(
name|emitPerMapTask
operator|)
operator|%
name|count
operator|>
name|records
condition|)
block|{
operator|++
name|emit
expr_stmt|;
block|}
name|emitCount
operator|+=
name|emit
expr_stmt|;
name|value
operator|=
operator|new
name|IntWritable
argument_list|()
expr_stmt|;
name|value
operator|.
name|set
argument_list|(
name|emit
argument_list|)
expr_stmt|;
return|return
name|records
operator|++
operator|<
name|count
return|;
block|}
specifier|public
name|IntWritable
name|getCurrentKey
parameter_list|()
block|{
return|return
name|key
return|;
block|}
specifier|public
name|IntWritable
name|getCurrentValue
parameter_list|()
block|{
return|return
name|value
return|;
block|}
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{ }
specifier|public
name|float
name|getProgress
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|count
operator|==
literal|0
condition|?
literal|100
else|:
name|records
operator|/
operator|(
operator|(
name|float
operator|)
name|count
operator|)
return|;
block|}
block|}
return|;
block|}
block|}
DECL|class|SleepMapper
specifier|public
specifier|static
class|class
name|SleepMapper
extends|extends
name|Mapper
argument_list|<
name|IntWritable
argument_list|,
name|IntWritable
argument_list|,
name|IntWritable
argument_list|,
name|NullWritable
argument_list|>
block|{
DECL|field|mapSleepDuration
specifier|private
name|long
name|mapSleepDuration
init|=
literal|100
decl_stmt|;
DECL|field|mapSleepCount
specifier|private
name|int
name|mapSleepCount
init|=
literal|1
decl_stmt|;
DECL|field|count
specifier|private
name|int
name|count
init|=
literal|0
decl_stmt|;
DECL|method|setup (Context context)
specifier|protected
name|void
name|setup
parameter_list|(
name|Context
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|Configuration
name|conf
init|=
name|context
operator|.
name|getConfiguration
argument_list|()
decl_stmt|;
name|this
operator|.
name|mapSleepCount
operator|=
name|conf
operator|.
name|getInt
argument_list|(
name|MAP_SLEEP_COUNT
argument_list|,
name|mapSleepCount
argument_list|)
expr_stmt|;
name|this
operator|.
name|mapSleepDuration
operator|=
name|mapSleepCount
operator|==
literal|0
condition|?
literal|0
else|:
name|conf
operator|.
name|getLong
argument_list|(
name|MAP_SLEEP_TIME
argument_list|,
literal|100
argument_list|)
operator|/
name|mapSleepCount
expr_stmt|;
block|}
DECL|method|map (IntWritable key, IntWritable value, Context context )
specifier|public
name|void
name|map
parameter_list|(
name|IntWritable
name|key
parameter_list|,
name|IntWritable
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
comment|//it is expected that every map processes mapSleepCount number of records.
try|try
block|{
name|context
operator|.
name|setStatus
argument_list|(
literal|"Sleeping... ("
operator|+
operator|(
name|mapSleepDuration
operator|*
operator|(
name|mapSleepCount
operator|-
name|count
operator|)
operator|)
operator|+
literal|") ms left"
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|mapSleepDuration
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ex
parameter_list|)
block|{
throw|throw
operator|(
name|IOException
operator|)
operator|new
name|IOException
argument_list|(
literal|"Interrupted while sleeping"
argument_list|)
operator|.
name|initCause
argument_list|(
name|ex
argument_list|)
throw|;
block|}
operator|++
name|count
expr_stmt|;
comment|// output reduceSleepCount * numReduce number of random values, so that
comment|// each reducer will get reduceSleepCount number of keys.
name|int
name|k
init|=
name|key
operator|.
name|get
argument_list|()
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
name|value
operator|.
name|get
argument_list|()
condition|;
operator|++
name|i
control|)
block|{
name|context
operator|.
name|write
argument_list|(
operator|new
name|IntWritable
argument_list|(
name|k
operator|+
name|i
argument_list|)
argument_list|,
name|NullWritable
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|class|SleepReducer
specifier|public
specifier|static
class|class
name|SleepReducer
extends|extends
name|Reducer
argument_list|<
name|IntWritable
argument_list|,
name|NullWritable
argument_list|,
name|NullWritable
argument_list|,
name|NullWritable
argument_list|>
block|{
DECL|field|reduceSleepDuration
specifier|private
name|long
name|reduceSleepDuration
init|=
literal|100
decl_stmt|;
DECL|field|reduceSleepCount
specifier|private
name|int
name|reduceSleepCount
init|=
literal|1
decl_stmt|;
DECL|field|count
specifier|private
name|int
name|count
init|=
literal|0
decl_stmt|;
DECL|method|setup (Context context)
specifier|protected
name|void
name|setup
parameter_list|(
name|Context
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|Configuration
name|conf
init|=
name|context
operator|.
name|getConfiguration
argument_list|()
decl_stmt|;
name|this
operator|.
name|reduceSleepCount
operator|=
name|conf
operator|.
name|getInt
argument_list|(
name|REDUCE_SLEEP_COUNT
argument_list|,
name|reduceSleepCount
argument_list|)
expr_stmt|;
name|this
operator|.
name|reduceSleepDuration
operator|=
name|reduceSleepCount
operator|==
literal|0
condition|?
literal|0
else|:
name|conf
operator|.
name|getLong
argument_list|(
name|REDUCE_SLEEP_TIME
argument_list|,
literal|100
argument_list|)
operator|/
name|reduceSleepCount
expr_stmt|;
block|}
DECL|method|reduce (IntWritable key, Iterable<NullWritable> values, Context context)
specifier|public
name|void
name|reduce
parameter_list|(
name|IntWritable
name|key
parameter_list|,
name|Iterable
argument_list|<
name|NullWritable
argument_list|>
name|values
parameter_list|,
name|Context
name|context
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|context
operator|.
name|setStatus
argument_list|(
literal|"Sleeping... ("
operator|+
operator|(
name|reduceSleepDuration
operator|*
operator|(
name|reduceSleepCount
operator|-
name|count
operator|)
operator|)
operator|+
literal|") ms left"
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|reduceSleepDuration
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ex
parameter_list|)
block|{
throw|throw
operator|(
name|IOException
operator|)
operator|new
name|IOException
argument_list|(
literal|"Interrupted while sleeping"
argument_list|)
operator|.
name|initCause
argument_list|(
name|ex
argument_list|)
throw|;
block|}
name|count
operator|++
expr_stmt|;
block|}
block|}
DECL|method|main (String[] args)
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
name|int
name|res
init|=
name|ToolRunner
operator|.
name|run
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|,
operator|new
name|SleepJob
argument_list|()
argument_list|,
name|args
argument_list|)
decl_stmt|;
name|System
operator|.
name|exit
argument_list|(
name|res
argument_list|)
expr_stmt|;
block|}
DECL|method|createJob (int numMapper, int numReducer, long mapSleepTime, int mapSleepCount, long reduceSleepTime, int reduceSleepCount)
specifier|public
name|Job
name|createJob
parameter_list|(
name|int
name|numMapper
parameter_list|,
name|int
name|numReducer
parameter_list|,
name|long
name|mapSleepTime
parameter_list|,
name|int
name|mapSleepCount
parameter_list|,
name|long
name|reduceSleepTime
parameter_list|,
name|int
name|reduceSleepCount
parameter_list|)
throws|throws
name|IOException
block|{
name|Configuration
name|conf
init|=
name|getConf
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|MAP_SLEEP_TIME
argument_list|,
name|mapSleepTime
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|REDUCE_SLEEP_TIME
argument_list|,
name|reduceSleepTime
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|MAP_SLEEP_COUNT
argument_list|,
name|mapSleepCount
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|REDUCE_SLEEP_COUNT
argument_list|,
name|reduceSleepCount
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|MRJobConfig
operator|.
name|NUM_MAPS
argument_list|,
name|numMapper
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
argument_list|,
literal|"sleep"
argument_list|)
decl_stmt|;
name|job
operator|.
name|setNumReduceTasks
argument_list|(
name|numReducer
argument_list|)
expr_stmt|;
name|job
operator|.
name|setJarByClass
argument_list|(
name|SleepJob
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setMapperClass
argument_list|(
name|SleepMapper
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setMapOutputKeyClass
argument_list|(
name|IntWritable
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setMapOutputValueClass
argument_list|(
name|NullWritable
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setReducerClass
argument_list|(
name|SleepReducer
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setOutputFormatClass
argument_list|(
name|NullOutputFormat
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setInputFormatClass
argument_list|(
name|SleepInputFormat
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setPartitionerClass
argument_list|(
name|SleepJobPartitioner
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setSpeculativeExecution
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|job
operator|.
name|setJobName
argument_list|(
literal|"Sleep job"
argument_list|)
expr_stmt|;
name|FileInputFormat
operator|.
name|addInputPath
argument_list|(
name|job
argument_list|,
operator|new
name|Path
argument_list|(
literal|"ignored"
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|job
return|;
block|}
DECL|method|run (String[] args)
specifier|public
name|int
name|run
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|args
operator|.
name|length
operator|<
literal|1
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"SleepJob [-m numMapper] [-r numReducer]"
operator|+
literal|" [-mt mapSleepTime (msec)] [-rt reduceSleepTime (msec)]"
operator|+
literal|" [-recordt recordSleepTime (msec)]"
argument_list|)
expr_stmt|;
name|ToolRunner
operator|.
name|printGenericCommandUsage
argument_list|(
name|System
operator|.
name|err
argument_list|)
expr_stmt|;
return|return
literal|2
return|;
block|}
name|int
name|numMapper
init|=
literal|1
decl_stmt|,
name|numReducer
init|=
literal|1
decl_stmt|;
name|long
name|mapSleepTime
init|=
literal|100
decl_stmt|,
name|reduceSleepTime
init|=
literal|100
decl_stmt|,
name|recSleepTime
init|=
literal|100
decl_stmt|;
name|int
name|mapSleepCount
init|=
literal|1
decl_stmt|,
name|reduceSleepCount
init|=
literal|1
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
name|args
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|args
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
literal|"-m"
argument_list|)
condition|)
block|{
name|numMapper
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|args
index|[
operator|++
name|i
index|]
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|args
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
literal|"-r"
argument_list|)
condition|)
block|{
name|numReducer
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|args
index|[
operator|++
name|i
index|]
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|args
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
literal|"-mt"
argument_list|)
condition|)
block|{
name|mapSleepTime
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|args
index|[
operator|++
name|i
index|]
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|args
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
literal|"-rt"
argument_list|)
condition|)
block|{
name|reduceSleepTime
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|args
index|[
operator|++
name|i
index|]
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|args
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
literal|"-recordt"
argument_list|)
condition|)
block|{
name|recSleepTime
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|args
index|[
operator|++
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
comment|// sleep for *SleepTime duration in Task by recSleepTime per record
name|mapSleepCount
operator|=
operator|(
name|int
operator|)
name|Math
operator|.
name|ceil
argument_list|(
name|mapSleepTime
operator|/
operator|(
operator|(
name|double
operator|)
name|recSleepTime
operator|)
argument_list|)
expr_stmt|;
name|reduceSleepCount
operator|=
operator|(
name|int
operator|)
name|Math
operator|.
name|ceil
argument_list|(
name|reduceSleepTime
operator|/
operator|(
operator|(
name|double
operator|)
name|recSleepTime
operator|)
argument_list|)
expr_stmt|;
name|Job
name|job
init|=
name|createJob
argument_list|(
name|numMapper
argument_list|,
name|numReducer
argument_list|,
name|mapSleepTime
argument_list|,
name|mapSleepCount
argument_list|,
name|reduceSleepTime
argument_list|,
name|reduceSleepCount
argument_list|)
decl_stmt|;
return|return
name|job
operator|.
name|waitForCompletion
argument_list|(
literal|true
argument_list|)
condition|?
literal|0
else|:
literal|1
return|;
block|}
block|}
end_class

end_unit

