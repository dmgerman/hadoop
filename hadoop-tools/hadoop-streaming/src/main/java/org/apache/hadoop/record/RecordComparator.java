begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.record
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|record
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
comment|/**  * A raw record comparator base class  *   * @deprecated Replaced by<a href="http://hadoop.apache.org/avro/">Avro</a>.  */
end_comment

begin_class
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
DECL|class|RecordComparator
specifier|public
specifier|abstract
class|class
name|RecordComparator
extends|extends
name|WritableComparator
block|{
comment|/**    * Construct a raw {@link Record} comparison implementation. */
DECL|method|RecordComparator (Class<? extends WritableComparable> recordClass)
specifier|protected
name|RecordComparator
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|WritableComparable
argument_list|>
name|recordClass
parameter_list|)
block|{
name|super
argument_list|(
name|recordClass
argument_list|)
expr_stmt|;
block|}
comment|// inheric JavaDoc
annotation|@
name|Override
DECL|method|compare (byte[] b1, int s1, int l1, byte[] b2, int s2, int l2)
specifier|public
specifier|abstract
name|int
name|compare
parameter_list|(
name|byte
index|[]
name|b1
parameter_list|,
name|int
name|s1
parameter_list|,
name|int
name|l1
parameter_list|,
name|byte
index|[]
name|b2
parameter_list|,
name|int
name|s2
parameter_list|,
name|int
name|l2
parameter_list|)
function_decl|;
comment|/**    * Register an optimized comparator for a {@link Record} implementation.    *    * @param c record classs for which a raw comparator is provided    * @param comparator Raw comparator instance for class c     */
DECL|method|define (Class c, RecordComparator comparator)
specifier|public
specifier|static
specifier|synchronized
name|void
name|define
parameter_list|(
name|Class
name|c
parameter_list|,
name|RecordComparator
name|comparator
parameter_list|)
block|{
name|WritableComparator
operator|.
name|define
argument_list|(
name|c
argument_list|,
name|comparator
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

