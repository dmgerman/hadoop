begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.csi.translator
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|csi
operator|.
name|translator
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
comment|/**  * ProtoTranslator converts a YARN side message to CSI proto message  * and vice versa. Each CSI proto message should have a corresponding  * YARN side message implementation, and a transformer to convert them  * one to the other. This layer helps we to hide CSI spec messages  * from YARN components.  *  * @param<A> YARN side internal messages  * @param<B> CSI proto messages  */
end_comment

begin_interface
DECL|interface|ProtoTranslator
specifier|public
interface|interface
name|ProtoTranslator
parameter_list|<
name|A
parameter_list|,
name|B
parameter_list|>
block|{
comment|/**    * Convert message from type A to type B.    * @param messageA    * @return messageB    * @throws YarnException    */
DECL|method|convertTo (A messageA)
name|B
name|convertTo
parameter_list|(
name|A
name|messageA
parameter_list|)
throws|throws
name|YarnException
function_decl|;
comment|/**    * Convert message from type B to type A.    * @param messageB    * @return messageA    * @throws YarnException    */
DECL|method|convertFrom (B messageB)
name|A
name|convertFrom
parameter_list|(
name|B
name|messageB
parameter_list|)
throws|throws
name|YarnException
function_decl|;
block|}
end_interface

end_unit

