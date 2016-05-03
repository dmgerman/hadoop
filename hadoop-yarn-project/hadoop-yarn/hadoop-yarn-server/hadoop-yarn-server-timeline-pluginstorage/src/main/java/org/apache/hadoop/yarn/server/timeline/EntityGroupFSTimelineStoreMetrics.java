begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timeline
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
name|timeline
package|;
end_package

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
name|MutableStat
import|;
end_import

begin_comment
comment|/**  * This class tracks metrics for the EntityGroupFSTimelineStore. It tracks  * the read and write metrics for timeline server v1.5. It serves as a  * complement to {@link TimelineDataManagerMetrics}.  */
end_comment

begin_class
annotation|@
name|Metrics
argument_list|(
name|about
operator|=
literal|"Metrics for EntityGroupFSTimelineStore"
argument_list|,
name|context
operator|=
literal|"yarn"
argument_list|)
DECL|class|EntityGroupFSTimelineStoreMetrics
specifier|public
class|class
name|EntityGroupFSTimelineStoreMetrics
block|{
DECL|field|DEFAULT_VALUE_WITH_SCALE
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_VALUE_WITH_SCALE
init|=
literal|"TimeMs"
decl_stmt|;
comment|// General read related metrics
annotation|@
name|Metric
argument_list|(
literal|"getEntity calls to summary storage"
argument_list|)
DECL|field|getEntityToSummaryOps
specifier|private
name|MutableCounterLong
name|getEntityToSummaryOps
decl_stmt|;
annotation|@
name|Metric
argument_list|(
literal|"getEntity calls to detail storage"
argument_list|)
DECL|field|getEntityToDetailOps
specifier|private
name|MutableCounterLong
name|getEntityToDetailOps
decl_stmt|;
comment|// Summary data related metrics
annotation|@
name|Metric
argument_list|(
name|value
operator|=
literal|"summary log read ops and time"
argument_list|,
name|valueName
operator|=
name|DEFAULT_VALUE_WITH_SCALE
argument_list|)
DECL|field|summaryLogRead
specifier|private
name|MutableStat
name|summaryLogRead
decl_stmt|;
annotation|@
name|Metric
argument_list|(
literal|"entities read into the summary storage"
argument_list|)
DECL|field|entitiesReadToSummary
specifier|private
name|MutableCounterLong
name|entitiesReadToSummary
decl_stmt|;
comment|// Detail data cache related metrics
annotation|@
name|Metric
argument_list|(
literal|"cache storage read that does not require a refresh"
argument_list|)
DECL|field|noRefreshCacheRead
specifier|private
name|MutableCounterLong
name|noRefreshCacheRead
decl_stmt|;
annotation|@
name|Metric
argument_list|(
literal|"cache storage refresh due to the cached storage is stale"
argument_list|)
DECL|field|cacheStaleRefreshes
specifier|private
name|MutableCounterLong
name|cacheStaleRefreshes
decl_stmt|;
annotation|@
name|Metric
argument_list|(
literal|"cache storage evicts"
argument_list|)
DECL|field|cacheEvicts
specifier|private
name|MutableCounterLong
name|cacheEvicts
decl_stmt|;
annotation|@
name|Metric
argument_list|(
name|value
operator|=
literal|"cache storage refresh ops and time"
argument_list|,
name|valueName
operator|=
name|DEFAULT_VALUE_WITH_SCALE
argument_list|)
DECL|field|cacheRefresh
specifier|private
name|MutableStat
name|cacheRefresh
decl_stmt|;
comment|// Log scanner and cleaner related metrics
annotation|@
name|Metric
argument_list|(
name|value
operator|=
literal|"active log scan ops and time"
argument_list|,
name|valueName
operator|=
name|DEFAULT_VALUE_WITH_SCALE
argument_list|)
DECL|field|activeLogDirScan
specifier|private
name|MutableStat
name|activeLogDirScan
decl_stmt|;
annotation|@
name|Metric
argument_list|(
name|value
operator|=
literal|"log cleaner purging ops and time"
argument_list|,
name|valueName
operator|=
name|DEFAULT_VALUE_WITH_SCALE
argument_list|)
DECL|field|logClean
specifier|private
name|MutableStat
name|logClean
decl_stmt|;
annotation|@
name|Metric
argument_list|(
literal|"log cleaner dirs purged"
argument_list|)
DECL|field|logsDirsCleaned
specifier|private
name|MutableCounterLong
name|logsDirsCleaned
decl_stmt|;
DECL|field|instance
specifier|private
specifier|static
name|EntityGroupFSTimelineStoreMetrics
name|instance
init|=
literal|null
decl_stmt|;
DECL|method|EntityGroupFSTimelineStoreMetrics ()
name|EntityGroupFSTimelineStoreMetrics
parameter_list|()
block|{   }
DECL|method|create ()
specifier|public
specifier|static
specifier|synchronized
name|EntityGroupFSTimelineStoreMetrics
name|create
parameter_list|()
block|{
if|if
condition|(
name|instance
operator|==
literal|null
condition|)
block|{
name|MetricsSystem
name|ms
init|=
name|DefaultMetricsSystem
operator|.
name|instance
argument_list|()
decl_stmt|;
name|instance
operator|=
name|ms
operator|.
name|register
argument_list|(
operator|new
name|EntityGroupFSTimelineStoreMetrics
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|instance
return|;
block|}
comment|// Setters
comment|// General read related
DECL|method|incrGetEntityToSummaryOps ()
specifier|public
name|void
name|incrGetEntityToSummaryOps
parameter_list|()
block|{
name|getEntityToSummaryOps
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|incrGetEntityToDetailOps ()
specifier|public
name|void
name|incrGetEntityToDetailOps
parameter_list|()
block|{
name|getEntityToDetailOps
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
comment|// Summary data related
DECL|method|addSummaryLogReadTime (long msec)
specifier|public
name|void
name|addSummaryLogReadTime
parameter_list|(
name|long
name|msec
parameter_list|)
block|{
name|summaryLogRead
operator|.
name|add
argument_list|(
name|msec
argument_list|)
expr_stmt|;
block|}
DECL|method|incrEntitiesReadToSummary (long delta)
specifier|public
name|void
name|incrEntitiesReadToSummary
parameter_list|(
name|long
name|delta
parameter_list|)
block|{
name|entitiesReadToSummary
operator|.
name|incr
argument_list|(
name|delta
argument_list|)
expr_stmt|;
block|}
comment|// Cache related
DECL|method|incrNoRefreshCacheRead ()
specifier|public
name|void
name|incrNoRefreshCacheRead
parameter_list|()
block|{
name|noRefreshCacheRead
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|incrCacheStaleRefreshes ()
specifier|public
name|void
name|incrCacheStaleRefreshes
parameter_list|()
block|{
name|cacheStaleRefreshes
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|incrCacheEvicts ()
specifier|public
name|void
name|incrCacheEvicts
parameter_list|()
block|{
name|cacheEvicts
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|addCacheRefreshTime (long msec)
specifier|public
name|void
name|addCacheRefreshTime
parameter_list|(
name|long
name|msec
parameter_list|)
block|{
name|cacheRefresh
operator|.
name|add
argument_list|(
name|msec
argument_list|)
expr_stmt|;
block|}
comment|// Log scanner and cleaner related
DECL|method|addActiveLogDirScanTime (long msec)
specifier|public
name|void
name|addActiveLogDirScanTime
parameter_list|(
name|long
name|msec
parameter_list|)
block|{
name|activeLogDirScan
operator|.
name|add
argument_list|(
name|msec
argument_list|)
expr_stmt|;
block|}
DECL|method|addLogCleanTime (long msec)
specifier|public
name|void
name|addLogCleanTime
parameter_list|(
name|long
name|msec
parameter_list|)
block|{
name|logClean
operator|.
name|add
argument_list|(
name|msec
argument_list|)
expr_stmt|;
block|}
DECL|method|incrLogsDirsCleaned ()
specifier|public
name|void
name|incrLogsDirsCleaned
parameter_list|()
block|{
name|logsDirsCleaned
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
comment|// Getters
DECL|method|getEntitiesReadToSummary ()
name|MutableCounterLong
name|getEntitiesReadToSummary
parameter_list|()
block|{
return|return
name|entitiesReadToSummary
return|;
block|}
DECL|method|getLogsDirsCleaned ()
name|MutableCounterLong
name|getLogsDirsCleaned
parameter_list|()
block|{
return|return
name|logsDirsCleaned
return|;
block|}
DECL|method|getGetEntityToSummaryOps ()
name|MutableCounterLong
name|getGetEntityToSummaryOps
parameter_list|()
block|{
return|return
name|getEntityToSummaryOps
return|;
block|}
DECL|method|getGetEntityToDetailOps ()
name|MutableCounterLong
name|getGetEntityToDetailOps
parameter_list|()
block|{
return|return
name|getEntityToDetailOps
return|;
block|}
DECL|method|getCacheRefresh ()
name|MutableStat
name|getCacheRefresh
parameter_list|()
block|{
return|return
name|cacheRefresh
return|;
block|}
block|}
end_class

end_unit

