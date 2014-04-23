begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
package|;
end_package

begin_import
import|import static
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
name|Util
operator|.
name|fileAsURI
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
name|assertNull
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assume
operator|.
name|assumeTrue
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
name|hdfs
operator|.
name|server
operator|.
name|common
operator|.
name|HdfsServerConstants
operator|.
name|StartupOption
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
name|datanode
operator|.
name|DataNode
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
name|io
operator|.
name|nativeio
operator|.
name|NativeIO
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
comment|/**  * Tests if a data-node can startup depending on configuration parameters.  */
end_comment

begin_class
DECL|class|TestDatanodeConfig
specifier|public
class|class
name|TestDatanodeConfig
block|{
DECL|field|BASE_DIR
specifier|private
specifier|static
specifier|final
name|File
name|BASE_DIR
init|=
operator|new
name|File
argument_list|(
name|MiniDFSCluster
operator|.
name|getBaseDirectory
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|cluster
specifier|private
specifier|static
name|MiniDFSCluster
name|cluster
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
name|clearBaseDir
argument_list|()
expr_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_HTTPS_PORT_KEY
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_ADDRESS_KEY
argument_list|,
literal|"localhost:0"
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
literal|0
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
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|clearBaseDir
argument_list|()
expr_stmt|;
block|}
DECL|method|clearBaseDir ()
specifier|private
specifier|static
name|void
name|clearBaseDir
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|BASE_DIR
operator|.
name|exists
argument_list|()
operator|&&
operator|!
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|BASE_DIR
argument_list|)
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Cannot clear BASE_DIR "
operator|+
name|BASE_DIR
argument_list|)
throw|;
block|}
comment|/**    * Test that a data-node does not start if configuration specifies    * incorrect URI scheme in data directory.    * Test that a data-node starts if data directory is specified as    * URI = "file:///path" or as a non URI path.    */
annotation|@
name|Test
DECL|method|testDataDirectories ()
specifier|public
name|void
name|testDataDirectories
parameter_list|()
throws|throws
name|IOException
block|{
name|File
name|dataDir
init|=
operator|new
name|File
argument_list|(
name|BASE_DIR
argument_list|,
literal|"data"
argument_list|)
operator|.
name|getCanonicalFile
argument_list|()
decl_stmt|;
name|Configuration
name|conf
init|=
name|cluster
operator|.
name|getConfiguration
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|// 1. Test unsupported schema. Only "file:" is supported.
name|String
name|dnDir
init|=
name|makeURI
argument_list|(
literal|"shv"
argument_list|,
literal|null
argument_list|,
name|fileAsURI
argument_list|(
name|dataDir
argument_list|)
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_DATA_DIR_KEY
argument_list|,
name|dnDir
argument_list|)
expr_stmt|;
name|DataNode
name|dn
init|=
literal|null
decl_stmt|;
try|try
block|{
name|dn
operator|=
name|DataNode
operator|.
name|createDataNode
argument_list|(
operator|new
name|String
index|[]
block|{}
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// expecting exception here
block|}
finally|finally
block|{
if|if
condition|(
name|dn
operator|!=
literal|null
condition|)
block|{
name|dn
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
name|assertNull
argument_list|(
literal|"Data-node startup should have failed."
argument_list|,
name|dn
argument_list|)
expr_stmt|;
comment|// 2. Test "file:" schema and no schema (path-only). Both should work.
name|String
name|dnDir1
init|=
name|fileAsURI
argument_list|(
name|dataDir
argument_list|)
operator|.
name|toString
argument_list|()
operator|+
literal|"1"
decl_stmt|;
name|String
name|dnDir2
init|=
name|makeURI
argument_list|(
literal|"file"
argument_list|,
literal|"localhost"
argument_list|,
name|fileAsURI
argument_list|(
name|dataDir
argument_list|)
operator|.
name|getPath
argument_list|()
operator|+
literal|"2"
argument_list|)
decl_stmt|;
name|String
name|dnDir3
init|=
name|dataDir
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|"3"
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_DATA_DIR_KEY
argument_list|,
name|dnDir1
operator|+
literal|","
operator|+
name|dnDir2
operator|+
literal|","
operator|+
name|dnDir3
argument_list|)
expr_stmt|;
try|try
block|{
name|cluster
operator|.
name|startDataNodes
argument_list|(
name|conf
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|,
name|StartupOption
operator|.
name|REGULAR
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Data-node should startup."
argument_list|,
name|cluster
operator|.
name|isDataNodeUp
argument_list|()
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
block|{
name|cluster
operator|.
name|shutdownDataNodes
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|makeURI (String scheme, String host, String path)
specifier|private
specifier|static
name|String
name|makeURI
parameter_list|(
name|String
name|scheme
parameter_list|,
name|String
name|host
parameter_list|,
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|URI
name|uDir
init|=
operator|new
name|URI
argument_list|(
name|scheme
argument_list|,
name|host
argument_list|,
name|path
argument_list|,
literal|null
argument_list|)
decl_stmt|;
return|return
name|uDir
operator|.
name|toString
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Bad URI"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testMemlockLimit ()
specifier|public
name|void
name|testMemlockLimit
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeTrue
argument_list|(
name|NativeIO
operator|.
name|isAvailable
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|long
name|memlockLimit
init|=
name|NativeIO
operator|.
name|POSIX
operator|.
name|getCacheManipulator
argument_list|()
operator|.
name|getMemlockLimit
argument_list|()
decl_stmt|;
comment|// Can't increase the memlock limit past the maximum.
name|assumeTrue
argument_list|(
name|memlockLimit
operator|!=
name|Long
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
name|File
name|dataDir
init|=
operator|new
name|File
argument_list|(
name|BASE_DIR
argument_list|,
literal|"data"
argument_list|)
operator|.
name|getCanonicalFile
argument_list|()
decl_stmt|;
name|Configuration
name|conf
init|=
name|cluster
operator|.
name|getConfiguration
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_DATA_DIR_KEY
argument_list|,
name|makeURI
argument_list|(
literal|"file"
argument_list|,
literal|null
argument_list|,
name|fileAsURI
argument_list|(
name|dataDir
argument_list|)
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|long
name|prevLimit
init|=
name|conf
operator|.
name|getLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_MAX_LOCKED_MEMORY_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_MAX_LOCKED_MEMORY_DEFAULT
argument_list|)
decl_stmt|;
name|DataNode
name|dn
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|// Try starting the DN with limit configured to the ulimit
name|conf
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_MAX_LOCKED_MEMORY_KEY
argument_list|,
name|memlockLimit
argument_list|)
expr_stmt|;
name|dn
operator|=
name|DataNode
operator|.
name|createDataNode
argument_list|(
operator|new
name|String
index|[]
block|{}
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|dn
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|dn
operator|=
literal|null
expr_stmt|;
comment|// Try starting the DN with a limit> ulimit
name|conf
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_MAX_LOCKED_MEMORY_KEY
argument_list|,
name|memlockLimit
operator|+
literal|1
argument_list|)
expr_stmt|;
try|try
block|{
name|dn
operator|=
name|DataNode
operator|.
name|createDataNode
argument_list|(
operator|new
name|String
index|[]
block|{}
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"more than the datanode's available RLIMIT_MEMLOCK"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|dn
operator|!=
literal|null
condition|)
block|{
name|dn
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
name|conf
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_MAX_LOCKED_MEMORY_KEY
argument_list|,
name|prevLimit
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

