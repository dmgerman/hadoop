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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|Lock
import|;
end_import

begin_comment
comment|/**  * KSM metadata manager interface.  */
end_comment

begin_interface
DECL|interface|MetadataManager
specifier|public
interface|interface
name|MetadataManager
block|{
comment|/**    * Start metadata manager.    */
DECL|method|start ()
name|void
name|start
parameter_list|()
function_decl|;
comment|/**    * Stop metadata manager.    */
DECL|method|stop ()
name|void
name|stop
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns the read lock used on Metadata DB.    * @return readLock    */
DECL|method|readLock ()
name|Lock
name|readLock
parameter_list|()
function_decl|;
comment|/**    * Returns the write lock used on Metadata DB.    * @return writeLock    */
DECL|method|writeLock ()
name|Lock
name|writeLock
parameter_list|()
function_decl|;
comment|/**    * Returns the value associated with this key.    * @param key - key    * @return value    */
DECL|method|get (byte[] key)
name|byte
index|[]
name|get
parameter_list|(
name|byte
index|[]
name|key
parameter_list|)
function_decl|;
comment|/**    * Puts a Key into Metadata DB.    * @param key   - key    * @param value - value    */
DECL|method|put (byte[] key, byte[] value)
name|void
name|put
parameter_list|(
name|byte
index|[]
name|key
parameter_list|,
name|byte
index|[]
name|value
parameter_list|)
function_decl|;
comment|/**    * Performs batch Put and Delete to Metadata DB.    * Can be used to do multiple puts and deletes atomically.    * @param putList - list of Key/Value to put into DB    * @param delList - list of Key to delete from DB    */
DECL|method|batchPutDelete (List<Map.Entry<byte[], byte[]>> putList, List<byte[]> delList)
name|void
name|batchPutDelete
parameter_list|(
name|List
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|byte
index|[]
argument_list|,
name|byte
index|[]
argument_list|>
argument_list|>
name|putList
parameter_list|,
name|List
argument_list|<
name|byte
index|[]
argument_list|>
name|delList
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Performs a batch Put to Metadata DB.    * Can be used to do multiple puts atomically.    * @param putList - list of Key/Value to put into DB    */
DECL|method|batchPut (List<Map.Entry<byte[], byte[]>> putList)
name|void
name|batchPut
parameter_list|(
name|List
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|byte
index|[]
argument_list|,
name|byte
index|[]
argument_list|>
argument_list|>
name|putList
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Given a volume return the corresponding DB key.    * @param volume - Volume name    */
DECL|method|getVolumeKey (String volume)
name|byte
index|[]
name|getVolumeKey
parameter_list|(
name|String
name|volume
parameter_list|)
function_decl|;
comment|/**    * Given a user return the corresponding DB key.    * @param user - User name    */
DECL|method|getUserKey (String user)
name|byte
index|[]
name|getUserKey
parameter_list|(
name|String
name|user
parameter_list|)
function_decl|;
comment|/**    * Given a volume and bucket, return the corresponding DB key.    * @param volume - User name    * @param bucket - Bucket name    */
DECL|method|getBucketKey (String volume, String bucket)
name|byte
index|[]
name|getBucketKey
parameter_list|(
name|String
name|volume
parameter_list|,
name|String
name|bucket
parameter_list|)
function_decl|;
comment|/**    * Given a volume, bucket and a key, return the corresponding DB key.    * @param volume - volume name    * @param bucket - bucket name    * @param key - key name    * @return bytes of DB key.    */
DECL|method|getDBKeyForKey (String volume, String bucket, String key)
name|byte
index|[]
name|getDBKeyForKey
parameter_list|(
name|String
name|volume
parameter_list|,
name|String
name|bucket
parameter_list|,
name|String
name|key
parameter_list|)
function_decl|;
comment|/**    * Deletes the key from DB.    *    * @param key - key name    */
DECL|method|deleteKey (byte[] key)
name|void
name|deleteKey
parameter_list|(
name|byte
index|[]
name|key
parameter_list|)
function_decl|;
comment|/**    * Given a volume, check if it is empty, i.e there are no buckets inside it.    * @param volume - Volume name    */
DECL|method|isVolumeEmpty (String volume)
name|boolean
name|isVolumeEmpty
parameter_list|(
name|String
name|volume
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

