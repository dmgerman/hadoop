begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.tools.rumen.state
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|tools
operator|.
name|rumen
operator|.
name|state
package|;
end_package

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|annotation
operator|.
name|JsonIgnore
import|;
end_import

begin_comment
comment|/**  * Represents a state. This state is managed by {@link StatePool}.  *   * Note that a {@link State} objects should be persistable. Currently, the   * {@link State} objects are persisted using the Jackson JSON library. Hence the  * implementors of the {@link State} interface should be careful while defining   * their public setter and getter APIs.    */
end_comment

begin_interface
DECL|interface|State
specifier|public
interface|interface
name|State
block|{
comment|/**    * Returns true if the state is updated since creation (or reload).    */
annotation|@
name|JsonIgnore
DECL|method|isUpdated ()
name|boolean
name|isUpdated
parameter_list|()
function_decl|;
comment|/**    * Get the name of the state.    */
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
function_decl|;
comment|/**    * Set the name of the state.    */
DECL|method|setName (String name)
specifier|public
name|void
name|setName
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

