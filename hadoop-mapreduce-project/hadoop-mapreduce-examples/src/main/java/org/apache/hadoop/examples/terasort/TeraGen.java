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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|LongWritable
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
name|WritableUtils
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
name|Counter
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
name|JobContext
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
comment|/**  * Generate the official GraySort input data set.  * The user specifies the number of rows and the output directory and this  * class runs a map/reduce program to generate the data.  * The format of the data is:  *<ul>  *<li>(10 bytes key) (constant 2 bytes) (32 bytes rowid)   *     (constant 4 bytes) (48 bytes filler) (constant 4 bytes)  *<li>The rowid is the right justified row id as a hex number.  *</ul>  *  *<p>  * To run the program:   *<b>bin/hadoop jar hadoop-*-examples.jar teragen 10000000000 in-dir</b>  */
end_comment

begin_class
DECL|class|TeraGen
specifier|public
class|class
name|TeraGen
extends|extends
name|Configured
implements|implements
name|Tool
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TeraGen
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|enum|Counters
DECL|enumConstant|CHECKSUM
specifier|public
enum|enum
name|Counters
block|{
name|CHECKSUM
block|}
comment|/**    * An input format that assigns ranges of longs to each mapper.    */
DECL|class|RangeInputFormat
specifier|static
class|class
name|RangeInputFormat
extends|extends
name|InputFormat
argument_list|<
name|LongWritable
argument_list|,
name|NullWritable
argument_list|>
block|{
comment|/**      * An input split consisting of a range on numbers.      */
DECL|class|RangeInputSplit
specifier|static
class|class
name|RangeInputSplit
extends|extends
name|InputSplit
implements|implements
name|Writable
block|{
DECL|field|firstRow
name|long
name|firstRow
decl_stmt|;
DECL|field|rowCount
name|long
name|rowCount
decl_stmt|;
DECL|method|RangeInputSplit ()
specifier|public
name|RangeInputSplit
parameter_list|()
block|{ }
DECL|method|RangeInputSplit (long offset, long length)
specifier|public
name|RangeInputSplit
parameter_list|(
name|long
name|offset
parameter_list|,
name|long
name|length
parameter_list|)
block|{
name|firstRow
operator|=
name|offset
expr_stmt|;
name|rowCount
operator|=
name|length
expr_stmt|;
block|}
DECL|method|getLength ()
specifier|public
name|long
name|getLength
parameter_list|()
throws|throws
name|IOException
block|{
return|return
literal|0
return|;
block|}
DECL|method|getLocations ()
specifier|public
name|String
index|[]
name|getLocations
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|String
index|[]
block|{}
return|;
block|}
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
name|firstRow
operator|=
name|WritableUtils
operator|.
name|readVLong
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|rowCount
operator|=
name|WritableUtils
operator|.
name|readVLong
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
name|WritableUtils
operator|.
name|writeVLong
argument_list|(
name|out
argument_list|,
name|firstRow
argument_list|)
expr_stmt|;
name|WritableUtils
operator|.
name|writeVLong
argument_list|(
name|out
argument_list|,
name|rowCount
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * A record reader that will generate a range of numbers.      */
DECL|class|RangeRecordReader
specifier|static
class|class
name|RangeRecordReader
extends|extends
name|RecordReader
argument_list|<
name|LongWritable
argument_list|,
name|NullWritable
argument_list|>
block|{
DECL|field|startRow
name|long
name|startRow
decl_stmt|;
DECL|field|finishedRows
name|long
name|finishedRows
decl_stmt|;
DECL|field|totalRows
name|long
name|totalRows
decl_stmt|;
DECL|field|key
name|LongWritable
name|key
init|=
literal|null
decl_stmt|;
DECL|method|RangeRecordReader ()
specifier|public
name|RangeRecordReader
parameter_list|()
block|{       }
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
block|{
name|startRow
operator|=
operator|(
operator|(
name|RangeInputSplit
operator|)
name|split
operator|)
operator|.
name|firstRow
expr_stmt|;
name|finishedRows
operator|=
literal|0
expr_stmt|;
name|totalRows
operator|=
operator|(
operator|(
name|RangeInputSplit
operator|)
name|split
operator|)
operator|.
name|rowCount
expr_stmt|;
block|}
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
comment|// NOTHING
block|}
DECL|method|getCurrentKey ()
specifier|public
name|LongWritable
name|getCurrentKey
parameter_list|()
block|{
return|return
name|key
return|;
block|}
DECL|method|getCurrentValue ()
specifier|public
name|NullWritable
name|getCurrentValue
parameter_list|()
block|{
return|return
name|NullWritable
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|getProgress ()
specifier|public
name|float
name|getProgress
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|finishedRows
operator|/
operator|(
name|float
operator|)
name|totalRows
return|;
block|}
DECL|method|nextKeyValue ()
specifier|public
name|boolean
name|nextKeyValue
parameter_list|()
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
name|LongWritable
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|finishedRows
operator|<
name|totalRows
condition|)
block|{
name|key
operator|.
name|set
argument_list|(
name|startRow
operator|+
name|finishedRows
argument_list|)
expr_stmt|;
name|finishedRows
operator|+=
literal|1
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
specifier|public
name|RecordReader
argument_list|<
name|LongWritable
argument_list|,
name|NullWritable
argument_list|>
DECL|method|createRecordReader (InputSplit split, TaskAttemptContext context)
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
name|RangeRecordReader
argument_list|()
return|;
block|}
comment|/**      * Create the desired number of splits, dividing the number of rows      * between the mappers.      */
DECL|method|getSplits (JobContext job)
specifier|public
name|List
argument_list|<
name|InputSplit
argument_list|>
name|getSplits
parameter_list|(
name|JobContext
name|job
parameter_list|)
block|{
name|long
name|totalRows
init|=
name|getNumberOfRows
argument_list|(
name|job
argument_list|)
decl_stmt|;
name|int
name|numSplits
init|=
name|job
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getInt
argument_list|(
name|MRJobConfig
operator|.
name|NUM_MAPS
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Generating "
operator|+
name|totalRows
operator|+
literal|" using "
operator|+
name|numSplits
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|InputSplit
argument_list|>
name|splits
init|=
operator|new
name|ArrayList
argument_list|<
name|InputSplit
argument_list|>
argument_list|()
decl_stmt|;
name|long
name|currentRow
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|split
init|=
literal|0
init|;
name|split
operator|<
name|numSplits
condition|;
operator|++
name|split
control|)
block|{
name|long
name|goal
init|=
operator|(
name|long
operator|)
name|Math
operator|.
name|ceil
argument_list|(
name|totalRows
operator|*
call|(
name|double
call|)
argument_list|(
name|split
operator|+
literal|1
argument_list|)
operator|/
name|numSplits
argument_list|)
decl_stmt|;
name|splits
operator|.
name|add
argument_list|(
operator|new
name|RangeInputSplit
argument_list|(
name|currentRow
argument_list|,
name|goal
operator|-
name|currentRow
argument_list|)
argument_list|)
expr_stmt|;
name|currentRow
operator|=
name|goal
expr_stmt|;
block|}
return|return
name|splits
return|;
block|}
block|}
DECL|method|getNumberOfRows (JobContext job)
specifier|static
name|long
name|getNumberOfRows
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
name|getLong
argument_list|(
name|TeraSortConfigKeys
operator|.
name|NUM_ROWS
operator|.
name|key
argument_list|()
argument_list|,
name|TeraSortConfigKeys
operator|.
name|DEFAULT_NUM_ROWS
argument_list|)
return|;
block|}
DECL|method|setNumberOfRows (Job job, long numRows)
specifier|static
name|void
name|setNumberOfRows
parameter_list|(
name|Job
name|job
parameter_list|,
name|long
name|numRows
parameter_list|)
block|{
name|job
operator|.
name|getConfiguration
argument_list|()
operator|.
name|setLong
argument_list|(
name|TeraSortConfigKeys
operator|.
name|NUM_ROWS
operator|.
name|key
argument_list|()
argument_list|,
name|numRows
argument_list|)
expr_stmt|;
block|}
comment|/**    * The Mapper class that given a row number, will generate the appropriate     * output line.    */
DECL|class|SortGenMapper
specifier|public
specifier|static
class|class
name|SortGenMapper
extends|extends
name|Mapper
argument_list|<
name|LongWritable
argument_list|,
name|NullWritable
argument_list|,
name|Text
argument_list|,
name|Text
argument_list|>
block|{
DECL|field|key
specifier|private
name|Text
name|key
init|=
operator|new
name|Text
argument_list|()
decl_stmt|;
DECL|field|value
specifier|private
name|Text
name|value
init|=
operator|new
name|Text
argument_list|()
decl_stmt|;
DECL|field|rand
specifier|private
name|Unsigned16
name|rand
init|=
literal|null
decl_stmt|;
DECL|field|rowId
specifier|private
name|Unsigned16
name|rowId
init|=
literal|null
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
DECL|field|crc32
specifier|private
name|Checksum
name|crc32
init|=
operator|new
name|PureJavaCrc32
argument_list|()
decl_stmt|;
DECL|field|total
specifier|private
name|Unsigned16
name|total
init|=
operator|new
name|Unsigned16
argument_list|()
decl_stmt|;
DECL|field|ONE
specifier|private
specifier|static
specifier|final
name|Unsigned16
name|ONE
init|=
operator|new
name|Unsigned16
argument_list|(
literal|1
argument_list|)
decl_stmt|;
DECL|field|buffer
specifier|private
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
name|TeraInputFormat
operator|.
name|KEY_LENGTH
operator|+
name|TeraInputFormat
operator|.
name|VALUE_LENGTH
index|]
decl_stmt|;
DECL|field|checksumCounter
specifier|private
name|Counter
name|checksumCounter
decl_stmt|;
DECL|method|map (LongWritable row, NullWritable ignored, Context context)
specifier|public
name|void
name|map
parameter_list|(
name|LongWritable
name|row
parameter_list|,
name|NullWritable
name|ignored
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
name|rand
operator|==
literal|null
condition|)
block|{
name|rowId
operator|=
operator|new
name|Unsigned16
argument_list|(
name|row
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|rand
operator|=
name|Random16
operator|.
name|skipAhead
argument_list|(
name|rowId
argument_list|)
expr_stmt|;
name|checksumCounter
operator|=
name|context
operator|.
name|getCounter
argument_list|(
name|Counters
operator|.
name|CHECKSUM
argument_list|)
expr_stmt|;
block|}
name|Random16
operator|.
name|nextRand
argument_list|(
name|rand
argument_list|)
expr_stmt|;
name|GenSort
operator|.
name|generateRecord
argument_list|(
name|buffer
argument_list|,
name|rand
argument_list|,
name|rowId
argument_list|)
expr_stmt|;
name|key
operator|.
name|set
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|TeraInputFormat
operator|.
name|KEY_LENGTH
argument_list|)
expr_stmt|;
name|value
operator|.
name|set
argument_list|(
name|buffer
argument_list|,
name|TeraInputFormat
operator|.
name|KEY_LENGTH
argument_list|,
name|TeraInputFormat
operator|.
name|VALUE_LENGTH
argument_list|)
expr_stmt|;
name|context
operator|.
name|write
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|crc32
operator|.
name|reset
argument_list|()
expr_stmt|;
name|crc32
operator|.
name|update
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|TeraInputFormat
operator|.
name|KEY_LENGTH
operator|+
name|TeraInputFormat
operator|.
name|VALUE_LENGTH
argument_list|)
expr_stmt|;
name|checksum
operator|.
name|set
argument_list|(
name|crc32
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|total
operator|.
name|add
argument_list|(
name|checksum
argument_list|)
expr_stmt|;
name|rowId
operator|.
name|add
argument_list|(
name|ONE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|cleanup (Context context)
specifier|public
name|void
name|cleanup
parameter_list|(
name|Context
name|context
parameter_list|)
block|{
if|if
condition|(
name|checksumCounter
operator|!=
literal|null
condition|)
block|{
name|checksumCounter
operator|.
name|increment
argument_list|(
name|total
operator|.
name|getLow8
argument_list|()
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
literal|"teragen<num rows><output dir>"
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"If you want to generate data and store them as "
operator|+
literal|"erasure code striping file, just make sure that the parent dir "
operator|+
literal|"of<output dir> has erasure code policy set"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Parse a number that optionally has a postfix that denotes a base.    * @param str an string integer with an option base {k,m,b,t}.    * @return the expanded value    */
DECL|method|parseHumanLong (String str)
specifier|private
specifier|static
name|long
name|parseHumanLong
parameter_list|(
name|String
name|str
parameter_list|)
block|{
name|char
name|tail
init|=
name|str
operator|.
name|charAt
argument_list|(
name|str
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
name|long
name|base
init|=
literal|1
decl_stmt|;
switch|switch
condition|(
name|tail
condition|)
block|{
case|case
literal|'t'
case|:
name|base
operator|*=
literal|1000
operator|*
literal|1000
operator|*
literal|1000
operator|*
literal|1000
expr_stmt|;
break|break;
case|case
literal|'b'
case|:
name|base
operator|*=
literal|1000
operator|*
literal|1000
operator|*
literal|1000
expr_stmt|;
break|break;
case|case
literal|'m'
case|:
name|base
operator|*=
literal|1000
operator|*
literal|1000
expr_stmt|;
break|break;
case|case
literal|'k'
case|:
name|base
operator|*=
literal|1000
expr_stmt|;
break|break;
default|default:
block|}
if|if
condition|(
name|base
operator|!=
literal|1
condition|)
block|{
name|str
operator|=
name|str
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|str
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
return|return
name|Long
operator|.
name|parseLong
argument_list|(
name|str
argument_list|)
operator|*
name|base
return|;
block|}
comment|/**    * @param args the cli arguments    */
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
name|IOException
throws|,
name|InterruptedException
throws|,
name|ClassNotFoundException
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
literal|2
return|;
block|}
name|setNumberOfRows
argument_list|(
name|job
argument_list|,
name|parseHumanLong
argument_list|(
name|args
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|Path
name|outputDir
init|=
operator|new
name|Path
argument_list|(
name|args
index|[
literal|1
index|]
argument_list|)
decl_stmt|;
name|FileOutputFormat
operator|.
name|setOutputPath
argument_list|(
name|job
argument_list|,
name|outputDir
argument_list|)
expr_stmt|;
name|job
operator|.
name|setJobName
argument_list|(
literal|"TeraGen"
argument_list|)
expr_stmt|;
name|job
operator|.
name|setJarByClass
argument_list|(
name|TeraGen
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setMapperClass
argument_list|(
name|SortGenMapper
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setNumReduceTasks
argument_list|(
literal|0
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
name|job
operator|.
name|setInputFormatClass
argument_list|(
name|RangeInputFormat
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setOutputFormatClass
argument_list|(
name|TeraOutputFormat
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
name|TeraGen
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

