begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode.ha
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
operator|.
name|ha
package|;
end_package

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
name|hdfs
operator|.
name|MiniDFSNNTopology
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
name|java
operator|.
name|util
operator|.
name|List
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
name|assertEquals
import|;
end_import

begin_comment
comment|/**  * Test that we correctly obtain remote namenode information  */
end_comment

begin_class
DECL|class|TestRemoteNameNodeInfo
specifier|public
class|class
name|TestRemoteNameNodeInfo
block|{
annotation|@
name|Test
DECL|method|testParseMultipleNameNodes ()
specifier|public
name|void
name|testParseMultipleNameNodes
parameter_list|()
throws|throws
name|Exception
block|{
comment|// start with an empty configuration
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
decl_stmt|;
comment|// add in keys for each of the NNs
name|String
name|nameservice
init|=
literal|"ns1"
decl_stmt|;
name|MiniDFSNNTopology
name|topology
init|=
operator|new
name|MiniDFSNNTopology
argument_list|()
operator|.
name|addNameservice
argument_list|(
operator|new
name|MiniDFSNNTopology
operator|.
name|NSConf
argument_list|(
name|nameservice
argument_list|)
operator|.
name|addNN
argument_list|(
operator|new
name|MiniDFSNNTopology
operator|.
name|NNConf
argument_list|(
literal|"nn1"
argument_list|)
operator|.
name|setIpcPort
argument_list|(
literal|10001
argument_list|)
argument_list|)
operator|.
name|addNN
argument_list|(
operator|new
name|MiniDFSNNTopology
operator|.
name|NNConf
argument_list|(
literal|"nn2"
argument_list|)
operator|.
name|setIpcPort
argument_list|(
literal|10002
argument_list|)
argument_list|)
operator|.
name|addNN
argument_list|(
operator|new
name|MiniDFSNNTopology
operator|.
name|NNConf
argument_list|(
literal|"nn3"
argument_list|)
operator|.
name|setIpcPort
argument_list|(
literal|10003
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
comment|// add the configurations of the NNs to the passed conf, so we can parse it back out
name|MiniDFSCluster
operator|.
name|configureNameNodes
argument_list|(
name|topology
argument_list|,
literal|false
argument_list|,
name|conf
argument_list|)
expr_stmt|;
comment|// set the 'local' one as nn1
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_HA_NAMENODE_ID_KEY
argument_list|,
literal|"nn1"
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|RemoteNameNodeInfo
argument_list|>
name|nns
init|=
name|RemoteNameNodeInfo
operator|.
name|getRemoteNameNodes
argument_list|(
name|conf
argument_list|)
decl_stmt|;
comment|// make sure it matches when we pass in the nameservice
name|List
argument_list|<
name|RemoteNameNodeInfo
argument_list|>
name|nns2
init|=
name|RemoteNameNodeInfo
operator|.
name|getRemoteNameNodes
argument_list|(
name|conf
argument_list|,
name|nameservice
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|nns
argument_list|,
name|nns2
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

