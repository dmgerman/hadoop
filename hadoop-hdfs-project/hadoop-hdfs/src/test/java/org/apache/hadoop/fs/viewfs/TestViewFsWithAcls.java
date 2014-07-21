begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.viewfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|viewfs
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
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
name|FileContextTestHelper
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
name|FsConstants
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
name|AclEntry
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
name|AclStatus
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
name|MiniDFSNNTopology
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
name|io
operator|.
name|IOException
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
name|AclEntryScope
operator|.
name|ACCESS
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
name|AclEntryScope
operator|.
name|DEFAULT
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
name|AclEntryType
operator|.
name|*
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
name|*
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
name|NONE
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
name|hdfs
operator|.
name|server
operator|.
name|namenode
operator|.
name|AclTestHelpers
operator|.
name|aclEntry
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
name|assertArrayEquals
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

begin_comment
comment|/**  * Verify ACL through ViewFs functionality.  */
end_comment

begin_class
DECL|class|TestViewFsWithAcls
specifier|public
class|class
name|TestViewFsWithAcls
block|{
DECL|field|cluster
specifier|private
specifier|static
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|clusterConf
specifier|private
specifier|static
name|Configuration
name|clusterConf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
DECL|field|fc
DECL|field|fc2
specifier|private
specifier|static
name|FileContext
name|fc
decl_stmt|,
name|fc2
decl_stmt|;
DECL|field|fcView
DECL|field|fcTarget
DECL|field|fcTarget2
specifier|private
name|FileContext
name|fcView
decl_stmt|,
name|fcTarget
decl_stmt|,
name|fcTarget2
decl_stmt|;
DECL|field|fsViewConf
specifier|private
name|Configuration
name|fsViewConf
decl_stmt|;
DECL|field|targetTestRoot
DECL|field|targetTestRoot2
DECL|field|mountOnNn1
DECL|field|mountOnNn2
specifier|private
name|Path
name|targetTestRoot
decl_stmt|,
name|targetTestRoot2
decl_stmt|,
name|mountOnNn1
decl_stmt|,
name|mountOnNn2
decl_stmt|;
DECL|field|fileContextTestHelper
specifier|private
name|FileContextTestHelper
name|fileContextTestHelper
init|=
operator|new
name|FileContextTestHelper
argument_list|(
literal|"/tmp/TestViewFsWithAcls"
argument_list|)
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|clusterSetupAtBeginning ()
specifier|public
specifier|static
name|void
name|clusterSetupAtBeginning
parameter_list|()
throws|throws
name|IOException
block|{
name|clusterConf
operator|.
name|setBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_ACLS_ENABLED_KEY
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|cluster
operator|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|clusterConf
argument_list|)
operator|.
name|nnTopology
argument_list|(
name|MiniDFSNNTopology
operator|.
name|simpleFederatedTopology
argument_list|(
literal|2
argument_list|)
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
name|clusterConf
argument_list|)
expr_stmt|;
name|fc2
operator|=
name|FileContext
operator|.
name|getFileContext
argument_list|(
name|cluster
operator|.
name|getURI
argument_list|(
literal|1
argument_list|)
argument_list|,
name|clusterConf
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
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
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
name|fcTarget
operator|=
name|fc
expr_stmt|;
name|fcTarget2
operator|=
name|fc2
expr_stmt|;
name|targetTestRoot
operator|=
name|fileContextTestHelper
operator|.
name|getAbsoluteTestRootPath
argument_list|(
name|fc
argument_list|)
expr_stmt|;
name|targetTestRoot2
operator|=
name|fileContextTestHelper
operator|.
name|getAbsoluteTestRootPath
argument_list|(
name|fc2
argument_list|)
expr_stmt|;
name|fcTarget
operator|.
name|delete
argument_list|(
name|targetTestRoot
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fcTarget2
operator|.
name|delete
argument_list|(
name|targetTestRoot2
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fcTarget
operator|.
name|mkdir
argument_list|(
name|targetTestRoot
argument_list|,
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
literal|0750
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fcTarget2
operator|.
name|mkdir
argument_list|(
name|targetTestRoot2
argument_list|,
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
literal|0750
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fsViewConf
operator|=
name|ViewFileSystemTestSetup
operator|.
name|createConfig
argument_list|()
expr_stmt|;
name|setupMountPoints
argument_list|()
expr_stmt|;
name|fcView
operator|=
name|FileContext
operator|.
name|getFileContext
argument_list|(
name|FsConstants
operator|.
name|VIEWFS_URI
argument_list|,
name|fsViewConf
argument_list|)
expr_stmt|;
block|}
DECL|method|setupMountPoints ()
specifier|private
name|void
name|setupMountPoints
parameter_list|()
block|{
name|mountOnNn1
operator|=
operator|new
name|Path
argument_list|(
literal|"/mountOnNn1"
argument_list|)
expr_stmt|;
name|mountOnNn2
operator|=
operator|new
name|Path
argument_list|(
literal|"/mountOnNn2"
argument_list|)
expr_stmt|;
name|ConfigUtil
operator|.
name|addLink
argument_list|(
name|fsViewConf
argument_list|,
name|mountOnNn1
operator|.
name|toString
argument_list|()
argument_list|,
name|targetTestRoot
operator|.
name|toUri
argument_list|()
argument_list|)
expr_stmt|;
name|ConfigUtil
operator|.
name|addLink
argument_list|(
name|fsViewConf
argument_list|,
name|mountOnNn2
operator|.
name|toString
argument_list|()
argument_list|,
name|targetTestRoot2
operator|.
name|toUri
argument_list|()
argument_list|)
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
name|fcTarget
operator|.
name|delete
argument_list|(
name|fileContextTestHelper
operator|.
name|getTestRootPath
argument_list|(
name|fcTarget
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fcTarget2
operator|.
name|delete
argument_list|(
name|fileContextTestHelper
operator|.
name|getTestRootPath
argument_list|(
name|fcTarget2
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * Verify a ViewFs wrapped over multiple federated NameNodes will    * dispatch the ACL operations to the correct NameNode.    */
annotation|@
name|Test
DECL|method|testAclOnMountEntry ()
specifier|public
name|void
name|testAclOnMountEntry
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Set ACLs on the first namespace and verify they are correct
name|List
argument_list|<
name|AclEntry
argument_list|>
name|aclSpec
init|=
name|Lists
operator|.
name|newArrayList
argument_list|(
name|aclEntry
argument_list|(
name|ACCESS
argument_list|,
name|USER
argument_list|,
name|READ_WRITE
argument_list|)
argument_list|,
name|aclEntry
argument_list|(
name|ACCESS
argument_list|,
name|USER
argument_list|,
literal|"foo"
argument_list|,
name|READ
argument_list|)
argument_list|,
name|aclEntry
argument_list|(
name|ACCESS
argument_list|,
name|GROUP
argument_list|,
name|READ
argument_list|)
argument_list|,
name|aclEntry
argument_list|(
name|ACCESS
argument_list|,
name|OTHER
argument_list|,
name|NONE
argument_list|)
argument_list|)
decl_stmt|;
name|fcView
operator|.
name|setAcl
argument_list|(
name|mountOnNn1
argument_list|,
name|aclSpec
argument_list|)
expr_stmt|;
name|AclEntry
index|[]
name|expected
init|=
operator|new
name|AclEntry
index|[]
block|{
name|aclEntry
argument_list|(
name|ACCESS
argument_list|,
name|USER
argument_list|,
literal|"foo"
argument_list|,
name|READ
argument_list|)
block|,
name|aclEntry
argument_list|(
name|ACCESS
argument_list|,
name|GROUP
argument_list|,
name|READ
argument_list|)
block|}
decl_stmt|;
name|assertArrayEquals
argument_list|(
name|expected
argument_list|,
name|aclEntryArray
argument_list|(
name|fcView
operator|.
name|getAclStatus
argument_list|(
name|mountOnNn1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// Double-check by getting ACL status using FileSystem
comment|// instead of ViewFs
name|assertArrayEquals
argument_list|(
name|expected
argument_list|,
name|aclEntryArray
argument_list|(
name|fc
operator|.
name|getAclStatus
argument_list|(
name|targetTestRoot
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// Modify the ACL entries on the first namespace
name|aclSpec
operator|=
name|Lists
operator|.
name|newArrayList
argument_list|(
name|aclEntry
argument_list|(
name|DEFAULT
argument_list|,
name|USER
argument_list|,
literal|"foo"
argument_list|,
name|READ
argument_list|)
argument_list|)
expr_stmt|;
name|fcView
operator|.
name|modifyAclEntries
argument_list|(
name|mountOnNn1
argument_list|,
name|aclSpec
argument_list|)
expr_stmt|;
name|expected
operator|=
operator|new
name|AclEntry
index|[]
block|{
name|aclEntry
argument_list|(
name|ACCESS
argument_list|,
name|USER
argument_list|,
literal|"foo"
argument_list|,
name|READ
argument_list|)
block|,
name|aclEntry
argument_list|(
name|ACCESS
argument_list|,
name|GROUP
argument_list|,
name|READ
argument_list|)
block|,
name|aclEntry
argument_list|(
name|DEFAULT
argument_list|,
name|USER
argument_list|,
name|READ_WRITE
argument_list|)
block|,
name|aclEntry
argument_list|(
name|DEFAULT
argument_list|,
name|USER
argument_list|,
literal|"foo"
argument_list|,
name|READ
argument_list|)
block|,
name|aclEntry
argument_list|(
name|DEFAULT
argument_list|,
name|GROUP
argument_list|,
name|READ
argument_list|)
block|,
name|aclEntry
argument_list|(
name|DEFAULT
argument_list|,
name|MASK
argument_list|,
name|READ
argument_list|)
block|,
name|aclEntry
argument_list|(
name|DEFAULT
argument_list|,
name|OTHER
argument_list|,
name|NONE
argument_list|)
block|}
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|expected
argument_list|,
name|aclEntryArray
argument_list|(
name|fcView
operator|.
name|getAclStatus
argument_list|(
name|mountOnNn1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|fcView
operator|.
name|removeDefaultAcl
argument_list|(
name|mountOnNn1
argument_list|)
expr_stmt|;
name|expected
operator|=
operator|new
name|AclEntry
index|[]
block|{
name|aclEntry
argument_list|(
name|ACCESS
argument_list|,
name|USER
argument_list|,
literal|"foo"
argument_list|,
name|READ
argument_list|)
block|,
name|aclEntry
argument_list|(
name|ACCESS
argument_list|,
name|GROUP
argument_list|,
name|READ
argument_list|)
block|}
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|expected
argument_list|,
name|aclEntryArray
argument_list|(
name|fcView
operator|.
name|getAclStatus
argument_list|(
name|mountOnNn1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|expected
argument_list|,
name|aclEntryArray
argument_list|(
name|fc
operator|.
name|getAclStatus
argument_list|(
name|targetTestRoot
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// Paranoid check: verify the other namespace does not
comment|// have ACLs set on the same path.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|fcView
operator|.
name|getAclStatus
argument_list|(
name|mountOnNn2
argument_list|)
operator|.
name|getEntries
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|fc2
operator|.
name|getAclStatus
argument_list|(
name|targetTestRoot2
argument_list|)
operator|.
name|getEntries
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// Remove the ACL entries on the first namespace
name|fcView
operator|.
name|removeAcl
argument_list|(
name|mountOnNn1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|fcView
operator|.
name|getAclStatus
argument_list|(
name|mountOnNn1
argument_list|)
operator|.
name|getEntries
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|fc
operator|.
name|getAclStatus
argument_list|(
name|targetTestRoot
argument_list|)
operator|.
name|getEntries
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// Now set ACLs on the second namespace
name|aclSpec
operator|=
name|Lists
operator|.
name|newArrayList
argument_list|(
name|aclEntry
argument_list|(
name|ACCESS
argument_list|,
name|USER
argument_list|,
literal|"bar"
argument_list|,
name|READ
argument_list|)
argument_list|)
expr_stmt|;
name|fcView
operator|.
name|modifyAclEntries
argument_list|(
name|mountOnNn2
argument_list|,
name|aclSpec
argument_list|)
expr_stmt|;
name|expected
operator|=
operator|new
name|AclEntry
index|[]
block|{
name|aclEntry
argument_list|(
name|ACCESS
argument_list|,
name|USER
argument_list|,
literal|"bar"
argument_list|,
name|READ
argument_list|)
block|,
name|aclEntry
argument_list|(
name|ACCESS
argument_list|,
name|GROUP
argument_list|,
name|READ_EXECUTE
argument_list|)
block|}
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|expected
argument_list|,
name|aclEntryArray
argument_list|(
name|fcView
operator|.
name|getAclStatus
argument_list|(
name|mountOnNn2
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|expected
argument_list|,
name|aclEntryArray
argument_list|(
name|fc2
operator|.
name|getAclStatus
argument_list|(
name|targetTestRoot2
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// Remove the ACL entries on the second namespace
name|fcView
operator|.
name|removeAclEntries
argument_list|(
name|mountOnNn2
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
name|aclEntry
argument_list|(
name|ACCESS
argument_list|,
name|USER
argument_list|,
literal|"bar"
argument_list|,
name|READ
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|expected
operator|=
operator|new
name|AclEntry
index|[]
block|{
name|aclEntry
argument_list|(
name|ACCESS
argument_list|,
name|GROUP
argument_list|,
name|READ_EXECUTE
argument_list|)
block|}
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|expected
argument_list|,
name|aclEntryArray
argument_list|(
name|fc2
operator|.
name|getAclStatus
argument_list|(
name|targetTestRoot2
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|fcView
operator|.
name|removeAcl
argument_list|(
name|mountOnNn2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|fcView
operator|.
name|getAclStatus
argument_list|(
name|mountOnNn2
argument_list|)
operator|.
name|getEntries
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|fc2
operator|.
name|getAclStatus
argument_list|(
name|targetTestRoot2
argument_list|)
operator|.
name|getEntries
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|aclEntryArray (AclStatus aclStatus)
specifier|private
name|AclEntry
index|[]
name|aclEntryArray
parameter_list|(
name|AclStatus
name|aclStatus
parameter_list|)
block|{
return|return
name|aclStatus
operator|.
name|getEntries
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|AclEntry
index|[
literal|0
index|]
argument_list|)
return|;
block|}
block|}
end_class

end_unit

