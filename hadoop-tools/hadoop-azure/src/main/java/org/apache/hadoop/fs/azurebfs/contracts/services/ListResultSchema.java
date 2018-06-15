begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.azurebfs.contracts.services
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|azurebfs
operator|.
name|contracts
operator|.
name|services
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|annotation
operator|.
name|JsonProperty
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
comment|/**  * The ListResultSchema model.  */
end_comment

begin_class
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|ListResultSchema
specifier|public
class|class
name|ListResultSchema
block|{
comment|/**    * The paths property.    */
annotation|@
name|JsonProperty
argument_list|(
name|value
operator|=
literal|"paths"
argument_list|)
DECL|field|paths
specifier|private
name|List
argument_list|<
name|ListResultEntrySchema
argument_list|>
name|paths
decl_stmt|;
comment|/**    * * Get the paths value.    *    * @return the paths value    */
DECL|method|paths ()
specifier|public
name|List
argument_list|<
name|ListResultEntrySchema
argument_list|>
name|paths
parameter_list|()
block|{
return|return
name|this
operator|.
name|paths
return|;
block|}
comment|/**    * Set the paths value.    *    * @param paths the paths value to set    * @return the ListSchema object itself.    */
DECL|method|withPaths (final List<ListResultEntrySchema> paths)
specifier|public
name|ListResultSchema
name|withPaths
parameter_list|(
specifier|final
name|List
argument_list|<
name|ListResultEntrySchema
argument_list|>
name|paths
parameter_list|)
block|{
name|this
operator|.
name|paths
operator|=
name|paths
expr_stmt|;
return|return
name|this
return|;
block|}
block|}
end_class

end_unit

