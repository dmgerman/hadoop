begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.server.appmaster.monkey
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|monkey
package|;
end_package

begin_import
import|import
name|com
operator|.
name|codahale
operator|.
name|metrics
operator|.
name|MetricRegistry
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
name|service
operator|.
name|AbstractService
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
name|api
operator|.
name|InternalKeys
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
name|server
operator|.
name|appmaster
operator|.
name|actions
operator|.
name|QueueAccess
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
name|server
operator|.
name|appmaster
operator|.
name|actions
operator|.
name|RenewingAction
import|;
end_import

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
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_comment
comment|/**  * A chaos monkey service which will invoke ChaosTarget events   */
end_comment

begin_class
DECL|class|ChaosMonkeyService
specifier|public
class|class
name|ChaosMonkeyService
extends|extends
name|AbstractService
block|{
DECL|field|log
specifier|protected
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ChaosMonkeyService
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|metrics
specifier|private
specifier|final
name|MetricRegistry
name|metrics
decl_stmt|;
DECL|field|queues
specifier|private
specifier|final
name|QueueAccess
name|queues
decl_stmt|;
DECL|field|random
specifier|private
specifier|final
name|Random
name|random
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
DECL|field|chaosEntries
specifier|private
specifier|final
name|List
argument_list|<
name|ChaosEntry
argument_list|>
name|chaosEntries
init|=
operator|new
name|ArrayList
argument_list|<
name|ChaosEntry
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|ChaosMonkeyService (MetricRegistry metrics, QueueAccess queues)
specifier|public
name|ChaosMonkeyService
parameter_list|(
name|MetricRegistry
name|metrics
parameter_list|,
name|QueueAccess
name|queues
parameter_list|)
block|{
name|super
argument_list|(
literal|"ChaosMonkeyService"
argument_list|)
expr_stmt|;
name|this
operator|.
name|metrics
operator|=
name|metrics
expr_stmt|;
name|this
operator|.
name|queues
operator|=
name|queues
expr_stmt|;
block|}
comment|/**    * Add a target ... it is only added if<code>probability&gt; 0</code>    * @param name name    * @param target chaos target    * @param probability probability    */
DECL|method|addTarget (String name, ChaosTarget target, long probability)
specifier|public
specifier|synchronized
name|void
name|addTarget
parameter_list|(
name|String
name|name
parameter_list|,
name|ChaosTarget
name|target
parameter_list|,
name|long
name|probability
parameter_list|)
block|{
if|if
condition|(
name|probability
operator|>
literal|0
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Adding {} with probability {}"
argument_list|,
name|name
argument_list|,
operator|(
operator|(
name|double
operator|)
name|probability
operator|)
operator|/
name|InternalKeys
operator|.
name|PROBABILITY_PERCENT_1
argument_list|)
expr_stmt|;
name|chaosEntries
operator|.
name|add
argument_list|(
operator|new
name|ChaosEntry
argument_list|(
name|name
argument_list|,
name|target
argument_list|,
name|probability
argument_list|,
name|metrics
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Action {} not enabled"
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Get the number of targets in the list    * @return the count of added targets    */
DECL|method|getTargetCount ()
specifier|public
name|int
name|getTargetCount
parameter_list|()
block|{
return|return
name|chaosEntries
operator|.
name|size
argument_list|()
return|;
block|}
comment|/**    * Iterate through all the entries and invoke chaos on those wanted    */
DECL|method|play ()
specifier|public
name|void
name|play
parameter_list|()
block|{
for|for
control|(
name|ChaosEntry
name|chaosEntry
range|:
name|chaosEntries
control|)
block|{
name|long
name|p
init|=
name|randomPercentage
argument_list|()
decl_stmt|;
name|chaosEntry
operator|.
name|maybeInvokeChaos
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|randomPercentage ()
specifier|public
name|int
name|randomPercentage
parameter_list|()
block|{
return|return
name|random
operator|.
name|nextInt
argument_list|(
name|InternalKeys
operator|.
name|PROBABILITY_PERCENT_100
argument_list|)
return|;
block|}
comment|/**    * Check for callers to see if chaos should be triggered; shares the    * same random number source as the rest of the monkey entries    * @param probability probability     * @return true if the action should happen    */
DECL|method|chaosCheck (long probability)
specifier|public
name|boolean
name|chaosCheck
parameter_list|(
name|long
name|probability
parameter_list|)
block|{
return|return
name|randomPercentage
argument_list|()
operator|<
name|probability
return|;
block|}
comment|/**    * Schedule the monkey    *    * @param delay initial delay    * @param timeUnit time unit    * @return true if it was scheduled (i.e. 1+ action) and interval> 0    */
DECL|method|schedule (long delay, long interval, TimeUnit timeUnit)
specifier|public
name|boolean
name|schedule
parameter_list|(
name|long
name|delay
parameter_list|,
name|long
name|interval
parameter_list|,
name|TimeUnit
name|timeUnit
parameter_list|)
block|{
if|if
condition|(
name|interval
operator|>
literal|0
operator|&&
operator|!
name|chaosEntries
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|queues
operator|.
name|schedule
argument_list|(
name|getChaosAction
argument_list|(
name|delay
argument_list|,
name|interval
argument_list|,
name|timeUnit
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
comment|/**    * Get the chaos action    *    * @param delay    * @param timeUnit time unit    * @return the action to schedule    */
DECL|method|getChaosAction (long delay, long interval, TimeUnit timeUnit)
specifier|public
name|RenewingAction
argument_list|<
name|MonkeyPlayAction
argument_list|>
name|getChaosAction
parameter_list|(
name|long
name|delay
parameter_list|,
name|long
name|interval
parameter_list|,
name|TimeUnit
name|timeUnit
parameter_list|)
block|{
name|RenewingAction
argument_list|<
name|MonkeyPlayAction
argument_list|>
name|action
init|=
operator|new
name|RenewingAction
argument_list|<
name|MonkeyPlayAction
argument_list|>
argument_list|(
operator|new
name|MonkeyPlayAction
argument_list|(
name|this
argument_list|,
literal|0
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
argument_list|,
name|delay
argument_list|,
name|interval
argument_list|,
name|timeUnit
argument_list|,
literal|0
argument_list|)
decl_stmt|;
return|return
name|action
return|;
block|}
block|}
end_class

end_unit

