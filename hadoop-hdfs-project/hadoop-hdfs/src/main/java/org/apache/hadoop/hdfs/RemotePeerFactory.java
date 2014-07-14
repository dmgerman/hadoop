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
name|hdfs
operator|.
name|net
operator|.
name|Peer
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
name|hdfs
operator|.
name|security
operator|.
name|token
operator|.
name|block
operator|.
name|BlockTokenIdentifier
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
name|token
operator|.
name|Token
import|;
end_import

begin_interface
DECL|interface|RemotePeerFactory
specifier|public
interface|interface
name|RemotePeerFactory
block|{
comment|/**    * @param addr          The address to connect to.    * @param blockToken    Token used during optional SASL negotiation    * @param datanodeId    ID of destination DataNode    * @return              A new Peer connected to the address.    *    * @throws IOException  If there was an error connecting or creating     *                      the remote socket, encrypted stream, etc.    */
DECL|method|newConnectedPeer (InetSocketAddress addr, Token<BlockTokenIdentifier> blockToken, DatanodeID datanodeId)
name|Peer
name|newConnectedPeer
parameter_list|(
name|InetSocketAddress
name|addr
parameter_list|,
name|Token
argument_list|<
name|BlockTokenIdentifier
argument_list|>
name|blockToken
parameter_list|,
name|DatanodeID
name|datanodeId
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

