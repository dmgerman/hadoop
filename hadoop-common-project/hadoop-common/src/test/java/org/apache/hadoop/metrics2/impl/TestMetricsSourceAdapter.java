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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|*
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
name|List
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
name|ScheduledFuture
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ThreadFactoryBuilder
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
name|MetricsCollector
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
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|metrics2
operator|.
name|MetricsSource
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
name|MetricsTag
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
name|MetricsAnnotations
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
name|MetricsSourceBuilder
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
import|import static
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
name|Interns
operator|.
name|info
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
name|assertEquals
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Logger
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
import|import
name|javax
operator|.
name|management
operator|.
name|MBeanAttributeInfo
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|MBeanInfo
import|;
end_import

begin_class
DECL|class|TestMetricsSourceAdapter
specifier|public
class|class
name|TestMetricsSourceAdapter
block|{
DECL|field|RACE_TEST_RUNTIME
specifier|private
specifier|static
specifier|final
name|int
name|RACE_TEST_RUNTIME
init|=
literal|10000
decl_stmt|;
comment|// 10 seconds
annotation|@
name|Test
DECL|method|testPurgeOldMetrics ()
specifier|public
name|void
name|testPurgeOldMetrics
parameter_list|()
throws|throws
name|Exception
block|{
comment|// create test source with a single metric counter of value 1
name|PurgableSource
name|source
init|=
operator|new
name|PurgableSource
argument_list|()
decl_stmt|;
name|MetricsSourceBuilder
name|sb
init|=
name|MetricsAnnotations
operator|.
name|newSourceBuilder
argument_list|(
name|source
argument_list|)
decl_stmt|;
specifier|final
name|MetricsSource
name|s
init|=
name|sb
operator|.
name|build
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|MetricsTag
argument_list|>
name|injectedTags
init|=
operator|new
name|ArrayList
argument_list|<
name|MetricsTag
argument_list|>
argument_list|()
decl_stmt|;
name|MetricsSourceAdapter
name|sa
init|=
operator|new
name|MetricsSourceAdapter
argument_list|(
literal|"tst"
argument_list|,
literal|"tst"
argument_list|,
literal|"testdesc"
argument_list|,
name|s
argument_list|,
name|injectedTags
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|MBeanInfo
name|info
init|=
name|sa
operator|.
name|getMBeanInfo
argument_list|()
decl_stmt|;
name|boolean
name|sawIt
init|=
literal|false
decl_stmt|;
for|for
control|(
name|MBeanAttributeInfo
name|mBeanAttributeInfo
range|:
name|info
operator|.
name|getAttributes
argument_list|()
control|)
block|{
name|sawIt
operator||=
name|mBeanAttributeInfo
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|source
operator|.
name|lastKeyName
argument_list|)
expr_stmt|;
block|}
empty_stmt|;
name|assertTrue
argument_list|(
literal|"The last generated metric is not exported to jmx"
argument_list|,
name|sawIt
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
comment|// skip JMX cache TTL
name|info
operator|=
name|sa
operator|.
name|getMBeanInfo
argument_list|()
expr_stmt|;
name|sawIt
operator|=
literal|false
expr_stmt|;
for|for
control|(
name|MBeanAttributeInfo
name|mBeanAttributeInfo
range|:
name|info
operator|.
name|getAttributes
argument_list|()
control|)
block|{
name|sawIt
operator||=
name|mBeanAttributeInfo
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|source
operator|.
name|lastKeyName
argument_list|)
expr_stmt|;
block|}
empty_stmt|;
name|assertTrue
argument_list|(
literal|"The last generated metric is not exported to jmx"
argument_list|,
name|sawIt
argument_list|)
expr_stmt|;
block|}
comment|//generate a new key per each call
DECL|class|PurgableSource
specifier|private
specifier|static
class|class
name|PurgableSource
implements|implements
name|MetricsSource
block|{
DECL|field|nextKey
name|int
name|nextKey
init|=
literal|0
decl_stmt|;
DECL|field|lastKeyName
name|String
name|lastKeyName
init|=
literal|null
decl_stmt|;
annotation|@
name|Override
DECL|method|getMetrics (MetricsCollector collector, boolean all)
specifier|public
name|void
name|getMetrics
parameter_list|(
name|MetricsCollector
name|collector
parameter_list|,
name|boolean
name|all
parameter_list|)
block|{
name|MetricsRecordBuilder
name|rb
init|=
name|collector
operator|.
name|addRecord
argument_list|(
literal|"purgablesource"
argument_list|)
operator|.
name|setContext
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|lastKeyName
operator|=
literal|"key"
operator|+
name|nextKey
operator|++
expr_stmt|;
name|rb
operator|.
name|addGauge
argument_list|(
name|info
argument_list|(
name|lastKeyName
argument_list|,
literal|"desc"
argument_list|)
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testGetMetricsAndJmx ()
specifier|public
name|void
name|testGetMetricsAndJmx
parameter_list|()
throws|throws
name|Exception
block|{
comment|// create test source with a single metric counter of value 0
name|TestSource
name|source
init|=
operator|new
name|TestSource
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|MetricsSourceBuilder
name|sb
init|=
name|MetricsAnnotations
operator|.
name|newSourceBuilder
argument_list|(
name|source
argument_list|)
decl_stmt|;
specifier|final
name|MetricsSource
name|s
init|=
name|sb
operator|.
name|build
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|MetricsTag
argument_list|>
name|injectedTags
init|=
operator|new
name|ArrayList
argument_list|<
name|MetricsTag
argument_list|>
argument_list|()
decl_stmt|;
name|MetricsSourceAdapter
name|sa
init|=
operator|new
name|MetricsSourceAdapter
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|,
literal|"test desc"
argument_list|,
name|s
argument_list|,
name|injectedTags
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|)
decl_stmt|;
comment|// all metrics are initially assumed to have changed
name|MetricsCollectorImpl
name|builder
init|=
operator|new
name|MetricsCollectorImpl
argument_list|()
decl_stmt|;
name|Iterable
argument_list|<
name|MetricsRecordImpl
argument_list|>
name|metricsRecords
init|=
name|sa
operator|.
name|getMetrics
argument_list|(
name|builder
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|// Validate getMetrics and JMX initial values
name|MetricsRecordImpl
name|metricsRecord
init|=
name|metricsRecords
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|0L
argument_list|,
name|metricsRecord
operator|.
name|metrics
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|value
argument_list|()
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
comment|// skip JMX cache TTL
name|assertEquals
argument_list|(
literal|0L
argument_list|,
operator|(
name|Number
operator|)
name|sa
operator|.
name|getAttribute
argument_list|(
literal|"C1"
argument_list|)
argument_list|)
expr_stmt|;
comment|// change metric value
name|source
operator|.
name|incrementCnt
argument_list|()
expr_stmt|;
comment|// validate getMetrics and JMX
name|builder
operator|=
operator|new
name|MetricsCollectorImpl
argument_list|()
expr_stmt|;
name|metricsRecords
operator|=
name|sa
operator|.
name|getMetrics
argument_list|(
name|builder
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|metricsRecord
operator|=
name|metricsRecords
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|metricsRecord
operator|.
name|metrics
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
comment|// skip JMX cache TTL
name|assertEquals
argument_list|(
literal|1L
argument_list|,
operator|(
name|Number
operator|)
name|sa
operator|.
name|getAttribute
argument_list|(
literal|"C1"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
annotation|@
name|Metrics
argument_list|(
name|context
operator|=
literal|"test"
argument_list|)
DECL|class|TestSource
specifier|private
specifier|static
class|class
name|TestSource
block|{
DECL|field|c1
annotation|@
name|Metric
argument_list|(
literal|"C1 desc"
argument_list|)
name|MutableCounterLong
name|c1
decl_stmt|;
DECL|field|registry
specifier|final
name|MetricsRegistry
name|registry
decl_stmt|;
DECL|method|TestSource (String recName)
name|TestSource
parameter_list|(
name|String
name|recName
parameter_list|)
block|{
name|registry
operator|=
operator|new
name|MetricsRegistry
argument_list|(
name|recName
argument_list|)
expr_stmt|;
block|}
DECL|method|incrementCnt ()
specifier|public
name|void
name|incrementCnt
parameter_list|()
block|{
name|c1
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Test a race condition when updating the JMX cache (HADOOP-12482):    * 1. Thread A reads the JMX metric every 2 JMX cache TTL. It marks the JMX    *    cache to be updated by marking lastRecs to null. After this it adds a    *    new key to the metrics. The next call to read should pick up this new    *    key.    * 2. Thread B triggers JMX metric update every 1 JMX cache TTL. It assigns    *    lastRecs to a new object (not null any more).    * 3. Thread A tries to read JMX metric again, sees lastRecs is not null and    *    does not update JMX cache. As a result the read does not pickup the new    *    metric.    * @throws Exception    */
annotation|@
name|Test
DECL|method|testMetricCacheUpdateRace ()
specifier|public
name|void
name|testMetricCacheUpdateRace
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Create test source with a single metric counter of value 1.
name|TestMetricsSource
name|source
init|=
operator|new
name|TestMetricsSource
argument_list|()
decl_stmt|;
name|MetricsSourceBuilder
name|sourceBuilder
init|=
name|MetricsAnnotations
operator|.
name|newSourceBuilder
argument_list|(
name|source
argument_list|)
decl_stmt|;
specifier|final
name|long
name|JMX_CACHE_TTL
init|=
literal|250
decl_stmt|;
comment|// ms
name|List
argument_list|<
name|MetricsTag
argument_list|>
name|injectedTags
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|MetricsSourceAdapter
name|sourceAdapter
init|=
operator|new
name|MetricsSourceAdapter
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|,
literal|"test JMX cache update race condition"
argument_list|,
name|sourceBuilder
operator|.
name|build
argument_list|()
argument_list|,
name|injectedTags
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|JMX_CACHE_TTL
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|ScheduledExecutorService
name|updaterExecutor
init|=
name|Executors
operator|.
name|newScheduledThreadPool
argument_list|(
literal|1
argument_list|,
operator|new
name|ThreadFactoryBuilder
argument_list|()
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
name|ScheduledExecutorService
name|readerExecutor
init|=
name|Executors
operator|.
name|newScheduledThreadPool
argument_list|(
literal|1
argument_list|,
operator|new
name|ThreadFactoryBuilder
argument_list|()
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|AtomicBoolean
name|hasError
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
comment|// Wake up every 1 JMX cache TTL to set lastRecs before updateJmxCache() is
comment|// called.
name|SourceUpdater
name|srcUpdater
init|=
operator|new
name|SourceUpdater
argument_list|(
name|sourceAdapter
argument_list|,
name|hasError
argument_list|)
decl_stmt|;
name|ScheduledFuture
argument_list|<
name|?
argument_list|>
name|updaterFuture
init|=
name|updaterExecutor
operator|.
name|scheduleAtFixedRate
argument_list|(
name|srcUpdater
argument_list|,
name|sourceAdapter
operator|.
name|getJmxCacheTTL
argument_list|()
argument_list|,
name|sourceAdapter
operator|.
name|getJmxCacheTTL
argument_list|()
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
name|srcUpdater
operator|.
name|setFuture
argument_list|(
name|updaterFuture
argument_list|)
expr_stmt|;
comment|// Wake up every 2 JMX cache TTL so updateJmxCache() will try to update
comment|// JMX cache.
name|SourceReader
name|srcReader
init|=
operator|new
name|SourceReader
argument_list|(
name|source
argument_list|,
name|sourceAdapter
argument_list|,
name|hasError
argument_list|)
decl_stmt|;
name|ScheduledFuture
argument_list|<
name|?
argument_list|>
name|readerFuture
init|=
name|readerExecutor
operator|.
name|scheduleAtFixedRate
argument_list|(
name|srcReader
argument_list|,
literal|0
argument_list|,
comment|// set JMX info cache at the beginning
literal|2
operator|*
name|sourceAdapter
operator|.
name|getJmxCacheTTL
argument_list|()
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
name|srcReader
operator|.
name|setFuture
argument_list|(
name|readerFuture
argument_list|)
expr_stmt|;
comment|// Let the threads do their work.
name|Thread
operator|.
name|sleep
argument_list|(
name|RACE_TEST_RUNTIME
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Hit error"
argument_list|,
name|hasError
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
comment|// cleanup
name|updaterExecutor
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
name|readerExecutor
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
name|updaterExecutor
operator|.
name|awaitTermination
argument_list|(
literal|1000
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
name|readerExecutor
operator|.
name|awaitTermination
argument_list|(
literal|1000
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
block|}
comment|/**    * Thread safe source: stores a key value pair. Allows thread safe key-value    * pair reads/writes.    */
DECL|class|TestMetricsSource
specifier|private
specifier|static
class|class
name|TestMetricsSource
implements|implements
name|MetricsSource
block|{
DECL|field|key
specifier|private
name|String
name|key
init|=
literal|"key0"
decl_stmt|;
DECL|field|val
specifier|private
name|int
name|val
init|=
literal|0
decl_stmt|;
DECL|method|getKey ()
specifier|synchronized
name|String
name|getKey
parameter_list|()
block|{
return|return
name|key
return|;
block|}
DECL|method|setKV (final String newKey, final int newVal)
specifier|synchronized
name|void
name|setKV
parameter_list|(
specifier|final
name|String
name|newKey
parameter_list|,
specifier|final
name|int
name|newVal
parameter_list|)
block|{
name|key
operator|=
name|newKey
expr_stmt|;
name|val
operator|=
name|newVal
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getMetrics (MetricsCollector collector, boolean all)
specifier|public
name|void
name|getMetrics
parameter_list|(
name|MetricsCollector
name|collector
parameter_list|,
name|boolean
name|all
parameter_list|)
block|{
name|MetricsRecordBuilder
name|rb
init|=
name|collector
operator|.
name|addRecord
argument_list|(
literal|"TestMetricsSource"
argument_list|)
operator|.
name|setContext
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|rb
operator|.
name|addGauge
argument_list|(
name|info
argument_list|(
name|key
argument_list|,
literal|"TestMetricsSource key"
argument_list|)
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * An thread that updates the metrics source every 1 JMX cache TTL    */
DECL|class|SourceUpdater
specifier|private
specifier|static
class|class
name|SourceUpdater
implements|implements
name|Runnable
block|{
DECL|field|sa
specifier|private
name|MetricsSourceAdapter
name|sa
init|=
literal|null
decl_stmt|;
DECL|field|future
specifier|private
name|ScheduledFuture
argument_list|<
name|?
argument_list|>
name|future
init|=
literal|null
decl_stmt|;
DECL|field|hasError
specifier|private
name|AtomicBoolean
name|hasError
init|=
literal|null
decl_stmt|;
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|SourceUpdater
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|SourceUpdater (MetricsSourceAdapter sourceAdapter, AtomicBoolean err)
specifier|public
name|SourceUpdater
parameter_list|(
name|MetricsSourceAdapter
name|sourceAdapter
parameter_list|,
name|AtomicBoolean
name|err
parameter_list|)
block|{
name|sa
operator|=
name|sourceAdapter
expr_stmt|;
name|hasError
operator|=
name|err
expr_stmt|;
block|}
DECL|method|setFuture (ScheduledFuture<?> f)
specifier|public
name|void
name|setFuture
parameter_list|(
name|ScheduledFuture
argument_list|<
name|?
argument_list|>
name|f
parameter_list|)
block|{
name|future
operator|=
name|f
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
name|MetricsCollectorImpl
name|builder
init|=
operator|new
name|MetricsCollectorImpl
argument_list|()
decl_stmt|;
try|try
block|{
comment|// This resets lastRecs.
name|sa
operator|.
name|getMetrics
argument_list|(
name|builder
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"reset lastRecs"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// catch all errors
name|hasError
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|e
operator|.
name|getStackTrace
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|hasError
operator|.
name|get
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Hit error, stopping now"
argument_list|)
expr_stmt|;
name|future
operator|.
name|cancel
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/**    * An thread that reads the metrics source every JMX cache TTL. After each    * read it updates the metric source to report a new key. The next read must    * be able to pick up this new key.    */
DECL|class|SourceReader
specifier|private
specifier|static
class|class
name|SourceReader
implements|implements
name|Runnable
block|{
DECL|field|sa
specifier|private
name|MetricsSourceAdapter
name|sa
init|=
literal|null
decl_stmt|;
DECL|field|src
specifier|private
name|TestMetricsSource
name|src
init|=
literal|null
decl_stmt|;
DECL|field|cnt
specifier|private
name|int
name|cnt
init|=
literal|0
decl_stmt|;
DECL|field|future
specifier|private
name|ScheduledFuture
argument_list|<
name|?
argument_list|>
name|future
init|=
literal|null
decl_stmt|;
DECL|field|hasError
specifier|private
name|AtomicBoolean
name|hasError
init|=
literal|null
decl_stmt|;
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|SourceReader
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|SourceReader ( TestMetricsSource source, MetricsSourceAdapter sourceAdapter, AtomicBoolean err)
specifier|public
name|SourceReader
parameter_list|(
name|TestMetricsSource
name|source
parameter_list|,
name|MetricsSourceAdapter
name|sourceAdapter
parameter_list|,
name|AtomicBoolean
name|err
parameter_list|)
block|{
name|src
operator|=
name|source
expr_stmt|;
name|sa
operator|=
name|sourceAdapter
expr_stmt|;
name|hasError
operator|=
name|err
expr_stmt|;
block|}
DECL|method|setFuture (ScheduledFuture<?> f)
specifier|public
name|void
name|setFuture
parameter_list|(
name|ScheduledFuture
argument_list|<
name|?
argument_list|>
name|f
parameter_list|)
block|{
name|future
operator|=
name|f
expr_stmt|;
block|}
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
comment|// This will trigger updateJmxCache().
name|MBeanInfo
name|info
init|=
name|sa
operator|.
name|getMBeanInfo
argument_list|()
decl_stmt|;
specifier|final
name|String
name|key
init|=
name|src
operator|.
name|getKey
argument_list|()
decl_stmt|;
for|for
control|(
name|MBeanAttributeInfo
name|mBeanAttributeInfo
range|:
name|info
operator|.
name|getAttributes
argument_list|()
control|)
block|{
comment|// Found the new key, update the metric source and move on.
if|if
condition|(
name|mBeanAttributeInfo
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|key
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"found key/val="
operator|+
name|cnt
operator|+
literal|"/"
operator|+
name|cnt
argument_list|)
expr_stmt|;
name|cnt
operator|++
expr_stmt|;
name|src
operator|.
name|setKV
argument_list|(
literal|"key"
operator|+
name|cnt
argument_list|,
name|cnt
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
name|LOG
operator|.
name|error
argument_list|(
literal|"key="
operator|+
name|key
operator|+
literal|" not found. Stopping now."
argument_list|)
expr_stmt|;
name|hasError
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// catch other errors
name|hasError
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|e
operator|.
name|getStackTrace
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|hasError
operator|.
name|get
argument_list|()
condition|)
block|{
name|future
operator|.
name|cancel
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

