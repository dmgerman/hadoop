begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|s3
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
name|net
operator|.
name|URI
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

begin_comment
comment|/**  *<p>  * Extracts AWS credentials from the filesystem URI or configuration.  *</p>  */
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
DECL|class|S3Credentials
specifier|public
class|class
name|S3Credentials
block|{
DECL|field|accessKey
specifier|private
name|String
name|accessKey
decl_stmt|;
DECL|field|secretAccessKey
specifier|private
name|String
name|secretAccessKey
decl_stmt|;
comment|/**    * @throws IllegalArgumentException if credentials for S3 cannot be    * determined.    * @throws IOException if credential providers are misconfigured and we have    *                     to talk to them.    */
DECL|method|initialize (URI uri, Configuration conf)
specifier|public
name|void
name|initialize
parameter_list|(
name|URI
name|uri
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|uri
operator|.
name|getHost
argument_list|()
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid hostname in URI "
operator|+
name|uri
argument_list|)
throw|;
block|}
name|String
name|userInfo
init|=
name|uri
operator|.
name|getUserInfo
argument_list|()
decl_stmt|;
if|if
condition|(
name|userInfo
operator|!=
literal|null
condition|)
block|{
name|int
name|index
init|=
name|userInfo
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
decl_stmt|;
if|if
condition|(
name|index
operator|!=
operator|-
literal|1
condition|)
block|{
name|accessKey
operator|=
name|userInfo
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|index
argument_list|)
expr_stmt|;
name|secretAccessKey
operator|=
name|userInfo
operator|.
name|substring
argument_list|(
name|index
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|accessKey
operator|=
name|userInfo
expr_stmt|;
block|}
block|}
name|String
name|scheme
init|=
name|uri
operator|.
name|getScheme
argument_list|()
decl_stmt|;
name|String
name|accessKeyProperty
init|=
name|String
operator|.
name|format
argument_list|(
literal|"fs.%s.awsAccessKeyId"
argument_list|,
name|scheme
argument_list|)
decl_stmt|;
name|String
name|secretAccessKeyProperty
init|=
name|String
operator|.
name|format
argument_list|(
literal|"fs.%s.awsSecretAccessKey"
argument_list|,
name|scheme
argument_list|)
decl_stmt|;
if|if
condition|(
name|accessKey
operator|==
literal|null
condition|)
block|{
name|accessKey
operator|=
name|conf
operator|.
name|getTrimmed
argument_list|(
name|accessKeyProperty
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|secretAccessKey
operator|==
literal|null
condition|)
block|{
specifier|final
name|char
index|[]
name|pass
init|=
name|conf
operator|.
name|getPassword
argument_list|(
name|secretAccessKeyProperty
argument_list|)
decl_stmt|;
if|if
condition|(
name|pass
operator|!=
literal|null
condition|)
block|{
name|secretAccessKey
operator|=
operator|(
operator|new
name|String
argument_list|(
name|pass
argument_list|)
operator|)
operator|.
name|trim
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|accessKey
operator|==
literal|null
operator|&&
name|secretAccessKey
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"AWS "
operator|+
literal|"Access Key ID and Secret Access "
operator|+
literal|"Key must be specified as the "
operator|+
literal|"username or password "
operator|+
literal|"(respectively) of a "
operator|+
name|scheme
operator|+
literal|" URL, or by setting the "
operator|+
name|accessKeyProperty
operator|+
literal|" or "
operator|+
name|secretAccessKeyProperty
operator|+
literal|" properties (respectively)."
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|accessKey
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"AWS "
operator|+
literal|"Access Key ID must be specified "
operator|+
literal|"as the username of a "
operator|+
name|scheme
operator|+
literal|" URL, or by setting the "
operator|+
name|accessKeyProperty
operator|+
literal|" property."
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|secretAccessKey
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"AWS "
operator|+
literal|"Secret Access Key must be "
operator|+
literal|"specified as the password of a "
operator|+
name|scheme
operator|+
literal|" URL, or by setting the "
operator|+
name|secretAccessKeyProperty
operator|+
literal|" property."
argument_list|)
throw|;
block|}
block|}
DECL|method|getAccessKey ()
specifier|public
name|String
name|getAccessKey
parameter_list|()
block|{
return|return
name|accessKey
return|;
block|}
DECL|method|getSecretAccessKey ()
specifier|public
name|String
name|getSecretAccessKey
parameter_list|()
block|{
return|return
name|secretAccessKey
return|;
block|}
block|}
end_class

end_unit

