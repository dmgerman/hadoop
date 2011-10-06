begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.protocolR23Compatible
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|protocolR23Compatible
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
name|io
operator|.
name|DataInputBuffer
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
name|DataOutputBuffer
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
name|Stable
DECL|class|TokenWritable
specifier|public
class|class
name|TokenWritable
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
comment|/**    * Construct a token from the components.    * @param identifier the token identifier    * @param password the token's password    * @param kind the kind of token    * @param service the service for this token    */
DECL|method|TokenWritable (byte[] identifier, byte[] password, Text kind, Text service)
specifier|public
name|TokenWritable
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
name|identifier
expr_stmt|;
name|this
operator|.
name|password
operator|=
name|password
expr_stmt|;
name|this
operator|.
name|kind
operator|=
name|kind
expr_stmt|;
name|this
operator|.
name|service
operator|=
name|service
expr_stmt|;
block|}
comment|/**    * Default constructor    */
DECL|method|TokenWritable ()
specifier|public
name|TokenWritable
parameter_list|()
block|{
name|this
argument_list|(
operator|new
name|byte
index|[
literal|0
index|]
argument_list|,
operator|new
name|byte
index|[
literal|0
index|]
argument_list|,
operator|new
name|Text
argument_list|()
argument_list|,
operator|new
name|Text
argument_list|()
argument_list|)
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
literal|"Ident: "
argument_list|)
expr_stmt|;
name|addBinaryBuffer
argument_list|(
name|buffer
argument_list|,
name|identifier
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|", Kind: "
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
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

