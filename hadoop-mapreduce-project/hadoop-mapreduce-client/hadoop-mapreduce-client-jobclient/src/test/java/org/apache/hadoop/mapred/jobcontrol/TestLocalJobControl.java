begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred.jobcontrol
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|jobcontrol
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
name|ArrayList
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
name|mapred
operator|.
name|HadoopTestCase
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
name|LoggerFactory
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
name|assertEquals
import|;
end_import

begin_comment
comment|/**  * HadoopTestCase that tests the local job runner.  */
end_comment

begin_class
DECL|class|TestLocalJobControl
specifier|public
class|class
name|TestLocalJobControl
extends|extends
name|HadoopTestCase
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestLocalJobControl
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Initialises a new instance of this test case to use a Local MR cluster and    * a local filesystem.    *     * @throws IOException If an error occurs initialising this object.    */
DECL|method|TestLocalJobControl ()
specifier|public
name|TestLocalJobControl
parameter_list|()
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|HadoopTestCase
operator|.
name|LOCAL_MR
argument_list|,
name|HadoopTestCase
operator|.
name|LOCAL_FS
argument_list|,
literal|2
argument_list|,
literal|2
argument_list|)
expr_stmt|;
block|}
comment|/**    * This is a main function for testing JobControl class. It first cleans all    * the dirs it will use. Then it generates some random text data in    * TestJobControlData/indir. Then it creates 4 jobs: Job 1: copy data from    * indir to outdir_1 Job 2: copy data from indir to outdir_2 Job 3: copy data    * from outdir_1 and outdir_2 to outdir_3 Job 4: copy data from outdir to    * outdir_4 The jobs 1 and 2 have no dependency. The job 3 depends on jobs 1    * and 2. The job 4 depends on job 3.    *     * Then it creates a JobControl object and add the 4 jobs to the JobControl    * object. Finally, it creates a thread to run the JobControl object and    * monitors/reports the job states.    */
annotation|@
name|Test
DECL|method|testLocalJobControlDataCopy ()
specifier|public
name|void
name|testLocalJobControlDataCopy
parameter_list|()
throws|throws
name|Exception
block|{
name|FileSystem
name|fs
init|=
name|FileSystem
operator|.
name|get
argument_list|(
name|createJobConf
argument_list|()
argument_list|)
decl_stmt|;
name|Path
name|rootDataDir
init|=
operator|new
name|Path
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.data"
argument_list|,
literal|"."
argument_list|)
argument_list|,
literal|"TestLocalJobControlData"
argument_list|)
decl_stmt|;
name|Path
name|indir
init|=
operator|new
name|Path
argument_list|(
name|rootDataDir
argument_list|,
literal|"indir"
argument_list|)
decl_stmt|;
name|Path
name|outdir_1
init|=
operator|new
name|Path
argument_list|(
name|rootDataDir
argument_list|,
literal|"outdir_1"
argument_list|)
decl_stmt|;
name|Path
name|outdir_2
init|=
operator|new
name|Path
argument_list|(
name|rootDataDir
argument_list|,
literal|"outdir_2"
argument_list|)
decl_stmt|;
name|Path
name|outdir_3
init|=
operator|new
name|Path
argument_list|(
name|rootDataDir
argument_list|,
literal|"outdir_3"
argument_list|)
decl_stmt|;
name|Path
name|outdir_4
init|=
operator|new
name|Path
argument_list|(
name|rootDataDir
argument_list|,
literal|"outdir_4"
argument_list|)
decl_stmt|;
name|JobControlTestUtils
operator|.
name|cleanData
argument_list|(
name|fs
argument_list|,
name|indir
argument_list|)
expr_stmt|;
name|JobControlTestUtils
operator|.
name|generateData
argument_list|(
name|fs
argument_list|,
name|indir
argument_list|)
expr_stmt|;
name|JobControlTestUtils
operator|.
name|cleanData
argument_list|(
name|fs
argument_list|,
name|outdir_1
argument_list|)
expr_stmt|;
name|JobControlTestUtils
operator|.
name|cleanData
argument_list|(
name|fs
argument_list|,
name|outdir_2
argument_list|)
expr_stmt|;
name|JobControlTestUtils
operator|.
name|cleanData
argument_list|(
name|fs
argument_list|,
name|outdir_3
argument_list|)
expr_stmt|;
name|JobControlTestUtils
operator|.
name|cleanData
argument_list|(
name|fs
argument_list|,
name|outdir_4
argument_list|)
expr_stmt|;
name|ArrayList
argument_list|<
name|Job
argument_list|>
name|dependingJobs
init|=
literal|null
decl_stmt|;
name|ArrayList
argument_list|<
name|Path
argument_list|>
name|inPaths_1
init|=
operator|new
name|ArrayList
argument_list|<
name|Path
argument_list|>
argument_list|()
decl_stmt|;
name|inPaths_1
operator|.
name|add
argument_list|(
name|indir
argument_list|)
expr_stmt|;
name|JobConf
name|jobConf_1
init|=
name|JobControlTestUtils
operator|.
name|createCopyJob
argument_list|(
name|inPaths_1
argument_list|,
name|outdir_1
argument_list|)
decl_stmt|;
name|Job
name|job_1
init|=
operator|new
name|Job
argument_list|(
name|jobConf_1
argument_list|,
name|dependingJobs
argument_list|)
decl_stmt|;
name|ArrayList
argument_list|<
name|Path
argument_list|>
name|inPaths_2
init|=
operator|new
name|ArrayList
argument_list|<
name|Path
argument_list|>
argument_list|()
decl_stmt|;
name|inPaths_2
operator|.
name|add
argument_list|(
name|indir
argument_list|)
expr_stmt|;
name|JobConf
name|jobConf_2
init|=
name|JobControlTestUtils
operator|.
name|createCopyJob
argument_list|(
name|inPaths_2
argument_list|,
name|outdir_2
argument_list|)
decl_stmt|;
name|Job
name|job_2
init|=
operator|new
name|Job
argument_list|(
name|jobConf_2
argument_list|,
name|dependingJobs
argument_list|)
decl_stmt|;
name|ArrayList
argument_list|<
name|Path
argument_list|>
name|inPaths_3
init|=
operator|new
name|ArrayList
argument_list|<
name|Path
argument_list|>
argument_list|()
decl_stmt|;
name|inPaths_3
operator|.
name|add
argument_list|(
name|outdir_1
argument_list|)
expr_stmt|;
name|inPaths_3
operator|.
name|add
argument_list|(
name|outdir_2
argument_list|)
expr_stmt|;
name|JobConf
name|jobConf_3
init|=
name|JobControlTestUtils
operator|.
name|createCopyJob
argument_list|(
name|inPaths_3
argument_list|,
name|outdir_3
argument_list|)
decl_stmt|;
name|dependingJobs
operator|=
operator|new
name|ArrayList
argument_list|<
name|Job
argument_list|>
argument_list|()
expr_stmt|;
name|dependingJobs
operator|.
name|add
argument_list|(
name|job_1
argument_list|)
expr_stmt|;
name|dependingJobs
operator|.
name|add
argument_list|(
name|job_2
argument_list|)
expr_stmt|;
name|Job
name|job_3
init|=
operator|new
name|Job
argument_list|(
name|jobConf_3
argument_list|,
name|dependingJobs
argument_list|)
decl_stmt|;
name|ArrayList
argument_list|<
name|Path
argument_list|>
name|inPaths_4
init|=
operator|new
name|ArrayList
argument_list|<
name|Path
argument_list|>
argument_list|()
decl_stmt|;
name|inPaths_4
operator|.
name|add
argument_list|(
name|outdir_3
argument_list|)
expr_stmt|;
name|JobConf
name|jobConf_4
init|=
name|JobControlTestUtils
operator|.
name|createCopyJob
argument_list|(
name|inPaths_4
argument_list|,
name|outdir_4
argument_list|)
decl_stmt|;
name|dependingJobs
operator|=
operator|new
name|ArrayList
argument_list|<
name|Job
argument_list|>
argument_list|()
expr_stmt|;
name|dependingJobs
operator|.
name|add
argument_list|(
name|job_3
argument_list|)
expr_stmt|;
name|Job
name|job_4
init|=
operator|new
name|Job
argument_list|(
name|jobConf_4
argument_list|,
name|dependingJobs
argument_list|)
decl_stmt|;
name|JobControl
name|theControl
init|=
operator|new
name|JobControl
argument_list|(
literal|"Test"
argument_list|)
decl_stmt|;
name|theControl
operator|.
name|addJob
argument_list|(
name|job_1
argument_list|)
expr_stmt|;
name|theControl
operator|.
name|addJob
argument_list|(
name|job_2
argument_list|)
expr_stmt|;
name|theControl
operator|.
name|addJob
argument_list|(
name|job_3
argument_list|)
expr_stmt|;
name|theControl
operator|.
name|addJob
argument_list|(
name|job_4
argument_list|)
expr_stmt|;
name|Thread
name|theController
init|=
operator|new
name|Thread
argument_list|(
name|theControl
argument_list|)
decl_stmt|;
name|theController
operator|.
name|start
argument_list|()
expr_stmt|;
while|while
condition|(
operator|!
name|theControl
operator|.
name|allFinished
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Jobs in waiting state: "
operator|+
name|theControl
operator|.
name|getWaitingJobs
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Jobs in ready state: "
operator|+
name|theControl
operator|.
name|getReadyJobs
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Jobs in running state: "
operator|+
name|theControl
operator|.
name|getRunningJobs
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Jobs in success state: "
operator|+
name|theControl
operator|.
name|getSuccessfulJobs
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Jobs in failed state: "
operator|+
name|theControl
operator|.
name|getFailedJobs
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{        }
block|}
name|assertEquals
argument_list|(
literal|"Some jobs failed"
argument_list|,
literal|0
argument_list|,
name|theControl
operator|.
name|getFailedJobs
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|theControl
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

