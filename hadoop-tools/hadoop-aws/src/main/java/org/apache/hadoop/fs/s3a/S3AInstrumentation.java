begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|s3a
package|;
end_package

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
name|Preconditions
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
name|metrics2
operator|.
name|MetricStringBuilder
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
name|metrics2
operator|.
name|annotation
operator|.
name|Metrics
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
name|metrics2
operator|.
name|lib
operator|.
name|Interns
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
name|metrics2
operator|.
name|lib
operator|.
name|MetricsRegistry
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
name|metrics2
operator|.
name|lib
operator|.
name|MutableCounterLong
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
name|metrics2
operator|.
name|lib
operator|.
name|MutableGaugeLong
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
name|metrics2
operator|.
name|lib
operator|.
name|MutableMetric
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|s3a
operator|.
name|Statistic
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Instrumentation of S3a.  * Derived from the {@code AzureFileSystemInstrumentation}.  *  * Counters and metrics are generally addressed in code by their name or  * {@link Statistic} key. There<i>may</i> be some Statistics which do  * not have an entry here. To avoid attempts to access such counters failing,  * the operations to increment/query metric values are designed to handle  * lookup failures.  */
end_comment

begin_class
annotation|@
name|Metrics
argument_list|(
name|about
operator|=
literal|"Metrics for S3a"
argument_list|,
name|context
operator|=
literal|"S3AFileSystem"
argument_list|)
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|S3AInstrumentation
specifier|public
class|class
name|S3AInstrumentation
block|{
DECL|field|CONTEXT
specifier|public
specifier|static
specifier|final
name|String
name|CONTEXT
init|=
literal|"S3AFileSystem"
decl_stmt|;
DECL|field|registry
specifier|private
specifier|final
name|MetricsRegistry
name|registry
init|=
operator|new
name|MetricsRegistry
argument_list|(
literal|"S3AFileSystem"
argument_list|)
operator|.
name|setContext
argument_list|(
name|CONTEXT
argument_list|)
decl_stmt|;
DECL|field|streamOpenOperations
specifier|private
specifier|final
name|MutableCounterLong
name|streamOpenOperations
decl_stmt|;
DECL|field|streamCloseOperations
specifier|private
specifier|final
name|MutableCounterLong
name|streamCloseOperations
decl_stmt|;
DECL|field|streamClosed
specifier|private
specifier|final
name|MutableCounterLong
name|streamClosed
decl_stmt|;
DECL|field|streamAborted
specifier|private
specifier|final
name|MutableCounterLong
name|streamAborted
decl_stmt|;
DECL|field|streamSeekOperations
specifier|private
specifier|final
name|MutableCounterLong
name|streamSeekOperations
decl_stmt|;
DECL|field|streamReadExceptions
specifier|private
specifier|final
name|MutableCounterLong
name|streamReadExceptions
decl_stmt|;
DECL|field|streamForwardSeekOperations
specifier|private
specifier|final
name|MutableCounterLong
name|streamForwardSeekOperations
decl_stmt|;
DECL|field|streamBackwardSeekOperations
specifier|private
specifier|final
name|MutableCounterLong
name|streamBackwardSeekOperations
decl_stmt|;
DECL|field|streamBytesSkippedOnSeek
specifier|private
specifier|final
name|MutableCounterLong
name|streamBytesSkippedOnSeek
decl_stmt|;
DECL|field|streamBytesBackwardsOnSeek
specifier|private
specifier|final
name|MutableCounterLong
name|streamBytesBackwardsOnSeek
decl_stmt|;
DECL|field|streamBytesRead
specifier|private
specifier|final
name|MutableCounterLong
name|streamBytesRead
decl_stmt|;
DECL|field|streamReadOperations
specifier|private
specifier|final
name|MutableCounterLong
name|streamReadOperations
decl_stmt|;
DECL|field|streamReadFullyOperations
specifier|private
specifier|final
name|MutableCounterLong
name|streamReadFullyOperations
decl_stmt|;
DECL|field|streamReadsIncomplete
specifier|private
specifier|final
name|MutableCounterLong
name|streamReadsIncomplete
decl_stmt|;
DECL|field|streamBytesReadInClose
specifier|private
specifier|final
name|MutableCounterLong
name|streamBytesReadInClose
decl_stmt|;
DECL|field|streamBytesDiscardedInAbort
specifier|private
specifier|final
name|MutableCounterLong
name|streamBytesDiscardedInAbort
decl_stmt|;
DECL|field|ignoredErrors
specifier|private
specifier|final
name|MutableCounterLong
name|ignoredErrors
decl_stmt|;
DECL|field|numberOfFilesCreated
specifier|private
specifier|final
name|MutableCounterLong
name|numberOfFilesCreated
decl_stmt|;
DECL|field|numberOfFilesCopied
specifier|private
specifier|final
name|MutableCounterLong
name|numberOfFilesCopied
decl_stmt|;
DECL|field|bytesOfFilesCopied
specifier|private
specifier|final
name|MutableCounterLong
name|bytesOfFilesCopied
decl_stmt|;
DECL|field|numberOfFilesDeleted
specifier|private
specifier|final
name|MutableCounterLong
name|numberOfFilesDeleted
decl_stmt|;
DECL|field|numberOfFakeDirectoryDeletes
specifier|private
specifier|final
name|MutableCounterLong
name|numberOfFakeDirectoryDeletes
decl_stmt|;
DECL|field|numberOfDirectoriesCreated
specifier|private
specifier|final
name|MutableCounterLong
name|numberOfDirectoriesCreated
decl_stmt|;
DECL|field|numberOfDirectoriesDeleted
specifier|private
specifier|final
name|MutableCounterLong
name|numberOfDirectoriesDeleted
decl_stmt|;
DECL|field|streamMetrics
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|MutableCounterLong
argument_list|>
name|streamMetrics
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
literal|30
argument_list|)
decl_stmt|;
DECL|field|COUNTERS_TO_CREATE
specifier|private
specifier|static
specifier|final
name|Statistic
index|[]
name|COUNTERS_TO_CREATE
init|=
block|{
name|INVOCATION_COPY_FROM_LOCAL_FILE
block|,
name|INVOCATION_EXISTS
block|,
name|INVOCATION_GET_FILE_STATUS
block|,
name|INVOCATION_GLOB_STATUS
block|,
name|INVOCATION_IS_DIRECTORY
block|,
name|INVOCATION_IS_FILE
block|,
name|INVOCATION_LIST_FILES
block|,
name|INVOCATION_LIST_LOCATED_STATUS
block|,
name|INVOCATION_LIST_STATUS
block|,
name|INVOCATION_MKDIRS
block|,
name|INVOCATION_RENAME
block|,
name|OBJECT_COPY_REQUESTS
block|,
name|OBJECT_DELETE_REQUESTS
block|,
name|OBJECT_LIST_REQUESTS
block|,
name|OBJECT_CONTINUE_LIST_REQUESTS
block|,
name|OBJECT_METADATA_REQUESTS
block|,
name|OBJECT_MULTIPART_UPLOAD_ABORTED
block|,
name|OBJECT_PUT_BYTES
block|,
name|OBJECT_PUT_REQUESTS
block|}
decl_stmt|;
DECL|method|S3AInstrumentation (URI name)
specifier|public
name|S3AInstrumentation
parameter_list|(
name|URI
name|name
parameter_list|)
block|{
name|UUID
name|fileSystemInstanceId
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
decl_stmt|;
name|registry
operator|.
name|tag
argument_list|(
literal|"FileSystemId"
argument_list|,
literal|"A unique identifier for the FS "
argument_list|,
name|fileSystemInstanceId
operator|.
name|toString
argument_list|()
operator|+
literal|"-"
operator|+
name|name
operator|.
name|getHost
argument_list|()
argument_list|)
expr_stmt|;
name|registry
operator|.
name|tag
argument_list|(
literal|"fsURI"
argument_list|,
literal|"URI of this filesystem"
argument_list|,
name|name
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|streamOpenOperations
operator|=
name|streamCounter
argument_list|(
name|STREAM_OPENED
argument_list|)
expr_stmt|;
name|streamCloseOperations
operator|=
name|streamCounter
argument_list|(
name|STREAM_CLOSE_OPERATIONS
argument_list|)
expr_stmt|;
name|streamClosed
operator|=
name|streamCounter
argument_list|(
name|STREAM_CLOSED
argument_list|)
expr_stmt|;
name|streamAborted
operator|=
name|streamCounter
argument_list|(
name|STREAM_ABORTED
argument_list|)
expr_stmt|;
name|streamSeekOperations
operator|=
name|streamCounter
argument_list|(
name|STREAM_SEEK_OPERATIONS
argument_list|)
expr_stmt|;
name|streamReadExceptions
operator|=
name|streamCounter
argument_list|(
name|STREAM_READ_EXCEPTIONS
argument_list|)
expr_stmt|;
name|streamForwardSeekOperations
operator|=
name|streamCounter
argument_list|(
name|STREAM_FORWARD_SEEK_OPERATIONS
argument_list|)
expr_stmt|;
name|streamBackwardSeekOperations
operator|=
name|streamCounter
argument_list|(
name|STREAM_BACKWARD_SEEK_OPERATIONS
argument_list|)
expr_stmt|;
name|streamBytesSkippedOnSeek
operator|=
name|streamCounter
argument_list|(
name|STREAM_SEEK_BYTES_SKIPPED
argument_list|)
expr_stmt|;
name|streamBytesBackwardsOnSeek
operator|=
name|streamCounter
argument_list|(
name|STREAM_SEEK_BYTES_BACKWARDS
argument_list|)
expr_stmt|;
name|streamBytesRead
operator|=
name|streamCounter
argument_list|(
name|STREAM_SEEK_BYTES_READ
argument_list|)
expr_stmt|;
name|streamReadOperations
operator|=
name|streamCounter
argument_list|(
name|STREAM_READ_OPERATIONS
argument_list|)
expr_stmt|;
name|streamReadFullyOperations
operator|=
name|streamCounter
argument_list|(
name|STREAM_READ_FULLY_OPERATIONS
argument_list|)
expr_stmt|;
name|streamReadsIncomplete
operator|=
name|streamCounter
argument_list|(
name|STREAM_READ_OPERATIONS_INCOMPLETE
argument_list|)
expr_stmt|;
name|streamBytesReadInClose
operator|=
name|streamCounter
argument_list|(
name|STREAM_CLOSE_BYTES_READ
argument_list|)
expr_stmt|;
name|streamBytesDiscardedInAbort
operator|=
name|streamCounter
argument_list|(
name|STREAM_ABORT_BYTES_DISCARDED
argument_list|)
expr_stmt|;
name|numberOfFilesCreated
operator|=
name|counter
argument_list|(
name|FILES_CREATED
argument_list|)
expr_stmt|;
name|numberOfFilesCopied
operator|=
name|counter
argument_list|(
name|FILES_COPIED
argument_list|)
expr_stmt|;
name|bytesOfFilesCopied
operator|=
name|counter
argument_list|(
name|FILES_COPIED_BYTES
argument_list|)
expr_stmt|;
name|numberOfFilesDeleted
operator|=
name|counter
argument_list|(
name|FILES_DELETED
argument_list|)
expr_stmt|;
name|numberOfFakeDirectoryDeletes
operator|=
name|counter
argument_list|(
name|FAKE_DIRECTORIES_DELETED
argument_list|)
expr_stmt|;
name|numberOfDirectoriesCreated
operator|=
name|counter
argument_list|(
name|DIRECTORIES_CREATED
argument_list|)
expr_stmt|;
name|numberOfDirectoriesDeleted
operator|=
name|counter
argument_list|(
name|DIRECTORIES_DELETED
argument_list|)
expr_stmt|;
name|ignoredErrors
operator|=
name|counter
argument_list|(
name|IGNORED_ERRORS
argument_list|)
expr_stmt|;
for|for
control|(
name|Statistic
name|statistic
range|:
name|COUNTERS_TO_CREATE
control|)
block|{
name|counter
argument_list|(
name|statistic
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Create a counter in the registry.    * @param name counter name    * @param desc counter description    * @return a new counter    */
DECL|method|counter (String name, String desc)
specifier|protected
specifier|final
name|MutableCounterLong
name|counter
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|desc
parameter_list|)
block|{
return|return
name|registry
operator|.
name|newCounter
argument_list|(
name|name
argument_list|,
name|desc
argument_list|,
literal|0L
argument_list|)
return|;
block|}
comment|/**    * Create a counter in the stream map: these are unregistered in the public    * metrics.    * @param name counter name    * @param desc counter description    * @return a new counter    */
DECL|method|streamCounter (String name, String desc)
specifier|protected
specifier|final
name|MutableCounterLong
name|streamCounter
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|desc
parameter_list|)
block|{
name|MutableCounterLong
name|counter
init|=
operator|new
name|MutableCounterLong
argument_list|(
name|Interns
operator|.
name|info
argument_list|(
name|name
argument_list|,
name|desc
argument_list|)
argument_list|,
literal|0L
argument_list|)
decl_stmt|;
name|streamMetrics
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|counter
argument_list|)
expr_stmt|;
return|return
name|counter
return|;
block|}
comment|/**    * Create a counter in the registry.    * @param op statistic to count    * @return a new counter    */
DECL|method|counter (Statistic op)
specifier|protected
specifier|final
name|MutableCounterLong
name|counter
parameter_list|(
name|Statistic
name|op
parameter_list|)
block|{
return|return
name|counter
argument_list|(
name|op
operator|.
name|getSymbol
argument_list|()
argument_list|,
name|op
operator|.
name|getDescription
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Create a counter in the stream map: these are unregistered in the public    * metrics.    * @param op statistic to count    * @return a new counter    */
DECL|method|streamCounter (Statistic op)
specifier|protected
specifier|final
name|MutableCounterLong
name|streamCounter
parameter_list|(
name|Statistic
name|op
parameter_list|)
block|{
return|return
name|streamCounter
argument_list|(
name|op
operator|.
name|getSymbol
argument_list|()
argument_list|,
name|op
operator|.
name|getDescription
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Create a gauge in the registry.    * @param name name gauge name    * @param desc description    * @return the gauge    */
DECL|method|gauge (String name, String desc)
specifier|protected
specifier|final
name|MutableGaugeLong
name|gauge
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|desc
parameter_list|)
block|{
return|return
name|registry
operator|.
name|newGauge
argument_list|(
name|name
argument_list|,
name|desc
argument_list|,
literal|0L
argument_list|)
return|;
block|}
comment|/**    * Get the metrics registry.    * @return the registry    */
DECL|method|getRegistry ()
specifier|public
name|MetricsRegistry
name|getRegistry
parameter_list|()
block|{
return|return
name|registry
return|;
block|}
comment|/**    * Dump all the metrics to a string.    * @param prefix prefix before every entry    * @param separator separator between name and value    * @param suffix suffix    * @param all get all the metrics even if the values are not changed.    * @return a string dump of the metrics    */
DECL|method|dump (String prefix, String separator, String suffix, boolean all)
specifier|public
name|String
name|dump
parameter_list|(
name|String
name|prefix
parameter_list|,
name|String
name|separator
parameter_list|,
name|String
name|suffix
parameter_list|,
name|boolean
name|all
parameter_list|)
block|{
name|MetricStringBuilder
name|metricBuilder
init|=
operator|new
name|MetricStringBuilder
argument_list|(
literal|null
argument_list|,
name|prefix
argument_list|,
name|separator
argument_list|,
name|suffix
argument_list|)
decl_stmt|;
name|registry
operator|.
name|snapshot
argument_list|(
name|metricBuilder
argument_list|,
name|all
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|MutableCounterLong
argument_list|>
name|entry
range|:
name|streamMetrics
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|metricBuilder
operator|.
name|tuple
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|value
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|metricBuilder
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Get the value of a counter.    * @param statistic the operation    * @return its value, or 0 if not found.    */
DECL|method|getCounterValue (Statistic statistic)
specifier|public
name|long
name|getCounterValue
parameter_list|(
name|Statistic
name|statistic
parameter_list|)
block|{
return|return
name|getCounterValue
argument_list|(
name|statistic
operator|.
name|getSymbol
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Get the value of a counter.    * If the counter is null, return 0.    * @param name the name of the counter    * @return its value.    */
DECL|method|getCounterValue (String name)
specifier|public
name|long
name|getCounterValue
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|MutableCounterLong
name|counter
init|=
name|lookupCounter
argument_list|(
name|name
argument_list|)
decl_stmt|;
return|return
name|counter
operator|==
literal|null
condition|?
literal|0
else|:
name|counter
operator|.
name|value
argument_list|()
return|;
block|}
comment|/**    * Lookup a counter by name. Return null if it is not known.    * @param name counter name    * @return the counter    */
DECL|method|lookupCounter (String name)
specifier|private
name|MutableCounterLong
name|lookupCounter
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|MutableMetric
name|metric
init|=
name|lookupMetric
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|metric
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|metric
argument_list|,
literal|"not found: "
operator|+
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
operator|(
name|metric
operator|instanceof
name|MutableCounterLong
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Metric "
operator|+
name|name
operator|+
literal|" is not a MutableCounterLong: "
operator|+
name|metric
argument_list|)
throw|;
block|}
return|return
operator|(
name|MutableCounterLong
operator|)
name|metric
return|;
block|}
comment|/**    * Look up a metric from both the registered set and the lighter weight    * stream entries.    * @param name metric name    * @return the metric or null    */
DECL|method|lookupMetric (String name)
specifier|public
name|MutableMetric
name|lookupMetric
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|MutableMetric
name|metric
init|=
name|getRegistry
argument_list|()
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|metric
operator|==
literal|null
condition|)
block|{
name|metric
operator|=
name|streamMetrics
operator|.
name|get
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
return|return
name|metric
return|;
block|}
comment|/**    * Indicate that S3A created a file.    */
DECL|method|fileCreated ()
specifier|public
name|void
name|fileCreated
parameter_list|()
block|{
name|numberOfFilesCreated
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
comment|/**    * Indicate that S3A deleted one or more file.s    * @param count number of files.    */
DECL|method|fileDeleted (int count)
specifier|public
name|void
name|fileDeleted
parameter_list|(
name|int
name|count
parameter_list|)
block|{
name|numberOfFilesDeleted
operator|.
name|incr
argument_list|(
name|count
argument_list|)
expr_stmt|;
block|}
comment|/**    * Indicate that fake directory request was made.    * @param count number of directory entries included in the delete request.    */
DECL|method|fakeDirsDeleted (int count)
specifier|public
name|void
name|fakeDirsDeleted
parameter_list|(
name|int
name|count
parameter_list|)
block|{
name|numberOfFakeDirectoryDeletes
operator|.
name|incr
argument_list|(
name|count
argument_list|)
expr_stmt|;
block|}
comment|/**    * Indicate that S3A created a directory.    */
DECL|method|directoryCreated ()
specifier|public
name|void
name|directoryCreated
parameter_list|()
block|{
name|numberOfDirectoriesCreated
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
comment|/**    * Indicate that S3A just deleted a directory.    */
DECL|method|directoryDeleted ()
specifier|public
name|void
name|directoryDeleted
parameter_list|()
block|{
name|numberOfDirectoriesDeleted
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
comment|/**    * Indicate that S3A copied some files within the store.    *    * @param files number of files    * @param size total size in bytes    */
DECL|method|filesCopied (int files, long size)
specifier|public
name|void
name|filesCopied
parameter_list|(
name|int
name|files
parameter_list|,
name|long
name|size
parameter_list|)
block|{
name|numberOfFilesCopied
operator|.
name|incr
argument_list|(
name|files
argument_list|)
expr_stmt|;
name|bytesOfFilesCopied
operator|.
name|incr
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
comment|/**    * Note that an error was ignored.    */
DECL|method|errorIgnored ()
specifier|public
name|void
name|errorIgnored
parameter_list|()
block|{
name|ignoredErrors
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
comment|/**    * Increment a specific counter.    * No-op if not defined.    * @param op operation    * @param count increment value    */
DECL|method|incrementCounter (Statistic op, long count)
specifier|public
name|void
name|incrementCounter
parameter_list|(
name|Statistic
name|op
parameter_list|,
name|long
name|count
parameter_list|)
block|{
name|MutableCounterLong
name|counter
init|=
name|lookupCounter
argument_list|(
name|op
operator|.
name|getSymbol
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|counter
operator|!=
literal|null
condition|)
block|{
name|counter
operator|.
name|incr
argument_list|(
name|count
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Create a stream input statistics instance.    * @return the new instance    */
DECL|method|newInputStreamStatistics ()
name|InputStreamStatistics
name|newInputStreamStatistics
parameter_list|()
block|{
return|return
operator|new
name|InputStreamStatistics
argument_list|()
return|;
block|}
comment|/**    * Merge in the statistics of a single input stream into    * the filesystem-wide statistics.    * @param statistics stream statistics    */
DECL|method|mergeInputStreamStatistics (InputStreamStatistics statistics)
specifier|private
name|void
name|mergeInputStreamStatistics
parameter_list|(
name|InputStreamStatistics
name|statistics
parameter_list|)
block|{
name|streamOpenOperations
operator|.
name|incr
argument_list|(
name|statistics
operator|.
name|openOperations
argument_list|)
expr_stmt|;
name|streamCloseOperations
operator|.
name|incr
argument_list|(
name|statistics
operator|.
name|closeOperations
argument_list|)
expr_stmt|;
name|streamClosed
operator|.
name|incr
argument_list|(
name|statistics
operator|.
name|closed
argument_list|)
expr_stmt|;
name|streamAborted
operator|.
name|incr
argument_list|(
name|statistics
operator|.
name|aborted
argument_list|)
expr_stmt|;
name|streamSeekOperations
operator|.
name|incr
argument_list|(
name|statistics
operator|.
name|seekOperations
argument_list|)
expr_stmt|;
name|streamReadExceptions
operator|.
name|incr
argument_list|(
name|statistics
operator|.
name|readExceptions
argument_list|)
expr_stmt|;
name|streamForwardSeekOperations
operator|.
name|incr
argument_list|(
name|statistics
operator|.
name|forwardSeekOperations
argument_list|)
expr_stmt|;
name|streamBytesSkippedOnSeek
operator|.
name|incr
argument_list|(
name|statistics
operator|.
name|bytesSkippedOnSeek
argument_list|)
expr_stmt|;
name|streamBackwardSeekOperations
operator|.
name|incr
argument_list|(
name|statistics
operator|.
name|backwardSeekOperations
argument_list|)
expr_stmt|;
name|streamBytesBackwardsOnSeek
operator|.
name|incr
argument_list|(
name|statistics
operator|.
name|bytesBackwardsOnSeek
argument_list|)
expr_stmt|;
name|streamBytesRead
operator|.
name|incr
argument_list|(
name|statistics
operator|.
name|bytesRead
argument_list|)
expr_stmt|;
name|streamReadOperations
operator|.
name|incr
argument_list|(
name|statistics
operator|.
name|readOperations
argument_list|)
expr_stmt|;
name|streamReadFullyOperations
operator|.
name|incr
argument_list|(
name|statistics
operator|.
name|readFullyOperations
argument_list|)
expr_stmt|;
name|streamReadsIncomplete
operator|.
name|incr
argument_list|(
name|statistics
operator|.
name|readsIncomplete
argument_list|)
expr_stmt|;
name|streamBytesReadInClose
operator|.
name|incr
argument_list|(
name|statistics
operator|.
name|bytesReadInClose
argument_list|)
expr_stmt|;
name|streamBytesDiscardedInAbort
operator|.
name|incr
argument_list|(
name|statistics
operator|.
name|bytesDiscardedInAbort
argument_list|)
expr_stmt|;
block|}
comment|/**    * Statistics updated by an input stream during its actual operation.    * These counters not thread-safe and are for use in a single instance    * of a stream.    */
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|InputStreamStatistics
specifier|public
specifier|final
class|class
name|InputStreamStatistics
implements|implements
name|AutoCloseable
block|{
DECL|field|openOperations
specifier|public
name|long
name|openOperations
decl_stmt|;
DECL|field|closeOperations
specifier|public
name|long
name|closeOperations
decl_stmt|;
DECL|field|closed
specifier|public
name|long
name|closed
decl_stmt|;
DECL|field|aborted
specifier|public
name|long
name|aborted
decl_stmt|;
DECL|field|seekOperations
specifier|public
name|long
name|seekOperations
decl_stmt|;
DECL|field|readExceptions
specifier|public
name|long
name|readExceptions
decl_stmt|;
DECL|field|forwardSeekOperations
specifier|public
name|long
name|forwardSeekOperations
decl_stmt|;
DECL|field|backwardSeekOperations
specifier|public
name|long
name|backwardSeekOperations
decl_stmt|;
DECL|field|bytesRead
specifier|public
name|long
name|bytesRead
decl_stmt|;
DECL|field|bytesSkippedOnSeek
specifier|public
name|long
name|bytesSkippedOnSeek
decl_stmt|;
DECL|field|bytesBackwardsOnSeek
specifier|public
name|long
name|bytesBackwardsOnSeek
decl_stmt|;
DECL|field|readOperations
specifier|public
name|long
name|readOperations
decl_stmt|;
DECL|field|readFullyOperations
specifier|public
name|long
name|readFullyOperations
decl_stmt|;
DECL|field|readsIncomplete
specifier|public
name|long
name|readsIncomplete
decl_stmt|;
DECL|field|bytesReadInClose
specifier|public
name|long
name|bytesReadInClose
decl_stmt|;
DECL|field|bytesDiscardedInAbort
specifier|public
name|long
name|bytesDiscardedInAbort
decl_stmt|;
DECL|method|InputStreamStatistics ()
specifier|private
name|InputStreamStatistics
parameter_list|()
block|{     }
comment|/**      * Seek backwards, incrementing the seek and backward seek counters.      * @param negativeOffset how far was the seek?      * This is expected to be negative.      */
DECL|method|seekBackwards (long negativeOffset)
specifier|public
name|void
name|seekBackwards
parameter_list|(
name|long
name|negativeOffset
parameter_list|)
block|{
name|seekOperations
operator|++
expr_stmt|;
name|backwardSeekOperations
operator|++
expr_stmt|;
name|bytesBackwardsOnSeek
operator|-=
name|negativeOffset
expr_stmt|;
block|}
comment|/**      * Record a forward seek, adding a seek operation, a forward      * seek operation, and any bytes skipped.      * @param skipped number of bytes skipped by reading from the stream.      * If the seek was implemented by a close + reopen, set this to zero.      */
DECL|method|seekForwards (long skipped)
specifier|public
name|void
name|seekForwards
parameter_list|(
name|long
name|skipped
parameter_list|)
block|{
name|seekOperations
operator|++
expr_stmt|;
name|forwardSeekOperations
operator|++
expr_stmt|;
if|if
condition|(
name|skipped
operator|>
literal|0
condition|)
block|{
name|bytesSkippedOnSeek
operator|+=
name|skipped
expr_stmt|;
block|}
block|}
comment|/**      * The inner stream was opened.      */
DECL|method|streamOpened ()
specifier|public
name|void
name|streamOpened
parameter_list|()
block|{
name|openOperations
operator|++
expr_stmt|;
block|}
comment|/**      * The inner stream was closed.      * @param abortedConnection flag to indicate the stream was aborted,      * rather than closed cleanly      * @param remainingInCurrentRequest the number of bytes remaining in      * the current request.      */
DECL|method|streamClose (boolean abortedConnection, long remainingInCurrentRequest)
specifier|public
name|void
name|streamClose
parameter_list|(
name|boolean
name|abortedConnection
parameter_list|,
name|long
name|remainingInCurrentRequest
parameter_list|)
block|{
name|closeOperations
operator|++
expr_stmt|;
if|if
condition|(
name|abortedConnection
condition|)
block|{
name|this
operator|.
name|aborted
operator|++
expr_stmt|;
name|bytesDiscardedInAbort
operator|+=
name|remainingInCurrentRequest
expr_stmt|;
block|}
else|else
block|{
name|closed
operator|++
expr_stmt|;
name|bytesReadInClose
operator|+=
name|remainingInCurrentRequest
expr_stmt|;
block|}
block|}
comment|/**      * An ignored stream read exception was received.      */
DECL|method|readException ()
specifier|public
name|void
name|readException
parameter_list|()
block|{
name|readExceptions
operator|++
expr_stmt|;
block|}
comment|/**      * Increment the bytes read counter by the number of bytes;      * no-op if the argument is negative.      * @param bytes number of bytes read      */
DECL|method|bytesRead (long bytes)
specifier|public
name|void
name|bytesRead
parameter_list|(
name|long
name|bytes
parameter_list|)
block|{
if|if
condition|(
name|bytes
operator|>
literal|0
condition|)
block|{
name|bytesRead
operator|+=
name|bytes
expr_stmt|;
block|}
block|}
comment|/**      * A {@code read(byte[] buf, int off, int len)} operation has started.      * @param pos starting position of the read      * @param len length of bytes to read      */
DECL|method|readOperationStarted (long pos, long len)
specifier|public
name|void
name|readOperationStarted
parameter_list|(
name|long
name|pos
parameter_list|,
name|long
name|len
parameter_list|)
block|{
name|readOperations
operator|++
expr_stmt|;
block|}
comment|/**      * A {@code PositionedRead.read(position, buffer, offset, length)}      * operation has just started.      * @param pos starting position of the read      * @param len length of bytes to read      */
DECL|method|readFullyOperationStarted (long pos, long len)
specifier|public
name|void
name|readFullyOperationStarted
parameter_list|(
name|long
name|pos
parameter_list|,
name|long
name|len
parameter_list|)
block|{
name|readFullyOperations
operator|++
expr_stmt|;
block|}
comment|/**      * A read operation has completed.      * @param requested number of requested bytes      * @param actual the actual number of bytes      */
DECL|method|readOperationCompleted (int requested, int actual)
specifier|public
name|void
name|readOperationCompleted
parameter_list|(
name|int
name|requested
parameter_list|,
name|int
name|actual
parameter_list|)
block|{
if|if
condition|(
name|requested
operator|>
name|actual
condition|)
block|{
name|readsIncomplete
operator|++
expr_stmt|;
block|}
block|}
comment|/**      * Close triggers the merge of statistics into the filesystem's      * instrumentation instance.      */
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
block|{
name|mergeInputStreamStatistics
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
comment|/**      * String operator describes all the current statistics.      *<b>Important: there are no guarantees as to the stability      * of this value.</b>      * @return the current values of the stream statistics.      */
annotation|@
name|Override
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
specifier|final
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"StreamStatistics{"
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"OpenOperations="
argument_list|)
operator|.
name|append
argument_list|(
name|openOperations
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|", CloseOperations="
argument_list|)
operator|.
name|append
argument_list|(
name|closeOperations
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|", Closed="
argument_list|)
operator|.
name|append
argument_list|(
name|closed
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|", Aborted="
argument_list|)
operator|.
name|append
argument_list|(
name|aborted
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|", SeekOperations="
argument_list|)
operator|.
name|append
argument_list|(
name|seekOperations
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|", ReadExceptions="
argument_list|)
operator|.
name|append
argument_list|(
name|readExceptions
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|", ForwardSeekOperations="
argument_list|)
operator|.
name|append
argument_list|(
name|forwardSeekOperations
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|", BackwardSeekOperations="
argument_list|)
operator|.
name|append
argument_list|(
name|backwardSeekOperations
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|", BytesSkippedOnSeek="
argument_list|)
operator|.
name|append
argument_list|(
name|bytesSkippedOnSeek
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|", BytesBackwardsOnSeek="
argument_list|)
operator|.
name|append
argument_list|(
name|bytesBackwardsOnSeek
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|", BytesRead="
argument_list|)
operator|.
name|append
argument_list|(
name|bytesRead
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|", BytesRead excluding skipped="
argument_list|)
operator|.
name|append
argument_list|(
name|bytesRead
operator|-
name|bytesSkippedOnSeek
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|", ReadOperations="
argument_list|)
operator|.
name|append
argument_list|(
name|readOperations
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|", ReadFullyOperations="
argument_list|)
operator|.
name|append
argument_list|(
name|readFullyOperations
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|", ReadsIncomplete="
argument_list|)
operator|.
name|append
argument_list|(
name|readsIncomplete
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|", BytesReadInClose="
argument_list|)
operator|.
name|append
argument_list|(
name|bytesReadInClose
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|", BytesDiscardedInAbort="
argument_list|)
operator|.
name|append
argument_list|(
name|bytesDiscardedInAbort
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|'}'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

