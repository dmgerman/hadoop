begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
package|;
end_package

begin_comment
comment|/**  * Class to hold accounting info about a queue  * such as remaining budget, spending rate and  * whether queue usage  */
end_comment

begin_class
DECL|class|BudgetQueue
specifier|public
class|class
name|BudgetQueue
block|{
DECL|field|name
name|String
name|name
decl_stmt|;
DECL|field|budget
specifier|volatile
name|float
name|budget
decl_stmt|;
DECL|field|spending
specifier|volatile
name|float
name|spending
decl_stmt|;
DECL|field|used
specifier|volatile
name|int
name|used
decl_stmt|;
DECL|field|pending
specifier|volatile
name|int
name|pending
decl_stmt|;
comment|/**    * @param name queue name    * @param budget queue budget in credits    * @param spending queue spending rate in credits per allocation interval    * to deduct from budget    */
DECL|method|BudgetQueue (String name, float budget, float spending)
specifier|public
name|BudgetQueue
parameter_list|(
name|String
name|name
parameter_list|,
name|float
name|budget
parameter_list|,
name|float
name|spending
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|budget
operator|=
name|budget
expr_stmt|;
name|this
operator|.
name|spending
operator|=
name|spending
expr_stmt|;
name|this
operator|.
name|used
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|pending
operator|=
literal|0
expr_stmt|;
block|}
comment|/**    * Thread safe addition of budget    * @param newBudget budget to add    */
DECL|method|addBudget (float newBudget)
specifier|public
specifier|synchronized
name|void
name|addBudget
parameter_list|(
name|float
name|newBudget
parameter_list|)
block|{
name|budget
operator|+=
name|newBudget
expr_stmt|;
block|}
block|}
end_class

end_unit

