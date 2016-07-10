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
name|io
operator|.
name|IOException
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
name|timeline
operator|.
name|GenericObjectMapper
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
name|TimelineCompareFilter
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
name|storage
operator|.
name|common
operator|.
name|TimelineStorageUtils
import|;
end_import

begin_comment
comment|/**  * Used for parsing numerical filters such as metric filters.  */
end_comment

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|TimelineParserForNumericFilters
class|class
name|TimelineParserForNumericFilters
extends|extends
name|TimelineParserForCompareExpr
block|{
DECL|method|TimelineParserForNumericFilters (String expression)
specifier|public
name|TimelineParserForNumericFilters
parameter_list|(
name|String
name|expression
parameter_list|)
block|{
name|super
argument_list|(
name|expression
argument_list|,
literal|"Metric Filter"
argument_list|)
expr_stmt|;
block|}
DECL|method|createFilter ()
specifier|protected
name|TimelineFilter
name|createFilter
parameter_list|()
block|{
return|return
operator|new
name|TimelineCompareFilter
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setCompareOpToCurrentFilter (TimelineCompareOp compareOp, boolean keyMustExistFlag)
specifier|protected
name|void
name|setCompareOpToCurrentFilter
parameter_list|(
name|TimelineCompareOp
name|compareOp
parameter_list|,
name|boolean
name|keyMustExistFlag
parameter_list|)
block|{
operator|(
operator|(
name|TimelineCompareFilter
operator|)
name|getCurrentFilter
argument_list|()
operator|)
operator|.
name|setCompareOp
argument_list|(
name|compareOp
argument_list|,
name|keyMustExistFlag
argument_list|)
expr_stmt|;
block|}
DECL|method|parseValue (String strValue)
specifier|protected
name|Object
name|parseValue
parameter_list|(
name|String
name|strValue
parameter_list|)
throws|throws
name|TimelineParseException
block|{
name|Object
name|value
init|=
literal|null
decl_stmt|;
try|try
block|{
name|value
operator|=
name|GenericObjectMapper
operator|.
name|OBJECT_READER
operator|.
name|readValue
argument_list|(
name|strValue
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|TimelineParseException
argument_list|(
literal|"Value cannot be parsed."
argument_list|)
throw|;
block|}
if|if
condition|(
name|value
operator|==
literal|null
operator|||
operator|!
operator|(
name|TimelineStorageUtils
operator|.
name|isIntegralValue
argument_list|(
name|value
argument_list|)
operator|)
condition|)
block|{
throw|throw
operator|new
name|TimelineParseException
argument_list|(
literal|"Value is not a number."
argument_list|)
throw|;
block|}
return|return
name|value
return|;
block|}
DECL|method|setValueToCurrentFilter (Object value)
specifier|protected
name|void
name|setValueToCurrentFilter
parameter_list|(
name|Object
name|value
parameter_list|)
block|{
name|TimelineFilter
name|currentFilter
init|=
name|getCurrentFilter
argument_list|()
decl_stmt|;
if|if
condition|(
name|currentFilter
operator|!=
literal|null
condition|)
block|{
operator|(
operator|(
name|TimelineCompareFilter
operator|)
name|currentFilter
operator|)
operator|.
name|setValue
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

