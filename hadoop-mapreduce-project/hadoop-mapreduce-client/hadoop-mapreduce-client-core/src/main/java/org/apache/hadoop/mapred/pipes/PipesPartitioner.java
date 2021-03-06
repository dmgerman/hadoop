begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred.pipes
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|pipes
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
name|JobConf
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
name|Partitioner
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
name|util
operator|.
name|ReflectionUtils
import|;
end_import

begin_comment
comment|/**  * This partitioner is one that can either be set manually per a record or it  * can fall back onto a Java partitioner that was set by the user.  */
end_comment

begin_class
DECL|class|PipesPartitioner
class|class
name|PipesPartitioner
parameter_list|<
name|K
extends|extends
name|WritableComparable
parameter_list|,
name|V
extends|extends
name|Writable
parameter_list|>
implements|implements
name|Partitioner
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
block|{
DECL|field|CACHE
specifier|private
specifier|static
specifier|final
name|ThreadLocal
argument_list|<
name|Integer
argument_list|>
name|CACHE
init|=
operator|new
name|ThreadLocal
argument_list|<
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|part
specifier|private
name|Partitioner
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|part
init|=
literal|null
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|configure (JobConf conf)
specifier|public
name|void
name|configure
parameter_list|(
name|JobConf
name|conf
parameter_list|)
block|{
name|part
operator|=
name|ReflectionUtils
operator|.
name|newInstance
argument_list|(
name|Submitter
operator|.
name|getJavaPartitioner
argument_list|(
name|conf
argument_list|)
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
comment|/**    * Set the next key to have the given partition.    * @param newValue the next partition value    */
DECL|method|setNextPartition (int newValue)
specifier|static
name|void
name|setNextPartition
parameter_list|(
name|int
name|newValue
parameter_list|)
block|{
name|CACHE
operator|.
name|set
argument_list|(
name|newValue
argument_list|)
expr_stmt|;
block|}
comment|/**    * If a partition result was set manually, return it. Otherwise, we call    * the Java partitioner.    * @param key the key to partition    * @param value the value to partition    * @param numPartitions the number of reduces    */
DECL|method|getPartition (K key, V value, int numPartitions)
specifier|public
name|int
name|getPartition
parameter_list|(
name|K
name|key
parameter_list|,
name|V
name|value
parameter_list|,
name|int
name|numPartitions
parameter_list|)
block|{
name|Integer
name|result
init|=
name|CACHE
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|result
operator|==
literal|null
condition|)
block|{
return|return
name|part
operator|.
name|getPartition
argument_list|(
name|key
argument_list|,
name|value
argument_list|,
name|numPartitions
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|result
return|;
block|}
block|}
block|}
end_class

end_unit

