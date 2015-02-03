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
name|URI
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
name|Log
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
name|MiniDFSNNTopology
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
name|web
operator|.
name|resources
operator|.
name|NamenodeWebHdfsMethods
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

begin_comment
comment|/**  * Test WebHDFS with multiple NameNodes  */
end_comment

begin_class
DECL|class|TestWebHdfsWithMultipleNameNodes
specifier|public
class|class
name|TestWebHdfsWithMultipleNameNodes
block|{
DECL|field|LOG
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|WebHdfsTestUtil
operator|.
name|LOG
decl_stmt|;
DECL|method|setLogLevel ()
specifier|static
specifier|private
name|void
name|setLogLevel
parameter_list|()
block|{
operator|(
operator|(
name|Log4JLogger
operator|)
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
name|GenericTestUtils
operator|.
name|setLogLevel
argument_list|(
name|NamenodeWebHdfsMethods
operator|.
name|LOG
argument_list|,
name|Level
operator|.
name|ALL
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|setNameNodeLogLevel
argument_list|(
name|Level
operator|.
name|ALL
argument_list|)
expr_stmt|;
block|}
DECL|field|conf
specifier|private
specifier|static
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
DECL|field|cluster
specifier|private
specifier|static
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|webhdfs
specifier|private
specifier|static
name|WebHdfsFileSystem
index|[]
name|webhdfs
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|setupTest ()
specifier|public
specifier|static
name|void
name|setupTest
parameter_list|()
block|{
name|setLogLevel
argument_list|()
expr_stmt|;
try|try
block|{
name|setupCluster
argument_list|(
literal|4
argument_list|,
literal|3
argument_list|)
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
DECL|method|setupCluster (final int nNameNodes, final int nDataNodes)
specifier|private
specifier|static
name|void
name|setupCluster
parameter_list|(
specifier|final
name|int
name|nNameNodes
parameter_list|,
specifier|final
name|int
name|nDataNodes
parameter_list|)
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"nNameNodes="
operator|+
name|nNameNodes
operator|+
literal|", nDataNodes="
operator|+
name|nDataNodes
argument_list|)
expr_stmt|;
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
name|nnTopology
argument_list|(
name|MiniDFSNNTopology
operator|.
name|simpleFederatedTopology
argument_list|(
name|nNameNodes
argument_list|)
argument_list|)
operator|.
name|numDataNodes
argument_list|(
name|nDataNodes
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
name|webhdfs
operator|=
operator|new
name|WebHdfsFileSystem
index|[
name|nNameNodes
index|]
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
name|webhdfs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|InetSocketAddress
name|addr
init|=
name|cluster
operator|.
name|getNameNode
argument_list|(
name|i
argument_list|)
operator|.
name|getHttpAddress
argument_list|()
decl_stmt|;
specifier|final
name|String
name|uri
init|=
name|WebHdfsFileSystem
operator|.
name|SCHEME
operator|+
literal|"://"
operator|+
name|addr
operator|.
name|getHostName
argument_list|()
operator|+
literal|":"
operator|+
name|addr
operator|.
name|getPort
argument_list|()
operator|+
literal|"/"
decl_stmt|;
name|webhdfs
index|[
name|i
index|]
operator|=
operator|(
name|WebHdfsFileSystem
operator|)
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
expr_stmt|;
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
DECL|method|createString (String prefix, int i)
specifier|private
specifier|static
name|String
name|createString
parameter_list|(
name|String
name|prefix
parameter_list|,
name|int
name|i
parameter_list|)
block|{
comment|//The suffix is to make sure the strings have different lengths.
specifier|final
name|String
name|suffix
init|=
literal|"*********************"
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|i
operator|+
literal|1
argument_list|)
decl_stmt|;
return|return
name|prefix
operator|+
name|i
operator|+
name|suffix
operator|+
literal|"\n"
return|;
block|}
DECL|method|createStrings (String prefix, String name)
specifier|private
specifier|static
name|String
index|[]
name|createStrings
parameter_list|(
name|String
name|prefix
parameter_list|,
name|String
name|name
parameter_list|)
block|{
specifier|final
name|String
index|[]
name|strings
init|=
operator|new
name|String
index|[
name|webhdfs
operator|.
name|length
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
name|webhdfs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|strings
index|[
name|i
index|]
operator|=
name|createString
argument_list|(
name|prefix
argument_list|,
name|i
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|name
operator|+
literal|"["
operator|+
name|i
operator|+
literal|"] = "
operator|+
name|strings
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|strings
return|;
block|}
annotation|@
name|Test
DECL|method|testRedirect ()
specifier|public
name|void
name|testRedirect
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|dir
init|=
literal|"/testRedirect/"
decl_stmt|;
specifier|final
name|String
name|filename
init|=
literal|"file"
decl_stmt|;
specifier|final
name|Path
name|p
init|=
operator|new
name|Path
argument_list|(
name|dir
argument_list|,
name|filename
argument_list|)
decl_stmt|;
specifier|final
name|String
index|[]
name|writeStrings
init|=
name|createStrings
argument_list|(
literal|"write to webhdfs "
argument_list|,
literal|"write"
argument_list|)
decl_stmt|;
specifier|final
name|String
index|[]
name|appendStrings
init|=
name|createStrings
argument_list|(
literal|"append to webhdfs "
argument_list|,
literal|"append"
argument_list|)
decl_stmt|;
comment|//test create: create a file for each namenode
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|webhdfs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|FSDataOutputStream
name|out
init|=
name|webhdfs
index|[
name|i
index|]
operator|.
name|create
argument_list|(
name|p
argument_list|)
decl_stmt|;
name|out
operator|.
name|write
argument_list|(
name|writeStrings
index|[
name|i
index|]
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|webhdfs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
comment|//check file length
specifier|final
name|long
name|expected
init|=
name|writeStrings
index|[
name|i
index|]
operator|.
name|length
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|webhdfs
index|[
name|i
index|]
operator|.
name|getFileStatus
argument_list|(
name|p
argument_list|)
operator|.
name|getLen
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//test read: check file content for each namenode
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|webhdfs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|FSDataInputStream
name|in
init|=
name|webhdfs
index|[
name|i
index|]
operator|.
name|open
argument_list|(
name|p
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|c
init|,
name|j
init|=
literal|0
init|;
operator|(
name|c
operator|=
name|in
operator|.
name|read
argument_list|()
operator|)
operator|!=
operator|-
literal|1
condition|;
name|j
operator|++
control|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|writeStrings
index|[
name|i
index|]
operator|.
name|charAt
argument_list|(
name|j
argument_list|)
argument_list|,
name|c
argument_list|)
expr_stmt|;
block|}
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|//test append: append to the file for each namenode
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|webhdfs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|FSDataOutputStream
name|out
init|=
name|webhdfs
index|[
name|i
index|]
operator|.
name|append
argument_list|(
name|p
argument_list|)
decl_stmt|;
name|out
operator|.
name|write
argument_list|(
name|appendStrings
index|[
name|i
index|]
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|webhdfs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
comment|//check file length
specifier|final
name|long
name|expected
init|=
name|writeStrings
index|[
name|i
index|]
operator|.
name|length
argument_list|()
operator|+
name|appendStrings
index|[
name|i
index|]
operator|.
name|length
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|webhdfs
index|[
name|i
index|]
operator|.
name|getFileStatus
argument_list|(
name|p
argument_list|)
operator|.
name|getLen
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//test read: check file content for each namenode
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|webhdfs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|StringBuilder
name|b
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
specifier|final
name|FSDataInputStream
name|in
init|=
name|webhdfs
index|[
name|i
index|]
operator|.
name|open
argument_list|(
name|p
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|c
init|;
operator|(
name|c
operator|=
name|in
operator|.
name|read
argument_list|()
operator|)
operator|!=
operator|-
literal|1
condition|;
control|)
block|{
name|b
operator|.
name|append
argument_list|(
operator|(
name|char
operator|)
name|c
argument_list|)
expr_stmt|;
block|}
specifier|final
name|int
name|wlen
init|=
name|writeStrings
index|[
name|i
index|]
operator|.
name|length
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|writeStrings
index|[
name|i
index|]
argument_list|,
name|b
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|wlen
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|appendStrings
index|[
name|i
index|]
argument_list|,
name|b
operator|.
name|substring
argument_list|(
name|wlen
argument_list|)
argument_list|)
expr_stmt|;
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

