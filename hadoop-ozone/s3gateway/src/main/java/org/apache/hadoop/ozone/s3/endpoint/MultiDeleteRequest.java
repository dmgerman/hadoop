begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.s3.endpoint
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|s3
operator|.
name|endpoint
package|;
end_package

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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * Request for multi object delete request.  */
end_comment

begin_class
annotation|@
name|XmlAccessorType
argument_list|(
name|XmlAccessType
operator|.
name|FIELD
argument_list|)
annotation|@
name|XmlRootElement
argument_list|(
name|name
operator|=
literal|"Delete"
argument_list|,
name|namespace
operator|=
literal|"http://s3.amazonaws"
operator|+
literal|".com/doc/2006-03-01/"
argument_list|)
DECL|class|MultiDeleteRequest
specifier|public
class|class
name|MultiDeleteRequest
block|{
annotation|@
name|XmlElement
argument_list|(
name|name
operator|=
literal|"Quiet"
argument_list|)
DECL|field|quiet
specifier|private
name|boolean
name|quiet
decl_stmt|;
annotation|@
name|XmlElement
argument_list|(
name|name
operator|=
literal|"Object"
argument_list|)
DECL|field|objects
specifier|private
name|List
argument_list|<
name|DeleteObject
argument_list|>
name|objects
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|isQuiet ()
specifier|public
name|boolean
name|isQuiet
parameter_list|()
block|{
return|return
name|quiet
return|;
block|}
DECL|method|setQuiet (boolean quiet)
specifier|public
name|void
name|setQuiet
parameter_list|(
name|boolean
name|quiet
parameter_list|)
block|{
name|this
operator|.
name|quiet
operator|=
name|quiet
expr_stmt|;
block|}
DECL|method|getObjects ()
specifier|public
name|List
argument_list|<
name|DeleteObject
argument_list|>
name|getObjects
parameter_list|()
block|{
return|return
name|objects
return|;
block|}
DECL|method|setObjects ( List<DeleteObject> objects)
specifier|public
name|void
name|setObjects
parameter_list|(
name|List
argument_list|<
name|DeleteObject
argument_list|>
name|objects
parameter_list|)
block|{
name|this
operator|.
name|objects
operator|=
name|objects
expr_stmt|;
block|}
comment|/**    * JAXB entity for child element.    */
annotation|@
name|XmlAccessorType
argument_list|(
name|XmlAccessType
operator|.
name|FIELD
argument_list|)
annotation|@
name|XmlRootElement
argument_list|(
name|name
operator|=
literal|"Object"
argument_list|,
name|namespace
operator|=
literal|"http://s3.amazonaws"
operator|+
literal|".com/doc/2006-03-01/"
argument_list|)
DECL|class|DeleteObject
specifier|public
specifier|static
class|class
name|DeleteObject
block|{
annotation|@
name|XmlElement
argument_list|(
name|name
operator|=
literal|"Key"
argument_list|)
DECL|field|key
specifier|private
name|String
name|key
decl_stmt|;
annotation|@
name|XmlElement
argument_list|(
name|name
operator|=
literal|"VersionId"
argument_list|)
DECL|field|versionId
specifier|private
name|String
name|versionId
decl_stmt|;
DECL|method|DeleteObject ()
specifier|public
name|DeleteObject
parameter_list|()
block|{     }
DECL|method|DeleteObject (String key)
specifier|public
name|DeleteObject
parameter_list|(
name|String
name|key
parameter_list|)
block|{
name|this
operator|.
name|key
operator|=
name|key
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
DECL|method|setKey (String key)
specifier|public
name|void
name|setKey
parameter_list|(
name|String
name|key
parameter_list|)
block|{
name|this
operator|.
name|key
operator|=
name|key
expr_stmt|;
block|}
DECL|method|getVersionId ()
specifier|public
name|String
name|getVersionId
parameter_list|()
block|{
return|return
name|versionId
return|;
block|}
DECL|method|setVersionId (String versionId)
specifier|public
name|void
name|setVersionId
parameter_list|(
name|String
name|versionId
parameter_list|)
block|{
name|this
operator|.
name|versionId
operator|=
name|versionId
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

