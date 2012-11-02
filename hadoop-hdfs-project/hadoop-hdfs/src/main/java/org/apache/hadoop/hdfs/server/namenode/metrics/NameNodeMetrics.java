begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode.metrics
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
name|namenode
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
name|ProcessName
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
name|server
operator|.
name|common
operator|.
name|HdfsServerConstants
operator|.
name|NamenodeRole
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
name|MutableGaugeInt
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
name|MutableQuantiles
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
comment|/**  * This class is for maintaining  the various NameNode activity statistics  * and publishing them through the metrics interfaces.  */
end_comment

begin_class
annotation|@
name|Metrics
argument_list|(
name|name
operator|=
literal|"NameNodeActivity"
argument_list|,
name|about
operator|=
literal|"NameNode metrics"
argument_list|,
name|context
operator|=
literal|"dfs"
argument_list|)
DECL|class|NameNodeMetrics
specifier|public
class|class
name|NameNodeMetrics
block|{
DECL|field|registry
specifier|final
name|MetricsRegistry
name|registry
init|=
operator|new
name|MetricsRegistry
argument_list|(
literal|"namenode"
argument_list|)
decl_stmt|;
DECL|field|createFileOps
annotation|@
name|Metric
name|MutableCounterLong
name|createFileOps
decl_stmt|;
DECL|field|filesCreated
annotation|@
name|Metric
name|MutableCounterLong
name|filesCreated
decl_stmt|;
DECL|field|filesAppended
annotation|@
name|Metric
name|MutableCounterLong
name|filesAppended
decl_stmt|;
DECL|field|getBlockLocations
annotation|@
name|Metric
name|MutableCounterLong
name|getBlockLocations
decl_stmt|;
DECL|field|filesRenamed
annotation|@
name|Metric
name|MutableCounterLong
name|filesRenamed
decl_stmt|;
DECL|field|getListingOps
annotation|@
name|Metric
name|MutableCounterLong
name|getListingOps
decl_stmt|;
DECL|field|deleteFileOps
annotation|@
name|Metric
name|MutableCounterLong
name|deleteFileOps
decl_stmt|;
annotation|@
name|Metric
argument_list|(
literal|"Number of files/dirs deleted by delete or rename operations"
argument_list|)
DECL|field|filesDeleted
name|MutableCounterLong
name|filesDeleted
decl_stmt|;
DECL|field|fileInfoOps
annotation|@
name|Metric
name|MutableCounterLong
name|fileInfoOps
decl_stmt|;
DECL|field|addBlockOps
annotation|@
name|Metric
name|MutableCounterLong
name|addBlockOps
decl_stmt|;
DECL|field|getAdditionalDatanodeOps
annotation|@
name|Metric
name|MutableCounterLong
name|getAdditionalDatanodeOps
decl_stmt|;
DECL|field|createSymlinkOps
annotation|@
name|Metric
name|MutableCounterLong
name|createSymlinkOps
decl_stmt|;
DECL|field|getLinkTargetOps
annotation|@
name|Metric
name|MutableCounterLong
name|getLinkTargetOps
decl_stmt|;
DECL|field|filesInGetListingOps
annotation|@
name|Metric
name|MutableCounterLong
name|filesInGetListingOps
decl_stmt|;
annotation|@
name|Metric
argument_list|(
literal|"Number of allowSnapshot operations"
argument_list|)
DECL|field|allowSnapshotOps
name|MutableCounterLong
name|allowSnapshotOps
decl_stmt|;
annotation|@
name|Metric
argument_list|(
literal|"Number of disallowSnapshot operations"
argument_list|)
DECL|field|disallowSnapshotOps
name|MutableCounterLong
name|disallowSnapshotOps
decl_stmt|;
annotation|@
name|Metric
argument_list|(
literal|"Number of createSnapshot operations"
argument_list|)
DECL|field|createSnapshotOps
name|MutableCounterLong
name|createSnapshotOps
decl_stmt|;
DECL|field|transactions
annotation|@
name|Metric
argument_list|(
literal|"Journal transactions"
argument_list|)
name|MutableRate
name|transactions
decl_stmt|;
DECL|field|syncs
annotation|@
name|Metric
argument_list|(
literal|"Journal syncs"
argument_list|)
name|MutableRate
name|syncs
decl_stmt|;
DECL|field|syncsQuantiles
name|MutableQuantiles
index|[]
name|syncsQuantiles
decl_stmt|;
annotation|@
name|Metric
argument_list|(
literal|"Journal transactions batched in sync"
argument_list|)
DECL|field|transactionsBatchedInSync
name|MutableCounterLong
name|transactionsBatchedInSync
decl_stmt|;
DECL|field|blockReport
annotation|@
name|Metric
argument_list|(
literal|"Block report"
argument_list|)
name|MutableRate
name|blockReport
decl_stmt|;
DECL|field|blockReportQuantiles
name|MutableQuantiles
index|[]
name|blockReportQuantiles
decl_stmt|;
DECL|field|safeModeTime
annotation|@
name|Metric
argument_list|(
literal|"Duration in SafeMode at startup"
argument_list|)
name|MutableGaugeInt
name|safeModeTime
decl_stmt|;
DECL|field|fsImageLoadTime
annotation|@
name|Metric
argument_list|(
literal|"Time loading FS Image at startup"
argument_list|)
name|MutableGaugeInt
name|fsImageLoadTime
decl_stmt|;
DECL|method|NameNodeMetrics (String processName, String sessionId, int[] intervals)
name|NameNodeMetrics
parameter_list|(
name|String
name|processName
parameter_list|,
name|String
name|sessionId
parameter_list|,
name|int
index|[]
name|intervals
parameter_list|)
block|{
name|registry
operator|.
name|tag
argument_list|(
name|ProcessName
argument_list|,
name|processName
argument_list|)
operator|.
name|tag
argument_list|(
name|SessionId
argument_list|,
name|sessionId
argument_list|)
expr_stmt|;
specifier|final
name|int
name|len
init|=
name|intervals
operator|.
name|length
decl_stmt|;
name|syncsQuantiles
operator|=
operator|new
name|MutableQuantiles
index|[
name|len
index|]
expr_stmt|;
name|blockReportQuantiles
operator|=
operator|new
name|MutableQuantiles
index|[
name|len
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|len
condition|;
name|i
operator|++
control|)
block|{
name|int
name|interval
init|=
name|intervals
index|[
name|i
index|]
decl_stmt|;
name|syncsQuantiles
index|[
name|i
index|]
operator|=
name|registry
operator|.
name|newQuantiles
argument_list|(
literal|"syncs"
operator|+
name|interval
operator|+
literal|"s"
argument_list|,
literal|"Journal syncs"
argument_list|,
literal|"ops"
argument_list|,
literal|"latency"
argument_list|,
name|interval
argument_list|)
expr_stmt|;
name|blockReportQuantiles
index|[
name|i
index|]
operator|=
name|registry
operator|.
name|newQuantiles
argument_list|(
literal|"blockReport"
operator|+
name|interval
operator|+
literal|"s"
argument_list|,
literal|"Block report"
argument_list|,
literal|"ops"
argument_list|,
literal|"latency"
argument_list|,
name|interval
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|create (Configuration conf, NamenodeRole r)
specifier|public
specifier|static
name|NameNodeMetrics
name|create
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|NamenodeRole
name|r
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
name|String
name|processName
init|=
name|r
operator|.
name|toString
argument_list|()
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
name|processName
argument_list|,
name|sessionId
argument_list|,
name|ms
argument_list|)
expr_stmt|;
comment|// Percentile measurement is off by default, by watching no intervals
name|int
index|[]
name|intervals
init|=
name|conf
operator|.
name|getInts
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_METRICS_PERCENTILES_INTERVALS_KEY
argument_list|)
decl_stmt|;
return|return
name|ms
operator|.
name|register
argument_list|(
operator|new
name|NameNodeMetrics
argument_list|(
name|processName
argument_list|,
name|sessionId
argument_list|,
name|intervals
argument_list|)
argument_list|)
return|;
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
DECL|method|incrGetBlockLocations ()
specifier|public
name|void
name|incrGetBlockLocations
parameter_list|()
block|{
name|getBlockLocations
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|incrFilesCreated ()
specifier|public
name|void
name|incrFilesCreated
parameter_list|()
block|{
name|filesCreated
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|incrCreateFileOps ()
specifier|public
name|void
name|incrCreateFileOps
parameter_list|()
block|{
name|createFileOps
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|incrFilesAppended ()
specifier|public
name|void
name|incrFilesAppended
parameter_list|()
block|{
name|filesAppended
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|incrAddBlockOps ()
specifier|public
name|void
name|incrAddBlockOps
parameter_list|()
block|{
name|addBlockOps
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|incrGetAdditionalDatanodeOps ()
specifier|public
name|void
name|incrGetAdditionalDatanodeOps
parameter_list|()
block|{
name|getAdditionalDatanodeOps
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|incrFilesRenamed ()
specifier|public
name|void
name|incrFilesRenamed
parameter_list|()
block|{
name|filesRenamed
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|incrFilesDeleted (int delta)
specifier|public
name|void
name|incrFilesDeleted
parameter_list|(
name|int
name|delta
parameter_list|)
block|{
name|filesDeleted
operator|.
name|incr
argument_list|(
name|delta
argument_list|)
expr_stmt|;
block|}
DECL|method|incrDeleteFileOps ()
specifier|public
name|void
name|incrDeleteFileOps
parameter_list|()
block|{
name|deleteFileOps
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|incrGetListingOps ()
specifier|public
name|void
name|incrGetListingOps
parameter_list|()
block|{
name|getListingOps
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|incrFilesInGetListingOps (int delta)
specifier|public
name|void
name|incrFilesInGetListingOps
parameter_list|(
name|int
name|delta
parameter_list|)
block|{
name|filesInGetListingOps
operator|.
name|incr
argument_list|(
name|delta
argument_list|)
expr_stmt|;
block|}
DECL|method|incrFileInfoOps ()
specifier|public
name|void
name|incrFileInfoOps
parameter_list|()
block|{
name|fileInfoOps
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|incrCreateSymlinkOps ()
specifier|public
name|void
name|incrCreateSymlinkOps
parameter_list|()
block|{
name|createSymlinkOps
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|incrGetLinkTargetOps ()
specifier|public
name|void
name|incrGetLinkTargetOps
parameter_list|()
block|{
name|getLinkTargetOps
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|incrAllowSnapshotOps ()
specifier|public
name|void
name|incrAllowSnapshotOps
parameter_list|()
block|{
name|allowSnapshotOps
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|incrDisAllowSnapshotOps ()
specifier|public
name|void
name|incrDisAllowSnapshotOps
parameter_list|()
block|{
name|disallowSnapshotOps
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|incrCreateSnapshotOps ()
specifier|public
name|void
name|incrCreateSnapshotOps
parameter_list|()
block|{
name|createSnapshotOps
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|addTransaction (long latency)
specifier|public
name|void
name|addTransaction
parameter_list|(
name|long
name|latency
parameter_list|)
block|{
name|transactions
operator|.
name|add
argument_list|(
name|latency
argument_list|)
expr_stmt|;
block|}
DECL|method|incrTransactionsBatchedInSync ()
specifier|public
name|void
name|incrTransactionsBatchedInSync
parameter_list|()
block|{
name|transactionsBatchedInSync
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|addSync (long elapsed)
specifier|public
name|void
name|addSync
parameter_list|(
name|long
name|elapsed
parameter_list|)
block|{
name|syncs
operator|.
name|add
argument_list|(
name|elapsed
argument_list|)
expr_stmt|;
for|for
control|(
name|MutableQuantiles
name|q
range|:
name|syncsQuantiles
control|)
block|{
name|q
operator|.
name|add
argument_list|(
name|elapsed
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|setFsImageLoadTime (long elapsed)
specifier|public
name|void
name|setFsImageLoadTime
parameter_list|(
name|long
name|elapsed
parameter_list|)
block|{
name|fsImageLoadTime
operator|.
name|set
argument_list|(
operator|(
name|int
operator|)
name|elapsed
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
name|blockReport
operator|.
name|add
argument_list|(
name|latency
argument_list|)
expr_stmt|;
for|for
control|(
name|MutableQuantiles
name|q
range|:
name|blockReportQuantiles
control|)
block|{
name|q
operator|.
name|add
argument_list|(
name|latency
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|setSafeModeTime (long elapsed)
specifier|public
name|void
name|setSafeModeTime
parameter_list|(
name|long
name|elapsed
parameter_list|)
block|{
name|safeModeTime
operator|.
name|set
argument_list|(
operator|(
name|int
operator|)
name|elapsed
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

