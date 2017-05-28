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
name|scm
operator|.
name|XceiverClientRatis
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
name|IOException
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
DECL|method|newXceiverClientRatis ( RpcType rpcType, Pipeline pipeline, OzoneConfiguration conf)
specifier|static
name|XceiverClientRatis
name|newXceiverClientRatis
parameter_list|(
name|RpcType
name|rpcType
parameter_list|,
name|Pipeline
name|pipeline
parameter_list|,
name|OzoneConfiguration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|initRatisConf
argument_list|(
name|rpcType
argument_list|,
name|conf
argument_list|)
expr_stmt|;
return|return
name|XceiverClientRatis
operator|.
name|newXceiverClientRatis
argument_list|(
name|pipeline
argument_list|,
name|conf
argument_list|)
return|;
block|}
block|}
end_interface

end_unit

