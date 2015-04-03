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
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|counters
operator|.
name|CounterGroupFactory
operator|.
name|getFrameworkGroupId
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
name|counters
operator|.
name|CounterGroupFactory
operator|.
name|isFrameworkGroup
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutput
import|;
end_import

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
name|HashSet
import|;
end_import

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
name|java
operator|.
name|util
operator|.
name|Map
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
name|ConcurrentSkipListMap
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|io
operator|.
name|Text
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
name|io
operator|.
name|Writable
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
name|io
operator|.
name|WritableUtils
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
name|Counter
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
name|FileSystemCounter
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
name|JobCounter
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
name|TaskCounter
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
name|StringInterner
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
name|Iterables
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
name|Maps
import|;
end_import

begin_comment
comment|/**  * An abstract class to provide common implementation for the Counters  * container in both mapred and mapreduce packages.  *  * @param<C> type of counter inside the counters  * @param<G> type of group inside the counters  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|class|AbstractCounters
specifier|public
specifier|abstract
class|class
name|AbstractCounters
parameter_list|<
name|C
extends|extends
name|Counter
parameter_list|,
name|G
extends|extends
name|CounterGroupBase
parameter_list|<
name|C
parameter_list|>
parameter_list|>
implements|implements
name|Writable
implements|,
name|Iterable
argument_list|<
name|G
argument_list|>
block|{
DECL|field|LOG
specifier|protected
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
literal|"mapreduce.Counters"
argument_list|)
decl_stmt|;
comment|/**    * A cache from enum values to the associated counter.    */
DECL|field|cache
specifier|private
specifier|final
name|Map
argument_list|<
name|Enum
argument_list|<
name|?
argument_list|>
argument_list|,
name|C
argument_list|>
name|cache
init|=
name|Maps
operator|.
name|newIdentityHashMap
argument_list|()
decl_stmt|;
comment|//framework& fs groups
DECL|field|fgroups
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|G
argument_list|>
name|fgroups
init|=
operator|new
name|ConcurrentSkipListMap
argument_list|<
name|String
argument_list|,
name|G
argument_list|>
argument_list|()
decl_stmt|;
comment|// other groups
DECL|field|groups
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|G
argument_list|>
name|groups
init|=
operator|new
name|ConcurrentSkipListMap
argument_list|<
name|String
argument_list|,
name|G
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|groupFactory
specifier|private
specifier|final
name|CounterGroupFactory
argument_list|<
name|C
argument_list|,
name|G
argument_list|>
name|groupFactory
decl_stmt|;
comment|// For framework counter serialization without strings
DECL|enum|GroupType
DECL|enumConstant|FRAMEWORK
DECL|enumConstant|FILESYSTEM
enum|enum
name|GroupType
block|{
name|FRAMEWORK
block|,
name|FILESYSTEM
block|}
empty_stmt|;
comment|// Writes only framework and fs counters if false.
DECL|field|writeAllCounters
specifier|private
name|boolean
name|writeAllCounters
init|=
literal|true
decl_stmt|;
DECL|field|legacyMap
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|legacyMap
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
static|static
block|{
name|legacyMap
operator|.
name|put
argument_list|(
literal|"org.apache.hadoop.mapred.Task$Counter"
argument_list|,
name|TaskCounter
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|legacyMap
operator|.
name|put
argument_list|(
literal|"org.apache.hadoop.mapred.JobInProgress$Counter"
argument_list|,
name|JobCounter
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|legacyMap
operator|.
name|put
argument_list|(
literal|"FileSystemCounters"
argument_list|,
name|FileSystemCounter
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|field|limits
specifier|private
specifier|final
name|Limits
name|limits
init|=
operator|new
name|Limits
argument_list|()
decl_stmt|;
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|method|AbstractCounters (CounterGroupFactory<C, G> gf)
specifier|public
name|AbstractCounters
parameter_list|(
name|CounterGroupFactory
argument_list|<
name|C
argument_list|,
name|G
argument_list|>
name|gf
parameter_list|)
block|{
name|groupFactory
operator|=
name|gf
expr_stmt|;
block|}
comment|/**    * Construct from another counters object.    * @param<C1> type of the other counter    * @param<G1> type of the other counter group    * @param counters the counters object to copy    * @param groupFactory the factory for new groups    */
annotation|@
name|InterfaceAudience
operator|.
name|Private
specifier|public
parameter_list|<
name|C1
extends|extends
name|Counter
parameter_list|,
name|G1
extends|extends
name|CounterGroupBase
argument_list|<
name|C1
argument_list|>
parameter_list|>
DECL|method|AbstractCounters (AbstractCounters<C1, G1> counters, CounterGroupFactory<C, G> groupFactory)
name|AbstractCounters
parameter_list|(
name|AbstractCounters
argument_list|<
name|C1
argument_list|,
name|G1
argument_list|>
name|counters
parameter_list|,
name|CounterGroupFactory
argument_list|<
name|C
argument_list|,
name|G
argument_list|>
name|groupFactory
parameter_list|)
block|{
name|this
operator|.
name|groupFactory
operator|=
name|groupFactory
expr_stmt|;
for|for
control|(
name|G1
name|group
range|:
name|counters
control|)
block|{
name|String
name|name
init|=
name|group
operator|.
name|getName
argument_list|()
decl_stmt|;
name|G
name|newGroup
init|=
name|groupFactory
operator|.
name|newGroup
argument_list|(
name|name
argument_list|,
name|group
operator|.
name|getDisplayName
argument_list|()
argument_list|,
name|limits
argument_list|)
decl_stmt|;
operator|(
name|isFrameworkGroup
argument_list|(
name|name
argument_list|)
condition|?
name|fgroups
else|:
name|groups
operator|)
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|newGroup
argument_list|)
expr_stmt|;
for|for
control|(
name|Counter
name|counter
range|:
name|group
control|)
block|{
name|newGroup
operator|.
name|addCounter
argument_list|(
name|counter
operator|.
name|getName
argument_list|()
argument_list|,
name|counter
operator|.
name|getDisplayName
argument_list|()
argument_list|,
name|counter
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/** Add a group.    * @param group object to add    * @return the group    */
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|method|addGroup (G group)
specifier|public
specifier|synchronized
name|G
name|addGroup
parameter_list|(
name|G
name|group
parameter_list|)
block|{
name|String
name|name
init|=
name|group
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|isFrameworkGroup
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|fgroups
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|group
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|limits
operator|.
name|checkGroups
argument_list|(
name|groups
operator|.
name|size
argument_list|()
operator|+
literal|1
argument_list|)
expr_stmt|;
name|groups
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|group
argument_list|)
expr_stmt|;
block|}
return|return
name|group
return|;
block|}
comment|/**    * Add a new group    * @param name of the group    * @param displayName of the group    * @return the group    */
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|method|addGroup (String name, String displayName)
specifier|public
name|G
name|addGroup
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|displayName
parameter_list|)
block|{
return|return
name|addGroup
argument_list|(
name|groupFactory
operator|.
name|newGroup
argument_list|(
name|name
argument_list|,
name|displayName
argument_list|,
name|limits
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Find a counter, create one if necessary    * @param groupName of the counter    * @param counterName name of the counter    * @return the matching counter    */
DECL|method|findCounter (String groupName, String counterName)
specifier|public
name|C
name|findCounter
parameter_list|(
name|String
name|groupName
parameter_list|,
name|String
name|counterName
parameter_list|)
block|{
name|G
name|grp
init|=
name|getGroup
argument_list|(
name|groupName
argument_list|)
decl_stmt|;
return|return
name|grp
operator|.
name|findCounter
argument_list|(
name|counterName
argument_list|)
return|;
block|}
comment|/**    * Find the counter for the given enum. The same enum will always return the    * same counter.    * @param key the counter key    * @return the matching counter object    */
DECL|method|findCounter (Enum<?> key)
specifier|public
specifier|synchronized
name|C
name|findCounter
parameter_list|(
name|Enum
argument_list|<
name|?
argument_list|>
name|key
parameter_list|)
block|{
name|C
name|counter
init|=
name|cache
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|counter
operator|==
literal|null
condition|)
block|{
name|counter
operator|=
name|findCounter
argument_list|(
name|key
operator|.
name|getDeclaringClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|key
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|cache
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|counter
argument_list|)
expr_stmt|;
block|}
return|return
name|counter
return|;
block|}
comment|/**    * Find the file system counter for the given scheme and enum.    * @param scheme of the file system    * @param key the enum of the counter    * @return the file system counter    */
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|method|findCounter (String scheme, FileSystemCounter key)
specifier|public
specifier|synchronized
name|C
name|findCounter
parameter_list|(
name|String
name|scheme
parameter_list|,
name|FileSystemCounter
name|key
parameter_list|)
block|{
return|return
operator|(
operator|(
name|FileSystemCounterGroup
argument_list|<
name|C
argument_list|>
operator|)
name|getGroup
argument_list|(
name|FileSystemCounter
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|getUnderlyingGroup
argument_list|()
operator|)
operator|.
name|findCounter
argument_list|(
name|scheme
argument_list|,
name|key
argument_list|)
return|;
block|}
comment|/**    * Returns the names of all counter classes.    * @return Set of counter names.    */
DECL|method|getGroupNames ()
specifier|public
specifier|synchronized
name|Iterable
argument_list|<
name|String
argument_list|>
name|getGroupNames
parameter_list|()
block|{
name|HashSet
argument_list|<
name|String
argument_list|>
name|deprecated
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|legacyMap
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|newGroup
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|boolean
name|isFGroup
init|=
name|isFrameworkGroup
argument_list|(
name|newGroup
argument_list|)
decl_stmt|;
if|if
condition|(
name|isFGroup
condition|?
name|fgroups
operator|.
name|containsKey
argument_list|(
name|newGroup
argument_list|)
else|:
name|groups
operator|.
name|containsKey
argument_list|(
name|newGroup
argument_list|)
condition|)
block|{
name|deprecated
operator|.
name|add
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|Iterables
operator|.
name|concat
argument_list|(
name|fgroups
operator|.
name|keySet
argument_list|()
argument_list|,
name|groups
operator|.
name|keySet
argument_list|()
argument_list|,
name|deprecated
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|iterator ()
specifier|public
name|Iterator
argument_list|<
name|G
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|Iterators
operator|.
name|concat
argument_list|(
name|fgroups
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
argument_list|,
name|groups
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Returns the named counter group, or an empty group if there is none    * with the specified name.    * @param groupName name of the group    * @return the group    */
DECL|method|getGroup (String groupName)
specifier|public
specifier|synchronized
name|G
name|getGroup
parameter_list|(
name|String
name|groupName
parameter_list|)
block|{
comment|// filterGroupName
name|boolean
name|groupNameInLegacyMap
init|=
literal|true
decl_stmt|;
name|String
name|newGroupName
init|=
name|legacyMap
operator|.
name|get
argument_list|(
name|groupName
argument_list|)
decl_stmt|;
if|if
condition|(
name|newGroupName
operator|==
literal|null
condition|)
block|{
name|groupNameInLegacyMap
operator|=
literal|false
expr_stmt|;
name|newGroupName
operator|=
name|Limits
operator|.
name|filterGroupName
argument_list|(
name|groupName
argument_list|)
expr_stmt|;
block|}
name|boolean
name|isFGroup
init|=
name|isFrameworkGroup
argument_list|(
name|newGroupName
argument_list|)
decl_stmt|;
name|G
name|group
init|=
name|isFGroup
condition|?
name|fgroups
operator|.
name|get
argument_list|(
name|newGroupName
argument_list|)
else|:
name|groups
operator|.
name|get
argument_list|(
name|newGroupName
argument_list|)
decl_stmt|;
if|if
condition|(
name|group
operator|==
literal|null
condition|)
block|{
name|group
operator|=
name|groupFactory
operator|.
name|newGroup
argument_list|(
name|newGroupName
argument_list|,
name|limits
argument_list|)
expr_stmt|;
if|if
condition|(
name|isFGroup
condition|)
block|{
name|fgroups
operator|.
name|put
argument_list|(
name|newGroupName
argument_list|,
name|group
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|limits
operator|.
name|checkGroups
argument_list|(
name|groups
operator|.
name|size
argument_list|()
operator|+
literal|1
argument_list|)
expr_stmt|;
name|groups
operator|.
name|put
argument_list|(
name|newGroupName
argument_list|,
name|group
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|groupNameInLegacyMap
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Group "
operator|+
name|groupName
operator|+
literal|" is deprecated. Use "
operator|+
name|newGroupName
operator|+
literal|" instead"
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|group
return|;
block|}
comment|/**    * Returns the total number of counters, by summing the number of counters    * in each group.    * @return the total number of counters    */
DECL|method|countCounters ()
specifier|public
specifier|synchronized
name|int
name|countCounters
parameter_list|()
block|{
name|int
name|result
init|=
literal|0
decl_stmt|;
for|for
control|(
name|G
name|group
range|:
name|this
control|)
block|{
name|result
operator|+=
name|group
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/**    * Write the set of groups.    * Counters ::= version #fgroups (groupId, group)* #groups (group)*    */
annotation|@
name|Override
DECL|method|write (DataOutput out)
specifier|public
specifier|synchronized
name|void
name|write
parameter_list|(
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|WritableUtils
operator|.
name|writeVInt
argument_list|(
name|out
argument_list|,
name|groupFactory
operator|.
name|version
argument_list|()
argument_list|)
expr_stmt|;
name|WritableUtils
operator|.
name|writeVInt
argument_list|(
name|out
argument_list|,
name|fgroups
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// framework groups first
for|for
control|(
name|G
name|group
range|:
name|fgroups
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|group
operator|.
name|getUnderlyingGroup
argument_list|()
operator|instanceof
name|FrameworkCounterGroup
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
condition|)
block|{
name|WritableUtils
operator|.
name|writeVInt
argument_list|(
name|out
argument_list|,
name|GroupType
operator|.
name|FRAMEWORK
operator|.
name|ordinal
argument_list|()
argument_list|)
expr_stmt|;
name|WritableUtils
operator|.
name|writeVInt
argument_list|(
name|out
argument_list|,
name|getFrameworkGroupId
argument_list|(
name|group
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|group
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|group
operator|.
name|getUnderlyingGroup
argument_list|()
operator|instanceof
name|FileSystemCounterGroup
argument_list|<
name|?
argument_list|>
condition|)
block|{
name|WritableUtils
operator|.
name|writeVInt
argument_list|(
name|out
argument_list|,
name|GroupType
operator|.
name|FILESYSTEM
operator|.
name|ordinal
argument_list|()
argument_list|)
expr_stmt|;
name|group
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|writeAllCounters
condition|)
block|{
name|WritableUtils
operator|.
name|writeVInt
argument_list|(
name|out
argument_list|,
name|groups
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|G
name|group
range|:
name|groups
operator|.
name|values
argument_list|()
control|)
block|{
name|Text
operator|.
name|writeString
argument_list|(
name|out
argument_list|,
name|group
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|group
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|WritableUtils
operator|.
name|writeVInt
argument_list|(
name|out
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|readFields (DataInput in)
specifier|public
specifier|synchronized
name|void
name|readFields
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|version
init|=
name|WritableUtils
operator|.
name|readVInt
argument_list|(
name|in
argument_list|)
decl_stmt|;
if|if
condition|(
name|version
operator|!=
name|groupFactory
operator|.
name|version
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Counters version mismatch, expected "
operator|+
name|groupFactory
operator|.
name|version
argument_list|()
operator|+
literal|" got "
operator|+
name|version
argument_list|)
throw|;
block|}
name|int
name|numFGroups
init|=
name|WritableUtils
operator|.
name|readVInt
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|fgroups
operator|.
name|clear
argument_list|()
expr_stmt|;
name|GroupType
index|[]
name|groupTypes
init|=
name|GroupType
operator|.
name|values
argument_list|()
decl_stmt|;
while|while
condition|(
name|numFGroups
operator|--
operator|>
literal|0
condition|)
block|{
name|GroupType
name|groupType
init|=
name|groupTypes
index|[
name|WritableUtils
operator|.
name|readVInt
argument_list|(
name|in
argument_list|)
index|]
decl_stmt|;
name|G
name|group
decl_stmt|;
switch|switch
condition|(
name|groupType
condition|)
block|{
case|case
name|FILESYSTEM
case|:
comment|// with nothing
name|group
operator|=
name|groupFactory
operator|.
name|newFileSystemGroup
argument_list|()
expr_stmt|;
break|break;
case|case
name|FRAMEWORK
case|:
comment|// with group id
name|group
operator|=
name|groupFactory
operator|.
name|newFrameworkGroup
argument_list|(
name|WritableUtils
operator|.
name|readVInt
argument_list|(
name|in
argument_list|)
argument_list|)
expr_stmt|;
break|break;
default|default:
comment|// Silence dumb compiler, as it would've thrown earlier
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unexpected counter group type: "
operator|+
name|groupType
argument_list|)
throw|;
block|}
name|group
operator|.
name|readFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|fgroups
operator|.
name|put
argument_list|(
name|group
operator|.
name|getName
argument_list|()
argument_list|,
name|group
argument_list|)
expr_stmt|;
block|}
name|int
name|numGroups
init|=
name|WritableUtils
operator|.
name|readVInt
argument_list|(
name|in
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|groups
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|groups
operator|.
name|clear
argument_list|()
expr_stmt|;
name|limits
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
while|while
condition|(
name|numGroups
operator|--
operator|>
literal|0
condition|)
block|{
name|limits
operator|.
name|checkGroups
argument_list|(
name|groups
operator|.
name|size
argument_list|()
operator|+
literal|1
argument_list|)
expr_stmt|;
name|G
name|group
init|=
name|groupFactory
operator|.
name|newGenericGroup
argument_list|(
name|StringInterner
operator|.
name|weakIntern
argument_list|(
name|Text
operator|.
name|readString
argument_list|(
name|in
argument_list|)
argument_list|)
argument_list|,
literal|null
argument_list|,
name|limits
argument_list|)
decl_stmt|;
name|group
operator|.
name|readFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|groups
operator|.
name|put
argument_list|(
name|group
operator|.
name|getName
argument_list|()
argument_list|,
name|group
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Return textual representation of the counter values.    * @return the string    */
annotation|@
name|Override
DECL|method|toString ()
specifier|public
specifier|synchronized
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"Counters: "
operator|+
name|countCounters
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|G
name|group
range|:
name|this
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"\n\t"
argument_list|)
operator|.
name|append
argument_list|(
name|group
operator|.
name|getDisplayName
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Counter
name|counter
range|:
name|group
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"\n\t\t"
argument_list|)
operator|.
name|append
argument_list|(
name|counter
operator|.
name|getDisplayName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"="
argument_list|)
operator|.
name|append
argument_list|(
name|counter
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Increments multiple counters by their amounts in another Counters    * instance.    * @param other the other Counters instance    */
DECL|method|incrAllCounters (AbstractCounters<C, G> other)
specifier|public
specifier|synchronized
name|void
name|incrAllCounters
parameter_list|(
name|AbstractCounters
argument_list|<
name|C
argument_list|,
name|G
argument_list|>
name|other
parameter_list|)
block|{
for|for
control|(
name|G
name|right
range|:
name|other
control|)
block|{
name|String
name|groupName
init|=
name|right
operator|.
name|getName
argument_list|()
decl_stmt|;
name|G
name|left
init|=
operator|(
name|isFrameworkGroup
argument_list|(
name|groupName
argument_list|)
condition|?
name|fgroups
else|:
name|groups
operator|)
operator|.
name|get
argument_list|(
name|groupName
argument_list|)
decl_stmt|;
if|if
condition|(
name|left
operator|==
literal|null
condition|)
block|{
name|left
operator|=
name|addGroup
argument_list|(
name|groupName
argument_list|,
name|right
operator|.
name|getDisplayName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|left
operator|.
name|incrAllCounters
argument_list|(
name|right
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|equals (Object genericRight)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|genericRight
parameter_list|)
block|{
if|if
condition|(
name|genericRight
operator|instanceof
name|AbstractCounters
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
condition|)
block|{
return|return
name|Iterators
operator|.
name|elementsEqual
argument_list|(
name|iterator
argument_list|()
argument_list|,
operator|(
operator|(
name|AbstractCounters
argument_list|<
name|C
argument_list|,
name|G
argument_list|>
operator|)
name|genericRight
operator|)
operator|.
name|iterator
argument_list|()
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|groups
operator|.
name|hashCode
argument_list|()
return|;
block|}
comment|/**    * Set the "writeAllCounters" option to true or false    * @param send  if true all counters would be serialized, otherwise only    *              framework counters would be serialized in    *              {@link #write(DataOutput)}    */
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|method|setWriteAllCounters (boolean send)
specifier|public
name|void
name|setWriteAllCounters
parameter_list|(
name|boolean
name|send
parameter_list|)
block|{
name|writeAllCounters
operator|=
name|send
expr_stmt|;
block|}
comment|/**    * Get the "writeAllCounters" option    * @return true of all counters would serialized    */
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|method|getWriteAllCounters ()
specifier|public
name|boolean
name|getWriteAllCounters
parameter_list|()
block|{
return|return
name|writeAllCounters
return|;
block|}
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|method|limits ()
specifier|public
name|Limits
name|limits
parameter_list|()
block|{
return|return
name|limits
return|;
block|}
block|}
end_class

end_unit

