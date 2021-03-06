begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred.pipes
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|pipes
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
name|io
operator|.
name|FloatWritable
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
name|NullWritable
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
name|MapRunner
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
name|OutputCollector
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
name|RecordReader
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
name|Reporter
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
name|SkipBadRecords
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

begin_comment
comment|/**  * An adaptor to run a C++ mapper.  */
end_comment

begin_class
DECL|class|PipesMapRunner
class|class
name|PipesMapRunner
parameter_list|<
name|K1
extends|extends
name|WritableComparable
parameter_list|,
name|V1
extends|extends
name|Writable
parameter_list|,
name|K2
extends|extends
name|WritableComparable
parameter_list|,
name|V2
extends|extends
name|Writable
parameter_list|>
extends|extends
name|MapRunner
argument_list|<
name|K1
argument_list|,
name|V1
argument_list|,
name|K2
argument_list|,
name|V2
argument_list|>
block|{
DECL|field|job
specifier|private
name|JobConf
name|job
decl_stmt|;
comment|/**    * Get the new configuration.    * @param job the job's configuration    */
DECL|method|configure (JobConf job)
specifier|public
name|void
name|configure
parameter_list|(
name|JobConf
name|job
parameter_list|)
block|{
name|this
operator|.
name|job
operator|=
name|job
expr_stmt|;
comment|//disable the auto increment of the counter. For pipes, no of processed
comment|//records could be different(equal or less) than the no of records input.
name|SkipBadRecords
operator|.
name|setAutoIncrMapperProcCount
argument_list|(
name|job
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * Run the map task.    * @param input the set of inputs    * @param output the object to collect the outputs of the map    * @param reporter the object to update with status    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|run (RecordReader<K1, V1> input, OutputCollector<K2, V2> output, Reporter reporter)
specifier|public
name|void
name|run
parameter_list|(
name|RecordReader
argument_list|<
name|K1
argument_list|,
name|V1
argument_list|>
name|input
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
block|{
name|Application
argument_list|<
name|K1
argument_list|,
name|V1
argument_list|,
name|K2
argument_list|,
name|V2
argument_list|>
name|application
init|=
literal|null
decl_stmt|;
try|try
block|{
name|RecordReader
argument_list|<
name|FloatWritable
argument_list|,
name|NullWritable
argument_list|>
name|fakeInput
init|=
operator|(
operator|!
name|Submitter
operator|.
name|getIsJavaRecordReader
argument_list|(
name|job
argument_list|)
operator|&&
operator|!
name|Submitter
operator|.
name|getIsJavaMapper
argument_list|(
name|job
argument_list|)
operator|)
condition|?
operator|(
name|RecordReader
argument_list|<
name|FloatWritable
argument_list|,
name|NullWritable
argument_list|>
operator|)
name|input
else|:
literal|null
decl_stmt|;
name|application
operator|=
operator|new
name|Application
argument_list|<
name|K1
argument_list|,
name|V1
argument_list|,
name|K2
argument_list|,
name|V2
argument_list|>
argument_list|(
name|job
argument_list|,
name|fakeInput
argument_list|,
name|output
argument_list|,
name|reporter
argument_list|,
operator|(
name|Class
argument_list|<
name|?
extends|extends
name|K2
argument_list|>
operator|)
name|job
operator|.
name|getOutputKeyClass
argument_list|()
argument_list|,
operator|(
name|Class
argument_list|<
name|?
extends|extends
name|V2
argument_list|>
operator|)
name|job
operator|.
name|getOutputValueClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"interrupted"
argument_list|,
name|ie
argument_list|)
throw|;
block|}
name|DownwardProtocol
argument_list|<
name|K1
argument_list|,
name|V1
argument_list|>
name|downlink
init|=
name|application
operator|.
name|getDownlink
argument_list|()
decl_stmt|;
name|boolean
name|isJavaInput
init|=
name|Submitter
operator|.
name|getIsJavaRecordReader
argument_list|(
name|job
argument_list|)
decl_stmt|;
name|downlink
operator|.
name|runMap
argument_list|(
name|reporter
operator|.
name|getInputSplit
argument_list|()
argument_list|,
name|job
operator|.
name|getNumReduceTasks
argument_list|()
argument_list|,
name|isJavaInput
argument_list|)
expr_stmt|;
name|boolean
name|skipping
init|=
name|job
operator|.
name|getBoolean
argument_list|(
name|MRJobConfig
operator|.
name|SKIP_RECORDS
argument_list|,
literal|false
argument_list|)
decl_stmt|;
try|try
block|{
if|if
condition|(
name|isJavaInput
condition|)
block|{
comment|// allocate key& value instances that are re-used for all entries
name|K1
name|key
init|=
name|input
operator|.
name|createKey
argument_list|()
decl_stmt|;
name|V1
name|value
init|=
name|input
operator|.
name|createValue
argument_list|()
decl_stmt|;
name|downlink
operator|.
name|setInputTypes
argument_list|(
name|key
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|value
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
while|while
condition|(
name|input
operator|.
name|next
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
condition|)
block|{
comment|// map pair to output
name|downlink
operator|.
name|mapItem
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
if|if
condition|(
name|skipping
condition|)
block|{
comment|//flush the streams on every record input if running in skip mode
comment|//so that we don't buffer other records surrounding a bad record.
name|downlink
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
block|}
name|downlink
operator|.
name|endOfInput
argument_list|()
expr_stmt|;
block|}
name|application
operator|.
name|waitForFinish
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|application
operator|.
name|abort
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|application
operator|.
name|cleanup
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

