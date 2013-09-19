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
name|security
operator|.
name|RpcAuthInfo
operator|.
name|AuthFlavor
import|;
end_import

begin_comment
comment|/**   * Base class for verifier. Currently we only support 3 types of auth flavors:   * {@link AuthFlavor#AUTH_NONE}, {@link AuthFlavor#AUTH_SYS},   * and {@link AuthFlavor#RPCSEC_GSS}.  */
end_comment

begin_class
DECL|class|Verifier
specifier|public
specifier|abstract
class|class
name|Verifier
extends|extends
name|RpcAuthInfo
block|{
DECL|method|Verifier (AuthFlavor flavor)
specifier|protected
name|Verifier
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
DECL|method|readFlavorAndVerifier (XDR xdr)
specifier|public
specifier|static
name|Verifier
name|readFlavorAndVerifier
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
name|Verifier
name|verifer
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
name|verifer
operator|=
operator|new
name|VerifierNone
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
name|verifer
operator|=
operator|new
name|VerifierGSS
argument_list|()
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Unsupported verifier flavor"
operator|+
name|flavor
argument_list|)
throw|;
block|}
name|verifer
operator|.
name|read
argument_list|(
name|xdr
argument_list|)
expr_stmt|;
return|return
name|verifer
return|;
block|}
block|}
end_class

end_unit

