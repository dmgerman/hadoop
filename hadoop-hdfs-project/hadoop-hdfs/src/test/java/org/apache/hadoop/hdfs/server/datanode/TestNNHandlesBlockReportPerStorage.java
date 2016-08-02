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
name|hdfs
operator|.
name|server
operator|.
name|protocol
operator|.
name|BlockReportContext
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
name|protocol
operator|.
name|DatanodeRegistration
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
name|protocol
operator|.
name|StorageBlockReport
import|;
end_import

begin_comment
comment|/**  * Runs all tests in BlockReportTestBase, sending one block per storage.  * This is the default DataNode behavior post HDFS-2832.  */
end_comment

begin_class
DECL|class|TestNNHandlesBlockReportPerStorage
specifier|public
class|class
name|TestNNHandlesBlockReportPerStorage
extends|extends
name|BlockReportTestBase
block|{
annotation|@
name|Override
DECL|method|sendBlockReports (DatanodeRegistration dnR, String poolId, StorageBlockReport[] reports)
specifier|protected
name|void
name|sendBlockReports
parameter_list|(
name|DatanodeRegistration
name|dnR
parameter_list|,
name|String
name|poolId
parameter_list|,
name|StorageBlockReport
index|[]
name|reports
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|StorageBlockReport
name|report
range|:
name|reports
control|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Sending block report for storage "
operator|+
name|report
operator|.
name|getStorage
argument_list|()
operator|.
name|getStorageID
argument_list|()
argument_list|)
expr_stmt|;
name|StorageBlockReport
index|[]
name|singletonReport
init|=
block|{
name|report
block|}
decl_stmt|;
name|cluster
operator|.
name|getNameNodeRpc
argument_list|()
operator|.
name|blockReport
argument_list|(
name|dnR
argument_list|,
name|poolId
argument_list|,
name|singletonReport
argument_list|,
operator|new
name|BlockReportContext
argument_list|(
name|reports
operator|.
name|length
argument_list|,
name|i
argument_list|,
name|System
operator|.
name|nanoTime
argument_list|()
argument_list|,
literal|0L
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

