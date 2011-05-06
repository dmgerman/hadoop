begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.metrics2.impl
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|metrics2
operator|.
name|impl
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|*
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
name|MutableCounterInt
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
name|MetricsRecordBuilder
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
name|util
operator|.
name|Contracts
operator|.
name|*
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
name|MetricsFilter
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
name|MetricsSink
import|;
end_import

begin_comment
comment|/**  * An adapter class for metrics sink and associated filters  */
end_comment

begin_class
DECL|class|MetricsSinkAdapter
class|class
name|MetricsSinkAdapter
implements|implements
name|SinkQueue
operator|.
name|Consumer
argument_list|<
name|MetricsBuffer
argument_list|>
block|{
DECL|field|LOG
specifier|private
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|MetricsSinkAdapter
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|name
DECL|field|description
DECL|field|context
specifier|private
specifier|final
name|String
name|name
decl_stmt|,
name|description
decl_stmt|,
name|context
decl_stmt|;
DECL|field|sink
specifier|private
specifier|final
name|MetricsSink
name|sink
decl_stmt|;
DECL|field|sourceFilter
DECL|field|recordFilter
DECL|field|metricFilter
specifier|private
specifier|final
name|MetricsFilter
name|sourceFilter
decl_stmt|,
name|recordFilter
decl_stmt|,
name|metricFilter
decl_stmt|;
DECL|field|queue
specifier|private
specifier|final
name|SinkQueue
argument_list|<
name|MetricsBuffer
argument_list|>
name|queue
decl_stmt|;
DECL|field|sinkThread
specifier|private
specifier|final
name|Thread
name|sinkThread
decl_stmt|;
DECL|field|stopping
specifier|private
specifier|volatile
name|boolean
name|stopping
init|=
literal|false
decl_stmt|;
DECL|field|inError
specifier|private
specifier|volatile
name|boolean
name|inError
init|=
literal|false
decl_stmt|;
DECL|field|period
DECL|field|firstRetryDelay
DECL|field|retryCount
specifier|private
specifier|final
name|int
name|period
decl_stmt|,
name|firstRetryDelay
decl_stmt|,
name|retryCount
decl_stmt|;
DECL|field|retryBackoff
specifier|private
specifier|final
name|float
name|retryBackoff
decl_stmt|;
DECL|field|registry
specifier|private
specifier|final
name|MetricsRegistry
name|registry
init|=
operator|new
name|MetricsRegistry
argument_list|(
literal|"sinkadapter"
argument_list|)
decl_stmt|;
DECL|field|latency
specifier|private
specifier|final
name|MutableStat
name|latency
decl_stmt|;
DECL|field|dropped
specifier|private
specifier|final
name|MutableCounterInt
name|dropped
decl_stmt|;
DECL|field|qsize
specifier|private
specifier|final
name|MutableGaugeInt
name|qsize
decl_stmt|;
DECL|method|MetricsSinkAdapter (String name, String description, MetricsSink sink, String context, MetricsFilter sourceFilter, MetricsFilter recordFilter, MetricsFilter metricFilter, int period, int queueCapacity, int retryDelay, float retryBackoff, int retryCount)
name|MetricsSinkAdapter
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|description
parameter_list|,
name|MetricsSink
name|sink
parameter_list|,
name|String
name|context
parameter_list|,
name|MetricsFilter
name|sourceFilter
parameter_list|,
name|MetricsFilter
name|recordFilter
parameter_list|,
name|MetricsFilter
name|metricFilter
parameter_list|,
name|int
name|period
parameter_list|,
name|int
name|queueCapacity
parameter_list|,
name|int
name|retryDelay
parameter_list|,
name|float
name|retryBackoff
parameter_list|,
name|int
name|retryCount
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|checkNotNull
argument_list|(
name|name
argument_list|,
literal|"name"
argument_list|)
expr_stmt|;
name|this
operator|.
name|description
operator|=
name|description
expr_stmt|;
name|this
operator|.
name|sink
operator|=
name|checkNotNull
argument_list|(
name|sink
argument_list|,
literal|"sink object"
argument_list|)
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
name|this
operator|.
name|sourceFilter
operator|=
name|sourceFilter
expr_stmt|;
name|this
operator|.
name|recordFilter
operator|=
name|recordFilter
expr_stmt|;
name|this
operator|.
name|metricFilter
operator|=
name|metricFilter
expr_stmt|;
name|this
operator|.
name|period
operator|=
name|checkArg
argument_list|(
name|period
argument_list|,
name|period
operator|>
literal|0
argument_list|,
literal|"period"
argument_list|)
expr_stmt|;
name|firstRetryDelay
operator|=
name|checkArg
argument_list|(
name|retryDelay
argument_list|,
name|retryDelay
operator|>
literal|0
argument_list|,
literal|"retry delay"
argument_list|)
expr_stmt|;
name|this
operator|.
name|retryBackoff
operator|=
name|checkArg
argument_list|(
name|retryBackoff
argument_list|,
name|retryBackoff
operator|>
literal|1
argument_list|,
literal|"retry backoff"
argument_list|)
expr_stmt|;
name|this
operator|.
name|retryCount
operator|=
name|retryCount
expr_stmt|;
name|this
operator|.
name|queue
operator|=
operator|new
name|SinkQueue
argument_list|<
name|MetricsBuffer
argument_list|>
argument_list|(
name|checkArg
argument_list|(
name|queueCapacity
argument_list|,
name|queueCapacity
operator|>
literal|0
argument_list|,
literal|"queue capacity"
argument_list|)
argument_list|)
expr_stmt|;
name|latency
operator|=
name|registry
operator|.
name|newRate
argument_list|(
literal|"Sink_"
operator|+
name|name
argument_list|,
literal|"Sink end to end latency"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|dropped
operator|=
name|registry
operator|.
name|newCounter
argument_list|(
literal|"Sink_"
operator|+
name|name
operator|+
literal|"Dropped"
argument_list|,
literal|"Dropped updates per sink"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|qsize
operator|=
name|registry
operator|.
name|newGauge
argument_list|(
literal|"Sink_"
operator|+
name|name
operator|+
literal|"Qsize"
argument_list|,
literal|"Queue size"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|sinkThread
operator|=
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|publishMetricsFromQueue
argument_list|()
expr_stmt|;
block|}
block|}
expr_stmt|;
name|sinkThread
operator|.
name|setName
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|sinkThread
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|putMetrics (MetricsBuffer buffer, long logicalTime)
name|boolean
name|putMetrics
parameter_list|(
name|MetricsBuffer
name|buffer
parameter_list|,
name|long
name|logicalTime
parameter_list|)
block|{
if|if
condition|(
name|logicalTime
operator|%
name|period
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"enqueue, logicalTime="
operator|+
name|logicalTime
argument_list|)
expr_stmt|;
if|if
condition|(
name|queue
operator|.
name|enqueue
argument_list|(
name|buffer
argument_list|)
condition|)
return|return
literal|true
return|;
name|dropped
operator|.
name|incr
argument_list|()
expr_stmt|;
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
comment|// OK
block|}
DECL|method|publishMetricsFromQueue ()
name|void
name|publishMetricsFromQueue
parameter_list|()
block|{
name|int
name|retryDelay
init|=
name|firstRetryDelay
decl_stmt|;
name|int
name|n
init|=
name|retryCount
decl_stmt|;
name|int
name|minDelay
init|=
name|Math
operator|.
name|min
argument_list|(
literal|500
argument_list|,
name|retryDelay
operator|*
literal|1000
argument_list|)
decl_stmt|;
comment|// millis
name|Random
name|rng
init|=
operator|new
name|Random
argument_list|(
name|System
operator|.
name|nanoTime
argument_list|()
argument_list|)
decl_stmt|;
while|while
condition|(
operator|!
name|stopping
condition|)
block|{
try|try
block|{
name|queue
operator|.
name|consumeAll
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|retryDelay
operator|=
name|firstRetryDelay
expr_stmt|;
name|n
operator|=
name|retryCount
expr_stmt|;
name|inError
operator|=
literal|false
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|name
operator|+
literal|" thread interrupted."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
if|if
condition|(
name|n
operator|>
literal|0
condition|)
block|{
name|int
name|retryWindow
init|=
name|Math
operator|.
name|max
argument_list|(
literal|0
argument_list|,
literal|1000
operator|/
literal|2
operator|*
name|retryDelay
operator|-
name|minDelay
argument_list|)
decl_stmt|;
name|int
name|awhile
init|=
name|rng
operator|.
name|nextInt
argument_list|(
name|retryWindow
argument_list|)
operator|+
name|minDelay
decl_stmt|;
if|if
condition|(
operator|!
name|inError
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Got sink exception, retry in "
operator|+
name|awhile
operator|+
literal|"ms"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|retryDelay
operator|*=
name|retryBackoff
expr_stmt|;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|awhile
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e2
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|name
operator|+
literal|" thread interrupted while waiting for retry"
argument_list|,
name|e2
argument_list|)
expr_stmt|;
block|}
operator|--
name|n
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
operator|!
name|inError
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Got sink exception and over retry limit, "
operator|+
literal|"suppressing further error messages"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|queue
operator|.
name|clear
argument_list|()
expr_stmt|;
name|inError
operator|=
literal|true
expr_stmt|;
comment|// Don't keep complaining ad infinitum
block|}
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|consume (MetricsBuffer buffer)
specifier|public
name|void
name|consume
parameter_list|(
name|MetricsBuffer
name|buffer
parameter_list|)
block|{
name|long
name|ts
init|=
literal|0
decl_stmt|;
for|for
control|(
name|MetricsBuffer
operator|.
name|Entry
name|entry
range|:
name|buffer
control|)
block|{
if|if
condition|(
name|sourceFilter
operator|==
literal|null
operator|||
name|sourceFilter
operator|.
name|accepts
argument_list|(
name|entry
operator|.
name|name
argument_list|()
argument_list|)
condition|)
block|{
for|for
control|(
name|MetricsRecordImpl
name|record
range|:
name|entry
operator|.
name|records
argument_list|()
control|)
block|{
if|if
condition|(
operator|(
name|context
operator|==
literal|null
operator|||
name|context
operator|.
name|equals
argument_list|(
name|record
operator|.
name|context
argument_list|()
argument_list|)
operator|)
operator|&&
operator|(
name|recordFilter
operator|==
literal|null
operator|||
name|recordFilter
operator|.
name|accepts
argument_list|(
name|record
argument_list|)
operator|)
condition|)
block|{
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
literal|"Pushing record "
operator|+
name|entry
operator|.
name|name
argument_list|()
operator|+
literal|"."
operator|+
name|record
operator|.
name|context
argument_list|()
operator|+
literal|"."
operator|+
name|record
operator|.
name|name
argument_list|()
operator|+
literal|" to "
operator|+
name|name
argument_list|)
expr_stmt|;
block|}
name|sink
operator|.
name|putMetrics
argument_list|(
name|metricFilter
operator|==
literal|null
condition|?
name|record
else|:
operator|new
name|MetricsRecordFiltered
argument_list|(
name|record
argument_list|,
name|metricFilter
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|ts
operator|==
literal|0
condition|)
name|ts
operator|=
name|record
operator|.
name|timestamp
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
if|if
condition|(
name|ts
operator|>
literal|0
condition|)
block|{
name|sink
operator|.
name|flush
argument_list|()
expr_stmt|;
name|latency
operator|.
name|add
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|ts
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"Done"
argument_list|)
expr_stmt|;
block|}
DECL|method|start ()
name|void
name|start
parameter_list|()
block|{
name|sinkThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Sink "
operator|+
name|name
operator|+
literal|" started"
argument_list|)
expr_stmt|;
block|}
DECL|method|stop ()
name|void
name|stop
parameter_list|()
block|{
name|stopping
operator|=
literal|true
expr_stmt|;
name|sinkThread
operator|.
name|interrupt
argument_list|()
expr_stmt|;
try|try
block|{
name|sinkThread
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Stop interrupted"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|name ()
name|String
name|name
parameter_list|()
block|{
return|return
name|name
return|;
block|}
DECL|method|description ()
name|String
name|description
parameter_list|()
block|{
return|return
name|description
return|;
block|}
DECL|method|snapshot (MetricsRecordBuilder rb, boolean all)
name|void
name|snapshot
parameter_list|(
name|MetricsRecordBuilder
name|rb
parameter_list|,
name|boolean
name|all
parameter_list|)
block|{
name|registry
operator|.
name|snapshot
argument_list|(
name|rb
argument_list|,
name|all
argument_list|)
expr_stmt|;
block|}
DECL|method|sink ()
name|MetricsSink
name|sink
parameter_list|()
block|{
return|return
name|sink
return|;
block|}
block|}
end_class

end_unit

