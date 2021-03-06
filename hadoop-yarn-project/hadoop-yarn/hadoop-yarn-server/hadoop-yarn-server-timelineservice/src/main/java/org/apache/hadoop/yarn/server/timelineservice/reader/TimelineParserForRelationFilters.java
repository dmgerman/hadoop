begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timelineservice.reader
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
package|;
end_package

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
name|Set
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
name|server
operator|.
name|timelineservice
operator|.
name|reader
operator|.
name|filter
operator|.
name|TimelineCompareOp
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
name|server
operator|.
name|timelineservice
operator|.
name|reader
operator|.
name|filter
operator|.
name|TimelineFilter
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
name|server
operator|.
name|timelineservice
operator|.
name|reader
operator|.
name|filter
operator|.
name|TimelineKeyValuesFilter
import|;
end_import

begin_comment
comment|/**  * Used for parsing relation filters.  */
end_comment

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|TimelineParserForRelationFilters
class|class
name|TimelineParserForRelationFilters
extends|extends
name|TimelineParserForEqualityExpr
block|{
DECL|field|valueDelimiter
specifier|private
specifier|final
name|String
name|valueDelimiter
decl_stmt|;
DECL|method|TimelineParserForRelationFilters (String expression, char valuesDelim, String valueDelim)
specifier|public
name|TimelineParserForRelationFilters
parameter_list|(
name|String
name|expression
parameter_list|,
name|char
name|valuesDelim
parameter_list|,
name|String
name|valueDelim
parameter_list|)
block|{
name|super
argument_list|(
name|expression
argument_list|,
literal|"Relation Filter"
argument_list|,
name|valuesDelim
argument_list|)
expr_stmt|;
name|valueDelimiter
operator|=
name|valueDelim
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createFilter ()
specifier|protected
name|TimelineFilter
name|createFilter
parameter_list|()
block|{
return|return
operator|new
name|TimelineKeyValuesFilter
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setCompareOpToCurrentFilter (TimelineCompareOp compareOp)
specifier|protected
name|void
name|setCompareOpToCurrentFilter
parameter_list|(
name|TimelineCompareOp
name|compareOp
parameter_list|)
block|{
operator|(
operator|(
name|TimelineKeyValuesFilter
operator|)
name|getCurrentFilter
argument_list|()
operator|)
operator|.
name|setCompareOp
argument_list|(
name|compareOp
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setValueToCurrentFilter (String value)
specifier|protected
name|void
name|setValueToCurrentFilter
parameter_list|(
name|String
name|value
parameter_list|)
throws|throws
name|TimelineParseException
block|{
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
name|String
index|[]
name|pairStrs
init|=
name|value
operator|.
name|split
argument_list|(
name|valueDelimiter
argument_list|)
decl_stmt|;
if|if
condition|(
name|pairStrs
operator|.
name|length
operator|<
literal|2
condition|)
block|{
throw|throw
operator|new
name|TimelineParseException
argument_list|(
literal|"Invalid relation filter expression"
argument_list|)
throw|;
block|}
name|String
name|key
init|=
name|pairStrs
index|[
literal|0
index|]
operator|.
name|trim
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|Object
argument_list|>
name|values
init|=
operator|new
name|HashSet
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|pairStrs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|values
operator|.
name|add
argument_list|(
name|pairStrs
index|[
name|i
index|]
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
block|}
operator|(
operator|(
name|TimelineKeyValuesFilter
operator|)
name|getCurrentFilter
argument_list|()
operator|)
operator|.
name|setKeyAndValues
argument_list|(
name|key
argument_list|,
name|values
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

