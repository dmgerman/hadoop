begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.slive
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|slive
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
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
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
name|slive
operator|.
name|OperationOutput
operator|.
name|OutputType
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
name|MapReduceBase
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
name|mapred
operator|.
name|OutputCollector
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
name|Reporter
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
name|util
operator|.
name|StringUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * The slive class which sets up the mapper to be used which itself will receive  * a single dummy key and value and then in a loop run the various operations  * that have been selected and upon operation completion output the collected  * output from that operation (and repeat until finished).  */
end_comment

begin_class
DECL|class|SliveMapper
specifier|public
class|class
name|SliveMapper
extends|extends
name|MapReduceBase
implements|implements
name|Mapper
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|,
name|Text
argument_list|,
name|Text
argument_list|>
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SliveMapper
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|OP_TYPE
specifier|private
specifier|static
specifier|final
name|String
name|OP_TYPE
init|=
name|SliveMapper
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
decl_stmt|;
DECL|field|filesystem
specifier|private
name|FileSystem
name|filesystem
decl_stmt|;
DECL|field|config
specifier|private
name|ConfigExtractor
name|config
decl_stmt|;
DECL|field|taskId
specifier|private
name|int
name|taskId
decl_stmt|;
comment|/*    * (non-Javadoc)    *     * @see    * org.apache.hadoop.mapred.MapReduceBase#configure(org.apache.hadoop.mapred    * .JobConf)    */
annotation|@
name|Override
comment|// MapReduceBase
DECL|method|configure (JobConf conf)
specifier|public
name|void
name|configure
parameter_list|(
name|JobConf
name|conf
parameter_list|)
block|{
try|try
block|{
name|config
operator|=
operator|new
name|ConfigExtractor
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|ConfigExtractor
operator|.
name|dumpOptions
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|filesystem
operator|=
name|config
operator|.
name|getBaseDirectory
argument_list|()
operator|.
name|getFileSystem
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unable to setup slive "
operator|+
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Unable to setup slive configuration"
argument_list|,
name|e
argument_list|)
throw|;
block|}
if|if
condition|(
name|conf
operator|.
name|get
argument_list|(
name|MRJobConfig
operator|.
name|TASK_ATTEMPT_ID
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|taskId
operator|=
name|TaskAttemptID
operator|.
name|forName
argument_list|(
name|conf
operator|.
name|get
argument_list|(
name|MRJobConfig
operator|.
name|TASK_ATTEMPT_ID
argument_list|)
argument_list|)
operator|.
name|getTaskID
argument_list|()
operator|.
name|getId
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// So that branch-1/0.20 can run this same code as well
name|this
operator|.
name|taskId
operator|=
name|TaskAttemptID
operator|.
name|forName
argument_list|(
name|conf
operator|.
name|get
argument_list|(
literal|"mapred.task.id"
argument_list|)
argument_list|)
operator|.
name|getTaskID
argument_list|()
operator|.
name|getId
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Fetches the config this object uses    *     * @return ConfigExtractor    */
DECL|method|getConfig ()
specifier|private
name|ConfigExtractor
name|getConfig
parameter_list|()
block|{
return|return
name|config
return|;
block|}
comment|/**    * Logs to the given reporter and logs to the internal logger at info level    *     * @param r    *          the reporter to set status on    * @param msg    *          the message to log    */
DECL|method|logAndSetStatus (Reporter r, String msg)
specifier|private
name|void
name|logAndSetStatus
parameter_list|(
name|Reporter
name|r
parameter_list|,
name|String
name|msg
parameter_list|)
block|{
name|r
operator|.
name|setStatus
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
comment|/**    * Runs the given operation and reports on its results    *     * @param op    *          the operation to run    * @param reporter    *          the status reporter to notify    * @param output    *          the output to write to    * @throws IOException    */
DECL|method|runOperation (Operation op, Reporter reporter, OutputCollector<Text, Text> output, long opNum)
specifier|private
name|void
name|runOperation
parameter_list|(
name|Operation
name|op
parameter_list|,
name|Reporter
name|reporter
parameter_list|,
name|OutputCollector
argument_list|<
name|Text
argument_list|,
name|Text
argument_list|>
name|output
parameter_list|,
name|long
name|opNum
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|op
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|logAndSetStatus
argument_list|(
name|reporter
argument_list|,
literal|"Running operation #"
operator|+
name|opNum
operator|+
literal|" ("
operator|+
name|op
operator|+
literal|")"
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|OperationOutput
argument_list|>
name|opOut
init|=
name|op
operator|.
name|run
argument_list|(
name|filesystem
argument_list|)
decl_stmt|;
name|logAndSetStatus
argument_list|(
name|reporter
argument_list|,
literal|"Finished operation #"
operator|+
name|opNum
operator|+
literal|" ("
operator|+
name|op
operator|+
literal|")"
argument_list|)
expr_stmt|;
if|if
condition|(
name|opOut
operator|!=
literal|null
operator|&&
operator|!
name|opOut
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
for|for
control|(
name|OperationOutput
name|outData
range|:
name|opOut
control|)
block|{
name|output
operator|.
name|collect
argument_list|(
name|outData
operator|.
name|getKey
argument_list|()
argument_list|,
name|outData
operator|.
name|getOutputValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/*    * (non-Javadoc)    *     * @see org.apache.hadoop.mapred.Mapper#map(java.lang.Object,    * java.lang.Object, org.apache.hadoop.mapred.OutputCollector,    * org.apache.hadoop.mapred.Reporter)    */
annotation|@
name|Override
comment|// Mapper
DECL|method|map (Object key, Object value, OutputCollector<Text, Text> output, Reporter reporter)
specifier|public
name|void
name|map
parameter_list|(
name|Object
name|key
parameter_list|,
name|Object
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
name|logAndSetStatus
argument_list|(
name|reporter
argument_list|,
literal|"Running slive mapper for dummy key "
operator|+
name|key
operator|+
literal|" and dummy value "
operator|+
name|value
argument_list|)
expr_stmt|;
comment|//Add taskID to randomSeed to deterministically seed rnd.
name|Random
name|rnd
init|=
name|config
operator|.
name|getRandomSeed
argument_list|()
operator|!=
literal|null
condition|?
operator|new
name|Random
argument_list|(
name|this
operator|.
name|taskId
operator|+
name|config
operator|.
name|getRandomSeed
argument_list|()
argument_list|)
else|:
operator|new
name|Random
argument_list|()
decl_stmt|;
name|WeightSelector
name|selector
init|=
operator|new
name|WeightSelector
argument_list|(
name|config
argument_list|,
name|rnd
argument_list|)
decl_stmt|;
name|long
name|startTime
init|=
name|Timer
operator|.
name|now
argument_list|()
decl_stmt|;
name|long
name|opAm
init|=
literal|0
decl_stmt|;
name|long
name|sleepOps
init|=
literal|0
decl_stmt|;
name|int
name|duration
init|=
name|getConfig
argument_list|()
operator|.
name|getDurationMilliseconds
argument_list|()
decl_stmt|;
name|Range
argument_list|<
name|Long
argument_list|>
name|sleepRange
init|=
name|getConfig
argument_list|()
operator|.
name|getSleepRange
argument_list|()
decl_stmt|;
name|Operation
name|sleeper
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|sleepRange
operator|!=
literal|null
condition|)
block|{
name|sleeper
operator|=
operator|new
name|SleepOp
argument_list|(
name|getConfig
argument_list|()
argument_list|,
name|rnd
argument_list|)
expr_stmt|;
block|}
while|while
condition|(
name|Timer
operator|.
name|elapsed
argument_list|(
name|startTime
argument_list|)
operator|<
name|duration
condition|)
block|{
try|try
block|{
name|logAndSetStatus
argument_list|(
name|reporter
argument_list|,
literal|"Attempting to select operation #"
operator|+
operator|(
name|opAm
operator|+
literal|1
operator|)
argument_list|)
expr_stmt|;
name|int
name|currElapsed
init|=
call|(
name|int
call|)
argument_list|(
name|Timer
operator|.
name|elapsed
argument_list|(
name|startTime
argument_list|)
argument_list|)
decl_stmt|;
name|Operation
name|op
init|=
name|selector
operator|.
name|select
argument_list|(
name|currElapsed
argument_list|,
name|duration
argument_list|)
decl_stmt|;
if|if
condition|(
name|op
operator|==
literal|null
condition|)
block|{
comment|// no ops left
break|break;
block|}
else|else
block|{
comment|// got a good op
operator|++
name|opAm
expr_stmt|;
name|runOperation
argument_list|(
name|op
argument_list|,
name|reporter
argument_list|,
name|output
argument_list|,
name|opAm
argument_list|)
expr_stmt|;
block|}
comment|// do a sleep??
if|if
condition|(
name|sleeper
operator|!=
literal|null
condition|)
block|{
comment|// these don't count against the number of operations
operator|++
name|sleepOps
expr_stmt|;
name|runOperation
argument_list|(
name|sleeper
argument_list|,
name|reporter
argument_list|,
name|output
argument_list|,
name|sleepOps
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|logAndSetStatus
argument_list|(
name|reporter
argument_list|,
literal|"Failed at running due to "
operator|+
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|getConfig
argument_list|()
operator|.
name|shouldExitOnFirstError
argument_list|()
condition|)
block|{
break|break;
block|}
block|}
block|}
comment|// write out any accumulated mapper stats
block|{
name|long
name|timeTaken
init|=
name|Timer
operator|.
name|elapsed
argument_list|(
name|startTime
argument_list|)
decl_stmt|;
name|OperationOutput
name|opCount
init|=
operator|new
name|OperationOutput
argument_list|(
name|OutputType
operator|.
name|LONG
argument_list|,
name|OP_TYPE
argument_list|,
name|ReportWriter
operator|.
name|OP_COUNT
argument_list|,
name|opAm
argument_list|)
decl_stmt|;
name|output
operator|.
name|collect
argument_list|(
name|opCount
operator|.
name|getKey
argument_list|()
argument_list|,
name|opCount
operator|.
name|getOutputValue
argument_list|()
argument_list|)
expr_stmt|;
name|OperationOutput
name|overallTime
init|=
operator|new
name|OperationOutput
argument_list|(
name|OutputType
operator|.
name|LONG
argument_list|,
name|OP_TYPE
argument_list|,
name|ReportWriter
operator|.
name|OK_TIME_TAKEN
argument_list|,
name|timeTaken
argument_list|)
decl_stmt|;
name|output
operator|.
name|collect
argument_list|(
name|overallTime
operator|.
name|getKey
argument_list|()
argument_list|,
name|overallTime
operator|.
name|getOutputValue
argument_list|()
argument_list|)
expr_stmt|;
name|logAndSetStatus
argument_list|(
name|reporter
argument_list|,
literal|"Finished "
operator|+
name|opAm
operator|+
literal|" operations in "
operator|+
name|timeTaken
operator|+
literal|" milliseconds"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

