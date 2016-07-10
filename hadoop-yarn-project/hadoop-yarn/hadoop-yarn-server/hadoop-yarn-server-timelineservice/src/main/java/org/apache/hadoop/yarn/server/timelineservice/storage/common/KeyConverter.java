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
comment|/**  * Interface which has to be implemented for encoding and decoding row keys and  * columns.  */
end_comment

begin_interface
DECL|interface|KeyConverter
specifier|public
interface|interface
name|KeyConverter
parameter_list|<
name|T
parameter_list|>
block|{
comment|/**    * Encodes a key as a byte array.    *    * @param key key to be encoded.    * @return a byte array.    */
DECL|method|encode (T key)
name|byte
index|[]
name|encode
parameter_list|(
name|T
name|key
parameter_list|)
function_decl|;
comment|/**    * Decodes a byte array and returns a key of type T.    *    * @param bytes byte representation    * @return an object(key) of type T which has been constructed after decoding    * the bytes.    */
DECL|method|decode (byte[] bytes)
name|T
name|decode
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

