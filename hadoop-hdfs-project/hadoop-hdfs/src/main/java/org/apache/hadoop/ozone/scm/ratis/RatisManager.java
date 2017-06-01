begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p/>  * http://www.apache.org/licenses/LICENSE-2.0  *<p/>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.scm.ratis
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|scm
operator|.
name|ratis
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
name|hdfs
operator|.
name|protocol
operator|.
name|DatanodeID
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
name|OzoneConfigKeys
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
name|OzoneConfiguration
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
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * Manage Ratis clusters.  */
end_comment

begin_interface
DECL|interface|RatisManager
specifier|public
interface|interface
name|RatisManager
block|{
comment|/**    * Create a new Ratis cluster with the given clusterId and datanodes.    */
DECL|method|createRatisCluster (String clusterId, List<DatanodeID> datanodes)
name|void
name|createRatisCluster
parameter_list|(
name|String
name|clusterId
parameter_list|,
name|List
argument_list|<
name|DatanodeID
argument_list|>
name|datanodes
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Close the Ratis cluster with the given clusterId.    */
DECL|method|closeRatisCluster (String clusterId)
name|void
name|closeRatisCluster
parameter_list|(
name|String
name|clusterId
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * @return the datanode list of the Ratis cluster with the given clusterId.    */
DECL|method|getDatanodes (String clusterId)
name|List
argument_list|<
name|DatanodeID
argument_list|>
name|getDatanodes
parameter_list|(
name|String
name|clusterId
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Update the datanode list of the Ratis cluster with the given clusterId.    */
DECL|method|updateDatanodes (String clusterId, List<DatanodeID> newDatanodes)
name|void
name|updateDatanodes
parameter_list|(
name|String
name|clusterId
parameter_list|,
name|List
argument_list|<
name|DatanodeID
argument_list|>
name|newDatanodes
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|newRatisManager (OzoneConfiguration conf)
specifier|static
name|RatisManager
name|newRatisManager
parameter_list|(
name|OzoneConfiguration
name|conf
parameter_list|)
block|{
specifier|final
name|String
name|rpc
init|=
name|conf
operator|.
name|get
argument_list|(
name|OzoneConfigKeys
operator|.
name|DFS_CONTAINER_RATIS_RPC_TYPE_KEY
argument_list|,
name|OzoneConfigKeys
operator|.
name|DFS_CONTAINER_RATIS_RPC_TYPE_DEFAULT
argument_list|)
decl_stmt|;
return|return
operator|new
name|RatisManagerImpl
argument_list|(
name|rpc
argument_list|)
return|;
block|}
block|}
end_interface

end_unit

