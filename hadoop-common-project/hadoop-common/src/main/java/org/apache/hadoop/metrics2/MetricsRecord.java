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
name|java
operator|.
name|util
operator|.
name|Collection
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

begin_comment
comment|/**  * An immutable snapshot of metrics with a timestamp  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|interface|MetricsRecord
specifier|public
interface|interface
name|MetricsRecord
block|{
comment|/**    * Get the timestamp of the metrics    * @return  the timestamp    */
DECL|method|timestamp ()
name|long
name|timestamp
parameter_list|()
function_decl|;
comment|/**    * @return the metrics record name    */
DECL|method|name ()
name|String
name|name
parameter_list|()
function_decl|;
comment|/**    * @return the description of the metrics record    */
DECL|method|description ()
name|String
name|description
parameter_list|()
function_decl|;
comment|/**    * @return the context name of the metrics record    */
DECL|method|context ()
name|String
name|context
parameter_list|()
function_decl|;
comment|/**    * Get the tags of the record    * Note: returning a collection instead of iterable as we    * need to use tags as keys (hence Collection#hashCode etc.) in maps    * @return an unmodifiable collection of tags    */
DECL|method|tags ()
name|Collection
argument_list|<
name|MetricsTag
argument_list|>
name|tags
parameter_list|()
function_decl|;
comment|/**    * Get the metrics of the record    * @return an immutable iterable interface for metrics    */
DECL|method|metrics ()
name|Iterable
argument_list|<
name|AbstractMetric
argument_list|>
name|metrics
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

