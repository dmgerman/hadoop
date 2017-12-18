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
name|org
operator|.
name|junit
operator|.
name|Before
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
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_DATA_DIR_KEY
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

begin_comment
comment|/**  * Test that the {@link SimulatedFSDataset} works correctly when configured  * with multiple storages.  */
end_comment

begin_class
DECL|class|TestSimulatedFSDatasetWithMultipleStorages
specifier|public
class|class
name|TestSimulatedFSDatasetWithMultipleStorages
extends|extends
name|TestSimulatedFSDataset
block|{
DECL|method|TestSimulatedFSDatasetWithMultipleStorages ()
specifier|public
name|TestSimulatedFSDatasetWithMultipleStorages
parameter_list|()
block|{
name|super
argument_list|(
literal|2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
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
operator|.
name|set
argument_list|(
name|DFS_DATANODE_DATA_DIR_KEY
argument_list|,
literal|"data1,data2"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMultipleStoragesConfigured ()
specifier|public
name|void
name|testMultipleStoragesConfigured
parameter_list|()
block|{
name|SimulatedFSDataset
name|fsDataset
init|=
name|getSimulatedFSDataset
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|fsDataset
operator|.
name|getStorageReports
argument_list|(
name|bpid
argument_list|)
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

