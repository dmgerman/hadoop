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
name|Test
import|;
end_import

begin_comment
comment|/**  * This class tests the validation of the configuration object when passed   * to the NameNode  */
end_comment

begin_class
DECL|class|TestValidateConfigurationSettings
specifier|public
class|class
name|TestValidateConfigurationSettings
block|{
comment|/**    * Tests setting the rpc port to the same as the web port to test that     * an exception    * is thrown when trying to re-use the same port    */
annotation|@
name|Test
DECL|method|testThatMatchingRPCandHttpPortsThrowException ()
specifier|public
name|void
name|testThatMatchingRPCandHttpPortsThrowException
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
comment|// set both of these to port 9000, should fail
name|FileSystem
operator|.
name|setDefaultUri
argument_list|(
name|conf
argument_list|,
literal|"hdfs://localhost:9000"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_HTTP_ADDRESS_KEY
argument_list|,
literal|"127.0.0.1:9000"
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|formatNameNode
argument_list|(
name|conf
argument_list|)
expr_stmt|;
try|try
block|{
name|NameNode
name|nameNode
init|=
operator|new
name|NameNode
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|fail
argument_list|(
literal|"Should have throw the exception since the ports match"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// verify we're getting the right IOException
name|assertTrue
argument_list|(
name|e
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"dfs.namenode.rpc-address ("
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Got expected exception: "
operator|+
name|e
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Tests setting the rpc port to a different as the web port that an     * exception is NOT thrown     */
annotation|@
name|Test
DECL|method|testThatDifferentRPCandHttpPortsAreOK ()
specifier|public
name|void
name|testThatDifferentRPCandHttpPortsAreOK
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
name|FileSystem
operator|.
name|setDefaultUri
argument_list|(
name|conf
argument_list|,
literal|"hdfs://localhost:8000"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_HTTP_ADDRESS_KEY
argument_list|,
literal|"127.0.0.1:9000"
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|formatNameNode
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|NameNode
name|nameNode
init|=
operator|new
name|NameNode
argument_list|(
name|conf
argument_list|)
decl_stmt|;
comment|// should be OK!
name|nameNode
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
comment|/**    * HDFS-3013: NameNode format command doesn't pick up    * dfs.namenode.name.dir.NameServiceId configuration.    */
annotation|@
name|Test
DECL|method|testGenericKeysForNameNodeFormat ()
specifier|public
name|void
name|testGenericKeysForNameNodeFormat
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
comment|// Set ephemeral ports
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_RPC_ADDRESS_KEY
argument_list|,
literal|"127.0.0.1:0"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_HTTP_ADDRESS_KEY
argument_list|,
literal|"127.0.0.1:0"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMESERVICES
argument_list|,
literal|"ns1"
argument_list|)
expr_stmt|;
comment|// Set a nameservice-specific configuration for name dir
name|File
name|dir
init|=
operator|new
name|File
argument_list|(
name|MiniDFSCluster
operator|.
name|getBaseDirectory
argument_list|()
argument_list|,
literal|"testGenericKeysForNameNodeFormat"
argument_list|)
decl_stmt|;
if|if
condition|(
name|dir
operator|.
name|exists
argument_list|()
condition|)
block|{
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|dir
argument_list|)
expr_stmt|;
block|}
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_NAME_DIR_KEY
operator|+
literal|".ns1"
argument_list|,
name|dir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
comment|// Format and verify the right dir is formatted.
name|DFSTestUtil
operator|.
name|formatNameNode
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|assertExists
argument_list|(
name|dir
argument_list|)
expr_stmt|;
comment|// Ensure that the same dir is picked up by the running NN
name|NameNode
name|nameNode
init|=
operator|new
name|NameNode
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|nameNode
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

