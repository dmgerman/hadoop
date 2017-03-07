begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.azure
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|azure
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
comment|/**  *  Interface to implement authorization support in WASB.  *  API's of this interface will be implemented in the  *  StorageInterface Layer before making calls to Azure  *  Storage.  */
end_comment

begin_interface
DECL|interface|WasbAuthorizerInterface
specifier|public
interface|interface
name|WasbAuthorizerInterface
block|{
comment|/**    * Initializer method    * @param conf - Configuration object    * @throws WasbAuthorizationException - On authorization exceptions    * @throws IOException - When not able to reach the authorizer    */
DECL|method|init (Configuration conf)
specifier|public
name|void
name|init
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|WasbAuthorizationException
throws|,
name|IOException
function_decl|;
comment|/**    * Authorizer API to authorize access in WASB.     * @param wasbAbolutePath : Absolute WASB Path used for access.    * @param accessType : Type of access    * @param delegationToken : The user information.    * @return : true - If access allowed false - If access is not allowed.    * @throws WasbAuthorizationException - On authorization exceptions    * @throws IOException - When not able to reach the authorizer    */
DECL|method|authorize (String wasbAbolutePath, String accessType, String delegationToken)
specifier|public
name|boolean
name|authorize
parameter_list|(
name|String
name|wasbAbolutePath
parameter_list|,
name|String
name|accessType
parameter_list|,
name|String
name|delegationToken
parameter_list|)
throws|throws
name|WasbAuthorizationException
throws|,
name|IOException
function_decl|;
block|}
end_interface

end_unit

