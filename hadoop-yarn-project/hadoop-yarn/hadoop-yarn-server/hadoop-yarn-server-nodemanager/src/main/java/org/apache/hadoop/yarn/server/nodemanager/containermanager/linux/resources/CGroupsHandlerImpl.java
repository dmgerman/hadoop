begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * *  *  Licensed to the Apache Software Foundation (ASF) under one  *  or more contributor license agreements. See the NOTICE file  *  distributed with this work for additional information  *  regarding copyright ownership. The ASF licenses this file  *  to you under the Apache License, Version 2.0 (the  *  "License"); you may not use this file except in compliance  *  with the License. You may obtain a copy of the License at  *  *  http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  * /  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.resources
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
name|nodemanager
operator|.
name|containermanager
operator|.
name|linux
operator|.
name|resources
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
name|annotations
operator|.
name|VisibleForTesting
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
name|io
operator|.
name|IOUtils
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
name|server
operator|.
name|nodemanager
operator|.
name|containermanager
operator|.
name|linux
operator|.
name|privileged
operator|.
name|PrivilegedOperation
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
name|nodemanager
operator|.
name|containermanager
operator|.
name|linux
operator|.
name|privileged
operator|.
name|PrivilegedOperationException
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
name|nodemanager
operator|.
name|containermanager
operator|.
name|linux
operator|.
name|privileged
operator|.
name|PrivilegedOperationExecutor
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
name|Clock
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
name|SystemClock
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Paths
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|concurrent
operator|.
name|locks
operator|.
name|ReadWriteLock
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
name|ReentrantReadWriteLock
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
name|Matcher
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

begin_comment
comment|/**  * Support for interacting with various CGroup subsystems. Thread-safe.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|CGroupsHandlerImpl
class|class
name|CGroupsHandlerImpl
implements|implements
name|CGroupsHandler
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
name|CGroupsHandlerImpl
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|MTAB_FILE
specifier|private
specifier|static
specifier|final
name|String
name|MTAB_FILE
init|=
literal|"/proc/mounts"
decl_stmt|;
DECL|field|CGROUPS_FSTYPE
specifier|private
specifier|static
specifier|final
name|String
name|CGROUPS_FSTYPE
init|=
literal|"cgroup"
decl_stmt|;
DECL|field|cGroupPrefix
specifier|private
specifier|final
name|String
name|cGroupPrefix
decl_stmt|;
DECL|field|enableCGroupMount
specifier|private
specifier|final
name|boolean
name|enableCGroupMount
decl_stmt|;
DECL|field|cGroupMountPath
specifier|private
specifier|final
name|String
name|cGroupMountPath
decl_stmt|;
DECL|field|deleteCGroupTimeout
specifier|private
specifier|final
name|long
name|deleteCGroupTimeout
decl_stmt|;
DECL|field|deleteCGroupDelay
specifier|private
specifier|final
name|long
name|deleteCGroupDelay
decl_stmt|;
DECL|field|controllerPaths
specifier|private
name|Map
argument_list|<
name|CGroupController
argument_list|,
name|String
argument_list|>
name|controllerPaths
decl_stmt|;
DECL|field|rwLock
specifier|private
specifier|final
name|ReadWriteLock
name|rwLock
decl_stmt|;
DECL|field|privilegedOperationExecutor
specifier|private
specifier|final
name|PrivilegedOperationExecutor
name|privilegedOperationExecutor
decl_stmt|;
DECL|field|clock
specifier|private
specifier|final
name|Clock
name|clock
decl_stmt|;
DECL|method|CGroupsHandlerImpl (Configuration conf, PrivilegedOperationExecutor privilegedOperationExecutor)
specifier|public
name|CGroupsHandlerImpl
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|PrivilegedOperationExecutor
name|privilegedOperationExecutor
parameter_list|)
throws|throws
name|ResourceHandlerException
block|{
name|this
operator|.
name|cGroupPrefix
operator|=
name|conf
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|NM_LINUX_CONTAINER_CGROUPS_HIERARCHY
argument_list|,
literal|"/hadoop-yarn"
argument_list|)
operator|.
name|replaceAll
argument_list|(
literal|"^/"
argument_list|,
literal|""
argument_list|)
operator|.
name|replaceAll
argument_list|(
literal|"$/"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|this
operator|.
name|enableCGroupMount
operator|=
name|conf
operator|.
name|getBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|NM_LINUX_CONTAINER_CGROUPS_MOUNT
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|this
operator|.
name|cGroupMountPath
operator|=
name|conf
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|NM_LINUX_CONTAINER_CGROUPS_MOUNT_PATH
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|deleteCGroupTimeout
operator|=
name|conf
operator|.
name|getLong
argument_list|(
name|YarnConfiguration
operator|.
name|NM_LINUX_CONTAINER_CGROUPS_DELETE_TIMEOUT
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_NM_LINUX_CONTAINER_CGROUPS_DELETE_TIMEOUT
argument_list|)
expr_stmt|;
name|this
operator|.
name|deleteCGroupDelay
operator|=
name|conf
operator|.
name|getLong
argument_list|(
name|YarnConfiguration
operator|.
name|NM_LINUX_CONTAINER_CGROUPS_DELETE_DELAY
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_NM_LINUX_CONTAINER_CGROUPS_DELETE_DELAY
argument_list|)
expr_stmt|;
name|this
operator|.
name|controllerPaths
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|this
operator|.
name|rwLock
operator|=
operator|new
name|ReentrantReadWriteLock
argument_list|()
expr_stmt|;
name|this
operator|.
name|privilegedOperationExecutor
operator|=
name|privilegedOperationExecutor
expr_stmt|;
name|this
operator|.
name|clock
operator|=
operator|new
name|SystemClock
argument_list|()
expr_stmt|;
name|init
argument_list|()
expr_stmt|;
block|}
DECL|method|init ()
specifier|private
name|void
name|init
parameter_list|()
throws|throws
name|ResourceHandlerException
block|{
name|initializeControllerPaths
argument_list|()
expr_stmt|;
block|}
DECL|method|getControllerPath (CGroupController controller)
specifier|private
name|String
name|getControllerPath
parameter_list|(
name|CGroupController
name|controller
parameter_list|)
block|{
try|try
block|{
name|rwLock
operator|.
name|readLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
return|return
name|controllerPaths
operator|.
name|get
argument_list|(
name|controller
argument_list|)
return|;
block|}
finally|finally
block|{
name|rwLock
operator|.
name|readLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|initializeControllerPaths ()
specifier|private
name|void
name|initializeControllerPaths
parameter_list|()
throws|throws
name|ResourceHandlerException
block|{
if|if
condition|(
name|enableCGroupMount
condition|)
block|{
comment|// nothing to do here - we support 'deferred' mounting of specific
comment|// controllers - we'll populate the path for a given controller when an
comment|// explicit mountCGroupController request is issued.
name|LOG
operator|.
name|info
argument_list|(
literal|"CGroup controller mounting enabled."
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// cluster admins are expected to have mounted controllers in specific
comment|// locations - we'll attempt to figure out mount points
name|Map
argument_list|<
name|CGroupController
argument_list|,
name|String
argument_list|>
name|cPaths
init|=
name|initializeControllerPathsFromMtab
argument_list|(
name|MTAB_FILE
argument_list|,
name|this
operator|.
name|cGroupPrefix
argument_list|)
decl_stmt|;
comment|// we want to do a bulk update without the paths changing concurrently
try|try
block|{
name|rwLock
operator|.
name|writeLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
name|controllerPaths
operator|=
name|cPaths
expr_stmt|;
block|}
finally|finally
block|{
name|rwLock
operator|.
name|writeLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|VisibleForTesting
DECL|method|initializeControllerPathsFromMtab ( String mtab, String cGroupPrefix)
specifier|static
name|Map
argument_list|<
name|CGroupController
argument_list|,
name|String
argument_list|>
name|initializeControllerPathsFromMtab
parameter_list|(
name|String
name|mtab
parameter_list|,
name|String
name|cGroupPrefix
parameter_list|)
throws|throws
name|ResourceHandlerException
block|{
try|try
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|parsedMtab
init|=
name|parseMtab
argument_list|(
name|mtab
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|CGroupController
argument_list|,
name|String
argument_list|>
name|ret
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|CGroupController
name|controller
range|:
name|CGroupController
operator|.
name|values
argument_list|()
control|)
block|{
name|String
name|name
init|=
name|controller
operator|.
name|getName
argument_list|()
decl_stmt|;
name|String
name|controllerPath
init|=
name|findControllerInMtab
argument_list|(
name|name
argument_list|,
name|parsedMtab
argument_list|)
decl_stmt|;
if|if
condition|(
name|controllerPath
operator|!=
literal|null
condition|)
block|{
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|controllerPath
operator|+
literal|"/"
operator|+
name|cGroupPrefix
argument_list|)
decl_stmt|;
if|if
condition|(
name|FileUtil
operator|.
name|canWrite
argument_list|(
name|f
argument_list|)
condition|)
block|{
name|ret
operator|.
name|put
argument_list|(
name|controller
argument_list|,
name|controllerPath
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|String
name|error
init|=
operator|new
name|StringBuffer
argument_list|(
literal|"Mount point Based on mtab file: "
argument_list|)
operator|.
name|append
argument_list|(
name|mtab
argument_list|)
operator|.
name|append
argument_list|(
literal|". Controller mount point not writable for: "
argument_list|)
operator|.
name|append
argument_list|(
name|name
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|error
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|ResourceHandlerException
argument_list|(
name|error
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Controller not mounted but automount disabled: "
operator|+
name|name
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|ret
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
name|warn
argument_list|(
literal|"Failed to initialize controller paths! Exception: "
operator|+
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|ResourceHandlerException
argument_list|(
literal|"Failed to initialize controller paths!"
argument_list|)
throw|;
block|}
block|}
comment|/* We are looking for entries of the form:    * none /cgroup/path/mem cgroup rw,memory 0 0    *    * Use a simple pattern that splits on the five spaces, and    * grabs the 2, 3, and 4th fields.    */
DECL|field|MTAB_FILE_FORMAT
specifier|private
specifier|static
specifier|final
name|Pattern
name|MTAB_FILE_FORMAT
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^[^\\s]+\\s([^\\s]+)\\s([^\\s]+)\\s([^\\s]+)\\s[^\\s]+\\s[^\\s]+$"
argument_list|)
decl_stmt|;
comment|/*    * Returns a map: path -> mount options    * for mounts with type "cgroup". Cgroup controllers will    * appear in the list of options for a path.    */
DECL|method|parseMtab (String mtab)
specifier|private
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|parseMtab
parameter_list|(
name|String
name|mtab
parameter_list|)
throws|throws
name|IOException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|ret
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|BufferedReader
name|in
init|=
literal|null
decl_stmt|;
try|try
block|{
name|FileInputStream
name|fis
init|=
operator|new
name|FileInputStream
argument_list|(
operator|new
name|File
argument_list|(
name|mtab
argument_list|)
argument_list|)
decl_stmt|;
name|in
operator|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|fis
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|str
init|=
name|in
operator|.
name|readLine
argument_list|()
init|;
name|str
operator|!=
literal|null
condition|;
name|str
operator|=
name|in
operator|.
name|readLine
argument_list|()
control|)
block|{
name|Matcher
name|m
init|=
name|MTAB_FILE_FORMAT
operator|.
name|matcher
argument_list|(
name|str
argument_list|)
decl_stmt|;
name|boolean
name|mat
init|=
name|m
operator|.
name|find
argument_list|()
decl_stmt|;
if|if
condition|(
name|mat
condition|)
block|{
name|String
name|path
init|=
name|m
operator|.
name|group
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|String
name|type
init|=
name|m
operator|.
name|group
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|String
name|options
init|=
name|m
operator|.
name|group
argument_list|(
literal|3
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|.
name|equals
argument_list|(
name|CGROUPS_FSTYPE
argument_list|)
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|value
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|options
operator|.
name|split
argument_list|(
literal|","
argument_list|)
argument_list|)
decl_stmt|;
name|ret
operator|.
name|put
argument_list|(
name|path
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Error while reading "
operator|+
name|mtab
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|cleanup
argument_list|(
name|LOG
argument_list|,
name|in
argument_list|)
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
DECL|method|findControllerInMtab (String controller, Map<String, List<String>> entries)
specifier|private
specifier|static
name|String
name|findControllerInMtab
parameter_list|(
name|String
name|controller
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|entries
parameter_list|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|e
range|:
name|entries
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|e
operator|.
name|getValue
argument_list|()
operator|.
name|contains
argument_list|(
name|controller
argument_list|)
condition|)
return|return
name|e
operator|.
name|getKey
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|mountCGroupController (CGroupController controller)
specifier|public
name|void
name|mountCGroupController
parameter_list|(
name|CGroupController
name|controller
parameter_list|)
throws|throws
name|ResourceHandlerException
block|{
if|if
condition|(
operator|!
name|enableCGroupMount
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"CGroup mounting is disabled - ignoring mount request for: "
operator|+
name|controller
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
name|String
name|path
init|=
name|getControllerPath
argument_list|(
name|controller
argument_list|)
decl_stmt|;
if|if
condition|(
name|path
operator|==
literal|null
condition|)
block|{
try|try
block|{
comment|//lock out other readers/writers till we are done
name|rwLock
operator|.
name|writeLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
name|String
name|hierarchy
init|=
name|cGroupPrefix
decl_stmt|;
name|StringBuffer
name|controllerPath
init|=
operator|new
name|StringBuffer
argument_list|()
operator|.
name|append
argument_list|(
name|cGroupMountPath
argument_list|)
operator|.
name|append
argument_list|(
literal|'/'
argument_list|)
operator|.
name|append
argument_list|(
name|controller
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|StringBuffer
name|cGroupKV
init|=
operator|new
name|StringBuffer
argument_list|()
operator|.
name|append
argument_list|(
name|controller
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|'='
argument_list|)
operator|.
name|append
argument_list|(
name|controllerPath
argument_list|)
decl_stmt|;
name|PrivilegedOperation
operator|.
name|OperationType
name|opType
init|=
name|PrivilegedOperation
operator|.
name|OperationType
operator|.
name|MOUNT_CGROUPS
decl_stmt|;
name|PrivilegedOperation
name|op
init|=
operator|new
name|PrivilegedOperation
argument_list|(
name|opType
argument_list|,
operator|(
name|String
operator|)
literal|null
argument_list|)
decl_stmt|;
name|op
operator|.
name|appendArgs
argument_list|(
name|hierarchy
argument_list|,
name|cGroupKV
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Mounting controller "
operator|+
name|controller
operator|.
name|getName
argument_list|()
operator|+
literal|" at "
operator|+
name|controllerPath
argument_list|)
expr_stmt|;
name|privilegedOperationExecutor
operator|.
name|executePrivilegedOperation
argument_list|(
name|op
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|//if privileged operation succeeds, update controller paths
name|controllerPaths
operator|.
name|put
argument_list|(
name|controller
argument_list|,
name|controllerPath
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
catch|catch
parameter_list|(
name|PrivilegedOperationException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to mount controller: "
operator|+
name|controller
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|ResourceHandlerException
argument_list|(
literal|"Failed to mount controller: "
operator|+
name|controller
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
finally|finally
block|{
name|rwLock
operator|.
name|writeLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"CGroup controller already mounted at: "
operator|+
name|path
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
annotation|@
name|Override
DECL|method|getPathForCGroup (CGroupController controller, String cGroupId)
specifier|public
name|String
name|getPathForCGroup
parameter_list|(
name|CGroupController
name|controller
parameter_list|,
name|String
name|cGroupId
parameter_list|)
block|{
return|return
operator|new
name|StringBuffer
argument_list|(
name|getControllerPath
argument_list|(
name|controller
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|'/'
argument_list|)
operator|.
name|append
argument_list|(
name|cGroupPrefix
argument_list|)
operator|.
name|append
argument_list|(
literal|"/"
argument_list|)
operator|.
name|append
argument_list|(
name|cGroupId
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getPathForCGroupTasks (CGroupController controller, String cGroupId)
specifier|public
name|String
name|getPathForCGroupTasks
parameter_list|(
name|CGroupController
name|controller
parameter_list|,
name|String
name|cGroupId
parameter_list|)
block|{
return|return
operator|new
name|StringBuffer
argument_list|(
name|getPathForCGroup
argument_list|(
name|controller
argument_list|,
name|cGroupId
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|'/'
argument_list|)
operator|.
name|append
argument_list|(
name|CGROUP_FILE_TASKS
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getPathForCGroupParam (CGroupController controller, String cGroupId, String param)
specifier|public
name|String
name|getPathForCGroupParam
parameter_list|(
name|CGroupController
name|controller
parameter_list|,
name|String
name|cGroupId
parameter_list|,
name|String
name|param
parameter_list|)
block|{
return|return
operator|new
name|StringBuffer
argument_list|(
name|getPathForCGroup
argument_list|(
name|controller
argument_list|,
name|cGroupId
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|'/'
argument_list|)
operator|.
name|append
argument_list|(
name|controller
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|'.'
argument_list|)
operator|.
name|append
argument_list|(
name|param
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|createCGroup (CGroupController controller, String cGroupId)
specifier|public
name|String
name|createCGroup
parameter_list|(
name|CGroupController
name|controller
parameter_list|,
name|String
name|cGroupId
parameter_list|)
throws|throws
name|ResourceHandlerException
block|{
name|String
name|path
init|=
name|getPathForCGroup
argument_list|(
name|controller
argument_list|,
name|cGroupId
argument_list|)
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"createCgroup: "
operator|+
name|path
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
operator|new
name|File
argument_list|(
name|path
argument_list|)
operator|.
name|mkdir
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ResourceHandlerException
argument_list|(
literal|"Failed to create cgroup at "
operator|+
name|path
argument_list|)
throw|;
block|}
return|return
name|path
return|;
block|}
comment|/*   * Utility routine to print first line from cgroup tasks file   */
DECL|method|logLineFromTasksFile (File cgf)
specifier|private
name|void
name|logLineFromTasksFile
parameter_list|(
name|File
name|cgf
parameter_list|)
block|{
name|String
name|str
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
try|try
init|(
name|BufferedReader
name|inl
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|cgf
operator|+
literal|"/tasks"
argument_list|)
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
init|)
block|{
if|if
condition|(
operator|(
name|str
operator|=
name|inl
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"First line in cgroup tasks file: "
operator|+
name|cgf
operator|+
literal|" "
operator|+
name|str
argument_list|)
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
name|warn
argument_list|(
literal|"Failed to read cgroup tasks file. "
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * If tasks file is empty, delete the cgroup.    *    * @param cgf object referring to the cgroup to be deleted    * @return Boolean indicating whether cgroup was deleted    */
DECL|method|checkAndDeleteCgroup (File cgf)
name|boolean
name|checkAndDeleteCgroup
parameter_list|(
name|File
name|cgf
parameter_list|)
throws|throws
name|InterruptedException
block|{
name|boolean
name|deleted
init|=
literal|false
decl_stmt|;
comment|// FileInputStream in = null;
try|try
init|(
name|FileInputStream
name|in
init|=
operator|new
name|FileInputStream
argument_list|(
name|cgf
operator|+
literal|"/tasks"
argument_list|)
init|)
block|{
if|if
condition|(
name|in
operator|.
name|read
argument_list|()
operator|==
operator|-
literal|1
condition|)
block|{
comment|/*          * "tasks" file is empty, sleep a bit more and then try to delete the          * cgroup. Some versions of linux will occasionally panic due to a race          * condition in this area, hence the paranoia.          */
name|Thread
operator|.
name|sleep
argument_list|(
name|deleteCGroupDelay
argument_list|)
expr_stmt|;
name|deleted
operator|=
name|cgf
operator|.
name|delete
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|deleted
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed attempt to delete cgroup: "
operator|+
name|cgf
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|logLineFromTasksFile
argument_list|(
name|cgf
argument_list|)
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
name|warn
argument_list|(
literal|"Failed to read cgroup tasks file. "
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
name|deleted
return|;
block|}
annotation|@
name|Override
DECL|method|deleteCGroup (CGroupController controller, String cGroupId)
specifier|public
name|void
name|deleteCGroup
parameter_list|(
name|CGroupController
name|controller
parameter_list|,
name|String
name|cGroupId
parameter_list|)
throws|throws
name|ResourceHandlerException
block|{
name|boolean
name|deleted
init|=
literal|false
decl_stmt|;
name|String
name|cGroupPath
init|=
name|getPathForCGroup
argument_list|(
name|controller
argument_list|,
name|cGroupId
argument_list|)
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"deleteCGroup: "
operator|+
name|cGroupPath
argument_list|)
expr_stmt|;
block|}
name|long
name|start
init|=
name|clock
operator|.
name|getTime
argument_list|()
decl_stmt|;
do|do
block|{
try|try
block|{
name|deleted
operator|=
name|checkAndDeleteCgroup
argument_list|(
operator|new
name|File
argument_list|(
name|cGroupPath
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|deleted
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|deleteCGroupDelay
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ex
parameter_list|)
block|{
comment|// NOP
block|}
block|}
do|while
condition|(
operator|!
name|deleted
operator|&&
operator|(
name|clock
operator|.
name|getTime
argument_list|()
operator|-
name|start
operator|)
operator|<
name|deleteCGroupTimeout
condition|)
do|;
if|if
condition|(
operator|!
name|deleted
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unable to delete  "
operator|+
name|cGroupPath
operator|+
literal|", tried to delete for "
operator|+
name|deleteCGroupTimeout
operator|+
literal|"ms"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|updateCGroupParam (CGroupController controller, String cGroupId, String param, String value)
specifier|public
name|void
name|updateCGroupParam
parameter_list|(
name|CGroupController
name|controller
parameter_list|,
name|String
name|cGroupId
parameter_list|,
name|String
name|param
parameter_list|,
name|String
name|value
parameter_list|)
throws|throws
name|ResourceHandlerException
block|{
name|String
name|cGroupParamPath
init|=
name|getPathForCGroupParam
argument_list|(
name|controller
argument_list|,
name|cGroupId
argument_list|,
name|param
argument_list|)
decl_stmt|;
name|PrintWriter
name|pw
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"updateCGroupParam for path: "
operator|+
name|cGroupParamPath
operator|+
literal|" with value "
operator|+
name|value
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|cGroupParamPath
argument_list|)
decl_stmt|;
name|Writer
name|w
init|=
operator|new
name|OutputStreamWriter
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
name|file
argument_list|)
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
name|pw
operator|=
operator|new
name|PrintWriter
argument_list|(
name|w
argument_list|)
expr_stmt|;
name|pw
operator|.
name|write
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ResourceHandlerException
argument_list|(
operator|new
name|StringBuffer
argument_list|(
literal|"Unable to write to "
argument_list|)
operator|.
name|append
argument_list|(
name|cGroupParamPath
argument_list|)
operator|.
name|append
argument_list|(
literal|" with value: "
argument_list|)
operator|.
name|append
argument_list|(
name|value
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|pw
operator|!=
literal|null
condition|)
block|{
name|boolean
name|hasError
init|=
name|pw
operator|.
name|checkError
argument_list|()
decl_stmt|;
name|pw
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|hasError
condition|)
block|{
throw|throw
operator|new
name|ResourceHandlerException
argument_list|(
operator|new
name|StringBuffer
argument_list|(
literal|"Unable to write to "
argument_list|)
operator|.
name|append
argument_list|(
name|cGroupParamPath
argument_list|)
operator|.
name|append
argument_list|(
literal|" with value: "
argument_list|)
operator|.
name|append
argument_list|(
name|value
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
name|pw
operator|.
name|checkError
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ResourceHandlerException
argument_list|(
literal|"Error while closing cgroup file"
operator|+
literal|" "
operator|+
name|cGroupParamPath
argument_list|)
throw|;
block|}
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|getCGroupParam (CGroupController controller, String cGroupId, String param)
specifier|public
name|String
name|getCGroupParam
parameter_list|(
name|CGroupController
name|controller
parameter_list|,
name|String
name|cGroupId
parameter_list|,
name|String
name|param
parameter_list|)
throws|throws
name|ResourceHandlerException
block|{
name|String
name|cGroupParamPath
init|=
name|getPathForCGroupParam
argument_list|(
name|controller
argument_list|,
name|cGroupId
argument_list|,
name|param
argument_list|)
decl_stmt|;
try|try
block|{
name|byte
index|[]
name|contents
init|=
name|Files
operator|.
name|readAllBytes
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
name|cGroupParamPath
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|new
name|String
argument_list|(
name|contents
argument_list|,
literal|"UTF-8"
argument_list|)
operator|.
name|trim
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ResourceHandlerException
argument_list|(
literal|"Unable to read from "
operator|+
name|cGroupParamPath
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

