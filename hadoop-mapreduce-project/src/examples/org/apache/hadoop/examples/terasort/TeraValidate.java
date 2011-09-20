begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.examples.terasort
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|examples
operator|.
name|terasort
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
name|zip
operator|.
name|Checksum
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
name|Cluster
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
name|mapreduce
operator|.
name|Mapper
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
name|FileSplit
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
name|util
operator|.
name|PureJavaCrc32
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
comment|/**  * Generate 1 mapper per a file that checks to make sure the keys  * are sorted within each file. The mapper also generates   * "$file:begin", first key and "$file:end", last key. The reduce verifies that  * all of the start/end items are in order.  * Any output from the reduce is problem report.  *<p>  * To run the program:   *<b>bin/hadoop jar hadoop-*-examples.jar teravalidate out-dir report-dir</b>  *<p>  * If there is any output, something is wrong and the output of the reduce  * will have the problem report.  */
end_comment

begin_class
DECL|class|TeraValidate
specifier|public
class|class
name|TeraValidate
extends|extends
name|Configured
implements|implements
name|Tool
block|{
DECL|field|ERROR
specifier|private
specifier|static
specifier|final
name|Text
name|ERROR
init|=
operator|new
name|Text
argument_list|(
literal|"error"
argument_list|)
decl_stmt|;
DECL|field|CHECKSUM
specifier|private
specifier|static
specifier|final
name|Text
name|CHECKSUM
init|=
operator|new
name|Text
argument_list|(
literal|"checksum"
argument_list|)
decl_stmt|;
DECL|method|textifyBytes (Text t)
specifier|private
specifier|static
name|String
name|textifyBytes
parameter_list|(
name|Text
name|t
parameter_list|)
block|{
name|BytesWritable
name|b
init|=
operator|new
name|BytesWritable
argument_list|()
decl_stmt|;
name|b
operator|.
name|set
argument_list|(
name|t
operator|.
name|getBytes
argument_list|()
argument_list|,
literal|0
argument_list|,
name|t
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|b
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|class|ValidateMapper
specifier|static
class|class
name|ValidateMapper
extends|extends
name|Mapper
argument_list|<
name|Text
argument_list|,
name|Text
argument_list|,
name|Text
argument_list|,
name|Text
argument_list|>
block|{
DECL|field|lastKey
specifier|private
name|Text
name|lastKey
decl_stmt|;
DECL|field|filename
specifier|private
name|String
name|filename
decl_stmt|;
DECL|field|checksum
specifier|private
name|Unsigned16
name|checksum
init|=
operator|new
name|Unsigned16
argument_list|()
decl_stmt|;
DECL|field|tmp
specifier|private
name|Unsigned16
name|tmp
init|=
operator|new
name|Unsigned16
argument_list|()
decl_stmt|;
DECL|field|crc32
specifier|private
name|Checksum
name|crc32
init|=
operator|new
name|PureJavaCrc32
argument_list|()
decl_stmt|;
comment|/**      * Get the final part of the input name      * @param split the input split      * @return the "part-r-00000" for the input      */
DECL|method|getFilename (FileSplit split)
specifier|private
name|String
name|getFilename
parameter_list|(
name|FileSplit
name|split
parameter_list|)
block|{
return|return
name|split
operator|.
name|getPath
argument_list|()
operator|.
name|getName
argument_list|()
return|;
block|}
DECL|method|map (Text key, Text value, Context context)
specifier|public
name|void
name|map
parameter_list|(
name|Text
name|key
parameter_list|,
name|Text
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
if|if
condition|(
name|lastKey
operator|==
literal|null
condition|)
block|{
name|FileSplit
name|fs
init|=
operator|(
name|FileSplit
operator|)
name|context
operator|.
name|getInputSplit
argument_list|()
decl_stmt|;
name|filename
operator|=
name|getFilename
argument_list|(
name|fs
argument_list|)
expr_stmt|;
name|context
operator|.
name|write
argument_list|(
operator|new
name|Text
argument_list|(
name|filename
operator|+
literal|":begin"
argument_list|)
argument_list|,
name|key
argument_list|)
expr_stmt|;
name|lastKey
operator|=
operator|new
name|Text
argument_list|()
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|key
operator|.
name|compareTo
argument_list|(
name|lastKey
argument_list|)
operator|<
literal|0
condition|)
block|{
name|context
operator|.
name|write
argument_list|(
name|ERROR
argument_list|,
operator|new
name|Text
argument_list|(
literal|"misorder in "
operator|+
name|filename
operator|+
literal|" between "
operator|+
name|textifyBytes
argument_list|(
name|lastKey
argument_list|)
operator|+
literal|" and "
operator|+
name|textifyBytes
argument_list|(
name|key
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// compute the crc of the key and value and add it to the sum
name|crc32
operator|.
name|reset
argument_list|()
expr_stmt|;
name|crc32
operator|.
name|update
argument_list|(
name|key
operator|.
name|getBytes
argument_list|()
argument_list|,
literal|0
argument_list|,
name|key
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|crc32
operator|.
name|update
argument_list|(
name|value
operator|.
name|getBytes
argument_list|()
argument_list|,
literal|0
argument_list|,
name|value
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|tmp
operator|.
name|set
argument_list|(
name|crc32
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|checksum
operator|.
name|add
argument_list|(
name|tmp
argument_list|)
expr_stmt|;
name|lastKey
operator|.
name|set
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
DECL|method|cleanup (Context context)
specifier|public
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
if|if
condition|(
name|lastKey
operator|!=
literal|null
condition|)
block|{
name|context
operator|.
name|write
argument_list|(
operator|new
name|Text
argument_list|(
name|filename
operator|+
literal|":end"
argument_list|)
argument_list|,
name|lastKey
argument_list|)
expr_stmt|;
name|context
operator|.
name|write
argument_list|(
name|CHECKSUM
argument_list|,
operator|new
name|Text
argument_list|(
name|checksum
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Check the boundaries between the output files by making sure that the    * boundary keys are always increasing.    * Also passes any error reports along intact.    */
DECL|class|ValidateReducer
specifier|static
class|class
name|ValidateReducer
extends|extends
name|Reducer
argument_list|<
name|Text
argument_list|,
name|Text
argument_list|,
name|Text
argument_list|,
name|Text
argument_list|>
block|{
DECL|field|firstKey
specifier|private
name|boolean
name|firstKey
init|=
literal|true
decl_stmt|;
DECL|field|lastKey
specifier|private
name|Text
name|lastKey
init|=
operator|new
name|Text
argument_list|()
decl_stmt|;
DECL|field|lastValue
specifier|private
name|Text
name|lastValue
init|=
operator|new
name|Text
argument_list|()
decl_stmt|;
DECL|method|reduce (Text key, Iterable<Text> values, Context context)
specifier|public
name|void
name|reduce
parameter_list|(
name|Text
name|key
parameter_list|,
name|Iterable
argument_list|<
name|Text
argument_list|>
name|values
parameter_list|,
name|Context
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
if|if
condition|(
name|ERROR
operator|.
name|equals
argument_list|(
name|key
argument_list|)
condition|)
block|{
for|for
control|(
name|Text
name|val
range|:
name|values
control|)
block|{
name|context
operator|.
name|write
argument_list|(
name|key
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|CHECKSUM
operator|.
name|equals
argument_list|(
name|key
argument_list|)
condition|)
block|{
name|Unsigned16
name|tmp
init|=
operator|new
name|Unsigned16
argument_list|()
decl_stmt|;
name|Unsigned16
name|sum
init|=
operator|new
name|Unsigned16
argument_list|()
decl_stmt|;
for|for
control|(
name|Text
name|val
range|:
name|values
control|)
block|{
name|tmp
operator|.
name|set
argument_list|(
name|val
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|sum
operator|.
name|add
argument_list|(
name|tmp
argument_list|)
expr_stmt|;
block|}
name|context
operator|.
name|write
argument_list|(
name|CHECKSUM
argument_list|,
operator|new
name|Text
argument_list|(
name|sum
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Text
name|value
init|=
name|values
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|firstKey
condition|)
block|{
name|firstKey
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|value
operator|.
name|compareTo
argument_list|(
name|lastValue
argument_list|)
operator|<
literal|0
condition|)
block|{
name|context
operator|.
name|write
argument_list|(
name|ERROR
argument_list|,
operator|new
name|Text
argument_list|(
literal|"bad key partitioning:\n  file "
operator|+
name|lastKey
operator|+
literal|" key "
operator|+
name|textifyBytes
argument_list|(
name|lastValue
argument_list|)
operator|+
literal|"\n  file "
operator|+
name|key
operator|+
literal|" key "
operator|+
name|textifyBytes
argument_list|(
name|value
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|lastKey
operator|.
name|set
argument_list|(
name|key
argument_list|)
expr_stmt|;
name|lastValue
operator|.
name|set
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|usage ()
specifier|private
specifier|static
name|void
name|usage
parameter_list|()
throws|throws
name|IOException
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"teravalidate<out-dir><report-dir>"
argument_list|)
expr_stmt|;
block|}
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
name|Job
name|job
init|=
name|Job
operator|.
name|getInstance
argument_list|(
name|getConf
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|!=
literal|2
condition|)
block|{
name|usage
argument_list|()
expr_stmt|;
return|return
literal|1
return|;
block|}
name|TeraInputFormat
operator|.
name|setInputPaths
argument_list|(
name|job
argument_list|,
operator|new
name|Path
argument_list|(
name|args
index|[
literal|0
index|]
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
name|args
index|[
literal|1
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|job
operator|.
name|setJobName
argument_list|(
literal|"TeraValidate"
argument_list|)
expr_stmt|;
name|job
operator|.
name|setJarByClass
argument_list|(
name|TeraValidate
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setMapperClass
argument_list|(
name|ValidateMapper
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setReducerClass
argument_list|(
name|ValidateReducer
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setOutputKeyClass
argument_list|(
name|Text
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setOutputValueClass
argument_list|(
name|Text
operator|.
name|class
argument_list|)
expr_stmt|;
comment|// force a single reducer
name|job
operator|.
name|setNumReduceTasks
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|// force a single split
name|FileInputFormat
operator|.
name|setMinInputSplitSize
argument_list|(
name|job
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
name|job
operator|.
name|setInputFormatClass
argument_list|(
name|TeraInputFormat
operator|.
name|class
argument_list|)
expr_stmt|;
return|return
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
return|;
block|}
comment|/**    * @param args    */
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
name|TeraValidate
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
block|}
end_class

end_unit

