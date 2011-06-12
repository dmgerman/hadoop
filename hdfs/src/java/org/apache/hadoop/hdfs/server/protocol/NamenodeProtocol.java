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
comment|/**    * Compared to the previous version the following changes have been introduced:    * (Only the latest change is reflected.    * The log of historical changes can be retrieved from the svn).    *     * 5: Added one parameter to rollFSImage() and    *    changed the definition of CheckpointSignature    */
DECL|field|versionID
specifier|public
specifier|static
specifier|final
name|long
name|versionID
init|=
literal|5L
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
comment|// Journal action codes. See journal().
DECL|field|JA_IS_ALIVE
specifier|public
specifier|static
name|byte
name|JA_IS_ALIVE
init|=
literal|100
decl_stmt|;
comment|// check whether the journal is alive
DECL|field|JA_JOURNAL
specifier|public
specifier|static
name|byte
name|JA_JOURNAL
init|=
literal|101
decl_stmt|;
comment|// just journal
DECL|field|JA_JSPOOL_START
specifier|public
specifier|static
name|byte
name|JA_JSPOOL_START
init|=
literal|102
decl_stmt|;
comment|// = FSEditLogOpCodes.OP_JSPOOL_START
DECL|field|JA_CHECKPOINT_TIME
specifier|public
specifier|static
name|byte
name|JA_CHECKPOINT_TIME
init|=
literal|103
decl_stmt|;
comment|// = FSEditLogOpCodes.OP_CHECKPOINT_TIME
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
comment|/**    * Get a list of blocks belonging to<code>datanode</code>    * whose total size equals<code>size</code>.    *     * @see org.apache.hadoop.hdfs.server.balancer.Balancer    * @param datanode  a data node    * @param size      requested size    * @return          a list of blocks& their locations    * @throws RemoteException if size is less than or equal to 0 or                                    datanode does not exist    */
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
comment|/**    * Get the size of the current edit log (in bytes).    * @return The number of bytes in the current edit log.    * @throws IOException    * @deprecated     *    See {@link org.apache.hadoop.hdfs.server.namenode.SecondaryNameNode}    */
annotation|@
name|Deprecated
DECL|method|getEditLogSize ()
specifier|public
name|long
name|getEditLogSize
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
comment|/**    * Rolls the fsImage log. It removes the old fsImage, copies the    * new image to fsImage, removes the old edits and renames edits.new     * to edits. The call fails if any of the four files are missing.    *     * @param sig the signature of this checkpoint (old fsimage)    * @throws IOException    * @deprecated     *    See {@link org.apache.hadoop.hdfs.server.namenode.SecondaryNameNode}    */
annotation|@
name|Deprecated
DECL|method|rollFsImage (CheckpointSignature sig)
specifier|public
name|void
name|rollFsImage
parameter_list|(
name|CheckpointSignature
name|sig
parameter_list|)
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
comment|/**    * Get the size of the active name-node journal (edit log) in bytes.    *     * @param registration the requesting node    * @return The number of bytes in the journal.    * @throws IOException    */
DECL|method|journalSize (NamenodeRegistration registration)
specifier|public
name|long
name|journalSize
parameter_list|(
name|NamenodeRegistration
name|registration
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Journal edit records.    * This message is sent by the active name-node to the backup node    * via {@code EditLogBackupOutputStream} in order to synchronize meta-data    * changes with the backup namespace image.    *     * @param registration active node registration    * @param jAction journal action    * @param length length of the byte array    * @param records byte array containing serialized journal records    * @throws IOException    */
DECL|method|journal (NamenodeRegistration registration, int jAction, int length, byte[] records)
specifier|public
name|void
name|journal
parameter_list|(
name|NamenodeRegistration
name|registration
parameter_list|,
name|int
name|jAction
parameter_list|,
name|int
name|length
parameter_list|,
name|byte
index|[]
name|records
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

