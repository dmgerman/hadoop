begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm.storage
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|scm
operator|.
name|storage
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

begin_comment
comment|/**  * Defines a functional interface having two inputs which throws IOException.  */
end_comment

begin_interface
annotation|@
name|FunctionalInterface
DECL|interface|CheckedBiFunction
specifier|public
interface|interface
name|CheckedBiFunction
parameter_list|<
name|LEFT
parameter_list|,
name|RIGHT
parameter_list|,
name|THROWABLE
extends|extends
name|IOException
parameter_list|>
block|{
DECL|method|apply (LEFT left, RIGHT right)
name|void
name|apply
parameter_list|(
name|LEFT
name|left
parameter_list|,
name|RIGHT
name|right
parameter_list|)
throws|throws
name|THROWABLE
function_decl|;
block|}
end_interface

end_unit

