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
name|*
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
name|ExtendedBlock
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
name|protocol
operator|.
name|LocatedBlock
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
comment|/**********************************************************************  * Protocol that a DFS datanode uses to communicate with the NameNode.  * It's used to upload current load information and block reports.  *  * The only way a NameNode can communicate with a DataNode is by  * returning values from these functions.  *  **********************************************************************/
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
name|DFS_DATANODE_USER_NAME_KEY
argument_list|)
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|interface|DatanodeProtocol
specifier|public
interface|interface
name|DatanodeProtocol
extends|extends
name|VersionedProtocol
block|{
comment|/**    * This class is used by both the Namenode (client) and BackupNode (server)     * to insulate from the protocol serialization.    *     * If you are adding/changing DN's interface then you need to     * change both this class and ALSO related protocol buffer    * wire protocol definition in DatanodeProtocol.proto.    *     * For more details on protocol buffer wire protocol, please see     * .../org/apache/hadoop/hdfs/protocolPB/overview.html    */
DECL|field|versionID
specifier|public
specifier|static
specifier|final
name|long
name|versionID
init|=
literal|28L
decl_stmt|;
comment|// error code
DECL|field|NOTIFY
specifier|final
specifier|static
name|int
name|NOTIFY
init|=
literal|0
decl_stmt|;
DECL|field|DISK_ERROR
specifier|final
specifier|static
name|int
name|DISK_ERROR
init|=
literal|1
decl_stmt|;
comment|// there are still valid volumes on DN
DECL|field|INVALID_BLOCK
specifier|final
specifier|static
name|int
name|INVALID_BLOCK
init|=
literal|2
decl_stmt|;
DECL|field|FATAL_DISK_ERROR
specifier|final
specifier|static
name|int
name|FATAL_DISK_ERROR
init|=
literal|3
decl_stmt|;
comment|// no valid volumes left on DN
comment|/**    * Determines actions that data node should perform     * when receiving a datanode command.     */
DECL|field|DNA_UNKNOWN
specifier|final
specifier|static
name|int
name|DNA_UNKNOWN
init|=
literal|0
decl_stmt|;
comment|// unknown action
DECL|field|DNA_TRANSFER
specifier|final
specifier|static
name|int
name|DNA_TRANSFER
init|=
literal|1
decl_stmt|;
comment|// transfer blocks to another datanode
DECL|field|DNA_INVALIDATE
specifier|final
specifier|static
name|int
name|DNA_INVALIDATE
init|=
literal|2
decl_stmt|;
comment|// invalidate blocks
DECL|field|DNA_SHUTDOWN
specifier|final
specifier|static
name|int
name|DNA_SHUTDOWN
init|=
literal|3
decl_stmt|;
comment|// shutdown node
DECL|field|DNA_REGISTER
specifier|final
specifier|static
name|int
name|DNA_REGISTER
init|=
literal|4
decl_stmt|;
comment|// re-register
DECL|field|DNA_FINALIZE
specifier|final
specifier|static
name|int
name|DNA_FINALIZE
init|=
literal|5
decl_stmt|;
comment|// finalize previous upgrade
DECL|field|DNA_RECOVERBLOCK
specifier|final
specifier|static
name|int
name|DNA_RECOVERBLOCK
init|=
literal|6
decl_stmt|;
comment|// request a block recovery
DECL|field|DNA_ACCESSKEYUPDATE
specifier|final
specifier|static
name|int
name|DNA_ACCESSKEYUPDATE
init|=
literal|7
decl_stmt|;
comment|// update access key
DECL|field|DNA_BALANCERBANDWIDTHUPDATE
specifier|final
specifier|static
name|int
name|DNA_BALANCERBANDWIDTHUPDATE
init|=
literal|8
decl_stmt|;
comment|// update balancer bandwidth
DECL|field|DNA_UC_ACTION_REPORT_STATUS
specifier|final
specifier|static
name|int
name|DNA_UC_ACTION_REPORT_STATUS
init|=
literal|100
decl_stmt|;
comment|// Report upgrade status
DECL|field|DNA_UC_ACTION_START_UPGRADE
specifier|final
specifier|static
name|int
name|DNA_UC_ACTION_START_UPGRADE
init|=
literal|101
decl_stmt|;
comment|// start upgrade
comment|/**     * Register Datanode.    *    * @see org.apache.hadoop.hdfs.server.namenode.FSNamesystem#registerDatanode(DatanodeRegistration)    * @param registration datanode registration information    * @param storages list of storages on the datanode``    * @return updated {@link org.apache.hadoop.hdfs.server.protocol.DatanodeRegistration}, which contains     * new storageID if the datanode did not have one and    * registration ID for further communication.    */
DECL|method|registerDatanode ( DatanodeRegistration registration, DatanodeStorage[] storages)
specifier|public
name|DatanodeRegistration
name|registerDatanode
parameter_list|(
name|DatanodeRegistration
name|registration
parameter_list|,
name|DatanodeStorage
index|[]
name|storages
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * sendHeartbeat() tells the NameNode that the DataNode is still    * alive and well.  Includes some status info, too.     * It also gives the NameNode a chance to return     * an array of "DatanodeCommand" objects.    * A DatanodeCommand tells the DataNode to invalidate local block(s),     * or to copy them to other DataNodes, etc.    * @param registration datanode registration information    * @param reports utilization report per storage    * @param xmitsInProgress number of transfers from this datanode to others    * @param xceiverCount number of active transceiver threads    * @param failedVolumes number of failed volumes    * @throws IOException on error    */
DECL|method|sendHeartbeat (DatanodeRegistration registration, StorageReport[] reports, int xmitsInProgress, int xceiverCount, int failedVolumes)
specifier|public
name|DatanodeCommand
index|[]
name|sendHeartbeat
parameter_list|(
name|DatanodeRegistration
name|registration
parameter_list|,
name|StorageReport
index|[]
name|reports
parameter_list|,
name|int
name|xmitsInProgress
parameter_list|,
name|int
name|xceiverCount
parameter_list|,
name|int
name|failedVolumes
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * blockReport() tells the NameNode about all the locally-stored blocks.    * The NameNode returns an array of Blocks that have become obsolete    * and should be deleted.  This function is meant to upload *all*    * the locally-stored blocks.  It's invoked upon startup and then    * infrequently afterwards.    * @param registration    * @param poolId - the block pool ID for the blocks    * @param reports - report of blocks per storage    *     Each block is represented as 2 longs.    *     This is done instead of Block[] to reduce memory used by block reports.    *         * @return - the next command for DN to process.    * @throws IOException    */
DECL|method|blockReport (DatanodeRegistration registration, String poolId, StorageBlockReport[] reports)
specifier|public
name|DatanodeCommand
name|blockReport
parameter_list|(
name|DatanodeRegistration
name|registration
parameter_list|,
name|String
name|poolId
parameter_list|,
name|StorageBlockReport
index|[]
name|reports
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * blockReceivedAndDeleted() allows the DataNode to tell the NameNode about    * recently-received and -deleted block data.     *     * For the case of received blocks, a hint for preferred replica to be     * deleted when there is any excessive blocks is provided.    * For example, whenever client code    * writes a new Block here, or another DataNode copies a Block to    * this DataNode, it will call blockReceived().    */
DECL|method|blockReceivedAndDeleted (DatanodeRegistration registration, String poolId, StorageReceivedDeletedBlocks[] rcvdAndDeletedBlocks)
specifier|public
name|void
name|blockReceivedAndDeleted
parameter_list|(
name|DatanodeRegistration
name|registration
parameter_list|,
name|String
name|poolId
parameter_list|,
name|StorageReceivedDeletedBlocks
index|[]
name|rcvdAndDeletedBlocks
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * errorReport() tells the NameNode about something that has gone    * awry.  Useful for debugging.    */
DECL|method|errorReport (DatanodeRegistration registration, int errorCode, String msg)
specifier|public
name|void
name|errorReport
parameter_list|(
name|DatanodeRegistration
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
DECL|method|versionRequest ()
specifier|public
name|NamespaceInfo
name|versionRequest
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * This is a very general way to send a command to the name-node during    * distributed upgrade process.    *     * The generosity is because the variety of upgrade commands is unpredictable.    * The reply from the name-node is also received in the form of an upgrade     * command.     *     * @return a reply in the form of an upgrade command    */
DECL|method|processUpgradeCommand (UpgradeCommand comm)
name|UpgradeCommand
name|processUpgradeCommand
parameter_list|(
name|UpgradeCommand
name|comm
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * same as {@link org.apache.hadoop.hdfs.protocol.ClientProtocol#reportBadBlocks(LocatedBlock[])}    * }    */
DECL|method|reportBadBlocks (LocatedBlock[] blocks)
specifier|public
name|void
name|reportBadBlocks
parameter_list|(
name|LocatedBlock
index|[]
name|blocks
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Commit block synchronization in lease recovery    */
DECL|method|commitBlockSynchronization (ExtendedBlock block, long newgenerationstamp, long newlength, boolean closeFile, boolean deleteblock, DatanodeID[] newtargets )
specifier|public
name|void
name|commitBlockSynchronization
parameter_list|(
name|ExtendedBlock
name|block
parameter_list|,
name|long
name|newgenerationstamp
parameter_list|,
name|long
name|newlength
parameter_list|,
name|boolean
name|closeFile
parameter_list|,
name|boolean
name|deleteblock
parameter_list|,
name|DatanodeID
index|[]
name|newtargets
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

