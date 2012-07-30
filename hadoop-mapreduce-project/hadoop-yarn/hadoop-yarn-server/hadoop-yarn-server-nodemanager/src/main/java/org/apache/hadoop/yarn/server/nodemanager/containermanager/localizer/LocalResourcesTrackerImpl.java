begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer
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
name|localizer
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
name|util
operator|.
name|Iterator
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
name|ConcurrentHashMap
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
name|ConcurrentMap
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
name|api
operator|.
name|records
operator|.
name|LocalResourceVisibility
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
name|event
operator|.
name|Dispatcher
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
name|DeletionService
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
name|localizer
operator|.
name|event
operator|.
name|ResourceEvent
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
name|localizer
operator|.
name|event
operator|.
name|ResourceEventType
import|;
end_import

begin_comment
comment|/**  * A collection of {@link LocalizedResource}s all of same  * {@link LocalResourceVisibility}.  *   */
end_comment

begin_class
DECL|class|LocalResourcesTrackerImpl
class|class
name|LocalResourcesTrackerImpl
implements|implements
name|LocalResourcesTracker
block|{
DECL|field|LOG
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|LocalResourcesTrackerImpl
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|RANDOM_DIR_REGEX
specifier|private
specifier|static
specifier|final
name|String
name|RANDOM_DIR_REGEX
init|=
literal|"-?\\d+"
decl_stmt|;
DECL|field|RANDOM_DIR_PATTERN
specifier|private
specifier|static
specifier|final
name|Pattern
name|RANDOM_DIR_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
name|RANDOM_DIR_REGEX
argument_list|)
decl_stmt|;
DECL|field|user
specifier|private
specifier|final
name|String
name|user
decl_stmt|;
DECL|field|dispatcher
specifier|private
specifier|final
name|Dispatcher
name|dispatcher
decl_stmt|;
DECL|field|localrsrc
specifier|private
specifier|final
name|ConcurrentMap
argument_list|<
name|LocalResourceRequest
argument_list|,
name|LocalizedResource
argument_list|>
name|localrsrc
decl_stmt|;
DECL|method|LocalResourcesTrackerImpl (String user, Dispatcher dispatcher)
specifier|public
name|LocalResourcesTrackerImpl
parameter_list|(
name|String
name|user
parameter_list|,
name|Dispatcher
name|dispatcher
parameter_list|)
block|{
name|this
argument_list|(
name|user
argument_list|,
name|dispatcher
argument_list|,
operator|new
name|ConcurrentHashMap
argument_list|<
name|LocalResourceRequest
argument_list|,
name|LocalizedResource
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|LocalResourcesTrackerImpl (String user, Dispatcher dispatcher, ConcurrentMap<LocalResourceRequest,LocalizedResource> localrsrc)
name|LocalResourcesTrackerImpl
parameter_list|(
name|String
name|user
parameter_list|,
name|Dispatcher
name|dispatcher
parameter_list|,
name|ConcurrentMap
argument_list|<
name|LocalResourceRequest
argument_list|,
name|LocalizedResource
argument_list|>
name|localrsrc
parameter_list|)
block|{
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
name|this
operator|.
name|dispatcher
operator|=
name|dispatcher
expr_stmt|;
name|this
operator|.
name|localrsrc
operator|=
name|localrsrc
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|handle (ResourceEvent event)
specifier|public
name|void
name|handle
parameter_list|(
name|ResourceEvent
name|event
parameter_list|)
block|{
name|LocalResourceRequest
name|req
init|=
name|event
operator|.
name|getLocalResourceRequest
argument_list|()
decl_stmt|;
name|LocalizedResource
name|rsrc
init|=
name|localrsrc
operator|.
name|get
argument_list|(
name|req
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|event
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|REQUEST
case|:
case|case
name|LOCALIZED
case|:
if|if
condition|(
name|rsrc
operator|!=
literal|null
operator|&&
operator|(
operator|!
name|isResourcePresent
argument_list|(
name|rsrc
argument_list|)
operator|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Resource "
operator|+
name|rsrc
operator|.
name|getLocalPath
argument_list|()
operator|+
literal|" is missing, localizing it again"
argument_list|)
expr_stmt|;
name|localrsrc
operator|.
name|remove
argument_list|(
name|req
argument_list|)
expr_stmt|;
name|rsrc
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
literal|null
operator|==
name|rsrc
condition|)
block|{
name|rsrc
operator|=
operator|new
name|LocalizedResource
argument_list|(
name|req
argument_list|,
name|dispatcher
argument_list|)
expr_stmt|;
name|localrsrc
operator|.
name|put
argument_list|(
name|req
argument_list|,
name|rsrc
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
name|RELEASE
case|:
if|if
condition|(
literal|null
operator|==
name|rsrc
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Release unknown rsrc null (discard)"
argument_list|)
expr_stmt|;
return|return;
block|}
break|break;
block|}
name|rsrc
operator|.
name|handle
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
comment|/**    * This module checks if the resource which was localized is already present    * or not    *     * @param rsrc    * @return true/false based on resource is present or not    */
DECL|method|isResourcePresent (LocalizedResource rsrc)
specifier|public
name|boolean
name|isResourcePresent
parameter_list|(
name|LocalizedResource
name|rsrc
parameter_list|)
block|{
name|boolean
name|ret
init|=
literal|true
decl_stmt|;
if|if
condition|(
name|rsrc
operator|.
name|getState
argument_list|()
operator|==
name|ResourceState
operator|.
name|LOCALIZED
condition|)
block|{
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|rsrc
operator|.
name|getLocalPath
argument_list|()
operator|.
name|toUri
argument_list|()
operator|.
name|getRawPath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|file
operator|.
name|exists
argument_list|()
condition|)
block|{
name|ret
operator|=
literal|false
expr_stmt|;
block|}
block|}
return|return
name|ret
return|;
block|}
annotation|@
name|Override
DECL|method|contains (LocalResourceRequest resource)
specifier|public
name|boolean
name|contains
parameter_list|(
name|LocalResourceRequest
name|resource
parameter_list|)
block|{
return|return
name|localrsrc
operator|.
name|containsKey
argument_list|(
name|resource
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|remove (LocalizedResource rem, DeletionService delService)
specifier|public
name|boolean
name|remove
parameter_list|(
name|LocalizedResource
name|rem
parameter_list|,
name|DeletionService
name|delService
parameter_list|)
block|{
comment|// current synchronization guaranteed by crude RLS event for cleanup
name|LocalizedResource
name|rsrc
init|=
name|localrsrc
operator|.
name|get
argument_list|(
name|rem
operator|.
name|getRequest
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|rsrc
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Attempt to remove absent resource: "
operator|+
name|rem
operator|.
name|getRequest
argument_list|()
operator|+
literal|" from "
operator|+
name|getUser
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
if|if
condition|(
name|rsrc
operator|.
name|getRefCount
argument_list|()
operator|>
literal|0
operator|||
name|ResourceState
operator|.
name|DOWNLOADING
operator|.
name|equals
argument_list|(
name|rsrc
operator|.
name|getState
argument_list|()
argument_list|)
operator|||
name|rsrc
operator|!=
name|rem
condition|)
block|{
comment|// internal error
name|LOG
operator|.
name|error
argument_list|(
literal|"Attempt to remove resource: "
operator|+
name|rsrc
operator|+
literal|" with non-zero refcount"
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
else|else
block|{
comment|// ResourceState is LOCALIZED or INIT
name|localrsrc
operator|.
name|remove
argument_list|(
name|rem
operator|.
name|getRequest
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|ResourceState
operator|.
name|LOCALIZED
operator|.
name|equals
argument_list|(
name|rsrc
operator|.
name|getState
argument_list|()
argument_list|)
condition|)
block|{
name|delService
operator|.
name|delete
argument_list|(
name|getUser
argument_list|()
argument_list|,
name|getPathToDelete
argument_list|(
name|rsrc
operator|.
name|getLocalPath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
block|}
comment|/**    * Returns the path up to the random directory component.    */
DECL|method|getPathToDelete (Path localPath)
specifier|private
name|Path
name|getPathToDelete
parameter_list|(
name|Path
name|localPath
parameter_list|)
block|{
name|Path
name|delPath
init|=
name|localPath
operator|.
name|getParent
argument_list|()
decl_stmt|;
name|String
name|name
init|=
name|delPath
operator|.
name|getName
argument_list|()
decl_stmt|;
name|Matcher
name|matcher
init|=
name|RANDOM_DIR_PATTERN
operator|.
name|matcher
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|matcher
operator|.
name|matches
argument_list|()
condition|)
block|{
return|return
name|delPath
return|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Random directory component did not match. "
operator|+
literal|"Deleting localized path only"
argument_list|)
expr_stmt|;
return|return
name|localPath
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|getUser ()
specifier|public
name|String
name|getUser
parameter_list|()
block|{
return|return
name|user
return|;
block|}
annotation|@
name|Override
DECL|method|iterator ()
specifier|public
name|Iterator
argument_list|<
name|LocalizedResource
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|localrsrc
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
return|;
block|}
block|}
end_class

end_unit

