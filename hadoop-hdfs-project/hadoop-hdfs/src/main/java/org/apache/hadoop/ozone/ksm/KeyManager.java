begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.ksm
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|ksm
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
name|ksm
operator|.
name|helpers
operator|.
name|KsmKeyArgs
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
name|ksm
operator|.
name|helpers
operator|.
name|KsmKeyInfo
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
comment|/**  * Handles key level commands.  */
end_comment

begin_interface
DECL|interface|KeyManager
specifier|public
interface|interface
name|KeyManager
block|{
comment|/**    * Given the args of a key to put, return a pipeline for the key. Writes    * the key to pipeline mapping to meta data.    *    * Note that this call only allocate a block for key, and adds the    * corresponding entry to metadata. The block will be returned to client side    * handler DistributedStorageHandler. Which will make another call to    * datanode to create container (if needed) and writes the key.    *    * In case that the container creation or key write failed on    * DistributedStorageHandler, this key's metadata will still stay in KSM.    *    * @param args the args of the key provided by client.    * @return a KsmKeyInfo instance client uses to talk to container.    * @throws Exception    */
DECL|method|allocateKey (KsmKeyArgs args)
name|KsmKeyInfo
name|allocateKey
parameter_list|(
name|KsmKeyArgs
name|args
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

