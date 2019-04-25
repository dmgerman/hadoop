begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.submarine.client.cli
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|submarine
operator|.
name|client
operator|.
name|cli
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
name|cli
operator|.
name|ParseException
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ApplicationId
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
name|yarn
operator|.
name|exceptions
operator|.
name|YarnException
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
name|yarn
operator|.
name|submarine
operator|.
name|client
operator|.
name|cli
operator|.
name|param
operator|.
name|RunJobParameters
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
name|yarn
operator|.
name|submarine
operator|.
name|common
operator|.
name|MockClientContext
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
name|yarn
operator|.
name|submarine
operator|.
name|common
operator|.
name|conf
operator|.
name|SubmarineLogs
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
name|yarn
operator|.
name|submarine
operator|.
name|runtimes
operator|.
name|RuntimeFactory
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
name|yarn
operator|.
name|submarine
operator|.
name|runtimes
operator|.
name|common
operator|.
name|JobMonitor
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
name|yarn
operator|.
name|submarine
operator|.
name|runtimes
operator|.
name|common
operator|.
name|JobSubmitter
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
name|yarn
operator|.
name|submarine
operator|.
name|runtimes
operator|.
name|common
operator|.
name|SubmarineStorage
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
name|yarn
operator|.
name|util
operator|.
name|resource
operator|.
name|Resources
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
name|Before
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

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|ArgumentMatchers
operator|.
name|any
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|when
import|;
end_import

begin_class
DECL|class|TestRunJobCliParsing
specifier|public
class|class
name|TestRunJobCliParsing
block|{
annotation|@
name|Before
DECL|method|before ()
specifier|public
name|void
name|before
parameter_list|()
block|{
name|SubmarineLogs
operator|.
name|verboseOff
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPrintHelp ()
specifier|public
name|void
name|testPrintHelp
parameter_list|()
block|{
name|MockClientContext
name|mockClientContext
init|=
operator|new
name|MockClientContext
argument_list|()
decl_stmt|;
name|JobSubmitter
name|mockJobSubmitter
init|=
name|mock
argument_list|(
name|JobSubmitter
operator|.
name|class
argument_list|)
decl_stmt|;
name|JobMonitor
name|mockJobMonitor
init|=
name|mock
argument_list|(
name|JobMonitor
operator|.
name|class
argument_list|)
decl_stmt|;
name|RunJobCli
name|runJobCli
init|=
operator|new
name|RunJobCli
argument_list|(
name|mockClientContext
argument_list|,
name|mockJobSubmitter
argument_list|,
name|mockJobMonitor
argument_list|)
decl_stmt|;
name|runJobCli
operator|.
name|printUsages
argument_list|()
expr_stmt|;
block|}
DECL|method|getMockClientContext ()
specifier|static
name|MockClientContext
name|getMockClientContext
parameter_list|()
throws|throws
name|IOException
throws|,
name|YarnException
block|{
name|MockClientContext
name|mockClientContext
init|=
operator|new
name|MockClientContext
argument_list|()
decl_stmt|;
name|JobSubmitter
name|mockJobSubmitter
init|=
name|mock
argument_list|(
name|JobSubmitter
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|mockJobSubmitter
operator|.
name|submitJob
argument_list|(
name|any
argument_list|(
name|RunJobParameters
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|ApplicationId
operator|.
name|newInstance
argument_list|(
literal|1234L
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|JobMonitor
name|mockJobMonitor
init|=
name|mock
argument_list|(
name|JobMonitor
operator|.
name|class
argument_list|)
decl_stmt|;
name|SubmarineStorage
name|storage
init|=
name|mock
argument_list|(
name|SubmarineStorage
operator|.
name|class
argument_list|)
decl_stmt|;
name|RuntimeFactory
name|rtFactory
init|=
name|mock
argument_list|(
name|RuntimeFactory
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|rtFactory
operator|.
name|getJobSubmitterInstance
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|mockJobSubmitter
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|rtFactory
operator|.
name|getJobMonitorInstance
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|mockJobMonitor
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|rtFactory
operator|.
name|getSubmarineStorage
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|storage
argument_list|)
expr_stmt|;
name|mockClientContext
operator|.
name|setRuntimeFactory
argument_list|(
name|rtFactory
argument_list|)
expr_stmt|;
return|return
name|mockClientContext
return|;
block|}
annotation|@
name|Test
DECL|method|testBasicRunJobForDistributedTraining ()
specifier|public
name|void
name|testBasicRunJobForDistributedTraining
parameter_list|()
throws|throws
name|Exception
block|{
name|RunJobCli
name|runJobCli
init|=
operator|new
name|RunJobCli
argument_list|(
name|getMockClientContext
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|SubmarineLogs
operator|.
name|isVerbose
argument_list|()
argument_list|)
expr_stmt|;
name|runJobCli
operator|.
name|run
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"--name"
block|,
literal|"my-job"
block|,
literal|"--docker_image"
block|,
literal|"tf-docker:1.1.0"
block|,
literal|"--input_path"
block|,
literal|"hdfs://input"
block|,
literal|"--checkpoint_path"
block|,
literal|"hdfs://output"
block|,
literal|"--num_workers"
block|,
literal|"3"
block|,
literal|"--num_ps"
block|,
literal|"2"
block|,
literal|"--worker_launch_cmd"
block|,
literal|"python run-job.py"
block|,
literal|"--worker_resources"
block|,
literal|"memory=2048M,vcores=2"
block|,
literal|"--ps_resources"
block|,
literal|"memory=4G,vcores=4"
block|,
literal|"--tensorboard"
block|,
literal|"true"
block|,
literal|"--ps_launch_cmd"
block|,
literal|"python run-ps.py"
block|,
literal|"--keytab"
block|,
literal|"/keytab/path"
block|,
literal|"--principal"
block|,
literal|"user/_HOST@domain.com"
block|,
literal|"--distribute_keytab"
block|,
literal|"--verbose"
block|}
argument_list|)
expr_stmt|;
name|RunJobParameters
name|jobRunParameters
init|=
name|runJobCli
operator|.
name|getRunJobParameters
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|jobRunParameters
operator|.
name|getInputPath
argument_list|()
argument_list|,
literal|"hdfs://input"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|jobRunParameters
operator|.
name|getCheckpointPath
argument_list|()
argument_list|,
literal|"hdfs://output"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|jobRunParameters
operator|.
name|getNumPS
argument_list|()
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|jobRunParameters
operator|.
name|getPSLaunchCmd
argument_list|()
argument_list|,
literal|"python run-ps.py"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Resources
operator|.
name|createResource
argument_list|(
literal|4096
argument_list|,
literal|4
argument_list|)
argument_list|,
name|jobRunParameters
operator|.
name|getPsResource
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|jobRunParameters
operator|.
name|getWorkerLaunchCmd
argument_list|()
argument_list|,
literal|"python run-job.py"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Resources
operator|.
name|createResource
argument_list|(
literal|2048
argument_list|,
literal|2
argument_list|)
argument_list|,
name|jobRunParameters
operator|.
name|getWorkerResource
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|jobRunParameters
operator|.
name|getDockerImageName
argument_list|()
argument_list|,
literal|"tf-docker:1.1.0"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|jobRunParameters
operator|.
name|getKeytab
argument_list|()
argument_list|,
literal|"/keytab/path"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|jobRunParameters
operator|.
name|getPrincipal
argument_list|()
argument_list|,
literal|"user/_HOST@domain.com"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|jobRunParameters
operator|.
name|isDistributeKeytab
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|SubmarineLogs
operator|.
name|isVerbose
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBasicRunJobForSingleNodeTraining ()
specifier|public
name|void
name|testBasicRunJobForSingleNodeTraining
parameter_list|()
throws|throws
name|Exception
block|{
name|RunJobCli
name|runJobCli
init|=
operator|new
name|RunJobCli
argument_list|(
name|getMockClientContext
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|SubmarineLogs
operator|.
name|isVerbose
argument_list|()
argument_list|)
expr_stmt|;
name|runJobCli
operator|.
name|run
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"--name"
block|,
literal|"my-job"
block|,
literal|"--docker_image"
block|,
literal|"tf-docker:1.1.0"
block|,
literal|"--input_path"
block|,
literal|"hdfs://input"
block|,
literal|"--checkpoint_path"
block|,
literal|"hdfs://output"
block|,
literal|"--num_workers"
block|,
literal|"1"
block|,
literal|"--worker_launch_cmd"
block|,
literal|"python run-job.py"
block|,
literal|"--worker_resources"
block|,
literal|"memory=4g,vcores=2"
block|,
literal|"--tensorboard"
block|,
literal|"true"
block|,
literal|"--verbose"
block|,
literal|"--wait_job_finish"
block|}
argument_list|)
expr_stmt|;
name|RunJobParameters
name|jobRunParameters
init|=
name|runJobCli
operator|.
name|getRunJobParameters
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|jobRunParameters
operator|.
name|getInputPath
argument_list|()
argument_list|,
literal|"hdfs://input"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|jobRunParameters
operator|.
name|getCheckpointPath
argument_list|()
argument_list|,
literal|"hdfs://output"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|jobRunParameters
operator|.
name|getNumWorkers
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|jobRunParameters
operator|.
name|getWorkerLaunchCmd
argument_list|()
argument_list|,
literal|"python run-job.py"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Resources
operator|.
name|createResource
argument_list|(
literal|4096
argument_list|,
literal|2
argument_list|)
argument_list|,
name|jobRunParameters
operator|.
name|getWorkerResource
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|SubmarineLogs
operator|.
name|isVerbose
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|jobRunParameters
operator|.
name|isWaitJobFinish
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNoInputPathOptionSpecified ()
specifier|public
name|void
name|testNoInputPathOptionSpecified
parameter_list|()
throws|throws
name|Exception
block|{
name|RunJobCli
name|runJobCli
init|=
operator|new
name|RunJobCli
argument_list|(
name|getMockClientContext
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|expectedErrorMessage
init|=
literal|"\"--"
operator|+
name|CliConstants
operator|.
name|INPUT_PATH
operator|+
literal|"\" is absent"
decl_stmt|;
name|String
name|actualMessage
init|=
literal|""
decl_stmt|;
try|try
block|{
name|runJobCli
operator|.
name|run
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"--name"
block|,
literal|"my-job"
block|,
literal|"--docker_image"
block|,
literal|"tf-docker:1.1.0"
block|,
literal|"--checkpoint_path"
block|,
literal|"hdfs://output"
block|,
literal|"--num_workers"
block|,
literal|"1"
block|,
literal|"--worker_launch_cmd"
block|,
literal|"python run-job.py"
block|,
literal|"--worker_resources"
block|,
literal|"memory=4g,vcores=2"
block|,
literal|"--tensorboard"
block|,
literal|"true"
block|,
literal|"--verbose"
block|,
literal|"--wait_job_finish"
block|}
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
name|actualMessage
operator|=
name|e
operator|.
name|getMessage
argument_list|()
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|expectedErrorMessage
argument_list|,
name|actualMessage
argument_list|)
expr_stmt|;
block|}
comment|/**    * when only run tensorboard, input_path is not needed    * */
annotation|@
name|Test
DECL|method|testNoInputPathOptionButOnlyRunTensorboard ()
specifier|public
name|void
name|testNoInputPathOptionButOnlyRunTensorboard
parameter_list|()
throws|throws
name|Exception
block|{
name|RunJobCli
name|runJobCli
init|=
operator|new
name|RunJobCli
argument_list|(
name|getMockClientContext
argument_list|()
argument_list|)
decl_stmt|;
name|boolean
name|success
init|=
literal|true
decl_stmt|;
try|try
block|{
name|runJobCli
operator|.
name|run
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"--name"
block|,
literal|"my-job"
block|,
literal|"--docker_image"
block|,
literal|"tf-docker:1.1.0"
block|,
literal|"--num_workers"
block|,
literal|"0"
block|,
literal|"--tensorboard"
block|,
literal|"--verbose"
block|,
literal|"--tensorboard_resources"
block|,
literal|"memory=2G,vcores=2"
block|,
literal|"--tensorboard_docker_image"
block|,
literal|"tb_docker_image:001"
block|}
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
name|success
operator|=
literal|false
expr_stmt|;
block|}
name|Assert
operator|.
name|assertTrue
argument_list|(
name|success
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testJobWithoutName ()
specifier|public
name|void
name|testJobWithoutName
parameter_list|()
throws|throws
name|Exception
block|{
name|RunJobCli
name|runJobCli
init|=
operator|new
name|RunJobCli
argument_list|(
name|getMockClientContext
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|expectedErrorMessage
init|=
literal|"--"
operator|+
name|CliConstants
operator|.
name|NAME
operator|+
literal|" is absent"
decl_stmt|;
name|String
name|actualMessage
init|=
literal|""
decl_stmt|;
try|try
block|{
name|runJobCli
operator|.
name|run
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"--docker_image"
block|,
literal|"tf-docker:1.1.0"
block|,
literal|"--num_workers"
block|,
literal|"0"
block|,
literal|"--tensorboard"
block|,
literal|"--verbose"
block|,
literal|"--tensorboard_resources"
block|,
literal|"memory=2G,vcores=2"
block|,
literal|"--tensorboard_docker_image"
block|,
literal|"tb_docker_image:001"
block|}
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
name|actualMessage
operator|=
name|e
operator|.
name|getMessage
argument_list|()
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|expectedErrorMessage
argument_list|,
name|actualMessage
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testLaunchCommandPatternReplace ()
specifier|public
name|void
name|testLaunchCommandPatternReplace
parameter_list|()
throws|throws
name|Exception
block|{
name|RunJobCli
name|runJobCli
init|=
operator|new
name|RunJobCli
argument_list|(
name|getMockClientContext
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|SubmarineLogs
operator|.
name|isVerbose
argument_list|()
argument_list|)
expr_stmt|;
name|runJobCli
operator|.
name|run
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"--name"
block|,
literal|"my-job"
block|,
literal|"--docker_image"
block|,
literal|"tf-docker:1.1.0"
block|,
literal|"--input_path"
block|,
literal|"hdfs://input"
block|,
literal|"--checkpoint_path"
block|,
literal|"hdfs://output"
block|,
literal|"--num_workers"
block|,
literal|"3"
block|,
literal|"--num_ps"
block|,
literal|"2"
block|,
literal|"--worker_launch_cmd"
block|,
literal|"python run-job.py --input=%input_path% "
operator|+
literal|"--model_dir=%checkpoint_path% "
operator|+
literal|"--export_dir=%saved_model_path%/savedmodel"
block|,
literal|"--worker_resources"
block|,
literal|"memory=2048,vcores=2"
block|,
literal|"--ps_resources"
block|,
literal|"memory=4096,vcores=4"
block|,
literal|"--tensorboard"
block|,
literal|"true"
block|,
literal|"--ps_launch_cmd"
block|,
literal|"python run-ps.py --input=%input_path% "
operator|+
literal|"--model_dir=%checkpoint_path%/model"
block|,
literal|"--verbose"
block|}
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"python run-job.py --input=hdfs://input --model_dir=hdfs://output "
operator|+
literal|"--export_dir=hdfs://output/savedmodel"
argument_list|,
name|runJobCli
operator|.
name|getRunJobParameters
argument_list|()
operator|.
name|getWorkerLaunchCmd
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"python run-ps.py --input=hdfs://input --model_dir=hdfs://output/model"
argument_list|,
name|runJobCli
operator|.
name|getRunJobParameters
argument_list|()
operator|.
name|getPSLaunchCmd
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

