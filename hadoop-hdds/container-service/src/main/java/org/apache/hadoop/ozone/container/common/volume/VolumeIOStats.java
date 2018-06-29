begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.common.volume
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
name|volume
package|;
end_package

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
name|AtomicLong
import|;
end_import

begin_comment
comment|/**  * This class is used to track Volume IO stats for each HDDS Volume.  */
end_comment

begin_class
DECL|class|VolumeIOStats
specifier|public
class|class
name|VolumeIOStats
block|{
DECL|field|readBytes
specifier|private
specifier|final
name|AtomicLong
name|readBytes
decl_stmt|;
DECL|field|readOpCount
specifier|private
specifier|final
name|AtomicLong
name|readOpCount
decl_stmt|;
DECL|field|writeBytes
specifier|private
specifier|final
name|AtomicLong
name|writeBytes
decl_stmt|;
DECL|field|writeOpCount
specifier|private
specifier|final
name|AtomicLong
name|writeOpCount
decl_stmt|;
DECL|field|readTime
specifier|private
specifier|final
name|AtomicLong
name|readTime
decl_stmt|;
DECL|field|writeTime
specifier|private
specifier|final
name|AtomicLong
name|writeTime
decl_stmt|;
DECL|method|VolumeIOStats ()
specifier|public
name|VolumeIOStats
parameter_list|()
block|{
name|readBytes
operator|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|readOpCount
operator|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|writeBytes
operator|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|writeOpCount
operator|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|readTime
operator|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|writeTime
operator|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
comment|/**    * Increment number of bytes read from the volume.    * @param bytesRead    */
DECL|method|incReadBytes (long bytesRead)
specifier|public
name|void
name|incReadBytes
parameter_list|(
name|long
name|bytesRead
parameter_list|)
block|{
name|readBytes
operator|.
name|addAndGet
argument_list|(
name|bytesRead
argument_list|)
expr_stmt|;
block|}
comment|/**    * Increment the read operations performed on the volume.    */
DECL|method|incReadOpCount ()
specifier|public
name|void
name|incReadOpCount
parameter_list|()
block|{
name|readOpCount
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
comment|/**    * Increment number of bytes written on to the volume.    * @param bytesWritten    */
DECL|method|incWriteBytes (long bytesWritten)
specifier|public
name|void
name|incWriteBytes
parameter_list|(
name|long
name|bytesWritten
parameter_list|)
block|{
name|writeBytes
operator|.
name|addAndGet
argument_list|(
name|bytesWritten
argument_list|)
expr_stmt|;
block|}
comment|/**    * Increment the write operations performed on the volume.    */
DECL|method|incWriteOpCount ()
specifier|public
name|void
name|incWriteOpCount
parameter_list|()
block|{
name|writeOpCount
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
comment|/**    * Increment the time taken by read operation on the volume.    * @param time    */
DECL|method|incReadTime (long time)
specifier|public
name|void
name|incReadTime
parameter_list|(
name|long
name|time
parameter_list|)
block|{
name|readTime
operator|.
name|addAndGet
argument_list|(
name|time
argument_list|)
expr_stmt|;
block|}
comment|/**    * Increment the time taken by write operation on the volume.    * @param time    */
DECL|method|incWriteTime (long time)
specifier|public
name|void
name|incWriteTime
parameter_list|(
name|long
name|time
parameter_list|)
block|{
name|writeTime
operator|.
name|addAndGet
argument_list|(
name|time
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns total number of bytes read from the volume.    * @return long    */
DECL|method|getReadBytes ()
specifier|public
name|long
name|getReadBytes
parameter_list|()
block|{
return|return
name|readBytes
operator|.
name|get
argument_list|()
return|;
block|}
comment|/**    * Returns total number of bytes written to the volume.    * @return long    */
DECL|method|getWriteBytes ()
specifier|public
name|long
name|getWriteBytes
parameter_list|()
block|{
return|return
name|writeBytes
operator|.
name|get
argument_list|()
return|;
block|}
comment|/**    * Returns total number of read operations performed on the volume.    * @return long    */
DECL|method|getReadOpCount ()
specifier|public
name|long
name|getReadOpCount
parameter_list|()
block|{
return|return
name|readOpCount
operator|.
name|get
argument_list|()
return|;
block|}
comment|/**    * Returns total number of write operations performed on the volume.    * @return long    */
DECL|method|getWriteOpCount ()
specifier|public
name|long
name|getWriteOpCount
parameter_list|()
block|{
return|return
name|writeOpCount
operator|.
name|get
argument_list|()
return|;
block|}
comment|/**    * Returns total read operations time on the volume.    * @return long    */
DECL|method|getReadTime ()
specifier|public
name|long
name|getReadTime
parameter_list|()
block|{
return|return
name|readTime
operator|.
name|get
argument_list|()
return|;
block|}
comment|/**    * Returns total write operations time on the volume.    * @return long    */
DECL|method|getWriteTime ()
specifier|public
name|long
name|getWriteTime
parameter_list|()
block|{
return|return
name|writeTime
operator|.
name|get
argument_list|()
return|;
block|}
block|}
end_class

end_unit

