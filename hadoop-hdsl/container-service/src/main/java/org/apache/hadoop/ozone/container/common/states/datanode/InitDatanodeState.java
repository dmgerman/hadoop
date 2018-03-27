begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.common.states.datanode
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|states
operator|.
name|datanode
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
name|base
operator|.
name|Strings
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
name|hdsl
operator|.
name|HdslUtils
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
name|hdsl
operator|.
name|protocol
operator|.
name|DatanodeDetails
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|ContainerUtils
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|statemachine
operator|.
name|DatanodeStateMachine
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|statemachine
operator|.
name|SCMConnectionManager
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|statemachine
operator|.
name|StateContext
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|states
operator|.
name|DatanodeState
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
name|scm
operator|.
name|ScmConfigKeys
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
name|InetSocketAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|TimeUnit
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
name|TimeoutException
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
name|hdsl
operator|.
name|HdslUtils
operator|.
name|getSCMAddresses
import|;
end_import

begin_comment
comment|/**  * Init Datanode State is the task that gets run when we are in Init State.  */
end_comment

begin_class
DECL|class|InitDatanodeState
specifier|public
class|class
name|InitDatanodeState
implements|implements
name|DatanodeState
implements|,
name|Callable
argument_list|<
name|DatanodeStateMachine
operator|.
name|DatanodeStates
argument_list|>
block|{
DECL|field|LOG
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|InitDatanodeState
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|connectionManager
specifier|private
specifier|final
name|SCMConnectionManager
name|connectionManager
decl_stmt|;
DECL|field|conf
specifier|private
specifier|final
name|Configuration
name|conf
decl_stmt|;
DECL|field|context
specifier|private
specifier|final
name|StateContext
name|context
decl_stmt|;
DECL|field|result
specifier|private
name|Future
argument_list|<
name|DatanodeStateMachine
operator|.
name|DatanodeStates
argument_list|>
name|result
decl_stmt|;
comment|/**    *  Create InitDatanodeState Task.    *    * @param conf - Conf    * @param connectionManager - Connection Manager    * @param context - Current Context    */
DECL|method|InitDatanodeState (Configuration conf, SCMConnectionManager connectionManager, StateContext context)
specifier|public
name|InitDatanodeState
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|SCMConnectionManager
name|connectionManager
parameter_list|,
name|StateContext
name|context
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
name|connectionManager
operator|=
name|connectionManager
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
block|}
comment|/**    * Computes a result, or throws an exception if unable to do so.    *    * @return computed result    * @throws Exception if unable to compute a result    */
annotation|@
name|Override
DECL|method|call ()
specifier|public
name|DatanodeStateMachine
operator|.
name|DatanodeStates
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|Collection
argument_list|<
name|InetSocketAddress
argument_list|>
name|addresses
init|=
literal|null
decl_stmt|;
try|try
block|{
name|addresses
operator|=
name|getSCMAddresses
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
if|if
condition|(
operator|!
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to get SCM addresses: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|DatanodeStateMachine
operator|.
name|DatanodeStates
operator|.
name|SHUTDOWN
return|;
block|}
if|if
condition|(
name|addresses
operator|==
literal|null
operator|||
name|addresses
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Null or empty SCM address list found."
argument_list|)
expr_stmt|;
return|return
name|DatanodeStateMachine
operator|.
name|DatanodeStates
operator|.
name|SHUTDOWN
return|;
block|}
else|else
block|{
for|for
control|(
name|InetSocketAddress
name|addr
range|:
name|addresses
control|)
block|{
name|connectionManager
operator|.
name|addSCMServer
argument_list|(
name|addr
argument_list|)
expr_stmt|;
block|}
block|}
comment|// If datanode ID is set, persist it to the ID file.
name|persistContainerDatanodeDetails
argument_list|()
expr_stmt|;
return|return
name|this
operator|.
name|context
operator|.
name|getState
argument_list|()
operator|.
name|getNextState
argument_list|()
return|;
block|}
comment|/**    * Persist DatanodeDetails to datanode.id file.    */
DECL|method|persistContainerDatanodeDetails ()
specifier|private
name|void
name|persistContainerDatanodeDetails
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|dataNodeIDPath
init|=
name|HdslUtils
operator|.
name|getDatanodeIdFilePath
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|File
name|idPath
init|=
operator|new
name|File
argument_list|(
name|dataNodeIDPath
argument_list|)
decl_stmt|;
name|DatanodeDetails
name|datanodeDetails
init|=
name|this
operator|.
name|context
operator|.
name|getParent
argument_list|()
operator|.
name|getDatanodeDetails
argument_list|()
decl_stmt|;
if|if
condition|(
name|datanodeDetails
operator|!=
literal|null
operator|&&
operator|!
name|idPath
operator|.
name|exists
argument_list|()
condition|)
block|{
name|ContainerUtils
operator|.
name|writeDatanodeDetailsTo
argument_list|(
name|datanodeDetails
argument_list|,
name|idPath
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"DatanodeDetails is persisted to {}"
argument_list|,
name|dataNodeIDPath
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Called before entering this state.    */
annotation|@
name|Override
DECL|method|onEnter ()
specifier|public
name|void
name|onEnter
parameter_list|()
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"Entering init container state"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Called After exiting this state.    */
annotation|@
name|Override
DECL|method|onExit ()
specifier|public
name|void
name|onExit
parameter_list|()
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"Exiting init container state"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Executes one or more tasks that is needed by this state.    *    * @param executor -  ExecutorService    */
annotation|@
name|Override
DECL|method|execute (ExecutorService executor)
specifier|public
name|void
name|execute
parameter_list|(
name|ExecutorService
name|executor
parameter_list|)
block|{
name|result
operator|=
name|executor
operator|.
name|submit
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
comment|/**    * Wait for execute to finish.    *    * @param time     - Time    * @param timeUnit - Unit of time.    */
annotation|@
name|Override
DECL|method|await (long time, TimeUnit timeUnit)
specifier|public
name|DatanodeStateMachine
operator|.
name|DatanodeStates
name|await
parameter_list|(
name|long
name|time
parameter_list|,
name|TimeUnit
name|timeUnit
parameter_list|)
throws|throws
name|InterruptedException
throws|,
name|ExecutionException
throws|,
name|TimeoutException
block|{
return|return
name|result
operator|.
name|get
argument_list|(
name|time
argument_list|,
name|timeUnit
argument_list|)
return|;
block|}
block|}
end_class

end_unit

