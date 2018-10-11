begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.cli.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|cli
operator|.
name|util
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
comment|/**  * This interface is to generalize types of test command for upstream projects.  */
end_comment

begin_interface
DECL|interface|CLICommand
specifier|public
interface|interface
name|CLICommand
block|{
DECL|method|getExecutor (String tag, Configuration conf)
specifier|public
name|CommandExecutor
name|getExecutor
parameter_list|(
name|String
name|tag
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IllegalArgumentException
function_decl|;
DECL|method|getType ()
specifier|public
name|CLICommandTypes
name|getType
parameter_list|()
function_decl|;
DECL|method|getCmd ()
specifier|public
name|String
name|getCmd
parameter_list|()
function_decl|;
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

