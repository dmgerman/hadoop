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
comment|/**  * For the usage and purpose of this class see {@link UnreliableInterface}  * which this class implements.  *  * @see UnreliableInterface  */
end_comment

begin_class
DECL|class|UnreliableImplementation
class|class
name|UnreliableImplementation
implements|implements
name|UnreliableInterface
block|{
DECL|field|failsOnceInvocationCount
specifier|private
name|int
name|failsOnceInvocationCount
decl_stmt|,
DECL|field|failsOnceWithValueInvocationCount
name|failsOnceWithValueInvocationCount
decl_stmt|,
DECL|field|failsOnceIOExceptionInvocationCount
name|failsOnceIOExceptionInvocationCount
decl_stmt|,
DECL|field|failsOnceRemoteExceptionInvocationCount
name|failsOnceRemoteExceptionInvocationCount
decl_stmt|,
DECL|field|failsTenTimesInvocationCount
name|failsTenTimesInvocationCount
decl_stmt|,
DECL|field|failsWithSASLExceptionTenTimesInvocationCount
name|failsWithSASLExceptionTenTimesInvocationCount
decl_stmt|,
DECL|field|failsWithAccessControlExceptionInvocationCount
name|failsWithAccessControlExceptionInvocationCount
decl_stmt|,
DECL|field|succeedsOnceThenFailsCount
name|succeedsOnceThenFailsCount
decl_stmt|,
DECL|field|succeedsOnceThenFailsIdempotentCount
name|succeedsOnceThenFailsIdempotentCount
decl_stmt|,
DECL|field|succeedsTenTimesThenFailsCount
name|succeedsTenTimesThenFailsCount
decl_stmt|;
DECL|field|identifier
specifier|private
name|String
name|identifier
decl_stmt|;
DECL|field|exceptionToFailWith
specifier|private
name|TypeOfExceptionToFailWith
name|exceptionToFailWith
decl_stmt|;
DECL|enum|TypeOfExceptionToFailWith
specifier|public
enum|enum
name|TypeOfExceptionToFailWith
block|{
DECL|enumConstant|UNRELIABLE_EXCEPTION
name|UNRELIABLE_EXCEPTION
block|,
DECL|enumConstant|STANDBY_EXCEPTION
name|STANDBY_EXCEPTION
block|,
DECL|enumConstant|IO_EXCEPTION
name|IO_EXCEPTION
block|,
DECL|enumConstant|REMOTE_EXCEPTION
name|REMOTE_EXCEPTION
block|}
DECL|method|UnreliableImplementation ()
specifier|public
name|UnreliableImplementation
parameter_list|()
block|{
name|this
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|UnreliableImplementation (String identifier)
specifier|public
name|UnreliableImplementation
parameter_list|(
name|String
name|identifier
parameter_list|)
block|{
name|this
argument_list|(
name|identifier
argument_list|,
name|TypeOfExceptionToFailWith
operator|.
name|UNRELIABLE_EXCEPTION
argument_list|)
expr_stmt|;
block|}
DECL|method|setIdentifier (String identifier)
specifier|public
name|void
name|setIdentifier
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
DECL|method|UnreliableImplementation (String identifier, TypeOfExceptionToFailWith exceptionToFailWith)
specifier|public
name|UnreliableImplementation
parameter_list|(
name|String
name|identifier
parameter_list|,
name|TypeOfExceptionToFailWith
name|exceptionToFailWith
parameter_list|)
block|{
name|this
operator|.
name|identifier
operator|=
name|identifier
expr_stmt|;
name|this
operator|.
name|exceptionToFailWith
operator|=
name|exceptionToFailWith
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|alwaysSucceeds ()
specifier|public
name|void
name|alwaysSucceeds
parameter_list|()
block|{
comment|// do nothing
block|}
annotation|@
name|Override
DECL|method|alwaysFailsWithFatalException ()
specifier|public
name|void
name|alwaysFailsWithFatalException
parameter_list|()
throws|throws
name|FatalException
block|{
throw|throw
operator|new
name|FatalException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|alwaysFailsWithRemoteFatalException ()
specifier|public
name|void
name|alwaysFailsWithRemoteFatalException
parameter_list|()
throws|throws
name|RemoteException
block|{
throw|throw
operator|new
name|RemoteException
argument_list|(
name|FatalException
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
literal|"Oops"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|failsOnceThenSucceeds ()
specifier|public
name|void
name|failsOnceThenSucceeds
parameter_list|()
throws|throws
name|UnreliableException
block|{
if|if
condition|(
name|failsOnceInvocationCount
operator|++
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|UnreliableException
argument_list|()
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|failsOnceThenSucceedsWithReturnValue ()
specifier|public
name|boolean
name|failsOnceThenSucceedsWithReturnValue
parameter_list|()
throws|throws
name|UnreliableException
block|{
if|if
condition|(
name|failsOnceWithValueInvocationCount
operator|++
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|UnreliableException
argument_list|()
throw|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|failsOnceWithIOException ()
specifier|public
name|void
name|failsOnceWithIOException
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|failsOnceIOExceptionInvocationCount
operator|++
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"test exception for failsOnceWithIOException"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|failsOnceWithRemoteException ()
specifier|public
name|void
name|failsOnceWithRemoteException
parameter_list|()
throws|throws
name|RemoteException
block|{
if|if
condition|(
name|failsOnceRemoteExceptionInvocationCount
operator|++
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|RemoteException
argument_list|(
name|IOException
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
literal|"test exception for failsOnceWithRemoteException"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|failsTenTimesThenSucceeds ()
specifier|public
name|void
name|failsTenTimesThenSucceeds
parameter_list|()
throws|throws
name|UnreliableException
block|{
if|if
condition|(
name|failsTenTimesInvocationCount
operator|++
operator|<
literal|10
condition|)
block|{
throw|throw
operator|new
name|UnreliableException
argument_list|()
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|failsWithSASLExceptionTenTimes ()
specifier|public
name|void
name|failsWithSASLExceptionTenTimes
parameter_list|()
throws|throws
name|SaslException
block|{
if|if
condition|(
name|failsWithSASLExceptionTenTimesInvocationCount
operator|++
operator|<
literal|10
condition|)
block|{
throw|throw
operator|new
name|SaslException
argument_list|()
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|failsWithAccessControlExceptionEightTimes ()
specifier|public
name|void
name|failsWithAccessControlExceptionEightTimes
parameter_list|()
throws|throws
name|AccessControlException
block|{
if|if
condition|(
name|failsWithAccessControlExceptionInvocationCount
operator|++
operator|<
literal|8
condition|)
block|{
throw|throw
operator|new
name|AccessControlException
argument_list|()
throw|;
block|}
block|}
DECL|method|failsWithWrappedAccessControlException ()
specifier|public
name|void
name|failsWithWrappedAccessControlException
parameter_list|()
throws|throws
name|IOException
block|{
name|AccessControlException
name|ace
init|=
operator|new
name|AccessControlException
argument_list|()
decl_stmt|;
name|IOException
name|ioe
init|=
operator|new
name|IOException
argument_list|(
name|ace
argument_list|)
decl_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
name|ioe
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|succeedsOnceThenFailsReturningString ()
specifier|public
name|String
name|succeedsOnceThenFailsReturningString
parameter_list|()
throws|throws
name|UnreliableException
throws|,
name|IOException
throws|,
name|StandbyException
block|{
if|if
condition|(
name|succeedsOnceThenFailsCount
operator|++
operator|<
literal|1
condition|)
block|{
return|return
name|identifier
return|;
block|}
else|else
block|{
name|throwAppropriateException
argument_list|(
name|exceptionToFailWith
argument_list|,
name|identifier
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|succeedsTenTimesThenFailsReturningString ()
specifier|public
name|String
name|succeedsTenTimesThenFailsReturningString
parameter_list|()
throws|throws
name|UnreliableException
throws|,
name|IOException
throws|,
name|StandbyException
block|{
if|if
condition|(
name|succeedsTenTimesThenFailsCount
operator|++
operator|<
literal|10
condition|)
block|{
return|return
name|identifier
return|;
block|}
else|else
block|{
name|throwAppropriateException
argument_list|(
name|exceptionToFailWith
argument_list|,
name|identifier
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
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
block|{
if|if
condition|(
name|succeedsOnceThenFailsIdempotentCount
operator|++
operator|<
literal|1
condition|)
block|{
return|return
name|identifier
return|;
block|}
else|else
block|{
name|throwAppropriateException
argument_list|(
name|exceptionToFailWith
argument_list|,
name|identifier
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
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
block|{
if|if
condition|(
name|this
operator|.
name|identifier
operator|.
name|equals
argument_list|(
name|identifier
argument_list|)
condition|)
block|{
return|return
name|identifier
return|;
block|}
else|else
block|{
name|String
name|message
init|=
literal|"expected '"
operator|+
name|this
operator|.
name|identifier
operator|+
literal|"' but received '"
operator|+
name|identifier
operator|+
literal|"'"
decl_stmt|;
name|throwAppropriateException
argument_list|(
name|exceptionToFailWith
argument_list|,
name|message
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|nonIdempotentVoidFailsIfIdentifierDoesntMatch (String identifier)
specifier|public
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
block|{
if|if
condition|(
name|this
operator|.
name|identifier
operator|.
name|equals
argument_list|(
name|identifier
argument_list|)
condition|)
block|{
return|return;
block|}
else|else
block|{
name|String
name|message
init|=
literal|"expected '"
operator|+
name|this
operator|.
name|identifier
operator|+
literal|"' but received '"
operator|+
name|identifier
operator|+
literal|"'"
decl_stmt|;
name|throwAppropriateException
argument_list|(
name|exceptionToFailWith
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"["
operator|+
name|identifier
operator|+
literal|"]"
return|;
block|}
DECL|method|throwAppropriateException (TypeOfExceptionToFailWith eType, String message)
specifier|private
specifier|static
name|void
name|throwAppropriateException
parameter_list|(
name|TypeOfExceptionToFailWith
name|eType
parameter_list|,
name|String
name|message
parameter_list|)
throws|throws
name|UnreliableException
throws|,
name|StandbyException
throws|,
name|IOException
block|{
switch|switch
condition|(
name|eType
condition|)
block|{
case|case
name|STANDBY_EXCEPTION
case|:
throw|throw
operator|new
name|StandbyException
argument_list|(
name|message
argument_list|)
throw|;
case|case
name|UNRELIABLE_EXCEPTION
case|:
throw|throw
operator|new
name|UnreliableException
argument_list|(
name|message
argument_list|)
throw|;
case|case
name|IO_EXCEPTION
case|:
throw|throw
operator|new
name|IOException
argument_list|(
name|message
argument_list|)
throw|;
case|case
name|REMOTE_EXCEPTION
case|:
throw|throw
operator|new
name|RemoteException
argument_list|(
name|IOException
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|message
argument_list|)
throw|;
default|default:
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|message
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

