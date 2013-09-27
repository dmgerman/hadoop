begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  *  or more contributor license agreements.  See the NOTICE file  *  distributed with this work for additional information  *  regarding copyright ownership.  The ASF licenses this file  *  to you under the Apache License, Version 2.0 (the  *  "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *       http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.swift.auth
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|swift
operator|.
name|auth
package|;
end_package

begin_comment
comment|/**  * Class for Keystone authentication.  * Used when {@link ApiKeyCredentials} is not applicable  * THIS FILE IS MAPPED BY JACKSON TO AND FROM JSON.  * DO NOT RENAME OR MODIFY FIELDS AND THEIR ACCESSORS.  */
end_comment

begin_class
DECL|class|KeystoneApiKeyCredentials
specifier|public
class|class
name|KeystoneApiKeyCredentials
block|{
comment|/**    * User access key    */
DECL|field|accessKey
specifier|private
name|String
name|accessKey
decl_stmt|;
comment|/**    * User access secret    */
DECL|field|secretKey
specifier|private
name|String
name|secretKey
decl_stmt|;
DECL|method|KeystoneApiKeyCredentials (String accessKey, String secretKey)
specifier|public
name|KeystoneApiKeyCredentials
parameter_list|(
name|String
name|accessKey
parameter_list|,
name|String
name|secretKey
parameter_list|)
block|{
name|this
operator|.
name|accessKey
operator|=
name|accessKey
expr_stmt|;
name|this
operator|.
name|secretKey
operator|=
name|secretKey
expr_stmt|;
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
DECL|method|setAccessKey (String accessKey)
specifier|public
name|void
name|setAccessKey
parameter_list|(
name|String
name|accessKey
parameter_list|)
block|{
name|this
operator|.
name|accessKey
operator|=
name|accessKey
expr_stmt|;
block|}
DECL|method|getSecretKey ()
specifier|public
name|String
name|getSecretKey
parameter_list|()
block|{
return|return
name|secretKey
return|;
block|}
DECL|method|setSecretKey (String secretKey)
specifier|public
name|void
name|setSecretKey
parameter_list|(
name|String
name|secretKey
parameter_list|)
block|{
name|this
operator|.
name|secretKey
operator|=
name|secretKey
expr_stmt|;
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
literal|"user "
operator|+
literal|"'"
operator|+
name|accessKey
operator|+
literal|'\''
operator|+
literal|" with key of length "
operator|+
operator|(
operator|(
name|secretKey
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|secretKey
operator|.
name|length
argument_list|()
operator|)
return|;
block|}
block|}
end_class

end_unit

