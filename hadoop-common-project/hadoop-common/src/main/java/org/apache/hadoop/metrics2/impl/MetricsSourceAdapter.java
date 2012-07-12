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
name|HashMap
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|Attribute
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|AttributeList
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|AttributeNotFoundException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|DynamicMBean
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|InvalidAttributeValueException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|MBeanException
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

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|ObjectName
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|ReflectionException
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
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
name|AbstractMetric
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
name|MetricsConfig
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
name|util
operator|.
name|MBeans
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

begin_comment
comment|/**  * An adapter class for metrics source and associated filter and jmx impl  */
end_comment

begin_class
DECL|class|MetricsSourceAdapter
class|class
name|MetricsSourceAdapter
implements|implements
name|DynamicMBean
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|MetricsSourceAdapter
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|prefix
DECL|field|name
specifier|private
specifier|final
name|String
name|prefix
decl_stmt|,
name|name
decl_stmt|;
DECL|field|source
specifier|private
specifier|final
name|MetricsSource
name|source
decl_stmt|;
DECL|field|recordFilter
DECL|field|metricFilter
specifier|private
specifier|final
name|MetricsFilter
name|recordFilter
decl_stmt|,
name|metricFilter
decl_stmt|;
DECL|field|attrCache
specifier|private
specifier|final
name|HashMap
argument_list|<
name|String
argument_list|,
name|Attribute
argument_list|>
name|attrCache
decl_stmt|;
DECL|field|infoBuilder
specifier|private
specifier|final
name|MBeanInfoBuilder
name|infoBuilder
decl_stmt|;
DECL|field|injectedTags
specifier|private
specifier|final
name|Iterable
argument_list|<
name|MetricsTag
argument_list|>
name|injectedTags
decl_stmt|;
DECL|field|lastRecs
specifier|private
name|Iterable
argument_list|<
name|MetricsRecordImpl
argument_list|>
name|lastRecs
decl_stmt|;
DECL|field|jmxCacheTS
specifier|private
name|long
name|jmxCacheTS
init|=
literal|0
decl_stmt|;
DECL|field|jmxCacheTTL
specifier|private
name|int
name|jmxCacheTTL
decl_stmt|;
DECL|field|infoCache
specifier|private
name|MBeanInfo
name|infoCache
decl_stmt|;
DECL|field|mbeanName
specifier|private
name|ObjectName
name|mbeanName
decl_stmt|;
DECL|field|startMBeans
specifier|private
specifier|final
name|boolean
name|startMBeans
decl_stmt|;
DECL|method|MetricsSourceAdapter (String prefix, String name, String description, MetricsSource source, Iterable<MetricsTag> injectedTags, MetricsFilter recordFilter, MetricsFilter metricFilter, int jmxCacheTTL, boolean startMBeans)
name|MetricsSourceAdapter
parameter_list|(
name|String
name|prefix
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|description
parameter_list|,
name|MetricsSource
name|source
parameter_list|,
name|Iterable
argument_list|<
name|MetricsTag
argument_list|>
name|injectedTags
parameter_list|,
name|MetricsFilter
name|recordFilter
parameter_list|,
name|MetricsFilter
name|metricFilter
parameter_list|,
name|int
name|jmxCacheTTL
parameter_list|,
name|boolean
name|startMBeans
parameter_list|)
block|{
name|this
operator|.
name|prefix
operator|=
name|checkNotNull
argument_list|(
name|prefix
argument_list|,
literal|"prefix"
argument_list|)
expr_stmt|;
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
name|source
operator|=
name|checkNotNull
argument_list|(
name|source
argument_list|,
literal|"source"
argument_list|)
expr_stmt|;
name|attrCache
operator|=
name|Maps
operator|.
name|newHashMap
argument_list|()
expr_stmt|;
name|infoBuilder
operator|=
operator|new
name|MBeanInfoBuilder
argument_list|(
name|name
argument_list|,
name|description
argument_list|)
expr_stmt|;
name|this
operator|.
name|injectedTags
operator|=
name|injectedTags
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
name|jmxCacheTTL
operator|=
name|checkArg
argument_list|(
name|jmxCacheTTL
argument_list|,
name|jmxCacheTTL
operator|>
literal|0
argument_list|,
literal|"jmxCacheTTL"
argument_list|)
expr_stmt|;
name|this
operator|.
name|startMBeans
operator|=
name|startMBeans
expr_stmt|;
block|}
DECL|method|MetricsSourceAdapter (String prefix, String name, String description, MetricsSource source, Iterable<MetricsTag> injectedTags, int period, MetricsConfig conf)
name|MetricsSourceAdapter
parameter_list|(
name|String
name|prefix
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|description
parameter_list|,
name|MetricsSource
name|source
parameter_list|,
name|Iterable
argument_list|<
name|MetricsTag
argument_list|>
name|injectedTags
parameter_list|,
name|int
name|period
parameter_list|,
name|MetricsConfig
name|conf
parameter_list|)
block|{
name|this
argument_list|(
name|prefix
argument_list|,
name|name
argument_list|,
name|description
argument_list|,
name|source
argument_list|,
name|injectedTags
argument_list|,
name|conf
operator|.
name|getFilter
argument_list|(
name|RECORD_FILTER_KEY
argument_list|)
argument_list|,
name|conf
operator|.
name|getFilter
argument_list|(
name|METRIC_FILTER_KEY
argument_list|)
argument_list|,
name|period
operator|+
literal|1
argument_list|,
comment|// hack to avoid most of the "innocuous" races.
name|conf
operator|.
name|getBoolean
argument_list|(
name|START_MBEANS_KEY
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|start ()
name|void
name|start
parameter_list|()
block|{
if|if
condition|(
name|startMBeans
condition|)
name|startMBeans
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getAttribute (String attribute)
specifier|public
name|Object
name|getAttribute
parameter_list|(
name|String
name|attribute
parameter_list|)
throws|throws
name|AttributeNotFoundException
throws|,
name|MBeanException
throws|,
name|ReflectionException
block|{
name|updateJmxCache
argument_list|()
expr_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|Attribute
name|a
init|=
name|attrCache
operator|.
name|get
argument_list|(
name|attribute
argument_list|)
decl_stmt|;
if|if
condition|(
name|a
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|AttributeNotFoundException
argument_list|(
name|attribute
operator|+
literal|" not found"
argument_list|)
throw|;
block|}
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
name|attribute
operator|+
literal|": "
operator|+
name|a
argument_list|)
expr_stmt|;
block|}
return|return
name|a
operator|.
name|getValue
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|setAttribute (Attribute attribute)
specifier|public
name|void
name|setAttribute
parameter_list|(
name|Attribute
name|attribute
parameter_list|)
throws|throws
name|AttributeNotFoundException
throws|,
name|InvalidAttributeValueException
throws|,
name|MBeanException
throws|,
name|ReflectionException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Metrics are read-only."
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|getAttributes (String[] attributes)
specifier|public
name|AttributeList
name|getAttributes
parameter_list|(
name|String
index|[]
name|attributes
parameter_list|)
block|{
name|updateJmxCache
argument_list|()
expr_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|AttributeList
name|ret
init|=
operator|new
name|AttributeList
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|key
range|:
name|attributes
control|)
block|{
name|Attribute
name|attr
init|=
name|attrCache
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
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
name|key
operator|+
literal|": "
operator|+
name|attr
argument_list|)
expr_stmt|;
block|}
name|ret
operator|.
name|add
argument_list|(
name|attr
argument_list|)
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|setAttributes (AttributeList attributes)
specifier|public
name|AttributeList
name|setAttributes
parameter_list|(
name|AttributeList
name|attributes
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Metrics are read-only."
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|invoke (String actionName, Object[] params, String[] signature)
specifier|public
name|Object
name|invoke
parameter_list|(
name|String
name|actionName
parameter_list|,
name|Object
index|[]
name|params
parameter_list|,
name|String
index|[]
name|signature
parameter_list|)
throws|throws
name|MBeanException
throws|,
name|ReflectionException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not supported yet."
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|getMBeanInfo ()
specifier|public
name|MBeanInfo
name|getMBeanInfo
parameter_list|()
block|{
name|updateJmxCache
argument_list|()
expr_stmt|;
return|return
name|infoCache
return|;
block|}
DECL|method|updateJmxCache ()
specifier|private
name|void
name|updateJmxCache
parameter_list|()
block|{
name|boolean
name|getAllMetrics
init|=
literal|false
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
name|Time
operator|.
name|now
argument_list|()
operator|-
name|jmxCacheTS
operator|>=
name|jmxCacheTTL
condition|)
block|{
comment|// temporarilly advance the expiry while updating the cache
name|jmxCacheTS
operator|=
name|Time
operator|.
name|now
argument_list|()
operator|+
name|jmxCacheTTL
expr_stmt|;
if|if
condition|(
name|lastRecs
operator|==
literal|null
condition|)
block|{
name|getAllMetrics
operator|=
literal|true
expr_stmt|;
block|}
block|}
else|else
block|{
return|return;
block|}
block|}
if|if
condition|(
name|getAllMetrics
condition|)
block|{
name|MetricsCollectorImpl
name|builder
init|=
operator|new
name|MetricsCollectorImpl
argument_list|()
decl_stmt|;
name|getMetrics
argument_list|(
name|builder
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
synchronized|synchronized
init|(
name|this
init|)
block|{
name|int
name|oldCacheSize
init|=
name|attrCache
operator|.
name|size
argument_list|()
decl_stmt|;
name|int
name|newCacheSize
init|=
name|updateAttrCache
argument_list|()
decl_stmt|;
if|if
condition|(
name|oldCacheSize
operator|<
name|newCacheSize
condition|)
block|{
name|updateInfoCache
argument_list|()
expr_stmt|;
block|}
name|jmxCacheTS
operator|=
name|Time
operator|.
name|now
argument_list|()
expr_stmt|;
name|lastRecs
operator|=
literal|null
expr_stmt|;
comment|// in case regular interval update is not running
block|}
block|}
DECL|method|getMetrics (MetricsCollectorImpl builder, boolean all)
name|Iterable
argument_list|<
name|MetricsRecordImpl
argument_list|>
name|getMetrics
parameter_list|(
name|MetricsCollectorImpl
name|builder
parameter_list|,
name|boolean
name|all
parameter_list|)
block|{
name|builder
operator|.
name|setRecordFilter
argument_list|(
name|recordFilter
argument_list|)
operator|.
name|setMetricFilter
argument_list|(
name|metricFilter
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
name|lastRecs
operator|==
literal|null
operator|&&
name|jmxCacheTS
operator|==
literal|0
condition|)
block|{
name|all
operator|=
literal|true
expr_stmt|;
comment|// Get all the metrics to populate the sink caches
block|}
block|}
try|try
block|{
name|source
operator|.
name|getMetrics
argument_list|(
name|builder
argument_list|,
name|all
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error getting metrics from source "
operator|+
name|name
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|MetricsRecordBuilderImpl
name|rb
range|:
name|builder
control|)
block|{
for|for
control|(
name|MetricsTag
name|t
range|:
name|injectedTags
control|)
block|{
name|rb
operator|.
name|add
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
block|}
synchronized|synchronized
init|(
name|this
init|)
block|{
name|lastRecs
operator|=
name|builder
operator|.
name|getRecords
argument_list|()
expr_stmt|;
return|return
name|lastRecs
return|;
block|}
block|}
DECL|method|stop ()
specifier|synchronized
name|void
name|stop
parameter_list|()
block|{
name|stopMBeans
argument_list|()
expr_stmt|;
block|}
DECL|method|startMBeans ()
specifier|synchronized
name|void
name|startMBeans
parameter_list|()
block|{
if|if
condition|(
name|mbeanName
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"MBean "
operator|+
name|name
operator|+
literal|" already initialized!"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Stacktrace: "
argument_list|,
operator|new
name|Throwable
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
name|mbeanName
operator|=
name|MBeans
operator|.
name|register
argument_list|(
name|prefix
argument_list|,
name|name
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"MBean for source "
operator|+
name|name
operator|+
literal|" registered."
argument_list|)
expr_stmt|;
block|}
DECL|method|stopMBeans ()
specifier|synchronized
name|void
name|stopMBeans
parameter_list|()
block|{
if|if
condition|(
name|mbeanName
operator|!=
literal|null
condition|)
block|{
name|MBeans
operator|.
name|unregister
argument_list|(
name|mbeanName
argument_list|)
expr_stmt|;
name|mbeanName
operator|=
literal|null
expr_stmt|;
block|}
block|}
DECL|method|updateInfoCache ()
specifier|private
name|void
name|updateInfoCache
parameter_list|()
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Updating info cache..."
argument_list|)
expr_stmt|;
name|infoCache
operator|=
name|infoBuilder
operator|.
name|reset
argument_list|(
name|lastRecs
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Done"
argument_list|)
expr_stmt|;
block|}
DECL|method|updateAttrCache ()
specifier|private
name|int
name|updateAttrCache
parameter_list|()
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Updating attr cache..."
argument_list|)
expr_stmt|;
name|int
name|recNo
init|=
literal|0
decl_stmt|;
name|int
name|numMetrics
init|=
literal|0
decl_stmt|;
for|for
control|(
name|MetricsRecordImpl
name|record
range|:
name|lastRecs
control|)
block|{
for|for
control|(
name|MetricsTag
name|t
range|:
name|record
operator|.
name|tags
argument_list|()
control|)
block|{
name|setAttrCacheTag
argument_list|(
name|t
argument_list|,
name|recNo
argument_list|)
expr_stmt|;
operator|++
name|numMetrics
expr_stmt|;
block|}
for|for
control|(
name|AbstractMetric
name|m
range|:
name|record
operator|.
name|metrics
argument_list|()
control|)
block|{
name|setAttrCacheMetric
argument_list|(
name|m
argument_list|,
name|recNo
argument_list|)
expr_stmt|;
operator|++
name|numMetrics
expr_stmt|;
block|}
operator|++
name|recNo
expr_stmt|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"Done. # tags& metrics="
operator|+
name|numMetrics
argument_list|)
expr_stmt|;
return|return
name|numMetrics
return|;
block|}
DECL|method|tagName (String name, int recNo)
specifier|private
specifier|static
name|String
name|tagName
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|recNo
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
name|name
operator|.
name|length
argument_list|()
operator|+
literal|16
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"tag."
argument_list|)
operator|.
name|append
argument_list|(
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|recNo
operator|>
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|'.'
argument_list|)
operator|.
name|append
argument_list|(
name|recNo
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|setAttrCacheTag (MetricsTag tag, int recNo)
specifier|private
name|void
name|setAttrCacheTag
parameter_list|(
name|MetricsTag
name|tag
parameter_list|,
name|int
name|recNo
parameter_list|)
block|{
name|String
name|key
init|=
name|tagName
argument_list|(
name|tag
operator|.
name|name
argument_list|()
argument_list|,
name|recNo
argument_list|)
decl_stmt|;
name|attrCache
operator|.
name|put
argument_list|(
name|key
argument_list|,
operator|new
name|Attribute
argument_list|(
name|key
argument_list|,
name|tag
operator|.
name|value
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|metricName (String name, int recNo)
specifier|private
specifier|static
name|String
name|metricName
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|recNo
parameter_list|)
block|{
if|if
condition|(
name|recNo
operator|==
literal|0
condition|)
block|{
return|return
name|name
return|;
block|}
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
name|name
operator|.
name|length
argument_list|()
operator|+
literal|12
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|recNo
operator|>
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|'.'
argument_list|)
operator|.
name|append
argument_list|(
name|recNo
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|setAttrCacheMetric (AbstractMetric metric, int recNo)
specifier|private
name|void
name|setAttrCacheMetric
parameter_list|(
name|AbstractMetric
name|metric
parameter_list|,
name|int
name|recNo
parameter_list|)
block|{
name|String
name|key
init|=
name|metricName
argument_list|(
name|metric
operator|.
name|name
argument_list|()
argument_list|,
name|recNo
argument_list|)
decl_stmt|;
name|attrCache
operator|.
name|put
argument_list|(
name|key
argument_list|,
operator|new
name|Attribute
argument_list|(
name|key
argument_list|,
name|metric
operator|.
name|value
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
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
DECL|method|source ()
name|MetricsSource
name|source
parameter_list|()
block|{
return|return
name|source
return|;
block|}
block|}
end_class

end_unit

