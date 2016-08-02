begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.server.services.security
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|services
operator|.
name|security
package|;
end_package

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
name|MapOperations
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
name|io
operator|.
name|File
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

begin_comment
comment|/**  *  */
end_comment

begin_interface
DECL|interface|SecurityStoreGenerator
specifier|public
interface|interface
name|SecurityStoreGenerator
block|{
DECL|method|generate (String hostname, String containerId, AggregateConf instanceDefinition, MapOperations compOps, String role)
name|SecurityStore
name|generate
parameter_list|(
name|String
name|hostname
parameter_list|,
name|String
name|containerId
parameter_list|,
name|AggregateConf
name|instanceDefinition
parameter_list|,
name|MapOperations
name|compOps
parameter_list|,
name|String
name|role
parameter_list|)
throws|throws
name|SliderException
throws|,
name|IOException
function_decl|;
DECL|method|isStoreRequested (MapOperations compOps)
name|boolean
name|isStoreRequested
parameter_list|(
name|MapOperations
name|compOps
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

