begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.cosn
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|cosn
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
comment|/**  *<p>  * Holds basic metadata for a file stored in a {@link NativeFileSystemStore}.  *</p>  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|FileMetadata
class|class
name|FileMetadata
block|{
DECL|field|key
specifier|private
specifier|final
name|String
name|key
decl_stmt|;
DECL|field|length
specifier|private
specifier|final
name|long
name|length
decl_stmt|;
DECL|field|lastModified
specifier|private
specifier|final
name|long
name|lastModified
decl_stmt|;
DECL|field|isFile
specifier|private
specifier|final
name|boolean
name|isFile
decl_stmt|;
DECL|method|FileMetadata (String key, long length, long lastModified)
name|FileMetadata
parameter_list|(
name|String
name|key
parameter_list|,
name|long
name|length
parameter_list|,
name|long
name|lastModified
parameter_list|)
block|{
name|this
argument_list|(
name|key
argument_list|,
name|length
argument_list|,
name|lastModified
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|FileMetadata (String key, long length, long lastModified, boolean isFile)
name|FileMetadata
parameter_list|(
name|String
name|key
parameter_list|,
name|long
name|length
parameter_list|,
name|long
name|lastModified
parameter_list|,
name|boolean
name|isFile
parameter_list|)
block|{
name|this
operator|.
name|key
operator|=
name|key
expr_stmt|;
name|this
operator|.
name|length
operator|=
name|length
expr_stmt|;
name|this
operator|.
name|lastModified
operator|=
name|lastModified
expr_stmt|;
name|this
operator|.
name|isFile
operator|=
name|isFile
expr_stmt|;
block|}
DECL|method|getKey ()
specifier|public
name|String
name|getKey
parameter_list|()
block|{
return|return
name|key
return|;
block|}
DECL|method|getLength ()
specifier|public
name|long
name|getLength
parameter_list|()
block|{
return|return
name|length
return|;
block|}
DECL|method|getLastModified ()
specifier|public
name|long
name|getLastModified
parameter_list|()
block|{
return|return
name|lastModified
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
literal|"FileMetadata["
operator|+
name|key
operator|+
literal|", "
operator|+
name|length
operator|+
literal|", "
operator|+
name|lastModified
operator|+
literal|", "
operator|+
literal|"file?"
operator|+
name|isFile
operator|+
literal|"]"
return|;
block|}
DECL|method|isFile ()
specifier|public
name|boolean
name|isFile
parameter_list|()
block|{
return|return
name|isFile
return|;
block|}
block|}
end_class

end_unit

