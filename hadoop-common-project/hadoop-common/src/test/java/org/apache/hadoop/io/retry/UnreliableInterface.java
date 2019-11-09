begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|IOException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|sasl
operator|.
name|SaslException
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
name|security
operator|.
name|AccessControlException
import|;
end_import

begin_comment
comment|/**  * The methods of UnreliableInterface could throw exceptions in a  * predefined way. It is currently used for testing {@link RetryPolicy}  * and {@link FailoverProxyProvider} classes, but can be potentially used  * to test any class's behaviour where an underlying interface or class  * may throw exceptions.  *  * Some methods may be annotated with the {@link Idempotent} annotation.  * In order to test those some methods of UnreliableInterface are annotated,  * but they are not actually Idempotent functions.  *  */
end_comment

begin_interface
DECL|interface|UnreliableInterface
specifier|public
interface|interface
name|UnreliableInterface
block|{
DECL|class|UnreliableException
specifier|public
specifier|static
class|class
name|UnreliableException
extends|extends
name|Exception
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
DECL|field|identifier
specifier|private
name|String
name|identifier
decl_stmt|;
DECL|method|UnreliableException ()
specifier|public
name|UnreliableException
parameter_list|()
block|{
comment|// no body
block|}
DECL|method|UnreliableException (String identifier)
specifier|public
name|UnreliableException
parameter_list|(
name|String
name|identifier
parameter_list|)
block|{
name|this
operator|.
name|identifier
operator|=
name|identifier
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getMessage ()
specifier|public
name|String
name|getMessage
parameter_list|()
block|{
return|return
name|identifier
return|;
block|}
block|}
DECL|class|FatalException
specifier|public
specifier|static
class|class
name|FatalException
extends|extends
name|UnreliableException
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
comment|// no body
block|}
DECL|method|alwaysSucceeds ()
name|void
name|alwaysSucceeds
parameter_list|()
throws|throws
name|UnreliableException
function_decl|;
DECL|method|alwaysFailsWithFatalException ()
name|void
name|alwaysFailsWithFatalException
parameter_list|()
throws|throws
name|FatalException
function_decl|;
DECL|method|alwaysFailsWithRemoteFatalException ()
name|void
name|alwaysFailsWithRemoteFatalException
parameter_list|()
throws|throws
name|RemoteException
function_decl|;
DECL|method|failsOnceWithIOException ()
name|void
name|failsOnceWithIOException
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|method|failsOnceWithRemoteException ()
name|void
name|failsOnceWithRemoteException
parameter_list|()
throws|throws
name|RemoteException
function_decl|;
DECL|method|failsOnceThenSucceeds ()
name|void
name|failsOnceThenSucceeds
parameter_list|()
throws|throws
name|UnreliableException
function_decl|;
DECL|method|failsOnceThenSucceedsWithReturnValue ()
name|boolean
name|failsOnceThenSucceedsWithReturnValue
parameter_list|()
throws|throws
name|UnreliableException
function_decl|;
DECL|method|failsTenTimesThenSucceeds ()
name|void
name|failsTenTimesThenSucceeds
parameter_list|()
throws|throws
name|UnreliableException
function_decl|;
DECL|method|failsWithSASLExceptionTenTimes ()
name|void
name|failsWithSASLExceptionTenTimes
parameter_list|()
throws|throws
name|SaslException
function_decl|;
annotation|@
name|Idempotent
DECL|method|failsWithAccessControlExceptionEightTimes ()
name|void
name|failsWithAccessControlExceptionEightTimes
parameter_list|()
throws|throws
name|AccessControlException
function_decl|;
annotation|@
name|Idempotent
DECL|method|failsWithWrappedAccessControlException ()
name|void
name|failsWithWrappedAccessControlException
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|method|succeedsOnceThenFailsReturningString ()
specifier|public
name|String
name|succeedsOnceThenFailsReturningString
parameter_list|()
throws|throws
name|UnreliableException
throws|,
name|StandbyException
throws|,
name|IOException
function_decl|;
annotation|@
name|Idempotent
DECL|method|succeedsOnceThenFailsReturningStringIdempotent ()
specifier|public
name|String
name|succeedsOnceThenFailsReturningStringIdempotent
parameter_list|()
throws|throws
name|UnreliableException
throws|,
name|StandbyException
throws|,
name|IOException
function_decl|;
DECL|method|succeedsTenTimesThenFailsReturningString ()
specifier|public
name|String
name|succeedsTenTimesThenFailsReturningString
parameter_list|()
throws|throws
name|UnreliableException
throws|,
name|StandbyException
throws|,
name|IOException
function_decl|;
annotation|@
name|Idempotent
DECL|method|failsIfIdentifierDoesntMatch (String identifier)
specifier|public
name|String
name|failsIfIdentifierDoesntMatch
parameter_list|(
name|String
name|identifier
parameter_list|)
throws|throws
name|UnreliableException
throws|,
name|StandbyException
throws|,
name|IOException
function_decl|;
DECL|method|nonIdempotentVoidFailsIfIdentifierDoesntMatch (String identifier)
name|void
name|nonIdempotentVoidFailsIfIdentifierDoesntMatch
parameter_list|(
name|String
name|identifier
parameter_list|)
throws|throws
name|UnreliableException
throws|,
name|StandbyException
throws|,
name|IOException
function_decl|;
block|}
end_interface

end_unit

