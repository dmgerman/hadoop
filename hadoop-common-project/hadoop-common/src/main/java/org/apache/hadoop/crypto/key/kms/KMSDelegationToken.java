begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.crypto.key.kms
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|crypto
operator|.
name|key
operator|.
name|kms
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
name|io
operator|.
name|Text
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
name|delegation
operator|.
name|web
operator|.
name|DelegationTokenIdentifier
import|;
end_import

begin_comment
comment|/**  * Holder class for KMS delegation tokens.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|KMSDelegationToken
specifier|public
specifier|final
class|class
name|KMSDelegationToken
block|{
DECL|field|TOKEN_KIND_STR
specifier|public
specifier|static
specifier|final
name|String
name|TOKEN_KIND_STR
init|=
literal|"kms-dt"
decl_stmt|;
DECL|field|TOKEN_KIND
specifier|public
specifier|static
specifier|final
name|Text
name|TOKEN_KIND
init|=
operator|new
name|Text
argument_list|(
name|TOKEN_KIND_STR
argument_list|)
decl_stmt|;
comment|// Utility class is not supposed to be instantiated.
DECL|method|KMSDelegationToken ()
specifier|private
name|KMSDelegationToken
parameter_list|()
block|{   }
comment|/**    * DelegationTokenIdentifier used for the KMS.    */
DECL|class|KMSDelegationTokenIdentifier
specifier|public
specifier|static
class|class
name|KMSDelegationTokenIdentifier
extends|extends
name|DelegationTokenIdentifier
block|{
DECL|method|KMSDelegationTokenIdentifier ()
specifier|public
name|KMSDelegationTokenIdentifier
parameter_list|()
block|{
name|super
argument_list|(
name|TOKEN_KIND
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getKind ()
specifier|public
name|Text
name|getKind
parameter_list|()
block|{
return|return
name|TOKEN_KIND
return|;
block|}
block|}
block|}
end_class

end_unit

