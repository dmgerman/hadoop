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
DECL|field|hasExpired
specifier|private
name|boolean
name|hasExpired
decl_stmt|;
comment|/**      * Builds a new CacheDirectiveStats populated with the set properties.      *      * @return New CacheDirectiveStats.      */
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
name|filesNeeded
argument_list|,
name|filesCached
argument_list|,
name|hasExpired
argument_list|)
return|;
block|}
comment|/**      * Creates an empty builder.      */
DECL|method|Builder ()
specifier|public
name|Builder
parameter_list|()
block|{     }
comment|/**      * Sets the bytes needed by this directive.      *      * @param bytesNeeded The bytes needed.      * @return This builder, for call chaining.      */
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
comment|/**      * Sets the bytes cached by this directive.      *      * @param bytesCached The bytes cached.      * @return This builder, for call chaining.      */
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
comment|/**      * Sets the files needed by this directive.      * @param filesNeeded The number of files needed      * @return This builder, for call chaining.      */
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
comment|/**      * Sets the files cached by this directive.      *      * @param filesCached The number of files cached.      * @return This builder, for call chaining.      */
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
comment|/**      * Sets whether this directive has expired.      *      * @param hasExpired if this directive has expired      * @return This builder, for call chaining.      */
DECL|method|setHasExpired (boolean hasExpired)
specifier|public
name|Builder
name|setHasExpired
parameter_list|(
name|boolean
name|hasExpired
parameter_list|)
block|{
name|this
operator|.
name|hasExpired
operator|=
name|hasExpired
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
DECL|field|hasExpired
specifier|private
specifier|final
name|boolean
name|hasExpired
decl_stmt|;
DECL|method|CacheDirectiveStats (long bytesNeeded, long bytesCached, long filesNeeded, long filesCached, boolean hasExpired)
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
name|filesNeeded
parameter_list|,
name|long
name|filesCached
parameter_list|,
name|boolean
name|hasExpired
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
name|this
operator|.
name|hasExpired
operator|=
name|hasExpired
expr_stmt|;
block|}
comment|/**    * @return The bytes needed.    */
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
comment|/**    * @return The bytes cached.    */
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
comment|/**    * @return The number of files needed.    */
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
comment|/**    * @return The number of files cached.    */
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
comment|/**    * @return Whether this directive has expired.    */
DECL|method|hasExpired ()
specifier|public
name|boolean
name|hasExpired
parameter_list|()
block|{
return|return
name|hasExpired
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
return|return
literal|"{"
operator|+
literal|"bytesNeeded: "
operator|+
name|bytesNeeded
operator|+
literal|", "
operator|+
literal|"bytesCached: "
operator|+
name|bytesCached
operator|+
literal|", "
operator|+
literal|"filesNeeded: "
operator|+
name|filesNeeded
operator|+
literal|", "
operator|+
literal|"filesCached: "
operator|+
name|filesCached
operator|+
literal|", "
operator|+
literal|"hasExpired: "
operator|+
name|hasExpired
operator|+
literal|"}"
return|;
block|}
block|}
end_class

end_unit

