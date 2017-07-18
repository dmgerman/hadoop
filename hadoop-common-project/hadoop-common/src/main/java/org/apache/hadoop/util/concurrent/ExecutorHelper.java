begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * *  *  Licensed to the Apache Software Foundation (ASF) under one  *  or more contributor license agreements.  See the NOTICE file  *  distributed with this work for additional information  *  regarding copyright ownership.  The ASF licenses this file  *  to you under the Apache License, Version 2.0 (the  *  "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  * /  */
end_comment

begin_package
DECL|package|org.apache.hadoop.util.concurrent
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
operator|.
name|concurrent
package|;
end_package

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
name|Future
import|;
end_import

begin_comment
comment|/** Helper functions for Executors. */
end_comment

begin_class
DECL|class|ExecutorHelper
specifier|public
specifier|final
class|class
name|ExecutorHelper
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
name|ExecutorHelper
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|logThrowableFromAfterExecute (Runnable r, Throwable t)
specifier|static
name|void
name|logThrowableFromAfterExecute
parameter_list|(
name|Runnable
name|r
parameter_list|,
name|Throwable
name|t
parameter_list|)
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
literal|"afterExecute in thread: "
operator|+
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|", runnable type: "
operator|+
name|r
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//For additional information, see: https://docs.oracle
comment|// .com/javase/7/docs/api/java/util/concurrent/ThreadPoolExecutor
comment|// .html#afterExecute(java.lang.Runnable,%20java.lang.Throwable) .
if|if
condition|(
name|t
operator|==
literal|null
operator|&&
name|r
operator|instanceof
name|Future
argument_list|<
name|?
argument_list|>
condition|)
block|{
try|try
block|{
operator|(
operator|(
name|Future
argument_list|<
name|?
argument_list|>
operator|)
name|r
operator|)
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|ee
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Execution exception when running task in "
operator|+
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|t
operator|=
name|ee
operator|.
name|getCause
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
name|warn
argument_list|(
literal|"Thread ("
operator|+
name|Thread
operator|.
name|currentThread
argument_list|()
operator|+
literal|") interrupted: "
argument_list|,
name|ie
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|throwable
parameter_list|)
block|{
name|t
operator|=
name|throwable
expr_stmt|;
block|}
block|}
if|if
condition|(
name|t
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Caught exception in thread "
operator|+
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|": "
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_constructor
DECL|method|ExecutorHelper ()
specifier|private
name|ExecutorHelper
parameter_list|()
block|{}
end_constructor

unit|}
end_unit

