begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.raid
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|raid
package|;
end_package

begin_interface
DECL|interface|ErasureCode
specifier|public
interface|interface
name|ErasureCode
block|{
comment|/**    * Encodes the given message.    * @param message The data of the message. The data is present in the least    *                significant bits of each int. The number of data bits is    *                symbolSize(). The number of elements of message is    *                stripeSize().    * @param parity  (out) The information is present in the least    *                significant bits of each int. The number of parity bits is    *                symbolSize(). The number of elements in the code is    *                paritySize().    */
DECL|method|encode (int[] message, int[] parity)
specifier|public
name|void
name|encode
parameter_list|(
name|int
index|[]
name|message
parameter_list|,
name|int
index|[]
name|parity
parameter_list|)
function_decl|;
comment|/**    * Generates missing portions of data.    * @param data The message and parity. The parity should be placed in the    *             first part of the array. In each integer, the relevant portion    *             is present in the least significant bits of each int.    *             The number of elements in data is stripeSize() + paritySize().    * @param erasedLocations The indexes in data which are not available.    * @param erasedValues    (out)The decoded values corresponding to erasedLocations.    */
DECL|method|decode (int[] data, int[] erasedLocations, int[] erasedValues)
specifier|public
name|void
name|decode
parameter_list|(
name|int
index|[]
name|data
parameter_list|,
name|int
index|[]
name|erasedLocations
parameter_list|,
name|int
index|[]
name|erasedValues
parameter_list|)
function_decl|;
comment|/**    * The number of elements in the message.    */
DECL|method|stripeSize ()
specifier|public
name|int
name|stripeSize
parameter_list|()
function_decl|;
comment|/**    * The number of elements in the code.    */
DECL|method|paritySize ()
specifier|public
name|int
name|paritySize
parameter_list|()
function_decl|;
comment|/**    * Number of bits for each symbol.    */
DECL|method|symbolSize ()
specifier|public
name|int
name|symbolSize
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

