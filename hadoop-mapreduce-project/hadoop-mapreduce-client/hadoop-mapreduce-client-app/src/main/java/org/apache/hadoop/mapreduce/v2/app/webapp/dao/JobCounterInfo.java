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
name|java
operator|.
name|util
operator|.
name|Map
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
name|v2
operator|.
name|api
operator|.
name|records
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
name|v2
operator|.
name|api
operator|.
name|records
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
name|api
operator|.
name|records
operator|.
name|TaskId
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
name|AppContext
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
name|Job
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
name|Task
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
name|impl
operator|.
name|JobImpl
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
literal|"jobCounters"
argument_list|)
annotation|@
name|XmlAccessorType
argument_list|(
name|XmlAccessType
operator|.
name|FIELD
argument_list|)
DECL|class|JobCounterInfo
specifier|public
class|class
name|JobCounterInfo
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
annotation|@
name|XmlTransient
DECL|field|map
specifier|protected
name|Counters
name|map
init|=
literal|null
decl_stmt|;
annotation|@
name|XmlTransient
DECL|field|reduce
specifier|protected
name|Counters
name|reduce
init|=
literal|null
decl_stmt|;
DECL|field|id
specifier|protected
name|String
name|id
decl_stmt|;
DECL|field|counterGroups
specifier|protected
name|ArrayList
argument_list|<
name|CounterGroupInfo
argument_list|>
name|counterGroups
decl_stmt|;
DECL|method|JobCounterInfo ()
specifier|public
name|JobCounterInfo
parameter_list|()
block|{   }
DECL|method|JobCounterInfo (AppContext ctx, Job job)
specifier|public
name|JobCounterInfo
parameter_list|(
name|AppContext
name|ctx
parameter_list|,
name|Job
name|job
parameter_list|)
block|{
name|getCounters
argument_list|(
name|ctx
argument_list|,
name|job
argument_list|)
expr_stmt|;
name|counterGroups
operator|=
operator|new
name|ArrayList
argument_list|<
name|CounterGroupInfo
argument_list|>
argument_list|()
expr_stmt|;
name|this
operator|.
name|id
operator|=
name|MRApps
operator|.
name|toString
argument_list|(
name|job
operator|.
name|getID
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|numGroups
init|=
literal|0
decl_stmt|;
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
operator|.
name|getAllCounterGroups
argument_list|()
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|g
operator|!=
literal|null
condition|)
block|{
name|CounterGroup
name|mg
init|=
name|map
operator|==
literal|null
condition|?
literal|null
else|:
name|map
operator|.
name|getCounterGroup
argument_list|(
name|g
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|CounterGroup
name|rg
init|=
name|reduce
operator|==
literal|null
condition|?
literal|null
else|:
name|reduce
operator|.
name|getCounterGroup
argument_list|(
name|g
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
operator|++
name|numGroups
expr_stmt|;
name|CounterGroupInfo
name|cginfo
init|=
operator|new
name|CounterGroupInfo
argument_list|(
name|g
operator|.
name|getName
argument_list|()
argument_list|,
name|g
argument_list|,
name|mg
argument_list|,
name|rg
argument_list|)
decl_stmt|;
name|counterGroups
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
DECL|method|getCounters (AppContext ctx, Job job)
specifier|private
name|void
name|getCounters
parameter_list|(
name|AppContext
name|ctx
parameter_list|,
name|Job
name|job
parameter_list|)
block|{
name|total
operator|=
name|JobImpl
operator|.
name|newCounters
argument_list|()
expr_stmt|;
if|if
condition|(
name|job
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|map
operator|=
name|JobImpl
operator|.
name|newCounters
argument_list|()
expr_stmt|;
name|reduce
operator|=
name|JobImpl
operator|.
name|newCounters
argument_list|()
expr_stmt|;
comment|// Get all types of counters
name|Map
argument_list|<
name|TaskId
argument_list|,
name|Task
argument_list|>
name|tasks
init|=
name|job
operator|.
name|getTasks
argument_list|()
decl_stmt|;
for|for
control|(
name|Task
name|t
range|:
name|tasks
operator|.
name|values
argument_list|()
control|)
block|{
name|Counters
name|counters
init|=
name|t
operator|.
name|getCounters
argument_list|()
decl_stmt|;
name|JobImpl
operator|.
name|incrAllCounters
argument_list|(
name|total
argument_list|,
name|counters
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|t
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|MAP
case|:
name|JobImpl
operator|.
name|incrAllCounters
argument_list|(
name|map
argument_list|,
name|counters
argument_list|)
expr_stmt|;
break|break;
case|case
name|REDUCE
case|:
name|JobImpl
operator|.
name|incrAllCounters
argument_list|(
name|reduce
argument_list|,
name|counters
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
end_class

end_unit

