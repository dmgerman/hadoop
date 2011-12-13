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
name|TaskCompletionEvent
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
name|TaskID
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
DECL|class|TestUberAM
specifier|public
class|class
name|TestUberAM
extends|extends
name|TestMRJobs
block|{
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
name|TestUberAM
operator|.
name|class
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
name|TestMRJobs
operator|.
name|setup
argument_list|()
expr_stmt|;
if|if
condition|(
name|mrCluster
operator|!=
literal|null
condition|)
block|{
name|mrCluster
operator|.
name|getConfig
argument_list|()
operator|.
name|setBoolean
argument_list|(
name|MRJobConfig
operator|.
name|JOB_UBERTASK_ENABLE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
annotation|@
name|Test
DECL|method|testSleepJob ()
specifier|public
name|void
name|testSleepJob
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
throws|,
name|ClassNotFoundException
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
name|getConfig
argument_list|()
operator|.
name|setInt
argument_list|(
literal|"TestMRJobs.testSleepJob.reduces"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
name|super
operator|.
name|testSleepJob
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|verifySleepJobCounters (Job job)
specifier|protected
name|void
name|verifySleepJobCounters
parameter_list|(
name|Job
name|job
parameter_list|)
throws|throws
name|InterruptedException
throws|,
name|IOException
block|{
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
literal|3
argument_list|,
name|counters
operator|.
name|findCounter
argument_list|(
name|JobCounter
operator|.
name|OTHER_LOCAL_MAPS
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
literal|1
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
name|assertTrue
argument_list|(
name|counters
operator|.
name|findCounter
argument_list|(
name|JobCounter
operator|.
name|SLOTS_MILLIS_MAPS
argument_list|)
operator|!=
literal|null
operator|&&
name|counters
operator|.
name|findCounter
argument_list|(
name|JobCounter
operator|.
name|SLOTS_MILLIS_MAPS
argument_list|)
operator|.
name|getValue
argument_list|()
operator|!=
literal|0
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|counters
operator|.
name|findCounter
argument_list|(
name|JobCounter
operator|.
name|SLOTS_MILLIS_MAPS
argument_list|)
operator|!=
literal|null
operator|&&
name|counters
operator|.
name|findCounter
argument_list|(
name|JobCounter
operator|.
name|SLOTS_MILLIS_MAPS
argument_list|)
operator|.
name|getValue
argument_list|()
operator|!=
literal|0
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
name|NUM_UBER_SUBMAPS
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
name|NUM_UBER_SUBREDUCES
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
literal|4
argument_list|,
name|counters
operator|.
name|findCounter
argument_list|(
name|JobCounter
operator|.
name|TOTAL_LAUNCHED_UBERTASKS
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|Test
DECL|method|testRandomWriter ()
specifier|public
name|void
name|testRandomWriter
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
throws|,
name|ClassNotFoundException
block|{
name|super
operator|.
name|testRandomWriter
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|verifyRandomWriterCounters (Job job)
specifier|protected
name|void
name|verifyRandomWriterCounters
parameter_list|(
name|Job
name|job
parameter_list|)
throws|throws
name|InterruptedException
throws|,
name|IOException
block|{
name|super
operator|.
name|verifyRandomWriterCounters
argument_list|(
name|job
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
literal|3
argument_list|,
name|counters
operator|.
name|findCounter
argument_list|(
name|JobCounter
operator|.
name|NUM_UBER_SUBMAPS
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
name|TOTAL_LAUNCHED_UBERTASKS
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|Test
DECL|method|testFailingMapper ()
specifier|public
name|void
name|testFailingMapper
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
throws|,
name|ClassNotFoundException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"\n\n\nStarting uberized testFailingMapper()."
argument_list|)
expr_stmt|;
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
name|Job
name|job
init|=
name|runFailingMapperJob
argument_list|()
decl_stmt|;
comment|// should be able to get diags for single task attempt...
name|TaskID
name|taskID
init|=
operator|new
name|TaskID
argument_list|(
name|job
operator|.
name|getJobID
argument_list|()
argument_list|,
name|TaskType
operator|.
name|MAP
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|TaskAttemptID
name|aId
init|=
operator|new
name|TaskAttemptID
argument_list|(
name|taskID
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Diagnostics for "
operator|+
name|aId
operator|+
literal|" :"
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|diag
range|:
name|job
operator|.
name|getTaskDiagnostics
argument_list|(
name|aId
argument_list|)
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|diag
argument_list|)
expr_stmt|;
block|}
comment|// ...but not for second (shouldn't exist:  uber-AM overrode max attempts)
name|boolean
name|secondTaskAttemptExists
init|=
literal|true
decl_stmt|;
try|try
block|{
name|aId
operator|=
operator|new
name|TaskAttemptID
argument_list|(
name|taskID
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Diagnostics for "
operator|+
name|aId
operator|+
literal|" :"
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|diag
range|:
name|job
operator|.
name|getTaskDiagnostics
argument_list|(
name|aId
argument_list|)
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|diag
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
name|secondTaskAttemptExists
operator|=
literal|false
expr_stmt|;
block|}
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|secondTaskAttemptExists
argument_list|)
expr_stmt|;
name|TaskCompletionEvent
index|[]
name|events
init|=
name|job
operator|.
name|getTaskCompletionEvents
argument_list|(
literal|0
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|events
operator|.
name|length
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|TaskCompletionEvent
operator|.
name|Status
operator|.
name|TIPFAILED
argument_list|,
name|events
index|[
literal|0
index|]
operator|.
name|getStatus
argument_list|()
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
name|FAILED
argument_list|,
name|job
operator|.
name|getJobState
argument_list|()
argument_list|)
expr_stmt|;
comment|//Disabling till UberAM honors MRJobConfig.MAP_MAX_ATTEMPTS
comment|//verifyFailingMapperCounters(job);
comment|// TODO later:  add explicit "isUber()" checks of some sort
block|}
annotation|@
name|Override
DECL|method|verifyFailingMapperCounters (Job job)
specifier|protected
name|void
name|verifyFailingMapperCounters
parameter_list|(
name|Job
name|job
parameter_list|)
throws|throws
name|InterruptedException
throws|,
name|IOException
block|{
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
name|OTHER_LOCAL_MAPS
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
name|NUM_FAILED_MAPS
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|counters
operator|.
name|findCounter
argument_list|(
name|JobCounter
operator|.
name|SLOTS_MILLIS_MAPS
argument_list|)
operator|!=
literal|null
operator|&&
name|counters
operator|.
name|findCounter
argument_list|(
name|JobCounter
operator|.
name|SLOTS_MILLIS_MAPS
argument_list|)
operator|.
name|getValue
argument_list|()
operator|!=
literal|0
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
name|TOTAL_LAUNCHED_UBERTASKS
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
name|NUM_UBER_SUBMAPS
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
name|NUM_FAILED_UBERTASKS
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//@Test  //FIXME:  if/when the corresponding TestMRJobs test gets enabled, do so here as well (potentially with mods for ubermode)
DECL|method|testSleepJobWithSecurityOn ()
specifier|public
name|void
name|testSleepJobWithSecurityOn
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
throws|,
name|ClassNotFoundException
block|{
name|super
operator|.
name|testSleepJobWithSecurityOn
argument_list|()
expr_stmt|;
block|}
comment|// Add a test for distcache when uber mode is enabled. TODO
annotation|@
name|Override
annotation|@
name|Test
DECL|method|testDistributedCache ()
specifier|public
name|void
name|testDistributedCache
parameter_list|()
throws|throws
name|Exception
block|{
comment|//
block|}
block|}
end_class

end_unit

