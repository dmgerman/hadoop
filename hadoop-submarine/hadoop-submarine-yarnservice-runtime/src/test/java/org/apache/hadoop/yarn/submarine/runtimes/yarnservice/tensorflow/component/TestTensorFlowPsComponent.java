begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *     http://www.apache.org/licenses/LICENSE-2.0  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.submarine.runtimes.yarnservice.tensorflow.component
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
name|runtimes
operator|.
name|yarnservice
operator|.
name|tensorflow
operator|.
name|component
package|;
end_package

begin_import
import|import static
name|junit
operator|.
name|framework
operator|.
name|TestCase
operator|.
name|assertTrue
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
name|ArgumentMatchers
operator|.
name|eq
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
name|verify
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
name|yarn
operator|.
name|service
operator|.
name|api
operator|.
name|records
operator|.
name|Artifact
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
name|Component
operator|.
name|RestartPolicyEnum
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
name|api
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

begin_comment
comment|/**  * This class is to test {@link TensorFlowPsComponent}.  */
end_comment

begin_class
DECL|class|TestTensorFlowPsComponent
specifier|public
class|class
name|TestTensorFlowPsComponent
block|{
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
DECL|field|testCommons
specifier|private
name|ComponentTestCommons
name|testCommons
init|=
operator|new
name|ComponentTestCommons
argument_list|(
name|TaskType
operator|.
name|PS
argument_list|)
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|IOException
block|{
name|testCommons
operator|.
name|setup
argument_list|()
expr_stmt|;
block|}
DECL|method|createPsComponent (RunJobParameters parameters)
specifier|private
name|TensorFlowPsComponent
name|createPsComponent
parameter_list|(
name|RunJobParameters
name|parameters
parameter_list|)
block|{
return|return
operator|new
name|TensorFlowPsComponent
argument_list|(
name|testCommons
operator|.
name|fsOperations
argument_list|,
name|testCommons
operator|.
name|mockClientContext
operator|.
name|getRemoteDirectoryManager
argument_list|()
argument_list|,
name|testCommons
operator|.
name|mockLaunchCommandFactory
argument_list|,
name|parameters
argument_list|,
name|testCommons
operator|.
name|yarnConfig
argument_list|)
return|;
block|}
DECL|method|verifyCommons (Component component)
specifier|private
name|void
name|verifyCommons
parameter_list|(
name|Component
name|component
parameter_list|)
throws|throws
name|IOException
block|{
name|assertEquals
argument_list|(
name|testCommons
operator|.
name|taskType
operator|.
name|getComponentName
argument_list|()
argument_list|,
name|component
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|testCommons
operator|.
name|verifyCommonConfigEnvs
argument_list|(
name|component
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|component
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getProperties
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|RestartPolicyEnum
operator|.
name|NEVER
argument_list|,
name|component
operator|.
name|getRestartPolicy
argument_list|()
argument_list|)
expr_stmt|;
name|testCommons
operator|.
name|verifyResources
argument_list|(
name|component
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|Artifact
argument_list|()
operator|.
name|type
argument_list|(
name|Artifact
operator|.
name|TypeEnum
operator|.
name|DOCKER
argument_list|)
operator|.
name|id
argument_list|(
literal|"testPSDockerImage"
argument_list|)
argument_list|,
name|component
operator|.
name|getArtifact
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|taskTypeUppercase
init|=
name|testCommons
operator|.
name|taskType
operator|.
name|name
argument_list|()
operator|.
name|toUpperCase
argument_list|()
decl_stmt|;
name|String
name|expectedScriptName
init|=
name|String
operator|.
name|format
argument_list|(
literal|"run-%s.sh"
argument_list|,
name|taskTypeUppercase
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"./%s"
argument_list|,
name|expectedScriptName
argument_list|)
argument_list|,
name|component
operator|.
name|getLaunchCommand
argument_list|()
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|testCommons
operator|.
name|fsOperations
argument_list|)
operator|.
name|uploadToRemoteFileAndLocalizeToContainerWorkDir
argument_list|(
name|any
argument_list|(
name|Path
operator|.
name|class
argument_list|)
argument_list|,
name|eq
argument_list|(
literal|"mockScript"
argument_list|)
argument_list|,
name|eq
argument_list|(
name|expectedScriptName
argument_list|)
argument_list|,
name|eq
argument_list|(
name|component
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPSComponentWithNullResource ()
specifier|public
name|void
name|testPSComponentWithNullResource
parameter_list|()
throws|throws
name|IOException
block|{
name|RunJobParameters
name|parameters
init|=
operator|new
name|RunJobParameters
argument_list|()
decl_stmt|;
name|parameters
operator|.
name|setPsResource
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|TensorFlowPsComponent
name|psComponent
init|=
name|createPsComponent
argument_list|(
name|parameters
argument_list|)
decl_stmt|;
name|expectedException
operator|.
name|expect
argument_list|(
name|NullPointerException
operator|.
name|class
argument_list|)
expr_stmt|;
name|expectedException
operator|.
name|expectMessage
argument_list|(
literal|"PS resource must not be null"
argument_list|)
expr_stmt|;
name|psComponent
operator|.
name|createComponent
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPSComponentWithNullJobName ()
specifier|public
name|void
name|testPSComponentWithNullJobName
parameter_list|()
throws|throws
name|IOException
block|{
name|RunJobParameters
name|parameters
init|=
operator|new
name|RunJobParameters
argument_list|()
decl_stmt|;
name|parameters
operator|.
name|setPsResource
argument_list|(
name|testCommons
operator|.
name|resource
argument_list|)
expr_stmt|;
name|parameters
operator|.
name|setNumPS
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|parameters
operator|.
name|setName
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|TensorFlowPsComponent
name|psComponent
init|=
name|createPsComponent
argument_list|(
name|parameters
argument_list|)
decl_stmt|;
name|expectedException
operator|.
name|expect
argument_list|(
name|NullPointerException
operator|.
name|class
argument_list|)
expr_stmt|;
name|expectedException
operator|.
name|expectMessage
argument_list|(
literal|"Job name must not be null"
argument_list|)
expr_stmt|;
name|psComponent
operator|.
name|createComponent
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPSComponentZeroNumberOfPS ()
specifier|public
name|void
name|testPSComponentZeroNumberOfPS
parameter_list|()
throws|throws
name|IOException
block|{
name|testCommons
operator|.
name|yarnConfig
operator|.
name|set
argument_list|(
literal|"hadoop.registry.dns.domain-name"
argument_list|,
literal|"testDomain"
argument_list|)
expr_stmt|;
name|RunJobParameters
name|parameters
init|=
operator|new
name|RunJobParameters
argument_list|()
decl_stmt|;
name|parameters
operator|.
name|setPsResource
argument_list|(
name|testCommons
operator|.
name|resource
argument_list|)
expr_stmt|;
name|parameters
operator|.
name|setName
argument_list|(
literal|"testJobName"
argument_list|)
expr_stmt|;
name|parameters
operator|.
name|setPsDockerImage
argument_list|(
literal|"testPSDockerImage"
argument_list|)
expr_stmt|;
name|parameters
operator|.
name|setNumPS
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|TensorFlowPsComponent
name|psComponent
init|=
name|createPsComponent
argument_list|(
name|parameters
argument_list|)
decl_stmt|;
name|expectedException
operator|.
name|expect
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|)
expr_stmt|;
name|expectedException
operator|.
name|expectMessage
argument_list|(
literal|"Number of PS should be at least 1!"
argument_list|)
expr_stmt|;
name|psComponent
operator|.
name|createComponent
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPSComponentNumPSIsOne ()
specifier|public
name|void
name|testPSComponentNumPSIsOne
parameter_list|()
throws|throws
name|IOException
block|{
name|testCommons
operator|.
name|yarnConfig
operator|.
name|set
argument_list|(
literal|"hadoop.registry.dns.domain-name"
argument_list|,
literal|"testDomain"
argument_list|)
expr_stmt|;
name|RunJobParameters
name|parameters
init|=
operator|new
name|RunJobParameters
argument_list|()
decl_stmt|;
name|parameters
operator|.
name|setPsResource
argument_list|(
name|testCommons
operator|.
name|resource
argument_list|)
expr_stmt|;
name|parameters
operator|.
name|setName
argument_list|(
literal|"testJobName"
argument_list|)
expr_stmt|;
name|parameters
operator|.
name|setNumPS
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|parameters
operator|.
name|setPsDockerImage
argument_list|(
literal|"testPSDockerImage"
argument_list|)
expr_stmt|;
name|TensorFlowPsComponent
name|psComponent
init|=
name|createPsComponent
argument_list|(
name|parameters
argument_list|)
decl_stmt|;
name|Component
name|component
init|=
name|psComponent
operator|.
name|createComponent
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1L
argument_list|,
operator|(
name|long
operator|)
name|component
operator|.
name|getNumberOfContainers
argument_list|()
argument_list|)
expr_stmt|;
name|verifyCommons
argument_list|(
name|component
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPSComponentNumPSIsTwo ()
specifier|public
name|void
name|testPSComponentNumPSIsTwo
parameter_list|()
throws|throws
name|IOException
block|{
name|testCommons
operator|.
name|yarnConfig
operator|.
name|set
argument_list|(
literal|"hadoop.registry.dns.domain-name"
argument_list|,
literal|"testDomain"
argument_list|)
expr_stmt|;
name|RunJobParameters
name|parameters
init|=
operator|new
name|RunJobParameters
argument_list|()
decl_stmt|;
name|parameters
operator|.
name|setPsResource
argument_list|(
name|testCommons
operator|.
name|resource
argument_list|)
expr_stmt|;
name|parameters
operator|.
name|setName
argument_list|(
literal|"testJobName"
argument_list|)
expr_stmt|;
name|parameters
operator|.
name|setNumPS
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|parameters
operator|.
name|setPsDockerImage
argument_list|(
literal|"testPSDockerImage"
argument_list|)
expr_stmt|;
name|TensorFlowPsComponent
name|psComponent
init|=
name|createPsComponent
argument_list|(
name|parameters
argument_list|)
decl_stmt|;
name|Component
name|component
init|=
name|psComponent
operator|.
name|createComponent
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|2L
argument_list|,
operator|(
name|long
operator|)
name|component
operator|.
name|getNumberOfContainers
argument_list|()
argument_list|)
expr_stmt|;
name|verifyCommons
argument_list|(
name|component
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

