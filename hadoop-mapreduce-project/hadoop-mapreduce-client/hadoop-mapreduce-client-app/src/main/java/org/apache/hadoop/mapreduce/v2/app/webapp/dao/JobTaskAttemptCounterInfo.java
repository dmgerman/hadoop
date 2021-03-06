begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.app.webapp.dao
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|webapp
operator|.
name|dao
package|;
end_package

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
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlAccessType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlAccessorType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlRootElement
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlTransient
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
name|mapreduce
operator|.
name|CounterGroup
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
name|mapreduce
operator|.
name|Counters
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
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|job
operator|.
name|TaskAttempt
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
name|mapreduce
operator|.
name|v2
operator|.
name|util
operator|.
name|MRApps
import|;
end_import

begin_class
annotation|@
name|XmlRootElement
argument_list|(
name|name
operator|=
literal|"jobTaskAttemptCounters"
argument_list|)
annotation|@
name|XmlAccessorType
argument_list|(
name|XmlAccessType
operator|.
name|FIELD
argument_list|)
DECL|class|JobTaskAttemptCounterInfo
specifier|public
class|class
name|JobTaskAttemptCounterInfo
block|{
annotation|@
name|XmlTransient
DECL|field|total
specifier|protected
name|Counters
name|total
init|=
literal|null
decl_stmt|;
DECL|field|id
specifier|protected
name|String
name|id
decl_stmt|;
DECL|field|taskAttemptCounterGroup
specifier|protected
name|ArrayList
argument_list|<
name|TaskCounterGroupInfo
argument_list|>
name|taskAttemptCounterGroup
decl_stmt|;
DECL|method|JobTaskAttemptCounterInfo ()
specifier|public
name|JobTaskAttemptCounterInfo
parameter_list|()
block|{   }
DECL|method|JobTaskAttemptCounterInfo (TaskAttempt taskattempt)
specifier|public
name|JobTaskAttemptCounterInfo
parameter_list|(
name|TaskAttempt
name|taskattempt
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|MRApps
operator|.
name|toString
argument_list|(
name|taskattempt
operator|.
name|getID
argument_list|()
argument_list|)
expr_stmt|;
name|total
operator|=
name|taskattempt
operator|.
name|getCounters
argument_list|()
expr_stmt|;
name|taskAttemptCounterGroup
operator|=
operator|new
name|ArrayList
argument_list|<
name|TaskCounterGroupInfo
argument_list|>
argument_list|()
expr_stmt|;
if|if
condition|(
name|total
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|CounterGroup
name|g
range|:
name|total
control|)
block|{
if|if
condition|(
name|g
operator|!=
literal|null
condition|)
block|{
name|TaskCounterGroupInfo
name|cginfo
init|=
operator|new
name|TaskCounterGroupInfo
argument_list|(
name|g
operator|.
name|getName
argument_list|()
argument_list|,
name|g
argument_list|)
decl_stmt|;
if|if
condition|(
name|cginfo
operator|!=
literal|null
condition|)
block|{
name|taskAttemptCounterGroup
operator|.
name|add
argument_list|(
name|cginfo
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

