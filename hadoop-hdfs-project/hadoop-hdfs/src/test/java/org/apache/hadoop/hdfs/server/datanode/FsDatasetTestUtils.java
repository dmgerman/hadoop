begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode
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
name|datanode
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
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
name|server
operator|.
name|datanode
operator|.
name|fsdataset
operator|.
name|impl
operator|.
name|FsDatasetFactory
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
name|fsdataset
operator|.
name|FsVolumeSpi
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
name|util
operator|.
name|ReflectionUtils
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
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
name|util
operator|.
name|Iterator
import|;
end_import

begin_comment
comment|/**  * Provide block access for FsDataset white box tests.  */
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
DECL|interface|FsDatasetTestUtils
specifier|public
interface|interface
name|FsDatasetTestUtils
block|{
DECL|class|Factory
specifier|abstract
class|class
name|Factory
parameter_list|<
name|D
extends|extends
name|FsDatasetTestUtils
parameter_list|>
block|{
comment|/**      * By default, it returns FsDatasetImplTestUtilsFactory.      *      * @return The configured Factory.      */
DECL|method|getFactory (Configuration conf)
specifier|public
specifier|static
name|Factory
argument_list|<
name|?
argument_list|>
name|getFactory
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|String
name|className
init|=
name|conf
operator|.
name|get
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_FSDATASET_FACTORY_KEY
argument_list|,
name|FsDatasetFactory
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|Preconditions
operator|.
name|checkState
argument_list|(
name|className
operator|.
name|contains
argument_list|(
literal|"Factory"
argument_list|)
argument_list|)
expr_stmt|;
name|className
operator|=
name|className
operator|.
name|replaceFirst
argument_list|(
literal|"(\\$)?Factory$"
argument_list|,
literal|"TestUtilsFactory"
argument_list|)
expr_stmt|;
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|Factory
argument_list|>
name|clazz
init|=
name|conf
operator|.
name|getClass
argument_list|(
name|className
argument_list|,
name|FsDatasetImplTestUtilsFactory
operator|.
name|class
argument_list|,
name|Factory
operator|.
name|class
argument_list|)
decl_stmt|;
return|return
name|ReflectionUtils
operator|.
name|newInstance
argument_list|(
name|clazz
argument_list|,
name|conf
argument_list|)
return|;
block|}
comment|/**      * Create a new instance of FsDatasetTestUtils.      */
DECL|method|newInstance (DataNode datanode)
specifier|public
specifier|abstract
name|D
name|newInstance
parameter_list|(
name|DataNode
name|datanode
parameter_list|)
function_decl|;
comment|/**      * @return True for SimulatedFsDataset      */
DECL|method|isSimulated ()
specifier|public
name|boolean
name|isSimulated
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/**      * Get the default number of data directories for underlying storage per      * DataNode.      *      * @return The default number of data dirs per DataNode.      */
DECL|method|getDefaultNumOfDataDirs ()
specifier|abstract
specifier|public
name|int
name|getDefaultNumOfDataDirs
parameter_list|()
function_decl|;
block|}
comment|/**    * A replica to be corrupted.    *    * It is safe to corrupt this replica even if the MiniDFSCluster is shutdown.    */
DECL|interface|MaterializedReplica
interface|interface
name|MaterializedReplica
block|{
comment|/**      * Corrupt the block file of the replica.      * @throws FileNotFoundException if the block file does not exist.      * @throws IOException if I/O error.      */
DECL|method|corruptData ()
name|void
name|corruptData
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**      * Corrupt the block file with the given content.      * @param newContent the new content written to the block file.      * @throws FileNotFoundException if the block file does not exist.      * @throws IOException if I/O error.      */
DECL|method|corruptData (byte[] newContent)
name|void
name|corruptData
parameter_list|(
name|byte
index|[]
name|newContent
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Truncate the block file of the replica to the newSize.      * @param newSize the new size of the block file.      * @throws FileNotFoundException if the block file does not exist.      * @throws IOException if I/O error.      */
DECL|method|truncateData (long newSize)
name|void
name|truncateData
parameter_list|(
name|long
name|newSize
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Delete the block file of the replica.      * @throws FileNotFoundException if the block file does not exist.      * @throws IOException if I/O error.      */
DECL|method|deleteData ()
name|void
name|deleteData
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**      * Corrupt the metadata file of the replica.      * @throws FileNotFoundException if the block file does not exist.      * @throws IOException if I/O error.      */
DECL|method|corruptMeta ()
name|void
name|corruptMeta
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**      * Delete the metadata file of the replcia.      * @throws FileNotFoundException if the block file does not exist.      * @throws IOException I/O error.      */
DECL|method|deleteMeta ()
name|void
name|deleteMeta
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**      * Truncate the metadata file of the replica to the newSize.      * @throws FileNotFoundException if the block file does not exist.      * @throws IOException I/O error.      */
DECL|method|truncateMeta (long newSize)
name|void
name|truncateMeta
parameter_list|(
name|long
name|newSize
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
comment|/**    * Get a materialized replica to corrupt its block / crc later.    * @param block the block of this replica begone to.    * @return a replica to corrupt. Return null if the replica does not exist    * in this dataset.    * @throws ReplicaNotFoundException if the replica does not exists on the    *         dataset.    */
DECL|method|getMaterializedReplica (ExtendedBlock block)
name|MaterializedReplica
name|getMaterializedReplica
parameter_list|(
name|ExtendedBlock
name|block
parameter_list|)
throws|throws
name|ReplicaNotFoundException
function_decl|;
comment|/**    * Create a finalized replica and add it into the FsDataset.    */
DECL|method|createFinalizedReplica (ExtendedBlock block)
name|Replica
name|createFinalizedReplica
parameter_list|(
name|ExtendedBlock
name|block
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Create a finalized replica on a particular volume, and add it into    * the FsDataset.    */
DECL|method|createFinalizedReplica (FsVolumeSpi volume, ExtendedBlock block)
name|Replica
name|createFinalizedReplica
parameter_list|(
name|FsVolumeSpi
name|volume
parameter_list|,
name|ExtendedBlock
name|block
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Create a {@link ReplicaInPipeline} and add it into the FsDataset.    */
DECL|method|createReplicaInPipeline (ExtendedBlock block)
name|Replica
name|createReplicaInPipeline
parameter_list|(
name|ExtendedBlock
name|block
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Create a {@link ReplicaInPipeline} and add it into the FsDataset.    */
DECL|method|createReplicaInPipeline (FsVolumeSpi volume, ExtendedBlock block)
name|Replica
name|createReplicaInPipeline
parameter_list|(
name|FsVolumeSpi
name|volume
parameter_list|,
name|ExtendedBlock
name|block
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Create a {@link ReplicaBeingWritten} and add it into the FsDataset.    */
DECL|method|createRBW (ExtendedBlock block)
name|Replica
name|createRBW
parameter_list|(
name|ExtendedBlock
name|block
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Create a {@link ReplicaBeingWritten} on the particular volume, and add it    * into the FsDataset.    */
DECL|method|createRBW (FsVolumeSpi volume, ExtendedBlock block)
name|Replica
name|createRBW
parameter_list|(
name|FsVolumeSpi
name|volume
parameter_list|,
name|ExtendedBlock
name|block
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Create a {@link ReplicaWaitingToBeRecovered} object and add it into the    * FsDataset.    */
DECL|method|createReplicaWaitingToBeRecovered (ExtendedBlock block)
name|Replica
name|createReplicaWaitingToBeRecovered
parameter_list|(
name|ExtendedBlock
name|block
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Create a {@link ReplicaWaitingToBeRecovered} on the particular volume,    * and add it into the FsDataset.    */
DECL|method|createReplicaWaitingToBeRecovered ( FsVolumeSpi volume, ExtendedBlock block)
name|Replica
name|createReplicaWaitingToBeRecovered
parameter_list|(
name|FsVolumeSpi
name|volume
parameter_list|,
name|ExtendedBlock
name|block
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Create a {@link ReplicaUnderRecovery} object and add it into the FsDataset.    */
DECL|method|createReplicaUnderRecovery (ExtendedBlock block, long recoveryId)
name|Replica
name|createReplicaUnderRecovery
parameter_list|(
name|ExtendedBlock
name|block
parameter_list|,
name|long
name|recoveryId
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Check the stored files / data of a replica.    * @param replica a replica object.    * @throws IOException    */
DECL|method|checkStoredReplica (final Replica replica)
name|void
name|checkStoredReplica
parameter_list|(
specifier|final
name|Replica
name|replica
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Create dummy replicas for block data and metadata.    * @param block the block of which replica to be created.    * @throws IOException on I/O error.    */
DECL|method|injectCorruptReplica (ExtendedBlock block)
name|void
name|injectCorruptReplica
parameter_list|(
name|ExtendedBlock
name|block
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Get the replica of a block. Returns null if it does not exist.    * @param block the block whose replica will be returned.    * @return Replica for the block.    */
DECL|method|fetchReplica (ExtendedBlock block)
name|Replica
name|fetchReplica
parameter_list|(
name|ExtendedBlock
name|block
parameter_list|)
function_decl|;
comment|/**    * @return The default value of number of data dirs per DataNode in    * MiniDFSCluster.    */
DECL|method|getDefaultNumOfDataDirs ()
name|int
name|getDefaultNumOfDataDirs
parameter_list|()
function_decl|;
comment|/**    * Obtain the raw capacity of underlying storage per DataNode.    */
DECL|method|getRawCapacity ()
name|long
name|getRawCapacity
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Get the persistently stored length of the block.    */
DECL|method|getStoredDataLength (ExtendedBlock block)
name|long
name|getStoredDataLength
parameter_list|(
name|ExtendedBlock
name|block
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Get the persistently stored generation stamp.    */
DECL|method|getStoredGenerationStamp (ExtendedBlock block)
name|long
name|getStoredGenerationStamp
parameter_list|(
name|ExtendedBlock
name|block
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Change the persistently stored generation stamp.    * @param block the block whose generation stamp will be changed    * @param newGenStamp the new generation stamp    * @throws IOException    */
DECL|method|changeStoredGenerationStamp (ExtendedBlock block, long newGenStamp)
name|void
name|changeStoredGenerationStamp
parameter_list|(
name|ExtendedBlock
name|block
parameter_list|,
name|long
name|newGenStamp
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Get all stored replicas in the specified block pool. */
DECL|method|getStoredReplicas (String bpid)
name|Iterator
argument_list|<
name|Replica
argument_list|>
name|getStoredReplicas
parameter_list|(
name|String
name|bpid
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

