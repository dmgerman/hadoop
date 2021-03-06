begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.azurebfs.contracts.diagnostics
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|azurebfs
operator|.
name|contracts
operator|.
name|diagnostics
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
name|fs
operator|.
name|azurebfs
operator|.
name|contracts
operator|.
name|exceptions
operator|.
name|InvalidConfigurationValueException
import|;
end_import

begin_comment
comment|/**  * ConfigurationValidator to validate the value of a configuration key  * @param<T> the type of the validator and the validated value.  */
end_comment

begin_interface
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|interface|ConfigurationValidator
specifier|public
interface|interface
name|ConfigurationValidator
parameter_list|<
name|T
parameter_list|>
block|{
comment|/**    * Validates a configuration value.    * @param configValue the configuration value to be validated.    * @return validated value of type T    * @throws InvalidConfigurationValueException if the configuration value is invalid.    */
DECL|method|validate (String configValue)
name|T
name|validate
parameter_list|(
name|String
name|configValue
parameter_list|)
throws|throws
name|InvalidConfigurationValueException
function_decl|;
block|}
end_interface

end_unit

