begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair
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
name|resourcemanager
operator|.
name|scheduler
operator|.
name|fair
package|;
end_package

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
name|Iterator
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
name|PriorityQueue
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
name|collect
operator|.
name|ArrayListMultimap
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
name|ListMultimap
import|;
end_import

begin_comment
comment|/**  * Handles tracking and enforcement for user and queue maxRunningApps  * constraints  */
end_comment

begin_class
DECL|class|MaxRunningAppsEnforcer
specifier|public
class|class
name|MaxRunningAppsEnforcer
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
name|FairScheduler
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|scheduler
specifier|private
specifier|final
name|FairScheduler
name|scheduler
decl_stmt|;
comment|// Tracks the number of running applications by user.
DECL|field|usersNumRunnableApps
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|usersNumRunnableApps
decl_stmt|;
annotation|@
name|VisibleForTesting
DECL|field|usersNonRunnableApps
specifier|final
name|ListMultimap
argument_list|<
name|String
argument_list|,
name|FSAppAttempt
argument_list|>
name|usersNonRunnableApps
decl_stmt|;
DECL|method|MaxRunningAppsEnforcer (FairScheduler scheduler)
specifier|public
name|MaxRunningAppsEnforcer
parameter_list|(
name|FairScheduler
name|scheduler
parameter_list|)
block|{
name|this
operator|.
name|scheduler
operator|=
name|scheduler
expr_stmt|;
name|this
operator|.
name|usersNumRunnableApps
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|()
expr_stmt|;
name|this
operator|.
name|usersNonRunnableApps
operator|=
name|ArrayListMultimap
operator|.
name|create
argument_list|()
expr_stmt|;
block|}
comment|/**    * Checks whether making the application runnable would exceed any    * maxRunningApps limits.    */
DECL|method|canAppBeRunnable (FSQueue queue, String user)
specifier|public
name|boolean
name|canAppBeRunnable
parameter_list|(
name|FSQueue
name|queue
parameter_list|,
name|String
name|user
parameter_list|)
block|{
name|AllocationConfiguration
name|allocConf
init|=
name|scheduler
operator|.
name|getAllocationConfiguration
argument_list|()
decl_stmt|;
name|Integer
name|userNumRunnable
init|=
name|usersNumRunnableApps
operator|.
name|get
argument_list|(
name|user
argument_list|)
decl_stmt|;
if|if
condition|(
name|userNumRunnable
operator|==
literal|null
condition|)
block|{
name|userNumRunnable
operator|=
literal|0
expr_stmt|;
block|}
if|if
condition|(
name|userNumRunnable
operator|>=
name|allocConf
operator|.
name|getUserMaxApps
argument_list|(
name|user
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// Check queue and all parent queues
while|while
condition|(
name|queue
operator|!=
literal|null
condition|)
block|{
name|int
name|queueMaxApps
init|=
name|allocConf
operator|.
name|getQueueMaxApps
argument_list|(
name|queue
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|queue
operator|.
name|getNumRunnableApps
argument_list|()
operator|>=
name|queueMaxApps
condition|)
block|{
return|return
literal|false
return|;
block|}
name|queue
operator|=
name|queue
operator|.
name|getParent
argument_list|()
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
comment|/**    * Tracks the given new runnable app for purposes of maintaining max running    * app limits.    */
DECL|method|trackRunnableApp (FSAppAttempt app)
specifier|public
name|void
name|trackRunnableApp
parameter_list|(
name|FSAppAttempt
name|app
parameter_list|)
block|{
name|String
name|user
init|=
name|app
operator|.
name|getUser
argument_list|()
decl_stmt|;
name|FSLeafQueue
name|queue
init|=
name|app
operator|.
name|getQueue
argument_list|()
decl_stmt|;
comment|// Increment running counts for all parent queues
name|FSParentQueue
name|parent
init|=
name|queue
operator|.
name|getParent
argument_list|()
decl_stmt|;
while|while
condition|(
name|parent
operator|!=
literal|null
condition|)
block|{
name|parent
operator|.
name|incrementRunnableApps
argument_list|()
expr_stmt|;
name|parent
operator|=
name|parent
operator|.
name|getParent
argument_list|()
expr_stmt|;
block|}
name|Integer
name|userNumRunnable
init|=
name|usersNumRunnableApps
operator|.
name|get
argument_list|(
name|user
argument_list|)
decl_stmt|;
name|usersNumRunnableApps
operator|.
name|put
argument_list|(
name|user
argument_list|,
operator|(
name|userNumRunnable
operator|==
literal|null
condition|?
literal|0
else|:
name|userNumRunnable
operator|)
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tracks the given new non runnable app so that it can be made runnable when    * it would not violate max running app limits.    */
DECL|method|trackNonRunnableApp (FSAppAttempt app)
specifier|public
name|void
name|trackNonRunnableApp
parameter_list|(
name|FSAppAttempt
name|app
parameter_list|)
block|{
name|String
name|user
init|=
name|app
operator|.
name|getUser
argument_list|()
decl_stmt|;
name|usersNonRunnableApps
operator|.
name|put
argument_list|(
name|user
argument_list|,
name|app
argument_list|)
expr_stmt|;
block|}
comment|/**    * Checks to see whether any other applications runnable now that the given    * application has been removed from the given queue.  And makes them so.    *     * Runs in O(n log(n)) where n is the number of queues that are under the    * highest queue that went from having no slack to having slack.    */
DECL|method|updateRunnabilityOnAppRemoval (FSAppAttempt app, FSLeafQueue queue)
specifier|public
name|void
name|updateRunnabilityOnAppRemoval
parameter_list|(
name|FSAppAttempt
name|app
parameter_list|,
name|FSLeafQueue
name|queue
parameter_list|)
block|{
name|AllocationConfiguration
name|allocConf
init|=
name|scheduler
operator|.
name|getAllocationConfiguration
argument_list|()
decl_stmt|;
comment|// childqueueX might have no pending apps itself, but if a queue higher up
comment|// in the hierarchy parentqueueY has a maxRunningApps set, an app completion
comment|// in childqueueX could allow an app in some other distant child of
comment|// parentqueueY to become runnable.
comment|// An app removal will only possibly allow another app to become runnable if
comment|// the queue was already at its max before the removal.
comment|// Thus we find the ancestor queue highest in the tree for which the app
comment|// that was at its maxRunningApps before the removal.
name|FSQueue
name|highestQueueWithAppsNowRunnable
init|=
operator|(
name|queue
operator|.
name|getNumRunnableApps
argument_list|()
operator|==
name|allocConf
operator|.
name|getQueueMaxApps
argument_list|(
name|queue
operator|.
name|getName
argument_list|()
argument_list|)
operator|-
literal|1
operator|)
condition|?
name|queue
else|:
literal|null
decl_stmt|;
name|FSParentQueue
name|parent
init|=
name|queue
operator|.
name|getParent
argument_list|()
decl_stmt|;
while|while
condition|(
name|parent
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|parent
operator|.
name|getNumRunnableApps
argument_list|()
operator|==
name|allocConf
operator|.
name|getQueueMaxApps
argument_list|(
name|parent
operator|.
name|getName
argument_list|()
argument_list|)
operator|-
literal|1
condition|)
block|{
name|highestQueueWithAppsNowRunnable
operator|=
name|parent
expr_stmt|;
block|}
name|parent
operator|=
name|parent
operator|.
name|getParent
argument_list|()
expr_stmt|;
block|}
name|List
argument_list|<
name|List
argument_list|<
name|FSAppAttempt
argument_list|>
argument_list|>
name|appsNowMaybeRunnable
init|=
operator|new
name|ArrayList
argument_list|<
name|List
argument_list|<
name|FSAppAttempt
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
comment|// Compile lists of apps which may now be runnable
comment|// We gather lists instead of building a set of all non-runnable apps so
comment|// that this whole operation can be O(number of queues) instead of
comment|// O(number of apps)
if|if
condition|(
name|highestQueueWithAppsNowRunnable
operator|!=
literal|null
condition|)
block|{
name|gatherPossiblyRunnableAppLists
argument_list|(
name|highestQueueWithAppsNowRunnable
argument_list|,
name|appsNowMaybeRunnable
argument_list|)
expr_stmt|;
block|}
name|String
name|user
init|=
name|app
operator|.
name|getUser
argument_list|()
decl_stmt|;
name|Integer
name|userNumRunning
init|=
name|usersNumRunnableApps
operator|.
name|get
argument_list|(
name|user
argument_list|)
decl_stmt|;
if|if
condition|(
name|userNumRunning
operator|==
literal|null
condition|)
block|{
name|userNumRunning
operator|=
literal|0
expr_stmt|;
block|}
if|if
condition|(
name|userNumRunning
operator|==
name|allocConf
operator|.
name|getUserMaxApps
argument_list|(
name|user
argument_list|)
operator|-
literal|1
condition|)
block|{
name|List
argument_list|<
name|FSAppAttempt
argument_list|>
name|userWaitingApps
init|=
name|usersNonRunnableApps
operator|.
name|get
argument_list|(
name|user
argument_list|)
decl_stmt|;
if|if
condition|(
name|userWaitingApps
operator|!=
literal|null
condition|)
block|{
name|appsNowMaybeRunnable
operator|.
name|add
argument_list|(
name|userWaitingApps
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Scan through and check whether this means that any apps are now runnable
name|Iterator
argument_list|<
name|FSAppAttempt
argument_list|>
name|iter
init|=
operator|new
name|MultiListStartTimeIterator
argument_list|(
name|appsNowMaybeRunnable
argument_list|)
decl_stmt|;
name|FSAppAttempt
name|prev
init|=
literal|null
decl_stmt|;
name|List
argument_list|<
name|FSAppAttempt
argument_list|>
name|noLongerPendingApps
init|=
operator|new
name|ArrayList
argument_list|<
name|FSAppAttempt
argument_list|>
argument_list|()
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|FSAppAttempt
name|next
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|next
operator|==
name|prev
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|canAppBeRunnable
argument_list|(
name|next
operator|.
name|getQueue
argument_list|()
argument_list|,
name|next
operator|.
name|getUser
argument_list|()
argument_list|)
condition|)
block|{
name|trackRunnableApp
argument_list|(
name|next
argument_list|)
expr_stmt|;
name|FSAppAttempt
name|appSched
init|=
name|next
decl_stmt|;
name|next
operator|.
name|getQueue
argument_list|()
operator|.
name|addApp
argument_list|(
name|appSched
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|noLongerPendingApps
operator|.
name|add
argument_list|(
name|appSched
argument_list|)
expr_stmt|;
comment|// No more than one app per list will be able to be made runnable, so
comment|// we can stop looking after we've found that many
if|if
condition|(
name|noLongerPendingApps
operator|.
name|size
argument_list|()
operator|>=
name|appsNowMaybeRunnable
operator|.
name|size
argument_list|()
condition|)
block|{
break|break;
block|}
block|}
name|prev
operator|=
name|next
expr_stmt|;
block|}
comment|// We remove the apps from their pending lists afterwards so that we don't
comment|// pull them out from under the iterator.  If they are not in these lists
comment|// in the first place, there is a bug.
for|for
control|(
name|FSAppAttempt
name|appSched
range|:
name|noLongerPendingApps
control|)
block|{
if|if
condition|(
operator|!
name|appSched
operator|.
name|getQueue
argument_list|()
operator|.
name|removeNonRunnableApp
argument_list|(
name|appSched
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Can't make app runnable that does not already exist in queue"
operator|+
literal|" as non-runnable: "
operator|+
name|appSched
operator|+
literal|". This should never happen."
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|usersNonRunnableApps
operator|.
name|remove
argument_list|(
name|appSched
operator|.
name|getUser
argument_list|()
argument_list|,
name|appSched
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Waiting app "
operator|+
name|appSched
operator|+
literal|" expected to be in "
operator|+
literal|"usersNonRunnableApps, but was not. This should never happen."
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Updates the relevant tracking variables after a runnable app with the given    * queue and user has been removed.    */
DECL|method|untrackRunnableApp (FSAppAttempt app)
specifier|public
name|void
name|untrackRunnableApp
parameter_list|(
name|FSAppAttempt
name|app
parameter_list|)
block|{
comment|// Update usersRunnableApps
name|String
name|user
init|=
name|app
operator|.
name|getUser
argument_list|()
decl_stmt|;
name|int
name|newUserNumRunning
init|=
name|usersNumRunnableApps
operator|.
name|get
argument_list|(
name|user
argument_list|)
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|newUserNumRunning
operator|==
literal|0
condition|)
block|{
name|usersNumRunnableApps
operator|.
name|remove
argument_list|(
name|user
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|usersNumRunnableApps
operator|.
name|put
argument_list|(
name|user
argument_list|,
name|newUserNumRunning
argument_list|)
expr_stmt|;
block|}
comment|// Update runnable app bookkeeping for queues
name|FSLeafQueue
name|queue
init|=
name|app
operator|.
name|getQueue
argument_list|()
decl_stmt|;
name|FSParentQueue
name|parent
init|=
name|queue
operator|.
name|getParent
argument_list|()
decl_stmt|;
while|while
condition|(
name|parent
operator|!=
literal|null
condition|)
block|{
name|parent
operator|.
name|decrementRunnableApps
argument_list|()
expr_stmt|;
name|parent
operator|=
name|parent
operator|.
name|getParent
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Stops tracking the given non-runnable app    */
DECL|method|untrackNonRunnableApp (FSAppAttempt app)
specifier|public
name|void
name|untrackNonRunnableApp
parameter_list|(
name|FSAppAttempt
name|app
parameter_list|)
block|{
name|usersNonRunnableApps
operator|.
name|remove
argument_list|(
name|app
operator|.
name|getUser
argument_list|()
argument_list|,
name|app
argument_list|)
expr_stmt|;
block|}
comment|/**    * Traverses the queue hierarchy under the given queue to gather all lists    * of non-runnable applications.    */
DECL|method|gatherPossiblyRunnableAppLists (FSQueue queue, List<List<FSAppAttempt>> appLists)
specifier|private
name|void
name|gatherPossiblyRunnableAppLists
parameter_list|(
name|FSQueue
name|queue
parameter_list|,
name|List
argument_list|<
name|List
argument_list|<
name|FSAppAttempt
argument_list|>
argument_list|>
name|appLists
parameter_list|)
block|{
if|if
condition|(
name|queue
operator|.
name|getNumRunnableApps
argument_list|()
operator|<
name|scheduler
operator|.
name|getAllocationConfiguration
argument_list|()
operator|.
name|getQueueMaxApps
argument_list|(
name|queue
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
name|queue
operator|instanceof
name|FSLeafQueue
condition|)
block|{
name|appLists
operator|.
name|add
argument_list|(
operator|(
operator|(
name|FSLeafQueue
operator|)
name|queue
operator|)
operator|.
name|getCopyOfNonRunnableAppSchedulables
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|FSQueue
name|child
range|:
name|queue
operator|.
name|getChildQueues
argument_list|()
control|)
block|{
name|gatherPossiblyRunnableAppLists
argument_list|(
name|child
argument_list|,
name|appLists
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/**    * Takes a list of lists, each of which is ordered by start time, and returns    * their elements in order of start time.    *     * We maintain positions in each of the lists.  Each next() call advances    * the position in one of the lists.  We maintain a heap that orders lists    * by the start time of the app in the current position in that list.    * This allows us to pick which list to advance in O(log(num lists)) instead    * of O(num lists) time.    */
DECL|class|MultiListStartTimeIterator
specifier|static
class|class
name|MultiListStartTimeIterator
implements|implements
name|Iterator
argument_list|<
name|FSAppAttempt
argument_list|>
block|{
DECL|field|appLists
specifier|private
name|List
argument_list|<
name|FSAppAttempt
argument_list|>
index|[]
name|appLists
decl_stmt|;
DECL|field|curPositionsInAppLists
specifier|private
name|int
index|[]
name|curPositionsInAppLists
decl_stmt|;
DECL|field|appListsByCurStartTime
specifier|private
name|PriorityQueue
argument_list|<
name|IndexAndTime
argument_list|>
name|appListsByCurStartTime
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|MultiListStartTimeIterator (List<List<FSAppAttempt>> appListList)
specifier|public
name|MultiListStartTimeIterator
parameter_list|(
name|List
argument_list|<
name|List
argument_list|<
name|FSAppAttempt
argument_list|>
argument_list|>
name|appListList
parameter_list|)
block|{
name|appLists
operator|=
name|appListList
operator|.
name|toArray
argument_list|(
operator|new
name|List
index|[
name|appListList
operator|.
name|size
argument_list|()
index|]
argument_list|)
expr_stmt|;
name|curPositionsInAppLists
operator|=
operator|new
name|int
index|[
name|appLists
operator|.
name|length
index|]
expr_stmt|;
name|appListsByCurStartTime
operator|=
operator|new
name|PriorityQueue
argument_list|<
name|IndexAndTime
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|appLists
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|long
name|time
init|=
name|appLists
index|[
name|i
index|]
operator|.
name|isEmpty
argument_list|()
condition|?
name|Long
operator|.
name|MAX_VALUE
else|:
name|appLists
index|[
name|i
index|]
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getStartTime
argument_list|()
decl_stmt|;
name|appListsByCurStartTime
operator|.
name|add
argument_list|(
operator|new
name|IndexAndTime
argument_list|(
name|i
argument_list|,
name|time
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|hasNext ()
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
operator|!
name|appListsByCurStartTime
operator|.
name|isEmpty
argument_list|()
operator|&&
name|appListsByCurStartTime
operator|.
name|peek
argument_list|()
operator|.
name|time
operator|!=
name|Long
operator|.
name|MAX_VALUE
return|;
block|}
annotation|@
name|Override
DECL|method|next ()
specifier|public
name|FSAppAttempt
name|next
parameter_list|()
block|{
name|IndexAndTime
name|indexAndTime
init|=
name|appListsByCurStartTime
operator|.
name|remove
argument_list|()
decl_stmt|;
name|int
name|nextListIndex
init|=
name|indexAndTime
operator|.
name|index
decl_stmt|;
name|FSAppAttempt
name|next
init|=
name|appLists
index|[
name|nextListIndex
index|]
operator|.
name|get
argument_list|(
name|curPositionsInAppLists
index|[
name|nextListIndex
index|]
argument_list|)
decl_stmt|;
name|curPositionsInAppLists
index|[
name|nextListIndex
index|]
operator|++
expr_stmt|;
if|if
condition|(
name|curPositionsInAppLists
index|[
name|nextListIndex
index|]
operator|<
name|appLists
index|[
name|nextListIndex
index|]
operator|.
name|size
argument_list|()
condition|)
block|{
name|indexAndTime
operator|.
name|time
operator|=
name|appLists
index|[
name|nextListIndex
index|]
operator|.
name|get
argument_list|(
name|curPositionsInAppLists
index|[
name|nextListIndex
index|]
argument_list|)
operator|.
name|getStartTime
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|indexAndTime
operator|.
name|time
operator|=
name|Long
operator|.
name|MAX_VALUE
expr_stmt|;
block|}
name|appListsByCurStartTime
operator|.
name|add
argument_list|(
name|indexAndTime
argument_list|)
expr_stmt|;
return|return
name|next
return|;
block|}
annotation|@
name|Override
DECL|method|remove ()
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Remove not supported"
argument_list|)
throw|;
block|}
DECL|class|IndexAndTime
specifier|private
specifier|static
class|class
name|IndexAndTime
implements|implements
name|Comparable
argument_list|<
name|IndexAndTime
argument_list|>
block|{
DECL|field|index
specifier|public
name|int
name|index
decl_stmt|;
DECL|field|time
specifier|public
name|long
name|time
decl_stmt|;
DECL|method|IndexAndTime (int index, long time)
specifier|public
name|IndexAndTime
parameter_list|(
name|int
name|index
parameter_list|,
name|long
name|time
parameter_list|)
block|{
name|this
operator|.
name|index
operator|=
name|index
expr_stmt|;
name|this
operator|.
name|time
operator|=
name|time
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|compareTo (IndexAndTime o)
specifier|public
name|int
name|compareTo
parameter_list|(
name|IndexAndTime
name|o
parameter_list|)
block|{
return|return
name|time
operator|<
name|o
operator|.
name|time
condition|?
operator|-
literal|1
else|:
operator|(
name|time
operator|>
name|o
operator|.
name|time
condition|?
literal|1
else|:
literal|0
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|equals (Object o)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|IndexAndTime
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|IndexAndTime
name|other
init|=
operator|(
name|IndexAndTime
operator|)
name|o
decl_stmt|;
return|return
name|other
operator|.
name|time
operator|==
name|time
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
operator|(
name|int
operator|)
name|time
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

