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
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|test
operator|.
name|MetricsAsserts
operator|.
name|assertCounter
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|test
operator|.
name|MetricsAsserts
operator|.
name|assertQuantileGauges
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|test
operator|.
name|MetricsAsserts
operator|.
name|getLongCounter
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|test
operator|.
name|MetricsAsserts
operator|.
name|getMetrics
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
name|assertNotNull
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
name|FSDataOutputStream
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
name|DFSOutputStream
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
name|DistributedFileSystem
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
name|metrics2
operator|.
name|MetricsRecordBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_class
DECL|class|TestDataNodeMetrics
specifier|public
class|class
name|TestDataNodeMetrics
block|{
annotation|@
name|Test
DECL|method|testDataNodeMetrics ()
specifier|public
name|void
name|testDataNodeMetrics
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|SimulatedFSDataset
operator|.
name|setFactory
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|MiniDFSCluster
name|cluster
init|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
name|FileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
specifier|final
name|long
name|LONG_FILE_LEN
init|=
name|Integer
operator|.
name|MAX_VALUE
operator|+
literal|1L
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
operator|new
name|Path
argument_list|(
literal|"/tmp.txt"
argument_list|)
argument_list|,
name|LONG_FILE_LEN
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
literal|1L
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|DataNode
argument_list|>
name|datanodes
init|=
name|cluster
operator|.
name|getDataNodes
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|datanodes
operator|.
name|size
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|DataNode
name|datanode
init|=
name|datanodes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|MetricsRecordBuilder
name|rb
init|=
name|getMetrics
argument_list|(
name|datanode
operator|.
name|getMetrics
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
name|assertCounter
argument_list|(
literal|"BytesWritten"
argument_list|,
name|LONG_FILE_LEN
argument_list|,
name|rb
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|cluster
operator|!=
literal|null
condition|)
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testSendDataPacketMetrics ()
specifier|public
name|void
name|testSendDataPacketMetrics
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
specifier|final
name|int
name|interval
init|=
literal|1
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_METRICS_PERCENTILES_INTERVALS_KEY
argument_list|,
literal|""
operator|+
name|interval
argument_list|)
expr_stmt|;
name|MiniDFSCluster
name|cluster
init|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
name|FileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
comment|// Create and read a 1 byte file
name|Path
name|tmpfile
init|=
operator|new
name|Path
argument_list|(
literal|"/tmp.txt"
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|tmpfile
argument_list|,
operator|(
name|long
operator|)
literal|1
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
literal|1L
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|readFile
argument_list|(
name|fs
argument_list|,
name|tmpfile
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|DataNode
argument_list|>
name|datanodes
init|=
name|cluster
operator|.
name|getDataNodes
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|datanodes
operator|.
name|size
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|DataNode
name|datanode
init|=
name|datanodes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|MetricsRecordBuilder
name|rb
init|=
name|getMetrics
argument_list|(
name|datanode
operator|.
name|getMetrics
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
comment|// Expect 2 packets, 1 for the 1 byte read, 1 for the empty packet
comment|// signaling the end of the block
name|assertCounter
argument_list|(
literal|"SendDataPacketTransferNanosNumOps"
argument_list|,
operator|(
name|long
operator|)
literal|2
argument_list|,
name|rb
argument_list|)
expr_stmt|;
name|assertCounter
argument_list|(
literal|"SendDataPacketBlockedOnNetworkNanosNumOps"
argument_list|,
operator|(
name|long
operator|)
literal|2
argument_list|,
name|rb
argument_list|)
expr_stmt|;
comment|// Wait for at least 1 rollover
name|Thread
operator|.
name|sleep
argument_list|(
operator|(
name|interval
operator|+
literal|1
operator|)
operator|*
literal|1000
argument_list|)
expr_stmt|;
comment|// Check that the sendPacket percentiles rolled to non-zero values
name|String
name|sec
init|=
name|interval
operator|+
literal|"s"
decl_stmt|;
name|assertQuantileGauges
argument_list|(
literal|"SendDataPacketBlockedOnNetworkNanos"
operator|+
name|sec
argument_list|,
name|rb
argument_list|)
expr_stmt|;
name|assertQuantileGauges
argument_list|(
literal|"SendDataPacketTransferNanos"
operator|+
name|sec
argument_list|,
name|rb
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|cluster
operator|!=
literal|null
condition|)
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testReceivePacketMetrics ()
specifier|public
name|void
name|testReceivePacketMetrics
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
specifier|final
name|int
name|interval
init|=
literal|1
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_METRICS_PERCENTILES_INTERVALS_KEY
argument_list|,
literal|""
operator|+
name|interval
argument_list|)
expr_stmt|;
name|MiniDFSCluster
name|cluster
init|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|DistributedFileSystem
name|fs
init|=
operator|(
name|DistributedFileSystem
operator|)
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|Path
name|testFile
init|=
operator|new
name|Path
argument_list|(
literal|"/testFlushNanosMetric.txt"
argument_list|)
decl_stmt|;
name|FSDataOutputStream
name|fout
init|=
name|fs
operator|.
name|create
argument_list|(
name|testFile
argument_list|)
decl_stmt|;
name|fout
operator|.
name|write
argument_list|(
operator|new
name|byte
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|fout
operator|.
name|hsync
argument_list|()
expr_stmt|;
name|fout
operator|.
name|close
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|DataNode
argument_list|>
name|datanodes
init|=
name|cluster
operator|.
name|getDataNodes
argument_list|()
decl_stmt|;
name|DataNode
name|datanode
init|=
name|datanodes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|MetricsRecordBuilder
name|dnMetrics
init|=
name|getMetrics
argument_list|(
name|datanode
operator|.
name|getMetrics
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
comment|// Expect two flushes, 1 for the flush that occurs after writing,
comment|// 1 that occurs on closing the data and metadata files.
name|assertCounter
argument_list|(
literal|"FlushNanosNumOps"
argument_list|,
literal|2L
argument_list|,
name|dnMetrics
argument_list|)
expr_stmt|;
comment|// Expect two syncs, one from the hsync, one on close.
name|assertCounter
argument_list|(
literal|"FsyncNanosNumOps"
argument_list|,
literal|2L
argument_list|,
name|dnMetrics
argument_list|)
expr_stmt|;
comment|// Wait for at least 1 rollover
name|Thread
operator|.
name|sleep
argument_list|(
operator|(
name|interval
operator|+
literal|1
operator|)
operator|*
literal|1000
argument_list|)
expr_stmt|;
comment|// Check the receivePacket percentiles that should be non-zero
name|String
name|sec
init|=
name|interval
operator|+
literal|"s"
decl_stmt|;
name|assertQuantileGauges
argument_list|(
literal|"FlushNanos"
operator|+
name|sec
argument_list|,
name|dnMetrics
argument_list|)
expr_stmt|;
name|assertQuantileGauges
argument_list|(
literal|"FsyncNanos"
operator|+
name|sec
argument_list|,
name|dnMetrics
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|cluster
operator|!=
literal|null
condition|)
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Tests that round-trip acks in a datanode write pipeline are correctly     * measured.     */
annotation|@
name|Test
DECL|method|testRoundTripAckMetric ()
specifier|public
name|void
name|testRoundTripAckMetric
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|datanodeCount
init|=
literal|2
decl_stmt|;
specifier|final
name|int
name|interval
init|=
literal|1
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_METRICS_PERCENTILES_INTERVALS_KEY
argument_list|,
literal|""
operator|+
name|interval
argument_list|)
expr_stmt|;
name|MiniDFSCluster
name|cluster
init|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|numDataNodes
argument_list|(
name|datanodeCount
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|FileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
comment|// Open a file and get the head of the pipeline
name|Path
name|testFile
init|=
operator|new
name|Path
argument_list|(
literal|"/testRoundTripAckMetric.txt"
argument_list|)
decl_stmt|;
name|FSDataOutputStream
name|fsout
init|=
name|fs
operator|.
name|create
argument_list|(
name|testFile
argument_list|,
operator|(
name|short
operator|)
name|datanodeCount
argument_list|)
decl_stmt|;
name|DFSOutputStream
name|dout
init|=
operator|(
name|DFSOutputStream
operator|)
name|fsout
operator|.
name|getWrappedStream
argument_list|()
decl_stmt|;
comment|// Slow down the writes to catch the write pipeline
name|dout
operator|.
name|setChunksPerPacket
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|dout
operator|.
name|setArtificialSlowdown
argument_list|(
literal|3000
argument_list|)
expr_stmt|;
name|fsout
operator|.
name|write
argument_list|(
operator|new
name|byte
index|[
literal|10000
index|]
argument_list|)
expr_stmt|;
name|DatanodeInfo
index|[]
name|pipeline
init|=
literal|null
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|pipeline
operator|==
literal|null
operator|&&
name|count
operator|<
literal|5
condition|)
block|{
name|pipeline
operator|=
name|dout
operator|.
name|getPipeline
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Waiting for pipeline to be created."
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
comment|// Get the head node that should be receiving downstream acks
name|DatanodeInfo
name|headInfo
init|=
name|pipeline
index|[
literal|0
index|]
decl_stmt|;
name|DataNode
name|headNode
init|=
literal|null
decl_stmt|;
for|for
control|(
name|DataNode
name|datanode
range|:
name|cluster
operator|.
name|getDataNodes
argument_list|()
control|)
block|{
if|if
condition|(
name|datanode
operator|.
name|getDatanodeId
argument_list|()
operator|.
name|equals
argument_list|(
name|headInfo
argument_list|)
condition|)
block|{
name|headNode
operator|=
name|datanode
expr_stmt|;
break|break;
block|}
block|}
name|assertNotNull
argument_list|(
literal|"Could not find the head of the datanode write pipeline"
argument_list|,
name|headNode
argument_list|)
expr_stmt|;
comment|// Close the file and wait for the metrics to rollover
name|Thread
operator|.
name|sleep
argument_list|(
operator|(
name|interval
operator|+
literal|1
operator|)
operator|*
literal|1000
argument_list|)
expr_stmt|;
comment|// Check the ack was received
name|MetricsRecordBuilder
name|dnMetrics
init|=
name|getMetrics
argument_list|(
name|headNode
operator|.
name|getMetrics
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Expected non-zero number of acks"
argument_list|,
name|getLongCounter
argument_list|(
literal|"PacketAckRoundTripTimeNanosNumOps"
argument_list|,
name|dnMetrics
argument_list|)
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertQuantileGauges
argument_list|(
literal|"PacketAckRoundTripTimeNanos"
operator|+
name|interval
operator|+
literal|"s"
argument_list|,
name|dnMetrics
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|cluster
operator|!=
literal|null
condition|)
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

