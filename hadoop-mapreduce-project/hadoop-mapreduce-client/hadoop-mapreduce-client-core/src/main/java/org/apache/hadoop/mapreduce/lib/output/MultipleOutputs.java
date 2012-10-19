begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.lib.output
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|lib
operator|.
name|output
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
name|io
operator|.
name|Text
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
name|mapreduce
operator|.
name|Reducer
operator|.
name|Context
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
name|lib
operator|.
name|output
operator|.
name|MultipleOutputs
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
name|task
operator|.
name|TaskAttemptContextImpl
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
name|ReflectionUtils
import|;
end_import

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
name|*
import|;
end_import

begin_comment
comment|/**  * The MultipleOutputs class simplifies writing output data   * to multiple outputs  *   *<p>   * Case one: writing to additional outputs other than the job default output.  *  * Each additional output, or named output, may be configured with its own  *<code>OutputFormat</code>, with its own key class and with its own value  * class.  *</p>  *   *<p>  * Case two: to write data to different files provided by user  *</p>  *   *<p>  * MultipleOutputs supports counters, by default they are disabled. The   * counters group is the {@link MultipleOutputs} class name. The names of the   * counters are the same as the output name. These count the number records   * written to each output name.  *</p>  *   * Usage pattern for job submission:  *<pre>  *  * Job job = new Job();  *  * FileInputFormat.setInputPath(job, inDir);  * FileOutputFormat.setOutputPath(job, outDir);  *  * job.setMapperClass(MOMap.class);  * job.setReducerClass(MOReduce.class);  * ...  *  * // Defines additional single text based output 'text' for the job  * MultipleOutputs.addNamedOutput(job, "text", TextOutputFormat.class,  * LongWritable.class, Text.class);  *  * // Defines additional sequence-file based output 'sequence' for the job  * MultipleOutputs.addNamedOutput(job, "seq",  *   SequenceFileOutputFormat.class,  *   LongWritable.class, Text.class);  * ...  *  * job.waitForCompletion(true);  * ...  *</pre>  *<p>  * Usage in Reducer:  *<pre>  *<K, V> String generateFileName(K k, V v) {  *   return k.toString() + "_" + v.toString();  * }  *   * public class MOReduce extends  *   Reducer&lt;WritableComparable, Writable,WritableComparable, Writable&gt; {  * private MultipleOutputs mos;  * public void setup(Context context) {  * ...  * mos = new MultipleOutputs(context);  * }  *  * public void reduce(WritableComparable key, Iterator&lt;Writable&gt; values,  * Context context)  * throws IOException {  * ...  * mos.write("text", , key, new Text("Hello"));  * mos.write("seq", LongWritable(1), new Text("Bye"), "seq_a");  * mos.write("seq", LongWritable(2), key, new Text("Chau"), "seq_b");  * mos.write(key, new Text("value"), generateFileName(key, new Text("value")));  * ...  * }  *  * public void cleanup(Context) throws IOException {  * mos.close();  * ...  * }  *  * }  *</pre>  *   *<p>  * When used in conjuction with org.apache.hadoop.mapreduce.lib.output.LazyOutputFormat,  * MultipleOutputs can mimic the behaviour of MultipleTextOutputFormat and MultipleSequenceFileOutputFormat  * from the old Hadoop API - ie, output can be written from the Reducer to more than one location.  *</p>  *   *<p>  * Use<code>MultipleOutputs.write(KEYOUT key, VALUEOUT value, String baseOutputPath)</code> to write key and   * value to a path specified by<code>baseOutputPath</code>, with no need to specify a named output:  *</p>  *   *<pre>  * private MultipleOutputs<Text, Text> out;  *   * public void setup(Context context) {  *   out = new MultipleOutputs<Text, Text>(context);  *   ...  * }  *   * public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {  * for (Text t : values) {  *   out.write(key, t, generateFileName(<<i>parameter list...</i>>));  *   }  * }  *   * protected void cleanup(Context context) throws IOException, InterruptedException {  *   out.close();  * }  *</pre>  *   *<p>  * Use your own code in<code>generateFileName()</code> to create a custom path to your results.   * '/' characters in<code>baseOutputPath</code> will be translated into directory levels in your file system.   * Also, append your custom-generated path with "part" or similar, otherwise your output will be -00000, -00001 etc.   * No call to<code>context.write()</code> is necessary. See example<code>generateFileName()</code> code below.   *</p>  *   *<pre>  * private String generateFileName(Text k) {  *   // expect Text k in format "Surname|Forename"  *   String[] kStr = k.toString().split("\\|");  *     *   String sName = kStr[0];  *   String fName = kStr[1];  *  *   // example for k = Smith|John  *   // output written to /user/hadoop/path/to/output/Smith/John-r-00000 (etc)  *   return sName + "/" + fName;  * }  *</pre>  *   *<p>  * Using MultipleOutputs in this way will still create zero-sized default output, eg part-00000.  * To prevent this use<code>LazyOutputFormat.setOutputFormatClass(job, TextOutputFormat.class);</code>  * instead of<code>job.setOutputFormatClass(TextOutputFormat.class);</code> in your Hadoop job configuration.  *</p>   *   */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|class|MultipleOutputs
specifier|public
class|class
name|MultipleOutputs
parameter_list|<
name|KEYOUT
parameter_list|,
name|VALUEOUT
parameter_list|>
block|{
DECL|field|MULTIPLE_OUTPUTS
specifier|private
specifier|static
specifier|final
name|String
name|MULTIPLE_OUTPUTS
init|=
literal|"mapreduce.multipleoutputs"
decl_stmt|;
DECL|field|MO_PREFIX
specifier|private
specifier|static
specifier|final
name|String
name|MO_PREFIX
init|=
literal|"mapreduce.multipleoutputs.namedOutput."
decl_stmt|;
DECL|field|FORMAT
specifier|private
specifier|static
specifier|final
name|String
name|FORMAT
init|=
literal|".format"
decl_stmt|;
DECL|field|KEY
specifier|private
specifier|static
specifier|final
name|String
name|KEY
init|=
literal|".key"
decl_stmt|;
DECL|field|VALUE
specifier|private
specifier|static
specifier|final
name|String
name|VALUE
init|=
literal|".value"
decl_stmt|;
DECL|field|COUNTERS_ENABLED
specifier|private
specifier|static
specifier|final
name|String
name|COUNTERS_ENABLED
init|=
literal|"mapreduce.multipleoutputs.counters"
decl_stmt|;
comment|/**    * Counters group used by the counters of MultipleOutputs.    */
DECL|field|COUNTERS_GROUP
specifier|private
specifier|static
specifier|final
name|String
name|COUNTERS_GROUP
init|=
name|MultipleOutputs
operator|.
name|class
operator|.
name|getName
argument_list|()
decl_stmt|;
comment|/**    * Cache for the taskContexts    */
DECL|field|taskContexts
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|TaskAttemptContext
argument_list|>
name|taskContexts
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|TaskAttemptContext
argument_list|>
argument_list|()
decl_stmt|;
comment|/**    * Cached TaskAttemptContext which uses the job's configured settings    */
DECL|field|jobOutputFormatContext
specifier|private
name|TaskAttemptContext
name|jobOutputFormatContext
decl_stmt|;
comment|/**    * Checks if a named output name is valid token.    *    * @param namedOutput named output Name    * @throws IllegalArgumentException if the output name is not valid.    */
DECL|method|checkTokenName (String namedOutput)
specifier|private
specifier|static
name|void
name|checkTokenName
parameter_list|(
name|String
name|namedOutput
parameter_list|)
block|{
if|if
condition|(
name|namedOutput
operator|==
literal|null
operator|||
name|namedOutput
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Name cannot be NULL or emtpy"
argument_list|)
throw|;
block|}
for|for
control|(
name|char
name|ch
range|:
name|namedOutput
operator|.
name|toCharArray
argument_list|()
control|)
block|{
if|if
condition|(
operator|(
name|ch
operator|>=
literal|'A'
operator|)
operator|&&
operator|(
name|ch
operator|<=
literal|'Z'
operator|)
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
operator|(
name|ch
operator|>=
literal|'a'
operator|)
operator|&&
operator|(
name|ch
operator|<=
literal|'z'
operator|)
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
operator|(
name|ch
operator|>=
literal|'0'
operator|)
operator|&&
operator|(
name|ch
operator|<=
literal|'9'
operator|)
condition|)
block|{
continue|continue;
block|}
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Name cannot be have a '"
operator|+
name|ch
operator|+
literal|"' char"
argument_list|)
throw|;
block|}
block|}
comment|/**    * Checks if output name is valid.    *    * name cannot be the name used for the default output    * @param outputPath base output Name    * @throws IllegalArgumentException if the output name is not valid.    */
DECL|method|checkBaseOutputPath (String outputPath)
specifier|private
specifier|static
name|void
name|checkBaseOutputPath
parameter_list|(
name|String
name|outputPath
parameter_list|)
block|{
if|if
condition|(
name|outputPath
operator|.
name|equals
argument_list|(
name|FileOutputFormat
operator|.
name|PART
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"output name cannot be 'part'"
argument_list|)
throw|;
block|}
block|}
comment|/**    * Checks if a named output name is valid.    *    * @param namedOutput named output Name    * @throws IllegalArgumentException if the output name is not valid.    */
DECL|method|checkNamedOutputName (JobContext job, String namedOutput, boolean alreadyDefined)
specifier|private
specifier|static
name|void
name|checkNamedOutputName
parameter_list|(
name|JobContext
name|job
parameter_list|,
name|String
name|namedOutput
parameter_list|,
name|boolean
name|alreadyDefined
parameter_list|)
block|{
name|checkTokenName
argument_list|(
name|namedOutput
argument_list|)
expr_stmt|;
name|checkBaseOutputPath
argument_list|(
name|namedOutput
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|definedChannels
init|=
name|getNamedOutputsList
argument_list|(
name|job
argument_list|)
decl_stmt|;
if|if
condition|(
name|alreadyDefined
operator|&&
name|definedChannels
operator|.
name|contains
argument_list|(
name|namedOutput
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Named output '"
operator|+
name|namedOutput
operator|+
literal|"' already alreadyDefined"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
operator|!
name|alreadyDefined
operator|&&
operator|!
name|definedChannels
operator|.
name|contains
argument_list|(
name|namedOutput
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Named output '"
operator|+
name|namedOutput
operator|+
literal|"' not defined"
argument_list|)
throw|;
block|}
block|}
comment|// Returns list of channel names.
DECL|method|getNamedOutputsList (JobContext job)
specifier|private
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|getNamedOutputsList
parameter_list|(
name|JobContext
name|job
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|names
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|StringTokenizer
name|st
init|=
operator|new
name|StringTokenizer
argument_list|(
name|job
operator|.
name|getConfiguration
argument_list|()
operator|.
name|get
argument_list|(
name|MULTIPLE_OUTPUTS
argument_list|,
literal|""
argument_list|)
argument_list|,
literal|" "
argument_list|)
decl_stmt|;
while|while
condition|(
name|st
operator|.
name|hasMoreTokens
argument_list|()
condition|)
block|{
name|names
operator|.
name|add
argument_list|(
name|st
operator|.
name|nextToken
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|names
return|;
block|}
comment|// Returns the named output OutputFormat.
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|getNamedOutputFormatClass ( JobContext job, String namedOutput)
specifier|private
specifier|static
name|Class
argument_list|<
name|?
extends|extends
name|OutputFormat
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
argument_list|>
name|getNamedOutputFormatClass
parameter_list|(
name|JobContext
name|job
parameter_list|,
name|String
name|namedOutput
parameter_list|)
block|{
return|return
operator|(
name|Class
argument_list|<
name|?
extends|extends
name|OutputFormat
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
argument_list|>
operator|)
name|job
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getClass
argument_list|(
name|MO_PREFIX
operator|+
name|namedOutput
operator|+
name|FORMAT
argument_list|,
literal|null
argument_list|,
name|OutputFormat
operator|.
name|class
argument_list|)
return|;
block|}
comment|// Returns the key class for a named output.
DECL|method|getNamedOutputKeyClass (JobContext job, String namedOutput)
specifier|private
specifier|static
name|Class
argument_list|<
name|?
argument_list|>
name|getNamedOutputKeyClass
parameter_list|(
name|JobContext
name|job
parameter_list|,
name|String
name|namedOutput
parameter_list|)
block|{
return|return
name|job
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getClass
argument_list|(
name|MO_PREFIX
operator|+
name|namedOutput
operator|+
name|KEY
argument_list|,
literal|null
argument_list|,
name|Object
operator|.
name|class
argument_list|)
return|;
block|}
comment|// Returns the value class for a named output.
DECL|method|getNamedOutputValueClass ( JobContext job, String namedOutput)
specifier|private
specifier|static
name|Class
argument_list|<
name|?
argument_list|>
name|getNamedOutputValueClass
parameter_list|(
name|JobContext
name|job
parameter_list|,
name|String
name|namedOutput
parameter_list|)
block|{
return|return
name|job
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getClass
argument_list|(
name|MO_PREFIX
operator|+
name|namedOutput
operator|+
name|VALUE
argument_list|,
literal|null
argument_list|,
name|Object
operator|.
name|class
argument_list|)
return|;
block|}
comment|/**    * Adds a named output for the job.    *<p/>    *    * @param job               job to add the named output    * @param namedOutput       named output name, it has to be a word, letters    *                          and numbers only, cannot be the word 'part' as    *                          that is reserved for the default output.    * @param outputFormatClass OutputFormat class.    * @param keyClass          key class    * @param valueClass        value class    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|addNamedOutput (Job job, String namedOutput, Class<? extends OutputFormat> outputFormatClass, Class<?> keyClass, Class<?> valueClass)
specifier|public
specifier|static
name|void
name|addNamedOutput
parameter_list|(
name|Job
name|job
parameter_list|,
name|String
name|namedOutput
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|OutputFormat
argument_list|>
name|outputFormatClass
parameter_list|,
name|Class
argument_list|<
name|?
argument_list|>
name|keyClass
parameter_list|,
name|Class
argument_list|<
name|?
argument_list|>
name|valueClass
parameter_list|)
block|{
name|checkNamedOutputName
argument_list|(
name|job
argument_list|,
name|namedOutput
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|Configuration
name|conf
init|=
name|job
operator|.
name|getConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|MULTIPLE_OUTPUTS
argument_list|,
name|conf
operator|.
name|get
argument_list|(
name|MULTIPLE_OUTPUTS
argument_list|,
literal|""
argument_list|)
operator|+
literal|" "
operator|+
name|namedOutput
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setClass
argument_list|(
name|MO_PREFIX
operator|+
name|namedOutput
operator|+
name|FORMAT
argument_list|,
name|outputFormatClass
argument_list|,
name|OutputFormat
operator|.
name|class
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setClass
argument_list|(
name|MO_PREFIX
operator|+
name|namedOutput
operator|+
name|KEY
argument_list|,
name|keyClass
argument_list|,
name|Object
operator|.
name|class
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setClass
argument_list|(
name|MO_PREFIX
operator|+
name|namedOutput
operator|+
name|VALUE
argument_list|,
name|valueClass
argument_list|,
name|Object
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
comment|/**    * Enables or disables counters for the named outputs.    *     * The counters group is the {@link MultipleOutputs} class name.    * The names of the counters are the same as the named outputs. These    * counters count the number records written to each output name.    * By default these counters are disabled.    *    * @param job    job  to enable counters    * @param enabled indicates if the counters will be enabled or not.    */
DECL|method|setCountersEnabled (Job job, boolean enabled)
specifier|public
specifier|static
name|void
name|setCountersEnabled
parameter_list|(
name|Job
name|job
parameter_list|,
name|boolean
name|enabled
parameter_list|)
block|{
name|job
operator|.
name|getConfiguration
argument_list|()
operator|.
name|setBoolean
argument_list|(
name|COUNTERS_ENABLED
argument_list|,
name|enabled
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns if the counters for the named outputs are enabled or not.    * By default these counters are disabled.    *    * @param job    the job     * @return TRUE if the counters are enabled, FALSE if they are disabled.    */
DECL|method|getCountersEnabled (JobContext job)
specifier|public
specifier|static
name|boolean
name|getCountersEnabled
parameter_list|(
name|JobContext
name|job
parameter_list|)
block|{
return|return
name|job
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getBoolean
argument_list|(
name|COUNTERS_ENABLED
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/**    * Wraps RecordWriter to increment counters.     */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|class|RecordWriterWithCounter
specifier|private
specifier|static
class|class
name|RecordWriterWithCounter
extends|extends
name|RecordWriter
block|{
DECL|field|writer
specifier|private
name|RecordWriter
name|writer
decl_stmt|;
DECL|field|counterName
specifier|private
name|String
name|counterName
decl_stmt|;
DECL|field|context
specifier|private
name|TaskInputOutputContext
name|context
decl_stmt|;
DECL|method|RecordWriterWithCounter (RecordWriter writer, String counterName, TaskInputOutputContext context)
specifier|public
name|RecordWriterWithCounter
parameter_list|(
name|RecordWriter
name|writer
parameter_list|,
name|String
name|counterName
parameter_list|,
name|TaskInputOutputContext
name|context
parameter_list|)
block|{
name|this
operator|.
name|writer
operator|=
name|writer
expr_stmt|;
name|this
operator|.
name|counterName
operator|=
name|counterName
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|}
argument_list|)
DECL|method|write (Object key, Object value)
specifier|public
name|void
name|write
parameter_list|(
name|Object
name|key
parameter_list|,
name|Object
name|value
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|context
operator|.
name|getCounter
argument_list|(
name|COUNTERS_GROUP
argument_list|,
name|counterName
argument_list|)
operator|.
name|increment
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
DECL|method|close (TaskAttemptContext context)
specifier|public
name|void
name|close
parameter_list|(
name|TaskAttemptContext
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|writer
operator|.
name|close
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
block|}
comment|// instance code, to be used from Mapper/Reducer code
DECL|field|context
specifier|private
name|TaskInputOutputContext
argument_list|<
name|?
argument_list|,
name|?
argument_list|,
name|KEYOUT
argument_list|,
name|VALUEOUT
argument_list|>
name|context
decl_stmt|;
DECL|field|namedOutputs
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|namedOutputs
decl_stmt|;
DECL|field|recordWriters
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|RecordWriter
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
argument_list|>
name|recordWriters
decl_stmt|;
DECL|field|countersEnabled
specifier|private
name|boolean
name|countersEnabled
decl_stmt|;
comment|/**    * Creates and initializes multiple outputs support,    * it should be instantiated in the Mapper/Reducer setup method.    *    * @param context the TaskInputOutputContext object    */
DECL|method|MultipleOutputs ( TaskInputOutputContext<?, ?, KEYOUT, VALUEOUT> context)
specifier|public
name|MultipleOutputs
parameter_list|(
name|TaskInputOutputContext
argument_list|<
name|?
argument_list|,
name|?
argument_list|,
name|KEYOUT
argument_list|,
name|VALUEOUT
argument_list|>
name|context
parameter_list|)
block|{
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
name|namedOutputs
operator|=
name|Collections
operator|.
name|unmodifiableSet
argument_list|(
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|(
name|MultipleOutputs
operator|.
name|getNamedOutputsList
argument_list|(
name|context
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|recordWriters
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|RecordWriter
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
argument_list|>
argument_list|()
expr_stmt|;
name|countersEnabled
operator|=
name|getCountersEnabled
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
comment|/**    * Write key and value to the namedOutput.    *    * Output path is a unique file generated for the namedOutput.    * For example, {namedOutput}-(m|r)-{part-number}    *     * @param namedOutput the named output name    * @param key         the key    * @param value       the value    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|write (String namedOutput, K key, V value)
specifier|public
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
name|void
name|write
parameter_list|(
name|String
name|namedOutput
parameter_list|,
name|K
name|key
parameter_list|,
name|V
name|value
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|write
argument_list|(
name|namedOutput
argument_list|,
name|key
argument_list|,
name|value
argument_list|,
name|namedOutput
argument_list|)
expr_stmt|;
block|}
comment|/**    * Write key and value to baseOutputPath using the namedOutput.    *     * @param namedOutput    the named output name    * @param key            the key    * @param value          the value    * @param baseOutputPath base-output path to write the record to.    * Note: Framework will generate unique filename for the baseOutputPath    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|write (String namedOutput, K key, V value, String baseOutputPath)
specifier|public
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
name|void
name|write
parameter_list|(
name|String
name|namedOutput
parameter_list|,
name|K
name|key
parameter_list|,
name|V
name|value
parameter_list|,
name|String
name|baseOutputPath
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|checkNamedOutputName
argument_list|(
name|context
argument_list|,
name|namedOutput
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|checkBaseOutputPath
argument_list|(
name|baseOutputPath
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|namedOutputs
operator|.
name|contains
argument_list|(
name|namedOutput
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Undefined named output '"
operator|+
name|namedOutput
operator|+
literal|"'"
argument_list|)
throw|;
block|}
name|TaskAttemptContext
name|taskContext
init|=
name|getContext
argument_list|(
name|namedOutput
argument_list|)
decl_stmt|;
name|getRecordWriter
argument_list|(
name|taskContext
argument_list|,
name|baseOutputPath
argument_list|)
operator|.
name|write
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
comment|/**    * Write key value to an output file name.    *     * Gets the record writer from job's output format.      * Job's output format should be a FileOutputFormat.    *     * @param key       the key    * @param value     the value    * @param baseOutputPath base-output path to write the record to.    * Note: Framework will generate unique filename for the baseOutputPath    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|write (KEYOUT key, VALUEOUT value, String baseOutputPath)
specifier|public
name|void
name|write
parameter_list|(
name|KEYOUT
name|key
parameter_list|,
name|VALUEOUT
name|value
parameter_list|,
name|String
name|baseOutputPath
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|checkBaseOutputPath
argument_list|(
name|baseOutputPath
argument_list|)
expr_stmt|;
if|if
condition|(
name|jobOutputFormatContext
operator|==
literal|null
condition|)
block|{
name|jobOutputFormatContext
operator|=
operator|new
name|TaskAttemptContextImpl
argument_list|(
name|context
operator|.
name|getConfiguration
argument_list|()
argument_list|,
name|context
operator|.
name|getTaskAttemptID
argument_list|()
argument_list|,
operator|new
name|WrappedStatusReporter
argument_list|(
name|context
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|getRecordWriter
argument_list|(
name|jobOutputFormatContext
argument_list|,
name|baseOutputPath
argument_list|)
operator|.
name|write
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
comment|// by being synchronized MultipleOutputTask can be use with a
comment|// MultithreadedMapper.
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|getRecordWriter ( TaskAttemptContext taskContext, String baseFileName)
specifier|private
specifier|synchronized
name|RecordWriter
name|getRecordWriter
parameter_list|(
name|TaskAttemptContext
name|taskContext
parameter_list|,
name|String
name|baseFileName
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
comment|// look for record-writer in the cache
name|RecordWriter
name|writer
init|=
name|recordWriters
operator|.
name|get
argument_list|(
name|baseFileName
argument_list|)
decl_stmt|;
comment|// If not in cache, create a new one
if|if
condition|(
name|writer
operator|==
literal|null
condition|)
block|{
comment|// get the record writer from context output format
name|FileOutputFormat
operator|.
name|setOutputName
argument_list|(
name|taskContext
argument_list|,
name|baseFileName
argument_list|)
expr_stmt|;
try|try
block|{
name|writer
operator|=
operator|(
operator|(
name|OutputFormat
operator|)
name|ReflectionUtils
operator|.
name|newInstance
argument_list|(
name|taskContext
operator|.
name|getOutputFormatClass
argument_list|()
argument_list|,
name|taskContext
operator|.
name|getConfiguration
argument_list|()
argument_list|)
operator|)
operator|.
name|getRecordWriter
argument_list|(
name|taskContext
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
comment|// if counters are enabled, wrap the writer with context
comment|// to increment counters
if|if
condition|(
name|countersEnabled
condition|)
block|{
name|writer
operator|=
operator|new
name|RecordWriterWithCounter
argument_list|(
name|writer
argument_list|,
name|baseFileName
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
comment|// add the record-writer to the cache
name|recordWriters
operator|.
name|put
argument_list|(
name|baseFileName
argument_list|,
name|writer
argument_list|)
expr_stmt|;
block|}
return|return
name|writer
return|;
block|}
comment|// Create a taskAttemptContext for the named output with
comment|// output format and output key/value types put in the context
DECL|method|getContext (String nameOutput)
specifier|private
name|TaskAttemptContext
name|getContext
parameter_list|(
name|String
name|nameOutput
parameter_list|)
throws|throws
name|IOException
block|{
name|TaskAttemptContext
name|taskContext
init|=
name|taskContexts
operator|.
name|get
argument_list|(
name|nameOutput
argument_list|)
decl_stmt|;
if|if
condition|(
name|taskContext
operator|!=
literal|null
condition|)
block|{
return|return
name|taskContext
return|;
block|}
comment|// The following trick leverages the instantiation of a record writer via
comment|// the job thus supporting arbitrary output formats.
name|Job
name|job
init|=
operator|new
name|Job
argument_list|(
name|context
operator|.
name|getConfiguration
argument_list|()
argument_list|)
decl_stmt|;
name|job
operator|.
name|setOutputFormatClass
argument_list|(
name|getNamedOutputFormatClass
argument_list|(
name|context
argument_list|,
name|nameOutput
argument_list|)
argument_list|)
expr_stmt|;
name|job
operator|.
name|setOutputKeyClass
argument_list|(
name|getNamedOutputKeyClass
argument_list|(
name|context
argument_list|,
name|nameOutput
argument_list|)
argument_list|)
expr_stmt|;
name|job
operator|.
name|setOutputValueClass
argument_list|(
name|getNamedOutputValueClass
argument_list|(
name|context
argument_list|,
name|nameOutput
argument_list|)
argument_list|)
expr_stmt|;
name|taskContext
operator|=
operator|new
name|TaskAttemptContextImpl
argument_list|(
name|job
operator|.
name|getConfiguration
argument_list|()
argument_list|,
name|context
operator|.
name|getTaskAttemptID
argument_list|()
argument_list|,
operator|new
name|WrappedStatusReporter
argument_list|(
name|context
argument_list|)
argument_list|)
expr_stmt|;
name|taskContexts
operator|.
name|put
argument_list|(
name|nameOutput
argument_list|,
name|taskContext
argument_list|)
expr_stmt|;
return|return
name|taskContext
return|;
block|}
DECL|class|WrappedStatusReporter
specifier|private
specifier|static
class|class
name|WrappedStatusReporter
extends|extends
name|StatusReporter
block|{
DECL|field|context
name|TaskAttemptContext
name|context
decl_stmt|;
DECL|method|WrappedStatusReporter (TaskAttemptContext context)
specifier|public
name|WrappedStatusReporter
parameter_list|(
name|TaskAttemptContext
name|context
parameter_list|)
block|{
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getCounter (Enum<?> name)
specifier|public
name|Counter
name|getCounter
parameter_list|(
name|Enum
argument_list|<
name|?
argument_list|>
name|name
parameter_list|)
block|{
return|return
name|context
operator|.
name|getCounter
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getCounter (String group, String name)
specifier|public
name|Counter
name|getCounter
parameter_list|(
name|String
name|group
parameter_list|,
name|String
name|name
parameter_list|)
block|{
return|return
name|context
operator|.
name|getCounter
argument_list|(
name|group
argument_list|,
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|progress ()
specifier|public
name|void
name|progress
parameter_list|()
block|{
name|context
operator|.
name|progress
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getProgress ()
specifier|public
name|float
name|getProgress
parameter_list|()
block|{
return|return
name|context
operator|.
name|getProgress
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setStatus (String status)
specifier|public
name|void
name|setStatus
parameter_list|(
name|String
name|status
parameter_list|)
block|{
name|context
operator|.
name|setStatus
argument_list|(
name|status
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Closes all the opened outputs.    *     * This should be called from cleanup method of map/reduce task.    * If overridden subclasses must invoke<code>super.close()</code> at the    * end of their<code>close()</code>    *     */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
for|for
control|(
name|RecordWriter
name|writer
range|:
name|recordWriters
operator|.
name|values
argument_list|()
control|)
block|{
name|writer
operator|.
name|close
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

