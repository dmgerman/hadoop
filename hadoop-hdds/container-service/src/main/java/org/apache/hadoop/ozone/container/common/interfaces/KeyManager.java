begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.common.interfaces
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|interfaces
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
name|hdds
operator|.
name|scm
operator|.
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|Pipeline
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
name|hdds
operator|.
name|scm
operator|.
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|StorageContainerException
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|KeyData
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * KeyManager deals with Key Operations in the container Level.  */
end_comment

begin_interface
DECL|interface|KeyManager
specifier|public
interface|interface
name|KeyManager
block|{
comment|/**    * Puts or overwrites a key.    *    * @param pipeline - Pipeline.    * @param data     - Key Data.    * @throws IOException    */
DECL|method|putKey (Pipeline pipeline, KeyData data)
name|void
name|putKey
parameter_list|(
name|Pipeline
name|pipeline
parameter_list|,
name|KeyData
name|data
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Gets an existing key.    *    * @param data - Key Data.    * @return Key Data.    * @throws IOException    */
DECL|method|getKey (KeyData data)
name|KeyData
name|getKey
parameter_list|(
name|KeyData
name|data
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Deletes an existing Key.    *    * @param pipeline - Pipeline.    * @param keyName  Key Data.    * @throws StorageContainerException    */
DECL|method|deleteKey (Pipeline pipeline, String keyName)
name|void
name|deleteKey
parameter_list|(
name|Pipeline
name|pipeline
parameter_list|,
name|String
name|keyName
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * List keys in a container.    *    * @param pipeline - pipeline.    * @param prefix   - Prefix in needed.    * @param startKey  - Key to start from, EMPTY_STRING to begin.    * @param count    - Number of keys to return.    * @return List of Keys that match the criteria.    */
DECL|method|listKey (Pipeline pipeline, String prefix, String startKey, int count)
name|List
argument_list|<
name|KeyData
argument_list|>
name|listKey
parameter_list|(
name|Pipeline
name|pipeline
parameter_list|,
name|String
name|prefix
parameter_list|,
name|String
name|startKey
parameter_list|,
name|int
name|count
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Shutdown keyManager.    */
DECL|method|shutdown ()
name|void
name|shutdown
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

