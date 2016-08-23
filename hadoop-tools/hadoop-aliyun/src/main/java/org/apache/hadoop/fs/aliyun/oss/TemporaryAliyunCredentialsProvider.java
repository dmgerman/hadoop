begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.aliyun.oss
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|aliyun
operator|.
name|oss
package|;
end_package

begin_import
import|import
name|com
operator|.
name|aliyun
operator|.
name|oss
operator|.
name|common
operator|.
name|auth
operator|.
name|Credentials
import|;
end_import

begin_import
import|import
name|com
operator|.
name|aliyun
operator|.
name|oss
operator|.
name|common
operator|.
name|auth
operator|.
name|CredentialsProvider
import|;
end_import

begin_import
import|import
name|com
operator|.
name|aliyun
operator|.
name|oss
operator|.
name|common
operator|.
name|auth
operator|.
name|DefaultCredentials
import|;
end_import

begin_import
import|import
name|com
operator|.
name|aliyun
operator|.
name|oss
operator|.
name|common
operator|.
name|auth
operator|.
name|InvalidCredentialsException
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
name|lang
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
name|java
operator|.
name|net
operator|.
name|URI
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
name|aliyun
operator|.
name|oss
operator|.
name|Constants
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Support session credentials for authenticating with ALiyun.  */
end_comment

begin_class
DECL|class|TemporaryAliyunCredentialsProvider
specifier|public
class|class
name|TemporaryAliyunCredentialsProvider
implements|implements
name|CredentialsProvider
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"org.apache.hadoop.fs.aliyun.oss.TemporaryAliyunCredentialsProvider"
decl_stmt|;
DECL|field|accessKeyId
specifier|private
specifier|final
name|String
name|accessKeyId
decl_stmt|;
DECL|field|accessKeySecret
specifier|private
specifier|final
name|String
name|accessKeySecret
decl_stmt|;
DECL|field|securityToken
specifier|private
specifier|final
name|String
name|securityToken
decl_stmt|;
DECL|method|TemporaryAliyunCredentialsProvider (URI uri, Configuration conf)
specifier|public
name|TemporaryAliyunCredentialsProvider
parameter_list|(
name|URI
name|uri
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|this
operator|.
name|accessKeyId
operator|=
name|conf
operator|.
name|get
argument_list|(
name|ACCESS_KEY
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|accessKeySecret
operator|=
name|conf
operator|.
name|get
argument_list|(
name|SECRET_KEY
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|securityToken
operator|=
name|conf
operator|.
name|get
argument_list|(
name|SECURITY_TOKEN
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setCredentials (Credentials creds)
specifier|public
name|void
name|setCredentials
parameter_list|(
name|Credentials
name|creds
parameter_list|)
block|{    }
annotation|@
name|Override
DECL|method|getCredentials ()
specifier|public
name|Credentials
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
name|accessKeyId
argument_list|)
operator|&&
operator|!
name|StringUtils
operator|.
name|isEmpty
argument_list|(
name|accessKeySecret
argument_list|)
operator|&&
operator|!
name|StringUtils
operator|.
name|isEmpty
argument_list|(
name|securityToken
argument_list|)
condition|)
block|{
return|return
operator|new
name|DefaultCredentials
argument_list|(
name|accessKeyId
argument_list|,
name|accessKeySecret
argument_list|,
name|securityToken
argument_list|)
return|;
block|}
throw|throw
operator|new
name|InvalidCredentialsException
argument_list|(
literal|"AccessKeyId, AccessKeySecret or SecurityToken is unset"
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

