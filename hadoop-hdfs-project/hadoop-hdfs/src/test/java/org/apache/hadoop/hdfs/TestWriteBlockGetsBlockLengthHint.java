begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
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
name|*
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
name|test
operator|.
name|GenericTestUtils
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
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|core
operator|.
name|Is
operator|.
name|is
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
name|*
import|;
end_import

begin_comment
comment|/**  * Test to verify that the DFSClient passes the expected block length to  * the DataNode via DataTransferProtocol.  */
end_comment

begin_class
DECL|class|TestWriteBlockGetsBlockLengthHint
specifier|public
class|class
name|TestWriteBlockGetsBlockLengthHint
block|{
DECL|field|DEFAULT_BLOCK_LENGTH
specifier|static
specifier|final
name|long
name|DEFAULT_BLOCK_LENGTH
init|=
literal|1024
decl_stmt|;
DECL|field|EXPECTED_BLOCK_LENGTH
specifier|static
specifier|final
name|long
name|EXPECTED_BLOCK_LENGTH
init|=
name|DEFAULT_BLOCK_LENGTH
operator|*
literal|2
decl_stmt|;
annotation|@
name|Test
DECL|method|blockLengthHintIsPropagated ()
specifier|public
name|void
name|blockLengthHintIsPropagated
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|String
name|METHOD_NAME
init|=
name|GenericTestUtils
operator|.
name|getMethodName
argument_list|()
decl_stmt|;
specifier|final
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
literal|"/"
operator|+
name|METHOD_NAME
operator|+
literal|".dat"
argument_list|)
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|FsDatasetChecker
operator|.
name|setFactory
argument_list|(
name|conf
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
name|DEFAULT_BLOCK_LENGTH
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_SCAN_PERIOD_HOURS_KEY
argument_list|,
operator|-
literal|1
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
literal|1
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
comment|// FsDatasetChecker#createRbw asserts during block creation if the test
comment|// fails.
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|cluster
operator|.
name|getFileSystem
argument_list|()
argument_list|,
name|path
argument_list|,
literal|4096
argument_list|,
comment|// Buffer size.
name|EXPECTED_BLOCK_LENGTH
argument_list|,
name|EXPECTED_BLOCK_LENGTH
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
literal|0x1BAD5EED
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|FsDatasetChecker
specifier|static
class|class
name|FsDatasetChecker
extends|extends
name|SimulatedFSDataset
block|{
DECL|class|Factory
specifier|static
class|class
name|Factory
extends|extends
name|FsDatasetSpi
operator|.
name|Factory
argument_list|<
name|SimulatedFSDataset
argument_list|>
block|{
annotation|@
name|Override
DECL|method|newInstance (DataNode datanode, DataStorage storage, Configuration conf)
specifier|public
name|SimulatedFSDataset
name|newInstance
parameter_list|(
name|DataNode
name|datanode
parameter_list|,
name|DataStorage
name|storage
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|FsDatasetChecker
argument_list|(
name|storage
argument_list|,
name|conf
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|isSimulated ()
specifier|public
name|boolean
name|isSimulated
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
DECL|method|setFactory (Configuration conf)
specifier|public
specifier|static
name|void
name|setFactory
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_FSDATASET_FACTORY_KEY
argument_list|,
name|Factory
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|FsDatasetChecker (DataStorage storage, Configuration conf)
specifier|public
name|FsDatasetChecker
parameter_list|(
name|DataStorage
name|storage
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|super
argument_list|(
name|storage
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
comment|/**      * Override createRbw to verify that the block length that is passed      * is correct. This requires both DFSOutputStream and BlockReceiver to      * correctly propagate the hint to FsDatasetSpi.      */
annotation|@
name|Override
DECL|method|createRbw ( StorageType storageType, ExtendedBlock b, boolean allowLazyPersist)
specifier|public
specifier|synchronized
name|ReplicaHandler
name|createRbw
parameter_list|(
name|StorageType
name|storageType
parameter_list|,
name|ExtendedBlock
name|b
parameter_list|,
name|boolean
name|allowLazyPersist
parameter_list|)
throws|throws
name|IOException
block|{
name|assertThat
argument_list|(
name|b
operator|.
name|getLocalBlock
argument_list|()
operator|.
name|getNumBytes
argument_list|()
argument_list|,
name|is
argument_list|(
name|EXPECTED_BLOCK_LENGTH
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|createRbw
argument_list|(
name|storageType
argument_list|,
name|b
argument_list|,
name|allowLazyPersist
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

