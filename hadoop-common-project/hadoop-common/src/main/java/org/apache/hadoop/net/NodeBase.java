begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.net
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|net
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
comment|/** A base class that implements interface Node  *   */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|LimitedPrivate
argument_list|(
block|{
literal|"HDFS"
block|,
literal|"MapReduce"
block|}
argument_list|)
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|NodeBase
specifier|public
class|class
name|NodeBase
implements|implements
name|Node
block|{
comment|/** Path separator {@value} */
DECL|field|PATH_SEPARATOR
specifier|public
specifier|final
specifier|static
name|char
name|PATH_SEPARATOR
init|=
literal|'/'
decl_stmt|;
comment|/** Path separator as a string {@value} */
DECL|field|PATH_SEPARATOR_STR
specifier|public
specifier|final
specifier|static
name|String
name|PATH_SEPARATOR_STR
init|=
literal|"/"
decl_stmt|;
comment|/** string representation of root {@value} */
DECL|field|ROOT
specifier|public
specifier|final
specifier|static
name|String
name|ROOT
init|=
literal|""
decl_stmt|;
DECL|field|name
specifier|protected
name|String
name|name
decl_stmt|;
comment|//host:port#
DECL|field|location
specifier|protected
name|String
name|location
decl_stmt|;
comment|//string representation of this node's location
DECL|field|level
specifier|protected
name|int
name|level
decl_stmt|;
comment|//which level of the tree the node resides
DECL|field|parent
specifier|protected
name|Node
name|parent
decl_stmt|;
comment|//its parent
comment|/** Default constructor */
DECL|method|NodeBase ()
specifier|public
name|NodeBase
parameter_list|()
block|{   }
comment|/** Construct a node from its path    * @param path     *   a concatenation of this node's location, the path seperator, and its name     */
DECL|method|NodeBase (String path)
specifier|public
name|NodeBase
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|path
operator|=
name|normalize
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|int
name|index
init|=
name|path
operator|.
name|lastIndexOf
argument_list|(
name|PATH_SEPARATOR
argument_list|)
decl_stmt|;
if|if
condition|(
name|index
operator|==
operator|-
literal|1
condition|)
block|{
name|set
argument_list|(
name|ROOT
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|set
argument_list|(
name|path
operator|.
name|substring
argument_list|(
name|index
operator|+
literal|1
argument_list|)
argument_list|,
name|path
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|index
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Construct a node from its name and its location    * @param name this node's name (can be null, must not contain {@link #PATH_SEPARATOR})    * @param location this node's location     */
DECL|method|NodeBase (String name, String location)
specifier|public
name|NodeBase
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|location
parameter_list|)
block|{
name|set
argument_list|(
name|name
argument_list|,
name|normalize
argument_list|(
name|location
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Construct a node from its name and its location    * @param name this node's name (can be null, must not contain {@link #PATH_SEPARATOR})    * @param location this node's location     * @param parent this node's parent node    * @param level this node's level in the tree    */
DECL|method|NodeBase (String name, String location, Node parent, int level)
specifier|public
name|NodeBase
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|location
parameter_list|,
name|Node
name|parent
parameter_list|,
name|int
name|level
parameter_list|)
block|{
name|set
argument_list|(
name|name
argument_list|,
name|normalize
argument_list|(
name|location
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
name|this
operator|.
name|level
operator|=
name|level
expr_stmt|;
block|}
comment|/**    * set this node's name and location    * @param name the (nullable) name -which cannot contain the {@link #PATH_SEPARATOR}    * @param location the location    */
DECL|method|set (String name, String location)
specifier|private
name|void
name|set
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|location
parameter_list|)
block|{
if|if
condition|(
name|name
operator|!=
literal|null
operator|&&
name|name
operator|.
name|contains
argument_list|(
name|PATH_SEPARATOR_STR
argument_list|)
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Network location name contains /: "
operator|+
name|name
argument_list|)
throw|;
name|this
operator|.
name|name
operator|=
operator|(
name|name
operator|==
literal|null
operator|)
condition|?
literal|""
else|:
name|name
expr_stmt|;
name|this
operator|.
name|location
operator|=
name|location
expr_stmt|;
block|}
comment|/** @return this node's name */
annotation|@
name|Override
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
comment|/** @return this node's network location */
annotation|@
name|Override
DECL|method|getNetworkLocation ()
specifier|public
name|String
name|getNetworkLocation
parameter_list|()
block|{
return|return
name|location
return|;
block|}
comment|/** Set this node's network location    * @param location the location    */
annotation|@
name|Override
DECL|method|setNetworkLocation (String location)
specifier|public
name|void
name|setNetworkLocation
parameter_list|(
name|String
name|location
parameter_list|)
block|{
name|this
operator|.
name|location
operator|=
name|location
expr_stmt|;
block|}
comment|/**    * Get the path of a node    * @param node a non-null node    * @return the path of a node    */
DECL|method|getPath (Node node)
specifier|public
specifier|static
name|String
name|getPath
parameter_list|(
name|Node
name|node
parameter_list|)
block|{
return|return
name|node
operator|.
name|getNetworkLocation
argument_list|()
operator|+
name|PATH_SEPARATOR_STR
operator|+
name|node
operator|.
name|getName
argument_list|()
return|;
block|}
comment|/** @return this node's path as its string representation */
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|getPath
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|/** Normalize a path by stripping off any trailing {@link #PATH_SEPARATOR}    * @param path path to normalize.    * @return the normalised path    * If<i>path</i>is null or empty {@link #ROOT} is returned    * @throws IllegalArgumentException if the first character of a non empty path    * is not {@link #PATH_SEPARATOR}    */
DECL|method|normalize (String path)
specifier|public
specifier|static
name|String
name|normalize
parameter_list|(
name|String
name|path
parameter_list|)
block|{
if|if
condition|(
name|path
operator|==
literal|null
operator|||
name|path
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
return|return
name|ROOT
return|;
if|if
condition|(
name|path
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|!=
name|PATH_SEPARATOR
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Network Location path does not start with "
operator|+
name|PATH_SEPARATOR_STR
operator|+
literal|": "
operator|+
name|path
argument_list|)
throw|;
block|}
name|int
name|len
init|=
name|path
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
name|path
operator|.
name|charAt
argument_list|(
name|len
operator|-
literal|1
argument_list|)
operator|==
name|PATH_SEPARATOR
condition|)
block|{
return|return
name|path
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|len
operator|-
literal|1
argument_list|)
return|;
block|}
return|return
name|path
return|;
block|}
comment|/** @return this node's parent */
annotation|@
name|Override
DECL|method|getParent ()
specifier|public
name|Node
name|getParent
parameter_list|()
block|{
return|return
name|parent
return|;
block|}
comment|/** Set this node's parent    * @param parent the parent    */
annotation|@
name|Override
DECL|method|setParent (Node parent)
specifier|public
name|void
name|setParent
parameter_list|(
name|Node
name|parent
parameter_list|)
block|{
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
block|}
comment|/** @return this node's level in the tree.    * E.g. the root of a tree returns 0 and its children return 1    */
annotation|@
name|Override
DECL|method|getLevel ()
specifier|public
name|int
name|getLevel
parameter_list|()
block|{
return|return
name|level
return|;
block|}
comment|/** Set this node's level in the tree    * @param level the level    */
annotation|@
name|Override
DECL|method|setLevel (int level)
specifier|public
name|void
name|setLevel
parameter_list|(
name|int
name|level
parameter_list|)
block|{
name|this
operator|.
name|level
operator|=
name|level
expr_stmt|;
block|}
DECL|method|locationToDepth (String location)
specifier|public
specifier|static
name|int
name|locationToDepth
parameter_list|(
name|String
name|location
parameter_list|)
block|{
name|String
name|normalizedLocation
init|=
name|normalize
argument_list|(
name|location
argument_list|)
decl_stmt|;
name|int
name|length
init|=
name|normalizedLocation
operator|.
name|length
argument_list|()
decl_stmt|;
name|int
name|depth
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|normalizedLocation
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
operator|==
name|PATH_SEPARATOR
condition|)
block|{
name|depth
operator|++
expr_stmt|;
block|}
block|}
return|return
name|depth
return|;
block|}
block|}
end_class

end_unit

