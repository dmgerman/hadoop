begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
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
name|assertNotNull
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
name|io
operator|.
name|OutputStream
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
name|URISyntaxException
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
name|test
operator|.
name|PathUtils
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
comment|/**  * Test of the URL stream handler.  */
end_comment

begin_class
DECL|class|TestUrlStreamHandler
specifier|public
class|class
name|TestUrlStreamHandler
block|{
DECL|field|TEST_ROOT_DIR
specifier|private
specifier|static
specifier|final
name|File
name|TEST_ROOT_DIR
init|=
name|PathUtils
operator|.
name|getTestDir
argument_list|(
name|TestUrlStreamHandler
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Test opening and reading from an InputStream through a hdfs:// URL.    *<p>    * First generate a file with some content through the FileSystem API, then    * try to open and read the file through the URL stream API.    *     * @throws IOException    */
annotation|@
name|Test
DECL|method|testDfsUrls ()
specifier|public
name|void
name|testDfsUrls
parameter_list|()
throws|throws
name|IOException
block|{
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|MiniDFSCluster
name|cluster
init|=
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
decl_stmt|;
name|FileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
comment|// Setup our own factory
comment|// setURLSteramHandlerFactor is can be set at most once in the JVM
comment|// the new URLStreamHandler is valid for all tests cases
comment|// in TestStreamHandler
name|FsUrlStreamHandlerFactory
name|factory
init|=
operator|new
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|FsUrlStreamHandlerFactory
argument_list|()
decl_stmt|;
name|java
operator|.
name|net
operator|.
name|URL
operator|.
name|setURLStreamHandlerFactory
argument_list|(
name|factory
argument_list|)
expr_stmt|;
name|Path
name|filePath
init|=
operator|new
name|Path
argument_list|(
literal|"/thefile"
argument_list|)
decl_stmt|;
try|try
block|{
name|byte
index|[]
name|fileContent
init|=
operator|new
name|byte
index|[
literal|1024
index|]
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
name|fileContent
operator|.
name|length
condition|;
operator|++
name|i
control|)
name|fileContent
index|[
name|i
index|]
operator|=
operator|(
name|byte
operator|)
name|i
expr_stmt|;
comment|// First create the file through the FileSystem API
name|OutputStream
name|os
init|=
name|fs
operator|.
name|create
argument_list|(
name|filePath
argument_list|)
decl_stmt|;
name|os
operator|.
name|write
argument_list|(
name|fileContent
argument_list|)
expr_stmt|;
name|os
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Second, open and read the file content through the URL API
name|URI
name|uri
init|=
name|fs
operator|.
name|getUri
argument_list|()
decl_stmt|;
name|URL
name|fileURL
init|=
operator|new
name|URL
argument_list|(
name|uri
operator|.
name|getScheme
argument_list|()
argument_list|,
name|uri
operator|.
name|getHost
argument_list|()
argument_list|,
name|uri
operator|.
name|getPort
argument_list|()
argument_list|,
name|filePath
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|InputStream
name|is
init|=
name|fileURL
operator|.
name|openStream
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|is
argument_list|)
expr_stmt|;
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
literal|4096
index|]
decl_stmt|;
name|assertEquals
argument_list|(
literal|1024
argument_list|,
name|is
operator|.
name|read
argument_list|(
name|bytes
argument_list|)
argument_list|)
expr_stmt|;
name|is
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
name|fileContent
operator|.
name|length
condition|;
operator|++
name|i
control|)
name|assertEquals
argument_list|(
name|fileContent
index|[
name|i
index|]
argument_list|,
name|bytes
index|[
name|i
index|]
argument_list|)
expr_stmt|;
comment|// Cleanup: delete the file
name|fs
operator|.
name|delete
argument_list|(
name|filePath
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Test opening and reading from an InputStream through a file:// URL.    *     * @throws IOException    * @throws URISyntaxException    */
annotation|@
name|Test
DECL|method|testFileUrls ()
specifier|public
name|void
name|testFileUrls
parameter_list|()
throws|throws
name|IOException
throws|,
name|URISyntaxException
block|{
comment|// URLStreamHandler is already set in JVM by testDfsUrls()
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
comment|// Locate the test temporary directory.
if|if
condition|(
operator|!
name|TEST_ROOT_DIR
operator|.
name|exists
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|TEST_ROOT_DIR
operator|.
name|mkdirs
argument_list|()
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Cannot create temporary directory: "
operator|+
name|TEST_ROOT_DIR
argument_list|)
throw|;
block|}
name|File
name|tmpFile
init|=
operator|new
name|File
argument_list|(
name|TEST_ROOT_DIR
argument_list|,
literal|"thefile"
argument_list|)
decl_stmt|;
name|URI
name|uri
init|=
name|tmpFile
operator|.
name|toURI
argument_list|()
decl_stmt|;
name|FileSystem
name|fs
init|=
name|FileSystem
operator|.
name|get
argument_list|(
name|uri
argument_list|,
name|conf
argument_list|)
decl_stmt|;
try|try
block|{
name|byte
index|[]
name|fileContent
init|=
operator|new
name|byte
index|[
literal|1024
index|]
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
name|fileContent
operator|.
name|length
condition|;
operator|++
name|i
control|)
name|fileContent
index|[
name|i
index|]
operator|=
operator|(
name|byte
operator|)
name|i
expr_stmt|;
comment|// First create the file through the FileSystem API
name|OutputStream
name|os
init|=
name|fs
operator|.
name|create
argument_list|(
operator|new
name|Path
argument_list|(
name|uri
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|os
operator|.
name|write
argument_list|(
name|fileContent
argument_list|)
expr_stmt|;
name|os
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Second, open and read the file content through the URL API.
name|URL
name|fileURL
init|=
name|uri
operator|.
name|toURL
argument_list|()
decl_stmt|;
name|InputStream
name|is
init|=
name|fileURL
operator|.
name|openStream
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|is
argument_list|)
expr_stmt|;
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
literal|4096
index|]
decl_stmt|;
name|assertEquals
argument_list|(
literal|1024
argument_list|,
name|is
operator|.
name|read
argument_list|(
name|bytes
argument_list|)
argument_list|)
expr_stmt|;
name|is
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
name|fileContent
operator|.
name|length
condition|;
operator|++
name|i
control|)
name|assertEquals
argument_list|(
name|fileContent
index|[
name|i
index|]
argument_list|,
name|bytes
index|[
name|i
index|]
argument_list|)
expr_stmt|;
comment|// Cleanup: delete the file
name|fs
operator|.
name|delete
argument_list|(
operator|new
name|Path
argument_list|(
name|uri
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

