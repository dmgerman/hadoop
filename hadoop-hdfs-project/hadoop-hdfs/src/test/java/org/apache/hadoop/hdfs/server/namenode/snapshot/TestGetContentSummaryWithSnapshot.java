begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode.snapshot
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
operator|.
name|snapshot
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
name|hdfs
operator|.
name|server
operator|.
name|namenode
operator|.
name|FSDirectory
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
name|server
operator|.
name|namenode
operator|.
name|FSNamesystem
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
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
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

begin_comment
comment|/**  * Verify content summary is computed correctly when  * 1. There are snapshots taken under the directory  * 2. The given path is a snapshot path  */
end_comment

begin_class
DECL|class|TestGetContentSummaryWithSnapshot
specifier|public
class|class
name|TestGetContentSummaryWithSnapshot
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
specifier|protected
name|Configuration
name|conf
decl_stmt|;
DECL|field|cluster
specifier|protected
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|fsn
specifier|protected
name|FSNamesystem
name|fsn
decl_stmt|;
DECL|field|fsdir
specifier|protected
name|FSDirectory
name|fsdir
decl_stmt|;
DECL|field|dfs
specifier|protected
name|DistributedFileSystem
name|dfs
decl_stmt|;
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
name|fsn
operator|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
expr_stmt|;
name|fsdir
operator|=
name|fsn
operator|.
name|getFSDirectory
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
comment|/**    * Calculate against a snapshot path.    * 1. create dirs /foo/bar    * 2. take snapshot s1 on /foo    * 3. create a 10 byte file /foo/bar/baz    * Make sure for "/foo/bar" and "/foo/.snapshot/s1/bar" have correct results:    * the 1 byte file is not included in snapshot s1.    * 4. create another snapshot, append to the file /foo/bar/baz,    * and make sure file count, directory count and file length is good.    * 5. delete the file, ensure contentSummary output too.    */
annotation|@
name|Test
DECL|method|testGetContentSummary ()
specifier|public
name|void
name|testGetContentSummary
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|Path
name|foo
init|=
operator|new
name|Path
argument_list|(
literal|"/foo"
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
literal|"bar"
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
literal|"baz"
argument_list|)
decl_stmt|;
name|dfs
operator|.
name|mkdirs
argument_list|(
name|bar
argument_list|)
expr_stmt|;
name|dfs
operator|.
name|allowSnapshot
argument_list|(
name|foo
argument_list|)
expr_stmt|;
name|dfs
operator|.
name|createSnapshot
argument_list|(
name|foo
argument_list|,
literal|"s1"
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
init|=
name|cluster
operator|.
name|getNameNodeRpc
argument_list|()
operator|.
name|getContentSummary
argument_list|(
name|bar
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|summary
operator|.
name|getDirectoryCount
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|summary
operator|.
name|getFileCount
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|summary
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|Path
name|barS1
init|=
name|SnapshotTestHelper
operator|.
name|getSnapshotPath
argument_list|(
name|foo
argument_list|,
literal|"s1"
argument_list|,
literal|"bar"
argument_list|)
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
name|barS1
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|summary
operator|.
name|getDirectoryCount
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|summary
operator|.
name|getFileCount
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|summary
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
comment|// also check /foo and /foo/.snapshot/s1
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
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|summary
operator|.
name|getDirectoryCount
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|summary
operator|.
name|getFileCount
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|summary
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|Path
name|fooS1
init|=
name|SnapshotTestHelper
operator|.
name|getSnapshotRoot
argument_list|(
name|foo
argument_list|,
literal|"s1"
argument_list|)
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
name|fooS1
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|summary
operator|.
name|getDirectoryCount
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|summary
operator|.
name|getFileCount
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|summary
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
comment|// create a new snapshot s2 and update the file
name|dfs
operator|.
name|createSnapshot
argument_list|(
name|foo
argument_list|,
literal|"s2"
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|appendFile
argument_list|(
name|dfs
argument_list|,
name|baz
argument_list|,
literal|10
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
name|bar
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|summary
operator|.
name|getDirectoryCount
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|summary
operator|.
name|getFileCount
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|20
argument_list|,
name|summary
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|Path
name|fooS2
init|=
name|SnapshotTestHelper
operator|.
name|getSnapshotRoot
argument_list|(
name|foo
argument_list|,
literal|"s2"
argument_list|)
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
name|fooS2
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|summary
operator|.
name|getDirectoryCount
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|summary
operator|.
name|getFileCount
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|summary
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|getNameNodeRpc
argument_list|()
operator|.
name|delete
argument_list|(
name|baz
operator|.
name|toString
argument_list|()
argument_list|,
literal|false
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
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|summary
operator|.
name|getSnapshotDirectoryCount
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|summary
operator|.
name|getSnapshotFileCount
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|20
argument_list|,
name|summary
operator|.
name|getSnapshotLength
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|Path
name|bazS1
init|=
name|SnapshotTestHelper
operator|.
name|getSnapshotPath
argument_list|(
name|foo
argument_list|,
literal|"s1"
argument_list|,
literal|"bar/baz"
argument_list|)
decl_stmt|;
try|try
block|{
name|cluster
operator|.
name|getNameNodeRpc
argument_list|()
operator|.
name|getContentSummary
argument_list|(
name|bazS1
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"should get FileNotFoundException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|ignored
parameter_list|)
block|{}
block|}
block|}
end_class

end_unit

