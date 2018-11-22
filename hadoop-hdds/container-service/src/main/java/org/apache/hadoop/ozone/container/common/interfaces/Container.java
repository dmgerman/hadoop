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
name|java
operator|.
name|io
operator|.
name|File
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
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|datanode
operator|.
name|proto
operator|.
name|ContainerProtos
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
name|ContainerReplicaProto
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|impl
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
name|container
operator|.
name|common
operator|.
name|volume
operator|.
name|VolumeSet
import|;
end_import

begin_comment
comment|/**  * Interface for Container Operations.  */
end_comment

begin_interface
DECL|interface|Container
specifier|public
interface|interface
name|Container
parameter_list|<
name|CONTAINERDATA
extends|extends
name|ContainerData
parameter_list|>
extends|extends
name|RwLock
block|{
comment|/**    * Creates a container.    *    * @throws StorageContainerException    */
DECL|method|create (VolumeSet volumeSet, VolumeChoosingPolicy volumeChoosingPolicy, String scmId)
name|void
name|create
parameter_list|(
name|VolumeSet
name|volumeSet
parameter_list|,
name|VolumeChoosingPolicy
name|volumeChoosingPolicy
parameter_list|,
name|String
name|scmId
parameter_list|)
throws|throws
name|StorageContainerException
function_decl|;
comment|/**    * Deletes the container.    *    * @param forceDelete   - whether this container should be deleted forcibly.    * @throws StorageContainerException    */
DECL|method|delete (boolean forceDelete)
name|void
name|delete
parameter_list|(
name|boolean
name|forceDelete
parameter_list|)
throws|throws
name|StorageContainerException
function_decl|;
comment|/**    * Update the container.    *    * @param metaData    * @param forceUpdate if true, update container forcibly.    * @throws StorageContainerException    */
DECL|method|update (Map<String, String> metaData, boolean forceUpdate)
name|void
name|update
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|metaData
parameter_list|,
name|boolean
name|forceUpdate
parameter_list|)
throws|throws
name|StorageContainerException
function_decl|;
comment|/**    * Get metadata about the container.    *    * @return ContainerData - Container Data.    */
DECL|method|getContainerData ()
name|CONTAINERDATA
name|getContainerData
parameter_list|()
function_decl|;
comment|/**    * Get the Container Lifecycle state.    *    * @return ContainerLifeCycleState - Container State.    */
DECL|method|getContainerState ()
name|ContainerProtos
operator|.
name|ContainerDataProto
operator|.
name|State
name|getContainerState
parameter_list|()
function_decl|;
comment|/**    * Marks the container for closing. Moves the container to CLOSING state.    */
DECL|method|markContainerForClose ()
name|void
name|markContainerForClose
parameter_list|()
throws|throws
name|StorageContainerException
function_decl|;
comment|/**    * Quasi Closes a open container, if it is already closed or does not exist a    * StorageContainerException is thrown.    *    * @throws StorageContainerException    */
DECL|method|quasiClose ()
name|void
name|quasiClose
parameter_list|()
throws|throws
name|StorageContainerException
function_decl|;
comment|/**    * Closes a open/quasi closed container, if it is already closed or does not    * exist a StorageContainerException is thrown.    *    * @throws StorageContainerException    */
DECL|method|close ()
name|void
name|close
parameter_list|()
throws|throws
name|StorageContainerException
function_decl|;
comment|/**    * Return the ContainerType for the container.    */
DECL|method|getContainerType ()
name|ContainerProtos
operator|.
name|ContainerType
name|getContainerType
parameter_list|()
function_decl|;
comment|/**    * Returns containerFile.    */
DECL|method|getContainerFile ()
name|File
name|getContainerFile
parameter_list|()
function_decl|;
comment|/**    * updates the DeleteTransactionId.    * @param deleteTransactionId    */
DECL|method|updateDeleteTransactionId (long deleteTransactionId)
name|void
name|updateDeleteTransactionId
parameter_list|(
name|long
name|deleteTransactionId
parameter_list|)
function_decl|;
comment|/**    * Returns blockIterator for the container.    * @return BlockIterator    * @throws IOException    */
DECL|method|blockIterator ()
name|BlockIterator
name|blockIterator
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Import the container from an external archive.    */
DECL|method|importContainerData (InputStream stream, ContainerPacker<CONTAINERDATA> packer)
name|void
name|importContainerData
parameter_list|(
name|InputStream
name|stream
parameter_list|,
name|ContainerPacker
argument_list|<
name|CONTAINERDATA
argument_list|>
name|packer
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Export all the data of the container to one output archive with the help    * of the packer.    *    */
DECL|method|exportContainerData (OutputStream stream, ContainerPacker<CONTAINERDATA> packer)
name|void
name|exportContainerData
parameter_list|(
name|OutputStream
name|stream
parameter_list|,
name|ContainerPacker
argument_list|<
name|CONTAINERDATA
argument_list|>
name|packer
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns containerReport for the container.    */
DECL|method|getContainerReport ()
name|ContainerReplicaProto
name|getContainerReport
parameter_list|()
throws|throws
name|StorageContainerException
function_decl|;
comment|/**    * updates the blockCommitSequenceId.    */
DECL|method|updateBlockCommitSequenceId (long blockCommitSequenceId)
name|void
name|updateBlockCommitSequenceId
parameter_list|(
name|long
name|blockCommitSequenceId
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

