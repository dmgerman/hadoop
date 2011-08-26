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
comment|/**  * Interface that all the Deserializers have to implement.  *   * @deprecated Replaced by<a href="http://hadoop.apache.org/avro/">Avro</a>.  */
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
DECL|interface|RecordInput
specifier|public
interface|interface
name|RecordInput
block|{
comment|/**    * Read a byte from serialized record.    * @param tag Used by tagged serialization formats (such as XML)    * @return value read from serialized record.    */
DECL|method|readByte (String tag)
name|byte
name|readByte
parameter_list|(
name|String
name|tag
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Read a boolean from serialized record.    * @param tag Used by tagged serialization formats (such as XML)    * @return value read from serialized record.    */
DECL|method|readBool (String tag)
name|boolean
name|readBool
parameter_list|(
name|String
name|tag
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Read an integer from serialized record.    * @param tag Used by tagged serialization formats (such as XML)    * @return value read from serialized record.    */
DECL|method|readInt (String tag)
name|int
name|readInt
parameter_list|(
name|String
name|tag
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Read a long integer from serialized record.    * @param tag Used by tagged serialization formats (such as XML)    * @return value read from serialized record.    */
DECL|method|readLong (String tag)
name|long
name|readLong
parameter_list|(
name|String
name|tag
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Read a single-precision float from serialized record.    * @param tag Used by tagged serialization formats (such as XML)    * @return value read from serialized record.    */
DECL|method|readFloat (String tag)
name|float
name|readFloat
parameter_list|(
name|String
name|tag
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Read a double-precision number from serialized record.    * @param tag Used by tagged serialization formats (such as XML)    * @return value read from serialized record.    */
DECL|method|readDouble (String tag)
name|double
name|readDouble
parameter_list|(
name|String
name|tag
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Read a UTF-8 encoded string from serialized record.    * @param tag Used by tagged serialization formats (such as XML)    * @return value read from serialized record.    */
DECL|method|readString (String tag)
name|String
name|readString
parameter_list|(
name|String
name|tag
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Read byte array from serialized record.    * @param tag Used by tagged serialization formats (such as XML)    * @return value read from serialized record.    */
DECL|method|readBuffer (String tag)
name|Buffer
name|readBuffer
parameter_list|(
name|String
name|tag
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Check the mark for start of the serialized record.    * @param tag Used by tagged serialization formats (such as XML)    */
DECL|method|startRecord (String tag)
name|void
name|startRecord
parameter_list|(
name|String
name|tag
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Check the mark for end of the serialized record.    * @param tag Used by tagged serialization formats (such as XML)    */
DECL|method|endRecord (String tag)
name|void
name|endRecord
parameter_list|(
name|String
name|tag
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Check the mark for start of the serialized vector.    * @param tag Used by tagged serialization formats (such as XML)    * @return Index that is used to count the number of elements.    */
DECL|method|startVector (String tag)
name|Index
name|startVector
parameter_list|(
name|String
name|tag
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Check the mark for end of the serialized vector.    * @param tag Used by tagged serialization formats (such as XML)    */
DECL|method|endVector (String tag)
name|void
name|endVector
parameter_list|(
name|String
name|tag
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Check the mark for start of the serialized map.    * @param tag Used by tagged serialization formats (such as XML)    * @return Index that is used to count the number of map entries.    */
DECL|method|startMap (String tag)
name|Index
name|startMap
parameter_list|(
name|String
name|tag
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Check the mark for end of the serialized map.    * @param tag Used by tagged serialization formats (such as XML)    */
DECL|method|endMap (String tag)
name|void
name|endMap
parameter_list|(
name|String
name|tag
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

