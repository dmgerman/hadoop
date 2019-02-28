begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.azurebfs.extensions
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|azurebfs
operator|.
name|extensions
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
name|time
operator|.
name|Clock
import|;
end_import

begin_import
import|import
name|java
operator|.
name|time
operator|.
name|Instant
import|;
end_import

begin_import
import|import
name|java
operator|.
name|time
operator|.
name|temporal
operator|.
name|ChronoUnit
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
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

begin_comment
comment|/**  * Token identifier for testing ABFS DT support; matched with  * a service declaration so it can be unmarshalled.  */
end_comment

begin_class
DECL|class|StubAbfsTokenIdentifier
specifier|public
class|class
name|StubAbfsTokenIdentifier
extends|extends
name|DelegationTokenIdentifier
block|{
DECL|field|ID
specifier|public
specifier|static
specifier|final
name|String
name|ID
init|=
literal|"StubAbfsTokenIdentifier"
decl_stmt|;
DECL|field|MAX_TEXT_LENGTH
specifier|public
specifier|static
specifier|final
name|int
name|MAX_TEXT_LENGTH
init|=
literal|512
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
name|ID
argument_list|)
decl_stmt|;
comment|/** Canonical URI of the store. */
DECL|field|uri
specifier|private
name|URI
name|uri
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
comment|/**    * This is the constructor used for deserialization, so there's    * no need to fill in all values.    */
DECL|method|StubAbfsTokenIdentifier ()
specifier|public
name|StubAbfsTokenIdentifier
parameter_list|()
block|{
name|super
argument_list|(
name|TOKEN_KIND
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create.    * @param uri owner UI    * @param owner token owner    * @param renewer token renewer    */
DECL|method|StubAbfsTokenIdentifier ( final URI uri, final Text owner, final Text renewer)
specifier|public
name|StubAbfsTokenIdentifier
parameter_list|(
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
parameter_list|)
block|{
name|super
argument_list|(
name|TOKEN_KIND
argument_list|,
name|owner
argument_list|,
name|renewer
argument_list|,
operator|new
name|Text
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|uri
operator|=
name|uri
expr_stmt|;
name|Clock
name|clock
init|=
name|Clock
operator|.
name|systemDefaultZone
argument_list|()
decl_stmt|;
name|long
name|now
init|=
name|clock
operator|.
name|millis
argument_list|()
decl_stmt|;
name|Instant
name|nowTime
init|=
name|Instant
operator|.
name|ofEpochMilli
argument_list|(
name|now
argument_list|)
decl_stmt|;
name|setIssueDate
argument_list|(
name|now
argument_list|)
expr_stmt|;
name|setMaxDate
argument_list|(
name|nowTime
operator|.
name|plus
argument_list|(
literal|1
argument_list|,
name|ChronoUnit
operator|.
name|HOURS
argument_list|)
operator|.
name|toEpochMilli
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|decodeIdentifier (final Token<?> token)
specifier|public
specifier|static
name|StubAbfsTokenIdentifier
name|decodeIdentifier
parameter_list|(
specifier|final
name|Token
argument_list|<
name|?
argument_list|>
name|token
parameter_list|)
throws|throws
name|IOException
block|{
name|StubAbfsTokenIdentifier
name|id
init|=
operator|(
name|StubAbfsTokenIdentifier
operator|)
name|token
operator|.
name|decodeIdentifier
argument_list|()
decl_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|id
argument_list|,
literal|"Null decoded identifier"
argument_list|)
expr_stmt|;
return|return
name|id
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
name|uuid
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
comment|/**    * Read state.    * {@link org.apache.hadoop.io.Writable#readFields(DataInput)}.    *    * Note: this operation gets called in toString() operations on tokens, so    * must either always succeed, or throw an IOException to trigger the    * catch& downgrade. RuntimeExceptions (e.g. Preconditions checks) are    * not to be used here for this reason.)    *    * @param in input stream    * @throws IOException IO problems.    */
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
name|created
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
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
literal|"AbfsIDBTokenIdentifier{"
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"uri="
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
literal|", uuid='"
argument_list|)
operator|.
name|append
argument_list|(
name|uuid
argument_list|)
operator|.
name|append
argument_list|(
literal|'\''
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|", created='"
argument_list|)
operator|.
name|append
argument_list|(
operator|new
name|Date
argument_list|(
name|created
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|'\''
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
name|StubAbfsTokenIdentifier
name|that
init|=
operator|(
name|StubAbfsTokenIdentifier
operator|)
name|o
decl_stmt|;
return|return
name|created
operator|==
name|that
operator|.
name|created
operator|&&
name|uri
operator|.
name|equals
argument_list|(
name|that
operator|.
name|uri
argument_list|)
operator|&&
name|uuid
operator|.
name|equals
argument_list|(
name|that
operator|.
name|uuid
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
argument_list|,
name|uuid
argument_list|)
return|;
block|}
block|}
end_class

end_unit

