begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a
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
package|;
end_package

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|AmazonClientException
import|;
end_import

begin_comment
comment|/**  * Exception which Hadoop's AWSCredentialsProvider implementations should  * throw when there is a problem with the credential setup. This  * is a subclass of {@link AmazonClientException} which sets  * {@link #isRetryable()} to false, so as to fail fast.  */
end_comment

begin_class
DECL|class|CredentialInitializationException
specifier|public
class|class
name|CredentialInitializationException
extends|extends
name|AmazonClientException
block|{
DECL|method|CredentialInitializationException (String message, Throwable t)
specifier|public
name|CredentialInitializationException
parameter_list|(
name|String
name|message
parameter_list|,
name|Throwable
name|t
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
DECL|method|CredentialInitializationException (String message)
specifier|public
name|CredentialInitializationException
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
comment|/**    * This exception is not going to go away if you try calling it again.    * @return false, always.    */
annotation|@
name|Override
DECL|method|isRetryable ()
specifier|public
name|boolean
name|isRetryable
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

