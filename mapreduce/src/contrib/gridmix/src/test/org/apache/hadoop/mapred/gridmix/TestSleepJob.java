begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p/>  * http://www.apache.org/licenses/LICENSE-2.0  *<p/>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|logging
operator|.
name|impl
operator|.
name|Log4JLogger
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
name|security
operator|.
name|UserGroupInformation
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
name|JobStory
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
name|log4j
operator|.
name|Level
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|BlockingQueue
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
name|CountDownLatch
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
name|LinkedBlockingQueue
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

begin_class
DECL|class|TestSleepJob
specifier|public
class|class
name|TestSleepJob
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|Gridmix
operator|.
name|class
argument_list|)
decl_stmt|;
block|{
operator|(
operator|(
name|Log4JLogger
operator|)
name|LogFactory
operator|.
name|getLog
argument_list|(
literal|"org.apache.hadoop.mapred.gridmix"
argument_list|)
operator|)
operator|.
name|getLogger
argument_list|()
operator|.
name|setLevel
argument_list|(
name|Level
operator|.
name|DEBUG
argument_list|)
expr_stmt|;
block|}
DECL|field|policy
specifier|static
name|GridmixJobSubmissionPolicy
name|policy
init|=
name|GridmixJobSubmissionPolicy
operator|.
name|REPLAY
decl_stmt|;
DECL|field|NJOBS
specifier|private
specifier|static
specifier|final
name|int
name|NJOBS
init|=
literal|2
decl_stmt|;
DECL|field|GENDATA
specifier|private
specifier|static
specifier|final
name|long
name|GENDATA
init|=
literal|50
decl_stmt|;
comment|// in megabytes
annotation|@
name|BeforeClass
DECL|method|init ()
specifier|public
specifier|static
name|void
name|init
parameter_list|()
throws|throws
name|IOException
block|{
name|GridmixTestUtils
operator|.
name|initCluster
argument_list|()
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|shutDown ()
specifier|public
specifier|static
name|void
name|shutDown
parameter_list|()
throws|throws
name|IOException
block|{
name|GridmixTestUtils
operator|.
name|shutdownCluster
argument_list|()
expr_stmt|;
block|}
DECL|class|TestMonitor
specifier|static
class|class
name|TestMonitor
extends|extends
name|JobMonitor
block|{
DECL|field|retiredJobs
specifier|private
specifier|final
name|BlockingQueue
argument_list|<
name|Job
argument_list|>
name|retiredJobs
decl_stmt|;
DECL|field|expected
specifier|private
specifier|final
name|int
name|expected
decl_stmt|;
DECL|method|TestMonitor (int expected, Statistics stats)
specifier|public
name|TestMonitor
parameter_list|(
name|int
name|expected
parameter_list|,
name|Statistics
name|stats
parameter_list|)
block|{
name|super
argument_list|(
name|stats
argument_list|)
expr_stmt|;
name|this
operator|.
name|expected
operator|=
name|expected
expr_stmt|;
name|retiredJobs
operator|=
operator|new
name|LinkedBlockingQueue
argument_list|<
name|Job
argument_list|>
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onSuccess (Job job)
specifier|protected
name|void
name|onSuccess
parameter_list|(
name|Job
name|job
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|" Job Sucess "
operator|+
name|job
argument_list|)
expr_stmt|;
name|retiredJobs
operator|.
name|add
argument_list|(
name|job
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onFailure (Job job)
specifier|protected
name|void
name|onFailure
parameter_list|(
name|Job
name|job
parameter_list|)
block|{
name|fail
argument_list|(
literal|"Job failure: "
operator|+
name|job
argument_list|)
expr_stmt|;
block|}
DECL|method|verify (ArrayList<JobStory> submitted)
specifier|public
name|void
name|verify
parameter_list|(
name|ArrayList
argument_list|<
name|JobStory
argument_list|>
name|submitted
parameter_list|)
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|"Bad job count"
argument_list|,
name|expected
argument_list|,
name|retiredJobs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|DebugGridmix
specifier|static
class|class
name|DebugGridmix
extends|extends
name|Gridmix
block|{
DECL|field|factory
specifier|private
name|JobFactory
name|factory
decl_stmt|;
DECL|field|monitor
specifier|private
name|TestMonitor
name|monitor
decl_stmt|;
annotation|@
name|Override
DECL|method|createJobMonitor (Statistics stats)
specifier|protected
name|JobMonitor
name|createJobMonitor
parameter_list|(
name|Statistics
name|stats
parameter_list|)
block|{
name|monitor
operator|=
operator|new
name|TestMonitor
argument_list|(
name|NJOBS
operator|+
literal|1
argument_list|,
name|stats
argument_list|)
expr_stmt|;
return|return
name|monitor
return|;
block|}
annotation|@
name|Override
DECL|method|createJobFactory ( JobSubmitter submitter, String traceIn, Path scratchDir, Configuration conf, CountDownLatch startFlag, UserResolver userResolver)
specifier|protected
name|JobFactory
name|createJobFactory
parameter_list|(
name|JobSubmitter
name|submitter
parameter_list|,
name|String
name|traceIn
parameter_list|,
name|Path
name|scratchDir
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|CountDownLatch
name|startFlag
parameter_list|,
name|UserResolver
name|userResolver
parameter_list|)
throws|throws
name|IOException
block|{
name|factory
operator|=
name|DebugJobFactory
operator|.
name|getFactory
argument_list|(
name|submitter
argument_list|,
name|scratchDir
argument_list|,
name|NJOBS
argument_list|,
name|conf
argument_list|,
name|startFlag
argument_list|,
name|userResolver
argument_list|)
expr_stmt|;
return|return
name|factory
return|;
block|}
DECL|method|checkMonitor ()
specifier|public
name|void
name|checkMonitor
parameter_list|()
throws|throws
name|Exception
block|{
name|monitor
operator|.
name|verify
argument_list|(
operator|(
operator|(
name|DebugJobFactory
operator|.
name|Debuggable
operator|)
name|factory
operator|)
operator|.
name|getSubmitted
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testReplaySubmit ()
specifier|public
name|void
name|testReplaySubmit
parameter_list|()
throws|throws
name|Exception
block|{
name|policy
operator|=
name|GridmixJobSubmissionPolicy
operator|.
name|REPLAY
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|" Replay started at "
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
name|doSubmission
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|" Replay ended at "
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRandomLocationSubmit ()
specifier|public
name|void
name|testRandomLocationSubmit
parameter_list|()
throws|throws
name|Exception
block|{
name|policy
operator|=
name|GridmixJobSubmissionPolicy
operator|.
name|STRESS
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|" Random locations started at "
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
name|doSubmission
argument_list|(
literal|"-D"
operator|+
name|JobCreator
operator|.
name|SLEEPJOB_RANDOM_LOCATIONS
operator|+
literal|"=3"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|" Random locations ended at "
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMapTasksOnlySubmit ()
specifier|public
name|void
name|testMapTasksOnlySubmit
parameter_list|()
throws|throws
name|Exception
block|{
name|policy
operator|=
name|GridmixJobSubmissionPolicy
operator|.
name|STRESS
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|" Map tasks only at "
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
name|doSubmission
argument_list|(
literal|"-D"
operator|+
name|SleepJob
operator|.
name|SLEEPJOB_MAPTASK_ONLY
operator|+
literal|"=true"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|" Map tasks only ended at "
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testLimitTaskSleepTimeSubmit ()
specifier|public
name|void
name|testLimitTaskSleepTimeSubmit
parameter_list|()
throws|throws
name|Exception
block|{
name|policy
operator|=
name|GridmixJobSubmissionPolicy
operator|.
name|STRESS
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|" Limit sleep time only at "
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
name|doSubmission
argument_list|(
literal|"-D"
operator|+
name|SleepJob
operator|.
name|GRIDMIX_SLEEP_MAX_MAP_TIME
operator|+
literal|"=100"
argument_list|,
literal|"-D"
operator|+
name|SleepJob
operator|.
name|GRIDMIX_SLEEP_MAX_REDUCE_TIME
operator|+
literal|"=200"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|" Limit sleep time ended at "
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testStressSubmit ()
specifier|public
name|void
name|testStressSubmit
parameter_list|()
throws|throws
name|Exception
block|{
name|policy
operator|=
name|GridmixJobSubmissionPolicy
operator|.
name|STRESS
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|" Stress started at "
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
name|doSubmission
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|" Stress ended at "
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSerialSubmit ()
specifier|public
name|void
name|testSerialSubmit
parameter_list|()
throws|throws
name|Exception
block|{
name|policy
operator|=
name|GridmixJobSubmissionPolicy
operator|.
name|SERIAL
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Serial started at "
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
name|doSubmission
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Serial ended at "
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRandomLocation ()
specifier|public
name|void
name|testRandomLocation
parameter_list|()
throws|throws
name|Exception
block|{
name|UserGroupInformation
name|ugi
init|=
name|UserGroupInformation
operator|.
name|getLoginUser
argument_list|()
decl_stmt|;
comment|// testRandomLocation(0, 10, ugi);
name|testRandomLocation
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|,
name|ugi
argument_list|)
expr_stmt|;
name|testRandomLocation
argument_list|(
literal|2
argument_list|,
literal|10
argument_list|,
name|ugi
argument_list|)
expr_stmt|;
block|}
DECL|method|testRandomLocation (int locations, int njobs, UserGroupInformation ugi)
specifier|private
name|void
name|testRandomLocation
parameter_list|(
name|int
name|locations
parameter_list|,
name|int
name|njobs
parameter_list|,
name|UserGroupInformation
name|ugi
parameter_list|)
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
name|conf
operator|.
name|setInt
argument_list|(
name|JobCreator
operator|.
name|SLEEPJOB_RANDOM_LOCATIONS
argument_list|,
name|locations
argument_list|)
expr_stmt|;
name|DebugJobProducer
name|jobProducer
init|=
operator|new
name|DebugJobProducer
argument_list|(
name|njobs
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|JobConf
name|jconf
init|=
name|GridmixTestUtils
operator|.
name|mrCluster
operator|.
name|createJobConf
argument_list|(
operator|new
name|JobConf
argument_list|(
name|conf
argument_list|)
argument_list|)
decl_stmt|;
name|JobStory
name|story
decl_stmt|;
name|int
name|seq
init|=
literal|1
decl_stmt|;
while|while
condition|(
operator|(
name|story
operator|=
name|jobProducer
operator|.
name|getNextJob
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|GridmixJob
name|gridmixJob
init|=
name|JobCreator
operator|.
name|SLEEPJOB
operator|.
name|createGridmixJob
argument_list|(
name|jconf
argument_list|,
literal|0
argument_list|,
name|story
argument_list|,
operator|new
name|Path
argument_list|(
literal|"ignored"
argument_list|)
argument_list|,
name|ugi
argument_list|,
name|seq
operator|++
argument_list|)
decl_stmt|;
name|gridmixJob
operator|.
name|buildSplits
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|InputSplit
argument_list|>
name|splits
init|=
operator|new
name|SleepJob
operator|.
name|SleepInputFormat
argument_list|()
operator|.
name|getSplits
argument_list|(
name|gridmixJob
operator|.
name|getJob
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|InputSplit
name|split
range|:
name|splits
control|)
block|{
name|assertEquals
argument_list|(
name|locations
argument_list|,
name|split
operator|.
name|getLocations
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testMapTasksOnlySleepJobs ()
specifier|public
name|void
name|testMapTasksOnlySleepJobs
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
name|conf
operator|.
name|setBoolean
argument_list|(
name|SleepJob
operator|.
name|SLEEPJOB_MAPTASK_ONLY
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|DebugJobProducer
name|jobProducer
init|=
operator|new
name|DebugJobProducer
argument_list|(
literal|5
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|JobConf
name|jconf
init|=
name|GridmixTestUtils
operator|.
name|mrCluster
operator|.
name|createJobConf
argument_list|(
operator|new
name|JobConf
argument_list|(
name|conf
argument_list|)
argument_list|)
decl_stmt|;
name|UserGroupInformation
name|ugi
init|=
name|UserGroupInformation
operator|.
name|getLoginUser
argument_list|()
decl_stmt|;
name|JobStory
name|story
decl_stmt|;
name|int
name|seq
init|=
literal|1
decl_stmt|;
while|while
condition|(
operator|(
name|story
operator|=
name|jobProducer
operator|.
name|getNextJob
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|GridmixJob
name|gridmixJob
init|=
name|JobCreator
operator|.
name|SLEEPJOB
operator|.
name|createGridmixJob
argument_list|(
name|jconf
argument_list|,
literal|0
argument_list|,
name|story
argument_list|,
operator|new
name|Path
argument_list|(
literal|"ignored"
argument_list|)
argument_list|,
name|ugi
argument_list|,
name|seq
operator|++
argument_list|)
decl_stmt|;
name|gridmixJob
operator|.
name|buildSplits
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|Job
name|job
init|=
name|gridmixJob
operator|.
name|call
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|job
operator|.
name|getNumReduceTasks
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|doSubmission (String...optional)
specifier|private
name|void
name|doSubmission
parameter_list|(
name|String
modifier|...
name|optional
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|Path
name|in
init|=
operator|new
name|Path
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|makeQualified
argument_list|(
name|GridmixTestUtils
operator|.
name|dfs
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|out
init|=
name|GridmixTestUtils
operator|.
name|DEST
operator|.
name|makeQualified
argument_list|(
name|GridmixTestUtils
operator|.
name|dfs
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|root
init|=
operator|new
name|Path
argument_list|(
literal|"/user"
argument_list|)
decl_stmt|;
name|Configuration
name|conf
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|// required options
specifier|final
name|String
index|[]
name|required
init|=
block|{
literal|"-D"
operator|+
name|FilePool
operator|.
name|GRIDMIX_MIN_FILE
operator|+
literal|"=0"
block|,
literal|"-D"
operator|+
name|Gridmix
operator|.
name|GRIDMIX_OUT_DIR
operator|+
literal|"="
operator|+
name|out
block|,
literal|"-D"
operator|+
name|Gridmix
operator|.
name|GRIDMIX_USR_RSV
operator|+
literal|"="
operator|+
name|EchoUserResolver
operator|.
name|class
operator|.
name|getName
argument_list|()
block|,
literal|"-D"
operator|+
name|JobCreator
operator|.
name|GRIDMIX_JOB_TYPE
operator|+
literal|"="
operator|+
name|JobCreator
operator|.
name|SLEEPJOB
operator|.
name|name
argument_list|()
block|,
literal|"-D"
operator|+
name|SleepJob
operator|.
name|GRIDMIX_SLEEP_INTERVAL
operator|+
literal|"="
operator|+
literal|"10"
block|}
decl_stmt|;
comment|// mandatory arguments
specifier|final
name|String
index|[]
name|mandatory
init|=
block|{
literal|"-generate"
block|,
name|String
operator|.
name|valueOf
argument_list|(
name|GENDATA
argument_list|)
operator|+
literal|"m"
block|,
name|in
operator|.
name|toString
argument_list|()
block|,
literal|"-"
comment|// ignored by DebugGridmix
block|}
decl_stmt|;
name|ArrayList
argument_list|<
name|String
argument_list|>
name|argv
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
name|required
operator|.
name|length
operator|+
name|optional
operator|.
name|length
operator|+
name|mandatory
operator|.
name|length
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|s
range|:
name|required
control|)
block|{
name|argv
operator|.
name|add
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|s
range|:
name|optional
control|)
block|{
name|argv
operator|.
name|add
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|s
range|:
name|mandatory
control|)
block|{
name|argv
operator|.
name|add
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
name|DebugGridmix
name|client
init|=
operator|new
name|DebugGridmix
argument_list|()
decl_stmt|;
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|setEnum
argument_list|(
name|GridmixJobSubmissionPolicy
operator|.
name|JOB_SUBMISSION_POLICY
argument_list|,
name|policy
argument_list|)
expr_stmt|;
name|conf
operator|=
name|GridmixTestUtils
operator|.
name|mrCluster
operator|.
name|createJobConf
argument_list|(
operator|new
name|JobConf
argument_list|(
name|conf
argument_list|)
argument_list|)
expr_stmt|;
comment|// allow synthetic users to create home directories
name|GridmixTestUtils
operator|.
name|dfs
operator|.
name|mkdirs
argument_list|(
name|root
argument_list|,
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
literal|0777
argument_list|)
argument_list|)
expr_stmt|;
name|GridmixTestUtils
operator|.
name|dfs
operator|.
name|setPermission
argument_list|(
name|root
argument_list|,
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
literal|0777
argument_list|)
argument_list|)
expr_stmt|;
name|String
index|[]
name|args
init|=
name|argv
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|argv
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Command line arguments:"
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
name|args
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
name|printf
argument_list|(
literal|"    [%d] %s\n"
argument_list|,
name|i
argument_list|,
name|args
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|int
name|res
init|=
name|ToolRunner
operator|.
name|run
argument_list|(
name|conf
argument_list|,
name|client
argument_list|,
name|args
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Client exited with nonzero status"
argument_list|,
literal|0
argument_list|,
name|res
argument_list|)
expr_stmt|;
name|client
operator|.
name|checkMonitor
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|in
operator|.
name|getFileSystem
argument_list|(
name|conf
argument_list|)
operator|.
name|delete
argument_list|(
name|in
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|out
operator|.
name|getFileSystem
argument_list|(
name|conf
argument_list|)
operator|.
name|delete
argument_list|(
name|out
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|root
operator|.
name|getFileSystem
argument_list|(
name|conf
argument_list|)
operator|.
name|delete
argument_list|(
name|root
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

