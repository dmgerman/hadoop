begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|namenode
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
name|ContentSummary
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
name|hdfs
operator|.
name|DFSConfigKeys
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
name|DFSTestUtil
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
name|security
operator|.
name|AccessControlException
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
name|security
operator|.
name|PrivilegedExceptionAction
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
name|fs
operator|.
name|permission
operator|.
name|FsAction
operator|.
name|READ_EXECUTE
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
comment|/**  * This class tests get content summary with permission settings.  */
end_comment

begin_class
DECL|class|TestGetContentSummaryWithPermission
specifier|public
class|class
name|TestGetContentSummaryWithPermission
block|{
DECL|field|REPLICATION
specifier|protected
specifier|static
specifier|final
name|short
name|REPLICATION
init|=
literal|3
decl_stmt|;
DECL|field|BLOCKSIZE
specifier|protected
specifier|static
specifier|final
name|long
name|BLOCKSIZE
init|=
literal|1024
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|cluster
specifier|private
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|dfs
specifier|private
name|DistributedFileSystem
name|dfs
decl_stmt|;
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
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BLOCK_SIZE_KEY
argument_list|,
name|BLOCKSIZE
argument_list|)
expr_stmt|;
name|cluster
operator|=
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
name|REPLICATION
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|dfs
operator|=
name|cluster
operator|.
name|getFileSystem
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
comment|/**    * Test getContentSummary for super user. For super user, whatever    * permission the directories are with, always allowed to access    *    * @throws Exception    */
annotation|@
name|Test
DECL|method|testGetContentSummarySuperUser ()
specifier|public
name|void
name|testGetContentSummarySuperUser
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Path
name|foo
init|=
operator|new
name|Path
argument_list|(
literal|"/fooSuper"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|bar
init|=
operator|new
name|Path
argument_list|(
name|foo
argument_list|,
literal|"barSuper"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|baz
init|=
operator|new
name|Path
argument_list|(
name|bar
argument_list|,
literal|"bazSuper"
argument_list|)
decl_stmt|;
name|dfs
operator|.
name|mkdirs
argument_list|(
name|bar
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|dfs
argument_list|,
name|baz
argument_list|,
literal|10
argument_list|,
name|REPLICATION
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
name|ContentSummary
name|summary
decl_stmt|;
name|summary
operator|=
name|cluster
operator|.
name|getNameNodeRpc
argument_list|()
operator|.
name|getContentSummary
argument_list|(
name|foo
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|verifySummary
argument_list|(
name|summary
argument_list|,
literal|2
argument_list|,
literal|1
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|dfs
operator|.
name|setPermission
argument_list|(
name|foo
argument_list|,
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|summary
operator|=
name|cluster
operator|.
name|getNameNodeRpc
argument_list|()
operator|.
name|getContentSummary
argument_list|(
name|foo
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|verifySummary
argument_list|(
name|summary
argument_list|,
literal|2
argument_list|,
literal|1
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|dfs
operator|.
name|setPermission
argument_list|(
name|bar
argument_list|,
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|summary
operator|=
name|cluster
operator|.
name|getNameNodeRpc
argument_list|()
operator|.
name|getContentSummary
argument_list|(
name|foo
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|verifySummary
argument_list|(
name|summary
argument_list|,
literal|2
argument_list|,
literal|1
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|dfs
operator|.
name|setPermission
argument_list|(
name|baz
argument_list|,
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|summary
operator|=
name|cluster
operator|.
name|getNameNodeRpc
argument_list|()
operator|.
name|getContentSummary
argument_list|(
name|foo
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|verifySummary
argument_list|(
name|summary
argument_list|,
literal|2
argument_list|,
literal|1
argument_list|,
literal|10
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test getContentSummary for non-super, non-owner. Such users are restricted    * by permission of subdirectories. Namely if there is any subdirectory that    * does not have READ_EXECUTE access, AccessControlException will be thrown.    *    * @throws Exception    */
annotation|@
name|Test
DECL|method|testGetContentSummaryNonSuperUser ()
specifier|public
name|void
name|testGetContentSummaryNonSuperUser
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Path
name|foo
init|=
operator|new
name|Path
argument_list|(
literal|"/fooNoneSuper"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|bar
init|=
operator|new
name|Path
argument_list|(
name|foo
argument_list|,
literal|"barNoneSuper"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|baz
init|=
operator|new
name|Path
argument_list|(
name|bar
argument_list|,
literal|"bazNoneSuper"
argument_list|)
decl_stmt|;
comment|// run as some random non-superuser, non-owner user.
specifier|final
name|UserGroupInformation
name|userUgi
init|=
name|UserGroupInformation
operator|.
name|createUserForTesting
argument_list|(
literal|"randomUser"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"randomGroup"
block|}
argument_list|)
decl_stmt|;
name|dfs
operator|.
name|mkdirs
argument_list|(
name|bar
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|dfs
argument_list|,
name|baz
argument_list|,
literal|10
argument_list|,
name|REPLICATION
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
comment|// by default, permission is rwxr-xr-x, as long as READ and EXECUTE are set,
comment|// content summary should accessible
name|FileStatus
name|fileStatus
decl_stmt|;
name|fileStatus
operator|=
name|dfs
operator|.
name|getFileStatus
argument_list|(
name|foo
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|short
operator|)
literal|755
argument_list|,
name|fileStatus
operator|.
name|getPermission
argument_list|()
operator|.
name|toOctal
argument_list|()
argument_list|)
expr_stmt|;
name|fileStatus
operator|=
name|dfs
operator|.
name|getFileStatus
argument_list|(
name|bar
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|short
operator|)
literal|755
argument_list|,
name|fileStatus
operator|.
name|getPermission
argument_list|()
operator|.
name|toOctal
argument_list|()
argument_list|)
expr_stmt|;
comment|// file has no EXECUTE, it is rw-r--r-- default
name|fileStatus
operator|=
name|dfs
operator|.
name|getFileStatus
argument_list|(
name|baz
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|short
operator|)
literal|644
argument_list|,
name|fileStatus
operator|.
name|getPermission
argument_list|()
operator|.
name|toOctal
argument_list|()
argument_list|)
expr_stmt|;
comment|// by default, can get content summary
name|ContentSummary
name|summary
init|=
name|userUgi
operator|.
name|doAs
argument_list|(
call|(
name|PrivilegedExceptionAction
argument_list|<
name|ContentSummary
argument_list|>
call|)
argument_list|()
operator|->
name|cluster
operator|.
name|getNameNodeRpc
argument_list|()
operator|.
name|getContentSummary
argument_list|(
name|foo
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|verifySummary
argument_list|(
name|summary
argument_list|,
literal|2
argument_list|,
literal|1
argument_list|,
literal|10
argument_list|)
expr_stmt|;
comment|// set empty access on root dir, should disallow content summary
name|dfs
operator|.
name|setPermission
argument_list|(
name|foo
argument_list|,
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
literal|0
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|userUgi
operator|.
name|doAs
argument_list|(
call|(
name|PrivilegedExceptionAction
argument_list|<
name|ContentSummary
argument_list|>
call|)
argument_list|()
operator|->
name|cluster
operator|.
name|getNameNodeRpc
argument_list|()
operator|.
name|getContentSummary
argument_list|(
name|foo
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should've fail due to access control exception."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AccessControlException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Permission denied"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// restore foo's permission to allow READ_EXECUTE
name|dfs
operator|.
name|setPermission
argument_list|(
name|foo
argument_list|,
operator|new
name|FsPermission
argument_list|(
name|READ_EXECUTE
argument_list|,
name|READ_EXECUTE
argument_list|,
name|READ_EXECUTE
argument_list|)
argument_list|)
expr_stmt|;
comment|// set empty access on subdir, should disallow content summary from root dir
name|dfs
operator|.
name|setPermission
argument_list|(
name|bar
argument_list|,
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
literal|0
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|userUgi
operator|.
name|doAs
argument_list|(
call|(
name|PrivilegedExceptionAction
argument_list|<
name|ContentSummary
argument_list|>
call|)
argument_list|()
operator|->
name|cluster
operator|.
name|getNameNodeRpc
argument_list|()
operator|.
name|getContentSummary
argument_list|(
name|foo
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should've fail due to access control exception."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AccessControlException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Permission denied"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// restore the permission of subdir to READ_EXECUTE. enable
comment|// getContentSummary again for root
name|dfs
operator|.
name|setPermission
argument_list|(
name|bar
argument_list|,
operator|new
name|FsPermission
argument_list|(
name|READ_EXECUTE
argument_list|,
name|READ_EXECUTE
argument_list|,
name|READ_EXECUTE
argument_list|)
argument_list|)
expr_stmt|;
name|summary
operator|=
name|userUgi
operator|.
name|doAs
argument_list|(
call|(
name|PrivilegedExceptionAction
argument_list|<
name|ContentSummary
argument_list|>
call|)
argument_list|()
operator|->
name|cluster
operator|.
name|getNameNodeRpc
argument_list|()
operator|.
name|getContentSummary
argument_list|(
name|foo
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|verifySummary
argument_list|(
name|summary
argument_list|,
literal|2
argument_list|,
literal|1
argument_list|,
literal|10
argument_list|)
expr_stmt|;
comment|// permission of files under the directory does not affect
comment|// getContentSummary
name|dfs
operator|.
name|setPermission
argument_list|(
name|baz
argument_list|,
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|summary
operator|=
name|userUgi
operator|.
name|doAs
argument_list|(
call|(
name|PrivilegedExceptionAction
argument_list|<
name|ContentSummary
argument_list|>
call|)
argument_list|()
operator|->
name|cluster
operator|.
name|getNameNodeRpc
argument_list|()
operator|.
name|getContentSummary
argument_list|(
name|foo
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|verifySummary
argument_list|(
name|summary
argument_list|,
literal|2
argument_list|,
literal|1
argument_list|,
literal|10
argument_list|)
expr_stmt|;
block|}
DECL|method|verifySummary (ContentSummary summary, int dirCount, int fileCount, int length)
specifier|private
name|void
name|verifySummary
parameter_list|(
name|ContentSummary
name|summary
parameter_list|,
name|int
name|dirCount
parameter_list|,
name|int
name|fileCount
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|dirCount
argument_list|,
name|summary
operator|.
name|getDirectoryCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|fileCount
argument_list|,
name|summary
operator|.
name|getFileCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|length
argument_list|,
name|summary
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

