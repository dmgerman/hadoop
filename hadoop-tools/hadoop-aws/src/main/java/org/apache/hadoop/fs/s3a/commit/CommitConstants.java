begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a.commit
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
operator|.
name|commit
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
import|import static
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
name|PathOutputCommitterFactory
operator|.
name|COMMITTER_FACTORY_SCHEME_PATTERN
import|;
end_import

begin_comment
comment|/**  * Constants for working with committers.  */
end_comment

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|CommitConstants
specifier|public
specifier|final
class|class
name|CommitConstants
block|{
DECL|method|CommitConstants ()
specifier|private
name|CommitConstants
parameter_list|()
block|{   }
comment|/**    * Path for "magic" writes: path and {@link #PENDING_SUFFIX} files:    * {@value}.    */
DECL|field|MAGIC
specifier|public
specifier|static
specifier|final
name|String
name|MAGIC
init|=
literal|"__magic"
decl_stmt|;
comment|/**    * Marker of the start of a directory tree for calculating    * the final path names: {@value}.    */
DECL|field|BASE
specifier|public
specifier|static
specifier|final
name|String
name|BASE
init|=
literal|"__base"
decl_stmt|;
comment|/**    * Suffix applied to pending commit metadata: {@value}.    */
DECL|field|PENDING_SUFFIX
specifier|public
specifier|static
specifier|final
name|String
name|PENDING_SUFFIX
init|=
literal|".pending"
decl_stmt|;
comment|/**    * Suffix applied to multiple pending commit metadata: {@value}.    */
DECL|field|PENDINGSET_SUFFIX
specifier|public
specifier|static
specifier|final
name|String
name|PENDINGSET_SUFFIX
init|=
literal|".pendingset"
decl_stmt|;
comment|/**    * Flag to indicate whether support for the Magic committer is enabled    * in the filesystem.    * Value: {@value}.    */
DECL|field|MAGIC_COMMITTER_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|MAGIC_COMMITTER_PREFIX
init|=
literal|"fs.s3a.committer.magic"
decl_stmt|;
comment|/**    * Flag to indicate whether support for the Magic committer is enabled    * in the filesystem.    * Value: {@value}.    */
DECL|field|MAGIC_COMMITTER_ENABLED
specifier|public
specifier|static
specifier|final
name|String
name|MAGIC_COMMITTER_ENABLED
init|=
name|MAGIC_COMMITTER_PREFIX
operator|+
literal|".enabled"
decl_stmt|;
comment|/**    * Flag to indicate whether a stream is a magic output stream;    * returned in {@code StreamCapabilities}    * Value: {@value}.    */
DECL|field|STREAM_CAPABILITY_MAGIC_OUTPUT
specifier|public
specifier|static
specifier|final
name|String
name|STREAM_CAPABILITY_MAGIC_OUTPUT
init|=
literal|"fs.s3a.capability.magic.output.stream"
decl_stmt|;
comment|/**    * Flag to indicate that a store supports magic committers.    * returned in {@code PathCapabilities}    * Value: {@value}.    */
DECL|field|STORE_CAPABILITY_MAGIC_COMMITTER
specifier|public
specifier|static
specifier|final
name|String
name|STORE_CAPABILITY_MAGIC_COMMITTER
init|=
literal|"fs.s3a.capability.magic.committer"
decl_stmt|;
comment|/**    * Flag to indicate whether a stream is a magic output stream;    * returned in {@code StreamCapabilities}    * Value: {@value}.    */
annotation|@
name|Deprecated
DECL|field|STREAM_CAPABILITY_MAGIC_OUTPUT_OLD
specifier|public
specifier|static
specifier|final
name|String
name|STREAM_CAPABILITY_MAGIC_OUTPUT_OLD
init|=
literal|"s3a:magic.output.stream"
decl_stmt|;
comment|/**    * Flag to indicate that a store supports magic committers.    * returned in {@code PathCapabilities}    * Value: {@value}.    */
annotation|@
name|Deprecated
DECL|field|STORE_CAPABILITY_MAGIC_COMMITTER_OLD
specifier|public
specifier|static
specifier|final
name|String
name|STORE_CAPABILITY_MAGIC_COMMITTER_OLD
init|=
literal|"s3a:magic.committer"
decl_stmt|;
comment|/**    * Is the committer enabled by default? No.    */
DECL|field|DEFAULT_MAGIC_COMMITTER_ENABLED
specifier|public
specifier|static
specifier|final
name|boolean
name|DEFAULT_MAGIC_COMMITTER_ENABLED
init|=
literal|false
decl_stmt|;
comment|/**    * This is the "Pending" directory of the {@code FileOutputCommitter};    * data written here is, in that algorithm, renamed into place.    * Value: {@value}.    */
DECL|field|TEMPORARY
specifier|public
specifier|static
specifier|final
name|String
name|TEMPORARY
init|=
literal|"_temporary"
decl_stmt|;
comment|/**    * Temp data which is not auto-committed: {@value}.    * Uses a different name from normal just to make clear it is different.    */
DECL|field|TEMP_DATA
specifier|public
specifier|static
specifier|final
name|String
name|TEMP_DATA
init|=
literal|"__temp-data"
decl_stmt|;
comment|/**    * Flag to trigger creation of a marker file on job completion.    */
DECL|field|CREATE_SUCCESSFUL_JOB_OUTPUT_DIR_MARKER
specifier|public
specifier|static
specifier|final
name|String
name|CREATE_SUCCESSFUL_JOB_OUTPUT_DIR_MARKER
init|=
literal|"mapreduce.fileoutputcommitter.marksuccessfuljobs"
decl_stmt|;
comment|/**    * Marker file to create on success: {@value}.    */
DECL|field|_SUCCESS
specifier|public
specifier|static
specifier|final
name|String
name|_SUCCESS
init|=
literal|"_SUCCESS"
decl_stmt|;
comment|/** Default job marker option: {@value}. */
DECL|field|DEFAULT_CREATE_SUCCESSFUL_JOB_DIR_MARKER
specifier|public
specifier|static
specifier|final
name|boolean
name|DEFAULT_CREATE_SUCCESSFUL_JOB_DIR_MARKER
init|=
literal|true
decl_stmt|;
comment|/**    * Key to set for the S3A schema to use the specific committer.    */
DECL|field|S3A_COMMITTER_FACTORY_KEY
specifier|public
specifier|static
specifier|final
name|String
name|S3A_COMMITTER_FACTORY_KEY
init|=
name|String
operator|.
name|format
argument_list|(
name|COMMITTER_FACTORY_SCHEME_PATTERN
argument_list|,
literal|"s3a"
argument_list|)
decl_stmt|;
comment|/**    * S3 Committer factory: {@value}.    * This uses the value of {@link #FS_S3A_COMMITTER_NAME}    * to choose the final committer.    */
DECL|field|S3A_COMMITTER_FACTORY
specifier|public
specifier|static
specifier|final
name|String
name|S3A_COMMITTER_FACTORY
init|=
name|S3ACommitterFactory
operator|.
name|CLASSNAME
decl_stmt|;
comment|/**    * Option to identify the S3A committer:    * {@value}.    */
DECL|field|FS_S3A_COMMITTER_NAME
specifier|public
specifier|static
specifier|final
name|String
name|FS_S3A_COMMITTER_NAME
init|=
literal|"fs.s3a.committer.name"
decl_stmt|;
comment|/**    * Option for {@link #FS_S3A_COMMITTER_NAME}:    * classic/file output committer: {@value}.    */
DECL|field|COMMITTER_NAME_FILE
specifier|public
specifier|static
specifier|final
name|String
name|COMMITTER_NAME_FILE
init|=
literal|"file"
decl_stmt|;
comment|/**    * Option for {@link #FS_S3A_COMMITTER_NAME}:    * magic output committer: {@value}.    */
DECL|field|COMMITTER_NAME_MAGIC
specifier|public
specifier|static
specifier|final
name|String
name|COMMITTER_NAME_MAGIC
init|=
literal|"magic"
decl_stmt|;
comment|/**    * Option for {@link #FS_S3A_COMMITTER_NAME}:    * directory output committer: {@value}.    */
DECL|field|COMMITTER_NAME_DIRECTORY
specifier|public
specifier|static
specifier|final
name|String
name|COMMITTER_NAME_DIRECTORY
init|=
literal|"directory"
decl_stmt|;
comment|/**    * Option for {@link #FS_S3A_COMMITTER_NAME}:    * partition output committer: {@value}.    */
DECL|field|COMMITTER_NAME_PARTITIONED
specifier|public
specifier|static
specifier|final
name|String
name|COMMITTER_NAME_PARTITIONED
init|=
literal|"partitioned"
decl_stmt|;
comment|/**    * Option for final files to have a uniqueness name through job attempt info,    * falling back to a new UUID if there is no job attempt information to use.    * {@value}.    * When writing data with the "append" conflict option, this guarantees    * that new data will not overwrite any existing data.    */
DECL|field|FS_S3A_COMMITTER_STAGING_UNIQUE_FILENAMES
specifier|public
specifier|static
specifier|final
name|String
name|FS_S3A_COMMITTER_STAGING_UNIQUE_FILENAMES
init|=
literal|"fs.s3a.committer.staging.unique-filenames"
decl_stmt|;
comment|/**    * Default value for {@link #FS_S3A_COMMITTER_STAGING_UNIQUE_FILENAMES}:    * {@value}.    */
DECL|field|DEFAULT_STAGING_COMMITTER_UNIQUE_FILENAMES
specifier|public
specifier|static
specifier|final
name|boolean
name|DEFAULT_STAGING_COMMITTER_UNIQUE_FILENAMES
init|=
literal|true
decl_stmt|;
comment|/**    * Staging committer conflict resolution policy: {@value}.    * Supported: fail, append, replace.    */
DECL|field|FS_S3A_COMMITTER_STAGING_CONFLICT_MODE
specifier|public
specifier|static
specifier|final
name|String
name|FS_S3A_COMMITTER_STAGING_CONFLICT_MODE
init|=
literal|"fs.s3a.committer.staging.conflict-mode"
decl_stmt|;
comment|/** Conflict mode: {@value}. */
DECL|field|CONFLICT_MODE_FAIL
specifier|public
specifier|static
specifier|final
name|String
name|CONFLICT_MODE_FAIL
init|=
literal|"fail"
decl_stmt|;
comment|/** Conflict mode: {@value}. */
DECL|field|CONFLICT_MODE_APPEND
specifier|public
specifier|static
specifier|final
name|String
name|CONFLICT_MODE_APPEND
init|=
literal|"append"
decl_stmt|;
comment|/** Conflict mode: {@value}. */
DECL|field|CONFLICT_MODE_REPLACE
specifier|public
specifier|static
specifier|final
name|String
name|CONFLICT_MODE_REPLACE
init|=
literal|"replace"
decl_stmt|;
comment|/** Default conflict mode: {@value}. */
DECL|field|DEFAULT_CONFLICT_MODE
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_CONFLICT_MODE
init|=
name|CONFLICT_MODE_APPEND
decl_stmt|;
comment|/**    * Number of threads in committers for parallel operations on files    * (upload, commit, abort, delete...): {@value}.    */
DECL|field|FS_S3A_COMMITTER_THREADS
specifier|public
specifier|static
specifier|final
name|String
name|FS_S3A_COMMITTER_THREADS
init|=
literal|"fs.s3a.committer.threads"
decl_stmt|;
comment|/**    * Default value for {@link #FS_S3A_COMMITTER_THREADS}: {@value}.    */
DECL|field|DEFAULT_COMMITTER_THREADS
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_COMMITTER_THREADS
init|=
literal|8
decl_stmt|;
comment|/**    * Path  in the cluster filesystem for temporary data: {@value}.    * This is for HDFS, not the local filesystem.    * It is only for the summary data of each file, not the actual    * data being committed.    */
DECL|field|FS_S3A_COMMITTER_STAGING_TMP_PATH
specifier|public
specifier|static
specifier|final
name|String
name|FS_S3A_COMMITTER_STAGING_TMP_PATH
init|=
literal|"fs.s3a.committer.staging.tmp.path"
decl_stmt|;
comment|/**    * Should the staging committers abort all pending uploads to the destination    * directory? Default: true.    *    * Changing this is if more than one partitioned committer is    * writing to the same destination tree simultaneously; otherwise    * the first job to complete will cancel all outstanding uploads from the    * others. However, it may lead to leaked outstanding uploads from failed    * tasks. If disabled, configure the bucket lifecycle to remove uploads    * after a time period, and/or set up a workflow to explicitly delete    * entries. Otherwise there is a risk that uncommitted uploads may run up    * bills.    */
DECL|field|FS_S3A_COMMITTER_STAGING_ABORT_PENDING_UPLOADS
specifier|public
specifier|static
specifier|final
name|String
name|FS_S3A_COMMITTER_STAGING_ABORT_PENDING_UPLOADS
init|=
literal|"fs.s3a.committer.staging.abort.pending.uploads"
decl_stmt|;
block|}
end_class

end_unit

