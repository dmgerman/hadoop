begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred.gridmix
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|gridmix
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * Interface for producing records as inputs and outputs to tasks.  */
end_comment

begin_class
DECL|class|RecordFactory
specifier|abstract
class|class
name|RecordFactory
implements|implements
name|Closeable
block|{
comment|/**    * Transform the given record or perform some operation.    * @return true if the record should be emitted.    */
DECL|method|next (GridmixKey key, GridmixRecord val)
specifier|public
specifier|abstract
name|boolean
name|next
parameter_list|(
name|GridmixKey
name|key
parameter_list|,
name|GridmixRecord
name|val
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Estimate of exhausted record capacity.    */
DECL|method|getProgress ()
specifier|public
specifier|abstract
name|float
name|getProgress
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

