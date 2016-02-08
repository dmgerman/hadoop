begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * Converter used to encode/decode value associated with a column prefix or a  * column.  */
end_comment

begin_interface
DECL|interface|ValueConverter
specifier|public
interface|interface
name|ValueConverter
block|{
comment|/**    * Encode an object as a byte array depending on the converter implementation.    *    * @param value Value to be encoded.    * @return a byte array    * @throws IOException if any problem is encountered while encoding.    */
DECL|method|encodeValue (Object value)
name|byte
index|[]
name|encodeValue
parameter_list|(
name|Object
name|value
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Decode a byte array and convert it into an object depending on the    * converter implementation.    *    * @param bytes Byte array to be decoded.    * @return an object    * @throws IOException if any problem is encountered while decoding.    */
DECL|method|decodeValue (byte[] bytes)
name|Object
name|decodeValue
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

