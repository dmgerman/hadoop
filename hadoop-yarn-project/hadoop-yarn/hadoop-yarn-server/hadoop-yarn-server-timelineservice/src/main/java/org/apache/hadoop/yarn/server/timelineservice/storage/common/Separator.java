begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timelineservice.storage.common
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|timelineservice
operator|.
name|storage
operator|.
name|common
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
name|hbase
operator|.
name|util
operator|.
name|Bytes
import|;
end_import

begin_comment
comment|/**  * Used to separate row qualifiers, column qualifiers and compount fields.  */
end_comment

begin_enum
DECL|enum|Separator
specifier|public
enum|enum
name|Separator
block|{
comment|/**    * separator in key or column qualifier fields    */
DECL|enumConstant|QUALIFIERS
name|QUALIFIERS
argument_list|(
literal|"!"
argument_list|,
literal|"%0$"
argument_list|)
block|,
comment|/**    * separator in values, and/or compound key/column qualifier fields.    */
DECL|enumConstant|VALUES
name|VALUES
argument_list|(
literal|"?"
argument_list|,
literal|"%1$"
argument_list|)
block|,
comment|/**    * separator in values, often used to avoid having these in qualifiers and    * names. Note that if we use HTML form encoding through URLEncoder, we end up    * getting a + for a space, which may already occur in strings, so we don't    * want that.    */
DECL|enumConstant|SPACE
name|SPACE
argument_list|(
literal|" "
argument_list|,
literal|"%2$"
argument_list|)
block|;
comment|/**    * The string value of this separator.    */
DECL|field|value
specifier|private
specifier|final
name|String
name|value
decl_stmt|;
comment|/**    * The URLEncoded version of this separator    */
DECL|field|encodedValue
specifier|private
specifier|final
name|String
name|encodedValue
decl_stmt|;
comment|/**    * The bye representation of value.    */
DECL|field|bytes
specifier|private
specifier|final
name|byte
index|[]
name|bytes
decl_stmt|;
comment|/**    * The value quoted so that it can be used as a safe regex    */
DECL|field|quotedValue
specifier|private
specifier|final
name|String
name|quotedValue
decl_stmt|;
DECL|field|EMPTY_BYTES
specifier|private
specifier|static
specifier|final
name|byte
index|[]
name|EMPTY_BYTES
init|=
operator|new
name|byte
index|[
literal|0
index|]
decl_stmt|;
comment|/**    * @param value of the separator to use. Cannot be null or empty string.    * @param encodedValue choose something that isn't likely to occur in the data    *          itself. Cannot be null or empty string.    */
DECL|method|Separator (String value, String encodedValue)
specifier|private
name|Separator
parameter_list|(
name|String
name|value
parameter_list|,
name|String
name|encodedValue
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
name|this
operator|.
name|encodedValue
operator|=
name|encodedValue
expr_stmt|;
comment|// validation
if|if
condition|(
name|value
operator|==
literal|null
operator|||
name|value
operator|.
name|length
argument_list|()
operator|==
literal|0
operator|||
name|encodedValue
operator|==
literal|null
operator|||
name|encodedValue
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Cannot create separator from null or empty string."
argument_list|)
throw|;
block|}
name|this
operator|.
name|bytes
operator|=
name|Bytes
operator|.
name|toBytes
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|this
operator|.
name|quotedValue
operator|=
name|Pattern
operator|.
name|quote
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
comment|/**    * @return the original value of the separator    */
DECL|method|getValue ()
specifier|public
name|String
name|getValue
parameter_list|()
block|{
return|return
name|value
return|;
block|}
comment|/**    * Used to make token safe to be used with this separator without collisions.    *    * @param token    * @return the token with any occurrences of this separator URLEncoded.    */
DECL|method|encode (String token)
specifier|public
name|String
name|encode
parameter_list|(
name|String
name|token
parameter_list|)
block|{
if|if
condition|(
name|token
operator|==
literal|null
operator|||
name|token
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|// Nothing to replace
return|return
name|token
return|;
block|}
return|return
name|token
operator|.
name|replace
argument_list|(
name|value
argument_list|,
name|encodedValue
argument_list|)
return|;
block|}
comment|/**    * @param token    * @return the token with any occurrences of the encoded separator replaced by    *         the separator itself.    */
DECL|method|decode (String token)
specifier|public
name|String
name|decode
parameter_list|(
name|String
name|token
parameter_list|)
block|{
if|if
condition|(
name|token
operator|==
literal|null
operator|||
name|token
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|// Nothing to replace
return|return
name|token
return|;
block|}
return|return
name|token
operator|.
name|replace
argument_list|(
name|encodedValue
argument_list|,
name|value
argument_list|)
return|;
block|}
comment|/**    * Encode the given separators in the token with their encoding equivalent.    * This means that when encoding is already present in the token itself, this    * is not a reversible process. See also {@link #decode(String, Separator...)}    *    * @param token containing possible separators that need to be encoded.    * @param separators to be encoded in the token with their URLEncoding    *          equivalent.    * @return non-null byte representation of the token with occurrences of the    *         separators encoded.    */
DECL|method|encode (String token, Separator... separators)
specifier|public
specifier|static
name|byte
index|[]
name|encode
parameter_list|(
name|String
name|token
parameter_list|,
name|Separator
modifier|...
name|separators
parameter_list|)
block|{
if|if
condition|(
name|token
operator|==
literal|null
condition|)
block|{
return|return
name|EMPTY_BYTES
return|;
block|}
name|String
name|result
init|=
name|token
decl_stmt|;
for|for
control|(
name|Separator
name|separator
range|:
name|separators
control|)
block|{
if|if
condition|(
name|separator
operator|!=
literal|null
condition|)
block|{
name|result
operator|=
name|separator
operator|.
name|encode
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|Bytes
operator|.
name|toBytes
argument_list|(
name|result
argument_list|)
return|;
block|}
comment|/**    * Decode the given separators in the token with their decoding equivalent.    * This means that when encoding is already present in the token itself, this    * is not a reversible process.    *    * @param token containing possible separators that need to be encoded.    * @param separators to be encoded in the token with their URLEncoding    *          equivalent.    * @return String representation of the token with occurrences of the URL    *         encoded separators decoded.    */
DECL|method|decode (byte[] token, Separator... separators)
specifier|public
specifier|static
name|String
name|decode
parameter_list|(
name|byte
index|[]
name|token
parameter_list|,
name|Separator
modifier|...
name|separators
parameter_list|)
block|{
if|if
condition|(
name|token
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|decode
argument_list|(
name|Bytes
operator|.
name|toString
argument_list|(
name|token
argument_list|)
argument_list|,
name|separators
argument_list|)
return|;
block|}
comment|/**    * Decode the given separators in the token with their decoding equivalent.    * This means that when encoding is already present in the token itself, this    * is not a reversible process.    *    * @param token containing possible separators that need to be encoded.    * @param separators to be encoded in the token with their URLEncoding    *          equivalent.    * @return String representation of the token with occurrences of the URL    *         encoded separators decoded.    */
DECL|method|decode (String token, Separator... separators)
specifier|public
specifier|static
name|String
name|decode
parameter_list|(
name|String
name|token
parameter_list|,
name|Separator
modifier|...
name|separators
parameter_list|)
block|{
if|if
condition|(
name|token
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|String
name|result
init|=
name|token
decl_stmt|;
for|for
control|(
name|Separator
name|separator
range|:
name|separators
control|)
block|{
if|if
condition|(
name|separator
operator|!=
literal|null
condition|)
block|{
name|result
operator|=
name|separator
operator|.
name|decode
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
comment|/**    * Returns a single byte array containing all of the individual arrays    * components separated by this separator.    *    * @param components    * @return byte array after joining the components    */
DECL|method|join (byte[]... components)
specifier|public
name|byte
index|[]
name|join
parameter_list|(
name|byte
index|[]
modifier|...
name|components
parameter_list|)
block|{
if|if
condition|(
name|components
operator|==
literal|null
operator|||
name|components
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
name|EMPTY_BYTES
return|;
block|}
name|int
name|finalSize
init|=
literal|0
decl_stmt|;
name|finalSize
operator|=
name|this
operator|.
name|value
operator|.
name|length
argument_list|()
operator|*
operator|(
name|components
operator|.
name|length
operator|-
literal|1
operator|)
expr_stmt|;
for|for
control|(
name|byte
index|[]
name|comp
range|:
name|components
control|)
block|{
if|if
condition|(
name|comp
operator|!=
literal|null
condition|)
block|{
name|finalSize
operator|+=
name|comp
operator|.
name|length
expr_stmt|;
block|}
block|}
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
name|finalSize
index|]
decl_stmt|;
name|int
name|offset
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|components
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|components
index|[
name|i
index|]
operator|!=
literal|null
condition|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|components
index|[
name|i
index|]
argument_list|,
literal|0
argument_list|,
name|buf
argument_list|,
name|offset
argument_list|,
name|components
index|[
name|i
index|]
operator|.
name|length
argument_list|)
expr_stmt|;
name|offset
operator|+=
name|components
index|[
name|i
index|]
operator|.
name|length
expr_stmt|;
block|}
if|if
condition|(
name|i
operator|<
operator|(
name|components
operator|.
name|length
operator|-
literal|1
operator|)
condition|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|this
operator|.
name|bytes
argument_list|,
literal|0
argument_list|,
name|buf
argument_list|,
name|offset
argument_list|,
name|this
operator|.
name|value
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|offset
operator|+=
name|this
operator|.
name|value
operator|.
name|length
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|buf
return|;
block|}
comment|/**    * Concatenates items (as String), using this separator.    *    * @param items Items join, {@code toString()} will be called in each item.    *          Any occurrence of the separator in the individual strings will be    *          first encoded. Cannot be null.    * @return non-null joined result. Note that when separator is {@literal null}    *         the result is simply all items concatenated and the process is not    *         reversible through {@link #splitEncoded(String)}    */
DECL|method|joinEncoded (String... items)
specifier|public
name|String
name|joinEncoded
parameter_list|(
name|String
modifier|...
name|items
parameter_list|)
block|{
if|if
condition|(
name|items
operator|==
literal|null
operator|||
name|items
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
literal|""
return|;
block|}
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
name|encode
argument_list|(
name|items
index|[
literal|0
index|]
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
comment|// Start at 1, we've already grabbed the first value at index 0
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|items
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|this
operator|.
name|value
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|encode
argument_list|(
name|items
index|[
name|i
index|]
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Concatenates items (as String), using this separator.    *    * @param items Items join, {@code toString()} will be called in each item.    *          Any occurrence of the separator in the individual strings will be    *          first encoded. Cannot be null.    * @return non-null joined result. Note that when separator is {@literal null}    *         the result is simply all items concatenated and the process is not    *         reversible through {@link #splitEncoded(String)}    */
DECL|method|joinEncoded (Iterable<?> items)
specifier|public
name|String
name|joinEncoded
parameter_list|(
name|Iterable
argument_list|<
name|?
argument_list|>
name|items
parameter_list|)
block|{
if|if
condition|(
name|items
operator|==
literal|null
condition|)
block|{
return|return
literal|""
return|;
block|}
name|Iterator
argument_list|<
name|?
argument_list|>
name|i
init|=
name|items
operator|.
name|iterator
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|i
operator|.
name|hasNext
argument_list|()
condition|)
block|{
return|return
literal|""
return|;
block|}
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
name|encode
argument_list|(
name|i
operator|.
name|next
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
while|while
condition|(
name|i
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|this
operator|.
name|value
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|encode
argument_list|(
name|i
operator|.
name|next
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * @param compoundValue containing individual values separated by this    *          separator, which have that separator encoded.    * @return non-null set of values from the compoundValue with the separator    *         decoded.    */
DECL|method|splitEncoded (String compoundValue)
specifier|public
name|Collection
argument_list|<
name|String
argument_list|>
name|splitEncoded
parameter_list|(
name|String
name|compoundValue
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|compoundValue
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|value
range|:
name|compoundValue
operator|.
name|split
argument_list|(
name|quotedValue
argument_list|)
control|)
block|{
name|result
operator|.
name|add
argument_list|(
name|decode
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
comment|/**    * Splits the source array into multiple array segments using this separator,    * up to a maximum of count items. This will naturally produce copied byte    * arrays for each of the split segments.    * @param source to be split    * @param limit on how many segments are supposed to be returned. Negative    *          value indicates no limit on number of segments.    * @return source split by this separator.    */
DECL|method|split (byte[] source, int limit)
specifier|public
name|byte
index|[]
index|[]
name|split
parameter_list|(
name|byte
index|[]
name|source
parameter_list|,
name|int
name|limit
parameter_list|)
block|{
return|return
name|TimelineWriterUtils
operator|.
name|split
argument_list|(
name|source
argument_list|,
name|this
operator|.
name|bytes
argument_list|,
name|limit
argument_list|)
return|;
block|}
block|}
end_enum

end_unit

