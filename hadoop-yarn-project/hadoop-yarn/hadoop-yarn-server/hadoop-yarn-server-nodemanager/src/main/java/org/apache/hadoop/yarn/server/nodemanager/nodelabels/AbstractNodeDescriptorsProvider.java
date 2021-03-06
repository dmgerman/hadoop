begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.nodelabels
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
name|nodelabels
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
name|util
operator|.
name|HashSet
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
name|Timer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TimerTask
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
name|Collections
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
name|service
operator|.
name|AbstractService
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
name|NodeLabel
import|;
end_import

begin_comment
comment|/**  * Provides base implementation of NodeDescriptorsProvider with Timer and  * expects subclass to provide TimerTask which can fetch node descriptors.  */
end_comment

begin_class
DECL|class|AbstractNodeDescriptorsProvider
specifier|public
specifier|abstract
class|class
name|AbstractNodeDescriptorsProvider
parameter_list|<
name|T
parameter_list|>
extends|extends
name|AbstractService
implements|implements
name|NodeDescriptorsProvider
argument_list|<
name|T
argument_list|>
block|{
DECL|field|DISABLE_NODE_DESCRIPTORS_PROVIDER_FETCH_TIMER
specifier|public
specifier|static
specifier|final
name|long
name|DISABLE_NODE_DESCRIPTORS_PROVIDER_FETCH_TIMER
init|=
operator|-
literal|1
decl_stmt|;
comment|// Delay after which timer task are triggered to fetch node descriptors.
comment|// Default interval is -1 means it is an one time task, each implementation
comment|// will override this value from configuration.
DECL|field|intervalTime
specifier|private
name|long
name|intervalTime
init|=
operator|-
literal|1
decl_stmt|;
comment|// Timer used to schedule node descriptors fetching
DECL|field|scheduler
specifier|private
name|Timer
name|scheduler
decl_stmt|;
DECL|field|readLock
specifier|protected
name|Lock
name|readLock
init|=
literal|null
decl_stmt|;
DECL|field|writeLock
specifier|protected
name|Lock
name|writeLock
init|=
literal|null
decl_stmt|;
DECL|field|timerTask
specifier|protected
name|TimerTask
name|timerTask
decl_stmt|;
DECL|field|nodeDescriptors
specifier|private
name|Set
argument_list|<
name|T
argument_list|>
name|nodeDescriptors
init|=
name|Collections
operator|.
name|unmodifiableSet
argument_list|(
operator|new
name|HashSet
argument_list|<>
argument_list|(
literal|0
argument_list|)
argument_list|)
decl_stmt|;
DECL|method|AbstractNodeDescriptorsProvider (String name)
specifier|public
name|AbstractNodeDescriptorsProvider
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
DECL|method|getIntervalTime ()
specifier|public
name|long
name|getIntervalTime
parameter_list|()
block|{
return|return
name|intervalTime
return|;
block|}
DECL|method|setIntervalTime (long intervalMS)
specifier|public
name|void
name|setIntervalTime
parameter_list|(
name|long
name|intervalMS
parameter_list|)
block|{
name|this
operator|.
name|intervalTime
operator|=
name|intervalMS
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceInit (Configuration conf)
specifier|protected
name|void
name|serviceInit
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|Exception
block|{
name|ReadWriteLock
name|readWriteLock
init|=
operator|new
name|ReentrantReadWriteLock
argument_list|()
decl_stmt|;
name|readLock
operator|=
name|readWriteLock
operator|.
name|readLock
argument_list|()
expr_stmt|;
name|writeLock
operator|=
name|readWriteLock
operator|.
name|writeLock
argument_list|()
expr_stmt|;
name|super
operator|.
name|serviceInit
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceStart ()
specifier|protected
name|void
name|serviceStart
parameter_list|()
throws|throws
name|Exception
block|{
name|timerTask
operator|=
name|createTimerTask
argument_list|()
expr_stmt|;
name|timerTask
operator|.
name|run
argument_list|()
expr_stmt|;
name|long
name|taskInterval
init|=
name|getIntervalTime
argument_list|()
decl_stmt|;
if|if
condition|(
name|taskInterval
operator|!=
name|DISABLE_NODE_DESCRIPTORS_PROVIDER_FETCH_TIMER
condition|)
block|{
name|scheduler
operator|=
operator|new
name|Timer
argument_list|(
literal|"DistributedNodeDescriptorsRunner-Timer"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// Start the timer task and then periodically at the configured interval
comment|// time. Illegal values for intervalTime is handled by timer api
name|scheduler
operator|.
name|schedule
argument_list|(
name|timerTask
argument_list|,
name|taskInterval
argument_list|,
name|taskInterval
argument_list|)
expr_stmt|;
block|}
name|super
operator|.
name|serviceStart
argument_list|()
expr_stmt|;
block|}
comment|/**    * terminate the timer    * @throws Exception    */
annotation|@
name|Override
DECL|method|serviceStop ()
specifier|protected
name|void
name|serviceStop
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|scheduler
operator|!=
literal|null
condition|)
block|{
name|scheduler
operator|.
name|cancel
argument_list|()
expr_stmt|;
block|}
name|cleanUp
argument_list|()
expr_stmt|;
name|super
operator|.
name|serviceStop
argument_list|()
expr_stmt|;
block|}
comment|/**    * method for subclasses to cleanup.    */
DECL|method|cleanUp ()
specifier|protected
specifier|abstract
name|void
name|cleanUp
parameter_list|()
throws|throws
name|Exception
function_decl|;
comment|/**    * @return Returns output from provider.    */
annotation|@
name|Override
DECL|method|getDescriptors ()
specifier|public
name|Set
argument_list|<
name|T
argument_list|>
name|getDescriptors
parameter_list|()
block|{
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
return|return
name|this
operator|.
name|nodeDescriptors
return|;
block|}
finally|finally
block|{
name|readLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|setDescriptors (Set<T> descriptorsSet)
specifier|public
name|void
name|setDescriptors
parameter_list|(
name|Set
argument_list|<
name|T
argument_list|>
name|descriptorsSet
parameter_list|)
block|{
name|writeLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|this
operator|.
name|nodeDescriptors
operator|=
name|descriptorsSet
expr_stmt|;
block|}
finally|finally
block|{
name|writeLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Method used to determine if or not node descriptors fetching script is    * configured and whether it is fit to run. Returns true if following    * conditions are met:    *    *<ol>    *<li>Path to the script is not empty</li>    *<li>The script file exists</li>    *</ol>    *    * @throws IOException    */
DECL|method|verifyConfiguredScript (String scriptPath)
specifier|protected
name|void
name|verifyConfiguredScript
parameter_list|(
name|String
name|scriptPath
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|invalidConfiguration
decl_stmt|;
if|if
condition|(
name|scriptPath
operator|==
literal|null
operator|||
name|scriptPath
operator|.
name|trim
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|invalidConfiguration
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|scriptPath
argument_list|)
decl_stmt|;
name|invalidConfiguration
operator|=
operator|!
name|f
operator|.
name|exists
argument_list|()
operator|||
operator|!
name|FileUtil
operator|.
name|canExecute
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|invalidConfiguration
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Node descriptors provider script \""
operator|+
name|scriptPath
operator|+
literal|"\" is not configured properly. Please check whether"
operator|+
literal|" the script path exists, owner and the access rights"
operator|+
literal|" are suitable for NM process to execute it"
argument_list|)
throw|;
block|}
block|}
DECL|method|convertToNodeLabelSet (String partitionNodeLabel)
specifier|static
name|Set
argument_list|<
name|NodeLabel
argument_list|>
name|convertToNodeLabelSet
parameter_list|(
name|String
name|partitionNodeLabel
parameter_list|)
block|{
if|if
condition|(
literal|null
operator|==
name|partitionNodeLabel
condition|)
block|{
return|return
literal|null
return|;
block|}
name|Set
argument_list|<
name|NodeLabel
argument_list|>
name|labels
init|=
operator|new
name|HashSet
argument_list|<
name|NodeLabel
argument_list|>
argument_list|()
decl_stmt|;
name|labels
operator|.
name|add
argument_list|(
name|NodeLabel
operator|.
name|newInstance
argument_list|(
name|partitionNodeLabel
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|labels
return|;
block|}
comment|/**    * Used only by tests to access the timer task directly    *    * @return the timer task    */
DECL|method|getTimerTask ()
name|TimerTask
name|getTimerTask
parameter_list|()
block|{
return|return
name|timerTask
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getScheduler ()
specifier|public
name|Timer
name|getScheduler
parameter_list|()
block|{
return|return
name|this
operator|.
name|scheduler
return|;
block|}
comment|/**    * Creates a timer task which be scheduled periodically by the provider,    * and the task is responsible to update node descriptors to the provider.    * @return a timer task.    */
DECL|method|createTimerTask ()
specifier|public
specifier|abstract
name|TimerTask
name|createTimerTask
parameter_list|()
function_decl|;
block|}
end_class

end_unit

