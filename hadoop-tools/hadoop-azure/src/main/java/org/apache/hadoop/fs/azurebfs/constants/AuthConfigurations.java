begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.azurebfs.constants
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
name|constants
package|;
end_package

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

begin_comment
comment|/**  * Responsible to keep all the Azure Blob File System auth related  * configurations.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|AuthConfigurations
specifier|public
specifier|final
class|class
name|AuthConfigurations
block|{
comment|/** Default OAuth token end point for the MSI flow. */
DECL|field|DEFAULT_FS_AZURE_ACCOUNT_OAUTH_MSI_ENDPOINT
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_FS_AZURE_ACCOUNT_OAUTH_MSI_ENDPOINT
init|=
literal|"http://169.254.169.254/metadata/identity/oauth2/token"
decl_stmt|;
comment|/** Default value for authority for the MSI flow. */
DECL|field|DEFAULT_FS_AZURE_ACCOUNT_OAUTH_MSI_AUTHORITY
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_FS_AZURE_ACCOUNT_OAUTH_MSI_AUTHORITY
init|=
literal|"https://login.microsoftonline.com/"
decl_stmt|;
comment|/** Default OAuth token end point for the refresh token flow. */
specifier|public
specifier|static
specifier|final
name|String
DECL|field|DEFAULT_FS_AZURE_ACCOUNT_OAUTH_REFRESH_TOKEN_ENDPOINT
name|DEFAULT_FS_AZURE_ACCOUNT_OAUTH_REFRESH_TOKEN_ENDPOINT
init|=
literal|"https://login.microsoftonline.com/Common/oauth2/token"
decl_stmt|;
DECL|method|AuthConfigurations ()
specifier|private
name|AuthConfigurations
parameter_list|()
block|{   }
block|}
end_class

end_unit

