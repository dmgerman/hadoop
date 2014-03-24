begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|FileContextTestHelper
operator|.
name|exists
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
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|login
operator|.
name|LoginException
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
name|Options
operator|.
name|Rename
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
name|DistributedFileSystem
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
name|hdfs
operator|.
name|protocol
operator|.
name|HdfsConstants
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
name|ipc
operator|.
name|RemoteException
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
name|Test
import|;
end_import

begin_class
DECL|class|TestHDFSFileContextMainOperations
specifier|public
class|class
name|TestHDFSFileContextMainOperations
extends|extends
name|FileContextMainOperationsBaseTest
block|{
DECL|field|cluster
specifier|private
specifier|static
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|defaultWorkingDirectory
specifier|private
specifier|static
name|Path
name|defaultWorkingDirectory
decl_stmt|;
DECL|field|CONF
specifier|private
specifier|static
specifier|final
name|HdfsConfiguration
name|CONF
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|createFileContextHelper ()
specifier|protected
name|FileContextTestHelper
name|createFileContextHelper
parameter_list|()
block|{
return|return
operator|new
name|FileContextTestHelper
argument_list|()
return|;
block|}
annotation|@
name|BeforeClass
DECL|method|clusterSetupAtBegining ()
specifier|public
specifier|static
name|void
name|clusterSetupAtBegining
parameter_list|()
throws|throws
name|IOException
throws|,
name|LoginException
throws|,
name|URISyntaxException
block|{
name|cluster
operator|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|CONF
argument_list|)
operator|.
name|numDataNodes
argument_list|(
literal|2
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|waitClusterUp
argument_list|()
expr_stmt|;
name|URI
name|uri0
init|=
name|cluster
operator|.
name|getURI
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|fc
operator|=
name|FileContext
operator|.
name|getFileContext
argument_list|(
name|uri0
argument_list|,
name|CONF
argument_list|)
expr_stmt|;
name|defaultWorkingDirectory
operator|=
name|fc
operator|.
name|makeQualified
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/user/"
operator|+
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|getShortUserName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|fc
operator|.
name|mkdir
argument_list|(
name|defaultWorkingDirectory
argument_list|,
name|FileContext
operator|.
name|DEFAULT_PERM
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|restartCluster ()
specifier|private
specifier|static
name|void
name|restartCluster
parameter_list|()
throws|throws
name|IOException
throws|,
name|LoginException
block|{
if|if
condition|(
name|cluster
operator|!=
literal|null
condition|)
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|cluster
operator|=
literal|null
expr_stmt|;
block|}
name|cluster
operator|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|CONF
argument_list|)
operator|.
name|numDataNodes
argument_list|(
literal|1
argument_list|)
operator|.
name|format
argument_list|(
literal|false
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|waitClusterUp
argument_list|()
expr_stmt|;
name|fc
operator|=
name|FileContext
operator|.
name|getFileContext
argument_list|(
name|cluster
operator|.
name|getURI
argument_list|(
literal|0
argument_list|)
argument_list|,
name|CONF
argument_list|)
expr_stmt|;
name|defaultWorkingDirectory
operator|=
name|fc
operator|.
name|makeQualified
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/user/"
operator|+
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|getShortUserName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|fc
operator|.
name|mkdir
argument_list|(
name|defaultWorkingDirectory
argument_list|,
name|FileContext
operator|.
name|DEFAULT_PERM
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|ClusterShutdownAtEnd ()
specifier|public
specifier|static
name|void
name|ClusterShutdownAtEnd
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|cluster
operator|!=
literal|null
condition|)
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|cluster
operator|=
literal|null
expr_stmt|;
block|}
block|}
annotation|@
name|Override
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|After
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getDefaultWorkingDirectory ()
specifier|protected
name|Path
name|getDefaultWorkingDirectory
parameter_list|()
block|{
return|return
name|defaultWorkingDirectory
return|;
block|}
annotation|@
name|Override
DECL|method|unwrapException (IOException e)
specifier|protected
name|IOException
name|unwrapException
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|instanceof
name|RemoteException
condition|)
block|{
return|return
operator|(
operator|(
name|RemoteException
operator|)
name|e
operator|)
operator|.
name|unwrapRemoteException
argument_list|()
return|;
block|}
return|return
name|e
return|;
block|}
DECL|method|getTestRootPath (FileContext fc, String path)
specifier|private
name|Path
name|getTestRootPath
parameter_list|(
name|FileContext
name|fc
parameter_list|,
name|String
name|path
parameter_list|)
block|{
return|return
name|fileContextTestHelper
operator|.
name|getTestRootPath
argument_list|(
name|fc
argument_list|,
name|path
argument_list|)
return|;
block|}
annotation|@
name|Test
DECL|method|testOldRenameWithQuota ()
specifier|public
name|void
name|testOldRenameWithQuota
parameter_list|()
throws|throws
name|Exception
block|{
name|DistributedFileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|Path
name|src1
init|=
name|getTestRootPath
argument_list|(
name|fc
argument_list|,
literal|"test/testOldRenameWithQuota/srcdir/src1"
argument_list|)
decl_stmt|;
name|Path
name|src2
init|=
name|getTestRootPath
argument_list|(
name|fc
argument_list|,
literal|"test/testOldRenameWithQuota/srcdir/src2"
argument_list|)
decl_stmt|;
name|Path
name|dst1
init|=
name|getTestRootPath
argument_list|(
name|fc
argument_list|,
literal|"test/testOldRenameWithQuota/dstdir/dst1"
argument_list|)
decl_stmt|;
name|Path
name|dst2
init|=
name|getTestRootPath
argument_list|(
name|fc
argument_list|,
literal|"test/testOldRenameWithQuota/dstdir/dst2"
argument_list|)
decl_stmt|;
name|createFile
argument_list|(
name|src1
argument_list|)
expr_stmt|;
name|createFile
argument_list|(
name|src2
argument_list|)
expr_stmt|;
name|fs
operator|.
name|setQuota
argument_list|(
name|src1
operator|.
name|getParent
argument_list|()
argument_list|,
name|HdfsConstants
operator|.
name|QUOTA_DONT_SET
argument_list|,
name|HdfsConstants
operator|.
name|QUOTA_DONT_SET
argument_list|)
expr_stmt|;
name|fc
operator|.
name|mkdir
argument_list|(
name|dst1
operator|.
name|getParent
argument_list|()
argument_list|,
name|FileContext
operator|.
name|DEFAULT_PERM
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fs
operator|.
name|setQuota
argument_list|(
name|dst1
operator|.
name|getParent
argument_list|()
argument_list|,
literal|2
argument_list|,
name|HdfsConstants
operator|.
name|QUOTA_DONT_SET
argument_list|)
expr_stmt|;
comment|/*       * Test1: src does not exceed quota and dst has no quota check and hence       * accommodates rename      */
name|oldRename
argument_list|(
name|src1
argument_list|,
name|dst1
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|/*      * Test2: src does not exceed quota and dst has *no* quota to accommodate       * rename.       */
comment|// dstDir quota = 1 and dst1 already uses it
name|oldRename
argument_list|(
name|src2
argument_list|,
name|dst2
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|/*      * Test3: src exceeds quota and dst has *no* quota to accommodate rename      */
comment|// src1 has no quota to accommodate new rename node
name|fs
operator|.
name|setQuota
argument_list|(
name|src1
operator|.
name|getParent
argument_list|()
argument_list|,
literal|1
argument_list|,
name|HdfsConstants
operator|.
name|QUOTA_DONT_SET
argument_list|)
expr_stmt|;
name|oldRename
argument_list|(
name|dst1
argument_list|,
name|src1
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRenameWithQuota ()
specifier|public
name|void
name|testRenameWithQuota
parameter_list|()
throws|throws
name|Exception
block|{
name|DistributedFileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|Path
name|src1
init|=
name|getTestRootPath
argument_list|(
name|fc
argument_list|,
literal|"test/testRenameWithQuota/srcdir/src1"
argument_list|)
decl_stmt|;
name|Path
name|src2
init|=
name|getTestRootPath
argument_list|(
name|fc
argument_list|,
literal|"test/testRenameWithQuota/srcdir/src2"
argument_list|)
decl_stmt|;
name|Path
name|dst1
init|=
name|getTestRootPath
argument_list|(
name|fc
argument_list|,
literal|"test/testRenameWithQuota/dstdir/dst1"
argument_list|)
decl_stmt|;
name|Path
name|dst2
init|=
name|getTestRootPath
argument_list|(
name|fc
argument_list|,
literal|"test/testRenameWithQuota/dstdir/dst2"
argument_list|)
decl_stmt|;
name|createFile
argument_list|(
name|src1
argument_list|)
expr_stmt|;
name|createFile
argument_list|(
name|src2
argument_list|)
expr_stmt|;
name|fs
operator|.
name|setQuota
argument_list|(
name|src1
operator|.
name|getParent
argument_list|()
argument_list|,
name|HdfsConstants
operator|.
name|QUOTA_DONT_SET
argument_list|,
name|HdfsConstants
operator|.
name|QUOTA_DONT_SET
argument_list|)
expr_stmt|;
name|fc
operator|.
name|mkdir
argument_list|(
name|dst1
operator|.
name|getParent
argument_list|()
argument_list|,
name|FileContext
operator|.
name|DEFAULT_PERM
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fs
operator|.
name|setQuota
argument_list|(
name|dst1
operator|.
name|getParent
argument_list|()
argument_list|,
literal|2
argument_list|,
name|HdfsConstants
operator|.
name|QUOTA_DONT_SET
argument_list|)
expr_stmt|;
comment|/*       * Test1: src does not exceed quota and dst has no quota check and hence       * accommodates rename      */
comment|// rename uses dstdir quota=1
name|rename
argument_list|(
name|src1
argument_list|,
name|dst1
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
name|Rename
operator|.
name|NONE
argument_list|)
expr_stmt|;
comment|// rename reuses dstdir quota=1
name|rename
argument_list|(
name|src2
argument_list|,
name|dst1
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
name|Rename
operator|.
name|OVERWRITE
argument_list|)
expr_stmt|;
comment|/*      * Test2: src does not exceed quota and dst has *no* quota to accommodate       * rename.       */
comment|// dstDir quota = 1 and dst1 already uses it
name|createFile
argument_list|(
name|src2
argument_list|)
expr_stmt|;
name|rename
argument_list|(
name|src2
argument_list|,
name|dst2
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
name|Rename
operator|.
name|NONE
argument_list|)
expr_stmt|;
comment|/*      * Test3: src exceeds quota and dst has *no* quota to accommodate rename      * rename to a destination that does not exist      */
comment|// src1 has no quota to accommodate new rename node
name|fs
operator|.
name|setQuota
argument_list|(
name|src1
operator|.
name|getParent
argument_list|()
argument_list|,
literal|1
argument_list|,
name|HdfsConstants
operator|.
name|QUOTA_DONT_SET
argument_list|)
expr_stmt|;
name|rename
argument_list|(
name|dst1
argument_list|,
name|src1
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
name|Rename
operator|.
name|NONE
argument_list|)
expr_stmt|;
comment|/*      * Test4: src exceeds quota and dst has *no* quota to accommodate rename      * rename to a destination that exists and quota freed by deletion of dst      * is same as quota needed by src.      */
comment|// src1 has no quota to accommodate new rename node
name|fs
operator|.
name|setQuota
argument_list|(
name|src1
operator|.
name|getParent
argument_list|()
argument_list|,
literal|100
argument_list|,
name|HdfsConstants
operator|.
name|QUOTA_DONT_SET
argument_list|)
expr_stmt|;
name|createFile
argument_list|(
name|src1
argument_list|)
expr_stmt|;
name|fs
operator|.
name|setQuota
argument_list|(
name|src1
operator|.
name|getParent
argument_list|()
argument_list|,
literal|1
argument_list|,
name|HdfsConstants
operator|.
name|QUOTA_DONT_SET
argument_list|)
expr_stmt|;
name|rename
argument_list|(
name|dst1
argument_list|,
name|src1
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
name|Rename
operator|.
name|OVERWRITE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRenameRoot ()
specifier|public
name|void
name|testRenameRoot
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|src
init|=
name|getTestRootPath
argument_list|(
name|fc
argument_list|,
literal|"test/testRenameRoot/srcdir/src1"
argument_list|)
decl_stmt|;
name|Path
name|dst
init|=
operator|new
name|Path
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|createFile
argument_list|(
name|src
argument_list|)
expr_stmt|;
name|rename
argument_list|(
name|src
argument_list|,
name|dst
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
name|Rename
operator|.
name|OVERWRITE
argument_list|)
expr_stmt|;
name|rename
argument_list|(
name|dst
argument_list|,
name|src
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
name|Rename
operator|.
name|OVERWRITE
argument_list|)
expr_stmt|;
block|}
comment|/**    * Perform operations such as setting quota, deletion of files, rename and    * ensure system can apply edits log during startup.    */
annotation|@
name|Test
DECL|method|testEditsLogOldRename ()
specifier|public
name|void
name|testEditsLogOldRename
parameter_list|()
throws|throws
name|Exception
block|{
name|DistributedFileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|Path
name|src1
init|=
name|getTestRootPath
argument_list|(
name|fc
argument_list|,
literal|"testEditsLogOldRename/srcdir/src1"
argument_list|)
decl_stmt|;
name|Path
name|dst1
init|=
name|getTestRootPath
argument_list|(
name|fc
argument_list|,
literal|"testEditsLogOldRename/dstdir/dst1"
argument_list|)
decl_stmt|;
name|createFile
argument_list|(
name|src1
argument_list|)
expr_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|dst1
operator|.
name|getParent
argument_list|()
argument_list|)
expr_stmt|;
name|createFile
argument_list|(
name|dst1
argument_list|)
expr_stmt|;
comment|// Set quota so that dst1 parent cannot allow under it new files/directories
name|fs
operator|.
name|setQuota
argument_list|(
name|dst1
operator|.
name|getParent
argument_list|()
argument_list|,
literal|2
argument_list|,
name|HdfsConstants
operator|.
name|QUOTA_DONT_SET
argument_list|)
expr_stmt|;
comment|// Free up quota for a subsequent rename
name|fs
operator|.
name|delete
argument_list|(
name|dst1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|oldRename
argument_list|(
name|src1
argument_list|,
name|dst1
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// Restart the cluster and ensure the above operations can be
comment|// loaded from the edits log
name|restartCluster
argument_list|()
expr_stmt|;
name|fs
operator|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
name|src1
operator|=
name|getTestRootPath
argument_list|(
name|fc
argument_list|,
literal|"testEditsLogOldRename/srcdir/src1"
argument_list|)
expr_stmt|;
name|dst1
operator|=
name|getTestRootPath
argument_list|(
name|fc
argument_list|,
literal|"testEditsLogOldRename/dstdir/dst1"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|fs
operator|.
name|exists
argument_list|(
name|src1
argument_list|)
argument_list|)
expr_stmt|;
comment|// ensure src1 is already renamed
name|Assert
operator|.
name|assertTrue
argument_list|(
name|fs
operator|.
name|exists
argument_list|(
name|dst1
argument_list|)
argument_list|)
expr_stmt|;
comment|// ensure rename dst exists
block|}
comment|/**    * Perform operations such as setting quota, deletion of files, rename and    * ensure system can apply edits log during startup.    */
annotation|@
name|Test
DECL|method|testEditsLogRename ()
specifier|public
name|void
name|testEditsLogRename
parameter_list|()
throws|throws
name|Exception
block|{
name|DistributedFileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|Path
name|src1
init|=
name|getTestRootPath
argument_list|(
name|fc
argument_list|,
literal|"testEditsLogRename/srcdir/src1"
argument_list|)
decl_stmt|;
name|Path
name|dst1
init|=
name|getTestRootPath
argument_list|(
name|fc
argument_list|,
literal|"testEditsLogRename/dstdir/dst1"
argument_list|)
decl_stmt|;
name|createFile
argument_list|(
name|src1
argument_list|)
expr_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|dst1
operator|.
name|getParent
argument_list|()
argument_list|)
expr_stmt|;
name|createFile
argument_list|(
name|dst1
argument_list|)
expr_stmt|;
comment|// Set quota so that dst1 parent cannot allow under it new files/directories
name|fs
operator|.
name|setQuota
argument_list|(
name|dst1
operator|.
name|getParent
argument_list|()
argument_list|,
literal|2
argument_list|,
name|HdfsConstants
operator|.
name|QUOTA_DONT_SET
argument_list|)
expr_stmt|;
comment|// Free up quota for a subsequent rename
name|fs
operator|.
name|delete
argument_list|(
name|dst1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|rename
argument_list|(
name|src1
argument_list|,
name|dst1
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
name|Rename
operator|.
name|OVERWRITE
argument_list|)
expr_stmt|;
comment|// Restart the cluster and ensure the above operations can be
comment|// loaded from the edits log
name|restartCluster
argument_list|()
expr_stmt|;
name|fs
operator|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
name|src1
operator|=
name|getTestRootPath
argument_list|(
name|fc
argument_list|,
literal|"testEditsLogRename/srcdir/src1"
argument_list|)
expr_stmt|;
name|dst1
operator|=
name|getTestRootPath
argument_list|(
name|fc
argument_list|,
literal|"testEditsLogRename/dstdir/dst1"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|fs
operator|.
name|exists
argument_list|(
name|src1
argument_list|)
argument_list|)
expr_stmt|;
comment|// ensure src1 is already renamed
name|Assert
operator|.
name|assertTrue
argument_list|(
name|fs
operator|.
name|exists
argument_list|(
name|dst1
argument_list|)
argument_list|)
expr_stmt|;
comment|// ensure rename dst exists
block|}
annotation|@
name|Test
DECL|method|testIsValidNameInvalidNames ()
specifier|public
name|void
name|testIsValidNameInvalidNames
parameter_list|()
block|{
name|String
index|[]
name|invalidNames
init|=
block|{
literal|"/foo/../bar"
block|,
literal|"/foo/./bar"
block|,
literal|"/foo/:/bar"
block|,
literal|"/foo:bar"
block|}
decl_stmt|;
for|for
control|(
name|String
name|invalidName
range|:
name|invalidNames
control|)
block|{
name|Assert
operator|.
name|assertFalse
argument_list|(
name|invalidName
operator|+
literal|" is not valid"
argument_list|,
name|fc
operator|.
name|getDefaultFileSystem
argument_list|()
operator|.
name|isValidName
argument_list|(
name|invalidName
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|oldRename (Path src, Path dst, boolean renameSucceeds, boolean exception)
specifier|private
name|void
name|oldRename
parameter_list|(
name|Path
name|src
parameter_list|,
name|Path
name|dst
parameter_list|,
name|boolean
name|renameSucceeds
parameter_list|,
name|boolean
name|exception
parameter_list|)
throws|throws
name|Exception
block|{
name|DistributedFileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
try|try
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|renameSucceeds
argument_list|,
name|fs
operator|.
name|rename
argument_list|(
name|src
argument_list|,
name|dst
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|exception
argument_list|)
expr_stmt|;
block|}
name|Assert
operator|.
name|assertEquals
argument_list|(
name|renameSucceeds
argument_list|,
operator|!
name|exists
argument_list|(
name|fc
argument_list|,
name|src
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|renameSucceeds
argument_list|,
name|exists
argument_list|(
name|fc
argument_list|,
name|dst
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|rename (Path src, Path dst, boolean dstExists, boolean renameSucceeds, boolean exception, Options.Rename... options)
specifier|private
name|void
name|rename
parameter_list|(
name|Path
name|src
parameter_list|,
name|Path
name|dst
parameter_list|,
name|boolean
name|dstExists
parameter_list|,
name|boolean
name|renameSucceeds
parameter_list|,
name|boolean
name|exception
parameter_list|,
name|Options
operator|.
name|Rename
modifier|...
name|options
parameter_list|)
throws|throws
name|Exception
block|{
try|try
block|{
name|fc
operator|.
name|rename
argument_list|(
name|src
argument_list|,
name|dst
argument_list|,
name|options
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|renameSucceeds
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|exception
argument_list|)
expr_stmt|;
block|}
name|Assert
operator|.
name|assertEquals
argument_list|(
name|renameSucceeds
argument_list|,
operator|!
name|exists
argument_list|(
name|fc
argument_list|,
name|src
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
operator|(
name|dstExists
operator|||
name|renameSucceeds
operator|)
argument_list|,
name|exists
argument_list|(
name|fc
argument_list|,
name|dst
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|listCorruptedBlocksSupported ()
specifier|protected
name|boolean
name|listCorruptedBlocksSupported
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Test
DECL|method|testCrossFileSystemRename ()
specifier|public
name|void
name|testCrossFileSystemRename
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|fc
operator|.
name|rename
argument_list|(
operator|new
name|Path
argument_list|(
literal|"hdfs://127.0.0.1/aaa/bbb/Foo"
argument_list|)
argument_list|,
operator|new
name|Path
argument_list|(
literal|"file://aaa/bbb/Moo"
argument_list|)
argument_list|,
name|Options
operator|.
name|Rename
operator|.
name|OVERWRITE
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"IOexception expected."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
comment|// okay
block|}
block|}
block|}
end_class

end_unit

