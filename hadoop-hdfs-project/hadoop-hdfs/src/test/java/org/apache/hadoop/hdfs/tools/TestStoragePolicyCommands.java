begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.tools
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|tools
package|;
end_package

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
name|URISyntaxException
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
name|FsShell
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
name|StorageType
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
name|BlockStoragePolicy
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
operator|.
name|StoragePolicySatisfierMode
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
name|blockmanagement
operator|.
name|BlockStoragePolicySuite
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

begin_comment
comment|/**  * Test StoragePolicyAdmin commands  */
end_comment

begin_class
DECL|class|TestStoragePolicyCommands
specifier|public
class|class
name|TestStoragePolicyCommands
block|{
DECL|field|REPL
specifier|private
specifier|static
specifier|final
name|short
name|REPL
init|=
literal|1
decl_stmt|;
DECL|field|SIZE
specifier|private
specifier|static
specifier|final
name|int
name|SIZE
init|=
literal|128
decl_stmt|;
DECL|field|conf
specifier|protected
specifier|static
name|Configuration
name|conf
decl_stmt|;
DECL|field|cluster
specifier|protected
specifier|static
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|fs
specifier|protected
specifier|static
name|FileSystem
name|fs
decl_stmt|;
annotation|@
name|Before
DECL|method|clusterSetUp ()
specifier|public
name|void
name|clusterSetUp
parameter_list|()
throws|throws
name|IOException
throws|,
name|URISyntaxException
block|{
name|conf
operator|=
operator|new
name|HdfsConfiguration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_STORAGE_POLICY_SATISFIER_MODE_KEY
argument_list|,
name|StoragePolicySatisfierMode
operator|.
name|EXTERNAL
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|StorageType
index|[]
index|[]
name|newtypes
init|=
operator|new
name|StorageType
index|[]
index|[]
block|{
block|{
name|StorageType
operator|.
name|ARCHIVE
block|,
name|StorageType
operator|.
name|DISK
block|}
block|}
decl_stmt|;
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
name|REPL
argument_list|)
operator|.
name|storageTypes
argument_list|(
name|newtypes
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
name|fs
operator|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|clusterShutdown ()
specifier|public
name|void
name|clusterShutdown
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|fs
operator|!=
literal|null
condition|)
block|{
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
name|fs
operator|=
literal|null
expr_stmt|;
block|}
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
name|Test
DECL|method|testSetAndUnsetStoragePolicy ()
specifier|public
name|void
name|testSetAndUnsetStoragePolicy
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
name|wow
init|=
operator|new
name|Path
argument_list|(
name|bar
argument_list|,
literal|"wow"
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|wow
argument_list|,
name|SIZE
argument_list|,
name|REPL
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|/*      * test: set storage policy      */
specifier|final
name|StoragePolicyAdmin
name|admin
init|=
operator|new
name|StoragePolicyAdmin
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|toolRun
argument_list|(
name|admin
argument_list|,
literal|"-setStoragePolicy -path "
operator|+
name|fs
operator|.
name|getUri
argument_list|()
operator|+
literal|"/foo -policy WARM"
argument_list|,
literal|0
argument_list|,
literal|"Set storage policy WARM on "
operator|+
name|fs
operator|.
name|getUri
argument_list|()
operator|+
literal|"/foo"
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|toolRun
argument_list|(
name|admin
argument_list|,
literal|"-setStoragePolicy -path /foo/bar -policy COLD"
argument_list|,
literal|0
argument_list|,
literal|"Set storage policy COLD on "
operator|+
name|bar
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|toolRun
argument_list|(
name|admin
argument_list|,
literal|"-setStoragePolicy -path /foo/bar/wow -policy HOT"
argument_list|,
literal|0
argument_list|,
literal|"Set storage policy HOT on "
operator|+
name|wow
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|toolRun
argument_list|(
name|admin
argument_list|,
literal|"-setStoragePolicy -path /fooz -policy WARM"
argument_list|,
literal|2
argument_list|,
literal|"File/Directory does not exist: /fooz"
argument_list|)
expr_stmt|;
comment|/*      * test: get storage policy after set      */
specifier|final
name|BlockStoragePolicySuite
name|suite
init|=
name|BlockStoragePolicySuite
operator|.
name|createDefaultSuite
argument_list|()
decl_stmt|;
specifier|final
name|BlockStoragePolicy
name|warm
init|=
name|suite
operator|.
name|getPolicy
argument_list|(
literal|"WARM"
argument_list|)
decl_stmt|;
specifier|final
name|BlockStoragePolicy
name|cold
init|=
name|suite
operator|.
name|getPolicy
argument_list|(
literal|"COLD"
argument_list|)
decl_stmt|;
specifier|final
name|BlockStoragePolicy
name|hot
init|=
name|suite
operator|.
name|getPolicy
argument_list|(
literal|"HOT"
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|toolRun
argument_list|(
name|admin
argument_list|,
literal|"-getStoragePolicy -path "
operator|+
name|fs
operator|.
name|getUri
argument_list|()
operator|+
literal|"/foo"
argument_list|,
literal|0
argument_list|,
literal|"The storage policy of "
operator|+
name|fs
operator|.
name|getUri
argument_list|()
operator|+
literal|"/foo:\n"
operator|+
name|warm
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|toolRun
argument_list|(
name|admin
argument_list|,
literal|"-getStoragePolicy -path /foo/bar"
argument_list|,
literal|0
argument_list|,
literal|"The storage policy of "
operator|+
name|bar
operator|.
name|toString
argument_list|()
operator|+
literal|":\n"
operator|+
name|cold
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|toolRun
argument_list|(
name|admin
argument_list|,
literal|"-getStoragePolicy -path /foo/bar/wow"
argument_list|,
literal|0
argument_list|,
literal|"The storage policy of "
operator|+
name|wow
operator|.
name|toString
argument_list|()
operator|+
literal|":\n"
operator|+
name|hot
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|toolRun
argument_list|(
name|admin
argument_list|,
literal|"-getStoragePolicy -path /fooz"
argument_list|,
literal|2
argument_list|,
literal|"File/Directory does not exist: /fooz"
argument_list|)
expr_stmt|;
comment|/*      * test: unset storage policy      */
name|DFSTestUtil
operator|.
name|toolRun
argument_list|(
name|admin
argument_list|,
literal|"-unsetStoragePolicy -path "
operator|+
name|fs
operator|.
name|getUri
argument_list|()
operator|+
literal|"/foo"
argument_list|,
literal|0
argument_list|,
literal|"Unset storage policy from "
operator|+
name|fs
operator|.
name|getUri
argument_list|()
operator|+
literal|"/foo"
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|toolRun
argument_list|(
name|admin
argument_list|,
literal|"-unsetStoragePolicy -path /foo/bar"
argument_list|,
literal|0
argument_list|,
literal|"Unset storage policy from "
operator|+
name|bar
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|toolRun
argument_list|(
name|admin
argument_list|,
literal|"-unsetStoragePolicy -path /foo/bar/wow"
argument_list|,
literal|0
argument_list|,
literal|"Unset storage policy from "
operator|+
name|wow
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|toolRun
argument_list|(
name|admin
argument_list|,
literal|"-unsetStoragePolicy -path /fooz"
argument_list|,
literal|2
argument_list|,
literal|"File/Directory does not exist: /fooz"
argument_list|)
expr_stmt|;
comment|/*      * test: get storage policy after unset      */
name|DFSTestUtil
operator|.
name|toolRun
argument_list|(
name|admin
argument_list|,
literal|"-getStoragePolicy -path /foo"
argument_list|,
literal|0
argument_list|,
literal|"The storage policy of "
operator|+
name|foo
operator|.
name|toString
argument_list|()
operator|+
literal|" is unspecified"
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|toolRun
argument_list|(
name|admin
argument_list|,
literal|"-getStoragePolicy -path /foo/bar"
argument_list|,
literal|0
argument_list|,
literal|"The storage policy of "
operator|+
name|bar
operator|.
name|toString
argument_list|()
operator|+
literal|" is unspecified"
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|toolRun
argument_list|(
name|admin
argument_list|,
literal|"-getStoragePolicy -path /foo/bar/wow"
argument_list|,
literal|0
argument_list|,
literal|"The storage policy of "
operator|+
name|wow
operator|.
name|toString
argument_list|()
operator|+
literal|" is unspecified"
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|toolRun
argument_list|(
name|admin
argument_list|,
literal|"-getStoragePolicy -path /fooz"
argument_list|,
literal|2
argument_list|,
literal|"File/Directory does not exist: /fooz"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSetAndGetStoragePolicy ()
specifier|public
name|void
name|testSetAndGetStoragePolicy
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
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|bar
argument_list|,
name|SIZE
argument_list|,
name|REPL
argument_list|,
literal|0
argument_list|)
expr_stmt|;
specifier|final
name|StoragePolicyAdmin
name|admin
init|=
operator|new
name|StoragePolicyAdmin
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|toolRun
argument_list|(
name|admin
argument_list|,
literal|"-getStoragePolicy -path /foo"
argument_list|,
literal|0
argument_list|,
literal|"The storage policy of "
operator|+
name|foo
operator|.
name|toString
argument_list|()
operator|+
literal|" is unspecified"
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|toolRun
argument_list|(
name|admin
argument_list|,
literal|"-getStoragePolicy -path /foo/bar"
argument_list|,
literal|0
argument_list|,
literal|"The storage policy of "
operator|+
name|bar
operator|.
name|toString
argument_list|()
operator|+
literal|" is unspecified"
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|toolRun
argument_list|(
name|admin
argument_list|,
literal|"-setStoragePolicy -path /foo -policy WARM"
argument_list|,
literal|0
argument_list|,
literal|"Set storage policy WARM on "
operator|+
name|foo
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|toolRun
argument_list|(
name|admin
argument_list|,
literal|"-setStoragePolicy -path /foo/bar -policy COLD"
argument_list|,
literal|0
argument_list|,
literal|"Set storage policy COLD on "
operator|+
name|bar
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|toolRun
argument_list|(
name|admin
argument_list|,
literal|"-setStoragePolicy -path /fooz -policy WARM"
argument_list|,
literal|2
argument_list|,
literal|"File/Directory does not exist: /fooz"
argument_list|)
expr_stmt|;
specifier|final
name|BlockStoragePolicySuite
name|suite
init|=
name|BlockStoragePolicySuite
operator|.
name|createDefaultSuite
argument_list|()
decl_stmt|;
specifier|final
name|BlockStoragePolicy
name|warm
init|=
name|suite
operator|.
name|getPolicy
argument_list|(
literal|"WARM"
argument_list|)
decl_stmt|;
specifier|final
name|BlockStoragePolicy
name|cold
init|=
name|suite
operator|.
name|getPolicy
argument_list|(
literal|"COLD"
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|toolRun
argument_list|(
name|admin
argument_list|,
literal|"-getStoragePolicy -path /foo"
argument_list|,
literal|0
argument_list|,
literal|"The storage policy of "
operator|+
name|foo
operator|.
name|toString
argument_list|()
operator|+
literal|":\n"
operator|+
name|warm
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|toolRun
argument_list|(
name|admin
argument_list|,
literal|"-getStoragePolicy -path /foo/bar"
argument_list|,
literal|0
argument_list|,
literal|"The storage policy of "
operator|+
name|bar
operator|.
name|toString
argument_list|()
operator|+
literal|":\n"
operator|+
name|cold
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|toolRun
argument_list|(
name|admin
argument_list|,
literal|"-getStoragePolicy -path /fooz"
argument_list|,
literal|2
argument_list|,
literal|"File/Directory does not exist: /fooz"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testLsWithSpParameter ()
specifier|public
name|void
name|testLsWithSpParameter
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|file
init|=
operator|new
name|Path
argument_list|(
literal|"/foo/bar"
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|file
argument_list|,
name|SIZE
argument_list|,
name|REPL
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|fs
operator|.
name|setStoragePolicy
argument_list|(
name|file
argument_list|,
literal|"COLD"
argument_list|)
expr_stmt|;
name|FsShell
name|shell
init|=
operator|new
name|FsShell
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|toolRun
argument_list|(
name|shell
argument_list|,
literal|"-ls -sp /foo"
argument_list|,
literal|0
argument_list|,
literal|"COLD"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testLsWithSpParameterUnsupportedFs ()
specifier|public
name|void
name|testLsWithSpParameterUnsupportedFs
parameter_list|()
throws|throws
name|Exception
block|{
name|FsShell
name|shell
init|=
operator|new
name|FsShell
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|toolRun
argument_list|(
name|shell
argument_list|,
literal|"-ls -sp file://"
argument_list|,
operator|-
literal|1
argument_list|,
literal|"UnsupportedOperationException"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

