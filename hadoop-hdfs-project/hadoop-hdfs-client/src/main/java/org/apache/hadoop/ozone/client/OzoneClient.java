begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.client
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|client
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
name|ozone
operator|.
name|client
operator|.
name|protocol
operator|.
name|ClientProtocol
import|;
end_import

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
comment|/**  * OzoneClient connects to Ozone Cluster and  * perform basic operations.  */
end_comment

begin_class
DECL|class|OzoneClient
specifier|public
class|class
name|OzoneClient
implements|implements
name|Closeable
block|{
comment|/*    * OzoneClient connects to Ozone Cluster and    * perform basic operations.    *    * +-------------+     +---+   +-------------------------------------+    * | OzoneClient | --> | C |   | Object Store                        |    * |_____________|     | l |   |  +-------------------------------+  |    *                     | i |   |  | Volume(s)                     |  |    *                     | e |   |  |   +------------------------+  |  |    *                     | n |   |  |   | Bucket(s)              |  |  |    *                     | t |   |  |   |   +------------------+ |  |  |    *                     |   |   |  |   |   | Key -> Value (s) | |  |  |    *                     | P |-->|  |   |   |                  | |  |  |    *                     | r |   |  |   |   |__________________| |  |  |    *                     | o |   |  |   |                        |  |  |    *                     | t |   |  |   |________________________|  |  |    *                     | o |   |  |                               |  |    *                     | c |   |  |_______________________________|  |    *                     | o |   |                                     |    *                     | l |   |_____________________________________|    *                     |___|    * Example:    * ObjectStore store = client.getObjectStore();    * store.createVolume(âvolume oneâ, VolumeArgs);    * volume.setQuota(â10 GBâ);    * OzoneVolume volume = store.getVolume(âvolume oneâ);    * volume.createBucket(âbucket oneâ, BucketArgs);    * bucket.setVersioning(true);    * OzoneOutputStream os = bucket.createKey(âkey oneâ, 1024);    * os.write(byte[]);    * os.close();    * OzoneInputStream is = bucket.readKey(âkey oneâ);    * is.read();    * is.close();    * bucket.deleteKey(âkey oneâ);    * volume.deleteBucket(âbucket oneâ);    * store.deleteVolume(âvolume oneâ);    * client.close();    */
DECL|field|proxy
specifier|private
specifier|final
name|ClientProtocol
name|proxy
decl_stmt|;
DECL|field|objectStore
specifier|private
specifier|final
name|ObjectStore
name|objectStore
decl_stmt|;
comment|/**    * Creates a new OzoneClient object, generally constructed    * using {@link OzoneClientFactory}.    * @param proxy    */
DECL|method|OzoneClient (ClientProtocol proxy)
specifier|public
name|OzoneClient
parameter_list|(
name|ClientProtocol
name|proxy
parameter_list|)
block|{
name|this
operator|.
name|proxy
operator|=
name|proxy
expr_stmt|;
name|this
operator|.
name|objectStore
operator|=
operator|new
name|ObjectStore
argument_list|(
name|this
operator|.
name|proxy
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns the object store associated with the Ozone Cluster.    * @return ObjectStore    */
DECL|method|getObjectStore ()
specifier|public
name|ObjectStore
name|getObjectStore
parameter_list|()
block|{
return|return
name|objectStore
return|;
block|}
comment|/**    * Closes the client and all the underlying resources.    * @throws IOException    */
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|proxy
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

