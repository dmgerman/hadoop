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
name|commons
operator|.
name|lang
operator|.
name|builder
operator|.
name|EqualsBuilder
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
name|lang
operator|.
name|builder
operator|.
name|HashCodeBuilder
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
DECL|class|PathBasedCacheDirective
specifier|public
class|class
name|PathBasedCacheDirective
block|{
comment|/**    * A builder for creating new PathBasedCacheDirective instances.    */
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
block|{
DECL|field|id
specifier|private
name|Long
name|id
decl_stmt|;
DECL|field|path
specifier|private
name|Path
name|path
decl_stmt|;
DECL|field|replication
specifier|private
name|Short
name|replication
decl_stmt|;
DECL|field|pool
specifier|private
name|String
name|pool
decl_stmt|;
comment|/**      * Builds a new PathBasedCacheDirective populated with the set properties.      *       * @return New PathBasedCacheDirective.      */
DECL|method|build ()
specifier|public
name|PathBasedCacheDirective
name|build
parameter_list|()
block|{
return|return
operator|new
name|PathBasedCacheDirective
argument_list|(
name|id
argument_list|,
name|path
argument_list|,
name|replication
argument_list|,
name|pool
argument_list|)
return|;
block|}
comment|/**      * Creates an empty builder.      */
DECL|method|Builder ()
specifier|public
name|Builder
parameter_list|()
block|{     }
comment|/**      * Creates a builder with all elements set to the same values as the      * given PathBasedCacheDirective.      */
DECL|method|Builder (PathBasedCacheDirective directive)
specifier|public
name|Builder
parameter_list|(
name|PathBasedCacheDirective
name|directive
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|directive
operator|.
name|getId
argument_list|()
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|directive
operator|.
name|getPath
argument_list|()
expr_stmt|;
name|this
operator|.
name|replication
operator|=
name|directive
operator|.
name|getReplication
argument_list|()
expr_stmt|;
name|this
operator|.
name|pool
operator|=
name|directive
operator|.
name|getPool
argument_list|()
expr_stmt|;
block|}
comment|/**      * Sets the id used in this request.      *       * @param id The id used in this request.      * @return This builder, for call chaining.      */
DECL|method|setId (Long id)
specifier|public
name|Builder
name|setId
parameter_list|(
name|Long
name|id
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the path used in this request.      *       * @param path The path used in this request.      * @return This builder, for call chaining.      */
DECL|method|setPath (Path path)
specifier|public
name|Builder
name|setPath
parameter_list|(
name|Path
name|path
parameter_list|)
block|{
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the replication used in this request.      *       * @param replication The replication used in this request.      * @return This builder, for call chaining.      */
DECL|method|setReplication (Short replication)
specifier|public
name|Builder
name|setReplication
parameter_list|(
name|Short
name|replication
parameter_list|)
block|{
name|this
operator|.
name|replication
operator|=
name|replication
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the pool used in this request.      *       * @param pool The pool used in this request.      * @return This builder, for call chaining.      */
DECL|method|setPool (String pool)
specifier|public
name|Builder
name|setPool
parameter_list|(
name|String
name|pool
parameter_list|)
block|{
name|this
operator|.
name|pool
operator|=
name|pool
expr_stmt|;
return|return
name|this
return|;
block|}
block|}
DECL|field|id
specifier|private
specifier|final
name|Long
name|id
decl_stmt|;
DECL|field|path
specifier|private
specifier|final
name|Path
name|path
decl_stmt|;
DECL|field|replication
specifier|private
specifier|final
name|Short
name|replication
decl_stmt|;
DECL|field|pool
specifier|private
specifier|final
name|String
name|pool
decl_stmt|;
DECL|method|PathBasedCacheDirective (Long id, Path path, Short replication, String pool)
name|PathBasedCacheDirective
parameter_list|(
name|Long
name|id
parameter_list|,
name|Path
name|path
parameter_list|,
name|Short
name|replication
parameter_list|,
name|String
name|pool
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|this
operator|.
name|replication
operator|=
name|replication
expr_stmt|;
name|this
operator|.
name|pool
operator|=
name|pool
expr_stmt|;
block|}
comment|/**    * @return The ID of this directive.    */
DECL|method|getId ()
specifier|public
name|Long
name|getId
parameter_list|()
block|{
return|return
name|id
return|;
block|}
comment|/**    * @return The path used in this request.    */
DECL|method|getPath ()
specifier|public
name|Path
name|getPath
parameter_list|()
block|{
return|return
name|path
return|;
block|}
comment|/**    * @return The number of times the block should be cached.    */
DECL|method|getReplication ()
specifier|public
name|Short
name|getReplication
parameter_list|()
block|{
return|return
name|replication
return|;
block|}
comment|/**    * @return The pool used in this request.    */
DECL|method|getPool ()
specifier|public
name|String
name|getPool
parameter_list|()
block|{
return|return
name|pool
return|;
block|}
annotation|@
name|Override
DECL|method|equals (Object o)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|PathBasedCacheDirective
name|other
init|=
operator|(
name|PathBasedCacheDirective
operator|)
name|o
decl_stmt|;
return|return
operator|new
name|EqualsBuilder
argument_list|()
operator|.
name|append
argument_list|(
name|getId
argument_list|()
argument_list|,
name|other
operator|.
name|getId
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
name|getPath
argument_list|()
argument_list|,
name|other
operator|.
name|getPath
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
name|getReplication
argument_list|()
argument_list|,
name|other
operator|.
name|getReplication
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
name|getPool
argument_list|()
argument_list|,
name|other
operator|.
name|getPool
argument_list|()
argument_list|)
operator|.
name|isEquals
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
operator|new
name|HashCodeBuilder
argument_list|()
operator|.
name|append
argument_list|(
name|id
argument_list|)
operator|.
name|append
argument_list|(
name|path
argument_list|)
operator|.
name|append
argument_list|(
name|replication
argument_list|)
operator|.
name|append
argument_list|(
name|pool
argument_list|)
operator|.
name|hashCode
argument_list|()
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
name|String
name|prefix
init|=
literal|""
decl_stmt|;
if|if
condition|(
name|id
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
name|prefix
argument_list|)
operator|.
name|append
argument_list|(
literal|"id: "
argument_list|)
operator|.
name|append
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|prefix
operator|=
literal|","
expr_stmt|;
block|}
if|if
condition|(
name|path
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
name|prefix
argument_list|)
operator|.
name|append
argument_list|(
literal|"path: "
argument_list|)
operator|.
name|append
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|prefix
operator|=
literal|","
expr_stmt|;
block|}
if|if
condition|(
name|replication
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
name|prefix
argument_list|)
operator|.
name|append
argument_list|(
literal|"replication: "
argument_list|)
operator|.
name|append
argument_list|(
name|replication
argument_list|)
expr_stmt|;
name|prefix
operator|=
literal|","
expr_stmt|;
block|}
if|if
condition|(
name|pool
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
name|prefix
argument_list|)
operator|.
name|append
argument_list|(
literal|"pool: "
argument_list|)
operator|.
name|append
argument_list|(
name|pool
argument_list|)
expr_stmt|;
name|prefix
operator|=
literal|","
expr_stmt|;
block|}
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

