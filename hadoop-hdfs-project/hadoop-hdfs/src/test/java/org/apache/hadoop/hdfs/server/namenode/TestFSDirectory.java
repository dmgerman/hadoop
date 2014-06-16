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
name|java
operator|.
name|io
operator|.
name|BufferedReader
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
name|io
operator|.
name|StringReader
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
name|List
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
name|XAttr
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
name|snapshot
operator|.
name|Snapshot
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
name|test
operator|.
name|GenericTestUtils
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

begin_comment
comment|/**  * Test {@link FSDirectory}, the in-memory namespace tree.  */
end_comment

begin_class
DECL|class|TestFSDirectory
specifier|public
class|class
name|TestFSDirectory
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
name|TestFSDirectory
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|seed
specifier|private
specifier|static
specifier|final
name|long
name|seed
init|=
literal|0
decl_stmt|;
DECL|field|REPLICATION
specifier|private
specifier|static
specifier|final
name|short
name|REPLICATION
init|=
literal|3
decl_stmt|;
DECL|field|dir
specifier|private
specifier|final
name|Path
name|dir
init|=
operator|new
name|Path
argument_list|(
literal|"/"
operator|+
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|sub1
specifier|private
specifier|final
name|Path
name|sub1
init|=
operator|new
name|Path
argument_list|(
name|dir
argument_list|,
literal|"sub1"
argument_list|)
decl_stmt|;
DECL|field|file1
specifier|private
specifier|final
name|Path
name|file1
init|=
operator|new
name|Path
argument_list|(
name|sub1
argument_list|,
literal|"file1"
argument_list|)
decl_stmt|;
DECL|field|file2
specifier|private
specifier|final
name|Path
name|file2
init|=
operator|new
name|Path
argument_list|(
name|sub1
argument_list|,
literal|"file2"
argument_list|)
decl_stmt|;
DECL|field|sub11
specifier|private
specifier|final
name|Path
name|sub11
init|=
operator|new
name|Path
argument_list|(
name|sub1
argument_list|,
literal|"sub11"
argument_list|)
decl_stmt|;
DECL|field|file3
specifier|private
specifier|final
name|Path
name|file3
init|=
operator|new
name|Path
argument_list|(
name|sub11
argument_list|,
literal|"file3"
argument_list|)
decl_stmt|;
DECL|field|file5
specifier|private
specifier|final
name|Path
name|file5
init|=
operator|new
name|Path
argument_list|(
name|sub1
argument_list|,
literal|"z_file5"
argument_list|)
decl_stmt|;
DECL|field|sub2
specifier|private
specifier|final
name|Path
name|sub2
init|=
operator|new
name|Path
argument_list|(
name|dir
argument_list|,
literal|"sub2"
argument_list|)
decl_stmt|;
DECL|field|file6
specifier|private
specifier|final
name|Path
name|file6
init|=
operator|new
name|Path
argument_list|(
name|sub2
argument_list|,
literal|"file6"
argument_list|)
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
DECL|field|fsn
specifier|private
name|FSNamesystem
name|fsn
decl_stmt|;
DECL|field|fsdir
specifier|private
name|FSDirectory
name|fsdir
decl_stmt|;
DECL|field|hdfs
specifier|private
name|DistributedFileSystem
name|hdfs
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
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_MAX_XATTRS_PER_INODE_KEY
argument_list|,
literal|2
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
name|hdfs
operator|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|hdfs
argument_list|,
name|file1
argument_list|,
literal|1024
argument_list|,
name|REPLICATION
argument_list|,
name|seed
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|hdfs
argument_list|,
name|file2
argument_list|,
literal|1024
argument_list|,
name|REPLICATION
argument_list|,
name|seed
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|hdfs
argument_list|,
name|file3
argument_list|,
literal|1024
argument_list|,
name|REPLICATION
argument_list|,
name|seed
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|hdfs
argument_list|,
name|file5
argument_list|,
literal|1024
argument_list|,
name|REPLICATION
argument_list|,
name|seed
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|mkdirs
argument_list|(
name|sub2
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
comment|/** Dump the tree, make some changes, and then dump the tree again. */
annotation|@
name|Test
DECL|method|testDumpTree ()
specifier|public
name|void
name|testDumpTree
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|INode
name|root
init|=
name|fsdir
operator|.
name|getINode
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Original tree"
argument_list|)
expr_stmt|;
specifier|final
name|StringBuffer
name|b1
init|=
name|root
operator|.
name|dumpTreeRecursively
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"b1="
operator|+
name|b1
argument_list|)
expr_stmt|;
specifier|final
name|BufferedReader
name|in
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|StringReader
argument_list|(
name|b1
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|line
init|=
name|in
operator|.
name|readLine
argument_list|()
decl_stmt|;
name|checkClassName
argument_list|(
name|line
argument_list|)
expr_stmt|;
for|for
control|(
init|;
operator|(
name|line
operator|=
name|in
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|;
control|)
block|{
name|line
operator|=
name|line
operator|.
name|trim
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|line
operator|.
name|isEmpty
argument_list|()
operator|&&
operator|!
name|line
operator|.
name|contains
argument_list|(
literal|"snapshot"
argument_list|)
condition|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"line="
operator|+
name|line
argument_list|,
name|line
operator|.
name|startsWith
argument_list|(
name|INodeDirectory
operator|.
name|DUMPTREE_LAST_ITEM
argument_list|)
operator|||
name|line
operator|.
name|startsWith
argument_list|(
name|INodeDirectory
operator|.
name|DUMPTREE_EXCEPT_LAST_ITEM
argument_list|)
argument_list|)
expr_stmt|;
name|checkClassName
argument_list|(
name|line
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testReset ()
specifier|public
name|void
name|testReset
parameter_list|()
throws|throws
name|Exception
block|{
name|fsdir
operator|.
name|reset
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|fsdir
operator|.
name|isReady
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|INodeDirectory
name|root
init|=
operator|(
name|INodeDirectory
operator|)
name|fsdir
operator|.
name|getINode
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|root
operator|.
name|getChildrenList
argument_list|(
name|Snapshot
operator|.
name|CURRENT_STATE_ID
argument_list|)
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|fsdir
operator|.
name|imageLoadComplete
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|fsdir
operator|.
name|isReady
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSkipQuotaCheck ()
specifier|public
name|void
name|testSkipQuotaCheck
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
comment|// set quota. nsQuota of 1 means no files can be created
comment|//  under this directory.
name|hdfs
operator|.
name|setQuota
argument_list|(
name|sub2
argument_list|,
literal|1
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
comment|// create a file
try|try
block|{
comment|// this should fail
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|hdfs
argument_list|,
name|file6
argument_list|,
literal|1024
argument_list|,
name|REPLICATION
argument_list|,
name|seed
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
literal|"The create should have failed."
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|NSQuotaExceededException
name|qe
parameter_list|)
block|{
comment|// ignored
block|}
comment|// disable the quota check and retry. this should succeed.
name|fsdir
operator|.
name|disableQuotaChecks
argument_list|()
expr_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|hdfs
argument_list|,
name|file6
argument_list|,
literal|1024
argument_list|,
name|REPLICATION
argument_list|,
name|seed
argument_list|)
expr_stmt|;
comment|// trying again after re-enabling the check.
name|hdfs
operator|.
name|delete
argument_list|(
name|file6
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// cleanup
name|fsdir
operator|.
name|enableQuotaChecks
argument_list|()
expr_stmt|;
try|try
block|{
comment|// this should fail
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|hdfs
argument_list|,
name|file6
argument_list|,
literal|1024
argument_list|,
name|REPLICATION
argument_list|,
name|seed
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
literal|"The create should have failed."
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|NSQuotaExceededException
name|qe
parameter_list|)
block|{
comment|// ignored
block|}
block|}
finally|finally
block|{
name|hdfs
operator|.
name|delete
argument_list|(
name|file6
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// cleanup, in case the test failed in the middle.
name|hdfs
operator|.
name|setQuota
argument_list|(
name|sub2
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|checkClassName (String line)
specifier|static
name|void
name|checkClassName
parameter_list|(
name|String
name|line
parameter_list|)
block|{
name|int
name|i
init|=
name|line
operator|.
name|lastIndexOf
argument_list|(
literal|'('
argument_list|)
decl_stmt|;
name|int
name|j
init|=
name|line
operator|.
name|lastIndexOf
argument_list|(
literal|'@'
argument_list|)
decl_stmt|;
specifier|final
name|String
name|classname
init|=
name|line
operator|.
name|substring
argument_list|(
name|i
operator|+
literal|1
argument_list|,
name|j
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|classname
operator|.
name|startsWith
argument_list|(
name|INodeFile
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
operator|||
name|classname
operator|.
name|startsWith
argument_list|(
name|INodeDirectory
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testINodeXAttrsLimit ()
specifier|public
name|void
name|testINodeXAttrsLimit
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|XAttr
argument_list|>
name|existingXAttrs
init|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|XAttr
name|xAttr1
init|=
operator|(
operator|new
name|XAttr
operator|.
name|Builder
argument_list|()
operator|)
operator|.
name|setNameSpace
argument_list|(
name|XAttr
operator|.
name|NameSpace
operator|.
name|USER
argument_list|)
operator|.
name|setName
argument_list|(
literal|"a1"
argument_list|)
operator|.
name|setValue
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|0x31
block|,
literal|0x32
block|,
literal|0x33
block|}
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|XAttr
name|xAttr2
init|=
operator|(
operator|new
name|XAttr
operator|.
name|Builder
argument_list|()
operator|)
operator|.
name|setNameSpace
argument_list|(
name|XAttr
operator|.
name|NameSpace
operator|.
name|USER
argument_list|)
operator|.
name|setName
argument_list|(
literal|"a2"
argument_list|)
operator|.
name|setValue
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|0x31
block|,
literal|0x31
block|,
literal|0x31
block|}
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|existingXAttrs
operator|.
name|add
argument_list|(
name|xAttr1
argument_list|)
expr_stmt|;
name|existingXAttrs
operator|.
name|add
argument_list|(
name|xAttr2
argument_list|)
expr_stmt|;
comment|// Adding a system namespace xAttr, isn't affected by inode xAttrs limit.
name|XAttr
name|newXAttr
init|=
operator|(
operator|new
name|XAttr
operator|.
name|Builder
argument_list|()
operator|)
operator|.
name|setNameSpace
argument_list|(
name|XAttr
operator|.
name|NameSpace
operator|.
name|SYSTEM
argument_list|)
operator|.
name|setName
argument_list|(
literal|"a3"
argument_list|)
operator|.
name|setValue
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|0x33
block|,
literal|0x33
block|,
literal|0x33
block|}
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|XAttr
argument_list|>
name|xAttrs
init|=
name|fsdir
operator|.
name|setINodeXAttr
argument_list|(
name|existingXAttrs
argument_list|,
name|newXAttr
argument_list|,
name|EnumSet
operator|.
name|of
argument_list|(
name|XAttrSetFlag
operator|.
name|CREATE
argument_list|,
name|XAttrSetFlag
operator|.
name|REPLACE
argument_list|)
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|xAttrs
operator|.
name|size
argument_list|()
argument_list|,
literal|3
argument_list|)
expr_stmt|;
comment|// Adding a trusted namespace xAttr, is affected by inode xAttrs limit.
name|XAttr
name|newXAttr1
init|=
operator|(
operator|new
name|XAttr
operator|.
name|Builder
argument_list|()
operator|)
operator|.
name|setNameSpace
argument_list|(
name|XAttr
operator|.
name|NameSpace
operator|.
name|TRUSTED
argument_list|)
operator|.
name|setName
argument_list|(
literal|"a4"
argument_list|)
operator|.
name|setValue
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|0x34
block|,
literal|0x34
block|,
literal|0x34
block|}
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
name|fsdir
operator|.
name|setINodeXAttr
argument_list|(
name|existingXAttrs
argument_list|,
name|newXAttr1
argument_list|,
name|EnumSet
operator|.
name|of
argument_list|(
name|XAttrSetFlag
operator|.
name|CREATE
argument_list|,
name|XAttrSetFlag
operator|.
name|REPLACE
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Setting user visable xattr on inode should fail if "
operator|+
literal|"reaching limit."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"Cannot add additional XAttr "
operator|+
literal|"to inode, would exceed limit"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

