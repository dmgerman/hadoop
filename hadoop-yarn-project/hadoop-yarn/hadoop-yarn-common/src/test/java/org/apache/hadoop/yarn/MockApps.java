begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|ApplicationAttemptId
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
name|ApplicationId
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
name|YarnApplicationState
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
name|util
operator|.
name|Records
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
name|collect
operator|.
name|Iterators
import|;
end_import

begin_comment
comment|/**  * Utilities to generate fake test apps  */
end_comment

begin_class
DECL|class|MockApps
specifier|public
class|class
name|MockApps
block|{
DECL|field|NAMES
specifier|static
specifier|final
name|Iterator
argument_list|<
name|String
argument_list|>
name|NAMES
init|=
name|Iterators
operator|.
name|cycle
argument_list|(
literal|"SleepJob"
argument_list|,
literal|"RandomWriter"
argument_list|,
literal|"TeraSort"
argument_list|,
literal|"TeraGen"
argument_list|,
literal|"PigLatin"
argument_list|,
literal|"WordCount"
argument_list|,
literal|"I18nApp<â¯>"
argument_list|)
decl_stmt|;
DECL|field|USERS
specifier|static
specifier|final
name|Iterator
argument_list|<
name|String
argument_list|>
name|USERS
init|=
name|Iterators
operator|.
name|cycle
argument_list|(
literal|"dorothy"
argument_list|,
literal|"tinman"
argument_list|,
literal|"scarecrow"
argument_list|,
literal|"glinda"
argument_list|,
literal|"nikko"
argument_list|,
literal|"toto"
argument_list|,
literal|"winkie"
argument_list|,
literal|"zeke"
argument_list|,
literal|"gulch"
argument_list|)
decl_stmt|;
DECL|field|STATES
specifier|static
specifier|final
name|Iterator
argument_list|<
name|YarnApplicationState
argument_list|>
name|STATES
init|=
name|Iterators
operator|.
name|cycle
argument_list|(
name|YarnApplicationState
operator|.
name|values
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|QUEUES
specifier|static
specifier|final
name|Iterator
argument_list|<
name|String
argument_list|>
name|QUEUES
init|=
name|Iterators
operator|.
name|cycle
argument_list|(
literal|"a.a1"
argument_list|,
literal|"a.a2"
argument_list|,
literal|"b.b1"
argument_list|,
literal|"b.b2"
argument_list|,
literal|"b.b3"
argument_list|,
literal|"c.c1.c11"
argument_list|,
literal|"c.c1.c12"
argument_list|,
literal|"c.c1.c13"
argument_list|,
literal|"c.c2"
argument_list|,
literal|"c.c3"
argument_list|,
literal|"c.c4"
argument_list|)
decl_stmt|;
DECL|field|TS
specifier|static
specifier|final
name|long
name|TS
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
DECL|method|newAppName ()
specifier|public
specifier|static
name|String
name|newAppName
parameter_list|()
block|{
synchronized|synchronized
init|(
name|NAMES
init|)
block|{
return|return
name|NAMES
operator|.
name|next
argument_list|()
return|;
block|}
block|}
DECL|method|newUserName ()
specifier|public
specifier|static
name|String
name|newUserName
parameter_list|()
block|{
synchronized|synchronized
init|(
name|USERS
init|)
block|{
return|return
name|USERS
operator|.
name|next
argument_list|()
return|;
block|}
block|}
DECL|method|newQueue ()
specifier|public
specifier|static
name|String
name|newQueue
parameter_list|()
block|{
synchronized|synchronized
init|(
name|QUEUES
init|)
block|{
return|return
name|QUEUES
operator|.
name|next
argument_list|()
return|;
block|}
block|}
DECL|method|newAppID (int i)
specifier|public
specifier|static
name|ApplicationId
name|newAppID
parameter_list|(
name|int
name|i
parameter_list|)
block|{
return|return
name|ApplicationId
operator|.
name|newInstance
argument_list|(
name|TS
argument_list|,
name|i
argument_list|)
return|;
block|}
DECL|method|newAppAttemptID (ApplicationId appId, int i)
specifier|public
specifier|static
name|ApplicationAttemptId
name|newAppAttemptID
parameter_list|(
name|ApplicationId
name|appId
parameter_list|,
name|int
name|i
parameter_list|)
block|{
name|ApplicationAttemptId
name|id
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|ApplicationAttemptId
operator|.
name|class
argument_list|)
decl_stmt|;
name|id
operator|.
name|setApplicationId
argument_list|(
name|appId
argument_list|)
expr_stmt|;
name|id
operator|.
name|setAttemptId
argument_list|(
name|i
argument_list|)
expr_stmt|;
return|return
name|id
return|;
block|}
DECL|method|newAppState ()
specifier|public
specifier|static
name|YarnApplicationState
name|newAppState
parameter_list|()
block|{
synchronized|synchronized
init|(
name|STATES
init|)
block|{
return|return
name|STATES
operator|.
name|next
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

