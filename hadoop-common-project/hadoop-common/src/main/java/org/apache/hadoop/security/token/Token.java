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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
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
name|codec
operator|.
name|binary
operator|.
name|Base64
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
name|classification
operator|.
name|InterfaceStability
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
name|conf
operator|.
name|Configuration
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
name|*
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
name|util
operator|.
name|ReflectionUtils
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|java
operator|.
name|util
operator|.
name|ServiceLoader
import|;
end_import

begin_comment
comment|/**  * The client-side form of the token.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
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
name|Token
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|tokenKindMap
specifier|private
specifier|static
name|Map
argument_list|<
name|Text
argument_list|,
name|Class
argument_list|<
name|?
extends|extends
name|TokenIdentifier
argument_list|>
argument_list|>
name|tokenKindMap
decl_stmt|;
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
DECL|field|renewer
specifier|private
name|TokenRenewer
name|renewer
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
name|password
operator|=
name|mgr
operator|.
name|createPassword
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|identifier
operator|=
name|id
operator|.
name|getBytes
argument_list|()
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
comment|/**    * Construct a token from the components.    * @param identifier the token identifier    * @param password the token's password    * @param kind the kind of token    * @param service the service for this token    */
DECL|method|Token (byte[] identifier, byte[] password, Text kind, Text service)
specifier|public
name|Token
parameter_list|(
name|byte
index|[]
name|identifier
parameter_list|,
name|byte
index|[]
name|password
parameter_list|,
name|Text
name|kind
parameter_list|,
name|Text
name|service
parameter_list|)
block|{
name|this
operator|.
name|identifier
operator|=
operator|(
name|identifier
operator|==
literal|null
operator|)
condition|?
operator|new
name|byte
index|[
literal|0
index|]
else|:
name|identifier
expr_stmt|;
name|this
operator|.
name|password
operator|=
operator|(
name|password
operator|==
literal|null
operator|)
condition|?
operator|new
name|byte
index|[
literal|0
index|]
else|:
name|password
expr_stmt|;
name|this
operator|.
name|kind
operator|=
operator|(
name|kind
operator|==
literal|null
operator|)
condition|?
operator|new
name|Text
argument_list|()
else|:
name|kind
expr_stmt|;
name|this
operator|.
name|service
operator|=
operator|(
name|service
operator|==
literal|null
operator|)
condition|?
operator|new
name|Text
argument_list|()
else|:
name|service
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
comment|/**    * Clone a token.    * @param other the token to clone    */
DECL|method|Token (Token<T> other)
specifier|public
name|Token
parameter_list|(
name|Token
argument_list|<
name|T
argument_list|>
name|other
parameter_list|)
block|{
name|this
operator|.
name|identifier
operator|=
name|other
operator|.
name|identifier
expr_stmt|;
name|this
operator|.
name|password
operator|=
name|other
operator|.
name|password
expr_stmt|;
name|this
operator|.
name|kind
operator|=
name|other
operator|.
name|kind
expr_stmt|;
name|this
operator|.
name|service
operator|=
name|other
operator|.
name|service
expr_stmt|;
block|}
comment|/**    * Get the token identifier's byte representation    * @return the token identifier's byte representation    */
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
specifier|private
specifier|static
name|Class
argument_list|<
name|?
extends|extends
name|TokenIdentifier
argument_list|>
DECL|method|getClassForIdentifier (Text kind)
name|getClassForIdentifier
parameter_list|(
name|Text
name|kind
parameter_list|)
block|{
name|Class
argument_list|<
name|?
extends|extends
name|TokenIdentifier
argument_list|>
name|cls
init|=
literal|null
decl_stmt|;
synchronized|synchronized
init|(
name|Token
operator|.
name|class
init|)
block|{
if|if
condition|(
name|tokenKindMap
operator|==
literal|null
condition|)
block|{
name|tokenKindMap
operator|=
name|Maps
operator|.
name|newHashMap
argument_list|()
expr_stmt|;
for|for
control|(
name|TokenIdentifier
name|id
range|:
name|ServiceLoader
operator|.
name|load
argument_list|(
name|TokenIdentifier
operator|.
name|class
argument_list|)
control|)
block|{
name|tokenKindMap
operator|.
name|put
argument_list|(
name|id
operator|.
name|getKind
argument_list|()
argument_list|,
name|id
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|cls
operator|=
name|tokenKindMap
operator|.
name|get
argument_list|(
name|kind
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|cls
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Cannot find class for token kind "
operator|+
name|kind
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
return|return
name|cls
return|;
block|}
comment|/**    * Get the token identifier object, or null if it could not be constructed    * (because the class could not be loaded, for example).    * @return the token identifier, or null    * @throws IOException     */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|decodeIdentifier ()
specifier|public
name|T
name|decodeIdentifier
parameter_list|()
throws|throws
name|IOException
block|{
name|Class
argument_list|<
name|?
extends|extends
name|TokenIdentifier
argument_list|>
name|cls
init|=
name|getClassForIdentifier
argument_list|(
name|getKind
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|cls
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|TokenIdentifier
name|tokenIdentifier
init|=
name|ReflectionUtils
operator|.
name|newInstance
argument_list|(
name|cls
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|ByteArrayInputStream
name|buf
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|identifier
argument_list|)
decl_stmt|;
name|DataInputStream
name|in
init|=
operator|new
name|DataInputStream
argument_list|(
name|buf
argument_list|)
decl_stmt|;
name|tokenIdentifier
operator|.
name|readFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
operator|(
name|T
operator|)
name|tokenIdentifier
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
specifier|synchronized
name|Text
name|getKind
parameter_list|()
block|{
return|return
name|kind
return|;
block|}
comment|/**    * Set the token kind. This is only intended to be used by services that    * wrap another service's token.    * @param newKind    */
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|method|setKind (Text newKind)
specifier|public
specifier|synchronized
name|void
name|setKind
parameter_list|(
name|Text
name|newKind
parameter_list|)
block|{
name|kind
operator|=
name|newKind
expr_stmt|;
name|renewer
operator|=
literal|null
expr_stmt|;
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
comment|/**    * Indicates whether the token is a clone.  Used by HA failover proxy    * to indicate a token should not be visible to the user via    * UGI.getCredentials()    */
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|PrivateToken
specifier|public
specifier|static
class|class
name|PrivateToken
parameter_list|<
name|T
extends|extends
name|TokenIdentifier
parameter_list|>
extends|extends
name|Token
argument_list|<
name|T
argument_list|>
block|{
DECL|method|PrivateToken (Token<T> token)
specifier|public
name|PrivateToken
parameter_list|(
name|Token
argument_list|<
name|T
argument_list|>
name|token
parameter_list|)
block|{
name|super
argument_list|(
name|token
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
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
annotation|@
name|Override
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
comment|/**    * Generate a string with the url-quoted base64 encoded serialized form    * of the Writable.    * @param obj the object to serialize    * @return the encoded string    * @throws IOException    */
DECL|method|encodeWritable (Writable obj)
specifier|private
specifier|static
name|String
name|encodeWritable
parameter_list|(
name|Writable
name|obj
parameter_list|)
throws|throws
name|IOException
block|{
name|DataOutputBuffer
name|buf
init|=
operator|new
name|DataOutputBuffer
argument_list|()
decl_stmt|;
name|obj
operator|.
name|write
argument_list|(
name|buf
argument_list|)
expr_stmt|;
name|Base64
name|encoder
init|=
operator|new
name|Base64
argument_list|(
literal|0
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|byte
index|[]
name|raw
init|=
operator|new
name|byte
index|[
name|buf
operator|.
name|getLength
argument_list|()
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|buf
operator|.
name|getData
argument_list|()
argument_list|,
literal|0
argument_list|,
name|raw
argument_list|,
literal|0
argument_list|,
name|buf
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|encoder
operator|.
name|encodeToString
argument_list|(
name|raw
argument_list|)
return|;
block|}
comment|/**    * Modify the writable to the value from the newValue    * @param obj the object to read into    * @param newValue the string with the url-safe base64 encoded bytes    * @throws IOException    */
DECL|method|decodeWritable (Writable obj, String newValue)
specifier|private
specifier|static
name|void
name|decodeWritable
parameter_list|(
name|Writable
name|obj
parameter_list|,
name|String
name|newValue
parameter_list|)
throws|throws
name|IOException
block|{
name|Base64
name|decoder
init|=
operator|new
name|Base64
argument_list|(
literal|0
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|DataInputBuffer
name|buf
init|=
operator|new
name|DataInputBuffer
argument_list|()
decl_stmt|;
name|byte
index|[]
name|decoded
init|=
name|decoder
operator|.
name|decode
argument_list|(
name|newValue
argument_list|)
decl_stmt|;
name|buf
operator|.
name|reset
argument_list|(
name|decoded
argument_list|,
name|decoded
operator|.
name|length
argument_list|)
expr_stmt|;
name|obj
operator|.
name|readFields
argument_list|(
name|buf
argument_list|)
expr_stmt|;
block|}
comment|/**    * Encode this token as a url safe string    * @return the encoded string    * @throws IOException    */
DECL|method|encodeToUrlString ()
specifier|public
name|String
name|encodeToUrlString
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|encodeWritable
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|/**    * Decode the given url safe string into this token.    * @param newValue the encoded string    * @throws IOException    */
DECL|method|decodeFromUrlString (String newValue)
specifier|public
name|void
name|decodeFromUrlString
parameter_list|(
name|String
name|newValue
parameter_list|)
throws|throws
name|IOException
block|{
name|decodeWritable
argument_list|(
name|this
argument_list|,
name|newValue
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
DECL|method|equals (Object right)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|right
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|right
condition|)
block|{
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|right
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|right
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
block|{
name|Token
argument_list|<
name|T
argument_list|>
name|r
init|=
operator|(
name|Token
argument_list|<
name|T
argument_list|>
operator|)
name|right
decl_stmt|;
return|return
name|Arrays
operator|.
name|equals
argument_list|(
name|identifier
argument_list|,
name|r
operator|.
name|identifier
argument_list|)
operator|&&
name|Arrays
operator|.
name|equals
argument_list|(
name|password
argument_list|,
name|r
operator|.
name|password
argument_list|)
operator|&&
name|kind
operator|.
name|equals
argument_list|(
name|r
operator|.
name|kind
argument_list|)
operator|&&
name|service
operator|.
name|equals
argument_list|(
name|r
operator|.
name|service
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|WritableComparator
operator|.
name|hashBytes
argument_list|(
name|identifier
argument_list|,
name|identifier
operator|.
name|length
argument_list|)
return|;
block|}
DECL|method|addBinaryBuffer (StringBuilder buffer, byte[] bytes)
specifier|private
specifier|static
name|void
name|addBinaryBuffer
parameter_list|(
name|StringBuilder
name|buffer
parameter_list|,
name|byte
index|[]
name|bytes
parameter_list|)
block|{
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|bytes
operator|.
name|length
condition|;
name|idx
operator|++
control|)
block|{
comment|// if not the first, put a blank separator in
if|if
condition|(
name|idx
operator|!=
literal|0
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
name|String
name|num
init|=
name|Integer
operator|.
name|toHexString
argument_list|(
literal|0xff
operator|&
name|bytes
index|[
name|idx
index|]
argument_list|)
decl_stmt|;
comment|// if it is only one digit, add a leading 0.
if|if
condition|(
name|num
operator|.
name|length
argument_list|()
operator|<
literal|2
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|'0'
argument_list|)
expr_stmt|;
block|}
name|buffer
operator|.
name|append
argument_list|(
name|num
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|identifierToString (StringBuilder buffer)
specifier|private
name|void
name|identifierToString
parameter_list|(
name|StringBuilder
name|buffer
parameter_list|)
block|{
name|T
name|id
init|=
literal|null
decl_stmt|;
try|try
block|{
name|id
operator|=
name|decodeIdentifier
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// handle in the finally block
block|}
finally|finally
block|{
if|if
condition|(
name|id
operator|!=
literal|null
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|"("
argument_list|)
operator|.
name|append
argument_list|(
name|id
argument_list|)
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|addBinaryBuffer
argument_list|(
name|buffer
argument_list|,
name|identifier
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|buffer
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|"Kind: "
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|kind
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|", Service: "
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|service
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|", Ident: "
argument_list|)
expr_stmt|;
name|identifierToString
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|field|renewers
specifier|private
specifier|static
name|ServiceLoader
argument_list|<
name|TokenRenewer
argument_list|>
name|renewers
init|=
name|ServiceLoader
operator|.
name|load
argument_list|(
name|TokenRenewer
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|getRenewer ()
specifier|private
specifier|synchronized
name|TokenRenewer
name|getRenewer
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|renewer
operator|!=
literal|null
condition|)
block|{
return|return
name|renewer
return|;
block|}
name|renewer
operator|=
name|TRIVIAL_RENEWER
expr_stmt|;
synchronized|synchronized
init|(
name|renewers
init|)
block|{
for|for
control|(
name|TokenRenewer
name|canidate
range|:
name|renewers
control|)
block|{
if|if
condition|(
name|canidate
operator|.
name|handleKind
argument_list|(
name|this
operator|.
name|kind
argument_list|)
condition|)
block|{
name|renewer
operator|=
name|canidate
expr_stmt|;
return|return
name|renewer
return|;
block|}
block|}
block|}
name|LOG
operator|.
name|warn
argument_list|(
literal|"No TokenRenewer defined for token kind "
operator|+
name|this
operator|.
name|kind
argument_list|)
expr_stmt|;
return|return
name|renewer
return|;
block|}
comment|/**    * Is this token managed so that it can be renewed or cancelled?    * @return true, if it can be renewed and cancelled.    */
DECL|method|isManaged ()
specifier|public
name|boolean
name|isManaged
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|getRenewer
argument_list|()
operator|.
name|isManaged
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|/**    * Renew this delegation token    * @return the new expiration time    * @throws IOException    * @throws InterruptedException    */
DECL|method|renew (Configuration conf )
specifier|public
name|long
name|renew
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
return|return
name|getRenewer
argument_list|()
operator|.
name|renew
argument_list|(
name|this
argument_list|,
name|conf
argument_list|)
return|;
block|}
comment|/**    * Cancel this delegation token    * @throws IOException    * @throws InterruptedException    */
DECL|method|cancel (Configuration conf )
specifier|public
name|void
name|cancel
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|getRenewer
argument_list|()
operator|.
name|cancel
argument_list|(
name|this
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
comment|/**    * A trivial renewer for token kinds that aren't managed. Sub-classes need    * to implement getKind for their token kind.    */
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
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|TrivialRenewer
specifier|public
specifier|static
class|class
name|TrivialRenewer
extends|extends
name|TokenRenewer
block|{
comment|// define the kind for this renewer
DECL|method|getKind ()
specifier|protected
name|Text
name|getKind
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|handleKind (Text kind)
specifier|public
name|boolean
name|handleKind
parameter_list|(
name|Text
name|kind
parameter_list|)
block|{
return|return
name|kind
operator|.
name|equals
argument_list|(
name|getKind
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|isManaged (Token<?> token)
specifier|public
name|boolean
name|isManaged
parameter_list|(
name|Token
argument_list|<
name|?
argument_list|>
name|token
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|renew (Token<?> token, Configuration conf)
specifier|public
name|long
name|renew
parameter_list|(
name|Token
argument_list|<
name|?
argument_list|>
name|token
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Token renewal is not supported "
operator|+
literal|" for "
operator|+
name|token
operator|.
name|kind
operator|+
literal|" tokens"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|cancel (Token<?> token, Configuration conf)
specifier|public
name|void
name|cancel
parameter_list|(
name|Token
argument_list|<
name|?
argument_list|>
name|token
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Token cancel is not supported "
operator|+
literal|" for "
operator|+
name|token
operator|.
name|kind
operator|+
literal|" tokens"
argument_list|)
throw|;
block|}
block|}
DECL|field|TRIVIAL_RENEWER
specifier|private
specifier|static
specifier|final
name|TokenRenewer
name|TRIVIAL_RENEWER
init|=
operator|new
name|TrivialRenewer
argument_list|()
decl_stmt|;
block|}
end_class

end_unit

