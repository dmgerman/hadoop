begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.lib.aggregate
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|lib
operator|.
name|aggregate
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
comment|/**  * This interface defines the minimal protocol for value aggregators.  *   */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|interface|ValueAggregator
specifier|public
interface|interface
name|ValueAggregator
parameter_list|<
name|E
parameter_list|>
block|{
comment|/**    * add a value to the aggregator    *     * @param val the value to be added    */
DECL|method|addNextValue (Object val)
specifier|public
name|void
name|addNextValue
parameter_list|(
name|Object
name|val
parameter_list|)
function_decl|;
comment|/**    * reset the aggregator    *    */
DECL|method|reset ()
specifier|public
name|void
name|reset
parameter_list|()
function_decl|;
comment|/**    * @return the string representation of the agregator    */
DECL|method|getReport ()
specifier|public
name|String
name|getReport
parameter_list|()
function_decl|;
comment|/**    *     * @return an array of values as the outputs of the combiner.    */
DECL|method|getCombinerOutput ()
specifier|public
name|ArrayList
argument_list|<
name|E
argument_list|>
name|getCombinerOutput
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

