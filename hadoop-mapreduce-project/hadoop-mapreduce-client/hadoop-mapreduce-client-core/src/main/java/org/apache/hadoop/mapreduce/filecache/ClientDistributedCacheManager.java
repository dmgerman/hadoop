begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.filecache
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|filecache
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
name|FsAction
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
name|security
operator|.
name|TokenCache
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
name|Credentials
import|;
end_import

begin_comment
comment|/**  * Manages internal configuration of the cache by the client for job submission.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|ClientDistributedCacheManager
specifier|public
class|class
name|ClientDistributedCacheManager
block|{
comment|/**    * Determines timestamps of files to be cached, and stores those    * in the configuration. Determines the visibilities of the distributed cache    * files and archives. The visibility of a cache path is "public" if the leaf    * component has READ permissions for others, and the parent subdirs have     * EXECUTE permissions for others.    *     * This is an internal method!    *     * @param job    * @throws IOException    */
DECL|method|determineTimestampsAndCacheVisibilities (Configuration job)
specifier|public
specifier|static
name|void
name|determineTimestampsAndCacheVisibilities
parameter_list|(
name|Configuration
name|job
parameter_list|)
throws|throws
name|IOException
block|{
name|Map
argument_list|<
name|URI
argument_list|,
name|FileStatus
argument_list|>
name|statCache
init|=
operator|new
name|HashMap
argument_list|<
name|URI
argument_list|,
name|FileStatus
argument_list|>
argument_list|()
decl_stmt|;
name|determineTimestampsAndCacheVisibilities
argument_list|(
name|job
argument_list|,
name|statCache
argument_list|)
expr_stmt|;
block|}
comment|/**    * See ClientDistributedCacheManager#determineTimestampsAndCacheVisibilities(    * Configuration).    *    * @param job Configuration of a job    * @param statCache A map containing cached file status objects    * @throws IOException if there is a problem with the underlying filesystem    */
DECL|method|determineTimestampsAndCacheVisibilities (Configuration job, Map<URI, FileStatus> statCache)
specifier|public
specifier|static
name|void
name|determineTimestampsAndCacheVisibilities
parameter_list|(
name|Configuration
name|job
parameter_list|,
name|Map
argument_list|<
name|URI
argument_list|,
name|FileStatus
argument_list|>
name|statCache
parameter_list|)
throws|throws
name|IOException
block|{
name|determineTimestamps
argument_list|(
name|job
argument_list|,
name|statCache
argument_list|)
expr_stmt|;
name|determineCacheVisibilities
argument_list|(
name|job
argument_list|,
name|statCache
argument_list|)
expr_stmt|;
block|}
comment|/**    * Determines timestamps of files to be cached, and stores those    * in the configuration.  This is intended to be used internally by JobClient    * after all cache files have been added.    *     * This is an internal method!    *     * @param job Configuration of a job.    * @throws IOException    */
DECL|method|determineTimestamps (Configuration job, Map<URI, FileStatus> statCache)
specifier|public
specifier|static
name|void
name|determineTimestamps
parameter_list|(
name|Configuration
name|job
parameter_list|,
name|Map
argument_list|<
name|URI
argument_list|,
name|FileStatus
argument_list|>
name|statCache
parameter_list|)
throws|throws
name|IOException
block|{
name|URI
index|[]
name|tarchives
init|=
name|DistributedCache
operator|.
name|getCacheArchives
argument_list|(
name|job
argument_list|)
decl_stmt|;
if|if
condition|(
name|tarchives
operator|!=
literal|null
condition|)
block|{
name|FileStatus
name|status
init|=
name|getFileStatus
argument_list|(
name|job
argument_list|,
name|tarchives
index|[
literal|0
index|]
argument_list|,
name|statCache
argument_list|)
decl_stmt|;
name|StringBuilder
name|archiveFileSizes
init|=
operator|new
name|StringBuilder
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|status
operator|.
name|getLen
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|StringBuilder
name|archiveTimestamps
init|=
operator|new
name|StringBuilder
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|status
operator|.
name|getModificationTime
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|tarchives
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|status
operator|=
name|getFileStatus
argument_list|(
name|job
argument_list|,
name|tarchives
index|[
name|i
index|]
argument_list|,
name|statCache
argument_list|)
expr_stmt|;
name|archiveFileSizes
operator|.
name|append
argument_list|(
literal|","
argument_list|)
expr_stmt|;
name|archiveFileSizes
operator|.
name|append
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|status
operator|.
name|getLen
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|archiveTimestamps
operator|.
name|append
argument_list|(
literal|","
argument_list|)
expr_stmt|;
name|archiveTimestamps
operator|.
name|append
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|status
operator|.
name|getModificationTime
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|job
operator|.
name|set
argument_list|(
name|MRJobConfig
operator|.
name|CACHE_ARCHIVES_SIZES
argument_list|,
name|archiveFileSizes
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|setArchiveTimestamps
argument_list|(
name|job
argument_list|,
name|archiveTimestamps
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|URI
index|[]
name|tfiles
init|=
name|DistributedCache
operator|.
name|getCacheFiles
argument_list|(
name|job
argument_list|)
decl_stmt|;
if|if
condition|(
name|tfiles
operator|!=
literal|null
condition|)
block|{
name|FileStatus
name|status
init|=
name|getFileStatus
argument_list|(
name|job
argument_list|,
name|tfiles
index|[
literal|0
index|]
argument_list|,
name|statCache
argument_list|)
decl_stmt|;
name|StringBuilder
name|fileSizes
init|=
operator|new
name|StringBuilder
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|status
operator|.
name|getLen
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|StringBuilder
name|fileTimestamps
init|=
operator|new
name|StringBuilder
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|status
operator|.
name|getModificationTime
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|tfiles
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|status
operator|=
name|getFileStatus
argument_list|(
name|job
argument_list|,
name|tfiles
index|[
name|i
index|]
argument_list|,
name|statCache
argument_list|)
expr_stmt|;
name|fileSizes
operator|.
name|append
argument_list|(
literal|","
argument_list|)
expr_stmt|;
name|fileSizes
operator|.
name|append
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|status
operator|.
name|getLen
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|fileTimestamps
operator|.
name|append
argument_list|(
literal|","
argument_list|)
expr_stmt|;
name|fileTimestamps
operator|.
name|append
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|status
operator|.
name|getModificationTime
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|job
operator|.
name|set
argument_list|(
name|MRJobConfig
operator|.
name|CACHE_FILES_SIZES
argument_list|,
name|fileSizes
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|setFileTimestamps
argument_list|(
name|job
argument_list|,
name|fileTimestamps
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * For each archive or cache file - get the corresponding delegation token    * @param job    * @param credentials    * @throws IOException    */
DECL|method|getDelegationTokens (Configuration job, Credentials credentials)
specifier|public
specifier|static
name|void
name|getDelegationTokens
parameter_list|(
name|Configuration
name|job
parameter_list|,
name|Credentials
name|credentials
parameter_list|)
throws|throws
name|IOException
block|{
name|URI
index|[]
name|tarchives
init|=
name|DistributedCache
operator|.
name|getCacheArchives
argument_list|(
name|job
argument_list|)
decl_stmt|;
name|URI
index|[]
name|tfiles
init|=
name|DistributedCache
operator|.
name|getCacheFiles
argument_list|(
name|job
argument_list|)
decl_stmt|;
name|int
name|size
init|=
operator|(
name|tarchives
operator|!=
literal|null
condition|?
name|tarchives
operator|.
name|length
else|:
literal|0
operator|)
operator|+
operator|(
name|tfiles
operator|!=
literal|null
condition|?
name|tfiles
operator|.
name|length
else|:
literal|0
operator|)
decl_stmt|;
name|Path
index|[]
name|ps
init|=
operator|new
name|Path
index|[
name|size
index|]
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|tarchives
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|i
operator|=
literal|0
init|;
name|i
operator|<
name|tarchives
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|ps
index|[
name|i
index|]
operator|=
operator|new
name|Path
argument_list|(
name|tarchives
index|[
name|i
index|]
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|tfiles
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|tfiles
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|ps
index|[
name|i
operator|+
name|j
index|]
operator|=
operator|new
name|Path
argument_list|(
name|tfiles
index|[
name|j
index|]
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|TokenCache
operator|.
name|obtainTokensForNamenodes
argument_list|(
name|credentials
argument_list|,
name|ps
argument_list|,
name|job
argument_list|)
expr_stmt|;
block|}
comment|/**    * Determines the visibilities of the distributed cache files and     * archives. The visibility of a cache path is "public" if the leaf component    * has READ permissions for others, and the parent subdirs have     * EXECUTE permissions for others    * @param job    * @throws IOException    */
DECL|method|determineCacheVisibilities (Configuration job, Map<URI, FileStatus> statCache)
specifier|public
specifier|static
name|void
name|determineCacheVisibilities
parameter_list|(
name|Configuration
name|job
parameter_list|,
name|Map
argument_list|<
name|URI
argument_list|,
name|FileStatus
argument_list|>
name|statCache
parameter_list|)
throws|throws
name|IOException
block|{
name|URI
index|[]
name|tarchives
init|=
name|DistributedCache
operator|.
name|getCacheArchives
argument_list|(
name|job
argument_list|)
decl_stmt|;
if|if
condition|(
name|tarchives
operator|!=
literal|null
condition|)
block|{
name|StringBuilder
name|archiveVisibilities
init|=
operator|new
name|StringBuilder
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|isPublic
argument_list|(
name|job
argument_list|,
name|tarchives
index|[
literal|0
index|]
argument_list|,
name|statCache
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|tarchives
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|archiveVisibilities
operator|.
name|append
argument_list|(
literal|","
argument_list|)
expr_stmt|;
name|archiveVisibilities
operator|.
name|append
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|isPublic
argument_list|(
name|job
argument_list|,
name|tarchives
index|[
name|i
index|]
argument_list|,
name|statCache
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|setArchiveVisibilities
argument_list|(
name|job
argument_list|,
name|archiveVisibilities
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|URI
index|[]
name|tfiles
init|=
name|DistributedCache
operator|.
name|getCacheFiles
argument_list|(
name|job
argument_list|)
decl_stmt|;
if|if
condition|(
name|tfiles
operator|!=
literal|null
condition|)
block|{
name|StringBuilder
name|fileVisibilities
init|=
operator|new
name|StringBuilder
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|isPublic
argument_list|(
name|job
argument_list|,
name|tfiles
index|[
literal|0
index|]
argument_list|,
name|statCache
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|tfiles
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|fileVisibilities
operator|.
name|append
argument_list|(
literal|","
argument_list|)
expr_stmt|;
name|fileVisibilities
operator|.
name|append
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|isPublic
argument_list|(
name|job
argument_list|,
name|tfiles
index|[
name|i
index|]
argument_list|,
name|statCache
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|setFileVisibilities
argument_list|(
name|job
argument_list|,
name|fileVisibilities
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * This is to check the public/private visibility of the archives to be    * localized.    *     * @param conf Configuration which stores the timestamp's    * @param booleans comma separated list of booleans (true - public)    * The order should be the same as the order in which the archives are added.    */
DECL|method|setArchiveVisibilities (Configuration conf, String booleans)
specifier|static
name|void
name|setArchiveVisibilities
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|booleans
parameter_list|)
block|{
name|conf
operator|.
name|set
argument_list|(
name|MRJobConfig
operator|.
name|CACHE_ARCHIVES_VISIBILITIES
argument_list|,
name|booleans
argument_list|)
expr_stmt|;
block|}
comment|/**    * This is to check the public/private visibility of the files to be localized    *     * @param conf Configuration which stores the timestamp's    * @param booleans comma separated list of booleans (true - public)    * The order should be the same as the order in which the files are added.    */
DECL|method|setFileVisibilities (Configuration conf, String booleans)
specifier|static
name|void
name|setFileVisibilities
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|booleans
parameter_list|)
block|{
name|conf
operator|.
name|set
argument_list|(
name|MRJobConfig
operator|.
name|CACHE_FILE_VISIBILITIES
argument_list|,
name|booleans
argument_list|)
expr_stmt|;
block|}
comment|/**    * This is to check the timestamp of the archives to be localized.    *     * @param conf Configuration which stores the timestamp's    * @param timestamps comma separated list of timestamps of archives.    * The order should be the same as the order in which the archives are added.    */
DECL|method|setArchiveTimestamps (Configuration conf, String timestamps)
specifier|static
name|void
name|setArchiveTimestamps
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|timestamps
parameter_list|)
block|{
name|conf
operator|.
name|set
argument_list|(
name|MRJobConfig
operator|.
name|CACHE_ARCHIVES_TIMESTAMPS
argument_list|,
name|timestamps
argument_list|)
expr_stmt|;
block|}
comment|/**    * This is to check the timestamp of the files to be localized.    *     * @param conf Configuration which stores the timestamp's    * @param timestamps comma separated list of timestamps of files.    * The order should be the same as the order in which the files are added.    */
DECL|method|setFileTimestamps (Configuration conf, String timestamps)
specifier|static
name|void
name|setFileTimestamps
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|timestamps
parameter_list|)
block|{
name|conf
operator|.
name|set
argument_list|(
name|MRJobConfig
operator|.
name|CACHE_FILE_TIMESTAMPS
argument_list|,
name|timestamps
argument_list|)
expr_stmt|;
block|}
comment|/**    * Gets the file status for the given URI.  If the URI is in the cache,    * returns it.  Otherwise, fetches it and adds it to the cache.    */
DECL|method|getFileStatus (Configuration job, URI uri, Map<URI, FileStatus> statCache)
specifier|private
specifier|static
name|FileStatus
name|getFileStatus
parameter_list|(
name|Configuration
name|job
parameter_list|,
name|URI
name|uri
parameter_list|,
name|Map
argument_list|<
name|URI
argument_list|,
name|FileStatus
argument_list|>
name|statCache
parameter_list|)
throws|throws
name|IOException
block|{
name|FileSystem
name|fileSystem
init|=
name|FileSystem
operator|.
name|get
argument_list|(
name|uri
argument_list|,
name|job
argument_list|)
decl_stmt|;
return|return
name|getFileStatus
argument_list|(
name|fileSystem
argument_list|,
name|uri
argument_list|,
name|statCache
argument_list|)
return|;
block|}
comment|/**    * Returns a boolean to denote whether a cache file is visible to all(public)    * or not    * @param conf the configuration    * @param uri the URI to test    * @return true if the path in the uri is visible to all, false otherwise    * @throws IOException thrown if a file system operation fails    */
DECL|method|isPublic (Configuration conf, URI uri, Map<URI, FileStatus> statCache)
specifier|static
name|boolean
name|isPublic
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|URI
name|uri
parameter_list|,
name|Map
argument_list|<
name|URI
argument_list|,
name|FileStatus
argument_list|>
name|statCache
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|isPublic
init|=
literal|true
decl_stmt|;
name|FileSystem
name|fs
init|=
name|FileSystem
operator|.
name|get
argument_list|(
name|uri
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|Path
name|current
init|=
operator|new
name|Path
argument_list|(
name|uri
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|current
operator|=
name|fs
operator|.
name|makeQualified
argument_list|(
name|current
argument_list|)
expr_stmt|;
comment|// If we're looking at a wildcarded path, we only need to check that the
comment|// ancestors allow execution.  Otherwise, look for read permissions in
comment|// addition to the ancestors' permissions.
if|if
condition|(
operator|!
name|current
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|DistributedCache
operator|.
name|WILDCARD
argument_list|)
condition|)
block|{
name|isPublic
operator|=
name|checkPermissionOfOther
argument_list|(
name|fs
argument_list|,
name|current
argument_list|,
name|FsAction
operator|.
name|READ
argument_list|,
name|statCache
argument_list|)
expr_stmt|;
block|}
return|return
name|isPublic
operator|&&
name|ancestorsHaveExecutePermissions
argument_list|(
name|fs
argument_list|,
name|current
operator|.
name|getParent
argument_list|()
argument_list|,
name|statCache
argument_list|)
return|;
block|}
comment|/**    * Returns true if all ancestors of the specified path have the 'execute'    * permission set for all users (i.e. that other users can traverse    * the directory heirarchy to the given path)    */
DECL|method|ancestorsHaveExecutePermissions (FileSystem fs, Path path, Map<URI, FileStatus> statCache)
specifier|static
name|boolean
name|ancestorsHaveExecutePermissions
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|path
parameter_list|,
name|Map
argument_list|<
name|URI
argument_list|,
name|FileStatus
argument_list|>
name|statCache
parameter_list|)
throws|throws
name|IOException
block|{
name|Path
name|current
init|=
name|path
decl_stmt|;
while|while
condition|(
name|current
operator|!=
literal|null
condition|)
block|{
comment|//the subdirs in the path should have execute permissions for others
if|if
condition|(
operator|!
name|checkPermissionOfOther
argument_list|(
name|fs
argument_list|,
name|current
argument_list|,
name|FsAction
operator|.
name|EXECUTE
argument_list|,
name|statCache
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|current
operator|=
name|current
operator|.
name|getParent
argument_list|()
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
comment|/**    * Checks for a given path whether the Other permissions on it     * imply the permission in the passed FsAction    * @param fs    * @param path    * @param action    * @return true if the path in the uri is visible to all, false otherwise    * @throws IOException    */
DECL|method|checkPermissionOfOther (FileSystem fs, Path path, FsAction action, Map<URI, FileStatus> statCache)
specifier|private
specifier|static
name|boolean
name|checkPermissionOfOther
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|path
parameter_list|,
name|FsAction
name|action
parameter_list|,
name|Map
argument_list|<
name|URI
argument_list|,
name|FileStatus
argument_list|>
name|statCache
parameter_list|)
throws|throws
name|IOException
block|{
name|FileStatus
name|status
init|=
name|getFileStatus
argument_list|(
name|fs
argument_list|,
name|path
operator|.
name|toUri
argument_list|()
argument_list|,
name|statCache
argument_list|)
decl_stmt|;
name|FsPermission
name|perms
init|=
name|status
operator|.
name|getPermission
argument_list|()
decl_stmt|;
name|FsAction
name|otherAction
init|=
name|perms
operator|.
name|getOtherAction
argument_list|()
decl_stmt|;
if|if
condition|(
name|otherAction
operator|.
name|implies
argument_list|(
name|action
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
DECL|method|getFileStatus (FileSystem fs, URI uri, Map<URI, FileStatus> statCache)
specifier|private
specifier|static
name|FileStatus
name|getFileStatus
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|URI
name|uri
parameter_list|,
name|Map
argument_list|<
name|URI
argument_list|,
name|FileStatus
argument_list|>
name|statCache
parameter_list|)
throws|throws
name|IOException
block|{
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
name|uri
argument_list|)
decl_stmt|;
if|if
condition|(
name|path
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|DistributedCache
operator|.
name|WILDCARD
argument_list|)
condition|)
block|{
name|path
operator|=
name|path
operator|.
name|getParent
argument_list|()
expr_stmt|;
name|uri
operator|=
name|path
operator|.
name|toUri
argument_list|()
expr_stmt|;
block|}
name|FileStatus
name|stat
init|=
name|statCache
operator|.
name|get
argument_list|(
name|uri
argument_list|)
decl_stmt|;
if|if
condition|(
name|stat
operator|==
literal|null
condition|)
block|{
name|stat
operator|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|statCache
operator|.
name|put
argument_list|(
name|uri
argument_list|,
name|stat
argument_list|)
expr_stmt|;
block|}
return|return
name|stat
return|;
block|}
block|}
end_class

end_unit

