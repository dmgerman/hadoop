begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.event
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|event
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
name|InterfaceAudience
operator|.
name|Public
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
name|Evolving
import|;
end_import

begin_comment
comment|/**  * Interface defining events api.  *  */
end_comment

begin_interface
annotation|@
name|Public
annotation|@
name|Evolving
DECL|interface|Event
specifier|public
interface|interface
name|Event
parameter_list|<
name|TYPE
extends|extends
name|Enum
parameter_list|<
name|TYPE
parameter_list|>
parameter_list|>
block|{
DECL|method|getType ()
name|TYPE
name|getType
parameter_list|()
function_decl|;
DECL|method|getTimestamp ()
name|long
name|getTimestamp
parameter_list|()
function_decl|;
DECL|method|toString ()
name|String
name|toString
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

