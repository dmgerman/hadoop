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
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * A consumer for metadata store key-value entries.  * Used by {@link MetadataStore} class.  */
end_comment

begin_interface
annotation|@
name|FunctionalInterface
DECL|interface|EntryConsumer
specifier|public
interface|interface
name|EntryConsumer
block|{
comment|/**    * Consumes a key and value and produces a boolean result.    * @param key key    * @param value value    * @return a boolean value produced by the consumer    * @throws IOException    */
DECL|method|consume (byte[] key, byte[] value)
name|boolean
name|consume
parameter_list|(
name|byte
index|[]
name|key
parameter_list|,
name|byte
index|[]
name|value
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

