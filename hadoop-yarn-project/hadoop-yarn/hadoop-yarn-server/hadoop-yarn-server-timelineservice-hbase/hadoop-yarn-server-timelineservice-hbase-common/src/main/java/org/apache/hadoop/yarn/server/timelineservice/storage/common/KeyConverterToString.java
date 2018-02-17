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

begin_comment
comment|/**  * Interface which has to be implemented for encoding and decoding row keys or  * column qualifiers as string.  */
end_comment

begin_interface
DECL|interface|KeyConverterToString
specifier|public
interface|interface
name|KeyConverterToString
parameter_list|<
name|T
parameter_list|>
block|{
comment|/**    * Encode key as string.    * @param key of type T to be encoded as string.    * @return encoded value as string.    */
DECL|method|encodeAsString (T key)
name|String
name|encodeAsString
parameter_list|(
name|T
name|key
parameter_list|)
function_decl|;
comment|/**    * Decode row key from string to a key of type T.    * @param encodedKey string representation of row key    * @return type T which has been constructed after decoding string.    */
DECL|method|decodeFromString (String encodedKey)
name|T
name|decodeFromString
parameter_list|(
name|String
name|encodedKey
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

