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
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
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
name|server
operator|.
name|resourcemanager
operator|.
name|recovery
operator|.
name|records
operator|.
name|RMStateVersion
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
name|resourcemanager
operator|.
name|recovery
operator|.
name|records
operator|.
name|impl
operator|.
name|pb
operator|.
name|ApplicationStateDataPBImpl
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
name|resourcemanager
operator|.
name|recovery
operator|.
name|records
operator|.
name|impl
operator|.
name|pb
operator|.
name|RMStateVersionPBImpl
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
name|resourcemanager
operator|.
name|rmapp
operator|.
name|RMApp
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
name|resourcemanager
operator|.
name|rmapp
operator|.
name|RMAppState
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
DECL|field|fsTester
specifier|private
name|TestFSRMStateStoreTester
name|fsTester
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
name|TestFileSystemRMStore
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
DECL|method|getVersionNode ()
specifier|public
name|Path
name|getVersionNode
parameter_list|()
block|{
return|return
operator|new
name|Path
argument_list|(
operator|new
name|Path
argument_list|(
name|workingDirPathURI
argument_list|,
name|ROOT_DIR_NAME
argument_list|)
argument_list|,
name|VERSION_NODE
argument_list|)
return|;
block|}
DECL|method|getCurrentVersion ()
specifier|public
name|RMStateVersion
name|getCurrentVersion
parameter_list|()
block|{
return|return
name|CURRENT_VERSION_INFO
return|;
block|}
DECL|method|getAppDir (String appId)
specifier|public
name|Path
name|getAppDir
parameter_list|(
name|String
name|appId
parameter_list|)
block|{
name|Path
name|rootDir
init|=
operator|new
name|Path
argument_list|(
name|workingDirPathURI
argument_list|,
name|ROOT_DIR_NAME
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
name|RM_APP_ROOT
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
name|appId
argument_list|)
decl_stmt|;
return|return
name|appDir
return|;
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
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|FS_RM_STATE_STORE_RETRY_POLICY_SPEC
argument_list|,
literal|"100,6000"
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
annotation|@
name|Override
DECL|method|writeVersion (RMStateVersion version)
specifier|public
name|void
name|writeVersion
parameter_list|(
name|RMStateVersion
name|version
parameter_list|)
throws|throws
name|Exception
block|{
name|store
operator|.
name|updateFile
argument_list|(
name|store
operator|.
name|getVersionNode
argument_list|()
argument_list|,
operator|(
operator|(
name|RMStateVersionPBImpl
operator|)
name|version
operator|)
operator|.
name|getProto
argument_list|()
operator|.
name|toByteArray
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getCurrentVersion ()
specifier|public
name|RMStateVersion
name|getCurrentVersion
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|store
operator|.
name|getCurrentVersion
argument_list|()
return|;
block|}
DECL|method|appExists (RMApp app)
specifier|public
name|boolean
name|appExists
parameter_list|(
name|RMApp
name|app
parameter_list|)
throws|throws
name|IOException
block|{
name|FileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|Path
name|nodePath
init|=
name|store
operator|.
name|getAppDir
argument_list|(
name|app
operator|.
name|getApplicationId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|fs
operator|.
name|exists
argument_list|(
name|nodePath
argument_list|)
return|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
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
name|fsTester
operator|=
operator|new
name|TestFSRMStateStoreTester
argument_list|(
name|cluster
argument_list|)
expr_stmt|;
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
name|appDir
init|=
name|fsTester
operator|.
name|store
operator|.
name|getAppDir
argument_list|(
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
name|fsTester
operator|.
name|workingDirPathURI
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
name|testCheckVersion
argument_list|(
name|fsTester
argument_list|)
expr_stmt|;
name|testAppDeletion
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
annotation|@
name|Override
DECL|method|modifyAppState ()
specifier|protected
name|void
name|modifyAppState
parameter_list|()
throws|throws
name|Exception
block|{
comment|// imitate appAttemptFile1 is still .new, but old one is deleted
name|String
name|appAttemptIdStr1
init|=
literal|"appattempt_1352994193343_0001_000001"
decl_stmt|;
name|ApplicationAttemptId
name|attemptId1
init|=
name|ConverterUtils
operator|.
name|toApplicationAttemptId
argument_list|(
name|appAttemptIdStr1
argument_list|)
decl_stmt|;
name|Path
name|appDir
init|=
name|fsTester
operator|.
name|store
operator|.
name|getAppDir
argument_list|(
name|attemptId1
operator|.
name|getApplicationId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|Path
name|appAttemptFile1
init|=
operator|new
name|Path
argument_list|(
name|appDir
argument_list|,
name|attemptId1
operator|.
name|toString
argument_list|()
operator|+
literal|".new"
argument_list|)
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
name|fileSystemRMStateStore
operator|.
name|renameFile
argument_list|(
name|appAttemptFile1
argument_list|,
operator|new
name|Path
argument_list|(
name|appAttemptFile1
operator|.
name|getParent
argument_list|()
argument_list|,
name|appAttemptFile1
operator|.
name|getName
argument_list|()
operator|+
literal|".new"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|modifyRMDelegationTokenState ()
specifier|protected
name|void
name|modifyRMDelegationTokenState
parameter_list|()
throws|throws
name|Exception
block|{
comment|// imitate dt file is still .new, but old one is deleted
name|Path
name|nodeCreatePath
init|=
name|fsTester
operator|.
name|store
operator|.
name|getNodePath
argument_list|(
name|fsTester
operator|.
name|store
operator|.
name|rmDTSecretManagerRoot
argument_list|,
name|FileSystemRMStateStore
operator|.
name|DELEGATION_TOKEN_PREFIX
operator|+
literal|0
argument_list|)
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
name|fileSystemRMStateStore
operator|.
name|renameFile
argument_list|(
name|nodeCreatePath
argument_list|,
operator|new
name|Path
argument_list|(
name|nodeCreatePath
operator|.
name|getParent
argument_list|()
argument_list|,
name|nodeCreatePath
operator|.
name|getName
argument_list|()
operator|+
literal|".new"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
DECL|method|testFSRMStateStoreClientRetry ()
specifier|public
name|void
name|testFSRMStateStoreClientRetry
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
literal|2
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
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
specifier|final
name|RMStateStore
name|store
init|=
name|fsTester
operator|.
name|getRMStateStore
argument_list|()
decl_stmt|;
name|store
operator|.
name|setRMDispatcher
argument_list|(
operator|new
name|TestDispatcher
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|AtomicBoolean
name|assertionFailedInThread
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|cluster
operator|.
name|shutdownNameNodes
argument_list|()
expr_stmt|;
name|Thread
name|clientThread
init|=
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|store
operator|.
name|storeApplicationStateInternal
argument_list|(
name|ApplicationId
operator|.
name|newInstance
argument_list|(
literal|100L
argument_list|,
literal|1
argument_list|)
argument_list|,
operator|(
name|ApplicationStateDataPBImpl
operator|)
name|ApplicationStateDataPBImpl
operator|.
name|newApplicationStateData
argument_list|(
literal|111
argument_list|,
literal|111
argument_list|,
literal|"user"
argument_list|,
literal|null
argument_list|,
name|RMAppState
operator|.
name|ACCEPTED
argument_list|,
literal|"diagnostics"
argument_list|,
literal|333
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// TODO 0 datanode exception will not be retried by dfs client, fix
comment|// that separately.
if|if
condition|(
operator|!
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"could only be replicated"
operator|+
literal|" to 0 nodes instead of minReplication (=1)"
argument_list|)
condition|)
block|{
name|assertionFailedInThread
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
name|clientThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|restartNameNode
argument_list|()
expr_stmt|;
name|clientThread
operator|.
name|join
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|assertionFailedInThread
operator|.
name|get
argument_list|()
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

