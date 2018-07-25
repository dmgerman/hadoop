begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.utils
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|utils
package|;
end_package

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
comment|/**  * Iterator for MetaDataStore DB.  * @param<T>  */
end_comment

begin_interface
DECL|interface|MetaStoreIterator
interface|interface
name|MetaStoreIterator
parameter_list|<
name|T
parameter_list|>
extends|extends
name|Iterator
argument_list|<
name|T
argument_list|>
block|{
comment|/**    * seek to first entry.    */
DECL|method|seekToFirst ()
name|void
name|seekToFirst
parameter_list|()
function_decl|;
comment|/**    * seek to last entry.    */
DECL|method|seekToLast ()
name|void
name|seekToLast
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

