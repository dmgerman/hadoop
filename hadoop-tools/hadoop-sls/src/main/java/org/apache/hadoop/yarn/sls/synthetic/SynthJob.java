begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.sls.synthetic
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|sls
operator|.
name|synthetic
package|;
end_package

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
name|commons
operator|.
name|math3
operator|.
name|distribution
operator|.
name|LogNormalDistribution
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
name|math3
operator|.
name|random
operator|.
name|JDKRandomGenerator
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
name|TaskStatus
operator|.
name|State
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
name|JobID
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
name|tools
operator|.
name|rumen
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
name|tools
operator|.
name|rumen
operator|.
name|Pre21JobHistoryConstants
operator|.
name|Values
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
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
operator|.
name|MILLISECONDS
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
operator|.
name|SECONDS
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|MRJobConfig
operator|.
name|QUEUE_NAME
import|;
end_import

begin_comment
comment|/**  * Generates random task data for a synthetic job.  */
end_comment

begin_class
DECL|class|SynthJob
specifier|public
class|class
name|SynthJob
implements|implements
name|JobStory
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"StaticVariableName"
argument_list|)
DECL|field|LOG
specifier|private
specifier|static
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|SynthJob
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|conf
specifier|private
specifier|final
name|Configuration
name|conf
decl_stmt|;
DECL|field|id
specifier|private
specifier|final
name|int
name|id
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"ConstantName"
argument_list|)
DECL|field|sequence
specifier|private
specifier|static
specifier|final
name|AtomicInteger
name|sequence
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|queueName
specifier|private
specifier|final
name|String
name|queueName
decl_stmt|;
DECL|field|jobClass
specifier|private
specifier|final
name|SynthJobClass
name|jobClass
decl_stmt|;
comment|// job timing
DECL|field|submitTime
specifier|private
specifier|final
name|long
name|submitTime
decl_stmt|;
DECL|field|duration
specifier|private
specifier|final
name|long
name|duration
decl_stmt|;
DECL|field|deadline
specifier|private
specifier|final
name|long
name|deadline
decl_stmt|;
DECL|field|numMapTasks
specifier|private
specifier|final
name|int
name|numMapTasks
decl_stmt|;
DECL|field|numRedTasks
specifier|private
specifier|final
name|int
name|numRedTasks
decl_stmt|;
DECL|field|mapMaxMemory
specifier|private
specifier|final
name|long
name|mapMaxMemory
decl_stmt|;
DECL|field|reduceMaxMemory
specifier|private
specifier|final
name|long
name|reduceMaxMemory
decl_stmt|;
DECL|field|mapMaxVcores
specifier|private
specifier|final
name|long
name|mapMaxVcores
decl_stmt|;
DECL|field|reduceMaxVcores
specifier|private
specifier|final
name|long
name|reduceMaxVcores
decl_stmt|;
DECL|field|mapRuntime
specifier|private
specifier|final
name|long
index|[]
name|mapRuntime
decl_stmt|;
DECL|field|reduceRuntime
specifier|private
specifier|final
name|float
index|[]
name|reduceRuntime
decl_stmt|;
DECL|field|totMapRuntime
specifier|private
name|long
name|totMapRuntime
decl_stmt|;
DECL|field|totRedRuntime
specifier|private
name|long
name|totRedRuntime
decl_stmt|;
DECL|method|SynthJob (JDKRandomGenerator rand, Configuration conf, SynthJobClass jobClass, long actualSubmissionTime)
specifier|public
name|SynthJob
parameter_list|(
name|JDKRandomGenerator
name|rand
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|SynthJobClass
name|jobClass
parameter_list|,
name|long
name|actualSubmissionTime
parameter_list|)
block|{
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
name|this
operator|.
name|jobClass
operator|=
name|jobClass
expr_stmt|;
name|this
operator|.
name|duration
operator|=
name|MILLISECONDS
operator|.
name|convert
argument_list|(
name|jobClass
operator|.
name|getDur
argument_list|()
argument_list|,
name|SECONDS
argument_list|)
expr_stmt|;
name|this
operator|.
name|numMapTasks
operator|=
name|jobClass
operator|.
name|getMtasks
argument_list|()
expr_stmt|;
name|this
operator|.
name|numRedTasks
operator|=
name|jobClass
operator|.
name|getRtasks
argument_list|()
expr_stmt|;
comment|// sample memory distributions, correct for sub-minAlloc sizes
name|long
name|tempMapMaxMemory
init|=
name|jobClass
operator|.
name|getMapMaxMemory
argument_list|()
decl_stmt|;
name|this
operator|.
name|mapMaxMemory
operator|=
name|tempMapMaxMemory
operator|<
name|MRJobConfig
operator|.
name|DEFAULT_MAP_MEMORY_MB
condition|?
name|MRJobConfig
operator|.
name|DEFAULT_MAP_MEMORY_MB
else|:
name|tempMapMaxMemory
expr_stmt|;
name|long
name|tempReduceMaxMemory
init|=
name|jobClass
operator|.
name|getReduceMaxMemory
argument_list|()
decl_stmt|;
name|this
operator|.
name|reduceMaxMemory
operator|=
name|tempReduceMaxMemory
operator|<
name|MRJobConfig
operator|.
name|DEFAULT_REDUCE_MEMORY_MB
condition|?
name|MRJobConfig
operator|.
name|DEFAULT_REDUCE_MEMORY_MB
else|:
name|tempReduceMaxMemory
expr_stmt|;
comment|// sample vcores distributions, correct for sub-minAlloc sizes
name|long
name|tempMapMaxVCores
init|=
name|jobClass
operator|.
name|getMapMaxVcores
argument_list|()
decl_stmt|;
name|this
operator|.
name|mapMaxVcores
operator|=
name|tempMapMaxVCores
operator|<
name|MRJobConfig
operator|.
name|DEFAULT_MAP_CPU_VCORES
condition|?
name|MRJobConfig
operator|.
name|DEFAULT_MAP_CPU_VCORES
else|:
name|tempMapMaxVCores
expr_stmt|;
name|long
name|tempReduceMaxVcores
init|=
name|jobClass
operator|.
name|getReduceMaxVcores
argument_list|()
decl_stmt|;
name|this
operator|.
name|reduceMaxVcores
operator|=
name|tempReduceMaxVcores
operator|<
name|MRJobConfig
operator|.
name|DEFAULT_REDUCE_CPU_VCORES
condition|?
name|MRJobConfig
operator|.
name|DEFAULT_REDUCE_CPU_VCORES
else|:
name|tempReduceMaxVcores
expr_stmt|;
if|if
condition|(
name|numMapTasks
operator|>
literal|0
condition|)
block|{
name|conf
operator|.
name|setLong
argument_list|(
name|MRJobConfig
operator|.
name|MAP_MEMORY_MB
argument_list|,
name|this
operator|.
name|mapMaxMemory
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|MRJobConfig
operator|.
name|MAP_JAVA_OPTS
argument_list|,
literal|"-Xmx"
operator|+
operator|(
name|this
operator|.
name|mapMaxMemory
operator|-
literal|100
operator|)
operator|+
literal|"m"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|numRedTasks
operator|>
literal|0
condition|)
block|{
name|conf
operator|.
name|setLong
argument_list|(
name|MRJobConfig
operator|.
name|REDUCE_MEMORY_MB
argument_list|,
name|this
operator|.
name|reduceMaxMemory
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|MRJobConfig
operator|.
name|REDUCE_JAVA_OPTS
argument_list|,
literal|"-Xmx"
operator|+
operator|(
name|this
operator|.
name|reduceMaxMemory
operator|-
literal|100
operator|)
operator|+
literal|"m"
argument_list|)
expr_stmt|;
block|}
name|boolean
name|hasDeadline
init|=
operator|(
name|rand
operator|.
name|nextDouble
argument_list|()
operator|<=
name|jobClass
operator|.
name|jobClass
operator|.
name|chance_of_reservation
operator|)
decl_stmt|;
name|LogNormalDistribution
name|deadlineFactor
init|=
name|SynthUtils
operator|.
name|getLogNormalDist
argument_list|(
name|rand
argument_list|,
name|jobClass
operator|.
name|jobClass
operator|.
name|deadline_factor_avg
argument_list|,
name|jobClass
operator|.
name|jobClass
operator|.
name|deadline_factor_stddev
argument_list|)
decl_stmt|;
name|double
name|deadlineFactorSample
init|=
operator|(
name|deadlineFactor
operator|!=
literal|null
operator|)
condition|?
name|deadlineFactor
operator|.
name|sample
argument_list|()
else|:
operator|-
literal|1
decl_stmt|;
name|this
operator|.
name|queueName
operator|=
name|jobClass
operator|.
name|workload
operator|.
name|getQueueName
argument_list|()
expr_stmt|;
name|this
operator|.
name|submitTime
operator|=
name|MILLISECONDS
operator|.
name|convert
argument_list|(
name|actualSubmissionTime
argument_list|,
name|SECONDS
argument_list|)
expr_stmt|;
name|this
operator|.
name|deadline
operator|=
name|hasDeadline
condition|?
name|MILLISECONDS
operator|.
name|convert
argument_list|(
name|actualSubmissionTime
argument_list|,
name|SECONDS
argument_list|)
operator|+
operator|(
name|long
operator|)
name|Math
operator|.
name|ceil
argument_list|(
name|deadlineFactorSample
operator|*
name|duration
argument_list|)
else|:
operator|-
literal|1
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|QUEUE_NAME
argument_list|,
name|queueName
argument_list|)
expr_stmt|;
comment|// name and initialize job randomness
specifier|final
name|long
name|seed
init|=
name|rand
operator|.
name|nextLong
argument_list|()
decl_stmt|;
name|rand
operator|.
name|setSeed
argument_list|(
name|seed
argument_list|)
expr_stmt|;
name|id
operator|=
name|sequence
operator|.
name|getAndIncrement
argument_list|()
expr_stmt|;
name|name
operator|=
name|String
operator|.
name|format
argument_list|(
name|jobClass
operator|.
name|getClassName
argument_list|()
operator|+
literal|"_%06d"
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
name|name
operator|+
literal|" ("
operator|+
name|seed
operator|+
literal|")"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"JOB TIMING`: job: "
operator|+
name|name
operator|+
literal|" submission:"
operator|+
name|submitTime
operator|+
literal|" deadline:"
operator|+
name|deadline
operator|+
literal|" duration:"
operator|+
name|duration
operator|+
literal|" deadline-submission: "
operator|+
operator|(
name|deadline
operator|-
name|submitTime
operator|)
argument_list|)
expr_stmt|;
comment|// generate map and reduce runtimes
name|mapRuntime
operator|=
operator|new
name|long
index|[
name|numMapTasks
index|]
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
name|numMapTasks
condition|;
name|i
operator|++
control|)
block|{
name|mapRuntime
index|[
name|i
index|]
operator|=
name|jobClass
operator|.
name|getMapTimeSample
argument_list|()
expr_stmt|;
name|totMapRuntime
operator|+=
name|mapRuntime
index|[
name|i
index|]
expr_stmt|;
block|}
name|reduceRuntime
operator|=
operator|new
name|float
index|[
name|numRedTasks
index|]
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
name|numRedTasks
condition|;
name|i
operator|++
control|)
block|{
name|reduceRuntime
index|[
name|i
index|]
operator|=
name|jobClass
operator|.
name|getReduceTimeSample
argument_list|()
expr_stmt|;
name|totRedRuntime
operator|+=
operator|(
name|long
operator|)
name|Math
operator|.
name|ceil
argument_list|(
name|reduceRuntime
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|hasDeadline ()
specifier|public
name|boolean
name|hasDeadline
parameter_list|()
block|{
return|return
name|deadline
operator|>
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
annotation|@
name|Override
DECL|method|getUser ()
specifier|public
name|String
name|getUser
parameter_list|()
block|{
return|return
name|jobClass
operator|.
name|getUserName
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getJobID ()
specifier|public
name|JobID
name|getJobID
parameter_list|()
block|{
return|return
operator|new
name|JobID
argument_list|(
literal|"job_mock_"
operator|+
name|name
argument_list|,
name|id
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getOutcome ()
specifier|public
name|Values
name|getOutcome
parameter_list|()
block|{
return|return
name|Values
operator|.
name|SUCCESS
return|;
block|}
annotation|@
name|Override
DECL|method|getSubmissionTime ()
specifier|public
name|long
name|getSubmissionTime
parameter_list|()
block|{
return|return
name|submitTime
return|;
block|}
annotation|@
name|Override
DECL|method|getNumberMaps ()
specifier|public
name|int
name|getNumberMaps
parameter_list|()
block|{
return|return
name|numMapTasks
return|;
block|}
annotation|@
name|Override
DECL|method|getNumberReduces ()
specifier|public
name|int
name|getNumberReduces
parameter_list|()
block|{
return|return
name|numRedTasks
return|;
block|}
annotation|@
name|Override
DECL|method|getTaskInfo (TaskType taskType, int taskNumber)
specifier|public
name|TaskInfo
name|getTaskInfo
parameter_list|(
name|TaskType
name|taskType
parameter_list|,
name|int
name|taskNumber
parameter_list|)
block|{
switch|switch
condition|(
name|taskType
condition|)
block|{
case|case
name|MAP
case|:
return|return
operator|new
name|TaskInfo
argument_list|(
operator|-
literal|1
argument_list|,
operator|-
literal|1
argument_list|,
operator|-
literal|1
argument_list|,
operator|-
literal|1
argument_list|,
name|mapMaxMemory
argument_list|,
name|mapMaxVcores
argument_list|)
return|;
case|case
name|REDUCE
case|:
return|return
operator|new
name|TaskInfo
argument_list|(
operator|-
literal|1
argument_list|,
operator|-
literal|1
argument_list|,
operator|-
literal|1
argument_list|,
operator|-
literal|1
argument_list|,
name|reduceMaxMemory
argument_list|,
name|reduceMaxVcores
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Not interested"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getInputSplits ()
specifier|public
name|InputSplit
index|[]
name|getInputSplits
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|getTaskAttemptInfo (TaskType taskType, int taskNumber, int taskAttemptNumber)
specifier|public
name|TaskAttemptInfo
name|getTaskAttemptInfo
parameter_list|(
name|TaskType
name|taskType
parameter_list|,
name|int
name|taskNumber
parameter_list|,
name|int
name|taskAttemptNumber
parameter_list|)
block|{
switch|switch
condition|(
name|taskType
condition|)
block|{
case|case
name|MAP
case|:
return|return
operator|new
name|MapTaskAttemptInfo
argument_list|(
name|State
operator|.
name|SUCCEEDED
argument_list|,
name|getTaskInfo
argument_list|(
name|taskType
argument_list|,
name|taskNumber
argument_list|)
argument_list|,
name|mapRuntime
index|[
name|taskNumber
index|]
argument_list|,
literal|null
argument_list|)
return|;
case|case
name|REDUCE
case|:
comment|// We assume uniform split between pull/sort/reduce
comment|// aligned with naive progress reporting assumptions
return|return
operator|new
name|ReduceTaskAttemptInfo
argument_list|(
name|State
operator|.
name|SUCCEEDED
argument_list|,
name|getTaskInfo
argument_list|(
name|taskType
argument_list|,
name|taskNumber
argument_list|)
argument_list|,
operator|(
name|long
operator|)
name|Math
operator|.
name|round
argument_list|(
operator|(
name|reduceRuntime
index|[
name|taskNumber
index|]
operator|/
literal|3
operator|)
argument_list|)
argument_list|,
operator|(
name|long
operator|)
name|Math
operator|.
name|round
argument_list|(
operator|(
name|reduceRuntime
index|[
name|taskNumber
index|]
operator|/
literal|3
operator|)
argument_list|)
argument_list|,
operator|(
name|long
operator|)
name|Math
operator|.
name|round
argument_list|(
operator|(
name|reduceRuntime
index|[
name|taskNumber
index|]
operator|/
literal|3
operator|)
argument_list|)
argument_list|,
literal|null
argument_list|)
return|;
default|default:
break|break;
block|}
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|getMapTaskAttemptInfoAdjusted (int taskNumber, int taskAttemptNumber, int locality)
specifier|public
name|TaskAttemptInfo
name|getMapTaskAttemptInfoAdjusted
parameter_list|(
name|int
name|taskNumber
parameter_list|,
name|int
name|taskAttemptNumber
parameter_list|,
name|int
name|locality
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|getJobConf ()
specifier|public
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|JobConf
name|getJobConf
parameter_list|()
block|{
return|return
operator|new
name|JobConf
argument_list|(
name|conf
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getQueueName ()
specifier|public
name|String
name|getQueueName
parameter_list|()
block|{
return|return
name|queueName
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"SynthJob [\n"
operator|+
literal|"  workload="
operator|+
name|jobClass
operator|.
name|getWorkload
argument_list|()
operator|.
name|getId
argument_list|()
operator|+
literal|"\n"
operator|+
literal|"  jobClass="
operator|+
name|jobClass
operator|.
name|getWorkload
argument_list|()
operator|.
name|getClassList
argument_list|()
operator|.
name|indexOf
argument_list|(
name|jobClass
argument_list|)
operator|+
literal|"\n"
operator|+
literal|"  conf="
operator|+
name|conf
operator|+
literal|",\n"
operator|+
literal|"  id="
operator|+
name|id
operator|+
literal|",\n"
operator|+
literal|"  name="
operator|+
name|name
operator|+
literal|",\n"
operator|+
literal|"  mapRuntime="
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|mapRuntime
argument_list|)
operator|+
literal|",\n"
operator|+
literal|"  reduceRuntime="
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|reduceRuntime
argument_list|)
operator|+
literal|",\n"
operator|+
literal|"  submitTime="
operator|+
name|submitTime
operator|+
literal|",\n"
operator|+
literal|"  numMapTasks="
operator|+
name|numMapTasks
operator|+
literal|",\n"
operator|+
literal|"  numRedTasks="
operator|+
name|numRedTasks
operator|+
literal|",\n"
operator|+
literal|"  mapMaxMemory="
operator|+
name|mapMaxMemory
operator|+
literal|",\n"
operator|+
literal|"  reduceMaxMemory="
operator|+
name|reduceMaxMemory
operator|+
literal|",\n"
operator|+
literal|"  queueName="
operator|+
name|queueName
operator|+
literal|"\n"
operator|+
literal|"]"
return|;
block|}
DECL|method|getJobClass ()
specifier|public
name|SynthJobClass
name|getJobClass
parameter_list|()
block|{
return|return
name|jobClass
return|;
block|}
DECL|method|getTotalSlotTime ()
specifier|public
name|long
name|getTotalSlotTime
parameter_list|()
block|{
return|return
name|totMapRuntime
operator|+
name|totRedRuntime
return|;
block|}
DECL|method|getDuration ()
specifier|public
name|long
name|getDuration
parameter_list|()
block|{
return|return
name|duration
return|;
block|}
DECL|method|getDeadline ()
specifier|public
name|long
name|getDeadline
parameter_list|()
block|{
return|return
name|deadline
return|;
block|}
annotation|@
name|Override
DECL|method|equals (Object other)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|other
operator|instanceof
name|SynthJob
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|SynthJob
name|o
init|=
operator|(
name|SynthJob
operator|)
name|other
decl_stmt|;
return|return
name|Arrays
operator|.
name|equals
argument_list|(
name|mapRuntime
argument_list|,
name|o
operator|.
name|mapRuntime
argument_list|)
operator|&&
name|Arrays
operator|.
name|equals
argument_list|(
name|reduceRuntime
argument_list|,
name|o
operator|.
name|reduceRuntime
argument_list|)
operator|&&
name|submitTime
operator|==
name|o
operator|.
name|submitTime
operator|&&
name|numMapTasks
operator|==
name|o
operator|.
name|numMapTasks
operator|&&
name|numRedTasks
operator|==
name|o
operator|.
name|numRedTasks
operator|&&
name|mapMaxMemory
operator|==
name|o
operator|.
name|mapMaxMemory
operator|&&
name|reduceMaxMemory
operator|==
name|o
operator|.
name|reduceMaxMemory
operator|&&
name|mapMaxVcores
operator|==
name|o
operator|.
name|mapMaxVcores
operator|&&
name|reduceMaxVcores
operator|==
name|o
operator|.
name|reduceMaxVcores
operator|&&
name|queueName
operator|.
name|equals
argument_list|(
name|o
operator|.
name|queueName
argument_list|)
operator|&&
name|jobClass
operator|.
name|equals
argument_list|(
name|o
operator|.
name|jobClass
argument_list|)
operator|&&
name|totMapRuntime
operator|==
name|o
operator|.
name|totMapRuntime
operator|&&
name|totRedRuntime
operator|==
name|o
operator|.
name|totRedRuntime
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
comment|// could have a bad distr; investigate if a relevant use case exists
return|return
name|jobClass
operator|.
name|hashCode
argument_list|()
operator|*
operator|(
name|int
operator|)
name|submitTime
return|;
block|}
block|}
end_class

end_unit

