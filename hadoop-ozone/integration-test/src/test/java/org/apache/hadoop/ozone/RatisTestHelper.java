begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
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
name|hdds
operator|.
name|conf
operator|.
name|OzoneConfiguration
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
name|hdds
operator|.
name|scm
operator|.
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|Pipeline
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
name|ozone
operator|.
name|client
operator|.
name|protocol
operator|.
name|ClientProtocol
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
name|ozone
operator|.
name|client
operator|.
name|rpc
operator|.
name|RpcClient
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
name|hdds
operator|.
name|protocol
operator|.
name|DatanodeDetails
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
name|ozone
operator|.
name|client
operator|.
name|rest
operator|.
name|OzoneException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|RatisHelper
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|client
operator|.
name|RaftClient
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|protocol
operator|.
name|RaftPeer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|rpc
operator|.
name|RpcType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|rpc
operator|.
name|SupportedRpcType
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
name|java
operator|.
name|io
operator|.
name|Closeable
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
name|URISyntaxException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeoutException
import|;
end_import

begin_comment
comment|/**  * Helpers for Ratis tests.  */
end_comment

begin_interface
DECL|interface|RatisTestHelper
specifier|public
interface|interface
name|RatisTestHelper
block|{
DECL|field|LOG
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|RatisTestHelper
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/** For testing Ozone with Ratis. */
DECL|class|RatisTestSuite
class|class
name|RatisTestSuite
implements|implements
name|Closeable
block|{
DECL|field|RPC
specifier|static
specifier|final
name|RpcType
name|RPC
init|=
name|SupportedRpcType
operator|.
name|NETTY
decl_stmt|;
DECL|field|NUM_DATANODES
specifier|static
specifier|final
name|int
name|NUM_DATANODES
init|=
literal|3
decl_stmt|;
DECL|field|conf
specifier|private
specifier|final
name|OzoneConfiguration
name|conf
decl_stmt|;
DECL|field|cluster
specifier|private
specifier|final
name|MiniOzoneCluster
name|cluster
decl_stmt|;
comment|/**      * Create a {@link MiniOzoneCluster} for testing by setting.      *   OZONE_ENABLED = true      *   RATIS_ENABLED = true      */
DECL|method|RatisTestSuite ()
specifier|public
name|RatisTestSuite
parameter_list|()
throws|throws
name|IOException
throws|,
name|TimeoutException
throws|,
name|InterruptedException
block|{
name|conf
operator|=
name|newOzoneConfiguration
argument_list|(
name|RPC
argument_list|)
expr_stmt|;
name|cluster
operator|=
name|newMiniOzoneCluster
argument_list|(
name|NUM_DATANODES
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
DECL|method|getConf ()
specifier|public
name|OzoneConfiguration
name|getConf
parameter_list|()
block|{
return|return
name|conf
return|;
block|}
DECL|method|getCluster ()
specifier|public
name|MiniOzoneCluster
name|getCluster
parameter_list|()
block|{
return|return
name|cluster
return|;
block|}
DECL|method|newOzoneClient ()
specifier|public
name|ClientProtocol
name|newOzoneClient
parameter_list|()
throws|throws
name|OzoneException
throws|,
name|URISyntaxException
throws|,
name|IOException
block|{
return|return
operator|new
name|RpcClient
argument_list|(
name|conf
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
DECL|method|getDatanodeOzoneRestPort ()
specifier|public
name|int
name|getDatanodeOzoneRestPort
parameter_list|()
block|{
return|return
name|cluster
operator|.
name|getHddsDatanodes
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getDatanodeDetails
argument_list|()
operator|.
name|getPort
argument_list|(
name|DatanodeDetails
operator|.
name|Port
operator|.
name|Name
operator|.
name|REST
argument_list|)
operator|.
name|getValue
argument_list|()
return|;
block|}
block|}
DECL|method|newOzoneConfiguration (RpcType rpc)
specifier|static
name|OzoneConfiguration
name|newOzoneConfiguration
parameter_list|(
name|RpcType
name|rpc
parameter_list|)
block|{
specifier|final
name|OzoneConfiguration
name|conf
init|=
operator|new
name|OzoneConfiguration
argument_list|()
decl_stmt|;
name|initRatisConf
argument_list|(
name|rpc
argument_list|,
name|conf
argument_list|)
expr_stmt|;
return|return
name|conf
return|;
block|}
DECL|method|initRatisConf (RpcType rpc, Configuration conf)
specifier|static
name|void
name|initRatisConf
parameter_list|(
name|RpcType
name|rpc
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|conf
operator|.
name|setBoolean
argument_list|(
name|OzoneConfigKeys
operator|.
name|DFS_CONTAINER_RATIS_ENABLED_KEY
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|OzoneConfigKeys
operator|.
name|DFS_CONTAINER_RATIS_RPC_TYPE_KEY
argument_list|,
name|rpc
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|OzoneConfigKeys
operator|.
name|DFS_CONTAINER_RATIS_RPC_TYPE_KEY
operator|+
literal|" = "
operator|+
name|rpc
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|newMiniOzoneCluster ( int numDatanodes, OzoneConfiguration conf)
specifier|static
name|MiniOzoneCluster
name|newMiniOzoneCluster
parameter_list|(
name|int
name|numDatanodes
parameter_list|,
name|OzoneConfiguration
name|conf
parameter_list|)
throws|throws
name|IOException
throws|,
name|TimeoutException
throws|,
name|InterruptedException
block|{
specifier|final
name|MiniOzoneCluster
name|cluster
init|=
name|MiniOzoneCluster
operator|.
name|newBuilder
argument_list|(
name|conf
argument_list|)
operator|.
name|setNumDatanodes
argument_list|(
name|numDatanodes
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|cluster
operator|.
name|waitForClusterToBeReady
argument_list|()
expr_stmt|;
return|return
name|cluster
return|;
block|}
DECL|method|initXceiverServerRatis ( RpcType rpc, DatanodeDetails dd, Pipeline pipeline)
specifier|static
name|void
name|initXceiverServerRatis
parameter_list|(
name|RpcType
name|rpc
parameter_list|,
name|DatanodeDetails
name|dd
parameter_list|,
name|Pipeline
name|pipeline
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|RaftPeer
name|p
init|=
name|RatisHelper
operator|.
name|toRaftPeer
argument_list|(
name|dd
argument_list|)
decl_stmt|;
specifier|final
name|OzoneConfiguration
name|conf
init|=
operator|new
name|OzoneConfiguration
argument_list|()
decl_stmt|;
specifier|final
name|RaftClient
name|client
init|=
name|RatisHelper
operator|.
name|newRaftClient
argument_list|(
name|rpc
argument_list|,
name|p
argument_list|,
name|RatisHelper
operator|.
name|createRetryPolicy
argument_list|(
name|conf
argument_list|)
argument_list|)
decl_stmt|;
name|client
operator|.
name|groupAdd
argument_list|(
name|RatisHelper
operator|.
name|newRaftGroup
argument_list|(
name|pipeline
argument_list|)
argument_list|,
name|p
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_interface

end_unit

