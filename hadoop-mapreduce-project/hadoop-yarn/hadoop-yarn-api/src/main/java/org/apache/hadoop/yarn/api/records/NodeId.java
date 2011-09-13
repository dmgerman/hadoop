begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.api.records
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
operator|.
name|records
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
name|Stable
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

begin_comment
comment|/**  *<p><code>NodeId</code> is the unique identifier for a node.</p>  *   *<p>It includes the<em>hostname</em> and<em>port</em> to uniquely   * identify the node. Thus, it is unique across restarts of any   *<code>NodeManager</code>.</p>  */
end_comment

begin_interface
annotation|@
name|Public
annotation|@
name|Stable
DECL|interface|NodeId
specifier|public
interface|interface
name|NodeId
extends|extends
name|Comparable
argument_list|<
name|NodeId
argument_list|>
block|{
comment|/**    * Get the<em>hostname</em> of the node.    * @return<em>hostname</em> of the node    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getHost ()
name|String
name|getHost
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setHost (String host)
name|void
name|setHost
parameter_list|(
name|String
name|host
parameter_list|)
function_decl|;
comment|/**    * Get the<em>port</em> for communicating with the node.    * @return<em>port</em> for communicating with the node    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getPort ()
name|int
name|getPort
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setPort (int port)
name|void
name|setPort
parameter_list|(
name|int
name|port
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

