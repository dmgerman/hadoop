begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.placement
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
name|placement
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|classification
operator|.
name|InterfaceAudience
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
name|ApplicationSubmissionContext
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
name|scheduler
operator|.
name|ResourceScheduler
import|;
end_import

begin_comment
comment|/**  * Abstract base for all Placement Rules.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|PlacementRule
specifier|public
specifier|abstract
class|class
name|PlacementRule
block|{
comment|/**    * Set the config based on the passed in argument. This construct is used to    * not pollute this abstract class with implementation specific references.    */
DECL|method|setConfig (Object initArg)
specifier|public
name|void
name|setConfig
parameter_list|(
name|Object
name|initArg
parameter_list|)
block|{
comment|// Default is a noop
block|}
comment|/**    * Return the name of the rule.    * @return The name of the rule, the fully qualified class name.    */
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
return|;
block|}
comment|/**    * Initialize the rule with the scheduler.    * @param scheduler the scheduler using the rule    * @return<code>true</code> or<code>false</code> The outcome of the    * initialisation, rule dependent response which might not be persisted in    * the rule.    * @throws IOException for any errors    */
DECL|method|initialize (ResourceScheduler scheduler)
specifier|public
specifier|abstract
name|boolean
name|initialize
parameter_list|(
name|ResourceScheduler
name|scheduler
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Return the scheduler queue name the application should be placed in    * wrapped in an {@link ApplicationPlacementContext} object.    *    * A non<code>null</code> return value places the application in a queue,    * a<code>null</code> value means the queue is not yet determined. The    * next {@link PlacementRule} in the list maintained in the    * {@link PlacementManager} will be executed.    *    * @param asc The context of the application created on submission    * @param user The name of the user submitting the application    *     * @throws YarnException for any error while executing the rule    *     * @return The queue name wrapped in {@link ApplicationPlacementContext} or    *<code>null</code> if no queue was resolved    */
DECL|method|getPlacementForApp ( ApplicationSubmissionContext asc, String user)
specifier|public
specifier|abstract
name|ApplicationPlacementContext
name|getPlacementForApp
parameter_list|(
name|ApplicationSubmissionContext
name|asc
parameter_list|,
name|String
name|user
parameter_list|)
throws|throws
name|YarnException
function_decl|;
block|}
end_class

end_unit

