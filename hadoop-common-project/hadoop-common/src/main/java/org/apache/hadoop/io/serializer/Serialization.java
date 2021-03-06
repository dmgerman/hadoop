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
comment|/**  *<p>  * Encapsulates a {@link Serializer}/{@link Deserializer} pair.  *</p>  * @param<T>  */
end_comment

begin_interface
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
name|Evolving
DECL|interface|Serialization
specifier|public
interface|interface
name|Serialization
parameter_list|<
name|T
parameter_list|>
block|{
comment|/**    * Allows clients to test whether this {@link Serialization}    * supports the given class.    */
DECL|method|accept (Class<?> c)
name|boolean
name|accept
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|c
parameter_list|)
function_decl|;
comment|/**    * @return a {@link Serializer} for the given class.    */
DECL|method|getSerializer (Class<T> c)
name|Serializer
argument_list|<
name|T
argument_list|>
name|getSerializer
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|c
parameter_list|)
function_decl|;
comment|/**    * @return a {@link Deserializer} for the given class.    */
DECL|method|getDeserializer (Class<T> c)
name|Deserializer
argument_list|<
name|T
argument_list|>
name|getDeserializer
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|c
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

