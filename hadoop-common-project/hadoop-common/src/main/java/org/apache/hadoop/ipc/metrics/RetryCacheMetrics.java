begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ipc.metrics
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ipc
operator|.
name|metrics
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
name|ipc
operator|.
name|RetryCache
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
comment|/**  * This class is for maintaining the various RetryCache-related statistics  * and publishing them through the metrics interfaces.  */
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
literal|"Aggregate RetryCache metrics"
argument_list|,
name|context
operator|=
literal|"rpc"
argument_list|)
DECL|class|RetryCacheMetrics
specifier|public
class|class
name|RetryCacheMetrics
block|{
DECL|field|LOG
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|RetryCacheMetrics
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|registry
specifier|final
name|MetricsRegistry
name|registry
decl_stmt|;
DECL|field|name
specifier|final
name|String
name|name
decl_stmt|;
DECL|method|RetryCacheMetrics (RetryCache retryCache)
name|RetryCacheMetrics
parameter_list|(
name|RetryCache
name|retryCache
parameter_list|)
block|{
name|name
operator|=
literal|"RetryCache."
operator|+
name|retryCache
operator|.
name|getCacheName
argument_list|()
expr_stmt|;
name|registry
operator|=
operator|new
name|MetricsRegistry
argument_list|(
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Initialized "
operator|+
name|registry
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
DECL|method|create (RetryCache cache)
specifier|public
specifier|static
name|RetryCacheMetrics
name|create
parameter_list|(
name|RetryCache
name|cache
parameter_list|)
block|{
name|RetryCacheMetrics
name|m
init|=
operator|new
name|RetryCacheMetrics
argument_list|(
name|cache
argument_list|)
decl_stmt|;
return|return
name|DefaultMetricsSystem
operator|.
name|instance
argument_list|()
operator|.
name|register
argument_list|(
name|m
operator|.
name|name
argument_list|,
literal|null
argument_list|,
name|m
argument_list|)
return|;
block|}
DECL|field|cacheHit
annotation|@
name|Metric
argument_list|(
literal|"Number of RetryCache hit"
argument_list|)
name|MutableCounterLong
name|cacheHit
decl_stmt|;
DECL|field|cacheCleared
annotation|@
name|Metric
argument_list|(
literal|"Number of RetryCache cleared"
argument_list|)
name|MutableCounterLong
name|cacheCleared
decl_stmt|;
DECL|field|cacheUpdated
annotation|@
name|Metric
argument_list|(
literal|"Number of RetryCache updated"
argument_list|)
name|MutableCounterLong
name|cacheUpdated
decl_stmt|;
comment|/**    * One cache hit event    */
DECL|method|incrCacheHit ()
specifier|public
name|void
name|incrCacheHit
parameter_list|()
block|{
name|cacheHit
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
comment|/**    * One cache cleared    */
DECL|method|incrCacheCleared ()
specifier|public
name|void
name|incrCacheCleared
parameter_list|()
block|{
name|cacheCleared
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
comment|/**    * One cache updated    */
DECL|method|incrCacheUpdated ()
specifier|public
name|void
name|incrCacheUpdated
parameter_list|()
block|{
name|cacheUpdated
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|getCacheHit ()
specifier|public
name|long
name|getCacheHit
parameter_list|()
block|{
return|return
name|cacheHit
operator|.
name|value
argument_list|()
return|;
block|}
DECL|method|getCacheCleared ()
specifier|public
name|long
name|getCacheCleared
parameter_list|()
block|{
return|return
name|cacheCleared
operator|.
name|value
argument_list|()
return|;
block|}
DECL|method|getCacheUpdated ()
specifier|public
name|long
name|getCacheUpdated
parameter_list|()
block|{
return|return
name|cacheUpdated
operator|.
name|value
argument_list|()
return|;
block|}
block|}
end_class

end_unit

