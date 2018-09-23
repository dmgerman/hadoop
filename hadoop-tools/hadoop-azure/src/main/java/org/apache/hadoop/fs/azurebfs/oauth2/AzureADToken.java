begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.azurebfs.oauth2
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|azurebfs
operator|.
name|oauth2
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

begin_comment
comment|/**  * Object representing the AAD access token to use when making HTTP requests to Azure Data Lake Storage.  */
end_comment

begin_class
DECL|class|AzureADToken
specifier|public
class|class
name|AzureADToken
block|{
DECL|field|accessToken
specifier|private
name|String
name|accessToken
decl_stmt|;
DECL|field|expiry
specifier|private
name|Date
name|expiry
decl_stmt|;
DECL|method|getAccessToken ()
specifier|public
name|String
name|getAccessToken
parameter_list|()
block|{
return|return
name|this
operator|.
name|accessToken
return|;
block|}
DECL|method|setAccessToken (String accessToken)
specifier|public
name|void
name|setAccessToken
parameter_list|(
name|String
name|accessToken
parameter_list|)
block|{
name|this
operator|.
name|accessToken
operator|=
name|accessToken
expr_stmt|;
block|}
DECL|method|getExpiry ()
specifier|public
name|Date
name|getExpiry
parameter_list|()
block|{
return|return
operator|new
name|Date
argument_list|(
name|this
operator|.
name|expiry
operator|.
name|getTime
argument_list|()
argument_list|)
return|;
block|}
DECL|method|setExpiry (Date expiry)
specifier|public
name|void
name|setExpiry
parameter_list|(
name|Date
name|expiry
parameter_list|)
block|{
name|this
operator|.
name|expiry
operator|=
operator|new
name|Date
argument_list|(
name|expiry
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

