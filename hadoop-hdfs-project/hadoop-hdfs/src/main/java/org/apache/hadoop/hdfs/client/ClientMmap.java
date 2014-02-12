begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.client
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
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
name|classification
operator|.
name|InterfaceAudience
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|MappedByteBuffer
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
name|atomic
operator|.
name|AtomicInteger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
import|;
end_import

begin_comment
comment|/**  * A memory-mapped region used by an HDFS client.  *   * This class includes a reference count and some other information used by  * ClientMmapManager to track and cache mmaps.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|ClientMmap
specifier|public
class|class
name|ClientMmap
block|{
DECL|field|LOG
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|ClientMmap
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * A reference to the block replica which this mmap relates to.    */
DECL|field|replica
specifier|private
specifier|final
name|ShortCircuitReplica
name|replica
decl_stmt|;
comment|/**    * The java ByteBuffer object.    */
DECL|field|map
specifier|private
specifier|final
name|MappedByteBuffer
name|map
decl_stmt|;
comment|/**    * Reference count of this ClientMmap object.    */
DECL|field|refCount
specifier|private
specifier|final
name|AtomicInteger
name|refCount
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|1
argument_list|)
decl_stmt|;
DECL|method|ClientMmap (ShortCircuitReplica replica, MappedByteBuffer map)
name|ClientMmap
parameter_list|(
name|ShortCircuitReplica
name|replica
parameter_list|,
name|MappedByteBuffer
name|map
parameter_list|)
block|{
name|this
operator|.
name|replica
operator|=
name|replica
expr_stmt|;
name|this
operator|.
name|map
operator|=
name|map
expr_stmt|;
block|}
comment|/**    * Increment the reference count.    *    * @return   The new reference count.    */
DECL|method|ref ()
name|void
name|ref
parameter_list|()
block|{
name|refCount
operator|.
name|addAndGet
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/**    * Decrement the reference count.    *    * The parent replica gets unreferenced each time the reference count     * of this object goes to 0.    */
DECL|method|unref ()
specifier|public
name|void
name|unref
parameter_list|()
block|{
name|refCount
operator|.
name|addAndGet
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
name|replica
operator|.
name|unref
argument_list|()
expr_stmt|;
block|}
DECL|method|getMappedByteBuffer ()
specifier|public
name|MappedByteBuffer
name|getMappedByteBuffer
parameter_list|()
block|{
return|return
name|map
return|;
block|}
block|}
end_class

end_unit

