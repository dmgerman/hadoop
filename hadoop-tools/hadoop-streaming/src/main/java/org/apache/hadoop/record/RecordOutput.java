begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.record
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|record
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
name|util
operator|.
name|TreeMap
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

begin_comment
comment|/**  * Interface that all the serializers have to implement.  *   * @deprecated Replaced by<a href="http://hadoop.apache.org/avro/">Avro</a>.  */
end_comment

begin_interface
annotation|@
name|Deprecated
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|interface|RecordOutput
specifier|public
interface|interface
name|RecordOutput
block|{
comment|/**    * Write a byte to serialized record.    * @param b Byte to be serialized    * @param tag Used by tagged serialization formats (such as XML)    * @throws IOException Indicates error in serialization    */
DECL|method|writeByte (byte b, String tag)
specifier|public
name|void
name|writeByte
parameter_list|(
name|byte
name|b
parameter_list|,
name|String
name|tag
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Write a boolean to serialized record.    * @param b Boolean to be serialized    * @param tag Used by tagged serialization formats (such as XML)    * @throws IOException Indicates error in serialization    */
DECL|method|writeBool (boolean b, String tag)
specifier|public
name|void
name|writeBool
parameter_list|(
name|boolean
name|b
parameter_list|,
name|String
name|tag
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Write an integer to serialized record.    * @param i Integer to be serialized    * @param tag Used by tagged serialization formats (such as XML)    * @throws IOException Indicates error in serialization    */
DECL|method|writeInt (int i, String tag)
specifier|public
name|void
name|writeInt
parameter_list|(
name|int
name|i
parameter_list|,
name|String
name|tag
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Write a long integer to serialized record.    * @param l Long to be serialized    * @param tag Used by tagged serialization formats (such as XML)    * @throws IOException Indicates error in serialization    */
DECL|method|writeLong (long l, String tag)
specifier|public
name|void
name|writeLong
parameter_list|(
name|long
name|l
parameter_list|,
name|String
name|tag
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Write a single-precision float to serialized record.    * @param f Float to be serialized    * @param tag Used by tagged serialization formats (such as XML)    * @throws IOException Indicates error in serialization    */
DECL|method|writeFloat (float f, String tag)
specifier|public
name|void
name|writeFloat
parameter_list|(
name|float
name|f
parameter_list|,
name|String
name|tag
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Write a double precision floating point number to serialized record.    * @param d Double to be serialized    * @param tag Used by tagged serialization formats (such as XML)    * @throws IOException Indicates error in serialization    */
DECL|method|writeDouble (double d, String tag)
specifier|public
name|void
name|writeDouble
parameter_list|(
name|double
name|d
parameter_list|,
name|String
name|tag
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Write a unicode string to serialized record.    * @param s String to be serialized    * @param tag Used by tagged serialization formats (such as XML)    * @throws IOException Indicates error in serialization    */
DECL|method|writeString (String s, String tag)
specifier|public
name|void
name|writeString
parameter_list|(
name|String
name|s
parameter_list|,
name|String
name|tag
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Write a buffer to serialized record.    * @param buf Buffer to be serialized    * @param tag Used by tagged serialization formats (such as XML)    * @throws IOException Indicates error in serialization    */
DECL|method|writeBuffer (Buffer buf, String tag)
specifier|public
name|void
name|writeBuffer
parameter_list|(
name|Buffer
name|buf
parameter_list|,
name|String
name|tag
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Mark the start of a record to be serialized.    * @param r Record to be serialized    * @param tag Used by tagged serialization formats (such as XML)    * @throws IOException Indicates error in serialization    */
DECL|method|startRecord (Record r, String tag)
specifier|public
name|void
name|startRecord
parameter_list|(
name|Record
name|r
parameter_list|,
name|String
name|tag
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Mark the end of a serialized record.    * @param r Record to be serialized    * @param tag Used by tagged serialization formats (such as XML)    * @throws IOException Indicates error in serialization    */
DECL|method|endRecord (Record r, String tag)
specifier|public
name|void
name|endRecord
parameter_list|(
name|Record
name|r
parameter_list|,
name|String
name|tag
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Mark the start of a vector to be serialized.    * @param v Vector to be serialized    * @param tag Used by tagged serialization formats (such as XML)    * @throws IOException Indicates error in serialization    */
DECL|method|startVector (ArrayList v, String tag)
specifier|public
name|void
name|startVector
parameter_list|(
name|ArrayList
name|v
parameter_list|,
name|String
name|tag
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Mark the end of a serialized vector.    * @param v Vector to be serialized    * @param tag Used by tagged serialization formats (such as XML)    * @throws IOException Indicates error in serialization    */
DECL|method|endVector (ArrayList v, String tag)
specifier|public
name|void
name|endVector
parameter_list|(
name|ArrayList
name|v
parameter_list|,
name|String
name|tag
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Mark the start of a map to be serialized.    * @param m Map to be serialized    * @param tag Used by tagged serialization formats (such as XML)    * @throws IOException Indicates error in serialization    */
DECL|method|startMap (TreeMap m, String tag)
specifier|public
name|void
name|startMap
parameter_list|(
name|TreeMap
name|m
parameter_list|,
name|String
name|tag
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Mark the end of a serialized map.    * @param m Map to be serialized    * @param tag Used by tagged serialization formats (such as XML)    * @throws IOException Indicates error in serialization    */
DECL|method|endMap (TreeMap m, String tag)
specifier|public
name|void
name|endMap
parameter_list|(
name|TreeMap
name|m
parameter_list|,
name|String
name|tag
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

