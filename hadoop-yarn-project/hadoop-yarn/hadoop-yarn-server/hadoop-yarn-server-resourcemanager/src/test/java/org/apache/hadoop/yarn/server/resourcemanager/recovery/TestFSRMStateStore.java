begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.recovery
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
name|resourcemanager
operator|.
name|recovery
package|;
end_package

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
import|import
name|junit
operator|.
name|framework
operator|.
name|Assert
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
name|FSDataOutputStream
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
name|FileStatus
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
name|hdfs
operator|.
name|HdfsConfiguration
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
name|hdfs
operator|.
name|MiniDFSCluster
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
name|util
operator|.
name|ConverterUtils
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
DECL|class|TestFSRMStateStore
specifier|public
class|class
name|TestFSRMStateStore
extends|extends
name|RMStateStoreTestBase
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TestFSRMStateStore
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|class|TestFSRMStateStoreTester
class|class
name|TestFSRMStateStoreTester
implements|implements
name|RMStateStoreHelper
block|{
DECL|field|workingDirPathURI
name|Path
name|workingDirPathURI
decl_stmt|;
DECL|field|store
name|FileSystemRMStateStore
name|store
decl_stmt|;
DECL|field|cluster
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|class|TestFileSystemRMStore
class|class
name|TestFileSystemRMStore
extends|extends
name|FileSystemRMStateStore
block|{
DECL|method|TestFileSystemRMStore (Configuration conf)
name|TestFileSystemRMStore
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|Exception
block|{
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNull
argument_list|(
name|fs
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|workingDirPathURI
operator|.
name|equals
argument_list|(
name|fsWorkingPath
argument_list|)
argument_list|)
expr_stmt|;
name|start
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|fs
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|TestFSRMStateStoreTester (MiniDFSCluster cluster)
specifier|public
name|TestFSRMStateStoreTester
parameter_list|(
name|MiniDFSCluster
name|cluster
parameter_list|)
throws|throws
name|Exception
block|{
name|Path
name|workingDirPath
init|=
operator|new
name|Path
argument_list|(
literal|"/Test"
argument_list|)
decl_stmt|;
name|this
operator|.
name|cluster
operator|=
name|cluster
expr_stmt|;
name|FileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|workingDirPath
argument_list|)
expr_stmt|;
name|Path
name|clusterURI
init|=
operator|new
name|Path
argument_list|(
name|cluster
operator|.
name|getURI
argument_list|()
argument_list|)
decl_stmt|;
name|workingDirPathURI
operator|=
operator|new
name|Path
argument_list|(
name|clusterURI
argument_list|,
name|workingDirPath
argument_list|)
expr_stmt|;
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getRMStateStore ()
specifier|public
name|RMStateStore
name|getRMStateStore
parameter_list|()
throws|throws
name|Exception
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
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|FS_RM_STATE_STORE_URI
argument_list|,
name|workingDirPathURI
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|store
operator|=
operator|new
name|TestFileSystemRMStore
argument_list|(
name|conf
argument_list|)
expr_stmt|;
return|return
name|store
return|;
block|}
annotation|@
name|Override
DECL|method|isFinalStateValid ()
specifier|public
name|boolean
name|isFinalStateValid
parameter_list|()
throws|throws
name|Exception
block|{
name|FileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|FileStatus
index|[]
name|files
init|=
name|fs
operator|.
name|listStatus
argument_list|(
name|workingDirPathURI
argument_list|)
decl_stmt|;
return|return
name|files
operator|.
name|length
operator|==
literal|1
return|;
block|}
block|}
annotation|@
name|Test
DECL|method|testFSRMStateStore ()
specifier|public
name|void
name|testFSRMStateStore
parameter_list|()
throws|throws
name|Exception
block|{
name|HdfsConfiguration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|MiniDFSCluster
name|cluster
init|=
operator|new
name|MiniDFSCluster
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
name|build
argument_list|()
decl_stmt|;
try|try
block|{
name|TestFSRMStateStoreTester
name|fsTester
init|=
operator|new
name|TestFSRMStateStoreTester
argument_list|(
name|cluster
argument_list|)
decl_stmt|;
comment|// If the state store is FileSystemRMStateStore then add corrupted entry.
comment|// It should discard the entry and remove it from file system.
name|FSDataOutputStream
name|fsOut
init|=
literal|null
decl_stmt|;
name|FileSystemRMStateStore
name|fileSystemRMStateStore
init|=
operator|(
name|FileSystemRMStateStore
operator|)
name|fsTester
operator|.
name|getRMStateStore
argument_list|()
decl_stmt|;
name|String
name|appAttemptIdStr3
init|=
literal|"appattempt_1352994193343_0001_000003"
decl_stmt|;
name|ApplicationAttemptId
name|attemptId3
init|=
name|ConverterUtils
operator|.
name|toApplicationAttemptId
argument_list|(
name|appAttemptIdStr3
argument_list|)
decl_stmt|;
name|Path
name|rootDir
init|=
operator|new
name|Path
argument_list|(
name|fileSystemRMStateStore
operator|.
name|fsWorkingPath
argument_list|,
literal|"FSRMStateRoot"
argument_list|)
decl_stmt|;
name|Path
name|appRootDir
init|=
operator|new
name|Path
argument_list|(
name|rootDir
argument_list|,
literal|"RMAppRoot"
argument_list|)
decl_stmt|;
name|Path
name|appDir
init|=
operator|new
name|Path
argument_list|(
name|appRootDir
argument_list|,
name|attemptId3
operator|.
name|getApplicationId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|Path
name|tempAppAttemptFile
init|=
operator|new
name|Path
argument_list|(
name|appDir
argument_list|,
name|attemptId3
operator|.
name|toString
argument_list|()
operator|+
literal|".tmp"
argument_list|)
decl_stmt|;
name|fsOut
operator|=
name|fileSystemRMStateStore
operator|.
name|fs
operator|.
name|create
argument_list|(
name|tempAppAttemptFile
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|fsOut
operator|.
name|write
argument_list|(
literal|"Some random data "
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|fsOut
operator|.
name|close
argument_list|()
expr_stmt|;
name|testRMAppStateStore
argument_list|(
name|fsTester
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|fileSystemRMStateStore
operator|.
name|fsWorkingPath
operator|.
name|getFileSystem
argument_list|(
name|conf
argument_list|)
operator|.
name|exists
argument_list|(
name|tempAppAttemptFile
argument_list|)
argument_list|)
expr_stmt|;
name|testRMDTSecretManagerStateStore
argument_list|(
name|fsTester
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

