begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|nodemanager
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Matchers
operator|.
name|argThat
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
name|isNull
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
name|spy
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
name|times
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
name|java
operator|.
name|security
operator|.
name|PrivilegedExceptionAction
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
name|List
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
name|fs
operator|.
name|FileContext
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
name|fs
operator|.
name|UnsupportedFileSystemException
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
name|yarn
operator|.
name|api
operator|.
name|ContainerManagementProtocol
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
name|protocolrecords
operator|.
name|GetContainerStatusRequest
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
name|protocolrecords
operator|.
name|StartContainerRequest
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
name|ApplicationAttemptId
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
name|api
operator|.
name|records
operator|.
name|ContainerId
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
name|ContainerLaunchContext
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
name|LocalResource
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
name|LocalResourceType
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
name|LocalResourceVisibility
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
name|NodeId
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
name|URL
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
name|conf
operator|.
name|YarnConfiguration
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
name|event
operator|.
name|Dispatcher
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
name|security
operator|.
name|NMTokenIdentifier
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
name|server
operator|.
name|nodemanager
operator|.
name|DeletionService
operator|.
name|FileDeletionTask
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
name|server
operator|.
name|nodemanager
operator|.
name|containermanager
operator|.
name|TestContainerManager
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
name|server
operator|.
name|nodemanager
operator|.
name|containermanager
operator|.
name|container
operator|.
name|Container
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
name|server
operator|.
name|nodemanager
operator|.
name|containermanager
operator|.
name|container
operator|.
name|ContainerState
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
name|server
operator|.
name|nodemanager
operator|.
name|containermanager
operator|.
name|localizer
operator|.
name|ContainerLocalizer
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
name|server
operator|.
name|nodemanager
operator|.
name|containermanager
operator|.
name|localizer
operator|.
name|ResourceLocalizationService
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
name|ConverterUtils
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
name|Records
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
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|ArgumentMatcher
import|;
end_import

begin_class
DECL|class|TestNodeManagerReboot
specifier|public
class|class
name|TestNodeManagerReboot
block|{
DECL|field|basedir
specifier|static
specifier|final
name|File
name|basedir
init|=
operator|new
name|File
argument_list|(
literal|"target"
argument_list|,
name|TestNodeManagerReboot
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|logsDir
specifier|static
specifier|final
name|File
name|logsDir
init|=
operator|new
name|File
argument_list|(
name|basedir
argument_list|,
literal|"logs"
argument_list|)
decl_stmt|;
DECL|field|nmLocalDir
specifier|static
specifier|final
name|File
name|nmLocalDir
init|=
operator|new
name|File
argument_list|(
name|basedir
argument_list|,
literal|"nm0"
argument_list|)
decl_stmt|;
DECL|field|localResourceDir
specifier|static
specifier|final
name|File
name|localResourceDir
init|=
operator|new
name|File
argument_list|(
name|basedir
argument_list|,
literal|"resource"
argument_list|)
decl_stmt|;
DECL|field|user
specifier|static
specifier|final
name|String
name|user
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"user.name"
argument_list|)
decl_stmt|;
DECL|field|localFS
specifier|private
name|FileContext
name|localFS
decl_stmt|;
DECL|field|nm
specifier|private
name|MyNodeManager
name|nm
decl_stmt|;
DECL|field|delService
specifier|private
name|DeletionService
name|delService
decl_stmt|;
DECL|field|LOG
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TestNodeManagerReboot
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|UnsupportedFileSystemException
block|{
name|localFS
operator|=
name|FileContext
operator|.
name|getLocalFSFileContext
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|localFS
operator|.
name|delete
argument_list|(
operator|new
name|Path
argument_list|(
name|basedir
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|nm
operator|!=
literal|null
condition|)
block|{
name|nm
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|2000000
argument_list|)
DECL|method|testClearLocalDirWhenNodeReboot ()
specifier|public
name|void
name|testClearLocalDirWhenNodeReboot
parameter_list|()
throws|throws
name|IOException
throws|,
name|YarnException
throws|,
name|InterruptedException
block|{
name|nm
operator|=
operator|new
name|MyNodeManager
argument_list|()
expr_stmt|;
name|nm
operator|.
name|start
argument_list|()
expr_stmt|;
specifier|final
name|ContainerManagementProtocol
name|containerManager
init|=
name|nm
operator|.
name|getContainerManager
argument_list|()
decl_stmt|;
comment|// create files under fileCache
name|createFiles
argument_list|(
name|nmLocalDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|ContainerLocalizer
operator|.
name|FILECACHE
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|localResourceDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|ContainerLaunchContext
name|containerLaunchContext
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|ContainerLaunchContext
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// Construct the Container-id
name|ContainerId
name|cId
init|=
name|createContainerId
argument_list|()
decl_stmt|;
name|URL
name|localResourceUri
init|=
name|ConverterUtils
operator|.
name|getYarnUrlFromPath
argument_list|(
name|localFS
operator|.
name|makeQualified
argument_list|(
operator|new
name|Path
argument_list|(
name|localResourceDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|LocalResource
name|localResource
init|=
name|LocalResource
operator|.
name|newInstance
argument_list|(
name|localResourceUri
argument_list|,
name|LocalResourceType
operator|.
name|FILE
argument_list|,
name|LocalResourceVisibility
operator|.
name|APPLICATION
argument_list|,
operator|-
literal|1
argument_list|,
name|localResourceDir
operator|.
name|lastModified
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|destinationFile
init|=
literal|"dest_file"
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|LocalResource
argument_list|>
name|localResources
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|LocalResource
argument_list|>
argument_list|()
decl_stmt|;
name|localResources
operator|.
name|put
argument_list|(
name|destinationFile
argument_list|,
name|localResource
argument_list|)
expr_stmt|;
name|containerLaunchContext
operator|.
name|setLocalResources
argument_list|(
name|localResources
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|commands
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|containerLaunchContext
operator|.
name|setCommands
argument_list|(
name|commands
argument_list|)
expr_stmt|;
specifier|final
name|StartContainerRequest
name|startRequest
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|StartContainerRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|startRequest
operator|.
name|setContainerLaunchContext
argument_list|(
name|containerLaunchContext
argument_list|)
expr_stmt|;
name|NodeId
name|nodeId
init|=
name|nm
operator|.
name|getNMContext
argument_list|()
operator|.
name|getNodeId
argument_list|()
decl_stmt|;
name|startRequest
operator|.
name|setContainerToken
argument_list|(
name|TestContainerManager
operator|.
name|createContainerToken
argument_list|(
name|cId
argument_list|,
literal|0
argument_list|,
name|nodeId
argument_list|,
name|destinationFile
argument_list|,
name|nm
operator|.
name|getNMContext
argument_list|()
operator|.
name|getContainerTokenSecretManager
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|UserGroupInformation
name|currentUser
init|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|cId
operator|.
name|getApplicationAttemptId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|NMTokenIdentifier
name|nmIdentifier
init|=
operator|new
name|NMTokenIdentifier
argument_list|(
name|cId
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|,
name|nodeId
argument_list|,
name|user
argument_list|,
literal|123
argument_list|)
decl_stmt|;
name|currentUser
operator|.
name|addTokenIdentifier
argument_list|(
name|nmIdentifier
argument_list|)
expr_stmt|;
name|currentUser
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Void
name|run
parameter_list|()
throws|throws
name|YarnException
throws|,
name|IOException
block|{
name|nm
operator|.
name|getContainerManager
argument_list|()
operator|.
name|startContainer
argument_list|(
name|startRequest
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|GetContainerStatusRequest
name|request
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|GetContainerStatusRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|request
operator|.
name|setContainerId
argument_list|(
name|cId
argument_list|)
expr_stmt|;
name|Container
name|container
init|=
name|nm
operator|.
name|getNMContext
argument_list|()
operator|.
name|getContainers
argument_list|()
operator|.
name|get
argument_list|(
name|request
operator|.
name|getContainerId
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|int
name|MAX_TRIES
init|=
literal|20
decl_stmt|;
name|int
name|numTries
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|!
name|container
operator|.
name|getContainerState
argument_list|()
operator|.
name|equals
argument_list|(
name|ContainerState
operator|.
name|DONE
argument_list|)
operator|&&
name|numTries
operator|<=
name|MAX_TRIES
condition|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|500
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ex
parameter_list|)
block|{
comment|// Do nothing
block|}
name|numTries
operator|++
expr_stmt|;
block|}
name|Assert
operator|.
name|assertEquals
argument_list|(
name|ContainerState
operator|.
name|DONE
argument_list|,
name|container
operator|.
name|getContainerState
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"The container should create a subDir named currentUser: "
operator|+
name|user
operator|+
literal|"under localDir/usercache"
argument_list|,
name|numOfLocalDirs
argument_list|(
name|nmLocalDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|ContainerLocalizer
operator|.
name|USERCACHE
argument_list|)
operator|>
literal|0
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"There should be files or Dirs under nm_private when "
operator|+
literal|"container is launched"
argument_list|,
name|numOfLocalDirs
argument_list|(
name|nmLocalDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|ResourceLocalizationService
operator|.
name|NM_PRIVATE_DIR
argument_list|)
operator|>
literal|0
argument_list|)
expr_stmt|;
comment|// restart the NodeManager
name|nm
operator|.
name|stop
argument_list|()
expr_stmt|;
name|nm
operator|=
operator|new
name|MyNodeManager
argument_list|()
expr_stmt|;
name|nm
operator|.
name|start
argument_list|()
expr_stmt|;
name|numTries
operator|=
literal|0
expr_stmt|;
while|while
condition|(
operator|(
name|numOfLocalDirs
argument_list|(
name|nmLocalDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|ContainerLocalizer
operator|.
name|USERCACHE
argument_list|)
operator|>
literal|0
operator|||
name|numOfLocalDirs
argument_list|(
name|nmLocalDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|ContainerLocalizer
operator|.
name|FILECACHE
argument_list|)
operator|>
literal|0
operator|||
name|numOfLocalDirs
argument_list|(
name|nmLocalDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|ResourceLocalizationService
operator|.
name|NM_PRIVATE_DIR
argument_list|)
operator|>
literal|0
operator|)
operator|&&
name|numTries
operator|<
name|MAX_TRIES
condition|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|500
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ex
parameter_list|)
block|{
comment|// Do nothing
block|}
name|numTries
operator|++
expr_stmt|;
block|}
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"After NM reboots, all local files should be deleted"
argument_list|,
name|numOfLocalDirs
argument_list|(
name|nmLocalDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|ContainerLocalizer
operator|.
name|USERCACHE
argument_list|)
operator|==
literal|0
operator|&&
name|numOfLocalDirs
argument_list|(
name|nmLocalDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|ContainerLocalizer
operator|.
name|FILECACHE
argument_list|)
operator|==
literal|0
operator|&&
name|numOfLocalDirs
argument_list|(
name|nmLocalDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|ResourceLocalizationService
operator|.
name|NM_PRIVATE_DIR
argument_list|)
operator|==
literal|0
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|delService
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|delete
argument_list|(
operator|(
name|String
operator|)
name|isNull
argument_list|()
argument_list|,
name|argThat
argument_list|(
operator|new
name|PathInclude
argument_list|(
name|ResourceLocalizationService
operator|.
name|NM_PRIVATE_DIR
operator|+
literal|"_DEL_"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|delService
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|delete
argument_list|(
operator|(
name|String
operator|)
name|isNull
argument_list|()
argument_list|,
name|argThat
argument_list|(
operator|new
name|PathInclude
argument_list|(
name|ContainerLocalizer
operator|.
name|FILECACHE
operator|+
literal|"_DEL_"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|delService
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|scheduleFileDeletionTask
argument_list|(
name|argThat
argument_list|(
operator|new
name|FileDeletionInclude
argument_list|(
name|user
argument_list|,
literal|null
argument_list|,
operator|new
name|String
index|[]
block|{
name|destinationFile
block|}
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|delService
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|scheduleFileDeletionTask
argument_list|(
name|argThat
argument_list|(
operator|new
name|FileDeletionInclude
argument_list|(
literal|null
argument_list|,
name|ContainerLocalizer
operator|.
name|USERCACHE
operator|+
literal|"_DEL_"
argument_list|,
operator|new
name|String
index|[]
block|{}
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|numOfLocalDirs (String localDir, String localSubDir)
specifier|private
name|int
name|numOfLocalDirs
parameter_list|(
name|String
name|localDir
parameter_list|,
name|String
name|localSubDir
parameter_list|)
block|{
name|File
index|[]
name|listOfFiles
init|=
operator|new
name|File
argument_list|(
name|localDir
argument_list|,
name|localSubDir
argument_list|)
operator|.
name|listFiles
argument_list|()
decl_stmt|;
if|if
condition|(
name|listOfFiles
operator|==
literal|null
condition|)
block|{
return|return
literal|0
return|;
block|}
else|else
block|{
return|return
name|listOfFiles
operator|.
name|length
return|;
block|}
block|}
DECL|method|createFiles (String dir, String subDir, int numOfFiles)
specifier|private
name|void
name|createFiles
parameter_list|(
name|String
name|dir
parameter_list|,
name|String
name|subDir
parameter_list|,
name|int
name|numOfFiles
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numOfFiles
condition|;
name|i
operator|++
control|)
block|{
name|File
name|newFile
init|=
operator|new
name|File
argument_list|(
name|dir
operator|+
literal|"/"
operator|+
name|subDir
argument_list|,
literal|"file_"
operator|+
operator|(
name|i
operator|+
literal|1
operator|)
argument_list|)
decl_stmt|;
try|try
block|{
name|newFile
operator|.
name|createNewFile
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// Do nothing
block|}
block|}
block|}
DECL|method|createContainerId ()
specifier|private
name|ContainerId
name|createContainerId
parameter_list|()
block|{
name|ApplicationId
name|appId
init|=
name|ApplicationId
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|ApplicationAttemptId
name|appAttemptId
init|=
name|ApplicationAttemptId
operator|.
name|newInstance
argument_list|(
name|appId
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|ContainerId
name|containerId
init|=
name|ContainerId
operator|.
name|newInstance
argument_list|(
name|appAttemptId
argument_list|,
literal|0
argument_list|)
decl_stmt|;
return|return
name|containerId
return|;
block|}
DECL|class|MyNodeManager
specifier|private
class|class
name|MyNodeManager
extends|extends
name|NodeManager
block|{
DECL|method|MyNodeManager ()
specifier|public
name|MyNodeManager
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|init
argument_list|(
name|createNMConfig
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createNodeStatusUpdater (Context context, Dispatcher dispatcher, NodeHealthCheckerService healthChecker)
specifier|protected
name|NodeStatusUpdater
name|createNodeStatusUpdater
parameter_list|(
name|Context
name|context
parameter_list|,
name|Dispatcher
name|dispatcher
parameter_list|,
name|NodeHealthCheckerService
name|healthChecker
parameter_list|)
block|{
name|MockNodeStatusUpdater
name|myNodeStatusUpdater
init|=
operator|new
name|MockNodeStatusUpdater
argument_list|(
name|context
argument_list|,
name|dispatcher
argument_list|,
name|healthChecker
argument_list|,
name|metrics
argument_list|)
decl_stmt|;
return|return
name|myNodeStatusUpdater
return|;
block|}
annotation|@
name|Override
DECL|method|createDeletionService (ContainerExecutor exec)
specifier|protected
name|DeletionService
name|createDeletionService
parameter_list|(
name|ContainerExecutor
name|exec
parameter_list|)
block|{
name|delService
operator|=
name|spy
argument_list|(
operator|new
name|DeletionService
argument_list|(
name|exec
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|delService
return|;
block|}
DECL|method|createNMConfig ()
specifier|private
name|YarnConfiguration
name|createNMConfig
parameter_list|()
block|{
name|YarnConfiguration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|YarnConfiguration
operator|.
name|NM_PMEM_MB
argument_list|,
literal|5
operator|*
literal|1024
argument_list|)
expr_stmt|;
comment|// 5GB
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|NM_ADDRESS
argument_list|,
literal|"127.0.0.1:12345"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|NM_LOCALIZER_ADDRESS
argument_list|,
literal|"127.0.0.1:12346"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|NM_LOG_DIRS
argument_list|,
name|logsDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|NM_LOCAL_DIRS
argument_list|,
name|nmLocalDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|conf
return|;
block|}
block|}
DECL|class|PathInclude
class|class
name|PathInclude
extends|extends
name|ArgumentMatcher
argument_list|<
name|Path
argument_list|>
block|{
DECL|field|part
specifier|final
name|String
name|part
decl_stmt|;
DECL|method|PathInclude (String part)
name|PathInclude
parameter_list|(
name|String
name|part
parameter_list|)
block|{
name|this
operator|.
name|part
operator|=
name|part
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|matches (Object o)
specifier|public
name|boolean
name|matches
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
return|return
operator|(
operator|(
name|Path
operator|)
name|o
operator|)
operator|.
name|getName
argument_list|()
operator|.
name|indexOf
argument_list|(
name|part
argument_list|)
operator|!=
operator|-
literal|1
return|;
block|}
block|}
DECL|class|FileDeletionInclude
class|class
name|FileDeletionInclude
extends|extends
name|ArgumentMatcher
argument_list|<
name|FileDeletionTask
argument_list|>
block|{
DECL|field|user
specifier|final
name|String
name|user
decl_stmt|;
DECL|field|subDirIncludes
specifier|final
name|String
name|subDirIncludes
decl_stmt|;
DECL|field|baseDirIncludes
specifier|final
name|String
index|[]
name|baseDirIncludes
decl_stmt|;
DECL|method|FileDeletionInclude (String user, String subDirIncludes, String [] baseDirIncludes)
specifier|public
name|FileDeletionInclude
parameter_list|(
name|String
name|user
parameter_list|,
name|String
name|subDirIncludes
parameter_list|,
name|String
index|[]
name|baseDirIncludes
parameter_list|)
block|{
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
name|this
operator|.
name|subDirIncludes
operator|=
name|subDirIncludes
expr_stmt|;
name|this
operator|.
name|baseDirIncludes
operator|=
name|baseDirIncludes
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|matches (Object o)
specifier|public
name|boolean
name|matches
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
name|FileDeletionTask
name|fd
init|=
operator|(
name|FileDeletionTask
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|fd
operator|.
name|getUser
argument_list|()
operator|==
literal|null
operator|&&
name|user
operator|!=
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
name|fd
operator|.
name|getUser
argument_list|()
operator|!=
literal|null
operator|&&
name|user
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
name|fd
operator|.
name|getUser
argument_list|()
operator|!=
literal|null
operator|&&
name|user
operator|!=
literal|null
condition|)
block|{
return|return
name|fd
operator|.
name|getUser
argument_list|()
operator|.
name|equals
argument_list|(
name|user
argument_list|)
return|;
block|}
if|if
condition|(
operator|!
name|comparePaths
argument_list|(
name|fd
operator|.
name|getSubDir
argument_list|()
argument_list|,
name|subDirIncludes
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|baseDirIncludes
operator|==
literal|null
operator|&&
name|fd
operator|.
name|getBaseDirs
argument_list|()
operator|!=
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
name|baseDirIncludes
operator|!=
literal|null
operator|&&
name|fd
operator|.
name|getBaseDirs
argument_list|()
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
name|baseDirIncludes
operator|!=
literal|null
operator|&&
name|fd
operator|.
name|getBaseDirs
argument_list|()
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|baseDirIncludes
operator|.
name|length
operator|!=
name|fd
operator|.
name|getBaseDirs
argument_list|()
operator|.
name|size
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|baseDirIncludes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|comparePaths
argument_list|(
name|fd
operator|.
name|getBaseDirs
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|baseDirIncludes
index|[
name|i
index|]
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
return|return
literal|true
return|;
block|}
DECL|method|comparePaths (Path p1, String p2)
specifier|public
name|boolean
name|comparePaths
parameter_list|(
name|Path
name|p1
parameter_list|,
name|String
name|p2
parameter_list|)
block|{
if|if
condition|(
name|p1
operator|==
literal|null
operator|&&
name|p2
operator|!=
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
name|p1
operator|!=
literal|null
operator|&&
name|p2
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
name|p1
operator|!=
literal|null
operator|&&
name|p2
operator|!=
literal|null
condition|)
block|{
return|return
name|p1
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
operator|.
name|contains
argument_list|(
name|p2
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
return|return
literal|true
return|;
block|}
block|}
block|}
end_class

end_unit

