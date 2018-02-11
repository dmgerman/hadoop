begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.blockmanagement
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
name|blockmanagement
package|;
end_package

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|annotation
operator|.
name|JsonIgnore
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|annotation
operator|.
name|JsonProperty
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|core
operator|.
name|JsonProcessingException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|databind
operator|.
name|ObjectMapper
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|databind
operator|.
name|ObjectWriter
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableList
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|primitives
operator|.
name|Doubles
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
name|classification
operator|.
name|InterfaceStability
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
name|protocol
operator|.
name|SlowDiskReports
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
name|SlowDiskReports
operator|.
name|DiskOp
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
name|Timer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

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
name|java
operator|.
name|util
operator|.
name|Comparator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|PriorityQueue
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
name|ConcurrentHashMap
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
import|;
end_import

begin_comment
comment|/**  * This class aggregates information from {@link SlowDiskReports} received via  * heartbeats.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|SlowDiskTracker
specifier|public
class|class
name|SlowDiskTracker
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SlowDiskTracker
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Time duration after which a report is considered stale. This is    * set to DFS_DATANODE_OUTLIERS_REPORT_INTERVAL_KEY * 3 i.e.    * maintained for at least two successive reports.    */
DECL|field|reportValidityMs
specifier|private
name|long
name|reportValidityMs
decl_stmt|;
comment|/**    * Timer object for querying the current time. Separated out for    * unit testing.    */
DECL|field|timer
specifier|private
specifier|final
name|Timer
name|timer
decl_stmt|;
comment|/**    * ObjectWriter to convert JSON reports to String.    */
DECL|field|WRITER
specifier|private
specifier|static
specifier|final
name|ObjectWriter
name|WRITER
init|=
operator|new
name|ObjectMapper
argument_list|()
operator|.
name|writer
argument_list|()
decl_stmt|;
comment|/**    * Number of disks to include in JSON report per operation. We will return    * disks with the highest latency.    */
DECL|field|MAX_DISKS_TO_REPORT
specifier|private
specifier|static
specifier|final
name|int
name|MAX_DISKS_TO_REPORT
init|=
literal|5
decl_stmt|;
DECL|field|DATANODE_DISK_SEPARATOR
specifier|private
specifier|static
specifier|final
name|String
name|DATANODE_DISK_SEPARATOR
init|=
literal|":"
decl_stmt|;
DECL|field|reportGenerationIntervalMs
specifier|private
specifier|final
name|long
name|reportGenerationIntervalMs
decl_stmt|;
DECL|field|lastUpdateTime
specifier|private
specifier|volatile
name|long
name|lastUpdateTime
decl_stmt|;
DECL|field|isUpdateInProgress
specifier|private
name|AtomicBoolean
name|isUpdateInProgress
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
comment|/**    * Information about disks that have been reported as being slow.    * It is map of (Slow Disk ID) -> (DiskLatency). The DiskLatency contains    * the disk ID, the latencies reported and the timestamp when the report    * was received.    */
DECL|field|diskIDLatencyMap
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|DiskLatency
argument_list|>
name|diskIDLatencyMap
decl_stmt|;
comment|/**    * Map of slow disk -> diskOperations it has been reported slow in.    */
DECL|field|slowDisksReport
specifier|private
specifier|volatile
name|ArrayList
argument_list|<
name|DiskLatency
argument_list|>
name|slowDisksReport
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
DECL|field|oldSlowDisksCheck
specifier|private
specifier|volatile
name|ArrayList
argument_list|<
name|DiskLatency
argument_list|>
name|oldSlowDisksCheck
decl_stmt|;
DECL|method|SlowDiskTracker (Configuration conf, Timer timer)
specifier|public
name|SlowDiskTracker
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|Timer
name|timer
parameter_list|)
block|{
name|this
operator|.
name|timer
operator|=
name|timer
expr_stmt|;
name|this
operator|.
name|lastUpdateTime
operator|=
name|timer
operator|.
name|monotonicNow
argument_list|()
expr_stmt|;
name|this
operator|.
name|diskIDLatencyMap
operator|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|this
operator|.
name|reportGenerationIntervalMs
operator|=
name|conf
operator|.
name|getTimeDuration
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_OUTLIERS_REPORT_INTERVAL_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_OUTLIERS_REPORT_INTERVAL_DEFAULT
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
name|this
operator|.
name|reportValidityMs
operator|=
name|reportGenerationIntervalMs
operator|*
literal|3
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getSlowDiskIDForReport (String datanodeID, String slowDisk)
specifier|public
specifier|static
name|String
name|getSlowDiskIDForReport
parameter_list|(
name|String
name|datanodeID
parameter_list|,
name|String
name|slowDisk
parameter_list|)
block|{
return|return
name|datanodeID
operator|+
name|DATANODE_DISK_SEPARATOR
operator|+
name|slowDisk
return|;
block|}
DECL|method|addSlowDiskReport (String dataNodeID, SlowDiskReports dnSlowDiskReport)
specifier|public
name|void
name|addSlowDiskReport
parameter_list|(
name|String
name|dataNodeID
parameter_list|,
name|SlowDiskReports
name|dnSlowDiskReport
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|DiskOp
argument_list|,
name|Double
argument_list|>
argument_list|>
name|slowDisks
init|=
name|dnSlowDiskReport
operator|.
name|getSlowDisks
argument_list|()
decl_stmt|;
name|long
name|now
init|=
name|timer
operator|.
name|monotonicNow
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|DiskOp
argument_list|,
name|Double
argument_list|>
argument_list|>
name|slowDiskEntry
range|:
name|slowDisks
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|diskID
init|=
name|getSlowDiskIDForReport
argument_list|(
name|dataNodeID
argument_list|,
name|slowDiskEntry
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|DiskOp
argument_list|,
name|Double
argument_list|>
name|latencies
init|=
name|slowDiskEntry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|DiskLatency
name|diskLatency
init|=
operator|new
name|DiskLatency
argument_list|(
name|diskID
argument_list|,
name|latencies
argument_list|,
name|now
argument_list|)
decl_stmt|;
name|diskIDLatencyMap
operator|.
name|put
argument_list|(
name|diskID
argument_list|,
name|diskLatency
argument_list|)
expr_stmt|;
block|}
name|checkAndUpdateReportIfNecessary
argument_list|()
expr_stmt|;
block|}
DECL|method|checkAndUpdateReportIfNecessary ()
specifier|private
name|void
name|checkAndUpdateReportIfNecessary
parameter_list|()
block|{
comment|// Check if it is time for update
name|long
name|now
init|=
name|timer
operator|.
name|monotonicNow
argument_list|()
decl_stmt|;
if|if
condition|(
name|now
operator|-
name|lastUpdateTime
operator|>
name|reportGenerationIntervalMs
condition|)
block|{
name|updateSlowDiskReportAsync
argument_list|(
name|now
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|VisibleForTesting
DECL|method|updateSlowDiskReportAsync (long now)
specifier|public
name|void
name|updateSlowDiskReportAsync
parameter_list|(
name|long
name|now
parameter_list|)
block|{
if|if
condition|(
name|isUpdateInProgress
operator|.
name|compareAndSet
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|lastUpdateTime
operator|=
name|now
expr_stmt|;
operator|new
name|Thread
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|slowDisksReport
operator|=
name|getSlowDisks
argument_list|(
name|diskIDLatencyMap
argument_list|,
name|MAX_DISKS_TO_REPORT
argument_list|,
name|now
argument_list|)
expr_stmt|;
name|cleanUpOldReports
argument_list|(
name|now
argument_list|)
expr_stmt|;
name|isUpdateInProgress
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * This structure is a thin wrapper over disk latencies.    */
DECL|class|DiskLatency
specifier|public
specifier|static
class|class
name|DiskLatency
block|{
annotation|@
name|JsonProperty
argument_list|(
literal|"SlowDiskID"
argument_list|)
DECL|field|slowDiskID
specifier|final
specifier|private
name|String
name|slowDiskID
decl_stmt|;
annotation|@
name|JsonProperty
argument_list|(
literal|"Latencies"
argument_list|)
DECL|field|latencyMap
specifier|final
specifier|private
name|Map
argument_list|<
name|DiskOp
argument_list|,
name|Double
argument_list|>
name|latencyMap
decl_stmt|;
annotation|@
name|JsonIgnore
DECL|field|timestamp
specifier|private
name|long
name|timestamp
decl_stmt|;
comment|/**      * Constructor needed by Jackson for Object mapping.      */
DECL|method|DiskLatency ( @sonPropertyR) String slowDiskID, @JsonProperty(R) Map<DiskOp, Double> latencyMap)
specifier|public
name|DiskLatency
parameter_list|(
annotation|@
name|JsonProperty
argument_list|(
literal|"SlowDiskID"
argument_list|)
name|String
name|slowDiskID
parameter_list|,
annotation|@
name|JsonProperty
argument_list|(
literal|"Latencies"
argument_list|)
name|Map
argument_list|<
name|DiskOp
argument_list|,
name|Double
argument_list|>
name|latencyMap
parameter_list|)
block|{
name|this
operator|.
name|slowDiskID
operator|=
name|slowDiskID
expr_stmt|;
name|this
operator|.
name|latencyMap
operator|=
name|latencyMap
expr_stmt|;
block|}
DECL|method|DiskLatency (String slowDiskID, Map<DiskOp, Double> latencyMap, long timestamp)
specifier|public
name|DiskLatency
parameter_list|(
name|String
name|slowDiskID
parameter_list|,
name|Map
argument_list|<
name|DiskOp
argument_list|,
name|Double
argument_list|>
name|latencyMap
parameter_list|,
name|long
name|timestamp
parameter_list|)
block|{
name|this
operator|.
name|slowDiskID
operator|=
name|slowDiskID
expr_stmt|;
name|this
operator|.
name|latencyMap
operator|=
name|latencyMap
expr_stmt|;
name|this
operator|.
name|timestamp
operator|=
name|timestamp
expr_stmt|;
block|}
DECL|method|getSlowDiskID ()
name|String
name|getSlowDiskID
parameter_list|()
block|{
return|return
name|this
operator|.
name|slowDiskID
return|;
block|}
DECL|method|getMaxLatency ()
name|double
name|getMaxLatency
parameter_list|()
block|{
name|double
name|maxLatency
init|=
literal|0
decl_stmt|;
for|for
control|(
name|double
name|latency
range|:
name|latencyMap
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|latency
operator|>
name|maxLatency
condition|)
block|{
name|maxLatency
operator|=
name|latency
expr_stmt|;
block|}
block|}
return|return
name|maxLatency
return|;
block|}
DECL|method|getLatency (DiskOp op)
name|Double
name|getLatency
parameter_list|(
name|DiskOp
name|op
parameter_list|)
block|{
return|return
name|this
operator|.
name|latencyMap
operator|.
name|get
argument_list|(
name|op
argument_list|)
return|;
block|}
block|}
comment|/**    * Retrieve a list of stop low disks i.e disks with the highest max latencies.    * @param numDisks number of disks to return. This is to limit the size of    *                 the generated JSON.    */
DECL|method|getSlowDisks ( Map<String, DiskLatency> reports, int numDisks, long now)
specifier|private
name|ArrayList
argument_list|<
name|DiskLatency
argument_list|>
name|getSlowDisks
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|DiskLatency
argument_list|>
name|reports
parameter_list|,
name|int
name|numDisks
parameter_list|,
name|long
name|now
parameter_list|)
block|{
if|if
condition|(
name|reports
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
operator|new
name|ArrayList
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|()
argument_list|)
return|;
block|}
specifier|final
name|PriorityQueue
argument_list|<
name|DiskLatency
argument_list|>
name|topNReports
init|=
operator|new
name|PriorityQueue
argument_list|<>
argument_list|(
name|reports
operator|.
name|size
argument_list|()
argument_list|,
operator|new
name|Comparator
argument_list|<
name|DiskLatency
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|DiskLatency
name|o1
parameter_list|,
name|DiskLatency
name|o2
parameter_list|)
block|{
return|return
name|Doubles
operator|.
name|compare
argument_list|(
name|o1
operator|.
name|getMaxLatency
argument_list|()
argument_list|,
name|o2
operator|.
name|getMaxLatency
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|ArrayList
argument_list|<
name|DiskLatency
argument_list|>
name|oldSlowDiskIDs
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|DiskLatency
argument_list|>
name|entry
range|:
name|reports
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|DiskLatency
name|diskLatency
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|now
operator|-
name|diskLatency
operator|.
name|timestamp
operator|<
name|reportValidityMs
condition|)
block|{
if|if
condition|(
name|topNReports
operator|.
name|size
argument_list|()
operator|<
name|numDisks
condition|)
block|{
name|topNReports
operator|.
name|add
argument_list|(
name|diskLatency
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|topNReports
operator|.
name|peek
argument_list|()
operator|.
name|getMaxLatency
argument_list|()
operator|<
name|diskLatency
operator|.
name|getMaxLatency
argument_list|()
condition|)
block|{
name|topNReports
operator|.
name|poll
argument_list|()
expr_stmt|;
name|topNReports
operator|.
name|add
argument_list|(
name|diskLatency
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|oldSlowDiskIDs
operator|.
name|add
argument_list|(
name|diskLatency
argument_list|)
expr_stmt|;
block|}
block|}
name|oldSlowDisksCheck
operator|=
name|oldSlowDiskIDs
expr_stmt|;
return|return
name|Lists
operator|.
name|newArrayList
argument_list|(
name|topNReports
argument_list|)
return|;
block|}
comment|/**    * Retrieve all valid reports as a JSON string.    * @return serialized representation of valid reports. null if    *         serialization failed.    */
DECL|method|getSlowDiskReportAsJsonString ()
specifier|public
name|String
name|getSlowDiskReportAsJsonString
parameter_list|()
block|{
try|try
block|{
if|if
condition|(
name|slowDisksReport
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|WRITER
operator|.
name|writeValueAsString
argument_list|(
name|slowDisksReport
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|JsonProcessingException
name|e
parameter_list|)
block|{
comment|// Failed to serialize. Don't log the exception call stack.
name|LOG
operator|.
name|debug
argument_list|(
literal|"Failed to serialize statistics"
operator|+
name|e
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
DECL|method|cleanUpOldReports (long now)
specifier|private
name|void
name|cleanUpOldReports
parameter_list|(
name|long
name|now
parameter_list|)
block|{
if|if
condition|(
name|oldSlowDisksCheck
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|DiskLatency
name|oldDiskLatency
range|:
name|oldSlowDisksCheck
control|)
block|{
name|diskIDLatencyMap
operator|.
name|remove
argument_list|(
name|oldDiskLatency
operator|.
name|getSlowDiskID
argument_list|()
argument_list|,
name|oldDiskLatency
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Replace oldSlowDiskIDsCheck with an empty ArrayList
name|oldSlowDisksCheck
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getSlowDisksReport ()
name|ArrayList
argument_list|<
name|DiskLatency
argument_list|>
name|getSlowDisksReport
parameter_list|()
block|{
return|return
name|this
operator|.
name|slowDisksReport
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getReportValidityMs ()
name|long
name|getReportValidityMs
parameter_list|()
block|{
return|return
name|reportValidityMs
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|setReportValidityMs (long reportValidityMs)
name|void
name|setReportValidityMs
parameter_list|(
name|long
name|reportValidityMs
parameter_list|)
block|{
name|this
operator|.
name|reportValidityMs
operator|=
name|reportValidityMs
expr_stmt|;
block|}
block|}
end_class

end_unit

