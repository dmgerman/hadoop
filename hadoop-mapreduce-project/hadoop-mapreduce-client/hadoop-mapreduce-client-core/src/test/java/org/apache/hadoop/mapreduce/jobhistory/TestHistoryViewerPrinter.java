begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.jobhistory
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|jobhistory
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
name|mapred
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
name|mapred
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
name|mapred
operator|.
name|TaskStatus
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
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|skyscreamer
operator|.
name|jsonassert
operator|.
name|JSONAssert
import|;
end_import

begin_import
import|import
name|org
operator|.
name|skyscreamer
operator|.
name|jsonassert
operator|.
name|JSONCompareMode
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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TimeZone
import|;
end_import

begin_class
DECL|class|TestHistoryViewerPrinter
specifier|public
class|class
name|TestHistoryViewerPrinter
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
name|TestHistoryViewerPrinter
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Test
DECL|method|testHumanPrinter ()
specifier|public
name|void
name|testHumanPrinter
parameter_list|()
throws|throws
name|Exception
block|{
name|JobHistoryParser
operator|.
name|JobInfo
name|job
init|=
name|createJobInfo
argument_list|()
decl_stmt|;
name|HumanReadableHistoryViewerPrinter
name|printer
init|=
operator|new
name|HumanReadableHistoryViewerPrinter
argument_list|(
name|job
argument_list|,
literal|false
argument_list|,
literal|"http://"
argument_list|,
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
literal|"GMT"
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|outStr
init|=
name|run
argument_list|(
name|printer
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"\n"
operator|+
literal|"Hadoop job: job_1317928501754_0001\n"
operator|+
literal|"=====================================\n"
operator|+
literal|"User: rkanter\n"
operator|+
literal|"JobName: my job\n"
operator|+
literal|"JobConf: /tmp/job.xml\n"
operator|+
literal|"Submitted At: 6-Oct-2011 19:15:01\n"
operator|+
literal|"Launched At: 6-Oct-2011 19:15:02 (1sec)\n"
operator|+
literal|"Finished At: 6-Oct-2011 19:15:16 (14sec)\n"
operator|+
literal|"Status: SUCCEEDED\n"
operator|+
literal|"Counters: \n"
operator|+
literal|"\n"
operator|+
literal|"|Group Name                    |Counter name                  |Map Value |Reduce Value|Total Value|\n"
operator|+
literal|"---------------------------------------------------------------------------------------\n"
operator|+
literal|"|group1                        |counter1                      |5         |5         |5         \n"
operator|+
literal|"|group1                        |counter2                      |10        |10        |10        \n"
operator|+
literal|"|group2                        |counter1                      |15        |15        |15        \n"
operator|+
literal|"\n"
operator|+
literal|"=====================================\n"
operator|+
literal|"\n"
operator|+
literal|"Task Summary\n"
operator|+
literal|"============================\n"
operator|+
literal|"Kind\tTotal\tSuccessful\tFailed\tKilled\tStartTime\tFinishTime\n"
operator|+
literal|"\n"
operator|+
literal|"Setup\t1\t1\t\t0\t0\t6-Oct-2011 19:15:03\t6-Oct-2011 19:15:04 (1sec)\n"
operator|+
literal|"Map\t6\t5\t\t1\t0\t6-Oct-2011 19:15:04\t6-Oct-2011 19:15:16 (12sec)\n"
operator|+
literal|"Reduce\t1\t1\t\t0\t0\t6-Oct-2011 19:15:10\t6-Oct-2011 19:15:18 (8sec)\n"
operator|+
literal|"Cleanup\t1\t1\t\t0\t0\t6-Oct-2011 19:15:11\t6-Oct-2011 19:15:20 (9sec)\n"
operator|+
literal|"============================\n"
operator|+
literal|"\n"
operator|+
literal|"\n"
operator|+
literal|"Analysis\n"
operator|+
literal|"=========\n"
operator|+
literal|"\n"
operator|+
literal|"Time taken by best performing map task task_1317928501754_0001_m_000003: 3sec\n"
operator|+
literal|"Average time taken by map tasks: 5sec\n"
operator|+
literal|"Worse performing map tasks: \n"
operator|+
literal|"TaskId\t\tTimetaken\n"
operator|+
literal|"task_1317928501754_0001_m_000007 7sec\n"
operator|+
literal|"task_1317928501754_0001_m_000006 6sec\n"
operator|+
literal|"task_1317928501754_0001_m_000005 5sec\n"
operator|+
literal|"task_1317928501754_0001_m_000004 4sec\n"
operator|+
literal|"task_1317928501754_0001_m_000003 3sec\n"
operator|+
literal|"The last map task task_1317928501754_0001_m_000007 finished at (relative to the Job launch time): 6-Oct-2011 19:15:16 (14sec)\n"
operator|+
literal|"\n"
operator|+
literal|"Time taken by best performing shuffle task task_1317928501754_0001_r_000008: 8sec\n"
operator|+
literal|"Average time taken by shuffle tasks: 8sec\n"
operator|+
literal|"Worse performing shuffle tasks: \n"
operator|+
literal|"TaskId\t\tTimetaken\n"
operator|+
literal|"task_1317928501754_0001_r_000008 8sec\n"
operator|+
literal|"The last shuffle task task_1317928501754_0001_r_000008 finished at (relative to the Job launch time): 6-Oct-2011 19:15:18 (16sec)\n"
operator|+
literal|"\n"
operator|+
literal|"Time taken by best performing reduce task task_1317928501754_0001_r_000008: 0sec\n"
operator|+
literal|"Average time taken by reduce tasks: 0sec\n"
operator|+
literal|"Worse performing reduce tasks: \n"
operator|+
literal|"TaskId\t\tTimetaken\n"
operator|+
literal|"task_1317928501754_0001_r_000008 0sec\n"
operator|+
literal|"The last reduce task task_1317928501754_0001_r_000008 finished at (relative to the Job launch time): 6-Oct-2011 19:15:18 (16sec)\n"
operator|+
literal|"=========\n"
operator|+
literal|"\n"
operator|+
literal|"FAILED MAP task list for job_1317928501754_0001\n"
operator|+
literal|"TaskId\t\tStartTime\tFinishTime\tError\tInputSplits\n"
operator|+
literal|"====================================================\n"
operator|+
literal|"task_1317928501754_0001_m_000002\t6-Oct-2011 19:15:04\t6-Oct-2011 19:15:06 (2sec)\t\t\n"
operator|+
literal|"\n"
operator|+
literal|"FAILED task attempts by nodes\n"
operator|+
literal|"Hostname\tFailedTasks\n"
operator|+
literal|"===============================\n"
operator|+
literal|"localhost\ttask_1317928501754_0001_m_000002, \n"
argument_list|,
name|outStr
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testHumanPrinterAll ()
specifier|public
name|void
name|testHumanPrinterAll
parameter_list|()
throws|throws
name|Exception
block|{
name|JobHistoryParser
operator|.
name|JobInfo
name|job
init|=
name|createJobInfo
argument_list|()
decl_stmt|;
name|HumanReadableHistoryViewerPrinter
name|printer
init|=
operator|new
name|HumanReadableHistoryViewerPrinter
argument_list|(
name|job
argument_list|,
literal|true
argument_list|,
literal|"http://"
argument_list|,
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
literal|"GMT"
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|outStr
init|=
name|run
argument_list|(
name|printer
argument_list|)
decl_stmt|;
if|if
condition|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.version"
argument_list|)
operator|.
name|startsWith
argument_list|(
literal|"1.7"
argument_list|)
condition|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"\n"
operator|+
literal|"Hadoop job: job_1317928501754_0001\n"
operator|+
literal|"=====================================\n"
operator|+
literal|"User: rkanter\n"
operator|+
literal|"JobName: my job\n"
operator|+
literal|"JobConf: /tmp/job.xml\n"
operator|+
literal|"Submitted At: 6-Oct-2011 19:15:01\n"
operator|+
literal|"Launched At: 6-Oct-2011 19:15:02 (1sec)\n"
operator|+
literal|"Finished At: 6-Oct-2011 19:15:16 (14sec)\n"
operator|+
literal|"Status: SUCCEEDED\n"
operator|+
literal|"Counters: \n"
operator|+
literal|"\n"
operator|+
literal|"|Group Name                    |Counter name                  |Map Value |Reduce Value|Total Value|\n"
operator|+
literal|"---------------------------------------------------------------------------------------\n"
operator|+
literal|"|group1                        |counter1                      |5         |5         |5         \n"
operator|+
literal|"|group1                        |counter2                      |10        |10        |10        \n"
operator|+
literal|"|group2                        |counter1                      |15        |15        |15        \n"
operator|+
literal|"\n"
operator|+
literal|"=====================================\n"
operator|+
literal|"\n"
operator|+
literal|"Task Summary\n"
operator|+
literal|"============================\n"
operator|+
literal|"Kind\tTotal\tSuccessful\tFailed\tKilled\tStartTime\tFinishTime\n"
operator|+
literal|"\n"
operator|+
literal|"Setup\t1\t1\t\t0\t0\t6-Oct-2011 19:15:03\t6-Oct-2011 19:15:04 (1sec)\n"
operator|+
literal|"Map\t6\t5\t\t1\t0\t6-Oct-2011 19:15:04\t6-Oct-2011 19:15:16 (12sec)\n"
operator|+
literal|"Reduce\t1\t1\t\t0\t0\t6-Oct-2011 19:15:10\t6-Oct-2011 19:15:18 (8sec)\n"
operator|+
literal|"Cleanup\t1\t1\t\t0\t0\t6-Oct-2011 19:15:11\t6-Oct-2011 19:15:20 (9sec)\n"
operator|+
literal|"============================\n"
operator|+
literal|"\n"
operator|+
literal|"\n"
operator|+
literal|"Analysis\n"
operator|+
literal|"=========\n"
operator|+
literal|"\n"
operator|+
literal|"Time taken by best performing map task task_1317928501754_0001_m_000003: 3sec\n"
operator|+
literal|"Average time taken by map tasks: 5sec\n"
operator|+
literal|"Worse performing map tasks: \n"
operator|+
literal|"TaskId\t\tTimetaken\n"
operator|+
literal|"task_1317928501754_0001_m_000007 7sec\n"
operator|+
literal|"task_1317928501754_0001_m_000006 6sec\n"
operator|+
literal|"task_1317928501754_0001_m_000005 5sec\n"
operator|+
literal|"task_1317928501754_0001_m_000004 4sec\n"
operator|+
literal|"task_1317928501754_0001_m_000003 3sec\n"
operator|+
literal|"The last map task task_1317928501754_0001_m_000007 finished at (relative to the Job launch time): 6-Oct-2011 19:15:16 (14sec)\n"
operator|+
literal|"\n"
operator|+
literal|"Time taken by best performing shuffle task task_1317928501754_0001_r_000008: 8sec\n"
operator|+
literal|"Average time taken by shuffle tasks: 8sec\n"
operator|+
literal|"Worse performing shuffle tasks: \n"
operator|+
literal|"TaskId\t\tTimetaken\n"
operator|+
literal|"task_1317928501754_0001_r_000008 8sec\n"
operator|+
literal|"The last shuffle task task_1317928501754_0001_r_000008 finished at (relative to the Job launch time): 6-Oct-2011 19:15:18 (16sec)\n"
operator|+
literal|"\n"
operator|+
literal|"Time taken by best performing reduce task task_1317928501754_0001_r_000008: 0sec\n"
operator|+
literal|"Average time taken by reduce tasks: 0sec\n"
operator|+
literal|"Worse performing reduce tasks: \n"
operator|+
literal|"TaskId\t\tTimetaken\n"
operator|+
literal|"task_1317928501754_0001_r_000008 0sec\n"
operator|+
literal|"The last reduce task task_1317928501754_0001_r_000008 finished at (relative to the Job launch time): 6-Oct-2011 19:15:18 (16sec)\n"
operator|+
literal|"=========\n"
operator|+
literal|"\n"
operator|+
literal|"FAILED MAP task list for job_1317928501754_0001\n"
operator|+
literal|"TaskId\t\tStartTime\tFinishTime\tError\tInputSplits\n"
operator|+
literal|"====================================================\n"
operator|+
literal|"task_1317928501754_0001_m_000002\t6-Oct-2011 19:15:04\t6-Oct-2011 19:15:06 (2sec)\t\t\n"
operator|+
literal|"\n"
operator|+
literal|"SUCCEEDED JOB_SETUP task list for job_1317928501754_0001\n"
operator|+
literal|"TaskId\t\tStartTime\tFinishTime\tError\n"
operator|+
literal|"====================================================\n"
operator|+
literal|"task_1317928501754_0001_s_000001\t6-Oct-2011 19:15:03\t6-Oct-2011 19:15:04 (1sec)\t\n"
operator|+
literal|"\n"
operator|+
literal|"SUCCEEDED MAP task list for job_1317928501754_0001\n"
operator|+
literal|"TaskId\t\tStartTime\tFinishTime\tError\tInputSplits\n"
operator|+
literal|"====================================================\n"
operator|+
literal|"task_1317928501754_0001_m_000006\t6-Oct-2011 19:15:08\t6-Oct-2011 19:15:14 (6sec)\t\t\n"
operator|+
literal|"\n"
operator|+
literal|"SUCCEEDED MAP task list for job_1317928501754_0001\n"
operator|+
literal|"TaskId\t\tStartTime\tFinishTime\tError\tInputSplits\n"
operator|+
literal|"====================================================\n"
operator|+
literal|"task_1317928501754_0001_m_000005\t6-Oct-2011 19:15:07\t6-Oct-2011 19:15:12 (5sec)\t\t\n"
operator|+
literal|"\n"
operator|+
literal|"SUCCEEDED MAP task list for job_1317928501754_0001\n"
operator|+
literal|"TaskId\t\tStartTime\tFinishTime\tError\tInputSplits\n"
operator|+
literal|"====================================================\n"
operator|+
literal|"task_1317928501754_0001_m_000004\t6-Oct-2011 19:15:06\t6-Oct-2011 19:15:10 (4sec)\t\t\n"
operator|+
literal|"\n"
operator|+
literal|"SUCCEEDED MAP task list for job_1317928501754_0001\n"
operator|+
literal|"TaskId\t\tStartTime\tFinishTime\tError\tInputSplits\n"
operator|+
literal|"====================================================\n"
operator|+
literal|"task_1317928501754_0001_m_000003\t6-Oct-2011 19:15:05\t6-Oct-2011 19:15:08 (3sec)\t\t\n"
operator|+
literal|"\n"
operator|+
literal|"SUCCEEDED MAP task list for job_1317928501754_0001\n"
operator|+
literal|"TaskId\t\tStartTime\tFinishTime\tError\tInputSplits\n"
operator|+
literal|"====================================================\n"
operator|+
literal|"task_1317928501754_0001_m_000007\t6-Oct-2011 19:15:09\t6-Oct-2011 19:15:16 (7sec)\t\t\n"
operator|+
literal|"\n"
operator|+
literal|"SUCCEEDED REDUCE task list for job_1317928501754_0001\n"
operator|+
literal|"TaskId\t\tStartTime\tFinishTime\tError\n"
operator|+
literal|"====================================================\n"
operator|+
literal|"task_1317928501754_0001_r_000008\t6-Oct-2011 19:15:10\t6-Oct-2011 19:15:18 (8sec)\t\n"
operator|+
literal|"\n"
operator|+
literal|"SUCCEEDED JOB_CLEANUP task list for job_1317928501754_0001\n"
operator|+
literal|"TaskId\t\tStartTime\tFinishTime\tError\n"
operator|+
literal|"====================================================\n"
operator|+
literal|"task_1317928501754_0001_c_000009\t6-Oct-2011 19:15:11\t6-Oct-2011 19:15:20 (9sec)\t\n"
operator|+
literal|"\n"
operator|+
literal|"JOB_SETUP task list for job_1317928501754_0001\n"
operator|+
literal|"TaskId\t\tStartTime\tFinishTime\tHostName\tError\tTaskLogs\n"
operator|+
literal|"====================================================\n"
operator|+
literal|"attempt_1317928501754_0001_s_000001_1\t6-Oct-2011 19:15:03\t6-Oct-2011 19:15:04 (1sec)\tlocalhost\thttp://t:1234/tasklog?attemptid=attempt_1317928501754_0001_s_000001_1\n"
operator|+
literal|"\n"
operator|+
literal|"MAP task list for job_1317928501754_0001\n"
operator|+
literal|"TaskId\t\tStartTime\tFinishTime\tHostName\tError\tTaskLogs\n"
operator|+
literal|"====================================================\n"
operator|+
literal|"attempt_1317928501754_0001_m_000002_1\t6-Oct-2011 19:15:04\t6-Oct-2011 19:15:06 (2sec)\tlocalhost\thttp://t:1234/tasklog?attemptid=attempt_1317928501754_0001_m_000002_1\n"
operator|+
literal|"attempt_1317928501754_0001_m_000006_1\t6-Oct-2011 19:15:08\t6-Oct-2011 19:15:14 (6sec)\tlocalhost\thttp://t:1234/tasklog?attemptid=attempt_1317928501754_0001_m_000006_1\n"
operator|+
literal|"attempt_1317928501754_0001_m_000005_1\t6-Oct-2011 19:15:07\t6-Oct-2011 19:15:12 (5sec)\tlocalhost\thttp://t:1234/tasklog?attemptid=attempt_1317928501754_0001_m_000005_1\n"
operator|+
literal|"attempt_1317928501754_0001_m_000004_1\t6-Oct-2011 19:15:06\t6-Oct-2011 19:15:10 (4sec)\tlocalhost\thttp://t:1234/tasklog?attemptid=attempt_1317928501754_0001_m_000004_1\n"
operator|+
literal|"attempt_1317928501754_0001_m_000003_1\t6-Oct-2011 19:15:05\t6-Oct-2011 19:15:08 (3sec)\tlocalhost\thttp://t:1234/tasklog?attemptid=attempt_1317928501754_0001_m_000003_1\n"
operator|+
literal|"attempt_1317928501754_0001_m_000007_1\t6-Oct-2011 19:15:09\t6-Oct-2011 19:15:16 (7sec)\tlocalhost\thttp://t:1234/tasklog?attemptid=attempt_1317928501754_0001_m_000007_1\n"
operator|+
literal|"\n"
operator|+
literal|"REDUCE task list for job_1317928501754_0001\n"
operator|+
literal|"TaskId\t\tStartTime\tShuffleFinished\tSortFinished\tFinishTime\tHostName\tError\tTaskLogs\n"
operator|+
literal|"====================================================\n"
operator|+
literal|"attempt_1317928501754_0001_r_000008_1\t6-Oct-2011 19:15:10\t6-Oct-2011 19:15:18 (8sec)\t6-Oct-2011 19:15:18 (0sec)6-Oct-2011 19:15:18 (8sec)\tlocalhost\thttp://t:1234/tasklog?attemptid=attempt_1317928501754_0001_r_000008_1\n"
operator|+
literal|"\n"
operator|+
literal|"JOB_CLEANUP task list for job_1317928501754_0001\n"
operator|+
literal|"TaskId\t\tStartTime\tFinishTime\tHostName\tError\tTaskLogs\n"
operator|+
literal|"====================================================\n"
operator|+
literal|"attempt_1317928501754_0001_c_000009_1\t6-Oct-2011 19:15:11\t6-Oct-2011 19:15:20 (9sec)\tlocalhost\thttp://t:1234/tasklog?attemptid=attempt_1317928501754_0001_c_000009_1\n"
operator|+
literal|"\n"
operator|+
literal|"FAILED task attempts by nodes\n"
operator|+
literal|"Hostname\tFailedTasks\n"
operator|+
literal|"===============================\n"
operator|+
literal|"localhost\ttask_1317928501754_0001_m_000002, \n"
argument_list|,
name|outStr
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"\n"
operator|+
literal|"Hadoop job: job_1317928501754_0001\n"
operator|+
literal|"=====================================\n"
operator|+
literal|"User: rkanter\n"
operator|+
literal|"JobName: my job\n"
operator|+
literal|"JobConf: /tmp/job.xml\n"
operator|+
literal|"Submitted At: 6-Oct-2011 19:15:01\n"
operator|+
literal|"Launched At: 6-Oct-2011 19:15:02 (1sec)\n"
operator|+
literal|"Finished At: 6-Oct-2011 19:15:16 (14sec)\n"
operator|+
literal|"Status: SUCCEEDED\n"
operator|+
literal|"Counters: \n"
operator|+
literal|"\n"
operator|+
literal|"|Group Name                    |Counter name                  |Map Value |Reduce Value|Total Value|\n"
operator|+
literal|"---------------------------------------------------------------------------------------\n"
operator|+
literal|"|group1                        |counter1                      |5         |5         |5         \n"
operator|+
literal|"|group1                        |counter2                      |10        |10        |10        \n"
operator|+
literal|"|group2                        |counter1                      |15        |15        |15        \n"
operator|+
literal|"\n"
operator|+
literal|"=====================================\n"
operator|+
literal|"\n"
operator|+
literal|"Task Summary\n"
operator|+
literal|"============================\n"
operator|+
literal|"Kind\tTotal\tSuccessful\tFailed\tKilled\tStartTime\tFinishTime\n"
operator|+
literal|"\n"
operator|+
literal|"Setup\t1\t1\t\t0\t0\t6-Oct-2011 19:15:03\t6-Oct-2011 19:15:04 (1sec)\n"
operator|+
literal|"Map\t6\t5\t\t1\t0\t6-Oct-2011 19:15:04\t6-Oct-2011 19:15:16 (12sec)\n"
operator|+
literal|"Reduce\t1\t1\t\t0\t0\t6-Oct-2011 19:15:10\t6-Oct-2011 19:15:18 (8sec)\n"
operator|+
literal|"Cleanup\t1\t1\t\t0\t0\t6-Oct-2011 19:15:11\t6-Oct-2011 19:15:20 (9sec)\n"
operator|+
literal|"============================\n"
operator|+
literal|"\n"
operator|+
literal|"\n"
operator|+
literal|"Analysis\n"
operator|+
literal|"=========\n"
operator|+
literal|"\n"
operator|+
literal|"Time taken by best performing map task task_1317928501754_0001_m_000003: 3sec\n"
operator|+
literal|"Average time taken by map tasks: 5sec\n"
operator|+
literal|"Worse performing map tasks: \n"
operator|+
literal|"TaskId\t\tTimetaken\n"
operator|+
literal|"task_1317928501754_0001_m_000007 7sec\n"
operator|+
literal|"task_1317928501754_0001_m_000006 6sec\n"
operator|+
literal|"task_1317928501754_0001_m_000005 5sec\n"
operator|+
literal|"task_1317928501754_0001_m_000004 4sec\n"
operator|+
literal|"task_1317928501754_0001_m_000003 3sec\n"
operator|+
literal|"The last map task task_1317928501754_0001_m_000007 finished at (relative to the Job launch time): 6-Oct-2011 19:15:16 (14sec)\n"
operator|+
literal|"\n"
operator|+
literal|"Time taken by best performing shuffle task task_1317928501754_0001_r_000008: 8sec\n"
operator|+
literal|"Average time taken by shuffle tasks: 8sec\n"
operator|+
literal|"Worse performing shuffle tasks: \n"
operator|+
literal|"TaskId\t\tTimetaken\n"
operator|+
literal|"task_1317928501754_0001_r_000008 8sec\n"
operator|+
literal|"The last shuffle task task_1317928501754_0001_r_000008 finished at (relative to the Job launch time): 6-Oct-2011 19:15:18 (16sec)\n"
operator|+
literal|"\n"
operator|+
literal|"Time taken by best performing reduce task task_1317928501754_0001_r_000008: 0sec\n"
operator|+
literal|"Average time taken by reduce tasks: 0sec\n"
operator|+
literal|"Worse performing reduce tasks: \n"
operator|+
literal|"TaskId\t\tTimetaken\n"
operator|+
literal|"task_1317928501754_0001_r_000008 0sec\n"
operator|+
literal|"The last reduce task task_1317928501754_0001_r_000008 finished at (relative to the Job launch time): 6-Oct-2011 19:15:18 (16sec)\n"
operator|+
literal|"=========\n"
operator|+
literal|"\n"
operator|+
literal|"FAILED MAP task list for job_1317928501754_0001\n"
operator|+
literal|"TaskId\t\tStartTime\tFinishTime\tError\tInputSplits\n"
operator|+
literal|"====================================================\n"
operator|+
literal|"task_1317928501754_0001_m_000002\t6-Oct-2011 19:15:04\t6-Oct-2011 19:15:06 (2sec)\t\t\n"
operator|+
literal|"\n"
operator|+
literal|"SUCCEEDED JOB_SETUP task list for job_1317928501754_0001\n"
operator|+
literal|"TaskId\t\tStartTime\tFinishTime\tError\n"
operator|+
literal|"====================================================\n"
operator|+
literal|"task_1317928501754_0001_s_000001\t6-Oct-2011 19:15:03\t6-Oct-2011 19:15:04 (1sec)\t\n"
operator|+
literal|"\n"
operator|+
literal|"SUCCEEDED MAP task list for job_1317928501754_0001\n"
operator|+
literal|"TaskId\t\tStartTime\tFinishTime\tError\tInputSplits\n"
operator|+
literal|"====================================================\n"
operator|+
literal|"task_1317928501754_0001_m_000007\t6-Oct-2011 19:15:09\t6-Oct-2011 19:15:16 (7sec)\t\t\n"
operator|+
literal|"\n"
operator|+
literal|"SUCCEEDED MAP task list for job_1317928501754_0001\n"
operator|+
literal|"TaskId\t\tStartTime\tFinishTime\tError\tInputSplits\n"
operator|+
literal|"====================================================\n"
operator|+
literal|"task_1317928501754_0001_m_000006\t6-Oct-2011 19:15:08\t6-Oct-2011 19:15:14 (6sec)\t\t\n"
operator|+
literal|"\n"
operator|+
literal|"SUCCEEDED MAP task list for job_1317928501754_0001\n"
operator|+
literal|"TaskId\t\tStartTime\tFinishTime\tError\tInputSplits\n"
operator|+
literal|"====================================================\n"
operator|+
literal|"task_1317928501754_0001_m_000005\t6-Oct-2011 19:15:07\t6-Oct-2011 19:15:12 (5sec)\t\t\n"
operator|+
literal|"\n"
operator|+
literal|"SUCCEEDED MAP task list for job_1317928501754_0001\n"
operator|+
literal|"TaskId\t\tStartTime\tFinishTime\tError\tInputSplits\n"
operator|+
literal|"====================================================\n"
operator|+
literal|"task_1317928501754_0001_m_000004\t6-Oct-2011 19:15:06\t6-Oct-2011 19:15:10 (4sec)\t\t\n"
operator|+
literal|"\n"
operator|+
literal|"SUCCEEDED MAP task list for job_1317928501754_0001\n"
operator|+
literal|"TaskId\t\tStartTime\tFinishTime\tError\tInputSplits\n"
operator|+
literal|"====================================================\n"
operator|+
literal|"task_1317928501754_0001_m_000003\t6-Oct-2011 19:15:05\t6-Oct-2011 19:15:08 (3sec)\t\t\n"
operator|+
literal|"\n"
operator|+
literal|"SUCCEEDED REDUCE task list for job_1317928501754_0001\n"
operator|+
literal|"TaskId\t\tStartTime\tFinishTime\tError\n"
operator|+
literal|"====================================================\n"
operator|+
literal|"task_1317928501754_0001_r_000008\t6-Oct-2011 19:15:10\t6-Oct-2011 19:15:18 (8sec)\t\n"
operator|+
literal|"\n"
operator|+
literal|"SUCCEEDED JOB_CLEANUP task list for job_1317928501754_0001\n"
operator|+
literal|"TaskId\t\tStartTime\tFinishTime\tError\n"
operator|+
literal|"====================================================\n"
operator|+
literal|"task_1317928501754_0001_c_000009\t6-Oct-2011 19:15:11\t6-Oct-2011 19:15:20 (9sec)\t\n"
operator|+
literal|"\n"
operator|+
literal|"JOB_SETUP task list for job_1317928501754_0001\n"
operator|+
literal|"TaskId\t\tStartTime\tFinishTime\tHostName\tError\tTaskLogs\n"
operator|+
literal|"====================================================\n"
operator|+
literal|"attempt_1317928501754_0001_s_000001_1\t6-Oct-2011 19:15:03\t6-Oct-2011 19:15:04 (1sec)\tlocalhost\thttp://t:1234/tasklog?attemptid=attempt_1317928501754_0001_s_000001_1\n"
operator|+
literal|"\n"
operator|+
literal|"MAP task list for job_1317928501754_0001\n"
operator|+
literal|"TaskId\t\tStartTime\tFinishTime\tHostName\tError\tTaskLogs\n"
operator|+
literal|"====================================================\n"
operator|+
literal|"attempt_1317928501754_0001_m_000007_1\t6-Oct-2011 19:15:09\t6-Oct-2011 19:15:16 (7sec)\tlocalhost\thttp://t:1234/tasklog?attemptid=attempt_1317928501754_0001_m_000007_1\n"
operator|+
literal|"attempt_1317928501754_0001_m_000002_1\t6-Oct-2011 19:15:04\t6-Oct-2011 19:15:06 (2sec)\tlocalhost\thttp://t:1234/tasklog?attemptid=attempt_1317928501754_0001_m_000002_1\n"
operator|+
literal|"attempt_1317928501754_0001_m_000006_1\t6-Oct-2011 19:15:08\t6-Oct-2011 19:15:14 (6sec)\tlocalhost\thttp://t:1234/tasklog?attemptid=attempt_1317928501754_0001_m_000006_1\n"
operator|+
literal|"attempt_1317928501754_0001_m_000005_1\t6-Oct-2011 19:15:07\t6-Oct-2011 19:15:12 (5sec)\tlocalhost\thttp://t:1234/tasklog?attemptid=attempt_1317928501754_0001_m_000005_1\n"
operator|+
literal|"attempt_1317928501754_0001_m_000004_1\t6-Oct-2011 19:15:06\t6-Oct-2011 19:15:10 (4sec)\tlocalhost\thttp://t:1234/tasklog?attemptid=attempt_1317928501754_0001_m_000004_1\n"
operator|+
literal|"attempt_1317928501754_0001_m_000003_1\t6-Oct-2011 19:15:05\t6-Oct-2011 19:15:08 (3sec)\tlocalhost\thttp://t:1234/tasklog?attemptid=attempt_1317928501754_0001_m_000003_1\n"
operator|+
literal|"\n"
operator|+
literal|"REDUCE task list for job_1317928501754_0001\n"
operator|+
literal|"TaskId\t\tStartTime\tShuffleFinished\tSortFinished\tFinishTime\tHostName\tError\tTaskLogs\n"
operator|+
literal|"====================================================\n"
operator|+
literal|"attempt_1317928501754_0001_r_000008_1\t6-Oct-2011 19:15:10\t6-Oct-2011 19:15:18 (8sec)\t6-Oct-2011 19:15:18 (0sec)6-Oct-2011 19:15:18 (8sec)\tlocalhost\thttp://t:1234/tasklog?attemptid=attempt_1317928501754_0001_r_000008_1\n"
operator|+
literal|"\n"
operator|+
literal|"JOB_CLEANUP task list for job_1317928501754_0001\n"
operator|+
literal|"TaskId\t\tStartTime\tFinishTime\tHostName\tError\tTaskLogs\n"
operator|+
literal|"====================================================\n"
operator|+
literal|"attempt_1317928501754_0001_c_000009_1\t6-Oct-2011 19:15:11\t6-Oct-2011 19:15:20 (9sec)\tlocalhost\thttp://t:1234/tasklog?attemptid=attempt_1317928501754_0001_c_000009_1\n"
operator|+
literal|"\n"
operator|+
literal|"FAILED task attempts by nodes\n"
operator|+
literal|"Hostname\tFailedTasks\n"
operator|+
literal|"===============================\n"
operator|+
literal|"localhost\ttask_1317928501754_0001_m_000002, \n"
argument_list|,
name|outStr
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testJSONPrinter ()
specifier|public
name|void
name|testJSONPrinter
parameter_list|()
throws|throws
name|Exception
block|{
name|JobHistoryParser
operator|.
name|JobInfo
name|job
init|=
name|createJobInfo
argument_list|()
decl_stmt|;
name|JSONHistoryViewerPrinter
name|printer
init|=
operator|new
name|JSONHistoryViewerPrinter
argument_list|(
name|job
argument_list|,
literal|false
argument_list|,
literal|"http://"
argument_list|)
decl_stmt|;
name|String
name|outStr
init|=
name|run
argument_list|(
name|printer
argument_list|)
decl_stmt|;
name|JSONAssert
operator|.
name|assertEquals
argument_list|(
literal|"{\n"
operator|+
literal|"    \"counters\": {\n"
operator|+
literal|"        \"group1\": [\n"
operator|+
literal|"            {\n"
operator|+
literal|"                \"counterName\": \"counter1\",\n"
operator|+
literal|"                \"mapValue\": 5,\n"
operator|+
literal|"                \"reduceValue\": 5,\n"
operator|+
literal|"                \"totalValue\": 5\n"
operator|+
literal|"            },\n"
operator|+
literal|"            {\n"
operator|+
literal|"                \"counterName\": \"counter2\",\n"
operator|+
literal|"                \"mapValue\": 10,\n"
operator|+
literal|"                \"reduceValue\": 10,\n"
operator|+
literal|"                \"totalValue\": 10\n"
operator|+
literal|"            }\n"
operator|+
literal|"        ],\n"
operator|+
literal|"        \"group2\": [\n"
operator|+
literal|"            {\n"
operator|+
literal|"                \"counterName\": \"counter1\",\n"
operator|+
literal|"                \"mapValue\": 15,\n"
operator|+
literal|"                \"reduceValue\": 15,\n"
operator|+
literal|"                \"totalValue\": 15\n"
operator|+
literal|"            }\n"
operator|+
literal|"        ]\n"
operator|+
literal|"    },\n"
operator|+
literal|"    \"finishedAt\": 1317928516754,\n"
operator|+
literal|"    \"hadoopJob\": \"job_1317928501754_0001\",\n"
operator|+
literal|"    \"jobConf\": \"/tmp/job.xml\",\n"
operator|+
literal|"    \"jobName\": \"my job\",\n"
operator|+
literal|"    \"launchedAt\": 1317928502754,\n"
operator|+
literal|"    \"status\": \"SUCCEEDED\",\n"
operator|+
literal|"    \"submittedAt\": 1317928501754,\n"
operator|+
literal|"    \"taskSummary\": {\n"
operator|+
literal|"        \"cleanup\": {\n"
operator|+
literal|"            \"failed\": 0,\n"
operator|+
literal|"            \"finishTime\": 1317928520754,\n"
operator|+
literal|"            \"killed\": 0,\n"
operator|+
literal|"            \"startTime\": 1317928511754,\n"
operator|+
literal|"            \"successful\": 1,\n"
operator|+
literal|"            \"total\": 1\n"
operator|+
literal|"        },\n"
operator|+
literal|"        \"map\": {\n"
operator|+
literal|"            \"failed\": 1,\n"
operator|+
literal|"            \"finishTime\": 1317928516754,\n"
operator|+
literal|"            \"killed\": 0,\n"
operator|+
literal|"            \"startTime\": 1317928504754,\n"
operator|+
literal|"            \"successful\": 5,\n"
operator|+
literal|"            \"total\": 6\n"
operator|+
literal|"        },\n"
operator|+
literal|"        \"reduce\": {\n"
operator|+
literal|"            \"failed\": 0,\n"
operator|+
literal|"            \"finishTime\": 1317928518754,\n"
operator|+
literal|"            \"killed\": 0,\n"
operator|+
literal|"            \"startTime\": 1317928510754,\n"
operator|+
literal|"            \"successful\": 1,\n"
operator|+
literal|"            \"total\": 1\n"
operator|+
literal|"        },\n"
operator|+
literal|"        \"setup\": {\n"
operator|+
literal|"            \"failed\": 0,\n"
operator|+
literal|"            \"finishTime\": 1317928504754,\n"
operator|+
literal|"            \"killed\": 0,\n"
operator|+
literal|"            \"startTime\": 1317928503754,\n"
operator|+
literal|"            \"successful\": 1,\n"
operator|+
literal|"            \"total\": 1\n"
operator|+
literal|"        }\n"
operator|+
literal|"    },\n"
operator|+
literal|"    \"tasks\": [\n"
operator|+
literal|"        {\n"
operator|+
literal|"            \"finishTime\": 1317928506754,\n"
operator|+
literal|"            \"inputSplits\": \"\",\n"
operator|+
literal|"            \"startTime\": 1317928504754,\n"
operator|+
literal|"            \"status\": \"FAILED\",\n"
operator|+
literal|"            \"taskId\": \"task_1317928501754_0001_m_000002\",\n"
operator|+
literal|"            \"type\": \"MAP\"\n"
operator|+
literal|"        }\n"
operator|+
literal|"    ],\n"
operator|+
literal|"    \"user\": \"rkanter\"\n"
operator|+
literal|"}\n"
argument_list|,
name|outStr
argument_list|,
name|JSONCompareMode
operator|.
name|NON_EXTENSIBLE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testJSONPrinterAll ()
specifier|public
name|void
name|testJSONPrinterAll
parameter_list|()
throws|throws
name|Exception
block|{
name|JobHistoryParser
operator|.
name|JobInfo
name|job
init|=
name|createJobInfo
argument_list|()
decl_stmt|;
name|JSONHistoryViewerPrinter
name|printer
init|=
operator|new
name|JSONHistoryViewerPrinter
argument_list|(
name|job
argument_list|,
literal|true
argument_list|,
literal|"http://"
argument_list|)
decl_stmt|;
name|String
name|outStr
init|=
name|run
argument_list|(
name|printer
argument_list|)
decl_stmt|;
name|JSONAssert
operator|.
name|assertEquals
argument_list|(
literal|"{\n"
operator|+
literal|"    \"counters\": {\n"
operator|+
literal|"        \"group1\": [\n"
operator|+
literal|"            {\n"
operator|+
literal|"                \"counterName\": \"counter1\",\n"
operator|+
literal|"                \"mapValue\": 5,\n"
operator|+
literal|"                \"reduceValue\": 5,\n"
operator|+
literal|"                \"totalValue\": 5\n"
operator|+
literal|"            },\n"
operator|+
literal|"            {\n"
operator|+
literal|"                \"counterName\": \"counter2\",\n"
operator|+
literal|"                \"mapValue\": 10,\n"
operator|+
literal|"                \"reduceValue\": 10,\n"
operator|+
literal|"                \"totalValue\": 10\n"
operator|+
literal|"            }\n"
operator|+
literal|"        ],\n"
operator|+
literal|"        \"group2\": [\n"
operator|+
literal|"            {\n"
operator|+
literal|"                \"counterName\": \"counter1\",\n"
operator|+
literal|"                \"mapValue\": 15,\n"
operator|+
literal|"                \"reduceValue\": 15,\n"
operator|+
literal|"                \"totalValue\": 15\n"
operator|+
literal|"            }\n"
operator|+
literal|"        ]\n"
operator|+
literal|"    },\n"
operator|+
literal|"    \"finishedAt\": 1317928516754,\n"
operator|+
literal|"    \"hadoopJob\": \"job_1317928501754_0001\",\n"
operator|+
literal|"    \"jobConf\": \"/tmp/job.xml\",\n"
operator|+
literal|"    \"jobName\": \"my job\",\n"
operator|+
literal|"    \"launchedAt\": 1317928502754,\n"
operator|+
literal|"    \"status\": \"SUCCEEDED\",\n"
operator|+
literal|"    \"submittedAt\": 1317928501754,\n"
operator|+
literal|"    \"taskSummary\": {\n"
operator|+
literal|"        \"cleanup\": {\n"
operator|+
literal|"            \"failed\": 0,\n"
operator|+
literal|"            \"finishTime\": 1317928520754,\n"
operator|+
literal|"            \"killed\": 0,\n"
operator|+
literal|"            \"startTime\": 1317928511754,\n"
operator|+
literal|"            \"successful\": 1,\n"
operator|+
literal|"            \"total\": 1\n"
operator|+
literal|"        },\n"
operator|+
literal|"        \"map\": {\n"
operator|+
literal|"            \"failed\": 1,\n"
operator|+
literal|"            \"finishTime\": 1317928516754,\n"
operator|+
literal|"            \"killed\": 0,\n"
operator|+
literal|"            \"startTime\": 1317928504754,\n"
operator|+
literal|"            \"successful\": 5,\n"
operator|+
literal|"            \"total\": 6\n"
operator|+
literal|"        },\n"
operator|+
literal|"        \"reduce\": {\n"
operator|+
literal|"            \"failed\": 0,\n"
operator|+
literal|"            \"finishTime\": 1317928518754,\n"
operator|+
literal|"            \"killed\": 0,\n"
operator|+
literal|"            \"startTime\": 1317928510754,\n"
operator|+
literal|"            \"successful\": 1,\n"
operator|+
literal|"            \"total\": 1\n"
operator|+
literal|"        },\n"
operator|+
literal|"        \"setup\": {\n"
operator|+
literal|"            \"failed\": 0,\n"
operator|+
literal|"            \"finishTime\": 1317928504754,\n"
operator|+
literal|"            \"killed\": 0,\n"
operator|+
literal|"            \"startTime\": 1317928503754,\n"
operator|+
literal|"            \"successful\": 1,\n"
operator|+
literal|"            \"total\": 1\n"
operator|+
literal|"        }\n"
operator|+
literal|"    },\n"
operator|+
literal|"    \"tasks\": [\n"
operator|+
literal|"        {\n"
operator|+
literal|"            \"attempts\": {\n"
operator|+
literal|"                \"attemptId\": \"attempt_1317928501754_0001_m_000002_1\",\n"
operator|+
literal|"                \"finishTime\": 1317928506754,\n"
operator|+
literal|"                \"hostName\": \"localhost\",\n"
operator|+
literal|"                \"startTime\": 1317928504754,\n"
operator|+
literal|"                \"taskLogs\": \"http://t:1234/tasklog?attemptid=attempt_1317928501754_0001_m_000002_1\"\n"
operator|+
literal|"            },\n"
operator|+
literal|"            \"counters\": {\n"
operator|+
literal|"                \"group1\": [\n"
operator|+
literal|"                    {\n"
operator|+
literal|"                        \"counterName\": \"counter1\",\n"
operator|+
literal|"                        \"value\": 5\n"
operator|+
literal|"                    },\n"
operator|+
literal|"                    {\n"
operator|+
literal|"                        \"counterName\": \"counter2\",\n"
operator|+
literal|"                        \"value\": 10\n"
operator|+
literal|"                    }\n"
operator|+
literal|"                ],\n"
operator|+
literal|"                \"group2\": [\n"
operator|+
literal|"                    {\n"
operator|+
literal|"                        \"counterName\": \"counter1\",\n"
operator|+
literal|"                        \"value\": 15\n"
operator|+
literal|"                    }\n"
operator|+
literal|"                ]\n"
operator|+
literal|"            },\n"
operator|+
literal|"            \"finishTime\": 1317928506754,\n"
operator|+
literal|"            \"inputSplits\": \"\",\n"
operator|+
literal|"            \"startTime\": 1317928504754,\n"
operator|+
literal|"            \"status\": \"FAILED\",\n"
operator|+
literal|"            \"taskId\": \"task_1317928501754_0001_m_000002\",\n"
operator|+
literal|"            \"type\": \"MAP\"\n"
operator|+
literal|"        },\n"
operator|+
literal|"        {\n"
operator|+
literal|"            \"attempts\": {\n"
operator|+
literal|"                \"attemptId\": \"attempt_1317928501754_0001_s_000001_1\",\n"
operator|+
literal|"                \"finishTime\": 1317928504754,\n"
operator|+
literal|"                \"hostName\": \"localhost\",\n"
operator|+
literal|"                \"startTime\": 1317928503754,\n"
operator|+
literal|"                \"taskLogs\": \"http://t:1234/tasklog?attemptid=attempt_1317928501754_0001_s_000001_1\"\n"
operator|+
literal|"            },\n"
operator|+
literal|"            \"counters\": {\n"
operator|+
literal|"                \"group1\": [\n"
operator|+
literal|"                    {\n"
operator|+
literal|"                        \"counterName\": \"counter1\",\n"
operator|+
literal|"                        \"value\": 5\n"
operator|+
literal|"                    },\n"
operator|+
literal|"                    {\n"
operator|+
literal|"                        \"counterName\": \"counter2\",\n"
operator|+
literal|"                        \"value\": 10\n"
operator|+
literal|"                    }\n"
operator|+
literal|"                ],\n"
operator|+
literal|"                \"group2\": [\n"
operator|+
literal|"                    {\n"
operator|+
literal|"                        \"counterName\": \"counter1\",\n"
operator|+
literal|"                        \"value\": 15\n"
operator|+
literal|"                    }\n"
operator|+
literal|"                ]\n"
operator|+
literal|"            },\n"
operator|+
literal|"            \"finishTime\": 1317928504754,\n"
operator|+
literal|"            \"startTime\": 1317928503754,\n"
operator|+
literal|"            \"status\": \"SUCCEEDED\",\n"
operator|+
literal|"            \"taskId\": \"task_1317928501754_0001_s_000001\",\n"
operator|+
literal|"            \"type\": \"JOB_SETUP\"\n"
operator|+
literal|"        },\n"
operator|+
literal|"        {\n"
operator|+
literal|"            \"attempts\": {\n"
operator|+
literal|"                \"attemptId\": \"attempt_1317928501754_0001_m_000006_1\",\n"
operator|+
literal|"                \"finishTime\": 1317928514754,\n"
operator|+
literal|"                \"hostName\": \"localhost\",\n"
operator|+
literal|"                \"startTime\": 1317928508754,\n"
operator|+
literal|"                \"taskLogs\": \"http://t:1234/tasklog?attemptid=attempt_1317928501754_0001_m_000006_1\"\n"
operator|+
literal|"            },\n"
operator|+
literal|"            \"counters\": {\n"
operator|+
literal|"                \"group1\": [\n"
operator|+
literal|"                    {\n"
operator|+
literal|"                        \"counterName\": \"counter1\",\n"
operator|+
literal|"                        \"value\": 5\n"
operator|+
literal|"                    },\n"
operator|+
literal|"                    {\n"
operator|+
literal|"                        \"counterName\": \"counter2\",\n"
operator|+
literal|"                        \"value\": 10\n"
operator|+
literal|"                    }\n"
operator|+
literal|"                ],\n"
operator|+
literal|"                \"group2\": [\n"
operator|+
literal|"                    {\n"
operator|+
literal|"                        \"counterName\": \"counter1\",\n"
operator|+
literal|"                        \"value\": 15\n"
operator|+
literal|"                    }\n"
operator|+
literal|"                ]\n"
operator|+
literal|"            },\n"
operator|+
literal|"            \"finishTime\": 1317928514754,\n"
operator|+
literal|"            \"inputSplits\": \"\",\n"
operator|+
literal|"            \"startTime\": 1317928508754,\n"
operator|+
literal|"            \"status\": \"SUCCEEDED\",\n"
operator|+
literal|"            \"taskId\": \"task_1317928501754_0001_m_000006\",\n"
operator|+
literal|"            \"type\": \"MAP\"\n"
operator|+
literal|"        },\n"
operator|+
literal|"        {\n"
operator|+
literal|"            \"attempts\": {\n"
operator|+
literal|"                \"attemptId\": \"attempt_1317928501754_0001_m_000005_1\",\n"
operator|+
literal|"                \"finishTime\": 1317928512754,\n"
operator|+
literal|"                \"hostName\": \"localhost\",\n"
operator|+
literal|"                \"startTime\": 1317928507754,\n"
operator|+
literal|"                \"taskLogs\": \"http://t:1234/tasklog?attemptid=attempt_1317928501754_0001_m_000005_1\"\n"
operator|+
literal|"            },\n"
operator|+
literal|"            \"counters\": {\n"
operator|+
literal|"                \"group1\": [\n"
operator|+
literal|"                    {\n"
operator|+
literal|"                        \"counterName\": \"counter1\",\n"
operator|+
literal|"                        \"value\": 5\n"
operator|+
literal|"                    },\n"
operator|+
literal|"                    {\n"
operator|+
literal|"                        \"counterName\": \"counter2\",\n"
operator|+
literal|"                        \"value\": 10\n"
operator|+
literal|"                    }\n"
operator|+
literal|"                ],\n"
operator|+
literal|"                \"group2\": [\n"
operator|+
literal|"                    {\n"
operator|+
literal|"                        \"counterName\": \"counter1\",\n"
operator|+
literal|"                        \"value\": 15\n"
operator|+
literal|"                    }\n"
operator|+
literal|"                ]\n"
operator|+
literal|"            },\n"
operator|+
literal|"            \"finishTime\": 1317928512754,\n"
operator|+
literal|"            \"inputSplits\": \"\",\n"
operator|+
literal|"            \"startTime\": 1317928507754,\n"
operator|+
literal|"            \"status\": \"SUCCEEDED\",\n"
operator|+
literal|"            \"taskId\": \"task_1317928501754_0001_m_000005\",\n"
operator|+
literal|"            \"type\": \"MAP\"\n"
operator|+
literal|"        },\n"
operator|+
literal|"        {\n"
operator|+
literal|"            \"attempts\": {\n"
operator|+
literal|"                \"attemptId\": \"attempt_1317928501754_0001_m_000004_1\",\n"
operator|+
literal|"                \"finishTime\": 1317928510754,\n"
operator|+
literal|"                \"hostName\": \"localhost\",\n"
operator|+
literal|"                \"startTime\": 1317928506754,\n"
operator|+
literal|"                \"taskLogs\": \"http://t:1234/tasklog?attemptid=attempt_1317928501754_0001_m_000004_1\"\n"
operator|+
literal|"            },\n"
operator|+
literal|"            \"counters\": {\n"
operator|+
literal|"                \"group1\": [\n"
operator|+
literal|"                    {\n"
operator|+
literal|"                        \"counterName\": \"counter1\",\n"
operator|+
literal|"                        \"value\": 5\n"
operator|+
literal|"                    },\n"
operator|+
literal|"                    {\n"
operator|+
literal|"                        \"counterName\": \"counter2\",\n"
operator|+
literal|"                        \"value\": 10\n"
operator|+
literal|"                    }\n"
operator|+
literal|"                ],\n"
operator|+
literal|"                \"group2\": [\n"
operator|+
literal|"                    {\n"
operator|+
literal|"                        \"counterName\": \"counter1\",\n"
operator|+
literal|"                        \"value\": 15\n"
operator|+
literal|"                    }\n"
operator|+
literal|"                ]\n"
operator|+
literal|"            },\n"
operator|+
literal|"            \"finishTime\": 1317928510754,\n"
operator|+
literal|"            \"inputSplits\": \"\",\n"
operator|+
literal|"            \"startTime\": 1317928506754,\n"
operator|+
literal|"            \"status\": \"SUCCEEDED\",\n"
operator|+
literal|"            \"taskId\": \"task_1317928501754_0001_m_000004\",\n"
operator|+
literal|"            \"type\": \"MAP\"\n"
operator|+
literal|"        },\n"
operator|+
literal|"        {\n"
operator|+
literal|"            \"attempts\": {\n"
operator|+
literal|"                \"attemptId\": \"attempt_1317928501754_0001_m_000003_1\",\n"
operator|+
literal|"                \"finishTime\": 1317928508754,\n"
operator|+
literal|"                \"hostName\": \"localhost\",\n"
operator|+
literal|"                \"startTime\": 1317928505754,\n"
operator|+
literal|"                \"taskLogs\": \"http://t:1234/tasklog?attemptid=attempt_1317928501754_0001_m_000003_1\"\n"
operator|+
literal|"            },\n"
operator|+
literal|"            \"counters\": {\n"
operator|+
literal|"                \"group1\": [\n"
operator|+
literal|"                    {\n"
operator|+
literal|"                        \"counterName\": \"counter1\",\n"
operator|+
literal|"                        \"value\": 5\n"
operator|+
literal|"                    },\n"
operator|+
literal|"                    {\n"
operator|+
literal|"                        \"counterName\": \"counter2\",\n"
operator|+
literal|"                        \"value\": 10\n"
operator|+
literal|"                    }\n"
operator|+
literal|"                ],\n"
operator|+
literal|"                \"group2\": [\n"
operator|+
literal|"                    {\n"
operator|+
literal|"                        \"counterName\": \"counter1\",\n"
operator|+
literal|"                        \"value\": 15\n"
operator|+
literal|"                    }\n"
operator|+
literal|"                ]\n"
operator|+
literal|"            },\n"
operator|+
literal|"            \"finishTime\": 1317928508754,\n"
operator|+
literal|"            \"inputSplits\": \"\",\n"
operator|+
literal|"            \"startTime\": 1317928505754,\n"
operator|+
literal|"            \"status\": \"SUCCEEDED\",\n"
operator|+
literal|"            \"taskId\": \"task_1317928501754_0001_m_000003\",\n"
operator|+
literal|"            \"type\": \"MAP\"\n"
operator|+
literal|"        },\n"
operator|+
literal|"        {\n"
operator|+
literal|"            \"attempts\": {\n"
operator|+
literal|"                \"attemptId\": \"attempt_1317928501754_0001_c_000009_1\",\n"
operator|+
literal|"                \"finishTime\": 1317928520754,\n"
operator|+
literal|"                \"hostName\": \"localhost\",\n"
operator|+
literal|"                \"startTime\": 1317928511754,\n"
operator|+
literal|"                \"taskLogs\": \"http://t:1234/tasklog?attemptid=attempt_1317928501754_0001_c_000009_1\"\n"
operator|+
literal|"            },\n"
operator|+
literal|"            \"counters\": {\n"
operator|+
literal|"                \"group1\": [\n"
operator|+
literal|"                    {\n"
operator|+
literal|"                        \"counterName\": \"counter1\",\n"
operator|+
literal|"                        \"value\": 5\n"
operator|+
literal|"                    },\n"
operator|+
literal|"                    {\n"
operator|+
literal|"                        \"counterName\": \"counter2\",\n"
operator|+
literal|"                        \"value\": 10\n"
operator|+
literal|"                    }\n"
operator|+
literal|"                ],\n"
operator|+
literal|"                \"group2\": [\n"
operator|+
literal|"                    {\n"
operator|+
literal|"                        \"counterName\": \"counter1\",\n"
operator|+
literal|"                        \"value\": 15\n"
operator|+
literal|"                    }\n"
operator|+
literal|"                ]\n"
operator|+
literal|"            },\n"
operator|+
literal|"            \"finishTime\": 1317928520754,\n"
operator|+
literal|"            \"startTime\": 1317928511754,\n"
operator|+
literal|"            \"status\": \"SUCCEEDED\",\n"
operator|+
literal|"            \"taskId\": \"task_1317928501754_0001_c_000009\",\n"
operator|+
literal|"            \"type\": \"JOB_CLEANUP\"\n"
operator|+
literal|"        },\n"
operator|+
literal|"        {\n"
operator|+
literal|"            \"attempts\": {\n"
operator|+
literal|"                \"attemptId\": \"attempt_1317928501754_0001_m_000007_1\",\n"
operator|+
literal|"                \"finishTime\": 1317928516754,\n"
operator|+
literal|"                \"hostName\": \"localhost\",\n"
operator|+
literal|"                \"startTime\": 1317928509754,\n"
operator|+
literal|"                \"taskLogs\": \"http://t:1234/tasklog?attemptid=attempt_1317928501754_0001_m_000007_1\"\n"
operator|+
literal|"            },\n"
operator|+
literal|"            \"counters\": {\n"
operator|+
literal|"                \"group1\": [\n"
operator|+
literal|"                    {\n"
operator|+
literal|"                        \"counterName\": \"counter1\",\n"
operator|+
literal|"                        \"value\": 5\n"
operator|+
literal|"                    },\n"
operator|+
literal|"                    {\n"
operator|+
literal|"                        \"counterName\": \"counter2\",\n"
operator|+
literal|"                        \"value\": 10\n"
operator|+
literal|"                    }\n"
operator|+
literal|"                ],\n"
operator|+
literal|"                \"group2\": [\n"
operator|+
literal|"                    {\n"
operator|+
literal|"                        \"counterName\": \"counter1\",\n"
operator|+
literal|"                        \"value\": 15\n"
operator|+
literal|"                    }\n"
operator|+
literal|"                ]\n"
operator|+
literal|"            },\n"
operator|+
literal|"            \"finishTime\": 1317928516754,\n"
operator|+
literal|"            \"inputSplits\": \"\",\n"
operator|+
literal|"            \"startTime\": 1317928509754,\n"
operator|+
literal|"            \"status\": \"SUCCEEDED\",\n"
operator|+
literal|"            \"taskId\": \"task_1317928501754_0001_m_000007\",\n"
operator|+
literal|"            \"type\": \"MAP\"\n"
operator|+
literal|"        },\n"
operator|+
literal|"        {\n"
operator|+
literal|"            \"attempts\": {\n"
operator|+
literal|"                \"attemptId\": \"attempt_1317928501754_0001_r_000008_1\",\n"
operator|+
literal|"                \"finishTime\": 1317928518754,\n"
operator|+
literal|"                \"hostName\": \"localhost\",\n"
operator|+
literal|"                \"shuffleFinished\": 1317928518754,\n"
operator|+
literal|"                \"sortFinished\": 1317928518754,\n"
operator|+
literal|"                \"startTime\": 1317928510754,\n"
operator|+
literal|"                \"taskLogs\": \"http://t:1234/tasklog?attemptid=attempt_1317928501754_0001_r_000008_1\"\n"
operator|+
literal|"            },\n"
operator|+
literal|"            \"counters\": {\n"
operator|+
literal|"                \"group1\": [\n"
operator|+
literal|"                    {\n"
operator|+
literal|"                        \"counterName\": \"counter1\",\n"
operator|+
literal|"                        \"value\": 5\n"
operator|+
literal|"                    },\n"
operator|+
literal|"                    {\n"
operator|+
literal|"                        \"counterName\": \"counter2\",\n"
operator|+
literal|"                        \"value\": 10\n"
operator|+
literal|"                    }\n"
operator|+
literal|"                ],\n"
operator|+
literal|"                \"group2\": [\n"
operator|+
literal|"                    {\n"
operator|+
literal|"                        \"counterName\": \"counter1\",\n"
operator|+
literal|"                        \"value\": 15\n"
operator|+
literal|"                    }\n"
operator|+
literal|"                ]\n"
operator|+
literal|"            },\n"
operator|+
literal|"            \"finishTime\": 1317928518754,\n"
operator|+
literal|"            \"startTime\": 1317928510754,\n"
operator|+
literal|"            \"status\": \"SUCCEEDED\",\n"
operator|+
literal|"            \"taskId\": \"task_1317928501754_0001_r_000008\",\n"
operator|+
literal|"            \"type\": \"REDUCE\"\n"
operator|+
literal|"        }\n"
operator|+
literal|"    ],\n"
operator|+
literal|"    \"user\": \"rkanter\"\n"
operator|+
literal|"}\n"
argument_list|,
name|outStr
argument_list|,
name|JSONCompareMode
operator|.
name|NON_EXTENSIBLE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testHumanDupePrinter ()
specifier|public
name|void
name|testHumanDupePrinter
parameter_list|()
throws|throws
name|Exception
block|{
name|JobHistoryParser
operator|.
name|JobInfo
name|job
init|=
name|createJobInfo2
argument_list|()
decl_stmt|;
comment|// Counters are only part of the overview so printAll can be false or true
comment|// this does not affect the test, task counters are not printed
name|HumanReadableHistoryViewerPrinter
name|printer
init|=
operator|new
name|HumanReadableHistoryViewerPrinter
argument_list|(
name|job
argument_list|,
literal|false
argument_list|,
literal|"http://"
argument_list|,
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
literal|"GMT"
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|outStr
init|=
name|run
argument_list|(
name|printer
argument_list|)
decl_stmt|;
comment|// We are not interested in anything but the duplicate counter
name|int
name|count1
init|=
name|outStr
operator|.
name|indexOf
argument_list|(
literal|"|Map-Reduce Framework          |Map input records             |"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotEquals
argument_list|(
literal|"First counter occurrence not found"
argument_list|,
operator|-
literal|1
argument_list|,
name|count1
argument_list|)
expr_stmt|;
name|int
name|count2
init|=
name|outStr
operator|.
name|indexOf
argument_list|(
literal|"|Map-Reduce Framework          |Map input records             |"
argument_list|,
name|count1
operator|+
literal|1
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Duplicate counter found at: "
operator|+
name|count1
operator|+
literal|" and "
operator|+
name|count2
argument_list|,
operator|-
literal|1
argument_list|,
name|count2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testJSONDupePrinter ()
specifier|public
name|void
name|testJSONDupePrinter
parameter_list|()
throws|throws
name|Exception
block|{
name|JobHistoryParser
operator|.
name|JobInfo
name|job
init|=
name|createJobInfo2
argument_list|()
decl_stmt|;
comment|// Counters are part of the overview and task info
comment|// Tasks only have bogus counters in the test if that is changed printAll
comment|// must then be kept as false for this test to pass
name|JSONHistoryViewerPrinter
name|printer
init|=
operator|new
name|JSONHistoryViewerPrinter
argument_list|(
name|job
argument_list|,
literal|false
argument_list|,
literal|"http://"
argument_list|)
decl_stmt|;
name|String
name|outStr
init|=
name|run
argument_list|(
name|printer
argument_list|)
decl_stmt|;
comment|// We are not interested in anything but the duplicate counter
name|int
name|count1
init|=
name|outStr
operator|.
name|indexOf
argument_list|(
literal|"\"counterName\":\"MAP_INPUT_RECORDS\""
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotEquals
argument_list|(
literal|"First counter occurrence not found"
argument_list|,
operator|-
literal|1
argument_list|,
name|count1
argument_list|)
expr_stmt|;
name|int
name|count2
init|=
name|outStr
operator|.
name|indexOf
argument_list|(
literal|"\"counterName\":\"MAP_INPUT_RECORDS\""
argument_list|,
name|count1
operator|+
literal|1
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Duplicate counter found at: "
operator|+
name|count1
operator|+
literal|" and "
operator|+
name|count2
argument_list|,
operator|-
literal|1
argument_list|,
name|count2
argument_list|)
expr_stmt|;
block|}
DECL|method|run (HistoryViewerPrinter printer)
specifier|private
name|String
name|run
parameter_list|(
name|HistoryViewerPrinter
name|printer
parameter_list|)
throws|throws
name|Exception
block|{
name|ByteArrayOutputStream
name|boas
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|PrintStream
name|out
init|=
operator|new
name|PrintStream
argument_list|(
name|boas
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|printer
operator|.
name|print
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|String
name|outStr
init|=
name|boas
operator|.
name|toString
argument_list|(
literal|"UTF-8"
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"out = "
operator|+
name|outStr
argument_list|)
expr_stmt|;
return|return
name|outStr
return|;
block|}
DECL|method|createJobInfo ()
specifier|private
specifier|static
name|JobHistoryParser
operator|.
name|JobInfo
name|createJobInfo
parameter_list|()
block|{
name|JobHistoryParser
operator|.
name|JobInfo
name|job
init|=
operator|new
name|JobHistoryParser
operator|.
name|JobInfo
argument_list|()
decl_stmt|;
name|job
operator|.
name|submitTime
operator|=
literal|1317928501754L
expr_stmt|;
name|job
operator|.
name|finishTime
operator|=
name|job
operator|.
name|submitTime
operator|+
literal|15000
expr_stmt|;
name|job
operator|.
name|jobid
operator|=
name|JobID
operator|.
name|forName
argument_list|(
literal|"job_1317928501754_0001"
argument_list|)
expr_stmt|;
name|job
operator|.
name|username
operator|=
literal|"rkanter"
expr_stmt|;
name|job
operator|.
name|jobname
operator|=
literal|"my job"
expr_stmt|;
name|job
operator|.
name|jobQueueName
operator|=
literal|"my queue"
expr_stmt|;
name|job
operator|.
name|jobConfPath
operator|=
literal|"/tmp/job.xml"
expr_stmt|;
name|job
operator|.
name|launchTime
operator|=
name|job
operator|.
name|submitTime
operator|+
literal|1000
expr_stmt|;
name|job
operator|.
name|totalMaps
operator|=
literal|5
expr_stmt|;
name|job
operator|.
name|totalReduces
operator|=
literal|1
expr_stmt|;
name|job
operator|.
name|failedMaps
operator|=
literal|1
expr_stmt|;
name|job
operator|.
name|failedReduces
operator|=
literal|0
expr_stmt|;
name|job
operator|.
name|succeededMaps
operator|=
literal|5
expr_stmt|;
name|job
operator|.
name|succeededReduces
operator|=
literal|1
expr_stmt|;
name|job
operator|.
name|jobStatus
operator|=
name|JobStatus
operator|.
name|State
operator|.
name|SUCCEEDED
operator|.
name|name
argument_list|()
expr_stmt|;
name|job
operator|.
name|totalCounters
operator|=
name|createCounters
argument_list|()
expr_stmt|;
name|job
operator|.
name|mapCounters
operator|=
name|createCounters
argument_list|()
expr_stmt|;
name|job
operator|.
name|reduceCounters
operator|=
name|createCounters
argument_list|()
expr_stmt|;
name|job
operator|.
name|tasksMap
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|addTaskInfo
argument_list|(
name|job
argument_list|,
name|TaskType
operator|.
name|JOB_SETUP
argument_list|,
literal|1
argument_list|,
name|TaskStatus
operator|.
name|State
operator|.
name|SUCCEEDED
argument_list|)
expr_stmt|;
name|addTaskInfo
argument_list|(
name|job
argument_list|,
name|TaskType
operator|.
name|MAP
argument_list|,
literal|2
argument_list|,
name|TaskStatus
operator|.
name|State
operator|.
name|FAILED
argument_list|)
expr_stmt|;
name|addTaskInfo
argument_list|(
name|job
argument_list|,
name|TaskType
operator|.
name|MAP
argument_list|,
literal|3
argument_list|,
name|TaskStatus
operator|.
name|State
operator|.
name|SUCCEEDED
argument_list|)
expr_stmt|;
name|addTaskInfo
argument_list|(
name|job
argument_list|,
name|TaskType
operator|.
name|MAP
argument_list|,
literal|4
argument_list|,
name|TaskStatus
operator|.
name|State
operator|.
name|SUCCEEDED
argument_list|)
expr_stmt|;
name|addTaskInfo
argument_list|(
name|job
argument_list|,
name|TaskType
operator|.
name|MAP
argument_list|,
literal|5
argument_list|,
name|TaskStatus
operator|.
name|State
operator|.
name|SUCCEEDED
argument_list|)
expr_stmt|;
name|addTaskInfo
argument_list|(
name|job
argument_list|,
name|TaskType
operator|.
name|MAP
argument_list|,
literal|6
argument_list|,
name|TaskStatus
operator|.
name|State
operator|.
name|SUCCEEDED
argument_list|)
expr_stmt|;
name|addTaskInfo
argument_list|(
name|job
argument_list|,
name|TaskType
operator|.
name|MAP
argument_list|,
literal|7
argument_list|,
name|TaskStatus
operator|.
name|State
operator|.
name|SUCCEEDED
argument_list|)
expr_stmt|;
name|addTaskInfo
argument_list|(
name|job
argument_list|,
name|TaskType
operator|.
name|REDUCE
argument_list|,
literal|8
argument_list|,
name|TaskStatus
operator|.
name|State
operator|.
name|SUCCEEDED
argument_list|)
expr_stmt|;
name|addTaskInfo
argument_list|(
name|job
argument_list|,
name|TaskType
operator|.
name|JOB_CLEANUP
argument_list|,
literal|9
argument_list|,
name|TaskStatus
operator|.
name|State
operator|.
name|SUCCEEDED
argument_list|)
expr_stmt|;
return|return
name|job
return|;
block|}
DECL|method|createJobInfo2 ()
specifier|private
specifier|static
name|JobHistoryParser
operator|.
name|JobInfo
name|createJobInfo2
parameter_list|()
block|{
name|JobHistoryParser
operator|.
name|JobInfo
name|job
init|=
operator|new
name|JobHistoryParser
operator|.
name|JobInfo
argument_list|()
decl_stmt|;
name|job
operator|.
name|submitTime
operator|=
literal|1317928501754L
expr_stmt|;
name|job
operator|.
name|finishTime
operator|=
name|job
operator|.
name|submitTime
operator|+
literal|15000
expr_stmt|;
name|job
operator|.
name|jobid
operator|=
name|JobID
operator|.
name|forName
argument_list|(
literal|"job_1317928501754_0001"
argument_list|)
expr_stmt|;
name|job
operator|.
name|username
operator|=
literal|"test"
expr_stmt|;
name|job
operator|.
name|jobname
operator|=
literal|"Dupe counter output"
expr_stmt|;
name|job
operator|.
name|jobQueueName
operator|=
literal|"root.test"
expr_stmt|;
name|job
operator|.
name|jobConfPath
operator|=
literal|"/tmp/job.xml"
expr_stmt|;
name|job
operator|.
name|launchTime
operator|=
name|job
operator|.
name|submitTime
operator|+
literal|1000
expr_stmt|;
name|job
operator|.
name|totalMaps
operator|=
literal|1
expr_stmt|;
name|job
operator|.
name|totalReduces
operator|=
literal|0
expr_stmt|;
name|job
operator|.
name|failedMaps
operator|=
literal|0
expr_stmt|;
name|job
operator|.
name|failedReduces
operator|=
literal|0
expr_stmt|;
name|job
operator|.
name|succeededMaps
operator|=
literal|1
expr_stmt|;
name|job
operator|.
name|succeededReduces
operator|=
literal|0
expr_stmt|;
name|job
operator|.
name|jobStatus
operator|=
name|JobStatus
operator|.
name|State
operator|.
name|SUCCEEDED
operator|.
name|name
argument_list|()
expr_stmt|;
name|job
operator|.
name|totalCounters
operator|=
name|createDeprecatedCounters
argument_list|()
expr_stmt|;
name|job
operator|.
name|mapCounters
operator|=
name|createDeprecatedCounters
argument_list|()
expr_stmt|;
name|job
operator|.
name|reduceCounters
operator|=
name|createDeprecatedCounters
argument_list|()
expr_stmt|;
name|job
operator|.
name|tasksMap
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|addTaskInfo
argument_list|(
name|job
argument_list|,
name|TaskType
operator|.
name|JOB_SETUP
argument_list|,
literal|1
argument_list|,
name|TaskStatus
operator|.
name|State
operator|.
name|SUCCEEDED
argument_list|)
expr_stmt|;
name|addTaskInfo
argument_list|(
name|job
argument_list|,
name|TaskType
operator|.
name|MAP
argument_list|,
literal|2
argument_list|,
name|TaskStatus
operator|.
name|State
operator|.
name|SUCCEEDED
argument_list|)
expr_stmt|;
name|addTaskInfo
argument_list|(
name|job
argument_list|,
name|TaskType
operator|.
name|JOB_CLEANUP
argument_list|,
literal|3
argument_list|,
name|TaskStatus
operator|.
name|State
operator|.
name|SUCCEEDED
argument_list|)
expr_stmt|;
return|return
name|job
return|;
block|}
DECL|method|createCounters ()
specifier|private
specifier|static
name|Counters
name|createCounters
parameter_list|()
block|{
name|Counters
name|counters
init|=
operator|new
name|Counters
argument_list|()
decl_stmt|;
name|counters
operator|.
name|findCounter
argument_list|(
literal|"group1"
argument_list|,
literal|"counter1"
argument_list|)
operator|.
name|setValue
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|counters
operator|.
name|findCounter
argument_list|(
literal|"group1"
argument_list|,
literal|"counter2"
argument_list|)
operator|.
name|setValue
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|counters
operator|.
name|findCounter
argument_list|(
literal|"group2"
argument_list|,
literal|"counter1"
argument_list|)
operator|.
name|setValue
argument_list|(
literal|15
argument_list|)
expr_stmt|;
return|return
name|counters
return|;
block|}
DECL|method|createDeprecatedCounters ()
specifier|private
specifier|static
name|Counters
name|createDeprecatedCounters
parameter_list|()
block|{
name|Counters
name|counters
init|=
operator|new
name|Counters
argument_list|()
decl_stmt|;
comment|// Deprecated counter: make sure it is only printed once
name|counters
operator|.
name|findCounter
argument_list|(
literal|"org.apache.hadoop.mapred.Task$Counter"
argument_list|,
literal|"MAP_INPUT_RECORDS"
argument_list|)
operator|.
name|setValue
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|counters
operator|.
name|findCounter
argument_list|(
literal|"File System Counters"
argument_list|,
literal|"FILE: Number of bytes read"
argument_list|)
operator|.
name|setValue
argument_list|(
literal|1
argument_list|)
expr_stmt|;
return|return
name|counters
return|;
block|}
DECL|method|addTaskInfo (JobHistoryParser.JobInfo job, TaskType type, int id, TaskStatus.State status)
specifier|private
specifier|static
name|void
name|addTaskInfo
parameter_list|(
name|JobHistoryParser
operator|.
name|JobInfo
name|job
parameter_list|,
name|TaskType
name|type
parameter_list|,
name|int
name|id
parameter_list|,
name|TaskStatus
operator|.
name|State
name|status
parameter_list|)
block|{
name|JobHistoryParser
operator|.
name|TaskInfo
name|task
init|=
operator|new
name|JobHistoryParser
operator|.
name|TaskInfo
argument_list|()
decl_stmt|;
name|task
operator|.
name|taskId
operator|=
operator|new
name|TaskID
argument_list|(
name|job
operator|.
name|getJobId
argument_list|()
argument_list|,
name|type
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|task
operator|.
name|startTime
operator|=
name|job
operator|.
name|getLaunchTime
argument_list|()
operator|+
name|id
operator|*
literal|1000
expr_stmt|;
name|task
operator|.
name|finishTime
operator|=
name|task
operator|.
name|startTime
operator|+
name|id
operator|*
literal|1000
expr_stmt|;
name|task
operator|.
name|taskType
operator|=
name|type
expr_stmt|;
name|task
operator|.
name|counters
operator|=
name|createCounters
argument_list|()
expr_stmt|;
name|task
operator|.
name|status
operator|=
name|status
operator|.
name|name
argument_list|()
expr_stmt|;
name|task
operator|.
name|attemptsMap
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|addTaskAttemptInfo
argument_list|(
name|task
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|job
operator|.
name|tasksMap
operator|.
name|put
argument_list|(
name|task
operator|.
name|getTaskId
argument_list|()
argument_list|,
name|task
argument_list|)
expr_stmt|;
block|}
DECL|method|addTaskAttemptInfo ( JobHistoryParser.TaskInfo task, int id)
specifier|private
specifier|static
name|void
name|addTaskAttemptInfo
parameter_list|(
name|JobHistoryParser
operator|.
name|TaskInfo
name|task
parameter_list|,
name|int
name|id
parameter_list|)
block|{
name|JobHistoryParser
operator|.
name|TaskAttemptInfo
name|attempt
init|=
operator|new
name|JobHistoryParser
operator|.
name|TaskAttemptInfo
argument_list|()
decl_stmt|;
name|attempt
operator|.
name|attemptId
operator|=
operator|new
name|TaskAttemptID
argument_list|(
name|TaskID
operator|.
name|downgrade
argument_list|(
name|task
operator|.
name|getTaskId
argument_list|()
argument_list|)
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|attempt
operator|.
name|startTime
operator|=
name|task
operator|.
name|getStartTime
argument_list|()
expr_stmt|;
name|attempt
operator|.
name|finishTime
operator|=
name|task
operator|.
name|getFinishTime
argument_list|()
expr_stmt|;
name|attempt
operator|.
name|shuffleFinishTime
operator|=
name|task
operator|.
name|getFinishTime
argument_list|()
expr_stmt|;
name|attempt
operator|.
name|sortFinishTime
operator|=
name|task
operator|.
name|getFinishTime
argument_list|()
expr_stmt|;
name|attempt
operator|.
name|mapFinishTime
operator|=
name|task
operator|.
name|getFinishTime
argument_list|()
expr_stmt|;
name|attempt
operator|.
name|status
operator|=
name|task
operator|.
name|getTaskStatus
argument_list|()
expr_stmt|;
name|attempt
operator|.
name|taskType
operator|=
name|task
operator|.
name|getTaskType
argument_list|()
expr_stmt|;
name|attempt
operator|.
name|trackerName
operator|=
literal|"localhost"
expr_stmt|;
name|attempt
operator|.
name|httpPort
operator|=
literal|1234
expr_stmt|;
name|attempt
operator|.
name|hostname
operator|=
literal|"localhost"
expr_stmt|;
name|task
operator|.
name|attemptsMap
operator|.
name|put
argument_list|(
name|attempt
operator|.
name|getAttemptId
argument_list|()
argument_list|,
name|attempt
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

