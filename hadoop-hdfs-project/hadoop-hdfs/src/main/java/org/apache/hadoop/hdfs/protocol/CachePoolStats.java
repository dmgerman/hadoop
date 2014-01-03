begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.protocol
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|protocol
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|classification
operator|.
name|InterfaceStability
import|;
end_import

begin_comment
comment|/**  * CachePoolStats describes cache pool statistics.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|CachePoolStats
specifier|public
class|class
name|CachePoolStats
block|{
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
block|{
DECL|field|bytesNeeded
specifier|private
name|long
name|bytesNeeded
decl_stmt|;
DECL|field|bytesCached
specifier|private
name|long
name|bytesCached
decl_stmt|;
DECL|field|bytesOverlimit
specifier|private
name|long
name|bytesOverlimit
decl_stmt|;
DECL|field|filesNeeded
specifier|private
name|long
name|filesNeeded
decl_stmt|;
DECL|field|filesCached
specifier|private
name|long
name|filesCached
decl_stmt|;
DECL|method|Builder ()
specifier|public
name|Builder
parameter_list|()
block|{     }
DECL|method|setBytesNeeded (long bytesNeeded)
specifier|public
name|Builder
name|setBytesNeeded
parameter_list|(
name|long
name|bytesNeeded
parameter_list|)
block|{
name|this
operator|.
name|bytesNeeded
operator|=
name|bytesNeeded
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setBytesCached (long bytesCached)
specifier|public
name|Builder
name|setBytesCached
parameter_list|(
name|long
name|bytesCached
parameter_list|)
block|{
name|this
operator|.
name|bytesCached
operator|=
name|bytesCached
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setBytesOverlimit (long bytesOverlimit)
specifier|public
name|Builder
name|setBytesOverlimit
parameter_list|(
name|long
name|bytesOverlimit
parameter_list|)
block|{
name|this
operator|.
name|bytesOverlimit
operator|=
name|bytesOverlimit
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setFilesNeeded (long filesNeeded)
specifier|public
name|Builder
name|setFilesNeeded
parameter_list|(
name|long
name|filesNeeded
parameter_list|)
block|{
name|this
operator|.
name|filesNeeded
operator|=
name|filesNeeded
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setFilesCached (long filesCached)
specifier|public
name|Builder
name|setFilesCached
parameter_list|(
name|long
name|filesCached
parameter_list|)
block|{
name|this
operator|.
name|filesCached
operator|=
name|filesCached
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|build ()
specifier|public
name|CachePoolStats
name|build
parameter_list|()
block|{
return|return
operator|new
name|CachePoolStats
argument_list|(
name|bytesNeeded
argument_list|,
name|bytesCached
argument_list|,
name|bytesOverlimit
argument_list|,
name|filesNeeded
argument_list|,
name|filesCached
argument_list|)
return|;
block|}
block|}
empty_stmt|;
DECL|field|bytesNeeded
specifier|private
specifier|final
name|long
name|bytesNeeded
decl_stmt|;
DECL|field|bytesCached
specifier|private
specifier|final
name|long
name|bytesCached
decl_stmt|;
DECL|field|bytesOverlimit
specifier|private
specifier|final
name|long
name|bytesOverlimit
decl_stmt|;
DECL|field|filesNeeded
specifier|private
specifier|final
name|long
name|filesNeeded
decl_stmt|;
DECL|field|filesCached
specifier|private
specifier|final
name|long
name|filesCached
decl_stmt|;
DECL|method|CachePoolStats (long bytesNeeded, long bytesCached, long bytesOverlimit, long filesNeeded, long filesCached)
specifier|private
name|CachePoolStats
parameter_list|(
name|long
name|bytesNeeded
parameter_list|,
name|long
name|bytesCached
parameter_list|,
name|long
name|bytesOverlimit
parameter_list|,
name|long
name|filesNeeded
parameter_list|,
name|long
name|filesCached
parameter_list|)
block|{
name|this
operator|.
name|bytesNeeded
operator|=
name|bytesNeeded
expr_stmt|;
name|this
operator|.
name|bytesCached
operator|=
name|bytesCached
expr_stmt|;
name|this
operator|.
name|bytesOverlimit
operator|=
name|bytesOverlimit
expr_stmt|;
name|this
operator|.
name|filesNeeded
operator|=
name|filesNeeded
expr_stmt|;
name|this
operator|.
name|filesCached
operator|=
name|filesCached
expr_stmt|;
block|}
DECL|method|getBytesNeeded ()
specifier|public
name|long
name|getBytesNeeded
parameter_list|()
block|{
return|return
name|bytesNeeded
return|;
block|}
DECL|method|getBytesCached ()
specifier|public
name|long
name|getBytesCached
parameter_list|()
block|{
return|return
name|bytesCached
return|;
block|}
DECL|method|getBytesOverlimit ()
specifier|public
name|long
name|getBytesOverlimit
parameter_list|()
block|{
return|return
name|bytesOverlimit
return|;
block|}
DECL|method|getFilesNeeded ()
specifier|public
name|long
name|getFilesNeeded
parameter_list|()
block|{
return|return
name|filesNeeded
return|;
block|}
DECL|method|getFilesCached ()
specifier|public
name|long
name|getFilesCached
parameter_list|()
block|{
return|return
name|filesCached
return|;
block|}
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
operator|new
name|StringBuilder
argument_list|()
operator|.
name|append
argument_list|(
literal|"{"
argument_list|)
operator|.
name|append
argument_list|(
literal|"bytesNeeded:"
argument_list|)
operator|.
name|append
argument_list|(
name|bytesNeeded
argument_list|)
operator|.
name|append
argument_list|(
literal|", bytesCached:"
argument_list|)
operator|.
name|append
argument_list|(
name|bytesCached
argument_list|)
operator|.
name|append
argument_list|(
literal|", bytesOverlimit:"
argument_list|)
operator|.
name|append
argument_list|(
name|bytesOverlimit
argument_list|)
operator|.
name|append
argument_list|(
literal|", filesNeeded:"
argument_list|)
operator|.
name|append
argument_list|(
name|filesNeeded
argument_list|)
operator|.
name|append
argument_list|(
literal|", filesCached:"
argument_list|)
operator|.
name|append
argument_list|(
name|filesCached
argument_list|)
operator|.
name|append
argument_list|(
literal|"}"
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

