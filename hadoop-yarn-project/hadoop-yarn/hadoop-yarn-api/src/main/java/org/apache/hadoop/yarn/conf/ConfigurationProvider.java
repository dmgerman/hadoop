begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.conf
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|conf
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
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
name|classification
operator|.
name|InterfaceAudience
operator|.
name|Private
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
name|classification
operator|.
name|InterfaceStability
operator|.
name|Unstable
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

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
comment|/**  * Base class to implement ConfigurationProvider.  * Real ConfigurationProvider implementations need to derive from it and  * implement load methods to actually load the configuration.  */
DECL|class|ConfigurationProvider
specifier|public
specifier|abstract
class|class
name|ConfigurationProvider
block|{
DECL|method|init (Configuration bootstrapConf)
specifier|public
name|void
name|init
parameter_list|(
name|Configuration
name|bootstrapConf
parameter_list|)
throws|throws
name|Exception
block|{
name|initInternal
argument_list|(
name|bootstrapConf
argument_list|)
expr_stmt|;
block|}
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|Exception
block|{
name|closeInternal
argument_list|()
expr_stmt|;
block|}
comment|/**    * Opens an InputStream at the indicated file    * @param bootstrapConf Configuration    * @param name The configuration file name    * @return configuration    * @throws YarnException    * @throws IOException    */
DECL|method|getConfigurationInputStream ( Configuration bootstrapConf, String name)
specifier|public
specifier|abstract
name|InputStream
name|getConfigurationInputStream
parameter_list|(
name|Configuration
name|bootstrapConf
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
comment|/**    * Derived classes initialize themselves using this method.    */
DECL|method|initInternal (Configuration bootstrapConf)
specifier|public
specifier|abstract
name|void
name|initInternal
parameter_list|(
name|Configuration
name|bootstrapConf
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**    * Derived classes close themselves using this method.    */
DECL|method|closeInternal ()
specifier|public
specifier|abstract
name|void
name|closeInternal
parameter_list|()
throws|throws
name|Exception
function_decl|;
block|}
end_class

end_unit

