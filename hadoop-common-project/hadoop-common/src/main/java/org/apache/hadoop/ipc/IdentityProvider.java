begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ipc
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ipc
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

begin_comment
comment|/**  * The IdentityProvider creates identities for each schedulable  * by extracting fields and returning an identity string.  *  * Implementers will be able to change how schedulers treat  * Schedulables.  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|interface|IdentityProvider
specifier|public
interface|interface
name|IdentityProvider
block|{
comment|/**    * Return the string used for scheduling.    * @param obj the schedulable to use.    * @return string identity, or null if no identity could be made.    */
DECL|method|makeIdentity (Schedulable obj)
specifier|public
name|String
name|makeIdentity
parameter_list|(
name|Schedulable
name|obj
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

