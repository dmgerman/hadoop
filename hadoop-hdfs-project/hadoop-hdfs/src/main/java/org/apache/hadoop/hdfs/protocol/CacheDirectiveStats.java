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
comment|/**  * Describes a path-based cache directive.  */
end_comment

begin_class
annotation|@
name|InterfaceStability
operator|.
name|Evolving
annotation|@
name|InterfaceAudience
operator|.
name|Public
DECL|class|CacheDirectiveStats
specifier|public
class|class
name|CacheDirectiveStats
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
DECL|field|filesAffected
specifier|private
name|long
name|filesAffected
decl_stmt|;
comment|/**      * Builds a new CacheDirectiveStats populated with the set properties.      *       * @return New CacheDirectiveStats.      */
DECL|method|build ()
specifier|public
name|CacheDirectiveStats
name|build
parameter_list|()
block|{
return|return
operator|new
name|CacheDirectiveStats
argument_list|(
name|bytesNeeded
argument_list|,
name|bytesCached
argument_list|,
name|filesAffected
argument_list|)
return|;
block|}
comment|/**      * Creates an empty builder.      */
DECL|method|Builder ()
specifier|public
name|Builder
parameter_list|()
block|{     }
comment|/**      * Sets the bytes needed by this directive.      *       * @param bytesNeeded The bytes needed.      * @return This builder, for call chaining.      */
DECL|method|setBytesNeeded (Long bytesNeeded)
specifier|public
name|Builder
name|setBytesNeeded
parameter_list|(
name|Long
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
comment|/**      * Sets the bytes cached by this directive.      *       * @param bytesCached The bytes cached.      * @return This builder, for call chaining.      */
DECL|method|setBytesCached (Long bytesCached)
specifier|public
name|Builder
name|setBytesCached
parameter_list|(
name|Long
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
comment|/**      * Sets the files affected by this directive.      *       * @param filesAffected The files affected.      * @return This builder, for call chaining.      */
DECL|method|setFilesAffected (Long filesAffected)
specifier|public
name|Builder
name|setFilesAffected
parameter_list|(
name|Long
name|filesAffected
parameter_list|)
block|{
name|this
operator|.
name|filesAffected
operator|=
name|filesAffected
expr_stmt|;
return|return
name|this
return|;
block|}
block|}
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
DECL|field|filesAffected
specifier|private
specifier|final
name|long
name|filesAffected
decl_stmt|;
DECL|method|CacheDirectiveStats (long bytesNeeded, long bytesCached, long filesAffected)
specifier|private
name|CacheDirectiveStats
parameter_list|(
name|long
name|bytesNeeded
parameter_list|,
name|long
name|bytesCached
parameter_list|,
name|long
name|filesAffected
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
name|filesAffected
operator|=
name|filesAffected
expr_stmt|;
block|}
comment|/**    * @return The bytes needed.    */
DECL|method|getBytesNeeded ()
specifier|public
name|Long
name|getBytesNeeded
parameter_list|()
block|{
return|return
name|bytesNeeded
return|;
block|}
comment|/**    * @return The bytes cached.    */
DECL|method|getBytesCached ()
specifier|public
name|Long
name|getBytesCached
parameter_list|()
block|{
return|return
name|bytesCached
return|;
block|}
comment|/**    * @return The files affected.    */
DECL|method|getFilesAffected ()
specifier|public
name|Long
name|getFilesAffected
parameter_list|()
block|{
return|return
name|filesAffected
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"{"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"bytesNeeded: "
argument_list|)
operator|.
name|append
argument_list|(
name|bytesNeeded
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
operator|.
name|append
argument_list|(
literal|"bytesCached: "
argument_list|)
operator|.
name|append
argument_list|(
name|bytesCached
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
operator|.
name|append
argument_list|(
literal|"filesAffected: "
argument_list|)
operator|.
name|append
argument_list|(
name|filesAffected
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"}"
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

begin_empty_stmt
empty_stmt|;
end_empty_stmt

end_unit

