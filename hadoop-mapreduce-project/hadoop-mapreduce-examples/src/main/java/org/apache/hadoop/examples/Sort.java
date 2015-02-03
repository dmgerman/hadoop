begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.examples
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|examples
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
name|net
operator|.
name|URI
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
name|conf
operator|.
name|Configured
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
name|filecache
operator|.
name|DistributedCache
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
name|Path
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
name|BytesWritable
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
name|Writable
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
name|WritableComparable
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
name|ClusterStatus
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
name|JobClient
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
name|lib
operator|.
name|input
operator|.
name|FileInputFormat
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
name|input
operator|.
name|SequenceFileInputFormat
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
name|FileOutputFormat
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
name|SequenceFileOutputFormat
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
name|partition
operator|.
name|InputSampler
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
name|partition
operator|.
name|TotalOrderPartitioner
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
name|Tool
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
name|ToolRunner
import|;
end_import

begin_comment
comment|/**  * This is the trivial map/reduce program that does absolutely nothing  * other than use the framework to fragment and sort the input values.  *  * To run: bin/hadoop jar build/hadoop-examples.jar sort  *            [-r<i>reduces</i>]  *            [-inFormat<i>input format class</i>]   *            [-outFormat<i>output format class</i>]   *            [-outKey<i>output key class</i>]   *            [-outValue<i>output value class</i>]   *            [-totalOrder<i>pcnt</i><i>num samples</i><i>max splits</i>]  *<i>in-dir</i><i>out-dir</i>   */
end_comment

begin_class
DECL|class|Sort
specifier|public
class|class
name|Sort
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
extends|extends
name|Configured
implements|implements
name|Tool
block|{
DECL|field|REDUCES_PER_HOST
specifier|public
specifier|static
specifier|final
name|String
name|REDUCES_PER_HOST
init|=
literal|"mapreduce.sort.reducesperhost"
decl_stmt|;
DECL|field|job
specifier|private
name|Job
name|job
init|=
literal|null
decl_stmt|;
DECL|method|printUsage ()
specifier|static
name|int
name|printUsage
parameter_list|()
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"sort [-r<reduces>] "
operator|+
literal|"[-inFormat<input format class>] "
operator|+
literal|"[-outFormat<output format class>] "
operator|+
literal|"[-outKey<output key class>] "
operator|+
literal|"[-outValue<output value class>] "
operator|+
literal|"[-totalOrder<pcnt><num samples><max splits>] "
operator|+
literal|"<input><output>"
argument_list|)
expr_stmt|;
name|ToolRunner
operator|.
name|printGenericCommandUsage
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
return|return
literal|2
return|;
block|}
comment|/**    * The main driver for sort program.    * Invoke this method to submit the map/reduce job.    * @throws IOException When there is communication problems with the     *                     job tracker.    */
DECL|method|run (String[] args)
specifier|public
name|int
name|run
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
name|getConf
argument_list|()
decl_stmt|;
name|JobClient
name|client
init|=
operator|new
name|JobClient
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|ClusterStatus
name|cluster
init|=
name|client
operator|.
name|getClusterStatus
argument_list|()
decl_stmt|;
name|int
name|num_reduces
init|=
call|(
name|int
call|)
argument_list|(
name|cluster
operator|.
name|getMaxReduceTasks
argument_list|()
operator|*
literal|0.9
argument_list|)
decl_stmt|;
name|String
name|sort_reduces
init|=
name|conf
operator|.
name|get
argument_list|(
name|REDUCES_PER_HOST
argument_list|)
decl_stmt|;
if|if
condition|(
name|sort_reduces
operator|!=
literal|null
condition|)
block|{
name|num_reduces
operator|=
name|cluster
operator|.
name|getTaskTrackers
argument_list|()
operator|*
name|Integer
operator|.
name|parseInt
argument_list|(
name|sort_reduces
argument_list|)
expr_stmt|;
block|}
name|Class
argument_list|<
name|?
extends|extends
name|InputFormat
argument_list|>
name|inputFormatClass
init|=
name|SequenceFileInputFormat
operator|.
name|class
decl_stmt|;
name|Class
argument_list|<
name|?
extends|extends
name|OutputFormat
argument_list|>
name|outputFormatClass
init|=
name|SequenceFileOutputFormat
operator|.
name|class
decl_stmt|;
name|Class
argument_list|<
name|?
extends|extends
name|WritableComparable
argument_list|>
name|outputKeyClass
init|=
name|BytesWritable
operator|.
name|class
decl_stmt|;
name|Class
argument_list|<
name|?
extends|extends
name|Writable
argument_list|>
name|outputValueClass
init|=
name|BytesWritable
operator|.
name|class
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|otherArgs
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|InputSampler
operator|.
name|Sampler
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|sampler
init|=
literal|null
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|args
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
try|try
block|{
if|if
condition|(
literal|"-r"
operator|.
name|equals
argument_list|(
name|args
index|[
name|i
index|]
argument_list|)
condition|)
block|{
name|num_reduces
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|args
index|[
operator|++
name|i
index|]
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"-inFormat"
operator|.
name|equals
argument_list|(
name|args
index|[
name|i
index|]
argument_list|)
condition|)
block|{
name|inputFormatClass
operator|=
name|Class
operator|.
name|forName
argument_list|(
name|args
index|[
operator|++
name|i
index|]
argument_list|)
operator|.
name|asSubclass
argument_list|(
name|InputFormat
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"-outFormat"
operator|.
name|equals
argument_list|(
name|args
index|[
name|i
index|]
argument_list|)
condition|)
block|{
name|outputFormatClass
operator|=
name|Class
operator|.
name|forName
argument_list|(
name|args
index|[
operator|++
name|i
index|]
argument_list|)
operator|.
name|asSubclass
argument_list|(
name|OutputFormat
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"-outKey"
operator|.
name|equals
argument_list|(
name|args
index|[
name|i
index|]
argument_list|)
condition|)
block|{
name|outputKeyClass
operator|=
name|Class
operator|.
name|forName
argument_list|(
name|args
index|[
operator|++
name|i
index|]
argument_list|)
operator|.
name|asSubclass
argument_list|(
name|WritableComparable
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"-outValue"
operator|.
name|equals
argument_list|(
name|args
index|[
name|i
index|]
argument_list|)
condition|)
block|{
name|outputValueClass
operator|=
name|Class
operator|.
name|forName
argument_list|(
name|args
index|[
operator|++
name|i
index|]
argument_list|)
operator|.
name|asSubclass
argument_list|(
name|Writable
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"-totalOrder"
operator|.
name|equals
argument_list|(
name|args
index|[
name|i
index|]
argument_list|)
condition|)
block|{
name|double
name|pcnt
init|=
name|Double
operator|.
name|parseDouble
argument_list|(
name|args
index|[
operator|++
name|i
index|]
argument_list|)
decl_stmt|;
name|int
name|numSamples
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|args
index|[
operator|++
name|i
index|]
argument_list|)
decl_stmt|;
name|int
name|maxSplits
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|args
index|[
operator|++
name|i
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
literal|0
operator|>=
name|maxSplits
condition|)
name|maxSplits
operator|=
name|Integer
operator|.
name|MAX_VALUE
expr_stmt|;
name|sampler
operator|=
operator|new
name|InputSampler
operator|.
name|RandomSampler
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
argument_list|(
name|pcnt
argument_list|,
name|numSamples
argument_list|,
name|maxSplits
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|otherArgs
operator|.
name|add
argument_list|(
name|args
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|except
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"ERROR: Integer expected instead of "
operator|+
name|args
index|[
name|i
index|]
argument_list|)
expr_stmt|;
return|return
name|printUsage
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|ArrayIndexOutOfBoundsException
name|except
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"ERROR: Required parameter missing from "
operator|+
name|args
index|[
name|i
operator|-
literal|1
index|]
argument_list|)
expr_stmt|;
return|return
name|printUsage
argument_list|()
return|;
comment|// exits
block|}
block|}
comment|// Set user-supplied (possibly default) job configs
name|job
operator|=
name|Job
operator|.
name|getInstance
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|job
operator|.
name|setJobName
argument_list|(
literal|"sorter"
argument_list|)
expr_stmt|;
name|job
operator|.
name|setJarByClass
argument_list|(
name|Sort
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setMapperClass
argument_list|(
name|Mapper
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setReducerClass
argument_list|(
name|Reducer
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setNumReduceTasks
argument_list|(
name|num_reduces
argument_list|)
expr_stmt|;
name|job
operator|.
name|setInputFormatClass
argument_list|(
name|inputFormatClass
argument_list|)
expr_stmt|;
name|job
operator|.
name|setOutputFormatClass
argument_list|(
name|outputFormatClass
argument_list|)
expr_stmt|;
name|job
operator|.
name|setOutputKeyClass
argument_list|(
name|outputKeyClass
argument_list|)
expr_stmt|;
name|job
operator|.
name|setOutputValueClass
argument_list|(
name|outputValueClass
argument_list|)
expr_stmt|;
comment|// Make sure there are exactly 2 parameters left.
if|if
condition|(
name|otherArgs
operator|.
name|size
argument_list|()
operator|!=
literal|2
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"ERROR: Wrong number of parameters: "
operator|+
name|otherArgs
operator|.
name|size
argument_list|()
operator|+
literal|" instead of 2."
argument_list|)
expr_stmt|;
return|return
name|printUsage
argument_list|()
return|;
block|}
name|FileInputFormat
operator|.
name|setInputPaths
argument_list|(
name|job
argument_list|,
name|otherArgs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|FileOutputFormat
operator|.
name|setOutputPath
argument_list|(
name|job
argument_list|,
operator|new
name|Path
argument_list|(
name|otherArgs
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|sampler
operator|!=
literal|null
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Sampling input to effect total-order sort..."
argument_list|)
expr_stmt|;
name|job
operator|.
name|setPartitionerClass
argument_list|(
name|TotalOrderPartitioner
operator|.
name|class
argument_list|)
expr_stmt|;
name|Path
name|inputDir
init|=
name|FileInputFormat
operator|.
name|getInputPaths
argument_list|(
name|job
argument_list|)
index|[
literal|0
index|]
decl_stmt|;
name|inputDir
operator|=
name|inputDir
operator|.
name|makeQualified
argument_list|(
name|inputDir
operator|.
name|getFileSystem
argument_list|(
name|conf
argument_list|)
argument_list|)
expr_stmt|;
name|Path
name|partitionFile
init|=
operator|new
name|Path
argument_list|(
name|inputDir
argument_list|,
literal|"_sortPartitioning"
argument_list|)
decl_stmt|;
name|TotalOrderPartitioner
operator|.
name|setPartitionFile
argument_list|(
name|conf
argument_list|,
name|partitionFile
argument_list|)
expr_stmt|;
name|InputSampler
operator|.
expr|<
name|K
operator|,
name|V
operator|>
name|writePartitionFile
argument_list|(
name|job
argument_list|,
name|sampler
argument_list|)
expr_stmt|;
name|URI
name|partitionUri
init|=
operator|new
name|URI
argument_list|(
name|partitionFile
operator|.
name|toString
argument_list|()
operator|+
literal|"#"
operator|+
literal|"_sortPartitioning"
argument_list|)
decl_stmt|;
name|DistributedCache
operator|.
name|addCacheFile
argument_list|(
name|partitionUri
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Running on "
operator|+
name|cluster
operator|.
name|getTaskTrackers
argument_list|()
operator|+
literal|" nodes to sort from "
operator|+
name|FileInputFormat
operator|.
name|getInputPaths
argument_list|(
name|job
argument_list|)
index|[
literal|0
index|]
operator|+
literal|" into "
operator|+
name|FileOutputFormat
operator|.
name|getOutputPath
argument_list|(
name|job
argument_list|)
operator|+
literal|" with "
operator|+
name|num_reduces
operator|+
literal|" reduces."
argument_list|)
expr_stmt|;
name|Date
name|startTime
init|=
operator|new
name|Date
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Job started: "
operator|+
name|startTime
argument_list|)
expr_stmt|;
name|int
name|ret
init|=
name|job
operator|.
name|waitForCompletion
argument_list|(
literal|true
argument_list|)
condition|?
literal|0
else|:
literal|1
decl_stmt|;
name|Date
name|end_time
init|=
operator|new
name|Date
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Job ended: "
operator|+
name|end_time
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"The job took "
operator|+
operator|(
name|end_time
operator|.
name|getTime
argument_list|()
operator|-
name|startTime
operator|.
name|getTime
argument_list|()
operator|)
operator|/
literal|1000
operator|+
literal|" seconds."
argument_list|)
expr_stmt|;
return|return
name|ret
return|;
block|}
DECL|method|main (String[] args)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|int
name|res
init|=
name|ToolRunner
operator|.
name|run
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|,
operator|new
name|Sort
argument_list|()
argument_list|,
name|args
argument_list|)
decl_stmt|;
name|System
operator|.
name|exit
argument_list|(
name|res
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get the last job that was run using this instance.    * @return the results of the last job that was run    */
DECL|method|getResult ()
specifier|public
name|Job
name|getResult
parameter_list|()
block|{
return|return
name|job
return|;
block|}
block|}
end_class

end_unit

