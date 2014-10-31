begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
import|;
end_import

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
name|URISyntaxException
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
name|concurrent
operator|.
name|Callable
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Future
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
name|commons
operator|.
name|logging
operator|.
name|Log
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
name|hadoop
operator|.
name|classification
operator|.
name|InterfaceAudience
operator|.
name|LimitedPrivate
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
name|FileContext
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
name|FileUtil
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
name|LocalFileSystem
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
name|Options
operator|.
name|Rename
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
name|RunJar
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
name|Shell
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|LocalResource
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|LocalResourceVisibility
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
name|cache
operator|.
name|CacheLoader
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
name|LoadingCache
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
name|Futures
import|;
end_import

begin_comment
comment|/**  * Download a single URL to the local disk.  *  */
end_comment

begin_class
annotation|@
name|LimitedPrivate
argument_list|(
block|{
literal|"YARN"
block|,
literal|"MapReduce"
block|}
argument_list|)
DECL|class|FSDownload
specifier|public
class|class
name|FSDownload
implements|implements
name|Callable
argument_list|<
name|Path
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
name|FSDownload
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|files
specifier|private
name|FileContext
name|files
decl_stmt|;
DECL|field|userUgi
specifier|private
specifier|final
name|UserGroupInformation
name|userUgi
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|resource
specifier|private
name|LocalResource
name|resource
decl_stmt|;
DECL|field|statCache
specifier|private
specifier|final
name|LoadingCache
argument_list|<
name|Path
argument_list|,
name|Future
argument_list|<
name|FileStatus
argument_list|>
argument_list|>
name|statCache
decl_stmt|;
comment|/** The local FS dir path under which this resource is to be localized to */
DECL|field|destDirPath
specifier|private
name|Path
name|destDirPath
decl_stmt|;
DECL|field|cachePerms
specifier|private
specifier|static
specifier|final
name|FsPermission
name|cachePerms
init|=
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
literal|0755
argument_list|)
decl_stmt|;
DECL|field|PUBLIC_FILE_PERMS
specifier|static
specifier|final
name|FsPermission
name|PUBLIC_FILE_PERMS
init|=
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
literal|0555
argument_list|)
decl_stmt|;
DECL|field|PRIVATE_FILE_PERMS
specifier|static
specifier|final
name|FsPermission
name|PRIVATE_FILE_PERMS
init|=
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
literal|0500
argument_list|)
decl_stmt|;
DECL|field|PUBLIC_DIR_PERMS
specifier|static
specifier|final
name|FsPermission
name|PUBLIC_DIR_PERMS
init|=
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
literal|0755
argument_list|)
decl_stmt|;
DECL|field|PRIVATE_DIR_PERMS
specifier|static
specifier|final
name|FsPermission
name|PRIVATE_DIR_PERMS
init|=
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
literal|0700
argument_list|)
decl_stmt|;
DECL|method|FSDownload (FileContext files, UserGroupInformation ugi, Configuration conf, Path destDirPath, LocalResource resource)
specifier|public
name|FSDownload
parameter_list|(
name|FileContext
name|files
parameter_list|,
name|UserGroupInformation
name|ugi
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|Path
name|destDirPath
parameter_list|,
name|LocalResource
name|resource
parameter_list|)
block|{
name|this
argument_list|(
name|files
argument_list|,
name|ugi
argument_list|,
name|conf
argument_list|,
name|destDirPath
argument_list|,
name|resource
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|FSDownload (FileContext files, UserGroupInformation ugi, Configuration conf, Path destDirPath, LocalResource resource, LoadingCache<Path,Future<FileStatus>> statCache)
specifier|public
name|FSDownload
parameter_list|(
name|FileContext
name|files
parameter_list|,
name|UserGroupInformation
name|ugi
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|Path
name|destDirPath
parameter_list|,
name|LocalResource
name|resource
parameter_list|,
name|LoadingCache
argument_list|<
name|Path
argument_list|,
name|Future
argument_list|<
name|FileStatus
argument_list|>
argument_list|>
name|statCache
parameter_list|)
block|{
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
name|this
operator|.
name|destDirPath
operator|=
name|destDirPath
expr_stmt|;
name|this
operator|.
name|files
operator|=
name|files
expr_stmt|;
name|this
operator|.
name|userUgi
operator|=
name|ugi
expr_stmt|;
name|this
operator|.
name|resource
operator|=
name|resource
expr_stmt|;
name|this
operator|.
name|statCache
operator|=
name|statCache
expr_stmt|;
block|}
DECL|method|getResource ()
name|LocalResource
name|getResource
parameter_list|()
block|{
return|return
name|resource
return|;
block|}
DECL|method|createDir (Path path, FsPermission perm)
specifier|private
name|void
name|createDir
parameter_list|(
name|Path
name|path
parameter_list|,
name|FsPermission
name|perm
parameter_list|)
throws|throws
name|IOException
block|{
name|files
operator|.
name|mkdir
argument_list|(
name|path
argument_list|,
name|perm
argument_list|,
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|perm
operator|.
name|equals
argument_list|(
name|files
operator|.
name|getUMask
argument_list|()
operator|.
name|applyUMask
argument_list|(
name|perm
argument_list|)
argument_list|)
condition|)
block|{
name|files
operator|.
name|setPermission
argument_list|(
name|path
argument_list|,
name|perm
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Creates the cache loader for the status loading cache. This should be used    * to create an instance of the status cache that is passed into the    * FSDownload constructor.    */
specifier|public
specifier|static
name|CacheLoader
argument_list|<
name|Path
argument_list|,
name|Future
argument_list|<
name|FileStatus
argument_list|>
argument_list|>
DECL|method|createStatusCacheLoader (final Configuration conf)
name|createStatusCacheLoader
parameter_list|(
specifier|final
name|Configuration
name|conf
parameter_list|)
block|{
return|return
operator|new
name|CacheLoader
argument_list|<
name|Path
argument_list|,
name|Future
argument_list|<
name|FileStatus
argument_list|>
argument_list|>
argument_list|()
block|{
specifier|public
name|Future
argument_list|<
name|FileStatus
argument_list|>
name|load
parameter_list|(
name|Path
name|path
parameter_list|)
block|{
try|try
block|{
name|FileSystem
name|fs
init|=
name|path
operator|.
name|getFileSystem
argument_list|(
name|conf
argument_list|)
decl_stmt|;
return|return
name|Futures
operator|.
name|immediateFuture
argument_list|(
name|fs
operator|.
name|getFileStatus
argument_list|(
name|path
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|th
parameter_list|)
block|{
comment|// report failures so it can be memoized
return|return
name|Futures
operator|.
name|immediateFailedFuture
argument_list|(
name|th
argument_list|)
return|;
block|}
block|}
block|}
return|;
block|}
comment|/**    * Returns a boolean to denote whether a cache file is visible to all (public)    * or not    *    * @return true if the path in the current path is visible to all, false    * otherwise    */
annotation|@
name|VisibleForTesting
DECL|method|isPublic (FileSystem fs, Path current, FileStatus sStat, LoadingCache<Path,Future<FileStatus>> statCache)
specifier|static
name|boolean
name|isPublic
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|current
parameter_list|,
name|FileStatus
name|sStat
parameter_list|,
name|LoadingCache
argument_list|<
name|Path
argument_list|,
name|Future
argument_list|<
name|FileStatus
argument_list|>
argument_list|>
name|statCache
parameter_list|)
throws|throws
name|IOException
block|{
name|current
operator|=
name|fs
operator|.
name|makeQualified
argument_list|(
name|current
argument_list|)
expr_stmt|;
comment|//the leaf level file should be readable by others
if|if
condition|(
operator|!
name|checkPublicPermsForAll
argument_list|(
name|fs
argument_list|,
name|sStat
argument_list|,
name|FsAction
operator|.
name|READ_EXECUTE
argument_list|,
name|FsAction
operator|.
name|READ
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|Shell
operator|.
name|WINDOWS
operator|&&
name|fs
operator|instanceof
name|LocalFileSystem
condition|)
block|{
comment|// Relax the requirement for public cache on LFS on Windows since default
comment|// permissions are "700" all the way up to the drive letter. In this
comment|// model, the only requirement for a user is to give EVERYONE group
comment|// permission on the file and the file will be considered public.
comment|// This code path is only hit when fs.default.name is file:/// (mainly
comment|// in tests).
return|return
literal|true
return|;
block|}
return|return
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
DECL|method|checkPublicPermsForAll (FileSystem fs, FileStatus status, FsAction dir, FsAction file)
specifier|private
specifier|static
name|boolean
name|checkPublicPermsForAll
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|FileStatus
name|status
parameter_list|,
name|FsAction
name|dir
parameter_list|,
name|FsAction
name|file
parameter_list|)
throws|throws
name|IOException
block|{
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
name|status
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|otherAction
operator|.
name|implies
argument_list|(
name|dir
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
for|for
control|(
name|FileStatus
name|child
range|:
name|fs
operator|.
name|listStatus
argument_list|(
name|status
operator|.
name|getPath
argument_list|()
argument_list|)
control|)
block|{
if|if
condition|(
operator|!
name|checkPublicPermsForAll
argument_list|(
name|fs
argument_list|,
name|child
argument_list|,
name|dir
argument_list|,
name|file
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
return|return
operator|(
name|otherAction
operator|.
name|implies
argument_list|(
name|file
argument_list|)
operator|)
return|;
block|}
comment|/**    * Returns true if all ancestors of the specified path have the 'execute'    * permission set for all users (i.e. that other users can traverse    * the directory hierarchy to the given path)    */
annotation|@
name|VisibleForTesting
DECL|method|ancestorsHaveExecutePermissions (FileSystem fs, Path path, LoadingCache<Path,Future<FileStatus>> statCache)
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
name|LoadingCache
argument_list|<
name|Path
argument_list|,
name|Future
argument_list|<
name|FileStatus
argument_list|>
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
DECL|method|checkPermissionOfOther (FileSystem fs, Path path, FsAction action, LoadingCache<Path,Future<FileStatus>> statCache)
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
name|LoadingCache
argument_list|<
name|Path
argument_list|,
name|Future
argument_list|<
name|FileStatus
argument_list|>
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
return|return
name|otherAction
operator|.
name|implies
argument_list|(
name|action
argument_list|)
return|;
block|}
comment|/**    * Obtains the file status, first by checking the stat cache if it is    * available, and then by getting it explicitly from the filesystem. If we got    * the file status from the filesystem, it is added to the stat cache.    *    * The stat cache is expected to be managed by callers who provided it to    * FSDownload.    */
DECL|method|getFileStatus (final FileSystem fs, final Path path, LoadingCache<Path,Future<FileStatus>> statCache)
specifier|private
specifier|static
name|FileStatus
name|getFileStatus
parameter_list|(
specifier|final
name|FileSystem
name|fs
parameter_list|,
specifier|final
name|Path
name|path
parameter_list|,
name|LoadingCache
argument_list|<
name|Path
argument_list|,
name|Future
argument_list|<
name|FileStatus
argument_list|>
argument_list|>
name|statCache
parameter_list|)
throws|throws
name|IOException
block|{
comment|// if the stat cache does not exist, simply query the filesystem
if|if
condition|(
name|statCache
operator|==
literal|null
condition|)
block|{
return|return
name|fs
operator|.
name|getFileStatus
argument_list|(
name|path
argument_list|)
return|;
block|}
try|try
block|{
comment|// get or load it from the cache
return|return
name|statCache
operator|.
name|get
argument_list|(
name|path
argument_list|)
operator|.
name|get
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|e
parameter_list|)
block|{
name|Throwable
name|cause
init|=
name|e
operator|.
name|getCause
argument_list|()
decl_stmt|;
comment|// the underlying exception should normally be IOException
if|if
condition|(
name|cause
operator|instanceof
name|IOException
condition|)
block|{
throw|throw
operator|(
name|IOException
operator|)
name|cause
throw|;
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|cause
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
comment|// should not happen
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|copy (Path sCopy, Path dstdir)
specifier|private
name|Path
name|copy
parameter_list|(
name|Path
name|sCopy
parameter_list|,
name|Path
name|dstdir
parameter_list|)
throws|throws
name|IOException
block|{
name|FileSystem
name|sourceFs
init|=
name|sCopy
operator|.
name|getFileSystem
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|Path
name|dCopy
init|=
operator|new
name|Path
argument_list|(
name|dstdir
argument_list|,
literal|"tmp_"
operator|+
name|sCopy
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|FileStatus
name|sStat
init|=
name|sourceFs
operator|.
name|getFileStatus
argument_list|(
name|sCopy
argument_list|)
decl_stmt|;
if|if
condition|(
name|sStat
operator|.
name|getModificationTime
argument_list|()
operator|!=
name|resource
operator|.
name|getTimestamp
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Resource "
operator|+
name|sCopy
operator|+
literal|" changed on src filesystem (expected "
operator|+
name|resource
operator|.
name|getTimestamp
argument_list|()
operator|+
literal|", was "
operator|+
name|sStat
operator|.
name|getModificationTime
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
name|resource
operator|.
name|getVisibility
argument_list|()
operator|==
name|LocalResourceVisibility
operator|.
name|PUBLIC
condition|)
block|{
if|if
condition|(
operator|!
name|isPublic
argument_list|(
name|sourceFs
argument_list|,
name|sCopy
argument_list|,
name|sStat
argument_list|,
name|statCache
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Resource "
operator|+
name|sCopy
operator|+
literal|" is not publicly accessable and as such cannot be part of the"
operator|+
literal|" public cache."
argument_list|)
throw|;
block|}
block|}
name|FileUtil
operator|.
name|copy
argument_list|(
name|sourceFs
argument_list|,
name|sStat
argument_list|,
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|conf
argument_list|)
argument_list|,
name|dCopy
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
name|conf
argument_list|)
expr_stmt|;
return|return
name|dCopy
return|;
block|}
DECL|method|unpack (File localrsrc, File dst)
specifier|private
name|long
name|unpack
parameter_list|(
name|File
name|localrsrc
parameter_list|,
name|File
name|dst
parameter_list|)
throws|throws
name|IOException
block|{
switch|switch
condition|(
name|resource
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|ARCHIVE
case|:
block|{
name|String
name|lowerDst
init|=
name|dst
operator|.
name|getName
argument_list|()
operator|.
name|toLowerCase
argument_list|()
decl_stmt|;
if|if
condition|(
name|lowerDst
operator|.
name|endsWith
argument_list|(
literal|".jar"
argument_list|)
condition|)
block|{
name|RunJar
operator|.
name|unJar
argument_list|(
name|localrsrc
argument_list|,
name|dst
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|lowerDst
operator|.
name|endsWith
argument_list|(
literal|".zip"
argument_list|)
condition|)
block|{
name|FileUtil
operator|.
name|unZip
argument_list|(
name|localrsrc
argument_list|,
name|dst
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|lowerDst
operator|.
name|endsWith
argument_list|(
literal|".tar.gz"
argument_list|)
operator|||
name|lowerDst
operator|.
name|endsWith
argument_list|(
literal|".tgz"
argument_list|)
operator|||
name|lowerDst
operator|.
name|endsWith
argument_list|(
literal|".tar"
argument_list|)
condition|)
block|{
name|FileUtil
operator|.
name|unTar
argument_list|(
name|localrsrc
argument_list|,
name|dst
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Cannot unpack "
operator|+
name|localrsrc
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|localrsrc
operator|.
name|renameTo
argument_list|(
name|dst
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unable to rename file: ["
operator|+
name|localrsrc
operator|+
literal|"] to ["
operator|+
name|dst
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
block|}
break|break;
case|case
name|PATTERN
case|:
block|{
name|String
name|lowerDst
init|=
name|dst
operator|.
name|getName
argument_list|()
operator|.
name|toLowerCase
argument_list|()
decl_stmt|;
if|if
condition|(
name|lowerDst
operator|.
name|endsWith
argument_list|(
literal|".jar"
argument_list|)
condition|)
block|{
name|String
name|p
init|=
name|resource
operator|.
name|getPattern
argument_list|()
decl_stmt|;
name|RunJar
operator|.
name|unJar
argument_list|(
name|localrsrc
argument_list|,
name|dst
argument_list|,
name|p
operator|==
literal|null
condition|?
name|RunJar
operator|.
name|MATCH_ANY
else|:
name|Pattern
operator|.
name|compile
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
name|File
name|newDst
init|=
operator|new
name|File
argument_list|(
name|dst
argument_list|,
name|dst
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|dst
operator|.
name|exists
argument_list|()
operator|&&
operator|!
name|dst
operator|.
name|mkdir
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unable to create directory: ["
operator|+
name|dst
operator|+
literal|"]"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|localrsrc
operator|.
name|renameTo
argument_list|(
name|newDst
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unable to rename file: ["
operator|+
name|localrsrc
operator|+
literal|"] to ["
operator|+
name|newDst
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
elseif|else
if|if
condition|(
name|lowerDst
operator|.
name|endsWith
argument_list|(
literal|".zip"
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Treating ["
operator|+
name|localrsrc
operator|+
literal|"] as an archive even though it "
operator|+
literal|"was specified as PATTERN"
argument_list|)
expr_stmt|;
name|FileUtil
operator|.
name|unZip
argument_list|(
name|localrsrc
argument_list|,
name|dst
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|lowerDst
operator|.
name|endsWith
argument_list|(
literal|".tar.gz"
argument_list|)
operator|||
name|lowerDst
operator|.
name|endsWith
argument_list|(
literal|".tgz"
argument_list|)
operator|||
name|lowerDst
operator|.
name|endsWith
argument_list|(
literal|".tar"
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Treating ["
operator|+
name|localrsrc
operator|+
literal|"] as an archive even though it "
operator|+
literal|"was specified as PATTERN"
argument_list|)
expr_stmt|;
name|FileUtil
operator|.
name|unTar
argument_list|(
name|localrsrc
argument_list|,
name|dst
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Cannot unpack "
operator|+
name|localrsrc
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|localrsrc
operator|.
name|renameTo
argument_list|(
name|dst
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unable to rename file: ["
operator|+
name|localrsrc
operator|+
literal|"] to ["
operator|+
name|dst
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
block|}
break|break;
case|case
name|FILE
case|:
default|default:
if|if
condition|(
operator|!
name|localrsrc
operator|.
name|renameTo
argument_list|(
name|dst
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unable to rename file: ["
operator|+
name|localrsrc
operator|+
literal|"] to ["
operator|+
name|dst
operator|+
literal|"]"
argument_list|)
throw|;
block|}
break|break;
block|}
if|if
condition|(
name|localrsrc
operator|.
name|isFile
argument_list|()
condition|)
block|{
try|try
block|{
name|files
operator|.
name|delete
argument_list|(
operator|new
name|Path
argument_list|(
name|localrsrc
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ignore
parameter_list|)
block|{       }
block|}
return|return
literal|0
return|;
comment|// TODO Should calculate here before returning
comment|//return FileUtil.getDU(destDir);
block|}
annotation|@
name|Override
DECL|method|call ()
specifier|public
name|Path
name|call
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Path
name|sCopy
decl_stmt|;
try|try
block|{
name|sCopy
operator|=
name|ConverterUtils
operator|.
name|getPathFromYarnURL
argument_list|(
name|resource
operator|.
name|getResource
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Invalid resource"
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|createDir
argument_list|(
name|destDirPath
argument_list|,
name|cachePerms
argument_list|)
expr_stmt|;
specifier|final
name|Path
name|dst_work
init|=
operator|new
name|Path
argument_list|(
name|destDirPath
operator|+
literal|"_tmp"
argument_list|)
decl_stmt|;
name|createDir
argument_list|(
name|dst_work
argument_list|,
name|cachePerms
argument_list|)
expr_stmt|;
name|Path
name|dFinal
init|=
name|files
operator|.
name|makeQualified
argument_list|(
operator|new
name|Path
argument_list|(
name|dst_work
argument_list|,
name|sCopy
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|Path
name|dTmp
init|=
literal|null
operator|==
name|userUgi
condition|?
name|files
operator|.
name|makeQualified
argument_list|(
name|copy
argument_list|(
name|sCopy
argument_list|,
name|dst_work
argument_list|)
argument_list|)
else|:
name|userUgi
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|Path
argument_list|>
argument_list|()
block|{
specifier|public
name|Path
name|run
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|files
operator|.
name|makeQualified
argument_list|(
name|copy
argument_list|(
name|sCopy
argument_list|,
name|dst_work
argument_list|)
argument_list|)
return|;
block|}
empty_stmt|;
block|}
argument_list|)
decl_stmt|;
name|unpack
argument_list|(
operator|new
name|File
argument_list|(
name|dTmp
operator|.
name|toUri
argument_list|()
argument_list|)
argument_list|,
operator|new
name|File
argument_list|(
name|dFinal
operator|.
name|toUri
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|changePermissions
argument_list|(
name|dFinal
operator|.
name|getFileSystem
argument_list|(
name|conf
argument_list|)
argument_list|,
name|dFinal
argument_list|)
expr_stmt|;
name|files
operator|.
name|rename
argument_list|(
name|dst_work
argument_list|,
name|destDirPath
argument_list|,
name|Rename
operator|.
name|OVERWRITE
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
try|try
block|{
name|files
operator|.
name|delete
argument_list|(
name|destDirPath
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ignore
parameter_list|)
block|{       }
throw|throw
name|e
throw|;
block|}
finally|finally
block|{
try|try
block|{
name|files
operator|.
name|delete
argument_list|(
name|dst_work
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|ignore
parameter_list|)
block|{       }
name|conf
operator|=
literal|null
expr_stmt|;
name|resource
operator|=
literal|null
expr_stmt|;
block|}
return|return
name|files
operator|.
name|makeQualified
argument_list|(
operator|new
name|Path
argument_list|(
name|destDirPath
argument_list|,
name|sCopy
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Recursively change permissions of all files/dirs on path based     * on resource visibility.    * Change to 755 or 700 for dirs, 555 or 500 for files.    * @param fs FileSystem    * @param path Path to modify perms for    * @throws IOException    * @throws InterruptedException     */
DECL|method|changePermissions (FileSystem fs, final Path path)
specifier|private
name|void
name|changePermissions
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
specifier|final
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|FileStatus
name|fStatus
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|FsPermission
name|perm
init|=
name|cachePerms
decl_stmt|;
comment|// set public perms as 755 or 555 based on dir or file
if|if
condition|(
name|resource
operator|.
name|getVisibility
argument_list|()
operator|==
name|LocalResourceVisibility
operator|.
name|PUBLIC
condition|)
block|{
name|perm
operator|=
name|fStatus
operator|.
name|isDirectory
argument_list|()
condition|?
name|PUBLIC_DIR_PERMS
else|:
name|PUBLIC_FILE_PERMS
expr_stmt|;
block|}
comment|// set private perms as 700 or 500
else|else
block|{
comment|// PRIVATE:
comment|// APPLICATION:
name|perm
operator|=
name|fStatus
operator|.
name|isDirectory
argument_list|()
condition|?
name|PRIVATE_DIR_PERMS
else|:
name|PRIVATE_FILE_PERMS
expr_stmt|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"Changing permissions for path "
operator|+
name|path
operator|+
literal|" to perm "
operator|+
name|perm
argument_list|)
expr_stmt|;
specifier|final
name|FsPermission
name|fPerm
init|=
name|perm
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|userUgi
condition|)
block|{
name|files
operator|.
name|setPermission
argument_list|(
name|path
argument_list|,
name|perm
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|userUgi
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
specifier|public
name|Void
name|run
parameter_list|()
throws|throws
name|Exception
block|{
name|files
operator|.
name|setPermission
argument_list|(
name|path
argument_list|,
name|fPerm
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fStatus
operator|.
name|isDirectory
argument_list|()
operator|&&
operator|!
name|fStatus
operator|.
name|isSymlink
argument_list|()
condition|)
block|{
name|FileStatus
index|[]
name|statuses
init|=
name|fs
operator|.
name|listStatus
argument_list|(
name|path
argument_list|)
decl_stmt|;
for|for
control|(
name|FileStatus
name|status
range|:
name|statuses
control|)
block|{
name|changePermissions
argument_list|(
name|fs
argument_list|,
name|status
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

