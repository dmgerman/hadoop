begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode.fsdataset
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|datanode
operator|.
name|fsdataset
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
comment|/**  * This holds volume reference count as AutoClosable resource.  * It increases the reference count by one in the constructor, and decreases  * the reference count by one in {@link #close()}.  *  *<pre>  *  {@code  *    try (FsVolumeReference ref = volume.obtainReference()) {  *      // Do IOs on the volume  *      volume.createRwb(...);  *      ...  *    }  *  }  *</pre>  */
end_comment

begin_interface
DECL|interface|FsVolumeReference
specifier|public
interface|interface
name|FsVolumeReference
extends|extends
name|Closeable
block|{
comment|/**    * Decrease the reference count of the volume.    * @throws IOException it never throws IOException.    */
annotation|@
name|Override
DECL|method|close ()
name|void
name|close
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns the underlying volume object. Return null if the reference was    * released.    */
DECL|method|getVolume ()
name|FsVolumeSpi
name|getVolume
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

