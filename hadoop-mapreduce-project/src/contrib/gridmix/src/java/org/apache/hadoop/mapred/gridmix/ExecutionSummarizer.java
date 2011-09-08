begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred.gridmix
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|gridmix
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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang
operator|.
name|time
operator|.
name|FastDateFormat
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
name|FileStatus
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
name|io
operator|.
name|MD5Hash
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
name|gridmix
operator|.
name|GenerateData
operator|.
name|DataStatistics
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
name|gridmix
operator|.
name|Statistics
operator|.
name|JobStats
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
name|util
operator|.
name|StringUtils
import|;
end_import

begin_comment
comment|/**  * Summarizes a {@link Gridmix} run. Statistics that are reported are  *<ul>  *<li>Total number of jobs in the input trace</li>  *<li>Trace signature</li>  *<li>Total number of jobs processed from the input trace</li>  *<li>Total number of jobs submitted</li>  *<li>Total number of successful and failed jobs</li>  *<li>Total number of map/reduce tasks launched</li>  *<li>Gridmix start& end time</li>  *<li>Total time for the Gridmix run (data-generation and simulation)</li>  *<li>Gridmix Configuration (i.e job-type, submission-type, resolver)</li>  *</ul>  */
end_comment

begin_class
DECL|class|ExecutionSummarizer
class|class
name|ExecutionSummarizer
implements|implements
name|StatListener
argument_list|<
name|JobStats
argument_list|>
block|{
DECL|field|LOG
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|ExecutionSummarizer
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|UTIL
specifier|private
specifier|static
specifier|final
name|FastDateFormat
name|UTIL
init|=
name|FastDateFormat
operator|.
name|getInstance
argument_list|()
decl_stmt|;
DECL|field|numJobsInInputTrace
specifier|private
name|int
name|numJobsInInputTrace
decl_stmt|;
DECL|field|totalSuccessfulJobs
specifier|private
name|int
name|totalSuccessfulJobs
decl_stmt|;
DECL|field|totalFailedJobs
specifier|private
name|int
name|totalFailedJobs
decl_stmt|;
DECL|field|totalMapTasksLaunched
specifier|private
name|int
name|totalMapTasksLaunched
decl_stmt|;
DECL|field|totalReduceTasksLaunched
specifier|private
name|int
name|totalReduceTasksLaunched
decl_stmt|;
DECL|field|totalSimulationTime
specifier|private
name|long
name|totalSimulationTime
decl_stmt|;
DECL|field|totalRuntime
specifier|private
name|long
name|totalRuntime
decl_stmt|;
DECL|field|commandLineArgs
specifier|private
specifier|final
name|String
name|commandLineArgs
decl_stmt|;
DECL|field|startTime
specifier|private
name|long
name|startTime
decl_stmt|;
DECL|field|endTime
specifier|private
name|long
name|endTime
decl_stmt|;
DECL|field|simulationStartTime
specifier|private
name|long
name|simulationStartTime
decl_stmt|;
DECL|field|inputTraceLocation
specifier|private
name|String
name|inputTraceLocation
decl_stmt|;
DECL|field|inputTraceSignature
specifier|private
name|String
name|inputTraceSignature
decl_stmt|;
DECL|field|jobSubmissionPolicy
specifier|private
name|String
name|jobSubmissionPolicy
decl_stmt|;
DECL|field|resolver
specifier|private
name|String
name|resolver
decl_stmt|;
DECL|field|dataStats
specifier|private
name|DataStatistics
name|dataStats
decl_stmt|;
DECL|field|expectedDataSize
specifier|private
name|String
name|expectedDataSize
decl_stmt|;
comment|/**    * Basic constructor initialized with the runtime arguments.     */
DECL|method|ExecutionSummarizer (String[] args)
name|ExecutionSummarizer
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|startTime
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
comment|// flatten the args string and store it
name|commandLineArgs
operator|=
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang
operator|.
name|StringUtils
operator|.
name|join
argument_list|(
name|args
argument_list|,
literal|' '
argument_list|)
expr_stmt|;
block|}
comment|/**    * Default constructor.     */
DECL|method|ExecutionSummarizer ()
name|ExecutionSummarizer
parameter_list|()
block|{
name|startTime
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
name|commandLineArgs
operator|=
name|Summarizer
operator|.
name|NA
expr_stmt|;
block|}
DECL|method|start (Configuration conf)
name|void
name|start
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|simulationStartTime
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
block|}
DECL|method|processJobState (JobStats stats)
specifier|private
name|void
name|processJobState
parameter_list|(
name|JobStats
name|stats
parameter_list|)
throws|throws
name|Exception
block|{
name|Job
name|job
init|=
name|stats
operator|.
name|getJob
argument_list|()
decl_stmt|;
if|if
condition|(
name|job
operator|.
name|isSuccessful
argument_list|()
condition|)
block|{
operator|++
name|totalSuccessfulJobs
expr_stmt|;
block|}
else|else
block|{
operator|++
name|totalFailedJobs
expr_stmt|;
block|}
block|}
DECL|method|processJobTasks (JobStats stats)
specifier|private
name|void
name|processJobTasks
parameter_list|(
name|JobStats
name|stats
parameter_list|)
throws|throws
name|Exception
block|{
name|totalMapTasksLaunched
operator|+=
name|stats
operator|.
name|getNoOfMaps
argument_list|()
expr_stmt|;
name|Job
name|job
init|=
name|stats
operator|.
name|getJob
argument_list|()
decl_stmt|;
name|totalReduceTasksLaunched
operator|+=
name|job
operator|.
name|getNumReduceTasks
argument_list|()
expr_stmt|;
block|}
DECL|method|process (JobStats stats)
specifier|private
name|void
name|process
parameter_list|(
name|JobStats
name|stats
parameter_list|)
block|{
try|try
block|{
comment|// process the job run state
name|processJobState
argument_list|(
name|stats
argument_list|)
expr_stmt|;
comment|// process the tasks information
name|processJobTasks
argument_list|(
name|stats
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
name|info
argument_list|(
literal|"Error in processing job "
operator|+
name|stats
operator|.
name|getJob
argument_list|()
operator|.
name|getJobID
argument_list|()
operator|+
literal|"."
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|update (JobStats item)
specifier|public
name|void
name|update
parameter_list|(
name|JobStats
name|item
parameter_list|)
block|{
comment|// process only if the simulation has started
if|if
condition|(
name|simulationStartTime
operator|>
literal|0
condition|)
block|{
name|process
argument_list|(
name|item
argument_list|)
expr_stmt|;
name|totalSimulationTime
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|getSimulationStartTime
argument_list|()
expr_stmt|;
block|}
block|}
comment|// Generates a signature for the trace file based on
comment|//   - filename
comment|//   - modification time
comment|//   - file length
comment|//   - owner
DECL|method|getTraceSignature (String input)
specifier|protected
specifier|static
name|String
name|getTraceSignature
parameter_list|(
name|String
name|input
parameter_list|)
throws|throws
name|IOException
block|{
name|Path
name|inputPath
init|=
operator|new
name|Path
argument_list|(
name|input
argument_list|)
decl_stmt|;
name|FileSystem
name|fs
init|=
name|inputPath
operator|.
name|getFileSystem
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
decl_stmt|;
name|FileStatus
name|status
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|inputPath
argument_list|)
decl_stmt|;
name|Path
name|qPath
init|=
name|fs
operator|.
name|makeQualified
argument_list|(
name|status
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|traceID
init|=
name|status
operator|.
name|getModificationTime
argument_list|()
operator|+
name|qPath
operator|.
name|toString
argument_list|()
operator|+
name|status
operator|.
name|getOwner
argument_list|()
operator|+
name|status
operator|.
name|getLen
argument_list|()
decl_stmt|;
return|return
name|MD5Hash
operator|.
name|digest
argument_list|(
name|traceID
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|finalize (JobFactory factory, String inputPath, long dataSize, UserResolver userResolver, DataStatistics stats, Configuration conf)
name|void
name|finalize
parameter_list|(
name|JobFactory
name|factory
parameter_list|,
name|String
name|inputPath
parameter_list|,
name|long
name|dataSize
parameter_list|,
name|UserResolver
name|userResolver
parameter_list|,
name|DataStatistics
name|stats
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|numJobsInInputTrace
operator|=
name|factory
operator|.
name|numJobsInTrace
expr_stmt|;
name|endTime
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
if|if
condition|(
literal|"-"
operator|.
name|equals
argument_list|(
name|inputPath
argument_list|)
condition|)
block|{
name|inputTraceLocation
operator|=
name|Summarizer
operator|.
name|NA
expr_stmt|;
name|inputTraceSignature
operator|=
name|Summarizer
operator|.
name|NA
expr_stmt|;
block|}
else|else
block|{
name|Path
name|inputTracePath
init|=
operator|new
name|Path
argument_list|(
name|inputPath
argument_list|)
decl_stmt|;
name|FileSystem
name|fs
init|=
name|inputTracePath
operator|.
name|getFileSystem
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|inputTraceLocation
operator|=
name|fs
operator|.
name|makeQualified
argument_list|(
name|inputTracePath
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
name|inputTraceSignature
operator|=
name|getTraceSignature
argument_list|(
name|inputPath
argument_list|)
expr_stmt|;
block|}
name|jobSubmissionPolicy
operator|=
name|Gridmix
operator|.
name|getJobSubmissionPolicy
argument_list|(
name|conf
argument_list|)
operator|.
name|name
argument_list|()
expr_stmt|;
name|resolver
operator|=
name|userResolver
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
expr_stmt|;
if|if
condition|(
name|dataSize
operator|>
literal|0
condition|)
block|{
name|expectedDataSize
operator|=
name|StringUtils
operator|.
name|humanReadableInt
argument_list|(
name|dataSize
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|expectedDataSize
operator|=
name|Summarizer
operator|.
name|NA
expr_stmt|;
block|}
name|dataStats
operator|=
name|stats
expr_stmt|;
name|totalRuntime
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|getStartTime
argument_list|()
expr_stmt|;
block|}
comment|/**    * Summarizes the current {@link Gridmix} run.    */
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"Execution Summary:-"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"\nInput trace: "
argument_list|)
operator|.
name|append
argument_list|(
name|getInputTraceLocation
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"\nInput trace signature: "
argument_list|)
operator|.
name|append
argument_list|(
name|getInputTraceSignature
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"\nTotal number of jobs in trace: "
argument_list|)
operator|.
name|append
argument_list|(
name|getNumJobsInTrace
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"\nExpected input data size: "
argument_list|)
operator|.
name|append
argument_list|(
name|getExpectedDataSize
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"\nInput data statistics: "
argument_list|)
operator|.
name|append
argument_list|(
name|getInputDataStatistics
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"\nTotal number of jobs processed: "
argument_list|)
operator|.
name|append
argument_list|(
name|getNumSubmittedJobs
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"\nTotal number of successful jobs: "
argument_list|)
operator|.
name|append
argument_list|(
name|getNumSuccessfulJobs
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"\nTotal number of failed jobs: "
argument_list|)
operator|.
name|append
argument_list|(
name|getNumFailedJobs
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"\nTotal number of map tasks launched: "
argument_list|)
operator|.
name|append
argument_list|(
name|getNumMapTasksLaunched
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"\nTotal number of reduce task launched: "
argument_list|)
operator|.
name|append
argument_list|(
name|getNumReduceTasksLaunched
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"\nGridmix start time: "
argument_list|)
operator|.
name|append
argument_list|(
name|UTIL
operator|.
name|format
argument_list|(
name|getStartTime
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"\nGridmix end time: "
argument_list|)
operator|.
name|append
argument_list|(
name|UTIL
operator|.
name|format
argument_list|(
name|getEndTime
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"\nGridmix simulation start time: "
argument_list|)
operator|.
name|append
argument_list|(
name|UTIL
operator|.
name|format
argument_list|(
name|getStartTime
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"\nGridmix runtime: "
argument_list|)
operator|.
name|append
argument_list|(
name|StringUtils
operator|.
name|formatTime
argument_list|(
name|getRuntime
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"\nTime spent in initialization (data-gen etc): "
argument_list|)
operator|.
name|append
argument_list|(
name|StringUtils
operator|.
name|formatTime
argument_list|(
name|getInitTime
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"\nTime spent in simulation: "
argument_list|)
operator|.
name|append
argument_list|(
name|StringUtils
operator|.
name|formatTime
argument_list|(
name|getSimulationTime
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"\nGridmix configuration parameters: "
argument_list|)
operator|.
name|append
argument_list|(
name|getCommandLineArgsString
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"\nGridmix job submission policy: "
argument_list|)
operator|.
name|append
argument_list|(
name|getJobSubmissionPolicy
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"\nGridmix resolver: "
argument_list|)
operator|.
name|append
argument_list|(
name|getUserResolver
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"\n\n"
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
comment|// Gets the stringified version of DataStatistics
DECL|method|stringifyDataStatistics (DataStatistics stats)
specifier|static
name|String
name|stringifyDataStatistics
parameter_list|(
name|DataStatistics
name|stats
parameter_list|)
block|{
if|if
condition|(
name|stats
operator|!=
literal|null
condition|)
block|{
name|StringBuffer
name|buffer
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|String
name|compressionStatus
init|=
name|stats
operator|.
name|isDataCompressed
argument_list|()
condition|?
literal|"Compressed"
else|:
literal|"Uncompressed"
decl_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|compressionStatus
argument_list|)
operator|.
name|append
argument_list|(
literal|" input data size: "
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|StringUtils
operator|.
name|humanReadableInt
argument_list|(
name|stats
operator|.
name|getDataSize
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|"Number of files: "
argument_list|)
operator|.
name|append
argument_list|(
name|stats
operator|.
name|getNumFiles
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|Summarizer
operator|.
name|NA
return|;
block|}
block|}
comment|// Getters
DECL|method|getExpectedDataSize ()
specifier|protected
name|String
name|getExpectedDataSize
parameter_list|()
block|{
return|return
name|expectedDataSize
return|;
block|}
DECL|method|getUserResolver ()
specifier|protected
name|String
name|getUserResolver
parameter_list|()
block|{
return|return
name|resolver
return|;
block|}
DECL|method|getInputDataStatistics ()
specifier|protected
name|String
name|getInputDataStatistics
parameter_list|()
block|{
return|return
name|stringifyDataStatistics
argument_list|(
name|dataStats
argument_list|)
return|;
block|}
DECL|method|getInputTraceSignature ()
specifier|protected
name|String
name|getInputTraceSignature
parameter_list|()
block|{
return|return
name|inputTraceSignature
return|;
block|}
DECL|method|getInputTraceLocation ()
specifier|protected
name|String
name|getInputTraceLocation
parameter_list|()
block|{
return|return
name|inputTraceLocation
return|;
block|}
DECL|method|getNumJobsInTrace ()
specifier|protected
name|int
name|getNumJobsInTrace
parameter_list|()
block|{
return|return
name|numJobsInInputTrace
return|;
block|}
DECL|method|getNumSuccessfulJobs ()
specifier|protected
name|int
name|getNumSuccessfulJobs
parameter_list|()
block|{
return|return
name|totalSuccessfulJobs
return|;
block|}
DECL|method|getNumFailedJobs ()
specifier|protected
name|int
name|getNumFailedJobs
parameter_list|()
block|{
return|return
name|totalFailedJobs
return|;
block|}
DECL|method|getNumSubmittedJobs ()
specifier|protected
name|int
name|getNumSubmittedJobs
parameter_list|()
block|{
return|return
name|totalSuccessfulJobs
operator|+
name|totalFailedJobs
return|;
block|}
DECL|method|getNumMapTasksLaunched ()
specifier|protected
name|int
name|getNumMapTasksLaunched
parameter_list|()
block|{
return|return
name|totalMapTasksLaunched
return|;
block|}
DECL|method|getNumReduceTasksLaunched ()
specifier|protected
name|int
name|getNumReduceTasksLaunched
parameter_list|()
block|{
return|return
name|totalReduceTasksLaunched
return|;
block|}
DECL|method|getStartTime ()
specifier|protected
name|long
name|getStartTime
parameter_list|()
block|{
return|return
name|startTime
return|;
block|}
DECL|method|getEndTime ()
specifier|protected
name|long
name|getEndTime
parameter_list|()
block|{
return|return
name|endTime
return|;
block|}
DECL|method|getInitTime ()
specifier|protected
name|long
name|getInitTime
parameter_list|()
block|{
return|return
name|simulationStartTime
operator|-
name|startTime
return|;
block|}
DECL|method|getSimulationStartTime ()
specifier|protected
name|long
name|getSimulationStartTime
parameter_list|()
block|{
return|return
name|simulationStartTime
return|;
block|}
DECL|method|getSimulationTime ()
specifier|protected
name|long
name|getSimulationTime
parameter_list|()
block|{
return|return
name|totalSimulationTime
return|;
block|}
DECL|method|getRuntime ()
specifier|protected
name|long
name|getRuntime
parameter_list|()
block|{
return|return
name|totalRuntime
return|;
block|}
DECL|method|getCommandLineArgsString ()
specifier|protected
name|String
name|getCommandLineArgsString
parameter_list|()
block|{
return|return
name|commandLineArgs
return|;
block|}
DECL|method|getJobSubmissionPolicy ()
specifier|protected
name|String
name|getJobSubmissionPolicy
parameter_list|()
block|{
return|return
name|jobSubmissionPolicy
return|;
block|}
block|}
end_class

end_unit

