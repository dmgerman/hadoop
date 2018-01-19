begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode.sps
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
name|namenode
operator|.
name|sps
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
name|fs
operator|.
name|ParentNotDirectoryException
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
name|fs
operator|.
name|StorageType
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
name|fs
operator|.
name|UnresolvedLinkException
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
name|BlockStoragePolicy
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
name|protocol
operator|.
name|HdfsFileStatus
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
name|DatanodeStorageReport
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
name|net
operator|.
name|NetworkTopology
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
name|AccessControlException
import|;
end_import

begin_comment
comment|/**  * An interface for the communication between NameNode and SPS module.  */
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
DECL|interface|Context
specifier|public
interface|interface
name|Context
block|{
comment|/**    * Returns true if the SPS is running, false otherwise.    */
DECL|method|isRunning ()
name|boolean
name|isRunning
parameter_list|()
function_decl|;
comment|/**    * Returns true if the Namenode in safe mode, false otherwise.    */
DECL|method|isInSafeMode ()
name|boolean
name|isInSafeMode
parameter_list|()
function_decl|;
comment|/**    * Returns true if Mover tool is already running, false otherwise.    */
DECL|method|isMoverRunning ()
name|boolean
name|isMoverRunning
parameter_list|()
function_decl|;
comment|/**    * Gets the Inode ID number for the given path.    *    * @param path    *          - file/dir path    * @return Inode id number    */
DECL|method|getFileID (String path)
name|long
name|getFileID
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|UnresolvedLinkException
throws|,
name|AccessControlException
throws|,
name|ParentNotDirectoryException
function_decl|;
comment|/**    * Gets the network topology.    *    * @return network topology    */
DECL|method|getNetworkTopology ()
name|NetworkTopology
name|getNetworkTopology
parameter_list|()
function_decl|;
comment|/**    * Returns true if the give Inode exists in the Namespace.    *    * @param inodeId    *          - Inode ID    * @return true if Inode exists, false otherwise.    */
DECL|method|isFileExist (long inodeId)
name|boolean
name|isFileExist
parameter_list|(
name|long
name|inodeId
parameter_list|)
function_decl|;
comment|/**    * Gets the storage policy details for the given policy ID.    *    * @param policyId    *          - Storage policy ID    * @return the detailed policy object    */
DECL|method|getStoragePolicy (byte policyId)
name|BlockStoragePolicy
name|getStoragePolicy
parameter_list|(
name|byte
name|policyId
parameter_list|)
function_decl|;
comment|/**    * Drop the SPS work in case if any previous work queued up.    */
DECL|method|addDropPreviousSPSWorkAtDNs ()
name|void
name|addDropPreviousSPSWorkAtDNs
parameter_list|()
function_decl|;
comment|/**    * Remove the hint which was added to track SPS call.    *    * @param inodeId    *          - Inode ID    * @throws IOException    */
DECL|method|removeSPSHint (long inodeId)
name|void
name|removeSPSHint
parameter_list|(
name|long
name|inodeId
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Gets the number of live datanodes in the cluster.    *    * @return number of live datanodes    */
DECL|method|getNumLiveDataNodes ()
name|int
name|getNumLiveDataNodes
parameter_list|()
function_decl|;
comment|/**    * Get the file info for a specific file.    *    * @param inodeID    *          inode identifier    * @return file status metadata information    */
DECL|method|getFileInfo (long inodeID)
name|HdfsFileStatus
name|getFileInfo
parameter_list|(
name|long
name|inodeID
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns all the live datanodes and its storage details.    *    * @throws IOException    */
DECL|method|getLiveDatanodeStorageReport ()
name|DatanodeStorageReport
index|[]
name|getLiveDatanodeStorageReport
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns true if the given inode file has low redundancy blocks.    *    * @param inodeID    *          inode identifier    * @return true if block collection has low redundancy blocks    */
DECL|method|hasLowRedundancyBlocks (long inodeID)
name|boolean
name|hasLowRedundancyBlocks
parameter_list|(
name|long
name|inodeID
parameter_list|)
function_decl|;
comment|/**    * Checks whether the given datanode has sufficient space to occupy the given    * blockSize data.    *    * @param dn    *          datanode info    * @param type    *          storage type    * @param blockSize    *          blockSize to be scheduled    * @return true if the given datanode has sufficient space to occupy blockSize    *         data, false otherwise.    */
DECL|method|verifyTargetDatanodeHasSpaceForScheduling (DatanodeInfo dn, StorageType type, long blockSize)
name|boolean
name|verifyTargetDatanodeHasSpaceForScheduling
parameter_list|(
name|DatanodeInfo
name|dn
parameter_list|,
name|StorageType
name|type
parameter_list|,
name|long
name|blockSize
parameter_list|)
function_decl|;
comment|/**    * @return next SPS path id to process.    */
DECL|method|getNextSPSPathId ()
name|Long
name|getNextSPSPathId
parameter_list|()
function_decl|;
comment|/**    * Removes the SPS path id.    */
DECL|method|removeSPSPathId (long pathId)
name|void
name|removeSPSPathId
parameter_list|(
name|long
name|pathId
parameter_list|)
function_decl|;
comment|/**    * Removes all SPS path ids.    */
DECL|method|removeAllSPSPathIds ()
name|void
name|removeAllSPSPathIds
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

