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
name|mapred
operator|.
name|JobConf
import|;
end_import

begin_comment
comment|/**  * This class implements a wrapper for a user defined value aggregator   * descriptor.  * It serves two functions: One is to create an object of   * ValueAggregatorDescriptor from the name of a user defined class that may be   * dynamically loaded. The other is to delegate invocations of   * generateKeyValPairs function to the created object.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|class|UserDefinedValueAggregatorDescriptor
specifier|public
class|class
name|UserDefinedValueAggregatorDescriptor
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
name|UserDefinedValueAggregatorDescriptor
implements|implements
name|ValueAggregatorDescriptor
block|{
comment|/**    * Create an instance of the given class    * @param className the name of the class    * @return a dynamically created instance of the given class     */
DECL|method|createInstance (String className)
specifier|public
specifier|static
name|Object
name|createInstance
parameter_list|(
name|String
name|className
parameter_list|)
block|{
return|return
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
name|UserDefinedValueAggregatorDescriptor
operator|.
name|createInstance
argument_list|(
name|className
argument_list|)
return|;
block|}
comment|/**    *     * @param className the class name of the user defined descriptor class    * @param job a configure object used for decriptor configuration    */
DECL|method|UserDefinedValueAggregatorDescriptor (String className, JobConf job)
specifier|public
name|UserDefinedValueAggregatorDescriptor
parameter_list|(
name|String
name|className
parameter_list|,
name|JobConf
name|job
parameter_list|)
block|{
name|super
argument_list|(
name|className
argument_list|,
name|job
argument_list|)
expr_stmt|;
operator|(
operator|(
name|ValueAggregatorDescriptor
operator|)
name|theAggregatorDescriptor
operator|)
operator|.
name|configure
argument_list|(
name|job
argument_list|)
expr_stmt|;
block|}
comment|/**    *  Do nothing.    */
DECL|method|configure (JobConf job)
specifier|public
name|void
name|configure
parameter_list|(
name|JobConf
name|job
parameter_list|)
block|{    }
block|}
end_class

end_unit

