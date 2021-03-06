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
name|util
operator|.
name|concurrent
operator|.
name|ThreadLocalRandom
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
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
name|server
operator|.
name|datanode
operator|.
name|metrics
operator|.
name|DataNodePeerMetrics
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
name|lib
operator|.
name|MetricsTestHelper
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
name|Time
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
name|CoreMatchers
operator|.
name|containsString
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
name|assertThat
import|;
end_import

begin_comment
comment|/**  * This class tests various cases of DataNode peer metrics.  */
end_comment

begin_class
DECL|class|TestDataNodePeerMetrics
specifier|public
class|class
name|TestDataNodePeerMetrics
block|{
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
DECL|method|testGetSendPacketDownstreamAvgInfo ()
specifier|public
name|void
name|testGetSendPacketDownstreamAvgInfo
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|windowSize
init|=
literal|5
decl_stmt|;
comment|// 5s roll over interval
specifier|final
name|int
name|numWindows
init|=
literal|2
decl_stmt|;
comment|// 2 rolling windows
specifier|final
name|int
name|iterations
init|=
literal|3
decl_stmt|;
specifier|final
name|int
name|numOpsPerIteration
init|=
literal|1000
decl_stmt|;
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_PEER_STATS_ENABLED_KEY
argument_list|,
literal|true
argument_list|)
expr_stmt|;
specifier|final
name|DataNodePeerMetrics
name|peerMetrics
init|=
name|DataNodePeerMetrics
operator|.
name|create
argument_list|(
literal|"Sample-DataNode"
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|MetricsTestHelper
operator|.
name|replaceRollingAveragesScheduler
argument_list|(
name|peerMetrics
operator|.
name|getSendPacketDownstreamRollingAverages
argument_list|()
argument_list|,
name|numWindows
argument_list|,
name|windowSize
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
specifier|final
name|long
name|start
init|=
name|Time
operator|.
name|monotonicNow
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|iterations
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|String
name|peerAddr
init|=
name|genPeerAddress
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|1
init|;
name|j
operator|<=
name|numOpsPerIteration
condition|;
name|j
operator|++
control|)
block|{
comment|/* simulate to get latency of 1 to 1000 ms */
specifier|final
name|long
name|latency
init|=
name|ThreadLocalRandom
operator|.
name|current
argument_list|()
operator|.
name|nextLong
argument_list|(
literal|1
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
name|peerMetrics
operator|.
name|addSendPacketDownstream
argument_list|(
name|peerAddr
argument_list|,
name|latency
argument_list|)
expr_stmt|;
block|}
comment|/**        * Sleep until 1s after the next windowSize seconds interval, to let the        * metrics roll over        */
specifier|final
name|long
name|sleep
init|=
operator|(
name|start
operator|+
operator|(
name|windowSize
operator|*
literal|1000
operator|*
name|i
operator|)
operator|+
literal|1000
operator|)
operator|-
name|Time
operator|.
name|monotonicNow
argument_list|()
decl_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|sleep
argument_list|)
expr_stmt|;
comment|/* dump avg info */
specifier|final
name|String
name|json
init|=
name|peerMetrics
operator|.
name|dumpSendPacketDownstreamAvgInfoAsJson
argument_list|()
decl_stmt|;
comment|/*        * example json:        * {"[185.164.159.81:9801]RollingAvgTime":504.867,        *  "[49.236.149.246:9801]RollingAvgTime":504.463,        *  "[84.125.113.65:9801]RollingAvgTime":497.954}        */
name|assertThat
argument_list|(
name|json
argument_list|,
name|containsString
argument_list|(
name|peerAddr
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Simulates to generate different peer addresses, e.g. [84.125.113.65:9801].    */
DECL|method|genPeerAddress ()
specifier|private
name|String
name|genPeerAddress
parameter_list|()
block|{
specifier|final
name|ThreadLocalRandom
name|r
init|=
name|ThreadLocalRandom
operator|.
name|current
argument_list|()
decl_stmt|;
return|return
name|String
operator|.
name|format
argument_list|(
literal|"[%d.%d.%d.%d:9801]"
argument_list|,
name|r
operator|.
name|nextInt
argument_list|(
literal|1
argument_list|,
literal|256
argument_list|)
argument_list|,
name|r
operator|.
name|nextInt
argument_list|(
literal|1
argument_list|,
literal|256
argument_list|)
argument_list|,
name|r
operator|.
name|nextInt
argument_list|(
literal|1
argument_list|,
literal|256
argument_list|)
argument_list|,
name|r
operator|.
name|nextInt
argument_list|(
literal|1
argument_list|,
literal|256
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

