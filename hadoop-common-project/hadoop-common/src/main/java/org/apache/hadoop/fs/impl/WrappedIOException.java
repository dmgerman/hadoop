begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.impl
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
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
name|concurrent
operator|.
name|ExecutionException
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

begin_comment
comment|/**  * A wrapper for an IOException which  * {@link FutureIOSupport#raiseInnerCause(ExecutionException)} knows to  * always extract the exception.  *  * The constructor signature guarantees the cause will be an IOException,  * and as it checks for a null-argument, non-null.  */
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
DECL|class|WrappedIOException
specifier|public
class|class
name|WrappedIOException
extends|extends
name|RuntimeException
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|2510210974235779294L
decl_stmt|;
comment|/**    * Construct from a non-null IOException.    * @param cause inner cause    * @throws NullPointerException if the cause is null.    */
DECL|method|WrappedIOException (final IOException cause)
specifier|public
name|WrappedIOException
parameter_list|(
specifier|final
name|IOException
name|cause
parameter_list|)
block|{
name|super
argument_list|(
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|cause
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getCause ()
specifier|public
specifier|synchronized
name|IOException
name|getCause
parameter_list|()
block|{
return|return
operator|(
name|IOException
operator|)
name|super
operator|.
name|getCause
argument_list|()
return|;
block|}
block|}
end_class

end_unit

