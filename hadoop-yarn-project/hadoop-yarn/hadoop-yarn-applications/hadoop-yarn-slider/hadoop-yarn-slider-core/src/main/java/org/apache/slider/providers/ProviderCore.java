begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.providers
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|providers
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
name|conf
operator|.
name|Configuration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|core
operator|.
name|conf
operator|.
name|AggregateConf
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|core
operator|.
name|conf
operator|.
name|ConfTree
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|core
operator|.
name|exceptions
operator|.
name|SliderException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_interface
DECL|interface|ProviderCore
specifier|public
interface|interface
name|ProviderCore
block|{
DECL|method|getName ()
name|String
name|getName
parameter_list|()
function_decl|;
DECL|method|getRoles ()
name|List
argument_list|<
name|ProviderRole
argument_list|>
name|getRoles
parameter_list|()
function_decl|;
DECL|method|getConf ()
name|Configuration
name|getConf
parameter_list|()
function_decl|;
comment|/**    * Verify that an instance definition is considered valid by the provider    * @param instanceDefinition instance definition    * @throws SliderException if the configuration is not valid    */
DECL|method|validateInstanceDefinition (AggregateConf instanceDefinition)
name|void
name|validateInstanceDefinition
parameter_list|(
name|AggregateConf
name|instanceDefinition
parameter_list|)
throws|throws
name|SliderException
function_decl|;
block|}
end_interface

end_unit

