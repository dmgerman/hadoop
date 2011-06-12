begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred.lib.aggregate
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
operator|.
name|aggregate
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
name|mapred
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
name|mapred
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
name|mapred
operator|.
name|InputFormat
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
name|mapred
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
name|mapred
operator|.
name|TextInputFormat
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
name|TextOutputFormat
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
name|jobcontrol
operator|.
name|Job
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
name|jobcontrol
operator|.
name|JobControl
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
name|GenericOptionsParser
import|;
end_import

begin_comment
comment|/**  * This is the main class for creating a map/reduce job using Aggregate  * framework. The Aggregate is a specialization of map/reduce framework,  * specilizing for performing various simple aggregations.  *   * Generally speaking, in order to implement an application using Map/Reduce  * model, the developer is to implement Map and Reduce functions (and possibly  * combine function). However, a lot of applications related to counting and  * statistics computing have very similar characteristics. Aggregate abstracts  * out the general patterns of these functions and implementing those patterns.  * In particular, the package provides generic mapper/redducer/combiner classes,  * and a set of built-in value aggregators, and a generic utility class that  * helps user create map/reduce jobs using the generic class. The built-in  * aggregators include:  *   * sum over numeric values count the number of distinct values compute the  * histogram of values compute the minimum, maximum, media,average, standard  * deviation of numeric values  *   * The developer using Aggregate will need only to provide a plugin class  * conforming to the following interface:  *   * public interface ValueAggregatorDescriptor { public ArrayList<Entry>  * generateKeyValPairs(Object key, Object value); public void  * configure(JobConfjob); }  *   * The package also provides a base class, ValueAggregatorBaseDescriptor,  * implementing the above interface. The user can extend the base class and  * implement generateKeyValPairs accordingly.  *   * The primary work of generateKeyValPairs is to emit one or more key/value  * pairs based on the input key/value pair. The key in an output key/value pair  * encode two pieces of information: aggregation type and aggregation id. The  * value will be aggregated onto the aggregation id according the aggregation  * type.  *   * This class offers a function to generate a map/reduce job using Aggregate  * framework. The function takes the following parameters: input directory spec  * input format (text or sequence file) output directory a file specifying the  * user plugin class  *   * @deprecated Use   * {@link org.apache.hadoop.mapreduce.lib.aggregate.ValueAggregatorJob} instead  */
end_comment

begin_class
annotation|@
name|Deprecated
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|class|ValueAggregatorJob
specifier|public
class|class
name|ValueAggregatorJob
block|{
DECL|method|createValueAggregatorJobs (String args[] , Class<? extends ValueAggregatorDescriptor>[] descriptors)
specifier|public
specifier|static
name|JobControl
name|createValueAggregatorJobs
parameter_list|(
name|String
name|args
index|[]
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|ValueAggregatorDescriptor
argument_list|>
index|[]
name|descriptors
parameter_list|)
throws|throws
name|IOException
block|{
name|JobControl
name|theControl
init|=
operator|new
name|JobControl
argument_list|(
literal|"ValueAggregatorJobs"
argument_list|)
decl_stmt|;
name|ArrayList
argument_list|<
name|Job
argument_list|>
name|dependingJobs
init|=
operator|new
name|ArrayList
argument_list|<
name|Job
argument_list|>
argument_list|()
decl_stmt|;
name|JobConf
name|aJobConf
init|=
name|createValueAggregatorJob
argument_list|(
name|args
argument_list|)
decl_stmt|;
if|if
condition|(
name|descriptors
operator|!=
literal|null
condition|)
name|setAggregatorDescriptors
argument_list|(
name|aJobConf
argument_list|,
name|descriptors
argument_list|)
expr_stmt|;
name|Job
name|aJob
init|=
operator|new
name|Job
argument_list|(
name|aJobConf
argument_list|,
name|dependingJobs
argument_list|)
decl_stmt|;
name|theControl
operator|.
name|addJob
argument_list|(
name|aJob
argument_list|)
expr_stmt|;
return|return
name|theControl
return|;
block|}
DECL|method|createValueAggregatorJobs (String args[])
specifier|public
specifier|static
name|JobControl
name|createValueAggregatorJobs
parameter_list|(
name|String
name|args
index|[]
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|createValueAggregatorJobs
argument_list|(
name|args
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/**    * Create an Aggregate based map/reduce job.    *     * @param args the arguments used for job creation. Generic hadoop    * arguments are accepted.    * @return a JobConf object ready for submission.    *     * @throws IOException    * @see GenericOptionsParser    */
DECL|method|createValueAggregatorJob (String args[])
specifier|public
specifier|static
name|JobConf
name|createValueAggregatorJob
parameter_list|(
name|String
name|args
index|[]
parameter_list|)
throws|throws
name|IOException
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|GenericOptionsParser
name|genericParser
init|=
operator|new
name|GenericOptionsParser
argument_list|(
name|conf
argument_list|,
name|args
argument_list|)
decl_stmt|;
name|args
operator|=
name|genericParser
operator|.
name|getRemainingArgs
argument_list|()
expr_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|<
literal|2
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"usage: inputDirs outDir "
operator|+
literal|"[numOfReducer [textinputformat|seq [specfile [jobName]]]]"
argument_list|)
expr_stmt|;
name|GenericOptionsParser
operator|.
name|printGenericCommandUsage
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|String
name|inputDir
init|=
name|args
index|[
literal|0
index|]
decl_stmt|;
name|String
name|outputDir
init|=
name|args
index|[
literal|1
index|]
decl_stmt|;
name|int
name|numOfReducers
init|=
literal|1
decl_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|>
literal|2
condition|)
block|{
name|numOfReducers
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|args
index|[
literal|2
index|]
argument_list|)
expr_stmt|;
block|}
name|Class
argument_list|<
name|?
extends|extends
name|InputFormat
argument_list|>
name|theInputFormat
init|=
name|TextInputFormat
operator|.
name|class
decl_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|>
literal|3
operator|&&
name|args
index|[
literal|3
index|]
operator|.
name|compareToIgnoreCase
argument_list|(
literal|"textinputformat"
argument_list|)
operator|==
literal|0
condition|)
block|{
name|theInputFormat
operator|=
name|TextInputFormat
operator|.
name|class
expr_stmt|;
block|}
else|else
block|{
name|theInputFormat
operator|=
name|SequenceFileInputFormat
operator|.
name|class
expr_stmt|;
block|}
name|Path
name|specFile
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|>
literal|4
condition|)
block|{
name|specFile
operator|=
operator|new
name|Path
argument_list|(
name|args
index|[
literal|4
index|]
argument_list|)
expr_stmt|;
block|}
name|String
name|jobName
init|=
literal|""
decl_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|>
literal|5
condition|)
block|{
name|jobName
operator|=
name|args
index|[
literal|5
index|]
expr_stmt|;
block|}
name|JobConf
name|theJob
init|=
operator|new
name|JobConf
argument_list|(
name|conf
argument_list|)
decl_stmt|;
if|if
condition|(
name|specFile
operator|!=
literal|null
condition|)
block|{
name|theJob
operator|.
name|addResource
argument_list|(
name|specFile
argument_list|)
expr_stmt|;
block|}
name|String
name|userJarFile
init|=
name|theJob
operator|.
name|get
argument_list|(
literal|"user.jar.file"
argument_list|)
decl_stmt|;
if|if
condition|(
name|userJarFile
operator|==
literal|null
condition|)
block|{
name|theJob
operator|.
name|setJarByClass
argument_list|(
name|ValueAggregator
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|theJob
operator|.
name|setJar
argument_list|(
name|userJarFile
argument_list|)
expr_stmt|;
block|}
name|theJob
operator|.
name|setJobName
argument_list|(
literal|"ValueAggregatorJob: "
operator|+
name|jobName
argument_list|)
expr_stmt|;
name|FileInputFormat
operator|.
name|addInputPaths
argument_list|(
name|theJob
argument_list|,
name|inputDir
argument_list|)
expr_stmt|;
name|theJob
operator|.
name|setInputFormat
argument_list|(
name|theInputFormat
argument_list|)
expr_stmt|;
name|theJob
operator|.
name|setMapperClass
argument_list|(
name|ValueAggregatorMapper
operator|.
name|class
argument_list|)
expr_stmt|;
name|FileOutputFormat
operator|.
name|setOutputPath
argument_list|(
name|theJob
argument_list|,
operator|new
name|Path
argument_list|(
name|outputDir
argument_list|)
argument_list|)
expr_stmt|;
name|theJob
operator|.
name|setOutputFormat
argument_list|(
name|TextOutputFormat
operator|.
name|class
argument_list|)
expr_stmt|;
name|theJob
operator|.
name|setMapOutputKeyClass
argument_list|(
name|Text
operator|.
name|class
argument_list|)
expr_stmt|;
name|theJob
operator|.
name|setMapOutputValueClass
argument_list|(
name|Text
operator|.
name|class
argument_list|)
expr_stmt|;
name|theJob
operator|.
name|setOutputKeyClass
argument_list|(
name|Text
operator|.
name|class
argument_list|)
expr_stmt|;
name|theJob
operator|.
name|setOutputValueClass
argument_list|(
name|Text
operator|.
name|class
argument_list|)
expr_stmt|;
name|theJob
operator|.
name|setReducerClass
argument_list|(
name|ValueAggregatorReducer
operator|.
name|class
argument_list|)
expr_stmt|;
name|theJob
operator|.
name|setCombinerClass
argument_list|(
name|ValueAggregatorCombiner
operator|.
name|class
argument_list|)
expr_stmt|;
name|theJob
operator|.
name|setNumMapTasks
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|theJob
operator|.
name|setNumReduceTasks
argument_list|(
name|numOfReducers
argument_list|)
expr_stmt|;
return|return
name|theJob
return|;
block|}
DECL|method|createValueAggregatorJob (String args[] , Class<? extends ValueAggregatorDescriptor>[] descriptors)
specifier|public
specifier|static
name|JobConf
name|createValueAggregatorJob
parameter_list|(
name|String
name|args
index|[]
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|ValueAggregatorDescriptor
argument_list|>
index|[]
name|descriptors
parameter_list|)
throws|throws
name|IOException
block|{
name|JobConf
name|job
init|=
name|createValueAggregatorJob
argument_list|(
name|args
argument_list|)
decl_stmt|;
name|setAggregatorDescriptors
argument_list|(
name|job
argument_list|,
name|descriptors
argument_list|)
expr_stmt|;
return|return
name|job
return|;
block|}
DECL|method|setAggregatorDescriptors (JobConf job , Class<? extends ValueAggregatorDescriptor>[] descriptors)
specifier|public
specifier|static
name|void
name|setAggregatorDescriptors
parameter_list|(
name|JobConf
name|job
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|ValueAggregatorDescriptor
argument_list|>
index|[]
name|descriptors
parameter_list|)
block|{
name|job
operator|.
name|setInt
argument_list|(
literal|"aggregator.descriptor.num"
argument_list|,
name|descriptors
operator|.
name|length
argument_list|)
expr_stmt|;
comment|//specify the aggregator descriptors
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|descriptors
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|job
operator|.
name|set
argument_list|(
literal|"aggregator.descriptor."
operator|+
name|i
argument_list|,
literal|"UserDefined,"
operator|+
name|descriptors
index|[
name|i
index|]
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * create and run an Aggregate based map/reduce job.    *     * @param args the arguments used for job creation    * @throws IOException    */
DECL|method|main (String args[])
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
name|args
index|[]
parameter_list|)
throws|throws
name|IOException
block|{
name|JobConf
name|job
init|=
name|ValueAggregatorJob
operator|.
name|createValueAggregatorJob
argument_list|(
name|args
argument_list|)
decl_stmt|;
name|JobClient
operator|.
name|runJob
argument_list|(
name|job
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

