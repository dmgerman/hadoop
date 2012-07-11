begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
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
name|io
operator|.
name|Closeable
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
name|SequenceFile
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

begin_comment
comment|/**   * Maps input key/value pairs to a set of intermediate key/value pairs.    *   *<p>Maps are the individual tasks which transform input records into a   * intermediate records. The transformed intermediate records need not be of   * the same type as the input records. A given input pair may map to zero or   * many output pairs.</p>   *   *<p>The Hadoop Map-Reduce framework spawns one map task for each   * {@link InputSplit} generated by the {@link InputFormat} for the job.  *<code>Mapper</code> implementations can access the {@link JobConf} for the   * job via the {@link JobConfigurable#configure(JobConf)} and initialize  * themselves. Similarly they can use the {@link Closeable#close()} method for  * de-initialization.</p>  *   *<p>The framework then calls   * {@link #map(Object, Object, OutputCollector, Reporter)}   * for each key/value pair in the<code>InputSplit</code> for that task.</p>  *   *<p>All intermediate values associated with a given output key are   * subsequently grouped by the framework, and passed to a {@link Reducer} to    * determine the final output. Users can control the grouping by specifying  * a<code>Comparator</code> via   * {@link JobConf#setOutputKeyComparatorClass(Class)}.</p>  *  *<p>The grouped<code>Mapper</code> outputs are partitioned per   *<code>Reducer</code>. Users can control which keys (and hence records) go to   * which<code>Reducer</code> by implementing a custom {@link Partitioner}.  *   *<p>Users can optionally specify a<code>combiner</code>, via   * {@link JobConf#setCombinerClass(Class)}, to perform local aggregation of the   * intermediate outputs, which helps to cut down the amount of data transferred   * from the<code>Mapper</code> to the<code>Reducer</code>.  *   *<p>The intermediate, grouped outputs are always stored in   * {@link SequenceFile}s. Applications can specify if and how the intermediate  * outputs are to be compressed and which {@link CompressionCodec}s are to be  * used via the<code>JobConf</code>.</p>  *    *<p>If the job has   *<a href="{@docRoot}/org/apache/hadoop/mapred/JobConf.html#ReducerNone">zero  * reduces</a> then the output of the<code>Mapper</code> is directly written  * to the {@link FileSystem} without grouping by keys.</p>  *   *<p>Example:</p>  *<p><blockquote><pre>  *     public class MyMapper&lt;K extends WritableComparable, V extends Writable&gt;   *     extends MapReduceBase implements Mapper&lt;K, V, K, V&gt; {  *       *       static enum MyCounters { NUM_RECORDS }  *         *       private String mapTaskId;  *       private String inputFile;  *       private int noRecords = 0;  *         *       public void configure(JobConf job) {  *         mapTaskId = job.get(JobContext.TASK_ATTEMPT_ID);  *         inputFile = job.get(JobContext.MAP_INPUT_FILE);  *       }  *         *       public void map(K key, V val,  *                       OutputCollector&lt;K, V&gt; output, Reporter reporter)  *       throws IOException {  *         // Process the&lt;key, value&gt; pair (assume this takes a while)  *         // ...  *         // ...  *           *         // Let the framework know that we are alive, and kicking!  *         // reporter.progress();  *           *         // Process some more  *         // ...  *         // ...  *           *         // Increment the no. of&lt;key, value&gt; pairs processed  *         ++noRecords;  *  *         // Increment counters  *         reporter.incrCounter(NUM_RECORDS, 1);  *          *         // Every 100 records update application-level status  *         if ((noRecords%100) == 0) {  *           reporter.setStatus(mapTaskId + " processed " + noRecords +   *                              " from input-file: " + inputFile);   *         }  *           *         // Output the result  *         output.collect(key, val);  *       }  *     }  *</pre></blockquote></p>  *  *<p>Applications may write a custom {@link MapRunnable} to exert greater  * control on map processing e.g. multi-threaded<code>Mapper</code>s etc.</p>  *   * @see JobConf  * @see InputFormat  * @see Partitioner    * @see Reducer  * @see MapReduceBase  * @see MapRunnable  * @see SequenceFile  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|interface|Mapper
specifier|public
interface|interface
name|Mapper
parameter_list|<
name|K1
parameter_list|,
name|V1
parameter_list|,
name|K2
parameter_list|,
name|V2
parameter_list|>
extends|extends
name|JobConfigurable
extends|,
name|Closeable
block|{
comment|/**     * Maps a single input key/value pair into an intermediate key/value pair.    *     *<p>Output pairs need not be of the same types as input pairs.  A given     * input pair may map to zero or many output pairs.  Output pairs are     * collected with calls to     * {@link OutputCollector#collect(Object,Object)}.</p>    *    *<p>Applications can use the {@link Reporter} provided to report progress     * or just indicate that they are alive. In scenarios where the application     * takes significant amount of time to process individual key/value    * pairs, this is crucial since the framework might assume that the task has     * timed-out and kill that task. The other way of avoiding this is to set     *<a href="{@docRoot}/../mapred-default.html#mapreduce.task.timeout">    * mapreduce.task.timeout</a> to a high-enough value (or even zero for no     * time-outs).</p>    *     * @param key the input key.    * @param value the input value.    * @param output collects mapped keys and values.    * @param reporter facility to report progress.    */
DECL|method|map (K1 key, V1 value, OutputCollector<K2, V2> output, Reporter reporter)
name|void
name|map
parameter_list|(
name|K1
name|key
parameter_list|,
name|V1
name|value
parameter_list|,
name|OutputCollector
argument_list|<
name|K2
argument_list|,
name|V2
argument_list|>
name|output
parameter_list|,
name|Reporter
name|reporter
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

