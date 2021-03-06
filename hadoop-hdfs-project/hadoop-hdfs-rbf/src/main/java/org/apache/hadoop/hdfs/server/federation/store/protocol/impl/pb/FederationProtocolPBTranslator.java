begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.federation.store.protocol.impl.pb
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|protocol
operator|.
name|impl
operator|.
name|pb
package|;
end_package

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
name|lang
operator|.
name|reflect
operator|.
name|Method
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
name|com
operator|.
name|google
operator|.
name|protobuf
operator|.
name|GeneratedMessageV3
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|protobuf
operator|.
name|Message
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|protobuf
operator|.
name|Message
operator|.
name|Builder
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|protobuf
operator|.
name|MessageOrBuilder
import|;
end_import

begin_comment
comment|/**  * Helper class for setting/getting data elements in an object backed by a  * protobuf implementation.  */
end_comment

begin_class
DECL|class|FederationProtocolPBTranslator
specifier|public
class|class
name|FederationProtocolPBTranslator
parameter_list|<
name|P
extends|extends
name|GeneratedMessageV3
parameter_list|,
name|B
extends|extends
name|Builder
parameter_list|,
name|T
extends|extends
name|MessageOrBuilder
parameter_list|>
block|{
comment|/** Optional proto byte stream used to create this object. */
DECL|field|proto
specifier|private
name|P
name|proto
decl_stmt|;
comment|/** The class of the proto handler for this translator. */
DECL|field|protoClass
specifier|private
name|Class
argument_list|<
name|P
argument_list|>
name|protoClass
decl_stmt|;
comment|/** Internal builder, used to store data that has been set. */
DECL|field|builder
specifier|private
name|B
name|builder
decl_stmt|;
DECL|method|FederationProtocolPBTranslator (Class<P> protoType)
specifier|public
name|FederationProtocolPBTranslator
parameter_list|(
name|Class
argument_list|<
name|P
argument_list|>
name|protoType
parameter_list|)
block|{
name|this
operator|.
name|protoClass
operator|=
name|protoType
expr_stmt|;
block|}
comment|/**    * Called if this translator is to be created from an existing protobuf byte    * stream.    *    * @param p The existing proto object to use to initialize the translator.    * @throws IllegalArgumentException    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|setProto (Message p)
specifier|public
name|void
name|setProto
parameter_list|(
name|Message
name|p
parameter_list|)
block|{
if|if
condition|(
name|protoClass
operator|.
name|isInstance
argument_list|(
name|p
argument_list|)
condition|)
block|{
if|if
condition|(
name|this
operator|.
name|builder
operator|!=
literal|null
condition|)
block|{
comment|// Merge with builder
name|this
operator|.
name|builder
operator|.
name|mergeFrom
argument_list|(
operator|(
name|P
operator|)
name|p
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Store proto
name|this
operator|.
name|proto
operator|=
operator|(
name|P
operator|)
name|p
expr_stmt|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Cannot decode proto type "
operator|+
name|p
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|/**    * Create or return the cached protobuf builder for this translator.    *    * @return cached Builder instance    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|getBuilder ()
specifier|public
name|B
name|getBuilder
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|builder
operator|==
literal|null
condition|)
block|{
try|try
block|{
name|Method
name|method
init|=
name|protoClass
operator|.
name|getMethod
argument_list|(
literal|"newBuilder"
argument_list|)
decl_stmt|;
name|this
operator|.
name|builder
operator|=
operator|(
name|B
operator|)
name|method
operator|.
name|invoke
argument_list|(
literal|null
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|proto
operator|!=
literal|null
condition|)
block|{
comment|// Merge in existing immutable proto
name|this
operator|.
name|builder
operator|.
name|mergeFrom
argument_list|(
name|this
operator|.
name|proto
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|ReflectiveOperationException
name|e
parameter_list|)
block|{
name|this
operator|.
name|builder
operator|=
literal|null
expr_stmt|;
block|}
block|}
return|return
name|this
operator|.
name|builder
return|;
block|}
comment|/**    * Get the serialized proto object. If the translator was created from a byte    * stream, returns the intitial byte stream. Otherwise creates a new byte    * stream from the cached builder.    *    * @return Protobuf message object    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|build ()
specifier|public
name|P
name|build
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|builder
operator|!=
literal|null
condition|)
block|{
comment|// serialize from builder (mutable) first
name|Message
name|m
init|=
name|this
operator|.
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
return|return
operator|(
name|P
operator|)
name|m
return|;
block|}
elseif|else
if|if
condition|(
name|this
operator|.
name|proto
operator|!=
literal|null
condition|)
block|{
comment|// Use immutable message source, message is unchanged
return|return
name|this
operator|.
name|proto
return|;
block|}
return|return
literal|null
return|;
block|}
comment|/**    * Returns an interface to access data stored within this object. The object    * may have been initialized either via a builder or by an existing protobuf    * byte stream.    *    * @return MessageOrBuilder protobuf interface for the requested class.    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|getProtoOrBuilder ()
specifier|public
name|T
name|getProtoOrBuilder
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|builder
operator|!=
literal|null
condition|)
block|{
comment|// Use mutable builder if it exists
return|return
operator|(
name|T
operator|)
name|this
operator|.
name|builder
return|;
block|}
elseif|else
if|if
condition|(
name|this
operator|.
name|proto
operator|!=
literal|null
condition|)
block|{
comment|// Use immutable message source
return|return
operator|(
name|T
operator|)
name|this
operator|.
name|proto
return|;
block|}
else|else
block|{
comment|// Construct empty builder
return|return
operator|(
name|T
operator|)
name|this
operator|.
name|getBuilder
argument_list|()
return|;
block|}
block|}
comment|/**    * Read instance from base64 data.    * @param base64String    * @throws IOException    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|readInstance (String base64String)
specifier|public
name|void
name|readInstance
parameter_list|(
name|String
name|base64String
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|bytes
init|=
name|Base64
operator|.
name|decodeBase64
argument_list|(
name|base64String
argument_list|)
decl_stmt|;
name|Message
name|msg
init|=
name|getBuilder
argument_list|()
operator|.
name|mergeFrom
argument_list|(
name|bytes
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|this
operator|.
name|proto
operator|=
operator|(
name|P
operator|)
name|msg
expr_stmt|;
block|}
block|}
end_class

end_unit

