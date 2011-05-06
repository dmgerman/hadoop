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
comment|/**  * A visitor interface for metrics  */
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
DECL|interface|MetricsVisitor
specifier|public
interface|interface
name|MetricsVisitor
block|{
comment|/**    * Callback for integer value gauges    * @param info  the metric info    * @param value of the metric    */
DECL|method|gauge (MetricsInfo info, int value)
specifier|public
name|void
name|gauge
parameter_list|(
name|MetricsInfo
name|info
parameter_list|,
name|int
name|value
parameter_list|)
function_decl|;
comment|/**    * Callback for long value gauges    * @param info  the metric info    * @param value of the metric    */
DECL|method|gauge (MetricsInfo info, long value)
specifier|public
name|void
name|gauge
parameter_list|(
name|MetricsInfo
name|info
parameter_list|,
name|long
name|value
parameter_list|)
function_decl|;
comment|/**    * Callback for float value gauges    * @param info  the metric info    * @param value of the metric    */
DECL|method|gauge (MetricsInfo info, float value)
specifier|public
name|void
name|gauge
parameter_list|(
name|MetricsInfo
name|info
parameter_list|,
name|float
name|value
parameter_list|)
function_decl|;
comment|/**    * Callback for double value gauges    * @param info  the metric info    * @param value of the metric    */
DECL|method|gauge (MetricsInfo info, double value)
specifier|public
name|void
name|gauge
parameter_list|(
name|MetricsInfo
name|info
parameter_list|,
name|double
name|value
parameter_list|)
function_decl|;
comment|/**    * Callback for integer value counters    * @param info  the metric info    * @param value of the metric    */
DECL|method|counter (MetricsInfo info, int value)
specifier|public
name|void
name|counter
parameter_list|(
name|MetricsInfo
name|info
parameter_list|,
name|int
name|value
parameter_list|)
function_decl|;
comment|/**    * Callback for long value counters    * @param info  the metric info    * @param value of the metric    */
DECL|method|counter (MetricsInfo info, long value)
specifier|public
name|void
name|counter
parameter_list|(
name|MetricsInfo
name|info
parameter_list|,
name|long
name|value
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

