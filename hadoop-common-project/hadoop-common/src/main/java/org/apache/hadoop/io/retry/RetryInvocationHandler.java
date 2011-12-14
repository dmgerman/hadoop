begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.io.retry
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|retry
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
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
name|reflect
operator|.
name|InvocationHandler
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|InvocationTargetException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Method
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
name|Map
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
name|io
operator|.
name|retry
operator|.
name|RetryPolicy
operator|.
name|RetryAction
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
name|ThreadUtil
import|;
end_import

begin_class
DECL|class|RetryInvocationHandler
class|class
name|RetryInvocationHandler
implements|implements
name|InvocationHandler
implements|,
name|Closeable
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|RetryInvocationHandler
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|proxyProvider
specifier|private
name|FailoverProxyProvider
name|proxyProvider
decl_stmt|;
comment|/**    * The number of times the associated proxyProvider has ever been failed over.    */
DECL|field|proxyProviderFailoverCount
specifier|private
name|long
name|proxyProviderFailoverCount
init|=
literal|0
decl_stmt|;
DECL|field|defaultPolicy
specifier|private
name|RetryPolicy
name|defaultPolicy
decl_stmt|;
DECL|field|methodNameToPolicyMap
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|RetryPolicy
argument_list|>
name|methodNameToPolicyMap
decl_stmt|;
DECL|field|currentProxy
specifier|private
name|Object
name|currentProxy
decl_stmt|;
DECL|method|RetryInvocationHandler (FailoverProxyProvider proxyProvider, RetryPolicy retryPolicy)
specifier|public
name|RetryInvocationHandler
parameter_list|(
name|FailoverProxyProvider
name|proxyProvider
parameter_list|,
name|RetryPolicy
name|retryPolicy
parameter_list|)
block|{
name|this
operator|.
name|proxyProvider
operator|=
name|proxyProvider
expr_stmt|;
name|this
operator|.
name|defaultPolicy
operator|=
name|retryPolicy
expr_stmt|;
name|this
operator|.
name|methodNameToPolicyMap
operator|=
name|Collections
operator|.
name|emptyMap
argument_list|()
expr_stmt|;
name|this
operator|.
name|currentProxy
operator|=
name|proxyProvider
operator|.
name|getProxy
argument_list|()
expr_stmt|;
block|}
DECL|method|RetryInvocationHandler (FailoverProxyProvider proxyProvider, Map<String, RetryPolicy> methodNameToPolicyMap)
specifier|public
name|RetryInvocationHandler
parameter_list|(
name|FailoverProxyProvider
name|proxyProvider
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|RetryPolicy
argument_list|>
name|methodNameToPolicyMap
parameter_list|)
block|{
name|this
operator|.
name|proxyProvider
operator|=
name|proxyProvider
expr_stmt|;
name|this
operator|.
name|defaultPolicy
operator|=
name|RetryPolicies
operator|.
name|TRY_ONCE_THEN_FAIL
expr_stmt|;
name|this
operator|.
name|methodNameToPolicyMap
operator|=
name|methodNameToPolicyMap
expr_stmt|;
name|this
operator|.
name|currentProxy
operator|=
name|proxyProvider
operator|.
name|getProxy
argument_list|()
expr_stmt|;
block|}
DECL|method|invoke (Object proxy, Method method, Object[] args)
specifier|public
name|Object
name|invoke
parameter_list|(
name|Object
name|proxy
parameter_list|,
name|Method
name|method
parameter_list|,
name|Object
index|[]
name|args
parameter_list|)
throws|throws
name|Throwable
block|{
name|RetryPolicy
name|policy
init|=
name|methodNameToPolicyMap
operator|.
name|get
argument_list|(
name|method
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|policy
operator|==
literal|null
condition|)
block|{
name|policy
operator|=
name|defaultPolicy
expr_stmt|;
block|}
comment|// The number of times this method invocation has been failed over.
name|int
name|invocationFailoverCount
init|=
literal|0
decl_stmt|;
name|int
name|retries
init|=
literal|0
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
comment|// The number of times this invocation handler has ever been failed over,
comment|// before this method invocation attempt. Used to prevent concurrent
comment|// failed method invocations from triggering multiple failover attempts.
name|long
name|invocationAttemptFailoverCount
decl_stmt|;
synchronized|synchronized
init|(
name|proxyProvider
init|)
block|{
name|invocationAttemptFailoverCount
operator|=
name|proxyProviderFailoverCount
expr_stmt|;
block|}
try|try
block|{
return|return
name|invokeMethod
argument_list|(
name|method
argument_list|,
name|args
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|boolean
name|isMethodIdempotent
init|=
name|proxyProvider
operator|.
name|getInterface
argument_list|()
operator|.
name|getMethod
argument_list|(
name|method
operator|.
name|getName
argument_list|()
argument_list|,
name|method
operator|.
name|getParameterTypes
argument_list|()
argument_list|)
operator|.
name|isAnnotationPresent
argument_list|(
name|Idempotent
operator|.
name|class
argument_list|)
decl_stmt|;
name|RetryAction
name|action
init|=
name|policy
operator|.
name|shouldRetry
argument_list|(
name|e
argument_list|,
name|retries
operator|++
argument_list|,
name|invocationFailoverCount
argument_list|,
name|isMethodIdempotent
argument_list|)
decl_stmt|;
if|if
condition|(
name|action
operator|.
name|action
operator|==
name|RetryAction
operator|.
name|RetryDecision
operator|.
name|FAIL
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Exception while invoking "
operator|+
name|method
operator|.
name|getName
argument_list|()
operator|+
literal|" of "
operator|+
name|currentProxy
operator|.
name|getClass
argument_list|()
operator|+
literal|". Not retrying."
argument_list|,
name|e
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|method
operator|.
name|getReturnType
argument_list|()
operator|.
name|equals
argument_list|(
name|Void
operator|.
name|TYPE
argument_list|)
condition|)
block|{
throw|throw
name|e
throw|;
comment|// non-void methods can't fail without an exception
block|}
return|return
literal|null
return|;
block|}
else|else
block|{
comment|// retry or failover
if|if
condition|(
name|action
operator|.
name|action
operator|==
name|RetryAction
operator|.
name|RetryDecision
operator|.
name|FAILOVER_AND_RETRY
condition|)
block|{
name|String
name|msg
init|=
literal|"Exception while invoking "
operator|+
name|method
operator|.
name|getName
argument_list|()
operator|+
literal|" of "
operator|+
name|currentProxy
operator|.
name|getClass
argument_list|()
operator|+
literal|" after "
operator|+
name|invocationFailoverCount
operator|+
literal|" fail over attempts."
operator|+
literal|" Trying to fail over "
operator|+
name|formatSleepMessage
argument_list|(
name|action
operator|.
name|delayMillis
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
name|msg
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
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
literal|"Exception while invoking "
operator|+
name|method
operator|.
name|getName
argument_list|()
operator|+
literal|" of "
operator|+
name|currentProxy
operator|.
name|getClass
argument_list|()
operator|+
literal|". Retrying "
operator|+
name|formatSleepMessage
argument_list|(
name|action
operator|.
name|delayMillis
argument_list|)
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|action
operator|.
name|delayMillis
operator|>
literal|0
condition|)
block|{
name|ThreadUtil
operator|.
name|sleepAtLeastIgnoreInterrupts
argument_list|(
name|action
operator|.
name|delayMillis
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|action
operator|.
name|action
operator|==
name|RetryAction
operator|.
name|RetryDecision
operator|.
name|FAILOVER_AND_RETRY
condition|)
block|{
comment|// Make sure that concurrent failed method invocations only cause a
comment|// single actual fail over.
synchronized|synchronized
init|(
name|proxyProvider
init|)
block|{
if|if
condition|(
name|invocationAttemptFailoverCount
operator|==
name|proxyProviderFailoverCount
condition|)
block|{
name|proxyProvider
operator|.
name|performFailover
argument_list|(
name|currentProxy
argument_list|)
expr_stmt|;
name|proxyProviderFailoverCount
operator|++
expr_stmt|;
name|currentProxy
operator|=
name|proxyProvider
operator|.
name|getProxy
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"A failover has occurred since the start of this method"
operator|+
literal|" invocation attempt."
argument_list|)
expr_stmt|;
block|}
block|}
name|invocationFailoverCount
operator|++
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
DECL|method|formatSleepMessage (long millis)
specifier|private
specifier|static
name|String
name|formatSleepMessage
parameter_list|(
name|long
name|millis
parameter_list|)
block|{
if|if
condition|(
name|millis
operator|>
literal|0
condition|)
block|{
return|return
literal|"after sleeping for "
operator|+
name|millis
operator|+
literal|"ms."
return|;
block|}
else|else
block|{
return|return
literal|"immediately."
return|;
block|}
block|}
DECL|method|invokeMethod (Method method, Object[] args)
specifier|private
name|Object
name|invokeMethod
parameter_list|(
name|Method
name|method
parameter_list|,
name|Object
index|[]
name|args
parameter_list|)
throws|throws
name|Throwable
block|{
try|try
block|{
if|if
condition|(
operator|!
name|method
operator|.
name|isAccessible
argument_list|()
condition|)
block|{
name|method
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
return|return
name|method
operator|.
name|invoke
argument_list|(
name|currentProxy
argument_list|,
name|args
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|InvocationTargetException
name|e
parameter_list|)
block|{
throw|throw
name|e
operator|.
name|getCause
argument_list|()
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|proxyProvider
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

