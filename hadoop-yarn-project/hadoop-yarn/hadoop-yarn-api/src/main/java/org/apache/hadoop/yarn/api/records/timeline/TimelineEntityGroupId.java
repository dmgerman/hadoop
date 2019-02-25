begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.api.records.timeline
package|package
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
name|timeline
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
name|classification
operator|.
name|InterfaceAudience
operator|.
name|Private
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
operator|.
name|Public
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
operator|.
name|Unstable
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Splitter
import|;
end_import

begin_comment
comment|/**  *<p><code>TimelineEntityGroupId</code> is an abstract way for  * timeline service users to represent âa group of related timeline data.  * For example, all entities that represents one data flow DAG execution  * can be grouped into one timeline entity group.</p>  */
end_comment

begin_class
annotation|@
name|Public
annotation|@
name|Unstable
DECL|class|TimelineEntityGroupId
specifier|public
class|class
name|TimelineEntityGroupId
implements|implements
name|Comparable
argument_list|<
name|TimelineEntityGroupId
argument_list|>
block|{
DECL|field|SPLITTER
specifier|private
specifier|static
specifier|final
name|Splitter
name|SPLITTER
init|=
name|Splitter
operator|.
name|on
argument_list|(
literal|'_'
argument_list|)
operator|.
name|trimResults
argument_list|()
decl_stmt|;
DECL|field|applicationId
specifier|private
name|ApplicationId
name|applicationId
decl_stmt|;
DECL|field|id
specifier|private
name|String
name|id
decl_stmt|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|field|TIMELINE_ENTITY_GROUPID_STR_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|TIMELINE_ENTITY_GROUPID_STR_PREFIX
init|=
literal|"timelineEntityGroupId"
decl_stmt|;
DECL|method|TimelineEntityGroupId ()
specifier|public
name|TimelineEntityGroupId
parameter_list|()
block|{    }
DECL|method|newInstance (ApplicationId applicationId, String id)
specifier|public
specifier|static
name|TimelineEntityGroupId
name|newInstance
parameter_list|(
name|ApplicationId
name|applicationId
parameter_list|,
name|String
name|id
parameter_list|)
block|{
name|TimelineEntityGroupId
name|timelineEntityGroupId
init|=
operator|new
name|TimelineEntityGroupId
argument_list|()
decl_stmt|;
name|timelineEntityGroupId
operator|.
name|setApplicationId
argument_list|(
name|applicationId
argument_list|)
expr_stmt|;
name|timelineEntityGroupId
operator|.
name|setTimelineEntityGroupId
argument_list|(
name|id
argument_list|)
expr_stmt|;
return|return
name|timelineEntityGroupId
return|;
block|}
comment|/**    * Get the<code>ApplicationId</code> of the    *<code>TimelineEntityGroupId</code>.    *    * @return<code>ApplicationId</code> of the    *<code>TimelineEntityGroupId</code>    */
DECL|method|getApplicationId ()
specifier|public
name|ApplicationId
name|getApplicationId
parameter_list|()
block|{
return|return
name|this
operator|.
name|applicationId
return|;
block|}
DECL|method|setApplicationId (ApplicationId appID)
specifier|public
name|void
name|setApplicationId
parameter_list|(
name|ApplicationId
name|appID
parameter_list|)
block|{
name|this
operator|.
name|applicationId
operator|=
name|appID
expr_stmt|;
block|}
comment|/**    * Get the<code>timelineEntityGroupId</code>.    *    * @return<code>timelineEntityGroupId</code>    */
DECL|method|getTimelineEntityGroupId ()
specifier|public
name|String
name|getTimelineEntityGroupId
parameter_list|()
block|{
return|return
name|this
operator|.
name|id
return|;
block|}
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setTimelineEntityGroupId (String timelineEntityGroupId)
specifier|protected
name|void
name|setTimelineEntityGroupId
parameter_list|(
name|String
name|timelineEntityGroupId
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|timelineEntityGroupId
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|result
init|=
name|getTimelineEntityGroupId
argument_list|()
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|getApplicationId
argument_list|()
operator|.
name|hashCode
argument_list|()
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|equals (Object obj)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|TimelineEntityGroupId
name|otherObject
init|=
operator|(
name|TimelineEntityGroupId
operator|)
name|obj
decl_stmt|;
if|if
condition|(
operator|!
name|this
operator|.
name|getApplicationId
argument_list|()
operator|.
name|equals
argument_list|(
name|otherObject
operator|.
name|getApplicationId
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
name|this
operator|.
name|getTimelineEntityGroupId
argument_list|()
operator|.
name|equals
argument_list|(
name|otherObject
operator|.
name|getTimelineEntityGroupId
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|compareTo (TimelineEntityGroupId other)
specifier|public
name|int
name|compareTo
parameter_list|(
name|TimelineEntityGroupId
name|other
parameter_list|)
block|{
name|int
name|compareAppIds
init|=
name|this
operator|.
name|getApplicationId
argument_list|()
operator|.
name|compareTo
argument_list|(
name|other
operator|.
name|getApplicationId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|compareAppIds
operator|==
literal|0
condition|)
block|{
return|return
name|this
operator|.
name|getTimelineEntityGroupId
argument_list|()
operator|.
name|compareTo
argument_list|(
name|other
operator|.
name|getTimelineEntityGroupId
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|compareAppIds
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|TIMELINE_ENTITY_GROUPID_STR_PREFIX
operator|+
literal|"_"
argument_list|)
expr_stmt|;
name|ApplicationId
name|appId
init|=
name|getApplicationId
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|appId
operator|.
name|getClusterTimestamp
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"_"
argument_list|)
operator|.
name|append
argument_list|(
name|appId
operator|.
name|getId
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"_"
argument_list|)
operator|.
name|append
argument_list|(
name|getTimelineEntityGroupId
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
specifier|static
name|TimelineEntityGroupId
DECL|method|fromString (String timelineEntityGroupIdStr)
name|fromString
parameter_list|(
name|String
name|timelineEntityGroupIdStr
parameter_list|)
block|{
name|StringBuffer
name|buf
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|String
argument_list|>
name|it
init|=
name|SPLITTER
operator|.
name|split
argument_list|(
name|timelineEntityGroupIdStr
argument_list|)
operator|.
name|iterator
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|it
operator|.
name|next
argument_list|()
operator|.
name|equals
argument_list|(
name|TIMELINE_ENTITY_GROUPID_STR_PREFIX
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid TimelineEntityGroupId prefix: "
operator|+
name|timelineEntityGroupIdStr
argument_list|)
throw|;
block|}
name|ApplicationId
name|appId
init|=
name|ApplicationId
operator|.
name|newInstance
argument_list|(
name|Long
operator|.
name|parseLong
argument_list|(
name|it
operator|.
name|next
argument_list|()
argument_list|)
argument_list|,
name|Integer
operator|.
name|parseInt
argument_list|(
name|it
operator|.
name|next
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|it
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|"_"
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|it
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|TimelineEntityGroupId
operator|.
name|newInstance
argument_list|(
name|appId
argument_list|,
name|buf
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

