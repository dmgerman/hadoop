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
name|FSDataOutputStream
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
name|mapreduce
operator|.
name|lib
operator|.
name|input
operator|.
name|SequenceFileRecordReader
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
name|NullOutputFormat
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
name|server
operator|.
name|tasktracker
operator|.
name|TTConfig
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

begin_comment
comment|/**  * GridmixJob that generates distributed cache files.  * {@link GenerateDistCacheData} expects a list of distributed cache files to be  * generated as input. This list is expected to be stored as a sequence file  * and the filename is expected to be configured using  * {@code gridmix.distcache.file.list}.  * This input file contains the list of distributed cache files and their sizes.  * For each record (i.e. file size and file path) in this input file,  * a file with the specific file size at the specific path is created.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|GenerateDistCacheData
class|class
name|GenerateDistCacheData
extends|extends
name|GridmixJob
block|{
comment|/**    * Number of distributed cache files to be created by gridmix    */
DECL|field|GRIDMIX_DISTCACHE_FILE_COUNT
specifier|static
specifier|final
name|String
name|GRIDMIX_DISTCACHE_FILE_COUNT
init|=
literal|"gridmix.distcache.file.count"
decl_stmt|;
comment|/**    * Total number of bytes to be written to the distributed cache files by    * gridmix. i.e. Sum of sizes of all unique distributed cache files to be    * created by gridmix.    */
DECL|field|GRIDMIX_DISTCACHE_BYTE_COUNT
specifier|static
specifier|final
name|String
name|GRIDMIX_DISTCACHE_BYTE_COUNT
init|=
literal|"gridmix.distcache.byte.count"
decl_stmt|;
comment|/**    * The special file created(and used) by gridmix, that contains the list of    * unique distributed cache files that are to be created and their sizes.    */
DECL|field|GRIDMIX_DISTCACHE_FILE_LIST
specifier|static
specifier|final
name|String
name|GRIDMIX_DISTCACHE_FILE_LIST
init|=
literal|"gridmix.distcache.file.list"
decl_stmt|;
DECL|field|JOB_NAME
specifier|static
specifier|final
name|String
name|JOB_NAME
init|=
literal|"GRIDMIX_GENERATE_DISTCACHE_DATA"
decl_stmt|;
comment|/**    * Create distributed cache file with the permissions 0644.    * Since the private distributed cache directory doesn't have execute    * permission for others, it is OK to set read permission for others for    * the files under that directory and still they will become 'private'    * distributed cache files on the simulated cluster.    */
DECL|field|GRIDMIX_DISTCACHE_FILE_PERM
specifier|static
specifier|final
name|short
name|GRIDMIX_DISTCACHE_FILE_PERM
init|=
literal|0644
decl_stmt|;
DECL|method|GenerateDistCacheData (Configuration conf)
specifier|public
name|GenerateDistCacheData
parameter_list|(
name|Configuration
name|conf
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
name|job
operator|.
name|setMapperClass
argument_list|(
name|GenDCDataMapper
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
name|GenDCDataFormat
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setOutputFormatClass
argument_list|(
name|NullOutputFormat
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setJarByClass
argument_list|(
name|GenerateDistCacheData
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
name|job
operator|.
name|submit
argument_list|()
expr_stmt|;
return|return
name|job
return|;
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
DECL|class|GenDCDataMapper
specifier|public
specifier|static
class|class
name|GenDCDataMapper
extends|extends
name|Mapper
argument_list|<
name|LongWritable
argument_list|,
name|BytesWritable
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
DECL|field|fs
specifier|private
name|FileSystem
name|fs
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
name|GenerateData
operator|.
name|GRIDMIX_VAL_BYTES
argument_list|,
literal|1024
operator|*
literal|1024
argument_list|)
index|]
argument_list|)
expr_stmt|;
name|fs
operator|=
name|FileSystem
operator|.
name|get
argument_list|(
name|context
operator|.
name|getConfiguration
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Create one distributed cache file with the needed file size.
comment|// key is distributed cache file size and
comment|// value is distributed cache file path.
annotation|@
name|Override
DECL|method|map (LongWritable key, BytesWritable value, Context context)
specifier|public
name|void
name|map
parameter_list|(
name|LongWritable
name|key
parameter_list|,
name|BytesWritable
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
name|fileName
init|=
operator|new
name|String
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
decl_stmt|;
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
name|fileName
argument_list|)
decl_stmt|;
name|FSDataOutputStream
name|dos
init|=
name|FileSystem
operator|.
name|create
argument_list|(
name|fs
argument_list|,
name|path
argument_list|,
operator|new
name|FsPermission
argument_list|(
name|GRIDMIX_DISTCACHE_FILE_PERM
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|size
init|=
literal|0
decl_stmt|;
for|for
control|(
name|long
name|bytes
init|=
name|key
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
name|size
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
name|size
operator|=
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
expr_stmt|;
name|dos
operator|.
name|write
argument_list|(
name|val
operator|.
name|getBytes
argument_list|()
argument_list|,
literal|0
argument_list|,
name|size
argument_list|)
expr_stmt|;
comment|// Write to distCache file
block|}
name|dos
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * InputFormat for GenerateDistCacheData.    * Input to GenerateDistCacheData is the special file(in SequenceFile format)    * that contains the list of distributed cache files to be generated along    * with their file sizes.    */
DECL|class|GenDCDataFormat
specifier|static
class|class
name|GenDCDataFormat
extends|extends
name|InputFormat
argument_list|<
name|LongWritable
argument_list|,
name|BytesWritable
argument_list|>
block|{
comment|// Split the special file that contains the list of distributed cache file
comment|// paths and their file sizes such that each split corresponds to
comment|// approximately same amount of distributed cache data to be generated.
comment|// Consider numTaskTrackers * numMapSlotsPerTracker as the number of maps
comment|// for this job, if there is lot of data to be generated.
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
name|JobConf
name|jobConf
init|=
operator|new
name|JobConf
argument_list|(
name|jobCtxt
operator|.
name|getConfiguration
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|JobClient
name|client
init|=
operator|new
name|JobClient
argument_list|(
name|jobConf
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
name|int
name|numTrackers
init|=
name|stat
operator|.
name|getTaskTrackers
argument_list|()
decl_stmt|;
specifier|final
name|int
name|fileCount
init|=
name|jobConf
operator|.
name|getInt
argument_list|(
name|GRIDMIX_DISTCACHE_FILE_COUNT
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
comment|// Total size of distributed cache files to be generated
specifier|final
name|long
name|totalSize
init|=
name|jobConf
operator|.
name|getLong
argument_list|(
name|GRIDMIX_DISTCACHE_BYTE_COUNT
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
comment|// Get the path of the special file
name|String
name|distCacheFileList
init|=
name|jobConf
operator|.
name|get
argument_list|(
name|GRIDMIX_DISTCACHE_FILE_LIST
argument_list|)
decl_stmt|;
if|if
condition|(
name|fileCount
operator|<
literal|0
operator|||
name|totalSize
operator|<
literal|0
operator|||
name|distCacheFileList
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Invalid metadata: #files ("
operator|+
name|fileCount
operator|+
literal|"), total_size ("
operator|+
name|totalSize
operator|+
literal|"), filelisturi ("
operator|+
name|distCacheFileList
operator|+
literal|")"
argument_list|)
throw|;
block|}
name|Path
name|sequenceFile
init|=
operator|new
name|Path
argument_list|(
name|distCacheFileList
argument_list|)
decl_stmt|;
name|FileSystem
name|fs
init|=
name|sequenceFile
operator|.
name|getFileSystem
argument_list|(
name|jobConf
argument_list|)
decl_stmt|;
name|FileStatus
name|srcst
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|sequenceFile
argument_list|)
decl_stmt|;
comment|// Consider the number of TTs * mapSlotsPerTracker as number of mappers.
name|int
name|numMapSlotsPerTracker
init|=
name|jobConf
operator|.
name|getInt
argument_list|(
name|TTConfig
operator|.
name|TT_MAP_SLOTS
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|int
name|numSplits
init|=
name|numTrackers
operator|*
name|numMapSlotsPerTracker
decl_stmt|;
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
argument_list|(
name|numSplits
argument_list|)
decl_stmt|;
name|LongWritable
name|key
init|=
operator|new
name|LongWritable
argument_list|()
decl_stmt|;
name|BytesWritable
name|value
init|=
operator|new
name|BytesWritable
argument_list|()
decl_stmt|;
comment|// Average size of data to be generated by each map task
specifier|final
name|long
name|targetSize
init|=
name|Math
operator|.
name|max
argument_list|(
name|totalSize
operator|/
name|numSplits
argument_list|,
name|DistributedCacheEmulator
operator|.
name|AVG_BYTES_PER_MAP
argument_list|)
decl_stmt|;
name|long
name|splitStartPosition
init|=
literal|0L
decl_stmt|;
name|long
name|splitEndPosition
init|=
literal|0L
decl_stmt|;
name|long
name|acc
init|=
literal|0L
decl_stmt|;
name|long
name|bytesRemaining
init|=
name|srcst
operator|.
name|getLen
argument_list|()
decl_stmt|;
name|SequenceFile
operator|.
name|Reader
name|reader
init|=
literal|null
decl_stmt|;
try|try
block|{
name|reader
operator|=
operator|new
name|SequenceFile
operator|.
name|Reader
argument_list|(
name|fs
argument_list|,
name|sequenceFile
argument_list|,
name|jobConf
argument_list|)
expr_stmt|;
while|while
condition|(
name|reader
operator|.
name|next
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
condition|)
block|{
comment|// If adding this file would put this split past the target size,
comment|// cut the last split and put this file in the next split.
if|if
condition|(
name|acc
operator|+
name|key
operator|.
name|get
argument_list|()
operator|>
name|targetSize
operator|&&
name|acc
operator|!=
literal|0
condition|)
block|{
name|long
name|splitSize
init|=
name|splitEndPosition
operator|-
name|splitStartPosition
decl_stmt|;
name|splits
operator|.
name|add
argument_list|(
operator|new
name|FileSplit
argument_list|(
name|sequenceFile
argument_list|,
name|splitStartPosition
argument_list|,
name|splitSize
argument_list|,
operator|(
name|String
index|[]
operator|)
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|bytesRemaining
operator|-=
name|splitSize
expr_stmt|;
name|splitStartPosition
operator|=
name|splitEndPosition
expr_stmt|;
name|acc
operator|=
literal|0L
expr_stmt|;
block|}
name|acc
operator|+=
name|key
operator|.
name|get
argument_list|()
expr_stmt|;
name|splitEndPosition
operator|=
name|reader
operator|.
name|getPosition
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|reader
operator|!=
literal|null
condition|)
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|bytesRemaining
operator|!=
literal|0
condition|)
block|{
name|splits
operator|.
name|add
argument_list|(
operator|new
name|FileSplit
argument_list|(
name|sequenceFile
argument_list|,
name|splitStartPosition
argument_list|,
name|bytesRemaining
argument_list|,
operator|(
name|String
index|[]
operator|)
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|splits
return|;
block|}
comment|/**      * Returns a reader for this split of the distributed cache file list.      */
annotation|@
name|Override
DECL|method|createRecordReader ( InputSplit split, final TaskAttemptContext taskContext)
specifier|public
name|RecordReader
argument_list|<
name|LongWritable
argument_list|,
name|BytesWritable
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
throws|,
name|InterruptedException
block|{
return|return
operator|new
name|SequenceFileRecordReader
argument_list|<
name|LongWritable
argument_list|,
name|BytesWritable
argument_list|>
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

