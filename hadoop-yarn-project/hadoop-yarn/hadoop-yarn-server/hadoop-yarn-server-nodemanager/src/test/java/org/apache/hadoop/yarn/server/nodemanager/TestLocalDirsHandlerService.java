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
name|io
operator|.
name|FileUtils
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
name|conf
operator|.
name|Configuration
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
name|CommonConfigurationKeys
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
name|FileUtil
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
name|permission
operator|.
name|FsPermission
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
name|service
operator|.
name|Service
operator|.
name|STATE
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
name|exceptions
operator|.
name|YarnRuntimeException
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

begin_class
DECL|class|TestLocalDirsHandlerService
specifier|public
class|class
name|TestLocalDirsHandlerService
block|{
DECL|field|testDir
specifier|private
specifier|static
specifier|final
name|File
name|testDir
init|=
operator|new
name|File
argument_list|(
literal|"target"
argument_list|,
name|TestDirectoryCollection
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|getAbsoluteFile
argument_list|()
decl_stmt|;
DECL|field|testFile
specifier|private
specifier|static
specifier|final
name|File
name|testFile
init|=
operator|new
name|File
argument_list|(
name|testDir
argument_list|,
literal|"testfile"
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
name|IOException
block|{
name|testDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|testFile
operator|.
name|createNewFile
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|teardown ()
specifier|public
name|void
name|teardown
parameter_list|()
block|{
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|testDir
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDirStructure ()
specifier|public
name|void
name|testDirStructure
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|String
name|localDir1
init|=
operator|new
name|File
argument_list|(
literal|"file:///"
operator|+
name|testDir
argument_list|,
literal|"localDir1"
argument_list|)
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|NM_LOCAL_DIRS
argument_list|,
name|localDir1
argument_list|)
expr_stmt|;
name|String
name|logDir1
init|=
operator|new
name|File
argument_list|(
literal|"file:///"
operator|+
name|testDir
argument_list|,
literal|"logDir1"
argument_list|)
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|NM_LOG_DIRS
argument_list|,
name|logDir1
argument_list|)
expr_stmt|;
name|LocalDirsHandlerService
name|dirSvc
init|=
operator|new
name|LocalDirsHandlerService
argument_list|()
decl_stmt|;
name|dirSvc
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|dirSvc
operator|.
name|getLocalDirs
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|dirSvc
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testValidPathsDirHandlerService ()
specifier|public
name|void
name|testValidPathsDirHandlerService
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|String
name|localDir1
init|=
operator|new
name|File
argument_list|(
literal|"file:///"
operator|+
name|testDir
argument_list|,
literal|"localDir1"
argument_list|)
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|String
name|localDir2
init|=
operator|new
name|File
argument_list|(
literal|"hdfs:///"
operator|+
name|testDir
argument_list|,
literal|"localDir2"
argument_list|)
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|NM_LOCAL_DIRS
argument_list|,
name|localDir1
operator|+
literal|","
operator|+
name|localDir2
argument_list|)
expr_stmt|;
name|String
name|logDir1
init|=
operator|new
name|File
argument_list|(
literal|"file:///"
operator|+
name|testDir
argument_list|,
literal|"logDir1"
argument_list|)
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|NM_LOG_DIRS
argument_list|,
name|logDir1
argument_list|)
expr_stmt|;
name|LocalDirsHandlerService
name|dirSvc
init|=
operator|new
name|LocalDirsHandlerService
argument_list|()
decl_stmt|;
try|try
block|{
name|dirSvc
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Service should have thrown an exception due to wrong URI"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnRuntimeException
name|e
parameter_list|)
block|{     }
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Service should not be inited"
argument_list|,
name|STATE
operator|.
name|STOPPED
argument_list|,
name|dirSvc
operator|.
name|getServiceState
argument_list|()
argument_list|)
expr_stmt|;
name|dirSvc
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetFullDirs ()
specifier|public
name|void
name|testGetFullDirs
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|CommonConfigurationKeys
operator|.
name|FS_PERMISSIONS_UMASK_KEY
argument_list|,
literal|"077"
argument_list|)
expr_stmt|;
name|FileContext
name|localFs
init|=
name|FileContext
operator|.
name|getLocalFSFileContext
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|String
name|localDir1
init|=
operator|new
name|File
argument_list|(
name|testDir
argument_list|,
literal|"localDir1"
argument_list|)
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|String
name|localDir2
init|=
operator|new
name|File
argument_list|(
name|testDir
argument_list|,
literal|"localDir2"
argument_list|)
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|String
name|logDir1
init|=
operator|new
name|File
argument_list|(
name|testDir
argument_list|,
literal|"logDir1"
argument_list|)
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|String
name|logDir2
init|=
operator|new
name|File
argument_list|(
name|testDir
argument_list|,
literal|"logDir2"
argument_list|)
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|Path
name|localDir1Path
init|=
operator|new
name|Path
argument_list|(
name|localDir1
argument_list|)
decl_stmt|;
name|Path
name|logDir1Path
init|=
operator|new
name|Path
argument_list|(
name|logDir1
argument_list|)
decl_stmt|;
name|FsPermission
name|dirPermissions
init|=
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
literal|0410
argument_list|)
decl_stmt|;
name|localFs
operator|.
name|mkdir
argument_list|(
name|localDir1Path
argument_list|,
name|dirPermissions
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|localFs
operator|.
name|mkdir
argument_list|(
name|logDir1Path
argument_list|,
name|dirPermissions
argument_list|,
literal|true
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
name|localDir1
operator|+
literal|","
operator|+
name|localDir2
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
name|logDir1
operator|+
literal|","
operator|+
name|logDir2
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setFloat
argument_list|(
name|YarnConfiguration
operator|.
name|NM_MAX_PER_DISK_UTILIZATION_PERCENTAGE
argument_list|,
literal|0.0f
argument_list|)
expr_stmt|;
name|LocalDirsHandlerService
name|dirSvc
init|=
operator|new
name|LocalDirsHandlerService
argument_list|()
decl_stmt|;
name|dirSvc
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|dirSvc
operator|.
name|getLocalDirs
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|dirSvc
operator|.
name|getLogDirs
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|dirSvc
operator|.
name|getDiskFullLocalDirs
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|dirSvc
operator|.
name|getDiskFullLogDirs
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
operator|new
name|File
argument_list|(
name|localDir1
argument_list|)
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
operator|new
name|File
argument_list|(
name|localDir2
argument_list|)
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
operator|new
name|File
argument_list|(
name|logDir1
argument_list|)
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
operator|new
name|File
argument_list|(
name|logDir1
argument_list|)
argument_list|)
expr_stmt|;
name|dirSvc
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

