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
name|test
operator|.
name|GenericTestUtils
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
name|event
operator|.
name|Level
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
name|List
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

begin_import
import|import static
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
operator|.
name|getLogger
import|;
end_import

begin_class
DECL|class|TestSleepJob
specifier|public
class|class
name|TestSleepJob
extends|extends
name|CommonJobTest
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|getLogger
argument_list|(
name|Gridmix
operator|.
name|class
argument_list|)
decl_stmt|;
static|static
block|{
name|GenericTestUtils
operator|.
name|setLogLevel
argument_list|(
name|getLogger
argument_list|(
literal|"org.apache.hadoop.mapred.gridmix"
argument_list|)
argument_list|,
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
argument_list|(
name|TestSleepJob
operator|.
name|class
argument_list|)
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
name|configuration
init|=
name|GridmixTestUtils
operator|.
name|mrvl
operator|.
name|getConfig
argument_list|()
decl_stmt|;
name|DebugJobProducer
name|jobProducer
init|=
operator|new
name|DebugJobProducer
argument_list|(
literal|5
argument_list|,
name|configuration
argument_list|)
decl_stmt|;
name|configuration
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
name|configuration
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
name|jobProducer
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|seq
argument_list|)
expr_stmt|;
block|}
comment|/*   * test RandomLocation   */
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
comment|// test Serial submit
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
comment|// set policy
name|policy
operator|=
name|GridmixJobSubmissionPolicy
operator|.
name|SERIAL
expr_stmt|;
name|LOG
operator|.
name|info
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
argument_list|(
name|JobCreator
operator|.
name|SLEEPJOB
operator|.
name|name
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
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
name|LOG
operator|.
name|info
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
argument_list|(
name|JobCreator
operator|.
name|SLEEPJOB
operator|.
name|name
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
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
name|LOG
operator|.
name|info
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
argument_list|(
name|JobCreator
operator|.
name|SLEEPJOB
operator|.
name|name
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
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
name|configuration
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|DebugJobProducer
name|jobProducer
init|=
operator|new
name|DebugJobProducer
argument_list|(
name|njobs
argument_list|,
name|configuration
argument_list|)
decl_stmt|;
name|Configuration
name|jconf
init|=
name|GridmixTestUtils
operator|.
name|mrvl
operator|.
name|getConfig
argument_list|()
decl_stmt|;
name|jconf
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
name|jobProducer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

