begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.cosn.auth
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|cosn
operator|.
name|auth
package|;
end_package

begin_import
import|import
name|com
operator|.
name|qcloud
operator|.
name|cos
operator|.
name|auth
operator|.
name|BasicCOSCredentials
import|;
end_import

begin_import
import|import
name|com
operator|.
name|qcloud
operator|.
name|cos
operator|.
name|auth
operator|.
name|COSCredentials
import|;
end_import

begin_import
import|import
name|com
operator|.
name|qcloud
operator|.
name|cos
operator|.
name|auth
operator|.
name|COSCredentialsProvider
import|;
end_import

begin_import
import|import
name|com
operator|.
name|qcloud
operator|.
name|cos
operator|.
name|exception
operator|.
name|CosClientException
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
name|lang3
operator|.
name|StringUtils
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
name|conf
operator|.
name|Configuration
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
name|cosn
operator|.
name|CosNConfigKeys
import|;
end_import

begin_comment
comment|/**  * Get the credentials from the hadoop configuration.  */
end_comment

begin_class
DECL|class|SimpleCredentialProvider
specifier|public
class|class
name|SimpleCredentialProvider
implements|implements
name|COSCredentialsProvider
block|{
DECL|field|secretId
specifier|private
name|String
name|secretId
decl_stmt|;
DECL|field|secretKey
specifier|private
name|String
name|secretKey
decl_stmt|;
DECL|method|SimpleCredentialProvider (Configuration conf)
specifier|public
name|SimpleCredentialProvider
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|this
operator|.
name|secretId
operator|=
name|conf
operator|.
name|get
argument_list|(
name|CosNConfigKeys
operator|.
name|COSN_SECRET_ID_KEY
argument_list|)
expr_stmt|;
name|this
operator|.
name|secretKey
operator|=
name|conf
operator|.
name|get
argument_list|(
name|CosNConfigKeys
operator|.
name|COSN_SECRET_KEY_KEY
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getCredentials ()
specifier|public
name|COSCredentials
name|getCredentials
parameter_list|()
block|{
if|if
condition|(
operator|!
name|StringUtils
operator|.
name|isEmpty
argument_list|(
name|this
operator|.
name|secretId
argument_list|)
operator|&&
operator|!
name|StringUtils
operator|.
name|isEmpty
argument_list|(
name|this
operator|.
name|secretKey
argument_list|)
condition|)
block|{
return|return
operator|new
name|BasicCOSCredentials
argument_list|(
name|this
operator|.
name|secretId
argument_list|,
name|this
operator|.
name|secretKey
argument_list|)
return|;
block|}
throw|throw
operator|new
name|CosClientException
argument_list|(
literal|"secret id or secret key is unset"
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

