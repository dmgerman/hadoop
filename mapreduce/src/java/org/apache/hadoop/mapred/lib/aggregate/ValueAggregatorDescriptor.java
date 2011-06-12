begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred.lib.aggregate
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|lib
operator|.
name|aggregate
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|Text
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
name|mapred
operator|.
name|JobConf
import|;
end_import

begin_comment
comment|/**  * This interface defines the contract a value aggregator descriptor must  * support. Such a descriptor can be configured with a JobConf object. Its main  * function is to generate a list of aggregation-id/value pairs. An aggregation  * id encodes an aggregation type which is used to guide the way to aggregate  * the value in the reduce/combiner phrase of an Aggregate based job.The mapper in  * an Aggregate based map/reduce job may create one or more of  * ValueAggregatorDescriptor objects at configuration time. For each input  * key/value pair, the mapper will use those objects to create aggregation  * id/value pairs.  *   * @deprecated Use   * {@link org.apache.hadoop.mapreduce.lib.aggregate.ValueAggregatorDescriptor}  * instead   */
end_comment

begin_interface
annotation|@
name|Deprecated
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|interface|ValueAggregatorDescriptor
specifier|public
interface|interface
name|ValueAggregatorDescriptor
extends|extends
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
operator|.
name|ValueAggregatorDescriptor
block|{
DECL|field|TYPE_SEPARATOR
specifier|public
specifier|static
specifier|final
name|String
name|TYPE_SEPARATOR
init|=
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
operator|.
name|ValueAggregatorDescriptor
operator|.
name|TYPE_SEPARATOR
decl_stmt|;
DECL|field|ONE
specifier|public
specifier|static
specifier|final
name|Text
name|ONE
init|=
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
operator|.
name|ValueAggregatorDescriptor
operator|.
name|ONE
decl_stmt|;
comment|/**    * Configure the object    *     * @param job    *          a JobConf object that may contain the information that can be used    *          to configure the object.    */
DECL|method|configure (JobConf job)
specifier|public
name|void
name|configure
parameter_list|(
name|JobConf
name|job
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

