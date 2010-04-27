begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.security.token.delegation
package|package
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
name|java
operator|.
name|io
operator|.
name|DataInput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutput
import|;
end_import

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
name|javax
operator|.
name|crypto
operator|.
name|SecretKey
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
name|Writable
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
name|WritableUtils
import|;
end_import

begin_comment
comment|/**  * Key used for generating and verifying delegation tokens  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|LimitedPrivate
argument_list|(
block|{
literal|"HDFS"
block|,
literal|"MapReduce"
block|}
argument_list|)
DECL|class|DelegationKey
specifier|public
class|class
name|DelegationKey
implements|implements
name|Writable
block|{
DECL|field|keyId
specifier|private
name|int
name|keyId
decl_stmt|;
DECL|field|expiryDate
specifier|private
name|long
name|expiryDate
decl_stmt|;
DECL|field|key
specifier|private
name|SecretKey
name|key
decl_stmt|;
DECL|method|DelegationKey ()
specifier|public
name|DelegationKey
parameter_list|()
block|{
name|this
argument_list|(
literal|0
argument_list|,
literal|0L
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|DelegationKey (int keyId, long expiryDate, SecretKey key)
specifier|public
name|DelegationKey
parameter_list|(
name|int
name|keyId
parameter_list|,
name|long
name|expiryDate
parameter_list|,
name|SecretKey
name|key
parameter_list|)
block|{
name|this
operator|.
name|keyId
operator|=
name|keyId
expr_stmt|;
name|this
operator|.
name|expiryDate
operator|=
name|expiryDate
expr_stmt|;
name|this
operator|.
name|key
operator|=
name|key
expr_stmt|;
block|}
DECL|method|getKeyId ()
specifier|public
name|int
name|getKeyId
parameter_list|()
block|{
return|return
name|keyId
return|;
block|}
DECL|method|getExpiryDate ()
specifier|public
name|long
name|getExpiryDate
parameter_list|()
block|{
return|return
name|expiryDate
return|;
block|}
DECL|method|getKey ()
specifier|public
name|SecretKey
name|getKey
parameter_list|()
block|{
return|return
name|key
return|;
block|}
DECL|method|setExpiryDate (long expiryDate)
specifier|public
name|void
name|setExpiryDate
parameter_list|(
name|long
name|expiryDate
parameter_list|)
block|{
name|this
operator|.
name|expiryDate
operator|=
name|expiryDate
expr_stmt|;
block|}
comment|/**    */
DECL|method|write (DataOutput out)
specifier|public
name|void
name|write
parameter_list|(
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|WritableUtils
operator|.
name|writeVInt
argument_list|(
name|out
argument_list|,
name|keyId
argument_list|)
expr_stmt|;
name|WritableUtils
operator|.
name|writeVLong
argument_list|(
name|out
argument_list|,
name|expiryDate
argument_list|)
expr_stmt|;
name|byte
index|[]
name|keyBytes
init|=
name|key
operator|.
name|getEncoded
argument_list|()
decl_stmt|;
name|WritableUtils
operator|.
name|writeVInt
argument_list|(
name|out
argument_list|,
name|keyBytes
operator|.
name|length
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|keyBytes
argument_list|)
expr_stmt|;
block|}
comment|/**    */
DECL|method|readFields (DataInput in)
specifier|public
name|void
name|readFields
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|keyId
operator|=
name|WritableUtils
operator|.
name|readVInt
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|expiryDate
operator|=
name|WritableUtils
operator|.
name|readVLong
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|int
name|len
init|=
name|WritableUtils
operator|.
name|readVInt
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|byte
index|[]
name|keyBytes
init|=
operator|new
name|byte
index|[
name|len
index|]
decl_stmt|;
name|in
operator|.
name|readFully
argument_list|(
name|keyBytes
argument_list|)
expr_stmt|;
name|key
operator|=
name|AbstractDelegationTokenSecretManager
operator|.
name|createSecretKey
argument_list|(
name|keyBytes
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

