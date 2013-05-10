begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
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
name|lang
operator|.
name|ref
operator|.
name|WeakReference
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
name|DelayQueue
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
name|Delayed
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
name|security
operator|.
name|token
operator|.
name|Token
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
name|token
operator|.
name|TokenIdentifier
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
name|Time
import|;
end_import

begin_comment
comment|/**  * A daemon thread that waits for the next file system to renew.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|DelegationTokenRenewer
specifier|public
class|class
name|DelegationTokenRenewer
extends|extends
name|Thread
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
name|DelegationTokenRenewer
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/** The renewable interface used by the renewer. */
DECL|interface|Renewable
specifier|public
interface|interface
name|Renewable
block|{
comment|/** @return the renew token. */
DECL|method|getRenewToken ()
specifier|public
name|Token
argument_list|<
name|?
argument_list|>
name|getRenewToken
parameter_list|()
function_decl|;
comment|/** Set delegation token. */
DECL|method|setDelegationToken (Token<T> token)
specifier|public
parameter_list|<
name|T
extends|extends
name|TokenIdentifier
parameter_list|>
name|void
name|setDelegationToken
parameter_list|(
name|Token
argument_list|<
name|T
argument_list|>
name|token
parameter_list|)
function_decl|;
block|}
comment|/**    * An action that will renew and replace the file system's delegation     * tokens automatically.    */
DECL|class|RenewAction
specifier|private
specifier|static
class|class
name|RenewAction
parameter_list|<
name|T
extends|extends
name|FileSystem
operator|&
name|Renewable
parameter_list|>
implements|implements
name|Delayed
block|{
comment|/** when should the renew happen */
DECL|field|renewalTime
specifier|private
name|long
name|renewalTime
decl_stmt|;
comment|/** a weak reference to the file system so that it can be garbage collected */
DECL|field|weakFs
specifier|private
specifier|final
name|WeakReference
argument_list|<
name|T
argument_list|>
name|weakFs
decl_stmt|;
DECL|field|token
specifier|private
name|Token
argument_list|<
name|?
argument_list|>
name|token
decl_stmt|;
DECL|method|RenewAction (final T fs)
specifier|private
name|RenewAction
parameter_list|(
specifier|final
name|T
name|fs
parameter_list|)
block|{
name|this
operator|.
name|weakFs
operator|=
operator|new
name|WeakReference
argument_list|<
name|T
argument_list|>
argument_list|(
name|fs
argument_list|)
expr_stmt|;
name|this
operator|.
name|token
operator|=
name|fs
operator|.
name|getRenewToken
argument_list|()
expr_stmt|;
name|updateRenewalTime
argument_list|(
name|renewCycle
argument_list|)
expr_stmt|;
block|}
comment|/** Get the delay until this event should happen. */
annotation|@
name|Override
DECL|method|getDelay (final TimeUnit unit)
specifier|public
name|long
name|getDelay
parameter_list|(
specifier|final
name|TimeUnit
name|unit
parameter_list|)
block|{
specifier|final
name|long
name|millisLeft
init|=
name|renewalTime
operator|-
name|Time
operator|.
name|now
argument_list|()
decl_stmt|;
return|return
name|unit
operator|.
name|convert
argument_list|(
name|millisLeft
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|compareTo (final Delayed delayed)
specifier|public
name|int
name|compareTo
parameter_list|(
specifier|final
name|Delayed
name|delayed
parameter_list|)
block|{
specifier|final
name|RenewAction
argument_list|<
name|?
argument_list|>
name|that
init|=
operator|(
name|RenewAction
argument_list|<
name|?
argument_list|>
operator|)
name|delayed
decl_stmt|;
return|return
name|this
operator|.
name|renewalTime
operator|<
name|that
operator|.
name|renewalTime
condition|?
operator|-
literal|1
else|:
name|this
operator|.
name|renewalTime
operator|==
name|that
operator|.
name|renewalTime
condition|?
literal|0
else|:
literal|1
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
name|token
operator|.
name|hashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|equals (final Object that)
specifier|public
name|boolean
name|equals
parameter_list|(
specifier|final
name|Object
name|that
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|that
condition|)
block|{
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|that
operator|==
literal|null
operator|||
operator|!
operator|(
name|that
operator|instanceof
name|RenewAction
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|token
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|RenewAction
argument_list|<
name|?
argument_list|>
operator|)
name|that
operator|)
operator|.
name|token
argument_list|)
return|;
block|}
comment|/**      * Set a new time for the renewal.      * It can only be called when the action is not in the queue or any      * collection because the hashCode may change      * @param newTime the new time      */
DECL|method|updateRenewalTime (long delay)
specifier|private
name|void
name|updateRenewalTime
parameter_list|(
name|long
name|delay
parameter_list|)
block|{
name|renewalTime
operator|=
name|Time
operator|.
name|now
argument_list|()
operator|+
name|delay
operator|-
name|delay
operator|/
literal|10
expr_stmt|;
block|}
comment|/**      * Renew or replace the delegation token for this file system.      * It can only be called when the action is not in the queue.      * @return      * @throws IOException      */
DECL|method|renew ()
specifier|private
name|boolean
name|renew
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
specifier|final
name|T
name|fs
init|=
name|weakFs
operator|.
name|get
argument_list|()
decl_stmt|;
specifier|final
name|boolean
name|b
init|=
name|fs
operator|!=
literal|null
decl_stmt|;
if|if
condition|(
name|b
condition|)
block|{
synchronized|synchronized
init|(
name|fs
init|)
block|{
try|try
block|{
name|long
name|expires
init|=
name|token
operator|.
name|renew
argument_list|(
name|fs
operator|.
name|getConf
argument_list|()
argument_list|)
decl_stmt|;
name|updateRenewalTime
argument_list|(
name|expires
operator|-
name|Time
operator|.
name|now
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ie
parameter_list|)
block|{
try|try
block|{
name|Token
argument_list|<
name|?
argument_list|>
index|[]
name|tokens
init|=
name|fs
operator|.
name|addDelegationTokens
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|tokens
operator|.
name|length
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"addDelegationTokens returned no tokens"
argument_list|)
throw|;
block|}
name|token
operator|=
name|tokens
index|[
literal|0
index|]
expr_stmt|;
name|updateRenewalTime
argument_list|(
name|renewCycle
argument_list|)
expr_stmt|;
name|fs
operator|.
name|setDelegationToken
argument_list|(
name|token
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ie2
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Can't renew or get new delegation token "
argument_list|,
name|ie
argument_list|)
throw|;
block|}
block|}
block|}
block|}
return|return
name|b
return|;
block|}
DECL|method|cancel ()
specifier|private
name|void
name|cancel
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
specifier|final
name|T
name|fs
init|=
name|weakFs
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|fs
operator|!=
literal|null
condition|)
block|{
name|token
operator|.
name|cancel
argument_list|(
name|fs
operator|.
name|getConf
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|Renewable
name|fs
init|=
name|weakFs
operator|.
name|get
argument_list|()
decl_stmt|;
return|return
name|fs
operator|==
literal|null
condition|?
literal|"evaporated token renew"
else|:
literal|"The token will be renewed in "
operator|+
name|getDelay
argument_list|(
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
operator|+
literal|" secs, renewToken="
operator|+
name|token
return|;
block|}
block|}
comment|/** assumes renew cycle for a token is 24 hours... */
DECL|field|RENEW_CYCLE
specifier|private
specifier|static
specifier|final
name|long
name|RENEW_CYCLE
init|=
literal|24
operator|*
literal|60
operator|*
literal|60
operator|*
literal|1000
decl_stmt|;
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|field|renewCycle
specifier|protected
specifier|static
name|long
name|renewCycle
init|=
name|RENEW_CYCLE
decl_stmt|;
comment|/** Queue to maintain the RenewActions to be processed by the {@link #run()} */
DECL|field|queue
specifier|private
specifier|volatile
name|DelayQueue
argument_list|<
name|RenewAction
argument_list|<
name|?
argument_list|>
argument_list|>
name|queue
init|=
operator|new
name|DelayQueue
argument_list|<
name|RenewAction
argument_list|<
name|?
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
comment|/** For testing purposes */
annotation|@
name|VisibleForTesting
DECL|method|getRenewQueueLength ()
specifier|protected
name|int
name|getRenewQueueLength
parameter_list|()
block|{
return|return
name|queue
operator|.
name|size
argument_list|()
return|;
block|}
comment|/**    * Create the singleton instance. However, the thread can be started lazily in    * {@link #addRenewAction(FileSystem)}    */
DECL|field|INSTANCE
specifier|private
specifier|static
name|DelegationTokenRenewer
name|INSTANCE
init|=
literal|null
decl_stmt|;
DECL|method|DelegationTokenRenewer (final Class<? extends FileSystem> clazz)
specifier|private
name|DelegationTokenRenewer
parameter_list|(
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|FileSystem
argument_list|>
name|clazz
parameter_list|)
block|{
name|super
argument_list|(
name|clazz
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"-"
operator|+
name|DelegationTokenRenewer
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|getInstance ()
specifier|public
specifier|static
specifier|synchronized
name|DelegationTokenRenewer
name|getInstance
parameter_list|()
block|{
if|if
condition|(
name|INSTANCE
operator|==
literal|null
condition|)
block|{
name|INSTANCE
operator|=
operator|new
name|DelegationTokenRenewer
argument_list|(
name|FileSystem
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
return|return
name|INSTANCE
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|reset ()
specifier|static
specifier|synchronized
name|void
name|reset
parameter_list|()
block|{
if|if
condition|(
name|INSTANCE
operator|!=
literal|null
condition|)
block|{
name|INSTANCE
operator|.
name|queue
operator|.
name|clear
argument_list|()
expr_stmt|;
name|INSTANCE
operator|.
name|interrupt
argument_list|()
expr_stmt|;
try|try
block|{
name|INSTANCE
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to reset renewer"
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|INSTANCE
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
comment|/** Add a renew action to the queue. */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"static-access"
argument_list|)
DECL|method|addRenewAction (final T fs)
specifier|public
parameter_list|<
name|T
extends|extends
name|FileSystem
operator|&
name|Renewable
parameter_list|>
name|void
name|addRenewAction
parameter_list|(
specifier|final
name|T
name|fs
parameter_list|)
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
operator|!
name|isAlive
argument_list|()
condition|)
block|{
name|start
argument_list|()
expr_stmt|;
block|}
block|}
name|RenewAction
argument_list|<
name|T
argument_list|>
name|action
init|=
operator|new
name|RenewAction
argument_list|<
name|T
argument_list|>
argument_list|(
name|fs
argument_list|)
decl_stmt|;
if|if
condition|(
name|action
operator|.
name|token
operator|!=
literal|null
condition|)
block|{
name|queue
operator|.
name|add
argument_list|(
name|action
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|fs
operator|.
name|LOG
operator|.
name|error
argument_list|(
literal|"does not have a token for renewal"
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Remove the associated renew action from the queue    *     * @throws IOException    */
DECL|method|removeRenewAction ( final T fs)
specifier|public
parameter_list|<
name|T
extends|extends
name|FileSystem
operator|&
name|Renewable
parameter_list|>
name|void
name|removeRenewAction
parameter_list|(
specifier|final
name|T
name|fs
parameter_list|)
throws|throws
name|IOException
block|{
name|RenewAction
argument_list|<
name|T
argument_list|>
name|action
init|=
operator|new
name|RenewAction
argument_list|<
name|T
argument_list|>
argument_list|(
name|fs
argument_list|)
decl_stmt|;
if|if
condition|(
name|queue
operator|.
name|remove
argument_list|(
name|action
argument_list|)
condition|)
block|{
try|try
block|{
name|action
operator|.
name|cancel
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Interrupted while canceling token for "
operator|+
name|fs
operator|.
name|getUri
argument_list|()
operator|+
literal|"filesystem"
argument_list|)
expr_stmt|;
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
name|ie
operator|.
name|getStackTrace
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"static-access"
argument_list|)
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
for|for
control|(
init|;
condition|;
control|)
block|{
name|RenewAction
argument_list|<
name|?
argument_list|>
name|action
init|=
literal|null
decl_stmt|;
try|try
block|{
name|action
operator|=
name|queue
operator|.
name|take
argument_list|()
expr_stmt|;
if|if
condition|(
name|action
operator|.
name|renew
argument_list|()
condition|)
block|{
name|queue
operator|.
name|add
argument_list|(
name|action
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
return|return;
block|}
catch|catch
parameter_list|(
name|Exception
name|ie
parameter_list|)
block|{
name|action
operator|.
name|weakFs
operator|.
name|get
argument_list|()
operator|.
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to renew token, action="
operator|+
name|action
argument_list|,
name|ie
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

