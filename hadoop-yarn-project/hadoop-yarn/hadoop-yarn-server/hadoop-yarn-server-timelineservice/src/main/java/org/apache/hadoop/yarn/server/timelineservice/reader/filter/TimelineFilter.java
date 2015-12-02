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
comment|/**  * Abstract base class extended to implement timeline filters.  */
end_comment

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|TimelineFilter
specifier|public
specifier|abstract
class|class
name|TimelineFilter
block|{
comment|/**    * Lists the different filter types.    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|enum|TimelineFilterType
specifier|public
enum|enum
name|TimelineFilterType
block|{
comment|/**      * Combines multiple filters.      */
DECL|enumConstant|LIST
name|LIST
block|,
comment|/**      * Filter which is used for comparison.      */
DECL|enumConstant|COMPARE
name|COMPARE
block|,
comment|/**      * Filter which matches prefix for a config or a metric.      */
DECL|enumConstant|PREFIX
name|PREFIX
block|}
DECL|method|getFilterType ()
specifier|public
specifier|abstract
name|TimelineFilterType
name|getFilterType
parameter_list|()
function_decl|;
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
return|;
block|}
block|}
end_class

end_unit

