begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode
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
name|datanode
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
name|assertTrue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutputStream
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
name|InetSocketAddress
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
name|net
operator|.
name|URLEncoder
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
name|jsp
operator|.
name|JspWriter
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
name|common
operator|.
name|JspHelper
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
name|NameNode
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
name|ServletUtil
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

begin_class
DECL|class|TestDatanodeJsp
specifier|public
class|class
name|TestDatanodeJsp
block|{
DECL|field|FILE_DATA
specifier|private
specifier|static
specifier|final
name|String
name|FILE_DATA
init|=
literal|"foo bar baz biz buz"
decl_stmt|;
DECL|field|CONF
specifier|private
specifier|static
specifier|final
name|HdfsConfiguration
name|CONF
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
DECL|method|testViewingFile (MiniDFSCluster cluster, String filePath, boolean doTail)
specifier|private
specifier|static
name|void
name|testViewingFile
parameter_list|(
name|MiniDFSCluster
name|cluster
parameter_list|,
name|String
name|filePath
parameter_list|,
name|boolean
name|doTail
parameter_list|)
throws|throws
name|IOException
block|{
name|FileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|Path
name|testPath
init|=
operator|new
name|Path
argument_list|(
name|filePath
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|fs
operator|.
name|exists
argument_list|(
name|testPath
argument_list|)
condition|)
block|{
name|DFSTestUtil
operator|.
name|writeFile
argument_list|(
name|fs
argument_list|,
name|testPath
argument_list|,
name|FILE_DATA
argument_list|)
expr_stmt|;
block|}
name|InetSocketAddress
name|nnIpcAddress
init|=
name|cluster
operator|.
name|getNameNode
argument_list|()
operator|.
name|getNameNodeAddress
argument_list|()
decl_stmt|;
name|InetSocketAddress
name|nnHttpAddress
init|=
name|cluster
operator|.
name|getNameNode
argument_list|()
operator|.
name|getHttpAddress
argument_list|()
decl_stmt|;
name|int
name|dnInfoPort
init|=
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getInfoPort
argument_list|()
decl_stmt|;
name|String
name|jspName
init|=
name|doTail
condition|?
literal|"tail.jsp"
else|:
literal|"browseDirectory.jsp"
decl_stmt|;
name|String
name|fileParamName
init|=
name|doTail
condition|?
literal|"filename"
else|:
literal|"dir"
decl_stmt|;
name|URL
name|url
init|=
operator|new
name|URL
argument_list|(
literal|"http://localhost:"
operator|+
name|dnInfoPort
operator|+
literal|"/"
operator|+
name|jspName
operator|+
name|JspHelper
operator|.
name|getUrlParam
argument_list|(
name|fileParamName
argument_list|,
name|URLEncoder
operator|.
name|encode
argument_list|(
name|testPath
operator|.
name|toString
argument_list|()
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|,
literal|true
argument_list|)
operator|+
name|JspHelper
operator|.
name|getUrlParam
argument_list|(
literal|"namenodeInfoPort"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|nnHttpAddress
operator|.
name|getPort
argument_list|()
argument_list|)
argument_list|)
operator|+
name|JspHelper
operator|.
name|getUrlParam
argument_list|(
literal|"nnaddr"
argument_list|,
literal|"localhost:"
operator|+
name|nnIpcAddress
operator|.
name|getPort
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|viewFilePage
init|=
name|DFSTestUtil
operator|.
name|urlGet
argument_list|(
name|url
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"page should show preview of file contents"
argument_list|,
name|viewFilePage
operator|.
name|contains
argument_list|(
name|FILE_DATA
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|doTail
condition|)
block|{
name|assertTrue
argument_list|(
literal|"page should show link to download file"
argument_list|,
name|viewFilePage
operator|.
name|contains
argument_list|(
literal|"/streamFile"
operator|+
name|ServletUtil
operator|.
name|encodePath
argument_list|(
name|testPath
operator|.
name|toString
argument_list|()
argument_list|)
operator|+
literal|"?nnaddr=localhost:"
operator|+
name|nnIpcAddress
operator|.
name|getPort
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testViewFileJsp ()
specifier|public
name|void
name|testViewFileJsp
parameter_list|()
throws|throws
name|IOException
block|{
name|MiniDFSCluster
name|cluster
init|=
literal|null
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
name|CONF
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
name|String
name|paths
index|[]
init|=
block|{
literal|"/test-file"
block|,
literal|"/tmp/test-file"
block|,
literal|"/tmp/test-file%with goofy&characters"
block|,
literal|"/foo bar/foo bar"
block|,
literal|"/foo+bar/foo+bar"
block|,
literal|"/foo;bar/foo;bar"
block|,
literal|"/foo=bar/foo=bar"
block|,
literal|"/foo,bar/foo,bar"
block|,
literal|"/foo?bar/foo?bar"
block|,
literal|"/foo\">bar/foo\">bar"
block|}
decl_stmt|;
for|for
control|(
name|String
name|p
range|:
name|paths
control|)
block|{
name|testViewingFile
argument_list|(
name|cluster
argument_list|,
name|p
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|testViewingFile
argument_list|(
name|cluster
argument_list|,
name|p
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
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
block|}
annotation|@
name|Test
DECL|method|testGenStamp ()
specifier|public
name|void
name|testGenStamp
parameter_list|()
throws|throws
name|Exception
block|{
name|MiniDFSCluster
name|cluster
init|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|CONF
argument_list|)
operator|.
name|numDataNodes
argument_list|(
literal|1
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
name|FileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|Path
name|testFile
init|=
operator|new
name|Path
argument_list|(
literal|"/test/mkdirs/TestchunkSizeToView"
argument_list|)
decl_stmt|;
name|writeFile
argument_list|(
name|fs
argument_list|,
name|testFile
argument_list|)
expr_stmt|;
name|JspWriter
name|writerMock
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|JspWriter
operator|.
name|class
argument_list|)
decl_stmt|;
name|HttpServletRequest
name|reqMock
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|HttpServletRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|setTheMockExpectationsFromReq
argument_list|(
name|testFile
argument_list|,
name|reqMock
argument_list|)
expr_stmt|;
name|DatanodeJspHelper
operator|.
name|generateFileDetails
argument_list|(
name|writerMock
argument_list|,
name|reqMock
argument_list|,
name|CONF
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|writerMock
argument_list|,
name|Mockito
operator|.
name|atLeastOnce
argument_list|()
argument_list|)
operator|.
name|print
argument_list|(
literal|"<input type=\"hidden\" name=\"genstamp\" value=\"987654321\">"
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
DECL|method|setTheMockExpectationsFromReq (Path testFile, HttpServletRequest reqMock)
specifier|private
name|void
name|setTheMockExpectationsFromReq
parameter_list|(
name|Path
name|testFile
parameter_list|,
name|HttpServletRequest
name|reqMock
parameter_list|)
block|{
name|Mockito
operator|.
name|doReturn
argument_list|(
literal|"987654321"
argument_list|)
operator|.
name|when
argument_list|(
name|reqMock
argument_list|)
operator|.
name|getParameter
argument_list|(
literal|"genstamp"
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|doReturn
argument_list|(
literal|"1234"
argument_list|)
operator|.
name|when
argument_list|(
name|reqMock
argument_list|)
operator|.
name|getParameter
argument_list|(
literal|"blockId"
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|doReturn
argument_list|(
literal|"8081"
argument_list|)
operator|.
name|when
argument_list|(
name|reqMock
argument_list|)
operator|.
name|getParameter
argument_list|(
literal|"datanodePort"
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|doReturn
argument_list|(
literal|"8080"
argument_list|)
operator|.
name|when
argument_list|(
name|reqMock
argument_list|)
operator|.
name|getParameter
argument_list|(
literal|"namenodeInfoPort"
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|doReturn
argument_list|(
literal|"100"
argument_list|)
operator|.
name|when
argument_list|(
name|reqMock
argument_list|)
operator|.
name|getParameter
argument_list|(
literal|"chunkSizeToView"
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|doReturn
argument_list|(
literal|"1"
argument_list|)
operator|.
name|when
argument_list|(
name|reqMock
argument_list|)
operator|.
name|getParameter
argument_list|(
literal|"startOffset"
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|doReturn
argument_list|(
literal|"1024"
argument_list|)
operator|.
name|when
argument_list|(
name|reqMock
argument_list|)
operator|.
name|getParameter
argument_list|(
literal|"blockSize"
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|doReturn
argument_list|(
name|NameNode
operator|.
name|getHostPortString
argument_list|(
name|NameNode
operator|.
name|getAddress
argument_list|(
name|CONF
argument_list|)
argument_list|)
argument_list|)
operator|.
name|when
argument_list|(
name|reqMock
argument_list|)
operator|.
name|getParameter
argument_list|(
literal|"nnaddr"
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|doReturn
argument_list|(
name|testFile
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|when
argument_list|(
name|reqMock
argument_list|)
operator|.
name|getPathInfo
argument_list|()
expr_stmt|;
block|}
DECL|method|writeFile (FileSystem fs, Path f)
specifier|static
name|Path
name|writeFile
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|f
parameter_list|)
throws|throws
name|IOException
block|{
name|DataOutputStream
name|out
init|=
name|fs
operator|.
name|create
argument_list|(
name|f
argument_list|)
decl_stmt|;
try|try
block|{
name|out
operator|.
name|writeBytes
argument_list|(
literal|"umamahesh: "
operator|+
name|f
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|fs
operator|.
name|exists
argument_list|(
name|f
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|f
return|;
block|}
block|}
end_class

end_unit

