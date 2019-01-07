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
name|Optional
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|s3
operator|.
name|model
operator|.
name|SSEAwsKeyManagementParams
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|s3
operator|.
name|model
operator|.
name|SSECustomerKey
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
name|S3AEncryptionMethods
import|;
end_import

begin_comment
comment|/**  * These support operations on {@link EncryptionSecrets} which use the AWS SDK  * operations. Isolating them here ensures that that class is not required on  * the classpath.  */
end_comment

begin_class
DECL|class|EncryptionSecretOperations
specifier|public
class|class
name|EncryptionSecretOperations
block|{
comment|/**    * Create SSE-C client side key encryption options on demand.    * @return an optional key to attach to a request.    * @param secrets source of the encryption secrets.    */
DECL|method|createSSECustomerKey ( final EncryptionSecrets secrets)
specifier|public
specifier|static
name|Optional
argument_list|<
name|SSECustomerKey
argument_list|>
name|createSSECustomerKey
parameter_list|(
specifier|final
name|EncryptionSecrets
name|secrets
parameter_list|)
block|{
if|if
condition|(
name|secrets
operator|.
name|hasEncryptionKey
argument_list|()
operator|&&
name|secrets
operator|.
name|getEncryptionMethod
argument_list|()
operator|==
name|S3AEncryptionMethods
operator|.
name|SSE_C
condition|)
block|{
return|return
name|Optional
operator|.
name|of
argument_list|(
operator|new
name|SSECustomerKey
argument_list|(
name|secrets
operator|.
name|getEncryptionKey
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|Optional
operator|.
name|empty
argument_list|()
return|;
block|}
block|}
comment|/**    * Create SSE-KMS options for a request, iff the encryption is SSE-KMS.    * @return an optional SSE-KMS param to attach to a request.    * @param secrets source of the encryption secrets.    */
DECL|method|createSSEAwsKeyManagementParams ( final EncryptionSecrets secrets)
specifier|public
specifier|static
name|Optional
argument_list|<
name|SSEAwsKeyManagementParams
argument_list|>
name|createSSEAwsKeyManagementParams
parameter_list|(
specifier|final
name|EncryptionSecrets
name|secrets
parameter_list|)
block|{
comment|//Use specified key, otherwise default to default master aws/s3 key by AWS
if|if
condition|(
name|secrets
operator|.
name|getEncryptionMethod
argument_list|()
operator|==
name|S3AEncryptionMethods
operator|.
name|SSE_KMS
condition|)
block|{
if|if
condition|(
name|secrets
operator|.
name|hasEncryptionKey
argument_list|()
condition|)
block|{
return|return
name|Optional
operator|.
name|of
argument_list|(
operator|new
name|SSEAwsKeyManagementParams
argument_list|(
name|secrets
operator|.
name|getEncryptionKey
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|Optional
operator|.
name|of
argument_list|(
operator|new
name|SSEAwsKeyManagementParams
argument_list|()
argument_list|)
return|;
block|}
block|}
else|else
block|{
return|return
name|Optional
operator|.
name|empty
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

