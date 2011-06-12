begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.lib.input
package|package
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
name|fs
operator|.
name|Seekable
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
name|compress
operator|.
name|CodecPool
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
name|io
operator|.
name|compress
operator|.
name|SplitCompressionInputStream
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
name|SplittableCompressionCodec
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
name|CompressionCodecFactory
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
name|Decompressor
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
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_comment
comment|/**  * Treats keys as offset in file and value as line.   */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|LimitedPrivate
argument_list|(
block|{
literal|"MapReduce"
block|,
literal|"Pig"
block|}
argument_list|)
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|LineRecordReader
specifier|public
class|class
name|LineRecordReader
extends|extends
name|RecordReader
argument_list|<
name|LongWritable
argument_list|,
name|Text
argument_list|>
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
name|LineRecordReader
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|MAX_LINE_LENGTH
specifier|public
specifier|static
specifier|final
name|String
name|MAX_LINE_LENGTH
init|=
literal|"mapreduce.input.linerecordreader.line.maxlength"
decl_stmt|;
DECL|field|compressionCodecs
specifier|private
name|CompressionCodecFactory
name|compressionCodecs
init|=
literal|null
decl_stmt|;
DECL|field|start
specifier|private
name|long
name|start
decl_stmt|;
DECL|field|pos
specifier|private
name|long
name|pos
decl_stmt|;
DECL|field|end
specifier|private
name|long
name|end
decl_stmt|;
DECL|field|in
specifier|private
name|LineReader
name|in
decl_stmt|;
DECL|field|fileIn
specifier|private
name|FSDataInputStream
name|fileIn
decl_stmt|;
DECL|field|filePosition
specifier|private
name|Seekable
name|filePosition
decl_stmt|;
DECL|field|maxLineLength
specifier|private
name|int
name|maxLineLength
decl_stmt|;
DECL|field|key
specifier|private
name|LongWritable
name|key
init|=
literal|null
decl_stmt|;
DECL|field|value
specifier|private
name|Text
name|value
init|=
literal|null
decl_stmt|;
DECL|field|inputByteCounter
specifier|private
name|Counter
name|inputByteCounter
decl_stmt|;
DECL|field|codec
specifier|private
name|CompressionCodec
name|codec
decl_stmt|;
DECL|field|decompressor
specifier|private
name|Decompressor
name|decompressor
decl_stmt|;
DECL|field|recordDelimiterBytes
specifier|private
name|byte
index|[]
name|recordDelimiterBytes
decl_stmt|;
DECL|method|LineRecordReader ()
specifier|public
name|LineRecordReader
parameter_list|()
block|{   }
DECL|method|LineRecordReader (byte[] recordDelimiter)
specifier|public
name|LineRecordReader
parameter_list|(
name|byte
index|[]
name|recordDelimiter
parameter_list|)
block|{
name|this
operator|.
name|recordDelimiterBytes
operator|=
name|recordDelimiter
expr_stmt|;
block|}
DECL|method|initialize (InputSplit genericSplit, TaskAttemptContext context)
specifier|public
name|void
name|initialize
parameter_list|(
name|InputSplit
name|genericSplit
parameter_list|,
name|TaskAttemptContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|FileSplit
name|split
init|=
operator|(
name|FileSplit
operator|)
name|genericSplit
decl_stmt|;
name|inputByteCounter
operator|=
name|context
operator|.
name|getCounter
argument_list|(
name|FileInputFormat
operator|.
name|COUNTER_GROUP
argument_list|,
name|FileInputFormat
operator|.
name|BYTES_READ
argument_list|)
expr_stmt|;
name|Configuration
name|job
init|=
name|context
operator|.
name|getConfiguration
argument_list|()
decl_stmt|;
name|this
operator|.
name|maxLineLength
operator|=
name|job
operator|.
name|getInt
argument_list|(
name|MAX_LINE_LENGTH
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
name|start
operator|=
name|split
operator|.
name|getStart
argument_list|()
expr_stmt|;
name|end
operator|=
name|start
operator|+
name|split
operator|.
name|getLength
argument_list|()
expr_stmt|;
specifier|final
name|Path
name|file
init|=
name|split
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|compressionCodecs
operator|=
operator|new
name|CompressionCodecFactory
argument_list|(
name|job
argument_list|)
expr_stmt|;
name|codec
operator|=
name|compressionCodecs
operator|.
name|getCodec
argument_list|(
name|file
argument_list|)
expr_stmt|;
comment|// open the file and seek to the start of the split
specifier|final
name|FileSystem
name|fs
init|=
name|file
operator|.
name|getFileSystem
argument_list|(
name|job
argument_list|)
decl_stmt|;
name|fileIn
operator|=
name|fs
operator|.
name|open
argument_list|(
name|file
argument_list|)
expr_stmt|;
if|if
condition|(
name|isCompressedInput
argument_list|()
condition|)
block|{
name|decompressor
operator|=
name|CodecPool
operator|.
name|getDecompressor
argument_list|(
name|codec
argument_list|)
expr_stmt|;
if|if
condition|(
name|codec
operator|instanceof
name|SplittableCompressionCodec
condition|)
block|{
specifier|final
name|SplitCompressionInputStream
name|cIn
init|=
operator|(
operator|(
name|SplittableCompressionCodec
operator|)
name|codec
operator|)
operator|.
name|createInputStream
argument_list|(
name|fileIn
argument_list|,
name|decompressor
argument_list|,
name|start
argument_list|,
name|end
argument_list|,
name|SplittableCompressionCodec
operator|.
name|READ_MODE
operator|.
name|BYBLOCK
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|this
operator|.
name|recordDelimiterBytes
condition|)
block|{
name|in
operator|=
operator|new
name|LineReader
argument_list|(
name|cIn
argument_list|,
name|job
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|in
operator|=
operator|new
name|LineReader
argument_list|(
name|cIn
argument_list|,
name|job
argument_list|,
name|this
operator|.
name|recordDelimiterBytes
argument_list|)
expr_stmt|;
block|}
name|start
operator|=
name|cIn
operator|.
name|getAdjustedStart
argument_list|()
expr_stmt|;
name|end
operator|=
name|cIn
operator|.
name|getAdjustedEnd
argument_list|()
expr_stmt|;
name|filePosition
operator|=
name|cIn
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
literal|null
operator|==
name|this
operator|.
name|recordDelimiterBytes
condition|)
block|{
name|in
operator|=
operator|new
name|LineReader
argument_list|(
name|codec
operator|.
name|createInputStream
argument_list|(
name|fileIn
argument_list|,
name|decompressor
argument_list|)
argument_list|,
name|job
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|in
operator|=
operator|new
name|LineReader
argument_list|(
name|codec
operator|.
name|createInputStream
argument_list|(
name|fileIn
argument_list|,
name|decompressor
argument_list|)
argument_list|,
name|job
argument_list|,
name|this
operator|.
name|recordDelimiterBytes
argument_list|)
expr_stmt|;
block|}
name|filePosition
operator|=
name|fileIn
expr_stmt|;
block|}
block|}
else|else
block|{
name|fileIn
operator|.
name|seek
argument_list|(
name|start
argument_list|)
expr_stmt|;
if|if
condition|(
literal|null
operator|==
name|this
operator|.
name|recordDelimiterBytes
condition|)
block|{
name|in
operator|=
operator|new
name|LineReader
argument_list|(
name|fileIn
argument_list|,
name|job
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|in
operator|=
operator|new
name|LineReader
argument_list|(
name|fileIn
argument_list|,
name|job
argument_list|,
name|this
operator|.
name|recordDelimiterBytes
argument_list|)
expr_stmt|;
block|}
name|filePosition
operator|=
name|fileIn
expr_stmt|;
block|}
comment|// If this is not the first split, we always throw away first record
comment|// because we always (except the last split) read one extra line in
comment|// next() method.
if|if
condition|(
name|start
operator|!=
literal|0
condition|)
block|{
name|start
operator|+=
name|in
operator|.
name|readLine
argument_list|(
operator|new
name|Text
argument_list|()
argument_list|,
literal|0
argument_list|,
name|maxBytesToConsume
argument_list|(
name|start
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|pos
operator|=
name|start
expr_stmt|;
block|}
DECL|method|isCompressedInput ()
specifier|private
name|boolean
name|isCompressedInput
parameter_list|()
block|{
return|return
operator|(
name|codec
operator|!=
literal|null
operator|)
return|;
block|}
DECL|method|maxBytesToConsume (long pos)
specifier|private
name|int
name|maxBytesToConsume
parameter_list|(
name|long
name|pos
parameter_list|)
block|{
return|return
name|isCompressedInput
argument_list|()
condition|?
name|Integer
operator|.
name|MAX_VALUE
else|:
operator|(
name|int
operator|)
name|Math
operator|.
name|min
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|end
operator|-
name|pos
argument_list|)
return|;
block|}
DECL|method|getFilePosition ()
specifier|private
name|long
name|getFilePosition
parameter_list|()
throws|throws
name|IOException
block|{
name|long
name|retVal
decl_stmt|;
if|if
condition|(
name|isCompressedInput
argument_list|()
operator|&&
literal|null
operator|!=
name|filePosition
condition|)
block|{
name|retVal
operator|=
name|filePosition
operator|.
name|getPos
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|retVal
operator|=
name|pos
expr_stmt|;
block|}
return|return
name|retVal
return|;
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
name|LongWritable
argument_list|()
expr_stmt|;
block|}
name|key
operator|.
name|set
argument_list|(
name|pos
argument_list|)
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
comment|// We always read one extra line, which lies outside the upper
comment|// split limit i.e. (end - 1)
while|while
condition|(
name|getFilePosition
argument_list|()
operator|<=
name|end
condition|)
block|{
name|newSize
operator|=
name|in
operator|.
name|readLine
argument_list|(
name|value
argument_list|,
name|maxLineLength
argument_list|,
name|Math
operator|.
name|max
argument_list|(
name|maxBytesToConsume
argument_list|(
name|pos
argument_list|)
argument_list|,
name|maxLineLength
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|newSize
operator|==
literal|0
condition|)
block|{
break|break;
block|}
name|pos
operator|+=
name|newSize
expr_stmt|;
name|inputByteCounter
operator|.
name|increment
argument_list|(
name|newSize
argument_list|)
expr_stmt|;
if|if
condition|(
name|newSize
operator|<
name|maxLineLength
condition|)
block|{
break|break;
block|}
comment|// line too long. try again
name|LOG
operator|.
name|info
argument_list|(
literal|"Skipped line of size "
operator|+
name|newSize
operator|+
literal|" at pos "
operator|+
operator|(
name|pos
operator|-
name|newSize
operator|)
argument_list|)
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
annotation|@
name|Override
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
annotation|@
name|Override
DECL|method|getCurrentValue ()
specifier|public
name|Text
name|getCurrentValue
parameter_list|()
block|{
return|return
name|value
return|;
block|}
comment|/**    * Get the progress within the split    */
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
name|start
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
name|getFilePosition
argument_list|()
operator|-
name|start
operator|)
operator|/
call|(
name|float
call|)
argument_list|(
name|end
operator|-
name|start
argument_list|)
argument_list|)
return|;
block|}
block|}
DECL|method|close ()
specifier|public
specifier|synchronized
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
if|if
condition|(
name|in
operator|!=
literal|null
condition|)
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|decompressor
operator|!=
literal|null
condition|)
block|{
name|CodecPool
operator|.
name|returnDecompressor
argument_list|(
name|decompressor
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

