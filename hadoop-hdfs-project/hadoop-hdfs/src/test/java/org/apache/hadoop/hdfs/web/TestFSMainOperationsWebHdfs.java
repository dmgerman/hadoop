begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.web
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|web
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|spy
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
name|doReturn
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
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|HttpURLConnection
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
name|URL
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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|impl
operator|.
name|Log4JLogger
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
name|FSMainOperationsBaseTest
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
name|AppendTestUtil
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
name|web
operator|.
name|resources
operator|.
name|ExceptionHandler
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
name|web
operator|.
name|resources
operator|.
name|GetOpParam
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
name|web
operator|.
name|resources
operator|.
name|HttpOpParam
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
DECL|class|TestFSMainOperationsWebHdfs
specifier|public
class|class
name|TestFSMainOperationsWebHdfs
extends|extends
name|FSMainOperationsBaseTest
block|{
block|{
operator|(
operator|(
name|Log4JLogger
operator|)
name|ExceptionHandler
operator|.
name|LOG
operator|)
operator|.
name|getLogger
argument_list|()
operator|.
name|setLevel
argument_list|(
name|Level
operator|.
name|ALL
argument_list|)
expr_stmt|;
block|}
DECL|field|cluster
specifier|private
specifier|static
name|MiniDFSCluster
name|cluster
init|=
literal|null
decl_stmt|;
DECL|field|defaultWorkingDirectory
specifier|private
specifier|static
name|Path
name|defaultWorkingDirectory
decl_stmt|;
DECL|field|fileSystem
specifier|private
specifier|static
name|FileSystem
name|fileSystem
decl_stmt|;
DECL|method|TestFSMainOperationsWebHdfs ()
specifier|public
name|TestFSMainOperationsWebHdfs
parameter_list|()
block|{
name|super
argument_list|(
literal|"/tmp/TestFSMainOperationsWebHdfs"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createFileSystem ()
specifier|protected
name|FileSystem
name|createFileSystem
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|fileSystem
return|;
block|}
annotation|@
name|BeforeClass
DECL|method|setupCluster ()
specifier|public
specifier|static
name|void
name|setupCluster
parameter_list|()
block|{
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BLOCK_SIZE_KEY
argument_list|,
literal|1024
argument_list|)
expr_stmt|;
try|try
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
literal|2
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
comment|//change root permission to 777
name|cluster
operator|.
name|getFileSystem
argument_list|()
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
specifier|final
name|String
name|uri
init|=
name|WebHdfsConstants
operator|.
name|WEBHDFS_SCHEME
operator|+
literal|"://"
operator|+
name|conf
operator|.
name|get
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_HTTP_ADDRESS_KEY
argument_list|)
decl_stmt|;
comment|//get file system as a non-superuser
specifier|final
name|UserGroupInformation
name|current
init|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
decl_stmt|;
specifier|final
name|UserGroupInformation
name|ugi
init|=
name|UserGroupInformation
operator|.
name|createUserForTesting
argument_list|(
name|current
operator|.
name|getShortUserName
argument_list|()
operator|+
literal|"x"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"user"
block|}
argument_list|)
decl_stmt|;
name|fileSystem
operator|=
name|ugi
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|FileSystem
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|FileSystem
name|run
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|FileSystem
operator|.
name|get
argument_list|(
operator|new
name|URI
argument_list|(
name|uri
argument_list|)
argument_list|,
name|conf
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|defaultWorkingDirectory
operator|=
name|fileSystem
operator|.
name|getWorkingDirectory
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|AfterClass
DECL|method|shutdownCluster ()
specifier|public
specifier|static
name|void
name|shutdownCluster
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
name|Test
DECL|method|testConcat ()
specifier|public
name|void
name|testConcat
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
index|[]
name|paths
init|=
block|{
operator|new
name|Path
argument_list|(
literal|"/test/hadoop/file1"
argument_list|)
block|,
operator|new
name|Path
argument_list|(
literal|"/test/hadoop/file2"
argument_list|)
block|,
operator|new
name|Path
argument_list|(
literal|"/test/hadoop/file3"
argument_list|)
block|}
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fSys
argument_list|,
name|paths
index|[
literal|0
index|]
argument_list|,
literal|1024
argument_list|,
operator|(
name|short
operator|)
literal|3
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fSys
argument_list|,
name|paths
index|[
literal|1
index|]
argument_list|,
literal|1024
argument_list|,
operator|(
name|short
operator|)
literal|3
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fSys
argument_list|,
name|paths
index|[
literal|2
index|]
argument_list|,
literal|1024
argument_list|,
operator|(
name|short
operator|)
literal|3
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|Path
name|catPath
init|=
operator|new
name|Path
argument_list|(
literal|"/test/hadoop/catFile"
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fSys
argument_list|,
name|catPath
argument_list|,
literal|1024
argument_list|,
operator|(
name|short
operator|)
literal|3
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|exists
argument_list|(
name|fSys
argument_list|,
name|catPath
argument_list|)
argument_list|)
expr_stmt|;
name|fSys
operator|.
name|concat
argument_list|(
name|catPath
argument_list|,
name|paths
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|exists
argument_list|(
name|fSys
argument_list|,
name|paths
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|exists
argument_list|(
name|fSys
argument_list|,
name|paths
index|[
literal|1
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|exists
argument_list|(
name|fSys
argument_list|,
name|paths
index|[
literal|2
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|FileStatus
name|fileStatus
init|=
name|fSys
operator|.
name|getFileStatus
argument_list|(
name|catPath
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1024
operator|*
literal|4
argument_list|,
name|fileStatus
operator|.
name|getLen
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testTruncate ()
specifier|public
name|void
name|testTruncate
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|short
name|repl
init|=
literal|3
decl_stmt|;
specifier|final
name|int
name|blockSize
init|=
literal|1024
decl_stmt|;
specifier|final
name|int
name|numOfBlocks
init|=
literal|2
decl_stmt|;
name|Path
name|dir
init|=
name|getTestRootPath
argument_list|(
name|fSys
argument_list|,
literal|"test/hadoop"
argument_list|)
decl_stmt|;
name|Path
name|file
init|=
name|getTestRootPath
argument_list|(
name|fSys
argument_list|,
literal|"test/hadoop/file"
argument_list|)
decl_stmt|;
specifier|final
name|byte
index|[]
name|data
init|=
name|getFileData
argument_list|(
name|numOfBlocks
argument_list|,
name|blockSize
argument_list|)
decl_stmt|;
name|createFile
argument_list|(
name|fSys
argument_list|,
name|file
argument_list|,
name|data
argument_list|,
name|blockSize
argument_list|,
name|repl
argument_list|)
expr_stmt|;
specifier|final
name|int
name|newLength
init|=
name|blockSize
decl_stmt|;
name|boolean
name|isReady
init|=
name|fSys
operator|.
name|truncate
argument_list|(
name|file
argument_list|,
name|newLength
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Recovery is not expected."
argument_list|,
name|isReady
argument_list|)
expr_stmt|;
name|FileStatus
name|fileStatus
init|=
name|fSys
operator|.
name|getFileStatus
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|fileStatus
operator|.
name|getLen
argument_list|()
argument_list|,
name|newLength
argument_list|)
expr_stmt|;
name|AppendTestUtil
operator|.
name|checkFullFile
argument_list|(
name|fSys
argument_list|,
name|file
argument_list|,
name|newLength
argument_list|,
name|data
argument_list|,
name|file
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|ContentSummary
name|cs
init|=
name|fSys
operator|.
name|getContentSummary
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Bad disk space usage"
argument_list|,
name|cs
operator|.
name|getSpaceConsumed
argument_list|()
argument_list|,
name|newLength
operator|*
name|repl
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Deleted"
argument_list|,
name|fSys
operator|.
name|delete
argument_list|(
name|dir
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Test that WebHdfsFileSystem.jsonParse() closes the connection's input
comment|// stream.
comment|// Closing the inputstream in jsonParse will allow WebHDFS to reuse
comment|// connections to the namenode rather than needing to always open new ones.
DECL|field|closedInputStream
name|boolean
name|closedInputStream
init|=
literal|false
decl_stmt|;
annotation|@
name|Test
DECL|method|testJsonParseClosesInputStream ()
specifier|public
name|void
name|testJsonParseClosesInputStream
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|WebHdfsFileSystem
name|webhdfs
init|=
operator|(
name|WebHdfsFileSystem
operator|)
name|fileSystem
decl_stmt|;
name|Path
name|file
init|=
name|getTestRootPath
argument_list|(
name|fSys
argument_list|,
literal|"test/hadoop/file"
argument_list|)
decl_stmt|;
name|createFile
argument_list|(
name|file
argument_list|)
expr_stmt|;
specifier|final
name|HttpOpParam
operator|.
name|Op
name|op
init|=
name|GetOpParam
operator|.
name|Op
operator|.
name|GETHOMEDIRECTORY
decl_stmt|;
specifier|final
name|URL
name|url
init|=
name|webhdfs
operator|.
name|toUrl
argument_list|(
name|op
argument_list|,
name|file
argument_list|)
decl_stmt|;
specifier|final
name|HttpURLConnection
name|conn
init|=
operator|(
name|HttpURLConnection
operator|)
name|url
operator|.
name|openConnection
argument_list|()
decl_stmt|;
name|conn
operator|.
name|setRequestMethod
argument_list|(
name|op
operator|.
name|getType
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|conn
operator|.
name|connect
argument_list|()
expr_stmt|;
name|InputStream
name|myIn
init|=
operator|new
name|InputStream
argument_list|()
block|{
specifier|private
name|HttpURLConnection
name|localConn
init|=
name|conn
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|closedInputStream
operator|=
literal|true
expr_stmt|;
name|localConn
operator|.
name|getInputStream
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|read
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|localConn
operator|.
name|getInputStream
argument_list|()
operator|.
name|read
argument_list|()
return|;
block|}
block|}
decl_stmt|;
specifier|final
name|HttpURLConnection
name|spyConn
init|=
name|spy
argument_list|(
name|conn
argument_list|)
decl_stmt|;
name|doReturn
argument_list|(
name|myIn
argument_list|)
operator|.
name|when
argument_list|(
name|spyConn
argument_list|)
operator|.
name|getInputStream
argument_list|()
expr_stmt|;
try|try
block|{
name|Assert
operator|.
name|assertFalse
argument_list|(
name|closedInputStream
argument_list|)
expr_stmt|;
name|WebHdfsFileSystem
operator|.
name|jsonParse
argument_list|(
name|spyConn
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|closedInputStream
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|junit
operator|.
name|framework
operator|.
name|TestCase
operator|.
name|fail
argument_list|()
expr_stmt|;
block|}
name|conn
operator|.
name|disconnect
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|Test
DECL|method|testMkdirsFailsForSubdirectoryOfExistingFile ()
specifier|public
name|void
name|testMkdirsFailsForSubdirectoryOfExistingFile
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|testDir
init|=
name|getTestRootPath
argument_list|(
name|fSys
argument_list|,
literal|"test/hadoop"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|exists
argument_list|(
name|fSys
argument_list|,
name|testDir
argument_list|)
argument_list|)
expr_stmt|;
name|fSys
operator|.
name|mkdirs
argument_list|(
name|testDir
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|exists
argument_list|(
name|fSys
argument_list|,
name|testDir
argument_list|)
argument_list|)
expr_stmt|;
name|createFile
argument_list|(
name|getTestRootPath
argument_list|(
name|fSys
argument_list|,
literal|"test/hadoop/file"
argument_list|)
argument_list|)
expr_stmt|;
name|Path
name|testSubDir
init|=
name|getTestRootPath
argument_list|(
name|fSys
argument_list|,
literal|"test/hadoop/file/subdir"
argument_list|)
decl_stmt|;
try|try
block|{
name|fSys
operator|.
name|mkdirs
argument_list|(
name|testSubDir
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Should throw IOException."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// expected
block|}
try|try
block|{
name|Assert
operator|.
name|assertFalse
argument_list|(
name|exists
argument_list|(
name|fSys
argument_list|,
name|testSubDir
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AccessControlException
name|e
parameter_list|)
block|{
comment|// also okay for HDFS.
block|}
name|Path
name|testDeepSubDir
init|=
name|getTestRootPath
argument_list|(
name|fSys
argument_list|,
literal|"test/hadoop/file/deep/sub/dir"
argument_list|)
decl_stmt|;
try|try
block|{
name|fSys
operator|.
name|mkdirs
argument_list|(
name|testDeepSubDir
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Should throw IOException."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// expected
block|}
try|try
block|{
name|Assert
operator|.
name|assertFalse
argument_list|(
name|exists
argument_list|(
name|fSys
argument_list|,
name|testDeepSubDir
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AccessControlException
name|e
parameter_list|)
block|{
comment|// also okay for HDFS.
block|}
block|}
block|}
end_class

end_unit

