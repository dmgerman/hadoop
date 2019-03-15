begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server
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
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|BlockingQueue
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
name|LinkedBlockingQueue
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
name|yarn
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|AllocateRequest
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
name|protocolrecords
operator|.
name|AllocateResponse
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
name|ApplicationId
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
name|utils
operator|.
name|YarnServerSecurityUtils
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
name|AsyncCallback
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

begin_comment
comment|/**  * Extends Thread and provides an implementation that is used for processing the  * AM heart beat request asynchronously and sending back the response using the  * callback method registered with the system.  */
end_comment

begin_class
DECL|class|AMHeartbeatRequestHandler
specifier|public
class|class
name|AMHeartbeatRequestHandler
extends|extends
name|Thread
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
name|AMHeartbeatRequestHandler
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// Indication flag for the thread to keep running
DECL|field|keepRunning
specifier|private
specifier|volatile
name|boolean
name|keepRunning
decl_stmt|;
comment|// For unit test draining
DECL|field|isThreadWaiting
specifier|private
specifier|volatile
name|boolean
name|isThreadWaiting
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|applicationId
specifier|private
name|ApplicationId
name|applicationId
decl_stmt|;
DECL|field|requestQueue
specifier|private
name|BlockingQueue
argument_list|<
name|AsyncAllocateRequestInfo
argument_list|>
name|requestQueue
decl_stmt|;
DECL|field|rmProxyRelayer
specifier|private
name|AMRMClientRelayer
name|rmProxyRelayer
decl_stmt|;
DECL|field|userUgi
specifier|private
name|UserGroupInformation
name|userUgi
decl_stmt|;
DECL|field|lastResponseId
specifier|private
name|int
name|lastResponseId
decl_stmt|;
DECL|method|AMHeartbeatRequestHandler (Configuration conf, ApplicationId applicationId, AMRMClientRelayer rmProxyRelayer)
specifier|public
name|AMHeartbeatRequestHandler
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|ApplicationId
name|applicationId
parameter_list|,
name|AMRMClientRelayer
name|rmProxyRelayer
parameter_list|)
block|{
name|super
argument_list|(
literal|"AMHeartbeatRequestHandler Heartbeat Handler Thread"
argument_list|)
expr_stmt|;
name|this
operator|.
name|setUncaughtExceptionHandler
argument_list|(
operator|new
name|HeartBeatThreadUncaughtExceptionHandler
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|keepRunning
operator|=
literal|true
expr_stmt|;
name|this
operator|.
name|isThreadWaiting
operator|=
literal|false
expr_stmt|;
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
name|this
operator|.
name|applicationId
operator|=
name|applicationId
expr_stmt|;
name|this
operator|.
name|requestQueue
operator|=
operator|new
name|LinkedBlockingQueue
argument_list|<>
argument_list|()
expr_stmt|;
name|this
operator|.
name|rmProxyRelayer
operator|=
name|rmProxyRelayer
expr_stmt|;
name|resetLastResponseId
argument_list|()
expr_stmt|;
block|}
comment|/**    * Shutdown the thread.    */
DECL|method|shutdown ()
specifier|public
name|void
name|shutdown
parameter_list|()
block|{
name|this
operator|.
name|keepRunning
operator|=
literal|false
expr_stmt|;
name|this
operator|.
name|interrupt
argument_list|()
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
while|while
condition|(
name|keepRunning
condition|)
block|{
name|AsyncAllocateRequestInfo
name|requestInfo
decl_stmt|;
try|try
block|{
name|this
operator|.
name|isThreadWaiting
operator|=
literal|true
expr_stmt|;
name|requestInfo
operator|=
name|this
operator|.
name|requestQueue
operator|.
name|take
argument_list|()
expr_stmt|;
name|this
operator|.
name|isThreadWaiting
operator|=
literal|false
expr_stmt|;
if|if
condition|(
name|requestInfo
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|YarnException
argument_list|(
literal|"Null requestInfo taken from request queue"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|this
operator|.
name|keepRunning
condition|)
block|{
break|break;
block|}
comment|// change the response id before forwarding the allocate request as we
comment|// could have different values for each UAM
name|AllocateRequest
name|request
init|=
name|requestInfo
operator|.
name|getRequest
argument_list|()
decl_stmt|;
if|if
condition|(
name|request
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|YarnException
argument_list|(
literal|"Null allocateRequest from requestInfo"
argument_list|)
throw|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"Sending Heartbeat to RM. AskList:{}"
argument_list|,
operator|(
operator|(
name|request
operator|.
name|getAskList
argument_list|()
operator|==
literal|null
operator|)
condition|?
literal|" empty"
else|:
name|request
operator|.
name|getAskList
argument_list|()
operator|.
name|size
argument_list|()
operator|)
argument_list|)
expr_stmt|;
name|request
operator|.
name|setResponseId
argument_list|(
name|lastResponseId
argument_list|)
expr_stmt|;
name|AllocateResponse
name|response
init|=
name|rmProxyRelayer
operator|.
name|allocate
argument_list|(
name|request
argument_list|)
decl_stmt|;
if|if
condition|(
name|response
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|YarnException
argument_list|(
literal|"Null allocateResponse from allocate"
argument_list|)
throw|;
block|}
name|lastResponseId
operator|=
name|response
operator|.
name|getResponseId
argument_list|()
expr_stmt|;
comment|// update token if RM has reissued/renewed
if|if
condition|(
name|response
operator|.
name|getAMRMToken
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Received new AMRMToken"
argument_list|)
expr_stmt|;
name|YarnServerSecurityUtils
operator|.
name|updateAMRMToken
argument_list|(
name|response
operator|.
name|getAMRMToken
argument_list|()
argument_list|,
name|userUgi
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"Received Heartbeat reply from RM. Allocated Containers:{}"
argument_list|,
operator|(
operator|(
name|response
operator|.
name|getAllocatedContainers
argument_list|()
operator|==
literal|null
operator|)
condition|?
literal|" empty"
else|:
name|response
operator|.
name|getAllocatedContainers
argument_list|()
operator|.
name|size
argument_list|()
operator|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|requestInfo
operator|.
name|getCallback
argument_list|()
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|YarnException
argument_list|(
literal|"Null callback from requestInfo"
argument_list|)
throw|;
block|}
name|requestInfo
operator|.
name|getCallback
argument_list|()
operator|.
name|callback
argument_list|(
name|response
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Interrupted while waiting for queue"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Error occurred while processing heart beat for "
operator|+
name|applicationId
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"AMHeartbeatRequestHandler thread for {} is exiting"
argument_list|,
name|applicationId
argument_list|)
expr_stmt|;
block|}
comment|/**    * Reset the lastResponseId to zero.    */
DECL|method|resetLastResponseId ()
specifier|public
name|void
name|resetLastResponseId
parameter_list|()
block|{
name|this
operator|.
name|lastResponseId
operator|=
literal|0
expr_stmt|;
block|}
comment|/**    * Set the UGI for RM connection.    */
DECL|method|setUGI (UserGroupInformation ugi)
specifier|public
name|void
name|setUGI
parameter_list|(
name|UserGroupInformation
name|ugi
parameter_list|)
block|{
name|this
operator|.
name|userUgi
operator|=
name|ugi
expr_stmt|;
block|}
comment|/**    * Sends the specified heart beat request to the resource manager and invokes    * the callback asynchronously with the response.    *    * @param request the allocate request    * @param callback the callback method for the request    * @throws YarnException if registerAM is not called yet    */
DECL|method|allocateAsync (AllocateRequest request, AsyncCallback<AllocateResponse> callback)
specifier|public
name|void
name|allocateAsync
parameter_list|(
name|AllocateRequest
name|request
parameter_list|,
name|AsyncCallback
argument_list|<
name|AllocateResponse
argument_list|>
name|callback
parameter_list|)
throws|throws
name|YarnException
block|{
try|try
block|{
name|this
operator|.
name|requestQueue
operator|.
name|put
argument_list|(
operator|new
name|AsyncAllocateRequestInfo
argument_list|(
name|request
argument_list|,
name|callback
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ex
parameter_list|)
block|{
comment|// Should not happen as we have MAX_INT queue length
name|LOG
operator|.
name|debug
argument_list|(
literal|"Interrupted while waiting to put on response queue"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|VisibleForTesting
DECL|method|drainHeartbeatThread ()
specifier|public
name|void
name|drainHeartbeatThread
parameter_list|()
block|{
while|while
condition|(
operator|!
name|this
operator|.
name|isThreadWaiting
operator|||
name|this
operator|.
name|requestQueue
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|10
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{       }
block|}
block|}
annotation|@
name|VisibleForTesting
DECL|method|getRequestQueueSize ()
specifier|public
name|int
name|getRequestQueueSize
parameter_list|()
block|{
return|return
name|this
operator|.
name|requestQueue
operator|.
name|size
argument_list|()
return|;
block|}
comment|/**    * Data structure that encapsulates AllocateRequest and AsyncCallback    * instance.    */
DECL|class|AsyncAllocateRequestInfo
specifier|public
specifier|static
class|class
name|AsyncAllocateRequestInfo
block|{
DECL|field|request
specifier|private
name|AllocateRequest
name|request
decl_stmt|;
DECL|field|callback
specifier|private
name|AsyncCallback
argument_list|<
name|AllocateResponse
argument_list|>
name|callback
decl_stmt|;
DECL|method|AsyncAllocateRequestInfo (AllocateRequest request, AsyncCallback<AllocateResponse> callback)
specifier|public
name|AsyncAllocateRequestInfo
parameter_list|(
name|AllocateRequest
name|request
parameter_list|,
name|AsyncCallback
argument_list|<
name|AllocateResponse
argument_list|>
name|callback
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|request
operator|!=
literal|null
argument_list|,
literal|"AllocateRequest cannot be null"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|callback
operator|!=
literal|null
argument_list|,
literal|"Callback cannot be null"
argument_list|)
expr_stmt|;
name|this
operator|.
name|request
operator|=
name|request
expr_stmt|;
name|this
operator|.
name|callback
operator|=
name|callback
expr_stmt|;
block|}
DECL|method|getCallback ()
specifier|public
name|AsyncCallback
argument_list|<
name|AllocateResponse
argument_list|>
name|getCallback
parameter_list|()
block|{
return|return
name|this
operator|.
name|callback
return|;
block|}
DECL|method|getRequest ()
specifier|public
name|AllocateRequest
name|getRequest
parameter_list|()
block|{
return|return
name|this
operator|.
name|request
return|;
block|}
block|}
comment|/**    * Uncaught exception handler for the background heartbeat thread.    */
DECL|class|HeartBeatThreadUncaughtExceptionHandler
specifier|public
class|class
name|HeartBeatThreadUncaughtExceptionHandler
implements|implements
name|UncaughtExceptionHandler
block|{
annotation|@
name|Override
DECL|method|uncaughtException (Thread t, Throwable e)
specifier|public
name|void
name|uncaughtException
parameter_list|(
name|Thread
name|t
parameter_list|,
name|Throwable
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Heartbeat thread {} for application {} crashed!"
argument_list|,
name|t
operator|.
name|getName
argument_list|()
argument_list|,
name|applicationId
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

