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
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|util
operator|.
name|ReflectionUtils
import|;
end_import

begin_comment
comment|/**  * Factory class for creating instances of {@link PlacementRule}.  */
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
DECL|class|PlacementFactory
specifier|public
specifier|final
class|class
name|PlacementFactory
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|PlacementFactory
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|PlacementFactory ()
specifier|private
name|PlacementFactory
parameter_list|()
block|{
comment|// Unused.
block|}
comment|/**    * Create a new {@link PlacementRule} based on the rule class from the    * configuration. This is used to instantiate rules by the scheduler which    * does not resolve the class before this call.    * @param ruleStr The name of the class to instantiate    * @param conf The configuration object to set for the rule    * @return Created class instance    */
DECL|method|getPlacementRule (String ruleStr, Configuration conf)
specifier|public
specifier|static
name|PlacementRule
name|getPlacementRule
parameter_list|(
name|String
name|ruleStr
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|ClassNotFoundException
block|{
name|Class
argument_list|<
name|?
extends|extends
name|PlacementRule
argument_list|>
name|ruleClass
init|=
name|Class
operator|.
name|forName
argument_list|(
name|ruleStr
argument_list|)
operator|.
name|asSubclass
argument_list|(
name|PlacementRule
operator|.
name|class
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Using PlacementRule implementation - "
operator|+
name|ruleClass
argument_list|)
expr_stmt|;
return|return
name|ReflectionUtils
operator|.
name|newInstance
argument_list|(
name|ruleClass
argument_list|,
name|conf
argument_list|)
return|;
block|}
comment|/**    * Create a new {@link PlacementRule} based on the rule class from the    * configuration. This is used to instantiate rules by the scheduler which    * resolve the class before this call.    * @param ruleClass The specific class reference to instantiate    * @param initArg The config to set    * @return Created class instance    */
DECL|method|getPlacementRule ( Class<? extends PlacementRule> ruleClass, Object initArg)
specifier|public
specifier|static
name|PlacementRule
name|getPlacementRule
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|PlacementRule
argument_list|>
name|ruleClass
parameter_list|,
name|Object
name|initArg
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Creating PlacementRule implementation: "
operator|+
name|ruleClass
argument_list|)
expr_stmt|;
name|PlacementRule
name|rule
init|=
name|ReflectionUtils
operator|.
name|newInstance
argument_list|(
name|ruleClass
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|rule
operator|.
name|setConfig
argument_list|(
name|initArg
argument_list|)
expr_stmt|;
return|return
name|rule
return|;
block|}
block|}
end_class

end_unit

