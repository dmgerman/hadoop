begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.io.serializer
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|serializer
package|;
end_package

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
name|DataOutputStream
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
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
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
name|RawComparator
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
name|WritableComparable
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
name|WritableComparator
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
operator|.
name|StringUtils
import|;
end_import

begin_comment
comment|/**  * A {@link SerializationBase} for {@link Writable}s that delegates to  * {@link Writable#write(java.io.DataOutput)} and  * {@link Writable#readFields(java.io.DataInput)}.  */
end_comment

begin_class
DECL|class|WritableSerialization
specifier|public
class|class
name|WritableSerialization
extends|extends
name|SerializationBase
argument_list|<
name|Writable
argument_list|>
block|{
DECL|class|WritableDeserializer
specifier|static
class|class
name|WritableDeserializer
extends|extends
name|DeserializerBase
argument_list|<
name|Writable
argument_list|>
block|{
DECL|field|writableClass
specifier|private
name|Class
argument_list|<
name|?
argument_list|>
name|writableClass
decl_stmt|;
DECL|field|dataIn
specifier|private
name|DataInputStream
name|dataIn
decl_stmt|;
DECL|method|WritableDeserializer (Configuration conf, Class<?> c)
specifier|public
name|WritableDeserializer
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|Class
argument_list|<
name|?
argument_list|>
name|c
parameter_list|)
block|{
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|this
operator|.
name|writableClass
operator|=
name|c
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|open (InputStream in)
specifier|public
name|void
name|open
parameter_list|(
name|InputStream
name|in
parameter_list|)
block|{
if|if
condition|(
name|in
operator|instanceof
name|DataInputStream
condition|)
block|{
name|dataIn
operator|=
operator|(
name|DataInputStream
operator|)
name|in
expr_stmt|;
block|}
else|else
block|{
name|dataIn
operator|=
operator|new
name|DataInputStream
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|deserialize (Writable w)
specifier|public
name|Writable
name|deserialize
parameter_list|(
name|Writable
name|w
parameter_list|)
throws|throws
name|IOException
block|{
name|Writable
name|writable
decl_stmt|;
if|if
condition|(
name|w
operator|==
literal|null
condition|)
block|{
name|writable
operator|=
operator|(
name|Writable
operator|)
name|ReflectionUtils
operator|.
name|newInstance
argument_list|(
name|writableClass
argument_list|,
name|getConf
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|writable
operator|=
name|w
expr_stmt|;
block|}
name|writable
operator|.
name|readFields
argument_list|(
name|dataIn
argument_list|)
expr_stmt|;
return|return
name|writable
return|;
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|dataIn
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|WritableSerializer
specifier|static
class|class
name|WritableSerializer
extends|extends
name|SerializerBase
argument_list|<
name|Writable
argument_list|>
block|{
DECL|field|metadata
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|metadata
decl_stmt|;
DECL|field|dataOut
specifier|private
name|DataOutputStream
name|dataOut
decl_stmt|;
DECL|field|serializedClass
specifier|private
name|Class
argument_list|<
name|?
argument_list|>
name|serializedClass
decl_stmt|;
DECL|method|WritableSerializer (Configuration conf, Map<String, String> metadata)
specifier|public
name|WritableSerializer
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|metadata
parameter_list|)
block|{
name|this
operator|.
name|metadata
operator|=
name|metadata
expr_stmt|;
comment|// If this metadata specifies a serialized class, memoize the
comment|// class object for this.
name|String
name|className
init|=
name|this
operator|.
name|metadata
operator|.
name|get
argument_list|(
name|CLASS_KEY
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|className
condition|)
block|{
try|try
block|{
name|this
operator|.
name|serializedClass
operator|=
name|conf
operator|.
name|getClassByName
argument_list|(
name|className
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|cnfe
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|cnfe
argument_list|)
throw|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"the "
operator|+
name|CLASS_KEY
operator|+
literal|" metadata is missing, but is required."
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|open (OutputStream out)
specifier|public
name|void
name|open
parameter_list|(
name|OutputStream
name|out
parameter_list|)
block|{
if|if
condition|(
name|out
operator|instanceof
name|DataOutputStream
condition|)
block|{
name|dataOut
operator|=
operator|(
name|DataOutputStream
operator|)
name|out
expr_stmt|;
block|}
else|else
block|{
name|dataOut
operator|=
operator|new
name|DataOutputStream
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|serialize (Writable w)
specifier|public
name|void
name|serialize
parameter_list|(
name|Writable
name|w
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|serializedClass
operator|!=
name|w
operator|.
name|getClass
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Type mismatch in serialization: expected "
operator|+
name|serializedClass
operator|+
literal|"; received "
operator|+
name|w
operator|.
name|getClass
argument_list|()
argument_list|)
throw|;
block|}
name|w
operator|.
name|write
argument_list|(
name|dataOut
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|dataOut
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getMetadata ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getMetadata
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|metadata
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|accept (Map<String, String> metadata)
specifier|public
name|boolean
name|accept
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|metadata
parameter_list|)
block|{
name|String
name|intendedSerializer
init|=
name|metadata
operator|.
name|get
argument_list|(
name|SERIALIZATION_KEY
argument_list|)
decl_stmt|;
if|if
condition|(
name|intendedSerializer
operator|!=
literal|null
operator|&&
operator|!
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|intendedSerializer
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|Class
argument_list|<
name|?
argument_list|>
name|c
init|=
name|getClassFromMetadata
argument_list|(
name|metadata
argument_list|)
decl_stmt|;
return|return
name|c
operator|==
literal|null
condition|?
literal|false
else|:
name|Writable
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|c
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getSerializer (Map<String, String> metadata)
specifier|public
name|SerializerBase
argument_list|<
name|Writable
argument_list|>
name|getSerializer
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|metadata
parameter_list|)
block|{
return|return
operator|new
name|WritableSerializer
argument_list|(
name|getConf
argument_list|()
argument_list|,
name|metadata
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getDeserializer (Map<String, String> metadata)
specifier|public
name|DeserializerBase
argument_list|<
name|Writable
argument_list|>
name|getDeserializer
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|metadata
parameter_list|)
block|{
name|Class
argument_list|<
name|?
argument_list|>
name|c
init|=
name|getClassFromMetadata
argument_list|(
name|metadata
argument_list|)
decl_stmt|;
return|return
operator|new
name|WritableDeserializer
argument_list|(
name|getConf
argument_list|()
argument_list|,
name|c
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|getRawComparator (Map<String, String> metadata)
specifier|public
name|RawComparator
argument_list|<
name|Writable
argument_list|>
name|getRawComparator
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|metadata
parameter_list|)
block|{
name|Class
argument_list|<
name|?
argument_list|>
name|klazz
init|=
name|getClassFromMetadata
argument_list|(
name|metadata
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|klazz
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Cannot get comparator without "
operator|+
name|SerializationBase
operator|.
name|CLASS_KEY
operator|+
literal|" set in metadata"
argument_list|)
throw|;
block|}
return|return
operator|(
name|RawComparator
operator|)
name|WritableComparator
operator|.
name|get
argument_list|(
operator|(
name|Class
argument_list|<
name|WritableComparable
argument_list|>
operator|)
name|klazz
argument_list|)
return|;
block|}
block|}
end_class

end_unit

