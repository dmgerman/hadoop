begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *     http://www.apache.org/licenses/LICENSE-2.0  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.submarine.client.cli.runjob
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
name|YamlConfigTestUtils
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
name|param
operator|.
name|runjob
operator|.
name|TensorFlowRunJobParameters
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
name|yaml
operator|.
name|YamlParseException
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
name|common
operator|.
name|exception
operator|.
name|SubmarineRuntimeException
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
name|resource
operator|.
name|ResourceUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
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
name|BeforeClass
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
name|File
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
comment|/**  * This class contains some test methods to test common YAML parsing  * functionality (including TF / PyTorch) of the run job Submarine command.  */
end_comment

begin_class
DECL|class|TestRunJobCliParsingCommonYaml
specifier|public
class|class
name|TestRunJobCliParsingCommonYaml
block|{
DECL|field|DIR_NAME
specifier|private
specifier|static
specifier|final
name|String
name|DIR_NAME
init|=
literal|"runjob-common-yaml"
decl_stmt|;
DECL|field|TF_DIR
specifier|private
specifier|static
specifier|final
name|String
name|TF_DIR
init|=
literal|"runjob-pytorch-yaml"
decl_stmt|;
DECL|field|yamlConfig
specifier|private
name|File
name|yamlConfig
decl_stmt|;
DECL|field|LOG
specifier|private
specifier|static
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestRunJobCliParsingCommonYaml
operator|.
name|class
argument_list|)
decl_stmt|;
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
name|After
DECL|method|after ()
specifier|public
name|void
name|after
parameter_list|()
block|{
name|YamlConfigTestUtils
operator|.
name|deleteFile
argument_list|(
name|yamlConfig
argument_list|)
expr_stmt|;
block|}
annotation|@
name|BeforeClass
DECL|method|configureResourceTypes ()
specifier|public
specifier|static
name|void
name|configureResourceTypes
parameter_list|()
block|{
try|try
block|{
name|ResourceUtils
operator|.
name|configureResourceType
argument_list|(
name|ResourceUtils
operator|.
name|GPU_URI
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SubmarineRuntimeException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"The hadoop dependency doesn't support gpu resource, "
operator|+
literal|"so just skip this test case."
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Rule
DECL|field|exception
specifier|public
name|ExpectedException
name|exception
init|=
name|ExpectedException
operator|.
name|none
argument_list|()
decl_stmt|;
annotation|@
name|Test
DECL|method|testYamlAndCliOptionIsDefinedIsInvalid ()
specifier|public
name|void
name|testYamlAndCliOptionIsDefinedIsInvalid
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
name|yamlConfig
operator|=
name|YamlConfigTestUtils
operator|.
name|createTempFileWithContents
argument_list|(
name|TF_DIR
operator|+
literal|"/valid-config.yaml"
argument_list|)
expr_stmt|;
name|String
index|[]
name|args
init|=
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
literal|"-f"
block|,
name|yamlConfig
operator|.
name|getAbsolutePath
argument_list|()
block|}
decl_stmt|;
name|exception
operator|.
name|expect
argument_list|(
name|YarnException
operator|.
name|class
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expectMessage
argument_list|(
literal|"defined both with YAML config and with "
operator|+
literal|"CLI argument"
argument_list|)
expr_stmt|;
name|runJobCli
operator|.
name|run
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testYamlAndCliOptionIsDefinedIsInvalidWithListOption ()
specifier|public
name|void
name|testYamlAndCliOptionIsDefinedIsInvalidWithListOption
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
name|yamlConfig
operator|=
name|YamlConfigTestUtils
operator|.
name|createTempFileWithContents
argument_list|(
name|TF_DIR
operator|+
literal|"/valid-config.yaml"
argument_list|)
expr_stmt|;
name|String
index|[]
name|args
init|=
operator|new
name|String
index|[]
block|{
literal|"--name"
block|,
literal|"my-job"
block|,
literal|"--quicklink"
block|,
literal|"AAA=http://master-0:8321"
block|,
literal|"--quicklink"
block|,
literal|"BBB=http://worker-0:1234"
block|,
literal|"-f"
block|,
name|yamlConfig
operator|.
name|getAbsolutePath
argument_list|()
block|}
decl_stmt|;
name|exception
operator|.
name|expect
argument_list|(
name|YarnException
operator|.
name|class
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expectMessage
argument_list|(
literal|"defined both with YAML config and with "
operator|+
literal|"CLI argument"
argument_list|)
expr_stmt|;
name|runJobCli
operator|.
name|run
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFalseValuesForBooleanFields ()
specifier|public
name|void
name|testFalseValuesForBooleanFields
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
name|yamlConfig
operator|=
name|YamlConfigTestUtils
operator|.
name|createTempFileWithContents
argument_list|(
name|DIR_NAME
operator|+
literal|"/test-false-values.yaml"
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
literal|"-f"
block|,
name|yamlConfig
operator|.
name|getAbsolutePath
argument_list|()
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
name|assertTrue
argument_list|(
name|RunJobParameters
operator|.
name|class
operator|+
literal|" must be an instance of "
operator|+
name|TensorFlowRunJobParameters
operator|.
name|class
argument_list|,
name|jobRunParameters
operator|instanceof
name|TensorFlowRunJobParameters
argument_list|)
expr_stmt|;
name|TensorFlowRunJobParameters
name|tensorFlowParams
init|=
operator|(
name|TensorFlowRunJobParameters
operator|)
name|jobRunParameters
decl_stmt|;
name|assertFalse
argument_list|(
name|jobRunParameters
operator|.
name|isDistributeKeytab
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|jobRunParameters
operator|.
name|isWaitJobFinish
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|tensorFlowParams
operator|.
name|isTensorboardEnabled
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testWrongIndentation ()
specifier|public
name|void
name|testWrongIndentation
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
name|yamlConfig
operator|=
name|YamlConfigTestUtils
operator|.
name|createTempFileWithContents
argument_list|(
name|DIR_NAME
operator|+
literal|"/wrong-indentation.yaml"
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expect
argument_list|(
name|YamlParseException
operator|.
name|class
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expectMessage
argument_list|(
literal|"Failed to parse YAML config, details:"
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
literal|"-f"
block|,
name|yamlConfig
operator|.
name|getAbsolutePath
argument_list|()
block|,
literal|"--verbose"
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testWrongFilename ()
specifier|public
name|void
name|testWrongFilename
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
name|exception
operator|.
name|expect
argument_list|(
name|YamlParseException
operator|.
name|class
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
literal|"-f"
block|,
literal|"not-existing"
block|,
literal|"--verbose"
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testEmptyFile ()
specifier|public
name|void
name|testEmptyFile
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
name|yamlConfig
operator|=
name|YamlConfigTestUtils
operator|.
name|createEmptyTempFile
argument_list|()
expr_stmt|;
name|exception
operator|.
name|expect
argument_list|(
name|YamlParseException
operator|.
name|class
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
literal|"-f"
block|,
name|yamlConfig
operator|.
name|getAbsolutePath
argument_list|()
block|,
literal|"--verbose"
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNotExistingFile ()
specifier|public
name|void
name|testNotExistingFile
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
name|exception
operator|.
name|expect
argument_list|(
name|YamlParseException
operator|.
name|class
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expectMessage
argument_list|(
literal|"file does not exist"
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
literal|"-f"
block|,
literal|"blabla"
block|,
literal|"--verbose"
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testWrongPropertyName ()
specifier|public
name|void
name|testWrongPropertyName
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
name|yamlConfig
operator|=
name|YamlConfigTestUtils
operator|.
name|createTempFileWithContents
argument_list|(
name|DIR_NAME
operator|+
literal|"/wrong-property-name.yaml"
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expect
argument_list|(
name|YamlParseException
operator|.
name|class
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expectMessage
argument_list|(
literal|"Failed to parse YAML config, details:"
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
literal|"-f"
block|,
name|yamlConfig
operator|.
name|getAbsolutePath
argument_list|()
block|,
literal|"--verbose"
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMissingConfigsSection ()
specifier|public
name|void
name|testMissingConfigsSection
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
name|yamlConfig
operator|=
name|YamlConfigTestUtils
operator|.
name|createTempFileWithContents
argument_list|(
name|DIR_NAME
operator|+
literal|"/missing-configs.yaml"
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expect
argument_list|(
name|YamlParseException
operator|.
name|class
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expectMessage
argument_list|(
literal|"config section should be defined, "
operator|+
literal|"but it cannot be found"
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
literal|"-f"
block|,
name|yamlConfig
operator|.
name|getAbsolutePath
argument_list|()
block|,
literal|"--verbose"
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMissingSectionsShouldParsed ()
specifier|public
name|void
name|testMissingSectionsShouldParsed
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
name|yamlConfig
operator|=
name|YamlConfigTestUtils
operator|.
name|createTempFileWithContents
argument_list|(
name|DIR_NAME
operator|+
literal|"/some-sections-missing.yaml"
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
literal|"-f"
block|,
name|yamlConfig
operator|.
name|getAbsolutePath
argument_list|()
block|,
literal|"--verbose"
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAbsentFramework ()
specifier|public
name|void
name|testAbsentFramework
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
name|yamlConfig
operator|=
name|YamlConfigTestUtils
operator|.
name|createTempFileWithContents
argument_list|(
name|DIR_NAME
operator|+
literal|"/missing-framework.yaml"
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
literal|"-f"
block|,
name|yamlConfig
operator|.
name|getAbsolutePath
argument_list|()
block|,
literal|"--verbose"
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testEmptyFramework ()
specifier|public
name|void
name|testEmptyFramework
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
name|yamlConfig
operator|=
name|YamlConfigTestUtils
operator|.
name|createTempFileWithContents
argument_list|(
name|DIR_NAME
operator|+
literal|"/empty-framework.yaml"
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
literal|"-f"
block|,
name|yamlConfig
operator|.
name|getAbsolutePath
argument_list|()
block|,
literal|"--verbose"
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testInvalidFramework ()
specifier|public
name|void
name|testInvalidFramework
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
name|yamlConfig
operator|=
name|YamlConfigTestUtils
operator|.
name|createTempFileWithContents
argument_list|(
name|DIR_NAME
operator|+
literal|"/invalid-framework.yaml"
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expect
argument_list|(
name|YamlParseException
operator|.
name|class
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expectMessage
argument_list|(
literal|"framework should is defined, "
operator|+
literal|"but it has an invalid value"
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
literal|"-f"
block|,
name|yamlConfig
operator|.
name|getAbsolutePath
argument_list|()
block|,
literal|"--verbose"
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

