begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.tools.rumen.datatypes.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|tools
operator|.
name|rumen
operator|.
name|datatypes
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Field
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|DecimalFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|ParseException
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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang
operator|.
name|StringUtils
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
name|mapred
operator|.
name|JobConf
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
name|mapreduce
operator|.
name|MRJobConfig
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
name|tools
operator|.
name|rumen
operator|.
name|datatypes
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
name|log4j
operator|.
name|Level
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

begin_comment
comment|/**  * A default parser for MapReduce job configuration properties.  * MapReduce job configuration properties are represented as key-value pairs.   * Each key represents a configuration knob which controls or affects the   * behavior of a MapReduce job or a job's task. The value associated with the   * configuration key represents its value. Some of the keys are deprecated. As a  * result of deprecation some keys change or are preferred over other keys,   * across versions. {@link MapReduceJobPropertiesParser} is a utility class that  * parses MapReduce job configuration properties and converts the value into a   * well defined {@link DataType}. Users can use the  * {@link #parseJobProperty(String, String)} API to process job   * configuration parameters. This API will parse a job property represented as a  * key-value pair and return the value wrapped inside a {@link DataType}.   * Callers can then use the returned {@link DataType} for further processing.  *   * {@link MapReduceJobPropertiesParser} thrives on the key name to decide which  * {@link DataType} to wrap the value with. Values for keys representing   * job-name, queue-name, user-name etc are wrapped inside {@link JobName},   * {@link QueueName}, {@link UserName} etc respectively. Keys ending with *dir*   * are considered as a directory and hence gets be wrapped inside   * {@link FileName}. Similarly key ending with *codec*, *log*, *class* etc are  * also handled accordingly. Values representing basic java data-types like   * integer, float, double, boolean etc are wrapped inside   * {@link DefaultDataType}. If the key represents some jvm-level settings then   * only standard settings are extracted and gets wrapped inside   * {@link DefaultDataType}. Currently only '-Xmx' and '-Xms' settings are   * considered while the rest are ignored.  *   * Note that the {@link #parseJobProperty(String, String)} API   * maps the keys to a configuration parameter listed in   * {@link MRJobConfig}. This not only filters non-framework specific keys thus   * ignoring user-specific and hard-to-parse keys but also provides a consistent  * view for all possible inputs. So if users invoke the   * {@link #parseJobProperty(String, String)} API with either  *&lt;"mapreduce.job.user.name", "bob"&gt; or&lt;"user.name", "bob"&gt;,  * then the result would be a {@link UserName} {@link DataType} wrapping  * the user-name "bob".  */
end_comment

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
DECL|class|MapReduceJobPropertiesParser
specifier|public
class|class
name|MapReduceJobPropertiesParser
implements|implements
name|JobPropertyParser
block|{
DECL|field|mrFields
specifier|private
name|Field
index|[]
name|mrFields
init|=
name|MRJobConfig
operator|.
name|class
operator|.
name|getFields
argument_list|()
decl_stmt|;
DECL|field|format
specifier|private
name|DecimalFormat
name|format
init|=
operator|new
name|DecimalFormat
argument_list|()
decl_stmt|;
DECL|field|configuration
specifier|private
name|JobConf
name|configuration
init|=
operator|new
name|JobConf
argument_list|(
literal|false
argument_list|)
decl_stmt|;
DECL|field|MAX_HEAP_PATTERN
specifier|private
specifier|static
specifier|final
name|Pattern
name|MAX_HEAP_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"-Xmx[0-9]+[kKmMgGtT]?+"
argument_list|)
decl_stmt|;
DECL|field|MIN_HEAP_PATTERN
specifier|private
specifier|static
specifier|final
name|Pattern
name|MIN_HEAP_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"-Xms[0-9]+[kKmMgGtT]?+"
argument_list|)
decl_stmt|;
comment|// turn off the warning w.r.t deprecated mapreduce keys
static|static
block|{
name|Logger
operator|.
name|getLogger
argument_list|(
name|Configuration
operator|.
name|class
argument_list|)
operator|.
name|setLevel
argument_list|(
name|Level
operator|.
name|OFF
argument_list|)
expr_stmt|;
block|}
comment|// Accepts a key if there is a corresponding key in the current mapreduce
comment|// configuration
DECL|method|accept (String key)
specifier|private
name|boolean
name|accept
parameter_list|(
name|String
name|key
parameter_list|)
block|{
return|return
name|getLatestKeyName
argument_list|(
name|key
argument_list|)
operator|!=
literal|null
return|;
block|}
comment|// Finds a corresponding key for the specified key in the current mapreduce
comment|// setup.
comment|// Note that this API uses a cached copy of the Configuration object. This is
comment|// purely for performance reasons.
DECL|method|getLatestKeyName (String key)
specifier|private
name|String
name|getLatestKeyName
parameter_list|(
name|String
name|key
parameter_list|)
block|{
comment|// set the specified key
name|configuration
operator|.
name|set
argument_list|(
name|key
argument_list|,
name|key
argument_list|)
expr_stmt|;
try|try
block|{
comment|// check if keys in MRConfig maps to the specified key.
for|for
control|(
name|Field
name|f
range|:
name|mrFields
control|)
block|{
name|String
name|mrKey
init|=
name|f
operator|.
name|get
argument_list|(
name|f
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|configuration
operator|.
name|get
argument_list|(
name|mrKey
argument_list|)
operator|!=
literal|null
condition|)
block|{
return|return
name|mrKey
return|;
block|}
block|}
comment|// unset the key
return|return
literal|null
return|;
block|}
catch|catch
parameter_list|(
name|IllegalAccessException
name|iae
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|iae
argument_list|)
throw|;
block|}
finally|finally
block|{
comment|// clean up!
name|configuration
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|parseJobProperty (String key, String value)
specifier|public
name|DataType
argument_list|<
name|?
argument_list|>
name|parseJobProperty
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|value
parameter_list|)
block|{
if|if
condition|(
name|accept
argument_list|(
name|key
argument_list|)
condition|)
block|{
return|return
name|fromString
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
comment|/**    * Extracts the -Xmx heap option from the specified string.    */
DECL|method|extractMaxHeapOpts (final String javaOptions, List<String> heapOpts, List<String> others)
specifier|public
specifier|static
name|void
name|extractMaxHeapOpts
parameter_list|(
specifier|final
name|String
name|javaOptions
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|heapOpts
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|others
parameter_list|)
block|{
for|for
control|(
name|String
name|opt
range|:
name|javaOptions
operator|.
name|split
argument_list|(
literal|" "
argument_list|)
control|)
block|{
name|Matcher
name|matcher
init|=
name|MAX_HEAP_PATTERN
operator|.
name|matcher
argument_list|(
name|opt
argument_list|)
decl_stmt|;
if|if
condition|(
name|matcher
operator|.
name|find
argument_list|()
condition|)
block|{
name|heapOpts
operator|.
name|add
argument_list|(
name|opt
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|others
operator|.
name|add
argument_list|(
name|opt
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Extracts the -Xms heap option from the specified string.    */
DECL|method|extractMinHeapOpts (String javaOptions, List<String> heapOpts, List<String> others)
specifier|public
specifier|static
name|void
name|extractMinHeapOpts
parameter_list|(
name|String
name|javaOptions
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|heapOpts
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|others
parameter_list|)
block|{
for|for
control|(
name|String
name|opt
range|:
name|javaOptions
operator|.
name|split
argument_list|(
literal|" "
argument_list|)
control|)
block|{
name|Matcher
name|matcher
init|=
name|MIN_HEAP_PATTERN
operator|.
name|matcher
argument_list|(
name|opt
argument_list|)
decl_stmt|;
if|if
condition|(
name|matcher
operator|.
name|find
argument_list|()
condition|)
block|{
name|heapOpts
operator|.
name|add
argument_list|(
name|opt
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|others
operator|.
name|add
argument_list|(
name|opt
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// Maps the value of the specified key.
DECL|method|fromString (String key, String value)
specifier|private
name|DataType
argument_list|<
name|?
argument_list|>
name|fromString
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|DefaultDataType
name|defaultValue
init|=
operator|new
name|DefaultDataType
argument_list|(
name|value
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
comment|// check known configs
comment|//  job-name
name|String
name|latestKey
init|=
name|getLatestKeyName
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|MRJobConfig
operator|.
name|JOB_NAME
operator|.
name|equals
argument_list|(
name|latestKey
argument_list|)
condition|)
block|{
return|return
operator|new
name|JobName
argument_list|(
name|value
argument_list|)
return|;
block|}
comment|// user-name
if|if
condition|(
name|MRJobConfig
operator|.
name|USER_NAME
operator|.
name|equals
argument_list|(
name|latestKey
argument_list|)
condition|)
block|{
return|return
operator|new
name|UserName
argument_list|(
name|value
argument_list|)
return|;
block|}
comment|// queue-name
if|if
condition|(
name|MRJobConfig
operator|.
name|QUEUE_NAME
operator|.
name|equals
argument_list|(
name|latestKey
argument_list|)
condition|)
block|{
return|return
operator|new
name|QueueName
argument_list|(
name|value
argument_list|)
return|;
block|}
if|if
condition|(
name|MRJobConfig
operator|.
name|MAP_JAVA_OPTS
operator|.
name|equals
argument_list|(
name|latestKey
argument_list|)
operator|||
name|MRJobConfig
operator|.
name|REDUCE_JAVA_OPTS
operator|.
name|equals
argument_list|(
name|latestKey
argument_list|)
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|heapOptions
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|extractMaxHeapOpts
argument_list|(
name|value
argument_list|,
name|heapOptions
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
name|extractMinHeapOpts
argument_list|(
name|value
argument_list|,
name|heapOptions
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
return|return
operator|new
name|DefaultDataType
argument_list|(
name|StringUtils
operator|.
name|join
argument_list|(
name|heapOptions
argument_list|,
literal|' '
argument_list|)
argument_list|)
return|;
block|}
comment|//TODO compression?
comment|//TODO Other job configs like FileOutputFormat/FileInputFormat etc
comment|// check if the config parameter represents a number
try|try
block|{
name|format
operator|.
name|parse
argument_list|(
name|value
argument_list|)
expr_stmt|;
return|return
name|defaultValue
return|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|pe
parameter_list|)
block|{}
comment|// check if the config parameters represents a boolean
comment|// avoiding exceptions
if|if
condition|(
literal|"true"
operator|.
name|equals
argument_list|(
name|value
argument_list|)
operator|||
literal|"false"
operator|.
name|equals
argument_list|(
name|value
argument_list|)
condition|)
block|{
return|return
name|defaultValue
return|;
block|}
comment|// check if the config parameter represents a class
if|if
condition|(
name|latestKey
operator|.
name|endsWith
argument_list|(
literal|".class"
argument_list|)
operator|||
name|latestKey
operator|.
name|endsWith
argument_list|(
literal|".codec"
argument_list|)
condition|)
block|{
return|return
operator|new
name|ClassName
argument_list|(
name|value
argument_list|)
return|;
block|}
comment|// handle distributed cache sizes and timestamps
if|if
condition|(
name|latestKey
operator|.
name|endsWith
argument_list|(
literal|"sizes"
argument_list|)
operator|||
name|latestKey
operator|.
name|endsWith
argument_list|(
literal|".timestamps"
argument_list|)
condition|)
block|{
return|return
name|defaultValue
return|;
block|}
comment|// check if the config parameter represents a file-system path
comment|//TODO: Make this concrete .location .path .dir .jar?
if|if
condition|(
name|latestKey
operator|.
name|endsWith
argument_list|(
literal|".dir"
argument_list|)
operator|||
name|latestKey
operator|.
name|endsWith
argument_list|(
literal|".location"
argument_list|)
operator|||
name|latestKey
operator|.
name|endsWith
argument_list|(
literal|".jar"
argument_list|)
operator|||
name|latestKey
operator|.
name|endsWith
argument_list|(
literal|".path"
argument_list|)
operator|||
name|latestKey
operator|.
name|endsWith
argument_list|(
literal|".logfile"
argument_list|)
operator|||
name|latestKey
operator|.
name|endsWith
argument_list|(
literal|".file"
argument_list|)
operator|||
name|latestKey
operator|.
name|endsWith
argument_list|(
literal|".files"
argument_list|)
operator|||
name|latestKey
operator|.
name|endsWith
argument_list|(
literal|".archives"
argument_list|)
condition|)
block|{
try|try
block|{
return|return
operator|new
name|FileName
argument_list|(
name|value
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ioe
parameter_list|)
block|{}
block|}
block|}
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

