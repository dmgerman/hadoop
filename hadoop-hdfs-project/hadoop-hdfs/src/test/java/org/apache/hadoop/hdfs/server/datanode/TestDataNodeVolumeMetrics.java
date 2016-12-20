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
name|ArrayList
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
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|DataNodeVolumeMetrics
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
name|Rule
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

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|Timeout
import|;
end_import

begin_comment
comment|/**  * Test class for DataNodeVolumeMetrics.  */
end_comment

begin_class
DECL|class|TestDataNodeVolumeMetrics
specifier|public
class|class
name|TestDataNodeVolumeMetrics
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TestDataNodeVolumeMetrics
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|BLOCK_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|BLOCK_SIZE
init|=
literal|1024
decl_stmt|;
DECL|field|REPL
specifier|private
specifier|static
specifier|final
name|short
name|REPL
init|=
literal|1
decl_stmt|;
DECL|field|NUM_DATANODES
specifier|private
specifier|static
specifier|final
name|int
name|NUM_DATANODES
init|=
literal|1
decl_stmt|;
annotation|@
name|Rule
DECL|field|timeout
specifier|public
name|Timeout
name|timeout
init|=
operator|new
name|Timeout
argument_list|(
literal|300000
argument_list|)
decl_stmt|;
annotation|@
name|Test
DECL|method|testVolumeMetrics ()
specifier|public
name|void
name|testVolumeMetrics
parameter_list|()
throws|throws
name|Exception
block|{
name|MiniDFSCluster
name|cluster
init|=
name|setupClusterForVolumeMetrics
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
name|Path
name|fileName
init|=
operator|new
name|Path
argument_list|(
literal|"/test.dat"
argument_list|)
decl_stmt|;
specifier|final
name|long
name|fileLen
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
name|fileName
argument_list|,
literal|false
argument_list|,
name|BLOCK_SIZE
argument_list|,
name|fileLen
argument_list|,
name|fs
operator|.
name|getDefaultBlockSize
argument_list|(
name|fileName
argument_list|)
argument_list|,
name|REPL
argument_list|,
literal|1L
argument_list|,
literal|true
argument_list|)
expr_stmt|;
try|try
init|(
name|FSDataOutputStream
name|out
init|=
name|fs
operator|.
name|append
argument_list|(
name|fileName
argument_list|)
init|)
block|{
name|out
operator|.
name|writeBytes
argument_list|(
literal|"hello world"
argument_list|)
expr_stmt|;
operator|(
operator|(
name|DFSOutputStream
operator|)
name|out
operator|.
name|getWrappedStream
argument_list|()
operator|)
operator|.
name|hsync
argument_list|()
expr_stmt|;
block|}
name|verifyDataNodeVolumeMetrics
argument_list|(
name|fs
argument_list|,
name|cluster
argument_list|,
name|fileName
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
DECL|method|testVolumeMetricsWithVolumeDepartureArrival ()
specifier|public
name|void
name|testVolumeMetricsWithVolumeDepartureArrival
parameter_list|()
throws|throws
name|Exception
block|{
name|MiniDFSCluster
name|cluster
init|=
name|setupClusterForVolumeMetrics
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
name|Path
name|fileName
init|=
operator|new
name|Path
argument_list|(
literal|"/test.dat"
argument_list|)
decl_stmt|;
specifier|final
name|long
name|fileLen
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
name|fileName
argument_list|,
literal|false
argument_list|,
name|BLOCK_SIZE
argument_list|,
name|fileLen
argument_list|,
name|fs
operator|.
name|getDefaultBlockSize
argument_list|(
name|fileName
argument_list|)
argument_list|,
name|REPL
argument_list|,
literal|1L
argument_list|,
literal|true
argument_list|)
expr_stmt|;
try|try
init|(
name|FSDataOutputStream
name|out
init|=
name|fs
operator|.
name|append
argument_list|(
name|fileName
argument_list|)
init|)
block|{
name|out
operator|.
name|writeBytes
argument_list|(
literal|"hello world"
argument_list|)
expr_stmt|;
operator|(
operator|(
name|DFSOutputStream
operator|)
name|out
operator|.
name|getWrappedStream
argument_list|()
operator|)
operator|.
name|hsync
argument_list|()
expr_stmt|;
block|}
name|ArrayList
argument_list|<
name|DataNode
argument_list|>
name|dns
init|=
name|cluster
operator|.
name|getDataNodes
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"DN1 should be up"
argument_list|,
name|dns
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|isDatanodeUp
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|String
name|dataDir
init|=
name|cluster
operator|.
name|getDataDirectory
argument_list|()
decl_stmt|;
specifier|final
name|File
name|dn1Vol2
init|=
operator|new
name|File
argument_list|(
name|dataDir
argument_list|,
literal|"data2"
argument_list|)
decl_stmt|;
name|DataNodeTestUtils
operator|.
name|injectDataDirFailure
argument_list|(
name|dn1Vol2
argument_list|)
expr_stmt|;
name|verifyDataNodeVolumeMetrics
argument_list|(
name|fs
argument_list|,
name|cluster
argument_list|,
name|fileName
argument_list|)
expr_stmt|;
name|DataNodeTestUtils
operator|.
name|restoreDataDirFromFailure
argument_list|(
name|dn1Vol2
argument_list|)
expr_stmt|;
name|DataNodeTestUtils
operator|.
name|reconfigureDataNode
argument_list|(
name|dns
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|dn1Vol2
argument_list|)
expr_stmt|;
name|verifyDataNodeVolumeMetrics
argument_list|(
name|fs
argument_list|,
name|cluster
argument_list|,
name|fileName
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
DECL|method|setupClusterForVolumeMetrics ()
specifier|private
name|MiniDFSCluster
name|setupClusterForVolumeMetrics
parameter_list|()
throws|throws
name|IOException
block|{
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
name|DFS_DATANODE_FILE_IO_EVENTS_CLASS_KEY
argument_list|,
literal|"org.apache.hadoop.hdfs.server.datanode.ProfilingFileIoEvents"
argument_list|)
expr_stmt|;
name|SimulatedFSDataset
operator|.
name|setFactory
argument_list|(
name|conf
argument_list|)
expr_stmt|;
return|return
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
name|NUM_DATANODES
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
name|RAM_DISK
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
name|build
argument_list|()
return|;
block|}
DECL|method|verifyDataNodeVolumeMetrics (final FileSystem fs, final MiniDFSCluster cluster, final Path fileName)
specifier|private
name|void
name|verifyDataNodeVolumeMetrics
parameter_list|(
specifier|final
name|FileSystem
name|fs
parameter_list|,
specifier|final
name|MiniDFSCluster
name|cluster
parameter_list|,
specifier|final
name|Path
name|fileName
parameter_list|)
throws|throws
name|IOException
block|{
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
specifier|final
name|ExtendedBlock
name|block
init|=
name|DFSTestUtil
operator|.
name|getFirstBlock
argument_list|(
name|fs
argument_list|,
name|fileName
argument_list|)
decl_stmt|;
specifier|final
name|FsVolumeSpi
name|volume
init|=
name|datanode
operator|.
name|getFSDataset
argument_list|()
operator|.
name|getVolume
argument_list|(
name|block
argument_list|)
decl_stmt|;
name|DataNodeVolumeMetrics
name|metrics
init|=
name|volume
operator|.
name|getMetrics
argument_list|()
decl_stmt|;
name|MetricsRecordBuilder
name|rb
init|=
name|getMetrics
argument_list|(
name|volume
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
literal|"TotalDataFileIos"
argument_list|,
name|metrics
operator|.
name|getTotalDataFileIos
argument_list|()
argument_list|,
name|rb
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"TotalMetadataOperations : "
operator|+
name|metrics
operator|.
name|getTotalMetadataOperations
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"TotalDataFileIos : "
operator|+
name|metrics
operator|.
name|getTotalDataFileIos
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"TotalFileIoErrors : "
operator|+
name|metrics
operator|.
name|getTotalFileIoErrors
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"MetadataOperationSampleCount : "
operator|+
name|metrics
operator|.
name|getMetadataOperationSampleCount
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"MetadataOperationMean : "
operator|+
name|metrics
operator|.
name|getMetadataOperationMean
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"MetadataFileIoStdDev : "
operator|+
name|metrics
operator|.
name|getMetadataOperationStdDev
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"DataFileIoSampleCount : "
operator|+
name|metrics
operator|.
name|getDataFileIoSampleCount
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"DataFileIoMean : "
operator|+
name|metrics
operator|.
name|getDataFileIoMean
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"DataFileIoStdDev : "
operator|+
name|metrics
operator|.
name|getDataFileIoStdDev
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"flushIoSampleCount : "
operator|+
name|metrics
operator|.
name|getFlushIoSampleCount
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"flushIoMean : "
operator|+
name|metrics
operator|.
name|getFlushIoMean
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"flushIoStdDev : "
operator|+
name|metrics
operator|.
name|getFlushIoStdDev
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"syncIoSampleCount : "
operator|+
name|metrics
operator|.
name|getSyncIoSampleCount
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"syncIoMean : "
operator|+
name|metrics
operator|.
name|getSyncIoMean
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"syncIoStdDev : "
operator|+
name|metrics
operator|.
name|getSyncIoStdDev
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"readIoSampleCount : "
operator|+
name|metrics
operator|.
name|getReadIoMean
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"readIoMean : "
operator|+
name|metrics
operator|.
name|getReadIoMean
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"readIoStdDev : "
operator|+
name|metrics
operator|.
name|getReadIoStdDev
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"writeIoSampleCount : "
operator|+
name|metrics
operator|.
name|getWriteIoSampleCount
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"writeIoMean : "
operator|+
name|metrics
operator|.
name|getWriteIoMean
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"writeIoStdDev : "
operator|+
name|metrics
operator|.
name|getWriteIoStdDev
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"fileIoErrorSampleCount : "
operator|+
name|metrics
operator|.
name|getFileIoErrorSampleCount
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"fileIoErrorMean : "
operator|+
name|metrics
operator|.
name|getFileIoErrorMean
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"fileIoErrorStdDev : "
operator|+
name|metrics
operator|.
name|getFileIoErrorStdDev
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

