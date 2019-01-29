begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.api
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|api
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
comment|/**  * csi-adaptor is a plugin, user can provide customized implementation  * according to this interface. NM will init and load this into a NM aux  * service, and it can run multiple csi-adaptor servers.  *  * User needs to implement all the methods defined in  * {@link CsiAdaptorProtocol}, and plus the methods in this interface.  */
end_comment

begin_interface
DECL|interface|CsiAdaptorPlugin
specifier|public
interface|interface
name|CsiAdaptorPlugin
extends|extends
name|CsiAdaptorProtocol
block|{
comment|/**    * A csi-adaptor implementation can init its state within this function.    * Configuration is available so the implementation can retrieve some    * customized configuration from yarn-site.xml.    * @param driverName the name of the csi-driver.    * @param conf configuration.    * @throws YarnException    */
DECL|method|init (String driverName, Configuration conf)
name|void
name|init
parameter_list|(
name|String
name|driverName
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|YarnException
function_decl|;
comment|/**    * Returns the driver name of the csi-driver this adaptor works with.    * The name should be consistent on all the places being used, ideally    * it should come from the value when init is done.    * @return the name of the csi-driver that this adaptor works with.    */
DECL|method|getDriverName ()
name|String
name|getDriverName
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

