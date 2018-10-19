begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.registry.conf
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|registry
operator|.
name|conf
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

begin_comment
comment|/**  * Intermediate configuration class to import the keys from YarnConfiguration  * in yarn-default.xml and yarn-site.xml. Once hadoop-yarn-registry is totally  * deprecated, this should be deprecated.  */
end_comment

begin_class
DECL|class|RegistryConfiguration
specifier|public
class|class
name|RegistryConfiguration
extends|extends
name|Configuration
block|{
static|static
block|{
name|Configuration
operator|.
name|addDefaultResource
argument_list|(
literal|"yarn-default.xml"
argument_list|)
expr_stmt|;
name|Configuration
operator|.
name|addDefaultResource
argument_list|(
literal|"yarn-site.xml"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Default constructor which relies on the static method to import the YARN    * settings.    */
DECL|method|RegistryConfiguration ()
specifier|public
name|RegistryConfiguration
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

