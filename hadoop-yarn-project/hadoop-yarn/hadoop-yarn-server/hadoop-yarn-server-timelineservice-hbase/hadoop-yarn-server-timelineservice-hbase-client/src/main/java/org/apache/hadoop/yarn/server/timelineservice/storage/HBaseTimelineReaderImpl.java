begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timelineservice.storage
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|timelineservice
operator|.
name|storage
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
name|java
operator|.
name|util
operator|.
name|Set
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
name|Executors
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
name|ScheduledExecutorService
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
name|hbase
operator|.
name|client
operator|.
name|Connection
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
name|hbase
operator|.
name|client
operator|.
name|ConnectionFactory
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
name|service
operator|.
name|AbstractService
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|timeline
operator|.
name|TimelineHealth
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|timelineservice
operator|.
name|TimelineEntity
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|timelineservice
operator|.
name|TimelineEntityType
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
name|yarn
operator|.
name|conf
operator|.
name|YarnConfiguration
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
name|yarn
operator|.
name|server
operator|.
name|timelineservice
operator|.
name|reader
operator|.
name|TimelineDataToRetrieve
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
name|yarn
operator|.
name|server
operator|.
name|timelineservice
operator|.
name|reader
operator|.
name|TimelineEntityFilters
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
name|yarn
operator|.
name|server
operator|.
name|timelineservice
operator|.
name|reader
operator|.
name|TimelineReaderContext
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
name|yarn
operator|.
name|server
operator|.
name|timelineservice
operator|.
name|storage
operator|.
name|common
operator|.
name|HBaseTimelineStorageUtils
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
name|yarn
operator|.
name|server
operator|.
name|timelineservice
operator|.
name|storage
operator|.
name|reader
operator|.
name|EntityTypeReader
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
name|yarn
operator|.
name|server
operator|.
name|timelineservice
operator|.
name|storage
operator|.
name|reader
operator|.
name|TimelineEntityReader
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
name|yarn
operator|.
name|server
operator|.
name|timelineservice
operator|.
name|storage
operator|.
name|reader
operator|.
name|TimelineEntityReaderFactory
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

begin_comment
comment|/**  * HBase based implementation for {@link TimelineReader}.  */
end_comment

begin_class
DECL|class|HBaseTimelineReaderImpl
specifier|public
class|class
name|HBaseTimelineReaderImpl
extends|extends
name|AbstractService
implements|implements
name|TimelineReader
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|HBaseTimelineReaderImpl
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|hbaseConf
specifier|private
name|Configuration
name|hbaseConf
init|=
literal|null
decl_stmt|;
DECL|field|conn
specifier|private
name|Connection
name|conn
decl_stmt|;
DECL|field|monitorHBaseConf
specifier|private
name|Configuration
name|monitorHBaseConf
init|=
literal|null
decl_stmt|;
DECL|field|monitorConn
specifier|private
name|Connection
name|monitorConn
decl_stmt|;
DECL|field|monitorExecutorService
specifier|private
name|ScheduledExecutorService
name|monitorExecutorService
decl_stmt|;
DECL|field|monitorContext
specifier|private
name|TimelineReaderContext
name|monitorContext
decl_stmt|;
DECL|field|monitorInterval
specifier|private
name|long
name|monitorInterval
decl_stmt|;
DECL|field|hbaseDown
specifier|private
name|AtomicBoolean
name|hbaseDown
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
DECL|method|HBaseTimelineReaderImpl ()
specifier|public
name|HBaseTimelineReaderImpl
parameter_list|()
block|{
name|super
argument_list|(
name|HBaseTimelineReaderImpl
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceInit (Configuration conf)
specifier|public
name|void
name|serviceInit
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|Exception
block|{
name|super
operator|.
name|serviceInit
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|String
name|clusterId
init|=
name|conf
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|RM_CLUSTER_ID
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_CLUSTER_ID
argument_list|)
decl_stmt|;
name|monitorContext
operator|=
operator|new
name|TimelineReaderContext
argument_list|(
name|clusterId
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|TimelineEntityType
operator|.
name|YARN_FLOW_ACTIVITY
operator|.
name|toString
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|monitorInterval
operator|=
name|conf
operator|.
name|getLong
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_READER_STORAGE_MONITOR_INTERVAL_MS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_TIMELINE_SERVICE_STORAGE_MONITOR_INTERVAL_MS
argument_list|)
expr_stmt|;
name|monitorHBaseConf
operator|=
name|HBaseTimelineStorageUtils
operator|.
name|getTimelineServiceHBaseConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|monitorHBaseConf
operator|.
name|setInt
argument_list|(
literal|"hbase.client.retries.number"
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|monitorHBaseConf
operator|.
name|setLong
argument_list|(
literal|"hbase.client.pause"
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
name|monitorHBaseConf
operator|.
name|setLong
argument_list|(
literal|"hbase.rpc.timeout"
argument_list|,
name|monitorInterval
argument_list|)
expr_stmt|;
name|monitorHBaseConf
operator|.
name|setLong
argument_list|(
literal|"hbase.client.scanner.timeout.period"
argument_list|,
name|monitorInterval
argument_list|)
expr_stmt|;
name|monitorHBaseConf
operator|.
name|setInt
argument_list|(
literal|"zookeeper.recovery.retry"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|monitorConn
operator|=
name|ConnectionFactory
operator|.
name|createConnection
argument_list|(
name|monitorHBaseConf
argument_list|)
expr_stmt|;
name|monitorExecutorService
operator|=
name|Executors
operator|.
name|newScheduledThreadPool
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|hbaseConf
operator|=
name|HBaseTimelineStorageUtils
operator|.
name|getTimelineServiceHBaseConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|conn
operator|=
name|ConnectionFactory
operator|.
name|createConnection
argument_list|(
name|hbaseConf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceStart ()
specifier|protected
name|void
name|serviceStart
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|serviceStart
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Scheduling HBase liveness monitor at interval {}"
argument_list|,
name|monitorInterval
argument_list|)
expr_stmt|;
name|monitorExecutorService
operator|.
name|scheduleAtFixedRate
argument_list|(
operator|new
name|HBaseMonitor
argument_list|()
argument_list|,
literal|0
argument_list|,
name|monitorInterval
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceStop ()
specifier|protected
name|void
name|serviceStop
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|conn
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"closing the hbase Connection"
argument_list|)
expr_stmt|;
name|conn
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|monitorExecutorService
operator|!=
literal|null
condition|)
block|{
name|monitorExecutorService
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|monitorExecutorService
operator|.
name|awaitTermination
argument_list|(
literal|30
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"failed to stop the monitir task in time. "
operator|+
literal|"will still proceed to close the monitor."
argument_list|)
expr_stmt|;
block|}
block|}
name|monitorConn
operator|.
name|close
argument_list|()
expr_stmt|;
name|super
operator|.
name|serviceStop
argument_list|()
expr_stmt|;
block|}
DECL|method|checkHBaseDown ()
specifier|private
name|void
name|checkHBaseDown
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|hbaseDown
operator|.
name|get
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"HBase is down"
argument_list|)
throw|;
block|}
block|}
DECL|method|isHBaseDown ()
specifier|public
name|boolean
name|isHBaseDown
parameter_list|()
block|{
return|return
name|hbaseDown
operator|.
name|get
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getEntity (TimelineReaderContext context, TimelineDataToRetrieve dataToRetrieve)
specifier|public
name|TimelineEntity
name|getEntity
parameter_list|(
name|TimelineReaderContext
name|context
parameter_list|,
name|TimelineDataToRetrieve
name|dataToRetrieve
parameter_list|)
throws|throws
name|IOException
block|{
name|checkHBaseDown
argument_list|()
expr_stmt|;
name|TimelineEntityReader
name|reader
init|=
name|TimelineEntityReaderFactory
operator|.
name|createSingleEntityReader
argument_list|(
name|context
argument_list|,
name|dataToRetrieve
argument_list|)
decl_stmt|;
return|return
name|reader
operator|.
name|readEntity
argument_list|(
name|hbaseConf
argument_list|,
name|conn
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getEntities (TimelineReaderContext context, TimelineEntityFilters filters, TimelineDataToRetrieve dataToRetrieve)
specifier|public
name|Set
argument_list|<
name|TimelineEntity
argument_list|>
name|getEntities
parameter_list|(
name|TimelineReaderContext
name|context
parameter_list|,
name|TimelineEntityFilters
name|filters
parameter_list|,
name|TimelineDataToRetrieve
name|dataToRetrieve
parameter_list|)
throws|throws
name|IOException
block|{
name|checkHBaseDown
argument_list|()
expr_stmt|;
name|TimelineEntityReader
name|reader
init|=
name|TimelineEntityReaderFactory
operator|.
name|createMultipleEntitiesReader
argument_list|(
name|context
argument_list|,
name|filters
argument_list|,
name|dataToRetrieve
argument_list|)
decl_stmt|;
return|return
name|reader
operator|.
name|readEntities
argument_list|(
name|hbaseConf
argument_list|,
name|conn
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getEntityTypes (TimelineReaderContext context)
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getEntityTypes
parameter_list|(
name|TimelineReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|checkHBaseDown
argument_list|()
expr_stmt|;
name|EntityTypeReader
name|reader
init|=
operator|new
name|EntityTypeReader
argument_list|(
name|context
argument_list|)
decl_stmt|;
return|return
name|reader
operator|.
name|readEntityTypes
argument_list|(
name|hbaseConf
argument_list|,
name|conn
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getHealthStatus ()
specifier|public
name|TimelineHealth
name|getHealthStatus
parameter_list|()
block|{
if|if
condition|(
operator|!
name|this
operator|.
name|isHBaseDown
argument_list|()
condition|)
block|{
return|return
operator|new
name|TimelineHealth
argument_list|(
name|TimelineHealth
operator|.
name|TimelineHealthStatus
operator|.
name|RUNNING
argument_list|,
literal|""
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|TimelineHealth
argument_list|(
name|TimelineHealth
operator|.
name|TimelineHealthStatus
operator|.
name|READER_CONNECTION_FAILURE
argument_list|,
literal|"HBase connection is down"
argument_list|)
return|;
block|}
block|}
DECL|field|MONITOR_FILTERS
specifier|protected
specifier|static
specifier|final
name|TimelineEntityFilters
name|MONITOR_FILTERS
init|=
operator|new
name|TimelineEntityFilters
operator|.
name|Builder
argument_list|()
operator|.
name|entityLimit
argument_list|(
literal|1L
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
DECL|field|DATA_TO_RETRIEVE
specifier|protected
specifier|static
specifier|final
name|TimelineDataToRetrieve
name|DATA_TO_RETRIEVE
init|=
operator|new
name|TimelineDataToRetrieve
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
DECL|class|HBaseMonitor
specifier|private
class|class
name|HBaseMonitor
implements|implements
name|Runnable
block|{
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Running HBase liveness monitor"
argument_list|)
expr_stmt|;
name|TimelineEntityReader
name|reader
init|=
name|TimelineEntityReaderFactory
operator|.
name|createMultipleEntitiesReader
argument_list|(
name|monitorContext
argument_list|,
name|MONITOR_FILTERS
argument_list|,
name|DATA_TO_RETRIEVE
argument_list|)
decl_stmt|;
name|reader
operator|.
name|readEntities
argument_list|(
name|monitorHBaseConf
argument_list|,
name|monitorConn
argument_list|)
expr_stmt|;
comment|// on success, reset hbase down flag
if|if
condition|(
name|hbaseDown
operator|.
name|getAndSet
argument_list|(
literal|false
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"HBase request succeeded, assuming HBase up"
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Got failure attempting to read from timeline storage, "
operator|+
literal|"assuming HBase down"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|hbaseDown
operator|.
name|getAndSet
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

