begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
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
comment|/**  * The base interface which various FileSystem FileContext Builder  * interfaces can extend, and which underlying implementations  * will then implement.  * @param<S> Return type on the {@link #build()} call.  * @param<B> type of builder itself.  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|interface|FSBuilder
specifier|public
interface|interface
name|FSBuilder
parameter_list|<
name|S
parameter_list|,
name|B
extends|extends
name|FSBuilder
parameter_list|<
name|S
parameter_list|,
name|B
parameter_list|>
parameter_list|>
block|{
comment|/**    * Set optional Builder parameter.    */
DECL|method|opt (@onnull String key, @Nonnull String value)
name|B
name|opt
parameter_list|(
annotation|@
name|Nonnull
name|String
name|key
parameter_list|,
annotation|@
name|Nonnull
name|String
name|value
parameter_list|)
function_decl|;
comment|/**    * Set optional boolean parameter for the Builder.    *    * @see #opt(String, String)    */
DECL|method|opt (@onnull String key, boolean value)
name|B
name|opt
parameter_list|(
annotation|@
name|Nonnull
name|String
name|key
parameter_list|,
name|boolean
name|value
parameter_list|)
function_decl|;
comment|/**    * Set optional int parameter for the Builder.    *    * @see #opt(String, String)    */
DECL|method|opt (@onnull String key, int value)
name|B
name|opt
parameter_list|(
annotation|@
name|Nonnull
name|String
name|key
parameter_list|,
name|int
name|value
parameter_list|)
function_decl|;
comment|/**    * Set optional float parameter for the Builder.    *    * @see #opt(String, String)    */
DECL|method|opt (@onnull String key, float value)
name|B
name|opt
parameter_list|(
annotation|@
name|Nonnull
name|String
name|key
parameter_list|,
name|float
name|value
parameter_list|)
function_decl|;
comment|/**    * Set optional double parameter for the Builder.    *    * @see #opt(String, String)    */
DECL|method|opt (@onnull String key, double value)
name|B
name|opt
parameter_list|(
annotation|@
name|Nonnull
name|String
name|key
parameter_list|,
name|double
name|value
parameter_list|)
function_decl|;
comment|/**    * Set an array of string values as optional parameter for the Builder.    *    * @see #opt(String, String)    */
DECL|method|opt (@onnull String key, @Nonnull String... values)
name|B
name|opt
parameter_list|(
annotation|@
name|Nonnull
name|String
name|key
parameter_list|,
annotation|@
name|Nonnull
name|String
modifier|...
name|values
parameter_list|)
function_decl|;
comment|/**    * Set mandatory option to the Builder.    *    * If the option is not supported or unavailable,    * the client should expect {@link #build()} throws IllegalArgumentException.    */
DECL|method|must (@onnull String key, @Nonnull String value)
name|B
name|must
parameter_list|(
annotation|@
name|Nonnull
name|String
name|key
parameter_list|,
annotation|@
name|Nonnull
name|String
name|value
parameter_list|)
function_decl|;
comment|/**    * Set mandatory boolean option.    *    * @see #must(String, String)    */
DECL|method|must (@onnull String key, boolean value)
name|B
name|must
parameter_list|(
annotation|@
name|Nonnull
name|String
name|key
parameter_list|,
name|boolean
name|value
parameter_list|)
function_decl|;
comment|/**    * Set mandatory int option.    *    * @see #must(String, String)    */
DECL|method|must (@onnull String key, int value)
name|B
name|must
parameter_list|(
annotation|@
name|Nonnull
name|String
name|key
parameter_list|,
name|int
name|value
parameter_list|)
function_decl|;
comment|/**    * Set mandatory float option.    *    * @see #must(String, String)    */
DECL|method|must (@onnull String key, float value)
name|B
name|must
parameter_list|(
annotation|@
name|Nonnull
name|String
name|key
parameter_list|,
name|float
name|value
parameter_list|)
function_decl|;
comment|/**    * Set mandatory double option.    *    * @see #must(String, String)    */
DECL|method|must (@onnull String key, double value)
name|B
name|must
parameter_list|(
annotation|@
name|Nonnull
name|String
name|key
parameter_list|,
name|double
name|value
parameter_list|)
function_decl|;
comment|/**    * Set a string array as mandatory option.    *    * @see #must(String, String)    */
DECL|method|must (@onnull String key, @Nonnull String... values)
name|B
name|must
parameter_list|(
annotation|@
name|Nonnull
name|String
name|key
parameter_list|,
annotation|@
name|Nonnull
name|String
modifier|...
name|values
parameter_list|)
function_decl|;
comment|/**    * Instantiate the object which was being built.    *    * @throws IllegalArgumentException if the parameters are not valid.    * @throws UnsupportedOperationException if the filesystem does not support    * the specific operation.    * @throws IOException on filesystem IO errors.    */
DECL|method|build ()
name|S
name|build
parameter_list|()
throws|throws
name|IllegalArgumentException
throws|,
name|UnsupportedOperationException
throws|,
name|IOException
function_decl|;
block|}
end_interface

end_unit

