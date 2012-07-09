begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
package|;
end_package

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|Thread
operator|.
name|UncaughtExceptionHandler
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
name|util
operator|.
name|ShutdownHookManager
import|;
end_import

begin_comment
comment|/**  * This class is intended to be installed by calling   * {@link Thread#setDefaultUncaughtExceptionHandler(UncaughtExceptionHandler)}  * In the main entry point.  It is intended to try and cleanly shut down  * programs using the Yarn Event framework.  *   * Note: Right now it only will shut down the program if a Error is caught, but  * not any other exception.  Anything else is just logged.  */
end_comment

begin_class
DECL|class|YarnUncaughtExceptionHandler
specifier|public
class|class
name|YarnUncaughtExceptionHandler
implements|implements
name|UncaughtExceptionHandler
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
name|YarnUncaughtExceptionHandler
operator|.
name|class
argument_list|)
decl_stmt|;
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
if|if
condition|(
name|ShutdownHookManager
operator|.
name|get
argument_list|()
operator|.
name|isShutdownInProgress
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Thread "
operator|+
name|t
operator|+
literal|" threw an Throwable, but we are shutting "
operator|+
literal|"down, so ignoring this"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|e
operator|instanceof
name|Error
condition|)
block|{
try|try
block|{
name|LOG
operator|.
name|fatal
argument_list|(
literal|"Thread "
operator|+
name|t
operator|+
literal|" threw an Error.  Shutting down now..."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|err
parameter_list|)
block|{
comment|//We don't want to not exit because of an issue with logging
block|}
if|if
condition|(
name|e
operator|instanceof
name|OutOfMemoryError
condition|)
block|{
comment|//After catching an OOM java says it is undefined behavior, so don't
comment|//even try to clean up or we can get stuck on shutdown.
try|try
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Halting due to Out Of Memory Error..."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|err
parameter_list|)
block|{
comment|//Again we done want to exit because of logging issues.
block|}
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|halt
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|exit
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Thread "
operator|+
name|t
operator|+
literal|" threw an Exception."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

