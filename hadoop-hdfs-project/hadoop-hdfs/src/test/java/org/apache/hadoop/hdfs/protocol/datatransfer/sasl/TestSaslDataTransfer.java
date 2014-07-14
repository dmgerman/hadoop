begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.protocol.datatransfer.sasl
package|package
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
name|datatransfer
operator|.
name|sasl
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
name|DFSConfigKeys
operator|.
name|DFS_DATA_TRANSFER_PROTECTION_KEY
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
name|*
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|BlockLocation
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
name|io
operator|.
name|IOUtils
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
name|Rule
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
name|junit
operator|.
name|rules
operator|.
name|ExpectedException
import|;
end_import

begin_class
DECL|class|TestSaslDataTransfer
specifier|public
class|class
name|TestSaslDataTransfer
extends|extends
name|SaslDataTransferTestCase
block|{
DECL|field|BLOCK_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|BLOCK_SIZE
init|=
literal|4096
decl_stmt|;
DECL|field|BUFFER_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|BUFFER_SIZE
init|=
literal|1024
decl_stmt|;
DECL|field|NUM_BLOCKS
specifier|private
specifier|static
specifier|final
name|int
name|NUM_BLOCKS
init|=
literal|3
decl_stmt|;
DECL|field|PATH
specifier|private
specifier|static
specifier|final
name|Path
name|PATH
init|=
operator|new
name|Path
argument_list|(
literal|"/file1"
argument_list|)
decl_stmt|;
DECL|field|REPLICATION
specifier|private
specifier|static
specifier|final
name|short
name|REPLICATION
init|=
literal|3
decl_stmt|;
DECL|field|cluster
specifier|private
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|fs
specifier|private
name|FileSystem
name|fs
decl_stmt|;
annotation|@
name|Rule
DECL|field|exception
specifier|public
name|ExpectedException
name|exception
init|=
name|ExpectedException
operator|.
name|none
argument_list|()
decl_stmt|;
annotation|@
name|After
DECL|method|shutdown ()
specifier|public
name|void
name|shutdown
parameter_list|()
block|{
name|IOUtils
operator|.
name|cleanup
argument_list|(
literal|null
argument_list|,
name|fs
argument_list|)
expr_stmt|;
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
annotation|@
name|Test
DECL|method|testAuthentication ()
specifier|public
name|void
name|testAuthentication
parameter_list|()
throws|throws
name|Exception
block|{
name|HdfsConfiguration
name|clusterConf
init|=
name|createSecureConfig
argument_list|(
literal|"authentication,integrity,privacy"
argument_list|)
decl_stmt|;
name|startCluster
argument_list|(
name|clusterConf
argument_list|)
expr_stmt|;
name|HdfsConfiguration
name|clientConf
init|=
operator|new
name|HdfsConfiguration
argument_list|(
name|clusterConf
argument_list|)
decl_stmt|;
name|clientConf
operator|.
name|set
argument_list|(
name|DFS_DATA_TRANSFER_PROTECTION_KEY
argument_list|,
literal|"authentication"
argument_list|)
expr_stmt|;
name|doTest
argument_list|(
name|clientConf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testIntegrity ()
specifier|public
name|void
name|testIntegrity
parameter_list|()
throws|throws
name|Exception
block|{
name|HdfsConfiguration
name|clusterConf
init|=
name|createSecureConfig
argument_list|(
literal|"authentication,integrity,privacy"
argument_list|)
decl_stmt|;
name|startCluster
argument_list|(
name|clusterConf
argument_list|)
expr_stmt|;
name|HdfsConfiguration
name|clientConf
init|=
operator|new
name|HdfsConfiguration
argument_list|(
name|clusterConf
argument_list|)
decl_stmt|;
name|clientConf
operator|.
name|set
argument_list|(
name|DFS_DATA_TRANSFER_PROTECTION_KEY
argument_list|,
literal|"integrity"
argument_list|)
expr_stmt|;
name|doTest
argument_list|(
name|clientConf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPrivacy ()
specifier|public
name|void
name|testPrivacy
parameter_list|()
throws|throws
name|Exception
block|{
name|HdfsConfiguration
name|clusterConf
init|=
name|createSecureConfig
argument_list|(
literal|"authentication,integrity,privacy"
argument_list|)
decl_stmt|;
name|startCluster
argument_list|(
name|clusterConf
argument_list|)
expr_stmt|;
name|HdfsConfiguration
name|clientConf
init|=
operator|new
name|HdfsConfiguration
argument_list|(
name|clusterConf
argument_list|)
decl_stmt|;
name|clientConf
operator|.
name|set
argument_list|(
name|DFS_DATA_TRANSFER_PROTECTION_KEY
argument_list|,
literal|"privacy"
argument_list|)
expr_stmt|;
name|doTest
argument_list|(
name|clientConf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testClientAndServerDoNotHaveCommonQop ()
specifier|public
name|void
name|testClientAndServerDoNotHaveCommonQop
parameter_list|()
throws|throws
name|Exception
block|{
name|HdfsConfiguration
name|clusterConf
init|=
name|createSecureConfig
argument_list|(
literal|"privacy"
argument_list|)
decl_stmt|;
name|startCluster
argument_list|(
name|clusterConf
argument_list|)
expr_stmt|;
name|HdfsConfiguration
name|clientConf
init|=
operator|new
name|HdfsConfiguration
argument_list|(
name|clusterConf
argument_list|)
decl_stmt|;
name|clientConf
operator|.
name|set
argument_list|(
name|DFS_DATA_TRANSFER_PROTECTION_KEY
argument_list|,
literal|"authentication"
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expect
argument_list|(
name|IOException
operator|.
name|class
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expectMessage
argument_list|(
literal|"could only be replicated to 0 nodes"
argument_list|)
expr_stmt|;
name|doTest
argument_list|(
name|clientConf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testClientSaslNoServerSasl ()
specifier|public
name|void
name|testClientSaslNoServerSasl
parameter_list|()
throws|throws
name|Exception
block|{
name|HdfsConfiguration
name|clusterConf
init|=
name|createSecureConfig
argument_list|(
literal|""
argument_list|)
decl_stmt|;
name|startCluster
argument_list|(
name|clusterConf
argument_list|)
expr_stmt|;
name|HdfsConfiguration
name|clientConf
init|=
operator|new
name|HdfsConfiguration
argument_list|(
name|clusterConf
argument_list|)
decl_stmt|;
name|clientConf
operator|.
name|set
argument_list|(
name|DFS_DATA_TRANSFER_PROTECTION_KEY
argument_list|,
literal|"authentication"
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expect
argument_list|(
name|IOException
operator|.
name|class
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expectMessage
argument_list|(
literal|"could only be replicated to 0 nodes"
argument_list|)
expr_stmt|;
name|doTest
argument_list|(
name|clientConf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testServerSaslNoClientSasl ()
specifier|public
name|void
name|testServerSaslNoClientSasl
parameter_list|()
throws|throws
name|Exception
block|{
name|HdfsConfiguration
name|clusterConf
init|=
name|createSecureConfig
argument_list|(
literal|"authentication,integrity,privacy"
argument_list|)
decl_stmt|;
name|startCluster
argument_list|(
name|clusterConf
argument_list|)
expr_stmt|;
name|HdfsConfiguration
name|clientConf
init|=
operator|new
name|HdfsConfiguration
argument_list|(
name|clusterConf
argument_list|)
decl_stmt|;
name|clientConf
operator|.
name|set
argument_list|(
name|DFS_DATA_TRANSFER_PROTECTION_KEY
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expect
argument_list|(
name|IOException
operator|.
name|class
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expectMessage
argument_list|(
literal|"could only be replicated to 0 nodes"
argument_list|)
expr_stmt|;
name|doTest
argument_list|(
name|clientConf
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests DataTransferProtocol with the given client configuration.    *    * @param conf client configuration    * @throws IOException if there is an I/O error    */
DECL|method|doTest (HdfsConfiguration conf)
specifier|private
name|void
name|doTest
parameter_list|(
name|HdfsConfiguration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|fs
operator|=
name|FileSystem
operator|.
name|get
argument_list|(
name|cluster
operator|.
name|getURI
argument_list|()
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|FileSystemTestHelper
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|PATH
argument_list|,
name|NUM_BLOCKS
argument_list|,
name|BLOCK_SIZE
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|FileSystemTestHelper
operator|.
name|getFileData
argument_list|(
name|NUM_BLOCKS
argument_list|,
name|BLOCK_SIZE
argument_list|)
argument_list|,
name|DFSTestUtil
operator|.
name|readFile
argument_list|(
name|fs
argument_list|,
name|PATH
argument_list|)
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
name|BlockLocation
index|[]
name|blockLocations
init|=
name|fs
operator|.
name|getFileBlockLocations
argument_list|(
name|PATH
argument_list|,
literal|0
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|blockLocations
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NUM_BLOCKS
argument_list|,
name|blockLocations
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|BlockLocation
name|blockLocation
range|:
name|blockLocations
control|)
block|{
name|assertNotNull
argument_list|(
name|blockLocation
operator|.
name|getHosts
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|blockLocation
operator|.
name|getHosts
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Starts a cluster with the given configuration.    *    * @param conf cluster configuration    * @throws IOException if there is an I/O error    */
DECL|method|startCluster (HdfsConfiguration conf)
specifier|private
name|void
name|startCluster
parameter_list|(
name|HdfsConfiguration
name|conf
parameter_list|)
throws|throws
name|IOException
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
block|}
end_class

end_unit

