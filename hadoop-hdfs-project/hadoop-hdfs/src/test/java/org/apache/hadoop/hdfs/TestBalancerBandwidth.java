begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
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
name|util
operator|.
name|ArrayList
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
name|DFSConfigKeys
import|;
end_import

begin_comment
comment|/**  * This test ensures that the balancer bandwidth is dynamically adjusted  * correctly.  */
end_comment

begin_class
DECL|class|TestBalancerBandwidth
specifier|public
class|class
name|TestBalancerBandwidth
extends|extends
name|TestCase
block|{
DECL|field|conf
specifier|final
specifier|static
specifier|private
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
DECL|field|NUM_OF_DATANODES
specifier|final
specifier|static
specifier|private
name|int
name|NUM_OF_DATANODES
init|=
literal|2
decl_stmt|;
DECL|field|DEFAULT_BANDWIDTH
specifier|final
specifier|static
specifier|private
name|int
name|DEFAULT_BANDWIDTH
init|=
literal|1024
operator|*
literal|1024
decl_stmt|;
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
name|TestBalancerBandwidth
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|testBalancerBandwidth ()
specifier|public
name|void
name|testBalancerBandwidth
parameter_list|()
throws|throws
name|Exception
block|{
comment|/* Set bandwidthPerSec to a low value of 1M bps. */
name|conf
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_BALANCE_BANDWIDTHPERSEC_KEY
argument_list|,
name|DEFAULT_BANDWIDTH
argument_list|)
expr_stmt|;
comment|/* Create and start cluster */
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
name|NUM_OF_DATANODES
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
name|ArrayList
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
comment|// Ensure value from the configuration is reflected in the datanodes.
name|assertEquals
argument_list|(
name|DEFAULT_BANDWIDTH
argument_list|,
operator|(
name|long
operator|)
name|datanodes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getBalancerBandwidth
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DEFAULT_BANDWIDTH
argument_list|,
operator|(
name|long
operator|)
name|datanodes
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getBalancerBandwidth
argument_list|()
argument_list|)
expr_stmt|;
comment|// Dynamically change balancer bandwidth and ensure the updated value
comment|// is reflected on the datanodes.
name|long
name|newBandwidth
init|=
literal|12
operator|*
name|DEFAULT_BANDWIDTH
decl_stmt|;
comment|// 12M bps
name|fs
operator|.
name|setBalancerBandwidth
argument_list|(
name|newBandwidth
argument_list|)
expr_stmt|;
comment|// Give it a few seconds to propogate new the value to the datanodes.
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{}
name|assertEquals
argument_list|(
name|newBandwidth
argument_list|,
operator|(
name|long
operator|)
name|datanodes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getBalancerBandwidth
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|newBandwidth
argument_list|,
operator|(
name|long
operator|)
name|datanodes
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getBalancerBandwidth
argument_list|()
argument_list|)
expr_stmt|;
comment|// Dynamically change balancer bandwidth to 0. Balancer bandwidth on the
comment|// datanodes should remain as it was.
name|fs
operator|.
name|setBalancerBandwidth
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// Give it a few seconds to propogate new the value to the datanodes.
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{}
name|assertEquals
argument_list|(
name|newBandwidth
argument_list|,
operator|(
name|long
operator|)
name|datanodes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getBalancerBandwidth
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|newBandwidth
argument_list|,
operator|(
name|long
operator|)
name|datanodes
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getBalancerBandwidth
argument_list|()
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
DECL|method|main (String[] args)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
operator|new
name|TestBalancerBandwidth
argument_list|()
operator|.
name|testBalancerBandwidth
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

