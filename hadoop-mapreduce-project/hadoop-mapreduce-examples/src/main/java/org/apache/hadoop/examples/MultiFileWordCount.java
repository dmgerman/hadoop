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
name|DataInput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutput
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
name|StringTokenizer
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
name|FSDataInputStream
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
name|IntWritable
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
name|mapreduce
operator|.
name|InputSplit
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
name|mapreduce
operator|.
name|TaskAttemptContext
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
name|CombineFileInputFormat
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
name|CombineFileRecordReader
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
name|CombineFileSplit
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
name|reduce
operator|.
name|IntSumReducer
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
name|LineReader
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
comment|/**  * MultiFileWordCount is an example to demonstrate the usage of   * MultiFileInputFormat. This examples counts the occurrences of  * words in the text files under the given input directory.  */
end_comment

begin_class
DECL|class|MultiFileWordCount
specifier|public
class|class
name|MultiFileWordCount
extends|extends
name|Configured
implements|implements
name|Tool
block|{
comment|/**    * This record keeps&lt;filename,offset&gt; pairs.    */
DECL|class|WordOffset
specifier|public
specifier|static
class|class
name|WordOffset
implements|implements
name|WritableComparable
block|{
DECL|field|offset
specifier|private
name|long
name|offset
decl_stmt|;
DECL|field|fileName
specifier|private
name|String
name|fileName
decl_stmt|;
DECL|method|readFields (DataInput in)
specifier|public
name|void
name|readFields
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|offset
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|this
operator|.
name|fileName
operator|=
name|Text
operator|.
name|readString
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
DECL|method|write (DataOutput out)
specifier|public
name|void
name|write
parameter_list|(
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeLong
argument_list|(
name|offset
argument_list|)
expr_stmt|;
name|Text
operator|.
name|writeString
argument_list|(
name|out
argument_list|,
name|fileName
argument_list|)
expr_stmt|;
block|}
DECL|method|compareTo (Object o)
specifier|public
name|int
name|compareTo
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
name|WordOffset
name|that
init|=
operator|(
name|WordOffset
operator|)
name|o
decl_stmt|;
name|int
name|f
init|=
name|this
operator|.
name|fileName
operator|.
name|compareTo
argument_list|(
name|that
operator|.
name|fileName
argument_list|)
decl_stmt|;
if|if
condition|(
name|f
operator|==
literal|0
condition|)
block|{
return|return
operator|(
name|int
operator|)
name|Math
operator|.
name|signum
argument_list|(
call|(
name|double
call|)
argument_list|(
name|this
operator|.
name|offset
operator|-
name|that
operator|.
name|offset
argument_list|)
argument_list|)
return|;
block|}
return|return
name|f
return|;
block|}
annotation|@
name|Override
DECL|method|equals (Object obj)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|obj
operator|instanceof
name|WordOffset
condition|)
return|return
name|this
operator|.
name|compareTo
argument_list|(
name|obj
argument_list|)
operator|==
literal|0
return|;
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
assert|assert
literal|false
operator|:
literal|"hashCode not designed"
assert|;
return|return
literal|42
return|;
comment|//an arbitrary constant
block|}
block|}
comment|/**    * To use {@link CombineFileInputFormat}, one should extend it, to return a     * (custom) {@link RecordReader}. CombineFileInputFormat uses     * {@link CombineFileSplit}s.     */
DECL|class|MyInputFormat
specifier|public
specifier|static
class|class
name|MyInputFormat
extends|extends
name|CombineFileInputFormat
argument_list|<
name|WordOffset
argument_list|,
name|Text
argument_list|>
block|{
DECL|method|createRecordReader (InputSplit split, TaskAttemptContext context)
specifier|public
name|RecordReader
argument_list|<
name|WordOffset
argument_list|,
name|Text
argument_list|>
name|createRecordReader
parameter_list|(
name|InputSplit
name|split
parameter_list|,
name|TaskAttemptContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|CombineFileRecordReader
argument_list|<
name|WordOffset
argument_list|,
name|Text
argument_list|>
argument_list|(
operator|(
name|CombineFileSplit
operator|)
name|split
argument_list|,
name|context
argument_list|,
name|CombineFileLineRecordReader
operator|.
name|class
argument_list|)
return|;
block|}
block|}
comment|/**    * RecordReader is responsible from extracting records from a chunk    * of the CombineFileSplit.     */
DECL|class|CombineFileLineRecordReader
specifier|public
specifier|static
class|class
name|CombineFileLineRecordReader
extends|extends
name|RecordReader
argument_list|<
name|WordOffset
argument_list|,
name|Text
argument_list|>
block|{
DECL|field|startOffset
specifier|private
name|long
name|startOffset
decl_stmt|;
comment|//offset of the chunk;
DECL|field|end
specifier|private
name|long
name|end
decl_stmt|;
comment|//end of the chunk;
DECL|field|pos
specifier|private
name|long
name|pos
decl_stmt|;
comment|// current pos
DECL|field|fs
specifier|private
name|FileSystem
name|fs
decl_stmt|;
DECL|field|path
specifier|private
name|Path
name|path
decl_stmt|;
DECL|field|key
specifier|private
name|WordOffset
name|key
decl_stmt|;
DECL|field|value
specifier|private
name|Text
name|value
decl_stmt|;
DECL|field|fileIn
specifier|private
name|FSDataInputStream
name|fileIn
decl_stmt|;
DECL|field|reader
specifier|private
name|LineReader
name|reader
decl_stmt|;
DECL|method|CombineFileLineRecordReader (CombineFileSplit split, TaskAttemptContext context, Integer index)
specifier|public
name|CombineFileLineRecordReader
parameter_list|(
name|CombineFileSplit
name|split
parameter_list|,
name|TaskAttemptContext
name|context
parameter_list|,
name|Integer
name|index
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|path
operator|=
name|split
operator|.
name|getPath
argument_list|(
name|index
argument_list|)
expr_stmt|;
name|fs
operator|=
name|this
operator|.
name|path
operator|.
name|getFileSystem
argument_list|(
name|context
operator|.
name|getConfiguration
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|startOffset
operator|=
name|split
operator|.
name|getOffset
argument_list|(
name|index
argument_list|)
expr_stmt|;
name|this
operator|.
name|end
operator|=
name|startOffset
operator|+
name|split
operator|.
name|getLength
argument_list|(
name|index
argument_list|)
expr_stmt|;
name|boolean
name|skipFirstLine
init|=
literal|false
decl_stmt|;
comment|//open the file
name|fileIn
operator|=
name|fs
operator|.
name|open
argument_list|(
name|path
argument_list|)
expr_stmt|;
if|if
condition|(
name|startOffset
operator|!=
literal|0
condition|)
block|{
name|skipFirstLine
operator|=
literal|true
expr_stmt|;
operator|--
name|startOffset
expr_stmt|;
name|fileIn
operator|.
name|seek
argument_list|(
name|startOffset
argument_list|)
expr_stmt|;
block|}
name|reader
operator|=
operator|new
name|LineReader
argument_list|(
name|fileIn
argument_list|)
expr_stmt|;
if|if
condition|(
name|skipFirstLine
condition|)
block|{
comment|// skip first line and re-establish "startOffset".
name|startOffset
operator|+=
name|reader
operator|.
name|readLine
argument_list|(
operator|new
name|Text
argument_list|()
argument_list|,
literal|0
argument_list|,
operator|(
name|int
operator|)
name|Math
operator|.
name|min
argument_list|(
operator|(
name|long
operator|)
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|end
operator|-
name|startOffset
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|pos
operator|=
name|startOffset
expr_stmt|;
block|}
DECL|method|initialize (InputSplit split, TaskAttemptContext context)
specifier|public
name|void
name|initialize
parameter_list|(
name|InputSplit
name|split
parameter_list|,
name|TaskAttemptContext
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{     }
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{ }
DECL|method|getProgress ()
specifier|public
name|float
name|getProgress
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|startOffset
operator|==
name|end
condition|)
block|{
return|return
literal|0.0f
return|;
block|}
else|else
block|{
return|return
name|Math
operator|.
name|min
argument_list|(
literal|1.0f
argument_list|,
operator|(
name|pos
operator|-
name|startOffset
operator|)
operator|/
call|(
name|float
call|)
argument_list|(
name|end
operator|-
name|startOffset
argument_list|)
argument_list|)
return|;
block|}
block|}
DECL|method|nextKeyValue ()
specifier|public
name|boolean
name|nextKeyValue
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|key
operator|==
literal|null
condition|)
block|{
name|key
operator|=
operator|new
name|WordOffset
argument_list|()
expr_stmt|;
name|key
operator|.
name|fileName
operator|=
name|path
operator|.
name|getName
argument_list|()
expr_stmt|;
block|}
name|key
operator|.
name|offset
operator|=
name|pos
expr_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
name|value
operator|=
operator|new
name|Text
argument_list|()
expr_stmt|;
block|}
name|int
name|newSize
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|pos
operator|<
name|end
condition|)
block|{
name|newSize
operator|=
name|reader
operator|.
name|readLine
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|pos
operator|+=
name|newSize
expr_stmt|;
block|}
if|if
condition|(
name|newSize
operator|==
literal|0
condition|)
block|{
name|key
operator|=
literal|null
expr_stmt|;
name|value
operator|=
literal|null
expr_stmt|;
return|return
literal|false
return|;
block|}
else|else
block|{
return|return
literal|true
return|;
block|}
block|}
DECL|method|getCurrentKey ()
specifier|public
name|WordOffset
name|getCurrentKey
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
return|return
name|key
return|;
block|}
DECL|method|getCurrentValue ()
specifier|public
name|Text
name|getCurrentValue
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
return|return
name|value
return|;
block|}
block|}
comment|/**    * This Mapper is similar to the one in {@link WordCount.TokenizerMapper}.    */
DECL|class|MapClass
specifier|public
specifier|static
class|class
name|MapClass
extends|extends
name|Mapper
argument_list|<
name|WordOffset
argument_list|,
name|Text
argument_list|,
name|Text
argument_list|,
name|IntWritable
argument_list|>
block|{
DECL|field|one
specifier|private
specifier|final
specifier|static
name|IntWritable
name|one
init|=
operator|new
name|IntWritable
argument_list|(
literal|1
argument_list|)
decl_stmt|;
DECL|field|word
specifier|private
name|Text
name|word
init|=
operator|new
name|Text
argument_list|()
decl_stmt|;
DECL|method|map (WordOffset key, Text value, Context context)
specifier|public
name|void
name|map
parameter_list|(
name|WordOffset
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
name|String
name|line
init|=
name|value
operator|.
name|toString
argument_list|()
decl_stmt|;
name|StringTokenizer
name|itr
init|=
operator|new
name|StringTokenizer
argument_list|(
name|line
argument_list|)
decl_stmt|;
while|while
condition|(
name|itr
operator|.
name|hasMoreTokens
argument_list|()
condition|)
block|{
name|word
operator|.
name|set
argument_list|(
name|itr
operator|.
name|nextToken
argument_list|()
argument_list|)
expr_stmt|;
name|context
operator|.
name|write
argument_list|(
name|word
argument_list|,
name|one
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|printUsage ()
specifier|private
name|void
name|printUsage
parameter_list|()
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Usage : multifilewc<input_dir><output>"
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
if|if
condition|(
name|args
operator|.
name|length
operator|<
literal|2
condition|)
block|{
name|printUsage
argument_list|()
expr_stmt|;
return|return
literal|2
return|;
block|}
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
name|job
operator|.
name|setJobName
argument_list|(
literal|"MultiFileWordCount"
argument_list|)
expr_stmt|;
name|job
operator|.
name|setJarByClass
argument_list|(
name|MultiFileWordCount
operator|.
name|class
argument_list|)
expr_stmt|;
comment|//set the InputFormat of the job to our InputFormat
name|job
operator|.
name|setInputFormatClass
argument_list|(
name|MyInputFormat
operator|.
name|class
argument_list|)
expr_stmt|;
comment|// the keys are words (strings)
name|job
operator|.
name|setOutputKeyClass
argument_list|(
name|Text
operator|.
name|class
argument_list|)
expr_stmt|;
comment|// the values are counts (ints)
name|job
operator|.
name|setOutputValueClass
argument_list|(
name|IntWritable
operator|.
name|class
argument_list|)
expr_stmt|;
comment|//use the defined mapper
name|job
operator|.
name|setMapperClass
argument_list|(
name|MapClass
operator|.
name|class
argument_list|)
expr_stmt|;
comment|//use the WordCount Reducer
name|job
operator|.
name|setCombinerClass
argument_list|(
name|IntSumReducer
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setReducerClass
argument_list|(
name|IntSumReducer
operator|.
name|class
argument_list|)
expr_stmt|;
name|FileInputFormat
operator|.
name|addInputPaths
argument_list|(
name|job
argument_list|,
name|args
index|[
literal|0
index|]
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
name|ret
init|=
name|ToolRunner
operator|.
name|run
argument_list|(
operator|new
name|MultiFileWordCount
argument_list|()
argument_list|,
name|args
argument_list|)
decl_stmt|;
name|System
operator|.
name|exit
argument_list|(
name|ret
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

