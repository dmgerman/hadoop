begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a.impl
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|s3a
operator|.
name|impl
package|;
end_package

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
name|util
operator|.
name|List
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
name|CancellationException
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
name|CompletableFuture
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
name|CompletionException
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
name|Executor
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Supplier
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
name|fs
operator|.
name|impl
operator|.
name|WrappedIOException
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
name|DurationInfo
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
name|fs
operator|.
name|impl
operator|.
name|FutureIOSupport
operator|.
name|raiseInnerCause
import|;
end_import

begin_comment
comment|/**  * A bridge from Callable to Supplier; catching exceptions  * raised by the callable and wrapping them as appropriate.  * @param<T> return type.  */
end_comment

begin_class
DECL|class|CallableSupplier
specifier|public
specifier|final
class|class
name|CallableSupplier
parameter_list|<
name|T
parameter_list|>
implements|implements
name|Supplier
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
name|CallableSupplier
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|call
specifier|private
specifier|final
name|Callable
argument_list|<
name|T
argument_list|>
name|call
decl_stmt|;
comment|/**    * Create.    * @param call call to invoke.    */
DECL|method|CallableSupplier (final Callable<T> call)
specifier|public
name|CallableSupplier
parameter_list|(
specifier|final
name|Callable
argument_list|<
name|T
argument_list|>
name|call
parameter_list|)
block|{
name|this
operator|.
name|call
operator|=
name|call
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|get ()
specifier|public
name|Object
name|get
parameter_list|()
block|{
try|try
block|{
return|return
name|call
operator|.
name|call
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
throw|throw
name|e
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|WrappedIOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|WrappedIOException
argument_list|(
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
argument_list|)
throw|;
block|}
block|}
comment|/**    * Submit a callable into a completable future.    * RTEs are rethrown.    * Non RTEs are caught and wrapped; IOExceptions to    * {@link WrappedIOException} instances.    * @param executor executor.    * @param call call to invoke    * @param<T> type    * @return the future to wait for    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|submit ( final Executor executor, final Callable<T> call)
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|CompletableFuture
argument_list|<
name|T
argument_list|>
name|submit
parameter_list|(
specifier|final
name|Executor
name|executor
parameter_list|,
specifier|final
name|Callable
argument_list|<
name|T
argument_list|>
name|call
parameter_list|)
block|{
return|return
name|CompletableFuture
operator|.
name|supplyAsync
argument_list|(
operator|new
name|CallableSupplier
argument_list|<
name|T
argument_list|>
argument_list|(
name|call
argument_list|)
argument_list|,
name|executor
argument_list|)
return|;
block|}
comment|/**    * Wait for a list of futures to complete. If the list is empty,    * return immediately.    * @param futures list of futures.    * @throws IOException if one of the called futures raised an IOE.    * @throws RuntimeException if one of the futures raised one.    */
DECL|method|waitForCompletion ( final List<CompletableFuture<T>> futures)
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|void
name|waitForCompletion
parameter_list|(
specifier|final
name|List
argument_list|<
name|CompletableFuture
argument_list|<
name|T
argument_list|>
argument_list|>
name|futures
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|futures
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return;
block|}
comment|// await completion
name|waitForCompletion
argument_list|(
name|CompletableFuture
operator|.
name|allOf
argument_list|(
name|futures
operator|.
name|toArray
argument_list|(
operator|new
name|CompletableFuture
index|[
literal|0
index|]
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Wait for a single of future to complete, extracting IOEs afterwards.    * @param future future to wait for.    * @throws IOException if one of the called futures raised an IOE.    * @throws RuntimeException if one of the futures raised one.    */
DECL|method|waitForCompletion ( final CompletableFuture<T> future)
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|void
name|waitForCompletion
parameter_list|(
specifier|final
name|CompletableFuture
argument_list|<
name|T
argument_list|>
name|future
parameter_list|)
throws|throws
name|IOException
block|{
try|try
init|(
name|DurationInfo
name|ignore
init|=
operator|new
name|DurationInfo
argument_list|(
name|LOG
argument_list|,
literal|false
argument_list|,
literal|"Waiting for task completion"
argument_list|)
init|)
block|{
name|future
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CancellationException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|CompletionException
name|e
parameter_list|)
block|{
name|raiseInnerCause
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

