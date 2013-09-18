begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.counters
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|counters
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
name|mapred
operator|.
name|JobConf
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|MRJobConfig
operator|.
name|*
import|;
end_import

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|Limits
specifier|public
class|class
name|Limits
block|{
DECL|field|conf
specifier|static
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|JobConf
argument_list|()
decl_stmt|;
DECL|field|totalCounters
specifier|private
name|int
name|totalCounters
decl_stmt|;
DECL|field|firstViolation
specifier|private
name|LimitExceededException
name|firstViolation
decl_stmt|;
DECL|field|isInited
specifier|private
specifier|static
name|boolean
name|isInited
decl_stmt|;
DECL|field|GROUP_NAME_MAX
specifier|private
specifier|static
name|int
name|GROUP_NAME_MAX
decl_stmt|;
DECL|field|COUNTER_NAME_MAX
specifier|private
specifier|static
name|int
name|COUNTER_NAME_MAX
decl_stmt|;
DECL|field|GROUPS_MAX
specifier|private
specifier|static
name|int
name|GROUPS_MAX
decl_stmt|;
DECL|field|COUNTERS_MAX
specifier|private
specifier|static
name|int
name|COUNTERS_MAX
decl_stmt|;
DECL|method|init (Configuration conf)
specifier|public
specifier|synchronized
specifier|static
name|void
name|init
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
if|if
condition|(
operator|!
name|isInited
condition|)
block|{
if|if
condition|(
name|conf
operator|==
literal|null
condition|)
block|{
name|conf
operator|=
operator|new
name|JobConf
argument_list|()
expr_stmt|;
block|}
name|GROUP_NAME_MAX
operator|=
name|conf
operator|.
name|getInt
argument_list|(
name|COUNTER_GROUP_NAME_MAX_KEY
argument_list|,
name|COUNTER_GROUP_NAME_MAX_DEFAULT
argument_list|)
expr_stmt|;
name|COUNTER_NAME_MAX
operator|=
name|conf
operator|.
name|getInt
argument_list|(
name|COUNTER_NAME_MAX_KEY
argument_list|,
name|COUNTER_NAME_MAX_DEFAULT
argument_list|)
expr_stmt|;
name|GROUPS_MAX
operator|=
name|conf
operator|.
name|getInt
argument_list|(
name|COUNTER_GROUPS_MAX_KEY
argument_list|,
name|COUNTER_GROUPS_MAX_DEFAULT
argument_list|)
expr_stmt|;
name|COUNTERS_MAX
operator|=
name|conf
operator|.
name|getInt
argument_list|(
name|COUNTERS_MAX_KEY
argument_list|,
name|COUNTERS_MAX_DEFAULT
argument_list|)
expr_stmt|;
block|}
name|isInited
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|getGroupNameMax ()
specifier|public
specifier|static
name|int
name|getGroupNameMax
parameter_list|()
block|{
if|if
condition|(
operator|!
name|isInited
condition|)
block|{
name|init
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
return|return
name|GROUP_NAME_MAX
return|;
block|}
DECL|method|getCounterNameMax ()
specifier|public
specifier|static
name|int
name|getCounterNameMax
parameter_list|()
block|{
if|if
condition|(
operator|!
name|isInited
condition|)
block|{
name|init
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
return|return
name|COUNTER_NAME_MAX
return|;
block|}
DECL|method|getGroupsMax ()
specifier|public
specifier|static
name|int
name|getGroupsMax
parameter_list|()
block|{
if|if
condition|(
operator|!
name|isInited
condition|)
block|{
name|init
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
return|return
name|GROUPS_MAX
return|;
block|}
DECL|method|getCountersMax ()
specifier|public
specifier|static
name|int
name|getCountersMax
parameter_list|()
block|{
if|if
condition|(
operator|!
name|isInited
condition|)
block|{
name|init
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
return|return
name|COUNTERS_MAX
return|;
block|}
DECL|method|filterName (String name, int maxLen)
specifier|public
specifier|static
name|String
name|filterName
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|maxLen
parameter_list|)
block|{
return|return
name|name
operator|.
name|length
argument_list|()
operator|>
name|maxLen
condition|?
name|name
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|maxLen
operator|-
literal|1
argument_list|)
else|:
name|name
return|;
block|}
DECL|method|filterCounterName (String name)
specifier|public
specifier|static
name|String
name|filterCounterName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|filterName
argument_list|(
name|name
argument_list|,
name|getCounterNameMax
argument_list|()
argument_list|)
return|;
block|}
DECL|method|filterGroupName (String name)
specifier|public
specifier|static
name|String
name|filterGroupName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|filterName
argument_list|(
name|name
argument_list|,
name|getGroupNameMax
argument_list|()
argument_list|)
return|;
block|}
DECL|method|checkCounters (int size)
specifier|public
specifier|synchronized
name|void
name|checkCounters
parameter_list|(
name|int
name|size
parameter_list|)
block|{
if|if
condition|(
name|firstViolation
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|LimitExceededException
argument_list|(
name|firstViolation
argument_list|)
throw|;
block|}
name|int
name|countersMax
init|=
name|getCountersMax
argument_list|()
decl_stmt|;
if|if
condition|(
name|size
operator|>
name|countersMax
condition|)
block|{
name|firstViolation
operator|=
operator|new
name|LimitExceededException
argument_list|(
literal|"Too many counters: "
operator|+
name|size
operator|+
literal|" max="
operator|+
name|countersMax
argument_list|)
expr_stmt|;
throw|throw
name|firstViolation
throw|;
block|}
block|}
DECL|method|incrCounters ()
specifier|public
specifier|synchronized
name|void
name|incrCounters
parameter_list|()
block|{
name|checkCounters
argument_list|(
name|totalCounters
operator|+
literal|1
argument_list|)
expr_stmt|;
operator|++
name|totalCounters
expr_stmt|;
block|}
DECL|method|checkGroups (int size)
specifier|public
specifier|synchronized
name|void
name|checkGroups
parameter_list|(
name|int
name|size
parameter_list|)
block|{
if|if
condition|(
name|firstViolation
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|LimitExceededException
argument_list|(
name|firstViolation
argument_list|)
throw|;
block|}
name|int
name|groupsMax
init|=
name|getGroupsMax
argument_list|()
decl_stmt|;
if|if
condition|(
name|size
operator|>
name|groupsMax
condition|)
block|{
name|firstViolation
operator|=
operator|new
name|LimitExceededException
argument_list|(
literal|"Too many counter groups: "
operator|+
name|size
operator|+
literal|" max="
operator|+
name|groupsMax
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|violation ()
specifier|public
specifier|synchronized
name|LimitExceededException
name|violation
parameter_list|()
block|{
return|return
name|firstViolation
return|;
block|}
block|}
end_class

end_unit

