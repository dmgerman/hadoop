begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.api.records.timelineservice
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|timelineservice
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
operator|.
name|Public
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
operator|.
name|Evolving
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlAccessType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlAccessorType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlElement
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlRootElement
import|;
end_import

begin_comment
comment|/**  *<p>  * This class contains the information about a timeline service domain, which is  * used to a user to host a number of timeline entities, isolating them from  * others'. The user can also define the reader and writer users/groups for  * the domain, which is used to control the access to its entities.  *</p>  *<p>  * The reader and writer users/groups pattern that the user can supply is the  * same as what<code>AccessControlList</code> takes.  *</p>  */
end_comment

begin_class
annotation|@
name|XmlRootElement
argument_list|(
name|name
operator|=
literal|"domain"
argument_list|)
annotation|@
name|XmlAccessorType
argument_list|(
name|XmlAccessType
operator|.
name|NONE
argument_list|)
annotation|@
name|Public
annotation|@
name|Evolving
DECL|class|TimelineDomain
specifier|public
class|class
name|TimelineDomain
block|{
DECL|field|id
specifier|private
name|String
name|id
decl_stmt|;
DECL|field|description
specifier|private
name|String
name|description
decl_stmt|;
DECL|field|owner
specifier|private
name|String
name|owner
decl_stmt|;
DECL|field|readers
specifier|private
name|String
name|readers
decl_stmt|;
DECL|field|writers
specifier|private
name|String
name|writers
decl_stmt|;
DECL|field|createdTime
specifier|private
name|Long
name|createdTime
decl_stmt|;
DECL|field|modifiedTime
specifier|private
name|Long
name|modifiedTime
decl_stmt|;
DECL|method|TimelineDomain ()
specifier|public
name|TimelineDomain
parameter_list|()
block|{   }
comment|/**    * Get the domain ID.    * @return the domain ID    */
annotation|@
name|XmlElement
argument_list|(
name|name
operator|=
literal|"id"
argument_list|)
DECL|method|getId ()
specifier|public
name|String
name|getId
parameter_list|()
block|{
return|return
name|id
return|;
block|}
comment|/**    * Set the domain ID.    * @param id the domain ID    */
DECL|method|setId (String id)
specifier|public
name|void
name|setId
parameter_list|(
name|String
name|id
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
block|}
comment|/**    * Get the domain description.    * @return the domain description    */
annotation|@
name|XmlElement
argument_list|(
name|name
operator|=
literal|"description"
argument_list|)
DECL|method|getDescription ()
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
name|description
return|;
block|}
comment|/**    * Set the domain description.    * @param description the domain description    */
DECL|method|setDescription (String description)
specifier|public
name|void
name|setDescription
parameter_list|(
name|String
name|description
parameter_list|)
block|{
name|this
operator|.
name|description
operator|=
name|description
expr_stmt|;
block|}
comment|/**    * Get the domain owner.    * @return the domain owner    */
annotation|@
name|XmlElement
argument_list|(
name|name
operator|=
literal|"owner"
argument_list|)
DECL|method|getOwner ()
specifier|public
name|String
name|getOwner
parameter_list|()
block|{
return|return
name|owner
return|;
block|}
comment|/**    * Set the domain owner. The user doesn't need to set it, which will    * automatically set to the user who puts the domain.    * @param owner the domain owner    */
DECL|method|setOwner (String owner)
specifier|public
name|void
name|setOwner
parameter_list|(
name|String
name|owner
parameter_list|)
block|{
name|this
operator|.
name|owner
operator|=
name|owner
expr_stmt|;
block|}
comment|/**    * Get the reader (and/or reader group) list string.    * @return the reader (and/or reader group) list string    */
annotation|@
name|XmlElement
argument_list|(
name|name
operator|=
literal|"readers"
argument_list|)
DECL|method|getReaders ()
specifier|public
name|String
name|getReaders
parameter_list|()
block|{
return|return
name|readers
return|;
block|}
comment|/**    * Set the reader (and/or reader group) list string.    * @param readers the reader (and/or reader group) list string    */
DECL|method|setReaders (String readers)
specifier|public
name|void
name|setReaders
parameter_list|(
name|String
name|readers
parameter_list|)
block|{
name|this
operator|.
name|readers
operator|=
name|readers
expr_stmt|;
block|}
comment|/**    * Get the writer (and/or writer group) list string.    * @return the writer (and/or writer group) list string    */
annotation|@
name|XmlElement
argument_list|(
name|name
operator|=
literal|"writers"
argument_list|)
DECL|method|getWriters ()
specifier|public
name|String
name|getWriters
parameter_list|()
block|{
return|return
name|writers
return|;
block|}
comment|/**    * Set the writer (and/or writer group) list string.    * @param writers the writer (and/or writer group) list string    */
DECL|method|setWriters (String writers)
specifier|public
name|void
name|setWriters
parameter_list|(
name|String
name|writers
parameter_list|)
block|{
name|this
operator|.
name|writers
operator|=
name|writers
expr_stmt|;
block|}
comment|/**    * Get the created time of the domain.    * @return the created time of the domain    */
annotation|@
name|XmlElement
argument_list|(
name|name
operator|=
literal|"createdtime"
argument_list|)
DECL|method|getCreatedTime ()
specifier|public
name|Long
name|getCreatedTime
parameter_list|()
block|{
return|return
name|createdTime
return|;
block|}
comment|/**    * Set the created time of the domain.    * @param createdTime the created time of the domain    */
DECL|method|setCreatedTime (Long createdTime)
specifier|public
name|void
name|setCreatedTime
parameter_list|(
name|Long
name|createdTime
parameter_list|)
block|{
name|this
operator|.
name|createdTime
operator|=
name|createdTime
expr_stmt|;
block|}
comment|/**    * Get the modified time of the domain.    * @return the modified time of the domain    */
annotation|@
name|XmlElement
argument_list|(
name|name
operator|=
literal|"modifiedtime"
argument_list|)
DECL|method|getModifiedTime ()
specifier|public
name|Long
name|getModifiedTime
parameter_list|()
block|{
return|return
name|modifiedTime
return|;
block|}
comment|/**    * Set the modified time of the domain.    * @param modifiedTime the modified time of the domain    */
DECL|method|setModifiedTime (Long modifiedTime)
specifier|public
name|void
name|setModifiedTime
parameter_list|(
name|Long
name|modifiedTime
parameter_list|)
block|{
name|this
operator|.
name|modifiedTime
operator|=
name|modifiedTime
expr_stmt|;
block|}
block|}
end_class

end_unit

