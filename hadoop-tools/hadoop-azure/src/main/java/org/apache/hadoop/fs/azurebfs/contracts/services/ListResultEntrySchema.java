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
name|org
operator|.
name|codehaus
operator|.
name|jackson
operator|.
name|annotate
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
comment|/**  * The ListResultEntrySchema model.  */
end_comment

begin_class
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|ListResultEntrySchema
specifier|public
class|class
name|ListResultEntrySchema
block|{
comment|/**    * The name property.    */
annotation|@
name|JsonProperty
argument_list|(
name|value
operator|=
literal|"name"
argument_list|)
DECL|field|name
specifier|private
name|String
name|name
decl_stmt|;
comment|/**    * The isDirectory property.    */
annotation|@
name|JsonProperty
argument_list|(
name|value
operator|=
literal|"isDirectory"
argument_list|)
DECL|field|isDirectory
specifier|private
name|Boolean
name|isDirectory
decl_stmt|;
comment|/**    * The lastModified property.    */
annotation|@
name|JsonProperty
argument_list|(
name|value
operator|=
literal|"lastModified"
argument_list|)
DECL|field|lastModified
specifier|private
name|String
name|lastModified
decl_stmt|;
comment|/**    * The eTag property.    */
annotation|@
name|JsonProperty
argument_list|(
name|value
operator|=
literal|"etag"
argument_list|)
DECL|field|eTag
specifier|private
name|String
name|eTag
decl_stmt|;
comment|/**    * The contentLength property.    */
annotation|@
name|JsonProperty
argument_list|(
name|value
operator|=
literal|"contentLength"
argument_list|)
DECL|field|contentLength
specifier|private
name|Long
name|contentLength
decl_stmt|;
comment|/**    * Get the name value.    *    * @return the name value    */
DECL|method|name ()
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
name|this
operator|.
name|name
return|;
block|}
comment|/**    * Set the name value.    *    * @param name the name value to set    * @return the ListEntrySchema object itself.    */
DECL|method|withName (String name)
specifier|public
name|ListResultEntrySchema
name|withName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * Get the isDirectory value.    *    * @return the isDirectory value    */
DECL|method|isDirectory ()
specifier|public
name|Boolean
name|isDirectory
parameter_list|()
block|{
return|return
name|this
operator|.
name|isDirectory
return|;
block|}
comment|/**    * Set the isDirectory value.    *    * @param isDirectory the isDirectory value to set    * @return the ListEntrySchema object itself.    */
DECL|method|withIsDirectory (final Boolean isDirectory)
specifier|public
name|ListResultEntrySchema
name|withIsDirectory
parameter_list|(
specifier|final
name|Boolean
name|isDirectory
parameter_list|)
block|{
name|this
operator|.
name|isDirectory
operator|=
name|isDirectory
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * Get the lastModified value.    *    * @return the lastModified value    */
DECL|method|lastModified ()
specifier|public
name|String
name|lastModified
parameter_list|()
block|{
return|return
name|this
operator|.
name|lastModified
return|;
block|}
comment|/**    * Set the lastModified value.    *    * @param lastModified the lastModified value to set    * @return the ListEntrySchema object itself.    */
DECL|method|withLastModified (String lastModified)
specifier|public
name|ListResultEntrySchema
name|withLastModified
parameter_list|(
name|String
name|lastModified
parameter_list|)
block|{
name|this
operator|.
name|lastModified
operator|=
name|lastModified
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * Get the etag value.    *    * @return the etag value    */
DECL|method|eTag ()
specifier|public
name|String
name|eTag
parameter_list|()
block|{
return|return
name|this
operator|.
name|eTag
return|;
block|}
comment|/**    * Set the eTag value.    *    * @param eTag the eTag value to set    * @return the ListEntrySchema object itself.    */
DECL|method|withETag (final String eTag)
specifier|public
name|ListResultEntrySchema
name|withETag
parameter_list|(
specifier|final
name|String
name|eTag
parameter_list|)
block|{
name|this
operator|.
name|eTag
operator|=
name|eTag
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * Get the contentLength value.    *    * @return the contentLength value    */
DECL|method|contentLength ()
specifier|public
name|Long
name|contentLength
parameter_list|()
block|{
return|return
name|this
operator|.
name|contentLength
return|;
block|}
comment|/**    * Set the contentLength value.    *    * @param contentLength the contentLength value to set    * @return the ListEntrySchema object itself.    */
DECL|method|withContentLength (final Long contentLength)
specifier|public
name|ListResultEntrySchema
name|withContentLength
parameter_list|(
specifier|final
name|Long
name|contentLength
parameter_list|)
block|{
name|this
operator|.
name|contentLength
operator|=
name|contentLength
expr_stmt|;
return|return
name|this
return|;
block|}
block|}
end_class

end_unit

