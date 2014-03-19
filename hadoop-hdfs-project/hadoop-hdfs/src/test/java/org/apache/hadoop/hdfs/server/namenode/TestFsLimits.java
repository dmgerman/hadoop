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
name|common
operator|.
name|Util
operator|.
name|fileAsURI
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
name|util
operator|.
name|Time
operator|.
name|now
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
name|mockito
operator|.
name|Matchers
operator|.
name|anyObject
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|when
import|;
end_import

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
name|hadoop
operator|.
name|HadoopIllegalArgumentException
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
name|fs
operator|.
name|permission
operator|.
name|PermissionStatus
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
name|protocol
operator|.
name|FSLimitException
operator|.
name|MaxDirectoryItemsExceededException
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
name|FSLimitException
operator|.
name|PathComponentTooLongException
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
name|server
operator|.
name|common
operator|.
name|HdfsServerConstants
operator|.
name|NamenodeRole
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
DECL|class|TestFsLimits
specifier|public
class|class
name|TestFsLimits
block|{
DECL|field|conf
specifier|static
name|Configuration
name|conf
decl_stmt|;
DECL|field|inodes
specifier|static
name|INode
index|[]
name|inodes
decl_stmt|;
DECL|field|fs
specifier|static
name|FSDirectory
name|fs
decl_stmt|;
DECL|field|fsIsReady
specifier|static
name|boolean
name|fsIsReady
decl_stmt|;
DECL|field|perms
specifier|static
name|PermissionStatus
name|perms
init|=
operator|new
name|PermissionStatus
argument_list|(
literal|"admin"
argument_list|,
literal|"admin"
argument_list|,
name|FsPermission
operator|.
name|getDefault
argument_list|()
argument_list|)
decl_stmt|;
DECL|method|getMockFSImage ()
specifier|static
specifier|private
name|FSImage
name|getMockFSImage
parameter_list|()
block|{
name|FSEditLog
name|editLog
init|=
name|mock
argument_list|(
name|FSEditLog
operator|.
name|class
argument_list|)
decl_stmt|;
name|FSImage
name|fsImage
init|=
name|mock
argument_list|(
name|FSImage
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|fsImage
operator|.
name|getEditLog
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|editLog
argument_list|)
expr_stmt|;
return|return
name|fsImage
return|;
block|}
DECL|method|getMockNamesystem ()
specifier|static
specifier|private
name|FSNamesystem
name|getMockNamesystem
parameter_list|()
block|{
name|FSNamesystem
name|fsn
init|=
name|mock
argument_list|(
name|FSNamesystem
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|fsn
operator|.
name|createFsOwnerPermissions
argument_list|(
operator|(
name|FsPermission
operator|)
name|anyObject
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|PermissionStatus
argument_list|(
literal|"root"
argument_list|,
literal|"wheel"
argument_list|,
name|FsPermission
operator|.
name|getDefault
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|fsn
return|;
block|}
DECL|class|MockFSDirectory
specifier|private
specifier|static
class|class
name|MockFSDirectory
extends|extends
name|FSDirectory
block|{
DECL|method|MockFSDirectory ()
specifier|public
name|MockFSDirectory
parameter_list|()
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|getMockFSImage
argument_list|()
argument_list|,
name|getMockNamesystem
argument_list|()
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|setReady
argument_list|(
name|fsIsReady
argument_list|)
expr_stmt|;
name|NameNode
operator|.
name|initMetrics
argument_list|(
name|conf
argument_list|,
name|NamenodeRole
operator|.
name|NAMENODE
argument_list|)
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
throws|throws
name|IOException
block|{
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_NAME_DIR_KEY
argument_list|,
name|fileAsURI
argument_list|(
operator|new
name|File
argument_list|(
name|MiniDFSCluster
operator|.
name|getBaseDirectory
argument_list|()
argument_list|,
literal|"namenode"
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|fs
operator|=
literal|null
expr_stmt|;
name|fsIsReady
operator|=
literal|true
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNoLimits ()
specifier|public
name|void
name|testNoLimits
parameter_list|()
throws|throws
name|Exception
block|{
name|mkdirs
argument_list|(
literal|"/1"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|mkdirs
argument_list|(
literal|"/22"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|mkdirs
argument_list|(
literal|"/333"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|mkdirs
argument_list|(
literal|"/4444"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|mkdirs
argument_list|(
literal|"/55555"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|mkdirs
argument_list|(
literal|"/1/"
operator|+
name|HdfsConstants
operator|.
name|DOT_SNAPSHOT_DIR
argument_list|,
name|HadoopIllegalArgumentException
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMaxComponentLength ()
specifier|public
name|void
name|testMaxComponentLength
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_MAX_COMPONENT_LENGTH_KEY
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|mkdirs
argument_list|(
literal|"/1"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|mkdirs
argument_list|(
literal|"/22"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|mkdirs
argument_list|(
literal|"/333"
argument_list|,
name|PathComponentTooLongException
operator|.
name|class
argument_list|)
expr_stmt|;
name|mkdirs
argument_list|(
literal|"/4444"
argument_list|,
name|PathComponentTooLongException
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMaxComponentLengthRename ()
specifier|public
name|void
name|testMaxComponentLengthRename
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_MAX_COMPONENT_LENGTH_KEY
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|mkdirs
argument_list|(
literal|"/5"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|rename
argument_list|(
literal|"/5"
argument_list|,
literal|"/555"
argument_list|,
name|PathComponentTooLongException
operator|.
name|class
argument_list|)
expr_stmt|;
name|rename
argument_list|(
literal|"/5"
argument_list|,
literal|"/55"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|mkdirs
argument_list|(
literal|"/6"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|deprecatedRename
argument_list|(
literal|"/6"
argument_list|,
literal|"/666"
argument_list|,
name|PathComponentTooLongException
operator|.
name|class
argument_list|)
expr_stmt|;
name|deprecatedRename
argument_list|(
literal|"/6"
argument_list|,
literal|"/66"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMaxDirItems ()
specifier|public
name|void
name|testMaxDirItems
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_MAX_DIRECTORY_ITEMS_KEY
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|mkdirs
argument_list|(
literal|"/1"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|mkdirs
argument_list|(
literal|"/22"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|mkdirs
argument_list|(
literal|"/333"
argument_list|,
name|MaxDirectoryItemsExceededException
operator|.
name|class
argument_list|)
expr_stmt|;
name|mkdirs
argument_list|(
literal|"/4444"
argument_list|,
name|MaxDirectoryItemsExceededException
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMaxDirItemsRename ()
specifier|public
name|void
name|testMaxDirItemsRename
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_MAX_DIRECTORY_ITEMS_KEY
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|mkdirs
argument_list|(
literal|"/1"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|mkdirs
argument_list|(
literal|"/2"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|mkdirs
argument_list|(
literal|"/2/A"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|rename
argument_list|(
literal|"/2/A"
argument_list|,
literal|"/A"
argument_list|,
name|MaxDirectoryItemsExceededException
operator|.
name|class
argument_list|)
expr_stmt|;
name|rename
argument_list|(
literal|"/2/A"
argument_list|,
literal|"/1/A"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|mkdirs
argument_list|(
literal|"/2/B"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|deprecatedRename
argument_list|(
literal|"/2/B"
argument_list|,
literal|"/B"
argument_list|,
name|MaxDirectoryItemsExceededException
operator|.
name|class
argument_list|)
expr_stmt|;
name|deprecatedRename
argument_list|(
literal|"/2/B"
argument_list|,
literal|"/1/B"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|rename
argument_list|(
literal|"/1"
argument_list|,
literal|"/3"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|deprecatedRename
argument_list|(
literal|"/2"
argument_list|,
literal|"/4"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMaxDirItemsLimits ()
specifier|public
name|void
name|testMaxDirItemsLimits
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_MAX_DIRECTORY_ITEMS_KEY
argument_list|,
literal|0
argument_list|)
expr_stmt|;
try|try
block|{
name|mkdirs
argument_list|(
literal|"1"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"Cannot set dfs"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_MAX_DIRECTORY_ITEMS_KEY
argument_list|,
literal|64
operator|*
literal|100
operator|*
literal|1024
argument_list|)
expr_stmt|;
try|try
block|{
name|mkdirs
argument_list|(
literal|"1"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"Cannot set dfs"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testMaxComponentsAndMaxDirItems ()
specifier|public
name|void
name|testMaxComponentsAndMaxDirItems
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_MAX_COMPONENT_LENGTH_KEY
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_MAX_DIRECTORY_ITEMS_KEY
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|mkdirs
argument_list|(
literal|"/1"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|mkdirs
argument_list|(
literal|"/22"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|mkdirs
argument_list|(
literal|"/333"
argument_list|,
name|MaxDirectoryItemsExceededException
operator|.
name|class
argument_list|)
expr_stmt|;
name|mkdirs
argument_list|(
literal|"/4444"
argument_list|,
name|PathComponentTooLongException
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDuringEditLogs ()
specifier|public
name|void
name|testDuringEditLogs
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_MAX_COMPONENT_LENGTH_KEY
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_MAX_DIRECTORY_ITEMS_KEY
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|fsIsReady
operator|=
literal|false
expr_stmt|;
name|mkdirs
argument_list|(
literal|"/1"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|mkdirs
argument_list|(
literal|"/22"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|mkdirs
argument_list|(
literal|"/333"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|mkdirs
argument_list|(
literal|"/4444"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|mkdirs
argument_list|(
literal|"/1/"
operator|+
name|HdfsConstants
operator|.
name|DOT_SNAPSHOT_DIR
argument_list|,
name|HadoopIllegalArgumentException
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|method|mkdirs (String name, Class<?> expected)
specifier|private
name|void
name|mkdirs
parameter_list|(
name|String
name|name
parameter_list|,
name|Class
argument_list|<
name|?
argument_list|>
name|expected
parameter_list|)
throws|throws
name|Exception
block|{
name|lazyInitFSDirectory
argument_list|()
expr_stmt|;
name|Class
argument_list|<
name|?
argument_list|>
name|generated
init|=
literal|null
decl_stmt|;
try|try
block|{
name|fs
operator|.
name|mkdirs
argument_list|(
name|name
argument_list|,
name|perms
argument_list|,
literal|false
argument_list|,
name|now
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|generated
operator|=
name|e
operator|.
name|getClass
argument_list|()
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|generated
argument_list|)
expr_stmt|;
block|}
DECL|method|rename (String src, String dst, Class<?> expected)
specifier|private
name|void
name|rename
parameter_list|(
name|String
name|src
parameter_list|,
name|String
name|dst
parameter_list|,
name|Class
argument_list|<
name|?
argument_list|>
name|expected
parameter_list|)
throws|throws
name|Exception
block|{
name|lazyInitFSDirectory
argument_list|()
expr_stmt|;
name|Class
argument_list|<
name|?
argument_list|>
name|generated
init|=
literal|null
decl_stmt|;
try|try
block|{
name|fs
operator|.
name|renameTo
argument_list|(
name|src
argument_list|,
name|dst
argument_list|,
literal|false
argument_list|,
operator|new
name|Rename
index|[]
block|{ }
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|generated
operator|=
name|e
operator|.
name|getClass
argument_list|()
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|generated
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
DECL|method|deprecatedRename (String src, String dst, Class<?> expected)
specifier|private
name|void
name|deprecatedRename
parameter_list|(
name|String
name|src
parameter_list|,
name|String
name|dst
parameter_list|,
name|Class
argument_list|<
name|?
argument_list|>
name|expected
parameter_list|)
throws|throws
name|Exception
block|{
name|lazyInitFSDirectory
argument_list|()
expr_stmt|;
name|Class
argument_list|<
name|?
argument_list|>
name|generated
init|=
literal|null
decl_stmt|;
try|try
block|{
name|fs
operator|.
name|renameTo
argument_list|(
name|src
argument_list|,
name|dst
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|generated
operator|=
name|e
operator|.
name|getClass
argument_list|()
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|generated
argument_list|)
expr_stmt|;
block|}
DECL|method|lazyInitFSDirectory ()
specifier|private
specifier|static
name|void
name|lazyInitFSDirectory
parameter_list|()
throws|throws
name|IOException
block|{
comment|// have to create after the caller has had a chance to set conf values
if|if
condition|(
name|fs
operator|==
literal|null
condition|)
block|{
name|fs
operator|=
operator|new
name|MockFSDirectory
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

