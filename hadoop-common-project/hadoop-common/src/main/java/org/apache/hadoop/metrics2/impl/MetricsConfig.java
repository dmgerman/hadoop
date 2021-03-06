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
name|io
operator|.
name|ByteArrayOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URLClassLoader
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|security
operator|.
name|AccessController
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|PrivilegedAction
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
name|base
operator|.
name|Joiner
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
name|base
operator|.
name|Splitter
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
name|Iterables
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
name|configuration2
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
name|commons
operator|.
name|configuration2
operator|.
name|PropertiesConfiguration
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
name|configuration2
operator|.
name|SubsetConfiguration
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
name|configuration2
operator|.
name|ex
operator|.
name|ConfigurationException
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
name|configuration2
operator|.
name|io
operator|.
name|FileHandler
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
name|MetricsPlugin
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
name|filter
operator|.
name|GlobFilter
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
name|StringUtils
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
comment|/**  * Metrics configuration for MetricsSystemImpl  */
end_comment

begin_class
DECL|class|MetricsConfig
class|class
name|MetricsConfig
extends|extends
name|SubsetConfiguration
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
name|MetricsConfig
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|DEFAULT_FILE_NAME
specifier|static
specifier|final
name|String
name|DEFAULT_FILE_NAME
init|=
literal|"hadoop-metrics2.properties"
decl_stmt|;
DECL|field|PREFIX_DEFAULT
specifier|static
specifier|final
name|String
name|PREFIX_DEFAULT
init|=
literal|"*."
decl_stmt|;
DECL|field|PERIOD_KEY
specifier|static
specifier|final
name|String
name|PERIOD_KEY
init|=
literal|"period"
decl_stmt|;
DECL|field|PERIOD_DEFAULT
specifier|static
specifier|final
name|int
name|PERIOD_DEFAULT
init|=
literal|10
decl_stmt|;
comment|// seconds
comment|// For testing, this will have the priority.
DECL|field|PERIOD_MILLIS_KEY
specifier|static
specifier|final
name|String
name|PERIOD_MILLIS_KEY
init|=
literal|"periodMillis"
decl_stmt|;
DECL|field|QUEUE_CAPACITY_KEY
specifier|static
specifier|final
name|String
name|QUEUE_CAPACITY_KEY
init|=
literal|"queue.capacity"
decl_stmt|;
DECL|field|QUEUE_CAPACITY_DEFAULT
specifier|static
specifier|final
name|int
name|QUEUE_CAPACITY_DEFAULT
init|=
literal|1
decl_stmt|;
DECL|field|RETRY_DELAY_KEY
specifier|static
specifier|final
name|String
name|RETRY_DELAY_KEY
init|=
literal|"retry.delay"
decl_stmt|;
DECL|field|RETRY_DELAY_DEFAULT
specifier|static
specifier|final
name|int
name|RETRY_DELAY_DEFAULT
init|=
literal|10
decl_stmt|;
comment|// seconds
DECL|field|RETRY_BACKOFF_KEY
specifier|static
specifier|final
name|String
name|RETRY_BACKOFF_KEY
init|=
literal|"retry.backoff"
decl_stmt|;
DECL|field|RETRY_BACKOFF_DEFAULT
specifier|static
specifier|final
name|int
name|RETRY_BACKOFF_DEFAULT
init|=
literal|2
decl_stmt|;
comment|// back off factor
DECL|field|RETRY_COUNT_KEY
specifier|static
specifier|final
name|String
name|RETRY_COUNT_KEY
init|=
literal|"retry.count"
decl_stmt|;
DECL|field|RETRY_COUNT_DEFAULT
specifier|static
specifier|final
name|int
name|RETRY_COUNT_DEFAULT
init|=
literal|1
decl_stmt|;
DECL|field|JMX_CACHE_TTL_KEY
specifier|static
specifier|final
name|String
name|JMX_CACHE_TTL_KEY
init|=
literal|"jmx.cache.ttl"
decl_stmt|;
DECL|field|START_MBEANS_KEY
specifier|static
specifier|final
name|String
name|START_MBEANS_KEY
init|=
literal|"source.start_mbeans"
decl_stmt|;
DECL|field|PLUGIN_URLS_KEY
specifier|static
specifier|final
name|String
name|PLUGIN_URLS_KEY
init|=
literal|"plugin.urls"
decl_stmt|;
DECL|field|CONTEXT_KEY
specifier|static
specifier|final
name|String
name|CONTEXT_KEY
init|=
literal|"context"
decl_stmt|;
DECL|field|NAME_KEY
specifier|static
specifier|final
name|String
name|NAME_KEY
init|=
literal|"name"
decl_stmt|;
DECL|field|DESC_KEY
specifier|static
specifier|final
name|String
name|DESC_KEY
init|=
literal|"description"
decl_stmt|;
DECL|field|SOURCE_KEY
specifier|static
specifier|final
name|String
name|SOURCE_KEY
init|=
literal|"source"
decl_stmt|;
DECL|field|SINK_KEY
specifier|static
specifier|final
name|String
name|SINK_KEY
init|=
literal|"sink"
decl_stmt|;
DECL|field|METRIC_FILTER_KEY
specifier|static
specifier|final
name|String
name|METRIC_FILTER_KEY
init|=
literal|"metric.filter"
decl_stmt|;
DECL|field|RECORD_FILTER_KEY
specifier|static
specifier|final
name|String
name|RECORD_FILTER_KEY
init|=
literal|"record.filter"
decl_stmt|;
DECL|field|SOURCE_FILTER_KEY
specifier|static
specifier|final
name|String
name|SOURCE_FILTER_KEY
init|=
literal|"source.filter"
decl_stmt|;
DECL|field|INSTANCE_REGEX
specifier|static
specifier|final
name|Pattern
name|INSTANCE_REGEX
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"([^.*]+)\\..+"
argument_list|)
decl_stmt|;
DECL|field|SPLITTER
specifier|static
specifier|final
name|Splitter
name|SPLITTER
init|=
name|Splitter
operator|.
name|on
argument_list|(
literal|','
argument_list|)
operator|.
name|trimResults
argument_list|()
decl_stmt|;
DECL|field|pluginLoader
specifier|private
name|ClassLoader
name|pluginLoader
decl_stmt|;
DECL|method|MetricsConfig (Configuration c, String prefix)
name|MetricsConfig
parameter_list|(
name|Configuration
name|c
parameter_list|,
name|String
name|prefix
parameter_list|)
block|{
name|super
argument_list|(
name|c
argument_list|,
name|StringUtils
operator|.
name|toLowerCase
argument_list|(
name|prefix
argument_list|)
argument_list|,
literal|"."
argument_list|)
expr_stmt|;
block|}
DECL|method|create (String prefix)
specifier|static
name|MetricsConfig
name|create
parameter_list|(
name|String
name|prefix
parameter_list|)
block|{
return|return
name|loadFirst
argument_list|(
name|prefix
argument_list|,
literal|"hadoop-metrics2-"
operator|+
name|StringUtils
operator|.
name|toLowerCase
argument_list|(
name|prefix
argument_list|)
operator|+
literal|".properties"
argument_list|,
name|DEFAULT_FILE_NAME
argument_list|)
return|;
block|}
DECL|method|create (String prefix, String... fileNames)
specifier|static
name|MetricsConfig
name|create
parameter_list|(
name|String
name|prefix
parameter_list|,
name|String
modifier|...
name|fileNames
parameter_list|)
block|{
return|return
name|loadFirst
argument_list|(
name|prefix
argument_list|,
name|fileNames
argument_list|)
return|;
block|}
comment|/**    * Load configuration from a list of files until the first successful load    * @param conf  the configuration object    * @param files the list of filenames to try    * @return  the configuration object    */
DECL|method|loadFirst (String prefix, String... fileNames)
specifier|static
name|MetricsConfig
name|loadFirst
parameter_list|(
name|String
name|prefix
parameter_list|,
name|String
modifier|...
name|fileNames
parameter_list|)
block|{
for|for
control|(
name|String
name|fname
range|:
name|fileNames
control|)
block|{
try|try
block|{
name|PropertiesConfiguration
name|pcf
init|=
operator|new
name|PropertiesConfiguration
argument_list|()
decl_stmt|;
name|FileHandler
name|fh
init|=
operator|new
name|FileHandler
argument_list|(
name|pcf
argument_list|)
decl_stmt|;
name|fh
operator|.
name|setFileName
argument_list|(
name|fname
argument_list|)
expr_stmt|;
name|fh
operator|.
name|load
argument_list|()
expr_stmt|;
name|Configuration
name|cf
init|=
name|pcf
operator|.
name|interpolatedConfiguration
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Loaded properties from {}"
argument_list|,
name|fname
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
literal|"Properties: {}"
argument_list|,
name|toString
argument_list|(
name|cf
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|MetricsConfig
name|mc
init|=
operator|new
name|MetricsConfig
argument_list|(
name|cf
argument_list|,
name|prefix
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Metrics Config: {}"
argument_list|,
name|mc
argument_list|)
expr_stmt|;
return|return
name|mc
return|;
block|}
catch|catch
parameter_list|(
name|ConfigurationException
name|e
parameter_list|)
block|{
comment|// Commons Configuration defines the message text when file not found
if|if
condition|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"Could not locate"
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Could not locate file {}"
argument_list|,
name|fname
argument_list|,
name|e
argument_list|)
expr_stmt|;
continue|continue;
block|}
throw|throw
operator|new
name|MetricsConfigException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
name|LOG
operator|.
name|warn
argument_list|(
literal|"Cannot locate configuration: tried "
operator|+
name|Joiner
operator|.
name|on
argument_list|(
literal|","
argument_list|)
operator|.
name|join
argument_list|(
name|fileNames
argument_list|)
argument_list|)
expr_stmt|;
comment|// default to an empty configuration
return|return
operator|new
name|MetricsConfig
argument_list|(
operator|new
name|PropertiesConfiguration
argument_list|()
argument_list|,
name|prefix
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|subset (String prefix)
specifier|public
name|MetricsConfig
name|subset
parameter_list|(
name|String
name|prefix
parameter_list|)
block|{
return|return
operator|new
name|MetricsConfig
argument_list|(
name|this
argument_list|,
name|prefix
argument_list|)
return|;
block|}
comment|/**    * Return sub configs for instance specified in the config.    * Assuming format specified as follows:<pre>    * [type].[instance].[option] = [value]</pre>    * Note, '*' is a special default instance, which is excluded in the result.    * @param type  of the instance    * @return  a map with [instance] as key and config object as value    */
DECL|method|getInstanceConfigs (String type)
name|Map
argument_list|<
name|String
argument_list|,
name|MetricsConfig
argument_list|>
name|getInstanceConfigs
parameter_list|(
name|String
name|type
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|MetricsConfig
argument_list|>
name|map
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
name|MetricsConfig
name|sub
init|=
name|subset
argument_list|(
name|type
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|key
range|:
name|sub
operator|.
name|keys
argument_list|()
control|)
block|{
name|Matcher
name|matcher
init|=
name|INSTANCE_REGEX
operator|.
name|matcher
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|matcher
operator|.
name|matches
argument_list|()
condition|)
block|{
name|String
name|instance
init|=
name|matcher
operator|.
name|group
argument_list|(
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|map
operator|.
name|containsKey
argument_list|(
name|instance
argument_list|)
condition|)
block|{
name|map
operator|.
name|put
argument_list|(
name|instance
argument_list|,
name|sub
operator|.
name|subset
argument_list|(
name|instance
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|map
return|;
block|}
DECL|method|keys ()
name|Iterable
argument_list|<
name|String
argument_list|>
name|keys
parameter_list|()
block|{
return|return
operator|new
name|Iterable
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|String
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|(
name|Iterator
argument_list|<
name|String
argument_list|>
operator|)
name|getKeys
argument_list|()
return|;
block|}
block|}
return|;
block|}
comment|/**    * Will poke parents for defaults    * @param key to lookup    * @return  the value or null    */
annotation|@
name|Override
DECL|method|getPropertyInternal (String key)
specifier|public
name|Object
name|getPropertyInternal
parameter_list|(
name|String
name|key
parameter_list|)
block|{
name|Object
name|value
init|=
name|super
operator|.
name|getPropertyInternal
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
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
literal|"poking parent '"
operator|+
name|getParent
argument_list|()
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"' for key: "
operator|+
name|key
argument_list|)
expr_stmt|;
block|}
return|return
name|getParent
argument_list|()
operator|.
name|getProperty
argument_list|(
name|key
operator|.
name|startsWith
argument_list|(
name|PREFIX_DEFAULT
argument_list|)
condition|?
name|key
else|:
name|PREFIX_DEFAULT
operator|+
name|key
argument_list|)
return|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"Returning '{}' for key: {}"
argument_list|,
name|value
argument_list|,
name|key
argument_list|)
expr_stmt|;
return|return
name|value
return|;
block|}
DECL|method|getPlugin (String name)
parameter_list|<
name|T
extends|extends
name|MetricsPlugin
parameter_list|>
name|T
name|getPlugin
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|String
name|clsName
init|=
name|getClassName
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|clsName
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
try|try
block|{
name|Class
argument_list|<
name|?
argument_list|>
name|cls
init|=
name|Class
operator|.
name|forName
argument_list|(
name|clsName
argument_list|,
literal|true
argument_list|,
name|getPluginLoader
argument_list|()
argument_list|)
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|T
name|plugin
init|=
operator|(
name|T
operator|)
name|cls
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|plugin
operator|.
name|init
argument_list|(
name|name
operator|.
name|isEmpty
argument_list|()
condition|?
name|this
else|:
name|subset
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|plugin
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|MetricsConfigException
argument_list|(
literal|"Error creating plugin: "
operator|+
name|clsName
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|getClassName (String prefix)
name|String
name|getClassName
parameter_list|(
name|String
name|prefix
parameter_list|)
block|{
name|String
name|classKey
init|=
name|prefix
operator|.
name|isEmpty
argument_list|()
condition|?
literal|"class"
else|:
name|prefix
operator|.
name|concat
argument_list|(
literal|".class"
argument_list|)
decl_stmt|;
name|String
name|clsName
init|=
name|getString
argument_list|(
name|classKey
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Class name for prefix {} is {}"
argument_list|,
name|prefix
argument_list|,
name|clsName
argument_list|)
expr_stmt|;
if|if
condition|(
name|clsName
operator|==
literal|null
operator|||
name|clsName
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
name|clsName
return|;
block|}
DECL|method|getPluginLoader ()
name|ClassLoader
name|getPluginLoader
parameter_list|()
block|{
if|if
condition|(
name|pluginLoader
operator|!=
literal|null
condition|)
block|{
return|return
name|pluginLoader
return|;
block|}
specifier|final
name|ClassLoader
name|defaultLoader
init|=
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
decl_stmt|;
name|Object
name|purls
init|=
name|super
operator|.
name|getProperty
argument_list|(
name|PLUGIN_URLS_KEY
argument_list|)
decl_stmt|;
if|if
condition|(
name|purls
operator|==
literal|null
condition|)
block|{
return|return
name|defaultLoader
return|;
block|}
name|Iterable
argument_list|<
name|String
argument_list|>
name|jars
init|=
name|SPLITTER
operator|.
name|split
argument_list|(
operator|(
name|String
operator|)
name|purls
argument_list|)
decl_stmt|;
name|int
name|len
init|=
name|Iterables
operator|.
name|size
argument_list|(
name|jars
argument_list|)
decl_stmt|;
if|if
condition|(
name|len
operator|>
literal|0
condition|)
block|{
specifier|final
name|URL
index|[]
name|urls
init|=
operator|new
name|URL
index|[
name|len
index|]
decl_stmt|;
try|try
block|{
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|jar
range|:
name|jars
control|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Parsing URL for {}"
argument_list|,
name|jar
argument_list|)
expr_stmt|;
name|urls
index|[
name|i
operator|++
index|]
operator|=
operator|new
name|URL
argument_list|(
name|jar
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
throw|throw
operator|new
name|MetricsConfigException
argument_list|(
name|e
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
literal|"Using plugin jars: {}"
argument_list|,
name|Iterables
operator|.
name|toString
argument_list|(
name|jars
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|pluginLoader
operator|=
name|doPrivileged
argument_list|(
operator|new
name|PrivilegedAction
argument_list|<
name|ClassLoader
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|ClassLoader
name|run
parameter_list|()
block|{
return|return
operator|new
name|URLClassLoader
argument_list|(
name|urls
argument_list|,
name|defaultLoader
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
return|return
name|pluginLoader
return|;
block|}
if|if
condition|(
name|parent
operator|instanceof
name|MetricsConfig
condition|)
block|{
return|return
operator|(
operator|(
name|MetricsConfig
operator|)
name|parent
operator|)
operator|.
name|getPluginLoader
argument_list|()
return|;
block|}
return|return
name|defaultLoader
return|;
block|}
DECL|method|getFilter (String prefix)
name|MetricsFilter
name|getFilter
parameter_list|(
name|String
name|prefix
parameter_list|)
block|{
comment|// don't create filter instances without out options
name|MetricsConfig
name|conf
init|=
name|subset
argument_list|(
name|prefix
argument_list|)
decl_stmt|;
if|if
condition|(
name|conf
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|MetricsFilter
name|filter
init|=
name|getPlugin
argument_list|(
name|prefix
argument_list|)
decl_stmt|;
if|if
condition|(
name|filter
operator|!=
literal|null
condition|)
block|{
return|return
name|filter
return|;
block|}
comment|// glob filter is assumed if pattern is specified but class is not.
name|filter
operator|=
operator|new
name|GlobFilter
argument_list|()
expr_stmt|;
name|filter
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
return|return
name|filter
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|toString
argument_list|(
name|this
argument_list|)
return|;
block|}
DECL|method|toString (Configuration c)
specifier|static
name|String
name|toString
parameter_list|(
name|Configuration
name|c
parameter_list|)
block|{
name|ByteArrayOutputStream
name|buffer
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
try|try
block|{
name|PrintWriter
name|pw
init|=
operator|new
name|PrintWriter
argument_list|(
name|buffer
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|PropertiesConfiguration
name|tmp
init|=
operator|new
name|PropertiesConfiguration
argument_list|()
decl_stmt|;
name|tmp
operator|.
name|copy
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|tmp
operator|.
name|write
argument_list|(
name|pw
argument_list|)
expr_stmt|;
return|return
name|buffer
operator|.
name|toString
argument_list|(
literal|"UTF-8"
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|MetricsConfigException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

