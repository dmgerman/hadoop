begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a.auth
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
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_comment
comment|/**  * A special exception which declares that no credentials were found;  * this can be treated specially in logging, handling, etc.  * As it subclasses {@link NoAuthWithAWSException}, the S3A retry handler  * knows not to attempt to ask for the credentials again.  */
end_comment

begin_class
DECL|class|NoAwsCredentialsException
specifier|public
class|class
name|NoAwsCredentialsException
extends|extends
name|NoAuthWithAWSException
block|{
comment|/**    * The default error message: {@value}.    */
DECL|field|E_NO_AWS_CREDENTIALS
specifier|public
specifier|static
specifier|final
name|String
name|E_NO_AWS_CREDENTIALS
init|=
literal|"No AWS Credentials"
decl_stmt|;
comment|/**    * Construct.    * @param credentialProvider name of the credential provider.    * @param message message.    */
DECL|method|NoAwsCredentialsException ( @onnull final String credentialProvider, @Nonnull final String message)
specifier|public
name|NoAwsCredentialsException
parameter_list|(
annotation|@
name|Nonnull
specifier|final
name|String
name|credentialProvider
parameter_list|,
annotation|@
name|Nonnull
specifier|final
name|String
name|message
parameter_list|)
block|{
name|this
argument_list|(
name|credentialProvider
argument_list|,
name|message
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**    * Construct with the default message of {@link #E_NO_AWS_CREDENTIALS}.    * @param credentialProvider name of the credential provider.    */
DECL|method|NoAwsCredentialsException ( @onnull final String credentialProvider)
specifier|public
name|NoAwsCredentialsException
parameter_list|(
annotation|@
name|Nonnull
specifier|final
name|String
name|credentialProvider
parameter_list|)
block|{
name|this
argument_list|(
name|credentialProvider
argument_list|,
name|E_NO_AWS_CREDENTIALS
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**    * Construct with exception.    * @param credentialProvider name of the credential provider.    * @param message message.    * @param thrown inner exception    */
DECL|method|NoAwsCredentialsException ( @onnull final String credentialProvider, @Nonnull final String message, final Throwable thrown)
specifier|public
name|NoAwsCredentialsException
parameter_list|(
annotation|@
name|Nonnull
specifier|final
name|String
name|credentialProvider
parameter_list|,
annotation|@
name|Nonnull
specifier|final
name|String
name|message
parameter_list|,
specifier|final
name|Throwable
name|thrown
parameter_list|)
block|{
name|super
argument_list|(
name|credentialProvider
operator|+
literal|": "
operator|+
name|message
argument_list|,
name|thrown
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

