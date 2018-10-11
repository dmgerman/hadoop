begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
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
name|TimeUnit
import|;
end_import

begin_comment
comment|/**  * Helper class to shutdown {@link Thread}s and {@link ExecutorService}s.  */
end_comment

begin_class
DECL|class|ShutdownThreadsHelper
specifier|public
class|class
name|ShutdownThreadsHelper
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
name|ShutdownThreadsHelper
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|VisibleForTesting
DECL|field|SHUTDOWN_WAIT_MS
specifier|static
specifier|final
name|int
name|SHUTDOWN_WAIT_MS
init|=
literal|3000
decl_stmt|;
comment|/**    * @param thread {@link Thread to be shutdown}    * @return<tt>true</tt> if the thread is successfully interrupted,    *<tt>false</tt> otherwise    */
DECL|method|shutdownThread (Thread thread)
specifier|public
specifier|static
name|boolean
name|shutdownThread
parameter_list|(
name|Thread
name|thread
parameter_list|)
block|{
return|return
name|shutdownThread
argument_list|(
name|thread
argument_list|,
name|SHUTDOWN_WAIT_MS
argument_list|)
return|;
block|}
comment|/**    * @param thread {@link Thread to be shutdown}    * @param timeoutInMilliSeconds time to wait for thread to join after being    *                              interrupted    * @return<tt>true</tt> if the thread is successfully interrupted,    *<tt>false</tt> otherwise    */
DECL|method|shutdownThread (Thread thread, long timeoutInMilliSeconds)
specifier|public
specifier|static
name|boolean
name|shutdownThread
parameter_list|(
name|Thread
name|thread
parameter_list|,
name|long
name|timeoutInMilliSeconds
parameter_list|)
block|{
if|if
condition|(
name|thread
operator|==
literal|null
condition|)
block|{
return|return
literal|true
return|;
block|}
try|try
block|{
name|thread
operator|.
name|interrupt
argument_list|()
expr_stmt|;
name|thread
operator|.
name|join
argument_list|(
name|timeoutInMilliSeconds
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Interrupted while shutting down thread - "
operator|+
name|thread
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
comment|/**    * @param service {@link ExecutorService to be shutdown}    * @return<tt>true</tt> if the service is terminated,    *<tt>false</tt> otherwise    * @throws InterruptedException    */
DECL|method|shutdownExecutorService (ExecutorService service)
specifier|public
specifier|static
name|boolean
name|shutdownExecutorService
parameter_list|(
name|ExecutorService
name|service
parameter_list|)
throws|throws
name|InterruptedException
block|{
return|return
name|shutdownExecutorService
argument_list|(
name|service
argument_list|,
name|SHUTDOWN_WAIT_MS
argument_list|)
return|;
block|}
comment|/**    * @param service {@link ExecutorService to be shutdown}    * @param timeoutInMs time to wait for {@link    * ExecutorService#awaitTermination(long, java.util.concurrent.TimeUnit)}    *                    calls in milli seconds.    * @return<tt>true</tt> if the service is terminated,    *<tt>false</tt> otherwise    * @throws InterruptedException    */
DECL|method|shutdownExecutorService (ExecutorService service, long timeoutInMs)
specifier|public
specifier|static
name|boolean
name|shutdownExecutorService
parameter_list|(
name|ExecutorService
name|service
parameter_list|,
name|long
name|timeoutInMs
parameter_list|)
throws|throws
name|InterruptedException
block|{
if|if
condition|(
name|service
operator|==
literal|null
condition|)
block|{
return|return
literal|true
return|;
block|}
name|service
operator|.
name|shutdown
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|service
operator|.
name|awaitTermination
argument_list|(
name|timeoutInMs
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
condition|)
block|{
name|service
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
return|return
name|service
operator|.
name|awaitTermination
argument_list|(
name|timeoutInMs
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|true
return|;
block|}
block|}
block|}
end_class

end_unit

