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
comment|/**  * The metrics record builder interface  */
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
DECL|class|MetricsRecordBuilder
specifier|public
specifier|abstract
class|class
name|MetricsRecordBuilder
block|{
comment|/**    * Add a metrics value with metrics information    * @param info  metadata of the tag    * @param value of the tag    * @return self    */
DECL|method|tag (MetricsInfo info, String value)
specifier|public
specifier|abstract
name|MetricsRecordBuilder
name|tag
parameter_list|(
name|MetricsInfo
name|info
parameter_list|,
name|String
name|value
parameter_list|)
function_decl|;
comment|/**    * Add an immutable metrics tag object    * @param tag a pre-made tag object (potentially save an object construction)    * @return self    */
DECL|method|add (MetricsTag tag)
specifier|public
specifier|abstract
name|MetricsRecordBuilder
name|add
parameter_list|(
name|MetricsTag
name|tag
parameter_list|)
function_decl|;
comment|/**    * Add a pre-made immutable metric object    * @param metric  the pre-made metric to save an object construction    * @return self    */
DECL|method|add (AbstractMetric metric)
specifier|public
specifier|abstract
name|MetricsRecordBuilder
name|add
parameter_list|(
name|AbstractMetric
name|metric
parameter_list|)
function_decl|;
comment|/**    * Set the context tag    * @param value of the context    * @return self    */
DECL|method|setContext (String value)
specifier|public
specifier|abstract
name|MetricsRecordBuilder
name|setContext
parameter_list|(
name|String
name|value
parameter_list|)
function_decl|;
comment|/**    * Add an integer metric    * @param info  metadata of the metric    * @param value of the metric    * @return self    */
DECL|method|addCounter (MetricsInfo info, int value)
specifier|public
specifier|abstract
name|MetricsRecordBuilder
name|addCounter
parameter_list|(
name|MetricsInfo
name|info
parameter_list|,
name|int
name|value
parameter_list|)
function_decl|;
comment|/**    * Add an long metric    * @param info  metadata of the metric    * @param value of the metric    * @return self    */
DECL|method|addCounter (MetricsInfo info, long value)
specifier|public
specifier|abstract
name|MetricsRecordBuilder
name|addCounter
parameter_list|(
name|MetricsInfo
name|info
parameter_list|,
name|long
name|value
parameter_list|)
function_decl|;
comment|/**    * Add a integer gauge metric    * @param info  metadata of the metric    * @param value of the metric    * @return self    */
DECL|method|addGauge (MetricsInfo info, int value)
specifier|public
specifier|abstract
name|MetricsRecordBuilder
name|addGauge
parameter_list|(
name|MetricsInfo
name|info
parameter_list|,
name|int
name|value
parameter_list|)
function_decl|;
comment|/**    * Add a long gauge metric    * @param info  metadata of the metric    * @param value of the metric    * @return self    */
DECL|method|addGauge (MetricsInfo info, long value)
specifier|public
specifier|abstract
name|MetricsRecordBuilder
name|addGauge
parameter_list|(
name|MetricsInfo
name|info
parameter_list|,
name|long
name|value
parameter_list|)
function_decl|;
comment|/**    * Add a float gauge metric    * @param info  metadata of the metric    * @param value of the metric    * @return self    */
DECL|method|addGauge (MetricsInfo info, float value)
specifier|public
specifier|abstract
name|MetricsRecordBuilder
name|addGauge
parameter_list|(
name|MetricsInfo
name|info
parameter_list|,
name|float
name|value
parameter_list|)
function_decl|;
comment|/**    * Add a double gauge metric    * @param info  metadata of the metric    * @param value of the metric    * @return self    */
DECL|method|addGauge (MetricsInfo info, double value)
specifier|public
specifier|abstract
name|MetricsRecordBuilder
name|addGauge
parameter_list|(
name|MetricsInfo
name|info
parameter_list|,
name|double
name|value
parameter_list|)
function_decl|;
comment|/**    * @return the parent metrics collector object    */
DECL|method|parent ()
specifier|public
specifier|abstract
name|MetricsCollector
name|parent
parameter_list|()
function_decl|;
comment|/**    * Syntactic sugar to add multiple records in a collector in a one liner.    * @return the parent metrics collector object    */
DECL|method|endRecord ()
specifier|public
name|MetricsCollector
name|endRecord
parameter_list|()
block|{
return|return
name|parent
argument_list|()
return|;
block|}
block|}
end_class

end_unit

