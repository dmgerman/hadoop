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
comment|/**  * This class implements a value aggregator that maintain the smallest of   * a sequence of strings.  *   */
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
DECL|class|StringValueMin
specifier|public
class|class
name|StringValueMin
implements|implements
name|ValueAggregator
argument_list|<
name|String
argument_list|>
block|{
DECL|field|minVal
name|String
name|minVal
init|=
literal|null
decl_stmt|;
comment|/**    *  the default constructor    *    */
DECL|method|StringValueMin ()
specifier|public
name|StringValueMin
parameter_list|()
block|{
name|reset
argument_list|()
expr_stmt|;
block|}
comment|/**    * add a value to the aggregator    *     * @param val    *          a string.    *     */
DECL|method|addNextValue (Object val)
specifier|public
name|void
name|addNextValue
parameter_list|(
name|Object
name|val
parameter_list|)
block|{
name|String
name|newVal
init|=
name|val
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|minVal
operator|==
literal|null
operator|||
name|this
operator|.
name|minVal
operator|.
name|compareTo
argument_list|(
name|newVal
argument_list|)
operator|>
literal|0
condition|)
block|{
name|this
operator|.
name|minVal
operator|=
name|newVal
expr_stmt|;
block|}
block|}
comment|/**    * @return the aggregated value    */
DECL|method|getVal ()
specifier|public
name|String
name|getVal
parameter_list|()
block|{
return|return
name|this
operator|.
name|minVal
return|;
block|}
comment|/**    * @return the string representation of the aggregated value    */
DECL|method|getReport ()
specifier|public
name|String
name|getReport
parameter_list|()
block|{
return|return
name|minVal
return|;
block|}
comment|/**    * reset the aggregator    */
DECL|method|reset ()
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|minVal
operator|=
literal|null
expr_stmt|;
block|}
comment|/**    * @return return an array of one element. The element is a string    *         representation of the aggregated value. The return value is    *         expected to be used by the a combiner.    */
DECL|method|getCombinerOutput ()
specifier|public
name|ArrayList
argument_list|<
name|String
argument_list|>
name|getCombinerOutput
parameter_list|()
block|{
name|ArrayList
argument_list|<
name|String
argument_list|>
name|retv
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|retv
operator|.
name|add
argument_list|(
name|minVal
argument_list|)
expr_stmt|;
return|return
name|retv
return|;
block|}
block|}
end_class

end_unit

