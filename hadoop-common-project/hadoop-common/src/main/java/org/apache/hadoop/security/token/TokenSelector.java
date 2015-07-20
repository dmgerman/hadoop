begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.security.token
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
operator|.
name|token
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|Text
import|;
end_import

begin_comment
comment|/**  * Select token of type T from tokens for use with named service  *   * @param<T>  *          T extends TokenIdentifier  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|interface|TokenSelector
specifier|public
interface|interface
name|TokenSelector
parameter_list|<
name|T
extends|extends
name|TokenIdentifier
parameter_list|>
block|{
DECL|method|selectToken (Text service, Collection<Token<? extends TokenIdentifier>> tokens)
name|Token
argument_list|<
name|T
argument_list|>
name|selectToken
parameter_list|(
name|Text
name|service
parameter_list|,
name|Collection
argument_list|<
name|Token
argument_list|<
name|?
extends|extends
name|TokenIdentifier
argument_list|>
argument_list|>
name|tokens
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

