begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.protocolR23Compatible
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|protocolR23Compatible
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
name|server
operator|.
name|protocol
operator|.
name|CheckpointCommand
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
name|protocol
operator|.
name|NamenodeRegistration
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
name|RemoteException
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

begin_comment
comment|/**   * This class defines the actual protocol used to communicate between namenodes.  * The parameters in the methods which are specified in the  * package are separate from those used internally in the DN and DFSClient  * and hence need to be converted using {@link NamenodeProtocolTranslatorR23}  * and {@link NamenodeProtocolServerSideTranslatorR23}.  */
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
DECL|interface|NamenodeWireProtocol
specifier|public
interface|interface
name|NamenodeWireProtocol
extends|extends
name|VersionedProtocol
block|{
comment|/**    * The  rules for changing this protocol are the same as that for    * {@link ClientNamenodeWireProtocol} - see that java file for details.    */
DECL|field|versionID
specifier|public
specifier|static
specifier|final
name|long
name|versionID
init|=
literal|6L
decl_stmt|;
comment|/**    * Get a list of blocks belonging to<code>datanode</code>    * whose total size equals<code>size</code>.    *     * @see org.apache.hadoop.hdfs.server.balancer.Balancer    * @param datanode  a data node    * @param size      requested size    * @return          a list of blocks& their locations    * @throws RemoteException if size is less than or equal to 0 or    *                               datanode does not exist    */
DECL|method|getBlocks (DatanodeInfoWritable datanode, long size)
specifier|public
name|BlocksWithLocationsWritable
name|getBlocks
parameter_list|(
name|DatanodeInfoWritable
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
name|ExportedBlockKeysWritable
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
name|CheckpointSignatureWritable
name|rollEditLog
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Request name-node version and storage information.    * @throws IOException    */
DECL|method|versionRequest ()
specifier|public
name|NamespaceInfoWritable
name|versionRequest
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Report to the active name-node an error occurred on a subordinate node.    * Depending on the error code the active node may decide to unregister the    * reporting node.    *     * @param registration requesting node.    * @param errorCode indicates the error    * @param msg free text description of the error    * @throws IOException    */
DECL|method|errorReport (NamenodeRegistrationWritable registration, int errorCode, String msg)
specifier|public
name|void
name|errorReport
parameter_list|(
name|NamenodeRegistrationWritable
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
DECL|method|register ( NamenodeRegistrationWritable registration)
specifier|public
name|NamenodeRegistrationWritable
name|register
parameter_list|(
name|NamenodeRegistrationWritable
name|registration
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * A request to the active name-node to start a checkpoint.    * The name-node should decide whether to admit it or reject.    * The name-node also decides what should be done with the backup node    * image before and after the checkpoint.    *     * @see CheckpointCommand    * @see NamenodeCommandWritable    * @see #ACT_SHUTDOWN    *     * @param registration the requesting node    * @return {@link CheckpointCommand} if checkpoint is allowed.    * @throws IOException    */
DECL|method|startCheckpoint ( NamenodeRegistrationWritable registration)
specifier|public
name|NamenodeCommandWritable
name|startCheckpoint
parameter_list|(
name|NamenodeRegistrationWritable
name|registration
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * A request to the active name-node to finalize    * previously started checkpoint.    *     * @param registration the requesting node    * @param sig {@code CheckpointSignature} which identifies the checkpoint.    * @throws IOException    */
DECL|method|endCheckpoint (NamenodeRegistrationWritable registration, CheckpointSignatureWritable sig)
specifier|public
name|void
name|endCheckpoint
parameter_list|(
name|NamenodeRegistrationWritable
name|registration
parameter_list|,
name|CheckpointSignatureWritable
name|sig
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Return a structure containing details about all edit logs    * available to be fetched from the NameNode.    * @param sinceTxId return only logs that contain transactions>= sinceTxId    */
DECL|method|getEditLogManifest (long sinceTxId)
specifier|public
name|RemoteEditLogManifestWritable
name|getEditLogManifest
parameter_list|(
name|long
name|sinceTxId
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * This method is defined to get the protocol signature using     * the R23 protocol - hence we have added the suffix of 2 the method name    * to avoid conflict.    */
specifier|public
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|protocolR23Compatible
operator|.
name|ProtocolSignatureWritable
DECL|method|getProtocolSignature2 (String protocol, long clientVersion, int clientMethodsHash)
name|getProtocolSignature2
parameter_list|(
name|String
name|protocol
parameter_list|,
name|long
name|clientVersion
parameter_list|,
name|int
name|clientMethodsHash
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

