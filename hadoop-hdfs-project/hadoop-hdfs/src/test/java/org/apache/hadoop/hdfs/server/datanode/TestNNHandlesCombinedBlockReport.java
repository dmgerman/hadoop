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
comment|/**  * Runs all tests in BlockReportTestBase, sending one block report  * per DataNode. This tests that the NN can handle the legacy DN  * behavior where it presents itself as a single logical storage.  */
end_comment

begin_class
DECL|class|TestNNHandlesCombinedBlockReport
specifier|public
class|class
name|TestNNHandlesCombinedBlockReport
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
name|LOG
operator|.
name|info
argument_list|(
literal|"Sending combined block reports for "
operator|+
name|dnR
argument_list|)
expr_stmt|;
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
name|reports
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

