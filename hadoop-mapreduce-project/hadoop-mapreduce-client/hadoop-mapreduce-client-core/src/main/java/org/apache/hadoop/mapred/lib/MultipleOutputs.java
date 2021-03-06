begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred.lib
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|lib
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
name|fs
operator|.
name|FileSystem
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
name|util
operator|.
name|Progressable
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
comment|/**  * The MultipleOutputs class simplifies writing to additional outputs other  * than the job default output via the<code>OutputCollector</code> passed to  * the<code>map()</code> and<code>reduce()</code> methods of the  *<code>Mapper</code> and<code>Reducer</code> implementations.  *<p>  * Each additional output, or named output, may be configured with its own  *<code>OutputFormat</code>, with its own key class and with its own value  * class.  *<p>  * A named output can be a single file or a multi file. The later is referred as  * a multi named output.  *<p>  * A multi named output is an unbound set of files all sharing the same  *<code>OutputFormat</code>, key class and value class configuration.  *<p>  * When named outputs are used within a<code>Mapper</code> implementation,  * key/values written to a name output are not part of the reduce phase, only  * key/values written to the job<code>OutputCollector</code> are part of the  * reduce phase.  *<p>  * MultipleOutputs supports counters, by default the are disabled. The counters  * group is the {@link MultipleOutputs} class name.  *</p>  * The names of the counters are the same as the named outputs. For multi  * named outputs the name of the counter is the concatenation of the named  * output, and underscore '_' and the multiname.  *<p>  * Job configuration usage pattern is:  *<pre>  *  * JobConf conf = new JobConf();  *  * conf.setInputPath(inDir);  * FileOutputFormat.setOutputPath(conf, outDir);  *  * conf.setMapperClass(MOMap.class);  * conf.setReducerClass(MOReduce.class);  * ...  *  * // Defines additional single text based output 'text' for the job  * MultipleOutputs.addNamedOutput(conf, "text", TextOutputFormat.class,  * LongWritable.class, Text.class);  *  * // Defines additional multi sequencefile based output 'sequence' for the  * // job  * MultipleOutputs.addMultiNamedOutput(conf, "seq",  *   SequenceFileOutputFormat.class,  *   LongWritable.class, Text.class);  * ...  *  * JobClient jc = new JobClient();  * RunningJob job = jc.submitJob(conf);  *  * ...  *</pre>  *<p>  * Job configuration usage pattern is:  *<pre>  *  * public class MOReduce implements  *   Reducer&lt;WritableComparable, Writable&gt; {  * private MultipleOutputs mos;  *  * public void configure(JobConf conf) {  * ...  * mos = new MultipleOutputs(conf);  * }  *  * public void reduce(WritableComparable key, Iterator&lt;Writable&gt; values,  * OutputCollector output, Reporter reporter)  * throws IOException {  * ...  * mos.getCollector("text", reporter).collect(key, new Text("Hello"));  * mos.getCollector("seq", "A", reporter).collect(key, new Text("Bye"));  * mos.getCollector("seq", "B", reporter).collect(key, new Text("Chau"));  * ...  * }  *  * public void close() throws IOException {  * mos.close();  * ...  * }  *  * }  *</pre>  */
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
block|{
DECL|field|NAMED_OUTPUTS
specifier|private
specifier|static
specifier|final
name|String
name|NAMED_OUTPUTS
init|=
literal|"mo.namedOutputs"
decl_stmt|;
DECL|field|MO_PREFIX
specifier|private
specifier|static
specifier|final
name|String
name|MO_PREFIX
init|=
literal|"mo.namedOutput."
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
DECL|field|MULTI
specifier|private
specifier|static
specifier|final
name|String
name|MULTI
init|=
literal|".multi"
decl_stmt|;
DECL|field|COUNTERS_ENABLED
specifier|private
specifier|static
specifier|final
name|String
name|COUNTERS_ENABLED
init|=
literal|"mo.counters"
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
comment|/**    * Checks if a named output is alreadyDefined or not.    *    * @param conf           job conf    * @param namedOutput    named output names    * @param alreadyDefined whether the existence/non-existence of    *                       the named output is to be checked    * @throws IllegalArgumentException if the output name is alreadyDefined or    *                                  not depending on the value of the    *                                  'alreadyDefined' parameter    */
DECL|method|checkNamedOutput (JobConf conf, String namedOutput, boolean alreadyDefined)
specifier|private
specifier|static
name|void
name|checkNamedOutput
parameter_list|(
name|JobConf
name|conf
parameter_list|,
name|String
name|namedOutput
parameter_list|,
name|boolean
name|alreadyDefined
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|definedChannels
init|=
name|getNamedOutputsList
argument_list|(
name|conf
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
comment|/**    * Checks if a named output name is valid.    *    * @param namedOutput named output Name    * @throws IllegalArgumentException if the output name is not valid.    */
DECL|method|checkNamedOutputName (String namedOutput)
specifier|private
specifier|static
name|void
name|checkNamedOutputName
parameter_list|(
name|String
name|namedOutput
parameter_list|)
block|{
name|checkTokenName
argument_list|(
name|namedOutput
argument_list|)
expr_stmt|;
comment|// name cannot be the name used for the default output
if|if
condition|(
name|namedOutput
operator|.
name|equals
argument_list|(
literal|"part"
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Named output name cannot be 'part'"
argument_list|)
throw|;
block|}
block|}
comment|/**    * Returns list of channel names.    *    * @param conf job conf    * @return List of channel Names    */
DECL|method|getNamedOutputsList (JobConf conf)
specifier|public
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|getNamedOutputsList
parameter_list|(
name|JobConf
name|conf
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
name|conf
operator|.
name|get
argument_list|(
name|NAMED_OUTPUTS
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
comment|/**    * Returns if a named output is multiple.    *    * @param conf        job conf    * @param namedOutput named output    * @return<code>true</code> if the name output is multi,<code>false</code>    *         if it is single. If the name output is not defined it returns    *<code>false</code>    */
DECL|method|isMultiNamedOutput (JobConf conf, String namedOutput)
specifier|public
specifier|static
name|boolean
name|isMultiNamedOutput
parameter_list|(
name|JobConf
name|conf
parameter_list|,
name|String
name|namedOutput
parameter_list|)
block|{
name|checkNamedOutput
argument_list|(
name|conf
argument_list|,
name|namedOutput
argument_list|,
literal|false
argument_list|)
expr_stmt|;
return|return
name|conf
operator|.
name|getBoolean
argument_list|(
name|MO_PREFIX
operator|+
name|namedOutput
operator|+
name|MULTI
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/**    * Returns the named output OutputFormat.    *    * @param conf        job conf    * @param namedOutput named output    * @return namedOutput OutputFormat    */
DECL|method|getNamedOutputFormatClass ( JobConf conf, String namedOutput)
specifier|public
specifier|static
name|Class
argument_list|<
name|?
extends|extends
name|OutputFormat
argument_list|>
name|getNamedOutputFormatClass
parameter_list|(
name|JobConf
name|conf
parameter_list|,
name|String
name|namedOutput
parameter_list|)
block|{
name|checkNamedOutput
argument_list|(
name|conf
argument_list|,
name|namedOutput
argument_list|,
literal|false
argument_list|)
expr_stmt|;
return|return
name|conf
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
comment|/**    * Returns the key class for a named output.    *    * @param conf        job conf    * @param namedOutput named output    * @return class for the named output key    */
DECL|method|getNamedOutputKeyClass (JobConf conf, String namedOutput)
specifier|public
specifier|static
name|Class
argument_list|<
name|?
argument_list|>
name|getNamedOutputKeyClass
parameter_list|(
name|JobConf
name|conf
parameter_list|,
name|String
name|namedOutput
parameter_list|)
block|{
name|checkNamedOutput
argument_list|(
name|conf
argument_list|,
name|namedOutput
argument_list|,
literal|false
argument_list|)
expr_stmt|;
return|return
name|conf
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
comment|/**    * Returns the value class for a named output.    *    * @param conf        job conf    * @param namedOutput named output    * @return class of named output value    */
DECL|method|getNamedOutputValueClass (JobConf conf, String namedOutput)
specifier|public
specifier|static
name|Class
argument_list|<
name|?
argument_list|>
name|getNamedOutputValueClass
parameter_list|(
name|JobConf
name|conf
parameter_list|,
name|String
name|namedOutput
parameter_list|)
block|{
name|checkNamedOutput
argument_list|(
name|conf
argument_list|,
name|namedOutput
argument_list|,
literal|false
argument_list|)
expr_stmt|;
return|return
name|conf
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
comment|/**    * Adds a named output for the job.    *    * @param conf              job conf to add the named output    * @param namedOutput       named output name, it has to be a word, letters    *                          and numbers only, cannot be the word 'part' as    *                          that is reserved for the    *                          default output.    * @param outputFormatClass OutputFormat class.    * @param keyClass          key class    * @param valueClass        value class    */
DECL|method|addNamedOutput (JobConf conf, String namedOutput, Class<? extends OutputFormat> outputFormatClass, Class<?> keyClass, Class<?> valueClass)
specifier|public
specifier|static
name|void
name|addNamedOutput
parameter_list|(
name|JobConf
name|conf
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
name|addNamedOutput
argument_list|(
name|conf
argument_list|,
name|namedOutput
argument_list|,
literal|false
argument_list|,
name|outputFormatClass
argument_list|,
name|keyClass
argument_list|,
name|valueClass
argument_list|)
expr_stmt|;
block|}
comment|/**    * Adds a multi named output for the job.    *    * @param conf              job conf to add the named output    * @param namedOutput       named output name, it has to be a word, letters    *                          and numbers only, cannot be the word 'part' as    *                          that is reserved for the    *                          default output.    * @param outputFormatClass OutputFormat class.    * @param keyClass          key class    * @param valueClass        value class    */
DECL|method|addMultiNamedOutput (JobConf conf, String namedOutput, Class<? extends OutputFormat> outputFormatClass, Class<?> keyClass, Class<?> valueClass)
specifier|public
specifier|static
name|void
name|addMultiNamedOutput
parameter_list|(
name|JobConf
name|conf
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
name|addNamedOutput
argument_list|(
name|conf
argument_list|,
name|namedOutput
argument_list|,
literal|true
argument_list|,
name|outputFormatClass
argument_list|,
name|keyClass
argument_list|,
name|valueClass
argument_list|)
expr_stmt|;
block|}
comment|/**    * Adds a named output for the job.    *    * @param conf              job conf to add the named output    * @param namedOutput       named output name, it has to be a word, letters    *                          and numbers only, cannot be the word 'part' as    *                          that is reserved for the    *                          default output.    * @param multi             indicates if the named output is multi    * @param outputFormatClass OutputFormat class.    * @param keyClass          key class    * @param valueClass        value class    */
DECL|method|addNamedOutput (JobConf conf, String namedOutput, boolean multi, Class<? extends OutputFormat> outputFormatClass, Class<?> keyClass, Class<?> valueClass)
specifier|private
specifier|static
name|void
name|addNamedOutput
parameter_list|(
name|JobConf
name|conf
parameter_list|,
name|String
name|namedOutput
parameter_list|,
name|boolean
name|multi
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
name|namedOutput
argument_list|)
expr_stmt|;
name|checkNamedOutput
argument_list|(
name|conf
argument_list|,
name|namedOutput
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|NAMED_OUTPUTS
argument_list|,
name|conf
operator|.
name|get
argument_list|(
name|NAMED_OUTPUTS
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
name|conf
operator|.
name|setBoolean
argument_list|(
name|MO_PREFIX
operator|+
name|namedOutput
operator|+
name|MULTI
argument_list|,
name|multi
argument_list|)
expr_stmt|;
block|}
comment|/**    * Enables or disables counters for the named outputs.    *<p>    * By default these counters are disabled.    *<p>    * MultipleOutputs supports counters, by default the are disabled.    * The counters group is the {@link MultipleOutputs} class name.    *</p>    * The names of the counters are the same as the named outputs. For multi    * named outputs the name of the counter is the concatenation of the named    * output, and underscore '_' and the multiname.    *    * @param conf    job conf to enableadd the named output.    * @param enabled indicates if the counters will be enabled or not.    */
DECL|method|setCountersEnabled (JobConf conf, boolean enabled)
specifier|public
specifier|static
name|void
name|setCountersEnabled
parameter_list|(
name|JobConf
name|conf
parameter_list|,
name|boolean
name|enabled
parameter_list|)
block|{
name|conf
operator|.
name|setBoolean
argument_list|(
name|COUNTERS_ENABLED
argument_list|,
name|enabled
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns if the counters for the named outputs are enabled or not.    *<p>    * By default these counters are disabled.    *<p>    * MultipleOutputs supports counters, by default the are disabled.    * The counters group is the {@link MultipleOutputs} class name.    *</p>    * The names of the counters are the same as the named outputs. For multi    * named outputs the name of the counter is the concatenation of the named    * output, and underscore '_' and the multiname.    *    *    * @param conf    job conf to enableadd the named output.    * @return TRUE if the counters are enabled, FALSE if they are disabled.    */
DECL|method|getCountersEnabled (JobConf conf)
specifier|public
specifier|static
name|boolean
name|getCountersEnabled
parameter_list|(
name|JobConf
name|conf
parameter_list|)
block|{
return|return
name|conf
operator|.
name|getBoolean
argument_list|(
name|COUNTERS_ENABLED
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|// instance code, to be used from Mapper/Reducer code
DECL|field|conf
specifier|private
name|JobConf
name|conf
decl_stmt|;
DECL|field|outputFormat
specifier|private
name|OutputFormat
name|outputFormat
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
argument_list|>
name|recordWriters
decl_stmt|;
DECL|field|countersEnabled
specifier|private
name|boolean
name|countersEnabled
decl_stmt|;
comment|/**    * Creates and initializes multiple named outputs support, it should be    * instantiated in the Mapper/Reducer configure method.    *    * @param job the job configuration object    */
DECL|method|MultipleOutputs (JobConf job)
specifier|public
name|MultipleOutputs
parameter_list|(
name|JobConf
name|job
parameter_list|)
block|{
name|this
operator|.
name|conf
operator|=
name|job
expr_stmt|;
name|outputFormat
operator|=
operator|new
name|InternalFileOutputFormat
argument_list|()
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
name|job
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
argument_list|>
argument_list|()
expr_stmt|;
name|countersEnabled
operator|=
name|getCountersEnabled
argument_list|(
name|job
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns iterator with the defined name outputs.    *    * @return iterator with the defined named outputs    */
DECL|method|getNamedOutputs ()
specifier|public
name|Iterator
argument_list|<
name|String
argument_list|>
name|getNamedOutputs
parameter_list|()
block|{
return|return
name|namedOutputs
operator|.
name|iterator
argument_list|()
return|;
block|}
comment|// by being synchronized MultipleOutputTask can be use with a
comment|// MultithreaderMapRunner.
DECL|method|getRecordWriter (String namedOutput, String baseFileName, final Reporter reporter)
specifier|private
specifier|synchronized
name|RecordWriter
name|getRecordWriter
parameter_list|(
name|String
name|namedOutput
parameter_list|,
name|String
name|baseFileName
parameter_list|,
specifier|final
name|Reporter
name|reporter
parameter_list|)
throws|throws
name|IOException
block|{
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
if|if
condition|(
name|writer
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|countersEnabled
operator|&&
name|reporter
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Counters are enabled, Reporter cannot be NULL"
argument_list|)
throw|;
block|}
name|JobConf
name|jobConf
init|=
operator|new
name|JobConf
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|jobConf
operator|.
name|set
argument_list|(
name|InternalFileOutputFormat
operator|.
name|CONFIG_NAMED_OUTPUT
argument_list|,
name|namedOutput
argument_list|)
expr_stmt|;
name|FileSystem
name|fs
init|=
name|FileSystem
operator|.
name|get
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|writer
operator|=
name|outputFormat
operator|.
name|getRecordWriter
argument_list|(
name|fs
argument_list|,
name|jobConf
argument_list|,
name|baseFileName
argument_list|,
name|reporter
argument_list|)
expr_stmt|;
if|if
condition|(
name|countersEnabled
condition|)
block|{
if|if
condition|(
name|reporter
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Counters are enabled, Reporter cannot be NULL"
argument_list|)
throw|;
block|}
name|writer
operator|=
operator|new
name|RecordWriterWithCounter
argument_list|(
name|writer
argument_list|,
name|baseFileName
argument_list|,
name|reporter
argument_list|)
expr_stmt|;
block|}
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
DECL|class|RecordWriterWithCounter
specifier|private
specifier|static
class|class
name|RecordWriterWithCounter
implements|implements
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
DECL|field|reporter
specifier|private
name|Reporter
name|reporter
decl_stmt|;
DECL|method|RecordWriterWithCounter (RecordWriter writer, String counterName, Reporter reporter)
specifier|public
name|RecordWriterWithCounter
parameter_list|(
name|RecordWriter
name|writer
parameter_list|,
name|String
name|counterName
parameter_list|,
name|Reporter
name|reporter
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
name|reporter
operator|=
name|reporter
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
block|{
name|reporter
operator|.
name|incrCounter
argument_list|(
name|COUNTERS_GROUP
argument_list|,
name|counterName
argument_list|,
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
DECL|method|close (Reporter reporter)
specifier|public
name|void
name|close
parameter_list|(
name|Reporter
name|reporter
parameter_list|)
throws|throws
name|IOException
block|{
name|writer
operator|.
name|close
argument_list|(
name|reporter
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Gets the output collector for a named output.    *    * @param namedOutput the named output name    * @param reporter    the reporter    * @return the output collector for the given named output    * @throws IOException thrown if output collector could not be created    */
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|}
argument_list|)
DECL|method|getCollector (String namedOutput, Reporter reporter)
specifier|public
name|OutputCollector
name|getCollector
parameter_list|(
name|String
name|namedOutput
parameter_list|,
name|Reporter
name|reporter
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getCollector
argument_list|(
name|namedOutput
argument_list|,
literal|null
argument_list|,
name|reporter
argument_list|)
return|;
block|}
comment|/**    * Gets the output collector for a multi named output.    *    * @param namedOutput the named output name    * @param multiName   the multi name part    * @param reporter    the reporter    * @return the output collector for the given named output    * @throws IOException thrown if output collector could not be created    */
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|}
argument_list|)
DECL|method|getCollector (String namedOutput, String multiName, Reporter reporter)
specifier|public
name|OutputCollector
name|getCollector
parameter_list|(
name|String
name|namedOutput
parameter_list|,
name|String
name|multiName
parameter_list|,
name|Reporter
name|reporter
parameter_list|)
throws|throws
name|IOException
block|{
name|checkNamedOutputName
argument_list|(
name|namedOutput
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
name|boolean
name|multi
init|=
name|isMultiNamedOutput
argument_list|(
name|conf
argument_list|,
name|namedOutput
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|multi
operator|&&
name|multiName
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Name output '"
operator|+
name|namedOutput
operator|+
literal|"' has not been defined as multi"
argument_list|)
throw|;
block|}
if|if
condition|(
name|multi
condition|)
block|{
name|checkTokenName
argument_list|(
name|multiName
argument_list|)
expr_stmt|;
block|}
name|String
name|baseFileName
init|=
operator|(
name|multi
operator|)
condition|?
name|namedOutput
operator|+
literal|"_"
operator|+
name|multiName
else|:
name|namedOutput
decl_stmt|;
specifier|final
name|RecordWriter
name|writer
init|=
name|getRecordWriter
argument_list|(
name|namedOutput
argument_list|,
name|baseFileName
argument_list|,
name|reporter
argument_list|)
decl_stmt|;
return|return
operator|new
name|OutputCollector
argument_list|()
block|{
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|}
argument_list|)
specifier|public
name|void
name|collect
parameter_list|(
name|Object
name|key
parameter_list|,
name|Object
name|value
parameter_list|)
throws|throws
name|IOException
block|{
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
block|}
return|;
block|}
comment|/**    * Closes all the opened named outputs.    *<p>    * If overriden subclasses must invoke<code>super.close()</code> at the    * end of their<code>close()</code>    *    * @throws java.io.IOException thrown if any of the MultipleOutput files    *                             could not be closed properly.    */
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
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
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|InternalFileOutputFormat
specifier|private
specifier|static
class|class
name|InternalFileOutputFormat
extends|extends
name|FileOutputFormat
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
block|{
DECL|field|CONFIG_NAMED_OUTPUT
specifier|public
specifier|static
specifier|final
name|String
name|CONFIG_NAMED_OUTPUT
init|=
literal|"mo.config.namedOutput"
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|}
argument_list|)
DECL|method|getRecordWriter ( FileSystem fs, JobConf job, String baseFileName, Progressable progress)
specifier|public
name|RecordWriter
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
name|getRecordWriter
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|JobConf
name|job
parameter_list|,
name|String
name|baseFileName
parameter_list|,
name|Progressable
name|progress
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|nameOutput
init|=
name|job
operator|.
name|get
argument_list|(
name|CONFIG_NAMED_OUTPUT
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|String
name|fileName
init|=
name|getUniqueName
argument_list|(
name|job
argument_list|,
name|baseFileName
argument_list|)
decl_stmt|;
comment|// The following trick leverages the instantiation of a record writer via
comment|// the job conf thus supporting arbitrary output formats.
name|JobConf
name|outputConf
init|=
operator|new
name|JobConf
argument_list|(
name|job
argument_list|)
decl_stmt|;
name|outputConf
operator|.
name|setOutputFormat
argument_list|(
name|getNamedOutputFormatClass
argument_list|(
name|job
argument_list|,
name|nameOutput
argument_list|)
argument_list|)
expr_stmt|;
name|outputConf
operator|.
name|setOutputKeyClass
argument_list|(
name|getNamedOutputKeyClass
argument_list|(
name|job
argument_list|,
name|nameOutput
argument_list|)
argument_list|)
expr_stmt|;
name|outputConf
operator|.
name|setOutputValueClass
argument_list|(
name|getNamedOutputValueClass
argument_list|(
name|job
argument_list|,
name|nameOutput
argument_list|)
argument_list|)
expr_stmt|;
name|OutputFormat
name|outputFormat
init|=
name|outputConf
operator|.
name|getOutputFormat
argument_list|()
decl_stmt|;
return|return
name|outputFormat
operator|.
name|getRecordWriter
argument_list|(
name|fs
argument_list|,
name|outputConf
argument_list|,
name|fileName
argument_list|,
name|progress
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

