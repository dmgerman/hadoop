begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  *  or more contributor license agreements.  See the NOTICE file  *  distributed with this work for additional information  *  regarding copyright ownership.  The ASF licenses this file  *  to you under the Apache License, Version 2.0 (the  *  "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *       http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.service.launcher
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|service
operator|.
name|launcher
package|;
end_package

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
name|service
operator|.
name|Service
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
name|ShutdownHookManager
import|;
end_import

begin_comment
comment|/**  * JVM Shutdown hook for Service which will stop the  * Service gracefully in case of JVM shutdown.  * This hook uses a weak reference to the service,  * and when shut down, calls {@link Service#stop()} if the reference is valid.  */
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
DECL|class|ServiceShutdownHook
specifier|public
class|class
name|ServiceShutdownHook
implements|implements
name|Runnable
block|{
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
name|ServiceShutdownHook
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * A weak reference to the service.    */
DECL|field|serviceRef
specifier|private
specifier|final
name|WeakReference
argument_list|<
name|Service
argument_list|>
name|serviceRef
decl_stmt|;
comment|/**    * Create an instance.    * @param service the service    */
DECL|method|ServiceShutdownHook (Service service)
specifier|public
name|ServiceShutdownHook
parameter_list|(
name|Service
name|service
parameter_list|)
block|{
name|serviceRef
operator|=
operator|new
name|WeakReference
argument_list|<>
argument_list|(
name|service
argument_list|)
expr_stmt|;
block|}
comment|/**    * Register the service for shutdown with Hadoop's    * {@link ShutdownHookManager}.    * @param priority shutdown hook priority    */
DECL|method|register (int priority)
specifier|public
specifier|synchronized
name|void
name|register
parameter_list|(
name|int
name|priority
parameter_list|)
block|{
name|unregister
argument_list|()
expr_stmt|;
name|ShutdownHookManager
operator|.
name|get
argument_list|()
operator|.
name|addShutdownHook
argument_list|(
name|this
argument_list|,
name|priority
argument_list|)
expr_stmt|;
block|}
comment|/**    * Unregister the hook.    */
DECL|method|unregister ()
specifier|public
specifier|synchronized
name|void
name|unregister
parameter_list|()
block|{
try|try
block|{
name|ShutdownHookManager
operator|.
name|get
argument_list|()
operator|.
name|removeShutdownHook
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Failed to unregister shutdown hook: {}"
argument_list|,
name|e
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Shutdown handler.    * Query the service hook reference -if it is still valid the     * {@link Service#stop()} operation is invoked.    */
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
name|shutdown
argument_list|()
expr_stmt|;
block|}
comment|/**    * Shutdown operation.    *<p>    * Subclasses may extend it, but it is primarily    * made available for testing.    * @return true if the service was stopped and no exception was raised.    */
DECL|method|shutdown ()
specifier|protected
name|boolean
name|shutdown
parameter_list|()
block|{
name|Service
name|service
decl_stmt|;
name|boolean
name|result
init|=
literal|false
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|service
operator|=
name|serviceRef
operator|.
name|get
argument_list|()
expr_stmt|;
name|serviceRef
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|service
operator|!=
literal|null
condition|)
block|{
try|try
block|{
comment|// Stop the  Service
name|service
operator|.
name|stop
argument_list|()
expr_stmt|;
name|result
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Error stopping {}"
argument_list|,
name|service
operator|.
name|getName
argument_list|()
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

