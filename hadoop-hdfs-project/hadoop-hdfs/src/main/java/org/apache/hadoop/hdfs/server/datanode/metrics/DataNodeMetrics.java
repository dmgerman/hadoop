begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode.metrics
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
operator|.
name|metrics
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
name|metrics2
operator|.
name|impl
operator|.
name|MsInfo
operator|.
name|SessionId
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
name|classification
operator|.
name|InterfaceAudience
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
name|DFSUtil
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
name|MetricsSystem
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
name|annotation
operator|.
name|Metric
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
name|annotation
operator|.
name|Metrics
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
name|DefaultMetricsSystem
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
name|MetricsRegistry
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
name|MutableCounterLong
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
name|MutableRate
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
name|source
operator|.
name|JvmMetrics
import|;
end_import

begin_comment
comment|/**  *  * This class is for maintaining  the various DataNode statistics  * and publishing them through the metrics interfaces.  * This also registers the JMX MBean for RPC.  *<p>  * This class has a number of metrics variables that are publicly accessible;  * these variables (objects) have methods to update their values;  *  for example:  *<p> {@link #blocksRead}.inc()  *  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|Metrics
argument_list|(
name|about
operator|=
literal|"DataNode metrics"
argument_list|,
name|context
operator|=
literal|"dfs"
argument_list|)
DECL|class|DataNodeMetrics
specifier|public
class|class
name|DataNodeMetrics
block|{
DECL|field|bytesWritten
annotation|@
name|Metric
name|MutableCounterLong
name|bytesWritten
decl_stmt|;
DECL|field|bytesRead
annotation|@
name|Metric
name|MutableCounterLong
name|bytesRead
decl_stmt|;
DECL|field|blocksWritten
annotation|@
name|Metric
name|MutableCounterLong
name|blocksWritten
decl_stmt|;
DECL|field|blocksRead
annotation|@
name|Metric
name|MutableCounterLong
name|blocksRead
decl_stmt|;
DECL|field|blocksReplicated
annotation|@
name|Metric
name|MutableCounterLong
name|blocksReplicated
decl_stmt|;
DECL|field|blocksRemoved
annotation|@
name|Metric
name|MutableCounterLong
name|blocksRemoved
decl_stmt|;
DECL|field|blocksVerified
annotation|@
name|Metric
name|MutableCounterLong
name|blocksVerified
decl_stmt|;
DECL|field|blockVerificationFailures
annotation|@
name|Metric
name|MutableCounterLong
name|blockVerificationFailures
decl_stmt|;
DECL|field|readsFromLocalClient
annotation|@
name|Metric
name|MutableCounterLong
name|readsFromLocalClient
decl_stmt|;
DECL|field|readsFromRemoteClient
annotation|@
name|Metric
name|MutableCounterLong
name|readsFromRemoteClient
decl_stmt|;
DECL|field|writesFromLocalClient
annotation|@
name|Metric
name|MutableCounterLong
name|writesFromLocalClient
decl_stmt|;
DECL|field|writesFromRemoteClient
annotation|@
name|Metric
name|MutableCounterLong
name|writesFromRemoteClient
decl_stmt|;
DECL|field|blocksGetLocalPathInfo
annotation|@
name|Metric
name|MutableCounterLong
name|blocksGetLocalPathInfo
decl_stmt|;
DECL|field|volumeFailures
annotation|@
name|Metric
name|MutableCounterLong
name|volumeFailures
decl_stmt|;
DECL|field|readBlockOp
annotation|@
name|Metric
name|MutableRate
name|readBlockOp
decl_stmt|;
DECL|field|writeBlockOp
annotation|@
name|Metric
name|MutableRate
name|writeBlockOp
decl_stmt|;
DECL|field|blockChecksumOp
annotation|@
name|Metric
name|MutableRate
name|blockChecksumOp
decl_stmt|;
DECL|field|copyBlockOp
annotation|@
name|Metric
name|MutableRate
name|copyBlockOp
decl_stmt|;
DECL|field|replaceBlockOp
annotation|@
name|Metric
name|MutableRate
name|replaceBlockOp
decl_stmt|;
DECL|field|heartbeats
annotation|@
name|Metric
name|MutableRate
name|heartbeats
decl_stmt|;
DECL|field|blockReports
annotation|@
name|Metric
name|MutableRate
name|blockReports
decl_stmt|;
DECL|field|registry
specifier|final
name|MetricsRegistry
name|registry
init|=
operator|new
name|MetricsRegistry
argument_list|(
literal|"datanode"
argument_list|)
decl_stmt|;
DECL|field|name
specifier|final
name|String
name|name
decl_stmt|;
DECL|method|DataNodeMetrics (String name, String sessionId)
specifier|public
name|DataNodeMetrics
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|sessionId
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|registry
operator|.
name|tag
argument_list|(
name|SessionId
argument_list|,
name|sessionId
argument_list|)
expr_stmt|;
block|}
DECL|method|create (Configuration conf, String dnName)
specifier|public
specifier|static
name|DataNodeMetrics
name|create
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|dnName
parameter_list|)
block|{
name|String
name|sessionId
init|=
name|conf
operator|.
name|get
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_METRICS_SESSION_ID_KEY
argument_list|)
decl_stmt|;
name|MetricsSystem
name|ms
init|=
name|DefaultMetricsSystem
operator|.
name|instance
argument_list|()
decl_stmt|;
name|JvmMetrics
operator|.
name|create
argument_list|(
literal|"DataNode"
argument_list|,
name|sessionId
argument_list|,
name|ms
argument_list|)
expr_stmt|;
name|String
name|name
init|=
literal|"DataNodeActivity-"
operator|+
operator|(
name|dnName
operator|.
name|isEmpty
argument_list|()
condition|?
literal|"UndefinedDataNodeName"
operator|+
name|DFSUtil
operator|.
name|getRandom
argument_list|()
operator|.
name|nextInt
argument_list|()
else|:
name|dnName
operator|.
name|replace
argument_list|(
literal|':'
argument_list|,
literal|'-'
argument_list|)
operator|)
decl_stmt|;
return|return
name|ms
operator|.
name|register
argument_list|(
name|name
argument_list|,
literal|null
argument_list|,
operator|new
name|DataNodeMetrics
argument_list|(
name|name
argument_list|,
name|sessionId
argument_list|)
argument_list|)
return|;
block|}
DECL|method|name ()
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
name|name
return|;
block|}
DECL|method|addHeartbeat (long latency)
specifier|public
name|void
name|addHeartbeat
parameter_list|(
name|long
name|latency
parameter_list|)
block|{
name|heartbeats
operator|.
name|add
argument_list|(
name|latency
argument_list|)
expr_stmt|;
block|}
DECL|method|addBlockReport (long latency)
specifier|public
name|void
name|addBlockReport
parameter_list|(
name|long
name|latency
parameter_list|)
block|{
name|blockReports
operator|.
name|add
argument_list|(
name|latency
argument_list|)
expr_stmt|;
block|}
DECL|method|incrBlocksReplicated (int delta)
specifier|public
name|void
name|incrBlocksReplicated
parameter_list|(
name|int
name|delta
parameter_list|)
block|{
name|blocksReplicated
operator|.
name|incr
argument_list|(
name|delta
argument_list|)
expr_stmt|;
block|}
DECL|method|incrBlocksWritten ()
specifier|public
name|void
name|incrBlocksWritten
parameter_list|()
block|{
name|blocksWritten
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|incrBlocksRemoved (int delta)
specifier|public
name|void
name|incrBlocksRemoved
parameter_list|(
name|int
name|delta
parameter_list|)
block|{
name|blocksRemoved
operator|.
name|incr
argument_list|(
name|delta
argument_list|)
expr_stmt|;
block|}
DECL|method|incrBytesWritten (int delta)
specifier|public
name|void
name|incrBytesWritten
parameter_list|(
name|int
name|delta
parameter_list|)
block|{
name|bytesWritten
operator|.
name|incr
argument_list|(
name|delta
argument_list|)
expr_stmt|;
block|}
DECL|method|incrBlockVerificationFailures ()
specifier|public
name|void
name|incrBlockVerificationFailures
parameter_list|()
block|{
name|blockVerificationFailures
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|incrBlocksVerified ()
specifier|public
name|void
name|incrBlocksVerified
parameter_list|()
block|{
name|blocksVerified
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|addReadBlockOp (long latency)
specifier|public
name|void
name|addReadBlockOp
parameter_list|(
name|long
name|latency
parameter_list|)
block|{
name|readBlockOp
operator|.
name|add
argument_list|(
name|latency
argument_list|)
expr_stmt|;
block|}
DECL|method|addWriteBlockOp (long latency)
specifier|public
name|void
name|addWriteBlockOp
parameter_list|(
name|long
name|latency
parameter_list|)
block|{
name|writeBlockOp
operator|.
name|add
argument_list|(
name|latency
argument_list|)
expr_stmt|;
block|}
DECL|method|addReplaceBlockOp (long latency)
specifier|public
name|void
name|addReplaceBlockOp
parameter_list|(
name|long
name|latency
parameter_list|)
block|{
name|replaceBlockOp
operator|.
name|add
argument_list|(
name|latency
argument_list|)
expr_stmt|;
block|}
DECL|method|addCopyBlockOp (long latency)
specifier|public
name|void
name|addCopyBlockOp
parameter_list|(
name|long
name|latency
parameter_list|)
block|{
name|copyBlockOp
operator|.
name|add
argument_list|(
name|latency
argument_list|)
expr_stmt|;
block|}
DECL|method|addBlockChecksumOp (long latency)
specifier|public
name|void
name|addBlockChecksumOp
parameter_list|(
name|long
name|latency
parameter_list|)
block|{
name|blockChecksumOp
operator|.
name|add
argument_list|(
name|latency
argument_list|)
expr_stmt|;
block|}
DECL|method|incrBytesRead (int delta)
specifier|public
name|void
name|incrBytesRead
parameter_list|(
name|int
name|delta
parameter_list|)
block|{
name|bytesRead
operator|.
name|incr
argument_list|(
name|delta
argument_list|)
expr_stmt|;
block|}
DECL|method|incrBlocksRead ()
specifier|public
name|void
name|incrBlocksRead
parameter_list|()
block|{
name|blocksRead
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|shutdown ()
specifier|public
name|void
name|shutdown
parameter_list|()
block|{
name|DefaultMetricsSystem
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
DECL|method|incrWritesFromClient (boolean local)
specifier|public
name|void
name|incrWritesFromClient
parameter_list|(
name|boolean
name|local
parameter_list|)
block|{
operator|(
name|local
condition|?
name|writesFromLocalClient
else|:
name|writesFromRemoteClient
operator|)
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|incrReadsFromClient (boolean local)
specifier|public
name|void
name|incrReadsFromClient
parameter_list|(
name|boolean
name|local
parameter_list|)
block|{
operator|(
name|local
condition|?
name|readsFromLocalClient
else|:
name|readsFromRemoteClient
operator|)
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|incrVolumeFailures ()
specifier|public
name|void
name|incrVolumeFailures
parameter_list|()
block|{
name|volumeFailures
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
comment|/** Increment for getBlockLocalPathInfo calls */
DECL|method|incrBlocksGetLocalPathInfo ()
specifier|public
name|void
name|incrBlocksGetLocalPathInfo
parameter_list|()
block|{
name|blocksGetLocalPathInfo
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

