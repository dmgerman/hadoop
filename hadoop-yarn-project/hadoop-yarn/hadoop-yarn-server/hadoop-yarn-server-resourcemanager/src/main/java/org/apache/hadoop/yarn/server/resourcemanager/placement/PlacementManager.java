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
name|concurrent
operator|.
name|locks
operator|.
name|ReentrantReadWriteLock
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
name|locks
operator|.
name|ReentrantReadWriteLock
operator|.
name|ReadLock
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
name|locks
operator|.
name|ReentrantReadWriteLock
operator|.
name|WriteLock
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
import|;
end_import

begin_class
DECL|class|PlacementManager
specifier|public
class|class
name|PlacementManager
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
name|PlacementManager
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|rules
name|List
argument_list|<
name|PlacementRule
argument_list|>
name|rules
decl_stmt|;
DECL|field|readLock
name|ReadLock
name|readLock
decl_stmt|;
DECL|field|writeLock
name|WriteLock
name|writeLock
decl_stmt|;
DECL|method|PlacementManager ()
specifier|public
name|PlacementManager
parameter_list|()
block|{
name|ReentrantReadWriteLock
name|lock
init|=
operator|new
name|ReentrantReadWriteLock
argument_list|()
decl_stmt|;
name|readLock
operator|=
name|lock
operator|.
name|readLock
argument_list|()
expr_stmt|;
name|writeLock
operator|=
name|lock
operator|.
name|writeLock
argument_list|()
expr_stmt|;
block|}
DECL|method|updateRules (List<PlacementRule> rules)
specifier|public
name|void
name|updateRules
parameter_list|(
name|List
argument_list|<
name|PlacementRule
argument_list|>
name|rules
parameter_list|)
block|{
name|writeLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|this
operator|.
name|rules
operator|=
name|rules
expr_stmt|;
block|}
finally|finally
block|{
name|writeLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|placeApplication ( ApplicationSubmissionContext asc, String user)
specifier|public
name|ApplicationPlacementContext
name|placeApplication
parameter_list|(
name|ApplicationSubmissionContext
name|asc
parameter_list|,
name|String
name|user
parameter_list|)
throws|throws
name|YarnException
block|{
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
if|if
condition|(
literal|null
operator|==
name|rules
operator|||
name|rules
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|ApplicationPlacementContext
name|placement
init|=
literal|null
decl_stmt|;
for|for
control|(
name|PlacementRule
name|rule
range|:
name|rules
control|)
block|{
name|placement
operator|=
name|rule
operator|.
name|getPlacementForApp
argument_list|(
name|asc
argument_list|,
name|user
argument_list|)
expr_stmt|;
if|if
condition|(
name|placement
operator|!=
literal|null
condition|)
block|{
break|break;
block|}
block|}
return|return
name|placement
return|;
block|}
finally|finally
block|{
name|readLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|VisibleForTesting
DECL|method|getPlacementRules ()
specifier|public
name|List
argument_list|<
name|PlacementRule
argument_list|>
name|getPlacementRules
parameter_list|()
block|{
return|return
name|rules
return|;
block|}
block|}
end_class

end_unit

