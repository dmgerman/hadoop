begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.submarine.client.cli.yarnservice
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
name|yarnservice
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
name|service
operator|.
name|api
operator|.
name|records
operator|.
name|Component
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
name|service
operator|.
name|api
operator|.
name|records
operator|.
name|Service
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
name|service
operator|.
name|client
operator|.
name|ServiceClient
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
name|api
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
name|StorageKeyConstants
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
name|submarine
operator|.
name|runtimes
operator|.
name|yarnservice
operator|.
name|YarnServiceJobSubmitter
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
name|yarnservice
operator|.
name|YarnServiceUtils
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
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Matchers
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
DECL|class|TestYarnServiceRunJobCli
specifier|public
class|class
name|TestYarnServiceRunJobCli
block|{
annotation|@
name|Before
DECL|method|before ()
specifier|public
name|void
name|before
parameter_list|()
throws|throws
name|IOException
throws|,
name|YarnException
block|{
name|SubmarineLogs
operator|.
name|verboseOff
argument_list|()
expr_stmt|;
name|ServiceClient
name|serviceClient
init|=
name|mock
argument_list|(
name|ServiceClient
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|serviceClient
operator|.
name|actionCreate
argument_list|(
name|any
argument_list|(
name|Service
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
name|YarnServiceUtils
operator|.
name|setStubServiceClient
argument_list|(
name|serviceClient
argument_list|)
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
name|YarnServiceCliTestUtils
operator|.
name|getMockClientContext
argument_list|()
decl_stmt|;
name|RunJobCli
name|runJobCli
init|=
operator|new
name|RunJobCli
argument_list|(
name|mockClientContext
argument_list|)
decl_stmt|;
name|runJobCli
operator|.
name|printUsages
argument_list|()
expr_stmt|;
block|}
DECL|method|getServiceSpecFromJobSubmitter (JobSubmitter jobSubmitter)
specifier|private
name|Service
name|getServiceSpecFromJobSubmitter
parameter_list|(
name|JobSubmitter
name|jobSubmitter
parameter_list|)
block|{
return|return
operator|(
operator|(
name|YarnServiceJobSubmitter
operator|)
name|jobSubmitter
operator|)
operator|.
name|getServiceSpec
argument_list|()
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
name|MockClientContext
name|mockClientContext
init|=
name|YarnServiceCliTestUtils
operator|.
name|getMockClientContext
argument_list|()
decl_stmt|;
name|RunJobCli
name|runJobCli
init|=
operator|new
name|RunJobCli
argument_list|(
name|mockClientContext
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
literal|"s3://input"
block|,
literal|"--checkpoint_path"
block|,
literal|"s3://output"
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
literal|"memory=4096M,vcores=4"
block|,
literal|"--tensorboard"
block|,
literal|"true"
block|,
literal|"--ps_docker_image"
block|,
literal|"ps.image"
block|,
literal|"--worker_docker_image"
block|,
literal|"worker.image"
block|,
literal|"--ps_launch_cmd"
block|,
literal|"python run-ps.py"
block|,
literal|"--verbose"
block|}
argument_list|)
expr_stmt|;
name|Service
name|serviceSpec
init|=
name|getServiceSpecFromJobSubmitter
argument_list|(
name|runJobCli
operator|.
name|getJobSubmitter
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|serviceSpec
operator|.
name|getComponents
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|serviceSpec
operator|.
name|getComponent
argument_list|(
name|TaskType
operator|.
name|WORKER
operator|.
name|getComponentName
argument_list|()
argument_list|)
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|serviceSpec
operator|.
name|getComponent
argument_list|(
name|TaskType
operator|.
name|PRIMARY_WORKER
operator|.
name|getComponentName
argument_list|()
argument_list|)
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|serviceSpec
operator|.
name|getComponent
argument_list|(
name|TaskType
operator|.
name|PS
operator|.
name|getComponentName
argument_list|()
argument_list|)
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|Component
name|primaryWorkerComp
init|=
name|serviceSpec
operator|.
name|getComponent
argument_list|(
name|TaskType
operator|.
name|PRIMARY_WORKER
operator|.
name|getComponentName
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2048
argument_list|,
name|primaryWorkerComp
operator|.
name|getResource
argument_list|()
operator|.
name|calcMemoryMB
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|primaryWorkerComp
operator|.
name|getResource
argument_list|()
operator|.
name|getCpus
argument_list|()
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
name|Component
name|workerComp
init|=
name|serviceSpec
operator|.
name|getComponent
argument_list|(
name|TaskType
operator|.
name|WORKER
operator|.
name|getComponentName
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2048
argument_list|,
name|workerComp
operator|.
name|getResource
argument_list|()
operator|.
name|calcMemoryMB
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|workerComp
operator|.
name|getResource
argument_list|()
operator|.
name|getCpus
argument_list|()
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
name|Component
name|psComp
init|=
name|serviceSpec
operator|.
name|getComponent
argument_list|(
name|TaskType
operator|.
name|PS
operator|.
name|getComponentName
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|4096
argument_list|,
name|psComp
operator|.
name|getResource
argument_list|()
operator|.
name|calcMemoryMB
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|psComp
operator|.
name|getResource
argument_list|()
operator|.
name|getCpus
argument_list|()
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"worker.image"
argument_list|,
name|workerComp
operator|.
name|getArtifact
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"ps.image"
argument_list|,
name|psComp
operator|.
name|getArtifact
argument_list|()
operator|.
name|getId
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
comment|// TODO, ADD TEST TO USE SERVICE CLIENT TO VALIDATE THE JSON SPEC
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
name|MockClientContext
name|mockClientContext
init|=
name|YarnServiceCliTestUtils
operator|.
name|getMockClientContext
argument_list|()
decl_stmt|;
name|RunJobCli
name|runJobCli
init|=
operator|new
name|RunJobCli
argument_list|(
name|mockClientContext
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
literal|"s3://input"
block|,
literal|"--checkpoint_path"
block|,
literal|"s3://output"
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
literal|"memory=2G,vcores=2"
block|,
literal|"--tensorboard"
block|,
literal|"true"
block|,
literal|"--verbose"
block|}
argument_list|)
expr_stmt|;
name|Service
name|serviceSpec
init|=
name|getServiceSpecFromJobSubmitter
argument_list|(
name|runJobCli
operator|.
name|getJobSubmitter
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|serviceSpec
operator|.
name|getComponents
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|serviceSpec
operator|.
name|getComponent
argument_list|(
name|TaskType
operator|.
name|PRIMARY_WORKER
operator|.
name|getComponentName
argument_list|()
argument_list|)
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|Component
name|primaryWorkerComp
init|=
name|serviceSpec
operator|.
name|getComponent
argument_list|(
name|TaskType
operator|.
name|PRIMARY_WORKER
operator|.
name|getComponentName
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2048
argument_list|,
name|primaryWorkerComp
operator|.
name|getResource
argument_list|()
operator|.
name|calcMemoryMB
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|primaryWorkerComp
operator|.
name|getResource
argument_list|()
operator|.
name|getCpus
argument_list|()
operator|.
name|intValue
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
comment|// TODO, ADD TEST TO USE SERVICE CLIENT TO VALIDATE THE JSON SPEC
block|}
annotation|@
name|Test
DECL|method|testParameterStorageForTrainingJob ()
specifier|public
name|void
name|testParameterStorageForTrainingJob
parameter_list|()
throws|throws
name|Exception
block|{
name|MockClientContext
name|mockClientContext
init|=
name|YarnServiceCliTestUtils
operator|.
name|getMockClientContext
argument_list|()
decl_stmt|;
name|RunJobCli
name|runJobCli
init|=
operator|new
name|RunJobCli
argument_list|(
name|mockClientContext
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
literal|"s3://input"
block|,
literal|"--checkpoint_path"
block|,
literal|"s3://output"
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
literal|"memory=2G,vcores=2"
block|,
literal|"--tensorboard"
block|,
literal|"true"
block|,
literal|"--verbose"
block|}
argument_list|)
expr_stmt|;
name|SubmarineStorage
name|storage
init|=
name|mockClientContext
operator|.
name|getRuntimeFactory
argument_list|()
operator|.
name|getSubmarineStorage
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|jobInfo
init|=
name|storage
operator|.
name|getJobInfoByName
argument_list|(
literal|"my-job"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|jobInfo
operator|.
name|size
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|jobInfo
operator|.
name|get
argument_list|(
name|StorageKeyConstants
operator|.
name|INPUT_PATH
argument_list|)
argument_list|,
literal|"s3://input"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

