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

begin_comment
comment|/** Credential used by RPCSEC_GSS */
end_comment

begin_class
DECL|class|CredentialsGSS
specifier|public
class|class
name|CredentialsGSS
extends|extends
name|Credentials
block|{
DECL|method|CredentialsGSS ()
specifier|public
name|CredentialsGSS
parameter_list|()
block|{
name|super
argument_list|(
name|AuthFlavor
operator|.
name|RPCSEC_GSS
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|read (XDR xdr)
specifier|public
name|void
name|read
parameter_list|(
name|XDR
name|xdr
parameter_list|)
block|{
comment|// TODO Auto-generated method stub
block|}
annotation|@
name|Override
DECL|method|write (XDR xdr)
specifier|public
name|void
name|write
parameter_list|(
name|XDR
name|xdr
parameter_list|)
block|{
comment|// TODO Auto-generated method stub
block|}
block|}
end_class

end_unit

