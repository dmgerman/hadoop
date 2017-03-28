begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|scheduler
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
name|util
operator|.
name|List
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
name|LimitedPrivate
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
name|api
operator|.
name|records
operator|.
name|NodeId
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
name|server
operator|.
name|resourcemanager
operator|.
name|RMContext
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
name|server
operator|.
name|resourcemanager
operator|.
name|recovery
operator|.
name|Recoverable
import|;
end_import

begin_comment
comment|/**  * This interface is the one implemented by the schedulers. It mainly extends   * {@link YarnScheduler}.   *  */
end_comment

begin_interface
annotation|@
name|LimitedPrivate
argument_list|(
literal|"yarn"
argument_list|)
annotation|@
name|Evolving
DECL|interface|ResourceScheduler
specifier|public
interface|interface
name|ResourceScheduler
extends|extends
name|YarnScheduler
extends|,
name|Recoverable
block|{
comment|/**    * Set RMContext for<code>ResourceScheduler</code>.    * This method should be called immediately after instantiating    * a scheduler once.    * @param rmContext created by ResourceManager    */
DECL|method|setRMContext (RMContext rmContext)
name|void
name|setRMContext
parameter_list|(
name|RMContext
name|rmContext
parameter_list|)
function_decl|;
comment|/**    * Re-initialize the<code>ResourceScheduler</code>.    * @param conf configuration    * @throws IOException    */
DECL|method|reinitialize (Configuration conf, RMContext rmContext)
name|void
name|reinitialize
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|RMContext
name|rmContext
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Get the {@link NodeId} available in the cluster by resource name.    * @param resourceName resource name    * @return the number of available {@link NodeId} by resource name.    */
DECL|method|getNodeIds (String resourceName)
name|List
argument_list|<
name|NodeId
argument_list|>
name|getNodeIds
parameter_list|(
name|String
name|resourceName
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

