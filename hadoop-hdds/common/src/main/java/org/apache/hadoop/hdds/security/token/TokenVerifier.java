begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.ââSee the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.ââThe ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.ââYou may obtain a copy of the License at  *  * ââââ http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.security.token
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|security
operator|.
name|token
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
name|hdds
operator|.
name|security
operator|.
name|exception
operator|.
name|SCMSecurityException
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
name|UserGroupInformation
import|;
end_import

begin_comment
comment|/**  * Ozone GRPC token header verifier.  */
end_comment

begin_interface
DECL|interface|TokenVerifier
specifier|public
interface|interface
name|TokenVerifier
block|{
comment|/**    * Given a user and tokenStr header, return a UGI object with token if    * verified.    * @param user user of the request    * @param tokenStr token str of the request    * @return UGI    * @throws SCMSecurityException    */
DECL|method|verify (String user, String tokenStr)
name|UserGroupInformation
name|verify
parameter_list|(
name|String
name|user
parameter_list|,
name|String
name|tokenStr
parameter_list|)
throws|throws
name|SCMSecurityException
function_decl|;
block|}
end_interface

end_unit

