begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.contrib.utils.join
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|contrib
operator|.
name|utils
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
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_comment
comment|/**  * This defines an iterator interface that will help the reducer class  * re-group its input by source tags. Once the values are re-grouped,  * the reducer will receive the cross product of values from different groups.  */
end_comment

begin_interface
DECL|interface|ResetableIterator
specifier|public
interface|interface
name|ResetableIterator
extends|extends
name|Iterator
block|{
DECL|method|reset ()
specifier|public
name|void
name|reset
parameter_list|()
function_decl|;
DECL|method|add (Object item)
specifier|public
name|void
name|add
parameter_list|(
name|Object
name|item
parameter_list|)
function_decl|;
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

