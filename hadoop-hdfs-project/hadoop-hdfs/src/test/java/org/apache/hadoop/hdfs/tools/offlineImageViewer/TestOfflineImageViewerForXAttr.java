begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.tools.offlineImageViewer
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|tools
operator|.
name|offlineImageViewer
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
name|util
operator|.
name|ArrayList
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
name|commons
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
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|XAttrHelper
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
name|namenode
operator|.
name|FSImageTestUtil
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
name|JsonUtil
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
name|WebHdfsFileSystem
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
name|net
operator|.
name|NetUtils
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

begin_comment
comment|/**  * Tests OfflineImageViewer if the input fsimage has XAttributes  */
end_comment

begin_class
DECL|class|TestOfflineImageViewerForXAttr
specifier|public
class|class
name|TestOfflineImageViewerForXAttr
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestOfflineImageViewerForXAttr
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|originalFsimage
specifier|private
specifier|static
name|File
name|originalFsimage
init|=
literal|null
decl_stmt|;
DECL|field|attr1JSon
specifier|static
name|String
name|attr1JSon
decl_stmt|;
comment|/**    * Create a populated namespace for later testing. Save its contents to a data    * structure and store its fsimage location. We only want to generate the    * fsimage file once and use it for multiple tests.    */
annotation|@
name|BeforeClass
DECL|method|createOriginalFSImage ()
specifier|public
specifier|static
name|void
name|createOriginalFSImage
parameter_list|()
throws|throws
name|IOException
block|{
name|MiniDFSCluster
name|cluster
init|=
literal|null
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
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
name|build
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|DistributedFileSystem
name|hdfs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
comment|// Create a name space with XAttributes
name|Path
name|dir
init|=
operator|new
name|Path
argument_list|(
literal|"/dir1"
argument_list|)
decl_stmt|;
name|hdfs
operator|.
name|mkdirs
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|setXAttr
argument_list|(
name|dir
argument_list|,
literal|"user.attr1"
argument_list|,
literal|"value1"
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|setXAttr
argument_list|(
name|dir
argument_list|,
literal|"user.attr2"
argument_list|,
literal|"value2"
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
comment|// Write results to the fsimage file
name|hdfs
operator|.
name|setSafeMode
argument_list|(
name|HdfsConstants
operator|.
name|SafeModeAction
operator|.
name|SAFEMODE_ENTER
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|saveNamespace
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|XAttr
argument_list|>
name|attributes
init|=
operator|new
name|ArrayList
argument_list|<
name|XAttr
argument_list|>
argument_list|()
decl_stmt|;
name|attributes
operator|.
name|add
argument_list|(
name|XAttrHelper
operator|.
name|buildXAttr
argument_list|(
literal|"user.attr1"
argument_list|,
literal|"value1"
operator|.
name|getBytes
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|attr1JSon
operator|=
name|JsonUtil
operator|.
name|toJsonString
argument_list|(
name|attributes
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|attributes
operator|.
name|add
argument_list|(
name|XAttrHelper
operator|.
name|buildXAttr
argument_list|(
literal|"user.attr2"
argument_list|,
literal|"value2"
operator|.
name|getBytes
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// Determine the location of the fsimage file
name|originalFsimage
operator|=
name|FSImageTestUtil
operator|.
name|findLatestImageFile
argument_list|(
name|FSImageTestUtil
operator|.
name|getFSImage
argument_list|(
name|cluster
operator|.
name|getNameNode
argument_list|()
argument_list|)
operator|.
name|getStorage
argument_list|()
operator|.
name|getStorageDir
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|originalFsimage
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Didn't generate or can't find fsimage"
argument_list|)
throw|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"original FS image file is "
operator|+
name|originalFsimage
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|cluster
operator|!=
literal|null
condition|)
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|AfterClass
DECL|method|deleteOriginalFSImage ()
specifier|public
specifier|static
name|void
name|deleteOriginalFSImage
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|originalFsimage
operator|!=
literal|null
operator|&&
name|originalFsimage
operator|.
name|exists
argument_list|()
condition|)
block|{
name|originalFsimage
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testWebImageViewerForListXAttrs ()
specifier|public
name|void
name|testWebImageViewerForListXAttrs
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|WebImageViewer
name|viewer
init|=
operator|new
name|WebImageViewer
argument_list|(
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
literal|"localhost:0"
argument_list|)
argument_list|)
init|)
block|{
name|viewer
operator|.
name|initServer
argument_list|(
name|originalFsimage
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|port
init|=
name|viewer
operator|.
name|getPort
argument_list|()
decl_stmt|;
name|URL
name|url
init|=
operator|new
name|URL
argument_list|(
literal|"http://localhost:"
operator|+
name|port
operator|+
literal|"/webhdfs/v1/dir1/?op=LISTXATTRS"
argument_list|)
decl_stmt|;
name|HttpURLConnection
name|connection
init|=
operator|(
name|HttpURLConnection
operator|)
name|url
operator|.
name|openConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|setRequestMethod
argument_list|(
literal|"GET"
argument_list|)
expr_stmt|;
name|connection
operator|.
name|connect
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|HttpURLConnection
operator|.
name|HTTP_OK
argument_list|,
name|connection
operator|.
name|getResponseCode
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|content
init|=
name|IOUtils
operator|.
name|toString
argument_list|(
name|connection
operator|.
name|getInputStream
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Missing user.attr1 in response "
argument_list|,
name|content
operator|.
name|contains
argument_list|(
literal|"user.attr1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Missing user.attr2 in response "
argument_list|,
name|content
operator|.
name|contains
argument_list|(
literal|"user.attr2"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testWebImageViewerForGetXAttrsWithOutParameters ()
specifier|public
name|void
name|testWebImageViewerForGetXAttrsWithOutParameters
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|WebImageViewer
name|viewer
init|=
operator|new
name|WebImageViewer
argument_list|(
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
literal|"localhost:0"
argument_list|)
argument_list|)
init|)
block|{
name|viewer
operator|.
name|initServer
argument_list|(
name|originalFsimage
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|port
init|=
name|viewer
operator|.
name|getPort
argument_list|()
decl_stmt|;
name|URL
name|url
init|=
operator|new
name|URL
argument_list|(
literal|"http://localhost:"
operator|+
name|port
operator|+
literal|"/webhdfs/v1/dir1/?op=GETXATTRS"
argument_list|)
decl_stmt|;
name|HttpURLConnection
name|connection
init|=
operator|(
name|HttpURLConnection
operator|)
name|url
operator|.
name|openConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|setRequestMethod
argument_list|(
literal|"GET"
argument_list|)
expr_stmt|;
name|connection
operator|.
name|connect
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|HttpURLConnection
operator|.
name|HTTP_OK
argument_list|,
name|connection
operator|.
name|getResponseCode
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|content
init|=
name|IOUtils
operator|.
name|toString
argument_list|(
name|connection
operator|.
name|getInputStream
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Missing user.attr1 in response "
argument_list|,
name|content
operator|.
name|contains
argument_list|(
literal|"user.attr1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Missing user.attr2 in response "
argument_list|,
name|content
operator|.
name|contains
argument_list|(
literal|"user.attr2"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testWebImageViewerForGetXAttrsWithParameters ()
specifier|public
name|void
name|testWebImageViewerForGetXAttrsWithParameters
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|WebImageViewer
name|viewer
init|=
operator|new
name|WebImageViewer
argument_list|(
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
literal|"localhost:0"
argument_list|)
argument_list|)
init|)
block|{
name|viewer
operator|.
name|initServer
argument_list|(
name|originalFsimage
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|port
init|=
name|viewer
operator|.
name|getPort
argument_list|()
decl_stmt|;
name|URL
name|url
init|=
operator|new
name|URL
argument_list|(
literal|"http://localhost:"
operator|+
name|port
operator|+
literal|"/webhdfs/v1/dir1/?op=GETXATTRS&xattr.name=attr8"
argument_list|)
decl_stmt|;
name|HttpURLConnection
name|connection
init|=
operator|(
name|HttpURLConnection
operator|)
name|url
operator|.
name|openConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|setRequestMethod
argument_list|(
literal|"GET"
argument_list|)
expr_stmt|;
name|connection
operator|.
name|connect
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|HttpURLConnection
operator|.
name|HTTP_BAD_REQUEST
argument_list|,
name|connection
operator|.
name|getResponseCode
argument_list|()
argument_list|)
expr_stmt|;
name|url
operator|=
operator|new
name|URL
argument_list|(
literal|"http://localhost:"
operator|+
name|port
operator|+
literal|"/webhdfs/v1/dir1/?op=GETXATTRS&xattr.name=user.attr1"
argument_list|)
expr_stmt|;
name|connection
operator|=
operator|(
name|HttpURLConnection
operator|)
name|url
operator|.
name|openConnection
argument_list|()
expr_stmt|;
name|connection
operator|.
name|setRequestMethod
argument_list|(
literal|"GET"
argument_list|)
expr_stmt|;
name|connection
operator|.
name|connect
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|HttpURLConnection
operator|.
name|HTTP_OK
argument_list|,
name|connection
operator|.
name|getResponseCode
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|content
init|=
name|IOUtils
operator|.
name|toString
argument_list|(
name|connection
operator|.
name|getInputStream
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|attr1JSon
argument_list|,
name|content
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testWebImageViewerForGetXAttrsWithCodecParameters ()
specifier|public
name|void
name|testWebImageViewerForGetXAttrsWithCodecParameters
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|WebImageViewer
name|viewer
init|=
operator|new
name|WebImageViewer
argument_list|(
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
literal|"localhost:0"
argument_list|)
argument_list|)
init|)
block|{
name|viewer
operator|.
name|initServer
argument_list|(
name|originalFsimage
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|port
init|=
name|viewer
operator|.
name|getPort
argument_list|()
decl_stmt|;
name|URL
name|url
init|=
operator|new
name|URL
argument_list|(
literal|"http://localhost:"
operator|+
name|port
operator|+
literal|"/webhdfs/v1/dir1/?op=GETXATTRS&xattr.name=USER.attr1&encoding=TEXT"
argument_list|)
decl_stmt|;
name|HttpURLConnection
name|connection
init|=
operator|(
name|HttpURLConnection
operator|)
name|url
operator|.
name|openConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|setRequestMethod
argument_list|(
literal|"GET"
argument_list|)
expr_stmt|;
name|connection
operator|.
name|connect
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|HttpURLConnection
operator|.
name|HTTP_OK
argument_list|,
name|connection
operator|.
name|getResponseCode
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|content
init|=
name|IOUtils
operator|.
name|toString
argument_list|(
name|connection
operator|.
name|getInputStream
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|attr1JSon
argument_list|,
name|content
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testWithWebHdfsFileSystem ()
specifier|public
name|void
name|testWithWebHdfsFileSystem
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|WebImageViewer
name|viewer
init|=
operator|new
name|WebImageViewer
argument_list|(
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
literal|"localhost:0"
argument_list|)
argument_list|)
init|)
block|{
name|viewer
operator|.
name|initServer
argument_list|(
name|originalFsimage
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|port
init|=
name|viewer
operator|.
name|getPort
argument_list|()
decl_stmt|;
comment|// create a WebHdfsFileSystem instance
name|URI
name|uri
init|=
operator|new
name|URI
argument_list|(
literal|"webhdfs://localhost:"
operator|+
name|String
operator|.
name|valueOf
argument_list|(
name|port
argument_list|)
argument_list|)
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|WebHdfsFileSystem
name|webhdfs
init|=
operator|(
name|WebHdfsFileSystem
operator|)
name|FileSystem
operator|.
name|get
argument_list|(
name|uri
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|names
init|=
name|webhdfs
operator|.
name|listXAttrs
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/dir1"
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|names
operator|.
name|contains
argument_list|(
literal|"user.attr1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|names
operator|.
name|contains
argument_list|(
literal|"user.attr2"
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|value
init|=
operator|new
name|String
argument_list|(
name|webhdfs
operator|.
name|getXAttr
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/dir1"
argument_list|)
argument_list|,
literal|"user.attr1"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"value1"
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|value
operator|=
operator|new
name|String
argument_list|(
name|webhdfs
operator|.
name|getXAttr
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/dir1"
argument_list|)
argument_list|,
literal|"USER.attr1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"value1"
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|byte
index|[]
argument_list|>
name|contentMap
init|=
name|webhdfs
operator|.
name|getXAttrs
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/dir1"
argument_list|)
argument_list|,
name|names
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"value1"
argument_list|,
operator|new
name|String
argument_list|(
name|contentMap
operator|.
name|get
argument_list|(
literal|"user.attr1"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"value2"
argument_list|,
operator|new
name|String
argument_list|(
name|contentMap
operator|.
name|get
argument_list|(
literal|"user.attr2"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testResponseCode ()
specifier|public
name|void
name|testResponseCode
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|WebImageViewer
name|viewer
init|=
operator|new
name|WebImageViewer
argument_list|(
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
literal|"localhost:0"
argument_list|)
argument_list|)
init|)
block|{
name|viewer
operator|.
name|initServer
argument_list|(
name|originalFsimage
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|port
init|=
name|viewer
operator|.
name|getPort
argument_list|()
decl_stmt|;
name|URL
name|url
init|=
operator|new
name|URL
argument_list|(
literal|"http://localhost:"
operator|+
name|port
operator|+
literal|"/webhdfs/v1/dir1/?op=GETXATTRS&xattr.name=user.notpresent&encoding=TEXT"
argument_list|)
decl_stmt|;
name|HttpURLConnection
name|connection
init|=
operator|(
name|HttpURLConnection
operator|)
name|url
operator|.
name|openConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|setRequestMethod
argument_list|(
literal|"GET"
argument_list|)
expr_stmt|;
name|connection
operator|.
name|connect
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|HttpURLConnection
operator|.
name|HTTP_FORBIDDEN
argument_list|,
name|connection
operator|.
name|getResponseCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

