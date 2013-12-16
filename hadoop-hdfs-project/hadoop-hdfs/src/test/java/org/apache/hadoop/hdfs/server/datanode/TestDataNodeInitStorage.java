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
name|*
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
name|IsNot
operator|.
name|not
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
comment|/**  * Test to verify that the DataNode Uuid is correctly initialized before  * FsDataSet initialization.  */
end_comment

begin_class
DECL|class|TestDataNodeInitStorage
specifier|public
class|class
name|TestDataNodeInitStorage
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TestDataNodeInitStorage
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|class|SimulatedFsDatasetVerifier
specifier|static
specifier|private
class|class
name|SimulatedFsDatasetVerifier
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
DECL|method|newInstance ( DataNode datanode, DataStorage storage, Configuration conf)
specifier|public
name|SimulatedFsDatasetVerifier
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
name|SimulatedFsDatasetVerifier
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
comment|// This constructor does the actual verification by ensuring that
comment|// the DatanodeUuid is initialized.
DECL|method|SimulatedFsDatasetVerifier (DataStorage storage, Configuration conf)
specifier|public
name|SimulatedFsDatasetVerifier
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
name|LOG
operator|.
name|info
argument_list|(
literal|"Assigned DatanodeUuid is "
operator|+
name|storage
operator|.
name|getDatanodeUuid
argument_list|()
argument_list|)
expr_stmt|;
assert|assert
operator|(
name|storage
operator|.
name|getDatanodeUuid
argument_list|()
operator|!=
literal|null
operator|)
assert|;
assert|assert
operator|(
name|storage
operator|.
name|getDatanodeUuid
argument_list|()
operator|.
name|length
argument_list|()
operator|!=
literal|0
operator|)
assert|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testDataNodeInitStorage ()
specifier|public
name|void
name|testDataNodeInitStorage
parameter_list|()
throws|throws
name|Throwable
block|{
comment|// Create configuration to use SimulatedFsDatasetVerifier#Factory.
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|SimulatedFsDatasetVerifier
operator|.
name|setFactory
argument_list|(
name|conf
argument_list|)
expr_stmt|;
comment|// Start a cluster so that SimulatedFsDatasetVerifier constructor is
comment|// invoked.
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
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

