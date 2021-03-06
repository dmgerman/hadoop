begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred.gridmix
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|gridmix
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
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|PrivilegedExceptionAction
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|Random
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
name|LocatedFileStatus
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
name|PathFilter
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
name|RemoteIterator
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
name|permission
operator|.
name|FsPermission
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
name|Utils
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
name|RecordWriter
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
name|security
operator|.
name|UserGroupInformation
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
name|StringUtils
import|;
end_import

begin_comment
comment|// TODO can replace with form of GridmixJob
end_comment

begin_class
DECL|class|GenerateData
class|class
name|GenerateData
extends|extends
name|GridmixJob
block|{
comment|/**    * Total bytes to write.    */
DECL|field|GRIDMIX_GEN_BYTES
specifier|public
specifier|static
specifier|final
name|String
name|GRIDMIX_GEN_BYTES
init|=
literal|"gridmix.gen.bytes"
decl_stmt|;
comment|/**    * Maximum size per file written.    */
DECL|field|GRIDMIX_GEN_CHUNK
specifier|public
specifier|static
specifier|final
name|String
name|GRIDMIX_GEN_CHUNK
init|=
literal|"gridmix.gen.bytes.per.file"
decl_stmt|;
comment|/**    * Size of writes to output file.    */
DECL|field|GRIDMIX_VAL_BYTES
specifier|public
specifier|static
specifier|final
name|String
name|GRIDMIX_VAL_BYTES
init|=
literal|"gendata.val.bytes"
decl_stmt|;
comment|/**    * Status reporting interval, in megabytes.    */
DECL|field|GRIDMIX_GEN_INTERVAL
specifier|public
specifier|static
specifier|final
name|String
name|GRIDMIX_GEN_INTERVAL
init|=
literal|"gendata.interval.mb"
decl_stmt|;
comment|/**    * Blocksize of generated data.    */
DECL|field|GRIDMIX_GEN_BLOCKSIZE
specifier|public
specifier|static
specifier|final
name|String
name|GRIDMIX_GEN_BLOCKSIZE
init|=
literal|"gridmix.gen.blocksize"
decl_stmt|;
comment|/**    * Replication of generated data.    */
DECL|field|GRIDMIX_GEN_REPLICATION
specifier|public
specifier|static
specifier|final
name|String
name|GRIDMIX_GEN_REPLICATION
init|=
literal|"gridmix.gen.replicas"
decl_stmt|;
DECL|field|JOB_NAME
specifier|static
specifier|final
name|String
name|JOB_NAME
init|=
literal|"GRIDMIX_GENERATE_INPUT_DATA"
decl_stmt|;
DECL|method|GenerateData (Configuration conf, Path outdir, long genbytes)
specifier|public
name|GenerateData
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|Path
name|outdir
parameter_list|,
name|long
name|genbytes
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|conf
argument_list|,
literal|0L
argument_list|,
name|JOB_NAME
argument_list|)
expr_stmt|;
name|job
operator|.
name|getConfiguration
argument_list|()
operator|.
name|setLong
argument_list|(
name|GRIDMIX_GEN_BYTES
argument_list|,
name|genbytes
argument_list|)
expr_stmt|;
name|FileOutputFormat
operator|.
name|setOutputPath
argument_list|(
name|job
argument_list|,
name|outdir
argument_list|)
expr_stmt|;
block|}
comment|/**    * Represents the input data characteristics.    */
DECL|class|DataStatistics
specifier|static
class|class
name|DataStatistics
block|{
DECL|field|dataSize
specifier|private
name|long
name|dataSize
decl_stmt|;
DECL|field|numFiles
specifier|private
name|long
name|numFiles
decl_stmt|;
DECL|field|isDataCompressed
specifier|private
name|boolean
name|isDataCompressed
decl_stmt|;
DECL|method|DataStatistics (long dataSize, long numFiles, boolean isCompressed)
name|DataStatistics
parameter_list|(
name|long
name|dataSize
parameter_list|,
name|long
name|numFiles
parameter_list|,
name|boolean
name|isCompressed
parameter_list|)
block|{
name|this
operator|.
name|dataSize
operator|=
name|dataSize
expr_stmt|;
name|this
operator|.
name|numFiles
operator|=
name|numFiles
expr_stmt|;
name|this
operator|.
name|isDataCompressed
operator|=
name|isCompressed
expr_stmt|;
block|}
DECL|method|getDataSize ()
name|long
name|getDataSize
parameter_list|()
block|{
return|return
name|dataSize
return|;
block|}
DECL|method|getNumFiles ()
name|long
name|getNumFiles
parameter_list|()
block|{
return|return
name|numFiles
return|;
block|}
DECL|method|isDataCompressed ()
name|boolean
name|isDataCompressed
parameter_list|()
block|{
return|return
name|isDataCompressed
return|;
block|}
block|}
comment|/**    * Publish the data statistics.    */
DECL|method|publishDataStatistics (Path inputDir, long genBytes, Configuration conf)
specifier|static
name|DataStatistics
name|publishDataStatistics
parameter_list|(
name|Path
name|inputDir
parameter_list|,
name|long
name|genBytes
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|CompressionEmulationUtil
operator|.
name|isCompressionEmulationEnabled
argument_list|(
name|conf
argument_list|)
condition|)
block|{
return|return
name|CompressionEmulationUtil
operator|.
name|publishCompressedDataStatistics
argument_list|(
name|inputDir
argument_list|,
name|conf
argument_list|,
name|genBytes
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|publishPlainDataStatistics
argument_list|(
name|conf
argument_list|,
name|inputDir
argument_list|)
return|;
block|}
block|}
DECL|method|publishPlainDataStatistics (Configuration conf, Path inputDir)
specifier|static
name|DataStatistics
name|publishPlainDataStatistics
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|Path
name|inputDir
parameter_list|)
throws|throws
name|IOException
block|{
name|FileSystem
name|fs
init|=
name|inputDir
operator|.
name|getFileSystem
argument_list|(
name|conf
argument_list|)
decl_stmt|;
comment|// obtain input data file statuses
name|long
name|dataSize
init|=
literal|0
decl_stmt|;
name|long
name|fileCount
init|=
literal|0
decl_stmt|;
name|RemoteIterator
argument_list|<
name|LocatedFileStatus
argument_list|>
name|iter
init|=
name|fs
operator|.
name|listFiles
argument_list|(
name|inputDir
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|PathFilter
name|filter
init|=
operator|new
name|Utils
operator|.
name|OutputFileUtils
operator|.
name|OutputFilesFilter
argument_list|()
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|LocatedFileStatus
name|lStatus
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|filter
operator|.
name|accept
argument_list|(
name|lStatus
operator|.
name|getPath
argument_list|()
argument_list|)
condition|)
block|{
name|dataSize
operator|+=
name|lStatus
operator|.
name|getLen
argument_list|()
expr_stmt|;
operator|++
name|fileCount
expr_stmt|;
block|}
block|}
comment|// publish the plain data statistics
name|LOG
operator|.
name|info
argument_list|(
literal|"Total size of input data : "
operator|+
name|StringUtils
operator|.
name|humanReadableInt
argument_list|(
name|dataSize
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Total number of input data files : "
operator|+
name|fileCount
argument_list|)
expr_stmt|;
return|return
operator|new
name|DataStatistics
argument_list|(
name|dataSize
argument_list|,
name|fileCount
argument_list|,
literal|false
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|call ()
specifier|public
name|Job
name|call
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
throws|,
name|ClassNotFoundException
block|{
name|UserGroupInformation
name|ugi
init|=
name|UserGroupInformation
operator|.
name|getLoginUser
argument_list|()
decl_stmt|;
name|ugi
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|Job
argument_list|>
argument_list|()
block|{
specifier|public
name|Job
name|run
parameter_list|()
throws|throws
name|IOException
throws|,
name|ClassNotFoundException
throws|,
name|InterruptedException
block|{
comment|// check if compression emulation is enabled
if|if
condition|(
name|CompressionEmulationUtil
operator|.
name|isCompressionEmulationEnabled
argument_list|(
name|job
operator|.
name|getConfiguration
argument_list|()
argument_list|)
condition|)
block|{
name|CompressionEmulationUtil
operator|.
name|configure
argument_list|(
name|job
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|configureRandomBytesDataGenerator
argument_list|()
expr_stmt|;
block|}
name|job
operator|.
name|submit
argument_list|()
expr_stmt|;
return|return
name|job
return|;
block|}
specifier|private
name|void
name|configureRandomBytesDataGenerator
parameter_list|()
block|{
name|job
operator|.
name|setMapperClass
argument_list|(
name|GenDataMapper
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
name|setMapOutputKeyClass
argument_list|(
name|NullWritable
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setMapOutputValueClass
argument_list|(
name|BytesWritable
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setInputFormatClass
argument_list|(
name|GenDataFormat
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setOutputFormatClass
argument_list|(
name|RawBytesOutputFormat
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setJarByClass
argument_list|(
name|GenerateData
operator|.
name|class
argument_list|)
expr_stmt|;
try|try
block|{
name|FileInputFormat
operator|.
name|addInputPath
argument_list|(
name|job
argument_list|,
operator|new
name|Path
argument_list|(
literal|"ignored"
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error while adding input path "
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
return|return
name|job
return|;
block|}
annotation|@
name|Override
DECL|method|canEmulateCompression ()
specifier|protected
name|boolean
name|canEmulateCompression
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
DECL|class|GenDataMapper
specifier|public
specifier|static
class|class
name|GenDataMapper
extends|extends
name|Mapper
argument_list|<
name|NullWritable
argument_list|,
name|LongWritable
argument_list|,
name|NullWritable
argument_list|,
name|BytesWritable
argument_list|>
block|{
DECL|field|val
specifier|private
name|BytesWritable
name|val
decl_stmt|;
DECL|field|r
specifier|private
specifier|final
name|Random
name|r
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|setup (Context context)
specifier|protected
name|void
name|setup
parameter_list|(
name|Context
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|val
operator|=
operator|new
name|BytesWritable
argument_list|(
operator|new
name|byte
index|[
name|context
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getInt
argument_list|(
name|GRIDMIX_VAL_BYTES
argument_list|,
literal|1024
operator|*
literal|1024
argument_list|)
index|]
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|map (NullWritable key, LongWritable value, Context context)
specifier|public
name|void
name|map
parameter_list|(
name|NullWritable
name|key
parameter_list|,
name|LongWritable
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
for|for
control|(
name|long
name|bytes
init|=
name|value
operator|.
name|get
argument_list|()
init|;
name|bytes
operator|>
literal|0
condition|;
name|bytes
operator|-=
name|val
operator|.
name|getLength
argument_list|()
control|)
block|{
name|r
operator|.
name|nextBytes
argument_list|(
name|val
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|val
operator|.
name|setSize
argument_list|(
operator|(
name|int
operator|)
name|Math
operator|.
name|min
argument_list|(
name|val
operator|.
name|getLength
argument_list|()
argument_list|,
name|bytes
argument_list|)
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
block|}
DECL|class|GenDataFormat
specifier|static
class|class
name|GenDataFormat
extends|extends
name|InputFormat
argument_list|<
name|NullWritable
argument_list|,
name|LongWritable
argument_list|>
block|{
annotation|@
name|Override
DECL|method|getSplits (JobContext jobCtxt)
specifier|public
name|List
argument_list|<
name|InputSplit
argument_list|>
name|getSplits
parameter_list|(
name|JobContext
name|jobCtxt
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|JobClient
name|client
init|=
operator|new
name|JobClient
argument_list|(
operator|new
name|JobConf
argument_list|(
name|jobCtxt
operator|.
name|getConfiguration
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|ClusterStatus
name|stat
init|=
name|client
operator|.
name|getClusterStatus
argument_list|(
literal|true
argument_list|)
decl_stmt|;
specifier|final
name|long
name|toGen
init|=
name|jobCtxt
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getLong
argument_list|(
name|GRIDMIX_GEN_BYTES
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|toGen
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Invalid/missing generation bytes: "
operator|+
name|toGen
argument_list|)
throw|;
block|}
specifier|final
name|int
name|nTrackers
init|=
name|stat
operator|.
name|getTaskTrackers
argument_list|()
decl_stmt|;
specifier|final
name|long
name|bytesPerTracker
init|=
name|toGen
operator|/
name|nTrackers
decl_stmt|;
specifier|final
name|ArrayList
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
argument_list|(
name|nTrackers
argument_list|)
decl_stmt|;
specifier|final
name|Pattern
name|trackerPattern
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"tracker_([^:]*):.*"
argument_list|)
decl_stmt|;
specifier|final
name|Matcher
name|m
init|=
name|trackerPattern
operator|.
name|matcher
argument_list|(
literal|""
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|tracker
range|:
name|stat
operator|.
name|getActiveTrackerNames
argument_list|()
control|)
block|{
name|m
operator|.
name|reset
argument_list|(
name|tracker
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|m
operator|.
name|find
argument_list|()
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Skipping node: "
operator|+
name|tracker
argument_list|)
expr_stmt|;
continue|continue;
block|}
specifier|final
name|String
name|name
init|=
name|m
operator|.
name|group
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|splits
operator|.
name|add
argument_list|(
operator|new
name|GenSplit
argument_list|(
name|bytesPerTracker
argument_list|,
operator|new
name|String
index|[]
block|{
name|name
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|splits
return|;
block|}
annotation|@
name|Override
DECL|method|createRecordReader ( InputSplit split, final TaskAttemptContext taskContext)
specifier|public
name|RecordReader
argument_list|<
name|NullWritable
argument_list|,
name|LongWritable
argument_list|>
name|createRecordReader
parameter_list|(
name|InputSplit
name|split
parameter_list|,
specifier|final
name|TaskAttemptContext
name|taskContext
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|RecordReader
argument_list|<
name|NullWritable
argument_list|,
name|LongWritable
argument_list|>
argument_list|()
block|{
name|long
name|written
init|=
literal|0L
decl_stmt|;
name|long
name|write
init|=
literal|0L
decl_stmt|;
name|long
name|RINTERVAL
decl_stmt|;
name|long
name|toWrite
decl_stmt|;
specifier|final
name|NullWritable
name|key
init|=
name|NullWritable
operator|.
name|get
argument_list|()
decl_stmt|;
specifier|final
name|LongWritable
name|val
init|=
operator|new
name|LongWritable
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|initialize
parameter_list|(
name|InputSplit
name|split
parameter_list|,
name|TaskAttemptContext
name|ctxt
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|toWrite
operator|=
name|split
operator|.
name|getLength
argument_list|()
expr_stmt|;
name|RINTERVAL
operator|=
name|ctxt
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getInt
argument_list|(
name|GRIDMIX_GEN_INTERVAL
argument_list|,
literal|10
argument_list|)
operator|<<
literal|20
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|nextKeyValue
parameter_list|()
throws|throws
name|IOException
block|{
name|written
operator|+=
name|write
expr_stmt|;
name|write
operator|=
name|Math
operator|.
name|min
argument_list|(
name|toWrite
operator|-
name|written
argument_list|,
name|RINTERVAL
argument_list|)
expr_stmt|;
name|val
operator|.
name|set
argument_list|(
name|write
argument_list|)
expr_stmt|;
return|return
name|written
operator|<
name|toWrite
return|;
block|}
annotation|@
name|Override
specifier|public
name|float
name|getProgress
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|written
operator|/
operator|(
operator|(
name|float
operator|)
name|toWrite
operator|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|NullWritable
name|getCurrentKey
parameter_list|()
block|{
return|return
name|key
return|;
block|}
annotation|@
name|Override
specifier|public
name|LongWritable
name|getCurrentValue
parameter_list|()
block|{
return|return
name|val
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|taskContext
operator|.
name|setStatus
argument_list|(
literal|"Wrote "
operator|+
name|toWrite
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
block|}
DECL|class|GenSplit
specifier|static
class|class
name|GenSplit
extends|extends
name|InputSplit
implements|implements
name|Writable
block|{
DECL|field|bytes
specifier|private
name|long
name|bytes
decl_stmt|;
DECL|field|nLoc
specifier|private
name|int
name|nLoc
decl_stmt|;
DECL|field|locations
specifier|private
name|String
index|[]
name|locations
decl_stmt|;
DECL|method|GenSplit ()
specifier|public
name|GenSplit
parameter_list|()
block|{ }
DECL|method|GenSplit (long bytes, String[] locations)
specifier|public
name|GenSplit
parameter_list|(
name|long
name|bytes
parameter_list|,
name|String
index|[]
name|locations
parameter_list|)
block|{
name|this
argument_list|(
name|bytes
argument_list|,
name|locations
operator|.
name|length
argument_list|,
name|locations
argument_list|)
expr_stmt|;
block|}
DECL|method|GenSplit (long bytes, int nLoc, String[] locations)
specifier|public
name|GenSplit
parameter_list|(
name|long
name|bytes
parameter_list|,
name|int
name|nLoc
parameter_list|,
name|String
index|[]
name|locations
parameter_list|)
block|{
name|this
operator|.
name|bytes
operator|=
name|bytes
expr_stmt|;
name|this
operator|.
name|nLoc
operator|=
name|nLoc
expr_stmt|;
name|this
operator|.
name|locations
operator|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|locations
argument_list|,
name|nLoc
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getLength ()
specifier|public
name|long
name|getLength
parameter_list|()
block|{
return|return
name|bytes
return|;
block|}
annotation|@
name|Override
DECL|method|getLocations ()
specifier|public
name|String
index|[]
name|getLocations
parameter_list|()
block|{
return|return
name|locations
return|;
block|}
annotation|@
name|Override
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
name|bytes
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|nLoc
operator|=
name|in
operator|.
name|readInt
argument_list|()
expr_stmt|;
if|if
condition|(
literal|null
operator|==
name|locations
operator|||
name|locations
operator|.
name|length
operator|<
name|nLoc
condition|)
block|{
name|locations
operator|=
operator|new
name|String
index|[
name|nLoc
index|]
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nLoc
condition|;
operator|++
name|i
control|)
block|{
name|locations
index|[
name|i
index|]
operator|=
name|Text
operator|.
name|readString
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
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
name|bytes
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|nLoc
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nLoc
condition|;
operator|++
name|i
control|)
block|{
name|Text
operator|.
name|writeString
argument_list|(
name|out
argument_list|,
name|locations
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|class|RawBytesOutputFormat
specifier|static
class|class
name|RawBytesOutputFormat
extends|extends
name|FileOutputFormat
argument_list|<
name|NullWritable
argument_list|,
name|BytesWritable
argument_list|>
block|{
annotation|@
name|Override
DECL|method|getRecordWriter ( TaskAttemptContext job)
specifier|public
name|RecordWriter
argument_list|<
name|NullWritable
argument_list|,
name|BytesWritable
argument_list|>
name|getRecordWriter
parameter_list|(
name|TaskAttemptContext
name|job
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|ChunkWriter
argument_list|(
name|getDefaultWorkFile
argument_list|(
name|job
argument_list|,
literal|""
argument_list|)
argument_list|,
name|job
operator|.
name|getConfiguration
argument_list|()
argument_list|)
return|;
block|}
DECL|class|ChunkWriter
specifier|static
class|class
name|ChunkWriter
extends|extends
name|RecordWriter
argument_list|<
name|NullWritable
argument_list|,
name|BytesWritable
argument_list|>
block|{
DECL|field|outDir
specifier|private
specifier|final
name|Path
name|outDir
decl_stmt|;
DECL|field|fs
specifier|private
specifier|final
name|FileSystem
name|fs
decl_stmt|;
DECL|field|blocksize
specifier|private
specifier|final
name|int
name|blocksize
decl_stmt|;
DECL|field|replicas
specifier|private
specifier|final
name|short
name|replicas
decl_stmt|;
DECL|field|genPerms
specifier|private
specifier|final
name|FsPermission
name|genPerms
init|=
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
literal|0777
argument_list|)
decl_stmt|;
DECL|field|maxFileBytes
specifier|private
specifier|final
name|long
name|maxFileBytes
decl_stmt|;
DECL|field|accFileBytes
specifier|private
name|long
name|accFileBytes
init|=
literal|0L
decl_stmt|;
DECL|field|fileIdx
specifier|private
name|long
name|fileIdx
init|=
operator|-
literal|1L
decl_stmt|;
DECL|field|fileOut
specifier|private
name|OutputStream
name|fileOut
init|=
literal|null
decl_stmt|;
DECL|method|ChunkWriter (Path outDir, Configuration conf)
specifier|public
name|ChunkWriter
parameter_list|(
name|Path
name|outDir
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|outDir
operator|=
name|outDir
expr_stmt|;
name|fs
operator|=
name|outDir
operator|.
name|getFileSystem
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|blocksize
operator|=
name|conf
operator|.
name|getInt
argument_list|(
name|GRIDMIX_GEN_BLOCKSIZE
argument_list|,
literal|1
operator|<<
literal|28
argument_list|)
expr_stmt|;
name|replicas
operator|=
operator|(
name|short
operator|)
name|conf
operator|.
name|getInt
argument_list|(
name|GRIDMIX_GEN_REPLICATION
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|maxFileBytes
operator|=
name|conf
operator|.
name|getLong
argument_list|(
name|GRIDMIX_GEN_CHUNK
argument_list|,
literal|1L
operator|<<
literal|30
argument_list|)
expr_stmt|;
name|nextDestination
argument_list|()
expr_stmt|;
block|}
DECL|method|nextDestination ()
specifier|private
name|void
name|nextDestination
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|fileOut
operator|!=
literal|null
condition|)
block|{
name|fileOut
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|fileOut
operator|=
name|fs
operator|.
name|create
argument_list|(
operator|new
name|Path
argument_list|(
name|outDir
argument_list|,
literal|"segment-"
operator|+
operator|(
operator|++
name|fileIdx
operator|)
argument_list|)
argument_list|,
name|genPerms
argument_list|,
literal|false
argument_list|,
literal|64
operator|*
literal|1024
argument_list|,
name|replicas
argument_list|,
name|blocksize
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|accFileBytes
operator|=
literal|0L
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|write (NullWritable key, BytesWritable value)
specifier|public
name|void
name|write
parameter_list|(
name|NullWritable
name|key
parameter_list|,
name|BytesWritable
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|written
init|=
literal|0
decl_stmt|;
specifier|final
name|int
name|total
init|=
name|value
operator|.
name|getLength
argument_list|()
decl_stmt|;
while|while
condition|(
name|written
operator|<
name|total
condition|)
block|{
if|if
condition|(
name|accFileBytes
operator|>=
name|maxFileBytes
condition|)
block|{
name|nextDestination
argument_list|()
expr_stmt|;
block|}
specifier|final
name|int
name|write
init|=
operator|(
name|int
operator|)
name|Math
operator|.
name|min
argument_list|(
name|total
operator|-
name|written
argument_list|,
name|maxFileBytes
operator|-
name|accFileBytes
argument_list|)
decl_stmt|;
name|fileOut
operator|.
name|write
argument_list|(
name|value
operator|.
name|getBytes
argument_list|()
argument_list|,
name|written
argument_list|,
name|write
argument_list|)
expr_stmt|;
name|written
operator|+=
name|write
expr_stmt|;
name|accFileBytes
operator|+=
name|write
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|close (TaskAttemptContext ctxt)
specifier|public
name|void
name|close
parameter_list|(
name|TaskAttemptContext
name|ctxt
parameter_list|)
throws|throws
name|IOException
block|{
name|fileOut
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

