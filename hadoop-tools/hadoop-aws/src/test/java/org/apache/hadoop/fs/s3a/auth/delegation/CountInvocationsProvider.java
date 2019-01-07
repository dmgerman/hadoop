begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a.auth.delegation
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
name|auth
operator|.
name|delegation
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicLong
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|auth
operator|.
name|AWSCredentials
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|auth
operator|.
name|AWSCredentialsProvider
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
name|s3a
operator|.
name|CredentialInitializationException
import|;
end_import

begin_comment
comment|/**  * Simple AWS credential provider which counts how often it is invoked.  */
end_comment

begin_class
DECL|class|CountInvocationsProvider
specifier|public
class|class
name|CountInvocationsProvider
implements|implements
name|AWSCredentialsProvider
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
name|CountInvocationsProvider
operator|.
name|class
operator|.
name|getName
argument_list|()
decl_stmt|;
DECL|field|COUNTER
specifier|public
specifier|static
specifier|final
name|AtomicLong
name|COUNTER
init|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
decl_stmt|;
annotation|@
name|Override
DECL|method|getCredentials ()
specifier|public
name|AWSCredentials
name|getCredentials
parameter_list|()
block|{
name|COUNTER
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|CredentialInitializationException
argument_list|(
literal|"no credentials"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|refresh ()
specifier|public
name|void
name|refresh
parameter_list|()
block|{    }
DECL|method|getInvocationCount ()
specifier|public
specifier|static
name|long
name|getInvocationCount
parameter_list|()
block|{
return|return
name|COUNTER
operator|.
name|get
argument_list|()
return|;
block|}
block|}
end_class

end_unit

