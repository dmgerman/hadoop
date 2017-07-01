begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.scm
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|scm
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
name|hdfs
operator|.
name|protocol
operator|.
name|DatanodeID
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
name|io
operator|.
name|IOUtils
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
name|ozone
operator|.
name|MiniOzoneCluster
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
name|ozone
operator|.
name|OzoneConfiguration
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
name|ozone
operator|.
name|OzoneConsts
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|ContainerData
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|KeyUtils
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|interfaces
operator|.
name|ContainerManager
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
name|ozone
operator|.
name|scm
operator|.
name|cli
operator|.
name|ResultCode
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
name|ozone
operator|.
name|scm
operator|.
name|cli
operator|.
name|SCMCLI
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
name|scm
operator|.
name|XceiverClientManager
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
name|scm
operator|.
name|client
operator|.
name|ContainerOperationClient
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
name|scm
operator|.
name|client
operator|.
name|ScmClient
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
name|scm
operator|.
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|Pipeline
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
name|scm
operator|.
name|protocolPB
operator|.
name|StorageContainerLocationProtocolClientSideTranslatorPB
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
name|StringUtils
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
name|Timeout
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
name|IOException
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
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
name|assertNotNull
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

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
import|;
end_import

begin_comment
comment|/**  * This class tests the CLI of SCM.  */
end_comment

begin_class
DECL|class|TestSCMCli
specifier|public
class|class
name|TestSCMCli
block|{
DECL|field|cli
specifier|private
specifier|static
name|SCMCLI
name|cli
decl_stmt|;
DECL|field|cluster
specifier|private
specifier|static
name|MiniOzoneCluster
name|cluster
decl_stmt|;
DECL|field|conf
specifier|private
specifier|static
name|OzoneConfiguration
name|conf
decl_stmt|;
specifier|private
specifier|static
name|StorageContainerLocationProtocolClientSideTranslatorPB
DECL|field|storageContainerLocationClient
name|storageContainerLocationClient
decl_stmt|;
DECL|field|scm
specifier|private
specifier|static
name|StorageContainerManager
name|scm
decl_stmt|;
DECL|field|containerManager
specifier|private
specifier|static
name|ContainerManager
name|containerManager
decl_stmt|;
DECL|field|outContent
specifier|private
specifier|static
name|ByteArrayOutputStream
name|outContent
decl_stmt|;
DECL|field|outStream
specifier|private
specifier|static
name|PrintStream
name|outStream
decl_stmt|;
DECL|field|errContent
specifier|private
specifier|static
name|ByteArrayOutputStream
name|errContent
decl_stmt|;
DECL|field|errStream
specifier|private
specifier|static
name|PrintStream
name|errStream
decl_stmt|;
annotation|@
name|Rule
DECL|field|globalTimeout
specifier|public
name|Timeout
name|globalTimeout
init|=
operator|new
name|Timeout
argument_list|(
literal|30000
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
name|Exception
block|{
name|conf
operator|=
operator|new
name|OzoneConfiguration
argument_list|()
expr_stmt|;
name|cluster
operator|=
operator|new
name|MiniOzoneCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|numDataNodes
argument_list|(
literal|1
argument_list|)
operator|.
name|setHandlerType
argument_list|(
name|OzoneConsts
operator|.
name|OZONE_HANDLER_DISTRIBUTED
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|storageContainerLocationClient
operator|=
name|cluster
operator|.
name|createStorageContainerLocationClient
argument_list|()
expr_stmt|;
name|ScmClient
name|client
init|=
operator|new
name|ContainerOperationClient
argument_list|(
name|storageContainerLocationClient
argument_list|,
operator|new
name|XceiverClientManager
argument_list|(
name|conf
argument_list|)
argument_list|)
decl_stmt|;
name|outContent
operator|=
operator|new
name|ByteArrayOutputStream
argument_list|()
expr_stmt|;
name|outStream
operator|=
operator|new
name|PrintStream
argument_list|(
name|outContent
argument_list|)
expr_stmt|;
name|errContent
operator|=
operator|new
name|ByteArrayOutputStream
argument_list|()
expr_stmt|;
name|errStream
operator|=
operator|new
name|PrintStream
argument_list|(
name|errContent
argument_list|)
expr_stmt|;
name|cli
operator|=
operator|new
name|SCMCLI
argument_list|(
name|client
argument_list|,
name|outStream
argument_list|,
name|errStream
argument_list|)
expr_stmt|;
name|scm
operator|=
name|cluster
operator|.
name|getStorageContainerManager
argument_list|()
expr_stmt|;
name|containerManager
operator|=
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getOzoneContainerManager
argument_list|()
operator|.
name|getContainerManager
argument_list|()
expr_stmt|;
block|}
DECL|method|runCommandAndGetOutput (String[] cmd, ByteArrayOutputStream out, ByteArrayOutputStream err)
specifier|private
name|int
name|runCommandAndGetOutput
parameter_list|(
name|String
index|[]
name|cmd
parameter_list|,
name|ByteArrayOutputStream
name|out
parameter_list|,
name|ByteArrayOutputStream
name|err
parameter_list|)
throws|throws
name|Exception
block|{
name|PrintStream
name|cmdOutStream
init|=
name|System
operator|.
name|out
decl_stmt|;
name|PrintStream
name|cmdErrStream
init|=
name|System
operator|.
name|err
decl_stmt|;
if|if
condition|(
name|out
operator|!=
literal|null
condition|)
block|{
name|cmdOutStream
operator|=
operator|new
name|PrintStream
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|err
operator|!=
literal|null
condition|)
block|{
name|cmdErrStream
operator|=
operator|new
name|PrintStream
argument_list|(
name|err
argument_list|)
expr_stmt|;
block|}
name|ScmClient
name|client
init|=
operator|new
name|ContainerOperationClient
argument_list|(
name|storageContainerLocationClient
argument_list|,
operator|new
name|XceiverClientManager
argument_list|(
name|conf
argument_list|)
argument_list|)
decl_stmt|;
name|SCMCLI
name|scmCLI
init|=
operator|new
name|SCMCLI
argument_list|(
name|client
argument_list|,
name|cmdOutStream
argument_list|,
name|cmdErrStream
argument_list|)
decl_stmt|;
return|return
name|scmCLI
operator|.
name|run
argument_list|(
name|cmd
argument_list|)
return|;
block|}
annotation|@
name|AfterClass
DECL|method|shutdown ()
specifier|public
specifier|static
name|void
name|shutdown
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|IOUtils
operator|.
name|cleanup
argument_list|(
literal|null
argument_list|,
name|storageContainerLocationClient
argument_list|,
name|cluster
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCreateContainer ()
specifier|public
name|void
name|testCreateContainer
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|containerName
init|=
literal|"containerTestCreate"
decl_stmt|;
try|try
block|{
name|scm
operator|.
name|getContainer
argument_list|(
name|containerName
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should not be able to get the container"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|ioe
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Specified key does not exist. key : "
operator|+
name|containerName
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|String
index|[]
name|args
init|=
block|{
literal|"-container"
block|,
literal|"-create"
block|,
literal|"-c"
block|,
name|containerName
block|}
decl_stmt|;
name|assertEquals
argument_list|(
name|ResultCode
operator|.
name|SUCCESS
argument_list|,
name|cli
operator|.
name|run
argument_list|(
name|args
argument_list|)
argument_list|)
expr_stmt|;
name|Pipeline
name|container
init|=
name|scm
operator|.
name|getContainer
argument_list|(
name|containerName
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|container
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|containerName
argument_list|,
name|container
operator|.
name|getContainerName
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|containerExist (String containerName)
specifier|private
name|boolean
name|containerExist
parameter_list|(
name|String
name|containerName
parameter_list|)
block|{
try|try
block|{
name|Pipeline
name|scmPipeline
init|=
name|scm
operator|.
name|getContainer
argument_list|(
name|containerName
argument_list|)
decl_stmt|;
return|return
name|scmPipeline
operator|!=
literal|null
operator|&&
name|containerName
operator|.
name|equals
argument_list|(
name|scmPipeline
operator|.
name|getContainerName
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
annotation|@
name|Test
DECL|method|testDeleteContainer ()
specifier|public
name|void
name|testDeleteContainer
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|containerName
decl_stmt|;
name|ContainerData
name|containerData
decl_stmt|;
name|Pipeline
name|pipeline
decl_stmt|;
name|String
index|[]
name|delCmd
decl_stmt|;
name|ByteArrayOutputStream
name|testErr
decl_stmt|;
name|int
name|exitCode
decl_stmt|;
comment|// ****************************************
comment|// 1. Test to delete a non-empty container.
comment|// ****************************************
comment|// Create an non-empty container
name|containerName
operator|=
literal|"non-empty-container"
expr_stmt|;
name|pipeline
operator|=
name|scm
operator|.
name|allocateContainer
argument_list|(
name|containerName
argument_list|)
expr_stmt|;
name|containerData
operator|=
operator|new
name|ContainerData
argument_list|(
name|containerName
argument_list|)
expr_stmt|;
name|containerManager
operator|.
name|createContainer
argument_list|(
name|pipeline
argument_list|,
name|containerData
argument_list|)
expr_stmt|;
name|ContainerData
name|cdata
init|=
name|containerManager
operator|.
name|readContainer
argument_list|(
name|containerName
argument_list|)
decl_stmt|;
name|KeyUtils
operator|.
name|getDB
argument_list|(
name|cdata
argument_list|,
name|conf
argument_list|)
operator|.
name|put
argument_list|(
name|containerName
operator|.
name|getBytes
argument_list|()
argument_list|,
literal|"someKey"
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|containerExist
argument_list|(
name|containerName
argument_list|)
argument_list|)
expr_stmt|;
comment|// Gracefully delete a container should fail because it is open.
name|delCmd
operator|=
operator|new
name|String
index|[]
block|{
literal|"-container"
block|,
literal|"-delete"
block|,
literal|"-c"
block|,
name|containerName
block|}
expr_stmt|;
name|testErr
operator|=
operator|new
name|ByteArrayOutputStream
argument_list|()
expr_stmt|;
name|exitCode
operator|=
name|runCommandAndGetOutput
argument_list|(
name|delCmd
argument_list|,
literal|null
argument_list|,
name|testErr
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ResultCode
operator|.
name|EXECUTION_ERROR
argument_list|,
name|exitCode
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|testErr
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Deleting an open container is not allowed."
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|containerExist
argument_list|(
name|containerName
argument_list|)
argument_list|)
expr_stmt|;
comment|// Close the container
name|containerManager
operator|.
name|closeContainer
argument_list|(
name|containerName
argument_list|)
expr_stmt|;
comment|// Gracefully delete a container should fail because it is not empty.
name|testErr
operator|=
operator|new
name|ByteArrayOutputStream
argument_list|()
expr_stmt|;
name|int
name|exitCode2
init|=
name|runCommandAndGetOutput
argument_list|(
name|delCmd
argument_list|,
literal|null
argument_list|,
name|testErr
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|ResultCode
operator|.
name|EXECUTION_ERROR
argument_list|,
name|exitCode2
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|testErr
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Container cannot be deleted because it is not empty."
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|containerExist
argument_list|(
name|containerName
argument_list|)
argument_list|)
expr_stmt|;
comment|// Try force delete again.
name|delCmd
operator|=
operator|new
name|String
index|[]
block|{
literal|"-container"
block|,
literal|"-delete"
block|,
literal|"-c"
block|,
name|containerName
block|,
literal|"-f"
block|}
expr_stmt|;
name|exitCode
operator|=
name|runCommandAndGetOutput
argument_list|(
name|delCmd
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ResultCode
operator|.
name|SUCCESS
argument_list|,
name|exitCode
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|containerExist
argument_list|(
name|containerName
argument_list|)
argument_list|)
expr_stmt|;
comment|// ****************************************
comment|// 2. Test to delete an empty container.
comment|// ****************************************
comment|// Create an empty container
name|containerName
operator|=
literal|"empty-container"
expr_stmt|;
name|pipeline
operator|=
name|scm
operator|.
name|allocateContainer
argument_list|(
name|containerName
argument_list|)
expr_stmt|;
name|containerData
operator|=
operator|new
name|ContainerData
argument_list|(
name|containerName
argument_list|)
expr_stmt|;
name|containerManager
operator|.
name|createContainer
argument_list|(
name|pipeline
argument_list|,
name|containerData
argument_list|)
expr_stmt|;
name|containerManager
operator|.
name|closeContainer
argument_list|(
name|containerName
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|containerExist
argument_list|(
name|containerName
argument_list|)
argument_list|)
expr_stmt|;
comment|// Successfully delete an empty container.
name|delCmd
operator|=
operator|new
name|String
index|[]
block|{
literal|"-container"
block|,
literal|"-delete"
block|,
literal|"-c"
block|,
name|containerName
block|}
expr_stmt|;
name|exitCode
operator|=
name|runCommandAndGetOutput
argument_list|(
name|delCmd
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ResultCode
operator|.
name|SUCCESS
argument_list|,
name|exitCode
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|containerExist
argument_list|(
name|containerName
argument_list|)
argument_list|)
expr_stmt|;
comment|// After the container is deleted,
comment|// a same name container can now be recreated.
name|pipeline
operator|=
name|scm
operator|.
name|allocateContainer
argument_list|(
name|containerName
argument_list|)
expr_stmt|;
name|containerManager
operator|.
name|createContainer
argument_list|(
name|pipeline
argument_list|,
name|containerData
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|containerExist
argument_list|(
name|containerName
argument_list|)
argument_list|)
expr_stmt|;
comment|// ****************************************
comment|// 3. Test to delete a non-exist container.
comment|// ****************************************
name|containerName
operator|=
literal|"non-exist-container"
expr_stmt|;
name|delCmd
operator|=
operator|new
name|String
index|[]
block|{
literal|"-container"
block|,
literal|"-delete"
block|,
literal|"-c"
block|,
name|containerName
block|}
expr_stmt|;
name|testErr
operator|=
operator|new
name|ByteArrayOutputStream
argument_list|()
expr_stmt|;
name|exitCode
operator|=
name|runCommandAndGetOutput
argument_list|(
name|delCmd
argument_list|,
literal|null
argument_list|,
name|testErr
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ResultCode
operator|.
name|EXECUTION_ERROR
argument_list|,
name|exitCode
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|testErr
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Specified key does not exist."
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testInfoContainer ()
specifier|public
name|void
name|testInfoContainer
parameter_list|()
throws|throws
name|Exception
block|{
comment|// The cluster has one Datanode server.
name|DatanodeID
name|datanodeID
init|=
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getDatanodeId
argument_list|()
decl_stmt|;
name|String
name|formatStr
init|=
literal|"Container Name: %s\n"
operator|+
literal|"Container State: %s\n"
operator|+
literal|"Container DB Path: %s\n"
operator|+
literal|"Container Path: %s\n"
operator|+
literal|"Container Metadata: {%s}\n"
operator|+
literal|"LeaderID: %s\n"
operator|+
literal|"Datanodes: [%s]\n"
decl_stmt|;
name|String
name|formatStrWithHash
init|=
literal|"Container Name: %s\n"
operator|+
literal|"Container State: %s\n"
operator|+
literal|"Container Hash: %s\n"
operator|+
literal|"Container DB Path: %s\n"
operator|+
literal|"Container Path: %s\n"
operator|+
literal|"Container Metadata: {%s}\n"
operator|+
literal|"LeaderID: %s\n"
operator|+
literal|"Datanodes: [%s]\n"
decl_stmt|;
comment|// Test a non-exist container
name|String
name|cname
init|=
literal|"nonExistContainer"
decl_stmt|;
name|String
index|[]
name|info
init|=
block|{
literal|"-container"
block|,
literal|"-info"
block|,
name|cname
block|}
decl_stmt|;
name|int
name|exitCode
init|=
name|runCommandAndGetOutput
argument_list|(
name|info
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|ResultCode
operator|.
name|EXECUTION_ERROR
argument_list|,
name|exitCode
argument_list|)
expr_stmt|;
comment|// Create an empty container.
name|cname
operator|=
literal|"ContainerTestInfo1"
expr_stmt|;
name|Pipeline
name|pipeline
init|=
name|scm
operator|.
name|allocateContainer
argument_list|(
name|cname
argument_list|)
decl_stmt|;
name|ContainerData
name|data
init|=
operator|new
name|ContainerData
argument_list|(
name|cname
argument_list|)
decl_stmt|;
name|containerManager
operator|.
name|createContainer
argument_list|(
name|pipeline
argument_list|,
name|data
argument_list|)
expr_stmt|;
name|info
operator|=
operator|new
name|String
index|[]
block|{
literal|"-container"
block|,
literal|"-info"
block|,
literal|"-c"
block|,
name|cname
block|}
expr_stmt|;
name|ByteArrayOutputStream
name|out
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|exitCode
operator|=
name|runCommandAndGetOutput
argument_list|(
name|info
argument_list|,
name|out
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ResultCode
operator|.
name|SUCCESS
argument_list|,
name|exitCode
argument_list|)
expr_stmt|;
name|String
name|openStatus
init|=
name|data
operator|.
name|isOpen
argument_list|()
condition|?
literal|"OPEN"
else|:
literal|"CLOSED"
decl_stmt|;
name|String
name|expected
init|=
name|String
operator|.
name|format
argument_list|(
name|formatStr
argument_list|,
name|cname
argument_list|,
name|openStatus
argument_list|,
name|data
operator|.
name|getDBPath
argument_list|()
argument_list|,
name|data
operator|.
name|getContainerPath
argument_list|()
argument_list|,
literal|""
argument_list|,
name|datanodeID
operator|.
name|getHostName
argument_list|()
argument_list|,
name|datanodeID
operator|.
name|getHostName
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|out
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|reset
argument_list|()
expr_stmt|;
comment|// Create an non-empty container
name|cname
operator|=
literal|"ContainerTestInfo2"
expr_stmt|;
name|pipeline
operator|=
name|scm
operator|.
name|allocateContainer
argument_list|(
name|cname
argument_list|)
expr_stmt|;
name|data
operator|=
operator|new
name|ContainerData
argument_list|(
name|cname
argument_list|)
expr_stmt|;
name|containerManager
operator|.
name|createContainer
argument_list|(
name|pipeline
argument_list|,
name|data
argument_list|)
expr_stmt|;
name|KeyUtils
operator|.
name|getDB
argument_list|(
name|data
argument_list|,
name|conf
argument_list|)
operator|.
name|put
argument_list|(
name|cname
operator|.
name|getBytes
argument_list|()
argument_list|,
literal|"someKey"
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|info
operator|=
operator|new
name|String
index|[]
block|{
literal|"-container"
block|,
literal|"-info"
block|,
literal|"-c"
block|,
name|cname
block|}
expr_stmt|;
name|exitCode
operator|=
name|runCommandAndGetOutput
argument_list|(
name|info
argument_list|,
name|out
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ResultCode
operator|.
name|SUCCESS
argument_list|,
name|exitCode
argument_list|)
expr_stmt|;
name|openStatus
operator|=
name|data
operator|.
name|isOpen
argument_list|()
condition|?
literal|"OPEN"
else|:
literal|"CLOSED"
expr_stmt|;
name|expected
operator|=
name|String
operator|.
name|format
argument_list|(
name|formatStr
argument_list|,
name|cname
argument_list|,
name|openStatus
argument_list|,
name|data
operator|.
name|getDBPath
argument_list|()
argument_list|,
name|data
operator|.
name|getContainerPath
argument_list|()
argument_list|,
literal|""
argument_list|,
name|datanodeID
operator|.
name|getHostName
argument_list|()
argument_list|,
name|datanodeID
operator|.
name|getHostName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|out
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|reset
argument_list|()
expr_stmt|;
comment|// Create a container with some meta data.
name|cname
operator|=
literal|"ContainerTestInfo3"
expr_stmt|;
name|pipeline
operator|=
name|scm
operator|.
name|allocateContainer
argument_list|(
name|cname
argument_list|)
expr_stmt|;
name|data
operator|=
operator|new
name|ContainerData
argument_list|(
name|cname
argument_list|)
expr_stmt|;
name|data
operator|.
name|addMetadata
argument_list|(
literal|"VOLUME"
argument_list|,
literal|"shire"
argument_list|)
expr_stmt|;
name|data
operator|.
name|addMetadata
argument_list|(
literal|"owner"
argument_list|,
literal|"bilbo"
argument_list|)
expr_stmt|;
name|containerManager
operator|.
name|createContainer
argument_list|(
name|pipeline
argument_list|,
name|data
argument_list|)
expr_stmt|;
name|KeyUtils
operator|.
name|getDB
argument_list|(
name|data
argument_list|,
name|conf
argument_list|)
operator|.
name|put
argument_list|(
name|cname
operator|.
name|getBytes
argument_list|()
argument_list|,
literal|"someKey"
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|metaList
init|=
name|data
operator|.
name|getAllMetadata
argument_list|()
operator|.
name|entrySet
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|entry
lambda|->
name|entry
operator|.
name|getKey
argument_list|()
operator|+
literal|":"
operator|+
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|metadataStr
init|=
name|StringUtils
operator|.
name|join
argument_list|(
literal|", "
argument_list|,
name|metaList
argument_list|)
decl_stmt|;
name|info
operator|=
operator|new
name|String
index|[]
block|{
literal|"-container"
block|,
literal|"-info"
block|,
literal|"-c"
block|,
name|cname
block|}
expr_stmt|;
name|exitCode
operator|=
name|runCommandAndGetOutput
argument_list|(
name|info
argument_list|,
name|out
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ResultCode
operator|.
name|SUCCESS
argument_list|,
name|exitCode
argument_list|)
expr_stmt|;
name|openStatus
operator|=
name|data
operator|.
name|isOpen
argument_list|()
condition|?
literal|"OPEN"
else|:
literal|"CLOSED"
expr_stmt|;
name|expected
operator|=
name|String
operator|.
name|format
argument_list|(
name|formatStr
argument_list|,
name|cname
argument_list|,
name|openStatus
argument_list|,
name|data
operator|.
name|getDBPath
argument_list|()
argument_list|,
name|data
operator|.
name|getContainerPath
argument_list|()
argument_list|,
name|metadataStr
argument_list|,
name|datanodeID
operator|.
name|getHostName
argument_list|()
argument_list|,
name|datanodeID
operator|.
name|getHostName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|out
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|reset
argument_list|()
expr_stmt|;
comment|// Close last container and test info again.
name|containerManager
operator|.
name|closeContainer
argument_list|(
name|cname
argument_list|)
expr_stmt|;
name|info
operator|=
operator|new
name|String
index|[]
block|{
literal|"-container"
block|,
literal|"-info"
block|,
literal|"-c"
block|,
name|cname
block|}
expr_stmt|;
name|exitCode
operator|=
name|runCommandAndGetOutput
argument_list|(
name|info
argument_list|,
name|out
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ResultCode
operator|.
name|SUCCESS
argument_list|,
name|exitCode
argument_list|)
expr_stmt|;
name|data
operator|=
name|containerManager
operator|.
name|readContainer
argument_list|(
name|cname
argument_list|)
expr_stmt|;
name|openStatus
operator|=
name|data
operator|.
name|isOpen
argument_list|()
condition|?
literal|"OPEN"
else|:
literal|"CLOSED"
expr_stmt|;
name|expected
operator|=
name|String
operator|.
name|format
argument_list|(
name|formatStrWithHash
argument_list|,
name|cname
argument_list|,
name|openStatus
argument_list|,
name|data
operator|.
name|getHash
argument_list|()
argument_list|,
name|data
operator|.
name|getDBPath
argument_list|()
argument_list|,
name|data
operator|.
name|getContainerPath
argument_list|()
argument_list|,
name|metadataStr
argument_list|,
name|datanodeID
operator|.
name|getHostName
argument_list|()
argument_list|,
name|datanodeID
operator|.
name|getHostName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|out
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNonExistCommand ()
specifier|public
name|void
name|testNonExistCommand
parameter_list|()
throws|throws
name|Exception
block|{
name|PrintStream
name|init
init|=
name|System
operator|.
name|out
decl_stmt|;
name|ByteArrayOutputStream
name|testContent
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|PrintStream
name|testPrintOut
init|=
operator|new
name|PrintStream
argument_list|(
name|testContent
argument_list|)
decl_stmt|;
name|System
operator|.
name|setOut
argument_list|(
name|testPrintOut
argument_list|)
expr_stmt|;
name|String
index|[]
name|args
init|=
block|{
literal|"-nothingUseful"
block|}
decl_stmt|;
name|assertEquals
argument_list|(
name|ResultCode
operator|.
name|UNRECOGNIZED_CMD
argument_list|,
name|cli
operator|.
name|run
argument_list|(
name|args
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|errContent
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Unrecognized options:[-nothingUseful]"
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|expectedOut
init|=
literal|"usage: hdfs scm<commands> [<options>]\n"
operator|+
literal|"where<commands> can be one of the following\n"
operator|+
literal|" -container   Container related options\n"
decl_stmt|;
name|assertEquals
argument_list|(
name|expectedOut
argument_list|,
name|testContent
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|setOut
argument_list|(
name|init
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testHelp ()
specifier|public
name|void
name|testHelp
parameter_list|()
throws|throws
name|Exception
block|{
comment|// TODO : this test assertion may break for every new help entry added
comment|// may want to disable this test some time later. For now, mainly to show
comment|// case the format of help output.
name|PrintStream
name|init
init|=
name|System
operator|.
name|out
decl_stmt|;
name|ByteArrayOutputStream
name|testContent
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|PrintStream
name|testPrintOut
init|=
operator|new
name|PrintStream
argument_list|(
name|testContent
argument_list|)
decl_stmt|;
name|System
operator|.
name|setOut
argument_list|(
name|testPrintOut
argument_list|)
expr_stmt|;
name|String
index|[]
name|args
init|=
block|{
literal|"-help"
block|}
decl_stmt|;
name|assertEquals
argument_list|(
name|ResultCode
operator|.
name|SUCCESS
argument_list|,
name|cli
operator|.
name|run
argument_list|(
name|args
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|expected
init|=
literal|"usage: hdfs scm<commands> [<options>]\n"
operator|+
literal|"where<commands> can be one of the following\n"
operator|+
literal|" -container   Container related options\n"
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|testContent
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|testContent
operator|.
name|reset
argument_list|()
expr_stmt|;
name|String
index|[]
name|args1
init|=
block|{
literal|"-container"
block|,
literal|"-help"
block|}
decl_stmt|;
name|assertEquals
argument_list|(
name|ResultCode
operator|.
name|SUCCESS
argument_list|,
name|cli
operator|.
name|run
argument_list|(
name|args1
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|expected1
init|=
literal|"usage: hdfs scm -container<commands><options>\n"
operator|+
literal|"where<commands> can be one of the following\n"
operator|+
literal|" -create   Create container\n"
operator|+
literal|" -delete   Delete container\n"
operator|+
literal|" -info     Info container\n"
decl_stmt|;
name|assertEquals
argument_list|(
name|expected1
argument_list|,
name|testContent
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|testContent
operator|.
name|reset
argument_list|()
expr_stmt|;
name|String
index|[]
name|args2
init|=
block|{
literal|"-container"
block|,
literal|"-create"
block|,
literal|"-help"
block|}
decl_stmt|;
name|assertEquals
argument_list|(
name|ResultCode
operator|.
name|SUCCESS
argument_list|,
name|cli
operator|.
name|run
argument_list|(
name|args2
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|expected2
init|=
literal|"usage: hdfs scm -container -create<option>\n"
operator|+
literal|"where<option> is\n"
operator|+
literal|" -c<arg>   Specify container name\n"
decl_stmt|;
name|assertEquals
argument_list|(
name|expected2
argument_list|,
name|testContent
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|testContent
operator|.
name|reset
argument_list|()
expr_stmt|;
name|String
index|[]
name|args3
init|=
block|{
literal|"-container"
block|,
literal|"-delete"
block|,
literal|"-help"
block|}
decl_stmt|;
name|assertEquals
argument_list|(
name|ResultCode
operator|.
name|SUCCESS
argument_list|,
name|cli
operator|.
name|run
argument_list|(
name|args3
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|expected3
init|=
literal|"usage: hdfs scm -container -delete<option>\n"
operator|+
literal|"where<option> is\n"
operator|+
literal|" -c<arg>   Specify container name\n"
operator|+
literal|" -f         forcibly delete a container\n"
decl_stmt|;
name|assertEquals
argument_list|(
name|expected3
argument_list|,
name|testContent
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|testContent
operator|.
name|reset
argument_list|()
expr_stmt|;
name|String
index|[]
name|args4
init|=
block|{
literal|"-container"
block|,
literal|"-info"
block|,
literal|"-help"
block|}
decl_stmt|;
name|assertEquals
argument_list|(
name|ResultCode
operator|.
name|SUCCESS
argument_list|,
name|cli
operator|.
name|run
argument_list|(
name|args4
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|expected4
init|=
literal|"usage: hdfs scm -container -info<option>\n"
operator|+
literal|"where<option> is\n"
operator|+
literal|" -c<arg>   Specify container name\n"
decl_stmt|;
name|assertEquals
argument_list|(
name|expected4
argument_list|,
name|testContent
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|testContent
operator|.
name|reset
argument_list|()
expr_stmt|;
name|System
operator|.
name|setOut
argument_list|(
name|init
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

