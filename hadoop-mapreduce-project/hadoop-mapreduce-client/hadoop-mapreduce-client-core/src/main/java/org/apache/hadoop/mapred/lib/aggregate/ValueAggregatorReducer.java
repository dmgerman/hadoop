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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|io
operator|.
name|Writable
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
name|WritableComparable
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
name|OutputCollector
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
name|Reporter
import|;
end_import

begin_comment
comment|/**  * This class implements the generic reducer of Aggregate.  */
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
DECL|class|ValueAggregatorReducer
specifier|public
class|class
name|ValueAggregatorReducer
parameter_list|<
name|K1
extends|extends
name|WritableComparable
parameter_list|,
name|V1
extends|extends
name|Writable
parameter_list|>
extends|extends
name|ValueAggregatorJobBase
argument_list|<
name|K1
argument_list|,
name|V1
argument_list|>
block|{
comment|/**    * @param key    *          the key is expected to be a Text object, whose prefix indicates    *          the type of aggregation to aggregate the values. In effect, data    *          driven computing is achieved. It is assumed that each aggregator's    *          getReport method emits appropriate output for the aggregator. This    *          may be further customiized.    * @value the values to be aggregated    */
DECL|method|reduce (Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter)
specifier|public
name|void
name|reduce
parameter_list|(
name|Text
name|key
parameter_list|,
name|Iterator
argument_list|<
name|Text
argument_list|>
name|values
parameter_list|,
name|OutputCollector
argument_list|<
name|Text
argument_list|,
name|Text
argument_list|>
name|output
parameter_list|,
name|Reporter
name|reporter
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|keyStr
init|=
name|key
operator|.
name|toString
argument_list|()
decl_stmt|;
name|int
name|pos
init|=
name|keyStr
operator|.
name|indexOf
argument_list|(
name|ValueAggregatorDescriptor
operator|.
name|TYPE_SEPARATOR
argument_list|)
decl_stmt|;
name|String
name|type
init|=
name|keyStr
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|pos
argument_list|)
decl_stmt|;
name|keyStr
operator|=
name|keyStr
operator|.
name|substring
argument_list|(
name|pos
operator|+
name|ValueAggregatorDescriptor
operator|.
name|TYPE_SEPARATOR
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|ValueAggregator
name|aggregator
init|=
name|ValueAggregatorBaseDescriptor
operator|.
name|generateValueAggregator
argument_list|(
name|type
argument_list|)
decl_stmt|;
while|while
condition|(
name|values
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|aggregator
operator|.
name|addNextValue
argument_list|(
name|values
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|String
name|val
init|=
name|aggregator
operator|.
name|getReport
argument_list|()
decl_stmt|;
name|key
operator|=
operator|new
name|Text
argument_list|(
name|keyStr
argument_list|)
expr_stmt|;
name|output
operator|.
name|collect
argument_list|(
name|key
argument_list|,
operator|new
name|Text
argument_list|(
name|val
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Do nothing. Should not be called    */
DECL|method|map (K1 arg0, V1 arg1, OutputCollector<Text, Text> arg2, Reporter arg3)
specifier|public
name|void
name|map
parameter_list|(
name|K1
name|arg0
parameter_list|,
name|V1
name|arg1
parameter_list|,
name|OutputCollector
argument_list|<
name|Text
argument_list|,
name|Text
argument_list|>
name|arg2
parameter_list|,
name|Reporter
name|arg3
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"should not be called\n"
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

