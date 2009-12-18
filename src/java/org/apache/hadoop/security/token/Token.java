begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.security.token
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
package|;
end_package

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
comment|/**  * The client-side form of the token.  */
end_comment

begin_class
DECL|class|Token
specifier|public
class|class
name|Token
parameter_list|<
name|T
extends|extends
name|TokenIdentifier
parameter_list|>
implements|implements
name|Writable
block|{
DECL|field|identifier
specifier|private
name|byte
index|[]
name|identifier
decl_stmt|;
DECL|field|password
specifier|private
name|byte
index|[]
name|password
decl_stmt|;
DECL|field|kind
specifier|private
name|Text
name|kind
decl_stmt|;
DECL|field|service
specifier|private
name|Text
name|service
decl_stmt|;
comment|/**    * Construct a token given a token identifier and a secret manager for the    * type of the token identifier.    * @param id the token identifier    * @param mgr the secret manager    */
DECL|method|Token (T id, SecretManager<T> mgr)
specifier|public
name|Token
parameter_list|(
name|T
name|id
parameter_list|,
name|SecretManager
argument_list|<
name|T
argument_list|>
name|mgr
parameter_list|)
block|{
name|identifier
operator|=
name|id
operator|.
name|getBytes
argument_list|()
expr_stmt|;
name|password
operator|=
name|mgr
operator|.
name|createPassword
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|kind
operator|=
name|id
operator|.
name|getKind
argument_list|()
expr_stmt|;
name|service
operator|=
operator|new
name|Text
argument_list|()
expr_stmt|;
block|}
comment|/**    * Default constructor    */
DECL|method|Token ()
specifier|public
name|Token
parameter_list|()
block|{
name|identifier
operator|=
operator|new
name|byte
index|[
literal|0
index|]
expr_stmt|;
name|password
operator|=
operator|new
name|byte
index|[
literal|0
index|]
expr_stmt|;
name|kind
operator|=
operator|new
name|Text
argument_list|()
expr_stmt|;
name|service
operator|=
operator|new
name|Text
argument_list|()
expr_stmt|;
block|}
comment|/**    * Get the token identifier    * @return the token identifier    */
DECL|method|getIdentifier ()
specifier|public
name|byte
index|[]
name|getIdentifier
parameter_list|()
block|{
return|return
name|identifier
return|;
block|}
comment|/**    * Get the token password/secret    * @return the token password/secret    */
DECL|method|getPassword ()
specifier|public
name|byte
index|[]
name|getPassword
parameter_list|()
block|{
return|return
name|password
return|;
block|}
comment|/**    * Get the token kind    * @return the kind of the token    */
DECL|method|getKind ()
specifier|public
name|Text
name|getKind
parameter_list|()
block|{
return|return
name|kind
return|;
block|}
comment|/**    * Get the service on which the token is supposed to be used    * @return the service name    */
DECL|method|getService ()
specifier|public
name|Text
name|getService
parameter_list|()
block|{
return|return
name|service
return|;
block|}
comment|/**    * Set the service on which the token is supposed to be used    * @param newService the service name    */
DECL|method|setService (Text newService)
specifier|public
name|void
name|setService
parameter_list|(
name|Text
name|newService
parameter_list|)
block|{
name|service
operator|=
name|newService
expr_stmt|;
block|}
comment|/** {@inheritDoc} */
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
if|if
condition|(
name|identifier
operator|==
literal|null
operator|||
name|identifier
operator|.
name|length
operator|!=
name|len
condition|)
block|{
name|identifier
operator|=
operator|new
name|byte
index|[
name|len
index|]
expr_stmt|;
block|}
name|in
operator|.
name|readFully
argument_list|(
name|identifier
argument_list|)
expr_stmt|;
name|len
operator|=
name|WritableUtils
operator|.
name|readVInt
argument_list|(
name|in
argument_list|)
expr_stmt|;
if|if
condition|(
name|password
operator|==
literal|null
operator|||
name|password
operator|.
name|length
operator|!=
name|len
condition|)
block|{
name|password
operator|=
operator|new
name|byte
index|[
name|len
index|]
expr_stmt|;
block|}
name|in
operator|.
name|readFully
argument_list|(
name|password
argument_list|)
expr_stmt|;
name|kind
operator|.
name|readFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|service
operator|.
name|readFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
comment|/** {@inheritDoc} */
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
name|identifier
operator|.
name|length
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|identifier
argument_list|)
expr_stmt|;
name|WritableUtils
operator|.
name|writeVInt
argument_list|(
name|out
argument_list|,
name|password
operator|.
name|length
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|password
argument_list|)
expr_stmt|;
name|kind
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|service
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

