begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.security.token.block
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|security
operator|.
name|token
operator|.
name|block
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
name|security
operator|.
name|token
operator|.
name|Token
import|;
end_import

begin_comment
comment|/** Utilities for security tests */
end_comment

begin_class
DECL|class|SecurityTestUtil
specifier|public
class|class
name|SecurityTestUtil
block|{
comment|/**    * check if an access token is expired. return true when token is expired,    * false otherwise    */
DECL|method|isBlockTokenExpired (Token<BlockTokenIdentifier> token)
specifier|public
specifier|static
name|boolean
name|isBlockTokenExpired
parameter_list|(
name|Token
argument_list|<
name|BlockTokenIdentifier
argument_list|>
name|token
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|BlockTokenSecretManager
operator|.
name|isTokenExpired
argument_list|(
name|token
argument_list|)
return|;
block|}
comment|/**    * set access token lifetime.    */
DECL|method|setBlockTokenLifetime (BlockTokenSecretManager handler, long tokenLifetime)
specifier|public
specifier|static
name|void
name|setBlockTokenLifetime
parameter_list|(
name|BlockTokenSecretManager
name|handler
parameter_list|,
name|long
name|tokenLifetime
parameter_list|)
block|{
name|handler
operator|.
name|setTokenLifetime
argument_list|(
name|tokenLifetime
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

