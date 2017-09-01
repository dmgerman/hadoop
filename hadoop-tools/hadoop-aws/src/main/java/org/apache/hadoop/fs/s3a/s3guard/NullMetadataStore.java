begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a.s3guard
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|s3a
operator|.
name|s3guard
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
name|conf
operator|.
name|Configuration
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
name|fs
operator|.
name|FileSystem
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
name|fs
operator|.
name|Path
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
name|Collection
import|;
end_import

begin_comment
comment|/**  * A no-op implementation of MetadataStore.  Clients that use this  * implementation should behave the same as they would without any  * MetadataStore.  */
end_comment

begin_class
DECL|class|NullMetadataStore
specifier|public
class|class
name|NullMetadataStore
implements|implements
name|MetadataStore
block|{
annotation|@
name|Override
DECL|method|initialize (FileSystem fs)
specifier|public
name|void
name|initialize
parameter_list|(
name|FileSystem
name|fs
parameter_list|)
throws|throws
name|IOException
block|{   }
annotation|@
name|Override
DECL|method|initialize (Configuration conf)
specifier|public
name|void
name|initialize
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{   }
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{   }
annotation|@
name|Override
DECL|method|delete (Path path)
specifier|public
name|void
name|delete
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{   }
annotation|@
name|Override
DECL|method|forgetMetadata (Path path)
specifier|public
name|void
name|forgetMetadata
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{   }
annotation|@
name|Override
DECL|method|deleteSubtree (Path path)
specifier|public
name|void
name|deleteSubtree
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{   }
annotation|@
name|Override
DECL|method|get (Path path)
specifier|public
name|PathMetadata
name|get
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|get (Path path, boolean wantEmptyDirectoryFlag)
specifier|public
name|PathMetadata
name|get
parameter_list|(
name|Path
name|path
parameter_list|,
name|boolean
name|wantEmptyDirectoryFlag
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|listChildren (Path path)
specifier|public
name|DirListingMetadata
name|listChildren
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|move (Collection<Path> pathsToDelete, Collection<PathMetadata> pathsToCreate)
specifier|public
name|void
name|move
parameter_list|(
name|Collection
argument_list|<
name|Path
argument_list|>
name|pathsToDelete
parameter_list|,
name|Collection
argument_list|<
name|PathMetadata
argument_list|>
name|pathsToCreate
parameter_list|)
throws|throws
name|IOException
block|{   }
annotation|@
name|Override
DECL|method|put (PathMetadata meta)
specifier|public
name|void
name|put
parameter_list|(
name|PathMetadata
name|meta
parameter_list|)
throws|throws
name|IOException
block|{   }
annotation|@
name|Override
DECL|method|put (Collection<PathMetadata> meta)
specifier|public
name|void
name|put
parameter_list|(
name|Collection
argument_list|<
name|PathMetadata
argument_list|>
name|meta
parameter_list|)
throws|throws
name|IOException
block|{   }
annotation|@
name|Override
DECL|method|put (DirListingMetadata meta)
specifier|public
name|void
name|put
parameter_list|(
name|DirListingMetadata
name|meta
parameter_list|)
throws|throws
name|IOException
block|{   }
annotation|@
name|Override
DECL|method|destroy ()
specifier|public
name|void
name|destroy
parameter_list|()
throws|throws
name|IOException
block|{   }
annotation|@
name|Override
DECL|method|prune (long modTime)
specifier|public
name|void
name|prune
parameter_list|(
name|long
name|modTime
parameter_list|)
block|{   }
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"NullMetadataStore"
return|;
block|}
block|}
end_class

end_unit

