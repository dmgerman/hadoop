begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p/>  * http://www.apache.org/licenses/LICENSE-2.0  *<p/>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.diskbalancer
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
name|diskbalancer
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
name|fs
operator|.
name|FileSystem
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
name|Path
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
name|DFSTestUtil
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
name|MiniDFSCluster
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
name|balancer
operator|.
name|TestBalancer
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
name|common
operator|.
name|HdfsServerConstants
operator|.
name|StartupOption
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
name|DataNode
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
name|FsDatasetSpi
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
name|FsVolumeImpl
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
name|diskbalancer
operator|.
name|connectors
operator|.
name|NullConnector
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
name|diskbalancer
operator|.
name|datamodel
operator|.
name|DiskBalancerCluster
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
name|diskbalancer
operator|.
name|datamodel
operator|.
name|DiskBalancerDataNode
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
name|diskbalancer
operator|.
name|datamodel
operator|.
name|DiskBalancerVolume
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
name|diskbalancer
operator|.
name|datamodel
operator|.
name|DiskBalancerVolumeSet
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
name|test
operator|.
name|GenericTestUtils
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
name|Time
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

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
name|util
operator|.
name|Random
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeoutException
import|;
end_import

begin_comment
comment|/**  * Helper class to create various cluster configurations at run time.  */
end_comment

begin_class
DECL|class|DiskBalancerTestUtil
specifier|public
class|class
name|DiskBalancerTestUtil
block|{
DECL|field|LOG
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestDiskBalancer
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|MB
specifier|public
specifier|static
specifier|final
name|long
name|MB
init|=
literal|1024
operator|*
literal|1024L
decl_stmt|;
DECL|field|GB
specifier|public
specifier|static
specifier|final
name|long
name|GB
init|=
name|MB
operator|*
literal|1024L
decl_stmt|;
DECL|field|TB
specifier|public
specifier|static
specifier|final
name|long
name|TB
init|=
name|GB
operator|*
literal|1024L
decl_stmt|;
DECL|field|diskSizes
specifier|private
specifier|static
name|int
index|[]
name|diskSizes
init|=
block|{
literal|1
block|,
literal|2
block|,
literal|3
block|,
literal|4
block|,
literal|5
block|,
literal|6
block|,
literal|7
block|,
literal|8
block|,
literal|9
block|,
literal|100
block|,
literal|200
block|,
literal|300
block|,
literal|400
block|,
literal|500
block|,
literal|600
block|,
literal|700
block|,
literal|800
block|,
literal|900
block|}
decl_stmt|;
DECL|field|rand
specifier|private
name|Random
name|rand
decl_stmt|;
DECL|field|stringTable
specifier|private
name|String
name|stringTable
init|=
literal|"ABCDEDFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0987654321"
decl_stmt|;
comment|/**    * Constructs a util class.    */
DECL|method|DiskBalancerTestUtil ()
specifier|public
name|DiskBalancerTestUtil
parameter_list|()
block|{
name|this
operator|.
name|rand
operator|=
operator|new
name|Random
argument_list|(
name|Time
operator|.
name|monotonicNow
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns a random string.    *    * @param length - Number of chars in the string    * @return random String    */
DECL|method|getRandomName (int length)
specifier|private
name|String
name|getRandomName
parameter_list|(
name|int
name|length
parameter_list|)
block|{
name|StringBuilder
name|name
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
name|length
condition|;
name|x
operator|++
control|)
block|{
name|name
operator|.
name|append
argument_list|(
name|stringTable
operator|.
name|charAt
argument_list|(
name|rand
operator|.
name|nextInt
argument_list|(
name|stringTable
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|name
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Returns a Random Storage Type.    *    * @return - StorageType    */
DECL|method|getRandomStorageType ()
specifier|private
name|StorageType
name|getRandomStorageType
parameter_list|()
block|{
return|return
name|StorageType
operator|.
name|parseStorageType
argument_list|(
name|rand
operator|.
name|nextInt
argument_list|(
literal|3
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Returns random capacity, if the size is smaller than 10    * they are TBs otherwise the size is assigned to GB range.    *    * @return Long - Disk Size    */
DECL|method|getRandomCapacity ()
specifier|private
name|long
name|getRandomCapacity
parameter_list|()
block|{
name|int
name|size
init|=
name|diskSizes
index|[
name|rand
operator|.
name|nextInt
argument_list|(
name|diskSizes
operator|.
name|length
argument_list|)
index|]
decl_stmt|;
if|if
condition|(
name|size
operator|<
literal|10
condition|)
block|{
return|return
name|size
operator|*
name|TB
return|;
block|}
else|else
block|{
return|return
name|size
operator|*
name|GB
return|;
block|}
block|}
comment|/**    * Some value under 20% in these tests.    */
DECL|method|getRandomReserved (long capacity)
specifier|private
name|long
name|getRandomReserved
parameter_list|(
name|long
name|capacity
parameter_list|)
block|{
name|double
name|rcap
init|=
name|capacity
operator|*
literal|0.2d
decl_stmt|;
name|double
name|randDouble
init|=
name|rand
operator|.
name|nextDouble
argument_list|()
decl_stmt|;
name|double
name|temp
init|=
name|randDouble
operator|*
name|rcap
decl_stmt|;
return|return
operator|(
operator|new
name|Double
argument_list|(
name|temp
argument_list|)
operator|)
operator|.
name|longValue
argument_list|()
return|;
block|}
comment|/**    * Some value less that capacity - reserved.    */
DECL|method|getRandomDfsUsed (long capacity, long reserved)
specifier|private
name|long
name|getRandomDfsUsed
parameter_list|(
name|long
name|capacity
parameter_list|,
name|long
name|reserved
parameter_list|)
block|{
name|double
name|rcap
init|=
name|capacity
operator|-
name|reserved
decl_stmt|;
name|double
name|randDouble
init|=
name|rand
operator|.
name|nextDouble
argument_list|()
decl_stmt|;
name|double
name|temp
init|=
name|randDouble
operator|*
name|rcap
decl_stmt|;
return|return
operator|(
operator|new
name|Double
argument_list|(
name|temp
argument_list|)
operator|)
operator|.
name|longValue
argument_list|()
return|;
block|}
comment|/**    * Creates a Random Volume of a specific storageType.    *    * @return Volume    */
DECL|method|createRandomVolume ()
specifier|public
name|DiskBalancerVolume
name|createRandomVolume
parameter_list|()
block|{
return|return
name|createRandomVolume
argument_list|(
name|getRandomStorageType
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Creates a Random Volume for testing purpose.    *    * @param type - StorageType    * @return DiskBalancerVolume    */
DECL|method|createRandomVolume (StorageType type)
specifier|public
name|DiskBalancerVolume
name|createRandomVolume
parameter_list|(
name|StorageType
name|type
parameter_list|)
block|{
name|DiskBalancerVolume
name|volume
init|=
operator|new
name|DiskBalancerVolume
argument_list|()
decl_stmt|;
name|volume
operator|.
name|setPath
argument_list|(
literal|"/tmp/disk/"
operator|+
name|getRandomName
argument_list|(
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|volume
operator|.
name|setStorageType
argument_list|(
name|type
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|volume
operator|.
name|setTransient
argument_list|(
name|type
operator|.
name|isTransient
argument_list|()
argument_list|)
expr_stmt|;
name|volume
operator|.
name|setCapacity
argument_list|(
name|getRandomCapacity
argument_list|()
argument_list|)
expr_stmt|;
name|volume
operator|.
name|setReserved
argument_list|(
name|getRandomReserved
argument_list|(
name|volume
operator|.
name|getCapacity
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|volume
operator|.
name|setUsed
argument_list|(
name|getRandomDfsUsed
argument_list|(
name|volume
operator|.
name|getCapacity
argument_list|()
argument_list|,
name|volume
operator|.
name|getReserved
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|volume
operator|.
name|setUuid
argument_list|(
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|volume
return|;
block|}
comment|/**    * Creates a RandomVolumeSet.    *    * @param type      - Storage Type    * @param diskCount - How many disks you need.    * @return volumeSet    * @throws Exception    */
DECL|method|createRandomVolumeSet (StorageType type, int diskCount)
specifier|public
name|DiskBalancerVolumeSet
name|createRandomVolumeSet
parameter_list|(
name|StorageType
name|type
parameter_list|,
name|int
name|diskCount
parameter_list|)
throws|throws
name|Exception
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
name|diskCount
operator|>
literal|0
argument_list|)
expr_stmt|;
name|DiskBalancerVolumeSet
name|volumeSet
init|=
operator|new
name|DiskBalancerVolumeSet
argument_list|(
name|type
operator|.
name|isTransient
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
name|diskCount
condition|;
name|x
operator|++
control|)
block|{
name|volumeSet
operator|.
name|addVolume
argument_list|(
name|createRandomVolume
argument_list|(
name|type
argument_list|)
argument_list|)
expr_stmt|;
block|}
assert|assert
operator|(
name|volumeSet
operator|.
name|getVolumeCount
argument_list|()
operator|==
name|diskCount
operator|)
assert|;
return|return
name|volumeSet
return|;
block|}
comment|/**    * Creates a RandomDataNode.    *    * @param diskTypes - Storage types needed in the Node    * @param diskCount - Disk count - that many disks of each type is created    * @return DataNode    * @throws Exception    */
DECL|method|createRandomDataNode (StorageType[] diskTypes, int diskCount)
specifier|public
name|DiskBalancerDataNode
name|createRandomDataNode
parameter_list|(
name|StorageType
index|[]
name|diskTypes
parameter_list|,
name|int
name|diskCount
parameter_list|)
throws|throws
name|Exception
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
name|diskTypes
operator|.
name|length
operator|>
literal|0
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkState
argument_list|(
name|diskCount
operator|>
literal|0
argument_list|)
expr_stmt|;
name|DiskBalancerDataNode
name|node
init|=
operator|new
name|DiskBalancerDataNode
argument_list|(
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|StorageType
name|t
range|:
name|diskTypes
control|)
block|{
name|DiskBalancerVolumeSet
name|vSet
init|=
name|createRandomVolumeSet
argument_list|(
name|t
argument_list|,
name|diskCount
argument_list|)
decl_stmt|;
for|for
control|(
name|DiskBalancerVolume
name|v
range|:
name|vSet
operator|.
name|getVolumes
argument_list|()
control|)
block|{
name|node
operator|.
name|addVolume
argument_list|(
name|v
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|node
return|;
block|}
comment|/**    * Creates a RandomCluster.    *    * @param dataNodeCount - How many nodes you need    * @param diskTypes     - StorageTypes you need in each node    * @param diskCount     - How many disks you need of each type.    * @return Cluster    * @throws Exception    */
DECL|method|createRandCluster (int dataNodeCount, StorageType[] diskTypes, int diskCount)
specifier|public
name|DiskBalancerCluster
name|createRandCluster
parameter_list|(
name|int
name|dataNodeCount
parameter_list|,
name|StorageType
index|[]
name|diskTypes
parameter_list|,
name|int
name|diskCount
parameter_list|)
throws|throws
name|Exception
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
name|diskTypes
operator|.
name|length
operator|>
literal|0
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkState
argument_list|(
name|diskCount
operator|>
literal|0
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkState
argument_list|(
name|dataNodeCount
operator|>
literal|0
argument_list|)
expr_stmt|;
name|NullConnector
name|nullConnector
init|=
operator|new
name|NullConnector
argument_list|()
decl_stmt|;
name|DiskBalancerCluster
name|cluster
init|=
operator|new
name|DiskBalancerCluster
argument_list|(
name|nullConnector
argument_list|)
decl_stmt|;
comment|// once we add these nodes into the connector, cluster will read them
comment|// from the connector.
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
name|dataNodeCount
condition|;
name|x
operator|++
control|)
block|{
name|nullConnector
operator|.
name|addNode
argument_list|(
name|createRandomDataNode
argument_list|(
name|diskTypes
argument_list|,
name|diskCount
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// with this call we have populated the cluster info
name|cluster
operator|.
name|readClusterInfo
argument_list|()
expr_stmt|;
return|return
name|cluster
return|;
block|}
comment|/**    * Returns the number of blocks on a volume.    *    * @param source - Source Volume.    * @return Number of Blocks.    * @throws IOException    */
DECL|method|getBlockCount (FsVolumeSpi source, boolean checkblockPoolCount)
specifier|public
specifier|static
name|int
name|getBlockCount
parameter_list|(
name|FsVolumeSpi
name|source
parameter_list|,
name|boolean
name|checkblockPoolCount
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|blockPoolID
range|:
name|source
operator|.
name|getBlockPoolList
argument_list|()
control|)
block|{
name|FsVolumeSpi
operator|.
name|BlockIterator
name|sourceIter
init|=
name|source
operator|.
name|newBlockIterator
argument_list|(
name|blockPoolID
argument_list|,
literal|"TestDiskBalancerSource"
argument_list|)
decl_stmt|;
name|int
name|blockCount
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|!
name|sourceIter
operator|.
name|atEnd
argument_list|()
condition|)
block|{
name|ExtendedBlock
name|block
init|=
name|sourceIter
operator|.
name|nextBlock
argument_list|()
decl_stmt|;
if|if
condition|(
name|block
operator|!=
literal|null
condition|)
block|{
name|blockCount
operator|++
expr_stmt|;
block|}
block|}
if|if
condition|(
name|checkblockPoolCount
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Block Pool Id:  {}, blockCount: {}"
argument_list|,
name|blockPoolID
argument_list|,
name|blockCount
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|blockCount
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
name|count
operator|+=
name|blockCount
expr_stmt|;
block|}
return|return
name|count
return|;
block|}
DECL|method|newImbalancedCluster ( final Configuration conf, final int numDatanodes, final long[] storageCapacities, final int defaultBlockSize, final int fileLen)
specifier|public
specifier|static
name|MiniDFSCluster
name|newImbalancedCluster
parameter_list|(
specifier|final
name|Configuration
name|conf
parameter_list|,
specifier|final
name|int
name|numDatanodes
parameter_list|,
specifier|final
name|long
index|[]
name|storageCapacities
parameter_list|,
specifier|final
name|int
name|defaultBlockSize
parameter_list|,
specifier|final
name|int
name|fileLen
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
throws|,
name|TimeoutException
block|{
return|return
name|newImbalancedCluster
argument_list|(
name|conf
argument_list|,
name|numDatanodes
argument_list|,
name|storageCapacities
argument_list|,
name|defaultBlockSize
argument_list|,
name|fileLen
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|newImbalancedCluster ( final Configuration conf, final int numDatanodes, final long[] storageCapacities, final int defaultBlockSize, final int fileLen, final StartupOption dnOption)
specifier|public
specifier|static
name|MiniDFSCluster
name|newImbalancedCluster
parameter_list|(
specifier|final
name|Configuration
name|conf
parameter_list|,
specifier|final
name|int
name|numDatanodes
parameter_list|,
specifier|final
name|long
index|[]
name|storageCapacities
parameter_list|,
specifier|final
name|int
name|defaultBlockSize
parameter_list|,
specifier|final
name|int
name|fileLen
parameter_list|,
specifier|final
name|StartupOption
name|dnOption
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
throws|,
name|TimeoutException
block|{
name|conf
operator|.
name|setBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DISK_BALANCER_ENABLED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BLOCK_SIZE_KEY
argument_list|,
name|defaultBlockSize
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BYTES_PER_CHECKSUM_KEY
argument_list|,
name|defaultBlockSize
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_HEARTBEAT_INTERVAL_KEY
argument_list|,
literal|1L
argument_list|)
expr_stmt|;
specifier|final
name|String
name|fileName
init|=
literal|"/"
operator|+
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
specifier|final
name|Path
name|filePath
init|=
operator|new
name|Path
argument_list|(
name|fileName
argument_list|)
decl_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|storageCapacities
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|storageCapacities
operator|.
name|length
operator|==
literal|2
argument_list|,
literal|"need to specify capacities for two storages."
argument_list|)
expr_stmt|;
comment|// Write a file and restart the cluster
name|File
name|basedir
init|=
operator|new
name|File
argument_list|(
name|GenericTestUtils
operator|.
name|getRandomizedTempPath
argument_list|()
argument_list|)
decl_stmt|;
name|MiniDFSCluster
name|cluster
init|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|,
name|basedir
argument_list|)
operator|.
name|numDataNodes
argument_list|(
name|numDatanodes
argument_list|)
operator|.
name|storageCapacities
argument_list|(
name|storageCapacities
argument_list|)
operator|.
name|storageTypes
argument_list|(
operator|new
name|StorageType
index|[]
block|{
name|StorageType
operator|.
name|DISK
block|,
name|StorageType
operator|.
name|DISK
block|}
argument_list|)
operator|.
name|storagesPerDatanode
argument_list|(
literal|2
argument_list|)
operator|.
name|dnStartupOption
argument_list|(
name|dnOption
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|FsVolumeImpl
name|source
init|=
literal|null
decl_stmt|;
name|FsVolumeImpl
name|dest
init|=
literal|null
decl_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|Random
name|r
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
name|FileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|TestBalancer
operator|.
name|createFile
argument_list|(
name|cluster
argument_list|,
name|filePath
argument_list|,
name|fileLen
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|waitReplication
argument_list|(
name|fs
argument_list|,
name|filePath
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|restartDataNodes
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
comment|// Get the data node and move all data to one disk.
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numDatanodes
condition|;
name|i
operator|++
control|)
block|{
name|DataNode
name|dnNode
init|=
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
try|try
init|(
name|FsDatasetSpi
operator|.
name|FsVolumeReferences
name|refs
init|=
name|dnNode
operator|.
name|getFSDataset
argument_list|()
operator|.
name|getFsVolumeReferences
argument_list|()
init|)
block|{
name|source
operator|=
operator|(
name|FsVolumeImpl
operator|)
name|refs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|dest
operator|=
operator|(
name|FsVolumeImpl
operator|)
name|refs
operator|.
name|get
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|DiskBalancerTestUtil
operator|.
name|getBlockCount
argument_list|(
name|source
argument_list|,
literal|true
argument_list|)
operator|>
literal|0
argument_list|)
expr_stmt|;
name|DiskBalancerTestUtil
operator|.
name|moveAllDataToDestVolume
argument_list|(
name|dnNode
operator|.
name|getFSDataset
argument_list|()
argument_list|,
name|source
argument_list|,
name|dest
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|DiskBalancerTestUtil
operator|.
name|getBlockCount
argument_list|(
name|source
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|cluster
operator|.
name|restartDataNodes
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
return|return
name|cluster
return|;
block|}
comment|/**    * Moves all blocks to the destination volume.    *    * @param fsDataset - Dataset    * @param source    - Source Volume.    * @param dest      - Destination Volume.    * @throws IOException    */
DECL|method|moveAllDataToDestVolume (FsDatasetSpi fsDataset, FsVolumeSpi source, FsVolumeSpi dest)
specifier|public
specifier|static
name|void
name|moveAllDataToDestVolume
parameter_list|(
name|FsDatasetSpi
name|fsDataset
parameter_list|,
name|FsVolumeSpi
name|source
parameter_list|,
name|FsVolumeSpi
name|dest
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|String
name|blockPoolID
range|:
name|source
operator|.
name|getBlockPoolList
argument_list|()
control|)
block|{
name|FsVolumeSpi
operator|.
name|BlockIterator
name|sourceIter
init|=
name|source
operator|.
name|newBlockIterator
argument_list|(
name|blockPoolID
argument_list|,
literal|"TestDiskBalancerSource"
argument_list|)
decl_stmt|;
while|while
condition|(
operator|!
name|sourceIter
operator|.
name|atEnd
argument_list|()
condition|)
block|{
name|ExtendedBlock
name|block
init|=
name|sourceIter
operator|.
name|nextBlock
argument_list|()
decl_stmt|;
if|if
condition|(
name|block
operator|!=
literal|null
condition|)
block|{
name|fsDataset
operator|.
name|moveBlockAcrossVolumes
argument_list|(
name|block
argument_list|,
name|dest
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

