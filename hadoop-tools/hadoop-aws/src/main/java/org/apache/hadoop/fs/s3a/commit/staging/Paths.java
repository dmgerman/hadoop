begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a.commit.staging
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
operator|.
name|staging
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ExecutionException
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
name|annotations
operator|.
name|VisibleForTesting
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
name|Preconditions
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
name|cache
operator|.
name|Cache
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
name|cache
operator|.
name|CacheBuilder
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
name|collect
operator|.
name|Sets
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
name|util
operator|.
name|concurrent
operator|.
name|UncheckedExecutionException
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
name|lang
operator|.
name|StringUtils
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
name|LocalDirAllocator
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
name|PathIsDirectoryException
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
name|s3a
operator|.
name|Constants
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
name|TaskAttemptID
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
name|commit
operator|.
name|CommitConstants
operator|.
name|*
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
name|commit
operator|.
name|staging
operator|.
name|StagingCommitterConstants
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Path operations for the staging committers.  */
end_comment

begin_class
DECL|class|Paths
specifier|public
specifier|final
class|class
name|Paths
block|{
DECL|method|Paths ()
specifier|private
name|Paths
parameter_list|()
block|{   }
comment|/**    * Insert the UUID to a path if it is not there already.    * If there is a trailing "." in the prefix after the last slash, the    * UUID is inserted before it with a "-" prefix; otherwise appended.    *    * Examples:    *<pre>    *   /example/part-0000  ==&gt; /example/part-0000-0ab34    *   /example/part-0001.gz.csv  ==&gt; /example/part-0001-0ab34.gz.csv    *   /example/part-0002-0abc3.gz.csv  ==&gt; /example/part-0002-0abc3.gz.csv    *   /example0abc3/part-0002.gz.csv  ==&gt; /example0abc3/part-0002.gz.csv    *</pre>    *    *    * @param pathStr path as a string; must not have a trailing "/".    * @param uuid UUID to append; must not be empty    * @return new path.    */
DECL|method|addUUID (String pathStr, String uuid)
specifier|public
specifier|static
name|String
name|addUUID
parameter_list|(
name|String
name|pathStr
parameter_list|,
name|String
name|uuid
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|StringUtils
operator|.
name|isNotEmpty
argument_list|(
name|pathStr
argument_list|)
argument_list|,
literal|"empty path"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|StringUtils
operator|.
name|isNotEmpty
argument_list|(
name|uuid
argument_list|)
argument_list|,
literal|"empty uuid"
argument_list|)
expr_stmt|;
comment|// In some cases, Spark will add the UUID to the filename itself.
if|if
condition|(
name|pathStr
operator|.
name|contains
argument_list|(
name|uuid
argument_list|)
condition|)
block|{
return|return
name|pathStr
return|;
block|}
name|int
name|dot
decl_stmt|;
comment|// location of the first '.' in the file name
name|int
name|lastSlash
init|=
name|pathStr
operator|.
name|lastIndexOf
argument_list|(
literal|'/'
argument_list|)
decl_stmt|;
if|if
condition|(
name|lastSlash
operator|>=
literal|0
condition|)
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
name|lastSlash
operator|+
literal|1
operator|<
name|pathStr
operator|.
name|length
argument_list|()
argument_list|,
literal|"Bad path: "
operator|+
name|pathStr
argument_list|)
expr_stmt|;
name|dot
operator|=
name|pathStr
operator|.
name|indexOf
argument_list|(
literal|'.'
argument_list|,
name|lastSlash
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|dot
operator|=
name|pathStr
operator|.
name|indexOf
argument_list|(
literal|'.'
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|dot
operator|>=
literal|0
condition|)
block|{
return|return
name|pathStr
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|dot
argument_list|)
operator|+
literal|"-"
operator|+
name|uuid
operator|+
name|pathStr
operator|.
name|substring
argument_list|(
name|dot
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|pathStr
operator|+
literal|"-"
operator|+
name|uuid
return|;
block|}
block|}
comment|/**    * Get the parent path of a string path: everything up to but excluding    * the last "/" in the path.    * @param pathStr path as a string    * @return the parent or null if there is no parent.    */
DECL|method|getParent (String pathStr)
specifier|public
specifier|static
name|String
name|getParent
parameter_list|(
name|String
name|pathStr
parameter_list|)
block|{
name|int
name|lastSlash
init|=
name|pathStr
operator|.
name|lastIndexOf
argument_list|(
literal|'/'
argument_list|)
decl_stmt|;
if|if
condition|(
name|lastSlash
operator|>=
literal|0
condition|)
block|{
return|return
name|pathStr
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|lastSlash
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
comment|/**    * Using {@code URI#relativize()}, build the relative path from the    * base path to the full path.    * If {@code childPath} is not a child of {@code basePath} the outcome    * os undefined.    * @param basePath base path    * @param fullPath full path under the base path.    * @return the relative path    */
DECL|method|getRelativePath (Path basePath, Path fullPath)
specifier|public
specifier|static
name|String
name|getRelativePath
parameter_list|(
name|Path
name|basePath
parameter_list|,
name|Path
name|fullPath
parameter_list|)
block|{
return|return
name|basePath
operator|.
name|toUri
argument_list|()
operator|.
name|relativize
argument_list|(
name|fullPath
operator|.
name|toUri
argument_list|()
argument_list|)
operator|.
name|getPath
argument_list|()
return|;
block|}
comment|/**    * Varags constructor of paths. Not very efficient.    * @param parent parent path    * @param child child entries. "" elements are skipped.    * @return the full child path.    */
DECL|method|path (Path parent, String... child)
specifier|public
specifier|static
name|Path
name|path
parameter_list|(
name|Path
name|parent
parameter_list|,
name|String
modifier|...
name|child
parameter_list|)
block|{
name|Path
name|p
init|=
name|parent
decl_stmt|;
for|for
control|(
name|String
name|c
range|:
name|child
control|)
block|{
if|if
condition|(
operator|!
name|c
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|p
operator|=
operator|new
name|Path
argument_list|(
name|p
argument_list|,
name|c
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|p
return|;
block|}
comment|/**    * A cache of temporary folders. There's a risk here that the cache    * gets too big    */
DECL|field|tempFolders
specifier|private
specifier|static
name|Cache
argument_list|<
name|TaskAttemptID
argument_list|,
name|Path
argument_list|>
name|tempFolders
init|=
name|CacheBuilder
operator|.
name|newBuilder
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
comment|/**    * Get the task attempt temporary directory in the local filesystem.    * @param conf configuration    * @param uuid some UUID, such as a job UUID    * @param attemptID attempt ID    * @return a local task attempt directory.    * @throws IOException IO problem.    */
DECL|method|getLocalTaskAttemptTempDir (final Configuration conf, final String uuid, final TaskAttemptID attemptID)
specifier|public
specifier|static
name|Path
name|getLocalTaskAttemptTempDir
parameter_list|(
specifier|final
name|Configuration
name|conf
parameter_list|,
specifier|final
name|String
name|uuid
parameter_list|,
specifier|final
name|TaskAttemptID
name|attemptID
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
specifier|final
name|LocalDirAllocator
name|allocator
init|=
operator|new
name|LocalDirAllocator
argument_list|(
name|Constants
operator|.
name|BUFFER_DIR
argument_list|)
decl_stmt|;
return|return
name|tempFolders
operator|.
name|get
argument_list|(
name|attemptID
argument_list|,
parameter_list|()
lambda|->
block|{
return|return
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|conf
argument_list|)
operator|.
name|makeQualified
argument_list|(
name|allocator
operator|.
name|getLocalPathForWrite
argument_list|(
name|uuid
argument_list|,
name|conf
argument_list|)
argument_list|)
return|;
block|}
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
operator|.
name|getCause
argument_list|()
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|UncheckedExecutionException
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|RuntimeException
condition|)
block|{
throw|throw
operator|(
name|RuntimeException
operator|)
name|e
operator|.
name|getCause
argument_list|()
throw|;
block|}
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**    * Remove all information held about task attempts.    * @param attemptID attempt ID.    */
DECL|method|clearTempFolderInfo (final TaskAttemptID attemptID)
specifier|public
specifier|static
name|void
name|clearTempFolderInfo
parameter_list|(
specifier|final
name|TaskAttemptID
name|attemptID
parameter_list|)
block|{
name|tempFolders
operator|.
name|invalidate
argument_list|(
name|attemptID
argument_list|)
expr_stmt|;
block|}
comment|/**    * Reset the temp folder cache; useful in tests.    */
annotation|@
name|VisibleForTesting
DECL|method|resetTempFolderCache ()
specifier|public
specifier|static
name|void
name|resetTempFolderCache
parameter_list|()
block|{
name|tempFolders
operator|.
name|invalidateAll
argument_list|()
expr_stmt|;
block|}
comment|/**    * Try to come up with a good temp directory for different filesystems.    * @param fs filesystem    * @param conf configuration    * @return a qualified path under which temporary work can go.    */
DECL|method|tempDirForStaging (FileSystem fs, Configuration conf)
specifier|public
specifier|static
name|Path
name|tempDirForStaging
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|String
name|fallbackPath
init|=
name|fs
operator|.
name|getScheme
argument_list|()
operator|.
name|equals
argument_list|(
literal|"file"
argument_list|)
condition|?
name|System
operator|.
name|getProperty
argument_list|(
name|JAVA_IO_TMPDIR
argument_list|)
else|:
name|FILESYSTEM_TEMP_PATH
decl_stmt|;
return|return
name|fs
operator|.
name|makeQualified
argument_list|(
operator|new
name|Path
argument_list|(
name|conf
operator|.
name|getTrimmed
argument_list|(
name|FS_S3A_COMMITTER_STAGING_TMP_PATH
argument_list|,
name|fallbackPath
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Get the Application Attempt ID for this job.    * @param conf the config to look in    * @return the Application Attempt ID for a given job.    */
DECL|method|getAppAttemptId (Configuration conf)
specifier|private
specifier|static
name|int
name|getAppAttemptId
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
return|return
name|conf
operator|.
name|getInt
argument_list|(
name|MRJobConfig
operator|.
name|APPLICATION_ATTEMPT_ID
argument_list|,
literal|0
argument_list|)
return|;
block|}
comment|/**    * Build a qualified temporary path for the multipart upload commit    * information in the cluster filesystem.    * Path is built by    * {@link #getMultipartUploadCommitsDirectory(FileSystem, Configuration, String)}    * @param conf configuration defining default FS.    * @param uuid uuid of job    * @return a path which can be used for temporary work    * @throws IOException on an IO failure.    */
DECL|method|getMultipartUploadCommitsDirectory (Configuration conf, String uuid)
specifier|public
specifier|static
name|Path
name|getMultipartUploadCommitsDirectory
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|uuid
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getMultipartUploadCommitsDirectory
argument_list|(
name|FileSystem
operator|.
name|get
argument_list|(
name|conf
argument_list|)
argument_list|,
name|conf
argument_list|,
name|uuid
argument_list|)
return|;
block|}
comment|/**    * Build a qualified temporary path for the multipart upload commit    * information in the supplied filesystem    * (which is expected to be the cluster FS).    * Currently {code $tempDir/$user/$uuid/staging-uploads} where    * {@code tempDir} is from    * {@link #tempDirForStaging(FileSystem, Configuration)}.    * @param fs target FS    * @param conf configuration    * @param uuid uuid of job    * @return a path which can be used for temporary work    * @throws IOException on an IO failure.    */
annotation|@
name|VisibleForTesting
DECL|method|getMultipartUploadCommitsDirectory (FileSystem fs, Configuration conf, String uuid)
specifier|static
name|Path
name|getMultipartUploadCommitsDirectory
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|String
name|uuid
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|path
argument_list|(
name|tempDirForStaging
argument_list|(
name|fs
argument_list|,
name|conf
argument_list|)
argument_list|,
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|getShortUserName
argument_list|()
argument_list|,
name|uuid
argument_list|,
name|STAGING_UPLOADS
argument_list|)
return|;
block|}
comment|/**    * Returns the partition of a relative file path, or null if the path is a    * file name with no relative directory.    *    * @param relative a relative file path    * @return the partition of the relative file path    */
DECL|method|getPartition (String relative)
specifier|protected
specifier|static
name|String
name|getPartition
parameter_list|(
name|String
name|relative
parameter_list|)
block|{
return|return
name|getParent
argument_list|(
name|relative
argument_list|)
return|;
block|}
comment|/**    * Get the set of partitions from the list of files being staged.    * This is all immediate parents of those files. If a file is in the root    * dir, the partition is declared to be    * {@link StagingCommitterConstants#TABLE_ROOT}.    * @param attemptPath path for the attempt    * @param taskOutput list of output files.    * @return list of partitions.    * @throws IOException IO failure    */
DECL|method|getPartitions (Path attemptPath, List<? extends FileStatus> taskOutput)
specifier|public
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|getPartitions
parameter_list|(
name|Path
name|attemptPath
parameter_list|,
name|List
argument_list|<
name|?
extends|extends
name|FileStatus
argument_list|>
name|taskOutput
parameter_list|)
throws|throws
name|IOException
block|{
comment|// get a list of partition directories
name|Set
argument_list|<
name|String
argument_list|>
name|partitions
init|=
name|Sets
operator|.
name|newLinkedHashSet
argument_list|()
decl_stmt|;
for|for
control|(
name|FileStatus
name|fileStatus
range|:
name|taskOutput
control|)
block|{
comment|// sanity check the output paths
name|Path
name|outputFile
init|=
name|fileStatus
operator|.
name|getPath
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|fileStatus
operator|.
name|isFile
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|PathIsDirectoryException
argument_list|(
name|outputFile
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
name|String
name|partition
init|=
name|getPartition
argument_list|(
name|getRelativePath
argument_list|(
name|attemptPath
argument_list|,
name|outputFile
argument_list|)
argument_list|)
decl_stmt|;
name|partitions
operator|.
name|add
argument_list|(
name|partition
operator|!=
literal|null
condition|?
name|partition
else|:
name|TABLE_ROOT
argument_list|)
expr_stmt|;
block|}
return|return
name|partitions
return|;
block|}
block|}
end_class

end_unit

