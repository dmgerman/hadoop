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
name|namenode
operator|.
name|sps
operator|.
name|StoragePolicySatisfier
operator|.
name|DatanodeMap
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
comment|/**  * An interface for the communication between SPS and Namenode module.  *  * @param<T>  *          is identifier of inode or full path name of inode. Internal sps will  *          use the file inodeId for the block movement. External sps will use  *          file string path representation for the block movement.  */
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
parameter_list|<
name|T
parameter_list|>
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
comment|/**    * Gets the network topology.    *    * @param datanodeMap    *          target datanodes    *    * @return network topology    */
DECL|method|getNetworkTopology (DatanodeMap datanodeMap)
name|NetworkTopology
name|getNetworkTopology
parameter_list|(
name|DatanodeMap
name|datanodeMap
parameter_list|)
function_decl|;
comment|/**    * Returns true if the give file exists in the Namespace.    *    * @param filePath    *          - file info    * @return true if the given file exists, false otherwise.    */
DECL|method|isFileExist (T filePath)
name|boolean
name|isFileExist
parameter_list|(
name|T
name|filePath
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
comment|/**    * Remove the hint which was added to track SPS call.    *    * @param spsPath    *          - user invoked satisfier path    * @throws IOException    */
DECL|method|removeSPSHint (T spsPath)
name|void
name|removeSPSHint
parameter_list|(
name|T
name|spsPath
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
comment|/**    * Get the file info for a specific file.    *    * @param file    *          file path    * @return file status metadata information    */
DECL|method|getFileInfo (T file)
name|HdfsFileStatus
name|getFileInfo
parameter_list|(
name|T
name|file
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
comment|/**    * @return next SPS path info to process.    */
DECL|method|getNextSPSPath ()
name|T
name|getNextSPSPath
parameter_list|()
function_decl|;
comment|/**    * Removes the SPS path id.    */
DECL|method|removeSPSPathId (T pathId)
name|void
name|removeSPSPathId
parameter_list|(
name|T
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

