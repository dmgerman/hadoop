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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|EnumSet
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
name|XAttrSetFlag
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
name|hdfs
operator|.
name|protocol
operator|.
name|NSQuotaExceededException
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
name|SnapshotAccessControlException
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
name|NameNode
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
name|NameNodeAdapter
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
name|util
operator|.
name|ToolRunner
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

begin_comment
comment|/**  * Tests interaction of XAttrs with snapshots.  */
end_comment

begin_class
DECL|class|TestXAttrWithSnapshot
specifier|public
class|class
name|TestXAttrWithSnapshot
block|{
DECL|field|cluster
specifier|private
specifier|static
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|conf
specifier|private
specifier|static
name|Configuration
name|conf
decl_stmt|;
DECL|field|hdfs
specifier|private
specifier|static
name|DistributedFileSystem
name|hdfs
decl_stmt|;
DECL|field|pathCount
specifier|private
specifier|static
name|int
name|pathCount
init|=
literal|0
decl_stmt|;
DECL|field|path
DECL|field|snapshotPath
DECL|field|snapshotPath2
DECL|field|snapshotPath3
specifier|private
specifier|static
name|Path
name|path
decl_stmt|,
name|snapshotPath
decl_stmt|,
name|snapshotPath2
decl_stmt|,
name|snapshotPath3
decl_stmt|;
DECL|field|snapshotName
DECL|field|snapshotName2
DECL|field|snapshotName3
specifier|private
specifier|static
name|String
name|snapshotName
decl_stmt|,
name|snapshotName2
decl_stmt|,
name|snapshotName3
decl_stmt|;
DECL|field|SUCCESS
specifier|private
specifier|final
name|int
name|SUCCESS
init|=
literal|0
decl_stmt|;
comment|// XAttrs
DECL|field|name1
specifier|private
specifier|static
specifier|final
name|String
name|name1
init|=
literal|"user.a1"
decl_stmt|;
DECL|field|value1
specifier|private
specifier|static
specifier|final
name|byte
index|[]
name|value1
init|=
block|{
literal|0x31
block|,
literal|0x32
block|,
literal|0x33
block|}
decl_stmt|;
DECL|field|newValue1
specifier|private
specifier|static
specifier|final
name|byte
index|[]
name|newValue1
init|=
block|{
literal|0x31
block|,
literal|0x31
block|,
literal|0x31
block|}
decl_stmt|;
DECL|field|name2
specifier|private
specifier|static
specifier|final
name|String
name|name2
init|=
literal|"user.a2"
decl_stmt|;
DECL|field|value2
specifier|private
specifier|static
specifier|final
name|byte
index|[]
name|value2
init|=
block|{
literal|0x37
block|,
literal|0x38
block|,
literal|0x39
block|}
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
name|BeforeClass
DECL|method|init ()
specifier|public
specifier|static
name|void
name|init
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
name|setBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_XATTRS_ENABLED_KEY
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|initCluster
argument_list|(
literal|true
argument_list|)
expr_stmt|;
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
name|Exception
block|{
name|IOUtils
operator|.
name|cleanup
argument_list|(
literal|null
argument_list|,
name|hdfs
argument_list|)
expr_stmt|;
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
block|}
block|}
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
block|{
operator|++
name|pathCount
expr_stmt|;
name|path
operator|=
operator|new
name|Path
argument_list|(
literal|"/p"
operator|+
name|pathCount
argument_list|)
expr_stmt|;
name|snapshotName
operator|=
literal|"snapshot"
operator|+
name|pathCount
expr_stmt|;
name|snapshotName2
operator|=
name|snapshotName
operator|+
literal|"-2"
expr_stmt|;
name|snapshotName3
operator|=
name|snapshotName
operator|+
literal|"-3"
expr_stmt|;
name|snapshotPath
operator|=
operator|new
name|Path
argument_list|(
name|path
argument_list|,
operator|new
name|Path
argument_list|(
literal|".snapshot"
argument_list|,
name|snapshotName
argument_list|)
argument_list|)
expr_stmt|;
name|snapshotPath2
operator|=
operator|new
name|Path
argument_list|(
name|path
argument_list|,
operator|new
name|Path
argument_list|(
literal|".snapshot"
argument_list|,
name|snapshotName2
argument_list|)
argument_list|)
expr_stmt|;
name|snapshotPath3
operator|=
operator|new
name|Path
argument_list|(
name|path
argument_list|,
operator|new
name|Path
argument_list|(
literal|".snapshot"
argument_list|,
name|snapshotName3
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests modifying xattrs on a directory that has been snapshotted    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|120000
argument_list|)
DECL|method|testModifyReadsCurrentState ()
specifier|public
name|void
name|testModifyReadsCurrentState
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Init
name|FileSystem
operator|.
name|mkdirs
argument_list|(
name|hdfs
argument_list|,
name|path
argument_list|,
name|FsPermission
operator|.
name|createImmutable
argument_list|(
operator|(
name|short
operator|)
literal|0700
argument_list|)
argument_list|)
expr_stmt|;
name|SnapshotTestHelper
operator|.
name|createSnapshot
argument_list|(
name|hdfs
argument_list|,
name|path
argument_list|,
name|snapshotName
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|setXAttr
argument_list|(
name|path
argument_list|,
name|name1
argument_list|,
name|value1
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|setXAttr
argument_list|(
name|path
argument_list|,
name|name2
argument_list|,
name|value2
argument_list|)
expr_stmt|;
comment|// Verify that current path reflects xattrs, snapshot doesn't
name|Map
argument_list|<
name|String
argument_list|,
name|byte
index|[]
argument_list|>
name|xattrs
init|=
name|hdfs
operator|.
name|getXAttrs
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|xattrs
operator|.
name|size
argument_list|()
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|value1
argument_list|,
name|xattrs
operator|.
name|get
argument_list|(
name|name1
argument_list|)
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|value2
argument_list|,
name|xattrs
operator|.
name|get
argument_list|(
name|name2
argument_list|)
argument_list|)
expr_stmt|;
name|xattrs
operator|=
name|hdfs
operator|.
name|getXAttrs
argument_list|(
name|snapshotPath
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|xattrs
operator|.
name|size
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// Modify each xattr and make sure it's reflected
name|hdfs
operator|.
name|setXAttr
argument_list|(
name|path
argument_list|,
name|name1
argument_list|,
name|value2
argument_list|,
name|EnumSet
operator|.
name|of
argument_list|(
name|XAttrSetFlag
operator|.
name|REPLACE
argument_list|)
argument_list|)
expr_stmt|;
name|xattrs
operator|=
name|hdfs
operator|.
name|getXAttrs
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|xattrs
operator|.
name|size
argument_list|()
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|value2
argument_list|,
name|xattrs
operator|.
name|get
argument_list|(
name|name1
argument_list|)
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|value2
argument_list|,
name|xattrs
operator|.
name|get
argument_list|(
name|name2
argument_list|)
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|setXAttr
argument_list|(
name|path
argument_list|,
name|name2
argument_list|,
name|value1
argument_list|,
name|EnumSet
operator|.
name|of
argument_list|(
name|XAttrSetFlag
operator|.
name|REPLACE
argument_list|)
argument_list|)
expr_stmt|;
name|xattrs
operator|=
name|hdfs
operator|.
name|getXAttrs
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|xattrs
operator|.
name|size
argument_list|()
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|value2
argument_list|,
name|xattrs
operator|.
name|get
argument_list|(
name|name1
argument_list|)
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|value1
argument_list|,
name|xattrs
operator|.
name|get
argument_list|(
name|name2
argument_list|)
argument_list|)
expr_stmt|;
comment|// Paranoia checks
name|xattrs
operator|=
name|hdfs
operator|.
name|getXAttrs
argument_list|(
name|snapshotPath
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|xattrs
operator|.
name|size
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|removeXAttr
argument_list|(
name|path
argument_list|,
name|name1
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|removeXAttr
argument_list|(
name|path
argument_list|,
name|name2
argument_list|)
expr_stmt|;
name|xattrs
operator|=
name|hdfs
operator|.
name|getXAttrs
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|xattrs
operator|.
name|size
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests removing xattrs on a directory that has been snapshotted    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|120000
argument_list|)
DECL|method|testRemoveReadsCurrentState ()
specifier|public
name|void
name|testRemoveReadsCurrentState
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Init
name|FileSystem
operator|.
name|mkdirs
argument_list|(
name|hdfs
argument_list|,
name|path
argument_list|,
name|FsPermission
operator|.
name|createImmutable
argument_list|(
operator|(
name|short
operator|)
literal|0700
argument_list|)
argument_list|)
expr_stmt|;
name|SnapshotTestHelper
operator|.
name|createSnapshot
argument_list|(
name|hdfs
argument_list|,
name|path
argument_list|,
name|snapshotName
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|setXAttr
argument_list|(
name|path
argument_list|,
name|name1
argument_list|,
name|value1
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|setXAttr
argument_list|(
name|path
argument_list|,
name|name2
argument_list|,
name|value2
argument_list|)
expr_stmt|;
comment|// Verify that current path reflects xattrs, snapshot doesn't
name|Map
argument_list|<
name|String
argument_list|,
name|byte
index|[]
argument_list|>
name|xattrs
init|=
name|hdfs
operator|.
name|getXAttrs
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|xattrs
operator|.
name|size
argument_list|()
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|value1
argument_list|,
name|xattrs
operator|.
name|get
argument_list|(
name|name1
argument_list|)
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|value2
argument_list|,
name|xattrs
operator|.
name|get
argument_list|(
name|name2
argument_list|)
argument_list|)
expr_stmt|;
name|xattrs
operator|=
name|hdfs
operator|.
name|getXAttrs
argument_list|(
name|snapshotPath
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|xattrs
operator|.
name|size
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// Remove xattrs and verify one-by-one
name|hdfs
operator|.
name|removeXAttr
argument_list|(
name|path
argument_list|,
name|name2
argument_list|)
expr_stmt|;
name|xattrs
operator|=
name|hdfs
operator|.
name|getXAttrs
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|xattrs
operator|.
name|size
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|value1
argument_list|,
name|xattrs
operator|.
name|get
argument_list|(
name|name1
argument_list|)
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|removeXAttr
argument_list|(
name|path
argument_list|,
name|name1
argument_list|)
expr_stmt|;
name|xattrs
operator|=
name|hdfs
operator|.
name|getXAttrs
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|xattrs
operator|.
name|size
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
comment|/**    * 1) Save xattrs, then create snapshot. Assert that inode of original and    * snapshot have same xattrs. 2) Change the original xattrs, assert snapshot    * still has old xattrs.    */
annotation|@
name|Test
DECL|method|testXAttrForSnapshotRootAfterChange ()
specifier|public
name|void
name|testXAttrForSnapshotRootAfterChange
parameter_list|()
throws|throws
name|Exception
block|{
name|FileSystem
operator|.
name|mkdirs
argument_list|(
name|hdfs
argument_list|,
name|path
argument_list|,
name|FsPermission
operator|.
name|createImmutable
argument_list|(
operator|(
name|short
operator|)
literal|0700
argument_list|)
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|setXAttr
argument_list|(
name|path
argument_list|,
name|name1
argument_list|,
name|value1
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|setXAttr
argument_list|(
name|path
argument_list|,
name|name2
argument_list|,
name|value2
argument_list|)
expr_stmt|;
name|SnapshotTestHelper
operator|.
name|createSnapshot
argument_list|(
name|hdfs
argument_list|,
name|path
argument_list|,
name|snapshotName
argument_list|)
expr_stmt|;
comment|// Both original and snapshot have same XAttrs.
name|Map
argument_list|<
name|String
argument_list|,
name|byte
index|[]
argument_list|>
name|xattrs
init|=
name|hdfs
operator|.
name|getXAttrs
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|xattrs
operator|.
name|size
argument_list|()
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertArrayEquals
argument_list|(
name|value1
argument_list|,
name|xattrs
operator|.
name|get
argument_list|(
name|name1
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertArrayEquals
argument_list|(
name|value2
argument_list|,
name|xattrs
operator|.
name|get
argument_list|(
name|name2
argument_list|)
argument_list|)
expr_stmt|;
name|xattrs
operator|=
name|hdfs
operator|.
name|getXAttrs
argument_list|(
name|snapshotPath
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|xattrs
operator|.
name|size
argument_list|()
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertArrayEquals
argument_list|(
name|value1
argument_list|,
name|xattrs
operator|.
name|get
argument_list|(
name|name1
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertArrayEquals
argument_list|(
name|value2
argument_list|,
name|xattrs
operator|.
name|get
argument_list|(
name|name2
argument_list|)
argument_list|)
expr_stmt|;
comment|// Original XAttrs have changed, but snapshot still has old XAttrs.
name|hdfs
operator|.
name|setXAttr
argument_list|(
name|path
argument_list|,
name|name1
argument_list|,
name|newValue1
argument_list|)
expr_stmt|;
name|doSnapshotRootChangeAssertions
argument_list|(
name|path
argument_list|,
name|snapshotPath
argument_list|)
expr_stmt|;
name|restart
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|doSnapshotRootChangeAssertions
argument_list|(
name|path
argument_list|,
name|snapshotPath
argument_list|)
expr_stmt|;
name|restart
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|doSnapshotRootChangeAssertions
argument_list|(
name|path
argument_list|,
name|snapshotPath
argument_list|)
expr_stmt|;
block|}
DECL|method|doSnapshotRootChangeAssertions (Path path, Path snapshotPath)
specifier|private
specifier|static
name|void
name|doSnapshotRootChangeAssertions
parameter_list|(
name|Path
name|path
parameter_list|,
name|Path
name|snapshotPath
parameter_list|)
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|byte
index|[]
argument_list|>
name|xattrs
init|=
name|hdfs
operator|.
name|getXAttrs
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|xattrs
operator|.
name|size
argument_list|()
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertArrayEquals
argument_list|(
name|newValue1
argument_list|,
name|xattrs
operator|.
name|get
argument_list|(
name|name1
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertArrayEquals
argument_list|(
name|value2
argument_list|,
name|xattrs
operator|.
name|get
argument_list|(
name|name2
argument_list|)
argument_list|)
expr_stmt|;
name|xattrs
operator|=
name|hdfs
operator|.
name|getXAttrs
argument_list|(
name|snapshotPath
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|xattrs
operator|.
name|size
argument_list|()
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertArrayEquals
argument_list|(
name|value1
argument_list|,
name|xattrs
operator|.
name|get
argument_list|(
name|name1
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertArrayEquals
argument_list|(
name|value2
argument_list|,
name|xattrs
operator|.
name|get
argument_list|(
name|name2
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * 1) Save xattrs, then create snapshot. Assert that inode of original and    * snapshot have same xattrs. 2) Remove some original xattrs, assert snapshot    * still has old xattrs.    */
annotation|@
name|Test
DECL|method|testXAttrForSnapshotRootAfterRemove ()
specifier|public
name|void
name|testXAttrForSnapshotRootAfterRemove
parameter_list|()
throws|throws
name|Exception
block|{
name|FileSystem
operator|.
name|mkdirs
argument_list|(
name|hdfs
argument_list|,
name|path
argument_list|,
name|FsPermission
operator|.
name|createImmutable
argument_list|(
operator|(
name|short
operator|)
literal|0700
argument_list|)
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|setXAttr
argument_list|(
name|path
argument_list|,
name|name1
argument_list|,
name|value1
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|setXAttr
argument_list|(
name|path
argument_list|,
name|name2
argument_list|,
name|value2
argument_list|)
expr_stmt|;
name|SnapshotTestHelper
operator|.
name|createSnapshot
argument_list|(
name|hdfs
argument_list|,
name|path
argument_list|,
name|snapshotName
argument_list|)
expr_stmt|;
comment|// Both original and snapshot have same XAttrs.
name|Map
argument_list|<
name|String
argument_list|,
name|byte
index|[]
argument_list|>
name|xattrs
init|=
name|hdfs
operator|.
name|getXAttrs
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|xattrs
operator|.
name|size
argument_list|()
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertArrayEquals
argument_list|(
name|value1
argument_list|,
name|xattrs
operator|.
name|get
argument_list|(
name|name1
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertArrayEquals
argument_list|(
name|value2
argument_list|,
name|xattrs
operator|.
name|get
argument_list|(
name|name2
argument_list|)
argument_list|)
expr_stmt|;
name|xattrs
operator|=
name|hdfs
operator|.
name|getXAttrs
argument_list|(
name|snapshotPath
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|xattrs
operator|.
name|size
argument_list|()
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertArrayEquals
argument_list|(
name|value1
argument_list|,
name|xattrs
operator|.
name|get
argument_list|(
name|name1
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertArrayEquals
argument_list|(
name|value2
argument_list|,
name|xattrs
operator|.
name|get
argument_list|(
name|name2
argument_list|)
argument_list|)
expr_stmt|;
comment|// Original XAttrs have been removed, but snapshot still has old XAttrs.
name|hdfs
operator|.
name|removeXAttr
argument_list|(
name|path
argument_list|,
name|name1
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|removeXAttr
argument_list|(
name|path
argument_list|,
name|name2
argument_list|)
expr_stmt|;
name|doSnapshotRootRemovalAssertions
argument_list|(
name|path
argument_list|,
name|snapshotPath
argument_list|)
expr_stmt|;
name|restart
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|doSnapshotRootRemovalAssertions
argument_list|(
name|path
argument_list|,
name|snapshotPath
argument_list|)
expr_stmt|;
name|restart
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|doSnapshotRootRemovalAssertions
argument_list|(
name|path
argument_list|,
name|snapshotPath
argument_list|)
expr_stmt|;
block|}
DECL|method|doSnapshotRootRemovalAssertions (Path path, Path snapshotPath)
specifier|private
specifier|static
name|void
name|doSnapshotRootRemovalAssertions
parameter_list|(
name|Path
name|path
parameter_list|,
name|Path
name|snapshotPath
parameter_list|)
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|byte
index|[]
argument_list|>
name|xattrs
init|=
name|hdfs
operator|.
name|getXAttrs
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|xattrs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|xattrs
operator|=
name|hdfs
operator|.
name|getXAttrs
argument_list|(
name|snapshotPath
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|xattrs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertArrayEquals
argument_list|(
name|value1
argument_list|,
name|xattrs
operator|.
name|get
argument_list|(
name|name1
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertArrayEquals
argument_list|(
name|value2
argument_list|,
name|xattrs
operator|.
name|get
argument_list|(
name|name2
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test successive snapshots in between modifications of XAttrs.    * Also verify that snapshot XAttrs are not altered when a    * snapshot is deleted.    */
annotation|@
name|Test
DECL|method|testSuccessiveSnapshotXAttrChanges ()
specifier|public
name|void
name|testSuccessiveSnapshotXAttrChanges
parameter_list|()
throws|throws
name|Exception
block|{
comment|// First snapshot
name|FileSystem
operator|.
name|mkdirs
argument_list|(
name|hdfs
argument_list|,
name|path
argument_list|,
name|FsPermission
operator|.
name|createImmutable
argument_list|(
operator|(
name|short
operator|)
literal|0700
argument_list|)
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|setXAttr
argument_list|(
name|path
argument_list|,
name|name1
argument_list|,
name|value1
argument_list|)
expr_stmt|;
name|SnapshotTestHelper
operator|.
name|createSnapshot
argument_list|(
name|hdfs
argument_list|,
name|path
argument_list|,
name|snapshotName
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|byte
index|[]
argument_list|>
name|xattrs
init|=
name|hdfs
operator|.
name|getXAttrs
argument_list|(
name|snapshotPath
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|xattrs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertArrayEquals
argument_list|(
name|value1
argument_list|,
name|xattrs
operator|.
name|get
argument_list|(
name|name1
argument_list|)
argument_list|)
expr_stmt|;
comment|// Second snapshot
name|hdfs
operator|.
name|setXAttr
argument_list|(
name|path
argument_list|,
name|name1
argument_list|,
name|newValue1
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|setXAttr
argument_list|(
name|path
argument_list|,
name|name2
argument_list|,
name|value2
argument_list|)
expr_stmt|;
name|SnapshotTestHelper
operator|.
name|createSnapshot
argument_list|(
name|hdfs
argument_list|,
name|path
argument_list|,
name|snapshotName2
argument_list|)
expr_stmt|;
name|xattrs
operator|=
name|hdfs
operator|.
name|getXAttrs
argument_list|(
name|snapshotPath2
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|xattrs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertArrayEquals
argument_list|(
name|newValue1
argument_list|,
name|xattrs
operator|.
name|get
argument_list|(
name|name1
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertArrayEquals
argument_list|(
name|value2
argument_list|,
name|xattrs
operator|.
name|get
argument_list|(
name|name2
argument_list|)
argument_list|)
expr_stmt|;
comment|// Third snapshot
name|hdfs
operator|.
name|setXAttr
argument_list|(
name|path
argument_list|,
name|name1
argument_list|,
name|value1
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|removeXAttr
argument_list|(
name|path
argument_list|,
name|name2
argument_list|)
expr_stmt|;
name|SnapshotTestHelper
operator|.
name|createSnapshot
argument_list|(
name|hdfs
argument_list|,
name|path
argument_list|,
name|snapshotName3
argument_list|)
expr_stmt|;
name|xattrs
operator|=
name|hdfs
operator|.
name|getXAttrs
argument_list|(
name|snapshotPath3
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|xattrs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertArrayEquals
argument_list|(
name|value1
argument_list|,
name|xattrs
operator|.
name|get
argument_list|(
name|name1
argument_list|)
argument_list|)
expr_stmt|;
comment|// Check that the first and second snapshots'
comment|// XAttrs have stayed constant
name|xattrs
operator|=
name|hdfs
operator|.
name|getXAttrs
argument_list|(
name|snapshotPath
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|xattrs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertArrayEquals
argument_list|(
name|value1
argument_list|,
name|xattrs
operator|.
name|get
argument_list|(
name|name1
argument_list|)
argument_list|)
expr_stmt|;
name|xattrs
operator|=
name|hdfs
operator|.
name|getXAttrs
argument_list|(
name|snapshotPath2
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|xattrs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertArrayEquals
argument_list|(
name|newValue1
argument_list|,
name|xattrs
operator|.
name|get
argument_list|(
name|name1
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertArrayEquals
argument_list|(
name|value2
argument_list|,
name|xattrs
operator|.
name|get
argument_list|(
name|name2
argument_list|)
argument_list|)
expr_stmt|;
comment|// Remove the second snapshot and verify the first and
comment|// third snapshots' XAttrs have stayed constant
name|hdfs
operator|.
name|deleteSnapshot
argument_list|(
name|path
argument_list|,
name|snapshotName2
argument_list|)
expr_stmt|;
name|xattrs
operator|=
name|hdfs
operator|.
name|getXAttrs
argument_list|(
name|snapshotPath
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|xattrs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertArrayEquals
argument_list|(
name|value1
argument_list|,
name|xattrs
operator|.
name|get
argument_list|(
name|name1
argument_list|)
argument_list|)
expr_stmt|;
name|xattrs
operator|=
name|hdfs
operator|.
name|getXAttrs
argument_list|(
name|snapshotPath3
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|xattrs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertArrayEquals
argument_list|(
name|value1
argument_list|,
name|xattrs
operator|.
name|get
argument_list|(
name|name1
argument_list|)
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|deleteSnapshot
argument_list|(
name|path
argument_list|,
name|snapshotName
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|deleteSnapshot
argument_list|(
name|path
argument_list|,
name|snapshotName3
argument_list|)
expr_stmt|;
block|}
comment|/**    * Assert exception of setting xattr on read-only snapshot.    */
annotation|@
name|Test
DECL|method|testSetXAttrSnapshotPath ()
specifier|public
name|void
name|testSetXAttrSnapshotPath
parameter_list|()
throws|throws
name|Exception
block|{
name|FileSystem
operator|.
name|mkdirs
argument_list|(
name|hdfs
argument_list|,
name|path
argument_list|,
name|FsPermission
operator|.
name|createImmutable
argument_list|(
operator|(
name|short
operator|)
literal|0700
argument_list|)
argument_list|)
expr_stmt|;
name|SnapshotTestHelper
operator|.
name|createSnapshot
argument_list|(
name|hdfs
argument_list|,
name|path
argument_list|,
name|snapshotName
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expect
argument_list|(
name|SnapshotAccessControlException
operator|.
name|class
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|setXAttr
argument_list|(
name|snapshotPath
argument_list|,
name|name1
argument_list|,
name|value1
argument_list|)
expr_stmt|;
block|}
comment|/**    * Assert exception of removing xattr on read-only snapshot.    */
annotation|@
name|Test
DECL|method|testRemoveXAttrSnapshotPath ()
specifier|public
name|void
name|testRemoveXAttrSnapshotPath
parameter_list|()
throws|throws
name|Exception
block|{
name|FileSystem
operator|.
name|mkdirs
argument_list|(
name|hdfs
argument_list|,
name|path
argument_list|,
name|FsPermission
operator|.
name|createImmutable
argument_list|(
operator|(
name|short
operator|)
literal|0700
argument_list|)
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|setXAttr
argument_list|(
name|path
argument_list|,
name|name1
argument_list|,
name|value1
argument_list|)
expr_stmt|;
name|SnapshotTestHelper
operator|.
name|createSnapshot
argument_list|(
name|hdfs
argument_list|,
name|path
argument_list|,
name|snapshotName
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expect
argument_list|(
name|SnapshotAccessControlException
operator|.
name|class
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|removeXAttr
argument_list|(
name|snapshotPath
argument_list|,
name|name1
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test that users can copy a snapshot while preserving its xattrs.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|120000
argument_list|)
DECL|method|testCopySnapshotShouldPreserveXAttrs ()
specifier|public
name|void
name|testCopySnapshotShouldPreserveXAttrs
parameter_list|()
throws|throws
name|Exception
block|{
name|FileSystem
operator|.
name|mkdirs
argument_list|(
name|hdfs
argument_list|,
name|path
argument_list|,
name|FsPermission
operator|.
name|createImmutable
argument_list|(
operator|(
name|short
operator|)
literal|0700
argument_list|)
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|setXAttr
argument_list|(
name|path
argument_list|,
name|name1
argument_list|,
name|value1
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|setXAttr
argument_list|(
name|path
argument_list|,
name|name2
argument_list|,
name|value2
argument_list|)
expr_stmt|;
name|SnapshotTestHelper
operator|.
name|createSnapshot
argument_list|(
name|hdfs
argument_list|,
name|path
argument_list|,
name|snapshotName
argument_list|)
expr_stmt|;
name|Path
name|snapshotCopy
init|=
operator|new
name|Path
argument_list|(
name|path
operator|.
name|toString
argument_list|()
operator|+
literal|"-copy"
argument_list|)
decl_stmt|;
name|String
index|[]
name|argv
init|=
operator|new
name|String
index|[]
block|{
literal|"-cp"
block|,
literal|"-px"
block|,
name|snapshotPath
operator|.
name|toUri
argument_list|()
operator|.
name|toString
argument_list|()
block|,
name|snapshotCopy
operator|.
name|toUri
argument_list|()
operator|.
name|toString
argument_list|()
block|}
decl_stmt|;
name|int
name|ret
init|=
name|ToolRunner
operator|.
name|run
argument_list|(
operator|new
name|FsShell
argument_list|(
name|conf
argument_list|)
argument_list|,
name|argv
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"cp -px is not working on a snapshot"
argument_list|,
name|SUCCESS
argument_list|,
name|ret
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|byte
index|[]
argument_list|>
name|xattrs
init|=
name|hdfs
operator|.
name|getXAttrs
argument_list|(
name|snapshotCopy
argument_list|)
decl_stmt|;
name|assertArrayEquals
argument_list|(
name|value1
argument_list|,
name|xattrs
operator|.
name|get
argument_list|(
name|name1
argument_list|)
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|value2
argument_list|,
name|xattrs
operator|.
name|get
argument_list|(
name|name2
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Initialize the cluster, wait for it to become active, and get FileSystem    * instances for our test users.    *     * @param format if true, format the NameNode and DataNodes before starting up    * @throws Exception if any step fails    */
DECL|method|initCluster (boolean format)
specifier|private
specifier|static
name|void
name|initCluster
parameter_list|(
name|boolean
name|format
parameter_list|)
throws|throws
name|Exception
block|{
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
literal|1
argument_list|)
operator|.
name|format
argument_list|(
name|format
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
name|hdfs
operator|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
block|}
comment|/**    * Restart the cluster, optionally saving a new checkpoint.    *     * @param checkpoint boolean true to save a new checkpoint    * @throws Exception if restart fails    */
DECL|method|restart (boolean checkpoint)
specifier|private
specifier|static
name|void
name|restart
parameter_list|(
name|boolean
name|checkpoint
parameter_list|)
throws|throws
name|Exception
block|{
name|NameNode
name|nameNode
init|=
name|cluster
operator|.
name|getNameNode
argument_list|()
decl_stmt|;
if|if
condition|(
name|checkpoint
condition|)
block|{
name|NameNodeAdapter
operator|.
name|enterSafeMode
argument_list|(
name|nameNode
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|NameNodeAdapter
operator|.
name|saveNamespace
argument_list|(
name|nameNode
argument_list|)
expr_stmt|;
block|}
name|shutdown
argument_list|()
expr_stmt|;
name|initCluster
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

