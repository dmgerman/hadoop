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
name|crypto
operator|.
name|key
operator|.
name|JavaKeyStoreProvider
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
name|CommonConfigurationKeysPublic
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
name|FileSystemTestHelper
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
name|security
operator|.
name|UserGroupInformation
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
name|apache
operator|.
name|log4j
operator|.
name|Level
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Logger
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
name|io
operator|.
name|File
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
comment|/**  * Test the behavior of nested encryption zones.  */
end_comment

begin_class
DECL|class|TestNestedEncryptionZones
specifier|public
class|class
name|TestNestedEncryptionZones
block|{
DECL|field|testRootDir
specifier|private
name|File
name|testRootDir
decl_stmt|;
DECL|field|TOP_EZ_KEY
specifier|private
specifier|final
name|String
name|TOP_EZ_KEY
init|=
literal|"topezkey"
decl_stmt|;
DECL|field|NESTED_EZ_KEY
specifier|private
specifier|final
name|String
name|NESTED_EZ_KEY
init|=
literal|"nestedezkey"
decl_stmt|;
DECL|field|cluster
specifier|private
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|fs
specifier|protected
name|DistributedFileSystem
name|fs
decl_stmt|;
DECL|field|rootDir
specifier|private
specifier|final
name|Path
name|rootDir
init|=
operator|new
name|Path
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
DECL|field|rawDir
specifier|private
specifier|final
name|Path
name|rawDir
init|=
operator|new
name|Path
argument_list|(
literal|"/.reserved/raw/"
argument_list|)
decl_stmt|;
DECL|field|nestedEZBaseFile
specifier|private
name|Path
name|nestedEZBaseFile
init|=
operator|new
name|Path
argument_list|(
name|rootDir
argument_list|,
literal|"nestedEZBaseFile"
argument_list|)
decl_stmt|;
DECL|field|topEZBaseFile
specifier|private
name|Path
name|topEZBaseFile
init|=
operator|new
name|Path
argument_list|(
name|rootDir
argument_list|,
literal|"topEZBaseFile"
argument_list|)
decl_stmt|;
DECL|field|topEZDir
specifier|private
name|Path
name|topEZDir
decl_stmt|;
DECL|field|nestedEZDir
specifier|private
name|Path
name|nestedEZDir
decl_stmt|;
DECL|field|topEZFile
specifier|private
name|Path
name|topEZFile
decl_stmt|;
DECL|field|nestedEZFile
specifier|private
name|Path
name|nestedEZFile
decl_stmt|;
DECL|field|topEZRawFile
specifier|private
name|Path
name|topEZRawFile
decl_stmt|;
DECL|field|nestedEZRawFile
specifier|private
name|Path
name|nestedEZRawFile
decl_stmt|;
comment|// File length
DECL|field|len
specifier|private
specifier|final
name|int
name|len
init|=
literal|8196
decl_stmt|;
DECL|method|getKeyProviderURI ()
specifier|private
name|String
name|getKeyProviderURI
parameter_list|()
block|{
return|return
name|JavaKeyStoreProvider
operator|.
name|SCHEME_NAME
operator|+
literal|"://file"
operator|+
operator|new
name|Path
argument_list|(
name|testRootDir
operator|.
name|toString
argument_list|()
argument_list|,
literal|"test.jks"
argument_list|)
operator|.
name|toUri
argument_list|()
return|;
block|}
DECL|method|setProvider ()
specifier|private
name|void
name|setProvider
parameter_list|()
block|{
comment|// Need to set the client's KeyProvider to the NN's for JKS,
comment|// else the updates do not get flushed properly
name|fs
operator|.
name|getClient
argument_list|()
operator|.
name|setKeyProvider
argument_list|(
name|cluster
operator|.
name|getNameNode
argument_list|()
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getProvider
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|FileSystemTestHelper
name|fsHelper
init|=
operator|new
name|FileSystemTestHelper
argument_list|()
decl_stmt|;
comment|// Set up java key store
name|String
name|testRoot
init|=
name|fsHelper
operator|.
name|getTestRootDir
argument_list|()
decl_stmt|;
name|testRootDir
operator|=
operator|new
name|File
argument_list|(
name|testRoot
argument_list|)
operator|.
name|getAbsoluteFile
argument_list|()
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|HADOOP_SECURITY_KEY_PROVIDER_PATH
argument_list|,
name|getKeyProviderURI
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_DELEGATION_TOKEN_ALWAYS_USE_KEY
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// Lower the batch size for testing
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_LIST_ENCRYPTION_ZONES_NUM_RESPONSES
argument_list|,
literal|2
argument_list|)
expr_stmt|;
comment|// enable trash for testing
name|conf
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|FS_TRASH_INTERVAL_KEY
argument_list|,
literal|1
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
literal|1
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|Logger
operator|.
name|getLogger
argument_list|(
name|EncryptionZoneManager
operator|.
name|class
argument_list|)
operator|.
name|setLevel
argument_list|(
name|Level
operator|.
name|TRACE
argument_list|)
expr_stmt|;
name|fs
operator|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
name|setProvider
argument_list|()
expr_stmt|;
comment|// Create test keys and EZs
name|DFSTestUtil
operator|.
name|createKey
argument_list|(
name|TOP_EZ_KEY
argument_list|,
name|cluster
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|createKey
argument_list|(
name|NESTED_EZ_KEY
argument_list|,
name|cluster
argument_list|,
name|conf
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
name|cluster
operator|=
literal|null
expr_stmt|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testNestedEncryptionZones ()
specifier|public
name|void
name|testNestedEncryptionZones
parameter_list|()
throws|throws
name|Exception
block|{
name|initTopEZDirAndNestedEZDir
argument_list|(
operator|new
name|Path
argument_list|(
name|rootDir
argument_list|,
literal|"topEZ"
argument_list|)
argument_list|)
expr_stmt|;
name|verifyEncryption
argument_list|()
expr_stmt|;
comment|// Restart NameNode to test if nested EZs can be loaded from edit logs
name|cluster
operator|.
name|restartNameNodes
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
name|verifyEncryption
argument_list|()
expr_stmt|;
comment|// Checkpoint and restart NameNode, to test if nested EZs can be loaded
comment|// from fsimage
name|fs
operator|.
name|setSafeMode
argument_list|(
name|HdfsConstants
operator|.
name|SafeModeAction
operator|.
name|SAFEMODE_ENTER
argument_list|)
expr_stmt|;
name|fs
operator|.
name|saveNamespace
argument_list|()
expr_stmt|;
name|fs
operator|.
name|setSafeMode
argument_list|(
name|HdfsConstants
operator|.
name|SafeModeAction
operator|.
name|SAFEMODE_LEAVE
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|restartNameNodes
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
name|verifyEncryption
argument_list|()
expr_stmt|;
name|renameChildrenOfEZ
argument_list|()
expr_stmt|;
comment|// Verify that a non-nested EZ cannot be moved into another EZ
name|Path
name|topEZ2Dir
init|=
operator|new
name|Path
argument_list|(
name|rootDir
argument_list|,
literal|"topEZ2"
argument_list|)
decl_stmt|;
name|fs
operator|.
name|mkdir
argument_list|(
name|topEZ2Dir
argument_list|,
name|FsPermission
operator|.
name|getDirDefault
argument_list|()
argument_list|)
expr_stmt|;
name|fs
operator|.
name|createEncryptionZone
argument_list|(
name|topEZ2Dir
argument_list|,
name|TOP_EZ_KEY
argument_list|)
expr_stmt|;
try|try
block|{
name|fs
operator|.
name|rename
argument_list|(
name|topEZ2Dir
argument_list|,
operator|new
name|Path
argument_list|(
name|topEZDir
argument_list|,
literal|"topEZ2"
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Shouldn't be able to move a non-nested EZ into another "
operator|+
literal|"existing EZ."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
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
literal|"can't be moved into an encryption zone"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Should be able to rename the root dir of an EZ.
name|fs
operator|.
name|rename
argument_list|(
name|topEZDir
argument_list|,
operator|new
name|Path
argument_list|(
name|rootDir
argument_list|,
literal|"newTopEZ"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Should be able to rename the nested EZ dir within the same top EZ.
name|fs
operator|.
name|rename
argument_list|(
operator|new
name|Path
argument_list|(
name|rootDir
argument_list|,
literal|"newTopEZ/nestedEZ"
argument_list|)
argument_list|,
operator|new
name|Path
argument_list|(
name|rootDir
argument_list|,
literal|"newTopEZ/newNestedEZ"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testNestedEZWithRoot ()
specifier|public
name|void
name|testNestedEZWithRoot
parameter_list|()
throws|throws
name|Exception
block|{
name|initTopEZDirAndNestedEZDir
argument_list|(
name|rootDir
argument_list|)
expr_stmt|;
name|verifyEncryption
argument_list|()
expr_stmt|;
comment|// test rename file
name|renameChildrenOfEZ
argument_list|()
expr_stmt|;
specifier|final
name|String
name|currentUser
init|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|getShortUserName
argument_list|()
decl_stmt|;
specifier|final
name|Path
name|suffixTrashPath
init|=
operator|new
name|Path
argument_list|(
name|FileSystem
operator|.
name|TRASH_PREFIX
argument_list|,
name|currentUser
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|rootTrash
init|=
name|fs
operator|.
name|getTrashRoot
argument_list|(
name|rootDir
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|topEZTrash
init|=
name|fs
operator|.
name|getTrashRoot
argument_list|(
name|topEZFile
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|nestedEZTrash
init|=
name|fs
operator|.
name|getTrashRoot
argument_list|(
name|nestedEZFile
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|expectedTopEZTrash
init|=
name|fs
operator|.
name|makeQualified
argument_list|(
operator|new
name|Path
argument_list|(
name|topEZDir
argument_list|,
name|suffixTrashPath
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|expectedNestedEZTrash
init|=
name|fs
operator|.
name|makeQualified
argument_list|(
operator|new
name|Path
argument_list|(
name|nestedEZDir
argument_list|,
name|suffixTrashPath
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Top ez trash should be "
operator|+
name|expectedTopEZTrash
argument_list|,
name|expectedTopEZTrash
argument_list|,
name|topEZTrash
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Root trash should be equal with TopEZFile trash"
argument_list|,
name|topEZTrash
argument_list|,
name|rootTrash
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Nested ez Trash should be "
operator|+
name|expectedNestedEZTrash
argument_list|,
name|expectedNestedEZTrash
argument_list|,
name|nestedEZTrash
argument_list|)
expr_stmt|;
comment|// delete rename file and test trash
name|FsShell
name|shell
init|=
operator|new
name|FsShell
argument_list|(
name|fs
operator|.
name|getConf
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|topTrashFile
init|=
operator|new
name|Path
argument_list|(
name|shell
operator|.
name|getCurrentTrashDir
argument_list|(
name|topEZFile
argument_list|)
operator|+
literal|"/"
operator|+
name|topEZFile
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|nestedTrashFile
init|=
operator|new
name|Path
argument_list|(
name|shell
operator|.
name|getCurrentTrashDir
argument_list|(
name|nestedEZFile
argument_list|)
operator|+
literal|"/"
operator|+
name|nestedEZFile
argument_list|)
decl_stmt|;
name|ToolRunner
operator|.
name|run
argument_list|(
name|shell
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"-rm"
block|,
name|topEZFile
operator|.
name|toString
argument_list|()
block|}
argument_list|)
expr_stmt|;
name|ToolRunner
operator|.
name|run
argument_list|(
name|shell
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"-rm"
block|,
name|nestedEZFile
operator|.
name|toString
argument_list|()
block|}
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"File not in trash : "
operator|+
name|topTrashFile
argument_list|,
name|fs
operator|.
name|exists
argument_list|(
name|topTrashFile
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"File not in trash : "
operator|+
name|nestedTrashFile
argument_list|,
name|fs
operator|.
name|exists
argument_list|(
name|nestedTrashFile
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|renameChildrenOfEZ ()
specifier|private
name|void
name|renameChildrenOfEZ
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|renamedTopEZFile
init|=
operator|new
name|Path
argument_list|(
name|topEZDir
argument_list|,
literal|"renamedFile"
argument_list|)
decl_stmt|;
name|Path
name|renamedNestedEZFile
init|=
operator|new
name|Path
argument_list|(
name|nestedEZDir
argument_list|,
literal|"renamedFile"
argument_list|)
decl_stmt|;
comment|//Should be able to rename files within the same EZ.
name|fs
operator|.
name|rename
argument_list|(
name|topEZFile
argument_list|,
name|renamedTopEZFile
argument_list|)
expr_stmt|;
name|fs
operator|.
name|rename
argument_list|(
name|nestedEZFile
argument_list|,
name|renamedNestedEZFile
argument_list|)
expr_stmt|;
name|topEZFile
operator|=
name|renamedTopEZFile
expr_stmt|;
name|nestedEZFile
operator|=
name|renamedNestedEZFile
expr_stmt|;
name|topEZRawFile
operator|=
operator|new
name|Path
argument_list|(
name|rawDir
operator|+
name|topEZFile
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|nestedEZRawFile
operator|=
operator|new
name|Path
argument_list|(
name|rawDir
operator|+
name|nestedEZFile
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|verifyEncryption
argument_list|()
expr_stmt|;
comment|// Verify that files in top EZ cannot be moved into the nested EZ, and
comment|// vice versa.
try|try
block|{
name|fs
operator|.
name|rename
argument_list|(
name|topEZFile
argument_list|,
operator|new
name|Path
argument_list|(
name|nestedEZDir
argument_list|,
literal|"movedTopEZFile"
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Shouldn't be able to rename between top EZ and nested EZ."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
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
literal|"can't be moved from encryption zone "
operator|+
name|topEZDir
operator|.
name|toString
argument_list|()
operator|+
literal|" to encryption zone "
operator|+
name|nestedEZDir
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|fs
operator|.
name|rename
argument_list|(
name|nestedEZFile
argument_list|,
operator|new
name|Path
argument_list|(
name|topEZDir
argument_list|,
literal|"movedNestedEZFile"
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Shouldn't be able to rename between top EZ and nested EZ."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
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
literal|"can't be moved from encryption zone "
operator|+
name|nestedEZDir
operator|.
name|toString
argument_list|()
operator|+
literal|" to encryption zone "
operator|+
name|topEZDir
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Verify that the nested EZ cannot be moved out of the top EZ.
try|try
block|{
name|fs
operator|.
name|rename
argument_list|(
name|nestedEZFile
argument_list|,
operator|new
name|Path
argument_list|(
name|rootDir
argument_list|,
literal|"movedNestedEZFile"
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Shouldn't be able to move the nested EZ out of the top EZ."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|String
name|exceptionMsg
init|=
name|e
operator|.
name|getMessage
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|exceptionMsg
operator|.
name|contains
argument_list|(
literal|"can't be moved from"
argument_list|)
operator|&&
name|exceptionMsg
operator|.
name|contains
argument_list|(
literal|"encryption zone"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|initTopEZDirAndNestedEZDir (Path topPath)
specifier|private
name|void
name|initTopEZDirAndNestedEZDir
parameter_list|(
name|Path
name|topPath
parameter_list|)
throws|throws
name|Exception
block|{
comment|// init fs root directory
name|fs
operator|.
name|delete
argument_list|(
name|rootDir
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// init top and nested path or file
name|topEZDir
operator|=
name|topPath
expr_stmt|;
name|nestedEZDir
operator|=
operator|new
name|Path
argument_list|(
name|topEZDir
argument_list|,
literal|"nestedEZ"
argument_list|)
expr_stmt|;
name|topEZFile
operator|=
operator|new
name|Path
argument_list|(
name|topEZDir
argument_list|,
literal|"file"
argument_list|)
expr_stmt|;
name|nestedEZFile
operator|=
operator|new
name|Path
argument_list|(
name|nestedEZDir
argument_list|,
literal|"file"
argument_list|)
expr_stmt|;
name|topEZRawFile
operator|=
operator|new
name|Path
argument_list|(
name|rawDir
operator|+
name|topEZFile
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|nestedEZRawFile
operator|=
operator|new
name|Path
argument_list|(
name|rawDir
operator|+
name|nestedEZFile
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
comment|// create ez zone
name|fs
operator|.
name|mkdir
argument_list|(
name|topEZDir
argument_list|,
name|FsPermission
operator|.
name|getDirDefault
argument_list|()
argument_list|)
expr_stmt|;
name|fs
operator|.
name|createEncryptionZone
argument_list|(
name|topEZDir
argument_list|,
name|TOP_EZ_KEY
argument_list|)
expr_stmt|;
name|fs
operator|.
name|mkdir
argument_list|(
name|nestedEZDir
argument_list|,
name|FsPermission
operator|.
name|getDirDefault
argument_list|()
argument_list|)
expr_stmt|;
name|fs
operator|.
name|createEncryptionZone
argument_list|(
name|nestedEZDir
argument_list|,
name|NESTED_EZ_KEY
argument_list|)
expr_stmt|;
comment|// create files
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|topEZBaseFile
argument_list|,
name|len
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
literal|0xFEED
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|topEZFile
argument_list|,
name|len
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
literal|0xFEED
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|nestedEZBaseFile
argument_list|,
name|len
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
literal|0xFEED
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|nestedEZFile
argument_list|,
name|len
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
literal|0xFEED
argument_list|)
expr_stmt|;
block|}
DECL|method|verifyEncryption ()
specifier|private
name|void
name|verifyEncryption
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|"Top EZ dir is encrypted"
argument_list|,
literal|true
argument_list|,
name|fs
operator|.
name|getFileStatus
argument_list|(
name|topEZDir
argument_list|)
operator|.
name|isEncrypted
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Nested EZ dir is encrypted"
argument_list|,
literal|true
argument_list|,
name|fs
operator|.
name|getFileStatus
argument_list|(
name|nestedEZDir
argument_list|)
operator|.
name|isEncrypted
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Top zone file is encrypted"
argument_list|,
literal|true
argument_list|,
name|fs
operator|.
name|getFileStatus
argument_list|(
name|topEZFile
argument_list|)
operator|.
name|isEncrypted
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Nested zone file is encrypted"
argument_list|,
literal|true
argument_list|,
name|fs
operator|.
name|getFileStatus
argument_list|(
name|nestedEZFile
argument_list|)
operator|.
name|isEncrypted
argument_list|()
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|verifyFilesEqual
argument_list|(
name|fs
argument_list|,
name|topEZBaseFile
argument_list|,
name|topEZFile
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|verifyFilesEqual
argument_list|(
name|fs
argument_list|,
name|nestedEZBaseFile
argument_list|,
name|nestedEZFile
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|verifyFilesNotEqual
argument_list|(
name|fs
argument_list|,
name|topEZRawFile
argument_list|,
name|nestedEZRawFile
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

