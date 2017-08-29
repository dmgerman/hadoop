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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|SCMNodeReport
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
name|scm
operator|.
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|Pipeline
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
comment|/**    * Init call that sets up a container Manager.    *    * @param config        - Configuration.    * @param containerDirs - List of Metadata Container locations.    * @throws StorageContainerException    */
DECL|method|init (Configuration config, List<StorageLocation> containerDirs)
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
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Creates a container with the given name.    *    * @param pipeline      -- Nodes which make up this container.    * @param containerData - Container Name and metadata.    * @throws StorageContainerException    */
DECL|method|createContainer (Pipeline pipeline, ContainerData containerData)
name|void
name|createContainer
parameter_list|(
name|Pipeline
name|pipeline
parameter_list|,
name|ContainerData
name|containerData
parameter_list|)
throws|throws
name|StorageContainerException
function_decl|;
comment|/**    * Deletes an existing container.    *    * @param pipeline      - nodes that make this container.    * @param containerName - name of the container.    * @param forceDelete   - whether this container should be deleted forcibly.    * @throws StorageContainerException    */
DECL|method|deleteContainer (Pipeline pipeline, String containerName, boolean forceDelete)
name|void
name|deleteContainer
parameter_list|(
name|Pipeline
name|pipeline
parameter_list|,
name|String
name|containerName
parameter_list|,
name|boolean
name|forceDelete
parameter_list|)
throws|throws
name|StorageContainerException
function_decl|;
comment|/**    * Update an existing container.    *    * @param pipeline container nodes    * @param containerName name of the container    * @param data container data    * @param forceUpdate if true, update container forcibly.    * @throws StorageContainerException    */
DECL|method|updateContainer (Pipeline pipeline, String containerName, ContainerData data, boolean forceUpdate)
name|void
name|updateContainer
parameter_list|(
name|Pipeline
name|pipeline
parameter_list|,
name|String
name|containerName
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
comment|/**    * As simple interface for container Iterations.    *    * @param prefix - Return only values matching this prefix    * @param count   - how many to return    * @param prevKey - Previous key - Server returns results from this point.    * @param data    - Actual containerData    * @throws StorageContainerException    */
DECL|method|listContainer (String prefix, long count, String prevKey, List<ContainerData> data)
name|void
name|listContainer
parameter_list|(
name|String
name|prefix
parameter_list|,
name|long
name|count
parameter_list|,
name|String
name|prevKey
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
comment|/**    * Get metadata about a specific container.    *    * @param containerName - Name of the container    * @return ContainerData - Container Data.    * @throws StorageContainerException    */
DECL|method|readContainer (String containerName)
name|ContainerData
name|readContainer
parameter_list|(
name|String
name|containerName
parameter_list|)
throws|throws
name|StorageContainerException
function_decl|;
comment|/**    * Closes a open container, if it is already closed or does not exist a    * StorageContainerException is thrown.    * @param containerName - Name of the container.    * @throws StorageContainerException    */
DECL|method|closeContainer (String containerName)
name|void
name|closeContainer
parameter_list|(
name|String
name|containerName
parameter_list|)
throws|throws
name|StorageContainerException
throws|,
name|NoSuchAlgorithmException
function_decl|;
comment|/**    * Checks if a container exists.    * @param containerName - Name of the container.    * @return true if the container is open false otherwise.    * @throws StorageContainerException  - Throws Exception if we are not    * able to find the container.    */
DECL|method|isOpen (String containerName)
name|boolean
name|isOpen
parameter_list|(
name|String
name|containerName
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
name|SCMNodeReport
name|getNodeReport
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Gets container reports.    * @return List of all closed containers.    * @throws IOException    */
DECL|method|getContainerReports ()
name|List
argument_list|<
name|ContainerData
argument_list|>
name|getContainerReports
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

