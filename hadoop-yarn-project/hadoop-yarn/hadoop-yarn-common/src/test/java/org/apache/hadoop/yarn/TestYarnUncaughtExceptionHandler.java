begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertSame
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|spy
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|verify
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
name|ExitUtil
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
name|YarnRuntimeException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_class
DECL|class|TestYarnUncaughtExceptionHandler
specifier|public
class|class
name|TestYarnUncaughtExceptionHandler
block|{
DECL|field|exHandler
specifier|private
specifier|static
specifier|final
name|YarnUncaughtExceptionHandler
name|exHandler
init|=
operator|new
name|YarnUncaughtExceptionHandler
argument_list|()
decl_stmt|;
comment|/**    * Throw {@code YarnRuntimeException} inside thread and    * check {@code YarnUncaughtExceptionHandler} instance    *    * @throws InterruptedException    */
annotation|@
name|Test
DECL|method|testUncaughtExceptionHandlerWithRuntimeException ()
specifier|public
name|void
name|testUncaughtExceptionHandlerWithRuntimeException
parameter_list|()
throws|throws
name|InterruptedException
block|{
specifier|final
name|YarnUncaughtExceptionHandler
name|spyYarnHandler
init|=
name|spy
argument_list|(
name|exHandler
argument_list|)
decl_stmt|;
specifier|final
name|YarnRuntimeException
name|yarnException
init|=
operator|new
name|YarnRuntimeException
argument_list|(
literal|"test-yarn-runtime-exception"
argument_list|)
decl_stmt|;
specifier|final
name|Thread
name|yarnThread
init|=
operator|new
name|Thread
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
throw|throw
name|yarnException
throw|;
block|}
block|}
argument_list|)
decl_stmt|;
name|yarnThread
operator|.
name|setUncaughtExceptionHandler
argument_list|(
name|spyYarnHandler
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|spyYarnHandler
argument_list|,
name|yarnThread
operator|.
name|getUncaughtExceptionHandler
argument_list|()
argument_list|)
expr_stmt|;
name|yarnThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|yarnThread
operator|.
name|join
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|spyYarnHandler
argument_list|)
operator|.
name|uncaughtException
argument_list|(
name|yarnThread
argument_list|,
name|yarnException
argument_list|)
expr_stmt|;
block|}
comment|/**    *<p>    * Throw {@code Error} inside thread and    * check {@code YarnUncaughtExceptionHandler} instance    *<p>    * Used {@code ExitUtil} class to avoid jvm exit through    * {@code System.exit(-1) }    *    * @throws InterruptedException    */
annotation|@
name|Test
DECL|method|testUncaughtExceptionHandlerWithError ()
specifier|public
name|void
name|testUncaughtExceptionHandlerWithError
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|ExitUtil
operator|.
name|disableSystemExit
argument_list|()
expr_stmt|;
specifier|final
name|YarnUncaughtExceptionHandler
name|spyErrorHandler
init|=
name|spy
argument_list|(
name|exHandler
argument_list|)
decl_stmt|;
specifier|final
name|java
operator|.
name|lang
operator|.
name|Error
name|error
init|=
operator|new
name|java
operator|.
name|lang
operator|.
name|Error
argument_list|(
literal|"test-error"
argument_list|)
decl_stmt|;
specifier|final
name|Thread
name|errorThread
init|=
operator|new
name|Thread
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
throw|throw
name|error
throw|;
block|}
block|}
argument_list|)
decl_stmt|;
name|errorThread
operator|.
name|setUncaughtExceptionHandler
argument_list|(
name|spyErrorHandler
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|spyErrorHandler
argument_list|,
name|errorThread
operator|.
name|getUncaughtExceptionHandler
argument_list|()
argument_list|)
expr_stmt|;
name|errorThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|errorThread
operator|.
name|join
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|spyErrorHandler
argument_list|)
operator|.
name|uncaughtException
argument_list|(
name|errorThread
argument_list|,
name|error
argument_list|)
expr_stmt|;
block|}
comment|/**    *<p>    * Throw {@code OutOfMemoryError} inside thread and    * check {@code YarnUncaughtExceptionHandler} instance    *<p>    * Used {@code ExitUtil} class to avoid jvm exit through    * {@code Runtime.getRuntime().halt(-1)}    *    * @throws InterruptedException    */
annotation|@
name|Test
DECL|method|testUncaughtExceptionHandlerWithOutOfMemoryError ()
specifier|public
name|void
name|testUncaughtExceptionHandlerWithOutOfMemoryError
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|ExitUtil
operator|.
name|disableSystemHalt
argument_list|()
expr_stmt|;
specifier|final
name|YarnUncaughtExceptionHandler
name|spyOomHandler
init|=
name|spy
argument_list|(
name|exHandler
argument_list|)
decl_stmt|;
specifier|final
name|OutOfMemoryError
name|oomError
init|=
operator|new
name|OutOfMemoryError
argument_list|(
literal|"out-of-memory-error"
argument_list|)
decl_stmt|;
specifier|final
name|Thread
name|oomThread
init|=
operator|new
name|Thread
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
throw|throw
name|oomError
throw|;
block|}
block|}
argument_list|)
decl_stmt|;
name|oomThread
operator|.
name|setUncaughtExceptionHandler
argument_list|(
name|spyOomHandler
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|spyOomHandler
argument_list|,
name|oomThread
operator|.
name|getUncaughtExceptionHandler
argument_list|()
argument_list|)
expr_stmt|;
name|oomThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|oomThread
operator|.
name|join
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|spyOomHandler
argument_list|)
operator|.
name|uncaughtException
argument_list|(
name|oomThread
argument_list|,
name|oomError
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

