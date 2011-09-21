begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
package|;
end_package

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

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|interface|MRJobConfig
specifier|public
interface|interface
name|MRJobConfig
block|{
comment|// Put all of the attribute names in here so that Job and JobContext are
comment|// consistent.
DECL|field|INPUT_FORMAT_CLASS_ATTR
specifier|public
specifier|static
specifier|final
name|String
name|INPUT_FORMAT_CLASS_ATTR
init|=
literal|"mapreduce.job.inputformat.class"
decl_stmt|;
DECL|field|MAP_CLASS_ATTR
specifier|public
specifier|static
specifier|final
name|String
name|MAP_CLASS_ATTR
init|=
literal|"mapreduce.job.map.class"
decl_stmt|;
DECL|field|COMBINE_CLASS_ATTR
specifier|public
specifier|static
specifier|final
name|String
name|COMBINE_CLASS_ATTR
init|=
literal|"mapreduce.job.combine.class"
decl_stmt|;
DECL|field|REDUCE_CLASS_ATTR
specifier|public
specifier|static
specifier|final
name|String
name|REDUCE_CLASS_ATTR
init|=
literal|"mapreduce.job.reduce.class"
decl_stmt|;
DECL|field|OUTPUT_FORMAT_CLASS_ATTR
specifier|public
specifier|static
specifier|final
name|String
name|OUTPUT_FORMAT_CLASS_ATTR
init|=
literal|"mapreduce.job.outputformat.class"
decl_stmt|;
DECL|field|PARTITIONER_CLASS_ATTR
specifier|public
specifier|static
specifier|final
name|String
name|PARTITIONER_CLASS_ATTR
init|=
literal|"mapreduce.job.partitioner.class"
decl_stmt|;
DECL|field|SETUP_CLEANUP_NEEDED
specifier|public
specifier|static
specifier|final
name|String
name|SETUP_CLEANUP_NEEDED
init|=
literal|"mapreduce.job.committer.setup.cleanup.needed"
decl_stmt|;
DECL|field|TASK_CLEANUP_NEEDED
specifier|public
specifier|static
specifier|final
name|String
name|TASK_CLEANUP_NEEDED
init|=
literal|"mapreduce.job.committer.task.cleanup.needed"
decl_stmt|;
DECL|field|JAR
specifier|public
specifier|static
specifier|final
name|String
name|JAR
init|=
literal|"mapreduce.job.jar"
decl_stmt|;
DECL|field|ID
specifier|public
specifier|static
specifier|final
name|String
name|ID
init|=
literal|"mapreduce.job.id"
decl_stmt|;
DECL|field|JOB_NAME
specifier|public
specifier|static
specifier|final
name|String
name|JOB_NAME
init|=
literal|"mapreduce.job.name"
decl_stmt|;
DECL|field|JAR_UNPACK_PATTERN
specifier|public
specifier|static
specifier|final
name|String
name|JAR_UNPACK_PATTERN
init|=
literal|"mapreduce.job.jar.unpack.pattern"
decl_stmt|;
DECL|field|USER_NAME
specifier|public
specifier|static
specifier|final
name|String
name|USER_NAME
init|=
literal|"mapreduce.job.user.name"
decl_stmt|;
DECL|field|PRIORITY
specifier|public
specifier|static
specifier|final
name|String
name|PRIORITY
init|=
literal|"mapreduce.job.priority"
decl_stmt|;
DECL|field|QUEUE_NAME
specifier|public
specifier|static
specifier|final
name|String
name|QUEUE_NAME
init|=
literal|"mapreduce.job.queuename"
decl_stmt|;
DECL|field|JVM_NUMTASKS_TORUN
specifier|public
specifier|static
specifier|final
name|String
name|JVM_NUMTASKS_TORUN
init|=
literal|"mapreduce.job.jvm.numtasks"
decl_stmt|;
DECL|field|SPLIT_FILE
specifier|public
specifier|static
specifier|final
name|String
name|SPLIT_FILE
init|=
literal|"mapreduce.job.splitfile"
decl_stmt|;
DECL|field|NUM_MAPS
specifier|public
specifier|static
specifier|final
name|String
name|NUM_MAPS
init|=
literal|"mapreduce.job.maps"
decl_stmt|;
DECL|field|MAX_TASK_FAILURES_PER_TRACKER
specifier|public
specifier|static
specifier|final
name|String
name|MAX_TASK_FAILURES_PER_TRACKER
init|=
literal|"mapreduce.job.maxtaskfailures.per.tracker"
decl_stmt|;
DECL|field|COMPLETED_MAPS_FOR_REDUCE_SLOWSTART
specifier|public
specifier|static
specifier|final
name|String
name|COMPLETED_MAPS_FOR_REDUCE_SLOWSTART
init|=
literal|"mapreduce.job.reduce.slowstart.completedmaps"
decl_stmt|;
DECL|field|NUM_REDUCES
specifier|public
specifier|static
specifier|final
name|String
name|NUM_REDUCES
init|=
literal|"mapreduce.job.reduces"
decl_stmt|;
DECL|field|SKIP_RECORDS
specifier|public
specifier|static
specifier|final
name|String
name|SKIP_RECORDS
init|=
literal|"mapreduce.job.skiprecords"
decl_stmt|;
DECL|field|SKIP_OUTDIR
specifier|public
specifier|static
specifier|final
name|String
name|SKIP_OUTDIR
init|=
literal|"mapreduce.job.skip.outdir"
decl_stmt|;
DECL|field|SPECULATIVE_SLOWNODE_THRESHOLD
specifier|public
specifier|static
specifier|final
name|String
name|SPECULATIVE_SLOWNODE_THRESHOLD
init|=
literal|"mapreduce.job.speculative.slownodethreshold"
decl_stmt|;
DECL|field|SPECULATIVE_SLOWTASK_THRESHOLD
specifier|public
specifier|static
specifier|final
name|String
name|SPECULATIVE_SLOWTASK_THRESHOLD
init|=
literal|"mapreduce.job.speculative.slowtaskthreshold"
decl_stmt|;
DECL|field|SPECULATIVECAP
specifier|public
specifier|static
specifier|final
name|String
name|SPECULATIVECAP
init|=
literal|"mapreduce.job.speculative.speculativecap"
decl_stmt|;
DECL|field|JOB_LOCAL_DIR
specifier|public
specifier|static
specifier|final
name|String
name|JOB_LOCAL_DIR
init|=
literal|"mapreduce.job.local.dir"
decl_stmt|;
DECL|field|OUTPUT_KEY_CLASS
specifier|public
specifier|static
specifier|final
name|String
name|OUTPUT_KEY_CLASS
init|=
literal|"mapreduce.job.output.key.class"
decl_stmt|;
DECL|field|OUTPUT_VALUE_CLASS
specifier|public
specifier|static
specifier|final
name|String
name|OUTPUT_VALUE_CLASS
init|=
literal|"mapreduce.job.output.value.class"
decl_stmt|;
DECL|field|KEY_COMPARATOR
specifier|public
specifier|static
specifier|final
name|String
name|KEY_COMPARATOR
init|=
literal|"mapreduce.job.output.key.comparator.class"
decl_stmt|;
DECL|field|GROUP_COMPARATOR_CLASS
specifier|public
specifier|static
specifier|final
name|String
name|GROUP_COMPARATOR_CLASS
init|=
literal|"mapreduce.job.output.group.comparator.class"
decl_stmt|;
DECL|field|WORKING_DIR
specifier|public
specifier|static
specifier|final
name|String
name|WORKING_DIR
init|=
literal|"mapreduce.job.working.dir"
decl_stmt|;
DECL|field|END_NOTIFICATION_URL
specifier|public
specifier|static
specifier|final
name|String
name|END_NOTIFICATION_URL
init|=
literal|"mapreduce.job.end-notification.url"
decl_stmt|;
DECL|field|END_NOTIFICATION_RETRIES
specifier|public
specifier|static
specifier|final
name|String
name|END_NOTIFICATION_RETRIES
init|=
literal|"mapreduce.job.end-notification.retry.attempts"
decl_stmt|;
DECL|field|END_NOTIFICATION_RETRIE_INTERVAL
specifier|public
specifier|static
specifier|final
name|String
name|END_NOTIFICATION_RETRIE_INTERVAL
init|=
literal|"mapreduce.job.end-notification.retry.interval"
decl_stmt|;
DECL|field|CLASSPATH_ARCHIVES
specifier|public
specifier|static
specifier|final
name|String
name|CLASSPATH_ARCHIVES
init|=
literal|"mapreduce.job.classpath.archives"
decl_stmt|;
DECL|field|CLASSPATH_FILES
specifier|public
specifier|static
specifier|final
name|String
name|CLASSPATH_FILES
init|=
literal|"mapreduce.job.classpath.files"
decl_stmt|;
DECL|field|CACHE_FILES
specifier|public
specifier|static
specifier|final
name|String
name|CACHE_FILES
init|=
literal|"mapreduce.job.cache.files"
decl_stmt|;
DECL|field|CACHE_ARCHIVES
specifier|public
specifier|static
specifier|final
name|String
name|CACHE_ARCHIVES
init|=
literal|"mapreduce.job.cache.archives"
decl_stmt|;
DECL|field|CACHE_FILES_SIZES
specifier|public
specifier|static
specifier|final
name|String
name|CACHE_FILES_SIZES
init|=
literal|"mapreduce.job.cache.files.filesizes"
decl_stmt|;
comment|// internal use only
DECL|field|CACHE_ARCHIVES_SIZES
specifier|public
specifier|static
specifier|final
name|String
name|CACHE_ARCHIVES_SIZES
init|=
literal|"mapreduce.job.cache.archives.filesizes"
decl_stmt|;
comment|// ditto
DECL|field|CACHE_LOCALFILES
specifier|public
specifier|static
specifier|final
name|String
name|CACHE_LOCALFILES
init|=
literal|"mapreduce.job.cache.local.files"
decl_stmt|;
DECL|field|CACHE_LOCALARCHIVES
specifier|public
specifier|static
specifier|final
name|String
name|CACHE_LOCALARCHIVES
init|=
literal|"mapreduce.job.cache.local.archives"
decl_stmt|;
DECL|field|CACHE_FILE_TIMESTAMPS
specifier|public
specifier|static
specifier|final
name|String
name|CACHE_FILE_TIMESTAMPS
init|=
literal|"mapreduce.job.cache.files.timestamps"
decl_stmt|;
DECL|field|CACHE_ARCHIVES_TIMESTAMPS
specifier|public
specifier|static
specifier|final
name|String
name|CACHE_ARCHIVES_TIMESTAMPS
init|=
literal|"mapreduce.job.cache.archives.timestamps"
decl_stmt|;
DECL|field|CACHE_FILE_VISIBILITIES
specifier|public
specifier|static
specifier|final
name|String
name|CACHE_FILE_VISIBILITIES
init|=
literal|"mapreduce.job.cache.files.visibilities"
decl_stmt|;
DECL|field|CACHE_ARCHIVES_VISIBILITIES
specifier|public
specifier|static
specifier|final
name|String
name|CACHE_ARCHIVES_VISIBILITIES
init|=
literal|"mapreduce.job.cache.archives.visibilities"
decl_stmt|;
DECL|field|CACHE_SYMLINK
specifier|public
specifier|static
specifier|final
name|String
name|CACHE_SYMLINK
init|=
literal|"mapreduce.job.cache.symlink.create"
decl_stmt|;
DECL|field|USER_LOG_RETAIN_HOURS
specifier|public
specifier|static
specifier|final
name|String
name|USER_LOG_RETAIN_HOURS
init|=
literal|"mapreduce.job.userlog.retain.hours"
decl_stmt|;
DECL|field|IO_SORT_FACTOR
specifier|public
specifier|static
specifier|final
name|String
name|IO_SORT_FACTOR
init|=
literal|"mapreduce.task.io.sort.factor"
decl_stmt|;
DECL|field|IO_SORT_MB
specifier|public
specifier|static
specifier|final
name|String
name|IO_SORT_MB
init|=
literal|"mapreduce.task.io.sort.mb"
decl_stmt|;
DECL|field|INDEX_CACHE_MEMORY_LIMIT
specifier|public
specifier|static
specifier|final
name|String
name|INDEX_CACHE_MEMORY_LIMIT
init|=
literal|"mapreduce.task.index.cache.limit.bytes"
decl_stmt|;
DECL|field|PRESERVE_FAILED_TASK_FILES
specifier|public
specifier|static
specifier|final
name|String
name|PRESERVE_FAILED_TASK_FILES
init|=
literal|"mapreduce.task.files.preserve.failedtasks"
decl_stmt|;
DECL|field|PRESERVE_FILES_PATTERN
specifier|public
specifier|static
specifier|final
name|String
name|PRESERVE_FILES_PATTERN
init|=
literal|"mapreduce.task.files.preserve.filepattern"
decl_stmt|;
DECL|field|TASK_TEMP_DIR
specifier|public
specifier|static
specifier|final
name|String
name|TASK_TEMP_DIR
init|=
literal|"mapreduce.task.tmp.dir"
decl_stmt|;
DECL|field|TASK_DEBUGOUT_LINES
specifier|public
specifier|static
specifier|final
name|String
name|TASK_DEBUGOUT_LINES
init|=
literal|"mapreduce.task.debugout.lines"
decl_stmt|;
DECL|field|RECORDS_BEFORE_PROGRESS
specifier|public
specifier|static
specifier|final
name|String
name|RECORDS_BEFORE_PROGRESS
init|=
literal|"mapreduce.task.merge.progress.records"
decl_stmt|;
DECL|field|SKIP_START_ATTEMPTS
specifier|public
specifier|static
specifier|final
name|String
name|SKIP_START_ATTEMPTS
init|=
literal|"mapreduce.task.skip.start.attempts"
decl_stmt|;
DECL|field|TASK_ATTEMPT_ID
specifier|public
specifier|static
specifier|final
name|String
name|TASK_ATTEMPT_ID
init|=
literal|"mapreduce.task.attempt.id"
decl_stmt|;
DECL|field|TASK_ISMAP
specifier|public
specifier|static
specifier|final
name|String
name|TASK_ISMAP
init|=
literal|"mapreduce.task.ismap"
decl_stmt|;
DECL|field|TASK_PARTITION
specifier|public
specifier|static
specifier|final
name|String
name|TASK_PARTITION
init|=
literal|"mapreduce.task.partition"
decl_stmt|;
DECL|field|TASK_PROFILE
specifier|public
specifier|static
specifier|final
name|String
name|TASK_PROFILE
init|=
literal|"mapreduce.task.profile"
decl_stmt|;
DECL|field|TASK_PROFILE_PARAMS
specifier|public
specifier|static
specifier|final
name|String
name|TASK_PROFILE_PARAMS
init|=
literal|"mapreduce.task.profile.params"
decl_stmt|;
DECL|field|NUM_MAP_PROFILES
specifier|public
specifier|static
specifier|final
name|String
name|NUM_MAP_PROFILES
init|=
literal|"mapreduce.task.profile.maps"
decl_stmt|;
DECL|field|NUM_REDUCE_PROFILES
specifier|public
specifier|static
specifier|final
name|String
name|NUM_REDUCE_PROFILES
init|=
literal|"mapreduce.task.profile.reduces"
decl_stmt|;
DECL|field|TASK_TIMEOUT
specifier|public
specifier|static
specifier|final
name|String
name|TASK_TIMEOUT
init|=
literal|"mapreduce.task.timeout"
decl_stmt|;
DECL|field|TASK_ID
specifier|public
specifier|static
specifier|final
name|String
name|TASK_ID
init|=
literal|"mapreduce.task.id"
decl_stmt|;
DECL|field|TASK_OUTPUT_DIR
specifier|public
specifier|static
specifier|final
name|String
name|TASK_OUTPUT_DIR
init|=
literal|"mapreduce.task.output.dir"
decl_stmt|;
DECL|field|TASK_USERLOG_LIMIT
specifier|public
specifier|static
specifier|final
name|String
name|TASK_USERLOG_LIMIT
init|=
literal|"mapreduce.task.userlog.limit.kb"
decl_stmt|;
DECL|field|MAP_SORT_SPILL_PERCENT
specifier|public
specifier|static
specifier|final
name|String
name|MAP_SORT_SPILL_PERCENT
init|=
literal|"mapreduce.map.sort.spill.percent"
decl_stmt|;
DECL|field|MAP_INPUT_FILE
specifier|public
specifier|static
specifier|final
name|String
name|MAP_INPUT_FILE
init|=
literal|"mapreduce.map.input.file"
decl_stmt|;
DECL|field|MAP_INPUT_PATH
specifier|public
specifier|static
specifier|final
name|String
name|MAP_INPUT_PATH
init|=
literal|"mapreduce.map.input.length"
decl_stmt|;
DECL|field|MAP_INPUT_START
specifier|public
specifier|static
specifier|final
name|String
name|MAP_INPUT_START
init|=
literal|"mapreduce.map.input.start"
decl_stmt|;
DECL|field|MAP_MEMORY_MB
specifier|public
specifier|static
specifier|final
name|String
name|MAP_MEMORY_MB
init|=
literal|"mapreduce.map.memory.mb"
decl_stmt|;
DECL|field|MAP_MEMORY_PHYSICAL_MB
specifier|public
specifier|static
specifier|final
name|String
name|MAP_MEMORY_PHYSICAL_MB
init|=
literal|"mapreduce.map.memory.physical.mb"
decl_stmt|;
DECL|field|MAP_ENV
specifier|public
specifier|static
specifier|final
name|String
name|MAP_ENV
init|=
literal|"mapreduce.map.env"
decl_stmt|;
DECL|field|MAP_JAVA_OPTS
specifier|public
specifier|static
specifier|final
name|String
name|MAP_JAVA_OPTS
init|=
literal|"mapreduce.map.java.opts"
decl_stmt|;
DECL|field|MAP_ULIMIT
specifier|public
specifier|static
specifier|final
name|String
name|MAP_ULIMIT
init|=
literal|"mapreduce.map.ulimit"
decl_stmt|;
DECL|field|MAP_MAX_ATTEMPTS
specifier|public
specifier|static
specifier|final
name|String
name|MAP_MAX_ATTEMPTS
init|=
literal|"mapreduce.map.maxattempts"
decl_stmt|;
DECL|field|MAP_DEBUG_SCRIPT
specifier|public
specifier|static
specifier|final
name|String
name|MAP_DEBUG_SCRIPT
init|=
literal|"mapreduce.map.debug.script"
decl_stmt|;
DECL|field|MAP_SPECULATIVE
specifier|public
specifier|static
specifier|final
name|String
name|MAP_SPECULATIVE
init|=
literal|"mapreduce.map.speculative"
decl_stmt|;
DECL|field|MAP_FAILURES_MAX_PERCENT
specifier|public
specifier|static
specifier|final
name|String
name|MAP_FAILURES_MAX_PERCENT
init|=
literal|"mapreduce.map.failures.maxpercent"
decl_stmt|;
DECL|field|MAP_SKIP_INCR_PROC_COUNT
specifier|public
specifier|static
specifier|final
name|String
name|MAP_SKIP_INCR_PROC_COUNT
init|=
literal|"mapreduce.map.skip.proc-count.auto-incr"
decl_stmt|;
DECL|field|MAP_SKIP_MAX_RECORDS
specifier|public
specifier|static
specifier|final
name|String
name|MAP_SKIP_MAX_RECORDS
init|=
literal|"mapreduce.map.skip.maxrecords"
decl_stmt|;
DECL|field|MAP_COMBINE_MIN_SPILLS
specifier|public
specifier|static
specifier|final
name|String
name|MAP_COMBINE_MIN_SPILLS
init|=
literal|"mapreduce.map.combine.minspills"
decl_stmt|;
DECL|field|MAP_OUTPUT_COMPRESS
specifier|public
specifier|static
specifier|final
name|String
name|MAP_OUTPUT_COMPRESS
init|=
literal|"mapreduce.map.output.compress"
decl_stmt|;
DECL|field|MAP_OUTPUT_COMPRESS_CODEC
specifier|public
specifier|static
specifier|final
name|String
name|MAP_OUTPUT_COMPRESS_CODEC
init|=
literal|"mapreduce.map.output.compress.codec"
decl_stmt|;
DECL|field|MAP_OUTPUT_KEY_CLASS
specifier|public
specifier|static
specifier|final
name|String
name|MAP_OUTPUT_KEY_CLASS
init|=
literal|"mapreduce.map.output.key.class"
decl_stmt|;
DECL|field|MAP_OUTPUT_VALUE_CLASS
specifier|public
specifier|static
specifier|final
name|String
name|MAP_OUTPUT_VALUE_CLASS
init|=
literal|"mapreduce.map.output.value.class"
decl_stmt|;
DECL|field|MAP_OUTPUT_KEY_FIELD_SEPERATOR
specifier|public
specifier|static
specifier|final
name|String
name|MAP_OUTPUT_KEY_FIELD_SEPERATOR
init|=
literal|"mapreduce.map.output.key.field.separator"
decl_stmt|;
DECL|field|MAP_LOG_LEVEL
specifier|public
specifier|static
specifier|final
name|String
name|MAP_LOG_LEVEL
init|=
literal|"mapreduce.map.log.level"
decl_stmt|;
DECL|field|REDUCE_LOG_LEVEL
specifier|public
specifier|static
specifier|final
name|String
name|REDUCE_LOG_LEVEL
init|=
literal|"mapreduce.reduce.log.level"
decl_stmt|;
DECL|field|DEFAULT_LOG_LEVEL
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_LOG_LEVEL
init|=
literal|"INFO"
decl_stmt|;
DECL|field|REDUCE_MERGE_INMEM_THRESHOLD
specifier|public
specifier|static
specifier|final
name|String
name|REDUCE_MERGE_INMEM_THRESHOLD
init|=
literal|"mapreduce.reduce.merge.inmem.threshold"
decl_stmt|;
DECL|field|REDUCE_INPUT_BUFFER_PERCENT
specifier|public
specifier|static
specifier|final
name|String
name|REDUCE_INPUT_BUFFER_PERCENT
init|=
literal|"mapreduce.reduce.input.buffer.percent"
decl_stmt|;
DECL|field|REDUCE_MARKRESET_BUFFER_PERCENT
specifier|public
specifier|static
specifier|final
name|String
name|REDUCE_MARKRESET_BUFFER_PERCENT
init|=
literal|"mapreduce.reduce.markreset.buffer.percent"
decl_stmt|;
DECL|field|REDUCE_MARKRESET_BUFFER_SIZE
specifier|public
specifier|static
specifier|final
name|String
name|REDUCE_MARKRESET_BUFFER_SIZE
init|=
literal|"mapreduce.reduce.markreset.buffer.size"
decl_stmt|;
DECL|field|REDUCE_MEMORY_PHYSICAL_MB
specifier|public
specifier|static
specifier|final
name|String
name|REDUCE_MEMORY_PHYSICAL_MB
init|=
literal|"mapreduce.reduce.memory.physical.mb"
decl_stmt|;
DECL|field|REDUCE_MEMORY_MB
specifier|public
specifier|static
specifier|final
name|String
name|REDUCE_MEMORY_MB
init|=
literal|"mapreduce.reduce.memory.mb"
decl_stmt|;
DECL|field|REDUCE_MEMORY_TOTAL_BYTES
specifier|public
specifier|static
specifier|final
name|String
name|REDUCE_MEMORY_TOTAL_BYTES
init|=
literal|"mapreduce.reduce.memory.totalbytes"
decl_stmt|;
DECL|field|SHUFFLE_INPUT_BUFFER_PERCENT
specifier|public
specifier|static
specifier|final
name|String
name|SHUFFLE_INPUT_BUFFER_PERCENT
init|=
literal|"mapreduce.reduce.shuffle.input.buffer.percent"
decl_stmt|;
DECL|field|SHUFFLE_MERGE_EPRCENT
specifier|public
specifier|static
specifier|final
name|String
name|SHUFFLE_MERGE_EPRCENT
init|=
literal|"mapreduce.reduce.shuffle.merge.percent"
decl_stmt|;
DECL|field|REDUCE_FAILURES_MAXPERCENT
specifier|public
specifier|static
specifier|final
name|String
name|REDUCE_FAILURES_MAXPERCENT
init|=
literal|"mapreduce.reduce.failures.maxpercent"
decl_stmt|;
DECL|field|REDUCE_ENV
specifier|public
specifier|static
specifier|final
name|String
name|REDUCE_ENV
init|=
literal|"mapreduce.reduce.env"
decl_stmt|;
DECL|field|REDUCE_JAVA_OPTS
specifier|public
specifier|static
specifier|final
name|String
name|REDUCE_JAVA_OPTS
init|=
literal|"mapreduce.reduce.java.opts"
decl_stmt|;
DECL|field|REDUCE_ULIMIT
specifier|public
specifier|static
specifier|final
name|String
name|REDUCE_ULIMIT
init|=
literal|"mapreduce.reduce.ulimit"
decl_stmt|;
DECL|field|REDUCE_MAX_ATTEMPTS
specifier|public
specifier|static
specifier|final
name|String
name|REDUCE_MAX_ATTEMPTS
init|=
literal|"mapreduce.reduce.maxattempts"
decl_stmt|;
DECL|field|SHUFFLE_PARALLEL_COPIES
specifier|public
specifier|static
specifier|final
name|String
name|SHUFFLE_PARALLEL_COPIES
init|=
literal|"mapreduce.reduce.shuffle.parallelcopies"
decl_stmt|;
DECL|field|REDUCE_DEBUG_SCRIPT
specifier|public
specifier|static
specifier|final
name|String
name|REDUCE_DEBUG_SCRIPT
init|=
literal|"mapreduce.reduce.debug.script"
decl_stmt|;
DECL|field|REDUCE_SPECULATIVE
specifier|public
specifier|static
specifier|final
name|String
name|REDUCE_SPECULATIVE
init|=
literal|"mapreduce.reduce.speculative"
decl_stmt|;
DECL|field|SHUFFLE_CONNECT_TIMEOUT
specifier|public
specifier|static
specifier|final
name|String
name|SHUFFLE_CONNECT_TIMEOUT
init|=
literal|"mapreduce.reduce.shuffle.connect.timeout"
decl_stmt|;
DECL|field|SHUFFLE_READ_TIMEOUT
specifier|public
specifier|static
specifier|final
name|String
name|SHUFFLE_READ_TIMEOUT
init|=
literal|"mapreduce.reduce.shuffle.read.timeout"
decl_stmt|;
DECL|field|SHUFFLE_FETCH_FAILURES
specifier|public
specifier|static
specifier|final
name|String
name|SHUFFLE_FETCH_FAILURES
init|=
literal|"mapreduce.reduce.shuffle.maxfetchfailures"
decl_stmt|;
DECL|field|SHUFFLE_NOTIFY_READERROR
specifier|public
specifier|static
specifier|final
name|String
name|SHUFFLE_NOTIFY_READERROR
init|=
literal|"mapreduce.reduce.shuffle.notify.readerror"
decl_stmt|;
DECL|field|REDUCE_SKIP_INCR_PROC_COUNT
specifier|public
specifier|static
specifier|final
name|String
name|REDUCE_SKIP_INCR_PROC_COUNT
init|=
literal|"mapreduce.reduce.skip.proc-count.auto-incr"
decl_stmt|;
DECL|field|REDUCE_SKIP_MAXGROUPS
specifier|public
specifier|static
specifier|final
name|String
name|REDUCE_SKIP_MAXGROUPS
init|=
literal|"mapreduce.reduce.skip.maxgroups"
decl_stmt|;
DECL|field|REDUCE_MEMTOMEM_THRESHOLD
specifier|public
specifier|static
specifier|final
name|String
name|REDUCE_MEMTOMEM_THRESHOLD
init|=
literal|"mapreduce.reduce.merge.memtomem.threshold"
decl_stmt|;
DECL|field|REDUCE_MEMTOMEM_ENABLED
specifier|public
specifier|static
specifier|final
name|String
name|REDUCE_MEMTOMEM_ENABLED
init|=
literal|"mapreduce.reduce.merge.memtomem.enabled"
decl_stmt|;
DECL|field|COMBINE_RECORDS_BEFORE_PROGRESS
specifier|public
specifier|static
specifier|final
name|String
name|COMBINE_RECORDS_BEFORE_PROGRESS
init|=
literal|"mapreduce.task.combine.progress.records"
decl_stmt|;
DECL|field|JOB_NAMENODES
specifier|public
specifier|static
specifier|final
name|String
name|JOB_NAMENODES
init|=
literal|"mapreduce.job.hdfs-servers"
decl_stmt|;
DECL|field|JOB_JOBTRACKER_ID
specifier|public
specifier|static
specifier|final
name|String
name|JOB_JOBTRACKER_ID
init|=
literal|"mapreduce.job.kerberos.jtprinicipal"
decl_stmt|;
DECL|field|JOB_CANCEL_DELEGATION_TOKEN
specifier|public
specifier|static
specifier|final
name|String
name|JOB_CANCEL_DELEGATION_TOKEN
init|=
literal|"mapreduce.job.complete.cancel.delegation.tokens"
decl_stmt|;
DECL|field|JOB_ACL_VIEW_JOB
specifier|public
specifier|static
specifier|final
name|String
name|JOB_ACL_VIEW_JOB
init|=
literal|"mapreduce.job.acl-view-job"
decl_stmt|;
DECL|field|JOB_ACL_MODIFY_JOB
specifier|public
specifier|static
specifier|final
name|String
name|JOB_ACL_MODIFY_JOB
init|=
literal|"mapreduce.job.acl-modify-job"
decl_stmt|;
DECL|field|JOB_SUBMITHOST
specifier|public
specifier|static
specifier|final
name|String
name|JOB_SUBMITHOST
init|=
literal|"mapreduce.job.submithostname"
decl_stmt|;
DECL|field|JOB_SUBMITHOSTADDR
specifier|public
specifier|static
specifier|final
name|String
name|JOB_SUBMITHOSTADDR
init|=
literal|"mapreduce.job.submithostaddress"
decl_stmt|;
DECL|field|COUNTERS_MAX_KEY
specifier|public
specifier|static
specifier|final
name|String
name|COUNTERS_MAX_KEY
init|=
literal|"mapreduce.job.counters.max"
decl_stmt|;
DECL|field|COUNTERS_MAX_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|COUNTERS_MAX_DEFAULT
init|=
literal|120
decl_stmt|;
DECL|field|COUNTER_GROUP_NAME_MAX_KEY
specifier|public
specifier|static
specifier|final
name|String
name|COUNTER_GROUP_NAME_MAX_KEY
init|=
literal|"mapreduce.job.counters.group.name.max"
decl_stmt|;
DECL|field|COUNTER_GROUP_NAME_MAX_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|COUNTER_GROUP_NAME_MAX_DEFAULT
init|=
literal|128
decl_stmt|;
DECL|field|COUNTER_NAME_MAX_KEY
specifier|public
specifier|static
specifier|final
name|String
name|COUNTER_NAME_MAX_KEY
init|=
literal|"mapreduce.job.counters.counter.name.max"
decl_stmt|;
DECL|field|COUNTER_NAME_MAX_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|COUNTER_NAME_MAX_DEFAULT
init|=
literal|64
decl_stmt|;
DECL|field|COUNTER_GROUPS_MAX_KEY
specifier|public
specifier|static
specifier|final
name|String
name|COUNTER_GROUPS_MAX_KEY
init|=
literal|"mapreduce.job.counters.groups.max"
decl_stmt|;
DECL|field|COUNTER_GROUPS_MAX_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|COUNTER_GROUPS_MAX_DEFAULT
init|=
literal|50
decl_stmt|;
DECL|field|JOB_UBERTASK_ENABLE
specifier|public
specifier|static
specifier|final
name|String
name|JOB_UBERTASK_ENABLE
init|=
literal|"mapreduce.job.ubertask.enable"
decl_stmt|;
DECL|field|JOB_UBERTASK_MAXMAPS
specifier|public
specifier|static
specifier|final
name|String
name|JOB_UBERTASK_MAXMAPS
init|=
literal|"mapreduce.job.ubertask.maxmaps"
decl_stmt|;
DECL|field|JOB_UBERTASK_MAXREDUCES
specifier|public
specifier|static
specifier|final
name|String
name|JOB_UBERTASK_MAXREDUCES
init|=
literal|"mapreduce.job.ubertask.maxreduces"
decl_stmt|;
DECL|field|JOB_UBERTASK_MAXBYTES
specifier|public
specifier|static
specifier|final
name|String
name|JOB_UBERTASK_MAXBYTES
init|=
literal|"mapreduce.job.ubertask.maxbytes"
decl_stmt|;
DECL|field|UBERTASK_JAVA_OPTS
specifier|public
specifier|static
specifier|final
name|String
name|UBERTASK_JAVA_OPTS
init|=
literal|"mapreduce.ubertask.child.java.opts"
decl_stmt|;
comment|// or mapreduce.uber.java.opts?
DECL|field|UBERTASK_ULIMIT
specifier|public
specifier|static
specifier|final
name|String
name|UBERTASK_ULIMIT
init|=
literal|"mapreduce.ubertask.child.ulimit"
decl_stmt|;
comment|// or mapreduce.uber.ulimit?
DECL|field|UBERTASK_ENV
specifier|public
specifier|static
specifier|final
name|String
name|UBERTASK_ENV
init|=
literal|"mapreduce.ubertask.child.env"
decl_stmt|;
comment|// or mapreduce.uber.env?
DECL|field|MR_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|MR_PREFIX
init|=
literal|"yarn.app.mapreduce."
decl_stmt|;
DECL|field|MR_AM_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|MR_AM_PREFIX
init|=
name|MR_PREFIX
operator|+
literal|"am."
decl_stmt|;
comment|/** The staging directory for map reduce.*/
DECL|field|MR_AM_STAGING_DIR
specifier|public
specifier|static
specifier|final
name|String
name|MR_AM_STAGING_DIR
init|=
name|MR_AM_PREFIX
operator|+
literal|"staging-dir"
decl_stmt|;
comment|/** The amount of memory the MR app master needs.*/
DECL|field|MR_AM_VMEM_MB
specifier|public
specifier|static
specifier|final
name|String
name|MR_AM_VMEM_MB
init|=
name|MR_AM_PREFIX
operator|+
literal|"resource.mb"
decl_stmt|;
DECL|field|DEFAULT_MR_AM_VMEM_MB
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MR_AM_VMEM_MB
init|=
literal|2048
decl_stmt|;
comment|/** Command line arguments passed to the MR app master.*/
DECL|field|MR_AM_COMMAND_OPTS
specifier|public
specifier|static
specifier|final
name|String
name|MR_AM_COMMAND_OPTS
init|=
name|MR_AM_PREFIX
operator|+
literal|"command-opts"
decl_stmt|;
DECL|field|DEFAULT_MR_AM_COMMAND_OPTS
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_MR_AM_COMMAND_OPTS
init|=
literal|"-Xmx1536m"
decl_stmt|;
comment|/** Root Logging level passed to the MR app master.*/
DECL|field|MR_AM_LOG_OPTS
specifier|public
specifier|static
specifier|final
name|String
name|MR_AM_LOG_OPTS
init|=
name|MR_AM_PREFIX
operator|+
literal|"log-opts"
decl_stmt|;
DECL|field|DEFAULT_MR_AM_LOG_OPTS
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_MR_AM_LOG_OPTS
init|=
literal|"INFO"
decl_stmt|;
comment|/**The number of splits when reporting progress in MR*/
DECL|field|MR_AM_NUM_PROGRESS_SPLITS
specifier|public
specifier|static
specifier|final
name|String
name|MR_AM_NUM_PROGRESS_SPLITS
init|=
name|MR_AM_PREFIX
operator|+
literal|"num-progress-splits"
decl_stmt|;
DECL|field|DEFAULT_MR_AM_NUM_PROGRESS_SPLITS
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MR_AM_NUM_PROGRESS_SPLITS
init|=
literal|12
decl_stmt|;
comment|/** Number of threads user to launch containers in the app master.*/
DECL|field|MR_AM_CONTAINERLAUNCHER_THREAD_COUNT
specifier|public
specifier|static
specifier|final
name|String
name|MR_AM_CONTAINERLAUNCHER_THREAD_COUNT
init|=
name|MR_AM_PREFIX
operator|+
literal|"containerlauncher.thread-count"
decl_stmt|;
comment|/** Number of threads to handle job client RPC requests.*/
DECL|field|MR_AM_JOB_CLIENT_THREAD_COUNT
specifier|public
specifier|static
specifier|final
name|String
name|MR_AM_JOB_CLIENT_THREAD_COUNT
init|=
name|MR_AM_PREFIX
operator|+
literal|"job.client.thread-count"
decl_stmt|;
DECL|field|DEFAULT_MR_AM_JOB_CLIENT_THREAD_COUNT
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MR_AM_JOB_CLIENT_THREAD_COUNT
init|=
literal|1
decl_stmt|;
comment|/** Enable blacklisting of nodes in the job.*/
DECL|field|MR_AM_JOB_NODE_BLACKLISTING_ENABLE
specifier|public
specifier|static
specifier|final
name|String
name|MR_AM_JOB_NODE_BLACKLISTING_ENABLE
init|=
name|MR_AM_PREFIX
operator|+
literal|"job.node.blacklisting.enable"
decl_stmt|;
comment|/** Enable job recovery.*/
DECL|field|MR_AM_JOB_RECOVERY_ENABLE
specifier|public
specifier|static
specifier|final
name|String
name|MR_AM_JOB_RECOVERY_ENABLE
init|=
name|MR_AM_PREFIX
operator|+
literal|"job.recovery.enable"
decl_stmt|;
comment|/**     * Limit on the number of reducers that can be preempted to ensure that at    *  least one map task can run if it needs to. Percentage between 0.0 and 1.0    */
DECL|field|MR_AM_JOB_REDUCE_PREEMPTION_LIMIT
specifier|public
specifier|static
specifier|final
name|String
name|MR_AM_JOB_REDUCE_PREEMPTION_LIMIT
init|=
name|MR_AM_PREFIX
operator|+
literal|"job.reduce.preemption.limit"
decl_stmt|;
DECL|field|DEFAULT_MR_AM_JOB_REDUCE_PREEMPTION_LIMIT
specifier|public
specifier|static
specifier|final
name|float
name|DEFAULT_MR_AM_JOB_REDUCE_PREEMPTION_LIMIT
init|=
literal|0.5f
decl_stmt|;
comment|/**    * Limit reduces starting until a certain percentage of maps have finished.    *  Percentage between 0.0 and 1.0    */
DECL|field|MR_AM_JOB_REDUCE_RAMPUP_UP_LIMIT
specifier|public
specifier|static
specifier|final
name|String
name|MR_AM_JOB_REDUCE_RAMPUP_UP_LIMIT
init|=
name|MR_AM_PREFIX
operator|+
literal|"job.reduce.rampup.limit"
decl_stmt|;
DECL|field|DEFAULT_MR_AM_JOB_REDUCE_RAMP_UP_LIMIT
specifier|public
specifier|static
specifier|final
name|float
name|DEFAULT_MR_AM_JOB_REDUCE_RAMP_UP_LIMIT
init|=
literal|0.5f
decl_stmt|;
comment|/** The class that should be used for speculative execution calculations.*/
DECL|field|MR_AM_JOB_SPECULATOR
specifier|public
specifier|static
specifier|final
name|String
name|MR_AM_JOB_SPECULATOR
init|=
name|MR_AM_PREFIX
operator|+
literal|"job.speculator.class"
decl_stmt|;
comment|/** Class used to estimate task resource needs.*/
DECL|field|MR_AM_TASK_ESTIMATOR
specifier|public
specifier|static
specifier|final
name|String
name|MR_AM_TASK_ESTIMATOR
init|=
name|MR_AM_PREFIX
operator|+
literal|"job.task.estimator.class"
decl_stmt|;
comment|/** The lambda value in the smoothing function of the task estimator.*/
DECL|field|MR_AM_TASK_ESTIMATOR_SMOOTH_LAMBDA_MS
specifier|public
specifier|static
specifier|final
name|String
name|MR_AM_TASK_ESTIMATOR_SMOOTH_LAMBDA_MS
init|=
name|MR_AM_PREFIX
operator|+
literal|"job.task.estimator.exponential.smooth.lambda-ms"
decl_stmt|;
DECL|field|DEFAULT_MR_AM_TASK_ESTIMATOR_SMNOOTH_LAMBDA_MS
specifier|public
specifier|static
specifier|final
name|long
name|DEFAULT_MR_AM_TASK_ESTIMATOR_SMNOOTH_LAMBDA_MS
init|=
literal|1000L
operator|*
literal|60
decl_stmt|;
comment|/** true if the smoothing rate should be exponential.*/
DECL|field|MR_AM_TASK_EXTIMATOR_EXPONENTIAL_RATE_ENABLE
specifier|public
specifier|static
specifier|final
name|String
name|MR_AM_TASK_EXTIMATOR_EXPONENTIAL_RATE_ENABLE
init|=
name|MR_AM_PREFIX
operator|+
literal|"job.task.estimator.exponential.smooth.rate"
decl_stmt|;
comment|/** The number of threads used to handle task RPC calls.*/
DECL|field|MR_AM_TASK_LISTENER_THREAD_COUNT
specifier|public
specifier|static
specifier|final
name|String
name|MR_AM_TASK_LISTENER_THREAD_COUNT
init|=
name|MR_AM_PREFIX
operator|+
literal|"job.task.listener.thread-count"
decl_stmt|;
DECL|field|DEFAULT_MR_AM_TASK_LISTENER_THREAD_COUNT
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MR_AM_TASK_LISTENER_THREAD_COUNT
init|=
literal|10
decl_stmt|;
comment|/** How often the AM should send heartbeats to the RM.*/
DECL|field|MR_AM_TO_RM_HEARTBEAT_INTERVAL_MS
specifier|public
specifier|static
specifier|final
name|String
name|MR_AM_TO_RM_HEARTBEAT_INTERVAL_MS
init|=
name|MR_AM_PREFIX
operator|+
literal|"scheduler.heartbeat.interval-ms"
decl_stmt|;
DECL|field|DEFAULT_MR_AM_TO_RM_HEARTBEAT_INTERVAL_MS
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MR_AM_TO_RM_HEARTBEAT_INTERVAL_MS
init|=
literal|2000
decl_stmt|;
comment|/**    * Boolean. Create the base dirs in the JobHistoryEventHandler    * Set to false for multi-user clusters.  This is an internal config that    * is set by the MR framework and read by it too.    */
DECL|field|MR_AM_CREATE_JH_INTERMEDIATE_BASE_DIR
specifier|public
specifier|static
specifier|final
name|String
name|MR_AM_CREATE_JH_INTERMEDIATE_BASE_DIR
init|=
name|MR_AM_PREFIX
operator|+
literal|"create-intermediate-jh-base-dir"
decl_stmt|;
DECL|field|MAPRED_MAP_ADMIN_JAVA_OPTS
specifier|public
specifier|static
specifier|final
name|String
name|MAPRED_MAP_ADMIN_JAVA_OPTS
init|=
literal|"mapreduce.admin.map.child.java.opts"
decl_stmt|;
DECL|field|MAPRED_REDUCE_ADMIN_JAVA_OPTS
specifier|public
specifier|static
specifier|final
name|String
name|MAPRED_REDUCE_ADMIN_JAVA_OPTS
init|=
literal|"mapreduce.admin.reduce.child.java.opts"
decl_stmt|;
DECL|field|DEFAULT_MAPRED_ADMIN_JAVA_OPTS
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_MAPRED_ADMIN_JAVA_OPTS
init|=
literal|"-Djava.net.preferIPv4Stack=true "
operator|+
literal|"-Dhadoop.metrics.log.level=WARN "
decl_stmt|;
DECL|field|MAPRED_ADMIN_USER_SHELL
specifier|public
specifier|static
specifier|final
name|String
name|MAPRED_ADMIN_USER_SHELL
init|=
literal|"mapreduce.admin.user.shell"
decl_stmt|;
DECL|field|DEFAULT_SHELL
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_SHELL
init|=
literal|"/bin/bash"
decl_stmt|;
DECL|field|MAPRED_ADMIN_USER_ENV
specifier|public
specifier|static
specifier|final
name|String
name|MAPRED_ADMIN_USER_ENV
init|=
literal|"mapreduce.admin.user.env"
decl_stmt|;
DECL|field|DEFAULT_MAPRED_ADMIN_USER_ENV
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_MAPRED_ADMIN_USER_ENV
init|=
literal|"LD_LIBRARY_PATH=$HADOOP_COMMON_HOME/lib"
decl_stmt|;
DECL|field|WORKDIR
specifier|public
specifier|static
specifier|final
name|String
name|WORKDIR
init|=
literal|"work"
decl_stmt|;
DECL|field|OUTPUT
specifier|public
specifier|static
specifier|final
name|String
name|OUTPUT
init|=
literal|"output"
decl_stmt|;
DECL|field|HADOOP_WORK_DIR
specifier|public
specifier|static
specifier|final
name|String
name|HADOOP_WORK_DIR
init|=
literal|"HADOOP_WORK_DIR"
decl_stmt|;
DECL|field|STDOUT_LOGFILE_ENV
specifier|public
specifier|static
specifier|final
name|String
name|STDOUT_LOGFILE_ENV
init|=
literal|"STDOUT_LOGFILE_ENV"
decl_stmt|;
DECL|field|STDERR_LOGFILE_ENV
specifier|public
specifier|static
specifier|final
name|String
name|STDERR_LOGFILE_ENV
init|=
literal|"STDERR_LOGFILE_ENV"
decl_stmt|;
comment|// This should be the directory where splits file gets localized on the node
comment|// running ApplicationMaster.
DECL|field|JOB_SUBMIT_DIR
specifier|public
specifier|static
specifier|final
name|String
name|JOB_SUBMIT_DIR
init|=
literal|"jobSubmitDir"
decl_stmt|;
comment|// This should be the name of the localized job-configuration file on the node
comment|// running ApplicationMaster and Task
DECL|field|JOB_CONF_FILE
specifier|public
specifier|static
specifier|final
name|String
name|JOB_CONF_FILE
init|=
literal|"job.xml"
decl_stmt|;
comment|// This should be the name of the localized job-jar file on the node running
comment|// individual containers/tasks.
DECL|field|JOB_JAR
specifier|public
specifier|static
specifier|final
name|String
name|JOB_JAR
init|=
literal|"job.jar"
decl_stmt|;
DECL|field|JOB_SPLIT
specifier|public
specifier|static
specifier|final
name|String
name|JOB_SPLIT
init|=
literal|"job.split"
decl_stmt|;
DECL|field|JOB_SPLIT_METAINFO
specifier|public
specifier|static
specifier|final
name|String
name|JOB_SPLIT_METAINFO
init|=
literal|"job.splitmetainfo"
decl_stmt|;
DECL|field|APPLICATION_MASTER_CLASS
specifier|public
specifier|static
specifier|final
name|String
name|APPLICATION_MASTER_CLASS
init|=
literal|"org.apache.hadoop.mapreduce.v2.app.MRAppMaster"
decl_stmt|;
comment|// The token file for the application. Should contain tokens for access to
comment|// remote file system and may optionally contain application specific tokens.
comment|// For now, generated by the AppManagers and used by NodeManagers and the
comment|// Containers.
DECL|field|APPLICATION_TOKENS_FILE
specifier|public
specifier|static
specifier|final
name|String
name|APPLICATION_TOKENS_FILE
init|=
literal|"appTokens"
decl_stmt|;
comment|/** The log directory for the containers */
DECL|field|TASK_LOG_DIR
specifier|public
specifier|static
specifier|final
name|String
name|TASK_LOG_DIR
init|=
name|MR_PREFIX
operator|+
literal|"container.log.dir"
decl_stmt|;
DECL|field|TASK_LOG_SIZE
specifier|public
specifier|static
specifier|final
name|String
name|TASK_LOG_SIZE
init|=
name|MR_PREFIX
operator|+
literal|"log.filesize"
decl_stmt|;
DECL|field|MAPREDUCE_V2_CHILD_CLASS
specifier|public
specifier|static
specifier|final
name|String
name|MAPREDUCE_V2_CHILD_CLASS
init|=
literal|"org.apache.hadoop.mapred.YarnChild"
decl_stmt|;
block|}
end_interface

end_unit

