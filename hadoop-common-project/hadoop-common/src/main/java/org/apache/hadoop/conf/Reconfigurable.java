begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.conf
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|conf
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

begin_comment
comment|/**  * Something whose {@link Configuration} can be changed at run time.  */
end_comment

begin_interface
DECL|interface|Reconfigurable
specifier|public
interface|interface
name|Reconfigurable
extends|extends
name|Configurable
block|{
comment|/**    * Change a configuration property on this object to the value specified.    *    * Change a configuration property on this object to the value specified     * and return the previous value that the configuration property was set to    * (or null if it was not previously set). If newVal is null, set the property    * to its default value;    *    * If the property cannot be changed, throw a     * {@link ReconfigurationException}.    */
DECL|method|reconfigureProperty (String property, String newVal)
name|void
name|reconfigureProperty
parameter_list|(
name|String
name|property
parameter_list|,
name|String
name|newVal
parameter_list|)
throws|throws
name|ReconfigurationException
function_decl|;
comment|/**    * Return whether a given property is changeable at run time.    *    * If isPropertyReconfigurable returns true for a property,    * then changeConf should not throw an exception when changing    * this property.    */
DECL|method|isPropertyReconfigurable (String property)
name|boolean
name|isPropertyReconfigurable
parameter_list|(
name|String
name|property
parameter_list|)
function_decl|;
comment|/**    * Return all the properties that can be changed at run time.    */
DECL|method|getReconfigurableProperties ()
name|Collection
argument_list|<
name|String
argument_list|>
name|getReconfigurableProperties
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

