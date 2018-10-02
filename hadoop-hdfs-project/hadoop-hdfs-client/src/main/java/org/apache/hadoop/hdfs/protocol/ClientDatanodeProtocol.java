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
name|java
operator|.
name|util
operator|.
name|List
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
name|conf
operator|.
name|ReconfigurationTaskStatus
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
name|client
operator|.
name|BlockReportOptions
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
name|client
operator|.
name|HdfsClientConfigKeys
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
name|datanode
operator|.
name|DiskBalancerWorkStatus
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
name|HdfsClientConfigKeys
operator|.
name|DFS_DATANODE_KERBEROS_PRINCIPAL_KEY
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
comment|/**    * Until version 9, this class ClientDatanodeProtocol served as both    * the client interface to the DN AND the RPC protocol used to    * communicate with the NN.    *    * This class is used by both the DFSClient and the    * DN server side to insulate from the protocol serialization.    *    * If you are adding/changing DN's interface then you need to    * change both this class and ALSO related protocol buffer    * wire protocol definition in ClientDatanodeProtocol.proto.    *    * For more details on protocol buffer wire protocol, please see    * .../org/apache/hadoop/hdfs/protocolPB/overview.html    *    * The log of historical changes can be retrieved from the svn).    * 9: Added deleteBlockPool method    *    * 9 is the last version id when this class was used for protocols    *  serialization. DO not update this version any further.    */
DECL|field|versionID
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
comment|/**    * Refresh the list of federated namenodes from updated configuration    * Adds new namenodes and stops the deleted namenodes.    *    * @throws IOException on error    **/
DECL|method|refreshNamenodes ()
name|void
name|refreshNamenodes
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Delete the block pool directory. If force is false it is deleted only if    * it is empty, otherwise it is deleted along with its contents.    *    * @param bpid Blockpool id to be deleted.    * @param force If false blockpool directory is deleted only if it is empty    *          i.e. if it doesn't contain any block files, otherwise it is    *          deleted along with its contents.    * @throws IOException    */
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
comment|/**    * Retrieves the path names of the block file and metadata file stored on the    * local file system.    *    * In order for this method to work, one of the following should be satisfied:    *<ul>    *<li>    * The client user must be configured at the datanode to be able to use this    * method.</li>    *<li>    * When security is enabled, kerberos authentication must be used to connect    * to the datanode.</li>    *</ul>    *    * @param block    *          the specified block on the local datanode    * @param token    *          the block access token.    * @return the BlockLocalPathInfo of a block    * @throws IOException    *           on error    */
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
comment|/**    * Shuts down a datanode.    *    * @param forUpgrade If true, data node does extra prep work before shutting    *          down. The work includes advising clients to wait and saving    *          certain states for quick restart. This should only be used when    *          the stored data will remain the same during upgrade/restart.    * @throws IOException    */
DECL|method|shutdownDatanode (boolean forUpgrade)
name|void
name|shutdownDatanode
parameter_list|(
name|boolean
name|forUpgrade
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Evict clients that are writing to a datanode.    *    * @throws IOException    */
DECL|method|evictWriters ()
name|void
name|evictWriters
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Obtains datanode info    *    * @return software/config version and uptime of the datanode    */
DECL|method|getDatanodeInfo ()
name|DatanodeLocalInfo
name|getDatanodeInfo
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Asynchronously reload configuration on disk and apply changes.    */
DECL|method|startReconfiguration ()
name|void
name|startReconfiguration
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Get the status of the previously issued reconfig task.    * @see org.apache.hadoop.conf.ReconfigurationTaskStatus    */
DECL|method|getReconfigurationStatus ()
name|ReconfigurationTaskStatus
name|getReconfigurationStatus
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Get a list of allowed properties for reconfiguration.    */
DECL|method|listReconfigurableProperties ()
name|List
argument_list|<
name|String
argument_list|>
name|listReconfigurableProperties
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Trigger a new block report.    */
DECL|method|triggerBlockReport (BlockReportOptions options)
name|void
name|triggerBlockReport
parameter_list|(
name|BlockReportOptions
name|options
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Get current value of the balancer bandwidth in bytes per second.    *    * @return balancer bandwidth    */
DECL|method|getBalancerBandwidth ()
name|long
name|getBalancerBandwidth
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Get volume report of datanode.    */
DECL|method|getVolumeReport ()
name|List
argument_list|<
name|DatanodeVolumeInfo
argument_list|>
name|getVolumeReport
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Submit a disk balancer plan for execution.    */
DECL|method|submitDiskBalancerPlan (String planID, long planVersion, String planFile, String planData, boolean skipDateCheck)
name|void
name|submitDiskBalancerPlan
parameter_list|(
name|String
name|planID
parameter_list|,
name|long
name|planVersion
parameter_list|,
name|String
name|planFile
parameter_list|,
name|String
name|planData
parameter_list|,
name|boolean
name|skipDateCheck
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Cancel an executing plan.    *    * @param planID - A SHA-1 hash of the plan string.    */
DECL|method|cancelDiskBalancePlan (String planID)
name|void
name|cancelDiskBalancePlan
parameter_list|(
name|String
name|planID
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Gets the status of an executing diskbalancer Plan.    */
DECL|method|queryDiskBalancerPlan ()
name|DiskBalancerWorkStatus
name|queryDiskBalancerPlan
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Gets a run-time configuration value from running diskbalancer instance.    * For example : Disk Balancer bandwidth of a running instance.    *    * @param key runtime configuration key    * @return value of the key as a string.    * @throws IOException - Throws if there is no such key    */
DECL|method|getDiskBalancerSetting (String key)
name|String
name|getDiskBalancerSetting
parameter_list|(
name|String
name|key
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

