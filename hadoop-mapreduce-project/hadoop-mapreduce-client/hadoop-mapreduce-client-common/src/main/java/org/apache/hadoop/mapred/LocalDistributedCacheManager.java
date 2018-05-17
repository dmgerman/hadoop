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
name|File
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
name|MalformedURLException
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
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URLClassLoader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|AccessController
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|PrivilegedAction
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
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
name|ExecutorService
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
name|concurrent
operator|.
name|ThreadFactory
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
name|mapreduce
operator|.
name|MRConfig
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
name|filecache
operator|.
name|DistributedCache
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
name|v2
operator|.
name|util
operator|.
name|MRApps
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
name|concurrent
operator|.
name|HadoopExecutors
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
name|LocalResourceType
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
name|util
operator|.
name|ConverterUtils
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
name|util
operator|.
name|FSDownload
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
name|Maps
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
name|ThreadFactoryBuilder
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

begin_comment
comment|/**  * A helper class for managing the distributed cache for {@link LocalJobRunner}.  */
end_comment

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
DECL|class|LocalDistributedCacheManager
class|class
name|LocalDistributedCacheManager
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
name|LocalDistributedCacheManager
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|localArchives
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|localArchives
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|localFiles
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|localFiles
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|localClasspaths
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|localClasspaths
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|symlinksCreated
specifier|private
name|List
argument_list|<
name|File
argument_list|>
name|symlinksCreated
init|=
operator|new
name|ArrayList
argument_list|<
name|File
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|classLoaderCreated
specifier|private
name|URLClassLoader
name|classLoaderCreated
init|=
literal|null
decl_stmt|;
DECL|field|setupCalled
specifier|private
name|boolean
name|setupCalled
init|=
literal|false
decl_stmt|;
comment|/**    * Set up the distributed cache by localizing the resources, and updating    * the configuration with references to the localized resources.    * @param conf    * @throws IOException    */
DECL|method|setup (JobConf conf, JobID jobId)
specifier|public
specifier|synchronized
name|void
name|setup
parameter_list|(
name|JobConf
name|conf
parameter_list|,
name|JobID
name|jobId
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|workDir
init|=
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"user.dir"
argument_list|)
argument_list|)
decl_stmt|;
comment|// Generate YARN local resources objects corresponding to the distributed
comment|// cache configuration
name|Map
argument_list|<
name|String
argument_list|,
name|LocalResource
argument_list|>
name|localResources
init|=
operator|new
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|LocalResource
argument_list|>
argument_list|()
decl_stmt|;
name|MRApps
operator|.
name|setupDistributedCache
argument_list|(
name|conf
argument_list|,
name|localResources
argument_list|)
expr_stmt|;
comment|// Generating unique numbers for FSDownload.
comment|// Find which resources are to be put on the local classpath
name|Map
argument_list|<
name|String
argument_list|,
name|Path
argument_list|>
name|classpaths
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Path
argument_list|>
argument_list|()
decl_stmt|;
name|Path
index|[]
name|archiveClassPaths
init|=
name|DistributedCache
operator|.
name|getArchiveClassPaths
argument_list|(
name|conf
argument_list|)
decl_stmt|;
if|if
condition|(
name|archiveClassPaths
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Path
name|p
range|:
name|archiveClassPaths
control|)
block|{
name|classpaths
operator|.
name|put
argument_list|(
name|p
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|p
argument_list|)
expr_stmt|;
block|}
block|}
name|Path
index|[]
name|fileClassPaths
init|=
name|DistributedCache
operator|.
name|getFileClassPaths
argument_list|(
name|conf
argument_list|)
decl_stmt|;
if|if
condition|(
name|fileClassPaths
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Path
name|p
range|:
name|fileClassPaths
control|)
block|{
name|classpaths
operator|.
name|put
argument_list|(
name|p
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|p
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Localize the resources
name|LocalDirAllocator
name|localDirAllocator
init|=
operator|new
name|LocalDirAllocator
argument_list|(
name|MRConfig
operator|.
name|LOCAL_DIR
argument_list|)
decl_stmt|;
name|FileContext
name|localFSFileContext
init|=
name|FileContext
operator|.
name|getLocalFSFileContext
argument_list|()
decl_stmt|;
name|UserGroupInformation
name|ugi
init|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
decl_stmt|;
name|ExecutorService
name|exec
init|=
literal|null
decl_stmt|;
try|try
block|{
name|ThreadFactory
name|tf
init|=
operator|new
name|ThreadFactoryBuilder
argument_list|()
operator|.
name|setNameFormat
argument_list|(
literal|"LocalDistributedCacheManager Downloader #%d"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|exec
operator|=
name|HadoopExecutors
operator|.
name|newCachedThreadPool
argument_list|(
name|tf
argument_list|)
expr_stmt|;
name|Path
name|destPath
init|=
name|localDirAllocator
operator|.
name|getLocalPathForWrite
argument_list|(
literal|"."
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|LocalResource
argument_list|,
name|Future
argument_list|<
name|Path
argument_list|>
argument_list|>
name|resourcesToPaths
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|LocalResource
name|resource
range|:
name|localResources
operator|.
name|values
argument_list|()
control|)
block|{
name|Path
name|destPathForDownload
init|=
operator|new
name|Path
argument_list|(
name|destPath
argument_list|,
name|jobId
operator|.
name|toString
argument_list|()
operator|+
literal|"_"
operator|+
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|Callable
argument_list|<
name|Path
argument_list|>
name|download
init|=
operator|new
name|FSDownload
argument_list|(
name|localFSFileContext
argument_list|,
name|ugi
argument_list|,
name|conf
argument_list|,
name|destPathForDownload
argument_list|,
name|resource
argument_list|)
decl_stmt|;
name|Future
argument_list|<
name|Path
argument_list|>
name|future
init|=
name|exec
operator|.
name|submit
argument_list|(
name|download
argument_list|)
decl_stmt|;
name|resourcesToPaths
operator|.
name|put
argument_list|(
name|resource
argument_list|,
name|future
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|LocalResource
argument_list|>
name|entry
range|:
name|localResources
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|LocalResource
name|resource
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|Path
name|path
decl_stmt|;
try|try
block|{
name|path
operator|=
name|resourcesToPaths
operator|.
name|get
argument_list|(
name|resource
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|String
name|pathString
init|=
name|path
operator|.
name|toUri
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|String
name|link
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|String
name|target
init|=
operator|new
name|File
argument_list|(
name|path
operator|.
name|toUri
argument_list|()
argument_list|)
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|symlink
argument_list|(
name|workDir
argument_list|,
name|target
argument_list|,
name|link
argument_list|)
expr_stmt|;
if|if
condition|(
name|resource
operator|.
name|getType
argument_list|()
operator|==
name|LocalResourceType
operator|.
name|ARCHIVE
condition|)
block|{
name|localArchives
operator|.
name|add
argument_list|(
name|pathString
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|resource
operator|.
name|getType
argument_list|()
operator|==
name|LocalResourceType
operator|.
name|FILE
condition|)
block|{
name|localFiles
operator|.
name|add
argument_list|(
name|pathString
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|resource
operator|.
name|getType
argument_list|()
operator|==
name|LocalResourceType
operator|.
name|PATTERN
condition|)
block|{
comment|//PATTERN is not currently used in local mode
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Resource type PATTERN is not "
operator|+
literal|"implemented yet. "
operator|+
name|resource
operator|.
name|getResource
argument_list|()
argument_list|)
throw|;
block|}
name|Path
name|resourcePath
decl_stmt|;
try|try
block|{
name|resourcePath
operator|=
name|resource
operator|.
name|getResource
argument_list|()
operator|.
name|toPath
argument_list|()
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
name|e
argument_list|)
throw|;
block|}
name|LOG
operator|.
name|info
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Localized %s as %s"
argument_list|,
name|resourcePath
argument_list|,
name|path
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|cp
init|=
name|resourcePath
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
decl_stmt|;
if|if
condition|(
name|classpaths
operator|.
name|keySet
argument_list|()
operator|.
name|contains
argument_list|(
name|cp
argument_list|)
condition|)
block|{
name|localClasspaths
operator|.
name|add
argument_list|(
name|path
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|exec
operator|!=
literal|null
condition|)
block|{
name|exec
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
comment|// Update the configuration object with localized data.
if|if
condition|(
operator|!
name|localArchives
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|conf
operator|.
name|set
argument_list|(
name|MRJobConfig
operator|.
name|CACHE_LOCALARCHIVES
argument_list|,
name|StringUtils
operator|.
name|arrayToString
argument_list|(
name|localArchives
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|localArchives
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|localFiles
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|conf
operator|.
name|set
argument_list|(
name|MRJobConfig
operator|.
name|CACHE_LOCALFILES
argument_list|,
name|StringUtils
operator|.
name|arrayToString
argument_list|(
name|localFiles
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|localArchives
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|setupCalled
operator|=
literal|true
expr_stmt|;
block|}
comment|/**    * Utility method for creating a symlink and warning on errors.    *    * If link is null, does nothing.    */
DECL|method|symlink (File workDir, String target, String link)
specifier|private
name|void
name|symlink
parameter_list|(
name|File
name|workDir
parameter_list|,
name|String
name|target
parameter_list|,
name|String
name|link
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|link
operator|!=
literal|null
condition|)
block|{
name|link
operator|=
name|workDir
operator|.
name|toString
argument_list|()
operator|+
name|Path
operator|.
name|SEPARATOR
operator|+
name|link
expr_stmt|;
name|File
name|flink
init|=
operator|new
name|File
argument_list|(
name|link
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|flink
operator|.
name|exists
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Creating symlink: %s<- %s"
argument_list|,
name|target
argument_list|,
name|link
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
literal|0
operator|!=
name|FileUtil
operator|.
name|symLink
argument_list|(
name|target
argument_list|,
name|link
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Failed to create symlink: %s<- %s"
argument_list|,
name|target
argument_list|,
name|link
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|symlinksCreated
operator|.
name|add
argument_list|(
operator|new
name|File
argument_list|(
name|link
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/**     * Are the resources that should be added to the classpath?     * Should be called after setup().    *     */
DECL|method|hasLocalClasspaths ()
specifier|public
specifier|synchronized
name|boolean
name|hasLocalClasspaths
parameter_list|()
block|{
if|if
condition|(
operator|!
name|setupCalled
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"hasLocalClasspaths() should be called after setup()"
argument_list|)
throw|;
block|}
return|return
operator|!
name|localClasspaths
operator|.
name|isEmpty
argument_list|()
return|;
block|}
comment|/**    * Creates a class loader that includes the designated    * files and archives.    */
DECL|method|makeClassLoader (final ClassLoader parent)
specifier|public
specifier|synchronized
name|ClassLoader
name|makeClassLoader
parameter_list|(
specifier|final
name|ClassLoader
name|parent
parameter_list|)
throws|throws
name|MalformedURLException
block|{
if|if
condition|(
name|classLoaderCreated
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"A classloader was already created"
argument_list|)
throw|;
block|}
specifier|final
name|URL
index|[]
name|urls
init|=
operator|new
name|URL
index|[
name|localClasspaths
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|localClasspaths
operator|.
name|size
argument_list|()
condition|;
operator|++
name|i
control|)
block|{
name|urls
index|[
name|i
index|]
operator|=
operator|new
name|File
argument_list|(
name|localClasspaths
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
operator|.
name|toURI
argument_list|()
operator|.
name|toURL
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|urls
index|[
name|i
index|]
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|AccessController
operator|.
name|doPrivileged
argument_list|(
operator|new
name|PrivilegedAction
argument_list|<
name|ClassLoader
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|ClassLoader
name|run
parameter_list|()
block|{
name|classLoaderCreated
operator|=
operator|new
name|URLClassLoader
argument_list|(
name|urls
argument_list|,
name|parent
argument_list|)
expr_stmt|;
return|return
name|classLoaderCreated
return|;
block|}
block|}
argument_list|)
return|;
block|}
DECL|method|close ()
specifier|public
specifier|synchronized
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|classLoaderCreated
operator|!=
literal|null
condition|)
block|{
name|AccessController
operator|.
name|doPrivileged
argument_list|(
operator|new
name|PrivilegedAction
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Void
name|run
parameter_list|()
block|{
try|try
block|{
name|classLoaderCreated
operator|.
name|close
argument_list|()
expr_stmt|;
name|classLoaderCreated
operator|=
literal|null
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
name|warn
argument_list|(
literal|"Failed to close classloader created "
operator|+
literal|"by LocalDistributedCacheManager"
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|File
name|symlink
range|:
name|symlinksCreated
control|)
block|{
if|if
condition|(
operator|!
name|symlink
operator|.
name|delete
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to delete symlink created by the local job runner: "
operator|+
name|symlink
argument_list|)
expr_stmt|;
block|}
block|}
name|FileContext
name|localFSFileContext
init|=
name|FileContext
operator|.
name|getLocalFSFileContext
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|archive
range|:
name|localArchives
control|)
block|{
name|localFSFileContext
operator|.
name|delete
argument_list|(
operator|new
name|Path
argument_list|(
name|archive
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|file
range|:
name|localFiles
control|)
block|{
name|localFSFileContext
operator|.
name|delete
argument_list|(
operator|new
name|Path
argument_list|(
name|file
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

