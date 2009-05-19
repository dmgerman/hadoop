begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * AbstractMetricsContext.java  *  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.metrics.spi
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|metrics
operator|.
name|spi
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|Map
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
name|Timer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TimerTask
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
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
name|metrics
operator|.
name|ContextFactory
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
name|metrics
operator|.
name|MetricsContext
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
name|metrics
operator|.
name|MetricsException
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
name|metrics
operator|.
name|MetricsRecord
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
name|metrics
operator|.
name|Updater
import|;
end_import

begin_comment
comment|/**  * The main class of the Service Provider Interface.  This class should be  * extended in order to integrate the Metrics API with a specific metrics  * client library.<p/>  *  * This class implements the internal table of metric data, and the timer  * on which data is to be sent to the metrics system.  Subclasses must  * override the abstract<code>emitRecord</code> method in order to transmit  * the data.<p/>  */
end_comment

begin_class
DECL|class|AbstractMetricsContext
specifier|public
specifier|abstract
class|class
name|AbstractMetricsContext
implements|implements
name|MetricsContext
block|{
DECL|field|period
specifier|private
name|int
name|period
init|=
name|MetricsContext
operator|.
name|DEFAULT_PERIOD
decl_stmt|;
DECL|field|timer
specifier|private
name|Timer
name|timer
init|=
literal|null
decl_stmt|;
DECL|field|updaters
specifier|private
name|Set
argument_list|<
name|Updater
argument_list|>
name|updaters
init|=
operator|new
name|HashSet
argument_list|<
name|Updater
argument_list|>
argument_list|(
literal|1
argument_list|)
decl_stmt|;
DECL|field|isMonitoring
specifier|private
specifier|volatile
name|boolean
name|isMonitoring
init|=
literal|false
decl_stmt|;
DECL|field|factory
specifier|private
name|ContextFactory
name|factory
init|=
literal|null
decl_stmt|;
DECL|field|contextName
specifier|private
name|String
name|contextName
init|=
literal|null
decl_stmt|;
DECL|class|TagMap
specifier|public
specifier|static
class|class
name|TagMap
extends|extends
name|TreeMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|3546309335061952993L
decl_stmt|;
DECL|method|TagMap ()
name|TagMap
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
DECL|method|TagMap (TagMap orig)
name|TagMap
parameter_list|(
name|TagMap
name|orig
parameter_list|)
block|{
name|super
argument_list|(
name|orig
argument_list|)
expr_stmt|;
block|}
comment|/**      * Returns true if this tagmap contains every tag in other.      */
DECL|method|containsAll (TagMap other)
specifier|public
name|boolean
name|containsAll
parameter_list|(
name|TagMap
name|other
parameter_list|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|entry
range|:
name|other
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Object
name|value
init|=
name|get
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
operator|||
operator|!
name|value
operator|.
name|equals
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
condition|)
block|{
comment|// either key does not exist here, or the value is different
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
block|}
DECL|class|MetricMap
specifier|public
specifier|static
class|class
name|MetricMap
extends|extends
name|TreeMap
argument_list|<
name|String
argument_list|,
name|Number
argument_list|>
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|7495051861141631609L
decl_stmt|;
DECL|method|MetricMap ()
name|MetricMap
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
DECL|method|MetricMap (MetricMap orig)
name|MetricMap
parameter_list|(
name|MetricMap
name|orig
parameter_list|)
block|{
name|super
argument_list|(
name|orig
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|RecordMap
specifier|static
class|class
name|RecordMap
extends|extends
name|HashMap
argument_list|<
name|TagMap
argument_list|,
name|MetricMap
argument_list|>
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|259835619700264611L
decl_stmt|;
block|}
DECL|field|bufferedData
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|RecordMap
argument_list|>
name|bufferedData
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|RecordMap
argument_list|>
argument_list|()
decl_stmt|;
comment|/**    * Creates a new instance of AbstractMetricsContext    */
DECL|method|AbstractMetricsContext ()
specifier|protected
name|AbstractMetricsContext
parameter_list|()
block|{   }
comment|/**    * Initializes the context.    */
DECL|method|init (String contextName, ContextFactory factory)
specifier|public
name|void
name|init
parameter_list|(
name|String
name|contextName
parameter_list|,
name|ContextFactory
name|factory
parameter_list|)
block|{
name|this
operator|.
name|contextName
operator|=
name|contextName
expr_stmt|;
name|this
operator|.
name|factory
operator|=
name|factory
expr_stmt|;
block|}
comment|/**    * Convenience method for subclasses to access factory attributes.    */
DECL|method|getAttribute (String attributeName)
specifier|protected
name|String
name|getAttribute
parameter_list|(
name|String
name|attributeName
parameter_list|)
block|{
name|String
name|factoryAttribute
init|=
name|contextName
operator|+
literal|"."
operator|+
name|attributeName
decl_stmt|;
return|return
operator|(
name|String
operator|)
name|factory
operator|.
name|getAttribute
argument_list|(
name|factoryAttribute
argument_list|)
return|;
block|}
comment|/**    * Returns an attribute-value map derived from the factory attributes    * by finding all factory attributes that begin with     *<i>contextName</i>.<i>tableName</i>.  The returned map consists of    * those attributes with the contextName and tableName stripped off.    */
DECL|method|getAttributeTable (String tableName)
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getAttributeTable
parameter_list|(
name|String
name|tableName
parameter_list|)
block|{
name|String
name|prefix
init|=
name|contextName
operator|+
literal|"."
operator|+
name|tableName
operator|+
literal|"."
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|result
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|attributeName
range|:
name|factory
operator|.
name|getAttributeNames
argument_list|()
control|)
block|{
if|if
condition|(
name|attributeName
operator|.
name|startsWith
argument_list|(
name|prefix
argument_list|)
condition|)
block|{
name|String
name|name
init|=
name|attributeName
operator|.
name|substring
argument_list|(
name|prefix
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|value
init|=
operator|(
name|String
operator|)
name|factory
operator|.
name|getAttribute
argument_list|(
name|attributeName
argument_list|)
decl_stmt|;
name|result
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
comment|/**    * Returns the context name.    */
DECL|method|getContextName ()
specifier|public
name|String
name|getContextName
parameter_list|()
block|{
return|return
name|contextName
return|;
block|}
comment|/**    * Returns the factory by which this context was created.    */
DECL|method|getContextFactory ()
specifier|public
name|ContextFactory
name|getContextFactory
parameter_list|()
block|{
return|return
name|factory
return|;
block|}
comment|/**    * Starts or restarts monitoring, the emitting of metrics records.    */
DECL|method|startMonitoring ()
specifier|public
specifier|synchronized
name|void
name|startMonitoring
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|isMonitoring
condition|)
block|{
name|startTimer
argument_list|()
expr_stmt|;
name|isMonitoring
operator|=
literal|true
expr_stmt|;
block|}
block|}
comment|/**    * Stops monitoring.  This does not free buffered data.     * @see #close()    */
DECL|method|stopMonitoring ()
specifier|public
specifier|synchronized
name|void
name|stopMonitoring
parameter_list|()
block|{
if|if
condition|(
name|isMonitoring
condition|)
block|{
name|stopTimer
argument_list|()
expr_stmt|;
name|isMonitoring
operator|=
literal|false
expr_stmt|;
block|}
block|}
comment|/**    * Returns true if monitoring is currently in progress.    */
DECL|method|isMonitoring ()
specifier|public
name|boolean
name|isMonitoring
parameter_list|()
block|{
return|return
name|isMonitoring
return|;
block|}
comment|/**    * Stops monitoring and frees buffered data, returning this    * object to its initial state.      */
DECL|method|close ()
specifier|public
specifier|synchronized
name|void
name|close
parameter_list|()
block|{
name|stopMonitoring
argument_list|()
expr_stmt|;
name|clearUpdaters
argument_list|()
expr_stmt|;
block|}
comment|/**    * Creates a new AbstractMetricsRecord instance with the given<code>recordName</code>.    * Throws an exception if the metrics implementation is configured with a fixed    * set of record names and<code>recordName</code> is not in that set.    *     * @param recordName the name of the record    * @throws MetricsException if recordName conflicts with configuration data    */
DECL|method|createRecord (String recordName)
specifier|public
specifier|final
specifier|synchronized
name|MetricsRecord
name|createRecord
parameter_list|(
name|String
name|recordName
parameter_list|)
block|{
if|if
condition|(
name|bufferedData
operator|.
name|get
argument_list|(
name|recordName
argument_list|)
operator|==
literal|null
condition|)
block|{
name|bufferedData
operator|.
name|put
argument_list|(
name|recordName
argument_list|,
operator|new
name|RecordMap
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|newRecord
argument_list|(
name|recordName
argument_list|)
return|;
block|}
comment|/**    * Subclasses should override this if they subclass MetricsRecordImpl.    * @param recordName the name of the record    * @return newly created instance of MetricsRecordImpl or subclass    */
DECL|method|newRecord (String recordName)
specifier|protected
name|MetricsRecord
name|newRecord
parameter_list|(
name|String
name|recordName
parameter_list|)
block|{
return|return
operator|new
name|MetricsRecordImpl
argument_list|(
name|recordName
argument_list|,
name|this
argument_list|)
return|;
block|}
comment|/**    * Registers a callback to be called at time intervals determined by    * the configuration.    *    * @param updater object to be run periodically; it should update    * some metrics records     */
DECL|method|registerUpdater (final Updater updater)
specifier|public
specifier|synchronized
name|void
name|registerUpdater
parameter_list|(
specifier|final
name|Updater
name|updater
parameter_list|)
block|{
if|if
condition|(
operator|!
name|updaters
operator|.
name|contains
argument_list|(
name|updater
argument_list|)
condition|)
block|{
name|updaters
operator|.
name|add
argument_list|(
name|updater
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Removes a callback, if it exists.    *    * @param updater object to be removed from the callback list    */
DECL|method|unregisterUpdater (Updater updater)
specifier|public
specifier|synchronized
name|void
name|unregisterUpdater
parameter_list|(
name|Updater
name|updater
parameter_list|)
block|{
name|updaters
operator|.
name|remove
argument_list|(
name|updater
argument_list|)
expr_stmt|;
block|}
DECL|method|clearUpdaters ()
specifier|private
specifier|synchronized
name|void
name|clearUpdaters
parameter_list|()
block|{
name|updaters
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
comment|/**    * Starts timer if it is not already started    */
DECL|method|startTimer ()
specifier|private
specifier|synchronized
name|void
name|startTimer
parameter_list|()
block|{
if|if
condition|(
name|timer
operator|==
literal|null
condition|)
block|{
name|timer
operator|=
operator|new
name|Timer
argument_list|(
literal|"Timer thread for monitoring "
operator|+
name|getContextName
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|TimerTask
name|task
init|=
operator|new
name|TimerTask
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|timerEvent
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|ioe
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|long
name|millis
init|=
name|period
operator|*
literal|1000
decl_stmt|;
name|timer
operator|.
name|scheduleAtFixedRate
argument_list|(
name|task
argument_list|,
name|millis
argument_list|,
name|millis
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Stops timer if it is running    */
DECL|method|stopTimer ()
specifier|private
specifier|synchronized
name|void
name|stopTimer
parameter_list|()
block|{
if|if
condition|(
name|timer
operator|!=
literal|null
condition|)
block|{
name|timer
operator|.
name|cancel
argument_list|()
expr_stmt|;
name|timer
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|/**    * Timer callback.    */
DECL|method|timerEvent ()
specifier|private
name|void
name|timerEvent
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|isMonitoring
condition|)
block|{
name|Collection
argument_list|<
name|Updater
argument_list|>
name|myUpdaters
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|myUpdaters
operator|=
operator|new
name|ArrayList
argument_list|<
name|Updater
argument_list|>
argument_list|(
name|updaters
argument_list|)
expr_stmt|;
block|}
comment|// Run all the registered updates without holding a lock
comment|// on this context
for|for
control|(
name|Updater
name|updater
range|:
name|myUpdaters
control|)
block|{
try|try
block|{
name|updater
operator|.
name|doUpdates
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|throwable
parameter_list|)
block|{
name|throwable
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
name|emitRecords
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    *  Emits the records.    */
DECL|method|emitRecords ()
specifier|private
specifier|synchronized
name|void
name|emitRecords
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|String
name|recordName
range|:
name|bufferedData
operator|.
name|keySet
argument_list|()
control|)
block|{
name|RecordMap
name|recordMap
init|=
name|bufferedData
operator|.
name|get
argument_list|(
name|recordName
argument_list|)
decl_stmt|;
synchronized|synchronized
init|(
name|recordMap
init|)
block|{
name|Set
argument_list|<
name|Entry
argument_list|<
name|TagMap
argument_list|,
name|MetricMap
argument_list|>
argument_list|>
name|entrySet
init|=
name|recordMap
operator|.
name|entrySet
argument_list|()
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|TagMap
argument_list|,
name|MetricMap
argument_list|>
name|entry
range|:
name|entrySet
control|)
block|{
name|OutputRecord
name|outRec
init|=
operator|new
name|OutputRecord
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
decl_stmt|;
name|emitRecord
argument_list|(
name|contextName
argument_list|,
name|recordName
argument_list|,
name|outRec
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|flush
argument_list|()
expr_stmt|;
block|}
comment|/**    * Retrieves all the records managed by this MetricsContext.    * Useful for monitoring systems that are polling-based.    * @return A non-null collection of all monitoring records.    */
DECL|method|getAllRecords ()
specifier|public
specifier|synchronized
name|Map
argument_list|<
name|String
argument_list|,
name|Collection
argument_list|<
name|OutputRecord
argument_list|>
argument_list|>
name|getAllRecords
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Collection
argument_list|<
name|OutputRecord
argument_list|>
argument_list|>
name|out
init|=
operator|new
name|TreeMap
argument_list|<
name|String
argument_list|,
name|Collection
argument_list|<
name|OutputRecord
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|recordName
range|:
name|bufferedData
operator|.
name|keySet
argument_list|()
control|)
block|{
name|RecordMap
name|recordMap
init|=
name|bufferedData
operator|.
name|get
argument_list|(
name|recordName
argument_list|)
decl_stmt|;
synchronized|synchronized
init|(
name|recordMap
init|)
block|{
name|List
argument_list|<
name|OutputRecord
argument_list|>
name|records
init|=
operator|new
name|ArrayList
argument_list|<
name|OutputRecord
argument_list|>
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|Entry
argument_list|<
name|TagMap
argument_list|,
name|MetricMap
argument_list|>
argument_list|>
name|entrySet
init|=
name|recordMap
operator|.
name|entrySet
argument_list|()
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|TagMap
argument_list|,
name|MetricMap
argument_list|>
name|entry
range|:
name|entrySet
control|)
block|{
name|OutputRecord
name|outRec
init|=
operator|new
name|OutputRecord
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
decl_stmt|;
name|records
operator|.
name|add
argument_list|(
name|outRec
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|put
argument_list|(
name|recordName
argument_list|,
name|records
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|out
return|;
block|}
comment|/**    * Sends a record to the metrics system.    */
DECL|method|emitRecord (String contextName, String recordName, OutputRecord outRec)
specifier|protected
specifier|abstract
name|void
name|emitRecord
parameter_list|(
name|String
name|contextName
parameter_list|,
name|String
name|recordName
parameter_list|,
name|OutputRecord
name|outRec
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Called each period after all records have been emitted, this method does nothing.    * Subclasses may override it in order to perform some kind of flush.    */
DECL|method|flush ()
specifier|protected
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
block|{   }
comment|/**    * Called by MetricsRecordImpl.update().  Creates or updates a row in    * the internal table of metric data.    */
DECL|method|update (MetricsRecordImpl record)
specifier|protected
name|void
name|update
parameter_list|(
name|MetricsRecordImpl
name|record
parameter_list|)
block|{
name|String
name|recordName
init|=
name|record
operator|.
name|getRecordName
argument_list|()
decl_stmt|;
name|TagMap
name|tagTable
init|=
name|record
operator|.
name|getTagTable
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|MetricValue
argument_list|>
name|metricUpdates
init|=
name|record
operator|.
name|getMetricTable
argument_list|()
decl_stmt|;
name|RecordMap
name|recordMap
init|=
name|getRecordMap
argument_list|(
name|recordName
argument_list|)
decl_stmt|;
synchronized|synchronized
init|(
name|recordMap
init|)
block|{
name|MetricMap
name|metricMap
init|=
name|recordMap
operator|.
name|get
argument_list|(
name|tagTable
argument_list|)
decl_stmt|;
if|if
condition|(
name|metricMap
operator|==
literal|null
condition|)
block|{
name|metricMap
operator|=
operator|new
name|MetricMap
argument_list|()
expr_stmt|;
name|TagMap
name|tagMap
init|=
operator|new
name|TagMap
argument_list|(
name|tagTable
argument_list|)
decl_stmt|;
comment|// clone tags
name|recordMap
operator|.
name|put
argument_list|(
name|tagMap
argument_list|,
name|metricMap
argument_list|)
expr_stmt|;
block|}
name|Set
argument_list|<
name|Entry
argument_list|<
name|String
argument_list|,
name|MetricValue
argument_list|>
argument_list|>
name|entrySet
init|=
name|metricUpdates
operator|.
name|entrySet
argument_list|()
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|MetricValue
argument_list|>
name|entry
range|:
name|entrySet
control|)
block|{
name|String
name|metricName
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|MetricValue
name|updateValue
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|Number
name|updateNumber
init|=
name|updateValue
operator|.
name|getNumber
argument_list|()
decl_stmt|;
name|Number
name|currentNumber
init|=
name|metricMap
operator|.
name|get
argument_list|(
name|metricName
argument_list|)
decl_stmt|;
if|if
condition|(
name|currentNumber
operator|==
literal|null
operator|||
name|updateValue
operator|.
name|isAbsolute
argument_list|()
condition|)
block|{
name|metricMap
operator|.
name|put
argument_list|(
name|metricName
argument_list|,
name|updateNumber
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Number
name|newNumber
init|=
name|sum
argument_list|(
name|updateNumber
argument_list|,
name|currentNumber
argument_list|)
decl_stmt|;
name|metricMap
operator|.
name|put
argument_list|(
name|metricName
argument_list|,
name|newNumber
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|getRecordMap (String recordName)
specifier|private
specifier|synchronized
name|RecordMap
name|getRecordMap
parameter_list|(
name|String
name|recordName
parameter_list|)
block|{
return|return
name|bufferedData
operator|.
name|get
argument_list|(
name|recordName
argument_list|)
return|;
block|}
comment|/**    * Adds two numbers, coercing the second to the type of the first.    *    */
DECL|method|sum (Number a, Number b)
specifier|private
name|Number
name|sum
parameter_list|(
name|Number
name|a
parameter_list|,
name|Number
name|b
parameter_list|)
block|{
if|if
condition|(
name|a
operator|instanceof
name|Integer
condition|)
block|{
return|return
name|Integer
operator|.
name|valueOf
argument_list|(
name|a
operator|.
name|intValue
argument_list|()
operator|+
name|b
operator|.
name|intValue
argument_list|()
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|a
operator|instanceof
name|Float
condition|)
block|{
return|return
operator|new
name|Float
argument_list|(
name|a
operator|.
name|floatValue
argument_list|()
operator|+
name|b
operator|.
name|floatValue
argument_list|()
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|a
operator|instanceof
name|Short
condition|)
block|{
return|return
name|Short
operator|.
name|valueOf
argument_list|(
call|(
name|short
call|)
argument_list|(
name|a
operator|.
name|shortValue
argument_list|()
operator|+
name|b
operator|.
name|shortValue
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|a
operator|instanceof
name|Byte
condition|)
block|{
return|return
name|Byte
operator|.
name|valueOf
argument_list|(
call|(
name|byte
call|)
argument_list|(
name|a
operator|.
name|byteValue
argument_list|()
operator|+
name|b
operator|.
name|byteValue
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|a
operator|instanceof
name|Long
condition|)
block|{
return|return
name|Long
operator|.
name|valueOf
argument_list|(
operator|(
name|a
operator|.
name|longValue
argument_list|()
operator|+
name|b
operator|.
name|longValue
argument_list|()
operator|)
argument_list|)
return|;
block|}
else|else
block|{
comment|// should never happen
throw|throw
operator|new
name|MetricsException
argument_list|(
literal|"Invalid number type"
argument_list|)
throw|;
block|}
block|}
comment|/**    * Called by MetricsRecordImpl.remove().  Removes all matching rows in    * the internal table of metric data.  A row matches if it has the same    * tag names and values as record, but it may also have additional    * tags.    */
DECL|method|remove (MetricsRecordImpl record)
specifier|protected
name|void
name|remove
parameter_list|(
name|MetricsRecordImpl
name|record
parameter_list|)
block|{
name|String
name|recordName
init|=
name|record
operator|.
name|getRecordName
argument_list|()
decl_stmt|;
name|TagMap
name|tagTable
init|=
name|record
operator|.
name|getTagTable
argument_list|()
decl_stmt|;
name|RecordMap
name|recordMap
init|=
name|getRecordMap
argument_list|(
name|recordName
argument_list|)
decl_stmt|;
synchronized|synchronized
init|(
name|recordMap
init|)
block|{
name|Iterator
argument_list|<
name|TagMap
argument_list|>
name|it
init|=
name|recordMap
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|TagMap
name|rowTags
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|rowTags
operator|.
name|containsAll
argument_list|(
name|tagTable
argument_list|)
condition|)
block|{
name|it
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/**    * Returns the timer period.    */
DECL|method|getPeriod ()
specifier|public
name|int
name|getPeriod
parameter_list|()
block|{
return|return
name|period
return|;
block|}
comment|/**    * Sets the timer period    */
DECL|method|setPeriod (int period)
specifier|protected
name|void
name|setPeriod
parameter_list|(
name|int
name|period
parameter_list|)
block|{
name|this
operator|.
name|period
operator|=
name|period
expr_stmt|;
block|}
comment|/**    * If a period is set in the attribute passed in, override    * the default with it.    */
DECL|method|parseAndSetPeriod (String attributeName)
specifier|protected
name|void
name|parseAndSetPeriod
parameter_list|(
name|String
name|attributeName
parameter_list|)
block|{
name|String
name|periodStr
init|=
name|getAttribute
argument_list|(
name|attributeName
argument_list|)
decl_stmt|;
if|if
condition|(
name|periodStr
operator|!=
literal|null
condition|)
block|{
name|int
name|period
init|=
literal|0
decl_stmt|;
try|try
block|{
name|period
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|periodStr
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|nfe
parameter_list|)
block|{       }
if|if
condition|(
name|period
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|MetricsException
argument_list|(
literal|"Invalid period: "
operator|+
name|periodStr
argument_list|)
throw|;
block|}
name|setPeriod
argument_list|(
name|period
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

