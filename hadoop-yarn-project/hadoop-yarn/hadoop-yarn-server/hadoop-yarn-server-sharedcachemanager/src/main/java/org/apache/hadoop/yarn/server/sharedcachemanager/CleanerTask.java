begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.sharedcachemanager
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|sharedcachemanager
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
name|concurrent
operator|.
name|locks
operator|.
name|Lock
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
name|Private
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
operator|.
name|Evolving
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
name|yarn
operator|.
name|conf
operator|.
name|YarnConfiguration
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
name|exceptions
operator|.
name|YarnException
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
name|server
operator|.
name|sharedcache
operator|.
name|SharedCacheUtil
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
name|server
operator|.
name|sharedcachemanager
operator|.
name|metrics
operator|.
name|CleanerMetrics
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
name|server
operator|.
name|sharedcachemanager
operator|.
name|store
operator|.
name|SCMStore
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
comment|/**  * The task that runs and cleans up the shared cache area for stale entries and  * orphaned files. It is expected that only one cleaner task runs at any given  * point in time.  */
end_comment

begin_class
annotation|@
name|Private
annotation|@
name|Evolving
DECL|class|CleanerTask
class|class
name|CleanerTask
implements|implements
name|Runnable
block|{
DECL|field|RENAMED_SUFFIX
specifier|private
specifier|static
specifier|final
name|String
name|RENAMED_SUFFIX
init|=
literal|"-renamed"
decl_stmt|;
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|CleanerTask
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|location
specifier|private
specifier|final
name|String
name|location
decl_stmt|;
DECL|field|sleepTime
specifier|private
specifier|final
name|long
name|sleepTime
decl_stmt|;
DECL|field|nestedLevel
specifier|private
specifier|final
name|int
name|nestedLevel
decl_stmt|;
DECL|field|root
specifier|private
specifier|final
name|Path
name|root
decl_stmt|;
DECL|field|fs
specifier|private
specifier|final
name|FileSystem
name|fs
decl_stmt|;
DECL|field|store
specifier|private
specifier|final
name|SCMStore
name|store
decl_stmt|;
DECL|field|metrics
specifier|private
specifier|final
name|CleanerMetrics
name|metrics
decl_stmt|;
DECL|field|cleanerTaskLock
specifier|private
specifier|final
name|Lock
name|cleanerTaskLock
decl_stmt|;
comment|/**    * Creates a cleaner task based on the configuration. This is provided for    * convenience.    *    * @param conf    * @param store    * @param metrics    * @param cleanerTaskLock lock that ensures a serial execution of cleaner    *                        task    * @return an instance of a CleanerTask    */
DECL|method|create (Configuration conf, SCMStore store, CleanerMetrics metrics, Lock cleanerTaskLock)
specifier|public
specifier|static
name|CleanerTask
name|create
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|SCMStore
name|store
parameter_list|,
name|CleanerMetrics
name|metrics
parameter_list|,
name|Lock
name|cleanerTaskLock
parameter_list|)
block|{
try|try
block|{
comment|// get the root directory for the shared cache
name|String
name|location
init|=
name|conf
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|SHARED_CACHE_ROOT
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_SHARED_CACHE_ROOT
argument_list|)
decl_stmt|;
name|long
name|sleepTime
init|=
name|conf
operator|.
name|getLong
argument_list|(
name|YarnConfiguration
operator|.
name|SCM_CLEANER_RESOURCE_SLEEP_MS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_SCM_CLEANER_RESOURCE_SLEEP_MS
argument_list|)
decl_stmt|;
name|int
name|nestedLevel
init|=
name|SharedCacheUtil
operator|.
name|getCacheDepth
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|FileSystem
name|fs
init|=
name|FileSystem
operator|.
name|get
argument_list|(
name|conf
argument_list|)
decl_stmt|;
return|return
operator|new
name|CleanerTask
argument_list|(
name|location
argument_list|,
name|sleepTime
argument_list|,
name|nestedLevel
argument_list|,
name|fs
argument_list|,
name|store
argument_list|,
name|metrics
argument_list|,
name|cleanerTaskLock
argument_list|)
return|;
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
literal|"Unable to obtain the filesystem for the cleaner service"
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|ExceptionInInitializerError
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**    * Creates a cleaner task based on the root directory location and the    * filesystem.    */
DECL|method|CleanerTask (String location, long sleepTime, int nestedLevel, FileSystem fs, SCMStore store, CleanerMetrics metrics, Lock cleanerTaskLock)
name|CleanerTask
parameter_list|(
name|String
name|location
parameter_list|,
name|long
name|sleepTime
parameter_list|,
name|int
name|nestedLevel
parameter_list|,
name|FileSystem
name|fs
parameter_list|,
name|SCMStore
name|store
parameter_list|,
name|CleanerMetrics
name|metrics
parameter_list|,
name|Lock
name|cleanerTaskLock
parameter_list|)
block|{
name|this
operator|.
name|location
operator|=
name|location
expr_stmt|;
name|this
operator|.
name|sleepTime
operator|=
name|sleepTime
expr_stmt|;
name|this
operator|.
name|nestedLevel
operator|=
name|nestedLevel
expr_stmt|;
name|this
operator|.
name|root
operator|=
operator|new
name|Path
argument_list|(
name|location
argument_list|)
expr_stmt|;
name|this
operator|.
name|fs
operator|=
name|fs
expr_stmt|;
name|this
operator|.
name|store
operator|=
name|store
expr_stmt|;
name|this
operator|.
name|metrics
operator|=
name|metrics
expr_stmt|;
name|this
operator|.
name|cleanerTaskLock
operator|=
name|cleanerTaskLock
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
if|if
condition|(
operator|!
name|this
operator|.
name|cleanerTaskLock
operator|.
name|tryLock
argument_list|()
condition|)
block|{
comment|// there is already another task running
name|LOG
operator|.
name|warn
argument_list|(
literal|"A cleaner task is already running. "
operator|+
literal|"This scheduled cleaner task will do nothing."
argument_list|)
expr_stmt|;
return|return;
block|}
try|try
block|{
if|if
condition|(
operator|!
name|fs
operator|.
name|exists
argument_list|(
name|root
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"The shared cache root "
operator|+
name|location
operator|+
literal|" was not found. "
operator|+
literal|"The cleaner task will do nothing."
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// we're now ready to process the shared cache area
name|process
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unexpected exception while initializing the cleaner task. "
operator|+
literal|"This task will do nothing,"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
comment|// this is set to false regardless of if it is a scheduled or on-demand
comment|// task
name|this
operator|.
name|cleanerTaskLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Sweeps and processes the shared cache area to clean up stale and orphaned    * files.    */
DECL|method|process ()
name|void
name|process
parameter_list|()
block|{
comment|// mark the beginning of the run in the metrics
name|metrics
operator|.
name|reportCleaningStart
argument_list|()
expr_stmt|;
try|try
block|{
comment|// now traverse individual directories and process them
comment|// the directory structure is specified by the nested level parameter
comment|// (e.g. 9/c/d/<checksum>)
name|String
name|pattern
init|=
name|SharedCacheUtil
operator|.
name|getCacheEntryGlobPattern
argument_list|(
name|nestedLevel
argument_list|)
decl_stmt|;
name|FileStatus
index|[]
name|resources
init|=
name|fs
operator|.
name|globStatus
argument_list|(
operator|new
name|Path
argument_list|(
name|root
argument_list|,
name|pattern
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|numResources
init|=
name|resources
operator|==
literal|null
condition|?
literal|0
else|:
name|resources
operator|.
name|length
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Processing "
operator|+
name|numResources
operator|+
literal|" resources in the shared cache"
argument_list|)
expr_stmt|;
name|long
name|beginMs
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
if|if
condition|(
name|resources
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|FileStatus
name|resource
range|:
name|resources
control|)
block|{
comment|// check for interruption so it can abort in a timely manner in case
comment|// of shutdown
if|if
condition|(
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|isInterrupted
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"The cleaner task was interrupted. Aborting."
argument_list|)
expr_stmt|;
break|break;
block|}
if|if
condition|(
name|resource
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|processSingleResource
argument_list|(
name|resource
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Invalid file at path "
operator|+
name|resource
operator|.
name|getPath
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|" when a directory was expected"
argument_list|)
expr_stmt|;
block|}
comment|// add sleep time between cleaning each directory if it is non-zero
if|if
condition|(
name|sleepTime
operator|>
literal|0
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|sleepTime
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|long
name|endMs
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|long
name|durationMs
init|=
name|endMs
operator|-
name|beginMs
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Processed "
operator|+
name|numResources
operator|+
literal|" resource(s) in "
operator|+
name|durationMs
operator|+
literal|" ms."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e1
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unable to complete the cleaner task"
argument_list|,
name|e1
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e2
parameter_list|)
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
comment|// restore the interrupt
block|}
block|}
comment|/**    * Returns a path for the root directory for the shared cache.    */
DECL|method|getRootPath ()
name|Path
name|getRootPath
parameter_list|()
block|{
return|return
name|root
return|;
block|}
comment|/**    * Processes a single shared cache resource directory.    */
DECL|method|processSingleResource (FileStatus resource)
name|void
name|processSingleResource
parameter_list|(
name|FileStatus
name|resource
parameter_list|)
block|{
name|Path
name|path
init|=
name|resource
operator|.
name|getPath
argument_list|()
decl_stmt|;
comment|// indicates the processing status of the resource
name|ResourceStatus
name|resourceStatus
init|=
name|ResourceStatus
operator|.
name|INIT
decl_stmt|;
comment|// first, if the path ends with the renamed suffix, it indicates the
comment|// directory was moved (as stale) but somehow not deleted (probably due to
comment|// SCM failure); delete the directory
if|if
condition|(
name|path
operator|.
name|toString
argument_list|()
operator|.
name|endsWith
argument_list|(
name|RENAMED_SUFFIX
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Found a renamed directory that was left undeleted at "
operator|+
name|path
operator|.
name|toString
argument_list|()
operator|+
literal|". Deleting."
argument_list|)
expr_stmt|;
try|try
block|{
if|if
condition|(
name|fs
operator|.
name|delete
argument_list|(
name|path
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|resourceStatus
operator|=
name|ResourceStatus
operator|.
name|DELETED
expr_stmt|;
block|}
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
literal|"Error while processing a shared cache resource: "
operator|+
name|path
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// this is the path to the cache resource directory
comment|// the directory name is the resource key (i.e. a unique identifier)
name|String
name|key
init|=
name|path
operator|.
name|getName
argument_list|()
decl_stmt|;
try|try
block|{
name|store
operator|.
name|cleanResourceReferences
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Exception thrown while removing dead appIds."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|store
operator|.
name|isResourceEvictable
argument_list|(
name|key
argument_list|,
name|resource
argument_list|)
condition|)
block|{
try|try
block|{
comment|/*            * TODO See YARN-2663: There is a race condition between            * store.removeResource(key) and            * removeResourceFromCacheFileSystem(path) operations because they do            * not happen atomically and resources can be uploaded with different            * file names by the node managers.            */
comment|// remove the resource from scm (checks for appIds as well)
if|if
condition|(
name|store
operator|.
name|removeResource
argument_list|(
name|key
argument_list|)
condition|)
block|{
comment|// remove the resource from the file system
name|boolean
name|deleted
init|=
name|removeResourceFromCacheFileSystem
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|deleted
condition|)
block|{
name|resourceStatus
operator|=
name|ResourceStatus
operator|.
name|DELETED
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to remove path from the file system."
operator|+
literal|" Skipping this resource: "
operator|+
name|path
argument_list|)
expr_stmt|;
name|resourceStatus
operator|=
name|ResourceStatus
operator|.
name|ERROR
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// we did not delete the resource because it contained application
comment|// ids
name|resourceStatus
operator|=
name|ResourceStatus
operator|.
name|PROCESSED
expr_stmt|;
block|}
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
literal|"Failed to remove path from the file system. Skipping this resource: "
operator|+
name|path
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|resourceStatus
operator|=
name|ResourceStatus
operator|.
name|ERROR
expr_stmt|;
block|}
block|}
else|else
block|{
name|resourceStatus
operator|=
name|ResourceStatus
operator|.
name|PROCESSED
expr_stmt|;
block|}
block|}
comment|// record the processing
switch|switch
condition|(
name|resourceStatus
condition|)
block|{
case|case
name|DELETED
case|:
name|metrics
operator|.
name|reportAFileDelete
argument_list|()
expr_stmt|;
break|break;
case|case
name|PROCESSED
case|:
name|metrics
operator|.
name|reportAFileProcess
argument_list|()
expr_stmt|;
break|break;
case|case
name|ERROR
case|:
name|metrics
operator|.
name|reportAFileError
argument_list|()
expr_stmt|;
break|break;
default|default:
name|LOG
operator|.
name|error
argument_list|(
literal|"Cleaner encountered an invalid status ("
operator|+
name|resourceStatus
operator|+
literal|") while processing resource: "
operator|+
name|path
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|removeResourceFromCacheFileSystem (Path path)
specifier|private
name|boolean
name|removeResourceFromCacheFileSystem
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
comment|// rename the directory to make the delete atomic
name|Path
name|renamedPath
init|=
operator|new
name|Path
argument_list|(
name|path
operator|.
name|toString
argument_list|()
operator|+
name|RENAMED_SUFFIX
argument_list|)
decl_stmt|;
if|if
condition|(
name|fs
operator|.
name|rename
argument_list|(
name|path
argument_list|,
name|renamedPath
argument_list|)
condition|)
block|{
comment|// the directory can be removed safely now
comment|// log the original path
name|LOG
operator|.
name|info
argument_list|(
literal|"Deleting "
operator|+
name|path
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|fs
operator|.
name|delete
argument_list|(
name|renamedPath
argument_list|,
literal|true
argument_list|)
return|;
block|}
else|else
block|{
comment|// we were unable to remove it for some reason: it's best to leave
comment|// it at that
name|LOG
operator|.
name|error
argument_list|(
literal|"We were not able to rename the directory to "
operator|+
name|renamedPath
operator|.
name|toString
argument_list|()
operator|+
literal|". We will leave it intact."
argument_list|)
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
comment|/**    * A status indicating what happened with the processing of a given cache    * resource.    */
DECL|enum|ResourceStatus
specifier|private
enum|enum
name|ResourceStatus
block|{
DECL|enumConstant|INIT
name|INIT
block|,
comment|/** Resource was successfully processed, but not deleted **/
DECL|enumConstant|PROCESSED
name|PROCESSED
block|,
comment|/** Resource was successfully deleted **/
DECL|enumConstant|DELETED
name|DELETED
block|,
comment|/** The cleaner task ran into an error while processing the resource **/
DECL|enumConstant|ERROR
name|ERROR
block|}
block|}
end_class

end_unit

