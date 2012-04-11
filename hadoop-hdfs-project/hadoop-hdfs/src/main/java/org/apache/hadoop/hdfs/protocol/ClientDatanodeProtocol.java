begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.protocol
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|classification
operator|.
name|InterfaceAudience
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
name|classification
operator|.
name|InterfaceStability
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
name|hdfs
operator|.
name|security
operator|.
name|token
operator|.
name|block
operator|.
name|BlockTokenSelector
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
name|KerberosInfo
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
name|TokenInfo
import|;
end_import

begin_comment
comment|/** An client-datanode protocol for block recovery  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
annotation|@
name|KerberosInfo
argument_list|(
name|serverPrincipal
operator|=
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_USER_NAME_KEY
argument_list|)
annotation|@
name|TokenInfo
argument_list|(
name|BlockTokenSelector
operator|.
name|class
argument_list|)
DECL|interface|ClientDatanodeProtocol
specifier|public
interface|interface
name|ClientDatanodeProtocol
block|{
comment|/**    * Until version 9, this class ClientDatanodeProtocol served as both    * the client interface to the DN AND the RPC protocol used to     * communicate with the NN.    *     * This class is used by both the DFSClient and the     * DN server side to insulate from the protocol serialization.    *     * If you are adding/changing DN's interface then you need to     * change both this class and ALSO related protocol buffer    * wire protocol definition in ClientDatanodeProtocol.proto.    *     * For more details on protocol buffer wire protocol, please see     * .../org/apache/hadoop/hdfs/protocolPB/overview.html    *     * The log of historical changes can be retrieved from the svn).    * 9: Added deleteBlockPool method    *     * 9 is the last version id when this class was used for protocols    *  serialization. DO not update this version any further.     */
DECL|field|versionID
specifier|public
specifier|static
specifier|final
name|long
name|versionID
init|=
literal|9L
decl_stmt|;
comment|/** Return the visible length of a replica. */
DECL|method|getReplicaVisibleLength (ExtendedBlock b)
name|long
name|getReplicaVisibleLength
parameter_list|(
name|ExtendedBlock
name|b
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Refresh the list of federated namenodes from updated configuration    * Adds new namenodes and stops the deleted namenodes.    *     * @throws IOException on error    **/
DECL|method|refreshNamenodes ()
name|void
name|refreshNamenodes
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Delete the block pool directory. If force is false it is deleted only if    * it is empty, otherwise it is deleted along with its contents.    *     * @param bpid Blockpool id to be deleted.    * @param force If false blockpool directory is deleted only if it is empty     *          i.e. if it doesn't contain any block files, otherwise it is     *          deleted along with its contents.    * @throws IOException    */
DECL|method|deleteBlockPool (String bpid, boolean force)
name|void
name|deleteBlockPool
parameter_list|(
name|String
name|bpid
parameter_list|,
name|boolean
name|force
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Retrieves the path names of the block file and metadata file stored on the    * local file system.    *     * In order for this method to work, one of the following should be satisfied:    *<ul>    *<li>    * The client user must be configured at the datanode to be able to use this    * method.</li>    *<li>    * When security is enabled, kerberos authentication must be used to connect    * to the datanode.</li>    *</ul>    *     * @param block    *          the specified block on the local datanode    * @param token    *          the block access token.    * @return the BlockLocalPathInfo of a block    * @throws IOException    *           on error    */
DECL|method|getBlockLocalPathInfo (ExtendedBlock block, Token<BlockTokenIdentifier> token)
name|BlockLocalPathInfo
name|getBlockLocalPathInfo
parameter_list|(
name|ExtendedBlock
name|block
parameter_list|,
name|Token
argument_list|<
name|BlockTokenIdentifier
argument_list|>
name|token
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

