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
comment|/**  * Describes credentials to log in Swift using Keystone authentication.  * THIS FILE IS MAPPED BY JACKSON TO AND FROM JSON.  * DO NOT RENAME OR MODIFY FIELDS AND THEIR ACCESSORS.  */
end_comment

begin_class
DECL|class|ApiKeyCredentials
specifier|public
class|class
name|ApiKeyCredentials
block|{
comment|/**    * user login    */
DECL|field|username
specifier|private
name|String
name|username
decl_stmt|;
comment|/**    * user password    */
DECL|field|apikey
specifier|private
name|String
name|apikey
decl_stmt|;
comment|/**    * default constructor    */
DECL|method|ApiKeyCredentials ()
specifier|public
name|ApiKeyCredentials
parameter_list|()
block|{   }
comment|/**    * @param username user login    * @param apikey user api key    */
DECL|method|ApiKeyCredentials (String username, String apikey)
specifier|public
name|ApiKeyCredentials
parameter_list|(
name|String
name|username
parameter_list|,
name|String
name|apikey
parameter_list|)
block|{
name|this
operator|.
name|username
operator|=
name|username
expr_stmt|;
name|this
operator|.
name|apikey
operator|=
name|apikey
expr_stmt|;
block|}
comment|/**    * @return user api key    */
DECL|method|getApiKey ()
specifier|public
name|String
name|getApiKey
parameter_list|()
block|{
return|return
name|apikey
return|;
block|}
comment|/**    * @param apikey user api key    */
DECL|method|setApiKey (String apikey)
specifier|public
name|void
name|setApiKey
parameter_list|(
name|String
name|apikey
parameter_list|)
block|{
name|this
operator|.
name|apikey
operator|=
name|apikey
expr_stmt|;
block|}
comment|/**    * @return login    */
DECL|method|getUsername ()
specifier|public
name|String
name|getUsername
parameter_list|()
block|{
return|return
name|username
return|;
block|}
comment|/**    * @param username login    */
DECL|method|setUsername (String username)
specifier|public
name|void
name|setUsername
parameter_list|(
name|String
name|username
parameter_list|)
block|{
name|this
operator|.
name|username
operator|=
name|username
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
name|username
operator|+
literal|'\''
operator|+
literal|" with key of length "
operator|+
operator|(
operator|(
name|apikey
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|apikey
operator|.
name|length
argument_list|()
operator|)
return|;
block|}
block|}
end_class

end_unit

