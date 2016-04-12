begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timelineservice.reader.filter
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
name|timelineservice
operator|.
name|reader
operator|.
name|filter
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
name|InterfaceStability
operator|.
name|Unstable
import|;
end_import

begin_comment
comment|/**  * Filter class which represents filter to be applied based on prefixes.  * Prefixes can either match or not match.  */
end_comment

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|TimelinePrefixFilter
specifier|public
class|class
name|TimelinePrefixFilter
extends|extends
name|TimelineFilter
block|{
DECL|field|compareOp
specifier|private
name|TimelineCompareOp
name|compareOp
decl_stmt|;
DECL|field|prefix
specifier|private
name|String
name|prefix
decl_stmt|;
DECL|method|TimelinePrefixFilter (TimelineCompareOp op, String prefix)
specifier|public
name|TimelinePrefixFilter
parameter_list|(
name|TimelineCompareOp
name|op
parameter_list|,
name|String
name|prefix
parameter_list|)
block|{
name|this
operator|.
name|prefix
operator|=
name|prefix
expr_stmt|;
if|if
condition|(
name|op
operator|!=
name|TimelineCompareOp
operator|.
name|EQUAL
operator|&&
name|op
operator|!=
name|TimelineCompareOp
operator|.
name|NOT_EQUAL
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"CompareOp for prefix filter should "
operator|+
literal|"be EQUAL or NOT_EQUAL"
argument_list|)
throw|;
block|}
name|this
operator|.
name|compareOp
operator|=
name|op
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getFilterType ()
specifier|public
name|TimelineFilterType
name|getFilterType
parameter_list|()
block|{
return|return
name|TimelineFilterType
operator|.
name|PREFIX
return|;
block|}
DECL|method|getPrefix ()
specifier|public
name|String
name|getPrefix
parameter_list|()
block|{
return|return
name|prefix
return|;
block|}
DECL|method|getCompareOp ()
specifier|public
name|TimelineCompareOp
name|getCompareOp
parameter_list|()
block|{
return|return
name|compareOp
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|String
operator|.
name|format
argument_list|(
literal|"%s (%s %s)"
argument_list|,
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|,
name|this
operator|.
name|compareOp
operator|.
name|name
argument_list|()
argument_list|,
name|this
operator|.
name|prefix
argument_list|)
return|;
block|}
block|}
end_class

end_unit

