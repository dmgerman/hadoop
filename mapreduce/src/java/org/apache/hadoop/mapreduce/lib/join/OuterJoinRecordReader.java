begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.lib.join
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
name|join
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
name|conf
operator|.
name|Configuration
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
name|io
operator|.
name|WritableComparator
import|;
end_import

begin_comment
comment|/**  * Full outer join.  */
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
DECL|class|OuterJoinRecordReader
specifier|public
class|class
name|OuterJoinRecordReader
parameter_list|<
name|K
extends|extends
name|WritableComparable
parameter_list|<
name|?
parameter_list|>
parameter_list|>
extends|extends
name|JoinRecordReader
argument_list|<
name|K
argument_list|>
block|{
DECL|method|OuterJoinRecordReader (int id, Configuration conf, int capacity, Class<? extends WritableComparator> cmpcl)
name|OuterJoinRecordReader
parameter_list|(
name|int
name|id
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|int
name|capacity
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|WritableComparator
argument_list|>
name|cmpcl
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|id
argument_list|,
name|conf
argument_list|,
name|capacity
argument_list|,
name|cmpcl
argument_list|)
expr_stmt|;
block|}
comment|/**    * Emit everything from the collector.    */
DECL|method|combine (Object[] srcs, TupleWritable dst)
specifier|protected
name|boolean
name|combine
parameter_list|(
name|Object
index|[]
name|srcs
parameter_list|,
name|TupleWritable
name|dst
parameter_list|)
block|{
assert|assert
name|srcs
operator|.
name|length
operator|==
name|dst
operator|.
name|size
argument_list|()
assert|;
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

