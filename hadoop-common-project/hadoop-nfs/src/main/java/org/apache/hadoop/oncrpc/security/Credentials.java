begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.oncrpc.security
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|oncrpc
operator|.
name|security
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
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
name|logging
operator|.
name|Log
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
name|logging
operator|.
name|LogFactory
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
name|oncrpc
operator|.
name|XDR
import|;
end_import

begin_comment
comment|/**  * Base class for all credentials. Currently we only support 3 different types  * of auth flavors: AUTH_NONE, AUTH_SYS, and RPCSEC_GSS.  */
end_comment

begin_class
DECL|class|Credentials
specifier|public
specifier|abstract
class|class
name|Credentials
extends|extends
name|RpcAuthInfo
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|Credentials
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|readFlavorAndCredentials (XDR xdr)
specifier|public
specifier|static
name|Credentials
name|readFlavorAndCredentials
parameter_list|(
name|XDR
name|xdr
parameter_list|)
block|{
name|AuthFlavor
name|flavor
init|=
name|AuthFlavor
operator|.
name|fromValue
argument_list|(
name|xdr
operator|.
name|readInt
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|Credentials
name|credentials
decl_stmt|;
if|if
condition|(
name|flavor
operator|==
name|AuthFlavor
operator|.
name|AUTH_NONE
condition|)
block|{
name|credentials
operator|=
operator|new
name|CredentialsNone
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|flavor
operator|==
name|AuthFlavor
operator|.
name|AUTH_SYS
condition|)
block|{
name|credentials
operator|=
operator|new
name|CredentialsSys
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|flavor
operator|==
name|AuthFlavor
operator|.
name|RPCSEC_GSS
condition|)
block|{
name|credentials
operator|=
operator|new
name|CredentialsGSS
argument_list|()
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Unsupported Credentials Flavor "
operator|+
name|flavor
argument_list|)
throw|;
block|}
name|credentials
operator|.
name|read
argument_list|(
name|xdr
argument_list|)
expr_stmt|;
return|return
name|credentials
return|;
block|}
comment|/**    * Write AuthFlavor and the credentials to the XDR    */
DECL|method|writeFlavorAndCredentials (Credentials cred, XDR xdr)
specifier|public
specifier|static
name|void
name|writeFlavorAndCredentials
parameter_list|(
name|Credentials
name|cred
parameter_list|,
name|XDR
name|xdr
parameter_list|)
block|{
if|if
condition|(
name|cred
operator|instanceof
name|CredentialsNone
condition|)
block|{
name|xdr
operator|.
name|writeInt
argument_list|(
name|AuthFlavor
operator|.
name|AUTH_NONE
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|cred
operator|instanceof
name|CredentialsSys
condition|)
block|{
name|xdr
operator|.
name|writeInt
argument_list|(
name|AuthFlavor
operator|.
name|AUTH_SYS
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|cred
operator|instanceof
name|CredentialsGSS
condition|)
block|{
name|xdr
operator|.
name|writeInt
argument_list|(
name|AuthFlavor
operator|.
name|RPCSEC_GSS
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Cannot recognize the verifier"
argument_list|)
throw|;
block|}
name|cred
operator|.
name|write
argument_list|(
name|xdr
argument_list|)
expr_stmt|;
block|}
DECL|field|mCredentialsLength
specifier|protected
name|int
name|mCredentialsLength
decl_stmt|;
DECL|method|Credentials (AuthFlavor flavor)
specifier|protected
name|Credentials
parameter_list|(
name|AuthFlavor
name|flavor
parameter_list|)
block|{
name|super
argument_list|(
name|flavor
argument_list|)
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getCredentialLength ()
name|int
name|getCredentialLength
parameter_list|()
block|{
return|return
name|mCredentialsLength
return|;
block|}
block|}
end_class

end_unit

