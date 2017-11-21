begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode.ha
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|namenode
operator|.
name|ha
package|;
end_package

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
name|lang
operator|.
name|reflect
operator|.
name|Proxy
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
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
name|CompletionService
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
name|ExecutorCompletionService
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
name|Executors
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
name|ExecutionException
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
name|ipc
operator|.
name|RemoteException
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
name|ipc
operator|.
name|StandbyException
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
name|MultiException
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
comment|/**  * A FailoverProxyProvider implementation that technically does not "failover"  * per-se. It constructs a wrapper proxy that sends the request to ALL  * underlying proxies simultaneously. It assumes the in an HA setup, there will  * be only one Active, and the active should respond faster than any configured  * standbys. Once it receive a response from any one of the configred proxies,  * outstanding requests to other proxies are immediately cancelled.  */
end_comment

begin_class
DECL|class|RequestHedgingProxyProvider
specifier|public
class|class
name|RequestHedgingProxyProvider
parameter_list|<
name|T
parameter_list|>
extends|extends
name|ConfiguredFailoverProxyProvider
argument_list|<
name|T
argument_list|>
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
name|RequestHedgingProxyProvider
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|class|RequestHedgingInvocationHandler
class|class
name|RequestHedgingInvocationHandler
implements|implements
name|InvocationHandler
block|{
DECL|field|targetProxies
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|ProxyInfo
argument_list|<
name|T
argument_list|>
argument_list|>
name|targetProxies
decl_stmt|;
DECL|method|RequestHedgingInvocationHandler ( Map<String, ProxyInfo<T>> targetProxies)
specifier|public
name|RequestHedgingInvocationHandler
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|ProxyInfo
argument_list|<
name|T
argument_list|>
argument_list|>
name|targetProxies
parameter_list|)
block|{
name|this
operator|.
name|targetProxies
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
name|targetProxies
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates a Executor and invokes all proxies concurrently. This      * implementation assumes that Clients have configured proper socket      * timeouts, else the call can block forever.      *      * @param proxy      * @param method      * @param args      * @return      * @throws Throwable      */
annotation|@
name|Override
specifier|public
name|Object
DECL|method|invoke (Object proxy, final Method method, final Object[] args)
name|invoke
parameter_list|(
name|Object
name|proxy
parameter_list|,
specifier|final
name|Method
name|method
parameter_list|,
specifier|final
name|Object
index|[]
name|args
parameter_list|)
throws|throws
name|Throwable
block|{
name|Map
argument_list|<
name|Future
argument_list|<
name|Object
argument_list|>
argument_list|,
name|ProxyInfo
argument_list|<
name|T
argument_list|>
argument_list|>
name|proxyMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|numAttempts
init|=
literal|0
decl_stmt|;
name|ExecutorService
name|executor
init|=
literal|null
decl_stmt|;
name|CompletionService
argument_list|<
name|Object
argument_list|>
name|completionService
decl_stmt|;
try|try
block|{
comment|// Optimization : if only 2 proxies are configured and one had failed
comment|// over, then we dont need to create a threadpool etc.
name|targetProxies
operator|.
name|remove
argument_list|(
name|toIgnore
argument_list|)
expr_stmt|;
if|if
condition|(
name|targetProxies
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
name|ProxyInfo
argument_list|<
name|T
argument_list|>
name|proxyInfo
init|=
name|targetProxies
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
try|try
block|{
name|currentUsedProxy
operator|=
name|proxyInfo
expr_stmt|;
name|Object
name|retVal
init|=
name|method
operator|.
name|invoke
argument_list|(
name|proxyInfo
operator|.
name|proxy
argument_list|,
name|args
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Invocation successful on [{}]"
argument_list|,
name|currentUsedProxy
operator|.
name|proxyInfo
argument_list|)
expr_stmt|;
return|return
name|retVal
return|;
block|}
catch|catch
parameter_list|(
name|InvocationTargetException
name|ex
parameter_list|)
block|{
name|Exception
name|unwrappedException
init|=
name|unwrapInvocationTargetException
argument_list|(
name|ex
argument_list|)
decl_stmt|;
name|logProxyException
argument_list|(
name|unwrappedException
argument_list|,
name|currentUsedProxy
operator|.
name|proxyInfo
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|trace
argument_list|(
literal|"Unsuccessful invocation on [{}]"
argument_list|,
name|currentUsedProxy
operator|.
name|proxyInfo
argument_list|)
expr_stmt|;
throw|throw
name|unwrappedException
throw|;
block|}
block|}
name|executor
operator|=
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
name|proxies
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|completionService
operator|=
operator|new
name|ExecutorCompletionService
argument_list|<>
argument_list|(
name|executor
argument_list|)
expr_stmt|;
for|for
control|(
specifier|final
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|ProxyInfo
argument_list|<
name|T
argument_list|>
argument_list|>
name|pEntry
range|:
name|targetProxies
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Callable
argument_list|<
name|Object
argument_list|>
name|c
init|=
operator|new
name|Callable
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Object
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"Invoking method {} on proxy {}"
argument_list|,
name|method
argument_list|,
name|pEntry
operator|.
name|getValue
argument_list|()
operator|.
name|proxyInfo
argument_list|)
expr_stmt|;
return|return
name|method
operator|.
name|invoke
argument_list|(
name|pEntry
operator|.
name|getValue
argument_list|()
operator|.
name|proxy
argument_list|,
name|args
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|proxyMap
operator|.
name|put
argument_list|(
name|completionService
operator|.
name|submit
argument_list|(
name|c
argument_list|)
argument_list|,
name|pEntry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|numAttempts
operator|++
expr_stmt|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|Exception
argument_list|>
name|badResults
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
while|while
condition|(
name|numAttempts
operator|>
literal|0
condition|)
block|{
name|Future
argument_list|<
name|Object
argument_list|>
name|callResultFuture
init|=
name|completionService
operator|.
name|take
argument_list|()
decl_stmt|;
name|Object
name|retVal
decl_stmt|;
try|try
block|{
name|currentUsedProxy
operator|=
name|proxyMap
operator|.
name|get
argument_list|(
name|callResultFuture
argument_list|)
expr_stmt|;
name|retVal
operator|=
name|callResultFuture
operator|.
name|get
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Invocation successful on [{}]"
argument_list|,
name|currentUsedProxy
operator|.
name|proxyInfo
argument_list|)
expr_stmt|;
return|return
name|retVal
return|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|ex
parameter_list|)
block|{
name|Exception
name|unwrappedException
init|=
name|unwrapExecutionException
argument_list|(
name|ex
argument_list|)
decl_stmt|;
name|ProxyInfo
argument_list|<
name|T
argument_list|>
name|tProxyInfo
init|=
name|proxyMap
operator|.
name|get
argument_list|(
name|callResultFuture
argument_list|)
decl_stmt|;
name|logProxyException
argument_list|(
name|unwrappedException
argument_list|,
name|tProxyInfo
operator|.
name|proxyInfo
argument_list|)
expr_stmt|;
name|badResults
operator|.
name|put
argument_list|(
name|tProxyInfo
operator|.
name|proxyInfo
argument_list|,
name|unwrappedException
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|trace
argument_list|(
literal|"Unsuccessful invocation on [{}]"
argument_list|,
name|tProxyInfo
operator|.
name|proxyInfo
argument_list|)
expr_stmt|;
name|numAttempts
operator|--
expr_stmt|;
block|}
block|}
comment|// At this point we should have All bad results (Exceptions)
comment|// Or should have returned with successful result.
if|if
condition|(
name|badResults
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
throw|throw
name|badResults
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
throw|;
block|}
else|else
block|{
throw|throw
operator|new
name|MultiException
argument_list|(
name|badResults
argument_list|)
throw|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|executor
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"Shutting down threadpool executor"
argument_list|)
expr_stmt|;
name|executor
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|field|currentUsedProxy
specifier|private
specifier|volatile
name|ProxyInfo
argument_list|<
name|T
argument_list|>
name|currentUsedProxy
init|=
literal|null
decl_stmt|;
DECL|field|toIgnore
specifier|private
specifier|volatile
name|String
name|toIgnore
init|=
literal|null
decl_stmt|;
DECL|method|RequestHedgingProxyProvider (Configuration conf, URI uri, Class<T> xface, HAProxyFactory<T> proxyFactory)
specifier|public
name|RequestHedgingProxyProvider
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|URI
name|uri
parameter_list|,
name|Class
argument_list|<
name|T
argument_list|>
name|xface
parameter_list|,
name|HAProxyFactory
argument_list|<
name|T
argument_list|>
name|proxyFactory
parameter_list|)
block|{
name|super
argument_list|(
name|conf
argument_list|,
name|uri
argument_list|,
name|xface
argument_list|,
name|proxyFactory
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
DECL|method|getProxy ()
specifier|public
specifier|synchronized
name|ProxyInfo
argument_list|<
name|T
argument_list|>
name|getProxy
parameter_list|()
block|{
if|if
condition|(
name|currentUsedProxy
operator|!=
literal|null
condition|)
block|{
return|return
name|currentUsedProxy
return|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|ProxyInfo
argument_list|<
name|T
argument_list|>
argument_list|>
name|targetProxyInfos
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|StringBuilder
name|combinedInfo
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"["
argument_list|)
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
name|proxies
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|ProxyInfo
argument_list|<
name|T
argument_list|>
name|pInfo
init|=
name|super
operator|.
name|getProxy
argument_list|()
decl_stmt|;
name|incrementProxyIndex
argument_list|()
expr_stmt|;
name|targetProxyInfos
operator|.
name|put
argument_list|(
name|pInfo
operator|.
name|proxyInfo
argument_list|,
name|pInfo
argument_list|)
expr_stmt|;
name|combinedInfo
operator|.
name|append
argument_list|(
name|pInfo
operator|.
name|proxyInfo
argument_list|)
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
block|}
name|combinedInfo
operator|.
name|append
argument_list|(
literal|']'
argument_list|)
expr_stmt|;
name|T
name|wrappedProxy
init|=
operator|(
name|T
operator|)
name|Proxy
operator|.
name|newProxyInstance
argument_list|(
name|RequestHedgingInvocationHandler
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
argument_list|,
operator|new
name|Class
argument_list|<
name|?
argument_list|>
index|[]
block|{
name|xface
block|}
operator|,
operator|new
name|RequestHedgingInvocationHandler
argument_list|(
name|targetProxyInfos
argument_list|)
block|)
function|;
return|return
operator|new
name|ProxyInfo
argument_list|<
name|T
argument_list|>
argument_list|(
name|wrappedProxy
argument_list|,
name|combinedInfo
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
end_class

begin_function
annotation|@
name|Override
DECL|method|performFailover (T currentProxy)
specifier|public
specifier|synchronized
name|void
name|performFailover
parameter_list|(
name|T
name|currentProxy
parameter_list|)
block|{
name|toIgnore
operator|=
name|this
operator|.
name|currentUsedProxy
operator|.
name|proxyInfo
expr_stmt|;
name|this
operator|.
name|currentUsedProxy
operator|=
literal|null
expr_stmt|;
block|}
end_function

begin_comment
comment|/**    * Check the exception returned by the proxy log a warning message if it's    * not a StandbyException (expected exception).    * @param ex Exception to evaluate.    * @param proxyInfo Information of the proxy reporting the exception.    */
end_comment

begin_function
DECL|method|logProxyException (Exception ex, String proxyInfo)
specifier|private
name|void
name|logProxyException
parameter_list|(
name|Exception
name|ex
parameter_list|,
name|String
name|proxyInfo
parameter_list|)
block|{
if|if
condition|(
name|isStandbyException
argument_list|(
name|ex
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Invocation returned standby exception on [{}]"
argument_list|,
name|proxyInfo
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Invocation returned exception on [{}]"
argument_list|,
name|proxyInfo
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
end_function

begin_comment
comment|/**    * Check if the returned exception is caused by an standby namenode.    * @param exception Exception to check.    * @return If the exception is caused by an standby namenode.    */
end_comment

begin_function
DECL|method|isStandbyException (Exception exception)
specifier|private
name|boolean
name|isStandbyException
parameter_list|(
name|Exception
name|exception
parameter_list|)
block|{
if|if
condition|(
name|exception
operator|instanceof
name|RemoteException
condition|)
block|{
return|return
operator|(
operator|(
name|RemoteException
operator|)
name|exception
operator|)
operator|.
name|unwrapRemoteException
argument_list|()
operator|instanceof
name|StandbyException
return|;
block|}
return|return
literal|false
return|;
block|}
end_function

begin_comment
comment|/**    * Unwraps the ExecutionException.<p>    * Example:    *<blockquote><pre>    * if ex is    * ExecutionException(InvocationTargetException(SomeException))    * returns SomeException    *</pre></blockquote>    *    * @return unwrapped exception    */
end_comment

begin_function
DECL|method|unwrapExecutionException (ExecutionException ex)
specifier|private
name|Exception
name|unwrapExecutionException
parameter_list|(
name|ExecutionException
name|ex
parameter_list|)
block|{
if|if
condition|(
name|ex
operator|!=
literal|null
condition|)
block|{
name|Throwable
name|cause
init|=
name|ex
operator|.
name|getCause
argument_list|()
decl_stmt|;
if|if
condition|(
name|cause
operator|instanceof
name|InvocationTargetException
condition|)
block|{
return|return
name|unwrapInvocationTargetException
argument_list|(
operator|(
name|InvocationTargetException
operator|)
name|cause
argument_list|)
return|;
block|}
block|}
return|return
name|ex
return|;
block|}
end_function

begin_comment
comment|/**    * Unwraps the InvocationTargetException.<p>    * Example:    *<blockquote><pre>    * if ex is InvocationTargetException(SomeException)    * returns SomeException    *</pre></blockquote>    *    * @return unwrapped exception    */
end_comment

begin_function
DECL|method|unwrapInvocationTargetException ( InvocationTargetException ex)
specifier|private
name|Exception
name|unwrapInvocationTargetException
parameter_list|(
name|InvocationTargetException
name|ex
parameter_list|)
block|{
if|if
condition|(
name|ex
operator|!=
literal|null
condition|)
block|{
name|Throwable
name|cause
init|=
name|ex
operator|.
name|getCause
argument_list|()
decl_stmt|;
if|if
condition|(
name|cause
operator|instanceof
name|Exception
condition|)
block|{
return|return
operator|(
name|Exception
operator|)
name|cause
return|;
block|}
block|}
return|return
name|ex
return|;
block|}
end_function

unit|}
end_unit

