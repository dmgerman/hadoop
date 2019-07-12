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
name|java
operator|.
name|io
operator|.
name|Serializable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
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

begin_comment
comment|/**  * Comparator of path ordering for sorting collections.  *  * The definition of "topmost" is:  *<ol>  *<li>The depth of a path is the primary comparator.</li>  *<li>Root is topmost, "0"</li>  *<li>If two paths are of equal depth, {@link Path#compareTo(Path)}</li>  *   is used. This delegates to URI compareTo.  *<li>repeated sorts do not change the order</li>  *</ol>  */
end_comment

begin_class
DECL|class|PathOrderComparators
specifier|final
class|class
name|PathOrderComparators
block|{
DECL|method|PathOrderComparators ()
specifier|private
name|PathOrderComparators
parameter_list|()
block|{   }
comment|/**    * The shallowest paths come first.    * This is to be used when adding entries.    */
DECL|field|TOPMOST_PATH_FIRST
specifier|static
specifier|final
name|Comparator
argument_list|<
name|Path
argument_list|>
name|TOPMOST_PATH_FIRST
init|=
operator|new
name|TopmostFirst
argument_list|()
decl_stmt|;
comment|/**    * The leaves come first.    * This is to be used when deleting entries.    */
DECL|field|TOPMOST_PATH_LAST
specifier|static
specifier|final
name|Comparator
argument_list|<
name|Path
argument_list|>
name|TOPMOST_PATH_LAST
init|=
operator|new
name|TopmostLast
argument_list|()
decl_stmt|;
comment|/**    * The shallowest paths come first.    * This is to be used when adding entries.    */
DECL|field|TOPMOST_PM_FIRST
specifier|static
specifier|final
name|Comparator
argument_list|<
name|PathMetadata
argument_list|>
name|TOPMOST_PM_FIRST
init|=
operator|new
name|PathMetadataComparator
argument_list|(
name|TOPMOST_PATH_FIRST
argument_list|)
decl_stmt|;
comment|/**    * The leaves come first.    * This is to be used when deleting entries.    */
DECL|field|TOPMOST_PM_LAST
specifier|static
specifier|final
name|Comparator
argument_list|<
name|PathMetadata
argument_list|>
name|TOPMOST_PM_LAST
init|=
operator|new
name|PathMetadataComparator
argument_list|(
name|TOPMOST_PATH_LAST
argument_list|)
decl_stmt|;
DECL|class|TopmostFirst
specifier|private
specifier|static
class|class
name|TopmostFirst
implements|implements
name|Comparator
argument_list|<
name|Path
argument_list|>
implements|,
name|Serializable
block|{
annotation|@
name|Override
DECL|method|compare (Path pathL, Path pathR)
specifier|public
name|int
name|compare
parameter_list|(
name|Path
name|pathL
parameter_list|,
name|Path
name|pathR
parameter_list|)
block|{
comment|// exit fast on equal values.
if|if
condition|(
name|pathL
operator|.
name|equals
argument_list|(
name|pathR
argument_list|)
condition|)
block|{
return|return
literal|0
return|;
block|}
name|int
name|depthL
init|=
name|pathL
operator|.
name|depth
argument_list|()
decl_stmt|;
name|int
name|depthR
init|=
name|pathR
operator|.
name|depth
argument_list|()
decl_stmt|;
if|if
condition|(
name|depthL
operator|<
name|depthR
condition|)
block|{
comment|// left is higher up than the right.
return|return
operator|-
literal|1
return|;
block|}
if|if
condition|(
name|depthR
operator|<
name|depthL
condition|)
block|{
comment|// right is higher up than the left
return|return
literal|1
return|;
block|}
comment|// and if they are of equal depth, use the "classic" comparator
comment|// of paths.
return|return
name|pathL
operator|.
name|compareTo
argument_list|(
name|pathR
argument_list|)
return|;
block|}
block|}
comment|/**    * Compare the topmost last.    * For some reason the .reverse() option wasn't giving the    * correct outcome.    */
DECL|class|TopmostLast
specifier|private
specifier|static
specifier|final
class|class
name|TopmostLast
extends|extends
name|TopmostFirst
block|{
annotation|@
name|Override
DECL|method|compare (final Path pathL, final Path pathR)
specifier|public
name|int
name|compare
parameter_list|(
specifier|final
name|Path
name|pathL
parameter_list|,
specifier|final
name|Path
name|pathR
parameter_list|)
block|{
name|int
name|compare
init|=
name|super
operator|.
name|compare
argument_list|(
name|pathL
argument_list|,
name|pathR
argument_list|)
decl_stmt|;
if|if
condition|(
name|compare
operator|<
literal|0
condition|)
block|{
return|return
literal|1
return|;
block|}
if|if
condition|(
name|compare
operator|>
literal|0
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
return|return
literal|0
return|;
block|}
block|}
comment|/**    * Compare on path status.    */
DECL|class|PathMetadataComparator
specifier|static
specifier|final
class|class
name|PathMetadataComparator
implements|implements
name|Comparator
argument_list|<
name|PathMetadata
argument_list|>
implements|,
name|Serializable
block|{
DECL|field|inner
specifier|private
specifier|final
name|Comparator
argument_list|<
name|Path
argument_list|>
name|inner
decl_stmt|;
DECL|method|PathMetadataComparator (final Comparator<Path> inner)
name|PathMetadataComparator
parameter_list|(
specifier|final
name|Comparator
argument_list|<
name|Path
argument_list|>
name|inner
parameter_list|)
block|{
name|this
operator|.
name|inner
operator|=
name|inner
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|compare (final PathMetadata o1, final PathMetadata o2)
specifier|public
name|int
name|compare
parameter_list|(
specifier|final
name|PathMetadata
name|o1
parameter_list|,
specifier|final
name|PathMetadata
name|o2
parameter_list|)
block|{
return|return
name|inner
operator|.
name|compare
argument_list|(
name|o1
operator|.
name|getFileStatus
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|,
name|o2
operator|.
name|getFileStatus
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

