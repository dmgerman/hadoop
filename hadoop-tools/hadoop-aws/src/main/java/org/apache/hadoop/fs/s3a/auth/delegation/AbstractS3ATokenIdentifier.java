begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a.auth.delegation
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|s3a
operator|.
name|auth
operator|.
name|delegation
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
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
name|DataInputStream
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
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
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
name|net
operator|.
name|NetUtils
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
name|Token
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

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Objects
operator|.
name|requireNonNull
import|;
end_import

begin_comment
comment|/**  * An S3A Delegation Token Identifier: contains the information needed  * to talk to S3A.  *  * These are loaded via the service loader API an used in a map of  * Kind to class, which is then looked up to deserialize token  * identifiers of a given class.  *  * Every non-abstract class must provide  *<ol>  *<li>Their unique token kind.</li>  *<li>An empty constructor.</li>  *<li>An entry in the resource file  *   {@code /META-INF/services/org.apache.hadoop.security.token.TokenIdentifier}  *</li>  *</ol>  *  * The base implementation contains  *<ol>  *<li>The URI of the FS.</li>  *<li>Encryption secrets for use in the destination FS.</li>  *</ol>  * Subclasses are required to add whatever information is needed to authenticate  * the user with the credential provider which their binding class will  * provide.  *  *<i>Important: Add no references to any AWS SDK class, to  * ensure it can be safely deserialized whenever the relevant token  * identifier of a token type declared in this JAR is examined.</i>  */
end_comment

begin_class
DECL|class|AbstractS3ATokenIdentifier
specifier|public
specifier|abstract
class|class
name|AbstractS3ATokenIdentifier
extends|extends
name|DelegationTokenIdentifier
block|{
comment|/**    * The maximum string length supported for text fields.    */
DECL|field|MAX_TEXT_LENGTH
specifier|protected
specifier|static
specifier|final
name|int
name|MAX_TEXT_LENGTH
init|=
literal|8192
decl_stmt|;
comment|/** Canonical URI of the bucket. */
DECL|field|uri
specifier|private
name|URI
name|uri
decl_stmt|;
comment|/**    * Encryption secrets to also marshall with any credentials.    * Set during creation to ensure it is never null.    */
DECL|field|encryptionSecrets
specifier|private
name|EncryptionSecrets
name|encryptionSecrets
init|=
operator|new
name|EncryptionSecrets
argument_list|()
decl_stmt|;
comment|/**    * Timestamp of creation.    * This is set to the current time; it will be overridden when    * deserializing data.    */
DECL|field|created
specifier|private
name|long
name|created
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
comment|/**    * An origin string for diagnostics.    */
DECL|field|origin
specifier|private
name|String
name|origin
init|=
literal|""
decl_stmt|;
comment|/**    * This marshalled UUID can be used in testing to verify transmission,    * and reuse; as it is printed you can see what is happending too.    */
DECL|field|uuid
specifier|private
name|String
name|uuid
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
comment|/**    * Constructor.    * @param kind token kind.    * @param uri filesystem URI.    * @param owner token owner.    * @param renewer token renewer.    * @param origin origin text for diagnostics.    * @param encryptionSecrets encryption secrets to set.    */
DECL|method|AbstractS3ATokenIdentifier ( final Text kind, final URI uri, final Text owner, final Text renewer, final String origin, final EncryptionSecrets encryptionSecrets)
specifier|protected
name|AbstractS3ATokenIdentifier
parameter_list|(
specifier|final
name|Text
name|kind
parameter_list|,
specifier|final
name|URI
name|uri
parameter_list|,
specifier|final
name|Text
name|owner
parameter_list|,
specifier|final
name|Text
name|renewer
parameter_list|,
specifier|final
name|String
name|origin
parameter_list|,
specifier|final
name|EncryptionSecrets
name|encryptionSecrets
parameter_list|)
block|{
name|this
argument_list|(
name|kind
argument_list|,
name|owner
argument_list|,
operator|(
name|renewer
operator|!=
literal|null
condition|?
name|renewer
else|:
operator|new
name|Text
argument_list|()
operator|)
argument_list|,
operator|new
name|Text
argument_list|()
argument_list|,
name|uri
argument_list|)
expr_stmt|;
name|this
operator|.
name|origin
operator|=
name|requireNonNull
argument_list|(
name|origin
argument_list|)
expr_stmt|;
name|this
operator|.
name|encryptionSecrets
operator|=
name|requireNonNull
argument_list|(
name|encryptionSecrets
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructor.    * @param kind token kind.    * @param owner token owner    * @param renewer token renewer    * @param realUser token real user    * @param uri filesystem URI.    */
DECL|method|AbstractS3ATokenIdentifier ( final Text kind, final Text owner, final Text renewer, final Text realUser, final URI uri)
specifier|protected
name|AbstractS3ATokenIdentifier
parameter_list|(
specifier|final
name|Text
name|kind
parameter_list|,
specifier|final
name|Text
name|owner
parameter_list|,
specifier|final
name|Text
name|renewer
parameter_list|,
specifier|final
name|Text
name|realUser
parameter_list|,
specifier|final
name|URI
name|uri
parameter_list|)
block|{
name|super
argument_list|(
name|kind
argument_list|,
name|owner
argument_list|,
name|renewer
argument_list|,
name|realUser
argument_list|)
expr_stmt|;
name|this
operator|.
name|uri
operator|=
name|requireNonNull
argument_list|(
name|uri
argument_list|)
expr_stmt|;
block|}
comment|/**    * Build from a token.    * This has been written for refresh operations;    * if someone implements refresh it will be relevant.    * @param kind token kind.    * @param token to to build from    * @throws IOException failure to build the identifier.    */
DECL|method|AbstractS3ATokenIdentifier ( final Text kind, final Token<AbstractS3ATokenIdentifier> token)
specifier|protected
name|AbstractS3ATokenIdentifier
parameter_list|(
specifier|final
name|Text
name|kind
parameter_list|,
specifier|final
name|Token
argument_list|<
name|AbstractS3ATokenIdentifier
argument_list|>
name|token
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|kind
argument_list|)
expr_stmt|;
name|ByteArrayInputStream
name|bais
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|token
operator|.
name|getIdentifier
argument_list|()
argument_list|)
decl_stmt|;
name|readFields
argument_list|(
operator|new
name|DataInputStream
argument_list|(
name|bais
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * For subclasses to use in their own empty-constructors.    * @param kind token kind.    */
DECL|method|AbstractS3ATokenIdentifier (final Text kind)
specifier|protected
name|AbstractS3ATokenIdentifier
parameter_list|(
specifier|final
name|Text
name|kind
parameter_list|)
block|{
name|super
argument_list|(
name|kind
argument_list|)
expr_stmt|;
block|}
DECL|method|getBucket ()
specifier|public
name|String
name|getBucket
parameter_list|()
block|{
return|return
name|uri
operator|.
name|getHost
argument_list|()
return|;
block|}
DECL|method|getUri ()
specifier|public
name|URI
name|getUri
parameter_list|()
block|{
return|return
name|uri
return|;
block|}
DECL|method|getOrigin ()
specifier|public
name|String
name|getOrigin
parameter_list|()
block|{
return|return
name|origin
return|;
block|}
DECL|method|setOrigin (final String origin)
specifier|public
name|void
name|setOrigin
parameter_list|(
specifier|final
name|String
name|origin
parameter_list|)
block|{
name|this
operator|.
name|origin
operator|=
name|origin
expr_stmt|;
block|}
DECL|method|getCreated ()
specifier|public
name|long
name|getCreated
parameter_list|()
block|{
return|return
name|created
return|;
block|}
comment|/**    * Write state.    * {@link org.apache.hadoop.io.Writable#write(DataOutput)}.    * @param out destination    * @throws IOException failure    */
annotation|@
name|Override
DECL|method|write (final DataOutput out)
specifier|public
name|void
name|write
parameter_list|(
specifier|final
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|Text
operator|.
name|writeString
argument_list|(
name|out
argument_list|,
name|uri
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Text
operator|.
name|writeString
argument_list|(
name|out
argument_list|,
name|origin
argument_list|)
expr_stmt|;
name|Text
operator|.
name|writeString
argument_list|(
name|out
argument_list|,
name|uuid
argument_list|)
expr_stmt|;
name|encryptionSecrets
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|created
argument_list|)
expr_stmt|;
block|}
comment|/**    * Read state.    * {@link org.apache.hadoop.io.Writable#readFields(DataInput)}.    *    * Note: this operation gets called in toString() operations on tokens, so    * must either always succeed, or throw an IOException to trigger the    * catch and downgrade. RuntimeExceptions (e.g. Preconditions checks) are    * not to be used here for this reason.)    *    * @param in input stream    * @throws DelegationTokenIOException if the token binding is wrong.    * @throws IOException IO problems.    */
annotation|@
name|Override
DECL|method|readFields (final DataInput in)
specifier|public
name|void
name|readFields
parameter_list|(
specifier|final
name|DataInput
name|in
parameter_list|)
throws|throws
name|DelegationTokenIOException
throws|,
name|IOException
block|{
name|super
operator|.
name|readFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|uri
operator|=
name|URI
operator|.
name|create
argument_list|(
name|Text
operator|.
name|readString
argument_list|(
name|in
argument_list|,
name|MAX_TEXT_LENGTH
argument_list|)
argument_list|)
expr_stmt|;
name|origin
operator|=
name|Text
operator|.
name|readString
argument_list|(
name|in
argument_list|,
name|MAX_TEXT_LENGTH
argument_list|)
expr_stmt|;
name|uuid
operator|=
name|Text
operator|.
name|readString
argument_list|(
name|in
argument_list|,
name|MAX_TEXT_LENGTH
argument_list|)
expr_stmt|;
name|encryptionSecrets
operator|.
name|readFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|created
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
block|}
comment|/**    * Validate the token by looking at its fields.    * @throws IOException on failure.    */
DECL|method|validate ()
specifier|public
name|void
name|validate
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|uri
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|DelegationTokenIOException
argument_list|(
literal|"No URI in "
operator|+
name|this
argument_list|)
throw|;
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
specifier|final
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"S3ATokenIdentifier{"
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|getKind
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"; uri="
argument_list|)
operator|.
name|append
argument_list|(
name|uri
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"; timestamp="
argument_list|)
operator|.
name|append
argument_list|(
name|created
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"; renewer="
argument_list|)
operator|.
name|append
argument_list|(
name|getRenewer
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"; encryption="
argument_list|)
operator|.
name|append
argument_list|(
name|encryptionSecrets
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"; "
argument_list|)
operator|.
name|append
argument_list|(
name|uuid
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"; "
argument_list|)
operator|.
name|append
argument_list|(
name|origin
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|'}'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Equality check is on superclass and UUID only.    * @param o other.    * @return true if the base class considers them equal and the URIs match.    */
annotation|@
name|Override
DECL|method|equals (final Object o)
specifier|public
name|boolean
name|equals
parameter_list|(
specifier|final
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
name|super
operator|.
name|equals
argument_list|(
name|o
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
specifier|final
name|AbstractS3ATokenIdentifier
name|that
init|=
operator|(
name|AbstractS3ATokenIdentifier
operator|)
name|o
decl_stmt|;
return|return
name|Objects
operator|.
name|equals
argument_list|(
name|uuid
argument_list|,
name|that
operator|.
name|uuid
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|uri
argument_list|,
name|that
operator|.
name|uri
argument_list|)
return|;
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
name|Objects
operator|.
name|hash
argument_list|(
name|super
operator|.
name|hashCode
argument_list|()
argument_list|,
name|uri
argument_list|)
return|;
block|}
comment|/**    * Return the expiry time in seconds since 1970-01-01.    * @return the time when the session credential expire.    */
DECL|method|getExpiryTime ()
specifier|public
name|long
name|getExpiryTime
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
comment|/**    * Get the UUID of this token identifier.    * @return a UUID.    */
DECL|method|getUuid ()
specifier|public
name|String
name|getUuid
parameter_list|()
block|{
return|return
name|uuid
return|;
block|}
comment|/**    * Get the encryption secrets.    * @return the encryption secrets within this identifier.    */
DECL|method|getEncryptionSecrets ()
specifier|public
name|EncryptionSecrets
name|getEncryptionSecrets
parameter_list|()
block|{
return|return
name|encryptionSecrets
return|;
block|}
comment|/**    * Create the default origin text message with local hostname and    * timestamp.    * @return a string for token diagnostics.    */
DECL|method|createDefaultOriginMessage ()
specifier|public
specifier|static
name|String
name|createDefaultOriginMessage
parameter_list|()
block|{
return|return
name|String
operator|.
name|format
argument_list|(
literal|"Created on %s at time %s."
argument_list|,
name|NetUtils
operator|.
name|getHostname
argument_list|()
argument_list|,
name|java
operator|.
name|time
operator|.
name|Instant
operator|.
name|now
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

