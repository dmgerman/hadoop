begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.web
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|web
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
name|UserGroupInformation
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
name|authentication
operator|.
name|client
operator|.
name|Authenticator
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
name|authentication
operator|.
name|client
operator|.
name|KerberosAuthenticator
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
name|authentication
operator|.
name|client
operator|.
name|PseudoAuthenticator
import|;
end_import

begin_comment
comment|/**  * Use UserGroupInformation as a fallback authenticator  * if the server does not use Kerberos SPNEGO HTTP authentication.  */
end_comment

begin_class
DECL|class|KerberosUgiAuthenticator
specifier|public
class|class
name|KerberosUgiAuthenticator
extends|extends
name|KerberosAuthenticator
block|{
annotation|@
name|Override
DECL|method|getFallBackAuthenticator ()
specifier|protected
name|Authenticator
name|getFallBackAuthenticator
parameter_list|()
block|{
return|return
operator|new
name|PseudoAuthenticator
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|String
name|getUserName
parameter_list|()
block|{
try|try
block|{
return|return
name|UserGroupInformation
operator|.
name|getLoginUser
argument_list|()
operator|.
name|getUserName
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SecurityException
argument_list|(
literal|"Failed to obtain current username"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

