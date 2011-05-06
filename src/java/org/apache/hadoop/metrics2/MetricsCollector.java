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
comment|/**  * The metrics collector interface  */
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
DECL|interface|MetricsCollector
specifier|public
interface|interface
name|MetricsCollector
block|{
comment|/**    * Add a metrics record    * @param name  of the record    * @return  a metrics record builder for the record    */
DECL|method|addRecord (String name)
specifier|public
name|MetricsRecordBuilder
name|addRecord
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**    * Add a metrics record    * @param info  of the record    * @return  a metrics record builder for the record    */
DECL|method|addRecord (MetricsInfo info)
specifier|public
name|MetricsRecordBuilder
name|addRecord
parameter_list|(
name|MetricsInfo
name|info
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

