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
name|File
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
name|InetSocketAddress
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
name|FileUtil
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
name|http
operator|.
name|HttpConfig
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
name|apache
operator|.
name|hadoop
operator|.
name|security
operator|.
name|ssl
operator|.
name|KeyStoreTestUtil
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
DECL|class|TestHttpsFileSystem
specifier|public
class|class
name|TestHttpsFileSystem
block|{
DECL|field|BASEDIR
specifier|private
specifier|static
specifier|final
name|String
name|BASEDIR
init|=
name|GenericTestUtils
operator|.
name|getTempPath
argument_list|(
name|TestHttpsFileSystem
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|cluster
specifier|private
specifier|static
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|conf
specifier|private
specifier|static
name|Configuration
name|conf
decl_stmt|;
DECL|field|keystoresDir
specifier|private
specifier|static
name|String
name|keystoresDir
decl_stmt|;
DECL|field|sslConfDir
specifier|private
specifier|static
name|String
name|sslConfDir
decl_stmt|;
DECL|field|nnAddr
specifier|private
specifier|static
name|String
name|nnAddr
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|setUp ()
specifier|public
specifier|static
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_HTTP_POLICY_KEY
argument_list|,
name|HttpConfig
operator|.
name|Policy
operator|.
name|HTTPS_ONLY
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_HTTPS_ADDRESS_KEY
argument_list|,
literal|"localhost:0"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_HTTPS_ADDRESS_KEY
argument_list|,
literal|"localhost:0"
argument_list|)
expr_stmt|;
name|File
name|base
init|=
operator|new
name|File
argument_list|(
name|BASEDIR
argument_list|)
decl_stmt|;
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|base
argument_list|)
expr_stmt|;
name|base
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|keystoresDir
operator|=
operator|new
name|File
argument_list|(
name|BASEDIR
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
expr_stmt|;
name|sslConfDir
operator|=
name|KeyStoreTestUtil
operator|.
name|getClasspathDir
argument_list|(
name|TestHttpsFileSystem
operator|.
name|class
argument_list|)
expr_stmt|;
name|KeyStoreTestUtil
operator|.
name|setupSSLConfig
argument_list|(
name|keystoresDir
argument_list|,
name|sslConfDir
argument_list|,
name|conf
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_CLIENT_HTTPS_KEYSTORE_RESOURCE_KEY
argument_list|,
name|KeyStoreTestUtil
operator|.
name|getClientSSLConfigFileName
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_SERVER_HTTPS_KEYSTORE_RESOURCE_KEY
argument_list|,
name|KeyStoreTestUtil
operator|.
name|getServerSSLConfigFileName
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
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|OutputStream
name|os
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
operator|.
name|create
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/test"
argument_list|)
argument_list|)
decl_stmt|;
name|os
operator|.
name|write
argument_list|(
literal|23
argument_list|)
expr_stmt|;
name|os
operator|.
name|close
argument_list|()
expr_stmt|;
name|InetSocketAddress
name|addr
init|=
name|cluster
operator|.
name|getNameNode
argument_list|()
operator|.
name|getHttpsAddress
argument_list|()
decl_stmt|;
name|nnAddr
operator|=
name|NetUtils
operator|.
name|getHostPortString
argument_list|(
name|addr
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_HTTPS_ADDRESS_KEY
argument_list|,
name|nnAddr
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|tearDown ()
specifier|public
specifier|static
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
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
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
operator|new
name|File
argument_list|(
name|BASEDIR
argument_list|)
argument_list|)
expr_stmt|;
name|KeyStoreTestUtil
operator|.
name|cleanupSSLConfig
argument_list|(
name|keystoresDir
argument_list|,
name|sslConfDir
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSWebHdfsFileSystem ()
specifier|public
name|void
name|testSWebHdfsFileSystem
parameter_list|()
throws|throws
name|Exception
block|{
name|FileSystem
name|fs
init|=
name|WebHdfsTestUtil
operator|.
name|getWebHdfsFileSystem
argument_list|(
name|conf
argument_list|,
literal|"swebhdfs"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|f
init|=
operator|new
name|Path
argument_list|(
literal|"/testswebhdfs"
argument_list|)
decl_stmt|;
name|FSDataOutputStream
name|os
init|=
name|fs
operator|.
name|create
argument_list|(
name|f
argument_list|)
decl_stmt|;
name|os
operator|.
name|write
argument_list|(
literal|23
argument_list|)
expr_stmt|;
name|os
operator|.
name|close
argument_list|()
expr_stmt|;
name|Assert
operator|.
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
name|InputStream
name|is
init|=
name|fs
operator|.
name|open
argument_list|(
name|f
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|23
argument_list|,
name|is
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

