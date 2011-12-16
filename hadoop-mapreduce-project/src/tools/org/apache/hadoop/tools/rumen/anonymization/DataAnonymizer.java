begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.tools.rumen.anonymization
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
name|anonymization
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
name|tools
operator|.
name|rumen
operator|.
name|state
operator|.
name|State
import|;
end_import

begin_comment
comment|/**  * The data anonymizer interface.  */
end_comment

begin_interface
DECL|interface|DataAnonymizer
specifier|public
interface|interface
name|DataAnonymizer
parameter_list|<
name|T
parameter_list|>
block|{
DECL|method|anonymize (T data, State state)
name|T
name|anonymize
parameter_list|(
name|T
name|data
parameter_list|,
name|State
name|state
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

