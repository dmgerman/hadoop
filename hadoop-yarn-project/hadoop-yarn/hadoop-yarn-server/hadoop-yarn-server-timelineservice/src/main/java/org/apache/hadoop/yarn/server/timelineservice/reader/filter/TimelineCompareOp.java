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
comment|/**  *  Comparison Operators.  */
end_comment

begin_enum
annotation|@
name|Private
annotation|@
name|Unstable
DECL|enum|TimelineCompareOp
specifier|public
enum|enum
name|TimelineCompareOp
block|{
DECL|enumConstant|LESS_THAN
name|LESS_THAN
block|,
DECL|enumConstant|LESS_OR_EQUAL
name|LESS_OR_EQUAL
block|,
DECL|enumConstant|EQUAL
name|EQUAL
block|,
DECL|enumConstant|NOT_EQUAL
name|NOT_EQUAL
block|,
DECL|enumConstant|GREATER_OR_EQUAL
name|GREATER_OR_EQUAL
block|,
DECL|enumConstant|GREATER_THAN
name|GREATER_THAN
block|}
end_enum

end_unit

