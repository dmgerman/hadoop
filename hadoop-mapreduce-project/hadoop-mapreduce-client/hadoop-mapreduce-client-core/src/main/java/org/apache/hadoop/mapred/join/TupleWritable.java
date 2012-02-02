begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred.join
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|join
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
name|Writable
import|;
end_import

begin_comment
comment|/**  * Writable type storing multiple {@link org.apache.hadoop.io.Writable}s.  *  * This is *not* a general-purpose tuple type. In almost all cases, users are  * encouraged to implement their own serializable types, which can perform  * better validation and provide more efficient encodings than this class is  * capable. TupleWritable relies on the join framework for type safety and  * assumes its instances will rarely be persisted, assumptions not only  * incompatible with, but contrary to the general case.  *  * @see org.apache.hadoop.io.Writable  */
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
DECL|class|TupleWritable
specifier|public
class|class
name|TupleWritable
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
name|join
operator|.
name|TupleWritable
block|{
comment|/**    * Create an empty tuple with no allocated storage for writables.    */
DECL|method|TupleWritable ()
specifier|public
name|TupleWritable
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
comment|/**    * Initialize tuple with storage; unknown whether any of them contain    *&quot;written&quot; values.    */
DECL|method|TupleWritable (Writable[] vals)
specifier|public
name|TupleWritable
parameter_list|(
name|Writable
index|[]
name|vals
parameter_list|)
block|{
name|super
argument_list|(
name|vals
argument_list|)
expr_stmt|;
block|}
comment|/**    * Record that the tuple contains an element at the position provided.    */
DECL|method|setWritten (int i)
name|void
name|setWritten
parameter_list|(
name|int
name|i
parameter_list|)
block|{
name|written
operator|.
name|set
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
comment|/**    * Record that the tuple does not contain an element at the position    * provided.    */
DECL|method|clearWritten (int i)
name|void
name|clearWritten
parameter_list|(
name|int
name|i
parameter_list|)
block|{
name|written
operator|.
name|clear
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
comment|/**    * Clear any record of which writables have been written to, without    * releasing storage.    */
DECL|method|clearWritten ()
name|void
name|clearWritten
parameter_list|()
block|{
name|written
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

