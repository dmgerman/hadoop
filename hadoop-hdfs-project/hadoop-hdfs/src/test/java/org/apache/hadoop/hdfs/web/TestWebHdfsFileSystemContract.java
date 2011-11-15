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
name|io
operator|.
name|InputStreamReader
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
name|URL
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
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletResponse
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
name|BlockLocation
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
name|FSDataInputStream
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
name|FSDataOutputStream
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
name|FileSystemContractBaseTest
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
name|DoAsParam
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
name|hdfs
operator|.
name|web
operator|.
name|resources
operator|.
name|PutOpParam
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
name|Assert
import|;
end_import

begin_class
DECL|class|TestWebHdfsFileSystemContract
specifier|public
class|class
name|TestWebHdfsFileSystemContract
extends|extends
name|FileSystemContractBaseTest
block|{
DECL|field|conf
specifier|private
specifier|static
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
DECL|field|cluster
specifier|private
specifier|static
specifier|final
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|defaultWorkingDirectory
specifier|private
name|String
name|defaultWorkingDirectory
decl_stmt|;
DECL|field|ugi
specifier|private
name|UserGroupInformation
name|ugi
decl_stmt|;
static|static
block|{
name|conf
operator|.
name|setBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_WEBHDFS_ENABLED_KEY
argument_list|,
literal|true
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
block|}
catch|catch
parameter_list|(
name|IOException
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
name|Override
DECL|method|setUp ()
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
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
name|ugi
operator|=
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
expr_stmt|;
name|fs
operator|=
name|WebHdfsTestUtil
operator|.
name|getWebHdfsFileSystemAs
argument_list|(
name|ugi
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|defaultWorkingDirectory
operator|=
name|fs
operator|.
name|getWorkingDirectory
argument_list|()
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getDefaultWorkingDirectory ()
specifier|protected
name|String
name|getDefaultWorkingDirectory
parameter_list|()
block|{
return|return
name|defaultWorkingDirectory
return|;
block|}
comment|/** HDFS throws AccessControlException    * when calling exist(..) on a path /foo/bar/file    * but /foo/bar is indeed a file in HDFS.    */
annotation|@
name|Override
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
name|path
argument_list|(
literal|"/test/hadoop"
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|fs
operator|.
name|exists
argument_list|(
name|testDir
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|mkdirs
argument_list|(
name|testDir
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|exists
argument_list|(
name|testDir
argument_list|)
argument_list|)
expr_stmt|;
name|createFile
argument_list|(
name|path
argument_list|(
literal|"/test/hadoop/file"
argument_list|)
argument_list|)
expr_stmt|;
name|Path
name|testSubDir
init|=
name|path
argument_list|(
literal|"/test/hadoop/file/subdir"
argument_list|)
decl_stmt|;
try|try
block|{
name|fs
operator|.
name|mkdirs
argument_list|(
name|testSubDir
argument_list|)
expr_stmt|;
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
name|assertFalse
argument_list|(
name|fs
operator|.
name|exists
argument_list|(
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
name|path
argument_list|(
literal|"/test/hadoop/file/deep/sub/dir"
argument_list|)
decl_stmt|;
try|try
block|{
name|fs
operator|.
name|mkdirs
argument_list|(
name|testDeepSubDir
argument_list|)
expr_stmt|;
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
name|assertFalse
argument_list|(
name|fs
operator|.
name|exists
argument_list|(
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
comment|//the following are new tests (i.e. not over-riding the super class methods)
DECL|method|testGetFileBlockLocations ()
specifier|public
name|void
name|testGetFileBlockLocations
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|String
name|f
init|=
literal|"/test/testGetFileBlockLocations"
decl_stmt|;
name|createFile
argument_list|(
name|path
argument_list|(
name|f
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|BlockLocation
index|[]
name|computed
init|=
name|fs
operator|.
name|getFileBlockLocations
argument_list|(
operator|new
name|Path
argument_list|(
name|f
argument_list|)
argument_list|,
literal|0L
argument_list|,
literal|1L
argument_list|)
decl_stmt|;
specifier|final
name|BlockLocation
index|[]
name|expected
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
operator|.
name|getFileBlockLocations
argument_list|(
operator|new
name|Path
argument_list|(
name|f
argument_list|)
argument_list|,
literal|0L
argument_list|,
literal|1L
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
operator|.
name|length
argument_list|,
name|computed
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|computed
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|expected
index|[
name|i
index|]
operator|.
name|toString
argument_list|()
argument_list|,
name|computed
index|[
name|i
index|]
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testCaseInsensitive ()
specifier|public
name|void
name|testCaseInsensitive
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|Path
name|p
init|=
operator|new
name|Path
argument_list|(
literal|"/test/testCaseInsensitive"
argument_list|)
decl_stmt|;
specifier|final
name|WebHdfsFileSystem
name|webhdfs
init|=
operator|(
name|WebHdfsFileSystem
operator|)
name|fs
decl_stmt|;
specifier|final
name|PutOpParam
operator|.
name|Op
name|op
init|=
name|PutOpParam
operator|.
name|Op
operator|.
name|MKDIRS
decl_stmt|;
comment|//replace query with mix case letters
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
name|p
argument_list|)
decl_stmt|;
name|WebHdfsFileSystem
operator|.
name|LOG
operator|.
name|info
argument_list|(
literal|"url      = "
operator|+
name|url
argument_list|)
expr_stmt|;
specifier|final
name|URL
name|replaced
init|=
operator|new
name|URL
argument_list|(
name|url
operator|.
name|toString
argument_list|()
operator|.
name|replace
argument_list|(
name|op
operator|.
name|toQueryString
argument_list|()
argument_list|,
literal|"Op=mkDIrs"
argument_list|)
argument_list|)
decl_stmt|;
name|WebHdfsFileSystem
operator|.
name|LOG
operator|.
name|info
argument_list|(
literal|"replaced = "
operator|+
name|replaced
argument_list|)
expr_stmt|;
comment|//connect with the replaced URL.
specifier|final
name|HttpURLConnection
name|conn
init|=
operator|(
name|HttpURLConnection
operator|)
name|replaced
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
specifier|final
name|BufferedReader
name|in
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|conn
operator|.
name|getInputStream
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|line
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
name|WebHdfsFileSystem
operator|.
name|LOG
operator|.
name|info
argument_list|(
literal|"> "
operator|+
name|line
argument_list|)
expr_stmt|;
block|}
comment|//check if the command successes.
name|assertTrue
argument_list|(
name|fs
operator|.
name|getFileStatus
argument_list|(
name|p
argument_list|)
operator|.
name|isDirectory
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testOpenNonExistFile ()
specifier|public
name|void
name|testOpenNonExistFile
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|Path
name|p
init|=
operator|new
name|Path
argument_list|(
literal|"/test/testOpenNonExistFile"
argument_list|)
decl_stmt|;
comment|//open it as a file, should get FileNotFoundException
try|try
block|{
specifier|final
name|FSDataInputStream
name|in
init|=
name|fs
operator|.
name|open
argument_list|(
name|p
argument_list|)
decl_stmt|;
name|in
operator|.
name|read
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|fnfe
parameter_list|)
block|{
name|WebHdfsFileSystem
operator|.
name|LOG
operator|.
name|info
argument_list|(
literal|"This is expected."
argument_list|,
name|fnfe
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testSeek ()
specifier|public
name|void
name|testSeek
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|Path
name|p
init|=
operator|new
name|Path
argument_list|(
literal|"/test/testSeek"
argument_list|)
decl_stmt|;
name|createFile
argument_list|(
name|p
argument_list|)
expr_stmt|;
specifier|final
name|int
name|one_third
init|=
name|data
operator|.
name|length
operator|/
literal|3
decl_stmt|;
specifier|final
name|int
name|two_third
init|=
name|one_third
operator|*
literal|2
decl_stmt|;
block|{
comment|//test seek
specifier|final
name|int
name|offset
init|=
name|one_third
decl_stmt|;
specifier|final
name|int
name|len
init|=
name|data
operator|.
name|length
operator|-
name|offset
decl_stmt|;
specifier|final
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
name|len
index|]
decl_stmt|;
specifier|final
name|FSDataInputStream
name|in
init|=
name|fs
operator|.
name|open
argument_list|(
name|p
argument_list|)
decl_stmt|;
name|in
operator|.
name|seek
argument_list|(
name|offset
argument_list|)
expr_stmt|;
comment|//read all remaining data
name|in
operator|.
name|readFully
argument_list|(
name|buf
argument_list|)
expr_stmt|;
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|buf
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|"Position "
operator|+
name|i
operator|+
literal|", offset="
operator|+
name|offset
operator|+
literal|", length="
operator|+
name|len
argument_list|,
name|data
index|[
name|i
operator|+
name|offset
index|]
argument_list|,
name|buf
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|{
comment|//test position read (read the data after the two_third location)
specifier|final
name|int
name|offset
init|=
name|two_third
decl_stmt|;
specifier|final
name|int
name|len
init|=
name|data
operator|.
name|length
operator|-
name|offset
decl_stmt|;
specifier|final
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
name|len
index|]
decl_stmt|;
specifier|final
name|FSDataInputStream
name|in
init|=
name|fs
operator|.
name|open
argument_list|(
name|p
argument_list|)
decl_stmt|;
name|in
operator|.
name|readFully
argument_list|(
name|offset
argument_list|,
name|buf
argument_list|)
expr_stmt|;
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|buf
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|"Position "
operator|+
name|i
operator|+
literal|", offset="
operator|+
name|offset
operator|+
literal|", length="
operator|+
name|len
argument_list|,
name|data
index|[
name|i
operator|+
name|offset
index|]
argument_list|,
name|buf
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testRootDir ()
specifier|public
name|void
name|testRootDir
parameter_list|()
throws|throws
name|IOException
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
name|WebHdfsFileSystem
name|webhdfs
init|=
operator|(
name|WebHdfsFileSystem
operator|)
name|fs
decl_stmt|;
specifier|final
name|URL
name|url
init|=
name|webhdfs
operator|.
name|toUrl
argument_list|(
name|GetOpParam
operator|.
name|Op
operator|.
name|NULL
argument_list|,
name|root
argument_list|)
decl_stmt|;
name|WebHdfsFileSystem
operator|.
name|LOG
operator|.
name|info
argument_list|(
literal|"null url="
operator|+
name|url
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|url
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"v1"
argument_list|)
argument_list|)
expr_stmt|;
comment|//test root permission
specifier|final
name|FileStatus
name|status
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|root
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|status
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0777
argument_list|,
name|status
operator|.
name|getPermission
argument_list|()
operator|.
name|toShort
argument_list|()
argument_list|)
expr_stmt|;
comment|//delete root - disabled due to a sticky bit bug
comment|//assertFalse(fs.delete(root, true));
comment|//create file using root path
try|try
block|{
specifier|final
name|FSDataOutputStream
name|out
init|=
name|fs
operator|.
name|create
argument_list|(
name|root
argument_list|)
decl_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|WebHdfsFileSystem
operator|.
name|LOG
operator|.
name|info
argument_list|(
literal|"This is expected."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
comment|//open file using root path
try|try
block|{
specifier|final
name|FSDataInputStream
name|in
init|=
name|fs
operator|.
name|open
argument_list|(
name|root
argument_list|)
decl_stmt|;
name|in
operator|.
name|read
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|WebHdfsFileSystem
operator|.
name|LOG
operator|.
name|info
argument_list|(
literal|"This is expected."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testResponseCode ()
specifier|public
name|void
name|testResponseCode
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|WebHdfsFileSystem
name|webhdfs
init|=
operator|(
name|WebHdfsFileSystem
operator|)
name|fs
decl_stmt|;
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
name|dir
init|=
operator|new
name|Path
argument_list|(
literal|"/test/testUrl"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|webhdfs
operator|.
name|mkdirs
argument_list|(
name|dir
argument_list|)
argument_list|)
expr_stmt|;
block|{
comment|//test GETHOMEDIRECTORY
specifier|final
name|URL
name|url
init|=
name|webhdfs
operator|.
name|toUrl
argument_list|(
name|GetOpParam
operator|.
name|Op
operator|.
name|GETHOMEDIRECTORY
argument_list|,
name|root
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
specifier|final
name|Map
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|m
init|=
name|WebHdfsTestUtil
operator|.
name|connectAndGetJson
argument_list|(
name|conn
argument_list|,
name|HttpServletResponse
operator|.
name|SC_OK
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|WebHdfsFileSystem
operator|.
name|getHomeDirectoryString
argument_list|(
name|ugi
argument_list|)
argument_list|,
name|m
operator|.
name|get
argument_list|(
name|Path
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|conn
operator|.
name|disconnect
argument_list|()
expr_stmt|;
block|}
block|{
comment|//test GETHOMEDIRECTORY with unauthorized doAs
specifier|final
name|URL
name|url
init|=
name|webhdfs
operator|.
name|toUrl
argument_list|(
name|GetOpParam
operator|.
name|Op
operator|.
name|GETHOMEDIRECTORY
argument_list|,
name|root
argument_list|,
operator|new
name|DoAsParam
argument_list|(
name|ugi
operator|.
name|getShortUserName
argument_list|()
operator|+
literal|"proxy"
argument_list|)
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
name|connect
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|HttpServletResponse
operator|.
name|SC_UNAUTHORIZED
argument_list|,
name|conn
operator|.
name|getResponseCode
argument_list|()
argument_list|)
expr_stmt|;
name|conn
operator|.
name|disconnect
argument_list|()
expr_stmt|;
block|}
block|{
comment|//test set owner with empty parameters
specifier|final
name|URL
name|url
init|=
name|webhdfs
operator|.
name|toUrl
argument_list|(
name|PutOpParam
operator|.
name|Op
operator|.
name|SETOWNER
argument_list|,
name|dir
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
name|connect
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|HttpServletResponse
operator|.
name|SC_BAD_REQUEST
argument_list|,
name|conn
operator|.
name|getResponseCode
argument_list|()
argument_list|)
expr_stmt|;
name|conn
operator|.
name|disconnect
argument_list|()
expr_stmt|;
block|}
block|{
comment|//test set replication on a directory
specifier|final
name|HttpOpParam
operator|.
name|Op
name|op
init|=
name|PutOpParam
operator|.
name|Op
operator|.
name|SETREPLICATION
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
name|dir
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
name|assertEquals
argument_list|(
name|HttpServletResponse
operator|.
name|SC_OK
argument_list|,
name|conn
operator|.
name|getResponseCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|webhdfs
operator|.
name|setReplication
argument_list|(
name|dir
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|conn
operator|.
name|disconnect
argument_list|()
expr_stmt|;
block|}
block|{
comment|//test get file status for a non-exist file.
specifier|final
name|Path
name|p
init|=
operator|new
name|Path
argument_list|(
name|dir
argument_list|,
literal|"non-exist"
argument_list|)
decl_stmt|;
specifier|final
name|URL
name|url
init|=
name|webhdfs
operator|.
name|toUrl
argument_list|(
name|GetOpParam
operator|.
name|Op
operator|.
name|GETFILESTATUS
argument_list|,
name|p
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
name|connect
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|HttpServletResponse
operator|.
name|SC_NOT_FOUND
argument_list|,
name|conn
operator|.
name|getResponseCode
argument_list|()
argument_list|)
expr_stmt|;
name|conn
operator|.
name|disconnect
argument_list|()
expr_stmt|;
block|}
block|{
comment|//test set permission with empty parameters
specifier|final
name|HttpOpParam
operator|.
name|Op
name|op
init|=
name|PutOpParam
operator|.
name|Op
operator|.
name|SETPERMISSION
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
name|dir
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
name|assertEquals
argument_list|(
name|HttpServletResponse
operator|.
name|SC_OK
argument_list|,
name|conn
operator|.
name|getResponseCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|short
operator|)
literal|0755
argument_list|,
name|webhdfs
operator|.
name|getFileStatus
argument_list|(
name|dir
argument_list|)
operator|.
name|getPermission
argument_list|()
operator|.
name|toShort
argument_list|()
argument_list|)
expr_stmt|;
name|conn
operator|.
name|disconnect
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

