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
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
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
name|FileStatus
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
name|SequenceFile
operator|.
name|CompressionType
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
name|SortValidator
operator|.
name|RecordStatsChecker
operator|.
name|NonSplitableSequenceFileInputFormat
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
name|lib
operator|.
name|IdentityMapper
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
name|lib
operator|.
name|IdentityReducer
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
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_class
DECL|class|BigMapOutput
specifier|public
class|class
name|BigMapOutput
extends|extends
name|Configured
implements|implements
name|Tool
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|BigMapOutput
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|random
specifier|private
specifier|static
name|Random
name|random
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
DECL|field|MIN_KEY
specifier|public
specifier|static
name|String
name|MIN_KEY
init|=
literal|"mapreduce.bmo.minkey"
decl_stmt|;
DECL|field|MIN_VALUE
specifier|public
specifier|static
name|String
name|MIN_VALUE
init|=
literal|"mapreduce.bmo.minvalue"
decl_stmt|;
DECL|field|MAX_KEY
specifier|public
specifier|static
name|String
name|MAX_KEY
init|=
literal|"mapreduce.bmo.maxkey"
decl_stmt|;
DECL|field|MAX_VALUE
specifier|public
specifier|static
name|String
name|MAX_VALUE
init|=
literal|"mapreduce.bmo.maxvalue"
decl_stmt|;
DECL|method|randomizeBytes (byte[] data, int offset, int length)
specifier|private
specifier|static
name|void
name|randomizeBytes
parameter_list|(
name|byte
index|[]
name|data
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
name|offset
operator|+
name|length
operator|-
literal|1
init|;
name|i
operator|>=
name|offset
condition|;
operator|--
name|i
control|)
block|{
name|data
index|[
name|i
index|]
operator|=
operator|(
name|byte
operator|)
name|random
operator|.
name|nextInt
argument_list|(
literal|256
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|createBigMapInputFile (Configuration conf, FileSystem fs, Path dir, long fileSizeInMB)
specifier|private
specifier|static
name|void
name|createBigMapInputFile
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|FileSystem
name|fs
parameter_list|,
name|Path
name|dir
parameter_list|,
name|long
name|fileSizeInMB
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Check if the input path exists and is non-empty
if|if
condition|(
name|fs
operator|.
name|exists
argument_list|(
name|dir
argument_list|)
condition|)
block|{
name|FileStatus
index|[]
name|list
init|=
name|fs
operator|.
name|listStatus
argument_list|(
name|dir
argument_list|)
decl_stmt|;
if|if
condition|(
name|list
operator|.
name|length
operator|>
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Input path: "
operator|+
name|dir
operator|+
literal|" already exists... "
argument_list|)
throw|;
block|}
block|}
name|Path
name|file
init|=
operator|new
name|Path
argument_list|(
name|dir
argument_list|,
literal|"part-0"
argument_list|)
decl_stmt|;
name|SequenceFile
operator|.
name|Writer
name|writer
init|=
name|SequenceFile
operator|.
name|createWriter
argument_list|(
name|fs
argument_list|,
name|conf
argument_list|,
name|file
argument_list|,
name|BytesWritable
operator|.
name|class
argument_list|,
name|BytesWritable
operator|.
name|class
argument_list|,
name|CompressionType
operator|.
name|NONE
argument_list|)
decl_stmt|;
name|long
name|numBytesToWrite
init|=
name|fileSizeInMB
operator|*
literal|1024
operator|*
literal|1024
decl_stmt|;
name|int
name|minKeySize
init|=
name|conf
operator|.
name|getInt
argument_list|(
name|MIN_KEY
argument_list|,
literal|10
argument_list|)
decl_stmt|;
empty_stmt|;
name|int
name|keySizeRange
init|=
name|conf
operator|.
name|getInt
argument_list|(
name|MAX_KEY
argument_list|,
literal|1000
argument_list|)
operator|-
name|minKeySize
decl_stmt|;
name|int
name|minValueSize
init|=
name|conf
operator|.
name|getInt
argument_list|(
name|MIN_VALUE
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|int
name|valueSizeRange
init|=
name|conf
operator|.
name|getInt
argument_list|(
name|MAX_VALUE
argument_list|,
literal|20000
argument_list|)
operator|-
name|minValueSize
decl_stmt|;
name|BytesWritable
name|randomKey
init|=
operator|new
name|BytesWritable
argument_list|()
decl_stmt|;
name|BytesWritable
name|randomValue
init|=
operator|new
name|BytesWritable
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Writing "
operator|+
name|numBytesToWrite
operator|+
literal|" bytes to "
operator|+
name|file
operator|+
literal|" with "
operator|+
literal|"minKeySize: "
operator|+
name|minKeySize
operator|+
literal|" keySizeRange: "
operator|+
name|keySizeRange
operator|+
literal|" minValueSize: "
operator|+
name|minValueSize
operator|+
literal|" valueSizeRange: "
operator|+
name|valueSizeRange
argument_list|)
expr_stmt|;
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
while|while
condition|(
name|numBytesToWrite
operator|>
literal|0
condition|)
block|{
name|int
name|keyLength
init|=
name|minKeySize
operator|+
operator|(
name|keySizeRange
operator|!=
literal|0
condition|?
name|random
operator|.
name|nextInt
argument_list|(
name|keySizeRange
argument_list|)
else|:
literal|0
operator|)
decl_stmt|;
name|randomKey
operator|.
name|setSize
argument_list|(
name|keyLength
argument_list|)
expr_stmt|;
name|randomizeBytes
argument_list|(
name|randomKey
operator|.
name|getBytes
argument_list|()
argument_list|,
literal|0
argument_list|,
name|randomKey
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|valueLength
init|=
name|minValueSize
operator|+
operator|(
name|valueSizeRange
operator|!=
literal|0
condition|?
name|random
operator|.
name|nextInt
argument_list|(
name|valueSizeRange
argument_list|)
else|:
literal|0
operator|)
decl_stmt|;
name|randomValue
operator|.
name|setSize
argument_list|(
name|valueLength
argument_list|)
expr_stmt|;
name|randomizeBytes
argument_list|(
name|randomValue
operator|.
name|getBytes
argument_list|()
argument_list|,
literal|0
argument_list|,
name|randomValue
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|append
argument_list|(
name|randomKey
argument_list|,
name|randomValue
argument_list|)
expr_stmt|;
name|numBytesToWrite
operator|-=
name|keyLength
operator|+
name|valueLength
expr_stmt|;
block|}
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|long
name|end
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Created "
operator|+
name|file
operator|+
literal|" of size: "
operator|+
name|fileSizeInMB
operator|+
literal|"MB in "
operator|+
operator|(
name|end
operator|-
name|start
operator|)
operator|/
literal|1000
operator|+
literal|"secs"
argument_list|)
expr_stmt|;
block|}
DECL|method|usage ()
specifier|private
specifier|static
name|void
name|usage
parameter_list|()
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"BigMapOutput -input<input-dir> -output<output-dir> "
operator|+
literal|"[-create<filesize in MB>]"
argument_list|)
expr_stmt|;
name|ToolRunner
operator|.
name|printGenericCommandUsage
argument_list|(
name|System
operator|.
name|err
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
literal|4
condition|)
block|{
comment|//input-dir should contain a huge file (> 2GB)
name|usage
argument_list|()
expr_stmt|;
block|}
name|Path
name|bigMapInput
init|=
literal|null
decl_stmt|;
name|Path
name|outputPath
init|=
literal|null
decl_stmt|;
name|boolean
name|createInput
init|=
literal|false
decl_stmt|;
name|long
name|fileSizeInMB
init|=
literal|3
operator|*
literal|1024
decl_stmt|;
comment|// default of 3GB (>2GB)
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
if|if
condition|(
literal|"-input"
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
name|bigMapInput
operator|=
operator|new
name|Path
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
literal|"-output"
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
name|outputPath
operator|=
operator|new
name|Path
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
literal|"-create"
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
name|createInput
operator|=
literal|true
expr_stmt|;
name|fileSizeInMB
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|args
index|[
operator|++
name|i
index|]
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|usage
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|bigMapInput
operator|==
literal|null
operator|||
name|outputPath
operator|==
literal|null
condition|)
block|{
comment|// report usage and exit
name|usage
argument_list|()
expr_stmt|;
comment|// this stops IDES warning about unset local variables.
return|return
operator|-
literal|1
return|;
block|}
name|JobConf
name|jobConf
init|=
operator|new
name|JobConf
argument_list|(
name|getConf
argument_list|()
argument_list|,
name|BigMapOutput
operator|.
name|class
argument_list|)
decl_stmt|;
name|jobConf
operator|.
name|setJobName
argument_list|(
literal|"BigMapOutput"
argument_list|)
expr_stmt|;
name|jobConf
operator|.
name|setInputFormat
argument_list|(
name|NonSplitableSequenceFileInputFormat
operator|.
name|class
argument_list|)
expr_stmt|;
name|jobConf
operator|.
name|setOutputFormat
argument_list|(
name|SequenceFileOutputFormat
operator|.
name|class
argument_list|)
expr_stmt|;
name|FileInputFormat
operator|.
name|setInputPaths
argument_list|(
name|jobConf
argument_list|,
name|bigMapInput
argument_list|)
expr_stmt|;
name|outputPath
operator|.
name|getFileSystem
argument_list|(
name|jobConf
argument_list|)
operator|.
name|delete
argument_list|(
name|outputPath
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|FileOutputFormat
operator|.
name|setOutputPath
argument_list|(
name|jobConf
argument_list|,
name|outputPath
argument_list|)
expr_stmt|;
name|jobConf
operator|.
name|setMapperClass
argument_list|(
name|IdentityMapper
operator|.
name|class
argument_list|)
expr_stmt|;
name|jobConf
operator|.
name|setReducerClass
argument_list|(
name|IdentityReducer
operator|.
name|class
argument_list|)
expr_stmt|;
name|jobConf
operator|.
name|setOutputKeyClass
argument_list|(
name|BytesWritable
operator|.
name|class
argument_list|)
expr_stmt|;
name|jobConf
operator|.
name|setOutputValueClass
argument_list|(
name|BytesWritable
operator|.
name|class
argument_list|)
expr_stmt|;
if|if
condition|(
name|createInput
condition|)
block|{
name|createBigMapInputFile
argument_list|(
name|jobConf
argument_list|,
name|bigMapInput
operator|.
name|getFileSystem
argument_list|(
name|jobConf
argument_list|)
argument_list|,
name|bigMapInput
argument_list|,
name|fileSizeInMB
argument_list|)
expr_stmt|;
block|}
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
name|JobClient
operator|.
name|runJob
argument_list|(
name|jobConf
argument_list|)
expr_stmt|;
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
return|return
literal|0
return|;
block|}
DECL|method|main (String argv[])
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
name|argv
index|[]
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
name|BigMapOutput
argument_list|()
argument_list|,
name|argv
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

