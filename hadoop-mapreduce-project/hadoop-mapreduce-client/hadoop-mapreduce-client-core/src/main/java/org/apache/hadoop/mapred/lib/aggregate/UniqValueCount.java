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

begin_comment
comment|/**  * This class implements a value aggregator that dedupes a sequence of objects.  */
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
DECL|class|UniqValueCount
specifier|public
class|class
name|UniqValueCount
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
name|UniqValueCount
implements|implements
name|ValueAggregator
argument_list|<
name|Object
argument_list|>
block|{
comment|/**    * the default constructor    *     */
DECL|method|UniqValueCount ()
specifier|public
name|UniqValueCount
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
comment|/**    * constructor    * @param maxNum the limit in the number of unique values to keep.    *      */
DECL|method|UniqValueCount (long maxNum)
specifier|public
name|UniqValueCount
parameter_list|(
name|long
name|maxNum
parameter_list|)
block|{
name|super
argument_list|(
name|maxNum
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

