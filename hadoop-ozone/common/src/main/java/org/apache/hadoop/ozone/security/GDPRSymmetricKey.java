begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.security
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
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
name|base
operator|.
name|Preconditions
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
name|lang3
operator|.
name|RandomStringUtils
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
name|ozone
operator|.
name|OzoneConsts
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|crypto
operator|.
name|Cipher
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|crypto
operator|.
name|spec
operator|.
name|SecretKeySpec
import|;
end_import

begin_comment
comment|/**  * Symmetric Key structure for GDPR.  */
end_comment

begin_class
DECL|class|GDPRSymmetricKey
specifier|public
class|class
name|GDPRSymmetricKey
block|{
DECL|field|secretKey
specifier|private
name|SecretKeySpec
name|secretKey
decl_stmt|;
DECL|field|cipher
specifier|private
name|Cipher
name|cipher
decl_stmt|;
DECL|field|algorithm
specifier|private
name|String
name|algorithm
decl_stmt|;
DECL|field|secret
specifier|private
name|String
name|secret
decl_stmt|;
DECL|method|getSecretKey ()
specifier|public
name|SecretKeySpec
name|getSecretKey
parameter_list|()
block|{
return|return
name|secretKey
return|;
block|}
DECL|method|getCipher ()
specifier|public
name|Cipher
name|getCipher
parameter_list|()
block|{
return|return
name|cipher
return|;
block|}
comment|/**    * Default constructor creates key with default values.    * @throws Exception    */
DECL|method|GDPRSymmetricKey ()
specifier|public
name|GDPRSymmetricKey
parameter_list|()
throws|throws
name|Exception
block|{
name|algorithm
operator|=
name|OzoneConsts
operator|.
name|GDPR_ALGORITHM_NAME
expr_stmt|;
name|secret
operator|=
name|RandomStringUtils
operator|.
name|randomAlphabetic
argument_list|(
name|OzoneConsts
operator|.
name|GDPR_DEFAULT_RANDOM_SECRET_LENGTH
argument_list|)
expr_stmt|;
name|this
operator|.
name|secretKey
operator|=
operator|new
name|SecretKeySpec
argument_list|(
name|secret
operator|.
name|getBytes
argument_list|(
name|OzoneConsts
operator|.
name|GDPR_CHARSET
argument_list|)
argument_list|,
name|algorithm
argument_list|)
expr_stmt|;
name|this
operator|.
name|cipher
operator|=
name|Cipher
operator|.
name|getInstance
argument_list|(
name|algorithm
argument_list|)
expr_stmt|;
block|}
comment|/**    * Overloaded constructor creates key with specified values.    * @throws Exception    */
DECL|method|GDPRSymmetricKey (String secret, String algorithm)
specifier|public
name|GDPRSymmetricKey
parameter_list|(
name|String
name|secret
parameter_list|,
name|String
name|algorithm
parameter_list|)
throws|throws
name|Exception
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|secret
argument_list|,
literal|"Secret cannot be null"
argument_list|)
expr_stmt|;
comment|//TODO: When we add feature to allow users to customize the secret length,
comment|// we need to update this length check Precondition
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|secret
operator|.
name|length
argument_list|()
operator|==
literal|16
argument_list|,
literal|"Secret must be exactly 16 characters"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|algorithm
argument_list|,
literal|"Algorithm cannot be null"
argument_list|)
expr_stmt|;
name|this
operator|.
name|secret
operator|=
name|secret
expr_stmt|;
name|this
operator|.
name|algorithm
operator|=
name|algorithm
expr_stmt|;
name|this
operator|.
name|secretKey
operator|=
operator|new
name|SecretKeySpec
argument_list|(
name|secret
operator|.
name|getBytes
argument_list|(
name|OzoneConsts
operator|.
name|GDPR_CHARSET
argument_list|)
argument_list|,
name|algorithm
argument_list|)
expr_stmt|;
name|this
operator|.
name|cipher
operator|=
name|Cipher
operator|.
name|getInstance
argument_list|(
name|algorithm
argument_list|)
expr_stmt|;
block|}
DECL|method|getKeyDetails ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getKeyDetails
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|keyDetail
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|keyDetail
operator|.
name|put
argument_list|(
name|OzoneConsts
operator|.
name|GDPR_SECRET
argument_list|,
name|this
operator|.
name|secret
argument_list|)
expr_stmt|;
name|keyDetail
operator|.
name|put
argument_list|(
name|OzoneConsts
operator|.
name|GDPR_ALGORITHM
argument_list|,
name|this
operator|.
name|algorithm
argument_list|)
expr_stmt|;
return|return
name|keyDetail
return|;
block|}
block|}
end_class

end_unit

