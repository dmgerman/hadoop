begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
package|;
end_package

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
name|FileContextTestWrapper
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
name|FileSystemTestWrapper
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
name|client
operator|.
name|CreateEncryptionZoneFlag
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
name|client
operator|.
name|HdfsAdmin
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
name|EncryptionZoneManager
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
name|FSDirectory
operator|.
name|DirOp
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
name|INodesInPath
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
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSTestUtil
operator|.
name|verifyFilesEqual
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
name|DFSTestUtil
operator|.
name|verifyFilesNotEqual
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
name|test
operator|.
name|GenericTestUtils
operator|.
name|assertExceptionContains
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
name|test
operator|.
name|GenericTestUtils
operator|.
name|assertMatches
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
name|assertFalse
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

begin_class
DECL|class|TestReservedRawPaths
specifier|public
class|class
name|TestReservedRawPaths
block|{
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|fsHelper
specifier|private
name|FileSystemTestHelper
name|fsHelper
decl_stmt|;
DECL|field|cluster
specifier|private
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|dfsAdmin
specifier|private
name|HdfsAdmin
name|dfsAdmin
decl_stmt|;
DECL|field|fs
specifier|private
name|DistributedFileSystem
name|fs
decl_stmt|;
DECL|field|TEST_KEY
specifier|private
specifier|final
name|String
name|TEST_KEY
init|=
literal|"test_key"
decl_stmt|;
DECL|field|fsWrapper
specifier|protected
name|FileSystemTestWrapper
name|fsWrapper
decl_stmt|;
DECL|field|fcWrapper
specifier|protected
name|FileContextTestWrapper
name|fcWrapper
decl_stmt|;
DECL|field|NO_TRASH
specifier|protected
specifier|static
specifier|final
name|EnumSet
argument_list|<
name|CreateEncryptionZoneFlag
argument_list|>
name|NO_TRASH
init|=
name|EnumSet
operator|.
name|of
argument_list|(
name|CreateEncryptionZoneFlag
operator|.
name|NO_TRASH
argument_list|)
decl_stmt|;
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
name|conf
operator|=
operator|new
name|HdfsConfiguration
argument_list|()
expr_stmt|;
name|fsHelper
operator|=
operator|new
name|FileSystemTestHelper
argument_list|()
expr_stmt|;
comment|// Set up java key store
name|String
name|testRoot
init|=
name|fsHelper
operator|.
name|getTestRootDir
argument_list|()
decl_stmt|;
name|File
name|testRootDir
init|=
operator|new
name|File
argument_list|(
name|testRoot
argument_list|)
operator|.
name|getAbsoluteFile
argument_list|()
decl_stmt|;
specifier|final
name|Path
name|jksPath
init|=
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
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|HADOOP_SECURITY_KEY_PROVIDER_PATH
argument_list|,
name|JavaKeyStoreProvider
operator|.
name|SCHEME_NAME
operator|+
literal|"://file"
operator|+
name|jksPath
operator|.
name|toUri
argument_list|()
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
name|fsWrapper
operator|=
operator|new
name|FileSystemTestWrapper
argument_list|(
name|cluster
operator|.
name|getFileSystem
argument_list|()
argument_list|)
expr_stmt|;
name|fcWrapper
operator|=
operator|new
name|FileContextTestWrapper
argument_list|(
name|FileContext
operator|.
name|getFileContext
argument_list|(
name|cluster
operator|.
name|getURI
argument_list|()
argument_list|,
name|conf
argument_list|)
argument_list|)
expr_stmt|;
name|dfsAdmin
operator|=
operator|new
name|HdfsAdmin
argument_list|(
name|cluster
operator|.
name|getURI
argument_list|()
argument_list|,
name|conf
argument_list|)
expr_stmt|;
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
name|DFSTestUtil
operator|.
name|createKey
argument_list|(
name|TEST_KEY
argument_list|,
name|cluster
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|teardown ()
specifier|public
name|void
name|teardown
parameter_list|()
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
comment|/**    * Verify resolving path will return an iip that tracks if the original    * path was a raw path.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|120000
argument_list|)
DECL|method|testINodesInPath ()
specifier|public
name|void
name|testINodesInPath
parameter_list|()
throws|throws
name|IOException
block|{
name|FSDirectory
name|fsd
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getFSDirectory
argument_list|()
decl_stmt|;
specifier|final
name|String
name|path
init|=
literal|"/path"
decl_stmt|;
name|INodesInPath
name|iip
init|=
name|fsd
operator|.
name|resolvePath
argument_list|(
literal|null
argument_list|,
name|path
argument_list|,
name|DirOp
operator|.
name|READ
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|iip
operator|.
name|isRaw
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|path
argument_list|,
name|iip
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|iip
operator|=
name|fsd
operator|.
name|resolvePath
argument_list|(
literal|null
argument_list|,
literal|"/.reserved/raw"
operator|+
name|path
argument_list|,
name|DirOp
operator|.
name|READ
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|iip
operator|.
name|isRaw
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|path
argument_list|,
name|iip
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Basic read/write tests of raw files.    * Create a non-encrypted file    * Create an encryption zone    * Verify that non-encrypted file contents and decrypted file in EZ are equal    * Compare the raw encrypted bytes of the file with the decrypted version to    *   ensure they're different    * Compare the raw and non-raw versions of the non-encrypted file to ensure    *   they're the same.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|120000
argument_list|)
DECL|method|testReadWriteRaw ()
specifier|public
name|void
name|testReadWriteRaw
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Create a base file for comparison
specifier|final
name|Path
name|baseFile
init|=
operator|new
name|Path
argument_list|(
literal|"/base"
argument_list|)
decl_stmt|;
specifier|final
name|int
name|len
init|=
literal|8192
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|baseFile
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
comment|// Create the first enc file
specifier|final
name|Path
name|zone
init|=
operator|new
name|Path
argument_list|(
literal|"/zone"
argument_list|)
decl_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|zone
argument_list|)
expr_stmt|;
name|dfsAdmin
operator|.
name|createEncryptionZone
argument_list|(
name|zone
argument_list|,
name|TEST_KEY
argument_list|,
name|NO_TRASH
argument_list|)
expr_stmt|;
specifier|final
name|Path
name|encFile1
init|=
operator|new
name|Path
argument_list|(
name|zone
argument_list|,
literal|"myfile"
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|encFile1
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
comment|// Read them back in and compare byte-by-byte
name|verifyFilesEqual
argument_list|(
name|fs
argument_list|,
name|baseFile
argument_list|,
name|encFile1
argument_list|,
name|len
argument_list|)
expr_stmt|;
comment|// Raw file should be different from encrypted file
specifier|final
name|Path
name|encFile1Raw
init|=
operator|new
name|Path
argument_list|(
name|zone
argument_list|,
literal|"/.reserved/raw/zone/myfile"
argument_list|)
decl_stmt|;
name|verifyFilesNotEqual
argument_list|(
name|fs
argument_list|,
name|encFile1Raw
argument_list|,
name|encFile1
argument_list|,
name|len
argument_list|)
expr_stmt|;
comment|// Raw file should be same as /base which is not in an EZ
specifier|final
name|Path
name|baseFileRaw
init|=
operator|new
name|Path
argument_list|(
name|zone
argument_list|,
literal|"/.reserved/raw/base"
argument_list|)
decl_stmt|;
name|verifyFilesEqual
argument_list|(
name|fs
argument_list|,
name|baseFile
argument_list|,
name|baseFileRaw
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
DECL|method|assertPathEquals (Path p1, Path p2)
specifier|private
name|void
name|assertPathEquals
parameter_list|(
name|Path
name|p1
parameter_list|,
name|Path
name|p2
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|FileStatus
name|p1Stat
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|p1
argument_list|)
decl_stmt|;
specifier|final
name|FileStatus
name|p2Stat
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|p2
argument_list|)
decl_stmt|;
comment|/*      * Use accessTime and modificationTime as substitutes for INode to check      * for resolution to the same underlying file.      */
name|assertEquals
argument_list|(
literal|"Access times not equal"
argument_list|,
name|p1Stat
operator|.
name|getAccessTime
argument_list|()
argument_list|,
name|p2Stat
operator|.
name|getAccessTime
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Modification times not equal"
argument_list|,
name|p1Stat
operator|.
name|getModificationTime
argument_list|()
argument_list|,
name|p2Stat
operator|.
name|getModificationTime
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"pathname1 not equal"
argument_list|,
name|p1
argument_list|,
name|Path
operator|.
name|getPathWithoutSchemeAndAuthority
argument_list|(
name|p1Stat
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"pathname1 not equal"
argument_list|,
name|p2
argument_list|,
name|Path
operator|.
name|getPathWithoutSchemeAndAuthority
argument_list|(
name|p2Stat
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests that getFileStatus on raw and non raw resolve to the same    * file.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|120000
argument_list|)
DECL|method|testGetFileStatus ()
specifier|public
name|void
name|testGetFileStatus
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Path
name|zone
init|=
operator|new
name|Path
argument_list|(
literal|"zone"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|slashZone
init|=
operator|new
name|Path
argument_list|(
literal|"/"
argument_list|,
name|zone
argument_list|)
decl_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|slashZone
argument_list|)
expr_stmt|;
name|dfsAdmin
operator|.
name|createEncryptionZone
argument_list|(
name|slashZone
argument_list|,
name|TEST_KEY
argument_list|,
name|NO_TRASH
argument_list|)
expr_stmt|;
specifier|final
name|Path
name|base
init|=
operator|new
name|Path
argument_list|(
literal|"base"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|reservedRaw
init|=
operator|new
name|Path
argument_list|(
literal|"/.reserved/raw"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|baseRaw
init|=
operator|new
name|Path
argument_list|(
name|reservedRaw
argument_list|,
name|base
argument_list|)
decl_stmt|;
specifier|final
name|int
name|len
init|=
literal|8192
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|baseRaw
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
name|assertPathEquals
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/"
argument_list|,
name|base
argument_list|)
argument_list|,
name|baseRaw
argument_list|)
expr_stmt|;
comment|/* Repeat the test for a file in an ez. */
specifier|final
name|Path
name|ezEncFile
init|=
operator|new
name|Path
argument_list|(
name|slashZone
argument_list|,
name|base
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|ezRawEncFile
init|=
operator|new
name|Path
argument_list|(
operator|new
name|Path
argument_list|(
name|reservedRaw
argument_list|,
name|zone
argument_list|)
argument_list|,
name|base
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|ezEncFile
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
name|assertPathEquals
argument_list|(
name|ezEncFile
argument_list|,
name|ezRawEncFile
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|120000
argument_list|)
DECL|method|testReservedRoot ()
specifier|public
name|void
name|testReservedRoot
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Path
name|root
init|=
operator|new
name|Path
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|rawRoot
init|=
operator|new
name|Path
argument_list|(
literal|"/.reserved/raw"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|rawRootSlash
init|=
operator|new
name|Path
argument_list|(
literal|"/.reserved/raw/"
argument_list|)
decl_stmt|;
name|assertPathEquals
argument_list|(
name|root
argument_list|,
name|rawRoot
argument_list|)
expr_stmt|;
name|assertPathEquals
argument_list|(
name|root
argument_list|,
name|rawRootSlash
argument_list|)
expr_stmt|;
block|}
comment|/* Verify mkdir works ok in .reserved/raw directory. */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|120000
argument_list|)
DECL|method|testReservedRawMkdir ()
specifier|public
name|void
name|testReservedRawMkdir
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Path
name|zone
init|=
operator|new
name|Path
argument_list|(
literal|"zone"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|slashZone
init|=
operator|new
name|Path
argument_list|(
literal|"/"
argument_list|,
name|zone
argument_list|)
decl_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|slashZone
argument_list|)
expr_stmt|;
name|dfsAdmin
operator|.
name|createEncryptionZone
argument_list|(
name|slashZone
argument_list|,
name|TEST_KEY
argument_list|,
name|NO_TRASH
argument_list|)
expr_stmt|;
specifier|final
name|Path
name|rawRoot
init|=
operator|new
name|Path
argument_list|(
literal|"/.reserved/raw"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|dir1
init|=
operator|new
name|Path
argument_list|(
literal|"dir1"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|rawDir1
init|=
operator|new
name|Path
argument_list|(
name|rawRoot
argument_list|,
name|dir1
argument_list|)
decl_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|rawDir1
argument_list|)
expr_stmt|;
name|assertPathEquals
argument_list|(
name|rawDir1
argument_list|,
operator|new
name|Path
argument_list|(
literal|"/"
argument_list|,
name|dir1
argument_list|)
argument_list|)
expr_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|rawDir1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
specifier|final
name|Path
name|rawZone
init|=
operator|new
name|Path
argument_list|(
name|rawRoot
argument_list|,
name|zone
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|rawDir1EZ
init|=
operator|new
name|Path
argument_list|(
name|rawZone
argument_list|,
name|dir1
argument_list|)
decl_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|rawDir1EZ
argument_list|)
expr_stmt|;
name|assertPathEquals
argument_list|(
name|rawDir1EZ
argument_list|,
operator|new
name|Path
argument_list|(
name|slashZone
argument_list|,
name|dir1
argument_list|)
argument_list|)
expr_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|rawDir1EZ
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|120000
argument_list|)
DECL|method|testRelativePathnames ()
specifier|public
name|void
name|testRelativePathnames
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Path
name|baseFileRaw
init|=
operator|new
name|Path
argument_list|(
literal|"/.reserved/raw/base"
argument_list|)
decl_stmt|;
specifier|final
name|int
name|len
init|=
literal|8192
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|baseFileRaw
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
specifier|final
name|Path
name|root
init|=
operator|new
name|Path
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|rawRoot
init|=
operator|new
name|Path
argument_list|(
literal|"/.reserved/raw"
argument_list|)
decl_stmt|;
name|assertPathEquals
argument_list|(
name|root
argument_list|,
operator|new
name|Path
argument_list|(
name|rawRoot
argument_list|,
literal|"../raw"
argument_list|)
argument_list|)
expr_stmt|;
name|assertPathEquals
argument_list|(
name|root
argument_list|,
operator|new
name|Path
argument_list|(
name|rawRoot
argument_list|,
literal|"../../.reserved/raw"
argument_list|)
argument_list|)
expr_stmt|;
name|assertPathEquals
argument_list|(
name|baseFileRaw
argument_list|,
operator|new
name|Path
argument_list|(
name|rawRoot
argument_list|,
literal|"../raw/base"
argument_list|)
argument_list|)
expr_stmt|;
name|assertPathEquals
argument_list|(
name|baseFileRaw
argument_list|,
operator|new
name|Path
argument_list|(
name|rawRoot
argument_list|,
literal|"../../.reserved/raw/base"
argument_list|)
argument_list|)
expr_stmt|;
name|assertPathEquals
argument_list|(
name|baseFileRaw
argument_list|,
operator|new
name|Path
argument_list|(
name|rawRoot
argument_list|,
literal|"../../.reserved/raw/base/../base"
argument_list|)
argument_list|)
expr_stmt|;
name|assertPathEquals
argument_list|(
name|baseFileRaw
argument_list|,
operator|new
name|Path
argument_list|(
literal|"/.reserved/../.reserved/raw/../raw/base"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|120000
argument_list|)
DECL|method|testUserReadAccessOnly ()
specifier|public
name|void
name|testUserReadAccessOnly
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Path
name|zone
init|=
operator|new
name|Path
argument_list|(
literal|"zone"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|slashZone
init|=
operator|new
name|Path
argument_list|(
literal|"/"
argument_list|,
name|zone
argument_list|)
decl_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|slashZone
argument_list|)
expr_stmt|;
name|dfsAdmin
operator|.
name|createEncryptionZone
argument_list|(
name|slashZone
argument_list|,
name|TEST_KEY
argument_list|,
name|NO_TRASH
argument_list|)
expr_stmt|;
specifier|final
name|Path
name|base
init|=
operator|new
name|Path
argument_list|(
literal|"base"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|reservedRaw
init|=
operator|new
name|Path
argument_list|(
literal|"/.reserved/raw"
argument_list|)
decl_stmt|;
specifier|final
name|int
name|len
init|=
literal|8192
decl_stmt|;
comment|/* Test failure of create file in reserved/raw as non admin */
specifier|final
name|UserGroupInformation
name|user
init|=
name|UserGroupInformation
operator|.
name|createUserForTesting
argument_list|(
literal|"user"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"mygroup"
block|}
argument_list|)
decl_stmt|;
name|user
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Object
name|run
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
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
specifier|final
name|Path
name|ezRawEncFile
init|=
operator|new
name|Path
argument_list|(
operator|new
name|Path
argument_list|(
name|reservedRaw
argument_list|,
name|zone
argument_list|)
argument_list|,
name|base
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|ezRawEncFile
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
name|fail
argument_list|(
literal|"access to /.reserved/raw is superuser-only operation"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AccessControlException
name|e
parameter_list|)
block|{
name|assertExceptionContains
argument_list|(
literal|"Superuser privilege is required"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
comment|/* Test success of getFileStatus in reserved/raw as non admin since      * read is allowed. */
specifier|final
name|Path
name|ezRawEncFile
init|=
operator|new
name|Path
argument_list|(
operator|new
name|Path
argument_list|(
name|reservedRaw
argument_list|,
name|zone
argument_list|)
argument_list|,
name|base
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|ezRawEncFile
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
name|user
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Object
name|run
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|DistributedFileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|fs
operator|.
name|getFileStatus
argument_list|(
name|ezRawEncFile
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
comment|/* Test success of listStatus in reserved/raw as non admin since read is      * allowed. */
name|user
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Object
name|run
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|DistributedFileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|fs
operator|.
name|listStatus
argument_list|(
name|ezRawEncFile
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|fs
operator|.
name|setPermission
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/"
argument_list|)
argument_list|,
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
literal|0777
argument_list|)
argument_list|)
expr_stmt|;
comment|/* Test failure of mkdir in reserved/raw as non admin */
name|user
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Object
name|run
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|DistributedFileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
specifier|final
name|Path
name|d1
init|=
operator|new
name|Path
argument_list|(
name|reservedRaw
argument_list|,
literal|"dir1"
argument_list|)
decl_stmt|;
try|try
block|{
name|fs
operator|.
name|mkdirs
argument_list|(
name|d1
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"access to /.reserved/raw is superuser-only operation"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AccessControlException
name|e
parameter_list|)
block|{
name|assertExceptionContains
argument_list|(
literal|"Superuser privilege is required"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|120000
argument_list|)
DECL|method|testListDotReserved ()
specifier|public
name|void
name|testListDotReserved
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Create a base file for comparison
specifier|final
name|Path
name|baseFileRaw
init|=
operator|new
name|Path
argument_list|(
literal|"/.reserved/raw/base"
argument_list|)
decl_stmt|;
specifier|final
name|int
name|len
init|=
literal|8192
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|baseFileRaw
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
comment|/*      * Ensure that you can list /.reserved, with results: raw and .inodes      */
name|FileStatus
index|[]
name|stats
init|=
name|fs
operator|.
name|listStatus
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/.reserved"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|stats
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|FSDirectory
operator|.
name|DOT_INODES_STRING
argument_list|,
name|stats
index|[
literal|0
index|]
operator|.
name|getPath
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"raw"
argument_list|,
name|stats
index|[
literal|1
index|]
operator|.
name|getPath
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|fs
operator|.
name|listStatus
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/.reserved/.inodes"
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"expected FNFE"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e
parameter_list|)
block|{
name|assertExceptionContains
argument_list|(
literal|"/.reserved/.inodes does not exist"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
specifier|final
name|FileStatus
index|[]
name|fileStatuses
init|=
name|fs
operator|.
name|listStatus
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/.reserved/raw"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"expected 1 entry"
argument_list|,
name|fileStatuses
operator|.
name|length
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertMatches
argument_list|(
name|fileStatuses
index|[
literal|0
index|]
operator|.
name|getPath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
literal|"/.reserved/raw/base"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|120000
argument_list|)
DECL|method|testListRecursive ()
specifier|public
name|void
name|testListRecursive
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|rootPath
init|=
operator|new
name|Path
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|Path
name|p
init|=
name|rootPath
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|3
condition|;
name|i
operator|++
control|)
block|{
name|p
operator|=
operator|new
name|Path
argument_list|(
name|p
argument_list|,
literal|"dir"
operator|+
name|i
argument_list|)
expr_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
name|Path
name|curPath
init|=
operator|new
name|Path
argument_list|(
literal|"/.reserved/raw"
argument_list|)
decl_stmt|;
name|int
name|cnt
init|=
literal|0
decl_stmt|;
name|FileStatus
index|[]
name|fileStatuses
init|=
name|fs
operator|.
name|listStatus
argument_list|(
name|curPath
argument_list|)
decl_stmt|;
while|while
condition|(
name|fileStatuses
operator|!=
literal|null
operator|&&
name|fileStatuses
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|FileStatus
name|f
init|=
name|fileStatuses
index|[
literal|0
index|]
decl_stmt|;
name|assertMatches
argument_list|(
name|f
operator|.
name|getPath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
literal|"/.reserved/raw"
argument_list|)
expr_stmt|;
name|curPath
operator|=
name|Path
operator|.
name|getPathWithoutSchemeAndAuthority
argument_list|(
name|f
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|cnt
operator|++
expr_stmt|;
name|fileStatuses
operator|=
name|fs
operator|.
name|listStatus
argument_list|(
name|curPath
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|cnt
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

