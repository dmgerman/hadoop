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
name|java
operator|.
name|io
operator|.
name|DataInputStream
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
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|HdfsConfiguration
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
name|Block
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
name|BlockListAsLongs
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
name|FSDatasetInterface
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
name|SimulatedFSDataset
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
name|FSDatasetInterface
operator|.
name|BlockWriteStreams
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
name|DataChecksum
import|;
end_import

begin_comment
comment|/**  * this class tests the methods of the  SimulatedFSDataset.  */
end_comment

begin_class
DECL|class|TestSimulatedFSDataset
specifier|public
class|class
name|TestSimulatedFSDataset
extends|extends
name|TestCase
block|{
DECL|field|conf
name|Configuration
name|conf
init|=
literal|null
decl_stmt|;
DECL|field|bpid
specifier|static
specifier|final
name|String
name|bpid
init|=
literal|"BP-TEST"
decl_stmt|;
DECL|field|NUMBLOCKS
specifier|static
specifier|final
name|int
name|NUMBLOCKS
init|=
literal|20
decl_stmt|;
DECL|field|BLOCK_LENGTH_MULTIPLIER
specifier|static
specifier|final
name|int
name|BLOCK_LENGTH_MULTIPLIER
init|=
literal|79
decl_stmt|;
DECL|method|setUp ()
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|conf
operator|=
operator|new
name|HdfsConfiguration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|SimulatedFSDataset
operator|.
name|CONFIG_PROPERTY_SIMULATED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|tearDown ()
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
DECL|method|blockIdToLen (long blkid)
name|long
name|blockIdToLen
parameter_list|(
name|long
name|blkid
parameter_list|)
block|{
return|return
name|blkid
operator|*
name|BLOCK_LENGTH_MULTIPLIER
return|;
block|}
DECL|method|addSomeBlocks (FSDatasetInterface fsdataset, int startingBlockId)
name|int
name|addSomeBlocks
parameter_list|(
name|FSDatasetInterface
name|fsdataset
parameter_list|,
name|int
name|startingBlockId
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|bytesAdded
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|startingBlockId
init|;
name|i
operator|<
name|startingBlockId
operator|+
name|NUMBLOCKS
condition|;
operator|++
name|i
control|)
block|{
name|ExtendedBlock
name|b
init|=
operator|new
name|ExtendedBlock
argument_list|(
name|bpid
argument_list|,
name|i
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
decl_stmt|;
comment|// we pass expected len as zero, - fsdataset should use the sizeof actual
comment|// data written
name|ReplicaInPipelineInterface
name|bInfo
init|=
name|fsdataset
operator|.
name|createRbw
argument_list|(
name|b
argument_list|)
decl_stmt|;
name|BlockWriteStreams
name|out
init|=
name|bInfo
operator|.
name|createStreams
argument_list|(
literal|true
argument_list|,
name|DataChecksum
operator|.
name|newDataChecksum
argument_list|(
name|DataChecksum
operator|.
name|CHECKSUM_CRC32
argument_list|,
literal|512
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|OutputStream
name|dataOut
init|=
name|out
operator|.
name|dataOut
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|fsdataset
operator|.
name|getLength
argument_list|(
name|b
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|1
init|;
name|j
operator|<=
name|blockIdToLen
argument_list|(
name|i
argument_list|)
condition|;
operator|++
name|j
control|)
block|{
name|dataOut
operator|.
name|write
argument_list|(
name|j
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|j
argument_list|,
name|bInfo
operator|.
name|getBytesOnDisk
argument_list|()
argument_list|)
expr_stmt|;
comment|// correct length even as we write
name|bytesAdded
operator|++
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|b
operator|.
name|setNumBytes
argument_list|(
name|blockIdToLen
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|fsdataset
operator|.
name|finalizeBlock
argument_list|(
name|b
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|blockIdToLen
argument_list|(
name|i
argument_list|)
argument_list|,
name|fsdataset
operator|.
name|getLength
argument_list|(
name|b
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|bytesAdded
return|;
block|}
DECL|method|addSomeBlocks (FSDatasetInterface fsdataset )
name|int
name|addSomeBlocks
parameter_list|(
name|FSDatasetInterface
name|fsdataset
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|addSomeBlocks
argument_list|(
name|fsdataset
argument_list|,
literal|1
argument_list|)
return|;
block|}
DECL|method|testGetMetaData ()
specifier|public
name|void
name|testGetMetaData
parameter_list|()
throws|throws
name|IOException
block|{
name|FSDatasetInterface
name|fsdataset
init|=
name|getSimulatedFSDataset
argument_list|()
decl_stmt|;
name|ExtendedBlock
name|b
init|=
operator|new
name|ExtendedBlock
argument_list|(
name|bpid
argument_list|,
literal|1
argument_list|,
literal|5
argument_list|,
literal|0
argument_list|)
decl_stmt|;
try|try
block|{
name|assertFalse
argument_list|(
name|fsdataset
operator|.
name|metaFileExists
argument_list|(
name|b
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Expected an IO exception"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// ok - as expected
block|}
name|addSomeBlocks
argument_list|(
name|fsdataset
argument_list|)
expr_stmt|;
comment|// Only need to add one but ....
name|b
operator|=
operator|new
name|ExtendedBlock
argument_list|(
name|bpid
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|InputStream
name|metaInput
init|=
name|fsdataset
operator|.
name|getMetaDataInputStream
argument_list|(
name|b
argument_list|)
decl_stmt|;
name|DataInputStream
name|metaDataInput
init|=
operator|new
name|DataInputStream
argument_list|(
name|metaInput
argument_list|)
decl_stmt|;
name|short
name|version
init|=
name|metaDataInput
operator|.
name|readShort
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|FSDataset
operator|.
name|METADATA_VERSION
argument_list|,
name|version
argument_list|)
expr_stmt|;
name|DataChecksum
name|checksum
init|=
name|DataChecksum
operator|.
name|newDataChecksum
argument_list|(
name|metaDataInput
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|DataChecksum
operator|.
name|CHECKSUM_NULL
argument_list|,
name|checksum
operator|.
name|getChecksumType
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|checksum
operator|.
name|getChecksumSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testStorageUsage ()
specifier|public
name|void
name|testStorageUsage
parameter_list|()
throws|throws
name|IOException
block|{
name|FSDatasetInterface
name|fsdataset
init|=
name|getSimulatedFSDataset
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|fsdataset
operator|.
name|getDfsUsed
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|fsdataset
operator|.
name|getRemaining
argument_list|()
argument_list|,
name|fsdataset
operator|.
name|getCapacity
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|bytesAdded
init|=
name|addSomeBlocks
argument_list|(
name|fsdataset
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|bytesAdded
argument_list|,
name|fsdataset
operator|.
name|getDfsUsed
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|fsdataset
operator|.
name|getCapacity
argument_list|()
operator|-
name|bytesAdded
argument_list|,
name|fsdataset
operator|.
name|getRemaining
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|checkBlockDataAndSize (FSDatasetInterface fsdataset, ExtendedBlock b, long expectedLen)
name|void
name|checkBlockDataAndSize
parameter_list|(
name|FSDatasetInterface
name|fsdataset
parameter_list|,
name|ExtendedBlock
name|b
parameter_list|,
name|long
name|expectedLen
parameter_list|)
throws|throws
name|IOException
block|{
name|InputStream
name|input
init|=
name|fsdataset
operator|.
name|getBlockInputStream
argument_list|(
name|b
argument_list|)
decl_stmt|;
name|long
name|lengthRead
init|=
literal|0
decl_stmt|;
name|int
name|data
decl_stmt|;
while|while
condition|(
operator|(
name|data
operator|=
name|input
operator|.
name|read
argument_list|()
operator|)
operator|!=
operator|-
literal|1
condition|)
block|{
name|assertEquals
argument_list|(
name|SimulatedFSDataset
operator|.
name|DEFAULT_DATABYTE
argument_list|,
name|data
argument_list|)
expr_stmt|;
name|lengthRead
operator|++
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|expectedLen
argument_list|,
name|lengthRead
argument_list|)
expr_stmt|;
block|}
DECL|method|testWriteRead ()
specifier|public
name|void
name|testWriteRead
parameter_list|()
throws|throws
name|IOException
block|{
name|FSDatasetInterface
name|fsdataset
init|=
name|getSimulatedFSDataset
argument_list|()
decl_stmt|;
name|addSomeBlocks
argument_list|(
name|fsdataset
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|NUMBLOCKS
condition|;
operator|++
name|i
control|)
block|{
name|ExtendedBlock
name|b
init|=
operator|new
name|ExtendedBlock
argument_list|(
name|bpid
argument_list|,
name|i
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|fsdataset
operator|.
name|isValidBlock
argument_list|(
name|b
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|blockIdToLen
argument_list|(
name|i
argument_list|)
argument_list|,
name|fsdataset
operator|.
name|getLength
argument_list|(
name|b
argument_list|)
argument_list|)
expr_stmt|;
name|checkBlockDataAndSize
argument_list|(
name|fsdataset
argument_list|,
name|b
argument_list|,
name|blockIdToLen
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testGetBlockReport ()
specifier|public
name|void
name|testGetBlockReport
parameter_list|()
throws|throws
name|IOException
block|{
name|SimulatedFSDataset
name|fsdataset
init|=
name|getSimulatedFSDataset
argument_list|()
decl_stmt|;
name|BlockListAsLongs
name|blockReport
init|=
name|fsdataset
operator|.
name|getBlockReport
argument_list|(
name|bpid
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|blockReport
operator|.
name|getNumberOfBlocks
argument_list|()
argument_list|)
expr_stmt|;
name|addSomeBlocks
argument_list|(
name|fsdataset
argument_list|)
expr_stmt|;
name|blockReport
operator|=
name|fsdataset
operator|.
name|getBlockReport
argument_list|(
name|bpid
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NUMBLOCKS
argument_list|,
name|blockReport
operator|.
name|getNumberOfBlocks
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Block
name|b
range|:
name|blockReport
control|)
block|{
name|assertNotNull
argument_list|(
name|b
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|blockIdToLen
argument_list|(
name|b
operator|.
name|getBlockId
argument_list|()
argument_list|)
argument_list|,
name|b
operator|.
name|getNumBytes
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testInjectionEmpty ()
specifier|public
name|void
name|testInjectionEmpty
parameter_list|()
throws|throws
name|IOException
block|{
name|SimulatedFSDataset
name|fsdataset
init|=
name|getSimulatedFSDataset
argument_list|()
decl_stmt|;
name|BlockListAsLongs
name|blockReport
init|=
name|fsdataset
operator|.
name|getBlockReport
argument_list|(
name|bpid
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|blockReport
operator|.
name|getNumberOfBlocks
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|bytesAdded
init|=
name|addSomeBlocks
argument_list|(
name|fsdataset
argument_list|)
decl_stmt|;
name|blockReport
operator|=
name|fsdataset
operator|.
name|getBlockReport
argument_list|(
name|bpid
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NUMBLOCKS
argument_list|,
name|blockReport
operator|.
name|getNumberOfBlocks
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Block
name|b
range|:
name|blockReport
control|)
block|{
name|assertNotNull
argument_list|(
name|b
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|blockIdToLen
argument_list|(
name|b
operator|.
name|getBlockId
argument_list|()
argument_list|)
argument_list|,
name|b
operator|.
name|getNumBytes
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Inject blocks into an empty fsdataset
comment|//  - injecting the blocks we got above.
name|SimulatedFSDataset
name|sfsdataset
init|=
name|getSimulatedFSDataset
argument_list|()
decl_stmt|;
name|sfsdataset
operator|.
name|injectBlocks
argument_list|(
name|bpid
argument_list|,
name|blockReport
argument_list|)
expr_stmt|;
name|blockReport
operator|=
name|sfsdataset
operator|.
name|getBlockReport
argument_list|(
name|bpid
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NUMBLOCKS
argument_list|,
name|blockReport
operator|.
name|getNumberOfBlocks
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Block
name|b
range|:
name|blockReport
control|)
block|{
name|assertNotNull
argument_list|(
name|b
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|blockIdToLen
argument_list|(
name|b
operator|.
name|getBlockId
argument_list|()
argument_list|)
argument_list|,
name|b
operator|.
name|getNumBytes
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|blockIdToLen
argument_list|(
name|b
operator|.
name|getBlockId
argument_list|()
argument_list|)
argument_list|,
name|sfsdataset
operator|.
name|getLength
argument_list|(
operator|new
name|ExtendedBlock
argument_list|(
name|bpid
argument_list|,
name|b
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|bytesAdded
argument_list|,
name|sfsdataset
operator|.
name|getDfsUsed
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|sfsdataset
operator|.
name|getCapacity
argument_list|()
operator|-
name|bytesAdded
argument_list|,
name|sfsdataset
operator|.
name|getRemaining
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testInjectionNonEmpty ()
specifier|public
name|void
name|testInjectionNonEmpty
parameter_list|()
throws|throws
name|IOException
block|{
name|SimulatedFSDataset
name|fsdataset
init|=
name|getSimulatedFSDataset
argument_list|()
decl_stmt|;
name|BlockListAsLongs
name|blockReport
init|=
name|fsdataset
operator|.
name|getBlockReport
argument_list|(
name|bpid
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|blockReport
operator|.
name|getNumberOfBlocks
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|bytesAdded
init|=
name|addSomeBlocks
argument_list|(
name|fsdataset
argument_list|)
decl_stmt|;
name|blockReport
operator|=
name|fsdataset
operator|.
name|getBlockReport
argument_list|(
name|bpid
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NUMBLOCKS
argument_list|,
name|blockReport
operator|.
name|getNumberOfBlocks
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Block
name|b
range|:
name|blockReport
control|)
block|{
name|assertNotNull
argument_list|(
name|b
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|blockIdToLen
argument_list|(
name|b
operator|.
name|getBlockId
argument_list|()
argument_list|)
argument_list|,
name|b
operator|.
name|getNumBytes
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|fsdataset
operator|=
literal|null
expr_stmt|;
comment|// Inject blocks into an non-empty fsdataset
comment|//  - injecting the blocks we got above.
name|SimulatedFSDataset
name|sfsdataset
init|=
name|getSimulatedFSDataset
argument_list|()
decl_stmt|;
comment|// Add come blocks whose block ids do not conflict with
comment|// the ones we are going to inject.
name|bytesAdded
operator|+=
name|addSomeBlocks
argument_list|(
name|sfsdataset
argument_list|,
name|NUMBLOCKS
operator|+
literal|1
argument_list|)
expr_stmt|;
name|sfsdataset
operator|.
name|getBlockReport
argument_list|(
name|bpid
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NUMBLOCKS
argument_list|,
name|blockReport
operator|.
name|getNumberOfBlocks
argument_list|()
argument_list|)
expr_stmt|;
name|sfsdataset
operator|.
name|getBlockReport
argument_list|(
name|bpid
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NUMBLOCKS
argument_list|,
name|blockReport
operator|.
name|getNumberOfBlocks
argument_list|()
argument_list|)
expr_stmt|;
name|sfsdataset
operator|.
name|injectBlocks
argument_list|(
name|bpid
argument_list|,
name|blockReport
argument_list|)
expr_stmt|;
name|blockReport
operator|=
name|sfsdataset
operator|.
name|getBlockReport
argument_list|(
name|bpid
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NUMBLOCKS
operator|*
literal|2
argument_list|,
name|blockReport
operator|.
name|getNumberOfBlocks
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Block
name|b
range|:
name|blockReport
control|)
block|{
name|assertNotNull
argument_list|(
name|b
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|blockIdToLen
argument_list|(
name|b
operator|.
name|getBlockId
argument_list|()
argument_list|)
argument_list|,
name|b
operator|.
name|getNumBytes
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|blockIdToLen
argument_list|(
name|b
operator|.
name|getBlockId
argument_list|()
argument_list|)
argument_list|,
name|sfsdataset
operator|.
name|getLength
argument_list|(
operator|new
name|ExtendedBlock
argument_list|(
name|bpid
argument_list|,
name|b
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|bytesAdded
argument_list|,
name|sfsdataset
operator|.
name|getDfsUsed
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|sfsdataset
operator|.
name|getCapacity
argument_list|()
operator|-
name|bytesAdded
argument_list|,
name|sfsdataset
operator|.
name|getRemaining
argument_list|()
argument_list|)
expr_stmt|;
comment|// Now test that the dataset cannot be created if it does not have sufficient cap
name|conf
operator|.
name|setLong
argument_list|(
name|SimulatedFSDataset
operator|.
name|CONFIG_PROPERTY_CAPACITY
argument_list|,
literal|10
argument_list|)
expr_stmt|;
try|try
block|{
name|sfsdataset
operator|=
name|getSimulatedFSDataset
argument_list|()
expr_stmt|;
name|sfsdataset
operator|.
name|addBlockPool
argument_list|(
name|bpid
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|sfsdataset
operator|.
name|injectBlocks
argument_list|(
name|bpid
argument_list|,
name|blockReport
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Expected an IO exception"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// ok - as expected
block|}
block|}
DECL|method|checkInvalidBlock (ExtendedBlock b)
specifier|public
name|void
name|checkInvalidBlock
parameter_list|(
name|ExtendedBlock
name|b
parameter_list|)
throws|throws
name|IOException
block|{
name|FSDatasetInterface
name|fsdataset
init|=
name|getSimulatedFSDataset
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|fsdataset
operator|.
name|isValidBlock
argument_list|(
name|b
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|fsdataset
operator|.
name|getLength
argument_list|(
name|b
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Expected an IO exception"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// ok - as expected
block|}
try|try
block|{
name|fsdataset
operator|.
name|getBlockInputStream
argument_list|(
name|b
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Expected an IO exception"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// ok - as expected
block|}
try|try
block|{
name|fsdataset
operator|.
name|finalizeBlock
argument_list|(
name|b
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Expected an IO exception"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// ok - as expected
block|}
block|}
DECL|method|testInValidBlocks ()
specifier|public
name|void
name|testInValidBlocks
parameter_list|()
throws|throws
name|IOException
block|{
name|FSDatasetInterface
name|fsdataset
init|=
name|getSimulatedFSDataset
argument_list|()
decl_stmt|;
name|ExtendedBlock
name|b
init|=
operator|new
name|ExtendedBlock
argument_list|(
name|bpid
argument_list|,
literal|1
argument_list|,
literal|5
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|checkInvalidBlock
argument_list|(
name|b
argument_list|)
expr_stmt|;
comment|// Now check invlaid after adding some blocks
name|addSomeBlocks
argument_list|(
name|fsdataset
argument_list|)
expr_stmt|;
name|b
operator|=
operator|new
name|ExtendedBlock
argument_list|(
name|bpid
argument_list|,
name|NUMBLOCKS
operator|+
literal|99
argument_list|,
literal|5
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|checkInvalidBlock
argument_list|(
name|b
argument_list|)
expr_stmt|;
block|}
DECL|method|testInvalidate ()
specifier|public
name|void
name|testInvalidate
parameter_list|()
throws|throws
name|IOException
block|{
name|FSDatasetInterface
name|fsdataset
init|=
name|getSimulatedFSDataset
argument_list|()
decl_stmt|;
name|int
name|bytesAdded
init|=
name|addSomeBlocks
argument_list|(
name|fsdataset
argument_list|)
decl_stmt|;
name|Block
index|[]
name|deleteBlocks
init|=
operator|new
name|Block
index|[
literal|2
index|]
decl_stmt|;
name|deleteBlocks
index|[
literal|0
index|]
operator|=
operator|new
name|Block
argument_list|(
literal|1
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|deleteBlocks
index|[
literal|1
index|]
operator|=
operator|new
name|Block
argument_list|(
literal|2
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|fsdataset
operator|.
name|invalidate
argument_list|(
name|bpid
argument_list|,
name|deleteBlocks
argument_list|)
expr_stmt|;
name|checkInvalidBlock
argument_list|(
operator|new
name|ExtendedBlock
argument_list|(
name|bpid
argument_list|,
name|deleteBlocks
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|checkInvalidBlock
argument_list|(
operator|new
name|ExtendedBlock
argument_list|(
name|bpid
argument_list|,
name|deleteBlocks
index|[
literal|1
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|long
name|sizeDeleted
init|=
name|blockIdToLen
argument_list|(
literal|1
argument_list|)
operator|+
name|blockIdToLen
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|bytesAdded
operator|-
name|sizeDeleted
argument_list|,
name|fsdataset
operator|.
name|getDfsUsed
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|fsdataset
operator|.
name|getCapacity
argument_list|()
operator|-
name|bytesAdded
operator|+
name|sizeDeleted
argument_list|,
name|fsdataset
operator|.
name|getRemaining
argument_list|()
argument_list|)
expr_stmt|;
comment|// Now make sure the rest of the blocks are valid
for|for
control|(
name|int
name|i
init|=
literal|3
init|;
name|i
operator|<=
name|NUMBLOCKS
condition|;
operator|++
name|i
control|)
block|{
name|Block
name|b
init|=
operator|new
name|Block
argument_list|(
name|i
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|fsdataset
operator|.
name|isValidBlock
argument_list|(
operator|new
name|ExtendedBlock
argument_list|(
name|bpid
argument_list|,
name|b
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getSimulatedFSDataset ()
specifier|private
name|SimulatedFSDataset
name|getSimulatedFSDataset
parameter_list|()
throws|throws
name|IOException
block|{
name|SimulatedFSDataset
name|fsdataset
init|=
operator|new
name|SimulatedFSDataset
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|fsdataset
operator|.
name|addBlockPool
argument_list|(
name|bpid
argument_list|,
name|conf
argument_list|)
expr_stmt|;
return|return
name|fsdataset
return|;
block|}
block|}
end_class

end_unit

