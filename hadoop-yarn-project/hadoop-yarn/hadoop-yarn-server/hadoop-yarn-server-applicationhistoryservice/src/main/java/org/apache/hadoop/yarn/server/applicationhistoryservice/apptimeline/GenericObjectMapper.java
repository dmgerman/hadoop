begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.applicationhistoryservice.apptimeline
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
name|applicationhistoryservice
operator|.
name|apptimeline
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
name|WritableUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jackson
operator|.
name|map
operator|.
name|ObjectMapper
import|;
end_import

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
name|ByteArrayOutputStream
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
name|HashMap
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
import|;
end_import

begin_comment
comment|/**  * A utility class providing methods for serializing and deserializing  * objects. The {@link #write(Object)}, {@link #read(byte[])} and {@link  * #write(java.io.DataOutputStream, Object)}, {@link  * #read(java.io.DataInputStream)} methods are used by the  * {@link LeveldbApplicationTimelineStore} to store and retrieve arbitrary  * JSON, while the {@link #writeReverseOrderedLong} and {@link  * #readReverseOrderedLong} methods are used to sort entities in descending  * start time order.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|GenericObjectMapper
specifier|public
class|class
name|GenericObjectMapper
block|{
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
DECL|field|LONG
specifier|private
specifier|static
specifier|final
name|byte
name|LONG
init|=
literal|0x1
decl_stmt|;
DECL|field|INTEGER
specifier|private
specifier|static
specifier|final
name|byte
name|INTEGER
init|=
literal|0x2
decl_stmt|;
DECL|field|DOUBLE
specifier|private
specifier|static
specifier|final
name|byte
name|DOUBLE
init|=
literal|0x3
decl_stmt|;
DECL|field|STRING
specifier|private
specifier|static
specifier|final
name|byte
name|STRING
init|=
literal|0x4
decl_stmt|;
DECL|field|BOOLEAN
specifier|private
specifier|static
specifier|final
name|byte
name|BOOLEAN
init|=
literal|0x5
decl_stmt|;
DECL|field|LIST
specifier|private
specifier|static
specifier|final
name|byte
name|LIST
init|=
literal|0x6
decl_stmt|;
DECL|field|MAP
specifier|private
specifier|static
specifier|final
name|byte
name|MAP
init|=
literal|0x7
decl_stmt|;
comment|/**    * Serializes an Object into a byte array. Along with {@link #read(byte[]) },    * can be used to serialize an Object and deserialize it into an Object of    * the same type without needing to specify the Object's type,    * as long as it is one of the JSON-compatible objects Long, Integer,    * Double, String, Boolean, List, or Map.  The current implementation uses    * ObjectMapper to serialize complex objects (List and Map) while using    * Writable to serialize simpler objects, to produce fewer bytes.    *    * @param o An Object    * @return A byte array representation of the Object    * @throws IOException    */
DECL|method|write (Object o)
specifier|public
specifier|static
name|byte
index|[]
name|write
parameter_list|(
name|Object
name|o
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|o
operator|==
literal|null
condition|)
return|return
name|EMPTY_BYTES
return|;
name|ByteArrayOutputStream
name|baos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|write
argument_list|(
operator|new
name|DataOutputStream
argument_list|(
name|baos
argument_list|)
argument_list|,
name|o
argument_list|)
expr_stmt|;
return|return
name|baos
operator|.
name|toByteArray
argument_list|()
return|;
block|}
comment|/**    * Serializes an Object and writes it to a DataOutputStream. Along with    * {@link #read(java.io.DataInputStream)}, can be used to serialize an Object    * and deserialize it into an Object of the same type without needing to    * specify the Object's type, as long as it is one of the JSON-compatible    * objects Long, Integer, Double, String, Boolean, List, or Map. The current    * implementation uses ObjectMapper to serialize complex objects (List and    * Map) while using Writable to serialize simpler objects, to produce fewer    * bytes.    *    * @param dos A DataOutputStream    * @param o An Object    * @throws IOException    */
DECL|method|write (DataOutputStream dos, Object o)
specifier|public
specifier|static
name|void
name|write
parameter_list|(
name|DataOutputStream
name|dos
parameter_list|,
name|Object
name|o
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|o
operator|==
literal|null
condition|)
return|return;
if|if
condition|(
name|o
operator|instanceof
name|Long
condition|)
block|{
name|dos
operator|.
name|write
argument_list|(
name|LONG
argument_list|)
expr_stmt|;
name|WritableUtils
operator|.
name|writeVLong
argument_list|(
name|dos
argument_list|,
operator|(
name|Long
operator|)
name|o
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|o
operator|instanceof
name|Integer
condition|)
block|{
name|dos
operator|.
name|write
argument_list|(
name|INTEGER
argument_list|)
expr_stmt|;
name|WritableUtils
operator|.
name|writeVInt
argument_list|(
name|dos
argument_list|,
operator|(
name|Integer
operator|)
name|o
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|o
operator|instanceof
name|Double
condition|)
block|{
name|dos
operator|.
name|write
argument_list|(
name|DOUBLE
argument_list|)
expr_stmt|;
name|dos
operator|.
name|writeDouble
argument_list|(
operator|(
name|Double
operator|)
name|o
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|o
operator|instanceof
name|String
condition|)
block|{
name|dos
operator|.
name|write
argument_list|(
name|STRING
argument_list|)
expr_stmt|;
name|WritableUtils
operator|.
name|writeString
argument_list|(
name|dos
argument_list|,
operator|(
name|String
operator|)
name|o
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|o
operator|instanceof
name|Boolean
condition|)
block|{
name|dos
operator|.
name|write
argument_list|(
name|BOOLEAN
argument_list|)
expr_stmt|;
name|dos
operator|.
name|writeBoolean
argument_list|(
operator|(
name|Boolean
operator|)
name|o
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|o
operator|instanceof
name|List
condition|)
block|{
name|dos
operator|.
name|write
argument_list|(
name|LIST
argument_list|)
expr_stmt|;
name|ObjectMapper
name|mapper
init|=
operator|new
name|ObjectMapper
argument_list|()
decl_stmt|;
name|mapper
operator|.
name|writeValue
argument_list|(
name|dos
argument_list|,
name|o
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|o
operator|instanceof
name|Map
condition|)
block|{
name|dos
operator|.
name|write
argument_list|(
name|MAP
argument_list|)
expr_stmt|;
name|ObjectMapper
name|mapper
init|=
operator|new
name|ObjectMapper
argument_list|()
decl_stmt|;
name|mapper
operator|.
name|writeValue
argument_list|(
name|dos
argument_list|,
name|o
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Couldn't serialize object"
argument_list|)
throw|;
block|}
block|}
comment|/**    * Deserializes an Object from a byte array created with    * {@link #write(Object)}.    *    * @param b A byte array    * @return An Object    * @throws IOException    */
DECL|method|read (byte[] b)
specifier|public
specifier|static
name|Object
name|read
parameter_list|(
name|byte
index|[]
name|b
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|b
operator|==
literal|null
operator|||
name|b
operator|.
name|length
operator|==
literal|0
condition|)
return|return
literal|null
return|;
name|ByteArrayInputStream
name|bais
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|b
argument_list|)
decl_stmt|;
return|return
name|read
argument_list|(
operator|new
name|DataInputStream
argument_list|(
name|bais
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Reads an Object from a DataInputStream whose data has been written with    * {@link #write(java.io.DataOutputStream, Object)}.    *    * @param dis A DataInputStream    * @return An Object, null if an unrecognized type    * @throws IOException    */
DECL|method|read (DataInputStream dis)
specifier|public
specifier|static
name|Object
name|read
parameter_list|(
name|DataInputStream
name|dis
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
name|code
init|=
operator|(
name|byte
operator|)
name|dis
operator|.
name|read
argument_list|()
decl_stmt|;
name|ObjectMapper
name|mapper
decl_stmt|;
switch|switch
condition|(
name|code
condition|)
block|{
case|case
name|LONG
case|:
return|return
name|WritableUtils
operator|.
name|readVLong
argument_list|(
name|dis
argument_list|)
return|;
case|case
name|INTEGER
case|:
return|return
name|WritableUtils
operator|.
name|readVInt
argument_list|(
name|dis
argument_list|)
return|;
case|case
name|DOUBLE
case|:
return|return
name|dis
operator|.
name|readDouble
argument_list|()
return|;
case|case
name|STRING
case|:
return|return
name|WritableUtils
operator|.
name|readString
argument_list|(
name|dis
argument_list|)
return|;
case|case
name|BOOLEAN
case|:
return|return
name|dis
operator|.
name|readBoolean
argument_list|()
return|;
case|case
name|LIST
case|:
name|mapper
operator|=
operator|new
name|ObjectMapper
argument_list|()
expr_stmt|;
return|return
name|mapper
operator|.
name|readValue
argument_list|(
name|dis
argument_list|,
name|ArrayList
operator|.
name|class
argument_list|)
return|;
case|case
name|MAP
case|:
name|mapper
operator|=
operator|new
name|ObjectMapper
argument_list|()
expr_stmt|;
return|return
name|mapper
operator|.
name|readValue
argument_list|(
name|dis
argument_list|,
name|HashMap
operator|.
name|class
argument_list|)
return|;
default|default:
return|return
literal|null
return|;
block|}
block|}
comment|/**    * Converts a long to a 8-byte array so that lexicographic ordering of the    * produced byte arrays sort the longs in descending order.    *    * @param l A long    * @return A byte array    */
DECL|method|writeReverseOrderedLong (long l)
specifier|public
specifier|static
name|byte
index|[]
name|writeReverseOrderedLong
parameter_list|(
name|long
name|l
parameter_list|)
block|{
name|byte
index|[]
name|b
init|=
operator|new
name|byte
index|[
literal|8
index|]
decl_stmt|;
name|b
index|[
literal|0
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|0x7f
operator|^
operator|(
operator|(
name|l
operator|>>
literal|56
operator|)
operator|&
literal|0xff
operator|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
literal|7
condition|;
name|i
operator|++
control|)
name|b
index|[
name|i
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|0xff
operator|^
operator|(
operator|(
name|l
operator|>>
literal|8
operator|*
operator|(
literal|7
operator|-
name|i
operator|)
operator|)
operator|&
literal|0xff
operator|)
argument_list|)
expr_stmt|;
name|b
index|[
literal|7
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|0xff
operator|^
operator|(
name|l
operator|&
literal|0xff
operator|)
argument_list|)
expr_stmt|;
return|return
name|b
return|;
block|}
comment|/**    * Reads 8 bytes from an array starting at the specified offset and    * converts them to a long.  The bytes are assumed to have been created    * with {@link #writeReverseOrderedLong}.    *    * @param b A byte array    * @param offset An offset into the byte array    * @return A long    */
DECL|method|readReverseOrderedLong (byte[] b, int offset)
specifier|public
specifier|static
name|long
name|readReverseOrderedLong
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|offset
parameter_list|)
block|{
name|long
name|l
init|=
name|b
index|[
name|offset
index|]
operator|&
literal|0xff
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
literal|8
condition|;
name|i
operator|++
control|)
block|{
name|l
operator|=
name|l
operator|<<
literal|8
expr_stmt|;
name|l
operator|=
name|l
operator||
operator|(
name|b
index|[
name|offset
operator|+
name|i
index|]
operator|&
literal|0xff
operator|)
expr_stmt|;
block|}
return|return
name|l
operator|^
literal|0x7fffffffffffffffl
return|;
block|}
block|}
end_class

end_unit

