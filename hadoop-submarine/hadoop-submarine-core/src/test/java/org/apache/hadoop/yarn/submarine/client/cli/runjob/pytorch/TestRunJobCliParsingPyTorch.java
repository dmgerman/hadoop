begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *     http://www.apache.org/licenses/LICENSE-2.0  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.submarine.client.cli.runjob.pytorch
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
operator|.
name|runjob
operator|.
name|pytorch
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
name|submarine
operator|.
name|client
operator|.
name|cli
operator|.
name|param
operator|.
name|runjob
operator|.
name|PyTorchRunJobParameters
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
name|runjob
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
name|client
operator|.
name|cli
operator|.
name|runjob
operator|.
name|RunJobCli
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
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
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
name|junit
operator|.
name|rules
operator|.
name|ExpectedException
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
name|yarn
operator|.
name|submarine
operator|.
name|client
operator|.
name|cli
operator|.
name|runjob
operator|.
name|TestRunJobCliParsingCommon
operator|.
name|getMockClientContext
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
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
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
name|assertTrue
import|;
end_import

begin_comment
comment|/**  * Test class that verifies the correctness of PyTorch  * CLI configuration parsing.  */
end_comment

begin_class
DECL|class|TestRunJobCliParsingPyTorch
specifier|public
class|class
name|TestRunJobCliParsingPyTorch
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
name|Rule
DECL|field|expectedException
specifier|public
name|ExpectedException
name|expectedException
init|=
name|ExpectedException
operator|.
name|none
argument_list|()
decl_stmt|;
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
literal|"--framework"
block|,
literal|"pytorch"
block|,
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
name|assertTrue
argument_list|(
name|RunJobParameters
operator|.
name|class
operator|+
literal|" must be an instance of "
operator|+
name|PyTorchRunJobParameters
operator|.
name|class
argument_list|,
name|jobRunParameters
operator|instanceof
name|PyTorchRunJobParameters
argument_list|)
expr_stmt|;
name|PyTorchRunJobParameters
name|pyTorchParams
init|=
operator|(
name|PyTorchRunJobParameters
operator|)
name|jobRunParameters
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
name|pyTorchParams
operator|.
name|getNumWorkers
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|pyTorchParams
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
name|pyTorchParams
operator|.
name|getWorkerResource
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|SubmarineLogs
operator|.
name|isVerbose
argument_list|()
argument_list|)
expr_stmt|;
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
DECL|method|testNumPSCannotBeDefined ()
specifier|public
name|void
name|testNumPSCannotBeDefined
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
name|assertFalse
argument_list|(
name|SubmarineLogs
operator|.
name|isVerbose
argument_list|()
argument_list|)
expr_stmt|;
name|expectedException
operator|.
name|expect
argument_list|(
name|ParseException
operator|.
name|class
argument_list|)
expr_stmt|;
name|expectedException
operator|.
name|expectMessage
argument_list|(
literal|"cannot be defined for PyTorch jobs"
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
literal|"--framework"
block|,
literal|"pytorch"
block|,
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
literal|"--worker_launch_cmd"
block|,
literal|"python run-job.py"
block|,
literal|"--worker_resources"
block|,
literal|"memory=2048M,vcores=2"
block|,
literal|"--num_ps"
block|,
literal|"2"
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPSResourcesCannotBeDefined ()
specifier|public
name|void
name|testPSResourcesCannotBeDefined
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
name|assertFalse
argument_list|(
name|SubmarineLogs
operator|.
name|isVerbose
argument_list|()
argument_list|)
expr_stmt|;
name|expectedException
operator|.
name|expect
argument_list|(
name|ParseException
operator|.
name|class
argument_list|)
expr_stmt|;
name|expectedException
operator|.
name|expectMessage
argument_list|(
literal|"cannot be defined for PyTorch jobs"
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
literal|"--framework"
block|,
literal|"pytorch"
block|,
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
literal|"memory=2048M,vcores=2"
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPSDockerImageCannotBeDefined ()
specifier|public
name|void
name|testPSDockerImageCannotBeDefined
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
name|assertFalse
argument_list|(
name|SubmarineLogs
operator|.
name|isVerbose
argument_list|()
argument_list|)
expr_stmt|;
name|expectedException
operator|.
name|expect
argument_list|(
name|ParseException
operator|.
name|class
argument_list|)
expr_stmt|;
name|expectedException
operator|.
name|expectMessage
argument_list|(
literal|"cannot be defined for PyTorch jobs"
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
literal|"--framework"
block|,
literal|"pytorch"
block|,
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
literal|"--worker_launch_cmd"
block|,
literal|"python run-job.py"
block|,
literal|"--worker_resources"
block|,
literal|"memory=2048M,vcores=2"
block|,
literal|"--ps_docker_image"
block|,
literal|"psDockerImage"
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPSLaunchCommandCannotBeDefined ()
specifier|public
name|void
name|testPSLaunchCommandCannotBeDefined
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
name|assertFalse
argument_list|(
name|SubmarineLogs
operator|.
name|isVerbose
argument_list|()
argument_list|)
expr_stmt|;
name|expectedException
operator|.
name|expect
argument_list|(
name|ParseException
operator|.
name|class
argument_list|)
expr_stmt|;
name|expectedException
operator|.
name|expectMessage
argument_list|(
literal|"cannot be defined for PyTorch jobs"
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
literal|"--framework"
block|,
literal|"pytorch"
block|,
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
literal|"--worker_launch_cmd"
block|,
literal|"python run-job.py"
block|,
literal|"--worker_resources"
block|,
literal|"memory=2048M,vcores=2"
block|,
literal|"--ps_launch_cmd"
block|,
literal|"psLaunchCommand"
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testTensorboardCannotBeDefined ()
specifier|public
name|void
name|testTensorboardCannotBeDefined
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
name|assertFalse
argument_list|(
name|SubmarineLogs
operator|.
name|isVerbose
argument_list|()
argument_list|)
expr_stmt|;
name|expectedException
operator|.
name|expect
argument_list|(
name|ParseException
operator|.
name|class
argument_list|)
expr_stmt|;
name|expectedException
operator|.
name|expectMessage
argument_list|(
literal|"cannot be defined for PyTorch jobs"
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
literal|"--framework"
block|,
literal|"pytorch"
block|,
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
literal|"--worker_launch_cmd"
block|,
literal|"python run-job.py"
block|,
literal|"--worker_resources"
block|,
literal|"memory=2048M,vcores=2"
block|,
literal|"--tensorboard"
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testTensorboardResourcesCannotBeDefined ()
specifier|public
name|void
name|testTensorboardResourcesCannotBeDefined
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
name|assertFalse
argument_list|(
name|SubmarineLogs
operator|.
name|isVerbose
argument_list|()
argument_list|)
expr_stmt|;
name|expectedException
operator|.
name|expect
argument_list|(
name|ParseException
operator|.
name|class
argument_list|)
expr_stmt|;
name|expectedException
operator|.
name|expectMessage
argument_list|(
literal|"cannot be defined for PyTorch jobs"
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
literal|"--framework"
block|,
literal|"pytorch"
block|,
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
literal|"--worker_launch_cmd"
block|,
literal|"python run-job.py"
block|,
literal|"--worker_resources"
block|,
literal|"memory=2048M,vcores=2"
block|,
literal|"--tensorboard_resources"
block|,
literal|"memory=2048M,vcores=2"
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testTensorboardDockerImageCannotBeDefined ()
specifier|public
name|void
name|testTensorboardDockerImageCannotBeDefined
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
name|assertFalse
argument_list|(
name|SubmarineLogs
operator|.
name|isVerbose
argument_list|()
argument_list|)
expr_stmt|;
name|expectedException
operator|.
name|expect
argument_list|(
name|ParseException
operator|.
name|class
argument_list|)
expr_stmt|;
name|expectedException
operator|.
name|expectMessage
argument_list|(
literal|"cannot be defined for PyTorch jobs"
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
literal|"--framework"
block|,
literal|"pytorch"
block|,
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
literal|"--worker_launch_cmd"
block|,
literal|"python run-job.py"
block|,
literal|"--worker_resources"
block|,
literal|"memory=2048M,vcores=2"
block|,
literal|"--tensorboard_docker_image"
block|,
literal|"TBDockerImage"
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

