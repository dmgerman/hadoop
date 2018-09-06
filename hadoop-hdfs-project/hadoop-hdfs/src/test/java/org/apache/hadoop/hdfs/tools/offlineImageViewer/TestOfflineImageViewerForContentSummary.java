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
comment|/**  * Tests GETCONTENTSUMMARY operation for WebImageViewer  */
end_comment

begin_class
DECL|class|TestOfflineImageViewerForContentSummary
specifier|public
class|class
name|TestOfflineImageViewerForContentSummary
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
name|TestOfflineImageViewerForContentSummary
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
DECL|field|summaryFromDFS
specifier|private
specifier|static
name|ContentSummary
name|summaryFromDFS
init|=
literal|null
decl_stmt|;
DECL|field|emptyDirSummaryFromDFS
specifier|private
specifier|static
name|ContentSummary
name|emptyDirSummaryFromDFS
init|=
literal|null
decl_stmt|;
DECL|field|fileSummaryFromDFS
specifier|private
specifier|static
name|ContentSummary
name|fileSummaryFromDFS
init|=
literal|null
decl_stmt|;
DECL|field|symLinkSummaryFromDFS
specifier|private
specifier|static
name|ContentSummary
name|symLinkSummaryFromDFS
init|=
literal|null
decl_stmt|;
DECL|field|symLinkSummaryForDirContainsFromDFS
specifier|private
specifier|static
name|ContentSummary
name|symLinkSummaryForDirContainsFromDFS
init|=
literal|null
decl_stmt|;
comment|/**    * Create a populated namespace for later testing. Save its contents to a    * data structure and store its fsimage location. We only want to generate    * the fsimage file once and use it for multiple tests.    */
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
name|Path
name|parentDir
init|=
operator|new
name|Path
argument_list|(
literal|"/parentDir"
argument_list|)
decl_stmt|;
name|Path
name|childDir1
init|=
operator|new
name|Path
argument_list|(
name|parentDir
argument_list|,
literal|"childDir1"
argument_list|)
decl_stmt|;
name|Path
name|childDir2
init|=
operator|new
name|Path
argument_list|(
name|parentDir
argument_list|,
literal|"childDir2"
argument_list|)
decl_stmt|;
name|Path
name|dirForLinks
init|=
operator|new
name|Path
argument_list|(
literal|"/dirForLinks"
argument_list|)
decl_stmt|;
name|hdfs
operator|.
name|mkdirs
argument_list|(
name|parentDir
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|mkdirs
argument_list|(
name|childDir1
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|mkdirs
argument_list|(
name|childDir2
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|mkdirs
argument_list|(
name|dirForLinks
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|setQuota
argument_list|(
name|parentDir
argument_list|,
literal|10
argument_list|,
literal|1024
operator|*
literal|1024
operator|*
literal|1024
argument_list|)
expr_stmt|;
name|Path
name|file1OnParentDir
init|=
operator|new
name|Path
argument_list|(
name|parentDir
argument_list|,
literal|"file1"
argument_list|)
decl_stmt|;
try|try
init|(
name|FSDataOutputStream
name|o
init|=
name|hdfs
operator|.
name|create
argument_list|(
name|file1OnParentDir
argument_list|)
init|)
block|{
name|o
operator|.
name|write
argument_list|(
literal|"123"
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
init|(
name|FSDataOutputStream
name|o
init|=
name|hdfs
operator|.
name|create
argument_list|(
operator|new
name|Path
argument_list|(
name|parentDir
argument_list|,
literal|"file2"
argument_list|)
argument_list|)
init|)
block|{
name|o
operator|.
name|write
argument_list|(
literal|"1234"
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
init|(
name|FSDataOutputStream
name|o
init|=
name|hdfs
operator|.
name|create
argument_list|(
operator|new
name|Path
argument_list|(
name|childDir1
argument_list|,
literal|"file3"
argument_list|)
argument_list|)
init|)
block|{
name|o
operator|.
name|write
argument_list|(
literal|"123"
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
init|(
name|FSDataOutputStream
name|o
init|=
name|hdfs
operator|.
name|create
argument_list|(
operator|new
name|Path
argument_list|(
name|parentDir
argument_list|,
literal|"file4"
argument_list|)
argument_list|)
init|)
block|{
name|o
operator|.
name|write
argument_list|(
literal|"123"
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Path
name|link1
init|=
operator|new
name|Path
argument_list|(
literal|"/link1"
argument_list|)
decl_stmt|;
name|Path
name|link2
init|=
operator|new
name|Path
argument_list|(
literal|"/dirForLinks/linkfordir1"
argument_list|)
decl_stmt|;
name|hdfs
operator|.
name|createSymlink
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/parentDir/file4"
argument_list|)
argument_list|,
name|link1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|summaryFromDFS
operator|=
name|hdfs
operator|.
name|getContentSummary
argument_list|(
name|parentDir
argument_list|)
expr_stmt|;
name|emptyDirSummaryFromDFS
operator|=
name|hdfs
operator|.
name|getContentSummary
argument_list|(
name|childDir2
argument_list|)
expr_stmt|;
name|fileSummaryFromDFS
operator|=
name|hdfs
operator|.
name|getContentSummary
argument_list|(
name|file1OnParentDir
argument_list|)
expr_stmt|;
name|symLinkSummaryFromDFS
operator|=
name|hdfs
operator|.
name|getContentSummary
argument_list|(
name|link1
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|createSymlink
argument_list|(
name|childDir1
argument_list|,
name|link2
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|symLinkSummaryForDirContainsFromDFS
operator|=
name|hdfs
operator|.
name|getContentSummary
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/dirForLinks"
argument_list|)
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
DECL|method|testGetContentSummaryForEmptyDirectory ()
specifier|public
name|void
name|testGetContentSummaryForEmptyDirectory
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
literal|"/webhdfs/v1/parentDir/childDir2?op=GETCONTENTSUMMARY"
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
name|webfs
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
name|ContentSummary
name|summary
init|=
name|webfs
operator|.
name|getContentSummary
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/parentDir/childDir2"
argument_list|)
argument_list|)
decl_stmt|;
name|verifyContentSummary
argument_list|(
name|emptyDirSummaryFromDFS
argument_list|,
name|summary
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testGetContentSummaryForDirectory ()
specifier|public
name|void
name|testGetContentSummaryForDirectory
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
literal|"/webhdfs/v1/parentDir/?op=GETCONTENTSUMMARY"
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
name|webfs
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
name|ContentSummary
name|summary
init|=
name|webfs
operator|.
name|getContentSummary
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/parentDir/"
argument_list|)
argument_list|)
decl_stmt|;
name|verifyContentSummary
argument_list|(
name|summaryFromDFS
argument_list|,
name|summary
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testGetContentSummaryForFile ()
specifier|public
name|void
name|testGetContentSummaryForFile
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
literal|"/webhdfs/v1/parentDir/file1?op=GETCONTENTSUMMARY"
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
name|webfs
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
name|ContentSummary
name|summary
init|=
name|webfs
operator|.
name|getContentSummary
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/parentDir/file1"
argument_list|)
argument_list|)
decl_stmt|;
name|verifyContentSummary
argument_list|(
name|fileSummaryFromDFS
argument_list|,
name|summary
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testGetContentSummaryForSymlink ()
specifier|public
name|void
name|testGetContentSummaryForSymlink
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
name|webfs
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
name|ContentSummary
name|summary
init|=
name|webfs
operator|.
name|getContentSummary
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/link1"
argument_list|)
argument_list|)
decl_stmt|;
name|verifyContentSummary
argument_list|(
name|symLinkSummaryFromDFS
argument_list|,
name|summary
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testGetContentSummaryForDirContainsSymlink ()
specifier|public
name|void
name|testGetContentSummaryForDirContainsSymlink
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
name|webfs
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
name|ContentSummary
name|summary
init|=
name|webfs
operator|.
name|getContentSummary
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/dirForLinks/"
argument_list|)
argument_list|)
decl_stmt|;
name|verifyContentSummary
argument_list|(
name|symLinkSummaryForDirContainsFromDFS
argument_list|,
name|summary
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|verifyContentSummary (ContentSummary expected, ContentSummary actual)
specifier|private
name|void
name|verifyContentSummary
parameter_list|(
name|ContentSummary
name|expected
parameter_list|,
name|ContentSummary
name|actual
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|expected
operator|.
name|getDirectoryCount
argument_list|()
argument_list|,
name|actual
operator|.
name|getDirectoryCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
operator|.
name|getFileCount
argument_list|()
argument_list|,
name|actual
operator|.
name|getFileCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
operator|.
name|getLength
argument_list|()
argument_list|,
name|actual
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
operator|.
name|getSpaceConsumed
argument_list|()
argument_list|,
name|actual
operator|.
name|getSpaceConsumed
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
operator|.
name|getQuota
argument_list|()
argument_list|,
name|actual
operator|.
name|getQuota
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
operator|.
name|getSpaceQuota
argument_list|()
argument_list|,
name|actual
operator|.
name|getSpaceQuota
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetContentSummaryResponseCode ()
specifier|public
name|void
name|testGetContentSummaryResponseCode
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
literal|"/webhdfs/v1/dir123/?op=GETCONTENTSUMMARY"
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
name|HTTP_NOT_FOUND
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

