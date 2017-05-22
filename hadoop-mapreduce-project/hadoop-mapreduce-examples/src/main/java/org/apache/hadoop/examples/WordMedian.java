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
name|BufferedReader
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
name|io
operator|.
name|InputStreamReader
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
name|TaskCounter
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

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Charsets
import|;
end_import

begin_class
DECL|class|WordMedian
specifier|public
class|class
name|WordMedian
extends|extends
name|Configured
implements|implements
name|Tool
block|{
DECL|field|median
specifier|private
name|double
name|median
init|=
literal|0
decl_stmt|;
DECL|field|ONE
specifier|private
specifier|final
specifier|static
name|IntWritable
name|ONE
init|=
operator|new
name|IntWritable
argument_list|(
literal|1
argument_list|)
decl_stmt|;
comment|/**    * Maps words from line of text into a key-value pair; the length of the word    * as the key, and 1 as the value.    */
DECL|class|WordMedianMapper
specifier|public
specifier|static
class|class
name|WordMedianMapper
extends|extends
name|Mapper
argument_list|<
name|Object
argument_list|,
name|Text
argument_list|,
name|IntWritable
argument_list|,
name|IntWritable
argument_list|>
block|{
DECL|field|length
specifier|private
name|IntWritable
name|length
init|=
operator|new
name|IntWritable
argument_list|()
decl_stmt|;
comment|/**      * Emits a key-value pair for counting the word. Outputs are (IntWritable,      * IntWritable).      *       * @param value      *          This will be a line of text coming in from our input file.      */
DECL|method|map (Object key, Text value, Context context)
specifier|public
name|void
name|map
parameter_list|(
name|Object
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
name|StringTokenizer
name|itr
init|=
operator|new
name|StringTokenizer
argument_list|(
name|value
operator|.
name|toString
argument_list|()
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
name|String
name|string
init|=
name|itr
operator|.
name|nextToken
argument_list|()
decl_stmt|;
name|length
operator|.
name|set
argument_list|(
name|string
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|context
operator|.
name|write
argument_list|(
name|length
argument_list|,
name|ONE
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Performs integer summation of all the values for each key.    */
DECL|class|WordMedianReducer
specifier|public
specifier|static
class|class
name|WordMedianReducer
extends|extends
name|Reducer
argument_list|<
name|IntWritable
argument_list|,
name|IntWritable
argument_list|,
name|IntWritable
argument_list|,
name|IntWritable
argument_list|>
block|{
DECL|field|val
specifier|private
name|IntWritable
name|val
init|=
operator|new
name|IntWritable
argument_list|()
decl_stmt|;
comment|/**      * Sums all the individual values within the iterator and writes them to the      * same key.      *       * @param key      *          This will be a length of a word that was read.      * @param values      *          This will be an iterator of all the values associated with that      *          key.      */
DECL|method|reduce (IntWritable key, Iterable<IntWritable> values, Context context)
specifier|public
name|void
name|reduce
parameter_list|(
name|IntWritable
name|key
parameter_list|,
name|Iterable
argument_list|<
name|IntWritable
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
name|int
name|sum
init|=
literal|0
decl_stmt|;
for|for
control|(
name|IntWritable
name|value
range|:
name|values
control|)
block|{
name|sum
operator|+=
name|value
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
name|val
operator|.
name|set
argument_list|(
name|sum
argument_list|)
expr_stmt|;
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
comment|/**    * This is a standard program to read and find a median value based on a file    * of word counts such as: 1 456, 2 132, 3 56... Where the first values are    * the word lengths and the following values are the number of times that    * words of that length appear.    *     * @param path    *          The path to read the HDFS file from (part-r-00000...00001...etc).    * @param medianIndex1    *          The first length value to look for.    * @param medianIndex2    *          The second length value to look for (will be the same as the first    *          if there are an even number of words total).    * @throws IOException    *           If file cannot be found, we throw an exception.    * */
DECL|method|readAndFindMedian (String path, int medianIndex1, int medianIndex2, Configuration conf)
specifier|private
name|double
name|readAndFindMedian
parameter_list|(
name|String
name|path
parameter_list|,
name|int
name|medianIndex1
parameter_list|,
name|int
name|medianIndex2
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
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
name|Path
name|file
init|=
operator|new
name|Path
argument_list|(
name|path
argument_list|,
literal|"part-r-00000"
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|fs
operator|.
name|exists
argument_list|(
name|file
argument_list|)
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Output not found!"
argument_list|)
throw|;
name|BufferedReader
name|br
init|=
literal|null
decl_stmt|;
try|try
block|{
name|br
operator|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|fs
operator|.
name|open
argument_list|(
name|file
argument_list|)
argument_list|,
name|Charsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|num
init|=
literal|0
decl_stmt|;
name|String
name|line
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|br
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|StringTokenizer
name|st
init|=
operator|new
name|StringTokenizer
argument_list|(
name|line
argument_list|)
decl_stmt|;
comment|// grab length
name|String
name|currLen
init|=
name|st
operator|.
name|nextToken
argument_list|()
decl_stmt|;
comment|// grab count
name|String
name|lengthFreq
init|=
name|st
operator|.
name|nextToken
argument_list|()
decl_stmt|;
name|int
name|prevNum
init|=
name|num
decl_stmt|;
name|num
operator|+=
name|Integer
operator|.
name|parseInt
argument_list|(
name|lengthFreq
argument_list|)
expr_stmt|;
if|if
condition|(
name|medianIndex2
operator|>=
name|prevNum
operator|&&
name|medianIndex1
operator|<=
name|num
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"The median is: "
operator|+
name|currLen
argument_list|)
expr_stmt|;
name|br
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|Double
operator|.
name|parseDouble
argument_list|(
name|currLen
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|medianIndex2
operator|>=
name|prevNum
operator|&&
name|medianIndex1
operator|<
name|num
condition|)
block|{
name|String
name|nextCurrLen
init|=
name|st
operator|.
name|nextToken
argument_list|()
decl_stmt|;
name|double
name|theMedian
init|=
operator|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|currLen
argument_list|)
operator|+
name|Integer
operator|.
name|parseInt
argument_list|(
name|nextCurrLen
argument_list|)
operator|)
operator|/
literal|2.0
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"The median is: "
operator|+
name|theMedian
argument_list|)
expr_stmt|;
name|br
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|theMedian
return|;
block|}
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|br
operator|!=
literal|null
condition|)
block|{
name|br
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|// error, no median found
return|return
operator|-
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
name|ToolRunner
operator|.
name|run
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|,
operator|new
name|WordMedian
argument_list|()
argument_list|,
name|args
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
operator|!=
literal|2
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Usage: wordmedian<in><out>"
argument_list|)
expr_stmt|;
return|return
literal|0
return|;
block|}
name|setConf
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
expr_stmt|;
name|Configuration
name|conf
init|=
name|getConf
argument_list|()
decl_stmt|;
name|Job
name|job
init|=
name|Job
operator|.
name|getInstance
argument_list|(
name|conf
argument_list|,
literal|"word median"
argument_list|)
decl_stmt|;
name|job
operator|.
name|setJarByClass
argument_list|(
name|WordMedian
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setMapperClass
argument_list|(
name|WordMedianMapper
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setCombinerClass
argument_list|(
name|WordMedianReducer
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setReducerClass
argument_list|(
name|WordMedianReducer
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setOutputKeyClass
argument_list|(
name|IntWritable
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setOutputValueClass
argument_list|(
name|IntWritable
operator|.
name|class
argument_list|)
expr_stmt|;
name|FileInputFormat
operator|.
name|addInputPath
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
name|boolean
name|result
init|=
name|job
operator|.
name|waitForCompletion
argument_list|(
literal|true
argument_list|)
decl_stmt|;
comment|// Wait for JOB 1 -- get middle value to check for Median
name|long
name|totalWords
init|=
name|job
operator|.
name|getCounters
argument_list|()
operator|.
name|getGroup
argument_list|(
name|TaskCounter
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
argument_list|)
operator|.
name|findCounter
argument_list|(
literal|"MAP_OUTPUT_RECORDS"
argument_list|,
literal|"Map output records"
argument_list|)
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|int
name|medianIndex1
init|=
operator|(
name|int
operator|)
name|Math
operator|.
name|ceil
argument_list|(
operator|(
name|totalWords
operator|/
literal|2.0
operator|)
argument_list|)
decl_stmt|;
name|int
name|medianIndex2
init|=
operator|(
name|int
operator|)
name|Math
operator|.
name|floor
argument_list|(
operator|(
name|totalWords
operator|/
literal|2.0
operator|)
argument_list|)
decl_stmt|;
name|median
operator|=
name|readAndFindMedian
argument_list|(
name|args
index|[
literal|1
index|]
argument_list|,
name|medianIndex1
argument_list|,
name|medianIndex2
argument_list|,
name|conf
argument_list|)
expr_stmt|;
return|return
operator|(
name|result
condition|?
literal|0
else|:
literal|1
operator|)
return|;
block|}
DECL|method|getMedian ()
specifier|public
name|double
name|getMedian
parameter_list|()
block|{
return|return
name|median
return|;
block|}
block|}
end_class

end_unit

