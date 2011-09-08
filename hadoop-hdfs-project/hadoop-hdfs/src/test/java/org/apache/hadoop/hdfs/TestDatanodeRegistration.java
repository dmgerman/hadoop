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
name|net
operator|.
name|InetSocketAddress
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
name|HdfsConstants
operator|.
name|DatanodeReportType
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
name|hdfs
operator|.
name|DFSClient
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

begin_comment
comment|/**  * This class tests that a file need not be closed before its  * data can be read by another client.  */
end_comment

begin_class
DECL|class|TestDatanodeRegistration
specifier|public
class|class
name|TestDatanodeRegistration
extends|extends
name|TestCase
block|{
comment|/**    * Regression test for HDFS-894 ensures that, when datanodes    * are restarted, the new IPC port is registered with the    * namenode.    */
DECL|method|testChangeIpcPort ()
specifier|public
name|void
name|testChangeIpcPort
parameter_list|()
throws|throws
name|Exception
block|{
name|HdfsConfiguration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|MiniDFSCluster
name|cluster
init|=
literal|null
decl_stmt|;
try|try
block|{
name|cluster
operator|=
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
expr_stmt|;
name|InetSocketAddress
name|addr
init|=
operator|new
name|InetSocketAddress
argument_list|(
literal|"localhost"
argument_list|,
name|cluster
operator|.
name|getNameNodePort
argument_list|()
argument_list|)
decl_stmt|;
name|DFSClient
name|client
init|=
operator|new
name|DFSClient
argument_list|(
name|addr
argument_list|,
name|conf
argument_list|)
decl_stmt|;
comment|// Restart datanodes
name|cluster
operator|.
name|restartDataNodes
argument_list|()
expr_stmt|;
comment|// Wait until we get a heartbeat from the new datanode
name|DatanodeInfo
index|[]
name|report
init|=
name|client
operator|.
name|datanodeReport
argument_list|(
name|DatanodeReportType
operator|.
name|ALL
argument_list|)
decl_stmt|;
name|long
name|firstUpdateAfterRestart
init|=
name|report
index|[
literal|0
index|]
operator|.
name|getLastUpdate
argument_list|()
decl_stmt|;
name|boolean
name|gotHeartbeat
init|=
literal|false
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
operator|&&
operator|!
name|gotHeartbeat
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|i
operator|*
literal|1000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{}
name|report
operator|=
name|client
operator|.
name|datanodeReport
argument_list|(
name|DatanodeReportType
operator|.
name|ALL
argument_list|)
expr_stmt|;
name|gotHeartbeat
operator|=
operator|(
name|report
index|[
literal|0
index|]
operator|.
name|getLastUpdate
argument_list|()
operator|>
name|firstUpdateAfterRestart
operator|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|gotHeartbeat
condition|)
block|{
name|fail
argument_list|(
literal|"Never got a heartbeat from restarted datanode."
argument_list|)
expr_stmt|;
block|}
name|int
name|realIpcPort
init|=
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getIpcPort
argument_list|()
decl_stmt|;
comment|// Now make sure the reported IPC port is the correct one.
name|assertEquals
argument_list|(
name|realIpcPort
argument_list|,
name|report
index|[
literal|0
index|]
operator|.
name|getIpcPort
argument_list|()
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

