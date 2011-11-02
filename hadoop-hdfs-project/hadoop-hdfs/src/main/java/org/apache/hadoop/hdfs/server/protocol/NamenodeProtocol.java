begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.protocol
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
name|protocol
operator|.
name|DatanodeInfo
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
name|ExportedBlockKeys
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
name|server
operator|.
name|namenode
operator|.
name|CheckpointSignature
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
name|ipc
operator|.
name|VersionedProtocol
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

begin_comment
comment|/*****************************************************************************  * Protocol that a secondary NameNode uses to communicate with the NameNode.  * It's used to get part of the name node state  *****************************************************************************/
end_comment

begin_interface
annotation|@
name|KerberosInfo
argument_list|(
name|serverPrincipal
operator|=
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_USER_NAME_KEY
argument_list|,
name|clientPrincipal
operator|=
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_USER_NAME_KEY
argument_list|)
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|interface|NamenodeProtocol
specifier|public
interface|interface
name|NamenodeProtocol
extends|extends
name|VersionedProtocol
block|{
comment|/**    * Until version 6L, this class served as both    * the client interface to the NN AND the RPC protocol used to     * communicate with the NN.    *     * Post version 70 (release 23 of Hadoop), the protocol is implemented in    * {@literal ../protocolR23Compatible/ClientNamenodeWireProtocol}    *     * This class is used by both the DFSClient and the     * NN server side to insulate from the protocol serialization.    *     * If you are adding/changing NN's interface then you need to     * change both this class and ALSO    * {@link org.apache.hadoop.hdfs.protocolR23Compatible.NamenodeWireProtocol}.    * These changes need to be done in a compatible fashion as described in     * {@link org.apache.hadoop.hdfs.protocolR23Compatible.ClientNamenodeWireProtocol}    *     * 6: Switch to txid-based file naming for image and edits    */
DECL|field|versionID
specifier|public
specifier|static
specifier|final
name|long
name|versionID
init|=
literal|6L
decl_stmt|;
comment|// Error codes passed by errorReport().
DECL|field|NOTIFY
specifier|final
specifier|static
name|int
name|NOTIFY
init|=
literal|0
decl_stmt|;
DECL|field|FATAL
specifier|final
specifier|static
name|int
name|FATAL
init|=
literal|1
decl_stmt|;
DECL|field|ACT_UNKNOWN
specifier|public
specifier|final
specifier|static
name|int
name|ACT_UNKNOWN
init|=
literal|0
decl_stmt|;
comment|// unknown action
DECL|field|ACT_SHUTDOWN
specifier|public
specifier|final
specifier|static
name|int
name|ACT_SHUTDOWN
init|=
literal|50
decl_stmt|;
comment|// shutdown node
DECL|field|ACT_CHECKPOINT
specifier|public
specifier|final
specifier|static
name|int
name|ACT_CHECKPOINT
init|=
literal|51
decl_stmt|;
comment|// do checkpoint
comment|/**    * Get a list of blocks belonging to<code>datanode</code>    * whose total size equals<code>size</code>.    *     * @see org.apache.hadoop.hdfs.server.balancer.Balancer    * @param datanode  a data node    * @param size      requested size    * @return          a list of blocks& their locations    * @throws IOException if size is less than or equal to 0 or                                    datanode does not exist    */
DECL|method|getBlocks (DatanodeInfo datanode, long size)
specifier|public
name|BlocksWithLocations
name|getBlocks
parameter_list|(
name|DatanodeInfo
name|datanode
parameter_list|,
name|long
name|size
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Get the current block keys    *     * @return ExportedBlockKeys containing current block keys    * @throws IOException     */
DECL|method|getBlockKeys ()
specifier|public
name|ExportedBlockKeys
name|getBlockKeys
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * @return The most recent transaction ID that has been synced to    * persistent storage.    * @throws IOException    */
DECL|method|getTransactionID ()
specifier|public
name|long
name|getTransactionID
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Closes the current edit log and opens a new one. The     * call fails if the file system is in SafeMode.    * @throws IOException    * @return a unique token to identify this transaction.    * @deprecated     *    See {@link org.apache.hadoop.hdfs.server.namenode.SecondaryNameNode}    */
annotation|@
name|Deprecated
DECL|method|rollEditLog ()
specifier|public
name|CheckpointSignature
name|rollEditLog
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Request name-node version and storage information.    *     * @return {@link NamespaceInfo} identifying versions and storage information     *          of the name-node    * @throws IOException    */
DECL|method|versionRequest ()
specifier|public
name|NamespaceInfo
name|versionRequest
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Report to the active name-node an error occurred on a subordinate node.    * Depending on the error code the active node may decide to unregister the    * reporting node.    *     * @param registration requesting node.    * @param errorCode indicates the error    * @param msg free text description of the error    * @throws IOException    */
DECL|method|errorReport (NamenodeRegistration registration, int errorCode, String msg)
specifier|public
name|void
name|errorReport
parameter_list|(
name|NamenodeRegistration
name|registration
parameter_list|,
name|int
name|errorCode
parameter_list|,
name|String
name|msg
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**     * Register a subordinate name-node like backup node.    *    * @return  {@link NamenodeRegistration} of the node,    *          which this node has just registered with.    */
DECL|method|register (NamenodeRegistration registration)
specifier|public
name|NamenodeRegistration
name|register
parameter_list|(
name|NamenodeRegistration
name|registration
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * A request to the active name-node to start a checkpoint.    * The name-node should decide whether to admit it or reject.    * The name-node also decides what should be done with the backup node    * image before and after the checkpoint.    *     * @see CheckpointCommand    * @see NamenodeCommand    * @see #ACT_SHUTDOWN    *     * @param registration the requesting node    * @return {@link CheckpointCommand} if checkpoint is allowed.    * @throws IOException    */
DECL|method|startCheckpoint (NamenodeRegistration registration)
specifier|public
name|NamenodeCommand
name|startCheckpoint
parameter_list|(
name|NamenodeRegistration
name|registration
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * A request to the active name-node to finalize    * previously started checkpoint.    *     * @param registration the requesting node    * @param sig {@code CheckpointSignature} which identifies the checkpoint.    * @throws IOException    */
DECL|method|endCheckpoint (NamenodeRegistration registration, CheckpointSignature sig)
specifier|public
name|void
name|endCheckpoint
parameter_list|(
name|NamenodeRegistration
name|registration
parameter_list|,
name|CheckpointSignature
name|sig
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Return a structure containing details about all edit logs    * available to be fetched from the NameNode.    * @param sinceTxId return only logs that contain transactions>= sinceTxId    */
DECL|method|getEditLogManifest (long sinceTxId)
specifier|public
name|RemoteEditLogManifest
name|getEditLogManifest
parameter_list|(
name|long
name|sinceTxId
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

