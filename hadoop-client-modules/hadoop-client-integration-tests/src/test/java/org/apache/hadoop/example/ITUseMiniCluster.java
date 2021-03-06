begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  *  */
end_comment

begin_package
DECL|package|org.apache.hadoop.example
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|example
package|;
end_package

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
name|URISyntaxException
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
name|Assert
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
name|hdfs
operator|.
name|web
operator|.
name|WebHdfsTestUtil
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
name|WebHdfsConstants
import|;
end_import

begin_comment
comment|/**  * Ensure that we can perform operations against the shaded minicluster  * given the API and runtime jars by performing some simple smoke tests.  */
end_comment

begin_class
DECL|class|ITUseMiniCluster
specifier|public
class|class
name|ITUseMiniCluster
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
name|ITUseMiniCluster
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|cluster
specifier|private
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|TEST_PATH
specifier|private
specifier|static
specifier|final
name|String
name|TEST_PATH
init|=
literal|"/foo/bar/cats/dee"
decl_stmt|;
DECL|field|FILENAME
specifier|private
specifier|static
specifier|final
name|String
name|FILENAME
init|=
literal|"test.file"
decl_stmt|;
DECL|field|TEXT
specifier|private
specifier|static
specifier|final
name|String
name|TEXT
init|=
literal|"Lorem ipsum dolor sit amet, consectetur "
operator|+
literal|"adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore "
operator|+
literal|"magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation "
operator|+
literal|"ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute "
operator|+
literal|"irure dolor in reprehenderit in voluptate velit esse cillum dolore eu "
operator|+
literal|"fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident,"
operator|+
literal|" sunt in culpa qui officia deserunt mollit anim id est laborum."
decl_stmt|;
annotation|@
name|Before
DECL|method|clusterUp ()
specifier|public
name|void
name|clusterUp
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
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
literal|3
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
block|}
annotation|@
name|After
DECL|method|clusterDown ()
specifier|public
name|void
name|clusterDown
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
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|useHdfsFileSystem ()
specifier|public
name|void
name|useHdfsFileSystem
parameter_list|()
throws|throws
name|IOException
block|{
try|try
init|(
name|FileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
init|)
block|{
name|simpleReadAfterWrite
argument_list|(
name|fs
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|simpleReadAfterWrite (final FileSystem fs)
specifier|public
name|void
name|simpleReadAfterWrite
parameter_list|(
specifier|final
name|FileSystem
name|fs
parameter_list|)
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Testing read-after-write with FS implementation: {}"
argument_list|,
name|fs
argument_list|)
expr_stmt|;
specifier|final
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
name|TEST_PATH
argument_list|,
name|FILENAME
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|fs
operator|.
name|mkdirs
argument_list|(
name|path
operator|.
name|getParent
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Mkdirs failed to create "
operator|+
name|TEST_PATH
argument_list|)
throw|;
block|}
try|try
init|(
name|FSDataOutputStream
name|out
init|=
name|fs
operator|.
name|create
argument_list|(
name|path
argument_list|)
init|)
block|{
name|out
operator|.
name|writeUTF
argument_list|(
name|TEXT
argument_list|)
expr_stmt|;
block|}
try|try
init|(
name|FSDataInputStream
name|in
init|=
name|fs
operator|.
name|open
argument_list|(
name|path
argument_list|)
init|)
block|{
specifier|final
name|String
name|result
init|=
name|in
operator|.
name|readUTF
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Didn't read back text we wrote."
argument_list|,
name|TEXT
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|useWebHDFS ()
specifier|public
name|void
name|useWebHDFS
parameter_list|()
throws|throws
name|IOException
throws|,
name|URISyntaxException
block|{
try|try
init|(
name|FileSystem
name|fs
init|=
name|WebHdfsTestUtil
operator|.
name|getWebHdfsFileSystem
argument_list|(
name|cluster
operator|.
name|getConfiguration
argument_list|(
literal|0
argument_list|)
argument_list|,
name|WebHdfsConstants
operator|.
name|WEBHDFS_SCHEME
argument_list|)
init|)
block|{
name|simpleReadAfterWrite
argument_list|(
name|fs
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

