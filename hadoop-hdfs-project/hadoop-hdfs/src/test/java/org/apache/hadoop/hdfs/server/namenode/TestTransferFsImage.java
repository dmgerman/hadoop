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
name|FileOutputStream
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
name|net
operator|.
name|SocketTimeoutException
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
name|Collections
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
name|javax
operator|.
name|servlet
operator|.
name|ServletException
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
name|HttpServlet
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
name|HttpServletRequest
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
name|hdfs
operator|.
name|DFSUtil
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
name|server
operator|.
name|namenode
operator|.
name|NNStorage
operator|.
name|NameNodeFile
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
name|http
operator|.
name|HttpServer2
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
name|http
operator|.
name|HttpServerFunctionalTest
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
name|apache
operator|.
name|hadoop
operator|.
name|util
operator|.
name|StringUtils
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
name|mockito
operator|.
name|Mockito
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
name|ImmutableList
import|;
end_import

begin_class
DECL|class|TestTransferFsImage
specifier|public
class|class
name|TestTransferFsImage
block|{
DECL|field|TEST_DIR
specifier|private
specifier|static
specifier|final
name|File
name|TEST_DIR
init|=
name|PathUtils
operator|.
name|getTestDir
argument_list|(
name|TestTransferFsImage
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Regression test for HDFS-1997. Test that, if an exception    * occurs on the client side, it is properly reported as such,    * and reported to the associated NNStorage object.    */
annotation|@
name|Test
DECL|method|testClientSideException ()
specifier|public
name|void
name|testClientSideException
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
literal|0
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|NNStorage
name|mockStorage
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|NNStorage
operator|.
name|class
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|File
argument_list|>
name|localPath
init|=
name|Collections
operator|.
expr|<
name|File
operator|>
name|singletonList
argument_list|(
operator|new
name|File
argument_list|(
literal|"/xxxxx-does-not-exist/blah"
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|URL
name|fsName
init|=
name|DFSUtil
operator|.
name|getInfoServer
argument_list|(
name|cluster
operator|.
name|getNameNode
argument_list|()
operator|.
name|getServiceRpcAddress
argument_list|()
argument_list|,
name|conf
argument_list|,
name|DFSUtil
operator|.
name|getHttpClientScheme
argument_list|(
name|conf
argument_list|)
argument_list|)
operator|.
name|toURL
argument_list|()
decl_stmt|;
name|String
name|id
init|=
literal|"getimage=1&txid=0"
decl_stmt|;
name|TransferFsImage
operator|.
name|getFileClient
argument_list|(
name|fsName
argument_list|,
name|id
argument_list|,
name|localPath
argument_list|,
name|mockStorage
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Didn't get an exception!"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|Mockito
operator|.
name|verify
argument_list|(
name|mockStorage
argument_list|)
operator|.
name|reportErrorOnFile
argument_list|(
name|localPath
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Unexpected exception: "
operator|+
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|ioe
argument_list|)
argument_list|,
name|ioe
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Unable to download to any storage"
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Similar to the above test, except that there are multiple local files    * and one of them can be saved.    */
annotation|@
name|Test
DECL|method|testClientSideExceptionOnJustOneDir ()
specifier|public
name|void
name|testClientSideExceptionOnJustOneDir
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
literal|0
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|NNStorage
name|mockStorage
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|NNStorage
operator|.
name|class
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|File
argument_list|>
name|localPaths
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
operator|new
name|File
argument_list|(
literal|"/xxxxx-does-not-exist/blah"
argument_list|)
argument_list|,
operator|new
name|File
argument_list|(
name|TEST_DIR
argument_list|,
literal|"testfile"
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|URL
name|fsName
init|=
name|DFSUtil
operator|.
name|getInfoServer
argument_list|(
name|cluster
operator|.
name|getNameNode
argument_list|()
operator|.
name|getServiceRpcAddress
argument_list|()
argument_list|,
name|conf
argument_list|,
name|DFSUtil
operator|.
name|getHttpClientScheme
argument_list|(
name|conf
argument_list|)
argument_list|)
operator|.
name|toURL
argument_list|()
decl_stmt|;
name|String
name|id
init|=
literal|"getimage=1&txid=0"
decl_stmt|;
name|TransferFsImage
operator|.
name|getFileClient
argument_list|(
name|fsName
argument_list|,
name|id
argument_list|,
name|localPaths
argument_list|,
name|mockStorage
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|mockStorage
argument_list|)
operator|.
name|reportErrorOnFile
argument_list|(
name|localPaths
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"The valid local file should get saved properly"
argument_list|,
name|localPaths
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|length
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Test to verify the read timeout    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|5000
argument_list|)
DECL|method|testGetImageTimeout ()
specifier|public
name|void
name|testGetImageTimeout
parameter_list|()
throws|throws
name|Exception
block|{
name|HttpServer2
name|testServer
init|=
name|HttpServerFunctionalTest
operator|.
name|createServer
argument_list|(
literal|"hdfs"
argument_list|)
decl_stmt|;
try|try
block|{
name|testServer
operator|.
name|addServlet
argument_list|(
literal|"ImageTransfer"
argument_list|,
name|ImageServlet
operator|.
name|PATH_SPEC
argument_list|,
name|TestImageTransferServlet
operator|.
name|class
argument_list|)
expr_stmt|;
name|testServer
operator|.
name|start
argument_list|()
expr_stmt|;
name|URL
name|serverURL
init|=
name|HttpServerFunctionalTest
operator|.
name|getServerURL
argument_list|(
name|testServer
argument_list|)
decl_stmt|;
name|TransferFsImage
operator|.
name|timeout
operator|=
literal|2000
expr_stmt|;
try|try
block|{
name|TransferFsImage
operator|.
name|getFileClient
argument_list|(
name|serverURL
argument_list|,
literal|"txid=1"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"TransferImage Should fail with timeout"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SocketTimeoutException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"Read should timeout"
argument_list|,
literal|"Read timed out"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|testServer
operator|!=
literal|null
condition|)
block|{
name|testServer
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Test to verify the timeout of Image upload    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
DECL|method|testImageUploadTimeout ()
specifier|public
name|void
name|testImageUploadTimeout
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
name|NNStorage
name|mockStorage
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|NNStorage
operator|.
name|class
argument_list|)
decl_stmt|;
name|HttpServer2
name|testServer
init|=
name|HttpServerFunctionalTest
operator|.
name|createServer
argument_list|(
literal|"hdfs"
argument_list|)
decl_stmt|;
try|try
block|{
name|testServer
operator|.
name|addServlet
argument_list|(
literal|"ImageTransfer"
argument_list|,
name|ImageServlet
operator|.
name|PATH_SPEC
argument_list|,
name|TestImageTransferServlet
operator|.
name|class
argument_list|)
expr_stmt|;
name|testServer
operator|.
name|start
argument_list|()
expr_stmt|;
name|URL
name|serverURL
init|=
name|HttpServerFunctionalTest
operator|.
name|getServerURL
argument_list|(
name|testServer
argument_list|)
decl_stmt|;
comment|// set the timeout here, otherwise it will take default.
name|TransferFsImage
operator|.
name|timeout
operator|=
literal|2000
expr_stmt|;
name|File
name|tmpDir
init|=
operator|new
name|File
argument_list|(
operator|new
name|FileSystemTestHelper
argument_list|()
operator|.
name|getTestRootDir
argument_list|()
argument_list|)
decl_stmt|;
name|tmpDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|File
name|mockImageFile
init|=
name|File
operator|.
name|createTempFile
argument_list|(
literal|"image"
argument_list|,
literal|""
argument_list|,
name|tmpDir
argument_list|)
decl_stmt|;
name|FileOutputStream
name|imageFile
init|=
operator|new
name|FileOutputStream
argument_list|(
name|mockImageFile
argument_list|)
decl_stmt|;
name|imageFile
operator|.
name|write
argument_list|(
literal|"data"
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|imageFile
operator|.
name|close
argument_list|()
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|mockStorage
operator|.
name|findImageFile
argument_list|(
name|Mockito
operator|.
name|any
argument_list|(
name|NameNodeFile
operator|.
name|class
argument_list|)
argument_list|,
name|Mockito
operator|.
name|anyLong
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|mockImageFile
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|mockStorage
operator|.
name|toColonSeparatedString
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"storage:info:string"
argument_list|)
expr_stmt|;
try|try
block|{
name|TransferFsImage
operator|.
name|uploadImageFromStorage
argument_list|(
name|serverURL
argument_list|,
name|conf
argument_list|,
name|mockStorage
argument_list|,
name|NameNodeFile
operator|.
name|IMAGE
argument_list|,
literal|1L
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"TransferImage Should fail with timeout"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SocketTimeoutException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"Upload should timeout"
argument_list|,
literal|"Read timed out"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|testServer
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|TestImageTransferServlet
specifier|public
specifier|static
class|class
name|TestImageTransferServlet
extends|extends
name|HttpServlet
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
annotation|@
name|Override
DECL|method|doGet (HttpServletRequest req, HttpServletResponse resp)
specifier|protected
name|void
name|doGet
parameter_list|(
name|HttpServletRequest
name|req
parameter_list|,
name|HttpServletResponse
name|resp
parameter_list|)
throws|throws
name|ServletException
throws|,
name|IOException
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
try|try
block|{
name|wait
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
comment|// Ignore
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|doPut (HttpServletRequest req, HttpServletResponse resp)
specifier|protected
name|void
name|doPut
parameter_list|(
name|HttpServletRequest
name|req
parameter_list|,
name|HttpServletResponse
name|resp
parameter_list|)
throws|throws
name|ServletException
throws|,
name|IOException
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
try|try
block|{
name|wait
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
comment|// Ignore
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

