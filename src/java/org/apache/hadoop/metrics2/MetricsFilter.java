begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.metrics2
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|metrics2
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
name|classification
operator|.
name|InterfaceStability
import|;
end_import

begin_comment
comment|/**  * The metrics filter interface  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|MetricsFilter
specifier|public
specifier|abstract
class|class
name|MetricsFilter
implements|implements
name|MetricsPlugin
block|{
comment|/**    * Whether to accept the name    * @param name  to filter on    * @return  true to accept; false otherwise.    */
DECL|method|accepts (String name)
specifier|public
specifier|abstract
name|boolean
name|accepts
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**    * Whether to accept the tag    * @param tag to filter on    * @return  true to accept; false otherwise    */
DECL|method|accepts (MetricsTag tag)
specifier|public
specifier|abstract
name|boolean
name|accepts
parameter_list|(
name|MetricsTag
name|tag
parameter_list|)
function_decl|;
comment|/**    * Whether to accept the tags    * @param tags to filter on    * @return  true to accept; false otherwise    */
DECL|method|accepts (Iterable<MetricsTag> tags)
specifier|public
specifier|abstract
name|boolean
name|accepts
parameter_list|(
name|Iterable
argument_list|<
name|MetricsTag
argument_list|>
name|tags
parameter_list|)
function_decl|;
comment|/**    * Whether to accept the record    * @param record  to filter on    * @return  true to accept; false otherwise.    */
DECL|method|accepts (MetricsRecord record)
specifier|public
name|boolean
name|accepts
parameter_list|(
name|MetricsRecord
name|record
parameter_list|)
block|{
return|return
name|accepts
argument_list|(
name|record
operator|.
name|tags
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

