begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.tools
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|tools
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

begin_comment
comment|/**  * Utility class to hold commonly used constants.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|LimitedPrivate
argument_list|(
literal|"Distcp support tools"
argument_list|)
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|DistCpConstants
specifier|public
specifier|final
class|class
name|DistCpConstants
block|{
DECL|method|DistCpConstants ()
specifier|private
name|DistCpConstants
parameter_list|()
block|{   }
comment|/* Default number of threads to use for building file listing */
DECL|field|DEFAULT_LISTSTATUS_THREADS
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_LISTSTATUS_THREADS
init|=
literal|1
decl_stmt|;
comment|/* Default number of maps to use for DistCp */
DECL|field|DEFAULT_MAPS
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MAPS
init|=
literal|20
decl_stmt|;
comment|/* Default bandwidth if none specified */
DECL|field|DEFAULT_BANDWIDTH_MB
specifier|public
specifier|static
specifier|final
name|float
name|DEFAULT_BANDWIDTH_MB
init|=
literal|100
decl_stmt|;
comment|/* Default strategy for copying. Implementation looked up      from distcp-default.xml    */
DECL|field|UNIFORMSIZE
specifier|public
specifier|static
specifier|final
name|String
name|UNIFORMSIZE
init|=
literal|"uniformsize"
decl_stmt|;
comment|/**    *  Constants mapping to command line switches/input options    */
DECL|field|CONF_LABEL_ATOMIC_COPY
specifier|public
specifier|static
specifier|final
name|String
name|CONF_LABEL_ATOMIC_COPY
init|=
literal|"distcp.atomic.copy"
decl_stmt|;
DECL|field|CONF_LABEL_WORK_PATH
specifier|public
specifier|static
specifier|final
name|String
name|CONF_LABEL_WORK_PATH
init|=
literal|"distcp.work.path"
decl_stmt|;
DECL|field|CONF_LABEL_LOG_PATH
specifier|public
specifier|static
specifier|final
name|String
name|CONF_LABEL_LOG_PATH
init|=
literal|"distcp.log.path"
decl_stmt|;
DECL|field|CONF_LABEL_VERBOSE_LOG
specifier|public
specifier|static
specifier|final
name|String
name|CONF_LABEL_VERBOSE_LOG
init|=
literal|"distcp.verbose.log"
decl_stmt|;
DECL|field|CONF_LABEL_IGNORE_FAILURES
specifier|public
specifier|static
specifier|final
name|String
name|CONF_LABEL_IGNORE_FAILURES
init|=
literal|"distcp.ignore.failures"
decl_stmt|;
DECL|field|CONF_LABEL_PRESERVE_STATUS
specifier|public
specifier|static
specifier|final
name|String
name|CONF_LABEL_PRESERVE_STATUS
init|=
literal|"distcp.preserve.status"
decl_stmt|;
DECL|field|CONF_LABEL_PRESERVE_RAWXATTRS
specifier|public
specifier|static
specifier|final
name|String
name|CONF_LABEL_PRESERVE_RAWXATTRS
init|=
literal|"distcp.preserve.rawxattrs"
decl_stmt|;
DECL|field|CONF_LABEL_SYNC_FOLDERS
specifier|public
specifier|static
specifier|final
name|String
name|CONF_LABEL_SYNC_FOLDERS
init|=
literal|"distcp.sync.folders"
decl_stmt|;
DECL|field|CONF_LABEL_DELETE_MISSING
specifier|public
specifier|static
specifier|final
name|String
name|CONF_LABEL_DELETE_MISSING
init|=
literal|"distcp.delete.missing.source"
decl_stmt|;
DECL|field|CONF_LABEL_TRACK_MISSING
specifier|public
specifier|static
specifier|final
name|String
name|CONF_LABEL_TRACK_MISSING
init|=
literal|"distcp.track.missing.source"
decl_stmt|;
DECL|field|CONF_LABEL_LISTSTATUS_THREADS
specifier|public
specifier|static
specifier|final
name|String
name|CONF_LABEL_LISTSTATUS_THREADS
init|=
literal|"distcp.liststatus.threads"
decl_stmt|;
DECL|field|CONF_LABEL_MAX_MAPS
specifier|public
specifier|static
specifier|final
name|String
name|CONF_LABEL_MAX_MAPS
init|=
literal|"distcp.max.maps"
decl_stmt|;
DECL|field|CONF_LABEL_SOURCE_LISTING
specifier|public
specifier|static
specifier|final
name|String
name|CONF_LABEL_SOURCE_LISTING
init|=
literal|"distcp.source.listing"
decl_stmt|;
DECL|field|CONF_LABEL_COPY_STRATEGY
specifier|public
specifier|static
specifier|final
name|String
name|CONF_LABEL_COPY_STRATEGY
init|=
literal|"distcp.copy.strategy"
decl_stmt|;
DECL|field|CONF_LABEL_SKIP_CRC
specifier|public
specifier|static
specifier|final
name|String
name|CONF_LABEL_SKIP_CRC
init|=
literal|"distcp.skip.crc"
decl_stmt|;
DECL|field|CONF_LABEL_OVERWRITE
specifier|public
specifier|static
specifier|final
name|String
name|CONF_LABEL_OVERWRITE
init|=
literal|"distcp.copy.overwrite"
decl_stmt|;
DECL|field|CONF_LABEL_APPEND
specifier|public
specifier|static
specifier|final
name|String
name|CONF_LABEL_APPEND
init|=
literal|"distcp.copy.append"
decl_stmt|;
DECL|field|CONF_LABEL_DIFF
specifier|public
specifier|static
specifier|final
name|String
name|CONF_LABEL_DIFF
init|=
literal|"distcp.copy.diff"
decl_stmt|;
DECL|field|CONF_LABEL_RDIFF
specifier|public
specifier|static
specifier|final
name|String
name|CONF_LABEL_RDIFF
init|=
literal|"distcp.copy.rdiff"
decl_stmt|;
DECL|field|CONF_LABEL_BANDWIDTH_MB
specifier|public
specifier|static
specifier|final
name|String
name|CONF_LABEL_BANDWIDTH_MB
init|=
literal|"distcp.map.bandwidth.mb"
decl_stmt|;
DECL|field|CONF_LABEL_SIMPLE_LISTING_FILESTATUS_SIZE
specifier|public
specifier|static
specifier|final
name|String
name|CONF_LABEL_SIMPLE_LISTING_FILESTATUS_SIZE
init|=
literal|"distcp.simplelisting.file.status.size"
decl_stmt|;
DECL|field|CONF_LABEL_SIMPLE_LISTING_RANDOMIZE_FILES
specifier|public
specifier|static
specifier|final
name|String
name|CONF_LABEL_SIMPLE_LISTING_RANDOMIZE_FILES
init|=
literal|"distcp.simplelisting.randomize.files"
decl_stmt|;
DECL|field|CONF_LABEL_FILTERS_FILE
specifier|public
specifier|static
specifier|final
name|String
name|CONF_LABEL_FILTERS_FILE
init|=
literal|"distcp.filters.file"
decl_stmt|;
DECL|field|CONF_LABEL_MAX_CHUNKS_TOLERABLE
specifier|public
specifier|static
specifier|final
name|String
name|CONF_LABEL_MAX_CHUNKS_TOLERABLE
init|=
literal|"distcp.dynamic.max.chunks.tolerable"
decl_stmt|;
DECL|field|CONF_LABEL_MAX_CHUNKS_IDEAL
specifier|public
specifier|static
specifier|final
name|String
name|CONF_LABEL_MAX_CHUNKS_IDEAL
init|=
literal|"distcp.dynamic.max.chunks.ideal"
decl_stmt|;
DECL|field|CONF_LABEL_MIN_RECORDS_PER_CHUNK
specifier|public
specifier|static
specifier|final
name|String
name|CONF_LABEL_MIN_RECORDS_PER_CHUNK
init|=
literal|"distcp.dynamic.min.records_per_chunk"
decl_stmt|;
DECL|field|CONF_LABEL_SPLIT_RATIO
specifier|public
specifier|static
specifier|final
name|String
name|CONF_LABEL_SPLIT_RATIO
init|=
literal|"distcp.dynamic.split.ratio"
decl_stmt|;
DECL|field|CONF_LABEL_DIRECT_WRITE
specifier|public
specifier|static
specifier|final
name|String
name|CONF_LABEL_DIRECT_WRITE
init|=
literal|"distcp.direct.write"
decl_stmt|;
comment|/* Total bytes to be copied. Updated by copylisting. Unfiltered count */
DECL|field|CONF_LABEL_TOTAL_BYTES_TO_BE_COPIED
specifier|public
specifier|static
specifier|final
name|String
name|CONF_LABEL_TOTAL_BYTES_TO_BE_COPIED
init|=
literal|"mapred.total.bytes.expected"
decl_stmt|;
comment|/* Total number of paths to copy, includes directories. Unfiltered count */
DECL|field|CONF_LABEL_TOTAL_NUMBER_OF_RECORDS
specifier|public
specifier|static
specifier|final
name|String
name|CONF_LABEL_TOTAL_NUMBER_OF_RECORDS
init|=
literal|"mapred.number.of.records"
decl_stmt|;
comment|/* If input is based -f<<source listing>>, file containing the src paths */
DECL|field|CONF_LABEL_LISTING_FILE_PATH
specifier|public
specifier|static
specifier|final
name|String
name|CONF_LABEL_LISTING_FILE_PATH
init|=
literal|"distcp.listing.file.path"
decl_stmt|;
comment|/* Directory where the mapreduce job will write to. If not atomic commit, then same     as CONF_LABEL_TARGET_FINAL_PATH    */
DECL|field|CONF_LABEL_TARGET_WORK_PATH
specifier|public
specifier|static
specifier|final
name|String
name|CONF_LABEL_TARGET_WORK_PATH
init|=
literal|"distcp.target.work.path"
decl_stmt|;
comment|/* Directory where the final data will be committed to. If not atomic commit, then same     as CONF_LABEL_TARGET_WORK_PATH    */
DECL|field|CONF_LABEL_TARGET_FINAL_PATH
specifier|public
specifier|static
specifier|final
name|String
name|CONF_LABEL_TARGET_FINAL_PATH
init|=
literal|"distcp.target.final.path"
decl_stmt|;
comment|/* Boolean to indicate whether the target of distcp exists. */
DECL|field|CONF_LABEL_TARGET_PATH_EXISTS
specifier|public
specifier|static
specifier|final
name|String
name|CONF_LABEL_TARGET_PATH_EXISTS
init|=
literal|"distcp.target.path.exists"
decl_stmt|;
comment|/**    * DistCp job id for consumers of the Disctp     */
DECL|field|CONF_LABEL_DISTCP_JOB_ID
specifier|public
specifier|static
specifier|final
name|String
name|CONF_LABEL_DISTCP_JOB_ID
init|=
literal|"distcp.job.id"
decl_stmt|;
comment|/* Meta folder where the job's intermediate data is kept */
DECL|field|CONF_LABEL_META_FOLDER
specifier|public
specifier|static
specifier|final
name|String
name|CONF_LABEL_META_FOLDER
init|=
literal|"distcp.meta.folder"
decl_stmt|;
comment|/* DistCp CopyListing class override param */
DECL|field|CONF_LABEL_COPY_LISTING_CLASS
specifier|public
specifier|static
specifier|final
name|String
name|CONF_LABEL_COPY_LISTING_CLASS
init|=
literal|"distcp.copy.listing.class"
decl_stmt|;
comment|/* DistCp Copy Buffer Size */
DECL|field|CONF_LABEL_COPY_BUFFER_SIZE
specifier|public
specifier|static
specifier|final
name|String
name|CONF_LABEL_COPY_BUFFER_SIZE
init|=
literal|"distcp.copy.buffer.size"
decl_stmt|;
comment|/** DistCp Blocks Per Chunk: {@value}. */
DECL|field|CONF_LABEL_BLOCKS_PER_CHUNK
specifier|public
specifier|static
specifier|final
name|String
name|CONF_LABEL_BLOCKS_PER_CHUNK
init|=
literal|"distcp.blocks.per.chunk"
decl_stmt|;
comment|/**    * Constants for DistCp return code to shell / consumer of ToolRunner's run    */
DECL|field|SUCCESS
specifier|public
specifier|static
specifier|final
name|int
name|SUCCESS
init|=
literal|0
decl_stmt|;
DECL|field|INVALID_ARGUMENT
specifier|public
specifier|static
specifier|final
name|int
name|INVALID_ARGUMENT
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|DUPLICATE_INPUT
specifier|public
specifier|static
specifier|final
name|int
name|DUPLICATE_INPUT
init|=
operator|-
literal|2
decl_stmt|;
DECL|field|ACLS_NOT_SUPPORTED
specifier|public
specifier|static
specifier|final
name|int
name|ACLS_NOT_SUPPORTED
init|=
operator|-
literal|3
decl_stmt|;
DECL|field|XATTRS_NOT_SUPPORTED
specifier|public
specifier|static
specifier|final
name|int
name|XATTRS_NOT_SUPPORTED
init|=
operator|-
literal|4
decl_stmt|;
DECL|field|UNKNOWN_ERROR
specifier|public
specifier|static
specifier|final
name|int
name|UNKNOWN_ERROR
init|=
operator|-
literal|999
decl_stmt|;
comment|/**    * Constants for DistCp default values of configurable values    */
DECL|field|MAX_CHUNKS_TOLERABLE_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|MAX_CHUNKS_TOLERABLE_DEFAULT
init|=
literal|400
decl_stmt|;
DECL|field|MAX_CHUNKS_IDEAL_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|MAX_CHUNKS_IDEAL_DEFAULT
init|=
literal|100
decl_stmt|;
DECL|field|MIN_RECORDS_PER_CHUNK_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|MIN_RECORDS_PER_CHUNK_DEFAULT
init|=
literal|5
decl_stmt|;
DECL|field|SPLIT_RATIO_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|SPLIT_RATIO_DEFAULT
init|=
literal|2
decl_stmt|;
comment|/**    * Constants for NONE file deletion    */
DECL|field|NONE_PATH_NAME
specifier|public
specifier|static
specifier|final
name|String
name|NONE_PATH_NAME
init|=
literal|"/NONE"
decl_stmt|;
DECL|field|NONE_PATH
specifier|public
specifier|static
specifier|final
name|Path
name|NONE_PATH
init|=
operator|new
name|Path
argument_list|(
name|NONE_PATH_NAME
argument_list|)
decl_stmt|;
DECL|field|RAW_NONE_PATH
specifier|public
specifier|static
specifier|final
name|Path
name|RAW_NONE_PATH
init|=
operator|new
name|Path
argument_list|(
name|DistCpConstants
operator|.
name|HDFS_RESERVED_RAW_DIRECTORY_NAME
operator|+
name|NONE_PATH_NAME
argument_list|)
decl_stmt|;
comment|/**    * Value of reserved raw HDFS directory when copying raw.* xattrs.    */
DECL|field|HDFS_RESERVED_RAW_DIRECTORY_NAME
specifier|public
specifier|static
specifier|final
name|String
name|HDFS_RESERVED_RAW_DIRECTORY_NAME
init|=
literal|"/.reserved/raw"
decl_stmt|;
DECL|field|HDFS_DISTCP_DIFF_DIRECTORY_NAME
specifier|static
specifier|final
name|String
name|HDFS_DISTCP_DIFF_DIRECTORY_NAME
init|=
literal|".distcp.diff.tmp"
decl_stmt|;
DECL|field|COPY_BUFFER_SIZE_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|COPY_BUFFER_SIZE_DEFAULT
init|=
literal|8
operator|*
literal|1024
decl_stmt|;
comment|/** Filename of sorted files in when tracking saves them. */
DECL|field|SOURCE_SORTED_FILE
specifier|public
specifier|static
specifier|final
name|String
name|SOURCE_SORTED_FILE
init|=
literal|"source_sorted.seq"
decl_stmt|;
comment|/** Filename of unsorted target listing. */
DECL|field|TARGET_LISTING_FILE
specifier|public
specifier|static
specifier|final
name|String
name|TARGET_LISTING_FILE
init|=
literal|"target_listing.seq"
decl_stmt|;
comment|/** Filename of sorted target listing. */
DECL|field|TARGET_SORTED_FILE
specifier|public
specifier|static
specifier|final
name|String
name|TARGET_SORTED_FILE
init|=
literal|"target_sorted.seq"
decl_stmt|;
block|}
end_class

end_unit

