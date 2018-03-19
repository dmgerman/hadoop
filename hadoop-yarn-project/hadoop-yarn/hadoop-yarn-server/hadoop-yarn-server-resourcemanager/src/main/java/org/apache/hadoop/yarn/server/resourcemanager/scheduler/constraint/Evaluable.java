begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.constraint
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
name|resourcemanager
operator|.
name|scheduler
operator|.
name|constraint
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
name|yarn
operator|.
name|exceptions
operator|.
name|YarnException
import|;
end_import

begin_comment
comment|/**  * A class implements Evaluable interface represents the internal state  * of the class can be changed against a given target.  * @param<T> a target to evaluate against  */
end_comment

begin_interface
DECL|interface|Evaluable
specifier|public
interface|interface
name|Evaluable
parameter_list|<
name|T
parameter_list|>
block|{
comment|/**    * Evaluate against a given target, this process changes the internal state    * of current class.    *    * @param target a generic type target that impacts this evaluation.    * @throws YarnException    */
DECL|method|evaluate (T target)
name|void
name|evaluate
parameter_list|(
name|T
name|target
parameter_list|)
throws|throws
name|YarnException
function_decl|;
block|}
end_interface

end_unit

