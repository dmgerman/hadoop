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
name|ozone
operator|.
name|container
operator|.
name|ContainerTestHelper
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
name|web
operator|.
name|client
operator|.
name|OzoneRestClient
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
comment|/**      * Create a {@link MiniOzoneCluster} for testing by setting      *   OZONE_ENABLED = true,      *   RATIS_ENABLED = true, and      *   OZONE_HANDLER_TYPE_KEY = "distributed".      */
DECL|method|RatisTestSuite (final Class<?> clazz)
specifier|public
name|RatisTestSuite
parameter_list|(
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
parameter_list|)
throws|throws
name|IOException
block|{
name|conf
operator|=
name|newOzoneConfiguration
argument_list|(
name|clazz
argument_list|,
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
DECL|method|newOzoneRestClient ()
specifier|public
name|OzoneRestClient
name|newOzoneRestClient
parameter_list|()
throws|throws
name|OzoneException
throws|,
name|URISyntaxException
block|{
return|return
name|RatisTestHelper
operator|.
name|newOzoneRestClient
argument_list|(
name|getDatanodeOzoneRestPort
argument_list|()
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
name|getOzoneRestPort
argument_list|()
return|;
block|}
block|}
DECL|method|newOzoneConfiguration ( Class<?> clazz, RpcType rpc)
specifier|static
name|OzoneConfiguration
name|newOzoneConfiguration
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
parameter_list|,
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
name|ContainerTestHelper
operator|.
name|setOzoneLocalStorageRoot
argument_list|(
name|clazz
argument_list|,
name|conf
argument_list|)
expr_stmt|;
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
return|return
name|cluster
return|;
block|}
DECL|method|newOzoneRestClient (int port)
specifier|static
name|OzoneRestClient
name|newOzoneRestClient
parameter_list|(
name|int
name|port
parameter_list|)
throws|throws
name|OzoneException
throws|,
name|URISyntaxException
block|{
return|return
operator|new
name|OzoneRestClient
argument_list|(
literal|"http://localhost:"
operator|+
name|port
argument_list|)
return|;
block|}
block|}
end_interface

end_unit

