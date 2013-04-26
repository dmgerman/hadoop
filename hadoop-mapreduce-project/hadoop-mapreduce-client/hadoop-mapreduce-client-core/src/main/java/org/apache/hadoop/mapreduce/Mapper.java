begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
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
name|RawComparator
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
name|compress
operator|.
name|CompressionCodec
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
name|MapContextImpl
import|;
end_import

begin_comment
comment|/**   * Maps input key/value pairs to a set of intermediate key/value pairs.    *   *<p>Maps are the individual tasks which transform input records into a   * intermediate records. The transformed intermediate records need not be of   * the same type as the input records. A given input pair may map to zero or   * many output pairs.</p>   *   *<p>The Hadoop Map-Reduce framework spawns one map task for each   * {@link InputSplit} generated by the {@link InputFormat} for the job.  *<code>Mapper</code> implementations can access the {@link Configuration} for   * the job via the {@link JobContext#getConfiguration()}.  *   *<p>The framework first calls   * {@link #setup(org.apache.hadoop.mapreduce.Mapper.Context)}, followed by  * {@link #map(Object, Object, Context)}   * for each key/value pair in the<code>InputSplit</code>. Finally   * {@link #cleanup(Context)} is called.</p>  *   *<p>All intermediate values associated with a given output key are   * subsequently grouped by the framework, and passed to a {@link Reducer} to    * determine the final output. Users can control the sorting and grouping by   * specifying two key {@link RawComparator} classes.</p>  *  *<p>The<code>Mapper</code> outputs are partitioned per   *<code>Reducer</code>. Users can control which keys (and hence records) go to   * which<code>Reducer</code> by implementing a custom {@link Partitioner}.  *   *<p>Users can optionally specify a<code>combiner</code>, via   * {@link Job#setCombinerClass(Class)}, to perform local aggregation of the   * intermediate outputs, which helps to cut down the amount of data transferred   * from the<code>Mapper</code> to the<code>Reducer</code>.  *   *<p>Applications can specify if and how the intermediate  * outputs are to be compressed and which {@link CompressionCodec}s are to be  * used via the<code>Configuration</code>.</p>  *    *<p>If the job has zero  * reduces then the output of the<code>Mapper</code> is directly written  * to the {@link OutputFormat} without sorting by keys.</p>  *   *<p>Example:</p>  *<p><blockquote><pre>  * public class TokenCounterMapper   *     extends Mapper&lt;Object, Text, Text, IntWritable&gt;{  *      *   private final static IntWritable one = new IntWritable(1);  *   private Text word = new Text();  *     *   public void map(Object key, Text value, Context context) throws IOException, InterruptedException {  *     StringTokenizer itr = new StringTokenizer(value.toString());  *     while (itr.hasMoreTokens()) {  *       word.set(itr.nextToken());  *       context.write(word, one);  *     }  *   }  * }  *</pre></blockquote></p>  *  *<p>Applications may override the {@link #run(Context)} method to exert   * greater control on map processing e.g. multi-threaded<code>Mapper</code>s   * etc.</p>  *   * @see InputFormat  * @see JobContext  * @see Partitioner    * @see Reducer  */
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
DECL|class|Mapper
specifier|public
class|class
name|Mapper
parameter_list|<
name|KEYIN
parameter_list|,
name|VALUEIN
parameter_list|,
name|KEYOUT
parameter_list|,
name|VALUEOUT
parameter_list|>
block|{
comment|/**    * The<code>Context</code> passed on to the {@link Mapper} implementations.    */
DECL|class|Context
specifier|public
specifier|abstract
class|class
name|Context
implements|implements
name|MapContext
argument_list|<
name|KEYIN
argument_list|,
name|VALUEIN
argument_list|,
name|KEYOUT
argument_list|,
name|VALUEOUT
argument_list|>
block|{   }
comment|/**    * Called once at the beginning of the task.    */
DECL|method|setup (Context context )
specifier|protected
name|void
name|setup
parameter_list|(
name|Context
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
comment|// NOTHING
block|}
comment|/**    * Called once for each key/value pair in the input split. Most applications    * should override this, but the default is the identity function.    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|map (KEYIN key, VALUEIN value, Context context)
specifier|protected
name|void
name|map
parameter_list|(
name|KEYIN
name|key
parameter_list|,
name|VALUEIN
name|value
parameter_list|,
name|Context
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|context
operator|.
name|write
argument_list|(
operator|(
name|KEYOUT
operator|)
name|key
argument_list|,
operator|(
name|VALUEOUT
operator|)
name|value
argument_list|)
expr_stmt|;
block|}
comment|/**    * Called once at the end of the task.    */
DECL|method|cleanup (Context context )
specifier|protected
name|void
name|cleanup
parameter_list|(
name|Context
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
comment|// NOTHING
block|}
comment|/**    * Expert users can override this method for more complete control over the    * execution of the Mapper.    * @param context    * @throws IOException    */
DECL|method|run (Context context)
specifier|public
name|void
name|run
parameter_list|(
name|Context
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|setup
argument_list|(
name|context
argument_list|)
expr_stmt|;
try|try
block|{
while|while
condition|(
name|context
operator|.
name|nextKeyValue
argument_list|()
condition|)
block|{
name|map
argument_list|(
name|context
operator|.
name|getCurrentKey
argument_list|()
argument_list|,
name|context
operator|.
name|getCurrentValue
argument_list|()
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|cleanup
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

