begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.common.interfaces
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|interfaces
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
name|Configuration
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
name|hdds
operator|.
name|scm
operator|.
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|StorageContainerException
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
name|StorageLocation
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
name|util
operator|.
name|RwLock
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
name|hdds
operator|.
name|protocol
operator|.
name|DatanodeDetails
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|ContainerReportsProto
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|NodeReportProto
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
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|ContainerData
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
name|security
operator|.
name|NoSuchAlgorithmException
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
comment|/**  * Interface for container operations.  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|interface|ContainerManager
specifier|public
interface|interface
name|ContainerManager
extends|extends
name|RwLock
block|{
comment|/**    * Init call that sets up a container Manager.    *    * @param config        - Configuration.    * @param containerDirs - List of Metadata Container locations.    * @param datanodeDetails - DatanodeDetails    * @throws StorageContainerException    */
DECL|method|init (Configuration config, List<StorageLocation> containerDirs, DatanodeDetails datanodeDetails)
name|void
name|init
parameter_list|(
name|Configuration
name|config
parameter_list|,
name|List
argument_list|<
name|StorageLocation
argument_list|>
name|containerDirs
parameter_list|,
name|DatanodeDetails
name|datanodeDetails
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Creates a container with the given name.    *    * @param containerData - Container Name and metadata.    * @throws StorageContainerException    */
DECL|method|createContainer (ContainerData containerData)
name|void
name|createContainer
parameter_list|(
name|ContainerData
name|containerData
parameter_list|)
throws|throws
name|StorageContainerException
function_decl|;
comment|/**    * Deletes an existing container.    *    * @param containerID - ID of the container.    * @param forceDelete   - whether this container should be deleted forcibly.    * @throws StorageContainerException    */
DECL|method|deleteContainer (long containerID, boolean forceDelete)
name|void
name|deleteContainer
parameter_list|(
name|long
name|containerID
parameter_list|,
name|boolean
name|forceDelete
parameter_list|)
throws|throws
name|StorageContainerException
function_decl|;
comment|/**    * Update an existing container.    *    * @param containerID ID of the container    * @param data container data    * @param forceUpdate if true, update container forcibly.    * @throws StorageContainerException    */
DECL|method|updateContainer (long containerID, ContainerData data, boolean forceUpdate)
name|void
name|updateContainer
parameter_list|(
name|long
name|containerID
parameter_list|,
name|ContainerData
name|data
parameter_list|,
name|boolean
name|forceUpdate
parameter_list|)
throws|throws
name|StorageContainerException
function_decl|;
comment|/**    * As simple interface for container Iterations.    *    * @param startContainerID -  Return containers with ID>= startContainerID.    * @param count - how many to return    * @param data - Actual containerData    * @throws StorageContainerException    */
DECL|method|listContainer (long startContainerID, long count, List<ContainerData> data)
name|void
name|listContainer
parameter_list|(
name|long
name|startContainerID
parameter_list|,
name|long
name|count
parameter_list|,
name|List
argument_list|<
name|ContainerData
argument_list|>
name|data
parameter_list|)
throws|throws
name|StorageContainerException
function_decl|;
comment|/**    * Choose containers for block deletion.    *    * @param count   - how many to return    * @throws StorageContainerException    */
DECL|method|chooseContainerForBlockDeletion (int count)
name|List
argument_list|<
name|ContainerData
argument_list|>
name|chooseContainerForBlockDeletion
parameter_list|(
name|int
name|count
parameter_list|)
throws|throws
name|StorageContainerException
function_decl|;
comment|/**    * Get metadata about a specific container.    *    * @param containerID - ID of the container.    * @return ContainerData - Container Data.    * @throws StorageContainerException    */
DECL|method|readContainer (long containerID)
name|ContainerData
name|readContainer
parameter_list|(
name|long
name|containerID
parameter_list|)
throws|throws
name|StorageContainerException
function_decl|;
comment|/**    * Closes a open container, if it is already closed or does not exist a    * StorageContainerException is thrown.    * @param containerID - ID of the container.    * @throws StorageContainerException    */
DECL|method|closeContainer (long containerID)
name|void
name|closeContainer
parameter_list|(
name|long
name|containerID
parameter_list|)
throws|throws
name|StorageContainerException
throws|,
name|NoSuchAlgorithmException
function_decl|;
comment|/**    * Checks if a container exists.    * @param containerID - ID of the container.    * @return true if the container is open false otherwise.    * @throws StorageContainerException  - Throws Exception if we are not    * able to find the container.    */
DECL|method|isOpen (long containerID)
name|boolean
name|isOpen
parameter_list|(
name|long
name|containerID
parameter_list|)
throws|throws
name|StorageContainerException
function_decl|;
comment|/**    * Supports clean shutdown of container.    *    * @throws StorageContainerException    */
DECL|method|shutdown ()
name|void
name|shutdown
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Sets the Chunk Manager.    *    * @param chunkManager - ChunkManager.    */
DECL|method|setChunkManager (ChunkManager chunkManager)
name|void
name|setChunkManager
parameter_list|(
name|ChunkManager
name|chunkManager
parameter_list|)
function_decl|;
comment|/**    * Gets the Chunk Manager.    *    * @return ChunkManager.    */
DECL|method|getChunkManager ()
name|ChunkManager
name|getChunkManager
parameter_list|()
function_decl|;
comment|/**    * Sets the Key Manager.    *    * @param keyManager - Key Manager.    */
DECL|method|setKeyManager (KeyManager keyManager)
name|void
name|setKeyManager
parameter_list|(
name|KeyManager
name|keyManager
parameter_list|)
function_decl|;
comment|/**    * Gets the Key Manager.    *    * @return KeyManager.    */
DECL|method|getKeyManager ()
name|KeyManager
name|getKeyManager
parameter_list|()
function_decl|;
comment|/**    * Get the Node Report of container storage usage.    * @return node report.    */
DECL|method|getNodeReport ()
name|NodeReportProto
name|getNodeReport
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Gets container report.    * @return container report.    * @throws IOException    */
DECL|method|getContainerReport ()
name|ContainerReportsProto
name|getContainerReport
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Gets container reports.    * @return List of all closed containers.    * @throws IOException    */
DECL|method|getClosedContainerReports ()
name|List
argument_list|<
name|ContainerData
argument_list|>
name|getClosedContainerReports
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Increase pending deletion blocks count number of specified container.    *    * @param numBlocks    *          increment  count number    * @param containerId    *          container id    */
DECL|method|incrPendingDeletionBlocks (int numBlocks, long containerId)
name|void
name|incrPendingDeletionBlocks
parameter_list|(
name|int
name|numBlocks
parameter_list|,
name|long
name|containerId
parameter_list|)
function_decl|;
comment|/**    * Decrease pending deletion blocks count number of specified container.    *    * @param numBlocks    *          decrement count number    * @param containerId    *          container id    */
DECL|method|decrPendingDeletionBlocks (int numBlocks, long containerId)
name|void
name|decrPendingDeletionBlocks
parameter_list|(
name|int
name|numBlocks
parameter_list|,
name|long
name|containerId
parameter_list|)
function_decl|;
comment|/**    * Increase the read count of the container.    * @param containerId - ID of the container.    */
DECL|method|incrReadCount (long containerId)
name|void
name|incrReadCount
parameter_list|(
name|long
name|containerId
parameter_list|)
function_decl|;
comment|/**    * Increse the read counter for bytes read from the container.    * @param containerId - ID of the container.    * @param readBytes - bytes read from the container.    */
DECL|method|incrReadBytes (long containerId, long readBytes)
name|void
name|incrReadBytes
parameter_list|(
name|long
name|containerId
parameter_list|,
name|long
name|readBytes
parameter_list|)
function_decl|;
comment|/**    * Increase the write count of the container.    * @param containerId - ID of the container.    */
DECL|method|incrWriteCount (long containerId)
name|void
name|incrWriteCount
parameter_list|(
name|long
name|containerId
parameter_list|)
function_decl|;
comment|/**    * Increase the write counter for bytes write into the container.    * @param containerId - ID of the container.    * @param writeBytes - bytes write into the container.    */
DECL|method|incrWriteBytes (long containerId, long writeBytes)
name|void
name|incrWriteBytes
parameter_list|(
name|long
name|containerId
parameter_list|,
name|long
name|writeBytes
parameter_list|)
function_decl|;
comment|/**    * Increase the bytes used by the container.    * @param containerId - ID of the container.    * @param used - additional bytes used by the container.    * @return the current bytes used.    */
DECL|method|incrBytesUsed (long containerId, long used)
name|long
name|incrBytesUsed
parameter_list|(
name|long
name|containerId
parameter_list|,
name|long
name|used
parameter_list|)
function_decl|;
comment|/**    * Decrease the bytes used by the container.    * @param containerId - ID of the container.    * @param used - additional bytes reclaimed by the container.    * @return the current bytes used.    */
DECL|method|decrBytesUsed (long containerId, long used)
name|long
name|decrBytesUsed
parameter_list|(
name|long
name|containerId
parameter_list|,
name|long
name|used
parameter_list|)
function_decl|;
comment|/**    * Get the bytes used by the container.    * @param containerId - ID of the container.    * @return the current bytes used by the container.    */
DECL|method|getBytesUsed (long containerId)
name|long
name|getBytesUsed
parameter_list|(
name|long
name|containerId
parameter_list|)
function_decl|;
comment|/**    * Get the number of keys in the container.    * @param containerId - ID of the container.    * @return the current key count.    */
DECL|method|getNumKeys (long containerId)
name|long
name|getNumKeys
parameter_list|(
name|long
name|containerId
parameter_list|)
function_decl|;
DECL|method|updateDeleteTransactionId (long containerId, long deleteTransactionId)
name|void
name|updateDeleteTransactionId
parameter_list|(
name|long
name|containerId
parameter_list|,
name|long
name|deleteTransactionId
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

