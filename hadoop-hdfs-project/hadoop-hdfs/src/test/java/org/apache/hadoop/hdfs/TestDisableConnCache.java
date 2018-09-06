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
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
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
name|client
operator|.
name|HdfsClientConfigKeys
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
name|client
operator|.
name|impl
operator|.
name|BlockReaderTestUtil
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
comment|/**  * This class tests disabling client connection caching in a single node  * mini-cluster.  */
end_comment

begin_class
DECL|class|TestDisableConnCache
specifier|public
class|class
name|TestDisableConnCache
block|{
DECL|field|LOG
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestDisableConnCache
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|BLOCK_SIZE
specifier|static
specifier|final
name|int
name|BLOCK_SIZE
init|=
literal|4096
decl_stmt|;
DECL|field|FILE_SIZE
specifier|static
specifier|final
name|int
name|FILE_SIZE
init|=
literal|3
operator|*
name|BLOCK_SIZE
decl_stmt|;
comment|/**    * Test that the socket cache can be disabled by setting the capacity to    * 0. Regression test for HDFS-3365.    * @throws Exception     */
annotation|@
name|Test
DECL|method|testDisableCache ()
specifier|public
name|void
name|testDisableCache
parameter_list|()
throws|throws
name|Exception
block|{
name|HdfsConfiguration
name|confWithoutCache
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
comment|// Configure a new instance with no peer caching, ensure that it doesn't
comment|// cache anything
name|confWithoutCache
operator|.
name|setInt
argument_list|(
name|HdfsClientConfigKeys
operator|.
name|DFS_CLIENT_SOCKET_CACHE_CAPACITY_KEY
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|BlockReaderTestUtil
name|util
init|=
operator|new
name|BlockReaderTestUtil
argument_list|(
literal|1
argument_list|,
name|confWithoutCache
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|testFile
init|=
operator|new
name|Path
argument_list|(
literal|"/testConnCache.dat"
argument_list|)
decl_stmt|;
name|util
operator|.
name|writeFile
argument_list|(
name|testFile
argument_list|,
name|FILE_SIZE
operator|/
literal|1024
argument_list|)
expr_stmt|;
name|FileSystem
name|fsWithoutCache
init|=
name|FileSystem
operator|.
name|newInstance
argument_list|(
name|util
operator|.
name|getConf
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|DFSTestUtil
operator|.
name|readFile
argument_list|(
name|fsWithoutCache
argument_list|,
name|testFile
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
operator|(
operator|(
name|DistributedFileSystem
operator|)
name|fsWithoutCache
operator|)
operator|.
name|dfs
operator|.
name|getClientContext
argument_list|()
operator|.
name|getPeerCache
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|fsWithoutCache
operator|.
name|close
argument_list|()
expr_stmt|;
name|util
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

